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
| Aspect assignment tag audit | `06_docs/aspect_assignment_tag_audit.md` |
| Aspect generateTags audit | `06_docs/aspect_generate_tags_audit.md` |
| Aspect assignment data format | `06_docs/aspect_assignment_data_format.md` |
| Aspect generated cache design | `06_docs/aspect_generated_cache_design.md` |
| Vanilla aspect policy | `06_docs/vanilla_aspect_policy.md` |
| Vanilla 1.21 aspect assignment audit | `06_docs/vanilla_1_21_aspect_assignments.md` |
| Vanilla post-1.12 aspect rationale table | `06_docs/vanilla_post_1_12_aspect_rationale.md` |
| Aspect legacy gap audit | `06_docs/aspect_legacy_gap_audit.md` |
| Aspect runtime logic audit | `06_docs/aspect_legacy_runtime_logic_audit.md` |
| Aspect parity comparison harness | `06_docs/aspect_parity_comparison_harness.md`; mapped runtime artifacts under `07_Test_Instance_and_Comparisons/aspect_parity` |
| Scan parity comparison harness | `06_docs/scanning_parity_validation.md`; mapped runtime artifacts under `07_Test_Instance_and_Comparisons/scan_parity` |
| Research table design | `06_docs/research_table_scribing_tools_design.md` |
| Visual reference | `07_Test_Instance_and_Comparisons/02_Thaumcraft 1.12.2 Inventory Screenshots` |

## Target baseline

