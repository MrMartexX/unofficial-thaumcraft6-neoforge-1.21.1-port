# Infernal Furnace Behavior Audit

Runtime checks for the TC6 Infernal Furnace standalone-device blocker slice.

## Summary

| Check | Result |
|---|---:|
| Passed | 12 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| infernal_furnace_block_and_blockitem_registered | PASS | block=thaumcraft:infernal_furnace, itemClass=BlockItem |
| legacy_bonus_outputs_registered | PASS | flattened old nugget/chunk metadata outputs are real item ids |
| shape_matches_legacy_half_height_box | PASS | bounds=AABB[0.0, 0.0, 0.0] -> [1.0, 0.5, 1.0] |
| light_level_matches_legacy_point_nine | PASS | light=14 |
| output_direction_is_facing_opposite | PASS | facing=north |
| top_only_input_capability_matches_legacy | PASS | topSlots=32, sideSlots=0, sideRemainder=1 |
| non_smeltable_items_are_destroyed_like_lava | PASS | destroyedItems=1, remainder=0 |
| aura_speedy_drain_sets_20_ticks | PASS | before=100.0, after=80.0, speedy=20 |
| bellows_distance_two_formula_matches_legacy | PASS | bellows=2, cookNoSpeed=102, cook4=72, speedy4=12 |
| vanilla_smelting_recipe_lookup_active | PASS | rawIronResult=minecraft:iron_ingot |
| cook_completion_ejects_front_and_consumes_input | PASS | completed=1, speedy=19, ironEntities=1, slot0=0 |
| bonus_table_has_legacy_meat_and_ore_candidates | PASS | beef, iron ore and copper cluster are recognized bonus inputs |

## Boundary

- Covers the active runtime machine: block identity, half-height shape/light, top-only input capability, lava destruction, vanilla smelting lookup, aura-speed drain, legacy bellows distance formula and front ejection.
- Includes the internal legacy default smelting bonus table and modern flattened bonus output ids for the current port.
- Does not implement the Salis Mundus multiblock/dust activation blueprint or final in-game pixel parity; those remain separate blocker slices.
