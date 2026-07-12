package thaumcraft.common.lib.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PotionWarpWard extends MobEffect {
    public static final int LEGACY_COLOR = 14742263;

    public PotionWarpWard() {
        super(MobEffectCategory.BENEFICIAL, LEGACY_COLOR);
    }
}
