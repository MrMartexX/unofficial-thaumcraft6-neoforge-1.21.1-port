package thaumcraft.common.entities;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import thaumcraft.common.lib.fx.TCFXDispatcher;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;

/** TC6 EntityCausalityCollapser: invisible rift-closing explosive projectile. */
public class TCCausalityCollapserEntity extends ThrowableItemProjectile {
    public static final int LEGACY_MAX_STACK_SIZE = 16;
    public static final float LEGACY_PROJECTILE_VELOCITY = 0.8F;
    public static final float LEGACY_THROW_INACCURACY = 2.0F;
    public static final float LEGACY_THROW_X_ROT_OFFSET = -5.0F;
    public static final float LEGACY_THROW_SOUND_VOLUME = 0.3F;
    public static final float LEGACY_THROW_SOUND_PITCH_NUMERATOR = 0.4F;
    public static final float LEGACY_THROW_SOUND_RANDOM_MULTIPLIER = 0.4F;
    public static final float LEGACY_THROW_SOUND_BASE = 0.8F;
    public static final float LEGACY_EXPLOSION_STRENGTH = 2.0F;
    public static final boolean LEGACY_EXPLOSION_CAUSES_FIRE = true;
    public static final double LEGACY_RIFT_COLLAPSE_RANGE = 3.0D;
    public static final int LEGACY_TRAIL_SAMPLES = 3;
    public static final float LEGACY_ALUMENTUM_TRAIL_ALPHA = 0.5F;
    public static final float LEGACY_ALUMENTUM_TRAIL_SCALE = 4.0F;
    public static final int LEGACY_GENERIC_TRAIL_START = 448;
    public static final int LEGACY_GENERIC_TRAIL_FRAMES = 8;
    public static final int LEGACY_GENERIC_TRAIL_AGE = 8;
    public static final float LEGACY_GENERIC_TRAIL_ALPHA = 0.7F;
    public static final float LEGACY_GENERIC_TRAIL_SCALE = 0.3F;

    public TCCausalityCollapserEntity(EntityType<? extends TCCausalityCollapserEntity> type, Level level) {
        super(type, level);
    }

    public TCCausalityCollapserEntity(Level level, LivingEntity shooter) {
        super(TCEntityTypes.CAUSALITY_COLLAPSER.get(), shooter, level);
    }

    public TCCausalityCollapserEntity(Level level, double x, double y, double z) {
        super(TCEntityTypes.CAUSALITY_COLLAPSER.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return TCItems.CAUSALITY_COLLAPSER.get();
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, LEGACY_PROJECTILE_VELOCITY, inaccuracy);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            spawnLegacyTrail();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            explodeLikeLegacy();
            collapseNearbyRifts();
            discard();
        }
    }

    public void explodeLikeLegacy() {
        level().explode(
                this,
                getX(),
                getY(),
                getZ(),
                LEGACY_EXPLOSION_STRENGTH,
                LEGACY_EXPLOSION_CAUSES_FIRE,
                legacyExplosionInteractionForValidation()
        );
    }

    public int collapseNearbyRiftsForValidation() {
        return collapseNearbyRifts();
    }

    public static Level.ExplosionInteraction legacyExplosionInteractionForValidation() {
        return Level.ExplosionInteraction.TNT;
    }

    private int collapseNearbyRifts() {
        AABB legacyRange = new AABB(getX(), getY(), getZ(), getX(), getY(), getZ())
                .inflate(LEGACY_RIFT_COLLAPSE_RANGE);
        List<TCFluxRiftEntity> rifts = level().getEntitiesOfClass(TCFluxRiftEntity.class, legacyRange);
        for (TCFluxRiftEntity rift : rifts) {
            rift.setCollapse(true);
        }
        return rifts.size();
    }

    private void spawnLegacyTrail() {
        for (int i = 0; i < LEGACY_TRAIL_SAMPLES; i++) {
            double coeff = i / (double) LEGACY_TRAIL_SAMPLES;
            double x = xOld + (getX() - xOld) * coeff;
            double y = yOld + (getY() - yOld) * coeff + getBbHeight() / 2.0F;
            double z = zOld + (getZ() - zOld) * coeff;
            TCFXDispatcher.drawAlumentum(
                    level(),
                    x,
                    y,
                    z,
                    0.0125D * (random.nextFloat() - 0.5F),
                    0.0125D * (random.nextFloat() - 0.5F),
                    0.0125D * (random.nextFloat() - 0.5F),
                    0.8F + random.nextFloat() * 0.2F,
                    0.3F + random.nextFloat() * 0.1F,
                    random.nextFloat() * 0.1F,
                    LEGACY_ALUMENTUM_TRAIL_ALPHA,
                    LEGACY_ALUMENTUM_TRAIL_SCALE
            );
            level().addParticle(
                    ParticleTypes.FLAME,
                    getX() + level().random.nextGaussian() * 0.2D,
                    getY() + level().random.nextGaussian() * 0.2D,
                    getZ() + level().random.nextGaussian() * 0.2D,
                    0.0D,
                    0.0D,
                    0.0D
            );
            TCFXDispatcher.drawAlumentumGenericTrail(
                    level(),
                    getX() + level().random.nextGaussian() * 0.2D,
                    getY() + level().random.nextGaussian() * 0.2D,
                    getZ() + level().random.nextGaussian() * 0.2D
            );
        }
    }
}
