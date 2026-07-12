# Current task

Last updated: 2026-07-12

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
- The crucible client slice now includes the synced liquid surface, exact fluid-height/recolor formulas, boil/froth/overflow/bubble particles and legacy dissolution/craft/spill block-event FX. Runtime behavior audit passes `16/16`.
- The infusion boundary now includes server-owned two-click caster activation, live bounded surroundings refresh, inactive stability charging, researched recipe start without pre-supplied essentia, persisted craft-cycle/stability state, exact five-tick default cadence, one-point nearest-source drain, the legacy 200-tick failed-source rescan delay, six-cycle component timing, result placement/damage carry-over, component remainders, completion/failure sounds, the two clientbound legacy FX message contracts, exact structure modifiers, exact stability math, all 24 executable instability rolls, Flux Goo/harm dependencies, inlay/Stabilizer pedestal mitigation, client matrix animation/halo, eight-sided essentia streams and item/block source debris. The runtime audit passes `95/95`.
- The first real essentia transport slice now has legacy capacities/cadence/suction formulas for all six tube variants, Warded Jar and Void Jar transfer/overflow, sided NeoForge capability access, persisted directional state, legacy multipart tube geometry, Alembic/Jar label filters, phial/jar item transfer quanta, caster tube sub-part side/choke/facing controls, manual/redstone valve state, vent sync, real Essentia Input/Output transfuser blocks, and the first real Essentia Mirror source bridge. The dedicated-server runtime audit passes `61/61`.
- Normal item Mirror and Magic Hand Mirror now have the first real standalone device slice: wall-mounted six-facing normal mirror block, linked mirror BlockItem Data Component preservation, bidirectional link restore, item-entity transport/ejection, instability flux cadence, hand-mirror one-slot sender menu/screen, self-rejection, missing-target link break and dedicated runtime audit `12/12`.
- Arcane Lamp, Lamp of Growth and Lamp of Fertility now have the next real standalone device slice: real blocks/BlockItems/BlockEntity, legacy lamp shape/light/redstone support checks, Arcane Lamp `effect_glimmer` placement/removal, Growth Lamp Herba suction/charge/plant tick behavior, Fertility Lamp Desiderium suction/animal-love behavior, and dedicated runtime audit `10/10`.
- Infernal Furnace now has the next row-13 runtime device slice: `infernal_furnace` block/BlockItem/BlockEntity, half-height/light/facing contract, 32-slot top-only item buffer, vanilla smelting/ejection/XP, aura-speed drain, distance-2 Bellows formula, non-smeltable lava destruction, internal legacy default bonus table with flattened nugget/chunk outputs, and dedicated runtime audit `12/12`. Salis Mundus now owns the legacy IDustTrigger-style Infernal Furnace multiblock activation/placeholder/rollback slice.
- Void Siphon now has its first row-13 standalone device slice: real `void_siphon` block/BlockItem/BlockEntity/menu/screen, legacy shape and redstone-enabled state, extract-only void-seed slot, progress persistence, rift-drain math and a future `TCVoidSiphonRiftAccess` adapter boundary. The standalone device blocker audit passes `9/9`; full Flux Rift lifecycle/rendering remains row 11/14 work.
- The first Thaumatorium machine foundation and functional recipe-selection screen are active: `thaumatorium` is a real two-block Block/BlockItem/BlockEntity/menu/transport endpoint, `thaumatorium_top` delegates inventory and essentia input, heat is read two blocks below through the crucible base, redstone pauses filling, suction is `128` for the first missing recipe aspect, selected crucible recipes consume one catalyst and eject output, the legacy GUI texture screen lists researched catalyst-matching recipes, and the dedicated runtime audit passes `19/19`. Exact legacy model/renderer/pixel parity remains separate visual work.
- Flux Rift, Arcane Bore and Thaumatorium visual blocker slice is active: `flux_rift` and `arcane_bore` are registered entity foundations with legacy tracking values, Void Siphon consumes real Flux Rift entities through the adapter, Arcane Bore has a placer item, one-pickaxe-slot menu and server mining loop, and Thaumatorium now uses the legacy OBJ model plus an output-item BER path. The dedicated blocker audit passes `11/11`; measured pixel parity remains later focused work.
- Flux Goo now has the missing legacy level-zero alternate result into real `taint_fibre`. The first TaintSeed/ecology slice is also active: `taint_seed` and `taint_seed_prime` entities, `TCTaintHelper` seed-radius bookkeeping/spread transforms, `taint_crust`, `taint_soil`, `taint_rock`, `taint_geyser`, `taint_log`, `taint_feature`, Flux Taint tainted-mob healing, modern resources and deterministic validation are covered by the dedicated taint ecology blocker audit passing `15/15`. The follow-up taint mob foundation slice registers `thaum_slime`, `taint_crawler`, `taintacle`, `taintacle_tiny` and `taint_swarm`, adds legacy scan/aspect identities where source data exists, and closes the crawler feature-spawn, crawler fibre trail, geyser swarm, taintacle tiny-spawn, swarm NBT and Thaumic Slime size/xp contracts with a `14/14` audit. The FallingTaint slice registers `falling_taint`, ports the crusted-taint falling gates, TC6 gravity/damping, first-tick source removal, side-overhang fall path, landing placement and GORE sound path, and passes the focused `10/10` audit. The Flux Rift consequence slice registers `wisp`, preserves Wisp dynamic aspect type, ports the legacy `50/10/20/20/1` event table, Wisp spawn, prime TaintSeed boost/pollution, infectious vis exhaustion and collapse effect fallthrough, and passes the focused `11/11` audit; event 3 remains explicitly deferred to the focus-cloud/projectile owner. The first natural-spawn slice now activates only the legacy Wisp Nether spawn (`#minecraft:is_nether`, weight `5`, `1-1`) with a NeoForge spawn placement predicate and a `10/10` audit. Full custom mob renderers/animations, remaining legacy natural spawn families, focus-cloud rift event execution, exact FallingTaint landing particles/render pixel parity and exact animated TaintSeed model parity remain deferred to focused row-11/14/17 slices.
- The Alembic/smelter machine batch now replaces the incremental placeholder path: all three smelter tiers share the server-owned two-slot machine state, exact tier efficiency/output intervals, Alumentum boost, fuel remainders, cumulative vent mitigation, direct plus attached auxiliary Alembic routing, modern sided item capability, legacy-layout menu/screen and detailed legacy-derived models. Its checks are now included in the combined transport/machine/transfuser/Void-Jar/essentia-mirror runtime audit at `61/61`.
- Bellows is now a focused device slice: placed Bellows uses a real BlockEntity/client renderer, legacy inflation animation, tube-buffer extension render, smelter/tube-buffer ownership, and a dedicated vanilla furnace cook-progress bridge. Keep `audit-bellows-device.ps1` green after changes.
- The item/equipment behavior pass is closed for the first safe wearable/utility slice. Goggles, cloth robes and void robes are real armor/equipment items with legacy vis/reveal/warp contracts; `sanity_checker`, `sane_soap` and `curio_rites` have their first server-side behavior contracts; the runtime item/equipment audit passes `17/17`.
- Focus/caster/Focal Manipulator core is closed for the first data/behavior slice. `caster_basic`, `focus_1`, `focus_2`, `focus_3` and `focus_pouch` are real items; caster focus state and focus package data use Data Components; `wand_workbench` has a server-owned Focal Manipulator BlockEntity/menu/screen and a validated design-intent payload; the runtime focus/caster core audit passes `10/10`.
- The first focus cast-effect execution slice is active for legacy `ROOT -> TOUCH -> FIRE`. Unsupported focus packages fail before vis/aura drain; supported caster use drains aura and sets cooldown; Fire effect mutates entities and blocks with the audited legacy formulas. Runtime focus cast execution audit passes `5/5`.
- The first custom entity foundation blocker is closed without broad renderer/golem scope: `TCEntityTypes` now registers `SpecialItem`, `FollowItem`, `FluxRift`, `ArcaneBore`, `FallingTaint`, `TaintSeed`, `TaintSeedPrime`, `Wisp`, `ThaumSlime`, `TaintCrawler`, `Taintacle`, `TaintacleTiny` and `TaintSwarm`, preserves the 43-entry legacy entity registry catalog as explicit metadata, renders the two item-entity types through the vanilla item renderer, gives FallingTaint a block-model renderer foundation and uses explicit no-op placeholders for newly server-complete mobs until visual parity lands. `06_docs/audits/generated/thaumcraft_1_21_entity_foundation_audit.md` now passes `25/25`.
- The focused Wisp behavior/render/FX blocker is now active: Wisp preserves legacy free-flight waypoint AI, hurt/player aggro cadence, chase motion, zap attack cadence/damage/sound, ambient mote particles, billboard render layers, source/target-id zap payload and dynamic aspect rendering contract. `06_docs/audits/generated/thaumcraft_1_21_wisp_behavior_audit.md` passes `11/11`. The first spawn-policy slice also activates the exact legacy Nether natural spawn and `06_docs/audits/generated/thaumcraft_1_21_entity_spawn_policy_audit.md` passes `10/10`. Remaining non-Wisp natural spawn rows and measured in-world pixel parity remain separate row-14/17 work.
- The item/block parity framework quick preset is executable with registry/resource checks plus `texture_color`, `item_visual_parity`, `legacy_shape_parity`, and `legacy_visual_collision_parity`. Use the focused `visual` preset for model/shape/outline/texture/FX certification evidence. Current report-only `visual` summary: `legacy_shape_parity` has `114` rows / `10` review rows; `legacy_visual_collision_parity` has `585` rows, `2` facing-domain mismatches (`golem_builder`, `research_table`), `0` missing rows, `341` unknown rows, and `outline_contract` has `68` match / `0` mismatch / `46` unknown; `item_visual_parity` has `34` missing item models and `219` item visual review rows; `texture_color` has `201` active texture refs with `176` exact matches and `25` review rows; `visual_equivalence_completion` has `17` rows with `11` pass / `6` review / `0` errors.

