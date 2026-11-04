# V0 visual research: consolidated findings, provenance and one-batch client gate

**Prepared:** 2026-11-04. **Pinned baseline:** `main` `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`. **Working state:** draft PR #11, source-only. **Scope:** original Thaumcraft 6 Forge 1.12.2 compared with NeoForge 1.21.1, `05_neoforge_port/`.

## Decision summary

- The static **inventory** for original block/item declarations, original PNG/metadata and matching model assets is complete **for the specifically enumerated source trees**. The source-level **render semantic audit** has found concrete divergences and left a documented follow-up queue; it is not an exhaustive proof that all renderer methods behave identically.
- **4 P0 visual high-confidence source gaps**, **17 P1 visual source-level gaps or algorithm/shape differences**, **1 separate P0 gameplay/cross-domain discrepancy**, **4 UI functionality/visual families with no equivalent registered menu**. These are risk-priority classifications. **None of these cases has a 1.12.2-vs-1.21.1 in-game A/B evidence bundle yet.**
- Source provenance is unified in the companion JSON with original report, legacy/current path anchors where available, confidence, runtime status, grouped scene and disposition. The register includes **37 rows** across P0/P1/P2/UI triage, including several *positive or review-only* P2 cases; **do not interpret all 37 as defects**.
- Do not start full rendering rework or merge this PR before grouped client comparison and feature-level approval. Fixes should be separate targeted implementation PRs.

## Inventory closure at pinned SHA

| Boundary | Verified evidence | What it does *not* imply |
| --- | ---: | --- |
| Original block registrations vs port | 200 legacy source paths / 175 modern active paths; 167 exact original IDs | 33 nonexact IDs are not 33 missing working features; banners, slab folding and renames differ |
| Original item registrations vs port | 108 legacy constructor declarations / 487 modern active IDs | 25 nonexact families include metadata-to-item splits; behavior and visuals need class-level review |
| Resource graph | 227/227 current blockstate JSON and 784/784 current model JSON parsed; 0 missing active local JSON model targets | Modern resource loader correctness, alpha and texture rendering are untested |
| Texture contents | 855/855 original textures byte-identical at original paths; 124 current `.mcmeta` files parse, frame slices in range | CTM support, animation cadence, 7 JPEG→PNG research page pixel parity untested |
| Legacy models | 274 JSON pairs field-compared; 225 path-normalized structurally equal; 27/27 OBJ SHA-identical; six MTL reviewed | Custom armor Java rigs and inherited display transforms can differ despite preserved mesh/PNG bytes |
| Entity / tile renderers | 43 original and 43 modern entity bindings; 23 original TESR vs 10 current BER bindings | Registrations do not imply output fidelity; renderer aliases and particle-only originals matter |
| GUI routing | 18 original GUI IDs, 13 modern menu+screen pairs, separate Thaumonomicon route | Menu counts alone cannot establish feature loss; four specific GUI routes are deferred/unmatched |

## Highest-priority visual work: 4 P0

| ID | Feature | Source-level finding | Client gate |
| --- | --- | --- | --- |
| PV-01 | Hole shader | Original camera-facing End-shader exposed-wall quads; modern invisible shape and portal particles, no equivalent registered BER | G01 |
| PV-02 | Magic and Essentia Mirrors | Original linked/near 16-plane tunnel; modern facing-only models and no linked-state BER | G01 |
| PV-03 | Void Siphon | Original enabled End-shader surfaces at two heights; current enabled/disabled baked model maps identically, no shader BER | G01 |
| V0FX-01 | Arcane Bore beam | Original draws mining `beam1` quads during digging; port renders device only, with break event but no beam path in renderer | G02 |

These paths are confirmed distinct in pinned source. They are *not* claims about observed client frames.

## P1 visual work: 17 cases

| IDs | Domain | Source-level distinction | Client gate |
| --- | --- | --- | --- |
| PV-04 | Flux Rift | Four-pass polycone, goggles depth and stability wobble vs fixed-purple emissive ribbon pass | G03 |
| V0FX-02 | Taint Bottle | Original 100 custom taint explosions plus 8 item bursts/sound vs current 24 item + 24 mycelium | G08 |
| V0FX-04 | Taint Seed | Legacy segmented dynamic tentacles vs approximate simplified modern foundation geometry | G08 |
| V0ER-focusmine | Entity | Original textured pulsating mine, modern invisible entity | G04 |
| V0ER-golem_dart | Entity | Original 3D textured dart, modern invisible entity | G04 |
| V0ER-grapple | Entity | Original grappler head plus polycone rope, modern invisible entity | G04 |
| V0ER-mind_spider | Entity | Original translucent viewer-gated spider, modern invisible entity | G04 |
| MUV-001 | Ancient stairs | Straight uses `ancient_stone_1`, corner variants preserve original `ancient_tile` | G06 |
| MUV-002 | Arcane stairs | Straight top/side switched to `_1`, corners retain original `_2`/`_3` faces | G06 |
| MUV-003 | Special armor | Four original 128×64 custom-model rigs (210 `.addBox` call sites) not found attached to current ArmorItem client hooks | G05 |
| V0-MCH-01 | Machine TESR: Dioptra | dynamic grid unrendered (see detailed source diff) | G09 |
| V0-MCH-02 | Machine TESR: Focal Manipulator | focus and orbit fx unrendered (see detailed source diff) | G09 |
| V0-MCH-03 | Machine TESR: Hungry Chest | custom chest silhouette and lid missing (see detailed source diff) | G09 |
| V0-MCH-05 | Machine TESR: Tube Buffer | choke red blue rods missing (see detailed source diff) | G09 |
| V0-MCH-06 | Machine TESR: Alembic | aspect label and dynamic nozzle missing (see detailed source diff) | G09 |
| V0-MCH-07 | Machine TESR: Centrifuge | rotation computed but not drawn (see detailed source diff) | G09 |
| V0-MCH-08 | Machine TESR: Pattern Crafter | mode icon and rotating gears missing (see detailed source diff) | G09 |

