# Thaumcraft 6 Creative Tab Order Reference

Target project: `05_neoforge_port`

Legacy source reference: `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master`

Visual reference: `07_Test_Instance_and_Comparisons/02_Thaumcraft 1.12.2 Inventory Screenshots`

Purpose: preserve the Thaumcraft 6 1.12.2 creative tab order and visible item/block presentation while porting to NeoForge 1.21.1.

## Hard Rule

The Thaumcraft creative tab must not be alphabetized, grouped by new registry class, or allowed to drift into arbitrary NeoForge registration order.

The port must preserve the visible 1.12.2 order as closely as possible.

This applies to:

- block items;
- plain items;
- metadata/subtype variants;
- damage/data-component variants that are visible in creative;
- dynamic entries such as golem seals;
- config-gated entries such as the cheat Thaumonomicon;
- icon/model appearance where reasonably possible.

## Current Confidence

This document is a working reference, not the final transcribed visual manifest.

The order below is derived from:

- `ConfigBlocks.initBlocks(...)`;
- `ConfigItems.initItems(...)`;
- `ItemTCBase#getSubItems(...)`;
- known subtype constructors and variant arrays;
- the 10 reference screenshots from the 1.12.2 test instance.

The code-derived order should be treated as the technical skeleton. The screenshots remain the visual truth. Before final release, the final NeoForge tab should be checked page-by-page against screenshots 1-10.

## Why Registry Order Alone Is Not Enough

In Thaumcraft 6 1.12.2, the creative tab order is effectively driven by the legacy item registry and each item's `getSubItems(...)` behavior.

Important details:

- many block items are registered during `ConfigBlocks.initBlocks(...)`;
- normal items are registered later in `ConfigItems.initItems(...)`;
- several legacy items expose multiple metadata variants;
- some variants are dynamic, config-gated, or damage-based;
- screenshots are needed to confirm the exact final visual order.

In NeoForge 1.21.1, we should not depend on registry order to recreate this. The creative tab should use an explicit display order class.

Recommended class:

```java
package thaumcraft.common.registry;

public final class TCCreativeTabOrder {
    private TCCreativeTabOrder() {}

    public static void addEntries(CreativeModeTab.Output output) {
        // Add entries in the same visible order as Thaumcraft 6 1.12.2.
        // Do not alphabetize.
        // Do not group by new implementation class.
        // Gaps are allowed during partial porting, but final release must be page-checked against screenshots.
    }
}
```

## Partial Port Rule

During early gates, not every old entry will exist yet. In that case:

- keep the manifest order in this document;
- add only implemented entries to the NeoForge tab;
- preserve relative order among implemented entries;
- do not move an implemented entry earlier just because previous legacy entries are not ported yet;
- do not add fake release placeholders just to fill gaps.

Optional later development tool: a debug-only placeholder mode can show missing entries as placeholder stacks for visual order testing. It must not ship as normal release behavior.

## Screenshot Reference Map

| Screenshot | Role in this document | Notes |
|---|---|---|
| `Thaumcraft_1.12.2_Inventory_1.png` | Start of Thaumcraft creative tab | First visible block/resource page. Use as page 1 visual baseline. |
| `Thaumcraft_1.12.2_Inventory_2.png` | Continuation of blocks | Includes more blocks and dye-based entries. |
| `Thaumcraft_1.12.2_Inventory_3.png` | Continuation of dye blocks and early devices | Use for candles/banners/nitor and early functional block visual checks. |
| `Thaumcraft_1.12.2_Inventory_4.png` | Functional blocks and machines | Important for workbench, jars, tubes, crafting devices, transport devices. |
| `Thaumcraft_1.12.2_Inventory_5.png` | Transition into items and curios | Important for early item order and first resource items. |
| `Thaumcraft_1.12.2_Inventory_6.png` | Resource items and crystals | Important for ingots, nuggets, clusters, crystal-related items. |
| `Thaumcraft_1.12.2_Inventory_7.png` | Essentia/phial-heavy item page | Important for phials and color/variant visual checks. |
| `Thaumcraft_1.12.2_Inventory_8.png` | Tools, foci, devices, early gear | Important for thaumometer, goggles, tools, focus items. |
| `Thaumcraft_1.12.2_Inventory_9.png` | Armor, baubles, devices, golem/seal area | Important for armor sets and bauble order. |
| `Thaumcraft_1.12.2_Inventory_10.png` | End of Thaumcraft creative tab | Important for late baubles, cult gear, seals, final entries. |

## Implementation Contract for NeoForge

Use one creative tab registration plus one explicit output order.

Recommended responsibilities:

| Class | Responsibility |
|---|---|
| `TCCreativeTabs` | Registers the Thaumcraft creative tab. |
| `TCCreativeTabOrder` | Adds visible entries in legacy visual order. |
| `TCItems` | Registers item objects only. Should not decide creative order. |
| `TCBlocks` | Registers block objects and block items only. Should not decide creative order. |
| `TCItemVariants` or later data-component helpers | Creates special visible variant stacks when one legacy item maps to multiple 1.21.1 stacks. |
| `creative_tab_order_reference.md` | Human reference for preserving and reviewing order. |

Do not use registration order as the creative order.

Do not use alphabetical sorting.

Do not allow each registry class to add entries independently.

## Variant Preservation Rules

Legacy metadata variants need one of these strategies:

| Legacy pattern | NeoForge port option | Creative tab rule |
|---|---|---|
| One item with metadata variants, simple resource item | Prefer separate registered items if they behave as separate resources. | Add them in legacy variant order. |
| One item with metadata variants, shared behavior | Use one registered item plus Data Components or separate ItemStacks if needed. | Add the visible stacks in legacy variant order. |
| Damage-based visual variant | Use durability, Data Components, or custom model predicates depending on behavior. | Only show the same visible creative variants as 1.12.2. |
| Dynamic variants, such as seals | Generate display order after the underlying registry is known. | Blank seal first, then registered seals in legacy seal registration order. |
| Config-gated variants | Keep the same config behavior. | Example: cheat Thaumonomicon should follow the 1.12.2 config gate. |

