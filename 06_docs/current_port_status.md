# Thaumcraft 6 NeoForge 1.21.1 Current Port Status

Last reviewed branch: `research-knowledge-scanning-design`
Last reviewed checkpoint: `2026-06-04` local validation before remote sync
Reviewed target module: `05_neoforge_port`
Working tree note: scanning/knowledge work is in progress; aspect, item-level scan, and entity-level scan parity dumps are clean for all comparable runtime keys. Thaumometer scan-key mutation and legacy-shaped client highlight/overlay behavior are active for the current predicate layer. The research-table/scribing-tools slice now includes storage, conversion, the first modern menu/screen boundary, server-owned theory data, validated table action payloads, server action-result screen refresh, legacy-asset card-sheet choice rendering, the first vanilla research-aid family for bookshelves, enchanting tables and beacons, safe Eldritch aids for glyphed stone plus vanilla Nether/End portals, the first basic block aids for crucible/arcane workbench/infusion matrix/wand workbench/golem press, the first Artifice, Basic Auromancy, Basic Golemancy and safe Eldritch theory cards, `CardInfuse`, `CardScripting`, `CardAwareness`, a minimal server-side warp storage bridge for current warp-side-effect cards, research bridge recipes for currently resolvable legacy requirements, current active resource/mining-tag cleanup, and a legacy-model-backed table-top renderer for scroll/inkwell/quill.

## Purpose

This is the current implementation status document. Use it together with the migration guide before starting new work. Older planning files remain useful, but some status sections are behind the actual code.

## Document priority

1. `06_docs/NeoForge_legacy_migration_guide_expanded_v3.docx` - main architecture guide.
2. `06_docs/current_port_status.md` - current repository status.
3. `06_docs/migration_matrix.md` - subsystem matrix and gate rules.
4. `06_docs/porting_order.md` - staged roadmap.
5. `06_docs/creative_tab_order_reference.md` - creative tab order rules.
6. `06_docs/subsystem_inventory.md` - legacy subsystem audit.
7. `06_docs/aspect_assignment_tag_audit.md` - exact OreDictionary-to-tag audit for aspect assignments.
8. `06_docs/aspect_generate_tags_audit.md` - exact legacy recipe-derived aspect generation audit and blockers.
9. `06_docs/aspect_assignment_data_format.md` - current data-driven aspect assignment format.
10. `06_docs/vanilla_aspect_policy.md` - policy for exact vanilla seeds, legacy OreDictionary tag bridges, and 1.21-only content.
11. `06_docs/vanilla_1_21_aspect_assignments.md` - complete current manual 1.21 vanilla assignment table and rationale.
12. `06_docs/vanilla_post_1_12_aspect_rationale.md` - complete modern-only/flattened/component stack table with aspect amounts and rationale.
13. `06_docs/aspect_legacy_gap_audit.md` - gap audit against 1.12 legacy and the rough 1.20.1 attempt.
14. `06_docs/aspect_generated_cache_design.md` - generated aspect stack key/cache scaffold and invalidation rules.
15. `06_docs/aspect_legacy_runtime_logic_audit.md` - detailed 1.12 runtime aspect lookup/bonus/generation/scanning audit.
16. `06_docs/aspect_parity_comparison_harness.md` - runtime dump and comparison method for 1.12.2 vs 1.21.1 aspect parity.
17. `06_docs/aura_design.md` - server-side aura storage/query/tick design for the first aura slice.
18. `06_docs/research_knowledge_scanning_design.md` - current research/knowledge/scanning design slice.
19. `06_docs/research_table_scribing_tools_design.md` - first research table/scribing tools BlockEntity slice boundary.
20. `06_docs/research_progression_parity_audit.md` - exact research progression, warp, reward, addendum, and data-parity checkpoint.
21. `06_docs/scanning_parity_validation.md` - runtime dump and comparison method for scan predicate parity.
22. `06_docs/entity_aspect_assignment_audit.md` - entity aspect assignment parity/policy audit for scanning.
23. `06_docs/rendering_model_pipeline_audit.md` - model/resource/rendering pipeline audit for 1.12 -> NeoForge 1.21.1.
24. `06_docs/gate1_items_plan.md` - historical Gate 1 workflow, not a full current inventory.

## High-level status

