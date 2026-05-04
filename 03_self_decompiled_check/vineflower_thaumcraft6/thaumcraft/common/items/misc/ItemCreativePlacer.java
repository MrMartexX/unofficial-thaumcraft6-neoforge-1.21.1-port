package thaumcraft.common.items.misc;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.items.ItemTCBase;

public class ItemCreativePlacer extends ItemTCBase {
   public ItemCreativePlacer() {
      super("creative_placer", "obelisk", "node", "caster");
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.func_77624_a(stack, worldIn, tooltip, flagIn);
      tooltip.add(TextFormatting.DARK_PURPLE + "Creative only");
   }

   public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
      IBlockState bs = world.func_180495_p(pos);
      if (!bs.func_185904_a().func_76220_a()) {
         return EnumActionResult.FAIL;
      }

      if (world.field_72995_K) {
         return EnumActionResult.PASS;
      }

      pos = pos.func_177972_a(side);
      bs = world.func_180495_p(pos);
      if (!player.func_175151_a(pos, side, player.func_184586_b(hand))) {
         return EnumActionResult.FAIL;
      }

      if (!bs.func_177230_c().func_176200_f(world, pos)) {
         return EnumActionResult.FAIL;
      }

      if (player.func_184586_b(hand).func_77952_i() == 0 && !world.func_180495_p(pos.func_177977_b()).func_185904_a().func_76220_a()) {
         return EnumActionResult.FAIL;
      }

      world.func_175698_g(pos);
      player.func_184586_b(hand).func_77952_i();
      return EnumActionResult.SUCCESS;
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.EPIC;
   }
}
