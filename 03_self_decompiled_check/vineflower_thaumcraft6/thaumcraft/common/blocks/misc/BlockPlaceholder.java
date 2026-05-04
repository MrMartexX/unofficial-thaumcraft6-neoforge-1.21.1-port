package thaumcraft.common.blocks.misc;

import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.blocks.crafting.BlockGolemBuilder;
import thaumcraft.common.blocks.devices.BlockInfernalFurnace;

public class BlockPlaceholder extends BlockTC {
   private final Random rand = new Random();

   public BlockPlaceholder(String name) {
      super(Material.field_151576_e, name);
      this.func_149711_c(2.5F);
      this.func_149672_a(SoundType.field_185851_d);
      this.func_149647_a(null);
   }

   public EnumPushReaction func_149656_h(IBlockState state) {
      return EnumPushReaction.BLOCK;
   }

   protected boolean func_149700_E() {
      return false;
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
      return state.func_177230_c() == BlocksTC.placeholderCauldron ? 13 : super.getLightValue(state, world, pos);
   }

   public EnumBlockRenderType func_149645_b(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public Item func_180660_a(IBlockState state, Random rand, int fortune) {
      if (state.func_177230_c() == BlocksTC.placeholderNetherbrick) {
         return Item.func_150898_a(Blocks.field_150385_bj);
      } else if (state.func_177230_c() == BlocksTC.placeholderObsidian) {
         return Item.func_150898_a(Blocks.field_150343_Z);
      } else if (state.func_177230_c() == BlocksTC.placeholderBars) {
         return Item.func_150898_a(Blocks.field_150411_aY);
      } else if (state.func_177230_c() == BlocksTC.placeholderAnvil) {
         return Item.func_150898_a(Blocks.field_150467_bQ);
      } else if (state.func_177230_c() == BlocksTC.placeholderCauldron) {
         return Item.func_150898_a(Blocks.field_150383_bp);
      } else {
         return state.func_177230_c() == BlocksTC.placeholderTable ? Item.func_150898_a(BlocksTC.tableStone) : Item.func_150899_d(0);
      }
   }

   @Override
   public int func_180651_a(IBlockState state) {
      return 0;
   }

   public boolean func_180639_a(
      World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ
   ) {
      if (world.field_72995_K) {
         return true;
      }

      if (state.func_177230_c() != BlocksTC.placeholderNetherbrick && state.func_177230_c() != BlocksTC.placeholderObsidian) {
         for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
               for (int c = -1; c <= 1; c++) {
                  IBlockState s = world.func_180495_p(pos.func_177982_a(a, b, c));
                  if (s.func_177230_c() == BlocksTC.golemBuilder) {
                     player.openGui(
                        Thaumcraft.instance,
                        19,
                        world,
                        pos.func_177982_a(a, b, c).func_177958_n(),
                        pos.func_177982_a(a, b, c).func_177956_o(),
                        pos.func_177982_a(a, b, c).func_177952_p()
                     );
                     return true;
                  }
               }
            }
         }
      }

      return super.func_180639_a(world, pos, state, player, hand, side, hitX, hitY, hitZ);
   }

   public void func_180663_b(World worldIn, BlockPos pos, IBlockState state) {
      if ((state.func_177230_c() == BlocksTC.placeholderNetherbrick || state.func_177230_c() == BlocksTC.placeholderObsidian)
         && !BlockInfernalFurnace.ignore
         && !worldIn.field_72995_K) {
         label76:
         for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
               for (int c = -1; c <= 1; c++) {
                  IBlockState s = worldIn.func_180495_p(pos.func_177982_a(a, b, c));
                  if (s.func_177230_c() == BlocksTC.infernalFurnace) {
                     BlockInfernalFurnace.destroyFurnace(worldIn, pos.func_177982_a(a, b, c), s, pos);
                     break label76;
                  }
               }
            }
         }
      } else if (state.func_177230_c() != BlocksTC.placeholderNetherbrick
         && state.func_177230_c() != BlocksTC.placeholderObsidian
         && !BlockGolemBuilder.ignore
         && !worldIn.field_72995_K) {
         label53:
         for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
               for (int c = -1; c <= 1; c++) {
                  IBlockState s = worldIn.func_180495_p(pos.func_177982_a(a, b, c));
                  if (s.func_177230_c() == BlocksTC.golemBuilder) {
                     BlockGolemBuilder.destroy(worldIn, pos.func_177982_a(a, b, c), s, pos);
                     break label53;
                  }
               }
            }
         }
      }

      super.func_180663_b(worldIn, pos, state);
   }
}
