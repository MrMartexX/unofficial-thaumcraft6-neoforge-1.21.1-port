# Port documentation

This folder is the main documentation area for the Thaumcraft 6 -> NeoForge 1.21.1 port.

## Start here

1. [Current task](CURRENT_TASK.md) - current working focus and project guardrails.
2. [Repository map](repo_map.md) - top-level folder map, active module map and workflow rules.
3. [Current port status](current_port_status.md) - current implementation status and document priority list.
4. [Migration matrix](migration_matrix.md) - staged gates, subsystem scope and risk rules.
5. [Porting order](porting_order.md) - staged roadmap.
6. [Legacy FX engine notes](legacy_fx_engine.md) - legacy FX/rendering notes.

## Core migration references

- [Current port status](current_port_status.md)
- [Migration matrix](migration_matrix.md)
- [Porting order](porting_order.md)
- [Subsystem inventory](subsystem_inventory.md)
- [Legacy migration guide] (NeoForge_legacy_migration_guide.md)

## Data and aspect references

- [Aspect assignment tag audit](aspect_assignment_tag_audit.md)
- [Aspect generateTags audit](aspect_generate_tags_audit.md)
- [Aspect assignment data format](aspect_assignment_data_format.md)
- [Aspect generated cache design](aspect_generated_cache_design.md)
- [Aspect legacy runtime logic audit](aspect_legacy_runtime_logic_audit.md)
- [Aspect parity comparison harness](aspect_parity_comparison_harness.md)
- [Vanilla aspect policy](vanilla_aspect_policy.md)
- [Vanilla 1.21 aspect assignments](vanilla_1_21_aspect_assignments.md)
- [Vanilla post-1.12 aspect rationale](vanilla_post_1_12_aspect_rationale.md)

## Research, scanning and Thaumonomicon references

- [Research knowledge scanning design](research_knowledge_scanning_design.md)
- [Research table and scribing tools design](research_table_scribing_tools_design.md)
- [Research progression parity audit](research_progression_parity_audit.md)
- [Thaumonomicon UI design](thaumonomicon_ui_design.md)
- [Scanning parity validation](scanning_parity_validation.md)
- [Entity aspect assignment audit](entity_aspect_assignment_audit.md)

## Recipes, workbench and gameplay references

- [Arcane crafting design](arcane_crafting_design.md)
- [Creative tab order reference](creative_tab_order_reference.md)
- [Aura design](aura_design.md)

## Rendering, assets and visual parity references

- [Rendering model pipeline audit](rendering_model_pipeline_audit.md)
- [Legacy FX engine notes](legacy_fx_engine.md)
- Curated audit summaries belong under [audits/](audits/).

## How to keep this folder usable

- Keep stable planning, decisions, migration notes and curated audits here.
- Keep reusable scripts under `../tools/`.
- Keep generated/local reports under `../tools/reports/local/` unless they are intentionally curated.
- Do not create a second docs folder for the same project state.

## Suggested subfolders

```text
06_docs/
  audits/      Curated audit summaries worth keeping.
  decisions/   Short architecture or parity decisions.
  migration/   Focused migration/API notes.
  references/  Small reference notes.
```
