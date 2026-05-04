# Gate 1 Items Plan

Project: `Thaumcraft_6_port_to_1.21.1`

Target module: `05_neoforge_port`

Target version: Minecraft `1.21.1`, NeoForge `21.1.228`, Java `21`

Legacy reference: `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master`

Visual reference: `07_Test_Instance_and_Comparisons/02_Thaumcraft 1.12.2 Inventory Screenshots`

Related docs:

- `06_docs/subsystem_inventory.md`
- `06_docs/porting_order.md`
- `06_docs/migration_matrix.md`
- `06_docs/creative_tab_order_reference.md`
- `06_docs/NeoForge_legacy_migration_guide_expanded_v3.docx`

## Purpose

Gate 1 is the first coding gate after the clean NeoForge bootstrap.

The goal is not to port Thaumcraft gameplay yet. The goal is to establish a safe, repeatable item-porting workflow:

1. register a small set of Thaumcraft items in NeoForge;
2. make them build successfully;
3. make them appear in the Thaumcraft creative tab;
4. preserve the visible 1.12.2 creative tab order as closely as possible;
5. add language entries, item models, and textures;
6. avoid research, aura, aspects, GUI, networking, worldgen, Baubles/Curios behavior, focus behavior, and complex item logic.

Gate 1 is intentionally conservative. It should create a stable foundation for later content gates.

## Hard Rules

1. Do not attempt to port all Thaumcraft items at once.
2. Do not implement item gameplay behavior unless the item is truly simple.
3. Do not port research, aura, aspects, spell casting, GUI, networking, or Baubles/Curios behavior in Gate 1.
4. Do not alphabetize items.
5. Do not use NeoForge registry declaration order as the creative tab order.
6. Preserve the visible 1.12.2 Thaumcraft creative tab order.
7. Keep implemented entries in the same relative order as legacy Thaumcraft.
8. Gaps are allowed during partial porting.
9. Every small batch must compile before adding the next batch.
10. If an item has complex behavior, register it later or create only a clearly marked non-functional placeholder during a controlled placeholder phase.

## Current Baseline

The target NeoForge project currently contains an empty bootstrap:

- `thaumcraft.Thaumcraft`
- `thaumcraft.common.registry.TCItems`
- `thaumcraft.common.registry.TCBlocks`
- `thaumcraft.common.registry.TCCreativeTabs`
- `thaumcraft.common.config.TCConfig`
- `META-INF/neoforge.mods.toml` generated from template

The local build already passed before Gate 1 work began. Treat this as the baseline to preserve.

## Gate 1 Success Criteria

Gate 1 is complete when:

1. The Thaumcraft creative tab exists.
2. A first controlled batch of simple items is registered.
3. Registered items have English language entries.
4. Registered items have item model JSON files.
5. Registered items have textures copied or temporarily mapped from legacy assets.
6. Implemented items appear in the Thaumcraft creative tab.
7. Implemented items preserve relative order from Thaumcraft 6 1.12.2.
8. `gradlew build` passes locally.
9. `runClient` opens without missing-model crashes.
10. No complex Thaumcraft systems are introduced accidentally.

Recommended local commands after each batch:

```powershell
.\gradlew.bat build --no-daemon
.\gradlew.bat runClient --no-daemon
```

## Why Gate 1 starts with items

Items are the safest first content type because they test the basic pipeline without requiring BlockEntity, menus, packets, worldgen, or rendering systems.

Gate 1 validates:

- registry pattern;
- creative tab registration;
- creative tab display order;
- resource paths;
- language file format;
- basic models;
- texture migration;
- small-batch workflow.

This is especially important because Thaumcraft 6 has many old metadata/subtype items. Those cannot be copied directly into 1.21.1 without a deliberate mapping.

## Legacy item order source

The legacy item registration order is in:

```text
02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/config/ConfigItems.java
```

The relevant legacy method is:

```java
public static void initItems(IForgeRegistry<Item> iForgeRegistry)
```

Many legacy items expose multiple creative variants through:

```java
ItemTCBase#getSubItems(...)
```

