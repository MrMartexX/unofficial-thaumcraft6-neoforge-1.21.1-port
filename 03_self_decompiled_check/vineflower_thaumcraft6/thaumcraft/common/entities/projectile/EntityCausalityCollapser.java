package thaumcraft.common.entities.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.EntityUtils;

public class EntityCausalityCollapser extends EntityThrowable {
   public EntityCausalityCollapser(World par1World) {
      super(par1World);
   }

   public EntityCausalityCollapser(World par1World, EntityLivingBase par2EntityLiving) {
      super(par1World, par2EntityLiving);
   }

   public EntityCausalityCollapser(World par1World, double par2, double par4, double par6) {
      super(par1World, par2, par4, par6);
   }

   public void func_70186_c(double x, double y, double z, float velocity, float inaccuracy) {
      super.func_70186_c(x, y, z, 0.8F, inaccuracy);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p.field_72995_K) {
         for (double i = 0.0; i < 3.0; i++) {
            double coeff = i / 3.0;
            FXDispatcher.INSTANCE
               .drawAlumentum(
                  (float)(this.field_70169_q + (this.field_70165_t - this.field_70169_q) * coeff),
                  (float)(this.field_70167_r + (this.field_70163_u - this.field_70167_r) * coeff) + this.field_70131_O / 2.0F,
                  (float)(this.field_70166_s + (this.field_70161_v - this.field_70166_s) * coeff),
                  0.0125F * (this.field_70146_Z.nextFloat() - 0.5F),
                  0.0125F * (this.field_70146_Z.nextFloat() - 0.5F),
                  0.0125F * (this.field_70146_Z.nextFloat() - 0.5F),
                  0.8F + this.field_70146_Z.nextFloat() * 0.2F,
                  0.3F + this.field_70146_Z.nextFloat() * 0.1F,
                  this.field_70146_Z.nextFloat() * 0.1F,
                  0.5F,
                  4.0F
               );
            FXDispatcher.INSTANCE
               .drawGenericParticles(
                  this.field_70165_t + this.field_70170_p.field_73012_v.nextGaussian() * 0.2F,
                  this.field_70163_u + this.field_70170_p.field_73012_v.nextGaussian() * 0.2F,
                  this.field_70161_v + this.field_70170_p.field_73012_v.nextGaussian() * 0.2F,
                  0.0,
                  0.0,
                  0.0,
                  1.0F,
                  1.0F,
                  1.0F,
                  0.7F,
                  false,
                  448,
                  8,
                  1,
                  8,
                  0,
                  0.3F,
                  0.0F,
                  1
               );
         }
      }
   }

   protected void func_70184_a(RayTraceResult par1RayTraceResult) {
      if (!this.field_70170_p.field_72995_K) {
         this.field_70170_p.func_72876_a(this, this.field_70165_t, this.field_70163_u, this.field_70161_v, 2.0F, true);

         for (EntityFluxRift fr : EntityUtils.getEntitiesInRange(
            this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, this, EntityFluxRift.class, 3.0
         )) {
            fr.setCollapse(true);
         }

         this.func_70106_y();
      }
   }
}
