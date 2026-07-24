# Thaumonomicon UI Design

## Scope

This document defines the first real Thaumonomicon item, open flow, research browser, and research entry screen for the NeoForge 1.21.1 port.

The UI is a client-side `Screen`, not an inventory `Menu`. All visibility, unlockability, progression, requirements, flags, selected stage, and bookmark availability remain server-owned and are transferred through the existing authoritative Thaumonomicon view/action protocol.

## Legacy references

| Area | Legacy class | Relevant behavior |
|---|---|---|
| Item use | `thaumcraft.common.items.curios.ItemThaumonomicon` | Stack size one, uncommon rarity, syncs knowledge and opens GUI id `12`; client plays the page sound. |
| GUI routing | `thaumcraft.proxies.ProxyGUI` | GUI id `12` creates `GuiResearchBrowser` without a server inventory container. |
| Browser | `thaumcraft.client.gui.GuiResearchBrowser` | Category backgrounds, research graph, parent/sibling links, category tabs, pan/zoom, research flags and server packets for start/acknowledge. |
| Entry | `thaumcraft.client.gui.GuiResearchPage` | Book background, selected stage text, requirements, bookmarks, page navigation and checked stage-advance packet. |
| Side inserts | `thaumcraft.client.gui.GuiResearchPage` | Aspect side tab after `FIRSTSTEPS`, knowledge side tab after `KNOWLEDGETYPES`, known-aspect component visibility and raw knowledge totals. |
| Warp warning | `thaumcraft.client.gui.GuiResearchPage` | Stage warp marker is shown on incomplete warping stages and uses the legacy forbidden-level translation keys. |
| Progress packets | `PacketSyncProgressToServer`, `PacketSyncResearchFlagsToServer` | Server mutation; browser start, known-entry acknowledgement/final-stage progression and checked stage advance have distinct semantics. |

## Modern architecture

| Concern | Modern owner |
|---|---|
| Item identity and right-click | `ItemThaumonomicon`, `TCItems.THAUMONOMICON` |
| Explicit open response | `TCThaumonomiconIndexPayload.openScreen`, server-built index revision, and `TCThaumonomiconNetwork.openFor` |
| Client open orchestration | `TCThaumonomiconClientCache`, `TCThaumonomiconClientController` |
| Browser | `TCThaumonomiconBrowserScreen` |
| Entry page | `TCThaumonomiconEntryScreen` |
| Side insert data | `TCKnowledgeClientCache` completed research keys plus raw observation/theory maps |
| Vanilla crafting page snapshot | `TCCraftingRecipePageView`, built by `TCResearchPageCatalogManager` |
| Arcane/crucible/infusion/blueprint/fake-display page snapshots | `TCArcaneRecipePageView`, `TCCrucibleRecipePageView`, `TCInfusionRecipePageView`, `TCBlueprintRecipePageView`, and `TCDisplayRecipePageView`, all server-built by `TCResearchPageCatalogManager` |
| Authoritative state | `TCThaumonomiconService`, `TCResearchManager`, `TCPlayerKnowledgeStore` |
| Server actions | `TCThaumonomiconActionPayload` with client revision echo |

The server sends `openScreen=true` only for an actual Thaumonomicon item use. Ordinary index refreshes after research mutation keep `openScreen=false`, so sync traffic cannot unexpectedly reopen the book.
Every index also carries a server-built revision over the current research data, page-catalog data and player knowledge state. Client entry/action payloads only echo the last revision they received; stale requests are answered with authoritative state and do not mutate research progression.

## Implemented legacy behavior

