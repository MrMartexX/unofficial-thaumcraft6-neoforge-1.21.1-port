package thaumcraft.common.blocks.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.crafting.infusion.TCInfusionStartResult;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;

public class TCInfusionMatrixBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
            box(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D),
            box(0.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D),
            box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D),
            box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 16.0D)
    );

    public TCInfusionMatrixBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static boolean isPlayerFacingCompletionEnabled() {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCInfusionMatrixBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide || type != TCBlockEntities.INFUSION_MATRIX.get()) {
            return null;
        }
        return (tickerLevel, tickerPos, tickerState, blockEntity) ->
                TCInfusionMatrixBlockEntity.serverTick(
                        tickerLevel,
                        tickerPos,
                        tickerState,
                        (TCInfusionMatrixBlockEntity) blockEntity
                );
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.is(TCItems.CASTER_BASIC.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return validateWithCaster(level, pos, player)
                ? ItemInteractionResult.sidedSuccess(level.isClientSide)
                : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return InteractionResult.PASS;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    private static boolean validateWithCaster(Level level, BlockPos pos, Player player) {
        if (!(level.getBlockEntity(pos) instanceof TCInfusionMatrixBlockEntity matrix)) {
            return false;
        }
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (!matrix.isActive()) {
                boolean activated = matrix.activate();
                if (activated) {
                    level.playSound(null, pos, TCSounds.CRAFTSTART.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                serverPlayer.displayClientMessage(Component.literal(
                        activated
                                ? "Infusion matrix: activated"
                                : "Infusion matrix: " + matrix.lastValidationReason()
                ), true);
                return true;
            }
            if (!matrix.isCrafting()) {
                TCInfusionStartResult start = matrix.tryStartCrafting(serverPlayer, new AspectList());
                serverPlayer.displayClientMessage(Component.literal(
                        start.started()
                                ? "Infusion matrix: started " + start.recipeId()
                                : "Infusion matrix: " + start.reason()
                ), true);
                return true;
            }
            serverPlayer.displayClientMessage(Component.literal(
                    "Infusion matrix: " + matrix.lastCycleReason()
                            + ", pedestals=" + matrix.findSurroundingPedestals().size()
                            + ", components=" + matrix.createSnapshot(new AspectList()).componentCount()
                            + ", active=true"
            ), true);
        }
        return true;
    }
}
