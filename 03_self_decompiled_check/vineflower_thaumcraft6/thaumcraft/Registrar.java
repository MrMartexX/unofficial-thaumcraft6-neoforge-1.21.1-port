package thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.potions.PotionBlurredVision;
import thaumcraft.common.lib.potions.PotionDeathGaze;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;
import thaumcraft.common.lib.potions.PotionSunScorned;
import thaumcraft.common.lib.potions.PotionThaumarhia;
import thaumcraft.common.lib.potions.PotionUnnaturalHunger;
import thaumcraft.common.lib.potions.PotionWarpWard;
import thaumcraft.common.world.biomes.BiomeGenEerie;
import thaumcraft.common.world.biomes.BiomeGenEldritch;
import thaumcraft.common.world.biomes.BiomeGenMagicalForest;
import thaumcraft.common.world.biomes.BiomeHandler;
import thaumcraft.proxies.ProxyBlock;

@EventBusSubscriber
public final class Registrar {
   @SubscribeEvent
   public static void registerBlocks(Register<Block> event) {
      ConfigBlocks.initBlocks(event.getRegistry());
      ConfigBlocks.initTileEntities();
      ConfigBlocks.initMisc();
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void registerBlocksClient(Register<Block> event) {
      ProxyBlock.setupBlocksClient(event.getRegistry());
   }

   @SubscribeEvent
   public static void registerItems(Register<Item> event) {
      ConfigItems.preInitSeals();
      ConfigItems.initItems(event.getRegistry());
      ConfigItems.initMisc();
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void registerItemsClient(Register<Item> event) {
      ConfigItems.initModelsAndVariants();
   }

   @SubscribeEvent
   public static void registerEntities(Register<EntityEntry> event) {
      ConfigEntities.initEntities(event.getRegistry());
   }

   @SubscribeEvent
   public static void registerVanillaRecipes(Register<IRecipe> event) {
      ModConfig.modCompatibility();
      ConfigRecipes.initializeNormalRecipes(event.getRegistry());
      ConfigRecipes.initializeArcaneRecipes(event.getRegistry());
      ConfigRecipes.initializeInfusionRecipes();
      ConfigRecipes.initializeAlchemyRecipes();
      ConfigRecipes.initializeCompoundRecipes();
   }

   @SubscribeEvent
   public static void registerPotions(Register<Potion> event) {
      PotionFluxTaint.instance = (Potion)new PotionFluxTaint(true, 6697847).setRegistryName("fluxTaint");
      PotionVisExhaust.instance = (Potion)new PotionVisExhaust(true, 6702199).setRegistryName("visExhaust");
      PotionInfectiousVisExhaust.instance = (Potion)new PotionInfectiousVisExhaust(true, 6706551).setRegistryName("infectiousVisExhaust");
      PotionUnnaturalHunger.instance = (Potion)new PotionUnnaturalHunger(true, 4482611).setRegistryName("unnaturalHunger");
      PotionWarpWard.instance = (Potion)new PotionWarpWard(false, 14742263).setRegistryName("warpWard");
      PotionDeathGaze.instance = (Potion)new PotionDeathGaze(true, 6702131).setRegistryName("deathGaze");
      PotionBlurredVision.instance = (Potion)new PotionBlurredVision(true, 8421504).setRegistryName("blurredVision");
      PotionSunScorned.instance = (Potion)new PotionSunScorned(true, 16308330).setRegistryName("sunScorned");
      PotionThaumarhia.instance = (Potion)new PotionThaumarhia(true, 6702199).setRegistryName("thaumarhia");
      event.getRegistry().register(PotionFluxTaint.instance);
      event.getRegistry().register(PotionVisExhaust.instance);
      event.getRegistry().register(PotionInfectiousVisExhaust.instance);
      event.getRegistry().register(PotionUnnaturalHunger.instance);
      event.getRegistry().register(PotionWarpWard.instance);
      event.getRegistry().register(PotionDeathGaze.instance);
      event.getRegistry().register(PotionBlurredVision.instance);
      event.getRegistry().register(PotionSunScorned.instance);
      event.getRegistry().register(PotionThaumarhia.instance);
   }

   @SubscribeEvent
   public static void registerBiomes(Register<Biome> event) {
      BiomeHandler.MAGICAL_FOREST = new BiomeGenMagicalForest(
         new BiomeProperties("Magical Forest").func_185398_c(0.2F).func_185400_d(0.3F).func_185410_a(0.8F).func_185395_b(0.4F)
      );
      event.getRegistry().register(BiomeHandler.MAGICAL_FOREST);
      BiomeHandler.EERIE = new BiomeGenEerie(new BiomeProperties("Eerie").func_185398_c(0.125F).func_185400_d(0.4F).func_185410_a(0.8F).func_185396_a());
      event.getRegistry().register(BiomeHandler.EERIE);
      BiomeHandler.ELDRITCH = new BiomeGenEldritch(
         new BiomeProperties("Outer Lands").func_185398_c(0.125F).func_185400_d(0.15F).func_185410_a(0.8F).func_185395_b(0.2F)
      );
      event.getRegistry().register(BiomeHandler.ELDRITCH);
      BiomeHandler.registerBiomes();
      if (ModConfig.CONFIG_WORLD.generateMagicForest) {
         BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(BiomeHandler.MAGICAL_FOREST, ModConfig.CONFIG_WORLD.biomeMagicalForestWeight));
         BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(BiomeHandler.MAGICAL_FOREST, ModConfig.CONFIG_WORLD.biomeMagicalForestWeight));
      }
   }

   @SubscribeEvent
   public static void registerSounds(Register<SoundEvent> event) {
      SoundsTC.registerSounds(event);
      SoundsTC.registerSoundTypes();
   }
}
