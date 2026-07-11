package thaumcraft.common.blocks.world.taint;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlocks;

/** Legacy taintwood log: stationary taint terrain that dies into Flux Goo outside seed range. */
public final class TCTaintLogBlock extends RotatedPillarBlock {
    public static final MapCodec<TCTaintLogBlock> CODEC = simpleCodec(TCTaintLogBlock::new);

    public TCTaintLogBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends RotatedPillarBlock> codec() {
        return CODEC;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!TCTaintHelper.isNearTaintSeed(level, pos)) {
            die(level, pos);
        } else {
            TCTaintHelper.spreadFibres(level, pos);
        }
    }

    public static void die(Level level, BlockPos pos) {
        level.setBlock(pos, TCBlocks.FLUX_GOO.get().defaultBlockState(), Block.UPDATE_ALL);
    }
}
