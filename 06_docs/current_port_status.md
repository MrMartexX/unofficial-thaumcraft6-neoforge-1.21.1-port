# Thaumcraft 6 NeoForge 1.21.1 Current Port Status

Last reviewed branch: `main`
Last reviewed checkpoint: `2026-06-19` infusion matrix/pedestal completion-readiness boundary
Reviewed target module: `05_neoforge_port`

## State Snapshot

This section records the current repository state. The lower "Changelog Notes" section records how the project reached this state.

### Active parity baseline

- Aspect, item-level scan, and entity-level scan parity dumps are clean for all comparable runtime keys.
- Thaumometer scan-key mutation and legacy-shaped client highlight/overlay behavior are active for the current predicate layer.
- Current comparable item aspect parity is `1139/1139`; item-level scan parity is `1139/1139`; entity scan parity has `83/85` parity-ok rows plus `2` documented expected modern entity-policy rows.

### Research, scanning and theorycraft

- The research-table/scribing-tools slice includes storage, conversion, the first modern menu/screen boundary, server-owned theory data, validated table action payloads, server action-result screen refresh, and legacy-asset card-sheet choice rendering.
- Current research aids/cards include the first vanilla bookshelf/enchanting-table/beacon family, safe Eldritch glyphed-stone/Nether-portal/End-portal aids, basic block aids for crucible/arcane workbench/infusion matrix/Focal Manipulator/golem press, first Artifice, Basic Auromancy, Basic Golemancy, safe Eldritch theory cards, `CardInfuse`, `CardScripting`, and `CardAwareness`.
- Minimal server-side warp storage exists only as a bridge for current warp-side-effect cards.

### Crafting, recipes and page data

- The first server-authoritative Arcane Workbench crafting path exists with server-owned base/discounted cost, aura, crystal GUI feedback, missing-vis ghost output, first player vis-discount service, and Workbench Charger 3 x 3 aura behavior.
- Exact active arcane recipes include `thaumometer`, `vis_resonator`, `workbenchcharger`, `goggles`, `mechanism_simple`, `mechanism_complex`, `wand_workbench`/Focal Manipulator, `caster_basic`, `enchantedfabric`, `mirrorglass`, `filter`, `morphicresonator`, `essentiasmelter`, and `infusionmatrix`, with passing server runtime audits.
- Arcane recipe-derived aspect generation is active for the current `TCArcaneRecipe` family and reload-validates `filter` plus `morphic_resonator`, including the legacy `praecantatio` vis bonus.
- The `thaumcraft:crucible` serializer/page-data boundary covers HEDGE_ALCHEMY, METAL_PURIFICATION, Alumentum, Nitor, Brass Ingot, Thaumium Ingot, all 37 vis-crystal recipes, and lowercase legacy OreDictionary catalyst tag bridges. The seven legacy dynamic HEDGE_ALCHEMY costs are resolved into explicit JSON aspect costs from current parity data so runtime recipe matching never sees zero-cost placeholders.
- The `thaumcraft:infusion` serializer/page-data boundary now has `42/42` valid JSON recipes, Thaumonomicon page snapshots, a server-owned `TCInfusionAssembly` validation snapshot, `TCInfusionCraftingPlan` active start state, `TCInfusionCompletionPlan` read-only completion-readiness state, runtime behavior audit, active pedestal block ids, one-slot pedestal BlockEntities, and a matrix BlockEntity that scans legacy-range pedestal inputs. Component matching now follows legacy Forge `RecipeMatcher.findMatches`: unordered by position, but exact 1:1 by supplied component count.

### Bridge recipe cleanup and identity alignment

- Recent bridge cleanup replaced duplicated caster-shaped placeholder JSONs across transport, alchemy/essentia, utilities, infusion support, pedestal/jar/artifice/vis/stability/grapple/banner/focus-pouch/mind/turret, robe, sanity checker, and golem-module recipe families.
- Explicit bridge identities now cover legacy mind metadata 1, primordial pearl, `brass_nugget`, and `quicksilver_nugget`; Advanced Crossbow and Advanced Alchemical Construct placeholders are resolved.
- Recipe registry/tag audit tooling verifies local Thaumcraft item/block identities, simple item registrations, and lowercase legacy tag bridges.

### Assets and visuals

- Active resource/mining-tag cleanup and legacy-id alignment are current for registered candle, tube, smelter auxiliary, charger, nitor, creative-tab, and `smelter_basic` model paths.
- Fresh in-game visual review is still separate from recipe cleanup and should be performed before treating visual parity as final.

### Deferred boundaries

- Special alchemy side effects, crucible-derived aspect generation, item pulling radius, Thaumatorium/alembic/jar/tube integration, infusion crafting mutation/consumption/instability/essentia/FX, full custom entity/golem systems, broad worldgen, broad rendering polish, full equipment/Curios discount integration, and remaining dependency-heavy recipe/page families remain deferred.

## Purpose

This is the current implementation status document. Use it together with the migration guide before starting new work. The `State Snapshot` and `High-level status` sections record current state; `Changelog Notes` records historical updates. Older planning files remain useful, but some status sections are behind the actual code.

## Document priority

1. `06_docs/migration/NeoForge_legacy_migration_guide.md` - main architecture guide.
2. `06_docs/current_port_status.md` - current repository status.
3. `06_docs/CURRENT_TASK.md` - live task queue and immediate guardrails.
4. `06_docs/documentation_index.md` - docs folder navigation and cleanup rules.
5. `06_docs/migration/migration_matrix.md` - subsystem matrix and gate rules.
6. `06_docs/migration/porting_order.md` - staged roadmap.
7. `06_docs/resources/creative_tab_order_reference.md` - creative tab order rules.
8. `06_docs/migration/subsystem_inventory.md` - legacy subsystem audit.
9. `06_docs/data/aspects/aspect_assignment_tag_audit.md` - exact OreDictionary-to-tag audit for aspect assignments.
10. `06_docs/data/aspects/aspect_generate_tags_audit.md` - exact legacy recipe-derived aspect generation audit and blockers.
11. `06_docs/data/aspects/aspect_assignment_data_format.md` - current data-driven aspect assignment format.
12. `06_docs/data/aspects/vanilla_aspect_policy.md` - policy for exact vanilla seeds, legacy OreDictionary tag bridges, and 1.21-only content.
13. `06_docs/data/aspects/vanilla_1_21_aspect_assignments.md` - complete current manual 1.21 vanilla assignment table and rationale.
14. `06_docs/data/aspects/vanilla_post_1_12_aspect_rationale.md` - complete modern-only/flattened/component stack table with aspect amounts and rationale.
15. `06_docs/data/aspects/aspect_legacy_gap_audit.md` - gap audit against 1.12 legacy and the rough 1.20.1 attempt.
16. `06_docs/data/aspects/aspect_generated_cache_design.md` - generated aspect stack key/cache scaffold and invalidation rules.
17. `06_docs/data/aspects/aspect_legacy_runtime_logic_audit.md` - detailed 1.12 runtime aspect lookup/bonus/generation/scanning audit.
18. `06_docs/data/aspects/aspect_parity_comparison_harness.md` - runtime dump and comparison method for 1.12.2 vs 1.21.1 aspect parity.
19. `06_docs/gameplay/aura_design.md` - server-side aura storage/query/tick design for the first aura slice.
20. `06_docs/research/research_knowledge_scanning_design.md` - current research/knowledge/scanning design slice.
21. `06_docs/research/research_table_scribing_tools_design.md` - first research table/scribing tools BlockEntity slice boundary.
22. `06_docs/research/research_progression_parity_audit.md` - exact research progression, warp, reward, addendum, and data-parity checkpoint.
23. `06_docs/research/thaumonomicon_ui_design.md` - server-authoritative item/open/browser/entry UI boundary and deferred recipe-page scope.
24. `06_docs/research/scanning_parity_validation.md` - runtime dump and comparison method for scan predicate parity.
25. `06_docs/data/aspects/entity_aspect_assignment_audit.md` - entity aspect assignment parity/policy audit for scanning.
26. `06_docs/rendering/rendering_model_pipeline_audit.md` - model/resource/rendering pipeline audit for 1.12 -> NeoForge 1.21.1.
27. `06_docs/migration/gate1_items_plan.md` - historical Gate 1 workflow, not a full current inventory.

