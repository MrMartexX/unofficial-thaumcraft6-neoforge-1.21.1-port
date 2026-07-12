package thaumcraft.common.entities;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCEntityTypes;

public final class TCEntitySpawnRules {
    public static final LegacyNaturalSpawn WISP_NETHER = new LegacyNaturalSpawn(
            "Wisp",
            "EntityWisp",
            "thaumcraft:wisp",
            "#minecraft:is_nether",
            5,
            1,
            1,
            true,
            "ConfigEntities.postInitEntitySpawns allowSpawnWisp Nether BiomeDictionary entry"
    );

    private static final List<LegacyNaturalSpawn> ACTIVE_NATURAL_SPAWNS = List.of(WISP_NETHER);
    private static final List<LegacyNaturalSpawn> DEFERRED_NATURAL_SPAWNS = List.of(
            new LegacyNaturalSpawn("Firebat", "EntityFireBat", null, "#minecraft:is_nether", 10, 1, 2, false, "EntityFireBat is not ported yet"),
            new LegacyNaturalSpawn("FirebatHalloween", "EntityFireBat", null, "legacy warm/cool/icy/desert biomes on Oct 31", 5, 1, 2, false, "EntityFireBat and calendar gated spawn policy are not ported yet"),
            new LegacyNaturalSpawn("Pech", "EntityPech", null, "legacy BiomeDictionary.Type.MAGICAL", 10, 1, 1, false, "Pech entity and magical-biome mapping are not ported yet"),
            new LegacyNaturalSpawn("BrainyZombie", "EntityBrainyZombie", null, "legacy warm/cool/icy/desert monster biomes", 10, 1, 1, false, "BrainyZombie entity is not ported yet"),
            new LegacyNaturalSpawn("MagicalForestWisp", "EntityWisp", "thaumcraft:wisp", "thaumcraft:magical_forest", 20, 1, 2, false, "Thaumcraft Magical Forest biome is not ported yet"),
            new LegacyNaturalSpawn("EerieWisp", "EntityWisp", "thaumcraft:wisp", "thaumcraft:eerie", 3, 1, 1, false, "Thaumcraft Eerie biome is not ported yet"),
            new LegacyNaturalSpawn("EerieEldritchGuardian", "EntityEldritchGuardian", null, "thaumcraft:eerie", 1, 1, 1, false, "EldritchGuardian entity and Eerie biome are not ported yet")
    );

    public static final SpawnPlacementType WISP_PLACEMENT_TYPE = SpawnPlacementTypes.NO_RESTRICTIONS;
    public static final Heightmap.Types WISP_HEIGHTMAP_TYPE = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

    private TCEntitySpawnRules() {
    }

    public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                TCEntityTypes.WISP.get(),
                WISP_PLACEMENT_TYPE,
                WISP_HEIGHTMAP_TYPE,
                TCWispEntity::checkWispSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        Thaumcraft.LOGGER.debug("Registered Thaumcraft Wisp spawn placement policy.");
    }

    public static List<LegacyNaturalSpawn> activeNaturalSpawns() {
        return ACTIVE_NATURAL_SPAWNS;
    }

    public static List<LegacyNaturalSpawn> deferredNaturalSpawns() {
        return DEFERRED_NATURAL_SPAWNS;
    }

    public record LegacyNaturalSpawn(
            String legacyId,
            String legacyClass,
            String modernEntityId,
            String biomeSelector,
            int weight,
            int minCount,
            int maxCount,
            boolean active,
            String notes
    ) {
    }
}
