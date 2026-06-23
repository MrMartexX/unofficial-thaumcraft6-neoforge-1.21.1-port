# Item/block parity framework alignment

Generated purpose: align the repository with the original item/block parity framework plan and stop ad-hoc resource-fix drift.

## Current interpretation

The item/block parity framework is the current work product. Porting fixes are allowed only when a later implementation batch explicitly uses framework output to close a classified family or subsystem.

## Current already-present implementation

The repository already has a working vertical slice:

- orchestrator script;
- primary legacy manifest extractor;
- live port manifest extractor;
- safe comparer;
- report validator;
- rule files for known renames, variants, extras, no-item/no-loot and source policy;
- registry, duplicate ID, block item pair, blockstate, model, lang and loot checks;
- gap candidate/report helpers.

## Gap against original plan

| Planned area | Status | Next action |
|---|---|---|
| Batch 1 source decision + framework plan | Closed by this alignment batch | Keep docs authoritative |
| Batch 2 skeleton scripts and rules | Partially implemented | Normalize presets and check registry to final plan |
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
