package thaumcraft.common.blocks.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.ILabelable;
import thaumcraft.common.items.TCEssentiaItemHelper;
import thaumcraft.common.items.TCPhialItem;
import thaumcraft.common.items.TCWardedJarBlockItem;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;

import java.util.List;

public final class TCWardedJarBlock extends Block implements EntityBlock, ILabelable {
    private static final VoxelShape SHAPE = box(3.0D, 0.0D, 3.0D, 13.0D, 14.0D, 13.0D);
    private final TCWardedJarBlockEntity.Kind kind;

    public TCWardedJarBlock(BlockBehaviour.Properties properties, TCWardedJarBlockEntity.Kind kind) {
        super(properties);
        this.kind = kind == null ? TCWardedJarBlockEntity.Kind.NORMAL : kind;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TCBlockEntities.createWardedJarBlockEntity(kind, pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide || type != TCBlockEntities.typeForWardedJar(kind)) {
            return null;
        }
        return (tickerLevel, pos, tickerState, blockEntity) ->
                TCWardedJarBlockEntity.serverTick(
                        tickerLevel,
                        pos,
                        tickerState,
                        (TCWardedJarBlockEntity) blockEntity
                );
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
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos) instanceof TCWardedJarBlockEntity jar)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (TCEssentiaItemHelper.isEmptyPhial(stack)) {
            if (jar.storedAspect() == null || jar.storedAmount() < TCPhialItem.BASE_AMOUNT) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            Aspect aspect = jar.storedAspect();
            if (!level.isClientSide && jar.takeFromContainer(aspect, TCPhialItem.BASE_AMOUNT)) {
                TCEssentiaItemHelper.replaceOneInHand(player, stack, TCEssentiaItemHelper.filledPhial(aspect), pos);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.25F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (TCEssentiaItemHelper.isFilledPhial(stack)) {
            Aspect aspect = TCEssentiaItemHelper.aspectFromStack(stack);
            if (!jar.canAcceptManual(aspect, TCPhialItem.BASE_AMOUNT)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (!level.isClientSide && jar.addToContainer(aspect, TCPhialItem.BASE_AMOUNT) == 0) {
                TCEssentiaItemHelper.replaceOneInHand(player, stack, TCEssentiaItemHelper.emptyPhial(), pos);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.25F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (TCEssentiaItemHelper.isLabel(stack)) {
            if (!level.isClientSide && applyLabel(player, pos, hitResult.getDirection(), stack)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                player.inventoryMenu.broadcastChanges();
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (!player.isShiftKeyDown()
                || !(level.getBlockEntity(pos) instanceof TCWardedJarBlockEntity jar)) {
            return InteractionResult.PASS;
        }

        if (jar.aspectFilter() != null && hitResult.getDirection() == jar.labelFacing()) {
            if (!level.isClientSide) {
                jar.setFilter(null);
                Direction side = hitResult.getDirection();
                level.addFreshEntity(new ItemEntity(
                        level,
                        pos.getX() + 0.5F + side.getStepX() / 3.0F,
                        pos.getY() + 0.5F,
                        pos.getZ() + 0.5F + side.getStepZ() / 3.0F,
                        TCEssentiaItemHelper.blankLabel()
                ));
                level.playSound(null, pos, TCSounds.PAGE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide && jar.storedAmount() > 0) {
            int emptied = jar.storedAmount();
            jar.setStoredForValidation(null, 0);
            AuraHelper.polluteAura(level, pos, emptied, true);
            level.playSound(null, pos, TCSounds.JAR.get(), SoundSource.BLOCKS, 0.4F, 1.0F);
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean applyLabel(Player player, BlockPos pos, Direction side, ItemStack labelStack) {
        if (!(player.level().getBlockEntity(pos) instanceof TCWardedJarBlockEntity jar)
                || jar.aspectFilter() != null) {
            return false;
        }
        Aspect labelAspect = TCEssentiaItemHelper.aspectFromStack(labelStack);
        if (jar.storedAmount() == 0 && labelAspect == null) {
            return false;
        }
        Aspect filter = jar.storedAmount() > 0 ? jar.storedAspect() : labelAspect;
        if (filter == null) {
            return false;
        }
        Direction facing = player.getDirection().getOpposite();
        jar.setFilter(filter, facing);
        player.level().playSound(null, pos, TCSounds.JAR.get(), SoundSource.BLOCKS, 0.4F, 1.0F);
        return true;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof TCWardedJarBlockEntity jar) {
            return List.of(TCWardedJarBlockItem.stackFromJar(jar));
        }
        return super.getDrops(state, params);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof TCWardedJarBlockEntity jar ? jar.comparatorSignal() : 0;
    }
}
