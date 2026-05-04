package thaumcraft.common.blocks.world.taint;

import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.lib.SoundsTC;

public class BlockTaintLog extends BlockTC implements ITaintBlock {
   public static final PropertyEnum AXIS = PropertyEnum.func_177709_a("axis", Axis.class);

   public BlockTaintLog() {
      super(ThaumcraftMaterials.MATERIAL_TAINT, "taint_log");
      this.setHarvestLevel("axe", 0);
      this.func_149711_c(3.0F);
      this.func_149752_b(100.0F);
      this.func_149672_a(SoundsTC.GORE);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(AXIS, Axis.Y));
      this.func_149675_a(true);
   }

   public SoundType func_185467_w() {
      return SoundsTC.GORE;
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 4;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 4;
   }

   @Override
   public void die(World world, BlockPos pos, IBlockState blockState) {
      world.func_175656_a(pos, BlocksTC.fluxGoo.func_176223_P());
   }

   public void func_180650_b(World world, BlockPos pos, IBlockState state, Random random) {
      if (!world.field_72995_K) {
         if (!TaintHelper.isNearTaintSeed(world, pos)) {
            this.die(world, pos, state);
         } else {
            TaintHelper.spreadFibres(world, pos);
         }
      }
   }

   public IBlockState func_180642_a(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int metadata, EntityLivingBase entity) {
      return super.func_180642_a(world, pos, side, hitX, hitY, hitZ, metadata, entity).func_177226_a(AXIS, side.func_176740_k());
   }

   protected ItemStack func_180643_i(IBlockState state) {
      return new ItemStack(Item.func_150898_a(this), 1, this.func_180651_a(state));
   }

   @Override
   public int func_180651_a(IBlockState state) {
      return 0;
   }

   public IBlockState func_176203_a(int meta) {
      int axis = meta % 3;
      return this.func_176223_P().func_177226_a(AXIS, Axis.values()[axis]);
   }

   public int func_176201_c(IBlockState state) {
      return ((Axis)state.func_177229_b(AXIS)).ordinal();
   }

   protected BlockStateContainer func_180661_e() {
      return new BlockStateContainer(this, new IProperty[]{AXIS});
   }

   public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
      return true;
   }

   public boolean isWood(IBlockAccess world, BlockPos pos) {
      return true;
   }

   public void func_180663_b(World worldIn, BlockPos pos, IBlockState state) {
      byte b0 = 4;
      int i = b0 + 1;
      if (worldIn.func_175707_a(pos.func_177982_a(-i, -i, -i), pos.func_177982_a(i, i, i))) {
         for (BlockPos blockpos1 : BlockPos.func_177980_a(pos.func_177982_a(-b0, -b0, -b0), pos.func_177982_a(b0, b0, b0))) {
            IBlockState iblockstate1 = worldIn.func_180495_p(blockpos1);
            if (iblockstate1.func_177230_c().isLeaves(iblockstate1, worldIn, blockpos1)) {
               iblockstate1.func_177230_c().beginLeavesDecay(iblockstate1, worldIn, blockpos1);
            }
         }
      }
   }
}
