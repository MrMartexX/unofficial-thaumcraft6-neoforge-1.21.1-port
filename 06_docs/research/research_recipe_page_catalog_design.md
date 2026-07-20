# Research Recipe/Page Catalog Design

## Scope

This design closes the data boundary between loaded research stages and the
Thaumonomicon page renderer. It does not implement in-world arcane workbench,
crucible, infusion altar, multiblock assembly, or the final Thaumonomicon visual
polish.

## Authoritative legacy behavior

Forge 1.12.2 `GuiResearchPage.addRecipesToList` and `findRecipePage` resolve a
research recipe/page reference in this exact order:

1. `CommonInternals.getCatalogRecipe`;
2. `CommonInternals.getCatalogRecipeFake`;
3. `CraftingManager.getRecipe`;
4. `ConfigRecipes.recipeGroups`.

Resolved values may be:

- a multiblock blueprint;
- a crucible recipe;
- an infusion recipe;
- an arcane recipe;
- a normal crafting recipe;
- a fake/display-only recipe;
- a group containing multiple recipe references.

The reference is therefore a page-catalog identifier. It is not necessarily a
craftable Minecraft recipe id and must not be passed directly to vanilla recipe
unlock APIs.

## NeoForge target

The permanent catalog is a reload-safe server-owned service keyed by the
original research recipe/page `ResourceLocation`.

Each catalog entry preserves:

- original reference id;
- page kind;
- resolution state;
- target recipe/page ids;
- research gate;
- output/display identity where available;
- explicit deferred reason when the dependent recipe subsystem is not ported.

Research JSON remains the authority for where references appear. The catalog is
the authority for what each reference means and whether it can be shown to a
specific player.

## Server-authoritative boundary

The Thaumonomicon client requests an index or entry page from the server. The
server validates player research state and returns only visible entries, the
current stage, visible addenda, current requirement state, and catalog
bookmarks. Client code may render the returned view model but must not decide
unlock state or resolve raw recipe ids itself.

The current mutating action set is validated on the server and preserves the
legacy browser semantics:

- `START_RESEARCH`: `first=true`, `checks=false`, `noFlags=true`;
- `ADVANCE_CURRENT_STAGE`: `first=false`, `checks=true`, `noFlags=true`;
- `ACKNOWLEDGE_ENTRY`: clears `RESEARCH` and `PAGE`, then attempts the legacy
  known-entry final-stage checked progression with `noFlags=false`.

A successful mutation sends the refreshed index before the refreshed entry
view. Every index refresh invalidates detailed client entry views so stale
stages, flags, unlockability, or bookmarks cannot survive a progression
change.

## Reload and dependency rules

- Research data and the catalog are rebuilt on server data reload.
- Recipe-backed entries are rebound after recipe data is available.
- Missing custom subsystems remain explicit `DEFERRED`, not fake vanilla
  recipes.
- Catalog groups preserve ordered member ids and must not recursively loop.
- Resolver precedence remains identical to legacy when multiple sources provide
  the same id.

## Validation gates

1. Export exact legacy runtime classifications.
2. Classify every unique built-in research recipe/page reference.
3. Reject duplicate catalog ids and cyclic groups.
4. Report missing/deferred references separately.
5. Keep the server-authoritative catalog query independent from rendering.

Latest validation:

- legacy/runtime catalog parity: `253` occurrences, `203` direct references,
  `325` total entries including group members, `0` field differences;
- runtime structural validation: `0` missing catalog references, unresolved
  group targets, or cycles;
- Thaumonomicon protocol audit: `41/41` checks passed. The first real item/open/browser/entry/search/drilldown flow, server-snapshot-backed vanilla crafting page renderer, exact arcane page renderer, crucible page renderer, infusion page renderer, blueprint construct page renderer, fake/display page renderer, legacy-style recipe tabs, side-panel knowledge/aspect state, selected-stage warp propagation, runtime PNG category background paths, server-built index revision, stale-action/stale-drilldown rejection without mutation, and server-side recipe output-stack drilldown resolution are active. Catalog live availability is `203 READY`, `0 DEFERRED`, `0 LEGACY_MISSING`; measured pixel-level polish remains row-17 work.

## Remaining boundary

- Preserve the implemented Thaumonomicon item/open/browser/entry/crafting/arcane-page flow.
- Keep vanilla crafting page contents server-resolved through
  `TCCraftingRecipePageView`; do not resolve recipes independently on the client.
- Keep arcane page contents server-resolved through `TCArcaneRecipePageView`.
- Keep crucible page contents server-resolved through `TCCrucibleRecipePageView`; do not treat this as in-world crucible behavior.
- Keep infusion page contents server-resolved through `TCInfusionRecipePageView`; do not treat this as in-world infusion altar behavior.
- Keep blueprint construct page contents server-resolved through `TCBlueprintRecipePageView`; do not let the client infer layers, source blocks, target replacements or required research.
- Do not let the client infer vis, crystals, research, instability, aspects, result, ingredient variants or blueprint cell transforms.
- Legacy fake/display-only pages are implemented as server-authored display snapshots and must stay non-craftable.
- Defer only final visual pixel parity and future owner-specific special page layouts; do not reclassify them as gameplay recipes without legacy ownership evidence.
