package thaumcraft.common.entities.construct;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thaumcraft.common.menu.TCTurretAdvancedMenu;

public class TCTurretCrossbowAdvancedEntity extends TCTurretCrossbowEntity {
    private static final EntityDataAccessor<Byte> FLAGS =
            SynchedEntityData.defineId(TCTurretCrossbowAdvancedEntity.class, EntityDataSerializers.BYTE);

    public TCTurretCrossbowAdvancedEntity(EntityType<? extends TCTurretCrossbowAdvancedEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.95D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        // Default targetMob = true (bit 1)
        builder.define(FLAGS, (byte) 0x2);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 0.0D, 20, 40, 24.0F));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, LivingEntity.class, 24.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 5, true, false, this::isTargetValid));
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }

    public boolean getTargetAnimal() {
        return (entityData.get(FLAGS) & 0x1) != 0;
    }

    public void setTargetAnimal(boolean flag) {
        byte b = entityData.get(FLAGS);
        entityData.set(FLAGS, (byte) (flag ? (b | 0x1) : (b & ~0x1)));
        setTarget(null);
    }

    public boolean getTargetMob() {
        return (entityData.get(FLAGS) & 0x2) != 0;
    }

    public void setTargetMob(boolean flag) {
        byte b = entityData.get(FLAGS);
        entityData.set(FLAGS, (byte) (flag ? (b | 0x2) : (b & ~0x2)));
        setTarget(null);
    }

    public boolean getTargetPlayer() {
        return (entityData.get(FLAGS) & 0x4) != 0;
    }

    public void setTargetPlayer(boolean flag) {
        byte b = entityData.get(FLAGS);
        entityData.set(FLAGS, (byte) (flag ? (b | 0x4) : (b & ~0x4)));
        setTarget(null);
    }

    public boolean getTargetFriendly() {
        return (entityData.get(FLAGS) & 0x8) != 0;
    }

    public void setTargetFriendly(boolean flag) {
        byte b = entityData.get(FLAGS);
        entityData.set(FLAGS, (byte) (flag ? (b | 0x8) : (b & ~0x8)));
        setTarget(null);
    }

    public boolean isTargetValid(LivingEntity target) {
        if (target == null || !target.isAlive() || target == this) {
            return false;
        }
        if (isOwner(target)) {
            return false;
        }
        if (!getTargetFriendly() && target instanceof OwnableEntity ownable
                && getOwnerUUID() != null && getOwnerUUID().equals(ownable.getOwnerUUID())) {
            return false;
        }
        if (target instanceof Animal && !(target instanceof Enemy)) {
            return getTargetAnimal();
        }
        if (target instanceof Enemy) {
            return getTargetMob();
        }
        if (target instanceof Player player) {
            if (!getTargetPlayer()) {
                return false;
            }
            if (level().getServer() != null && !level().getServer().isPvpAllowed()) {
                return false;
            }
            if (!getTargetFriendly() && isAlliedTo(player)) {
                return false;
            }
            return true;
        }
        if (target instanceof OwnableEntity ownable) {
            UUID ownerId = ownable.getOwnerUUID();
            if (ownerId != null && ownerId.equals(getOwnerUUID())) {
                return getTargetFriendly();
            }
        }
        return false;
    }

    @Override
    protected void openTurretMenu(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) -> new TCTurretAdvancedMenu(containerId, inventory, this),
                Component.translatable("entity.thaumcraft.turret_advanced")
        ), buf -> buf.writeInt(getId()));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("targets", entityData.get(FLAGS));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("targets")) {
            entityData.set(FLAGS, tag.getByte("targets"));
        }
    }
}
