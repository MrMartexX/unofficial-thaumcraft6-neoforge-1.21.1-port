# Remaining arcane recipe placeholder audit

Last updated: 2026-06-17

This note documents the small set of arcane recipe JSONs that still match the original duplicated `caster_basic` placeholder shape after the broad recipe cleanup pass.

## Known exact legacy false positive

- `caster_basic.json`
  - Status: intentionally unchanged.
  - Reason: legacy Thaumcraft 6 `caster_basic` uses the same research, vis, six primal crystals, pattern, and key layout as the generated placeholder shape:
    - research `UNLOCKAUROMANCY@2`
    - vis `100`
    - crystals `aer`, `terra`, `aqua`, `ignis`, `ordo`, `perditio`
    - pattern `III / LRL / LTL`
    - key `T = thaumometer`, `R = vis_resonator`, `L = leather`, `I = iron ingot`
  - The audit tool excludes this recipe by default. Pass `-IncludeKnownExact` to include it in the output.

## Deferred, not safe for recipe-only cleanup yet

- `advancedcrossbow.json`
  - Legacy input requires `ItemsTC.mind` metadata `1`.
  - Current recipe cleanup only bridged `mindclockwork`, corresponding to legacy `ItemsTC.mind` metadata `0`.
  - Do not rewrite this recipe until the metadata `1` identity is explicitly bridged.

- `advalchemyconstruct.json`
  - Legacy recipe output is `BlocksTC.metalAlchemicalAdvanced`, not a standalone `advalchemyconstruct` block/item.
  - Legacy input requires `ItemsTC.primordialPearl`.
  - Do not rewrite this recipe until the primordial pearl item identity and output mapping are explicitly audited/bridged.

## Recommended next implementation step

1. Add or verify explicit bridge identity for legacy `ItemsTC.mind` metadata `1`.
2. Add or verify explicit bridge identity for legacy `ItemsTC.primordialPearl`.
3. Then repair `advancedcrossbow.json` and `advalchemyconstruct.json`.
4. Keep `caster_basic.json` unchanged.