package thaumcraft.common.items.casters;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.items.components.TCFocusPackageComponent;

public final class TCFocusCastExecutor {
    private static final String ROOT = "root";
    private static final String TOUCH = "thaumcraft.touch";
    private static final String FIRE = "thaumcraft.fire";

    private TCFocusCastExecutor() {
    }

    public static CastPlan plan(ItemStack focusStack) {
        TCFocusPackageComponent packageData = TCFocusPackageHelper.getPackage(focusStack);
        if (packageData.isEmpty()) {
            return CastPlan.unsupported("missing focus package");
        }
        List<TCFocusPackageHelper.NodeInstance> nodes = TCFocusPackageHelper.decode(packageData.nodes());
        if (nodes.size() != 3) {
            return CastPlan.unsupported("only ROOT->TOUCH->FIRE is executable in this parity slice");
        }
        TCFocusPackageHelper.NodeInstance root = findFirst(nodes, ROOT).orElse(null);
        TCFocusPackageHelper.NodeInstance touch = findFirst(nodes, TOUCH).orElse(null);
        TCFocusPackageHelper.NodeInstance fire = findFirst(nodes, FIRE).orElse(null);
        if (root == null || touch == null || fire == null) {
            return CastPlan.unsupported("supported slice requires ROOT, TOUCH and FIRE");
        }
        if (touch.parent() != root.id() || fire.parent() != touch.id()) {
            return CastPlan.unsupported("supported slice requires legacy ROOT->TOUCH->FIRE parent chain");
        }
        if (!root.children().contains(touch.id()) || !touch.children().contains(fire.id())) {
            return CastPlan.unsupported("supported slice requires legacy child links");
        }
        return new CastPlan(true, "ROOT->TOUCH->FIRE", packageData, touch, fire);
    }

    public static boolean canExecute(ItemStack focusStack) {
        return plan(focusStack).supported();
    }

    public static CastResult cast(ServerPlayer player, InteractionHand hand, ItemStack focusStack) {
        CastPlan plan = plan(focusStack);
        if (!plan.supported()) {
            return CastResult.unsupported(plan.reason());
        }
        playFireCastSound(player);
        FocusTarget target = traceTouchTarget(player, hand);
        if (target == null) {
            return new CastResult(true, false, "no touch target");
        }

        return executeTouchFireAtTarget(player, plan, target, true);
    }

    static CastResult executeTouchFireAtEntity(ServerPlayer player, ItemStack focusStack, Entity entity) {
        CastPlan plan = plan(focusStack);
        if (!plan.supported()) {
            return CastResult.unsupported(plan.reason());
        }
        return executeTouchFireAtTarget(
                player,
                plan,
                FocusTarget.entity(entity, entity.position().add(0.0D, entity.getBbHeight() * 0.5D, 0.0D)),
                false
        );
    }

    static CastResult executeTouchFireAtBlock(ServerPlayer player, ItemStack focusStack, BlockPos blockPos, Direction side) {
        CastPlan plan = plan(focusStack);
        if (!plan.supported()) {
            return CastResult.unsupported(plan.reason());
        }
        Vec3 hit = Vec3.atCenterOf(blockPos.relative(side));
        return executeTouchFireAtTarget(player, plan, FocusTarget.block(hit, blockPos, side), false);
    }

    private static CastResult executeTouchFireAtTarget(ServerPlayer player, CastPlan plan, FocusTarget target, boolean sendParticles) {
        Map<String, Integer> fireSettings = plan.effect().settings();
        int power = clamped(fireSettings, "power", 1, 1, 5);
        int duration = clamped(fireSettings, "duration", 0, 0, 5);
        float finalPower = 1.0F;

        if (sendParticles) {
            player.serverLevel().sendParticles(
                    ParticleTypes.FLAME,
                    target.hit().x,
                    target.hit().y,
                    target.hit().z,
                    8,
                    0.08D,
                    0.08D,
                    0.08D,
                    0.01D
            );
        }
        boolean applied = executeFire(player, target, power, duration, finalPower);
        String targetNotes = target.entity() == null
                ? "block=" + target.blockPos() + ", side=" + target.side()
                : "entity=" + BuiltInRegistries.ENTITY_TYPE.getKey(target.entity().getType());
        return new CastResult(true, applied, targetNotes + ", " + (applied ? "fire effect applied" : "fire effect had no valid mutation"));
    }

    public static Vec3 legacySourceVector(ServerPlayer player, InteractionHand hand) {
        boolean mainHand = hand == InteractionHand.MAIN_HAND;
        double yaw = (player.getYRot() - 0.5F) / 180.0F * Math.PI;
        double x = -Math.cos(yaw) * 0.2D * (mainHand ? 1.0D : -1.0D);
        double z = -Math.sin(yaw) * 0.3D * (mainHand ? 1.0D : -1.0D);
        return new Vec3(player.getX(), player.getY(), player.getZ())
                .add(x, player.getEyeHeight() - 0.4D, z)
                .add(player.getLookAngle());
    }

