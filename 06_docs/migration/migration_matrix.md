# Thaumcraft 6 to NeoForge 1.21.1 Migration Matrix

This document maps the Thaumcraft 6 Forge 1.12.2 codebase to the staged NeoForge 1.21.1 port.

For current implementation status, always read `06_docs/current_port_status.md` together with this matrix. For the active task queue, read `06_docs/CURRENT_TASK.md`. This matrix defines scope, sequencing, and risk. The status document records what the repository currently contains.

## Source baseline

| Area | Location |
|---|---|
| Legacy source reference | `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master` |
| Original jar reference | `01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar` |
| API reference | `04_api_reference/thaumcraft-api-master` |
| NeoForge target project | `05_neoforge_port` |
| Main migration guide | `06_docs/migration/NeoForge_legacy_migration_guide.md` |
| Current task queue | `06_docs/CURRENT_TASK.md` |
| Current implementation status | `06_docs/current_port_status.md` |
| Documentation index | `06_docs/documentation_index.md` |
| Subsystem inventory | `06_docs/migration/subsystem_inventory.md` |
| Porting order | `06_docs/migration/porting_order.md` |
| Creative tab reference | `06_docs/resources/creative_tab_order_reference.md` |
| Aspect assignment tag audit | `06_docs/data/aspects/aspect_assignment_tag_audit.md` |
| Aspect generateTags audit | `06_docs/data/aspects/aspect_generate_tags_audit.md` |
| Aspect assignment data format | `06_docs/data/aspects/aspect_assignment_data_format.md` |
| Aspect generated cache design | `06_docs/data/aspects/aspect_generated_cache_design.md` |
| Vanilla aspect policy | `06_docs/data/aspects/vanilla_aspect_policy.md` |
| Vanilla 1.21 aspect assignment audit | `06_docs/data/aspects/vanilla_1_21_aspect_assignments.md` |
| Vanilla post-1.12 aspect rationale table | `06_docs/data/aspects/vanilla_post_1_12_aspect_rationale.md` |
| Aspect legacy gap audit | `06_docs/data/aspects/aspect_legacy_gap_audit.md` |
| Aspect runtime logic audit | `06_docs/data/aspects/aspect_legacy_runtime_logic_audit.md` |
| Aspect parity comparison harness | `06_docs/data/aspects/aspect_parity_comparison_harness.md`; mapped runtime artifacts under `07_Test_Instance_and_Comparisons/aspect_parity` |
| Scan parity comparison harness | `06_docs/research/scanning_parity_validation.md`; mapped runtime artifacts under `07_Test_Instance_and_Comparisons/scan_parity` |
| Research table design | `06_docs/research/research_table_scribing_tools_design.md` |
| Research progression parity audit | `06_docs/research/research_progression_parity_audit.md`; runtime/source artifacts under `07_Test_Instance_and_Comparisons/research_data_parity` |
| Arcane crafting design | `06_docs/crafting/arcane_crafting_design.md`; runtime audit under `07_Test_Instance_and_Comparisons/arcane_crafting` |
| Visual reference | `07_Test_Instance_and_Comparisons/02_Thaumcraft 1.12.2 Inventory Screenshots` |

## Target baseline

| Component | Target |
|---|---|
| Minecraft | `1.21.1` |
| NeoForge | `21.1.228` |
| Java | `21` |
| Mod id | `thaumcraft` |
| Project state | See "Current Project State Snapshot" below. |

## Current Project State Snapshot

This snapshot is extracted from the old long `Project state` table cell so the target baseline remains readable. Detailed status still lives in `06_docs/current_port_status.md`.

