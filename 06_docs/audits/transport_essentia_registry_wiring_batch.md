# Transport/essentia registry wiring batch

This batch converts the transport/essentia catalog ids from item-only placeholders into registered blocks, block items and block entity types.

## Wired ids

- `thaumcraft:tube`
- `thaumcraft:tube_buffer`
- `thaumcraft:tube_filter`
- `thaumcraft:tube_oneway`
- `thaumcraft:tube_restrict`
- `thaumcraft:tube_valve`
- `thaumcraft:essentiasmelterthaumium`
- `thaumcraft:essentiasmeltervoid`

## Added registry wiring

- TCBlocks block registrations for all tube and smelter endpoint ids.
- TCItems placeholder fields remapped to BlockItem registrations.
- TCBlockEntities registrations for all tube variants and smelter endpoints.
- Basic blockstate, block model and item model assets.

## Still not final

The blocks now have block entity wiring, but full legacy suction graph ticking, renderer parity and GUI/inventory behavior still remain later passes.
