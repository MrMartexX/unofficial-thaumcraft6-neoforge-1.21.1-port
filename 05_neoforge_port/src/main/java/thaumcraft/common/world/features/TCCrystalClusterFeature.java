package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Restores TC6-style vis crystal clusters in natural cave air pockets. */
public final class TCCrystalClusterFeature extends Feature<NoneFeatureConfiguration> {
    private final Supplier<Block> crystal;

    public TCCrystalClusterFeature(Codec<NoneFeatureConfiguration> codec, Supplier<Block> crystal) {
        super(codec);
        this.crystal = crystal;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos seed = findCaveFloor(level, context.origin(), random);
        if (seed == null) {
            return false;
        }

        int placed = 0;
        int attempts = 10 + random.nextInt(9);
        for (int attempt = 0; attempt < attempts; attempt++) {
            BlockPos pos = seed.offset(random.nextInt(5) - 2, random.nextInt(3) - 1, random.nextInt(5) - 2);
            if (!isOpen(level.getBlockState(pos)) || !isRock(level.getBlockState(pos.below()))) {
                continue;
            }
            level.setBlock(pos, crystal.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            placed++;
        }
        return placed > 0;
    }

    private static BlockPos findCaveFloor(WorldGenLevel level, BlockPos origin, RandomSource random) {
        for (int attempt = 0; attempt < 32; attempt++) {
            BlockPos pos = origin.offset(random.nextInt(9) - 4, random.nextInt(9) - 4, random.nextInt(9) - 4);
            if (isOpen(level.getBlockState(pos)) && isRock(level.getBlockState(pos.below()))) {
                return pos;
            }
        }
        return null;
    }

    private static boolean isOpen(BlockState state) {
        return state.isAir() || state.is(Blocks.CAVE_AIR);
    }

    private static boolean isRock(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(Blocks.STONE)
                || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.GRANITE)
                || state.is(Blocks.DIORITE)
                || state.is(Blocks.ANDESITE)
                || state.is(Blocks.TUFF)
                || state.is(Blocks.CALCITE);
    }
}
