# Arcane Crafting Design

## Scope

This slice establishes the NeoForge 1.21.1 data and serializer boundary for
Thaumcraft 6 arcane recipes and the first server-authoritative Arcane
Workbench crafting path. It now covers the full regular legacy TC6 arcane
recipe id set and the first legacy equipment-discount bridge. It deliberately
does not implement recipe-book integration, the special void-jar stack-copy
recipe behavior, or final optional accessory/Curios slot integration. The
current slice does include the legacy-style player vis-discount service and
Workbench Charger 3 x 3 aura query/drain behavior because both directly affect
Arcane Workbench correctness.
It also includes the legacy missing-vis ghost output boundary: the screen may
show the recipe result as a blocked ghost when only vis is missing, but the
server result remains non-pickup and non-craftable.

## Authoritative legacy behavior

The original runtime contains `89` regular `thaumcraft:arcane` recipes:

- `80` regular shaped recipes;
- `9` regular shapeless recipes;

`ShapedArcaneVoidJar` has additional stack-copy semantics in legacy code. The
current data path contains the `jarvoid` recipe id and output, but the special
stack-copy behavior remains a focused follow-up.

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

The current runtime audit enforces the complete `89`-recipe legacy id set, all
loaded arcane recipes building Thaumonomicon page snapshots, and all arcane
recipe outputs resolving to real non-empty item ids. The table below lists the
earlier high-risk fixtures that remain individually asserted because they are
common dependency anchors:

| Recipe id | Shape | Research | Vis | Crystals |
|---|---|---|---:|---|
| `thaumcraft:thaumometer` | shaped | `FIRSTSTEPS@2` | 20 | `aer`, `terra`, `aqua`, `ignis`, `ordo`, `perditio`, one each |
| `thaumcraft:vis_resonator` | shapeless | `UNLOCKAUROMANCY@2` | 50 | `aer`, `aqua`, one each |
| `thaumcraft:workbenchcharger` | shaped | `WORKBENCHCHARGER` | 200 | `aer` x2, `ordo` x2 |
| `thaumcraft:goggles` | shaped | `UNLOCKARTIFICE` | 50 | none |
| `thaumcraft:mechanism_simple` | shaped | `BASEARTIFICE` | 10 | `ignis`, `aqua`, one each |
| `thaumcraft:mechanism_complex` | shaped | `BASEARTIFICE` | 50 | `ignis`, `aqua`, one each |
| `thaumcraft:wand_workbench` | shaped | `BASEAUROMANCY@2` | 100 | `terra`, `aqua`, one each |
| `thaumcraft:caster_basic` | shaped | `UNLOCKAUROMANCY@2` | 100 | `aer`, `terra`, `aqua`, `ignis`, `ordo`, `perditio`, one each |
| `thaumcraft:enchantedfabric` | shaped | `UNLOCKINFUSION` | 5 | none |
| `thaumcraft:mirrorglass` | shapeless | `BASEARTIFICE` | 50 | `aqua`, `ordo`, one each |
| `thaumcraft:filter` | shaped | `BASEALCHEMY` | 15 | `aqua` x1 |
| `thaumcraft:morphicresonator` | shaped | `BASEALCHEMY` | 50 | `aer` x1, `ignis` x1 |
| `thaumcraft:essentiasmelter` | shaped | `ESSENTIASMELTER@2` | 50 | `ignis` x1 |
| `thaumcraft:infusionmatrix` | shaped | `INFUSION@2` | 150 | `aer`, `terra`, `aqua`, `ignis`, `ordo`, `perditio`, one each |

Legacy TC6 uses the registry and recipe id `wand_workbench` for the visible
block named Focal Manipulator. It is not a separate wand workbench subsystem in
the TC4 sense.

Legacy OreDictionary ingredients are translated to current common tags:

- `ingotGold` -> `c:ingots/gold`;
- glass pane -> `minecraft:glass_pane`.
- `plateIron` -> `c:plates/iron`, backed by `thaumcraft:iron_plate`;
- `plateBrass` -> `c:plates/brass`, backed by `thaumcraft:brass_plate`;
- `plateThaumium` -> `c:plates/thaumium`, backed by `thaumcraft:thaumium_plate`;
- `gemQuartz` -> `c:gems/quartz`.
- `stickWood` -> `c:rods/wooden`, backed by `minecraft:stick`;
- `ingotIron` -> `c:ingots/iron`;
- `plankGreatwood` -> `thaumcraft:plank_greatwood`;
- `visResonator` -> `thaumcraft:vis_resonator`.
- `ingotBrass` -> `c:ingots/brass`, backed by `thaumcraft:brass_ingot`;
- `leather` -> `minecraft:leather`;
- `thaumometer` -> `thaumcraft:thaumometer`.
- `stoneArcane` -> `thaumcraft:stone_arcane`;
- `slabArcaneStone` -> `thaumcraft:slab_arcane_stone`;
- `tableStone` -> `thaumcraft:table_stone`.
- `string` -> `minecraft:string`;
- wildcard wool -> `minecraft:wool`;
- `quicksilver` -> `thaumcraft:quicksilver`;
- `paneGlass` -> `minecraft:glass_pane`.
- `Blocks.PISTON` -> `minecraft:piston`;
- `ItemsTC.mechanismSimple` -> `thaumcraft:mechanism_simple`.
- legacy cobblestone ingredient -> `c:cobblestones`;
- `nitor` -> `thaumcraft:legacy_ore_dictionary/nitor`, backed by all
  sixteen currently registered nitor block-item ids.
