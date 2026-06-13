# Shobie 1.20.1 Tag Compatibility Import

Date: 2026-06-13
Branch: `codex/experiment-shobie-1-20-merge`

## Scope

This pass imports the useful part of Shobie's Thaumcraft tag taxonomy as a
NeoForge 1.21.1 compatibility layer, but keeps the current port's 1.12-style
registry ids authoritative.

The original Shobie paths use the older plural folders:

- `data/thaumcraft/tags/blocks`
- `data/thaumcraft/tags/items`

The port writes modern paths:

- `data/thaumcraft/tags/block`
- `data/thaumcraft/tags/item`

Migration guide rule applied: translate data roles to modern resource
conventions and current registry ids. Do not copy tag files that reference
unregistered ids.

## Imported Block Tags

| Tag | Current values |
|---|---|
| `thaumcraft:ancient_stone` | `stone_ancient`, `stone_ancient_tile`, `stone_ancient_rock`, `stone_ancient_glyphed`, `stone_ancient_doorway`, `slab_ancient`, `stairs_ancient` |
| `thaumcraft:arcane_stone` | `stone_arcane`, `stone_arcane_brick`, `slab_arcane_stone`, `slab_arcane_brick`, `stairs_arcane`, `stairs_arcane_brick` |
| `thaumcraft:crystals` | `crystal_aer`, `crystal_ignis`, `crystal_aqua`, `crystal_terra`, `crystal_ordo`, `crystal_perditio`, `crystal_vitium` |
| `thaumcraft:jars` | `jar_normal`, `jar_void` |
| `thaumcraft:lamps` | `lamp_arcane` |
| `thaumcraft:leaves` | `leaves_greatwood`, `leaves_silverwood` |
| `thaumcraft:logs` | `log_greatwood`, `log_silverwood` |
| `thaumcraft:metal_blocks` | `metal_brass`, `metal_thaumium`, `metal_void`, `metal_alchemical`, `metal_alchemical_advanced` |
| `thaumcraft:nitor` | all 16 currently registered nitor colors |
| `thaumcraft:ores` | `ore_amber`, `ore_cinnabar`, `ore_quartz` |
| `thaumcraft:pedestals` | `pedestal_arcane`, `pedestal_ancient`, `pedestal_eldritch` |
| `thaumcraft:planks` | `plank_greatwood`, `plank_silverwood` |
| `thaumcraft:saplings` | `sapling_greatwood`, `sapling_silverwood` |
| `thaumcraft:tubes` | `tube_normal`, `tube_restrict`, `tube_filter`, `tube_valve`, `tube_buffer`, `tube_oneway` |

## Imported Item Tags

| Tag | Current values |
|---|---|
| `thaumcraft:ingots` | `thaumium_ingot`, `void_metal_ingot`, `brass_ingot` |
| `thaumcraft:nuggets` | `thaumium_nugget`, `void_metal_nugget`, `brass_nugget`, `quicksilver_nugget`, `quartz_nugget` |
| `thaumcraft:plates` | `iron_plate`, `brass_plate`, `thaumium_plate`, `void_plate` |
| `thaumcraft:tools/axes` | `thaumium_axe`, `void_axe` |
| `thaumcraft:tools/hoes` | `thaumium_hoe`, `void_hoe` |
| `thaumcraft:tools/pickaxes` | `thaumium_pick`, `void_pick` |
| `thaumcraft:tools/shovels` | `thaumium_shovel`, `void_shovel` |
| `thaumcraft:tools/swords` | `thaumium_sword`, `void_sword` |

## Deliberately Deferred

- `banner_*` blocks: not registered.
- Colored candle blocks other than the current item-only `candle_white`: not registered.
- Deepslate Thaumcraft ores: not registered.
- `cluster_*` items: not registered.
- `shard_*` items: current port uses explicit crystal/block and crystal essence ids; shard identity still needs a focused legacy variant decision.
- `elemental_*`, `crimson_blade`, and other late-game tools: not registered.
- Worldgen biome tags: not imported until the worldgen/biome layer is designed.

## Vanilla Tag Closure

Shobie includes explicit smelting recipes for Greatwood/Silverwood logs to
charcoal. In the NeoForge 1.21.1 port this is handled through the vanilla
integration path instead:

| Vanilla tag | Current values | Reason |
|---|---|---|
| `minecraft:logs_that_burn` block/item | `log_greatwood`, `log_silverwood` | Lets vanilla log-to-charcoal and burnable-log behavior apply without adding duplicate Thaumcraft smelting recipes. |
| `minecraft:planks` block/item | `plank_greatwood`, `plank_silverwood` | Preserves the vanilla wood-family tag role for block and item lookups. |
| `minecraft:wooden_slabs` block/item | `slab_greatwood`, `slab_silverwood` | Preserves the vanilla wood-family tag role for block and item lookups. |
| `minecraft:wooden_stairs` block/item | `stairs_greatwood`, `stairs_silverwood` | Preserves the vanilla wood-family tag role for block and item lookups. |

## Common Tag Closure

Shobie still used Forge `forge:` tags. The NeoForge 1.21.1 port translates the
safe registered-material subset into common `c:` tags and keeps current
legacy-style ids authoritative.

| Common tag | Current values | Reason |
|---|---|---|
| `c:storage_blocks/brass` block/item | `metal_brass` | Safe translation of Shobie `forge:storage_blocks/brass`. |
| `c:storage_blocks/thaumium` block/item | `metal_thaumium` | Safe translation of Shobie `forge:storage_blocks/thaumium`. |
| `c:storage_blocks/void` block/item | `metal_void` | Current port's existing short void-metal common tag form. |
| `c:storage_blocks/void_metal` block/item | `metal_void` | Compatibility alias for Shobie `forge:storage_blocks/void_metal`. |
| `c:ingots/void_metal` item | `void_metal_ingot` | Compatibility alias for Shobie `forge:ingots/void_metal`; `c:ingots/void` remains available. |
| `c:nuggets/void_metal` item | `void_metal_nugget` | Compatibility alias for Shobie `forge:nuggets/void_metal`; `c:nuggets/void` remains available. |
| root `c:ingots`, `c:nuggets`, `c:ores` | includes void/void_metal and quartz sub-tags where applicable | Keeps common root tags complete for recipes, generated aspects and future Shobie recipe translation. |

## Validation

| Check | Result | Notes |
|---|---|---|
| `.\gradlew.bat build --no-daemon` | Passed | Resource processing accepted all generated tag files. |
| `.\tools\ci\server-smoke.ps1 -TimeoutSeconds 420` | Passed | Dedicated server reached `Done`, loaded `1479` recipes, rebuilt `581` generated object assignments, and passed aspect tag reload validation. No tag/registry parse errors were found in the smoke log. |
