package thaumcraft.common.lib.crafting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IRegistryDelegate;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.internal.CommonInternals;

public class ThaumcraftCraftingManager {
   static final int ASPECTCAP = 500;

   public static CrucibleRecipe findMatchingCrucibleRecipe(EntityPlayer player, AspectList aspects, ItemStack lastDrop) {
      int highest = 0;
      CrucibleRecipe out = null;

      for (Object re : ThaumcraftApi.getCraftingRecipes().values()) {
         if (re != null && re instanceof CrucibleRecipe) {
            CrucibleRecipe recipe = (CrucibleRecipe)re;
            ItemStack temp = lastDrop.func_77946_l();
            temp.func_190920_e(1);
            if (player != null && ThaumcraftCapabilities.knowsResearchStrict(player, recipe.getResearch()) && recipe.matches(aspects, temp)) {
               int result = recipe.getAspects().visSize();
               if (result > highest) {
                  highest = result;
                  out = recipe;
               }
            }
         }
      }

      return out;
   }

   public static IArcaneRecipe findMatchingArcaneRecipe(InventoryCrafting matrix, EntityPlayer player) {
      int var2 = 0;
      ItemStack var3 = null;
      ItemStack var4 = null;

      for (int var5 = 0; var5 < 15; var5++) {
         ItemStack var6 = matrix.func_70301_a(var5);
         if (!var6.func_190926_b()) {
            if (var2 == 0) {
               ;
            }

            if (var2 == 1) {
               ;
            }

            var2++;
         }
      }

      for (ResourceLocation key : CraftingManager.field_193380_a.func_148742_b()) {
         IRecipe recipe = (IRecipe)CraftingManager.field_193380_a.func_82594_a(key);
         if (recipe != null && recipe instanceof IArcaneRecipe && recipe.func_77569_a(matrix, player.field_70170_p)) {
            return (IArcaneRecipe)recipe;
         }
      }

      return null;
   }

   public static ItemStack findMatchingArcaneRecipeResult(InventoryCrafting awb, EntityPlayer player) {
      IArcaneRecipe var13 = findMatchingArcaneRecipe(awb, player);
      return var13 == null ? null : var13.func_77572_b(awb);
   }

   public static AspectList findMatchingArcaneRecipeCrystals(InventoryCrafting awb, EntityPlayer player) {
      IArcaneRecipe var13 = findMatchingArcaneRecipe(awb, player);
      return var13 == null ? null : var13.getCrystals();
   }

   public static int findMatchingArcaneRecipeVis(InventoryCrafting awb, EntityPlayer player) {
      IArcaneRecipe var13 = findMatchingArcaneRecipe(awb, player);
      return var13 == null ? 0 : (var13.getVis() > 0 ? var13.getVis() : var13.getVis());
   }

   public static InfusionRecipe findMatchingInfusionRecipe(ArrayList<ItemStack> items, ItemStack input, EntityPlayer player) {
      for (Object recipe : ThaumcraftApi.getCraftingRecipes().values()) {
         if (recipe != null && recipe instanceof InfusionRecipe && ((InfusionRecipe)recipe).matches(items, input, player.field_70170_p, player)) {
            return (InfusionRecipe)recipe;
         }
      }

      return null;
   }

   public static AspectList getObjectTags(ItemStack itemstack) {
      return getObjectTags(itemstack, null);
   }

   public static AspectList getObjectTags(ItemStack itemstack, ArrayList<String> history) {
      if (itemstack.func_190926_b()) {
         return null;
      }

      int ss = CommonInternals.generateUniqueItemstackId(itemstack);
      AspectList tmp = CommonInternals.objectTags.get(ss);
      if (tmp == null) {
         try {
            ItemStack sc = itemstack.func_77946_l();
            sc.func_77964_b(32767);
            ss = CommonInternals.generateUniqueItemstackId(sc);
            tmp = CommonInternals.objectTags.get(ss);
            if (tmp == null) {
               if (itemstack.func_77952_i() == 32767) {
                  int index = 0;

                  do {
                     sc.func_77964_b(index);
                     ss = CommonInternals.generateUniqueItemstackId(sc);
                     tmp = CommonInternals.objectTags.get(ss);
                  } while (++index < 16 && tmp == null);
               }

               if (tmp == null) {
                  sc = itemstack.func_77946_l();
                  ss = CommonInternals.generateUniqueItemstackIdStripped(sc);
                  tmp = CommonInternals.objectTags.get(ss);
                  if (tmp == null) {
                     sc = itemstack.func_77946_l();
                     sc.func_77964_b(32767);
                     ss = CommonInternals.generateUniqueItemstackIdStripped(sc);
                     tmp = CommonInternals.objectTags.get(ss);
                  }
               }

               if (tmp == null) {
                  tmp = generateTags(itemstack, history);
               }
            }
         } catch (Exception var6) {
         }
      }

      return capAspects(getBonusTags(itemstack, tmp), 500);
   }

