# Thaumonomicon protocol catalog progress

Date: 2026-06-16

This checkpoint records the current catalog migration state after the crafting substrate, fake-crafting classification, arcane decorative placeholder, arcane uncategorized classification, arcane blockentity placeholder, arcane gameplay placeholder, transport/essentia placeholder, and placeholder output remapping batches.

## Latest verified audit summary

```text
Ready crafting catalog entries: 79
Fake crafting catalog entries: 2
Deferred crafting catalog entries: 0
Ready arcane catalog entries: 89
Deferred arcane catalog entries: 0
Deferred arcane decorative/asset catalog entries: 0
Deferred arcane blockentity catalog entries: 0
Deferred arcane gameplay catalog entries: 0
Deferred arcane transport/essentia catalog entries: 0
Deferred arcane uncategorized catalog entries: 0
```

## Completed migration checkpoints

- Crafting catalog is clean for real recipe entries:
  - 79 ready crafting entries.
  - 2 fake/research bridge entries classified separately.
  - 0 real deferred crafting entries.
- Arcane catalog is clean at catalog placeholder level:
  - 89 ready arcane entries.
  - 0 deferred arcane entries.
  - 0 deferred decorative/asset entries.
  - 0 deferred blockentity entries.
  - 0 deferred gameplay entries.
  - 0 deferred transport/essentia entries.
  - 0 uncategorized entries.
- Placeholder recipe outputs have been remapped to `thaumcraft:*` IDs with simple registry item placeholders where needed.
- A generated placeholder output audit exists at:
  - `06_docs/audits/placeholder_recipe_output_audit.md`

## Important implementation note

The latest arcane decorative, blockentity, gameplay, and transport/essentia work is a catalog/rendering bridge. Many entries are currently represented by placeholder arcane recipes and simple `thaumcraft:*` output items. This makes Thaumonomicon pages render through the server recipe pipeline and keeps the protocol audit deterministic, but it does not mean the final gameplay, block entity, or transport behavior is complete.

## Remaining real implementation work

### Decorative/assets

Replace placeholder items with real placeable blocks, models, blockstates, loot tables, and final recipe outputs where needed.

### Block entities

Replace item-only registry placeholders with real blocks + block entities. Real work includes:

- block registration;
- block item registration;
- block entity type registration;
- save/load;
- server/client sync;
- menu or interaction logic where needed;
- renderer placeholder or renderer parity.

### Gameplay items

Replace placeholder items with real item classes and behavior. Real work includes:

- crossbow/grapple behavior;
- focus pouch handling;
- robe armor behavior;
- sanity checker behavior;
- module/mod item behavior;
- seal blank usage.

### Transport / essentia

Transport/essentia is still only catalog-clean. Real work remains a separate subsystem pass:

- tube graph behavior;
- essentia storage;
- suction rules;
- transfer tick logic;
- jar/smelter/tube integration;
- renderer/sync parity.

## Recommended next order

1. Audit the generated placeholder output table and pick the first real implementation target group.
2. Start with a small blockentity skeleton batch for safe entries such as `redstonerelay`, `resonator`, `visbattery`, `visgenerator`, `hungrychest`, or `levitator`.
3. Keep transport/essentia real behavior for a dedicated subsystem pass.
4. Replace placeholder recipes with real TC6 recipe outputs as each actual item/block/system becomes available.
