package thaumcraft.common.audit.parity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.TCConfig;
import thaumcraft.common.entities.TCCultistClericEntity;
import thaumcraft.common.entities.TCCultistEntity;
import thaumcraft.common.entities.TCCultistKnightEntity;
import thaumcraft.common.entities.TCCultistPortalLesserEntity;
import thaumcraft.common.registry.TCBiomes;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.world.features.TCCrystalClusterFeature;
import thaumcraft.common.world.features.TCCultistPortalFeature;
import thaumcraft.common.world.features.TCLegacyPatchFeature;
import thaumcraft.common.world.placement.TCOreDensityPlacement;

/**
 * Gate 11 diagnostic runner:
 * 0. Exact 1.12.2 BiomeDictionary crystal pools, TCOreDensityPlacement config scaling/flags, and 2D per-patch flower noise.
 * 1. Biome morphology (TCMagicalForestShapeFilter) and fresh chunk generation timing profile with strict SLA bounds.
 * 2. Natural Magical Forest chunk vegetation, tree, boulder, ore, and crystal density statistics compared against 1.12.2 baseline.
 * 3. Natural Lesser Cultist Portal structure admission, natural player-proximity activation, wave interval cadence, minion cap, and self-damage collapse.
 *
 * Refuses to run outside disposable tc_worldgen_audit_* saves.
 */
public final class TCWorldgenGate11AuditExporter {
    private static final int TARGET_FOREST_CHUNKS = 30;
    private static final int PROFILE_CHUNK_COUNT = 32;

