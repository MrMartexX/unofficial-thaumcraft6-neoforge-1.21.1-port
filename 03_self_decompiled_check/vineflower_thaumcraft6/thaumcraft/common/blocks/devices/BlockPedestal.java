package thaumcraft.common.blocks.devices;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTCTile;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.crafting.TilePedestal;

public class BlockPedestal extends BlockTCTile implements IInfusionStabiliserExt {
   public static BlockPedestal instance;

   public BlockPedestal(String name) {
      super(Material.field_151576_e, TilePedestal.class, name);
      this.func_149672_a(SoundType.field_185851_d);
      instance = this;
      IBlockState bs = this.field_176227_L.func_177621_b();
      bs.func_177226_a(BlockInlay.CHARGE, 0);
      this.func_180632_j(bs);
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean func_180639_a(
      World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ
   ) {
      if (world.field_72995_K) {
         return true;
      }

      TileEntity tile = world.func_175625_s(pos);
      if (tile != null && tile instanceof TilePedestal) {
         TilePedestal ped = (TilePedestal)tile;
         if (ped.func_70301_a(0).func_190926_b()
            && !player.field_71071_by.func_70448_g().func_190926_b()
            && player.field_71071_by.func_70448_g().func_190916_E() > 0) {
            ItemStack i = player.func_184586_b(hand).func_77946_l();
            i.func_190920_e(1);
            ped.func_70299_a(0, i);
            player.func_184586_b(hand).func_190918_g(1);
            if (player.func_184586_b(hand).func_190916_E() == 0) {
               player.func_184611_a(hand, ItemStack.field_190927_a);
            }

            player.field_71071_by.func_70296_d();
            world.func_184133_a(
               null,
               pos,
               SoundEvents.field_187638_cR,
               SoundCategory.BLOCKS,
               0.2F,
               ((world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 0.7F + 1.0F) * 1.6F
            );
            return true;
         }

         if (!ped.func_70301_a(0).func_190926_b()) {
            InventoryUtils.dropItemsAtEntity(world, pos, player);
            world.func_184133_a(
               null,
               pos,
               SoundEvents.field_187638_cR,
               SoundCategory.BLOCKS,
               0.2F,
               ((world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 0.7F + 1.0F) * 1.5F
            );
            return true;
         }
      }

      return super.func_180639_a(world, pos, state, player, hand, side, hitX, hitY, hitZ);
   }

   public IBlockState func_176203_a(int meta) {
      return this.func_176223_P().func_177226_a(BlockInlay.CHARGE, meta);
   }

   public int func_176201_c(IBlockState state) {
      return (Integer)state.func_177229_b(BlockInlay.CHARGE);
   }

   protected BlockStateContainer func_180661_e() {
      return new BlockStateContainer(this, new IProperty[]{BlockInlay.CHARGE});
   }

   @SideOnly(Side.CLIENT)
   public void func_180655_c(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      int charge = (Integer)stateIn.func_177229_b(BlockInlay.CHARGE);
      if (charge > 0) {
         FXDispatcher.INSTANCE.blockRunes2(pos.func_177958_n(), pos.func_177956_o() - 0.375, pos.func_177952_p(), 1.0F, 0.0F, 0.0F, 10, 0.0F);
      }
   }

   public void func_176213_c(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.field_72995_K) {
         BlockInlay.updateSurroundingInlay(worldIn, pos, state);

         for (EnumFacing enumfacing1 : Plane.HORIZONTAL) {
            BlockInlay.notifyInlayNeighborsOfStateChange(worldIn, pos.func_177972_a(enumfacing1));
         }
      }
   }

   @Override
   public void func_180663_b(World worldIn, BlockPos pos, IBlockState state) {
      super.func_180663_b(worldIn, pos, state);
      if (!worldIn.field_72995_K) {
         for (EnumFacing enumfacing : Plane.HORIZONTAL) {
            worldIn.func_175685_c(pos.func_177972_a(enumfacing), this, false);
         }

         BlockInlay.updateSurroundingInlay(worldIn, pos, state);

         for (EnumFacing enumfacing1 : Plane.HORIZONTAL) {
            BlockInlay.notifyInlayNeighborsOfStateChange(worldIn, pos.func_177972_a(enumfacing1));
         }
      }
   }

   public void func_189540_a(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.field_72995_K) {
         BlockInlay.updateSurroundingInlay(worldIn, pos, state);
      }
   }

   @Override
   public boolean canStabaliseInfusion(World world, BlockPos pos) {
      return true;
   }

   @Override
   public float getStabilizationAmount(World world, BlockPos pos) {
      Block b = world.func_180495_p(pos).func_177230_c();
      return b == BlocksTC.pedestalEldritch ? 0.1F : 0.0F;
   }

   @Override
   public boolean hasSymmetryPenalty(World world, BlockPos pos1, BlockPos pos2) {
      TileEntity te1 = world.func_175625_s(pos1);
      TileEntity te2 = world.func_175625_s(pos2);
      if (world.field_72995_K) {
         if (te1 != null && te2 != null && te1 instanceof TilePedestal && te2 instanceof TilePedestal) {
            return ((TilePedestal)te1).getSyncedStackInSlot(0).func_190926_b() != ((TilePedestal)te2).getSyncedStackInSlot(0).func_190926_b();
         }
      } else if (te1 != null && te2 != null && te1 instanceof TilePedestal && te2 instanceof TilePedestal) {
         return ((TilePedestal)te1).func_70301_a(0).func_190926_b() != ((TilePedestal)te2).func_70301_a(0).func_190926_b();
      }

      return false;
   }

   @Override
   public float getSymmetryPenalty(World world, BlockPos pos) {
      return 0.1F;
   }
}
