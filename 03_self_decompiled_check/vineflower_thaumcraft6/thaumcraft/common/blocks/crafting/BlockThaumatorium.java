package thaumcraft.common.blocks.crafting;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import thaumcraft.common.tiles.crafting.TileThaumatoriumTop;

public class BlockThaumatorium extends BlockTCDevice implements IBlockFacingHorizontal {
   boolean top;

   public BlockThaumatorium(boolean top) {
      super(Material.field_151573_f, null, top ? "thaumatorium_top" : "thaumatorium");
      this.func_149672_a(SoundType.field_185852_e);
      this.func_149647_a(null);
      this.top = top;
   }

   @Override
   public TileEntity func_149915_a(World world, int metadata) {
      if (!this.top) {
         return new TileThaumatorium();
      } else {
         return this.top ? new TileThaumatoriumTop() : null;
      }
   }

   @Override
   public void func_176213_c(World worldIn, BlockPos pos, IBlockState state) {
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public EnumBlockRenderType func_149645_b(IBlockState state) {
      return this.top ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;
   }

   public boolean func_180639_a(
      World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ
   ) {
      if (!world.field_72995_K && !player.func_70093_af()) {
         if (!this.top) {
            player.openGui(Thaumcraft.instance, 3, world, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
         } else {
            player.openGui(
               Thaumcraft.instance, 3, world, pos.func_177977_b().func_177958_n(), pos.func_177977_b().func_177956_o(), pos.func_177977_b().func_177952_p()
            );
         }
      }

      return true;
   }

   public Item func_180660_a(IBlockState state, Random rand, int fortune) {
      return Item.func_150898_a(BlocksTC.metalAlchemical);
   }

   @Override
   public int func_180651_a(IBlockState state) {
      return 0;
   }

   @Override
   public void func_180663_b(World worldIn, BlockPos pos, IBlockState state) {
      if (this.top && worldIn.func_180495_p(pos.func_177977_b()).func_177230_c() == BlocksTC.thaumatorium) {
         worldIn.func_175656_a(pos.func_177977_b(), BlocksTC.metalAlchemical.func_176223_P());
      }

      if (!this.top && worldIn.func_180495_p(pos.func_177984_a()).func_177230_c() == BlocksTC.thaumatoriumTop) {
         worldIn.func_175656_a(pos.func_177984_a(), BlocksTC.metalAlchemical.func_176223_P());
      }

      super.func_180663_b(worldIn, pos, state);
   }

   @Override
   public void func_189540_a(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
      if (!this.top && worldIn.func_180495_p(pos.func_177977_b()).func_177230_c() != BlocksTC.crucible) {
         worldIn.func_175656_a(pos, BlocksTC.metalAlchemical.func_176223_P());
         if (worldIn.func_180495_p(pos.func_177984_a()).func_177230_c() == BlocksTC.thaumatoriumTop) {
            worldIn.func_175656_a(pos.func_177984_a(), BlocksTC.metalAlchemical.func_176223_P());
         }
      }
   }

   public boolean func_149740_M(IBlockState state) {
      return !this.top;
   }

   public int func_180641_l(IBlockState state, World world, BlockPos pos) {
      TileEntity tile = world.func_175625_s(pos);
      return tile != null && tile instanceof TileThaumatorium ? Container.func_94526_b((IInventory)tile) : super.func_180641_l(state, world, pos);
   }
}
