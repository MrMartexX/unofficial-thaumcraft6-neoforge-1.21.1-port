# Development Workflow: ChatGPT + AIH + GitHub + Minecraft

Status: proposed project working agreement (pending review/merge).
Recorded: 2026-11-01. Scope: Thaumcraft 6 (Forge 1.12.2) port to Minecraft 1.21.1 / NeoForge.

## Goals

- Preserve verifiable legacy gameplay identity and behavior while using NeoForge 1.21.1 APIs.
- Work in small, auditable, reversible changes with explicit acceptance criteria.
- Optimize for engineering quality, iteration speed, and repeatable evidence, not unverified automated parity percentages.
- Avoid duplicate AI agent work, broad unreviewed edits, and development on real player saves.

## Responsibilities

| Role | Responsibilities |
| --- | --- |
| ChatGPT | Legacy/current code analysis, dependency/risk assessment, implementation strategy, focused code edits when authorized, diff review, CI/result interpretation, and handoff instructions. |
| AIH on the user's PC | Optional local task dispatch, isolated Git worktrees, supported agent routing, verification/review evidence, and recoverable sessions after a verified pilot. |
| GitHub | Shared source of truth, branches/PRs, commit history, automated builds, dedicated-server smoke tests, and audit artifacts. |
| User's PC | Native Windows/Java toolchain, local AIH execution, Minecraft client/multiplayer tests, in-game screenshots, and reproducible logs. |
| Project owner | Scope and priority decisions, enabling local runs, game-experience acceptance, and final approval for important merges. |

ChatGPT's authenticated GitHub access does not imply direct access to arbitrary local PC directories or a running AIH instance. Local execution requires an explicitly available environment or user handoff. AIH is not a substitute for GitHub Actions or in-game tests.

## Source-of-truth order

1. Fetch current GitHub branch heads and the exact task baseline SHA; do not rely on an older chat or a historical status report.
2. Read 06_docs/CURRENT_TASK.md, 06_docs/current_port_status.md, and relevant subsystem decisions.
3. Compare behavior against 02_existing_decompiled_repo, 01_original_jar, 04_api_reference, and other approved legacy evidence.
4. Inspect the actual 05_neoforge_port code and relevant data/assets; documentation and parity matrices can lag behind code.
5. Use evidence tied to a specific commit and runtime. Registry coverage, static audits, and green build badges do not alone establish gameplay or visual parity.

## Standard task lifecycle

1. **Intake.** Define a narrow subsystem/outcome, exact baseline SHA, source references, affected boundaries, risk, acceptance criteria, and required tests. For an AIH task, encode the contract in task.yaml and details in spec.md.
2. **Analysis.** Inspect legacy semantics, current NeoForge implementation, API/data/network/save dependencies, security and dedicated-server risks, and alternative designs before editing.
3. **Implementation.** Create a focused branch or isolated AIH worktree from the verified baseline; use one owner for shared registries/bootstrap. No direct pushes to main, automatic merge, force-push, or destructive cleanup as part of the routine.
4. **Fast validation.** Run relevant static/resource audits, serializers/datapack validation, focused unit/GameTests if available, and a build when code/resources change.
5. **Integration validation.** Open a PR; require appropriate GitHub Actions checks, especially NeoForge build and dedicated-server smoke. Investigate failures and report-only workflows instead of treating every green badge as strict parity proof.
6. **Runtime validation.** For visual/client/gameplay work, test runClient and relevant mechanics, multiplayer/server authority, save/load, and a disposable world. Keep comparison screenshots/logs at a known GUI scale/commit when applicable.
7. **Review and integration.** Review the diff, limitations, test evidence, and compatibility impact; request owner approval for significant merges. Record merged SHA and update targeted status/audit docs.

## Choosing an execution path

- Small, focused changes (roughly 1-3 files): ChatGPT may prepare them directly through the GitHub integration on a feature branch and submit a PR.
- Larger changes or local-runtime-heavy tasks: use AIH on the user's machine in a pinned worktree after its pilot has been verified.
- Source analysis and non-mutating audit triage: ChatGPT/GitHub read-only access is sufficient.
- Final in-game UI/model/particle/worldgen acceptance: native Minecraft client on the user's PC; CI can complement but not replace this step.
- Parallel work is allowed only across independent file/subsystem boundaries; coordinate shared registries, networking bootstrap, Gradle, and data contracts explicitly.

## AIH adoption gate

- Antigravity has already assigned the initial AIH pilot. Do not duplicate or overwrite it.
- Before production use, check AIH branch choice, Python 3.12+, uv, Git, agent CLI availability/authentication, dependencies, and lint/type/test results on the actual host.
- As of 2026-11-01, the AIH Stage 5A experimental branch's observed GitHub CI failed at Ruff; Mypy/Pytest were skipped. Do not treat that branch as verified until the checks pass.
- First prove task validation, pinned baseline, isolated worktree, agent discovery, result recovery, review pack, and safe cleanup on a disposable task.
- Do not silently enable paid providers/fallback, write secrets to task specs/logs, or authorize AIH to push or merge production branches. AIH's normal dispatch/run flow leaves integration to a separate reviewed operation.
- Refresh any previously pinned Thaumcraft baseline against the current main before new production tasks.

## Definition of done

- The intended legacy behavior and deliberate modern deviations are documented.
- Relevant code and data have been reviewed for server ownership, persistence, networking, and compatibility risks.
- Build, appropriate runtime audits, and dedicated-server smoke pass for the exact integration candidate.
- Client/in-game tests pass where relevant, or remaining gaps are explicitly marked as unverified.
- No regression, secret, unrelated formatting churn, generated output, or accidental save-world mutation is hidden in the diff.
- A reviewed PR is merged only after the applicable owner approval; resulting SHA and relevant documentation are updated.

## Repository safeguards to configure

- Protect main with pull requests, checks, and disabled force-push/deletion. Both main and dev were observed unprotected when this agreement was drafted.
- Account for path-filtered workflows when defining required status checks; documentation-only PRs must not become permanently blocked by skipped checks.
- Keep GitHub Actions permissions minimally scoped and do not execute untrusted PR code with elevated secrets.
- Prefer separate test worlds; keep generated reports/logs under ignored local report directories unless intentionally curated.

## Operating note

This document is a project process baseline, not a claim that a local AIH service is currently reachable from ChatGPT. Amend it through review as the real pilot and tool capabilities are verified.
