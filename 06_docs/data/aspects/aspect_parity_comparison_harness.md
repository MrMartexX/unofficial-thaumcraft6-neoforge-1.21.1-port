# Thaumcraft Aspect Parity Comparison Harness

Last reviewed branch: `main`
Target module: `05_neoforge_port`

## Purpose

This document defines the reliable method for comparing real item aspects in Thaumcraft 6 Forge 1.12.2 and the NeoForge 1.21.1 port.

The goal is not to compare source files or screenshots. The goal is to compare resolved runtime `AspectList` results from the actual loaded mods, recipes, tags, stack data, and configs.

## Accuracy Boundary

`100% accurate` can only mean 100% accurate for a declared runtime manifest:

| Requirement | Reason |
|---|---|
| Same original Thaumcraft 6 jar and Forge version for the legacy dump | Decompiled source can miss bytecode/runtime quirks. |
| Same mod list and config for the legacy dump | Addons can register aspects through `AspectRegistryEvent` and `OreDictionary`. |
| Same NeoForge build, datapacks, tags, and configs for the port dump | Tags and recipes directly affect generated aspects. |
| Same stack manifest for both sides | Minecraft 1.12.2 and 1.21.1 do not have the same item universe. |
| Deterministic JSON output | Human screenshots are not acceptable as parity proof. |

Recommended baseline:

| Side | Version |
|---|---|
| Legacy | Forge `1.12.2-14.23.5.2860`, `Thaumcraft-1.12.2-6.1.BETA26.jar`, `Baubles-1.12-1.5.2.jar` |
| Port | NeoForge `21.1.228`, Java 21, current `05_neoforge_port` build |

## Why Tooltip Comparison Is Not Enough

| Problem | Consequence |
|---|---|
| Legacy tooltip depends on Shift and `ModConfig.CONFIG_GRAPHICS.showTags`. | A missing tooltip icon can mean config/display state, not missing aspects. |
| Tooltip hides `null` vs empty-list difference. | API behavior can diverge while the screen looks identical. |
| Tooltip sorts by amount for display. | It does not prove original insertion order or exact `AspectList` semantics. |
| Tooltip only tests manually hovered stacks. | It will miss generated recipe outputs, potion variants, enchantment variants, and edge cases. |

Use tooltip screenshots only as visual regression evidence after API dumps match.

## Artifacts

Store parity artifacts outside the source code modules:

```text
07_Test_Instance_and_Comparisons/aspect_parity/
  input/
    legacy_stack_manifest.json
    legacy_to_modern_stack_map.json
  dumps/
    thaumcraft_1_12_aspects.json
    thaumcraft_1_21_aspects.json
  reports/
    aspect_diff.json
    aspect_diff.md
  tools/
    compare_aspect_dumps.py
```

The source repositories should not be patched just to collect the 1.12 dump. Use a separate diagnostic mod/addon or a temporary harness in the comparison folder.

## Legacy 1.12 Exporter

Create a small Forge 1.12.2 diagnostic addon loaded alongside the original Thaumcraft jar.

| Requirement | Detail |
|---|---|
| Load timing | Run after Thaumcraft `ConfigAspects.postInit`, recipe registration, ore dictionary registration, and addon aspect events. A server command is safest. |
| Command | Example: `/tc_aspect_dump dump_all`. It writes JSON under the test instance folder. |
| Data source | Call `thaumcraft.api.aspects.AspectHelper.getObjectAspects(stack)` for the public API result. Optionally use reflection/instrumentation for trace-only source labels. |
| Do not use UI | No tooltip parsing, no screenshots, no JEI/creative-tab scraping as the source of truth. |
| Preserve distinction | Export `result_kind: "null"` separately from `result_kind: "empty"`. |
| Preserve order | Export aspects in `AspectList.getAspects()` order, and optionally also sorted-by-amount for tooltip checks. |

Minimum output schema:

```json
{
  "environment": {
    "minecraft": "1.12.2",
    "forge": "14.23.5.2860",
    "thaumcraft": "6.1.BETA26",
    "mods": ["thaumcraft", "baubles"]
  },
  "entries": [
    {
      "legacy_key": "minecraft:coal@0{}",
      "item": "minecraft:coal",
      "meta": 0,
      "nbt": null,
      "count": 1,
      "result_kind": "aspects",
      "aspects": [
        { "id": "potentia", "amount": 10 },
        { "id": "ignis", "amount": 10 }
      ]
    }
  ]
}
```

## Legacy Stack Manifest

