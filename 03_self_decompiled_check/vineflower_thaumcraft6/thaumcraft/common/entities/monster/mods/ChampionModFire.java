package thaumcraft.common.entities.monster.mods;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.FXDispatcher;

public class ChampionModFire implements IChampionModifierEffect {
   @Override
   public float performEffect(EntityLivingBase boss, EntityLivingBase target, DamageSource source, float amount) {
      if (boss.field_70170_p.field_73012_v.nextFloat() < 0.4F) {
         target.func_70015_d(4);
      }

      return amount;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void showFX(EntityLivingBase boss) {
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
            0.9F + boss.field_70170_p.field_73012_v.nextFloat() * 0.1F,
            1.0F,
            1.0F,
            0.7F,
            false,
            640,
            10,
            1,
            8 + boss.field_70170_p.field_73012_v.nextInt(4),
            0,
            0.7F + boss.field_70170_p.field_73012_v.nextFloat() * 0.2F,
            0.0F,
            0
         );
   }

   @Override
   public void preRender(EntityLivingBase boss, RenderLivingBase renderLivingBase) {
      GL11.glColor4f(1.0F, 0.75F, 5.0F, 1.0F);
   }
}
