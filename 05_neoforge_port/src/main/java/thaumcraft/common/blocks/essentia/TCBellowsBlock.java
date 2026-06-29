package thaumcraft.common.blocks.essentia;

import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.phys.shapes.Shapes;

import net.minecraft.world.phys.shapes.CollisionContext;

import net.minecraft.world.level.BlockGetter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.tiles.essentia.TCBellowsBlockEntity;

/** TC6 Bellows facing, redstone-enabled state and server-owned device boundary. */
public class TCBellowsBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");

    private static final VoxelShape CORE_SHAPE = Shapes.or(
            Block.box(2.0D, 2.0D, 2.0D, 14.0D, 4.0D, 14.0D),
            Block.box(3.0D, 4.0D, 3.0D, 13.0D, 12.0D, 13.0D),
            Block.box(2.0D, 7.0D, 2.0D, 14.0D, 9.0D, 14.0D),
            Block.box(2.0D, 12.0D, 2.0D, 14.0D, 14.0D, 14.0D)
    );
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            CORE_SHAPE,
            Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 2.0D)
    );
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            CORE_SHAPE,
            Block.box(6.0D, 6.0D, 14.0D, 10.0D, 10.0D, 16.0D)
    );
    private static final VoxelShape SHAPE_WEST = Shapes.or(
            CORE_SHAPE,
            Block.box(0.0D, 6.0D, 6.0D, 2.0D, 10.0D, 10.0D)
    );
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            CORE_SHAPE,
            Block.box(14.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D)
    );
    private static final VoxelShape SHAPE_UP = Shapes.or(
            CORE_SHAPE,
            Block.box(6.0D, 14.0D, 6.0D, 10.0D, 16.0D, 10.0D)
    );
    private static final VoxelShape SHAPE_DOWN = Shapes.or(
            CORE_SHAPE,
            Block.box(6.0D, 0.0D, 6.0D, 10.0D, 2.0D, 10.0D)
    );
    public TCBellowsBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ENABLED, Boolean.TRUE));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCBellowsBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != TCBlockEntities.BELLOWS.get()) {
            return null;
        }
        if (level.isClientSide) {
            return (tickerLevel, pos, tickerState, blockEntity) ->
                    TCBellowsBlockEntity.clientTick(
                            tickerLevel,
                            pos,
                            tickerState,
                            (TCBellowsBlockEntity) blockEntity
                    );
        }
        return (tickerLevel, pos, tickerState, blockEntity) ->
                TCBellowsBlockEntity.serverTick(
                        tickerLevel,
                        pos,
                        tickerState,
                        (TCBellowsBlockEntity) blockEntity
                );
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeFor(state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeFor(state);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    private static VoxelShape shapeFor(BlockState state) {
        Direction facing = state.hasProperty(FACING) ? state.getValue(FACING) : Direction.NORTH;
        return switch (facing) {
            case DOWN -> SHAPE_DOWN;
            case UP -> SHAPE_UP;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            case NORTH -> SHAPE_NORTH;
        };
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getClickedFace().getOpposite())
                .setValue(ENABLED, !context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    protected void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block neighbourBlock,
            BlockPos neighbourPos,
            boolean movedByPiston
    ) {
        if (!level.isClientSide) {
            boolean enabled = !level.hasNeighborSignal(pos);
            if (state.getValue(ENABLED) != enabled) {
                level.setBlock(pos, state.setValue(ENABLED, enabled), Block.UPDATE_ALL);
            }
        }
        super.neighborChanged(state, level, pos, neighbourBlock, neighbourPos, movedByPiston);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ENABLED);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
