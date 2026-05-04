package thaumcraft.common.golems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;

public class ItemGolemBell extends ItemTCBase implements ISealDisplayer {
   public ItemGolemBell() {
      super("golem_bell");
      this.func_77627_a(false);
      this.func_77625_d(1);
   }

   public ActionResult<ItemStack> func_77659_a(World worldIn, EntityPlayer playerIn, EnumHand hand) {
      playerIn.func_184609_a(hand);
      if (worldIn.field_72995_K) {
         playerIn.func_184185_a(SoundEvents.field_187604_bf, 0.6F, 1.0F + worldIn.field_73012_v.nextFloat() * 0.1F);
      } else {
         RayTraceResult mop = RayTracer.retrace(playerIn);
         if (mop != null && (mop.field_72313_a == Type.BLOCK || mop.field_72313_a == Type.ENTITY)) {
            ISealEntity se = getSeal(playerIn);
            if (se != null) {
               if (playerIn.func_70093_af()) {
                  SealHandler.removeSealEntity(playerIn.field_70170_p, se.getSealPos(), false);
                  worldIn.func_184133_a(null, se.getSealPos().pos, SoundsTC.zap, SoundCategory.BLOCKS, 0.5F, 1.0F);
               } else {
                  playerIn.openGui(
                     Thaumcraft.instance,
                     18,
                     playerIn.field_70170_p,
                     se.getSealPos().pos.func_177958_n(),
                     se.getSealPos().pos.func_177956_o(),
                     se.getSealPos().pos.func_177952_p()
                  );
               }
            }

            return new ActionResult(EnumActionResult.FAIL, playerIn.func_184586_b(hand));
         }

         if (playerIn.func_70093_af() && ThaumcraftCapabilities.knowsResearch(playerIn, "GOLEMLOGISTICS")) {
            playerIn.openGui(
               Thaumcraft.instance, 20, playerIn.field_70170_p, (int)playerIn.field_70165_t, (int)playerIn.field_70163_u, (int)playerIn.field_70161_v
            );
            return new ActionResult(EnumActionResult.FAIL, playerIn.func_184586_b(hand));
         }
      }

      return super.func_77659_a(worldIn, playerIn, hand);
   }

