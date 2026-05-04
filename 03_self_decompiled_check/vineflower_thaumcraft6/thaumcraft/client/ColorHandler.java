package thaumcraft.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.devices.BlockInlay;
import thaumcraft.common.blocks.devices.BlockStabilizer;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.blocks.essentia.BlockTube;
import thaumcraft.common.blocks.world.ore.BlockCrystal;
import thaumcraft.common.golems.GolemProperties;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.tiles.devices.TileStabilizer;
import thaumcraft.common.tiles.essentia.TileTubeFilter;

@SideOnly(Side.CLIENT)
public class ColorHandler {
   public static void registerColourHandlers() {
      BlockColors blockColors = Minecraft.func_71410_x().func_184125_al();
      ItemColors itemColors = Minecraft.func_71410_x().getItemColors();
      registerBlockColourHandlers(blockColors);
      registerItemColourHandlers(blockColors, itemColors);
   }

   private static void registerBlockColourHandlers(BlockColors blockColors) {
      IBlockColor basicColourHandler = (state, blockAccess, pos, tintIndex) -> state.func_177230_c().func_180659_g(state, blockAccess, pos).field_76291_p;
      Block[] basicBlocks = new Block[BlocksTC.candles.size() + BlocksTC.banners.size() + BlocksTC.nitor.size()];
      int i = 0;

      for (Block b : BlocksTC.candles.values()) {
         basicBlocks[i] = b;
         i++;
      }

      for (Block b : BlocksTC.banners.values()) {
         basicBlocks[i] = b;
         i++;
      }

      for (Block b : BlocksTC.nitor.values()) {
         basicBlocks[i] = b;
         i++;
      }

      blockColors.func_186722_a(basicColourHandler, basicBlocks);
      IBlockColor grassColourHandler = (state, blockAccess, pos, tintIndex) -> blockAccess != null && pos != null
         ? BiomeColorHelper.func_180286_a(blockAccess, pos)
         : ColorizerGrass.func_77480_a(0.5, 1.0);
      blockColors.func_186722_a(grassColourHandler, new Block[]{BlocksTC.grassAmbient});
      IBlockColor leafColourHandler = (state, blockAccess, pos, tintIndex) -> {
         if (state.func_177230_c() == BlocksTC.leafSilverwood) {
            return 16777215;
         } else {
            return blockAccess != null && pos != null ? BiomeColorHelper.func_180287_b(blockAccess, pos) : ColorizerFoliage.func_77468_c();
         }
      };
      blockColors.func_186722_a(leafColourHandler, new Block[]{BlocksTC.leafGreatwood, BlocksTC.leafSilverwood});
      IBlockColor crystalColourHandler = (state, blockAccess, pos, tintIndex) -> state.func_177230_c() instanceof BlockCrystal
         ? ((BlockCrystal)state.func_177230_c()).aspect.getColor()
         : 16777215;
      blockColors.func_186722_a(
         crystalColourHandler,
         new Block[]{
            BlocksTC.crystalAir,
            BlocksTC.crystalEarth,
            BlocksTC.crystalFire,
            BlocksTC.crystalWater,
            BlocksTC.crystalEntropy,
            BlocksTC.crystalOrder,
            BlocksTC.crystalTaint
         }
      );
      IBlockColor tubeFilterColourHandler = (state, blockAccess, pos, tintIndex) -> {
         if (state.func_177230_c() instanceof BlockTube && tintIndex == 1) {
            TileEntity te = blockAccess.func_175625_s(pos);
            if (te != null && te instanceof TileTubeFilter && ((TileTubeFilter)te).aspectFilter != null) {
               return ((TileTubeFilter)te).aspectFilter.getColor();
            }
         }

         return 16777215;
      };
      blockColors.func_186722_a(tubeFilterColourHandler, new Block[]{BlocksTC.tubeFilter});
      IBlockColor inlayColourHandler = (state, blockAccess, pos, tintIndex) -> {
         if (state.func_177230_c() instanceof BlockInlay && tintIndex == 0) {
            BlockInlay var10000 = (BlockInlay)state.func_177230_c();
            return BlockInlay.colorMultiplier(state.func_177230_c().func_176201_c(state));
         } else {
            return 16777215;
         }
      };
      blockColors.func_186722_a(inlayColourHandler, new Block[]{BlocksTC.inlay});
      IBlockColor stabilizerColourHandler = (state, blockAccess, pos, tintIndex) -> {
         if (state.func_177230_c() instanceof BlockStabilizer && tintIndex == 0) {
            int charge = 0;
            TileEntity te = blockAccess.func_175625_s(pos);
            if (te != null && te instanceof TileStabilizer) {
               charge = ((TileStabilizer)te).getEnergy();
            }

            BlockStabilizer var10000 = (BlockStabilizer)state.func_177230_c();
            return BlockStabilizer.colorMultiplier(charge);
         } else {
            return 16777215;
         }
      };
      blockColors.func_186722_a(stabilizerColourHandler, new Block[]{BlocksTC.stabilizer});
   }

