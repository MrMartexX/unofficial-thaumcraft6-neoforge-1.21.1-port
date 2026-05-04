package thaumcraft.api.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class CrucibleRecipe implements IThaumcraftRecipe {
   private ItemStack recipeOutput;
   private Ingredient catalyst;
   private AspectList aspects;
   private String research;
   private String name;
   public int hash;
   private String group = "";

   public CrucibleRecipe(String researchKey, ItemStack result, Object catalyst, AspectList tags) {
      this.recipeOutput = result;
      this.name = "";
      this.setAspects(tags);
      this.research = researchKey;
      this.setCatalyst(ThaumcraftApiHelper.getIngredient(catalyst));
      if (this.getCatalyst() == null) {
         throw new RuntimeException("Invalid crucible recipe catalyst: " + catalyst);
      }

      this.generateHash();
   }

   private void generateHash() {
      String hc = this.research;
      hc = hc + this.recipeOutput.toString();
      if (this.recipeOutput.func_77942_o()) {
         hc = hc + this.recipeOutput.func_77978_p().toString();
      }

      for (ItemStack is : this.getCatalyst().func_193365_a()) {
         hc = hc + is.toString();
         if (is.func_77942_o()) {
            hc = hc + is.func_77978_p().toString();
         }
      }

      this.hash = hc.hashCode();
   }

   public boolean matches(AspectList itags, ItemStack cat) {
      if (!this.getCatalyst().apply(cat)) {
         return false;
      }

      if (itags == null) {
         return false;
      }

      for (Aspect tag : this.getAspects().getAspects()) {
         if (itags.getAmount(tag) < this.getAspects().getAmount(tag)) {
            return false;
         }
      }

      return true;
   }

   public boolean catalystMatches(ItemStack cat) {
      return this.getCatalyst().apply(cat);
   }

   public AspectList removeMatching(AspectList itags) {
      AspectList temptags = new AspectList();
      temptags.aspects.putAll(itags.aspects);

      for (Aspect tag : this.getAspects().getAspects()) {
         temptags.remove(tag, this.getAspects().getAmount(tag));
      }

      return temptags;
   }

   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
   }

   @Override
   public String getResearch() {
      return this.research;
   }

   public Ingredient getCatalyst() {
      return this.catalyst;
   }

   public void setCatalyst(Ingredient catalyst) {
      this.catalyst = catalyst;
   }

   public AspectList getAspects() {
      return this.aspects;
   }

   public void setAspects(AspectList aspects) {
      this.aspects = aspects;
   }

   @Override
   public String getGroup() {
      return this.group;
   }

   public CrucibleRecipe setGroup(ResourceLocation s) {
      this.group = s.toString();
      return this;
   }
}
