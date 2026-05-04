package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityGrapple extends EntityThrowable implements IEntityAdditionalSpawnData {
   public EnumHand hand = EnumHand.MAIN_HAND;
   EntityLivingBase cthrower;
   boolean p = false;
   boolean boost;
   int prevDist = 0;
   int count = 0;
   boolean added = false;
   public float ampl = 0.0F;
   public static HashMap<Integer, Integer> grapples = new HashMap<>();

   public EntityGrapple(World par1World) {
      super(par1World);
      this.func_70105_a(0.1F, 0.1F);
   }

   public boolean func_70112_a(double distance) {
      return distance < 4096.0;
   }

   public void func_70186_c(double x, double y, double z, float velocity, float inaccuracy) {
      super.func_70186_c(x, y, z, velocity, 0.0F);
   }

   public EntityGrapple(World par1World, EntityLivingBase par2EntityLiving, EnumHand hand) {
      super(par1World, par2EntityLiving);
      this.func_70105_a(0.1F, 0.1F);
      this.hand = hand;
   }

   public EntityGrapple(World par1World, double par2, double par4, double par6) {
      super(par1World, par2, par4, par6);
      this.func_70105_a(0.1F, 0.1F);
   }

   public void writeSpawnData(ByteBuf data) {
      int id = -1;
      if (this.func_85052_h() != null) {
         id = this.func_85052_h().func_145782_y();
      }

      data.writeInt(id);
      data.writeBoolean(this.hand == EnumHand.MAIN_HAND);
   }

   public void readSpawnData(ByteBuf data) {
      int id = data.readInt();
      this.hand = data.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;

      try {
         if (id >= 0) {
            this.cthrower = (EntityLivingBase)this.field_70170_p.func_73045_a(id);
         }
      } catch (Exception var4) {
      }
   }

   public EntityLivingBase func_85052_h() {
      return this.cthrower != null ? this.cthrower : super.func_85052_h();
   }

   protected float func_70185_h() {
      return this.getPulling() ? 0.0F : 0.03F;
   }

   public void func_70088_a() {
      super.func_70088_a();
   }

   public void setPulling() {
      this.p = true;
   }

   public boolean getPulling() {
      return this.p;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.getPulling() && !this.field_70128_L && (this.field_70173_aa > 30 || this.func_85052_h() == null)) {
         if (this.func_85052_h() != null) {
            grapples.remove(this.func_85052_h().func_145782_y());
         }

         this.func_70106_y();
      }

      if (this.func_85052_h() != null) {
         if (!this.field_70170_p.field_72995_K && !this.field_70128_L && !this.added) {
            if (grapples.containsKey(this.func_85052_h().func_145782_y())) {
               int ii = grapples.get(this.func_85052_h().func_145782_y());
               if (ii != this.func_145782_y()) {
                  Entity e = this.field_70170_p.func_73045_a(ii);
                  if (e != null) {
                     e.func_70106_y();
                  }
               }
            }

            grapples.put(this.func_85052_h().func_145782_y(), this.func_145782_y());
            this.added = true;
         }

         try {
            if (this.func_85052_h() != null
               && grapples.containsKey(this.func_85052_h().func_145782_y())
               && grapples.get(this.func_85052_h().func_145782_y()) != this.func_145782_y()) {
               this.func_70106_y();
            }
         } catch (Exception var13) {
         }

         double dis = this.func_85052_h().func_70032_d(this);
         if (this.func_85052_h() != null && this.getPulling() && !this.field_70128_L) {
            if (this.func_85052_h().func_70093_af()) {
               grapples.remove(this.func_85052_h().func_145782_y());
               this.func_70106_y();
            } else {
               if (!this.field_70170_p.field_72995_K && this.func_85052_h() instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)this.func_85052_h()).field_71135_a.field_147365_f = 0;
               }

               this.func_85052_h().field_70143_R = 0.0F;
               double mx = this.field_70165_t - this.func_85052_h().field_70165_t;
               double my = this.field_70163_u - this.func_85052_h().field_70163_u;
               double mz = this.field_70161_v - this.func_85052_h().field_70161_v;
               double dd = dis;
               if (dis < 8.0) {
                  dd = dis * (8.0 - dis);
               }

               dd = Math.max(1.0E-9, dd);
               mx /= dd * 5.0;
               my /= dd * 5.0;
               mz /= dd * 5.0;
               Vec3d v2 = new Vec3d(mx, my, mz);
               if (v2.func_72433_c() > 0.25) {
                  v2 = v2.func_72432_b();
                  mx = v2.field_72450_a / 4.0;
                  my = v2.field_72448_b / 4.0;
                  mz = v2.field_72449_c / 4.0;
               }

               this.func_85052_h().field_70159_w += mx;
               this.func_85052_h().field_70181_x += my + 0.033;
               this.func_85052_h().field_70179_y += mz;
               if (!this.boost) {
                  this.func_85052_h().field_70181_x += 0.4F;
                  this.boost = true;
               }

               int d = (int)(dis / 2.0);
               if (d == this.prevDist) {
                  this.count++;
               } else {
                  this.count = 0;
               }

               this.prevDist = d;
            }
         }

         if (this.field_70170_p.field_72995_K) {
            if (!this.getPulling()) {
               this.ampl += 0.02F;
            } else {
               this.ampl *= 0.66F;
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public void func_70103_a(byte id) {
      if (id == 6) {
         this.setPulling();
         this.field_70159_w = 0.0;
         this.field_70181_x = 0.0;
         this.field_70179_y = 0.0;
      }
   }

   protected void func_70184_a(RayTraceResult mop) {
      if (!this.field_70170_p.field_72995_K) {
         this.setPulling();
         this.field_70159_w = 0.0;
         this.field_70181_x = 0.0;
         this.field_70179_y = 0.0;
         this.field_70165_t = mop.field_72307_f.field_72450_a;
         this.field_70163_u = mop.field_72307_f.field_72448_b;
         this.field_70161_v = mop.field_72307_f.field_72449_c;
         this.field_70170_p.func_72960_a(this, (byte)6);
      }
   }
}