| Area | Status | Notes |
|---|---|---|
| Gate 0 bootstrap | Complete enough to continue | NeoForge module exists in `05_neoforge_port`; Java 21 and ModDevGradle are configured. |
| Main mod class | Implemented | `Thaumcraft` registers blocks, block entities, items, creative tabs, config, and current event listeners. |
| Item registry | Partially implemented | More than the original Gate 1 item slice exists. |
| Block registry | Partially implemented | Simple blocks, ores, stones, wood blocks, and plants have started. |
| Creative tab | Implemented, needs visual review | `TCCreativeTabOrder` owns visible order. Do not use registry order. |
| Assets | Runtime audited for active content | Missing original `assets` files were copied into the port without overwriting adapted 1.21 resources. Registered active content has model/lang/blockstate/loot coverage; `amber`, `quicksilver`, `fabric`, `scribing_tools`, `table_wood`, `table_stone`, and `research_table` item/model texture paths were fixed from legacy `items/`/`blocks/` to modern `item/`/`block/`, with active PNGs copied into `textures/item` or `textures/block`. `thaumometer` now uses the legacy 3D `scanner.obj` through the NeoForge OBJ loader with modern `textures/item/thaumometer.png`, alpha-pane `textures/item/scanscreen.png`, explicit OBJ texture aliases and translucent render type; in-game transform tuning still needs visual comparison against 1.12.2. |
| Loot tables | Active registered content covered | Modern simple-block loot tables exist under `data/thaumcraft/loot_table`; legacy `assets/thaumcraft/loot_tables` is imported as reference material and is not the 1.21 data path. |
| Tags | Aspect tag audit expanded | Tags replace old `OreDictionary` patterns. `aspect_assignment_tag_audit.md` maps current legacy aspect-related keys; safe current common tag resources exist for amber/cinnabar/quartz ores, amber gems, vanilla ore/gem/ingot/dust/raw-material bridges, and copper material bridges; exact `thaumcraft:legacy_ore_dictionary/*` item/block tags now preserve all already registered 1.12 OreDictionary entries. |
| Aspects | Core/API slice parity-clean for comparable stacks/entities, with two documented modern entity policy rows | `Aspect`, `AspectList`, pure `AspectHelper` logic, reload-safe data-driven exact/tag/manual assignments, vanilla material tag bridges, crafting generated-cache slice, legacy stack-sensitive bonus rules, component-aware potion and enchanted-book lookup, spawn-egg exclusion, vanilla entity aspect assignments, bootstrap parity validation, server-data-load tag validation, OreDictionary-to-tag audit, `generateTags` audit, read-only Shift inventory tooltip rendering, and assignment/cache/manual-policy docs are implemented/documented. All assignable current `minecraft:*` item ids have aspects after reload validation; spawn eggs, firework star/rocket, infested blocks, and empty component-only potion carriers remain intentionally excluded for legacy runtime parity. Current registered Thaumcraft option items used by theorycraft now include dump-derived exact values for `alumentum`, `salis_mundus`, `brain`, `nitor_yellow`, `thaumium_ingot`, and `brass_ingot`. `elder_guardian` and `zombie_villager` now have documented living-mob aspect rows as intentional modern-policy corrections. The runtime dump harness now runs on both original Forge 1.12.2 Thaumcraft and the 1.21.1 port; the comparers separate expected version differences from real port gaps. Current comparable item aspect parity is `1139/1139` with `0` amount/set/order/kind/null gaps; current comparable entity scan report has `83/85` fully parity-ok rows plus `2` expected modern entity aspect policy rows and `0` actionable gaps. Crucible, infusion, arcane recipe outputs, essentia transport, Thaumcraft custom entity aspects, and gameplay-heavy consumers remain blocked until their own design slices. |
| Aura | Started with server-side core | `AuraHelper`, per-level `SavedData`, chunk `base/vis/flux`, automatic chunk initialization, legacy formula for aura base generation, main-thread 20-tick legacy-like update loop, and permission-level-2 debug commands are implemented. The biome category mapper is legacy-like because 1.21 has no `BiomeDictionary`. HUD sync, FX, flux rifts, research-aware preservation, and gameplay consumers are intentionally not started. |
| Research | Progression core parity-closed; UI/catalog incomplete | Player knowledge storage, reload-safe research data, requirements, scan-key mutation, table/theorycraft slice, and GUI-ready knowledge sync exist. Checked stage completion now prebuilds item/knowledge consume plans before mutation and preserves exact legacy stage advancement, empty-gate handling, warp calculation/split, `wussMode`, completion flags, entry rewards, addendum `PAGE` notifications, siblings, XP, and final sync ordering. The research data harness reports `148/148` source/runtime entries, `7/7` Java categories, `0` differences, and `10/10` progression/parser checks. The requirement audit remains `0` unresolved with `16` subsystem bridge warnings; Research Table diagnostics remain `58/58`. Full warp events/effects/client sync, cancellable research/knowledge events, the permanent research recipe/page catalog, Thaumonomicon index/page/action payloads, and final Thaumonomicon UI remain blocked. |
| Recipes | Basic vanilla crafting fixtures and research bridges started | Simple modern `data/thaumcraft/recipe` crafting recipes exist for generated-aspect validation and the first research table slice: `tablewood`, `tablestone`, `scribingtoolscraft2`, and `scribingtoolsrefill`. Additional current `data/thaumcraft/recipe/research_bridge` recipes make registered/placeholder research requirement outputs craft-detectable for the modern `required_craft` marker path. These are bridge recipes, not final Thaumcraft recipe behavior. Custom Thaumcraft recipe serializers are not started. |
| BlockEntities | Started narrowly | `TCResearchTableBlockEntity` stores the two legacy research-table slots: scribing tools and paper, implements the table `Container`, provides the menu opening data, saves/loads theory state under `note`, can finish theories into raw THEORY knowledge by category, and now sends vanilla update packets/tags so the table-top renderer can see slot changes. Do not copy legacy `TileEntity` classes directly. |
| Menus/screens | Started narrowly | `TCResearchTableMenu` opens through `TCMenus.RESEARCH_TABLE`, exposes the two table slots plus the legacy-offset player inventory, and `TCResearchTableScreen` draws the legacy background with minimal functional controls for theory create/draw/complete/scrap plus the first vanilla/basic aid selection row. The screen now consumes server action-result payloads, applies authoritative table state after every accepted or rejected action, and uses legacy paper/gilded-paper sheets as clickable card choices for the current atomic select+commit request. Full 1.12 page-flip/hover/zoom animation, category icon column, full research-aid polish and final card animation are not complete. |
| Networking | Started narrowly | Modern custom payloads exist for aura sync, GUI-ready research knowledge sync, and research-table action/state/result sync. `TCKnowledgeSyncPayload` now carries completed research keys, stages, flags, and raw observation/theory knowledge for future research GUI consumers while preserving the Thaumometer completed-key cache path. The research table action payload carries selected aid keys only for theory creation; server revalidates open menu/table state and nearby aid keys before mutation, and every action result includes authoritative table data so the screen does not infer state locally. Do not treat this as a complete networking subsystem yet; every new payload still needs focused design and server validation. |
| Worldgen | Started early, not as a system | Sapling-grown Greatwood/Silverwood tree generators exist; biome modifiers, configured features, and structure/world placement are not implemented. |
| Rendering/FX | Started early, still high risk | Legacy-style FX dispatcher/particle scaffolding exists and `rendering_model_pipeline_audit.md` documents the 1.12 -> 1.21 resource/model split. Thaumometer right-click runes, held target highlight, and living-mob aspect icon overlay are started with legacy target ranges, wild block highlight behavior, legacy icon UV order, and separate known-vs-unknown gating. `TCResearchTableRenderer` now bakes the legacy `ModelResearchTable` parts for the scroll/tube/ribbon and inkwell, uses the legacy quill asset/transform, and renders those table-top objects only from synced BlockEntity state. Full rendering systems, BEWLR work, overlays, old shader wiring, and polished research-table/card animation must wait for focused validation. |

