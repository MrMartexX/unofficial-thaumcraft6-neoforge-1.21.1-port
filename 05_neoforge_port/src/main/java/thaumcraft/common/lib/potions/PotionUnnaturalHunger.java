package thaumcraft.common.lib.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class PotionUnnaturalHunger extends MobEffect {
    public static final int LEGACY_COLOR = 4482611;

    public PotionUnnaturalHunger() {
        super(MobEffectCategory.HARMFUL, LEGACY_COLOR);
    }

    @Override
    public boolean applyEffectTick(LivingEntity target, int amplifier) {
        if (!target.level().isClientSide && target instanceof Player player) {
            player.causeFoodExhaustion(0.025F * (amplifier + 1));
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
