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
| 9 | BlockEntity/capability/menu boundary | Closed: report-only behavior boundary module compares legacy TileEntity/inventory/GUI clues with live port BlockEntity/capability/menu evidence |

## Remaining framework batches

| Planned area | Status | Next action |
|---|---|---|
| Batch 10 runtime integration | Missing | Add RunBuild, RunSmoke and RunRelatedAudits orchestration |
| Batch 11 CI report-only mode | Missing | Add workflow artifact mode after local reports stabilize |
| Batch 12 CI hard fail safe categories | Missing | Enable only safe categories after classification |

## Rule from this point onward

Do not continue fixing individual resource gaps as the default activity. The next batches should close framework-plan gaps in order.
