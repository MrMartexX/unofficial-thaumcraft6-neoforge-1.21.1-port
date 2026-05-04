package thaumcraft.common.blocks.world.plants;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.world.objects.WorldGenGreatwoodTrees;
import thaumcraft.common.world.objects.WorldGenSilverwoodTrees;

public class BlockSaplingTC extends BlockBush implements IGrowable {
   public static final PropertyInteger STAGE = PropertyInteger.func_177719_a("stage", 0, 1);
   protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.099999994F, 0.0, 0.099999994F, 0.9F, 0.8F, 0.9F);

   public BlockSaplingTC(String name) {
      this.func_149663_c(name);
      this.setRegistryName("thaumcraft", name);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(STAGE, 0));
      this.func_149647_a(ConfigItems.TABTC);
      this.func_149672_a(SoundType.field_185850_c);
   }

   public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
      return SAPLING_AABB;
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 60;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 30;
   }

   public void func_180650_b(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.field_72995_K) {
         super.func_180650_b(worldIn, pos, state, rand);
         if (worldIn.func_175671_l(pos.func_177984_a()) >= 9 && rand.nextInt(7) == 0) {
            this.grow(worldIn, pos, state, rand);
         }
      }
   }

   public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if ((Integer)state.func_177229_b(STAGE) == 0) {
         worldIn.func_180501_a(pos, state.func_177231_a(STAGE), 4);
      } else {
         this.generateTree(worldIn, pos, state, rand);
      }
   }

   public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (TerrainGen.saplingGrowTree(worldIn, rand, pos)) {
         Object object = null;
         int i = 0;
         int j = 0;
         boolean flag = false;
         if (state.func_177230_c() == BlocksTC.saplingGreatwood) {
            label45:
            for (i = 0; i >= -1; i--) {
               for (j = 0; j >= -1; j--) {
                  if (this.isTwoByTwoOfType(worldIn, pos, i, j, BlocksTC.saplingGreatwood)) {
                     object = new WorldGenGreatwoodTrees(true, false);
                     flag = true;
                     break label45;
                  }
               }
            }
         } else {
            object = new WorldGenSilverwoodTrees(true, 7, 4);
         }

         if (object != null) {
            IBlockState iblockstate1 = Blocks.field_150350_a.func_176223_P();
            if (flag) {
               worldIn.func_180501_a(pos.func_177982_a(i, 0, j), iblockstate1, 4);
               worldIn.func_180501_a(pos.func_177982_a(i + 1, 0, j), iblockstate1, 4);
               worldIn.func_180501_a(pos.func_177982_a(i, 0, j + 1), iblockstate1, 4);
               worldIn.func_180501_a(pos.func_177982_a(i + 1, 0, j + 1), iblockstate1, 4);
            } else {
               worldIn.func_180501_a(pos, iblockstate1, 4);
            }

            if (!((WorldGenerator)object).func_180709_b(worldIn, rand, pos.func_177982_a(i, 0, j))) {
               if (flag) {
                  worldIn.func_180501_a(pos.func_177982_a(i, 0, j), state, 4);
                  worldIn.func_180501_a(pos.func_177982_a(i + 1, 0, j), state, 4);
                  worldIn.func_180501_a(pos.func_177982_a(i, 0, j + 1), state, 4);
                  worldIn.func_180501_a(pos.func_177982_a(i + 1, 0, j + 1), state, 4);
               } else {
                  worldIn.func_180501_a(pos.func_177982_a(i, 0, j), state, 4);
               }
            }
         }
      }
   }

   private boolean isTwoByTwoOfType(World worldIn, BlockPos pos, int p_181624_3_, int p_181624_4_, Block type) {
      return this.isTypeAt(worldIn, pos.func_177982_a(p_181624_3_, 0, p_181624_4_), type)
         && this.isTypeAt(worldIn, pos.func_177982_a(p_181624_3_ + 1, 0, p_181624_4_), type)
         && this.isTypeAt(worldIn, pos.func_177982_a(p_181624_3_, 0, p_181624_4_ + 1), type)
         && this.isTypeAt(worldIn, pos.func_177982_a(p_181624_3_ + 1, 0, p_181624_4_ + 1), type);
   }

   public boolean isTypeAt(World worldIn, BlockPos pos, Block type) {
      IBlockState iblockstate = worldIn.func_180495_p(pos);
      return iblockstate.func_177230_c() == type;
   }

   public int func_180651_a(IBlockState state) {
      return 0;
   }

   public boolean func_176473_a(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   public boolean func_180670_a(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return worldIn.field_73012_v.nextFloat() < 0.25;
   }

   public void func_176474_b(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      this.grow(worldIn, pos, state, rand);
   }

   public IBlockState func_176203_a(int meta) {
      return this.func_176223_P().func_177226_a(STAGE, (meta & 8) >> 3);
   }

   public int func_176201_c(IBlockState state) {
      int i = 0;
      return i | (Integer)state.func_177229_b(STAGE) << 3;
   }

   protected BlockStateContainer func_180661_e() {
      return new BlockStateContainer(this, new IProperty[]{STAGE});
   }
}
