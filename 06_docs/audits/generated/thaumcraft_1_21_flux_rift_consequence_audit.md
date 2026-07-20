# Flux Rift Consequence Audit

Runtime checks for the row-11 Flux Rift event/consequence slice after the initial entity
foundation. This covers the TC6 weighted event table, Wisp dependency foundation, Prime
Taint Seed event, infectious vis exhaustion event, rift-owned ROOT -> CLOUD -> FLUX
focus-cloud event execution, collapse effects and the Causality Collapser
rift-closing projectile.

## Summary

| Check | Result |
|---|---:|
| Passed | 21 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| infectious_vis_exhaust_effect_registered | PASS | effect=thaumcraft:infectious_vis_exhaust |
| wisp_registered_with_legacy_tracking | PASS | entity=thaumcraft:wisp |
| focus_cloud_registered_with_legacy_tracking | PASS | entity=thaumcraft:focus_cloud |
| rift_event_table_matches_legacy_weights_costs | PASS | events=[FluxEventEntry[event=0, weight=50, cost=5, nearTaintAllowed=true, owner=wisp], FluxEventEntry[event=1, weight=10, cost=0, nearTaintAllowed=false, owner=taint_seed_prime], FluxEventEntry[event=2, weight=20, cost=10, nearTaintAllowed=true, owner=infectious_vis_exhaust], FluxEventEntry[event=3, weight=20, cost=10, nearTaintAllowed=true, owner=focus_flux_cloud], FluxEventEntry[event=4, weight=1, cost=0, nearTaintAllowed=true, owner=collapse]] |
| causality_collapser_item_registered_with_legacy_id_and_stack | PASS | item=thaumcraft:causality_collapser, maxStack=16 |
| causality_collapser_entity_registered_with_legacy_tracking | PASS | entity=thaumcraft:causality_collapser |
| causality_collapser_projectile_constants_match_legacy | PASS | speed=0.800000011920929, explosion=2.0, range=3.0 |
| causality_collapser_client_trail_contract_matches_legacy_constants | PASS | samples=3, fireMote alpha/scale=0.5/4.0, generic start=448, frames=8, age=8 |
| focus_cloud_flux_contract_matches_legacy_constants | PASS | radius=1..3, tick=5, cooldown=2000ms, fluxDamage=2.0 |
| wisp_dynamic_type_aspects_match_legacy | PASS | type=vitium |
| rift_event_0_spawns_real_wisp_and_adds_stability_cost | PASS | result=applied, before=0, after=1, stability=-25.0 |
| rift_event_1_spawns_prime_seed_boost_pollutes_and_discards_rift | PASS | result=applied, seeds=0->1, boost=14, fluxDelta=7.0 |
| rift_event_2_applies_infectious_vis_exhaust_and_cost | PASS | result=applied, hasEffect=true, stability=-40.0 |
| rift_event_3_no_player_is_legacy_noop_without_stability_cost | PASS | result=no_player_noop, rawResult=failed, stability=-50.0 |
| rift_event_3_spawns_legacy_flux_focus_cloud_contract | PASS | spawned=true, count=0->1, radius=3.0, duration=8 |
| focus_cloud_tick_applies_flux_damage_and_cooldown | PASS | health=20.0->18.0->18.0, executions=2, hits=2 |
| rift_event_3_focus_cloud_keeps_legacy_no_stability_cost | PASS | spawned=true, stability=-50.0, clouds=2->3 |
| rift_event_4_sets_collapse_without_stability_cost | PASS | collapse=true, stability=-5.0 |
| rift_complete_collapse_applies_unstable_weakness_effect_and_discards | PASS | alive=false, weakness=true |
| rift_complete_collapse_applies_very_unstable_flux_taint_fallthrough | PASS | fluxTaint=true, weakness=true |
| causality_collapser_collapses_rifts_in_legacy_aabb_range | PASS | collapsed=2, center=true, edge=true, far=false |

## Boundary

- Implemented: legacy rift weighted event table `50/10/20/20/1`, near-taint filter, Wisp spawn event, Prime Taint Seed boost/pollution event, infectious vis exhaustion event, rift-owned ROOT -> CLOUD -> FLUX event, collapse aura/explosion/drop/effect path, Causality Collapser throw/explosion/rift-collapse AABB and dynamic Wisp aspect assignment.
- Implemented dependency: `thaumcraft:wisp` is registered with TC6 tracking values and minimal server state needed by rifts; full Wisp AI/model/particles remain entity/render row work.
- Boundary: event 3 now executes the rift-owned focus cloud path. Broad player-authored focus projectile/cloud/mine gameplay and measured cloud/impact particle pixel parity remain focus/render row work.
