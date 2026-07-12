# Alchemy Automation Device Audit

Runtime checks for the TC6 Everfull Urn and Arcane Spa server-automation blocker slice.

## Summary

| Check | Result |
|---|---:|
| Passed | 11 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| spa_registered_as_legacy_block_output | PASS | block=thaumcraft:spa, catalogItem=thaumcraft:spa |
| everfull_urn_registered_as_real_block | PASS | block=thaumcraft:everfull_urn, itemClass=BlockItem |
| everfull_urn_shape_matches_legacy_aabb | PASS | outline=AABB[0.1875, 0.0, 0.1875] -> [0.8125, 1.0, 0.8125] |
| everfull_urn_top_only_drain_capability_no_external_fill | PASS | urn=true, top=true, side=false, fillTop=0 |
| everfull_urn_aura_refill_legacy_quanta | PASS | water=100, vis=9.9 |
| everfull_urn_fills_cauldron_for_333mb | PASS | cauldron=minecraft:water_cauldron, level=1, water=667, handlers=[42] |
| everfull_urn_glass_bottle_cost_contract | PASS | waterAfterBottle=667 |
| arcane_spa_item_and_fluid_capability_contract | PASS | spa=true, sideSlots=1, topSlots=0, fluid=true, bathRemainder=0, dirtRemainder=1 |
| arcane_spa_mix_places_purifying_fluid | PASS | above=thaumcraft:purifying_fluid, fluid=0, salt=0 |
| arcane_spa_liquid_only_places_source_fluid | PASS | above=minecraft:water, fluid=0 |
| arcane_spa_expands_adjacent_source_in_5x5_layer | PASS | sources=2, expansion=BlockPos{x=173, y=73, z=244}, fluid=0 |

## Boundary

- Covers real block, BlockItem and BlockEntity identities for `spa` and `everfull_urn`.
- Covers Everfull Urn drain-only fluid capability, 5x3x5 cached target scan, cauldron fill cost and aura refill quanta.
- Covers Arcane Spa bath-salts slot contract, 5000 mB tank, mix-mode purifying-fluid placement, fluid-only placement and 5x5 adjacent expansion.
- Does not claim final Spa GUI, exact client water-trail/splash particles, Botania Petal Apothecary integration or pixel-level model parity.
