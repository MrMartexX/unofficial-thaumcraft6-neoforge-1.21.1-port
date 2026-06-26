# Item/block parity layer completion matrix

Batch 26 establishes the completion contract for each existing item/block parity framework layer. This document does not claim that gameplay parity is complete; it defines what each layer must prove before the framework can be called complete.

## Completion policy

A layer is complete only when its implemented checks are invoked by the orchestrator, produce schema-valid and fresh JSON/Markdown reports, have verifier coverage, and classify remaining gaps as implemented, intentional, superseded, blocked or explicitly review-only. Planned checks are framework work, not port parity gaps.

## Registry snapshot

| Layer | Implemented | Planned | Other | Checks |
|---|---:|---:|---:|---|
| Source quality and legacy evidence ($layer=source_quality) | 14 | 0 | 0 | access_transformers, auto_fix_candidates, check_invocation, ci_strict_safe_policy, docs_deferred, final_framework_completion, legacy_primary_manifest, original_jar_probe, public_api, report_freshness, report_schema, secondary_legacy_probe, source_conflict_report, status_taxonomy |
| Registry identity ($layer=registry_identity) | 4 | 0 | 0 | duplicate_registry_id, legacy_mapping, registry, variants |
| Resource boundary ($layer=resource_boundary) | 9 | 0 | 0 | block_item_pairs, blockstates, creative_tabs, json_validity, lang, loot, models, orphan_references, textures |
| Data boundary ($layer=data_boundary) | 10 | 0 | 0 | aspects, config_gates, drop_behavior, entity_links, fuels_flammability, recipes, research_refs, tags, thaumonomicon_refs, worldgen_links |
| Behavior boundary ($layer=behavior_boundary) | 9 | 0 | 0 | block_properties, blockentities, capabilities, client_server_safety, data_components, equipment, item_properties, menus, networking |
| Visual, model and FX boundary ($layer=visual_boundary) | 4 | 0 | 0 | sounds_particles, texture_color, visual_boundary, visual_equivalence_completion |
| Runtime and smoke boundary ($layer=runtime_boundary) | 3 | 0 | 0 | datapack_load, game_test_smoke, runtime_smoke |

## Layer matrix

| Layer | Purpose | Current evidence/reports | Known limitations | Completion requirements | Verifier/CI requirement |
|---|---|---|---|---|---|
| Source quality and legacy evidence ($layer=source_quality) | Establishes what legacy, secondary legacy, original-jar, access-transformer, public API, source-conflict, port and report-contract evidence is available before parity is judged. | legacy_primary_manifest reports, secondary legacy probe reports, original jar probe report, source conflict report, access transformer visibility report, public API surface report, auto-fix candidate reports, check invocation self-test, report freshness guard report, report schema contract report, status taxonomy report, docs/registry consistency report | Source-quality rows remain review-only until original jar authority, visibility-widening, public API and source conflict policies are classified. | Primary, secondary, original jar, access-transformer, public API and source-conflict evidence must be schema-valid, fresh and conflict-classified. | Verifier must check schema, invocation, freshness and docs consistency for all source-quality reports. |
| Registry identity ($layer=registry_identity) | Tracks legacy-to-port item/block IDs, duplicate IDs, split variants and rename policy. | item_block_parity_report comparer output, legacy mapping review report and variant split audit report | Review rows still need human rename/variant policy decisions. | Every legacy ID and variant family must resolve to implemented, renamed, intentionally missing, superseded or blocked status. | Verifier must prove registry comparer, mapping and variant reports are invoked and fresh. |
| Resource boundary ($layer=resource_boundary) | Validates blockstates, models, textures, lang, creative tab grouping, loot files and unresolved resource references. | item_block_parity_report, JSON validity report, creative tab audit report plus visual/texture specialized reports | Visual equivalence criteria need hardening; creative tab grouping rows remain review-only until player-facing ordering policy is classified. | All resource checks must be machine-parseable, schema-valid and distinguish missing, renamed and intentional gaps. | Verifier and CI-safe mode must run mechanical resource checks and fail only safe mechanical errors. |
| Data boundary ($layer=data_boundary) | Audits recipes, tags, aspects, research references, thaumonomicon references, fuel/flammability evidence, entity/spawn-egg links, worldgen-linked block/data evidence, config-gate evidence and source-evidenced data behavior. | item_block_data_reference_report, fuel/flammability report, entity link report, worldgen link report, config gate report and loot/drop behavior reports | Data-boundary rows remain review-only until burn-time, fire-spread, EntityType, spawn-egg, configured-feature, biome-modifier and config-gate policies are classified. | All data-backed parity dimensions must have report-only evidence and explicit intentional/superseded policy. | Verifier must run focused data smoke and validate report schema/freshness. |
| Behavior boundary ($layer=behavior_boundary) | Audits item properties, data component and legacy tag-state evidence, tool/armor/equipment links, block properties, block entity/menu/capability references, networking and client/server boundaries. | item/block property reports, data component bridge report, equipment audit report, networking boundary report, behavior boundary report, client/server safety report | Equipment and networking rows remain review-only until tool/armor/accessory and payload validation policies are classified. | Every behavior dimension must classify static parity, runtime-required evidence and known review gaps. | Verifier must run focused behavior-boundary smoke and later runtime-backed checks. |
| Visual, model and FX boundary ($layer=visual_boundary) | Audits model transforms, visual equivalence, texture color/shape clues and sound/particle/client FX references. | visual model/transform, texture color, sound/particle reports and visual equivalence completion criteria | Equivalence rule acceptance is conservative; unresolved review rows block strict visual certification but are not mechanical errors. | Reviewed equivalence rules and texture/FX policies must separate acceptable modern differences from true gaps, and visual completion criteria must identify strict-promotion blockers. | Verifier must validate visual report schema/freshness, rule-completion status and visual completion policy wiring. |
| Runtime and smoke boundary ($layer=runtime_boundary) | Validates load-critical datapack layout, GameTest/scripted smoke readiness and runtime smoke opt-in execution readiness. | runtime datapack smoke report, game_test_smoke readiness report and runtime smoke readiness report | Runtime rows remain report-only until runtime execution policy is promoted from readiness inventory to strict certification. | Runtime reports must have artifact schema, opt-in runner modes and minimal safe GameTest/server-startup coverage; the minimal fixture must remain report-only until strict runtime policy is finalized. | Verifier and CI must support report-only, safe and strict runtime modes. |