The legacy exporter should enumerate deterministic stack samples. Do not compare only plain item ids.

| Category | Samples |
|---|---|
| Plain registry items | Every non-air `Item.REGISTRY` entry as count `1`, default meta. |
| Creative subitems | Values returned by `getSubItems` for all creative tabs, preserving valid metadata/NBT variants. |
| Wildcard-sensitive metadata | Explicit meta `0..15` for items/blocks with 1.12 metadata assignments or wildcard assignments in `ConfigAspects`. |
| Damageable items | At least undamaged, damage `1`, mid damage, and max-1 damage. Legacy often wildcard-normalizes damageable items, but this must be proven. |
| Potions | Every `PotionType.REGISTRY` value for regular potion, splash potion, lingering potion, and tipped arrow. |
| Enchanted books | Every vanilla enchantment at every valid level as stored enchantment NBT. |
| Enchanted equipment/tools/books | Representative applicable items with normal enchantment NBT for every vanilla enchantment and level. |
| Generated crafting outputs | All outputs from crafting registry that can be generated by Thaumcraft, plus known Thaumcraft custom recipe outputs after those systems are ported. |
| Essentia containers | Deferred until essentia item state is ported; then include empty and filled states. |
| Entities | Separate later dump for entity aspect parity, because entity predicates are not `ItemStack` aspects. |

The first pass should focus on the item stack categories that already exist in the port: plain vanilla, current Thaumcraft ids, potions, enchanted books/items, equipment/tools, dyes, and standard crafting outputs.

## Modern 1.21 Exporter

Add a debug-only exporter in the NeoForge port after the comparison format is stable.

| Requirement | Detail |
|---|---|
| Load timing | Run after server data reload, tag binding, recipe loading, and generated aspect cache rebuild. |
| Command | Example: `/tc_aspect_dump dump_mapped`. |
| Input | Consume `legacy_to_modern_stack_map.json`, so only comparable stacks are checked as exact parity targets. |
| Data source | Call `AspectHelper.getObjectAspects(stack)` and `AspectHelper.generateTags(stack)` where needed. |
| Components | Use Data Components to construct potion contents, enchantments, stored enchantments, damage, and future item state. |
| Output | Same schema shape as the 1.12 dump, with a modern `stack_key` and the source legacy key. |

The exporter must run server-side where possible. Client tooltip rendering is a separate visual check after API equality passes.

## Mapping File

Minecraft 1.12.2 and 1.21.1 stacks need an explicit mapping table.

Example:

```json
{
  "mappings": [
    {
      "legacy_key": "minecraft:coal@0{}",
      "modern_stack": { "item": "minecraft:coal" },
      "mode": "exact"
    },
    {
      "legacy_key": "minecraft:coal@1{}",
      "modern_stack": { "item": "minecraft:charcoal" },
      "mode": "metadata_split"
    },
    {
      "legacy_key": "minecraft:potion@0{Potion:\"minecraft:water\"}",
      "modern_stack": {
        "item": "minecraft:potion",
        "components": { "minecraft:potion_contents": "minecraft:water" }
      },
      "mode": "component_equivalent"
    }
  ]
}
```

Mapping modes:

| Mode | Meaning |
|---|---|
| `exact` | Same item concept and aspect result must match exactly. |
| `metadata_split` | 1.12 metadata became separate 1.21 ids. Result must match the mapped legacy meta. |
| `component_equivalent` | 1.12 NBT became 1.21 Data Components. Result must match after component construction. |
| `removed` | Legacy stack has no 1.21 equivalent. No exact comparison required. |
| `modern_only` | New 1.21 stack. Must compare against documented policy, not against 1.12. |
| `expected_divergence` | Deliberate difference with a written reason. Should be rare. |

Current implementation note: `07_Test_Instance_and_Comparisons/aspect_parity/input/legacy_to_modern_stack_map.json` is now active. It contains comparison-key aliases for renamed ids and stack-key aliases for 1.12 metadata families such as wood variants, wool/carpet/bed/banner colors, dyes, fish variants, skull variants, stained glass, terracotta, concrete, shulker boxes, and flattened block/item ids. The comparer treats entries that match only after this mapping as `PARITY_OK_LEGACY_TO_MODERN_MAP`, not as generic parity.

## Comparison Rules

The comparison script should produce machine-readable JSON and a human-readable markdown report.

