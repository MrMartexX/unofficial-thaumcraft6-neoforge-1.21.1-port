package thaumcraft.common.blocks.basic;

import java.util.List;
import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.tiles.misc.TileBarrierStone;

public class BlockPavingStone extends BlockTC {
   public BlockPavingStone(String name) {
      super(Material.field_151576_e, name);
      this.func_149711_c(2.5F);
      this.func_149672_a(SoundType.field_185851_d);
      this.func_149675_a(true);
   }

   public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
      return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);
   }

   public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
      return true;
   }

   public boolean hasTileEntity(IBlockState state) {
      return state.func_177230_c() == BlocksTC.pavingStoneBarrier;
   }

   public TileEntity createTileEntity(World world, IBlockState state) {
      return state.func_177230_c() == BlocksTC.pavingStoneBarrier ? new TileBarrierStone() : null;
   }

   public void func_176199_a(World worldIn, BlockPos pos, Entity e) {
      IBlockState state = worldIn.func_180495_p(pos);
      if (!worldIn.field_72995_K && state.func_177230_c() == BlocksTC.pavingStoneTravel && e instanceof EntityLivingBase) {
         ((EntityLivingBase)e).func_70690_d(new PotionEffect(MobEffects.field_76424_c, 40, 1, false, false));
         ((EntityLivingBase)e).func_70690_d(new PotionEffect(MobEffects.field_76430_j, 40, 0, false, false));
      }

      super.func_176199_a(worldIn, pos, e);
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public void func_180655_c(IBlockState state, World world, BlockPos pos, Random random) {
      if (state.func_177230_c() == BlocksTC.pavingStoneBarrier) {
         if (world.func_175687_A(pos) > 0) {
            for (int a = 0; a < 4; a++) {
               FXDispatcher.INSTANCE
                  .blockRunes(
                     pos.func_177958_n(),
                     pos.func_177956_o() + 0.7F,
                     pos.func_177952_p(),
                     0.2F + random.nextFloat() * 0.4F,
                     random.nextFloat() * 0.3F,
                     0.8F + random.nextFloat() * 0.2F,
                     20,
                     -0.02F
                  );
            }
         } else if (world.func_180495_p(pos.func_177981_b(1)) == BlocksTC.barrier.func_176223_P()
               && world.func_180495_p(pos.func_177981_b(1)).func_177230_c().func_176205_b(world, pos.func_177981_b(1))
            || world.func_180495_p(pos.func_177981_b(2)) == BlocksTC.barrier.func_176223_P()
               && world.func_180495_p(pos.func_177981_b(2)).func_177230_c().func_176205_b(world, pos.func_177981_b(2))) {
            for (int a = 0; a < 6; a++) {
               FXDispatcher.INSTANCE
                  .blockRunes(
                     pos.func_177958_n(),
                     pos.func_177956_o() + 0.7F,
                     pos.func_177952_p(),
                     0.9F + random.nextFloat() * 0.1F,
                     random.nextFloat() * 0.3F,
                     random.nextFloat() * 0.3F,
                     24,
                     -0.02F
                  );
            }
         } else {
            List list = world.func_72839_b(
               (Entity)null,
               new AxisAlignedBB(
                     pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), pos.func_177958_n() + 1, pos.func_177956_o() + 1, pos.func_177952_p() + 1
                  )
                  .func_72314_b(1.0, 1.0, 1.0)
            );
            if (!list.isEmpty()) {
               for (Entity entity : list) {
                  if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer)) {
                     FXDispatcher.INSTANCE
                        .blockRunes(
                           pos.func_177958_n(),
                           pos.func_177956_o() + 0.6F + random.nextFloat() * Math.max(0.8F, entity.func_70047_e()),
                           pos.func_177952_p(),
                           0.6F + random.nextFloat() * 0.4F,
                           0.0F,
                           0.3F + random.nextFloat() * 0.7F,
                           20,
                           0.0F
                        );
                     break;
                  }
               }
            }
         }
      }
   }
}