## Research/scanning stabilization split

| Bucket | Current contents | Notes |
|---|---|---|
| Ready | Aspect lookup parity harness, item scan parity harness, entity scan parity harness, reload-safe research JSON parsing, research reference validation, read-only scan commands, current Thaumometer scan-key mutation, GUI-ready knowledge sync payload/cache, and the first research table/scribing tools storage/menu/action-result/vanilla-aid/basic-block-aid/safe-Eldritch-aid slice. | Keep these covered by build/server/client smoke checks before expanding consumers. |
| Placeholder / Bridge | Remaining legacy requirement bridges for focus/caster/vis, unfinished advanced theorycraft cards/aids, minimal warp storage/debug commands before real warp events, crucible/smelter/nitor behavior, infusion matrix behavior, hidden Golem Press block identity before full golem-builder multiblock logic, Brain-in-a-Jar block/entity behavior, Crimson portal entity behavior, Alumentum/Salis Mundus behavior, mirror, curio, zombie brain behavior, OreDictionary chest and flattened vanilla metadata. The current XP/phial/vanilla-aid/Golemancy/table-inventory/Artifice/basic-Auromancy/basic-Infusion/basic-Golemancy/safe-Eldritch theory cards are still bridge-level gameplay until the full table UI/reward loop is finished. Research bridge recipes, generated parity reports and debug-only research completion commands are also bridge tooling. | These are scaffolds for validation and migration. Do not treat them as final gameplay, reward, recipe, or subsystem behavior. |
| Blocked | Permanent research recipe/page catalog, Thaumonomicon UI/page rendering and payloads, full warp events/effects/client sync, cancellable research/knowledge events, exact direct `required_craft` legacy hash mapping, essentia container behavior/filling/draining, custom Thaumcraft entities, and ScanSky celestial-note side effects. | These need their own focused design/validation slices. |
| Next subsystem | Permanent research recipe/page catalog and server-authoritative Thaumonomicon protocol foundation. | Progression and raw research data are now parity-checked. Resolve/classify the `248` legacy research recipe/page references before building the real browser/page UI. |

