package thaumcraft.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.tiles.devices.TCVoidSiphonBlockEntity;

/** Legacy Void Siphon block shell: enabled redstone state, non-full model shape and one-slot GUI. */
public final class TCVoidSiphonBlock extends Block implements EntityBlock {
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    public static final VoxelShape OUTLINE_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
    public static final VoxelShape COLLISION_SHAPE = Shapes.or(
            Block.box(3.0D, 0.0D, 3.0D, 13.0D, 2.0D, 13.0D),
            Block.box(4.0D, 2.0D, 4.0D, 12.0D, 11.0D, 12.0D),
            Block.box(5.0D, 12.0D, 5.0D, 10.0D, 16.0D, 10.0D)
    );

    public TCVoidSiphonBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ENABLED, Boolean.TRUE));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCVoidSiphonBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != TCBlockEntities.VOID_SIPHON.get()) {
            return null;
        }
        return (tickerLevel, pos, tickerState, blockEntity) ->
                TCVoidSiphonBlockEntity.serverTick(
                        tickerLevel,
                        pos,
                        tickerState,
                        (TCVoidSiphonBlockEntity) blockEntity
                );
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(ENABLED, !context.getLevel().hasNeighborSignal(context.getClickedPos()));
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
        return openMenu(level, pos, player)
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
        return openMenu(level, pos, player)
                ? InteractionResult.sidedSuccess(level.isClientSide)
                : InteractionResult.PASS;
    }

    private boolean openMenu(Level level, BlockPos pos, Player player) {
        if (!(level.getBlockEntity(pos) instanceof TCVoidSiphonBlockEntity siphon)) {
            return false;
        }
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(siphon);
        }
        return true;
    }

    @Override
    protected void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block block,
            BlockPos fromPos,
            boolean isMoving
    ) {
        if (!level.isClientSide) {
            boolean enabled = !level.hasNeighborSignal(pos);
            if (state.getValue(ENABLED) != enabled) {
                level.setBlock(pos, state.setValue(ENABLED, enabled), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    protected void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean movedByPiston
    ) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof TCVoidSiphonBlockEntity siphon) {
            Containers.dropContents(level, pos, siphon);
            siphon.clearContent();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ENABLED);
    }
}
