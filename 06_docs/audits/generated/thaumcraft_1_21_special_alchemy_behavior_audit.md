# Special Alchemy Behavior Audit

Runtime checks for TC6 special crucible outputs whose gameplay lives on the produced item/entity rather than in `TileCrucible`.

## Summary

| Check | Result |
|---|---:|
| Passed | 32 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| bath_salts_item_registered_with_legacy_id_and_lifespan | PASS | item=thaumcraft:bath_salts, lifespan=200 |
| alumentum_item_registered_with_legacy_id_stack_and_projectile_interface | PASS | item=thaumcraft:alumentum, maxStack=64 |
| alumentum_throw_constants_match_legacy | PASS | requestedVelocity=0.4, actualVelocity=0.75, inaccuracy=2.0 |
| bottle_taint_item_registered_with_legacy_id_and_stack_size | PASS | item=thaumcraft:bottle_taint, maxStack=8 |
| bottle_taint_throw_constants_match_legacy | PASS | velocity=0.66, inaccuracy=1.0, xRotOffset=-5.0 |
| liquid_death_bucket_is_real_bucket_for_source_fluid | PASS | item=thaumcraft:liquid_death_bucket, fluidBucket=thaumcraft:liquid_death_bucket |
| purifying_fluid_bucket_is_real_bucket_for_source_fluid | PASS | item=thaumcraft:purifying_fluid_bucket, fluidBucket=thaumcraft:purifying_fluid_bucket |
| alumentum_entity_registered_with_legacy_tracking | PASS | entity=thaumcraft:alumentum, tracking=64, update=20, velocity=true |
| alumentum_projectile_default_stack_contract | PASS | type=thaumcraft:alumentum, stack=thaumcraft:alumentum |
| alumentum_projectile_ignores_requested_velocity_like_legacy | PASS | speed=0.75, requested=0.4, actual=0.75 |
| alumentum_explosion_contract_matches_legacy | PASS | strength=1.1, fire=true, interaction=TNT |
| alumentum_client_trail_contract_matches_legacy_constants | PASS | samples=3, fireMote alpha/scale=0.5/4.0, generic start=448, frames=8, age=8 |
| bottle_taint_entity_registered_with_legacy_tracking | PASS | entity=thaumcraft:bottle_taint, tracking=64, update=20, velocity=true |
| bottle_taint_projectile_default_stack_contract | PASS | type=thaumcraft:bottle_taint, stack=thaumcraft:bottle_taint |
| bottle_taint_flux_taint_predicate_matches_legacy | PASS | playerApplied=true, undeadApplied=false, taintedApplied=false, duration=100 |
| bottle_taint_places_flux_goo_on_supported_replaceable_target | PASS | placed=true, block=thaumcraft:flux_goo |
| bottle_taint_rejects_unsupported_or_nonreplaceable_goo_targets | PASS | unsupported=false, solid=false |
| bottle_taint_one_block_down_fallback_matches_legacy | PASS | fallbackPlaced=true, fallbackBlock=thaumcraft:flux_goo |
| special_fluids_registered_with_legacy_public_ids | PASS | blocks=thaumcraft:liquid_death/thaumcraft:purifying_fluid, fluids=thaumcraft:liquid_death/thaumcraft:purifying_fluid |
| warp_ward_effect_registered_with_modern_legacy_id_and_color | PASS | effect=thaumcraft:warp_ward, color=14742263 |
| bath_salts_expire_converts_source_water_to_purifying_fluid | PASS | converted=true, block=thaumcraft:purifying_fluid |
| bath_salts_rejects_flowing_water_and_nonwater | PASS | flowing=false, stone=false |
| liquid_death_legacy_damage_and_slowdown_formula | PASS | damage0=5.0, damage3=2.0, slowdown0=0.5 |
| liquid_death_uses_legacy_dissolve_damage_identity | PASS | msgId=dissolve, bypassesArmor=true, exhaustion=0.3 |
| liquid_death_dissolve_drops_entity_aspect_crystals | PASS | added=3, drops=3 |
| purifying_fluid_grants_warp_ward_with_legacy_duration_and_consumes_source | PASS | applied=true, duration=20000, block=minecraft:air |
| sane_soap_uses_legacy_warp_ward_and_purifying_fluid_bonus | PASS | amount=3, normal=2, temp=0 |
| special_alchemy_recipe_alumentum | PASS | result=thaumcraft:alumentum, research=ALUMENTUM, aspects=[TCCrucibleAspectCost[aspect=potentia, amount=10], TCCrucibleAspectCost[aspect=ignis, amount=10], TCCrucibleAspectCost[aspect=perditio, amount=5]] |
| special_alchemy_recipe_bottletaint | PASS | result=thaumcraft:bottle_taint, research=BOTTLETAINT, aspects=[TCCrucibleAspectCost[aspect=vitium, amount=30], TCCrucibleAspectCost[aspect=aqua, amount=30]] |
| special_alchemy_recipe_bathsalts | PASS | result=thaumcraft:bath_salts, research=BATHSALTS, aspects=[TCCrucibleAspectCost[aspect=cognitio, amount=40], TCCrucibleAspectCost[aspect=aer, amount=40], TCCrucibleAspectCost[aspect=ordo, amount=40], TCCrucibleAspectCost[aspect=victus, amount=40]] |
| special_alchemy_recipe_liquiddeath | PASS | result=thaumcraft:liquid_death_bucket, research=LIQUIDDEATH, aspects=[TCCrucibleAspectCost[aspect=mortuus, amount=100], TCCrucibleAspectCost[aspect=alkimia, amount=20], TCCrucibleAspectCost[aspect=perditio, amount=50]] |
| fluid_specials_are_real_registered_behaviors_not_fake_placeholders | PASS | liquidDeathBlock=true, purifyingFluidBlock=true, warpWard=true |

## Boundary

- Implemented in this slice: `bath_salts` legacy dropped-item lifespan and water-source conversion, `alumentum` throw/dispenser projectile contract, invisible projectile body, fiery trail bridge and legacy flaming explosion constants, `bottle_taint` stack size/use constants, `bottle_taint` projectile registration, Flux Taint splash predicate/effect and Flux Goo placement support rules, real Liquid Death/Purifying Fluid registries/blocks, `thaumcraft:dissolve` Liquid Death damage identity, dissolve-crystal living drop bridge, Warp Ward effect and Sanity Soap Purifying Fluid/Warp Ward bonuses.
- Already data-backed before this slice: special crucible recipes for Alumentum, BottleTaint, BathSalts, LiquidDeath and SaneSoap.
- Deferred to later visual/automation slices: exact Alumentum fire-mote pixel parity, exact client fluid particles/render translucency, Arcane Spa/Everfull Urn automation consumers and broader alchemy automation consumers.
