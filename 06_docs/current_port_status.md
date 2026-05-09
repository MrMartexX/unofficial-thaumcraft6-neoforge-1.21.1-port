# Thaumcraft 6 NeoForge 1.21.1 Current Port Status

Last reviewed branch: `main`
Last reviewed base commit: `70ec2f06ff06d53f7119f7db9adb83b792368874`
Reviewed target module: `05_neoforge_port`
Working tree note: runtime asset audit, Gate 1/2 cleanup notes, and aspects design are being finalized after the asset checkpoint.

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
18. `06_docs/gate1_items_plan.md` - historical Gate 1 workflow, not a full current inventory.

## High-level status

| Area | Status | Notes |
|---|---|---|
| Gate 0 bootstrap | Complete enough to continue | NeoForge module exists in `05_neoforge_port`; Java 21 and ModDevGradle are configured. |
| Main mod class | Implemented | `Thaumcraft` registers blocks, items, creative tabs, and config. |
| Item registry | Partially implemented | More than the original Gate 1 item slice exists. |
| Block registry | Partially implemented | Simple blocks, ores, stones, wood blocks, and plants have started. |
| Creative tab | Implemented, needs visual review | `TCCreativeTabOrder` owns visible order. Do not use registry order. |
| Assets | Runtime audited for active content | Missing original `assets` files were copied into the port without overwriting adapted 1.21 resources. Registered active content has model/lang/blockstate/loot coverage; `amber`, `quicksilver`, and `fabric` item model texture paths were fixed from legacy `items/` to modern `item/`, with active PNGs copied into `textures/item`. |
| Loot tables | Active registered content covered | Modern simple-block loot tables exist under `data/thaumcraft/loot_table`; legacy `assets/thaumcraft/loot_tables` is imported as reference material and is not the 1.21 data path. |
| Tags | Aspect tag audit expanded | Tags replace old `OreDictionary` patterns. `aspect_assignment_tag_audit.md` maps current legacy aspect-related keys; safe current common tag resources exist for amber/cinnabar/quartz ores, amber gems, vanilla ore/gem/ingot/dust/raw-material bridges, and copper material bridges; exact `thaumcraft:legacy_ore_dictionary/*` item/block tags now preserve all already registered 1.12 OreDictionary entries. |
| Aspects | Core/API slice started with parity validation | `Aspect`, `AspectList`, pure `AspectHelper` logic, reload-safe data-driven exact/tag/manual assignments, vanilla material tag bridges, crafting generated-cache slice, legacy stack-sensitive bonus rules, component-aware potion and enchanted-book lookup, spawn-egg exclusion, bootstrap parity validation, server-data-load tag validation, OreDictionary-to-tag audit, `generateTags` audit, read-only Shift inventory tooltip rendering, and assignment/cache/manual-policy docs are implemented/documented. All assignable current `minecraft:*` item ids have aspects after reload validation; spawn eggs and empty component-only potion carriers are intentionally excluded for legacy parity. The runtime dump harness now runs on both original Forge 1.12.2 Thaumcraft and the 1.21.1 port; the comparer uses `legacy_to_modern_stack_map.json` to separate expected version differences from real port gaps. Crucible/infusion/arcane recipe generation and aura/research/essentia/scanning/gameplay integrations are still not started. |
| Aura | Started with server-side core | `AuraHelper`, per-level `SavedData`, chunk `base/vis/flux`, automatic chunk initialization, legacy formula for aura base generation, main-thread 20-tick legacy-like update loop, and permission-level-2 debug commands are implemented. The biome category mapper is legacy-like because 1.21 has no `BiomeDictionary`. HUD sync, FX, flux rifts, research-aware preservation, and gameplay consumers are intentionally not started. |
| Research | Not started | Requires data model, player storage, and sync design. |
| Recipes | Basic vanilla crafting fixtures started | Simple modern `data/thaumcraft/recipe` crafting recipes exist for generated-aspect validation. Custom Thaumcraft recipe serializers are not started. |
| BlockEntities | Not started | Do not copy legacy `TileEntity` classes directly. |
| Menus/screens | Not started | Requires server menu/client screen split. The current aspect tooltip is a client-only vanilla tooltip component, not a menu/screen subsystem. |
| Networking | Not started | Must use modern custom payloads with server validation. |
| Worldgen | Started early, not as a system | Sapling-grown Greatwood/Silverwood tree generators exist; biome modifiers, configured features, and structure/world placement are not implemented. |
| Rendering/FX | Started early, still high risk | Legacy-style FX dispatcher/particle scaffolding exists; full rendering systems must wait for design and validation. |

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
| `.\gradlew.bat build --no-daemon` | Passed | Re-run after each aspect/tag/client-tooltip/aura expansion batch. |
| `.\gradlew.bat runClient --no-daemon` | Passed startup/resource smoke check | Client reached resource atlas creation, aspect assignment reload, generated cache rebuild, tag validation, and integrated-server player join. Visual tooltip inspection still needs a manual inventory hover check. |
| `.\gradlew.bat runServer --no-daemon` | Started successfully | Dedicated server reached `Done`; bundled bootstrap, data-resource assignment reload, generated crafting cache rebuild, tag reload validation, aura event/command registration, automatic aura chunk initialization, and the client-only tooltip registration boundary all passed. Latest dump run loaded `672` exact assignments, `46` tag assignments, `32` complex exact assignments, rebuilt `475` generated crafting cache entries, and reported `1230 of 1230` assignable minecraft item ids with non-empty aspects. Spawn eggs and empty component-only splash/lingering/tipped carrier ids are excluded for 1.12 parity. Server smoke produced `run/world/data/thaumcraft_aura.dat` with `49` aura chunks in the prepared spawn area. |
| Aspect runtime dumps | Passed mapped harness run | Original Forge 1.12.2 Thaumcraft server wrote `1798` entries; NeoForge 1.21.1 server wrote `1986` entries. With `legacy_to_modern_stack_map.json`, the comparer has `1138` comparable keys: `1138` identical, including `283` legacy-to-modern mapped parity entries. Current real mapped gaps are `0`; potion content/order, mapped Sweeping Edge stored books, and currently registered Thaumcraft set differences are closed. |

