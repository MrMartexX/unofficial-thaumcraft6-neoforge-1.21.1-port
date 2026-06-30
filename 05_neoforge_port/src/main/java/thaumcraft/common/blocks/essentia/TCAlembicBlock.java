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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.ILabelable;
import thaumcraft.common.items.TCEssentiaItemHelper;
import thaumcraft.common.items.TCPhialItem;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.tiles.essentia.TCAlembicBlockEntity;
import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;

/** TC6 Arcane Alembic block boundary. */
public final class TCAlembicBlock extends Block implements EntityBlock, ILabelable {
    private static final VoxelShape SHAPE = box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public TCAlembicBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCAlembicBlockEntity(pos, state);
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
        if (!(level.getBlockEntity(pos) instanceof TCAlembicBlockEntity alembic)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (TCEssentiaItemHelper.isEmptyPhial(stack)) {
            if (alembic.storedAspect() == null || alembic.storedAmount() < TCPhialItem.BASE_AMOUNT) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            Aspect aspect = alembic.storedAspect();
            if (!level.isClientSide && alembic.takeFromContainer(aspect, TCPhialItem.BASE_AMOUNT)) {
                TCEssentiaItemHelper.replaceOneInHand(player, stack, TCEssentiaItemHelper.filledPhial(aspect), pos);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.25F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (stack.is(TCItems.JAR_NORMAL.get())) {
            if (alembic.storedAspect() == null || alembic.storedAmount() <= 0) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            Aspect existing = TCEssentiaItemHelper.aspectFromStack(stack);
            Aspect filter = TCEssentiaItemHelper.filterAspect(stack);
            if (filter != null && filter != alembic.storedAspect()
                    || existing != null && existing != alembic.storedAspect()) {
                return ItemInteractionResult.FAIL;
            }
            int base = Math.max(0, TCEssentiaItemHelper.aspectAmount(stack));
            Aspect movedAspect = alembic.storedAspect();
            int moved = Math.min(alembic.storedAmount(), TCWardedJarBlockEntity.CAPACITY - base);
            if (moved <= 0) {
                return ItemInteractionResult.FAIL;
            }
            if (!level.isClientSide && alembic.takeFromContainer(movedAspect, moved)) {
                if (stack.getCount() > 1 && !player.getAbilities().instabuild) {
                    ItemStack filled = stack.copyWithCount(1);
                    TCEssentiaItemHelper.setAspect(filled, movedAspect, base + moved);
                    stack.shrink(1);
                    if (!player.getInventory().add(filled)) {
                        level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), filled));
                    }
                } else {
                    TCEssentiaItemHelper.setAspect(stack, movedAspect, base + moved);
                }
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.25F, 1.0F);
                player.inventoryMenu.broadcastChanges();
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
                || !(level.getBlockEntity(pos) instanceof TCAlembicBlockEntity alembic)) {
            return InteractionResult.PASS;
        }
        if (alembic.aspectFilter() != null && hitResult.getDirection() == alembic.labelFacing()) {
            if (!level.isClientSide) {
                Direction side = hitResult.getDirection();
                alembic.setFilter(null, Direction.DOWN);
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
        if (!level.isClientSide) {
            int emptied = alembic.emptyIntoAura();
            if (emptied > 0) {
                AuraHelper.polluteAura(level, pos, emptied, true);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.5F, 1.0F);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean applyLabel(Player player, BlockPos pos, Direction side, ItemStack labelStack) {
        if (side == null || side.getAxis().isVertical()
                || !(player.level().getBlockEntity(pos) instanceof TCAlembicBlockEntity alembic)
                || alembic.aspectFilter() != null) {
            return false;
        }
        Aspect labelAspect = TCEssentiaItemHelper.aspectFromStack(labelStack);
        if (alembic.storedAmount() == 0 && labelAspect == null) {
            return false;
        }
        Aspect filter = alembic.storedAmount() > 0 ? alembic.storedAspect() : labelAspect;
        if (filter == null) {
            return false;
        }
        alembic.setFilter(filter, side);
        player.level().playSound(null, pos, TCSounds.PAGE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof TCAlembicBlockEntity alembic) {
            return alembic.comparatorOutput();
        }
        return 0;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }
}
