package thaumcraft.common.world.biome;

import java.util.Arrays;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import terrablender.worldgen.IExtendedParameterList;

/**
 * 2D Morphological Opening filter ({@code M_final = M ∩ (M_core ⊕ B_R)}) for {@code thaumcraft:magical_forest}.
 *
 * <p>Eliminates all remaining rare edge cases where a TerraBlender 2D {@code Region} polygon
 * ({@code thaumcraft:overworld_magical_forest}) is clipped into a mini-biome or narrow ribbon by:
 * <ul>
 *   <li>Ocean coastlines ({@code Continentalness < -0.19}) or small ocean islands,</li>
 *   <li>Extreme desert/badlands borders ({@code Temperature > +0.55}, {@code T: 4}),</li>
 *   <li>Extreme frozen tundra borders ({@code Temperature < -0.45}, {@code T: 0}),</li>
 *   <li>Narrow fractal {@code ZoomLayer} peninsulas/pinches narrower than ~100 blocks.</li>
 * </ul>
 *
 * <p>A chunk {@code (cx, cz)} is a <b>Core Chunk</b> iff all 29 chunks within Euclidean radius
 * {@code R_core = 3} chunks ({@code dx^2 + dz^2 <= 9}, diameter ~112 blocks) are inside
 * {@code Region: thaumcraft:overworld_magical_forest} and have non-ocean continentalness
 * ({@code C >= -0.19}) and unfrozen/non-desert temperature ({@code -0.45 <= T <= +0.55}).
 * A quart {@code (qx, qz)} is approved iff it lies within Euclidean quart distance squared
 * {@code <= 180} (~54 blocks) of at least one Core Chunk center. Unapproved quarts seamlessly
 * fall back to the Vanilla Overworld biome tree via {@link Climate.ParameterList#findValue}.</p>
 */
public final class TCMagicalForestShapeFilter {
    private static final long MIN_TEMP_QUANTIZED = Climate.quantizeCoord(-0.46F);
    private static final long MAX_TEMP_QUANTIZED = Climate.quantizeCoord(0.56F);
    private static final double MIN_LAND_CONTINENTALNESS = -0.19D;
    private static final double MIN_TEMPERATE_WARM_COOL_TEMP = -0.45D;
    private static final double MAX_TEMPERATE_WARM_COOL_TEMP = 0.55D;

    /**
     * Maximum squared quart distance from a Core Chunk center {@code ((gx << 2) + 2, (gz << 2) + 2)}.
     * Covers 100% of quarts in all 29 chunks of the radius-3 Core Disk ({@code max = 13^2 + 2^2 = 173 <= 180})
     * while never exceeding the {@code [-3, +3]} chunk search window ({@code 14^2 = 196 > 180}).
     */
    private static final int MAX_QUART_DIST_SQ = 180;

