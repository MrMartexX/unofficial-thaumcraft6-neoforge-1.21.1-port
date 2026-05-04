package thaumcraft.common.blocks.misc;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.lib.SoundsTC;

public class BlockFlesh extends BlockTC {
   public BlockFlesh() {
      super(Material.field_151583_m, "flesh_block");
      this.func_149752_b(2.0F);
      this.func_149711_c(0.25F);
   }

   public SoundType func_185467_w() {
      return SoundsTC.GORE;
   }
}
