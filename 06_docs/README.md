# Port documentation

This folder is the main documentation area for the Thaumcraft 6 -> NeoForge 1.21.1 port.

## Start here

1. [Current task](CURRENT_TASK.md) - current working focus and project guardrails.
2. [Repository map](repo_map.md) - top-level folder map, active module map and workflow rules.
3. [Current port status](current_port_status.md) - current implementation status and document priority list.
4. [Migration matrix](migration/migration_matrix.md) - staged gates, subsystem scope and risk rules.
5. [Porting order](migration/porting_order.md) - staged roadmap.
6. [Documentation index](documentation_index.md) - detailed folder map and cleanup rules.
7. [Legacy FX engine notes](rendering/legacy_fx_engine.md) - legacy FX/rendering notes.

## Core migration references

- [Current port status](current_port_status.md)
- [Migration matrix](migration/migration_matrix.md)
- [Porting order](migration/porting_order.md)
- [Documentation index](documentation_index.md)
- [Subsystem inventory](migration/subsystem_inventory.md)
- [Legacy migration guide](migration/NeoForge_legacy_migration_guide.md)

## Data and aspect references

- [Aspect assignment tag audit](data/aspects/aspect_assignment_tag_audit.md)
- [Aspect generateTags audit](data/aspects/aspect_generate_tags_audit.md)
- [Aspect assignment data format](data/aspects/aspect_assignment_data_format.md)
- [Aspect generated cache design](data/aspects/aspect_generated_cache_design.md)
- [Aspect legacy runtime logic audit](data/aspects/aspect_legacy_runtime_logic_audit.md)
- [Aspect parity comparison harness](data/aspects/aspect_parity_comparison_harness.md)
- [Vanilla aspect policy](data/aspects/vanilla_aspect_policy.md)
- [Vanilla 1.21 aspect assignments](data/aspects/vanilla_1_21_aspect_assignments.md)
- [Vanilla post-1.12 aspect rationale](data/aspects/vanilla_post_1_12_aspect_rationale.md)

## Research, scanning and Thaumonomicon references

- [Research knowledge scanning design](research/research_knowledge_scanning_design.md)
- [Research table and scribing tools design](research/research_table_scribing_tools_design.md)
- [Research progression parity audit](research/research_progression_parity_audit.md)
- [Thaumonomicon UI design](research/thaumonomicon_ui_design.md)
- [Scanning parity validation](research/scanning_parity_validation.md)
- [Entity aspect assignment audit](data/aspects/entity_aspect_assignment_audit.md)

## Recipes, workbench and gameplay references

- [Arcane crafting design](crafting/arcane_crafting_design.md)
- [Creative tab order reference](resources/creative_tab_order_reference.md)
- [Aura design](gameplay/aura_design.md)

## Rendering, assets and visual parity references

- [Rendering model pipeline audit](rendering/rendering_model_pipeline_audit.md)
- [Legacy FX engine notes](rendering/legacy_fx_engine.md)
- Curated audit summaries belong under [audits/](audits/).

## How to keep this folder usable

- Treat [Current task](CURRENT_TASK.md) as the only live task queue.
- Treat [Current port status](current_port_status.md) as the state snapshot plus changelog.
- Treat [Documentation index](documentation_index.md) as the file-role map and cleanup plan.
- Keep stable planning, decisions, migration notes and curated audits here.
- Keep reusable scripts under `../tools/`.
- Keep generated/local reports under `../tools/reports/local/` unless they are intentionally curated.
- Do not create a second docs folder for the same project state.

## Folder structure

```text
06_docs/
  audits/       Curated audit summaries worth keeping.
  crafting/     Recipe, crafting and page-data design docs.
  data/aspects/ Aspect model, assignment and parity docs.
  gameplay/     Gameplay subsystem design docs.
  migration/    Migration guide, matrix, roadmap and subsystem inventory.
  raw_legacy/   Large raw extracts, copied source excerpts and evidence files.
  rendering/    FX, model, overlay and visual parity docs/assets.
  research/     Research, scanning, Thaumonomicon and table docs.
  resources/    Asset, block parity, creative order and import docs.
```

When adding a new document, follow [Documentation index](documentation_index.md) and keep root limited to navigation/current-state files.