## Do not change without explicit request

- Do not move or delete large legacy/audit documents until references are checked and the move is recorded in `06_docs/documentation_index.md`.
- Do not treat `thaumcraft:crucible` recipe/page data as in-world crucible gameplay.
- Do not expand the current in-world crucible slice, infusion, essentia transport, broad worldgen, or broad rendering systems without a focused design note and validation path.
- Do not commit generated local reports unless they are intentionally curated under `06_docs/audits/`.

## Near-term tasks

1. Continue blocker-removal work outside the focus/caster layer unless explicitly requested.
   - Row 4 now has real `essentiatransportin` / `essentiatransportout`, `jar_void`, and `mirror_essentia` blocks/BlockEntities where applicable, sided capability boundaries, modern blockstates/item models and runtime transfer/source checks.
   - The old `essentia_importer` / `essentia_exporter` ids stay as non-block, non-player-facing reference aliases; active recipes use real transport ids.
   - Row 13 now has closed standalone device sub-slices for normal item Mirror/Magic Hand Mirror, Arcane/Growth/Fertility Lamps, Infernal Furnace runtime, Infernal Furnace Salis Mundus multiblock activation/rollback, Void Siphon foundation/runtime math and the first Arcane Bore device/entity slice.
   - Row 11 now has the finite Flux Goo -> Taint Fibre blocker slice, the audited TaintSeed/terrain ecology slice, the first taint mob/Thaumic Slime server-foundation slice, the focused FallingTaint crust-physics slice, the focused Flux Rift consequence slice, the Wisp dependency behavior/FX slice, and the exact Wisp Nether natural-spawn slice. Do not expand this into remaining broad natural spawns, focus-cloud rift event execution or final taint/rift visual-particle parity without a focused design/audit slice.
   - Row 14 now has an entity registry foundation, safe legacy item-entity implementations, focused Flux Rift/Arcane Bore entity foundations, Wisp behavior/render/spawn contracts, and the first taint mob server behavior contracts. Do not expand this into golems, focus projectiles/clouds/mines or broad custom renderers without a focused design/audit slice.
   - Row 10 now has the first real Thaumatorium server machine foundation, functional GUI/screen recipe selection, legacy OBJ model path and output-item BER. Measured pixel parity and special alchemy integrations remain separate work.
   - Keep focus medium/effect expansion paused until the user explicitly returns to row 7.
