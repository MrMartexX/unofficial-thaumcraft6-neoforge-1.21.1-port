# Item/block CI strict/safe policy

Batch 54 defines the CI policy matrix for the item/block parity framework. It does not promote strict runtime or visual certification by default.

## Policy modes

| Policy mode | Verifier FailMode | Intended trigger | Hard-fail boundary |
|---|---|---|---|
| `report_only` | `off` | default push, pull request and normal manual runs | parser errors, invalid rule JSON, missing required reports and generated report summaries with errors/failures |
| `safe` | `safe` | manual hardening run | mechanical framework errors only |
| `strict` | `strict` | manual opt-in certification run | safe-mode failures plus unresolved visual/runtime strict blockers |

## Strict blockers

Strict mode remains expected to fail until the following reports have no unresolved certification blockers:

- `item_block_visual_equivalence_completion_report.json`
- `item_block_runtime_smoke_report.json`
- `item_block_game_test_smoke_report.json`

## Default CI behavior

The workflow keeps `report_only` as the default policy mode and uploads JSON/Markdown artifacts on every run. `strict` is intentionally manual and opt-in.

## Local smoke command

```powershell
pwsh -NoProfile -File tools/audits/item-block-parity/audit-item-block-parity.ps1 -RepoRoot . -Checks ci_strict_safe_policy,check_invocation,report_schema,status_taxonomy,docs_deferred -UseCachedLegacy -FailMode off
```
