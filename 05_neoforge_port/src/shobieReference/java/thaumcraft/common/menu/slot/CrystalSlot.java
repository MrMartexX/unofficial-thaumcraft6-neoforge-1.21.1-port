package thaumcraft.common.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;

/**
 * CrystalSlot - A slot that only accepts crystal essence of a specific aspect.
 * 
 * Used in the Arcane Workbench for the 6 primal aspect crystal slots.
 * Each slot filters for one specific primal aspect: AIR, FIRE, WATER, EARTH, ORDER, ENTROPY.
 */
public class CrystalSlot extends Slot {
    
    private final Aspect requiredAspect;
    
    public CrystalSlot(Aspect aspect, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.requiredAspect = aspect;
    }
    
    @Override
    public boolean mayPlace(ItemStack stack) {
        return isValidCrystal(stack, requiredAspect);
    }
    
    /**
     * Check if the given item stack is a valid crystal for the specified aspect.
     * 
     * @param stack The item stack to check
     * @param aspect The aspect to match
     * @return true if the stack is a crystal containing the required aspect
     */
    public static boolean isValidCrystal(ItemStack stack, Aspect aspect) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        
        // Check if it's an essentia container item (crystals implement this)
        if (stack.getItem() instanceof IEssentiaContainerItem crystalItem) {
            var aspects = crystalItem.getAspects(stack);
            if (aspects != null && aspects.size() > 0) {
                Aspect[] contained = aspects.getAspects();
                return contained != null && contained.length > 0 && contained[0] == aspect;
            }
        }
        
        return false;
    }
    
    public Aspect getRequiredAspect() {
        return requiredAspect;
    }
}
