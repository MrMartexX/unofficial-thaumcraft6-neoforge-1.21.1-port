package thaumcraft.common.blocks.crafting;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.tiles.crafting.TCThaumatoriumBlockEntity;
import thaumcraft.common.tiles.crafting.TCThaumatoriumTopBlockEntity;

public final class TCThaumatoriumBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape BOTTOM_SHAPE = Shapes.block();
    private static final VoxelShape TOP_SHAPE = Shapes.block();

    private final boolean top;

    public TCThaumatoriumBlock(BlockBehaviour.Properties properties, boolean top) {
        super(properties);
        this.top = top;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public boolean isTop() {
        return top;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return top ? new TCThaumatoriumTopBlockEntity(pos, state) : new TCThaumatoriumBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        if (!top && type == TCBlockEntities.THAUMATORIUM.get()) {
            return (tickerLevel, pos, tickerState, blockEntity) ->
                    TCThaumatoriumBlockEntity.serverTick(
                            tickerLevel,
                            pos,
                            tickerState,
                            (TCThaumatoriumBlockEntity) blockEntity
                    );
        }
        if (top && type == TCBlockEntities.THAUMATORIUM_TOP.get()) {
            return (tickerLevel, pos, tickerState, blockEntity) ->
                    TCThaumatoriumTopBlockEntity.serverTick(
                            tickerLevel,
                            pos,
                            tickerState,
                            (TCThaumatoriumTopBlockEntity) blockEntity
                    );
        }
        return null;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (top) {
            return null;
        }
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos.below()).is(TCBlocks.CRUCIBLE.get())) {
            return null;
        }
        BlockState above = level.getBlockState(pos.above());
        if (!above.canBeReplaced(context)) {
            return null;
        }
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(
            Level level,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack stack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (top || level.isClientSide) {
            return;
        }
        BlockPos topPos = pos.above();
        if (level.getBlockState(topPos).canBeReplaced()) {
            level.setBlock(
                    topPos,
                    TCBlocks.THAUMATORIUM_TOP.get().defaultBlockState().setValue(FACING, state.getValue(FACING)),
                    Block.UPDATE_ALL
            );
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        return openMenu(level, bottomPos(pos), player)
                ? ItemInteractionResult.sidedSuccess(level.isClientSide)
                : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        return openMenu(level, bottomPos(pos), player)
                ? InteractionResult.sidedSuccess(level.isClientSide)
                : InteractionResult.PASS;
    }

    private BlockPos bottomPos(BlockPos pos) {
        return top ? pos.below() : pos;
    }

    private boolean openMenu(Level level, BlockPos pos, Player player) {
        if (!(level.getBlockEntity(pos) instanceof TCThaumatoriumBlockEntity thaumatorium)) {
            return false;
        }
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(thaumatorium);
        }
        return true;
    }

    @Override
    protected void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean movedByPiston
    ) {
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof TCThaumatoriumBlockEntity thaumatorium) {
                Containers.dropContents(level, pos, thaumatorium);
                thaumatorium.clearContent();
            }
            BlockPos counterpart = top ? pos.below() : pos.above();
            Block expected = top ? TCBlocks.THAUMATORIUM.get() : TCBlocks.THAUMATORIUM_TOP.get();
            if (level.getBlockState(counterpart).is(expected)) {
                level.setBlock(counterpart, TCBlocks.METAL_ALCHEMICAL.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block block,
            BlockPos fromPos,
            boolean isMoving
    ) {
        if (level.isClientSide) {
            return;
        }
        if (top) {
            if (!level.getBlockState(pos.below()).is(TCBlocks.THAUMATORIUM.get())) {
                level.setBlock(pos, TCBlocks.METAL_ALCHEMICAL.get().defaultBlockState(), Block.UPDATE_ALL);
            }
            return;
        }
        if (!level.getBlockState(pos.below()).is(TCBlocks.CRUCIBLE.get())) {
            if (level.getBlockState(pos.above()).is(TCBlocks.THAUMATORIUM_TOP.get())) {
                level.setBlock(pos.above(), TCBlocks.METAL_ALCHEMICAL.get().defaultBlockState(), Block.UPDATE_ALL);
            }
            level.setBlock(pos, TCBlocks.METAL_ALCHEMICAL.get().defaultBlockState(), Block.UPDATE_ALL);
        } else if (!level.getBlockState(pos.above()).is(TCBlocks.THAUMATORIUM_TOP.get())) {
            level.setBlock(pos, TCBlocks.METAL_ALCHEMICAL.get().defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return !top;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof TCThaumatoriumBlockEntity thaumatorium ? thaumatorium.comparatorSignal() : 0;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return top ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return top ? TOP_SHAPE : BOTTOM_SHAPE;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(new ItemStack(TCItems.METAL_ALCHEMICAL.get()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
