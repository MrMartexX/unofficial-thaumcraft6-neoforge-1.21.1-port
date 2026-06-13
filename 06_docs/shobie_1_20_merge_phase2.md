# Shobie 1.20.1 Merge Phase 2

Date: 2026-06-12
Branch: `codex/experiment-shobie-1-20-merge`

## Scope

This phase keeps the current NeoForge 1.21.1 port as the base and treats Shobie 1.20.1 as a secondary source. The migration guide requires role-based migration and warns against direct cross-loader Java/data copying. Therefore this phase imports only safe runtime data and creates catalogs for everything else.

## Runtime Import Applied

Imported the four Shobie damage type data files into `05_neoforge_port/src/main/resources/data/thaumcraft/damage_type/`:

- `dissolve.json`
- `swarm.json`
- `taint.json`
- `tentacle.json`

Imported matching Minecraft damage type tags into `05_neoforge_port/src/main/resources/data/minecraft/tags/damage_type/`:

- `bypasses_armor.json`
- `witch_resistant_to.json`

Converted a first safe subset of Shobie vanilla recipes into Minecraft 1.21.1 `data/thaumcraft/recipe` JSON using current legacy-style ids:

- `stone_arcane_brick.json`
- `slab_ancient.json`
- `stairs_ancient.json`
- `slab_arcane_stone.json`
- `stairs_arcane.json`
- `slab_arcane_brick.json`
- `stairs_arcane_brick.json`
- `metal_brass.json`
- `brass_ingot_from_metal_brass.json`
- `ore_amber_smelting.json`
- `ore_cinnabar_smelting.json`
- `ore_quartz_smelting.json`
- `log_greatwood_charcoal.json`
- `log_silverwood_charcoal.json`

Not imported: deepslate ores, nuggets, void-metal ingot/nugget recipes, and any recipe requiring unregistered content.

## Resource Delta Summary

Full catalog: `06_docs/shobie_1_20_resource_delta.csv`.

| Status / Decision | Count |
| --- | ---: |
| different, keep_current_1_21_or_legacy_asset | 638 |
| identical, already_present | 1408 |
| identical, imported_runtime_phase2 | 6 |
| missing, catalog_only_loot_table_singular_path | 13 |
| missing, catalog_only_tag_path_review | 52 |
| missing, catalog_only_worldgen_later | 54 |
| missing, recipe_catalog_managed | 270 |
| missing, review | 2 |

Superseding closure after the later tag/recipe passes is recorded in
`06_docs/shobie_1_20_full_merge_closure.md`. The resource delta catalog now
distinguishes translated tag imports, reference-only files, legacy AT reference,
and unregistered/worldgen deferrals.

## Recipe Catalog Summary

Full catalog: `06_docs/shobie_1_20_recipe_catalog.csv`.

| Recipe Type / Decision | Count |
| --- | ---: |
| minecraft:crafting_shaped, already_present_or_imported | 51 |
| minecraft:crafting_shapeless, already_present_or_imported | 13 |
| minecraft:smelting, already_present_or_imported | 8 |
| thaumcraft:arcane_workbench_shaped, imported_translated_current_serializer | 48 |
| thaumcraft:arcane_workbench_shapeless, imported_translated_current_serializer | 5 |
| thaumcraft:arcane_workbench_shaped, imported_legacy_corrected | 3 |
| thaumcraft:arcane_workbench_shaped, already_present_or_imported | 20 |
| thaumcraft:arcane_workbench_shapeless, already_present_or_imported | 2 |
| thaumcraft:arcane_workbench_shaped, defer_behavior_or_unconfirmed_mapping | 1 |
| thaumcraft:crucible, imported_legacy_corrected | 27 |
| thaumcraft:crucible, defer_behavior_or_unconfirmed_mapping | 30 |
| thaumcraft:infusion, imported_legacy_corrected | 2 |
| thaumcraft:infusion, defer_behavior_or_unconfirmed_mapping | 60 |

No vanilla crafting, shapeless, or smelting Shobie recipe rows remain deferred
after the closure pass. The only deferred arcane row is `primal_charm`, blocked
on the unresolved `balanced_shard` identity; the other remaining deferred rows
are crucible or infusion behavior/identity rows.

## Legacy Naming Policy

Current port ids remain authoritative. Shobie ids that use newer or renamed forms must be mapped back to the legacy-style ids already used in the NeoForge port. This makes the port easier to compare against Thaumcraft 6 1.12.2.

