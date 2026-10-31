package thaumcraft.common.blocks.devices;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.tiles.devices.TCCondenserBlockEntity;

/**
 * TC6 Condenser Lattice block (clean and dirty variants).
 * Multi-block lattice matrix placed above the flux condenser. Connects in all 6 directions.
 * If dirty, can be cleansed by right-clicking with a filter, yielding flux crystals.
 */
public class TCCondenserLatticeBlock extends Block {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    private static final VoxelShape CORE_SHAPE = Block.box(5, 5, 5, 11, 11, 11);
    private static final VoxelShape SHAPE_DOWN = Block.box(5, 0, 5, 11, 5, 11);
    private static final VoxelShape SHAPE_UP = Block.box(5, 11, 5, 11, 16, 11);
    private static final VoxelShape SHAPE_NORTH = Block.box(5, 5, 0, 11, 11, 5);
    private static final VoxelShape SHAPE_SOUTH = Block.box(5, 5, 11, 11, 11, 16);
    private static final VoxelShape SHAPE_WEST = Block.box(0, 5, 5, 5, 11, 11);
    private static final VoxelShape SHAPE_EAST = Block.box(11, 5, 5, 16, 11, 11);

    private final boolean dirty;

    public TCCondenserLatticeBlock(boolean dirty) {
        this(dirty, BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .strength(0.5F, 5.0F)
                .lightLevel(state -> dirty ? 0 : 5)
                .noOcclusion());
    }

    public TCCondenserLatticeBlock(boolean dirty, BlockBehaviour.Properties properties) {
        super(properties);
        this.dirty = dirty;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    public boolean isDirty() {
        return dirty;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    public static boolean canConnectTo(BlockGetter level, BlockPos pos, Direction side) {
        BlockPos adjacent = pos.relative(side);
        BlockState adjState = level.getBlockState(adjacent);
        Block adjBlock = adjState.getBlock();
        if (adjBlock instanceof TCCondenserLatticeBlock) {
            return true;
        }
        return side == Direction.DOWN && adjBlock instanceof TCCondenserBlock;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(DOWN, canConnectTo(level, pos, Direction.DOWN))
                .setValue(UP, canConnectTo(level, pos, Direction.UP))
                .setValue(NORTH, canConnectTo(level, pos, Direction.NORTH))
                .setValue(SOUTH, canConnectTo(level, pos, Direction.SOUTH))
                .setValue(WEST, canConnectTo(level, pos, Direction.WEST))
                .setValue(EAST, canConnectTo(level, pos, Direction.EAST));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        boolean connects = canConnectTo(level, pos, direction);
        return switch (direction) {
            case DOWN -> state.setValue(DOWN, connects);
            case UP -> state.setValue(UP, connects);
            case NORTH -> state.setValue(NORTH, connects);
            case SOUTH -> state.setValue(SOUTH, connects);
            case WEST -> state.setValue(WEST, connects);
            case EAST -> state.setValue(EAST, connects);
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE_SHAPE;
        if (state.getValue(DOWN)) shape = Shapes.or(shape, SHAPE_DOWN);
        if (state.getValue(UP)) shape = Shapes.or(shape, SHAPE_UP);
        if (state.getValue(NORTH)) shape = Shapes.or(shape, SHAPE_NORTH);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SHAPE_SOUTH);
        if (state.getValue(WEST)) shape = Shapes.or(shape, SHAPE_WEST);
        if (state.getValue(EAST)) shape = Shapes.or(shape, SHAPE_EAST);
        return shape;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (dirty && stack.is(TCItems.FILTER.get())) {
            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                if (level.random.nextBoolean()) {
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            new ItemStack(TCItems.CRYSTAL_ESSENCE_VITIUM.get()));
                }
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2F, 1.2F);
                BlockState cleanState = TCBlocks.CONDENSER_LATTICE.get().defaultBlockState()
                        .setValue(DOWN, state.getValue(DOWN))
                        .setValue(UP, state.getValue(UP))
                        .setValue(NORTH, state.getValue(NORTH))
                        .setValue(SOUTH, state.getValue(SOUTH))
                        .setValue(WEST, state.getValue(WEST))
                        .setValue(EAST, state.getValue(EAST));
                level.setBlock(pos, cleanState, 3);
                triggerUpdate(level, pos);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            triggerUpdate(level, pos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide && (block instanceof TCCondenserLatticeBlock || block instanceof TCCondenserBlock)) {
            triggerUpdate(level, pos);
        }
    }

    public void triggerUpdate(Level level, BlockPos pos) {
        Set<Long> visited = new HashSet<>();
        BlockPos condenserPos = findCondenser(level, pos, visited);
        if (condenserPos == null || condenserPos.distSqr(pos) > 74.0) {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    new ItemStack(this.asItem()));
            level.removeBlock(pos, false);
        }
    }

    private BlockPos findCondenser(Level level, BlockPos current, Set<Long> visited) {
        visited.add(current.asLong());
        for (Direction dir : Direction.values()) {
            BlockPos next = current.relative(dir);
            if (!visited.contains(next.asLong())) {
                BlockState bs = level.getBlockState(next);
                if (bs.getBlock() instanceof TCCondenserLatticeBlock) {
                    BlockPos p = findCondenser(level, next, visited);
                    if (p != null) return p;
                }
                if (dir == Direction.DOWN && bs.getBlock() instanceof TCCondenserBlock) {
                    if (level.getBlockEntity(next) instanceof TCCondenserBlockEntity condenser) {
                        condenser.invalidateLatticeCheck();
                    }
                    return next;
                }
            }
        }
        return null;
    }
}
