package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.resources.ItemCrystalEssence;

public class RecipeMagicDust extends Impl<IRecipe> implements IRecipe {
   public boolean func_77569_a(InventoryCrafting inv, World worldIn) {
      boolean bowl = false;
      boolean flint = false;
      boolean redstone = false;
      ArrayList<String> crystals = new ArrayList<>();

      for (int a = 0; a < 3; a++) {
         for (int b = 0; b < 3; b++) {
            if (inv.func_70463_b(a, b) != null && !inv.func_70463_b(a, b).func_190926_b()) {
               ItemStack stack = inv.func_70463_b(a, b).func_77946_l();
               if (stack.func_77973_b() == Items.field_151054_z && bowl) {
                  return false;
               }

               if (stack.func_77973_b() == Items.field_151054_z && !bowl) {
                  bowl = true;
               } else {
                  if (stack.func_77973_b() == Items.field_151145_ak && flint) {
                     return false;
                  }

                  if (stack.func_77973_b() == Items.field_151145_ak && !flint) {
                     flint = true;
                  } else {
                     if (stack.func_77973_b() == Items.field_151137_ax && redstone) {
                        return false;
                     }

                     if (stack.func_77973_b() == Items.field_151137_ax && !redstone) {
                        redstone = true;
                     } else {
                        if (stack.func_77973_b() != ItemsTC.crystalEssence) {
                           return false;
                        }

                        ItemCrystalEssence ice = (ItemCrystalEssence)stack.func_77973_b();
                        if (crystals.contains(ice.getAspects(stack).getAspects()[0].getTag()) || crystals.size() >= 3) {
                           return false;
                        }

                        crystals.add(ice.getAspects(stack).getAspects()[0].getTag());
                     }
                  }
               }
            }
         }
      }

      return bowl && redstone && flint && crystals.size() == 3;
   }

   public ItemStack func_77572_b(InventoryCrafting inv) {
      return new ItemStack(ItemsTC.salisMundus);
   }

   public boolean func_194133_a(int width, int height) {
      return width * height >= 6;
   }

   public ItemStack func_77571_b() {
      return new ItemStack(ItemsTC.salisMundus);
   }

   public NonNullList<ItemStack> func_179532_b(InventoryCrafting inv) {
      NonNullList<ItemStack> ret = NonNullList.func_191197_a(inv.func_70302_i_(), ItemStack.field_190927_a);

      for (int i = 0; i < ret.size(); i++) {
         ItemStack itemstack = inv.func_70301_a(i);
         ItemStack itemstack2 = ForgeHooks.getContainerItem(itemstack);
         if (itemstack != null
            && !itemstack.func_190926_b()
            && (itemstack.func_77973_b() == Items.field_151145_ak || itemstack.func_77973_b() == Items.field_151054_z)) {
            ItemStack is = itemstack.func_77946_l();
            is.func_190920_e(1);
            itemstack2 = is;
         }

         ret.set(i, itemstack2);
      }

      return ret;
   }
}
