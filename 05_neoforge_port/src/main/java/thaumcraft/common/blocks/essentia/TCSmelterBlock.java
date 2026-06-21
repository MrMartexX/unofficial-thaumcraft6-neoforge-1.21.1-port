package thaumcraft.common.blocks.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import thaumcraft.common.tiles.essentia.TCSmelterBlockEntity;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TCSmelterBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");

    public TCSmelterBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ENABLED, Boolean.FALSE));
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCSmelterBlockEntity(pos, state);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(ENABLED, Boolean.FALSE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ENABLED);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(ENABLED)) {
            return;
        }

        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.2D + random.nextDouble() * 5.0D / 16.0D;
        double z = pos.getZ() + 0.5D;
        double frontOffset = 0.52D;
        double sideOffset = random.nextDouble() * 0.5D - 0.25D;

        switch (state.getValue(FACING)) {
            case WEST -> spawnFrontParticles(level, x - frontOffset, y, z + sideOffset);
            case EAST -> spawnFrontParticles(level, x + frontOffset, y, z + sideOffset);
            case NORTH -> spawnFrontParticles(level, x + sideOffset, y, z - frontOffset);
            case SOUTH -> spawnFrontParticles(level, x + sideOffset, y, z + frontOffset);
            default -> {
            }
        }
    }

    private static void spawnFrontParticles(Level level, double x, double y, double z) {
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
    }
}

