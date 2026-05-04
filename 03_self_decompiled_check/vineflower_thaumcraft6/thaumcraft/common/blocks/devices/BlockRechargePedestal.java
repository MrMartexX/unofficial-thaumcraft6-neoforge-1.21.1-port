package thaumcraft.common.blocks.devices;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.devices.TileRechargePedestal;

public class BlockRechargePedestal extends BlockTCDevice {
   public BlockRechargePedestal() {
      super(Material.field_151576_e, TileRechargePedestal.class, "recharge_pedestal");
      this.func_149672_a(SoundType.field_185851_d);
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
      if (tile != null && tile instanceof TileRechargePedestal) {
         TileRechargePedestal ped = (TileRechargePedestal)tile;
         if (ped.func_70301_a(0).func_190926_b() && player.field_71071_by.func_70448_g().func_77973_b() instanceof IRechargable) {
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
}
