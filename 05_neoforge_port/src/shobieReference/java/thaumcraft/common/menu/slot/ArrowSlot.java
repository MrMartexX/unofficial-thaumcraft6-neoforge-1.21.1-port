package thaumcraft.common.menu.slot;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;

/**
 * ArrowSlot - A mob equipment slot that only accepts arrows.
 * 
 * Used for crossbow turrets to hold their ammunition.
 */
public class ArrowSlot extends MobEquipmentSlot {
    
    public ArrowSlot(Mob entity, int slotIndex, int x, int y) {
        super(entity, slotIndex, x, y);
    }
    
    @Override
    public boolean mayPlace(ItemStack stack) {
        return isValidArrow(stack);
    }
    
    /**
     * Check if the given item is a valid arrow.
     * 
     * @param stack The item stack to check
     * @return true if the stack is an arrow item
     */
    public static boolean isValidArrow(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof ArrowItem;
    }
}
