package thaumcraft.common.blocks.devices;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.items.TCMirrorBlockItem;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.tiles.devices.TCMirrorBlockEntity;
import thaumcraft.common.tiles.devices.TCMirrorEssentiaBlockEntity;

public final class TCMirrorBlock extends Block implements EntityBlock {
    public enum Kind {
        ITEM,
        ESSENTIA
    }

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final VoxelShape DOWN_SHAPE = box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape UP_SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    private static final VoxelShape NORTH_SHAPE = box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SOUTH_SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
    private static final VoxelShape WEST_SHAPE = box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_SHAPE = box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);
    private final Kind kind;

    public TCMirrorBlock(BlockBehaviour.Properties properties, Kind kind) {
        super(properties);
        this.kind = kind;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.DOWN));
    }

    public Kind kind() {
        return kind;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getClickedFace());
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos support = pos.relative(facing.getOpposite());
        return level.getBlockState(support).isFaceSturdy(level, support, facing);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        super.neighborChanged(state, level, pos, block, fromPos, movedByPiston);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return switch (kind) {
            case ITEM -> new TCMirrorBlockEntity(pos, state);
            case ESSENTIA -> new TCMirrorEssentiaBlockEntity(pos, state);
        };
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        if (kind == Kind.ITEM && type == TCBlockEntities.MIRROR.get()) {
            return (tickerLevel, pos, tickerState, blockEntity) -> TCMirrorBlockEntity.serverTick(
                    tickerLevel,
                    pos,
                    tickerState,
                    (TCMirrorBlockEntity) blockEntity
            );
        }
        if (kind == Kind.ESSENTIA && type == TCBlockEntities.MIRROR_ESSENTIA.get()) {
            return (tickerLevel, pos, tickerState, blockEntity) -> TCMirrorEssentiaBlockEntity.serverTick(
                    tickerLevel,
                    pos,
                    tickerState,
                    (TCMirrorEssentiaBlockEntity) blockEntity
            );
        }
        return null;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            case DOWN -> DOWN_SHAPE;
        };
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide
                && kind == Kind.ITEM
                && entity instanceof ItemEntity itemEntity
                && level.getBlockEntity(pos) instanceof TCMirrorBlockEntity mirror) {
            mirror.transportEntity(itemEntity);
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof TCMirrorBlockEntity mirror) {
            ItemStack drop = TCMirrorBlockItem.stackFromMirror(mirror);
            mirror.invalidateLink();
            return List.of(drop);
        }
        if (blockEntity instanceof TCMirrorEssentiaBlockEntity mirror) {
            ItemStack drop = TCMirrorBlockItem.stackFromMirror(mirror);
            mirror.invalidateLink();
            return List.of(drop);
        }
        return super.getDrops(state, params);
    }
}
