# Thaumcraft 6 Aspect Assignment Tag Audit

Last reviewed branch: `main`
Target module: `05_neoforge_port`
Legacy references:

- `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/api/OreDictionaryEntries.java`
- `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/config/ConfigAspects.java`
- `06_docs/NeoForge_legacy_migration_guide_expanded_v3.docx`
- `06_docs/aspects_design.md`
- `06_docs/aspect_generate_tags_audit.md`
- `06_docs/aspect_assignment_data_format.md`
- `06_docs/vanilla_aspect_policy.md`

## Purpose

This document audits the legacy `OreDictionary` keys that affect aspect assignment and maps them to safe NeoForge 1.21.1 tag targets.

Guide rule applied: legacy `OreDictionary` is not a registry in the port. It becomes data-driven tag membership, usually in the `c` namespace, with exact id assignments kept authoritative when the legacy source registered a specific Thaumcraft item/block directly.

No gameplay behavior is implemented by this document. It is the blocker-clearing audit for the current tag-backed aspect lookup and remains a hard boundary before `generateTags`, scanning, research, recipes, or essentia systems are connected.

## Current Port State

| Area | Status |
|---|---|
| Direct aspect assignments | `TCAspectAssignments` loads 676 exact assignments from bundled aspect assignment data: current registered Thaumcraft ids, legacy vanilla seeds, modern exact audit entries, audited 1.21-only vanilla manual entries, dump-derived runtime parity overrides for legacy-equivalent plain vanilla stacks, and dump-derived current registered Thaumcraft parity values. Spawn eggs, firework star/rocket, and infested blocks are excluded for 1.12 parity. |
| Parity guard | `TCAspectParityValidator` validates aspect definitions, `AspectList`, helper algorithms, and direct assignments at bootstrap. |
| Current tag resources | Only vanilla-style `minecraft` item/block tags exist for logs, leaves, saplings, planks, stairs/slabs, and mining requirements. |
| Current common `c` tags in port resources | Authored for audited ore/gem/material memberships: amber/cinnabar/quartz, vanilla ores/gems/ingots/dusts, and ore-derived raw materials. NeoForge provides common tags too, but the port authors the needed memberships so validation is deterministic. |
| Current legacy compatibility tags in port resources | Authored under `thaumcraft:legacy_ore_dictionary/*` for every 1.12 `OreDictionaryEntries` key whose target item/block is already registered in the port. These preserve exact legacy key names without inventing questionable `c:` names. |
| Tag-backed aspect lookup | Implemented from `aspect_assignments` data for audited common item tags and legacy compatibility item tags that correspond to `ConfigAspects` string-key assignments, with base priority `exact id > tag > generated`. Stack-sensitive exclusions/specials/bonuses wrap the base lookup. |
| Reload validation | `TCAspectReloadValidator` validates authored common and legacy item/block tag memberships after `TagsUpdatedEvent.SERVER_DATA_LOAD`, verifies representative tag fallbacks, rebuilds generated crafting aspects, and fails if any assignable current `minecraft:*` item id lacks aspects. |

## Mapping Rules

| Rule | Decision |
|---|---|
| Lookup priority | Exact item/block id assignment must win over tag assignment. Legacy direct `ItemStack` registrations in `ConfigAspects` were more specific than broad ore dictionary keys. |
| Unknown/generated aspects | Do not infer. If legacy used recipe generation or `ThaumcraftApi.internalMethods.generateTags`, leave it unavailable until that exact algorithm is ported. |
| `OreDictionary` string keys | Map only to tags when the target tag name is explicit and stable enough to validate. |
| Legacy wildcard metadata | Map only after the flattened 1.21 id set exists. Do not use a single broad tag to hide missing variants. |
| `c` namespace | Use for common material categories. If no established `c` convention exists, use `thaumcraft:legacy_ore_dictionary/*` only when preserving an exact 1.12 key for already registered content. |
| `minecraft` namespace | Use for vanilla behavior groups that already exist in the port, such as `minecraft:logs`, `minecraft:leaves`, and `minecraft:saplings`. |
| Validation | Any tag-backed aspect lookup must extend parity validation with expected tag ids, expected member ids, and exact amounts. |

## Registered Thaumcraft Keys

These legacy ore dictionary keys are relevant to ids already present in `TCItems`/`TCBlocks`.

