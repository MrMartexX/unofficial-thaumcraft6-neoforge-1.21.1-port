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
| Ores | `ore_amber`, `ore_cinnabar`, `ore_quartz` | Stone-like blocks with `requiresCorrectToolForDrops()` | Hardness/resistance now match legacy source. Drops still need the exact legacy XP and rare amber-curio behavior. |
| Crystals | `crystal_aer`, `crystal_ignis`, `crystal_aqua`, `crystal_terra`, `crystal_ordo`, `crystal_perditio`, `crystal_vitium` | Fragile, no-collision glass-like placeholders with light level 1 | Properties are only the safe identity slice. Exact crystal growth/drop logic is blocked by aspects/aura/crystal item work. |
| Arcane stone blocks | `stone_arcane`, `stone_arcane_brick` | Stone-like blocks | Verify hardness, resistance, tool requirement, and drops |
| Ancient stone blocks | `stone_ancient`, `stone_ancient_tile`, `stone_ancient_rock`, `stone_ancient_glyphed`, `stone_ancient_doorway` | Normal ancient variants are stone-like; rock and doorway currently use unbreakable stone helpers | Verify exact legacy values and whether unbreakable behavior belongs on these ids |
| Eldritch/porous stone | `stone_eldritch_tile`, `stone_porous` | Stone-like blocks; eldritch tile has high resistance | Verify exact values and drops |
| Stone stairs/slabs | `stairs_arcane`, `stairs_arcane_brick`, `stairs_ancient`, `slab_arcane_stone`, `slab_arcane_brick`, `slab_ancient`, `slab_eldritch` | Modern stair/slab blocks with stone helpers | Verify parity with parent block hardness, resistance, and drops |
| Amber blocks | `amber_block`, `amber_brick` | Glass-copy blocks with stone sound and no occlusion | Verify hardness, resistance, tool, transparency, and drops |
| Logs | `log_greatwood`, `log_silverwood` | `TCLogBlock`; Silverwood emits light level 5 | Verify hardness, resistance, axe behavior, drops, and Silverwood light parity |
| Leaves | `leaves_greatwood`, `leaves_silverwood` | `TCLeavesBlock` based on oak leaves | Verify decay behavior, drops, sapling/drop rates, Silk Touch, and shears |
| Saplings | `sapling_greatwood`, `sapling_silverwood` | `TCSaplingBlock` with growth stage, bonemeal support, Greatwood/Silverwood tree generation | Keep as sapling-only tree growth; do not treat this as full worldgen/biome placement |
| Plants | `shimmerleaf`, `cinderpearl`, `vishroom` | `TCPlantBlock`; light and placement behavior started | Verify placement, light, shape, particles, and survival drops |
| Planks | `plank_greatwood`, `plank_silverwood` | Oak-plank-like blocks | Hardness/resistance and axe tag now match the legacy `BlockPlanksTC` identity behavior. |
| Wood stairs/slabs | `stairs_greatwood`, `stairs_silverwood`, `slab_greatwood`, `slab_silverwood` | Modern stair/slab blocks with wood helpers | Hardness/resistance now match legacy source values. |

## Detailed Audit Table