2. Keep already closed behavior audits green after related changes:
   - Combined tube/jar/Alembic/smelter/transfuser/Void-Jar/essentia-mirror runtime audit: `61/61`.
   - Normal mirror/hand mirror behavior audit: `12/12`.
   - Lamp device behavior audit: `10/10`.
   - Infernal Furnace behavior audit: `12/12`.
   - Standalone device blocker audit for Salis Mundus + Void Siphon: `9/9`.
   - Thaumatorium behavior audit: `19/19`.
   - Dedicated Bellows audit: `0` errors.
   - Item/equipment behavior audit: `17/17`.
   - Focus/caster core audit: `10/10`.
   - Focus cast execution audit: `5/5`.
   - Entity foundation audit: `25/25`.
   - Flux/Bore/Thaumatorium blocker audit: `11/11`.
   - Flux Rift consequence audit: `11/11`.
   - Wisp behavior/render contract audit: `11/11`.
   - Entity spawn policy audit: `10/10`.
   - Flux Goo/Taint Fibre blocker audit: `13/13`.
   - Taint ecology blocker audit: `15/15`.
   - Taint mob blocker audit: `14/14`.
   - FallingTaint blocker audit: `10/10`.
   - Keep final measured valve/vent and armor pixel parity as visual review work, not behavior blockers.
