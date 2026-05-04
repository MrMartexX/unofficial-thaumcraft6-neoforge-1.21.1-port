package thaumcraft.api.crafting;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.RecipeMatcher;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class InfusionRecipe implements IThaumcraftRecipe {
   public AspectList aspects;
   public String research;
   private String name;
   protected NonNullList<Ingredient> components = NonNullList.func_191196_a();
   public Ingredient sourceInput;
   public Object recipeOutput;
   public int instability;
   private String group = "";

   public InfusionRecipe(String research, Object outputResult, int inst, AspectList aspects2, Object centralItem, Object... recipe) {
      this.name = "";
      this.research = research;
      this.recipeOutput = outputResult;
      this.aspects = aspects2;
      this.instability = inst;
      this.sourceInput = ThaumcraftApiHelper.getIngredient(centralItem);
      if (this.sourceInput == null) {
         String ret = "Invalid infusion central item: " + centralItem;
         throw new RuntimeException(ret);
      }

      for (Object in : recipe) {
         Ingredient ing = ThaumcraftApiHelper.getIngredient(in);
         if (ing == null) {
            String ret = "Invalid infusion recipe: ";

            for (Object tmp : recipe) {
               ret = ret + tmp + ", ";
            }

            ret = ret + outputResult;
            throw new RuntimeException(ret);
         }

         this.components.add(ing);
      }
   }

   public boolean matches(List<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
      if (this.getRecipeInput() == null) {
         return false;
      } else {
         return !ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(this.research)
            ? false
            : (this.getRecipeInput() == Ingredient.field_193370_a || this.getRecipeInput().apply(central))
               && RecipeMatcher.findMatches(input, this.getComponents()) != null;
      }
   }

   @Override
   public String getResearch() {
      return this.research;
   }

   public Ingredient getRecipeInput() {
      return this.sourceInput;
   }

   public NonNullList<Ingredient> getComponents() {
      return this.components;
   }

   public Object getRecipeOutput() {
      return this.recipeOutput;
   }

   public AspectList getAspects() {
      return this.aspects;
   }

   public Object getRecipeOutput(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
      return this.recipeOutput;
   }

   public AspectList getAspects(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
      return this.aspects;
   }

   public int getInstability(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
      return this.instability;
   }

   @Override
   public String getGroup() {
      return this.group;
   }

   public InfusionRecipe setGroup(ResourceLocation s) {
      this.group = s.toString();
      return this;
   }
}