## High-level status

| Area | Status | Notes |
|---|---|---|
| Gate 0 bootstrap | Complete enough to continue | NeoForge module exists in `05_neoforge_port`; Java 21 and ModDevGradle are configured. |
| Main mod class | Implemented | `Thaumcraft` registers blocks, block entities, items, creative tabs, config, and current event listeners. |
| Item registry | Partially implemented | More than the original Gate 1 item slice exists. |
| Block registry | Partially implemented | Simple blocks, ores, stones, wood blocks, plants, all sixteen legacy `candle_<color>` ids, current smelters, and legacy-named tube block identities have started. |
| Creative tab | Implemented, needs visual review | `TCCreativeTabOrder` owns visible order. Do not use registry order. |
| Assets | Runtime audited for active content | Missing original `assets` files were copied into the port without overwriting adapted 1.21 resources. Registered active content has model/lang/blockstate/loot coverage; `amber`, `quicksilver`, `fabric`, `scribing_tools`, `thaumonomicon`, `table_wood`, `table_stone`, `research_table`, `iron_plate`, `brass_plate`, `thaumium_plate`, `void_plate`, `mechanism_simple`, `mechanism_complex`, and `vis_resonator` item/model texture paths were fixed from legacy `items/`/`blocks/` to modern `item`/`block`, with active PNGs copied into `textures/item` or `textures/block`. Active research text required by the first Thaumonomicon screen is merged into modern `en_us.json`. The `2026-06-17` identity cleanup realigned active candle, tube and smelter-auxiliary resources to legacy public ids while keeping modern 1.21 blockstate/model paths authoritative. `thaumometer` now uses the legacy 3D `scanner.obj` through the NeoForge OBJ loader with modern `textures/item/thaumometer.png`, alpha-pane `textures/item/scanscreen.png`, explicit OBJ texture aliases and translucent render type; in-game transform tuning still needs visual comparison against 1.12.2. |
| Loot tables | Active registered content covered | Modern simple-block loot tables exist under `data/thaumcraft/loot_table`; legacy `assets/thaumcraft/loot_tables` is imported as reference material and is not the 1.21 data path. |
| Tags | Aspect tag audit expanded | Tags replace old `OreDictionary` patterns. `aspect_assignment_tag_audit.md` maps current legacy aspect-related keys; safe current common tag resources exist for amber/cinnabar/quartz ores, amber gems, vanilla ore/gem/ingot/dust/raw-material bridges, plate bridges, brass/thaumium ingots, rods/wooden, and copper material bridges; exact `thaumcraft:legacy_ore_dictionary/*` item/block tags now preserve all already registered 1.12 OreDictionary entries, including `plateIron`, `plateBrass`, `plateThaumium`, `plateVoid`, `stickWood`, `ingotGold`, `ingotBrass`, `ingotThaumium`, and `nitor` for current exact arcane recipes, plus lowercase `coal`, `dust_glowstone`, `ingot_iron`, and `nugget_quartz` bridges for current crucible page-data recipes. |
| Aspects | Core/API slice parity-clean for comparable stacks/entities, with two documented modern entity policy rows | `Aspect`, `AspectList`, pure `AspectHelper` logic, reload-safe data-driven exact/tag/manual assignments, vanilla material tag bridges, crafting and current arcane generated-cache slice, legacy stack-sensitive bonus rules, component-aware potion and enchanted-book lookup, spawn-egg exclusion, vanilla entity aspect assignments, bootstrap parity validation, server-data-load tag validation, OreDictionary-to-tag audit, `generateTags` audit, read-only Shift inventory tooltip rendering, and assignment/cache/manual-policy docs are implemented/documented. All assignable current `minecraft:*` item ids have aspects after reload validation; spawn eggs, firework star/rocket, infested blocks, and empty component-only potion carriers remain intentionally excluded for legacy runtime parity. Current registered Thaumcraft option items used by theorycraft now include dump-derived exact values for `alumentum`, `salis_mundus`, `brain`, `nitor_yellow`, `thaumium_ingot`, and `brass_ingot`. `filter` and `morphic_resonator` now reload-validate generated arcane recipe aspects, including the legacy `praecantatio` bonus from vis. `elder_guardian` and `zombie_villager` now have documented living-mob aspect rows as intentional modern-policy corrections. The runtime dump harness now runs on both original Forge 1.12.2 Thaumcraft and the 1.21.1 port; the comparers separate expected version differences from real port gaps. Current comparable item aspect parity is `1139/1139` with `0` amount/set/order/kind/null gaps; current comparable entity scan report has `83/85` fully parity-ok rows plus `2` expected modern entity aspect policy rows and `0` actionable gaps. Crucible recipe-page aspects are data/display costs only; crucible-derived `generateTags`, infusion, essentia transport, Thaumcraft custom entity aspects, and gameplay-heavy consumers remain blocked until their own design slices. |
| Aura | Started with server-side core | `AuraHelper`, per-level `SavedData`, chunk `base/vis/flux`, automatic chunk initialization, legacy formula for aura base generation, main-thread 20-tick legacy-like update loop, and permission-level-2 debug commands are implemented. The biome category mapper is legacy-like because 1.21 has no `BiomeDictionary`. HUD sync, FX, flux rifts, research-aware preservation, and gameplay consumers are intentionally not started. |
| Research | Progression/page-catalog core parity-closed; first real Thaumonomicon UI, vanilla, arcane, crucible and infusion page snapshots active | Player knowledge storage, reload-safe research data, requirements, scan-key mutation, table/theorycraft slice, GUI-ready knowledge sync, permanent research recipe/page catalog, server-authoritative revision-gated Thaumonomicon protocol, real Thaumonomicon item/open flow, browser, entry screen, vanilla crafting-page renderer, first arcane recipe page renderer, first crucible recipe page renderer, and infusion recipe page renderer exist. Checked stage completion preserves exact legacy ordering. Browser start preserves `first=true`/`checks=false`/`noFlags=true`, stage advance preserves `first=false`/`checks=true`/`noFlags=true`, and entry acknowledgement clears `RESEARCH`/`PAGE` before attempting the legacy known-entry final-stage checked progression with `noFlags=false`. The research data harness reports `148/148` entries and `10/10` semantic checks. The page catalog reports `253` research occurrences, `203` direct references, `325` total entries including group members, and `0` parity/structural differences; latest live availability is `113 READY`, `86 DEFERRED`, `4 LEGACY_MISSING`. The protocol audit passes `27/27`; stale client action revisions are rejected without progression mutation, and live vanilla crafting, arcane and crucible catalog entries produce valid server-resolved snapshots. Full warp events/effects/client sync, cancellable research/knowledge events, final visual/search parity, and the remaining blueprint/fake/special recipe-page systems/renderers remain blocked. |
| Recipes | Vanilla crafting fixtures, research bridges, arcane workbench path, crucible path, and infusion completion-readiness boundary started | Simple modern `data/thaumcraft/recipe` crafting recipes exist for generated-aspect validation and the first research table slice: `tablewood`, `tablestone`, `scribingtoolscraft2`, `scribingtoolsrefill`, and exact legacy `ironplate`, `brassplate`, and `thaumiumplate` recipes. The candle/tube/smelter-auxiliary cleanup keeps legacy recipe filenames where useful, but all active outputs now resolve to legacy public ids such as `candle_white`, `tube_buffer`, `tube_filter`, `tube_oneway`, `tube_restrict`, `tube_valve`, `smelter_aux`, and `smelter_vent`. Remaining `data/thaumcraft/recipe/research_bridge` recipes make registered/placeholder research requirement outputs craft-detectable for the modern `required_craft` marker path, but exact arcane recipes remove their obsolete bridges. The custom arcane boundary exists: `thaumcraft:arcane` `RecipeType`, `thaumcraft:arcane_shaped`/`thaumcraft:arcane_shapeless` serializers, public `IArcaneRecipe`, public `IArcaneWorkbench` marker, exact `thaumcraft:thaumometer`, exact `thaumcraft:vis_resonator`, exact `thaumcraft:workbenchcharger`, exact `thaumcraft:goggles`, exact `thaumcraft:mechanism_simple`, exact `thaumcraft:mechanism_complex`, exact `thaumcraft:wand_workbench` Focal Manipulator fixture, exact `thaumcraft:caster_basic`, exact `thaumcraft:enchantedfabric`, exact `thaumcraft:mirrorglass`, exact `thaumcraft:filter`, exact `thaumcraft:morphicresonator`, exact `thaumcraft:essentiasmelter`, and exact `thaumcraft:infusionmatrix` fixtures. `filter` uses direct `minecraft:gold_ingot` plus `thaumcraft:plank_silverwood`; `morphicresonator` uses `minecraft:glass_pane`, `c:plates/brass`, and exact `thaumcraft:rare_earth`; `essentiasmelter` uses the modern `c:cobblestones` bridge for the legacy cobblestone ingredient; `infusionmatrix` uses `thaumcraft:legacy_ore_dictionary/nitor` over all sixteen registered nitor block items. Arcane Workbench server crafting now has research/vis/crystal checks, no-charger current-chunk aura drain, Workbench Charger 3x3 aura query/drain, first player vis-discount cost path, atomic output-take consumption, crystal consumption, vanilla 3x3 fallback, missing-vis ghost output, and server-owned menu feedback for base/discounted cost, available aura and required crystal slots. Current `TCArcaneRecipe` outputs also feed the generated aspect cache with the exact legacy ingredient formula and vis-derived `praecantatio` bonus. The custom crucible page boundary now exists: `thaumcraft:crucible` `RecipeType`/serializer, catalyst ingredient, research key, explicit aspect costs, result stack, server page snapshot, codec/payload wiring, HEDGE_ALCHEMY, METAL_PURIFICATION, Alumentum, Nitor, Brass Ingot, Thaumium Ingot, and all 37 vis-crystal data recipes. Legacy dynamic HEDGE costs are explicit data now, and the crucible recipe data audit reports `77/77` valid files. Current crucible in-world slices use server-owned water/heat/aspect state, manual top-side item insertion, item-entity collision absorption, hot living contact damage, existing item-aspect dissolution, research-gated highest-cost recipe lookup, aspect/water consumption, result ejection, special-result reabsorption protection, comparator output, and legacy-style spill pollution into aura flux. The custom infusion boundary has `thaumcraft:infusion` `RecipeType`/serializer, `42/42` valid recipe JSON files, page snapshots, `TCInfusionRecipeMatcher`, `TCInfusionAssembly`, `TCInfusionValidationResult`, `TCInfusionCraftingPlan`, `TCInfusionCompletionPlan`, active pedestal blocks, one-slot pedestal BlockEntities, matrix legacy-range pedestal discovery, saved non-consuming active start state, read-only active-plan readiness checks, and a `25/25` runtime behavior audit. The current infusion matcher uses legacy-compatible exact-count unordered component semantics. Remaining arcane recipes, full equipment/Curios discount integration, flux rifts, taint spread, liquid death/special alchemy behavior, crucible-derived aspect generation, essentia/Thaumatorium integration, client crucible FX, infusion crafting mutation/consumption/instability/essentia/visuals are not implemented. |
| BlockEntities | Started narrowly | `TCResearchTableBlockEntity` stores the two legacy research-table slots: scribing tools and paper, implements the table `Container`, provides the menu opening data, saves/loads theory state under `note`, can finish theories into raw THEORY knowledge by category, and now sends vanilla update packets/tags so the table-top renderer can see slot changes. `TCArcaneWorkbenchBlockEntity` now stores the legacy 5x3 workbench inventory, exposes six fixed primal crystal slots, opens the menu server-side, saves/loads contents, drops contents on removal, and delegates no-charger current-chunk plus Charger 3x3 vis query/drain to the existing aura core. `TCCrucibleBlockEntity` now owns the current server-side crucible state boundary: water amount, heat, aspect pool, save/load, comparator value, manual catalyst/aspect insertion, legacy-style item entity absorption, special result reabsorption protection, and recipe result ejection. `TCInfusionPedestalBlockEntity` stores one item with legacy right-click insert/extract behavior; `TCInfusionMatrixBlockEntity` scans the legacy pedestal volume, feeds `TCInfusionAssembly` for validation, stores a saved `TCInfusionCraftingPlan` without consuming items/aspects, and builds a read-only `TCInfusionCompletionPlan` against current world/aspect state. Do not copy legacy `TileEntity` classes directly. |
| Menus/screens | Started with research table, Arcane Workbench, and first Thaumonomicon screens/pages | `TCResearchTableMenu` opens through `TCMenus.RESEARCH_TABLE`, exposes the two table slots plus the legacy-offset player inventory, and `TCResearchTableScreen` draws the legacy background with minimal functional theory controls. `TCArcaneWorkbenchMenu` opens through `TCMenus.ARCANE_WORKBENCH`, exposes the legacy result/matrix/crystal/player slot layout, validates crystal slots by aspect, resolves recipes server-side, syncs base/discounted cost, aura and crystal requirement feedback through menu data, exposes missing-vis arcane outputs as non-pickup ghost stacks, and consumes output through `TCArcaneWorkbenchCrafting`; `TCArcaneWorkbenchScreen` uses the legacy 190x234 background, legacy-positioned available/cost/discount text, missing-vis ghost output and required-primal crystal glows as a functional first pass. `TCThaumonomiconBrowserScreen` and `TCThaumonomiconEntryScreen` consume authoritative server view models and provide category graph navigation, pan/zoom, exact start/acknowledge/stage actions, translated stage text, requirements, bookmark availability, a legacy-style vanilla crafting paper page, and first arcane crafting paper page using legacy overlay UV/positions. Search, exact visual parity, recipe drilldown/history, final Arcane Workbench GUI polish, and remaining custom recipe pages remain incomplete. |
| Networking | Started narrowly | Modern custom payloads exist for aura sync, GUI-ready research knowledge sync, research-table action/state/result sync, and the Thaumonomicon index/entry/action flow. Thaumonomicon protocol version `4` carries separate vanilla crafting and arcane recipe page snapshots plus a server-built index revision; item use sends an explicit server-owned open intent; ordinary index refreshes cannot reopen the screen; stale client entry/action revisions force authoritative refresh without mutation. Research visibility, unlockability, requirements, flags and mutation remain server-owned. Do not treat this as a complete networking subsystem yet; every new payload still needs focused design and server validation. |
| Worldgen | Started early, not as a system | Sapling-grown Greatwood/Silverwood tree generators exist; biome modifiers, configured features, and structure/world placement are not implemented. |
| Rendering/FX | Started early, still high risk | Legacy-style FX dispatcher/particle scaffolding exists and `rendering_model_pipeline_audit.md` documents the 1.12 -> 1.21 resource/model split. Thaumometer right-click runes, held target highlight, and living-mob aspect icon overlay are started with legacy target ranges, wild block highlight behavior, legacy icon UV order, and separate known-vs-unknown gating. `TCResearchTableRenderer` now bakes the legacy `ModelResearchTable` parts for the scroll/tube/ribbon and inkwell, uses the legacy quill asset/transform, and renders those table-top objects only from synced BlockEntity state. Full rendering systems, BEWLR work, overlays, old shader wiring, and polished research-table/card animation must wait for focused validation. |

