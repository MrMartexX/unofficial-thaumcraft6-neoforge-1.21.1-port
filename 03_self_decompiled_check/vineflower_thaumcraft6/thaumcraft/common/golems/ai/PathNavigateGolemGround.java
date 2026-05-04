package thaumcraft.common.golems.ai;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateGolemGround extends PathNavigateGround {
   public PathNavigateGolemGround(EntityLiving entitylivingIn, World worldIn) {
      super(entitylivingIn, worldIn);
   }

   protected PathFinder func_179679_a() {
      this.field_179695_a = new GolemNodeProcessor();
      this.field_179695_a.func_186317_a(true);
      return new PathFinder(this.field_179695_a);
   }

   protected boolean func_75485_k() {
      return this.field_75515_a.field_70122_E || this.func_179684_h() && this.func_75506_l() || this.field_75515_a.func_184218_aH();
   }

   protected Vec3d func_75502_i() {
      return new Vec3d(this.field_75515_a.field_70165_t, this.getPathablePosY(), this.field_75515_a.field_70161_v);
   }

   public Path func_179680_a(BlockPos pos) {
      if (this.field_75513_b.func_180495_p(pos).func_185904_a() == Material.field_151579_a) {
         BlockPos blockpos = pos.func_177977_b();

         while (blockpos.func_177956_o() > 0 && this.field_75513_b.func_180495_p(blockpos).func_185904_a() == Material.field_151579_a) {
            blockpos = blockpos.func_177977_b();
         }

         if (blockpos.func_177956_o() > 0) {
            return super.func_179680_a(blockpos.func_177984_a());
         }

         while (
            blockpos.func_177956_o() < this.field_75513_b.func_72800_K()
               && this.field_75513_b.func_180495_p(blockpos).func_185904_a() == Material.field_151579_a
         ) {
            blockpos = blockpos.func_177984_a();
         }

         pos = blockpos;
      }

      if (!this.field_75513_b.func_180495_p(pos).func_185904_a().func_76220_a()) {
         return super.func_179680_a(pos);
      }

      BlockPos blockpos1 = pos.func_177984_a();

      while (blockpos1.func_177956_o() < this.field_75513_b.func_72800_K() && this.field_75513_b.func_180495_p(blockpos1).func_185904_a().func_76220_a()) {
         blockpos1 = blockpos1.func_177984_a();
      }

      return super.func_179680_a(blockpos1);
   }

   public Path func_75494_a(Entity entityIn) {
      BlockPos blockpos = new BlockPos(entityIn);
      return this.func_179680_a(blockpos);
   }

   private int getPathablePosY() {
      if (this.field_75515_a.func_70090_H() && this.func_179684_h()) {
         int i = (int)this.field_75515_a.func_174813_aQ().field_72338_b;
         Block block = this.field_75513_b
            .func_180495_p(
               new BlockPos(MathHelper.func_76128_c(this.field_75515_a.field_70165_t), i, MathHelper.func_76128_c(this.field_75515_a.field_70161_v))
            )
            .func_177230_c();
         int j = 0;

         while (block == Blocks.field_150358_i || block == Blocks.field_150355_j) {
            block = this.field_75513_b
               .func_180495_p(
                  new BlockPos(MathHelper.func_76128_c(this.field_75515_a.field_70165_t), ++i, MathHelper.func_76128_c(this.field_75515_a.field_70161_v))
               )
               .func_177230_c();
            if (++j > 16) {
               return (int)this.field_75515_a.func_174813_aQ().field_72338_b;
            }
         }

         return i;
      } else {
         return (int)(this.field_75515_a.func_174813_aQ().field_72338_b + 0.5);
      }
   }

   protected void func_75487_m() {
      super.func_75487_m();

      for (int i = 0; i < this.field_75514_c.func_75874_d(); i++) {
         PathPoint pathpoint = this.field_75514_c.func_75877_a(i);
         PathPoint pathpoint1 = i + 1 < this.field_75514_c.func_75874_d() ? this.field_75514_c.func_75877_a(i + 1) : null;
         IBlockState iblockstate = this.field_75513_b.func_180495_p(new BlockPos(pathpoint.field_75839_a, pathpoint.field_75837_b, pathpoint.field_75838_c));
         Block block = iblockstate.func_177230_c();
         if (block == Blocks.field_150383_bp) {
            this.field_75514_c.func_186309_a(i, pathpoint.func_186283_a(pathpoint.field_75839_a, pathpoint.field_75837_b + 1, pathpoint.field_75838_c));
            if (pathpoint1 != null && pathpoint.field_75837_b >= pathpoint1.field_75837_b) {
               this.field_75514_c
                  .func_186309_a(i + 1, pathpoint1.func_186283_a(pathpoint1.field_75839_a, pathpoint.field_75837_b + 1, pathpoint1.field_75838_c));
            }
         }
      }
   }

   protected boolean func_75493_a(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
      int i = MathHelper.func_76128_c(posVec31.field_72450_a);
      int j = MathHelper.func_76128_c(posVec31.field_72449_c);
      double d0 = posVec32.field_72450_a - posVec31.field_72450_a;
      double d1 = posVec32.field_72449_c - posVec31.field_72449_c;
      double d2 = d0 * d0 + d1 * d1;
      if (d2 < 1.0E-8) {
         return false;
      }

      double d3 = 1.0 / Math.sqrt(d2);
      d0 *= d3;
      d1 *= d3;
      sizeX += 2;
      sizeZ += 2;
      if (!this.isSafeToStandAt(i, (int)posVec31.field_72448_b, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
         return false;
      }

      sizeX -= 2;
      sizeZ -= 2;
      double d4 = 1.0 / Math.abs(d0);
      double d5 = 1.0 / Math.abs(d1);
      double d6 = i - posVec31.field_72450_a;
      double d7 = j - posVec31.field_72449_c;
      if (d0 >= 0.0) {
         d6++;
      }

      if (d1 >= 0.0) {
         d7++;
      }

      d6 /= d0;
      d7 /= d1;
      int k = d0 < 0.0 ? -1 : 1;
      int l = d1 < 0.0 ? -1 : 1;
      int i1 = MathHelper.func_76128_c(posVec32.field_72450_a);
      int j1 = MathHelper.func_76128_c(posVec32.field_72449_c);
      int k1 = i1 - i;
      int l1 = j1 - j;

      while (k1 * k > 0 || l1 * l > 0) {
         if (d6 < d7) {
            d6 += d4;
            i += k;
            k1 = i1 - i;
         } else {
            d7 += d5;
            j += l;
            l1 = j1 - j;
         }

         if (!this.isSafeToStandAt(i, (int)posVec31.field_72448_b, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
            return false;
         }
      }

      return true;
   }

   private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d vec31, double p_179683_8_, double p_179683_10_) {
      int i = x - sizeX / 2;
      int j = z - sizeZ / 2;
      if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_)) {
         return false;
      }

      for (int k = i; k < i + sizeX; k++) {
         for (int l = j; l < j + sizeZ; l++) {
            double d0 = k + 0.5 - vec31.field_72450_a;
            double d1 = l + 0.5 - vec31.field_72449_c;
            if (d0 * p_179683_8_ + d1 * p_179683_10_ >= 0.0) {
               PathNodeType pathnodetype = this.field_179695_a
                  .func_186319_a(this.field_75513_b, k, y - 1, l, this.field_75515_a, sizeX, sizeY, sizeZ, true, true);
               if (pathnodetype == PathNodeType.WATER) {
                  return false;
               }

               if (pathnodetype == PathNodeType.LAVA) {
                  return false;
               }

               if (pathnodetype == PathNodeType.OPEN) {
                  return false;
               }

               pathnodetype = this.field_179695_a.func_186319_a(this.field_75513_b, k, y, l, this.field_75515_a, sizeX, sizeY, sizeZ, true, true);
               float f = this.field_75515_a.func_184643_a(pathnodetype);
               if (f < 0.0F || f >= 8.0F) {
                  return false;
               }

               if (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE || pathnodetype == PathNodeType.DAMAGE_OTHER) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private boolean isPositionClear(
      int p_179692_1_,
      int p_179692_2_,
      int p_179692_3_,
      int p_179692_4_,
      int p_179692_5_,
      int p_179692_6_,
      Vec3d p_179692_7_,
      double p_179692_8_,
      double p_179692_10_
   ) {
      for (BlockPos blockpos : BlockPos.func_177980_a(
         new BlockPos(p_179692_1_, p_179692_2_, p_179692_3_),
         new BlockPos(p_179692_1_ + p_179692_4_ - 1, p_179692_2_ + p_179692_5_ - 1, p_179692_3_ + p_179692_6_ - 1)
      )) {
         double d0 = blockpos.func_177958_n() + 0.5 - p_179692_7_.field_72450_a;
         double d1 = blockpos.func_177952_p() + 0.5 - p_179692_7_.field_72449_c;
         if (d0 * p_179692_8_ + d1 * p_179692_10_ >= 0.0) {
            Block block = this.field_75513_b.func_180495_p(blockpos).func_177230_c();
            if (!block.func_176205_b(this.field_75513_b, blockpos)) {
               return false;
            }
         }
      }

      return true;
   }

   public void func_179691_c(boolean enterDoors) {
      this.field_179695_a.func_186317_a(enterDoors);
   }

   public boolean func_179686_g() {
      return this.field_179695_a.func_186323_c();
   }

   public void func_179693_d(boolean canSwim) {
      this.field_179695_a.func_186316_c(canSwim);
   }

   public boolean func_179684_h() {
      return this.field_179695_a.func_186322_e();
   }

   public void func_179685_e(boolean avoidSun) {
   }
}
