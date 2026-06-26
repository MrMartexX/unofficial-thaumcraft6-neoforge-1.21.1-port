# Item/block parity framework alignment

Generated purpose: align the repository with the original item/block parity framework plan and stop ad-hoc resource-fix drift.

## Current interpretation

The item/block parity framework is the current work product. Porting fixes are allowed only when a later implementation batch explicitly uses framework output to close a classified family or subsystem.

## Closed framework batches

| Batch | Name | Status |
|---:|---|---|
| 1 | Source decision + framework plan | Closed |
| 2 | Skeleton scripts and rules | Closed |
| 3 | Primary legacy extractor v1 | Closed |
| 4 | Port extractor v1 | Closed |
| 5 | Safe compare v1 | Closed |
| 6 | Rule overrides | Closed |
| 7 | Secondary legacy probe | Closed |
| 8 | Aspect/research/recipe refs | Closed |
| 9 | BlockEntity/capability/menu boundary | Closed |
| 10 | Runtime integration | Closed |
| 11 | CI report-only mode | Closed |
| 13 | Visual model/transform parity audit | Closed: report-only visual boundary module records model parent, block geometry clues and item display transforms |

## Remaining framework batches

| Planned area | Status | Next action |
|---|---|---|
| 11 | CI report-only mode | Closed: GitHub Actions workflow runs report-only audit presets and uploads local reports as an artifact |
| Batch 12 CI hard fail safe categories | Delayed | Enable only after visual/property/drop false-positive classification improves |

## Precision extension batches

| Batch | Status | Next action |
|---:|---|---|
| 14 | Closed: report-only texture/color parity audit compares referenced port textures against primary legacy resources or original jar fallback | Texture/color parity audit |
| 15 | Closed: reviewed item-transform and model-parent equivalence rule placeholders are wired into the visual model/transform report | Item transform tolerance/equivalence rules |
| 16 | Closed: report-only item property parity audit compares source-evidenced stack size, durability, rarity and behavior/property clues | Item property parity audit |
| 18 | Closed: report-only block property parity audit compares source-evidenced hardness, resistance, sound, light and behavior flags | Block property parity audit |
| 19 | Closed: report-only loot/drop behavior audit compares source-evidenced legacy drop clues against port loot tables and source clues | Loot/drop behavior parity audit |
| 20 | Closed: report-only sound/particle/FX reference audit records source-evidenced SoundType, sound event calls, particle calls and client FX clues | Sound/particle/FX parity audit |
| 21 | Closed: report-only client/server safety audit records client-only references outside client packages, server references in client packages and guarded boundary evidence | Client/server safety audit |
| 22 | Closed: report-only runtime/datapack smoke audit validates static datapack load-critical JSON, namespace layout and pack metadata without claiming gameplay parity | Runtime registry/datapack smoke audit |
| 23 | Closed: report-only GameTest/scripted behavior smoke readiness audit inventories GameTest namespace wiring, Gradle run configs, fixture directories, scripted runtime audit hooks and recommended smoke commands without launching Minecraft | GameTest/scripted behavior smoke framework |
| 24 | Closed: focused filtering generates filtered manifests for Ids, IdPrefix, Families, Packages and ChangedOnly/SinceCommit before checks execute | Focused audit filtering |
| 25 | Closed: focus-aware auto-fix candidate reporter classifies missing/review rows and filters stale local module reports to the current focused manifest IDs | Auto-fix candidate reporter |
| 26 | Closed: layer completion matrix defines completion requirements, known limitations and verifier/CI expectations for each existing item/block parity framework layer | Layer completion matrix |
| 27 | Closed: report schema contract validator audits generated JSON reports for required top-level schema fields and summary/results consistency | Report schema contract |
| 28 | Closed: implemented-check invocation self-test verifies every implemented registry check has an invocation owner and existing comparer/module/script path | Check invocation self-test |
| 29 | Closed: report-only freshness guard detects stale local item/block parity JSON reports by comparing report timestamps with audit scripts, rules and resolved input paths | Report freshness/stale report guard |
| 30 | Closed: report-only status taxonomy validator maps observed report statuses to canonical categories and flags unmapped statuses for rule refinement | Status taxonomy normalization |
| 31 | Closed: report-only docs/registry consistency audit checks check registry, invocation owner rules and framework documentation for drift | Docs/registry consistency audit |
| 32 | Closed: focused filtering expands selected IDs with directly referenced manifest, resource and data dependencies before generating focused manifests | Dependency-aware focused filtering |
| 33 | Closed: CI-safe JSON validity audit parses configured Thaumcraft resource/data JSON and audit rule JSON before resource-boundary checks trust them | JSON validity CI-safe mechanical check |
| 34 | Closed: report-only legacy mapping review inventories direct legacy-to-port ID matches, missing legacy IDs, port-only IDs and conservative rename candidates | Legacy mapping review |
| 35 | Closed: report-only variant split audit inventories metadata/suffix-derived variant families, split candidates, collapsed families and port-only variant families | Variant split audit |
| 36 | Closed: report-only creative tab audit inventories player-facing port manifest IDs observed in creative tab source and review-only unobserved/source-only IDs | Creative tab and player-facing grouping audit |
| 37 | Closed: report-only legacy NBT/data component bridge audit inventories source evidence for typed data components, tag/save-load usage and component-policy candidate IDs | Legacy NBT/data component bridge audit |
| 38 | Closed: report-only networking boundary audit inventories custom payload registration, clientbound/serverbound handlers, validation evidence and mutation-risk source clues | Networking boundary deep audit |
| 39 | Closed: report-only fuel and flammability audit inventories data tags, source burn-time/flammability evidence and manifest fuel/flame candidate IDs | Fuel and flammability parity audit |
| 40 | Closed: report-only tool, armor and equipment link audit inventories item/material/accessory source evidence and manifest equipment candidates | Tool/armor/equipment parity audit |
| 41 | Closed: report-only entity and spawn-egg link audit inventories EntityType, spawn egg, attribute, renderer, data and manifest entity candidate evidence | Entity and spawn-egg link audit |
| 42 | Closed: report-only worldgen-linked block/data audit inventories configured/placed feature, biome modifier, structure/dimension and manifest worldgen candidate evidence | Worldgen-linked block/data audit |
| 43 | Closed: report-only config gate audit inventories config specs, enable/disable gates, feature flags and manifest config candidate IDs | Config gate audit |
| 44 | Closed: report-only access transformer visibility audit inventories AT files, build wiring, visibility/reflection source evidence and manifest/source candidate IDs | Access transformer visibility audit |
| 45 | Closed: report-only public API surface audit inventories legacy and port public declarations, interop/stability clues and manifest/source API candidate IDs | Public API surface audit |
| 46 | Closed: report-only source conflict aggregation inventories primary legacy, secondary legacy and port class presence/hash confidence conflicts | Primary/secondary/port source conflict aggregation |
| 47 | Closed: report-only original jar probe inventories jar class/resource entries, source coverage gaps and manifest/resource candidates | Original jar resource/class probe |
| 48 | Closed: report-only runtime smoke audit inventories Gradle wrapper/build metadata, mod metadata, task clues and explicit opt-in runtime/build commands | Runtime smoke readiness and opt-in execution audit |
| 49 | Closed: verifier v2 framework gate validates PowerShell parser state, rule JSON, registry owner wiring, docs backlog state, report schema/freshness/status taxonomy and report-only audit smoke | Framework verifier v2 certification gate |
| 50 | Closed: CI framework verifier workflow runs verifier v2 in report-only mode and uploads local audit JSON/Markdown artifacts | CI framework verifier workflow and artifact upload |
| 51 | Closed: golden focused family runner defines stable dependency-aware slices for crystals, jars, tables, thaumonomicon and golems | Golden focused family regression slices |
| 52 | Closed: minimal opt-in scripted GameTest/runtime fixture validates server startup and representative Thaumcraft registry entries | Minimal GameTest/runtime fixture coverage |
| 53 | Closed: visual equivalence completion criteria classifies model, texture and FX review blockers before strict visual certification | Visual equivalence completion criteria |
| 54 | Closed: CI strict/safe policy matrix keeps report-only default, safe mechanical hard-fail and strict manual certification boundaries | CI strict/safe matrix policy |
| 55 | Closed: final production-grade framework completion audit (`final_framework_completion`) validates registry, owner, report, verifier, CI and strict-blocker policy consistency | Final framework completion audit |