## Research/scanning stabilization split

| Bucket | Current contents | Notes |
|---|---|---|
| Ready | Aspect lookup parity harness, item scan parity harness, entity scan parity harness, reload-safe research JSON parsing, research reference validation, read-only scan commands, current Thaumometer scan-key mutation, GUI-ready knowledge sync payload/cache, first research table slice, first server-authoritative revision-gated Thaumonomicon item/open/browser/entry/vanilla-crafting-page flow, exact arcane recipe/page snapshots for `thaumcraft:thaumometer`, `thaumcraft:vis_resonator`, `thaumcraft:workbenchcharger`, `thaumcraft:goggles`, `thaumcraft:mechanism_simple`, `thaumcraft:mechanism_complex`, `thaumcraft:wand_workbench`/Focal Manipulator, `thaumcraft:caster_basic`, `thaumcraft:enchantedfabric`, `thaumcraft:mirrorglass`, `thaumcraft:filter`, `thaumcraft:morphicresonator`, `thaumcraft:essentiasmelter`, and `thaumcraft:infusionmatrix`, the first server-authoritative Arcane Workbench crafting/menu-feedback path for those exact recipes, current exact-arcane recipe-derived aspect generation, missing-vis ghost output, Workbench Charger 3 x 3 aura behavior, first player vis-discount service, the Arcane Workbench server behavior audit harness, infusion recipe/page data, and the non-consuming infusion matrix/pedestal start-plan plus completion-readiness runtime audit boundary. | Keep these covered by build/server/client smoke checks before expanding consumers. |
| Placeholder / Bridge | Remaining legacy requirement bridges for focus/vis and other unresolved requirement outputs, unfinished advanced theorycraft cards/aids, minimal warp storage/debug commands before real warp events, crucible/smelter/nitor behavior, infusion crafting mutation beyond the non-consuming start/readiness plan, hidden Golem Press block identity before full golem-builder multiblock logic, Brain-in-a-Jar block/entity behavior, Crimson portal entity behavior, Alumentum/Salis Mundus behavior, caster, mirror, curio, zombie brain behavior, OreDictionary chest and flattened vanilla metadata. The current XP/phial/vanilla-aid/Golemancy/table-inventory/Artifice/basic-Auromancy/basic-Infusion/basic-Golemancy/safe-Eldritch theory cards are still bridge-level gameplay until the full table UI/reward loop is finished. Research bridge recipes, generated parity reports, and debug-only research completion commands remain bridge/deferred tooling. | These are scaffolds for validation and migration. Do not treat them as final gameplay, reward, recipe, or subsystem behavior. |
| Blocked | Thaumonomicon final search/visual parity, recipe drilldown/history, final Arcane Workbench GUI polish, full equipment/Curios vis-discount integration, remaining arcane recipes, infusion altar crafting mutation/rendering/instability/essentia, blueprint/fake recipe systems/renderers, full warp events/effects/client sync, cancellable research/knowledge events, exact direct `required_craft` legacy hash mapping, essentia container behavior/filling/draining, custom Thaumcraft entities, caster/mirror behavior, and ScanSky celestial-note side effects. | These need their own focused design/validation slices. |
| Next subsystem | Continue infusion with an atomic component-consumption and aspect/essentia-source mutation executor over the audited completion plan, or continue exact arcane recipe expansion by audited dependency family if infusion prerequisites are not ready. | Do not add instability, beams, particles, sounds or broad infusion visuals until the consumption/drain executor has its own green audit. |

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

