# Aura Design

Source basis:

- `06_docs/NeoForge_legacy_migration_guide.md`
- `06_docs/subsystem_inventory.md`
- `06_docs/porting_order.md`
- Legacy `thaumcraft.api.aura.AuraHelper`
- Legacy `thaumcraft.common.world.aura.AuraHandler`
- Legacy `thaumcraft.common.world.aura.AuraWorld`
- Legacy `thaumcraft.common.world.aura.AuraChunk`
- Legacy `thaumcraft.common.world.aura.AuraThread`
- Legacy `thaumcraft.common.lib.events.ChunkEvents`
- Legacy `thaumcraft.common.lib.events.WorldEvents`
- Legacy `thaumcraft.common.lib.events.ServerEvents`
- Legacy `thaumcraft.common.lib.network.misc.PacketAuraToClient`

## Legacy behavior summary

Thaumcraft 6 stores aura as local chunk data:

- `base`: local aura baseline, generated from biome aura modifiers and noise.
- `vis`: current usable aura.
- `flux`: local pollution.

The public API is `AuraHelper`:

- `drainVis(world, pos, amount, simulate)`
- `drainFlux(world, pos, amount, simulate)`
- `addVis(world, pos, amount)`
- `polluteAura(world, pos, amount, showEffect)`
- `getVis(world, pos)`
- `getFlux(world, pos)`
- `getAuraBase(world, pos)`
- `shouldPreserveAura(world, player, pos)`

The full legacy implementation depends on:

- chunk save/load events writing a `Thaumcraft` NBT subtag into each chunk;
- static dimension maps in `AuraHandler`;
- a background `AuraThread` per dimension;
- direct world access from that background thread;
- clientbound aura packets for thaumometer/caster HUD;
- biome aura generation from `BiomeHandler`;
- flux rift creation and taint consumers.

## NeoForge target

The guide requires replacing legacy static world maps and off-thread world mutation with server-owned modern storage. The first aura slice uses:

- `SavedData` per `ServerLevel` for persistent aura chunks;
- explicit chunk keys from `ChunkPos`;
- main-server-thread tick processing;
- a public `thaumcraft.api.aura.AuraHelper` compatibility facade;
- read-only clientbound `aura_sync` custom payload and client cache;
- debug commands for inspection and controlled seeding during port validation.

This first slice intentionally does not implement:

- worldgen feature/block placement integration;
- final HUD rendering;
- particles or sounds;
- flux rift spawning;
- taint behavior;
- research-based aura preservation checks for real players;
- vis consumers from caster/tools/machines;
- custom payload networking.

## Storage model

`TCAuraSavedData` stores only initialized chunks:

| Field | Type | Notes |
|---|---|---|
| chunk x/z | int | Stored as exact chunk coordinates and as `ChunkPos` long keys in memory. |
| base | int | Clamped to `0..500`, matching legacy generated ceiling. |
| vis | float | Clamped to `0..32766`, matching legacy safety ceiling. |
| flux | float | Clamped to `0..32766`, matching legacy safety ceiling. |

Loaded chunks are initialized automatically when `generateAura = true`. Manual debug seeding still exists for validation and can overwrite the current chunk with `base=vis`, `flux=0`.

Saved aura chunks and runtime-loaded aura chunks are intentionally separate. `SavedData` keeps all known aura chunk values so they persist, but the 20-tick aura update loop only processes chunks currently marked loaded by `ChunkEvent.Load`. `ChunkEvent.Unload` removes the key from the runtime loaded set without deleting saved aura data. This mirrors the legacy split between chunk NBT persistence and the active `AuraWorld` runtime map.

## Aura generation

Legacy `AuraHandler.generateAura` samples the biome at the target chunk center and the four horizontal neighbor chunk centers:

- block sample: `chunkX * 16 + 8`, `y = 50`, `chunkZ * 16 + 8`;
- aura life: average of the five biome aura modifiers;
- base: `life * 500 * (1 + random.nextGaussian() * 0.10000000149011612)`;
- clamp: `0..500`;
- initial values: `vis = base`, `flux = 0`.

The port preserves that formula and uses the same deterministic chunk random seeding pattern used by the legacy regeneration path:

