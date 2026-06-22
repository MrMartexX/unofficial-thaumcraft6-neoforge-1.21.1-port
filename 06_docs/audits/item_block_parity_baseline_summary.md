# Item/block parity baseline summary

Generated: 2026-06-22T14:19:26.0326902Z

The executable baseline produced 1444 evaluated results: 1245 pass, 3 mapped rename, 1 intentional missing and 195 missing.

Of the missing results, 117 currently fall into safe resource-boundary checks. The baseline is intentionally generated with FailMode off until the rule overrides are reviewed.

There are 106 legacy source entries whose IDs were inferred from symbols and therefore did not contribute to a registry pass.

Intentional missing results by implemented check:

- block_item_pairs: 1

Missing results by implemented check:

- lang: 76
- models: 41
- registry: 78

This milestone covers only: registry, duplicate_registry_id, block_item_pairs, blockstates, models, lang. It does not claim behavior, runtime or visual parity.

Raw details: `tools/reports/local/item-block-parity/item_block_parity_report.md` (local, ignored).
