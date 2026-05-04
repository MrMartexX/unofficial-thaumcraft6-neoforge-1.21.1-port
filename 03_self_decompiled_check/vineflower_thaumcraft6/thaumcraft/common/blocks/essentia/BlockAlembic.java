package thaumcraft.common.blocks.essentia;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.ILabelable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.BlockTCTile;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.essentia.TileAlembic;

public class BlockAlembic extends BlockTCTile implements ILabelable {
   public BlockAlembic() {
      super(Material.field_151575_d, TileAlembic.class, "alembic");
      this.func_149672_a(SoundType.field_185848_a);
   }

   @Override
   public TileEntity func_149915_a(World world, int metadata) {
      return metadata == 0 ? new TileAlembic() : null;
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.func_176203_a(meta);
   }

   public boolean func_180639_a(
      World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ
   ) {
      TileEntity te = world.func_175625_s(pos);
      if (te != null
         && te instanceof TileAlembic
         && player.func_70093_af()
         && ((TileAlembic)te).aspectFilter != null
         && side.ordinal() == ((TileAlembic)te).facing) {
         ((TileAlembic)te).aspectFilter = null;
         ((TileAlembic)te).facing = EnumFacing.DOWN.ordinal();
         te.func_70296_d();
         world.markAndNotifyBlock(pos, world.func_175726_f(pos), state, state, 3);
         if (world.field_72995_K) {
            world.func_184134_a(
               pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, SoundsTC.page, SoundCategory.BLOCKS, 1.0F, 1.0F, false
            );
         } else {
            world.func_72838_d(
               new EntityItem(
                  world,
                  pos.func_177958_n() + 0.5F + side.func_82601_c() / 3.0F,
                  pos.func_177956_o() + 0.5F,
                  pos.func_177952_p() + 0.5F + side.func_82599_e() / 3.0F,
                  new ItemStack(ItemsTC.label)
               )
            );
         }

         return true;
      } else {
         if (te != null
            && te instanceof TileAlembic
            && player.func_70093_af()
            && player.func_184614_ca().func_190926_b()
            && (((TileAlembic)te).aspectFilter == null || side.ordinal() != ((TileAlembic)te).facing)) {
            ((TileAlembic)te).aspect = null;
            if (world.field_72995_K) {
               world.func_184134_a(
                  pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, SoundsTC.jar, SoundCategory.BLOCKS, 0.4F, 1.0F, false
               );
               world.func_184134_a(
                  pos.func_177958_n() + 0.5,
                  pos.func_177956_o() + 0.5,
                  pos.func_177952_p() + 0.5,
                  SoundEvents.field_187615_H,
                  SoundCategory.BLOCKS,
                  0.5F,
                  1.0F + (world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 0.3F,
                  false
               );
            } else {
               AuraHelper.polluteAura(world, pos, ((TileAlembic)te).amount, true);
            }

            ((TileAlembic)te).amount = 0;
            te.func_70296_d();
            world.markAndNotifyBlock(pos, world.func_175726_f(pos), state, state, 3);
         }

         return true;
      }
   }

   public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
      return new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 1.0, 0.875);
   }

   public boolean func_149740_M(IBlockState state) {
      return true;
   }

   public int func_180641_l(IBlockState state, World world, BlockPos pos) {
      TileEntity tile = world.func_175625_s(pos);
      if (tile != null && tile instanceof TileAlembic) {
         float r = (float)((TileAlembic)tile).amount / ((TileAlembic)tile).maxAmount;
         return MathHelper.func_76141_d(r * 14.0F) + (((TileAlembic)tile).amount > 0 ? 1 : 0);
      } else {
         return super.func_180641_l(state, world, pos);
      }
   }

   @Override
   public boolean applyLabel(EntityPlayer player, BlockPos pos, EnumFacing side, ItemStack labelstack) {
      TileEntity te = player.field_70170_p.func_175625_s(pos);
      if (te != null && te instanceof TileAlembic && side.ordinal() > 1 && ((TileAlembic)te).aspectFilter == null) {
         Aspect la = null;
         if (((IEssentiaContainerItem)labelstack.func_77973_b()).getAspects(labelstack) != null) {
            la = ((IEssentiaContainerItem)labelstack.func_77973_b()).getAspects(labelstack).getAspects()[0];
         }

         if (((TileAlembic)te).amount == 0 && la == null) {
            return false;
         }

         Aspect aspect = null;
         if (((TileAlembic)te).amount == 0 && la != null) {
            aspect = la;
         }

         if (((TileAlembic)te).amount > 0) {
            aspect = ((TileAlembic)te).aspect;
         }

         if (aspect == null) {
            return false;
         }

         IBlockState state = player.field_70170_p.func_180495_p(pos);
         this.func_180633_a(player.field_70170_p, pos, state, player, null);
         ((TileAlembic)te).aspectFilter = aspect;
         ((TileAlembic)te).facing = side.ordinal();
         te.func_70296_d();
         player.field_70170_p.markAndNotifyBlock(pos, player.field_70170_p.func_175726_f(pos), state, state, 3);
         player.field_70170_p
            .func_184148_a(
               null, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, SoundsTC.page, SoundCategory.BLOCKS, 1.0F, 1.0F
            );
         return true;
      } else {
         return false;
      }
   }
}
