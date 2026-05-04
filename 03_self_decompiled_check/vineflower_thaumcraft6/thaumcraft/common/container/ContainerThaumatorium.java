package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.common.tiles.crafting.TileThaumatorium;

public class ContainerThaumatorium extends Container {
   private TileThaumatorium thaumatorium;
   private EntityPlayer player = null;

   public ContainerThaumatorium(InventoryPlayer par1InventoryPlayer, TileThaumatorium tileEntity) {
      this.player = par1InventoryPlayer.field_70458_d;
      this.thaumatorium = tileEntity;
      this.thaumatorium.eventHandler = this;
      this.func_75146_a(new Slot(tileEntity, 0, 55, 24));

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.func_75146_a(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 135 + i * 18));
         }
      }

      for (int var5 = 0; var5 < 9; var5++) {
         this.func_75146_a(new Slot(par1InventoryPlayer, var5, 8 + var5 * 18, 193));
      }

      this.thaumatorium.updateRecipes(this.player);
   }

   public void func_75142_b() {
      super.func_75142_b();
      this.thaumatorium.updateRecipes(this.player);
   }

   public void func_75134_a(EntityPlayer par1EntityPlayer) {
      super.func_75134_a(par1EntityPlayer);
      if (!this.thaumatorium.func_145831_w().field_72995_K) {
         this.thaumatorium.eventHandler = null;
      }
   }

   public boolean func_75145_c(EntityPlayer par1EntityPlayer) {
      return this.thaumatorium.func_70300_a(par1EntityPlayer);
   }

   public ItemStack func_82846_b(EntityPlayer par1EntityPlayer, int par2) {
      ItemStack itemstack = ItemStack.field_190927_a;
      Slot slot = (Slot)this.field_75151_b.get(par2);
      if (slot != null && slot.func_75216_d()) {
         ItemStack itemstack1 = slot.func_75211_c();
         itemstack = itemstack1.func_77946_l();
         if (par2 != 0) {
            if (!this.func_75135_a(itemstack1, 0, 1, false)) {
               return ItemStack.field_190927_a;
            }
         } else if (par2 >= 1 && par2 < 28) {
            if (!this.func_75135_a(itemstack1, 28, 37, false)) {
               return ItemStack.field_190927_a;
            }
         } else {
            if (par2 >= 28 && par2 < 37 && !this.func_75135_a(itemstack1, 1, 28, false)) {
               return ItemStack.field_190927_a;
            }

            if (!this.func_75135_a(itemstack1, 1, 37, false)) {
               return ItemStack.field_190927_a;
            }
         }

         if (itemstack1.func_190916_E() == 0) {
            slot.func_75215_d(ItemStack.field_190927_a);
         } else {
            slot.func_75218_e();
         }

         if (itemstack1.func_190916_E() == itemstack.func_190916_E()) {
            return ItemStack.field_190927_a;
         }

         slot.func_190901_a(par1EntityPlayer, itemstack1);
      }

      return itemstack;
   }
}
