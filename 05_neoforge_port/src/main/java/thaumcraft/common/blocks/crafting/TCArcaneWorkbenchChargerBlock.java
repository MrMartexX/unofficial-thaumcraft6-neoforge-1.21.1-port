package thaumcraft.common.blocks.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.tiles.crafting.TCArcaneWorkbenchBlockEntity;

public class TCArcaneWorkbenchChargerBlock extends Block {
    public TCArcaneWorkbenchChargerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(TCBlocks.ARCANE_WORKBENCH.get()) || below.is(TCBlocks.WAND_WORKBENCH.get());
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean movedByPiston) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return openBelowWorkbench(level, pos, player)
                ? ItemInteractionResult.sidedSuccess(level.isClientSide)
                : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return openBelowWorkbench(level, pos, player)
                ? InteractionResult.sidedSuccess(level.isClientSide)
                : InteractionResult.PASS;
    }

    private boolean openBelowWorkbench(Level level, BlockPos pos, Player player) {
        BlockPos workbenchPos = pos.below();
        if (!(level.getBlockEntity(workbenchPos) instanceof TCArcaneWorkbenchBlockEntity workbench)) {
            return false;
        }
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(workbench);
        }
        return true;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