## Legacy asset corpus import

The original Thaumcraft 6 asset tree from `03_self_decompiled_check/vineflower_thaumcraft6/assets` has been imported into `05_neoforge_port/src/main/resources/assets`.

| Asset import detail | Value |
|---|---:|
| Source asset files | `1531` |
| Files copied into the port | `828` |
| Existing port files preserved | `703` |
| Port asset files after import | `1669` |
| Thaumcraft namespace files after import | `1658` |
| Minecraft shader namespace files after import | `11` |

Import rule: do not overwrite already-adapted 1.21 resources. Some shared legacy paths differ from the current port versions, especially `blockstates`, `models/block`, and `models/item`; the port versions remain authoritative for currently registered content.

Imported legacy resources include old `.lang` files, `research`, `shader`, `sounds`, `textures/gui`, `textures/entity`, `textures/research`, OBJ/MTL models, legacy `loot_tables`, and legacy `textures/blocks`. These are reference/base assets until each subsystem adapts them to NeoForge/Minecraft 1.21.1 conventions.

The copied-file manifest is `06_docs/asset_bulk_import_manifest.txt`.

The runtime asset audit is `06_docs/runtime_asset_audit.md`.

## Last local validation

| Check | Result | Notes |
|---|---|---|
| `.\gradlew.bat build --no-daemon` | Passed | Re-run after each aspect/tag/client-tooltip/aura/research expansion batch. Latest research table/scribing tools slice compiles. |
| `.\gradlew.bat runClient --no-daemon` | Startup/resource smoke reached integrated world join | Client reached resource loading, aspect assignment reload, generated cache rebuild, tag validation, texture atlas creation, integrated server startup and `Dev joined the game`. The session was stopped manually after startup; latest reviewed log had no Thaumcraft missing texture/model errors. Remaining warnings were vanilla/NeoForge asset URL schema noise, missing vanilla goat horn sounds, and one vanilla shader sampler warning. |
| `.\gradlew.bat runServer --no-daemon` | Startup/reload smoke reached `Done` through the research audit server runs | Dedicated server reached `Done`; bundled bootstrap, data-resource assignment reload, generated crafting cache rebuild, tag reload validation, research data reload, aura event/command registration, automatic aura chunk initialization, and the client-only tooltip registration boundary all passed. Latest audit run loaded `682` exact assignments, `46` tag assignments, `32` complex exact assignments, rebuilt `496` generated crafting cache entries from `1341` loaded recipes, loaded `7` research categories / `148` entries / `271` stages / `16` addenda, and reported `1230 of 1230` assignable minecraft item ids with non-empty aspects. Spawn eggs and empty component-only splash/lingering/tipped carrier ids are excluded for 1.12 parity. |
| `/tc research validate` | Reload-equivalent validation passed | The server reload validator reported `201` resolved entry references, `95` external scan/flag trigger references, and `0` unresolved research references. Direct console command execution from the Codex Gradle terminal did not reach the server stdin, so this row records the equivalent reload-time validation, not a typed command transcript. |
| Research requirement audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcResearchRequirementAudit=true "-PtcResearchRequirementAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_requirement_audit\thaumcraft_1_21_research_requirements.md" -PtcResearchRequirementAuditDetailLimit=200` reached `Done`, wrote the audit, and stopped automatically. Current result: `required_item=69/69`, `required_craft=34/34`, `required_knowledge=170/170`, `0` identity-unresolved requirements, and `16` remaining bridge/placeholder warnings. |
| Research table diagnostic exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcResearchTableAudit=true "-PtcResearchTableAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_table_audit\thaumcraft_1_21_research_table.md"` reached `Done`, wrote `07_Test_Instance_and_Comparisons/research_table_audit/thaumcraft_1_21_research_table.md`, reported `58` passed / `0` failed static checks, and stopped automatically. New checks cover `CardAwareness`, minimal warp storage round-trip/clamping, `CardInfuse`, the basic Alchemy/Artifice/Infusion/Auromancy/Golemancy block aids, safe Eldritch glyphed-stone/Nether-portal/End-portal aids, safe Eldritch cards, GUI-ready knowledge sync cache contents, authoritative research-table action-result payloads, and the fact that Dragon Egg theorycraft code remains unregistered like original TC6. In-game `/tc research_table validate player` adds live player checks for paper, scribing-tool damage, required item checks/consumption, draw-card availability, XP-gated card activation including `CardDarkWhispers`, and finish-theory knowledge mutation. |
| Aspect runtime dumps | Passed mapped harness run | Original Forge 1.12.2 Thaumcraft server wrote `1798` entries; NeoForge 1.21.1 server wrote `1987` entries. With `legacy_to_modern_stack_map.json`, the comparer has `1139` comparable keys: `1139` identical, including `283` legacy-to-modern mapped parity entries. Current real mapped gaps are `0`; potion content/order, mapped Sweeping Edge stored books, and currently registered Thaumcraft set differences are closed. |
| Scan runtime dumps | Passed item-level and entity-level scan harness runs | Original Forge 1.12.2 scan exporter wrote `1798` item entries and `129` entity entries; NeoForge 1.21.1 server wrote `1987` item entries and `131` entity entries. With stack/research-key/entity-id normalization, item scans have `1139/1139` comparable parity-ok rows. Entity scans now have `83/85` comparable parity-ok rows plus `2` expected modern-policy living-mob rows (`elder_guardian`, `zombie_villager`). Both reports have `0` actionable scan key/set/found/aspect gaps. |

