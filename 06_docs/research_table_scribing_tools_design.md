# Research Table and Scribing Tools Slice

This note defines the first NeoForge 1.21.1 implementation slice for the Thaumcraft 6 research table path.

Guide rule applied: research, BlockEntity, menu and player-progression systems are high risk. Preserve the legacy role and exact externally visible ids first, then add modern storage/menu/networking in small buildable slices.

## Legacy References

| Legacy class/data | Behavior used for this slice |
|---|---|
| `thaumcraft.api.items.IScribeTools` | Public marker interface for items accepted by the research table scribing-tools slot. |
| `thaumcraft.common.items.tools.ItemScribingTools` | Stack size `1`, max damage `100`, implements `IScribeTools`. |
| `thaumcraft.common.blocks.basic.BlockTable` | `table_wood` plus held `IScribeTools` converts into `research_table`, transfers the held scribing tools into slot `0`, and fires a crafting event for `research_table`. |
| `thaumcraft.common.blocks.crafting.BlockResearchTable` | Non-full-cube, wood-sound table block, horizontal facing on placement, opens GUI id `10`. |
| `thaumcraft.common.tiles.crafting.TileResearchTable` | Two slots: slot `0` accepts `IScribeTools`, slot `1` accepts undamaged paper; theory data and card state are stored on the tile. |
| `thaumcraft.common.container.ContainerResearchTable` | Slot layout uses scribing tools at `(16,15)`, paper at `(224,16)`, player inventory at the legacy offsets, and full card draw/selection/finish flow consumes paper and scribing-tool durability through the table inventory. |
| `thaumcraft.api.research.theorycraft.ResearchTableData` | Stores theory owner, inspiration, bonus draws, saved cards, aid cards, category totals, blocked categories, current card choices, and last draw using the legacy NBT keys. |
| `thaumcraft.api.research.theorycraft.TheorycraftCard` / core cards | Card seed, card serialization, inspiration cost, category gating, activation, and selected-card flow drive the table theory state. |
| `thaumcraft.api.research.theorycraft.ITheorycraftAid` and `AidBookshelf` | A 9x9x3 block area around the table plus entity range `5` is checked for aid objects. Selected aids reduce starting inspiration by one each and append their card ids to `aidCards`; `AidBookshelf` contributes `CardBalance`, two `CardNotation`, and three `CardStudy` entries. |
| `thaumcraft.common.tiles.crafting.TileResearchTable#checkSurroundingAids` and `PacketStartTheoryToServer` | Legacy detects available aids around the table, the GUI lets the player select a subset, and the selected legacy aid keys are sent when creating the theory. |
| `data/thaumcraft/research/basics.json` | `THEORYRESEARCH` stage 1 requires crafted `thaumcraft:scribing_tools` and `thaumcraft:research_table`; recipe list references legacy `tablewood` and `inkwell`. |

## Implemented Now