| Check | Required behavior |
|---|---|
| `null` vs empty | Compare separately. A `null` result and an empty `AspectList` are not the same API result. |
| Aspect set | Every aspect id must match. |
| Aspect amount | Every amount must match exactly for `exact`, `metadata_split`, and `component_equivalent` mappings. |
| Aspect order | Compare insertion order from `getAspects()`. Tooltip order can be checked separately through sorted-by-amount output. |
| Generated result | Compare both `getObjectAspects` and `generateTags` where a generated output is expected. |
| Source category | Optional trace labels can explain diffs, but the final pass/fail must use resolved values. |

Diff categories:

| Category | Meaning |
|---|---|
| `IDENTICAL` | Same `result_kind`, same order, same aspects, same amounts. |
| `ORDER_ONLY_DIFF` | Same aspects and amounts, different insertion order. Still important for UI/API parity. |
| `AMOUNT_DIFF` | Same aspect id exists but amount differs. |
| `ASPECT_SET_DIFF` | Missing or extra aspect id. |
| `NULL_EMPTY_DIFF` | One side returned `null`, the other returned empty list. |
| `LEGACY_ONLY` | Legacy stack has no mapped modern equivalent. |
| `MODERN_ONLY_POLICY` | New 1.21 stack covered by documented manual policy. |
| `EXPECTED_DIVERGENCE` | Deliberate and documented behavior difference. |
| `PORT_GAP` | Unexpected mismatch that must be fixed before gameplay uses the aspect. |

## Current Report Snapshot

Latest mapped report:

```text
07_Test_Instance_and_Comparisons/aspect_parity/reports/aspect_diff.json
07_Test_Instance_and_Comparisons/aspect_parity/reports/aspect_diff.md
```

| Metric | Count |
|---|---:|
| Legacy dump entries | `1798` |
| Modern dump entries | `1987` |
| Legacy comparison keys after mapping | `1537` |
| Modern comparison keys | `1987` |
| Comparable keys | `1139` |
| Configured key aliases | `82` |
| Configured stack-key aliases | `201` |
| Legacy entries mapped through aliases | `283` |

Raw result:

| Raw category | Count |
|---|---:|
| `IDENTICAL` | `1139` |
| `ORDER_ONLY_DIFF` | `0` |
| `AMOUNT_DIFF` | `0` |
| `ASPECT_SET_DIFF` | `0` |
| `NULL_EMPTY_DIFF` | `0` |
| `RESULT_KIND_DIFF` | `0` |
| `LEGACY_ONLY` | `398` |
| `MODERN_ONLY` | `848` |

Policy classification:

| Classification | Count | Meaning |
|---|---:|---|
| `PARITY_OK` | `856` | Direct exact runtime match. |
| `PARITY_OK_LEGACY_TO_MODERN_MAP` | `283` | Runtime match after explicit legacy-to-modern id/meta mapping. |
| `EXPECTED_VERSION_FLATTENED_STACK` | `332` | Expected flattening/new-id difference, including per-entity spawn eggs. |
| `EXPECTED_VERSION_MODERN_ADDITION` | `93` | 1.21-only item/enchantment/music-disc additions. |
| `MODERN_ONLY_COMPONENT_POLICY_REVIEW` | `40` | Modern-only component samples needing policy, not 1.12 exact parity. |
| `MODERN_ONLY_POLICY_REVIEW` | `383` | Modern-only ids still requiring documented policy review. |
| `LEGACY_ONLY_THAUMCRAFT_UNPORTED` | `395` | Legacy Thaumcraft content not registered in the port yet. |
| `LEGACY_ONLY_MAPPING_REVIEW` | `3` | Remaining legacy-only non-Thaumcraft keys needing map/removal review. |

Confirmed fixed by the latest mapped run:

| Prior gap | Current status |
|---|---|
| `fire_charge` and `end_crystal` amount diffs | Fixed by moving `blaze_powder` and `ender_eye` to complex assignment semantics. |
| `elytra` result-kind diff | Fixed: undamaged elytra has legacy aspects; damaged elytra is no-aspect. |
| `firework_star`, `firework_rocket`, infested blocks | Fixed as no-aspect legacy-equivalent stacks. |
| stained glass and melon block/slice mapping | Fixed through `blockGlass` tag bridge and exact melon block mapping. |
| buckets, boats, doors, fence gates, chests, arrows, and color/meta families | Fixed through audited direct/complex data plus `legacy_vanilla_runtime_parity.json` final dump-value overrides. |
| potion carrier/content variants | Fixed through dump-derived final 1.12 runtime outputs for comparable potion, splash potion, lingering potion, and tipped-arrow stacks. |
| mapped `sweeping` -> `sweeping_edge` stored books | Fixed by preserving the legacy stored-book base aspects and omitting a non-legacy Sweeping Edge aspect bonus. |
| currently registered Thaumcraft runtime values | Fixed through `current_registered_runtime_parity.json`, including exact empty-list parity for `amber_brick` and final runtime parity for `thaumometer`. |

