package thaumcraft.common.items.tools;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.TCFollowingItemEntity;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;

@EventBusSubscriber(modid = Thaumcraft.MODID)
public final class TCToolEvents {
    private static final Map<UUID, Direction> LAST_FACE_CLICKED = new ConcurrentHashMap<>();
    private static final ThreadLocal<Boolean> HARVESTING = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> IN_ARCING = ThreadLocal.withInitial(() -> false);

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

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        Entity breaker = event.getBreaker();
        ItemStack tool = event.getTool();
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        if (level.isClientSide()) {
            return;
        }

        // 1. Rare Earth drop from ores when not using silk touch
        if (breaker instanceof Player player && !tool.isEmpty()) {
            boolean hasSilk = false;
            try {
                var enchRegistry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
                Holder<Enchantment> silkHolder = enchRegistry.getHolderOrThrow(Enchantments.SILK_TOUCH);
                hasSilk = tool.getEnchantmentLevel(silkHolder) > 0;
            } catch (Exception ignored) {
            }

            if (!hasSilk && isOreBlock(state)) {
                if (level.random.nextFloat() < 0.02F) {
                    event.getDrops().add(new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                            new ItemStack(TCItems.RARE_EARTH.get())));
                }
            }
        }

        // 2. REFINING: Chance to create native ore clusters
        int refining = TCEnumInfusionEnchantment.getEnchantmentLevel(tool, TCEnumInfusionEnchantment.REFINING);
        if (refining > 0) {
            List<ItemEntity> bonusDrops = new ArrayList<>();
            float chance = (1 + refining) * 0.125F;
            for (ItemEntity drop : event.getDrops()) {
                ItemStack dropStack = drop.getItem();
                ItemStack special = TCSpecialMiningResult.findSpecialMiningResult(dropStack);
                if (!special.isEmpty() && level.random.nextFloat() < chance) {
                    bonusDrops.add(new ItemEntity(level, drop.getX(), drop.getY(), drop.getZ(), special.copy()));
                }
            }
            event.getDrops().addAll(bonusDrops);
        }

        // 3. COLLECTOR: Convert drops to following items targeting player
        int collector = TCEnumInfusionEnchantment.getEnchantmentLevel(tool, TCEnumInfusionEnchantment.COLLECTOR);
        if (collector > 0 && breaker instanceof Player player && !player.isShiftKeyDown()) {
            List<ItemEntity> newDrops = new ArrayList<>();
            for (ItemEntity drop : event.getDrops()) {
                if (!(drop instanceof TCFollowingItemEntity)) {
                    TCFollowingItemEntity following = new TCFollowingItemEntity(
                            level, drop.getX(), drop.getY(), drop.getZ(), drop.getItem(), player, 10);
                    following.setDeltaMovement(drop.getDeltaMovement());
                    newDrops.add(following);
                } else {
                    newDrops.add(drop);
                }
            }
            event.getDrops().clear();
            event.getDrops().addAll(newDrops);
        }

        // 4. LAMPLIGHT: Place glimmer if light is low
        int lamplight = TCEnumInfusionEnchantment.getEnchantmentLevel(tool, TCEnumInfusionEnchantment.LAMPLIGHT);
        if (lamplight > 0 && level.getMaxLocalRawBrightness(pos) < 10) {
            if (level.getBlockState(pos).isAir() || level.getBlockState(pos).canBeReplaced()) {
                level.setBlock(pos, TCBlocks.EFFECT_GLIMMER.get().defaultBlockState(), 3);
            }
        }
    }

    private static boolean isOreBlock(BlockState state) {
        return state.is(Tags.Blocks.ORES)
                || state.is(BlockTags.COAL_ORES)
                || state.is(BlockTags.IRON_ORES)
                || state.is(BlockTags.COPPER_ORES)
                || state.is(BlockTags.GOLD_ORES)
                || state.is(BlockTags.REDSTONE_ORES)
                || state.is(BlockTags.LAPIS_ORES)
                || state.is(BlockTags.DIAMOND_ORES)
                || state.is(BlockTags.EMERALD_ORES);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (isHarvesting()) {
            return;
        }
        Player player = event.getPlayer();
        if (player == null || player.isShiftKeyDown()) {
            return;
        }
        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty()) {
            return;
        }
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        // 1. BURROWING: Break furthest connected block in tree/vein
        int burrowing = TCEnumInfusionEnchantment.getEnchantmentLevel(tool, TCEnumInfusionEnchantment.BURROWING);
        if (burrowing > 0 && !(tool.getItem() instanceof ItemElementalAxe)) {
            if (state.is(BlockTags.LOGS) || isOreBlock(state)) {
                breakFurthestConnectedBlock(player, level, pos, state, tool);
            }
        }

        // 2. DESTRUCTIVE: 3x3 excavation for regular tools with the enchantment
        int destructive = TCEnumInfusionEnchantment.getEnchantmentLevel(tool, TCEnumInfusionEnchantment.DESTRUCTIVE);
        if (destructive > 0 && !(tool.getItem() instanceof ItemElementalShovel) && !(tool.getItem() instanceof ItemPrimalCrusher)) {
            mine3x3Area(player, level, pos, tool);
        }
    }

    private static void breakFurthestConnectedBlock(Player player, Level level, BlockPos origin, BlockState targetState, ItemStack tool) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(origin);
        visited.add(origin);

        BlockPos furthest = null;
        double maxDist = 0.0D;

        while (!queue.isEmpty() && visited.size() < 64) {
            BlockPos current = queue.poll();
            for (BlockPos neighbor : BlockPos.betweenClosed(current.offset(-1, -1, -1), current.offset(1, 1, 1))) {
                BlockPos immutable = neighbor.immutable();
                if (!visited.contains(immutable)) {
                    visited.add(immutable);
                    BlockState s = level.getBlockState(immutable);
                    if (s.is(targetState.getBlock())) {
                        double d = origin.distSqr(immutable);
                        if (d > maxDist) {
                            maxDist = d;
                            furthest = immutable;
                        }
                        queue.add(immutable);
                    }
                }
            }
        }

        if (furthest != null && !furthest.equals(origin)) {
            destroyNeighborBlock(player, level, furthest, tool);
        }
    }

    private static void mine3x3Area(Player player, Level level, BlockPos pos, ItemStack tool) {
        Direction face = getMiningFace(player, pos);
        for (int aa = -1; aa <= 1; aa++) {
            for (int bb = -1; bb <= 1; bb++) {
                if (aa == 0 && bb == 0) continue;
                int xx = 0;
                int yy = 0;
                int zz = 0;
                if (face.getAxis() == Direction.Axis.Y) {
                    xx = aa;
                    zz = bb;
                } else if (face.getAxis() == Direction.Axis.Z) {
                    xx = aa;
                    yy = bb;
                } else {
                    zz = aa;
                    yy = bb;
                }
                BlockPos target = pos.offset(xx, yy, zz);
                BlockState s = level.getBlockState(target);
                if (s.getDestroySpeed(level, target) >= 0.0F && tool.isCorrectToolForDrops(s)) {
                    destroyNeighborBlock(player, level, target, tool);
                    if (tool.isEmpty()) {
                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        if (IN_ARCING.get()) {
            return;
        }
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            int arcing = TCEnumInfusionEnchantment.getEnchantmentLevel(weapon, TCEnumInfusionEnchantment.ARCING);
            if (arcing > 0) {
                LivingEntity victim = event.getEntity();
                Level level = victim.level();
                AABB searchBox = victim.getBoundingBox().inflate(4.0D);
                List<LivingEntity> nearby = level.getEntitiesOfClass(
                        LivingEntity.class,
                        searchBox,
                        e -> e != attacker && e != victim && !e.isAlliedTo(attacker) && e.isAlive()
                );
                int chained = 0;
                float damage = Math.max(1.0F, event.getNewDamage() * 0.5F);
                try {
                    IN_ARCING.set(true);
                    for (LivingEntity target : nearby) {
                        if (chained >= arcing) {
                            break;
                        }
                        target.hurt(attacker.damageSources().mobAttack(attacker), damage);
                        Vec3 push = target.position().subtract(victim.position()).normalize().scale(0.4D);
                        target.setDeltaMovement(target.getDeltaMovement().add(push.x, 0.2D, push.z));
                        chained++;
                    }
                } finally {
                    IN_ARCING.set(false);
                }
                if (chained > 0) {
                    level.playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                            SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.3F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            int essence = TCEnumInfusionEnchantment.getEnchantmentLevel(weapon, TCEnumInfusionEnchantment.ESSENCE);
            if (essence > 0 && player.getRandom().nextFloat() < (0.25F * essence)) {
                var primals = Aspect.getPrimalAspects();
                Aspect randomPrimal = primals.get(player.getRandom().nextInt(primals.size()));
                ItemStack crystal = TCItems.getCrystalItem(randomPrimal);
                if (!crystal.isEmpty()) {
                    LivingEntity victim = event.getEntity();
                    event.getDrops().add(new ItemEntity(
                            victim.level(),
                            victim.getX(),
                            victim.getY(),
                            victim.getZ(),
                            crystal
                    ));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        int sounding = TCEnumInfusionEnchantment.getEnchantmentLevel(stack, TCEnumInfusionEnchantment.SOUNDING);
        if (sounding > 0 && event.getEntity().isShiftKeyDown()) {
            Level level = event.getLevel();
            BlockPos pos = event.getPos();
            Player player = event.getEntity();

            int depth = 16 * sounding;
            int minY = Math.max(level.getMinBuildHeight(), pos.getY() - depth);
            BlockPos foundPos = null;
            BlockState foundState = null;

            for (int y = pos.getY() - 1; y >= minY; y--) {
                BlockPos probe = new BlockPos(pos.getX(), y, pos.getZ());
                BlockState s = level.getBlockState(probe);
                if (isOreBlock(s)) {
                    foundPos = probe;
                    foundState = s;
                    break;
                }
            }

            if (foundPos != null) {
                int dist = pos.getY() - foundPos.getY();
                player.displayClientMessage(
                        Component.translatable("tc.sounding.found", foundState.getBlock().getName(), dist),
                        true
                );
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 1.0F, 1.5F);
            } else {
                player.displayClientMessage(
                        Component.translatable("tc.sounding.none"),
                        true
                );
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.PLAYERS, 0.8F, 0.5F);
            }
        }
    }
}
