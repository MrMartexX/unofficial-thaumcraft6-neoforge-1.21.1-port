package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
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
import thaumcraft.common.config.TCConfig;
import thaumcraft.common.registry.TCBiomes;
import thaumcraft.common.registry.TCBlocks;

/** Legacy attempt budget, 3x3x3 sampling, and exact 1.12.2 BiomeDictionary aspect pools with modern deepslate/biome fallback. */
public final class TCCrystalClusterFeature extends Feature<NoneFeatureConfiguration> {
    private static final Aspect[] PRIMALS = {Aspect.AIR, Aspect.FIRE, Aspect.WATER, Aspect.EARTH, Aspect.ORDER, Aspect.ENTROPY};
    private static final Map<ResourceKey<Biome>, Aspect[]> EXACT_LEGACY_BIOME_POOLS = createExactLegacyBiomePools();
    private final Supplier<Block> crystalOverride;

    public TCCrystalClusterFeature(Codec<NoneFeatureConfiguration> codec, Supplier<Block> crystal) {
        super(codec);
        this.crystalOverride = crystal;
    }

    public TCCrystalClusterFeature(Codec<NoneFeatureConfiguration> codec) { this(codec, null); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (TCConfig.SPEC.isLoaded() && !TCConfig.GENERATE_CRYSTALS.get()) {
            return false;
        }
        float density = TCConfig.SPEC.isLoaded() ? (TCConfig.ORE_DENSITY.get() / 100.0F) : 1.0F;
        if (density <= 0.0F) {
            return false;
        }
        int maxAttempts = Math.round((crystalOverride == null ? 8 : 1) * density);
        int maxCrystals = Math.round(64.0F * density);
        int amount = 0;
        for (int n = 0; n < maxAttempts; n++) {
            amount += placeAttempt(context);
            // Legacy tests the cumulative size budget BETWEEN complete attempts, not inside a cluster.
            if (amount > maxCrystals) break;
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
        ResourceKey<Biome> key = biome.unwrapKey().orElse(null);
        if (key != null) {
            Aspect[] exactPool = EXACT_LEGACY_BIOME_POOLS.get(key);
            if (exactPool != null) {
                return exactPool.length == 0 ? null : exactPool[random.nextInt(exactPool.length)];
            }
        }
        // Fallback for post-1.12.2 vanilla biomes (e.g. lush_caves, deep_dark, mangrove_swamp) and third-party modded biomes.
        List<Aspect> pool = new ArrayList<>();
        if (biome.is(BiomeTags.IS_OCEAN)) {
            pool.add(Aspect.WATER);
            pool.add(Aspect.WATER);
        }
        if (biome.is(BiomeTags.IS_RIVER)) {
            pool.add(Aspect.WATER);
            pool.add(Aspect.WATER);
        }
        if (biome.value().getModifiedClimateSettings().downfall() > 0.85F) pool.add(Aspect.WATER);
        if (biome.is(Biomes.LUSH_CAVES)) pool.add(Aspect.WATER);
        if (biome.value().getBaseTemperature() > 0.85F) pool.add(Aspect.FIRE);
        if (biome.is(BiomeTags.IS_NETHER)) pool.add(Aspect.FIRE);
        if (biome.is(BiomeTags.IS_BADLANDS)) pool.add(Aspect.FIRE);
        if (biome.value().getBaseTemperature() < 0.15F) pool.add(Aspect.ORDER);
        if (biome.is(Biomes.MUSHROOM_FIELDS) || biome.is(TCBiomes.MAGICAL_FOREST)) pool.add(Aspect.ORDER);
        if (biome.is(BiomeTags.IS_TAIGA)) pool.add(Aspect.EARTH);
        if (biome.is(BiomeTags.IS_FOREST)) pool.add(Aspect.EARTH);
        if (biome.is(BiomeTags.IS_JUNGLE)) pool.add(Aspect.EARTH);
        if (biome.is(BiomeTags.IS_BEACH) || biome.is(Biomes.DESERT)) pool.add(Aspect.EARTH);
        if (biome.is(BiomeTags.IS_MOUNTAIN)) pool.add(Aspect.AIR);
        if (biome.is(BiomeTags.IS_HILL)) pool.add(Aspect.AIR);
        if (biome.is(BiomeTags.IS_SAVANNA) || biome.is(Biomes.PLAINS) || biome.is(Biomes.SUNFLOWER_PLAINS)) pool.add(Aspect.AIR);
        // DRY is registered twice in legacy BiomeHandler; its final value is ENTROPY.
        if (biome.value().getModifiedClimateSettings().downfall() < 0.15F) pool.add(Aspect.ENTROPY);
        if (biome.is(Biomes.SWAMP) || biome.is(Biomes.MANGROVE_SWAMP) || biome.is(Biomes.DEEP_DARK)) pool.add(Aspect.ENTROPY);
        return pool.isEmpty() ? null : pool.get(random.nextInt(pool.size()));
    }

    /**
     * Exact 1.12.2 Forge BiomeDictionary.registerVanillaBiomes() + Thaumcraft BiomeHandler.registerBiomes() pools.
     * Note: OCEAN and RIVER automatically include supertype WATER in 1.12.2 BiomeDictionary.
     * RARE and VOID have no entry in BiomeHandler.biomeInfo, causing getRandomBiomeTag to return null
     * (which falls back to a uniform random primal aspect in placeAttempt).
     */
    private static Map<ResourceKey<Biome>, Aspect[]> createExactLegacyBiomePools() {
        Map<ResourceKey<Biome>, Aspect[]> map = new HashMap<>();
        // Thaumcraft biomes
        map.put(TCBiomes.MAGICAL_FOREST, new Aspect[] {Aspect.ORDER, Aspect.EARTH}); // MAGICAL, FOREST
        map.put(TCBiomes.EERIE, new Aspect[] {Aspect.ORDER, Aspect.FIRE}); // MAGICAL, SPOOKY
        map.put(TCBiomes.ELDRITCH, new Aspect[] {Aspect.ORDER, Aspect.FIRE, Aspect.AIR}); // MAGICAL, SPOOKY, END

        // Oceans & Rivers
        map.put(Biomes.OCEAN, new Aspect[] {Aspect.WATER, Aspect.WATER}); // OCEAN, WATER
        map.put(Biomes.DEEP_OCEAN, new Aspect[] {Aspect.WATER, Aspect.WATER}); // OCEAN, WATER
        map.put(Biomes.FROZEN_OCEAN, new Aspect[] {Aspect.ORDER, Aspect.WATER, Aspect.ORDER, Aspect.WATER}); // COLD, OCEAN, SNOWY, WATER
        map.put(Biomes.RIVER, new Aspect[] {Aspect.WATER, Aspect.WATER}); // RIVER, WATER
        map.put(Biomes.FROZEN_RIVER, new Aspect[] {Aspect.ORDER, Aspect.WATER, Aspect.ORDER, Aspect.WATER}); // COLD, RIVER, SNOWY, WATER

        // Beaches & Shores
        map.put(Biomes.BEACH, new Aspect[] {Aspect.EARTH}); // BEACH
        map.put(Biomes.STONY_SHORE, new Aspect[] {Aspect.EARTH}); // stone_beach: BEACH
        map.put(Biomes.SNOWY_BEACH, new Aspect[] {Aspect.ORDER, Aspect.EARTH, Aspect.ORDER}); // cold_beach: COLD, BEACH, SNOWY

        // Plains & Savannas
        map.put(Biomes.PLAINS, new Aspect[] {Aspect.AIR}); // PLAINS
        map.put(Biomes.SUNFLOWER_PLAINS, new Aspect[] {Aspect.AIR, null}); // mutated_plains: PLAINS, RARE
        map.put(Biomes.SAVANNA, new Aspect[] {Aspect.FIRE, Aspect.AIR, Aspect.AIR, Aspect.ENTROPY}); // HOT, SAVANNA, PLAINS, SPARSE
        map.put(Biomes.SAVANNA_PLATEAU, new Aspect[] {Aspect.FIRE, Aspect.AIR, Aspect.AIR, Aspect.ENTROPY, null}); // savanna_rock: HOT, SAVANNA, PLAINS, SPARSE, RARE
        map.put(Biomes.WINDSWEPT_SAVANNA, new Aspect[] {Aspect.FIRE, Aspect.ENTROPY, Aspect.ENTROPY, Aspect.AIR, Aspect.AIR, null}); // mutated_savanna: HOT, DRY, SPARSE, SAVANNA, MOUNTAIN, RARE

        // Deserts & Badlands (Mesa)
        map.put(Biomes.DESERT, new Aspect[] {Aspect.FIRE, Aspect.ENTROPY, Aspect.EARTH}); // HOT, DRY, SANDY
        map.put(Biomes.BADLANDS, new Aspect[] {Aspect.FIRE, Aspect.EARTH, Aspect.ENTROPY}); // mesa: MESA, SANDY, DRY
        map.put(Biomes.WOODED_BADLANDS, new Aspect[] {Aspect.FIRE, Aspect.EARTH, Aspect.ENTROPY, Aspect.ENTROPY}); // mesa_rock: MESA, SANDY, DRY, SPARSE
        map.put(Biomes.ERODED_BADLANDS, new Aspect[] {Aspect.FIRE, Aspect.ENTROPY, Aspect.ENTROPY, Aspect.AIR, null}); // mutated_mesa: HOT, DRY, SPARSE, MOUNTAIN, RARE

        // Extreme Hills (Windswept)
        map.put(Biomes.WINDSWEPT_HILLS, new Aspect[] {Aspect.AIR, Aspect.AIR}); // extreme_hills: MOUNTAIN, HILLS
        map.put(Biomes.WINDSWEPT_GRAVELLY_HILLS, new Aspect[] {Aspect.AIR, Aspect.ENTROPY, null}); // mutated_extreme_hills: MOUNTAIN, SPARSE, RARE
        map.put(Biomes.WINDSWEPT_FOREST, new Aspect[] {Aspect.AIR, Aspect.EARTH, Aspect.ENTROPY}); // extreme_hills_with_trees: MOUNTAIN, FOREST, SPARSE

        // Forests & Taigas
        map.put(Biomes.FOREST, new Aspect[] {Aspect.EARTH}); // FOREST
        map.put(Biomes.FLOWER_FOREST, new Aspect[] {Aspect.EARTH, Aspect.AIR, null}); // mutated_forest: FOREST, HILLS, RARE
        map.put(Biomes.BIRCH_FOREST, new Aspect[] {Aspect.EARTH}); // FOREST
        map.put(Biomes.OLD_GROWTH_BIRCH_FOREST, new Aspect[] {Aspect.EARTH, Aspect.ORDER, Aspect.AIR, null}); // mutated_birch_forest: FOREST, DENSE, HILLS, RARE
        map.put(Biomes.DARK_FOREST, new Aspect[] {Aspect.FIRE, Aspect.ORDER, Aspect.EARTH}); // roofed_forest: SPOOKY, DENSE, FOREST
        map.put(Biomes.TAIGA, new Aspect[] {Aspect.ORDER, Aspect.EARTH, Aspect.EARTH}); // COLD, CONIFEROUS, FOREST
        map.put(Biomes.SNOWY_TAIGA, new Aspect[] {Aspect.ORDER, Aspect.EARTH, Aspect.EARTH, Aspect.ORDER}); // taiga_cold: COLD, CONIFEROUS, FOREST, SNOWY
        map.put(Biomes.OLD_GROWTH_PINE_TAIGA, new Aspect[] {Aspect.ORDER, Aspect.EARTH, Aspect.EARTH}); // redwood_taiga: COLD, CONIFEROUS, FOREST
        map.put(Biomes.OLD_GROWTH_SPRUCE_TAIGA, new Aspect[] {Aspect.ORDER, Aspect.EARTH, null}); // mutated_redwood_taiga: DENSE, FOREST, RARE

        // Swamps & Jungles
        map.put(Biomes.SWAMP, new Aspect[] {Aspect.WATER, Aspect.ENTROPY}); // swampland: WET, SWAMP
        map.put(Biomes.JUNGLE, new Aspect[] {Aspect.FIRE, Aspect.WATER, Aspect.ORDER, Aspect.EARTH}); // HOT, WET, DENSE, JUNGLE
        map.put(Biomes.SPARSE_JUNGLE, new Aspect[] {Aspect.FIRE, Aspect.WATER, Aspect.EARTH, Aspect.EARTH, null}); // jungle_edge: HOT, WET, JUNGLE, FOREST, RARE

        // Snowy & Mushroom
        map.put(Biomes.SNOWY_PLAINS, new Aspect[] {Aspect.ORDER, Aspect.ORDER, Aspect.ENTROPY}); // ice_flats: COLD, SNOWY, WASTELAND
        map.put(Biomes.ICE_SPIKES, new Aspect[] {Aspect.ORDER, Aspect.ORDER, Aspect.AIR, null}); // mutated_ice_flats: COLD, SNOWY, HILLS, RARE
        map.put(Biomes.SNOWY_SLOPES, new Aspect[] {Aspect.ORDER, Aspect.ORDER, Aspect.AIR}); // ice_mountains: COLD, SNOWY, MOUNTAIN
        map.put(Biomes.MUSHROOM_FIELDS, new Aspect[] {Aspect.ORDER, null}); // mushroom_island: MUSHROOM, RARE

        // Dimensions & Void
        map.put(Biomes.NETHER_WASTES, new Aspect[] {Aspect.FIRE, Aspect.ENTROPY, Aspect.FIRE}); // hell: HOT, DRY, NETHER
        map.put(Biomes.THE_END, new Aspect[] {Aspect.ORDER, Aspect.ENTROPY, Aspect.AIR}); // sky: COLD, DRY, END
        map.put(Biomes.THE_VOID, new Aspect[] {null}); // void: VOID
        return Map.copyOf(map);
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
