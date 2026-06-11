# Current task

Last updated: 2026-06-10

## Current branch

`main` is the active working branch after merging PR #4 (`research-knowledge-scanning-design`).

## Current focus

- Continue Thaumcraft 6 -> NeoForge 1.21.1 port from the merged research/knowledge/scanning baseline.
- Keep build CI green.
- Use GitHub Actions build and dedicated server smoke testing only for relevant NeoForge port changes.
- Continue visual and asset parity work carefully, especially for legacy TC6 models, item variants, particles, GUI visuals and creative tab content.
- Keep documentation and audit outputs organized under `06_docs/` and `tools/`, not under duplicate docs folders.

## Recently confirmed

- PR #4 was merged into `main`.
- GitHub Actions build workflow exists and runs on GitHub-hosted Windows runner.
- Build workflow is now path-filtered to `05_neoforge_port/**` and `.github/workflows/build.yml`.
- Dedicated server smoke test script exists under `tools/ci/server-smoke.ps1`.

## Do not change without explicit request

- Do not rework Nitor tint/color logic unless a new specific issue is reported.
- Do not reintroduce enchanted placeholder books into the Thaumcraft creative tab.
- Do not start large new systems such as wand workbench GUI or broad gameplay rewrites unless requested.
- Do not commit throwaway root-level audit reports or one-off patch scripts.

## Near-term tasks

1. Verify the next GitHub Actions result after the workflow path and smoke-test path fixes.
2. Keep reusable audit scripts under `tools/audits/`.
3. Keep local/generated audit output under ignored `tools/reports/local/` or curate it into `06_docs/audits/` only when useful.
4. Continue next asset/visual parity fixes after local client screenshots or runtime reports.
