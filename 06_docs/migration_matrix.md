# Thaumcraft 6 to NeoForge 1.21.1 Migration Matrix

This document maps the current Thaumcraft 6 Forge 1.12.2 reference code to a staged NeoForge 1.21.1 porting plan.

Source baseline:

- Legacy source reference: `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master`
- Original jar reference: `01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar`
- API reference: `04_api_reference/thaumcraft-api-master`
- NeoForge target project: `05_neoforge_port`
- Subsystem inventory: `06_docs/subsystem_inventory.md`
- Porting order: `06_docs/porting_order.md`
- Visual creative tab reference: `07_Test_Instance_and_Comparisons/02_Thaumcraft 1.12.2 Inventory Screenshots`

Current target:

- Minecraft: `1.21.1`
- NeoForge: `21.1.228`
- Java: `21`
- Mod id: `thaumcraft`
- Current project state: Gate 0 bootstrap exists and local build passed.

## How to use this matrix

This is not a direct copy checklist. Legacy Thaumcraft 6 code should be treated as a behavioral reference. The NeoForge port should rebuild registration, data loading, storage, networking, menus, screens, worldgen, rendering, and resources through current NeoForge 1.21.1 APIs.

For each subsystem, this matrix answers:

- where the legacy system lives;
- what old Forge or Minecraft APIs it depends on;
- what the NeoForge target shape should be;
- how difficult the migration is;
- what must be finished first;
- what the first safe implementation step should be.

Because the coordinator has only basic programming knowledge, implementation tasks should stay small and testable. Do not ask Codex or another agent to port a whole subsystem at once. Ask for one small slice, check the diff, run build, then continue.

## Difficulty scale

| Difficulty | Meaning | Practical consequence |
|---|---|---|
| Low | Mostly data or simple registry work | Can be done early, still test after every change |
| Medium | API layer must be rewritten, gameplay logic mostly simple | Requires focused implementation and build testing |
| High | Several systems interact | Needs design notes before code |
| Very high | Stateful, networked, rendered, or data-driven system | Must be split into multiple gates |
| Critical | Direct port is unsafe or unrealistic | Rebuild as a new modern architecture using legacy behavior as reference |

## Status values

| Status | Meaning |
|---|---|
| Not started | No port implementation exists yet |
| Skeleton ready | Empty or minimal class/registry scaffold exists |
| Design required | More architecture is needed before code |
| Port later | Do not implement until dependencies are stable |
| Verify visually | Must be checked against the 1.12.2 reference instance or screenshots |

## Global migration rules

1. Do not copy whole legacy classes into the NeoForge project.
2. Preserve gameplay behavior before preserving class names.
3. Keep public-facing registry ids stable where practical, but do not preserve broken 1.12 metadata patterns internally.
4. Preserve creative tab order and visual order from the 1.12.2 reference screenshots.
5. Keep creative display order separate from registry declaration order.
6. Replace `OreDictionary` with tags and explicit data mappings.
7. Replace item metadata variants with separate items, data components, or explicit variant mapping.
8. Replace TileEntity systems with BlockEntity, BlockEntityType, EntityBlock, capabilities, menu sync, and payloads.
9. Replace SimpleNetworkWrapper/IMessage with NeoForge custom payloads.
10. Replace legacy GUI classes with Menu and Screen systems.
11. Replace old worldgen hooks with data-driven features, biome modifiers, and datagen where practical.
12. Every stage must compile before moving to the next stage.

## Gate overview

| Gate | Name | Goal | Must not include yet |
|---|---|---|---|
| Gate 0 | Bootstrap | Empty NeoForge mod builds and loads | Thaumcraft gameplay content |
| Gate 1 | Item identity | Safe simple items, creative tab order scaffold | Research, aura, GUI, networking |
| Gate 2 | Block identity | Simple blocks, block items, models, loot | BlockEntity machines |
| Gate 3 | Data layer | aspects, tags, data loaders, registries | Gameplay-heavy crafting logic |
| Gate 4 | Basic BlockEntities | storage, ticking, save/load | final GUI and full sync |
| Gate 5 | Capabilities | item, fluid, energy, essentia access patterns | large machine networks |
| Gate 6 | Recipes | vanilla-like, arcane, crucible, infusion serializers | final research UI |
| Gate 7 | Player progression | research, scanning, knowledge, warp | final Thaumonomicon UI polish |
| Gate 8 | Menus and Screens | modern GUI for machines and research | large visual overhaul |
| Gate 9 | Networking | custom payload categories and validation | uncontrolled client authority |
| Gate 10 | Entities and golems | entity types, AI, rendering, tasks | full logistics polish |
| Gate 11 | Worldgen | features, trees, biomes, structures | unsafe direct old generators |
| Gate 12 | Rendering and FX | BER, entity renderers, particles, overlays | raw GL copy-paste |
| Gate 13 | Integrations | accessories, JEI/REI style hooks, optional APIs | hard required missing mods |
| Gate 14 | Parity and polish | creative order, balance, compatibility, testing | new unrelated features |

