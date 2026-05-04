package thaumcraft.common.blocks.essentia;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.utils.BlockStateUtils;

public class BlockEssentiaTransport extends BlockTCDevice implements IBlockFacing {
   public BlockEssentiaTransport(Class te, String name) {
      super(Material.field_151573_f, te, name);
      this.func_149672_a(SoundType.field_185852_e);
      this.func_149711_c(1.0F);
      this.func_149752_b(10.0F);
      IBlockState bs = this.field_176227_L.func_177621_b();
      bs.func_177226_a(IBlockFacing.FACING, EnumFacing.UP);
      this.func_180632_j(bs);
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @Override
   public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
      return true;
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   @Override
   public int func_180651_a(IBlockState state) {
      return 0;
   }

   @Override
   public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState bs = this.func_176223_P();
      return bs.func_177226_a(IBlockFacing.FACING, facing);
   }

   // $VF: Unable to simplify switch-on-enum, as the enum class was not able to be found.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
      EnumFacing facing = BlockStateUtils.getFacing(state);
      switch (facing.ordinal()) {
         case 1:
            return new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.5, 0.75);
         case 2:
            return new AxisAlignedBB(0.25, 0.25, 0.5, 0.75, 0.75, 1.0);
         case 3:
            return new AxisAlignedBB(0.25, 0.25, 0.0, 0.75, 0.75, 0.5);
         case 4:
            return new AxisAlignedBB(0.5, 0.25, 0.25, 1.0, 0.75, 0.75);
         case 5:
            return new AxisAlignedBB(0.0, 0.25, 0.25, 0.5, 0.75, 0.75);
         default:
            return new AxisAlignedBB(0.25, 0.5, 0.25, 0.75, 1.0, 0.75);
      }
   }
}
