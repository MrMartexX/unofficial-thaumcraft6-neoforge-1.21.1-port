# Smelter Runtime Boundary Audit

Updated: 2026-06-30

## Scope

This audit closes the server-owned TC6 Alembic/smelter machine boundary after comparing the port
against legacy `TileSmelter`, `TileAlembic`, `BlockSmelterAux`, `BlockSmelterVent`,
`ContainerSmelter` and `GuiSmelter`.

## Confirmed parity

| Area | Legacy behavior retained | Modern implementation |
|---|---|---|
| Tiers | Basic `80% / 15`, Thaumium `90% / 10`, Void `95% / 15` | One server-owned machine model with explicit tier |
| Storage | Two inventory slots, 256 slurry points | `WorldlyContainer` plus persisted `AspectList` |
| Automation | Side input, bottom fuel, no top slots | Sided NeoForge item capability adapters |
| Fuel | Alumentum 4800, Greatwood 500, Silverwood 400 | Furnace fuel data plus exact event override |
| Boost | Alumentum accelerates distillation by 20% | Output interval `15 -> 12`, `10 -> 8` |
| Conversion | Per-point tier loss; flux aspect uses `efficiency * 0.66` | Server world RNG, no client authority |
| Vents | Every valid side vent gets a 0.333 chance per lost point | Front excluded; cumulative diminishing-return loop |
| Alembics | Matching filled Alembic first, then first compatible empty/filter slot | Contiguous vertical column scan |
| Aux pumps | One additional output per valid attached aux each output cycle | Facing and attachment validated |
| GUI | Original two slots and five progress values | `TCSmelterMenu` / `TCSmelterScreen` with DataSlots |
| Models | Detailed legacy-derived Alembic/Bellows/aux/vent geometry | Modern blockstate/model resources |

## Corrected regressions

- Removed the invented upgraded-smelter essentia transport node. TC6 smelters are slurry machines,
  not tube endpoints.
- Restored detailed Bellows, auxiliary pump and vent models that had been replaced by cube
  placeholders.
- Replaced single-Alembic output with the exact matching-first column algorithm.
- Replaced one-vent-only mitigation with cumulative per-vent probability.
- Restored Alumentum boost and custom TC6 fuel values.
- Added exact aux/vent facing and side-attachment checks.
- Added server-authoritative menu, inventory dropping and sided automation.

## Runtime result

`tools/audits/audit-smelter-runtime-boundary.ps1` validates detailed resources and runs the combined
transport/machine exporter. Current combined result after the later Alembic/Jar item-transfer and
tube-caster control closure: **46/46 passed**.

## Deferred owning slices

- Final measured Bellows/valve/vent visual parity.
- Void Jar overflow and remaining importer/exporter/Thaumatorium consumers.
- Any remaining device-specific label renderer polish discovered during screenshot review.
