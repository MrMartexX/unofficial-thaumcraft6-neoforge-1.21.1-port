package thaumcraft.common.blocks.devices;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.tiles.devices.TileWaterJug;

public class BlockWaterJug extends BlockTCDevice {
   public BlockWaterJug() {
      super(Material.field_151576_e, TileWaterJug.class, "everfull_urn");
      this.func_149672_a(SoundType.field_185851_d);
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
      return new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 1.0, 0.8125);
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean func_180639_a(
      World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ
   ) {
      if (!world.field_72995_K) {
         TileEntity te = world.func_175625_s(pos);
         if (te != null && te instanceof TileWaterJug) {
            TileWaterJug tile = (TileWaterJug)te;
            if (FluidUtil.interactWithFluidHandler(player, hand, tile.tank)) {
               player.field_71069_bz.func_75142_b();
               te.func_70296_d();
               tile.syncTile(false);
               world.func_184133_a(
                  null,
                  pos,
                  SoundEvents.field_187615_H,
                  SoundCategory.BLOCKS,
                  0.33F,
                  1.0F + (world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 0.3F
               );
            } else if (player.func_184586_b(hand).func_77973_b() == Items.field_151069_bo && tile.tank.getFluidAmount() >= 333) {
               ItemStack itemstack = player.func_184586_b(hand);
               ItemStack itemstack3 = PotionUtils.func_185188_a(new ItemStack(Items.field_151068_bn), PotionTypes.field_185230_b);
               if (!player.field_71075_bZ.field_75098_d) {
                  itemstack.func_190918_g(1);
               }

               if (itemstack.func_190926_b()) {
                  player.func_184611_a(hand, itemstack3);
               } else if (!player.field_71071_by.func_70441_a(itemstack3)) {
                  player.func_71019_a(itemstack3, false);
               } else if (player instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)player).func_71120_a(player.field_71069_bz);
               }

               tile.drain(new FluidStack(FluidRegistry.WATER, 333), true);
               world.func_184133_a(
                  null,
                  pos,
                  SoundEvents.field_187615_H,
                  SoundCategory.BLOCKS,
                  0.33F,
                  1.0F + (world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 0.3F
               );
            }
         }
      }

      return true;
   }

   @SideOnly(Side.CLIENT)
   public void func_180655_c(IBlockState state, World world, BlockPos pos, Random rand) {
      Block block = state.func_177230_c();
      if (block.hasTileEntity(state)) {
         TileEntity te = world.func_175625_s(pos);
         if (te != null && te instanceof TileWaterJug) {
            TileWaterJug tile = (TileWaterJug)te;
            if (tile.tank.getFluidAmount() >= tile.tank.getCapacity()) {
               FXDispatcher.INSTANCE.jarSplashFx(pos.func_177958_n() + 0.5, pos.func_177956_o() + 1, pos.func_177952_p() + 0.5);
            }
         }
      }
   }
}
