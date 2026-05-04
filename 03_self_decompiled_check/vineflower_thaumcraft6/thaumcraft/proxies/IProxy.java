package thaumcraft.proxies;

import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;

public interface IProxy {
   void preInit(FMLPreInitializationEvent var1);

   void init(FMLInitializationEvent var1);

   void postInit(FMLPostInitializationEvent var1);

   World getClientWorld();

   boolean getSingleplayer();

   World getWorld(int var1);

   boolean isShiftKeyDown();

   void registerModel(ItemBlock var1);

   void checkInterModComs(IMCEvent var1);
}