This means the final visible order is not only one item per registry object. It is:

```text
legacy registration order + each item's subtype/variant order
```

The screenshot folder is the visual truth for final review.

## NeoForge implementation shape

### Registry class

Use `TCItems` only for item registration.

Expected pattern:

```java
public final class TCItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Thaumcraft.MODID);

    public static final Supplier<Item> AMBER = ITEMS.register("amber", () -> new Item(new Item.Properties()));

    private TCItems() {}
}
```

Do not put creative ordering logic in `TCItems`.

### Creative tab class

Use `TCCreativeTabs` only for registering the Thaumcraft creative tab.

The tab should call a separate order class:

```java
.displayItems((parameters, output) -> TCCreativeTabOrder.addEntries(output))
```

### Creative order class

Create:

```text
05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java
```

Expected responsibility:

```java
public final class TCCreativeTabOrder {
    private TCCreativeTabOrder() {}

    public static void addEntries(CreativeModeTab.Output output) {
        // Add only implemented entries, in legacy visual order.
    }
}
```

### Resource paths

Modern resource paths should use singular folder names used by current Minecraft resource conventions:

```text
assets/thaumcraft/textures/item/<item_id>.png
assets/thaumcraft/models/item/<item_id>.json
assets/thaumcraft/lang/en_us.json
```

Legacy textures are mostly under:

```text
02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/resources/assets/thaumcraft/textures/items/
```

Copy to modern path:

```text
textures/items/<legacy>.png -> textures/item/<modern>.png
```

Do not rename texture files casually. If the item registry id is changed from the legacy texture name, document the mapping.

## Registry ID strategy

Thaumcraft 6 1.12.2 often used one registry item plus metadata variants. In NeoForge 1.21.1, Gate 1 should prefer explicit separate items for simple visible material variants.

Examples:

| Legacy registry item | Legacy variant | Recommended NeoForge item id | Reason |
|---|---|---|---|
| `ingot` | `thaumium` | `ingot_thaumium` | Matches existing texture name and lang meaning |
| `ingot` | `void` | `ingot_void` | Avoids old metadata logic |
| `ingot` | `brass` | `ingot_brass` | Clear simple material item |
| `nugget` | `quicksilver` | `nugget_quicksilver` | Matches texture name |
| `cluster` | `cinnabar` | `cluster_cinnabar` | Matches texture name |
| `plate` | `thaumium` | `plate_thaumium` | Matches texture name |

This should be decided early because registry IDs become world-save-visible once released.

If a later compatibility review decides to use `thaumium_ingot` style IDs instead, do that before public builds. For Gate 1, use asset-preserving IDs unless there is a strong reason not to.

## Gate 1 phase structure

Gate 1 should be split into small phases.

Do not start the next phase until the previous phase builds.

## Phase 1A: Creative tab scaffold only

Goal: make the Thaumcraft creative tab exist and use explicit ordering.

Files likely touched:

| File | Change |
|---|---|
| `TCCreativeTabs.java` | Register one Thaumcraft creative tab with builder |
| `TCCreativeTabOrder.java` | New explicit creative order class |
| `en_us.json` | Add creative tab title key |

Suggested creative tab lang key:

```json
{
  "itemGroup.thaumcraft": "Thaumcraft"
}
```

Temporary icon rule:

- Final icon should be checked against Thaumcraft 6 1.12.2.
- If `thaumonomicon` is not implemented yet, use `amber` after Phase 1B or another first implemented item as a temporary icon.
- Mark temporary icon with a TODO comment.

Acceptance criteria:

- Project builds.
- Creative tab registration compiles.
- No items need to be visible yet.

## Phase 1B: First simple scalar resource items

Goal: add the first group of one-texture, mostly no-behavior items.

These are safe because they can initially be plain `Item` instances with legacy texture/name.

Recommended first batch:

