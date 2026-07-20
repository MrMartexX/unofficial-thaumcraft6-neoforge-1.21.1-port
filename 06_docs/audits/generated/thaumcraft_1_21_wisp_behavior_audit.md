# Wisp Behavior and Render Contract Audit

Runtime checks for the TC6 Wisp slice after Flux Rift event 0 began spawning real Wisps.
This covers server AI constants, type/aspect persistence, targeting, zap cadence and the
legacy billboard render contract and the modern source/target-id equivalent of PacketFXWispZap.

## Summary

| Check | Result |
|---|---:|
| Passed | 11 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| wisp_type_registered_with_legacy_tracking | PASS | entity=thaumcraft:wisp |
| wisp_attributes_match_legacy | PASS | health=22, attack=3, move/fly=0.15, follow=16 |
| wisp_type_nbt_roundtrip_matches_legacy_key | PASS | tag.Type=ignis |
| wisp_dynamic_entity_aspects_match_legacy | PASS | type=vitium |
| wisp_scan_keys_match_legacy | PASS | legacy_core.json contains !Wisp and f_FLY mappings |
| wisp_hurt_sets_target_and_legacy_aggro_cooldown | PASS | target=entity.minecraft.zombie, aggro=200 |
| wisp_wander_selects_flight_target_and_updates_motion | PASS | target=BlockPos{x=257, y=81, z=331}, motion=(-0.05000000074505806, 0.06999999985098837, -0.05000000074505806) |
| wisp_zap_cadence_resets_after_twentieth_visible_tick | PASS | prev=19, current=-14 |
| wisp_render_textures_exist | PASS | particles=thaumcraft:textures/misc/particles.png, nodes=thaumcraft:textures/misc/auranodes.png |
| wisp_render_layers_use_legacy_frames_scales_and_alpha | PASS | core=512/0.4/1.0, halo=320/0.75/0.25, node=800/0.75/0.5 |
| wisp_zap_payload_matches_legacy_source_target_contract | PASS | payload=thaumcraft:wisp_zap_fx, range=32 |

## Boundary

- Implemented: legacy Wisp type persistence, aspect crystal drop, peaceful despawn, hurt aggro cooldown, free-flight waypoint AI, chase motion, zap attack cadence/sound/damage, dynamic type aspects and source/target-id zap payload.
- Implemented: client renderer registration uses TC6 billboard frame indices from `particles.png` and `auranodes.png` with fullbright additive blending; ambient Wisp motes use the legacy `drawWispParticles` particle parameters.
- Implemented with modern renderer constraints: Wisp zap bolt uses the same TC6 source/target id packet contract, color extraction and short-lived bolt point math, but renders through PoseStack/BufferBuilder instead of legacy CoreGLE.
