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

1. Continue exact arcane recipe expansion by audited dependency family; prefer bridge-only recipe cleanups where outputs already exist but gameplay is deferred, and avoid custom behavior until the owning subsystem is implemented; keep utility recipes bridge-only until block behavior is ported; handle non-arcane legacy recipe conversions separately from arcane recipe batches; keep infusion-support outputs bridge-only until block behavior is ported; verify block item identity before touching ancient/eldritch variants; keep special recipe behavior noted when represented by bridge JSON; continue bridge recipe cleanup only when dependencies are already registered; Vis Generator now uses the existing rare_earth bridge item for legacy nugget meta 10; keep future meta-variant mappings explicit; recipe identity is not gameplay implementation for Grapple Gun behavior; banner recipes are decorative bridge data only; Focus Pouch recipe is bridge identity only until item behavior/inventory is ported; Automated Crossbow and Mnemonic Matrix recipe identity is bridge data only until turret/brainbox behavior is ported; remaining arcane placeholder audit should now only report caster_basic as an ignored exact legacy match; recipe cleanup audits require a final server-smoke recheck with the hardened log quality gate; next work should move from data identity cleanup only after that server smoke is clean.
2. Keep bridge/placeholder outputs clearly marked as non-gameplay implementations until their subsystems exist.
3. Keep reusable audit scripts under `tools/audits/`.
4. Keep local/generated audit output under ignored `tools/reports/local/` or curate it into `06_docs/audits/` only when useful.

## CI smoke note

- Keep CI/server smoke strict for datapack/recipe/log-quality failures while avoiding false positives from DEBUG dependency names.

## CI smoke stale-lock note

- Server smoke should fail early on a locked local run/world/session.lock and print stale runServer process hints.

## CI smoke local cleanup note

- Local server smoke can be run with -KillStaleRunServer to clean up stale repo runServer Java or Gradle processes before testing.

## CI smoke clean-workspace note

- Server smoke pre-seeds run/server.properties so CI clean-workspace first startup does not create a benign Minecraft Settings ERROR.

## Custom recipe boundary note

- Use 06_docs/audits/custom_recipe_boundary_audit.md before implementing the next non-arcane custom recipe serializer/page/behavior slice.

## Research recipe page gap note

- Use 06_docs/audits/research_recipe_page_gap_audit.md to pick the next serializer/page implementation slice from actual stage/addendum recipe page gaps.

## Legacy alchemy recipe source note

- Use 06_docs/audits/legacy_alchemy_recipe_source_audit.md to choose the first alchemy/crucible/special recipe page serializer slice from legacy source evidence.

## Hedge alchemy recipe extraction note

- Use 06_docs/audits/hedge_alchemy_legacy_recipe_blocks.md as the source of truth for the first crucible recipe data/page boundary batch.

## Crucible recipe page boundary note

- Re-run research recipe page gap and page catalog audits after the HEDGE_ALCHEMY crucible recipe page boundary batch.

## Post-HEDGE audit refresh note

- Use refreshed post-HEDGE research_recipe_page_gap_audit.md counts to choose the next family-level recipe/page batch.