| Legacy key | Legacy registration source | Legacy aspect source | Current ids | Target tag decision | Status | Notes |
|---|---|---|---|---|---|---|
| `oreAmber` | `OreDictionaryEntries` registers `BlocksTC.oreAmber` | `ConfigAspects` line 481: `EARTH 5`, `TRAP 10`, `CRYSTAL 10` | `thaumcraft:ore_amber` | `c:ores/amber` plus `thaumcraft:legacy_ore_dictionary/ore_amber` for item and block membership | Tag resources and tag lookup authored | Keep direct id assignment authoritative. `c:ores` aggregate includes `#c:ores/amber`. |
| `oreCinnabar` | `OreDictionaryEntries` registers `BlocksTC.oreCinnabar` | `ConfigAspects` line 480: `EARTH 5`, `METAL 10`, `ALCHEMY 5`, `DEATH 5` | `thaumcraft:ore_cinnabar` | `c:ores/cinnabar` plus `thaumcraft:legacy_ore_dictionary/ore_cinnabar` for item and block membership | Tag resources and tag lookup authored | No vanilla/NeoForge built-in cinnabar tag existed in the artifact, so the port defines it. `c:ores` aggregate includes `#c:ores/cinnabar`. |
| `oreQuartz` | `OreDictionaryEntries` registers `BlocksTC.oreQuartz` | `ConfigAspects` line 138: `EARTH 5`, `CRYSTAL 10` | `thaumcraft:ore_quartz` | `c:ores/quartz` plus `thaumcraft:legacy_ore_dictionary/ore_quartz` for item and block membership | Tag resources and tag lookup authored | This extends the existing NeoForge common tag with the TC ore and preserves the legacy key. |
| `oreCrystalAir` | `OreDictionaryEntries` registers `BlocksTC.crystalAir` wildcard | Direct `ItemStack` assignment line 485: `AIR 15`, `CRYSTAL 10` | `thaumcraft:crystal_aer` | `thaumcraft:legacy_ore_dictionary/ore_crystal_air` for item and block membership | Legacy tag resource authored; direct aspect only | Legacy aspect source is direct id, not a string key. No standard `c` tag is established for TC crystal aspects. |
| `oreCrystalEarth` | `OreDictionaryEntries` registers `BlocksTC.crystalEarth` wildcard | Direct `ItemStack` assignment line 488: `EARTH 15`, `CRYSTAL 10` | `thaumcraft:crystal_terra` | `thaumcraft:legacy_ore_dictionary/ore_crystal_earth` for item and block membership | Legacy tag resource authored; direct aspect only | Preserve direct assignment. |
| `oreCrystalWater` | `OreDictionaryEntries` registers `BlocksTC.crystalWater` wildcard | Direct `ItemStack` assignment line 487: `WATER 15`, `CRYSTAL 10` | `thaumcraft:crystal_aqua` | `thaumcraft:legacy_ore_dictionary/ore_crystal_water` for item and block membership | Legacy tag resource authored; direct aspect only | Preserve direct assignment. |
| `oreCrystalFire` | `OreDictionaryEntries` registers `BlocksTC.crystalFire` wildcard | Direct `ItemStack` assignment line 486: `FIRE 15`, `CRYSTAL 10` | `thaumcraft:crystal_ignis` | `thaumcraft:legacy_ore_dictionary/ore_crystal_fire` for item and block membership | Legacy tag resource authored; direct aspect only | Preserve direct assignment. |
| `oreCrystalOrder` | `OreDictionaryEntries` registers `BlocksTC.crystalOrder` wildcard | Direct `ItemStack` assignment line 489: `ORDER 15`, `CRYSTAL 10` | `thaumcraft:crystal_ordo` | `thaumcraft:legacy_ore_dictionary/ore_crystal_order` for item and block membership | Legacy tag resource authored; direct aspect only | Preserve direct assignment. |
| `oreCrystalEntropy` | `OreDictionaryEntries` registers `BlocksTC.crystalEntropy` wildcard | Direct `ItemStack` assignment line 490: `ENTROPY 15`, `CRYSTAL 10` | `thaumcraft:crystal_perditio` | `thaumcraft:legacy_ore_dictionary/ore_crystal_entropy` for item and block membership | Legacy tag resource authored; direct aspect only | Preserve direct assignment. |
| `oreCrystalTaint` | `OreDictionaryEntries` registers `BlocksTC.crystalTaint` wildcard | Direct `ItemStack` assignment line 491: `FLUX 15`, `CRYSTAL 10` | `thaumcraft:crystal_vitium` | `thaumcraft:legacy_ore_dictionary/ore_crystal_taint` for item and block membership | Legacy tag resource authored; direct aspect only | Legacy name is taint, port id is `crystal_vitium`; no guessed common tag was added. |
| `logWood` | `OreDictionaryEntries` registers Greatwood and Silverwood logs | Generic line 226: `PLANT 20`; direct lines 499-500 add tree-specific extras | `thaumcraft:log_greatwood`, `thaumcraft:log_silverwood` | Existing `minecraft:logs` plus `thaumcraft:legacy_ore_dictionary/log_wood` for item and block membership | Legacy tag resource and generic fallback authored; direct assignment wins | Direct log assignments override generic `logWood` semantics. |
| `plankWood` | `OreDictionaryEntries` registers Greatwood and Silverwood planks | No direct `ConfigAspects` string assignment found | `thaumcraft:plank_greatwood`, `thaumcraft:plank_silverwood` | Existing `minecraft:planks` item tag plus `thaumcraft:legacy_ore_dictionary/plank_wood` for item and block membership | Legacy tag resource authored; aspects deferred | Plank aspects likely came from recipe generation, not direct config. Do not assign manually. |
| `slabWood` | `OreDictionaryEntries` registers Greatwood and Silverwood slabs | No direct `ConfigAspects` string assignment found | `thaumcraft:slab_greatwood`, `thaumcraft:slab_silverwood` | Existing `minecraft:wooden_slabs` item tag plus `thaumcraft:legacy_ore_dictionary/slab_wood` for item and block membership | Legacy tag resource authored; aspects deferred | Slab aspects likely came from recipe generation. |
| `treeSapling` | `OreDictionaryEntries` registers Greatwood and Silverwood saplings | Generic line 227: `PLANT 15`, `LIFE 5`; direct lines 503-504 add tree-specific values | `thaumcraft:sapling_greatwood`, `thaumcraft:sapling_silverwood` | Existing `minecraft:saplings` plus `thaumcraft:legacy_ore_dictionary/tree_sapling` for item and block membership | Legacy tag resource and generic fallback authored; direct assignment wins | Silverwood direct assignment uses `AURA 5`, so generic tag assignment cannot override it. |
| `treeLeaves` | `OreDictionaryEntries` registers Greatwood and Silverwood leaves wildcard | Generic line 228: `PLANT 5`; direct lines 501-502 match generic value | `thaumcraft:leaves_greatwood`, `thaumcraft:leaves_silverwood` | Existing `minecraft:leaves` plus `thaumcraft:legacy_ore_dictionary/tree_leaves` for item and block membership | Legacy tag resource and generic fallback authored; direct assignment wins | Direct and generic values currently match. |
| `gemAmber` | `OreDictionaryEntries` registers `ItemsTC.amber` | `ConfigAspects` line 483: `TRAP 10`, `CRYSTAL 10` | `thaumcraft:amber` | `c:gems/amber` plus `thaumcraft:legacy_ore_dictionary/gem_amber` item membership | Tag resources and tag lookup authored | Item tag only; `c:gems` aggregate includes `#c:gems/amber`. No block tag unless amber block gets a separate legacy source. |
| `quicksilver` | `OreDictionaryEntries` registers `ItemsTC.quicksilver` | `ConfigAspects` line 482: `METAL 10`, `DEATH 5`, `ALCHEMY 5` | `thaumcraft:quicksilver` | `thaumcraft:legacy_ore_dictionary/quicksilver` item membership | Legacy tag resource and tag lookup authored | No guessed common `c:` quicksilver tag was added. |

