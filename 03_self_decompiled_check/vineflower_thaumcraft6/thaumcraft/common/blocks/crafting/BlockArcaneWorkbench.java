package thaumcraft.common.blocks.crafting;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

public class BlockArcaneWorkbench extends BlockTCDevice {
   public BlockArcaneWorkbench() {
      super(Material.field_151575_d, TileArcaneWorkbench.class, "arcane_workbench");
      this.func_149672_a(SoundType.field_185848_a);
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public boolean func_180639_a(
      World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ
   ) {
      if (world.field_72995_K) {
         return true;
      }

      player.openGui(Thaumcraft.instance, 13, world, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
      return true;
   }

   @Override
   public void func_180663_b(World world, BlockPos pos, IBlockState state) {
      TileEntity tileEntity = world.func_175625_s(pos);
      if (tileEntity != null && tileEntity instanceof TileArcaneWorkbench) {
         InventoryHelper.func_180175_a(world, pos, ((TileArcaneWorkbench)tileEntity).inventoryCraft);
      }

      super.func_180663_b(world, pos, state);
      world.func_175713_t(pos);
   }
}
