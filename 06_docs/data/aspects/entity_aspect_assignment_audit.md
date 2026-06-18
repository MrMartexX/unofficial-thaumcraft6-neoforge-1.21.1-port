# Entity Aspect Assignment Audit

Scope: vanilla entity aspects used by `AspectHelper.getEntityAspects(Entity)` and therefore by Thaumometer/scan targeting. This follows the migration guide rule that gameplay systems must preserve legacy behavior first, then explicitly document modern-only extensions instead of mixing guessed behavior into the legacy contract.

## Legacy Source

Primary reference:

- `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/config/ConfigAspects.java`
- `ConfigAspects.registerEntityAspects()`
- `thaumcraft.api.ThaumcraftApi.registerEntityTag(...)`
- `thaumcraft.api.aspects.AspectHelper.getEntityAspects(Entity)`

Legacy behavior:

- Player aspects are generated dynamically: `HUMANUS 4` plus three random aspects at amount `15`, seeded by player name hash.
- Non-player aspects come from `CommonInternals.scanEntities`, populated by `registerEntityTag`.
- Some legacy entries are NBT-sensitive: charged creeper adds `POTENTIA 15`; elder guardian was a Guardian NBT variant and is a separate `minecraft:elder_guardian` type in 1.21.1.
- Entity aspects are independent from spawn egg item aspects. Spawn eggs remain aspectless for legacy parity.

## Implemented Port Behavior

Code:

- `05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCEntityAspectAssignments.java`
- `05_neoforge_port/src/main/java/thaumcraft/api/aspects/AspectHelper.java`
- `05_neoforge_port/src/main/java/thaumcraft/common/aspects/TCAspectParityValidator.java`

The port now resolves vanilla entity aspects through a stable entity-type table before scanning. The table includes:

- exact Thaumcraft 6 / Minecraft 1.12.2 vanilla entity assignments;
- charged creeper runtime bonus;
- 1.21.1 type remaps such as `PigZombie -> ZOMBIFIED_PIGLIN`, `LavaSlime -> MAGMA_CUBE`, `VillagerGolem -> IRON_GOLEM`, `EvocationIllager -> EVOKER`, `VindicationIllager -> VINDICATOR`;
- post-1.12 vanilla entities assigned by nearest legacy Thaumcraft semantics, clearly separated from exact legacy rows.

## Exact Legacy Rows

| Legacy entity | 1.21.1 entity type | Aspects | Notes |
|---|---|---|---|
| Spider | `minecraft:spider` | `bestia 10`, `perditio 10`, `vinculum 10` | Fixes visible mob scan gap where `f_SPIDER` existed but aspects were `none`. |
| Bat | `minecraft:bat` | `bestia 5`, `volatus 5`, `tenebrae 5` | Also used by `f_BAT` and `f_FLY` scan keys. |
| Enderman | `minecraft:enderman` | `alienis 10`, `motus 15`, `desiderium 5` | Also used by `f_TELEPORT`. |
| Creeper | `minecraft:creeper` | `herba 15`, `ignis 15` | Powered creeper adds `potentia 15`. |
| Elder Guardian | `minecraft:elder_guardian` | `bestia 10`, `alienis 15`, `aqua 15` | Deliberate modern policy correction: real 1.12 runtime missed this row, but legacy config intent used a Guardian+Elder NBT variant. |
| Zombie Villager | `minecraft:zombie_villager` | `exanimis 20`, `humanus 15`, `terra 5` | Deliberate modern policy correction: zombie/villager hybrid semantics, not exact 1.12 runtime no-aspect behavior. |
| Iron Golem | `minecraft:iron_golem` | `metallum 15`, `humanus 5`, `machina 5`, `praecantatio 5` | Legacy `VillagerGolem`. |
| Zombified Piglin | `minecraft:zombified_piglin` | `exanimis 15`, `ignis 15`, `bestia 10` | Legacy `PigZombie`. |
| Magma Cube | `minecraft:magma_cube` | `aqua 5`, `ignis 10`, `alkimia 5` | Legacy `LavaSlime`; odd water component is preserved. |

The full exact table is in `TCEntityAspectAssignments` and is guarded by bootstrap validation for high-risk representative rows.

## Post-1.12 Entity Policy

| Modern entity area | Examples | Aspect policy |
|---|---|---|
| New natural beasts | `armadillo`, `camel`, `goat`, `panda`, `fox` | Start from legacy passive animal values: `bestia`, `terra`, plus one defining trait such as `praemunio`, `motus`, `aversio`, `herba`, or `desiderium`. |
| Aquatic animals | `axolotl`, `cod`, `salmon`, `tropical_fish`, `dolphin`, `turtle` | Start from legacy squid/guardian water logic: `bestia` + `aqua`; add `victus`, `sensus`, or `praemunio` only where the mob identity needs it. |
| Nether humanoids/beasts | `piglin`, `piglin_brute`, `hoglin`, `strider`, `zoglin` | Preserve Thaumcraft's nether style: `ignis`, `aversio`, `bestia` or `humanus`; undead variants get `exanimis`. |
| Illager family | `pillager`, `ravager` | Continue 1.12 illager logic: `humanus`/`aversio`/`instrumentum` for humanoids; large mount uses `bestia`/`aversio`/`terra`. |
| Magical/flying/deep-dark | `allay`, `phantom`, `breeze`, `warden` | Use the same semantic vocabulary as legacy wisp/vex/shulker/undead rows: `spiritus`, `praecantatio`, `volatus`, `tenebrae`, `sensus`, `aversio`. |

These rows are not 1.12 parity rows because the entities did not exist in 1.12.2. They are policy rows and should be reviewed again when research entries for new vanilla discoveries are designed.

## Remaining Gaps

| Gap | Status | Reason |
|---|---|---|
| Thaumcraft custom entities | Deferred | Firebat, Pech, Wisp, taint mobs, cultists, golems and bosses are not ported/registered yet. Their legacy aspect rows should be copied when the entities exist. |
| NBT-specific Wisp aspects | Deferred | Requires Thaumcraft Wisp entity and stable modern data field for the aspect type. |
| Entity scan parity dump | Implemented | Current report has `83/85` comparable rows fully parity-ok, `2` expected modern entity aspect policy rows (`elder_guardian`, `zombie_villager`), and `0` actionable gaps. |
| Real scan reward mutation | Deferred | `ScanningManager.scanTheThing` exists, but Thaumometer still uses dry-run feedback until research categories/reward sync are exact. |
