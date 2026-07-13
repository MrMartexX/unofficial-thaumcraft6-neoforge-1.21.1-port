package thaumcraft.common.lib.potions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class PotionSunScorned extends MobEffect {
    public static final int LEGACY_COLOR = 16308330;

    public PotionSunScorned() {
        super(MobEffectCategory.HARMFUL, LEGACY_COLOR);
    }

    @Override
    public boolean applyEffectTick(LivingEntity target, int amplifier) {
        Level level = target.level();
        if (level.isClientSide) {
            return true;
        }

        float light = target.getLightLevelDependentMagicValue();
        BlockPos pos = BlockPos.containing(target.getX(), target.getY(), target.getZ());
        if (light > 0.5F && level.random.nextFloat() * 30.0F < (light - 0.4F) * 2.0F && level.canSeeSky(pos)) {
            target.igniteForSeconds(4.0F);
        } else if (light < 0.25F && level.random.nextFloat() > light * 2.0F) {
            target.heal(1.0F);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }
}