## Cross-cutting framework layers

| Layer | Current state | Completion requirement |
|---|---|---|
| Focus and scope control | Focused filtering supports Ids, IdPrefix, Families, Packages, ChangedOnly and SinceCommit; Batch 32 adds conservative dependency expansion from manifest text and Thaumcraft resource/data JSON references; Batch 51 adds golden focused family runner/rules for stable crystals, jars, tables, thaumonomicon and golems slices. | Dependency-aware focus must remain conservative, explain added IDs in reports and golden focused families must stay stable regression slices without silently expanding beyond directly evidenced support blocks, recipes and related wiring. |
| Rule and override governance | Rule override files exist, auto-fix candidate rules are present, status taxonomy/docs registry checks are present, Batch 34 adds legacy mapping review evidence, Batch 35 adds variant split review evidence, Batch 36 adds creative tab grouping evidence, Batch 37 adds data component bridge evidence, Batch 38 adds networking boundary evidence, Batch 39 adds fuel/flammability evidence, Batch 40 adds equipment evidence, Batch 41 adds entity/spawn-egg link evidence, Batch 42 adds worldgen-link evidence, Batch 43 adds config-gate evidence, Batch 44 adds access-transformer visibility evidence, Batch 45 adds public API surface evidence, Batch 46 adds source conflict aggregation evidence, and Batch 47 adds original jar probe evidence. | Status taxonomy must remain mapped, and final registry cleanup must remove ambiguous planned statuses. |
| Report governance | Batch 27 adds a report-only schema contract validator, Batch 29 adds a report-only freshness guard, and Batch 30 adds status taxonomy normalization and Batch 31 adds docs/registry consistency audit for local JSON reports; input hashes and commit metadata remain planned. | SchemaVersion, summary/results consistency, freshness, status taxonomy, input hashes and repo commit metadata must be enforced. |
| CI and verifier | Verifier v2 is available as `tools/audits/item-block-parity/verify-item-block-parity-framework.ps1`; GitHub Actions workflow `.github/workflows/item-block-framework-verifier.yml` runs it with a report-only default policy, manual safe/strict policy modes and uploads generated report artifacts; Batch 55 adds the final framework completion audit consumed by verifier v2. | CI must keep report-only runs stable, allow safe mechanical hard-fail runs, keep strict mode manual/opt-in until runtime and visual blockers are classified, publish artifacts without committing generated local reports, and keep final framework completion evidence report-backed. |
| Runtime execution | Runtime/datapack, GameTest/scripted and runtime-smoke layers are report-only readiness checks with explicit opt-in execution commands; Batch 52 adds a minimal opt-in scripted GameTest/runtime fixture for server startup and representative registry coverage. | Minimal opt-in scripted GameTest/runtime fixture coverage and normalized runtime artifacts must remain opt-in and report-only until strict runtime policy is finalized. |

## Completion backlog derived from the current registry

| Check | Layer | Reason | Intended closure path |
|---|---|---|---|
| <none> | <none> | All currently registered checks are implemented. | No planned framework check remains in the registry. |

## Final framework completion gate

The framework may be called complete only after:

- every implemented check has an invocation owner and schema-valid, status-normalized, fresh reports;
- every remaining non-implemented check is explicitly intentionally_out_of_scope, superseded or blocked;
- verifier v2 gate script validates script syntax, rule JSON, registry ownership, schema contracts, stale report guards, docs/registry consistency, report summaries and report-only framework smoke;
- runtime smoke and GameTest/scripted layers have report-only readiness plus documented opt-in execution coverage;
- CI publishes report artifacts and supports report-only, safe and strict modes without committing generated local reports;
- final framework completion report validates registry, owner, report, verifier, CI and strict-blocker policy consistency without claiming gameplay parity completion.
