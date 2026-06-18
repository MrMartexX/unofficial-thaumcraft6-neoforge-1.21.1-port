# In-world Crucible Behavior Boundary Design

Last updated: 2026-06-18

## Purpose

This document defines the first safe implementation boundary for in-world crucible gameplay after the recipe/page-data migration. It exists because `thaumcraft:crucible` recipes are valid data and Thaumonomicon page snapshots, while real in-world behavior must be added in narrow, auditable slices.

## Current state

- `thaumcraft:crucible` recipe JSONs are reloadable data resources.
- Crucible recipe pages can render data snapshots in the Thaumonomicon.
- A first in-world server behavior slice exists through `TCCrucibleBlock`, `TCCrucibleBlockEntity`, and `TCCrucibleRecipeMatcher`.
- Current behavior covers manual water-bucket fill, server heat tracking from legacy-compatible heat-source blocks, manual top-side item insertion, existing item-aspect dissolution, research-gated recipe lookup, highest-aspect-cost recipe selection, aspect-cost removal, water drain and result ejection.
- A second server-owned slice covers legacy-style item entity collision absorption and living-entity contact damage while hot and filled.
- Craft ejection marks result entities with a persistent special-item marker, replacing legacy `EntitySpecialItem` for the current non-custom-entity slice and preventing immediate reabsorption.
- A third server-owned slice covers legacy spill pollution through the existing modern `AuraHelper` facade: periodic/overflow `spillRandom` removes one aspect and adds flux, while `spillRemnants` converts remaining aspects into aura flux.
- The seven HEDGE_ALCHEMY recipes that legacy built with dynamic `AspectList(ItemStack)` formulas now have explicit JSON aspect costs resolved from the current parity data; this keeps runtime reload and page snapshots deterministic.
- Taint side effects, special alchemy behavior, essentia interaction, client particles, Thaumatorium, jars, alembics, tubes and automation are still deferred.

## First allowed gameplay slice

The first implementation slice may add only a minimal, server-owned crucible crafting path:

1. A focused block/entity or server-side state holder for the existing crucible block. Done for the first slice.
2. Server-only item insertion handling for a single catalyst item stack. Done for manual top-side use.
3. Recipe lookup against loaded `TCCrucibleRecipe` instances. Done through `TCCrucibleRecipeMatcher`.
4. Aspect-cost validation using the recipe data already present in JSON. Done for lookup and craft mutation.
5. Result spawning or insertion with no automation, no essentia network, and no client-only authority. Done through server-side item ejection.
6. A small audit or test path proving that all active crucible recipe JSON files remain reload-valid. Done through the data audit plus the first behavior audit.

## Explicit non-goals for the first slice

Do not include these in the first implementation slice:

- Full legacy essentia mechanics.
- Alembic or jar integration.
- Automation from tubes, hoppers or golems.
- Taint, liquid death, special alchemy side effects or biome effects.
- Client particle parity.
- Thaumatorium behavior.
- Recipe-derived aspect generation beyond the existing page-data/audit path.

## Second allowed gameplay slice

The second slice may add collision behavior that is directly present in legacy `BlockCrucible.onEntityCollidedWithBlock` and `TileCrucible.attemptSmelt(EntityItem)`:

1. Server-only `ItemEntity` absorption when water is present and heat is at least 151.
2. Ignore entities marked as modern replacements for legacy `EntitySpecialItem`.
3. Preserve legacy stack-loop semantics: `attemptSmelt(ItemStack)` mutates the remaining stack counter while iterating, so multi-item entity stacks may consume roughly half the current stack per collision pass rather than exactly one item.
4. Resolve player ownership from the modern item entity owner when present; if no live server player is available, research-gated recipes must not craft, but ordinary aspect dissolution still works.
5. Preserve the hot-crucible living contact damage: one `inFire` damage with lava-extinguish sound after the same block-instance collision delay used by legacy.
6. Do not add client bubbles, spill particles, flux pollution, item pulling radius, hopper behavior or special alchemy side effects in this slice.

## Third allowed gameplay slice

The third slice may add server-side spill pollution because the modern aura core already provides the equivalent public `AuraHelper.polluteAura` facade:

