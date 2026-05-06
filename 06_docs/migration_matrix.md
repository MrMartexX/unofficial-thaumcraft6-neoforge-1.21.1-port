# Thaumcraft 6 to NeoForge 1.21.1 Migration Matrix

This document maps the Thaumcraft 6 Forge 1.12.2 codebase to the staged NeoForge 1.21.1 port.

For current implementation status, always read `06_docs/current_port_status.md` together with this matrix. This matrix defines scope, sequencing, and risk. The status document records what the repository currently contains.

## Source baseline

| Area | Location |
|---|---|
| Legacy source reference | `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master` |
| Original jar reference | `01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar` |
| API reference | `04_api_reference/thaumcraft-api-master` |
| NeoForge target project | `05_neoforge_port` |
| Main migration guide | `06_docs/NeoForge_legacy_migration_guide_expanded_v3.docx` |
| Current implementation status | `06_docs/current_port_status.md` |
| Subsystem inventory | `06_docs/subsystem_inventory.md` |
| Porting order | `06_docs/porting_order.md` |
| Creative tab reference | `06_docs/creative_tab_order_reference.md` |
| Visual reference | `07_Test_Instance_and_Comparisons/02_Thaumcraft 1.12.2 Inventory Screenshots` |

## Target baseline

| Component | Target |
|---|---|
| Minecraft | `1.21.1` |
| NeoForge | `21.1.228` |
| Java | `21` |
| Mod id | `thaumcraft` |
| Project state | Gate 0 complete enough to continue; Gate 1 and early Gate 2 identity work are in progress; active resources have been runtime-audited; original legacy asset corpus is imported as reference/base material |

## Purpose

Legacy Thaumcraft classes are behavior and architecture references. The NeoForge implementation must rebuild registration, resources, data loading, storage, networking, menus, screens, world generation, and rendering using current NeoForge 1.21.1 patterns.

The main rule from the migration guide is: port the role of each subsystem, not the old classes line by line.

## Global migration rules

1. Do not copy whole legacy classes directly into the NeoForge project.
2. Preserve gameplay behavior and public-facing identity before preserving class names.
3. Keep registry ids stable where practical, but do not preserve broken metadata patterns internally.
4. Preserve creative tab visual order from the Thaumcraft 6 1.12.2 reference screenshots.
5. Keep creative display order separate from registry declaration order.
6. Replace `OreDictionary` with tags and explicit data mappings.
7. Replace item metadata variants with separate items, data components, or explicit variant mapping.
8. Replace `TileEntity` systems with `BlockEntity`, `BlockEntityType`, `EntityBlock`, capabilities, menus, and payload sync.
9. Replace `SimpleNetworkWrapper` and `IMessage` with NeoForge custom payloads.
10. Replace legacy GUI classes with `Menu` and `Screen` systems.
11. Replace old worldgen hooks with data-driven features, biome modifiers, and datagen where practical.
12. Every gate must build before the next gate begins.
13. High-risk systems need a design note before code.

## Gate overview

| Gate | Name | Goal | Current status | Must not include yet |
|---|---|---|---|---|
| Gate 0 | Bootstrap | Empty NeoForge mod builds and loads | Complete enough to continue | Thaumcraft gameplay content |
| Gate 1 | Item identity | Safe simple items and creative tab order scaffold | In progress, expanded beyond first slice; active item models/lang covered | Research, aura, GUI, networking |
| Gate 2 | Block identity | Simple blocks, block items, models and loot | Started early; active blockstates/models/loot/lang covered; imported legacy assets remain reference | BlockEntity machines |
| Gate 3 | Data layer | Aspects, tags, data loaders and registries | Design-only started through `06_docs/aspects_design.md` | Gameplay-heavy crafting logic |
| Gate 4 | Basic BlockEntities | Storage, ticking and save/load | Not started | Final GUI and full sync |
| Gate 5 | Capabilities | Item, fluid, energy and essentia access patterns | Not started | Large machine networks |
| Gate 6 | Recipes | Vanilla-like, arcane, crucible and infusion serializers | Not started | Final research UI |
| Gate 7 | Player progression | Research, scanning, knowledge and warp | Not started | Final Thaumonomicon UI polish |
| Gate 8 | Menus and Screens | Modern GUI for machines and research | Not started | Large visual overhaul |
| Gate 9 | Networking | Custom payload categories and validation | Not started | Uncontrolled client authority |
| Gate 10 | Entities and golems | Entity types, AI, rendering and tasks | Not started | Full logistics polish |
| Gate 11 | Worldgen | Features, trees, biomes and structures | Started early only for sapling-grown trees; not a biome/worldgen system | Unsafe direct old generators |
| Gate 12 | Rendering and FX | BERs, entity renderers, particles and overlays | Started early through legacy-style FX scaffolding and imported visual assets | Raw GL copy-paste |
| Gate 13 | Integrations | Accessories, JEI/REI style hooks and optional APIs | Not started | Hard-required missing mods |
| Gate 14 | Parity and polish | Creative order, balance, compatibility and testing | Ongoing mindset | New unrelated features |

