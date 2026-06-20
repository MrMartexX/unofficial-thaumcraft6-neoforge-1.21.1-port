package thaumcraft.common.blocks.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.crafting.infusion.TCInfusionSurroundingsInvalidator;

/** Matrix speed/cost stone with immediate live-profile invalidation. */
public final class TCInfusionModifierBlock extends Block {
    public TCInfusionModifierBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!state.is(oldState.getBlock())) {
            TCInfusionSurroundingsInvalidator.requestNearby(level, pos);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!state.is(newState.getBlock())) {
            TCInfusionSurroundingsInvalidator.requestNearby(level, pos);
        }
    }
}
