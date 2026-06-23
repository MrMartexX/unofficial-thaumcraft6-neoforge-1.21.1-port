# Item/block parity framework alignment

Generated purpose: align the repository with the original item/block parity framework plan and stop ad-hoc resource-fix drift.

## Current interpretation

The item/block parity framework is the current work product. Porting fixes are allowed only when a later implementation batch explicitly uses framework output to close a classified family or subsystem.

## Closed framework batches

| Batch | Name | Status |
|---:|---|---|
| 1 | Source decision + framework plan | Closed |
| 2 | Skeleton scripts and rules | Closed: final orchestrator contract, preset names and check registry skeleton exist |

## Remaining framework batches

| Planned area | Status | Next action |
|---|---|---|
| Batch 3 primary legacy extractor v1 | Partially implemented | Expand manifest fields for class roles, behavior clues, tile/menu/render references and variant hints |
| Batch 4 port extractor v1 | Partially implemented | Expand live manifest for BE/menu/capability/data references |
| Batch 5 safe compare v1 | Partially implemented | Add texture graph and orphan reference coverage |
| Batch 6 rule overrides | Partially implemented | Add deferred-boundaries and broader source-policy integration where missing |
| Batch 7 secondary legacy probe | Missing | Implement explicit probe-only module |
| Batch 8 aspect/research/recipe refs | Missing in item/block framework | Integrate existing aspect/research/recipe audit outputs as related checks |
| Batch 9 BE/capability/menu boundary | Missing in item/block framework | Add behavior-boundary modules |
| Batch 10 runtime integration | Missing | Add RunBuild, RunSmoke and RunRelatedAudits orchestration |
| Batch 11 CI report-only mode | Missing | Add workflow artifact mode after local reports stabilize |
| Batch 12 CI hard fail safe categories | Missing | Enable only safe categories after classification |

## Rule from this point onward

Do not continue fixing individual resource gaps as the default activity. The next batches should close framework-plan gaps in order.