## First Safe Creative Tab Implementation Strategy

First implementation should not attempt to add all Thaumcraft entries.

Recommended Gate 1 strategy:

1. Create the Thaumcraft creative tab.
2. Create `TCCreativeTabOrder`.
3. Add only a small set of simple implemented items.
4. Keep their relative order according to this file.
5. Build.
6. Launch client.
7. Compare the visible result with the expected order for implemented entries.

First safe item candidates:

| Candidate | Why safe | Caution |
|---|---|---|
| `amber` | Simple resource item. | Needs texture/model. |
| `quicksilver` | Simple resource item. | Needs texture/model. |
| `ingot` variants | Simple subtype/resource group. | Decide separate items vs visible variant stacks. |
| `nugget` variants | Simple resource group. | Many variants, keep exact variant order. |
| `cluster` variants | Simple resource group. | Keep exact variant order. |
| `fabric` | Simple resource item. | Needs texture/model. |
| `tallow` | Simple resource item. | Needs texture/model. |
| `mechanism_simple` | Simple resource item. | Needs texture/model. |
| `mechanism_complex` | Simple resource item. | Needs texture/model. |
| `plate` variants | Simple resource group. | Keep variant order: brass, iron, thaumium, void. |
| `filter` | Simple resource item. | Needs texture/model. |
| `morphic_resonator` | Resource/tool-like item, but can start as inert. | Behavior later. |
| `salis_mundus` | Iconic item, but has behavior. | Start as inert only if explicitly marked temporary. |

## Code-Derived Block Display Order Candidate

This table is derived from `ConfigBlocks.initBlocks(...)` and manual item-block registrations. It should be reconciled with screenshots 1-4.

