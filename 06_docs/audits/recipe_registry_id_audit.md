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
## Local tag cleanup

Added local Thaumcraft item tags for `thaumcraft:candle_whites` and `thaumcraft:legacy_ore_dictionary/blockGlass`.

- `thaumcraft:candle_whites` is used by colored tallow candle recoloring recipes and intentionally points to `thaumcraft:candle_white`.
- `thaumcraft:legacy_ore_dictionary/blockGlass` bridges the legacy `blockGlass` OreDictionary input used by the Tube recipe to vanilla glass and stained glass item forms.
## ResourceLocation casing cleanup

Dedicated server datapack loading revealed invalid recipe tag resource locations with uppercase characters:

- `thaumcraft:legacy_ore_dictionary/blockGlass`
- `thaumcraft:legacy_ore_dictionary/nuggetBrass`
- `thaumcraft:legacy_ore_dictionary/nuggetQuicksilver`

These were rewritten to lowercase/snake_case tag IDs:

- `thaumcraft:legacy_ore_dictionary/block_glass`
- `thaumcraft:legacy_ore_dictionary/nugget_brass`
- `thaumcraft:legacy_ore_dictionary/nugget_quicksilver`

The recipe registry audit now validates ResourceLocation syntax for nested recipe `item`, `tag`, and `result.id` references.