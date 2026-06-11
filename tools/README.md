# tools

Reusable helper scripts for the Thaumcraft 6 NeoForge 1.21.1 port.

## Structure

```text
tools/
  ci/       Scripts called by GitHub Actions.
  audits/   Reusable static or runtime audit scripts.
  reports/  Local generated outputs. Most report files are ignored by git.
```

## Rules

- Put reusable scripts here instead of the repository root.
- Put temporary local output under `tools/reports/local/`.
- Commit only curated reports that are useful for future comparison.
- One-off patch scripts should usually not be committed after they have served their purpose.
