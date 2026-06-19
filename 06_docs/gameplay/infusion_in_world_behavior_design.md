# In-world Infusion Behavior Boundary Design

Last updated: 2026-06-19

## Purpose

This document defines the first safe implementation boundary for in-world infusion gameplay after the infusion recipe/page-data migration. The current `thaumcraft:infusion` recipe serializer and Thaumonomicon page snapshots are data/page infrastructure only. They are not an infusion altar, matrix or pedestal behavior implementation.

## Current state

- `thaumcraft:infusion` recipe JSONs are reloadable data resources.
- Infusion recipe pages can render data snapshots in the Thaumonomicon.
- `TCInfusionRecipeMatcher` validates catalyst, exact-count unordered pedestal components, and aspect costs without mutation.
- `TCInfusionAssembly` and `TCInfusionValidationResult` provide the first server-owned validation snapshot/result boundary.
- `tools/audits/audit-infusion-behavior.ps1` runs the current server runtime audit; latest result is `9/9` checks passing.
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
4. `tools/audits/audit-infusion-behavior.ps1`.
5. `tools/audits/audit-research-recipe-page-gaps.ps1`.
6. A focused runtime audit must stay green before any in-world infusion behavior is considered implemented.

## First implementation checklist

- Done: small server-side recipe validation helper.
- Done: exact catalyst/component/aspect validation without consuming items.
- Done: legacy-compatible unordered component matching with exact 1:1 count semantics.
- Done: runtime audit/exporter for the current validation boundary.
- Next: minimal matrix/pedestal BlockEntity relationship that feeds `TCInfusionAssembly` but still does not consume items or run instability/FX.
- Still deferred: full matrix/pedestal inventory behavior, item consumption timing, essentia drain, instability, particles/beams/sounds and completion behavior.
## Validation helper note

- `TCInfusionRecipeMatcher` provides the first server-side validation helper for catalyst, unordered pedestal components and aspect costs.
- The helper now mirrors the legacy `RecipeMatcher.findMatches` constraint: component order is flexible, but the number of supplied pedestal items must exactly equal the recipe component count.
- The helper is intentionally non-mutating and does not activate matrix crafting, pedestal inventories, instability events or visual effects.
- Future in-world infusion behavior should call `TCInfusionAssembly.validateBest` or `TCInfusionAssembly.validateAgainst` before consuming items or mutating aura/aspect state.
