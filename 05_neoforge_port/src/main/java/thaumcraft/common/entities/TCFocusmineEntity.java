package thaumcraft.common.entities;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.items.casters.TCFocusCastExecutor;
import thaumcraft.common.registry.TCEntityTypes;

/**
 * TC6 EntityFocusMine: proximity-triggered arcane focus mine.
 */
public class TCFocusmineEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<Boolean> ARMED =
            SynchedEntityData.defineId(TCFocusmineEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TARGET_FRIEND =
            SynchedEntityData.defineId(TCFocusmineEntity.class, EntityDataSerializers.BOOLEAN);

    private String remainingNodes = "";
    private float power = 1.0F;

    public TCFocusmineEntity(EntityType<? extends TCFocusmineEntity> type, Level level) {
        super(type, level);
    }

    public TCFocusmineEntity(Level level, LivingEntity shooter, Vec3 direction, boolean targetFriend, String remainingNodes, float power) {
        super(TCEntityTypes.FOCUSMINE.get(), shooter, level);
        this.remainingNodes = remainingNodes != null ? remainingNodes : "";
        this.power = power;
        setTargetFriend(targetFriend);

        Vec3 look = direction != null ? direction.normalize() : shooter.getLookAngle();
        setPos(shooter.getX(), shooter.getEyeY() - 0.1D, shooter.getZ());
        shoot(look.x, look.y, look.z, 0.75F, 0.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ARMED, false);
        builder.define(TARGET_FRIEND, false);
    }

    public boolean isArmed() {
        return entityData.get(ARMED);
    }

    public void setArmed(boolean armed) {
        entityData.set(ARMED, armed);
    }

    public boolean isTargetFriend() {
        return entityData.get(TARGET_FRIEND);
    }

    public void setTargetFriend(boolean friend) {
        entityData.set(TARGET_FRIEND, friend);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04D;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            if (isArmed() && level().random.nextFloat() < 0.2F) {
                level().addParticle(
                        ParticleTypes.WITCH,
                        getX(), getY() + 0.1D, getZ(),
                        0.0D, 0.05D, 0.0D
                );
            }
            return;
        }

        if (tickCount > 1200) {
            discard();
            return;
        }

        if (!isArmed() && tickCount > 20) {
            setArmed(true);
            level().playSound(null, blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 1.5F);
        }

        if (isArmed() && tickCount % 4 == 0) {
            AABB triggerBox = getBoundingBox().inflate(1.5D);
            List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, triggerBox, e -> e.isAlive() && e != getOwner());
            for (LivingEntity entity : list) {
                boolean isEnemy = (entity instanceof Enemy);
                if (isTargetFriend() != isEnemy) {
                    detonate(new EntityHitResult(entity));
                    return;
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.getType() == HitResult.Type.BLOCK) {
            setDeltaMovement(Vec3.ZERO);
        } else if (result.getType() == HitResult.Type.ENTITY && isArmed()) {
            detonate(result);
        }
    }

    private void detonate(HitResult hit) {
        if (level().isClientSide()) {
            return;
        }
        level().playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.0F, 1.2F);
        LivingEntity shooter = getOwner() instanceof LivingEntity living ? living : null;
        TCFocusCastExecutor.executeRemainingNodesAt(level(), shooter, hit, remainingNodes, power);
        discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("armed", isArmed());
        tag.putBoolean("target_friend", isTargetFriend());
        tag.putString("remaining_nodes", remainingNodes);
        tag.putFloat("power", power);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setArmed(tag.getBoolean("armed"));
        setTargetFriend(tag.getBoolean("target_friend"));
        remainingNodes = tag.getString("remaining_nodes");
        power = tag.getFloat("power");
    }
}