- `Items.GOLD_INGOT` -> `minecraft:gold_ingot`;
- `BlocksTC.plankSilverwood` -> `thaumcraft:plank_silverwood`;
- `ItemsTC.nuggets` metadata `10` -> `thaumcraft:rare_earth`;
- `ItemsTC.filter` -> `thaumcraft:filter`;
- `ItemsTC.morphicResonator` -> `thaumcraft:morphic_resonator`.

The `filter`, `morphicresonator`, `essentiasmelter`, and `infusionmatrix`
fixtures are exact recipe and Thaumonomicon-page fixtures. Essentia Filter and
Morphic Resonator behavior, `smelter_basic` machine behavior, Infusion Matrix
multiblock behavior, and final nitor gameplay/FX behavior remain separate
subsystem work.

The old `research_bridge/thaumometer`, `research_bridge/vis_resonator`, and
`research_bridge/wand_workbench`, `research_bridge/caster_basic`, and
`research_bridge/mirrored_glass` vanilla recipes are removed once the real
arcane recipes exist. Workbench Charger and Enchanted Fabric were imported
directly as real arcane recipes and did not use vanilla bridges. Keeping
obsolete bridges would create incorrect vanilla crafting paths and distort
generated-aspect recipe audits.

## Recipe-derived aspect boundary

Current `TCArcaneRecipe` outputs feed the reload-owned generated aspect cache
through the same legacy ingredient formula used by normal crafting:

1. take the first matching stack from each ingredient;
2. subtract remaining items;
3. multiply by `0.75 / output count`;
4. floor values and drop non-positive aspects;
5. add `praecantatio` from `sqrt(1 + vis / 2) / output count`.

Exact/tag/manual/runtime-parity assignments still win before generated values.
All currently loaded regular arcane recipes feed this generated cache after
server datapack reload. Crucible and infusion recipe-derived aspect generation
remain blocked until those serializers and machine models own their exact
ingredient/remaining-item semantics.

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
   If only vis is missing, the menu exposes the arcane output as a non-pickup
   ghost stack for the client screen, matching the old greyed-output behavior.
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

- `arcane_workbench` and `wand_workbench`/Focal Manipulator staying distinct block identities;
- `arcane_workbench_charger` surviving above Arcane Workbench and Focal Manipulator blocks;
- empty workbench resolution;
- fixed primal crystal slot aspect validation in legacy order;
- `canSpendVis` simulation not draining aura;
- no-charger current-chunk aura query;
- Workbench Charger 3 x 3 chunk aura query and drain behavior;
- `vis_resonator` missing-research fallback to vanilla 3 x 3 crafting;
- missing crystals, wrong crystal aspect, and missing vis blocking fallback;
- missing-vis ghost output being visible but not pickup/craftable;
- successful `vis_resonator` resolution with research, crystals, and vis;
- output take consuming matrix ingredients, required crystals, and current-chunk
  vis;
- 5% Goggles of Revealing discount reducing the resolved cost and drained aura;
- legacy Thaumaturge robe discounts: boots `2%`, chest `3%`, legs `3%`;
- legacy Void robe discounts: helm/chest/legs `5%` each;
- external discount stack provider contribution, used as the modern bridge for
  future Baubles/Curios-like equipment slots without a hard optional dependency;
- combined discount cap at the public API limit of `50%`;
- combined equipment/provider discount applying to the same integer-truncated
  vis-cost formula used by legacy code;
- vanilla 3 x 3 fallback crafting for the current exact `iron_plate` fixture.
- server-owned Arcane Workbench menu feedback for resolved cost/aura, discounted
  cost, missing vis, missing crystals, and vanilla fallback staying costless.

Current runtime result: `28/28` Arcane Workbench behavior checks pass. The
separate arcane recipe audit currently passes `109/109` checks with `89` loaded
arcane recipes and an exact legacy 1.12 id-set match. The research recipe/page
catalog is still the owner of broader page availability; this does not make
deferred recipe-page renderer families part of the arcane workbench slice.

Run from `05_neoforge_port`:

```powershell
.\gradlew.bat runServer --no-daemon -PtcArcaneRecipeAudit=true "-PtcArcaneRecipeAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\arcane_crafting\thaumcraft_1_21_arcane_recipe_audit.md"
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

- Final item/equipment behavior for wearing/rendering robes, goggles variants,
  Baubles/Curios-style slot adapter wiring, and vis-exhaustion penalties.
  Current discount support covers vanilla armor slots and a provider bridge.
- Exact GUI polish: final visual tuning beyond the current server-owned
  aura/cost labels, missing-vis ghost output and crystal glow overlays.
- Special `ShapedArcaneVoidJar` stack-copy behavior.