## Roadmap numbering note

Batch 24 was implemented before Batch 22/23 because focused filtering became necessary after the broad report-only layers made full reports too noisy for day-to-day work. Batch 22 and Batch 23 have since been closed as report-only framework/smoke readiness layers, not gameplay parity claims.

## Rule from this point onward

Do not continue fixing individual resource gaps as the default activity. The next batches should close framework-plan gaps in order.

Batch 25 refinement: auto-fix candidate reports must respect focused manifests when reusing stale local module reports.

Batch 23 closure: scripted behavior smoke is a report-only readiness framework; it inventories wiring and recommended commands but does not launch Minecraft automatically.

Batch 26 closure: the layer completion matrix is the authoritative contract for deciding whether a layer is complete, still planned, intentionally out of scope, superseded or blocked.

Batch 27 closure: report schema validation is report-only framework hardening; review rows identify reports that still need schema/freshness refinement and are not gameplay parity failures.

Batch 28 closure: check invocation self-test is report-only framework hardening; it verifies implemented registry checks have explicit comparer/module/script owners before verifier v2 promotes this to a certification gate.

Batch 29 closure: report freshness validation is report-only framework hardening; review rows identify stale local reports that should be regenerated before being trusted.

Batch 30 closure: status taxonomy validation is report-only framework hardening; review rows identify unmapped report statuses or reports without status fields before verifier v2 treats status semantics as a certification gate.

Batch 31 closure: docs/registry consistency validation is report-only framework hardening; review rows identify registry, invocation-rule or documentation drift before verifier v2 treats docs consistency as a certification gate.

