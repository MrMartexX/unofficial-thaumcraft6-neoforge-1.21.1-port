package thaumcraft.common.entities.monster.mods;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.FXDispatcher;

public class ChampionModGrim implements IChampionModifierEffect {
   @Override
   public float performEffect(EntityLivingBase boss, EntityLivingBase target, DamageSource source, float amount) {
      if (boss.field_70170_p.field_73012_v.nextFloat() < 0.4F) {
         target.func_70690_d(new PotionEffect(MobEffects.field_82731_v, 200));
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
               -0.02,
               0.0,
               boss.field_70170_p.field_73012_v.nextFloat() * 0.2F,
               boss.field_70170_p.field_73012_v.nextFloat() * 0.2F,
               boss.field_70170_p.field_73012_v.nextFloat() * 0.2F,
               0.8F,
               false,
               640,
               10,
               1,
               8 + boss.field_70170_p.field_73012_v.nextInt(4),
               0,
               0.6F + boss.field_70170_p.field_73012_v.nextFloat() * 0.4F,
               0.0F,
               0
            );
      }
   }

   @Override
   public void preRender(EntityLivingBase boss, RenderLivingBase renderLivingBase) {
      GL11.glColor4f(0.6F, 0.6F, 0.6F, 1.0F);
   }
}
