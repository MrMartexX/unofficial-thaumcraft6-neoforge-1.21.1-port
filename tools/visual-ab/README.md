# Thaumcraft Visual A/B Harness: Cycle 1 foundation

Status: **initial infrastructure, not a finished client visual-parity verifier**.

This branch is based on the remediation branch containing the existing paired comparison worlds. No game logic, world saves, existing fixture builders, PR #10 or PR #11 are modified by this slice.

## Files

- ab.ps1: read-only preflight and catalog-integrity commands.
- catalog/v0-groups.json: exact G00-G12 group mapping imported from draft PR #11, at pinned SHA 66d23966.
- schemas/: machine contracts for a future detailed scene catalog and A/B results.
- config.example.json: host configuration template. Never fill real local paths into tracked files.
- tests/validate-catalog.ps1: PowerShell 7 catalog smoke test.

The catalog intentionally contains broad group-level prerequisites. Preparation for each client is marked UNVERIFIED, and detailed state-level commands have not yet been designed or executed. An item in the source report is **not** an observed client defect.

## Windows 10/11 quick start (after checking out this branch)

1. Verify PowerShell 7 is installed by running: pwsh --version
2. Copy config.example.json to tools/reports/local/visual-ab/config.local.json.
3. Edit repositoryRoot and the two gameDirectory fields in the copied file. Leave the tracked example unchanged.
4. Validate the source catalog:
   
       pwsh -NoProfile -File .\tools\visual-ab\ab.ps1 -Action catalog

5. Inspect the configured local environment without launching Minecraft:

       pwsh -NoProfile -File .\tools\visual-ab\ab.ps1 -Action doctor -SaveReport

6. Test the importer invariant:

       pwsh -NoProfile -File .\tools\visual-ab\tests\validate-catalog.ps1

The optional doctor report is created only under the existing Git-ignored tools/reports/local/visual-ab directory.

## Guardrails

- Do not run Restore clean test worlds.bat just for this diagnostic.
- No save data, JARs, screenshots or launcher tokens are copied or uploaded by ab.ps1.
- The doctor checks filenames, configured paths, known worlds and selected options.txt keys; it does not prove that scenes actually load.
- G00 graphics calibration is still pending; the user inventory showed differences in gamma, render distance, GUI scale and FPS.
- The original research baseline 72eaac17 is not the same as the local remediation runtime HEAD b0c972a2. Future reports must store both.
- Avoid reusing old READY archives with an elevated platform. The RESET worlds may need their first game load to initialize region data.
- No gameplay PASS may be produced by these commands.

## Research provenance

- Source register: draft PR #11, 37 findings, 13 G-groups, source baseline 72eaac17a1e1c0c51b27cb151ef7f244944c89d5.
- Master research: draft PR #10 through F136. Additional D/H/N/F records require a separate full mapping review.
- Paired-world guide: 07_Test_Instance_and_Comparisons/paired_flat_worlds/README.md and CHECKLIST_RU.md in the remediation branch.
- Local environment inventory: TC6_AB_inventory.json supplied on 2026-11-04 (not committed).

## Cycle 1 incremental additions (04.11.2026)

The Windows `doctor` run on the user's isolated detached worktree `D:\TC6_AB_Cycle1` succeeded on PowerShell 7.6.9: **0 errors, 4 expected graphics-calibration warnings**. Runtime game verification remains `NOT_RUN`.

Additional read-only commands are now available:

```powershell
pwsh -NoProfile -File D:\TC6_AB_Cycle1\tools\visual-ab\ab.ps1 -Action findings
pwsh -NoProfile -File D:\TC6_AB_Cycle1\tools\visual-ab\ab.ps1 -Action fixtures -SaveReport
pwsh -NoProfile -File D:\TC6_AB_Cycle1\tools\visual-ab\tests\validate-cycle1.ps1
```

- `catalog/v0-finding-ledger.json`: 37 detailed source records, legacy/modern code anchor references, and all **6 watchlist** entries. `W-02` is explicitly unmapped in the group catalog rather than silently discarded. Additional D/H/N/F master research records remain a separate import task.
- `-Action findings`: verifies ledger IDs, per-finding G-group mapping, research baseline, watchlist uniqueness and `NOT_RUN` runtime status. Does not assign gameplay PASS.
- `-Action fixtures`: read-only inventory of configured world directories, `level.dat`, region file presence and exact PRIMARY/RESET timestamp pairing. An ungenerated RESET world is **not** automatically corrupt; a `PATHS_PAIRED_CLIENT_UNVERIFIED` pair is **not** client-ready proof. No NBT data is modified and no game is launched.
- `tests/validate-cycle1.ps1`: one offline check for catalog + ledger, optionally the local pair inventory when config exists. Does not call doctor again.
- `06_docs/audits/visual_ab_calibration_protocol_v1_ru.md`: Russian G00 calibration instructions, including graphics differences, camera and manual acceptance gates.

**Host verification completed:** on 2026-11-04 the user ran the combined smoke test successfully and produced `fixtures-20261104-184538.json`. Four world directories passed file-presence checks, forming exactly two candidate pairs (PRIMARY and `RESET_20261103_213123_465257`). All still have `clientVerification=NOT_RUN`. The user-specific `config.local.json` under the ignored `tools/reports/local` directory should not be committed.

## Next gates

1. Run combined Cycle 1 smoke + fixture inventory on Windows PowerShell 7 (new additions not yet runtime-tested).
2. Calibrate G00 in clients and verify each selected PRIMARY/RESET world without modifying existing worlds automatically.
3. Decompose G-groups into stable state/camera IDs; verify version-specific setup commands in both games.
4. Implement session control and evidence capture in Cycle 2.

## First client pass staged

See `06_docs/audits/visual_ab_first_client_runbook_v1_ru.md`: validated directory-pair choice, locked/unlocked research ordering, G00 calibration gate, safe Exchange/Seals pilot instructions and explicit distinction between the Harness Git SHA and the independently built modern port runtime SHA. **Do not begin bulk G01-G12 capture before Cycle 2 evidence collection is ready.**