The four `V0ER-*` P1 labels are a consolidation prioritisation, not newly observed game bugs. Preserve original model motion, tint, visibility rules and texture UV, not merely generic substitute particles.

## P2 source-level machine detail

- **V0-MCH-04 / Tube One-way:** legacy connected-side blue rod has no render path in inspected current one-way tube models or BER registrations. Medium visual priority, client G09 acceptance still pending.

## Cross-domain and deferred features (not silently part of the visual fix)

- **GM-01 / P0 gameplay:** Hole initially fills surrounding perpendicular 3×3 plane in original; modern progression is longitudinal only. This affects visible footprint but fixing the actual block-propagation algorithm needs its own gameplay review and regression tests.
- **17 original world banner block IDs:** not present as exact registered modern world block paths despite preserved orphan assets. Verify alternate representations, placement/saves and variants; do not classify the preserved textures as a completed banner port.
- **Four legacy GUI families:** Arcane Spa (explicitly deferred GUI), Potion Sprayer, Logistics and configurable Golem Seal (modern action-bar/status text only) are not equivalent registered menus. Source review establishes routes, not all device-server semantics.
- **Eight TESR machine visual source gaps classified:** V0-MCH-01..08 (7 P1, 1 P2), including Hungry Chest special mesh/lid, Centrifuge rotor, Dioptra height grid, Focal crystals, Alembic label/nozzle, Tube rods and Pattern Crafter modes/gears. Runtime validation remains G09. Recharge Pedestal continues to use a **shared** Infusion Pedestal BER, so its source registration is not missing.
- **HUD review:** casting-wand/trade display, focus radial choice and seal-area overlay lack direct counterparts in inspected central event paths. Search alternative hooks and compare actual user interaction before claiming global absence.
- **Model/asset watchlist:** Scribing Tools handheld parent, Pure Bucket icon substitution, generic crystal MTL numeric color, Redstone Relay old unregistered ID, `empty` orphan model; CTM integration and seven Thaumonomicon research backgrounds; legacy `hole.json` Forge marker.

## Explicit false-positive corrections

1. **Five of nine invisible entity renderers** have empty/no-projectile original render implementations or separate particle work; only four models are classified source gaps.
2. **Recharge Pedestal** is already rendered by `TCInfusionPedestalRenderer`; test scale, height, rotation speed, do not mark rendering absent.
3. **Six legacy double-slab registry paths** may be represented by `type=double` states on modern single-slab blocks. Old-save ID mapping is unverified, but standalone missing IDs are not six proven visual losses.
4. **52 orphan blockstates** are not 52 missing active block textures. The resource graph has zero active local model-target misses; 51 orphan files retain legacy syntax. The active Hole file itself retains an old marker, but its runtime shape is invisible.
5. **18 versus 13 GUI registrations** cannot be subtracted to infer five absent screens. Thaumonomicon is standalone, and only four specific GUI families are documented as outstanding.

## Single grouped client comparison protocol

**Execution design:** capture the reference once on original 1.12.2 and the modern set once on 1.21.1. Recreate test scenes instead of loading legacy player saves directly in the port; match screen size, FOV, gamma, light/time, camera angles, shader/resource-pack state and item state. Avoid 13 separate build/test cycles.

| Group | Scene | Linked evidence |
| --- | --- | --- |
| G00 | Client environment and resource reload | W-04, W-05 |
| G01 | Portal and device shaders | PV-01, PV-02, PV-03, GM-01 |
| G02 | Arcane Bore mining beam | V0FX-01 |
| G03 | Flux Rift depth and motion | PV-04 |
| G04 | Four invisible-entity geometry gaps | V0ER-focusmine, V0ER-golem_dart, V0ER-grapple, V0ER-mind_spider |
| G05 | Special armor silhouettes and UV | MUV-003 |
| G06 | Stairs geometry and face-texture continuity | MUV-001, MUV-002 |
| G07 | GUI and HUD interaction sweep | V0GUI-spa, V0GUI-potion_sprayer, V0GUI-logistics, V0GUI-seal, W-03 |
| G08 | FX burst and per-tick cadence | V0FX-02, V0FX-03, V0FX-04, V0FX-05, V0FX-06, V0FX-07, V0FX-08 |
| G09 | Eight classified machine TESR gaps and animations | V0-MCH-01..08, W-02 (runtime open) |
| G10 | Texture metadata / Thaumonomicon backgrounds | W-04 |
| G11 | Item display and OBJ material smoke | MUV-004, MUV-005, MUV-008 |
| G12 | Registry orphans and world banners | W-01, MUV-006, MUV-007, W-06 |

