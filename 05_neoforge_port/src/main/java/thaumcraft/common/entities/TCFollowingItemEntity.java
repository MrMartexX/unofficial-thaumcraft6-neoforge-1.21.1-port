package thaumcraft.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import thaumcraft.common.registry.TCEntityTypes;

public class TCFollowingItemEntity extends TCSpecialItemEntity implements IEntityWithComplexSpawn {
    private double targetX;
    private double targetY;
    private double targetZ;
    private int followType = 3;
    private Entity target;
    private int approachAge = 20;
    private double gravity = 0.04D;

    public TCFollowingItemEntity(EntityType<? extends TCFollowingItemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public TCFollowingItemEntity(Level level, double x, double y, double z, ItemStack stack) {
        super(TCEntityTypes.FOLLOW_ITEM.get(), level);
        setPos(x, y, z);
        setItem(stack);
        setYRot(random.nextFloat() * 360.0F);
        lifespan = stack.getEntityLifespan(level);
    }

    public TCFollowingItemEntity(Level level, double x, double y, double z, ItemStack stack, Entity target, int followType) {
        this(level, x, y, z, stack);
        this.target = target;
        this.targetX = target.getX();
        this.targetY = target.getBoundingBox().minY + target.getBbHeight() / 2.0D;
        this.targetZ = target.getZ();
        this.followType = followType;
        noPhysics = true;
    }

    public TCFollowingItemEntity(Level level, double x, double y, double z, ItemStack stack, double targetX, double targetY, double targetZ) {
        this(level, x, y, z, stack);
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    @Override
    public void tick() {
        if (target != null) {
            targetX = target.getX();
            targetY = target.getBoundingBox().minY + target.getBbHeight() / 2.0D;
            targetZ = target.getZ();
        }

        if (targetX != 0.0D || targetY != 0.0D || targetZ != 0.0D) {
            float xDelta = (float) (targetX - getX());
            float yDelta = (float) (targetY - getY());
            float zDelta = (float) (targetZ - getZ());
            if (approachAge > 1) {
                approachAge--;
            }

            double distance = Mth.sqrt(xDelta * xDelta + yDelta * yDelta + zDelta * zDelta);
            if (distance > 0.5D) {
                double scaledDistance = distance * approachAge;
                setDeltaMovement(xDelta / scaledDistance, yDelta / scaledDistance, zDelta / scaledDistance);
            } else {
                Vec3 movement = getDeltaMovement().scale(0.1D);
                setDeltaMovement(movement);
                targetX = 0.0D;
                targetY = 0.0D;
                targetZ = 0.0D;
                target = null;
                noPhysics = false;
            }
        } else {
            Vec3 movement = getDeltaMovement();
            setDeltaMovement(movement.x, movement.y - gravity, movement.z);
        }

        super.tick();
    }

    public int followType() {
        return followType;
    }

    public double targetX() {
        return targetX;
    }

    public double targetY() {
        return targetY;
    }

    public double targetZ() {
        return targetZ;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putShort("type", (short) followType);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        followType = tag.getShort("type");
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(target == null ? -1 : target.getId());
        buffer.writeDouble(targetX);
        buffer.writeDouble(targetY);
        buffer.writeDouble(targetZ);
        buffer.writeByte(followType);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer) {
        int targetId = buffer.readInt();
        if (targetId > -1) {
            target = level().getEntity(targetId);
        }
        targetX = buffer.readDouble();
        targetY = buffer.readDouble();
        targetZ = buffer.readDouble();
        followType = buffer.readByte();
    }
}