The copied-file manifest is `06_docs/resources/asset_bulk_import_manifest.txt`.

The runtime asset audit is `06_docs/resources/runtime_asset_audit.md`.

## Last local validation

| Check | Result | Notes |
|---|---|---|
| `.\gradlew.bat build --no-daemon` | Passed | Latest infusion completion-readiness boundary batch builds successfully. Re-run after each recipe, UI, menu, networking, gameplay or asset expansion batch. |
| `.\gradlew.bat runClient --no-daemon` | Startup/resource smoke reached client resource reload and atlas creation | Client reached mod bootstrap, resource loading, sound engine startup, and texture atlas creation. The process was stopped intentionally after the smoke window, before world-join gameplay testing. Latest reviewed log had no Thaumcraft `ERROR`, `FATAL`, `Exception`, missing texture/model, or file-not-found signals. Remaining warnings were vanilla/NeoForge asset URL schema noise, missing vanilla goat horn sounds, and one vanilla shader sampler warning. |
| `tools/ci/server-smoke.ps1 -TimeoutSeconds 420 -WorldName tc_server_smoke_infusion_completion_plan -ServerPort 0 -KillStaleRunServer` | Startup/reload smoke reached `Done` | Dedicated server reached `Done`; bundled bootstrap, data-resource assignment reload, generated crafting/arcane cache rebuild, tag reload validation, research data reload, aura event/command registration, automatic aura chunk initialization, and the client-only tooltip registration boundary all passed. Latest smoke loaded `687` exact assignments, `46` tag assignments, `32` complex exact assignments, rebuilt `640` generated cache entries, loaded `7` research categories / `148` entries / `271` stages / `16` addenda, and reported `1230 of 1230` assignable minecraft item ids with non-empty aspects. Latest full audit checkpoint also passed the page catalog audit with `113 READY` / `86 DEFERRED` / `4 LEGACY_MISSING`, the Thaumonomicon protocol audit `27/27`, the arcane recipe audit `105/105`, the Arcane Workbench behavior audit `23/23`, the current Crucible behavior audit `12/12`, and the current Infusion behavior audit `25/25`. |
| Arcane Workbench behavior audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcArcaneWorkbenchAudit=true "-PtcArcaneWorkbenchAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\arcane_crafting\thaumcraft_1_21_arcane_workbench_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `23/23` checks validate distinct Arcane Workbench and Focal Manipulator (`wand_workbench`) identities, Workbench Charger support placement, empty resolution, fixed primal crystal slots, vis simulation, no-charger current-chunk aura, Charger 3 x 3 aura query/drain, missing-research fallback, missing/wrong crystal blocking, missing-vis blocking plus non-pickup/non-craftable ghost output, successful `vis_resonator` resolution, matrix/crystal/vis consumption, 5% goggles discount cost/drain behavior, vanilla `iron_plate` fallback consumption, and server-owned menu feedback for arcane cost/aura, discounted cost, missing vis, missing crystals, and vanilla fallback. |
| Infusion behavior audit exporter | Passed | `tools/audits/audit-infusion-behavior.ps1` reached `Done`, wrote `tools/reports/local/infusion/thaumcraft_1_21_infusion_behavior_audit.md`, and stopped automatically. Current `25/25` checks validate 42 loaded infusion recipes, CLOUDRING data shape, direct validation, unordered components, exact 1:1 component-count failure for extra pedestal inputs, missing-aspect failure, wrong-catalyst failure, player-context research gating, runtime matrix/pedestal BlockEntity creation, legacy center/surrounding pedestal discovery, world snapshot validation, active start-plan creation, recorded legacy fields, component pedestal positions, NBT round-trip, read-only completion-plan readiness, missing-aspect readiness rejection, changed-catalyst rejection, changed-component rejection, missing-component-pedestal rejection, second-start rejection, abort clearing, extra filled pedestal mismatch, and missing central pedestal failure. |
| Infusion recipe data audit | Passed | `tools/audits/audit-infusion-recipe-data.ps1` reports `42` infusion recipe files and `0` invalid files. |
| Research recipe page gap audit | Passed | `tools/audits/audit-research-recipe-page-gaps.ps1` reports `253` stage/addendum recipe page refs, `238` resolved recipe refs, and `0` missing recipe page refs. |
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
| Other blocks/items | `amber_block`, `amber_brick`, `table_wood`, `table_stone`, `research_table`, `arcane_workbench`, `arcane_workbench_charger`, `wand_workbench`, `golem_builder`, `smelter_basic`, `smelter_thaumium`, `smelter_void`, `tube`, `tube_buffer`, `tube_filter`, `tube_oneway`, `tube_restrict`, `tube_valve`, `infusion_matrix`, `arcane_pedestal`, `ancient_pedestal`, `eldritch_pedestal`, all registered `nitor_*` and `candle_*` color variants, `thaumometer`, `vis_resonator`, `goggles`, `caster_basic`, `mirrored_glass`, `amber`, `quicksilver`, `fabric`, `salis_mundus`, `alumentum`, `rare_earth`, `filter`, `morphic_resonator`, `iron_plate`, `brass_plate`, `thaumium_plate`, `void_plate`, `mechanism_simple`, `mechanism_complex`, placeholder `smelter_aux`, and placeholder `smelter_vent` |
| Research/progression bridge identities | Thaumium/brass materials, aspect crystal essence variants, phial variants, stored-enchantment requirements and legacy metadata-family requirements now carry component-level semantics for requirement matching. Scribing tools and research table now have their first legacy-backed storage/conversion slice, `arcane_workbench` has its first server-owned exact-recipe crafting path, `arcane_workbench_charger` has its first Workbench 3 x 3 aura behavior and exact recipe, `mechanism_simple`/`mechanism_complex` have exact Artifice arcane recipes and page snapshots, `filter` and `morphic_resonator` have exact Basic Alchemy arcane recipes and page snapshots, `wand_workbench` is the TC6 Focal Manipulator id and now has its exact legacy arcane recipe and page snapshot, `golem_builder` exists as the hidden Golem Press identity for Basic Golemancy aid detection, and Alumentum/Salis Mundus exist as identity/requirement items for theorycraft, but their real behavior is not implemented. Focal Manipulator block behavior, crucible/smelter/infusion matrix blocks, filter/resonator behavior, focus/caster/vis placeholder items, full equipment/Curios discount integration, Golem Press multiblock/GUI/essentia logic, special thaumium tool behavior, curio, mirror and brain behavior remain subsystem bridges until their behavior slices are implemented. |

