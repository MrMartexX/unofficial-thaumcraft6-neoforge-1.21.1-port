package thaumcraft.common.blocks.world.ore;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;

public class TCCrystalBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final IntegerProperty SIZE = IntegerProperty.create("size", 0, 3);
    public static final IntegerProperty GENERATION = IntegerProperty.create("gen", 1, 4);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape UP_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
    private static final VoxelShape DOWN_SHAPE = Block.box(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    private static final VoxelShape NORTH_SHAPE = Block.box(2.0, 2.0, 4.0, 14.0, 14.0, 16.0);
    private static final VoxelShape SOUTH_SHAPE = Block.box(2.0, 2.0, 0.0, 14.0, 14.0, 12.0);
    private static final VoxelShape WEST_SHAPE = Block.box(4.0, 2.0, 2.0, 16.0, 14.0, 14.0);
    private static final VoxelShape EAST_SHAPE = Block.box(0.0, 2.0, 2.0, 12.0, 14.0, 14.0);

    private final Aspect aspect;
    private final Supplier<Item> shardItem;

    public TCCrystalBlock(Aspect aspect, Supplier<Item> shardItem, BlockBehaviour.Properties properties) {
        super(properties);
        this.aspect = aspect;
        this.shardItem = shardItem;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(SIZE, 0)
                .setValue(GENERATION, 1)
                .setValue(WATERLOGGED, false));
    }

    public Aspect getAspect() {
        return aspect;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SIZE, GENERATION, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case DOWN -> DOWN_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> UP_SHAPE;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction attachedFace = state.getValue(FACING).getOpposite();
        BlockPos attachedPos = pos.relative(attachedFace);
        BlockState attachedState = level.getBlockState(attachedPos);
        return attachedState.isFaceSturdy(level, attachedPos, state.getValue(FACING)) || attachedState.isSolid();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        Direction clickedFace = context.getClickedFace();
        BlockState state = this.defaultBlockState()
                .setValue(FACING, clickedFace)
                .setValue(SIZE, 0)
                .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
        if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
            return state;
        }
        for (Direction dir : Direction.values()) {
            state = state.setValue(FACING, dir);
            if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
                return state;
            }
        }
        return null;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3 + state.getValue(GENERATION)) != 0) {
            return;
        }

        int threshold = 10;
        int size = state.getValue(SIZE);

        if (this.aspect != Aspect.FLUX) {
            float vis = AuraHandler.getAuraChunk(level, pos).map(AuraChunk::getVis).orElse(0.0F);
            float base = (float) AuraHandler.getAuraBase(level, pos);

            if (vis <= threshold) {
                // Dissolving/shrinking when depleted
                if (size > 0) {
                    level.setBlock(pos, state.setValue(SIZE, size - 1), Block.UPDATE_ALL);
                    AuraHelper.addVis(level, pos, threshold);
                } else if (hasNeighborCrystal(level, pos)) {
                    level.removeBlock(pos, false);
                    AuraHelper.addVis(level, pos, threshold);
                }
            } else if (vis > base + threshold) {
                // Growing when abundant
                if (size < 3 && size < 5 - state.getValue(GENERATION) + pos.asLong() % 3L) {
                    if (AuraHelper.drainVis(level, pos, threshold, false) >= threshold) {
                        level.setBlock(pos, state.setValue(SIZE, size + 1), Block.UPDATE_ALL);
                    }
                } else if (state.getValue(GENERATION) < 4) {
                    // Spreading to neighbor
                    trySpread(state, level, pos, random);
                }
            }
        } else {
            // Vitium / Flux crystals consume flux
            float flux = AuraHandler.getAuraChunk(level, pos).map(AuraChunk::getFlux).orElse(0.0F);
            if (flux > AuraHandler.getAuraBase(level, pos) + threshold) {
                if (size < 3 && size < 5 - state.getValue(GENERATION) + pos.asLong() % 3L) {
                    if (AuraHelper.drainFlux(level, pos, threshold, false) >= threshold) {
                        level.setBlock(pos, state.setValue(SIZE, size + 1), Block.UPDATE_ALL);
                    }
                } else if (state.getValue(GENERATION) < 4) {
                    trySpread(state, level, pos, random);
                }
            } else if (flux <= threshold && size > 0) {
                level.setBlock(pos, state.setValue(SIZE, size - 1), Block.UPDATE_ALL);
                AuraHelper.polluteAura(level, pos, threshold, false);
            } else if (flux <= threshold && hasNeighborCrystal(level, pos)) {
                level.removeBlock(pos, false);
                AuraHelper.polluteAura(level, pos, threshold, false);
            }
        }
    }

    private void trySpread(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos target = pos.offset(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1);
        if (target.equals(pos) || random.nextInt(16) != 0) return;
        if (level.isEmptyBlock(target)) {
            for (Direction checkFace : Direction.values()) {
                BlockPos support = target.relative(checkFace);
                BlockState supportState = level.getBlockState(support);
                if (supportState.isSolid() || supportState.isFaceSturdy(level, support, checkFace.getOpposite())) {
                    BlockState newState = this.defaultBlockState()
                            .setValue(FACING, checkFace.getOpposite())
                            .setValue(SIZE, 0);
                    float drained = aspect == Aspect.FLUX
                            ? AuraHelper.drainFlux(level, pos, 10.0F, false)
                            : AuraHelper.drainVis(level, pos, 10.0F, false);
                    if (drained > 0) {
                        int generation = state.getValue(GENERATION);
                        if (random.nextInt(6) == 0) generation--;
                        newState = newState.setValue(GENERATION, generation + 1);
                        level.setBlock(target, newState, Block.UPDATE_ALL);
                    }
                    break;
                }
            }
        }
    }

    private boolean hasNeighborCrystal(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).is(this)) return true;
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        int count = state.getValue(SIZE) + 1;
        return List.of(new ItemStack(this.shardItem.get(), count));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
