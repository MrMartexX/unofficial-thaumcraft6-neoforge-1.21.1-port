package thaumcraft.common.golems.ai;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.IBlockAccess;

public class GolemNodeProcessor extends NodeProcessor {
   private float avoidsWater;

   public void func_186315_a(IBlockAccess sourceIn, EntityLiving mob) {
      super.func_186315_a(sourceIn, mob);
      this.avoidsWater = mob.func_184643_a(PathNodeType.WATER);
   }

   public void func_176163_a() {
      this.field_186326_b.func_184644_a(PathNodeType.WATER, this.avoidsWater);
      super.func_176163_a();
   }

   public PathPoint func_186318_b() {
      int i;
      if (this.func_186322_e() && this.field_186326_b.func_70090_H()) {
         i = (int)this.field_186326_b.func_174813_aQ().field_72338_b;
         MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos(
            MathHelper.func_76128_c(this.field_186326_b.field_70165_t), i, MathHelper.func_76128_c(this.field_186326_b.field_70161_v)
         );

         for (Block block = this.field_176169_a.func_180495_p(blockpos$mutableblockpos).func_177230_c();
            block == Blocks.field_150358_i || block == Blocks.field_150355_j;
            block = this.field_176169_a.func_180495_p(blockpos$mutableblockpos).func_177230_c()
         ) {
            blockpos$mutableblockpos.func_181079_c(
               MathHelper.func_76128_c(this.field_186326_b.field_70165_t), ++i, MathHelper.func_76128_c(this.field_186326_b.field_70161_v)
            );
         }
      } else if (this.field_186326_b.field_70122_E) {
         i = MathHelper.func_76128_c(this.field_186326_b.func_174813_aQ().field_72338_b + 0.5);
      } else {
         BlockPos blockpos = new BlockPos(this.field_186326_b);

         while (
            (
                  this.field_176169_a.func_180495_p(blockpos).func_185904_a() == Material.field_151579_a
                     || this.field_176169_a.func_180495_p(blockpos).func_177230_c().func_176205_b(this.field_176169_a, blockpos)
               )
               && blockpos.func_177956_o() > 0
         ) {
            blockpos = blockpos.func_177977_b();
         }

         i = blockpos.func_177984_a().func_177956_o();
      }

      BlockPos blockpos2 = new BlockPos(this.field_186326_b);
      PathNodeType pathnodetype1 = this.getPathNodeType(this.field_186326_b, blockpos2.func_177958_n(), i, blockpos2.func_177952_p());
      if (this.field_186326_b.func_184643_a(pathnodetype1) < 0.0F) {
         Set<BlockPos> set = Sets.newHashSet();
         set.add(new BlockPos(this.field_186326_b.func_174813_aQ().field_72340_a, i, this.field_186326_b.func_174813_aQ().field_72339_c));
         set.add(new BlockPos(this.field_186326_b.func_174813_aQ().field_72340_a, i, this.field_186326_b.func_174813_aQ().field_72334_f));
         set.add(new BlockPos(this.field_186326_b.func_174813_aQ().field_72336_d, i, this.field_186326_b.func_174813_aQ().field_72339_c));
         set.add(new BlockPos(this.field_186326_b.func_174813_aQ().field_72336_d, i, this.field_186326_b.func_174813_aQ().field_72334_f));

         for (BlockPos blockpos1 : set) {
            PathNodeType pathnodetype = this.getPathNodeType(this.field_186326_b, blockpos1);
            if (this.field_186326_b.func_184643_a(pathnodetype) >= 0.0F) {
               return this.func_176159_a(blockpos1.func_177958_n(), blockpos1.func_177956_o(), blockpos1.func_177952_p());
            }
         }
      }

      return this.func_176159_a(blockpos2.func_177958_n(), i, blockpos2.func_177952_p());
   }

   public PathPoint func_186325_a(double x, double y, double z) {
      return this.func_176159_a(
         MathHelper.func_76128_c(x - this.field_186326_b.field_70130_N / 2.0F),
         MathHelper.func_76128_c(y),
         MathHelper.func_76128_c(z - this.field_186326_b.field_70130_N / 2.0F)
      );
   }

