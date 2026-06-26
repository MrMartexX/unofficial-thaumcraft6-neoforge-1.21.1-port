# Item/block final framework completion audit

Batch 55 closes the item/block parity framework contract. It does not claim that all Thaumcraft gameplay parity is complete.

## Added files

- `tools/audits/item-block-parity/modules/final_framework_completion.ps1`
- `tools/audits/item-block-parity/rules/final-framework-completion.json`

## What this certifies

The framework is considered production-grade for report-only parity evidence collection when:

- every registered framework check is implemented;
- every implemented check has an invocation owner;
- required JSON/Markdown reports are generated, schema-visible and error-free;
- verifier v2 consumes framework, runtime, visual and CI policy reports;
- GitHub Actions keeps report-only as default and exposes manual safe/strict policy modes;
- unresolved visual/runtime blockers remain review-only until explicit strict promotion.

## What this does not certify

This is not a gameplay parity completion claim. Remaining review rows in registry, visual, runtime, data or behavior reports still require classification or implementation before strict gameplay parity can be claimed.

## Recommended command

```powershell
pwsh -NoProfile -File tools/audits/item-block-parity/audit-item-block-parity.ps1 -RepoRoot . -Checks final_framework_completion,ci_strict_safe_policy,visual_equivalence_completion,runtime_smoke,game_test_smoke,check_invocation,docs_deferred,status_taxonomy,report_freshness,report_schema -UseCachedLegacy -FailMode off
```

## Promotion boundary

`report_only` remains the default operating mode. `safe` is for mechanical framework hard-fail runs. `strict` remains manual and expected to be blocked until visual and runtime certification blockers are resolved.
