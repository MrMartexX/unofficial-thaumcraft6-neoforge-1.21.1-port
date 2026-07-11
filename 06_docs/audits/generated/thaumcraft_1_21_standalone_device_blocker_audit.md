# Standalone Device Blocker Audit

Runtime checks for row-13 blockers closed after the Infernal Furnace runtime slice.

## Summary

| Check | Result |
|---|---:|
| Passed | 9 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| salis_mundus_uses_real_dust_item | PASS | itemClass=ItemSalisMundus |
| void_siphon_block_blockitem_and_blockentity_registered | PASS | block=thaumcraft:void_siphon, itemClass=BlockItem |
| infernal_furnace_dust_blueprint_activates | PASS | activated=true, placeholders=12/12, output=south |
| infernal_furnace_break_rolls_structure_back | PASS | lava=true, netherBricks=12, obsidian=12 |
| infernal_placeholder_break_destroys_linked_furnace | PASS | brokenPlaceholder=BlockPos{x=120, y=72, z=200}, center=minecraft:lava |
| void_siphon_shapes_match_legacy_aabbs | PASS | outline=AABB[0.1875, 0.0, 0.1875] -> [0.8125, 1.0, 0.8125], collisionBoxes=3 |
| void_siphon_extract_only_slot_contract | PASS | handler=1, insertRemainder=1 |
| void_siphon_rift_drain_math_matches_legacy | PASS | drained=3, progress=3, stability=0.8, size=9 |
| void_siphon_progress_outputs_void_seed | PASS | slot=thaumcraft:void_seedx1, progress=1 |

## Boundary

- Covers Salis Mundus IDustTrigger-style Infernal Furnace multiblock detection, conversion placeholders, furnace rollback and placeholder-triggered teardown.
- Covers Void Siphon block identity, shape, redstone enabled state, one extract-only slot, GUI progress data, rift-drain math and void-seed output conversion.
- Does not implement full Flux Rift spawning/lifecycle/rendering; Void Siphon consumes entities that implement the explicit `TCVoidSiphonRiftAccess` adapter.