### Minimum evidence package and decision rules

- Create one environment manifest with exact original/mod versions, Java, SHA, installed mods, resource packs, graphics options and which features cannot be spawned.
- For each scene, record **legacy image/video**, **modern image/video**, status (`PASS`, `SOURCE_GAP_CONFIRMED_IN_CLIENT`, `INCONCLUSIVE`, `NOT_RUN`, `BLOCKED_BY_UNPORTED_FEATURE`), reproduction steps and log excerpt. A path/texture being present does not yield `PASS`.
- Include modern `F3+T` resource reload and client `latest.log` error scan; do not treat a docs-only GitHub commit or CI green badge as proof of model loader, shader or render parity.
- Blocked GUI/world-banner scenes are meaningful **deferred-feature evidence**, not automatic source-audit failures; route them to an implementation backlog.
- Do **not** change production code, delete legacy placeholders, merge PR #11 or run AIH tasks automatically as part of this handoff. Grouped runtime tests are a future local-client step.

### V0 gate separation

| Gate | Current status | Exit criteria |
| --- | --- | --- |
| V0-I: full source identity/resource inventory | **PASS for pinned listed trees** | Every declared ID and referenced asset enumerated with source provenance; owner of exceptions identified |
| V0-S: dynamic semantic and priority triage | **DOCUMENTED, open reviews** | Every visual family classified as original-like, confirmed source gap, intentionally deferred, or explicitly needing runtime, including unclassified TESR/HUD corners |
| V0-C: original vs port client proof | **NOT RUN** | Reproducible A/B screenshot/video bundle for key P0/P1 scenes plus resource reload logs; no unsupported claims of 100% visual parity |
| V0 overall closure | **BLOCKED** | Owner accepts V0-I and V0-S evidence, runs or explicitly defers V0-C and records all unported features as open backlog |

## Referenced audit provenance

All reports are from draft PR #11, pinned to the same `main` source commit. This checkpoint consolidates them without changing their evidence:

- [`visual_v0_full_block_registry_checkpoint_2026_11_03.md`](./visual_v0_full_block_registry_checkpoint_2026_11_03.md) and [`visual_v0_full_item_registry_checkpoint_2026_11_03.md`](./visual_v0_full_item_registry_checkpoint_2026_11_03.md) (complete registry source crosswalks).
- [`visual_v0_resource_graph_first_pass_2026_11_03.md`](./visual_v0_resource_graph_first_pass_2026_11_03.md) (all blockstate/model JSON, 52 orphans and loader paths).
- [`visual_v0_dynamic_renderer_census_2026_11_03.md`](./visual_v0_dynamic_renderer_census_2026_11_03.md) (invisible entities, BER/TESR and false positives).
- [`visual_v0_gui_fx_hud_census_2026_11_03.md`](./visual_v0_gui_fx_hud_census_2026_11_03.md) (GUI and HUD routing).
- [`visual_v0_client_fx_animation_semantic_diff_2026_11_03.md`](./visual_v0_client_fx_animation_semantic_diff_2026_11_03.md) (beam, bottle, Nitor and stream recipes).
- [`visual_v0_portal_mirror_rift_semantic_diff_2026_11_03.md`](./visual_v0_portal_mirror_rift_semantic_diff_2026_11_03.md) (PV-01–04 and GM-01).
- [`visual_v0_texture_fingerprint_animation_checkpoint_2026_11_03.md`](./visual_v0_texture_fingerprint_animation_checkpoint_2026_11_03.md) (855 original texture hashes and metadata).
- [`visual_v0_model_geometry_uv_material_checkpoint_2026_11_04.md`](./visual_v0_model_geometry_uv_material_checkpoint_2026_11_04.md) (274 JSON pairs, 27 OBJ, 6 MTL and armor UV).
- [`visual_v0_legacy_machine_tesr_semantic_diff_2026_11_04.md`](./visual_v0_legacy_machine_tesr_semantic_diff_2026_11_04.md) (eight machine TESR family source-level differences and state-based G09 acceptance).

**Companion machine register:** `06_docs/audits/generated/visual_v0_consolidated_finding_register_2026_11_04.json`. Contains the complete 37-row triage, evidence anchors, explicit counterexamples and 13 grouped client-scene contracts.

**Verification performed for this checkpoint:** read and cross-reconciled all 12 PR narrative reports, reviewed five structured evidence JSON datasets and the eight-record machine TESR delta, rechecked exact source `main` SHA and PR draft state. No Minecraft client, local PowerShell, Gradle or automated gameplay tests were run. No gameplay/client files modified.
