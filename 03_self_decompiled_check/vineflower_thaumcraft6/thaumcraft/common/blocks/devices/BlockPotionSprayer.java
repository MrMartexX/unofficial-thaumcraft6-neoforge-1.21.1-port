package thaumcraft.common.blocks.devices;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.tiles.devices.TilePotionSprayer;

public class BlockPotionSprayer extends BlockTCDevice implements IBlockFacing, IBlockEnabled {
   public BlockPotionSprayer() {
      super(Material.field_151573_f, TilePotionSprayer.class, "potion_sprayer");
      this.func_149672_a(SoundType.field_185852_e);
   }

   public boolean func_180639_a(
      World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ
   ) {
      if (world.field_72995_K) {
         return true;
      }

      player.openGui(Thaumcraft.instance, 21, world, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
      return true;
   }

   @Override
   public int func_180651_a(IBlockState state) {
      return 0;
   }
}