| Order note | Legacy field | Recommended id | Legacy texture | Legacy lang key | Gate 1 behavior |
|---:|---|---|---|---|---|
| after early complex curios | `ItemsTC.amber` | `amber` | `amber.png` | `item.amber.name` | Plain item |
| after amber | `ItemsTC.quicksilver` | `quicksilver` | `quicksilver.png` | `item.quicksilver.name` | Plain item |
| after cluster variants | `ItemsTC.fabric` | `fabric` | `fabric.png` | `item.fabric.name` | Plain item |
| after fabric | `ItemsTC.visResonator` | `vis_resonator` | `vis_resonator.png` | `item.vis_resonator.name` | Plain item |
| after vis resonator | `ItemsTC.tallow` | `tallow` | `tallow.png` | `item.tallow.name` | Plain item |
| after tallow | `ItemsTC.mechanismSimple` | `mechanism_simple` | `mechanism_simple.png` | `item.mechanism_simple.name` | Plain item |
| after simple mechanism | `ItemsTC.mechanismComplex` | `mechanism_complex` | `mechanism_complex.png` | `item.mechanism_complex.name` | Plain item |
| after plate variants | `ItemsTC.filter` | `filter` | `filter.png` | `item.filter.name` | Plain item |
| after filter | `ItemsTC.morphicResonator` | `morphic_resonator` | `morphic_resonator.png` | `item.morphic_resonator.name` | Plain item |
| after salis mundus | `ItemsTC.mirroredGlass` | `mirrored_glass` | `mirrored_glass.png` | `item.mirrored_glass.name` | Plain item |
| after mirrored glass | `ItemsTC.voidSeed` | `void_seed` | `void_seed.png` | `item.void_seed.name` | Plain item |
| later consumable group | `ItemsTC.jarBrace` | `jar_brace` | check legacy item texture | `item.jar_brace.name` | Plain item |

Important: this table is not the full visible order. It lists safe simple items from the legacy item sequence. The creative tab order class must place them in the same relative positions, with gaps reserved for unimplemented complex entries.

Do not add `salis_mundus` in the first tiny batch unless it is explicitly marked as behavior-deferred. In Thaumcraft 6 it has block interaction logic through `IDustTrigger`, so it is not just a cosmetic material.

Acceptance criteria:

- Items register.
- Items have models.
- Items have textures.
- Items have translated names.
- Items appear in the Thaumcraft tab in the relative order above.
- Build passes.

## Phase 1C: Simple material variant items

Goal: replace legacy metadata material items with explicit NeoForge items.

Legacy source examples:

```java
new ItemTCBase("ingot", "thaumium", "void", "brass")
new ItemTCBase("nugget", "iron", "copper", "tin", "silver", "lead", "quicksilver", "thaumium", "void", "brass", "quartz", "rareearth")
new ItemTCBase("cluster", "iron", "gold", "copper", "tin", "silver", "lead", "cinnabar", "quartz")
new ItemTCBase("plate", "brass", "iron", "thaumium", "void")
```

Recommended NeoForge approach for Gate 1:

- register each visible material variant as a separate item;
- keep creative display order matching the variant order in legacy constructors;
- copy matching textures;
- add or migrate language entries;
- add tags later, not in the first coding slice unless trivial.

### Ingot variants

| Legacy order | NeoForge id | Texture | Name source |
|---:|---|---|---|
| 1 | `ingot_thaumium` | `ingot_thaumium.png` | Thaumium Ingot |
| 2 | `ingot_void` | `ingot_void.png` | Void Metal Ingot |
| 3 | `ingot_brass` | `ingot_brass.png` | Alchemical Brass Ingot |

### Nugget variants

| Legacy order | NeoForge id | Texture | Name source |
|---:|---|---|---|
| 1 | `nugget_iron` | `nugget_iron.png` | Iron Nugget |
| 2 | `nugget_copper` | `nugget_copper.png` | Copper Nugget |
| 3 | `nugget_tin` | `nugget_tin.png` | Tin Nugget |
| 4 | `nugget_silver` | `nugget_silver.png` | Silver Nugget |
| 5 | `nugget_lead` | `nugget_lead.png` | Lead Nugget |
| 6 | `nugget_quicksilver` | `nugget_quicksilver.png` | Quicksilver Drop |
| 7 | `nugget_thaumium` | `nugget_thaumium.png` | Thaumium Nugget |
| 8 | `nugget_void` | `nugget_void.png` | Void Metal Nugget |
| 9 | `nugget_brass` | `nugget_brass.png` | Alchemical Brass Nugget |
| 10 | `nugget_quartz` | `nugget_quartz.png` | Quartz Sliver |
| 11 | `nugget_rareearth` | `nugget_rareearth.png` | Rare Earths |

