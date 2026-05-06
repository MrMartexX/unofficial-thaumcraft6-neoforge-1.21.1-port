package thaumcraft.common.blocks.world.plants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TCLogBlock extends RotatedPillarBlock {
    public TCLogBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            markNearbyLegacyLeaves(level, pos);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private static void markNearbyLegacyLeaves(Level level, BlockPos pos) {
        int radius = 4;

        if (!level.isAreaLoaded(pos, radius + 1)) {
            return;
        }

        for (BlockPos scanPos : BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius))) {
            BlockState leafState = level.getBlockState(scanPos);

            if (leafState.getBlock() instanceof TCLeavesBlock) {
                BlockState decayingState = TCLeavesBlock.beginLegacyDecay(leafState);

                if (decayingState != leafState) {
                    level.setBlock(scanPos, decayingState, 3);
                }
            }
        }
    }
}