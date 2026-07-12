# Research progression parity audit

Status: implemented and runtime-audited on `2026-06-04`.

This document records the exact Forge 1.12.2 `ResearchManager.progressResearch` behavior now preserved by the NeoForge research progression core.

## Authoritative legacy references

- `thaumcraft/common/lib/research/ResearchManager.java`
- `thaumcraft/api/research/ResearchEntry.java`
- `thaumcraft/api/research/ResearchStage.java`
- `thaumcraft/api/research/ResearchAddendum.java`
- `thaumcraft/common/config/ConfigResearch.java`

## Implemented parity behavior

- Stage advancement preserves the legacy empty-gate auto-skip rules.
- Stage warp preserves the legacy calculation, including the original double-add behavior for ordinary gated stages and the combined previous/final-stage behavior when an empty final gate is skipped.
- Warp values above one are split exactly like legacy: the larger half is permanent and the smaller half is normal.
- Common config `wussMode` disables research progression warp awards like legacy.
- Checked stage completion builds the complete item and knowledge consume plan before mutating inventory, knowledge, or stage state.
- Completion applies `POPUP` and `RESEARCH` flags using the existing `noResearchFlag` boundary.
- Completion grants entry-level `reward_item` and `reward_knowledge` values when sync/reward processing is enabled.
- Completion scans already-completed entries for newly triggered addenda, sets the `PAGE` flag, and sends the addendum notification.
- Sibling completion and the legacy five experience-point award remain in the progression path.
- Final knowledge sync occurs after the committed server-side mutation.

## Research data parity harness

The harness lives under `07_Test_Instance_and_Comparisons/research_data_parity`.

It compares:

1. the eight authoritative legacy research JSON files;
2. the NeoForge server-data research JSON copies;
3. the normalized runtime data produced by the NeoForge parser;
4. the seven Java-registered legacy category definitions from `ConfigResearch.initCategories`.

Latest result:

| Check | Result |
|---|---:|
| Legacy entries | `148` |
| NeoForge source differences | `0` |
| NeoForge runtime parser differences | `0` |
| Legacy category differences | `0` |
| Progression/parser semantic checks | `10/10` passed |

The semantic checks cover gated start, non-final and final warp calculation, empty-stage completion, final empty-stage combined warp, warp split values `1/2/3/5`, and parser preservation of reward/addendum fields.

## Regression results

- Research requirements: `69/69` item, `34/34` craft, `170/170` knowledge resolved; `0` unresolved; `16` documented subsystem bridge warnings.
- Research Table diagnostics: `59/59` passed.
- Dedicated server research reload: `7` categories, `148` entries, `271` stages, `16` addenda; `0` unresolved research references.

## Remaining boundaries

- A modern cancellable equivalent for legacy `ResearchEvent.Research` and `ResearchEvent.Knowledge` is not yet defined.
- Full warp events, effects, client sync, and consequences remain outside the minimal warp storage bridge.
- Built-in TC6 research JSON does not currently use entry rewards, so reward handling is parser/contract-tested but still needs a real addon/runtime fixture before it is treated as integration-proven.
- Research recipe references are page/catalog identifiers, not simple vanilla recipe unlock ids. The permanent catalog now preserves all `253` research occurrences, `203` direct references, and `325` entries including group members with `0` comparison differences.
- Server-authoritative Thaumonomicon index/entry/action/drilldown payloads, item/open flow, first browser/entry/search screens, recipe drilldown/history, and the first vanilla crafting, arcane, crucible and infusion page snapshots/renderers are implemented and the protocol audit passes `34/34`. The audit covers exact legacy browser start, checked stage advance, entry acknowledgement flag semantics and known-entry final-stage progression, visibility filtering, server-owned unlockability/flags, client-cache invalidation, server-built revision freshness, stale-action and stale-drilldown rejection without mutation, explicit-open-versus-refresh separation, valid server snapshots for every live crafting/arcane/crucible/infusion catalog entry, and server-side output-stack drilldown resolution. Browser search filters only the server-visible index and reuses the same action payload path as graph clicks. The remaining boundary is final visual parity and remaining blueprint/fake/special recipe-page systems/renderers.
