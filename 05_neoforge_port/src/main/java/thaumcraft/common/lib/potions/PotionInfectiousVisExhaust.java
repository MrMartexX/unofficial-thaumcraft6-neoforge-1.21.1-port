package thaumcraft.common.lib.potions;

import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import thaumcraft.common.registry.TCMobEffects;

/** TC6 infectious vis exhaustion: every 40 ticks it spreads, then degrades to normal vis exhaust. */
public final class PotionInfectiousVisExhaust extends MobEffect {
    public PotionInfectiousVisExhaust() {
        super(MobEffectCategory.HARMFUL, 6706551);
    }

    @Override
    public boolean applyEffectTick(LivingEntity target, int amplifier) {
        List<LivingEntity> targets = target.level().getEntitiesOfClass(
                LivingEntity.class,
                target.getBoundingBox().inflate(4.0D)
        );
        for (LivingEntity living : targets) {
            if (living.hasEffect(TCMobEffects.INFECTIOUS_VIS_EXHAUST)) {
                continue;
            }
            MobEffectInstance effect;
            if (amplifier > 0) {
                effect = new MobEffectInstance(TCMobEffects.INFECTIOUS_VIS_EXHAUST, 6000, amplifier - 1, false, true);
            } else {
                effect = new MobEffectInstance(TCMobEffects.VIS_EXHAUST, 6000, 0, false, true);
            }
            effect.getCures().clear();
            living.addEffect(effect);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }
}
