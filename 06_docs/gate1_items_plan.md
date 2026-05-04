# Gate 1 Item Identity Plan

This document defines the first item-focused implementation gate for the Thaumcraft 6 NeoForge 1.21.1 port.

## Scope

| Area | Location |
|---|---|
| Target module | `05_neoforge_port` |
| Legacy source reference | `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master` |
| Visual reference | `07_Test_Instance_and_Comparisons/02_Thaumcraft 1.12.2 Inventory Screenshots` |
| Migration matrix | `06_docs/migration_matrix.md` |
| Creative order reference | `06_docs/creative_tab_order_reference.md` |

## Purpose

Gate 1 establishes a stable item-porting workflow before gameplay-heavy systems are introduced.

The gate covers:

1. simple item registration through `TCItems`;
2. Thaumcraft creative tab registration;
3. explicit creative tab ordering through `TCCreativeTabOrder`;
4. language entries;
5. item model JSON files;
6. texture migration or documented temporary texture mapping;
7. local build and client validation.

Gate 1 does not cover research, aura, aspects, GUI, networking, world generation, Baubles/Curios behavior, focus behavior or complex item logic.

## Hard rules

1. Do not port all Thaumcraft items at once.
2. Do not implement gameplay behavior unless the item is genuinely simple.
3. Do not add research, aura, aspects, spell casting, GUI, networking or worldgen in this gate.
4. Do not alphabetize item order.
5. Do not use registry declaration order as the creative tab display order.
6. Preserve the visible 1.12.2 Thaumcraft creative tab order as closely as practical.
7. Keep implemented entries in the same relative order as legacy Thaumcraft.
8. Leave gaps for unimplemented legacy entries during partial porting.
9. Every small batch must build before the next batch begins.
10. Complex items should be deferred or explicitly marked as inert identity placeholders.

## Current baseline

The target NeoForge project already contains:

| Component | Status |
|---|---|
| `thaumcraft.Thaumcraft` | Present |
| `thaumcraft.common.registry.TCItems` | Present |
| `thaumcraft.common.registry.TCBlocks` | Present |
| `thaumcraft.common.registry.TCCreativeTabs` | Present |
| `thaumcraft.common.config.TCConfig` | Present |
| `META-INF/neoforge.mods.toml` template | Present |

The local build passed before Gate 1 began and should remain the baseline for regression checks.

## Success criteria

Gate 1 is complete when:

1. the Thaumcraft creative tab exists;
2. a controlled batch of simple items is registered;
3. registered items have English language entries;
4. registered items have item model JSON files;
5. registered items have textures or documented temporary texture mapping;
6. implemented items appear in the Thaumcraft creative tab;
7. implemented items preserve relative order from Thaumcraft 6 1.12.2;
8. `gradlew build` passes locally;
9. `runClient` opens without missing-model crashes;
10. no complex Thaumcraft systems are introduced accidentally.

Recommended local validation from `05_neoforge_port`:

```powershell
.\gradlew.bat build --no-daemon
.\gradlew.bat runClient --no-daemon
```

## Implementation contract

### Item registry

`TCItems` registers item objects only. Creative ordering logic must not be placed in this class.

```java
public final class TCItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Thaumcraft.MODID);

    public static final Supplier<Item> AMBER = ITEMS.register("amber", () -> new Item(new Item.Properties()));

    private TCItems() {}
}
```

### Creative tab registration

`TCCreativeTabs` registers the Thaumcraft creative tab and delegates display order to `TCCreativeTabOrder`.

```java
.displayItems((parameters, output) -> TCCreativeTabOrder.addThaumcraftItems(output))
```

### Creative order class

`TCCreativeTabOrder` owns visible ordering.

```java
public final class TCCreativeTabOrder {
    private TCCreativeTabOrder() {}

    public static void addThaumcraftItems(CreativeModeTab.Output output) {
        // Add only implemented entries, in legacy visual order.
    }
}
```

## Resource path policy

Gate 1 currently uses legacy texture folder paths in item model JSON files:

```text
assets/thaumcraft/models/item/<item_id>.json
layer0: thaumcraft:items/<item_id>
```

This maps to:

```text
assets/thaumcraft/textures/items/<item_id>.png
```

This is acceptable for the initial Gate 1 slice because the legacy Thaumcraft asset tree already uses `textures/items`. A later resource cleanup may standardize paths, but it should be done deliberately and consistently across all item models and copied textures.

