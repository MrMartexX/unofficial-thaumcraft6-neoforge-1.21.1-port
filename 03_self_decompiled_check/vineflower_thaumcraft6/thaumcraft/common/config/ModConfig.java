package thaumcraft.common.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.EntityFallingTaint;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.entities.monster.EntityEldritchCrab;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;
import thaumcraft.common.entities.monster.boss.EntityCultistPortalGreater;
import thaumcraft.common.entities.monster.boss.EntityEldritchGolem;
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;
import thaumcraft.common.lib.utils.CropUtils;
import thaumcraft.common.lib.utils.Utils;

public class ModConfig {
   public static final float auraSize = 4.0F;
   public static ArrayList<Aspect> aspectOrder = new ArrayList<>();
   public static boolean foundCopperIngot = false;
   public static boolean foundTinIngot = false;
   public static boolean foundSilverIngot = false;
   public static boolean foundLeadIngot = false;
   public static boolean foundCopperOre = false;
   public static boolean foundTinOre = false;
   public static boolean foundSilverOre = false;
   public static boolean foundLeadOre = false;
   public static boolean isHalloween;

   public static void postInitLoot() {
      int COMMON = 0;
      int UNCOMMON = 1;
      int RARE = 2;
      new Random(System.currentTimeMillis());
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151074_bl, 1), 2500, 0);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151074_bl, 2), 2250, 1);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151074_bl, 3), 2000, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.salisMundus), 3, 0);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.salisMundus), 6, 1);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.salisMundus), 9, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_185161_cS), 5, 0, 1, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151111_aL), 5, 0, 1, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151106_aX), 5, 0, 1, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.primordialPearl, 1, 7), 1, 0);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.primordialPearl, 1, 7), 3, 1);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.primordialPearl, 1, 6), 1, 1);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.primordialPearl, 1, 5), 9, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.primordialPearl, 1, 3), 3, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.primordialPearl, 1), 1, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151156_bN), 1, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151045_i), 10, 0);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151045_i), 50, 1, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151166_bC), 15, 0);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151166_bC), 75, 1, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151043_k), 100, 0, 1, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151079_bi), 100, 0, 1, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.amuletVis, 1, 0), 6, 1, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 0), 10, 0);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 1), 10, 0);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 2), 10, 0);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 3), 5, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 4), 5, 1);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 5), 5, 1);
      ThaumcraftApi.addLootBagItem(new ItemStack(ItemsTC.baubles, 1, 6), 5, 1);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151062_by), 5, 0);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151062_by), 10, 1);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151062_by), 20, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151153_ao, 1, 1), 1, 0);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151153_ao, 1, 1), 2, 1);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151153_ao, 1, 1), 3, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151153_ao, 1, 0), 3, 0);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151153_ao, 1, 0), 6, 1);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151153_ao, 1, 0), 9, 2);
      ThaumcraftApi.addLootBagItem(new ItemStack(Items.field_151122_aG), 10, 0, 1, 2);

      for (PotionType pt : PotionType.field_185176_a) {
         ThaumcraftApi.addLootBagItem(PotionUtils.func_185188_a(new ItemStack(Items.field_151068_bn), pt), 2, 0, 1, 2);
         ThaumcraftApi.addLootBagItem(PotionUtils.func_185188_a(new ItemStack(Items.field_185155_bH), pt), 2, 0, 1, 2);
         ThaumcraftApi.addLootBagItem(PotionUtils.func_185188_a(new ItemStack(Items.field_185156_bI), pt), 2, 1, 2);
      }

      ItemStack[] commonLoot = new ItemStack[]{new ItemStack(ItemsTC.lootBag, 1, 0), new ItemStack(ItemsTC.ingots), new ItemStack(ItemsTC.amber)};
      commonLoot = new ItemStack[]{
         new ItemStack(ItemsTC.lootBag, 1, 1), new ItemStack(ItemsTC.baubles, 1, 0), new ItemStack(ItemsTC.baubles, 1, 1), new ItemStack(ItemsTC.baubles, 1, 2)
      };
      commonLoot = new ItemStack[]{
         new ItemStack(ItemsTC.lootBag, 1, 2),
         new ItemStack(ItemsTC.thaumonomicon),
         new ItemStack(ItemsTC.thaumiumSword),
         new ItemStack(ItemsTC.thaumiumAxe),
         new ItemStack(ItemsTC.thaumiumHoe),
         new ItemStack(ItemsTC.thaumiumPick),
         new ItemStack(ItemsTC.baubles, 1, 3),
         new ItemStack(ItemsTC.baubles, 1, 4),
         new ItemStack(ItemsTC.baubles, 1, 5),
         new ItemStack(ItemsTC.baubles, 1, 6),
         new ItemStack(ItemsTC.amuletVis, 1, 0)
      };
   }

   public static void modCompatibility() {
      Thaumcraft.log.info("Checking for mod & oredict compatibilities");
      ResourceLocation defaultGroup = new ResourceLocation("");

      try {
         if (OreDictionary.doesOreNameExist("oreIron") && OreDictionary.getOres("oreIron", false).size() > 1) {
            for (ItemStack is : OreDictionary.getOres("oreIron", false)) {
               if (is.func_77973_b() != Item.func_150898_a(Blocks.field_150366_p)) {
                  Utils.addSpecialMiningResult(is, new ItemStack(ItemsTC.clusters, 1, 0), 1.0F);
               }
            }
         }

         if (OreDictionary.doesOreNameExist("oreGold") && OreDictionary.getOres("oreGold", false).size() > 1) {
            for (ItemStack is : OreDictionary.getOres("oreGold", false)) {
               if (is.func_77973_b() != Item.func_150898_a(Blocks.field_150352_o)) {
                  Utils.addSpecialMiningResult(is, new ItemStack(ItemsTC.clusters, 1, 1), 1.0F);
               }
            }
         }
      } catch (Exception var4) {
      }

      if (OreDictionary.doesOreNameExist("oreCopper")) {
         for (ItemStack is : OreDictionary.getOres("oreCopper", false)) {
            Utils.addSpecialMiningResult(is, new ItemStack(ItemsTC.clusters, 1, 2), 1.0F);
            foundCopperOre = true;
         }
      }

      if (OreDictionary.doesOreNameExist("ingotCopper")) {
         boolean first = true;

         for (ItemStack is : OreDictionary.getOres("ingotCopper", false)) {
            if (is.func_190916_E() > 1) {
               is.func_190920_e(1);
            }

            foundCopperIngot = true;
            GameRegistry.addShapedRecipe(
               new ResourceLocation("thaumcraft", "coppertonuggets"), defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 1), new Object[]{"#", '#', is}
            );
            if (first) {
               first = false;
               FurnaceRecipes.func_77602_a().func_151394_a(new ItemStack(ItemsTC.clusters, 1, 2), new ItemStack(is.func_77973_b(), 2, is.func_77952_i()), 1.0F);
               ConfigRecipes.oreDictRecipe(
                  "coppernuggetstoingot", defaultGroup, is, new Object[]{"###", "###", "###", '#', new ItemStack(ItemsTC.nuggets, 1, 1)}
               );
            }
         }
      }

      if (OreDictionary.doesOreNameExist("oreTin")) {
         for (ItemStack is : OreDictionary.getOres("oreTin", false)) {
            Utils.addSpecialMiningResult(is, new ItemStack(ItemsTC.clusters, 1, 3), 1.0F);
            foundTinOre = true;
         }
      }

      if (OreDictionary.doesOreNameExist("ingotTin")) {
         boolean first = true;

         for (ItemStack is : OreDictionary.getOres("ingotTin", false)) {
            if (is.func_190916_E() > 1) {
               is.func_190920_e(1);
            }

            foundTinIngot = true;
            GameRegistry.addShapedRecipe(
               new ResourceLocation("thaumcraft", "tintonuggets"), defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 2), new Object[]{"#", '#', is}
            );
            if (first) {
               first = false;
               FurnaceRecipes.func_77602_a().func_151394_a(new ItemStack(ItemsTC.clusters, 1, 3), new ItemStack(is.func_77973_b(), 2, is.func_77952_i()), 1.0F);
               ConfigRecipes.oreDictRecipe("tinnuggetstoingot", defaultGroup, is, new Object[]{"###", "###", "###", '#', new ItemStack(ItemsTC.nuggets, 1, 2)});
            }
         }
      }

      if (OreDictionary.doesOreNameExist("oreSilver")) {
         for (ItemStack is : OreDictionary.getOres("oreSilver", false)) {
            Utils.addSpecialMiningResult(is, new ItemStack(ItemsTC.clusters, 1, 4), 1.0F);
            foundSilverOre = true;
         }
      }

      if (OreDictionary.doesOreNameExist("ingotSilver")) {
         boolean first = true;

         for (ItemStack is : OreDictionary.getOres("ingotSilver", false)) {
            if (is.func_190916_E() > 1) {
               is.func_190920_e(1);
            }

            foundSilverIngot = true;
            GameRegistry.addShapedRecipe(
               new ResourceLocation("thaumcraft", "silvertonuggets"), defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 3), new Object[]{"#", '#', is}
            );
            if (first) {
               first = false;
               FurnaceRecipes.func_77602_a().func_151394_a(new ItemStack(ItemsTC.clusters, 1, 4), new ItemStack(is.func_77973_b(), 2, is.func_77952_i()), 1.0F);
               ConfigRecipes.oreDictRecipe(
                  "silvernuggetstoingot", defaultGroup, is, new Object[]{"###", "###", "###", '#', new ItemStack(ItemsTC.nuggets, 1, 3)}
               );
            }
         }
      }

      if (OreDictionary.doesOreNameExist("oreLead")) {
         for (ItemStack is : OreDictionary.getOres("oreLead", false)) {
            Utils.addSpecialMiningResult(is, new ItemStack(ItemsTC.clusters, 1, 5), 1.0F);
            foundLeadOre = true;
         }
      }

      if (OreDictionary.doesOreNameExist("ingotLead")) {
         boolean first = true;

         for (ItemStack is : OreDictionary.getOres("ingotLead", false)) {
            if (is.func_190916_E() > 1) {
               is.func_190920_e(1);
            }

            foundLeadIngot = true;
            GameRegistry.addShapedRecipe(
               new ResourceLocation("thaumcraft", "leadtonuggets"), defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 4), new Object[]{"#", '#', is}
            );
            if (first) {
               first = false;
               FurnaceRecipes.func_77602_a().func_151394_a(new ItemStack(ItemsTC.clusters, 1, 5), new ItemStack(is.func_77973_b(), 2, is.func_77952_i()), 1.0F);
               ConfigRecipes.oreDictRecipe("leadnuggetstoingot", defaultGroup, is, new Object[]{"###", "###", "###", '#', new ItemStack(ItemsTC.nuggets, 1, 4)});
            }
         }
      }

      Thaumcraft.log.info("Adding entities to MFR safari net blacklist.");
      registerSafariNetBlacklist(EntityOwnedConstruct.class);
      registerSafariNetBlacklist(EntityFallingTaint.class);
      registerSafariNetBlacklist(EntityWisp.class);
      registerSafariNetBlacklist(EntityPech.class);
      registerSafariNetBlacklist(EntityEldritchGuardian.class);
      registerSafariNetBlacklist(EntityEldritchWarden.class);
      registerSafariNetBlacklist(EntityEldritchGolem.class);
      registerSafariNetBlacklist(EntityCultistCleric.class);
      registerSafariNetBlacklist(EntityCultistKnight.class);
      registerSafariNetBlacklist(EntityCultistLeader.class);
      registerSafariNetBlacklist(EntityCultistPortalGreater.class);
      registerSafariNetBlacklist(EntityCultistPortalLesser.class);
      registerSafariNetBlacklist(EntityEldritchCrab.class);
      registerSafariNetBlacklist(EntityInhabitedZombie.class);
   }

   public static void registerSafariNetBlacklist(Class<?> blacklistedEntity) {
      try {
         Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
         if (registry != null) {
            Method reg = registry.getMethod("registerSafariNetBlacklist", Class.class);
            reg.invoke(registry, blacklistedEntity);
         }
      } catch (Exception var3) {
      }
   }

   public static void postInitMisc() {
      for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
         if (item != null && item instanceof IPlantable) {
            try {
               IBlockState bs = ((IPlantable)item).getPlant(null, null);
               if (bs != null) {
                  ThaumcraftApi.registerSeed(bs.func_177230_c(), new ItemStack(item));
               }
            } catch (Exception var3) {
            }
         }
      }

      CropUtils.addStandardCrop(Blocks.field_150440_ba, 32767);
      CropUtils.addStandardCrop(Blocks.field_150423_aK, 32767);
      CropUtils.addStackedCrop(Blocks.field_150436_aH, 32767);
      CropUtils.addStackedCrop(Blocks.field_150434_aF, 32767);
      CropUtils.addStandardCrop(Blocks.field_150388_bm, 3);
      ThaumcraftApi.registerSeed(Blocks.field_150375_by, new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BROWN.func_176767_b()));
      Utils.addSpecialMiningResult(new ItemStack(Blocks.field_150366_p), new ItemStack(ItemsTC.clusters, 1, 0), 1.0F);
      Utils.addSpecialMiningResult(new ItemStack(Blocks.field_150352_o), new ItemStack(ItemsTC.clusters, 1, 1), 1.0F);
      Utils.addSpecialMiningResult(new ItemStack(BlocksTC.oreCinnabar), new ItemStack(ItemsTC.clusters, 1, 6), 1.0F);
      Utils.addSpecialMiningResult(new ItemStack(Items.field_151128_bU), new ItemStack(ItemsTC.clusters, 1, 7), 1.0F);

      for (Aspect aspect : Aspect.aspects.values()) {
         aspectOrder.add(aspect);
      }
   }

   @LangKey("thaumcraft.config.graphics")
   @Config(modid = "thaumcraft", type = Type.INSTANCE, name = "thaumcraft_graphics")
   public static class CONFIG_GRAPHICS {
      @Comment("Setting this to true will make the amount text in aspect tags twice as large. Useful for certain resolutions and custom fonts.")
      public static boolean largeTagText = false;
      @Comment("This setting will disable certain thaumcraft shaders for those who experience FPS drops.")
      public static boolean disableShaders = false;
      @Comment("Set to true to disable anxiety triggers like the heartbeat sound.")
      public static boolean nostress = false;
      @Comment("Hate crooked labels, kittens, puppies and all things awesome? If yes, set this to false.")
      public static boolean crooked = true;
      @Comment("Set to true to have the wand dial display in the bottom left instead of the top left.")
      public static boolean dialBottom = false;
      @Comment(
         {
               "Item aspects are hidden by default and pressing shift reveals them.",
               "Changing this setting to 'true' will reverse this behaviour and always",
               "display aspects unless shift is pressed."
         }
      )
      public static boolean showTags = false;
      @Comment("Set this to true to get the old blue magical forest back.")
      public static boolean blueBiome = false;
      @Comment("Will golems display emote particles if they recieve orders or encounter problems")
      public static boolean showGolemEmotes = true;
   }

   @LangKey("thaumcraft.config.misc")
   @Config(modid = "thaumcraft", type = Type.INSTANCE, name = "thaumcraft_misc")
   public static class CONFIG_MISC {
      @Comment("Setting this to true will make you get the recipe book for salis mundus without having to sleep first.")
      public static boolean noSleep = false;
      @Comment("Setting this to true disables Warp, Taint spread and similar mechanics. You wuss.")
      public static boolean wussMode = false;
      @Comment("Enables a version of the Thauminomicon in creative mode that grants you all the research when you first use it.")
      public static boolean allowCheatSheet = false;
      @Comment({"How many milliseconds passes between runic shielding recharge ticks.", "Lower values equals faster recharge. Minimum of 500."})
      @RangeInt(min = 500, max = 500000)
      public static int shieldRecharge = 2000;
      @Comment({"How many milliseconds passes after a shield has been reduced to zero", "before it can start recharging again. Minimum of 0."})
      @RangeInt(min = 0, max = 50000)
      public static int shieldWait = 4000;
      @Comment("How much vis it costs to reacharge a single unit of shielding. Minimum of 0.")
      @RangeInt(min = 0, max = 500)
      public static int shieldCost = 1;
   }

   @LangKey("thaumcraft.config.world")
   @Config(modid = "thaumcraft", type = Type.INSTANCE, name = "thaumcraft_world")
   public static class CONFIG_WORLD {
      @Comment("The dimension considered to be your 'overworld'. Certain TC structures will only spawn in this dim.")
      @RequiresMcRestart
      public static int overworldDim = 0;
      @Comment("Outer lands dimension id")
      @RequiresMcRestart
      public static int dimensionOuterId = -42;
      @Comment({"The % of normal ore amounts that will be spawned. For example 50 will spawn half", "the ores while 200 will spawn double. Default 100"})
      @RangeInt(min = 1, max = 500)
      @RequiresMcRestart
      public static int oreDensity = 100;
      public static boolean generateMagicForest = true;
      @Comment(
         {"Higher values increases number of magical forest biomes. If you are using biome", "addon mods you probably want to increase this weight quite a bit"}
      )
      @RangeInt(min = 0, max = 100)
      @RequiresMcRestart
      public static int biomeMagicalForestWeight = 5;
      @Comment({"The % chance of taint fibres spreading on a block tick.", "Setting this to 0 will effectively stop taint fibre spread."})
      @RangeInt(min = 0, max = 500)
      public static float taintSpreadRate = 100.0F;
      @Comment({"The range at which taint can spread from a taint seed.", "This value is only a base and will be modified by flux levels."})
      @RangeInt(min = 8, max = 256)
      public static int taintSpreadArea = 32;
      public static boolean generateAura = true;
      public static boolean generateStructure = true;
      public static boolean generateCinnabar = true;
      public static boolean generateAmber = true;
      public static boolean generateQuartz = true;
      public static boolean generateCrystals = true;
      public static boolean generateTrees = true;
      @Comment(
         {
               "This key is used to keep track of which chunk have been generated/regenerated.",
               "Changing it will cause the regeneration code to run again, so only change it if you want it to happen.",
               "Useful to regen only one world feature at a time."
         }
      )
      public static String regenKey = "DEFAULT";
      public static boolean regenAura = false;
      public static boolean regenStructure = false;
      public static boolean regenCinnabar = false;
      public static boolean regenAmber = false;
      public static boolean regenQuartz = false;
      public static boolean regenCrystals = false;
      public static boolean regenTrees = false;
      public static boolean allowSpawnAngryZombie = true;
      public static boolean allowSpawnFireBat = true;
      public static boolean allowSpawnWisp = true;
      public static boolean allowSpawnTaintacle = true;
      public static boolean allowSpawnPech = true;
      public static boolean allowSpawnElder = true;
      @Comment(
         {
               "Setting this to false will disable spawning champion mobs. Even when false they will still",
               "have a greatly reduced chance of spawning in certain dangerous places."
         }
      )
      public static boolean allowChampionMobs = true;
   }
}
