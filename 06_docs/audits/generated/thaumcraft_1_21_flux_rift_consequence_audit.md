# Flux Rift Consequence Audit

Runtime checks for the row-11 Flux Rift event/consequence slice after the initial entity
foundation. This covers the TC6 weighted event table, Wisp dependency foundation, Prime
Taint Seed event, infectious vis exhaustion event and collapse effects. Focus-cloud execution
is explicitly left to the paused focus/projectile owner row.

## Summary

| Check | Result |
|---|---:|
| Passed | 11 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| infectious_vis_exhaust_effect_registered | PASS | effect=thaumcraft:infectious_vis_exhaust |
| wisp_registered_with_legacy_tracking | PASS | entity=thaumcraft:wisp |
| rift_event_table_matches_legacy_weights_costs | PASS | events=[FluxEventEntry[event=0, weight=50, cost=5, nearTaintAllowed=true, owner=wisp], FluxEventEntry[event=1, weight=10, cost=0, nearTaintAllowed=false, owner=taint_seed_prime], FluxEventEntry[event=2, weight=20, cost=10, nearTaintAllowed=true, owner=infectious_vis_exhaust], FluxEventEntry[event=3, weight=20, cost=10, nearTaintAllowed=true, owner=focus_flux_cloud], FluxEventEntry[event=4, weight=1, cost=0, nearTaintAllowed=true, owner=collapse]] |
| wisp_dynamic_type_aspects_match_legacy | PASS | type=vitium |
| rift_event_0_spawns_real_wisp_and_adds_stability_cost | PASS | result=applied, before=0, after=1, stability=-25.0 |
| rift_event_1_spawns_prime_seed_boost_pollutes_and_discards_rift | PASS | result=applied, seeds=0->1, boost=14, fluxDelta=7.0 |
| rift_event_2_applies_infectious_vis_exhaust_and_cost | PASS | result=applied, hasEffect=true, stability=-40.0 |
| rift_event_3_focus_cloud_is_explicitly_deferred_to_focus_owner | PASS | result=deferred_focus_cloud_owner |
| rift_event_4_sets_collapse_without_stability_cost | PASS | collapse=true, stability=-5.0 |
| rift_complete_collapse_applies_unstable_weakness_effect_and_discards | PASS | alive=false, weakness=true |
| rift_complete_collapse_applies_very_unstable_flux_taint_fallthrough | PASS | fluxTaint=true, weakness=true |

## Boundary

- Implemented: legacy rift weighted event table `50/10/20/20/1`, near-taint filter, Wisp spawn event, Prime Taint Seed boost/pollution event, infectious vis exhaustion event, collapse aura/explosion/drop/effect path and dynamic Wisp aspect assignment.
- Implemented dependency: `thaumcraft:wisp` is registered with TC6 tracking values and minimal server state needed by rifts; full Wisp AI/model/particles remain entity/render row work.
- Deferred by owner, not guessed: event 3 still requires real `EntityFocusCloud` / focus cloud execution before it can be made player-facing.
