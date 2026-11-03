package thaumcraft.common.golems.seals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;

public class SealHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<String, ISeal> types = new LinkedHashMap<>();
    public static final Map<ResourceKey<Level>, Map<SealPos, SealEntity>> sealEntities = new ConcurrentHashMap<>();
    private static int validityCounter;

    private static Map<SealPos, SealEntity> worldSeals(Level level) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            return TCSealSavedData.get(serverLevel).seals;
        }
        return sealEntities.computeIfAbsent(level.dimension(), key -> new ConcurrentHashMap<>());
    }

    private static void dirty(Level level) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) TCSealSavedData.get(serverLevel).setDirty();
    }

    public static void registerSeal(ISeal seal) {
        if (types.containsKey(seal.getKey())) {
            LOGGER.error("Attempting to register Seal [{}] twice. Ignoring.", seal.getKey());
        } else {
            types.put(seal.getKey(), seal);
        }
    }

    public static synchronized void initSeals() {
        if (!types.isEmpty()) return;
        registerSeal(new SealPickup());
        registerSeal(new SealPickupAdvanced());
        registerSeal(new SealFill());
        registerSeal(new SealFillAdvanced());
        registerSeal(new SealEmpty());
        registerSeal(new SealEmptyAdvanced());
        registerSeal(new SealHarvest());
        registerSeal(new SealGuard());
        registerSeal(new SealGuardAdvanced());
        registerSeal(new SealProvide());
        registerSeal(new SealStock());
        registerSeal(new SealBreaker());
        registerSeal(new SealBreakerAdvanced());
        registerSeal(new SealLumber());
        registerSeal(new SealButcher());
        registerSeal(new SealUse());
    }

    static {
        initSeals();
    }

    public static String[] getRegisteredSeals() {
        return types.keySet().toArray(new String[0]);
    }

    public static ISeal getSeal(String key) {
        return types.get(key);
    }

    public static SealEntity getSealEntity(Level level, SealPos pos) {
        Map<SealPos, SealEntity> map = worldSeals(level);
        return map != null ? map.get(pos) : null;
    }

    public static void applyClientSync(Level level, TCSealSyncPayload payload) {
        if (!level.isClientSide) return;
        Map<SealPos, SealEntity> map = worldSeals(level);
        var tag = payload.data();
        if (payload.operation() == TCSealNetwork.CLEAR_CHUNK) {
            long chunk = tag.getLong("chunk");
            map.keySet().removeIf(pos -> new net.minecraft.world.level.ChunkPos(pos.pos).toLong() == chunk);
            return;
        }
        SealPos pos = new SealPos(BlockPos.of(tag.getLong("pos")), Direction.from3DDataValue(tag.getByte("face")));
        if (payload.operation() == TCSealNetwork.REMOVE) { map.remove(pos); return; }
        if (payload.operation() != TCSealNetwork.UPSERT || getSeal(tag.getString("type")) == null) return;
        SealEntity seal = map.computeIfAbsent(pos, key -> new SealEntity());
        seal.readNBT(tag, level.registryAccess());
    }

    public static List<SealEntity> getSealsInRange(Level level, BlockPos source, int range) {
        List<SealEntity> out = new ArrayList<>();
        Map<SealPos, SealEntity> map = worldSeals(level);
        if (map != null) {
            double rangeSq = (double) range * range;
            for (SealEntity se : map.values()) {
                if (se != null && se.getSeal() != null && se.getSealPos() != null) {
                    if (se.getSealPos().pos.distSqr(source) <= rangeSq) {
                        out.add(se);
                    }
                }
            }
        }
        return out;
    }

    public static boolean addSealEntity(Level level, BlockPos pos, Direction side, ISeal seal, Player player) {
        SealPos sealPos = new SealPos(pos, side);
        Map<SealPos, SealEntity> map = worldSeals(level);
        if (map.containsKey(sealPos)) {
            return false;
        }

        SealEntity sealEntity = new SealEntity(level, sealPos, seal);
        if (player != null) {
            sealEntity.setOwner(player.getStringUUID());
        }
        map.put(sealPos, sealEntity);
        dirty(level);
        sealEntity.syncToClient(level);
        return true;
    }

    public static void removeSealEntity(Level level, SealPos pos, boolean quiet) {
        Map<SealPos, SealEntity> map = worldSeals(level);
        if (map != null) {
            SealEntity seal = map.remove(pos);
            if (seal != null) dirty(level);
            if (seal != null && level instanceof net.minecraft.server.level.ServerLevel server) {
                TCSealNetwork.send(server, seal, TCSealNetwork.REMOVE);
            }
            if (seal != null && seal.getSeal() != null) {
                if (!level.isClientSide) {
                    seal.getSeal().onRemoval(level, pos.pos, pos.face);
                    if (!quiet) {
                        ItemStack drop = ItemSealPlacer.getSealStack(seal.getSeal().getKey());
                        if (drop != null && !drop.isEmpty()) {
                            BlockPos dropPos = pos.pos.relative(pos.face);
                            ItemEntity item = new ItemEntity(level, dropPos.getX() + 0.5D, dropPos.getY() + 0.5D, dropPos.getZ() + 0.5D, drop);
                            level.addFreshEntity(item);
                        }
                    }
                }
            }
        }
    }

    public static void tickSeals(Level level) {
        if (level.isClientSide) return;
        boolean checkSupport = ++validityCounter % 20 == 0;
        Map<SealPos, SealEntity> map = worldSeals(level);
        if (map != null && !map.isEmpty()) {
            for (SealEntity se : map.values()) {
                if (se != null && level.hasChunkAt(se.getSealPos().pos)) {
                    try {
                        if (checkSupport && !se.getSeal().canPlaceAt(level, se.getSealPos().pos, se.getSealPos().face)) {
                            removeSealEntity(level, se.getSealPos(), false);
                        } else se.tickSealEntity(level);
                    } catch (RuntimeException exception) {
                        LOGGER.error("Removing failing seal at {}", se.getSealPos(), exception);
                        removeSealEntity(level, se.getSealPos(), false);
                    }
                }
            }
            if (level.getGameTime() % 20 == 0) dirty(level);
        }
    }

    public static void openSealGui(Player player, ISealEntity seal) {
        if (!(player instanceof net.minecraft.server.level.ServerPlayer server) || !(seal instanceof SealEntity entity)
                || !server.getStringUUID().equals(entity.getOwner()) || entity.getSealPos() == null
                || !server.level().hasChunkAt(entity.getSealPos().pos)
                || getSealEntity(server.level(), entity.getSealPos()) != entity
                || server.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(entity.getSealPos().pos)) > 64) return;
        server.openMenu(new net.minecraft.world.SimpleMenuProvider(
                (id, inventory, p) -> new thaumcraft.common.menu.TCSealMenu(id, inventory, entity),
                net.minecraft.network.chat.Component.translatable("item.thaumcraft.seal")),
                buffer -> buffer.writeNbt(entity.writeNBT()));
    }
}
