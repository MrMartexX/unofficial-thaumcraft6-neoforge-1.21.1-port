# Arcane Workbench Runtime Audit

| Check | Status | Detail |
|---|---|---|
| `arcane_and_wand_workbench_blocks_are_distinct` | `PASS` | arcane=TCArcaneWorkbenchBlock, wand=Block |
| `empty_workbench_resolves_empty` | `PASS` | kind=EMPTY |
| `crystal_slots_accept_only_matching_primal_aspects` | `PASS` | order=[aer, ignis, aqua, terra, ordo, perditio] |
| `can_spend_vis_simulation_does_not_drain` | `PASS` | vis=100.0 |
| `vis_resonator_missing_research_falls_back_to_empty_vanilla` | `PASS` | kind=EMPTY |
| `vis_resonator_missing_crystals_blocks_fallback` | `PASS` | kind=ARCANE_BLOCKED |
| `vis_resonator_wrong_crystal_aspect_blocks_recipe` | `PASS` | kind=ARCANE_BLOCKED |
| `vis_resonator_missing_vis_blocks_fallback` | `PASS` | kind=ARCANE_BLOCKED |
| `vis_resonator_resolves_when_requirements_met` | `PASS` | output=1 thaumcraft:vis_resonator |
| `vis_resonator_craft_consumes_matrix_crystals_and_vis` | `PASS` | vis=50.0 |
| `vanilla_fallback_ironplate_output_and_consumption` | `PASS` | vis=100.0 |

- Passed: `11`
- Failed: `0`
