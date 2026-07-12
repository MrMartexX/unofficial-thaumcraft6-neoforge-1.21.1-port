package thaumcraft.common.blocks.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCFluids;
import thaumcraft.common.registry.TCMobEffects;
import thaumcraft.common.warp.TCPlayerWarpStore;
import thaumcraft.common.warp.TCWarpType;

public class TCPurifyingFluidBlock extends LiquidBlock {
    public static final int LEGACY_QUANTA_PER_BLOCK = 8;
    public static final int LEGACY_MAX_WARP_WARD_DURATION = 32000;
    public static final int LEGACY_WARP_WARD_DURATION_BASE = 200000;

    public TCPurifyingFluidBlock(BlockBehaviour.Properties properties) {
        super(TCFluids.PURIFYING_FLUID.get(), properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        TCLiquidDeathBlock.slowEntityLikeLegacy(state, entity, LEGACY_QUANTA_PER_BLOCK);
        if (!level.isClientSide && isSource(state) && entity instanceof ServerPlayer player
                && !player.hasEffect(TCMobEffects.WARP_WARD)) {
            applyWarpWardAndConsumeSource(player, level, pos);
        }
        super.entityInside(state, level, pos, entity);
    }

    public static boolean isSource(BlockState state) {
        return state.getFluidState().isSource();
    }

    public static int warpWardDurationForPermanentWarp(int permanentWarp) {
        int div = 1;
        if (permanentWarp > 0) {
            div = Math.max(1, (int) Math.sqrt(permanentWarp));
        }
        return Math.min(LEGACY_MAX_WARP_WARD_DURATION, LEGACY_WARP_WARD_DURATION_BASE / div);
    }

    public static boolean applyWarpWardAndConsumeSource(ServerPlayer player, Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.getFluidState().isSource() || player.hasEffect(TCMobEffects.WARP_WARD)) {
            return false;
        }

        int permanentWarp = TCPlayerWarpStore.get(player).get(TCWarpType.PERMANENT);
        int duration = warpWardDurationForPermanentWarp(permanentWarp);
        player.addEffect(new MobEffectInstance(TCMobEffects.WARP_WARD, duration, 0, true, true));
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        return true;
    }
}
