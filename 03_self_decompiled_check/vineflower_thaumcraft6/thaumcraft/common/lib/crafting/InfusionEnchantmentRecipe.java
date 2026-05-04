package thaumcraft.common.lib.crafting;

import baubles.api.IBauble;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.world.World;
import net.minecraftforge.common.util.RecipeMatcher;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;

public class InfusionEnchantmentRecipe extends InfusionRecipe {
   EnumInfusionEnchantment enchantment;

   public InfusionEnchantmentRecipe(EnumInfusionEnchantment ench, AspectList as, Object... components) {
      super(ench.research, null, 4, as, Ingredient.field_193370_a, components);
      this.enchantment = ench;
   }

   public InfusionEnchantmentRecipe(InfusionEnchantmentRecipe recipe, ItemStack in) {
      super(recipe.enchantment.research, null, recipe.instability, recipe.aspects, in, recipe.components.toArray());
      this.enchantment = recipe.enchantment;
   }

   @Override
   public boolean matches(List<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
      if (central == null || central.func_190926_b() || !ThaumcraftCapabilities.knowsResearch(player, this.research)) {
         return false;
      }

      if (EnumInfusionEnchantment.getInfusionEnchantmentLevel(central, this.enchantment) >= this.enchantment.maxLevel) {
         return false;
      }

      if (!this.enchantment.toolClasses.contains("all")) {
         Multimap<String, AttributeModifier> itemMods = central.func_111283_C(EntityEquipmentSlot.MAINHAND);
         boolean cool = false;
         if (itemMods != null
            && itemMods.containsKey(SharedMonsterAttributes.field_111264_e.func_111108_a())
            && this.enchantment.toolClasses.contains("weapon")) {
            cool = true;
         }

         if (!cool && central.func_77973_b() instanceof ItemTool) {
            for (String tc : ((ItemTool)central.func_77973_b()).getToolClasses(central)) {
               if (this.enchantment.toolClasses.contains(tc)) {
                  cool = true;
                  break;
               }
            }
         }

         if (!cool && central.func_77973_b() instanceof ItemArmor) {
            String at = "none";
            switch (((ItemArmor)central.func_77973_b()).field_77881_a) {
               case HEAD:
                  at = "helm";
                  break;
               case CHEST:
                  at = "chest";
                  break;
               case LEGS:
                  at = "legs";
                  break;
               case FEET:
                  at = "boots";
            }

            if (this.enchantment.toolClasses.contains("armor") || this.enchantment.toolClasses.contains(at)) {
               cool = true;
            }
         }

         if (!cool && central.func_77973_b() instanceof IBauble) {
            String at = "none";
            switch (((IBauble)central.func_77973_b()).getBaubleType(central)) {
               case AMULET:
                  at = "amulet";
                  break;
               case BELT:
                  at = "belt";
                  break;
               case RING:
                  at = "ring";
            }

            if (this.enchantment.toolClasses.contains("bauble") || this.enchantment.toolClasses.contains(at)) {
               cool = true;
            }
         }

         if (!cool && central.func_77973_b() instanceof IRechargable && this.enchantment.toolClasses.contains("chargable")) {
            cool = true;
         }

         if (!cool) {
            return false;
         }
      }

      return (this.getRecipeInput() == Ingredient.field_193370_a || this.getRecipeInput().apply(central))
         && RecipeMatcher.findMatches(input, this.getComponents()) != null;
   }

   @Override
   public Object getRecipeOutput(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
      if (input == null) {
         return null;
      }

      ItemStack out = input.func_77946_l();
      int cl = EnumInfusionEnchantment.getInfusionEnchantmentLevel(out, this.enchantment);
      if (cl >= this.enchantment.maxLevel) {
         return null;
      }

      List<EnumInfusionEnchantment> el = EnumInfusionEnchantment.getInfusionEnchantments(input);
      Random rand = new Random(System.nanoTime());
      if (rand.nextInt(10) < el.size()) {
         int base = 1;
         if (input.func_77942_o()) {
            base += input.func_77978_p().func_74771_c("TC.WARP");
         }

         out.func_77983_a("TC.WARP", new NBTTagByte((byte)base));
      }

      EnumInfusionEnchantment.addInfusionEnchantment(out, this.enchantment, cl + 1);
      return out;
   }

   @Override
   public AspectList getAspects(EntityPlayer player, ItemStack input, List<ItemStack> comps) {
      AspectList out = new AspectList();
      if (input != null && !input.func_190926_b()) {
         int cl = EnumInfusionEnchantment.getInfusionEnchantmentLevel(input, this.enchantment) + 1;
         if (cl > this.enchantment.maxLevel) {
            return out;
         }

         List<EnumInfusionEnchantment> el = EnumInfusionEnchantment.getInfusionEnchantments(input);
         int otherEnchantments = el.size();
         if (el.contains(this.enchantment)) {
            otherEnchantments--;
         }

         float modifier = cl + otherEnchantments * 0.33F;

         for (Aspect a : this.getAspects().getAspects()) {
            out.add(a, (int)(this.getAspects().getAmount(a) * modifier));
         }

         return out;
      } else {
         return out;
      }
   }
}
