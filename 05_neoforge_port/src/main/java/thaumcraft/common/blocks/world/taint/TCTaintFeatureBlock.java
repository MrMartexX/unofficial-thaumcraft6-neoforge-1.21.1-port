package thaumcraft.common.blocks.world.taint;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.entities.TCTaintCrawlerEntity;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCEntityTypes;

/** Swollen taint orb/feature growth with the TC6 break-spawn crawler hook. */
public final class TCTaintFeatureBlock extends DirectionalBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final MapCodec<TCTaintFeatureBlock> CODEC = simpleCodec(TCTaintFeatureBlock::new);
    private static final VoxelShape DOWN_SHAPE = box(2.0D, 10.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final VoxelShape UP_SHAPE = box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    private static final VoxelShape NORTH_SHAPE = box(2.0D, 2.0D, 10.0D, 14.0D, 14.0D, 16.0D);
    private static final VoxelShape SOUTH_SHAPE = box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 6.0D);
    private static final VoxelShape WEST_SHAPE = box(10.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
    private static final VoxelShape EAST_SHAPE = box(0.0D, 2.0D, 2.0D, 6.0D, 14.0D, 14.0D);

    public TCTaintFeatureBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeForFacing(state.getValue(FACING));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!TCTaintHelper.isNearTaintSeed(level, pos) && random.nextInt(10) == 0) {
            die(level, pos);
            return;
        }
        TCTaintHelper.spreadFibres(level, pos);
        if (level.getBlockState(pos.below()).is(TCBlocks.TAINT_LOG.get())
                && level.getBlockState(pos.below()).getValue(TCTaintLogBlock.AXIS) == Direction.Axis.Y
                && random.nextInt(100) == 0) {
            level.setBlock(pos, TCBlocks.TAINT_GEYSER.get().defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(newState.getBlock())) {
            if (level.getRandom().nextFloat() < 0.333F) {
                spawnCrawler(level, pos);
            } else {
                AuraHelper.polluteAura(level, pos, 1.0F, true);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public static VoxelShape shapeForFacing(Direction facing) {
        return switch (facing) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    public static void die(Level level, BlockPos pos) {
        level.setBlock(pos, TCBlocks.FLUX_GOO.get().defaultBlockState(), Block.UPDATE_ALL);
    }

    public static boolean spawnCrawlerForValidation(Level level, BlockPos pos) {
        return spawnCrawler(level, pos);
    }

    private static boolean spawnCrawler(Level level, BlockPos pos) {
        Entity crawler = TCEntityTypes.TAINT_CRAWLER.get().create(level);
        if (!(crawler instanceof TCTaintCrawlerEntity)) {
            return false;
        }
        crawler.moveTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, level.getRandom().nextInt(360), 0.0F);
        level.addFreshEntity(crawler);
        return true;
    }
}
