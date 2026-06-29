# Curated audits

This folder is for audit summaries that are useful after the original local run is gone.

Use it for:

- asset/model/texture parity summaries;
- research/scanning/recipe bridge summaries;
- build and dedicated server smoke-test notes;
- known visual parity decisions backed by screenshots or runtime checks.

Do not use it for every raw generated log. Temporary output belongs under `../../tools/reports/local/` and is ignored by git.

## Cross-cutting parity framework

- `legacy_source_selection.md` defines source authority and conflict handling.
- `item_block_parity_framework.md` defines the item/block evidence layers, fail policy and implementation sequence.
- `item_block_parity_layer_completion_matrix.md` defines which item/block layers are report-only evidence, strict blockers, or still review-only.
- The focused local command for visual evidence is `tools/audits/item-block-parity/audit-item-block-parity.ps1 -Preset visual -FailMode off`; raw output stays under `tools/reports/local/item-block-parity/`.