Batch 32 closure: dependency-aware focused filtering is report-only framework hardening; focused manifests now keep selected seed IDs plus directly referenced manifest/resource/data dependencies so focused reports are less likely to omit support blocks, recipes or related wiring evidence.

Batch 33 closure: JSON validity validation is CI-safe framework hardening; it parses configured resource/data and audit-rule JSON files before resource-boundary reports rely on those files.

Batch 34 closure: legacy mapping review is report-only framework hardening; review rows identify direct mapping gaps, possible renames and port-only IDs before verifier v2 treats registry identity mapping as a certification gate.

Batch 35 closure: variant split validation is report-only registry-identity hardening; review rows identify metadata/suffix-derived split, collapse, missing and port-only variant families before verifier v2 treats variant policy as a certification gate.

Batch 36 closure: creative tab validation is report-only resource-boundary hardening; review rows identify player-facing manifest IDs not directly observed in creative tab source and source-only creative references before verifier v2 treats creative grouping as a certification gate.

Batch 37 closure: data component bridge validation is report-only behavior-boundary hardening; review rows identify legacy tag/save-load usage, ItemStack NBT candidates and typed data component evidence before verifier v2 treats stack-state migration policy as a certification gate.

Batch 38 closure: networking boundary validation is report-only behavior-boundary hardening; review rows identify payload registration, clientbound/serverbound handler evidence, validation clues and mutation-risk source patterns before verifier v2 treats networking policy as a certification gate.

Batch 39 closure: fuel and flammability validation is report-only data-boundary hardening; review rows identify burn-time, flammability, combustible tag and manifest fuel/flame candidates before verifier v2 treats fuel/flammability policy as a certification gate.

Batch 40 closure: equipment validation is report-only behavior-boundary hardening; review rows identify tool, armor, material and accessory source evidence plus manifest equipment candidates before verifier v2 treats equipment policy as a certification gate.

Batch 41 closure: entity link validation is report-only data-boundary hardening; review rows identify EntityType, spawn egg, attribute, renderer, data-resource and manifest entity candidates before verifier v2 treats entity/spawn-egg policy as a certification gate.

Batch 42 closure: worldgen link validation is report-only data-boundary hardening; review rows identify configured/placed feature, biome modifier, structure/dimension, data-resource and manifest worldgen candidates before verifier v2 treats worldgen link policy as a certification gate.

Batch 43 closure: config gate validation is report-only data-boundary hardening; review rows identify config specs, enable/disable gates, feature flags, config-backed resources and manifest config candidates before verifier v2 treats feature-gate policy as a certification gate.

Batch 44 closure: access transformer validation is report-only source-quality hardening; review rows identify legacy and port AT files, build wiring, visibility/reflection source evidence and candidate IDs before verifier v2 treats visibility-widening policy as a certification gate.

Batch 45 closure: public API validation is report-only source-quality hardening; review rows identify legacy and port public declarations, interop clues, stability markers and candidate IDs before verifier v2 treats public API surface policy as a certification gate.

Batch 46 closure: source conflict aggregation is report-only source-quality hardening; review rows identify primary-only, secondary-only, legacy-only, port-only and primary/secondary hash conflicts before verifier v2 treats source authority as a certification gate.

Batch 47 closure: original jar probe validation is report-only source-quality hardening; review rows identify jar class/resource entries, legacy source coverage gaps, port overlap and manifest/resource candidates before verifier v2 treats original jar evidence as a certification gate.

Batch 48 closure: runtime smoke validation is report-only runtime-boundary hardening; rows inventory Gradle wrapper/build metadata, mod metadata, runtime task clues and explicit opt-in commands before verifier v2 treats runtime execution readiness as a certification gate.
Batch 49 closure: verifier v2 validation is report-only framework certification hardening; it validates parser state, rule JSON, check registry owner wiring, docs backlog state, report summaries and full audit smoke before CI/strict modes rely on the framework as authoritative.

Batch 50 closure: CI verifier validation is report-only workflow hardening; it runs verifier v2 from GitHub Actions, publishes generated local report artifacts and keeps strict mode opt-in until runtime/visual policies are promoted.

Batch 51 closure: golden focused family validation is report-only focus-control hardening; it defines stable dependency-aware family slices and a runner for quick regression checks before broad reports are reviewed.

Batch 52 closure: minimal GameTest fixture validation is report-only runtime-boundary hardening; it adds an opt-in server-started fixture that writes a runtime report for representative registry/bootstrap coverage and remains separate from strict gameplay parity.

Batch 53 closure: visual equivalence completion validation is report-only visual-boundary hardening; it defines strict-promotion blockers for model transforms, texture/color parity and sound/particle/FX evidence without treating unresolved review rows as mechanical errors.

Batch 54 closure: CI strict/safe policy validation is report-only workflow hardening; it documents report-only, safe and strict policy boundaries, keeps strict certification manual/opt-in and adds a policy audit report consumed by verifier v2.

Batch 55 closure: `final_framework_completion` validation is report-only framework certification hardening; it verifies that all registered checks are implemented, owned, documented, report-backed, verifier-covered and CI-policy consistent while preserving the boundary that this is not a gameplay parity completion claim.
