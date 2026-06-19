# Infusion Legacy Container and Essentia Cycle Audit

Generated: 2026-06-19 16:46:59 +03:00

## Source

- Legacy source file: 02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java
- SHA-256: 5f6a75bad068972ac319b46c2d1263e9e33f0690c3dee2fce29afe58bef26b82

## Required method anchors

| Legacy method | First line | Why it matters |
|---|---:|---|
| craftingStart(...) | 291 | Confirms start captures a plan before mutation. |
| craftCycle(...) | 281 | Confirms mutation belongs to a later cycle boundary. |
| craftingFinish(...) | 546 | Confirms output/finalization is a separate boundary. |

## Focused keyword evidence

| Topic | First matching lines | Porting note |
|---|---|---|
| craft start state capture | 90, 92, 94, 95, 96, 121, 123, 125, 126, 127, 150, 151, 159, 160, 175, 178, 179, 181, 206, 209, 210, 212, 213, 215 | start records recipe state; executor must not invent new inputs after start |
| craft cycle mutation loop | 90, 96, 101, 121, 127, 132, 150, 151, 159, 160, 281, 340, 348, 358, 361, 456, 457, 479, 482, 496, 497, 498, 501, 534 | legacy mutation is cycle-based, not immediate one-shot gameplay |
| essentia/source drain handling | 28, 69, 72, 90, 99, 121, 130, 151, 160, 348, 456, 472, 473, 479, 482, 496, 497, 498, 500, 501, 521, 534, 537, 557 | source/drain timing must be modeled separately from component consumption |
| component/catalyst inventory mutation | 22, 91, 92, 94, 95, 104, 122, 123, 125, 126, 134, 168, 171, 175, 178, 179, 181, 193, 195, 206, 209, 210, 212, 213 | component/catalyst mutation should be atomic only after readiness is rechecked |
| container-item handling | 50, 66, 78, 524, 948, 953, 958, 963, 968, 973, 978 | container item policy must be explicit before executor becomes player-facing |
| final output boundary | 92, 94, 123, 125, 175, 178, 179, 206, 209, 210, 212, 213, 215, 216, 332, 333, 334, 335, 338, 525, 546, 547, 633, 655 | output placement/spawn belongs to finish boundary, not start boundary |
| instability and effects | 18, 32, 61, 63, 67, 71, 72, 96, 99, 127, 130, 150, 159, 340, 351, 358, 403, 408, 457, 459, 473, 474, 521, 552 | effects remain outside the first executor slice |

## Focused porting conclusion

- The current port should not connect item mutation to normal player-facing activation yet.
- The next safe code batch may add an executor, but it must remain audit-driven and callable only from validation/test paths until craft timing is proven.
- The executor must treat TCInfusionCompletionPlan as the final precondition check. If readiness fails, it must perform no mutations.
- The first executor must be all-or-nothing for component pedestal consumption and center catalyst/result handling.
- Container-item behavior must be documented as a temporary explicit policy if not fully legacy-parity yet.
- Essentia network/source draining must remain separate from the first item mutation executor unless a dedicated source interface is audited.
- Instability, beam/particle/sound effects, flux/taint side effects, jars/tubes and Thaumatorium remain out of scope.

## Recommended next code slice

1. Add TCInfusionMutationExecutor with a method that accepts matrix plus TCInfusionCompletionPlan.
2. Reject missing/invalid completion plan before any mutation.
3. Re-fetch and verify center pedestal and component pedestals immediately before mutation.
4. Consume exactly the component stacks recorded by the active plan.
5. Replace the center catalyst with the recipe result or return a result action for a later output policy.
6. Clear the active plan only after every mutation succeeds.
7. Add runtime audit checks for valid execution, failed precondition no-op, changed pedestal no-op and active plan cleanup.