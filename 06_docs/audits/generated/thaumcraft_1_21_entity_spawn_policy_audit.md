# Thaumcraft Entity Spawn Policy Audit

Runtime checks for legacy natural-spawn rows ported to NeoForge 1.21.1.
Only rows whose entities and server-side behavior have a registered foundation are active:
Wisp Nether, Angry Zombie overworld, Firebat Nether/Halloween and Pech magical-biome rows. Thaumcraft-biome rows whose biome ids are not present yet stay exact-tag gated or deferred.

## Summary

| Check | Result |
|---|---:|
| Passed | 34 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| active_spawn_catalog_contains_safe_wisp_brainy_firebat_and_pech_rows | PASS | active=5, wisp=LegacyNaturalSpawn[legacyId=Wisp, legacyClass=EntityWisp, modernEntityId=thaumcraft:wisp, biomeSelector=#minecraft:is_nether, weight=5, minCount=1, maxCount=1, active=true, notes=ConfigEntities.postInitEntitySpawns allowSpawnWisp Nether BiomeDictionary entry], brainy=LegacyNaturalSpawn[legacyId=BrainyZombie, legacyClass=EntityBrainyZombie, modernEntityId=thaumcraft:brainy_zombie, biomeSelector=#thaumcraft:legacy_angry_zombie_spawn_biomes, weight=10, minCount=1, maxCount=1, active=true, notes=ConfigEntities.postInitEntitySpawns allowSpawnAngryZombie warm/cool/icy/desert monster-biome row mapped to a 1.21 land-biome tag], firebatNether=LegacyNaturalSpawn[legacyId=Firebat, legacyClass=EntityFireBat, modernEntityId=thaumcraft:firebat, biomeSelector=#minecraft:is_nether, weight=10, minCount=1, maxCount=2, active=true, notes=ConfigEntities.postInitEntitySpawns allowSpawnFireBat Nether BiomeDictionary entry], firebatHalloween=LegacyNaturalSpawn[legacyId=FirebatHalloween, legacyClass=EntityFireBat, modernEntityId=thaumcraft:firebat, biomeSelector=#thaumcraft:legacy_firebat_halloween_spawn_biomes, weight=5, minCount=1, maxCount=2, active=true, notes=ConfigEntities.postInitEntitySpawns Oct 31 warm/cool/icy/desert monster-biome row; predicate gates date], pech=LegacyNaturalSpawn[legacyId=Pech, legacyClass=EntityPech, modernEntityId=thaumcraft:pech, biomeSelector=#thaumcraft:legacy_magical_spawn_biomes, weight=10, minCount=1, maxCount=1, active=true, notes=ConfigEntities.postInitEntitySpawns allowSpawnPech BiomeDictionary.Type.MAGICAL row. Tag is intentionally exact-gated to optional Thaumcraft Magical Forest/Eerie biomes until biome porting supplies real magical biome data.] |
| unsafe_legacy_spawn_rows_remain_deferred | PASS | deferred=5 |
| wisp_nether_biome_modifier_resource_exists | PASS | data/thaumcraft/neoforge/biome_modifier/wisp_nether_spawns.json |
| wisp_nether_biome_modifier_matches_legacy_values | PASS | type=neoforge:add_spawns, biome=#minecraft:is_nether, spawner={"type":"thaumcraft:wisp","weight":5,"minCount":1,"maxCount":1} |
| brainy_zombie_biome_modifier_resource_exists | PASS | data/thaumcraft/neoforge/biome_modifier/brainy_zombie_legacy_overworld_spawns.json |
| brainy_zombie_biome_modifier_matches_legacy_values | PASS | type=neoforge:add_spawns, biome=#thaumcraft:legacy_angry_zombie_spawn_biomes, spawner={"type":"thaumcraft:brainy_zombie","weight":10,"minCount":1,"maxCount":1} |
| brainy_zombie_legacy_biome_tag_resource_exists | PASS | data/thaumcraft/tags/worldgen/biome/legacy_angry_zombie_spawn_biomes.json |
| firebat_nether_biome_modifier_resource_exists | PASS | data/thaumcraft/neoforge/biome_modifier/firebat_nether_spawns.json |
| firebat_nether_biome_modifier_matches_legacy_values | PASS | type=neoforge:add_spawns, biome=#minecraft:is_nether, spawner={"type":"thaumcraft:firebat","weight":10,"minCount":1,"maxCount":2} |
| firebat_halloween_biome_modifier_resource_exists | PASS | data/thaumcraft/neoforge/biome_modifier/firebat_halloween_overworld_spawns.json |
| firebat_halloween_biome_modifier_matches_legacy_values | PASS | type=neoforge:add_spawns, biome=#thaumcraft:legacy_firebat_halloween_spawn_biomes, spawner={"type":"thaumcraft:firebat","weight":5,"minCount":1,"maxCount":2} |
| firebat_halloween_legacy_biome_tag_resource_exists | PASS | data/thaumcraft/tags/worldgen/biome/legacy_firebat_halloween_spawn_biomes.json |
| pech_magical_biome_modifier_resource_exists | PASS | data/thaumcraft/neoforge/biome_modifier/pech_legacy_magical_spawns.json |
| pech_magical_biome_modifier_matches_legacy_values | PASS | type=neoforge:add_spawns, biome=#thaumcraft:legacy_magical_spawn_biomes, spawner={"type":"thaumcraft:pech","weight":10,"minCount":1,"maxCount":1} |
| pech_legacy_magical_biome_tag_resource_exists | PASS | tag intentionally contains only optional Thaumcraft magical biomes until biome subsystem is ported |
| wisp_spawn_placement_registered | PASS | placement=net.minecraft.world.entity.SpawnPlacementTypes$$Lambda/0x0000000800eb85f0@2231828f, heightmap=MOTION_BLOCKING_NO_LEAVES |
| brainy_zombie_spawn_placement_registered | PASS | placement=net.minecraft.world.entity.SpawnPlacementTypes$1@3ad3cf24, heightmap=MOTION_BLOCKING_NO_LEAVES |
| firebat_spawn_placement_registered | PASS | placement=net.minecraft.world.entity.SpawnPlacementTypes$$Lambda/0x0000000800eb85f0@2231828f, heightmap=MOTION_BLOCKING_NO_LEAVES |
| pech_spawn_placement_registered | PASS | placement=net.minecraft.world.entity.SpawnPlacementTypes$1@3ad3cf24, heightmap=MOTION_BLOCKING_NO_LEAVES |
| wisp_spawn_predicate_allows_dark_unobstructed_normal_cell | PASS | pos=BlockPos{x=16, y=48, z=16}, brightness=0 |
| wisp_spawn_predicate_denies_obstructed_cell | PASS | block=Block{minecraft:stone} |
| wisp_spawn_gates_deny_bright_cell_like_legacy_light_check | PASS | localRawBrightness=15, blockRoll=7 |
| wisp_spawn_gates_deny_legacy_local_cap_at_eight | PASS | nearby=8 |
| wisp_spawn_predicate_denies_peaceful | PASS | difficulty=PEACEFUL |
| brainy_zombie_spawn_predicate_allows_dark_ground_cell | PASS | pos=BlockPos{x=16, y=48, z=16}, brightness=0 |
| brainy_zombie_spawn_gate_denies_config_disabled | PASS | allowSpawnAngryZombie=false |
| brainy_zombie_spawn_predicate_denies_peaceful | PASS | difficulty=PEACEFUL |
| firebat_spawn_predicate_allows_dark_nether_cell | PASS | pos=BlockPos{x=16, y=48, z=16}, dimension=minecraft:the_nether, brightness=0 |
| firebat_spawn_gate_denies_brightness_above_legacy_roll | PASS | localRawBrightness=7, roll=6 |
| firebat_spawn_gate_denies_config_disabled | PASS | allowSpawnFireBat=false |
| firebat_spawn_gate_denies_halloween_row_outside_halloween | PASS | halloweenBiome=true, halloweenDate=false |
| firebat_spawn_gate_allows_halloween_row_on_oct_31 | PASS | halloweenBiome=true, halloweenDate=true |
| firebat_spawn_predicate_denies_peaceful | PASS | difficulty=PEACEFUL |
| pech_spawn_gates_match_legacy_magical_biome_budget | PASS | allow=true magical=true dimensionAllowed=true nearby=3 passes; disabled/non-magical/wrong-dimension/nearby=4 deny |

## Active legacy natural spawns

| Legacy id | Class | Modern entity | Biome selector | Weight | Min | Max | Notes |
|---|---|---|---|---:|---:|---:|---|
| Wisp | EntityWisp | thaumcraft:wisp | #minecraft:is_nether | 5 | 1 | 1 | ConfigEntities.postInitEntitySpawns allowSpawnWisp Nether BiomeDictionary entry |
| BrainyZombie | EntityBrainyZombie | thaumcraft:brainy_zombie | #thaumcraft:legacy_angry_zombie_spawn_biomes | 10 | 1 | 1 | ConfigEntities.postInitEntitySpawns allowSpawnAngryZombie warm/cool/icy/desert monster-biome row mapped to a 1.21 land-biome tag |
| Firebat | EntityFireBat | thaumcraft:firebat | #minecraft:is_nether | 10 | 1 | 2 | ConfigEntities.postInitEntitySpawns allowSpawnFireBat Nether BiomeDictionary entry |
| FirebatHalloween | EntityFireBat | thaumcraft:firebat | #thaumcraft:legacy_firebat_halloween_spawn_biomes | 5 | 1 | 2 | ConfigEntities.postInitEntitySpawns Oct 31 warm/cool/icy/desert monster-biome row; predicate gates date |
| Pech | EntityPech | thaumcraft:pech | #thaumcraft:legacy_magical_spawn_biomes | 10 | 1 | 1 | ConfigEntities.postInitEntitySpawns allowSpawnPech BiomeDictionary.Type.MAGICAL row. Tag is intentionally exact-gated to optional Thaumcraft Magical Forest/Eerie biomes until biome porting supplies real magical biome data. |

## Deferred legacy natural spawns

| Legacy id | Class | Modern entity | Biome selector | Weight | Min | Max | Notes |
|---|---|---|---|---:|---:|---:|---|
| EerieBrainyZombie | EntityBrainyZombie | thaumcraft:brainy_zombie | thaumcraft:eerie | 32 | 1 | 1 | Thaumcraft Eerie biome is not ported yet |
| EerieGiantBrainyZombie | EntityGiantBrainyZombie | thaumcraft:giant_brainy_zombie | thaumcraft:eerie | 8 | 1 | 1 | Thaumcraft Eerie biome is not ported yet |
| MagicalForestWisp | EntityWisp | thaumcraft:wisp | thaumcraft:magical_forest | 20 | 1 | 2 | Thaumcraft Magical Forest biome is not ported yet |
| EerieWisp | EntityWisp | thaumcraft:wisp | thaumcraft:eerie | 3 | 1 | 1 | Thaumcraft Eerie biome is not ported yet |
| EerieEldritchGuardian | EntityEldritchGuardian | thaumcraft:eldritch_guardian | thaumcraft:eerie | 1 | 1 | 1 | Eerie biome is not ported yet |
