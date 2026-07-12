package thaumcraft.common.blocks.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.lib.damage.TCDamageTypes;
import thaumcraft.common.registry.TCFluids;

public class TCLiquidDeathBlock extends LiquidBlock {
    public static final int LEGACY_QUANTA_PER_BLOCK = 4;

    public TCLiquidDeathBlock(BlockBehaviour.Properties properties) {
        super(TCFluids.LIQUID_DEATH.get(), properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        slowEntityLikeLegacy(state, entity, LEGACY_QUANTA_PER_BLOCK);
        if (!level.isClientSide && entity instanceof LivingEntity livingEntity) {
            livingEntity.hurt(TCDamageTypes.dissolve(level), legacyDamageForState(state));
        }
        super.entityInside(state, level, pos, entity);
    }

    public static float legacyDamageForState(BlockState state) {
        return legacyDamageForLevel(legacyFluidLevel(state));
    }

    public static float legacyDamageForLevel(int level) {
        return Math.max(1, 5 - Math.min(LEGACY_QUANTA_PER_BLOCK, Math.max(0, level)));
    }

    public static void slowEntityLikeLegacy(BlockState state, Entity entity, int quantaPerBlock) {
        double multiplier = legacyHorizontalSlowdownMultiplier(legacyFluidLevel(state), quantaPerBlock);
        Vec3 motion = entity.getDeltaMovement();
        entity.setDeltaMovement(motion.x * multiplier, motion.y, motion.z * multiplier);
    }

    public static double legacyHorizontalSlowdownMultiplier(int fluidLevel, int quantaPerBlock) {
        int level = Math.min(quantaPerBlock, Math.max(0, fluidLevel));
        double quantaPercentage = (double) Math.max(0, quantaPerBlock - level) / (double) quantaPerBlock;
        return 1.0D - quantaPercentage / 2.0D;
    }

    public static int legacyFluidLevel(BlockState state) {
        if (!state.hasProperty(LEVEL)) {
            return 0;
        }
        return Math.max(0, state.getValue(LEVEL));
    }
}
