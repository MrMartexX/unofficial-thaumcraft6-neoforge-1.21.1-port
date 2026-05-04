package thaumcraft.common.blocks.world.taint;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;
import thaumcraft.common.lib.utils.BlockStateUtils;

public class BlockTaintFeature extends BlockTC implements ITaintBlock {
   public BlockTaintFeature() {
      super(ThaumcraftMaterials.MATERIAL_TAINT, "taint_feature");
      this.func_149711_c(0.1F);
      this.func_149715_a(0.625F);
      IBlockState bs = this.field_176227_L.func_177621_b();
      bs.func_177226_a(IBlockFacing.FACING, EnumFacing.UP);
      this.func_180632_j(bs);
      this.func_149675_a(true);
   }

   protected boolean func_149700_E() {
      return false;
   }

   public void func_180663_b(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.field_72995_K) {
         if (worldIn.field_73012_v.nextFloat() < 0.333F) {
            Entity e = new EntityTaintCrawler(worldIn);
            e.func_70012_b(pos.func_177958_n() + 0.5F, pos.func_177956_o() + 0.5F, pos.func_177952_p() + 0.5F, worldIn.field_73012_v.nextInt(360), 0.0F);
            worldIn.func_72838_d(e);
         } else {
            AuraHelper.polluteAura(worldIn, pos, 1.0F, true);
         }
      }

      super.func_180663_b(worldIn, pos, state);
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @Override
   public void die(World world, BlockPos pos, IBlockState blockState) {
      world.func_175656_a(pos, BlocksTC.fluxGoo.func_176223_P());
   }

   public void func_180650_b(World world, BlockPos pos, IBlockState state, Random random) {
      if (!world.field_72995_K) {
         if (!TaintHelper.isNearTaintSeed(world, pos) && random.nextInt(10) == 0) {
            this.die(world, pos, state);
            return;
         }

         TaintHelper.spreadFibres(world, pos);
         if (world.func_180495_p(pos.func_177977_b()).func_177230_c() == BlocksTC.taintLog
            && world.func_180495_p(pos.func_177977_b()).func_177229_b(BlockTaintLog.AXIS) == Axis.Y
            && world.field_73012_v.nextInt(100) == 0) {
            world.func_175656_a(pos, BlocksTC.taintGeyser.func_176223_P());
         }
      }
   }

   @Override
   public int func_180651_a(IBlockState state) {
      return 0;
   }

   public Item func_180660_a(IBlockState state, Random rand, int fortune) {
      return Item.func_150899_d(0);
   }

   public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      return true;
   }

   public int func_185484_c(IBlockState state, IBlockAccess source, BlockPos pos) {
      return 200;
   }

   public void func_189540_a(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
      if (!worldIn.field_72995_K
         && !worldIn.func_180495_p(pos.func_177972_a(BlockStateUtils.getFacing(state).func_176734_d()))
            .isSideSolid(worldIn, pos.func_177972_a(BlockStateUtils.getFacing(state).func_176734_d()), BlockStateUtils.getFacing(state))) {
         worldIn.func_175698_g(pos);
      }
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState bs = this.func_176223_P();
      return bs.func_177226_a(IBlockFacing.FACING, facing);
   }

   public IBlockState func_176203_a(int meta) {
      IBlockState bs = this.func_176223_P();
      return bs.func_177226_a(IBlockFacing.FACING, BlockStateUtils.getFacing(meta));
   }

   public int func_176201_c(IBlockState state) {
      byte b0 = 0;
      return b0 | ((EnumFacing)state.func_177229_b(IBlockFacing.FACING)).func_176745_a();
   }

   protected BlockStateContainer func_180661_e() {
      ArrayList<IProperty> ip = new ArrayList<>();
      ip.add(IBlockFacing.FACING);
      return new BlockStateContainer(this, ip.toArray(new IProperty[ip.size()]));
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
         case 5:
            return new AxisAlignedBB(0.0, 0.125, 0.125, 0.375, 0.875, 0.875);
         default:
            return super.func_185496_a(state, source, pos);
      }
   }
}
