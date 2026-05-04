package thaumcraft.api.crafting;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;

public class ShapedArcaneRecipe extends ShapedOreRecipe implements IArcaneRecipe {
   private String research;
   private int vis;
   private AspectList crystals;

   public ShapedArcaneRecipe(ResourceLocation group, String res, int vis, AspectList crystals, Block result, Object... recipe) {
      this(group, res, vis, crystals, new ItemStack(result), recipe);
   }

   public ShapedArcaneRecipe(ResourceLocation group, String res, int vis, AspectList crystals, Item result, Object... recipe) {
      this(group, res, vis, crystals, new ItemStack(result), recipe);
   }

   public ShapedArcaneRecipe(ResourceLocation group, String res, int vis, AspectList crystals, @Nonnull ItemStack result, Object... recipe) {
      this(group, res, vis, crystals, result, CraftingHelper.parseShaped(recipe));
   }

   public ShapedArcaneRecipe(ResourceLocation group, String res, int vis, AspectList crystals, @Nonnull ItemStack result, ShapedPrimer primer) {
      super(group, result, primer);
      this.research = res;
      this.vis = vis;
      this.crystals = crystals;
   }

   public ItemStack func_77572_b(InventoryCrafting var1) {
      return !(var1 instanceof IArcaneWorkbench) ? ItemStack.field_190927_a : super.func_77572_b(var1);
   }

   public boolean func_77569_a(InventoryCrafting inv, World world) {
      if (inv.func_70302_i_() < 15) {
         return false;
      }

      InventoryCrafting dummy = new InventoryCrafting(new ContainerDummy(), 3, 3);

      for (int a = 0; a < 9; a++) {
         dummy.func_70299_a(a, inv.func_70301_a(a));
      }

      if (this.crystals != null && inv.func_70302_i_() >= 15) {
         for (Aspect aspect : this.crystals.getAspects()) {
            ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect, this.crystals.getAmount(aspect));
            boolean b = false;

            for (int i = 0; i < 6; i++) {
               ItemStack itemstack1 = inv.func_70301_a(9 + i);
               if (itemstack1 != null
                  && itemstack1.func_77973_b() == ItemsTC.crystalEssence
                  && itemstack1.func_190916_E() >= cs.func_190916_E()
                  && ItemStack.func_77970_a(cs, itemstack1)) {
                  b = true;
               }
            }

            if (!b) {
               return false;
            }
         }
      }

      return inv instanceof IArcaneWorkbench && super.func_77569_a(dummy, world);
   }

   @Override
   public int getVis() {
      return this.vis;
   }

   @Override
   public String getResearch() {
      return this.research;
   }

   @Override
   public AspectList getCrystals() {
      return this.crystals;
   }
}