## Main subsystem matrix

| # | Subsystem | Legacy source | Legacy APIs and risks | NeoForge 1.21.1 target | Difficulty | Dependencies | First safe step | Status |
|---:|---|---|---|---|---|---|---|---|
| 0 | Bootstrap project | `05_neoforge_port` | None, already modern scaffold | Keep clean project with `Thaumcraft`, `TCItems`, `TCBlocks`, `TCCreativeTabs`, `TCConfig` | Low | None | Preserve current buildable state and commit/tag it | Skeleton ready |
| 1 | Core mod initialization | `thaumcraft.Thaumcraft`, `Registrar`, `proxies.*` | `@Mod.EventHandler`, proxies, old lifecycle, `GameRegistry`, `MinecraftForge.EVENT_BUS` | Constructor-based mod bootstrap, mod event bus, `NeoForge.EVENT_BUS`, `DeferredRegister`, side-safe client init | High | Gate 0 | Replace concepts only, do not copy proxy code | Skeleton ready |
| 2 | Mod metadata | `mcmod.info`, `pack.mcmeta`, manifest | Old Forge metadata format | `META-INF/neoforge.mods.toml`, current `pack.mcmeta`, generated metadata task | Low | Gate 0 | Verify displayed mod name, version, authors, description | Skeleton ready |
| 3 | Access transformer audit | `META-INF/tc_at.cfg` | Old private/protected access hacks may not map to 1.21.1 | Avoid ATs where possible; add modern AT only for proven blockers | High | Gate 0 | Create `docs/access_transformer_audit.md` before using any AT | Design required |
| 4 | Creative tab and order | `CreativeTabThaumcraft`, `ConfigItems`, screenshots | Old tab order depends on registration, subitems, metadata, hidden items | `CreativeModeTab.builder()` plus explicit display order class | High | Gate 1 | Create `creative_tab_order_reference.md` from screenshots 1-10 | Not started |
| 5 | Simple item registry | `ConfigItems`, `ItemsTC`, `common.items.resources.*` | static `ItemsTC`, metadata variants, `OreDictionary`, old `Item` callbacks | `DeferredRegister.Items`, tags, data components, item models, lang | Medium | Gate 0, creative order rule | Port first 5-10 simple resource items only | Not started |
| 6 | Item metadata variants | nuggets, ingots, clusters, plates, curios, loot bags, focus items | 1.12 item damage/meta variants do not map cleanly | Decide per item: separate registry entries, data component, or single item with property | High | Gate 1 | Build variant decision table before coding variants | Design required |
| 7 | Caster and focus items | `common.items.casters.*`, API `casters.*` | NBT-heavy, custom behavior, packets, rendering | Data components, validated payloads, modern item use callbacks, staged focus API | Very high | Aspects, aura, networking, rendering | Add placeholder data model only after simple items | Port later |
| 8 | Armor, baubles, wearable items | `common.items.armor.*`, `common.items.baubles.*`, item interfaces | Baubles dependency, old equipment slots, armor/render hooks | Vanilla equipment first, optional modern accessory integration later | High | Items, integrations | Port non-accessory armor identity first | Port later |
| 9 | Item behavior marker API | `IGoggles`, `IRevealer`, `IVisDiscountGear`, `IWarpingGear`, `IScribeTools` | Old addon API, Baubles assumptions, client HUD hooks | Keep as internal interfaces first, expose public API only after stable | Medium | Items, capabilities, rendering | Recreate minimal marker interfaces without behavior | Design required |
| 10 | Basic blocks | `ConfigBlocks`, `BlocksTC`, `common.blocks.basic.*` | static `BlocksTC`, old blockstate/model schemas | `DeferredRegister.Blocks`, block items, generated blockstates/models, loot tables | Medium | Gate 0, item conventions | Port simple non-BE blocks first | Not started |
| 11 | Block metadata variants | crystals, stones, planks, world blocks, devices | old metadata/subtype behavior | modern `BlockState` or separate blocks | High | Basic blocks | Create block variant mapping table | Design required |
| 12 | Block items | old block item registration | hidden or ordered old block items | `registerSimpleBlockItem` or explicit `BlockItem`, creative order entry | Medium | Basic blocks, creative order | Add block item only when creative position is known | Not started |
| 13 | Aspects model | `api.aspects.*`, `ConfigAspects` | static maps, `OreDictionary`, metadata matching | modern aspect service, data-driven aspect assignment, tags, reload support | High | Items, blocks, tags | Port core aspect definitions and `AspectList`-like value object | Design required |
| 14 | Aspect assignment | `AspectHelper`, `AspectEventProxy`, `CommonInternals` | static mutable maps, item/meta/entity keys | JSON/datapack or generated data mapping to items, blocks, tags, entities | High | Aspects, item/block ids | Create data format and loader before content data | Design required |
| 15 | Essentia containers and transport API | `IAspectContainer`, `IAspectSource`, `IEssentiaTransport` | old TileEntity interfaces, side checks | BlockEntity capabilities or explicit service interfaces | Very high | BlockEntities, capabilities, aspects | Define modern essentia access interface, no network yet | Port later |
| 16 | Aura storage | `AuraHandler`, `AuraWorld`, `AuraChunk`, `AuraThread` | manual thread, static world maps, chunk lifecycle, packets | server-owned `SavedData` or chunk attachments, safe tick/update loop, payload sync later | Very high | Aspects, storage design | Prototype server-side aura data without visuals | Port later |
| 17 | Aura client sync and HUD | `PacketAuraToClient`, HUD render code | legacy packets, client-only rendering | custom payloads, client cache, HUD overlay | Very high | Aura storage, networking, rendering | Implement only after aura server API is stable | Port later |
| 18 | Research content model | `ResearchCategories`, `ResearchEntry`, `ResearchStage`, `ConfigResearch` | manual asset JSON loading, old recipe references | reloadable data loader or datapack format, validation, datagen | Very high | Items, blocks, aspects, recipes | Create model classes and load a tiny test category | Design required |
| 19 | Player knowledge | `PlayerKnowledge`, `IPlayerKnowledge` | Forge 1.12 capabilities, manual packets | player attachment/capability, explicit sync payloads, server authority | Very high | Research model, networking | Store one test research flag per player | Port later |
| 20 | Scanning system | `ScanningManager`, `ScanItem`, `ScanBlock`, `ScanEntity`, `ScanOreDictionary` | metadata and ore dictionary predicates | tags, item predicates, entity predicates, server validation | High | Aspects, research, tags | Port scan predicate model, no GUI | Port later |
| 21 | Thaumonomicon/research UI | `GuiResearchBrowser`, `GuiResearchPage`, plugins | old GUI, raw GL, research page renderer | modern `Screen`, custom widgets, client data cache | Critical | Research data, networking, rendering | Build mock screen only after data model is stable | Port later |
| 22 | Theorycrafting | `research/theorycraft/*`, `TileResearchTable`, GUI | random cards, player state, GUI, packets | server state plus modern Screen/Menu and sync payloads | Very high | Research, block entities, GUI | Port data model before UI | Port later |
| 23 | Vanilla-style recipes | `ConfigRecipes`, normal crafting entries | old Forge recipe registration | `data/thaumcraft/recipe`, datagen, tags | Medium | Items, blocks, tags | Generate simple material recipes only | Not started |
| 24 | Arcane crafting | `IArcaneRecipe`, `ShapedArcaneRecipe`, `TileArcaneWorkbench` | old `IRecipe`, `InventoryCrafting`, old container | custom recipe type/serializer, menu, BlockEntity, vis checks | Very high | Items, blocks, aspects, aura, research | Define recipe JSON/serializer first | Port later |
| 25 | Infusion crafting | `InfusionRecipe`, `TileInfusionMatrix`, `TilePedestal`, stabilizers | BlockEntity network, instability, packets, particles | custom recipe type, modern BE scanning, payload FX, stability service | Critical | Aspects, aura, recipes, BE, networking, particles | Write design document before code | Port later |
| 26 | Crucible/alchemy | `CrucibleRecipe`, `TileCrucible`, `TileThaumatorium`, smelters | BE ticking, aspects, fluid/block interaction, menus | custom recipe types, BE save/tick, capability-based essentia access | Critical | Aspects, BE, recipes, GUI, networking | Prototype crucible data model first | Port later |
| 27 | BlockEntity base classes | `TileThaumcraft`, `TileThaumcraftInventory` | `TileEntity`, `ITickable`, `NBTTagCompound`, `markDirty` | `BlockEntity`, `EntityBlock#getTicker`, `CompoundTag`, `setChanged` | Very high | Blocks | Create one simple non-inventory BE first | Not started |
| 28 | Inventory BlockEntities | `TileThaumcraftInventory`, machine tiles | `IInventory`, manual slot sync | `ItemStackHandler`, capability provider, menu slots, save/load | Very high | BE base, capabilities | Prototype one 1-slot test BE | Port later |
| 29 | Essentia machines | alembic, smelter, centrifuge, jars, tubes | essentia transport, old TileEntity sync, models | modern BE, side-aware essentia interface, payload/client render | Critical | Aspects, capabilities, BE, rendering | Design interface and storage first | Port later |
| 30 | Devices and machines | stabilizer, focal manipulator, golem builder, thaumatorium | BE, GUI, recipes, networking, rendering | staged BE plus menu/screen plus payloads | Very high | BE, GUI, recipes, networking | Implement only after simple BE and menus compile | Port later |
| 31 | Menus and machine GUI | `common.container.*`, `Gui*` classes | `Container`, `GuiContainer`, `IGuiHandler`, raw GL | `AbstractContainerMenu`, `MenuType`, `Screen`, data slots, payload actions | Very high | BlockEntities, networking | Port one simple inventory menu first | Port later |
| 32 | Research GUI | research browser/pages/plugins | custom complex GUI, GL, data rendering | modern custom Screen with client cache | Critical | Research data, networking, rendering | Do not start until research data is stable | Port later |
| 33 | Golem GUI and seal GUI | `common.golems.client.gui.*` | GUI, packets, filters, task state | Screen/Menu or pure Screen with validated payloads | Very high | Golems, networking | Port after golem data model | Port later |
| 34 | Networking core | `PacketHandler`, `EventHandlerNetwork` | `SimpleNetworkWrapper`, `IMessage`, side handlers | `CustomPacketPayload`, `StreamCodec`, `RegisterPayloadHandlersEvent`, `PayloadRegistrar` | Very high | Data models stable | Create packet category inventory before code | Design required |
| 35 | Tile sync packets | `PacketTileToClient`, `PacketTileToServer` | manual NBT sync, client-to-server trust risk | BE update tags for simple state, payloads for actions | Very high | BE, networking | Split passive sync from player actions | Port later |
| 36 | Player data packets | `playerdata.*` | knowledge/warp sync | player data payloads with server authority | Very high | Research, warp, networking | Port after player storage exists | Port later |
| 37 | FX packets | `network.fx.*`, `FXDispatcher` | visual-only network events | clientbound payloads or level events | High | Particles, rendering, networking | Build visual event list first | Port later |
| 38 | Entities | `ConfigEntities`, `common.entities.*` | `EntityRegistry`, old entity classes, damage sources | `EntityType`, attributes, synced data, modern damage types | High | Items, aspects, rendering | Port one projectile or simple entity first | Port later |
| 39 | Projectiles | `common.entities.projectile.*`, focus effects | legacy projectile classes, caster item ties | modern projectile entity types and payload/particle hooks | High | Caster, networking, rendering | Defer until focus system exists | Port later |
| 40 | Mobs and eldritch/taint markers | monster classes, `IEldritchMob`, `ITaintedMob` | old AI, spawn rules, damage, attributes | modern goals, attributes, damage types, biome modifiers | Very high | Entities, worldgen | Port marker interfaces before mobs | Port later |
| 41 | Golem entity system | `EntityThaumcraftGolem`, golem AI/tasks | old AI/pathfinding, NBT, GUI, networking | modern entity, goals, task manager, attachments/data components | Critical | Entities, items, networking, GUI | Separate golem property model from entity first | Port later |
| 42 | Golem parts and traits | `api.golems.parts.*`, `EnumGolemTrait`, `GolemProperties` | NBT-heavy, partly public API | data-driven parts/traits if possible | Very high | Golem data model | Decide public API status first | Port later |
| 43 | Seals and logistics | `api.golems.seals.*`, `common.golems.seals.*` | block/world task state, GUI, packets | saved data/attachments, validated commands, screen UI | Critical | Golem system, networking, GUI | Create conceptual design only | Port later |
| 44 | Worldgen features | `ThaumcraftWorldGenerator`, `WorldGen*` | `IWorldGenerator`, old chunk gen hooks | configured/placed features, biome modifiers, datagen | Very high | Blocks, tags | Port one ore or plant feature as test | Port later |
| 45 | Magical trees | greatwood/silverwood generators | custom tree code and old block placement | modern features or custom tree/structure generator | Very high | Blocks, worldgen | Preserve shape reference, rebuild generator | Port later |
| 46 | Biomes | `BiomeGenMagicalForest`, eerie, eldritch, `BiomeHandler` | old biome registration and decoration | modern biome data, biome modifiers, tags | Critical | Blocks, entities, worldgen | Defer until simple features are stable | Port later |
| 47 | Structures/mounds | `WorldGenMound`, objects | old direct generation | structure sets, template pools, custom features | Very high | Blocks, worldgen | Convert to design notes first | Port later |
| 48 | Rendering core | `ProxyTESR`, `ProxyEntities`, renderers | TESR, old entity renderers, raw GL | client event registration, BER, EntityRenderer, RenderType, PoseStack | Critical | Blocks/entities/BE stable | Do not port renderers before target objects exist | Port later |
| 49 | Item and block models | assets models/blockstates, `ProxyBlock` | old model schemas, custom render hooks | modern assets/datagen, item properties | High | Items/blocks | Start with simple generated models | Not started |
| 50 | Custom models and OBJ | `client.lib.obj.*`, CodeChicken helpers | custom loaders, old GL/math/render helpers | modern model loading or rewritten renderer | Critical | Rendering core | Audit which assets really need custom loaders | Port later |
| 51 | Particles and beams | `FXDispatcher`, `client.fx.*`, `network.fx.*` | old particle manager, GL, packets | particle types/providers, clientbound payloads, modern render | Very high | Networking, rendering | Implement after one visual payload path exists | Port later |
| 52 | Sounds | `SoundsTC`, `sounds.json`, sound assets | old registry events, AT for sound registration | `DeferredRegister<SoundEvent>`, current `sounds.json` schema | Medium | Gate 0 | Register a small subset only when used | Port later |
| 53 | Config values | `ModConfig`, `Config*` classes | Forge 1.12 `Configuration`, side-effect registration | `ModConfigSpec` for values, data files for content, no registration side effects | High | Gate 0 | Move only true config values, not registries | Skeleton ready |
| 54 | Assets and resources | `assets/thaumcraft/*` | old paths, old loot tables, old lang format, `mcmod.info` | modern `assets` and `data`, datagen, `en_us.json`, `recipe`, `loot_table`, tags | High | Items/blocks/data | Convert only assets required by current gate | Not started |
| 55 | Research assets | `assets/thaumcraft/research` | manually loaded assets, old schemas | datapack or reloadable custom data under `data/thaumcraft/...` | Very high | Research model | Design new research data location/schema | Port later |
| 56 | Loot tables | old `assets/thaumcraft/loot_tables` | old resource location and schema | `data/thaumcraft/loot_table/...` and global loot modifiers where needed | Medium | Items/blocks/entities | Convert block loot only during block gate | Not started |
| 57 | Tags and OreDictionary replacements | `OreDictionaryEntries`, ore dict usages | `OreDictionary`, strings like ingot/ore/dust | `c:` tags and mod tags | High | Items/blocks | Create tag naming policy before recipes | Design required |
| 58 | Commands/debug tools | server starting event, debug hooks if present | old command registration | Brigadier command registration | Medium | Data systems | Add debug commands only for development gates | Port later |
| 59 | Integrations: Baubles/accessories | Baubles dependency and item classes | hard dependency on Baubles 1.12 | optional modern accessory mod integration, or vanilla equipment fallback | High | Items, marker interfaces | Do not block Gate 1 with accessory integration | Port later |
| 60 | Integrations: JEI/recipe display | recipe/research display assumptions | old APIs or no current equivalent | optional integration after recipes are stable | Medium | Recipes, research | Defer until core recipes work | Port later |
| 61 | Bundled helper libraries | CodeChicken-style helpers, GLE, starlite | old GL/math/render code | reference only unless proven needed | High | Rendering audit | Do not copy wholesale | Port later |
| 62 | Public API compatibility | `04_api_reference`, `thaumcraft.api.*` | API and implementation mixed in legacy source | rebuild minimal API after internal systems stabilize | High | Most core systems | Keep API packages but expose only stable contracts | Design required |
| 63 | Build and testing | Gradle wrapper, local build log | build can hide runtime/client-server issues | build, runClient, runServer, datagen, visual checks | Medium | Every gate | Run build after every small change | Skeleton ready |