| Registry id | Legacy source checked | Legacy hardness | Legacy resistance | Legacy tool behavior | Legacy survival drop | Current implementation | Action |
|---|---|---:|---:|---|---|---|---|
| `ore_amber` | `ConfigBlocks`, `BlockOreTC` | 1.5 | 5.0 | pickaxe level 1 | 1-2 `amber`; Fortune applies; Silk Touch drops block; 6.6% amber drop replacement with curio meta 1 | `stoneBlock(1.5F, 5.0F)` | Properties aligned; XP/rare curio drop deferred until item/loot support exists. |
| `ore_cinnabar` | `ConfigBlocks`, `BlockOreTC` | 2.0 | 5.0 | pickaxe level 2 | block itself | `stoneBlock(2.0F, 5.0F)` | Properties and basic drop aligned. |
| `ore_quartz` | `ConfigBlocks`, `BlockOreTC` | 3.0 | 5.0 | no explicit legacy harvest level | `minecraft:quartz`; Fortune applies; Silk Touch drops block; XP 1-4 | `stoneBlock(3.0F, 5.0F)` | Properties/drop aligned; XP deferred. |
| `crystal_aer` | `ConfigBlocks`, `BlockCrystal` | 0.25 | default glass/no explicit resistance | none; no collision | `ThaumcraftApiHelper.makeCrystal(Aspect.AIR)`, count = growth + 1; no Silk Touch | `crystalPlaceholder()` | Identity only; exact growth/drop requires aspect crystal item and aura. |
| `crystal_ignis` | `ConfigBlocks`, `BlockCrystal` | 0.25 | default glass/no explicit resistance | none; no collision | `makeCrystal(Aspect.FIRE)`, count = growth + 1; no Silk Touch | `crystalPlaceholder()` | Identity only; exact growth/drop requires aspect crystal item and aura. |
| `crystal_aqua` | `ConfigBlocks`, `BlockCrystal` | 0.25 | default glass/no explicit resistance | none; no collision | `makeCrystal(Aspect.WATER)`, count = growth + 1; no Silk Touch | `crystalPlaceholder()` | Identity only; exact growth/drop requires aspect crystal item and aura. |
| `crystal_terra` | `ConfigBlocks`, `BlockCrystal` | 0.25 | default glass/no explicit resistance | none; no collision | `makeCrystal(Aspect.EARTH)`, count = growth + 1; no Silk Touch | `crystalPlaceholder()` | Identity only; exact growth/drop requires aspect crystal item and aura. |
| `crystal_ordo` | `ConfigBlocks`, `BlockCrystal` | 0.25 | default glass/no explicit resistance | none; no collision | `makeCrystal(Aspect.ORDER)`, count = growth + 1; no Silk Touch | `crystalPlaceholder()` | Identity only; exact growth/drop requires aspect crystal item and aura. |
| `crystal_perditio` | `ConfigBlocks`, `BlockCrystal` | 0.25 | default glass/no explicit resistance | none; no collision | `makeCrystal(Aspect.ENTROPY)`, count = growth + 1; no Silk Touch | `crystalPlaceholder()` | Identity only; exact growth/drop requires aspect crystal item and aura. |
| `crystal_vitium` | `ConfigBlocks`, `BlockCrystal` | 0.25 | default glass/no explicit resistance | none; no collision | `makeCrystal(Aspect.FLUX)`, count = growth + 1; no Silk Touch | `crystalPlaceholder()` | Identity only; exact growth/drop requires aspect crystal item and aura. |
| `stone_arcane` | `ConfigBlocks`, `BlockStoneTC` | 2.0 | 10.0 | stone/pickaxe identity | block itself | `stoneBlock(2.0F, 10.0F)` | Aligned. |
| `stone_arcane_brick` | `ConfigBlocks`, `BlockStoneTC` | 2.0 | 10.0 | stone/pickaxe identity | block itself | `stoneBlock(2.0F, 10.0F)` | Aligned. |
| `stone_ancient` | `ConfigBlocks`, `BlockStoneTC` | 2.0 | 10.0 | stone/pickaxe identity | block itself | `stoneBlock(2.0F, 10.0F)` | Aligned. |
| `stone_ancient_tile` | `ConfigBlocks`, `BlockStoneTC` | 2.0 | 10.0 | stone/pickaxe identity | block itself | `stoneBlock(2.0F, 10.0F)` | Aligned. |
| `stone_ancient_rock` | `ConfigBlocks`, `BlockStoneTC` | -1.0 | 10.0 | unbreakable; `canEntityDestroy` false | none in survival | `unbreakableStoneBlock(10.0F)` | Aligned. |
| `stone_ancient_glyphed` | `ConfigBlocks`, `BlockStoneTC` | 2.0 | 10.0 | stone/pickaxe identity | block itself | `stoneBlock(2.0F, 10.0F)` | Aligned. |
| `stone_ancient_doorway` | `ConfigBlocks`, `BlockStoneTC` | -1.0 | 10.0 | unbreakable; `canEntityDestroy` false | none in survival | `unbreakableStoneBlock(10.0F)` | Aligned. |
| `stone_eldritch_tile` | `ConfigBlocks`, `BlockStoneTC` | 15.0 | 1000.0 | stone/pickaxe identity | block itself | `stoneBlock(15.0F, 1000.0F)` | Aligned. |
| `stone_porous` | `ConfigBlocks`, `BlockStonePorous` | 1.0 | 5.0 | stone/pickaxe identity | mostly gravel; rare weighted drops including essentia crystal, amber, clusters, gems, redstone, clay, quartz | `stoneBlock(1.0F, 5.0F)` | Properties aligned; exact weighted loot deferred until required items/aspects exist. |
| `stairs_arcane` | `ConfigBlocks`, `BlockStairsTC` | inherited from `stone_arcane` | inherited from `stone_arcane` | stone/pickaxe identity | block itself | `stoneStairBlock(..., 2.0F, 10.0F)` | Aligned. |
| `stairs_arcane_brick` | `ConfigBlocks`, `BlockStairsTC` | inherited from `stone_arcane_brick` | inherited from `stone_arcane_brick` | stone/pickaxe identity | block itself | `stoneStairBlock(..., 2.0F, 10.0F)` | Aligned. |
| `stairs_ancient` | `ConfigBlocks`, `BlockStairsTC` | inherited from `stone_ancient` | inherited from `stone_ancient` | stone/pickaxe identity | block itself | `stoneStairBlock(..., 2.0F, 10.0F)` | Aligned. |
| `slab_arcane_stone` | `ConfigBlocks`, `BlockSlabTC` | 2.0 | 10.0 | stone/pickaxe identity | half slab item; double slab drops half slab item | `stoneSlabBlock(2.0F, 10.0F)` | Aligned. |
| `slab_arcane_brick` | `ConfigBlocks`, `BlockSlabTC` | 2.0 | 10.0 | stone/pickaxe identity | half slab item; double slab drops half slab item | `stoneSlabBlock(2.0F, 10.0F)` | Aligned. |
| `slab_ancient` | `ConfigBlocks`, `BlockSlabTC` | 2.0 | 10.0 | stone/pickaxe identity | half slab item; double slab drops half slab item | `stoneSlabBlock(2.0F, 10.0F)` | Aligned. |
| `slab_eldritch` | `ConfigBlocks`, `BlockSlabTC` | 2.0 | 10.0 | stone/pickaxe identity | half slab item; double slab drops half slab item | `stoneSlabBlock(2.0F, 10.0F)` | Aligned; note legacy slab is weaker than `stone_eldritch_tile`. |
| `amber_block` | `ConfigBlocks`, `BlockTranslucent` | 0.5 | inherited `BlockTC` 2.0 | `canHarvestBlock` true | block itself | `amberBlock()` | Aligned. |
| `amber_brick` | `ConfigBlocks`, `BlockTranslucent` | 0.5 | inherited `BlockTC` 2.0 | `canHarvestBlock` true | block itself | `amberBlock()` | Aligned. |
| `log_greatwood` | `ConfigBlocks`, `BlockLogsTC` | 2.0 | 5.0 | axe level 0; flammable 5/spread 5 | block itself; Silk Touch block | `logBlock(false)` | Aligned. |
| `log_silverwood` | `ConfigBlocks`, `BlockLogsTC` | 2.0 | 5.0 | axe level 0; flammable 5/spread 5 | block itself; Silk Touch block | `logBlock(true)` | Aligned, including light level 5. |
| `leaves_greatwood` | `ConfigBlocks`, `BlockLeavesTC` | vanilla leaves | vanilla leaves | shears/Silk Touch drop leaves | sapling chance 1/75 before Fortune; shears/Silk Touch drop leaves | `leavesBlock()` + loot table | Decay/drop basics aligned; Silverwood aura behavior not relevant. |
| `leaves_silverwood` | `ConfigBlocks`, `BlockLeavesTC` | vanilla leaves | vanilla leaves | shears/Silk Touch drop leaves | silverwood sapling chance 1/75; rare quicksilver nugget via `dropApple`; shears/Silk Touch drop leaves | `leavesBlock()` + loot table | Sapling/shears aligned; quicksilver nugget approximation must be revisited when nugget variants exist. |
| `sapling_greatwood` | `ConfigBlocks`, `BlockSaplingTC` | bush default | bush default | plant; bonemeal success 25% | block item | `TCSaplingBlock.Kind.GREATWOOD` | Growth stage/bonemeal/2x2 generation aligned for sapling use. |
| `sapling_silverwood` | `ConfigBlocks`, `BlockSaplingTC` | bush default | bush default | plant; bonemeal success 25% | block item | `TCSaplingBlock.Kind.SILVERWOOD` | Growth stage/bonemeal/single-tree generation aligned for sapling use. |
| `shimmerleaf` | `ConfigBlocks`, `BlockPlantShimmerleaf` | bush default | bush default | survives on grass or dirt; offset XZ; light 0.4 => 6 | block item | `TCPlantBlock.Kind.SHIMMERLEAF` | Aligned for light/particles/survival. |
| `cinderpearl` | `ConfigBlocks`, `BlockPlantCinderpearl` | bush default | bush default | survives on sand, dirt, hardened clay, stained hardened clay; offset XZ; light 0.5 => 7 | block item | `TCPlantBlock.Kind.CINDERPEARL` | Aligned for light/particles/survival with modern terracotta mapping. |
| `vishroom` | `ConfigBlocks`, `BlockPlantVishroom` | bush default | bush default | cave plant type; nausea 200 ticks, 20% collision chance; light 0.4 => 6 | block item | `TCPlantBlock.Kind.VISHROOM` | Collision/light/particles aligned; survival substrate is broader than legacy and should be reviewed before world placement. |
| `plank_greatwood` | `ConfigBlocks`, `BlockPlanksTC` | 2.0 | inherited `BlockTC` 2.0 | axe level 0; flammable 20/spread 5 | block itself | `woodBlock()` | Aligned. |
| `plank_silverwood` | `ConfigBlocks`, `BlockPlanksTC` | 2.0 | inherited `BlockTC` 2.0 | axe level 0; flammable 20/spread 5 | block itself | `woodBlock()` | Aligned. |
| `stairs_greatwood` | `ConfigBlocks`, `BlockStairsTC` | inherited from `plank_greatwood` | inherited from `plank_greatwood` | wood/axe identity; flammable 20/spread 5 | block itself | `woodStairBlock(...)` | Aligned. |
| `stairs_silverwood` | `ConfigBlocks`, `BlockStairsTC` | inherited from `plank_silverwood` | inherited from `plank_silverwood` | wood/axe identity; flammable 20/spread 5 | block itself | `woodStairBlock(...)` | Aligned. |
| `slab_greatwood` | `ConfigBlocks`, `BlockSlabTC` | 1.2 | 2.0 | wood/axe identity; flammable 20/spread 5 | half slab item; double slab drops half slab item | `woodSlabBlock(1.2F, 2.0F)` | Aligned. |
| `slab_silverwood` | `ConfigBlocks`, `BlockSlabTC` | 1.0 | 2.0 | wood/axe identity; flammable 20/spread 5 | half slab item; double slab drops half slab item | `woodSlabBlock(1.0F, 2.0F)` | Aligned. |