   private static AspectList capAspects(AspectList sourcetags, int amount) {
      if (sourcetags == null) {
         return sourcetags;
      }

      AspectList out = new AspectList();

      for (Aspect aspect : sourcetags.getAspects()) {
         if (aspect != null) {
            out.merge(aspect, Math.min(amount, sourcetags.getAmount(aspect)));
         }
      }

      return out;
   }

   private static AspectList getBonusTags(ItemStack itemstack, AspectList sourcetags) {
      AspectList tmp = new AspectList();
      if (itemstack.func_190926_b()) {
         return tmp;
      }

      Item item = itemstack.func_77973_b();
      if (item != null && item instanceof IEssentiaContainerItem && !((IEssentiaContainerItem)item).ignoreContainedAspects()) {
         if (sourcetags != null) {
            sourcetags.aspects.clear();
         }

         tmp = ((IEssentiaContainerItem)item).getAspects(itemstack);
         if (tmp != null && tmp.size() > 0) {
            for (Aspect tag : tmp.copy().getAspects()) {
               if (tmp.getAmount(tag) <= 0) {
                  tmp.remove(tag);
               }
            }
         }
      }

      if (tmp == null) {
         tmp = new AspectList();
      }

      if (sourcetags != null) {
         for (Aspect tag : sourcetags.getAspects()) {
            if (tag != null) {
               tmp.add(tag, sourcetags.getAmount(tag));
            }
         }
      }

      if (item != null && (tmp != null || item == Items.field_151068_bn)) {
         if (item instanceof ItemArmor) {
            tmp.merge(Aspect.PROTECT, ((ItemArmor)item).field_77879_b * 4);
         } else if (item instanceof ItemSword && ((ItemSword)item).func_150931_i() + 1.0F > 0.0F) {
            tmp.merge(Aspect.AVERSION, (int)(((ItemSword)item).func_150931_i() + 1.0F) * 4);
         } else if (item instanceof ItemBow) {
            tmp.merge(Aspect.AVERSION, 10).merge(Aspect.FLIGHT, 5);
         } else if (item instanceof ItemTool) {
            String mat = ((ItemTool)item).func_77861_e();

            for (ToolMaterial tm : ToolMaterial.values()) {
               if (tm.toString().equals(mat)) {
                  tmp.merge(Aspect.TOOL, (tm.func_77996_d() + 1) * 4);
               }
            }
         } else if (item instanceof ItemShears || item instanceof ItemHoe) {
            if (item.func_77612_l() <= ToolMaterial.WOOD.func_77997_a()) {
               tmp.merge(Aspect.TOOL, 4);
            } else if (item.func_77612_l() <= ToolMaterial.STONE.func_77997_a() || item.func_77612_l() <= ToolMaterial.GOLD.func_77997_a()) {
               tmp.merge(Aspect.TOOL, 8);
            } else if (item.func_77612_l() <= ToolMaterial.IRON.func_77997_a()) {
               tmp.merge(Aspect.TOOL, 12);
            } else {
               tmp.merge(Aspect.TOOL, 16);
            }
         }

         String[] dyes = new String[]{
            "dyeBlack",
            "dyeRed",
            "dyeGreen",
            "dyeBrown",
            "dyeBlue",
            "dyePurple",
            "dyeCyan",
            "dyeLightGray",
            "dyeGray",
            "dyePink",
            "dyeLime",
            "dyeYellow",
            "dyeLightBlue",
            "dyeMagenta",
            "dyeOrange",
            "dyeWhite"
         };
         int[] ores = OreDictionary.getOreIDs(itemstack);
         if (ores != null && ores.length > 0) {
            Arrays.sort(dyes);

            for (int od : ores) {
               String s = OreDictionary.getOreName(od);
               if (s != null && Arrays.binarySearch(dyes, s) >= 0) {
                  tmp.merge(Aspect.SENSES, 5);
                  break;
               }
            }
         }

         NBTTagList ench = itemstack.func_77986_q();
         if (item instanceof ItemEnchantedBook) {
            ench = ItemEnchantedBook.func_92110_g(itemstack);
         }

         if (ench != null) {
            int var5 = 0;

            for (int var3 = 0; var3 < ench.func_74745_c(); var3++) {
               short eid = ench.func_150305_b(var3).func_74765_d("id");
               short lvl = (short)(ench.func_150305_b(var3).func_74765_d("lvl") * 3);
               Enchantment e = Enchantment.func_185262_c(eid);
               if (e != null) {
                  if (e == Enchantments.field_185299_g) {
                     tmp.merge(Aspect.WATER, lvl);
                  } else if (e == Enchantments.field_180312_n) {
                     tmp.merge(Aspect.BEAST, lvl / 2).merge(Aspect.AVERSION, lvl / 2);
                  } else if (e == Enchantments.field_185297_d) {
                     tmp.merge(Aspect.PROTECT, lvl / 2).merge(Aspect.ENTROPY, lvl / 2);
                  } else if (e == Enchantments.field_185305_q) {
                     tmp.merge(Aspect.TOOL, lvl);
                  } else if (e == Enchantments.field_180309_e) {
                     tmp.merge(Aspect.FLIGHT, lvl);
                  } else if (e == Enchantments.field_77334_n) {
                     tmp.merge(Aspect.FIRE, lvl / 2).merge(Aspect.AVERSION, lvl / 2);
                  } else if (e == Enchantments.field_77329_d) {
                     tmp.merge(Aspect.PROTECT, lvl / 2).merge(Aspect.FIRE, lvl / 2);
                  } else if (e == Enchantments.field_185311_w) {
                     tmp.merge(Aspect.FIRE, lvl);
                  } else if (e == Enchantments.field_185308_t) {
                     tmp.merge(Aspect.DESIRE, lvl);
                  } else if (e == Enchantments.field_185312_x) {
                     tmp.merge(Aspect.CRAFT, lvl);
                  } else if (e == Enchantments.field_180313_o) {
                     tmp.merge(Aspect.AIR, lvl);
                  } else if (e == Enchantments.field_185304_p) {
                     tmp.merge(Aspect.DESIRE, lvl);
                  } else if (e == Enchantments.field_185309_u) {
                     tmp.merge(Aspect.AVERSION, lvl);
                  } else if (e == Enchantments.field_180308_g) {
                     tmp.merge(Aspect.PROTECT, lvl);
                  } else if (e == Enchantments.field_180310_c) {
                     tmp.merge(Aspect.PROTECT, lvl);
                  } else if (e == Enchantments.field_185310_v) {
                     tmp.merge(Aspect.AIR, lvl);
                  } else if (e == Enchantments.field_185298_f) {
                     tmp.merge(Aspect.AIR, lvl);
                  } else if (e == Enchantments.field_185302_k) {
                     tmp.merge(Aspect.AVERSION, lvl);
                  } else if (e == Enchantments.field_185306_r) {
                     tmp.merge(Aspect.EXCHANGE, lvl);
                  } else if (e == Enchantments.field_92091_k) {
                     tmp.merge(Aspect.AVERSION, lvl);
                  } else if (e == Enchantments.field_185303_l) {
                     tmp.merge(Aspect.UNDEAD, lvl / 2).merge(Aspect.AVERSION, lvl / 2);
                  } else if (e == Enchantments.field_185307_s) {
                     tmp.merge(Aspect.EARTH, lvl);
                  } else if (e == Enchantments.field_185300_i) {
                     tmp.merge(Aspect.WATER, lvl);
                  } else if (e == Enchantments.field_151370_z) {
                     tmp.merge(Aspect.DESIRE, lvl);
                  } else if (e == Enchantments.field_151369_A) {
                     tmp.merge(Aspect.BEAST, lvl);
                  } else if (e == Enchantments.field_185301_j) {
                     tmp.merge(Aspect.COLD, lvl);
                  } else if (e == Enchantments.field_185296_A) {
                     tmp.merge(Aspect.CRAFT, lvl);
                  }

                  if (e.func_77324_c() == Rarity.UNCOMMON) {
                     var5 += 2;
                  }

                  if (e.func_77324_c() == Rarity.RARE) {
                     var5 += 4;
                  }

                  if (e.func_77324_c() == Rarity.VERY_RARE) {
                     var5 += 6;
                  }
               }

               var5 += lvl;
            }

            if (var5 > 0) {
               tmp.merge(Aspect.MAGIC, var5);
            }
         }
      }

      return AspectHelper.cullTags(tmp);
   }