### Cluster variants

| Legacy order | NeoForge id | Texture | Name source |
|---:|---|---|---|
| 1 | `cluster_iron` | `cluster_iron.png` | Native Iron Cluster |
| 2 | `cluster_gold` | `cluster_gold.png` | Native Gold Cluster |
| 3 | `cluster_copper` | `cluster_copper.png` | Native Copper Cluster |
| 4 | `cluster_tin` | `cluster_tin.png` | Native Tin Cluster |
| 5 | `cluster_silver` | `cluster_silver.png` | Native Silver Cluster |
| 6 | `cluster_lead` | `cluster_lead.png` | Native Lead Cluster |
| 7 | `cluster_cinnabar` | `cluster_cinnabar.png` | Native Cinnabar Cluster |
| 8 | `cluster_quartz` | `cluster_quartz.png` | Native Quartz Cluster |

### Plate variants

| Legacy order | NeoForge id | Texture | Name source |
|---:|---|---|---|
| 1 | `plate_brass` | `plate_brass.png` | Brass Plate |
| 2 | `plate_iron` | `plate_iron.png` | Iron Plate |
| 3 | `plate_thaumium` | `plate_thaumium.png` | Thaumium Plate |
| 4 | `plate_void` | `plate_void.png` | Void Metal Plate |

Acceptance criteria:

- Each material variant is a separate registered NeoForge item.
- Relative order follows the legacy subtype order.
- Model and lang entries are present.
- Build passes.

## Phase 1D: Visible-only simple special items

This phase is optional in Gate 1. It may be safer to postpone some entries until their systems exist.

Candidate items that can be registered as visible-only placeholders if needed:

| Legacy field | Recommended id | Risk | Gate 1 recommendation |
|---|---|---|---|
| `ItemsTC.alumentum` | `alumentum` | Explosive/use behavior later | Defer unless placeholder clearly marked |
| `ItemsTC.brain` | `brain` | Mostly simple consumable/resource | Possible after materials |
| `ItemsTC.tripleMeatTreat` | `triple_meat_treat` | Consumable behavior | Defer or visible-only |
| `ItemsTC.label` | `label` | Has blank/filled state | Defer until variant/data-component plan |
| `ItemsTC.phial` | `phial` | Empty/filled essentia variants | Defer until aspects/essentia data exists |
| `ItemsTC.bottleTaint` | `bottle_taint` | World/effect interaction | Defer |
| `ItemsTC.sanitySoap` | `sanity_soap` | Warp/sanity behavior | Defer |
| `ItemsTC.bathSalts` | `bath_salts` | Warp/sanity behavior | Defer |
| `ItemsTC.causalityCollapser` | `causality_collapser` | Rift/flux behavior | Defer |

Gate 1 should not hide the fact that these items are incomplete. If visible-only placeholders are used, add comments and status notes.

## Items explicitly not for Gate 1

These should not be implemented as functional items in Gate 1:

| Item/group | Reason |
|---|---|
| `thaumonomicon` | Needs research/progression/book GUI behavior |
| `curio` variants | Needs research/knowledge use behavior |
| `lootBag` variants | Needs loot table behavior |
| `primordialPearl` variants | Needs damage/state behavior |
| `pechWand` | Special behavior |
| `celestialNotes` | Complex subtype/text behavior |
| `salis_mundus` | Block transformation triggers and particles |
| `crystalEssence` | Requires aspects/essentia data |
| `chunks` | Food variants and behavior |
| tools | Need modern tool tiers, attributes, behavior |
| weapons | Need combat behavior and attributes |
| armor | Need armor materials, models, trims/rendering considerations |
| baubles | Needs Curios or replacement accessory system |
| caster/focus items | Needs focus engine and data components |
| golem bell/placer/seals | Needs golem/seal systems |
| turret placer | Needs entity/turret systems |
| creative flux sponge | Debug/cheat item; depends on flux/aura systems |

