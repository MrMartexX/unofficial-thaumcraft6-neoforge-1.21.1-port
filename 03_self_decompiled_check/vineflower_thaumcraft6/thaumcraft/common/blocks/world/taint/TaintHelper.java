package thaumcraft.common.blocks.world.taint;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.world.aura.AuraHandler;

public class TaintHelper {
   private static ConcurrentHashMap<Integer, ArrayList<BlockPos>> taintSeeds = new ConcurrentHashMap<>();

   public static void addTaintSeed(World world, BlockPos pos) {
      ArrayList<BlockPos> locs = taintSeeds.get(world.field_73011_w.getDimension());
      if (locs == null) {
         locs = new ArrayList<>();
      }

      locs.add(pos);
      taintSeeds.put(world.field_73011_w.getDimension(), locs);
   }

   public static void removeTaintSeed(World world, BlockPos pos) {
      ArrayList<BlockPos> locs = taintSeeds.get(world.field_73011_w.getDimension());
      if (locs != null && !locs.isEmpty()) {
         locs.remove(pos);
      }
   }

   public static boolean isNearTaintSeed(World world, BlockPos pos) {
      double area = ModConfig.CONFIG_WORLD.taintSpreadArea * ModConfig.CONFIG_WORLD.taintSpreadArea;
      ArrayList<BlockPos> locs = taintSeeds.get(world.field_73011_w.getDimension());
      if (locs != null && !locs.isEmpty()) {
         for (BlockPos p : locs) {
            if (p.func_177951_i(pos) <= area) {
               if (EntityUtils.getEntitiesInRange(world, p, null, EntityTaintSeed.class, 1.0).size() <= 0) {
                  removeTaintSeed(world, p);
                  return false;
               }

               return true;
            }
         }
      }

      return false;
   }

   public static boolean isAtTaintSeedEdge(World world, BlockPos pos) {
      double area = ModConfig.CONFIG_WORLD.taintSpreadArea * ModConfig.CONFIG_WORLD.taintSpreadArea;
      double fringe = ModConfig.CONFIG_WORLD.taintSpreadArea * 0.8 * (ModConfig.CONFIG_WORLD.taintSpreadArea * 0.8);
      ArrayList<BlockPos> locs = taintSeeds.get(world.field_73011_w.getDimension());
      if (locs != null && !locs.isEmpty()) {
         for (BlockPos p : locs) {
            double d = p.func_177951_i(pos);
            if (d < area && d > fringe) {
               return true;
            }
         }
      }

      return false;
   }

   public static void spreadFibres(World world, BlockPos pos) {
      spreadFibres(world, pos, false);
   }

