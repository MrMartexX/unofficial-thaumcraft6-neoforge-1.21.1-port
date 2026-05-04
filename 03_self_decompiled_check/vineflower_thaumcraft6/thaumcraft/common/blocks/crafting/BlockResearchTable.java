package thaumcraft.common.blocks.crafting;

import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.Thaumcraft;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.tiles.crafting.TileResearchTable;

public class BlockResearchTable extends BlockTCDevice implements IBlockFacingHorizontal {
   public BlockResearchTable() {
      super(Material.field_151575_d, TileResearchTable.class, "research_table");
      this.func_149672_a(SoundType.field_185848_a);
   }

   @Override
   public int func_180651_a(IBlockState state) {
      return 0;
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      return false;
   }

   public boolean func_180639_a(
      World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ
   ) {
      if (world.field_72995_K) {
         return true;
      }

      player.openGui(Thaumcraft.instance, 10, world, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
      return true;
   }

   @Override
   public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState bs = this.func_176223_P();
      return bs.func_177226_a(IBlockFacingHorizontal.FACING, placer.func_174811_aO());
   }

   @SideOnly(Side.CLIENT)
   public void func_180655_c(IBlockState state, World world, BlockPos pos, Random rand) {
      TileEntity te = world.func_175625_s(pos);
      if (rand.nextInt(5) == 0 && te != null && ((TileResearchTable)te).data != null) {
         double xx = rand.nextGaussian() / 2.0;
         double zz = rand.nextGaussian() / 2.0;
         double yy = 1.5 + rand.nextFloat();
         int a = 40 + rand.nextInt(20);
         FXGeneric fb = new FXGeneric(
            world, pos.func_177958_n() + 0.5 + xx, pos.func_177956_o() + yy, pos.func_177952_p() + 0.5 + zz, -xx / a, -(yy - 0.85) / a, -zz / a
         );
         fb.func_187114_a(a);
         fb.func_70538_b(0.5F + rand.nextFloat() * 0.5F, 0.5F + rand.nextFloat() * 0.5F, 0.5F + rand.nextFloat() * 0.5F);
         fb.setAlphaF(0.0F, 0.25F, 0.5F, 0.75F, 0.0F);
         fb.setParticles(384 + rand.nextInt(16), 1, 1);
         fb.setScale(0.8F + rand.nextFloat() * 0.3F, 0.3F);
         fb.setLayer(0);
         ParticleEngine.addEffect(world, fb);
      }
   }
}
