# Shobie 1.20.1 Arcane Recipe Import

Date: 2026-06-13
Branch: `codex/experiment-shobie-1-20-merge`

## Scope

This pass imports Shobie arcane workbench JSON only after translating it into the current NeoForge 1.21.1 port serializers:

- `thaumcraft:arcane_workbench_shaped` -> `thaumcraft:arcane_shaped`
- `thaumcraft:arcane_workbench_shapeless` -> `thaumcraft:arcane_shapeless`
- Shobie result `item` fields -> Minecraft 1.21 `id` fields
- Shobie `crystals`/`aspects` maps -> ordered crystal cost arrays
- Forge 1.20 style tags -> current `c:` tags or direct vanilla items where the old tag represented a single vanilla item

The migration guide rule used here is role-based migration: Shobie data is treated as a reference/source of intent, while our current registry ids, serializer contracts, and 1.12-style names remain authoritative. No Shobie Java logic is imported in this pass.

## Summary

| Status | Count |
| --- | ---: |
| Imported | 56 |
| Already present | 22 |
| Deferred | 1 |

Full catalog: `06_docs/shobie_1_20_arcane_recipe_catalog.csv`.

## Imported Recipes

- `alembic.json` -> `thaumcraft:alembic`
- `arcane_ear.json` -> `thaumcraft:arcane_ear`
- `arcane_lamp.json` -> `thaumcraft:lamp_arcane`
- `bellows.json` -> `thaumcraft:bellows`
- `brain_box.json` -> `thaumcraft:brain_box`
- `caster_advanced.json` -> `thaumcraft:caster_advanced`
- `centrifuge.json` -> `thaumcraft:centrifuge`
- `condenser.json` -> `thaumcraft:condenser`
- `condenser_lattice.json` -> `thaumcraft:condenser_lattice`
- `dioptra.json` -> `thaumcraft:dioptra`
- `essentia_transport_input.json` -> `thaumcraft:essentia_input`
- `essentia_transport_output.json` -> `thaumcraft:essentia_output`
- `focus_pouch.json` -> `thaumcraft:focus_pouch`
- `grapple_gun_spool.json` -> `thaumcraft:grapple_gun_spool`
- `grapple_gun_tip.json` -> `thaumcraft:grapple_gun_tip`
- `hungry_chest.json` -> `thaumcraft:hungry_chest`
- `inlay.json` -> `thaumcraft:inlay`
- `jar_void.json` -> `thaumcraft:jar_void`
- `levitator.json` -> `thaumcraft:levitator`
- `matrix_cost.json` -> `thaumcraft:matrix_cost`
- `matrix_speed.json` -> `thaumcraft:matrix_speed`
- `metal_alchemical.json` -> `thaumcraft:metal_alchemical`
- `metal_alchemical_advanced.json` -> `thaumcraft:metal_alchemical_advanced`
- `mind_clockwork.json` -> `thaumcraft:brain_clockwork`
- `pattern_crafter.json` -> `thaumcraft:pattern_crafter`
- `paving_barrier.json` -> `thaumcraft:paving_stone_barrier`
- `paving_travel.json` -> `thaumcraft:paving_stone_travel`
- `pedestal_ancient.json` -> `thaumcraft:pedestal_ancient`
- `pedestal_arcane.json` -> `thaumcraft:pedestal_arcane`
- `pedestal_eldritch.json` -> `thaumcraft:pedestal_eldritch`
- `potion_sprayer.json` -> `thaumcraft:potion_sprayer`
- `recharge_pedestal.json` -> `thaumcraft:recharge_pedestal`
- `redstone_relay.json` -> `thaumcraft:redstone_relay`
- `robe_boots.json` -> `thaumcraft:cloth_boots`
- `robe_chest.json` -> `thaumcraft:cloth_chest`
- `robe_legs.json` -> `thaumcraft:cloth_legs`
- `seal_blank.json` -> `thaumcraft:blank_seal`
- `smelter_aux.json` -> `thaumcraft:smelter_aux`
- `smelter_thaumium.json` -> `thaumcraft:smelter_thaumium`
- `smelter_vent.json` -> `thaumcraft:smelter_vent`
- `smelter_void.json` -> `thaumcraft:smelter_void`
- `spa.json` -> `thaumcraft:spa`
- `stabilizer.json` -> `thaumcraft:stabilizer`
- `tube_buffer.json` -> `thaumcraft:tube_buffer`
- `tube_filter.json` -> `thaumcraft:tube_filter`
- `tube_oneway.json` -> `thaumcraft:tube_oneway`
- `tube_restrict.json` -> `thaumcraft:tube_restrict`
- `tube_valve.json` -> `thaumcraft:tube_valve`
- `resonator.json` -> `thaumcraft:resonator`
- `sanity_checker.json` -> `thaumcraft:sanity_checker`
- `tube.json` -> `thaumcraft:tube_normal`
- `turret_crossbow.json` -> `thaumcraft:turret_placer_basic`
- `turret_crossbow_advanced.json` -> `thaumcraft:turret_placer_advanced`
- `vis_battery.json` -> `thaumcraft:vis_battery`
- `vis_generator.json` -> `thaumcraft:vis_generator`
- `warded_jar.json` -> `thaumcraft:jar_normal`

