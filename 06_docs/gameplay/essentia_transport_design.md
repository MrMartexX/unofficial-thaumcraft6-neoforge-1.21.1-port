# Essentia Transport Design

Last updated: 2026-06-22

## Purpose

This document owns the NeoForge 1.21.1 boundary for TC6 essentia tubes, Warded Jars,
Alembics and smelters. These are not generic fluid pipes. Legacy suction, one-point cadence,
machine ownership and side rules remain authoritative.

## Legacy authority

Behavior is checked against:

- `thaumcraft.api.aspects.IEssentiaTransport`;
- `TileTube` and all five specialized tube subclasses;
- `TileJarFillable`;
- `TileAlembic` and `BlockAlembic`;
- `TileSmelter`, `BlockSmelterAux`, `BlockSmelterVent`;
- `ContainerSmelter` and `GuiSmelter`;
- `TileBellows` and `BlockBellows`;
- `ThaumcraftApiHelper.getConnectableTile`;
- legacy multipart/model resources.

## Transport rules

| Legacy rule | Port behavior |
|---|---|
| Normal tube stores at most one point | Capacity `1`; one-point add/take |
| Buffer stores mixed essentia | Capacity `10`; one-point calls only |
| Normal suction propagation | Strongest compatible neighbor minus one |
| Restrict tube | Integer half of propagated suction |
| Filter tube | Only matching typed suction propagates |
| One-way tube | Legacy facing controls propagation/equalization |
| Valve | Handle face disconnected; redstone closes flow |
| Buffer choke | Levels `0/1/2` mean normal/`1`/`0` suction |
| Tick cadence | Suction conflict every two ticks; transfer every five |
| Warded Jar | Top face, capacity `250`, suction `32/64`, one point per five ticks |
| Alembic | Capacity `128`, one aspect, output only, zero suction |
| Side closure | Open sides persist and reciprocal tube sides synchronize |

## Smelter rules

| Area | Preserved behavior |
|---|---|
| Inventory | Input slot `0`, fuel slot `1`; sides input, bottom fuel, top none |
| Slurry | Maximum `256` aspect points |
| Tiers | Basic `0.8 / 15`, Thaumium `0.9 / 10`, Void `0.95 / 15` |
| Smelt duration | `vis * 2 * (1 - 0.125 * bellows)` |
| Fuels | Alumentum `4800`, Greatwood `500`, Silverwood `400` |
| Alumentum | Distillation interval multiplied by `0.8` |
| Efficiency | Per-point loss; Flux uses `efficiency * 0.66` |
| Vents | Each valid non-front vent gets an independent `0.333` chance per lost point |
| Alembic selection | Matching filled Alembic first, then first compatible empty/filter Alembic |
| Aux output | Main column and every valid attached auxiliary pump may each move one point per cycle |

## Modern architecture

- `TCEssentiaTransport` is the internal legacy-shaped domain contract.
- `TCEssentiaCapabilities.BLOCK` exposes only transport participants: tubes, jars and Alembics.
- Smelters are internal slurry machines and deliberately do not expose essentia transport capability.
- `TCMachineCapabilities` exposes smelter inventory through NeoForge's sided item capability.
- Machine state is server-owned and persisted through BlockEntity NBT.
- Menu fields use DataSlots; the client cannot author fuel, progress, slurry or output state.
- Blockstate/model JSON owns static geometry; client-only dynamic renderers remain separate.

## Current implementation

- Six tube variants and Warded Jar.
- Alembic storage, filter state, comparator output, empty-to-flux behavior and output capability.
- Matching-first contiguous Alembic column processing.
- Basic, Thaumium and Void smelter machine tiers.
- Fuel/cook/aspect conversion, efficiency loss and aura pollution.
- Bellows count input for smelter duration.
- Bellows focused device slice: real BlockEntity, legacy client animation, tube-buffer extension render, tube-buffer pressure ownership and vanilla-furnace cook-progress boost.
- Cumulative facing-aware vent mitigation and vent event FX.
- Facing/attachment-aware auxiliary output.
- Sided item automation.
- Legacy-layout smelter menu/screen.
- Detailed legacy-derived Alembic, Bellows, auxiliary and vent models.

## Validation

Run:

```powershell
.\tools\audits\audit-smelter-runtime-boundary.ps1
```

This first validates that detailed models have not regressed to cubes, then runs the dedicated-server
transport/machine fixtures. Current result: **37/37 passed**.

The exporter validates:

- capacities, suction, transfer cadence, valve/filter/one-way/choke rules;
- reciprocal side closure and capability visibility;
- tube NBT and Warded Jar timing;
- Alembic storage, sided output, filter and column priority;
- tier formulas and exact custom fuel values;
- sided smelter inventory and non-exposure as an essentia endpoint;
- Alumentum boost;
- direct plus auxiliary output in the same cycle;
- vent selection and upgraded tier ownership.

## Deferred owning slices

1. Alembic label application/removal and label renderer.
2. Phial/jar direct transfer using the current aspect Data Component model.
3. Caster tube sub-part ray tracing, side closure/choke/facing controls.
4. Final valve wheel, vent and Bellows measured visual parity.
5. Void Jar overflow and remaining importer/exporter/Thaumatorium consumers.

## Non-negotiable constraints

- Do not expose smelters as `TCEssentiaTransport`.
- Do not replace one-point essentia movement with generic fluid transfer.
- Do not flatten multiple Alembic columns or auxiliary outputs into a single destination.
- Do not use client state to decide conversion, loss, venting or transfer.
- Do not replace detailed active models with cube placeholders.
