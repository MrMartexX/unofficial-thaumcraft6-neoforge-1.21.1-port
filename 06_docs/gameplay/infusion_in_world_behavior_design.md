# In-world Infusion Behavior Design

Last updated: 2026-06-20

## Purpose

This is the active design boundary for Thaumcraft 6 infusion gameplay on NeoForge 1.21.1. Legacy `TileInfusionMatrix`, `TilePedestal`, `EssentiaHandler` and the two infusion FX packets are behavior references. Server state, persistence, recipes and networking use modern APIs.

## Implemented server path

- `42/42` `thaumcraft:infusion` recipe JSON files load and render as Thaumonomicon page snapshots.
- Catalyst and pedestal components are matched server-side; components are unordered but exact 1:1 by count like legacy Forge `RecipeMatcher`.
- `TCInfusionCraftingPlan` persists recipe id, research, instability, catalyst, component snapshots/positions, aspect cost, output and initiating player name.
- `TCInfusionCycleState` persists remaining aspects, pending components, the component countdown, cycle delay and completed-cycle count.
- The ordinary matrix baseline is `cycleTime=10`, `countDelay=5`, matching `getSurroundings()` rather than the constructor-only pre-scan value.
- Every craft cycle revalidates the center catalyst.
- One aspect point is drained per cycle from the nearest unblocked `TCAspectSourceContainer` in legacy range 12.
- A failed source pass invalidates the cache and delays discovery for 200 ticks, equivalent to legacy `EssentiaHandler.DELAY=10000 ms` at 20 TPS.
- Each component takes one target cycle, four charging cycles and one consumption cycle. Completion occurs on the following cycle.
- Component crafting remainders remain on their original pedestal. The center catalyst is replaced by the result, as in legacy.
- If a damaged catalyst produces an undamaged damageable output, its damage ratio is transferred.
- Completion and catalyst-failure sounds use the legacy sound events.
- A valid altar requires the center pedestal and all four legacy pillar positions.
- Arcane, ancient, eldritch and mixed pillar sets reproduce the legacy cycle/cost/stability modifiers.
- `matrix_speed` and `matrix_cost` are read from the four blocks below the pillars and stack exactly like legacy.
- Ancient and eldritch surrounding pedestal cost modifiers are included before the legacy `0.5` cost floor and integer aspect truncation.
- The preserved public `IInfusionStabiliser` / `IInfusionStabiliserExt` contracts drive candle, skull and pedestal symmetry scanning. Matched pairs use per-block `0.75^n` diminishing returns; mismatched and unpaired candidates apply the legacy penalty.
- Craft plans persist the resolved cycle time/delay, cost multiplier and stability replenishment so save/load cannot silently revert altar modifiers.
- Matrix stability is persistent and follows the legacy `VERY_STABLE` / `STABLE` / `UNSTABLE` / `VERY_UNSTABLE` thresholds with loss modifiers `5/6/7/8`.
- Every cycle applies random instability loss, structure replenishment and the legacy `[-100,25]` clamp before catalyst/event handling.
- The event trigger preserves `nextInt(1500) <= abs(stability)` and all 24 legacy roll positions. Event recovery is added after the clamp exactly like legacy and is clamped only by the following cycle.
- Real effects are implemented for all 24 rolls: ordinary/Flux Goo/flux/explosive ejection, matrix explosion, warp, single/multi zap and single/multi harm.
- `flux_goo` preserves levels `0..7`, entity slowdown, 600-tick Vis Exhaust with `level/3` amplifier, legacy decay/aura pollution and exact ejection placement/sound. Finite-fluid spreading, Thaumic Slime spawning and the level-zero taint-fibre branch remain dependency-owned taint work.
- Flux Taint and Vis Exhaust are real registered effects. Infusion uses exact `120`/`2400` durations, ambient/particle flags and no-curative Vis Exhaust behavior. Flux Taint damages non-undead targets every `40 >> amplifier` ticks; healing of future `ITaintedMob` equivalents remains owned by the taint entity subsystem.
- Stabilizing inlay and pedestal charge use a bounded server-side network with legacy strength `0..15`, one-point attenuation per network step and source threshold `>=5`.
- Stabilizer energy persists, recharges one point per 20 ticks up to 15 at a cost of `0.25` aura flux, and absorbs ejection before any item mutation for the exact random cost `5..10`.
- Nearby players receive the `!INSTABILITY` discovery only after a real event executes.

For the Cloud Ring fixture (`50 aer`, two components), the audited sequence is `50 + 6 + 6 + 1 = 63` craft cycles.

## Implemented client protocol

- `TCInfusionEssentiaSourcePayload` is the modern equivalent of `PacketFXEssentiaSource`.
- `TCInfusionSourcePayload` is the modern equivalent of `PacketFXInfusionSource`.
- Packets are server-triggered and sent only to players within the legacy 32-block radius.
- Component source state uses the legacy 15-tick default and 60-tick pedestal lifetime.
- `TCInfusionClientFXCache` owns display state only; no recipe, inventory, aspect or progression mutation is client-authoritative.

The current generic billboard output is a visual bridge. Exact `FXEssentiaStream` polycone rendering and `FXBoreParticles` item/block debris are not complete and must not be called visual parity.

## Explicitly disabled player path

`TCInfusionMatrixBlock.isPlayerFacingCompletionEnabled()` remains `false`. Caster interaction is validation/status-only. The production ticker is real server code, but normal players cannot start it until the remaining server rules below are audited.

## Next server batch

1. Refresh surroundings when relevant blocks change while preserving server ownership and bounded scans.
2. Add inactive matrix activation/stability charging plus start/fail/finish state sync and activation audit.
3. Enable caster start only after failed paths cannot duplicate or delete items.
4. Complete finite Flux Goo flow, Thaumic Slime/taint-fibre dependencies and stabilizer-to-Flux-Rift behavior with their owning subsystems.

## Deferred boundaries

- exact polycone essentia stream and item/block debris renderers;
- essentia mirrors, alembics and addon source containers;
- enchantment/NBT-object infusion outputs beyond the current item-stack recipe model;
- Thaumatorium and golem automation;
- broad pedestal GUI;
- finite Flux Goo spreading, Thaumic Slime/taint-fibre transformations and Flux Rift stabilization;
- exact mob-effect icons and taint-specific healing behavior.

## Required validation

Every infusion batch must pass:

1. `05_neoforge_port/gradlew.bat build --no-daemon`;
2. `tools/audits/audit-infusion-recipe-data.ps1`;
3. `tools/audits/audit-infusion-behavior.ps1`;
4. `tools/audits/audit-infusion-tag-input-expansion.ps1` when accepted inputs change;
5. `tools/ci/server-smoke.ps1`;
6. client startup when payload, particle, sound, model or renderer code changes.

Current runtime result: `86/86` infusion behavior checks pass.