3. Continue item/block parity automation from `06_docs/audits/item_block_parity_framework.md`:
   - Classify known renames, legacy metadata variants, intentional no-item/no-loot entries, missing item models, item visual review rows, and allowed modern extras before enabling safe CI failures.
   - Treat `golem_builder` and `research_table` facing-domain mismatches from `legacy_visual_collision_parity` as focused visual/blockstate parity candidates, not as framework errors.
   - Keep inferred legacy IDs review-only until confirmed by class source, secondary decompile, original jar or runtime evidence.
   - Do not call report-only visual-boundary evidence strict visual parity until targeted fixes and in-game comparison are done.
4. Keep bridge/placeholder outputs clearly marked as non-gameplay implementations until their subsystems exist.
5. Keep reusable audit scripts under `tools/audits/`.
6. Keep local/generated audit output under ignored `tools/reports/local/` or curate it into `06_docs/audits/` only when useful.
7. Use `06_docs/migration/remaining_subsystem_unblock_plan.md` as the current subsystem unblock order after Bellows and item/equipment.

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
- Current live catalog audit result: `190 READY`, `13 DEFERRED`, `0 LEGACY_MISSING`.
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

- Registered alchemical_construct, essentia_importer, and essentia_exporter bridge item ids. Thaumatorium recipe components now use the real `essentiatransportin` / `essentiatransportout` block ids; importer/exporter are no longer active recipe dependencies.
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
- Do not expand this further into Flux Rift focus-cloud event execution, rift visual parity, taint mob natural-spawn/rendering parity, Thaumic Slime polish, essentia networks, automation, item pulling radius or special alchemy side effects without a new focused slice.
## Crucible contact cooldown scope note

- Moved the living-entity crucible contact damage cooldown from the singleton block instance to TCCrucibleBlockEntity.
- This prevents one crucible position from throttling or advancing another crucible's contact damage cadence.
## Infusion gameplay boundary design note