    /**
     * Exact 29 chunk offsets {@code (dx, dz)} in the disk {@code dx^2 + dz^2 <= 9}, ordered for
     * fast boundary rejection during Erosion (center first, then outer ring, then interior).
     */
    private static final int[][] EROSION_DISK_OFFSETS = {
            {0, 0},
            {3, 0}, {-3, 0}, {0, 3}, {0, -3},
            {2, 2}, {-2, 2}, {2, -2}, {-2, -2},
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {-1, 2}, {1, -2}, {-1, -2},
            {2, 0}, {-2, 0}, {0, 2}, {0, -2},
            {1, 1}, {-1, 1}, {1, -1}, {-1, -1},
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    /**
     * Full search square ordered from center outward for fast approval during Dilation.
     * The quart-distance test also admits chunks such as (3,1), outside the erosion disk.
     * (interior points hit {@code (0, 0)} on the first iteration).
     */
    private static final int[][] DILATION_DISK_OFFSETS = {
            {0, 0},
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {-1, 1}, {1, -1}, {-1, -1},
            {2, 0}, {-2, 0}, {0, 2}, {0, -2},
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {-1, 2}, {1, -2}, {-1, -2},
            {2, 2}, {-2, 2}, {2, -2}, {-2, -2},
            {3, 0}, {-3, 0}, {0, 3}, {0, -3},
            {3, 1}, {3, -1}, {-3, 1}, {-3, -1}, {1, 3}, {-1, 3}, {1, -3}, {-1, -3},
            {3, 2}, {3, -2}, {-3, 2}, {-3, -2}, {2, 3}, {-2, 3}, {2, -3}, {-2, -3},
            {3, 3}, {3, -3}, {-3, 3}, {-3, -3}
    };

    private static final ThreadLocal<ChunkFilterCache> CACHE = ThreadLocal.withInitial(ChunkFilterCache::new);

    private TCMagicalForestShapeFilter() {
    }

    @SuppressWarnings("unchecked")
    public static Holder<Biome> filterMagicalForest(
            Climate.ParameterList<Holder<Biome>> parameters,
            int quartX,
            int quartY,
            int quartZ,
            Climate.Sampler sampler,
            Holder<Biome> current) {
        if (!(parameters instanceof IExtendedParameterList<?> rawExt) || !rawExt.isInitialized()) {
            return current;
        }
        IExtendedParameterList<Holder<Biome>> extParams = (IExtendedParameterList<Holder<Biome>>) rawExt;

        int magicalRegionIndex = extParams.getUniqueness(quartX, 0, quartZ);
        if (magicalRegionIndex == 0) {
            return current;
        }

        ChunkFilterCache cache = CACHE.get();
        cache.prepare(sampler, magicalRegionIndex);

        if (!isApprovedQuart(extParams, sampler, magicalRegionIndex, cache, quartX, quartZ)) {
            return getVanillaFallback(parameters, sampler.sample(quartX, quartY, quartZ), current);
        }

        // Guard against merged-span biomes (such as Mangrove Swamp [0.20, 1.00]) leaking into T: 4 (> 0.55)
        Climate.TargetPoint target = sampler.sample(quartX, quartY, quartZ);
        if (target.temperature() < MIN_TEMP_QUANTIZED || target.temperature() > MAX_TEMP_QUANTIZED) {
            return getVanillaFallback(parameters, target, current);
        }

        return current;
    }

    private static boolean isApprovedQuart(
            IExtendedParameterList<Holder<Biome>> extParams,
            Climate.Sampler sampler,
            int magicalRegionIndex,
            ChunkFilterCache cache,
            int quartX,
            int quartZ) {
        int chunkX = quartX >> 2;
        int chunkZ = quartZ >> 2;

        for (int[] offset : DILATION_DISK_OFFSETS) {
            int gx = chunkX + offset[0];
            int gz = chunkZ + offset[1];
            int dqx = quartX - ((gx << 2) + 2);
            int dqz = quartZ - ((gz << 2) + 2);
            if (dqx * dqx + dqz * dqz <= MAX_QUART_DIST_SQ) {
                if (isCoreChunk(extParams, sampler, magicalRegionIndex, cache, gx, gz)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isCoreChunk(
            IExtendedParameterList<Holder<Biome>> extParams,
            Climate.Sampler sampler,
            int magicalRegionIndex,
            ChunkFilterCache cache,
            int chunkX,
            int chunkZ) {
        long key = ChunkPos.asLong(chunkX, chunkZ);
        int cached = cache.getCore(key);
        if (cached != 0) {
            return cached == 1;
        }

        for (int[] offset : EROSION_DISK_OFFSETS) {
            if (!isRawMagicalLand(extParams, sampler, magicalRegionIndex, cache, chunkX + offset[0], chunkZ + offset[1])) {
                cache.putCore(key, false);
                return false;
            }
        }

        cache.putCore(key, true);
        return true;
    }

    private static boolean isRawMagicalLand(
            IExtendedParameterList<Holder<Biome>> extParams,
            Climate.Sampler sampler,
            int magicalRegionIndex,
            ChunkFilterCache cache,
            int chunkX,
            int chunkZ) {
        long key = ChunkPos.asLong(chunkX, chunkZ);
        int cached = cache.getRaw(key);
        if (cached != 0) {
            return cached == 1;
        }

        int qx = (chunkX << 2) + 2;
        int qz = (chunkZ << 2) + 2;
        if (extParams.getUniqueness(qx, 0, qz) != magicalRegionIndex) {
            cache.putRaw(key, false);
            return false;
        }

        DensityFunction.SinglePointContext ctx = new DensityFunction.SinglePointContext(qx << 2, 0, qz << 2);
        double cont = sampler.continentalness().compute(ctx);
        if (cont < MIN_LAND_CONTINENTALNESS) {
            cache.putRaw(key, false);
            return false;
        }

        double temp = sampler.temperature().compute(ctx);
        boolean valid = temp >= MIN_TEMPERATE_WARM_COOL_TEMP && temp <= MAX_TEMPERATE_WARM_COOL_TEMP;
        cache.putRaw(key, valid);
        return valid;
    }

    private static Holder<Biome> getVanillaFallback(
            Climate.ParameterList<Holder<Biome>> parameters,
            Climate.TargetPoint target,
            Holder<Biome> fallback) {
        Holder<Biome> vanilla = parameters.findValue(target);
        return vanilla != null ? vanilla : fallback;
    }

    private static final class ChunkFilterCache {
        private static final int SIZE = 4096;
        private static final int MASK = SIZE - 1;
        private static final long EMPTY_KEY = Long.MIN_VALUE;

        private final long[] rawKeys = new long[SIZE];
        private final byte[] rawVals = new byte[SIZE];
        private final long[] coreKeys = new long[SIZE];
        private final byte[] coreVals = new byte[SIZE];
        private Climate.Sampler lastSampler;
        private int lastRegionIndex = -1;

        ChunkFilterCache() {
            Arrays.fill(this.rawKeys, EMPTY_KEY);
            Arrays.fill(this.coreKeys, EMPTY_KEY);
        }

        void prepare(Climate.Sampler sampler, int regionIndex) {
            if (this.lastSampler != sampler || this.lastRegionIndex != regionIndex) {
                this.lastSampler = sampler;
                this.lastRegionIndex = regionIndex;
                Arrays.fill(this.rawKeys, EMPTY_KEY);
                Arrays.fill(this.coreKeys, EMPTY_KEY);
            }
        }

        int getRaw(long key) {
            int idx = mixHash(key) & MASK;
            return this.rawKeys[idx] == key ? this.rawVals[idx] : 0;
        }

        void putRaw(long key, boolean value) {
            int idx = mixHash(key) & MASK;
            this.rawKeys[idx] = key;
            this.rawVals[idx] = (byte) (value ? 1 : -1);
        }

        int getCore(long key) {
            int idx = mixHash(key) & MASK;
            return this.coreKeys[idx] == key ? this.coreVals[idx] : 0;
        }

        void putCore(long key, boolean value) {
            int idx = mixHash(key) & MASK;
            this.coreKeys[idx] = key;
            this.coreVals[idx] = (byte) (value ? 1 : -1);
        }

        private static int mixHash(long z) {
            z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
            z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
            return (int) (z ^ (z >>> 31));
        }
    }
}
