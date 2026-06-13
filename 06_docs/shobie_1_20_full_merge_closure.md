# Shobie 1.20.1 Full Safe Merge Closure

Date: 2026-06-14
Branch: `codex/experiment-shobie-1-20-merge`

## Decision

The Shobie 1.20.1 repository has now been exhausted as a safe bulk-merge
source for the current NeoForge 1.21.1 port.

The current port remains authoritative. Shobie is retained as a secondary
reference for recipes, tags, behavior names and coverage checks. The migration
guide rule applied here is still role-based migration: do not copy Forge
1.20.1 Java or old resource paths directly into the NeoForge 1.21.1 codebase.

Unsafe addendum: after the safe closure, the experimental branch also imports
the remaining recipe catalog rows under separate `unsafe_shobie` recipe ids and
copies the full Shobie Java/resource tree to `src/shobieReference`. This does
not change the rule above: Shobie Java/worldgen/resources remain reference-only
unless a focused subsystem migration promotes them later.

## Source Size

| Source area | Count | Merge treatment |
|---|---:|---|
| Shobie Java files | 744 | Reference only. Direct import is blocked by loader/API drift and subsystem design requirements. |
| Shobie resource/data files | 2447 | Imported only when the file role is safe and can be translated to current ids and 1.21 resource paths. |

## Runtime Data Imported Or Confirmed

| Area | Result |
|---|---|
| Damage types and damage tags | Imported into modern `data/thaumcraft/damage_type` and `data/minecraft/tags/damage_type`. |
| Thaumcraft tag taxonomy | Safe registered subsets translated from old plural paths and `forge:` tags into modern `thaumcraft:` and `c:` tags. |
| Vanilla wood/common tags | Greatwood/Silverwood block and item planks, slabs, stairs, burnable logs, storage blocks, void-metal aliases and quartz/root tag closure are present. |
| Vanilla crafting/shapeless/smelting recipes | All Shobie rows are either already present or imported/translated; no vanilla recipe rows remain deferred in the catalog. |
| Arcane recipes | 56 safe rows translated to current `thaumcraft:arcane_shaped` / `thaumcraft:arcane_shapeless`; 22 already covered by existing current recipes; 1 unsafe edge row now lives under `unsafe_shobie/arcane`. |
| Crucible recipes | 41 legacy-correct translated recipes load through the current `thaumcraft:crucible` serializer; 14 unsafe seal/edge rows now load under `unsafe_shobie/crucible` with NBT flattened. |
| Infusion recipes | 38 legacy-correct translated recipes load through the current `thaumcraft:infusion` serializer; 24 unsafe Shobie rows now load under `unsafe_shobie/infusion`, including flattened seal variants and alternate equipment/device recipes. |

## Recipe Catalog Status

Full catalog: `06_docs/shobie_1_20_recipe_catalog.csv`.

| Recipe type / decision | Count |
|---|---:|
| `minecraft:crafting_shaped`, already present or imported | 51 |
| `minecraft:crafting_shapeless`, already present or imported | 13 |
| `minecraft:smelting`, already present or imported | 8 |
| `thaumcraft:arcane_workbench_shaped`, imported translated current serializer | 48 |
| `thaumcraft:arcane_workbench_shapeless`, imported translated current serializer | 5 |
| `thaumcraft:arcane_workbench_shaped`, imported legacy corrected | 3 |
| `thaumcraft:arcane_workbench_shaped`, imported unsafe translated current serializer | 1 |
| `thaumcraft:arcane_workbench_shaped`, already present or imported | 20 |
| `thaumcraft:arcane_workbench_shapeless`, already present or imported | 2 |
| `thaumcraft:crucible`, already present or imported | 2 |
| `thaumcraft:crucible`, imported legacy corrected | 41 |
| `thaumcraft:crucible`, imported unsafe translated current serializer | 14 |
| `thaumcraft:infusion`, imported legacy corrected | 38 |
| `thaumcraft:infusion`, imported unsafe translated current serializer | 24 |

No recipe catalog rows remain deferred. The unsafe rows are loaded only as
experimental data and still require subsystem-specific behavior work before they
can be considered parity complete.

## Resource Delta Status

Full catalog: `06_docs/shobie_1_20_resource_delta.csv`.

| Status / decision | Count |
|---|---:|
| identical, already present | 1408 |
| different, keep current 1.21 or legacy asset | 638 |
| missing, recipe catalog managed | 270 |
| missing, catalog only worldgen later | 54 |
| missing, imported translated tag compat | 45 |
| missing, catalog only loot table singular path | 13 |
| identical, imported runtime phase 2 | 6 |
| missing, defer unregistered or worldgen | 7 |
| missing, defer legacy access transformer reference | 1 |
| missing, reference only changelog | 1 |

`recipe_catalog_managed` rows are not unreviewed files; they are managed by the
recipe catalog and the current custom serializer import notes. Old plural
`loot_tables` paths are not copied because active block drops use modern
singular `data/*/loot_table` paths. Shobie worldgen/biome modifier files remain
deferred until the NeoForge 1.21.1 worldgen design exists.

## Not Copied On Purpose

| Area | Reason |
|---|---|
| Shobie Java classes | Forge 1.20.1 code is not source-compatible with NeoForge 1.21.1 and often encodes incomplete or placeholder behavior. Port subsystem roles instead. |
| Shobie assets that differ from current assets | Current adapted 1.21 resources and the imported TC6 legacy asset corpus remain authoritative. Do not overwrite by bulk copy. |
| Old plural `data/thaumcraft/recipes` and `loot_tables` paths | Minecraft 1.21.1 uses singular paths and the current serializers/resources are authoritative. |
| Worldgen/biome modifiers | Needs a focused NeoForge 1.21.1 design and parity pass. |
| Remaining crucible/infusion/arcane edge semantics | The unsafe rows are enabled as separate experimental recipe ids, but seal outputs with legacy NBT are flattened and behavior parity is not complete. |

## Remaining Shobie-Derived Work

| Remaining area | Count | Next safe step |
|---|---:|---|
| Arcane unsafe edge rows | 1 | `primal_charm` is present under `unsafe_shobie/arcane`; it still needs exact legacy recipe-path confirmation before being promoted. |
| Crucible unsafe rows | 14 | Mostly seal/NBT rows; active unsafe JSON flattens seal identity and must be revisited after real crucible behavior and seal DataComponents exist. |
| Infusion unsafe rows | 24 | Mostly seal/NBT, equipment/variant or Shobie-conflicting rows; active unsafe JSON loads, but real Infusion Matrix, pedestal, DataComponent and variant behavior are still missing. |
| Worldgen/biome data | 54 resource rows plus biome tags | Write worldgen design before converting features/biome modifiers. |
| Loot table singular-path review | 13 | Convert only for registered blocks that lack modern loot tables. |

This means the next step should not be another broad Shobie copy pass. The safe
and unsafe data-copy surfaces are both exhausted. The productive work is now a
focused behavior subsystem: real crucible, real infusion matrix/pedestals, seal
DataComponents, variant equipment/curio behavior, or worldgen, each with its own
parity checks.
