# Warp Event Behavior Audit

Runtime parity checks for the first server-side TC6 WarpEvents slice.

## Summary

| Check | Result |
|---|---:|
| Passed | 19 |
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
| legacy_entity_outcomes_are_marked_deferred_until_entities_exist | PASS | deferred outcomes tracked |
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

- Implemented: server tick owner, temporary warp decay, legacy trigger/counter math, legacy outcome threshold table, legacy potion/effect outcomes, Death Gaze range/cone basics and warp research unlock thresholds.
- Implemented: rotten flesh / zombie brain relief path for Unnatural Hunger.
- Deferred by missing owners: real Eldritch Guardian, Mind Spider, lesser cultist portal spawning, PacketMiscEvent client hallucination/stress visuals and fortress mask mitigation.
