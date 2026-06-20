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

1. Port `getSurroundings()` structure validation and modifiers:
   - four required pillars;
   - ancient/eldritch pillar cycle/cost/stability modifiers;
   - `matrix_speed` and `matrix_cost` modifiers;
   - stabilizer discovery and cached surroundings invalidation.
2. Port stability state and per-cycle loss/replenishment.
3. Port instability event selection and effects in dependency-safe families, with no silent substitution for missing blocks/entities/effects.
4. Add start/fail/finish state sync and activation audit.
5. Enable caster start only after failed paths cannot duplicate or delete items.

## Deferred boundaries

- exact polycone essentia stream and item/block debris renderers;
- essentia mirrors, alembics and addon source containers;
- enchantment/NBT-object infusion outputs beyond the current item-stack recipe model;
- Thaumatorium and golem automation;
- broad pedestal GUI;
- instability effects whose owning blocks/entities are not registered.

## Required validation

Every infusion batch must pass:

1. `05_neoforge_port/gradlew.bat build --no-daemon`;
2. `tools/audits/audit-infusion-recipe-data.ps1`;
3. `tools/audits/audit-infusion-behavior.ps1`;
4. `tools/audits/audit-infusion-tag-input-expansion.ps1` when accepted inputs change;
5. `tools/ci/server-smoke.ps1`;
6. client startup when payload, particle, sound, model or renderer code changes.

Current runtime result: `63/63` infusion behavior checks pass.
