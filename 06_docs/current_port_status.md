# Thaumcraft 6 NeoForge 1.21.1 Current Port Status

Last reviewed branch: `main`
Last reviewed commit: `97b86810fe771e0aa78ff16f4b407b59e4ceebba`
Reviewed target module: `05_neoforge_port`

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
| Assets | Needs audit | Check models, lang, textures, blockstates, and missing-model behavior. |
| Loot tables | Needs audit | Simple blocks need correct drops. |
| Tags | Needs audit/design | Tags replace old `OreDictionary` patterns. |
| Aspects | Not started | Wait until item/block ids and tag strategy are stable. |
| Aura | Not started | Requires design note. |
| Research | Not started | Requires data model, player storage, and sync design. |
| Recipes | Not started | Requires custom recipe serializer design. |
| BlockEntities | Not started | Do not copy legacy `TileEntity` classes directly. |
| Menus/screens | Not started | Requires server menu/client screen split. |
| Networking | Not started | Must use modern custom payloads with server validation. |
| Worldgen | Not started as a system | Blocks/plants exist, but features and biome modifiers are not implemented. |
| Rendering/FX | Minimal only | Plant particle polish exists; full rendering systems must wait. |

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
| `migration_matrix.md` | Basic blocks and current scope statuses are behind the code. | Keep matrix; use this file for live status. |

## Do not start without a design note

Do not implement aspects, aura, research, arcane crafting, crucible, infusion, BlockEntities, menus, networking, worldgen, or large rendering systems by direct legacy class copying. Each one needs a small design document first.

## Immediate next work

1. Run `./gradlew build --no-daemon` from `05_neoforge_port`.
2. Run `./gradlew runClient --no-daemon` and inspect the Thaumcraft creative tab.
3. Run `./gradlew runServer --no-daemon` after client/rendering work.
4. Audit implemented entries for lang, model, texture, blockstate, and loot table coverage.
5. Compare creative tab order with the 1.12.2 inventory screenshots.
6. Decide whether the next safe step is asset cleanup or the next simple identity batch.

## Local validation commands

From `05_neoforge_port`:

```powershell
.\gradlew.bat build --no-daemon
.\gradlew.bat runClient --no-daemon
.\gradlew.bat runServer --no-daemon
```