## First coding task recommendation

The first actual coding task should be only Phase 1A plus two or three simple items.

Recommended first micro-batch:

1. `amber`
2. `quicksilver`
3. `fabric`

Why these:

- they are simple visual/resource items;
- they have obvious texture files;
- they have direct language entries;
- they do not require aspects, research, GUI, packets, or BlockEntity;
- they are enough to test the full item pipeline.

After that works, add Phase 1B rest.

## Prompt for a coding agent or future Codex run

Use this exact prompt style for the first coding slice:

```text
We are working on the Thaumcraft 6 to NeoForge 1.21.1 port in `05_neoforge_port`.

Read:
- `06_docs/gate1_items_plan.md`
- `06_docs/creative_tab_order_reference.md`
- `06_docs/migration_matrix.md`

Task: implement only Gate 1A and the first micro-batch of Gate 1B.

Add only these items:
- amber
- quicksilver
- fabric

Requirements:
- Use NeoForge 1.21.1 style.
- Register items in `TCItems` through `DeferredRegister.Items`.
- Create or update `TCCreativeTabs` to register the Thaumcraft creative tab.
- Create `TCCreativeTabOrder` and add implemented entries there.
- Do not alphabetize.
- Preserve relative legacy order among implemented items.
- Add `assets/thaumcraft/lang/en_us.json` entries.
- Add item model JSON files.
- Copy the matching legacy textures from `textures/items` to modern `textures/item` path.
- Do not add research, aura, aspects, networking, GUI, blocks, tools, armor, Baubles/Curios, or behavior.
- Show the list of modified files.
- Run `gradlew build --no-daemon` if possible.
```

## Expected files changed by the first micro-batch

| File | Expected status |
|---|---|
| `src/main/java/thaumcraft/common/registry/TCItems.java` | modified |
| `src/main/java/thaumcraft/common/registry/TCCreativeTabs.java` | modified |
| `src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java` | new |
| `src/main/resources/assets/thaumcraft/lang/en_us.json` | new or modified |
| `src/main/resources/assets/thaumcraft/models/item/amber.json` | new |
| `src/main/resources/assets/thaumcraft/models/item/quicksilver.json` | new |
| `src/main/resources/assets/thaumcraft/models/item/fabric.json` | new |
| `src/main/resources/assets/thaumcraft/textures/item/amber.png` | copied |
| `src/main/resources/assets/thaumcraft/textures/item/quicksilver.png` | copied |
| `src/main/resources/assets/thaumcraft/textures/item/fabric.png` | copied |

## Model JSON template

Each simple item model should look like this:

```json
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "thaumcraft:item/amber"
  }
}
```

Change `amber` to the matching item id.

## Language key strategy

Use modern keys:

```json
{
  "itemGroup.thaumcraft": "Thaumcraft",
  "item.thaumcraft.amber": "Amber",
  "item.thaumcraft.quicksilver": "Quicksilver",
  "item.thaumcraft.fabric": "Enchanted Fabric"
}
```

Do not copy `.lang` files directly. Convert required entries into `en_us.json`.

## Creative tab order strategy

`TCCreativeTabOrder.addEntries(output)` should use legacy visual order.

For the first micro-batch:

```java
public static void addEntries(CreativeModeTab.Output output) {
    output.accept(TCItems.AMBER.get());
    output.accept(TCItems.QUICKSILVER.get());
    output.accept(TCItems.FABRIC.get());
}
```

This is not the final full Thaumcraft order. It is only the relative order among implemented Gate 1 items.

When later items are added, insert them into this method at their correct legacy position. Do not append everything at the bottom.

## Review checklist after first micro-batch

After the first micro-batch, check:

- build passes;
- the mod loads;
- the Thaumcraft creative tab appears;
- amber, quicksilver, and fabric appear in that order;
- item names are translated;
- item textures display correctly;
- there are no missing model warnings for these items;
- no unrelated files were changed;
- no complex Thaumcraft systems were introduced.

