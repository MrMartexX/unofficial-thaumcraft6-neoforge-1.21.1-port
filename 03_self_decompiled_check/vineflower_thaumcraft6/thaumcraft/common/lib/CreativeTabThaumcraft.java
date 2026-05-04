package thaumcraft.common.lib;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.ItemsTC;

public final class CreativeTabThaumcraft extends CreativeTabs {
   public CreativeTabThaumcraft(int par1, String par2Str) {
      super(par1, par2Str);
   }

   @SideOnly(Side.CLIENT)
   public ItemStack func_78016_d() {
      return new ItemStack(ItemsTC.goggles);
   }
}