## Implemented identity entries seen in `TCItems`

| Group | Entries |
|---|---|
| Ores | `ore_amber`, `ore_cinnabar`, `ore_quartz` |
| Crystals | `crystal_aer`, `crystal_ignis`, `crystal_aqua`, `crystal_terra`, `crystal_ordo`, `crystal_perditio`, `crystal_vitium` |
| Stone blocks | `stone_arcane`, `stone_arcane_brick`, `stone_ancient`, `stone_ancient_tile`, `stone_ancient_rock`, `stone_ancient_glyphed`, `stone_ancient_doorway`, `stone_eldritch_tile`, `stone_porous` |
| Stairs and slabs | `stairs_arcane`, `stairs_arcane_brick`, `stairs_ancient`, `stairs_greatwood`, `stairs_silverwood`, `slab_arcane_stone`, `slab_arcane_brick`, `slab_ancient`, `slab_eldritch`, `slab_greatwood`, `slab_silverwood` |
| Wood, leaves, plants | `log_greatwood`, `log_silverwood`, `leaves_greatwood`, `leaves_silverwood`, `sapling_greatwood`, `sapling_silverwood`, `shimmerleaf`, `cinderpearl`, `vishroom`, `plank_greatwood`, `plank_silverwood` |
| Other blocks/items | `amber_block`, `amber_brick`, `table_wood`, `table_stone`, `research_table`, `golem_builder`, `thaumometer`, `goggles`, `amber`, `quicksilver`, `fabric`, `salis_mundus`, `alumentum` |
| Research/progression bridge identities | Thaumium/brass materials, aspect crystal essence variants, phial variants, stored-enchantment requirements and legacy metadata-family requirements now carry component-level semantics for requirement matching. Scribing tools and research table now have their first legacy-backed storage/conversion slice, `golem_builder` exists as the hidden Golem Press identity for Basic Golemancy aid detection, and Alumentum/Salis Mundus exist as identity/requirement items for theorycraft, but their real behavior is not implemented. Arcane/wand workbench blocks, crucible/smelter/infusion matrix blocks, focus/caster/vis placeholder items, Golem Press multiblock/GUI/essentia logic, special thaumium tool behavior, curio, mirror and brain behavior remain subsystem bridges until their behavior slices are implemented. |

## Aspect assignment data resources

The current aspect assignment source of truth is split across bundled data files under `05_neoforge_port/src/main/resources/data/thaumcraft/aspect_assignments/`.

| Assignment layer | Count | Notes |
|---|---:|---|
| Exact item assignments | `682` | `current_registered.json` covers normally authored registered Thaumcraft ids; `current_registered_runtime_parity.json` preserves dump-derived registered Thaumcraft final values, including the registered `thaumometer`, `table_wood`, `table_stone`, `research_table`, `alumentum`, `salis_mundus`, `brain`, `nitor_yellow`, `thaumium_ingot`, and `brass_ingot`; `legacy_vanilla_core.json` and `legacy_vanilla_modern_exact.json` preserve direct vanilla seeds; `legacy_vanilla_modern_manual.json` covers 1.21-only vanilla ids by audited Thaumcraft-style category; `legacy_vanilla_runtime_parity.json` preserves dump-derived final 1.12 values for shared plain vanilla stacks affected by metadata flattening, generated recipes, complex extras, wildcard specificity, or stack bonuses. Spawn eggs, firework star/rocket, and infested blocks are excluded because 1.12 gave those comparable stacks no aspects. |
| Item tag assignments | `46` | Includes safe current common `c:` tags, vanilla material bridges for legacy ore/gem/ingot/dust/base-block keys, ore-derived 1.21 raw materials, `blockGlass`, plus exact `thaumcraft:legacy_ore_dictionary/*` compatibility tags where legacy used string-key aspect assignments. |
| Complex exact assignments | `32` | Current complex extras cover audited buckets, boats, doors, fence gates, and related legacy complex additions. Runtime diff proves this layer must continue to be source-vs-runtime reviewed before broad expansion because legacy exact/generated/wildcard lookup order can mask wildcard complex values. |
| Generated crafting assignments | `496` | Built from `RecipeType.CRAFTING` recipes after server data/tag reload for current `minecraft:*` and `thaumcraft:*` outputs that have known ingredient aspects. Exact/tag/manual/runtime-parity assignments still win over generated values. The current count includes safe research bridge recipes used to make modern `required_craft` markers observable; those recipes are not final gameplay implementations. |

