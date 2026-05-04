package thaumcraft.common.blocks.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.Utils;

public class BlockLoot extends BlockTC {
   BlockLoot.LootType type;
   Random rand = new Random();

   public BlockLoot(Material mat, String name, BlockLoot.LootType type) {
      super(mat, name);
      this.func_149711_c(0.15F);
      this.func_149752_b(0.0F);
      this.type = type;
   }

   public SoundType func_185467_w() {
      return this.field_149764_J == Material.field_151575_d ? SoundType.field_185848_a : SoundsTC.URN;
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   protected boolean func_149700_E() {
      return true;
   }

   public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
      return true;
   }

   public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
      return this.func_149688_o(state) == Material.field_151576_e
         ? new AxisAlignedBB(0.125, 0.0625, 0.125, 0.875, 0.8125, 0.875)
         : new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
   }

   public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      ArrayList<ItemStack> ret = new ArrayList<>();
      int q = 1 + this.type.ordinal() + this.rand.nextInt(3);

      for (int a = 0; a < q; a++) {
         ItemStack is = Utils.generateLoot(this.type.ordinal(), this.rand);
         if (is != null && !is.func_190926_b()) {
            ret.add(is.func_77946_l());
         }
      }

      return ret;
   }

   public enum LootType {
      COMMON,
      UNCOMMON,
      RARE;
   }
}
