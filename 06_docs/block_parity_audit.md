# Thaumcraft 6 Block Parity Audit

Last reviewed branch: `main`
Last reviewed base commit: `70ec2f06ff06d53f7119f7db9adb83b792368874`
Target module: `05_neoforge_port`

This document tracks mining speed, tool requirements, resistance, survival drops, loot, and active resources for blocks already added to the NeoForge port.

Do not treat this file as final parity data yet. Legacy values must be checked against the 1.12.2 source and original jar behavior before changing code.

## Rules

1. Do not change hardness, resistance, tool requirements, or drops by guesswork.
2. Check the legacy class or `ConfigBlocks` source before changing behavior.
3. Check survival drops separately from creative visibility.
4. Check whether Silk Touch, Fortune, or no-drop behavior existed.
5. Keep block identity work separate from BlockEntity, biome/worldgen, aura, research, and recipe systems.
6. Every parity patch must build after changes.
7. Active 1.21 resources are authoritative for registered content: `textures/block`, `textures/item`, `models/block`, `models/item`, modern `blockstates`, modern data loot tables, and tags.
8. Legacy `textures/blocks`, old `.lang`, `assets/thaumcraft/loot_tables`, shaders, research assets, OBJ/MTL files, and unregistered legacy models are reference/base assets until their subsystem is ported.

## Current Resource Coverage

| Surface | Current status | Notes |
|---|---|---|
| Registered blocks | 43 ids in `TCBlocks` | Checked against active blockstates, block models, loot tables, and `en_us.json`. |
| Registered item/block items | 47 ids in `TCItems` | Includes 43 block items plus `goggles`, `amber`, `quicksilver`, and `fabric`. |
| Active item models | Covered | `amber`, `quicksilver`, and `fabric` were fixed from legacy `thaumcraft:items/*` texture references to modern `thaumcraft:item/*`, with active PNGs copied into `textures/item`. |
| Active translations | Covered | No extra legacy `.lang` entries were needed for currently registered ids. |
| Legacy imported assets | Reference/base | Do not mass-convert old paths just because the imported corpus contains them. |

## Current Implementation Groups

| Group | Registry ids | Current status | Next action |
|---|---|---|---|
| Ores | `ore_amber`, `ore_cinnabar`, `ore_quartz` | Stone-like blocks with `requiresCorrectToolForDrops()` | Verify hardness, resistance, required tool, harvest level equivalent, drops, Silk Touch, and Fortune |
| Crystals | `crystal_aer`, `crystal_ignis`, `crystal_aqua`, `crystal_terra`, `crystal_ordo`, `crystal_perditio`, `crystal_vitium` | Fragile, no-collision glass-like placeholders with light level 1 | Verify shape, placement, drops, Silk Touch behavior, and aspect crystal parity |
| Arcane stone blocks | `stone_arcane`, `stone_arcane_brick` | Stone-like blocks | Verify hardness, resistance, tool requirement, and drops |
| Ancient stone blocks | `stone_ancient`, `stone_ancient_tile`, `stone_ancient_rock`, `stone_ancient_glyphed`, `stone_ancient_doorway` | Normal ancient variants are stone-like; rock and doorway currently use unbreakable stone helpers | Verify exact legacy values and whether unbreakable behavior belongs on these ids |
| Eldritch/porous stone | `stone_eldritch_tile`, `stone_porous` | Stone-like blocks; eldritch tile has high resistance | Verify exact values and drops |
| Stone stairs/slabs | `stairs_arcane`, `stairs_arcane_brick`, `stairs_ancient`, `slab_arcane_stone`, `slab_arcane_brick`, `slab_ancient`, `slab_eldritch` | Modern stair/slab blocks with stone helpers | Verify parity with parent block hardness, resistance, and drops |
| Amber blocks | `amber_block`, `amber_brick` | Glass-copy blocks with stone sound and no occlusion | Verify hardness, resistance, tool, transparency, and drops |
| Logs | `log_greatwood`, `log_silverwood` | `TCLogBlock`; Silverwood emits light level 5 | Verify hardness, resistance, axe behavior, drops, and Silverwood light parity |
| Leaves | `leaves_greatwood`, `leaves_silverwood` | `TCLeavesBlock` based on oak leaves | Verify decay behavior, drops, sapling/drop rates, Silk Touch, and shears |
| Saplings | `sapling_greatwood`, `sapling_silverwood` | `TCSaplingBlock` with growth stage, bonemeal support, Greatwood/Silverwood tree generation | Keep as sapling-only tree growth; do not treat this as full worldgen/biome placement |
| Plants | `shimmerleaf`, `cinderpearl`, `vishroom` | `TCPlantBlock`; light and placement behavior started | Verify placement, light, shape, particles, and survival drops |
| Planks | `plank_greatwood`, `plank_silverwood` | Oak-plank-like blocks | Verify hardness, resistance, axe behavior, and drops |
| Wood stairs/slabs | `stairs_greatwood`, `stairs_silverwood`, `slab_greatwood`, `slab_silverwood` | Modern stair/slab blocks with wood helpers | Verify parity with parent planks and drops |

