package thaumcraft.common.blocks.world.plants;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.world.trees.TCGreatwoodTreeGenerator;
import thaumcraft.common.world.trees.TCSilverwoodTreeGenerator;

public class TCSaplingBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<TCSaplingBlock> CODEC = simpleCodec(properties ->
            new TCSaplingBlock(Kind.SILVERWOOD, properties)
    );

    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 1);

    private static final VoxelShape SAPLING_SHAPE = Block.box(
            1.6D, 0.0D, 1.6D,
            14.4D, 12.8D, 14.4D
    );

    public enum Kind {
        GREATWOOD,
        SILVERWOOD
    }

    private final Kind kind;

    public TCSaplingBlock(Kind kind, BlockBehaviour.Properties properties) {
        super(properties);
        this.kind = kind;
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    public Kind getKind() {
        return kind;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SAPLING_SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MOSS_BLOCK)
                || state.is(Blocks.FARMLAND);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0) {
            grow(level, pos, state, random);
        }
    }

    private void grow(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            level.setBlock(pos, state.cycle(STAGE), 4);
            return;
        }

        generateTree(level, pos, state, random);
    }

    private void generateTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        int offsetX = 0;
        int offsetZ = 0;
        boolean twoByTwo = false;

        if (kind == Kind.GREATWOOD) {
            search:
            for (offsetX = 0; offsetX >= -1; --offsetX) {
                for (offsetZ = 0; offsetZ >= -1; --offsetZ) {
                    if (isTwoByTwoOfType(level, pos, offsetX, offsetZ, TCBlocks.SAPLING_GREATWOOD.get())) {
                        twoByTwo = true;
                        break search;
                    }
                }
            }

            if (!twoByTwo) {
                return;
            }
        }

        BlockState air = Blocks.AIR.defaultBlockState();
        BlockPos base = pos.offset(offsetX, 0, offsetZ);

        if (twoByTwo) {
            level.setBlock(base, air, 4);
            level.setBlock(base.east(), air, 4);
            level.setBlock(base.south(), air, 4);
            level.setBlock(base.east().south(), air, 4);
        } else {
            level.setBlock(base, air, 4);
        }

        boolean generated;

        if (kind == Kind.GREATWOOD) {
            generated = new TCGreatwoodTreeGenerator(false).generate(level, random, base);
        } else {
            generated = new TCSilverwoodTreeGenerator(true, 7, 4).generate(level, random, base);
        }

        if (!generated) {
            if (twoByTwo) {
                level.setBlock(base, state, 4);
                level.setBlock(base.east(), state, 4);
                level.setBlock(base.south(), state, 4);
                level.setBlock(base.east().south(), state, 4);
            } else {
                level.setBlock(base, state, 4);
            }
        }
    }

    private static boolean isTwoByTwoOfType(ServerLevel level, BlockPos pos, int offsetX, int offsetZ, Block block) {
        return isTypeAt(level, pos.offset(offsetX, 0, offsetZ), block)
                && isTypeAt(level, pos.offset(offsetX + 1, 0, offsetZ), block)
                && isTypeAt(level, pos.offset(offsetX, 0, offsetZ + 1), block)
                && isTypeAt(level, pos.offset(offsetX + 1, 0, offsetZ + 1), block);
    }

    private static boolean isTypeAt(ServerLevel level, BlockPos pos, Block block) {
        return level.getBlockState(pos).is(block);
    }

    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return level.random.nextFloat() < 0.25F;
    }

    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        grow(level, pos, state, random);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }
}