| Component | Target |
|---|---|
| Minecraft | `1.21.1` |
| NeoForge | `21.1.228` |
| Java | `21` |
| Mod id | `thaumcraft` |
| Project state | Gate 0 complete enough to continue; Gate 1 and early Gate 2 identity work are in progress; active resources have been runtime-audited; original legacy asset corpus is imported as reference/base material; Gate 3 aspect core/API slice has started with parity validation, OreDictionary-to-tag audit, generateTags audit, detailed legacy runtime logic audit, mapped runtime parity dump harness, safe current common tag resources, exact legacy compatibility tags, conservative vanilla seeds, vanilla material tag bridges, ore-derived 1.21 raw materials, reload-safe data-driven exact/tag/manual assignments, generated cache for current `minecraft:*`/`thaumcraft:*` standard crafting outputs, legacy stack-sensitive bonus rules, component-aware potion/enchanted-book behavior, spawn-egg/firework/infested no-aspect parity, documented elder-guardian/zombie-villager living-mob policy corrections, audited tag-backed fallback, and server-data-load tag validation. Current comparable aspect parity is `1139/1139`; current comparable item-level scan parity is `1139/1139`; current comparable entity-level scan report has `83/85` parity-ok rows plus `2` expected modern entity policy rows. Gate 4 has a narrow research table BlockEntity/menu/screen/action-result boundary, GUI-ready player knowledge sync, the first vanilla theorycraft-aid family for bookshelves, enchanting tables and beacons, safe Eldritch aids for glyphed stone plus vanilla Nether/End portals, first basic block aids for crucible/arcane workbench/infusion matrix/wand workbench/golem press, first Artifice, Basic Auromancy, Basic Golemancy and safe Eldritch item/progression cards, `CardInfuse`, `CardAwareness`, `CardScripting`, and legacy-model-backed table-top renderer; full theorycraft gameplay is still incomplete. Brain-in-a-Jar, Crimson portal, Basic Eldritch and Dragon Egg theorycraft paths remain deferred/reference-only according to their legacy registration and missing subsystem dependencies. |

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
| Gate 3 | Data layer | Aspects, tags, data loaders and registries | Started with exact legacy aspect definitions, lists, pure helper logic, data-driven current registered-id assignments, conservative vanilla seeds, tag compatibility, documented manual 1.21 vanilla assignments, generated aspects for `minecraft:*`/`thaumcraft:*` standard crafting outputs, stack-sensitive bonus rules, component-aware potion/enchanted-book behavior, spawn-egg/firework/infested no-aspect parity, bootstrap/reload validation, runtime logic audit, and mapped dump-based parity harness | Gameplay-heavy custom crafting logic |
| Gate 4 | Basic BlockEntities | Storage, ticking and save/load | Started narrowly with research table slots, menu opening data, theory data save/load, first vanilla and safe-Eldritch aid scan integration, first basic block aids for crucible/arcane workbench/infusion matrix/wand workbench/golem press, table-inventory card support, update packet/tag sync, and legacy-model-backed table-top renderer consumer | Full theory data/card/aids polish |
| Gate 5 | Capabilities | Item, fluid, energy and essentia access patterns | Not started | Large machine networks |
| Gate 6 | Recipes | Vanilla-like, arcane, crucible and infusion serializers | Basic vanilla crafting recipes started for generated-aspect fixtures; custom serializers not started | Final research UI |
| Gate 7 | Player progression | Research, scanning, knowledge and warp | Started with server-side player knowledge storage/commands, minimal server-side warp storage/debug commands for current theorycraft side effects, reload-safe research data skeleton plus research-reference validation, reload-safe scan predicates, and runtime item/entity scan parity harnesses; full warp effects/events/client sync and full research progression are not started | Final Thaumonomicon UI polish |
| Gate 8 | Menus and Screens | Modern GUI for machines and research | Started narrowly with the research table inventory menu, background screen, minimal theory controls, first vanilla/basic aid selection, and legacy-asset paper-sheet card choices | Large visual overhaul |
| Gate 9 | Networking | Custom payload categories and validation | Started narrowly with aura sync, GUI-ready knowledge sync, and research-table action/sync/result payloads; every research-table mutation remains server-authoritative | Uncontrolled client authority |
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
| 13 | Aspect assignment | JSON/datapack or generated mappings to items, blocks, tags, entities | High | Exact direct/tag/manual/runtime-parity assignments stay authoritative; generated values are reload-owned and must not be heuristic | Started: 682 exact assignments, 46 audited tag assignments, and 32 complex exact assignments are loaded from `aspect_assignments` data; generated cache produced 496 current `minecraft:*`/`thaumcraft:*` standard crafting entries after current research bridge recipes; current registered theorycraft option items now include exact runtime parity values for `alumentum`, `salis_mundus`, `brain`, `nitor_yellow`, `thaumium_ingot`, and `brass_ingot`; assignable vanilla item-id coverage is `1230/1230`; spawn eggs, firework star/rocket, and infested blocks intentionally have no aspects; mapped runtime comparison currently has `0` real `PORT_GAP_*` buckets for comparable stacks |
| 14 | Essentia transport API | BlockEntity capabilities or explicit service interfaces | Very high | Define modern essentia access interface | Port later |
| 15 | Aura storage | Server-owned `SavedData` or chunk attachments, safe tick/update loop | Very high | Prototype server-side aura data without visuals | Started: `aura_design.md`, `AuraHelper`, per-level `SavedData`, chunk `base/vis/flux`, automatic chunk initialization with legacy base formula, main-thread legacy-like update loop, and permission-level-2 debug commands exist; 1.21 biome categories are mapped legacy-like because `BiomeDictionary` is gone; sync, FX, rifts, research-aware preservation, and gameplay consumers remain blocked. |
| 16 | Research model | Reloadable data loader/datapack format, validation, datagen | Very high | Create model classes and load a tiny test category | Started: legacy-style `data/thaumcraft/research/*.json` loader, hardcoded legacy category metadata/formulas, model records, list/info/status/visible/validate/stage-check commands, reload-time reference validation, first server progression layer, checked current-stage requirement path, modern crafting-event marker emission plus bridge recipes for resolvable `required_craft` entries, component-aware matching for legacy aspect-stack/material-family/stored-enchantment requirements, legacy-shaped category/entry visibility helpers, first research table/scribing tools storage/conversion/menu/action-result slice, GUI-ready knowledge sync for completed keys/stages/flags/raw observation/theory, bookshelf/enchanting-table/beacon theorycraft aids, safe Eldritch aids for glyphed stone plus vanilla Nether/End portals, basic Alchemy/Artifice/Infusion/Auromancy/Golemancy block aids, first XP/vanilla-aid cards, phial-backed `CardChannel`, option-item plus phial-backed `CardInfuse`, `CardAwareness` with minimal normal-warp storage side effect, safe Eldritch cards with minimal warp storage side effects, clay-backed `CardSculpting`, table-inventory `CardScripting`, Artifice item-option cards `CardTinker`/`CardMindOverMatter`, Dragon Egg unregistered guard, stage `warp` parsing with minimal permanent-warp reward storage, and non-interactive requirement/research-table audit exporters; latest validation reports `7` categories, `148` entries, `271` stages, `16` addenda, `201` resolved entry references, `95` external scan/flag trigger references, `0` unresolved research references, `0` identity-unresolved item/craft/knowledge stage requirements, `16` remaining subsystem bridge/placeholder warnings; Thaumonomicon screen rendering, exact direct craft-reference hash parity, rewards, recipe unlocks, Brain-in-a-Jar/Crimson portal/Basic Eldritch/Curio/Celestial theorycraft policies, and remaining advanced theorycraft cards/aids are not implemented |
| 17 | Player knowledge | Player attachment/capability, explicit sync payloads, server authority | Very high | Store one test research flag per player | Started: player persistent data wrapper stores observation/theory raw knowledge, known research keys, research stages, and research flags; raw knowledge mutation exists for scan rewards and checked-stage knowledge costs; a separate minimal player warp store preserves legacy warp array order and clamp for theorycraft side effects and current stage warp rewards; `progressResearch`, `completeResearch`, parent requisites with `&&`/`||`/`@stage`, hidden `~` prefix stripping, sibling completion, current-stage checked advancement, and visibility audits exist; `TCKnowledgeSyncPayload` now syncs completed keys, stages, flags, and raw observation/theory knowledge for Thaumometer filtering and future research GUI consumers. Reward handling, full warp events/effects/client sync, recipe unlocks, and final Thaumonomicon UI consumers remain blocked |
| 18 | Scanning system | Tags, item predicates, entity predicates, server validation | High | Port scan predicate model, no GUI | Started: debug `/scan held` and `/scan looking` use aspect lookup plus a legacy-shaped `IScanThing` registry; modern `ScanItem`, `ScanBlock`, `ScanEntity`, `ScanOreDictionary`, `ScanAspect`, generic scan, tag scan predicate, reloadable `data/thaumcraft/scannables` definitions, dynamic mob-effect/enchantment scans, vanilla entity aspects, and gated sky scan exist; bundled `legacy_core.json` currently loads 33 valid legacy definitions, base reload has 123 active predicates, and dynamic server predicates bring the current count to 205; item/potion/enchantment scan diff is `1139/1139` parity-ok with `0` scan or aspect differences; entity/state-variant scan diff has `83/85` parity-ok rows, `2` expected modern entity policy rows, and `0` actionable scan/aspect gaps; dropped `ItemEntity` targets now follow the legacy item-stack scan path; Thaumometer right-click mutates through `TCResearchManager.progressResearch`, blank-key scans suppress status messages, known targets no longer retrigger success, `ScanAspect` and `TCScanGeneric` grant legacy raw observation knowledge, and client visuals use legacy ranges/zone highlight/wild rays with living-mob aspect overlay separated from sparkle gating. Celestial-note side effects, non-observation rewards, and aura HUD remain blocked |
| 19 | Thaumonomicon UI | Modern `Screen`, custom widgets, client data cache | Critical | Build mock screen only after data model is stable | Port later |
| 20 | Arcane crafting | Custom recipe type/serializer, menu, BlockEntity, vis checks | Very high | Define recipe JSON/serializer first | Port later |
| 21 | Infusion crafting | Custom recipe type, modern BE scanning, payload FX, stability service | Critical | Write design document before code | Port later |
| 22 | Crucible and alchemy | Custom recipe types, BE save/tick, capability-based essentia access | Critical | Prototype crucible data model first | Port later |
| 23 | BlockEntity base classes | `BlockEntity`, `EntityBlock#getTicker`, `CompoundTag`, `setChanged` | Very high | Create one simple non-inventory BE first | Started narrowly: `TCResearchTableBlockEntity` saves/drops scribing tools and paper slots, owns theory data under the legacy `note` tag, provides the server menu container, and syncs update packets/tags for the table-top renderer; no ticking yet |
| 24 | Menus and machine GUI | `AbstractContainerMenu`, `MenuType`, `Screen`, data slots, payload actions | Very high | Port one simple inventory menu first | Started narrowly: `TCResearchTableMenu` and `TCResearchTableScreen` open the research table with legacy slot coordinates, minimal theory controls/payload actions, first vanilla/basic aid selection, action-result status, clickable legacy paper/gilded-paper card choices, and an atomic minimal select+commit request; page-flip/hover/zoom animations and full card UI are not complete |
| 25 | Networking | `CustomPacketPayload`, `StreamCodec`, `PayloadRegistrar`, validated handlers | Very high | Define packet categories and policies first | Started narrowly: aura sync, GUI-ready knowledge sync, and research-table serverbound action/clientbound sync/result payloads exist; each new gameplay subsystem still needs a focused packet inventory and validation policy |
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