## Registered But Not Directly Aspect-Mapped Yet

| Current id | Legacy evidence | Decision |
|---|---|---|
| `thaumcraft:amber_block` | Registered in the current port, but no direct `ConfigAspects` entry found in the audited range. | Defer. Do not derive from `gemAmber` until exact recipe/generated aspect behavior is ported. |
| `thaumcraft:amber_brick` | Registered in the current port, but no direct `ConfigAspects` entry found. | Defer. |
| `thaumcraft:stone_arcane` | Registered in the current port, but no direct `ConfigAspects` entry found for this id. | Defer until recipe/generated aspects or exact legacy source is identified. |
| `thaumcraft:stone_arcane_brick` | Registered in the current port, but no direct `ConfigAspects` entry found for this id. | Defer. |
| `thaumcraft:stairs_*`, `thaumcraft:slab_*` | Registered in the current port; no direct legacy aspect assignment found. | Defer; likely generated from source block recipes. |
| `thaumcraft:fabric` | Registered in legacy and port; no direct `ConfigAspects` assignment found. | Defer until generated/recipe aspects are ported. |

## Unregistered Legacy Thaumcraft Keys

These keys appear in `OreDictionaryEntries` but their target items/blocks are not currently registered in the NeoForge port. Do not create tags or aspect assignments for them yet.

