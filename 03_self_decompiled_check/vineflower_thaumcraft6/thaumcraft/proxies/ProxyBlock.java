package thaumcraft.proxies;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.renderers.block.CrystalModel;
import thaumcraft.common.blocks.world.ore.ShardType;

public class ProxyBlock {
   static ModelResourceLocation[] crystals = new ModelResourceLocation[ShardType.values().length];
   static ModelResourceLocation[] jars = new ModelResourceLocation[4];
   static ModelResourceLocation[] jarsVoid = new ModelResourceLocation[4];
   static ModelResourceLocation fibres;
   private static ModelResourceLocation fluidGooLocation = new ModelResourceLocation("thaumcraft:flux_goo", "fluid");
   private static ModelResourceLocation fluidDeathLocation = new ModelResourceLocation("thaumcraft:liquid_death", "fluid");
   private static ModelResourceLocation fluidPureLocation = new ModelResourceLocation("thaumcraft:purifying_fluid", "fluid");

   public static void setupBlocksClient(IForgeRegistry<Block> iForgeRegistry) {
      ModelLoader.setCustomModelResourceLocation(
         Item.func_150898_a(BlocksTC.slabAncient), 0, new ModelResourceLocation(new ResourceLocation("thaumcraft:slab_ancient"), "half=bottom,variant=default")
      );
      ModelLoader.setCustomModelResourceLocation(
         Item.func_150898_a(BlocksTC.slabArcaneStone),
         0,
         new ModelResourceLocation(new ResourceLocation("thaumcraft:slab_arcane_stone"), "half=bottom,variant=default")
      );
      ModelLoader.setCustomModelResourceLocation(
         Item.func_150898_a(BlocksTC.slabArcaneBrick),
         0,
         new ModelResourceLocation(new ResourceLocation("thaumcraft:slab_arcane_brick"), "half=bottom,variant=default")
      );
      ModelLoader.setCustomModelResourceLocation(
         Item.func_150898_a(BlocksTC.slabEldritch),
         0,
         new ModelResourceLocation(new ResourceLocation("thaumcraft:slab_eldritch"), "half=bottom,variant=default")
      );
      ModelLoader.setCustomModelResourceLocation(
         Item.func_150898_a(BlocksTC.slabGreatwood),
         0,
         new ModelResourceLocation(new ResourceLocation("thaumcraft:slab_greatwood"), "half=bottom,variant=default")
      );
      ModelLoader.setCustomModelResourceLocation(
         Item.func_150898_a(BlocksTC.slabSilverwood),
         0,
         new ModelResourceLocation(new ResourceLocation("thaumcraft:slab_silverwood"), "half=bottom,variant=default")
      );

      for (int a = 0; a < ShardType.values().length; a++) {
         crystals[a] = new ModelResourceLocation(iForgeRegistry.getKey(ShardType.values()[a].getOre()), "normal");
         final ModelResourceLocation mrl = crystals[a];
         ModelLoader.setCustomStateMapper(ShardType.values()[a].getOre(), new StateMapperBase() {
            protected ModelResourceLocation func_178132_a(IBlockState p_178132_1_) {
               return mrl;
            }
         });
      }

      for (Block b : BlocksTC.banners.values()) {
         ModelLoader.setCustomModelResourceLocation(Item.func_150898_a(b), 0, new ModelResourceLocation(new ResourceLocation("thaumcraft:banner"), "inventory"));
      }

      ModelLoader.setCustomModelResourceLocation(
         Item.func_150898_a(BlocksTC.bannerCrimsonCult), 0, new ModelResourceLocation(new ResourceLocation("thaumcraft:banner_crimson_cult"), "inventory")
      );

      for (Block b : BlocksTC.nitor.values()) {
         ModelLoader.setCustomModelResourceLocation(Item.func_150898_a(b), 0, new ModelResourceLocation(new ResourceLocation("thaumcraft:nitor"), "inventory"));
      }

      ModelBakery.registerItemVariants(
         Item.func_150898_a(BlocksTC.mirror), new ResourceLocation[]{new ResourceLocation("thaumcraft:mirror"), new ResourceLocation("thaumcraft:mirror_on")}
      );
      ModelBakery.registerItemVariants(
         Item.func_150898_a(BlocksTC.mirrorEssentia),
         new ResourceLocation[]{new ResourceLocation("thaumcraft:mirror_essentia"), new ResourceLocation("thaumcraft:mirror_essentia_on")}
      );
      ModelLoader.setCustomModelResourceLocation(
         Item.func_150898_a(BlocksTC.mirror), 1, new ModelResourceLocation(new ResourceLocation("thaumcraft:mirror_on"), "inventory")
      );
      ModelLoader.setCustomModelResourceLocation(
         Item.func_150898_a(BlocksTC.mirrorEssentia), 1, new ModelResourceLocation(new ResourceLocation("thaumcraft:mirror_essentia_on"), "inventory")
      );
      Item fluxGooItem = Item.func_150898_a(BlocksTC.fluxGoo);
      ModelBakery.registerItemVariants(fluxGooItem, new ResourceLocation[0]);
      ModelLoader.setCustomMeshDefinition(fluxGooItem, new ItemMeshDefinition() {
         public ModelResourceLocation func_178113_a(ItemStack stack) {
            return ProxyBlock.fluidGooLocation;
         }
      });
      ModelLoader.setCustomStateMapper(BlocksTC.fluxGoo, new StateMapperBase() {
         protected ModelResourceLocation func_178132_a(IBlockState state) {
            return ProxyBlock.fluidGooLocation;
         }
      });
      Item liquidDeathItem = Item.func_150898_a(BlocksTC.liquidDeath);
      ModelBakery.registerItemVariants(liquidDeathItem, new ResourceLocation[0]);
      ModelLoader.setCustomMeshDefinition(liquidDeathItem, new ItemMeshDefinition() {
         public ModelResourceLocation func_178113_a(ItemStack stack) {
            return ProxyBlock.fluidDeathLocation;
         }
      });
      ModelLoader.setCustomStateMapper(BlocksTC.liquidDeath, new StateMapperBase() {
         protected ModelResourceLocation func_178132_a(IBlockState state) {
            return ProxyBlock.fluidDeathLocation;
         }
      });
      Item purifyingFluidItem = Item.func_150898_a(BlocksTC.purifyingFluid);
      ModelBakery.registerItemVariants(purifyingFluidItem, new ResourceLocation[0]);
      ModelLoader.setCustomMeshDefinition(purifyingFluidItem, new ItemMeshDefinition() {
         public ModelResourceLocation func_178113_a(ItemStack stack) {
            return ProxyBlock.fluidPureLocation;
         }
      });
      ModelLoader.setCustomStateMapper(BlocksTC.purifyingFluid, new StateMapperBase() {
         protected ModelResourceLocation func_178132_a(IBlockState state) {
            return ProxyBlock.fluidPureLocation;
         }
      });
   }

   @EventBusSubscriber(Side.CLIENT)
   public static class BakeBlockEventHandler {
      @SubscribeEvent
      public static void onModelBakeEvent(ModelBakeEvent event) {
         TextureAtlasSprite crystalTexture = Minecraft.func_71410_x().func_147117_R().func_110572_b("thaumcraft:blocks/crystal");
         IBakedModel customCrystalModel = new CrystalModel(crystalTexture);

         for (int a = 0; a < ShardType.values().length; a++) {
            event.getModelRegistry().func_82595_a(ProxyBlock.crystals[a], customCrystalModel);
         }
      }
   }
}
