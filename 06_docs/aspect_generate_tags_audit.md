# Thaumcraft 6 Legacy generateTags Audit

Last reviewed branch: `main`
Target module: `05_neoforge_port`

Legacy references:

- `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java`
- `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/lib/InternalMethodHandler.java`
- `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/config/ConfigAspects.java`
- `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/api/OreDictionaryEntries.java`
- `06_docs/NeoForge_legacy_migration_guide_expanded_v3.docx`

## Purpose

This document records the exact legacy `generateTags` behavior that must be preserved before assigning aspects to recipe-derived entries such as planks, slabs, stairs, amber blocks, amber bricks, or fabric.

Guide rule applied: old `OreDictionary` memberships become tags, but recipe-derived behavior must not be reduced to a guessed tag or copied as old Forge code. Recipe lookup, custom Thaumcraft recipes, caches, and data-driven serializers need a modern design before implementation.

## Legacy Lookup Flow

| Step | Legacy method | Behavior | Port decision |
|---|---|---|---|
| Public API bridge | `InternalMethodHandler#getObjectAspects` | Delegates to `ThaumcraftCraftingManager.getObjectTags`. | Preserve concept through `TCAspectAssignments`/future service. |
| Public generation bridge | `InternalMethodHandler#generateTags` | Delegates to `ThaumcraftCraftingManager.generateTags`. | Current port returns cached generated entries through `AspectHelper.generateTags`; generation itself is reload-owned. |
| Exact lookup | `getObjectTags(ItemStack, history)` | Looks up `CommonInternals.objectTags` by unique stack id. | Current port uses exact `ResourceLocation` ids for registered non-variant items. |
| Wildcard lookup | `getObjectTags` | Retries damage `32767`, then scans metadata `0..15` when input itself is wildcard. | Deferred; current registered ids are flattened. |
| Stripped lookup | `getObjectTags` | Retries stripped stack id without volatile data. | Deferred until data components/NBT-sensitive items exist. |
| Generated fallback | `getObjectTags` | Calls `generateTags` only after exact/wildcard/stripped lookup fails. | Current scaffold enforces last priority: `exact id > audited tag > generated cache`. |
| Bonus aspects | `getBonusTags` | Adds aspects for essentia containers, armor/tools/weapons, dyes, enchantments, and potion-like data. | Implemented for current vanilla armor/tools/weapons/dyes/enchantments/potions through `TCAspectStackRules`; essentia containers are deferred. |

## Legacy Generation Flow

| Step | Legacy method | Behavior | Port blocker |
|---|---|---|---|
| Stack normalization | `generateTags(ItemStack, history)` | Copies stack, sets count to `1`, and sets damage to wildcard for damageable or non-subtype items. | Requires modern item component/variant policy. |
| Existing-tag guard | `ThaumcraftApi.exists(stack)` | If a tag exists after normalization, returns `getObjectTags` instead of generating. | Current assignment service checks exact/tag assignments before generated cache. |
| Recursion guard | Serialized stack NBT history | Stops cycles and caps history at `< 100`. | Current crafting generator uses `TCAspectStackKey` history and the same `< 100` cap. |
| Crucible output | `generateTagsFromCrucibleRecipes` | Uses catalyst aspects plus square-root-scaled recipe aspects divided by output count. | Requires crucible recipe type/serializer and catalyst matching. |
| Infusion output | `generateTagsFromInfusionRecipes` | Uses central input plus components plus square-root-scaled recipe aspects divided by output count. | Requires infusion recipe type/serializer and ingredient model. |
| Crafting output | `generateTagsFromCraftingRecipes` | Scans every crafting recipe and chooses the lowest positive generated aspect total for matching output item/damage. | Implemented for standard `RecipeType.CRAFTING`; custom Thaumcraft recipes are still deferred. |
| Ingredient aspects | `getAspectsFromIngredients` | Uses first matching stack from each `Ingredient`, subtracts remaining items, applies `0.75 / output count`, floors to int, and removes non-positive amounts. | Implemented for standard crafting recipes; more parity fixtures are still needed for shapeless and remaining-item cases. |
| Arcane crafting bonus | `generateTagsFromCraftingRecipes` | Adds `MAGIC` based on `sqrt(1 + vis / 2) / output count` for `IArcaneRecipe`. | Requires arcane recipe design. |
| Caching side effect | `generateTags` | Registers generated aspects back into `ThaumcraftApi.registerObjectTag`. | Ported as reload-safe `TCGeneratedAspectCache`, rebuilt after server data/tag reload. |

## Current Port Decision

`AspectHelper.generateTags` now returns cached generated entries for the implemented vanilla crafting slice. It does not scan recipes on lookup. `TCGeneratedAspectRecipeGenerator` rebuilds the generated cache after server data/tag reload, using loaded `RecipeType.CRAFTING` recipes.

Current safety boundary: generated recipe outputs are restricted to the `minecraft` and `thaumcraft` namespaces. Vanilla items can be exact/tag/manual seeds and recipe ingredients, and standard vanilla crafting outputs can be generated after server data/tag reload. Third-party modded recipe outputs are not generated until their policy is audited.

The port may safely add tag membership for exact legacy `OreDictionary` keys that point to already registered items/blocks. That is separate from generated aspect calculation. A tag can preserve the old compatibility name without implying that generated aspects are available.

## Current Generated Crafting Scope

The first named validation slice covers already registered simple Thaumcraft content with normal crafting recipes:

