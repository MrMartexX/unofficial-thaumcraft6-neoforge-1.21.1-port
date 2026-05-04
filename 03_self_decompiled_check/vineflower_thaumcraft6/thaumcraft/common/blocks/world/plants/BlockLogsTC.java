package thaumcraft.common.blocks.world.plants;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;

public class BlockLogsTC extends BlockTC {
   public static final PropertyEnum AXIS = PropertyEnum.func_177709_a("axis", Axis.class);

   public BlockLogsTC(String name) {
      super(Material.field_151575_d, name);
      this.setHarvestLevel("axe", 0);
      this.func_149711_c(2.0F);
      this.func_149752_b(5.0F);
      this.func_149672_a(SoundType.field_185848_a);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(AXIS, Axis.Y));
   }

   public IBlockState func_180642_a(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int metadata, EntityLivingBase entity) {
      return super.func_180642_a(world, pos, side, hitX, hitY, hitZ, metadata, entity).func_177226_a(AXIS, side.func_176740_k());
   }

   public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
      return state.func_177230_c() == BlocksTC.logSilverwood ? 5 : super.getLightValue(state, world, pos);
   }

   public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
      IBlockState state = world.func_180495_p(pos);
      UnmodifiableIterator var5 = state.func_177228_b().keySet().iterator();

      while (var5.hasNext()) {
         IProperty<?> prop = (IProperty<?>)var5.next();
         if (prop.func_177701_a().equals("axis")) {
            world.func_175656_a(pos, state.func_177231_a(prop));
            return true;
         }
      }

      return false;
   }

   protected ItemStack func_180643_i(IBlockState state) {
      return new ItemStack(Item.func_150898_a(this));
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

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 5;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 5;
   }
}
