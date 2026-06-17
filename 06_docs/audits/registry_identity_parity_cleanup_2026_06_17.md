# Registry identity parity cleanup - 2026-06-17

This cleanup fixes registry/resource ids that were technically loadable in the NeoForge port but did not match Thaumcraft 6 1.12.2 public ids.

## Corrected active ids

| Area | Previous port ids | Legacy-aligned ids after cleanup | Notes |
|---|---|---|---|
| Tallow candles | `tallow_candle`, `tallow_candle_<color>` | `candle_<color>` | Legacy `ConfigBlocks` registered only dyed candle blocks. The base `tallow_candle` block id did not exist in TC6. The base `TallowCandle` recipe now outputs `3x thaumcraft:candle_white`, matching legacy recipe intent. |
| Essentia tubes | `tubebuffer`, `tubefilter`, `tubeoneway`, `tuberestrict`, `tubevalve` | `tube_buffer`, `tube_filter`, `tube_oneway`, `tube_restrict`, `tube_valve` | Legacy recipe ids such as `TubeBuffer` normalize to no-underscore catalog ids in some audit data, but the actual block/item registry outputs are underscore ids. The port keeps alias handling for old catalog ids while registering the block, item and BlockEntityType ids with underscores. |
| Smelter auxiliaries | `smelteraux`, `smeltervent` placeholder items | `smelter_aux`, `smelter_vent` placeholder items | These remain item-only placeholders until the real auxiliary pump and vent block/entity behavior is ported, but their public ids now match legacy block/resource ids. |

## Resource policy

- Active 1.21 resources remain under modern paths: `assets/thaumcraft/blockstates`, `models/block`, `models/item`, and `data/thaumcraft/recipe`.
- Old copied legacy blockstate JSON is not authoritative for registered blocks when it uses Forge 1.12-only shapes or model paths.
- Recipe file names can still mirror legacy recipe registry names such as `tubebuffer.json` or `tallowcandle.json`; the important public identity is the result item/block id.
- Candle resources use the existing legacy `thaumcraft:block/candle` tinted model through modern `blockstates/candle_<color>.json` and `models/item/candle_<color>.json`; colors are supplied by the NeoForge client color handler to match the TC6 `ColorHandler` pattern.
- Tube resources use static modern blockstates for the current shell blocks because the ported `TCLegacyTubeBlock` does not yet expose the legacy `north/east/south/west/up/down` connection properties. The legacy multipart side/core models remain reference material for the later transport-logic render pass.
- `research_page_catalog/legacy_builtin.json` may keep legacy recipe ids such as `thaumcraft:tubebuffer` because those ids address recipe/page catalog entries, not public block/item registry ids. Their `legacy_output.item` fields must point to the corrected public ids.

## Validation targets

- All registered blocks must have a blockstate.
- All registered items must have an item model.
- All `data/thaumcraft/recipe` outputs in the `thaumcraft` namespace must resolve to a registered item or block item.
- Client and server smoke tests must stay free of missing model/texture and unknown recipe result errors.
