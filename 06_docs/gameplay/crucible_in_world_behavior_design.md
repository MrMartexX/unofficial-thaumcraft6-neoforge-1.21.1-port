# In-world Crucible Behavior Boundary Design

Last updated: 2026-06-18

## Purpose

This document defines the first safe implementation boundary for in-world crucible gameplay after the recipe/page-data migration. It exists because `thaumcraft:crucible` recipes are currently valid data and Thaumonomicon page snapshots, but they are not active in-world crafting behavior.

## Current state

- `thaumcraft:crucible` recipe JSONs are reloadable data resources.
- Crucible recipe pages can render data snapshots in the Thaumonomicon.
- The current crucible recipe boundary is intentionally page/data only.
- In-world crafting, boiling behavior, item absorption, flux/taint side effects, essentia interaction, client particles and automation are still deferred.

## First allowed gameplay slice

The first implementation slice may add only a minimal, server-owned crucible crafting path:

1. A focused block/entity or server-side state holder for the existing crucible block.
2. Server-only item insertion handling for a single catalyst item stack.
3. Recipe lookup against loaded `TCCrucibleRecipe` instances.
4. Aspect-cost validation using the recipe data already present in JSON.
5. Result spawning or insertion with no automation, no essentia network, and no client-only authority.
6. A small audit or test path proving that all active crucible recipe JSON files remain reload-valid.

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
2. `tools/ci/server-smoke.ps1 -TimeoutSeconds 420 -KillStaleRunServer`.
3. `tools/audits/audit-crucible-recipe-data.ps1`.
4. `tools/audits/audit-research-recipe-page-gaps.ps1`.
5. A manual or automated test for at least one simple data recipe such as nitor/alumentum or a vis crystal recipe before any broader behavior is marked complete.

## First implementation checklist

- Add a small server-side crucible behavior class or block entity boundary.
- Keep it disabled from automation and non-player interactions in the first pass.
- Add a recipe lookup helper that does not mutate the recipe object.
- Add a strict validation path for catalyst, result and aspect cost.
- Add one minimal happy-path craft test and one failed-craft test.
- Update this document after the first behavior slice lands.