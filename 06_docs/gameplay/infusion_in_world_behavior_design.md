# In-world Infusion Behavior Boundary Design

Last updated: 2026-06-20

## Purpose

This document defines the first safe implementation boundary for in-world infusion gameplay after the infusion recipe/page-data migration. The current `thaumcraft:infusion` recipe serializer and Thaumonomicon page snapshots are data/page infrastructure only. They are not an infusion altar, matrix or pedestal behavior implementation.

## Current state

- `thaumcraft:infusion` recipe JSONs are reloadable data resources.
- Infusion recipe pages can render data snapshots in the Thaumonomicon.
- `TCInfusionRecipeMatcher` validates catalyst, exact-count unordered pedestal components, and aspect costs without mutation.
- `TCInfusionAssembly` and `TCInfusionValidationResult` provide the first server-owned validation snapshot/result boundary.
- `TCInfusionCraftingPlan` and `TCInfusionStartResult` provide the first saved server-owned crafting-start state after validation.
- `TCInfusionCompletionPlan` provides the first read-only active-plan readiness check against the current center pedestal, originally matched component pedestal positions, and available aspects.
- `tools/audits/audit-infusion-behavior.ps1` runs the current server runtime audit; latest result is `50/50` checks passing after the mutation, container-remainder, Warded Jar source-resolver and five-tick pull slices.
- Custom recipe boundary audit recognizes `thaumcraft:infusion` as `INFUSION_PAGE_READY_NO_GAMEPLAY`.
- `TCInfusionMatrixBlockEntity` scans the legacy matrix-centered pedestal range and feeds a `TCInfusionAssembly` snapshot.
- `TCInfusionPedestalBlockEntity` owns the first legacy-shaped one-slot pedestal state: empty pedestal accepts one item, occupied pedestal returns/drops its stored item.
- Audit-only full-plan item/aspect mutation now exists, including component remainders and Warded Jar source discovery. Exact one-point legacy cycle timing, instability, client particles/beams and player-facing completion remain deferred.

## First allowed gameplay slice

The first implementation slice may add only a minimal, server-owned infusion validation and state boundary:

1. Done: a focused BlockEntity relationship for matrix and pedestal discovery.
2. Done: server-side recipe lookup against loaded `TCInfusionRecipe` instances.
3. Done: catalyst, component multiset and aspect-cost validation without mutating recipe objects.
4. Done: deterministic validation/audit path for the current recipe JSON structure.
5. Done: minimal happy-path and failed validation scenarios, including runtime world placement.
6. Done: saved active crafting start plan recording recipe id, research, instability, catalyst, matched component stacks, matched pedestal positions, required aspects, result and player name.
7. Done: read-only completion/readiness planning that rechecks the current catalyst, originally matched component pedestal stacks/positions and available aspect totals before any future mutation.
8. Done for audit use: component mutation, component remainders and fail-closed Warded Jar aspect-source resolution over the exact legacy range.
9. Still required: persisted one-point craft-cycle state, source FX, instability and player-facing server activation.

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
- Broad essentia transport, additional jars/mirrors/alembics or golem automation.
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
- Still deferred: exact legacy per-cycle item/essentia timing, instability, particles/beams/sounds and player-facing completion.
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
## Container remainder guard note

- `TCInfusionContainerRemainderPolicy` blocks audit-only mutation execution for plans that contain known bucket, bottle, potion, honey bottle or bowl-style remainder inputs.
- This is a temporary safety guard, not full legacy container-item parity.
- Recipes such as jar brain and some verdant/mask recipes still require a real container-item policy before player-facing infusion completion is enabled.
## Aspect source boundary note

- `TCInfusionAspectSource` is an audit-only in-memory aspect source used to prove all-or-nothing drain semantics.
- It is not jar, tube, alembic, aura or essentia transport gameplay.
- The future player-facing executor must drain from a real source only after a valid `TCInfusionCompletionPlan` and before/with item mutation as one atomic completion policy.
## Aspect-source executor integration note

- `TCInfusionMutationExecutor.executeWithAspectSource(...)` combines the audit-only mutation executor with `TCInfusionAspectSource`.
- It rejects insufficient aspect sources before item mutation, then drains the source and completes the same audit-only item mutation path.
- This is still not player-facing and still does not implement jars, tubes, aura, alembics or essentia transport.
## Aspect source interface note

- `TCInfusionAspectSource` is now an interface with the current audit-only in-memory implementation behind `TCInfusionAspectSource.memory(...)`.
- This keeps the executor/source contract stable for future jar, tube, alembic or aura-backed sources without making those systems part of the current slice.
## Player-facing completion gate note

- `TCInfusionMatrixBlock.isPlayerFacingCompletionEnabled()` is explicitly false.
- Caster interaction on the matrix remains validation/status-only and reports `completion=disabled` in the action-bar status.
- The audit-only executor/source path must not be wired into normal player activation until container-item policy and real essentia source policy are implemented.
## Component remainder policy note

- Component pedestal inputs with known remainders now preserve the remainder on the same pedestal after audit-only mutation.
- Center catalyst inputs with known remainders remain blocked because the center pedestal is currently used for the result output.
- This unblocks current component-side bucket/bottle/bowl policy in the audit-only executor without enabling player-facing infusion completion.
## Refreshed container remainder audit note

- `infusion_container_item_remainder_audit.md` now separates handled component-side remainders from still-blocked catalyst-side remainders.
- Current data has component-side remainder inputs covered by the audit-only policy; catalyst-side container remainders remain blocked if they appear later.
## Tag input expansion audit note

- `infusion_tag_input_expansion_audit.md` expands locally available infusion tag inputs and flags missing/external tags.
- This audit keeps tag-based recipe ingredients from bypassing the container/remainder policy when player-facing completion is eventually enabled.
## Built-in tag fallback audit note

- `audit-infusion-tag-input-expansion.ps1` now treats `minecraft:wool` as a known built-in fallback tag for static audit purposes.
- Current local plus built-in fallback tag expansion does not introduce known bucket/bottle/bowl-style remainder inputs.
## Real aspect source resolver boundary note

- `TCInfusionAspectSourceResolver` discovers reviewed `TCAspectSourceContainer` BlockEntities and fails closed for unknown source types.
- The first supported source is `thaumcraft:jar_normal`; direct transport buffers are excluded to preserve legacy `IAspectSource` semantics.
- Player-facing completion must not bypass this resolver with direct in-memory sources.
## Real source policy design checkpoint

- See `06_docs/gameplay/infusion_real_source_policy_design.md` before implementing any real jar, tube, alembic, aura or network-backed source.
- The resolver must fail closed and must not use audit-only memory sources for player-facing completion.

## Warded Jar source checkpoint

- `thaumcraft:jar_normal` now owns the first real storage-bearing source BlockEntity with legacy capacity `250`, blocked/filter persistence, top-face transport access and comparator output.
- `TCInfusionAspectSourceResolver` now scans the legacy `25 x 24 x 25` source volume and sorts `TCAspectSourceContainer` candidates nearest-first.
- Transient tubes are intentionally excluded because legacy infusion discovered `IAspectSource`, not arbitrary `IEssentiaTransport` buffers.
- Runtime behavior audit includes the Warded Jar five-tick pull cadence; player-facing completion remains disabled until one-point matrix cycle semantics are implemented.