- Gate 0 is complete enough to continue: the NeoForge module, mod id, Java 21 setup, and client/server bootstrap exist.
- Gate 1 and early Gate 2 identity work are in progress: active resources are runtime-audited, legacy asset corpus is imported as reference/base material, and public ids for candles, tubes, and smelter auxiliary placeholders have been realigned to legacy names.
- Gate 3 is in progress: aspects have parity validation, OreDictionary-to-tag audit coverage, runtime parity dumps, data-driven assignments, stack-sensitive bonuses, component-aware potion/enchanted-book behavior, spawn-egg/firework/infested parity exclusions, and current generated cache support for standard plus current exact arcane outputs.
- Current comparable parity baseline: item aspects `1139/1139`, item-level scans `1139/1139`, and entity scans `83/85` parity-ok plus `2` documented expected modern-policy rows.
- Gate 4 is in progress: research table BlockEntity/menu/screen/action-result boundary, GUI-ready knowledge sync, early theorycraft aids/cards, minimal warp storage bridge, and legacy-model-backed table renderer exist; full theorycraft gameplay is still incomplete.
- Gate 6 is in progress: arcane recipe type/serializers, selected exact arcane fixtures, tag bridges, normal plate fixtures, Arcane Workbench server crafting/menu feedback, Workbench Charger aura behavior, server behavior audit coverage, and current arcane recipe-derived aspect generation exist.
- The `thaumcraft:crucible` serializer/page-data boundary exists for HEDGE_ALCHEMY, METAL_PURIFICATION, Alumentum, Nitor, Brass/Thaumium Ingots, and vis crystals. Dynamic legacy HEDGE_ALCHEMY costs are explicit data and the current crucible recipe data audit reports `77/77` valid files. Current in-world crucible slices now cover server-owned water/heat/aspect state, manual item insertion, item-entity collision absorption, hot living contact damage, research-gated recipe lookup, aspect-cost consumption, result ejection and spill pollution into aura flux; flux rifts, taint spread, item pulling radius, special alchemy, essentia integration and particles remain deferred.
- Brain-in-a-Jar, Crimson portal, Basic Eldritch, Dragon Egg theorycraft, remaining custom entities, full essentia/infusion behavior, and broad render/worldgen systems remain deferred/reference-only until their dependencies are designed.

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
| Gate 2 | Block identity | Simple blocks, block items, models and loot | Started early; active blockstates/models/loot/lang covered; active candle and tube public ids now match legacy registry ids; imported legacy assets remain reference | BlockEntity machines |
| Gate 3 | Data layer | Aspects, tags, data loaders and registries | Started with exact legacy aspect definitions, lists, pure helper logic, data-driven current registered-id assignments, conservative vanilla seeds, tag compatibility, documented manual 1.21 vanilla assignments, generated aspects for `minecraft:*`/`thaumcraft:*` standard crafting outputs and current exact arcane recipe outputs, stack-sensitive bonus rules, component-aware potion/enchanted-book behavior, spawn-egg/firework/infested no-aspect parity, bootstrap/reload validation, runtime logic audit, and mapped dump-based parity harness | Gameplay-heavy custom crafting logic |
| Gate 4 | Basic BlockEntities | Storage, ticking and save/load | Started narrowly with research table slots, menu opening data, theory data save/load, first vanilla and safe-Eldritch aid scan integration, first basic block aids for crucible/arcane workbench/infusion matrix/Focal Manipulator/golem press, table-inventory card support, update packet/tag sync, and legacy-model-backed table-top renderer consumer | Full theory data/card/aids polish |
| Gate 5 | Capabilities | Item, fluid, energy and essentia access patterns | Not started | Large machine networks |
| Gate 6 | Recipes | Vanilla-like, arcane, crucible and infusion serializers | Basic vanilla crafting recipes started for generated-aspect fixtures; first custom arcane recipe type/serializers, exact `thaumometer`, `vis_resonator`, `workbenchcharger`, `goggles`, `mechanism_simple`, `mechanism_complex`, `wand_workbench`/Focal Manipulator, `caster_basic`, `enchantedfabric`, `mirrorglass`, `filter`, `morphicresonator`, `essentiasmelter`, and `infusionmatrix` fixtures, exact `plateIron`, `plateBrass`, `plateThaumium`, `ingotGold`, `ingotBrass`, `ingotThaumium`, `stickWood`, and `nitor` tag bridges, exact normal `brassplate`/`thaumiumplate` fixtures, first Arcane Workbench server crafting path, Workbench Charger 3 x 3 aura behavior, first player vis-discount path, missing-vis ghost output, server-owned workbench base/discounted cost/crystal feedback, current arcane recipe-derived aspect generation, legacy-id cleanup for candle/tube/smelter-auxiliary recipe outputs, `thaumcraft:crucible` serializer/page-snapshot data for HEDGE_ALCHEMY, METAL_PURIFICATION, Alumentum, Nitor, Brass/Thaumium Ingots and vis crystals, and the current manual/collision/spill in-world crucible recipe-consumption path started | Remaining arcane recipes, filter/resonator behavior, full equipment/Curios discount integration, crucible flux rifts/taint/special alchemy/automation/particles, crucible-derived aspect generation, and infusion serializers |
| Gate 7 | Player progression | Research, scanning, knowledge and warp | Research data/progression and the permanent recipe/page catalog are parity-closed; server-side knowledge, scanning, minimal warp storage, server-authoritative revision-gated Thaumonomicon protocol, item/open flow, browser/entry screens, first vanilla crafting page, and first arcane page snapshot exist. Full warp effects/events/client sync, search/final visual parity and remaining custom recipe pages remain incomplete | Final Thaumonomicon UI polish |
| Gate 8 | Menus and Screens | Modern GUI for machines and research | Started with the research table inventory/menu screen, first Arcane Workbench functional screen/menu including server-owned cost/aura/crystal feedback and missing-vis ghost output, and the first real server-authoritative Thaumonomicon browser/entry/crafting/arcane-page screens. Search, exact visual parity, final Arcane Workbench GUI polish, drilldown/history and remaining custom recipe-page renderers remain incomplete | Large visual overhaul |
| Gate 9 | Networking | Custom payload categories and validation | Started with aura sync, GUI-ready knowledge sync, research-table action/sync/result payloads, and server-authoritative Thaumonomicon index/entry/action payloads with revision/stale-action validation; gameplay mutation remains server-validated | Uncontrolled client authority |
| Gate 10 | Entities and golems | Entity types, AI, rendering and tasks | Not started | Full logistics polish |
| Gate 11 | Worldgen | Features, trees, biomes and structures | Started early only for sapling-grown trees; not a biome/worldgen system | Unsafe direct old generators |
| Gate 12 | Rendering and FX | BERs, entity renderers, particles and overlays | Started early through legacy-style FX scaffolding, imported visual assets, Thaumometer overlays, and a narrow research table BER | Raw GL copy-paste |
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
| 5 | Simple item registry | `DeferredRegister.Items`, item models, lang, textures, tags | Medium | Keep active 1.21 `models/item`, `textures/item`, and `en_us.json` authoritative; port legacy entries only when their ids are registered | In progress; active resources covered; scribing tools now preserve legacy stack/durability and `IScribeTools` marker |
| 6 | Item metadata variants | Separate items, data components, or explicit variant mapping | High | Build variant decision table | Design required |
| 7 | Caster and focus items | Data components, validated payloads, modern item callbacks, staged focus API | Very high | Add placeholder data model only after simple items | Port later |
| 8 | Armor, baubles and wearables | Vanilla equipment first, optional accessory integration later | High | Port non-accessory armor identity first | Port later |
| 9 | Item behavior marker API | Internal interfaces first; public API after stable behavior | Medium | Recreate minimal marker interfaces without behavior | Design required |
| 10 | Basic blocks | `DeferredRegister.Blocks`, block items, blockstates, models, loot tables | Medium | Keep current 1.21 blockstates/models/loot authoritative over imported legacy variants | In progress; active resources covered; wood/stone tables and research table are registered with modern resources |
| 11 | Block metadata variants | `BlockState` or separate blocks | High | Create block variant mapping table | Design required |
| 12 | Aspects model | Modern aspect service, data-driven assignments, tags, reload support | High | Preserve exact legacy core semantics before adding gameplay consumers | Started: `Aspect`, `AspectList`, pure `AspectHelper` logic, cached `generateTags` lookup, item/block assignment data, and vanilla entity aspect lookup are implemented and parity-validated; deeper legacy runtime behavior is documented in `aspect_legacy_runtime_logic_audit.md` |
| 13 | Aspect assignment | JSON/datapack or generated mappings to items, blocks, tags, entities | High | Exact direct/tag/manual/runtime-parity assignments stay authoritative; generated values are reload-owned and must not be heuristic | Started: 687 exact assignments, 46 audited tag assignments, and 32 complex exact assignments are loaded from `aspect_assignments` data; the registered Thaumonomicon plus current exact mechanism/plate fixtures now have exporter-derived legacy runtime aspects, and `rare_earth` has its exact legacy seed for Morphic Resonator ingredients; generated cache produced 634 current `minecraft:*`/`thaumcraft:*` standard/arcane crafting entries after current research bridge, normal plate recipes, and current exact arcane recipes; crucible page-data recipes are intentionally excluded from generated aspect fallback; assignable vanilla item-id coverage is `1230/1230`; mapped runtime comparison currently has `0` real `PORT_GAP_*` buckets for comparable stacks |
| 14 | Essentia transport API | BlockEntity capabilities or explicit service interfaces | Very high | Define modern essentia access interface | Port later |
| 15 | Aura storage | Server-owned `SavedData` or chunk attachments, safe tick/update loop | Very high | Prototype server-side aura data without visuals | Started: `aura_design.md`, `AuraHelper`, per-level `SavedData`, chunk `base/vis/flux`, automatic chunk initialization with legacy base formula, main-thread legacy-like update loop, and permission-level-2 debug commands exist; 1.21 biome categories are mapped legacy-like because `BiomeDictionary` is gone; sync, FX, rifts, research-aware preservation, and gameplay consumers remain blocked. |
| 16 | Research model | Reloadable data loader/datapack format, validation, datagen | Very high | Create model classes and load a tiny test category | Progression/data core and permanent page-catalog boundary are parity-closed: `148` entries, `271` stages, `16` addenda, seven categories, `253` recipe/page occurrences, `203` direct references, and `325` catalog entries including group members all compare without differences. Checked progression preserves exact legacy ordering. Requirements remain `0` unresolved with `16` subsystem bridge warnings. Final UI, custom recipe-page renderers, full warp system/events, and remaining dependency-heavy theorycraft content are not implemented. |
| 17 | Player knowledge | Player attachment/capability, explicit sync payloads, server authority | Very high | Store one test research flag per player | Started: persistent player storage and GUI-ready sync cover observation/theory raw knowledge, known research keys, stages, and flags. Checked progression plans item/knowledge consumption before mutation and final sync. Minimal warp storage receives exact legacy research warp split. Server-authoritative Thaumonomicon index/entry views filter player-visible state. Full warp effects/events/client sync, cancellable research/knowledge events, and final UI consumers remain blocked. |
| 18 | Scanning system | Tags, item predicates, entity predicates, server validation | High | Port scan predicate model, no GUI | Started: debug `/scan held` and `/scan looking` use aspect lookup plus a legacy-shaped `IScanThing` registry; modern `ScanItem`, `ScanBlock`, `ScanEntity`, `ScanOreDictionary`, `ScanAspect`, generic scan, tag scan predicate, reloadable `data/thaumcraft/scannables` definitions, dynamic mob-effect/enchantment scans, vanilla entity aspects, and gated sky scan exist; bundled `legacy_core.json` currently loads 33 valid legacy definitions, base reload has 123 active predicates, and dynamic server predicates bring the current count to 205; item/potion/enchantment scan diff is `1139/1139` parity-ok with `0` scan or aspect differences; entity/state-variant scan diff has `83/85` parity-ok rows, `2` expected modern entity policy rows, and `0` actionable scan/aspect gaps; dropped `ItemEntity` targets now follow the legacy item-stack scan path; Thaumometer right-click mutates through `TCResearchManager.progressResearch`, blank-key scans suppress status messages, known targets no longer retrigger success, `ScanAspect` and `TCScanGeneric` grant legacy raw observation knowledge, and client visuals use legacy ranges/zone highlight/wild rays with living-mob aspect overlay separated from sparkle gating. Celestial-note side effects, non-observation rewards, and aura HUD remain blocked |
| 19 | Thaumonomicon UI | Modern `Screen`, custom widgets, client data cache | Critical | Build real browser/entry screens only over authoritative view models | First real item/open/browser/entry flow and vanilla crafting, arcane and crucible page renderers are implemented over server-authoritative visible index, entry/stage/requirement/bookmark, and recipe snapshot views. Exact legacy actions, cache/open separation, revision freshness, stale-action rejection, and snapshot boundaries pass `27/27` audit checks. Search/final visual parity, drilldown/history, and remaining infusion/blueprint/fake/special recipe pages remain. |
| 20 | Arcane crafting | Custom recipe type/serializer, menu, BlockEntity, vis checks | Very high | Define recipe JSON/serializer first | Started: recipe type/serializers, exact `thaumometer`, `vis_resonator`, `workbenchcharger`, `goggles`, `mechanism_simple`, `mechanism_complex`, `wand_workbench`/Focal Manipulator, `caster_basic`, `enchantedfabric`, `mirrorglass`, `filter`, `morphicresonator`, `essentiasmelter`, and `infusionmatrix`, exact `plateIron`, `plateBrass`, `plateThaumium`, `ingotGold`, `ingotBrass`, `ingotThaumium`, `stickWood`, and `nitor` bridges, exact normal `brassplate`/`thaumiumplate` recipes, server-authored Thaumonomicon snapshots, first Arcane Workbench BlockEntity/menu/no-charger current-chunk aura drain path, Workbench Charger 3 x 3 aura query/drain, first player vis-discount path, server-owned base/discounted cost/aura/crystal feedback, missing-vis ghost output, current arcane recipe-derived aspects, a 105-check arcane recipe audit over 14 exact arcane recipes, and a 23-check server behavior audit for matrix/crystal/vis/fallback/consumption/discount/Charger/ghost/menu-feedback behavior exist. Remaining recipes, filter/resonator behavior, full equipment/Curios discount integration, and final visual parity remain blocked. Crucible recipe/page data is tracked separately under subsystem 22. |
| 21 | Infusion crafting | Custom recipe type, modern BE scanning, payload FX, stability service | Critical | Write design document before code | Port later |
| 22 | Crucible and alchemy | Custom recipe types, BE save/tick, capability-based essentia access | Critical | Keep manual/collision/spill in-world behavior separate from full alchemy side effects and automation | Started narrowly: `thaumcraft:crucible` recipe type/serializer, explicit aspect costs, catalyst variants, server Thaumonomicon page snapshots, codec/payload wiring, HEDGE_ALCHEMY, METAL_PURIFICATION, Alumentum, Nitor, Brass/Thaumium Ingots and vis crystal recipe data are implemented. Current in-world slices add `TCCrucibleBlock`, `TCCrucibleBlockEntity`, and `TCCrucibleRecipeMatcher` for water/heat/aspect state, manual top-side item insertion, legacy-style item-entity collision absorption, hot living contact damage, existing item-aspect dissolution, research-gated highest-cost recipe lookup, aspect/water consumption, result ejection, special-result reabsorption protection, comparator output, periodic/overflow `spillRandom`, and `spillRemnants` aura pollution. Flux rifts, taint spread, liquid death/special alchemy behavior, item pulling radius, essentia/alembic/jar/tube/Thaumatorium integration, crucible-derived `generateTags`, client FX, and final visuals remain blocked. |
| 23 | BlockEntity base classes | `BlockEntity`, `EntityBlock#getTicker`, `CompoundTag`, `setChanged` | Very high | Create one simple non-inventory BE first | Started narrowly: `TCResearchTableBlockEntity` saves/drops scribing tools and paper slots, owns theory data under the legacy `note` tag, provides the server menu container, and syncs update packets/tags for the table-top renderer. `TCArcaneWorkbenchBlockEntity` stores the legacy 9 matrix + 6 crystal slot shape, saves/loads/drops contents, opens a server menu, and delegates no-charger current-chunk plus Charger 3 x 3 vis checks/drain to the aura core. `TCCrucibleBlockEntity` stores water/heat/aspect state, ticks heat and spill counter from legacy-compatible sources, saves/loads NBT, syncs updates, owns manual item insertion plus item-entity absorption, pollutes aura on spills, and marks ejected recipe results to avoid immediate reabsorption like legacy `EntitySpecialItem`. Broad machine ticking and capability networks remain deferred. |
| 24 | Menus and machine GUI | `AbstractContainerMenu`, `MenuType`, `Screen`, data slots, payload actions | Very high | Port one simple inventory menu first | Started narrowly: `TCResearchTableMenu` and `TCResearchTableScreen` open the research table with legacy slot coordinates, minimal theory controls/payload actions, first vanilla/basic aid selection, action-result status, clickable legacy paper/gilded-paper card choices, and an atomic minimal select+commit request. `TCArcaneWorkbenchMenu` exposes legacy result/matrix/crystal/player slot coordinates, validates primal crystal slots, resolves server-owned arcane/vanilla fallback output, syncs base/discounted cost and aura/crystal feedback, exposes missing-vis ghost output without allowing pickup, and consumes output atomically; `TCArcaneWorkbenchScreen` is a functional legacy-background first pass with discount text and ghost output. Page-flip/hover/zoom animations, full card UI, and final Arcane Workbench visual parity are not complete. |
| 25 | Networking | `CustomPacketPayload`, `StreamCodec`, `PayloadRegistrar`, validated handlers | Very high | Define packet categories and policies first | Started: aura sync, GUI-ready knowledge sync, research-table serverbound action/clientbound sync/result payloads, and Thaumonomicon index/entry/action payloads exist. Thaumonomicon actions validate visibility and stage requirements server-side; explicit open intent is separated from ordinary index refresh. Each new gameplay subsystem still needs a focused packet inventory and validation policy |
| 26 | Entities and golems | `EntityType`, `SynchedEntityData`, goals, attributes, renderers | Very high | Port one non-golem test entity later | Port later |
| 27 | Worldgen | Datapack/datagen features, biome modifiers, structures | Critical | Convert sapling tree behavior into a modern design before expanding to biome/world placement | Early sapling-only work |
| 28 | Rendering and FX | BERs, entity renderers, particles, overlays, `PoseStack` | Critical | Validate imported FX/textures and isolate old GL/shader assumptions before expanding | Early FX work; `rendering_model_pipeline_audit.md` records OBJ/MTL, BER and BEWLR migration rules; a narrow research table BER now uses legacy `ModelResearchTable` part geometry/UVs for the scroll/tube/ribbon and inkwell, plus the legacy quill texture/transform from synced slots |
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