- Added a focused design document for the first in-world infusion behavior slice.
- Added an infusion recipe data audit to validate catalyst/components/aspects/result shape before behavior activation.
- Full in-world infusion completion, broad pedestal UI, instability events and visual effects remain deferred beyond the current non-consuming matrix/pedestal start-plan plus read-only completion-plan slice.
## Infusion validation helper note

- Added `TCInfusionRecipeMatcher` as a non-mutating server-side validation helper for catalyst, components and aspect costs.
- Added `TCInfusionAssembly` and `TCInfusionValidationResult` as the current server-owned input snapshot and validation-result boundary.
- `TCInfusionRecipeMatcher` now uses NeoForge `RecipeMatcher` like legacy Forge 1.12.2, so component matching is unordered but exact 1:1 by count.
- `tools/audits/audit-infusion-behavior.ps1` validates the current boundary at server runtime and currently passes `95/95`.
- Full server-side in-world completion, essentia drain/source, instability effects and caster start are active. The focused Stage 13 renderer slice is implemented; final visual parity remains an explicit in-world comparison task.

## Infusion start-plan boundary note

- Added `TCInfusionCraftingPlan` and `TCInfusionStartResult` as the saved server-owned active infusion start state.
- The matrix can start an audited plan only after legacy-shaped validation succeeds; the plan records recipe id, research, instability, catalyst, matched component stacks, matched pedestal positions, required aspects, result and player name.
- Start planning intentionally does not consume catalyst, components, essentia, aura or aspects. Legacy `TileInfusionMatrix.craftingStart` also captures recipe state first; consumption happens later during crafting cycles.
- The behavior audit now covers active plan creation, field parity for `CLOUDRING`, component pedestal positions, NBT round-trip, second-start rejection and abort.

## Infusion completion-readiness boundary note

- Added `TCInfusionCompletionPlan` as the read-only server-owned readiness check for an active infusion plan.
- The matrix now rechecks current center catalyst, the originally matched component pedestal positions/stacks, and available aspect totals before any future mutation.
- The behavior audit now covers valid readiness, missing-aspect rejection, changed catalyst rejection, changed component rejection, and missing component pedestal rejection.
- This still does not consume pedestal items, drain essentia/aspects, replace the catalyst with output, roll instability, or run beams/particles/sounds.
## Infusion legacy cycle semantics audit note

- Added a legacy cycle semantics audit for `TileInfusionMatrix` method anchors before implementing any mutation executor.
- Do not connect a one-shot item mutation path to player-facing matrix activation yet.
- Next safe slice: an audited, non-player-facing mutation/executor boundary based on `TCInfusionCraftingPlan` and `TCInfusionCompletionPlan`.
## Infusion container and essentia cycle audit note

- Added a focused legacy audit for item/container and essentia/source timing before implementing an infusion mutation executor.
- Next safe code slice: non-player-facing, audit-only `TCInfusionMutationExecutor` based on `TCInfusionCompletionPlan`.
- Keep real essentia network drain, instability and FX deferred.
## Infusion mutation executor audit boundary note

- Added audit-only `TCInfusionMutationExecutor` for valid `TCInfusionCompletionPlan` execution.
- The executor is not connected to normal player-facing matrix activation.
- Runtime audit covers valid execution, result placement, component consumption, active plan cleanup, invalid completion rejection and invalid-plan no-op state.
## Infusion container remainder audit note

- Added a current-data audit for known vanilla container/remainder inputs in infusion catalyst/component recipe data.
- This checks whether the audit-only mutation executor can safely use raw pedestal extraction for the current recipe set.
- Generic future container parity remains deferred until real player-facing execution is designed.
## Infusion container remainder guard note

- Added `TCInfusionContainerRemainderPolicy` so the mutation executor refuses plans containing known container/remainder inputs until explicit policy exists.
- This protects current bucket/bottle/potion infusion inputs from silent deletion if the executor is reused before player-facing parity work.
- Runtime audit now covers safe Cloud Ring inputs and a blocked water-bucket plan.
## Infusion aspect source boundary note

