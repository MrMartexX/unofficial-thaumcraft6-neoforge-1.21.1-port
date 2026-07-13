package thaumcraft.common.lib.potions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import thaumcraft.common.registry.TCBlocks;

public final class PotionThaumarhia extends MobEffect {
    public static final int LEGACY_COLOR = 6702199;

    public PotionThaumarhia() {
        super(MobEffectCategory.HARMFUL, LEGACY_COLOR);
    }

    @Override
    public boolean applyEffectTick(LivingEntity target, int amplifier) {
        Level level = target.level();
        BlockPos pos = BlockPos.containing(target.getX(), target.getY(), target.getZ());
        if (!level.isClientSide && level.random.nextInt(15) == 0 && level.isEmptyBlock(pos)) {
            level.setBlockAndUpdate(pos, TCBlocks.FLUX_GOO.get().defaultBlockState());
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
