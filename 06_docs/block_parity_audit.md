# Thaumcraft 6 Block Parity Audit

This document tracks mining speed, required tools, resistance, survival drops, and loot status for blocks already added to the NeoForge port.

Do not treat this file as final parity data yet. It is a working audit file. Legacy values must be checked against the 1.12.2 source and original jar behavior before changing code.

## Rules

1. Do not change hardness, resistance, tool requirements, or drops by guesswork.
2. Check the legacy class or `ConfigBlocks` source before changing behavior.
3. Check survival drops separately from creative visibility.
4. Check whether Silk Touch, Fortune, or no-drop behavior existed.
5. Keep block identity work separate from BlockEntity, worldgen, aura, research, and recipe systems.
6. Every parity patch must build after changes.

## Current implementation groups

| Group | Registry ids | Current status | Next action |
|---|---|---|---|
| Ores | `ore_amber`, `ore_cinnabar`, `ore_quartz` | Implemented as stone-like blocks with `requiresCorrectToolForDrops()` | Verify hardness, resistance, required tool, harvest level equivalent, drops, Silk Touch, Fortune |
| Crystals | `crystal_aer`, `crystal_ignis`, `crystal_aqua`, `crystal_terra`, `crystal_ordo`, `crystal_perditio`, `crystal_vitium` | Implemented as glass-like placeholders | Verify if they should be fragile, non-colliding, silk-touch-sensitive, or drop crystal items |
| Arcane stone blocks | `stone_arcane`, `stone_arcane_brick` | Implemented as stone-like blocks | Verify hardness, resistance, tool requirement, drops |
| Ancient stone blocks | `stone_ancient`, `stone_ancient_tile`, `stone_ancient_rock`, `stone_ancient_glyphed`, `stone_ancient_doorway` | Implemented as stone-like blocks, some very high resistance | Verify exact legacy values and whether high-resistance variants are correct |
| Eldritch/porous stone | `stone_eldritch_tile`, `stone_porous` | Implemented as stone-like blocks | Verify exact legacy values and drops |
| Stone stairs/slabs | `stairs_arcane`, `stairs_arcane_brick`, `stairs_ancient`, `slab_arcane_stone`, `slab_arcane_brick`, `slab_ancient`, `slab_eldritch` | Implemented with stone stair/slab helpers | Verify parity with parent block hardness, resistance, drops |
| Amber blocks | `amber_block`, `amber_brick` | Implemented as glass-like blocks with stone sound | Verify hardness, resistance, tool, transparency, drops |
| Logs | `log_greatwood`, `log_silverwood` | Implemented as oak-log-like blocks | Verify hardness, resistance, axe behavior, drops, Silverwood light |
| Leaves | `leaves_greatwood`, `leaves_silverwood` | Implemented as oak-leaves-like blocks | Verify decay behavior, drops, sapling/drop rates, Silk Touch, shears |
| Saplings | `sapling_greatwood`, `sapling_silverwood` | Implemented as temporary plant blocks | Real sapling growth and tree generation belong to Patch B |
| Plants | `shimmerleaf`, `cinderpearl`, `vishroom` | Implemented through `TCPlantBlock` | Verify placement, light, shape, particles, survival drops |
| Planks | `plank_greatwood`, `plank_silverwood` | Implemented as oak-plank-like blocks | Verify hardness, resistance, axe behavior, drops |
| Wood stairs/slabs | `stairs_greatwood`, `stairs_silverwood`, `slab_greatwood`, `slab_silverwood` | Implemented with wood stair/slab helpers | Verify parity with parent planks and drops |

## Detailed audit table

