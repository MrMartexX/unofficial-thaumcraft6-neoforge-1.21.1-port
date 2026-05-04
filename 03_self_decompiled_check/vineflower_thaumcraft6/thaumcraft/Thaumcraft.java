package thaumcraft;

import java.io.File;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.common.lib.CommandThaumcraft;
import thaumcraft.proxies.IProxy;

@Mod(
   modid = "thaumcraft",
   name = "Thaumcraft",
   version = "6.1.BETA26",
   dependencies = "required-after:forge@[14.23.5.2768,);required-after:baubles@[1.5.2,)",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class Thaumcraft {
   public static final String MODID = "thaumcraft";
   public static final String MODNAME = "Thaumcraft";
   public static final String VERSION = "6.1.BETA26";
   @SidedProxy(clientSide = "thaumcraft.proxies.ClientProxy", serverSide = "thaumcraft.proxies.ServerProxy")
   public static IProxy proxy;
   @Instance("thaumcraft")
   public static Thaumcraft instance;
   public File modDir;
   public static final Logger log = LogManager.getLogger("thaumcraft".toUpperCase());

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }

   @EventHandler
   public void serverLoad(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandThaumcraft());
   }

   @EventHandler
   public void interModComs(IMCEvent event) {
      proxy.checkInterModComs(event);
   }

   @SubscribeEvent
   public void onConfigChangedEvent(OnConfigChangedEvent event) {
      if (event.getModID().equals("thaumcraft")) {
         ConfigManager.sync("thaumcraft", Type.INSTANCE);
      }
   }

   static {
      FluidRegistry.enableUniversalBucket();
   }
}
