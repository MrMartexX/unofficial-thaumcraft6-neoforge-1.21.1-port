# 6_docs

This folder is the curated documentation area for the Thaumcraft 6 NeoForge 1.21.1 port.

Use this folder for stable project notes, migration decisions, parity reports, and reusable audit summaries. Temporary local logs and one-off generated reports should stay outside committed documentation unless they are intentionally curated.

## Suggested structure

```text
6_docs/
  README.md
  CURRENT_TASK.md
  migration/
  audits/
  decisions/
  references/
```

## Folder roles

- `migration/` — migration guides, subsystem migration notes, API mapping notes.
- `audits/` — curated audit results that are useful to preserve across sessions.
- `decisions/` — short architecture or parity decisions, especially when a behavior was deliberately matched to legacy TC6.
- `references/` — small reference notes that are useful but not active tasks.

## Rules

- Keep root-level repository output clean.
- Do not commit throwaway `*_audit.txt`, `*_audit.csv`, or runtime logs unless they have been reviewed and moved here intentionally.
- Prefer short, dated notes over large unstructured dumps.
- When a subsystem changes meaningfully, update `CURRENT_TASK.md` or add a focused note under `decisions/` or `audits/`.
