package thaumcraft.common.blocks.essentia;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.tiles.devices.TileJarBrain;

public class BlockJarBrainItem extends ItemBlock {
   public BlockJarBrainItem(Block block) {
      super(block);
   }

   public boolean placeBlockAt(
      ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState
   ) {
      boolean b = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
      if (b && !world.field_72995_K) {
         TileEntity te = world.func_175625_s(pos);
         if (te != null && te instanceof TileJarBrain) {
            TileJarBrain jar = (TileJarBrain)te;
            if (stack.func_77942_o()) {
               jar.xp = stack.func_77978_p().func_74762_e("xp");
            }

            te.func_70296_d();
            world.markAndNotifyBlock(pos, world.func_175726_f(pos), newState, newState, 3);
         }
      }

      return b;
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (stack.func_77942_o() && stack.func_77978_p().func_74764_b("xp")) {
         int tf = stack.func_77978_p().func_74762_e("xp");
         tooltip.add("§a" + tf + " xp");
      }

      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }
}