1. Preserve `TileCrucible.update` spill timing: a `counter` starts at `-100`, increments once per server tick, periodically calls `spillRandom` at `>= 100`, and resets to `0`.
2. Preserve overflow pressure: if the aspect pool is above `500`, call `spillRandom` every server tick.
3. Preserve `attemptSmelt` counter resets: `-250` after a craft and `-150` after an aspect dissolution.
4. Preserve `spillRandom`: remove one random aspect and pollute the aura by `1.0` for flux/vitium or `0.25` for any other aspect.
5. Preserve `spillRemnants`: remove all water/aspects, pollute `visSize * 0.25`, and add an extra `fluxAspectAmount * 0.75` when flux/vitium is present.
6. Do not add flux rifts, taint spread, liquid death side effects, client froth/bubbles, block event particles, Thaumatorium behavior or essentia network interactions in this slice.

## Legacy collision audit

Relevant legacy behavior checked before the second slice:

- `BlockCrucible.onEntityCollidedWithBlock` processes server-side `EntityItem` collisions only when the colliding entity is not `EntitySpecialItem`, `heat > 150`, and the tank has water.
- Non-item living entities are damaged through `DamageSource.IN_FIRE` after a block-instance `delay` reaches 10, with `BLOCK_LAVA_EXTINGUISH` at volume `0.4` and pitch `2.0 + rand * 0.4`.
- `TileCrucible.attemptSmelt(EntityItem)` reads the toss owner from entity data key `thrower`, calls `attemptSmelt(ItemStack, username)`, and writes the returned remaining stack back to the entity or kills it when empty.
- `TileCrucible.attemptSmelt(ItemStack, username)` tries recipe crafting first; if no recipe matches, it dissolves the current item aspects. Recipes require water at the moment of the craft, but the dissolution branch is governed by the initial collision/activation gate.
- `TileCrucible.ejectItem` spawns `EntitySpecialItem`, which is why modern ejected results must be marked to opt out of crucible absorption until a custom special item entity exists.
- `TileCrucible.update` calls `spillRandom` if `aspects.visSize() > 500` or once `counter >= 100`; `TileCrucible.spillRandom` always marks/syncs after optionally removing one aspect.
- `TileCrucible.spillRemnants` does not reset heat immediately; heat decays through normal ticking after the water is removed.

## Compatibility rules

- Keep recipe existence separate from research unlock.
- Do not make Thaumonomicon page availability depend on in-world behavior.
- Keep all server authority on the server thread.
- If the recipe model needs to change, provide a data migration or audit before touching recipe JSON files.
- Existing `thaumcraft:crucible` data should remain loadable on dedicated server smoke.

## Validation path

Every crucible behavior change must pass:

1. `gradlew.bat build --no-daemon`.
2. `tools/ci/server-smoke.ps1 -TimeoutSeconds 420 -WorldName tc_server_smoke -ServerPort 0` for local isolated smoke, or the CI default smoke command in a clean workspace.
3. `tools/audits/audit-crucible-recipe-data.ps1`.
4. `tools/audits/audit-research-recipe-page-gaps.ps1`.
5. `tools/audits/audit-crucible-behavior.ps1`.
6. A manual in-game check for water fill, heat source, aspect dissolution, item entity absorption, living contact damage and a known-research recipe before any broader behavior is marked complete.

## First implementation checklist

- Done: add a small server-side crucible behavior class or block entity boundary.
- Done: keep it disabled from automation and non-player interactions in the first pass.
- Done: add a recipe lookup helper that does not mutate the recipe object.
- Done: add a strict validation path for catalyst, result and aspect cost.
- Done: add one minimal happy-path recipe-match test and one failed recipe-match test.
- Done: update this document after the first behavior slice lands.
- Done: audit legacy collision behavior before adding item-entity absorption.
- Done: add item-entity absorption with a modern special-item marker boundary.
- Done: add hot living-entity contact damage with the legacy delay threshold.
- Done: audit and implement server-side spill pollution through the existing aura facade.

## Next behavior checklist

- Add a controlled live-player manual test path for a known research key rather than bypassing research.
- Keep empty-bucket draining out unless a later audit finds a real TC6 legacy path; legacy right-click fill uses fluid containers, while sneak-empty-hand spills the crucible.
- Do not add client boil/froth/bubble particles until the rendering/FX slice is opened.
- Do not add flux rifts, taint spread, liquid death or special alchemy side effects until their own legacy source audit and validation path exist.
