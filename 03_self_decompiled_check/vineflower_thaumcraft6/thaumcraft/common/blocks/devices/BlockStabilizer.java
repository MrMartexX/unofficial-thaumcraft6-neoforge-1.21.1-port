package thaumcraft.common.blocks.devices;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.tiles.devices.TileStabilizer;

public class BlockStabilizer extends BlockTCDevice implements IInfusionStabiliserExt {
   public BlockStabilizer() {
      super(Material.field_151576_e, TileStabilizer.class, "stabilizer");
      this.func_149672_a(SoundType.field_185851_d);
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

   @SideOnly(Side.CLIENT)
   public static int colorMultiplier(int meta) {
      float f = meta / 15.0F;
      float f1 = f * 0.5F + 0.5F;
      if (meta == 0) {
         f1 = 0.3F;
      }

      int i = MathHelper.func_76125_a((int)(f1 * 255.0F), 0, 255);
      int j = MathHelper.func_76125_a((int)(f1 * 255.0F), 0, 255);
      int k = MathHelper.func_76125_a((int)(f1 * 255.0F), 0, 255);
      return 0xFF000000 | i << 16 | j << 8 | k;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public int func_149750_m(IBlockState state) {
      return 4;
   }

   @Override
   public boolean canStabaliseInfusion(World world, BlockPos pos) {
      return true;
   }

   @Override
   public float getStabilizationAmount(World world, BlockPos pos) {
      return 0.25F;
   }
}