   public int func_186320_a(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
      int i = 0;
      int j = 0;
      PathNodeType pathnodetype = this.getPathNodeType(
         this.field_186326_b, currentPoint.field_75839_a, currentPoint.field_75837_b + 1, currentPoint.field_75838_c
      );
      if (this.field_186326_b.func_184643_a(pathnodetype) >= 0.0F) {
         j = MathHelper.func_76141_d(Math.max(1.0F, this.field_186326_b.field_70138_W));
      }

      BlockPos blockpos = new BlockPos(currentPoint.field_75839_a, currentPoint.field_75837_b, currentPoint.field_75838_c).func_177977_b();
      double d0 = currentPoint.field_75837_b - (1.0 - this.field_176169_a.func_180495_p(blockpos).func_185900_c(this.field_176169_a, blockpos).field_72337_e);
      PathPoint pathpoint = this.getSafePoint(currentPoint.field_75839_a, currentPoint.field_75837_b, currentPoint.field_75838_c + 1, j, d0, EnumFacing.SOUTH);
      PathPoint pathpoint1 = this.getSafePoint(currentPoint.field_75839_a - 1, currentPoint.field_75837_b, currentPoint.field_75838_c, j, d0, EnumFacing.WEST);
      PathPoint pathpoint2 = this.getSafePoint(currentPoint.field_75839_a + 1, currentPoint.field_75837_b, currentPoint.field_75838_c, j, d0, EnumFacing.EAST);
      PathPoint pathpoint3 = this.getSafePoint(currentPoint.field_75839_a, currentPoint.field_75837_b, currentPoint.field_75838_c - 1, j, d0, EnumFacing.NORTH);
      if (pathpoint != null && !pathpoint.field_75842_i && pathpoint.func_75829_a(targetPoint) < maxDistance) {
         pathOptions[i++] = pathpoint;
      }

      if (pathpoint1 != null && !pathpoint1.field_75842_i && pathpoint1.func_75829_a(targetPoint) < maxDistance) {
         pathOptions[i++] = pathpoint1;
      }

      if (pathpoint2 != null && !pathpoint2.field_75842_i && pathpoint2.func_75829_a(targetPoint) < maxDistance) {
         pathOptions[i++] = pathpoint2;
      }

      if (pathpoint3 != null && !pathpoint3.field_75842_i && pathpoint3.func_75829_a(targetPoint) < maxDistance) {
         pathOptions[i++] = pathpoint3;
      }

      boolean flag = pathpoint3 == null || pathpoint3.field_186287_m == PathNodeType.OPEN || pathpoint3.field_186286_l != 0.0F;
      boolean flag1 = pathpoint == null || pathpoint.field_186287_m == PathNodeType.OPEN || pathpoint.field_186286_l != 0.0F;
      boolean flag2 = pathpoint2 == null || pathpoint2.field_186287_m == PathNodeType.OPEN || pathpoint2.field_186286_l != 0.0F;
      boolean flag3 = pathpoint1 == null || pathpoint1.field_186287_m == PathNodeType.OPEN || pathpoint1.field_186286_l != 0.0F;
      if (flag && flag3) {
         PathPoint pathpoint4 = this.getSafePoint(
            currentPoint.field_75839_a - 1, currentPoint.field_75837_b, currentPoint.field_75838_c - 1, j, d0, EnumFacing.NORTH
         );
         if (pathpoint4 != null && !pathpoint4.field_75842_i && pathpoint4.func_75829_a(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint4;
         }
      }

      if (flag && flag2) {
         PathPoint pathpoint5 = this.getSafePoint(
            currentPoint.field_75839_a + 1, currentPoint.field_75837_b, currentPoint.field_75838_c - 1, j, d0, EnumFacing.NORTH
         );
         if (pathpoint5 != null && !pathpoint5.field_75842_i && pathpoint5.func_75829_a(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint5;
         }
      }

      if (flag1 && flag3) {
         PathPoint pathpoint6 = this.getSafePoint(
            currentPoint.field_75839_a - 1, currentPoint.field_75837_b, currentPoint.field_75838_c + 1, j, d0, EnumFacing.SOUTH
         );
         if (pathpoint6 != null && !pathpoint6.field_75842_i && pathpoint6.func_75829_a(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint6;
         }
      }

      if (flag1 && flag2) {
         PathPoint pathpoint7 = this.getSafePoint(
            currentPoint.field_75839_a + 1, currentPoint.field_75837_b, currentPoint.field_75838_c + 1, j, d0, EnumFacing.SOUTH
         );
         if (pathpoint7 != null && !pathpoint7.field_75842_i && pathpoint7.func_75829_a(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint7;
         }
      }

      return i;
   }

   private PathPoint getSafePoint(int x, int y, int z, int p_186332_4_, double p_186332_5_, EnumFacing facing) {
      PathPoint pathpoint = null;
      BlockPos blockpos = new BlockPos(x, y, z);
      BlockPos blockpos1 = blockpos.func_177977_b();
      double d0 = y - (1.0 - this.field_176169_a.func_180495_p(blockpos1).func_185900_c(this.field_176169_a, blockpos1).field_72337_e);
      if (d0 - p_186332_5_ > 1.125) {
         return null;
      }

      PathNodeType pathnodetype = this.getPathNodeType(this.field_186326_b, x, y, z);
      float f = this.field_186326_b.func_184643_a(pathnodetype);
      double d1 = this.field_186326_b.field_70130_N / 2.0;
      if (f >= 0.0F) {
         pathpoint = this.func_176159_a(x, y, z);
         pathpoint.field_186287_m = pathnodetype;
         pathpoint.field_186286_l = Math.max(pathpoint.field_186286_l, f);
      }

      if (pathnodetype == PathNodeType.WALKABLE) {
         return pathpoint;
      }

      if (pathpoint == null && p_186332_4_ > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
         pathpoint = this.getSafePoint(x, y + 1, z, p_186332_4_ - 1, p_186332_5_, facing);
         if (pathpoint != null
            && (pathpoint.field_186287_m == PathNodeType.OPEN || pathpoint.field_186287_m == PathNodeType.WALKABLE)
            && this.field_186326_b.field_70130_N < 1.0F) {
            double d2 = x - facing.func_82601_c() + 0.5;
            double d3 = z - facing.func_82599_e() + 0.5;
            AxisAlignedBB axisalignedbb = new AxisAlignedBB(d2 - d1, y + 0.001, d3 - d1, d2 + d1, y + this.field_186326_b.field_70131_O, d3 + d1);
            AxisAlignedBB axisalignedbb1 = this.field_176169_a.func_180495_p(blockpos).func_185900_c(this.field_176169_a, blockpos);
            AxisAlignedBB axisalignedbb2 = axisalignedbb.func_72321_a(0.0, axisalignedbb1.field_72337_e - 0.002, 0.0);
            if (this.field_186326_b.field_70170_p.func_184143_b(axisalignedbb2)) {
               pathpoint = null;
            }
         }
      }

      if (pathnodetype == PathNodeType.OPEN) {
         AxisAlignedBB axisalignedbb3 = new AxisAlignedBB(
            x - d1 + 0.5, y + 0.001, z - d1 + 0.5, x + d1 + 0.5, y + this.field_186326_b.field_70131_O, z + d1 + 0.5
         );
         if (this.field_186326_b.field_70170_p.func_184143_b(axisalignedbb3)) {
            return null;
         }

         if (this.field_186326_b.field_70130_N >= 1.0F) {
            PathNodeType pathnodetype1 = this.getPathNodeType(this.field_186326_b, x, y - 1, z);
            if (pathnodetype1 == PathNodeType.BLOCKED) {
               pathpoint = this.func_176159_a(x, y, z);
               pathpoint.field_186287_m = PathNodeType.WALKABLE;
               pathpoint.field_186286_l = Math.max(pathpoint.field_186286_l, f);
               return pathpoint;
            }
         }

         int i = 0;

         while (y > 0 && pathnodetype == PathNodeType.OPEN) {
            y--;
            if (i++ >= this.field_186326_b.func_82143_as()) {
               return null;
            }

            pathnodetype = this.getPathNodeType(this.field_186326_b, x, y, z);
            f = this.field_186326_b.func_184643_a(pathnodetype);
            if (pathnodetype != PathNodeType.OPEN && f >= 0.0F) {
               pathpoint = this.func_176159_a(x, y, z);
               pathpoint.field_186287_m = pathnodetype;
               pathpoint.field_186286_l = Math.max(pathpoint.field_186286_l, f);
               break;
            }

            if (f < 0.0F) {
               return null;
            }
         }
      }

      return pathpoint;
   }

   public PathNodeType func_186319_a(
      IBlockAccess blockaccessIn,
      int x,
      int y,
      int z,
      EntityLiving entitylivingIn,
      int xSize,
      int ySize,
      int zSize,
      boolean canBreakDoorsIn,
      boolean canEnterDoorsIn
   ) {
      EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
      PathNodeType pathnodetype = PathNodeType.BLOCKED;
      double d0 = entitylivingIn.field_70130_N / 2.0;
      BlockPos blockpos = new BlockPos(entitylivingIn);

      for (int i = 0; i < xSize; i++) {
         for (int j = 0; j < ySize; j++) {
            for (int k = 0; k < zSize; k++) {
               int l = i + x;
               int i1 = j + y;
               int j1 = k + z;
               PathNodeType pathnodetype1 = this.func_186330_a(blockaccessIn, l, i1, j1);
               if (pathnodetype1 == PathNodeType.DOOR_WOOD_CLOSED && canBreakDoorsIn && canEnterDoorsIn) {
                  pathnodetype1 = PathNodeType.WALKABLE;
               }

               if (pathnodetype1 == PathNodeType.DOOR_OPEN && !canEnterDoorsIn) {
                  pathnodetype1 = PathNodeType.BLOCKED;
               }

               if (pathnodetype1 == PathNodeType.RAIL
                  && !(blockaccessIn.func_180495_p(blockpos).func_177230_c() instanceof BlockRailBase)
                  && !(blockaccessIn.func_180495_p(blockpos.func_177977_b()).func_177230_c() instanceof BlockRailBase)) {
                  pathnodetype1 = PathNodeType.FENCE;
               }

               if (i == 0 && j == 0 && k == 0) {
                  pathnodetype = pathnodetype1;
               }

               enumset.add(pathnodetype1);
            }
         }
      }

      if (enumset.contains(PathNodeType.FENCE)) {
         return PathNodeType.FENCE;
      }

      PathNodeType pathnodetype2 = PathNodeType.BLOCKED;

      for (PathNodeType pathnodetype3 : enumset) {
         if (entitylivingIn.func_184643_a(pathnodetype3) < 0.0F) {
            return pathnodetype3;
         }

         if (entitylivingIn.func_184643_a(pathnodetype3) >= entitylivingIn.func_184643_a(pathnodetype2)) {
            pathnodetype2 = pathnodetype3;
         }
      }

      return pathnodetype == PathNodeType.OPEN && entitylivingIn.func_184643_a(pathnodetype2) == 0.0F ? PathNodeType.OPEN : pathnodetype2;
   }

   private PathNodeType getPathNodeType(EntityLiving entitylivingIn, BlockPos pos) {
      return this.getPathNodeType(entitylivingIn, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
   }

   private PathNodeType getPathNodeType(EntityLiving entitylivingIn, int x, int y, int z) {
      return this.func_186319_a(
         this.field_176169_a, x, y, z, entitylivingIn, this.field_176168_c, this.field_176165_d, this.field_176166_e, false, this.func_186323_c()
      );
   }

   public PathNodeType func_186330_a(IBlockAccess blockaccessIn, int x, int y, int z) {
      PathNodeType pathnodetype = this.getPathNodeTypeRaw(blockaccessIn, x, y, z);
      if (pathnodetype == PathNodeType.OPEN && y >= 1) {
         Block block = blockaccessIn.func_180495_p(new BlockPos(x, y - 1, z)).func_177230_c();
         PathNodeType pathnodetype1 = this.getPathNodeTypeRaw(blockaccessIn, x, y - 1, z);
         pathnodetype = pathnodetype1 != PathNodeType.WALKABLE
               && pathnodetype1 != PathNodeType.OPEN
               && pathnodetype1 != PathNodeType.WATER
               && pathnodetype1 != PathNodeType.LAVA
            ? PathNodeType.WALKABLE
            : PathNodeType.OPEN;
         if (pathnodetype1 == PathNodeType.DAMAGE_FIRE || block == Blocks.field_189877_df) {
            pathnodetype = PathNodeType.DAMAGE_FIRE;
         }

         if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
            pathnodetype = PathNodeType.DAMAGE_CACTUS;
         }
      }

      PooledMutableBlockPos blockpos$pooledmutableblockpos = PooledMutableBlockPos.func_185346_s();
      if (pathnodetype == PathNodeType.WALKABLE) {
         for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
               if (j != 0 || i != 0) {
                  Block block1 = blockaccessIn.func_180495_p(blockpos$pooledmutableblockpos.func_181079_c(j + x, y, i + z)).func_177230_c();
                  if (block1 == Blocks.field_150434_aF) {
                     pathnodetype = PathNodeType.DANGER_CACTUS;
                  } else if (block1 == Blocks.field_150480_ab) {
                     pathnodetype = PathNodeType.DANGER_FIRE;
                  }
               }
            }
         }
      }

      blockpos$pooledmutableblockpos.func_185344_t();
      return pathnodetype;
   }

   private PathNodeType getPathNodeTypeRaw(IBlockAccess p_189553_1_, int p_189553_2_, int p_189553_3_, int p_189553_4_) {
      BlockPos blockpos = new BlockPos(p_189553_2_, p_189553_3_, p_189553_4_);
      IBlockState iblockstate = p_189553_1_.func_180495_p(blockpos);
      Block block = iblockstate.func_177230_c();
      Material material = iblockstate.func_185904_a();
      return material == Material.field_151579_a
         ? PathNodeType.OPEN
         : (
            block == Blocks.field_150415_aT || block == Blocks.field_180400_cw || block == Blocks.field_150392_bi
               ? PathNodeType.TRAPDOOR
               : (
                  block == Blocks.field_150480_ab
                     ? PathNodeType.DAMAGE_FIRE
                     : (
                        block == Blocks.field_150434_aF
                           ? PathNodeType.DAMAGE_CACTUS
                           : (
                              block instanceof BlockDoor && material == Material.field_151575_d && !iblockstate.func_177229_b(BlockDoor.field_176519_b)
                                 ? PathNodeType.DOOR_WOOD_CLOSED
                                 : (
                                    block instanceof BlockDoor && material == Material.field_151573_f && !iblockstate.func_177229_b(BlockDoor.field_176519_b)
                                       ? PathNodeType.DOOR_IRON_CLOSED
                                       : (
                                          block instanceof BlockDoor && iblockstate.func_177229_b(BlockDoor.field_176519_b)
                                             ? PathNodeType.DOOR_OPEN
                                             : (
                                                block instanceof BlockRailBase
                                                   ? PathNodeType.RAIL
                                                   : (
                                                      !(block instanceof BlockFence)
                                                            && !(block instanceof BlockWall)
                                                            && (!(block instanceof BlockFenceGate) || iblockstate.func_177229_b(BlockFenceGate.field_176466_a))
                                                         ? (
                                                            material == Material.field_151586_h
                                                               ? PathNodeType.WATER
                                                               : (
                                                                  material == Material.field_151587_i
                                                                     ? PathNodeType.LAVA
                                                                     : (block.func_176205_b(p_189553_1_, blockpos) ? PathNodeType.OPEN : PathNodeType.BLOCKED)
                                                               )
                                                         )
                                                         : PathNodeType.FENCE
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }
}
