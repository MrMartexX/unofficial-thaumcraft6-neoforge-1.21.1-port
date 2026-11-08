package thaumcraft.common.items.tools;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import thaumcraft.Thaumcraft;

@EventBusSubscriber(modid = Thaumcraft.MODID)
public final class TCToolEvents {
    private static final Map<UUID, Direction> LAST_FACE_CLICKED = new ConcurrentHashMap<>();
    private static final ThreadLocal<Boolean> HARVESTING = ThreadLocal.withInitial(() -> false);

    private TCToolEvents() {
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity() != null && event.getFace() != null) {
            LAST_FACE_CLICKED.put(event.getEntity().getUUID(), event.getFace());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_FACE_CLICKED.remove(event.getEntity().getUUID());
    }

    public static void clearAll() {
        LAST_FACE_CLICKED.clear();
    }

    public static boolean isHarvesting() {
        return HARVESTING.get();
    }

    public static Direction getMiningFace(Player player, BlockPos pos) {
        Direction face = LAST_FACE_CLICKED.remove(player.getUUID());
        if (face != null) {
            return face;
        }
        Vec3 look = player.getViewVector(1.0F);
        return Direction.getNearest(look.x, look.y, look.z).getOpposite();
    }

    public static boolean destroyNeighborBlock(LivingEntity entity, Level level, BlockPos pos, ItemStack toolStack) {
        if (toolStack.isEmpty() || HARVESTING.get()) {
            return false;
        }
        if (entity instanceof ServerPlayer serverPlayer) {
            try {
                HARVESTING.set(true);
                return serverPlayer.gameMode.destroyBlock(pos);
            } finally {
                HARVESTING.set(false);
            }
        } else {
            return level.destroyBlock(pos, true, entity);
        }
    }
}
