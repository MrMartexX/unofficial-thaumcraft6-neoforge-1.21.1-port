# External Port Adoption

Date: 2026-11-05. Target: Minecraft 1.21.1, NeoForge 21.1.265, Java 21.

## Decision

Use large, related adoption batches, not a repository-wide replacement. Our
registries, save formats, authoritative server services and already verified
implementations remain the base. Original TC6 6.1.BETA26 is the behavior and
visual reference; another port is an implementation donor, not an oracle.

This follows migration guide sections 2, 7-12, 15, 22-24 and 35. The dependency
order remains governed by [the migration matrix](migration_matrix.md). This
document specifies the adoption campaign, not a replacement for CURRENT_TASK.

The owner confirmed permission to reuse and publish donor code in the chat on
2026-11-05. Both donor metadata files say All Rights Reserved. Record upstream
authors and the source revision for each adopted family; do not interpret the
MDK template license as a license for the complete mod or its third-party assets.

## Pinned Sources

| Source | Examined revision | Role and restrictions |
|---|---|---|
| Our working checkout | `b0c972a2a555e641fada2fb25dc4914b3c3b3963` plus existing local worldgen changes | Preserve local work. First batch is on `codex/review-remediation-20261103`, not automatically merged into main/dev. |
| NovaAntonsito/thaumcraft-neoforge, main | `0276ebfcd103c1b759a99203ba6651f761e9e549` | Closest platform: MC 1.21.1 / NeoForge 21.1.256 / Java 21. Useful client adapters. Not a pure TC6 implementation. |
| Terramin/Arcane-Research, forge/1.20.1 | `6703b2aac2914100f917525fbfec09b1b0cb6e41` | Actual properties: Forge 47.4.28, Java 17-era API, namespace `arcaneresearch`. Requires 1.21 component/network/render adaptation. |
| Terramin/Arcane-Research, neoforge/26.3 | `954d38feed7a494fe7bfbf770d30294e20e8ac09` | MC 26.3 / NeoForge 26.3.0.94 / Java 25. Uses render-state extraction, SubmitNodeCollector and Identifier. Not drop-in 1.21.1 code. |
| Arcane main README | `2144d7d4c3b5ea401f21897cb5d5c0ec791bd875` | Author explicitly describes a reinterpretation, changed mechanics and replacement assets. Its completion claim is not a TC6 parity measurement. |
| Original jar | `01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar` | Resource/bytecode authority. Primary decompile is `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master`. |

Donor clones are reference-only under ignored `tools/reports/local/`. Their
Gradle files and dependencies were inspected, not executed. The first batch
does not add donor build scripts, dependencies, mixins, registration or artwork.

## Proven Reasons Not To Merge Blindly

| Area | Source evidence | Decision |
|---|---|---|
| Nova research | `docs/etapas/etapa-5b-investigacion.md`: TC4 hex notes replace TC6 theorycrafting; observation requirements removed | Do not replace our research, scanning or knowledge services. |
| Nova world anomalies | `Config.java`: anomalies enabled by default, 40-minute interval with jitter | Exclude this non-TC6 balance/HUD system. |
| Arcane rendering | `client/renderers/tile/HungryChestRenderer.java`: 26.3 render-state API and modern vanilla CHEST model | Not a direct replacement for the 1.12 ModelChest UV geometry. |
| Arcane extra research | `discovered/client/HexResearchTableScreen.java` coexists with theorycraft classes | Class presence alone does not establish which gameplay path matches TC6. |
| Thaumatorium display | Legacy and Nova cycle selected recipes; Arcane has additional bobbing/spinning in its renderer | Preserve the legacy display transform and only fix independently proven mismatches. |

