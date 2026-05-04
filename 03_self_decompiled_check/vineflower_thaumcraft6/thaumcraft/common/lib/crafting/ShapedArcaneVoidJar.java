package thaumcraft.common.lib.crafting;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ShapedArcaneRecipe;

public class ShapedArcaneVoidJar extends ShapedArcaneRecipe {
   public ShapedArcaneVoidJar(ResourceLocation group, String res, int vis, AspectList crystals, ItemStack result, Object... recipe) {
      super(group, res, vis, crystals, result, recipe);
   }

   @Override
   public ItemStack func_77572_b(InventoryCrafting var1) {
      NBTTagCompound nbt = null;

      for (int a = 0; a < var1.func_70302_i_(); a++) {
         if (Block.func_149634_a(var1.func_70301_a(a).func_77973_b()) == BlocksTC.jarNormal) {
            nbt = var1.func_70301_a(a).func_77978_p();
            break;
         }
      }

      ItemStack res = super.func_77572_b(var1);
      res.func_77982_d(nbt);
      return res;
   }
}
