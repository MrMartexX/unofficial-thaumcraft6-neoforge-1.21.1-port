package thaumcraft.common.blocks.basic;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigItems;

public class BlockSlabTC extends BlockSlab {
   public static final PropertyEnum<BlockSlabTC.Variant> VARIANT = PropertyEnum.func_177709_a("variant", BlockSlabTC.Variant.class);
   boolean wood = false;
   Block drop = null;

   protected BlockSlabTC(String name, Block b, boolean wood) {
      super(wood ? Material.field_151575_d : Material.field_151576_e);
      this.drop = b;
      this.wood = wood;
      this.func_149663_c(name);
      this.setRegistryName("thaumcraft", name);
      IBlockState iblockstate = this.field_176227_L.func_177621_b();
      if (!this.func_176552_j()) {
         iblockstate = iblockstate.func_177226_a(field_176554_a, EnumBlockHalf.BOTTOM);
         this.func_149647_a(ConfigItems.TABTC);
      }

      this.func_149672_a(wood ? SoundType.field_185848_a : SoundType.field_185851_d);
      this.func_180632_j(iblockstate.func_177226_a(VARIANT, BlockSlabTC.Variant.DEFAULT));
      this.field_149783_u = !this.func_176552_j();
   }

   public Item func_180660_a(IBlockState state, Random rand, int fortune) {
      return this.drop == null ? Item.func_150898_a(state.func_177230_c()) : Item.func_150898_a(this.drop);
   }

   @SideOnly(Side.CLIENT)
   public ItemStack func_185473_a(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(state.func_177230_c());
   }

   public IBlockState func_176203_a(int meta) {
      IBlockState iblockstate = this.func_176223_P().func_177226_a(VARIANT, BlockSlabTC.Variant.DEFAULT);
      if (!this.func_176552_j()) {
         iblockstate = iblockstate.func_177226_a(field_176554_a, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
      }

      return iblockstate;
   }

   public int func_176201_c(IBlockState state) {
      int i = 0;
      if (!this.func_176552_j() && state.func_177229_b(field_176554_a) == EnumBlockHalf.TOP) {
         i |= 8;
      }

      return i;
   }

   protected BlockStateContainer func_180661_e() {
      return this.func_176552_j()
         ? new BlockStateContainer(this, new IProperty[]{VARIANT})
         : new BlockStateContainer(this, new IProperty[]{field_176554_a, VARIANT});
   }

   public int func_180651_a(IBlockState state) {
      return 0;
   }

   public boolean func_176552_j() {
      return false;
   }

   public String func_150002_b(int meta) {
      return this.func_149739_a();
   }

   public IProperty<?> func_176551_l() {
      return VARIANT;
   }

   public Comparable<?> func_185674_a(ItemStack stack) {
      return BlockSlabTC.Variant.DEFAULT;
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return this.wood ? 20 : super.getFlammability(world, pos, face);
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return this.wood ? 5 : super.getFireSpreadSpeed(world, pos, face);
   }

   public static class Double extends BlockSlabTC {
      public Double(String name, Block b, boolean wood) {
         super(name, b, wood);
      }

      @Override
      public boolean func_176552_j() {
         return true;
      }
   }

   public static class Half extends BlockSlabTC {
      public Half(String name, Block b, boolean wood) {
         super(name, b, wood);
      }

      @Override
      public boolean func_176552_j() {
         return false;
      }
   }

   public enum Variant implements IStringSerializable {
      DEFAULT;

      public String func_176610_l() {
         return "default";
      }
   }
}
