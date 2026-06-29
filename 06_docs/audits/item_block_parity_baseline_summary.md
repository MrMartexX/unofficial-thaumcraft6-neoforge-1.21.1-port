# Item/block parity baseline summary

Generated: 2026-06-29T14:02:02.3699220Z

The focused `visual` preset comparer portion produced 1635 evaluated results: 1477 pass, 0 mapped rename, 0 variant mapped, 0 intentional missing and 158 missing.

Of the missing results, 34 currently fall into safe resource-boundary checks. The baseline is intentionally generated with FailMode off until the rule overrides are reviewed.

There are 106 legacy source entries whose IDs were inferred from symbols and therefore did not contribute to a registry pass.

Intentional missing results by implemented check:

- none: 0

Deferred results by implemented check:

- none: 0

Missing results by implemented check:

- models: 34
- orphan_references: 62
- textures: 62

The comparer portion of this focused run covers only: blockstates, models, textures, orphan_references. It does not claim behavior, runtime or measured in-game visual parity by itself.

The same `visual` preset also generated report-only visual-boundary subreports: `visual_boundary`, `item_visual_parity`, `legacy_shape_parity`, `legacy_visual_collision_parity`, `texture_color`, `sounds_particles`, and `visual_equivalence_completion`. Current visual subreports classify the known `golem_builder` and `research_table` facing-domain mismatches, `outline_contract` evidence, active texture SHA/color/alpha parity, item model/placeholder risks, and FX review debt before any strict visual certification can be considered.

Raw details: `tools/reports/local/item-block-parity/item_block_parity_report.md` (local, ignored).
