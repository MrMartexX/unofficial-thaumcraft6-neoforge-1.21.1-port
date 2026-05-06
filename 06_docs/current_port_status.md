# Thaumcraft 6 NeoForge 1.21.1 Current Port Status

Last reviewed branch: `main`
Last reviewed base commit: `c25f03e11642bf23edb73c5136f38ffe587b4308`
Reviewed target module: `05_neoforge_port`
Working tree note: legacy asset corpus imported after this commit; review and commit separately.

## Purpose

This is the current implementation status document. Use it together with the migration guide before starting new work. Older planning files remain useful, but some status sections are behind the actual code.

## Document priority

1. `06_docs/NeoForge_legacy_migration_guide_expanded_v3.docx` - main architecture guide.
2. `06_docs/current_port_status.md` - current repository status.
3. `06_docs/migration_matrix.md` - subsystem matrix and gate rules.
4. `06_docs/porting_order.md` - staged roadmap.
5. `06_docs/creative_tab_order_reference.md` - creative tab order rules.
6. `06_docs/subsystem_inventory.md` - legacy subsystem audit.
7. `06_docs/gate1_items_plan.md` - historical Gate 1 workflow, not a full current inventory.

## High-level status

| Area | Status | Notes |
|---|---|---|
| Gate 0 bootstrap | Complete enough to continue | NeoForge module exists in `05_neoforge_port`; Java 21 and ModDevGradle are configured. |
| Main mod class | Implemented | `Thaumcraft` registers blocks, items, creative tabs, and config. |
| Item registry | Partially implemented | More than the original Gate 1 item slice exists. |
| Block registry | Partially implemented | Simple blocks, ores, stones, wood blocks, and plants have started. |
| Creative tab | Implemented, needs visual review | `TCCreativeTabOrder` owns visible order. Do not use registry order. |
| Assets | Legacy corpus imported, needs audit | Missing original `assets` files were copied into the port without overwriting adapted 1.21 resources. Check models, lang, textures, blockstates, shaders, sounds, research assets, and missing-model behavior. |
| Loot tables | Needs audit | Modern simple-block loot tables exist under `data/thaumcraft/loot_table`; legacy `assets/thaumcraft/loot_tables` is imported as reference material and is not the 1.21 data path. |
| Tags | Needs audit/design | Tags replace old `OreDictionary` patterns. |
| Aspects | Not started | Wait until item/block ids and tag strategy are stable. |
| Aura | Not started | Requires design note. |
| Research | Not started | Requires data model, player storage, and sync design. |
| Recipes | Not started | Requires custom recipe serializer design. |
| BlockEntities | Not started | Do not copy legacy `TileEntity` classes directly. |
| Menus/screens | Not started | Requires server menu/client screen split. |
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

## Last local validation

| Check | Result | Notes |
|---|---|---|
| `.\gradlew.bat build --no-daemon` | Passed | Run from `05_neoforge_port` after the legacy asset import using the configured Java 21 runtime. |
| `runClient` | Not run in this pass | Next visual/resource-warning check. |
| `runServer` | Not run in this pass | Run after client/rendering checks. |

## Implemented identity entries seen in `TCItems`

| Group | Entries |
|---|---|
| Ores | `ore_amber`, `ore_cinnabar`, `ore_quartz` |
| Crystals | `crystal_aer`, `crystal_ignis`, `crystal_aqua`, `crystal_terra`, `crystal_ordo`, `crystal_perditio`, `crystal_vitium` |
| Stone blocks | `stone_arcane`, `stone_arcane_brick`, `stone_ancient`, `stone_ancient_tile`, `stone_ancient_rock`, `stone_ancient_glyphed`, `stone_ancient_doorway`, `stone_eldritch_tile`, `stone_porous` |
| Stairs and slabs | `stairs_arcane`, `stairs_arcane_brick`, `stairs_ancient`, `stairs_greatwood`, `stairs_silverwood`, `slab_arcane_stone`, `slab_arcane_brick`, `slab_ancient`, `slab_eldritch`, `slab_greatwood`, `slab_silverwood` |
| Wood, leaves, plants | `log_greatwood`, `log_silverwood`, `leaves_greatwood`, `leaves_silverwood`, `sapling_greatwood`, `sapling_silverwood`, `shimmerleaf`, `cinderpearl`, `vishroom`, `plank_greatwood`, `plank_silverwood` |
| Other blocks/items | `amber_block`, `amber_brick`, `goggles`, `amber`, `quicksilver`, `fabric` |

## Current gate interpretation

| Gate | Current interpretation |
|---|---|
| Gate 0 | Complete enough to continue, but still validate `runServer` after client/render changes. |
| Gate 1 | In progress and expanded beyond the first simple item batch. Needs asset and creative order audit. |
| Gate 2 | Started early through simple block and block item identity work. Needs loot/model/blockstate validation. |
| Gate 3 | Not started. Do not begin aspects until ids, tags, and simple content are stabilized. |
| Gate 4+ | Not started. Requires design notes before implementation. |

## Partially stale documents

| Document | Stale part | Current handling |
|---|---|---|
| `gate1_items_plan.md` | Lists only `amber`, `quicksilver`, and `fabric` as the first implemented slice. | Keep as workflow guidance; use this file for actual inventory. |
| `creative_tab_order_reference.md` | First implemented entries section no longer reflects all implemented entries. | Keep policy; status should point here. |
| `migration_matrix.md` | Needs to distinguish imported legacy assets from adapted active resources. | Keep matrix for policy and gate sequencing; use this file for live implementation status. |
| `block_parity_audit.md` | Some block rows predate sapling/tree and block property updates. | Refresh before using it as a parity checklist. |

## Do not start without a design note

Do not implement aspects, aura, research, arcane crafting, crucible, infusion, BlockEntities, menus, networking, worldgen, or large rendering systems by direct legacy class copying. Each one needs a small design document first.

## Immediate next work

1. Run `./gradlew build --no-daemon` from `05_neoforge_port` after the asset import.
2. Run `./gradlew runClient --no-daemon` and inspect the Thaumcraft creative tab plus resource warnings.
3. Run `./gradlew runServer --no-daemon` after client/rendering work.
4. Audit implemented entries for lang, model, texture, blockstate, and loot table coverage.
5. Separate active 1.21 assets from imported legacy reference assets where runtime warnings require it.
6. Compare creative tab order with the 1.12.2 inventory screenshots.
7. Decide whether the next safe step is asset cleanup or the next simple identity batch.

## Local validation commands

From `05_neoforge_port`:

```powershell
.\gradlew.bat build --no-daemon
.\gradlew.bat runClient --no-daemon
.\gradlew.bat runServer --no-daemon
```
