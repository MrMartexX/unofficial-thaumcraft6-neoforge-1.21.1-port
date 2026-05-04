package thaumcraft.proxies;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.BehaviorDispenseAlumetum;
import thaumcraft.common.lib.InternalMethodHandler;
import thaumcraft.common.lib.capabilities.PlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerWarp;
import thaumcraft.common.lib.events.CraftingEvents;
import thaumcraft.common.lib.events.WorldEvents;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.CropUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.world.ThaumcraftWorldGenerator;
import thaumcraft.common.world.biomes.BiomeHandler;

public class CommonProxy implements IGuiHandler, IProxy {
   ProxyGUI proxyGUI = new ProxyGUI();

   @Override
   public void preInit(FMLPreInitializationEvent event) {
      event.getModMetadata().version = "6.1.BETA26";
      Thaumcraft.instance.modDir = event.getModConfigurationDirectory();
      ThaumcraftApi.internalMethods = new InternalMethodHandler();
      PlayerKnowledge.preInit();
      PlayerWarp.preInit();
      PacketHandler.preInit();
      MinecraftForge.TERRAIN_GEN_BUS.register(WorldEvents.INSTANCE);
      GameRegistry.registerFuelHandler(new CraftingEvents());
      GameRegistry.registerWorldGenerator(ThaumcraftWorldGenerator.INSTANCE, 0);
      MinecraftForge.EVENT_BUS.register(Thaumcraft.instance);
   }

   @Override
   public void init(FMLInitializationEvent event) {
      ConfigItems.init();
      BlockDispenser.field_149943_a.func_82595_a(ItemsTC.alumentum, new BehaviorDispenseAlumetum());
      NetworkRegistry.INSTANCE.registerGuiHandler(Thaumcraft.instance, this);
      ConfigResearch.init();
      ConfigManager.sync("thaumcraft", Type.INSTANCE);
      ConfigRecipes.initializeSmelting();
   }

   @Override
   public void postInit(FMLPostInitializationEvent event) {
      ConfigEntities.postInitEntitySpawns();
      ConfigAspects.postInit();
      ConfigRecipes.postAspects();
      ModConfig.postInitLoot();
      ModConfig.postInitMisc();
      ConfigRecipes.compileGroups();
      ConfigResearch.postInit();
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      return this.proxyGUI.getClientGuiElement(ID, player, world, x, y, z);
   }

   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      return this.proxyGUI.getServerGuiElement(ID, player, world, x, y, z);
   }

   @Override
   public boolean isShiftKeyDown() {
      return false;
   }

   @Override
   public World getClientWorld() {
      return null;
   }

   @Override
   public void registerModel(ItemBlock itemBlock) {
   }

   @Override
   public void checkInterModComs(IMCEvent event) {
      UnmodifiableIterator var2 = event.getMessages().iterator();

      while (var2.hasNext()) {
         IMCMessage message = (IMCMessage)var2.next();
         if (message.key.equals("portableHoleBlacklist") && message.isStringMessage()) {
            BlockUtils.portableHoleBlackList.add(message.getStringValue());
         }

         if (message.key.equals("harvestStandardCrop") && message.isItemStackMessage()) {
            ItemStack crop = message.getItemStackValue();
            CropUtils.addStandardCrop(crop, crop.func_77952_i());
         }

         if (message.key.equals("harvestClickableCrop") && message.isItemStackMessage()) {
            ItemStack crop = message.getItemStackValue();
            CropUtils.addClickableCrop(crop, crop.func_77952_i());
         }

         if (message.key.equals("harvestStackedCrop") && message.isItemStackMessage()) {
            ItemStack crop = message.getItemStackValue();
            CropUtils.addStackedCrop(crop, crop.func_77952_i());
         }

         if (message.key.equals("nativeCluster") && message.isStringMessage()) {
            String[] t = message.getStringValue().split(",");
            if (t != null && t.length == 5) {
               try {
                  ItemStack ore = new ItemStack(Item.func_150899_d(Integer.parseInt(t[0])), 1, Integer.parseInt(t[1]));
                  ItemStack cluster = new ItemStack(Item.func_150899_d(Integer.parseInt(t[2])), 1, Integer.parseInt(t[3]));
                  Utils.addSpecialMiningResult(ore, cluster, Float.parseFloat(t[4]));
               } catch (Exception var7) {
               }
            }
         }

         if (message.key.equals("lampBlacklist") && message.isItemStackMessage()) {
            ItemStack crop = message.getItemStackValue();
            CropUtils.blacklistLamp(crop, crop.func_77952_i());
         }

         if (message.key.equals("dimensionBlacklist") && message.isStringMessage()) {
            String[] t = message.getStringValue().split(":");
            if (t != null && t.length == 2) {
               try {
                  BiomeHandler.addDimBlacklist(Integer.parseInt(t[0]), Integer.parseInt(t[1]));
               } catch (Exception var8) {
               }
            }
         }

         if (message.key.equals("biomeBlacklist") && message.isStringMessage()) {
            String[] t = message.getStringValue().split(":");
            if (t != null && t.length == 2 && Biome.func_150568_d(Integer.parseInt(t[0])) != null) {
               try {
                  BiomeHandler.addBiomeBlacklist(Integer.parseInt(t[0]), Integer.parseInt(t[1]));
               } catch (Exception var9) {
               }
            }
         }

         if (message.key.equals("championWhiteList") && message.isStringMessage()) {
            try {
               String[] t = message.getStringValue().split(":");
               Class oclass = EntityList.func_192839_a(t[0]);
               if (oclass != null) {
                  ConfigEntities.championModWhitelist.put(oclass, Integer.parseInt(t[1]));
               }
            } catch (Exception e) {
               Thaumcraft.log.error("Failed to Whitelist [" + message.getStringValue() + "] with [ championWhiteList ] message.");
            }
         }
      }
   }

   @Override
   public World getWorld(int dim) {
      return null;
   }

   @Override
   public boolean getSingleplayer() {
      return false;
   }
}
