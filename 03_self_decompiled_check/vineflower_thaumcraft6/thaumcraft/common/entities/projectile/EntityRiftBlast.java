package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.codechicken.lib.vec.Quat;
import thaumcraft.common.lib.SoundsTC;

public class EntityRiftBlast extends EntityThrowable implements IEntityAdditionalSpawnData {
   int targetID = 0;
   EntityLivingBase target;
   public boolean red = false;
   public double[][] points;
   public float[][] colours;
   public double[] radii;
   int growing = -1;
   ArrayList<Quat> vecs = new ArrayList<>();

   public EntityRiftBlast(World par1World) {
      super(par1World);
   }

   public EntityRiftBlast(World par1World, EntityLivingBase par2EntityLiving, EntityLivingBase t, boolean r) {
      super(par1World, par2EntityLiving);
      this.target = t;
      this.red = r;
   }

   protected float func_70185_h() {
      return 0.0F;
   }

   public void writeSpawnData(ByteBuf data) {
      int id = -1;
      if (this.target != null) {
         id = this.target.func_145782_y();
      }

      data.writeInt(id);
      data.writeBoolean(this.red);
   }

   public void readSpawnData(ByteBuf data) {
      int id = data.readInt();

      try {
         if (id >= 0) {
            this.target = (EntityLivingBase)this.field_70170_p.func_73045_a(id);
         }
      } catch (Exception var4) {
      }

      this.red = data.readBoolean();
   }

   protected void func_70184_a(RayTraceResult mop) {
      if (!this.field_70170_p.field_72995_K && this.func_85052_h() != null && mop.field_72313_a == Type.ENTITY) {
         mop.field_72308_g
            .func_70097_a(
               DamageSource.func_76354_b(this, this.func_85052_h()),
               (float)this.func_85052_h().func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e() * (this.red ? 1.0F : 0.6F)
            );
      }

      this.func_184185_a(SoundsTC.shock, 1.0F, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F);
      if (this.field_70170_p.field_72995_K) {
         FXDispatcher.INSTANCE.burst(this.field_70165_t, this.field_70163_u, this.field_70161_v, 6.0F);
      }

      this.func_70106_y();
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70173_aa > (this.red ? 240 : 160)) {
         this.func_70106_y();
      }

      if (this.target != null) {
         if (this.target == null || this.target.field_70128_L) {
            this.func_70106_y();
         }

         double d = this.func_70068_e(this.target);
         double dx = this.target.field_70165_t - this.field_70165_t;
         double var10001 = this.target.field_70131_O;
         double dy = this.target.func_174813_aQ().field_72338_b + var10001 * 0.6 - this.field_70163_u;
         double dz = this.target.field_70161_v - this.field_70161_v;
         double d13 = 1.0;
         dx /= d;
         dy /= d;
         dz /= d;
         this.field_70159_w += dx * d13;
         this.field_70181_x += dy * d13;
         this.field_70179_y += dz * d13;
         this.field_70159_w = MathHelper.func_76131_a((float)this.field_70159_w, -0.33F, 0.33F);
         this.field_70181_x = MathHelper.func_76131_a((float)this.field_70181_x, -0.33F, 0.33F);
         this.field_70179_y = MathHelper.func_76131_a((float)this.field_70179_y, -0.33F, 0.33F);
         if (this.field_70170_p.field_72995_K) {
            Quat q = new Quat(
               0.1,
               this.field_70165_t + this.field_70146_Z.nextGaussian() * 0.05,
               this.field_70163_u + this.field_70146_Z.nextGaussian() * 0.05,
               this.field_70161_v + this.field_70146_Z.nextGaussian() * 0.05
            );
            this.vecs.add(q);
            FXDispatcher.INSTANCE
               .drawCurlyWisp(
                  q.x,
                  q.y,
                  q.z,
                  0.0,
                  0.0,
                  0.0,
                  0.3F + this.field_70146_Z.nextFloat() * 0.2F,
                  this.field_70146_Z.nextFloat(),
                  this.field_70146_Z.nextFloat() * 0.2F,
                  this.field_70146_Z.nextFloat() * 0.2F,
                  0.5F,
                  null,
                  1,
                  this.field_70146_Z.nextInt(2),
                  0
               );
            if (this.vecs.size() > 9) {
               this.vecs.remove(0);
            }

            this.points = new double[this.vecs.size()][3];
            this.colours = new float[this.vecs.size()][4];
            this.radii = new double[this.vecs.size()];
            int c = 0;
            if (this.vecs.size() > 1) {
               float vv = (float)(Math.PI / (this.vecs.size() - 1));

               for (Quat v : this.vecs) {
                  float variance = 1.0F + MathHelper.func_76126_a((c + this.field_70173_aa) / 3.0F) * 0.2F;
                  float xx = MathHelper.func_76126_a((c + this.field_70173_aa) / 6.0F) * 0.01F;
                  float yy = MathHelper.func_76126_a((c + this.field_70173_aa) / 7.0F) * 0.01F;
                  float zz = MathHelper.func_76126_a((c + this.field_70173_aa) / 8.0F) * 0.01F;
                  this.points[c][0] = v.x + xx;
                  this.points[c][1] = v.y + yy;
                  this.points[c][2] = v.z + zz;
                  this.radii[c] = v.s * variance;
                  this.radii[c] = this.radii[c] * MathHelper.func_76126_a(c * vv);
                  this.colours[c][0] = 1.0F;
                  this.colours[c][1] = 0.0F;
                  this.colours[c][2] = 0.0F;
                  this.colours[c][3] = 1.0F;
                  c++;
               }
            }
         }
      }
   }

   public boolean func_70097_a(DamageSource source, float damage) {
      if (this.func_180431_b(source)) {
         return false;
      }

      if (source.func_76346_g() != null) {
         Vec3d vec3 = source.func_76346_g().func_70040_Z();
         if (vec3 != null) {
            this.field_70159_w = vec3.field_72450_a;
            this.field_70181_x = vec3.field_72448_b;
            this.field_70179_y = vec3.field_72449_c;
            this.field_70159_w *= 0.9;
            this.field_70181_x *= 0.9;
            this.field_70179_y *= 0.9;
            this.func_184185_a(SoundsTC.zap, 1.0F, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F);
         }

         return true;
      } else {
         return false;
      }
   }
}
