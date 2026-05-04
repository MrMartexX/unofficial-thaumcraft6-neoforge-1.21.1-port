package thaumcraft.common.blocks.basic;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thaumcraft.common.blocks.BlockTC;

public class BlockStoneTC extends BlockTC {
   private boolean spawn;

   public BlockStoneTC(String name, boolean spawn) {
      super(Material.field_151576_e, name);
      this.spawn = spawn;
      this.func_149711_c(2.0F);
      this.func_149752_b(10.0F);
      this.func_149672_a(SoundType.field_185851_d);
   }

   public boolean isBeaconBase(IBlockAccess world, BlockPos pos, BlockPos beacon) {
      return true;
   }

   public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
      return this.field_149782_v >= 0.0F;
   }
}