| Legacy display order candidate | API field / group | Legacy registry id | Source | Status | Notes |
|---:|---|---|---|---|---|
| 1 | `oreAmber` | `thaumcraft:ore_amber` | `BlockOreTC` | Pending |  |
| 2 | `oreCinnabar` | `thaumcraft:ore_cinnabar` | `BlockOreTC` | Pending |  |
| 3 | `oreQuartz` | `thaumcraft:ore_quartz` | `BlockOreTC` | Pending |  |
| 4 | `crystalAir` | `thaumcraft:crystal_aer` | `BlockCrystal` | Pending |  |
| 5 | `crystalFire` | `thaumcraft:crystal_ignis` | `BlockCrystal` | Pending |  |
| 6 | `crystalWater` | `thaumcraft:crystal_aqua` | `BlockCrystal` | Pending |  |
| 7 | `crystalEarth` | `thaumcraft:crystal_terra` | `BlockCrystal` | Pending |  |
| 8 | `crystalOrder` | `thaumcraft:crystal_ordo` | `BlockCrystal` | Pending |  |
| 9 | `crystalEntropy` | `thaumcraft:crystal_perditio` | `BlockCrystal` | Pending |  |
| 10 | `crystalTaint` | `thaumcraft:crystal_vitium` | `BlockCrystal` | Pending |  |
| 11 | `stoneArcane` | `thaumcraft:stone_arcane` | `BlockStoneTC` | Pending |  |
| 12 | `stoneArcaneBrick` | `thaumcraft:stone_arcane_brick` | `BlockStoneTC` | Pending |  |
| 13 | `stoneAncient` | `thaumcraft:stone_ancient` | `BlockStoneTC` | Pending |  |
| 14 | `stoneAncientTile` | `thaumcraft:stone_ancient_tile` | `BlockStoneTC` | Pending |  |
| 15 | `stoneAncientRock` | `thaumcraft:stone_ancient_rock` | `BlockStoneTC` | Pending |  |
| 16 | `stoneAncientGlyphed` | `thaumcraft:stone_ancient_glyphed` | `BlockStoneTC` | Pending |  |
| 17 | `stoneAncientDoorway` | `thaumcraft:stone_ancient_doorway` | `BlockStoneTC` | Pending |  |
| 18 | `stoneEldritchTile` | `thaumcraft:stone_eldritch_tile` | `BlockStoneTC` | Pending |  |
| 19 | `stonePorous` | `thaumcraft:stone_porous` | `BlockStonePorous` | Pending |  |
| 20 | `stairsArcane` | `thaumcraft:stairs_arcane` | `BlockStairsTC` | Pending |  |
| 21 | `stairsArcaneBrick` | `thaumcraft:stairs_arcane_brick` | `BlockStairsTC` | Pending |  |
| 22 | `stairsAncient` | `thaumcraft:stairs_ancient` | `BlockStairsTC` | Pending |  |
| 23 | `slabArcaneStone` | `thaumcraft:slab_arcane_stone` | `ConfigBlocks.initBlocks manual slab item` | Pending | manual slab item registration |
| 24 | `slabArcaneBrick` | `thaumcraft:slab_arcane_brick` | `ConfigBlocks.initBlocks manual slab item` | Pending | manual slab item registration |
| 25 | `slabAncient` | `thaumcraft:slab_ancient` | `ConfigBlocks.initBlocks manual slab item` | Pending | manual slab item registration |
| 26 | `slabEldritch` | `thaumcraft:slab_eldritch` | `ConfigBlocks.initBlocks manual slab item` | Pending | manual slab item registration |
| 27 | `saplingGreatwood` | `thaumcraft:sapling_greatwood` | `BlockSaplingTC` | Pending |  |
| 28 | `saplingSilverwood` | `thaumcraft:sapling_silverwood` | `BlockSaplingTC` | Pending |  |
| 29 | `logGreatwood` | `thaumcraft:log_greatwood` | `BlockLogsTC` | Pending |  |
| 30 | `logSilverwood` | `thaumcraft:log_silverwood` | `BlockLogsTC` | Pending |  |
| 31 | `leafGreatwood` | `thaumcraft:leaves_greatwood` | `BlockLeavesTC` | Pending |  |
| 32 | `leafSilverwood` | `thaumcraft:leaves_silverwood` | `BlockLeavesTC` | Pending |  |
| 33 | `shimmerleaf` | `thaumcraft:shimmerleaf` | `BlockPlantShimmerleaf` | Pending |  |
| 34 | `cinderpearl` | `thaumcraft:cinderpearl` | `BlockPlantCinderpearl` | Pending |  |
| 35 | `vishroom` | `thaumcraft:vishroom` | `BlockPlantVishroom` | Pending |  |
| 36 | `plankGreatwood` | `thaumcraft:plank_greatwood` | `BlockPlanksTC` | Pending |  |
| 37 | `plankSilverwood` | `thaumcraft:plank_silverwood` | `BlockPlanksTC` | Pending |  |
| 38 | `stairsGreatwood` | `thaumcraft:stairs_greatwood` | `BlockStairsTC` | Pending |  |
| 39 | `stairsSilverwood` | `thaumcraft:stairs_silverwood` | `BlockStairsTC` | Pending |  |
| 40 | `slabGreatwood` | `thaumcraft:slab_greatwood` | `ConfigBlocks.initBlocks manual slab item` | Pending | manual slab item registration |
| 41 | `slabSilverwood` | `thaumcraft:slab_silverwood` | `ConfigBlocks.initBlocks manual slab item` | Pending | manual slab item registration |
| 42 | `amberBlock` | `thaumcraft:amber_block` | `BlockTranslucent` | Pending |  |
| 43 | `amberBrick` | `thaumcraft:amber_brick` | `BlockTranslucent` | Pending |  |
| 44 | `fleshBlock` | `thaumcraft:flesh_block` | `BlockFlesh` | Pending |  |
| 45 | `lootCrateCommon` | `thaumcraft:loot_crate_common` | `BlockLoot` | Pending |  |
| 46 | `lootCrateUncommon` | `thaumcraft:loot_crate_uncommon` | `BlockLoot` | Pending |  |
| 47 | `lootCrateRare` | `thaumcraft:loot_crate_rare` | `BlockLoot` | Pending |  |
| 48 | `lootUrnCommon` | `thaumcraft:loot_urn_common` | `BlockLoot` | Pending |  |
| 49 | `lootUrnUncommon` | `thaumcraft:loot_urn_uncommon` | `BlockLoot` | Pending |  |
| 50 | `lootUrnRare` | `thaumcraft:loot_urn_rare` | `BlockLoot` | Pending |  |
| 51 | `taintFibre` | `thaumcraft:taint_fibre` | `BlockTaintFibre` | Pending |  |
| 52 | `taintCrust` | `thaumcraft:taint_crust` | `BlockTaint` | Pending |  |
| 53 | `taintSoil` | `thaumcraft:taint_soil` | `BlockTaint` | Pending |  |
| 54 | `taintRock` | `thaumcraft:taint_rock` | `BlockTaint` | Pending |  |
| 55 | `taintGeyser` | `thaumcraft:taint_geyser` | `BlockTaint` | Pending |  |
| 56 | `taintFeature` | `thaumcraft:taint_feature` | `BlockTaintFeature` | Pending |  |
| 57 | `taintLog` | `thaumcraft:taint_log` | `BlockTaintLog` | Pending |  |
| 58 | `grassAmbient` | `thaumcraft:grass_ambient` | `BlockGrassAmbient` | Pending |  |
| 59 | `tableWood` | `thaumcraft:table_wood` | `BlockTable` | Pending |  |
| 60 | `tableStone` | `thaumcraft:table_stone` | `BlockTable` | Pending |  |
| 61 | `pedestalArcane` | `thaumcraft:pedestal_arcane` | `BlockPedestal` | Pending |  |
| 62 | `pedestalAncient` | `thaumcraft:pedestal_ancient` | `BlockPedestal` | Pending |  |
| 63 | `pedestalEldritch` | `thaumcraft:pedestal_eldritch` | `BlockPedestal` | Pending |  |
| 64 | `metalBlockBrass` | `thaumcraft:metal_brass` | `BlockMetalTC` | Pending |  |
| 65 | `metalBlockThaumium` | `thaumcraft:metal_thaumium` | `BlockMetalTC` | Pending |  |
| 66 | `metalBlockVoid` | `thaumcraft:metal_void` | `BlockMetalTC` | Pending |  |
| 67 | `metalAlchemical` | `thaumcraft:metal_alchemical` | `BlockMetalTC` | Pending |  |
| 68 | `metalAlchemicalAdvanced` | `thaumcraft:metal_alchemical_advanced` | `BlockMetalTC` | Pending |  |
| 69 | `pavingStoneTravel` | `thaumcraft:paving_stone_travel` | `BlockPavingStone` | Pending |  |
| 70 | `pavingStoneBarrier` | `thaumcraft:paving_stone_barrier` | `BlockPavingStone` | Pending |  |
| 71 | `pillarArcane` | `thaumcraft:pillar_arcane` | `BlockPillar` | Pending |  |
| 72 | `pillarAncient` | `thaumcraft:pillar_ancient` | `BlockPillar` | Pending |  |
| 73 | `pillarEldritch` | `thaumcraft:pillar_eldritch` | `BlockPillar` | Pending |  |
| 74 | `matrixSpeed` | `thaumcraft:matrix_speed` | `BlockStoneTC` | Pending |  |
| 75 | `matrixCost` | `thaumcraft:matrix_cost` | `BlockStoneTC` | Pending |  |
| 76 | `candles[white]` | `thaumcraft:candle_white` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 77 | `candles[orange]` | `thaumcraft:candle_orange` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 78 | `candles[magenta]` | `thaumcraft:candle_magenta` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 79 | `candles[light_blue]` | `thaumcraft:candle_light_blue` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 80 | `candles[yellow]` | `thaumcraft:candle_yellow` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 81 | `candles[lime]` | `thaumcraft:candle_lime` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 82 | `candles[pink]` | `thaumcraft:candle_pink` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 83 | `candles[gray]` | `thaumcraft:candle_gray` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 84 | `candles[silver]` | `thaumcraft:candle_silver` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 85 | `candles[cyan]` | `thaumcraft:candle_cyan` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 86 | `candles[purple]` | `thaumcraft:candle_purple` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 87 | `candles[blue]` | `thaumcraft:candle_blue` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 88 | `candles[brown]` | `thaumcraft:candle_brown` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 89 | `candles[green]` | `thaumcraft:candle_green` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 90 | `candles[red]` | `thaumcraft:candle_red` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 91 | `candles[black]` | `thaumcraft:candle_black` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 92 | `banners[white]` | `thaumcraft:banner_white` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 93 | `banners[orange]` | `thaumcraft:banner_orange` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 94 | `banners[magenta]` | `thaumcraft:banner_magenta` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 95 | `banners[light_blue]` | `thaumcraft:banner_light_blue` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 96 | `banners[yellow]` | `thaumcraft:banner_yellow` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 97 | `banners[lime]` | `thaumcraft:banner_lime` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 98 | `banners[pink]` | `thaumcraft:banner_pink` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 99 | `banners[gray]` | `thaumcraft:banner_gray` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 100 | `banners[silver]` | `thaumcraft:banner_silver` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 101 | `banners[cyan]` | `thaumcraft:banner_cyan` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 102 | `banners[purple]` | `thaumcraft:banner_purple` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 103 | `banners[blue]` | `thaumcraft:banner_blue` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 104 | `banners[brown]` | `thaumcraft:banner_brown` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 105 | `banners[green]` | `thaumcraft:banner_green` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 106 | `banners[red]` | `thaumcraft:banner_red` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 107 | `banners[black]` | `thaumcraft:banner_black` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 108 | `bannerCrimsonCult` | `thaumcraft:banner_crimson_cult` | `ConfigBlocks.initBlocks` | Pending | manual banner after dye banners |
| 109 | `bannerCrimsonCult` | `thaumcraft:banner_crimson_cult` | `ConfigBlocks.initBlocks` | Pending | manual banner after dye banners |
| 110 | `nitor[white]` | `thaumcraft:nitor_white` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 111 | `nitor[orange]` | `thaumcraft:nitor_orange` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 112 | `nitor[magenta]` | `thaumcraft:nitor_magenta` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 113 | `nitor[light_blue]` | `thaumcraft:nitor_light_blue` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 114 | `nitor[yellow]` | `thaumcraft:nitor_yellow` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 115 | `nitor[lime]` | `thaumcraft:nitor_lime` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 116 | `nitor[pink]` | `thaumcraft:nitor_pink` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 117 | `nitor[gray]` | `thaumcraft:nitor_gray` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 118 | `nitor[silver]` | `thaumcraft:nitor_silver` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 119 | `nitor[cyan]` | `thaumcraft:nitor_cyan` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 120 | `nitor[purple]` | `thaumcraft:nitor_purple` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 121 | `nitor[blue]` | `thaumcraft:nitor_blue` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 122 | `nitor[brown]` | `thaumcraft:nitor_brown` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 123 | `nitor[green]` | `thaumcraft:nitor_green` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 124 | `nitor[red]` | `thaumcraft:nitor_red` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 125 | `nitor[black]` | `thaumcraft:nitor_black` | `ConfigBlocks.initBlocks loop` | Pending | dye loop, EnumDyeColor order |
| 126 | `visBattery` | `thaumcraft:vis_battery` | `BlockVisBattery` | Pending |  |
| 127 | `inlay` | `thaumcraft:inlay` | `BlockInlay` | Pending |  |
| 128 | `arcaneWorkbench` | `thaumcraft:arcane_workbench` | `BlockArcaneWorkbench` | Pending |  |
| 129 | `arcaneWorkbenchCharger` | `thaumcraft:arcane_workbench_charger` | `BlockArcaneWorkbenchCharger` | Pending |  |
| 130 | `dioptra` | `thaumcraft:dioptra` | `BlockDioptra` | Pending |  |
| 131 | `researchTable` | `thaumcraft:research_table` | `BlockResearchTable` | Pending |  |
| 132 | `crucible` | `thaumcraft:crucible` | `BlockCrucible` | Pending |  |
| 133 | `arcaneEar` | `thaumcraft:arcane_ear` | `BlockArcaneEar` | Pending |  |
| 134 | `arcaneEarToggle` | `thaumcraft:arcane_ear_toggle` | `BlockArcaneEarToggle` | Pending |  |
| 135 | `lampArcane` | `thaumcraft:lamp_arcane` | `BlockLamp` | Pending |  |
| 136 | `lampFertility` | `thaumcraft:lamp_fertility` | `BlockLamp` | Pending |  |
| 137 | `lampGrowth` | `thaumcraft:lamp_growth` | `BlockLamp` | Pending |  |
| 138 | `levitator` | `thaumcraft:levitator` | `BlockLevitator` | Pending |  |
| 139 | `centrifuge` | `thaumcraft:centrifuge` | `BlockCentrifuge` | Pending |  |
| 140 | `bellows` | `thaumcraft:bellows` | `BlockBellows` | Pending |  |
| 141 | `smelterBasic` | `thaumcraft:smelter_basic` | `BlockSmelter` | Pending |  |
| 142 | `smelterThaumium` | `thaumcraft:smelter_thaumium` | `BlockSmelter` | Pending |  |
| 143 | `smelterVoid` | `thaumcraft:smelter_void` | `BlockSmelter` | Pending |  |
| 144 | `smelterAux` | `thaumcraft:smelter_aux` | `BlockSmelterAux` | Pending |  |
| 145 | `smelterVent` | `thaumcraft:smelter_vent` | `BlockSmelterVent` | Pending |  |
| 146 | `alembic` | `thaumcraft:alembic` | `BlockAlembic` | Pending |  |
| 147 | `rechargePedestal` | `thaumcraft:recharge_pedestal` | `BlockRechargePedestal` | Pending |  |
| 148 | `wandWorkbench` | `thaumcraft:wand_workbench` | `BlockFocalManipulator` | Pending |  |
| 149 | `hungryChest` | `thaumcraft:hungry_chest` | `BlockHungryChest` | Pending |  |
| 150 | `tube` | `thaumcraft:tube` | `BlockTube` | Pending |  |
| 151 | `tubeValve` | `thaumcraft:tube_valve` | `BlockTube` | Pending |  |
| 152 | `tubeRestrict` | `thaumcraft:tube_restrict` | `BlockTube` | Pending |  |
| 153 | `tubeOneway` | `thaumcraft:tube_oneway` | `BlockTube` | Pending |  |
| 154 | `tubeFilter` | `thaumcraft:tube_filter` | `BlockTube` | Pending |  |
| 155 | `tubeBuffer` | `thaumcraft:tube_buffer` | `BlockTube` | Pending |  |
| 156 | `jarNormal` | `thaumcraft:jar_normal` | `BlockJar` | Pending |  |
| 157 | `jarVoid` | `thaumcraft:jar_void` | `BlockJar` | Pending |  |
| 158 | `jarBrain` | `thaumcraft:jar_brain` | `BlockJar` | Pending |  |
| 159 | `infusionMatrix` | `thaumcraft:infusion_matrix` | `BlockInfusionMatrix` | Pending |  |
| 160 | `infernalFurnace` | `thaumcraft:infernal_furnace` | `BlockInfernalFurnace` | Pending |  |
| 161 | `everfullUrn` | `thaumcraft:everfull_urn` | `BlockWaterJug` | Pending |  |
| 162 | `thaumatorium` | `thaumcraft:thaumatorium_top` | `BlockThaumatorium` | Pending |  |
| 163 | `thaumatoriumTop` | `thaumcraft:thaumatorium_top` | `BlockThaumatorium` | Pending |  |
| 164 | `brainBox` | `thaumcraft:brain_box` | `BlockBrainBox` | Pending |  |
| 165 | `spa` | `thaumcraft:spa` | `BlockSpa` | Pending |  |
| 166 | `golemBuilder` | `thaumcraft:golem_builder` | `BlockGolemBuilder` | Pending |  |
| 167 | `mirror` | `thaumcraft:mirror` | `BlockMirror` | Pending |  |
| 168 | `mirrorEssentia` | `thaumcraft:mirror_essentia` | `BlockMirror` | Pending |  |
| 169 | `essentiaTransportInput` | `thaumcraft:essentia_input` | `BlockEssentiaTransport` | Pending |  |
| 170 | `essentiaTransportOutput` | `thaumcraft:essentia_output` | `BlockEssentiaTransport` | Pending |  |
| 171 | `redstoneRelay` | `thaumcraft:redstone_relay` | `BlockRedstoneRelay` | Pending |  |
| 172 | `patternCrafter` | `thaumcraft:pattern_crafter` | `BlockPatternCrafter` | Pending |  |
| 173 | `potionSprayer` | `thaumcraft:potion_sprayer` | `BlockPotionSprayer` | Pending |  |
| 174 | `activatorRail` | `thaumcraft:activator_rail` | `BlockRailPowered` | Pending |  |
| 175 | `stabilizer` | `thaumcraft:stabilizer` | `BlockStabilizer` | Pending |  |
| 176 | `visGenerator` | `thaumcraft:vis_generator` | `BlockVisGenerator` | Pending |  |
| 177 | `condenser` | `thaumcraft:condenser` | `BlockCondenser` | Pending |  |
| 178 | `condenserlattice` | `thaumcraft:condenser_lattice_dirty` | `BlockCondenserLattice` | Pending |  |
| 179 | `condenserlatticeDirty` | `thaumcraft:condenser_lattice_dirty` | `BlockCondenserLattice` | Pending |  |
| 180 | `voidSiphon` | `thaumcraft:void_siphon` | `BlockVoidSiphon` | Pending |  |
| 181 | `hole` | `thaumcraft:hole` | `BlockHole` | Pending |  |
| 182 | `effectShock` | `thaumcraft:effect_shock` | `BlockEffect` | Pending |  |
| 183 | `effectSap` | `thaumcraft:effect_sap` | `BlockEffect` | Pending |  |
| 184 | `effectGlimmer` | `thaumcraft:effect_glimmer` | `BlockEffect` | Pending |  |
| 185 | `placeholderNetherbrick` | `thaumcraft:placeholder_brick` | `BlockPlaceholder` | Pending |  |
| 186 | `placeholderObsidian` | `thaumcraft:placeholder_obsidian` | `BlockPlaceholder` | Pending |  |
| 187 | `placeholderBars` | `thaumcraft:placeholder_bars` | `BlockPlaceholder` | Pending |  |
| 188 | `placeholderAnvil` | `thaumcraft:placeholder_anvil` | `BlockPlaceholder` | Pending |  |
| 189 | `placeholderCauldron` | `thaumcraft:placeholder_cauldron` | `BlockPlaceholder` | Pending |  |
| 190 | `placeholderTable` | `thaumcraft:placeholder_table` | `BlockPlaceholder` | Pending |  |
| 191 | `empty` | `thaumcraft:empty` | `BlockTranslucent` | Pending |  |
| 192 | `barrier` | `thaumcraft:barrier` | `BlockBarrier` | Pending |  |

