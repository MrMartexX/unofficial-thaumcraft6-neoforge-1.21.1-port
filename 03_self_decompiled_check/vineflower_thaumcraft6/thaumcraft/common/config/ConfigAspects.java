package thaumcraft.common.config;

import java.util.HashSet;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectRegistryEvent;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class ConfigAspects {
   public static String[] dyes = new String[]{
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

   public static void postInit() {
      CommonInternals.objectTags.clear();
      registerItemAspects();
      registerEntityAspects();
      AspectRegistryEvent are = new AspectRegistryEvent();
      are.register = new AspectEventProxy();
      MinecraftForge.EVENT_BUS.post(are);
   }

   private static void registerEntityAspects() {
      ThaumcraftApi.registerEntityTag("Zombie", new AspectList().add(Aspect.UNDEAD, 20).add(Aspect.MAN, 10).add(Aspect.EARTH, 5));
      ThaumcraftApi.registerEntityTag("Husk", new AspectList().add(Aspect.UNDEAD, 20).add(Aspect.MAN, 10).add(Aspect.FIRE, 5));
      ThaumcraftApi.registerEntityTag("Giant", new AspectList().add(Aspect.UNDEAD, 25).add(Aspect.MAN, 15).add(Aspect.EARTH, 10));
      ThaumcraftApi.registerEntityTag("Skeleton", new AspectList().add(Aspect.UNDEAD, 20).add(Aspect.MAN, 5).add(Aspect.EARTH, 5));
      ThaumcraftApi.registerEntityTag("WitherSkeleton", new AspectList().add(Aspect.UNDEAD, 25).add(Aspect.MAN, 5).add(Aspect.ENTROPY, 10));
      ThaumcraftApi.registerEntityTag("Creeper", new AspectList().add(Aspect.PLANT, 15).add(Aspect.FIRE, 15));
      ThaumcraftApi.registerEntityTag(
         "Creeper", new AspectList().add(Aspect.PLANT, 15).add(Aspect.FIRE, 15).add(Aspect.ENERGY, 15), new ThaumcraftApi.EntityTagsNBT("powered", (byte)1)
      );
      ThaumcraftApi.registerEntityTag("Horse", new AspectList().add(Aspect.BEAST, 15).add(Aspect.EARTH, 5).add(Aspect.AIR, 5));
      ThaumcraftApi.registerEntityTag("Donkey", new AspectList().add(Aspect.BEAST, 15).add(Aspect.EARTH, 5).add(Aspect.AIR, 5));
      ThaumcraftApi.registerEntityTag("Mule", new AspectList().add(Aspect.BEAST, 15).add(Aspect.EARTH, 5).add(Aspect.AIR, 5));
      ThaumcraftApi.registerEntityTag("SkeletonHorse", new AspectList().add(Aspect.BEAST, 5).add(Aspect.UNDEAD, 10).add(Aspect.EARTH, 5).add(Aspect.AIR, 5));
      ThaumcraftApi.registerEntityTag("ZombieHorse", new AspectList().add(Aspect.BEAST, 10).add(Aspect.UNDEAD, 5).add(Aspect.EARTH, 5).add(Aspect.AIR, 5));
      ThaumcraftApi.registerEntityTag("Pig", new AspectList().add(Aspect.BEAST, 10).add(Aspect.EARTH, 10).add(Aspect.DESIRE, 5));
      ThaumcraftApi.registerEntityTag("XPOrb", new AspectList().add(Aspect.MIND, 10));
      ThaumcraftApi.registerEntityTag("Sheep", new AspectList().add(Aspect.BEAST, 10).add(Aspect.EARTH, 10));
      ThaumcraftApi.registerEntityTag("Cow", new AspectList().add(Aspect.BEAST, 15).add(Aspect.EARTH, 15));
      ThaumcraftApi.registerEntityTag("MushroomCow", new AspectList().add(Aspect.BEAST, 15).add(Aspect.PLANT, 15).add(Aspect.EARTH, 15));
      ThaumcraftApi.registerEntityTag("SnowMan", new AspectList().add(Aspect.COLD, 10).add(Aspect.MAN, 5).add(Aspect.MECHANISM, 5).add(Aspect.MAGIC, 5));
      ThaumcraftApi.registerEntityTag("Ozelot", new AspectList().add(Aspect.BEAST, 10).add(Aspect.ENTROPY, 10));
      ThaumcraftApi.registerEntityTag("Chicken", new AspectList().add(Aspect.BEAST, 5).add(Aspect.FLIGHT, 5).add(Aspect.AIR, 5));
      ThaumcraftApi.registerEntityTag("Squid", new AspectList().add(Aspect.BEAST, 5).add(Aspect.WATER, 10));
      ThaumcraftApi.registerEntityTag("Wolf", new AspectList().add(Aspect.BEAST, 15).add(Aspect.EARTH, 10).add(Aspect.AVERSION, 5));
      ThaumcraftApi.registerEntityTag("Bat", new AspectList().add(Aspect.BEAST, 5).add(Aspect.FLIGHT, 5).add(Aspect.DARKNESS, 5));
      ThaumcraftApi.registerEntityTag("Spider", new AspectList().add(Aspect.BEAST, 10).add(Aspect.ENTROPY, 10).add(Aspect.TRAP, 10));
      ThaumcraftApi.registerEntityTag("Slime", new AspectList().add(Aspect.LIFE, 10).add(Aspect.WATER, 10).add(Aspect.ALCHEMY, 5));
      ThaumcraftApi.registerEntityTag("Ghast", new AspectList().add(Aspect.UNDEAD, 15).add(Aspect.FIRE, 15));
      ThaumcraftApi.registerEntityTag("PigZombie", new AspectList().add(Aspect.UNDEAD, 15).add(Aspect.FIRE, 15).add(Aspect.BEAST, 10));
      ThaumcraftApi.registerEntityTag("Enderman", new AspectList().add(Aspect.ELDRITCH, 10).add(Aspect.MOTION, 15).add(Aspect.DESIRE, 5));
      ThaumcraftApi.registerEntityTag("CaveSpider", new AspectList().add(Aspect.BEAST, 5).add(Aspect.DEATH, 10).add(Aspect.TRAP, 10));
      ThaumcraftApi.registerEntityTag("Silverfish", new AspectList().add(Aspect.BEAST, 5).add(Aspect.EARTH, 10));
      ThaumcraftApi.registerEntityTag("Blaze", new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.FIRE, 15).add(Aspect.FLIGHT, 5));
      ThaumcraftApi.registerEntityTag("LavaSlime", new AspectList().add(Aspect.WATER, 5).add(Aspect.FIRE, 10).add(Aspect.ALCHEMY, 5));
      ThaumcraftApi.registerEntityTag(
         "EnderDragon", new AspectList().add(Aspect.ELDRITCH, 50).add(Aspect.BEAST, 30).add(Aspect.ENTROPY, 50).add(Aspect.FLIGHT, 10)
      );
      ThaumcraftApi.registerEntityTag("WitherBoss", new AspectList().add(Aspect.UNDEAD, 50).add(Aspect.ENTROPY, 25).add(Aspect.FIRE, 25));
      ThaumcraftApi.registerEntityTag("Witch", new AspectList().add(Aspect.MAN, 15).add(Aspect.MAGIC, 5).add(Aspect.ALCHEMY, 10));
      ThaumcraftApi.registerEntityTag("Villager", new AspectList().add(Aspect.MAN, 15));
      ThaumcraftApi.registerEntityTag("VillagerGolem", new AspectList().add(Aspect.METAL, 15).add(Aspect.MAN, 5).add(Aspect.MECHANISM, 5).add(Aspect.MAGIC, 5));
      ThaumcraftApi.registerEntityTag("EnderCrystal", new AspectList().add(Aspect.ELDRITCH, 15).add(Aspect.AURA, 15).add(Aspect.LIFE, 15));
      ThaumcraftApi.registerEntityTag("ItemFrame", new AspectList().add(Aspect.SENSES, 5).add(Aspect.CRAFT, 5));
      ThaumcraftApi.registerEntityTag("Painting", new AspectList().add(Aspect.SENSES, 10).add(Aspect.CRAFT, 5));
      ThaumcraftApi.registerEntityTag("Guardian", new AspectList().add(Aspect.BEAST, 10).add(Aspect.ELDRITCH, 10).add(Aspect.WATER, 10));
      ThaumcraftApi.registerEntityTag(
         "Guardian", new AspectList().add(Aspect.BEAST, 10).add(Aspect.ELDRITCH, 15).add(Aspect.WATER, 15), new ThaumcraftApi.EntityTagsNBT("Elder", true)
      );
      ThaumcraftApi.registerEntityTag("Rabbit", new AspectList().add(Aspect.BEAST, 5).add(Aspect.EARTH, 5).add(Aspect.MOTION, 5));
      ThaumcraftApi.registerEntityTag("Endermite", new AspectList().add(Aspect.BEAST, 5).add(Aspect.ELDRITCH, 5).add(Aspect.MOTION, 5));
      ThaumcraftApi.registerEntityTag("PolarBear", new AspectList().add(Aspect.BEAST, 15).add(Aspect.COLD, 10));
      ThaumcraftApi.registerEntityTag("Shulker", new AspectList().add(Aspect.ELDRITCH, 10).add(Aspect.TRAP, 5).add(Aspect.FLIGHT, 5).add(Aspect.PROTECT, 5));
      ThaumcraftApi.registerEntityTag("EvocationIllager", new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.MAGIC, 5).add(Aspect.MAN, 10));
      ThaumcraftApi.registerEntityTag("VindicationIllager", new AspectList().add(Aspect.AVERSION, 5).add(Aspect.MAGIC, 5).add(Aspect.MAN, 10));
      ThaumcraftApi.registerEntityTag("IllusionIllager", new AspectList().add(Aspect.SENSES, 5).add(Aspect.MAGIC, 5).add(Aspect.MAN, 10));
      ThaumcraftApi.registerEntityTag("Llama", new AspectList().add(Aspect.BEAST, 15).add(Aspect.WATER, 5));
      ThaumcraftApi.registerEntityTag("Parrot", new AspectList().add(Aspect.BEAST, 5).add(Aspect.FLIGHT, 5).add(Aspect.SENSES, 5));
      ThaumcraftApi.registerEntityTag("Stray", new AspectList().add(Aspect.UNDEAD, 20).add(Aspect.MAN, 5).add(Aspect.TRAP, 5));
      ThaumcraftApi.registerEntityTag("Vex", new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.FLIGHT, 5).add(Aspect.MAGIC, 5).add(Aspect.MAN, 5));
      ThaumcraftApi.registerEntityTag("Stray", new AspectList().add(Aspect.UNDEAD, 20).add(Aspect.MAN, 5).add(Aspect.TRAP, 5));
      ThaumcraftApi.registerEntityTag("Thaumcraft.FluxRift", new AspectList().add(Aspect.FLUX, 20).add(Aspect.ELDRITCH, 20).add(Aspect.AURA, 20));
      ThaumcraftApi.registerEntityTag("Thaumcraft.Firebat", new AspectList().add(Aspect.BEAST, 5).add(Aspect.FLIGHT, 5).add(Aspect.FIRE, 10));
      ThaumcraftApi.registerEntityTag(
         "Thaumcraft.Pech",
         new AspectList().add(Aspect.MAN, 10).add(Aspect.AURA, 5).add(Aspect.EXCHANGE, 10).add(Aspect.DESIRE, 5),
         new ThaumcraftApi.EntityTagsNBT("PechType", (byte)0)
      );
      ThaumcraftApi.registerEntityTag(
         "Thaumcraft.Pech",
         new AspectList().add(Aspect.MAN, 10).add(Aspect.AURA, 5).add(Aspect.EXCHANGE, 10).add(Aspect.AVERSION, 5),
         new ThaumcraftApi.EntityTagsNBT("PechType", (byte)1)
      );
      ThaumcraftApi.registerEntityTag(
         "Thaumcraft.Pech",
         new AspectList().add(Aspect.MAN, 10).add(Aspect.AURA, 5).add(Aspect.EXCHANGE, 10).add(Aspect.MAGIC, 5),
         new ThaumcraftApi.EntityTagsNBT("PechType", (byte)2)
      );
      ThaumcraftApi.registerEntityTag(
         "Thaumcraft.ThaumSlime", new AspectList().add(Aspect.LIFE, 5).add(Aspect.WATER, 5).add(Aspect.FLUX, 5).add(Aspect.ALCHEMY, 5)
      );
      ThaumcraftApi.registerEntityTag(
         "Thaumcraft.BrainyZombie", new AspectList().add(Aspect.UNDEAD, 20).add(Aspect.MAN, 10).add(Aspect.MIND, 5).add(Aspect.AVERSION, 5)
      );
      ThaumcraftApi.registerEntityTag(
         "Thaumcraft.GiantBrainyZombie", new AspectList().add(Aspect.UNDEAD, 25).add(Aspect.MAN, 15).add(Aspect.MIND, 5).add(Aspect.AVERSION, 10)
      );
      ThaumcraftApi.registerEntityTag("Thaumcraft.Taintacle", new AspectList().add(Aspect.FLUX, 15).add(Aspect.BEAST, 10));
      ThaumcraftApi.registerEntityTag("Thaumcraft.TaintSeed", new AspectList().add(Aspect.FLUX, 20).add(Aspect.AURA, 10).add(Aspect.PLANT, 5));
      ThaumcraftApi.registerEntityTag("Thaumcraft.TaintSeedPrime", new AspectList().add(Aspect.FLUX, 25).add(Aspect.AURA, 15).add(Aspect.PLANT, 5));
      ThaumcraftApi.registerEntityTag("Thaumcraft.TaintacleTiny", new AspectList().add(Aspect.FLUX, 5).add(Aspect.BEAST, 5));
      ThaumcraftApi.registerEntityTag("Thaumcraft.TaintSwarm", new AspectList().add(Aspect.FLUX, 15).add(Aspect.AIR, 5));
      ThaumcraftApi.registerEntityTag("Thaumcraft.MindSpider", new AspectList().add(Aspect.FLUX, 5).add(Aspect.FIRE, 5));
      ThaumcraftApi.registerEntityTag("Thaumcraft.EldritchGuardian", new AspectList().add(Aspect.ELDRITCH, 20).add(Aspect.DEATH, 20).add(Aspect.UNDEAD, 20));
      ThaumcraftApi.registerEntityTag("Thaumcraft.CultistKnight", new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.MAN, 15).add(Aspect.AVERSION, 5));
      ThaumcraftApi.registerEntityTag("Thaumcraft.CultistCleric", new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.MAN, 15).add(Aspect.AVERSION, 5));
      ThaumcraftApi.registerEntityTag("Thaumcraft.EldritchCrab", new AspectList().add(Aspect.ELDRITCH, 10).add(Aspect.BEAST, 10).add(Aspect.TRAP, 10));
      ThaumcraftApi.registerEntityTag("Thaumcraft.InhabitedZombie", new AspectList().add(Aspect.ELDRITCH, 10).add(Aspect.UNDEAD, 10).add(Aspect.MAN, 5));
      ThaumcraftApi.registerEntityTag("Thaumcraft.TaintSeed", new AspectList().add(Aspect.PLANT, 20).add(Aspect.BEAST, 20).add(Aspect.FLUX, 20));
      ThaumcraftApi.registerEntityTag("Thaumcraft.TaintSeedPrime", new AspectList().add(Aspect.PLANT, 30).add(Aspect.BEAST, 30).add(Aspect.FLUX, 30));
      ThaumcraftApi.registerEntityTag("Thaumcraft.EldritchWarden", new AspectList().add(Aspect.ELDRITCH, 40).add(Aspect.DEATH, 40).add(Aspect.UNDEAD, 40));
      ThaumcraftApi.registerEntityTag("Thaumcraft.EldritchGolem", new AspectList().add(Aspect.ELDRITCH, 40).add(Aspect.ENERGY, 40).add(Aspect.MECHANISM, 40));
      ThaumcraftApi.registerEntityTag("Thaumcraft.CultistLeader", new AspectList().add(Aspect.ELDRITCH, 40).add(Aspect.AVERSION, 40).add(Aspect.MAN, 40));
      ThaumcraftApi.registerEntityTag("Thaumcraft.TaintacleGiant", new AspectList().add(Aspect.ELDRITCH, 40).add(Aspect.BEAST, 40).add(Aspect.FLUX, 40));

      for (Aspect tag : Aspect.aspects.values()) {
         ThaumcraftApi.registerEntityTag(
            "Thaumcraft.Wisp", new AspectList().add(tag, 5).add(Aspect.AURA, 5).add(Aspect.FLIGHT, 5), new ThaumcraftApi.EntityTagsNBT("Type", tag.getTag())
         );
      }

      ThaumcraftApi.registerEntityTag("Thaumcraft.Golem", new AspectList().add(Aspect.MECHANISM, 10).add(Aspect.MAN, 10).add(Aspect.MOTION, 10));
   }

   private static void registerItemAspects() {
      ThaumcraftApi.registerObjectTag("oreLapis", new AspectList().add(Aspect.EARTH, 5).add(Aspect.SENSES, 15));
      ThaumcraftApi.registerObjectTag("oreDiamond", new AspectList().add(Aspect.EARTH, 5).add(Aspect.DESIRE, 15).add(Aspect.CRYSTAL, 15));
      ThaumcraftApi.registerObjectTag("gemDiamond", new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.DESIRE, 15));
      ThaumcraftApi.registerObjectTag("oreRedstone", new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENERGY, 15));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150439_ay), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENERGY, 15));
      ThaumcraftApi.registerObjectTag("oreEmerald", new AspectList().add(Aspect.EARTH, 5).add(Aspect.DESIRE, 10).add(Aspect.CRYSTAL, 15));
      ThaumcraftApi.registerObjectTag("gemEmerald", new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.DESIRE, 10));
      ThaumcraftApi.registerObjectTag("oreQuartz", new AspectList().add(Aspect.EARTH, 5).add(Aspect.CRYSTAL, 10));
      ThaumcraftApi.registerObjectTag("gemQuartz", new AspectList().add(Aspect.CRYSTAL, 5));
      ThaumcraftApi.registerObjectTag("oreIron", new AspectList().add(Aspect.EARTH, 5).add(Aspect.METAL, 15));
      ThaumcraftApi.registerObjectTag("dustIron", new AspectList().add(Aspect.METAL, 15).add(Aspect.ENTROPY, 1));
      ThaumcraftApi.registerObjectTag("ingotIron", new AspectList().add(Aspect.METAL, 15));
      ThaumcraftApi.registerObjectTag("oreGold", new AspectList().add(Aspect.EARTH, 5).add(Aspect.METAL, 10).add(Aspect.DESIRE, 10));
      ThaumcraftApi.registerObjectTag("dustGold", new AspectList().add(Aspect.METAL, 10).add(Aspect.DESIRE, 10).add(Aspect.ENTROPY, 1));
      ThaumcraftApi.registerObjectTag("ingotGold", new AspectList().add(Aspect.METAL, 10).add(Aspect.DESIRE, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150365_q), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENERGY, 15).add(Aspect.FIRE, 15));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151044_h, 1, 32767), new AspectList().add(Aspect.ENERGY, 10).add(Aspect.FIRE, 10));
      ThaumcraftApi.registerObjectTag("dustRedstone", new AspectList().add(Aspect.ENERGY, 10));
      ThaumcraftApi.registerObjectTag("dustGlowstone", new AspectList().add(Aspect.SENSES, 5).add(Aspect.LIGHT, 10));
      ThaumcraftApi.registerObjectTag("glowstone", new AspectList(new ItemStack(Blocks.field_150426_aN)));
      ThaumcraftApi.registerObjectTag("ingotCopper", new AspectList().add(Aspect.METAL, 10).add(Aspect.EXCHANGE, 5));
      ThaumcraftApi.registerObjectTag("dustCopper", new AspectList().add(Aspect.METAL, 10).add(Aspect.ENTROPY, 1).add(Aspect.EXCHANGE, 5));
      ThaumcraftApi.registerObjectTag("oreCopper", new AspectList().add(Aspect.METAL, 10).add(Aspect.EARTH, 5).add(Aspect.EXCHANGE, 5));
      ThaumcraftApi.registerObjectTag(
         "clusterCopper", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5).add(Aspect.EXCHANGE, 10)
      );
      ThaumcraftApi.registerObjectTag("ingotTin", new AspectList().add(Aspect.METAL, 10).add(Aspect.CRYSTAL, 5));
      ThaumcraftApi.registerObjectTag("dustTin", new AspectList().add(Aspect.METAL, 10).add(Aspect.ENTROPY, 1).add(Aspect.CRYSTAL, 5));
      ThaumcraftApi.registerObjectTag("oreTin", new AspectList().add(Aspect.METAL, 10).add(Aspect.EARTH, 5).add(Aspect.CRYSTAL, 5));
      ThaumcraftApi.registerObjectTag("clusterTin", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5).add(Aspect.CRYSTAL, 10));
      ThaumcraftApi.registerObjectTag("ingotSilver", new AspectList().add(Aspect.METAL, 10).add(Aspect.DESIRE, 5));
      ThaumcraftApi.registerObjectTag("dustSilver", new AspectList().add(Aspect.METAL, 10).add(Aspect.ENTROPY, 1).add(Aspect.DESIRE, 5));
      ThaumcraftApi.registerObjectTag("oreSilver", new AspectList().add(Aspect.METAL, 10).add(Aspect.EARTH, 5).add(Aspect.DESIRE, 5));
      ThaumcraftApi.registerObjectTag("clusterSilver", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5).add(Aspect.DESIRE, 10));
      ThaumcraftApi.registerObjectTag("ingotLead", new AspectList().add(Aspect.METAL, 10).add(Aspect.ORDER, 5));
      ThaumcraftApi.registerObjectTag("dustLead", new AspectList().add(Aspect.METAL, 10).add(Aspect.ENTROPY, 1).add(Aspect.ORDER, 5));
      ThaumcraftApi.registerObjectTag("oreLead", new AspectList().add(Aspect.METAL, 10).add(Aspect.EARTH, 5).add(Aspect.ORDER, 5));
      ThaumcraftApi.registerObjectTag("clusterLead", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5).add(Aspect.ORDER, 10));
      ThaumcraftApi.registerObjectTag("ingotBrass", new AspectList().add(Aspect.METAL, 10).add(Aspect.TOOL, 5));
      ThaumcraftApi.registerObjectTag("dustBrass", new AspectList().add(Aspect.METAL, 10).add(Aspect.ENTROPY, 1).add(Aspect.TOOL, 5));
      ThaumcraftApi.registerObjectTag("ingotBronze", new AspectList().add(Aspect.METAL, 10).add(Aspect.TOOL, 5));
      ThaumcraftApi.registerObjectTag("dustBronze", new AspectList().add(Aspect.METAL, 10).add(Aspect.ENTROPY, 1).add(Aspect.TOOL, 5));
      ThaumcraftApi.registerObjectTag("oreUranium", new AspectList().add(Aspect.METAL, 10).add(Aspect.DEATH, 5).add(Aspect.ENERGY, 10));
      ThaumcraftApi.registerObjectTag("itemDropUranium", new AspectList().add(Aspect.METAL, 10).add(Aspect.DEATH, 5).add(Aspect.ENERGY, 10));
      ThaumcraftApi.registerObjectTag("ingotUranium", new AspectList().add(Aspect.METAL, 10).add(Aspect.DEATH, 5).add(Aspect.ENERGY, 10));
      ThaumcraftApi.registerObjectTag("gemRuby", new AspectList().add(Aspect.CRYSTAL, 10).add(Aspect.DESIRE, 10));
      ThaumcraftApi.registerObjectTag("gemGreenSapphire", new AspectList().add(Aspect.CRYSTAL, 10).add(Aspect.DESIRE, 10));
      ThaumcraftApi.registerObjectTag("gemSapphire", new AspectList().add(Aspect.CRYSTAL, 10).add(Aspect.DESIRE, 10));
      ThaumcraftApi.registerObjectTag("ingotSteel", new AspectList().add(Aspect.METAL, 15).add(Aspect.ORDER, 5));
      ThaumcraftApi.registerObjectTag("itemRubber", new AspectList().add(Aspect.MOTION, 5).add(Aspect.TOOL, 5));
      ThaumcraftApi.registerObjectTag("stone", new AspectList().add(Aspect.EARTH, 5));
      ThaumcraftApi.registerObjectTag("stoneGranite", new AspectList().add(Aspect.EARTH, 5));
      ThaumcraftApi.registerObjectTag("stoneDiorite", new AspectList().add(Aspect.EARTH, 5));
      ThaumcraftApi.registerObjectTag("stoneAndesite", new AspectList().add(Aspect.EARTH, 5));
      ThaumcraftApi.registerObjectTag("cobblestone", new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 1));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150357_h), new AspectList().add(Aspect.VOID, 25).add(Aspect.ENTROPY, 25).add(Aspect.EARTH, 25).add(Aspect.DARKNESS, 25)
      );
      ThaumcraftApi.registerObjectTag("dirt", new AspectList().add(Aspect.EARTH, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150346_d, 1, 2), new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 1));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150458_ak, 1, 32767), new AspectList().add(Aspect.EARTH, 5).add(Aspect.WATER, 2).add(Aspect.ORDER, 2)
      );
      ThaumcraftApi.registerObjectTag("sand", new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5));
      ThaumcraftApi.registerObjectTag("grass", new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 2));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_185774_da), new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 2).add(Aspect.ORDER, 2));
      ThaumcraftApi.registerObjectTag("endstone", new AspectList().add(Aspect.EARTH, 5).add(Aspect.DARKNESS, 5));
      ThaumcraftApi.registerObjectTag("gravel", new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 2));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150391_bh), new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 1).add(Aspect.FLUX, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151119_aD, 1, 32767), new AspectList().add(Aspect.WATER, 5).add(Aspect.EARTH, 5));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150405_ch, 1, 32767), new AspectList(new ItemStack(Blocks.field_150435_aG)).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150406_ce, 1, 32767), new AspectList(new ItemStack(Blocks.field_150435_aG)).add(Aspect.FIRE, 1).add(Aspect.SENSES, 1)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151118_aC, 1, 32767), new AspectList(new ItemStack(Items.field_151119_aD)).add(Aspect.FIRE, 1));
      ThaumcraftApi.registerObjectTag("netherrack", new AspectList().add(Aspect.EARTH, 5).add(Aspect.FIRE, 2));
      ThaumcraftApi.registerObjectTag("ingotBrickNether", new AspectList(new ItemStack(Blocks.field_150424_aL)).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150425_aM, 1, 32767), new AspectList().add(Aspect.EARTH, 3).add(Aspect.TRAP, 1).add(Aspect.SOUL, 3)
      );
      ThaumcraftApi.registerObjectTag("blockGlass", new AspectList().add(Aspect.CRYSTAL, 5));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150341_Y, 1, 32767), new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 3).add(Aspect.ENTROPY, 1)
      );
      ThaumcraftApi.registerObjectTag("obsidian", new AspectList().add(Aspect.EARTH, 5).add(Aspect.FIRE, 5).add(Aspect.DARKNESS, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150417_aV, 1, 1), new AspectList(new ItemStack(Blocks.field_150417_aV)).add(Aspect.PLANT, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150417_aV, 1, 2), new AspectList(new ItemStack(Blocks.field_150417_aV)).add(Aspect.ENTROPY, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150417_aV, 1, 3), new AspectList(new ItemStack(Blocks.field_150417_aV)).add(Aspect.ORDER, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150322_A, 1, 1), new AspectList(new ItemStack(Blocks.field_150322_A)).add(Aspect.ORDER, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150322_A, 1, 2), new AspectList(new ItemStack(Blocks.field_150322_A)).add(Aspect.ORDER, 1));
      ThaumcraftApi.registerComplexObjectTag(
         new ItemStack(Blocks.field_192443_dR, 1, 32767), new AspectList(new ItemStack(Blocks.field_192444_dS)).add(Aspect.WATER, 1).add(Aspect.ORDER, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192442_dQ), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192438_dM), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192439_dN), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192436_dK), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192434_dI), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192440_dO), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192430_dE), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192432_dG), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192429_dD), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192428_dC), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192433_dH), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192437_dL), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192441_dP), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192435_dJ), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192427_dB), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_192431_dF), new AspectList(new ItemStack(Blocks.field_150406_ce)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag("logWood", new AspectList().add(Aspect.PLANT, 20));
      ThaumcraftApi.registerObjectTag("treeSapling", new AspectList().add(Aspect.PLANT, 15).add(Aspect.LIFE, 5));
      ThaumcraftApi.registerObjectTag("treeLeaves", new AspectList().add(Aspect.PLANT, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150329_H, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150398_cm, 1, 0), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1).add(Aspect.SENSES, 5).add(Aspect.LIFE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150398_cm, 1, 1), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1).add(Aspect.SENSES, 5).add(Aspect.LIFE, 1)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150398_cm, 1, 2), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150398_cm, 1, 3), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150398_cm, 1, 4), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1).add(Aspect.SENSES, 5).add(Aspect.LIFE, 1)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150398_cm, 1, 5), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1).add(Aspect.SENSES, 5).add(Aspect.LIFE, 1)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150392_bi, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.WATER, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150330_I, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.ENTROPY, 1));
      ThaumcraftApi.registerObjectTag("vine", new AspectList().add(Aspect.PLANT, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151014_N, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151081_bc, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151080_bb, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_185163_cU, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 1));
      ThaumcraftApi.registerObjectTag("cropNetherWart", new AspectList().add(Aspect.PLANT, 1).add(Aspect.FLUX, 2).add(Aspect.ALCHEMY, 3));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150328_O, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 1).add(Aspect.SENSES, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150327_N, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 1).add(Aspect.SENSES, 5)
      );
      ThaumcraftApi.registerObjectTag("blockCactus", new AspectList().add(Aspect.PLANT, 5).add(Aspect.WATER, 5).add(Aspect.AVERSION, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150338_P), new AspectList().add(Aspect.PLANT, 5).add(Aspect.DARKNESS, 2).add(Aspect.EARTH, 2));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150337_Q), new AspectList().add(Aspect.PLANT, 5).add(Aspect.DARKNESS, 2).add(Aspect.FIRE, 2));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150420_aW, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.DARKNESS, 2).add(Aspect.EARTH, 2)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150419_aX, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.DARKNESS, 2).add(Aspect.FIRE, 2)
      );
      ThaumcraftApi.registerObjectTag("sugarcane", new AspectList().add(Aspect.PLANT, 5).add(Aspect.WATER, 3).add(Aspect.AIR, 2));
      ThaumcraftApi.registerObjectTag("cropWheat", new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151034_e), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 5));
      ThaumcraftApi.registerObjectTag("cropCarrot", new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 5).add(Aspect.SENSES, 5));
      ThaumcraftApi.registerObjectTag("cropPotato", new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 5).add(Aspect.EARTH, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_185164_cV), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 5).add(Aspect.DESIRE, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151168_bH), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151170_bI), new AspectList().add(Aspect.PLANT, 5).add(Aspect.DEATH, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150423_aK), new AspectList().add(Aspect.PLANT, 10));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150440_ba, 1, 32767), new AspectList().add(Aspect.PLANT, 10).remove(Aspect.LIFE, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151127_ba), new AspectList().add(Aspect.PLANT, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150360_v, 1, 0), new AspectList().add(Aspect.EARTH, 5).add(Aspect.TRAP, 5).add(Aspect.VOID, 5));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150360_v, 1, 1), new AspectList().add(Aspect.EARTH, 5).add(Aspect.TRAP, 5).add(Aspect.WATER, 5)
      );
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151102_aT), new AspectList().add(Aspect.DESIRE, 1).add(Aspect.ENERGY, 1));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151105_aU), new AspectList().add(Aspect.DESIRE, 1).add(Aspect.LIFE, 2));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151158_bO), new AspectList().add(Aspect.DESIRE, 1).add(Aspect.LIFE, 2));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150325_L), new AspectList().add(Aspect.BEAST, 15).add(Aspect.CRAFT, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151062_by), new AspectList().add(Aspect.MIND, 20));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151057_cb, 1, 32767), new AspectList().add(Aspect.MIND, 10).add(Aspect.BEAST, 10));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151138_bX, 1, 32767), new AspectList().add(Aspect.METAL, 15).add(Aspect.PROTECT, 10).add(Aspect.BEAST, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151136_bY, 1, 32767), new AspectList().add(Aspect.METAL, 10).add(Aspect.PROTECT, 15).add(Aspect.BEAST, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151125_bZ, 1, 32767), new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.PROTECT, 20).add(Aspect.BEAST, 5)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150480_ab, 1, 32767), new AspectList().add(Aspect.FIRE, 20));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_189877_df, 1, 32767), new AspectList().add(Aspect.FIRE, 10).add(Aspect.EARTH, 5));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_185766_cS, 1, 32767), new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.SENSES, 5).add(Aspect.PLANT, 5)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_185765_cR, 1, 32767), new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.PLANT, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_185161_cS), new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.SENSES, 5).add(Aspect.PLANT, 5));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_185162_cT), new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.SENSES, 5).add(Aspect.PLANT, 4).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150432_aD, 1, 32767), new AspectList().add(Aspect.COLD, 20));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150403_cj, 1, 32767), new AspectList().add(Aspect.COLD, 15).add(Aspect.ORDER, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151126_ay, 1, 32767), new AspectList().add(Aspect.COLD, 1));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151106_aX, 1, 32767), new AspectList().add(Aspect.DESIRE, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151068_bn), new AspectList().add(Aspect.WATER, 5).add(Aspect.CRYSTAL, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150478_aa, 1, 32767), new AspectList().add(Aspect.LIGHT, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_150321_G, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.BEAST, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151145_ak, 1, 32767), new AspectList().add(Aspect.EARTH, 5).add(Aspect.TOOL, 5));
      ThaumcraftApi.registerObjectTag("string", new AspectList().add(Aspect.BEAST, 5).add(Aspect.CRAFT, 1));
      ThaumcraftApi.registerObjectTag("slimeball", new AspectList().add(Aspect.WATER, 5).add(Aspect.LIFE, 5).add(Aspect.ALCHEMY, 1));
      ThaumcraftApi.registerObjectTag("leather", new AspectList().add(Aspect.BEAST, 5).add(Aspect.PROTECT, 5));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151078_bh, 1, 32767), new AspectList().add(Aspect.MAN, 5).add(Aspect.LIFE, 5).add(Aspect.ENTROPY, 5)
      );
      ThaumcraftApi.registerObjectTag("feather", new AspectList().add(Aspect.FLIGHT, 5).add(Aspect.AIR, 5));
      ThaumcraftApi.registerObjectTag("bone", new AspectList().add(Aspect.DEATH, 5).add(Aspect.LIFE, 5));
      ThaumcraftApi.registerObjectTag("egg", new AspectList().add(Aspect.LIFE, 5).add(Aspect.BEAST, 5));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151070_bp, 1, 32767), new AspectList().add(Aspect.SENSES, 5).add(Aspect.BEAST, 5).add(Aspect.DEATH, 5)
      );
      ThaumcraftApi.registerObjectTag("gunpowder", new AspectList().add(Aspect.FIRE, 10).add(Aspect.ENTROPY, 10).add(Aspect.ALCHEMY, 5));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151115_aP, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.WATER, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_179566_aV, 1, 32767), new AspectList().add(Aspect.CRAFT, 1).add(Aspect.BEAST, 5).add(Aspect.LIFE, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151076_bf, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.AIR, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151077_bg, 1, 32767), new AspectList().add(Aspect.CRAFT, 1).add(Aspect.BEAST, 5).add(Aspect.LIFE, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151147_al, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.EARTH, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151157_am, 1, 32767), new AspectList().add(Aspect.CRAFT, 1).add(Aspect.BEAST, 5).add(Aspect.LIFE, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151082_bd, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.EARTH, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151083_be, 1, 32767), new AspectList().add(Aspect.CRAFT, 1).add(Aspect.BEAST, 5).add(Aspect.LIFE, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_179561_bm, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.EARTH, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_179557_bn, 1, 32767), new AspectList().add(Aspect.CRAFT, 1).add(Aspect.BEAST, 5).add(Aspect.LIFE, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_179558_bo, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.EARTH, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_179559_bp, 1, 32767), new AspectList().add(Aspect.CRAFT, 1).add(Aspect.BEAST, 5).add(Aspect.LIFE, 5)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_179555_bs, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.PROTECT, 2));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_179556_br, 1, 32767),
         new AspectList().add(Aspect.BEAST, 5).add(Aspect.PROTECT, 5).add(Aspect.MOTION, 10).add(Aspect.ALCHEMY, 5)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151072_bj, 1, 32767), new AspectList().add(Aspect.FIRE, 15).add(Aspect.ENERGY, 5));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151141_av, 1, 32767), new AspectList().add(Aspect.BEAST, 10).add(Aspect.MOTION, 10).add(Aspect.ORDER, 5)
      );
      ThaumcraftApi.registerObjectTag("enderpearl", new AspectList().add(Aspect.ELDRITCH, 10).add(Aspect.MOTION, 15));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151073_bk, 1, 32767), new AspectList().add(Aspect.UNDEAD, 5).add(Aspect.SOUL, 10).add(Aspect.ALCHEMY, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151144_bL, 1, 0), new AspectList().add(Aspect.DEATH, 10).add(Aspect.SOUL, 10).add(Aspect.UNDEAD, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151144_bL, 1, 1), new AspectList().add(Aspect.DEATH, 10).add(Aspect.SOUL, 10).add(Aspect.UNDEAD, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151144_bL, 1, 2), new AspectList().add(Aspect.DEATH, 10).add(Aspect.SOUL, 10).add(Aspect.MAN, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151144_bL, 1, 3), new AspectList().add(Aspect.DEATH, 10).add(Aspect.SOUL, 10).add(Aspect.MAN, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151144_bL, 1, 4), new AspectList().add(Aspect.DEATH, 10).add(Aspect.SOUL, 10).add(Aspect.ENTROPY, 5).add(Aspect.FIRE, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151144_bL, 1, 5), new AspectList().add(Aspect.DEATH, 10).add(Aspect.SOUL, 10).add(Aspect.FIRE, 10).add(Aspect.DARKNESS, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_185157_bK), new AspectList().add(Aspect.DARKNESS, 10).add(Aspect.ENTROPY, 10).add(Aspect.FIRE, 10).add(Aspect.ALCHEMY, 10)
      );
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151066_bu), new AspectList().add(Aspect.ALCHEMY, 15));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151071_bq), new AspectList().add(Aspect.ALCHEMY, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151065_br), new AspectList().add(Aspect.ALCHEMY, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151060_bw), new AspectList().add(Aspect.ALCHEMY, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151064_bs), new AspectList().add(Aspect.ALCHEMY, 5));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_190929_cY), new AspectList().add(Aspect.ORDER, 10).add(Aspect.ENTROPY, 10).add(Aspect.LIFE, 25).add(Aspect.UNDEAD, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_190930_cZ), new AspectList().add(Aspect.PROTECT, 10).add(Aspect.ELDRITCH, 5).add(Aspect.BEAST, 5).add(Aspect.VOID, 5)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190975_dA), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190988_dw), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190990_dy), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190991_dz), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190981_dp), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190985_dt), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190977_dl), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190978_dm), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190983_dr), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190984_ds), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190986_du), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190980_do), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190982_dq), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190979_dn), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_190989_dx), new AspectList(new ItemStack(Blocks.field_190987_dv)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151086_cn), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.DESIRE, 15));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151096_cd), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.WATER, 5).add(Aspect.DESIRE, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151093_ce), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.BEAST, 5).add(Aspect.DESIRE, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151091_cg), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.EARTH, 5).add(Aspect.DESIRE, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151092_ch), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.ELDRITCH, 5).add(Aspect.DESIRE, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151089_ci), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.MAN, 5).add(Aspect.DESIRE, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151090_cj), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.CRAFT, 5).add(Aspect.DESIRE, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151087_ck), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.DARKNESS, 5).add(Aspect.DESIRE, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151088_cl), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.ENERGY, 5).add(Aspect.DESIRE, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151085_cm), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.LIFE, 5).add(Aspect.DESIRE, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151094_cf), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.TOOL, 5).add(Aspect.DESIRE, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151084_co), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.TRAP, 5).add(Aspect.DESIRE, 10)
      );
      ThaumcraftApi.registerObjectTag("netherStar", new AspectList().add(Aspect.ELDRITCH, 10).add(Aspect.MAGIC, 20).add(Aspect.ORDER, 20).add(Aspect.AURA, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151020_U, 1, 32767), new AspectList().add(Aspect.METAL, 42));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151023_V, 1, 32767), new AspectList().add(Aspect.METAL, 67));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151022_W, 1, 32767), new AspectList().add(Aspect.METAL, 58));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151029_X, 1, 32767), new AspectList().add(Aspect.METAL, 33));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151121_aF), new AspectList().add(Aspect.MIND, 2));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151134_bR), new AspectList(new ItemStack(Items.field_151122_aG)));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150342_X), new AspectList().add(Aspect.MIND, 8));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150380_bt),
         new AspectList().add(Aspect.ELDRITCH, 15).add(Aspect.BEAST, 15).add(Aspect.DARKNESS, 15).add(Aspect.MOTION, 15).add(Aspect.MAGIC, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150427_aO, 1, 32767), new AspectList().add(Aspect.FIRE, 10).add(Aspect.MOTION, 20).add(Aspect.MAGIC, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150384_bq, 1, 32767), new AspectList().add(Aspect.ELDRITCH, 10).add(Aspect.MOTION, 20).add(Aspect.MAGIC, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150378_br, 1, 32767),
         new AspectList().add(Aspect.ELDRITCH, 10).add(Aspect.ENERGY, 10).add(Aspect.MOTION, 10).add(Aspect.MAGIC, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150474_ac, 1, 32767),
         new AspectList().add(Aspect.BEAST, 20).add(Aspect.MOTION, 20).add(Aspect.UNDEAD, 20).add(Aspect.MAGIC, 20)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_179562_cC), new AspectList().add(Aspect.WATER, 5).add(Aspect.EARTH, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_179563_cD), new AspectList().add(Aspect.WATER, 5).add(Aspect.CRYSTAL, 5).add(Aspect.LIGHT, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_185160_cR), new AspectList().add(Aspect.FLIGHT, 20).add(Aspect.MOTION, 15));
      ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.field_185764_cQ, 1, 32767), new AspectList().add(Aspect.FIRE, 1).add(Aspect.LIGHT, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150486_ae, 1, 32767), new AspectList().add(Aspect.VOID, 15));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150447_bR, 1, 32767), new AspectList().add(Aspect.TRAP, 10));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151061_bv), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MAGIC, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151032_g), new AspectList().add(Aspect.AVERSION, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151069_bo), new AspectList().add(Aspect.VOID, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151153_ao, 1, 0), new AspectList().add(Aspect.MAGIC, 5).add(Aspect.LIFE, 10));
      ThaumcraftApi.registerComplexObjectTag(
         new ItemStack(Items.field_151153_ao, 1, 1), new AspectList().add(Aspect.MAGIC, 5).add(Aspect.LIFE, 15).add(Aspect.PROTECT, 15)
      );
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151054_z), new AspectList().add(Aspect.VOID, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151009_A), new AspectList().add(Aspect.LIFE, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151143_au), new AspectList().add(Aspect.MOTION, 15));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151139_aw), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_179572_au), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_179571_av), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_179567_at), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_179570_aq), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_179569_ar), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_179568_as), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151124_az), new AspectList().add(Aspect.WATER, 10).add(Aspect.MOTION, 15));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_185153_aK), new AspectList().add(Aspect.WATER, 10).add(Aspect.MOTION, 15));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_185151_aI), new AspectList().add(Aspect.WATER, 10).add(Aspect.MOTION, 15));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_185154_aL), new AspectList().add(Aspect.WATER, 10).add(Aspect.MOTION, 15));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_185152_aJ), new AspectList().add(Aspect.WATER, 10).add(Aspect.MOTION, 15));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_185150_aH), new AspectList().add(Aspect.WATER, 10).add(Aspect.MOTION, 15));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151033_d, 1, 32767), new AspectList().add(Aspect.FIRE, 10).add(Aspect.TOOL, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151112_aM, 1, 32767), new AspectList().add(Aspect.WATER, 10).add(Aspect.TOOL, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_185159_cQ, 1, 32767), new AspectList().add(Aspect.PROTECT, 20));

      for (int a = 0; a < 16; a++) {
         ItemStack sis = new ItemStack(Items.field_185159_cQ, 1, 32767);
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.func_74768_a("Base", a);
         sis.func_77983_a("BlockEntityTag", nbttagcompound);
         ThaumcraftApi.registerComplexObjectTag(sis, new AspectList().merge(Aspect.PROTECT, 20));
      }

      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_185166_h, 1, 32767), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MAGIC, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151133_ar), new AspectList().add(Aspect.VOID, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(ForgeModContainer.getInstance().universalBucket), new AspectList(new ItemStack(Items.field_151133_ar)));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151131_as), new AspectList(new ItemStack(Items.field_151133_ar)).add(Aspect.WATER, 20));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151129_at), new AspectList(new ItemStack(Items.field_151133_ar)).add(Aspect.FIRE, 15).add(Aspect.EARTH, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151117_aB),
         new AspectList(new ItemStack(Items.field_151133_ar)).add(Aspect.LIFE, 10).add(Aspect.BEAST, 5).add(Aspect.WATER, 5)
      );
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151067_bt), new AspectList().add(Aspect.CRAFT, 15).add(Aspect.ALCHEMY, 25));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150430_aB), new AspectList().add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150448_aq, 1, 32767), new AspectList().add(Aspect.MOTION, 10));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150319_E, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5).add(Aspect.SENSES, 1));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150318_D, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5).add(Aspect.ENERGY, 1));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150408_cc, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_180387_bt, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_180385_bs, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_180386_br, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_180385_bs, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_180391_bp, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_180392_bq, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150452_aw, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5).add(Aspect.SENSES, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150456_au, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5).add(Aspect.SENSES, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150445_bS, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5).add(Aspect.SENSES, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150443_bT, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5).add(Aspect.SENSES, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150442_at, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150331_J, 1, 32767), new AspectList().add(Aspect.MECHANISM, 10).add(Aspect.MOTION, 10));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150320_F, 1, 32767), new AspectList().add(Aspect.MECHANISM, 10).add(Aspect.MOTION, 10));
      ThaumcraftApi.registerComplexObjectTag(
         new ItemStack(Blocks.field_150421_aI), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MECHANISM, 10).add(Aspect.AIR, 15)
      );
      ThaumcraftApi.registerComplexObjectTag(
         new ItemStack(Blocks.field_150323_B), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MECHANISM, 10).add(Aspect.AIR, 15)
      );
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150415_aT, 1, 32767), new AspectList().add(Aspect.MOTION, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150460_al, 1, 32767), new AspectList().add(Aspect.FIRE, 10));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150381_bn), new AspectList().add(Aspect.MAGIC, 25).add(Aspect.CRAFT, 15));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150462_ai), new AspectList().add(Aspect.CRAFT, 20));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151113_aN), new AspectList().add(Aspect.MECHANISM, 10));
      ThaumcraftApi.registerComplexObjectTag(
         new ItemStack(Blocks.field_150461_bJ), new AspectList().add(Aspect.AURA, 10).add(Aspect.MAGIC, 10).add(Aspect.EXCHANGE, 10)
      );
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.field_150471_bO, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151146_bM, 1, 32767), new AspectList().add(Aspect.MOTION, 5).add(Aspect.DESIRE, 10));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151162_bE), new AspectList().add(Aspect.VOID, 5).add(Aspect.PLANT, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.field_151150_bK), new AspectList().add(Aspect.SENSES, 10).add(Aspect.ALCHEMY, 5));
      ThaumcraftApi.registerComplexObjectTag(
         new ItemStack(Blocks.field_150477_bB, 1, 32767), new AspectList().merge(Aspect.EXCHANGE, 10).merge(Aspect.MOTION, 10).merge(Aspect.VOID, 20)
      );
      ThaumcraftApi.registerComplexObjectTag(
         new ItemStack(Items.field_151132_bS, 1, 32767), new AspectList().merge(Aspect.MECHANISM, 15).merge(Aspect.ORDER, 5).merge(Aspect.SENSES, 5)
      );
      ThaumcraftApi.registerComplexObjectTag(
         new ItemStack(Items.field_151107_aW, 1, 32767), new AspectList().merge(Aspect.MECHANISM, 15).merge(Aspect.ENERGY, 10)
      );
      ThaumcraftApi.registerComplexObjectTag(
         new ItemStack(Blocks.field_150438_bZ, 1, 32767), new AspectList().merge(Aspect.MECHANISM, 5).merge(Aspect.EXCHANGE, 10).merge(Aspect.VOID, 5)
      );
      ThaumcraftApi.registerComplexObjectTag(
         new ItemStack(Blocks.field_150409_cd, 1, 32767), new AspectList().merge(Aspect.MECHANISM, 5).merge(Aspect.EXCHANGE, 10).merge(Aspect.VOID, 5)
      );
      ThaumcraftApi.registerComplexObjectTag(
         new ItemStack(Blocks.field_150367_z, 1, 32767), new AspectList().merge(Aspect.MECHANISM, 5).merge(Aspect.EXCHANGE, 10).merge(Aspect.VOID, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Blocks.field_150479_bC, 1, 32767), new AspectList().add(Aspect.SENSES, 5).add(Aspect.MECHANISM, 5).add(Aspect.TRAP, 5)
      );
      ThaumcraftApi.registerComplexObjectTag(
         new ItemStack(Blocks.field_150473_bD, 1, 32767), new AspectList().merge(Aspect.SENSES, 5).merge(Aspect.MECHANISM, 5).merge(Aspect.TRAP, 5)
      );
      ThaumcraftApi.registerComplexObjectTag(
         new ItemStack(Blocks.field_150453_bW, 1, 32767), new AspectList().merge(Aspect.SENSES, 10).merge(Aspect.LIGHT, 10).merge(Aspect.MECHANISM, 5)
      );
      ThaumcraftApi.registerComplexObjectTag("gear*", new AspectList().add(Aspect.MECHANISM, 5));

      for (PotionType potiontype : PotionType.field_185176_a) {
         ItemStack stack = PotionUtils.func_185188_a(new ItemStack(Items.field_151068_bn), potiontype);
         ThaumcraftApi.registerObjectTag(stack, getPotionAspects(stack).add(Aspect.WATER, 5));
         ItemStack stack2 = PotionUtils.func_185188_a(new ItemStack(Items.field_185167_i), potiontype);
         ThaumcraftApi.registerObjectTag(stack2, getPotionAspects(stack2).add(Aspect.AVERSION, 5));
         ItemStack stack3 = PotionUtils.func_185188_a(new ItemStack(Items.field_185155_bH), potiontype);
         ThaumcraftApi.registerObjectTag(stack3, getPotionAspects(stack3).add(Aspect.ENERGY, 5));
         ItemStack stack4 = PotionUtils.func_185188_a(new ItemStack(Items.field_185156_bI), potiontype);
         ThaumcraftApi.registerObjectTag(stack4, getPotionAspects(stack4).add(Aspect.TRAP, 5));
      }

      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151100_aR, 1, 0), new AspectList().add(Aspect.WATER, 2).add(Aspect.BEAST, 2));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151100_aR, 1, 2), new AspectList(new ItemStack(Blocks.field_150434_aF)).add(Aspect.WATER, 1).add(Aspect.FIRE, 1)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151100_aR, 1, 3), new AspectList().add(Aspect.DESIRE, 2).add(Aspect.ENERGY, 2));
      ThaumcraftApi.registerObjectTag(new ItemStack(Items.field_151100_aR, 1, 4), new AspectList().add(Aspect.EARTH, 2).add(Aspect.DESIRE, 2));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(Items.field_151100_aR, 1, 15), new AspectList().add(Aspect.LIFE, 2).add(Aspect.DEATH, 1).add(Aspect.PLANT, 1)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.phial, 1, 0), new AspectList().add(Aspect.VOID, 3));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.phial, 1, 1), new AspectList());
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.grassAmbient), new AspectList(new ItemStack(Blocks.field_150349_c)).add(Aspect.LIGHT, 5));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(BlocksTC.tableWood), new AspectList().add(Aspect.TOOL, 1));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(BlocksTC.tableStone), new AspectList().add(Aspect.TOOL, 1));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(BlocksTC.arcaneWorkbench), new AspectList(new ItemStack(Blocks.field_150462_ai)).add(Aspect.MAGIC, 5).add(Aspect.AURA, 5)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.tripleMeatTreat), new AspectList().add(Aspect.LIFE, 10).add(Aspect.DESIRE, 10));
      ThaumcraftApi.registerObjectTag(
         FluidUtil.getFilledBucket(new FluidStack(ConfigBlocks.FluidPure.instance, 1000)),
         new AspectList(new ItemStack(Items.field_151133_ar)).add(Aspect.MIND, 15).add(Aspect.ORDER, 15)
      );
      ThaumcraftApi.registerObjectTag(
         FluidUtil.getFilledBucket(new FluidStack(ConfigBlocks.FluidDeath.instance, 1000)),
         new AspectList(new ItemStack(Items.field_151133_ar)).add(Aspect.DEATH, 15).add(Aspect.ENTROPY, 15)
      );
      ThaumcraftApi.registerObjectTag("clusterIron", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5));
      ThaumcraftApi.registerObjectTag("clusterGold", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5).add(Aspect.DESIRE, 10));
      ThaumcraftApi.registerObjectTag(
         "clusterCinnabar", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5).add(Aspect.ALCHEMY, 5).add(Aspect.DEATH, 5)
      );
      ThaumcraftApi.registerObjectTag("clusterQuartz", new AspectList().add(Aspect.ORDER, 5).add(Aspect.CRYSTAL, 10));
      ThaumcraftApi.registerObjectTag("oreCinnabar", new AspectList().add(Aspect.EARTH, 5).add(Aspect.METAL, 10).add(Aspect.ALCHEMY, 5).add(Aspect.DEATH, 5));
      ThaumcraftApi.registerObjectTag("oreAmber", new AspectList().add(Aspect.EARTH, 5).add(Aspect.TRAP, 10).add(Aspect.CRYSTAL, 10));
      ThaumcraftApi.registerObjectTag("quicksilver", new AspectList().add(Aspect.METAL, 10).add(Aspect.DEATH, 5).add(Aspect.ALCHEMY, 5));
      ThaumcraftApi.registerObjectTag("gemAmber", new AspectList().add(Aspect.TRAP, 10).add(Aspect.CRYSTAL, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.nuggets, 1, 10), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ORDER, 5).add(Aspect.METAL, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalAir, 1, 32767), new AspectList().add(Aspect.AIR, 15).add(Aspect.CRYSTAL, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalFire, 1, 32767), new AspectList().add(Aspect.FIRE, 15).add(Aspect.CRYSTAL, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalWater, 1, 32767), new AspectList().add(Aspect.WATER, 15).add(Aspect.CRYSTAL, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalEarth, 1, 32767), new AspectList().add(Aspect.EARTH, 15).add(Aspect.CRYSTAL, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalOrder, 1, 32767), new AspectList().add(Aspect.ORDER, 15).add(Aspect.CRYSTAL, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalEntropy, 1, 32767), new AspectList().add(Aspect.ENTROPY, 15).add(Aspect.CRYSTAL, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalTaint, 1, 32767), new AspectList().add(Aspect.FLUX, 15).add(Aspect.CRYSTAL, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.taintFibre), new AspectList().add(Aspect.PLANT, 5).add(Aspect.FLUX, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.taintCrust), new AspectList().add(Aspect.LIFE, 5).add(Aspect.FLUX, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.taintSoil), new AspectList().add(Aspect.EARTH, 5).add(Aspect.FLUX, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.taintGeyser), new AspectList().add(Aspect.AURA, 5).add(Aspect.WATER, 5).add(Aspect.FLUX, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.taintRock), new AspectList().add(Aspect.EARTH, 10).add(Aspect.FLUX, 5));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(BlocksTC.taintFeature, 1, 0), new AspectList().add(Aspect.AURA, 5).add(Aspect.BEAST, 5).add(Aspect.FLUX, 10)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.taintLog, 1, 0), new AspectList().add(Aspect.PLANT, 5).add(Aspect.FLUX, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.logGreatwood), new AspectList().add(Aspect.PLANT, 20).add(Aspect.LIFE, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.logSilverwood), new AspectList().add(Aspect.PLANT, 20).add(Aspect.AURA, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.leafGreatwood), new AspectList().add(Aspect.PLANT, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.leafSilverwood), new AspectList().add(Aspect.PLANT, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.saplingGreatwood), new AspectList().add(Aspect.PLANT, 15).add(Aspect.LIFE, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.saplingSilverwood), new AspectList().add(Aspect.PLANT, 15).add(Aspect.AURA, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.shimmerleaf), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AURA, 10).add(Aspect.ENERGY, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.cinderpearl), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AURA, 5).add(Aspect.FIRE, 10));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(BlocksTC.vishroom), new AspectList().add(Aspect.PLANT, 2).add(Aspect.DEATH, 1).add(Aspect.MAGIC, 1).add(Aspect.ENTROPY, 1)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.stoneAncient), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ELDRITCH, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.stoneAncientTile), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ELDRITCH, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.stoneAncientRock), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ELDRITCH, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.stoneEldritchTile), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ELDRITCH, 5));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(BlocksTC.stoneAncientDoorway), new AspectList().add(Aspect.METAL, 5).add(Aspect.ELDRITCH, 5).add(Aspect.TRAP, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(BlocksTC.stoneAncientGlyphed), new AspectList().add(Aspect.METAL, 5).add(Aspect.ELDRITCH, 5).add(Aspect.MIND, 5)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.stonePorous), new AspectList().add(Aspect.EARTH, 5).add(Aspect.VOID, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.researchTable, 1, 32767), new AspectList(new ItemStack(BlocksTC.tableWood)).add(Aspect.MIND, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.brain), new AspectList().add(Aspect.LIFE, 5).add(Aspect.MIND, 20).add(Aspect.UNDEAD, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.lootBag, 1, 0), new AspectList().add(Aspect.DESIRE, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.lootBag, 1, 1), new AspectList().add(Aspect.DESIRE, 20));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.lootBag, 1, 2), new AspectList().add(Aspect.DESIRE, 30));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.lootUrnCommon), new AspectList().add(Aspect.DESIRE, 10).add(Aspect.EARTH, 2));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.lootUrnUncommon), new AspectList().add(Aspect.DESIRE, 20).add(Aspect.EARTH, 2));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.lootUrnRare), new AspectList().add(Aspect.DESIRE, 30).add(Aspect.EARTH, 2));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.lootCrateCommon), new AspectList().add(Aspect.DESIRE, 10).add(Aspect.PLANT, 2));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.lootCrateUncommon), new AspectList().add(Aspect.DESIRE, 20).add(Aspect.PLANT, 2));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.lootCrateRare), new AspectList().add(Aspect.DESIRE, 30).add(Aspect.PLANT, 2));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.chunks, 1, 32767), new AspectList().add(Aspect.LIFE, 5).add(Aspect.ENTROPY, 1));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.salisMundus), new AspectList().add(Aspect.MAGIC, 5).add(Aspect.ENERGY, 5));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(BlocksTC.crucible), new AspectList(new ItemStack(Items.field_151066_bu, 1, 32767)).add(Aspect.CRAFT, 20).add(Aspect.ALCHEMY, 20)
      );

      for (Block ca : BlocksTC.candles.values()) {
         ThaumcraftApi.registerComplexObjectTag(new ItemStack(ca), new AspectList().add(Aspect.LIGHT, 5));
      }

      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.thaumonomicon, 1, 32767), new AspectList(new ItemStack(Blocks.field_150342_X)).merge(Aspect.MAGIC, 10)
      );
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(BlocksTC.pedestalArcane, 1, 0), new AspectList().add(Aspect.MAGIC, 3).add(Aspect.AIR, 3));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(BlocksTC.pedestalAncient, 1, 1), new AspectList().add(Aspect.MAGIC, 3).add(Aspect.ELDRITCH, 3));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(BlocksTC.pedestalEldritch, 1, 2), new AspectList().add(Aspect.MAGIC, 3).add(Aspect.ELDRITCH, 3));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(ItemsTC.thaumometer, 1, 32767), new AspectList().add(Aspect.SENSES, 10).add(Aspect.AURA, 10));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(ItemsTC.goggles, 1, 32767), new AspectList().merge(Aspect.SENSES, 10).merge(Aspect.AURA, 10));
      ThaumcraftApi.registerComplexObjectTag(new ItemStack(BlocksTC.arcaneEar), new AspectList().add(Aspect.SENSES, 20));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.amuletVis, 1, 0), new AspectList().add(Aspect.AURA, 20).add(Aspect.METAL, 5).add(Aspect.MAGIC, 5));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.baubles, 1, 3), new AspectList().add(Aspect.AURA, 5).add(Aspect.METAL, 5).add(Aspect.MAGIC, 20));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.crimsonPlateChest, 1, 32767), new AspectList(new ItemStack(Items.field_151030_Z)).add(Aspect.ELDRITCH, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.crimsonPraetorChest, 1, 32767), new AspectList(new ItemStack(Items.field_151030_Z)).add(Aspect.ELDRITCH, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.crimsonRobeChest, 1, 32767), new AspectList(new ItemStack(Items.field_151027_R)).add(Aspect.MAGIC, 5).add(Aspect.ELDRITCH, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.crimsonPlateLegs, 1, 32767), new AspectList(new ItemStack(Items.field_151165_aa)).add(Aspect.ELDRITCH, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.crimsonPraetorLegs, 1, 32767), new AspectList(new ItemStack(Items.field_151165_aa)).add(Aspect.ELDRITCH, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.crimsonRobeLegs, 1, 32767), new AspectList(new ItemStack(Items.field_151026_S)).add(Aspect.MAGIC, 5).add(Aspect.ELDRITCH, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.crimsonPlateHelm, 1, 32767), new AspectList(new ItemStack(Items.field_151028_Y)).add(Aspect.ELDRITCH, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.crimsonPraetorHelm, 1, 32767), new AspectList(new ItemStack(Items.field_151028_Y)).add(Aspect.ELDRITCH, 10)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.crimsonRobeHelm, 1, 32767), new AspectList(new ItemStack(Items.field_151024_Q)).add(Aspect.MAGIC, 5).add(Aspect.ELDRITCH, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.crimsonBoots, 1, 32767), new AspectList(new ItemStack(Items.field_151167_ab)).add(Aspect.ELDRITCH, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.crimsonBlade, 1, 32767), new AspectList(new ItemStack(Items.field_151040_l)).add(Aspect.ELDRITCH, 10).add(Aspect.DEATH, 10)
      );

      for (Block ca : BlocksTC.banners.values()) {
         ThaumcraftApi.registerComplexObjectTag(new ItemStack(ca), new AspectList().add(Aspect.ELDRITCH, 5));
      }

      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.eldritchEye), new AspectList().add(Aspect.ELDRITCH, 15).add(Aspect.AURA, 15).add(Aspect.SENSES, 15).add(Aspect.SOUL, 15)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.curio, 1, 0), new AspectList().add(Aspect.MIND, 15).add(Aspect.MAGIC, 15));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.curio, 1, 1), new AspectList().add(Aspect.MIND, 15).add(Aspect.BEAST, 15));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.curio, 1, 2), new AspectList().add(Aspect.MIND, 15).add(Aspect.DEATH, 15));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.curio, 1, 3), new AspectList().add(Aspect.MIND, 15).add(Aspect.ELDRITCH, 15));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.curio, 1, 4), new AspectList().add(Aspect.MIND, 30));
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.curio, 1, 5), new AspectList().add(Aspect.MIND, 15).add(Aspect.FLUX, 15));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.curio, 1, 6), new AspectList().add(Aspect.MIND, 15).add(Aspect.ELDRITCH, 5).add(Aspect.SOUL, 5).add(Aspect.MAGIC, 5)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.runedTablet), new AspectList().add(Aspect.TRAP, 15).add(Aspect.MIND, 15).add(Aspect.MECHANISM, 15));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.celestialNotes, 1, 32767), new AspectList().add(Aspect.MIND, 5).add(Aspect.DARKNESS, 5).add(Aspect.LIGHT, 5)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(ItemsTC.primordialPearl, 1, 32767),
         new AspectList().add(Aspect.AIR, 10).add(Aspect.FIRE, 10).add(Aspect.WATER, 10).add(Aspect.EARTH, 10).add(Aspect.ORDER, 10).add(Aspect.ENTROPY, 10)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.eldritch, 1, 0), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.eldritch, 1, 1), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.eldritch, 1, 2), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10));
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.eldritch, 1, 3), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(BlocksTC.eldritch, 1, 4), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10).add(Aspect.MECHANISM, 10)
      );
      ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.eldritch, 1, 5), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10));
      ThaumcraftApi.registerObjectTag(
         new ItemStack(BlocksTC.eldritch, 1, 6), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10).add(Aspect.MOTION, 15)
      );
      ThaumcraftApi.registerObjectTag(
         new ItemStack(BlocksTC.eldritch, 1, 7), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10).add(Aspect.BEAST, 15)
      );
   }

   public static AspectList getPotionAspects(ItemStack itemstack) {
      AspectList tmp = new AspectList();
      boolean didit = false;
      PotionType potion = PotionUtils.func_185191_c(itemstack);
      if (potion != PotionTypes.field_185230_b) {
         HashSet<ItemStack> hs = new HashSet<>();
         ThaumcraftCraftingManager.getPotionReagentsRecursive(potion, hs);

         for (ItemStack his : hs) {
            AspectList tmp2 = ThaumcraftCraftingManager.getObjectTags(his);

            for (Aspect a : tmp2.aspects.keySet()) {
               tmp.add(a, tmp2.getAmount(a));
            }

            didit = true;
            tmp.add(Aspect.ALCHEMY, 3);
         }

         try {
            for (Aspect a : tmp.aspects.keySet().toArray(new Aspect[0])) {
               int amt = tmp.getAmount(a);
               tmp.remove(a, (int)(amt * 0.66));
            }
         } catch (Exception var10) {
         }

         if (!didit) {
            tmp.add(Aspect.MAGIC, 5);
            tmp.add(Aspect.ALCHEMY, 5);
         }
      } else {
         tmp.add(Aspect.WATER, 5);
      }

      return tmp;
   }
}
