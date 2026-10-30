package thaumcraft.common.blocks.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.tiles.essentia.TCCentrifugeBlockEntity;

/** TC6 Essentia Centrifuge block boundary. */
public final class TCCentrifugeBlock extends Block implements EntityBlock {

    public TCCentrifugeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCCentrifugeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != TCBlockEntities.CENTRIFUGE.get()) {
            return null;
        }
        if (level.isClientSide) {
            return (tickerLevel, pos, tickerState, blockEntity) ->
                    TCCentrifugeBlockEntity.clientTick(
                            tickerLevel,
                            pos,
                            tickerState,
                            (TCCentrifugeBlockEntity) blockEntity
                    );
        } else {
            return (tickerLevel, pos, tickerState, blockEntity) ->
                    TCCentrifugeBlockEntity.serverTick(
                            tickerLevel,
                            pos,
                            tickerState,
                            (TCCentrifugeBlockEntity) blockEntity
                    );
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return InteractionResult.PASS;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL; // Usually handled via custom BER or block model
    }
}
