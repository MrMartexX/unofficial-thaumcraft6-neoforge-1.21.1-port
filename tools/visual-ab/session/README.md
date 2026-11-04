# Thaumcraft Visual A/B Harness: Cycle 2 session core

**Status:** source implemented, Windows PowerShell 7 execution pending. No game-code changes. Screenshot intake is available; automated video clipping and semantic A/B comparison are *not* implemented in this slice.

## Scope and safety

- One run has one persisted `session.json` plus read-only copies of screenshots under `evidence/<side>/<sceneId>/`.
- `start` creates a **new** uniquely named results folder. No existing run is reset or overwritten.
- `select` sets the current group/finding/state/view and client side.
- `capture` imports a chosen PNG; `watch` detects **new** Minecraft F2 files after watcher activation (old screenshots are ignored).
- Source PNGs are never deleted or moved.
- Every imported file is SHA-256 compared against its copy before the session record is saved. Duplicate hashes within the same side/scene are ignored.
- `status` resumes read-only progress; `verify` rehashes every registered file and reports corruption/missing files.
- The controller **does not set gameplay PASS**; evidence checks do not prove equivalence.
- File imports do not modify options.txt, world data, JARs or original/modern launcher profiles.

## Run the complete offline smoke test first

After updating the isolated worktree to the Cycle 2 branch:

```powershell
pwsh -NoProfile -File D:\TC6_AB_Cycle1\tools\visual-ab\tests\validate-offline.ps1
```

This runs source catalog integrity, the source-only 48 scene candidates, paired world folder checks, and a self-contained synthetic screenshot test. The latter creates a temporary miniature PNG, starts a temporary run, selects legacy and modern scenes, imports two files, proves duplicate suppression, verifies SHA-256, checks that the watcher ignores pre-existing files and restores status in new PowerShell processes. Temporary test files are removed on normal completion.

**Do not start actual Minecraft A/B capture until the offline smoke passes.** If a script fails, stop and send its output.

## First real session (after approval of G00 and candidate scene recipes)

Choose an evidence directory with free space **outside the Git repository**; the path below is an example, not an automatic action:

```powershell
$ab = 'D:\TC6_AB_Cycle1\tools\visual-ab\session\session.ps1'
$created = pwsh -NoProfile -File $ab -Action start -ResultsRoot 'D:\TC6_AB_Evidence' -PairKey 'RESET_20261103_213123_465257' | ConvertFrom-Json
$run = $created.runPath
$run
```

Do not pass `-RuntimeTargetSha` until you have verified the **actual game build** source revision. The harness worktree SHA is not the port runtime revision. Omission leaves `runtimeTargetVerification=NOT_VERIFIED`.

Select an explicitly catalogued draft state:

```powershell
pwsh -NoProfile -File $ab -Action select -RunPath $run -SceneId 'G07.V0GUI-seal.PICKUP_ADVANCED.FRONT' -Side legacy
```

Then start watching **before** pressing F2 in Minecraft:

```powershell
pwsh -NoProfile -File $ab -Action watch -RunPath $run -MaxSeconds 120 -MaxFiles 1
```

The watcher reads the legacy screenshot directory from the existing ignored `config.local.json`. It only registers a newly created PNG and stops after one image or 120 seconds. For a series, increase `-MaxFiles` up to the expected number.

Switch the active side only **after** the legacy watcher has finished, then repeat in the modern client:

```powershell
pwsh -NoProfile -File $ab -Action select -RunPath $run -SceneId 'G07.V0GUI-seal.PICKUP_ADVANCED.FRONT' -Side modern
pwsh -NoProfile -File $ab -Action watch -RunPath $run -MaxSeconds 120 -MaxFiles 1
```

If the screenshot was taken before the watcher began, import a specific F2 PNG manually (no directory scan or guessing):

```powershell
pwsh -NoProfile -File $ab -Action capture -RunPath $run -FilePath 'FULL_PATH_TO_NEW_SCREENSHOT.png'
```

Resume or verify at any time:

```powershell
pwsh -NoProfile -File $ab -Action status -RunPath $run
pwsh -NoProfile -File $ab -Action verify -RunPath $run
```

## Critical boundaries

1. **One active controller per run.** Parallel capture processes writing the same `session.json` are not supported in this initial slice. Wait for `watch` to finish before switching side/scene.
2. The first 43 baseline cards are broad **source candidates**; their setup commands remain `MANUAL_RECIPE_REQUIRED`. The five additional pilot cards are *guided drafts* awaiting client verification. Do not blindly execute an invented spawn/registry command.
3. An imported screenshot has `observation=NOT_RUN`, `review=NOT_REVIEWED`; it only proves a file was captured and retained with a correct checksum.
4. If a run is interrupted, the most recent completed screenshot transaction is retained. Images copied but not registered due to a crash remain as unindexed files; no automatic cleanup or deletion occurs.
5. Multi-process lock handling, video capture, log pairing, comparison images, automatic reports and archive import/export are not yet part of this core.
6. Uploaded/exported logs and reports must be checked for private data. The local `session.json` uses the original screenshot file **name**, not the absolute local source path.
7. No gameplay world resets or automatic `tc_parity:build` commands exist in this module.

## Next gate

Once the synthetic test passes, proceed with an isolated limited F2 smoke on G00 or a deliberately chosen safe scene; then implement continuous-session UI, capture-count completeness, video timestamps and comparison reporting. Only then schedule full G00–G12 client passes.
