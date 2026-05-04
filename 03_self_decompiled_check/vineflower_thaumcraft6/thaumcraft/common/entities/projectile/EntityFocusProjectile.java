package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import java.awt.Color;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;

public class EntityFocusProjectile extends EntityThrowable implements IEntityAdditionalSpawnData {
   FocusPackage focusPackage;
   private static final DataParameter<Integer> SPECIAL = EntityDataManager.func_187226_a(EntityFocusProjectile.class, DataSerializers.field_187192_b);
   private static final DataParameter<Integer> OWNER = EntityDataManager.func_187226_a(EntityFocusProjectile.class, DataSerializers.field_187192_b);
   boolean noTouchy = false;
   private Entity target;
   boolean firstParticle = false;
   public float lastRenderTick = 0.0F;
   FocusEffect[] effects = null;

   public EntityFocusProjectile(World par1World) {
      super(par1World);
      this.func_70105_a(0.15F, 0.15F);
   }

   public EntityFocusProjectile(FocusPackage pack, float speed, Trajectory trajectory, int special) {
      super(pack.world, pack.getCaster());
      this.focusPackage = pack;
      this.func_70107_b(
         trajectory.source.field_72450_a + trajectory.direction.field_72450_a * pack.getCaster().field_70130_N * 2.1,
         trajectory.source.field_72448_b + trajectory.direction.field_72448_b * pack.getCaster().field_70130_N * 2.1,
         trajectory.source.field_72449_c + trajectory.direction.field_72449_c * pack.getCaster().field_70130_N * 2.1
      );
      this.func_70186_c(trajectory.direction.field_72450_a, trajectory.direction.field_72448_b, trajectory.direction.field_72449_c, speed, 0.0F);
      this.func_70105_a(0.15F, 0.15F);
      this.setSpecial(special);
      this.field_184539_c = pack.getCaster();
      this.setOwner(this.func_85052_h().func_145782_y());
   }

   protected float func_70185_h() {
      return this.getSpecial() > 1 ? 0.005F : 0.01F;
   }

   public void func_70088_a() {
      super.func_70088_a();
      this.func_184212_Q().func_187214_a(SPECIAL, 0);
      this.func_184212_Q().func_187214_a(OWNER, 0);
   }

   public void setOwner(int s) {
      this.func_184212_Q().func_187227_b(OWNER, s);
   }

   public int getOwner() {
      return (Integer)this.func_184212_Q().func_187225_a(OWNER);
   }

   public EntityLivingBase func_85052_h() {
      if (this.field_70170_p.field_72995_K) {
         Entity e = this.field_70170_p.func_73045_a(this.getOwner());
         if (e != null && e instanceof EntityLivingBase) {
            return (EntityLivingBase)e;
         }
      }

      return super.func_85052_h();
   }

   public void setSpecial(int s) {
      this.func_184212_Q().func_187227_b(SPECIAL, s);
   }

   public int getSpecial() {
      return (Integer)this.func_184212_Q().func_187225_a(SPECIAL);
   }

   public void writeSpawnData(ByteBuf data) {
      Utils.writeNBTTagCompoundToBuffer(data, this.focusPackage.serialize());
   }

