# In-world Infusion Behavior Boundary Design

Last updated: 2026-06-19

## Purpose

This document defines the first safe implementation boundary for in-world infusion gameplay after the infusion recipe/page-data migration. The current `thaumcraft:infusion` recipe serializer and Thaumonomicon page snapshots are data/page infrastructure only. They are not an infusion altar, matrix or pedestal behavior implementation.

## Current state

- `thaumcraft:infusion` recipe JSONs are reloadable data resources.
- Infusion recipe pages can render data snapshots in the Thaumonomicon.
- Custom recipe boundary audit recognizes `thaumcraft:infusion` as `INFUSION_PAGE_READY_NO_GAMEPLAY`.
- In-world infusion matrix activation, pedestal inventories, instability events, essentia/aura side effects, item consumption timing, client particles and completion behavior are still deferred.

## First allowed gameplay slice

The first implementation slice may add only a minimal, server-owned infusion validation and state boundary:

1. A focused BlockEntity or server-side state holder for the infusion matrix and pedestal relationship.
2. Server-side recipe lookup against loaded `TCInfusionRecipe` instances.
3. Catalyst, component multiset and aspect-cost validation without mutating recipe objects.
4. A deterministic validation/audit path for the current recipe JSON structure.
5. One minimal happy-path validation scenario and one failed validation scenario.
6. No client authority and no implicit crafting from client-side state.

## Explicit non-goals for the first slice

Do not include these in the first implementation slice:

- Full altar animation, beam rendering or particle parity.
- Instability events beyond inert data validation.
- Item tossing/consumption timing from legacy code.
- Essentia transport, jars, tubes or golem automation.
- Flux rifts, taint spread or biome/world side effects.
- Thaumatorium behavior.
- Broad pedestal inventory UI.
- Curios/Baubles integration.
- Enchantment infusion behavior beyond data shape validation.

## Compatibility rules

- Keep recipe existence separate from research unlock.
- Do not make Thaumonomicon page availability depend on in-world altar behavior.
- Keep all state mutation server-authoritative.
- Treat legacy class names as behavior references, not copy targets.
- If the recipe model changes, update the audit before changing data files.

## Validation path

Every infusion behavior change must pass:

1. `gradlew.bat build --no-daemon`.
2. `tools/ci/server-smoke.ps1 -TimeoutSeconds 420 -KillStaleRunServer`.
3. `tools/audits/audit-infusion-recipe-data.ps1`.
4. `tools/audits/audit-research-recipe-page-gaps.ps1`.
5. A focused runtime audit before any in-world infusion behavior is considered implemented.

## First implementation checklist

- Add or confirm a small server-side recipe validation helper.
- Add exact catalyst/component/aspect validation without consuming items.
- Keep matrix/pedestal inventory behavior deferred until validation is stable.
- Add a runtime audit/exporter only after the validation helper exists.
- Update this document after the first behavior slice lands.