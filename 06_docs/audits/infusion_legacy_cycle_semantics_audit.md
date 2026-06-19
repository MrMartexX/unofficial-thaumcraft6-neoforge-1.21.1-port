# Infusion Legacy Cycle Semantics Audit

Generated: 2026-06-19 16:42:49 +03:00

## Source

- Legacy source file: 02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java
- SHA-256: 5f6a75bad068972ac319b46c2d1263e9e33f0690c3dee2fce29afe58bef26b82

## Method anchors

| Legacy method | First line | Porting interpretation |
|---|---:|---|
| craftingStart(...) | 291 | Capture and validate start state; do not immediately consume catalyst, components or essentia. |
| craftCycle(...) | 281 | Per-cycle mutation timing; must be modeled separately from start-plan validation. |
| craftingFinish(...) | 546 | Final result/output boundary; should not be guessed before cycle semantics are modeled. |
| getSurroundings(...) | 260 | Matrix/pedestal discovery and stability context reference. |

## Keyword evidence map

| Topic | First matching lines | Note |
|---|---|---|
| crafting start entry | 291, 834 | start boundary and initial recipe-state capture |
| craft cycle entry | 281, 361 | per-cycle mutation timing boundary |
| craft finish entry | 546, 655 | final output/completion boundary |
| pedestal surroundings | 260, 298, 699 | legacy pedestal discovery and structure context |
| essentia/aspect drain mentions | 48, 49, 50, 69, 78, 90, 121, 151, 160, 341, 342, 343, 348, 456, 479, 481 | drain/source semantics must be audited before mutation executor |
| inventory/item mutation mentions | 22, 50, 66, 78, 91, 95, 104, 134, 168, 171, 175, 181, 195, 206, 212, 213 | item/container timing must not be guessed |
| instability/effects mentions | 18, 32, 63, 67, 71, 72, 96, 99, 127, 130, 150, 159, 340, 351, 358, 457 | effects remain separate from first mutation boundary |

## Porting conclusion

- The current port already models the start-plan and read-only completion-readiness boundary.
- A one-shot gameplay executor should not be connected directly to player-facing matrix activation yet.
- The next safe batch is a small, explicit mutation executor boundary that is still audit-driven and does not add instability, particles, beams, sounds, jars, tubes or broad essentia network behavior.
- Before the executor consumes anything, it must re-read the current center catalyst, matched component pedestal positions/stacks and available aspects from the existing `TCInfusionCompletionPlan`.
- Container-item behavior and essentia drain/source timing remain the main unresolved legacy parity risks.

## Next recommended implementation slice

1. Add a non-player-facing `TCInfusionMutationExecutor` or equivalent helper.
2. Input: active `TCInfusionCraftingPlan` plus `TCInfusionCompletionPlan`.
3. Preconditions: completion plan must be valid and still match catalyst/component pedestal positions.
4. Mutations allowed in this slice: consume matched component pedestal stacks, consume or replace center catalyst according to an explicit policy, clear active plan, expose result/output readiness.
5. Mutations not allowed yet: instability events, particles, beams, sound loops, essentia network drain, flux rifts, taint, jars, tubes, Thaumatorium or golem automation.
6. Add runtime audit checks for all-or-nothing mutation failure cases before exposing this to normal gameplay triggers.