    private TCWorldgenGate11AuditExporter() {}

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.getBoolean("tc.worldgenGate11Audit")) {
            return;
        }
        var server = event.getServer();
        Path world = server.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize();
        if (!world.getFileName().toString().startsWith("tc_worldgen_audit_")) {
            throw new IllegalStateException("Gate 11 worldgen audit requires a disposable tc_worldgen_audit_* save");
        }
        List<String> rows = new ArrayList<>();
        try {
            ServerLevel level = server.overworld();
            String runId = System.getProperty("tc.worldgenAuditRunId", "untracked");
            String manifestHash = System.getProperty("tc.worldgenAuditManifest", "untracked");
            rows.add("Run ID: `" + runId + "`");
            rows.add("Manifest Fingerprint: `" + manifestHash + "`");
            rows.add("Seed: `" + level.getSeed() + "`; world: `" + world.getFileName() + "`\n");

            List<ChunkPos> forestChunks = locateNaturalForestChunks(level);
            runStage2ContractAndBiomeChecks(level, rows);
            runBiomeMorphologyAndChunkProfile(level, rows, forestChunks);
            runNaturalMagicalForestSurvey(level, rows, forestChunks);
            runNaturalPortalLifecycleAudit(level, rows);
        } catch (Exception e) {
            rows.add("- FAIL exception: " + e);
            Thaumcraft.LOGGER.error("Gate 11 worldgen audit exception", e);
        } finally {
            boolean pass = rows.stream().noneMatch(s -> s.startsWith("- FAIL "));
            String report = "# Worldgen Gate 11 Diagnostics (Natural Forest Density, Profile & Portal Lifecycle)\n\n"
                    + String.join("\n", rows)
                    + "\n\nResult: **" + (pass ? "PASS" : "FAIL") + "**\n";
            try {
                Path out = Path.of(System.getProperty("tc.worldgenGate11AuditPath", "worldgen-gate11-audit.md")).toAbsolutePath();
                Files.createDirectories(out.getParent());
                Files.writeString(out, report);
                Thaumcraft.LOGGER.info("TC_WORLDGEN_GATE11_AUDIT_{} report={}", pass ? "PASS" : "FAIL", out);
            } catch (Exception e) {
                throw new IllegalStateException("Cannot write Gate 11 worldgen audit report", e);
            } finally {
                server.halt(false);
            }
        }
    }

    private static void runStage2ContractAndBiomeChecks(ServerLevel level, List<String> rows) {
        rows.add("## 0. Stage 2 BiomeDictionary Crystal Pools, Config Wiring & 2D Per-Patch Flower Noise\n");
        var biomeReg = level.registryAccess().registryOrThrow(Registries.BIOME);
        RandomSource rand = RandomSource.create(61222121L);
        int trials = 6000;

        // 1. Magical Forest: 50% Ordo, 50% Terra
        var mf = biomeReg.getHolderOrThrow(TCBiomes.MAGICAL_FOREST);
        int mfOrdo = 0, mfTerra = 0, mfOther = 0;
        for (int i = 0; i < trials; i++) {
            Aspect a = TCCrystalClusterFeature.getBiomeAspect(mf, rand);
            if (a == Aspect.ORDER) mfOrdo++;
            else if (a == Aspect.EARTH) mfTerra++;
            else mfOther++;
        }
        check(rows, "magical_forest crystal pool is 50% Ordo / 50% Terra (MAGICAL, FOREST)",
                mfOther == 0 && Math.abs(mfOrdo - trials / 2) < 200 && Math.abs(mfTerra - trials / 2) < 200);

        // 2. Eerie: 50% Ordo, 50% Ignis
        var eerie = biomeReg.getHolderOrThrow(TCBiomes.EERIE);
        int eeOrdo = 0, eeIgnis = 0, eeOther = 0;
        for (int i = 0; i < trials; i++) {
            Aspect a = TCCrystalClusterFeature.getBiomeAspect(eerie, rand);
            if (a == Aspect.ORDER) eeOrdo++;
            else if (a == Aspect.FIRE) eeIgnis++;
            else eeOther++;
        }
        check(rows, "eerie crystal pool is 50% Ordo / 50% Ignis (MAGICAL, SPOOKY)",
                eeOther == 0 && Math.abs(eeOrdo - trials / 2) < 200 && Math.abs(eeIgnis - trials / 2) < 200);

        // 3. Eldritch: 1/3 Ordo, 1/3 Ignis, 1/3 Aer
        var eldritch = biomeReg.getHolderOrThrow(TCBiomes.ELDRITCH);
        int elOrdo = 0, elIgnis = 0, elAer = 0, elOther = 0;
        for (int i = 0; i < trials; i++) {
            Aspect a = TCCrystalClusterFeature.getBiomeAspect(eldritch, rand);
            if (a == Aspect.ORDER) elOrdo++;
            else if (a == Aspect.FIRE) elIgnis++;
            else if (a == Aspect.AIR) elAer++;
            else elOther++;
        }
        check(rows, "eldritch crystal pool is 1/3 Ordo, 1/3 Ignis, 1/3 Aer (MAGICAL, SPOOKY, END)",
                elOther == 0 && Math.abs(elOrdo - trials / 3) < 200
                        && Math.abs(elIgnis - trials / 3) < 200 && Math.abs(elAer - trials / 3) < 200);

        // 4. Stony shore (stone_beach -> BEACH -> 100% Terra) & Windswept hills (extreme_hills -> MOUNTAIN, HILLS -> 100% Aer)
        var stonyShore = biomeReg.getHolderOrThrow(net.minecraft.world.level.biome.Biomes.STONY_SHORE);
        var windsweptHills = biomeReg.getHolderOrThrow(net.minecraft.world.level.biome.Biomes.WINDSWEPT_HILLS);
        boolean stonyAndHillsOk = true;
        for (int i = 0; i < 500; i++) {
            stonyAndHillsOk &= TCCrystalClusterFeature.getBiomeAspect(stonyShore, rand) == Aspect.EARTH;
            stonyAndHillsOk &= TCCrystalClusterFeature.getBiomeAspect(windsweptHills, rand) == Aspect.AIR;
        }
        check(rows, "stony_shore (100% Terra) and windswept_hills (100% Aer) match 1.12.2 BiomeDictionary", stonyAndHillsOk);

        // 5. TCConfig oreDensity and generateCinnabar / generateQuartz / generateAmber live wiring via TCOreDensityPlacement
        var placedReg = level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
        var placedCinnabar = placedReg.get(ResourceLocation.fromNamespaceAndPath("thaumcraft", "ore_cinnabar"));
        var placedQuartz = placedReg.get(ResourceLocation.fromNamespaceAndPath("thaumcraft", "ore_quartz"));
        var placedAmber = placedReg.get(ResourceLocation.fromNamespaceAndPath("thaumcraft", "ore_amber"));
        boolean modifiersWired = placedCinnabar != null && placedQuartz != null && placedAmber != null
                && placedCinnabar.placement().getFirst() instanceof TCOreDensityPlacement
                && placedQuartz.placement().getFirst() instanceof TCOreDensityPlacement
                && placedAmber.placement().getFirst() instanceof TCOreDensityPlacement;

        int oldDensity = TCConfig.ORE_DENSITY.get();
        boolean oldCin = TCConfig.GENERATE_CINNABAR.get();
        boolean oldQtz = TCConfig.GENERATE_QUARTZ.get();
        boolean oldAmb = TCConfig.GENERATE_AMBER.get();
        boolean scalingAndFlagsOk = modifiersWired;
        try {
            // Default 100%
            scalingAndFlagsOk &= TCOreDensityPlacement.computeConfiguredCount("cinnabar", 18) == 18
                    && TCOreDensityPlacement.computeConfiguredCount("quartz", 18) == 18
                    && TCOreDensityPlacement.computeConfiguredCount("amber", 20) == 20;
            // 50% density
            TCConfig.ORE_DENSITY.set(50);
            scalingAndFlagsOk &= TCOreDensityPlacement.computeConfiguredCount("cinnabar", 18) == 9
                    && TCOreDensityPlacement.computeConfiguredCount("quartz", 18) == 9
                    && TCOreDensityPlacement.computeConfiguredCount("amber", 20) == 10;
            // 200% density
            TCConfig.ORE_DENSITY.set(200);
            scalingAndFlagsOk &= TCOreDensityPlacement.computeConfiguredCount("cinnabar", 18) == 36
                    && TCOreDensityPlacement.computeConfiguredCount("quartz", 18) == 36
                    && TCOreDensityPlacement.computeConfiguredCount("amber", 20) == 40;
            // Individual disable flags
            TCConfig.ORE_DENSITY.set(100);
            TCConfig.GENERATE_CINNABAR.set(false);
            scalingAndFlagsOk &= TCOreDensityPlacement.computeConfiguredCount("cinnabar", 18) == 0
                    && TCOreDensityPlacement.computeConfiguredCount("quartz", 18) == 18;
            TCConfig.GENERATE_CINNABAR.set(true);
            TCConfig.GENERATE_QUARTZ.set(false);
            scalingAndFlagsOk &= TCOreDensityPlacement.computeConfiguredCount("quartz", 18) == 0
                    && TCOreDensityPlacement.computeConfiguredCount("cinnabar", 18) == 18;
            TCConfig.GENERATE_QUARTZ.set(true);
            TCConfig.GENERATE_AMBER.set(false);
            scalingAndFlagsOk &= TCOreDensityPlacement.computeConfiguredCount("amber", 20) == 0
                    && TCOreDensityPlacement.computeConfiguredCount("cinnabar", 18) == 18;
        } finally {
            TCConfig.ORE_DENSITY.set(oldDensity);
            TCConfig.GENERATE_CINNABAR.set(oldCin);
            TCConfig.GENERATE_QUARTZ.set(oldQtz);
            TCConfig.GENERATE_AMBER.set(oldAmb);
        }
        check(rows, "TCOreDensityPlacement scales cinnabar/quartz/amber by ORE_DENSITY and honors GENERATE_* flags",
                scalingAndFlagsOk);

        // 6. 2D GRASS_COLOR_NOISE (seed 2345L, scale 1/48) selects one flower species per patch (Y-invariant, mono-species patch)
        boolean yInvariant = true;
        Set<BlockState> sampledSpecies = new HashSet<>();
        for (int gx = -20; gx <= 20; gx++) {
            for (int gz = -20; gz <= 20; gz++) {
                int x = gx * 24;
                int z = gz * 24;
                BlockState atY10 = TCLegacyPatchFeature.pickLegacyMagicalForestFlower(new BlockPos(x, 10, z));
                BlockState atY120 = TCLegacyPatchFeature.pickLegacyMagicalForestFlower(new BlockPos(x, 120, z));
                yInvariant &= (atY10 == atY120);
                sampledSpecies.add(atY10);
            }
        }
        check(rows, "2D flower noise is Y-invariant and covers all 10 legacy EnumFlowerType species",
                yInvariant && sampledSpecies.size() == TCLegacyPatchFeature.legacyFlowerTypeCount());

        // 6b. Independent 1.12.2 flower species grid oracle: verify zero species mismatches on fixed grid of 625 coords
        int gridPoints = 0;
        int gridMismatches = 0;
        try {
            Path refPath = null;
            String customProp = System.getProperty("tc.legacyFlowerGridReference");
            if (customProp != null && !customProp.isBlank()) {
                refPath = Path.of(customProp).toAbsolutePath().normalize();
            } else {
                for (String candidate : List.of(
                        "../../tools/reports/local/worldgen-20261103/legacy_flower_grid_reference.json",
                        "../tools/reports/local/worldgen-20261103/legacy_flower_grid_reference.json",
                        "tools/reports/local/worldgen-20261103/legacy_flower_grid_reference.json")) {
                    Path p = Path.of(candidate).toAbsolutePath().normalize();
                    if (Files.exists(p)) {
                        refPath = p;
                        break;
                    }
                }
            }
            if (refPath != null && Files.exists(refPath)) {
                var jsonTree = JsonParser.parseString(Files.readString(refPath)).getAsJsonObject();
                var pointsArray = jsonTree.getAsJsonArray("points");
                gridPoints = pointsArray.size();
                for (var el : pointsArray) {
                    var obj = el.getAsJsonObject();
                    int x = obj.get("x").getAsInt();
                    int z = obj.get("z").getAsInt();
                    String expectedFlower = obj.get("flower").getAsString();
                    BlockState actualState = TCLegacyPatchFeature.pickLegacyMagicalForestFlower(new BlockPos(x, 64, z));
                    String actualKey = BuiltInRegistries.BLOCK.getKey(actualState.getBlock()).toString();
                    if (!actualKey.equals(expectedFlower)) {
                        gridMismatches++;
                    }
                }
            } else {
                rows.add("- FAIL legacy_flower_grid_reference.json not found in candidate paths");
            }
        } catch (Exception e) {
            rows.add("- FAIL error reading legacy flower grid reference: " + e);
        }
        check(rows, "independent 1.12.2 flower grid oracle: zero species mismatches across 625 coords",
                gridPoints == 625 && gridMismatches == 0);
        rows.add("  - Flower grid oracle against 1.12.2 Forge bytecode: points=" + gridPoints + ", mismatches=" + gridMismatches);

        // Verify per-patch single-species placement in world
        var flowerConfigured = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                .get(ResourceLocation.fromNamespaceAndPath("thaumcraft", "flower_magical_forest"));
        var generator = level.getChunkSource().getGenerator();
        for (int cx = 35; cx <= 38; cx++) {
            for (int cz = 35; cz <= 38; cz++) {
                level.getChunk(cx, cz);
            }
        }
        int groundY = 80;
        for (int x = 584; x <= 600; x++) {
            for (int z = 584; z <= 600; z++) {
                for (int y = 0; y < level.getMaxBuildHeight(); y++) {
                    level.setBlock(new BlockPos(x, y, z),
                            y == groundY ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
        BlockPos patchCenter = new BlockPos(592, groundY + 1, 592);
        BlockState expectedPatchSpecies = TCLegacyPatchFeature.pickLegacyMagicalForestFlower(patchCenter);
        boolean monoSpeciesPatches = true;
        int patchesWithFlowers = 0;
        for (int trial = 0; trial < 30; trial++) {
            for (int x = 584; x <= 600; x++) {
                for (int z = 584; z <= 600; z++) {
                    for (int y = groundY + 1; y <= groundY + 4; y++) {
                        level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
            configuredPlaceSinglePatch(flowerConfigured, level, generator, RandomSource.create(1000L + trial), patchCenter);
            Set<BlockState> placedInPatch = new HashSet<>();
            for (int x = 584; x <= 600; x++) {
                for (int z = 584; z <= 600; z++) {
                    BlockState s = level.getBlockState(new BlockPos(x, groundY + 1, z));
                    if (s.is(BlockTags.SMALL_FLOWERS)) {
                        placedInPatch.add(s);
                    }
                }
            }
            if (!placedInPatch.isEmpty()) {
                patchesWithFlowers++;
                monoSpeciesPatches &= (placedInPatch.size() == 1 && placedInPatch.contains(expectedPatchSpecies));
            }
        }
        check(rows, "each flower_magical_forest patch places a single flower species determined by 2D patch-center noise",
                patchesWithFlowers > 0 && monoSpeciesPatches);
        rows.add("");
    }

    private static void configuredPlaceSinglePatch(
            net.minecraft.world.level.levelgen.feature.ConfiguredFeature<?, ?> configured,
            ServerLevel level,
            net.minecraft.world.level.chunk.ChunkGenerator generator,
            RandomSource random,
            BlockPos origin
    ) {
        configured.place(level, generator, random, origin);
    }

    private static List<ChunkPos> locateNaturalForestChunks(ServerLevel level) {
        var biomeSource = level.getChunkSource().getGenerator().getBiomeSource();
        var sampler = level.getChunkSource().randomState().sampler();
        List<ChunkPos> forestChunks = new ArrayList<>();
        for (int radius = 2; radius <= 250 && forestChunks.size() < TARGET_FOREST_CHUNKS; radius += 2) {
            for (int dx = -radius; dx <= radius && forestChunks.size() < TARGET_FOREST_CHUNKS; dx += 2) {
                for (int dz = -radius; dz <= radius && forestChunks.size() < TARGET_FOREST_CHUNKS; dz += 2) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    int qx = (dx << 2) + 2;
                    int qz = (dz << 2) + 2;
                    if (biomeSource.getNoiseBiome(qx, 16, qz, sampler).is(TCBiomes.MAGICAL_FOREST)
                            && biomeSource.getNoiseBiome(qx - 2, 16, qz, sampler).is(TCBiomes.MAGICAL_FOREST)
                            && biomeSource.getNoiseBiome(qx + 2, 16, qz, sampler).is(TCBiomes.MAGICAL_FOREST)
                            && biomeSource.getNoiseBiome(qx, 16, qz - 2, sampler).is(TCBiomes.MAGICAL_FOREST)
                            && biomeSource.getNoiseBiome(qx, 16, qz + 2, sampler).is(TCBiomes.MAGICAL_FOREST)) {
                        forestChunks.add(new ChunkPos(dx, dz));
                    }
                }
            }
        }
        return forestChunks;
    }

    private static void runBiomeMorphologyAndChunkProfile(ServerLevel level, List<String> rows, List<ChunkPos> forestChunks) {
        rows.add("## 1. Biome Morphology & Chunk Generation Profile\n");
        var generator = level.getChunkSource().getGenerator();
        var biomeSource = generator.getBiomeSource();
        var sampler = level.getChunkSource().randomState().sampler();

        ChunkPos anchor = forestChunks.isEmpty() ? new ChunkPos(0, 0) : forestChunks.getFirst();
        for (int i = 0; i < 4200; i++) {
            biomeSource.getNoiseBiome((50000 + i) << 2, 16, 50000 << 2, sampler);
        }

        int startQuartX = (anchor.x - 16) << 2;
        int startQuartZ = (anchor.z - 16) << 2;
        int gridQuarts = 128;
        int totalQuarts = gridQuarts * gridQuarts;

        long coldStart = System.nanoTime();
        int magicalForestQuartsCold = 0;
        for (int dx = 0; dx < gridQuarts; dx++) {
            for (int dz = 0; dz < gridQuarts; dz++) {
                Holder<Biome> b = biomeSource.getNoiseBiome(startQuartX + dx, 16, startQuartZ + dz, sampler);
                if (b.is(TCBiomes.MAGICAL_FOREST)) {
                    magicalForestQuartsCold++;
                }
            }
        }
        long coldElapsedNs = System.nanoTime() - coldStart;

        long warmStart = System.nanoTime();
        int magicalForestQuartsWarm = 0;
        for (int dx = 0; dx < gridQuarts; dx++) {
            for (int dz = 0; dz < gridQuarts; dz++) {
                Holder<Biome> b = biomeSource.getNoiseBiome(startQuartX + dx, 16, startQuartZ + dz, sampler);
                if (b.is(TCBiomes.MAGICAL_FOREST)) {
                    magicalForestQuartsWarm++;
                }
            }
        }
        long warmElapsedNs = System.nanoTime() - warmStart;

        double coldUsPerQuart = (coldElapsedNs / 1000.0) / totalQuarts;
        double warmUsPerQuart = (warmElapsedNs / 1000.0) / totalQuarts;

        check(rows, "morphology filter deterministic across cold/warm cache",
                magicalForestQuartsCold > 0 && magicalForestQuartsCold == magicalForestQuartsWarm);
        check(rows, "quart biome lookup latency within SLA (cold <= 50 us/quart, warm <= 35 us/quart)",
                coldUsPerQuart > 0.0 && coldUsPerQuart <= 50.0 && warmUsPerQuart > 0.0 && warmUsPerQuart <= 35.0);

        double[] chunkTimesMs = new double[PROFILE_CHUNK_COUNT];
        // Use an unpredictable distant coordinate sector per run so repeat audits cannot reuse saved chunks on disk.
        java.util.Random offsetRand = new java.util.Random(System.nanoTime() ^ level.getSeed());
        int baseChunkX = 1200 + offsetRand.nextInt(2000);
        int baseChunkZ = -1200 - offsetRand.nextInt(2000);
        for (int i = 0; i < PROFILE_CHUNK_COUNT; i++) {
            int cx = baseChunkX + (i % 8) * 4;
            int cz = baseChunkZ + (i / 8) * 4;
            if (level.getChunkSource().hasChunk(cx, cz)) {
                throw new IllegalStateException("Profiling chunk unexpectedly already loaded in memory: " + cx + "," + cz);
            }
            long t0 = System.nanoTime();
            level.getChunk(cx, cz);
            chunkTimesMs[i] = (System.nanoTime() - t0) / 1_000_000.0;
        }
        Arrays.sort(chunkTimesMs);
        double meanMs = Arrays.stream(chunkTimesMs).average().orElse(0.0);
        double p50Ms = chunkTimesMs[PROFILE_CHUNK_COUNT / 2];
        double p95Ms = chunkTimesMs[(int) Math.min(PROFILE_CHUNK_COUNT - 1, Math.floor(PROFILE_CHUNK_COUNT * 0.95))];
        double maxMs = chunkTimesMs[PROFILE_CHUNK_COUNT - 1];

        check(rows, "fresh chunk generation latency within SLA (mean <= 600 ms, p50 <= 500 ms, p95 <= 2000 ms)",
                meanMs > 0.0 && meanMs <= 600.0 && p50Ms <= 500.0 && p95Ms <= 2000.0);
        rows.add(String.format(Locale.ROOT,
                "  - Quart biome lookup (%d queries, 32x32 chunks around Magical Forest `%s`): cold=%.2f ms (%.3f us/quart), warm=%.2f ms (%.3f us/quart), magical_forest=%d/%d (%.2f%%)",
                totalQuarts, anchor,
                coldElapsedNs / 1_000_000.0, coldUsPerQuart,
                warmElapsedNs / 1_000_000.0, warmUsPerQuart,
                magicalForestQuartsCold, totalQuarts, (100.0 * magicalForestQuartsCold) / totalQuarts));
        rows.add(String.format(Locale.ROOT,
                "  - Fresh chunk FULL generation (%d distant chunks): mean=%.2f ms, p50=%.2f ms, p95=%.2f ms, max=%.2f ms\n",
                PROFILE_CHUNK_COUNT, meanMs, p50Ms, p95Ms, maxMs));
    }

    private static void runNaturalMagicalForestSurvey(ServerLevel level, List<String> rows, List<ChunkPos> forestChunks) {
        rows.add("## 2. Natural Magical Forest Feature Density Statistics (" + TARGET_FOREST_CHUNKS + " Natural Chunks vs 1.12.2 Baseline)\n");

        check(rows, "located " + TARGET_FOREST_CHUNKS + " natural interior magical_forest chunks",
                forestChunks.size() >= TARGET_FOREST_CHUNKS);
        if (forestChunks.isEmpty()) {
            return;
        }

        String[] keys = {
                "small_flowers",
                "grass_and_ferns",
                "vishroom",
                "shimmerleaf",
                "small_mushrooms",
                "huge_mushroom_blocks",
                "silverwood_logs",
                "greatwood_logs",
                "mossy_cobblestone",
                "waterlily",
                "cinnabar_ore",
                "quartz_ore",
                "amber_ore",
                "crystals_total",
                "crystal_aer",
                "crystal_ignis",
                "crystal_aqua",
                "crystal_terra",
                "crystal_ordo",
                "crystal_perditio"
        };
        Map<String, List<Integer>> samples = new LinkedHashMap<>();
        for (String k : keys) {
            samples.put(k, new ArrayList<>());
        }
        List<Integer> legacy1122NaturalFlowerSamples = new ArrayList<>();

        Block vishroom = TCBlocks.VISHROOM.get();
        Block shimmerleaf = TCBlocks.SHIMMERLEAF.get();
        Block silverwoodLog = TCBlocks.LOG_SILVERWOOD.get();
        Block greatwoodLog = TCBlocks.LOG_GREATWOOD.get();
        Block cinnabar = TCBlocks.ORE_CINNABAR.get();
        Block deepslateCinnabar = TCBlocks.DEEPSLATE_CINNABAR_ORE.get();
        Block quartz = TCBlocks.ORE_QUARTZ.get();
        Block deepslateQuartz = TCBlocks.DEEPSLATE_QUARTZ_ORE.get();
        Block amber = TCBlocks.ORE_AMBER.get();
        Block deepslateAmber = TCBlocks.DEEPSLATE_AMBER_ORE.get();
        Block cAer = TCBlocks.CRYSTAL_AER.get();
        Block cIgnis = TCBlocks.CRYSTAL_IGNIS.get();
        Block cAqua = TCBlocks.CRYSTAL_AQUA.get();
        Block cTerra = TCBlocks.CRYSTAL_TERRA.get();
        Block cOrdo = TCBlocks.CRYSTAL_ORDO.get();
        Block cPerditio = TCBlocks.CRYSTAL_PERDITIO.get();

        for (int idx = 0; idx < forestChunks.size(); idx++) {
            ChunkPos cp = forestChunks.get(idx);
            for (int ncx = cp.x - 1; ncx <= cp.x + 1; ncx++) {
                for (int ncz = cp.z - 1; ncz <= cp.z + 1; ncz++) {
                    level.getChunk(ncx, ncz);
                }
            }
            var chunk = level.getChunk(cp.x, cp.z);
            chunk.postProcessGeneration();

            Map<String, Integer> counts = new LinkedHashMap<>();
            for (String k : keys) {
                counts.put(k, 0);
            }

            int minX = cp.getMinBlockX();
            int maxX = cp.getMaxBlockX();
            int minZ = cp.getMinBlockZ();
            int maxZ = cp.getMaxBlockZ();
            int minY = level.getMinBuildHeight();
            int maxY = chunk.getHighestFilledSectionIndex() < 0
                    ? level.getSeaLevel() + 32
                    : Math.min(level.getMaxBuildHeight() - 1, (chunk.getHighestFilledSectionIndex() + 1) * 16 + 15);

            for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
                BlockState state = chunk.getBlockState(pos);
                if (state.isAir()) {
                    continue;
                }
                Block block = state.getBlock();
                if (block == vishroom) {
                    counts.compute("vishroom", (k, v) -> v + 1);
                } else if (block == shimmerleaf) {
                    counts.compute("shimmerleaf", (k, v) -> v + 1);
                } else if (state.is(BlockTags.SMALL_FLOWERS)) {
                    counts.compute("small_flowers", (k, v) -> v + 1);
                } else if (block == Blocks.SHORT_GRASS || block == Blocks.FERN
                        || block == Blocks.TALL_GRASS || block == Blocks.LARGE_FERN) {
                    counts.compute("grass_and_ferns", (k, v) -> v + 1);
                } else if (block == Blocks.BROWN_MUSHROOM || block == Blocks.RED_MUSHROOM) {
                    counts.compute("small_mushrooms", (k, v) -> v + 1);
                } else if (block == Blocks.BROWN_MUSHROOM_BLOCK || block == Blocks.RED_MUSHROOM_BLOCK
                        || block == Blocks.MUSHROOM_STEM) {
                    counts.compute("huge_mushroom_blocks", (k, v) -> v + 1);
                } else if (block == silverwoodLog) {
                    counts.compute("silverwood_logs", (k, v) -> v + 1);
                } else if (block == greatwoodLog) {
                    counts.compute("greatwood_logs", (k, v) -> v + 1);
                } else if (block == Blocks.MOSSY_COBBLESTONE) {
                    counts.compute("mossy_cobblestone", (k, v) -> v + 1);
                } else if (block == Blocks.LILY_PAD) {
                    counts.compute("waterlily", (k, v) -> v + 1);
                } else if (block == cinnabar || block == deepslateCinnabar) {
                    counts.compute("cinnabar_ore", (k, v) -> v + 1);
                } else if (block == quartz || block == deepslateQuartz) {
                    counts.compute("quartz_ore", (k, v) -> v + 1);
                } else if (block == amber || block == deepslateAmber) {
                    counts.compute("amber_ore", (k, v) -> v + 1);
                } else if (block == cAer) {
                    counts.compute("crystals_total", (k, v) -> v + 1);
                    counts.compute("crystal_aer", (k, v) -> v + 1);
                } else if (block == cIgnis) {
                    counts.compute("crystals_total", (k, v) -> v + 1);
                    counts.compute("crystal_ignis", (k, v) -> v + 1);
                } else if (block == cAqua) {
                    counts.compute("crystals_total", (k, v) -> v + 1);
                    counts.compute("crystal_aqua", (k, v) -> v + 1);
                } else if (block == cTerra) {
                    counts.compute("crystals_total", (k, v) -> v + 1);
                    counts.compute("crystal_terra", (k, v) -> v + 1);
                } else if (block == cOrdo) {
                    counts.compute("crystals_total", (k, v) -> v + 1);
                    counts.compute("crystal_ordo", (k, v) -> v + 1);
                } else if (block == cPerditio) {
                    counts.compute("crystals_total", (k, v) -> v + 1);
                    counts.compute("crystal_perditio", (k, v) -> v + 1);
                }
            }

            for (String k : keys) {
                samples.get(k).add(counts.get(k));
            }

            // Isolated replay of 1.12.2 ChunkGeneratorOverworld.populate flower stage + BiomeDecorator (flowersPerChunk=10) on modern terrain
            legacy1122NaturalFlowerSamples.add(simulate1122FlowersOnChunkTerrain(level, cp, level.getSeed()));
        }

        double flowerMean = mean(samples.get("small_flowers"));
        double legacy1122FlowerMean = mean(legacy1122NaturalFlowerSamples);
        double grassMean = mean(samples.get("grass_and_ferns"));
        double cinnabarMean = mean(samples.get("cinnabar_ore"));
        double quartzMean = mean(samples.get("quartz_ore"));
        double amberMean = mean(samples.get("amber_ore"));
        double crystalMean = mean(samples.get("crystals_total"));
        int totalCrystals = samples.get("crystals_total").stream().mapToInt(Integer::intValue).sum();
        int totalOrdoTerra = samples.get("crystal_ordo").stream().mapToInt(Integer::intValue).sum()
                + samples.get("crystal_terra").stream().mapToInt(Integer::intValue).sum();

        check(rows, String.format(Locale.ROOT,
                "natural magical_forest flower density matches isolated 1.12.2 flower stage replay on modern terrain on same 30 chunks (1.21.1=%.2f vs 1.12.2=%.2f, |diff| <= 3.5)",
                flowerMean, legacy1122FlowerMean),
                flowerMean > 0.0 && Math.abs(flowerMean - legacy1122FlowerMean) <= 3.5);
        check(rows, "natural magical_forest grass/fern sanity limits (12x128 attempt budget, 40 <= mean <= 140)",
                grassMean >= 40.0 && grassMean <= 140.0);
        check(rows, "natural magical_forest ore sanity limits (18/18/20 attempt budgets, 11 <= mean <= 20)",
                cinnabarMean >= 11.0 && cinnabarMean <= 18.0
                        && quartzMean >= 11.0 && quartzMean <= 18.0
                        && amberMean >= 11.0 && amberMean <= 20.0);
        check(rows, "natural magical_forest crystal sanity limits (8-attempt budget and Ordo+Terra bias >= 45%)",
                crystalMean >= 1.5 && crystalMean <= 8.0 && totalCrystals > 0 && (totalOrdoTerra * 100.0 / totalCrystals) >= 45.0);

        rows.add("");
        rows.add("| Feature / Block Family | Mean / Chunk | StdDev | Min | Max | Total (" + forestChunks.size() + " Chunks) |");
        rows.add("|---|---:|---:|---:|---:|---:|");
        for (String k : keys) {
            List<Integer> vals = samples.get(k);
            int sum = vals.stream().mapToInt(Integer::intValue).sum();
            int min = vals.stream().mapToInt(Integer::intValue).min().orElse(0);
            int max = vals.stream().mapToInt(Integer::intValue).max().orElse(0);
            double m = mean(vals);
            double sd = stdDev(vals, m);
            rows.add(String.format(Locale.ROOT, "| `%s` | %.2f | %.2f | %d | %d | %d |", k, m, sd, min, max, sum));
        }
        int legSum = legacy1122NaturalFlowerSamples.stream().mapToInt(Integer::intValue).sum();
        int legMin = legacy1122NaturalFlowerSamples.stream().mapToInt(Integer::intValue).min().orElse(0);
        int legMax = legacy1122NaturalFlowerSamples.stream().mapToInt(Integer::intValue).max().orElse(0);
        rows.add(String.format(Locale.ROOT, "| `small_flowers_isolated_1122_stage_replay` | %.2f | %.2f | %d | %d | %d |",
                legacy1122FlowerMean, stdDev(legacy1122NaturalFlowerSamples, legacy1122FlowerMean), legMin, legMax, legSum));
        rows.add("");
    }

    /**
     * Isolated replay of 1.12.2 ChunkGeneratorOverworld.populate flower stage + BiomeDecorator (flowersPerChunk = 10)
     * + WorldGenFlowers (64 probes) on modern terrain across the 3x3 chunk neighborhood (treating small_flowers and
     * post-flower decorations as open air). Does NOT claim full populate replay because preceding stages in 1.12.2
     * also consumed RNG. Uses exact 1.12.2 chunk seed derivation (without idx mix-in) and +8 X/Z offset:
     *   rand.setSeed(worldSeed);
     *   long k = rand.nextLong() / 2L * 2L + 1L;
     *   long l = rand.nextLong() / 2L * 2L + 1L;
     *   rand.setSeed((long)chunkX * k + (long)chunkZ * l ^ worldSeed);
     *   int x = minX + rand.nextInt(16) + 8;
     *   int z = minZ + rand.nextInt(16) + 8;
     */
    private static int simulate1122FlowersOnChunkTerrain(ServerLevel level, ChunkPos cp, long seed) {
        Set<BlockPos> placedInTargetChunk = new HashSet<>();
        java.util.Random seedRand = new java.util.Random(seed);
        long k = seedRand.nextLong() / 2L * 2L + 1L;
        long l = seedRand.nextLong() / 2L * 2L + 1L;

        for (int ncx = cp.x - 1; ncx <= cp.x + 1; ncx++) {
            for (int ncz = cp.z - 1; ncz <= cp.z + 1; ncz++) {
                java.util.Random rand = new java.util.Random(((long) ncx * k + (long) ncz * l) ^ seed);
                int minX = ncx << 4;
                int minZ = ncz << 4;
                for (int patch = 0; patch < 10; patch++) {
                    int x = minX + rand.nextInt(16) + 8;
                    int z = minZ + rand.nextInt(16) + 8;
                    int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                    int yBound = Math.max(1, surfaceY + 32);
                    int y = rand.nextInt(yBound);
                    BlockPos patchOrigin = new BlockPos(x, y, z);
                    BlockState flower = TCLegacyPatchFeature.pickLegacyMagicalForestFlower(patchOrigin);
                    for (int i = 0; i < 64; i++) {
                        int px = patchOrigin.getX() + rand.nextInt(8) - rand.nextInt(8);
                        int py = patchOrigin.getY() + rand.nextInt(4) - rand.nextInt(4);
                        int pz = patchOrigin.getZ() + rand.nextInt(8) - rand.nextInt(8);
                        if ((px >> 4) != cp.x || (pz >> 4) != cp.z) {
                            continue;
                        }
                        BlockPos probe = new BlockPos(px, py, pz);
                        if (!level.isOutsideBuildHeight(probe) && !placedInTargetChunk.contains(probe)) {
                            BlockState existing = level.getBlockState(probe);
                            if (isOpenAtFlowerStage(existing) && flower.canSurvive(level, probe)) {
                                placedInTargetChunk.add(probe);
                            }
                        }
                    }
                }
            }
        }
        return placedInTargetChunk.size();
    }

    private static boolean isOpenAtFlowerStage(BlockState state) {
        if (state.isAir() || state.is(BlockTags.SMALL_FLOWERS)) {
            return true;
        }
        Block b = state.getBlock();
        // In both 1.12.2 BiomeDecorator and 1.21.1 magical_forest.json, grass, ferns, vishrooms, and mushrooms run AFTER flowers.
        return b == Blocks.SHORT_GRASS || b == Blocks.FERN || b == Blocks.TALL_GRASS || b == Blocks.LARGE_FERN
                || b == TCBlocks.VISHROOM.get() || b == TCBlocks.SHIMMERLEAF.get()
                || b == Blocks.BROWN_MUSHROOM || b == Blocks.RED_MUSHROOM;
    }

    private static void runNaturalPortalLifecycleAudit(ServerLevel level, List<String> rows) {
        rows.add("## 3. Natural Lesser Cultist Portal Structure & Natural Lifecycle Verification\n");
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        var holder = registry.getHolderOrThrow(ResourceKey.create(Registries.STRUCTURE,
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "cultist_portal_lesser")));

        int[][] searchOrigins = {{0, 0}, {4096, -4096}, {-4096, 4096}};
        for (int i = 0; i < searchOrigins.length; i++) {
            BlockPos searchFrom = new BlockPos(searchOrigins[i][0], 80, searchOrigins[i][1]);
            var located = level.getChunkSource().getGenerator().findNearestMapStructure(
                    level, HolderSet.direct(holder), searchFrom, 96, false);
            check(rows, "natural cultist_portal_lesser locate " + i, located != null);
            if (located == null) {
                continue;
            }
            BlockPos locatePos = located.getFirst();
            var chunk = level.getChunk(locatePos.getX() >> 4, locatePos.getZ() >> 4);
            var start = chunk.getStartForStructure(holder.value());
            check(rows, "natural cultist_portal_lesser start valid " + i, start != null && start.isValid());
            if (start == null || !start.isValid()) {
                continue;
            }
            BoundingBox box = start.getBoundingBox();
            BlockPos portalOrigin = new BlockPos(box.minX(), box.minY(), box.minZ());
            BlockState groundState = level.getBlockState(portalOrigin.below());
            boolean validGround = portalOrigin.getY() >= level.getSeaLevel()
                    && TCCultistPortalFeature.isValidPortalGround(groundState);
            check(rows, "natural cultist_portal_lesser dry surface ground valid " + i, validGround);

            // Verify completely natural lifecycle on the first located natural portal site
            if (i == 0) {
                int pcx = portalOrigin.getX() >> 4;
                int pcz = portalOrigin.getZ() >> 4;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        level.getChunk(pcx + dx, pcz + dz);
                        level.setChunkForced(pcx + dx, pcz + dz, true);
                    }
                }
                for (int pump = 0; pump < 40; pump++) {
                    while (level.getChunkSource().pollTask()) {
                        // Drain distance manager and chunk holder status promotions
                    }
                    level.getChunkSource().tick(() -> true, true);
                    level.tick(() -> true);
                    if (level.areEntitiesLoaded(ChunkPos.asLong(pcx, pcz))) {
                        break;
                    }
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                }
                while (level.getChunkSource().pollTask()) {
                    // Final drain
                }

                AABB portalBounds = new AABB(portalOrigin).inflate(3.0D);
                List<TCCultistPortalLesserEntity> portals = level.getEntitiesOfClass(
                        TCCultistPortalLesserEntity.class, portalBounds);
                check(rows, "natural portal entity spawned by worldgen at persisted structure origin", !portals.isEmpty());
                if (!portals.isEmpty()) {
                    TCCultistPortalLesserEntity portal = portals.getFirst();
                    float initialHealth = portal.getHealth();
                    boolean initialDormant = !portal.isActive() && portal.isPersistenceRequired();
                    check(rows, "natural portal starts dormant with 100 HP and persistenceRequired",
                            initialDormant && Math.abs(initialHealth - 100.0F) < 1e-3F);

                    ServerPlayer fakePlayer = FakePlayerFactory.getMinecraft(level);
                    fakePlayer.setGameMode(GameType.SURVIVAL);
                    boolean addedPlayer = false;
                    try {
                        // 1. Tick 20 times with no player in range -> remains dormant
                        for (int t = 0; t < 20; t++) {
                            advancePortalTick(portal);
                        }
                        boolean staysDormantWithoutPlayer = !portal.isActive() && portal.spawnedMinionCountForValidation() == 0;

                        // 2. Place player at 40 blocks (> 32-block radius) -> remains dormant
                        fakePlayer.setPos(portal.getX() + 40.0D, portal.getY(), portal.getZ());
                        if (!level.players().contains(fakePlayer)) {
                            level.players().add(fakePlayer);
                            addedPlayer = true;
                        }
                        for (int t = 0; t < 20; t++) {
                            advancePortalTick(portal);
                        }
                        boolean staysDormantBeyond32Blocks = !portal.isActive() && portal.spawnedMinionCountForValidation() == 0;
                        check(rows, "natural portal stays dormant without player or when player is outside 32-block radius",
                                staysDormantWithoutPlayer && staysDormantBeyond32Blocks);

                        // 3. Move player inside 32-block radius with unobstructed line of sight and tick up to 10 ticks
                        fakePlayer.setPos(portal.getX() + 0.5D, portal.getY(), portal.getZ() + 0.5D);
                        int ticksToActivate = 0;
                        for (int t = 1; t <= 10 && !portal.isActive(); t++) {
                            advancePortalTick(portal);
                            ticksToActivate = t;
                        }
                        check(rows, "natural portal activates via tickLegacyActivation when player enters 32-block radius on tickCount % 10 == 0",
                                portal.isActive() && ticksToActivate >= 1 && ticksToActivate <= 10 && (portal.tickCount % 10 == 0));

                        // 4. Verify initial 100-tick stageCounter countdown before Wave 1 spawns
                        boolean noPrematureSpawn = true;
                        for (int t = 1; t <= 100; t++) {
                            advancePortalTick(portal);
                            if (portal.spawnedMinionCountForValidation() != 0) {
                                noPrematureSpawn = false;
                            }
                        }
                        // On active tick 101, stageCounter-- <= 0 triggers Wave 1
                        advancePortalTick(portal);
                        boolean wave1SpawnedOnTick101 = noPrematureSpawn && portal.spawnedMinionCountForValidation() == 1;
                        float healthAfterWave1 = portal.getHealth();
                        check(rows, "natural portal waits 100 active ticks before spawning Wave 1 on tick 101 with 5..9 self-damage",
                                wave1SpawnedOnTick101 && healthAfterWave1 <= initialHealth - 5.0F && healthAfterWave1 >= initialHealth - 9.0F);

                        // 5. Verify natural 50..100 tick wave interval and nearby-cultist cap (cultistMinionBudget)
                        int budget = portal.cultistMinionBudget();
                        List<Integer> waveIntervals = new ArrayList<>();
                        int ticksSinceLastWave = 0;
                        int lastWaveCount = portal.spawnedMinionCountForValidation();
                        while (portal.spawnedMinionCountForValidation() < budget && ticksSinceLastWave < 120) {
                            advancePortalTick(portal);
                            ticksSinceLastWave++;
                            if (portal.spawnedMinionCountForValidation() > lastWaveCount) {
                                waveIntervals.add(ticksSinceLastWave);
                                ticksSinceLastWave = 0;
                                lastWaveCount = portal.spawnedMinionCountForValidation();
                            }
                        }
                        // With living cultists at budget cap, ticking another 110 ticks must spawn 0 additional minions
                        int countAtCap = portal.spawnedMinionCountForValidation();
                        for (int t = 0; t < 110; t++) {
                            advancePortalTick(portal);
                        }
                        boolean capEnforced = (countAtCap == budget) && (portal.spawnedMinionCountForValidation() == budget);

                        // Verify spawned minions in world have 32-block legacyHome at portal
                        AABB minionBox = new AABB(portalOrigin).inflate(36.0D);
                        List<TCCultistEntity> activeMinions = level.getEntitiesOfClass(TCCultistEntity.class, minionBox);
                        boolean minionsHomeValid = !activeMinions.isEmpty() && activeMinions.stream().allMatch(m ->
                                portal.blockPosition().equals(m.legacyHomePosForValidation())
                                        && m.legacyHomeDistanceForValidation() == 32);

                        // Slay/clear minions and let portal naturally tick through remaining waves until self-damage collapses it
                        boolean sawKnight = activeMinions.stream().anyMatch(m -> m instanceof TCCultistKnightEntity);
                        boolean sawCleric = activeMinions.stream().anyMatch(m -> m instanceof TCCultistClericEntity);
                        activeMinions.forEach(e -> e.discard());

                        // First tick until the next wave after clearing cap
                        int postCapWait = 0;
                        while (portal.isAlive() && portal.spawnedMinionCountForValidation() == lastWaveCount && postCapWait < 120) {
                            advancePortalTick(portal);
                            postCapWait++;
                        }
                        lastWaveCount = portal.spawnedMinionCountForValidation();
                        for (TCCultistEntity m : level.getEntitiesOfClass(TCCultistEntity.class, minionBox)) {
                            sawKnight |= (m instanceof TCCultistKnightEntity);
                            sawCleric |= (m instanceof TCCultistClericEntity);
                            m.discard();
                        }

                        ticksSinceLastWave = 0;
                        int safetyTicks = 0;
                        while (portal.isAlive() && safetyTicks < 3000) {
                            advancePortalTick(portal);
                            ticksSinceLastWave++;
                            safetyTicks++;
                            if (portal.spawnedMinionCountForValidation() > lastWaveCount) {
                                waveIntervals.add(ticksSinceLastWave);
                                ticksSinceLastWave = 0;
                                lastWaveCount = portal.spawnedMinionCountForValidation();
                                for (TCCultistEntity m : level.getEntitiesOfClass(TCCultistEntity.class, minionBox)) {
                                    sawKnight |= (m instanceof TCCultistKnightEntity);
                                    sawCleric |= (m instanceof TCCultistClericEntity);
                                    m.discard();
                                }
                            }
                        }

                        boolean intervalsValid = !waveIntervals.isEmpty()
                                && waveIntervals.stream().allMatch(dt -> dt >= 50 && dt <= 100);
                        int minInterval = waveIntervals.stream().mapToInt(Integer::intValue).min().orElse(0);
                        int maxInterval = waveIntervals.stream().mapToInt(Integer::intValue).max().orElse(0);
                        int totalWaves = portal.spawnedMinionCountForValidation();

                        check(rows, "natural portal wave intervals strictly follow 50..100 tick cadence and enforce nearby cultist cap",
                                intervalsValid && capEnforced);
                        check(rows, "natural portal spawns cultists with 32-block legacyHome and collapses naturally after exhausting HP",
                                minionsHomeValid && sawKnight && sawCleric && !portal.isAlive() && totalWaves >= 12 && totalWaves <= 22);

                        rows.add(String.format(Locale.ROOT,
                                "  - Natural lifecycle evidence at `%s`: activateTicks=%d, wave1Tick=101, waveIntervals=%d..%d ticks (%d measured), cap=%d, totalWavesToCollapse=%d, sawKnight=%s, sawCleric=%s",
                                portalOrigin, ticksToActivate, minInterval, maxInterval, waveIntervals.size(), budget, totalWaves, sawKnight, sawCleric));
                    } finally {
                        if (addedPlayer) {
                            level.players().remove(fakePlayer);
                        }
                        AABB searchBox = new AABB(portalOrigin).inflate(36.0D);
                        level.getEntitiesOfClass(TCCultistEntity.class, searchBox).forEach(e -> e.discard());
                        portal.discard();
                    }
                }
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        level.setChunkForced(pcx + dx, pcz + dz, false);
                    }
                }
            }
            rows.add(String.format(Locale.ROOT, "  - Located portal %d: locate=`%s`, origin=`%s`, ground=`%s`",
                    i, locatePos, portalOrigin, groundState.getBlock()));
        }
    }

    private static void advancePortalTick(TCCultistPortalLesserEntity portal) {
        portal.tickCount++;
        portal.tick();
    }

    private static double mean(List<Integer> values) {
        return values.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    private static double stdDev(List<Integer> values, double mean) {
        if (values.isEmpty()) {
            return 0.0;
        }
        double sumSq = 0.0;
        for (int v : values) {
            double d = v - mean;
            sumSq += d * d;
        }
        return Math.sqrt(sumSq / values.size());
    }

    private static void check(List<String> rows, String name, boolean pass) {
        rows.add("- " + (pass ? "PASS" : "FAIL") + " " + name);
        Thaumcraft.LOGGER.info("Gate 11 worldgen check {}: {}", name, pass ? "PASS" : "FAIL");
    }
}
