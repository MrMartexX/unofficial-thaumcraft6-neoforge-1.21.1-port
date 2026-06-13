# Shobie 1.20.1 Crucible Recipe Import

Date: 2026-06-14
Branch: `codex/experiment-shobie-1-20-merge`

## Scope

This import keeps the NeoForge 1.21.1 port as the base and treats Shobie 1.20.1 as a secondary recipe reference. Following the migration guide, the work ports the recipe role and data shape into modern `RecipeType` / `RecipeSerializer` infrastructure instead of copying Shobie Forge 1.20.1 Java or legacy 1.12.2 `TileCrucible` logic.

This is not full crucible gameplay yet. There is no crucible BlockEntity, water/heat handling, catalyst ejection, essentia cloud, boil FX, instability, or in-world alchemy loop in this step.

## Source Reliability

| Source | Purpose | Reliability | Notes |
|---|---|---:|---|
| Thaumcraft 6 1.12.2 source/jar | Authoritative behavior and naming reference | High | Legacy `CrucibleRecipe` semantics and registry ids remain the source of truth. |
| Shobie 1.20.1 port | Secondary data reference | Medium | Useful for broad recipe coverage, but ids and some recipe choices need legacy correction. |
| Current NeoForge 1.21.1 port | Runtime target | High | Uses modern singular `data/*/recipe`, `MapCodec`, `StreamCodec`, and current registered ids. |

## Runtime Import

Added a dedicated `thaumcraft:crucible` recipe type and serializer. The JSON shape is intentionally narrow:

| Field | Meaning |
|---|---|
| `type` | `thaumcraft:crucible` |
| `group` | Optional grouping label for recipe manager/UI consumers. |
| `research` | Optional legacy research key or stage key. This is data only until full crucible gating exists. |
| `catalyst` | Modern `Ingredient`, allowing item ids or tags. |
| `aspects` | Legacy aspect-cost map with positive integer amounts. |
| `result` | Modern 1.21 `ItemStack` result object using `id` and optional `count/components`. |

The generated aspect cache now understands crucible recipes using the legacy-derived rule:

1. Start with the catalyst stack aspects.
2. Add each required crucible aspect as `floor(sqrt(requiredAmount) / outputCount)`.
3. Remove zero or negative entries.

Exact assignments and tag assignments still win over generated fallback values.

## Imported Recipes

Currently `41` Shobie/legacy-derived recipes load after mapping ids back to the current 1.12-style port names and rejecting unsafe behavior-only entries. The first pass imported `27`; the 2026-06-14 expansion added safe registered identities that now have exact or acceptable bridge mappings.

| Recipe id | Notes |
|---|---|
| `alumentum` | Legacy alchemy output; behavior/explosion use remains deferred. |
| `bath_salts` | Identity recipe imported; bath/sanity gameplay remains deferred. |
| `brassingot` | Legacy brass crucible path using copper catalyst and `metallum`/`ordo`. |
| `focus_1` | Legacy blank lesser focus path using `crystal_ordo` catalyst. |
| `hedge_clay` | Hedge alchemy conversion. |
| `hedge_dye` | Hedge alchemy conversion. |
| `hedge_glowstone` | Hedge alchemy conversion. |
| `hedge_gunpowder` | Hedge alchemy conversion. |
| `hedge_lava` | Hedge alchemy conversion. |
| `hedge_leather` | Hedge alchemy conversion. |
| `hedge_slime` | Hedge alchemy conversion. |
| `hedge_string` | Hedge alchemy conversion. |
| `hedge_tallow` | Hedge alchemy conversion. |
| `hedge_web` | Hedge alchemy conversion. |
| `metal_purification_cinnabar` | Tag-backed ore purification; waits for real output behavior where needed. |
| `metal_purification_copper` | Tag-backed ore purification. |
| `metal_purification_gold` | Tag-backed ore purification. |
| `metal_purification_iron` | Tag-backed ore purification. |
| `nitor` | Corrected to legacy `nitor` key / `UNLOCKALCHEMY@3`; colored nitor behavior remains separate. |
| `thaumiumingot` | Legacy thaumium crucible path. |
| `vis_crystal_aer` | Legacy crystal growth path. |
| `vis_crystal_aqua` | Legacy crystal growth path. |
| `vis_crystal_ignis` | Legacy crystal growth path. |
| `vis_crystal_ordo` | Legacy crystal growth path. |
| `vis_crystal_perditio` | Legacy crystal growth path. |
| `vis_crystal_terra` | Legacy crystal growth path. |
| `voidingot` | Uses current `void_seed` identity item; void-metal behavior remains deferred. |
| `amber` | Legacy-style slime/vinculum route for amber identity. |
| `balanced_shard` | Current registered bridge identity for primal charm and related recipes. |
| `bottle_taint` | Uses the current `phial_vitium` identity instead of legacy filled-phial NBT. |
| `everfull_urn` | Registers and drops the placeholder urn block; real behavior is deferred. |
| `fabric_enchanted` | Current `fabric` identity bridge for recipe/aspect coverage. |
| `leather_from_flesh` | Hedge conversion route. |
| `phial_empty` | Current empty phial identity. |
| `primal_charm` | Crucible bridge using the registered `balanced_shard` identity. |
| `quicksilver` | Redstone/metallum/permutatio route. |
| `salis_mundus` | Registered bridge route for current recipe/aspect coverage; final use behavior remains separate. |
| `sanity_soap_from_tallow` | Registered soap identity path. |
| `sanity_soap_from_flesh_block` | Registered flesh block catalyst path; real sanity behavior remains deferred. |
| `void_seed` | Uses current `crystal_vitium` as the flux crystal bridge. |
| `zombie_brain` | Registered `brain` identity route. |

## Deferred Shobie Crucible Entries

`14` Shobie crucible entries remain deferred.

| Deferred area | Reason |
|---|---|
| `tallow_candles` | Legacy candle path is normal crafting, not this crucible serializer. |
| Seal recipes | Legacy seal type is data/NBT-specific; current `blank_seal` cannot preserve output subtype yet. |
| Behavior-heavy or conflicting entries | Entries that require real crucible BlockEntity behavior, item DataComponents, or conflict with confirmed TC6 legacy semantics are still held back instead of flattened into plain placeholder outputs. |

## Validation

Current validation after this import:

| Check | Result |
|---|---|
| `.\gradlew.bat build --no-daemon` | Passed. |
| `.\gradlew.bat runServer --no-daemon` | Reached `Done`. |
| Recipe count | `1533` loaded recipes. |
| Generated aspect cache | `620` generated object assignments from crafting, arcane, crucible, and infusion recipes. |
| Aspect coverage | `1230/1230` assignable current vanilla item ids have aspects. |
| Reload validation | `Thaumcraft aspect tag reload validation passed.` |

## Next

Before real crucible gameplay, add a focused crucible design slice covering BlockEntity state, water/heat, catalyst insertion, aspect consumption, pollution/flux side effects, in-world output spawning, particle/sound behavior, server authority, and Thaumonomicon recipe page rendering.

Infusion remains the next large Shobie custom recipe family, but it should follow the same pattern: serializer/data boundary first, behavior and visuals later.
