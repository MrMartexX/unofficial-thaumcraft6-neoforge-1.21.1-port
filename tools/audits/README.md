# tools/audits

Reusable audit scripts belong here.

Suggested folders:

- `assets/` - model, texture, lang, blockstate, loot and creative tab parity checks.
- `research/` - research data, requirement, scanning, recipe bridge and Thaumonomicon checks.
- `runtime/` - runtime-oriented checks that are safe to run from Gradle or local scripts.
- `server/` - dedicated server checks.
- `item-block-parity/` - evidence-layered legacy/port registry and resource parity manifests.

Generated output should usually go to `tools/reports/` or be curated into `06_docs/audits/`.

The item/block framework now includes registry, resource, data/behavior/source-quality and report-only visual-boundary checks. Use `item-block-parity/audit-item-block-parity.ps1 -Preset visual -FailMode off` for the focused visual evidence pass. Read `../../06_docs/audits/item_block_parity_framework.md` before treating any output as a parity verdict; report-only visual evidence is not strict in-game visual parity.

Focused runtime scripts currently include `audit-infernal-furnace-behavior.ps1`, which writes the curated Infernal Furnace blocker report under `06_docs/audits/generated/`.
