package thaumcraft.common.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * TC6 EntityGolemDart: golem ranged combat dart projectile.
 */
public class TCGolemDartEntity extends AbstractArrow {

    public TCGolemDartEntity(EntityType<? extends TCGolemDartEntity> type, Level level) {
        super(type, level);
    }

    public TCGolemDartEntity(EntityType<? extends TCGolemDartEntity> type, LivingEntity shooter, Level level) {
        super(type, shooter, level, new ItemStack(Items.ARROW), null);
    }

    @Override
    protected ItemStack getDefaultPickupItem() { return new ItemStack(Items.ARROW); }
}