## Later Gate 1 batch order

After the first micro-batch succeeds, add items in this staged order.

### Batch 1B-2: remaining simple scalar resources

1. `vis_resonator`
2. `tallow`
3. `mechanism_simple`
4. `mechanism_complex`
5. `filter`
6. `morphic_resonator`
7. `mirrored_glass`
8. `void_seed`

### Batch 1C-1: ingots

1. `ingot_thaumium`
2. `ingot_void`
3. `ingot_brass`

### Batch 1C-2: nuggets

1. `nugget_iron`
2. `nugget_copper`
3. `nugget_tin`
4. `nugget_silver`
5. `nugget_lead`
6. `nugget_quicksilver`
7. `nugget_thaumium`
8. `nugget_void`
9. `nugget_brass`
10. `nugget_quartz`
11. `nugget_rareearth`

### Batch 1C-3: clusters

1. `cluster_iron`
2. `cluster_gold`
3. `cluster_copper`
4. `cluster_tin`
5. `cluster_silver`
6. `cluster_lead`
7. `cluster_cinnabar`
8. `cluster_quartz`

### Batch 1C-4: plates

1. `plate_brass`
2. `plate_iron`
3. `plate_thaumium`
4. `plate_void`

### Batch 1C-5: simple golem/material variants

Only after previous material batches are stable:

1. `mind_clockwork`
2. `mind_biothaumic`
3. `module_vision`
4. `module_aggression`

## Tag work deferred from Gate 1

Do not block Gate 1 on tags, but create a TODO list for Gate 2 or Gate 3.

Likely tags later:

| Item | Later tag examples |
|---|---|
| `ingot_thaumium` | `c:ingots/thaumium`, `c:ingots` |
| `ingot_brass` | `c:ingots/brass`, `c:ingots` |
| `nugget_thaumium` | `c:nuggets/thaumium`, `c:nuggets` |
| `plate_brass` | `c:plates/brass`, `c:plates` |
| `cluster_cinnabar` | custom Thaumcraft/native cluster tag later |

Tags are important, but Gate 1 should prove item identity first.

## Data generation decision

For the first micro-batch, hand-written JSON is acceptable.

For larger item batches, consider adding datagen providers later for:

- item models;
- language entries;
- item tags.

Do not let datagen setup delay the first visible items. The first goal is a small working pipeline.

## Coordinator checklist

Because the coordinator has only basic programming knowledge, every coding task should be reviewed with this short checklist:

1. Did the agent change only the files it was supposed to change?
2. Did it add only the requested items?
3. Did it preserve creative order?
4. Did it avoid complex systems?
5. Did `build` pass?
6. Did the items appear correctly in game?
7. Are there obvious missing textures or purple-black models?
8. Was a backup/git commit made before continuing?

## Recommended commit structure

Use small commits.

Suggested commits:

```text
Gate 1A: add Thaumcraft creative tab scaffold
Gate 1B: add first simple item batch
Gate 1C: add material variant items
```

Do not make one giant commit for all items.

## Stop conditions

Stop and review if any of these happen:

- build fails;
- creative tab does not appear;
- item textures are missing;
- item names show as untranslated keys;
- the agent changes unrelated systems;
- the agent adds research/aspect/networking/GUI code;
- registry IDs are changed without documenting why;
- item order drifts away from the 1.12.2 reference.

## Gate 1 final deliverables

At the end of Gate 1, the project should have:

- `TCItems` with a first controlled item set;
- `TCCreativeTabs` with the Thaumcraft creative tab;
- `TCCreativeTabOrder` with explicit order;
- item models for the first batch;
- copied textures for the first batch;
- English names in `en_us.json`;
- successful build;
- screenshot comparison notes for implemented entries.

## Next gate after Gate 1

After Gate 1 succeeds, continue to Gate 2: simple block identity.

Gate 2 should add simple non-BlockEntity blocks and their block items. It should not yet add arcane workbench, jars, tubes, machines, aura nodes, essentia transport, or complex rendering.
