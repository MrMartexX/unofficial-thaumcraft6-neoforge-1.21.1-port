# Thaumcraft 6 to NeoForge 1.21.1 Migration Matrix

This document maps the Thaumcraft 6 Forge 1.12.2 codebase to a staged NeoForge 1.21.1 migration plan.

## Source baseline

| Area | Location |
|---|---|
| Legacy source reference | `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master` |
| Original jar reference | `01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar` |
| API reference | `04_api_reference/thaumcraft-api-master` |
| NeoForge target project | `05_neoforge_port` |
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
| Project state | Gate 0 bootstrap exists and local build has passed |

## Purpose

The matrix is a planning artifact for maintaining migration scope, sequencing and technical risk. Legacy Thaumcraft classes should be treated as behavior and architecture references. The NeoForge implementation should rebuild registration, resources, data loading, storage, networking, menus, screens, world generation and rendering using current NeoForge 1.21.1 patterns.

## Global migration rules

1. Do not copy whole legacy classes directly into the NeoForge project.
2. Preserve gameplay behavior and public-facing identity before preserving class names.
3. Keep registry ids stable where practical, but do not preserve broken metadata patterns internally.
4. Preserve creative tab visual order from the Thaumcraft 6 1.12.2 reference screenshots.
5. Keep creative display order separate from registry declaration order.
6. Replace `OreDictionary` with tags and explicit data mappings.
7. Replace item metadata variants with separate items, data components or explicit variant mapping.
8. Replace `TileEntity` systems with `BlockEntity`, `BlockEntityType`, `EntityBlock`, capabilities, menus and payload sync.
9. Replace `SimpleNetworkWrapper` and `IMessage` with NeoForge custom payloads.
10. Replace legacy GUI classes with `Menu` and `Screen` systems.
11. Replace old worldgen hooks with data-driven features, biome modifiers and datagen where practical.
12. Every gate must build before the next gate begins.

## Difficulty scale

| Difficulty | Meaning | Practical consequence |
|---|---|---|
| Low | Mostly data or simple registry work | Can be done early, with build validation |
| Medium | API layer must be rewritten, gameplay logic is limited | Requires focused implementation and testing |
| High | Several systems interact | Requires design notes before code |
| Very high | Stateful, networked, rendered or data-driven system | Split into multiple gates |
| Critical | Direct port is unsafe or unrealistic | Rebuild as modern architecture using legacy behavior as reference |

## Gate overview

| Gate | Name | Goal | Must not include yet |
|---|---|---|---|
| Gate 0 | Bootstrap | Empty NeoForge mod builds and loads | Thaumcraft gameplay content |
| Gate 1 | Item identity | Safe simple items and creative tab order scaffold | Research, aura, GUI, networking |
| Gate 2 | Block identity | Simple blocks, block items, models and loot | BlockEntity machines |
| Gate 3 | Data layer | Aspects, tags, data loaders and registries | Gameplay-heavy crafting logic |
| Gate 4 | Basic BlockEntities | Storage, ticking and save/load | Final GUI and full sync |
| Gate 5 | Capabilities | Item, fluid, energy and essentia access patterns | Large machine networks |
| Gate 6 | Recipes | Vanilla-like, arcane, crucible and infusion serializers | Final research UI |
| Gate 7 | Player progression | Research, scanning, knowledge and warp | Final Thaumonomicon UI polish |
| Gate 8 | Menus and Screens | Modern GUI for machines and research | Large visual overhaul |
| Gate 9 | Networking | Custom payload categories and validation | Uncontrolled client authority |
| Gate 10 | Entities and golems | Entity types, AI, rendering and tasks | Full logistics polish |
| Gate 11 | Worldgen | Features, trees, biomes and structures | Unsafe direct old generators |
| Gate 12 | Rendering and FX | BERs, entity renderers, particles and overlays | Raw GL copy-paste |
| Gate 13 | Integrations | Accessories, JEI/REI style hooks and optional APIs | Hard-required missing mods |
| Gate 14 | Parity and polish | Creative order, balance, compatibility and testing | New unrelated features |

