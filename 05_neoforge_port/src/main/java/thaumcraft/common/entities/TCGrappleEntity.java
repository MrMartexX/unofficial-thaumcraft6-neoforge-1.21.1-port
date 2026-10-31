package thaumcraft.common.entities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * TC6 Grappling Tool hook projectile.
 * Flies to the target location, embeds into surface, and pulls the shooting owner
 * towards the impact point while negating fall damage.
 */
public class TCGrappleEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<Boolean> PULLING =
            SynchedEntityData.defineId(TCGrappleEntity.class, EntityDataSerializers.BOOLEAN);

    private boolean boosted = false;

    public TCGrappleEntity(EntityType<? extends TCGrappleEntity> type, Level level) {
        super(type, level);
    }

    public TCGrappleEntity(EntityType<? extends TCGrappleEntity> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PULLING, false);
    }

    public boolean isPulling() {
        return entityData.get(PULLING);
    }

    public void setPulling(boolean pulling) {
        entityData.set(PULLING, pulling);
    }

    @Override
    protected double getDefaultGravity() {
        return isPulling() ? 0.0 : 0.03;
    }

    @Override
    public void tick() {
        super.tick();

        Entity owner = getOwner();

        if (!isPulling()) {
            if (tickCount > 40 || owner == null || !owner.isAlive()) {
                if (!level().isClientSide) {
                    discard();
                }
                return;
            }
        } else {
            // Embedded and pulling owner
            if (owner == null || !owner.isAlive() || owner.isShiftKeyDown()) {
                if (!level().isClientSide) {
                    discard();
                }
                return;
            }

            if (!level().isClientSide && owner instanceof ServerPlayer sp) {
                sp.connection.resetPosition();
            }

            owner.resetFallDistance();
            double dist = owner.distanceTo(this);
            if (dist < 1.5) {
                if (!level().isClientSide) {
                    discard();
                }
                return;
            }

            double mx = getX() - owner.getX();
            double my = getY() - owner.getY();
            double mz = getZ() - owner.getZ();

            double dd = dist < 8.0 ? dist * (8.0 - dist) : dist;
            dd = Math.max(1.0E-9, dd);
            mx /= dd * 5.0;
            my /= dd * 5.0;
            mz /= dd * 5.0;

            Vec3 moveVec = new Vec3(mx, my, mz);
            if (moveVec.length() > 0.25) {
                moveVec = moveVec.normalize().scale(0.25);
                mx = moveVec.x;
                my = moveVec.y;
                mz = moveVec.z;
            }

            double boostY = !boosted ? 0.35 : 0.0;
            owner.setDeltaMovement(owner.getDeltaMovement().add(mx, my + 0.033 + boostY, mz));
            owner.hurtMarked = true;
            boosted = true;

            if (level().isClientSide && level().random.nextFloat() < 0.3F) {
                level().addParticle(ParticleTypes.CRIT, getX(), getY(), getZ(), 0, 0, 0);
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide && !isPulling()) {
            setPulling(true);
            setDeltaMovement(Vec3.ZERO);
            setPos(result.getLocation());
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide && !isPulling()) {
            setPulling(true);
            setDeltaMovement(Vec3.ZERO);
            setPos(result.getLocation());
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide && !isPulling()) {
            setPulling(true);
            setDeltaMovement(Vec3.ZERO);
            setPos(result.getLocation());
        }
    }
}
