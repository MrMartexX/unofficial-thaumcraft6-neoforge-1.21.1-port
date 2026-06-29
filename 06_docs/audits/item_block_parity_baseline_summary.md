# Item/block parity baseline summary

Generated: 2026-06-29T13:19:46.5690961Z

The executable baseline produced 2458 evaluated results: 2149 pass, 5 mapped rename, 2 variant mapped, 1 intentional missing and 301 missing.

Of the missing results, 103 currently fall into safe resource-boundary checks. The baseline is intentionally generated with FailMode off until the rule overrides are reviewed.

There are 106 legacy source entries whose IDs were inferred from symbols and therefore did not contribute to a registry pass.

Intentional missing results by implemented check:

- block_item_pairs: 1

Deferred results by implemented check:

- none: 0

Missing results by implemented check:

- lang: 69
- models: 34
- orphan_references: 62
- registry: 74
- textures: 62

The comparer portion of this milestone covers: registry, duplicate_registry_id, block_item_pairs, blockstates, models, textures, lang, orphan_references.

The `quick` preset also generated report-only visual-boundary subreports: `legacy_shape_parity`, `legacy_visual_collision_parity`, and `item_visual_parity`. These subreports identify review/mismatch rows, but they do not claim strict behavior, runtime, measured in-game visual, or pixel parity.

Raw details: `tools/reports/local/item-block-parity/item_block_parity_report.md` (local, ignored).
