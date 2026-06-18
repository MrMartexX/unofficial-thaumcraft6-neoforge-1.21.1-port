# Current task

Last updated: 2026-06-18

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
- The active migration guide is now `06_docs/migration/NeoForge_legacy_migration_guide.md`; older `.docx` references were removed from current docs.
- The latest crucible recipe/page boundary batch passed build, server smoke, research page catalog audit, and Thaumonomicon protocol audit.
- The in-world crucible behavior slices have a design boundary in `06_docs/gameplay/crucible_in_world_behavior_design.md`.
- The seven legacy dynamic HEDGE_ALCHEMY crucible costs are now explicit JSON aspect costs resolved from the current parity data, and `audit-crucible-recipe-data.ps1` reports `77/77` valid recipe files.

## Do not change without explicit request

- Do not move or delete large legacy/audit documents until references are checked and the move is recorded in `06_docs/documentation_index.md`.
- Do not treat `thaumcraft:crucible` recipe/page data as in-world crucible gameplay.
- Do not expand the current in-world crucible slice, infusion, essentia transport, broad worldgen, or broad rendering systems without a focused design note and validation path.
- Do not commit generated local reports unless they are intentionally curated under `06_docs/audits/`.

## Near-term tasks

1. Continue the crucible/alchemy work from the documented in-world behavior slices:
   - Current implemented scope includes server-owned water/heat/aspect pool, manual top-side item insertion, item-entity collision absorption, hot living-entity contact damage, periodic/overflow spill pollution, recipe lookup, research-gated crafting, result ejection, and modern special-item reabsorption protection.
   - Keep flux rifts, taint spread, liquid death, special alchemy effects, Thaumatorium, alembic/jar/tube integration, client particles, automation, item pulling radius, and recipe-derived aspect generation deferred.
   - Re-run build, dedicated server smoke, crucible recipe-data audit, crucible behavior audit, research page catalog audit, and protocol audit after the batch.
   - Use legacy `BlockCrucible`, `TileCrucible`, `CrucibleRecipe`, and `ThaumcraftCraftingManager.findMatchingCrucibleRecipe` as behavior references, not direct copy sources.
2. Keep bridge/placeholder outputs clearly marked as non-gameplay implementations until their subsystems exist.
3. Keep reusable audit scripts under `tools/audits/`.
4. Keep local/generated audit output under ignored `tools/reports/local/` or curate it into `06_docs/audits/` only when useful.

## CI smoke note

- Keep CI/server smoke strict for datapack/recipe/log-quality failures while avoiding false positives from DEBUG dependency names.

## CI smoke stale-lock note

- Server smoke should fail early on a locked local run/world/session.lock and print stale runServer process hints.
- For local work while another dev client/server is open, prefer an isolated smoke run such as `tools/ci/server-smoke.ps1 -TimeoutSeconds 420 -WorldName tc_server_smoke -ServerPort 0`.

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

## Remaining alchemy recipe extraction note

- Use 06_docs/audits/remaining_alchemy_legacy_recipe_blocks.md to select the next remaining alchemy family-level recipe/page batch.

## Crucible aspect alias note

- Crucible recipe aspect costs now accept legacy Aspect enum aliases; keep generated crucible JSON in legacy-source terms when useful and let the serializer canonicalize.

## Metal purification crucible note

- Re-run recipe page gap and registry/tag audits after the METAL_PURIFICATION crucible recipe/page batch.

## Post-metal-purification audit note

- Use refreshed post-METAL_PURIFICATION audits to choose the next small pure crucible alchemy batch.

## Base alchemy/metallurgy crucible page note

- `thaumcraft:crucible` page-data now covers Alumentum, Nitor, Brass Ingot, Thaumium Ingot and 37 vis-crystal recipes.
- Current catalog audit result: `113 READY`, `86 DEFERRED`, `4 LEGACY_MISSING`.
- Current protocol audit result: `27/27`.
- Full alchemy side effects, automation, FX, and crucible-derived aspect generation remain deferred beyond the documented manual/collision server slices.
## Special alchemy crucible page note

