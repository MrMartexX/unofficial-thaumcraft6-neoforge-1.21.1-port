package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.common.container.slot.SlotLimitedByClass;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.ItemFocusPouch;

public class ContainerFocusPouch extends Container implements IInventoryChangedListener {
   private World worldObj;
   private int posX;
   private int posY;
   private int posZ;
   private int blockSlot;
   public IInventory input = new InventoryFocusPouch(this);
   ItemStack pouch = null;
   EntityPlayer player = null;

   public ContainerFocusPouch(InventoryPlayer iinventory, World par2World, int par3, int par4, int par5) {
      this.worldObj = par2World;
      this.posX = par3;
      this.posY = par4;
      this.posZ = par5;
      this.player = iinventory.field_70458_d;
      this.pouch = iinventory.func_70448_g();
      this.blockSlot = iinventory.field_70461_c + 45;

      for (int a = 0; a < 18; a++) {
         this.func_75146_a(new SlotLimitedByClass(ItemFocus.class, this.input, a, 37 + a % 6 * 18, 51 + a / 6 * 18));
      }

      this.bindPlayerInventory(iinventory);
      if (!par2World.field_72995_K) {
         try {
            NonNullList<ItemStack> list = ((ItemFocusPouch)this.pouch.func_77973_b()).getInventory(this.pouch);

            for (int a = 0; a < list.size(); a++) {
               this.input.func_70299_a(a, (ItemStack)list.get(a));
            }
         } catch (Exception var8) {
         }
      }

      this.func_75130_a(this.input);
   }

   public void func_76316_a(IInventory invBasic) {
      this.func_75142_b();
   }

   protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.func_75146_a(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 151 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.func_75146_a(new Slot(inventoryPlayer, i, 8 + i * 18, 209));
      }
   }

   public ItemStack func_82846_b(EntityPlayer par1EntityPlayer, int slot) {
      if (slot == this.blockSlot) {
         return ItemStack.field_190927_a;
      }

      ItemStack stack = ItemStack.field_190927_a;
      Slot slotObject = (Slot)this.field_75151_b.get(slot);
      if (slotObject != null && slotObject.func_75216_d()) {
         ItemStack stackInSlot = slotObject.func_75211_c();
         stack = stackInSlot.func_77946_l();
         if (slot < 18) {
            if (!this.input.func_94041_b(slot, stackInSlot) || !this.func_75135_a(stackInSlot, 18, this.field_75151_b.size(), true)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.input.func_94041_b(slot, stackInSlot) || !this.func_75135_a(stackInSlot, 0, 18, false)) {
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

   public boolean func_75145_c(EntityPlayer var1) {
      return true;
   }

   public ItemStack func_184996_a(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
      return slotId == this.blockSlot ? ItemStack.field_190927_a : super.func_184996_a(slotId, dragType, clickTypeIn, player);
   }

   public void func_75134_a(EntityPlayer par1EntityPlayer) {
      super.func_75134_a(par1EntityPlayer);
      if (!this.worldObj.field_72995_K) {
         NonNullList<ItemStack> list = NonNullList.func_191197_a(18, ItemStack.field_190927_a);

         for (int a = 0; a < list.size(); a++) {
            list.set(a, this.input.func_70301_a(a));
         }

         if (this.pouch.func_77973_b() instanceof ItemFocusPouch) {
            ((ItemFocusPouch)this.pouch.func_77973_b()).setInventory(this.pouch, list);
         }

         if (this.player == null) {
            return;
         }

         if (this.player.func_184586_b(this.player.func_184600_cs()).func_77969_a(this.pouch)) {
            this.player.func_184611_a(this.player.func_184600_cs(), this.pouch);
         }

         this.player.field_71071_by.func_70296_d();
      }
   }
}
