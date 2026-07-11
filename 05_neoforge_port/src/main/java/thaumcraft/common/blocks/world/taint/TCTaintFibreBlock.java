package thaumcraft.common.blocks.world.taint;

import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMobEffects;

/** Legacy TC6 taint fibre surface/growth block. */
public final class TCTaintFibreBlock extends Block {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty GROWTH1 = BooleanProperty.create("growth1");
    public static final BooleanProperty GROWTH2 = BooleanProperty.create("growth2");
    public static final BooleanProperty GROWTH3 = BooleanProperty.create("growth3");
    public static final BooleanProperty GROWTH4 = BooleanProperty.create("growth4");
    public static final MapCodec<TCTaintFibreBlock> CODEC = simpleCodec(TCTaintFibreBlock::new);

    private static final VoxelShape FACE_UP = box(0.0D, 15.2D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape FACE_DOWN = box(0.0D, 0.0D, 0.0D, 16.0D, 0.8D, 16.0D);
    private static final VoxelShape FACE_EAST = box(15.2D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape FACE_WEST = box(0.0D, 0.0D, 0.0D, 0.8D, 16.0D, 16.0D);
    private static final VoxelShape FACE_SOUTH = box(0.0D, 0.0D, 15.2D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape FACE_NORTH = box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 0.8D);
    private static final VoxelShape GROWTH_SHAPE_1 = box(1.6D, 0.0D, 1.6D, 14.4D, 6.4D, 14.4D);
    private static final VoxelShape GROWTH_SHAPE_2 = box(3.2D, 0.0D, 3.2D, 12.8D, 16.0D, 12.8D);
    private static final VoxelShape GROWTH_SHAPE_3 = box(4.0D, 0.0D, 4.0D, 12.0D, 5.0D, 12.0D);
    private static final VoxelShape GROWTH_SHAPE_4 = box(1.6D, 4.8D, 1.6D, 14.4D, 16.0D, 14.4D);

    public TCTaintFibreBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(clearState(stateDefinition.any()));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return stateForWorld(context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        return stateForWorld(level, pos);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState actual = stateForWorld(level, pos);
        if (hasNoGrowth(actual) && isOnlyAdjacentToTaint(level, pos)) {
            die(level, pos);
        } else if (!isNearTaintSeed(level, pos)) {
            die(level, pos);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide
                && entity instanceof LivingEntity living
                && level.getRandom().nextInt(750) == 0) {
            applyWalkTaint(living);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return true;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeForState(level instanceof LevelReader ? stateForWorld((LevelReader) level, pos) : state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeForState(level instanceof LevelReader ? stateForWorld((LevelReader) level, pos) : state);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        BlockState actual = stateForWorld(level, pos);
        if (actual.getValue(GROWTH3) && level.getRandom().nextInt(5) == 0) {
            popResource(level, pos, new ItemStack(TCItems.CRYSTAL_ESSENCE_VITIUM.get()));
            AuraHelper.polluteAura(level, pos, 1.0F, true);
        }
        super.spawnAfterBreak(state, level, pos, tool, dropExperience);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, GROWTH1, GROWTH2, GROWTH3, GROWTH4);
    }

    public BlockState stateForWorld(LevelReader level, BlockPos pos) {
        BlockState state = clearState(defaultBlockState())
                .setValue(DOWN, drawAt(level, pos.below(), Direction.DOWN))
                .setValue(UP, drawAt(level, pos.above(), Direction.UP))
                .setValue(NORTH, drawAt(level, pos.north(), Direction.NORTH))
                .setValue(SOUTH, drawAt(level, pos.south(), Direction.SOUTH))
                .setValue(WEST, drawAt(level, pos.west(), Direction.WEST))
                .setValue(EAST, drawAt(level, pos.east(), Direction.EAST));

        int growth = legacyGrowth(level, pos);
        return state
                .setValue(GROWTH1, growth == 1)
                .setValue(GROWTH2, growth == 2)
                .setValue(GROWTH3, growth == 3)
                .setValue(GROWTH4, growth == 4);
    }

    public static boolean applyWalkTaintForValidation(LivingEntity living) {
        return applyWalkTaint(living);
    }

    public static boolean applyWalkTaint(LivingEntity living) {
        if (living.getType().is(EntityTypeTags.UNDEAD)) {
            return false;
        }
        MobEffectInstance effect = new MobEffectInstance(TCMobEffects.FLUX_TAINT, 200, 0, false, true);
        effect.getCures().clear();
        living.addEffect(effect);
        return true;
    }

    public static VoxelShape shapeForState(BlockState state) {
        VoxelShape shape = Shapes.empty();
        if (state.getValue(UP)) {
            shape = Shapes.joinUnoptimized(shape, FACE_UP, BooleanOp.OR);
        }
        if (state.getValue(DOWN)) {
            shape = Shapes.joinUnoptimized(shape, FACE_DOWN, BooleanOp.OR);
        }
        if (state.getValue(EAST)) {
            shape = Shapes.joinUnoptimized(shape, FACE_EAST, BooleanOp.OR);
        }
        if (state.getValue(WEST)) {
            shape = Shapes.joinUnoptimized(shape, FACE_WEST, BooleanOp.OR);
        }
        if (state.getValue(SOUTH)) {
            shape = Shapes.joinUnoptimized(shape, FACE_SOUTH, BooleanOp.OR);
        }
        if (state.getValue(NORTH)) {
            shape = Shapes.joinUnoptimized(shape, FACE_NORTH, BooleanOp.OR);
        }
        if (state.getValue(GROWTH1)) {
            shape = Shapes.joinUnoptimized(shape, GROWTH_SHAPE_1, BooleanOp.OR);
        } else if (state.getValue(GROWTH2)) {
            shape = Shapes.joinUnoptimized(shape, GROWTH_SHAPE_2, BooleanOp.OR);
        } else if (state.getValue(GROWTH3)) {
            shape = Shapes.joinUnoptimized(shape, GROWTH_SHAPE_3, BooleanOp.OR);
        } else if (state.getValue(GROWTH4)) {
            shape = Shapes.joinUnoptimized(shape, GROWTH_SHAPE_4, BooleanOp.OR);
        }
        return shape.optimize();
    }

    public static int lightForState(BlockState state) {
        if (state.hasProperty(GROWTH3) && state.getValue(GROWTH3)) {
            return 12;
        }
        if ((state.hasProperty(GROWTH2) && state.getValue(GROWTH2))
                || (state.hasProperty(GROWTH4) && state.getValue(GROWTH4))) {
            return 6;
        }
        return 0;
    }

    public static boolean isOnlyAdjacentToTaint(LevelReader level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighbor = level.getBlockState(neighborPos);
            if (!neighbor.isAir()
                    && !isTaintState(neighbor)
                    && neighbor.isFaceSturdy(level, neighborPos, direction.getOpposite())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isHemmedByTaint(LevelReader level, BlockPos pos) {
        int count = 0;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighbor = level.getBlockState(neighborPos);
            if (isTaintState(neighbor)) {
                count++;
            } else if (neighbor.isAir()) {
                count--;
            } else if (neighbor.getFluidState().isEmpty()
                    && !neighbor.isFaceSturdy(level, neighborPos, direction.getOpposite())) {
                count--;
            }
        }
        return count > 0;
    }

    public static boolean isNearTaintSeed(LevelReader level, BlockPos pos) {
        return TCTaintHelper.isNearTaintSeed(level, pos);
    }

    private static int legacyGrowth(LevelReader level, BlockPos pos) {
        boolean down = drawAt(level, pos.below(), Direction.DOWN);
        boolean up = drawAt(level, pos.above(), Direction.UP);
        int q = new Random(pos.asLong()).nextInt(50);
        int growth = 0;
        if (down) {
            if (q < 4) {
                growth = 1;
            } else if (q == 4 || q == 5) {
                growth = 2;
            } else if (q == 6) {
                growth = 3;
            }
        }
        if (up && q > 47) {
            growth = 4;
        }
        return growth;
    }

    private static boolean drawAt(LevelReader level, BlockPos pos, Direction side) {
        BlockState state = level.getBlockState(pos);
        return !state.is(TCBlocks.TAINT_FIBRE.get())
                && state.isFaceSturdy(level, pos, side.getOpposite());
    }

    private static boolean isTaintState(BlockState state) {
        Block block = state.getBlock();
        return TCTaintHelper.isTaintState(state);
    }

    private static boolean hasNoGrowth(BlockState state) {
        return !state.getValue(GROWTH1)
                && !state.getValue(GROWTH2)
                && !state.getValue(GROWTH3)
                && !state.getValue(GROWTH4);
    }

    private static void die(Level level, BlockPos pos) {
        level.removeBlock(pos, false);
    }

    private static BlockState clearState(BlockState state) {
        return state
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(GROWTH1, false)
                .setValue(GROWTH2, false)
                .setValue(GROWTH3, false)
                .setValue(GROWTH4, false);
    }
}
