# Item/block parity golden focused families

Batch 51 adds stable, dependency-aware focused audit slices for day-to-day regression checks.

## Purpose

Golden focused families are not new parity checks. They are small report-only runners that exercise existing checks against representative item/block families so regressions are caught before broad reports become noisy.

## Files

- Rules: `tools/audits/item-block-parity/rules/golden-focused-families.json`
- Runner: `tools/audits/item-block-parity/run-golden-focused-families.ps1`
- Reports: `tools/reports/local/item-block-parity/item_block_golden_focused_families_report.json` and `.md`
- Logs: `tools/reports/local/item-block-parity/golden-focused-family-logs/`

## Default families

| Family | Intent |
|---|---|
| `crystals` | Crystal block/item resources and direct dependencies |
| `jars` | Jar-like storage blocks and direct dependencies |
| `tables` | Table/workbench style crafting/UI blocks and direct dependencies |
| `thaumonomicon` | Thaumonomicon item/UI entry point and direct dependencies |
| `golems` | Golem-linked item/entity/resource clues and direct dependencies |

## Usage

Run all golden focused families:

```powershell
pwsh -File tools/audits/item-block-parity/run-golden-focused-families.ps1 -RepoRoot <repo> -FailMode off
```

Run a single family:

```powershell
pwsh -File tools/audits/item-block-parity/run-golden-focused-families.ps1 -RepoRoot <repo> -Families thaumonomicon -FailMode off
```

The runner uses the existing audit orchestrator with `-Preset ci-safe`, `-Families`, `-IdPrefix`, and dependency-aware focus expansion. Generated reports are local artifacts and remain uncommitted.

## Policy

Golden focused families are a regression safety net. They do not certify gameplay parity and should stay report-only until individual family policies are promoted to safe or strict checks.
