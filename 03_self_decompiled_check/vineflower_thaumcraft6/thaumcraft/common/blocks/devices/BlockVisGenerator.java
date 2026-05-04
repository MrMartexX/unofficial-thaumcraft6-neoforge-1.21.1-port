package thaumcraft.common.blocks.devices;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.tiles.devices.TileVisGenerator;

public class BlockVisGenerator extends BlockTCDevice implements IBlockFacing, IBlockEnabled {
   public BlockVisGenerator() {
      super(Material.field_151575_d, TileVisGenerator.class, "vis_generator");
      this.func_149672_a(SoundType.field_185848_a);
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

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @Override
   public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      for (EnumFacing face : EnumFacing.field_82609_l) {
         TileEntity tileentity = worldIn.func_175625_s(pos.func_177972_a(face));
         if (tileentity != null && tileentity.hasCapability(CapabilityEnergy.ENERGY, face.func_176734_d())) {
            IEnergyStorage capability = (IEnergyStorage)tileentity.getCapability(CapabilityEnergy.ENERGY, face.func_176734_d());
            if (capability.canReceive()) {
               IBlockState bs = this.func_176223_P();
               bs = bs.func_177226_a(IBlockFacing.FACING, face);
               return bs.func_177226_a(IBlockEnabled.ENABLED, true);
            }
         }
      }

      return super.func_180642_a(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
   }

   @SideOnly(Side.CLIENT)
   public void func_180655_c(IBlockState state, World world, BlockPos pos, Random rand) {
      Block block = state.func_177230_c();
      if (block.hasTileEntity(state)) {
         TileEntity tileentity = world.func_175625_s(pos);
         if (tileentity != null) {
            EnumFacing face = BlockStateUtils.getFacing(state);
            if (tileentity.hasCapability(CapabilityEnergy.ENERGY, face)) {
               IEnergyStorage capability = (IEnergyStorage)tileentity.getCapability(CapabilityEnergy.ENERGY, face);
               if (capability.getEnergyStored() > 0) {
                  double x = face.func_82601_c() == 0 ? rand.nextGaussian() * 0.1 : face.func_82601_c() * 0.1;
                  double y = face.func_96559_d() == 0 ? rand.nextGaussian() * 0.1 : face.func_96559_d() * 0.1;
                  double z = face.func_82599_e() == 0 ? rand.nextGaussian() * 0.1 : face.func_82599_e() * 0.1;
                  FXDispatcher.INSTANCE
                     .spark(
                        pos.func_177958_n() + 0.5 + x,
                        pos.func_177956_o() + 0.5 + y,
                        pos.func_177952_p() + 0.5 + z,
                        0.66F + rand.nextFloat(),
                        0.65F + rand.nextFloat() * 0.1F,
                        1.0F,
                        1.0F,
                        0.8F
                     );
               }
            }
         }
      }
   }

   public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
      return Utils.rotateBlockAABB(new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.875, 0.75), BlockStateUtils.getFacing(this.func_176201_c(state)));
   }
}
