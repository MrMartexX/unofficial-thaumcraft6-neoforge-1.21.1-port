package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.world.ore.TCCrystalBlock;
import thaumcraft.common.registry.TCBlocks;

/**
 * Restores TC6-style vis crystal clusters in natural cave air pockets.
 *
 * Implements:
 * - 33% biome-biased aspect selection (desert/nether -> Ignis, ocean/swamp -> Aqua,
 *   mountain/plains -> Aer, forest/lush -> Terra, snowy -> Ordo, deep dark -> Perditio);
 * - 67% uniform primal aspect distribution;
 * - Omnidirectional rock attachment (floor, ceiling, and all 4 walls);
 * - Natural cluster sizes between 1 and 3.
 */
public final class TCCrystalClusterFeature extends Feature<NoneFeatureConfiguration> {
    private final Supplier<Block> crystalOverride;

    public TCCrystalClusterFeature(Codec<NoneFeatureConfiguration> codec, Supplier<Block> crystal) {
        super(codec);
        this.crystalOverride = crystal;
    }

    public TCCrystalClusterFeature(Codec<NoneFeatureConfiguration> codec) {
        this(codec, null);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        BlockPos seed = findCaveAirNearRock(level, origin, random);
        if (seed == null) {
            return false;
        }

        Block crystalToPlace = determineCrystalBlock(level, seed, random);
        if (crystalToPlace == null) {
            return false;
        }

        int placed = 0;
        int maxCluster = 8 + random.nextInt(9);

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (random.nextInt(3) == 0) {
                        continue;
                    }

                    BlockPos pos = seed.offset(dx, dy, dz);
                    if (!isOpen(level.getBlockState(pos))) {
                        continue;
                    }

                    Direction rockDir = findAdjacentRock(level, pos);
                    if (rockDir == null) {
                        continue;
                    }

                    Direction facing = rockDir.getOpposite();
                    int size = 1 + random.nextInt(3);

                    BlockState state = crystalToPlace.defaultBlockState()
                            .setValue(TCCrystalBlock.FACING, facing)
                            .setValue(TCCrystalBlock.SIZE, size);

                    level.setBlock(pos, state, Block.UPDATE_CLIENTS);
                    placed++;

                    if (placed >= maxCluster) {
                        return true;
                    }
                }
            }
        }

        return placed > 0;
    }

    private Block determineCrystalBlock(WorldGenLevel level, BlockPos pos, RandomSource random) {
        if (this.crystalOverride != null) {
            return this.crystalOverride.get();
        }

        // 1 in 3 chance: derive from biome
        if (random.nextInt(3) == 0) {
            Holder<Biome> biomeHolder = level.getBiome(pos);
            Aspect biomeAspect = getBiomeAspect(biomeHolder);
            if (biomeAspect != null) {
                return getBlockForAspect(biomeAspect);
            }
        }

        // Otherwise uniform random of 6 primal aspects
        Aspect[] primals = { Aspect.AIR, Aspect.FIRE, Aspect.WATER, Aspect.EARTH, Aspect.ORDER, Aspect.ENTROPY };
        return getBlockForAspect(primals[random.nextInt(primals.length)]);
    }

    private static Aspect getBiomeAspect(Holder<Biome> biomeHolder) {
        if (biomeHolder.is(BiomeTags.IS_NETHER) || biomeHolder.is(BiomeTags.IS_BADLANDS) || biomeHolder.is(BiomeTags.HAS_DESERT_PYRAMID)) {
            return Aspect.FIRE;
        }
        if (biomeHolder.is(BiomeTags.IS_OCEAN) || biomeHolder.is(BiomeTags.IS_RIVER) || biomeHolder.is(BiomeTags.IS_BEACH)) {
            return Aspect.WATER;
        }
        if (biomeHolder.is(BiomeTags.IS_MOUNTAIN) || biomeHolder.is(BiomeTags.IS_HILL)) {
            return Aspect.AIR;
        }
        if (biomeHolder.is(BiomeTags.IS_FOREST) || biomeHolder.is(BiomeTags.IS_JUNGLE)) {
            return Aspect.EARTH;
        }
        if (biomeHolder.is(BiomeTags.IS_TAIGA) || biomeHolder.is(BiomeTags.SNOW_GOLEM_MELTS)) {
            return Aspect.ORDER;
        }
        if (biomeHolder.is(BiomeTags.HAS_SWAMP_HUT) || biomeHolder.is(BiomeTags.IS_DEEP_OCEAN)) {
            return Aspect.ENTROPY;
        }
        return null;
    }

    private static Block getBlockForAspect(Aspect aspect) {
        if (aspect == Aspect.AIR) return TCBlocks.CRYSTAL_AER.get();
        if (aspect == Aspect.FIRE) return TCBlocks.CRYSTAL_IGNIS.get();
        if (aspect == Aspect.WATER) return TCBlocks.CRYSTAL_AQUA.get();
        if (aspect == Aspect.EARTH) return TCBlocks.CRYSTAL_TERRA.get();
        if (aspect == Aspect.ORDER) return TCBlocks.CRYSTAL_ORDO.get();
        if (aspect == Aspect.ENTROPY) return TCBlocks.CRYSTAL_PERDITIO.get();
        return TCBlocks.CRYSTAL_VITIUM.get();
    }

    private static BlockPos findCaveAirNearRock(WorldGenLevel level, BlockPos origin, RandomSource random) {
        for (int attempt = 0; attempt < 32; attempt++) {
            BlockPos pos = origin.offset(random.nextInt(9) - 4, random.nextInt(9) - 4, random.nextInt(9) - 4);
            if (isOpen(level.getBlockState(pos)) && findAdjacentRock(level, pos) != null) {
                return pos;
            }
        }
        return null;
    }

    private static Direction findAdjacentRock(WorldGenLevel level, BlockPos pos) {
        // Prefer floor (DOWN) if rock, else check other sides
        if (isRock(level.getBlockState(pos.below()))) {
            return Direction.DOWN;
        }
        for (Direction dir : Direction.values()) {
            if (dir != Direction.DOWN && isRock(level.getBlockState(pos.relative(dir)))) {
                return dir;
            }
        }
        return null;
    }

    private static boolean isOpen(BlockState state) {
        return state.isAir() || state.is(Blocks.CAVE_AIR);
    }

    private static boolean isRock(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(BlockTags.STONE_ORE_REPLACEABLES)
                || state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                || state.is(Blocks.STONE)
                || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.GRANITE)
                || state.is(Blocks.DIORITE)
                || state.is(Blocks.ANDESITE)
                || state.is(Blocks.TUFF)
                || state.is(Blocks.CALCITE);
    }
}
