package thaumcraft.api.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.tags.EntityTypeTags;

/** Legacy Flux Taint effect. Tainted-mob healing remains owned by the future taint entity subsystem. */
public final class PotionFluxTaint extends MobEffect {
    public PotionFluxTaint() {
        super(MobEffectCategory.HARMFUL, 6697847);
    }

    @Override
    public boolean applyEffectTick(LivingEntity target, int amplifier) {
        if (!target.getType().is(EntityTypeTags.UNDEAD)) {
            target.hurt(target.damageSources().magic(), 1.0F);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int interval = 40 >> amplifier;
        return interval <= 0 || duration % interval == 0;
    }
}
