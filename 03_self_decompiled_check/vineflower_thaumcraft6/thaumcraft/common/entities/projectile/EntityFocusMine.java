package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;

public class EntityFocusMine extends EntityThrowable implements IEntityAdditionalSpawnData {
   FocusPackage focusPackage;
   boolean friendly = false;
   private static final DataParameter<Boolean> ARMED = EntityDataManager.func_187226_a(EntityFocusMine.class, DataSerializers.field_187198_h);
   public int counter = 40;
   FocusEffect[] effects = null;

   public EntityFocusMine(World par1World) {
      super(par1World);
      this.func_70105_a(0.15F, 0.15F);
   }

   public EntityFocusMine(FocusPackage pack, Trajectory trajectory, boolean friendly) {
      super(pack.world, pack.getCaster());
      this.focusPackage = pack;
      this.friendly = friendly;
      this.func_70107_b(trajectory.source.field_72450_a, trajectory.source.field_72448_b, trajectory.source.field_72449_c);
      this.func_70186_c(trajectory.direction.field_72450_a, trajectory.direction.field_72448_b, trajectory.direction.field_72449_c, 0.0F, 0.0F);
      this.func_70105_a(0.15F, 0.15F);
   }

   public void func_70088_a() {
      super.func_70088_a();
      this.func_184212_Q().func_187214_a(ARMED, false);
   }

   public boolean getIsArmed() {
      return (Boolean)this.func_184212_Q().func_187225_a(ARMED);
   }

   public void setIsArmed(boolean par1) {
      this.func_184212_Q().func_187227_b(ARMED, par1);
   }

   protected float func_70185_h() {
      return 0.01F;
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
      nbt.func_74757_a("armed", this.getIsArmed());
      nbt.func_74782_a("pack", this.focusPackage.serialize());
      nbt.func_74757_a("friendly", this.friendly);
   }

   public void func_70037_a(NBTTagCompound nbt) {
      super.func_70037_a(nbt);
      this.friendly = nbt.func_74767_n("friendly");
      this.setIsArmed(nbt.func_74767_n("armed"));
      if (this.getIsArmed()) {
         this.counter = 0;
      }

      try {
         this.focusPackage = new FocusPackage();
         this.focusPackage.deserialize(nbt.func_74775_l("pack"));
      } catch (Exception var3) {
      }
   }

   protected void func_70184_a(RayTraceResult mop) {
      if (mop != null && this.func_85052_h() != null) {
         this.setIsArmed(true);
      }
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.func_145771_j(this.field_70165_t, this.field_70163_u, this.field_70161_v)) {
         this.field_70159_w *= 0.25;
         this.field_70181_x *= 0.25;
         this.field_70179_y *= 0.25;
      }

      if (this.field_70173_aa > 1200 || !this.field_70170_p.field_72995_K && this.func_85052_h() == null) {
         this.func_70106_y();
      }

      if (this.func_70089_S() && this.getIsArmed()) {
         if (this.counter > 0) {
            this.counter--;
         }

         if (this.counter <= 0 && this.field_70173_aa % 5 == 0) {
            if (this.field_70170_p.field_72995_K) {
               if (this.effects == null) {
                  this.effects = this.focusPackage.getFocusEffects();
               }

               if (this.effects != null && this.effects.length > 0) {
                  FocusEffect eff = this.effects[this.field_70146_Z.nextInt(this.effects.length)];
                  eff.renderParticleFX(
                     this.field_70170_p,
                     this.field_70165_t + this.field_70170_p.field_73012_v.nextGaussian() * 0.1,
                     this.field_70163_u + this.field_70170_p.field_73012_v.nextGaussian() * 0.1,
                     this.field_70161_v + this.field_70170_p.field_73012_v.nextGaussian() * 0.1,
                     this.field_70170_p.field_73012_v.nextGaussian() * 0.01F,
                     this.field_70170_p.field_73012_v.nextGaussian() * 0.01F,
                     this.field_70170_p.field_73012_v.nextGaussian() * 0.01F
                  );
               }
            } else {
               List<EntityLivingBase> list2 = EntityUtils.getEntitiesInRange(
                  this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, this, EntityLivingBase.class, 1.0
               );
               int d = 0;

               for (final EntityLivingBase e : list2) {
                  if (!e.field_70128_L
                     && (this.friendly ? EntityUtils.isFriendly(this.focusPackage.getCaster(), e) : !EntityUtils.isFriendly(this.focusPackage.getCaster(), e))) {
                     final Vec3d epv = e.func_174791_d().func_72441_c(0.0, e.field_70131_O / 2.0F, 0.0);
                     ServerEvents.addRunnableServer(
                        this.func_130014_f_(),
                        new Runnable() {
                           @Override
                           public void run() {
                              RayTraceResult ray = new RayTraceResult(e);
                              ray.field_72307_f = e.func_174791_d().func_72441_c(0.0, e.field_70131_O / 2.0F, 0.0);
                              FocusEngine.runFocusPackage(
                                 EntityFocusMine.this.focusPackage.copy(EntityFocusMine.this.func_85052_h()),
                                 new Trajectory[]{
                                    new Trajectory(EntityFocusMine.this.func_174791_d(), epv.func_178788_d(EntityFocusMine.this.func_174791_d()).func_72432_b())
                                 },
                                 new RayTraceResult[]{ray}
                              );
                           }
                        },
                        d++
                     );
                  }
               }

               if (d > 0) {
                  this.func_70106_y();
               }
            }
         }
      }
   }
}