## Main subsystem matrix

| # | Subsystem | Legacy source | Legacy APIs and risks | NeoForge 1.21.1 target | Difficulty | Dependencies | First safe step | Status |
|---:|---|---|---|---|---|---|---|---|
| 0 | Bootstrap project | `05_neoforge_port` | None, already modern scaffold | Keep buildable project with `Thaumcraft`, `TCItems`, `TCBlocks`, `TCCreativeTabs`, `TCConfig` | Low | None | Preserve current buildable state | Skeleton ready |
| 1 | Core mod initialization | `thaumcraft.Thaumcraft`, `Registrar`, `proxies.*` | `@Mod.EventHandler`, proxies, old lifecycle, `GameRegistry` | Constructor bootstrap, mod event bus, `NeoForge.EVENT_BUS`, `DeferredRegister`, side-safe client init | High | Gate 0 | Replace architecture concepts, not proxy code | Skeleton ready |
| 2 | Mod metadata | `mcmod.info`, `pack.mcmeta`, manifest | Old Forge metadata format | `META-INF/neoforge.mods.toml`, current `pack.mcmeta`, generated metadata task | Low | Gate 0 | Verify displayed mod name, version, authors and description | Skeleton ready |
| 3 | Access transformer audit | `META-INF/tc_at.cfg` | Old private/protected access hacks may not map to 1.21.1 | Avoid ATs where possible; add modern ATs only for proven blockers | High | Gate 0 | Create `access_transformer_audit.md` before using any AT | Design required |
| 4 | Creative tab and order | `CreativeTabThaumcraft`, `ConfigItems`, screenshots | Old order depends on registration, subitems, metadata and hidden items | `CreativeModeTab.builder()` plus explicit display order class | High | Gate 1 | Maintain `creative_tab_order_reference.md` | In progress |
| 5 | Simple item registry | `ConfigItems`, `ItemsTC`, `common.items.resources.*` | Static `ItemsTC`, metadata variants, `OreDictionary`, old callbacks | `DeferredRegister.Items`, tags, data components, item models, lang | Medium | Gate 0, creative order | Port a small simple item batch | In progress |
| 6 | Item metadata variants | ingots, nuggets, clusters, plates, curios, loot bags, focus items | 1.12 item damage/meta variants do not map cleanly | Separate items, data components or explicit variant mapping | High | Gate 1 | Build variant decision table before coding variants | Design required |
| 7 | Caster and focus items | `common.items.casters.*`, API `casters.*` | NBT-heavy, custom behavior, packets, rendering | Data components, validated payloads, modern item callbacks, staged focus API | Very high | Aspects, aura, networking, rendering | Add placeholder data model only after simple items | Port later |
| 8 | Armor, baubles and wearables | `common.items.armor.*`, `common.items.baubles.*` | Baubles dependency, old equipment slots, render hooks | Vanilla equipment first, optional accessory integration later | High | Items, integrations | Port non-accessory armor identity first | Port later |
| 9 | Item behavior marker API | `IGoggles`, `IRevealer`, `IVisDiscountGear`, `IWarpingGear`, `IScribeTools` | Old addon API, Baubles assumptions, HUD hooks | Internal interfaces first; public API after stable behavior | Medium | Items, capabilities, rendering | Recreate minimal marker interfaces without behavior | Design required |
| 10 | Basic blocks | `ConfigBlocks`, `BlocksTC`, `common.blocks.basic.*` | Static `BlocksTC`, old blockstate/model schemas | `DeferredRegister.Blocks`, block items, generated blockstates/models, loot tables | Medium | Gate 0, item conventions | Port simple non-BE blocks first | Not started |
| 11 | Block metadata variants | crystals, stones, planks, world blocks, devices | Old metadata/subtype behavior | `BlockState` or separate blocks | High | Basic blocks | Create block variant mapping table | Design required |
| 12 | Aspects model | `api.aspects.*`, `ConfigAspects` | Static maps, `OreDictionary`, metadata matching | Modern aspect service, data-driven assignments, tags, reload support | High | Items, blocks, tags | Port core aspect definitions and value object | Design required |
| 13 | Aspect assignment | `AspectHelper`, `AspectEventProxy`, `CommonInternals` | Static mutable maps, item/meta/entity keys | JSON/datapack or generated mappings to items, blocks, tags, entities | High | Aspects, item/block ids | Create data format and loader before content data | Design required |
| 14 | Essentia transport API | `IAspectContainer`, `IAspectSource`, `IEssentiaTransport` | Old TileEntity interfaces and side checks | BlockEntity capabilities or explicit service interfaces | Very high | BlockEntities, capabilities, aspects | Define modern essentia access interface | Port later |
| 15 | Aura storage | `AuraHandler`, `AuraWorld`, `AuraChunk`, `AuraThread` | Manual thread, static world maps, chunk lifecycle, packets | Server-owned `SavedData` or chunk attachments, safe tick/update loop | Very high | Aspects, storage design | Prototype server-side aura data without visuals | Port later |
| 16 | Research model | `ResearchCategories`, `ResearchEntry`, `ResearchStage`, `ConfigResearch` | Manual asset JSON loading, old recipe references | Reloadable data loader or datapack format, validation, datagen | Very high | Items, blocks, aspects, recipes | Create model classes and load a tiny test category | Design required |
| 17 | Player knowledge | `PlayerKnowledge`, `IPlayerKnowledge` | Forge 1.12 capabilities, manual packets | Player attachment/capability, explicit sync payloads, server authority | Very high | Research model, networking | Store one test research flag per player | Port later |
| 18 | Scanning system | `ScanningManager`, `ScanItem`, `ScanBlock`, `ScanEntity` | Metadata and ore dictionary predicates | Tags, item predicates, entity predicates, server validation | High | Aspects, research, tags | Port scan predicate model, no GUI | Port later |
| 19 | Thaumonomicon UI | `GuiResearchBrowser`, `GuiResearchPage`, plugins | Old GUI, raw GL, research page renderer | Modern `Screen`, custom widgets, client data cache | Critical | Research data, networking, rendering | Build mock screen only after data model is stable | Port later |
| 20 | Arcane crafting | `IArcaneRecipe`, `ShapedArcaneRecipe`, `TileArcaneWorkbench` | Old `IRecipe`, `InventoryCrafting`, old container | Custom recipe type/serializer, menu, BlockEntity, vis checks | Very high | Items, blocks, aspects, aura, research | Define recipe JSON/serializer first | Port later |
| 21 | Infusion crafting | `InfusionRecipe`, `TileInfusionMatrix`, `TilePedestal` | BlockEntity network, instability, packets, particles | Custom recipe type, modern BE scanning, payload FX, stability service | Critical | Aspects, aura, recipes, BE, networking, particles | Write design document before code | Port later |
| 22 | Crucible and alchemy | `CrucibleRecipe`, `TileCrucible`, `TileThaumatorium` | BE ticking, aspects, fluid/block interaction, menus | Custom recipe types, BE save/tick, capability-based essentia access | Critical | Aspects, BE, recipes, GUI, networking | Prototype crucible data model first | Port later |
| 23 | BlockEntity base classes | `TileThaumcraft`, `TileThaumcraftInventory` | `TileEntity`, `ITickable`, `NBTTagCompound`, `markDirty` | `BlockEntity`, `EntityBlock#getTicker`, `CompoundTag`, `setChanged` | Very high | Blocks | Create one simple non-inventory BE first | Not started |
| 24 | Menus and machine GUI | `common.container.*`, `Gui*` classes | `Container`, `GuiContainer`, `IGuiHandler`, raw GL | `AbstractContainerMenu`, `MenuType`, `Screen`, data slots, payload actions | Very high | BlockEntities, networking | Port one simple inventory menu first | Port later |
| 25 | Networking | `Packet*`, `PacketHandler`, `SimpleNetworkWrapper` | `IMessage`, old packet side handling, client trust risks | `CustomPacketPayload`, `StreamCodec`, `PayloadRegistrar`, validated handlers | Very high | Menus, research, aura, entities | Define packet categories and policies first | Design required |
| 26 | Entities and golems | `common.entities.*`, `common.golems.*` | Old `EntityRegistry`, AI tasks, data watcher, rendering | `EntityType`, `SynchedEntityData`, goals, attributes, client renderers | Very high | Items, blocks, networking, rendering | Port one non-golem test entity later | Port later |
| 27 | Worldgen | `world.*`, features, trees, structures | Old generators, biome hooks, numeric assumptions | Datapack/datagen features, biome modifiers, structures | Critical | Blocks, items, tags | Design worldgen data mapping first | Port later |
| 28 | Rendering and FX | `client.fx.*`, `client.renderers.*`, GUI GL code | Raw GL, TESR, old model assumptions | `BlockEntityRenderer`, `EntityRenderer`, particles, overlays, `PoseStack` | Critical | Content systems, networking | Port only after server logic exists | Port later |
| 29 | Optional integrations | Baubles, JEI style hooks, other mods | Hard dependency and old API risk | Optional modern abstraction, Curios/Accessories later if selected | High | Items, capabilities | Keep integrations disabled until core is stable | Port later |
| 30 | Parity and QA | Creative screenshots, original jar, test instance | Visual drift and feature regression | Page-by-page comparison, targeted checklists and build gates | High | All gates | Maintain visual and behavior checklists | Ongoing |