## Code-Derived Item Display Order Candidate

This table is derived from `ConfigItems.initItems(...)` and subtype constructors. It should be reconciled with screenshots 5-10.

| Legacy registry order | API field | Legacy registry id | Visible variants / subitems | Legacy class | Port status | Notes |
|---:|---|---|---|---|---|---|
| 1 | `thaumonomicon` | `thaumcraft:thaumonomicon` | normal, cheat | `ItemThaumonomicon` | Pending | cheat variant is config-gated in 1.12.2 |
| 2 | `curio` | `thaumcraft:curio` | arcane, preserved, ancient, eldritch, knowledge, twisted, rites | `ItemCurio` | Pending |  |
| 3 | `lootBag` | `thaumcraft:loot_bag` | common, uncommon, rare | `ItemLootBag` | Pending |  |
| 4 | `primordialPearl` | `thaumcraft:primordial_pearl` | single visible stack | `ItemPrimordialPearl` | Pending | damage changes visual/name; creative listing needs visual check |
| 5 | `pechWand` | `thaumcraft:pech_wand` | single visible stack | `ItemPechWand` | Pending |  |
| 6 | `celestialNotes` | `thaumcraft:celestial_notes` | sun, stars_1, stars_2, stars_3, stars_4, moon_1, moon_2, moon_3, moon_4, moon_5, moon_6, moon_7, moon_8 | `ItemCelestialNotes` | Pending |  |
| 7 | `amber` | `thaumcraft:amber` | single visible stack | `ItemTCBase` | Pending |  |
| 8 | `quicksilver` | `thaumcraft:quicksilver` | single visible stack | `ItemTCBase` | Pending |  |
| 9 | `ingots` | `thaumcraft:ingot` | thaumium, void, brass | `ItemTCBase` | Pending |  |
| 10 | `nuggets` | `thaumcraft:nugget` | iron, copper, tin, silver, lead, quicksilver, thaumium, void, brass, quartz, rareearth | `ItemTCBase` | Pending |  |
| 11 | `clusters` | `thaumcraft:cluster` | iron, gold, copper, tin, silver, lead, cinnabar, quartz | `ItemTCBase` | Pending |  |
| 12 | `fabric` | `thaumcraft:fabric` | single visible stack | `ItemTCBase` | Pending |  |
| 13 | `visResonator` | `thaumcraft:vis_resonator` | single visible stack | `ItemTCBase` | Pending |  |
| 14 | `tallow` | `thaumcraft:tallow` | single visible stack | `ItemTCBase` | Pending |  |
| 15 | `mechanismSimple` | `thaumcraft:mechanism_simple` | single visible stack | `ItemTCBase` | Pending |  |
| 16 | `mechanismComplex` | `thaumcraft:mechanism_complex` | single visible stack | `ItemTCBase` | Pending |  |
| 17 | `plate` | `thaumcraft:plate` | brass, iron, thaumium, void | `ItemTCBase` | Pending |  |
| 18 | `filter` | `thaumcraft:filter` | single visible stack | `ItemTCBase` | Pending |  |
| 19 | `morphicResonator` | `thaumcraft:morphic_resonator` | single visible stack | `ItemTCBase` | Pending |  |
| 20 | `salisMundus` | `thaumcraft:salis_mundus` | single visible stack | `ItemMagicDust` | Pending |  |
| 21 | `mirroredGlass` | `thaumcraft:mirrored_glass` | single visible stack | `ItemTCBase` | Pending |  |
| 22 | `voidSeed` | `thaumcraft:void_seed` | single visible stack | `ItemTCBase` | Pending |  |
| 23 | `mind` | `thaumcraft:mind` | clockwork, biothaumic | `ItemTCBase` | Pending |  |
| 24 | `modules` | `thaumcraft:module` | vision, aggression | `ItemTCBase` | Pending |  |
| 25 | `crystalEssence` | `thaumcraft:crystal_essence` | single visible stack | `ItemCrystalEssence` | Pending |  |
| 26 | `chunks` | `thaumcraft:chunk` | beef, chicken, pork, fish, rabbit, mutton | `ItemChunksEdible` | Pending |  |
| 27 | `tripleMeatTreat` | `thaumcraft:triple_meat_treat` | single visible stack | `ItemTripleMeatTreat` | Pending |  |
| 28 | `brain` | `thaumcraft:brain` | single visible stack | `ItemZombieBrain` | Pending |  |
| 29 | `label` | `thaumcraft:label` | single visible stack | `ItemLabel` | Pending |  |
| 30 | `phial` | `thaumcraft:phial` | single visible stack | `ItemPhial` | Pending |  |
| 31 | `alumentum` | `thaumcraft:alumentum` | single visible stack | `ItemAlumentum` | Pending |  |
| 32 | `jarBrace` | `thaumcraft:jar_brace` | single visible stack | `ItemTCBase` | Pending |  |
| 33 | `bottleTaint` | `thaumcraft:bottle_taint` | single visible stack | `ItemBottleTaint` | Pending |  |
| 34 | `sanitySoap` | `thaumcraft:sanity_soap` | single visible stack | `ItemSanitySoap` | Pending |  |
| 35 | `bathSalts` | `thaumcraft:bath_salts` | single visible stack | `ItemBathSalts` | Pending |  |
| 36 | `turretPlacer` | `thaumcraft:turret` | basic, advanced, bore | `ItemTurretPlacer` | Pending |  |
| 37 | `causalityCollapser` | `thaumcraft:causality_collapser` | single visible stack | `ItemCausalityCollapser` | Pending |  |
| 38 | `scribingTools` | `thaumcraft:scribing_tools` | single visible stack | `ItemScribingTools` | Pending |  |
| 39 | `thaumometer` | `thaumcraft:thaumometer` | single visible stack | `ItemThaumometer` | Pending |  |
| 40 | `resonator` | `thaumcraft:resonator` | single visible stack | `ItemResonator` | Pending |  |
| 41 | `sanityChecker` | `thaumcraft:sanity_checker` | single visible stack | `ItemSanityChecker` | Pending |  |
| 42 | `handMirror` | `thaumcraft:hand_mirror` | single visible stack | `ItemHandMirror` | Pending |  |
| 43 | `thaumiumAxe` | `thaumcraft:thaumium_axe` | single visible stack | `ItemThaumiumAxe` | Pending |  |
| 44 | `thaumiumSword` | `thaumcraft:thaumium_sword` | single visible stack | `ItemThaumiumSword` | Pending |  |
| 45 | `thaumiumShovel` | `thaumcraft:thaumium_shovel` | single visible stack | `ItemThaumiumShovel` | Pending |  |
| 46 | `thaumiumPick` | `thaumcraft:thaumium_pick` | single visible stack | `ItemThaumiumPickaxe` | Pending |  |
| 47 | `thaumiumHoe` | `thaumcraft:thaumium_hoe` | single visible stack | `ItemThaumiumHoe` | Pending |  |
| 48 | `voidAxe` | `thaumcraft:void_axe` | single visible stack | `ItemVoidAxe` | Pending |  |
| 49 | `voidSword` | `thaumcraft:void_sword` | single visible stack | `ItemVoidSword` | Pending |  |
| 50 | `voidShovel` | `thaumcraft:void_shovel` | single visible stack | `ItemVoidShovel` | Pending |  |
| 51 | `voidPick` | `thaumcraft:void_pick` | single visible stack | `ItemVoidPickaxe` | Pending |  |
| 52 | `voidHoe` | `thaumcraft:void_hoe` | single visible stack | `ItemVoidHoe` | Pending |  |
| 53 | `elementalAxe` | `thaumcraft:elemental_axe` | single visible stack | `ItemElementalAxe` | Pending |  |
| 54 | `elementalSword` | `thaumcraft:elemental_sword` | single visible stack | `ItemElementalSword` | Pending |  |
| 55 | `elementalShovel` | `thaumcraft:elemental_shovel` | single visible stack | `ItemElementalShovel` | Pending |  |
| 56 | `elementalPick` | `thaumcraft:elemental_pick` | single visible stack | `ItemElementalPickaxe` | Pending |  |
| 57 | `elementalHoe` | `thaumcraft:elemental_hoe` | single visible stack | `ItemElementalHoe` | Pending |  |
| 58 | `primalCrusher` | `thaumcraft:primal_crusher` | single visible stack | `ItemPrimalCrusher` | Pending |  |
| 59 | `crimsonBlade` | `thaumcraft:crimson_blade` | single visible stack | `ItemCrimsonBlade` | Pending |  |
| 60 | `grappleGun` | `thaumcraft:grapple_gun` | single visible stack | `ItemGrappleGun` | Pending |  |
| 61 | `grappleGunTip` | `thaumcraft:grapple_gun_tip` | single visible stack | `ItemTCBase` | Pending |  |
| 62 | `grappleGunSpool` | `thaumcraft:grapple_gun_spool` | single visible stack | `ItemTCBase` | Pending |  |
| 63 | `goggles` | `thaumcraft:goggles` | single visible stack | `ItemGoggles` | Pending |  |
| 64 | `thaumiumHelm` | `thaumcraft:thaumium_helm` | single visible stack | `ItemThaumiumArmor` | Pending |  |
| 65 | `thaumiumChest` | `thaumcraft:thaumium_chest` | single visible stack | `ItemThaumiumArmor` | Pending |  |
| 66 | `thaumiumLegs` | `thaumcraft:thaumium_legs` | single visible stack | `ItemThaumiumArmor` | Pending |  |
| 67 | `thaumiumBoots` | `thaumcraft:thaumium_boots` | single visible stack | `ItemThaumiumArmor` | Pending |  |
| 68 | `clothChest` | `thaumcraft:cloth_chest` | single visible stack | `ItemRobeArmor` | Pending |  |
| 69 | `clothLegs` | `thaumcraft:cloth_legs` | single visible stack | `ItemRobeArmor` | Pending |  |
| 70 | `clothBoots` | `thaumcraft:cloth_boots` | single visible stack | `ItemRobeArmor` | Pending |  |
| 71 | `travellerBoots` | `thaumcraft:traveller_boots` | single visible stack | `ItemBootsTraveller` | Pending |  |
| 72 | `fortressHelm` | `thaumcraft:fortress_helm` | single visible stack | `ItemFortressArmor` | Pending |  |
| 73 | `fortressChest` | `thaumcraft:fortress_chest` | single visible stack | `ItemFortressArmor` | Pending |  |
| 74 | `fortressLegs` | `thaumcraft:fortress_legs` | single visible stack | `ItemFortressArmor` | Pending |  |
| 75 | `voidHelm` | `thaumcraft:void_helm` | single visible stack | `ItemVoidArmor` | Pending |  |
| 76 | `voidChest` | `thaumcraft:void_chest` | single visible stack | `ItemVoidArmor` | Pending |  |
| 77 | `voidLegs` | `thaumcraft:void_legs` | single visible stack | `ItemVoidArmor` | Pending |  |
| 78 | `voidBoots` | `thaumcraft:void_boots` | single visible stack | `ItemVoidArmor` | Pending |  |
| 79 | `voidRobeHelm` | `thaumcraft:void_robe_helm` | single visible stack | `ItemVoidRobeArmor` | Pending |  |
| 80 | `voidRobeChest` | `thaumcraft:void_robe_chest` | single visible stack | `ItemVoidRobeArmor` | Pending |  |
| 81 | `voidRobeLegs` | `thaumcraft:void_robe_legs` | single visible stack | `ItemVoidRobeArmor` | Pending |  |
| 82 | `crimsonPlateHelm` | `thaumcraft:crimson_plate_helm` | single visible stack | `ItemCultistPlateArmor` | Pending |  |
| 83 | `crimsonPlateChest` | `thaumcraft:crimson_plate_chest` | single visible stack | `ItemCultistPlateArmor` | Pending |  |
| 84 | `crimsonPlateLegs` | `thaumcraft:crimson_plate_legs` | single visible stack | `ItemCultistPlateArmor` | Pending |  |
| 85 | `crimsonBoots` | `thaumcraft:crimson_boots` | single visible stack | `ItemCultistBoots` | Pending |  |
| 86 | `crimsonRobeHelm` | `thaumcraft:crimson_robe_helm` | single visible stack | `ItemCultistRobeArmor` | Pending |  |
| 87 | `crimsonRobeChest` | `thaumcraft:crimson_robe_chest` | single visible stack | `ItemCultistRobeArmor` | Pending |  |
| 88 | `crimsonRobeLegs` | `thaumcraft:crimson_robe_legs` | single visible stack | `ItemCultistRobeArmor` | Pending |  |
| 89 | `crimsonPraetorHelm` | `thaumcraft:crimson_praetor_helm` | single visible stack | `ItemCultistLeaderArmor` | Pending |  |
| 90 | `crimsonPraetorChest` | `thaumcraft:crimson_praetor_chest` | single visible stack | `ItemCultistLeaderArmor` | Pending |  |
| 91 | `crimsonPraetorLegs` | `thaumcraft:crimson_praetor_legs` | single visible stack | `ItemCultistLeaderArmor` | Pending |  |
| 92 | `baubles` | `thaumcraft:baubles` | amulet_mundane, ring_mundane, girdle_mundane, ring_apprentice, amulet_fancy, ring_fancy, girdle_fancy | `ItemBaubles` | Pending |  |
| 93 | `amuletVis` | `thaumcraft:amulet_vis` | found, crafted | `ItemAmuletVis` | Pending |  |
| 94 | `charmVerdant` | `thaumcraft:verdant_charm` | single visible stack | `ItemVerdantCharm` | Pending |  |
| 95 | `bandCuriosity` | `thaumcraft:curiosity_band` | single visible stack | `ItemCuriosityBand` | Pending |  |
| 96 | `charmVoidseer` | `thaumcraft:voidseer_charm` | single visible stack | `ItemVoidseerCharm` | Pending |  |
| 97 | `ringCloud` | `thaumcraft:cloud_ring` | single visible stack | `ItemCloudRing` | Pending |  |
| 98 | `charmUndying` | `thaumcraft:charm_undying` | single visible stack | `ItemCharmUndying` | Pending |  |
| 99 | `creativeFluxSponge` | `thaumcraft:creative_flux_sponge` | single visible stack | `ItemCreativeFluxSponge` | Pending |  |
| 100 | `enchantedPlaceholder` | `thaumcraft:enchanted_placeholder` | single visible stack | `ItemEnchantmentPlaceholder` | Pending |  |
| 101 | `casterBasic` | `thaumcraft:caster_basic` | single visible stack | `ItemCaster` | Pending | caster/focus behavior later; registry/display can be staged |
| 102 | `focus1` | `thaumcraft:focus_1` | single visible stack | `ItemFocus` | Pending | caster/focus behavior later; registry/display can be staged |
| 103 | `focus2` | `thaumcraft:focus_2` | single visible stack | `ItemFocus` | Pending | caster/focus behavior later; registry/display can be staged |
| 104 | `focus3` | `thaumcraft:focus_3` | single visible stack | `ItemFocus` | Pending | caster/focus behavior later; registry/display can be staged |
| 105 | `focusPouch` | `thaumcraft:focus_pouch` | single visible stack | `ItemFocusPouch` | Pending |  |
| 106 | `golemBell` | `thaumcraft:golem_bell` | single visible stack | `ItemGolemBell` | Pending |  |
| 107 | `golemPlacer` | `thaumcraft:golem` | single visible stack | `ItemGolemPlacer` | Pending |  |
| 108 | `seals` | `thaumcraft:seal` | blank | `ItemSealPlacer` | Pending | variants are dynamic after seal registration |

