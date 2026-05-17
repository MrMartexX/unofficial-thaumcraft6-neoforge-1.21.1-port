# Thaumcraft 6 Aspect Legacy Gap Audit

Last reviewed branch: `main`
Target module: `05_neoforge_port`

## Purpose

This document records the current gap between legacy Thaumcraft 6 aspect assignment behavior, the rough 1.20.1 port attempt, and this NeoForge 1.21.1 port.

Guide rule applied: old `OreDictionary` mappings become tags, metadata/wildcard entries require explicit flattened-id review, and recipe-derived behavior must be rebuilt through reload-safe caches or custom recipe serializers instead of copied line-for-line.

## Direct Answers

| Question | Answer |
|---|---|
| Do our exact JSON assignments fully match legacy 1.12? | For currently comparable legacy-equivalent stacks, yes after the dump-derived runtime parity override layers and stack-sensitive potion/enchantment fixes. Exact JSON is still only one layer of the full system, but the current mapped runtime report has no real `PORT_GAP_*` buckets. |
| Do our exact JSON assignments match the 1.20.1 attempt? | No. The 1.20.1 attempt hardcodes a large `ConfigAspects` port with many `forge:*` strings and direct modern ids. Our port uses reloadable JSON/tag data and validation instead. |
| Are there gaps for things that received aspects in 1.12 but not yet in the port? | Yes, but they are now scope gaps rather than current mapped parity bugs: unported legacy Thaumcraft content remains `LEGACY_ONLY_THAUMCRAFT_UNPORTED`, and three non-Thaumcraft legacy-only keys still need mapping/removal review. The current comparable set has no potion, enchanted-book, registered-Thaumcraft, amount, set, order, null/empty, or result-kind gaps. |
| Should we rely on the 1.20.1 attempt for final values? | No. It is useful as a modern-id clue, but not as authority. The authority remains 1.12 `ConfigAspects` plus the original API/source behavior. |

## Source Counts

| Source | Count / status | Notes |
|---|---:|---|
| Legacy 1.12 `ConfigAspects` object/complex registration lines | `427` | Includes direct stack assignments, string/OreDictionary assignments, complex/generated-style assignments, and Thaumcraft-specific entries. |
| Legacy 1.12 string/OreDictionary object lines | `86` | These become modern tags, not hardcoded item maps. |
| Legacy 1.12 complex object lines | `94` | These need flattening, generated recipe support, or component-aware logic depending on the entry. |
| 1.20.1 attempt object registration lines | `438` | Large hardcoded rewrite; not final parity because complex behavior and modern tag namespaces are not handled the same way. |
| Current 1.21 exact item assignments | `673` | Current registered Thaumcraft ids, direct legacy vanilla seeds, modern-id exact audit entries, audited 1.21-only vanilla manual entries, dump-derived runtime parity overrides for legacy-equivalent plain vanilla stacks, and current registered Thaumcraft runtime parity overrides. Spawn eggs, firework star/rocket, and infested blocks are excluded for 1.12 parity. |
| Current 1.21 item tag assignments | `46` | Current common/legacy tags, vanilla material bridges, base material tags, `blockGlass`, and ore-derived raw material bridges. |
| Current 1.21 complex exact assignments | `32` | Audited slice for buckets/boats/doors/fence gates and related complex extras. Do not bulk-expand from source alone; the runtime dump showed legacy `registerComplexObjectTag` can be masked by exact generated entries and wildcard lookup order. |
| Current 1.21 generated crafting assignments | `475` | Standard crafting cache for current `minecraft:*` and `thaumcraft:*` outputs whose ingredients have known aspects. Some legacy-equivalent vanilla and currently registered Thaumcraft outputs are now exact runtime parity overrides because final 1.12 lookup order, metadata flattening, or wildcard specificity proved different from generated/manual fallback. |
| Current vanilla item id coverage | `1230/1230` assignable ids | Enforced by server data-load validation. Spawn eggs and empty component-only splash/lingering/tipped carrier ids are intentionally excluded because legacy Thaumcraft 6 did not treat those as plain aspect-bearing ids. |
| Mapped runtime parity report | `1139` comparable keys | `1139` identical, including `283` explicit legacy-to-modern id/meta mappings. Remaining real mapped gaps are `0`: potion content/order parity, enchanted-book parity, and registered Thaumcraft set parity are closed. |

## Current Implemented Assignment Layers

| Layer | File/code | Purpose |
|---|---|---|
| Current registered Thaumcraft exact/tag data | `data/thaumcraft/aspect_assignments/current_registered.json` | Direct values for already registered Thaumcraft content plus safe current tags. |
| Legacy vanilla exact core | `data/thaumcraft/aspect_assignments/legacy_vanilla_core.json` | Direct vanilla seeds such as coal, bedrock, clay/brick, buckets, and copper OreDictionary bridge. |
| Legacy vanilla material tags | `data/thaumcraft/aspect_assignments/legacy_vanilla_materials.json` | First broad material tag bridge for vanilla ores, gems, ingots, dusts, and 1.21 raw materials. |
| Legacy/modern vanilla exact audit | `data/thaumcraft/aspect_assignments/legacy_vanilla_modern_exact.json` | Direct modern-id entries extracted from the 1.20.1 attempt only where they map back to legacy-style values; useful as id coverage, not final authority. The mapped runtime dump now decides which entries are true parity and which are port gaps. |
| 1.21-only vanilla manual layer | `data/thaumcraft/aspect_assignments/legacy_vanilla_modern_manual.json` | Explicit category-based values for current vanilla ids with no direct 1.12 id, using the closest legacy Thaumcraft aspect style. |
| Legacy vanilla runtime parity layer | `data/thaumcraft/aspect_assignments/legacy_vanilla_runtime_parity.json` | Exact override data generated from the mapped 1.12-vs-1.21 runtime dump for shared plain vanilla stacks. It records final resolved 1.12 values after direct seed, generated recipe, complex extra, wildcard specificity, and metadata-flattening behavior are known. |
| Generated crafting cache | `TCGeneratedAspectRecipeGenerator` | Recipe-derived values for validated standard crafting outputs in current `minecraft:*` and `thaumcraft:*` namespaces. |
| Stack-sensitive legacy rules | `TCAspectStackRules` | Legacy no-aspect spawn eggs, potion `POTION_CONTENTS`, armor/tool/weapon/dye bonuses, and enchantment/stored-enchantment bonuses. |