   public static void spreadFibres(World world, BlockPos pos, boolean ignore) {
      if (ignore || !ModConfig.CONFIG_MISC.wussMode) {
         float mod = 0.001F + AuraHandler.getFluxSaturation(world, pos) * 2.0F;
         if (ignore || !(world.field_73012_v.nextFloat() > ModConfig.CONFIG_WORLD.taintSpreadRate / 100.0F * mod)) {
            if (isNearTaintSeed(world, pos)) {
               int xx = pos.func_177958_n() + world.field_73012_v.nextInt(3) - 1;
               int yy = pos.func_177956_o() + world.field_73012_v.nextInt(3) - 1;
               int zz = pos.func_177952_p() + world.field_73012_v.nextInt(3) - 1;
               BlockPos t = new BlockPos(xx, yy, zz);
               if (t.equals(pos)) {
                  return;
               }

               IBlockState bs = world.func_180495_p(t);
               Material bm = bs.func_177230_c().func_149688_o(bs);
               float bh = bs.func_177230_c().func_176195_g(bs, world, t);
               if (bh < 0.0F || bh > 10.0F) {
                  return;
               }

               if (!bs.func_177230_c().isLeaves(bs, world, t)
                  && !bm.func_76224_d()
                  && (
                     world.func_175623_d(t)
                        || bs.func_177230_c().func_176200_f(world, t)
                        || bs.func_177230_c() instanceof BlockFlower
                        || bs.func_177230_c() instanceof IPlantable
                  )
                  && BlockUtils.isAdjacentToSolidBlock(world, t)
                  && !BlockTaintFibre.isOnlyAdjacentToTaint(world, t)) {
                  world.func_175656_a(t, BlocksTC.taintFibre.func_176223_P());
                  world.func_175641_c(t, BlocksTC.taintFibre, 1, 0);
                  AuraHelper.drainFlux(world, t, 0.01F, false);
                  return;
               }

               if (bs.func_177230_c().isLeaves(bs, world, t)) {
                  EnumFacing face = null;
                  if (world.field_73012_v.nextFloat() < 0.6 && (face = BlockUtils.getFaceBlockTouching(world, t, BlocksTC.taintLog)) != null) {
                     world.func_175656_a(t, BlocksTC.taintFeature.func_176223_P().func_177226_a(IBlockFacing.FACING, face.func_176734_d()));
                  } else {
                     world.func_175656_a(t, BlocksTC.taintFibre.func_176223_P());
                     world.func_175641_c(t, BlocksTC.taintFibre, 1, 0);
                     AuraHelper.drainFlux(world, t, 0.01F, false);
                  }

                  return;
               }

               if (BlockTaintFibre.isHemmedByTaint(world, t) && bs.func_185887_b(world, t) < 5.0F) {
                  if (Utils.isWoodLog(world, t) && bs.func_185904_a() != ThaumcraftMaterials.MATERIAL_TAINT) {
                     world.func_175656_a(t, BlocksTC.taintLog.func_176223_P().func_177226_a(BlockTaintLog.AXIS, BlockUtils.getBlockAxis(world, t)));
                     return;
                  }

                  if (bs.func_177230_c() == Blocks.field_150419_aX
                     || bs.func_177230_c() == Blocks.field_150420_aW
                     || bm == Material.field_151572_C
                     || bm == Material.field_151570_A
                     || bm == Material.field_151589_v
                     || bm == Material.field_151583_m
                     || bm == Material.field_151575_d) {
                     world.func_175656_a(t, BlocksTC.taintCrust.func_176223_P());
                     world.func_175641_c(t, BlocksTC.taintCrust, 1, 0);
                     AuraHelper.drainFlux(world, t, 0.01F, false);
                     return;
                  }

                  if (bm == Material.field_151595_p || bm == Material.field_151578_c || bm == Material.field_151577_b || bm == Material.field_151571_B) {
                     world.func_175656_a(t, BlocksTC.taintSoil.func_176223_P());
                     world.func_175641_c(t, BlocksTC.taintSoil, 1, 0);
                     AuraHelper.drainFlux(world, t, 0.01F, false);
                     return;
                  }

                  if (bm == Material.field_151576_e) {
                     world.func_175656_a(t, BlocksTC.taintRock.func_176223_P());
                     world.func_175641_c(t, BlocksTC.taintRock, 1, 0);
                     AuraHelper.drainFlux(world, t, 0.01F, false);
                     return;
                  }
               }

               if ((bs.func_177230_c() == BlocksTC.taintSoil || bs.func_177230_c() == BlocksTC.taintRock)
                  && world.func_175623_d(t.func_177984_a())
                  && AuraHelper.getFlux(world, t) >= 5.0F
                  && world.field_73012_v.nextFloat() < ModConfig.CONFIG_WORLD.taintSpreadRate / 100.0F * 0.33
                  && isAtTaintSeedEdge(world, t)) {
                  EntityTaintSeed e = new EntityTaintSeed(world);
                  e.func_70012_b(t.func_177958_n() + 0.5F, t.func_177984_a().func_177956_o(), t.func_177952_p() + 0.5F, world.field_73012_v.nextInt(360), 0.0F);
                  if (e.func_70601_bi()) {
                     AuraHelper.drainFlux(world, t, 5.0F, false);
                     world.func_72838_d(e);
                     return;
                  }
               }
            }
         }
      }
   }
}
