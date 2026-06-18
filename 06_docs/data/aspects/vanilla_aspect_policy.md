# Thaumcraft 6 Vanilla Aspect Policy

Last reviewed branch: `main`
Target module: `05_neoforge_port`

## Purpose

This document answers how vanilla item and block aspects are known in the NeoForge 1.21.1 port.

Guide rule applied: legacy `OreDictionary` assignments must become explicit tags, metadata/wildcard behavior must be reviewed instead of copied blindly, and generated recipe data must be cached on reload instead of scanned at lookup time.

## Source Of Truth

Vanilla aspects are not guessed from block names, item names, or modern Minecraft tags. The source of truth is legacy Thaumcraft 6:

```text
02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/config/ConfigAspects.java
```

Legacy `ConfigAspects.registerItemAspects()` uses two assignment styles:

| Legacy style | Example | Port rule |
|---|---|---|
| Exact `ItemStack`, `Item`, or `Block` assignment | `Blocks.COAL_ORE`, `Blocks.BEDROCK`, `Items.MILK_BUCKET` | Map to an exact 1.21 item id when the modern id is the same concept and no metadata ambiguity remains. |
| String key assignment through old OreDictionary semantics | `oreCopper`, `ingotCopper`, `stone`, `logWood` | Map to audited item tags, usually `c:<material>` for modern common tags or `thaumcraft:legacy_ore_dictionary/*` for exact legacy compatibility. |
| Recipe-derived assignment through `generateTags` | planks, slabs, stairs, blocks from gems, outputs with container remainders | Rebuild into a reload-safe generated cache from recipes. Do not write guessed static values. |
| Stack-sensitive bonus assignment through `getBonusTags` | armor, swords, bows, tools, hoes/shears, dyes, enchanted items, enchanted books | Apply after exact/tag/generated base aspects, using item type and data components instead of old NBT lists. |
| Potion variant assignment | potion/tipped-arrow/splash/lingering stacks with potion data | Resolve from `POTION_CONTENTS`, follow the legacy brewing reagent chain, then add carrier aspects. |

## 1.21.1 New Content Rule

Minecraft 1.21.1 contains vanilla items and blocks that did not exist in Minecraft 1.12.2. They are handled by strict source categories:

| 1.21.1 content category | Gets aspects? | Reason |
|---|---|---|
| Direct legacy equivalent exists | Yes, exact assignment. | Example: `minecraft:coal_ore`, `minecraft:coal`, `minecraft:bedrock`, buckets. |
| Legacy OreDictionary material exists and the modern item/block is in the audited replacement tag | Yes, tag assignment. | Example: `oreCopper` and `ingotCopper` now bridge to `c:ores/copper` and `c:ingots/copper`, so vanilla copper ore and copper ingot receive those legacy material aspects. |
| Output is a Thaumcraft recipe result whose ingredients already have known aspects | Yes, generated cache entry after server data/tag reload. | Example: Greatwood planks/slabs/stairs and amber block/brick. |
| Modern raw material for a legacy ore material | Yes, using the corresponding legacy `ore*` aspects. | Example: `minecraft:raw_copper` uses `oreCopper` aspects because raw copper is the modern unprocessed ore drop. |
| Truly new concept without a legacy direct assignment, legacy OreDictionary material, or audited generated recipe path | Yes, only through a documented manual exact entry. | Values must be assigned by closest legacy Thaumcraft category, not by name heuristics at lookup time. |
| Vanilla spawn egg | No. | Thaumcraft 6 1.12 did not assign aspects to spawn eggs, and they do not gain recipe or bonus aspects. |
| Empty component-only potion carrier id | No plain-id assignment. | `splash_potion`, `lingering_potion`, and `tipped_arrow` are variant stacks in legacy behavior; real 1.21 stacks must carry `POTION_CONTENTS`. |

This keeps 1.21.1 additions deterministic. A new item can be added later, but only by adding an explicit data assignment, an audited tag bridge, or a validated recipe-derived path.

The full current manual 1.21 assignment table is `06_docs/data/aspects/vanilla_1_21_aspect_assignments.md`.

## Current Implementation Decision

The current port has three active assignment layers:

| Layer | Resource/code | Current role |
|---|---|---|
| Current Thaumcraft registered ids | `data/thaumcraft/aspect_assignments/current_registered.json` | Exact/tag assignments for already registered Thaumcraft content. |
| Legacy vanilla core | `data/thaumcraft/aspect_assignments/legacy_vanilla_core.json` | Conservative exact vanilla seeds plus copper OreDictionary tag bridge. |
| Legacy vanilla materials | `data/thaumcraft/aspect_assignments/legacy_vanilla_materials.json` | Vanilla ore/gem/ingot/dust/raw-material bridges from legacy OreDictionary keys. |
| Legacy/modern vanilla exact audit | `data/thaumcraft/aspect_assignments/legacy_vanilla_modern_exact.json` | Direct modern-id entries derived from legacy-style `ConfigAspects` values. The 1.20.1 attempt is treated as a clue, not authority. |
| 1.21-only vanilla manual layer | `data/thaumcraft/aspect_assignments/legacy_vanilla_modern_manual.json` | Explicit manual values for vanilla ids that have no direct 1.12 equivalent, assigned by closest legacy category. |
| Legacy vanilla runtime parity layer | `data/thaumcraft/aspect_assignments/legacy_vanilla_runtime_parity.json` | Dump-derived final 1.12 values for legacy-equivalent plain vanilla ids where metadata flattening, generated recipes, complex extras, wildcard specificity, or stack bonus behavior would otherwise diverge. |
| Generated crafting cache | `TCGeneratedAspectRecipeGenerator` | Reload-built recipe-derived entries for current `minecraft:*` and `thaumcraft:*` standard crafting outputs whose ingredients have known aspects. |
| Stack-sensitive legacy rules | `TCAspectStackRules` | Applies `getBonusTags`-style armor/tool/weapon/dye/enchantment additions, potion component lookup, and legacy no-aspect exclusions. |