## API migration matrix

| Legacy API or pattern | Where it appears | NeoForge target | Risk | Notes |
|---|---|---|---|---|
| `@Mod.EventHandler` lifecycle | `Thaumcraft` | constructor plus mod event bus listeners | High | Do not recreate preInit/init/postInit structure directly |
| `@SidedProxy` | `proxies.*` | explicit common/client classes and `Dist`-safe registration | High | Client-only imports must not leak into common code |
| `GameRegistry.registerTileEntity` | tile registration | `DeferredRegister<BlockEntityType<?>>` | High | Ticker belongs on block implementing `EntityBlock` |
| `GameRegistry.registerWorldGenerator` | worldgen | configured/placed features and biome modifiers | Very high | Direct port is unsafe |
| `RegistryEvent.Register<T>` | config/registration | `DeferredRegister` first, `RegisterEvent` only when needed | Medium | Keep registration out of config classes |
| static `ItemsTC`/`BlocksTC` assignments | API/config classes | `Supplier<T>` or `DeferredHolder` in registry classes | Medium | API compatibility can wrap target holders later |
| `OreDictionary` | aspects, recipes, materials | tags, mostly `c:` common tags | High | Needs naming policy before recipes |
| item metadata/damage variants | many resource/curio/item classes | separate items, data components, or explicit variant service | High | Must preserve creative tab visible order |
| `NBTTagCompound` item state | casters, foci, golems, bags | data components for ItemStack data | High | Raw NBT can exist internally, but should not be the main public pattern |
| `TileEntity` | all `common.tiles.*` | `BlockEntity` | Very high | Rewrite save/load/tick/sync |
| `ITickable` | machines/devices | `EntityBlock#getTicker` plus server-side tick methods | High | Audit tick cost |
| `IInventory` | tiles, golems, containers | `ItemStackHandler`, capabilities, menus | High | Expose via registered capability providers |
| old fluid/essentia transport | essentia tiles | custom essentia capability/service | Very high | Design before code |
| `SimpleNetworkWrapper` | `PacketHandler` | custom payloads with `StreamCodec` | Very high | Build packet inventory first |
| `IMessage`/`IMessageHandler` | network packet classes | `record implements CustomPacketPayload` and handler | Very high | Validate all C2S payloads server-side |
| `GuiScreen` | client GUI | `Screen` | High | Research browser is a major rewrite |
| `GuiContainer` | machine GUI | `AbstractContainerScreen` | High | Requires `AbstractContainerMenu` |
| `Container` | `common.container.*` | `AbstractContainerMenu` | High | Use data slots and payloads as needed |
| `IGuiHandler` | `ProxyGUI` | `MenuProvider`, `serverPlayer.openMenu` | High | Do not use legacy openGui pattern |
| `TileEntitySpecialRenderer` | `client.renderers.tile.*` | `BlockEntityRenderer` | Very high | Rewrite render code |
| raw `GlStateManager`/OpenGL | GUI/render/fx | modern pose/render APIs | Critical | Do not copy raw GL blindly |
| `EntityRegistry` | `ConfigEntities` | `DeferredRegister<EntityType<?>>` | High | Attributes/renderers/spawn are separate tasks |
| old damage sources | API damage source classes | data-driven damage types and modern `DamageSource` access | Medium | Needs verification per entity/item |
| old biome registration | worldgen/biomes | modern biome data and modifiers | Critical | Defer until simple worldgen works |
| old loot table path/schema | assets resources | `data/thaumcraft/loot_table/...` | Medium | Convert only when relevant |
| old recipe registration | `ConfigRecipes` | `data/thaumcraft/recipe/...`, custom serializers | High | Custom crafting needs serializer design |
| `mcmod.info` | resources | `META-INF/neoforge.mods.toml` | Low | Gate 0 already has template |
| Forge 1.12 `Configuration` | `ModConfig`, `Config*` | `ModConfigSpec` for true configs | Medium | Do not use config class as registry bootstrap |
| Baubles API | baubles and wearable items | optional accessory integration or vanilla fallback | High | Decide later, not Gate 1 |
| access transformer entries | `tc_at.cfg` | avoid or re-audit | High | Only add modern AT for proven blocker |