## Implemented identity entries seen in `TCItems`

| Group | Entries |
|---|---|
| Ores | `ore_amber`, `ore_cinnabar`, `ore_quartz` |
| Crystals | `crystal_aer`, `crystal_ignis`, `crystal_aqua`, `crystal_terra`, `crystal_ordo`, `crystal_perditio`, `crystal_vitium` |
| Stone blocks | `stone_arcane`, `stone_arcane_brick`, `stone_ancient`, `stone_ancient_tile`, `stone_ancient_rock`, `stone_ancient_glyphed`, `stone_ancient_doorway`, `stone_eldritch_tile`, `stone_porous` |
| Stairs and slabs | `stairs_arcane`, `stairs_arcane_brick`, `stairs_ancient`, `stairs_greatwood`, `stairs_silverwood`, `slab_arcane_stone`, `slab_arcane_brick`, `slab_ancient`, `slab_eldritch`, `slab_greatwood`, `slab_silverwood` |
| Wood, leaves, plants | `log_greatwood`, `log_silverwood`, `leaves_greatwood`, `leaves_silverwood`, `sapling_greatwood`, `sapling_silverwood`, `shimmerleaf`, `cinderpearl`, `vishroom`, `plank_greatwood`, `plank_silverwood` |
| Other blocks/items | `amber_block`, `amber_brick`, `goggles`, `amber`, `quicksilver`, `fabric` |

## Aspect assignment data resources

The current aspect assignment source of truth is split across bundled data files under `05_neoforge_port/src/main/resources/data/thaumcraft/aspect_assignments/`.

