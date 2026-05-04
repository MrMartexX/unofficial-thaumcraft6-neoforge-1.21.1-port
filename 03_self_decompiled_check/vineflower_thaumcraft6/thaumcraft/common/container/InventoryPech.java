package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import thaumcraft.common.entities.monster.EntityPech;

public class InventoryPech extends InventoryBasic {
   private final EntityPech theMerchant;
   private final EntityPlayer thePlayer;

   public InventoryPech(IInventoryChangedListener listener, EntityPlayer par1EntityPlayer, EntityPech par2IMerchant) {
      super("container.pech", false, 5);
      this.func_110134_a(listener);
      this.thePlayer = par1EntityPlayer;
      this.theMerchant = par2IMerchant;
   }

   public boolean func_70300_a(EntityPlayer player) {
      return this.theMerchant.isTamed();
   }

   public boolean func_94041_b(int index, ItemStack stack) {
      return index == 0;
   }
}
