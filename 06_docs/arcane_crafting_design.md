# Arcane Crafting Design

## Scope

This slice establishes the NeoForge 1.21.1 data and serializer boundary for
Thaumcraft 6 arcane recipes. It deliberately does not implement the Arcane
Workbench menu, crystal or aura consumption, vis discounts, recipe output
taking, charger behavior, or recipe-book integration.

## Authoritative legacy behavior

The original runtime contains `73` arcane recipe catalog entries:

- `63` regular shaped recipes;
- `9` regular shapeless recipes;
- `1` special shaped void-jar recipe with additional stack behavior.

The public `IArcaneRecipe` contract exposes:

- a research requirement string;
- a base vis cost;
- an ordered `AspectList` of crystal costs.

`ShapedArcaneRecipe` and `ShapelessArcaneRecipe` only match an
`IArcaneWorkbench`. The workbench owns a `5 x 3` inventory: the first nine
slots form the crafting matrix and six dedicated slots hold primal crystals.
The legacy workbench then separately validates player research, discounted vis
availability, crystal counts, and vanilla crafting fallback.

On output take, the server:

1. resolves the matching arcane recipe;
2. applies the player's vis discount;
3. drains aura;
4. consumes normal ingredients and remaining items;
5. consumes the required crystal counts.

## Modern target

- Preserve `thaumcraft.api.crafting.IArcaneRecipe` as the public domain
  contract.
- Register a dedicated `thaumcraft:arcane` `RecipeType`; arcane recipes must
  never be craftable in a vanilla crafting grid.
- Register separate `thaumcraft:arcane_shaped` and
  `thaumcraft:arcane_shapeless` serializers.
- Store research, base vis, ordered crystal costs, ingredients, and result in
  datapack recipe JSON.
- Keep workbench validation and atomic consumption in a later server-owned
  service. Recipe matching in this slice only validates the 3 x 3 matrix.
- Preserve crystal aspect identity through the existing modern aspect stack
  model. Do not infer crystal costs from ingredients or output aspects.

## First exact fixture

The first recipe is a direct legacy transcription whose output and ingredients
already exist in the port:

| Recipe id | Shape | Research | Vis | Crystals |
|---|---|---|---:|---|
| `thaumcraft:thaumometer` | shaped | `FIRSTSTEPS@2` | 20 | `aer`, `terra`, `aqua`, `ignis`, `ordo`, `perditio`, one each |

Legacy OreDictionary ingredients are translated to current common tags:

- `ingotGold` -> `c:ingots/gold`;
- glass pane -> `minecraft:glass_pane`.

`thaumcraft:vis_resonator` is intentionally not added yet. Legacy
`ConfigRecipes` defines it as shapeless `plateIron` + `gemQuartz`, and the
current port does not have a precise modern iron-plate bridge item/tag. Mapping
`plateIron` to `c:ingots/iron` would make the first custom recipe page look
ready while silently changing the legacy recipe.

## First page snapshot

The Thaumonomicon page renderer now has a server-authored
`TCArcaneRecipePageView` snapshot for ready arcane catalog entries. It contains
the recipe id, shaped/shapeless flag, compact grid dimensions, resolved
ingredient variants, result stack, research key, base vis cost, and ordered
crystal display stacks.

This snapshot is display-only. It does not imply that the Arcane Workbench can
craft the recipe yet.

## Blocked behavior

- Arcane Workbench BlockEntity, menu, slots, and screen.
- Research-aware recipe matching.
- Player vis-discount calculation.
- Aura query/drain and Workbench Charger 3 x 3 chunk behavior.
- Atomic ingredient, remaining-item, crystal, and aura consumption.
- Vanilla recipe fallback inside the Arcane Workbench.
- Special `ShapedArcaneVoidJar` stack-copy behavior.
- The remaining `72` legacy recipes until their exact outputs and ingredients
  exist and can be mapped without placeholders.
