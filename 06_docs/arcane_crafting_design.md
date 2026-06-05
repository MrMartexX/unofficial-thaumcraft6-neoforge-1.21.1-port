# Arcane Crafting Design

## Scope

This slice establishes the NeoForge 1.21.1 data and serializer boundary for
Thaumcraft 6 arcane recipes and the first server-authoritative Arcane
Workbench crafting path. It deliberately does not implement broad recipe
import, full caster-equipment/Curios integration, recipe-book integration, or
the special void-jar recipe behavior. The current slice does include the first
legacy-style player vis-discount service and Workbench Charger 3 x 3 aura
query/drain behavior because both directly affect Arcane Workbench correctness.

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
- Keep workbench validation and consumption in a server-owned service. Recipe
  matching validates the 3 x 3 matrix; the workbench service separately gates
  research, aura vis and crystal slots.
- Preserve crystal aspect identity through the existing modern aspect stack
  model. Do not infer crystal costs from ingredients or output aspects.
- Keep vanilla crafting fallback inside the Arcane Workbench, but only against
  the first nine matrix slots, matching the legacy container.

## Exact fixtures

Current exact arcane recipe fixtures:

| Recipe id | Shape | Research | Vis | Crystals |
|---|---|---|---:|---|
| `thaumcraft:thaumometer` | shaped | `FIRSTSTEPS@2` | 20 | `aer`, `terra`, `aqua`, `ignis`, `ordo`, `perditio`, one each |
| `thaumcraft:vis_resonator` | shapeless | `UNLOCKAUROMANCY@2` | 50 | `aer`, `aqua`, one each |
| `thaumcraft:workbenchcharger` | shaped | `WORKBENCHCHARGER` | 200 | `aer` x2, `ordo` x2 |

Legacy OreDictionary ingredients are translated to current common tags:

- `ingotGold` -> `c:ingots/gold`;
- glass pane -> `minecraft:glass_pane`.
- `plateIron` -> `c:plates/iron`, backed by `thaumcraft:iron_plate`;
- `gemQuartz` -> `c:gems/quartz`.
- `ingotIron` -> `c:ingots/iron`;
- `plankGreatwood` -> `thaumcraft:plank_greatwood`;
- `visResonator` -> `thaumcraft:vis_resonator`.

The old `research_bridge/thaumometer` and `research_bridge/vis_resonator`
vanilla recipes are removed once the real arcane recipes exist. Workbench
Charger was imported directly as a real arcane recipe and does not use a vanilla
bridge. Keeping obsolete bridges would create incorrect vanilla crafting paths
and distort generated-aspect recipe audits.

## Arcane Workbench server model

Implemented modern classes:

- `TCArcaneWorkbenchBlock`
- `TCArcaneWorkbenchChargerBlock`
- `TCArcaneWorkbenchBlockEntity`
- `TCArcaneWorkbenchMenu`
- `TCArcaneWorkbenchScreen`
- `TCArcaneWorkbenchCrafting`
- public marker `thaumcraft.api.crafting.IArcaneWorkbench`
- public discount marker `thaumcraft.api.items.IVisDiscountGear`
- `CasterManager`

The BlockEntity owns the legacy `5 x 3` inventory shape:

- slots `0..8`: 3 x 3 crafting matrix;
- slots `9..14`: fixed primal crystal slots in legacy order:
  `aer`, `ignis`, `aqua`, `terra`, `ordo`, `perditio`.

The menu keeps the legacy slot layout:

- result at `160,64`;
- crafting matrix at `40 + column * 24`, `40 + row * 24`;
- crystal slots at legacy `ContainerArcaneWorkbench.xx/yy`;
- player inventory at `16,151`;
- hotbar at `16,209`.

The menu also owns the current GUI feedback data. It syncs the resolved recipe
kind, discounted vis cost, base vis cost, available aura vis, requirement flags,
and required primal-crystal slot mask through menu data slots. The client screen
only renders that server-owned state: legacy-positioned available/cost text,
discount text when base cost is reduced, and the rotating primal crystal glow
overlay around required crystal slots.

