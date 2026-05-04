package thaumcraft.common.entities.monster.mods;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import thaumcraft.client.fx.FXDispatcher;

public class ChampionModArmored implements IChampionModifierEffect {
   @Override
   public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
      if (!source.func_76363_c()) {
         float f1 = amount * 19.0F;
         amount = f1 / 25.0F;
      }

      return amount;
   }

   @Override
   public void showFX(EntityLivingBase boss) {
      if (boss.field_70170_p.field_73012_v.nextInt(4) == 0) {
         float w = boss.field_70170_p.field_73012_v.nextFloat() * boss.field_70130_N;
         float d = boss.field_70170_p.field_73012_v.nextFloat() * boss.field_70130_N;
         float h = boss.field_70170_p.field_73012_v.nextFloat() * boss.field_70131_O;
         FXDispatcher.INSTANCE
            .drawGenericParticles(
               boss.func_174813_aQ().field_72340_a + w,
               boss.func_174813_aQ().field_72338_b + h,
               boss.func_174813_aQ().field_72339_c + d,
               0.0,
               0.0,
               0.0,
               0.9F,
               0.9F,
               0.9F + boss.field_70170_p.field_73012_v.nextFloat() * 0.1F,
               0.7F,
               false,
               448,
               9,
               1,
               5 + boss.field_70170_p.field_73012_v.nextInt(4),
               0,
               0.6F + boss.field_70170_p.field_73012_v.nextFloat() * 0.2F,
               0.0F,
               0
            );
      }
   }

   @Override
   public void preRender(EntityLivingBase boss, RenderLivingBase renderLivingBase) {
   }
}