## Known Special Cases

| Entry | Issue | Porting decision |
|---|---|---|
| `thaumonomicon` | Has normal and cheat variant in 1.12.2, cheat variant is config-gated. | Preserve config gate. Do not always show cheat variant. |
| `primordial_pearl` | Damage value changes display/name and container behavior. | Needs separate visual decision before final creative order. |
| `celestial_notes` | Many moon/star variants. | Preserve visible variant order if these are exposed in creative. |
| `crystal_essence` | Aspect-based behavior. | Do not finalize before aspect registry/data model is stable. |
| `phial` | Likely depends on essentia/aspect storage. | Delay behavior; visual/order can be staged. |
| `turret` | Subtypes basic/advanced/bore and entity behavior. | Delay behavior; preserve visible order. |
| `caster_basic` and foci | Depend on caster/focus system. | Register later, or temporary inert entries only if marked. |
| `golem` and `seal` | Depend on golem/seal systems. | Delay until seal registry design is stable. |
| Dye blocks: candles, banners, nitor | Legacy uses `EnumDyeColor.values()` order. | Preserve 1.12.2 dye order, not alphabetical order. |
| Slabs | Manual item-block registration in legacy source. | Preserve visual order from screenshots and code. |
| Placeholder/effect/hidden blocks | Some may not be intended for normal player creative use. | Verify against screenshots before adding to release tab. |