Active registered content cleanup note: `amber`, `quicksilver`, `fabric`, `scribing_tools`, `table_wood`, `table_stone`, `research_table`, `iron_plate`, `brass_plate`, `thaumium_plate`, `void_plate`, `mechanism_simple`, `mechanism_complex`, `thaumometer`, and `vis_resonator` models now point at modern `thaumcraft:item/*` or `thaumcraft:block/*` textures, with active PNGs copied into `textures/item` or `textures/block`. Do not mass-convert unregistered legacy `thaumcraft:items/*` or `thaumcraft:blocks/*` references until those ids are intentionally ported.

## Legacy API replacement matrix

| Legacy API / pattern | NeoForge 1.21.1 target | Notes |
|---|---|---|
| `GameRegistry.register` | `DeferredRegister` | Registry declarations only, no creative ordering logic. |
| `CreativeTabs` / `getSubItems` | `CreativeModeTab.builder()` and explicit output order | Final order must be screenshot-reviewed. |
| Item metadata variants | Separate items or data components | Decide per variant group before public builds. |
| `OreDictionary` | Tags and explicit data mappings | Common tags where compatible. |
| `ScanningManager` audit/debug checks | Reload-safe server command dumps | `/thaumcraft scan audit_items`, `/thaumcraft scan audit_entities`, `-PtcScanDump=true`, and `-PtcScanEntityDump=true` write deterministic item/potion/enchantment/entity scan data without mutating knowledge; current item and entity comparers report `0` actionable scan logic gaps. |
| `TileEntity` | `BlockEntity` | Ticker in block class through `EntityBlock#getTicker`; first research table BlockEntity/menu boundary exists, but legacy `TileResearchTable` theory data is not copied. |
| `IInventory` | `ItemStackHandler`, capabilities, menus | Do not expose legacy inventory directly. |
| `IMessage` / `SimpleNetworkWrapper` | Custom payloads | Validate all client-to-server requests. |
| `GuiContainer` / `IGuiHandler` | `AbstractContainerMenu`, `Screen`, `openMenu` | Keep client-only code isolated. |
| Raw GL render code | Modern rendering abstractions | Do not copy GL code without redesign; see `rendering_model_pipeline_audit.md` before adapting OBJ-heavy blocks, BERs, BEWLRs or shaders. |
| Old world generators | Datapack/datagen features and biome modifiers | Design before code. |
| Baubles API | Optional modern accessory integration | Do not hard require until selected. |

## Immediate next work

This matrix is not the live task queue. For current priorities, read `06_docs/CURRENT_TASK.md`.

Current rule of thumb:

1. Keep build, dedicated server smoke, page catalog audits, protocol audits, and relevant parity exporters green after each batch.
2. Pick the next implementation slice by audited dependency family, not by copying whole legacy packages.
3. Keep bridge/placeholder behavior explicitly marked until the owning subsystem is implemented.
4. Do not expand in-world crucible, infusion, essentia transport, broad worldgen, broad rendering, or integrations without a focused design note and validation path.
