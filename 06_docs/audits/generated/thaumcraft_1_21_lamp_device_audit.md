# Lamp Device Audit

Runtime checks for the TC6 Arcane Lamp, Lamp of Growth and Lamp of Fertility blocker slice.

## Summary

| Check | Result |
|---|---:|
| Passed | 10 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| legacy_lamp_blocks_registered | PASS | blocks=lamp_arcane,lamp_growth,lamp_fertility |
| legacy_lamp_items_are_block_items | PASS | items=BlockItem,BlockItem,BlockItem |
| effect_glimmer_is_light_block_without_item | PASS | block=thaumcraft:effect_glimmer |
| lamp_shape_matches_legacy_aabb | PASS | bounds=AABB[0.25, 0.125, 0.25] -> [0.75, 0.875, 0.75] |
| lamp_enabled_light_matches_legacy | PASS | on=15, off=0 |
| arcane_lamp_places_and_removes_glimmer | PASS | placed=true, glimmerBeforeRemove=true, placedAt=BlockPos{x=77, y=74, z=76}, removed=true |
| growth_lamp_draws_herba_like_legacy | PASS | charges=20, jarHerba=1 |
| growth_lamp_targets_and_ticks_ungrown_plants | PASS | grewTarget=true, charges=2, target=BlockPos{x=89, y=84, z=76} |
| fertility_lamp_draws_desiderium_like_legacy | PASS | charges=1, jarDesiderium=1 |
| fertility_lamp_sets_two_adult_animals_in_love | PASS | animalsAdded=true, firstLove=true, secondLove=true, charges=1 |

## Boundary

- Covers server-side device identity, blockstate/light/shape contracts and first gameplay behavior for the three legacy lamps.
- Uses legacy ids `lamp_arcane`, `lamp_growth`, `lamp_fertility` and keeps `arcanelamp` as the research recipe/page id only.
- Final pixel/model comparison, colored light illusion and advanced crop blacklist/inter-mod behavior remain visual/integration follow-ups.
