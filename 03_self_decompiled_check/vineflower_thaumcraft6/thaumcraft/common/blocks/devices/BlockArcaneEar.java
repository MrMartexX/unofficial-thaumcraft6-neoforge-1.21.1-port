package thaumcraft.common.blocks.devices;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileArcaneEar;

public class BlockArcaneEar extends BlockTCDevice implements IBlockFacing, IBlockEnabled {
   private static final List<SoundEvent> INSTRUMENTS = Lists.newArrayList(
      new SoundEvent[]{
         SoundEvents.field_187682_dG,
         SoundEvents.field_187676_dE,
         SoundEvents.field_187688_dI,
         SoundEvents.field_187685_dH,
         SoundEvents.field_187679_dF,
         SoundEvents.field_193809_ey,
         SoundEvents.field_193807_ew,
         SoundEvents.field_193810_ez,
         SoundEvents.field_193808_ex,
         SoundEvents.field_193785_eE
      }
   );

   public BlockArcaneEar(String name) {
      super(Material.field_151575_d, TileArcaneEar.class, name);
      this.func_149672_a(SoundType.field_185848_a);
      this.func_149711_c(1.0F);
      IBlockState bs = this.field_176227_L.func_177621_b();
      bs.func_177226_a(IBlockFacing.FACING, EnumFacing.UP);
      bs.func_177226_a(IBlockEnabled.ENABLED, false);
      this.func_180632_j(bs);
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
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
      bs = bs.func_177226_a(IBlockFacing.FACING, facing);
      return bs.func_177226_a(IBlockEnabled.ENABLED, false);
   }

   @Override
   public void func_176213_c(World worldIn, BlockPos pos, IBlockState state) {
      TileArcaneEar tile = (TileArcaneEar)worldIn.func_175625_s(pos);
      if (tile != null) {
         tile.updateTone();
      }
   }

   @Override
   public void func_189540_a(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
      TileArcaneEar tile = (TileArcaneEar)worldIn.func_175625_s(pos);
      if (tile != null) {
         tile.updateTone();
      }

      if (!worldIn.func_180495_p(pos.func_177972_a(BlockStateUtils.getFacing(state).func_176734_d()))
         .isSideSolid(worldIn, pos.func_177972_a(BlockStateUtils.getFacing(state).func_176734_d()), BlockStateUtils.getFacing(state))) {
         this.func_176226_b(worldIn, pos, this.func_176223_P(), 0);
         worldIn.func_175698_g(pos);
      }
   }

   public boolean func_180639_a(
      World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ
   ) {
      if (world.field_72995_K) {
         return true;
      }

      TileArcaneEar tile = (TileArcaneEar)world.func_175625_s(pos);
      if (tile != null) {
         tile.changePitch();
         tile.triggerNote(world, pos, true);
      }

      return true;
   }

   public boolean func_149744_f(IBlockState state) {
      return true;
   }

   public int func_180656_a(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return BlockStateUtils.isEnabled(state.func_177230_c().func_176201_c(state)) ? 15 : 0;
   }

   public int func_176211_b(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return BlockStateUtils.isEnabled(state.func_177230_c().func_176201_c(state)) ? 15 : 0;
   }

   public boolean func_176198_a(World worldIn, BlockPos pos, EnumFacing side) {
      return worldIn.func_180495_p(pos.func_177972_a(side.func_176734_d())).isSideSolid(worldIn, pos.func_177972_a(side.func_176734_d()), side);
   }

   // $VF: Unable to simplify switch-on-enum, as the enum class was not able to be found.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
      EnumFacing facing = BlockStateUtils.getFacing(this.func_176201_c(state));
      switch (facing.ordinal()) {
         case 0:
            return new AxisAlignedBB(0.125, 0.625, 0.125, 0.875, 1.0, 0.875);
         case 1:
            return new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 0.375, 0.875);
         case 2:
            return new AxisAlignedBB(0.125, 0.125, 0.625, 0.875, 0.875, 1.0);
         case 3:
            return new AxisAlignedBB(0.125, 0.125, 0.0, 0.875, 0.875, 0.375);
         case 4:
            return new AxisAlignedBB(0.625, 0.125, 0.125, 1.0, 0.875, 0.875);
         default:
            return new AxisAlignedBB(0.0, 0.125, 0.125, 0.375, 0.875, 0.875);
      }
   }

   protected SoundEvent getInstrument(int type) {
      if (type < 0 || type >= INSTRUMENTS.size()) {
         type = 0;
      }

      return INSTRUMENTS.get(type);
   }

   @Override
   public boolean func_189539_a(IBlockState state, World worldIn, BlockPos pos, int par5, int par6) {
      super.func_189539_a(state, worldIn, pos, par5, par6);
      float var7 = (float)Math.pow(2.0, (par6 - 12) / 12.0);
      worldIn.func_184133_a((EntityPlayer)null, pos, this.getInstrument(par5), SoundCategory.BLOCKS, 3.0F, var7);
      worldIn.func_175688_a(
         EnumParticleTypes.NOTE, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, par6 / 24.0, 0.0, 0.0, new int[0]
      );
      return true;
   }
}