## Main subsystem matrix

| # | Subsystem | NeoForge 1.21.1 target | Difficulty | First safe step | Status |
|---:|---|---|---|---|---|
| 0 | Bootstrap project | Buildable NeoForge module with `Thaumcraft`, registries, config, metadata | Low | Preserve buildable state | Skeleton ready |
| 1 | Core mod initialization | Constructor bootstrap, mod event bus, `DeferredRegister`, side-safe client init | High | Replace architecture concepts, not proxy code | Skeleton ready |
| 2 | Mod metadata | `META-INF/neoforge.mods.toml`, current `pack.mcmeta`, generated metadata task | Low | Verify displayed mod metadata | Skeleton ready |
| 3 | Access transformer audit | Avoid ATs where possible; add only for proven blockers | High | Create `access_transformer_audit.md` before any AT | Design required |
| 4 | Creative tab and order | `CreativeModeTab.builder()` plus explicit display order class | High | Maintain `creative_tab_order_reference.md` | In progress |
| 5 | Simple item registry | `DeferredRegister.Items`, item models, lang, textures, tags | Medium | Keep active 1.21 `models/item`, `textures/item`, and `en_us.json` authoritative; port legacy entries only when their ids are registered | In progress; active resources covered |
| 6 | Item metadata variants | Separate items, data components, or explicit variant mapping | High | Build variant decision table | Design required |
| 7 | Caster and focus items | Data components, validated payloads, modern item callbacks, staged focus API | Very high | Add placeholder data model only after simple items | Port later |
| 8 | Armor, baubles and wearables | Vanilla equipment first, optional accessory integration later | High | Port non-accessory armor identity first | Port later |
| 9 | Item behavior marker API | Internal interfaces first; public API after stable behavior | Medium | Recreate minimal marker interfaces without behavior | Design required |
| 10 | Basic blocks | `DeferredRegister.Blocks`, block items, blockstates, models, loot tables | Medium | Keep current 1.21 blockstates/models/loot authoritative over imported legacy variants | In progress; active resources covered |
| 11 | Block metadata variants | `BlockState` or separate blocks | High | Create block variant mapping table | Design required |
| 12 | Aspects model | Modern aspect service, data-driven assignments, tags, reload support | High | Implement immutable aspect definitions and id-keyed amount lists after `aspects_design.md` | Design complete; implementation not started |
| 13 | Aspect assignment | JSON/datapack or generated mappings to items, blocks, tags, entities | High | Implement exact id/tag assignment loader after the aspect value model | Design complete; implementation not started |
| 14 | Essentia transport API | BlockEntity capabilities or explicit service interfaces | Very high | Define modern essentia access interface | Port later |
| 15 | Aura storage | Server-owned `SavedData` or chunk attachments, safe tick/update loop | Very high | Prototype server-side aura data without visuals | Port later |
| 16 | Research model | Reloadable data loader/datapack format, validation, datagen | Very high | Create model classes and load a tiny test category | Design required |
| 17 | Player knowledge | Player attachment/capability, explicit sync payloads, server authority | Very high | Store one test research flag per player | Port later |
| 18 | Scanning system | Tags, item predicates, entity predicates, server validation | High | Port scan predicate model, no GUI | Port later |
| 19 | Thaumonomicon UI | Modern `Screen`, custom widgets, client data cache | Critical | Build mock screen only after data model is stable | Port later |
| 20 | Arcane crafting | Custom recipe type/serializer, menu, BlockEntity, vis checks | Very high | Define recipe JSON/serializer first | Port later |
| 21 | Infusion crafting | Custom recipe type, modern BE scanning, payload FX, stability service | Critical | Write design document before code | Port later |
| 22 | Crucible and alchemy | Custom recipe types, BE save/tick, capability-based essentia access | Critical | Prototype crucible data model first | Port later |
| 23 | BlockEntity base classes | `BlockEntity`, `EntityBlock#getTicker`, `CompoundTag`, `setChanged` | Very high | Create one simple non-inventory BE first | Not started |
| 24 | Menus and machine GUI | `AbstractContainerMenu`, `MenuType`, `Screen`, data slots, payload actions | Very high | Port one simple inventory menu first | Port later |
| 25 | Networking | `CustomPacketPayload`, `StreamCodec`, `PayloadRegistrar`, validated handlers | Very high | Define packet categories and policies first | Design required |
| 26 | Entities and golems | `EntityType`, `SynchedEntityData`, goals, attributes, renderers | Very high | Port one non-golem test entity later | Port later |
| 27 | Worldgen | Datapack/datagen features, biome modifiers, structures | Critical | Convert sapling tree behavior into a modern design before expanding to biome/world placement | Early sapling-only work |
| 28 | Rendering and FX | BERs, entity renderers, particles, overlays, `PoseStack` | Critical | Validate imported FX/textures and isolate old GL/shader assumptions before expanding | Early FX work |
| 29 | Optional integrations | Optional modern abstraction, accessory/recipe viewer hooks later | High | Keep integrations disabled until core is stable | Port later |
| 30 | Parity and QA | Page-by-page comparison, targeted checklists, build gates | High | Maintain visual and behavior checklists | Ongoing |