## Aspect assignment data resources

The current aspect assignment source of truth is split across bundled data files under `05_neoforge_port/src/main/resources/data/thaumcraft/aspect_assignments/`.

| Assignment layer | Count | Notes |
|---|---:|---|
| Exact item assignments | `687` | `current_registered.json` covers normally authored registered Thaumcraft ids, including exact `rare_earth` (`terra 5`, `ordo 5`, `metallum 5`) for the Morphic Resonator fixture; `current_registered_runtime_parity.json` preserves dump-derived registered Thaumcraft final values, including the registered `thaumonomicon`, `thaumometer`, `table_wood`, `table_stone`, `research_table`, `alumentum`, `salis_mundus`, `brain`, `nitor_yellow`, `thaumium_ingot`, `brass_ingot`, `brass_plate`, `mechanism_simple`, and `mechanism_complex`; `legacy_vanilla_core.json` and `legacy_vanilla_modern_exact.json` preserve direct vanilla seeds; `legacy_vanilla_modern_manual.json` covers 1.21-only vanilla ids by audited Thaumcraft-style category; `legacy_vanilla_runtime_parity.json` preserves dump-derived final 1.12 values for shared plain vanilla stacks affected by metadata flattening, generated recipes, complex extras, wildcard specificity, or stack bonuses. Spawn eggs, firework star/rocket, and infested blocks are excluded because 1.12 gave those comparable stacks no aspects. |
| Item tag assignments | `46` | Includes safe current common `c:` tags, vanilla material bridges for legacy ore/gem/ingot/dust/base-block keys, ore-derived 1.21 raw materials, `blockGlass`, plus exact `thaumcraft:legacy_ore_dictionary/*` compatibility tags where legacy used string-key aspect assignments. |
| Complex exact assignments | `32` | Current complex extras cover audited buckets, boats, doors, fence gates, and related legacy complex additions. Runtime diff proves this layer must continue to be source-vs-runtime reviewed before broad expansion because legacy exact/generated/wildcard lookup order can mask wildcard complex values. |
| Generated crafting assignments | `634` | Built after server data/tag reload from current `RecipeType.CRAFTING` recipes and current `thaumcraft:arcane` recipes for `minecraft:*` and `thaumcraft:*` outputs that have known ingredient aspects. Exact/tag/manual/runtime-parity assignments still win over generated values. The current count includes remaining safe research bridge recipes used to make modern `required_craft` markers observable, exact normal plate recipes, and current arcane outputs without stronger exact assignments; bridge recipes are not final gameplay implementations. Crucible page-data recipes intentionally do not feed this generated-aspect cache yet. |

## Generated aspect recipe cache

`TCAspectStackKey`, `TCGeneratedAspectCache`, and `TCGeneratedAspectRecipeGenerator` define the current generated-aspect cache boundary. The key uses item registry id plus the stack data component patch and ignores stack count, matching the legacy normalization intent. The cache is cleared on aspect assignment bootstrap/reload, then rebuilt after server data/tag reload from loaded vanilla crafting and current arcane recipes.

The normal crafting slice does not depend on whether a recipe is crafted from the 2x2 inventory grid or the 3x3 crafting table; it scans `RecipeType.CRAFTING`, and each recipe's own dimensions decide where it can be crafted. The current arcane slice scans the custom `thaumcraft:arcane` recipe type and adds the legacy `praecantatio` bonus from `vis` after the ingredient formula.

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
- current arcane recipe-derived outputs match the legacy ingredient formula plus `sqrt(1 + vis / 2) / output count` `praecantatio` bonus for `filter` and `morphic_resonator`.

## Current gate interpretation

| Gate | Current interpretation |
|---|---|
| Gate 0 | Complete enough to continue, but still validate `runServer` after client/render changes. |
| Gate 1 | In progress and expanded beyond the first simple item batch. Active registered item resources are covered; creative order still needs visual review. |
| Gate 2 | Started early through simple block and block item identity work. Active registered blockstates, models, loot tables, and translations are covered. |
| Gate 3 | Implementation started carefully. Exact legacy core aspect definitions/list/helper logic, current registered-id assignments, generated crafting cache, vanilla entity aspect lookup, read-only Shift tooltip rendering, and scan-resolved aspect lookup are present and guarded by validation; gameplay-heavy consumers remain blocked. |
| Gate 4+ | Aura is started as an isolated server-side storage/API slice after `aura_design.md`; the first research-table BlockEntity/menu/screen boundary exists; the first custom arcane recipe and Arcane Workbench server crafting boundary exists, including Workbench Charger 3 x 3 aura use and the first player vis-discount path; the first crucible recipe serializer/page snapshot boundary and current manual/collision/spill in-world crucible behavior slices exist; the first infusion recipe/page plus non-consuming matrix/pedestal start-plan and completion-readiness boundary exists. Capabilities, broad machine networks, full alchemy side effects, full infusion altar crafting mutation/instability/FX, broad networking, worldgen, and large rendering systems remain not started. |

## Partially stale documents

| Document | Stale part | Current handling |
|---|---|---|
| `gate1_items_plan.md` | Lists only `amber`, `quicksilver`, and `fabric` as the first implemented slice. | Keep as workflow guidance; use this file for actual inventory. |
| `creative_tab_order_reference.md` | First implemented entries section no longer reflects all implemented entries. | Keep policy; status should point here. |
| `migration_matrix.md` | Matrix now distinguishes imported legacy assets from active adapted resources. | Keep matrix for policy and gate sequencing; use this file for live implementation status. |
| `block_parity_audit.md` | Refreshed for sapling/tree and block property updates. | Still requires exact legacy parity checks before behavior tuning. |

## Do not start without a design note

Do not implement or expand aura, research, arcane crafting, crucible/alchemy, infusion, BlockEntities, menus, networking, worldgen, or large rendering systems by direct legacy class copying. Each one needs a small design document first. Current arcane crafting work is limited to the documented exact-recipe/Arcane Workbench server slice; current crucible work is limited to the documented manual/collision/spill in-world behavior boundary and must not grow into flux rifts, taint spread, item pulling radius, special alchemy, essentia networks or client particle parity without a new focused slice; current aspect work is limited to the documented data layer and read-only inventory tooltip consumer.

## Immediate next work

1. Re-run `./gradlew build --no-daemon` after every arcane workbench GUI, discount, charger, or recipe expansion batch.
2. Keep the mapped aspect diff report at `0` real `PORT_GAP_*` buckets before treating current coverage as safe for gameplay consumers.
3. Use the next full `./gradlew runClient --no-daemon` visual pass to inspect creative tab order, active item icons, Shift-held aspect tooltip visuals, and the 3D Thaumometer OBJ transforms in GUI/hand views.
4. Compare creative tab order with the 1.12.2 inventory screenshots.
5. Add future registered item/block aspect values through `data/thaumcraft/aspect_assignments`; entity aspects currently use a legacy Java table until an entity assignment datapack format is designed.
6. Keep vanilla item coverage at `0 missing` after every aspect/tag change; do not broaden third-party modded generated outputs until an addon policy exists.
7. For new arcane or crucible behavior families, add validation coverage alongside the import so the recipe/page/data path cannot silently accept wrong ingredient mapping; keep crucible recipe-derived aspect generation and infusion recipe-derived generation blocked until their machines and ingredient mappings exist.
8. For the next crucible batch, audit legacy alchemy side-effect calls before wiring flux rifts, taint spread, liquid death, Thaumatorium, alembic, jar, tube or client FX behavior.
8. Continue vanilla aspect changes only from `ConfigAspects`, audited legacy OreDictionary-to-tag bridges, recipe-derived cache behavior, or documented 1.21-only category policy.
9. Keep parity validation and reload validation passing before expanding aspect consumers.
10. Do not expand aura beyond saved-data/query/debug-command/autogenerated chunk state, and do not begin essentia, broad GUI, broad networking, crafting costs, or gameplay-heavy systems without their own design notes.
11. Keep the research data parity harness at `0` source/runtime/category differences and all progression checks passing after every parser or progression change.
12. Keep the permanent research recipe/page catalog at exact parity before extending Thaumonomicon rendering; do not treat legacy `stages[].recipes` as simple vanilla recipe unlock ids.
13. Keep the research table diagnostic harness passing after every `TCResearchTableData`, card, aid, table menu, or action payload change.
14. Visually check vanilla/basic aid selection, table-top scroll/inkwell/quill placement, and the first Arcane Workbench screen in `runClient`; then port remaining theorycraft cards/aids or arcane recipes only by audited dependency family. Advanced cards that require full warp effects, curios, focus/caster, infusion, celestial notes, portals, or other unported systems must stay deferred or explicitly bridged.
15. For Arcane Workbench, keep player vis discount, Workbench Charger 3 x 3 aura behavior, and missing-vis ghost output covered by the current audits before importing more recipes. Current base/discounted cost, aura, crystal-slot GUI feedback, and non-pickup ghost output are server-owned and covered by the workbench audit; final visual tuning remains a separate GUI polish task.
16. For Infusion, keep `audit-infusion-recipe-data.ps1` and `audit-infusion-behavior.ps1` green before adding atomic crafting completion. Preserve legacy exact-count unordered component matching; do not consume items/aspects or trigger instability/FX until the next focused consumption/drain slice has its own plan and audit.

