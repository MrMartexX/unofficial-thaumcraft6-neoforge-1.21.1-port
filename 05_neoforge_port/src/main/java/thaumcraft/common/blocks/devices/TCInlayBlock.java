package thaumcraft.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.api.crafting.IInfusionStabiliserExt;

public final class TCInlayBlock extends Block implements IInfusionStabiliserExt {
    public static final EnumProperty<Attach> NORTH = EnumProperty.create("north", Attach.class);
    public static final EnumProperty<Attach> EAST = EnumProperty.create("east", Attach.class);
    public static final EnumProperty<Attach> SOUTH = EnumProperty.create("south", Attach.class);
    public static final EnumProperty<Attach> WEST = EnumProperty.create("west", Attach.class);
    public static final IntegerProperty CHARGE = IntegerProperty.create("charge", 0, 15);
    private static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public TCInlayBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, Attach.NONE)
                .setValue(EAST, Attach.NONE)
                .setValue(SOUTH, Attach.NONE)
                .setValue(WEST, Attach.NONE)
                .setValue(CHARGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, CHARGE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return connections(context.getLevel(), context.getClickedPos(), defaultBlockState());
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.setBlock(pos, connections(level, pos, state), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            TCInlayNetwork.recalculateAround(serverLevel, pos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        BlockState connected = connections(level, pos, state);
        if (connected != state) {
            level.setBlock(pos, connected, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        }
        TCInlayNetwork.recalculateAround(serverLevel, pos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel serverLevel) {
            TCInlayNetwork.recalculateAround(serverLevel, pos);
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean canStabaliseInfusion(LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public float getStabilizationAmount(LevelReader level, BlockPos pos) {
        return 0.025F;
    }

    private static BlockState connections(BlockGetter level, BlockPos pos, BlockState state) {
        return state
                .setValue(NORTH, attachment(level, pos, Direction.NORTH))
                .setValue(EAST, attachment(level, pos, Direction.EAST))
                .setValue(SOUTH, attachment(level, pos, Direction.SOUTH))
                .setValue(WEST, attachment(level, pos, Direction.WEST));
    }

    private static Attach attachment(BlockGetter level, BlockPos pos, Direction direction) {
        BlockPos adjacent = pos.relative(direction);
        BlockState state = level.getBlockState(adjacent);
        if (TCInlayNetwork.isNetworkNode(state)) {
            return Attach.SIDE;
        }
        return state.is(thaumcraft.common.registry.TCBlocks.STABILIZER.get()) ? Attach.EXT : Attach.NONE;
    }

    public enum Attach implements StringRepresentable {
        SIDE("side"), NONE("none"), EXT("ext");

        private final String serializedName;

        Attach(String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }
}
