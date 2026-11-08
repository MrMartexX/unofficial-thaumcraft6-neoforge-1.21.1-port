package thaumcraft.common.world.features;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

/**
 * Legacy decorator anchor sampling; counts, patch sizes and predicates remain datapack-owned.
 * For FLOWER patches, selects one flower species per patch at the patch origin using the exact
 * 1.12.2 2D GRASS_COLOR_NOISE (seed 2345L, 1 octave, scale 1/48) over the 10 legacy EnumFlowerType values.
 */
public final class TCLegacyPatchFeature extends RandomPatchFeature {
    public enum Anchor { FLOWER, GRASS, WATERLILY, VISHROOM, CINDERPEARL, RANDOM_HEIGHT }

    private static final PerlinSimplexNoise LEGACY_GRASS_COLOR_NOISE =
            new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(2345L)), List.of(0));

    private static final BlockState[] LEGACY_ENUM_FLOWER_TYPES = {
            Blocks.DANDELION.defaultBlockState(),
            Blocks.POPPY.defaultBlockState(),
            Blocks.BLUE_ORCHID.defaultBlockState(),
            Blocks.ALLIUM.defaultBlockState(),
            Blocks.AZURE_BLUET.defaultBlockState(),
            Blocks.RED_TULIP.defaultBlockState(),
            Blocks.ORANGE_TULIP.defaultBlockState(),
            Blocks.WHITE_TULIP.defaultBlockState(),
            Blocks.PINK_TULIP.defaultBlockState(),
            Blocks.OXEYE_DAISY.defaultBlockState()
    };

    private final Anchor anchor;

    public TCLegacyPatchFeature(Anchor anchor) {
        super(RandomPatchConfiguration.CODEC);
        this.anchor = anchor;
    }

    public static BlockState pickLegacyMagicalForestFlower(BlockPos patchOrigin) {
        double d0 = Mth.clamp((1.0D + LEGACY_GRASS_COLOR_NOISE.getValue(
                patchOrigin.getX() / 48.0D, patchOrigin.getZ() / 48.0D, false)) / 2.0D, 0.0D, 0.9999D);
        return LEGACY_ENUM_FLOWER_TYPES[(int) (d0 * LEGACY_ENUM_FLOWER_TYPES.length)];
    }

    public static int legacyFlowerTypeCount() {
        return LEGACY_ENUM_FLOWER_TYPES.length;
    }

    @Override
    public boolean place(FeaturePlaceContext<RandomPatchConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        switch (anchor) {
            case FLOWER -> {
                pos = pos.atY(context.random().nextInt(Math.max(1, pos.getY() + 32)));
                return placeFlowerPatch(context, pos);
            }
            case GRASS, WATERLILY, RANDOM_HEIGHT -> {
                pos = pos.atY(context.random().nextInt(Math.max(1, pos.getY() * 2)));
                if (anchor == Anchor.GRASS) {
                    while (pos.getY() > 0 && (level.isEmptyBlock(pos) || level.getBlockState(pos).is(BlockTags.LEAVES)))
                        pos = pos.below();
                } else if (anchor == Anchor.WATERLILY) {
                    while (pos.getY() > 0 && level.isEmptyBlock(pos.below())) pos = pos.below();
                }
            }
            case VISHROOM -> {
                pos = pos.below();
                while (pos.getY() > 50 && !level.getBlockState(pos).is(Blocks.GRASS_BLOCK)) pos = pos.below();
                if (!level.getBlockState(pos).is(Blocks.GRASS_BLOCK)) return false;
                pos = pos.above();
            }
            case CINDERPEARL -> {
                if (!level.getBlockState(pos.below()).is(BlockTags.SAND)
                        || level.getBiome(pos).value().getBaseTemperature() <= 1.0F) return false;
            }
        }
        return super.place(new FeaturePlaceContext<>(context.topFeature(), level, context.chunkGenerator(),
                context.random(), pos, context.config()));
    }

    private boolean placeFlowerPatch(FeaturePlaceContext<RandomPatchConfiguration> context, BlockPos patchOrigin) {
        RandomPatchConfiguration config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        PlacedFeature placedFeature = config.feature().value();

        // Sample flower species for the entire patch at the patch origin from the configured feature's state provider
        BlockState patchFlower;
        if (placedFeature.feature().value().config() instanceof SimpleBlockConfiguration simpleConfig) {
            patchFlower = simpleConfig.toPlace().getState(random, patchOrigin);
        } else {
            // If datapack customized the feature to something other than simple_block, fall back to standard placement
            return super.place(new FeaturePlaceContext<>(context.topFeature(), level, context.chunkGenerator(),
                    random, patchOrigin, config));
        }

        ConfiguredFeature<?, ?> monoConfigured = new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(patchFlower))
        );
        PlacedFeature monoPlaced = new PlacedFeature(
                Holder.direct(monoConfigured),
                placedFeature.placement()
        );

        int placed = 0;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int xzSpread = config.xzSpread() + 1;
        int ySpread = config.ySpread() + 1;
        for (int i = 0; i < config.tries(); i++) {
            mutable.setWithOffset(patchOrigin,
                    random.nextInt(xzSpread) - random.nextInt(xzSpread),
                    random.nextInt(ySpread) - random.nextInt(ySpread),
                    random.nextInt(xzSpread) - random.nextInt(xzSpread)
            );
            if (monoPlaced.place(level, context.chunkGenerator(), random, mutable)) {
                placed++;
            }
        }
        return placed > 0;
    }
}
