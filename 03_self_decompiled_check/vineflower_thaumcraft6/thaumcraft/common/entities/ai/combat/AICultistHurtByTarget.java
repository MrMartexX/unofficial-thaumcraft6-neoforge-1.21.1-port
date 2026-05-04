package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.entities.monster.cult.EntityCultist;

public class AICultistHurtByTarget extends EntityAITarget {
   boolean entityCallsForHelp;
   private int revengeTimerOld;

   public AICultistHurtByTarget(EntityCreature owner, boolean callsHelp) {
      super(owner, false);
      this.entityCallsForHelp = callsHelp;
      this.func_75248_a(1);
   }

   public boolean func_75250_a() {
      int i = this.field_75299_d.func_142015_aE();
      EntityLivingBase entitylivingbase = this.field_75299_d.func_70643_av();
      return i != this.revengeTimerOld && entitylivingbase != null && this.func_75296_a(entitylivingbase, false);
   }

   public void func_75249_e() {
      this.field_75299_d.func_70624_b(this.field_75299_d.func_70643_av());
      this.field_188509_g = this.field_75299_d.func_70638_az();
      this.revengeTimerOld = this.field_75299_d.func_142015_aE();
      this.field_188510_h = 300;
      if (this.entityCallsForHelp) {
         this.alertOthers();
      }

      super.func_75249_e();
   }

   protected void alertOthers() {
      double d0 = this.func_111175_f();

      for (EntityCreature entitycreature : this.field_75299_d
         .field_70170_p
         .func_72872_a(
            EntityCultist.class,
            new AxisAlignedBB(
                  this.field_75299_d.field_70165_t,
                  this.field_75299_d.field_70163_u,
                  this.field_75299_d.field_70161_v,
                  this.field_75299_d.field_70165_t + 1.0,
                  this.field_75299_d.field_70163_u + 1.0,
                  this.field_75299_d.field_70161_v + 1.0
               )
               .func_72314_b(d0, 10.0, d0)
         )) {
         if (this.field_75299_d != entitycreature
            && entitycreature.func_70638_az() == null
            && (
               !(this.field_75299_d instanceof EntityTameable)
                  || ((EntityTameable)this.field_75299_d).func_70902_q() == ((EntityTameable)entitycreature).func_70902_q()
            )
            && !entitycreature.func_184191_r(this.field_75299_d.func_70643_av())) {
            this.setEntityAttackTarget(entitycreature, this.field_75299_d.func_70643_av());
         }
      }
   }

   protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn) {
      creatureIn.func_70624_b(entityLivingBaseIn);
   }
}
