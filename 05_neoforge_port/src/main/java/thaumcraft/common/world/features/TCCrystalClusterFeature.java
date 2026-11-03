package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.world.ore.TCCrystalBlock;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCBiomes;

/** Legacy attempt budget and 3x3x3 sampling; modern deepslate and biome-category adapters. */
public final class TCCrystalClusterFeature extends Feature<NoneFeatureConfiguration> {
    private static final Aspect[] PRIMALS = {Aspect.AIR, Aspect.FIRE, Aspect.WATER, Aspect.EARTH, Aspect.ORDER, Aspect.ENTROPY};
    private final Supplier<Block> crystalOverride;

    public TCCrystalClusterFeature(Codec<NoneFeatureConfiguration> codec, Supplier<Block> crystal) {
        super(codec);
        this.crystalOverride = crystal;
    }

    public TCCrystalClusterFeature(Codec<NoneFeatureConfiguration> codec) { this(codec, null); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        int amount = 0;
        for (int n = 0; n < (crystalOverride == null ? 8 : 1); n++) {
            amount += placeAttempt(context);
            // Legacy tests the cumulative size budget BETWEEN complete attempts, not inside a cluster.
            if (amount > 64) break;
        }
        return amount > 0;
    }

    private int placeAttempt(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int x = (origin.getX() & ~15) + 2 + random.nextInt(13);
        int z = (origin.getZ() & ~15) + 2 + random.nextInt(13);
        int minY = level.getMinBuildHeight();
        int maxY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 5;
        if (maxY <= minY) return 0;
        BlockPos seed = new BlockPos(x, minY + random.nextInt(maxY - minY), z);
        Aspect aspect = PRIMALS[random.nextInt(PRIMALS.length)];
        if (random.nextInt(3) == 0) {
            Aspect biased = getBiomeAspect(level.getBiome(seed), random);
            aspect = biased == null ? PRIMALS[random.nextInt(PRIMALS.length)] : biased;
        }
        Block crystal = crystalOverride == null ? getBlockForAspect(aspect) : crystalOverride.get();
        int amount = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (random.nextInt(3) == 0) continue;
                    BlockPos pos = seed.offset(dx, dy, dz);
                    if (level.isOutsideBuildHeight(pos)) continue;
                    BlockState old = level.getBlockState(pos);
                    if (!(old.isAir() || old.canBeReplaced()) || !old.getFluidState().isEmpty()) continue;
                    Direction rock = findAdjacentRock(level, pos);
                    if (rock == null) continue;
                    int size = 1 + random.nextInt(3);
                    BlockState state = crystal.defaultBlockState().setValue(TCCrystalBlock.FACING, rock.getOpposite())
                            .setValue(TCCrystalBlock.SIZE, size);
                    if (level.setBlock(pos, state, Block.UPDATE_CLIENTS)) amount += size;
                }
            }
        }
        return amount;
    }

    public static Aspect getBiomeAspect(Holder<Biome> biome, RandomSource random) {
        // BiomeHandler chooses a random dictionary TYPE, not the first matching biome family.
        // This is a documented modern category approximation, not the exact old dictionary membership.
        List<Aspect> pool = new ArrayList<>();
        if (biome.is(BiomeTags.IS_OCEAN)) pool.add(Aspect.WATER);
        if (biome.is(BiomeTags.IS_RIVER)) pool.add(Aspect.WATER);
        if (biome.value().getModifiedClimateSettings().downfall() >= 0.85F) pool.add(Aspect.WATER);
        if (biome.is(Biomes.LUSH_CAVES)) pool.add(Aspect.WATER);
        if (biome.value().getBaseTemperature() >= 1.0F) pool.add(Aspect.FIRE);
        if (biome.is(BiomeTags.IS_NETHER)) pool.add(Aspect.FIRE);
        if (biome.is(BiomeTags.IS_BADLANDS)) pool.add(Aspect.FIRE);
        if (biome.value().getBaseTemperature() <= 0.25F) pool.add(Aspect.ORDER);
        if (biome.is(Biomes.MUSHROOM_FIELDS) || biome.is(TCBiomes.MAGICAL_FOREST)) pool.add(Aspect.ORDER);
        if (biome.is(BiomeTags.IS_TAIGA)) pool.add(Aspect.EARTH);
        if (biome.is(BiomeTags.IS_FOREST)) pool.add(Aspect.EARTH);
        if (biome.is(BiomeTags.IS_JUNGLE)) pool.add(Aspect.EARTH);
        if (biome.is(BiomeTags.IS_BEACH) || biome.is(Biomes.DESERT)) pool.add(Aspect.EARTH);
        if (biome.is(BiomeTags.IS_MOUNTAIN)) pool.add(Aspect.AIR);
        if (biome.is(BiomeTags.IS_HILL)) pool.add(Aspect.AIR);
        if (biome.is(BiomeTags.IS_SAVANNA) || biome.is(Biomes.PLAINS) || biome.is(Biomes.SUNFLOWER_PLAINS)) pool.add(Aspect.AIR);
        // DRY is registered twice in legacy BiomeHandler; its final value is ENTROPY.
        if (biome.value().getModifiedClimateSettings().downfall() <= 0.05F) pool.add(Aspect.ENTROPY);
        if (biome.is(Biomes.SWAMP) || biome.is(Biomes.MANGROVE_SWAMP) || biome.is(Biomes.DEEP_DARK)) pool.add(Aspect.ENTROPY);
        return pool.isEmpty() ? null : pool.get(random.nextInt(pool.size()));
    }

    private static Block getBlockForAspect(Aspect aspect) {
        if (aspect == Aspect.AIR) return TCBlocks.CRYSTAL_AER.get();
        if (aspect == Aspect.FIRE) return TCBlocks.CRYSTAL_IGNIS.get();
        if (aspect == Aspect.WATER) return TCBlocks.CRYSTAL_AQUA.get();
        if (aspect == Aspect.EARTH) return TCBlocks.CRYSTAL_TERRA.get();
        if (aspect == Aspect.ORDER) return TCBlocks.CRYSTAL_ORDO.get();
        return TCBlocks.CRYSTAL_PERDITIO.get();
    }

    private static Direction findAdjacentRock(WorldGenLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos support = pos.relative(dir);
            BlockState state = level.getBlockState(support);
            if ((state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.STONE_ORE_REPLACEABLES)
                    || state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES) || state.is(Blocks.CALCITE))
                    && state.isFaceSturdy(level, support, dir.getOpposite())) return dir;
        }
        return null;
    }
}
