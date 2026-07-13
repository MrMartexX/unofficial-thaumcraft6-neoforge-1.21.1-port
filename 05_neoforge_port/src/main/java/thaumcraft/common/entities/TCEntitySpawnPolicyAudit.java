package thaumcraft.common.entities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import thaumcraft.common.registry.TCEntityTypes;

public final class TCEntitySpawnPolicyAudit {
    public static final String ENABLE_PROPERTY = "tc.entitySpawnPolicyAudit";
    public static final String OUTPUT_PROPERTY = "tc.entitySpawnPolicyAuditPath";

    private TCEntitySpawnPolicyAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        if (output.getParent() != null) {
            Files.createDirectories(output.getParent());
        }
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Thaumcraft Entity Spawn Policy Audit");
        lines.add("");
        lines.add("Runtime checks for legacy natural-spawn rows ported to NeoForge 1.21.1.");
        lines.add("Only rows whose entities and server-side behavior have a registered foundation are active:");
        lines.add("Wisp Nether and Angry Zombie overworld. Thaumcraft-biome rows and unported mob families stay deferred.");
        lines.add("");
        lines.add("## Summary");
        lines.add("");
        lines.add("| Check | Result |");
        lines.add("|---|---:|");
        lines.add("| Passed | " + report.passed() + " |");
        lines.add("| Failed | " + report.failed() + " |");
        lines.add("");
        lines.add("## Checks");
        lines.add("");
        lines.add("| Name | Result | Notes |");
        lines.add("|---|---|---|");
        for (Check check : report.checks()) {
            lines.add("| " + check.name() + " | " + (check.passed() ? "PASS" : "FAIL")
                    + " | " + escape(check.notes()) + " |");
        }
        lines.add("");
        lines.add("## Active legacy natural spawns");
        lines.add("");
        lines.add("| Legacy id | Class | Modern entity | Biome selector | Weight | Min | Max | Notes |");
        lines.add("|---|---|---|---|---:|---:|---:|---|");
        for (TCEntitySpawnRules.LegacyNaturalSpawn spawn : TCEntitySpawnRules.activeNaturalSpawns()) {
            lines.add(spawnRow(spawn));
        }
        lines.add("");
        lines.add("## Deferred legacy natural spawns");
        lines.add("");
        lines.add("| Legacy id | Class | Modern entity | Biome selector | Weight | Min | Max | Notes |");
        lines.add("|---|---|---|---|---:|---:|---:|---|");
        for (TCEntitySpawnRules.LegacyNaturalSpawn spawn : TCEntitySpawnRules.deferredNaturalSpawns()) {
            lines.add(spawnRow(spawn));
        }
        Files.write(output, lines, StandardCharsets.UTF_8);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.getLevel(Level.NETHER);
        if (level == null) {
            level = server.overworld();
        }
        BlockPos origin = new BlockPos(16, level.getMinBuildHeight() + 48, 16);
        Difficulty originalDifficulty = level.getDifficulty();
        cleanup(level, origin);
        try {
            server.setDifficulty(Difficulty.NORMAL, true);
            prepareDarkSpawnCell(level, origin);
            addMetadataChecks(checks);
            addBiomeModifierChecks(checks);
            addPlacementRegistrationChecks(checks);
            addPredicateChecks(level, origin, server, checks);
        } finally {
            server.setDifficulty(originalDifficulty, true);
            cleanup(level, origin);
        }
        return new Report(List.copyOf(checks));
    }

    private static void addMetadataChecks(ArrayList<Check> checks) {
        TCEntitySpawnRules.LegacyNaturalSpawn wisp = TCEntitySpawnRules.WISP_NETHER;
        TCEntitySpawnRules.LegacyNaturalSpawn brainy = TCEntitySpawnRules.BRAINY_ZOMBIE_OVERWORLD;
        checks.add(check("active_spawn_catalog_contains_safe_wisp_and_brainy_rows",
                TCEntitySpawnRules.activeNaturalSpawns().size() == 2
                        && TCEntitySpawnRules.activeNaturalSpawns().contains(wisp)
                        && TCEntitySpawnRules.activeNaturalSpawns().contains(brainy)
                        && wisp.active()
                        && "Wisp".equals(wisp.legacyId())
                        && "thaumcraft:wisp".equals(wisp.modernEntityId())
                        && "#minecraft:is_nether".equals(wisp.biomeSelector())
                        && wisp.weight() == 5
                        && wisp.minCount() == 1
                        && wisp.maxCount() == 1
                        && brainy.active()
                        && "BrainyZombie".equals(brainy.legacyId())
                        && "thaumcraft:brainy_zombie".equals(brainy.modernEntityId())
                        && "#thaumcraft:legacy_angry_zombie_spawn_biomes".equals(brainy.biomeSelector())
                        && brainy.weight() == 10
                        && brainy.minCount() == 1
                        && brainy.maxCount() == 1,
                "active=" + TCEntitySpawnRules.activeNaturalSpawns().size() + ", wisp=" + wisp + ", brainy=" + brainy));
        checks.add(check("unsafe_legacy_spawn_rows_remain_deferred",
                TCEntitySpawnRules.deferredNaturalSpawns().stream().noneMatch(TCEntitySpawnRules.LegacyNaturalSpawn::active)
                        && TCEntitySpawnRules.deferredNaturalSpawns().size() >= 8,
                "deferred=" + TCEntitySpawnRules.deferredNaturalSpawns().size()));
    }

    private static void addBiomeModifierChecks(ArrayList<Check> checks) {
        String resource = resourceText("data/thaumcraft/neoforge/biome_modifier/wisp_nether_spawns.json");
        checks.add(check("wisp_nether_biome_modifier_resource_exists",
                !resource.isBlank(),
                "data/thaumcraft/neoforge/biome_modifier/wisp_nether_spawns.json"));
        if (resource.isBlank()) {
            checks.add(check("wisp_nether_biome_modifier_matches_legacy_values", false, "resource missing"));
            return;
        }
        JsonObject root = JsonParser.parseString(resource).getAsJsonObject();
        JsonObject spawner = root.getAsJsonObject("spawners");
        boolean passed = "neoforge:add_spawns".equals(root.get("type").getAsString())
                && "#minecraft:is_nether".equals(root.get("biomes").getAsString())
                && "thaumcraft:wisp".equals(spawner.get("type").getAsString())
                && spawner.get("weight").getAsInt() == 5
                && spawner.get("minCount").getAsInt() == 1
                && spawner.get("maxCount").getAsInt() == 1;
        checks.add(check("wisp_nether_biome_modifier_matches_legacy_values",
                passed,
                "type=" + root.get("type").getAsString() + ", biome=" + root.get("biomes").getAsString() + ", spawner=" + spawner));

        String brainyResource = resourceText("data/thaumcraft/neoforge/biome_modifier/brainy_zombie_legacy_overworld_spawns.json");
        checks.add(check("brainy_zombie_biome_modifier_resource_exists",
                !brainyResource.isBlank(),
                "data/thaumcraft/neoforge/biome_modifier/brainy_zombie_legacy_overworld_spawns.json"));
        if (!brainyResource.isBlank()) {
            JsonObject brainyRoot = JsonParser.parseString(brainyResource).getAsJsonObject();
            JsonObject brainySpawner = brainyRoot.getAsJsonObject("spawners");
            boolean brainyPassed = "neoforge:add_spawns".equals(brainyRoot.get("type").getAsString())
                    && "#thaumcraft:legacy_angry_zombie_spawn_biomes".equals(brainyRoot.get("biomes").getAsString())
                    && "thaumcraft:brainy_zombie".equals(brainySpawner.get("type").getAsString())
                    && brainySpawner.get("weight").getAsInt() == 10
                    && brainySpawner.get("minCount").getAsInt() == 1
                    && brainySpawner.get("maxCount").getAsInt() == 1;
            checks.add(check("brainy_zombie_biome_modifier_matches_legacy_values",
                    brainyPassed,
                    "type=" + brainyRoot.get("type").getAsString() + ", biome=" + brainyRoot.get("biomes").getAsString() + ", spawner=" + brainySpawner));
        } else {
            checks.add(check("brainy_zombie_biome_modifier_matches_legacy_values", false, "resource missing"));
        }

        String brainyTag = resourceText("data/thaumcraft/tags/worldgen/biome/legacy_angry_zombie_spawn_biomes.json");
        checks.add(check("brainy_zombie_legacy_biome_tag_resource_exists",
                !brainyTag.isBlank() && brainyTag.contains("\"minecraft:desert\"") && brainyTag.contains("\"minecraft:snowy_plains\""),
                "data/thaumcraft/tags/worldgen/biome/legacy_angry_zombie_spawn_biomes.json"));
    }

    private static void addPlacementRegistrationChecks(ArrayList<Check> checks) {
        checks.add(check("wisp_spawn_placement_registered",
                SpawnPlacements.hasPlacement(TCEntityTypes.WISP.get())
                        && SpawnPlacements.getPlacementType(TCEntityTypes.WISP.get()) == SpawnPlacementTypes.NO_RESTRICTIONS
                        && SpawnPlacements.getHeightmapType(TCEntityTypes.WISP.get()) == TCEntitySpawnRules.WISP_HEIGHTMAP_TYPE,
                "placement=" + SpawnPlacements.getPlacementType(TCEntityTypes.WISP.get())
                        + ", heightmap=" + SpawnPlacements.getHeightmapType(TCEntityTypes.WISP.get())));
        checks.add(check("brainy_zombie_spawn_placement_registered",
                SpawnPlacements.hasPlacement(TCEntityTypes.BRAINY_ZOMBIE.get())
                        && SpawnPlacements.getPlacementType(TCEntityTypes.BRAINY_ZOMBIE.get()) == SpawnPlacementTypes.ON_GROUND
                        && SpawnPlacements.getHeightmapType(TCEntityTypes.BRAINY_ZOMBIE.get()) == TCEntitySpawnRules.BRAINY_ZOMBIE_HEIGHTMAP_TYPE,
                "placement=" + SpawnPlacements.getPlacementType(TCEntityTypes.BRAINY_ZOMBIE.get())
                        + ", heightmap=" + SpawnPlacements.getHeightmapType(TCEntityTypes.BRAINY_ZOMBIE.get())));
    }

    private static void addPredicateChecks(ServerLevel level, BlockPos origin, MinecraftServer server, ArrayList<Check> checks) {
        boolean darkAllowed = checkWisp(level, origin, 1L);
        checks.add(check("wisp_spawn_predicate_allows_dark_unobstructed_normal_cell",
                darkAllowed,
                "pos=" + origin + ", brightness=" + level.getMaxLocalRawBrightness(origin)));

        level.setBlock(origin, Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        boolean collisionDenied = !checkWisp(level, origin, 2L);
        checks.add(check("wisp_spawn_predicate_denies_obstructed_cell",
                collisionDenied,
                "block=" + level.getBlockState(origin)));
        level.setBlock(origin, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

        boolean brightDenied = !TCWispEntity.testLegacySpawnGatesForValidation(
                Difficulty.NORMAL,
                true,
                true,
                0,
                0,
                15,
                31,
                7
        );
        checks.add(check("wisp_spawn_gates_deny_bright_cell_like_legacy_light_check",
                brightDenied,
                "localRawBrightness=15, blockRoll=7"));

        boolean capDenied = !TCWispEntity.testLegacySpawnGatesForValidation(
                Difficulty.NORMAL,
                true,
                true,
                8,
                0,
                0,
                31,
                7
        );
        checks.add(check("wisp_spawn_gates_deny_legacy_local_cap_at_eight",
                capDenied,
                "nearby=8"));

        Difficulty previous = level.getDifficulty();
        server.setDifficulty(Difficulty.PEACEFUL, true);
        boolean peacefulDenied = !checkWisp(level, origin, 5L);
        checks.add(check("wisp_spawn_predicate_denies_peaceful",
                peacefulDenied,
                "difficulty=" + level.getDifficulty()));
        server.setDifficulty(previous, true);

        prepareDarkSpawnCell(level, origin);
        boolean brainyAllowed = checkBrainy(level, origin, 11L);
        checks.add(check("brainy_zombie_spawn_predicate_allows_dark_ground_cell",
                brainyAllowed,
                "pos=" + origin + ", brightness=" + level.getMaxLocalRawBrightness(origin)));

        boolean brainyConfigDenied = !TCBrainyZombieEntity.testLegacySpawnGatesForValidation(false, Difficulty.NORMAL, true);
        checks.add(check("brainy_zombie_spawn_gate_denies_config_disabled",
                brainyConfigDenied,
                "allowSpawnAngryZombie=false"));

        server.setDifficulty(Difficulty.PEACEFUL, true);
        boolean brainyPeacefulDenied = !checkBrainy(level, origin, 12L);
        checks.add(check("brainy_zombie_spawn_predicate_denies_peaceful",
                brainyPeacefulDenied,
                "difficulty=" + level.getDifficulty()));
        server.setDifficulty(previous, true);
    }

    private static boolean checkWisp(ServerLevel level, BlockPos pos, long seed) {
        return SpawnPlacements.checkSpawnRules(
                TCEntityTypes.WISP.get(),
                level,
                MobSpawnType.NATURAL,
                pos,
                RandomSource.create(seed)
        );
    }

    private static boolean checkBrainy(ServerLevel level, BlockPos pos, long seed) {
        return SpawnPlacements.checkSpawnRules(
                TCEntityTypes.BRAINY_ZOMBIE.get(),
                level,
                MobSpawnType.NATURAL,
                pos,
                RandomSource.create(seed)
        );
    }

    private static void prepareDarkSpawnCell(ServerLevel level, BlockPos origin) {
        BlockPos.betweenClosed(origin.offset(-3, -2, -3), origin.offset(3, 4, 3)).forEach(pos ->
                level.setBlock(pos, Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL));
        BlockPos.betweenClosed(origin.offset(-2, -1, -2), origin.offset(2, 2, 2)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
        level.setBlock(origin.below(), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(origin.above(3), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        level.getEntities(null, new AABB(origin).inflate(48.0D)).forEach(Entity::discard);
        BlockPos.betweenClosed(origin.offset(-4, -3, -4), origin.offset(4, 5, 4)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
    }

    private static String resourceText(String path) {
        try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return "";
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return "";
        }
    }

    private static String spawnRow(TCEntitySpawnRules.LegacyNaturalSpawn spawn) {
        return "| " + spawn.legacyId()
                + " | " + spawn.legacyClass()
                + " | " + (spawn.modernEntityId() == null ? "" : spawn.modernEntityId())
                + " | " + escape(spawn.biomeSelector())
                + " | " + spawn.weight()
                + " | " + spawn.minCount()
                + " | " + spawn.maxCount()
                + " | " + escape(spawn.notes())
                + " |";
    }

    private static Check check(String name, boolean passed, String notes) {
        return new Check(name, passed, notes);
    }

    private static String escape(String value) {
        return value.replace("|", "\\|");
    }

    public record Check(String name, boolean passed, String notes) {
    }

    public record Report(List<Check> checks) {
        public int passed() {
            return (int) checks.stream().filter(Check::passed).count();
        }

        public int failed() {
            return checks.size() - passed();
        }
    }
}