## Local validation commands

From `05_neoforge_port`:

```powershell
.\gradlew.bat build --no-daemon
.\gradlew.bat runClient --no-daemon
.\gradlew.bat runServer --no-daemon
.\gradlew.bat runServer --no-daemon -PtcArcaneRecipeAudit=true "-PtcArcaneRecipeAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\arcane_crafting\thaumcraft_1_21_arcane_recipe_audit.md"
.\gradlew.bat runServer --no-daemon -PtcArcaneWorkbenchAudit=true "-PtcArcaneWorkbenchAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\arcane_crafting\thaumcraft_1_21_arcane_workbench_audit.md"
.\gradlew.bat runServer --no-daemon -PtcResearchPageCatalogAudit=true "-PtcResearchPageCatalogAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_recipe_catalog\thaumcraft_1_21_research_recipe_catalog.md"
.\gradlew.bat runServer --no-daemon -PtcThaumonomiconProtocolAudit=true "-PtcThaumonomiconProtocolAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_recipe_catalog\thaumcraft_1_21_thaumonomicon_protocol_audit.md"
.\gradlew.bat runServer --no-daemon -PtcResearchTableAudit=true "-PtcResearchTableAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_table_audit\thaumcraft_1_21_research_table.md"
```

## Research, knowledge and scanning design

Started:
- Added design document for player knowledge, research commands and scanning.
- Added legacy source audit for CommandThaumcraft, PlayerKnowledge, IPlayerKnowledge, ResearchManager, ScanningManager and scan predicate classes.
- Knowledge storage, commands, progression, page catalog, and the server-authoritative Thaumonomicon protocol are implemented.

The server-authoritative vanilla crafting-page view model, custom arcane recipe type, and first real renderer for catalog pages that are actually `READY` are implemented. The current research/crafting boundary is audited exact arcane recipe expansion by dependency family; deferred page contents must not be invented.
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
- Thaumonomicon search/final visual parity, exact direct craft-reference hash parity, custom recipe-page renderers, full warp events/effects/client sync, and cancellable research/knowledge events. The real item/open/browser/entry flow, permanent page catalog and server-authoritative index/entry/action protocol are implemented; entry rewards and addendum notifications are implemented, but built-in TC6 data does not provide a real reward integration fixture.
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
- Design and implement the first real custom recipe type before its authoritative page snapshot/renderer; do not duplicate recipe, visibility, requirement, or page-catalog decisions on the client.

## Changelog Notes

The sections below are historical update notes. They should not be read as the current task queue; use `06_docs/CURRENT_TASK.md` for current priorities and the `State Snapshot` plus `High-level status` sections above for current state.

### Latest server smoke hardening update

- The dedicated server smoke log-quality gate now uses case-sensitive log severity markers instead of a broad generic ERROR regex, avoiding false positives from DEBUG dependency paths such as rror_prone_annotations.
- Datapack, recipe, tag, invalid resource path, crash and startup failure markers remain hard failures. -FailOnWarnings remains opt-in for exact WARN-level markers.

### Latest server smoke stale-lock preflight update

- Dedicated server smoke checks run/world/session.lock before starting runServer.
- If a local stale Java or Gradle process still owns the world lock, smoke fails early with matching process hints instead of a long Minecraft DirectoryLock stacktrace.

### Latest server smoke stale-process cleanup update

- Dedicated server smoke now supports -KillStaleRunServer for local runs.
- With that switch, smoke stops only matching Java or Gradle runServer and NeoForge devlaunch processes from this repository before retrying the world session-lock check.
- CI remains conservative by default; the switch is intended for local developer runs where a previous smoke left run/world/session.lock held.

### Latest CI server smoke properties update

