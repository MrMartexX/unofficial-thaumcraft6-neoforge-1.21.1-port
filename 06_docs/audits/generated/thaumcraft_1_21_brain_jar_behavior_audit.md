# Brain-in-a-Jar Behavior Audit

Runtime checks for the TC6 Brain-in-a-Jar XP jar and theorycraft aid blocker slice.

## Summary

| Check | Result |
|---|---:|
| Passed | 10 |
| Failed | 0 |

## Checks

| Name | Result | Notes |
|---|---|---|
| jar_brain_registered_as_real_block_item | PASS | block=thaumcraft:jar_brain, item=thaumcraft:jar_brain, itemClass=TCBrainJarBlockItem |
| jar_brain_block_entity_registered | PASS | blockEntity=thaumcraft:jar_brain |
| jar_brain_shape_matches_legacy_aabb | PASS | outline=AABB[0.1875, 0.0, 0.1875] -> [0.8125, 0.75, 0.8125] |
| jar_brain_entity_capability_enchant_contract | PASS | jar=true, noCaps=true, enchant=5.0 |
| jar_brain_item_preserves_xp_payload | PASS | stack=thaumcraft:jar_brain, xp=1234 |
| jar_brain_comparator_matches_legacy_formula | PASS | signals=0/8/15 |
| jar_brain_legacy_pull_formula | PASS | pull=(-0.21167999999999995, 0.0, 0.0) |
| jar_brain_absorbs_close_xp_orb | PASS | xp=13, orbRemoved=true |
| jar_brain_release_sets_delay_and_spawns_xp | PASS | xp=63, eatDelay=40 |
| aid_brain_in_a_jar_registers_dark_whispers | PASS | aid=[thaumcraft.common.lib.research.theorycraft.CardDarkWhispers] |

## Boundary

- Covers real block, BlockItem and BlockEntity identities for `jar_brain`.
- Covers legacy server XP storage, close-orb absorption, 8-block orb pull, right-click release delay, comparator and enchanting bonus.
- Covers the `AidBrainInAJar` theorycraft aid source for `CardDarkWhispers`.
- Does not claim final animated brain BER/model rotation, full-client spark pixel parity or the legacy item-warp registry.
