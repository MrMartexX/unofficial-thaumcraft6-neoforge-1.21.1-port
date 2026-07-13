package thaumcraft.common.entities;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCSounds;

/** TC6 EntityGolemOrb projectile: no gravity, target-homing, reflectable magic orb. */
public class TCGolemOrbEntity extends ThrowableProjectile {
    public static final int LEGACY_RED_LIFETIME_TICKS = 240;
    public static final int LEGACY_BLUE_LIFETIME_TICKS = 160;
    public static final double LEGACY_HOMING_ACCELERATION = 0.2D;
    public static final double LEGACY_MOTION_CLAMP = 0.25D;
    public static final float LEGACY_RED_DAMAGE_MULTIPLIER = 1.0F;
    public static final float LEGACY_BLUE_DAMAGE_MULTIPLIER = 0.6F;
    public static final float LEGACY_SHOOT_VELOCITY = 0.66F;
    public static final float LEGACY_SHOOT_INACCURACY = 3.0F;
    public static final double LEGACY_TARGET_MOTION_LEAD = 10.0D;

    private static final EntityDataAccessor<Integer> TARGET_ID =
            SynchedEntityData.defineId(TCGolemOrbEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> RED =
            SynchedEntityData.defineId(TCGolemOrbEntity.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    private LivingEntity cachedTarget;

    public TCGolemOrbEntity(EntityType<? extends TCGolemOrbEntity> type, Level level) {
        super(type, level);
    }

    public TCGolemOrbEntity(Level level, LivingEntity shooter, LivingEntity target, boolean red) {
        super(TCEntityTypes.GOLEM_ORB.get(), shooter, level);
        setTarget(target);
        setRed(red);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TARGET_ID, -1);
        builder.define(RED, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount > legacyLifetime()) {
            discard();
            return;
        }
        LivingEntity target = target();
        if (target != null && target.isAlive()) {
            applyLegacyHoming(target);
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0D;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide && getOwner() instanceof LivingEntity owner) {
            result.getEntity().hurt(
                    damageSources().indirectMagic(this, owner),
                    legacyDamageFromAttack(owner.getAttributeValue(Attributes.ATTACK_DAMAGE), isRed())
            );
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        playSound(TCSounds.SHOCK.get(), 1.0F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (isInvulnerableTo(source)) {
            return false;
        }
        Entity sourceEntity = source.getEntity();
        if (sourceEntity != null) {
            Vec3 look = sourceEntity.getLookAngle().scale(0.9D);
            setDeltaMovement(look);
            hasImpulse = true;
            playSound(TCSounds.ZAP.get(), 1.0F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F);
            return true;
        }
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("target", entityData.get(TARGET_ID));
        tag.putBoolean("red", isRed());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(TARGET_ID, tag.getInt("target"));
        setRed(tag.getBoolean("red"));
        cachedTarget = null;
    }

    public void setTarget(@Nullable LivingEntity target) {
        cachedTarget = target;
        entityData.set(TARGET_ID, target == null ? -1 : target.getId());
    }

    @Nullable
    public LivingEntity target() {
        if (cachedTarget != null && cachedTarget.isAlive()) {
            return cachedTarget;
        }
        int id = entityData.get(TARGET_ID);
        Entity entity = id < 0 ? null : level().getEntity(id);
        cachedTarget = entity instanceof LivingEntity living ? living : null;
        return cachedTarget;
    }

    public int targetId() {
        return entityData.get(TARGET_ID);
    }

    public boolean isRed() {
        return entityData.get(RED);
    }

    public void setRed(boolean red) {
        entityData.set(RED, red);
    }

    public int legacyLifetime() {
        return isRed() ? LEGACY_RED_LIFETIME_TICKS : LEGACY_BLUE_LIFETIME_TICKS;
    }

    public void applyLegacyHoming(LivingEntity target) {
        double distanceSquared = Math.max(distanceToSqr(target), 0.0001D);
        double dx = (target.getX() - getX()) / distanceSquared;
        double dy = (target.getBoundingBox().minY + target.getBbHeight() * 0.6D - getY()) / distanceSquared;
        double dz = (target.getZ() - getZ()) / distanceSquared;
        Vec3 motion = getDeltaMovement().add(
                dx * LEGACY_HOMING_ACCELERATION,
                dy * LEGACY_HOMING_ACCELERATION,
                dz * LEGACY_HOMING_ACCELERATION
        );
        setDeltaMovement(
                Mth.clamp(motion.x, -LEGACY_MOTION_CLAMP, LEGACY_MOTION_CLAMP),
                Mth.clamp(motion.y, -LEGACY_MOTION_CLAMP, LEGACY_MOTION_CLAMP),
                Mth.clamp(motion.z, -LEGACY_MOTION_CLAMP, LEGACY_MOTION_CLAMP)
        );
        hasImpulse = true;
    }

    public static float legacyDamageFromAttack(double attackDamage, boolean red) {
        return (float) attackDamage * (red ? LEGACY_RED_DAMAGE_MULTIPLIER : LEGACY_BLUE_DAMAGE_MULTIPLIER);
    }

    public double gravityForValidation() {
        return getDefaultGravity();
    }
}
