package thaumcraft.api;

import java.util.Arrays;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.GameData;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.Part;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.internal.DummyInternalMethodHandler;
import thaumcraft.api.internal.IInternalMethodHandler;
import thaumcraft.api.internal.WeightedRandomLoot;

public class ThaumcraftApi {
   public static IInternalMethodHandler internalMethods = new DummyInternalMethodHandler();

   public static void registerResearchLocation(ResourceLocation loc) {
      if (!CommonInternals.jsonLocs.containsKey(loc.toString())) {
         CommonInternals.jsonLocs.put(loc.toString(), loc);
      }
   }

   public static void addSmeltingBonus(Object in, ItemStack out, float chance) {
      if (in instanceof ItemStack || in instanceof String) {
         CommonInternals.smeltingBonus.add(new ThaumcraftApi.SmeltBonus(in, out, chance));
      }
   }

   public static void addSmeltingBonus(Object in, ItemStack out) {
      if (in instanceof ItemStack || in instanceof String) {
         CommonInternals.smeltingBonus.add(new ThaumcraftApi.SmeltBonus(in, out, 0.33F));
      }
   }

   public static HashMap<ResourceLocation, IThaumcraftRecipe> getCraftingRecipes() {
      return CommonInternals.craftingRecipeCatalog;
   }

   public static HashMap<ResourceLocation, Object> getCraftingRecipesFake() {
      return CommonInternals.craftingRecipeCatalogFake;
   }

   public static void addFakeCraftingRecipe(ResourceLocation registry, Object recipe) {
      getCraftingRecipesFake().put(registry, recipe);
   }

   public static void addMultiblockRecipeToCatalog(ResourceLocation registry, ThaumcraftApi.BluePrint recipe) {
      getCraftingRecipes().put(registry, recipe);
   }

   public static void addArcaneCraftingRecipe(ResourceLocation registry, IArcaneRecipe recipe) {
      recipe.setRegistryName(registry);
      GameData.register_impl(recipe);
   }

   public static void addInfusionCraftingRecipe(ResourceLocation registry, InfusionRecipe recipe) {
      getCraftingRecipes().put(registry, recipe);
   }

   public static InfusionRecipe getInfusionRecipe(ItemStack res) {
      for (Object r : getCraftingRecipes().values()) {
         if (r instanceof InfusionRecipe
            && ((InfusionRecipe)r).getRecipeOutput() instanceof ItemStack
            && ((ItemStack)((InfusionRecipe)r).getRecipeOutput()).func_77969_a(res)) {
            return (InfusionRecipe)r;
         }
      }

      return null;
   }

   public static void addCrucibleRecipe(ResourceLocation registry, CrucibleRecipe recipe) {
      getCraftingRecipes().put(registry, recipe);
   }

   public static CrucibleRecipe getCrucibleRecipe(ItemStack stack) {
      for (Object r : getCraftingRecipes().values()) {
         if (r instanceof CrucibleRecipe && ((CrucibleRecipe)r).getRecipeOutput().func_77969_a(stack)) {
            return (CrucibleRecipe)r;
         }
      }

      return null;
   }

   public static CrucibleRecipe getCrucibleRecipeFromHash(int hash) {
      for (Object recipe : getCraftingRecipes().values()) {
         if (recipe instanceof CrucibleRecipe && ((CrucibleRecipe)recipe).hash == hash) {
            return (CrucibleRecipe)recipe;
         }
      }

      return null;
   }

   public static boolean exists(ItemStack item) {
      ItemStack stack = item.func_77946_l();
      stack.func_190920_e(1);
      AspectList tmp = CommonInternals.objectTags.get(stack.serializeNBT().toString());
      if (tmp == null) {
         try {
            stack.func_77964_b(32767);
            tmp = CommonInternals.objectTags.get(stack.serializeNBT().toString());
            if (item.func_77952_i() == 32767 && tmp == null) {
               int index = 0;

               do {
                  stack.func_77964_b(index);
                  tmp = CommonInternals.objectTags.get(stack.serializeNBT().toString());
               } while (++index < 16 && tmp == null);
            }

            if (tmp == null) {
               return false;
            }
         } catch (Exception var4) {
         }
      }

      return true;
   }

   @Deprecated
   public static void registerObjectTag(ItemStack item, AspectList aspects) {
      new AspectEventProxy().registerObjectTag(item, aspects);
   }