| Piece | NeoForge target | Legacy parity note |
|---|---|---|
| Scribing marker | `thaumcraft.api.items.IScribeTools` | Marker-only API preserved. |
| Scribing tools item | `ItemScribingTools extends Item implements IScribeTools` | Stack size `1` and durability `100` preserved. Theory card activation consumes one durability through the table. |
| Basic tables | `table_wood`, `table_stone` registered blocks/items | Legacy ids and crafting recipes restored. Wood table is the only table that converts into a research table, matching legacy. |
| Research table block entity | `TCResearchTableBlockEntity` with two slots plus `TCResearchTableData` | Slot meanings match legacy: scribing tools in slot `0`, paper in slot `1`. Theory data is saved under the legacy `note` tag and synced separately to the open screen. |
| Research table menu | `TCMenus.RESEARCH_TABLE` and `TCResearchTableMenu` | Modern `AbstractContainerMenu` with the two legacy slots and legacy player inventory offsets. Shift-click routing accepts only `IScribeTools` or paper into the table slots. |
| Minimal research table screen | `TCResearchTableScreen` registered through `RegisterMenuScreensEvent` | Uses legacy `textures/gui/gui_research_table.png` as the background and exposes minimal functional buttons for create, draw, select, complete and scrap. Legacy paper/card animations remain deferred. |
| Table consumables | `consumeInkFromTable`, `consumePaperFromTable` | Matches the legacy server-side role: card activation consumes one durability from usable scribing tools, and card draw consumes one paper. |
| Theory data model | `TCResearchTableData` | Preserves legacy field names and NBT keys: `player`, `inspiration`, `inspirationStart`, `placedCards`, `bonusDraws`, `aidsChosen`, `penaltyStart`, `savedCards`, `categoriesBlocked`, `categoryTotals`, `aidCards`, `cardChoices`, `lastDraw`. |
| Theory card registry | `TCTheorycraftManager` with first core-card slice plus bridge cards | Registers the public API/core legacy card ids for `CardStudy`, `CardAnalyze`, `CardBalance`, `CardNotation`, `CardPonder`, `CardRethink`, `CardReject`, `CardExperimentation`, and `CardInspired`. The first common-card bridge adds dependency-free theory-total cards (`CardMeasure`, `CardCalibrate`, `CardFocus`, `CardSynergy`) and aspect-crystal Alchemy cards (`CardConcentrate`, `CardReactions`, `CardSynthesis`). |
| Research aids | `TCTheorycraftAid`, `TCTheorycraftManager#collectNearbyAidKeys`, `AidBookshelf` bridge, and minimal screen aid selection | The first safe aid is active: bookshelf detection uses the legacy 9x9x3 block scan and selected aid keys are sent in the start-theory payload. Server revalidates selected keys against nearby aids and keeps the legacy `selectedAids.size()+1 < inspirationStart` limit before adding aid cards. |
| Research table payloads | `TCResearchTableActionPayload`, `TCResearchTableSyncPayload`, `TCResearchTableNetwork` | Client sends table action intent plus selected aid keys only for theory creation. Server validates the currently open `TCResearchTableMenu`, table validity, nearby aids, paper, scribing tools, card index and current theory state before mutation. |
| Card required items | `TCResearchTableActions` inventory pre-check/consume path | Mirrors legacy `ContainerResearchTable`: card required stacks are checked in the player main inventory; only entries marked by `getRequiredItemsConsumed()` are consumed before activation. The current modern stack match is exact item plus DataComponents, which is required for aspect-crystal cards. |
| Diagnostic parity harness | `TCResearchTableDiagnostics`, `/tc research_table validate`, `-PtcResearchTableAudit=true` | Static checks cover legacy NBT keys, round-trip card choices, `addTotal`, `addInspiration`, finish-theory raw award math, card registry slices, dependency-free bridge cards, aspect-crystal Alchemy cards, bookshelf aid card injection, and sync payload round-trip. Player checks cover real scribing-tool damage, paper consumption, card required item checks/consumption, draw-card availability and finish-theory knowledge mutation without leaving player knowledge changed. |
| Wood-table conversion | `TCTableBlock#useItemOn` | Held scribing tools are transferred into the research table block entity, removed from the hand, and the modern required-craft marker path is notified for `thaumcraft:research_table`. |
| Active assets | Modern blockstates/models/item models using `textures/block` and `textures/item` | Legacy imported `textures/blocks` and `textures/items` remain reference/base assets. |
| Aspect parity for new active ids | Exact runtime dump values for `table_wood`, `table_stone`, `research_table` | These are final 1.12 `getObjectAspects` values from the legacy dump, not recalculated guesses. |

## Deferred

| Deferred piece | Reason |
|---|---|
| Full legacy theorycraft card set | The public/API slice, dependency-free bridge cards, and first item-consuming aspect-crystal Alchemy cards exist. Cards tied to curio, warp, celestial notes, portals, enchantment table, beacon, phials, focus/caster runtime state, infusion/golem gameplay and other unported subsystems must wait for their target systems or explicit bridge policy. |
| Polished legacy card rendering | The current screen is functional. Legacy paper draw animation, gilded aid sheets, category icon column, sparkle feedback, card hover/zoom animation and table-top paper/scribing-tool visuals are still client-rendering work. |
| Remaining research aids | Bookshelf is active. Brain-in-a-jar, glyphed stone, portals, basic category aids, enchantment table, beacon and any entity/item aids need their target blocks/entities/cards audited and ported by dependency family. |
| Full `THEORYRESEARCH` reward/unlock UX | Requires Thaumonomicon UI and broader stage/flag sync. |

## Next Checklist

1. Run and keep `-PtcResearchTableAudit=true` passing after every research-table change.
2. Visually check the minimal aid selection row in `runClient` with bookshelves around the table.
3. Port the next safe advanced cards by dependency family, starting with cards that do not require unported subsystems.
4. Add a block entity renderer for table-top scribing tools/paper after the storage/menu sync is visually checked.
5. Replace the minimal card buttons with the legacy paper/card animation after the full card data payload is stable.
