# Repository Branch Cleanup And A/B Hold

Date: 2026-11-05. Scope: Git references, pull requests and documentation only.
The owner approved retaining three branches and archiving the others before
deleting their branch references. This operation does not merge gameplay code,
certify parity, discard pending work, or mark archived proposals as completed.

## A/B Status: Temporarily Frozen

The visual A/B comparison-tool development (Cycles 1-6) is paused by the owner.
Do not start Cycle 7, resume clip/capture-tool development, or merge the draft
A/B stack merely because a previous task document lists it as next work.
Resumption requires an explicit owner request.

The complete Cycle 6 history is retained at:

`archive/2026-11-05/codex/visual-ab-cycle6-clips-20261105`

Its commit is `1ef0f1752fd0d786aae28f4756605c9568d9afa8`. Every earlier cycle
also has its own tag. `D:/TC6_AB_Cycle1` remains intact at that same commit in
detached-HEAD mode. Local sessions, screenshots and ignored evidence were not
deleted. The hold concerns the visual A/B tooling; it is not a claim that the
separate [external-port adoption campaign](../migration/external_ports_adoption.md)
has been completed.

## Retained Branches

| Branch | Head before this documentation commit | Role |
|---|---|---|
| `main` | `72eaac17a1e1c0c51b27cb151ef7f244944c89d5` | Existing default branch. Code/history unchanged by cleanup. |
| `dev` | `5e7d49335c755bcae5e1f2d0f04b020fcde212db` | Preserves the consolidated visual research document. Now explicitly tracks `origin/dev`. |
| `codex/review-remediation-20261103` | `b85fa0ccc77fc2b5b903e200b1b920aa6427fd85` | Current port implementation and working directory, including existing uncommitted work. This cleanup report is committed here. |

These are not interchangeable snapshots. At cleanup, the working branch was
19 commits ahead of `main`; `dev` was 5 commits behind and 1 commit ahead of
`main`. Do not reset either branch to another or pretend that `dev` contains
the latest implementation. Consolidating them is a separate reviewed task,
after the uncommitted work has been accounted for.

## Archive Map

Every tag below is annotated and published on `origin`. Prefix every entry in
the first column with `archive/2026-11-05/` to get its exact recovery tag.
The underlying commits and their parent history remain available.

| Original branch / archive suffix | Preserved commit | Disposition |
|---|---|---|
| `aih/task-brain-box` | `488cabe68a91070a29a9a7648a0c2b9d9a4899c7` | Two unmerged commits; PR #8 closed as archived, not merged. |
| `aih/task-flesh-block` | `8ca7ee0fb4715f4dde0d71053ada209ac941b8d6` | Already merged by PR #6; redundant branch removed. |
| `audit/v0-visual-inventory-2026-11-03` | `66d239663355bd433f960b48540ae6fd8d3f1603` | Unique inventory and evidence retained; PR #11 closed without merging. |
| `codex/visual-ab-cycle1-20261104` | `c7067419b8fb23f84baeb9f755e7bc45cb759034` | Frozen; PR #12 closed without merging. |
| `codex/visual-ab-cycle2-core-20261104` | `2cf6f17b4943499026e3b0024f46fcc947047eab` | Frozen; PR #13 closed without merging. |
| `codex/visual-ab-cycle3-20261104` | `157960c2a6c0becee2bc2a12fe6d679d9543088a` | Frozen; PR #14 closed without merging. |
| `codex/visual-ab-cycle4-calibration-20261105` | `8f3a840f242d791770a188b5a056b55a60b49b95` | Frozen; PR #15 closed without merging. |
| `codex/visual-ab-cycle5-video-probe-20261105` | `4ac84bfba9374e466eb6d32450713d18d3ed238c` | Frozen; PR #16 closed without merging. |
| `codex/visual-ab-cycle6-clips-20261105` | `1ef0f1752fd0d786aae28f4756605c9568d9afa8` | Frozen; PR #17 closed without merging. |
| `docs/chatgpt-aih-development-workflow-20261101` | `79f6c81dea35076ccf23501189aa8a7bca990788` | Already merged by PR #5; redundant branch removed. |
| `docs/visual-parity-research-master-2026-11-03` | `a1f39e81cd83ef349b4cf7b4a45d6766942a06a9` | Original report history preserved; consolidated document also exists in `dev`. PR #10 closed without merging. |
| `fix/parity-audit-honest-metrics-20261102` | `1ec087cfe5c0b23c7cef533c3f542a9720f81ed3` | Unique patch preserved for review; PR #9 closed without merging. |
| `prepare_thaumcraft_comparison_env` | `72eaac17a1e1c0c51b27cb151ef7f244944c89d5` | Local-only duplicate of `main`; no unique commits. |
| `thaumcraft_6_port_comparison` | `72eaac17a1e1c0c51b27cb151ef7f244944c89d5` | Local-only duplicate of `main`; no unique commits. |
| `thaumcraft_version_comparison_setup` | `72eaac17a1e1c0c51b27cb151ef7f244944c89d5` | Local-only duplicate of `main`; no unique commits. |

