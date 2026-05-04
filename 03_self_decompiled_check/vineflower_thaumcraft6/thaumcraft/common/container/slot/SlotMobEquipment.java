package thaumcraft.common.container.slot;

import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class SlotMobEquipment extends Slot {
   EntityLiving entity;

   public SlotMobEquipment(EntityLiving entity, int par3, int par4, int par5) {
      super(null, par3, par4, par5);
      this.entity = entity;
   }

   public ItemStack func_75211_c() {
      return this.entity.func_184586_b(EnumHand.MAIN_HAND);
   }

   public void func_75215_d(ItemStack stack) {
      this.entity.func_184611_a(EnumHand.MAIN_HAND, stack);
      if (stack != null && !stack.func_190926_b() && stack.func_190916_E() > this.func_75219_a()) {
         stack.func_190920_e(this.func_75219_a());
      }

      this.func_75218_e();
   }

   public void func_75218_e() {
   }

   public int func_75219_a() {
      return 64;
   }

   public ItemStack func_75209_a(int amount) {
      if (!this.func_75211_c().func_190926_b()) {
         if (this.func_75211_c().func_190916_E() <= amount) {
            ItemStack itemstack = this.func_75211_c();
            this.func_75215_d(ItemStack.field_190927_a);
            return itemstack;
         }

         ItemStack itemstack = this.func_75211_c().func_77979_a(amount);
         if (this.func_75211_c().func_190916_E() == 0) {
            this.func_75215_d(ItemStack.field_190927_a);
         }

         return itemstack;
      } else {
         return ItemStack.field_190927_a;
      }
   }

   public boolean func_75217_a(IInventory inv, int slotIn) {
      return slotIn == this.getSlotIndex();
   }
}
