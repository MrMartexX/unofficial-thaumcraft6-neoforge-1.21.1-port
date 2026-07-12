# Special Alchemy Behavior Audit

Runtime checks for TC6 special crucible outputs whose gameplay lives on the produced item/entity rather than in `TileCrucible`.

## Summary

| Check | Result |
|---|---:|
| Passed | 13 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| bath_salts_item_registered_with_legacy_id_and_lifespan | PASS | item=thaumcraft:bath_salts, lifespan=200 |
| bottle_taint_item_registered_with_legacy_id_and_stack_size | PASS | item=thaumcraft:bottle_taint, maxStack=8 |
| bottle_taint_throw_constants_match_legacy | PASS | velocity=0.66, inaccuracy=1.0, xRotOffset=-5.0 |
| bottle_taint_entity_registered_with_legacy_tracking | PASS | entity=thaumcraft:bottle_taint, tracking=64, update=20, velocity=true |
| bottle_taint_projectile_default_stack_contract | PASS | type=thaumcraft:bottle_taint, stack=thaumcraft:bottle_taint |
| bottle_taint_flux_taint_predicate_matches_legacy | PASS | playerApplied=true, undeadApplied=false, taintedApplied=false, duration=100 |
| bottle_taint_places_flux_goo_on_supported_replaceable_target | PASS | placed=true, block=thaumcraft:flux_goo |
| bottle_taint_rejects_unsupported_or_nonreplaceable_goo_targets | PASS | unsupported=false, solid=false |
| bottle_taint_one_block_down_fallback_matches_legacy | PASS | fallbackPlaced=true, fallbackBlock=thaumcraft:flux_goo |
| special_alchemy_recipe_bottletaint | PASS | result=thaumcraft:bottle_taint, research=BOTTLETAINT, aspects=[TCCrucibleAspectCost[aspect=vitium, amount=30], TCCrucibleAspectCost[aspect=aqua, amount=30]] |
| special_alchemy_recipe_bathsalts | PASS | result=thaumcraft:bath_salts, research=BATHSALTS, aspects=[TCCrucibleAspectCost[aspect=cognitio, amount=40], TCCrucibleAspectCost[aspect=aer, amount=40], TCCrucibleAspectCost[aspect=ordo, amount=40], TCCrucibleAspectCost[aspect=victus, amount=40]] |
| special_alchemy_recipe_liquiddeath | PASS | result=thaumcraft:liquid_death_bucket, research=LIQUIDDEATH, aspects=[TCCrucibleAspectCost[aspect=mortuus, amount=100], TCCrucibleAspectCost[aspect=alkimia, amount=20], TCCrucibleAspectCost[aspect=perditio, amount=50]] |
| fluid_specials_remain_explicit_blockers_not_fake_placeholders | PASS | liquidDeathBlock=false, purifyingFluidBlock=false, warpWard=false |

## Boundary

- Implemented in this slice: `bath_salts` legacy dropped-item lifespan, `bottle_taint` stack size/use constants, `bottle_taint` projectile registration, Flux Taint splash predicate/effect and Flux Goo placement support rules.
- Already data-backed before this slice: special crucible recipes for BottleTaint, BathSalts, LiquidDeath and SaneSoap.
- Deferred to a later fluid blocker: real `liquid_death` and `purifying_fluid` blocks/fluids, Bath Salts water-source conversion and Warp Ward effect behavior.
