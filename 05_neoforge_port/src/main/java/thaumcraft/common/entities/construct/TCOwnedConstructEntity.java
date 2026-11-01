package thaumcraft.common.entities.construct;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;

public abstract class TCOwnedConstructEntity extends PathfinderMob implements OwnableEntity {
    protected static final EntityDataAccessor<Byte> TAMED =
            SynchedEntityData.defineId(TCOwnedConstructEntity.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID =
            SynchedEntityData.defineId(TCOwnedConstructEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private boolean validSpawn = false;

    protected TCOwnedConstructEntity(EntityType<? extends TCOwnedConstructEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TAMED, (byte) 0);
        builder.define(OWNER_UNIQUE_ID, Optional.empty());
    }

    public boolean isOwned() {
        return (entityData.get(TAMED) & 0x4) != 0;
    }

    public void setOwned(boolean owned) {
        byte b = entityData.get(TAMED);
        if (owned) {
            entityData.set(TAMED, (byte) (b | 0x4));
        } else {
            entityData.set(TAMED, (byte) (b & ~0x4));
        }
    }

    @Override
    @Nullable
    public UUID getOwnerUUID() {
        return entityData.get(OWNER_UNIQUE_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(uuid));
        setOwned(uuid != null);
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        UUID uuid = getOwnerUUID();
        if (uuid == null) {
            return null;
        }
        return level().getPlayerByUUID(uuid);
    }

    public boolean isOwner(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        UUID uuid = getOwnerUUID();
        return uuid != null && uuid.equals(entity.getUUID());
    }

    public boolean isValidSpawn() {
        return validSpawn;
    }

    public void setValidSpawn(boolean valid) {
        this.validSpawn = valid;
    }

    @Override
    public boolean canDrownInFluidType(net.neoforged.neoforge.fluids.FluidType type) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 240;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public PlayerTeam getTeam() {
        if (isOwned()) {
            LivingEntity owner = getOwner();
            if (owner instanceof Player p) {
                return p.getTeam();
            }
        }
        return super.getTeam();
    }

    @Override
    public boolean isAlliedTo(net.minecraft.world.entity.Entity entity) {
        if (isOwned()) {
            LivingEntity owner = getOwner();
            if (entity == owner) {
                return true;
            }
            if (owner != null) {
                return owner.isAlliedTo(entity);
            }
        }
        return super.isAlliedTo(entity);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("validSpawn", validSpawn);
        UUID uuid = getOwnerUUID();
        if (uuid != null) {
            tag.putUUID("OwnerUUID", uuid);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.validSpawn = tag.getBoolean("validSpawn");
        if (tag.hasUUID("OwnerUUID")) {
            setOwnerUUID(tag.getUUID("OwnerUUID"));
        } else if (tag.contains("OwnerUUID")) {
            try {
                setOwnerUUID(UUID.fromString(tag.getString("OwnerUUID")));
            } catch (Exception ignored) {
            }
        }
    }
}
