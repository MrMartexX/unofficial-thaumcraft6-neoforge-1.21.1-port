# Documentation Index And Cleanup Plan

Last updated: 2026-06-18

This file keeps `06_docs/` navigable. It does not replace the migration guide, status document, or migration matrix.

## Read First

| Purpose | Document |
|---|---|
| Active task queue and guardrails | `CURRENT_TASK.md` |
| Current repository state | `current_port_status.md` |
| Scope, gates and risk rules | `migration_matrix.md` |
| Roadmap order | `porting_order.md` |
| Main migration guide | `NeoForge_legacy_migration_guide.md` |
| Repository layout | `repo_map.md` |

## Current File Roles

| Role | Keep at root for now | Notes |
|---|---|---|
| Active status and planning | `CURRENT_TASK.md`, `current_port_status.md`, `migration_matrix.md`, `porting_order.md`, `repo_map.md`, `README.md` | These are high-traffic files and should stay at root unless every reference is updated. |
| Core migration reference | `NeoForge_legacy_migration_guide.md`, `subsystem_inventory.md` | The migration guide is intentionally Russian for now. |
| Aspect/data references | `aspects_design.md`, `aspect_assignment_data_format.md`, `aspect_assignment_tag_audit.md`, `aspect_generate_tags_audit.md`, `aspect_generated_cache_design.md`, `aspect_legacy_gap_audit.md`, `aspect_legacy_runtime_logic_audit.md`, `aspect_parity_comparison_harness.md`, `vanilla_aspect_policy.md`, `vanilla_1_21_aspect_assignments.md`, `vanilla_post_1_12_aspect_rationale.md` | Useful, but this group is large enough to move under `data/` or `aspects/` later. |
| Research/scanning/Thaumonomicon references | `research_knowledge_scanning_design.md`, `research_progression_parity_audit.md`, `research_recipe_page_catalog_design.md`, `research_requirement_blocker_audit.md`, `research_requirement_material_family_mapping.md`, `research_scanning_stabilization_checkpoint.md`, `research_table_gui_parity_audit.md`, `research_table_scribing_tools_design.md`, `scannable_data_format.md`, `scanning_gap_audit.md`, `scanning_parity_validation.md`, `post_1_12_scanning_policy.md`, `thaumonomicon_ui_design.md` | Keep until research progression and UI are stable; later split into `research/` and `scanning/`. |
| Crafting/gameplay design | `arcane_crafting_design.md`, `aura_design.md`, `block_parity_audit.md`, `creative_tab_order_reference.md`, `runtime_asset_audit.md` | Active references for current port slices. |
| Rendering/visual parity | `legacy_fx_engine.md`, `rendering_model_pipeline_audit.md`, `fx_scanning_render_parity_status.md`, `fx_wispy_behavior_verification.md`, `thaumometer_visual_parity_audit.md` | Keep grouped mentally as `rendering/` candidates. |
| Large raw or near-raw legacy extracts | `sapling_tree_generation_research.md`, `research_knowledge_scanning_legacy_audit.txt`, `block_mining_and_tree_growth_audit.txt`, `fx_legacy_exact_extract.md`, `fx_legacy_usage_hits.csv`, `fxdispatcher_fxgeneric_methods.txt` | Do not delete. These are bulky but still useful as parity evidence until replaced by reproducible scripts and curated summaries. |
| Generated/import manifests | `asset_bulk_import_manifest.txt` | Keep as an import audit record. |
| Historical plans | `gate1_items_plan.md` | Keep as historical context; do not treat as current task queue. |

## Existing Subfolders

| Folder | Current role | Recommendation |
|---|---|---|
| `audits/` | Curated audit summaries | Continue using for reviewed reports worth keeping. |
| `legacy_sources/` | Legacy source excerpts/references | Keep for source-backed migration evidence. |
| `research_knowledge_scanning_legacy_sources/` | Research/scanning source excerpts | Keep until research/scanning parity is closed. |
| `sapling_exact_legacy_sources/` | Sapling/tree source excerpts | Keep until tree/worldgen parity is revisited. |
| `fx_preview/` | FX preview assets/notes | Keep with rendering work. |

## Proposed Future Structure

Do not move files into this structure without checking references first.

```text
06_docs/
  status/       Stable snapshots and current-task history after root links are updated.
  data/         Aspects, tags, generated cache and vanilla assignment policy.
  research/     Research, scanning, Thaumonomicon and table documents.
  crafting/     Arcane, crucible, infusion and recipe-page documents.
  rendering/    FX, model, overlay and visual parity documents.
  raw_legacy/   Large raw extracts and CSV/text evidence files.
  audits/       Curated summaries that should stay reviewable.
```

## Merge Or Move Candidates

| Candidate | Safe action | Reason |
|---|---|---|
| `vanilla_1_21_aspect_assignments.md` + `vanilla_post_1_12_aspect_rationale.md` | Keep separate for now; later add a short index page | One is current assignment table, the other is rationale/evidence-heavy. Combining would make review harder. |
| `fx_legacy_exact_extract.md`, `fx_legacy_usage_hits.csv`, `fxdispatcher_fxgeneric_methods.txt` | Move together later under `raw_legacy/rendering/` | They are source evidence, not active design docs. |
| `sapling_tree_generation_research.md` + `sapling_exact_legacy_sources/` | Move the large root file later under `raw_legacy/worldgen/` or summarize into `worldgen_tree_audit.md` | The current root file is too large for normal navigation. |
| `research_knowledge_scanning_legacy_audit.txt` + `research_knowledge_scanning_legacy_sources/` | Move the raw audit later under `raw_legacy/research/` and keep a curated summary in root/research docs | The raw audit is valuable but too bulky for root. |
| `gate1_items_plan.md` | Keep but label historical when touched next | It is no longer the current implementation plan. |

## Cleanup Rules

- Prefer adding an index or curated summary before deleting or merging evidence files.
- Move large files only in a dedicated docs-only commit and update every reference in the same commit.
- Keep generated local reports out of `06_docs/` unless they are curated and intentionally referenced.
- Keep `CURRENT_TASK.md` as the only live task queue.
- Keep `current_port_status.md` as the current state snapshot plus changelog, not as a planning backlog.
- Keep `migration_matrix.md` focused on gates, risks and scope; detailed task sequencing belongs in `CURRENT_TASK.md` or a subsystem design doc.