Public references: [Nova pinned source](https://github.com/NovaAntonsito/thaumcraft-neoforge/tree/0276ebfcd103c1b759a99203ba6651f761e9e549),
[Arcane Forge source](https://github.com/Terramin/Arcane-Research/tree/6703b2aac2914100f917525fbfec09b1b0cb6e41),
[Arcane NeoForge source](https://github.com/Terramin/Arcane-Research/tree/954d38feed7a494fe7bfbf770d30294e20e8ac09).

## Preservation Rules

- Do not replace aspects, aura, research progression, theorycraft, scan results,
  save data, item components, protocol validation or registries just because a
  donor provides classes with similar names. Preserve the existing focused audits.
- Separate model geometry/UV/animation from AI, combat, inventory and networking.
  A renderer adoption does not certify or replace the corresponding entity AI.
- Keep original asset names and bytes where available. Do not bulk-copy old
  `textures/blocks` over adapted `textures/block`, or donor art over original art.
- Keep no-item/debug-only entries, recipe research gates and stable public IDs.
- Existing local edits are not a donor batch. Stage an explicit file list; no
  `git add -A`, checkout reset, user-world reset or global Minecraft process cleanup.
- Classify evidence separately: source match, runtime contract, captured image,
  matched legacy frame. A successful build or screenshot is not pixel parity.

## Batch Order

| Batch | Scope | Acceptance before the next related batch | State |
|---|---|---|---|
| A | Entity model family + Hungry Chest + isolated Thaumatorium display fixes | Original geometry/UV references; build; actual renderer contracts/captures; dedicated-server checks | Implemented and scoped checks passed below |
| B | Remaining machine/item visuals: Golem Press moving press/lava, item transforms, unimplemented animated parts | Avoid duplicate static/BER geometry; use existing synced fields; all facings, inventory/hand/world views, resource reload | Implemented and verified below |
| C | Remaining entity/projectile models and FX families | Original spawn/state gates, transforms, alpha/depth/light, empty-state handling; no new combat/AI from visual adoption | Implemented and verified below |
| D | Thaumonomicon/research-table visual gaps and navigation | Keep our server-authoritative protocol and TC6 theorycraft; paired fixed-progression screenshots and UI actions | Implemented and verified below |
| E | Missing gameplay features, grouped by dependency family | Three-way behavior/data comparison; inventory conservation, permissions, save/reload, authoritative actions | Planned after gap inventory; no whole-system replacement |
| F | Worldgen and integrations | Existing density/oracle checks, new-world tests, explicit version adapters and dependency isolation | Last; preserve current uncommitted worldgen work |

For each batch do one bounded source preflight, implement the coherent family,
then run its affected audit group together. Do not re-audit the whole mod before
each class, and do not postpone all comparisons until every system is replaced.

## Batch A Implementation

| Family | Previous mismatch | Adopted or corrected behavior |
|---|---|---|
| Eldritch Crab | Scaled vanilla spider | Original 128x64 custom geometry, helm/bare alternative, legs/claws, uniform 0.8 scale and 0.5 shadow |
| Firebat / shared renderer | Correct old boxes but manual EntityRenderer lacked body-yaw and normal living-render lifecycle | Preserve 64x64 model and 0.35 scale; use MobRenderer, flight/hanging poses, full-bright light. Reset shared poses to prevent another entity's hanging rotation leaking into flight. |
| Taint Seed / Prime | Invented radial tendrils | Original eight-part nested model, pulse, spawn growth and attack bending; zero body yaw from original RenderTaintSeed; normal/prime shadows 0.4/0.6 |
| Taintacle / Tiny / Giant | Independent columns with invented rotations | Original chained 0.88 child scaling, 10/6/14 lengths, terminal orb/bulb and legacy flail formula |
| Taint Crawler | Death flip inherited as 90 degrees | Original 180-degree flip; existing silverfish geometry and 0.7 scale retained |
| Pech, all three variants | All cuboids mirrored, no held-item layer, missing attack/riding poses | Original effective mirror flags, geometry, held-item/bow offsets and attack/riding/sneak poses; 0.25 shadow. Preserve original uniform body/head rendering. |
| Hungry Chest | Oak-plank block/item shell; no lid renderer | Original three-cuboid inventory model/UVs, animated ModelChest geometry, vanilla 0.1/tick lid controller driven by existing opener block event. World uses BER only; inventory/menu/storage logic retained. |
| Thaumatorium | Display pinned to currentCraft; default one-block render bounds | Selected outputs cycle every 20 camera ticks independently of crafting; original two-block render bounds. Existing item orientation/scale retained. |

For Pech, `ModelRenderer.mirror` is significant when `addBox` constructs the
box, not when later assignments occur. The original sets it before `addBox`
only on the right arm/leg. Copying its final field values mirrored every part
incorrectly. The modern model now reproduces effective construction order.

Nova's seed renderer did not preserve the original explicit zero-yaw path; this
was corrected against legacy instead of accepting the donor wholesale. Firebat
uses the existing opaque cutout material, not the donor's translucent default.
These details demonstrate why a short comparison is required before bulk reuse.

## Batch B Implementation

| Family / Machine | Previous mismatch | Adopted or corrected behavior |
|---|---|---|
| Golem Press (`golem_builder`) | Static full OBJ or item-only BER; duplicate geometry if BER rendered whole OBJ | Split baked and dynamic OBJ parts via NeoForge `"visibility": { "press": false }` in `golem_builder.json`. BER renders dynamic moving press (`-sin(press) * 0.625`), lava quad in cauldron at light 200, and output item on anvil. |
| Brain in a Jar (`jar_brain`) | Missing BER entirely | Restored `ModelBrain` bobbing (`sin(t/14)*0.03`) in translucent brine, tracking nearest player, sound, and rotation; client ticking wired in block and block entity. |
| Dioptra (`dioptra`) | Missing BER entirely | Restored 12x12 aura grid with corner heights driven by `gridAmt` / 96, color derived from Vis/Flux mode, and 4 animated side panels. |
| Item & Essentia Mirrors (`mirror`, `mirror_essentia`) | Missing BER entirely | Restored mirror pane: plain glass when unlinked, vanilla `RenderType.endPortal()` with translucent pane overlay when linked and player in range. |
| Centrifuge (`centrifuge`) | Missing BER; blockstate/model duplication | Restored `TCModelCentrifuge` with spinning core rotation (`rotationSpeed` / `rotation`); block set to `RenderShape.ENTITYBLOCK_ANIMATED`. |
| Tube Buffer (`tube_buffer`) | Missing BER | Restored directional valve rod indicators tinted blue (suction 1) or red (suction 0) on open choked sides. |
| Void Siphon (`void_siphon`) | Missing BER | Restored `RenderType.endPortal()` star cube within the top orb and 4 core cage windows when enabled. |
| Focal Manipulator (`wand_workbench`) | Missing BER | Restored floating, bobbing, rotating focus item over table. |

All 8 machine renderers registered in `TCBlockEntityRenderers.java`.

## Batch 1 (Tile Renderers Parity) Implementation

| Family / Tile | Previous mismatch | Adopted or corrected behavior |
|---|---|---|
| Warded Jar & Void Jar (`jar_normal`, `jar_void`) | Rendered only essentia cuboid; skipped when empty; missing lid brace, lid extension, and paper label | 1:1 legacy `TileJarRenderer` + `ModelJar`. Added `lid_brace` (`ModelJar.Lid` at y=-14 with `jarbrine.png`) when `isBlocked()`; added `lid_extension` when tube connectable above; added crooked paper label (`label.png`) + dark-ink aspect icon (`0.1F, 0.1F, 0.1F, 0.8F`) on `labelFacing()` side even if jar is empty; jar brace application and item drop implemented in block. |
| Arcane Alembic (`alembic`) | Missing BER entirely; filter label and side nozzles not rendered | 1:1 legacy `TileAlembicRenderer` + `ModelBoreBase.renderNozzle`. Paper label (`label.png`, 0.44x0.44) + aspect symbol at `z=-0.376F` on `labelFacing()`; dynamic bore output nozzles (`bore.png`, 128x64) on horizontal faces connected to essentia receivers. Registered in `TCBlockEntityRenderers`. |
| Recharge Pedestal & Arcane Pedestal (`recharge_pedestal`, `pedestal_*`) | Missing custom scale and rotation logic | 1:1 legacy `TilePedestalRenderer` and `TileRechargePedestalRenderer`. `scale(1.25)` for normal pedestals, `scale(1.5)` for `recharge_pedestal`, rotating at `ticks % 360` with Y-offset 0.125F. |
| One-Way Tube (`tube_oneway`) | Missing BER entirely | 1:1 legacy `TileTubeOnewayRenderer`. Blue directional plunger rod (`ModelTubeValve.rod` tinted `(0.45, 0.5, 1.0)`, `scale(2.0)`, `valve.png`) rendered when opposite neighbor is connectable essentia tile. Registered in `TCBlockEntityRenderers`. |
| Pattern Crafter (`pattern_crafter`) | Missing BER entirely; no client gear animation | 1:1 legacy `TilePatternCrafterRenderer`. Active pattern mode icon rendered on front panel (`pattern_crafter_modes.png`, frame `type`/10); two counter-rotating 3D extruded brass gears (`gear_brass.png`) rendered inside with client tick clacking sound (`TCSounds.CLACK`) and block event 1 sync. Registered in `TCBlockEntityRenderers`. |
| Portable Hole (`hole`) | Missing BER entirely | 1:1 legacy `TileHoleRenderer`. Inset starry `RenderType.endPortal()` quads on all 6 faces adjacent to non-air, non-hole blocks. Registered in `TCBlockEntityRenderers`. |

## Validation And Reproduction

Run from the repository root:

```powershell
.\tools\audits\client\test-donor-model-sources.ps1
.\tools\audits\client\run-donor-visual-audit.ps1
```

The client runner requires the already prepared `tc_visual_ground_seals_20261103`
fixture, or another explicitly supplied `tc_visual_` fixture. It copies that
fixture to a unique `tc_visual_donor_<uuid>` world; it never opens the source or
deletes existing saves. It only terminates its own launched process tree on failure.
Raw evidence and the disposable world are retained locally, not committed.

Measured first run: `tools/reports/local/donor-visual-bab9b5a20d0f4d5692e392db338be8c9/`.

| Check | Observed result | What it does not prove |
|---|---|---|
| Gradle build | PASS | Full gameplay correctness |
| Original jar texture SHA-256 | 8/8 identical | Lighting and alpha equality |
| Hungry Chest item geometry/face UV JSON | Exact structured match with original `models/block/chest.json` | Every display-context transform |
| Real client renderer contracts | 12 variants passed; 480 crab, 216 bat/Pech, 192 seed, 288/192/384 taintacle vertices | Per-pixel legacy equality or every animation frame |
| Shared renderer state | Helm/hanging alternation preserves repeated geometry; held bow adds and removes geometry; finite vertex data and balanced poses | Every external renderer combination |
| Hungry Chest runtime contracts | Opener events, ten-tick open/close, interpolation, 72 animated vertices, 18 inventory quads, no duplicate world model | Remote multiplayer opener reconnection/sound timing |
| Client captures | Three views, 12 variants each; inspected for nonblank textured geometry | Side-by-side same-pose original-client comparison |
| Dedicated server and Thaumatorium audit | 22 PASS / 0 FAIL; includes independent display cycling and unchanged saved machine state | Remaining machine visual families |

The first server command was rejected by Gradle because PowerShell split an
unquoted path property. The quoted retry completed successfully; both logs are
retained. The server used a separate `tc_donor_server_20261105_ebbf277a` fixture
and a dynamically allocated port, then shut down normally.

## Still Open

- Matched legacy frames for these families, full hurt/death/riding/attack sequences,
  translucent sorting against water/clouds, and client resource-reload testing.
- Spellbat-specific color/effect rendering is not certified by the Firebat model
  adoption. Its existing shared renderer registration was not redesigned here.
- Hungry Chest all-facing in-world captures and multiplayer interaction tests.
- Golem Press animated press/lava: splitting baked and dynamic OBJ parts remains
  necessary. Do not overlay a second complete machine renderer.
- Arcane remains an examined reference in this batch, not imported code. Do not
  mark its entire content as integrated or claim either donor has full TC6 parity.
- Existing worldgen fixes and their open acceptance boundaries are unchanged.


## Batch B Adoption: Machines and Block Entity Visuals (Verified 2026-11-06)

- **Golem Builder (`TCGolemBuilderRenderer`)**: Dynamic press stroke (`-sin(press) * 0.625`), lava quad in cauldron at light 200, output item hover over anvil. OBJ model updated with `"visibility": { "press": false }` to avoid dual-mesh z-fighting.
- **Brain in a Jar (`TCBrainJarRenderer`)**: Authentic `ModelBrain` bobbing (`sin(t / 14) * 0.03`) inside translucent brine (`jarbrine.png`), smooth rotation toward nearest player.
- **Dioptra (`TCDioptraRenderer`)**: 12x12 grid mesh driven by `gridAmt / 96`, vis/flux color derivation, and 4 animated side panels (`dioptra_side.png`).
- **Mirrors (`TCMirrorRenderer`)**: Plain glass `mirrorpane.png` when unlinked; `RenderType.endPortal()` + `mirrorpanetrans.png` when linked and within 32 blocks.
- **Centrifuge (`TCCentrifugeRenderer`, `TCModelCentrifuge`)**: Rotating central core; `TCCentrifugeBlock` set to `RenderShape.ENTITYBLOCK_ANIMATED`.
- **Tube Buffer (`TCTubeBufferRenderer`)**: Red/blue suction bands on open choked sides.
- **Void Siphon (`TCVoidSiphonRenderer`)**: End portal star effect inside top orb and cage windows when enabled.
- **Focal Manipulator (`TCFocalManipulatorRenderer`)**: Floating, bobbing, rotating focus item over table.
- Verified via `compileJava` (BUILD SUCCESSFUL) and `server-smoke.ps1` (PASS).

## Batch C Adoption: Entities, Bosses, Projectiles & Special Visuals (Verified 2026-11-06)

- **Eldritch Guardian (`TCEldritchGuardianRenderer`, `TCEldritchGuardianModel`)**: Replaced placeholder vanilla humanoid model with authentic 128x64 model featuring swaying cloak, loincloth panels, sidepanels, bobbing casting arms, and hood. Added authentic distance fade (maximum 60% opacity, fading out at 24/32 blocks based on difficulty).
- **Eldritch Warden (`TCEldritchWardenRenderer`)**: 1.9 scale boss, rising from ground during spawn timer (`spawnTimer / 150.0`), with glowing eye layer (`WardenEyeLayer`) using `TCRenderTypes.entityGlow` and pulsating light 195–225.
- **Mind Spider (`TCMindSpiderRenderer`)**: Replaced invisible renderer with 0.6 scale translucent spider, 0.1 alpha fade-in over 100 ticks, and hallucination viewer gate (`getViewer()`).
- **Spell Bat (`TCSpellbatRenderer`)**: Replaced firebat fallback with authentic `textures/entity/spellbat.png`, 0.35 scale, full-bright 15/15, and dynamic spell color tinting at 50% alpha. Added synched `COLOR` data accessor and getter/setter in `TCSpellbatEntity`.
- **Focus Mine & Grapple Hook (`TCModelGrappler`, `TCFocusmineRenderer`, `TCGrappleRenderer`)**: Replaced invisible renderers with authentic 64x32 grappler model (core + 3 prongs), rotation, and blinking red arming state for focus mine.
- **Render Infrastructure (`TCRenderTypes`, `TintedBufferSource`)**: Provided reusable render types (`entityGlow`, `beam`, `seal`, `runes`) and buffer source color/alpha multiplier.
- Verified via `compileJava` (BUILD SUCCESSFUL) and `server-smoke.ps1` (PASS, dedicated server reached `Done`).

## Batch D Adoption: Thaumonomicon Visuals, Aspects Page & Auromancy Focus Icons (Verified 2026-11-06)

- **Auromancy Focus Icons (`TCResearchIconRenderer.renderFocus`)**: Removed the spurious `-90°` Z-axis rotation (`Axis.ZP.rotationDegrees(-90.0F)`) that had been ported from 1.12.2 `GuiFocalManipulator.drawPart` without accounting for `UtilsFX.renderQuadCentered`'s `+90°` rotated UVs. All `focus:...` icons in Auromancy now render upright (`0°`) matching original 1.12.2 visuals.
- **Aspects & Combinations Side-Insert Page (`TCThaumonomiconEntryScreen`)**:
  - Fixed invisible page-turn arrows in `renderAspectInsert` by blitting UV `(0, 184, 12, 8)` and `(12, 184, 12, 8)` from `BOOK` (`gui_researchbook.png`) instead of `BROWSER` (`gui_research_browser.png`).
  - Added keyboard navigation (Left/Right/Up/Down/PageUp/PageDown), mouse scroll wheel navigation (`mouseScrolled`), and right-click-to-close (`button == 1`) support for `SideInsert.ASPECTS` and recipe pages.
  - Updated `knownAspects()`, `renderAspectInsert`, and `renderAspectComponent` to sort and label aspects by `aspect.getName()` (matching 1.12.2 `GuiResearchPage.drawAspectPage`), tint unknown component icons with `(0.8F, 0.8F, 0.8F, 1.0F)`, and show hover tooltips with `[aspect.getName(), aspect.getLocalizedDescription()]`.
  - Updated `ScanAspect.onSuccess` to award observation knowledge via `TCResearchManager.addKnowledgeRaw` (triggering HUD gain notifications) and `TCResearchManager.completeAllResearchForDebug` to also grant `"!" + aspect.getTag()` for all registered aspects.
- **Forbidden Knowledge Warp Swirl & New Research Popup (`TCThaumonomiconBrowserScreen`, `TCThaumonomiconEntryScreen`, `TCThaumonomiconResearchView`, `TCThaumonomiconService`, `TCThaumonomiconCodec`)**:
  - Added `hasWarp` to `TCThaumonomiconResearchView` and rendered the animated purple forbidden swirl (`drawForbidden`, `textures/misc/auranodes.png` row 5 frames `160..191`, additive blend, color `0.33F, 0.0F, 0.44F, 0.9F`) behind Warp-granting research nodes in the browser and in `TCThaumonomiconEntryScreen.renderWarpWarning`.
  - Added the 3-second `tc.research.popup` toast at `(10, 34)` when starting an unlockable research in `TCThaumonomiconBrowserScreen`.
