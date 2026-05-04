package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.container.slot.SlotArcaneBorePickaxe;
import thaumcraft.common.entities.construct.EntityArcaneBore;

public class ContainerArcaneBore extends Container {
   private EntityArcaneBore turret;
   private EntityPlayer player;
   private final World theWorld;

   public ContainerArcaneBore(InventoryPlayer par1InventoryPlayer, World par3World, EntityArcaneBore ent) {
      this.turret = ent;
      this.theWorld = par3World;
      this.player = par1InventoryPlayer.field_70458_d;
      this.func_75146_a(new SlotArcaneBorePickaxe(this.turret, 0, 80, 29));

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.func_75146_a(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int var6 = 0; var6 < 9; var6++) {
         this.func_75146_a(new Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 142));
      }
   }

   public boolean func_75140_a(EntityPlayer par1EntityPlayer, int par2) {
      return par2 == 0 ? true : super.func_75140_a(par1EntityPlayer, par2);
   }

   @SideOnly(Side.CLIENT)
   public void func_75137_b(int par1, int par2) {
   }

   public boolean func_75145_c(EntityPlayer par1EntityPlayer) {
      return true;
   }

   public ItemStack func_82846_b(EntityPlayer par1EntityPlayer, int slot) {
      ItemStack stack = ItemStack.field_190927_a;
      Slot slotObject = (Slot)this.field_75151_b.get(slot);
      if (slotObject != null && slotObject.func_75216_d()) {
         ItemStack stackInSlot = slotObject.func_75211_c();
         stack = stackInSlot.func_77946_l();
         if (slot == 0) {
            if (!this.func_75135_a(stackInSlot, 1, this.field_75151_b.size(), true)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.func_75135_a(stackInSlot, 0, 1, false)) {
            return ItemStack.field_190927_a;
         }

         if (stackInSlot.func_190916_E() == 0) {
            slotObject.func_75215_d(ItemStack.field_190927_a);
         } else {
            slotObject.func_75218_e();
         }
      }

      return stack;
   }
}