| Shobie id | Current legacy-style id | Status |
| --- | --- | --- |
| thaumcraft:amber_ore | thaumcraft:ore_amber | registered |
| thaumcraft:ancient_stone | thaumcraft:stone_ancient | registered |
| thaumcraft:ancient_stone_slab | thaumcraft:slab_ancient | registered |
| thaumcraft:ancient_stone_stairs | thaumcraft:stairs_ancient | registered |
| thaumcraft:arcane_stone | thaumcraft:stone_arcane | registered |
| thaumcraft:arcane_stone_brick | thaumcraft:stone_arcane_brick | registered |
| thaumcraft:arcane_stone_brick_slab | thaumcraft:slab_arcane_brick | registered |
| thaumcraft:arcane_stone_brick_stairs | thaumcraft:stairs_arcane_brick | registered |
| thaumcraft:arcane_stone_slab | thaumcraft:slab_arcane_stone | registered |
| thaumcraft:arcane_stone_stairs | thaumcraft:stairs_arcane | registered |
| thaumcraft:brass_block | thaumcraft:metal_brass | registered |
| thaumcraft:cinnabar_ore | thaumcraft:ore_cinnabar | registered |
| thaumcraft:crystal_air | thaumcraft:crystal_aer | registered |
| thaumcraft:crystal_earth | thaumcraft:crystal_terra | registered |
| thaumcraft:crystal_entropy | thaumcraft:crystal_perditio | registered |
| thaumcraft:crystal_fire | thaumcraft:crystal_ignis | registered |
| thaumcraft:crystal_flux | thaumcraft:crystal_vitium | registered |
| thaumcraft:crystal_order | thaumcraft:crystal_ordo | registered |
| thaumcraft:crystal_water | thaumcraft:crystal_aqua | registered |
| thaumcraft:greatwood_log | thaumcraft:log_greatwood | registered |
| thaumcraft:greatwood_planks | thaumcraft:plank_greatwood | registered |
| thaumcraft:greatwood_slab | thaumcraft:slab_greatwood | registered |
| thaumcraft:greatwood_stairs | thaumcraft:stairs_greatwood | registered |
| thaumcraft:plate_brass | thaumcraft:brass_plate | registered |
| thaumcraft:plate_iron | thaumcraft:iron_plate | registered |
| thaumcraft:plate_thaumium | thaumcraft:thaumium_plate | registered |
| thaumcraft:plate_void | thaumcraft:void_plate | registered |
| thaumcraft:quartz_ore | thaumcraft:ore_quartz | registered |
| thaumcraft:silverwood_log | thaumcraft:log_silverwood | registered |
| thaumcraft:silverwood_planks | thaumcraft:plank_silverwood | registered |
| thaumcraft:silverwood_slab | thaumcraft:slab_silverwood | registered |
| thaumcraft:silverwood_stairs | thaumcraft:stairs_silverwood | registered |
| thaumcraft:thaumium_block | thaumcraft:metal_thaumium | registered |
| thaumcraft:void_block | thaumcraft:metal_void | registered |
| thaumcraft:void_metal_block | thaumcraft:metal_void | registered |

## Decisions

- Do not import Shobie Java directly: it is Forge 1.20.1-specific and mixes loader/client/common concerns.
- Do not import `data/thaumcraft/recipes` directly: Minecraft 1.21.1 uses the singular `recipe` path and our custom serializers must remain authoritative.
- Do not import Shobie `worldgen` or `forge/biome_modifier` directly: these need NeoForge 1.21.1 format review.
- Do not overwrite current assets with Shobie assets. Existing adapted 1.21/legacy assets stay authoritative.
- Preserve 1.12-style asset and registry names where our port already has them.
- Crucible recipe JSONs are now handled by the dedicated import note `06_docs/shobie_1_20_crucible_recipe_import.md`; imported recipes are data/serializer coverage only, not complete crucible gameplay.
- Infusion recipe JSONs are now handled by the dedicated import note `06_docs/shobie_1_20_infusion_recipe_import.md`; imported recipes are data/serializer/aspect-cache coverage only, not complete Infusion Matrix gameplay.

## Next Large Merge Step

Work through the remaining `candidate_convert_to_1_21` and `defer_unregistered_or_unmapped` rows only after the missing ids are intentionally registered or explicitly rejected. Arcane, crucible and infusion data layers are started; the next large merge target should be a real behavior slice with its own design and validation, not another blind Shobie data copy.

Closure update: broad safe Shobie data/resource merging is complete for this
branch. See `06_docs/shobie_1_20_full_merge_closure.md` for the final imported
and deferred counts.