## First implemented item slice

| Order | Legacy field | Registry id | Display name | Gate 1 behavior | Status |
|---:|---|---|---|---|---|
| 1 | `ItemsTC.amber` | `amber` | Amber | Plain item | Implemented |
| 2 | `ItemsTC.quicksilver` | `quicksilver` | Quicksilver | Plain item | Implemented |
| 3 | `ItemsTC.fabric` | `fabric` | Enchanted Fabric | Plain item | Implemented |

These entries are a controlled implementation slice, not the beginning of the final full Thaumcraft tab. Earlier unimplemented legacy entries remain implicit gaps.

## Required files for each simple item

| File type | Path |
|---|---|
| Java registry entry | `05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java` |
| Creative tab entry | `05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java` |
| Model JSON | `05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/<item_id>.json` |
| Lang entry | `05_neoforge_port/src/main/resources/assets/thaumcraft/lang/en_us.json` |
| Texture | `05_neoforge_port/src/main/resources/assets/thaumcraft/textures/items/<item_id>.png` |

## Next simple item candidates

The next Gate 1 expansion should continue with simple, low-risk resource identity entries.

| Suggested order | Legacy field | Recommended id | Legacy texture | Gate 1 behavior | Notes |
|---:|---|---|---|---|---|
| 4 | `ItemsTC.visResonator` | `vis_resonator` | `vis_resonator.png` | Plain item | Behavior later if required |
| 5 | `ItemsTC.tallow` | `tallow` | `tallow.png` | Plain item | Simple resource |
| 6 | `ItemsTC.mechanismSimple` | `mechanism_simple` | `mechanism_simple.png` | Plain item | Simple resource |
| 7 | `ItemsTC.mechanismComplex` | `mechanism_complex` | `mechanism_complex.png` | Plain item | Simple resource |
| 8 | `ItemsTC.filter` | `filter` | `filter.png` | Plain item | Simple resource |
| 9 | `ItemsTC.morphicResonator` | `morphic_resonator` | `morphic_resonator.png` | Plain item | Behavior deferred |
| 10 | `ItemsTC.voidSeed` | `void_seed` | `void_seed.png` | Plain item | Simple resource |

Do not add `salis_mundus` as a normal simple item unless its block interaction behavior is explicitly deferred and documented. In Thaumcraft 6, it participates in block interaction logic through `IDustTrigger`.

## Metadata variant strategy

Thaumcraft 6 1.12.2 uses one registry item plus metadata variants for several material groups. Gate 1 should prefer explicit separate items for simple visible material variants.

| Legacy registry item | Legacy variant | Recommended NeoForge id | Reason |
|---|---|---|---|
| `ingot` | `thaumium` | `ingot_thaumium` | Clear resource identity |
| `ingot` | `void` | `ingot_void` | Avoids old metadata logic |
| `ingot` | `brass` | `ingot_brass` | Clear resource identity |
| `nugget` | `quicksilver` | `nugget_quicksilver` | Matches variant identity |
| `cluster` | `cinnabar` | `cluster_cinnabar` | Matches variant identity |
| `plate` | `thaumium` | `plate_thaumium` | Matches variant identity |

Registry ids become world-save-visible once released. Final id style should be settled before public builds.

## Deferred item categories

| Category | Reason |
|---|---|
| Foci and caster items | Require data components, spell logic, networking and rendering |
| Thaumonomicon | Requires research data and UI systems |
| Salis Mundus behavior | Requires block interaction logic and progression rules |
| Baubles/accessories | Requires accessory integration decision |
| Phials and essentia containers | Require aspects, essentia storage and possibly custom rendering |
| Seals and golem tools | Require golem task systems and dynamic registries |
| Research-only or hidden items | Require progression and visibility rules |

## Acceptance checklist for each item batch

1. Java registry entries compile.
2. Item models exist.
3. Lang entries exist.
4. Textures exist or temporary texture mapping is documented.
5. Creative tab entries are explicit and ordered.
6. The item batch does not introduce unrelated systems.
7. `gradlew build` passes locally.
8. `runClient` opens and the tab can be visually checked.

## Stop conditions

Stop the current batch and review if:

1. an item requires NBT, data components, packets or a custom renderer;
2. an item requires research, aura or aspects to behave correctly;
3. a registry id is uncertain;
4. the legacy item is a metadata variant and the variant mapping is not documented;
5. build fails;
6. the client opens but items show as missing models or missing textures.
