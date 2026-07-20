# Thaumatorium Behavior Audit

Runtime checks for the first NeoForge Thaumatorium slice against TC6 legacy machine semantics.

## Summary

| Check | Result |
|---|---:|
| Passed | 19 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| thaumatorium_block_item_is_real_block_item | PASS | item=thaumcraft:thaumatorium |
| thaumatorium_block_entities_registered | PASS | bottom/top block entity types valid |
| legacy_placeholder_importer_exporter_not_player_facing_blocks | PASS | importer/exporter retained as non-block reference aliases |
| runtime_thaumatorium_block_entities_created | PASS | bottom=true, top=true |
| thaumatorium_heat_source_is_two_blocks_below | PASS | heat block=Block{minecraft:lava} |
| thaumatorium_without_crucible_heat_source_is_cold | PASS | heat block=Block{minecraft:stone} |
| thaumatorium_front_face_does_not_connect | PASS | front=NORTH, east=true, output=false |
| thaumatorium_capabilities_are_sided_and_top_delegates | PASS | bottomTransport=true, frontTransport=false, topTransport=true, itemSlots=1/1 |
| alumentum_crucible_recipe_loaded_for_fixture | PASS | recipe=thaumcraft:alumentum |
| thaumatorium_recipe_list_is_research_gated | PASS | beforeResearch=0 |
| thaumatorium_recipe_list_exposes_known_catalyst_recipe | PASS | afterResearch=1 |
| thaumatorium_menu_toggle_selects_and_removes_recipe | PASS | selected=true, removed=true, count=0 |
| thaumatorium_suction_is_128_for_first_missing_aspect | PASS | aspect=ignis, suction=128 |
| thaumatorium_top_reports_same_suction | PASS | topSuction=128 |
| thaumatorium_manual_input_clamps_to_current_recipe_missing_amount | PASS | accepted=10, required=10 |
| thaumatorium_pulls_one_point_from_adjacent_tube | PASS | aspect=perditio, before=0, after=1, tube=0 |
| thaumatorium_redstone_power_pauses_filling | PASS | aspect=perditio, before=1, after=1 |
| thaumatorium_completion_consumes_one_catalyst_resets_essentia_and_ejects_output | PASS | catalysts=1, essentia=0, itemEntities=0>1 |
| thaumatorium_mnemonic_matrix_placeholder_adds_two_recipe_slots | PASS | base=1, upgraded=3 |

## Boundary

- Covers the server-owned two-block machine foundation, not final GUI/screen parity.
- Uses the loaded `thaumcraft:alumentum` crucible recipe as a stable catalyst/aspect fixture.
- Verifies legacy-relevant heat, redstone, suction, input-only transport, top delegation and craft completion.
- Mnemonic Matrix support is currently mapped to the existing `golem_builder` placeholder until the real brain box block is ported.