## Already Present Or Deferred Recipes

- `arcane_workbench.json` -> `thaumcraft:arcane_workbench`: already_has_recipe_for_result:research_bridge/arcane_workbench.json
- `caster_basic.json` -> `thaumcraft:caster_basic`: already_has_recipe_for_result:caster_basic.json
- `crucible.json` -> `thaumcraft:crucible`: already_has_recipe_for_result:research_bridge/crucible.json
- `enchanted_fabric.json` -> `thaumcraft:fabric`: already_has_recipe_for_result:enchantedfabric.json
- `filter.json` -> `thaumcraft:filter`: already_has_recipe_for_result:filter.json
- `goggles.json` -> `thaumcraft:goggles`: already_has_recipe_for_result:goggles.json
- `infusion_matrix.json` -> `thaumcraft:infusion_matrix`: already_has_recipe_for_result:infusionmatrix.json,research_bridge/infusion_matrix.json
- `mechanism_complex.json` -> `thaumcraft:mechanism_complex`: already_has_recipe_for_result:mechanism_complex.json
- `mechanism_simple.json` -> `thaumcraft:mechanism_simple`: already_has_recipe_for_result:mechanism_simple.json
- `mirrored_glass.json` -> `thaumcraft:mirrored_glass`: already_has_recipe_for_result:mirrorglass.json
- `morphic_resonator.json` -> `thaumcraft:morphic_resonator`: already_has_recipe_for_result:morphicresonator.json
- `primal_charm.json` -> `thaumcraft:primal_charm`: unresolved_legacy_item:thaumcraft:balanced_shard
- `research_table.json` -> `thaumcraft:research_table`: already_has_recipe_for_result:research_bridge/research_table.json
- `scribing_tools.json` -> `thaumcraft:scribing_tools`: already_has_recipe_for_result:scribingtoolscraft2.json,scribingtoolsrefill.json,research_bridge/scribing_tools.json;unresolved_legacy_item:thaumcraft:shard_order
- `smelter_basic.json` -> `thaumcraft:smelter_basic`: already_has_recipe_for_result:essentiasmelter.json,research_bridge/smelter_basic.json
- `thaumium_axe.json` -> `thaumcraft:thaumium_axe`: already_has_recipe_for_result:thaumium_axe.json
- `thaumium_hoe.json` -> `thaumcraft:thaumium_hoe`: already_has_recipe_for_result:thaumium_hoe.json
- `thaumium_pick.json` -> `thaumcraft:thaumium_pick`: already_has_recipe_for_result:thaumium_pick.json
- `thaumium_shovel.json` -> `thaumcraft:thaumium_shovel`: already_has_recipe_for_result:thaumium_shovel.json
- `thaumium_sword.json` -> `thaumcraft:thaumium_sword`: already_has_recipe_for_result:thaumium_sword.json
- `thaumometer.json` -> `thaumcraft:thaumometer`: already_has_recipe_for_result:thaumometer.json
- `vis_resonator.json` -> `thaumcraft:vis_resonator`: already_has_recipe_for_result:vis_resonator.json
- `workbench_charger.json` -> `thaumcraft:arcane_workbench_charger`: already_has_recipe_for_result:workbenchcharger.json

## Remaining Deferred Reason Counts

- unresolved_legacy_item:thaumcraft:balanced_shard: 1

## Notes

- Existing port recipes were not overwritten when a recipe for the same output already existed.
- `thaumcraft:nitor` was added as an item tag containing the currently registered nitor colors, because Shobie uses it as an arcane ingredient tag.
- Legacy meta families such as `thaumcraft:nugget`, old shard variants, and exact primordial pearl metadata still need a dedicated legacy-to-modern item model before those recipes should be enabled.
- Imported blocks/items are still placeholder registry entries unless a dedicated block entity, menu, renderer, or gameplay class already existed in the port.