   public static void getPotionReagentsRecursive(PotionType potion, HashSet<ItemStack> hashSet) {
      try {
         String mixPredicateName = "net.minecraft.potion.PotionHelper$MixPredicate";
         Class<?> mixPredicateClass = Class.forName(mixPredicateName);
         Field output = ReflectionHelper.findField(
            mixPredicateClass, ObfuscationReflectionHelper.remapFieldNames(mixPredicateName, new String[]{"field_185200_c"})
         );
         Field reagent = ReflectionHelper.findField(
            mixPredicateClass, ObfuscationReflectionHelper.remapFieldNames(mixPredicateName, new String[]{"field_185199_b"})
         );
         Field input = ReflectionHelper.findField(
            mixPredicateClass, ObfuscationReflectionHelper.remapFieldNames(mixPredicateName, new String[]{"field_185198_a"})
         );
         output.setAccessible(true);
         reagent.setAccessible(true);
         input.setAccessible(true);

         for (Object mixpre : PotionHelper.field_185213_a) {
            if (((IRegistryDelegate)output.get(mixpre)).get() instanceof PotionType
               && ((PotionType)((IRegistryDelegate)output.get(mixpre)).get()).getRegistryName() == potion.getRegistryName()) {
               try {
                  hashSet.add(((Ingredient)reagent.get(mixpre)).func_193365_a()[0]);
                  if (((IRegistryDelegate)input.get(mixpre)).get() != PotionTypes.field_185230_b
                     && ((IRegistryDelegate)input.get(mixpre)).get() instanceof PotionType) {
                     getPotionReagentsRecursive((PotionType)((IRegistryDelegate)input.get(mixpre)).get(), hashSet);
                  }
                  break;
               } catch (Exception var10) {
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static AspectList generateTags(ItemStack is) {
      return generateTags(is, new ArrayList<>());
   }

   public static AspectList generateTags(ItemStack is, ArrayList<String> history) {
      if (history == null) {
         history = new ArrayList<>();
      }

      ItemStack stack = is.func_77946_l();
      stack.func_190920_e(1);

      try {
         if (stack.func_77973_b().func_77645_m() || !stack.func_77973_b().func_77614_k()) {
            stack.func_77964_b(32767);
         }
      } catch (Exception var5) {
      }

      if (ThaumcraftApi.exists(stack)) {
         return getObjectTags(stack, history);
      }

      String ss = stack.serializeNBT().toString();
      if (history.contains(ss)) {
         return null;
      }

      history.add(ss);
      if (history.size() < 100) {
         if (stack.func_77952_i() == 32767) {
            stack.func_77964_b(0);
         }

         AspectList ret = generateTagsFromRecipes(stack, history);
         ret = capAspects(ret, 500);
         ThaumcraftApi.registerObjectTag(is, ret);
         return ret;
      } else {
         return null;
      }
   }

   private static AspectList generateTagsFromCrucibleRecipes(ItemStack stack, ArrayList<String> history) {
      CrucibleRecipe cr = ThaumcraftApi.getCrucibleRecipe(stack);
      if (cr == null) {
         return null;
      }

      AspectList ot = cr.getAspects().copy();
      int ss = cr.getRecipeOutput().func_190916_E();
      ItemStack cat = cr.getCatalyst().func_193365_a()[0];
      if (cat != null && !cat.func_190926_b()) {
         AspectList ot2 = getObjectTags(cat, history);
         AspectList out = new AspectList();
         if (ot2 != null && ot2.size() > 0) {
            for (Aspect tt : ot2.getAspects()) {
               out.add(tt, ot2.getAmount(tt));
            }
         }

         for (Aspect tt : ot.getAspects()) {
            int amt = (int)(Math.sqrt(ot.getAmount(tt)) / ss);
            out.add(tt, amt);
         }

         for (Aspect as : out.getAspects()) {
            if (out.getAmount(as) <= 0) {
               out.remove(as);
            }
         }

         return out;
      } else {
         return null;
      }
   }

   private static AspectList generateTagsFromInfusionRecipes(ItemStack stack, ArrayList<String> history) {
      InfusionRecipe cr = ThaumcraftApi.getInfusionRecipe(stack);
      if (cr == null) {
         return null;
      }

      AspectList ot = cr.getAspects().copy();
      NonNullList<Ingredient> ingredients = NonNullList.func_191196_a();
      ingredients.add(cr.getRecipeInput());
      ingredients.addAll(cr.getComponents());
      AspectList out = new AspectList();
      AspectList ot2 = getAspectsFromIngredients(ingredients, (ItemStack)cr.getRecipeOutput(), null, history);

      for (Aspect tt : ot2.getAspects()) {
         out.add(tt, ot2.getAmount(tt));
      }

      for (Aspect tt : ot.getAspects()) {
         int amt = (int)(Math.sqrt(ot.getAmount(tt)) / ((ItemStack)cr.getRecipeOutput()).func_190916_E());
         out.add(tt, amt);
      }

      for (Aspect as : out.getAspects()) {
         if (out.getAmount(as) <= 0) {
            out.remove(as);
         }
      }

      return out;
   }

   private static AspectList generateTagsFromCraftingRecipes(ItemStack stack, ArrayList<String> history) {
      AspectList ret = null;
      int value = Integer.MAX_VALUE;

      for (ResourceLocation key : CraftingManager.field_193380_a.func_148742_b()) {
         IRecipe recipe = (IRecipe)CraftingManager.field_193380_a.func_82594_a(key);
         if (recipe != null
            && recipe.func_77571_b() != null
            && Item.func_150891_b(recipe.func_77571_b().func_77973_b()) > 0
            && recipe.func_77571_b().func_77973_b() != null) {
            int idR = recipe.func_77571_b().func_77952_i() == 32767 ? 0 : recipe.func_77571_b().func_77952_i();
            int idS = stack.func_77952_i() == 32767 ? 0 : stack.func_77952_i();
            if (recipe.func_77571_b().func_77973_b() == stack.func_77973_b() && idR == idS) {
               new ArrayList();
               new AspectList();
               int cval = 0;

               try {
                  AspectList ph = getAspectsFromIngredients(recipe.func_192400_c(), recipe.func_77571_b(), recipe, history);
                  if (recipe instanceof IArcaneRecipe) {
                     IArcaneRecipe ar = (IArcaneRecipe)recipe;
                     if (ar.getVis() > 0) {
                        ph.add(Aspect.MAGIC, (int)(Math.sqrt(1 + ar.getVis() / 2) / recipe.func_77571_b().func_190916_E()));
                     }
                  }

                  for (Aspect as : ph.copy().getAspects()) {
                     if (ph.getAmount(as) <= 0) {
                        ph.remove(as);
                     }
                  }

                  if (ph.visSize() < value && ph.visSize() > 0) {
                     ret = ph;
                     value = ph.visSize();
                  }
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         }
      }

      return ret;
   }

   private static AspectList getAspectsFromIngredients(NonNullList<Ingredient> nonNullList, ItemStack recipeOut, IRecipe recipe, ArrayList<String> history) {
      AspectList out = new AspectList();
      AspectList mid = new AspectList();
      NonNullList<ItemStack> exlist = NonNullList.func_191196_a();
      if (recipe != null) {
         InventoryCrafting inv = new InventoryCrafting(new ContainerFake(), 3, 3);
         int index = 0;

         for (Ingredient is : nonNullList) {
            if (is.func_193365_a().length > 0) {
               inv.func_70299_a(index, is.func_193365_a()[0]);
            }

            index++;
         }

         exlist = recipe.func_179532_b(inv);
      }

      int index = -1;

      for (Ingredient is : nonNullList) {
         index++;
         if (is.func_193365_a().length > 0) {
            AspectList obj = getObjectTags(is.func_193365_a()[0], history);
            if (obj != null) {
               for (Aspect as : obj.getAspects()) {
                  if (as != null) {
                     mid.add(as, obj.getAmount(as));
                  }
               }
            }
         }
      }

      if (exlist != null) {
         for (ItemStack ri : exlist) {
            if (!ri.func_190926_b()) {
               AspectList obj = getObjectTags(ri, history);

               for (Aspect as : obj.getAspects()) {
                  mid.reduce(as, obj.getAmount(as));
               }
            }
         }
      }

      for (Aspect as : mid.getAspects()) {
         if (as != null) {
            float v = mid.getAmount(as) * 0.75F / recipeOut.func_190916_E();
            if (v < 1.0F && v > 0.75) {
               v = 1.0F;
            }

            out.add(as, (int)v);
         }
      }

      for (Aspect as : out.getAspects()) {
         if (out.getAmount(as) <= 0) {
            out.remove(as);
         }
      }

      return out;
   }

   private static AspectList generateTagsFromRecipes(ItemStack stack, ArrayList<String> history) {
      AspectList ret = null;
      int value = 0;
      ret = generateTagsFromCrucibleRecipes(stack, history);
      if (ret != null) {
         return ret;
      }

      ret = generateTagsFromInfusionRecipes(stack, history);
      return ret != null ? ret : generateTagsFromCraftingRecipes(stack, history);
   }
}