| Legacy key group | Examples | Decision |
|---|---|---|
| Nitor | `nitor` | Defer until nitor blocks/items are registered and light/render behavior is designed. |
| Nuggets | `nuggetIron`, `nuggetCopper`, `nuggetTin`, `nuggetSilver`, `nuggetLead`, `nuggetQuicksilver`, `nuggetThaumium`, `nuggetVoid`, `nuggetBrass`, `nuggetQuartz`, `nuggetMeat` | Defer until item metadata variants are split into 1.21 ids or data components. |
| Ingots and metal blocks | `ingotThaumium`, `ingotVoid`, `ingotBrass`, `blockThaumium`, `blockVoid`, `blockBrass` | Defer until material items/blocks are ported. |
| Plates | `plateIron`, `plateBrass`, `plateThaumium`, `plateVoid` | Defer until plate item model/id strategy is decided. |
| Clusters | `clusterIron`, `clusterGold`, `clusterCopper`, `clusterTin`, `clusterSilver`, `clusterLead`, `clusterCinnabar`, `clusterQuartz` | Defer until cluster items are registered. |
| Vanilla compatibility | `trapdoorWood` | Defer; not part of current Thaumcraft ids. |

## Global `ConfigAspects` String Keys

`ConfigAspects` also assigns aspects to many vanilla/common ore dictionary strings that are not Thaumcraft-specific. These are relevant to future scanning and generated aspect lookup, but they are not safe to connect to current gameplay yet.

| Legacy group | Examples | Modern tag direction | Current status |
|---|---|---|---|
| Vanilla ores | `oreLapis`, `oreDiamond`, `oreRedstone`, `oreEmerald`, `oreQuartz`, `oreIron`, `oreGold` | Existing `c:ores/*` item/block tags where present | Implemented for current vanilla coverage. |
| Vanilla gems | `gemDiamond`, `gemEmerald`, `gemQuartz` | Existing `c:gems/*` item tags | Implemented for current vanilla coverage. |
| Vanilla dusts | `dustRedstone`, `dustGlowstone` | Existing `c:dusts/*` item tags | Implemented for current vanilla coverage. |
| Common metals | `ingotCopper`, `dustCopper`, `oreCopper`, `clusterCopper`, etc. | Existing or future `c:ingots/*`, `c:dusts/*`, `c:ores/*` item/block tags | Vanilla iron/gold/copper ore and ingot keys are bridged; raw iron/gold/copper use corresponding legacy `ore*` aspects. Other metal keys remain deferred until the full material catalog and modded compatibility policy are designed. |
| Vanilla blocks/items | `stone`, `cobblestone`, `dirt`, `sand`, `grass`, `blockGlass`, `obsidian`, `logWood`, `treeSapling`, `treeLeaves`, etc. | Mix of exact ids, `minecraft` tags, `c` tags, and manual 1.21-only entries | Implemented for current vanilla item-id coverage; component-sensitive stack variants remain deferred. |

## Next Implementation Checklist

1. Keep `TCAspectParityValidator` validating direct and tag assignments.
2. Keep `TCAspectReloadValidator` validating real loaded tags and fallback lookup after server data load.
3. Keep generated recipe outputs restricted to audited scope; current cache writes current `minecraft:*` and `thaumcraft:*` standard crafting outputs only.
4. Do not add guessed quicksilver or crystal common `c:` tags until their modern tag names are explicitly chosen and documented.
5. Do not broaden vanilla/modded `c:` material bridges without a `ConfigAspects` source line and reload validation.
