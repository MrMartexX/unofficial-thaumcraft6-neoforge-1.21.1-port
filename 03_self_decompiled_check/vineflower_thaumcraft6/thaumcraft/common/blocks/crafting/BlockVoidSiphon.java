package thaumcraft.common.blocks.crafting;

import java.util.List;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;

@EventBusSubscriber(Side.CLIENT)
public class BlockVoidSiphon extends BlockTCDevice implements IBlockEnabled {
   protected static final AxisAlignedBB AABB_MAIN = new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 1.0, 0.8125);
   protected static final AxisAlignedBB AABB_BASE = new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.125, 0.8125);
   protected static final AxisAlignedBB AABB_TOP = new AxisAlignedBB(0.25, 0.125, 0.25, 0.75, 0.6875, 0.75);
   protected static final AxisAlignedBB AABB_ORB = new AxisAlignedBB(0.3125, 0.75, 0.3125, 0.625, 1.0, 0.625);

   public BlockVoidSiphon() {
      super(Material.field_151573_f, TileVoidSiphon.class, "void_siphon");
      this.func_149672_a(SoundType.field_185852_e);
   }

   @Override
   public int func_180651_a(IBlockState state) {
      return 0;
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      return false;
   }

   public void func_185477_a(
      IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB AABB, List<AxisAlignedBB> list, Entity p_185477_6_, boolean isActualState
   ) {
      func_185492_a(pos, AABB, list, AABB_BASE);
      func_185492_a(pos, AABB, list, AABB_TOP);
      func_185492_a(pos, AABB, list, AABB_ORB);
   }

   public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB_MAIN;
   }

   public boolean func_180639_a(
      World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ
   ) {
      if (world.field_72995_K) {
         return true;
      }

      player.openGui(Thaumcraft.instance, 22, world, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
      return true;
   }
}
