# Item/block parity CI framework verifier

Batch 50 adds a GitHub Actions report-only workflow for the item/block parity framework verifier.

## Workflow

Path: `.github/workflows/item-block-framework-verifier.yml`

The workflow runs on:

- `workflow_dispatch` for manual verifier runs;
- pull requests touching audit framework, audit documentation, or port source/resource paths;
- pushes to `main` touching the same paths.

## What it validates

The workflow invokes:

```powershell
pwsh -File tools/audits/item-block-parity/verify-item-block-parity-framework.ps1 -RepoRoot <repo> -FullAudit -FailMode off
```

Manual runs may choose `off`, `safe`, or `strict` fail mode. Push and pull request runs default to `off` so the framework remains report-only while runtime and visual policies are still being classified.

## Artifacts

The workflow uploads generated item/block parity framework reports from:

```text
tools/reports/local/item-block-parity/*.json
tools/reports/local/item-block-parity/*.md
tools/reports/local/item-block-parity/runtime-logs/**
```

Artifacts are retained for 14 days. They are intended for review and audit traceability; generated local reports remain uncommitted.

## Policy

CI report-only mode is a framework safety net. It should fail only for verifier errors, parser failures, broken rule JSON, broken owner wiring, or missing required workflow wiring. Review rows remain advisory until the relevant parity/runtime policy is promoted to safe or strict mode.