## Review Checklist Before Adding Any New Entry

For each new creative entry, answer:

| Question | Required answer |
|---|---|
| Is this entry visible in the 1.12.2 creative tab screenshots? | Yes, No, or Needs visual check. |
| What is its legacy registry id? | `thaumcraft:<id>`. |
| Does it have metadata/subtype variants? | List them in legacy order. |
| Does it depend on aspects, aura, research, essentia, GUI, rendering, worldgen, Baubles/Curios, or networking? | List blockers. |
| Can it be added as inert placeholder during an early gate? | Yes only if clearly marked temporary. |
| Where does it belong relative to already implemented entries? | Reference this document and screenshots. |
| Does the final visible model match the 1.12.2 screenshot? | Verify before marking complete. |

## Suggested Status Values

Use these status labels in future updates:

| Status | Meaning |
|---|---|
| `Pending` | Not implemented in NeoForge yet. |
| `Registered` | Registry object exists. |
| `Visible` | Appears in the creative tab. |
| `Model OK` | Texture/model looks correct in-game. |
| `Order OK` | Position checked against reference screenshots. |
| `Behavior WIP` | Visible but gameplay behavior is incomplete. |
| `Complete` | Visible order, model, and behavior are acceptable. |
| `Deferred` | Intentionally delayed because dependent systems are missing. |
| `Do not show yet` | Registry may exist, but entry should not appear in normal creative tab. |

## Immediate Next Step

Before adding items in code, create or update:

```text
05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java
```

Then wire it from the creative tab builder or creative tab build event.

Do not add entries directly from `TCItems` or `TCBlocks`.

Recommended next document after this one:

```text
06_docs/gate1_items_plan.md
```

That document should select the first small batch of simple items and define the exact files to edit.
