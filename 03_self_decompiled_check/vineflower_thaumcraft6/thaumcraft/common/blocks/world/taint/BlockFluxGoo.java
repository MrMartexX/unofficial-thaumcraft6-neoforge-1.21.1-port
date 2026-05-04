package thaumcraft.common.blocks.world.taint;

import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.lib.SoundsTC;

public class BlockFluxGoo extends BlockFluidFinite {
   public BlockFluxGoo() {
      super(ConfigBlocks.FluidFluxGoo.instance, ThaumcraftMaterials.MATERIAL_TAINT);
      this.setRegistryName("flux_goo");
      this.func_149663_c("flux_goo");
      this.func_149647_a(ConfigItems.TABTC);
      this.func_149672_a(SoundsTC.GORE);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(LEVEL, 7));
   }

   public SoundType func_185467_w() {
      return SoundsTC.GORE;
   }

   public void func_180634_a(World world, BlockPos pos, IBlockState state, Entity entity) {
      int md = (Integer)state.func_177229_b(LEVEL);
      if (entity instanceof EntityThaumicSlime) {
         EntityThaumicSlime slime = (EntityThaumicSlime)entity;
         if (slime.func_70809_q() < md && world.field_73012_v.nextBoolean()) {
            slime.func_70799_a(slime.func_70809_q() + 1, true);
            if (md > 1) {
               world.func_180501_a(pos, state.func_177226_a(LEVEL, md - 1), 2);
            } else {
               world.func_175698_g(pos);
            }
         }
      } else {
         entity.field_70159_w = entity.field_70159_w * (1.0F - this.getQuantaPercentage(world, pos));
         entity.field_70179_y = entity.field_70179_y * (1.0F - this.getQuantaPercentage(world, pos));
         if (entity instanceof EntityLivingBase) {
            PotionEffect pe = new PotionEffect(PotionVisExhaust.instance, 600, md / 3, true, true);
            pe.getCurativeItems().clear();
            ((EntityLivingBase)entity).func_70690_d(pe);
         }
      }
   }

   public void func_180650_b(World world, BlockPos pos, IBlockState state, Random rand) {
      int meta = (Integer)state.func_177229_b(LEVEL);
      if (meta >= 2 && meta < 6 && world.func_175623_d(pos.func_177984_a()) && rand.nextInt(50) == 0) {
         world.func_175698_g(pos);
         EntityThaumicSlime slime = new EntityThaumicSlime(world);
         slime.func_70012_b(pos.func_177958_n() + 0.5F, pos.func_177956_o(), pos.func_177952_p() + 0.5F, 0.0F, 0.0F);
         slime.func_70799_a(1, true);
         world.func_72838_d(slime);
         slime.func_184185_a(SoundsTC.gore, 1.0F, 1.0F);
      } else if (meta >= 6 && world.func_175623_d(pos.func_177984_a()) && rand.nextInt(50) == 0) {
         world.func_175698_g(pos);
         EntityThaumicSlime slime = new EntityThaumicSlime(world);
         slime.func_70012_b(pos.func_177958_n() + 0.5F, pos.func_177956_o(), pos.func_177952_p() + 0.5F, 0.0F, 0.0F);
         slime.func_70799_a(2, true);
         world.func_72838_d(slime);
         slime.func_184185_a(SoundsTC.gore, 1.0F, 1.0F);
      } else if (rand.nextInt(4) == 0) {
         if (meta == 0) {
            if (rand.nextBoolean()) {
               AuraHelper.polluteAura(world, pos, 1.0F, true);
               world.func_175698_g(pos);
            } else {
               world.func_175656_a(pos, BlocksTC.taintFibre.func_176223_P());
            }
         } else {
            world.func_180501_a(pos, state.func_177226_a(LEVEL, meta - 1), 2);
            AuraHelper.polluteAura(world, pos, 1.0F, true);
         }
      } else {
         super.func_180650_b(world, pos, state, rand);
      }
   }

   public boolean func_176200_f(IBlockAccess world, BlockPos pos) {
      return (Integer)world.func_180495_p(pos).func_177229_b(LEVEL) < 4;
   }

   public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void func_180655_c(IBlockState state, World world, BlockPos pos, Random rand) {
      int meta = this.func_176201_c(state);
      if (rand.nextInt(44) <= meta) {
         FXGeneric fb = new FXGeneric(
            world, pos.func_177958_n() + rand.nextFloat(), pos.func_177956_o() + 0.125F * meta, pos.func_177952_p() + rand.nextFloat(), 0.0, 0.0, 0.0
         );
         fb.func_187114_a(2 + world.field_73012_v.nextInt(3));
         fb.setScale(world.field_73012_v.nextFloat() * 0.3F + 0.2F);
         fb.func_70538_b(1.0F, 0.0F, 0.5F);
         fb.setRandomMovementScale(0.001F, 0.001F, 0.001F);
         fb.setGravity(-0.01F);
         fb.func_82338_g(0.25F);
         fb.func_70536_a(64);
         fb.setFinalFrames(65, 66);
         ParticleEngine.addEffect(world, fb);
      }
   }

   static {
      defaultDisplacements.put(BlocksTC.taintFibre, true);
   }
}
