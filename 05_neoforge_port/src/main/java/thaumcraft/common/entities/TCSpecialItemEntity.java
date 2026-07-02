package thaumcraft.common.entities;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.registry.TCEntityTypes;

public class TCSpecialItemEntity extends ItemEntity {
    public TCSpecialItemEntity(EntityType<? extends TCSpecialItemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public TCSpecialItemEntity(Level level, double x, double y, double z, ItemStack stack) {
        this(TCEntityTypes.SPECIAL_ITEM.get(), level);
        setPos(x, y, z);
        setItem(stack);
        setYRot(random.nextFloat() * 360.0F);
        setDeltaMovement(
                random.nextDouble() * 0.2D - 0.1D,
                0.2D,
                random.nextDouble() * 0.2D - 0.1D
        );
        lifespan = stack.getEntityLifespan(level);
    }

    @Override
    public void tick() {
        if (tickCount > 1) {
            Vec3 movement = getDeltaMovement();
            if (movement.y > 0.0D) {
                movement = new Vec3(movement.x, movement.y * 0.9D, movement.z);
            }
            setDeltaMovement(movement.add(0.0D, 0.04D, 0.0D));
        }
        super.tick();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !source.is(DamageTypeTags.IS_EXPLOSION) && super.hurt(source, amount);
    }
}
