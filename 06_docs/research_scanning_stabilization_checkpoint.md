# Research, knowledge and scanning stabilization checkpoint

Status: stabilization checkpoint for the `research-knowledge-scanning-design` branch after the first working Thaumometer scan/research slice.

This note records what is currently considered stable enough to build on, what is intentionally narrow, and what must still remain blocked until a later design slice.

## Current confirmed scope

The current branch contains a working server-side research/scanning skeleton:

- player knowledge storage for observation/theory raw values;
- completed research key storage;
- research stage and flag storage;
- reloadable research JSON entries under `data/thaumcraft/research`;
- reloadable scannable definitions under `data/thaumcraft/scannables`;
- debug command trees under `/thaumcraft`, `/thaum`, and `/tc`;
- read-only scan debug commands;
- Thaumometer right-click scan mutation;
- completed-research-key client cache for highlight gating;
- scan/audit parity tooling and generated runtime reports.

The local smoke tests after stabilization confirmed that commands and client-side highlight behavior still work after the latest payload and scan command text cleanup.

## Stabilization fixes already applied

- Current-stage advancement now validates stage requirements before consuming inventory or knowledge.
- Item requirement consumption is simulated on copied inventory stacks before mutating the real player inventory.
- Knowledge costs are checked in raw units before mutation.
- Research entry lookup now canonicalizes legacy-shaped references by stripping `~` and dropping `@stage` before entry lookup.
- Debug scan command text now states that command scans are read-only while Thaumometer use may mutate scan/research/knowledge state.
- The client sync payload field is named `completedResearchKeys` to clarify that it is not full legacy `PlayerKnowledge` sync.
- Large generated parity JSON files are marked as generated/no-diff artifacts in `.gitattributes`.
- The scan parity README documents that generated dumps/reports must be regenerated, not edited manually.

## Important current limitation

`TCKnowledgeSyncPayload` is intentionally a narrow client cache payload. It only synchronizes completed research keys for client-side scan highlight filtering. It does not synchronize full player knowledge, stages, flags, observation/theory values, recipes, rewards, popups, or Thaumonomicon page state.

This is acceptable for the current slice because the only client consumer is the highlight/overlay gating path. A future Thaumonomicon UI slice must define a separate full research/knowledge sync payload.

## Current debug command interpretation

The scan debug commands are intentionally read-only:

```text
/tc scan held
/tc scan looking
/tc scan audit_items
/tc scan audit_entities
```

They should be used to inspect aspect lookup, scan predicate matching and audit output without changing the player's knowledge state.

Thaumometer right-click is the gameplay-like mutation path for the current slice. It attempts to scan the looked target, grants newly unknown scan research keys through `TCResearchManager.progressResearch`, and shows the current legacy-like known/unknown feedback message.

## Research stage interpretation

The current stage-advance path is still a server-side skeleton for future Thaumonomicon page actions. It checks and consumes:

- required item stacks where the legacy requirement can be resolved to a modern item or tag;
- required knowledge values using legacy raw unit conversion;
- required research keys and staged references;
- craft markers for modern resolvable craft events.

Exact direct legacy `ItemStack.toString().hashCode()` craft marker parity is not solved. Do not guess those marker ids. They require a separate 1.12 runtime exporter/mapping pass.

## Generated artifact policy

The parity JSON files under `07_Test_Instance_and_Comparisons/*_parity/dumps` and `reports` are generated audit artifacts. They are committed for reproducibility, but they are not hand-maintained source files.

When scan/aspect behavior changes:

1. regenerate the runtime dumps;
2. regenerate the JSON and Markdown reports;
3. review the Markdown summaries first;
4. keep generated JSON diffs out of normal code review unless the report reveals a real behavior change.

## Still blocked

Do not treat the current branch as a full research system yet. These areas remain blocked:

- Thaumonomicon GUI and page interaction flow;
- full stage/flag/knowledge sync for UI;
- recipe unlocks;
- research rewards;
- addendum notifications;
- warp;
- theorycrafting;
- exact legacy direct craft-hash mapping;
- ScanSky celestial-note side effects;
- complete Thaumcraft custom entity scan/aspect handling before those entities exist.

## Recommended next implementation order

1. Keep this branch stable and avoid adding unrelated gameplay systems.
2. Add validation/reporting for unresolved `required_item` and `required_craft` entries.
3. Extract legacy item/tag requirement mapping out of `TCResearchManager` if it grows further.
4. Add a dedicated 1.12 exporter for direct craft-hash markers.
5. Only after that, design the full Thaumonomicon research UI sync and page action flow.
