# Item/block parity layer completion matrix

Batch 26 establishes the completion contract for each existing item/block parity framework layer. This document does not claim that gameplay parity is complete; it defines what each layer must prove before the framework can be called complete.

## Completion policy

A layer is complete only when its implemented checks are invoked by the orchestrator, produce schema-valid and fresh JSON/Markdown reports, have verifier coverage, and classify remaining gaps as implemented, intentional, superseded, blocked or explicitly review-only. Planned checks are framework work, not port parity gaps.

## Registry snapshot

| Layer | Implemented | Planned | Other | Checks |
|---|---:|---:|---:|---|
| Source quality and legacy evidence ($layer=source_quality) | 7 | 5 | 0 | access_transformers, auto_fix_candidates, check_invocation, docs_deferred, legacy_primary_manifest, original_jar_probe, public_api, report_freshness, report_schema, secondary_legacy_probe, source_conflict_report, status_taxonomy |
| Registry identity ($layer=registry_identity) | 2 | 2 | 0 | duplicate_registry_id, legacy_mapping, registry, variants |
| Resource boundary ($layer=resource_boundary) | 7 | 2 | 0 | block_item_pairs, blockstates, creative_tabs, json_validity, lang, loot, models, orphan_references, textures |
| Data boundary ($layer=data_boundary) | 6 | 4 | 0 | aspects, config_gates, drop_behavior, entity_links, fuels_flammability, recipes, research_refs, tags, thaumonomicon_refs, worldgen_links |
| Behavior boundary ($layer=behavior_boundary) | 6 | 3 | 0 | block_properties, blockentities, capabilities, client_server_safety, data_components, equipment, item_properties, menus, networking |
| Visual, model and FX boundary ($layer=visual_boundary) | 3 | 0 | 0 | sounds_particles, texture_color, visual_boundary |
| Runtime and smoke boundary ($layer=runtime_boundary) | 2 | 1 | 0 | datapack_load, game_test_smoke, runtime_smoke |

## Layer matrix

| Layer | Purpose | Current evidence/reports | Known limitations | Completion requirements | Verifier/CI requirement |
|---|---|---|---|---|---|
| Source quality and legacy evidence ($layer=source_quality) | Establishes what legacy, secondary legacy, original-jar, port and report-contract evidence is available before parity is judged. | legacy_primary_manifest reports, secondary legacy probe reports, auto-fix candidate reports, check invocation self-test, report freshness guard report, report schema contract report, status taxonomy report | Primary decompile is not absolute authority; original jar and conflict aggregation are still planned. | Primary, secondary and original jar evidence must be schema-valid, fresh and conflict-classified. | Verifier must check schema, invocation, freshness and docs consistency for all source-quality reports. |
| Registry identity ($layer=registry_identity) | Tracks legacy-to-port item/block IDs, duplicate IDs, split variants and rename policy. | item_block_parity_report comparer output | Dedicated legacy mapping and variant split checks remain planned. | Every legacy ID must resolve to implemented, renamed, intentionally missing, superseded or blocked status. | Verifier must prove registry comparer and mapping/variant reports are invoked and fresh. |
| Resource boundary ($layer=resource_boundary) | Validates blockstates, models, textures, lang, loot files and unresolved resource references. | item_block_parity_report plus visual/texture specialized reports | json_validity and creative tab parity are still planned; visual equivalence criteria need hardening. | All resource checks must be machine-parseable, schema-valid and distinguish missing, renamed and intentional gaps. | Verifier and CI-safe mode must run mechanical resource checks and fail only safe mechanical errors. |
| Data boundary ($layer=data_boundary) | Audits recipes, tags, aspects, research references, thaumonomicon references and source-evidenced data behavior. | item_block_data_reference_report and loot/drop behavior reports | Fuel/flammability, entity links, worldgen links and config gates remain planned. | All data-backed parity dimensions must have report-only evidence and explicit intentional/superseded policy. | Verifier must run focused data smoke and validate report schema/freshness. |
| Behavior boundary ($layer=behavior_boundary) | Audits item properties, block properties, block entity/menu/capability references, networking and client/server boundaries. | item/block property reports, behavior boundary report, client/server safety report | data_components, networking and equipment checks remain planned; many behavior facts are static/source-evidenced. | Every behavior dimension must classify static parity, runtime-required evidence and known review gaps. | Verifier must run focused behavior-boundary smoke and later runtime-backed checks. |
| Visual, model and FX boundary ($layer=visual_boundary) | Audits model transforms, visual equivalence, texture color/shape clues and sound/particle/client FX references. | visual model/transform, texture color and sound/particle reports | Equivalence rule acceptance is conservative; some rows remain review-only until legacy visual intent is classified. | Reviewed equivalence rules and texture/FX policies must separate acceptable modern differences from true gaps. | Verifier must validate visual report schema/freshness and rule-completion status. |
| Runtime and smoke boundary ($layer=runtime_boundary) | Validates load-critical datapack layout, GameTest/scripted smoke readiness and future real runtime smoke execution. | runtime datapack smoke report and game_test_smoke readiness report | runtime_smoke remains planned; GameTest layer inventories readiness but does not launch Minecraft automatically. | Runtime reports must have artifact schema, opt-in runner modes and minimal safe GameTest/server-startup coverage. | Verifier and CI must support report-only, safe and strict runtime modes. |

