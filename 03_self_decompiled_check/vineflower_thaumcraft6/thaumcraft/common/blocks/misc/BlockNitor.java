package thaumcraft.common.blocks.misc;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.tiles.misc.TileNitor;

public class BlockNitor extends BlockTC implements ITileEntityProvider {
   public final EnumDyeColor dye;

   public BlockNitor(String name, EnumDyeColor dye) {
      super(Material.field_151594_q, name);
      this.func_149711_c(0.1F);
      this.func_149672_a(SoundType.field_185854_g);
      this.func_149715_a(1.0F);
      this.dye = dye;
   }

   public TileEntity func_149915_a(World worldIn, int meta) {
      return new TileNitor();
   }

   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   public MapColor func_180659_g(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return MapColor.func_193558_a(this.dye);
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public EnumBlockRenderType func_149645_b(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
      return new AxisAlignedBB(0.33F, 0.33F, 0.33F, 0.66F, 0.66F, 0.66F);
   }

   public AxisAlignedBB func_180646_a(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return null;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }
}
