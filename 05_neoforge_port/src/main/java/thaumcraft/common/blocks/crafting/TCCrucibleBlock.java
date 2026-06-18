package thaumcraft.common.blocks.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.tiles.crafting.TCCrucibleBlockEntity;

public class TCCrucibleBlock extends Block implements EntityBlock {
    private int livingContactDelay;

    private static final VoxelShape SHAPE = Shapes.or(
            box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
            box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D),
            box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D),
            box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D)
    );

    public TCCrucibleBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCCrucibleBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != TCBlockEntities.CRUCIBLE.get()) {
            return null;
        }
        return (tickerLevel, pos, tickerState, blockEntity) ->
                TCCrucibleBlockEntity.serverTick(tickerLevel, pos, tickerState, (TCCrucibleBlockEntity) blockEntity);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof TCCrucibleBlockEntity crucible) {
            if (entity instanceof ItemEntity itemEntity) {
                crucible.absorbItemEntity(itemEntity);
            } else {
                livingContactDelay++;
                if (livingContactDelay >= 10) {
                    livingContactDelay = 0;
                    if (entity instanceof LivingEntity && crucible.isBoiling()) {
                        entity.hurt(level.damageSources().inFire(), 1.0F);
                        level.playSound(
                                null,
                                pos.getX() + 0.5D,
                                pos.getY() + 0.5D,
                                pos.getZ() + 0.5D,
                                SoundEvents.LAVA_EXTINGUISH,
                                SoundSource.BLOCKS,
                                0.4F,
                                2.0F + level.random.nextFloat() * 0.4F
                        );
                    }
                }
            }
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof TCCrucibleBlockEntity crucible)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.is(Items.WATER_BUCKET)) {
            if (!level.isClientSide && crucible.fillWaterFromBucket()) {
                consumeWaterBucket(player, hand, stack);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.33F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (player.isShiftKeyDown() || hitResult.getDirection() != Direction.UP || !(player instanceof ServerPlayer serverPlayer)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        TCCrucibleBlockEntity.CrucibleUseResult result = crucible.useCatalystOrDissolve(serverPlayer, stack);
        if (result == TCCrucibleBlockEntity.CrucibleUseResult.IGNORED
                || result == TCCrucibleBlockEntity.CrucibleUseResult.NOT_BOILING
                || result == TCCrucibleBlockEntity.CrucibleUseResult.NO_ASPECTS) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (result.consumesCatalyst() && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.isShiftKeyDown() || !(level.getBlockEntity(pos) instanceof TCCrucibleBlockEntity crucible)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            crucible.spillRemnants();
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof TCCrucibleBlockEntity crucible) {
            crucible.spillRemnants();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof TCCrucibleBlockEntity crucible) {
            return crucible.comparatorSignal();
        }
        return 0;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    private static void consumeWaterBucket(Player player, InteractionHand hand, ItemStack stack) {
        if (player.getAbilities().instabuild) {
            return;
        }

        stack.shrink(1);
        ItemStack bucket = new ItemStack(Items.BUCKET);
        if (stack.isEmpty()) {
            player.setItemInHand(hand, bucket);
        } else if (!player.getInventory().add(bucket)) {
            player.drop(bucket, false);
        }
    }
}