- Added special alchemy crucible recipe/page boundary entries for Bath Salts, Bottled Taint, Liquid Death, and Sane Soap.
- Keep their real gameplay/fluids/consumable behavior deferred; current scope is recipe identity and Thaumonomicon page availability.

## Post-metal-purification audit note

- Use refreshed post-SPECIAL_ALCHEMY audits to confirm whether ALCHEMY_CRUCIBLE_OR_SPECIAL_PAGE is closed before choosing the next batch.

## Golemancy boundary source audit note

- Use `06_docs/audits/golemancy_page_boundary_source_audit.md` before implementing GOLEMANCY_PAGE_DEFERRED references.
- Separate seal behavior placeholders, golem machine/block boundaries, and actual recipes into different batches.

## Golemancy boundary source audit repair note

- The first golemancy extraction commit produced an empty audit file; this was corrected by rebuilding `tools/audits/extract-golemancy-page-boundaries.ps1`.
- Use the repaired `06_docs/audits/golemancy_page_boundary_source_audit.md` before implementing golemancy page references.

## Focused golemancy recipe candidate note

- Use `06_docs/audits/golemancy_recipe_source_candidates.md` to choose the first golemancy recipe/page implementation batch.
- Avoid implementing broad seal behavior directly from the noisy boundary audit.
## Golemancy seal crucible page note

- Added crucible recipe/page boundary entries for base and advanced golem seals.
- Do not treat these bridge items as full seal behavior implementations; actual seal AI/placement behavior is deferred.
- Re-run research recipe page gap audit after push to verify how many GOLEMANCY_PAGE_DEFERRED entries remain.
## Golemancy seal crucible repair note

- If dedicated server smoke fails in TCItems.<clinit> after adding seal bridge items, check for duplicate DeferredRegister item ids.
- The current repair removes duplicate added seal registrations while keeping the existing registry identity.

## Post-metal-purification audit note

- Use refreshed post-GOLEMANCY_SEALS audits to confirm how many GOLEMANCY_PAGE_DEFERRED entries remain before choosing the next batch.
## Focused infusion recipe candidate note

- Use `06_docs/audits/focused_infusion_recipe_source_candidates.md` before implementing an infusion recipe/page boundary.
- Scope for the first infusion slice should be serializer/catalog/page display only, not in-world infusion altar behavior.
## Infusion recipe page boundary slice note

- 	haumcraft:infusion is now intended to load data-driven infusion recipes and expose them to Thaumonomicon pages.
- Keep in-world infusion altar crafting behavior deferred until a separate stateful gameplay slice.
- Next safe batch should add a small number of infusion JSON recipes and refresh audits.
## Golemancy first infusion recipe page note

- Added first data-driven infusion recipe/page entries for remaining golemancy infusion candidates.
- Re-run recipe/page audits after push to confirm GOLEMANCY_PAGE_DEFERRED reduction.
- Keep GolemPress separate as a machine/block boundary.

## Post-metal-purification audit note

- Use refreshed post-GOLEMANCY_INFUSION audits to confirm how many GOLEMANCY_PAGE_DEFERRED entries remain before choosing the next batch.
## Utility infusion recipe page note

- Added a small utility infusion JSON/catalog batch for BootsTraveller, CLOUDRING, and CHARMUNDYING.
- Re-run recipe/page audits after push to measure INFUSION_PAGE_DEFERRED reduction.

## Post-metal-purification audit note

- Use refreshed post-UTILITY_INFUSION audits to confirm how many GOLEMANCY_PAGE_DEFERRED entries remain before choosing the next batch.
## Elemental tool infusion recipe page note

- Added a data-driven infusion JSON/catalog batch for ElementalAxe, ElementalPick, ElementalSword, ElementalShovel, and ElementalHoe.
- This script integrates audit refresh after successful build/smoke.
## Fortress mask infusion recipe page note

- Added data-driven infusion JSON/catalog entries for MaskGrinningDevil, MaskAngryGhost, and MaskSippingFiend.
- This script integrates audit refresh after successful build/smoke.
## Fortress armor infusion recipe page note

