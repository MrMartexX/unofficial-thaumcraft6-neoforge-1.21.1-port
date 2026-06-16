# Transport/essentia blockentity wiring skeleton batch

This batch wires the legacy-aligned transport foundation into block and blockentity skeleton classes without registering the final blocks yet.

## Added

- `TCLegacyTubeVariant` maps tube catalog ids to legacy-aligned tube modes.
- `TCLegacyTubeBlock` is a common tube block shell.
- `TCLegacySmelterEndpoint` maps smelter endpoints to transport modes.
- `TCLegacySmelterEndpointBlock` is a smelter endpoint block shell.
- `TCAbstractEssentiaTransportBlockEntity` delegates transport behavior to `TCLegacyEssentiaTransportNode`.
- `TCLegacyTubeBlockEntity` stores tube variant state and transport node state.
- `TCLegacySmelterEndpointBlockEntity` stores smelter endpoint state and transport node state.

## Covered requirements

- common tube block class skeleton;
- tube variant enum/switch mapping;
- smelter endpoint skeleton;
- foundation transport node wiring;
- save/load baseline;
- block update/sync baseline;
- server tick skeleton;
- recipe/catalog audit remains green.

## Next step

Register these blocks and block entities in the project registry, then replace item-only placeholders for tube/smelter catalog ids with actual block items.