## Generated aspect recipe cache

`TCAspectStackKey`, `TCGeneratedAspectCache`, and `TCGeneratedAspectRecipeGenerator` define the current generated-aspect cache boundary. The key uses item registry id plus the stack data component patch and ignores stack count, matching the legacy normalization intent. The cache is cleared on aspect assignment bootstrap/reload, then rebuilt after server data/tag reload from loaded vanilla crafting recipes.

The current generated slice covers only normal crafting recipes. It does not depend on whether a recipe is crafted from the 2x2 inventory grid or the 3x3 crafting table; it scans `RecipeType.CRAFTING`, and each recipe's own dimensions decide where it can be crafted.

Validation proves:

- exact item assignment wins over generated cache;
- tag assignment wins over generated cache after tags are loaded;
- generated fallback works for recipe-derived outputs;
- `AspectHelper.generateTags` returns generated cache entries without doing lookup-time recipe scans.
- every assignable current vanilla `minecraft:*` item registry id has aspects after server data/tag reload;
- conservative vanilla direct and tag seeds work for coal, buckets, ores, gems, ingots, dusts, and copper;
- 1.21 raw iron/gold/copper are intentionally ore-derived from corresponding legacy `ore*` entries;
- spawn eggs intentionally return no aspects;
- potions, splash potions, lingering potions, tipped arrows, and enchanted books use stack components for legacy parity instead of plain id-only lookup;
- scan-specific long slowness potion quirks are isolated in `AspectHelper.getScanAspects` so the normal object/tooltip aspect dump remains identical to legacy;
- shapeless crafting and remaining-item subtraction match the legacy crafting formula.

## Current gate interpretation

| Gate | Current interpretation |
|---|---|
| Gate 0 | Complete enough to continue, but still validate `runServer` after client/render changes. |
| Gate 1 | In progress and expanded beyond the first simple item batch. Active registered item resources are covered; creative order still needs visual review. |
| Gate 2 | Started early through simple block and block item identity work. Active registered blockstates, models, loot tables, and translations are covered. |
| Gate 3 | Implementation started carefully. Exact legacy core aspect definitions/list/helper logic, current registered-id assignments, generated crafting cache, vanilla entity aspect lookup, read-only Shift tooltip rendering, and scan-resolved aspect lookup are present and guarded by validation; gameplay-heavy consumers remain blocked. |
| Gate 4+ | Aura is started as an isolated server-side storage/API slice after `aura_design.md`; the first research-table BlockEntity and menu/screen boundary exists. Capabilities, custom recipes, broad networking, worldgen, and large rendering systems remain not started. |

## Partially stale documents

| Document | Stale part | Current handling |
|---|---|---|
| `gate1_items_plan.md` | Lists only `amber`, `quicksilver`, and `fabric` as the first implemented slice. | Keep as workflow guidance; use this file for actual inventory. |
| `creative_tab_order_reference.md` | First implemented entries section no longer reflects all implemented entries. | Keep policy; status should point here. |
| `migration_matrix.md` | Matrix now distinguishes imported legacy assets from active adapted resources. | Keep matrix for policy and gate sequencing; use this file for live implementation status. |
| `block_parity_audit.md` | Refreshed for sapling/tree and block property updates. | Still requires exact legacy parity checks before behavior tuning. |

## Do not start without a design note

Do not implement aura, research, arcane crafting, crucible, infusion, BlockEntities, menus, networking, worldgen, or large rendering systems by direct legacy class copying. Each one needs a small design document first. The current aspect work is limited to the documented data layer and read-only inventory tooltip consumer.

## Immediate next work

