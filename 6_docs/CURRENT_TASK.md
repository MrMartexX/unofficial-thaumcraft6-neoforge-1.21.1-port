# Current task

Last updated: 2026-06-10

## Current branch

`main` is the active working branch after merging PR #4 (`research-knowledge-scanning-design`).

## Current focus

- Continue Thaumcraft 6 → NeoForge 1.21.1 port from the merged research/knowledge/scanning baseline.
- Keep build CI green.
- Add and use dedicated server smoke testing to catch client-only code leaking into common/server paths.
- Continue visual and asset parity work carefully, especially for legacy TC6 models, item variants, particles, and GUI visuals.
- Keep documentation and audit outputs organized under `6_docs/` and `tools/`.

## Recently confirmed

- GitHub Actions build workflow exists and runs on GitHub-hosted Windows runner.
- Latest build workflow passed after fixing Gradle Java path handling.
- PR #4 was merged into `main`.

## Do not change without explicit request

- Do not rework Nitor tint/color logic unless a new specific issue is reported.
- Do not reintroduce enchanted placeholder books into the Thaumcraft creative tab.
- Do not start large new systems such as wand workbench GUI or broad gameplay rewrites unless requested.
- Do not commit throwaway root-level audit reports or one-off patch scripts.

## Near-term tasks

1. Verify dedicated server smoke workflow result.
2. Remove or prevent temporary backup files such as `*.broken-before-rebuild`.
3. Move reusable audit logic into `tools/audits/` instead of keeping ad-hoc scripts in the repository root.
4. Add curated documentation and decisions under `6_docs/`.
5. Continue next asset/visual parity fixes after local client screenshots or runtime reports.
