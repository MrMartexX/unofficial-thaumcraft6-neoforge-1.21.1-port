# Item/block visual equivalence completion criteria

Batch 53 adds a report-only completion policy for the visual boundary. It does not claim that all visual parity is complete; it defines how visual/model/texture/FX review rows block promotion to strict mode.

## Added files

- `tools/audits/item-block-parity/modules/visual_equivalence_completion.ps1`
- `tools/audits/item-block-parity/rules/visual-equivalence-completion.json`

## Required upstream reports

- `item_block_visual_model_transform_report.json`
- `item_block_texture_color_report.json`
- `item_block_sound_particle_fx_report.json`

## Policy

The visual boundary remains report-only. Review rows are not mechanical failures, but they block strict visual certification until each row is resolved as direct evidence, reviewed equivalence, intentional modern difference, superseded, blocked or intentionally out of scope.

## Strict promotion blockers

- model transform `reviewNeeded` or `missing` rows;
- texture/color `reviewNeeded` rows;
- sound/particle/FX review-like rows, including missing and not-evidenced statuses.

## Recommended command

```powershell
pwsh -NoProfile -File tools/audits/item-block-parity/audit-item-block-parity.ps1 -RepoRoot . -Checks visual_boundary,texture_color,sounds_particles,visual_equivalence_completion -UseCachedLegacy -FailMode off
```
