# Arcane Workbench Runtime Audit

| Check | Status | Detail |
|---|---|---|
| `arcane_and_wand_workbench_blocks_are_distinct` | `PASS` | arcane=TCArcaneWorkbenchBlock, wand=Block |
| `workbench_charger_block_survives_above_arcane_or_wand_workbench` | `PASS` | block=TCArcaneWorkbenchChargerBlock |
| `empty_workbench_resolves_empty` | `PASS` | kind=EMPTY |
| `crystal_slots_accept_only_matching_primal_aspects` | `PASS` | order=[aer, ignis, aqua, terra, ordo, perditio] |
| `can_spend_vis_simulation_does_not_drain` | `PASS` | vis=100.0 |
| `workbench_without_charger_uses_current_chunk_vis` | `PASS` | available=30 |
| `workbench_charger_sums_nine_chunk_vis` | `PASS` | available=90 |
| `workbench_charger_spends_vis_across_nine_chunks` | `PASS` | available=40 |
| `vis_resonator_missing_research_falls_back_to_empty_vanilla` | `PASS` | kind=EMPTY |
| `vis_resonator_missing_crystals_blocks_fallback` | `PASS` | kind=ARCANE_BLOCKED |
| `vis_resonator_wrong_crystal_aspect_blocks_recipe` | `PASS` | kind=ARCANE_BLOCKED |
| `vis_resonator_missing_vis_blocks_fallback` | `PASS` | kind=ARCANE_BLOCKED |
| `missing_vis_ghost_output_is_not_craftable` | `PASS` | slot=1 thaumcraft:vis_resonator |
| `vis_resonator_resolves_when_requirements_met` | `PASS` | output=1 thaumcraft:vis_resonator |
| `vis_resonator_craft_consumes_matrix_crystals_and_vis` | `PASS` | vis=50.0 |
| `vis_discount_reduces_arcane_cost` | `PASS` | cost=47 |
| `discounted_arcane_craft_drains_discounted_vis` | `PASS` | vis=53.0 |
| `vanilla_fallback_ironplate_output_and_consumption` | `PASS` | vis=100.0 |
| `menu_feedback_reports_arcane_cost_and_aura` | `PASS` | cost=50 |
| `menu_feedback_reports_discounted_arcane_cost` | `PASS` | cost=47 |
| `menu_feedback_marks_missing_vis` | `PASS` | available=10 |
| `menu_feedback_marks_missing_crystals` | `PASS` | mask=5 |
| `menu_feedback_keeps_vanilla_fallback_costless` | `PASS` | kind=3 |

- Passed: `23`
- Failed: `0`