1. Re-run `./gradlew build --no-daemon` after the runtime audit and design-document cleanup.
2. Keep the mapped aspect diff report at `0` real `PORT_GAP_*` buckets before treating current coverage as safe for gameplay consumers.
3. Use the next full `./gradlew runClient --no-daemon` visual pass to inspect creative tab order, active item icons, Shift-held aspect tooltip visuals, and the 3D Thaumometer OBJ transforms in GUI/hand views.
4. Compare creative tab order with the 1.12.2 inventory screenshots.
5. Add future registered item/block aspect values through `data/thaumcraft/aspect_assignments`; entity aspects currently use a legacy Java table until an entity assignment datapack format is designed.
6. Keep vanilla item coverage at `0 missing` after every aspect/tag change; do not broaden third-party modded generated outputs until an addon policy exists.
7. Implement crucible, infusion, and arcane recipe-derived `generateTags` behavior from `aspect_generate_tags_audit.md` before assigning aspects to those custom recipe outputs.
8. Continue vanilla aspect changes only from `ConfigAspects`, audited legacy OreDictionary-to-tag bridges, recipe-derived cache behavior, or documented 1.21-only category policy.
9. Keep parity validation and reload validation passing before expanding aspect consumers.
10. Do not expand aura beyond saved-data/query/debug-command/autogenerated chunk state, and do not begin essentia, broad GUI, broad networking, crafting costs, or gameplay-heavy systems without their own design notes.
11. Keep the research data parity harness at `0` source/runtime/category differences and all progression checks passing after every parser or progression change.
12. Build the permanent research recipe/page catalog before implementing Thaumonomicon page rendering; do not treat legacy `stages[].recipes` as simple vanilla recipe unlock ids.
13. Keep the research table diagnostic harness passing after every `TCResearchTableData`, card, aid, table menu, or action payload change.
14. Visually check vanilla/basic aid selection and table-top scroll/inkwell/quill placement in `runClient`; then port remaining theorycraft cards/aids only by audited dependency family. Advanced cards that require full warp effects, curios, focus/caster, infusion, celestial notes, portals, or other unported systems must stay deferred or explicitly bridged.

## Local validation commands

From `05_neoforge_port`:

```powershell
.\gradlew.bat build --no-daemon
.\gradlew.bat runClient --no-daemon
.\gradlew.bat runServer --no-daemon
.\gradlew.bat runServer --no-daemon -PtcResearchTableAudit=true "-PtcResearchTableAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_table_audit\thaumcraft_1_21_research_table.md"
```

## Research, knowledge and scanning design

Started:
- Added design document for player knowledge, research commands and scanning.
- Added legacy source audit for CommandThaumcraft, PlayerKnowledge, IPlayerKnowledge, ResearchManager, ScanningManager and scan predicate classes.
- Next implementation should start with knowledge storage and commands only.

Do not start full Thaumonomicon GUI, crucible, infusion or full warp events/effects before the player knowledge and research skeleton is stable.
## Research data skeleton

Started:
- Added server data reload listener for `data/thaumcraft/research/*.json`.
- Copied the eight legacy Thaumcraft research files into the server-data path.
- Added category/entry/stage model records and hardcoded legacy category metadata/formulas.
- Added read-only `/thaumcraft research list` and `/thaumcraft research info <key>` commands.
- Latest server reload validates `7` categories, `148` entries, `271` stages, and `16` addenda.
- Added read-only research reference validation through reload logging and `/thaumcraft research validate`; latest server reload reports `201` resolved entry references, `95` external scan/flag trigger references, and `0` unresolved research references.
- Added checked current-stage requirement diagnostics and advancement commands: `/thaumcraft research <player> stage <research_key> check` and `advance`.
- The checked-stage path mirrors legacy `PacketSyncProgressToServer.checkRequisites`: it verifies current-stage item/craft/research/knowledge gates and consumes item/knowledge costs only after all gates pass.
- Added modern crafting-event marker emission for resolvable `required_craft` entries. This preserves the legacy hidden-marker role for future stage checks, while exact direct legacy ItemStack hash ids remain blocked until exported/mapped.
- Added component-aware research requirement semantics for aspect crystal essence, filled phials, legacy material-family metadata and stored enchantment requirements. Enchanted-placeholder requirements match real enchanted item/book stacks like legacy `InventoryUtils.checkEnchantedPlaceholder`, not just fake placeholder ids.

Still blocked:
- Thaumonomicon screen rendering, exact direct craft-reference hash parity, permanent recipe/page catalog resolution, final page/action UI consumers, full warp events/effects/client sync, and cancellable research/knowledge events. Entry rewards and addendum notifications are implemented, but built-in TC6 data does not provide a real reward integration fixture.
## Player knowledge command skeleton

Started:
- Added server-side player persistent knowledge storage wrapper.
- Added observation and theory knowledge types with legacy-like raw point conversion.
- Added /thaumcraft, /thaum and /tc knowledge debug command tree.
- Added minimal stored research key skeleton commands.
- Scan observation rewards and Thaumometer scan-key mutation are implemented. Celestial-note side effects, non-observation scan rewards, aura HUD, and final Thaumonomicon consumers remain intentionally incomplete.
## Scanning debug command skeleton

