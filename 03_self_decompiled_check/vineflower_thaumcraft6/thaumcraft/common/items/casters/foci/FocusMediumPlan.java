package thaumcraft.common.items.casters.foci;

import java.util.ArrayList;
import java.util.stream.Collectors;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.items.IArchitect;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.utils.BlockUtils;

public class FocusMediumPlan extends FocusMedium implements IArchitect {
   ArrayList<BlockPos> checked = new ArrayList<>();

   @Override
   public String getResearch() {
      return "FOCUSPLAN";
   }

   @Override
   public String getKey() {
      return "thaumcraft.PLAN";
   }

   @Override
   public int getComplexity() {
      return 4;
   }

   @Override
   public Aspect getAspect() {
      return Aspect.CRAFT;
   }

   @Override
   public NodeSetting[] createSettings() {
      int[] method = new int[]{0, 1};
      String[] methodDesc = new String[]{"focus.plan.full", "focus.plan.surface"};
      return new NodeSetting[]{new NodeSetting("method", "focus.plan.method", new NodeSetting.NodeSettingIntList(method, methodDesc))};
   }

   @Override
   public RayTraceResult[] supplyTargets() {
      if (this.getParent() != null && this.getPackage().getCaster() instanceof EntityPlayer) {
         ArrayList<RayTraceResult> targets = new ArrayList<>();
         ItemStack casterStack = ItemStack.field_190927_a;
         if (this.getPackage().getCaster().func_184614_ca() != null && this.getPackage().getCaster().func_184614_ca().func_77973_b() instanceof ICaster) {
            casterStack = this.getPackage().getCaster().func_184614_ca();
         } else if (this.getPackage().getCaster().func_184592_cb() != null && this.getPackage().getCaster().func_184592_cb().func_77973_b() instanceof ICaster) {
            casterStack = this.getPackage().getCaster().func_184592_cb();
         }

         for (Trajectory sT : this.getParent().supplyTrajectories()) {
            Vec3d end = sT.direction.func_72432_b();
            end = end.func_186678_a(16.0);
            end = end.func_178787_e(sT.source);
            RayTraceResult target = this.getPackage().world.func_72933_a(sT.source, end);
            if (target != null && target.field_72313_a == Type.BLOCK) {
               ArrayList<BlockPos> usl = this.getArchitectBlocks(
                  casterStack, this.getPackage().world, target.func_178782_a(), target.field_178784_b, (EntityPlayer)this.getPackage().getCaster()
               );

               for (BlockPos p : (ArrayList)usl.stream().sorted(new BlockUtils.BlockPosComparator(target.func_178782_a())).collect(Collectors.toList())) {
                  targets.add(
                     new RayTraceResult(new Vec3d(p.func_177958_n() + 0.5, p.func_177956_o() + 0.5, p.func_177952_p() + 0.5), target.field_178784_b, p)
                  );
               }
            }
         }

         return targets.toArray(new RayTraceResult[0]);
      } else {
         return new RayTraceResult[0];
      }
   }

   @Override
   public RayTraceResult getArchitectMOP(ItemStack stack, World world, EntityLivingBase player) {
      Vec3d start = player.func_174791_d();
      start = start.func_72441_c(0.0, player.func_70047_e(), 0.0);
      Vec3d end = player.func_70040_Z();
      end = end.func_186678_a(16.0);
      end = end.func_178787_e(start);
      return world.func_72933_a(start, end);
   }

   @Override
   public boolean useBlockHighlight(ItemStack stack) {
      return false;
   }

   @Override
   public boolean isExclusive() {
      return true;
   }

