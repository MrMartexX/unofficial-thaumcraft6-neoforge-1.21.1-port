package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipesArmorDyes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.DyeUtils;
import thaumcraft.common.items.armor.ItemRobeArmor;

public class RecipesRobeArmorDyes extends RecipesArmorDyes {
   public boolean func_77569_a(InventoryCrafting par1InventoryCrafting, World par2World) {
      ItemStack itemstack = ItemStack.field_190927_a;
      ArrayList arraylist = new ArrayList();

      for (int i = 0; i < par1InventoryCrafting.func_70302_i_(); i++) {
         ItemStack itemstack1 = par1InventoryCrafting.func_70301_a(i);
         if (itemstack1 != null && !itemstack1.func_190926_b()) {
            if (itemstack1.func_77973_b() instanceof ItemArmor) {
               ItemArmor itemarmor = (ItemArmor)itemstack1.func_77973_b();
               if (!(itemarmor instanceof ItemRobeArmor) || !itemstack.func_190926_b()) {
                  return false;
               }

               itemstack = itemstack1;
            } else {
               if (!DyeUtils.isDye(itemstack1)) {
                  return false;
               }

               arraylist.add(itemstack1);
            }
         }
      }

      return !itemstack.func_190926_b() && !arraylist.isEmpty();
   }

   public ItemStack func_77572_b(InventoryCrafting par1InventoryCrafting) {
      ItemStack itemstack = ItemStack.field_190927_a;
      int[] aint = new int[3];
      int i = 0;
      int j = 0;
      ItemArmor itemarmor = null;

      for (int k = 0; k < par1InventoryCrafting.func_70302_i_(); k++) {
         ItemStack itemstack1 = par1InventoryCrafting.func_70301_a(k);
         if (itemstack1 != null && !itemstack1.func_190926_b()) {
            if (itemstack1.func_77973_b() instanceof ItemArmor) {
               itemarmor = (ItemArmor)itemstack1.func_77973_b();
               if (!(itemarmor instanceof ItemRobeArmor) || !itemstack.func_190926_b()) {
                  return ItemStack.field_190927_a;
               }

               itemstack = itemstack1.func_77946_l();
               itemstack.func_190920_e(1);
               if (itemarmor.func_82816_b_(itemstack1)) {
                  int l = itemarmor.func_82814_b(itemstack);
                  float f = (l >> 16 & 0xFF) / 255.0F;
                  float f1 = (l >> 8 & 0xFF) / 255.0F;
                  float f2 = (l & 0xFF) / 255.0F;
                  i = (int)(i + Math.max(f, Math.max(f1, f2)) * 255.0F);
                  aint[0] = (int)(aint[0] + f * 255.0F);
                  aint[1] = (int)(aint[1] + f1 * 255.0F);
                  aint[2] = (int)(aint[2] + f2 * 255.0F);
                  j++;
               }
            } else {
               if (!DyeUtils.isDye(itemstack1)) {
                  return ItemStack.field_190927_a;
               }

               float[] afloat = ((EnumDyeColor)DyeUtils.colorFromStack(itemstack1).get()).func_193349_f();
               int j1 = (int)(afloat[0] * 255.0F);
               int k1 = (int)(afloat[1] * 255.0F);
               int i1 = (int)(afloat[2] * 255.0F);
               i += Math.max(j1, Math.max(k1, i1));
               aint[0] += j1;
               aint[1] += k1;
               aint[2] += i1;
               j++;
            }
         }
      }

      if (itemarmor == null) {
         return ItemStack.field_190927_a;
      }

      int var16 = aint[0] / j;
      int l1 = aint[1] / j;
      int l = aint[2] / j;
      float f = (float)i / j;
      float f1 = Math.max(var16, Math.max(l1, l));
      var16 = (int)(var16 * f / f1);
      l1 = (int)(l1 * f / f1);
      l = (int)(l * f / f1);
      int i1 = (var16 << 8) + l1;
      i1 = (i1 << 8) + l;
      itemarmor.func_82813_b(itemstack, i1);
      return itemstack;
   }
}
