package thaumcraft.common.blocks.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Side-mounted vent with the exact TC6 half-block collision bounds. */
public final class TCSmelterVentBlock extends TCSmelterAuxBlock {
    private static final VoxelShape NORTH = box(2, 2, 0, 14, 14, 8);
    private static final VoxelShape SOUTH = box(2, 2, 8, 14, 14, 16);
    private static final VoxelShape WEST = box(0, 2, 2, 8, 14, 14);
    private static final VoxelShape EAST = box(8, 2, 2, 16, 14, 14);

    public TCSmelterVentBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> NORTH;
        };
    }
}
