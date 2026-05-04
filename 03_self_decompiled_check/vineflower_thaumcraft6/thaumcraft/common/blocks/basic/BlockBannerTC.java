package thaumcraft.common.blocks.basic;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.tiles.misc.TileBanner;

public class BlockBannerTC extends BlockTC implements ITileEntityProvider {
   public final EnumDyeColor dye;

   public BlockBannerTC(String name, EnumDyeColor dye) {
      super(Material.field_151575_d, name);
      this.func_149711_c(1.0F);
      this.func_149672_a(SoundType.field_185848_a);
      this.dye = dye;
   }

   public EnumBlockRenderType func_149645_b(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public MapColor func_180659_g(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return this.dye == null ? MapColor.field_151645_D : MapColor.func_193558_a(this.dye);
   }

   public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
      TileEntity tile = source.func_175625_s(pos);
      if (tile == null || !(tile instanceof TileBanner)) {
         return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      }

      if (!((TileBanner)tile).getWall()) {
         return new AxisAlignedBB(0.33F, 0.0, 0.33F, 0.66F, 2.0, 0.66F);
      }

      switch (((TileBanner)tile).getBannerFacing()) {
         case 0:
            return new AxisAlignedBB(0.0, -1.0, 0.0, 1.0, 1.0, 0.25);
         case 4:
            return new AxisAlignedBB(0.75, -1.0, 0.0, 1.0, 1.0, 1.0);
         case 8:
            return new AxisAlignedBB(0.0, -1.0, 0.75, 1.0, 1.0, 1.0);
         case 12:
            return new AxisAlignedBB(0.0, -1.0, 0.0, 0.25, 1.0, 1.0);
         default:
            return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      }
   }

   public AxisAlignedBB func_180646_a(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return null;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_180639_a(World w, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!w.field_72995_K && (p.func_70093_af() || p.func_184586_b(hand).func_77973_b() instanceof ItemPhial)) {
         TileBanner te = (TileBanner)w.func_175625_s(pos);
         if (te != null && this.dye != null) {
            if (p.func_70093_af()) {
               te.setAspect(null);
            } else if (((IEssentiaContainerItem)p.func_184586_b(hand).func_77973_b()).getAspects(p.func_184586_b(hand)) != null) {
               te.setAspect(((IEssentiaContainerItem)p.func_184586_b(hand).func_77973_b()).getAspects(p.func_184586_b(hand)).getAspects()[0]);
               p.func_184586_b(hand).func_190918_g(1);
            }

            w.markAndNotifyBlock(pos, w.func_175726_f(pos), state, state, 3);
            te.func_70296_d();
            te.syncTile(false);
            w.func_184133_a(null, pos, SoundEvents.field_187550_ag, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }
      }

      return true;
   }

   public TileEntity func_149915_a(World worldIn, int meta) {
      return new TileBanner();
   }

   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      TileEntity te = world.func_175625_s(pos);
      if (!(te instanceof TileBanner)) {
         return super.getPickBlock(state, target, world, pos, player);
      }

      ItemStack drop = new ItemStack(this);
      if (this.dye != null || ((TileBanner)te).getAspect() != null) {
         drop.func_77982_d(new NBTTagCompound());
         if (((TileBanner)te).getAspect() != null) {
            drop.func_77978_p().func_74778_a("aspect", ((TileBanner)te).getAspect().getTag());
         }
      }

      return drop;
   }

   public void func_180653_a(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      TileEntity te = worldIn.func_175625_s(pos);
      if (te instanceof TileBanner) {
         ItemStack drop = new ItemStack(this);
         if (this.dye != null || ((TileBanner)te).getAspect() != null) {
            drop.func_77982_d(new NBTTagCompound());
            if (((TileBanner)te).getAspect() != null) {
               drop.func_77978_p().func_74778_a("aspect", ((TileBanner)te).getAspect().getTag());
            }
         }

         func_180635_a(worldIn, pos, drop);
      } else {
         super.func_180653_a(worldIn, pos, state, chance, fortune);
      }
   }

   public void func_180657_a(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
      if (te instanceof TileBanner) {
         ItemStack drop = new ItemStack(this);
         if (this.dye != null || ((TileBanner)te).getAspect() != null) {
            drop.func_77982_d(new NBTTagCompound());
            if (((TileBanner)te).getAspect() != null) {
               drop.func_77978_p().func_74778_a("aspect", ((TileBanner)te).getAspect().getTag());
            }
         }

         func_180635_a(worldIn, pos, drop);
      } else {
         super.func_180657_a(worldIn, player, pos, state, (TileEntity)null, stack);
      }
   }
}
