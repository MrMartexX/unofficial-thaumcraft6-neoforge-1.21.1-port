# Thaumonomicon protocol catalog progress

Date: 2026-06-16

This checkpoint records the current catalog migration state after the crafting substrate, fake-crafting classification, arcane decorative placeholder, arcane uncategorized classification, and arcane blockentity placeholder batches.

## Latest verified audit summary

```text
Ready crafting catalog entries: 79
Fake crafting catalog entries: 2
Deferred crafting catalog entries: 0
Ready arcane catalog entries: 62
Deferred arcane catalog entries: 27
Deferred arcane decorative/asset catalog entries: 0
Deferred arcane blockentity catalog entries: 0
Deferred arcane gameplay catalog entries: 17
Deferred arcane transport/essentia catalog entries: 10
Deferred arcane uncategorized catalog entries: 0
```

## Completed migration checkpoints

- Crafting catalog is clean for real recipe entries:
  - 79 ready crafting entries.
  - 2 fake/research bridge entries classified separately.
  - 0 real deferred crafting entries.
- Arcane decorative/asset bucket is clean:
  - 0 deferred decorative/asset entries.
- Arcane blockentity bucket is clean at catalog placeholder level:
  - 0 deferred blockentity entries.
- Uncategorized arcane bucket is clean:
  - 0 uncategorized entries.

## Important implementation note

The latest arcane decorative and blockentity work is a catalog/rendering bridge. Many entries are currently represented by placeholder arcane recipes using safe existing output items. This makes Thaumonomicon pages render through the server recipe pipeline and keeps the protocol audit deterministic, but it does not mean the final gameplay/block entity/transport behavior is complete.

## Remaining protocol buckets

### Gameplay

17 arcane gameplay entries remain deferred. These should be handled as item/entity/player interaction systems rather than block entity systems.

Expected examples include crossbow/grapple/robe/sanity/module-style entries.

### Transport / essentia

10 arcane transport/essentia entries remain deferred. These should be treated as a separate subsystem and not mixed into placeholder blockentity batches.

Expected work includes tube graph behavior, essentia storage/flow/suction, jar interaction, and renderer/sync parity.

## Recommended next order

1. Gameplay placeholder/catalog bridge batch for the remaining 17 gameplay entries.
2. Transport/essentia placeholder/catalog bridge batch for the remaining 10 transport entries.
3. Replace placeholder outputs with real registered items/blocks as the actual gameplay systems are implemented.
4. Implement real block entity and transport behavior incrementally after catalog readiness is stable.
