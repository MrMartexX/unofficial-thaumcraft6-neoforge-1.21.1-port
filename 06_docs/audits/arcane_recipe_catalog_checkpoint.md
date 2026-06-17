# Arcane recipe and Thaumonomicon catalog checkpoint

Source: runtime audits generated after `a38f6ea3d6129602c19a644753abf942d2dc64d7`.

## Current result

| Area | Result |
|---|---:|
| Arcane recipes loaded | `14` |
| Arcane recipe audit checks | `PASS` |
| Thaumonomicon ready arcane entries | `14` |
| Thaumonomicon deferred arcane entries | `75` |
| Thaumonomicon ready crafting entries | `7` |
| Thaumonomicon deferred crafting entries | `74` |

The current implemented arcane recipe slice is internally consistent: every currently loaded arcane recipe has a matching custom arcane recipe type, expected research gate, vis/crystal cost, result stack, pattern/ingredient check, and a ready Thaumonomicon arcane page snapshot.

## Ready arcane recipe slice

These are currently loaded and audit-clean:

- `thaumcraft:thaumometer`
- `thaumcraft:vis_resonator`
- `thaumcraft:workbenchcharger`
- `thaumcraft:goggles`
- `thaumcraft:mechanism_simple`
- `thaumcraft:mechanism_complex`
- `thaumcraft:wand_workbench`
- `thaumcraft:caster_basic`
- `thaumcraft:enchantedfabric`
- `thaumcraft:mirrorglass`
- `thaumcraft:filter`
- `thaumcraft:morphicresonator`
- `thaumcraft:essentiasmelter`
- `thaumcraft:infusionmatrix`

## Deferred arcane entries are not all simple missing recipe files

The deferred arcane list contains many entries whose outputs or backing gameplay systems are not ported yet. They must not be blindly converted to ready pages until their registered item/block, block entity, renderer, menu, or gameplay behavior exists.

Large batches should therefore be cut by subsystem readiness, not by catalog list order.

## Next large recipe/catalog batch candidates

### Safe crafting/data batch candidates

These are good candidates for one future grouped commit because they mostly depend on registered base items/blocks rather than new BlockEntity systems:

- Brass and thaumium block conversion recipes:
  - `thaumcraft:brassingotstoblock`
  - `thaumcraft:brassblocktoingots`
  - `thaumcraft:thaumiumingotstoblock`
  - `thaumcraft:thaumiumblocktoingots`
- Arcane stone/basic decorative crafting:
  - `thaumcraft:stonearcane`
  - `thaumcraft:brickarcane`
- Nitor color conversion recipes, only if all sixteen Nitor block items stay intentionally registered:
  - `thaumcraft:nitordyeblack`
  - `thaumcraft:nitordyeblue`
  - `thaumcraft:nitordyebrown`
  - `thaumcraft:nitordyecyan`
  - `thaumcraft:nitordyegray`
  - `thaumcraft:nitordyegreen`
  - `thaumcraft:nitordyelightblue`
  - `thaumcraft:nitordyelime`
  - `thaumcraft:nitordyemagenta`
  - `thaumcraft:nitordyeorange`
  - `thaumcraft:nitordyepink`
  - `thaumcraft:nitordyepurple`
  - `thaumcraft:nitordyered`
  - `thaumcraft:nitordyesilver`
  - `thaumcraft:nitordyewhite`
  - `thaumcraft:nitordyeyellow`

### Defer until subsystem exists

Do not add recipes/pages for these as isolated JSON-only fixes. They need their actual systems first:

- Essentia transport and tubes:
  - `thaumcraft:essentiatransportin`
  - `thaumcraft:essentiatransportout`
  - `thaumcraft:tube`
  - `thaumcraft:tube_buffer`
  - `thaumcraft:tube_filter`
  - `thaumcraft:tube_oneway`
  - `thaumcraft:tube_restrict`
  - `thaumcraft:tube_valve`
- Smelter upgrades and auxiliaries:
  - `thaumcraft:essentiasmelterthaumium`
  - `thaumcraft:essentiasmeltervoid`
  - `thaumcraft:smelter_aux`
  - `thaumcraft:smelter_vent`
- Devices with missing or incomplete block/menu/gameplay systems:
  - `thaumcraft:alchemicalconstruct`
  - `thaumcraft:advalchemyconstruct`
  - `thaumcraft:alembic`
  - `thaumcraft:bellows`
  - `thaumcraft:centrifuge`
  - `thaumcraft:condenser`
  - `thaumcraft:dioptra`
  - `thaumcraft:hungrychest`
  - `thaumcraft:levitator`
  - `thaumcraft:patterncrafter`
  - `thaumcraft:visbattery`
  - `thaumcraft:visgenerator`
  - `thaumcraft:wardedjar`

## Working rule for future commits

Do not turn a deferred catalog page into ready just because a legacy recipe id exists. A page becomes ready only when all three are true:

1. The recipe JSON exists and loads under the correct modern recipe type.
2. The result item/block is registered and has usable assets.
3. The backing gameplay subsystem is implemented enough that exposing the recipe does not create a fake completed feature.

This keeps recipe/catalog progress large but avoids placeholder-driven false parity.
