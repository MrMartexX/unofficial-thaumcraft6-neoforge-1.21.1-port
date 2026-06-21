package thaumcraft.common.essentia.transport.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.blockentity.TCLegacyTubeBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;

/** Block shell and connection geometry for the TC6 tube family. */
public class TCLegacyTubeBlock extends Block implements EntityBlock {
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;

    private static final VoxelShape NORMAL_CORE = box(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);
    private static final VoxelShape BUFFER_CORE = box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);
    private static final VoxelShape DOWN_ARM = box(6.0D, 0.0D, 6.0D, 10.0D, 5.0D, 10.0D);
    private static final VoxelShape UP_ARM = box(6.0D, 11.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    private static final VoxelShape NORTH_ARM = box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 5.0D);
    private static final VoxelShape SOUTH_ARM = box(6.0D, 6.0D, 11.0D, 10.0D, 10.0D, 16.0D);
    private static final VoxelShape WEST_ARM = box(0.0D, 6.0D, 6.0D, 5.0D, 10.0D, 10.0D);
    private static final VoxelShape EAST_ARM = box(11.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

    private final TCLegacyTubeVariant variant;

    public TCLegacyTubeBlock(TCLegacyTubeVariant variant, BlockBehaviour.Properties properties) {
        super(properties);
        this.variant = variant;
        registerDefaultState(stateDefinition.any()
                .setValue(DOWN, false)
                .setValue(UP, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false));
    }

    public TCLegacyTubeVariant variant() {
        return variant;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TCBlockEntities.createTubeBlockEntity(variant, pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide || type != TCBlockEntities.typeForTube(variant)) {
            return null;
        }
        return (tickerLevel, pos, tickerState, blockEntity) ->
                TCLegacyTubeBlockEntity.serverTick(
                        tickerLevel,
                        pos,
                        tickerState,
                        (TCLegacyTubeBlockEntity) blockEntity
                );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer != null && level.getBlockEntity(pos) instanceof TCLegacyTubeBlockEntity tube) {
            tube.setFacing(legacyPlacementFacing(pos, placer));
            tube.refreshConnectionState();
        }
    }

    public BlockState connectionState(
            Level level,
            BlockPos pos,
            BlockState state,
            TCLegacyTubeBlockEntity tube
    ) {
        BlockState updated = state;
        for (Direction direction : Direction.values()) {
            boolean connected = tube.isConnectable(direction)
                    && level.getCapability(
                            TCEssentiaCapabilities.BLOCK,
                            pos.relative(direction),
                            direction.getOpposite()
                    ) != null;
            updated = updated.setValue(property(direction), connected);
        }
        return updated;
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        VoxelShape shape = variant == TCLegacyTubeVariant.BUFFER ? BUFFER_CORE : NORMAL_CORE;
        if (state.getValue(DOWN)) shape = Shapes.or(shape, DOWN_ARM);
        if (state.getValue(UP)) shape = Shapes.or(shape, UP_ARM);
        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_ARM);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_ARM);
        if (state.getValue(WEST)) shape = Shapes.or(shape, WEST_ARM);
        if (state.getValue(EAST)) shape = Shapes.or(shape, EAST_ARM);
        return shape;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return variant == TCLegacyTubeVariant.BUFFER;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (variant != TCLegacyTubeVariant.BUFFER
                || !(level.getBlockEntity(pos) instanceof TCLegacyTubeBlockEntity tube)) {
            return 0;
        }
        int amount = tube.transportNode().storage().totalAmount();
        return amount <= 0 ? 0 : Math.min(15, 1 + (int) Math.floor(amount / 10.0D * 14.0D));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST);
    }

    private static BooleanProperty property(Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    private static Direction legacyPlacementFacing(BlockPos pos, LivingEntity placer) {
        if (Math.abs(placer.getX() - (pos.getX() + 0.5D)) < 2.0D
                && Math.abs(placer.getZ() - (pos.getZ() + 0.5D)) < 2.0D) {
            double eyeY = placer.getY() + placer.getEyeHeight();
            if (eyeY - pos.getY() > 2.0D) {
                return Direction.UP;
            }
            if (pos.getY() - eyeY > 0.0D) {
                return Direction.DOWN;
            }
        }
        return placer.getDirection().getOpposite();
    }
}