| Output | Recipe source | Status |
|---|---|---|
| `plank_greatwood` | `log_greatwood` -> 4 | Generated and reload-validated. |
| `plank_silverwood` | `log_silverwood` -> 4 | Generated and reload-validated. |
| `slab_greatwood` | 3 Greatwood planks -> 6 | Generated and reload-validated. |
| `slab_silverwood` | 3 Silverwood planks -> 6 | Generated and reload-validated. |
| `stairs_greatwood` | 6 Greatwood planks -> 4 | Generated and reload-validated. |
| `stairs_silverwood` | 6 Silverwood planks -> 4 | Generated and reload-validated. |
| `amber_block` | 4 amber gems -> 1 | Generated and reload-validated. |
| `amber_brick` | 4 amber blocks -> 4 | Generated and reload-validated. |

This logic is recipe-type based, not UI based. A 2x2 inventory recipe and a 3x3 crafting table recipe both live under `RecipeType.CRAFTING`; the recipe's own dimensions decide where it can actually be crafted. Validation-only fixtures now cover shapeless recipe math and remaining-item subtraction without adding fake gameplay recipes.

The live generated cache now also scans current `minecraft:*` standard crafting outputs. The latest server validation rebuilt `476` generated entries and then enforced `1230/1230` assignable current vanilla item-id coverage. Final 1.12 dump values for legacy-equivalent flattened vanilla ids and currently registered Thaumcraft ids can be promoted into exact runtime parity layers when generated/manual/tag behavior would otherwise resolve differently from 1.12. Spawn eggs, firework star/rocket, infested blocks, and empty component-only potion carrier ids are intentionally excluded for legacy parity.

## Current Tag Expansion Scope

The following legacy OreDictionary keys are now represented as `thaumcraft:legacy_ore_dictionary/<snake_case_key>` tags for already registered content:

| Legacy key | Item tag | Block tag | Aspect lookup |
|---|---|---|---|
| `oreAmber` | Yes | Yes | Yes, mirrors `ConfigAspects` string key. |
| `oreCinnabar` | Yes | Yes | Yes, mirrors `ConfigAspects` string key. |
| `oreQuartz` | Yes | Yes | Yes, mirrors `ConfigAspects` string key. |
| `oreCrystalAir` | Yes | Yes | No, legacy aspects are direct crystal block assignments. |
| `oreCrystalEarth` | Yes | Yes | No, legacy aspects are direct crystal block assignments. |
| `oreCrystalWater` | Yes | Yes | No, legacy aspects are direct crystal block assignments. |
| `oreCrystalFire` | Yes | Yes | No, legacy aspects are direct crystal block assignments. |
| `oreCrystalOrder` | Yes | Yes | No, legacy aspects are direct crystal block assignments. |
| `oreCrystalEntropy` | Yes | Yes | No, legacy aspects are direct crystal block assignments. |
| `oreCrystalTaint` | Yes | Yes | No, legacy aspects are direct crystal block assignments. |
| `logWood` | Yes | Yes | Yes, generic fallback only; exact Greatwood/Silverwood ids still win. |
| `plankWood` | Yes | Yes | Yes for current Greatwood/Silverwood recipe outputs; broader wood policy still deferred. |
| `slabWood` | Yes | Yes | Yes for current Greatwood/Silverwood recipe outputs; broader wood policy still deferred. |
| `treeSapling` | Yes | Yes | Yes, generic fallback only; exact sapling ids still win. |
| `treeLeaves` | Yes | Yes | Yes, direct and generic legacy values currently match. |
| `gemAmber` | Yes | No | Yes, mirrors `ConfigAspects` string key. |
| `quicksilver` | Yes | No | Yes, mirrors `ConfigAspects` string key. |

Additional vanilla bridge:

| Legacy key/source | Modern target | Aspect lookup | Notes |
|---|---|---|---|
| `Blocks.COAL_ORE` | `minecraft:coal_ore` exact id | Yes | Direct block assignment from `ConfigAspects`. |
| wildcard `Items.COAL` | `minecraft:coal`, `minecraft:charcoal` exact ids | Yes | 1.12 metadata split into two modern ids. |
| `Blocks.BEDROCK` | `minecraft:bedrock` exact id | Yes | Direct block assignment. |
| `Items.BUCKET`, `Items.WATER_BUCKET`, `Items.LAVA_BUCKET`, `Items.MILK_BUCKET` | exact ids | Yes | Bucket seed is also used to validate remaining-item subtraction. |
| `ingotCopper` | `c:ingots/copper` | Yes | Legacy OreDictionary material key; covers 1.21 vanilla copper ingot through an explicit tag bridge. |
| `oreCopper` | `c:ores/copper` | Yes | Legacy OreDictionary material key; covers 1.21 vanilla copper ores through an explicit tag bridge. |
| vanilla ore/gem/ingot/dust keys | matching `c:` tags | Yes | Covers audited legacy keys such as `oreDiamond`, `gemDiamond`, `oreIron`, `ingotIron`, `dustRedstone`, and `dustGlowstone`. |
| raw iron/gold/copper | `c:raw_materials/*` | Yes | Modern raw materials use the corresponding legacy `ore*` aspect list because they are unprocessed ore drops. |

## Non-Goals

- No generated aspects for third-party modded namespaces yet.
- No crucible, infusion, or arcane crafting implementation.
- No global third-party modded `ConfigAspects` catalog connection.
- No scanning UI, research unlocks, essentia transport, aura, or gameplay effects.

## Implementation Checklist For The Real generateTags Port

1. Decide the 1.21.1 policy for third-party modded generated outputs before generating aspects for unrelated addon content.
2. Add more deterministic ingredient-selection fixtures for tag ingredients and multi-match recipes.
3. Keep vanilla item-id coverage validation at `0 missing` before exposing aspects through gameplay consumers.
4. Port crucible recipe data and catalyst matching.
5. Port infusion recipe data and component matching.
6. Port arcane crafting recipe data and `MAGIC` bonus calculation.
7. Add parity fixtures for known legacy custom recipe-derived outputs before exposing gameplay consumers.
