package thaumcraft.common.entities;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCEntityTypes;

public final class TCEntitySpawnRules {
    public static final TagKey<Biome> LEGACY_MAGICAL_BIOME_TAG = TagKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "legacy_magical_spawn_biomes")
    );

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
    public static final LegacyNaturalSpawn BRAINY_ZOMBIE_OVERWORLD = new LegacyNaturalSpawn(
            "BrainyZombie",
            "EntityBrainyZombie",
            "thaumcraft:brainy_zombie",
            "#thaumcraft:legacy_angry_zombie_spawn_biomes",
            10,
            1,
            1,
            true,
            "ConfigEntities.postInitEntitySpawns allowSpawnAngryZombie warm/cool/icy/desert monster-biome row mapped to a 1.21 land-biome tag"
    );
    public static final LegacyNaturalSpawn FIREBAT_NETHER = new LegacyNaturalSpawn(
            "Firebat",
            "EntityFireBat",
            "thaumcraft:firebat",
            "#minecraft:is_nether",
            10,
            1,
            2,
            true,
            "ConfigEntities.postInitEntitySpawns allowSpawnFireBat Nether BiomeDictionary entry"
    );
    public static final LegacyNaturalSpawn FIREBAT_HALLOWEEN_OVERWORLD = new LegacyNaturalSpawn(
            "FirebatHalloween",
            "EntityFireBat",
            "thaumcraft:firebat",
            "#thaumcraft:legacy_firebat_halloween_spawn_biomes",
            5,
            1,
            2,
            true,
            "ConfigEntities.postInitEntitySpawns Oct 31 warm/cool/icy/desert monster-biome row; predicate gates date"
    );
    public static final LegacyNaturalSpawn PECH_MAGICAL = new LegacyNaturalSpawn(
            "Pech",
            "EntityPech",
            "thaumcraft:pech",
            "#thaumcraft:legacy_magical_spawn_biomes",
            10,
            1,
            1,
            true,
            "ConfigEntities.postInitEntitySpawns allowSpawnPech BiomeDictionary.Type.MAGICAL row. Tag is intentionally exact-gated to optional Thaumcraft Magical Forest/Eerie biomes until biome porting supplies real magical biome data."
    );

    private static final List<LegacyNaturalSpawn> ACTIVE_NATURAL_SPAWNS = List.of(
            WISP_NETHER,
            BRAINY_ZOMBIE_OVERWORLD,
            FIREBAT_NETHER,
            FIREBAT_HALLOWEEN_OVERWORLD,
            PECH_MAGICAL
    );
    private static final List<LegacyNaturalSpawn> DEFERRED_NATURAL_SPAWNS = List.of(
            new LegacyNaturalSpawn("EerieBrainyZombie", "EntityBrainyZombie", "thaumcraft:brainy_zombie", "thaumcraft:eerie", 32, 1, 1, false, "Thaumcraft Eerie biome is not ported yet"),
            new LegacyNaturalSpawn("EerieGiantBrainyZombie", "EntityGiantBrainyZombie", "thaumcraft:giant_brainy_zombie", "thaumcraft:eerie", 8, 1, 1, false, "Thaumcraft Eerie biome is not ported yet"),
            new LegacyNaturalSpawn("MagicalForestWisp", "EntityWisp", "thaumcraft:wisp", "thaumcraft:magical_forest", 20, 1, 2, false, "Thaumcraft Magical Forest biome is not ported yet"),
            new LegacyNaturalSpawn("EerieWisp", "EntityWisp", "thaumcraft:wisp", "thaumcraft:eerie", 3, 1, 1, false, "Thaumcraft Eerie biome is not ported yet"),
            new LegacyNaturalSpawn("EerieEldritchGuardian", "EntityEldritchGuardian", "thaumcraft:eldritch_guardian", "thaumcraft:eerie", 1, 1, 1, false, "Eerie biome is not ported yet")
    );

    public static final SpawnPlacementType WISP_PLACEMENT_TYPE = SpawnPlacementTypes.NO_RESTRICTIONS;
    public static final Heightmap.Types WISP_HEIGHTMAP_TYPE = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
    public static final SpawnPlacementType BRAINY_ZOMBIE_PLACEMENT_TYPE = SpawnPlacementTypes.ON_GROUND;
    public static final Heightmap.Types BRAINY_ZOMBIE_HEIGHTMAP_TYPE = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
    public static final SpawnPlacementType FIREBAT_PLACEMENT_TYPE = SpawnPlacementTypes.NO_RESTRICTIONS;
    public static final Heightmap.Types FIREBAT_HEIGHTMAP_TYPE = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
    public static final SpawnPlacementType PECH_PLACEMENT_TYPE = SpawnPlacementTypes.ON_GROUND;
    public static final Heightmap.Types PECH_HEIGHTMAP_TYPE = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

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
        event.register(
                TCEntityTypes.BRAINY_ZOMBIE.get(),
                BRAINY_ZOMBIE_PLACEMENT_TYPE,
                BRAINY_ZOMBIE_HEIGHTMAP_TYPE,
                TCBrainyZombieEntity::checkBrainyZombieSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        event.register(
                TCEntityTypes.FIREBAT.get(),
                FIREBAT_PLACEMENT_TYPE,
                FIREBAT_HEIGHTMAP_TYPE,
                TCFirebatEntity::checkFirebatSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        event.register(
                TCEntityTypes.PECH.get(),
                PECH_PLACEMENT_TYPE,
                PECH_HEIGHTMAP_TYPE,
                TCPechEntity::checkPechSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        Thaumcraft.LOGGER.debug("Registered Thaumcraft natural spawn placement policies.");
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