   @Override
   public boolean showAxis(ItemStack stack, World world, EntityPlayer player, EnumFacing side, IArchitect.EnumAxis axis) {
      if (stack != null && !stack.func_190926_b()) {
         int dim = CasterManager.getAreaDim(stack);
         if (this.getSettingValue("method") == 0) {
            switch (axis) {
               case Y:
                  if (dim == 0 || dim == 3) {
                     return true;
                  }
                  break;
               case Z:
                  if (dim == 0 || dim == 2) {
                     return true;
                  }
                  break;
               case X:
                  if (dim == 0 || dim == 1) {
                     return true;
                  }
            }
         } else {
            switch (side.func_176740_k()) {
               case Y:
                  if (axis == IArchitect.EnumAxis.X && (dim == 0 || dim == 1) || axis == IArchitect.EnumAxis.Z && (dim == 0 || dim == 2)) {
                     return true;
                  }
                  break;
               case Z:
                  if (axis == IArchitect.EnumAxis.Y && (dim == 0 || dim == 1) || axis == IArchitect.EnumAxis.X && (dim == 0 || dim == 2)) {
                     return true;
                  }
                  break;
               case X:
                  if (axis == IArchitect.EnumAxis.Y && (dim == 0 || dim == 1) || axis == IArchitect.EnumAxis.Z && (dim == 0 || dim == 2)) {
                     return true;
                  }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public ArrayList<BlockPos> getArchitectBlocks(ItemStack stack, World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
      ArrayList<BlockPos> out = new ArrayList<>();
      if (stack != null && !stack.func_190926_b()) {
         if (this.getSettingValue("method") == 0) {
            this.checked.clear();
            this.checkNeighboursFull(
               world, pos, new BlockPos(pos), side, CasterManager.getAreaX(stack), CasterManager.getAreaY(stack), CasterManager.getAreaZ(stack), out, player
            );
         } else {
            IBlockState bi = world.func_180495_p(pos);
            this.checked.clear();
            if (side.func_176740_k() == Axis.Z) {
               this.checkNeighboursSurface(
                  world,
                  pos,
                  bi,
                  new BlockPos(pos),
                  side,
                  CasterManager.getAreaZ(stack),
                  CasterManager.getAreaY(stack),
                  CasterManager.getAreaX(stack),
                  out,
                  player
               );
            } else {
               this.checkNeighboursSurface(
                  world,
                  pos,
                  bi,
                  new BlockPos(pos),
                  side,
                  CasterManager.getAreaX(stack),
                  CasterManager.getAreaY(stack),
                  CasterManager.getAreaZ(stack),
                  out,
                  player
               );
            }
         }

         return out;
      } else {
         return out;
      }
   }

   public void checkNeighboursFull(
      World world, BlockPos pos1, BlockPos pos2, EnumFacing side, int sizeX, int sizeY, int sizeZ, ArrayList<BlockPos> list, EntityPlayer player
   ) {
      if (!this.checked.contains(pos2)) {
         this.checked.add(pos2);
         if (!world.func_175623_d(pos2)) {
            list.add(pos2);
         }

         int xs = -sizeX + pos1.func_177958_n();
         int xe = sizeX + pos1.func_177958_n();
         int ys = -sizeY + pos1.func_177956_o();
         int ye = sizeY + pos1.func_177956_o();
         int zs = -sizeZ + pos1.func_177952_p();
         int ze = sizeZ + pos1.func_177952_p();
         xs -= sizeX * side.func_82601_c();
         xe -= sizeX * side.func_82601_c();
         ys -= sizeY * side.func_96559_d();
         ye -= sizeY * side.func_96559_d();
         zs -= sizeZ * side.func_82599_e();
         ze -= sizeZ * side.func_82599_e();

         for (EnumFacing dir : EnumFacing.values()) {
            BlockPos q = pos2.func_177972_a(dir);
            if (q.func_177958_n() >= xs
               && q.func_177958_n() <= xe
               && q.func_177956_o() >= ys
               && q.func_177956_o() <= ye
               && q.func_177952_p() >= zs
               && q.func_177952_p() <= ze) {
               this.checkNeighboursFull(world, pos1, q, side, sizeX, sizeY, sizeZ, list, player);
            }
         }
      }
   }

   public void checkNeighboursSurface(
      World world,
      BlockPos pos1,
      IBlockState bi,
      BlockPos pos2,
      EnumFacing side,
      int sizeX,
      int sizeY,
      int sizeZ,
      ArrayList<BlockPos> list,
      EntityPlayer player
   ) {
      if (!this.checked.contains(pos2)) {
         this.checked.add(pos2);
         switch (side.func_176740_k()) {
            case Y:
               if (Math.abs(pos2.func_177958_n() - pos1.func_177958_n()) > sizeX) {
                  return;
               }

               if (Math.abs(pos2.func_177952_p() - pos1.func_177952_p()) > sizeZ) {
                  return;
               }
               break;
            case Z:
               if (Math.abs(pos2.func_177958_n() - pos1.func_177958_n()) > sizeX) {
                  return;
               }

               if (Math.abs(pos2.func_177956_o() - pos1.func_177956_o()) > sizeZ) {
                  return;
               }
               break;
            case X:
               if (Math.abs(pos2.func_177956_o() - pos1.func_177956_o()) > sizeX) {
                  return;
               }

               if (Math.abs(pos2.func_177952_p() - pos1.func_177952_p()) > sizeZ) {
                  return;
               }
         }

         if (world.func_180495_p(pos2) == bi && BlockUtils.isBlockExposed(world, pos2) && !world.func_175623_d(pos2)) {
            list.add(pos2);

            for (EnumFacing dir : EnumFacing.values()) {
               if (dir != side && dir.func_176734_d() != side) {
                  this.checkNeighboursSurface(world, pos1, bi, pos2.func_177972_a(dir), side, sizeX, sizeY, sizeZ, list, player);
               }
            }
         }
      }
   }
}