- Added audit-only `TCInfusionAspectSource` to model all-or-nothing aspect drain semantics without jar/tube/aura integration.
- Runtime audit covers exact drain success and insufficient-aspect no-op failure.
- Player-facing infusion completion remains deferred until this source boundary is connected to a real essentia source policy.
## Infusion aspect-source executor integration note

- Added `TCInfusionMutationExecutor.executeWithAspectSource(...)` for audit-only completion that drains an in-memory source before item mutation.
- Runtime audit covers exact source drain plus item completion, and insufficient source rejection with no item/source mutation.
- Player-facing completion remains deferred until this boundary is wired to a real essentia source policy.
## Infusion aspect source interface note

- Refactored `TCInfusionAspectSource` into a small interface while preserving the audit-only `memory(...)` source.
- This prepares future real essentia source implementations without changing current non-player-facing behavior.
## Infusion player-facing completion gate note

- Added an explicit matrix completion gate: `TCInfusionMatrixBlock.isPlayerFacingCompletionEnabled()` returns false.
- Matrix caster interaction now reports completion disabled and remains validation/status-only.
- Runtime audit verifies the player-facing completion gate stays disabled.
## Infusion component remainder policy note

- Added component-side remainder preservation for the audit-only mutation executor.
- Bucket, potion/honey bottle and stew-style component remainders are restored to their original pedestal.
- Container/remainder catalysts remain blocked until a center-output/remainder policy is designed.
## Refreshed infusion container remainder audit note

- Refreshed the container remainder audit after adding component-side remainder preservation.
- The audit now distinguishes handled component remainders from still-blocked catalyst remainders and tag-input follow-ups.
## Infusion tag input expansion audit note

- Added an audit for tag-based infusion catalyst/component inputs.
- The audit expands local tags, reports external or missing tags, and checks for known remainder item members.
## Infusion built-in tag fallback audit note

- Added a reusable tag expansion audit tool with a built-in fallback for `minecraft:wool`.
- The refreshed audit reports no known remainder items through local or built-in fallback tag expansion.
## Infusion real aspect source resolver boundary note

- `TCInfusionAspectSourceResolver` now discovers `TCAspectSourceContainer` BlockEntities in the exact legacy range-12 volume and orders them nearest-first.
- The first supported source is `thaumcraft:jar_normal`; unknown source types fail closed and transient tube buffers are excluded.
- Runtime audit verifies range, ordering, blocked-source and insufficient-source no-op behavior.
## Infusion real source policy checkpoint

- Added `06_docs/gameplay/infusion_real_source_policy_design.md` to define the next real source policy boundary.
- Current infusion completion remains audit-only: executor/source/remainder/tag checks exist, but player-facing matrix completion is disabled.
- The next implementation slice should fail closed and resolve exactly one supported real source type before enabling any player-facing completion.
## Infusion real source candidate audit note

- Added `tools/audits/audit-infusion-real-source-candidates.ps1` and `06_docs/audits/infusion_real_source_candidate_audit.md`.
- Use this audit before choosing the first real aspect/essentia source adapter for infusion completion.
- Keep player-facing completion disabled unless a reviewed source type has stable storage semantics and a fail-closed resolver path.
## Infusion transport source readiness note

- Added `tools/audits/audit-infusion-transport-source-readiness.ps1` and `06_docs/audits/infusion_transport_source_readiness_audit.md`.
- Use this audit to decide whether existing essentia transport classes can safely back the first real infusion source resolver slice.
- Do not connect player-facing infusion completion to tubes or transport block entities without reviewed read/drain/storage semantics.
## Infusion Warded Jar source checkpoint

- Added the legacy-id `thaumcraft:jar_normal` block/item/BlockEntity and corrected Warded Jar recipe dependencies to use that identity.
- `TCTransportInfusionAspectSource` is retained only for isolated transport tests; it is not a resolver-backed gameplay source.
- Player-facing completion remains disabled until exact one-point legacy cycle semantics, instability and FX are implemented.
## Alembic legacy source audit note

