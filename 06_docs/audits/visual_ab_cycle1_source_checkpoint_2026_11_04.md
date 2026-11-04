# Visual A/B Harness Cycle 1 source checkpoint

Prepared: 2026-11-04. Implementation branch based on remediation HEAD b0c972a2; research source pinned separately to main 72eaac17.

## Scope

This slice adds non-mutating host diagnostics, machine contracts, and an imported G00-G12 source group catalog. It does **not** implement client scene creation, screenshot interception, video recording, automatic comparison, or visual-parity decisions.

## Inventory facts carried forward

- 13 groups and 37 consolidated finding IDs from draft PR #11 are mapped. The source register also cites five W-series watchlist IDs.
- The canonical research master in draft PR #10 extends through F136; it has not been exhaustively reduced into test states. No false all-research-complete claim is made.
- Existing fixture tooling is in the remediation branch: paired-flat-world documentation, PRISTINE/RESET archives and world verification scripts.
- Local instance inventory reports a Forge 1.12.2 reference with Baubles/JEI and NeoForge 1.21.1 development client with Curios/JEI/TerraBlender.
- Graphics are not yet normalized (gamma, render distance, GUI scale and FPS differ).
- No original screenshot pair exists in the supplied snapshot; current visual A/B proof remains NOT_RUN.

## Boundary and pending validation

- No code of the mod, assets, world save or existing CI workflow is modified.
- JSON catalog structure was checked when generated. The PowerShell CLI and tests still require real pwsh 7 execution in the user's Windows environment.
- The doctor does not launch Minecraft or change graphics options; it can optionally write one diagnostic report to an ignored local path.
- Branch state, local dirty files and exact runtime SHA must be checked again before actual client testing.

## Cycle 1 completion criteria

- [x] Source provenance preserved for groups and findings.
- [x] No findings silently unmapped in the imported 37-row register.
- [x] Environment-config template defined without credentials or hardcoded local paths.
- [x] Read-only preflight implementation prepared.
- [x] Initial PowerShell 7 catalog and Environment Doctor executed on Windows 10/11 (PowerShell 7.6.9; 0 errors, 4 graphic warnings; detached runtime SHA ee48907e).
- [x] New combined ledger/fixtures smoke test executed on Windows; 13 groups, 37 findings, 6 watchlist, 4 world directories and 2 candidate pairs passed.
- [x] Candidate local fixture paths inspected: PRIMARY and RESET_20261103_213123_465257; all four have level.dat and region files.
- [ ] Selected paired worlds actually loaded and verified in clients; structure alone is insufficient.
- [ ] Detailed state-level catalog prepared and every setup command verified in both clients.

Cycle 1 remains **IN_PROGRESS** until the three unchecked gates close.

## Incremental source-only additions

- `catalog/v0-finding-ledger.json`: preserved all 37 finding records with legacy/modern source anchors and 6 watchlist records; W-02 is explicitly unmapped, rather than lost.
- `ab.ps1 -Action findings`: checks one-to-many group assignments and provenance. No client observation status changes.
- `ab.ps1 -Action fixtures -SaveReport`: read-only PRIMARY/RESET pairing and level.dat/region presence. No NBT parse, client launch or data mutation.
- `tests/validate-cycle1.ps1`: one combined offline smoke for the above.
- `visual_ab_calibration_protocol_v1_ru.md`: G00 profile and client camera verification instructions. No automatic edits to options.txt.

Latest Doctor report on the user host: `doctor-20261104-183251.json`, with 0 errors and four expected graphics WARNs. The report is local and ignored by Git, not part of this commit. New additions await a single Windows execution after updating the test worktree.

## Windows fixture confirmation and staged pilot

The user supplied the actual PowerShell 7.6.9 outputs for `validate-cycle1.ps1` and `-Action fixtures -SaveReport`; both succeeded. `fixtures-20261104-184538.json` lists two candidate pairs and no warnings. It records **clientVerification=NOT_RUN** for all four worlds. The local report is not committed.

`visual_ab_first_client_runbook_v1_ru.md` now stages G00 + pilot with the corrected RESET pair, immutable initial-book capture before research unlock, and an explicit check that the modern runtime SHA belongs to the **original development checkout**, not the isolated Harness worktree. First client validation and state-level source-to-client recipe confirmation remain open gates.