| Assignment layer | Count | Notes |
|---|---:|---|
| Exact item assignments | `672` | `current_registered.json` covers normally authored registered Thaumcraft ids; `current_registered_runtime_parity.json` preserves dump-derived registered Thaumcraft final values; `legacy_vanilla_core.json` and `legacy_vanilla_modern_exact.json` preserve direct vanilla seeds; `legacy_vanilla_modern_manual.json` covers 1.21-only vanilla ids by audited Thaumcraft-style category; `legacy_vanilla_runtime_parity.json` preserves dump-derived final 1.12 values for shared plain vanilla stacks affected by metadata flattening, generated recipes, complex extras, wildcard specificity, or stack bonuses. Spawn eggs, firework star/rocket, and infested blocks are excluded because 1.12 gave those comparable stacks no aspects. |
| Item tag assignments | `46` | Includes safe current common `c:` tags, vanilla material bridges for legacy ore/gem/ingot/dust/base-block keys, ore-derived 1.21 raw materials, `blockGlass`, plus exact `thaumcraft:legacy_ore_dictionary/*` compatibility tags where legacy used string-key aspect assignments. |
| Complex exact assignments | `32` | Current complex extras cover audited buckets, boats, doors, fence gates, and related legacy complex additions. Runtime diff proves this layer must continue to be source-vs-runtime reviewed before broad expansion because legacy exact/generated/wildcard lookup order can mask wildcard complex values. |
| Generated crafting assignments | `475` | Built from `RecipeType.CRAFTING` recipes after server data/tag reload for current `minecraft:*` and `thaumcraft:*` outputs that have known ingredient aspects. Exact/tag/manual/runtime-parity assignments still win over generated values. |

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
- shapeless crafting and remaining-item subtraction match the legacy crafting formula.

## Current gate interpretation

| Gate | Current interpretation |
|---|---|
| Gate 0 | Complete enough to continue, but still validate `runServer` after client/render changes. |
| Gate 1 | In progress and expanded beyond the first simple item batch. Active registered item resources are covered; creative order still needs visual review. |
| Gate 2 | Started early through simple block and block item identity work. Active registered blockstates, models, loot tables, and translations are covered. |
| Gate 3 | Implementation started carefully. Exact legacy core aspect definitions/list/helper logic, current registered-id assignments, generated crafting cache, and read-only Shift tooltip rendering are present and guarded by validation; no gameplay integrations yet. |
| Gate 4+ | Aura is started as an isolated server-side storage/API slice after `aura_design.md`; BlockEntities, capabilities, research, custom recipes, menus, networking, worldgen, and large rendering systems remain not started. |

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
3. Use the next full `./gradlew runClient --no-daemon` visual pass to inspect creative tab order, active item icons, and Shift-held aspect tooltip visuals.
4. Compare creative tab order with the 1.12.2 inventory screenshots.
5. Add future registered-id aspect values through `data/thaumcraft/aspect_assignments`, not hardcoded Java maps.
6. Keep vanilla item coverage at `0 missing` after every aspect/tag change; do not broaden third-party modded generated outputs until an addon policy exists.
7. Implement crucible, infusion, and arcane recipe-derived `generateTags` behavior from `aspect_generate_tags_audit.md` before assigning aspects to those custom recipe outputs.
8. Continue vanilla aspect changes only from `ConfigAspects`, audited legacy OreDictionary-to-tag bridges, recipe-derived cache behavior, or documented 1.21-only category policy.
9. Keep parity validation and reload validation passing before expanding aspect consumers.
10. Do not expand aura beyond saved-data/query/debug-command/autogenerated chunk state, and do not begin research, essentia, GUI, networking, crafting costs, or gameplay-heavy systems without their own design notes.

## Local validation commands

From `05_neoforge_port`:

```powershell
.\gradlew.bat build --no-daemon
.\gradlew.bat runClient --no-daemon
.\gradlew.bat runServer --no-daemon
```
