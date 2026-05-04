package thaumcraft.common.golems.parts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.init.SoundEvents;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.common.entities.projectile.EntityGolemDart;
import thaumcraft.common.golems.ai.AIArrowAttack;

public class GolemArmDart implements GolemArm.IArmFunction {
   @Override
   public void onMeleeAttack(IGolemAPI golem, Entity target) {
   }

   @Override
   public void onRangedAttack(IGolemAPI golem, EntityLivingBase target, float range) {
      EntityGolemDart entityarrow = new EntityGolemDart(golem.getGolemWorld(), golem.getGolemEntity());
      float dmg = (float)golem.getGolemEntity().func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e() / 3.0F;
      entityarrow.func_70239_b(dmg + range + golem.getGolemWorld().field_73012_v.nextGaussian() * 0.25);
      double d0 = target.field_70165_t - golem.getGolemEntity().field_70165_t;
      double d1 = target.func_174813_aQ().field_72338_b + target.func_70047_e() + range * range - entityarrow.field_70163_u;
      double d2 = target.field_70161_v - golem.getGolemEntity().field_70161_v;
      entityarrow.func_70186_c(d0, d1, d2, 1.6F, 3.0F);
      golem.getGolemWorld().func_72838_d(entityarrow);
      golem.getGolemEntity().func_184185_a(SoundEvents.field_187737_v, 1.0F, 1.0F / (golem.getGolemWorld().field_73012_v.nextFloat() * 0.4F + 0.8F));
   }

   @Override
   public EntityAIAttackRanged getRangedAttackAI(IRangedAttackMob golem) {
      return new AIArrowAttack(golem, 1.0, 20, 25, 16.0F);
   }

   @Override
   public void onUpdateTick(IGolemAPI golem) {
   }
}