## Registered Simple Item Audit

| Registry id | Legacy source checked | Legacy behavior | Current implementation | Action |
|---|---|---|---|---|
| `amber` | `ConfigItems`, `ItemTCBase` | Simple no-repair item, no subtypes | `new Item(new Item.Properties())` | Identity aligned; no behavior required yet. |
| `quicksilver` | `ConfigItems`, `ItemTCBase` | Simple no-repair item, no subtypes | `new Item(new Item.Properties())` | Identity aligned; later verify nugget/meta split before silverwood leaf rare drop. |
| `fabric` | `ConfigItems`, `ItemTCBase` | Simple no-repair item, no subtypes | `new Item(new Item.Properties())` | Identity aligned; no behavior required yet. |
| `goggles` | `ConfigItems`, `ItemGoggles` | Armor head slot, max damage 350, rare, vis discount 5%, reveals nodes/popups, Baubles head render, thaumium repair | plain item identity only | Not aligned by design; wearable/accessory behavior is Gate 8/13-level work and needs its own design note. |

## Known Findings

- Simple active block hardness/resistance/light values are now checked against `ConfigBlocks` and the relevant legacy block classes.
- `ore_amber`, `ore_quartz`, `stone_porous`, crystals, and silverwood leaf rare drops still need exact loot behavior once the required item/aspect systems exist. Do not replace those with approximate loot.
- `goggles` is currently identity-only. Do not implement its behavior as a plain item hack; it needs the later armor/accessory/revealer design.
- Sapling growth and bonemeal behavior exist now. Biome/world placement, configured features, and structure/world generators are still separate worldgen work.
- Imported legacy asset paths are intentionally mixed with modern active paths. Fix active warnings only; defer unregistered legacy model/resource adaptation until each content slice is ported.
