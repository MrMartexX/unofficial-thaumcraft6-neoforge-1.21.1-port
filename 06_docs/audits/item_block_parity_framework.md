# Item/block parity audit framework

Status: authoritative framework plan for the Thaumcraft 6 -> NeoForge 1.21.1 item/block parity audit.

## 1. Goal

The framework is an audit and planning instrument. It must collect legacy item/block evidence, collect the current port state, normalize known mappings and deferrals, and report parity by layer.

It must not call resource existence "full parity". Full parity is only a candidate when registry, resources, data references, behavior boundaries, runtime behavior and visual evidence have all been checked or explicitly documented.

## 2. Source of truth policy

| Role | Path | Use |
|---|---|---|
| Primary legacy source | `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/` | Readable MCP/deobfuscated behavior, formulas, class roles, registry construction, recipes, GUI/container references, renderer references and comments/variant hints. |
| Secondary legacy source | `03_self_decompiled_check/vineflower_thaumcraft6/` | Explicit cross-check/fallback only when the primary source is missing, suspicious or contradictory. |
| Original jar fallback | `01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar` | Packaged class/resource existence, asset truth and final tie-breaker when decompiled sources disagree. |
| Runtime truth | Legacy and port runtime exporters/comparers | State-dependent behavior that cannot be proven safely from decompiled code alone. |

The primary source is the default source for cached manifests. Secondary and jar probes must never silently replace primary evidence; they produce explicit review statuses.

## 3. Three-layer input model

```text
legacy_primary_manifest.json
port_manifest.json
rules/*.json
```

Legacy data is cached and fingerprinted. Port data is live and must be extracted on every audit run because the port changes after each implementation batch.

## 4. Planned folder structure

```text
tools/audits/item-block-parity/
  audit-item-block-parity.ps1
  extract-legacy-primary-manifest.ps1
  extract-port-manifest.ps1
  compare-item-block-parity.ps1
  validate-parity-report.ps1
  modules/
    registry.ps1
    legacy_mapping.ps1
    variants.ps1
    item_properties.ps1
    block_properties.ps1
    blockstates.ps1
    models.ps1
    textures.ps1
    lang.ps1
    creative_tabs.ps1
    loot.ps1
    recipes.ps1
    tags.ps1
    aspects.ps1
    research_refs.ps1
    data_components.ps1
    blockentities.ps1
    capabilities.ps1
    menus.ps1
    networking.ps1
    client_server_safety.ps1
    sounds_particles.ps1
    fuels_flammability.ps1
    equipment.ps1
    entity_links.ps1
    worldgen_links.ps1
    config_gates.ps1
    access_transformers.ps1
    public_api.ps1
    orphan_references.ps1
    runtime_smoke.ps1
    visual_boundary.ps1
    item_visual_parity.ps1
    legacy_shape_parity.ps1
    legacy_visual_collision_parity.ps1
    docs_deferred.ps1
    secondary_legacy_probe.ps1
    original_jar_probe.ps1
  rules/
    parity-rules.json
    known-renames.json
    deferred-boundaries.json
    allowed-extras.json
    variant-mapping.json
    no-item-block-expected.json
    no-loot-expected.json
    source-policy.json
  schema/
    legacy_manifest.schema.json
    port_manifest.schema.json
    parity_report.schema.json
```

Generated/local reports belong under `tools/reports/local/item-block-parity/`. Curated summaries and milestone reports belong under `06_docs/audits/`.

## 5. Orchestrator contract

Main entry point:

```powershell
.\tools\audits\item-block-parity\audit-item-block-parity.ps1 `
  -RepoRoot "D:\Thaumcraft_6_port_to_1.21.1" `
  -Preset quick `
  -FailMode safe
