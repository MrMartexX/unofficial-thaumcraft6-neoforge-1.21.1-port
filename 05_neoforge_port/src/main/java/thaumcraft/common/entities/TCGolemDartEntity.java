package thaumcraft.common.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * TC6 EntityGolemDart: golem ranged combat dart projectile.
 */
public class TCGolemDartEntity extends ThrowableProjectile {
    public static final float LEGACY_DAMAGE = 2.0F;

    public TCGolemDartEntity(EntityType<? extends TCGolemDartEntity> type, Level level) {
        super(type, level);
    }

    public TCGolemDartEntity(EntityType<? extends TCGolemDartEntity> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide && result.getEntity() != null) {
            result.getEntity().hurt(damageSources().thrown(this, getOwner()), LEGACY_DAMAGE);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            discard();
        }
    }
}
