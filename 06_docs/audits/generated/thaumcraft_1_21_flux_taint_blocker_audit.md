# Flux Goo and Taint Fibre Blocker Audit

Runtime checks for the row-11 finite taint world-mutation slice: Flux Goo level-zero
decay can now produce Taint Fibre, and Taint Fibre keeps the TC6 state, shape and effect contract.

## Summary

| Check | Result |
|---|---:|
| Passed | 13 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| taint_fibre_block_and_item_registered_with_legacy_id | PASS | block=thaumcraft:taint_fibre, item=thaumcraft:taint_fibre |
| flux_goo_block_item_registered_for_world_slice | PASS | item=thaumcraft:flux_goo |
| taint_fibre_default_state_has_all_legacy_false_properties | PASS | propertyCount=10, defaultFalse=true |
| taint_fibre_face_shapes_use_legacy_0_05_block_thickness | PASS | allFacesBounds=AABB[0.0, 0.0, 0.0] -> [1.0, 1.0, 1.0] |
| taint_fibre_growth_shapes_and_light_match_legacy | PASS | growth1=AABB[0.1, 0.0, 0.1] -> [0.9, 0.4, 0.9] |
| taint_fibre_actual_state_uses_pos_seeded_growth_and_support_faces | PASS | pos=BlockPos{x=192, y=74, z=283}, q=6, down=true, growth3=true |
| flux_goo_level_zero_alternate_result_now_resolves_taint_fibre | PASS | block=thaumcraft:taint_fibre, down=true |
| taint_fibre_replacement_is_non_full_collision_shape | PASS | bounds=AABB[0.0, 0.0, 0.0] -> [1.0, 0.05, 1.0] |
| taint_fibre_walk_applies_flux_taint_to_living_non_undead_only | PASS | playerApplied=true, undeadApplied=false |
| taint_seed_dependent_spread_is_explicitly_deferred | PASS | nearSeed=false, deferred=TaintSeed registry/entity and full TaintHelper spread |
| taint_fibre_blockstate_uses_modern_block_model_paths | PASS | blockstateLength=1305 |
| taint_fibre_models_use_modern_block_texture_paths | PASS | blockModelLength=359, itemModelLength=116 |
| flux_goo_block_item_has_modern_item_model | PASS | itemModelLength=106 |

## Boundary

- Implemented: Taint Fibre registration, block item, creative visibility, 10-property legacy state, deterministic growth, face/growth shapes, light levels, walk taint and Flux Goo level-zero alternate result.
- Implemented: resource path modernization for Taint Fibre multipart blockstate and growth models.
- Deferred from this earlier Flux Goo/Taint Fibre audit: later TaintSeed/terrain ecology and taint mob server-foundation hooks are covered by their focused audits; FallingTaint crust physics and final taint visuals remain separate blockers.
- Until TaintSeed exists, Taint Fibre intentionally withers on random tick just like legacy fibres outside seed range.
