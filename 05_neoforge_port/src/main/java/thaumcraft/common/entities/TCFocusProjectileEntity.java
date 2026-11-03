package thaumcraft.common.entities;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.items.casters.TCFocusCastExecutor;
import thaumcraft.common.items.casters.TCFocusPackageHelper;
import thaumcraft.common.registry.TCEntityTypes;

/**
 * TC6 EntityFocusProjectile: caster-discharged focus orb projectile with bouncy and seeking mechanics.
 */
public class TCFocusProjectileEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<Integer> SPECIAL =
            SynchedEntityData.defineId(TCFocusProjectileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(TCFocusProjectileEntity.class, EntityDataSerializers.INT);

    private String remainingNodes = "";
    private float power = 1.0F;
    private Entity target = null;

    public TCFocusProjectileEntity(EntityType<? extends TCFocusProjectileEntity> type, Level level) {
        super(type, level);
    }

    public TCFocusProjectileEntity(Level level, LivingEntity shooter, float speed, Vec3 direction, int special, String remainingNodes, float power, int color) {
        super(TCEntityTypes.FOCUS_PROJECTILE.get(), shooter, level);
        this.remainingNodes = remainingNodes != null ? remainingNodes : "";
        this.power = power;
        setSpecial(special);
        setColor(color);

        Vec3 look = direction != null ? direction.normalize() : shooter.getLookAngle();
        setPos(
                shooter.getX() + look.x * shooter.getBbWidth() * 1.5D,
                shooter.getEyeY() - 0.1D + look.y * shooter.getBbWidth() * 1.5D,
                shooter.getZ() + look.z * shooter.getBbWidth() * 1.5D
        );
        shoot(look.x, look.y, look.z, speed, 0.0F);
    }

    public TCFocusProjectileEntity(Level level, Vec3 pos, LivingEntity shooter, float speed, Vec3 direction, int special, String remainingNodes, float power, int color) {
        super(TCEntityTypes.FOCUS_PROJECTILE.get(), level);
        if (shooter != null) {
            setOwner(shooter);
        }
        this.remainingNodes = remainingNodes != null ? remainingNodes : "";
        this.power = power;
        setSpecial(special);
        setColor(color);
        Vec3 look = direction != null && direction.lengthSqr() > 1.0E-6D ? direction.normalize() : new Vec3(0, 1, 0);
        setPos(pos.x, pos.y, pos.z);
        shoot(look.x, look.y, look.z, speed, 0.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SPECIAL, 0);
        builder.define(COLOR, 0xFFFFFF);
    }

    public int getSpecial() {
        return entityData.get(SPECIAL);
    }

    public void setSpecial(int special) {
        entityData.set(SPECIAL, special);
    }

    public int getColor() {
        return entityData.get(COLOR);
    }

    public String getRemainingNodes() {
        return remainingNodes != null ? remainingNodes : "";
    }

    public float getPower() {
        return power;
    }

    public void setColor(int color) {
        entityData.set(COLOR, color);
    }

    @Override
    protected double getDefaultGravity() {
        return getSpecial() > 1 ? 0.005D : 0.01D;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > 1200) {
            discard();
            return;
        }

        if (level().isClientSide()) {
            if (level().random.nextFloat() < 0.8F) {
                level().addParticle(
                        ParticleTypes.CRIT,
                        getX(), getY(), getZ(),
                        (level().random.nextDouble() - 0.5D) * 0.1D,
                        (level().random.nextDouble() - 0.5D) * 0.1D,
                        (level().random.nextDouble() - 0.5D) * 0.1D
                );
            }
            return;
        }

        // Seeking behavior (special 2 = hostile, special 3 = friendly)
        if (getSpecial() > 1 && tickCount % 3 == 0) {
            if (target == null || !target.isAlive() || target.distanceToSqr(this) > 256.0D) {
                target = findSeekingTarget();
            }
            if (target != null) {
                Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5D, 0);
                Vec3 toTarget = targetPos.subtract(position()).normalize();
                Vec3 curMotion = getDeltaMovement();
                double speed = curMotion.length();
                Vec3 steered = curMotion.add(toTarget.scale(0.2D)).normalize().scale(speed);
                setDeltaMovement(steered);
                hasImpulse = true;
            }
        }
    }

    private Entity findSeekingTarget() {
        AABB box = getBoundingBox().inflate(12.0D);
        List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, box, e -> e != getOwner() && e.isAlive());
        Entity best = null;
        double bestDist = Double.MAX_VALUE;
        boolean seekHostile = getSpecial() == 2;

        for (LivingEntity entity : list) {
            boolean isEnemy = (entity instanceof Enemy);
            if (seekHostile == isEnemy) {
                double d = entity.distanceToSqr(this);
                if (d < bestDist) {
                    bestDist = d;
                    best = entity;
                }
            }
        }
        return best;
    }

    @Override
    protected void onHit(HitResult result) {
        if (level().isClientSide()) {
            return;
        }

        if (getSpecial() == 1 && result.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bhr = (BlockHitResult) result;
            Vec3 motion = getDeltaMovement();
            switch (bhr.getDirection().getAxis()) {
                case X -> motion = new Vec3(-motion.x * 0.85D, motion.y * 0.85D, motion.z * 0.85D);
                case Y -> motion = new Vec3(motion.x * 0.85D, -motion.y * 0.85D, motion.z * 0.85D);
                case Z -> motion = new Vec3(motion.x * 0.85D, motion.y * 0.85D, -motion.z * 0.85D);
            }
            setDeltaMovement(motion);
            level().playSound(null, blockPosition(), SoundEvents.ARROW_HIT, SoundSource.PLAYERS, 0.4F, 1.2F);
            if (motion.length() < 0.15D || tickCount > 600) {
                executeRemaining(result);
                discard();
            }
            return;
        }

        executeRemaining(result);
        discard();
    }

    private void executeRemaining(HitResult result) {
        if (remainingNodes == null || remainingNodes.isBlank()) {
            return;
        }
        LivingEntity shooter = getOwner() instanceof LivingEntity living ? living : null;
        TCFocusCastExecutor.executeRemainingNodesAt(level(), shooter, result, remainingNodes, power);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("special", getSpecial());
        tag.putInt("color", getColor());
        tag.putString("remaining_nodes", remainingNodes);
        tag.putFloat("power", power);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setSpecial(tag.getInt("special"));
        setColor(tag.getInt("color"));
        remainingNodes = tag.getString("remaining_nodes");
        power = tag.getFloat("power");
    }
}
