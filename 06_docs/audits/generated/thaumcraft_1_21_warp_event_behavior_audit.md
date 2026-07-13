# Warp Event Behavior Audit

Runtime parity checks for the first server-side TC6 WarpEvents slice.

## Summary

| Check | Result |
|---|---:|
| Passed | 31 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| legacy_interval_constants | PASS | warp=2000, gaze=20 |
| legacy_trigger_uses_sqrt_counter_threshold | PASS | counter16 sqrt=4 |
| legacy_adjusted_warp_formula_caps_at_100 | PASS | 30/18=26, cap=100 |
| legacy_counter_reduction_with_gear | PASS | 25/0=15, 25/3=20 |
| legacy_amplifier_and_death_gaze_range_formula | PASS | amp45=3, range10=24 |
| legacy_outcome_threshold_boundaries | PASS | 73=UNNATURAL_HUNGER_LONG, 76=MOMENT_OF_CLARITY |
| legacy_entity_outcomes_are_backed_by_registered_foundations | PASS | entity outcomes use real registered foundations |
| legacy_guardian_count_formula_and_cap | PASS | one=1, light=warp/30, heavy=warp/15, cap=8 |
| legacy_entity_type_shapes_for_warp_outcomes | PASS | guardian=0.8x2.25 eye2.1, spider=0.7x0.5 eye0.45, portal=1.5x3.0, cultists=0.6x1.8 |
| legacy_eldritch_orb_type_shape | PASS | orb=0.25x0.25, tracking=64, update=20, velocity=true |
| legacy_entity_foundation_classes_construct | PASS | spider=true, portal=true, knight=true, cleric=true, guardian=true, orb=true |
| eldritch_orb_projectile_contract | PASS | life=100, radius=2, damage=attack*0.666, weakness=160, gravity=0.0 |
| eldritch_orb_renderer_contract | PASS | 12 seeded tendrils, 13-frame particle strip, billboard scale 0.75 |
| eldritch_guardian_ranged_orb_contract | PASS | min=8, speed=1, interval=20..40, radius=24, sonic=15% |
| mind_spider_harmless_viewer_lifespan_contract | PASS | harmless=true, viewer=FakePlayer, lifespan=1200 |
| lesser_cultist_portal_active_state_and_budget_contract | PASS | active=true, budgets easy/normal/hard=2/4/6 |
| lesser_cultist_portal_spawns_knight_and_self_damages | PASS | spawned=thaumcraft:cultist_knight, lostHealth=6.0 |
| lesser_cultist_portal_can_force_cleric_spawn_path_for_validation | PASS | spawned=thaumcraft:cultist_cleric |
| custom_warp_entity_aspect_contracts_match_config_aspects | PASS | MindSpider=vitium5/ignis5, EldritchGuardian=alienis20/mortuus20/exanimis20, Cultists=alienis5/humanus15/aversio5 |
| unnatural_hunger_effect_registered_with_legacy_color | PASS | id=thaumcraft:unnatural_hunger, color=4482611 |
| death_gaze_effect_registered_with_legacy_color | PASS | id=thaumcraft:death_gaze, color=6702131 |
| blurred_vision_effect_registered_with_legacy_color | PASS | id=thaumcraft:blurred_vision, color=8421504 |
| sun_scorned_effect_registered_with_legacy_color | PASS | id=thaumcraft:sun_scorned, color=16308330 |
| thaumarhia_effect_registered_with_legacy_color | PASS | id=thaumcraft:thaumarhia, color=6702199 |
| vis_exhaust_outcome_matches_legacy_duration_amp | PASS | effect=duration=5000, amp=3 |
| death_gaze_outcome_matches_legacy_duration_amp | PASS | effect=duration=6000, amp=3 |
| long_unnatural_hunger_outcome_matches_legacy_duration_amp | PASS | effect=duration=6000, amp=3 |
| unnatural_hunger_curative_items_reduce_duration_and_amplifier | PASS | effect=duration=600, amp=0 |
| normal_food_does_not_relieve_unnatural_hunger | PASS | effect=duration=1200, amp=1 |
| actual_warp_10_unlocks_no_eldritch_or_bathsalts | PASS | changed=false |
| actual_warp_thresholds_unlock_hidden_bathsalts_and_eldritch_research | PASS | bathsalts=true, minor=true, major=true |

## Boundary

- Implemented: server tick owner, temporary warp decay, legacy trigger/counter math, legacy outcome threshold table, legacy potion/effect outcomes, Death Gaze range/cone basics, warp research unlock thresholds, warp outcome entity spawn foundations, Eldritch Guardian orb projectile path and lesser cultist portal minion spawning.
- Implemented: rotten flesh / zombie brain relief path for Unnatural Hunger.
- Deferred by missing owners: PacketMiscEvent client hallucination/stress visuals, exact Guardian/orb/cultist/portal renderer pixel parity, CultistCleric GolemOrb projectile branch and fortress mask mitigation.
