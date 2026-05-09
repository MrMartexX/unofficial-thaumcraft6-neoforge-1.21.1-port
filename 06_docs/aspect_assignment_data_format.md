# Thaumcraft 6 Aspect Assignment Data Format

Last reviewed branch: `main`
Target module: `05_neoforge_port`

## Purpose

This document defines the reload-safe data format for item/tag aspect assignments in the NeoForge 1.21.1 port.

Guide rule applied: large legacy data sets should move to data-driven resources rather than hardcoded registration code. This format covers exact item ids, item tags, and complex item/tag extras used by the generated cache. It does not implement entity assignments, aura, research, scanning, essentia, or gameplay effects.

## Resource Location

Aspect assignment files live under:

```text
data/<namespace>/aspect_assignments/<path>.json
```

The bundled baseline files are:

```text
05_neoforge_port/src/main/resources/data/thaumcraft/aspect_assignments/current_registered.json
05_neoforge_port/src/main/resources/data/thaumcraft/aspect_assignments/current_registered_runtime_parity.json
05_neoforge_port/src/main/resources/data/thaumcraft/aspect_assignments/legacy_vanilla_core.json
05_neoforge_port/src/main/resources/data/thaumcraft/aspect_assignments/legacy_vanilla_materials.json
05_neoforge_port/src/main/resources/data/thaumcraft/aspect_assignments/legacy_vanilla_modern_exact.json
05_neoforge_port/src/main/resources/data/thaumcraft/aspect_assignments/legacy_vanilla_modern_manual.json
05_neoforge_port/src/main/resources/data/thaumcraft/aspect_assignments/legacy_vanilla_complex.json
05_neoforge_port/src/main/resources/data/thaumcraft/aspect_assignments/legacy_vanilla_runtime_parity.json
```

Files are loaded by a server-side reload listener registered through `AddReloadListenerEvent`.

## File Shape

```json
{
  "replace": false,
  "assignments": [
    {
      "item": "thaumcraft:amber",
      "aspects": [
        { "aspect": "thaumcraft:vinculum", "amount": 10 },
        { "aspect": "thaumcraft:vitreus", "amount": 10 }
      ]
    },
    {
      "tag": "c:gems/amber",
      "aspects": [
        { "aspect": "thaumcraft:vinculum", "amount": 10 },
        { "aspect": "thaumcraft:vitreus", "amount": 10 }
      ]
    }
  ]
}
```

## Rules

| Field | Rule |
|---|---|
| `replace` | Optional. When true, clears assignments loaded from lower-sorted files before reading this file. Keep false for bundled baseline files. |
| `overrides` | Optional. When true, later assignments in that file may replace an already loaded target of the same kind. This is used only by bundled runtime parity layers to preserve final 1.12 dump values for flattened legacy-equivalent vanilla ids and currently registered Thaumcraft ids. |
| `assignments` | Required array. Each entry must define exactly one of `item`, `tag`, `complex_item`, or `complex_tag`. |
| `item` | Exact item id. Exact item assignments have lookup priority over tag assignments. |
| `tag` | Item tag id. This is item-only for this first format. Block tag membership may mirror legacy OreDictionary keys, but lookup is from `ItemStack`. |
| `complex_item` | Exact item id whose value is added to recipe-derived generated aspects, matching legacy `registerComplexObjectTag` intent. |
| `complex_tag` | Item tag whose value is added to recipe-derived generated aspects. |
| `aspects` | Required ordered array. Order is preserved because legacy `AspectList` order is observable in parity tests. Empty arrays are allowed only for explicit dump-proven exact runtime parity values, such as legacy empty `AspectList` results. |
| `aspect` | Full Thaumcraft aspect id, e.g. `thaumcraft:terra`. Non-Thaumcraft namespaces are rejected for now. |
| `amount` | Positive integer. Zero and negative values are rejected. |

Duplicate item or tag assignment targets are rejected during reload unless the file explicitly sets `overrides: true`. This keeps normal datapack behavior deterministic while still allowing the bundled final-runtime parity layer to replace earlier seed/manual values after the legacy dump proves the resolved 1.12 value.

## Current Loader Classes

| Class | Role |
|---|---|
| `TCAspectAssignments` | Active read-only lookup service with priority `legacy exclusion/component special > exact id > tag > generated > stack bonuses`; also exposes complex extras to the generator. |
| `TCAspectAssignmentParser` | Parses bundled and reload data into deterministic maps. |
| `TCAspectAssignmentReloadListener` | Hooks `SimpleJsonResourceReloadListener` into server reload. |
| `TCAspectParityValidator` | Validates the active loaded assignments against the audited legacy baseline. |
| `TCAspectReloadValidator` | Validates real loaded tag membership after server data tags update. |
| `TCAspectStackRules` | Applies legacy no-aspect exclusions, potion component lookup, equipment/tool/dye bonuses, and enchantment/stored-enchantment bonuses. |

## Current Scope

The current bundled files contain 672 exact direct assignments, 46 audited item-tag assignments, and 32 complex exact assignments. `current_registered.json` owns normally authored currently registered Thaumcraft ids. `current_registered_runtime_parity.json` owns dump-derived final values for already registered Thaumcraft ids whose runtime result differs from a simple source-line or generated fallback reading, including exact empty-list registrations. `legacy_vanilla_core.json` and `legacy_vanilla_modern_exact.json` own direct vanilla seeds from legacy and the rough modern-id audit. `legacy_vanilla_materials.json` owns vanilla material bridges for ores, gems, ingots, dusts, broad base materials, `blockGlass`, and ore-derived 1.21 raw materials. `legacy_vanilla_modern_manual.json` owns audited 1.21-only vanilla ids so the current server coverage audit stays at `1230/1230` assignable vanilla item ids. `legacy_vanilla_runtime_parity.json` owns dump-derived final values for legacy-equivalent plain vanilla stacks where 1.12 metadata, generated recipe, wildcard, or complex lookup order resolves differently than a simple seed/manual assignment. Spawn eggs, firework star/rocket, and infested blocks are intentionally excluded because legacy Thaumcraft 6 gave their comparable stacks no aspects.

Generated aspects are runtime cache entries, not static JSON values. Do not add `plankWood`, `slabWood`, stairs, amber block, amber brick, or fabric values as guessed static data when they should be derived from recipes.