```java
Random random = new Random(worldSeed);
long xSeed = random.nextLong() >> 3;
long zSeed = random.nextLong() >> 3;
random.setSeed(xSeed * chunkX + zSeed * chunkZ ^ worldSeed);
```

The part that cannot be byte-identical is the biome category source. Forge 1.12.2 used `BiomeDictionary.Type`, while Minecraft/NeoForge 1.21.1 uses biome registry keys, vanilla biome tags, and climate settings. `TCAuraBiomeModifiers` maps modern biomes back to legacy-like modifier values:

| Modern source | Legacy-like modifier |
|---|---:|
| ocean | `0.33` |
| river | `0.4` |
| nether | `0.125` |
| end | `0.125` |
| badlands / mesa | `0.33` |
| taiga / coniferous | `0.33` |
| forest | `0.5` |
| jungle | `0.6` |
| savanna | `0.25` |
| mountain | `0.3` |
| hill | `0.33` |
| beach | `0.3` |
| plains / meadow | `0.3` |
| desert / sandy | `0.25` |
| swamp | `0.5` |
| mushroom fields | `0.75` |
| lush caves / cherry grove | `0.5` |
| dripstone caves / stony biomes | `0.3` |
| deep dark | `0.3` |
| dry climate fallback | `0.125` |
| wet climate fallback | `0.4` |
| cold/snow fallback | `0.25` |

When multiple modern categories apply, the port averages them, matching legacy `getBiomeAuraModifier`.

## Tick model

Legacy `AuraThread` runs about once per second. The modern first slice runs on the logical server thread every 20 server ticks:

- compute moon phase from `dayTime / 24000 % 8`;
- use legacy `phaseTable` and `maxTable`;
- move up to `1` vis toward the lowest neighboring chunk when the neighbor is sufficiently lower and not over effective base;
- move up to `1` flux toward the lowest flux neighbor when flux is above the legacy threshold;
- regenerate vis toward effective base by the legacy phase amount;
- convert excess/low vis pressure into flux with the legacy probability rule.

Rift triggering is recorded as a later entity/world effect and is not spawned in this slice.

## Public API boundary

`AuraHelper` is preserved as the external query/mutation contract, but with modern types:

- `Level` instead of `World`;
- `BlockPos` remains;
- `Player` instead of `EntityPlayer`.

Client levels and missing chunks return zero/no-op. This keeps server authority and prevents client-side gameplay mutation.

`shouldPreserveAura` currently supports the legacy null-player behavior only. Real player research checks must wait for the research subsystem.

## Network boundary

Legacy `PacketAuraToClient` sent `base`, `vis`, and `flux` to client HUD consumers. The port now has the same read-only data boundary:

- payload id: `thaumcraft:aura_sync`;
- fields: `chunkX`, `chunkZ`, `base`, `vis`, `flux`;
- direction: server to client;
- handler: stores the payload in `TCAuraClientCache`;
- current sender: debug command and future thaumometer/caster callers.

The payload does not let the client mutate aura. Final HUD drawing and item behavior remain separate work.

## Debug commands

The initial port registers permission-level-2 commands:

- `/thaumcraft aura get`
- `/thaumcraft aura stats`
- `/thaumcraft aura sync`
- `/thaumcraft aura seed [base]`
- `/thaumcraft aura add_vis <amount>`
- `/thaumcraft aura add_flux <amount>`
- `/thaumcraft aura drain_vis <amount>`
- `/thaumcraft aura drain_flux <amount>`

They are validation tools, not final gameplay UI.

## Blockers

- HUD sync requires the networking stage.
- Thaumometer/caster display requires item behavior and client UI work.
- Flux rifts require entity registration and world effect policy.
- Research-aware preserve checks require player knowledge storage.
- Vis drains from arcane crafting, machines, and casters require their own subsystem ports.

## Implementation checklist

1. Add `AuraHelper` API facade.
2. Add server-side aura chunk value object.
3. Add `SavedData` storage and NBT serialization.
4. Add `AuraHandler` query/mutation/tick methods.
5. Add server tick event registration.
6. Add debug commands.
7. Add automatic chunk aura initialization.
8. Add read-only clientbound aura sync payload and client cache.
9. Validate build, dedicated server startup, saved-data sanity, and aspect parity.
10. Keep `current_port_status.md` and `migration_matrix.md` in sync.
