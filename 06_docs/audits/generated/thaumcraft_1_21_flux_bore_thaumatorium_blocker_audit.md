# Flux Rift, Arcane Bore and Thaumatorium Blocker Audit

Runtime checks for the row-11/13/14/17 blocker slice that connects real Flux Rift entities,
Arcane Bore entity/menu/mining foundation and Thaumatorium OBJ/output rendering resources.

## Summary

| Check | Result |
|---|---:|
| Passed | 11 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| flux_rift_entity_registered_with_legacy_tracking | PASS | entity=thaumcraft:flux_rift |
| arcane_bore_entity_and_item_registered_with_legacy_tracking | PASS | entity=thaumcraft:arcane_bore, itemClass=ItemArcaneBore |
| flux_rift_seeded_geometry_and_stability_clamps_match_legacy | PASS | points=8, widths=8, clampHigh=true, clampLow=true |
| void_siphon_consumes_real_flux_rift_entity | PASS | drained=3, progress=3, stability=0.8, size=9 |
| flux_rift_collapse_drops_void_seed_family_and_discards | PASS | alive=false |
| arcane_bore_pickaxe_slot_contract | PASS | valid=true, radius=2, depth=16, pickaxeTag=true |
| arcane_bore_mines_target_and_consumes_vis_charge_path | PASS | mined=true, targetState=minecraft:air |
| arcane_bore_menu_exposes_single_pickaxe_slot | PASS | slots=37 |
| thaumatorium_model_uses_legacy_obj_loader_not_cube_fallback | PASS | modelLength=366 |
| thaumatorium_mtl_uses_modern_block_texture_location | PASS | materialLength=378 |
| thaumatorium_blockstate_uses_legacy_obj_orientation | PASS | blockstateLength=432 |

## Boundary

- Flux Rift now has a registered entity, data-synced seed/size/stability/collapse state, TC6-style geometry and Void Siphon adapter integration.
- Full rift event consequences that depend on Wisp, taint seed, focus cloud and warp subsystems remain owned by those later subsystem rows.
- Arcane Bore now has a registered entity, placer item, one-slot pickaxe menu, redstone active state, vis charge and server-side mining loop.
- Thaumatorium no longer uses a cube fallback model; it resolves the legacy OBJ model and has a BER output-item render path.
