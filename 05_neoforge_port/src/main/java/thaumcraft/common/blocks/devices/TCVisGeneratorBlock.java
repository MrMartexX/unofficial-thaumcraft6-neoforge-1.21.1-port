package thaumcraft.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.tiles.devices.TCVisGeneratorBlockEntity;

/**
 * TC6 Vis Generator block.
 * Directional energy producer. Draws 1.0F Vis from chunk aura to produce 1000 FE,
 * pushing up to 20 FE/t to an adjacent block capability on its facing side.
 */
public class TCVisGeneratorBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final VoxelShape SHAPE_UP = Block.box(4, 0, 4, 12, 14, 12);
    private static final VoxelShape SHAPE_DOWN = Block.box(4, 2, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_NORTH = Block.box(4, 4, 2, 12, 12, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(4, 4, 0, 12, 12, 14);
    private static final VoxelShape SHAPE_WEST = Block.box(2, 4, 4, 16, 12, 12);
    private static final VoxelShape SHAPE_EAST = Block.box(0, 4, 4, 14, 12, 12);

    public TCVisGeneratorBlock() {
        this(BlockBehaviour.Properties.of()
                .sound(SoundType.WOOD)
                .strength(3.0F)
                .noOcclusion());
    }

    public TCVisGeneratorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> SHAPE_DOWN;
            case UP -> SHAPE_UP;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        // Check surrounding blocks to see if one can receive FE
        for (Direction dir : Direction.values()) {
            BlockPos targetPos = pos.relative(dir);
            var targetEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, dir.getOpposite());
            if (targetEnergy != null && targetEnergy.canReceive()) {
                return this.defaultBlockState().setValue(FACING, dir);
            }
        }
        return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCVisGeneratorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != TCBlockEntities.VIS_GENERATOR.get()) {
            return null;
        }
        return (tickerLevel, pos, tickerState, blockEntity) ->
                TCVisGeneratorBlockEntity.serverTick(tickerLevel, pos, tickerState, (TCVisGeneratorBlockEntity) blockEntity);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        if (level.getBlockEntity(pos) instanceof TCVisGeneratorBlockEntity gen && gen.getEnergy() > 0) {
            Direction face = state.getValue(FACING);
            double x = (face.getStepX() == 0) ? (rand.nextGaussian() * 0.1) : (face.getStepX() * 0.1);
            double y = (face.getStepY() == 0) ? (rand.nextGaussian() * 0.1) : (face.getStepY() * 0.1);
            double z = (face.getStepZ() == 0) ? (rand.nextGaussian() * 0.1) : (face.getStepZ() * 0.1);
            level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                    pos.getX() + 0.5 + x,
                    pos.getY() + 0.5 + y,
                    pos.getZ() + 0.5 + z,
                    (rand.nextFloat() - 0.5) * 0.05,
                    (rand.nextFloat() - 0.5) * 0.05,
                    (rand.nextFloat() - 0.5) * 0.05);
        }
    }
}
