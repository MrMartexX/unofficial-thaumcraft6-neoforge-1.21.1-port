# Taint Mob and Thaumic Slime Blocker Audit

Runtime checks for the row-11/14 taint mob foundation slice: TC6 entity registrations,
server-side taint ecology hooks, scan/aspect identities and safe client renderer registration.

## Summary

| Check | Result |
|---|---:|
| Passed | 14 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| thaum_slime_registered_with_legacy_tracking | PASS | entity=thaumcraft:thaum_slime |
| taint_crawler_registered_with_legacy_tracking | PASS | entity=thaumcraft:taint_crawler |
| taintacle_registered_with_legacy_tracking | PASS | entity=thaumcraft:taintacle |
| taintacle_tiny_registered_with_legacy_tracking | PASS | entity=thaumcraft:taintacle_tiny |
| taint_swarm_registered_with_legacy_tracking | PASS | entity=thaumcraft:taint_swarm |
| taint_mob_attribute_baselines_match_legacy | PASS | crawler/tentacle/tiny/swarm base attributes |
| custom_taint_entity_aspects_match_legacy_explicit_assignments | PASS | crawler intentionally has no explicit ConfigAspects assignment in TC6 source |
| custom_taint_entity_scan_keys_match_legacy | PASS | legacy scan keys plus TaintSwarm f_FLY fact |
| taint_crawler_places_legacy_surface_fibre_trail | PASS | state=thaumcraft:taint_fibre |
| taint_feature_break_hook_spawns_taint_crawler | PASS | before=0, after=1 |
| taint_geyser_spawn_hook_creates_taint_swarm | PASS | before=0, after=1 |
| taintacle_spawns_tiny_taintacle_only_on_taint_substrate | PASS | tinyBefore=0, tinyAfter=1 |
| taint_swarm_summoned_and_damage_bonus_nbt_roundtrip | PASS | tag={AbsorptionAmount:0.0f,ArmorDropChances:[0.085f,0.085f,0.085f,0.085f],ArmorItems:[{},{},{},{}],Brain:{memories:{}},CanPickUpLoot:0b,DeathTime:0s,FallFlying:0b,HandDropChances:[0.085f,0.085f],HandItems:[{},{}],Health:30.0f,HurtByTimestamp:0,HurtTime:0s,LeftHanded:0b,PersistenceRequired:0b,attributes:[{base:5.0d,id:"minecraft:generic.attack_damage"}],damBonus:3b,summoned:1b} |
| thaumic_slime_size_controls_xp_like_legacy | PASS | size=4, xp=6 |

## Boundary

- Implemented: `thaum_slime`, `taint_crawler`, `taintacle`, `taintacle_tiny` and `taint_swarm` entity types with TC6 tracking/update/velocity values.
- Implemented: server-side foundations for crawler fibre trail/Flux Taint bite, feature break crawler spawn, geyser swarm spawn, taintacle tiny spawn/lifetime, swarm summoned NBT and Thaumic Slime ranged split.
- Implemented: legacy scan keys for custom taint mobs and exact ConfigAspects assignments where legacy provided explicit entity tags.
- Covered separately: FallingTaint crust physics is covered by TCFallingTaintBlockerAudit. Deferred: measured mob model/animation renderer parity, full taint swarm particle renderer and broad natural spawn placement tables.
