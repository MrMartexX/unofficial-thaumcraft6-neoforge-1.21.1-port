package thaumcraft.common.entities;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.items.casters.TCFocusCastExecutor;
import thaumcraft.common.registry.TCEntityTypes;

/**
 * TC6 EntitySpellBat: focus-summoned bat variant that flies to targets and discharges spells.
 */
public class TCSpellbatEntity extends TCFirebatEntity {
    private static final EntityDataAccessor<Boolean> FRIENDLY =
            SynchedEntityData.defineId(TCSpellbatEntity.class, EntityDataSerializers.BOOLEAN);

    private UUID ownerUuid = null;
    private LivingEntity owner = null;
    private String remainingNodes = "";
    private float power = 0.33F;
    private LivingEntity target = null;

    public TCSpellbatEntity(EntityType<? extends TCSpellbatEntity> type, Level level) {
        super(type, level);
    }

    public TCSpellbatEntity(Level level, LivingEntity owner, boolean friendly, String remainingNodes, float power) {
        super(TCEntityTypes.SPELLBAT.get(), level);
        this.owner = owner;
        this.ownerUuid = owner != null ? owner.getUUID() : null;
        this.remainingNodes = remainingNodes != null ? remainingNodes : "";
        this.power = power;
        setFriendly(friendly);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FRIENDLY, false);
    }

    public boolean isFriendly() {
        return entityData.get(FRIENDLY);
    }

    public void setFriendly(boolean friendly) {
        entityData.set(FRIENDLY, friendly);
    }

    public LivingEntity getOwner() {
        if (owner == null && ownerUuid != null && level() instanceof ServerLevel serverLevel) {
            Entity e = serverLevel.getEntity(ownerUuid);
            if (e instanceof LivingEntity living) {
                owner = living;
            }
        }
        return owner;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            if (level().random.nextFloat() < 0.3F) {
                level().addParticle(
                        ParticleTypes.ENCHANT,
                        getX() + (level().random.nextDouble() - 0.5D) * 0.4D,
                        getY() + 0.2D,
                        getZ() + (level().random.nextDouble() - 0.5D) * 0.4D,
                        0.0D, 0.05D, 0.0D
                );
            }
            return;
        }

        if (tickCount > 600 || getOwner() == null) {
            discard();
            return;
        }

        if (target == null || !target.isAlive() || target.distanceToSqr(this) > 400.0D) {
            target = findTarget();
        }

        if (target != null) {
            Vec3 toTarget = target.position().add(0, target.getBbHeight() * 0.5D, 0).subtract(position());
            double dist = toTarget.length();
            if (dist < 1.8D) {
                detonateOnTarget(target);
                return;
            }
            Vec3 dir = toTarget.normalize().scale(0.35D);
            setDeltaMovement(getDeltaMovement().scale(0.8D).add(dir.scale(0.25D)));
        }
    }

    private LivingEntity findTarget() {
        AABB box = getBoundingBox().inflate(16.0D);
        List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, box, e -> e.isAlive() && e != getOwner());
        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;
        boolean wantFriend = isFriendly();

        for (LivingEntity entity : list) {
            boolean isEnemy = (entity instanceof Enemy);
            if (wantFriend != isEnemy) {
                double d = entity.distanceToSqr(this);
                if (d < bestDist) {
                    bestDist = d;
                    best = entity;
                }
            }
        }
        return best;
    }

    private void detonateOnTarget(LivingEntity hitTarget) {
        level().playSound(null, blockPosition(), SoundEvents.BAT_TAKEOFF, SoundSource.NEUTRAL, 0.8F, 1.2F);
        TCFocusCastExecutor.executeRemainingNodesAt(level(), getOwner(), new EntityHitResult(hitTarget), remainingNodes, power);
        discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("friendly", isFriendly());
        if (ownerUuid != null) {
            tag.putUUID("owner_uuid", ownerUuid);
        }
        tag.putString("remaining_nodes", remainingNodes);
        tag.putFloat("power", power);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setFriendly(tag.getBoolean("friendly"));
        if (tag.hasUUID("owner_uuid")) {
            ownerUuid = tag.getUUID("owner_uuid");
        }
        remainingNodes = tag.getString("remaining_nodes");
        power = tag.getFloat("power");
    }
}
