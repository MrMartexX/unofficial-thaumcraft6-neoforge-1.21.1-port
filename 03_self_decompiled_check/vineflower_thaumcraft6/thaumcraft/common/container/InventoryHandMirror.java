package thaumcraft.common.container;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class InventoryHandMirror extends InventoryBasic {
   Container container;

   public InventoryHandMirror(IInventoryChangedListener listener) {
      super("container.handmirror", false, 1);
      this.func_110134_a(listener);
      this.container = (Container)listener;
   }

   public void func_70299_a(int index, ItemStack stack) {
      super.func_70299_a(index, stack);
      if (!stack.func_190926_b()) {
         this.container.func_75130_a(this);
      }
   }
}