Started:
- Added TCScanningManager and TCScanResult.
- Added /thaumcraft, /thaum and /tc scan debug command tree.
- Added held item aspect lookup.
- Added looking block aspect lookup through block item form.
- Added legacy-shaped `thaumcraft.api.research.IScanThing` and `ScanningManager` shell.
- Added modern `ScanItem`, `ScanBlock`, `ScanEntity`, `ScanOreDictionary`, and `ScanAspect` predicate classes.
- Added initial generic scan predicate for aspect-bearing items, blocks and entities.
- Restored legacy aspect-trigger scan behavior: `Aspect` registers `ScanAspect("!"+tag)` and the reload bootstrap re-adds aspect predicates before generic scan.
- Added reloadable `data/thaumcraft/scannables/*.json` format and documented it in `scannable_data_format.md`.
- Added bundled `legacy_core.json` with 32 currently valid legacy scan definitions.
- Dedicated server reload currently reports 123 active scan predicates before dynamic server predicates and 205 after dynamic mob-effect/enchantment predicates register.
- Added dynamic mob-effect and enchantment scan predicates as modern equivalents for legacy `ScanPotion` and `ScanEnchantment`; server startup currently reports 205 active predicates after dynamic registration.
- Added gated sky scan predicate for `CELESTIALSCANNING`, without celestial note side effects.
- Added vanilla entity aspect assignments for legacy mob/object scan targets, with documented post-1.12 entity policy rows. Runtime parity shows `minecraft:elder_guardian` and `minecraft:zombie_villager` had no effective 1.12 aspects, but the port now intentionally gives them living-mob aspect rows: elder guardian uses the legacy Guardian+Elder NBT intent; zombie villager uses zombie/villager hybrid semantics. Thaumcraft custom entity assignments remain deferred until those entities are registered.
- Thaumometer right-click now plays registered `thaumcraft:scan` sound and spawns legacy-shaped rune particles. Server scan and client use visuals share the legacy entity target resolver: min range `1`, scan range `9`, zone-style inflated hitboxes, and line-of-sight checks. While held, the client uses the longer legacy highlight pass: entity range `16` with `padding=5`, plus separate wild block rays at range `16` with random yaw/pitch spread. Highlight eligibility derives potential scan keys from aspect lookup, active data scannables, potion/effect scans, and enchantment scans, then filters already-known keys through the completed-key portion of the GUI-ready knowledge sync payload. Living-mob aspect icons plus amounts render above normal aspect-bearing living mobs even after known keys; sparkle highlight is the part gated by not-yet-known keys. Right-click scan mutation now uses `TCResearchManager.progressResearch`, grants only newly unknown scan keys, respects parent requisites where loaded entries exist, and preserves blank-key suppress behavior. Aura HUD, celestial-note side effects, and non-observation scan rewards are still pending.
- Added legacy scan learning side effects: `ScanAspect` now grants the same raw `+1` observation unit to AUROMANCY/BASICS/ALCHEMY as 1.12, and `TCScanGeneric` applies the legacy category formula to scanned aspects before adding raw OBSERVATION knowledge.
- Added `post_1_12_scanning_policy.md`: post-1.12 vanilla items use documented aspect policy plus generic scan; bespoke research keys require explicit design.
- Added `/thaumcraft scan audit_items`, automated `-PtcScanDump=true` server dumps, `scanning_parity_validation.md`, and `07_Test_Instance_and_Comparisons/scan_parity` for deterministic item, potion, enchantment and scan-key audit diffs.
- The latest scan report has `1139/1139` comparable item/potion/enchantment rows parity-ok and no aspect-value or scan-logic differences.
- Added `scanning_gap_audit.md`, restored legacy dropped-item scan targeting by allowing `ItemEntity` look targets, and added `/thaumcraft scan audit_entities` plus `-PtcScanEntityDump=true` modern server dumps.
- Added Forge 1.12.2 legacy entity/state-variant exporter and `compare_entity_scan_dumps.py`. Latest entity report: `83/85` comparable vanilla entity/state rows parity-ok, `2` expected modern entity aspect policy rows, `0` actionable gaps, `44` expected legacy-only rows for deferred Thaumcraft entities/guardian NBT probe, and `46` expected modern-only post-1.12 rows.
- Scan commands currently report aspects and matched scan keys without mutating player knowledge; the actual Thaumometer item performs the server-side scan-key mutation.

Next:
- Fill deferred `ConfigResearch.initScannables` entries as their target ids become registered.
- Keep checking scan observation rewards against real Thaumonomicon knowledge costs once the page UI exists.
- Add `ScanSky` celestial-note side effects after celestial notes and scribing tools exist.
- Continue research with reward handling, visibility/page filtering, recipe unlocks, and Thaumonomicon page/action UI now that the first GUI-ready knowledge sync exists.