## Legacy asset corpus policy

All original asset files from `03_self_decompiled_check/vineflower_thaumcraft6/assets` are now present in the NeoForge project unless an adapted 1.21 file already existed at the same path. The import is intentionally non-destructive:

| Category | Status | Notes |
|---|---|---|
| Adapted 1.21 resources | Authoritative for registered content | Existing `blockstates`, `models/block`, `models/item`, `textures/block`, `textures/item`, data loot tables, and tags must not be replaced by old Forge 1.12 variants without review. |
| Imported legacy resources | Reference/base corpus | Includes legacy `textures/blocks`, GUI/research/entity textures, sounds, OBJ/MTL models, old `.lang`, legacy research JSON, shaders, and legacy `loot_tables`. |
| `assets/minecraft/shaders` | Imported but high risk | These old shader files are retained as reference assets. Do not wire them into rendering until the 1.21 shader pipeline is reviewed. |
| Legacy blockstates/models for unported content | Deferred adaptation | Safe to keep as source material, but each registered block/item needs a modern resource audit before it is considered ported. |

Active registered content cleanup note: `amber`, `quicksilver`, and `fabric` item models now point at modern `thaumcraft:item/*` textures, with active PNGs copied into `textures/item`. Do not mass-convert unregistered legacy `thaumcraft:items/*` or `thaumcraft:blocks/*` references until those ids are intentionally ported.

## Legacy API replacement matrix

| Legacy API / pattern | NeoForge 1.21.1 target | Notes |
|---|---|---|
| `GameRegistry.register` | `DeferredRegister` | Registry declarations only, no creative ordering logic. |
| `CreativeTabs` / `getSubItems` | `CreativeModeTab.builder()` and explicit output order | Final order must be screenshot-reviewed. |
| Item metadata variants | Separate items or data components | Decide per variant group before public builds. |
| `OreDictionary` | Tags and explicit data mappings | Common tags where compatible. |
| `TileEntity` | `BlockEntity` | Ticker in block class through `EntityBlock#getTicker`. |
| `IInventory` | `ItemStackHandler`, capabilities, menus | Do not expose legacy inventory directly. |
| `IMessage` / `SimpleNetworkWrapper` | Custom payloads | Validate all client-to-server requests. |
| `GuiContainer` / `IGuiHandler` | `AbstractContainerMenu`, `Screen`, `openMenu` | Keep client-only code isolated. |
| Raw GL render code | Modern rendering abstractions | Do not copy GL code without redesign. |
| Old world generators | Datapack/datagen features and biome modifiers | Design before code. |
| Baubles API | Optional modern accessory integration | Do not hard require until selected. |

## Immediate next work

1. Read `06_docs/current_port_status.md`, `06_docs/runtime_asset_audit.md`, and `06_docs/aspects_design.md`.
2. Re-run local build from `05_neoforge_port` after each change batch.
3. Use the next client visual pass to confirm the fixed active item texture paths and review creative tab order.
4. If starting aspects implementation, keep it limited to the data-layer checklist in `aspects_design.md`.
5. Do not begin aura, research, recipes, BlockEntities, networking, GUI, or expanded worldgen without a design note.
