package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import thaumcraft.api.items.ItemsTC;

public class RecipeTripleMeatTreat extends Impl<IRecipe> implements IRecipe {
   public boolean func_77569_a(InventoryCrafting inv, World worldIn) {
      boolean sugar = false;
      ArrayList<Integer> meats = new ArrayList<>();

      for (int a = 0; a < 3; a++) {
         for (int b = 0; b < 3; b++) {
            if (inv.func_70463_b(a, b) != null && !inv.func_70463_b(a, b).func_190926_b()) {
               ItemStack stack = inv.func_70463_b(a, b).func_77946_l();
               if (stack.func_77973_b() == Items.field_151102_aT && sugar) {
                  return false;
               }

               if (stack.func_77973_b() == Items.field_151102_aT && !sugar) {
                  sugar = true;
               } else {
                  if (stack.func_77973_b() != ItemsTC.chunks) {
                     return false;
                  }

                  if (meats.contains(stack.func_77952_i()) || meats.size() >= 3) {
                     return false;
                  }

                  meats.add(stack.func_77952_i());
               }
            }
         }
      }

      return sugar && meats.size() == 3;
   }

   public ItemStack func_77572_b(InventoryCrafting inv) {
      return new ItemStack(ItemsTC.tripleMeatTreat);
   }

   public ItemStack func_77571_b() {
      return new ItemStack(ItemsTC.tripleMeatTreat);
   }

   public boolean func_194133_a(int width, int height) {
      return width * height >= 4;
   }
}
