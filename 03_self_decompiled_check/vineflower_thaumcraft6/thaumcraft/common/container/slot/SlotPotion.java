package thaumcraft.common.container.slot;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;

public class SlotPotion extends Slot {
   int limit = 64;

   public SlotPotion(IInventory par2IInventory, int par3, int par4, int par5) {
      super(par2IInventory, par3, par4, par5);
   }

   public SlotPotion(int limit, IInventory par2IInventory, int par3, int par4, int par5) {
      super(par2IInventory, par3, par4, par5);
      this.limit = limit;
   }

   public boolean func_75214_a(ItemStack stack) {
      return stack != null && !stack.func_190926_b() && isValidPotion(stack);
   }

   public static boolean isValidPotion(ItemStack stack) {
      if (stack.func_77973_b() == Items.field_151068_bn || stack.func_77973_b() == Items.field_185156_bI || stack.func_77973_b() == Items.field_185155_bH) {
         try {
            PotionType potion = PotionUtils.func_185191_c(stack);
            return potion != null
               && potion != PotionTypes.field_185230_b
               && potion != PotionTypes.field_185233_e
               && potion != PotionTypes.field_185229_a
               && potion != PotionTypes.field_185231_c
               && potion != PotionTypes.field_185232_d;
         } catch (Exception var2) {
         }
      }

      return false;
   }

   public int func_75219_a() {
      return this.limit;
   }
}
