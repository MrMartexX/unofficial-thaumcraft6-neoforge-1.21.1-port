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
| B | Remaining machine/item visuals: Golem Press moving press/lava, item transforms, unimplemented animated parts | Avoid duplicate static/BER geometry; use existing synced fields; all facings, inventory/hand/world views, resource reload | Next candidate batch, not implemented here |
| C | Remaining entity/projectile models and FX families | Original spawn/state gates, transforms, alpha/depth/light, empty-state handling; no new combat/AI from visual adoption | Planned |
| D | Thaumonomicon/research-table visual gaps and navigation | Keep our server-authoritative protocol and TC6 theorycraft; paired fixed-progression screenshots and UI actions | Planned, not donor research replacement |
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