| Registry id | Legacy source to check | Legacy hardness | Legacy resistance | Legacy tool behavior | Legacy survival drop | Current implementation | Action |
|---|---|---:|---:|---|---|---|---|
| `ore_amber` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(1.5F, 3.0F)` | Verify |
| `ore_cinnabar` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(2.0F, 3.0F)` | Verify |
| `ore_quartz` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(3.0F, 3.0F)` | Verify |
| `crystal_aer` | TODO | TODO | TODO | TODO | TODO | `crystalPlaceholder()` | Verify |
| `crystal_ignis` | TODO | TODO | TODO | TODO | TODO | `crystalPlaceholder()` | Verify |
| `crystal_aqua` | TODO | TODO | TODO | TODO | TODO | `crystalPlaceholder()` | Verify |
| `crystal_tERRA_note` | TODO | TODO | TODO | TODO | TODO | Replace this row with `crystal_terra` after audit | Fix audit row |
| `crystal_ordo` | TODO | TODO | TODO | TODO | TODO | `crystalPlaceholder()` | Verify |
| `crystal_perditio` | TODO | TODO | TODO | TODO | TODO | `crystalPlaceholder()` | Verify |
| `crystal_vitium` | TODO | TODO | TODO | TODO | TODO | `crystalPlaceholder()` | Verify |
| `stone_arcane` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(2.0F, 10.0F)` | Verify |
| `stone_arcane_brick` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(2.0F, 10.0F)` | Verify |
| `stone_ancient` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(2.0F, 10.0F)` | Verify |
| `stone_ancient_tile` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(2.0F, 10.0F)` | Verify |
| `stone_ancient_rock` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(50.0F, 1200.0F)` | Verify |
| `stone_ancient_glyphed` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(2.0F, 10.0F)` | Verify |
| `stone_ancient_doorway` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(50.0F, 1200.0F)` | Verify |
| `stone_eldritch_tile` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(15.0F, 1000.0F)` | Verify |
| `stone_porous` | TODO | TODO | TODO | TODO | TODO | `stoneBlock(1.5F, 6.0F)` | Verify |
| `amber_block` | TODO | TODO | TODO | TODO | TODO | `amberBlock()` | Verify |
| `amber_brick` | TODO | TODO | TODO | TODO | TODO | `amberBlock()` | Verify |
| `log_greatwood` | TODO | TODO | TODO | TODO | TODO | `logBlock(false)` | Verify |
| `log_silverwood` | TODO | TODO | TODO | TODO | TODO | `logBlock(true)` | Verify |
| `leaves_greatwood` | TODO | TODO | TODO | TODO | TODO | `leavesBlock()` | Verify |
| `leaves_silverwood` | TODO | TODO | TODO | TODO | TODO | `leavesBlock()` | Verify |
| `sapling_greatwood` | TODO | TODO | TODO | TODO | TODO | temporary `TCPlantBlock.Kind.SAPLING` | Patch B |
| `sapling_silverwood` | TODO | TODO | TODO | TODO | TODO | temporary `TCPlantBlock.Kind.SAPLING` | Patch B |
| `shimmerleaf` | TODO | TODO | TODO | TODO | TODO | `TCPlantBlock.Kind.SHIMMERLEAF` | Verify |
| `cinderpearl` | TODO | TODO | TODO | TODO | TODO | `TCPlantBlock.Kind.CINDERPEARL` | Verify |
| `vishroom` | TODO | TODO | TODO | TODO | TODO | `TCPlantBlock.Kind.VISHROOM` | Verify after placement fix |
| `plank_greatwood` | TODO | TODO | TODO | TODO | TODO | `woodBlock()` | Verify |
| `plank_silverwood` | TODO | TODO | TODO | TODO | TODO | `woodBlock()` | Verify |

## Known immediate findings

- `Vishroom` placement was too narrow after Patch A. It allowed stone-like and cave-like blocks but did not allow grass/dirt-like blocks. Patch A2.1 restores grass/dirt-like placement.
- `TCBlocks.java` had a duplicated `TCPlantBlock` import after patching. Patch A2.1 removes the duplicate.
- Saplings are still temporary plant blocks. Real growth, bonemeal, stage property, and Greatwood/Silverwood tree generation belong to Patch B.
- Shimmerleaf and Vishroom still use temporary dust particles. Real Thaumcraft wispy particles need a separate client FX patch.