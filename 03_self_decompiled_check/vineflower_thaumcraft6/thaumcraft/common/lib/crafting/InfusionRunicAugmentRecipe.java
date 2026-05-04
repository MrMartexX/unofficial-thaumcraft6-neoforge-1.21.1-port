package thaumcraft.common.lib.crafting;

import baubles.api.IBauble;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.RecipeMatcher;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.events.PlayerEvents;

public class InfusionRunicAugmentRecipe extends InfusionRecipe {
   public InfusionRunicAugmentRecipe() {
      super("RUNICSHIELDING", null, 0, null, Ingredient.field_193370_a, "gemAmber", ItemsTC.salisMundus);
   }

   public InfusionRunicAugmentRecipe(ItemStack in) {
      super("RUNICSHIELDING", null, 0, null, in, new ItemStack(ItemsTC.salisMundus), "gemAmber");
      int fc = PlayerEvents.getRunicCharge(in);
      if (fc > 0) {
         this.components.clear();
         new ArrayList();
         this.components.add(Ingredient.func_193367_a(ItemsTC.salisMundus));
         this.components.add(ThaumcraftApiHelper.getIngredient("gemAmber"));
         int c = 0;

         while (c < fc) {
            c++;
            this.components.add(ThaumcraftApiHelper.getIngredient("gemAmber"));
         }
      }
   }

   @Override
   public boolean matches(List<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
      if (this.getRecipeInput() == null) {
         return false;
      } else if (!ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(this.research)) {
         return false;
      } else {
         return !(central.func_77973_b() instanceof ItemArmor) && !(central.func_77973_b() instanceof IBauble)
            ? false
            : (this.getRecipeInput() == Ingredient.field_193370_a || this.getRecipeInput().apply(central))
               && RecipeMatcher.findMatches(input, this.getComponents(central)) != null;
      }
   }

   @Override
   public Object getRecipeOutput(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
      if (input == null) {
         return null;
      }

      ItemStack out = input.func_77946_l();
      int base = PlayerEvents.getRunicCharge(input) + 1;
      out.func_77983_a("TC.RUNIC", new NBTTagByte((byte)base));
      return out;
   }

   @Override
   public AspectList getAspects(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
      AspectList out = new AspectList();
      int vis = 20 + (int)(20.0 * Math.pow(2.0, PlayerEvents.getRunicCharge(input)));
      if (vis > 0) {
         out.add(Aspect.PROTECT, vis);
         out.add(Aspect.CRYSTAL, vis / 2);
         out.add(Aspect.ENERGY, vis / 2);
      }

      return out;
   }

   @Override
   public int getInstability(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
      return 5 + PlayerEvents.getRunicCharge(input) / 2;
   }

   public NonNullList<Ingredient> getComponents(ItemStack input) {
      NonNullList<Ingredient> com = NonNullList.func_191196_a();
      com.add(Ingredient.func_193367_a(ItemsTC.salisMundus));
      com.add(ThaumcraftApiHelper.getIngredient("gemAmber"));
      int fc = PlayerEvents.getRunicCharge(input);
      if (fc > 0) {
         for (int c = 0; c < fc; c++) {
            com.add(ThaumcraftApiHelper.getIngredient("gemAmber"));
         }
      }

      return com;
   }
}
