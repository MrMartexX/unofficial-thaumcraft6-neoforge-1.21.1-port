# Recipe cleanup verification

Last updated: 2026-06-17

The recipe cleanup verification checkpoint confirms:

- Arcane caster placeholder audit reports no unresolved duplicated caster-shaped recipes.
- caster_basic.json is intentionally ignored as an exact legacy recipe match.
- Recipe registry ID audit reports no missing local 	haumcraft: item/block IDs.
- Recipe registry ID audit reports no missing local 	haumcraft: tag files after adding candle_whites and legacy_ore_dictionary/blockGlass.

## Commands

`powershell
pwsh -ExecutionPolicy Bypass -File .\tools\audits\audit-arcane-recipe-placeholders.ps1 -RepoRoot "D:\Thaumcraft_6_port_to_1.21.1"
pwsh -ExecutionPolicy Bypass -File .\tools\audits\audit-recipe-registry-ids.ps1 -RepoRoot "D:\Thaumcraft_6_port_to_1.21.1" -FailOnMissing
`

## Scope

This verification proves recipe JSON structure and local registry/tag identity resolution. It does not claim final gameplay behavior for placeholder bridge items, inventory items, turrets, focus pouch, mnemonic matrix, primordial pearl behavior, or advanced alchemical construct behavior.
## Follow-up server smoke correction

A follow-up dedicated server smoke exposed uppercase legacy tag paths that are invalid ResourceLocations in Minecraft 1.21.1. The affected tag IDs were rewritten to lowercase/snake_case and the recipe registry audit was extended to validate ResourceLocation syntax.