## Creative tab preservation matrix

This project has an extra hard requirement: creative inventory order and visual appearance should match Thaumcraft 6 on Minecraft 1.12.2 as closely as possible.

| Area | Legacy source | Target approach | Risk | Required action |
|---|---|---|---|---|
| Main Thaumcraft creative tab | `CreativeTabThaumcraft`, screenshots 1-10 | explicit `displayItems` order | High | Create `TCCreativeTabOrder` and never sort alphabetically |
| Item order | `ConfigItems`, `getSubItems`, screenshots | hand-authored order list | High | Build `creative_tab_order_reference.md` before mass item port |
| Metadata variants | old subitems | separate visible entries in same order | High | Map every variant to target registry id or component variant |
| Hidden/internal items | `setCreativeTab(null)` or no subitem | do not display unless reference shows it | Medium | Mark hidden entries in order reference |
| Blocks in tab | `ConfigBlocks` plus screenshots | block items inserted in reference position | High | Do not rely on registry declaration order |
| Placeholder items | not in 1.12.2 | keep out of main tab or put in dev-only tab | Medium | Avoid breaking parity screenshots |

Initial creative tab implementation should add only items that have been mapped. If an item exists in code but the exact 1.12.2 visual position is unknown, keep it out of the final creative tab until checked.

