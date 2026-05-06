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
7. `06_docs/gate1_items_plan.md` - historical Gate 1 workflow, not a full current inventory.

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
| Tags | Needs audit/design | Tags replace old `OreDictionary` patterns. |
| Aspects | Design note created | `06_docs/aspects_design.md` defines the first data-layer target. No aspect code is implemented yet. |
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

The runtime asset audit is `06_docs/runtime_asset_audit.md`.

## Last local validation

| Check | Result | Notes |
|---|---|---|
| `.\gradlew.bat build --no-daemon` | Passed | Run from `05_neoforge_port` after the legacy asset import using the configured Java 21 runtime. |
| `.\gradlew.bat runClient --no-daemon` | Passed startup/resource smoke check | Client log from `2026-05-06-1.log.gz`; three active item texture warnings were found. A second short client resource smoke after the fixes no longer reported Thaumcraft missing texture warnings. |
| `.\gradlew.bat runServer --no-daemon` | Started successfully | Dedicated server reached `Done (5.095s)!`; the Gradle/server Java processes were stopped manually after successful startup because piped `stop` was not consumed. |

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
| Gate 1 | In progress and expanded beyond the first simple item batch. Active registered item resources are covered; creative order still needs visual review. |
| Gate 2 | Started early through simple block and block item identity work. Active registered blockstates, models, loot tables, and translations are covered. |
| Gate 3 | Design-only started. `aspects_design.md` exists; no aspect implementation yet. |
| Gate 4+ | Not started. Requires design notes before implementation. |

## Partially stale documents

| Document | Stale part | Current handling |
|---|---|---|
| `gate1_items_plan.md` | Lists only `amber`, `quicksilver`, and `fabric` as the first implemented slice. | Keep as workflow guidance; use this file for actual inventory. |
| `creative_tab_order_reference.md` | First implemented entries section no longer reflects all implemented entries. | Keep policy; status should point here. |
| `migration_matrix.md` | Matrix now distinguishes imported legacy assets from active adapted resources. | Keep matrix for policy and gate sequencing; use this file for live implementation status. |
| `block_parity_audit.md` | Refreshed for sapling/tree and block property updates. | Still requires exact legacy parity checks before behavior tuning. |

## Do not start without a design note

Do not implement aspects, aura, research, arcane crafting, crucible, infusion, BlockEntities, menus, networking, worldgen, or large rendering systems by direct legacy class copying. Each one needs a small design document first.

## Immediate next work

1. Re-run `./gradlew build --no-daemon` after the runtime audit and design-document cleanup.
2. Use the next full `./gradlew runClient --no-daemon` visual pass to inspect creative tab order and active item icons.
3. Compare creative tab order with the 1.12.2 inventory screenshots.
4. Before aspect implementation, use `06_docs/aspects_design.md` as the scope boundary.
5. Do not expand aura, research, essentia, GUI, networking, or gameplay-heavy systems without their own design notes.

## Local validation commands

From `05_neoforge_port`:

```powershell
.\gradlew.bat build --no-daemon
.\gradlew.bat runClient --no-daemon
.\gradlew.bat runServer --no-daemon
```
