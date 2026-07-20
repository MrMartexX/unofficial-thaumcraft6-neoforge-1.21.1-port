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
        lines.add("Wisp Nether, Angry Zombie overworld, Firebat Nether/Halloween and Pech magical-biome rows. Thaumcraft-biome rows whose biome ids are not present yet stay exact-tag gated or deferred.");
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
        TCEntitySpawnRules.LegacyNaturalSpawn firebatNether = TCEntitySpawnRules.FIREBAT_NETHER;
        TCEntitySpawnRules.LegacyNaturalSpawn firebatHalloween = TCEntitySpawnRules.FIREBAT_HALLOWEEN_OVERWORLD;
        TCEntitySpawnRules.LegacyNaturalSpawn pech = TCEntitySpawnRules.PECH_MAGICAL;
        checks.add(check("active_spawn_catalog_contains_safe_wisp_brainy_firebat_and_pech_rows",
                TCEntitySpawnRules.activeNaturalSpawns().size() == 5
                        && TCEntitySpawnRules.activeNaturalSpawns().contains(wisp)
                        && TCEntitySpawnRules.activeNaturalSpawns().contains(brainy)
                        && TCEntitySpawnRules.activeNaturalSpawns().contains(firebatNether)
                        && TCEntitySpawnRules.activeNaturalSpawns().contains(firebatHalloween)
                        && TCEntitySpawnRules.activeNaturalSpawns().contains(pech)
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
                        && brainy.maxCount() == 1
                        && firebatNether.active()
                        && "Firebat".equals(firebatNether.legacyId())
                        && "thaumcraft:firebat".equals(firebatNether.modernEntityId())
                        && "#minecraft:is_nether".equals(firebatNether.biomeSelector())
                        && firebatNether.weight() == 10
                        && firebatNether.minCount() == 1
                        && firebatNether.maxCount() == 2
                        && firebatHalloween.active()
                        && "FirebatHalloween".equals(firebatHalloween.legacyId())
                        && "thaumcraft:firebat".equals(firebatHalloween.modernEntityId())
                        && "#thaumcraft:legacy_firebat_halloween_spawn_biomes".equals(firebatHalloween.biomeSelector())
                        && firebatHalloween.weight() == 5
                        && firebatHalloween.minCount() == 1
                        && firebatHalloween.maxCount() == 2
                        && pech.active()
                        && "Pech".equals(pech.legacyId())
                        && "thaumcraft:pech".equals(pech.modernEntityId())
                        && "#thaumcraft:legacy_magical_spawn_biomes".equals(pech.biomeSelector())
                        && pech.weight() == 10
                        && pech.minCount() == 1
                        && pech.maxCount() == 1,
                "active=" + TCEntitySpawnRules.activeNaturalSpawns().size() + ", wisp=" + wisp + ", brainy=" + brainy + ", firebatNether=" + firebatNether + ", firebatHalloween=" + firebatHalloween + ", pech=" + pech));
        checks.add(check("unsafe_legacy_spawn_rows_remain_deferred",
                TCEntitySpawnRules.deferredNaturalSpawns().stream().noneMatch(TCEntitySpawnRules.LegacyNaturalSpawn::active)
                        && TCEntitySpawnRules.deferredNaturalSpawns().size() >= 5,
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

        String firebatNetherResource = resourceText("data/thaumcraft/neoforge/biome_modifier/firebat_nether_spawns.json");
        checks.add(check("firebat_nether_biome_modifier_resource_exists",
                !firebatNetherResource.isBlank(),
                "data/thaumcraft/neoforge/biome_modifier/firebat_nether_spawns.json"));
        if (!firebatNetherResource.isBlank()) {
            JsonObject firebatNetherRoot = JsonParser.parseString(firebatNetherResource).getAsJsonObject();
            JsonObject firebatNetherSpawner = firebatNetherRoot.getAsJsonObject("spawners");
            boolean firebatNetherPassed = "neoforge:add_spawns".equals(firebatNetherRoot.get("type").getAsString())
                    && "#minecraft:is_nether".equals(firebatNetherRoot.get("biomes").getAsString())
                    && "thaumcraft:firebat".equals(firebatNetherSpawner.get("type").getAsString())
                    && firebatNetherSpawner.get("weight").getAsInt() == 10
                    && firebatNetherSpawner.get("minCount").getAsInt() == 1
                    && firebatNetherSpawner.get("maxCount").getAsInt() == 2;
            checks.add(check("firebat_nether_biome_modifier_matches_legacy_values",
                    firebatNetherPassed,
                    "type=" + firebatNetherRoot.get("type").getAsString() + ", biome=" + firebatNetherRoot.get("biomes").getAsString() + ", spawner=" + firebatNetherSpawner));
        } else {
            checks.add(check("firebat_nether_biome_modifier_matches_legacy_values", false, "resource missing"));
        }

        String firebatHalloweenResource = resourceText("data/thaumcraft/neoforge/biome_modifier/firebat_halloween_overworld_spawns.json");
        checks.add(check("firebat_halloween_biome_modifier_resource_exists",
                !firebatHalloweenResource.isBlank(),
                "data/thaumcraft/neoforge/biome_modifier/firebat_halloween_overworld_spawns.json"));
        if (!firebatHalloweenResource.isBlank()) {
            JsonObject firebatHalloweenRoot = JsonParser.parseString(firebatHalloweenResource).getAsJsonObject();
            JsonObject firebatHalloweenSpawner = firebatHalloweenRoot.getAsJsonObject("spawners");
            boolean firebatHalloweenPassed = "neoforge:add_spawns".equals(firebatHalloweenRoot.get("type").getAsString())
                    && "#thaumcraft:legacy_firebat_halloween_spawn_biomes".equals(firebatHalloweenRoot.get("biomes").getAsString())
                    && "thaumcraft:firebat".equals(firebatHalloweenSpawner.get("type").getAsString())
                    && firebatHalloweenSpawner.get("weight").getAsInt() == 5
                    && firebatHalloweenSpawner.get("minCount").getAsInt() == 1
                    && firebatHalloweenSpawner.get("maxCount").getAsInt() == 2;
            checks.add(check("firebat_halloween_biome_modifier_matches_legacy_values",
                    firebatHalloweenPassed,
                    "type=" + firebatHalloweenRoot.get("type").getAsString() + ", biome=" + firebatHalloweenRoot.get("biomes").getAsString() + ", spawner=" + firebatHalloweenSpawner));
        } else {
            checks.add(check("firebat_halloween_biome_modifier_matches_legacy_values", false, "resource missing"));
        }

        String firebatTag = resourceText("data/thaumcraft/tags/worldgen/biome/legacy_firebat_halloween_spawn_biomes.json");
        checks.add(check("firebat_halloween_legacy_biome_tag_resource_exists",
                !firebatTag.isBlank() && firebatTag.contains("\"minecraft:desert\"") && firebatTag.contains("\"minecraft:snowy_plains\""),
                "data/thaumcraft/tags/worldgen/biome/legacy_firebat_halloween_spawn_biomes.json"));

        String pechResource = resourceText("data/thaumcraft/neoforge/biome_modifier/pech_legacy_magical_spawns.json");
        checks.add(check("pech_magical_biome_modifier_resource_exists",
                !pechResource.isBlank(),
                "data/thaumcraft/neoforge/biome_modifier/pech_legacy_magical_spawns.json"));
        if (!pechResource.isBlank()) {
            JsonObject pechRoot = JsonParser.parseString(pechResource).getAsJsonObject();
            JsonObject pechSpawner = pechRoot.getAsJsonObject("spawners");
            boolean pechPassed = "neoforge:add_spawns".equals(pechRoot.get("type").getAsString())
                    && "#thaumcraft:legacy_magical_spawn_biomes".equals(pechRoot.get("biomes").getAsString())
                    && "thaumcraft:pech".equals(pechSpawner.get("type").getAsString())
                    && pechSpawner.get("weight").getAsInt() == 10
                    && pechSpawner.get("minCount").getAsInt() == 1
                    && pechSpawner.get("maxCount").getAsInt() == 1;
            checks.add(check("pech_magical_biome_modifier_matches_legacy_values",
                    pechPassed,
                    "type=" + pechRoot.get("type").getAsString() + ", biome=" + pechRoot.get("biomes").getAsString() + ", spawner=" + pechSpawner));
        } else {
            checks.add(check("pech_magical_biome_modifier_matches_legacy_values", false, "resource missing"));
        }

        String pechTag = resourceText("data/thaumcraft/tags/worldgen/biome/legacy_magical_spawn_biomes.json");
        checks.add(check("pech_legacy_magical_biome_tag_resource_exists",
                !pechTag.isBlank()
                        && pechTag.contains("\"thaumcraft:magical_forest\"")
                        && pechTag.contains("\"thaumcraft:eerie\"")
                        && pechTag.contains("\"required\": false"),
                "tag intentionally contains only optional Thaumcraft magical biomes until biome subsystem is ported"));
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
        checks.add(check("firebat_spawn_placement_registered",
                SpawnPlacements.hasPlacement(TCEntityTypes.FIREBAT.get())
                        && SpawnPlacements.getPlacementType(TCEntityTypes.FIREBAT.get()) == SpawnPlacementTypes.NO_RESTRICTIONS
                        && SpawnPlacements.getHeightmapType(TCEntityTypes.FIREBAT.get()) == TCEntitySpawnRules.FIREBAT_HEIGHTMAP_TYPE,
                "placement=" + SpawnPlacements.getPlacementType(TCEntityTypes.FIREBAT.get())
                        + ", heightmap=" + SpawnPlacements.getHeightmapType(TCEntityTypes.FIREBAT.get())));
        checks.add(check("pech_spawn_placement_registered",
                SpawnPlacements.hasPlacement(TCEntityTypes.PECH.get())
                        && SpawnPlacements.getPlacementType(TCEntityTypes.PECH.get()) == SpawnPlacementTypes.ON_GROUND
                        && SpawnPlacements.getHeightmapType(TCEntityTypes.PECH.get()) == TCEntitySpawnRules.PECH_HEIGHTMAP_TYPE,
                "placement=" + SpawnPlacements.getPlacementType(TCEntityTypes.PECH.get())
                        + ", heightmap=" + SpawnPlacements.getHeightmapType(TCEntityTypes.PECH.get())));
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

        prepareDarkSpawnCell(level, origin);
        boolean firebatAllowed = checkFirebat(level, origin, 21L);
        checks.add(check("firebat_spawn_predicate_allows_dark_nether_cell",
                firebatAllowed,
                "pos=" + origin + ", dimension=" + level.dimension().location() + ", brightness=" + level.getMaxLocalRawBrightness(origin)));

        boolean firebatConfigDenied = !TCFirebatEntity.testLegacySpawnGatesForValidation(true, Difficulty.NORMAL, true, 7, 6, true, false, false);
        checks.add(check("firebat_spawn_gate_denies_brightness_above_legacy_roll",
                firebatConfigDenied,
                "localRawBrightness=7, roll=6"));

        boolean firebatDisabledDenied = !TCFirebatEntity.testLegacySpawnGatesForValidation(false, Difficulty.NORMAL, true, 0, 6, true, false, false);
        checks.add(check("firebat_spawn_gate_denies_config_disabled",
                firebatDisabledDenied,
                "allowSpawnFireBat=false"));

        boolean firebatHalloweenOutsideDateDenied = !TCFirebatEntity.testLegacySpawnGatesForValidation(true, Difficulty.NORMAL, true, 0, 6, false, true, false);
        checks.add(check("firebat_spawn_gate_denies_halloween_row_outside_halloween",
                firebatHalloweenOutsideDateDenied,
                "halloweenBiome=true, halloweenDate=false"));

        boolean firebatHalloweenAllowed = TCFirebatEntity.testLegacySpawnGatesForValidation(true, Difficulty.NORMAL, true, 0, 6, false, true, true);
        checks.add(check("firebat_spawn_gate_allows_halloween_row_on_oct_31",
                firebatHalloweenAllowed,
                "halloweenBiome=true, halloweenDate=true"));

        server.setDifficulty(Difficulty.PEACEFUL, true);
        boolean firebatPeacefulDenied = !checkFirebat(level, origin, 22L);
        checks.add(check("firebat_spawn_predicate_denies_peaceful",
                firebatPeacefulDenied,
                "difficulty=" + level.getDifficulty()));
        server.setDifficulty(previous, true);

        boolean pechGateAllowed = TCPechEntity.testLegacySpawnGatesForValidation(true, true, true, 3, true);
        boolean pechConfigDenied = !TCPechEntity.testLegacySpawnGatesForValidation(false, true, true, 0, true);
        boolean pechNonMagicalDenied = !TCPechEntity.testLegacySpawnGatesForValidation(true, false, true, 0, true);
        boolean pechWrongDimensionDenied = !TCPechEntity.testLegacySpawnGatesForValidation(true, true, false, 0, true);
        boolean pechCapDenied = !TCPechEntity.testLegacySpawnGatesForValidation(true, true, true, 4, true);
        checks.add(check("pech_spawn_gates_match_legacy_magical_biome_budget",
                pechGateAllowed
                        && pechConfigDenied
                        && pechNonMagicalDenied
                        && pechWrongDimensionDenied
                        && pechCapDenied,
                "allow=true magical=true dimensionAllowed=true nearby=3 passes; disabled/non-magical/wrong-dimension/nearby=4 deny"));
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

    private static boolean checkFirebat(ServerLevel level, BlockPos pos, long seed) {
        return SpawnPlacements.checkSpawnRules(
                TCEntityTypes.FIREBAT.get(),
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
