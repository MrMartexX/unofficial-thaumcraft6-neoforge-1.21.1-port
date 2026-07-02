# Essentia Transport Behavior Batch

Date: 2026-06-21

## Result

- Dedicated-server runtime audit: `23/23` checks passed.
- Normal tube capacity: `1`.
- Buffer total capacity: `10`.
- Normal suction propagation: `32 -> 31`.
- Restrict propagation: `32 -> 16`.
- Filter, one-way, valve and three-level buffer choke rules are covered.
- Warded Jar five-tick one-point pull, filtered suction and all-or-nothing transport extraction are covered.
- Venting pause, multipart connection state, sided NeoForge capability visibility, reciprocal side closure, update-tag fields and NBT round-trip are covered.
- Test-world cleanup exits without stale BlockEntity warnings.

## Resource correction

The static/full-cube tube placeholders were replaced with modern-path adaptations of the legacy multipart blockstates and existing tube core/side model assets. The normal tube item model again uses the legacy `tube_normal` 2D icon.

## Remaining boundary

This batch does not mark Bellows, caster sub-part interaction, vent/valve client rendering, Alembic or smelter gameplay complete. See `06_docs/gameplay/essentia_transport_design.md`.

## 2026-07-02 Transfuser Extension

- Dedicated-server runtime audit now passes `52/52`.
- `essentiatransportin` and `essentiatransportout` are real blocks/block items instead of catalog-only item placeholders.
- The port now covers legacy Essentia Input/Output placement, six-facing AABBs, back-face-only capability access, input suction `128`, output suction `0`, five-tick transfer cadence, and 16-block remote source-container search.
- Thaumatorium recipe components now point at the real transport input/output ids instead of temporary `essentia_importer` / `essentia_exporter` placeholders.