```

Planned parameters:

```text
RepoRoot
LegacyRoot
SecondaryLegacyRoot
OriginalJar
Preset
Checks
Ids
IdPrefix
Families
Packages
ChangedOnly
SinceCommit
RefreshLegacy
UseCachedLegacy
ProbeSecondaryLegacy
ExplainPlan
ListChecks
WriteCuratedSummary
RunBuild
RunSmoke
RunRelatedAudits
FailMode: off | safe | strict
```

## 6. Presets

| Preset | Purpose | Checks |
|---|---|---|
| `quick` | Daily fast check | registry, duplicate_registry_id, block_item_pairs, blockstates, models, textures, texture_color, lang, orphan_references, item_visual_parity, legacy_shape_parity, legacy_visual_collision_parity |
| `resources` | Asset/resource changes | blockstates, models, textures, texture_color, lang, creative_tabs, loot, tags, recipes, orphan_references, item_visual_parity, legacy_shape_parity, legacy_visual_collision_parity |
| `visual` | Focused visual/model/shape/FX evidence | blockstates, models, textures, orphan_references, visual_boundary, item_visual_parity, legacy_shape_parity, legacy_visual_collision_parity, texture_color, sounds_particles, visual_equivalence_completion |
| `data` | Datapack/data changes | recipes, loot, tags, fuels_flammability, aspects, research_refs, thaumonomicon_refs |
| `behavior-boundary` | Java behavior changes | item_properties, data_components, equipment, block_properties, blockentities, capabilities, menus, networking, client_server_safety, item_visual_parity, legacy_shape_parity, legacy_visual_collision_parity |
| `source-quality` | Legacy source consistency | legacy_primary_manifest, secondary_legacy_probe, source_conflict_report, original_jar_probe |
| `ci-safe` | CI-safe hard-fail subset | registry, json_validity, blockstates, models, textures, lang, orphan_references, client_server_safety, datapack_load |
| `full` | Manual milestone audit | all implemented and probe checks |

The full preset is not for every commit. Use it for parity milestones, extractor changes and major family reviews.

## 7. Status model

Reports must use layered statuses, not only pass/fail.

```text
PASS
MISSING
EXTRA
RENAMED_WITH_MAPPING
VARIANT_MAPPED
DEFERRED
INTENTIONAL_MISSING
PARTIAL_PARITY
RESOURCE_PARITY
DATA_PARITY
BOUNDARY_PARITY
RUNTIME_PARITY
VISUAL_PARITY_UNCHECKED
LEGACY_SOURCE_REVIEW_NEEDED
ITEM_VISUAL_PASS
ITEM_VISUAL_REVIEW_NEEDED
ITEM_VISUAL_MISSING
LEGACY_SHAPE_PASS
LEGACY_SHAPE_REVIEW_NEEDED
LEGACY_PARITY_MATCH
LEGACY_PARITY_MISMATCH
LEGACY_PARITY_MISSING
LEGACY_PARITY_UNKNOWN
FULL_PARITY_CANDIDATE
NOT_EVALUATED
```

`FULL_PARITY_CANDIDATE` is allowed only when registry, resource, data, behavior boundary, runtime and visual boundaries are all resolved or explicitly documented.

## 8. Fail modes

| Fail mode | Meaning |
|---|---|
| `off` | Report only. Never fail for gaps. |
| `safe` | Default. Fail only for safe mechanical errors in implemented checks: duplicate IDs, broken JSON, missing required registered resources, missing model textures, missing tag targets, missing recipe outputs, client imports in common/server code. |
| `strict` | Manual milestone mode. Also fail for unresolved deferred, unchecked behavior, secondary source conflicts and visual boundary gaps. |

Strict is not the default because it would block normal development before classifications exist.

## 9. Report policy

Local raw reports:

```text
tools/reports/local/item-block-parity/
```

Curated reports:

```text
06_docs/audits/
```

Commit only baseline summaries, milestone summaries, source decisions and architecture decisions. Do not commit large raw JSON/Markdown report dumps unless intentionally curated.

## 10. Planned implementation sequence

| Batch | Name | Result |
|---:|---|---|
| 1 | Source decision + framework plan | Framework and source policy docs; no port code changes |
| 2 | Skeleton scripts and rules | Orchestrator skeleton, rules, ListChecks, ExplainPlan |
| 3 | Primary legacy extractor v1 | Cached primary legacy manifest |
| 4 | Port extractor v1 | Live port manifest |
| 5 | Safe compare v1 | Registry/resources/basic orphan comparison |
| 6 | Rule overrides | Renames, variants, extras, no-item, no-loot, deferrals |
| 7 | Secondary legacy probe | Explicit cross-check report |
| 8 | Aspect/research/recipe references | Data/reference checks |
| 9 | BlockEntity/capability/menu boundary | Behavior-boundary checks |
| 10 | Runtime integration | Build/smoke/related audit orchestration |
| 11 | CI report-only mode | Artifact-producing CI-safe report mode |
| 12 | CI hard fail safe categories | Enforce safe mechanical categories |

## 11. Non-goals

The framework must not:

- copy legacy classes into the port;
- treat Vineflower as primary source by default;
- run the full audit every time;
- commit raw local reports;
- call resource parity full gameplay parity;
- fail CI for visual/manual parity questions before those checks are classified;
- hide source conflicts;
- ignore the original jar when both decompiled sources disagree.

## 12. Current note

The existing `item-block-parity` implementation already contains parts of batches 2-6, but future work must now proceed in the planned sequence and classify framework gaps explicitly instead of continuing ad-hoc resource fixing.

2026-06-29 framework wiring note:

- `item_visual_parity` is wired through the orchestrator and reports all registered `TCItems` entries, including missing item models, placeholder implementations and risky block-item display inheritance.
- `legacy_shape_parity` remains the source/manifest shape classifier.
- `legacy_visual_collision_parity` is a stricter original-jar/source-backed report-only audit for block model geometry, facing domains, occlusion contracts, outline/selection-shape contracts, collision contracts and block-item display transform slots.
- `texture_color` now maps active modern `textures/block` and `textures/item` model references back to legacy `textures/blocks` and `textures/items` resources before comparing SHA, dimensions, alpha ratio and sampled average color.
- The `visual` preset is the focused visual evidence run. Current `visual` report is executable and report-only: `legacy_shape_parity` reports `114` rows with `10` review rows, `legacy_visual_collision_parity` reports `585` rows with `2` mismatches (`golem_builder`, `research_table` facing domain), `0` missing rows and `341` unknown rows, `outline_contract` reports `68` match / `0` mismatch / `46` unknown, `item_visual_parity` reports `2009` rows with `34` missing item models plus `219` review rows, `texture_color` reports `201` active texture refs with `176` exact matches and `25` review rows, and `visual_equivalence_completion` reports `17` rows with `11` pass / `6` review / `0` errors.
- These visual-boundary reports do not claim full gameplay or measured pixel parity. They only classify evidence and blockers before targeted fixes.
