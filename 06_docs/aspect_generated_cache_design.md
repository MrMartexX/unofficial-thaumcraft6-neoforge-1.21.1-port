# Thaumcraft 6 Generated Aspect Cache Design

Last reviewed branch: `main`
Target module: `05_neoforge_port`

## Purpose

This document defines the stable stack identity and generated-aspect cache boundary needed before porting the legacy `generateTags` algorithm.

Guide rule applied: old `ItemStack` NBT and metadata cannot be copied directly into NeoForge 1.21.1. Stack-specific data must be reviewed through Data Components, and recipe-derived behavior must use a reload-safe cache instead of scanning `RecipeManager` ad hoc.

## Legacy Problem

Legacy `ThaumcraftCraftingManager.generateTags` normalizes an `ItemStack`, recursively derives aspects from crucible, infusion, and crafting recipes, and caches the result by registering it back into `CommonInternals.objectTags`.

The old cache key was tied to 1.12 `ItemStack` ids, metadata, wildcard damage, stripped NBT, and serialized NBT history. That is not stable enough for 1.21.1 because:

- metadata variants are flattened into ids, data components, or explicit variant mappings;
- count should not affect generated aspect identity;
- Data Components replaced much of the old ad hoc stack NBT behavior;
- datapack/resource reload can change recipes, tags, and assignment data;
- generated output must not survive a reload of the inputs that created it.

## Current Implementation Boundary

| Piece | Class | Current behavior |
|---|---|---|
| Stack identity | `TCAspectStackKey` | Uses item `ResourceLocation` plus `ItemStack#getComponentsPatch()`. Stack count is intentionally ignored. Empty stacks are rejected. |
| Generated cache | `TCGeneratedAspectCache` | In-memory cache keyed by `TCAspectStackKey`. It is rebuilt after server data/tag reload for the currently supported vanilla crafting slice. |
| Crafting generator | `TCGeneratedAspectRecipeGenerator` | Scans loaded `RecipeType.CRAFTING` recipes after tags are rebound, derives aspects from first matching ingredient stacks, and writes generated entries into the cache. |
| Lookup priority | `TCAspectAssignments#getObjectAspects` | Resolves `exact item id > item tag > generated cache`. |
| Public bridge | `AspectHelper#generateTags` | Returns cached generated entries only. It does not scan recipes on lookup. |
| Reload invalidation | `TCAspectAssignments#bootstrap` and `reload` | Clears generated cache whenever bundled data or datapack assignment data is loaded; server tag reload rebuilds it from recipes. |
| Validation | `TCAspectParityValidator`, `TCAspectReloadValidator` | Validates count-insensitive stack keys, generated crafting results, generated fallback behavior, and exact/tag priority over generated cache. |

## Lookup Priority

The modern lookup order is:

1. Exact item id assignment from `data/*/aspect_assignments/*.json`.
2. Item tag assignment from `data/*/aspect_assignments/*.json`.
3. Generated aspect cache.
4. `null`.

This mirrors the legacy intent that explicit object tags beat generated recipe-derived values. The generated cache must never overwrite exact/tag assignment data.

## Current Crafting Scope

The implemented slice covers standard Minecraft `RecipeType.CRAFTING` only. It is not tied to a crafting UI:

- inventory 2x2 recipes and crafting-table 3x3 recipes share the same recipe type;
- each recipe decides its own `canCraftInDimensions` behavior;
- generated aspects are rebuilt from loaded recipe data, not when the player takes an output item.

The current generator is intentionally restricted to the `minecraft` and `thaumcraft` namespaces. Exact/tag/manual vanilla assignments can be used as ingredients, and standard crafting outputs in those namespaces can receive generated aspects. Third-party modded namespaces are still excluded until an addon/modded-content policy exists.

Representative recipe-derived entries:

