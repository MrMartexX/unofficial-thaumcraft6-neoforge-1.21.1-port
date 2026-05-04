package thaumcraft.common.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidUtil;

public class SlotGhostFluid extends SlotGhost {
   public SlotGhostFluid(IInventory par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
   }

   @Override
   public int func_75219_a() {
      return 1;
   }

   public boolean func_75214_a(ItemStack stack1) {
      return FluidUtil.getFluidHandler(stack1) != null;
   }

   @Override
   public boolean func_82869_a(EntityPlayer par1EntityPlayer) {
      return false;
   }
}