   @Deprecated
   public static void registerObjectTag(ItemStack item, int[] meta, AspectList aspects) {
   }

   @Deprecated
   public static void registerObjectTag(String oreDict, AspectList aspects) {
      new AspectEventProxy().registerObjectTag(oreDict, aspects);
   }

   @Deprecated
   public static void registerComplexObjectTag(ItemStack item, AspectList aspects) {
      new AspectEventProxy().registerComplexObjectTag(item, aspects);
   }

   @Deprecated
   public static void registerComplexObjectTag(String oreDict, AspectList aspects) {
      new AspectEventProxy().registerComplexObjectTag(oreDict, aspects);
   }

   @Deprecated
   public static void registerEntityTag(String entityName, AspectList aspects, ThaumcraftApi.EntityTagsNBT... nbt) {
      CommonInternals.scanEntities.add(new ThaumcraftApi.EntityTags(entityName, aspects, nbt));
   }

   public static void addWarpToItem(ItemStack craftresult, int amount) {
      CommonInternals.warpMap.put(Arrays.asList(craftresult.func_77973_b(), craftresult.func_77952_i()), amount);
   }

   public static int getWarp(ItemStack in) {
      if (in == null) {
         return 0;
      } else {
         return in instanceof ItemStack && CommonInternals.warpMap.containsKey(Arrays.asList(in.func_77973_b(), in.func_77952_i()))
            ? CommonInternals.warpMap.get(Arrays.asList(in.func_77973_b(), in.func_77952_i()))
            : 0;
      }
   }

   public static void addLootBagItem(ItemStack item, int weight, int... bagTypes) {
      if (bagTypes != null && bagTypes.length != 0) {
         for (int rarity : bagTypes) {
            switch (rarity) {
               case 0:
                  WeightedRandomLoot.lootBagCommon.add(new WeightedRandomLoot(item, weight));
                  break;
               case 1:
                  WeightedRandomLoot.lootBagUncommon.add(new WeightedRandomLoot(item, weight));
                  break;
               case 2:
                  WeightedRandomLoot.lootBagRare.add(new WeightedRandomLoot(item, weight));
            }
         }
      } else {
         WeightedRandomLoot.lootBagCommon.add(new WeightedRandomLoot(item, weight));
      }
   }

   public static void registerSeed(Block block, ItemStack seed) {
      CommonInternals.seedList.put(block.func_149739_a(), seed);
   }

   public static ItemStack getSeed(Block block) {
      return CommonInternals.seedList.get(block.func_149739_a());
   }

   public static class BluePrint implements IThaumcraftRecipe {
      Part[][][] parts;
      String research;
      ItemStack displayStack;
      ItemStack[] ingredientList;
      private String group;

      public BluePrint(String research, Part[][][] parts, ItemStack... ingredientList) {
         this.parts = parts;
         this.research = research;
         this.ingredientList = ingredientList;
      }

      public BluePrint(String research, ItemStack display, Part[][][] parts, ItemStack... ingredientList) {
         this.parts = parts;
         this.research = research;
         this.displayStack = display;
         this.ingredientList = ingredientList;
      }

      public Part[][][] getParts() {
         return this.parts;
      }

      @Override
      public String getResearch() {
         return this.research;
      }

      public ItemStack[] getIngredientList() {
         return this.ingredientList;
      }

      public ItemStack getDisplayStack() {
         return this.displayStack;
      }

      @Override
      public String getGroup() {
         return this.group;
      }

      public ThaumcraftApi.BluePrint setGroup(ResourceLocation loc) {
         this.group = loc.toString();
         return this;
      }
   }

   public static class EntityTags {
      public String entityName;
      public ThaumcraftApi.EntityTagsNBT[] nbts;
      public AspectList aspects;

      public EntityTags(String entityName, AspectList aspects, ThaumcraftApi.EntityTagsNBT... nbts) {
         this.entityName = entityName;
         this.nbts = nbts;
         this.aspects = aspects;
      }
   }

   public static class EntityTagsNBT {
      public String name;
      public Object value;

      public EntityTagsNBT(String name, Object value) {
         this.name = name;
         this.value = value;
      }
   }

   public static class SmeltBonus {
      public Object in;
      public ItemStack out;
      public float chance;

      public SmeltBonus(Object in, ItemStack out, float chance) {
         this.in = in;
         this.out = out;
         this.chance = chance;
      }
   }
}