## Final Assignment Policy

| Legacy source kind | Modern mechanism | Rule |
|---|---|---|
| Exact `ItemStack`, `Item`, or `Block` assignment | Exact item id JSON | Use when the 1.21 item id is a direct equivalent and no metadata ambiguity remains. |
| Legacy wildcard metadata | Multiple exact ids or reviewed tag | Split into flattened ids. Do not hide missing variants with a broad tag unless the legacy meaning was already broad. |
| Legacy OreDictionary string key | `c:` tag or `thaumcraft:legacy_ore_dictionary/*` tag | Use `c:` only for stable common material categories; otherwise preserve the exact legacy key under Thaumcraft namespace. |
| Vanilla crafting generated value | Reload-built generated cache | Sum ingredient aspects, subtract remaining items, apply `0.75 / outputCount`, floor, remove non-positive values, and choose the lowest positive total among matching recipes. |
| Arcane/crucible/infusion generated value | Future custom recipe service | Must wait for the proper recipe type/serializer and design document. |
| Bonus aspects for tools, armor, enchantments, and potions | Component-aware stack service | Implemented for current vanilla equipment/dyes/enchantments/potion carriers. Essentia containers and future custom item state still need gameplay systems. |
| Entity aspects | Future entity aspect data loader | Separate from item/block stack aspects. |

## New 1.21 Content Policy

| New content type | Decision | Reason |
|---|---|---|
| Deepslate variants of legacy ores | Same tag as the legacy ore material | They are the same ore concept in the modern worldgen layer. |
| Raw iron/gold/copper | Same aspects as corresponding legacy `ore*` entry | Raw materials are modern ore drops, closer to unprocessed ore chunks than finished ingots. |
| New unrelated materials with no legacy key | Manual exact entry after category audit | Values must be tied to the closest legacy family and documented in `legacy_vanilla_modern_manual.json`. |
| New recipe outputs outside `thaumcraft:*` | Generated when standard crafting inputs have known aspects | Exact/tag/manual entries still win; third-party modded namespaces remain excluded. |
| New wood/leaves/saplings | Tag bridge or manual exact entry depending on scope | Vanilla ids are covered; Thaumcraft generated wood outputs keep recipe-derived values. |
| Spawn eggs | No aspects | Matches 1.12 behavior. Do not add manual spawn-egg aspects. |

## Current Known Gaps

| Gap area | Examples | Needed next |
|---|---|---|
| Component-sensitive stack variants still outside current vanilla scope | essentia containers, future custom item data, future damage-specific metadata if discovered | Current vanilla potion/enchantment/equipment stack rules exist; custom Thaumcraft item state still needs its subsystem. |
| Full material catalog | tin, silver, lead, brass, thaumium, void metal, clusters, nuggets, plates | Wait until corresponding Thaumcraft items or modern material tags are registered/selected. |
| Complex object tags and flattened vanilla families | boats, doors, fence gates, arrows, buckets, chests, color/meta blocks, generated vanilla families | Closed for shared plain vanilla runtime parity by using observed dump values. Keep the trace lesson: source intent alone is not enough because generated exact entries and wildcard complex entries can mask each other. |
| Legacy runtime lookup specificity | future component-sensitive stacks, custom Thaumcraft item state, addon registrations | Shared plain vanilla ids now use dump-derived exact runtime values. Future stateful stacks still need component/key specificity rather than broad id-only assignments. |
| Recipe-generated outputs | custom Thaumcraft arcane/crucible/infusion outputs, future modded outputs | Vanilla/Thaumcraft standard crafting cache exists; custom recipe systems still need serializers and parity fixtures. |
| Bonus tags still missing | essentia containers and future custom item state | Vanilla equipment/dye/enchantment/potion behavior is implemented; essentia containers wait for essentia systems. |
| Entities | vanilla mobs and Thaumcraft mobs | Needs entity tag data loader and validation after entity systems start. |

## Next Safe Expansion Order

1. Keep vanilla assignable item coverage validation at `1230/1230` after every data change.
2. Keep the mapped runtime comparer at `0` real `PORT_GAP_*` entries before connecting aspects to aura, research, scanning, essentia, or custom crafting gameplay.
3. Add tests before changing component-aware bonus aspects for potions, enchantments, damageable gear, and contained items.
4. Add validation for every new tag-backed category with at least one representative item/block.
5. Expand `legacy_to_modern_stack_map.json` only when the equivalent stack is clear; otherwise keep entries in mapping/policy review buckets.
6. Port arcane/crucible/infusion generated paths before assigning custom recipe output aspects.
8. Add entity aspect data separately.
