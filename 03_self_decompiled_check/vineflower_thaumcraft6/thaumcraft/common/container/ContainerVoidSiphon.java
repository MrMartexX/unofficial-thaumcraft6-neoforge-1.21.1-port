package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.container.slot.SlotOutput;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;

public class ContainerVoidSiphon extends Container {
   private TileVoidSiphon siphon;
   private int lastProgress;

   public ContainerVoidSiphon(InventoryPlayer par1InventoryPlayer, TileVoidSiphon tileEntity) {
      this.siphon = tileEntity;
      this.func_75146_a(new SlotOutput(tileEntity, 0, 80, 32));

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.func_75146_a(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int var5 = 0; var5 < 9; var5++) {
         this.func_75146_a(new Slot(par1InventoryPlayer, var5, 8 + var5 * 18, 142));
      }
   }

   public void func_75132_a(IContainerListener par1ICrafting) {
      super.func_75132_a(par1ICrafting);
      par1ICrafting.func_71112_a(this, 0, this.siphon.progress);
   }

   public void func_75142_b() {
      super.func_75142_b();

      for (int i = 0; i < this.field_75149_d.size(); i++) {
         IContainerListener icrafting = (IContainerListener)this.field_75149_d.get(i);
         if (this.lastProgress != this.siphon.progress) {
            icrafting.func_71112_a(this, 0, this.siphon.progress);
         }
      }

      this.lastProgress = this.siphon.progress;
   }

   @SideOnly(Side.CLIENT)
   public void func_75137_b(int par1, int par2) {
      if (par1 == 0) {
         this.siphon.progress = par2;
      }
   }

   public boolean func_75145_c(EntityPlayer par1EntityPlayer) {
      return this.siphon.func_70300_a(par1EntityPlayer);
   }

   public ItemStack func_82846_b(EntityPlayer par1EntityPlayer, int slot) {
      ItemStack stack = ItemStack.field_190927_a;
      Slot slotObject = (Slot)this.field_75151_b.get(slot);
      if (slotObject != null && slotObject.func_75216_d()) {
         ItemStack stackInSlot = slotObject.func_75211_c();
         stack = stackInSlot.func_77946_l();
         if (slot == 0) {
            if (!this.siphon.func_94041_b(slot, stackInSlot) || !this.func_75135_a(stackInSlot, 1, this.field_75151_b.size(), true)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.siphon.func_94041_b(slot, stackInSlot) || !this.func_75135_a(stackInSlot, 0, 1, false)) {
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
