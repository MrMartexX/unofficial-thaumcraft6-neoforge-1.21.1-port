package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigItems;

public class BlockTC extends Block {
   public BlockTC(Material material, String name) {
      super(material);
      this.func_149663_c(name);
      this.setRegistryName("thaumcraft", name);
      this.func_149647_a(ConfigItems.TABTC);
      this.func_149752_b(2.0F);
      this.func_149711_c(1.5F);
   }

   public BlockTC(Material mat, String name, SoundType st) {
      this(mat, name);
      this.func_149672_a(st);
   }

   @SideOnly(Side.CLIENT)
   public void func_149666_a(CreativeTabs tab, NonNullList<ItemStack> list) {
      list.add(new ItemStack(this, 1, 0));
   }

   public int func_180651_a(IBlockState state) {
      return 0;
   }
}
