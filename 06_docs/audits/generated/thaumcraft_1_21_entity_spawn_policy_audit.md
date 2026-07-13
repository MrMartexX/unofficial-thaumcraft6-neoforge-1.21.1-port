# Thaumcraft Entity Spawn Policy Audit

Runtime checks for legacy natural-spawn rows ported to NeoForge 1.21.1.
Only rows whose entities and server-side behavior have a registered foundation are active:
Wisp Nether and Angry Zombie overworld. Thaumcraft-biome rows and unported mob families stay deferred.

## Summary

| Check | Result |
|---|---:|
| Passed | 17 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| active_spawn_catalog_contains_safe_wisp_and_brainy_rows | PASS | active=2, wisp=LegacyNaturalSpawn[legacyId=Wisp, legacyClass=EntityWisp, modernEntityId=thaumcraft:wisp, biomeSelector=#minecraft:is_nether, weight=5, minCount=1, maxCount=1, active=true, notes=ConfigEntities.postInitEntitySpawns allowSpawnWisp Nether BiomeDictionary entry], brainy=LegacyNaturalSpawn[legacyId=BrainyZombie, legacyClass=EntityBrainyZombie, modernEntityId=thaumcraft:brainy_zombie, biomeSelector=#thaumcraft:legacy_angry_zombie_spawn_biomes, weight=10, minCount=1, maxCount=1, active=true, notes=ConfigEntities.postInitEntitySpawns allowSpawnAngryZombie warm/cool/icy/desert monster-biome row mapped to a 1.21 land-biome tag] |
| unsafe_legacy_spawn_rows_remain_deferred | PASS | deferred=8 |
| wisp_nether_biome_modifier_resource_exists | PASS | data/thaumcraft/neoforge/biome_modifier/wisp_nether_spawns.json |
| wisp_nether_biome_modifier_matches_legacy_values | PASS | type=neoforge:add_spawns, biome=#minecraft:is_nether, spawner={"type":"thaumcraft:wisp","weight":5,"minCount":1,"maxCount":1} |
| brainy_zombie_biome_modifier_resource_exists | PASS | data/thaumcraft/neoforge/biome_modifier/brainy_zombie_legacy_overworld_spawns.json |
| brainy_zombie_biome_modifier_matches_legacy_values | PASS | type=neoforge:add_spawns, biome=#thaumcraft:legacy_angry_zombie_spawn_biomes, spawner={"type":"thaumcraft:brainy_zombie","weight":10,"minCount":1,"maxCount":1} |
| brainy_zombie_legacy_biome_tag_resource_exists | PASS | data/thaumcraft/tags/worldgen/biome/legacy_angry_zombie_spawn_biomes.json |
| wisp_spawn_placement_registered | PASS | placement=net.minecraft.world.entity.SpawnPlacementTypes$$Lambda/0x0000000800ea3170@558c43d7, heightmap=MOTION_BLOCKING_NO_LEAVES |
| brainy_zombie_spawn_placement_registered | PASS | placement=net.minecraft.world.entity.SpawnPlacementTypes$1@2118ee0e, heightmap=MOTION_BLOCKING_NO_LEAVES |
| wisp_spawn_predicate_allows_dark_unobstructed_normal_cell | PASS | pos=BlockPos{x=16, y=48, z=16}, brightness=0 |
| wisp_spawn_predicate_denies_obstructed_cell | PASS | block=Block{minecraft:stone} |
| wisp_spawn_gates_deny_bright_cell_like_legacy_light_check | PASS | localRawBrightness=15, blockRoll=7 |
| wisp_spawn_gates_deny_legacy_local_cap_at_eight | PASS | nearby=8 |
| wisp_spawn_predicate_denies_peaceful | PASS | difficulty=PEACEFUL |
| brainy_zombie_spawn_predicate_allows_dark_ground_cell | PASS | pos=BlockPos{x=16, y=48, z=16}, brightness=0 |
| brainy_zombie_spawn_gate_denies_config_disabled | PASS | allowSpawnAngryZombie=false |
| brainy_zombie_spawn_predicate_denies_peaceful | PASS | difficulty=PEACEFUL |

## Active legacy natural spawns

| Legacy id | Class | Modern entity | Biome selector | Weight | Min | Max | Notes |
|---|---|---|---|---:|---:|---:|---|
| Wisp | EntityWisp | thaumcraft:wisp | #minecraft:is_nether | 5 | 1 | 1 | ConfigEntities.postInitEntitySpawns allowSpawnWisp Nether BiomeDictionary entry |
| BrainyZombie | EntityBrainyZombie | thaumcraft:brainy_zombie | #thaumcraft:legacy_angry_zombie_spawn_biomes | 10 | 1 | 1 | ConfigEntities.postInitEntitySpawns allowSpawnAngryZombie warm/cool/icy/desert monster-biome row mapped to a 1.21 land-biome tag |

## Deferred legacy natural spawns

| Legacy id | Class | Modern entity | Biome selector | Weight | Min | Max | Notes |
|---|---|---|---|---:|---:|---:|---|
| Firebat | EntityFireBat |  | #minecraft:is_nether | 10 | 1 | 2 | EntityFireBat is not ported yet |
| FirebatHalloween | EntityFireBat |  | legacy warm/cool/icy/desert biomes on Oct 31 | 5 | 1 | 2 | EntityFireBat and calendar gated spawn policy are not ported yet |
| Pech | EntityPech |  | legacy BiomeDictionary.Type.MAGICAL | 10 | 1 | 1 | Pech entity and magical-biome mapping are not ported yet |
| EerieBrainyZombie | EntityBrainyZombie | thaumcraft:brainy_zombie | thaumcraft:eerie | 32 | 1 | 1 | Thaumcraft Eerie biome is not ported yet |
| EerieGiantBrainyZombie | EntityGiantBrainyZombie | thaumcraft:giant_brainy_zombie | thaumcraft:eerie | 8 | 1 | 1 | Thaumcraft Eerie biome is not ported yet |
| MagicalForestWisp | EntityWisp | thaumcraft:wisp | thaumcraft:magical_forest | 20 | 1 | 2 | Thaumcraft Magical Forest biome is not ported yet |
| EerieWisp | EntityWisp | thaumcraft:wisp | thaumcraft:eerie | 3 | 1 | 1 | Thaumcraft Eerie biome is not ported yet |
| EerieEldritchGuardian | EntityEldritchGuardian | thaumcraft:eldritch_guardian | thaumcraft:eerie | 1 | 1 | 1 | Eerie biome is not ported yet |
