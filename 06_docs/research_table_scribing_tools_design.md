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
| `thaumcraft.common.container.ContainerResearchTable` | Slot layout uses scribing tools at `(16,15)`, paper at `(224,16)`, player inventory at the legacy offsets, and full card draw/selection/finish flow consumes paper and scribing-tool durability through the table inventory. Card behavior remains deferred. |
| `data/thaumcraft/research/basics.json` | `THEORYRESEARCH` stage 1 requires crafted `thaumcraft:scribing_tools` and `thaumcraft:research_table`; recipe list references legacy `tablewood` and `inkwell`. |

## Implemented Now

| Piece | NeoForge target | Legacy parity note |
|---|---|---|
| Scribing marker | `thaumcraft.api.items.IScribeTools` | Marker-only API preserved. |
| Scribing tools item | `ItemScribingTools extends Item implements IScribeTools` | Stack size `1` and durability `100` preserved. Ink consumption waits for full table menu/theorycraft. |
| Basic tables | `table_wood`, `table_stone` registered blocks/items | Legacy ids and crafting recipes restored. Wood table is the only table that converts into a research table, matching legacy. |
| Research table block entity | `TCResearchTableBlockEntity` with two slots | Slot meanings match legacy: scribing tools in slot `0`, paper in slot `1`. No card/theory data is stored yet. |
| Research table menu | `TCMenus.RESEARCH_TABLE` and `TCResearchTableMenu` | Modern `AbstractContainerMenu` with the two legacy slots and legacy player inventory offsets. Shift-click routing accepts only `IScribeTools` or paper into the table slots. |
| Minimal research table screen | `TCResearchTableScreen` registered through `RegisterMenuScreensEvent` | Uses legacy `textures/gui/gui_research_table.png` as the background but intentionally omits card rendering until the server theory model exists. |
| Table consumables | `consumeInkFromTable`, `consumePaperFromTable` | Matches the legacy server-side role: card activation consumes one durability from usable scribing tools, and card draw consumes one paper. The methods are ready but not called until theorycraft actions exist. |
| Wood-table conversion | `TCTableBlock#useItemOn` | Held scribing tools are transferred into the research table block entity, removed from the hand, and the modern required-craft marker path is notified for `thaumcraft:research_table`. |
| Active assets | Modern blockstates/models/item models using `textures/block` and `textures/item` | Legacy imported `textures/blocks` and `textures/items` remain reference/base assets. |
| Aspect parity for new active ids | Exact runtime dump values for `table_wood`, `table_stone`, `research_table` | These are final 1.12 `getObjectAspects` values from the legacy dump, not recalculated guesses. |

## Deferred

| Deferred piece | Reason |
|---|---|
| Full theorycraft menu actions | The inventory menu exists; start/draw/select/complete/scrap actions need server-owned theory data and validated payloads. |
| Theorycraft cards and `ResearchTableData` | Large gameplay system; must be ported from legacy card behavior and synced explicitly. |
| Paper draw/card selection/finish actions | Requires the theorycraft data model and menu action payloads first. |
| Scribing-tool ink durability integration | The table has the legacy consumable method; it must only be called from validated card activation. |
| Research aids scan around the table | Depends on theorycraft card registry and modern block/entity aid matching policy. |
| Full `THEORYRESEARCH` reward/unlock UX | Requires Thaumonomicon UI and broader stage/flag sync. |

## Next Checklist

1. Port `ResearchTableData` serialization shape as a modern server-owned model.
2. Port theorycraft card registry and one deterministic card fixture before adding random draws.
3. Add client-to-server payloads for start theory, draw cards, select card, complete/abandon theory, with server-side validation only.
4. Add parity fixtures for paper consumption, scribing-tool damage, inspiration calculation and category totals.
5. Add a block entity renderer for table-top scribing tools/paper after the storage/menu sync is visually checked.