Primary root cause learned from the dump: do not trust source-line intent alone for `registerComplexObjectTag`, potion reagent paths, or generated fallback candidates. Legacy runtime can register a generated exact stack and a later wildcard complex stack; because lookup checks exact before wildcard, the plain meta-0 item may resolve to the generated value rather than the wildcard complex value. The current mapped comparable set has no real `PORT_GAP_*` buckets, but the same trace-first rule must be used for future Thaumcraft/stateful/addon stacks.

## Trace Instrumentation

For difficult mismatches, add optional trace output.

Legacy trace should identify:

| Trace label | Meaning |
|---|---|
| `exact` | Hit the exact objectTags key. |
| `wildcard_damage` | Hit damage `32767`. |
| `wildcard_scan_0_15` | Input was wildcard and found a concrete damage match. |
| `stripped_nbt` | Hit after removing NBT. |
| `stripped_nbt_wildcard` | Hit after removing NBT and wildcarding damage. |
| `generated_crucible` | Generated by crucible recipe path. |
| `generated_infusion` | Generated by infusion recipe path. |
| `generated_crafting` | Generated by crafting recipe path. |
| `bonus` | Runtime bonuses changed the base list. |

This trace may require a local patched diagnostic copy or reflection because the public API only exposes final results. Trace code belongs in the comparison harness, not in production port logic.

## Pass Criteria

Gate the port in layers:

| Gate | Pass condition |
|---|---|
| Aspect core | All 37 aspect definitions and pure `AspectList`/`AspectHelper` fixtures match. Already covered by current bootstrap validation. |
| Plain legacy-equivalent item stacks | Every mapped exact or metadata-split stack matches final `getObjectAspects` output. |
| Stack-sensitive variants | Potions, enchanted books, enchanted items, damageable samples, dyes, tools, armor, and bows match. |
| Generated crafting | Standard crafting outputs match for legacy-equivalent recipes. |
| Custom recipes | Crucible, infusion, and arcane outputs match after those recipe systems are ported. |
| Entity aspects | Entity type and predicate results match after entity aspect data is ported. |
| Modern-only items | Every new 1.21 item has a documented policy entry and is excluded from exact legacy parity counts. |

## Implementation Checklist

1. Create `07_Test_Instance_and_Comparisons/aspect_parity` folder structure. Done.
2. Create a Forge 1.12.2 diagnostic addon that writes `thaumcraft_1_12_aspects.json`. Done for the first server-side pass.
3. Generate the first `legacy_stack_manifest.json` from the 1.12 runtime. Deferred; current pass embeds comparison keys directly in dump entries.
4. Create `legacy_to_modern_stack_map.json` for direct equivalents, metadata splits, and component equivalents. Done for the first mapped id/meta pass.
5. Add a debug-only NeoForge exporter that writes `thaumcraft_1_21_aspects.json`. Done through `-PtcAspectDump=true`.
6. Write `compare_aspect_dumps.py` to produce `aspect_diff.json` and `aspect_diff.md`. Done.
7. Fix all `PORT_GAP`, `AMOUNT_DIFF`, `ASPECT_SET_DIFF`, and unexpected `NULL_EMPTY_DIFF` entries before connecting aspects to scanning/research/aura/essentia gameplay.
8. Keep tooltip screenshots as a final visual check only after API dumps pass.

## Mapped Runtime Pass

The current implemented pass is stored under `07_Test_Instance_and_Comparisons/aspect_parity`.

| Metric | Count |
|---|---:|
| Legacy 1.12 dump entries | `1798` |
| Modern 1.21 dump entries | `1987` |
| Comparable keys after mapping | `1139` |
| Identical comparable keys | `1139` |
| Legacy-to-modern mapped parity keys | `283` |
| Order-only diffs | `0` |
| Amount diffs | `0` |
| Aspect-set diffs | `0` |
| Null/empty diffs | `0` |
| Result-kind diffs | `0` |

This proves the dump pipeline and mapping policy work for the current comparable set. The next improvement is not broader mapping by itself; it is keeping this `0` real-gap state while new Thaumcraft ids, custom recipe systems, entity aspects, and stateful stacks are ported.
