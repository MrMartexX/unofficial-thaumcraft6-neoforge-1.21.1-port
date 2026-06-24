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
| 22 | Deferred: dedicated runtime registry/datapack load smoke audit remains reserved; Batch 10 already provides explicit -RunBuild/-RunSmoke/-RunRelatedAudits orchestration | Runtime registry/datapack smoke audit |
| 23 | Deferred: GameTest/scripted behavior smoke framework remains reserved until runtime fixtures are stable | GameTest/scripted behavior smoke framework |
| 24 | Closed: focused filtering generates filtered manifests for Ids, IdPrefix, Families, Packages and ChangedOnly/SinceCommit before checks execute | Focused audit filtering |
| 25 | Closed: focus-aware auto-fix candidate reporter classifies missing/review rows and filters stale local module reports to the current focused manifest IDs | Auto-fix candidate reporter |

## Roadmap numbering note

Batch 24 was implemented before Batch 22/23 because focused filtering became necessary after the broad report-only layers made full reports too noisy for day-to-day work. Batch 22 and Batch 23 remain reserved/deferred roadmap slots, not silently skipped implementation claims.

## Rule from this point onward

Do not continue fixing individual resource gaps as the default activity. The next batches should close framework-plan gaps in order.

Batch 25 refinement: auto-fix candidate reports must respect focused manifests when reusing stale local module reports.