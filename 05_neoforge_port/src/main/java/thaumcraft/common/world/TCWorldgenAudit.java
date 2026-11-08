package thaumcraft.common.world;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.world.features.TCCrystalClusterFeature;
import thaumcraft.common.world.features.TCMoundFeature;
import thaumcraft.common.world.structures.TCMoundStructure;

/** Opt-in destructive tests, refused outside disposable tc_worldgen_audit_* saves. */
public final class TCWorldgenAudit {
    private TCWorldgenAudit() {}

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.getBoolean("tc.worldgenAudit")) return;
        var server = event.getServer();
        Path world = server.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize();
        if (!world.getFileName().toString().startsWith("tc_worldgen_audit_"))
            throw new IllegalStateException("Worldgen audit requires a disposable tc_worldgen_audit_* save");
        List<String> rows = new ArrayList<>();
        try {
            ServerLevel level = server.overworld();
            String runId = System.getProperty("tc.worldgenAuditRunId", "untracked");
            String manifestHash = System.getProperty("tc.worldgenAuditManifest", "untracked");
            rows.add("Run ID: `" + runId + "`");
            rows.add("Manifest Fingerprint: `" + manifestHash + "`");
            rows.add("Seed: `" + level.getSeed() + "`; world: `" + world.getFileName() + "`\n");
            check(rows, "uniform-cube replacement removed", samplingChecks());
            check(rows, "night light gate: 9 of 13 rolls", lightChecks());
            check(rows, "flower count/anchor/duplicate contract", json("placed_feature/flower_magical_forest").getAsJsonArray("placement")
                    .get(0).getAsJsonObject().get("count").getAsInt() == 10
                    && json("configured_feature/flower_magical_forest").get("type").getAsString().equals("thaumcraft:legacy_flowers")
                    && !json("biome/magical_forest").toString().contains("minecraft:forest_flowers"));
            for (String ore : List.of("amber", "cinnabar", "quartz"))
                check(rows, "single block ore: " + ore, json("configured_feature/ore_" + ore).get("type").getAsString()
                        .equals(ore.equals("amber") ? "thaumcraft:surface_amber_ore" : "minecraft:replace_single_block"));
            check(rows, "amber uses surface-relative offset", json("placed_feature/ore_amber").toString().contains("minecraft:heightmap")
                    && !json("placed_feature/ore_amber").toString().contains("height_range"));
            check(rows, "crystal budget is one eight-attempt batch", json("placed_feature/crystal_cluster").getAsJsonArray("placement")
                    .get(0).getAsJsonObject().get("count").getAsInt() == 1);
            var savanna = level.registryAccess().registryOrThrow(Registries.BIOME)
                    .getHolderOrThrow(net.minecraft.world.level.biome.Biomes.SAVANNA);
            boolean savannaOk = true;
            RandomSource random = RandomSource.create(314);
            for (int i = 0; i < 1000; i++) savannaOk &= TCCrystalClusterFeature.getBiomeAspect(savanna, random) != Aspect.ORDER;
            check(rows, "hot savanna is not classified as snowy Ordo", savannaOk);
            flowerCalibration(level, rows);
            sliceChecks(level, rows);
            int[][] origins = {{0, 0}, {2048, -2048}, {-2048, 2048}};
            for (int i = 0; i < origins.length; i++) naturalMound(level, rows, origins[i][0], origins[i][1], i);
        } catch (Exception e) {
            rows.add("- FAIL exception: " + e + "\n");
            Thaumcraft.LOGGER.error("Worldgen audit exception", e);
        } finally {
            boolean pass = rows.stream().noneMatch(s -> s.contains("FAIL"));
            String report = "# Worldgen Runtime Audit\n\n" + String.join("\n", rows)
                    + "\n\nResult: **" + (pass ? "PASS" : "FAIL") + "**\n";
            try {
                Path out = Path.of(System.getProperty("tc.worldgenAuditPath", "worldgen-audit.md")).toAbsolutePath();
                Files.createDirectories(out.getParent());
                Files.writeString(out, report);
                Thaumcraft.LOGGER.info("TC_WORLDGEN_AUDIT_{} report={}", pass ? "PASS" : "FAIL", out);
            } catch (Exception e) { throw new IllegalStateException("Cannot write worldgen audit evidence", e); }
            finally { server.halt(false); }
        }
    }

    private static boolean samplingChecks() {
        for (int r : new int[] {16, 32}) {
            double sum = 0;
            for (int x = -r; x <= r; x++) for (int y = -r; y <= r; y++) for (int z = -r; z <= r; z++)
                sum += TCForestMoteSampling.positionProbability(x, y, z, r);
            if (Math.abs(sum - 1) > 1e-10) return false;
        }
        RandomSource random = RandomSource.create(61222121);
        int count = 0, trials = 100000;
        double p = TCForestMoteSampling.positionProbability(0, -1, 0, 16);
        for (int i = 0; i < trials; i++) count += TCForestMoteSampling.triggers(random, p);
        double expected = trials * 667 * p;
        return Math.abs(count - expected) < 6 * Math.sqrt(expected * (1 - p));
    }

    private static boolean lightChecks() {
        int night = 0, day = 0, roof = 0;
        for (int roll = 0; roll < 13; roll++) {
            if (TCForestMoteSampling.passesLightGate(TCForestMoteSampling.adjustedLight(15, 11, (float) Math.PI), roll)) night++;
            if (TCForestMoteSampling.passesLightGate(TCForestMoteSampling.adjustedLight(15, 0, 0), roll)) day++;
            if (TCForestMoteSampling.passesLightGate(TCForestMoteSampling.adjustedLight(0, 11, (float) Math.PI), roll)) roof++;
        }
        return night == 9 && day == 0 && roof == 0;
    }

    private static void sliceChecks(ServerLevel level, List<String> rows) {
        var piece = new TCMoundStructure.Piece(260, 90, 260, 1234);
        var restored = new TCMoundStructure.Piece(piece.createTag(StructurePieceSerializationContext.fromLevel(level)));
        BoundingBox bounds = piece.getBoundingBox();
        check(rows, "piece NBT retains real 19x17x19 bounds", bounds.getXSpan() == 19 && bounds.getYSpan() == 17
                && bounds.getZSpan() == 19 && bounds.equals(restored.getBoundingBox()));
        for (int cx = bounds.minX() >> 4; cx <= bounds.maxX() >> 4; cx++)
            for (int cz = bounds.minZ() >> 4; cz <= bounds.maxZ() >> 4; cz++) level.getChunk(cx, cz);
        BoundingBox clip = new BoundingBox(256, level.getMinBuildHeight(), 256, 271, level.getMaxBuildHeight() - 1, 271);
        BlockPos sentinel = new BlockPos(272, 90, 260);
        level.setBlock(sentinel, Blocks.GOLD_BLOCK.defaultBlockState(), 2);
        piece.postProcess(level, level.structureManager(), level.getChunkSource().getGenerator(), RandomSource.create(1), clip,
                new ChunkPos(16, 16), BlockPos.ZERO);
        check(rows, "chunk clipping preserves adjacent sentinel", level.getBlockState(sentinel).is(Blocks.GOLD_BLOCK));
        Map<BlockPos, BlockState> first = new HashMap<>();
        for (BlockPos p : BlockPos.betweenClosed(260, 81, 260, 271, 97, 271)) first.put(p.immutable(), level.getBlockState(p));
        restored.postProcess(level, level.structureManager(), level.getChunkSource().getGenerator(), RandomSource.create(999), clip,
                new ChunkPos(16, 16), BlockPos.ZERO);
        check(rows, "slice output independent of caller RNG and NBT reload", first.entrySet().stream()
                .allMatch(e -> e.getValue().equals(level.getBlockState(e.getKey()))));
    }

    private static void flowerCalibration(ServerLevel level, List<String> rows) {
        var generator = level.getChunkSource().getGenerator();
        var configured = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                .get(ResourceLocation.fromNamespaceAndPath("thaumcraft", "flower_magical_forest"));
        var config = (net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration) configured.config();
        var oldPatch = new net.minecraft.world.level.levelgen.feature.RandomPatchFeature(
                net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration.CODEC);
        // Finish every neighboring feature origin before editing the control plot.
        for (int cx = 31; cx <= 34; cx++) for (int cz = 31; cz <= 34; cz++) level.getChunk(cx, cz);
        int ground = 80;
        if (ground + 36 >= level.getMaxBuildHeight()) throw new IllegalStateException("No calibration headroom");
        // Clear the entire sampled column, not just the visible plot: hidden flowers below it change RNG consumption.
        for (int x = 520; x <= 536; x++) for (int z = 520; z <= 536; z++) {
            for (int y = 0; y < level.getMaxBuildHeight(); y++)
                level.setBlock(new BlockPos(x, y, z), y == ground ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.AIR.defaultBlockState(), 2);
        }
        BlockPos origin = new BlockPos(528, ground + 1, 528);
        int[] totals = new int[3];
        for (int mode = 0; mode < 3; mode++) {
            for (int seed = 0; seed < 30; seed++) {
                for (int x = 520; x <= 536; x++) for (int z = 520; z <= 536; z++)
                    for (int y = ground + 1; y <= ground + 4; y++)
                        level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
                RandomSource random = RandomSource.create(seed + 61222121);
                for (int n = 0; n < (mode == 1 ? 6 : 10); n++) {
                    if (mode == 0) configured.place(level, generator, random, origin);
                    else if (mode == 1) oldPatch.place(config, level, generator, random, origin);
                    else place1122ReferenceFlowerPatch(level, random, origin);
                }
                for (int x = 520; x <= 536; x++) for (int z = 520; z <= 536; z++)
                    if (level.getBlockState(new BlockPos(x, ground + 1, z)).is(net.minecraft.tags.BlockTags.SMALL_FLOWERS)) totals[mode]++;
            }
        }
        check(rows, "real flower anchor regression: matches 1.12.2 WorldGenFlowers and old surface patches are denser",
                totals[0] > 0 && totals[0] == totals[2] && totals[1] > totals[0] * 4);
        rows.add("  Controlled flat plots (30 seeds, same central X/Z; NOT natural biome density): new=" + totals[0]
                + "/30, legacy1122=" + totals[2] + "/30, previous surface-anchored=" + totals[1] + "/30; groundY=" + ground + ".");
    }

    private static final net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise INDEPENDENT_1122_GRASS_COLOR_NOISE =
            new net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise(
                    new net.minecraft.world.level.levelgen.WorldgenRandom(new net.minecraft.world.level.levelgen.LegacyRandomSource(2345L)),
                    List.of(0));

    private static final BlockState[] INDEPENDENT_1122_FLOWER_STATES = {
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

    private static BlockState independent1122PickFlower(BlockPos pos) {
        double d0 = net.minecraft.util.Mth.clamp(
                (1.0D + INDEPENDENT_1122_GRASS_COLOR_NOISE.getValue(pos.getX() / 48.0D, pos.getZ() / 48.0D, false)) / 2.0D,
                0.0D, 0.9999D);
        return INDEPENDENT_1122_FLOWER_STATES[(int) (d0 * INDEPENDENT_1122_FLOWER_STATES.length)];
    }

    private static void place1122ReferenceFlowerPatch(ServerLevel level, RandomSource random, BlockPos surfaceOrigin) {
        BlockPos patchOrigin = surfaceOrigin.atY(random.nextInt(Math.max(1, surfaceOrigin.getY() + 32)));
        BlockState flower = independent1122PickFlower(patchOrigin);
        for (int i = 0; i < 64; i++) {
            BlockPos probe = patchOrigin.offset(
                    random.nextInt(8) - random.nextInt(8),
                    random.nextInt(4) - random.nextInt(4),
                    random.nextInt(8) - random.nextInt(8)
            );
            if (!level.isOutsideBuildHeight(probe) && level.isEmptyBlock(probe) && flower.canSurvive(level, probe)) {
                level.setBlock(probe, flower, 2);
            }
        }
    }

    private static void naturalMound(ServerLevel level, List<String> rows, int x, int z, int index) {
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        var holder = registry.getHolderOrThrow(ResourceKey.create(Registries.STRUCTURE,
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "mound")));
        long started = System.nanoTime();
        var located = level.getChunkSource().getGenerator().findNearestMapStructure(level, HolderSet.direct(holder),
                new BlockPos(x, 80, z), 64, false);
        check(rows, "natural locate " + index, located != null);
        if (located == null) return;
        var pos = located.getFirst();
        Thaumcraft.LOGGER.info("Worldgen audit loading mound {} at {}", index, pos);
        var chunk = level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        var start = chunk.getStartForStructure(holder.value());
        check(rows, "located start exists " + index, start != null && start.isValid());
        if (start == null || !start.isValid()) return;
        BoundingBox box = start.getBoundingBox();
        check(rows, "locate X/Z is inside real mound bounds " + index,
                box.isInside(new BlockPos(pos.getX(), box.minY(), pos.getZ())));
        for (int cx = box.minX() >> 4; cx <= box.maxX() >> 4; cx++)
            for (int cz = box.minZ() >> 4; cz <= box.maxZ() >> 4; cz++) level.getChunk(cx, cz);
        // FULL is not BLOCK_TICKING: off-player server chunks need the same finalization vanilla runs on activation.
        for (int cx = box.minX() >> 4; cx <= box.maxX() >> 4; cx++)
            for (int cz = box.minZ() >> 4; cz <= box.maxZ() >> 4; cz++) level.getChunk(cx, cz).postProcessGeneration();
        int spawners = 0, bars = 0, connected = 0;
        for (BlockPos p : BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ())) {
            BlockState state = level.getBlockState(p);
            if (state.is(Blocks.SPAWNER)) spawners++;
            if (state.is(Blocks.IRON_BARS)) {
                bars++;
                if (state.getValue(IronBarsBlock.NORTH) || state.getValue(IronBarsBlock.SOUTH)
                        || state.getValue(IronBarsBlock.WEST) || state.getValue(IronBarsBlock.EAST)) connected++;
            }
        }
        check(rows, "natural mound physically present across chunks " + index, spawners >= 2 && bars > 0);
        check(rows, "natural bars have postprocessed connections " + index, connected == bars && bars > 0);
        rows.add("  Evidence: locate=" + pos + ", bounds=" + box + ", spawners=" + spawners + ", bars=" + bars
                + ", connected=" + connected + ", elapsedMs=" + (System.nanoTime() - started) / 1000000 + ".");
    }

    private static JsonObject json(String path) throws Exception {
        String resource = "/data/thaumcraft/worldgen/" + path + ".json";
        try (var stream = TCWorldgenAudit.class.getResourceAsStream(resource)) {
            if (stream == null) throw new IllegalStateException("Missing " + resource);
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    private static void check(List<String> rows, String name, boolean pass) {
        rows.add("- " + (pass ? "PASS" : "FAIL") + " " + name);
        Thaumcraft.LOGGER.info("Worldgen check {}: {}", name, pass ? "PASS" : "FAIL");
    }
}
