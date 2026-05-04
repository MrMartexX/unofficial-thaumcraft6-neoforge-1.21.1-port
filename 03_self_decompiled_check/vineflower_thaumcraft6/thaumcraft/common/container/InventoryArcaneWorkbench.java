package thaumcraft.common.container;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.crafting.IArcaneWorkbench;

public class InventoryArcaneWorkbench extends InventoryCrafting implements IArcaneWorkbench {
   TileEntity workbench;

   public InventoryArcaneWorkbench(TileEntity tileEntity, Container container) {
      super(container, 5, 3);
      this.workbench = tileEntity;
   }

   public String func_70005_c_() {
      return "container.arcaneworkbench";
   }

   public void func_70296_d() {
      super.func_70296_d();
      this.workbench.func_70296_d();
   }
}
