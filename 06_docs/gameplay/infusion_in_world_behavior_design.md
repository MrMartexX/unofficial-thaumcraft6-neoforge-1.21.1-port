# In-world Infusion Behavior Boundary Design

Last updated: 2026-06-19

## Purpose

This document defines the first safe implementation boundary for in-world infusion gameplay after the infusion recipe/page-data migration. The current `thaumcraft:infusion` recipe serializer and Thaumonomicon page snapshots are data/page infrastructure only. They are not an infusion altar, matrix or pedestal behavior implementation.

## Current state

- `thaumcraft:infusion` recipe JSONs are reloadable data resources.
- Infusion recipe pages can render data snapshots in the Thaumonomicon.
- `TCInfusionRecipeMatcher` validates catalyst, exact-count unordered pedestal components, and aspect costs without mutation.
- `TCInfusionAssembly` and `TCInfusionValidationResult` provide the first server-owned validation snapshot/result boundary.
- `TCInfusionCraftingPlan` and `TCInfusionStartResult` provide the first saved server-owned crafting-start state after validation.
- `TCInfusionCompletionPlan` provides the first read-only active-plan readiness check against the current center pedestal, originally matched component pedestal positions, and available aspects.
- `tools/audits/audit-infusion-behavior.ps1` runs the current server runtime audit; latest result is `25/25` checks passing after the matrix/pedestal start-plan plus completion-readiness slice.
- Custom recipe boundary audit recognizes `thaumcraft:infusion` as `INFUSION_PAGE_READY_NO_GAMEPLAY`.
- `TCInfusionMatrixBlockEntity` scans the legacy matrix-centered pedestal range and feeds a `TCInfusionAssembly` snapshot.
- `TCInfusionPedestalBlockEntity` owns the first legacy-shaped one-slot pedestal state: empty pedestal accepts one item, occupied pedestal returns/drops its stored item.
- Full item/aspect mutation timing, instability events, essentia/aura side effects, client particles/beams and completion output behavior are still deferred.

## First allowed gameplay slice

The first implementation slice may add only a minimal, server-owned infusion validation and state boundary:

1. Done: a focused BlockEntity relationship for matrix and pedestal discovery.
2. Done: server-side recipe lookup against loaded `TCInfusionRecipe` instances.
3. Done: catalyst, component multiset and aspect-cost validation without mutating recipe objects.
4. Done: deterministic validation/audit path for the current recipe JSON structure.
5. Done: minimal happy-path and failed validation scenarios, including runtime world placement.
6. Done: saved active crafting start plan recording recipe id, research, instability, catalyst, matched component stacks, matched pedestal positions, required aspects, result and player name.
7. Done: read-only completion/readiness planning that rechecks the current catalyst, originally matched component pedestal stacks/positions and available aspect totals before any future mutation.
8. Still required: no client authority and no implicit crafting from client-side state for later slices.

## Legacy crafting-start semantics

Legacy `TileInfusionMatrix.craftingStart(EntityPlayer)` is the reference for the current boundary:

- It first rejects invalid matrix/pedestal structure, missing center catalyst, and empty component lists.
- It calls `getSurroundings()` to collect surrounding pedestals and stability/cost context.
- It calls `ThaumcraftCraftingManager.findMatchingInfusionRecipe(components, recipeInput, player)`.
- If a recipe is found, it records recipe type, input component copies, output, instability, required essentia list and player name, then sets `crafting = true`.
- It does not immediately consume the center catalyst, component pedestal items, or essentia at start.
- Later `craftCycle()` revalidates the center catalyst, drains essentia one unit at a time, consumes matching component pedestal items, and only then calls `craftingFinish(...)`.

The port now mirrors the start-state boundary and the first non-mutating craft-cycle readiness check: it records a validated plan, can verify whether that plan still matches the current world/aspect state, and leaves all actual mutation for a later atomic consumption/essentia slice.

## Explicit non-goals for the first slice

Do not include these in the first implementation slice:

- Full altar animation, beam rendering or particle parity.
- Instability events beyond inert data validation.
- Item tossing/consumption timing from legacy code.
- Essentia transport, jars, tubes or golem automation.
- Flux rifts, taint spread or biome/world side effects.
- Thaumatorium behavior.
- Broad pedestal inventory UI beyond the legacy one-item right-click slot.
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
- Done: minimal matrix/pedestal BlockEntity relationship that feeds `TCInfusionAssembly` but still does not consume items/aspects or run instability/FX.
- Done: `TCInfusionCraftingPlan` active start state with NBT round-trip, second-start rejection and abort audit coverage.
- Done: `TCInfusionCompletionPlan` read-only readiness state with catalyst, component-stack, component-pedestal and missing-aspect audit coverage.
- Still deferred: item consumption timing, essentia drain/source integration, instability, particles/beams/sounds and completion output behavior.
## Validation helper note

- `TCInfusionRecipeMatcher` provides the first server-side validation helper for catalyst, unordered pedestal components and aspect costs.
- The helper now mirrors the legacy `RecipeMatcher.findMatches` constraint: component order is flexible, but the number of supplied pedestal items must exactly equal the recipe component count.
- The helper is intentionally non-mutating and does not activate matrix crafting, pedestal UI, instability events or visual effects.
- Future in-world infusion behavior should build from the active `TCInfusionCraftingPlan` and the audited `TCInfusionCompletionPlan` before consuming items or mutating aura/aspect state.
## Legacy cycle semantics audit note

- `infusion_legacy_cycle_semantics_audit.md` records the legacy `craftingStart`, `craftCycle`, `craftingFinish`, and `getSurroundings` anchors before adding any mutation executor.
- The next safe implementation step is not a player-facing one-shot craft trigger. It is an audited mutation/executor boundary built on `TCInfusionCraftingPlan` and `TCInfusionCompletionPlan`.
- Container-item behavior and essentia drain/source timing remain unresolved parity risks and must stay separate from instability/FX work.
## Container and essentia cycle audit note

- `infusion_legacy_container_essentia_cycle_audit.md` narrows the legacy audit from broad method anchors to item/container and essentia/source timing.
- The next code slice may introduce a non-player-facing mutation executor, but it must be audit-only until the timing policy is proven.
- Container item handling and real essentia network/source drain remain separate parity risks and must not be mixed with instability/FX work.
## Mutation executor audit boundary note

- `TCInfusionMutationExecutor` is an audit-only, non-player-facing boundary that executes a previously validated `TCInfusionCompletionPlan`.
- It rechecks center catalyst and component pedestals before mutation, consumes matched component pedestal stacks, places the result on the center pedestal, and clears the active plan.
- It does not implement instability, beams, particles, sounds, essentia network drain, jars/tubes, flux, taint, Thaumatorium or golem automation.
## Container remainder audit note

- `infusion_container_item_remainder_audit.md` scans current infusion catalyst/component data for known vanilla container or crafting-remainder inputs.
- The current data set must remain free of unhandled container/remainder inputs before the audit-only mutation executor is considered safe for the current recipes.
- Re-run this audit whenever infusion recipe data or tag-based ingredients change.