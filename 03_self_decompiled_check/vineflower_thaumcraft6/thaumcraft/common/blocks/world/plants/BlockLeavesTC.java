package thaumcraft.common.blocks.world.plants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.world.aura.AuraHandler;

public class BlockLeavesTC extends BlockLeaves {
   public BlockLeavesTC(String name) {
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176236_b, true).func_177226_a(field_176237_a, true));
      this.func_149647_a(ConfigItems.TABTC);
      this.func_149663_c(name);
      this.setRegistryName("thaumcraft", name);
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 60;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 30;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer func_180664_k() {
      return Blocks.field_150362_t.func_180664_k();
   }

   public boolean func_149662_c(IBlockState state) {
      return Blocks.field_150362_t.func_149662_c(state);
   }

   public void func_180633_a(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      super.func_180633_a(worldIn, pos, state, placer, stack);
      if (placer != null && placer instanceof EntityPlayer) {
         worldIn.func_175656_a(pos, state.func_177226_a(field_176237_a, false));
      }
   }

   public boolean func_176225_a(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      this.func_150122_b(!this.func_149662_c(blockState));
      return super.func_176225_a(blockState, blockAccess, pos, side);
   }

   public MapColor func_180659_g(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.func_177230_c() == BlocksTC.leafSilverwood ? MapColor.field_151674_s : super.func_180659_g(state, worldIn, pos);
   }

   public int func_180651_a(IBlockState state) {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public void func_149666_a(CreativeTabs tab, NonNullList<ItemStack> list) {
      list.add(new ItemStack(this));
   }

   protected ItemStack func_180643_i(IBlockState state) {
      return new ItemStack(this);
   }

   public void func_180650_b(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.field_72995_K
         && state.func_177230_c() == BlocksTC.leafSilverwood
         && (Boolean)state.func_177229_b(field_176237_a)
         && AuraHandler.getVis(worldIn, pos) < AuraHandler.getAuraBase(worldIn, pos)) {
         AuraHandler.addVis(worldIn, pos, 0.01F);
      }

      super.func_180650_b(worldIn, pos, state, rand);
   }

   public IBlockState func_176203_a(int meta) {
      return this.func_176223_P().func_177226_a(field_176237_a, (meta & 4) == 0).func_177226_a(field_176236_b, (meta & 8) > 0);
   }

   public int func_176201_c(IBlockState state) {
      int i = 0;
      if (!(Boolean)state.func_177229_b(field_176237_a)) {
         i |= 4;
      }

      if ((Boolean)state.func_177229_b(field_176236_b)) {
         i |= 8;
      }

      return i;
   }

   protected int func_176232_d(IBlockState state) {
      return 75;
   }

   protected void func_176234_a(World worldIn, BlockPos pos, IBlockState state, int chance) {
      if (state.func_177230_c() == BlocksTC.leafSilverwood && worldIn.field_73012_v.nextInt((int)(chance * 0.75)) == 0) {
         func_180635_a(worldIn, pos, new ItemStack(ItemsTC.nuggets, 1, 5));
      }
   }

   public Item func_180660_a(IBlockState state, Random rand, int fortune) {
      return state.func_177230_c() == BlocksTC.leafSilverwood ? Item.func_150898_a(BlocksTC.saplingSilverwood) : Item.func_150898_a(BlocksTC.saplingGreatwood);
   }

   protected BlockStateContainer func_180661_e() {
      return new BlockStateContainer(this, new IProperty[]{field_176236_b, field_176237_a});
   }

   public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
      IBlockState state = world.func_180495_p(pos);
      return new ArrayList<>(Arrays.asList(new ItemStack(this)));
   }

   public EnumType func_176233_b(int meta) {
      return null;
   }
}
