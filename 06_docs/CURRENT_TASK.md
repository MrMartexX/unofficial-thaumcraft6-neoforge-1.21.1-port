# Current task

Last updated: 2026-06-10

## Current branch

`main` is the active working branch.

## Current focus

- Keep build CI green.
- Use GitHub Actions build and dedicated server smoke testing only for relevant NeoForge port changes.
- Keep documentation and audit outputs organized under `06_docs/` and `tools/`.

## Recently confirmed

- GitHub Actions build workflow exists and runs on GitHub-hosted Windows runner.
- Build workflow is now path-filtered to `05_neoforge_port/**` and `.github/workflows/build.yml`.
- Dedicated server smoke test script exists under `tools/ci/server-smoke.ps1`.

## Do not change without explicit request



## Near-term tasks

1. Verify the next GitHub Actions result after the workflow path and smoke-test path fixes.
2. Keep reusable audit scripts under `tools/audits/`.
3. Keep local/generated audit output under ignored `tools/reports/local/` or curate it into `06_docs/audits/` only when useful.
4. Continue next asset/visual parity fixes after local client screenshots or runtime reports.