## First safe implementation candidates

These are safer first targets because they are identity/data-heavy and do not require aura, research, GUI, networking, or complex rendering.

| Candidate | Why it is safe | Required checks |
|---|---|---|
| Simple resource item | basic registry, lang, model | creative order, item texture, build |
| Simple ingot/nugget/plate item | likely no complex behavior | metadata variant decision, tags, creative order |
| Simple non-ticking block | block registry, block item, blockstate/model, loot | texture path, creative order, build |
| Sound event declaration | registry and `sounds.json` only | only when used by a ported object |
| Basic tag set | supports recipes and aspects later | tag namespace policy |

Do not start with casters, foci, research, infusion, aura, golems, or worldgen. Those systems depend on too many unstable layers.

## Implementation prompt template for future Codex use

Use this style when Codex is available again:

```text
Work only inside 05_neoforge_port.
Use 02_existing_decompiled_repo only as behavioral reference.
Do not copy whole legacy classes.
Do not change unrelated files.
Before coding, show a short plan.
After coding, show changed files.
Run .\gradlew.bat build --no-daemon or explain why it was not run.
Preserve Thaumcraft 6 1.12.2 creative tab order according to 07_Test_Instance_and_Comparisons/02_Thaumcraft 1.12.2 Inventory Screenshots.
If an item order position is unknown, do not add it to the final creative tab yet.
```

## Near-term documentation tasks

| Task | Output | Why it matters |
|---|---|---|
| Build creative order reference | `06_docs/creative_tab_order_reference.md` | Prevents accidental order drift |
| Build item variant map | `06_docs/item_variant_mapping.md` | Old metadata variants need explicit decisions |
| Build registry id policy | `06_docs/registry_id_policy.md` | Avoids random ids and future breaking changes |
| Build tag policy | `06_docs/tag_policy.md` | Replaces OreDictionary consistently |
| Build access transformer audit | `06_docs/access_transformer_audit.md` | Prevents blindly recreating old hacks |
| Build networking inventory | `06_docs/network_payload_inventory.md` | Needed before any payload implementation |
| Build BE inventory | `06_docs/blockentity_inventory.md` | Needed before machines/devices |

## Immediate next gate recommendation

Next recommended work item:

1. Create `creative_tab_order_reference.md` from screenshots 1-10.
2. Create `item_variant_mapping.md` for the first item families.
3. Add only a very small item batch to the NeoForge project.
4. Keep the batch visually aligned with the 1.12.2 creative tab reference.
5. Run `build` after the batch.

Do not implement all items at once. The first code gate should prove the registry, generated resources, creative tab order, and build pipeline on a small controlled sample.
