package thaumcraft.common.blocks.basic;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.misc.TileBanner;

public class BlockBannerTCItem extends ItemBlock {
   public BlockBannerTCItem(BlockBannerTC block) {
      super(block);
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (stack.func_77942_o() && stack.func_77978_p().func_74779_i("aspect") != null && Aspect.getAspect(stack.func_77978_p().func_74779_i("aspect")) != null) {
         tooltip.add(Aspect.getAspect(stack.func_77978_p().func_74779_i("aspect")).getName());
      }
   }

   public EnumActionResult func_180614_a(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (side == EnumFacing.DOWN) {
         return EnumActionResult.FAIL;
      }

      if (!worldIn.func_180495_p(pos).func_185904_a().func_76220_a()) {
         return EnumActionResult.FAIL;
      }

      pos = pos.func_177972_a(side);
      if (!player.func_175151_a(pos, side, player.func_184586_b(hand))) {
         return EnumActionResult.FAIL;
      }

      if (!Blocks.field_180393_cK.func_176196_c(worldIn, pos)) {
         return EnumActionResult.FAIL;
      }

      if (worldIn.field_72995_K) {
         return EnumActionResult.FAIL;
      }

      worldIn.func_180501_a(pos, this.field_150939_a.func_176223_P(), 3);
      TileBanner tile = (TileBanner)worldIn.func_175625_s(pos);
      if (tile != null) {
         if (side == EnumFacing.UP) {
            int i = MathHelper.func_76128_c((player.field_70177_z + 180.0F) * 16.0F / 360.0F + 0.5) & 15;
            tile.setBannerFacing((byte)i);
         } else {
            tile.setWall(true);
            int i = 0;
            if (side == EnumFacing.NORTH) {
               i = 8;
            }

            if (side == EnumFacing.WEST) {
               i = 4;
            }

            if (side == EnumFacing.EAST) {
               i = 12;
            }

            tile.setBannerFacing((byte)i);
         }

         if (player.func_184586_b(hand).func_77942_o() && player.func_184586_b(hand).func_77978_p().func_74779_i("aspect") != null) {
            tile.setAspect(Aspect.getAspect(player.func_184586_b(hand).func_77978_p().func_74779_i("aspect")));
         }

         tile.func_70296_d();
         worldIn.markAndNotifyBlock(pos, worldIn.func_175726_f(pos), this.field_150939_a.func_176223_P(), this.field_150939_a.func_176223_P(), 3);
      }

      player.func_184586_b(hand).func_190918_g(1);
      return EnumActionResult.SUCCESS;
   }
}
