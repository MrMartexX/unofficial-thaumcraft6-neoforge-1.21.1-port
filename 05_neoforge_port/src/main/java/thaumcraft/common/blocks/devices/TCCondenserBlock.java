package thaumcraft.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.tiles.devices.TCCondenserBlockEntity;

/**
 * TC6 Flux Condenser block.
 * Base mechanism of the flux filtration array. Draws essentia from sides to scrub
 * flux from the aura chunk into concentrated flux crystals/sludge with the help of
 * condenser lattice blocks built above it.
 */
public class TCCondenserBlock extends Block implements EntityBlock {
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public TCCondenserBlock() {
        this(BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .strength(2.0F, 10.0F)
                .noOcclusion());
    }

    public TCCondenserBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ENABLED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ENABLED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCCondenserBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != TCBlockEntities.CONDENSER.get()) {
            return null;
        }
        return (tickerLevel, pos, tickerState, blockEntity) ->
                TCCondenserBlockEntity.serverTick(tickerLevel, pos, tickerState, (TCCondenserBlockEntity) blockEntity);
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
