# Transport/essentia port progress - 2026-06-17

## Current checkpoint

Latest pushed implementation checkpoint:

- `05c999e` - legacy-aligned essentia transport foundation
- `522dad3` - transport essentia blockentity wiring skeleton
- `b0438cd` - transport essentia blocks and block entities wired into registry

The current registry wiring batch moves the transport/essentia entries from item-only catalog placeholders toward real block/blockentity-backed entries:

- `tube`
- `tube_buffer`
- `tube_filter`
- `tube_oneway`
- `tube_restrict`
- `tube_valve`
- `essentiasmelterthaumium`
- `essentiasmeltervoid`

## Verified audit state after `b0438cd`

Thaumonomicon protocol audit remains fully green:

| Category | Count |
|---|---:|
| Ready crafting catalog entries | 79 |
| Fake crafting catalog entries | 2 |
| Deferred crafting catalog entries | 0 |
| Ready arcane catalog entries | 89 |
| Deferred arcane catalog entries | 0 |
| Deferred arcane decorative/asset catalog entries | 0 |
| Deferred arcane blockentity catalog entries | 0 |
| Deferred arcane gameplay catalog entries | 0 |
| Deferred arcane transport/essentia catalog entries | 0 |
| Deferred arcane uncategorized catalog entries | 0 |

## What is now in place

### Foundation layer

The transport foundation was introduced as a modern 1.21-safe layer while keeping the important TC6 legacy concepts:

- directional connectability;
- side-specific input/output checks;
- suction type;
- suction amount;
- minimum suction;
- filtered aspect;
- valve open/closed state;
- one-way output face;
- buffer-style local storage;
- suction-driven transfer rule.

### Block/blockentity skeleton layer

The next layer introduced skeleton classes for transport objects and smelter endpoints:

- common tube block skeleton;
- tube variant mapping;
- tube blockentity skeleton;
- smelter endpoint block skeleton;
- smelter endpoint blockentity skeleton;
- save/load base;
- block update/sync base;
- server tick skeleton.

### Registry wiring layer

The latest batch wires the transport/essentia objects into the registry layer:

- tube blocks are registered;
- smelter endpoint blocks are registered;
- matching block item outputs are used instead of item-only placeholders;
- block entity type registrations are added;
- basic blockstate/model/item-model assets are present so the objects can load without missing-model failures.

## Still intentionally incomplete

This is not yet full TC6 essentia transport behavior. The following pieces still need implementation in later batches:

- actual suction propagation across neighboring transport nodes;
- full essentia transfer between smelters, tubes, jars and consumers;
- tube connection shape/model parity;
- filtered tube behavior beyond stored filter state;
- valve interaction behavior;
- one-way side selection and interaction;
- restrict tube suction rule parity;
- buffer internal behavior parity;
- smelter endpoint production/extraction logic;
- full persistence/sync verification with real gameplay objects;
- renderer/model parity against legacy TC6.

## Suggested next batch

The next practical batch should focus on behavior wiring instead of more registry scaffolding:

1. Implement neighbor scanning for `TCLegacyTubeBlockEntity`.
2. Add a minimal suction propagation pass based on the legacy tube model.
3. Add side-aware transfer from one transport node to another.
4. Add valve and one-way side gating.
5. Keep build and Thaumonomicon audit green.

Avoid adding new catalog placeholders here. The catalog bridge is complete; future work should convert placeholders into real mechanics while preserving the already green audit state.
