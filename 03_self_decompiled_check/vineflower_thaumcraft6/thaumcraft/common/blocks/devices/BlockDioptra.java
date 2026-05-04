package thaumcraft.common.blocks.devices;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.tiles.devices.TileDioptra;

public class BlockDioptra extends BlockTCDevice implements IBlockEnabled {
   public BlockDioptra() {
      super(Material.field_151576_e, TileDioptra.class, "dioptra");
      this.func_149672_a(SoundType.field_185851_d);
   }

   @Override
   public int func_180651_a(IBlockState state) {
      return 0;
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public boolean func_149740_M(IBlockState state) {
      return true;
   }

   public int func_180641_l(IBlockState state, World world, BlockPos pos) {
      TileEntity tile = world.func_175625_s(pos);
      if (tile != null && tile instanceof TileDioptra) {
         float r = ((TileDioptra)tile).grid_amt[84] / 64.0F;
         return MathHelper.func_76141_d(r * 14.0F) + (r > 0.0F ? 1 : 0);
      } else {
         return super.func_180641_l(state, world, pos);
      }
   }

   @Override
   protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
   }

   public boolean func_180639_a(
      World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ
   ) {
      boolean b = (Boolean)state.func_177229_b(IBlockEnabled.ENABLED);
      world.func_180501_a(pos, state.func_177226_a(IBlockEnabled.ENABLED, !b), 3);
      return true;
   }
}
