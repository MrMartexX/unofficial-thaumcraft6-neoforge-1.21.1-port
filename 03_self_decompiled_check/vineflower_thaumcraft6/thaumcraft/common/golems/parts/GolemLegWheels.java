package thaumcraft.common.golems.parts;

import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.GolemLeg;

public class GolemLegWheels implements GolemLeg.ILegFunction {
   public static HashMap<Integer, Float> ani = new HashMap<>();

   @Override
   public void onUpdateTick(IGolemAPI golem) {
      if (golem.getGolemWorld().field_72995_K) {
         double dist = golem.getGolemEntity()
            .func_70011_f(golem.getGolemEntity().field_70169_q, golem.getGolemEntity().field_70167_r, golem.getGolemEntity().field_70166_s);
         float lastRot = 0.0F;
         if (ani.containsKey(golem.getGolemEntity().func_145782_y())) {
            lastRot = ani.get(golem.getGolemEntity().func_145782_y());
         }

         double d0 = golem.getGolemEntity().field_70165_t - golem.getGolemEntity().field_70169_q;
         double d1 = golem.getGolemEntity().field_70163_u - golem.getGolemEntity().field_70167_r;
         double d2 = golem.getGolemEntity().field_70161_v - golem.getGolemEntity().field_70166_s;
         float dirTravel = (float)(Math.atan2(d2, d0) * 180.0 / Math.PI) - 90.0F;
         double dir = 360.0F - (golem.getGolemEntity().field_70177_z - dirTravel);
         lastRot = (float)(lastRot + dist / 1.571 * dir);
         if (lastRot > 360.0F) {
            lastRot -= 360.0F;
         }

         ani.put(golem.getGolemEntity().func_145782_y(), lastRot);
         if (golem.getGolemEntity().field_70122_E && !golem.getGolemEntity().func_70090_H() && dist > 0.25) {
            int i = MathHelper.func_76128_c(golem.getGolemEntity().field_70165_t);
            int j = MathHelper.func_76128_c(golem.getGolemEntity().field_70163_u - 0.2F);
            int k = MathHelper.func_76128_c(golem.getGolemEntity().field_70161_v);
            BlockPos blockpos = new BlockPos(i, j, k);
            IBlockState iblockstate = golem.getGolemEntity().field_70170_p.func_180495_p(blockpos);
            Block block = iblockstate.func_177230_c();
            if (block.func_149645_b(iblockstate) != EnumBlockRenderType.INVISIBLE) {
               golem.getGolemEntity()
                  .field_70170_p
                  .func_175688_a(
                     EnumParticleTypes.BLOCK_CRACK,
                     golem.getGolemEntity().field_70165_t + (golem.getGolemWorld().field_73012_v.nextFloat() - 0.5) * golem.getGolemEntity().field_70130_N,
                     golem.getGolemEntity().func_174813_aQ().field_72338_b + 0.1,
                     golem.getGolemEntity().field_70161_v + (golem.getGolemWorld().field_73012_v.nextFloat() - 0.5) * golem.getGolemEntity().field_70130_N,
                     -golem.getGolemEntity().field_70159_w * 4.0,
                     1.5,
                     -golem.getGolemEntity().field_70179_y * 4.0,
                     new int[]{Block.func_176210_f(iblockstate)}
                  );
            }
         }
      }
   }
}
