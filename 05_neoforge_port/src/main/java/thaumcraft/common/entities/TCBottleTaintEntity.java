package thaumcraft.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMobEffects;

/** TC6 EntityBottleTaint port: Flux Taint splash plus legacy random Flux Goo placement. */
public class TCBottleTaintEntity extends ThrowableItemProjectile {
    public static final int LEGACY_FLUX_TAINT_DURATION = 100;
    public static final int LEGACY_FLUX_TAINT_AMPLIFIER = 0;
    public static final double LEGACY_EFFECT_RADIUS = 5.0D;
    public static final int LEGACY_GOO_ATTEMPTS = 10;
    public static final float LEGACY_GOO_RANGE = 4.0F;
    public static final byte LEGACY_BREAK_EVENT = 3;

    public TCBottleTaintEntity(EntityType<? extends TCBottleTaintEntity> type, Level level) {
        super(type, level);
    }

    public TCBottleTaintEntity(Level level, LivingEntity shooter) {
        super(TCEntityTypes.BOTTLE_TAINT.get(), shooter, level);
    }

    public TCBottleTaintEntity(Level level, double x, double y, double z) {
        super(TCEntityTypes.BOTTLE_TAINT.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return TCItems.BOTTLE_TAINT.get();
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == LEGACY_BREAK_EVENT) {
            for (int i = 0; i < 24; i++) {
                double motionX = (random.nextDouble() - 0.5D) * 0.18D;
                double motionY = random.nextDouble() * 0.16D;
                double motionZ = (random.nextDouble() - 0.5D) * 0.18D;
                level().addParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(TCItems.BOTTLE_TAINT.get())),
                        getX(),
                        getY(),
                        getZ(),
                        motionX,
                        motionY,
                        motionZ
                );
                level().addParticle(ParticleTypes.MYCELIUM, getX(), getY(), getZ(), motionX, motionY, motionZ);
            }
            return;
        }
        super.handleEntityEvent(id);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide) {
            return;
        }

        applyFluxTaintSplash();
        placeFluxGooLikeLegacy();
        level().broadcastEntityEvent(this, LEGACY_BREAK_EVENT);
        discard();
    }

    private void applyFluxTaintSplash() {
        AABB area = new AABB(getX(), getY(), getZ(), getX(), getY(), getZ()).inflate(LEGACY_EFFECT_RADIUS);
        for (LivingEntity living : level().getEntitiesOfClass(LivingEntity.class, area)) {
            applyFluxTaintTo(living);
        }
    }

    private void placeFluxGooLikeLegacy() {
        for (int i = 0; i < LEGACY_GOO_ATTEMPTS; i++) {
            int offsetX = (int) ((random.nextFloat() - random.nextFloat()) * LEGACY_GOO_RANGE);
            int offsetZ = (int) ((random.nextFloat() - random.nextFloat()) * LEGACY_GOO_RANGE);
            if (!random.nextBoolean()) {
                continue;
            }
            BlockPos target = blockPosition().offset(offsetX, 0, offsetZ);
            tryPlaceFluxGooWithLegacyFallback(level(), target);
        }
    }

    public static boolean applyFluxTaintTo(LivingEntity living) {
        if (!shouldApplyFluxTaint(living)) {
            return false;
        }
        living.addEffect(new MobEffectInstance(
                TCMobEffects.FLUX_TAINT,
                LEGACY_FLUX_TAINT_DURATION,
                LEGACY_FLUX_TAINT_AMPLIFIER,
                false,
                true
        ));
        return true;
    }

    public static boolean shouldApplyFluxTaint(LivingEntity living) {
        return !(living instanceof ITaintedMob) && !living.isInvertedHealAndHarm();
    }

    public static boolean tryPlaceFluxGooWithLegacyFallback(Level level, BlockPos target) {
        return tryPlaceFluxGoo(level, target) || tryPlaceFluxGoo(level, target.below());
    }

    public static boolean tryPlaceFluxGoo(Level level, BlockPos target) {
        if (!hasLegacySolidSupport(level, target)) {
            return false;
        }
        BlockState current = level.getBlockState(target);
        if (!current.canBeReplaced()) {
            return false;
        }
        return level.setBlock(target, TCBlocks.FLUX_GOO.get().defaultBlockState(), Block.UPDATE_ALL);
    }

    public static boolean hasLegacySolidSupport(Level level, BlockPos target) {
        BlockPos supportPos = target.below();
        return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, Direction.UP);
    }
}