The two AIH working directories were detached at their existing commits before
removing their local branch names. Their files, including untracked `.aih/`
state, remain present. Create a new task branch before making future commits
there. The Antigravity detached working directory was not altered.

Foreign `fork` and `shobie` remotes/reference refs were not removed or changed;
they are source references, not development branches on our GitHub repository.

## Verification And Backup

| Check | Result before the documentation-only commit |
|---|---|
| Branch count on `origin` | 15 -> 3; 12 removed with expected-SHA guards in one atomic push |
| Local branch count | 8 -> 3; 5 removed after archival |
| Archive tags | 15/15 peeled commit IDs equal the recorded original heads |
| Open PRs | 10 -> 0; #8-#17 closed with archive links/reasons, no merge |
| Retained branch heads | All three unchanged by reference cleanup |
| Working directory heads | 5/5 unchanged |
| Existing modified/untracked files | 75/75 recorded SHA-256 values unchanged; old Gradle-cache directory excluded from content hashing and left untouched |
| Stash | `933168ecbb79cfefb89c73f21125aba2b8676030` unchanged, including `pre-sync saplings fx trees block data work` |
| Independent local history backup | `git bundle verify` passed; self-contained bundle, 39 refs |

Private local evidence is in `tools/reports/local/branch-cleanup-20261105/`:
`inventory.json`, `verification.json`, `before-cleanup.bundle`, and
`bundle-sha256.json`. The bundle is a backup of committed Git history, not of
uncommitted/ignored files; those files were preserved in their original places.
No file trees, worlds, screenshots, caches, stash entries or history objects
were recursively deleted. No history rewrite or branch consolidation occurred.

Build/client/server checks were not rerun for this cleanup: it changes no game
code. Verification here covers Git preservation and file integrity, not gameplay.

## Recovery

List archive tags without changing the working directory:

```powershell
git tag --list 'archive/2026-11-05/*'
```

Restore a branch pointer without switching the dirty primary checkout:

```powershell
git branch recovery/visual-ab archive/2026-11-05/codex/visual-ab-cycle6-clips-20261105
```

This restores access, not authorization to resume the frozen A/B work. Other
branches can be restored the same way using the archive map. Prefer reading
archived files with `git show <tag>:<path>` when no new development is needed.

## Keeping It Tidy

- Keep these three development branches until a separate consolidation decision.
- Do not create one permanent branch per small checkpoint; use commits and tags.
- Keep only actively used task branches. Archive unique work before retiring one.
- Close superseded draft PRs with a reason and a recovery reference, not a merge.
- Review differences before moving code to `dev` or `main`; branch cleanup is
  not a substitute for migration-guide gates or legacy parity validation.