## Cross-cutting framework layers

| Layer | Current state | Completion requirement |
|---|---|---|
| Focus and scope control | Focused filtering supports Ids, IdPrefix, Families, Packages, ChangedOnly and SinceCommit; current smelter smoke confirms focused manifests are generated. | Dependency-aware focus must include related block entities, menus, capabilities, recipes and support blocks by rule. |
| Rule and override governance | Rule override files exist, auto-fix candidate rules are present, and Batch 30 adds a report-only status taxonomy validator for observed report statuses. | Status taxonomy must remain mapped, and final registry cleanup must remove ambiguous planned statuses. |
| Report governance | Batch 27 adds a report-only schema contract validator, Batch 29 adds a report-only freshness guard, and Batch 30 adds status taxonomy normalization for local JSON reports; input hashes and commit metadata remain planned. | SchemaVersion, summary/results consistency, freshness, status taxonomy, input hashes and repo commit metadata must be enforced. |
| CI and verifier | A local verifier confirms clean working tree, script syntax, rule JSON, focused presets, check invocation ownership and CI-safe smoke; CI report-only workflow exists. | Verifier v2 must become a complete framework certification gate; CI must publish artifacts and support report-only/safe/strict modes. |
| Runtime execution | Runtime/datapack and GameTest/scripted layers are report-only readiness checks. | Real runtime smoke runner, minimal fixture coverage and normalized runtime artifacts must be opt-in but implemented. |

## Completion backlog derived from the current registry

| Check | Layer | Reason | Intended closure path |
|---|---|---|---|
| data_components | behavior_boundary | Legacy NBT/data component check planned | Batch 37 legacy NBT/data component bridge audit |
| equipment | behavior_boundary | Tool/armor material and equipment links planned | Batch 40 tool/armor/equipment parity audit |
| networking | behavior_boundary | Payload reference boundary planned | Batch 38 networking boundary deep audit |
| config_gates | data_boundary | Config feature gate checks planned | Batch 43 config gate audit |
| entity_links | data_boundary | Entity/spawn egg links planned | Batch 41 entity and spawn-egg link audit |
| fuels_flammability | data_boundary | Fuel/flammability maps planned | Batch 39 fuel and flammability parity audit |
| worldgen_links | data_boundary | Worldgen-linked block references planned | Batch 42 worldgen-linked block/data audit |
| legacy_mapping | registry_identity | Dedicated mapping review module planned | Batch 34 dedicated legacy mapping review |
| variants | registry_identity | Metadata/split-ID module planned | Batch 35 metadata/split-ID variant audit |
| creative_tabs | resource_boundary | Creative order parity module planned | Batch 36 creative tab and player-facing grouping audit |
| json_validity | resource_boundary | CI-safe mechanical check planned | Batch 33 hard JSON validity check |
| runtime_smoke | runtime_boundary | Batch 10 runtime orchestration is available through explicit -RunSmoke; this check remains out of presets/CI hard-fail until policy is finalized | Batch 48 real runtime smoke runner |
| access_transformers | source_quality | AT visibility audit planned | Batch 44 access transformer visibility audit |
| docs_deferred | source_quality | Deferred documentation consistency planned | Batch 31 docs/registry consistency audit |
| original_jar_probe | source_quality | Batch 7/quality jar probe planned | Batch 47 original jar resource/class probe |
| public_api | source_quality | Public API surface check planned | Batch 45 public API surface audit |
| source_conflict_report | source_quality | Source conflict aggregation planned | Batch 46 primary/secondary/jar source conflict aggregation |

## Final framework completion gate

The framework may be called complete only after:

- every implemented check has an invocation owner and schema-valid, status-normalized, fresh reports;
- every remaining non-implemented check is explicitly intentionally_out_of_scope, superseded or blocked;
- verifier v2 validates script syntax, rule JSON, schema contracts, stale report guards, docs/registry consistency, focused family smoke and CI-safe mode;
- runtime smoke and GameTest/scripted layers have at least minimal opt-in runtime execution coverage;
- CI publishes report artifacts and supports report-only, safe and strict modes without committing generated local reports.