## Detailed Audit Table

| Registry id | Legacy source to check | Legacy hardness | Legacy resistance | Legacy tool behavior | Legacy survival drop | Current implementation | Action |
|---|---|---:|---:|---|---|---|---|
| `ore_amber` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(1.5F, 3.0F)` | Verify |
| `ore_cinnabar` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(2.0F, 3.0F)` | Verify |
| `ore_quartz` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(3.0F, 3.0F)` | Verify |
| `crystal_aer` | TODO | TODO | TODO | TODO | TODO | `crystalPlaceholder()` | Verify |
| `crystal_ignis` | TODO | TODO | TODO | TODO | TODO | `crystalPlaceholder()` | Verify |
| `crystal_aqua` | TODO | TODO | TODO | TODO | TODO | `crystalPlaceholder()` | Verify |
| `crystal_terra` | TODO | TODO | TODO | TODO | TODO | `crystalPlaceholder()` | Verify |
| `crystal_ordo` | TODO | TODO | TODO | TODO | TODO | `crystalPlaceholder()` | Verify |
| `crystal_perditio` | TODO | TODO | TODO | TODO | TODO | `crystalPlaceholder()` | Verify |
| `crystal_vitium` | TODO | TODO | TODO | TODO | TODO | `crystalPlaceholder()` | Verify |
| `stone_arcane` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(2.0F, 10.0F)` | Verify |
| `stone_arcane_brick` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(2.0F, 10.0F)` | Verify |
| `stone_ancient` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(2.0F, 10.0F)` | Verify |
| `stone_ancient_tile` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(2.0F, 10.0F)` | Verify |
| `stone_ancient_rock` | TODO | TODO | TODO | TODO | TODO | `unbreakableStoneBlock(1200.0F)` | Verify |
| `stone_ancient_glyphed` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(2.0F, 10.0F)` | Verify |
| `stone_ancient_doorway` | TODO | TODO | TODO | TODO | TODO | `unbreakableStoneBlock(1200.0F)` | Verify |
| `stone_eldritch_tile` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(15.0F, 1000.0F)` | Verify |
| `stone_porous` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(1.0F, 5.0F)` | Verify |
| `amber_block` | TODO | TODO | TODO | TODO | TODO | `amberBlock()` | Verify |
| `amber_brick` | TODO | TODO | TODO | TODO | TODO | `amberBlock()` | Verify |
| `log_greatwood` | TODO | TODO | TODO | TODO | TODO | `logBlock(false)` | Verify |
| `log_silverwood` | TODO | TODO | TODO | TODO | TODO | `logBlock(true)` | Verify |
| `leaves_greatwood` | TODO | TODO | TODO | TODO | TODO | `leavesBlock()` | Verify |
| `leaves_silverwood` | TODO | TODO | TODO | TODO | TODO | `leavesBlock()` | Verify |
| `sapling_greatwood` | TODO | TODO | TODO | TODO | TODO | `TCSaplingBlock.Kind.GREATWOOD` | Verify sapling growth and tree shape |
| `sapling_silverwood` | TODO | TODO | TODO | TODO | TODO | `TCSaplingBlock.Kind.SILVERWOOD` | Verify sapling growth and tree shape |
| `shimmerleaf` | TODO | TODO | TODO | TODO | TODO | `TCPlantBlock.Kind.SHIMMERLEAF` | Verify |
| `cinderpearl` | TODO | TODO | TODO | TODO | TODO | `TCPlantBlock.Kind.CINDERPEARL` | Verify |
| `vishroom` | TODO | TODO | TODO | TODO | TODO | `TCPlantBlock.Kind.VISHROOM` | Verify |
| `plank_greatwood` | TODO | TODO | TODO | TODO | TODO | `woodBlock()` | Verify |
| `plank_silverwood` | TODO | TODO | TODO | TODO | TODO | `woodBlock()` | Verify |

## Known Findings

- Exact legacy hardness, resistance, harvest behavior, Silk Touch/Fortune behavior, and survival drops are still not fully audited. Do not tune values without checking legacy source and jar behavior.
- `stone_ancient_rock` and `stone_ancient_doorway` currently use `unbreakableStoneBlock(1200.0F)`. This is intentional current code state, not final parity proof.
- `stone_porous` currently uses `stoneBlock(1.0F, 5.0F)`. The previous audit value was stale.
- Sapling growth and bonemeal behavior exist now. Biome/world placement, configured features, and structure/world generators are still separate worldgen work.
- Imported legacy asset paths are intentionally mixed with modern active paths. Fix active warnings only; defer unregistered legacy model/resource adaptation until each content slice is ported.
