# In-world Crucible Behavior Boundary Design

Last updated: 2026-06-18

## Purpose

This document defines the first safe implementation boundary for in-world crucible gameplay after the recipe/page-data migration. It exists because `thaumcraft:crucible` recipes are valid data and Thaumonomicon page snapshots, while real in-world behavior must be added in narrow, auditable slices.

## Current state

- `thaumcraft:crucible` recipe JSONs are reloadable data resources.
- Crucible recipe pages can render data snapshots in the Thaumonomicon.
- A first in-world server behavior slice exists through `TCCrucibleBlock`, `TCCrucibleBlockEntity`, and `TCCrucibleRecipeMatcher`.
- Current behavior covers manual water-bucket fill, server heat tracking from legacy-compatible heat-source blocks, manual top-side item insertion, existing item-aspect dissolution, research-gated recipe lookup, highest-aspect-cost recipe selection, aspect-cost removal, water drain and result ejection.
- The seven HEDGE_ALCHEMY recipes that legacy built with dynamic `AspectList(ItemStack)` formulas now have explicit JSON aspect costs resolved from the current parity data; this keeps runtime reload and page snapshots deterministic.
- Flux/taint side effects, special alchemy behavior, item-entity collision/suction, essentia interaction, client particles, Thaumatorium, jars, alembics, tubes and automation are still deferred.

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
- Flux, taint, pollution, liquid death, special alchemy side effects or biome effects.
- Client particle parity.
- Thaumatorium behavior.
- Broad item entity suction or delayed recipe chaining.
- Recipe-derived aspect generation beyond the existing page-data/audit path.

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
6. A manual in-game check for water fill, heat source, aspect dissolution and a known-research recipe before any broader behavior is marked complete.

## First implementation checklist

- Done: add a small server-side crucible behavior class or block entity boundary.
- Done: keep it disabled from automation and non-player interactions in the first pass.
- Done: add a recipe lookup helper that does not mutate the recipe object.
- Done: add a strict validation path for catalyst, result and aspect cost.
- Done: add one minimal happy-path recipe-match test and one failed recipe-match test.
- Done: update this document after the first behavior slice lands.

## Next behavior checklist

- Add a controlled live-player manual test path for a known research key rather than bypassing research.
- Decide exact water drain/fill bucket parity before adding empty-bucket draining.
- Audit legacy `TileCrucible.spillRandom` and `spillRemnants` before adding flux/taint pollution.
- Audit legacy `BlockCrucible.onEntityCollidedWithBlock` before adding item-entity absorption.
- Do not add client boil/froth/bubble particles until the rendering/FX slice is opened.