- Added data-driven infusion JSON/catalog entries for ThaumiumFortressHelm, ThaumiumFortressChest, and ThaumiumFortressLegs.
- This script integrates audit refresh after successful build/smoke.
## Verdant charm infusion recipe page note

- Added data-driven infusion JSON/catalog entries for VerdantHeart, VerdantHeartLife, and VerdantHeartSustain.
- This script integrates audit refresh after successful build/smoke.
## Verdant charm repair note

- 	haumcraft:bauble_charm is now explicitly registered because VerdantHeart uses it as a catalyst.
- Keep future recipe batch scripts checking all custom item ids before smoke.
## Crystal cluster recipe page note

- Added larger integrated recipe/catalog batch for CrystalClusterAir, Fire, Water, Earth, Order, Entropy, and Flux.
- This script integrates audit refresh after successful build/smoke.
## Crystal cluster repair note

- Fixed blank ingredient item ids in generated CrystalCluster recipe JSONs.
- Future batch scripts should use hashtable index access such as $cluster["Input"], not $cluster.Input.
## Simple legacy page recipe note

- Added arcane stone/brick crafting entries plus CuriosityBand and HelmGoggles infusion entries.
- This script integrates audit refresh after successful build/smoke.
## Auromancy focus recipe page note

- Added focus_1, focus_2, focus_3, and VisAmulet recipe/page entries in one larger batch.
- This leaves caster/altar/fake/machine behavior boundaries for later targeted work.
## Eldritch infusion recipe page note

- Added a five-recipe Eldritch infusion page batch: PrimalCrusher, VoidRobeHelm, VoidRobeChest, VoidRobeLegs, and VoidseerPearl.
- voidingot and VoidSiphon remain deferred for separate source/block-boundary handling.
## Artifice behavior recipe page note

- Added seven artifice behavior page recipes as data/page boundary only.
- Machine/block functionality remains intentionally deferred.
## Remaining non-fake page recipe note

- Added seven conservative page-boundary recipes for remaining non-fake references.
- Fake/synthetic pages, infusion altar variants, and GolemPress remain separate targeted work.
## Remaining non-fake page recipe repair note

- Registered alchemical_construct, essentia_importer, and essentia_exporter bridge item ids used by thaumatorium.json.
- This repairs the previous server smoke datapack parse failure and keeps the same batch commit target.
## Blueprint page placeholder note

- Added placeholder recipe/page entries for infusion altar variants and GolemPress.
- Fake/synthetic pages remain intentionally separate.
## Blueprint page placeholder repair note

- Registered arcane_pedestal, ancient_pedestal, and eldritch_pedestal bridge item ids used by infusion altar placeholder recipes.
- This repairs the previous server smoke datapack parse failure and keeps the same batch commit target.
## Blueprint placeholder audit finalize note

- The blueprint placeholder batch removed the last GOLEMANCY_PAGE_DEFERRED reference.
- The golemancy page-boundary extractor is now skipped when that class is absent from the page-gap audit.
## Synthetic recipe page audit classification

- FAKE_OR_SYNTHETIC_PAGE references are now reported separately from actionable missing recipe pages.
- This preserves the list of synthetic teaching/UI placeholders while allowing actionable recipe page gaps to reach zero.
## Infusion page boundary audit classification

- Custom recipe boundary audit now treats thaumcraft:infusion as INFUSION_PAGE_READY_NO_GAMEPLAY.
- This reflects the implemented infusion serializer/page snapshot boundary while keeping in-world infusion altar behavior deferred.
## Current crucible behavior boundary

- Recipe/page actionable gaps are closed.
- The current in-world crucible behavior slices are gated by `06_docs/gameplay/crucible_in_world_behavior_design.md`, `tools/audits/audit-crucible-recipe-data.ps1`, and `tools/audits/audit-crucible-behavior.ps1`.
- `tools/audits/audit-crucible-behavior.ps1` runs against an isolated world/port by default to avoid false failures from an already open local dev server.
- Do not expand this into flux rifts, taint spread, essentia networks, automation, item pulling radius, client particles or special alchemy side effects without a new focused slice.