- Dedicated server smoke now pre-seeds run/server.properties before launching runServer.
- This prevents the clean CI workspace first-run message Failed to load properties from file: server.properties from being logged as a Minecraft ERROR and tripping the strict log-quality gate.
- Build workflow artifact upload already includes 05_neoforge_port/build/ci-logs/** for smoke diagnostics.

### Latest custom recipe boundary audit update

- Added tools/audits/audit-custom-recipe-boundary.ps1 and 06_docs/audits/custom_recipe_boundary_audit.md.
- The audit scans current recipe JSON, research recipe-like references, missing recipe references, and custom recipe keywords to separate READY data recipes from custom behavior that still needs a serializer, page, or behavior design slice.
- Next custom recipe work should use this audit to pick the largest safe target without copying legacy crucible, infusion, fake, blueprint or special recipe classes directly.

### Latest research recipe page gap audit update

- Added tools/audits/audit-research-recipe-page-gaps.ps1 and 06_docs/audits/research_recipe_page_gap_audit.md.
- This audit narrows the broad custom recipe boundary scan to actual Thaumonomicon stage/addendum recipe page references and separates them from icons, required_item, and required_craft gates.
- Use the missing recipe page class distribution to choose the next large serializer/page implementation slice without conflating item requirements with recipe pages.

### Latest legacy alchemy recipe source audit update

- Added tools/audits/audit-legacy-alchemy-recipe-sources.ps1 and 06_docs/audits/legacy_alchemy_recipe_source_audit.md.
- This audit traces the dominant alchemy/crucible/special recipe page gaps back to the local legacy source corpus before implementation.
- Next alchemy work should implement only a recipe data model, serializer, loader audit, and Thaumonomicon page snapshot for the selected family before any crucible or machine behavior.

### Latest hedge alchemy recipe extraction update

- Added tools/audits/extract-legacy-hedge-alchemy-recipes.ps1 and 06_docs/audits/hedge_alchemy_legacy_recipe_blocks.md.
- This extraction captures exact legacy CrucibleRecipe source blocks for the dominant HEDGE_ALCHEMY page-gap family before writing any new NeoForge serializer or page renderer.
- The next implementation slice can use this document to add the first crucible recipe data/page boundary while keeping in-world crucible behavior deferred.

### Latest crucible recipe page boundary update

- Added a first loader/page boundary for thaumcraft:crucible recipes without implementing in-world crucible behavior.
- Added the HEDGE_ALCHEMY legacy crucible recipe family as data recipes and research-page catalog entries using the extracted legacy ResourceLocation ids.
- The first crucible Thaumonomicon page snapshot carries result, catalyst variants, research key, and explicit aspect display stacks. Dynamic AspectList-derived HEDGE legacy costs were initially deferred as empty placeholders, but are now resolved into explicit data from the current parity aspect assignments.

### Latest post-HEDGE recipe page audit refresh

- Refreshed custom recipe boundary, research recipe page gap, and legacy alchemy source audits after the HEDGE_ALCHEMY crucible recipe page boundary landed.
- Use the refreshed missing recipe page class and research-file distributions as the source of truth for the next large family-level recipe/page batch.
- The next batch should target the largest remaining family, not individual recipe ids, unless an audit shows a family needs a separate data-model boundary.

### Latest remaining alchemy recipe extraction update

- Added tools/audits/extract-remaining-alchemy-recipes.ps1 and 06_docs/audits/remaining_alchemy_legacy_recipe_blocks.md.
- This extraction captures exact legacy recipe source blocks for remaining non-HEDGE alchemy recipe-page gaps after the first crucible boundary batch.
- Use the extracted API kind and family distribution to choose the next broad alchemy batch without mixing crucible page recipes with infusion or machine behavior.

### Latest crucible aspect alias fix

- Fixed the first crucible recipe boundary to accept legacy Aspect enum names in recipe JSON by canonicalizing them to the port's active aspect tags.
- This maps legacy names such as fire, air, earth, beast, magic, order and entropy to modern tags such as ignis, aer, terra, bestia, praecantatio, ordo and perditio.
- This keeps current HEDGE_ALCHEMY JSON compatible and reduces future generated alchemy recipe batch risk because legacy source extraction reports Aspect enum names.

### Latest metal purification crucible recipe page update

- Added the METAL_PURIFICATION alchemy family as crucible recipe/page data for iron, gold, copper, tin, silver, lead and cinnabar.
- Added legacy cluster bridge item identities for the old ItemsTC.clusters metadata outputs.
- Added optional legacy ore dictionary catalyst tags so current vanilla/Thaumcraft ores resolve where available while absent legacy-only metal ores remain safe placeholders.

### Latest post-metal-purification audit refresh

- Refreshed recipe/page and alchemy source audits after the METAL_PURIFICATION crucible recipe/page batch.
- Use the refreshed counts to select the next safe alchemy family-level batch.
- Prefer small pure crucible families next, and avoid mixed ALCHEMY_OTHER entries until their false positives and machine/page boundaries are separated.

### Latest base alchemy/metallurgy crucible recipe page update

- Added current pure crucible page-data recipes for Alumentum, Nitor, Brass Ingot, Thaumium Ingot, and all 37 vis-crystal essence variants.
- Added lowercase legacy OreDictionary catalyst tags for `coal`, `dust_glowstone`, `ingot_iron`, and `nugget_quartz` so current recipes avoid invalid uppercase legacy ResourceLocations.
- Refreshed research recipe page and custom recipe boundary audits. Current catalog audit now reports `113 READY`, `86 DEFERRED`, `4 LEGACY_MISSING`; the Thaumonomicon protocol audit passes `27/27`.
- Fixed a malformed generated `brassingot` aspect list before accepting the batch; dedicated server smoke now reaches `Done` and reloads `1558` recipes without datapack/recipe errors.
- Full in-world crucible behavior, essentia/alchemy side effects, item-entity suction, flux/taint effects and crucible-derived generated aspect behavior remain deferred until their own design/validation slice.
### Latest special alchemy crucible page batch

- Added bridge identities and crucible recipe/page entries for Bath Salts, Bottled Taint, Liquid Death, and Sane Soap.
- These recipes preserve the legacy research/page ids as canonical lowercase recipe ids and keep full gameplay/fluid/block behavior deferred.
- `liquid_death_bucket` and `flesh_block` are bridge item identities for recipe/page display until their dedicated gameplay blocks/fluids are ported.

### Latest post-special-alchemy audit refresh

- Refreshed recipe/page and alchemy source audits after the SPECIAL_ALCHEMY crucible recipe/page batch.
- Use the refreshed counts to confirm the special alchemy gap is closed and select the next safe batch.
- If alchemy crucible/special gaps are closed, avoid mixed ALCHEMY_OTHER entries until EverfullUrn, JarLabelEssence, and Thaumatorium are separated into proper item/page/machine boundaries.

### Latest golemancy boundary source audit

- Added a dedicated source audit for current GOLEMANCY_PAGE_DEFERRED references.
- This audit is analysis-only and is meant to separate real recipe/page work from seal, machine, and behavior boundaries before implementation.
- Next golemancy work should choose a narrow family from this audit instead of implementing all deferred references as recipes.

### Latest golemancy boundary source audit repair

- Rebuilt `golemancy_page_boundary_source_audit.md` with a stricter extractor that fails if no GOLEMANCY_PAGE_DEFERRED references are found or if the output is unexpectedly small.
- Use the repaired audit to select the next narrow golemancy implementation batch.

### Latest focused golemancy recipe source candidate audit

- Added `golemancy_recipe_source_candidates.md`, a filtered audit that extracts likely `ConfigRecipes.java` recipe blocks for current GOLEMANCY_PAGE_DEFERRED references.
- Use this before selecting the first golemancy implementation batch; avoid broad seal behavior until recipe candidates are exhausted.
### Latest golemancy seal crucible page batch

- Added bridge item identities and crucible recipe/page entries for base and advanced golem seals that are data-driven in legacy crucible recipes.
- Real golem seal behavior remains deferred; this batch only makes the recipe/page identities visible and loadable.
- Infusion-based seals, JarBrain, MindBiothaumic, and GolemPress remain out of scope for this batch.
### Latest golemancy seal crucible repair note

- Repaired the first golemancy seal crucible batch after dedicated server smoke failed during TCItems static initialization.
- The repair removes duplicate item registrations from the added seal bridge block when an item id was already registered earlier.

### Latest post-golemancy-seals audit refresh

- Refreshed recipe/page and alchemy source audits after the GOLEMANCY_SEAL_CRUCIBLE recipe/page batch.
- Use the refreshed counts to confirm the golemancy seal crucible page gap reduction and select the next safe batch.
- If golemancy seal crucible gaps are closed, keep infusion-based seals, JarBrain, MindBiothaumic, and GolemPress separated into their own boundary batches.
### Latest focused infusion recipe source candidate audit

- Added `focused_infusion_recipe_source_candidates.md`, extracting legacy `addInfusionCraftingRecipe` blocks for missing infusion-related research page references.
- This prepares the next architecture slice: a modern infusion recipe/page boundary without in-world infusion crafting behavior.
### Latest infusion recipe page boundary slice

- Added 	haumcraft:infusion recipe type/serializer and Thaumonomicon recipe-page view plumbing.
- This is a data/page boundary only; in-world infusion altar gameplay remains deferred.
- No infusion JSON recipe batch is added in this slice.
### Latest golemancy first infusion recipe page batch

- Added the first small infusion recipe/page JSON batch after introducing the 	haumcraft:infusion boundary.
- Covered JarBrain, MindBiothaumic, SealBreak, SealButcher, and SealHarvest as data/page entries only.
- Real infusion altar gameplay and golem seal behavior remain deferred.

### Latest post-golemancy-infusion audit refresh

- Refreshed recipe/page and alchemy source audits after the FIRST_GOLEMANCY_INFUSION recipe/page batch.
- Use the refreshed counts to confirm the first golemancy infusion page gap reduction and select the next safe batch.
- If first golemancy infusion gaps are closed, GolemPress should remain separated as a machine/block boundary.
### Latest utility infusion recipe page batch

- Added first non-golem utility infusion recipe/page entries: BootsTraveller, CLOUDRING, and CHARMUNDYING.
- These remain data/page boundaries only; in-world infusion altar behavior is still deferred.

### Latest post-utility-infusion audit refresh

- Refreshed recipe/page and alchemy source audits after the FIRST_UTILITY_INFUSION recipe/page batch.
- Use the refreshed counts to confirm the first utility infusion page gap reduction and select the next safe batch.
- If first utility infusion gaps are closed, continue with another small infusion JSON batch instead of broad fake/synthetic pages.
### Latest elemental tool infusion recipe page batch

- Added elemental tool infusion recipe/page entries for axe, pick, sword, shovel, and hoe.
- Audit refresh is integrated into this batch script by default, after build/smoke and before commit.
- These remain data/page boundaries only; in-world infusion altar behavior is still deferred.
### Latest fortress mask infusion recipe page batch

- Added fortress mask infusion recipe/page entries for Grinning Devil, Angry Ghost, and Sipping Fiend masks.
- Added a bridge item identity for the Thaumium Fortress Helm catalyst; full fortress armor behavior remains deferred.
- Audit refresh is integrated into this batch script by default, after build/smoke and before commit.
### Latest fortress armor infusion recipe page batch

- Added Thaumium Fortress helm, chestplate, and leggings infusion recipe/page entries.
- Added bridge item identities for thaumium armor catalysts where needed.
- Audit refresh is integrated into this batch script by default, after build/smoke and before commit.
### Latest verdant charm infusion recipe page batch

- Added Verdant Heart, Verdant Heart of Life, and Verdant Heart of Sustenance infusion recipe/page entries.
- Potion-specific legacy ingredients are represented by broad potion item placeholders until richer item/NBT ingredient handling is implemented.
- Audit refresh is integrated into this batch script by default, after build/smoke and before commit.
### Verdant charm infusion repair

- Added the missing auble_charm bridge item used by the VerdantHeart infusion catalyst.
- This fixes the server smoke datapack parse error for 	haumcraft:verdantheart.
### Latest crystal cluster recipe page batch

- Added 7 crystal cluster crafting recipe/page entries for primal and flux crystal clusters.
- This is a larger integrated batch and includes audit refresh after build/smoke.
### Crystal cluster recipe repair

- Rewrote crystal cluster recipe JSONs to use explicit non-empty item ids.
- The previous generated files accidentally used blank item values because of PowerShell hashtable property access.
### Latest simple legacy page recipe batch

- Added larger mixed recipe/catalog batch for arcane stone, arcane brick, Curiosity Band, and Helm Goggles.
- This targets the remaining simple INFUSION_RESEARCH_LEGACY_PAGE_KEY items without touching altar/block behavior.
- Audit refresh is integrated after build/smoke.
### Latest auromancy focus recipe page batch

- Added data-driven recipe/catalog entries for focus_1, focus_2, focus_3, and VisAmulet.
- focus_1 is a conservative crucible page boundary because focused legacy source extraction has no direct source block for it yet.
- This script integrates audit refresh after successful build/smoke.
### Latest eldritch infusion recipe page batch

- Added data-driven infusion JSON/catalog entries for PrimalCrusher, VoidRobeHelm, VoidRobeChest, VoidRobeLegs, and VoidseerPearl.
- Deliberately left voidingot and VoidSiphon for separate follow-up because source classification is ambiguous/block-oriented.
- This script integrates audit refresh after successful build/smoke.
### Latest artifice behavior page recipe batch

- Added recipe/page boundary entries for ArcaneBore, InfernalFurnace, LampFertility, LampGrowth, Mirror, MirrorEssentia, and MirrorHand.
- This is still page/data boundary only; actual machine/block behavior remains deferred.
- This script integrates audit refresh after successful build/smoke.
### Latest remaining non-fake recipe page batch

- Added page-boundary recipes/catalog entries for EverfullUrn, JarLabelEssence, Thaumatorium, voidingot, VoidSiphon, CausalityCollapser, and nitorcolor.
- These are conservative recipe/page placeholders for remaining non-fake missing references; gameplay behavior remains deferred where appropriate.
- This script integrates audit refresh after successful build/smoke.
## Remaining non-fake page recipe repair note

- Registered alchemical_construct, essentia_importer, and essentia_exporter bridge item ids used by thaumatorium.json.
- This repairs the previous server smoke datapack parse failure and keeps the same batch commit target.
### Latest blueprint page placeholder batch

- Added explicit blueprint/page placeholder recipes and catalog entries for infusion altar variants and GolemPress.
- These are not gameplay multiblock/machine implementations; they only close recipe page references.
- This script integrates audit refresh after successful build/smoke.
## Blueprint page placeholder repair note

- Registered arcane_pedestal, ancient_pedestal, and eldritch_pedestal bridge item ids used by infusion altar placeholder recipes.
- This repairs the previous server smoke datapack parse failure and keeps the same batch commit target.
## Blueprint placeholder audit finalize note

- The blueprint placeholder batch removed the last GOLEMANCY_PAGE_DEFERRED reference.
- The golemancy page-boundary extractor is now skipped when that class is absent from the page-gap audit.
## Synthetic recipe page audit classification

- FAKE_OR_SYNTHETIC_PAGE references are now reported separately from actionable missing recipe pages.
- This preserves the list of synthetic teaching/UI placeholders while allowing actionable recipe page gaps to reach zero.
## Infusion page boundary audit classification

- Custom recipe boundary audit now treats thaumcraft:infusion as INFUSION_PAGE_READY_NO_GAMEPLAY.
- This reflects the implemented infusion serializer/page snapshot boundary while keeping in-world infusion altar behavior deferred.
## Crucible gameplay boundary design note

- Added a focused design document for the first in-world crucible behavior slice.
- Added a crucible recipe data audit to validate catalyst/aspects/result shape before gameplay activation.
- The first manual in-world crucible behavior checklist is implemented; full alchemy side effects and automation remain deferred.
### Latest crucible behavior validation hardening

- Resolved the seven legacy dynamic HEDGE_ALCHEMY aspect costs into explicit JSON values from current item-aspect parity data.
- `audit-crucible-recipe-data.ps1` now validates `77/77` crucible JSON files.
- `audit-crucible-behavior.ps1` and `server-smoke.ps1` can run against isolated world/port settings, avoiding false local failures when another dev server or client is open; server smoke now also cleans up child runServer processes started by that smoke run.
- The current crucible behavior audit passes `12/12`, including item-entity absorption, special-result reabsorption protection, living contact damage boundaries, spill-remnants aura pollution and overflow `spillRandom`.
## Crucible contact cooldown scope note

- Moved the living-entity crucible contact damage cooldown from the singleton block instance to TCCrucibleBlockEntity.
- This prevents one crucible position from throttling or advancing another crucible's contact damage cadence.
## Infusion gameplay boundary design note

- Added a focused design document for the first in-world infusion behavior slice.
- Added an infusion recipe data audit to validate catalyst/components/aspects/result shape before behavior activation.
- Full in-world infusion completion, instability events and visual effects remain deferred beyond the current non-consuming matrix/pedestal start-plan plus completion-readiness slice.
## Infusion validation helper note

- Added `TCInfusionRecipeMatcher` as a non-mutating server-side validation helper for loaded `thaumcraft:infusion` recipe data.
- This is a validation/start-plan/readiness boundary only; item/aspect consumption, instability effects and rendering remain deferred.
## Infusion validation boundary audit note

- Added `TCInfusionAssembly` and `TCInfusionValidationResult` as the first server-owned infusion input snapshot/result layer.
- Fixed component validation to match legacy Forge `RecipeMatcher.findMatches`: pedestal component order is flexible, but supplied component count must exactly match the recipe count.
- Added `TCInfusionBehaviorAuditExporter` and `tools/audits/audit-infusion-behavior.ps1`; latest runtime audit passes `25/25`.
- Added active pedestal blocks, one-slot pedestal BlockEntities, matrix BlockEntity legacy-range pedestal discovery, non-consuming start-plan state, read-only completion-readiness state, and world snapshot checks.

## Infusion start-plan boundary note

- Added `TCInfusionCraftingPlan` and `TCInfusionStartResult` for the matrix active crafting-start state.
- The plan records recipe id, research key, instability, catalyst, matched component stacks, component pedestal positions, required aspects, result stack and player name.
- The plan is saved through BlockEntity NBT using modern `ItemStack` serialization and `AspectList` tags, and the behavior audit verifies round-trip load.
- Item consumption, essentia drain, instability rolls, beams, sounds, particles and completion output remain deferred to the next focused slice.
- This still does not implement item/aspect consumption, instability events, essentia transport, beams, particles, sounds or completion behavior.

## Infusion completion-readiness boundary note

- Added `TCInfusionCompletionPlan` as the read-only server-owned check that an active infusion plan still matches the current world/aspect state.
- The matrix rechecks the center catalyst, each originally matched component pedestal position/stack, and available aspect totals before any future mutation.
- The behavior audit now covers valid readiness, missing-aspect rejection, changed catalyst rejection, changed component rejection, and missing component pedestal rejection.
- This still does not consume pedestal items, drain essentia/aspects, replace the catalyst with output, roll instability, or run beams/particles/sounds.
## Infusion legacy cycle semantics audit note

- Added `06_docs/audits/infusion_legacy_cycle_semantics_audit.md` to capture legacy `craftingStart`, `craftCycle`, `craftingFinish`, and `getSurroundings` anchors.
- The next implementation should be a small audited mutation/executor boundary, not a full player-facing infusion craft trigger.
## Infusion container and essentia cycle audit note

- Added a focused legacy container/essentia timing audit for the infusion executor boundary.
- The next implementation should remain audit-only and non-player-facing until mutation timing is validated.