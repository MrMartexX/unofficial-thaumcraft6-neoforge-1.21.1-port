# Item and block parity framework

## Purpose

The framework produces evidence-backed item/block parity reports without calling resource existence "full parity". It compares a fingerprinted legacy baseline, a live port manifest and explicit mapping/deferral rules.

## Corrections to the proposed plan

1. **Batch 1 is an executable vertical slice.** Documentation-only and skeleton-only batches would postpone feedback while establishing untested interfaces. The first batch therefore includes source policy, schemas, primary/live extractors, a safe comparer and a local report.
2. **The original jar is not merely an asset fallback.** It is the packaged class/resource inventory and the tie-breaker when decompiles disagree.
3. **Regex evidence is confidence-scored.** An inferred camelCase registry name is not equivalent to an explicit registry string. Unresolved entries remain visible and cannot produce a parity pass.
4. **Legacy variants are separate from registry IDs.** Metadata variants such as `ingot:thaumium` must be mapped to modern split IDs explicitly.
5. **Safe failures apply only to implemented checks.** Unsupported behavior, texture graph, runtime and visual checks are marked `NOT_EVALUATED`, not passed.
6. **CI is deferred until the baseline is classified.** The initial local run uses `FailMode off`; safe CI enforcement starts only after known dynamic models, intentional no-loot entries and renames are encoded.
7. **Generated reports stay local.** Only source decisions and milestone summaries belong under `06_docs/audits/`.

## Evidence layers

| Layer | Meaning |
|---|---|
| Registry identity | Explicit or mapped legacy ID exists in the matching modern registry |
| Resource boundary | Required blockstate/model/lang/loot files exist for the selected modern entry |
| Data boundary | Recipes, tags, aspects and research references resolve |
| Behavior boundary | Block entity, capabilities, menu/network and side ownership match the intended contract |
| Runtime parity | A dedicated runtime fixture/exporter verifies behavior and values |
| Visual parity | Client inspection or deterministic render evidence verifies appearance |

No lower layer implies a higher one.

## Batch 1 scope

Implemented now:

- authoritative source policy;
- fingerprinted primary legacy manifest;
- live port block/item/resource manifest;
- explicit evidence confidence;
- registry identity/duplicates, block-item pair, blockstate, model, lang and loot existence comparison;
- ID filtering, presets, `ListChecks`, `ExplainPlan` and `off/safe/strict` fail modes;
- JSON and Markdown local reports.

Not evaluated yet:

- recursive model parent/texture resolution;
- metadata-to-split-ID variant mapping;
- secondary decompiler and jar probes;
- recipes, tags, aspects and research references;
- block entities, capabilities, menus, networking and client/server ownership;
- build, server smoke, gameplay runtime and visual parity.

## Corrected implementation sequence

1. **Executable baseline:** source policy, schemas, primary/live manifests and safe comparison.
2. **Identity normalization:** known renames, metadata variants, no-item/no-loot rules and allowed modern extras.
3. **Resource graph:** recursive blockstate/model/texture resolution and orphan detection.
4. **Data references:** recipes, loot contents, tags, aspects, scanning, research and Thaumonomicon pages.
5. **Behavior boundary:** block entities, data components, capabilities, menus, payloads and side safety.
6. **Source conflict probes:** secondary decompile and original jar inventory.
7. **Runtime orchestration:** build, datapack load, server smoke and related subsystem audits.
8. **CI report-only:** publish local artifacts without hard failure.
9. **CI safe enforcement:** fail only stable, low-false-positive checks.
10. **Family parity milestones:** curated summaries plus manual visual verification where required.

## Commands

```powershell
# Refresh both manifests and write a report without failing on the unclassified baseline.
.\tools\audits\item-block-parity\audit-item-block-parity.ps1 `
  -RefreshLegacy -Preset quick -FailMode off

# Inspect only the smelter family.
.\tools\audits\item-block-parity\audit-item-block-parity.ps1 `
  -Ids thaumcraft:smelter_basic,thaumcraft:smelter_thaumium,thaumcraft:smelter_void `
  -Preset resources -FailMode safe

# Show the implemented execution plan.
.\tools\audits\item-block-parity\audit-item-block-parity.ps1 -ExplainPlan
```

Raw output is written to `tools/reports/local/item-block-parity/` and is intentionally ignored by Git.