   public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
      player.func_184609_a(hand);
      if (!world.field_72995_K) {
         ISealEntity se = SealHandler.getSealEntity(world.field_73011_w.getDimension(), new SealPos(pos, side));
         if (se != null) {
            if (player.func_70093_af()) {
               SealHandler.removeSealEntity(world, se.getSealPos(), false);
               world.func_184133_a(null, pos, SoundsTC.zap, SoundCategory.BLOCKS, 0.5F, 1.0F);
            } else {
               player.openGui(Thaumcraft.instance, 18, world, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
            }

            return EnumActionResult.SUCCESS;
         }

         if (player.func_70093_af() && ThaumcraftCapabilities.knowsResearch(player, "GOLEMLOGISTICS")) {
            player.openGui(Thaumcraft.instance, 20, world, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
            return EnumActionResult.SUCCESS;
         }
      }

      return EnumActionResult.PASS;
   }

   public static ISealEntity getSeal(EntityPlayer playerIn) {
      float f = playerIn.field_70125_A;
      float f1 = playerIn.field_70177_z;
      double d0 = playerIn.field_70165_t;
      double d1 = playerIn.field_70163_u + playerIn.func_70047_e();
      double d2 = playerIn.field_70161_v;
      Vec3d vec0 = new Vec3d(d0, d1, d2);
      float f2 = MathHelper.func_76134_b(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f3 = MathHelper.func_76126_a(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f4 = -MathHelper.func_76134_b(-f * (float) (Math.PI / 180.0));
      float f5 = MathHelper.func_76126_a(-f * (float) (Math.PI / 180.0));
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      double d3 = 5.0;
      Vec3d vec1 = vec0.func_72441_c(f6 * d3, f5 * d3, f7 * d3);
      Vec3d vec2 = new Vec3d(f6 * d3, f5 * d3, f7 * d3);
      Vec3d vec3 = vec0.func_72441_c(vec2.field_72450_a / 10.0, vec2.field_72448_b / 10.0, vec2.field_72449_c / 10.0);

      for (int a = 0; a < vec2.func_72433_c() * 10.0; a++) {
         BlockPos pos = new BlockPos(vec3);
         RayTraceResult mop = collisionRayTrace(playerIn.field_70170_p, pos, vec0, vec1);
         if (mop != null) {
            ISealEntity se = SealHandler.getSealEntity(playerIn.field_70170_p.field_73011_w.getDimension(), new SealPos(pos, mop.field_178784_b));
            if (se != null) {
               return se;
            }
         }

         vec3 = vec3.func_72441_c(vec2.field_72450_a / 10.0, vec2.field_72448_b / 10.0, vec2.field_72449_c / 10.0);
      }

      return null;
   }

   private static boolean isVecInsideYZBounds(Vec3d point, BlockPos pos) {
      return point == null
         ? false
         : point.field_72448_b >= pos.func_177956_o()
            && point.field_72448_b <= pos.func_177956_o() + 1
            && point.field_72449_c >= pos.func_177952_p()
            && point.field_72449_c <= pos.func_177952_p() + 1;
   }

   private static boolean isVecInsideXZBounds(Vec3d point, BlockPos pos) {
      return point == null
         ? false
         : point.field_72450_a >= pos.func_177958_n()
            && point.field_72450_a <= pos.func_177958_n() + 1
            && point.field_72449_c >= pos.func_177952_p()
            && point.field_72449_c <= pos.func_177952_p() + 1;
   }

   private static boolean isVecInsideXYBounds(Vec3d point, BlockPos pos) {
      return point == null
         ? false
         : point.field_72450_a >= pos.func_177958_n()
            && point.field_72450_a <= pos.func_177958_n() + 1
            && point.field_72448_b >= pos.func_177956_o()
            && point.field_72448_b <= pos.func_177956_o() + 1;
   }

   private static RayTraceResult collisionRayTrace(World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
      Vec3d vec3 = start.func_72429_b(end, pos.func_177958_n());
      Vec3d vec31 = start.func_72429_b(end, pos.func_177958_n() + 1);
      Vec3d vec32 = start.func_72435_c(end, pos.func_177956_o());
      Vec3d vec33 = start.func_72435_c(end, pos.func_177956_o() + 1);
      Vec3d vec34 = start.func_72434_d(end, pos.func_177952_p());
      Vec3d vec35 = start.func_72434_d(end, pos.func_177952_p() + 1);
      if (!isVecInsideYZBounds(vec3, pos)) {
         vec3 = null;
      }

      if (!isVecInsideYZBounds(vec31, pos)) {
         vec31 = null;
      }

      if (!isVecInsideXZBounds(vec32, pos)) {
         vec32 = null;
      }

      if (!isVecInsideXZBounds(vec33, pos)) {
         vec33 = null;
      }

      if (!isVecInsideXYBounds(vec34, pos)) {
         vec34 = null;
      }

      if (!isVecInsideXYBounds(vec35, pos)) {
         vec35 = null;
      }

      Vec3d vec36 = null;
      if (vec3 != null && (vec36 == null || start.func_72436_e(vec3) < start.func_72436_e(vec36))) {
         vec36 = vec3;
      }

      if (vec31 != null && (vec36 == null || start.func_72436_e(vec31) < start.func_72436_e(vec36))) {
         vec36 = vec31;
      }

      if (vec32 != null && (vec36 == null || start.func_72436_e(vec32) < start.func_72436_e(vec36))) {
         vec36 = vec32;
      }

      if (vec33 != null && (vec36 == null || start.func_72436_e(vec33) < start.func_72436_e(vec36))) {
         vec36 = vec33;
      }

      if (vec34 != null && (vec36 == null || start.func_72436_e(vec34) < start.func_72436_e(vec36))) {
         vec36 = vec34;
      }

      if (vec35 != null && (vec36 == null || start.func_72436_e(vec35) < start.func_72436_e(vec36))) {
         vec36 = vec35;
      }

      if (vec36 == null) {
         return null;
      }

      EnumFacing enumfacing = null;
      if (vec36 == vec3) {
         enumfacing = EnumFacing.WEST;
      }

      if (vec36 == vec31) {
         enumfacing = EnumFacing.EAST;
      }

      if (vec36 == vec32) {
         enumfacing = EnumFacing.DOWN;
      }

      if (vec36 == vec33) {
         enumfacing = EnumFacing.UP;
      }

      if (vec36 == vec34) {
         enumfacing = EnumFacing.NORTH;
      }

      if (vec36 == vec35) {
         enumfacing = EnumFacing.SOUTH;
      }

      return new RayTraceResult(vec36.func_72441_c(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p()), enumfacing, pos);
   }
}
