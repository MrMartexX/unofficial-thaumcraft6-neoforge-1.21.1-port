# Current task

Last updated: 2026-06-17

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

1. Continue exact arcane recipe expansion by audited dependency family; prefer bridge-only recipe cleanups where outputs already exist but gameplay is deferred, and avoid custom behavior until the owning subsystem is implemented; keep utility recipes bridge-only until block behavior is ported; handle non-arcane legacy recipe conversions separately from arcane recipe batches; keep infusion-support outputs bridge-only until block behavior is ported; verify block item identity before touching ancient/eldritch variants; keep special recipe behavior noted when represented by bridge JSON; continue bridge recipe cleanup only when dependencies are already registered; Vis Generator now uses the existing rare_earth bridge item for legacy nugget meta 10; keep future meta-variant mappings explicit; recipe identity is not gameplay implementation for Grapple Gun behavior; banner recipes are decorative bridge data only; Focus Pouch recipe is bridge identity only until item behavior/inventory is ported.
2. Keep bridge/placeholder outputs clearly marked as non-gameplay implementations until their subsystems exist.
3. Keep reusable audit scripts under `tools/audits/`.
4. Keep local/generated audit output under ignored `tools/reports/local/` or curate it into `06_docs/audits/` only when useful.