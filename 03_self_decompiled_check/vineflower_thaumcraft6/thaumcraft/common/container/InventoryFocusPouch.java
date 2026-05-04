package thaumcraft.common.container;

import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.casters.ItemFocus;

public class InventoryFocusPouch extends InventoryBasic {
   public InventoryFocusPouch(IInventoryChangedListener listener) {
      super("container.focuspouch", false, 18);
      this.func_110134_a(listener);
   }

   public int func_70297_j_() {
      return 1;
   }

   public boolean func_94041_b(int i, ItemStack itemstack) {
      return !itemstack.func_190926_b() && itemstack.func_77973_b() instanceof ItemFocus;
   }
}