   private static void registerItemColourHandlers(BlockColors blockColors, ItemColors itemColors) {
      IItemColor itemBlockColourHandler = (stack, tintIndex) -> {
         IBlockState state = ((ItemBlock)stack.func_77973_b()).func_179223_d().func_176203_a(stack.func_77960_j());
         return blockColors.func_186724_a(state, null, null, tintIndex);
      };
      Block[] basicBlocks = new Block[BlocksTC.candles.size() + BlocksTC.nitor.size() + 3];
      int i = 0;

      for (Block b : BlocksTC.candles.values()) {
         basicBlocks[i] = b;
         i++;
      }

      for (Block b : BlocksTC.nitor.values()) {
         basicBlocks[i] = b;
         i++;
      }

      basicBlocks[i] = BlocksTC.leafGreatwood;
      basicBlocks[++i] = BlocksTC.leafSilverwood;
      basicBlocks[++i] = BlocksTC.grassAmbient;
      i++;
      itemColors.func_186731_a(itemBlockColourHandler, basicBlocks);
      IItemColor itemEssentiaColourHandler = (stack, tintIndex) -> {
         ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer)stack.func_77973_b();

         try {
            if (item != null && item.getAspects(stack) != null) {
               return item.getAspects(stack).getAspects()[0].getColor();
            }
         } catch (Exception var4x) {
         }

         return 16777215;
      };
      itemColors.func_186730_a(itemEssentiaColourHandler, new Item[]{ItemsTC.crystalEssence});
      IItemColor itemJarColourHandler = (stack, tintIndex) -> {
         BlockJarItem item = (BlockJarItem)stack.func_77973_b();

         try {
            if (item.getAspects(stack) != null && tintIndex == 1) {
               return item.getAspects(stack).getAspects()[0].getColor();
            }
         } catch (Exception var4x) {
         }

         return 16777215;
      };
      itemColors.func_186731_a(itemJarColourHandler, new Block[]{BlocksTC.jarNormal});
      itemColors.func_186731_a(itemJarColourHandler, new Block[]{BlocksTC.jarVoid});
      IItemColor itemCrystalPlanterColourHandler = (stack, tintIndex) -> {
         Item item = stack.func_77973_b();
         return item instanceof ItemBlock && ((ItemBlock)item).func_179223_d() instanceof BlockCrystal
            ? ((BlockCrystal)((ItemBlock)item).func_179223_d()).aspect.getColor()
            : 16777215;
      };
      itemColors.func_186731_a(itemCrystalPlanterColourHandler, new Block[]{BlocksTC.crystalAir});
      itemColors.func_186731_a(itemCrystalPlanterColourHandler, new Block[]{BlocksTC.crystalEarth});
      itemColors.func_186731_a(itemCrystalPlanterColourHandler, new Block[]{BlocksTC.crystalFire});
      itemColors.func_186731_a(itemCrystalPlanterColourHandler, new Block[]{BlocksTC.crystalWater});
      itemColors.func_186731_a(itemCrystalPlanterColourHandler, new Block[]{BlocksTC.crystalEntropy});
      itemColors.func_186731_a(itemCrystalPlanterColourHandler, new Block[]{BlocksTC.crystalOrder});
      itemColors.func_186731_a(itemCrystalPlanterColourHandler, new Block[]{BlocksTC.crystalTaint});
      IItemColor itemEssentiaAltColourHandler = (stack, tintIndex) -> {
         ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer)stack.func_77973_b();
         return stack.func_77952_i() == 1 && item.getAspects(stack) != null && tintIndex == 1 ? item.getAspects(stack).getAspects()[0].getColor() : 16777215;
      };
      itemColors.func_186730_a(itemEssentiaAltColourHandler, new Item[]{ItemsTC.phial, ItemsTC.label});
      IItemColor itemArmorColourHandler = (stack, tintIndex) -> {
         ItemArmor item = (ItemArmor)stack.func_77973_b();
         return tintIndex > 0 ? -1 : item.func_82814_b(stack);
      };
      itemColors.func_186730_a(
         itemArmorColourHandler,
         new Item[]{ItemsTC.voidRobeChest, ItemsTC.voidRobeHelm, ItemsTC.voidRobeLegs, ItemsTC.clothChest, ItemsTC.clothLegs, ItemsTC.clothBoots}
      );
      IItemColor itemCasterColourHandler = (stack, tintIndex) -> {
         ItemCaster item = (ItemCaster)stack.func_77973_b();
         ItemFocus focus = item.getFocus(stack);
         return tintIndex > 0 && focus != null ? focus.getFocusColor(item.getFocusStack(stack)) : -1;
      };
      itemColors.func_186730_a(itemCasterColourHandler, new Item[]{ItemsTC.casterBasic});
      IItemColor itemFocusColourHandler = (stack, tintIndex) -> {
         ItemFocus item = (ItemFocus)stack.func_77973_b();
         return item.getFocusColor(stack);
      };
      itemColors.func_186730_a(itemFocusColourHandler, new Item[]{ItemsTC.focus1});
      itemColors.func_186730_a(itemFocusColourHandler, new Item[]{ItemsTC.focus2});
      itemColors.func_186730_a(itemFocusColourHandler, new Item[]{ItemsTC.focus3});
      IItemColor itemGolemColourHandler = (stack, tintIndex) -> {
         if (stack.func_77942_o() && stack.func_77978_p().func_74764_b("props")) {
            IGolemProperties props = GolemProperties.fromLong(stack.func_77978_p().func_74763_f("props"));
            return props.getMaterial().itemColor;
         } else {
            return 16777215;
         }
      };
      itemColors.func_186730_a(itemGolemColourHandler, new Item[]{ItemsTC.golemPlacer});
      IItemColor itemBannerColourHandler = (stack, tintIndex) -> {
         if (tintIndex == 1) {
            IBlockState state = ((ItemBlock)stack.func_77973_b()).func_179223_d().func_176203_a(stack.func_77960_j());
            return blockColors.func_186724_a(state, null, null, tintIndex);
         }

         if (tintIndex == 2) {
            if (stack.func_77942_o() && stack.func_77978_p().func_74764_b("aspect") && stack.func_77978_p().func_74779_i("aspect") != null) {
               return Aspect.getAspect(stack.func_77978_p().func_74779_i("aspect")).getColor();
            }

            IBlockState state = ((ItemBlock)stack.func_77973_b()).func_179223_d().func_176203_a(stack.func_77960_j());
            return blockColors.func_186724_a(state, null, null, tintIndex);
         } else {
            return 16777215;
         }
      };
      Block[] bannerBlocks = new Block[BlocksTC.banners.size()];
      i = 0;

      for (Block b : BlocksTC.banners.values()) {
         bannerBlocks[i] = b;
         i++;
      }

      itemColors.func_186731_a(itemBannerColourHandler, bannerBlocks);
   }
}