- Thaumonomicon is a stack-size-one uncommon item and appears at the start of the legacy item sequence in the creative tab.
- Right-click sends an authoritative visible research index, syncs player knowledge, plays the page sound, and opens the browser on the client.
- Browser categories preserve the server/legacy category order.
- Browser graph uses legacy category backgrounds, overlay, research frame sprites, icon cycling, flags, parent links, pan, and zoom. The legacy `.jpg` category backgrounds remain imported as source/reference assets, while the 1.21 runtime view sends `.png` background paths to avoid missing-texture rendering in the modern client. The browser background render path now uses a dedicated legacy-style tiled UV helper for the 256-unit repeating research-map coordinates instead of a normal single GUI blit, normalizes bare legacy texture paths into the `thaumcraft` namespace, clips graph contents inside the book frame and keeps the first TC category tab at the lower legacy offset.
- Browser search follows the legacy activation shape: the search icon is at the lower-left edge, the text field appears at `(20,20)`, results use 10px rows and legacy colors, and search mode suppresses the category map/background until a category or research hit is selected. Search-result clicks reuse the same server action/revision payload path as graph clicks.
- Browser search also receives server-owned recipe-output aliases for known/active research entries. A recipe search result opens the same authoritative entry view and then selects the exact bookmark/page that produced the result.
- Locked research hover now follows the legacy tooltip structure: title, missing-research heading, unresolved parent names, and `RESEARCH`/`PAGE` flag messages such as `tc.research.newresearch`.
- Unknown unlockable entries send `START_RESEARCH`; known entries send `ACKNOWLEDGE_ENTRY`.
- Thaumonomicon item open runs the legacy known-entry sibling completion pass before sending the index, matching the original `ItemThaumonomicon` behavior that completes available sibling entries such as `KNOWLEDGETYPES` after `FIRSTSTEPS`.
- Thaumonomicon item pickup records `!gotthaumonomicon`, matching legacy `PlayerEvents.pickupItem`. Creative/right-click-only book use does not grant this marker; in that state `FIRSTSTEPS` is visible but locked with missing required research, while legacy browser parent lookup still exposes discovery-chain entries such as `Discovering Artifice` and `Discovering Golemancy`.
- The entry screen opens only after the server accepts the action and returns an authoritative entry view.
- Entry stage text, visible addenda, requirement results, stage state, bookmarks, book asset, page navigation, and checked stage advance are active.
- Renderable recipe bookmarks are legacy-style right-side tabs with output stack icons. They open a legacy-style paper recipe page and can be switched while a recipe page is already open. The server snapshot owns the real result stack, recipe kind, shaped dimensions, ingredient slots, component/aspect displays and display-only recipe contents; the client only cycles and renders those values.
- Recipe papers, the aspects insert and the knowledge insert are mutually exclusive exactly like the legacy screen. Opening one closes the previous insert, clicking the selected recipe bookmark closes it, and stage requirements/results are never rendered through an open paper.
- Crafting, arcane, crucible, infusion and fake/display recipe pages use the legacy `GuiResearchPage` slot/overlay geometry through modern `GuiGraphics` calls. Crucible and infusion aspect costs are rendered as aspect icons plus amounts from the stack `ASPECT_STACK` component instead of clickable crystal recipe stacks, matching the legacy popup/cost intent while keeping the current server snapshot protocol unchanged.
- Entry and recipe page navigation no longer uses invented persistent text buttons. Main page arrows, recipe-page arrows, aspect-page arrows, the hidden/hover return zone, Escape/Inventory close behavior, Backspace recipe-history behavior and return hover text follow the legacy `GuiResearchPage` navigation model. The `recipe.return`/`Back` text is only the legacy hover label for recipe-history return, not a persistent modern button.
- Incomplete-stage requirement rows reserve first-page text space using the legacy `heightRemaining` rule, then render item/craft hovers through normal stack tooltips instead of raw requirement ids. Requirement stack clicks reuse the same server-authoritative recipe drilldown path as recipe-page stack clicks.
- Live vanilla crafting, arcane, crucible, infusion, blueprint construct and fake/display catalog entries produce valid server snapshots. Direct-reference live availability remains catalog-owned; deferred or legacy-missing groups stay non-interactive until their subsystem or mapping exists.
- Recipe stack click-through is server-authoritative: the client sends the hovered stack and current index revision, the server resolves the first visible matching research recipe page, stale revisions are rejected without mutation, and the client keeps only local page history for Back/Escape/right-click navigation.
- Entry-side aspect and knowledge inserts are active. The aspects tab appears only after completed `FIRSTSTEPS`; the knowledge tab appears only after completed `KNOWLEDGETYPES` and is hidden on that entry itself. Aspect pages show only known aspects from completed `!aspect` keys, sort by translated name, hide unknown components with the legacy unknown texture, use the legacy aspect side tab and expose legacy page arrows when required. Knowledge pages read versioned, server-synced raw observation/theory totals from `TCKnowledgeClientCache`.
- Empty knowledge totals deliberately render as a blank legacy leaf. Theory/observation icons and totals only appear after synced raw knowledge exists, in legacy `EnumKnowledgeType` order. The in-page `KNOWLEDGETYPES` totals reserve first-page text height using the legacy `heightRemaining -= 2 + 20 * rows + divider` rule so text cannot overlap the icon row.
- Knowledge icons preserve the legacy base/icon-overlay alpha, scale, offsets, z-order and one-line hover text. Both `THEORY` and `OBSERVATION` are category-valued, so all seven non-empty category totals are transferred instead of collapsing to `BASICS`.
- The `KNOWLEDGETYPES` entry also has the legacy in-page knowledge totals boundary when the research is complete.
- Browser node/category `RESEARCH` and `PAGE` markers preserve the legacy draw order, scale and offsets. Category hover includes its server-calculated completion percentage, excluding `AUTOUNLOCK` entries as legacy does.
- Research and recipe icons are resolved from the real legacy output identity, metadata and component variant by `TCResearchIconResolver`; item hovers use the normal `ItemStack` tooltip path so rarity/name colors match inventory tooltips instead of a synthetic recipe label.
- Primordial pearl item model predicates use a namespaced `thaumcraft:type` property derived from the legacy metadata component ranges so the icon variant can match the original item state.
- Incomplete warping stages render a legacy-shaped forbidden marker and `tc.forbidden.level.*`/`tc.warp.warn` text from the selected server stage.
- Thaumonomicon has its exact legacy runtime aspect result from the 1.12 exporter dump.
- Legacy `research.*` English translations required by the active screen are present in modern `en_us.json`.

