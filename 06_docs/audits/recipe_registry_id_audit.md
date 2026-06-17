# Recipe registry ID audit

Last updated: 2026-06-17

This audit checks recipe JSON files under:

- `05_neoforge_port/src/main/resources/data/thaumcraft/recipe`

It extracts nested `item`, `result.id`, and `tag` references and compares local `thaumcraft:` item/result IDs against the IDs registered through:

- `TCItems.ITEMS.register(...)`
- `TCItems.simpleItem(...)`
- `TCItems.blockItem(...)`
- `TCBlocks.BLOCKS.register(...)`

## Usage

```powershell
pwsh -ExecutionPolicy Bypass -File .\tools\audits\audit-recipe-registry-ids.ps1 -RepoRoot "D:\Thaumcraft_6_port_to_1.21.1"
```

Use `-FailOnMissing` for CI-like failure behavior.

Use `-IncludeExternalTags` to list non-local external tags such as `c:` tags. These are informational because many common tags are intentionally supplied by NeoForge or other data providers.

## Scope notes

- `minecraft:` item and result IDs are treated as external vanilla IDs and are not locally enumerated.
- Non-`thaumcraft:` tags are not treated as failures by default.
- Missing local `thaumcraft:` tag JSON files are treated as actionable.
- This is a structural data audit; it does not prove gameplay behavior is ported.
## Parser correction

The first version of this audit did not include `TCItems.simpleItem("...")` calls, so it over-reported active simple item IDs such as `vis_resonator`, `mechanism_simple`, plates, ingots, baubles, and bridge items as missing. The parser now includes `simpleItem(...)` registrations.