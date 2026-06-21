# Essentia Transport Design

Last updated: 2026-06-21

## Purpose

This document owns the NeoForge 1.21.1 transport boundary for the TC6 tube family and Warded Jar. It prevents the registered tube blocks from being mistaken for either generic fluid pipes or a completed smelter/alembic subsystem.

## Legacy authority

Behavior was checked against:

- `thaumcraft.api.aspects.IEssentiaTransport`;
- `TileTube`;
- `TileTubeBuffer`;
- `TileTubeFilter`;
- `TileTubeOneway`;
- `TileTubeRestrict`;
- `TileTubeValve`;
- `TileJarFillable`;
- `ThaumcraftApiHelper.getConnectableTile`;
- legacy tube multipart blockstates and tube core/side models.

## Preserved behavior

| Legacy rule | Port behavior |
|---|---|
| Normal tube stores at most one essentia point | Capacity is exactly `1`; add/take moves one point |
| Buffer stores mixed essentia | Total capacity is exactly `10`; only one-point add calls are accepted |
| Normal suction propagation | Strongest compatible neighbor suction is copied as `neighbor - 1` |
| Restrict tube | Propagated suction is integer `neighbor / 2` |
| Filter tube | Only matching typed suction propagates; empty filter remains untyped |
| One-way tube | Legacy facing tests control suction propagation and source equalization; input/output methods are not replaced with invented hard side gates |
| Valve | Handle-facing side is not connectable; indirect redstone power closes suction propagation |
| Buffer choke | Level `0` is normal, `1` forces suction `1`, `2` forces suction `0` |
| Tick cadence | Suction/vent conflict pass every `2` ticks; transfer/fill pass every `5` ticks |
| Warded Jar | Top face only, capacity `250`, suction `32` or filtered `64`, one-point pull every `5` ticks |
| Side closure | Six open-side values persist and reciprocal tube sides are synchronized |

## Modern architecture

- `TCEssentiaTransport` is the internal legacy-shaped domain contract.
- `TCEssentiaCapabilities.BLOCK` is the sided NeoForge `BlockCapability` boundary.
- Tube and Warded Jar providers return no capability for a disconnected side.
- Neighbor lookup uses the capability, not a direct cast to arbitrary block entities.
- Tube state remains server-owned and is persisted/synced through BlockEntity NBT/update tags.
- Multipart blockstates mirror the legacy six-direction model topology while using modern `thaumcraft:block/...` resource paths.

## Validation

Run:

```powershell
.\tools\audits\audit-essentia-transport-behavior.ps1
```

The runtime audit places real block entities in a dedicated server world and validates capacities, suction formulas, venting pause, direction/filter rules, transfer, multipart connection state, side capability visibility, reciprocal closure, valve power, NBT round-trip and Warded Jar timing.

## Explicitly incomplete

- Bellows block discovery and its live multiplier.
- Caster sub-part ray tracing and side/facing/choke interaction.
- Client vent particles/sounds and valve wheel BER animation.
- Filter label insertion/removal interaction and tint registration.
- Alembic/importer/exporter behavior.
- Smelter inventory, fuel, efficiency, flux and alembic output behavior.
- Void Jar overflow pollution.

The registered smelter endpoint skeletons are not evidence of completed legacy smelters and are not exposed through the new transport capability.

## Next dependency batch

1. Audit and port Alembic as the first real smelter output transport endpoint.
2. Replace the smelter endpoint skeleton with the real inventory/aspect machine model.
3. Add Bellows only after its block and neighbor contract are explicit.
4. Add caster sub-part interactions and client renderers after gameplay state is stable.
## Alembic source audit checkpoint

- Added `tools/audits/audit-legacy-alembic-transport-source.ps1` and `06_docs/audits/alembic_legacy_transport_source_audit.md`.
- Use this audit as the source evidence for the next Alembic endpoint batch.
- Do not expose Alembic capability or claim smelter completion until its sided transport and storage semantics are audited.
## Alembic endpoint checkpoint

- Added first Alembic block/entity transport endpoint boundary from legacy `TileAlembic` evidence.
- Preserves capacity `128`, single-aspect storage, optional filter, output-only sides excluding facing and down, zero suction, and exact requested take semantics.
- Exposes Alembic through `TCEssentiaCapabilities.BLOCK` after runtime audit coverage.
- Full smelter inventory/fuel/efficiency and real alembic production remain separate.
## Smelter machine model audit checkpoint

