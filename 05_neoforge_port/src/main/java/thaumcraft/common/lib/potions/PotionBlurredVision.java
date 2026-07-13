package thaumcraft.common.lib.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class PotionBlurredVision extends MobEffect {
    public static final int LEGACY_COLOR = 8421504;

    public PotionBlurredVision() {
        super(MobEffectCategory.HARMFUL, LEGACY_COLOR);
    }
}
