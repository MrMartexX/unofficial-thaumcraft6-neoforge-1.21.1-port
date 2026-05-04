package thaumcraft.common.blocks.devices;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.world.aura.AuraHandler;

public class BlockVisBattery extends Block {
   public static final PropertyInteger CHARGE = PropertyInteger.func_177719_a("charge", 0, 10);

   public BlockVisBattery() {
      super(Material.field_151576_e);
      this.func_149663_c("vis_battery");
      this.setRegistryName("thaumcraft", "vis_battery");
      this.func_149711_c(0.5F);
      this.func_149672_a(SoundType.field_185851_d);
      this.func_149675_a(true);
      this.func_149647_a(ConfigItems.TABTC);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(CHARGE, 0));
   }

   public int func_180651_a(IBlockState state) {
      return 0;
   }

   public void func_180650_b(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.field_72995_K) {
         int charge = this.func_176201_c(state);
         if (worldIn.func_175640_z(pos)) {
            if (charge > 0) {
               AuraHandler.addVis(worldIn, pos, 1.0F);
               worldIn.func_175656_a(pos, state.func_177226_a(CHARGE, charge - 1));
               worldIn.func_175684_a(pos, state.func_177230_c(), 5);
            }
         } else {
            float aura = AuraHelper.getVis(worldIn, pos);
            int base = AuraHelper.getAuraBase(worldIn, pos);
            if (charge < 10 && aura > base * 0.9 && aura > 1.0F) {
               AuraHandler.drainVis(worldIn, pos, 1.0F, false);
               worldIn.func_175656_a(pos, state.func_177226_a(CHARGE, charge + 1));
               worldIn.func_175684_a(pos, state.func_177230_c(), 100 + rand.nextInt(100));
            } else if (charge > 0 && aura < base * 0.75) {
               AuraHandler.addVis(worldIn, pos, 1.0F);
               worldIn.func_175656_a(pos, state.func_177226_a(CHARGE, charge - 1));
               worldIn.func_175684_a(pos, state.func_177230_c(), 20 + rand.nextInt(20));
            }
         }
      }
   }

   public void func_189540_a(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (worldIn.func_175640_z(pos)) {
         worldIn.func_175684_a(pos, this, 1);
      }
   }

   public boolean func_149740_M(IBlockState state) {
      return true;
   }

   public int func_180641_l(IBlockState state, World world, BlockPos pos) {
      return this.func_176201_c(state);
   }

   public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
      return state.func_177230_c().func_176201_c(state);
   }

   public int func_185484_c(IBlockState state, IBlockAccess source, BlockPos pos) {
      int i = source.func_175626_b(pos, state.getLightValue(source, pos));
      int j = 180;
      int k = i & 0xFF;
      int l = j & 0xFF;
      int i1 = i >> 16 & 0xFF;
      int j1 = j >> 16 & 0xFF;
      return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
   }

   protected BlockStateContainer func_180661_e() {
      return new BlockStateContainer(this, new IProperty[]{CHARGE});
   }

   public IBlockState func_176203_a(int meta) {
      return this.func_176223_P().func_177226_a(CHARGE, meta);
   }

   public int func_176201_c(IBlockState state) {
      return (Integer)state.func_177229_b(CHARGE);
   }

   @SideOnly(Side.CLIENT)
   public void func_149666_a(CreativeTabs tab, NonNullList<ItemStack> list) {
      list.add(new ItemStack(this, 1, 0));
   }
}