    private static FocusTarget traceTouchTarget(ServerPlayer player, InteractionHand hand) {
        ServerLevel level = player.serverLevel();
        Vec3 source = legacySourceVector(player, hand);
        Vec3 direction = player.getLookAngle().normalize();
        double range = Math.max(4.5D, player.blockInteractionRange());
        Vec3 end = source.add(direction.scale(range));

        EntityHit entityHit = traceEntity(level, player, source, end, range).orElse(null);
        if (entityHit != null) {
            return FocusTarget.entity(entityHit.entity(), entityHit.hit());
        }

        BlockHitResult blockHit = level.clip(new ClipContext(
                source,
                end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            return FocusTarget.block(blockHit.getLocation(), blockHit.getBlockPos(), blockHit.getDirection());
        }
        return null;
    }

    private static Optional<EntityHit> traceEntity(ServerLevel level, ServerPlayer player, Vec3 source, Vec3 end, double range) {
        AABB search = new AABB(source, end).inflate(1.0D);
        double maxDistance = range * range;
        EntityHit best = null;
        for (Entity entity : level.getEntities(player, search, entity -> !entity.isSpectator() && entity.isPickable())) {
            AABB box = entity.getBoundingBox().inflate(Math.max(0.25D, entity.getPickRadius() + 0.25D));
            Optional<Vec3> hit = box.clip(source, end);
            if (box.contains(source)) {
                hit = Optional.of(source);
            }
            if (hit.isEmpty()) {
                continue;
            }
            double distance = source.distanceToSqr(hit.get());
            if (distance <= maxDistance && (best == null || distance < best.distanceSqr())) {
                best = new EntityHit(entity, hit.get(), distance);
            }
        }
        return Optional.ofNullable(best);
    }

    private static boolean executeFire(ServerPlayer caster, FocusTarget target, int power, int duration, float finalPower) {
        if (target.entity() != null) {
            Entity entity = target.entity();
            if (entity.fireImmune()) {
                return false;
            }
            float damage = (3.0F + power) * finalPower;
            boolean hurt = entity.hurt(caster.damageSources().source(DamageTypes.FIREBALL, caster, caster), damage);
            int fireSeconds = Math.round((1.0F + duration * duration) * finalPower);
            if (fireSeconds > 0) {
                entity.igniteForSeconds(fireSeconds);
            }
            return hurt || fireSeconds > 0;
        }
        if (target.blockPos() == null || target.side() == null || duration <= 0) {
            return false;
        }
        ServerLevel level = caster.serverLevel();
        BlockPos firePos = target.blockPos().relative(target.side());
        if (!level.isEmptyBlock(firePos) || level.random.nextFloat() >= finalPower) {
            return false;
        }
        level.playSound(null, firePos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 0.8F);
        return level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 11);
    }

    private static void playFireCastSound(ServerPlayer player) {
        player.level().playSound(
                null,
                player.getX(),
                player.getY() + player.getEyeHeight(),
                player.getZ(),
                SoundEvents.FIRECHARGE_USE,
                SoundSource.PLAYERS,
                1.0F,
                1.0F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.05F
        );
    }

    private static Optional<TCFocusPackageHelper.NodeInstance> findFirst(List<TCFocusPackageHelper.NodeInstance> nodes, String key) {
        return nodes.stream()
                .filter(node -> key.equals(TCFocusElements.normalizeKey(node.key())))
                .min(Comparator.comparingInt(TCFocusPackageHelper.NodeInstance::id));
    }

    private static int clamped(Map<String, Integer> settings, String key, int fallback, int min, int max) {
        return Mth.clamp(settings.getOrDefault(key, fallback), min, max);
    }

    public record CastPlan(
            boolean supported,
            String reason,
            TCFocusPackageComponent packageData,
            TCFocusPackageHelper.NodeInstance medium,
            TCFocusPackageHelper.NodeInstance effect
    ) {
        private static CastPlan unsupported(String reason) {
            return new CastPlan(false, reason, TCFocusPackageComponent.EMPTY, null, null);
        }
    }

    public record CastResult(boolean supported, boolean applied, String notes) {
        private static CastResult unsupported(String notes) {
            return new CastResult(false, false, notes);
        }
    }

    private record FocusTarget(Entity entity, Vec3 hit, BlockPos blockPos, Direction side) {
        private static FocusTarget entity(Entity entity, Vec3 hit) {
            return new FocusTarget(entity, hit, null, null);
        }

        private static FocusTarget block(Vec3 hit, BlockPos blockPos, Direction side) {
            return new FocusTarget(null, hit, blockPos, side);
        }
    }

    private record EntityHit(Entity entity, Vec3 hit, double distanceSqr) {
    }
}
