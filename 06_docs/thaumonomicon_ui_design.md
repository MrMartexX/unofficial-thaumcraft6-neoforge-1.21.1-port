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
| Progress packets | `PacketSyncProgressToServer`, `PacketSyncResearchFlagsToServer` | Server mutation; browser start, known-entry acknowledgement/final-stage progression and checked stage advance have distinct semantics. |

## Modern architecture

| Concern | Modern owner |
|---|---|
| Item identity and right-click | `ItemThaumonomicon`, `TCItems.THAUMONOMICON` |
| Explicit open response | `TCThaumonomiconIndexPayload.openScreen`, server-built index revision, and `TCThaumonomiconNetwork.openFor` |
| Client open orchestration | `TCThaumonomiconClientCache`, `TCThaumonomiconClientController` |
| Browser | `TCThaumonomiconBrowserScreen` |
| Entry page | `TCThaumonomiconEntryScreen` |
| Vanilla crafting page snapshot | `TCCraftingRecipePageView`, built by `TCResearchPageCatalogManager` |
| Authoritative state | `TCThaumonomiconService`, `TCResearchManager`, `TCPlayerKnowledgeStore` |
| Server actions | `TCThaumonomiconActionPayload` with client revision echo |

The server sends `openScreen=true` only for an actual Thaumonomicon item use. Ordinary index refreshes after research mutation keep `openScreen=false`, so sync traffic cannot unexpectedly reopen the book.
Every index also carries a server-built revision over the current research data, page-catalog data and player knowledge state. Client entry/action payloads only echo the last revision they received; stale requests are answered with authoritative state and do not mutate research progression.

## Implemented legacy behavior

- Thaumonomicon is a stack-size-one uncommon item and appears at the start of the legacy item sequence in the creative tab.
- Right-click sends an authoritative visible research index, syncs player knowledge, plays the page sound, and opens the browser on the client.
- Browser categories preserve the server/legacy category order.
- Browser graph uses legacy category backgrounds, overlay, research frame sprites, icon cycling, flags, parent links, pan, and zoom.
- Unknown unlockable entries send `START_RESEARCH`; known entries send `ACKNOWLEDGE_ENTRY`.
- The entry screen opens only after the server accepts the action and returns an authoritative entry view.
- Entry stage text, visible addenda, requirement results, stage state, bookmarks, book asset, page navigation, and checked stage advance are active.
- Renderable crafting bookmarks open a legacy-style paper recipe page. The server snapshot owns the real result stack, shaped/shapeless kind, shaped dimensions, ingredient slots, and ingredient variants; the client only cycles and renders those values.
- Five catalog crafting entries and both current arcane catalog entries produce valid server snapshots. Direct-reference live availability remains catalog-owned; deferred or legacy-missing groups stay non-interactive until their subsystem or mapping exists.
- Thaumonomicon has its exact legacy runtime aspect result from the 1.12 exporter dump.
- Legacy `research.*` English translations required by the active screen are present in modern `en_us.json`.

## Intentionally deferred

- Search mode and search-result drilldown.
- Exact arrow shapes, forbidden/warp marker, category completion percentages, popup animation, and final browser visual parity tuning.
- Aspect and knowledge side pages.
- Recipe drilldown history.
- Arcane, crucible, infusion, blueprint, fake, and missing recipe-page renderers.
- Cheat Thaumonomicon variant.

Deferred recipe pages are shown only as catalog bookmarks with their authoritative kind/availability. Only `READY` crafting pages carrying a server snapshot are interactive; the UI does not invent recipe contents.

## Validation

- `TCThaumonomiconProtocolAudit` validates visibility, server-owned state, revision freshness, stale-action rejection without mutation, exact start/advance/acknowledge semantics, final-stage progression, cache invalidation, explicit-open-versus-refresh separation, and the `READY` crafting/arcane snapshot boundary.
- Latest protocol result: `25/25` checks passed; all five live vanilla crafting catalog entries and both live arcane catalog entries produce valid server snapshots.
- `gradlew build` passes.
- Dedicated-server reload passes with `683` exact aspect assignments and the Thaumonomicon protocol audit.

## Next boundary

1. Keep `DEFERRED` and `LEGACY_MISSING` pages non-interactive until their crafting subsystem or mapping is implemented.
2. Design and implement the first custom recipe type before adding its page renderer; arcane crafting is the next lowest-level dependency.
3. Add recipe drilldown/history only after server-authoritative page snapshots can represent the referenced recipe family.
4. Run a focused visual parity pass against the legacy browser, entry page, and crafting paper page before calling the UI final.
