package thaumcraft.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import thaumcraft.client.ColorHandler;
import thaumcraft.client.lib.ender.ShaderHelper;
import thaumcraft.common.lib.events.KeyHandler;

public class ClientProxy extends CommonProxy {
   ProxyEntities proxyEntities = new ProxyEntities();
   ProxyTESR proxyTESR = new ProxyTESR();
   KeyHandler kh = new KeyHandler();

   @Override
   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      OBJLoader.INSTANCE.addDomain("thaumcraft".toLowerCase());
      ShaderHelper.initShaders();
   }

   @Override
   public void init(FMLInitializationEvent event) {
      super.init(event);
      ColorHandler.registerColourHandlers();
      this.registerKeyBindings();
      this.proxyEntities.setupEntityRenderers();
      this.proxyTESR.setupTESR();
   }

   @Override
   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
   }

   public void registerKeyBindings() {
      MinecraftForge.EVENT_BUS.register(this.kh);
   }

   @Override
   public World getClientWorld() {
      return FMLClientHandler.instance().getClient().field_71441_e;
   }

   @Override
   public World getWorld(int dim) {
      return this.getClientWorld();
   }

   @Override
   public boolean getSingleplayer() {
      return Minecraft.func_71410_x().func_71356_B();
   }

   @Override
   public boolean isShiftKeyDown() {
      return GuiScreen.func_146272_n();
   }

   public void setOtherBlockRenderers() {
   }

   @Override
   public void registerModel(ItemBlock itemBlock) {
      ModelLoader.setCustomModelResourceLocation(itemBlock, 0, new ModelResourceLocation(itemBlock.getRegistryName(), "inventory"));
   }
}