Active registered content cleanup note: `amber`, `quicksilver`, `fabric`, `scribing_tools`, `table_wood`, `table_stone`, and `research_table` models now point at modern `thaumcraft:item/*` or `thaumcraft:block/*` textures, with active PNGs copied into `textures/item` or `textures/block`. Do not mass-convert unregistered legacy `thaumcraft:items/*` or `thaumcraft:blocks/*` references until those ids are intentionally ported.

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

1. Read `06_docs/current_port_status.md`, `06_docs/runtime_asset_audit.md`, `06_docs/aspects_design.md`, `06_docs/aspect_legacy_runtime_logic_audit.md`, and `06_docs/aspect_parity_comparison_harness.md`.
2. Re-run local build from `05_neoforge_port` after each change batch.
3. Keep the mapped dump-based aspect parity harness at `0` real `PORT_GAP_*` buckets before treating current aspect coverage as safe for gameplay consumers.
4. Keep the scan dump comparers at `1139/1139` comparable item rows and keep entity scan diffs at `0` actionable gaps before wiring Thaumometer gameplay mutation.
5. Use the next client visual pass to confirm the fixed active item texture paths and review creative tab order.
6. Define the policy for broad 1.21.1 vanilla/modded recipe-derived aspects before generating values for unrelated new content.
7. Continue vanilla aspect changes only from exact `ConfigAspects` assignments, audited legacy OreDictionary-to-tag bridges, validated crafting generation, or documented 1.21-only manual categories.
8. Do not expand aura beyond the current saved-data/query/debug-command/autogenerated chunk state core, or begin research, custom recipes, BlockEntities, networking, GUI, or expanded worldgen without a design note.