| Output | Source recipe | Generated aspects |
|---|---|---|
| `thaumcraft:plank_greatwood` | `thaumcraft:log_greatwood` -> 4 planks | `herba 3`, `victus 1` |
| `thaumcraft:plank_silverwood` | `thaumcraft:log_silverwood` -> 4 planks | `herba 3`, `auram 1` |
| `thaumcraft:slab_greatwood` | 3 Greatwood planks -> 6 slabs | `herba 1` |
| `thaumcraft:slab_silverwood` | 3 Silverwood planks -> 6 slabs | `herba 1` |
| `thaumcraft:stairs_greatwood` | 6 Greatwood planks -> 4 stairs | `herba 3`, `victus 1` |
| `thaumcraft:stairs_silverwood` | 6 Silverwood planks -> 4 stairs | `herba 3`, `auram 1` |
| `thaumcraft:amber_block` | 4 amber gems -> 1 block | `vinculum 30`, `vitreus 30` |
| `thaumcraft:amber_brick` | 4 amber blocks -> 4 bricks | `vinculum 22`, `vitreus 22` |

The formula matches the legacy crafting path: sum ingredient aspects, subtract remaining-item aspects, multiply each aspect by `0.75 / outputCount`, floor to integer, lift values in `(0.75, 1.0)` to `1`, remove non-positive aspects, then choose the lowest positive aspect total among matching output recipes. Reload validation now covers both shapeless recipes and remaining-item subtraction with validation-only fixtures.

Current server reload validation rebuilt `475` generated crafting assignments while keeping exact/tag/manual/runtime-parity assignments authoritative. The count dropped because final 1.12 dump values for legacy-equivalent flattened vanilla ids and currently registered Thaumcraft parity overrides now live in exact runtime parity data instead of being left to generated fallback. Assignable current vanilla item-id coverage is enforced separately by `TCAspectReloadValidator`; spawn eggs, firework star/rocket, infested blocks, and empty component-only potion carrier ids are excluded for legacy parity.

## Stack Key Rules

| Rule | Decision |
|---|---|
| Item identity | Use registry id from `BuiltInRegistries.ITEM`. |
| Stack count | Ignored. Legacy generation copied stacks and set count to `1`; generated identity should not differ between one item and a stack of items. |
| Components | Use `ItemStack#getComponentsPatch()` as the current stable component delta. |
| Empty stacks | Rejected for keys and return `null` through public lookup. |
| Transient components | Not specially filtered yet. The cache clears on reload and is not persisted. If transient components become a real issue, add an explicit allow/deny list before gameplay consumers use generated aspects. |
| Persistence | None. Generated cache is runtime-only. It is rebuilt after reload by the future generator. |

## Invalidated Inputs

Generated aspects must be cleared whenever any of these change:

- aspect assignment data reloads;
- item/block/tag membership reloads;
- vanilla, arcane, crucible, or infusion recipes reload;
- future generated-aspect algorithm version changes.

The current implementation clears on aspect assignment reload and rebuilds after server-side tag reload, when loaded recipes and tag-backed ingredients are available.

## Non-Goals

- No lookup-time recipe scanning.
- No generated aspects for third-party modded namespaces yet.
- No crucible, infusion, or arcane recipe serializers.
- No public addon API for injecting generated cache entries.
- No persistence of generated cache to disk.
- No scanning/research/essentia/aura/gameplay consumers.

## Next Implementation Checklist

1. Decide the generated-aspect policy for third-party modded recipe outputs.
2. Add more ingredient-selection parity fixtures for tag ingredients and multi-match recipes.
3. Add policy for recipe outputs outside the `minecraft` and `thaumcraft` namespaces before enabling them.
4. Port crucible recipe data and catalyst matching.
5. Port infusion recipe data and component matching.
6. Port arcane crafting recipe data and the legacy `MAGIC` bonus calculation.
7. Add deterministic ingredient selection tests matching the old `Ingredient#getMatchingStacks()[0]` behavior.