## Intentionally deferred

- Exact screenshot-measured arrow/popup animation timing and final browser/paper-page pixel-level tuning.
- Exact measured recipe-output search parity against a live 1.12 client index. The current implementation no longer guesses client-side and carries server-owned aliases for current ready recipe snapshots, but a runtime legacy search-export comparison is still required before claiming pixel-perfect search behavior.
- Cheat Thaumonomicon variant.

Deferred recipe pages are shown only as catalog bookmarks with their authoritative kind/availability. Only `READY` pages carrying a matching server snapshot are interactive; the UI does not invent recipe contents.

## Validation

- `TCThaumonomiconProtocolAudit` validates visibility, server-owned state, revision freshness, stale-action and stale-drilldown rejection without mutation, exact start/advance/acknowledge semantics, final-stage progression, cache invalidation, explicit-open-versus-refresh separation, open-time known-sibling completion, selected-stage warp propagation, side-panel knowledge cache exposure, server-owned recipe-output search aliases, server-side drilldown output matching, and the `READY` crafting/arcane/crucible/infusion/blueprint/fake-display snapshot boundary. The report deliberately records stable revision match labels instead of raw hash values.
- Latest protocol result: `55/55` checks passed. In addition to the recipe/page contracts, the audit now proves full seven-category knowledge wire round-trip, exact legacy knowledge field/progression semantics, research-all category completion, resolution of all `165` active research icon contracts, readable dimensions for all `51` texture icons, and the exact set/frame layout of `11` animated legacy seal spritesheets. Animated texture layout cache invalidation is registered on client resource reload.
- `gradlew build --no-daemon` passes after the tab/overlay, tooltip, marker, knowledge-sync and icon-resolution corrections.
- Dedicated-server reload passes with `702` exact aspect assignments, `46` tag assignments, `32` complex exact assignments, `636` generated assignments, `1230/1230` vanilla item aspect coverage and the Thaumonomicon protocol audit.

## Next boundary

1. Keep `DEFERRED` and `LEGACY_MISSING` pages non-interactive until their crafting subsystem or mapping is implemented.
2. Keep recipe drilldown limited to server-returned snapshots; do not let the client resolve hidden or deferred recipes independently.
3. Run manual `runClient` screenshot comparison before claiming pixel-perfect browser/entry parity; functional/server-authoritative parity for the implemented page kinds is audited, but final UI parity is not complete until measured screenshots and legacy search-export comparison are closed.
