package thaumcraft.common.entities.monster.mods;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;

public class ChampionModUndying implements IChampionModifierEffect {
   @Override
   public float performEffect(EntityLivingBase mob, EntityLivingBase target, DamageSource source, float amount) {
      if (mob.field_70173_aa % 20 == 0) {
         mob.func_70691_i(1.0F);
      }

      return amount;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void showFX(EntityLivingBase boss) {
      if (!boss.field_70170_p.field_73012_v.nextBoolean()) {
         float w = boss.field_70170_p.field_73012_v.nextFloat() * boss.field_70130_N;
         float d = boss.field_70170_p.field_73012_v.nextFloat() * boss.field_70130_N;
         float h = boss.field_70170_p.field_73012_v.nextFloat() * boss.field_70131_O;
         FXDispatcher.INSTANCE
            .drawGenericParticles(
               boss.func_174813_aQ().field_72340_a + w,
               boss.func_174813_aQ().field_72338_b + h,
               boss.func_174813_aQ().field_72339_c + d,
               0.0,
               0.03,
               0.0,
               0.1F + boss.field_70170_p.field_73012_v.nextFloat() * 0.1F,
               0.8F + boss.field_70170_p.field_73012_v.nextFloat() * 0.2F,
               0.1F + boss.field_70170_p.field_73012_v.nextFloat() * 0.1F,
               0.9F,
               true,
               69,
               4,
               1,
               4 + boss.field_70170_p.field_73012_v.nextInt(4),
               0,
               0.5F + boss.field_70170_p.field_73012_v.nextFloat() * 0.2F,
               0.0F,
               0
            );
      }
   }

   @Override
   public void preRender(EntityLivingBase boss, RenderLivingBase renderLivingBase) {
   }
}
