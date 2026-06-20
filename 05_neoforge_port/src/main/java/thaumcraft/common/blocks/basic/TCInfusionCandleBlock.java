package thaumcraft.common.blocks.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.crafting.infusion.TCInfusionSurroundingsInvalidator;

/** Legacy tallow-candle shape and 0.1-per-symmetrical-pair infusion stabilization. */
public final class TCInfusionCandleBlock extends Block implements IInfusionStabiliserExt {
    private static final VoxelShape SHAPE = box(6.0D, 0.0D, 6.0D, 10.0D, 8.0D, 10.0D);

    public TCInfusionCandleBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canStabaliseInfusion(LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public float getStabilizationAmount(LevelReader level, BlockPos pos) {
        return 0.1F;
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

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }
}
