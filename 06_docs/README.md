# Port documentation

This folder is the main documentation area for the Thaumcraft 6 -> NeoForge 1.21.1 port.

## Start here

1. [Current task](CURRENT_TASK.md) - current working focus and project guardrails.
2. [Current port status](current_port_status.md) - current implementation status and document priority list.
3. [Legacy FX engine notes](legacy_fx_engine.md) - legacy FX/rendering notes.

## How to keep this folder usable

- Keep stable planning, decisions, migration notes and curated audits here.
- Keep reusable scripts under `../tools/`.
- Keep generated/local reports under `../tools/reports/local/` unless they are intentionally curated.
- Do not create a second docs folder for the same project state.

## Suggested subfolders

```text
06_docs/
  audits/      Curated audit summaries worth keeping.
  decisions/   Short architecture or parity decisions.
  migration/   Focused migration/API notes.
  references/  Small reference notes.
```
