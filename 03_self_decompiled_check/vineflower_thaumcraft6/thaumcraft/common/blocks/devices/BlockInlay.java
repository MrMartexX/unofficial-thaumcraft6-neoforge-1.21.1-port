package thaumcraft.common.blocks.devices;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.tiles.devices.TileStabilizer;

public class BlockInlay extends BlockTC implements IInfusionStabiliserExt {
   public static final PropertyEnum<BlockInlay.EnumAttachPosition> NORTH = PropertyEnum.func_177709_a("north", BlockInlay.EnumAttachPosition.class);
   public static final PropertyEnum<BlockInlay.EnumAttachPosition> EAST = PropertyEnum.func_177709_a("east", BlockInlay.EnumAttachPosition.class);
   public static final PropertyEnum<BlockInlay.EnumAttachPosition> SOUTH = PropertyEnum.func_177709_a("south", BlockInlay.EnumAttachPosition.class);
   public static final PropertyEnum<BlockInlay.EnumAttachPosition> WEST = PropertyEnum.func_177709_a("west", BlockInlay.EnumAttachPosition.class);
   public static final PropertyInteger CHARGE = PropertyInteger.func_177719_a("charge", 0, 15);
   protected static final AxisAlignedBB[] REDSTONE_WIRE_AABB = new AxisAlignedBB[]{
      new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 0.8125),
      new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 1.0),
      new AxisAlignedBB(0.0, 0.0, 0.1875, 0.8125, 0.0625, 0.8125),
      new AxisAlignedBB(0.0, 0.0, 0.1875, 0.8125, 0.0625, 1.0),
      new AxisAlignedBB(0.1875, 0.0, 0.0, 0.8125, 0.0625, 0.8125),
      new AxisAlignedBB(0.1875, 0.0, 0.0, 0.8125, 0.0625, 1.0),
      new AxisAlignedBB(0.0, 0.0, 0.0, 0.8125, 0.0625, 0.8125),
      new AxisAlignedBB(0.0, 0.0, 0.0, 0.8125, 0.0625, 1.0),
      new AxisAlignedBB(0.1875, 0.0, 0.1875, 1.0, 0.0625, 0.8125),
      new AxisAlignedBB(0.1875, 0.0, 0.1875, 1.0, 0.0625, 1.0),
      new AxisAlignedBB(0.0, 0.0, 0.1875, 1.0, 0.0625, 0.8125),
      new AxisAlignedBB(0.0, 0.0, 0.1875, 1.0, 0.0625, 1.0),
      new AxisAlignedBB(0.1875, 0.0, 0.0, 1.0, 0.0625, 0.8125),
      new AxisAlignedBB(0.1875, 0.0, 0.0, 1.0, 0.0625, 1.0),
      new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 0.8125),
      new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0)
   };

   public BlockInlay() {
      super(Material.field_151573_f, "inlay");
      this.func_149672_a(SoundType.field_185852_e);
      this.func_149711_c(0.5F);
      this.func_180632_j(
         this.field_176227_L
            .func_177621_b()
            .func_177226_a(NORTH, BlockInlay.EnumAttachPosition.NONE)
            .func_177226_a(EAST, BlockInlay.EnumAttachPosition.NONE)
            .func_177226_a(SOUTH, BlockInlay.EnumAttachPosition.NONE)
            .func_177226_a(WEST, BlockInlay.EnumAttachPosition.NONE)
            .func_177226_a(CHARGE, 0)
      );
   }

   public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
      return REDSTONE_WIRE_AABB[getAABBIndex(state.func_185899_b(source, pos))];
   }

   private static int getAABBIndex(IBlockState state) {
      int i = 0;
      boolean flag = state.func_177229_b(NORTH) != BlockInlay.EnumAttachPosition.NONE;
      boolean flag1 = state.func_177229_b(EAST) != BlockInlay.EnumAttachPosition.NONE;
      boolean flag2 = state.func_177229_b(SOUTH) != BlockInlay.EnumAttachPosition.NONE;
      boolean flag3 = state.func_177229_b(WEST) != BlockInlay.EnumAttachPosition.NONE;
      if (flag || flag2 && !flag && !flag1 && !flag3) {
         i |= 1 << EnumFacing.NORTH.func_176736_b();
      }

      if (flag1 || flag3 && !flag && !flag1 && !flag2) {
         i |= 1 << EnumFacing.EAST.func_176736_b();
      }

      if (flag2 || flag && !flag1 && !flag2 && !flag3) {
         i |= 1 << EnumFacing.SOUTH.func_176736_b();
      }

      if (flag3 || flag1 && !flag && !flag2 && !flag3) {
         i |= 1 << EnumFacing.WEST.func_176736_b();
      }

      return i;
   }

   public IBlockState func_176221_a(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      state = state.func_177226_a(WEST, this.getAttachPosition(worldIn, pos, EnumFacing.WEST));
      state = state.func_177226_a(EAST, this.getAttachPosition(worldIn, pos, EnumFacing.EAST));
      state = state.func_177226_a(NORTH, this.getAttachPosition(worldIn, pos, EnumFacing.NORTH));
      return state.func_177226_a(SOUTH, this.getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
   }

   private BlockInlay.EnumAttachPosition getAttachPosition(IBlockAccess worldIn, BlockPos pos, EnumFacing direction) {
      BlockPos blockpos = pos.func_177972_a(direction);
      IBlockState iblockstate = worldIn.func_180495_p(pos.func_177972_a(direction));
      if (!canConnectTo(worldIn.func_180495_p(blockpos), direction, worldIn, blockpos)) {
         Block b = worldIn.func_180495_p(blockpos).func_177230_c();
         return isSourceBlock(worldIn, blockpos) ? BlockInlay.EnumAttachPosition.EXT : BlockInlay.EnumAttachPosition.NONE;
      } else {
         return BlockInlay.EnumAttachPosition.SIDE;
      }
   }

   protected static boolean canConnectTo(IBlockState blockState, @Nullable EnumFacing side, IBlockAccess world, BlockPos pos) {
      Block block = blockState.func_177230_c();
      return block == BlocksTC.inlay || block instanceof BlockPedestal;
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public boolean func_176196_c(World worldIn, BlockPos pos) {
      return worldIn.func_180495_p(pos.func_177977_b()).func_185896_q();
   }

   @Nullable
   public AxisAlignedBB func_180646_a(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return field_185506_k;
   }

   public ItemStack func_185473_a(World worldIn, BlockPos pos, IBlockState state) {
      return super.func_185473_a(worldIn, pos, state);
   }

   public Item func_180660_a(IBlockState state, Random rand, int fortune) {
      return super.func_180660_a(state, rand, fortune);
   }

   @Override
   public int func_180651_a(IBlockState state) {
      return 0;
   }

   public IBlockState func_176203_a(int meta) {
      return this.func_176223_P().func_177226_a(CHARGE, meta);
   }

   public int func_176201_c(IBlockState state) {
      return (Integer)state.func_177229_b(CHARGE);
   }

   public IBlockState func_185499_a(IBlockState state, Rotation rot) {
      switch (rot) {
         case CLOCKWISE_180:
            return state.func_177226_a(NORTH, state.func_177229_b(SOUTH))
               .func_177226_a(EAST, state.func_177229_b(WEST))
               .func_177226_a(SOUTH, state.func_177229_b(NORTH))
               .func_177226_a(WEST, state.func_177229_b(EAST));
         case COUNTERCLOCKWISE_90:
            return state.func_177226_a(NORTH, state.func_177229_b(EAST))
               .func_177226_a(EAST, state.func_177229_b(SOUTH))
               .func_177226_a(SOUTH, state.func_177229_b(WEST))
               .func_177226_a(WEST, state.func_177229_b(NORTH));
         case CLOCKWISE_90:
            return state.func_177226_a(NORTH, state.func_177229_b(WEST))
               .func_177226_a(EAST, state.func_177229_b(NORTH))
               .func_177226_a(SOUTH, state.func_177229_b(EAST))
               .func_177226_a(WEST, state.func_177229_b(SOUTH));
         default:
            return state;
      }
   }

   public IBlockState func_185471_a(IBlockState state, Mirror mirrorIn) {
      switch (mirrorIn) {
         case LEFT_RIGHT:
            return state.func_177226_a(NORTH, state.func_177229_b(SOUTH)).func_177226_a(SOUTH, state.func_177229_b(NORTH));
         case FRONT_BACK:
            return state.func_177226_a(EAST, state.func_177229_b(WEST)).func_177226_a(WEST, state.func_177229_b(EAST));
         default:
            return super.func_185471_a(state, mirrorIn);
      }
   }

   protected BlockStateContainer func_180661_e() {
      return new BlockStateContainer(this, new IProperty[]{NORTH, EAST, SOUTH, WEST, CHARGE});
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @SideOnly(Side.CLIENT)
   public void func_180655_c(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      int charge = (Integer)stateIn.func_177229_b(CHARGE);
      if (charge > 0 && rand.nextInt(20 - charge) == 0) {
         EnumFacing face = EnumFacing.field_176754_o[rand.nextInt(EnumFacing.field_176754_o.length)];
         if (this.getAttachPosition(worldIn, pos, face) != BlockInlay.EnumAttachPosition.NONE) {
            double d0 = pos.func_177958_n() + 0.5 + rand.nextGaussian() * 0.08;
            double d1 = pos.func_177956_o() + 0.025F;
            double d2 = pos.func_177952_p() + 0.5 + rand.nextGaussian() * 0.08;
            double f0 = face.func_82601_c() / 70.0 * (1.0 - rand.nextFloat() * 0.1);
            double f1 = face.func_82599_e() / 70.0 * (1.0 - rand.nextFloat() * 0.1);
            float r = MathHelper.func_76136_a(rand, 150, 200) / 255.0F;
            float g = MathHelper.func_76136_a(rand, 0, 200) / 255.0F;
            FXDispatcher.INSTANCE.drawLineSparkle(rand, d0, d1, d2, f0, 0.0, f1, 0.33F, r, g, g / 2.0F, 0, 1.0F, 0.0F, 16);
         }
      }
   }

   public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
      return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
   }

   @SideOnly(Side.CLIENT)
   public static int colorMultiplier(int meta) {
      float f = meta / 15.0F;
      float f1 = f * 0.5F + 0.5F;
      if (meta == 0) {
         f1 = 0.3F;
      }

      int i = MathHelper.func_76125_a((int)(f1 * 255.0F), 0, 255);
      int j = MathHelper.func_76125_a((int)(f1 * 255.0F), 0, 255);
      int k = MathHelper.func_76125_a((int)(f1 * 255.0F), 0, 255);
      return 0xFF000000 | i << 16 | j << 8 | k;
   }

   public int func_149750_m(IBlockState state) {
      return 1;
   }

   public boolean func_189539_a(IBlockState state, World worldIn, BlockPos pos, int par5, int par6) {
      return super.func_189539_a(state, worldIn, pos, par5, par6);
   }

   public void func_176213_c(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.field_72995_K) {
         updateSurroundingInlay(worldIn, pos, state);

         for (EnumFacing enumfacing1 : Plane.HORIZONTAL) {
            notifyInlayNeighborsOfStateChange(worldIn, pos.func_177972_a(enumfacing1));
         }
      }
   }

   public static void notifyInlayNeighborsOfStateChange(World worldIn, BlockPos pos) {
      IBlockState bs = worldIn.func_180495_p(pos);
      if (bs.func_177230_c() == BlocksTC.inlay || bs.func_177230_c() instanceof BlockPedestal) {
         worldIn.func_175685_c(pos, bs.func_177230_c(), false);

         for (EnumFacing enumfacing : Plane.HORIZONTAL) {
            worldIn.func_175685_c(pos.func_177972_a(enumfacing), bs.func_177230_c(), false);
         }
      }
   }

   public static IBlockState updateSurroundingInlay(World worldIn, BlockPos pos, IBlockState state) {
      Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();
      state = calculateChanges(worldIn, pos, pos, state, blocksNeedingUpdate);

      for (BlockPos blockpos : Lists.newArrayList(blocksNeedingUpdate)) {
         worldIn.func_175685_c(blockpos, worldIn.func_180495_p(pos).func_177230_c(), false);
      }

      return state;
   }

   public static int getMaxStrength(World worldIn, BlockPos pos, int strength) {
      IBlockState bs = worldIn.func_180495_p(pos);
      if (bs.func_177230_c() != BlocksTC.inlay && !(bs.func_177230_c() instanceof BlockPedestal)) {
         return strength;
      }

      int i = (Integer)bs.func_177229_b(CHARGE);
      return i > strength ? i : strength;
   }

   public static int getSourceStrength(IBlockAccess world, BlockPos pos) {
      for (EnumFacing enumfacing : Plane.HORIZONTAL) {
         int e = getSourceStrengthAt(world, pos.func_177972_a(enumfacing));
         if (e > 0) {
            return e;
         }
      }

      return 0;
   }

   public static int getSourceStrengthAt(IBlockAccess world, BlockPos pos) {
      if (isSourceBlock(world, pos)) {
         TileEntity te = world.func_175625_s(pos);
         if (te != null && te instanceof TileStabilizer) {
            return ((TileStabilizer)te).getEnergy();
         }
      }

      return 0;
   }

   public static boolean isSourceBlock(IBlockAccess world, BlockPos pos) {
      return world.func_180495_p(pos).func_177230_c() == BlocksTC.stabilizer;
   }

   public static IBlockState calculateChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state, Set<BlockPos> blocksNeedingUpdate) {
      IBlockState iblockstate = state;
      int current = (Integer)state.func_177229_b(CHARGE);
      int max = 0;
      max = getMaxStrength(worldIn, pos2, max);
      int source = getSourceStrength(worldIn, pos1);
      if (source > 0 && source > max - 1) {
         max = source;
      }

      int neighbour = 0;

      for (EnumFacing enumfacing : Plane.HORIZONTAL) {
         BlockPos blockpos = pos1.func_177972_a(enumfacing);
         boolean flag = blockpos.func_177958_n() != pos2.func_177958_n() || blockpos.func_177952_p() != pos2.func_177952_p();
         if (flag) {
            neighbour = getMaxStrength(worldIn, blockpos, neighbour);
         }
      }

      if (neighbour > max) {
         max = neighbour - 1;
      } else if (max > 0) {
         max--;
      } else {
         max = 0;
      }

      if (source > max - 1) {
         max = source;
      }

      if (current != max) {
         state = state.func_177226_a(CHARGE, max);
         if (worldIn.func_180495_p(pos1) == iblockstate) {
            worldIn.func_180501_a(pos1, state, 2);
         }

         blocksNeedingUpdate.add(pos1);

         for (EnumFacing enumfacing1 : EnumFacing.values()) {
            blocksNeedingUpdate.add(pos1.func_177972_a(enumfacing1));
         }
      }

      return state;
   }

   public void func_180663_b(World worldIn, BlockPos pos, IBlockState state) {
      super.func_180663_b(worldIn, pos, state);
      if (!worldIn.field_72995_K) {
         for (EnumFacing enumfacing : Plane.HORIZONTAL) {
            worldIn.func_175685_c(pos.func_177972_a(enumfacing), this, false);
         }

         updateSurroundingInlay(worldIn, pos, state);

         for (EnumFacing enumfacing1 : Plane.HORIZONTAL) {
            notifyInlayNeighborsOfStateChange(worldIn, pos.func_177972_a(enumfacing1));
         }
      }
   }

   public void func_189540_a(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.field_72995_K) {
         if (this.func_176196_c(worldIn, pos)) {
            updateSurroundingInlay(worldIn, pos, state);
         } else {
            this.func_176226_b(worldIn, pos, state, 0);
            worldIn.func_175698_g(pos);
         }
      }
   }

   @Override
   public boolean canStabaliseInfusion(World world, BlockPos pos) {
      return true;
   }

   @Override
   public float getStabilizationAmount(World world, BlockPos pos) {
      return 0.025F;
   }

   enum EnumAttachPosition implements IStringSerializable {
      SIDE("side"),
      NONE("none"),
      EXT("ext");

      private final String name;

      EnumAttachPosition(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.func_176610_l();
      }

      public String func_176610_l() {
         return this.name;
      }
   }
}
