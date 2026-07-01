# Documentation Index

Last updated: 2026-07-01

This is the navigation map for `06_docs/`. It records where documents live after the docs-folder restructuring.

## Read First

| Purpose | Document |
|---|---|
| Active task queue and guardrails | `CURRENT_TASK.md` |
| Current repository state | `current_port_status.md` |
| Repository layout | `repo_map.md` |
| Scope, gates and risk rules | `migration/migration_matrix.md` |
| Roadmap order | `migration/porting_order.md` |
| Main migration guide | `migration/NeoForge_legacy_migration_guide.md` |

## Root Files

Only high-traffic navigation and state files should stay directly under `06_docs/`.

| File | Role |
|---|---|
| `README.md` | Short start page and quick links. |
| `CURRENT_TASK.md` | The only live task queue. |
| `current_port_status.md` | Current state snapshot plus historical changelog notes. |
| `documentation_index.md` | This detailed docs map. |
| `repo_map.md` | Repository/workflow map. |

## Folder Map

| Folder | Role | Typical files |
|---|---|---|
| `migration/` | Migration guide, gate matrix, staged order, legacy subsystem inventory and historical gate plans. | `NeoForge_legacy_migration_guide.md`, `migration_matrix.md`, `porting_order.md`, `subsystem_inventory.md` |
| `data/aspects/` | Aspect model, assignments, runtime parity, vanilla policy and entity-aspect audit docs. | `aspects_design.md`, `aspect_*`, `vanilla_*`, `entity_aspect_assignment_audit.md` |
| `research/` | Research, scanning, Thaumonomicon, research table and requirement docs. | `research_*`, `scanning_*`, `scannable_data_format.md`, `thaumonomicon_ui_design.md` |
| `crafting/` | Recipe, crafting and page-data design docs. | `arcane_crafting_design.md` |
| `gameplay/` | Gameplay subsystem design docs that are not pure data/crafting/research. | `aura_design.md`, `crucible_in_world_behavior_design.md`, `focus_caster_core_design.md` |
| `resources/` | Asset import/runtime audits, block parity and creative order references. | `runtime_asset_audit.md`, `asset_bulk_import_manifest.txt`, `block_parity_audit.md`, `creative_tab_order_reference.md` |
| `rendering/` | FX, model, overlay and visual parity docs/assets. | `legacy_fx_engine.md`, `rendering_model_pipeline_audit.md`, `fx_preview/` |
| `raw_legacy/` | Large raw extracts, source excerpts and evidence files. These are reference material, not current task docs. | `sapling_tree_generation_research.md`, `research_knowledge_scanning_legacy_audit.txt`, legacy source folders |
| `audits/` | Curated audit summaries and cross-cutting parity policies worth keeping as reviewable reports. | `legacy_source_selection.md`, `item_block_parity_framework.md`, `item_block_parity_baseline_summary.md`, current subsystem checkpoints |

## What Not To Delete

- `raw_legacy/` is bulky by design. It stores evidence and extracted source material used for parity work.
- `audits/` contains curated summaries. Delete only obsolete generated reports after confirming they are not referenced.
- Historical plans such as `migration/gate1_items_plan.md` are not current task queues, but they explain why earlier gate decisions were made.

## Where New Docs Should Go

| New document type | Destination |
|---|---|
| Current task/status update | Root only if it updates `CURRENT_TASK.md` or `current_port_status.md`. |
| Migration rule or gate sequencing | `migration/` |
| Aspect/data assignment policy | `data/aspects/` |
| Research, scanning or Thaumonomicon work | `research/` |
| Recipe/page/crafting work | `crafting/` |
| Aura, machines or broad gameplay system design | `gameplay/` |
| Asset/model/blockstate/lang/creative ordering audit | `resources/` |
| FX/model/overlay/visual parity work | `rendering/` |
| Raw extractor output or copied legacy source | `raw_legacy/` unless curated into `audits/` |
| Stable audit summary | `audits/` |

## Cleanup Rules

- Keep `CURRENT_TASK.md` as the only live task queue.
- Keep `current_port_status.md` as the current state snapshot plus changelog, not as a planning backlog.
- Keep `migration/migration_matrix.md` focused on gates, risks and scope.
- Keep generated local reports out of `06_docs/` unless they are curated and intentionally referenced.
- Move files with `git mv` and update references in the same commit.
- Do not move code, assets or runtime data just to mirror documentation folders.
