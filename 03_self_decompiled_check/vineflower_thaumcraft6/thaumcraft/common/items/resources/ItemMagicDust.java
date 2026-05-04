package thaumcraft.common.items.resources;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;

public class ItemMagicDust extends ItemTCBase {
   public ItemMagicDust() {
      super("salis_mundus");
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.UNCOMMON;
   }

   public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
      if (!player.func_175151_a(pos, side, player.func_184586_b(hand))) {
         return EnumActionResult.FAIL;
      }

      if (player.func_70093_af()) {
         return EnumActionResult.PASS;
      }

      player.func_184609_a(hand);

      for (IDustTrigger trigger : IDustTrigger.triggers) {
         IDustTrigger.Placement place = trigger.getValidFace(world, player, pos, side);
         if (place != null) {
            if (!player.field_71075_bZ.field_75098_d) {
               player.func_184586_b(hand).func_190918_g(1);
            }

            trigger.execute(world, player, pos, place, side);
            if (!world.field_72995_K) {
               return EnumActionResult.SUCCESS;
            }

            this.doSparkles(player, world, pos, hitX, hitY, hitZ, hand, trigger, place);
            break;
         }
      }

      return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
   }

   private void doSparkles(
      EntityPlayer player, World world, BlockPos pos, float hitX, float hitY, float hitZ, EnumHand hand, IDustTrigger trigger, IDustTrigger.Placement place
   ) {
      Vec3d v1 = EntityUtils.posToHand(player, hand);
      Vec3d v2 = new Vec3d(pos);
      v2 = v2.func_72441_c(0.5, 0.5, 0.5);
      v2 = v2.func_178788_d(v1);
      int cnt = 50;

      for (int a = 0; a < cnt; a++) {
         boolean floaty = a < cnt / 3;
         float r = MathHelper.func_76136_a(world.field_73012_v, 255, 255) / 255.0F;
         float g = MathHelper.func_76136_a(world.field_73012_v, 189, 255) / 255.0F;
         float b = MathHelper.func_76136_a(world.field_73012_v, 64, 255) / 255.0F;
         FXDispatcher.INSTANCE
            .drawSimpleSparkle(
               world.field_73012_v,
               v1.field_72450_a,
               v1.field_72448_b,
               v1.field_72449_c,
               v2.field_72450_a / 6.0 + world.field_73012_v.nextGaussian() * 0.05,
               v2.field_72448_b / 6.0 + world.field_73012_v.nextGaussian() * 0.05 + (floaty ? 0.05 : 0.15),
               v2.field_72449_c / 6.0 + world.field_73012_v.nextGaussian() * 0.05,
               0.5F,
               r,
               g,
               b,
               world.field_73012_v.nextInt(5),
               floaty ? 0.3F + world.field_73012_v.nextFloat() * 0.5F : 0.85F,
               floaty ? 0.2F : 0.5F,
               16
            );
      }

      world.func_184134_a(
         pos.func_177958_n(),
         pos.func_177956_o(),
         pos.func_177952_p(),
         SoundsTC.dust,
         SoundCategory.PLAYERS,
         0.33F,
         1.0F + (float)world.field_73012_v.nextGaussian() * 0.05F,
         false
      );
      List<BlockPos> sparkles = trigger.sparkle(world, player, pos, place);
      if (sparkles != null) {
         Vec3d v = new Vec3d(pos).func_72441_c(hitX, hitY, hitZ);

         for (BlockPos p : sparkles) {
            FXDispatcher.INSTANCE.drawBlockSparkles(p, v);
         }
      }
   }
}