- Added `tools/audits/audit-legacy-smelter-machine-model.ps1` and `06_docs/audits/smelter_legacy_machine_model_audit.md`.
- Use this audit to replace the current smelter skeleton with a real inventory/aspect/fuel/efficiency model in one focused batch.
- Keep Bellows discovery, vent rendering, thaumatorium/importer/exporter and broad automation out of that first smelter machine batch.
## Smelter machine model checkpoint

- Added first `TCSmelterBlockEntity` machine-state boundary for the basic smelter.
- Preserves the legacy two-slot shape, aspect buffer cap `256`, default smelt time `100`, burn/cook fields, speed-boost flag and bellows counter.
- Encodes the legacy type constants: Basic efficiency `0.8`, Thaumium `0.9`, Void `0.95`, Basic/Void speed `15`, Thaumium speed `10`.
- This is still not full smelting: item aspect lookup, fuel consumption ticking, flux losses, vent behavior and Alembic production remain the next implementation slice.
## Smelter tick-state checkpoint

- Added the first server tick state progression for the basic smelter block entity.
- The tick decrements `furnaceBurnTime`, advances/reset `furnaceCookTime`, and synchronizes the block `enabled` state.
- This is intentionally pre-smelting: it does not yet consume fuel, convert input item aspects, apply flux loss, use vents, discover bellows or fill Alembics.
## Smelter fuel and input aspect checkpoint

- Added first basic smelter server processing path: vanilla fuel start/consume, cook-time progression and input `ItemStack` aspect conversion into the internal aspect buffer.
- This still uses full aspect conversion without legacy efficiency/flux loss and does not yet push buffered essentia into Alembics.
- Next slice should add efficiency/flux handling or Alembic output, but not both at once.
## Smelter Alembic output checkpoint

- Added the first basic smelter buffered essentia output path into an Alembic directly above the smelter.
- The smelter now attempts to move one aspect point every Basic smelter output-speed interval (`15` ticks), matching the current Basic speed boundary.
- This is intentionally narrower than legacy full output discovery: auxiliary smelter offsets, Bellows effects, vents and efficiency/flux loss remain separate slices.
## Smelter efficiency and pending flux checkpoint

- Added the first legacy-style efficiency loss calculation for basic smelter input aspect conversion.
- Basic smelter conversion now keeps each aspect point with `0.8` probability and records lost points as `pendingFlux`; flux aspect points use the legacy lower threshold multiplier `0.66`.
- This intentionally records pending flux only. Aura pollution and vent mitigation remain separate because they depend on aura/vent behavior boundaries.
## Smelter pending-flux aura bridge audit checkpoint

- Added `tools/audits/audit-smelter-flux-aura-bridge.ps1` and `06_docs/audits/smelter_flux_aura_bridge_audit.md`.
- Use this audit before wiring `pendingFlux` into aura pollution or adding vent mitigation.
## Smelter pending flux aura pollution checkpoint

- Wired smelter `pendingFlux` into the current `AuraHelper.polluteAura(Level, BlockPos, float, boolean)` API after conversion/output tick work.
- This mirrors the direct pollution portion of legacy `smeltItem()` after lost aspect points are counted.
- Vent mitigation is still separate and should intercept pending flux before this direct pollution in a future slice.
## Smelter vent placeholder and mitigation checkpoint

- Added a minimal `smelter_vent` placeholder block/id boundary where it was missing from `TCBlocks`.
- Added first placeholder-level pending-flux mitigation before aura pollution: adjacent `smelter_vent` gives each pending flux point a `0.333` mitigation chance.
- This still does not implement exact legacy vent facing or side placement parity.

## Smelter variant endpoint boundary checkpoint

- Added an audit documenting the boundary conflict between real legacy smelter variants and the current transport endpoint placeholder classes.
- `SMELTER_THAUMIUM` and `SMELTER_VOID` should not be activated as runtime machine variants until their transport endpoint role is resolved.

## Smelter machine type field checkpoint

- Added an explicit `SmelterType` ownership field to `TCSmelterBlockEntity` and routed runtime efficiency/output-speed through the instance type.
- Basic smelter behavior remains unchanged because the public constructor still defaults to `SmelterType.BASIC`.
- This prepares a later bridge where thaumium/void smelters can become real machine variants without changing the endpoint placeholder classes in the same slice.
