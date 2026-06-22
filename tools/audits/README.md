# tools/audits

Reusable audit scripts belong here.

Suggested folders:

- `assets/` - model, texture, lang, blockstate, loot and creative tab parity checks.
- `research/` - research data, requirement, scanning, recipe bridge and Thaumonomicon checks.
- `runtime/` - runtime-oriented checks that are safe to run from Gradle or local scripts.
- `server/` - dedicated server checks.
- `item-block-parity/` - evidence-layered legacy/port registry and resource parity manifests.

Generated output should usually go to `tools/reports/` or be curated into `6_docs/audits/`.

The item/block framework starts with registry and resource boundaries only. Read `../../06_docs/audits/item_block_parity_framework.md` before treating its output as a parity verdict.
