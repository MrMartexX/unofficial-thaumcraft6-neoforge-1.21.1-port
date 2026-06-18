# Thaumcraft 6 Creative Tab Order Reference

This document defines how the NeoForge port preserves the visible Thaumcraft 6 creative tab order from Minecraft 1.12.2.

For the current list of implemented registry entries, use `06_docs/current_port_status.md`. This file defines ordering policy and review rules, not the live implementation inventory.

## Scope

| Area | Location |
|---|---|
| Target project | `05_neoforge_port` |
| Legacy source reference | `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master` |
| Visual reference | `07_Test_Instance_and_Comparisons/02_Thaumcraft 1.12.2 Inventory Screenshots` |
| Related migration matrix | `06_docs/migration/migration_matrix.md` |
| Current status | `06_docs/current_port_status.md` |

## Creative order policy

The Thaumcraft creative tab must not be alphabetized, grouped by implementation class, or left to arbitrary registry declaration order.

The NeoForge port should preserve the visible 1.12.2 order as closely as practical. This applies to:

1. block items;
2. plain items;
3. metadata and subtype variants;
4. data-component variants that are visible in creative;
5. dynamic entries such as golem seals;
6. config-gated entries such as the cheat Thaumonomicon;
7. icon and model presentation where reasonably practical.

## Sources of truth

| Source | Role |
|---|---|
| `ConfigBlocks.initBlocks(...)` | Technical skeleton for block item order |
| `ConfigItems.initItems(...)` | Technical skeleton for item order |
| `ItemTCBase#getSubItems(...)` | Variant order for legacy subtype items |
| 1.12.2 creative screenshots | Final visual review reference |
| `current_port_status.md` | Current implemented entry inventory |

The code-derived order is the implementation skeleton. The screenshot set remains the final visual review source.

## Why registry order is not enough

Thaumcraft 6 1.12.2 creative order is affected by:

1. block item registration during `ConfigBlocks.initBlocks(...)`;
2. item registration during `ConfigItems.initItems(...)`;
3. metadata and subtype expansion from `getSubItems(...)`;
4. hidden, dynamic and config-gated entries;
5. item behavior that exposes multiple visible stacks from one registry item.

In NeoForge 1.21.1, registration order must not be used as the display order. A dedicated creative tab order class is required.

## Implementation contract

| Class | Responsibility |
|---|---|
| `TCCreativeTabs` | Registers the Thaumcraft creative tab. |
| `TCCreativeTabOrder` | Adds visible entries in the 1.12.2 visual order. |
| `TCItems` | Registers item objects only. It must not define creative display order. |
| `TCBlocks` | Registers block objects only. It must not define creative display order. |
| Variant helper classes | Create special visible variant stacks when a legacy item maps to multiple modern stacks. |
| This document | Human-maintained reference for order preservation and review. |

Expected shape:

```java
public final class TCCreativeTabOrder {
    private TCCreativeTabOrder() {}

    public static void addThaumcraftItems(CreativeModeTab.Output output) {
        // Add implemented entries in legacy visual order.
        // Do not alphabetize.
        // Do not group by registry class.
    }
}
```

## Partial port rule

During early gates, not every old entry exists yet. In that case:

1. keep the full order manifest in this document or a dedicated manifest file;
2. add only implemented entries to the NeoForge tab;
3. preserve relative order among implemented entries;
4. do not move an implemented entry earlier only because previous legacy entries are not ported yet;
5. do not ship fake placeholder stacks only to fill visual gaps.

A later debug-only placeholder mode may be useful for visual page comparison. It must not be enabled in normal release behavior.

## Screenshot reference map

| Screenshot | Role | Notes |
|---|---|---|
| `Thaumcraft_1.12.2_Inventory_1.png` | Start of Thaumcraft tab | First visible block/resource page |
| `Thaumcraft_1.12.2_Inventory_2.png` | Block continuation | More blocks and dye-based entries |
| `Thaumcraft_1.12.2_Inventory_3.png` | Dye blocks and early devices | Candles, banners, nitor and early functional blocks |
| `Thaumcraft_1.12.2_Inventory_4.png` | Functional blocks and machines | Workbench, jars, tubes, crafting and transport devices |
| `Thaumcraft_1.12.2_Inventory_5.png` | Transition into items and curios | Early item order and first resource items |
| `Thaumcraft_1.12.2_Inventory_6.png` | Resource items and crystals | Ingots, nuggets, clusters and crystal-related items |
| `Thaumcraft_1.12.2_Inventory_7.png` | Essentia and phials | Phial and color/variant visual checks |
| `Thaumcraft_1.12.2_Inventory_8.png` | Tools, foci and devices | Thaumometer, goggles, tools and focus items |
| `Thaumcraft_1.12.2_Inventory_9.png` | Armor, baubles and golem/seal area | Armor sets and bauble order |
| `Thaumcraft_1.12.2_Inventory_10.png` | End of Thaumcraft tab | Late baubles, cult gear, seals and final entries |

## Variant preservation rules

| Legacy pattern | NeoForge port option | Creative tab rule |
|---|---|---|
| One item with simple metadata variants | Prefer separate registered items if they behave as separate resources | Add variants in legacy order |
| One item with shared behavior variants | Use one item plus data components or explicit visible stacks | Add visible stacks in legacy order |
| Damage-based visual variant | Use durability, data components or model predicates as appropriate | Match visible creative variants from 1.12.2 |
| Dynamic variants such as seals | Generate display entries after the underlying registry is stable | Blank seal first, then registered seals in legacy order |
| Config-gated variants | Preserve the same config behavior | Keep cheat and debug entries gated |

## Implementation inventory note

The old first-slice list of `amber`, `quicksilver`, and `fabric` is no longer a complete description of implemented content. The port now includes additional block items and simple block identity entries. Do not use this document as the live registry inventory.

Current implemented entries are tracked in `06_docs/current_port_status.md`.

## Future order manifest

The final manifest should be expanded in stages:

1. block items from `ConfigBlocks.initBlocks(...)`;
2. simple scalar items from `ConfigItems.initItems(...)`;
3. simple material variant groups such as ingots, nuggets, clusters and plates;
4. functional items that can start as inert identity entries;
5. NBT/data-component items;
6. tools and equipment;
7. foci and caster items;
8. golem, seal and late-game entries.

Every expansion should be reviewed against the screenshot set before release.

## Review checklist

Before a creative tab order change is accepted:

1. the order class adds entries explicitly;
2. no alphabetical sorting is introduced;
3. no registry class independently appends entries to the tab;
4. implemented entries preserve their relative legacy order;
5. missing legacy entries are documented as gaps, not replaced by release placeholders;
6. the tab builds and opens in-game;
7. the relevant screenshot page is checked when visual parity matters.
