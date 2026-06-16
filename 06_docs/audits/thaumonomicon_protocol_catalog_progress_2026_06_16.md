# Thaumonomicon protocol catalog progress

Date: 2026-06-16

This checkpoint records the current catalog migration state after the crafting substrate, fake-crafting classification, arcane decorative placeholder, arcane uncategorized classification, arcane blockentity placeholder, and arcane gameplay placeholder batches.

## Latest verified audit summary

```text
Ready crafting catalog entries: 79
Fake crafting catalog entries: 2
Deferred crafting catalog entries: 0
Ready arcane catalog entries: 79
Deferred arcane catalog entries: 10
Deferred arcane decorative/asset catalog entries: 0
Deferred arcane blockentity catalog entries: 0
Deferred arcane gameplay catalog entries: 0
Deferred arcane transport/essentia catalog entries: 10
Deferred arcane uncategorized catalog entries: 0
```

## Completed migration checkpoints

- Crafting catalog is clean for real recipe entries:
  - 79 ready crafting entries.
  - 2 fake/research bridge entries classified separately.
  - 0 real deferred crafting entries.
- Arcane decorative/asset bucket is clean at catalog placeholder level:
  - 0 deferred decorative/asset entries.
- Arcane blockentity bucket is clean at catalog placeholder level:
  - 0 deferred blockentity entries.
- Arcane gameplay bucket is clean at catalog placeholder level:
  - 0 deferred gameplay entries.
- Uncategorized arcane bucket is clean:
  - 0 uncategorized entries.

## Important implementation note

The latest arcane decorative, blockentity, and gameplay work is a catalog/rendering bridge. Many entries are currently represented by placeholder arcane recipes using safe existing output items. This makes Thaumonomicon pages render through the server recipe pipeline and keeps the protocol audit deterministic, but it does not mean the final gameplay/block entity/transport behavior is complete.

## Remaining protocol buckets

### Transport / essentia

10 arcane transport/essentia entries remain deferred:

- `thaumcraft:essentiasmelterthaumium`
- `thaumcraft:essentiasmeltervoid`
- `thaumcraft:essentiatransportin`
- `thaumcraft:essentiatransportout`
- `thaumcraft:tube`
- `thaumcraft:tubebuffer`
- `thaumcraft:tubefilter`
- `thaumcraft:tubeoneway`
- `thaumcraft:tuberestrict`
- `thaumcraft:tubevalve`

These should be treated as a separate subsystem and not mixed into placeholder blockentity/gameplay batches.

Expected real implementation work includes tube graph behavior, essentia storage/flow/suction, jar interaction, smelter integration, and renderer/sync parity.

## Recommended next order

1. Transport/essentia placeholder/catalog bridge batch for the remaining 10 transport entries.
2. Keep transport real behavior for a later dedicated subsystem pass.
3. Replace placeholder outputs with real registered items/blocks as the actual gameplay systems are implemented.
4. Implement real block entity, gameplay, and transport behavior incrementally after catalog readiness is stable.
