package thaumcraft.api.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/** Legacy Vis Exhaust marker effect; caster cooldown code consumes its amplifier. */
public final class PotionVisExhaust extends MobEffect {
    public PotionVisExhaust() {
        super(MobEffectCategory.HARMFUL, 6702199);
    }
}
