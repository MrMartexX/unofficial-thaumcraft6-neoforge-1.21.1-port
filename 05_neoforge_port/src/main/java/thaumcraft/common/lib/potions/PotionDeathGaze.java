package thaumcraft.common.lib.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class PotionDeathGaze extends MobEffect {
    public static final int LEGACY_COLOR = 6702131;

    public PotionDeathGaze() {
        super(MobEffectCategory.HARMFUL, LEGACY_COLOR);
    }
}
