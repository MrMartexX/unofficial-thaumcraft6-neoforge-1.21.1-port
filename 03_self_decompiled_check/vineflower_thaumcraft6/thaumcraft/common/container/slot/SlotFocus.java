package thaumcraft.common.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.casters.ItemFocus;

public class SlotFocus extends Slot {
   int limit = 64;

   public SlotFocus(IInventory par2IInventory, int par3, int par4, int par5) {
      super(par2IInventory, par3, par4, par5);
   }

   public SlotFocus(int limit, IInventory par2IInventory, int par3, int par4, int par5) {
      super(par2IInventory, par3, par4, par5);
      this.limit = limit;
   }

   public boolean func_75214_a(ItemStack stack) {
      return stack != null && !stack.func_190926_b() && stack.func_77973_b() != null && stack.func_77973_b() instanceof ItemFocus;
   }

   public int func_75219_a() {
      return this.limit;
   }
}