- Use `06_docs/audits/alembic_legacy_transport_source_audit.md` before implementing the next Alembic endpoint batch.
- Scope should stay on Alembic as a transport endpoint; full smelter inventory/fuel/efficiency remains separate.
## Alembic endpoint implementation note

- Added Alembic as the first real smelter output transport endpoint boundary.
- Continue to keep smelter inventory/aspect/fuel/efficiency separate from this endpoint slice.
## Smelter legacy machine audit note

- Use `06_docs/audits/smelter_legacy_machine_model_audit.md` before replacing the current smelter skeleton with the real machine model.
- The next implementation scope should be smelter inventory/aspect/fuel/efficiency plus Alembic output hookup only if the audit evidence supports it.
## Smelter machine model boundary note

- Added the first basic smelter block entity machine-state boundary.
- Next smelter batch should implement ticking/fuel/input aspect conversion and Alembic output from this persisted state.
## Smelter tick-state progression note

- Added basic server tick state progression for `TCSmelterBlockEntity`.
- Next smelter batch should add real fuel start/consumption and item aspect conversion before Alembic output.
## Smelter fuel/input conversion note

- Added basic smelter fuel start/consume and input item aspect conversion into the smelter aspect buffer.
- Next smelter batch should stay focused: either legacy efficiency/flux loss or Alembic output transfer.
## Smelter Alembic output note

- Added basic smelter buffered essentia output into the Alembic block entity above it.
- Next smelter batch should implement legacy efficiency/flux loss or auxiliary output discovery, not both at once.
## Smelter efficiency/flux note

- Added basic smelter efficiency loss and pending flux accounting during input aspect conversion.
- Next smelter batch should implement vent mitigation or aura pollution for pending flux, not both with auxiliary smelter discovery.
## Smelter pending-flux aura bridge note

- Added an audit for connecting smelter `pendingFlux` to the current aura/flux API.
- Next batch should use the audit result to add either direct aura pollution or a missing aura mutation boundary.
## Smelter pending flux aura pollution note

- Added direct aura pollution for smelter `pendingFlux` through `AuraHelper.polluteAura`.
- Next smelter batch should add smelter vent mitigation or auxiliary output discovery, not both at once.
## Smelter vent placeholder mitigation note

- Added minimal `smelter_vent` block placeholder and connected it to basic smelter pending-flux mitigation.
- Future vent batch should make vent placement/facing exact and should not mix in aux smelter discovery.

## Smelter variant endpoint boundary note

- Added an audit for the `smelter_basic` / `smelter_thaumium` / `smelter_void` runtime boundary.
- Next implementation should avoid mixing endpoint transport placeholders with actual upgraded smelter machine behavior.

## Smelter machine type field note

- Added a variant-ready machine type field to the basic smelter block entity.
- `smelter_thaumium` and `smelter_void` runtime activation remains blocked by the endpoint/machine ownership split.

## Upgraded smelter endpoint machine bridge note

- `smelter_thaumium` and `smelter_void` now bridge to smelter machine state while still exposing the transport endpoint interface.
- Next smelter slices can verify type-specific speed/efficiency and then address exact placement/facing/Bellows behavior.

## Smelter Bellows placeholder boost note

- Added minimal Bellows block/id and first smelter speed interaction through the existing `smeltTimeForVis` formula.
- Exact Bellows visuals and any non-smelter Bellows interactions remain future work.

## Smelter vent facing mitigation note

- Upgraded `smelter_vent` to a facing placeholder and tightened smelter pending-flux mitigation to require a vent facing the smelter.
- Future work: exact vent model, placement parity and visual emission behavior.

## Smelter auxiliary Alembic routing note

- Added `smelter_aux` placeholder and first auxiliary Alembic output routing from the smelter buffer.
- Future work: exact aux model, valid placement/attachment rules and broader legacy multiblock behavior.

## Smelter runtime boundary audit note

- Added a smelter runtime boundary audit covering registry ids, output method uniqueness, Bellows, vent, aux routing and upgraded smelter endpoint bridge.
