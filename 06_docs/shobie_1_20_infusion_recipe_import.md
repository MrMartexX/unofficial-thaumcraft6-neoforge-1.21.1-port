# Shobie 1.20.1 Infusion Recipe Import

Date: 2026-06-13
Branch: `codex/experiment-shobie-1-20-merge`

## Scope

This document records the first safe infusion data import from the Shobie 1.20.1 port into the current NeoForge 1.21.1 port.

The current port stays authoritative. Shobie data is treated as a secondary reference only. The migration guide rule applied here is to port the role and validated data shape, not copy the Forge 1.20.1 implementation or accept mismatched ids.

This pass implements only the recipe serializer/data/aspect-cache boundary:

- `thaumcraft:infusion` recipe type and serializer;
- modern JSON recipe loading for simple infusion recipes;
- server/network recipe serialization;
- generated-aspect support using the legacy infusion formula;
- reload validation for the imported safe focus recipes.

This pass does not implement Infusion Matrix gameplay. Pedestal scanning, symmetry/stability, essentia/aspect consumption from jars, item motion, instability events, FX, sounds, research page renderers, and actual crafting execution remain deferred.

## Source Reliability

| Source | Use | Reliability | Notes |
|---|---|---|---|
| Legacy TC6 1.12.2 `ConfigRecipes.initializeInfusionRecipes` | Authoritative recipe ids, research keys, instability, components, aspect costs and outputs | High | Used to correct Shobie mismatches. |
| Shobie 1.20.1 recipe JSONs | Secondary structure/reference | Medium | Useful for candidate coverage, but not authoritative. At least `focus_greater` had a wrong output and several recipes require unported ids or component semantics. |
| Current NeoForge 1.21.1 port | Authoritative ids, tags, registered content and serializer shape | High for implemented slice | Uses singular `data/thaumcraft/recipe`, modern tags, current item ids, and the current aspect service. |

## Serializer Shape

Current `thaumcraft:infusion` JSON fields:

| Field | Meaning |
|---|---|
| `type` | Always `thaumcraft:infusion`. |
| `group` | Optional recipe-book/group field. |
| `research` | Optional legacy research key requirement, for example `FOCUSGREATER@1`. |
| `instability` | Legacy instability integer. Stored now; behavior comes later. |
| `central` | The central input `Ingredient`. |
| `components` | Ordered component `Ingredient` list. |
| `aspects` | Required aspect-cost map. Stored as `AspectList`. |
| `result` | Modern `ItemStack` result. |

## Imported Recipes

| Recipe | Legacy source | Current JSON | Decision | Notes |
|---|---|---|---|---|
| `thaumcraft:focus_2` | `new InfusionRecipe("FOCUSADVANCED@1", new ItemStack(ItemsTC.focus2), 3, praecantatio 25 + ordo 50, focus1, quicksilver, gemDiamond, quicksilver, ender_pearl)` | `data/thaumcraft/recipe/infusion/focus_2.json` | Imported | Uses current `thaumcraft:focus_1`, `thaumcraft:quicksilver`, `c:gems/diamond`, and `minecraft:ender_pearl`. |
| `thaumcraft:focus_3` | `new InfusionRecipe("FOCUSGREATER@1", new ItemStack(ItemsTC.focus3), 5, praecantatio 25 + ordo 50 + vacuos 100, focus2, quicksilver, primordialPearl, quicksilver, nether_star)` | `data/thaumcraft/recipe/infusion/focus_3.json` | Imported, legacy-corrected | Shobie `focus_greater.json` returned `focus_advanced`; current import corrects this to legacy `focus_3`. |

The obsolete temporary research bridge recipes for `focus_2` and `focus_3` were removed. These ids now resolve through the real infusion recipe type for data loading and generated aspect calculation.

## Generated Aspect Behavior

Legacy `ThaumcraftCraftingManager.generateTagsFromInfusionRecipes` does:

1. Resolve the matching infusion recipe for the output stack.
2. Build an ingredient list from central input plus components.
3. Apply the same `getAspectsFromIngredients` formula used by normal crafting, without remaining-item subtraction.
4. Add each recipe aspect as `floor(sqrt(requiredAspectAmount) / outputCount)`.
5. Remove non-positive entries.

The port mirrors that behavior in `TCGeneratedAspectRecipeGenerator` for the implemented current recipe set. Generation priority remains:

1. exact item assignment;
2. tag assignment;
3. generated crucible recipe;
4. generated infusion recipe;
5. generated standard/arcane crafting recipe with the lowest positive total.

The recipe-source list uses the full loaded recipe registry and deterministic id sorting, because relying on ordered crafting views can hide alternative vanilla candidates and break legacy-like generated values. Broad modern tags use audited legacy-preferred representative ingredients for aspect generation only; they do not change recipe matching.

Reload validation covers:

| Output | Validation |
|---|---|
| `thaumcraft:focus_2` | Infusion-generated aspects match the current legacy formula fixture. |
| `thaumcraft:focus_3` | Infusion-generated aspects match the current legacy formula fixture, including stable `AspectList` insertion order. |

## Deferred Shobie Infusion Recipes

Shobie contains `62` infusion JSON files. This pass imports `2` and defers `60`.

| Deferred family | Count / examples | Reason |
|---|---|---|
| Seal variants | `seal_breaker`, `seal_harvest`, `seal_butcher` | Legacy output is a component/NBT-specific seal stack; current `blank_seal` identity cannot preserve seal subtype yet. |
| Crystal clusters | `crystal_cluster_air`, `crystal_cluster_fire`, etc. | Current crystal block ids exist, but cluster growth/farm behavior, crystal essence mapping and block behavior need a focused review before data import. |
| Duplicate or wrong-type entries | `goggles_revealing`, `stabilizer` | Existing authoritative arcane recipes already cover these identities or the Shobie recipe expresses a later behavior path, not the base item recipe. |
| Base thaumium/void equipment | `thaumium_*`, `void_*` | TC6 legacy uses normal crafting for base equipment; Shobie infusion versions are not legacy-correct for this stage. |
| Component/variant-heavy outputs | fortress masks/robes, verdant charms, vis amulet, runic-like baubles | Need data components, accessories/Curios decision, armor behavior and exact variant mapping before import. |
| Unregistered or behavior-heavy machines/items | arcane bore, biothaumic mind, causality collapser, charm undying, cloud ring, curiosity band, elemental tools, grapple gun, hand mirror, infernal furnace, jar brain, lamps, mirrors, primal crusher, traveller boots, vis generator, void siphon, voidseer charm | Output ids, block entities, item behavior, capabilities, or rendering are not ready. Importing these now would create fake gameplay or broken page data. |

## Validation

Current checkpoint:

```powershell
.\gradlew.bat clean build --no-daemon
.\tools\ci\server-smoke.ps1 -TimeoutSeconds 420
```

The dedicated server smoke test reached `Done`, loaded `1479` recipes, rebuilt the generated aspect cache from crafting, arcane, crucible and infusion recipes with `581` generated assignments, and passed aspect tag reload validation.

## Next Work

Do not add the remaining infusion recipes by bulk copy. The next safe infusion step is a focused `infusion_design.md` / behavior slice covering:

- Infusion Matrix and pedestal block identities;
- central input plus component discovery around the matrix;
- stability/symmetry rules;
- aspect storage and consumption source;
- research checks and server-owned crafting transaction;
- FX/sounds and failure behavior;
- Thaumonomicon infusion recipe page snapshots/rendering.
