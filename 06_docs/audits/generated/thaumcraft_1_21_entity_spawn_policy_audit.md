# Thaumcraft Entity Spawn Policy Audit

Runtime checks for the first legacy natural-spawn boundary ported to NeoForge 1.21.1.
This intentionally activates only the Wisp Nether spawn that has both a registered modern entity
and exact TC6 legacy source evidence. Other legacy spawn rows are cataloged as deferred until their
own entities or Thaumcraft biomes exist.

## Summary

| Check | Result |
|---|---:|
| Passed | 10 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| active_spawn_catalog_contains_only_safe_wisp_nether | PASS | active=1, row=LegacyNaturalSpawn[legacyId=Wisp, legacyClass=EntityWisp, modernEntityId=thaumcraft:wisp, biomeSelector=#minecraft:is_nether, weight=5, minCount=1, maxCount=1, active=true, notes=ConfigEntities.postInitEntitySpawns allowSpawnWisp Nether BiomeDictionary entry] |
| unsafe_legacy_spawn_rows_remain_deferred | PASS | deferred=7 |
| wisp_nether_biome_modifier_resource_exists | PASS | data/thaumcraft/neoforge/biome_modifier/wisp_nether_spawns.json |
| wisp_nether_biome_modifier_matches_legacy_values | PASS | type=neoforge:add_spawns, biome=#minecraft:is_nether, spawner={"type":"thaumcraft:wisp","weight":5,"minCount":1,"maxCount":1} |
| wisp_spawn_placement_registered | PASS | placement=net.minecraft.world.entity.SpawnPlacementTypes$$Lambda/0x0000000800e79370@211b072c, heightmap=MOTION_BLOCKING_NO_LEAVES |
| wisp_spawn_predicate_allows_dark_unobstructed_normal_cell | PASS | pos=BlockPos{x=16, y=48, z=16}, brightness=0 |
| wisp_spawn_predicate_denies_obstructed_cell | PASS | block=Block{minecraft:stone} |
| wisp_spawn_gates_deny_bright_cell_like_legacy_light_check | PASS | localRawBrightness=15, blockRoll=7 |
| wisp_spawn_gates_deny_legacy_local_cap_at_eight | PASS | nearby=8 |
| wisp_spawn_predicate_denies_peaceful | PASS | difficulty=PEACEFUL |

## Active legacy natural spawns

| Legacy id | Class | Modern entity | Biome selector | Weight | Min | Max | Notes |
|---|---|---|---|---:|---:|---:|---|
| Wisp | EntityWisp | thaumcraft:wisp | #minecraft:is_nether | 5 | 1 | 1 | ConfigEntities.postInitEntitySpawns allowSpawnWisp Nether BiomeDictionary entry |

## Deferred legacy natural spawns

| Legacy id | Class | Modern entity | Biome selector | Weight | Min | Max | Notes |
|---|---|---|---|---:|---:|---:|---|
| Firebat | EntityFireBat |  | #minecraft:is_nether | 10 | 1 | 2 | EntityFireBat is not ported yet |
| FirebatHalloween | EntityFireBat |  | legacy warm/cool/icy/desert biomes on Oct 31 | 5 | 1 | 2 | EntityFireBat and calendar gated spawn policy are not ported yet |
| Pech | EntityPech |  | legacy BiomeDictionary.Type.MAGICAL | 10 | 1 | 1 | Pech entity and magical-biome mapping are not ported yet |
| BrainyZombie | EntityBrainyZombie |  | legacy warm/cool/icy/desert monster biomes | 10 | 1 | 1 | BrainyZombie entity is not ported yet |
| MagicalForestWisp | EntityWisp | thaumcraft:wisp | thaumcraft:magical_forest | 20 | 1 | 2 | Thaumcraft Magical Forest biome is not ported yet |
| EerieWisp | EntityWisp | thaumcraft:wisp | thaumcraft:eerie | 3 | 1 | 1 | Thaumcraft Eerie biome is not ported yet |
| EerieEldritchGuardian | EntityEldritchGuardian |  | thaumcraft:eerie | 1 | 1 | 1 | EldritchGuardian entity and Eerie biome are not ported yet |