   public void readSpawnData(ByteBuf data) {
      try {
         this.focusPackage = new FocusPackage();
         this.focusPackage.deserialize(Utils.readNBTTagCompoundFromBuffer(data));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void func_70014_b(NBTTagCompound nbt) {
      super.func_70014_b(nbt);
      nbt.func_74782_a("pack", this.focusPackage.serialize());
      nbt.func_74768_a("special", this.getSpecial());
   }

   public void func_70037_a(NBTTagCompound nbt) {
      super.func_70037_a(nbt);
      this.setSpecial(nbt.func_74762_e("special"));

      try {
         this.focusPackage = new FocusPackage();
         this.focusPackage.deserialize(nbt.func_74775_l("pack"));
      } catch (Exception var3) {
      }

      if (this.func_85052_h() != null) {
         this.setOwner(this.func_85052_h().func_145782_y());
      }
   }

   protected void func_70184_a(final RayTraceResult mop) {
      if (mop != null) {
         if (this.getSpecial() == 1 && mop.field_72313_a == Type.BLOCK) {
            IBlockState bs = this.field_70170_p.func_180495_p(mop.func_178782_a());
            AxisAlignedBB bb = bs.func_185890_d(this.field_70170_p, mop.func_178782_a());
            if (bb == null) {
               return;
            }

            this.field_70165_t = this.field_70165_t - this.field_70159_w;
            this.field_70163_u = this.field_70163_u - this.field_70181_x;
            this.field_70161_v = this.field_70161_v - this.field_70179_y;
            if (mop.field_178784_b.func_82599_e() != 0) {
               this.field_70179_y *= -1.0;
            }

            if (mop.field_178784_b.func_82601_c() != 0) {
               this.field_70159_w *= -1.0;
            }

            if (mop.field_178784_b.func_96559_d() != 0) {
               this.field_70181_x *= -0.9;
            }

            this.field_70159_w *= 0.9;
            this.field_70181_x *= 0.9;
            this.field_70179_y *= 0.9;
            float var20 = MathHelper.func_76133_a(
               this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y
            );
            this.field_70165_t = this.field_70165_t - this.field_70159_w / var20 * 0.05F;
            this.field_70163_u = this.field_70163_u - this.field_70181_x / var20 * 0.05F;
            this.field_70161_v = this.field_70161_v - this.field_70179_y / var20 * 0.05F;
            if (!this.field_70170_p.field_72995_K) {
               this.func_184185_a(SoundEvents.field_187748_db, 0.25F, 1.0F);
            }

            if (!this.field_70170_p.field_72995_K && new Vec3d(this.field_70159_w, this.field_70181_x, this.field_70179_y).func_72433_c() < 0.2) {
               this.func_70106_y();
            }

            return;
         }

         if (!this.field_70170_p.field_72995_K) {
            if (mop.field_72308_g != null) {
               mop.field_72307_f = this.func_174791_d();
            }

            final Vec3d pv = new Vec3d(this.field_70169_q, this.field_70167_r, this.field_70166_s);
            final Vec3d vf = new Vec3d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
            ServerEvents.addRunnableServer(
               this.func_130014_f_(),
               new Runnable() {
                  @Override
                  public void run() {
                     FocusEngine.runFocusPackage(
                        EntityFocusProjectile.this.focusPackage, new Trajectory[]{new Trajectory(pv, vf.func_72432_b())}, new RayTraceResult[]{mop}
                     );
                  }
               },
               0
            );
            this.func_70106_y();
         }
      }
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70173_aa > 1200 || !this.field_70170_p.field_72995_K && this.func_85052_h() == null) {
         this.func_70106_y();
      }

      this.firstParticle = true;
      if (this.target == null && this.field_70173_aa % 5 == 0 && this.getSpecial() > 1) {
         for (EntityLivingBase pt : EntityUtils.getEntitiesInRangeSorted(this.func_130014_f_(), this, EntityLivingBase.class, 16.0)) {
            if (!pt.field_70128_L && EntityUtils.isVisibleTo(1.75F, this, pt, 16.0F) && EntityUtils.canEntityBeSeen(this, pt)) {
               boolean f = EntityUtils.isFriendly(this.func_85052_h(), pt);
               if (f && this.getSpecial() == 3) {
                  this.target = pt;
                  break;
               }

               if (!f && this.getSpecial() == 2) {
                  this.target = pt;
                  break;
               }
            }
         }
      }

      if (this.target != null) {
         double d = this.func_70068_e(this.target);
         double dx = this.target.field_70165_t - this.field_70165_t;
         double var10001 = this.target.field_70131_O;
         double dy = this.target.func_174813_aQ().field_72338_b + var10001 * 0.6 - this.field_70163_u;
         double dz = this.target.field_70161_v - this.field_70161_v;
         Vec3d v = new Vec3d(dx, dy, dz);
         v = v.func_72432_b();
         Vec3d mv = new Vec3d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
         double lv = mv.func_72433_c();
         mv = mv.func_72432_b().func_178787_e(v.func_186678_a(0.275));
         mv = mv.func_72432_b().func_186678_a(lv);
         this.field_70159_w = mv.field_72450_a;
         this.field_70181_x = mv.field_72448_b;
         this.field_70179_y = mv.field_72449_c;
         if (this.field_70173_aa % 5 == 0
            && (this.target.field_70128_L || !EntityUtils.isVisibleTo(1.75F, this, this.target, 16.0F) || !EntityUtils.canEntityBeSeen(this, this.target))) {
            this.target = null;
         }
      }
   }

   public Vec3d func_70040_Z() {
      return new Vec3d(this.field_70159_w, this.field_70181_x, this.field_70179_y).func_72432_b();
   }

   public void renderParticle(float coeff) {
      this.lastRenderTick = coeff;
      if (this.effects == null) {
         this.effects = this.focusPackage.getFocusEffects();
      }

      if (this.effects != null && this.effects.length > 0) {
         FocusEffect eff = this.effects[this.field_70146_Z.nextInt(this.effects.length)];
         float scale = 1.0F;
         Color c1 = new Color(FocusEngine.getElementColor(eff.getKey()));
         FXDispatcher.INSTANCE
            .drawFireMote(
               (float)(this.field_70169_q + (this.field_70165_t - this.field_70169_q) * coeff),
               (float)(this.field_70167_r + (this.field_70163_u - this.field_70167_r) * coeff) + this.field_70131_O / 2.0F,
               (float)(this.field_70166_s + (this.field_70161_v - this.field_70166_s) * coeff),
               0.0125F * (this.field_70146_Z.nextFloat() - 0.5F) * scale,
               0.0125F * (this.field_70146_Z.nextFloat() - 0.5F) * scale,
               0.0125F * (this.field_70146_Z.nextFloat() - 0.5F) * scale,
               c1.getRed() / 255.0F,
               c1.getGreen() / 255.0F,
               c1.getBlue() / 255.0F,
               0.5F,
               7.0F * scale
            );
         if (this.firstParticle) {
            this.firstParticle = false;
            eff.renderParticleFX(
               this.field_70170_p,
               this.field_70169_q + (this.field_70165_t - this.field_70169_q) * coeff + this.field_70170_p.field_73012_v.nextGaussian() * 0.1F,
               this.field_70167_r
                  + (this.field_70163_u - this.field_70167_r) * coeff
                  + this.field_70131_O / 2.0F
                  + this.field_70170_p.field_73012_v.nextGaussian() * 0.1F,
               this.field_70166_s + (this.field_70161_v - this.field_70166_s) * coeff + this.field_70170_p.field_73012_v.nextGaussian() * 0.1F,
               this.field_70170_p.field_73012_v.nextGaussian() * 0.01F,
               this.field_70170_p.field_73012_v.nextGaussian() * 0.01F,
               this.field_70170_p.field_73012_v.nextGaussian() * 0.01F
            );
         }
      }
   }
}
