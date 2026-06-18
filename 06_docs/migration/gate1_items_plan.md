# Gate 1 Item Identity Workflow

This document defines the safe workflow for item-focused Gate 1 work in the Thaumcraft 6 NeoForge 1.21.1 port.

This is no longer the live implementation inventory. For current repository status and the list of already implemented entries, use `06_docs/current_port_status.md`.

## Scope

| Area | Location |
|---|---|
| Target module | `05_neoforge_port` |
| Legacy source reference | `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master` |
| Visual reference | `07_Test_Instance_and_Comparisons/02_Thaumcraft 1.12.2 Inventory Screenshots` |
| Migration matrix | `06_docs/migration/migration_matrix.md` |
| Creative order reference | `06_docs/resources/creative_tab_order_reference.md` |
| Current status | `06_docs/current_port_status.md` |

## Purpose

Gate 1 establishes a stable item-porting workflow before gameplay-heavy systems are introduced.

The workflow covers:

1. simple item registration through `TCItems`;
2. Thaumcraft creative tab registration;
3. explicit creative tab ordering through `TCCreativeTabOrder`;
4. language entries;
5. item model JSON files;
6. texture migration or documented temporary texture mapping;
7. local build and client validation.

Gate 1 does not cover research, aura, aspects, GUI, networking, world generation, Baubles/Curios behavior, focus behavior, or complex item logic.

## Hard rules

1. Do not port all Thaumcraft items at once.
2. Do not implement gameplay behavior unless the item is genuinely simple.
3. Do not add research, aura, aspects, spell casting, GUI, networking, or worldgen in this gate.
4. Do not alphabetize item order.
5. Do not use registry declaration order as the creative tab display order.
6. Preserve the visible 1.12.2 Thaumcraft creative tab order as closely as practical.
7. Keep implemented entries in the same relative order as legacy Thaumcraft.
8. Leave gaps for unimplemented legacy entries during partial porting.
9. Every small batch must build before the next batch begins.
10. Complex items should be deferred or explicitly marked as inert identity placeholders.

## Current baseline note

The original first implemented Gate 1 slice was:

| Legacy field | Registry id | Display name | Gate 1 behavior |
|---|---|---|---|
| `ItemsTC.amber` | `amber` | Amber | Plain item |
| `ItemsTC.quicksilver` | `quicksilver` | Quicksilver | Plain item |
| `ItemsTC.fabric` | `fabric` | Enchanted Fabric | Plain item |

The repository has since expanded beyond this slice and now includes additional block items, simple blocks, plants, and plain items. Do not use this document as a full inventory of implemented content.

## Required files for each simple item

| File type | Path |
|---|---|
| Java registry entry | `05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java` |
| Creative tab entry | `05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java` |
| Model JSON | `05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/<item_id>.json` |
| Lang entry | `05_neoforge_port/src/main/resources/assets/thaumcraft/lang/en_us.json` |
| Texture | `05_neoforge_port/src/main/resources/assets/thaumcraft/textures/items/<item_id>.png` |

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

This is acceptable for the initial port because the legacy Thaumcraft asset tree already uses `textures/items`. A later resource cleanup may standardize paths, but it should be done deliberately and consistently across all item models and copied textures.

## Simple item candidate status

The Gate 1 candidate list is historical workflow guidance. Some entries below
are already registered because later research/arcane recipe slices needed their
stable ids. Future Gate 1 work should continue with low-risk resource identity
entries only after the current implemented entries pass asset and creative-order
audit.

| Suggested order | Legacy field | Recommended id | Legacy texture | Gate 1 behavior | Status |
|---:|---|---|---|---|---|
| 1 | `ItemsTC.visResonator` | `vis_resonator` | `vis_resonator.png` | Plain item | Registered; exact arcane recipe/page fixture exists; behavior later if required |
| 2 | `ItemsTC.tallow` | `tallow` | `tallow.png` | Plain item | Still a low-risk future candidate |
| 3 | `ItemsTC.mechanismSimple` | `mechanism_simple` | `mechanism_simple.png` | Plain item | Registered; exact arcane recipe/page fixture exists |
| 4 | `ItemsTC.mechanismComplex` | `mechanism_complex` | `mechanism_complex.png` | Plain item | Registered; exact arcane recipe/page fixture exists |
| 5 | `ItemsTC.filter` | `filter` | `filter.png` | Plain item | Registered; exact Basic Alchemy arcane recipe/page fixture exists; essentia behavior deferred |
| 6 | `ItemsTC.morphicResonator` | `morphic_resonator` | `morphic_resonator.png` | Plain item | Registered; exact Basic Alchemy arcane recipe/page fixture exists; behavior deferred |
| 7 | `ItemsTC.voidSeed` | `void_seed` | `void_seed.png` | Plain item | Still a low-risk future candidate |

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
| Foci and caster items | Require data components, spell logic, networking, and rendering. |
| Thaumonomicon | Requires research data and UI systems. |
| Salis Mundus behavior | Requires block interaction logic and progression rules. |
| Baubles/accessories | Requires accessory integration decision. |
| Phials and essentia containers | Require aspects, essentia storage, and possibly custom rendering. |
| Seals and golem tools | Require golem task systems and dynamic registries. |
| Research-only or hidden items | Require progression and visibility rules. |

## Acceptance checklist for each item batch

1. Java registry entries compile.
2. Item models exist.
3. Lang entries exist.
4. Textures exist or temporary texture mapping is documented.
5. Creative tab entries are explicit and ordered.
6. The item batch does not introduce unrelated systems.
7. `gradlew build` passes locally.
8. `runClient` opens and the tab can be visually checked.
9. `runServer` is checked if any shared/common code changed near client-only behavior.

## Stop conditions

Stop the current batch and review if:

1. an item requires NBT, data components, packets, or a custom renderer;
2. an item requires research, aura, or aspects to behave correctly;
3. a registry id is uncertain;
4. the legacy item is a metadata variant and the variant mapping is not documented;
5. build fails;
6. the client opens but items show as missing models or missing textures.