Server resolution order:

1. Build a 3 x 3 `CraftingInput` from slots `0..8`.
2. Find a matching `thaumcraft:arcane` recipe first.
3. If an arcane recipe matches and vis/crystals are missing, output is blocked.
4. If vis/crystals are present and research is known, output the arcane result.
5. If vis/crystals are present but research is not known, fall back to vanilla
   3 x 3 crafting only if the matrix also matches a vanilla recipe.
6. If no arcane recipe matches, try vanilla 3 x 3 crafting.

On output take, the server re-resolves the recipe, drains discounted aura vis
for arcane recipes, consumes one item from each occupied matrix slot, applies
vanilla remaining items for vanilla fallback recipes, consumes required primal
crystals, and marks matching `required_craft` research markers.

Current vis behavior uses the existing port `AuraHandler.getVis/drainVis`.
Without a charger it queries and drains the workbench's current chunk. With
`arcane_workbench_charger` directly above the workbench it sums the center chunk
plus the surrounding eight chunks and drains from those nine chunks using the
legacy loop shape. This is intentionally scoped to the Arcane Workbench and
does not yet imply final Focal Manipulator behavior.

## Server behavior audit

`TCArcaneWorkbenchAudit` is a dedicated server-runtime harness for the first
Arcane Workbench slice. It uses a real `ServerLevel`, fake server player, loaded
recipe manager, player knowledge storage, aspect crystal stacks, and the current
aura saved-data service. The audit writes:

`07_Test_Instance_and_Comparisons/arcane_crafting/thaumcraft_1_21_arcane_workbench_audit.md`

Current checks cover:

- `arcane_workbench` and `wand_workbench` staying distinct block identities;
- `arcane_workbench_charger` surviving above arcane/wand workbenches;
- empty workbench resolution;
- fixed primal crystal slot aspect validation in legacy order;
- `canSpendVis` simulation not draining aura;
- no-charger current-chunk aura query;
- Workbench Charger 3 x 3 chunk aura query and drain behavior;
- `vis_resonator` missing-research fallback to vanilla 3 x 3 crafting;
- missing crystals, wrong crystal aspect, and missing vis blocking fallback;
- successful `vis_resonator` resolution with research, crystals, and vis;
- output take consuming matrix ingredients, required crystals, and current-chunk
  vis;
- 5% Goggles of Revealing discount reducing the resolved cost and drained aura;
- vanilla 3 x 3 fallback crafting for the current exact `iron_plate` fixture.
- server-owned Arcane Workbench menu feedback for resolved cost/aura, discounted
  cost, missing vis, missing crystals, and vanilla fallback staying costless.

Run from `05_neoforge_port`:

```powershell
.\gradlew.bat runServer --no-daemon -PtcArcaneWorkbenchAudit=true "-PtcArcaneWorkbenchAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\arcane_crafting\thaumcraft_1_21_arcane_workbench_audit.md"
```

## First page snapshot

The Thaumonomicon page renderer now has a server-authored
`TCArcaneRecipePageView` snapshot for ready arcane catalog entries. It contains
the recipe id, shaped/shapeless flag, compact grid dimensions, resolved
ingredient variants, result stack, research key, base vis cost, and ordered
crystal display stacks.

This snapshot is display-only and remains server-authored. It now reflects the
same recipe data consumed by the first Arcane Workbench server crafting path.

## Blocked behavior

- Full equipment integration for robes, goggles variants, Baubles/Curios-style
  slots, and vis-exhaustion penalties. Current discount support is limited to
  the first vanilla armor-slot marker service and Goggles fixture.
- Exact GUI polish: blocked-output ghost rendering and final visual tuning
  beyond the current server-owned aura/cost labels and crystal glow overlays.
- Special `ShapedArcaneVoidJar` stack-copy behavior.
- The remaining `70` legacy recipes until their exact outputs and ingredients
  exist and can be mapped without placeholders.
