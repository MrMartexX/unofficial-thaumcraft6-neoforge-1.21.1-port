# FallingTaint Blocker Audit

Runtime checks for the row-11/14 slice that ports TC6 `EntityFallingTaint` and
the crusted-taint falling rules from legacy `BlockTaint`.

## Summary

| Check | Result |
|---|---:|
| Passed | 10 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| falling_taint_entity_registered_with_legacy_tracking | PASS | entity=thaumcraft:falling_taint, size=0.98x0.98, tracking=64, update=3, velocity=true |
| falling_taint_legacy_catalog_registered | PASS | catalogStatus=registered_foundation |
| gore_sound_registered_for_landing_path | PASS | sound=thaumcraft:gore |
| can_fall_below_accepts_legacy_replaceable_targets | PASS | air/fibre/low-goo/water accepted |
| can_fall_below_rejects_high_flux_goo_and_nearby_logs | PASS | highGoo=false, nearbyLog=false |
| crusted_taint_try_to_fall_accepts_and_preserves_source_until_entity_tick | PASS | spawned=true, entities=1, source=thaumcraft:taint_crust, entityOld=BlockPos{x=0, y=97, z=64} |
| falling_taint_first_tick_removes_original_crust | PASS | source=minecraft:air, fallTime=1 |
| falling_taint_lands_as_crust_above_solid_support | PASS | alive=false, landing=thaumcraft:taint_crust, y=93.0 |
| crusted_taint_overhang_fall_uses_side_target_and_original_source | PASS | spawned=true, entities=1, source=thaumcraft:taint_crust, target=BlockPos{x=13, y=98, z=64}, entityPos=BlockPos{x=13, y=98, z=64}, entityOld=BlockPos{x=12, y=98, z=64} |
| falling_taint_nbt_round_trip_preserves_legacy_fields | PASS | block=thaumcraft:taint_crust, old=BlockPos{x=16, y=92, z=64}, time=1, hurt=2.0/40 |

## Boundary

- Implemented: `falling_taint` entity registration with TC6 tracking `64`, update interval `3` and velocity updates enabled.
- Implemented: crusted-taint `tryToFall` server path, source-block removal on first tick, TC6 gravity/damping constants, landing placement over solid support and timeout discard.
- Implemented: TC6 `canFallBelow` blockers for nearby logs, flux-goo level threshold, taint fibre, replaceable blocks, water and lava.
- Implemented: GORE sound registration for the landing path and a block-model renderer foundation for the falling crust.
- Deferred: exact landing particles, measured renderer pixel parity and broad natural taint-spawn placement tables.