## Legacy API replacement matrix

| Legacy API / pattern | NeoForge 1.21.1 target | Notes |
|---|---|---|
| `GameRegistry.register` | `DeferredRegister` | Registry declarations only, no creative ordering logic |
| `RegistryEvent.Register` | `DeferredRegister` or `RegisterEvent` | Prefer `DeferredRegister` for stable project conventions |
| `CreativeTabs` / `getSubItems` | `CreativeModeTab.builder()` and explicit output order | Final order must be screenshot-reviewed |
| Item metadata variants | Separate items or data components | Decide per variant group before public builds |
| `OreDictionary` | Tags and explicit data mappings | Common tags where compatible |
| `TileEntity` | `BlockEntity` | Ticker in block class through `EntityBlock#getTicker` |
| `IInventory` | `ItemStackHandler`, capabilities, menus | Do not expose legacy inventory directly |
| `IMessage` / `SimpleNetworkWrapper` | Custom payloads | Validate all client-to-server requests |
| `GuiContainer` / `IGuiHandler` | `AbstractContainerMenu`, `Screen`, `openMenu` | Keep client-only code isolated |
| Raw GL render code | Modern rendering abstractions | Do not copy GL code without redesign |
| Old world generators | Datapack/datagen features and biome modifiers | Design before code |
| Baubles API | Optional modern accessory integration | Do not hard require until selected |

## Gate 1 current scope

Gate 1 establishes item identity, creative tab order scaffolding and the first simple item batch. It is intentionally narrow. The first implemented entries are:

| Item | Registry id | Gate 1 behavior | Notes |
|---|---|---|---|
| Amber | `amber` | Plain item | Temporary creative tab icon until Thaumonomicon exists |
| Quicksilver | `quicksilver` | Plain item | Simple material item |
| Enchanted Fabric | `fabric` | Plain item | Simple material item |

Gate 1 is not complete until all three entries have models, lang entries, textures or documented temporary texture mapping, and the project builds locally.

## Immediate next work

1. Complete missing resources for the first Gate 1 item batch.
2. Confirm local build from `05_neoforge_port`.
3. Launch client and verify that the Thaumcraft creative tab exists.
4. Check that the three implemented entries appear in the expected relative order.
5. Only then extend Gate 1 to the next simple item batch.
