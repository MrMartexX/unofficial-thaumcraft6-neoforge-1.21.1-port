package thaumcraft.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.tiles.devices.TCPotionSprayerBlockEntity;

/**
 * TC6 Potion Sprayer block.
 * Directional automated aerosol dispersal mechanism. Draws essentia to replicate
 * inserted potion effects and sprays them in a conical mist upon redstone activation.
 */
public class TCPotionSprayerBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    public TCPotionSprayerBlock() {
        this(BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .strength(2.0F, 10.0F)
                .noOcclusion());
    }

    public TCPotionSprayerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(ENABLED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ENABLED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(ENABLED, true);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCPotionSprayerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != TCBlockEntities.POTION_SPRAYER.get()) {
            return null;
        }
        return (tickerLevel, pos, tickerState, blockEntity) ->
                TCPotionSprayerBlockEntity.serverTick(tickerLevel, pos, tickerState, (TCPotionSprayerBlockEntity) blockEntity);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof TCPotionSprayerBlockEntity sprayer) {
            if (!player.isShiftKeyDown()) {
                if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    serverPlayer.openMenu(sprayer, buffer -> buffer.writeBlockPos(pos));
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            // Sneak click allows quick inserting/extracting in-world
            if (!level.isClientSide) {
                ItemStack stored = sprayer.getPotionItem();
                boolean isPotion = stack.has(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
                if (!stored.isEmpty() && stack.isEmpty()) {
                    player.setItemInHand(hand, stored.copy());
                    sprayer.setPotionItem(ItemStack.EMPTY);
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.4F, 1.0F);
                    return ItemInteractionResult.SUCCESS;
                } else if (isPotion) {
                    ItemStack toInsert = stack.split(1);
                    if (!stored.isEmpty()) {
                        Containers.dropItemStack(level, player.getX(), player.getY(), player.getZ(), stored);
                    }
                    sprayer.setPotionItem(toInsert);
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.4F, 1.0F);
                    return ItemInteractionResult.SUCCESS;
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected net.minecraft.world.InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (level.getBlockEntity(pos) instanceof TCPotionSprayerBlockEntity sprayer) {
            if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.openMenu(sprayer, buffer -> buffer.writeBlockPos(pos));
            }
            return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
        }
        return net.minecraft.world.InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof TCPotionSprayerBlockEntity sprayer) {
            if (!sprayer.getPotionItem().isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sprayer.getPotionItem());
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            boolean powered = level.hasNeighborSignal(pos);
            if (powered == state.getValue(ENABLED)) {
                level.setBlock(pos, state.setValue(ENABLED, !powered), 3);
            }
        }
    }
}
