package thaumcraft.common.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/**
 * TC6 EntityFocusProjectile: caster-discharged focus orb projectile.
 */
public class TCFocusProjectileEntity extends ThrowableProjectile {
    public TCFocusProjectileEntity(EntityType<? extends TCFocusProjectileEntity> type, Level level) {
        super(type, level);
    }

    public TCFocusProjectileEntity(EntityType<? extends TCFocusProjectileEntity> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            discard();
        }
    }
}
