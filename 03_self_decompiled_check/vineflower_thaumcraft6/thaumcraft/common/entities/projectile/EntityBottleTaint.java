package thaumcraft.common.entities.projectile;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.client.fx.FXDispatcher;

public class EntityBottleTaint extends EntityThrowable {
   public EntityBottleTaint(World p_i1788_1_) {
      super(p_i1788_1_);
   }

   public EntityBottleTaint(World p_i1790_1_, EntityLivingBase p_i1790_2) {
      super(p_i1790_1_, p_i1790_2);
   }

   public EntityBottleTaint(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @SideOnly(Side.CLIENT)
   public void func_70103_a(byte id) {
      if (id == 3) {
         for (int a = 0; a < 100; a++) {
            FXDispatcher.INSTANCE.taintsplosionFX(this);
         }

         FXDispatcher.INSTANCE.bottleTaintBreak(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      }
   }

   protected void func_70184_a(RayTraceResult ray) {
      if (!this.field_70170_p.field_72995_K) {
         List ents = this.field_70170_p
            .func_72872_a(
               EntityLivingBase.class,
               new AxisAlignedBB(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70165_t, this.field_70163_u, this.field_70161_v)
                  .func_72314_b(5.0, 5.0, 5.0)
            );
         if (ents.size() > 0) {
            for (Object ent : ents) {
               EntityLivingBase el = (EntityLivingBase)ent;
               if (!(el instanceof ITaintedMob) && !el.func_70662_br()) {
                  el.func_70690_d(new PotionEffect(PotionFluxTaint.instance, 100, 0, false, true));
               }
            }
         }

         for (int a = 0; a < 10; a++) {
            int xx = (int)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 4.0F);
            int zz = (int)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 4.0F);
            BlockPos p = this.func_180425_c().func_177982_a(xx, 0, zz);
            if (this.field_70170_p.field_73012_v.nextBoolean()) {
               if (this.field_70170_p.func_175677_d(p.func_177977_b(), false)
                  && this.field_70170_p.func_180495_p(p).func_177230_c().func_176200_f(this.field_70170_p, p)) {
                  this.field_70170_p.func_175656_a(p, BlocksTC.fluxGoo.func_176223_P());
               } else {
                  p = p.func_177977_b();
                  if (this.field_70170_p.func_175677_d(p.func_177977_b(), false)
                     && this.field_70170_p.func_180495_p(p).func_177230_c().func_176200_f(this.field_70170_p, p)) {
                     this.field_70170_p.func_175656_a(p, BlocksTC.fluxGoo.func_176223_P());
                  }
               }
            }
         }

         this.field_70170_p.func_72960_a(this, (byte)3);
         this.func_70106_y();
      }
   }
}
