# Asset audits

Reusable static asset audit scripts for the active NeoForge port.

## `static-asset-reference-audit.ps1`

Checks active Thaumcraft asset JSON under `05_neoforge_port/src/main/resources/assets/thaumcraft`.

Currently verifies:

- model `parent` references for Thaumcraft namespace models;
- blockstate `model` references;
- model `textures` references;
- NeoForge OBJ `model` and `mtl_override` references;
- legacy plural `items/` or `blocks/` texture references as warnings.

Run locally from the repository root:

```powershell
.\tools\audits\assets\static-asset-reference-audit.ps1
```

Temporary/generated outputs should go under `tools/reports/local/` unless a summary is curated into `06_docs/audits/`.
