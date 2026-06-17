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
  - Status: resolved as bridge recipe data.
  - Added `thaumcraft:mindclockwork_advanced` as explicit bridge identity for legacy `ItemsTC.mind` metadata `1`.
  - Recipe now uses `thaumcraft:automatedcrossbow`, `c:plates/iron`, and `thaumcraft:mindclockwork_advanced`.

- `advalchemyconstruct.json`
  - Status: resolved as bridge recipe data.
  - Added `thaumcraft:primordial_pearl` as explicit bridge identity for legacy `ItemsTC.primordialPearl`.
  - Recipe output is mapped to `thaumcraft:metal_alchemical_advanced`, matching legacy `BlocksTC.metalAlchemicalAdvanced`.

## Recommended next implementation step

1. Re-run `tools/audits/audit-arcane-recipe-placeholders.ps1`.
2. The only expected ignored match should be `caster_basic.json`.
3. Keep `caster_basic.json` unchanged.