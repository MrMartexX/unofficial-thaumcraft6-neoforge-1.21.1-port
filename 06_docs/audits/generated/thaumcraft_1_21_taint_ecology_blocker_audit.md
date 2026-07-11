# Taint Ecology Blocker Audit

Runtime checks for the row-11 TaintSeed/TaintHelper slice: seed radius bookkeeping,
registered taint terrain blocks, server spread transforms and resource modernization.

## Summary

| Check | Result |
|---|---:|
| Passed | 15 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| taint_terrain_blocks_and_items_registered | PASS | terrain ids registered with legacy names |
| taint_seed_entity_types_registered | PASS | seed=thaumcraft:taint_seed, prime=thaumcraft:taint_seed_prime |
| taint_config_defaults_match_legacy | PASS | area=32, rate=100.0 |
| taint_seed_area_and_prime_contract | PASS | seedArea=1, primeArea=2 |
| taint_seed_near_check_requires_live_entity | PASS | nearLiveSeed=true |
| taint_seed_near_check_prunes_stale_entries | PASS | nearAfterDiscard=false |
| taint_seed_spawn_rule_blocks_nearby_duplicate | PASS | prime still occupies spread-area exclusion |
| spread_surface_target_becomes_taint_fibre | PASS | block=thaumcraft:taint_fibre |
| spread_dirt_target_becomes_taint_soil | PASS | block=thaumcraft:taint_soil |
| spread_stone_target_becomes_taint_rock | PASS | block=thaumcraft:taint_rock |
| spread_log_target_becomes_taint_log_preserving_axis | PASS | block=thaumcraft:taint_log, axis=x |
| flux_taint_heals_tainted_and_damages_non_undead | PASS | seedHealth=11.0, cowBefore=10.0, cowAfter=9.0, zombieHealth=10.0 |
| tainted_seed_accepts_flux_taint_effect_without_damage_path | PASS | effectPresent=true |
| taint_resources_use_modern_model_and_texture_paths | PASS | checked=8 |
| taint_feature_shapes_keep_legacy_directional_bounds | PASS | up=AABB[0.125, 0.0, 0.125] -> [0.875, 0.375, 0.875], north=AABB[0.125, 0.125, 0.625] -> [0.875, 0.875, 1.0] |

## Boundary

- Implemented: `taint_seed` and `taint_seed_prime` entity registration, attributes, boost save/load, seed-radius map, near/edge checks, flux-gated spread loop and Flux Taint healing for tainted mobs.
- Implemented: `taint_crust`, `taint_soil`, `taint_rock`, `taint_geyser`, `taint_log` and `taint_feature` registration with BlockItems, creative visibility and modern block/item models.
- Implemented: deterministic validation for TC6-style spread target categories: surface fibre, dirt -> taint soil, stone -> taint rock, log -> taint log and log-adjacent leaves -> taint feature/fibre.
- Deferred: full TaintCrawler/TaintSwarm/Taintacle AI and renderers, FallingTaint crust physics, GORE sound parity and exact animated TaintSeed model parity.
