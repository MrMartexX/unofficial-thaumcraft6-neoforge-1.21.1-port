package thaumcraft.common.blocks.misc;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.events.ServerEvents;

public class BlockEffect extends BlockTC {
   public BlockEffect(String name) {
      super(Material.field_151579_a, name);
      this.func_149675_a(true);
      this.field_149781_w = 999.0F;
      this.func_149715_a(0.5F);
   }

   public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
      if (state.func_177230_c() != this) {
         return super.getLightValue(state, world, pos);
      } else {
         return state.func_177230_c() == BlocksTC.effectGlimmer ? 15 : super.getLightValue(state, world, pos);
      }
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public void func_180634_a(World world, BlockPos pos, IBlockState state, final Entity entity) {
      if (state.func_177230_c() == BlocksTC.effectShock) {
         if (entity instanceof EntityLivingBase) {
            ServerEvents.addRunnableServer(world, new Runnable() {
               @Override
               public void run() {
                  entity.func_70097_a(DamageSource.field_76376_m, 1.0F);
                  PotionEffect pe = new PotionEffect(MobEffects.field_76421_d, 20, 0, true, true);
                  ((EntityLivingBase)entity).func_70690_d(pe);
               }
            }, 0);
         }

         if (!world.field_72995_K && world.field_73012_v.nextInt(100) == 0) {
            world.func_175698_g(pos);
         }
      } else if (state.func_177230_c() == BlocksTC.effectSap
         && !(entity instanceof IEldritchMob)
         && entity instanceof EntityLivingBase
         && !((EntityLivingBase)entity).func_70644_a(MobEffects.field_82731_v)) {
         ServerEvents.addRunnableServer(world, new Runnable() {
            @Override
            public void run() {
               PotionEffect pe0 = new PotionEffect(MobEffects.field_82731_v, 40, 0, true, true);
               ((EntityLivingBase)entity).func_70690_d(pe0);
               PotionEffect pe1 = new PotionEffect(MobEffects.field_76421_d, 40, 1, true, true);
               ((EntityLivingBase)entity).func_70690_d(pe1);
               PotionEffect pe2 = new PotionEffect(MobEffects.field_76438_s, 40, 1, true, true);
               ((EntityLivingBase)entity).func_70690_d(pe2);
            }
         }, 0);
      }
   }

   public void func_180650_b(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      super.func_180650_b(worldIn, pos, state, rand);
      if (!worldIn.field_72995_K && state.func_177230_c() != BlocksTC.effectGlimmer) {
         worldIn.func_175698_g(pos);
      }
   }

   @SideOnly(Side.CLIENT)
   public void func_180655_c(IBlockState state, World w, BlockPos pos, Random r) {
      if (state.func_177230_c() != BlocksTC.effectGlimmer) {
         float h = r.nextFloat() * 0.33F;
         if (state.func_177230_c() == BlocksTC.effectShock) {
            FXDispatcher.INSTANCE
               .spark(
                  pos.func_177958_n() + w.field_73012_v.nextFloat(),
                  pos.func_177956_o() + 0.1515F + h / 2.0F,
                  pos.func_177952_p() + w.field_73012_v.nextFloat(),
                  3.0F + h * 6.0F,
                  0.65F + w.field_73012_v.nextFloat() * 0.1F,
                  1.0F,
                  1.0F,
                  0.8F
               );
         } else {
            FXDispatcher.INSTANCE
               .spark(
                  pos.func_177958_n() + w.field_73012_v.nextFloat(),
                  pos.func_177956_o() + 0.1515F + h / 2.0F,
                  pos.func_177952_p() + w.field_73012_v.nextFloat(),
                  3.0F + h * 6.0F,
                  0.3F - w.field_73012_v.nextFloat() * 0.1F,
                  0.0F,
                  0.5F + w.field_73012_v.nextFloat() * 0.2F,
                  1.0F
               );
         }

         if (r.nextInt(50) == 0) {
            w.func_184134_a(
               pos.func_177958_n(),
               pos.func_177956_o(),
               pos.func_177952_p(),
               SoundsTC.jacobs,
               SoundCategory.AMBIENT,
               0.25F,
               1.0F + (r.nextFloat() - r.nextFloat()) * 0.2F,
               false
            );
         }
      }
   }

   public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) {
      return true;
   }

   public EnumBlockRenderType func_149645_b(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public boolean func_176200_f(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return ItemStack.field_190927_a;
   }

   public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing o) {
      return false;
   }

   public boolean func_176205_b(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   public AxisAlignedBB func_180646_a(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return null;
   }

   public boolean func_176209_a(IBlockState state, boolean hitIfLiquid) {
      return false;
   }

   public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
      return new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public Item func_180660_a(IBlockState state, Random rand, int fortune) {
      return Item.func_150899_d(0);
   }
}