Lookup order is:

1. Legacy no-aspect exclusions, currently vanilla spawn eggs.
2. Component-aware potion stack lookup for potion/tipped/splash/lingering stacks with `POTION_CONTENTS`.
3. Exact item id assignment.
4. Item tag assignment.
5. Generated cache entry.
6. Stack-sensitive bonus rules for equipment, dyes, and enchantments.
7. `null`.

## Single Blocks Without Crafting

Single blocks/items that are not recipe outputs receive aspects only if one of these is true:

- the exact item id has an assignment, for example `minecraft:coal_ore`;
- the block item belongs to an audited aspect tag, for example `minecraft:copper_ore` in `c:ores/copper`;
- a future validated generated cache path creates the value.

They do not receive aspects merely because they are blocks.

## Current Vanilla Coverage

Server data-load validation currently enforces full assignable item-id coverage:

```text
1230 of 1230 minecraft item ids have aspects; 0 missing
```

This excludes legacy no-aspect spawn eggs, firework star/rocket, infested blocks, and empty component-only potion carrier ids. Real potion/tipped-arrow/splash/lingering stacks with `POTION_CONTENTS`, and enchanted items/books with enchantment components, are validated through component-aware lookup rather than plain id-only lookup.

## Core Vanilla Seeds

The first vanilla seed slice is intentionally small:

| Legacy source | Modern assignment | Aspects |
|---|---|---|
| `Blocks.COAL_ORE` | `minecraft:coal_ore` | `terra 5`, `potentia 15`, `ignis 15` |
| wildcard `Items.COAL` | `minecraft:coal`, `minecraft:charcoal` | `potentia 10`, `ignis 10` |
| `Blocks.BEDROCK` | `minecraft:bedrock` | `vacuos 25`, `perditio 25`, `terra 25`, `tenebrae 25` |
| wildcard `Items.CLAY_BALL` | `minecraft:clay_ball` | `aqua 5`, `terra 5` |
| wildcard `Items.BRICK` from clay ball + fire | `minecraft:brick` | `aqua 5`, `terra 5`, `ignis 1` |
| `Items.BUCKET` complex seed | `minecraft:bucket` | `vacuos 5`, `metallum 33` |
| `Items.WATER_BUCKET` | `minecraft:water_bucket` | `vacuos 5`, `metallum 33`, `aqua 20` |
| `Items.LAVA_BUCKET` | `minecraft:lava_bucket` | `vacuos 5`, `metallum 33`, `ignis 15`, `terra 5` |
| `Items.MILK_BUCKET` | `minecraft:milk_bucket` | `vacuos 5`, `metallum 33`, `victus 10`, `bestia 5`, `aqua 5` |
| `ingotCopper` | `c:ingots/copper` | `metallum 10`, `permutatio 5` |
| `oreCopper` | `c:ores/copper` | `metallum 10`, `terra 5`, `permutatio 5` |
| `oreIron`, `ingotIron` | `c:ores/iron`, `c:ingots/iron` | `terra 5`, `metallum 15`; `metallum 15` |
| `oreGold`, `ingotGold` | `c:ores/gold`, `c:ingots/gold` | `terra 5`, `metallum 10`, `desiderium 10`; `metallum 10`, `desiderium 10` |
| `oreDiamond`, `gemDiamond` | `c:ores/diamond`, `c:gems/diamond` | `terra 5`, `desiderium 15`, `vitreus 15`; `vitreus 15`, `desiderium 15` |
| `oreEmerald`, `gemEmerald` | `c:ores/emerald`, `c:gems/emerald` | `terra 5`, `desiderium 10`, `vitreus 15`; `vitreus 15`, `desiderium 10` |
| `oreLapis`, `oreRedstone`, `dustRedstone`, `dustGlowstone`, `gemQuartz` | matching `c:` tags | Direct legacy aspect values from `ConfigAspects`. |
| 1.21 raw material bridge | `c:raw_materials/iron`, `c:raw_materials/gold`, `c:raw_materials/copper` | Same aspect list as corresponding legacy `ore*` entry. |

## Validation

Current server reload validation checks:

- exact vanilla block/item lookup for coal ore, coal, charcoal, and milk bucket;
- tag fallback for copper ore and copper ingot;
- tag fallback for vanilla ores, gems, ingots, dusts, and ore-derived raw materials;
- full current `minecraft:*` item id coverage, failing reload validation if any item id is missing aspects;
- manual 1.21-only ids such as copper oxidation variants, Trial Chambers items, pottery sherds, new plants, new buckets, new discs, and new stone families;
- legacy no-aspect behavior for spawn eggs;
- component-aware potion carrier lookup;
- component-aware enchanted book lookup with stored enchantment bonus aspects;
- shapeless crafting formula parity;
- remaining-item subtraction parity using milk bucket -> bucket;
- exact/tag assignments winning over generated cache;
- generated cache exposed through `AspectHelper.generateTags`.

## Deferred Policy

Do not add broad assignments for all modded `c:` tags yet. For vanilla, keep using explicit exact/manual data, audited tags, or standard crafting generation. For third-party content, audit the relevant legacy key, decide exact modern tag membership, and add validation for at least one representative lookup before exposing it.
