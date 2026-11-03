# Thaumcraft 6 → NeoForge 1.21.1: Consolidated Visual Parity Research

> **Research snapshot: November 3, 2026. Investigation ongoing; this is NOT a completed parity audit. No mod code, audit scripts, or CI configuration have been changed.**
>
> **Code baseline:** [`main` at `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/commit/72eaac17a1e1c0c51b27cb151ef7f244944c89d5). Unless otherwise stated, source links refer to this exact snapshot. Revalidate findings when the target branch changes.

## Purpose and reading order

This is the **canonical research checkpoint** for the Thaumcraft 6 (Minecraft 1.12.2/Forge) → Minecraft 1.21.1/NeoForge visual-parity investigation. It integrates the original defect investigation, the follow-up static checks, additional source/commit history, negative controls, and the remaining work.

The **reader-facing report is in English**. The two original Russian research reports remain **fully preserved as collapsible archival evidence** in Appendices A and B. They are deliberately not silently rewritten: the original wording, links, tables, intermediate findings, and version history remain auditable. This archival material does not supersede the more recent English findings. Technical results from the user-supplied chat history are integrated as H-series records; private conversation text is not posted to this public repository.

### Navigation

1. [Evidence and confidence levels](#evidence-and-confidence-levels)
2. [Scope and coverage](#scope-and-coverage)
3. [Initial findings: D01–D13](#initial-findings-d01d13)
4. [Recovered first-pass details: H01–H10](#recovered-first-pass-details-h01h10)
5. [Follow-up investigation: N01–N16](#follow-up-investigation-n01n16)
6. [Additional source-history checks: F01–F39](#additional-source-history-checks-f01f39)
7. [Black borders: competing causes](#black-borders-competing-causes)
8. [What remains before completion](#what-remains-before-completion)
9. [Provenance and source archive](#provenance-and-source-archive)

## Evidence and confidence levels

| Status | Meaning | Caution |
| --- | --- | --- |
| `confirmed_code` | A specific code, registration, resource, or call-chain difference was inspected | A screenshot-visible artifact may still require runtime reproduction |
| `confirmed_legacy_mismatch` | The difference violates an inspected original Thaumcraft behavior or rendering rule | Does not identify a universal root cause for similar-looking problems |
| `historical_find` | Recorded during the first investigation and retained for continuity | Recheck against current SHA, active IDs, and runtime paths |
| `probable_cause` | The technical mechanism plausibly explains a symptom | Is not a proven root cause without a controlled test |
| `negative_control` | A specific suspected mechanism was disproved or not substantiated | Does not certify that the object is perfect |
| `requires_client` | Matching client conditions, screenshots, or execution traces are needed | Must not be represented as "passed" |
| `not_checked` | The object or condition lacks verified coverage | Must not be represented as "no defect" |

For each future object: record `ID | active registry ID | legacy source / line / SHA | port source / line / SHA | visual symptom | mechanism | evidence | test | expected result | status | audit blind spot`. Retain negative controls, not just confirmed failures.

## Scope and coverage

**The original user request covers ten areas**: (1) Creative tab order and extra/non-legacy items; (2) item colors and every item display transform; (3) untranslated/technical item names; (4) armor and Curios geometry; (5) non-full-cube blocks, collision, selection, and occlusion; (6) GUI, HUD, and focus research icons; (7) installed seals not visible; (8) Stabilizing Inlay rendered as a black plane; (9) missing aspects on legacy item variants; and (10) pervasive entity texture/model problems.

Subsequently reported cases include the Grappler icon, Thaumaturge/Darkseer dye icons, Sanity/Warp meter and black outlines, the Hungry Chest looking like plain wooden planks, and possible Knowledge Gain HUD overdraw. The requested work is **investigation and evidence gathering first**; changes to auditors, mod code, CI, or gameplay systems come only after the discovery phase and separate approval.

### Snapshot and numerical coverage

| Inspection or sample | Recorded size | Interpretation |
| --- | ---: | --- |
| First-pass item JSON models | 533 | Not all perspectives, variants, or runtime states have been validated |
| First-pass block JSON models | 264 | A separate alpha scan used 253 eligible models |
| Blockstate JSON files | 227 | Existence alone cannot prove parity |
| Working-module Java files in initial count | 735 | A breadth count, not individual behavior coverage |
| Registered blocks | 175 | Historical count; must be recounted on future SHAs |
| Item registrations/factory declarations | Approximately 466 | Not equivalent to Creative item-stack variants |
| Primary Java block classes | 59 | Historical first-pass count |
| Renderer classes in early inventory | 33 | Later renderer registration enumeration is separate |
| Alpha/model candidate funnel | 98 → 77 → 37 | Progressive filters; these are **not counts of defects** |
| Legacy layer contracts in 37 candidates | 18 confirmed, 19 without proven *layer* mismatch | All 37 still require checks of other render mechanisms |
| Legacy Creative inventory screenshots | 10 | All stored in the repo; slot-by-slot comparison unfinished |
| Active special-armor item IDs lacking legacy custom model | 16 | Runtime rendering on player/armor stand remains unverified |
| Entity-renderer registrations | 43 modern vs 43 legacy | Nine modern invisible renderer cases classified separately |
| GUI source files in focused scan | 25 | 18 contain direct blits; runtime GPU-state testing remains open |

Original sources: `01_original_jar/`, `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/`, `04_api_reference/`. Modern implementation: `05_neoforge_port/`. Comparison materials: `07_Test_Instance_and_Comparisons/`. The existing audits and source manifests are valuable structural checks, **not evidence of pixel-perfect parity**.

## Initial findings: D01–D13

These identifiers originated in the first checkpoint (Appendix A). This table provides an English working summary; newer N/F entries supersede any old uncertainty only for the particular mechanism tested.

| ID | Finding / code evidence | Verified scope and unresolved work |
| --- | --- | --- |
| D01 | `grapple_gun.json` selects an override using `type`; `TCItemProperties` registers `type` for Primordial Pearl, not the active Grappler. Original `ItemGrappleGun` maintained `loaded` state. | Missing property and lost state transitions confirmed by code. Confirm all inventory/icon states and the rope entity separately. |
| D02 | Six Cloth/Void Robe item colors return `0xRRGGBB` (`DyedItemColor` and `WHITE=0xFFFFFF`), whereas NeoForge 1.21.1 item tint uses ARGB. | Code mismatch confirmed; affected client render paths still need visual tests. Different layer orders of Cloth and Voidseer also existed in the original and are **not a regression**. |
| D03 | Candles, crystal items, and several dye/foliage color handlers pass 24-bit RGB, while Nitor and aspect/jar code already uses opaque ARGB. | Inspect the actual tinted layer and each active consumer; do not assume every pixel becomes black. |
| D04 | Legacy `ColorHandler` provided dynamic color for Inlay, Stabilizer, filtered tube, focus items, caster focus, golem placer, and potentially banners; some equivalents are missing from modern BlockColor/ItemColor registration. | Concrete missing branches exist; classify each active ID, JSON `tintindex`, property, and gameplay value. |
| D05 | Modern `TCInstrumentHud` replaces textured Vis/Flux and Warp fills with `GuiGraphics.fill`; draw order, scaling, coordinates, and max-Warp indicator differ from `HudHandler`. | Multiple static divergences confirmed; the **thick black border cause is still unknown**. |
| D06 | Four standalone blueprint item registrations appear in modern `TCItems` and `TCCreativeTabOrder` without legacy counterparts in inspected original registrations. | Port additions confirmed in those source paths; full ten-image Creative ordering is not yet checked. |
| D07 | `SealEntity.syncToClient(Level)` is effectively a stub; no verified modern equivalent of legacy `RenderEventHandler.drawSeals`. | Client display/sync is incomplete by code; persistence and network scope require tests. |
| D08 | Several complex blocks/models have unverified or mismatched `display` transforms and `VoxelShape` behavior. | Do not use `noOcclusion()` or model complexity as a proxy for collision or selection parity. |
| D09 | Guardian/Warden/Crab/Golem use simplified mesh pipelines; Grapple, Focus Mine, Golem Dart and Mind Spider may lose visible geometry through invisible renderer. | Later checked against 43 entity bindings: four definite mesh/visual paths, one FX-only question, four negative controls among nine invisible cases. |
| D10 | Custom legacy armor geometry and some Curios rendering are not recreated by standard armor textures and vanilla models. | N01 identifies 16 affected active armor item IDs; normal Cloth Robes are **not** automatically in those 16. |
| D11 | Aspect tooltip requires a compatible screen, Shift, non-empty stack and non-empty aspect data. Historical aspect comparison: 1,798 legacy, 1,987 port, 1,139 mapped and matching, 398 legacy without reliable mapping. | Full active legacy ItemStack/metadata coverage and data provenance remain open. |
| D12 | Initial alpha scan reported 98 textured models, 77 candidates without a suitable explicit layer, then 37 after tighter UV/legacy filters. | N05 resolves only legacy layer classification: 18 with contracts lost/changed; 19 with no proven layer mismatch. |
| D13 | Translation keys, Creative aliases and alternate registry names (for example `pattern_crafter` vs `patterncrafter`) require active-ID classification. | Exhaustive language/Creative catalog still open; do not treat archived aliases as active bugs. |

## Recovered first-pass details: H01–H10

These findings were present in the original research conversation but not all in the short checkpoint summary. **`historical_find` indicates a preserved first-pass result, not a fresh exhaustive verification of the active version.**

| ID | First-pass technical detail | Follow-up / limitations |
| --- | --- | --- |
| H01 | `TCCreativeTabOrder` reportedly added `VIS_GENERATOR` twice (lines 300 and 352) and `ARCANE_EAR_TOGGLE` twice (360 and 740). Modern `deepslate_amber_ore`, `deepslate_cinnabar_ore` and `deepslate_quartz_ore` did not exist in Minecraft 1.12.2. Compatibility placeholders and the documented policy on hidden unfinished items may conflict. | Duplicate additions in code do not alone prove duplicated displayed stacks. Reconstruct the actual legacy order, metadata variants, and modern tab output. |
| H02 | `thaumatorium.json` had only a GUI-specific transform; `condenser_lattice` and `condenser_lattice_dirty` configured GUI/fixed but lacked dedicated hand transforms. 142 item models were linked directly to block or complex-loader geometry; 126 had no own `display` block. | Missing local transforms may be inherited; this is a screening list, **not 126 proven rotation bugs**. Compare effective transforms in all first/third-person, ground, GUI, frame, and fixed contexts. |
| H03 | Missing or incomplete color registrations were reported for caster focus, `focus_1/2/3`, golem placer, filtered tube, Inlay/Stabilizer, and possibly banners. Crystals, phials, aspect containers and Nitor have at least some existing modern handlers. | Classify each active registry/object and tint layer; F03/F08 contain more reliable current details. |
| H04 | First-pass missing-name candidates: `golem`, `vis_battery`, `condenser_lattice`, `condenser_lattice_dirty`, `pattern_crafter`, `potion_sprayer`, `grapple_gun`, `grapple_gun_tip` and `grapple_gun_spool`. Technical placeholders `placeholder_bars`, `placeholder_anvil`, `placeholder_cauldron`, `placeholder_table` and `hole` need separate policies. A legacy `ru_ru.lang` is not a modern `ru_ru.json`. | Check exact effective translation keys of **active** items in current `en_us.json` and separate internationalization coverage from fallback English names. |
| H05 | `CuriosClientCompat` was observed to register only `CURIOSITY_BAND` and `GOGGLES` special renderers. `HeadCurioRenderer` drew a flat textured surface. Legacy special armor geometry (robes, fortress, cultist plate/leader) was not connected. | Reinspect active Curios integration, wearer-specific models, Goggles, Traveller's Boots, and all relevant armor states. |
| H06 | Complex-shape candidates: Arcane Workbench, Arcane Workbench Charger, Golem Builder, Stabilizer, Pattern Crafter. Several had geometry not reflected by a dedicated shape function. | Evaluate selection, collision, occlusion and raycast independently; some legacy blocks deliberately had full-cube selection. N12–N16 isolate confirmed semantics. |
| H07 | GUI review candidates: Smelter, Golem Builder, Focal Manipulator, Research Table, Thaumonomicon Browser and Entry. Legacy Focus Medium/Effect icons already used -90° rotation and square background images `textures/foci/_medium.png` and `_effect.png`. | N10 rejects claims that rotation or square backing were invented by the port. Compare actual framebuffer orientation, scale, and transparency. |
| H08 | Installed seals lacked a completed `SealEntity.syncToClient` path and a verified `drawSeals` replacement, even with a bell/`ISealDisplayer`. Server map persistence was also questioned. | Server → client → overlay and saved-data behavior still require validation. |
| H09 | `TCAspectTooltipEvents` enforces screen/Shift/stack/data conditions; `TCAspectAssignments` combines explicit mappings with a generated recipe-oriented cache. | Compare all legacy item variants, including non-craftable items, under equivalent tooltip settings. |
| H10 | Entity candidates beyond Guardian/Warden/Crab/Golem/Mind Spider: Cultist Knight/Cleric, Brainy/Inhabited Zombie, Firebat, Pech, Taintacle and Taint Seed. | Some newer custom models **are** substantial; do not declare every special mob a vanilla fallback. |

### Audit blind spots already established

- `creative_tabs.ps1` mainly checked existence of IDs, not legacy order, duplicates, extra items, hidden entries, and metadata variants.
- `visual_model_transforms.ps1` accepted some effective transform presence without requiring each display context to match the original.
- `texture_color_parity.ps1` did not prove complete 1.21.1 ARGB/tint handler behavior and active `tintindex` connections.
- `legacy_shape_parity.ps1` could treat complex model metadata as a sign of a valid shape without verifying the actual selection/collision function.
- `visual_equivalence_completion.ps1` primarily produced a report; review warnings did not necessarily fail builds.
- `lint-assets.py`, startup/client smoke tests, and green CI verify useful structural/functional properties, **not on-screen pixel parity**.

**Do not modify these audits until the investigation and coverage ledger are sufficiently complete.**

## Follow-up investigation: N01–N16

The entries below summarize the more rigorous November 3 analysis in English. The complete original evidence, source references, intermediate exclusions, and numeric tables remain in Appendix B.

### N01 — Missing legacy custom armor geometry for 16 active item IDs

**Status:** `confirmed_code_model_path_loss`; exact in-game UV/animation defects require client inspection.

Legacy armor classes selected `ModelRobe`, `ModelKnightArmor`, `ModelLeaderArmor` and `ModelFortressArmor` through custom armor-model APIs. Modern corresponding items are built around standard armor geometry, without an equivalent client armor model registration. `TCClientExtensions` only registers fluid extensions, not humanoid armor model replacements.

| Legacy model | Active modern item IDs | Count |
| --- | --- | ---: |
| `ModelKnightArmor` | `crimson_plate_helm`, `crimson_plate_chest`, `crimson_plate_legs` | 3 |
| `ModelRobe` (cultist) | `crimson_robe_helm`, `crimson_robe_chest`, `crimson_robe_legs` | 3 |
| `ModelLeaderArmor` | `crimson_praetor_helm`, `crimson_praetor_chest`, `crimson_praetor_legs` | 3 |
| `ModelFortressArmor` | `thaumium_fortress_helm`, `thaumium_fortress_helm_goggles`, `thaumium_fortress_chest`, `thaumium_fortress_legs` | 4 |
| `ModelRobe` (void) | `void_robe_helm`, `void_robe_chest`, `void_robe_legs` | 3 |
| **Total** | **16 active IDs** | **16** |

A set of 26 modern armor textures exists, but textures and `ArmorMaterial.Layer` do not recreate custom mesh UVs. Legacy ordinary Thaumaturge Cloth did not use the same custom `getArmorModel` method; exclude it from this 16-ID finding, while still testing its item color.

Evidence: [modern item registrations](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java), [armor material definitions](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCArmorMaterials.java), [client extensions](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientExtensions.java). Client test: each item on player and armor stand, front/back, walking/crouching, inner and outer layers, plus inventory icon.

### N02 — Inhabited Zombie wears the wrong armor family

**Status:** `confirmed_code_behavior_mismatch`.

Original `EntityInhabitedZombie.onInitialSpawn` equipped `crimsonPlateHelm`, optionally Crimson Plate chest and leggings according to difficulty. Modern `TCInhabitedZombieEntity.populateLegacyArmor` instead equips `VOID_HELM`, `VOID_CHEST` and `VOID_LEGS`. This changes silhouette and potentially armor behavior. Independently, the original `ItemCultistPlateArmor.getArmorTexture` used special `zombie_plate_armor` texture for inhabited zombies, whereas modern `ArmorMaterial.Layer` selection is static. Treat wrong equipment and missing entity-specific armor texture as separate issues.

[Legacy entity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/monster/EntityInhabitedZombie.java) · [modern entity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCInhabitedZombieEntity.java).

### N03 — Cultist Cleric ritual hover and altar tether are absent

**Status:** `confirmed_renderer_feature_absence`; home-position synchronization requires validation.

Legacy `RenderCultist` applied vertical sinusoidal bobbing and animated translucent wispy beam toward the ritual altar/home location when a cleric was a ritualist. Modern `TCCultistRenderer` does not reproduce the bobbing/tether, even though the modern entity retains a synced `RITUALIST` flag. **Negative control:** an unused `drawFloatyLine` helper in legacy `RenderCultistLeader` is not evidence that leaders always displayed the same effect.

[Legacy renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/mob/RenderCultist.java) · [modern renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCCultistRenderer.java).

### N04 — Giant Brainy Zombie anger tint is missing

**Status:** `confirmed_renderer_behavior_mismatch`.

Legacy `RenderBrainyZombie.preRenderScale` both scaled the giant as `1 + anger` and shifted its color toward red via `glColor3f(1, 1-q, 1-q)` where `q = min(1, anger)/2`. Modern `TCBrainyZombieRenderer.scale` retains size scaling but not the tint. Compare calm, intermediate and maximum anger frames. **Negative control:** a declared legacy zombie-villager texture was not actually returned by the old renderer, so absence of its switch alone is not a demonstrated regression.

[Legacy renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/mob/RenderBrainyZombie.java) · [modern renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBrainyZombieRenderer.java).

### N05 — Completed static legacy render-layer classification of the 37 alpha/solid candidates

**Status:** `layer_classification_completed` for the specific legacy layer contract; `runtime_pixel_parity_open`.

Of the 37 narrowed model candidates, 18 demonstrably lost or changed an explicit legacy render-layer rule:

| Family | Count | Legacy rule |
| --- | ---: | --- |
| `inlay_*` | 9 | Legacy CUTOUT plus TRANSLUCENT support |
| `taint_fibre` / `taint_growth` | 4 | Legacy-specific alpha/cutout contract |
| `taint_geyser` | 1 | Legacy-specific alpha contract |
| Amber blocks | 2 | Legacy TRANSLUCENT |
| Stabilizer | 1 | Legacy CUTOUT |
| Infernal Furnace | 1 | Legacy-specific render-layer semantics |
| **Total** | **18** | **Static layer regression evidenced** |

The remaining **19/37** had no special `getBlockLayer`/`canRenderInLayer` in the inspected legacy implementations or their parent chain, so missing explicit modern `render_type` **alone** does not establish a legacy layer regression:

| Remaining model family | Number |
| --- | ---: |
| `lamp_arcane_on`, `lamp_growth_on`, `lamp_fertility_on` | 3 |
| `arcane_ear_off/on` and `arcane_ear_toggle_off/on` | 4 |
| `recharge_pedestal` | 1 |
| `condenser`, `condenser_off` | 2 |
| `vis_generator` | 1 |
| `void_siphon` | 1 |
| `smelter_vent` | 1 |
| `alembic` | 1 |
| `pillar_ancient`, `pillar_eldritch`, `pillar_arcane` | 3 |
| `metal_alchemical_advanced` | 1 |
| `candle` | 1 |
| **Total** | **19** |

**Important:** these 19 were cleared only of this specific **legacy-layer** accusation; geometry, material, texture alpha, UVs, BE renderer, tint and client appearance remain unchecked. For example `condenser` has non-full-volume model elements and a full modern shape, but its legacy block also did not override bounding box; it may be awkward UX but not a proven legacy collision regression.

[NeoForge model render-type rules](https://docs.neoforged.net/docs/1.21.1/resources/client/models/).

### N06 — Four stand-alone blueprint item registrations have no verified legacy counterpart

**Status:** `confirmed_port_extra_item_registration` for inspected sources.

Full passes over original `ConfigItems.java`, `ConfigBlocks.java`, `ItemsTC.java` and `CreativeTabThaumcraft.java` did not find stand-alone items `golem_press_blueprint`, `infusion_altar_blueprint`, `infusion_altar_ancient_blueprint` and `infusion_altar_eldritch_blueprint`; modern `TCItems` and `TCCreativeTabOrder` do register and show them. Do **not** confuse Thaumonomicon research blueprints/pages with independently registered inventory items. Complete inventory order still requires the ten legacy Creative screenshot comparisons.

### N07 — HUD texture size is not the generic UV culprit

**Status:** `negative_control_confirmed`.

The original `hud.png` used by `TCInstrumentHud` is **256×256**, matching the implicit 256×256 dimensions of the employed `GuiGraphics.blit` overload. The copied legacy and modern HUD image bytes also matched. The thick outline cannot be explained by simply assuming the PNG has a different native size. UV selection, repeated framing, draw order, blend state, atlas filtering, and GUI scale remain open.

[Official NeoForge GUI documentation](https://docs.neoforged.net/docs/1.21.1/gui/screens/).

### N08 — Basic GUI texture dimensions checked across 18 screens

**Status:** `negative_control_confirmed` for the inspected image dimensions.

The conventional textures passed to 256×256 GUI blits had the expected dimensions. `gui_researchbook.png` and `gui_researchbook_overlay.png` are **512×512**, but these sizes and blobs match the original assets; legacy `GuiResearchPage` already used 256-coordinate drawing and modern `TCThaumonomiconEntryScreen` retains a legacy 256 coordinate constant. A physical 512×512 image used with 256 UV coordinates is **not by itself a new port regression**. Runtime sampling/UV, blend, z-order and scale still need inspection.

[Legacy research page](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/gui/GuiResearchPage.java) · [modern entry screen](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCThaumonomiconEntryScreen.java).

### N09 — Research Table draws Knowledge Gain HUD repeatedly

**Status:** `confirmed_repeated_render_call`; intensity and frame count `requires_client`.

`TCResearchTableScreen.render` calls `TCKnowledgeGainHud.renderOverScreen` **twice** in adjacent lines (~165–166). `renderTooltip` contains the same consecutive pair (~191–192). The HUD tick gate limits simulation update, **not drawing**; overlay graphics may therefore be layered multiple times while `kgFade > 0`. The global Knowledge Gain layer intentionally skips the Research Table screen, but that does not remove these local duplicates. This should **not be conflated** with the Vis/Sanity meter border.

[Modern Research Table screen](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java) · [Knowledge Gain HUD](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java).

### N10 — Thaumonomicon Focus node background and rotation: false-positive controls

**Status:** `negative_control_confirmed` for the existence of backing frames and -90° rotation; framebuffer appearance still open.

Legacy `GuiResearchBrowser` rendered a basic node backing, with square/round/hex variants and hidden/spiky decoration. Legacy `GuiFocalManipulator.drawPart` applied a -90° effective Z rotation; modern `TCResearchIconRenderer.renderFocus` also applies `Axis.ZP.rotationDegrees(-90)`. Therefore **the mere presence of a square background or a 90-degree rotation is not evidence of a port-only bug**. The user-observed sideways appearance could still arise from a different modern rendering coordinate system, model/texture orientation, tint, or alpha. Compare exact `_medium.png` and `_effect.png` regions.

[Legacy browser](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java) · [modern icon renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchIconRenderer.java).

### N11 — Quantified Vis/Flux/Sanity HUD coordinate and algorithm differences

**Status:** `confirmed_static_coordinate_and_behavior_mismatch`; in-game pixels still require capture.

| Element | Legacy (GUI pixels) | Modern port | Difference |
| --- | --- | --- | --- |
| Thaumometer frame origin | `(3,1)` | `(2,2)` | X -1, Y +1 |
| Vis top at normalized `f` | `74 − 64f` | `76 − round(64f)` | Approximately 2 px downward |
| Base marker, normalized `b` | `(4,72 − 64b)`, `b = base/525` | `(3,74 − round(64b))` | X -1, Y about +2 |
| Sanity frame, alone | `(1,1)` | `(1,2)` | Y +1 |
| Sanity frame after Thaumometer | `(1,81)` | `(1,84)` | Y +3 |
| Sanity fill left edge | X=7 | X=8 | X +1 |

Separately, legacy Vis/Flux normalization uses a fixed **525**, includes proportional combined-fill reduction, textured animated fill, and a special icon for maximum Warp. The modern code normalizes to `aura.base()`, draws solid ARGB-filled rectangles and lacks that max-Warp icon. With Vis=200 and base=200, for example, legacy Vis would use roughly 24/64 pixels while the modern formula can fill the entire 64 pixels. The aura overlay also has a potentially changed activation condition (held Thaumometer **or** Goggles, vs legacy hand/focus checks). Whether intentional or not, record this delta.

[Legacy `HudHandler`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/lib/events/HudHandler.java) · [modern `TCInstrumentHud`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCInstrumentHud.java).

### N12 — Arcane Ear/Toggle hitbox dimensions differ

**Status:** `confirmed_voxel_shape_mismatch`.

Legacy Arcane Ear uses a **12×6×12** orientation-specific AABB (example UP: `[2,0,2]..[14,6,14]`; NORTH: `[2,2,10]..[14,14,16]`). The modern `TCArcaneEarBlock` uses **10×7×10** (UP: `[3,0,3]..[13,7,13]`; NORTH: `[3,3,9]..[13,13,16]`). The modern block is 2 px narrower in each transverse dimension and 1 px thicker along its facing. Toggle inherits the base shape in both versions. This is selection/collision behavior, **not proof of a black texture edge**.

[Legacy Arcane Ear](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockArcaneEar.java) · [modern block](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCArcaneEarBlock.java).

### N13 — Pattern Crafter loses sub-part-specific hit testing

**Status:** `confirmed_interaction_semantics_loss`.

Legacy `BlockPatternCrafter` used `RayTracer.retraceBlock`, `IndexedCuboid6` and `subHit` to select a precise component and only call `TilePatternCrafter.cycle()` when `subHit==0`. Modern `TCPatternCrafterBlock.useWithoutItem` cycles on any use and no equivalent subHit path is present in the class. **Negative control:** the legacy block's **general** bounding box was `FULL_BLOCK_AABB`; calling a full cube alone a legacy regression would be wrong. The proven loss is per-component selection and action gating.

[Legacy Pattern Crafter](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/crafting/BlockPatternCrafter.java) · [modern Pattern Crafter](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCPatternCrafterBlock.java).

### N14 — Normal/Void Warded Jar shape is 2/16 block too tall

**Status:** `confirmed_voxel_shape_mismatch`; transparent render layer verified as **not the problem** for these two models.

Legacy `BlockJar` AABB: `(3,0,3)..(13,12,13)`. Modern `TCWardedJarBlock.SHAPE`: `(3,0,3)..(13,14,13)`. The same class backs active `JAR_NORMAL` and `JAR_VOID`. Their modern JSON models correctly specify `"render_type":"minecraft:translucent"` and elements extend only to Y=12, consistent with legacy `TRANSLUCENT`. `TCBrainJarBlock` has the expected Y=12 height, another valuable control.

[Legacy Jar](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java) · [modern Warded Jar](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java).

### N15 — Stabilizing Inlay selection ignores connections

**Status:** `confirmed_voxel_shape_mismatch`.

Legacy `BlockInlay.getBoundingBox` selected from **16 connection-dependent `REDSTONE_WIRE_AABB`** boxes: isolated shape `(3,0,3)..(13,1,13)` expands along connected sides up to `(0,0,0)..(16,1,16)`. Modern `TCInlayBlock.getShape` always returns the full thin plane `(0,0,0)..(16,1,16)` even when isolated, despite preserving `NORTH/EAST/SOUTH/WEST` connection properties. Both implementations have empty solid collision; the difference is selection/raycast. This finding is separate from the black-plane material problem or missing dynamic tint.

[Legacy Inlay](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockInlay.java) · [modern Inlay](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCInlayBlock.java).

Additional non-regression controls: modern Lamp shape matches legacy; Infusion Pillar retains a shorter selection and two-block-high collision; Void Siphon composite AABB matches legacy; Candle keeps its shape/non-collision; Condenser has a full box in both versions unless proven otherwise.

### N16 — Six blocks changed selection/collision semantics relative to legacy full cubes

**Status:** `confirmed_static_geometry_behavior_delta`; whether acceptable modernization requires an explicit strict-parity decision.

| Block | Legacy selection/collision | Modern selection/collision |
| --- | --- | --- |
| `crucible` | Full-cube selection; hollow base-and-walls collision | Hollow model-following shape now used for selection and default collision, removing full-cube ray hit in empty middle |
| `bellows` | Full/full by default | Core plus direction-dependent output; tighter selection and collision |
| `centrifuge` | Full/full | Composite narrow body AABB, excluding corners |
| `infusion_matrix` | Full/full | Cross-shaped composite boxes |
| `table` | Full/full | Tabletop and leg shapes, gaps are non-colliding |
| `research_table` | Full/full | Tabletop and legs, as above |

These are **behavior differences**, not six automatically proven bugs. The port may intentionally improve selection accuracy. The strict original behavior criterion must be applied consciously. For Crucible specifically, legacy **selection** and **collision** were deliberately different; collapsing them into one shape loses that distinction.

[Legacy Crucible](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/crafting/BlockCrucible.java) · [modern Crucible](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCCrucibleBlock.java).


## Additional source-history checks: F01–F39

These findings were collected after the N16 checkpoint, including commit-level research into the earlier Research Table border fix. They supplement, rather than erase, the older D/N observations.

### F01 — Previous Research Table black-border fix: blend state is a credible HUD lead

**Status:** `confirmed_code` for Research Table; `probable_cause` only for the superficially similar HUD outline.

- [Commit `a0095771`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/commit/a0095771d3127bdab457cb3e2add5a14961a8f34) explicitly addressed Research Table GUI alpha blending and added its category panel.
- [Commit `d39be45`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/commit/d39be45db1b6d0c577bd85f78fc29c2b269b6a0d) changed `TCResearchTableScreen.blitGui` from `guiGraphics.blit(RenderType::guiTextured, ...)` to `RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); guiGraphics.blit(...)`.
- [Commit `ddc8c5a`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/commit/ddc8c5acf1a76e26456d0f4d31a7386cb3036f79) removed duplicated Knowledge drawing inside **`TCThaumonomiconEntryScreen`**, not Research Table. N09 identifies other repetitions **inside** Research Table.
- [Modern `TCInstrumentHud`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCInstrumentHud.java) does not contain that same explicit `enableBlend/defaultBlendFunc` pair. However vanilla `GuiGraphics` may already establish blending: absence of explicit calls is **not proof**.
- Original and modern `hud.png` were found identical and its actual 256×256 dimensions agree with the source coordinates. Blend state, UV samples, overlapping frame quads and draw order remain plausible alternatives.

**Controlled experiment required:** capture blend state and actual draw sequence while rendering Vis/Flux/Sanity; compare exact border pixels with and without the compatible blending configuration in an isolated test. Do not announce a common root cause yet.

### F02 — Direct blit usage and explicit blending across GUI sources

**Status:** `confirmed_code_inventory`; `requires_client` for actual corruption.

Focused scan: **25 GUI Java files**; **18** with direct blit usage. Of these 18, **13** contain explicit `RenderSystem.enableBlend`, while **five** do not: `ClientAspectTooltipComponent.java`, `TCFocusPouchScreen.java`, `TCInstrumentHud.java`, `TCTurretAdvancedScreen.java` and `TCTurretBasicScreen.java`.

These are inspection candidates **not five proven bugs**, since the surrounding vanilla GUI pipeline may provide blending. `ClientAspectTooltipComponent.renderAmount` intentionally draws four black displaced copies of a numeric label to create a text outline. This is **not** the same as the unwanted thick frame around a HUD meter.

### F03 — Tint-index inventory and 24-bit RGB color registrations

**Status:** `confirmed_code` for specified values and model/index wiring; `requires_client` for rendered pixels.

Direct JSON search located **21 modern model files** with explicit `"tintindex"`, including `block/candle`, `inlay_*`, `stabilizer`, `tube_filter_core` (six faces using index 1), `jar_normal/void_25/50/75/100`, `taint_growth_*`, `condenser_lattice_side`, and `item/caster_gauntlet_focus` (15 tintable faces). A tint index is only useful if the active renderer supplies the corresponding color.

Six active Cloth/Void Robe item color handlers — `cloth_boots/legs/chest` and `void_robe_legs/chest/helm` — return 24-bit `DyedItemColor.getOrDefault` RGB for layer 0 and `WHITE=0xFFFFFF` for other layers. Legacy default dye 6961280 is `0x6A3880`. NeoForge 1.21.1 uses ARGB color values; the usual opaque form is `0xFFRRGGBB`. The order of the relevant item texture layers already differs **in the original** and matches the port, so layer-order swapping is not supported as a regression.

Likewise, 16 candle tint constants and seven crystal item tint values are RGB; their model layers have tint consumers. Foliage handlers derive RGB from `FoliageColor`/`BiomeColors`. These findings demand an active-processor/runtime matrix, especially for world objects and OBJ models. **Negative controls:** Nitor item colors and aspect/jar tint paths already use opaque ARGB; do not label every color handler broken.

[Modern `TCColorHandlers`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java).

### F04 — Enumerated item-model overrides and the Grappler state bug

**Status:** `confirmed_code` for property/model mismatches.

Seven modern item JSON files contain direct `overrides`. Active `jar_normal`/`jar_void` use `thaumcraft:fill`; `caster_basic` uses `thaumcraft:focus`; `primordial_pearl` uses `thaumcraft:type`. Those properties are registered. Active `grapple_gun` instead uses **unnamespaced** `type` without the required active property registration. Moreover, `TCGrappleGunItem` does not set/reset the original `loaded` state when the fired grapple exists or disappears. Both model property and runtime state mechanism must be checked together.

`caster_advanced.json` and `verdant_charm.json` retain historical predicates but lack verified active same-name registrations. Do not count them as player-visible defects without a reachable item path.

[Modern Grappler item](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/tools/TCGrappleGunItem.java) · [item-property registration](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCItemProperties.java).

### F05 — Entity renderer inventory: nine invisible renderers are not nine failures

**Status:** `confirmed_code_classification`, per-entity client FX remains open.

Among 43 modern entity-renderer registrations, nine use `TCInvisibleEntityRenderer`. Direct legacy comparison yielded:

| Modern entity | Original rendering | Classification |
| --- | --- | --- |
| `GRAPPLE` | Visible hook/rope | Lost visible geometry/effects; `CRIT` particles alone do not recreate the original |
| `FOCUSMINE` | Visible mine element | Lost model/effect; `WITCH` particles are insufficient |
| `GOLEM_DART` | Visible projectile | Lost visible mesh/effect |
| `MIND_SPIDER` | Conditional, highly translucent spider: legacy `RenderMindSpider` considered viewer and used approximately 0.1 alpha and 0.6 scale | Lost viewer-conditional geometry. Modern synced `VIEWER` data survives, but renderer is invisible |
| `FOCUS_PROJECTILE` | Legacy `RenderNoProjectile` without mesh but with explicit `renderParticle` call | Compare specific particle behavior; a mesh is not mandatory |
| `FOCUS_CLOUD` | No legacy mesh | Geometry negative control |
| `TAINT_SWARM` | No legacy mesh | Geometry negative control |
| `ALUMENTUM` | Legacy `RenderNoProjectile` | Geometry negative control |
| `CAUSALITY_COLLAPSER` | Legacy `RenderNoProjectile` | Geometry negative control |

The first four represent actual visible-model/FX losses by code; one requires particle-specific testing; the remaining four cannot be called missing meshes merely because their renderers are empty.

### F06 — Specialized mob geometry and Special Item glow

**Status:** `confirmed_renderer_model_difference`; client screenshots and reachability remain open.

- **Eldritch Guardian/Warden:** original `ModelEldritchGuardian` used 128×64 UV layout, segmented cloak, belts and armor; modern `HumanoidModel(ModelLayers.PLAYER)` assumes vanilla 64×64 geometry.
- **Eldritch Crab:** original `ModelEldritchCrab` used 128×64 UV layout with claws/tail/armor; modern `SpiderModel(ModelLayers.SPIDER)`.
- **Thaumcraft Golem:** original `golem_base.obj` plus modular `PartModel` attachments; modern vanilla `HumanoidModel(ModelLayers.PLAYER)`, scaled and texture-switched.
- **Negative controls:** Taint Crawler used vanilla Silverfish geometry in both versions. Modern `TCEldritchGolemModel` and `TCPechRenderer` contain meaningful custom geometry; do not falsely group them with vanilla substitutions.
- **Additional Special Item difference:** original `RenderSpecialItem` rendered glowing, rotating magenta-white additive rays (roughly 5–10 quads depending on graphics settings), followed by the dropped item. Modern `SPECIAL_ITEM` has plain `ItemEntityRenderer`. Whether `TCSpecialItemEntity` can actually spawn in the current gameplay remains unverified; therefore record the code delta without claiming a demonstrated player-visible loss.

### F07 — Hungry Chest observation and complex block-entity visual candidates

**Status:** Hungry Chest `user_observed_requires_code_and_client_confirmation`; remaining items are preliminary review candidates.

The user reports that **Hungry Chest renders like a plain block of wooden planks**. The earlier inspection suggested a simplified model and an incomplete block-entity visual path, but a complete `blockstate → model → BER → animation → dynamic state` proof has **not yet been documented**. Test the placed block and inventory model, front and back, open/closed behavior, item intake, animation, lighting and outline.

Preserve these other cases in the work queue, distinguishing evidence already recorded in D/N from purely historical leads:

| Group | Visual behavior to compare |
| --- | --- |
| Brain Jar | Container mesh, contents, labels and animation |
| Hole | Shader/material and hole appearance |
| Dioptra | Dynamic indicator/grid |
| Mirrors | Tunnel/portal rendering |
| Focal Manipulator | Rotating focus crystals and GUI |
| Centrifuge | Rotor and animation |
| Void Siphon | Portal visual effect |
| Pattern Crafter | Per-part hit testing and icons (N13 already proves interaction loss) |
| Alembic | Text labels and contents |
| Essentia Tubes | Connectors/rods and filter tint |
| Golem Builder | Press and lava animation |
| Warded Jars | Jar shape, contents and labels (N14 proves a shape mismatch) |
| Flux Rift | Geometry and animated effect |
| Cultist Portal | Frame, geometry and UV |
| Arcane Bore | Beam/FX |

This table is **not** a list of fully confirmed defects. The audit must inspect reachability, current model/BER registrations and exact legacy visuals first.

### F08 — Dynamic tints are missing, but missing Inlay tint cannot alone explain a black surface

**Status:** `confirmed_code_registration_loss`.

Legacy `ColorHandler.java` supported dynamic colors for `BlockInlay` (charge 0–15), `BlockStabilizer` (energy), filtered tube (aspect, model tint index 1), `ItemFocus` (`getFocusColor`), caster gauntlet (focus color, index 1) and golem placer (material color). Modern corresponding registrations are absent or incomplete for several active IDs. Inlay and Stabilizer independently also appear among the **18** render-layer mismatches.

Importantly, original Inlay/Stabilizer handlers already produced **opaque** `0xFF...` colors. Inlay at charge 0 yields approximately `0xFF4C4C4C` and at charge 15 `0xFFFFFFFF`. Missing tint would tend to eliminate darkening, **not generate a fully black plane by itself**. The black Inlay case must therefore test cutout/translucent state, alpha, geometry, blending, and texture/material paths separately.

### F09 — Explicit false-positive exclusions

1. Copied HUD and nine selected alpha-related image assets matched the original byte-for-byte: these files were **not corrupted during copying**. Correct assets do not guarantee correct renderer behavior.
2. `hud.png` dimensions and the inspected basic GUI texture sizes do not support a universal source-size mismatch.
3. The two Sanity rendering phases are intentional in legacy; that does not validate modern offsets and fill.
4. The different Cloth vs Voidseer texture-layer orders existed in legacy.
5. A -90° focus rotation and square focus-research backing existed in legacy, though on-screen orientation remains open.
6. Nine invisible entity renderers do not all imply missing geometry: see F05.
7. The 19 alpha candidates with no proven **legacy-layer** change are not cleared of other visual problems.
8. No broken **direct** internal JSON parent/texture/override model reference was found in the selected resource integrity scan; indirect OBJ/MTL, baked model geometry and runtime effects remain outside that control.
9. Successful build/startup, asset linting, and parity report scores cannot establish 100% visual equivalence.


## New renderer-path investigation: F10–F18 (November 3, 2026)

This section continues the investigation **after F09**. It compares the registered legacy tile-entity special renderers (TESRs) to modern block-entity renderer (BER) bindings and checks the actual models/state, rather than treating a missing Java class name as proof of a missing on-screen feature.

### F10 — Registered legacy TESR → modern BER coverage, with active-ID exclusions

**Status:** `confirmed_static_registration_delta`; client-rendering parity remains open.

A complete enumeration of [legacy `ProxyTESR.setupTESR`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/proxies/ProxyTESR.java) yields **23** renderer registrations (22 renderer instances/classes because the same Mirror TESR was used for two tile classes). The only modern call site for `registerBlockEntityRenderer` found under `05_neoforge_port/` is [`TCBlockEntityRenderers.java`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java), containing **10** modern BER registrations. The modern [`TCBlockEntities` registry](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java) has **45** registered block-entity type names. These are distinct counting universes, not a direct "23 minus 10 bugs" result.

| Legacy TESR registration | Modern rendering path at this SHA | Classification |
| --- | --- | --- |
| Crucible | `TCCrucibleRenderer` | Mapped; parity still requires visual review |
| Infusion Pedestal | `TCInfusionPedestalRenderer` | Mapped |
| Recharge Pedestal | Shares modern `INFUSION_PEDESTAL` block-entity type/renderer | **Covered through a shared path**, not automatically missing |
| Tube Valve | `TCTubeValveRenderer` | Mapped |
| Warded Jar | `TCWardedJarRenderer` for normal and void jar registrations | Mapped; separate jar shape issue N14 persists |
| Bellows | `TCBellowsRenderer` | Mapped |
| Infusion Matrix | `TCInfusionMatrixRenderer` | Mapped |
| Research Table | `TCResearchTableRenderer` | Mapped; GUI overdraw N09 is separate |
| Thaumatorium | `TCThaumatoriumRenderer` | Mapped |
| Golem Builder | `TCGolemBuilderRenderer` | Mapped |
| Dioptra | No direct BER registration | Active and affected; F12 |
| Focal Manipulator / Wand Workbench | No direct BER registration | Active and affected; F14 |
| Hungry Chest | No direct BER registration | Active; **confirmed wrong visible block model**; F11 |
| Tube Oneway | No direct BER registration | Active; investigate missing rod/pose; F17 |
| Tube Buffer | No direct BER registration | Active; investigate missing rod/pose; F17 |
| Alembic | No direct BER registration | Active; dynamic label/nozzle difference; F17 |
| Centrifuge | No direct BER registration | Active; missing animated rotor path; F13 |
| Item Mirror | No direct BER registration | Active; legacy portal tunnel not recreated by static pane; F15 |
| Essentia Mirror | No direct BER registration | Active; same portal renderer gap; F15 |
| Pattern Crafter | No direct BER registration | Active; lost mode/gear overlay; F16 |
| Void Siphon | No direct BER registration | Active; missing animated portal quads; F15 |
| Hole | No direct BER registration; block explicitly `RenderShape.INVISIBLE` | Active; no verified replacement for legacy portal effect; F15 |
| Banner | No matching active banner block/entity type in modern `TCBlocks`/`TCBlockEntities` | **Not an active visual defect on this evidence**; classify separately as legacy content coverage |

**Interpretation:** the original list contains **13 registrations without a direct modern counterpart**, of which **12 have corresponding active modern block-entity types**. One (Banner) cannot be scored as a runtime renderer bug without an active ID. For each of the twelve, assess static model alternatives and other render event/particle paths before calling the full *visual* feature absent.

### F11 — Hungry Chest: confirmed oak-plank block model; preserved chest geometry is orphaned

**Status:** `confirmed_active_block_resource_regression`; lid animation also missing from the registered modern rendering path.

Source chain:

1. Original [`TileHungryChestRenderer.java`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileHungryChestRenderer.java) uses vanilla `ModelChest` geometry, `textures/models/chesthungry.png`, facing-specific rotation, an interpolated easing function on `prevLidAngle/lidAngle`, and `chestLid.rotateAngleX`.
2. Modern [`TCBlocks.HUNGRY_CHEST`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java) is an active registered ID `hungry_chest`. [`TCHungryChestBlock`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCHungryChestBlock.java) returns `RenderShape.MODEL`, while the BER registry has no `HUNGRY_CHEST` binding.
3. Active [`blockstates/hungry_chest.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/hungry_chest.json) selects [`models/block/hungry_chest.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/hungry_chest.json), a **single [1,0,1]–[15,14,15] box** with **`minecraft:block/oak_planks`** on all six faces. Its different facing variants merely rotate this identical texture cube.
4. Active [`models/item/hungry_chest.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/hungry_chest.json) also inherits that plank model.
5. A more faithful but apparently **unused** legacy-named [`models/block/chest.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/chest.json) still exists, featuring a chest body, lid/lock elements, and `thaumcraft:block/chesthungry`. A separate [`models/item/hungrychest.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/hungrychest.json) points to it; there is no corresponding `hungrychest` active block registration. The modern texture directory also contains `chesthungry.png`, so lack of artwork is **not** the blocker.
6. [`TCHungryChestBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCHungryChestBlockEntity.java) retains open/close event tracking and sounds via `ContainerOpenersCounter`, but no lid animation is consumed by a registered BER. The gameplay inventory/eating behavior is separate.

**Root cause supported by the registration/model chain:** the active block points to a plank placeholder and does not use the preserved chest geometry or original lid renderer. This directly explains the reported "block of planks" appearance. Runtime testing remains necessary for precise animation and view comparisons, not for establishing the wrong active model mapping.

### F12 — Dioptra: the 13×13 aura data is computed and synced but never rendered as the legacy height field

**Status:** `confirmed_visual_data_pipeline_gap`; exact GPU/framebuffer parity requires runtime.

Legacy [`TileDioptraRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileDioptraRenderer.java) builds a **12×12 mesh of quads from a 13×13 vertex-height array**, animates color/height, and uses additive blending and side surfaces. Modern [`TCDioptraBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCDioptraBlockEntity.java) still maintains a **169-byte grid**, recalculates values from vis/flux every **20 ticks** and sends them to the client via `getUpdateTag` and `ClientboundBlockEntityDataPacket`. The `getGridAmt()` access point was found only in the block-entity class in the modern source search, with no rendering consumer.

The active [`dioptra.json` blockstate](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/dioptra.json) merely alternates fixed models for enabled and disabled modes. The static [`models/block/dioptra.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/dioptra.json) contains stationary side/top elements; `DIOPTRA` has no BER registration. **The data and network work survive, but the hallmark dynamic field visualization does not have an equivalent rendering path.**

### F13 — Centrifuge: client rotation logic survives, animated rotor rendering does not

**Status:** `confirmed_legacy_animation_render_path_loss`.

Legacy [`TileCentrifugeRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileCentrifugeRenderer.java) renders the fixed body and separately rotates `ModelCentrifuge.renderSpinnyBit()` by the tile's `rotation`. Modern [`TCCentrifugeBlockEntity.clientTick`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCCentrifugeBlockEntity.java) still updates `rotationSpeed` and `rotation` and triggers a pump sound on rotation thresholds. However the active `centrifuge` blockstate references a fixed [`models/block/centrifuge.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/centrifuge.json) and no `CENTRIFUGE` BER exists. The retained rotation state has no visible rotor consumer in that path.

### F14 — Focal Manipulator: missing rotating focus crystals and rendering layers

**Status:** `confirmed_legacy_TESR_path_loss`; particles outside BER still need source/runtime inspection.

Legacy [`TileFocalManipulatorRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileFocalManipulatorRenderer.java) contains tick-based rotating/bobbing geometry and billboards, additive particle/blend passes, multiple transforms and dynamic highlights. The modern active block ID is `wand_workbench` with [`blockstates/wand_workbench.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/wand_workbench.json) pointing to a static model inheriting `arcane_workbench`; [`TCFocalManipulatorBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCFocalManipulatorBlockEntity.java) handles crafting progression but there is no `FOCAL_MANIPULATOR` BER. The original dynamic scene is not reproduced by the static block model. Check for independent client particles before closing the entire visual-effects path.

### F15 — Mirror, Void Siphon and Hole: three related portal/shader-style visual paths

**Status:** `confirmed_special_renderer_absence`; individual framebuffer effects require visual tests.

| Active object | Legacy rendering | Modern block rendering at the inspected SHA |
| --- | --- | --- |
| `mirror` and `mirror_essentia` | Shared ~29 kB `TileMirrorRenderer`; camera-relative portal planes, multi-pass textures, animated transforms, additive blending and apparent tunnel depth | Both active block-entity types lack BER; [`models/block/mirror.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/mirror.json) supplies a fixed frame and thin translucent pane. It does **not** implement the legacy camera-dependent portal shader |
| `void_siphon` | `TileVoidSiphonRenderer` draws two end-portal-textured quads with specific transforms/blend modes | [Static block model](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/void_siphon.json), enabled/disabled variants share the same model, and no BER; the portal-plane pass is absent |
| `hole` | `TileHoleRenderer` draws an end-portal-textured quad in the appropriate facing orientation | [`TCHoleBlock`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/misc/TCHoleBlock.java) explicitly returns `RenderShape.INVISIBLE` and has no BER; [legacy-format blockstates/hole.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/hole.json) refers to `thaumcraft:empty`. No verified replacement for the original visible hole shader |

The blockstate/BER absence is a strong code finding; do not misattribute all effects to missing textures, and check any independent particles or custom world overlay before claiming exhaustive framebuffer behavior.

### F16 — Pattern Crafter: legacy mode indicator and rotating gears are no longer tied to its active model

**Status:** `confirmed_dynamic_visual_state_loss`; complements N13 interaction `subHit` loss.

Legacy [`TilePatternCrafterRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TilePatternCrafterRenderer.java) renders a mode icon from `textures/blocks/pattern_crafter_modes.png` (selected by tile `type`) and two counter-rotating 3D gear elements. Modern [`TCPatternCrafterBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCPatternCrafterBlockEntity.java) still stores/cycles `type` in the 0–9 range. But active [`blockstates/pattern_crafter.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/pattern_crafter.json) varies only by `facing` and points to a fixed [block model](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/pattern_crafter.json), while no `PATTERN_CRAFTER` BER is registered. A mode-changing block entity alone does not update the visible mode icon or animate gears.

### F17 — Alembic and tube mechanisms: dynamic overlays/rods require specific parity checks

**Status:** `confirmed_TESR_path_loss`; exact static-model geometry parity and independent visual paths remain open.

- **Alembic:** legacy [`TileAlembicRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java) draws a label quad, `UtilsFX.drawTag(... tile.aspectFilter)` and a nozzle element. Modern `ALEMBIC` uses a fixed [`alembic.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/alembic.json) inheriting `alembic_normal`, with no BER. A static model cannot directly reproduce per-tile aspect labeling.
- **Tube Buffer:** legacy [`TileTubeBufferRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileTubeBufferRenderer.java) uses `ModelTubeValve.renderRod()` with direction-dependent rotation. The active modern [`tube_buffer.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/tube_buffer.json) assembles fixed `tube_side` pieces plus `tube_buffer_core` and has no own BER; verify whether this static arrangement matches rod alignment and sub-state geometry.
- **Tube Oneway:** legacy [`TileTubeOnewayRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileTubeOnewayRenderer.java) similarly rotates a model rod according to tile direction. Modern [`tube_oneway.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/tube_oneway.json) is connection-flag multipart JSON without BER. Direction is also tracked in [`TCLegacyTubeBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java); audit whether `facing` is reflected visually in every state. Missing TESR does not by itself prove the static tube is visually wrong in every pose.

### F18 — Negative controls and stronger audit methodology from this comparison

1. **Recharge Pedestal:** the modern `INFUSION_PEDESTAL` block entity type also includes `RECHARGE_PEDESTAL`; [`TCInfusionPedestalRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCInfusionPedestalRenderer.java) does render a rotating stored item. The missing *separate class name* is **not** proof of loss.
2. **Banner:** original `TileBannerRenderer` had a waving model and aspect emblem, but no active modern banner block/entity registration was found. Track a possible **content coverage gap**, not an active renderer regression.
3. **Already mapped BERs:** Crucible, Warded Jar, Bellows, Research Table, Golem Builder, Infusion Matrix/Pedestal, Thaumatorium and Tube Valve are present in the modern renderer registry. Their registration does **not** prove legacy-perfect animation/geometry.
4. **Static asset leftovers:** `models/block/chest.json` and `models/item/hungrychest.json` preserve closer Hungry Chest geometry but are not on the active `hungry_chest` model path. Asset existence/JSON validity tests cannot detect this **active reference mismatch**.
5. **State-to-renderer closure:** every active block-entity type with a legacy TESR needs a path `active block ID → blockstate/model → block entity → dynamic state → BER or explicitly equivalent alternate renderer → screenshot`. A registration-only audit or PNG existence check will miss Dioptra, Centrifuge, Pattern Crafter and Hungry Chest.
6. **Numbers are not interchangeable:** 23 legacy TESR registrations, 10 modern BER registrations, 45 modern block-entity types, 13 unpaired TESR registrations, and 12 active unpaired types refer to different denominators. Avoid reporting "13 confirmed broken blocks" without per-case inspection.

**Next investigation queue after F18:** cross-check all twelve active unpaired TESR paths against independent modern particle/render callbacks; then inspect the mapped ten BERs for partial legacy animation loss, validate item vs placed-block render pipelines, and continue the complete active-registry parity ledger. All visual claims remain subject to client verification.


### F19 — Mapped block-entity renderer coverage: registrations are not feature-completeness evidence

**Scope:** all ten `EntityRenderersEvent.RegisterRenderers` registrations at the pinned main baseline. These map to nine renderer classes because ordinary and void Warded Jars share the same class. The original and current code paths were inspected for the corresponding mechanisms. **Status:** `confirmed_code` for registered paths; individual outcomes below; pixel equivalence remains `requires_client`.

| Registered modern type(s) | Legacy dynamic paths | Modern implementation | Current assessment |
| --- | --- | --- | --- |
| `RESEARCH_TABLE` | Scroll, tinted ribbon, inkwell, 3-D quill | `TCResearchTableRenderer` has all three, including a custom quill mesh | `negative_control` for total missing props; alignment/alpha remain `requires_client` |
| `CRUCIBLE` | Fluid-surface height and aspect-dependent tint | `TCCrucibleRenderer` uses `getFluidHeight()` and the same `visSize()/500` color algorithm | `negative_control` for missing liquid surface; screenshot needed |
| `INFUSION_PEDESTAL` (also recharge variant) | Floating rotating stack | `TCInfusionPedestalRenderer` renders stack with different transform/display pipeline | `confirmed_code` transform difference, `requires_client` for visual impact (F24) |
| `INFUSION_MATRIX` | Eight rotating cubes, overlay and additive crafting halo | `TCInfusionMatrixRenderer` plus `TCInfusionClientEffects` implement those paths | `negative_control` for alleged missing halo (F23); stage ordering still `requires_client` |
| `BELLOWS` | Dynamic inflated bag/planks/nozzle and Tube Buffer extension | `TCBellowsRenderer` reconstructs these mechanisms | `negative_control` for missing renderer/mechanism; geometry parity needs client |
| `TUBE_VALVE` | Rotating/offset rod and ring, facing-dependent transforms | `TCTubeValveRenderer` reconstructs both mesh parts and orientation logic | `negative_control` for loss of moving valve parts; client verification pending |
| `THAUMATORIUM` | Facing-specific floating recipe-output stack | `TCThaumatoriumRenderer` preserves position, facing, output preview | `negative_control` for missing preview; item display pipeline differs |
| `WARDED_JAR` and `JAR_VOID` | Colored fluid, conditional lid/connector and aspect-filter label | `TCWardedJarRenderer` renders fluid only | `confirmed_legacy_mismatch` for missing dynamic labels and connector/brace branches (F21) |
| `GOLEM_BUILDER` | 3-D OBJ body, animated press and lava quad | Static OBJ block model; BER renders *only* an output item | `confirmed_legacy_mismatch` for animation/lava renderer paths (F20) |

**Method:** a shared modern renderer should be evaluated for *all* original branches, not merely the existence of `registerBlockEntityRenderer`. The nine-class/ten-registration denominator must not be mixed with the previously identified twelve active unpaired TESR paths.

Source: [modern BER registrations](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java).

### F20 — Golem Builder: the OBJ press remains static and the legacy lava pass is not reproduced

**Status:** `confirmed_legacy_mismatch` for missing dynamic render paths; `requires_client` for final appearance.

1. The [legacy `TileGolemBuilderRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileGolemBuilderRenderer.java) draws `model.renderAllExcept("press")`, moves the press by `sin(radians(tile.press)) * 0.625`, separately draws `model.renderPart("press")`, then draws a lava-textured quad.
2. The [modern `golem_builder.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/golem_builder.json) **does load the original OBJ asset**. The [referenced OBJ](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/golembuilder.obj) explicitly contains groups `table`, `press`, `anvil` and `box`; the press is therefore likely baked into the static model, not missing as an asset.
3. The [modern `TCGolemBuilderRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCGolemBuilderRenderer.java) only renders `entity.getItem(0)` as a rotating stack at `(0.5, 0.65, 0.5)`. It never reads `entity.press` and contains neither an OBJ part transform nor a lava quad.
4. Crucially, [`TCGolemBuilderBlockEntity.clientTick`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCGolemBuilderBlockEntity.java) **does update `press`** while crafting. The state-to-render bridge is the missing piece.
5. Negative control: do **not** report that the entire machine model is absent; the static OBJ is the registered world model. The additional modern output-item preview is a separate path not present in the original TESR and merits screenshot review.

**Client checks:** press motion at idle/start/mid-craft/completion, lava texture visibility and depth, object group visibility/orientation in four facing states, floating result item.

### F21 — Warded / Void Jar: stored aspect-filter state has no label or conditional fitting render path

**Status:** `confirmed_legacy_mismatch` for code-path loss; `requires_client` for exact visible occlusion/translucency.

- [Legacy `TileJarRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java) conditionally renders `ModelJar.renderLidBrace()` when blocked, `renderLidExtension()` for a top connection, and an oriented `textures/models/label.png` quad plus `UtilsFX.drawTag(aspectFilter)` when an aspect filter is set. These branches work independently of nonempty fluid.
- [Modern `TCWardedJarBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCWardedJarBlockEntity.java) preserves `aspectFilter`, `labelFacing` and `blocked`, including NBT/network updates. Those states have no counterpart in the actual dynamic draw code.
- [Modern `TCWardedJarRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCWardedJarRenderer.java) exits early whenever there is no fluid, otherwise renders only six tinted quads for the fluid volume. It does **not** read `aspectFilter`, `labelFacing` or `blocked`.
- [`jar_normal.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/jar_normal.json) and [`jar_void.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/jar_void.json) provide static container/fixed lid geometry, not dynamic label text or conditionally extended connectors.

**Reproduction matrix:** ordinary and void jars × empty/filled × unfiltered/filtered × blocked/open × top connection/no connection × each label facing. An *empty, filtered* jar is a particularly clear regression probe: the original label branch still exists while the modern BER returns before drawing anything.

### F22 — Brain in a Jar: original animated 3-D brain replaced by cuboid block model

**Status:** `confirmed_legacy_mismatch` for renderer/model behavior; `requires_client` for appearance.

- The original [`TileJarBrain`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/devices/TileJarBrain.java) extends `TileJar` and has client rotation state. The original [`TileJarRenderer.renderBrain`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java) draws `ModelBrain` with `brain2.png`, a sinusoidal bob, interpolated facing rotation and a separate brine mesh/texture.
- The modern [`JAR_BRAIN`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java) has no entry in [modern BER registration](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java). [`TCBrainJarBlock`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBrainJarBlock.java) uses `RenderShape.MODEL`, [`jar_brain.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/jar_brain.json) refers to [`jar_brain_item.json`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/jar_brain_item.json).
- The referenced model contains **axis-aligned cuboids** for the `flesh` brain, brine and container. The [modern `TCBrainJarBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCBrainJarBlockEntity.java) implements the XP behavior but no original camera/nearby-entity tracking or client brain-rotation renderer.
- This is distinct from F15–F18's missing BER tally and supports the user-observed boxed appearance. It must not be misattributed to `brain_box.json` merely because that separate asset also exists: the *active blockstate* is `jar_brain_item.json`.

**Client checks:** idle and XP-attracting brain motion, near/far player, brine transparency and layering, inventory item vs placed block, side/above viewpoints.

### F23 — Infusion Matrix halo: negative control established by following the independent client effects path

**Status:** `negative_control` for “halo omitted”; `confirmed_code` for moved rendering implementation; `requires_client` for event-stage outcome and pixel equality.

- The [original `TileInfusionMatrixRenderer.drawHalo`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileInfusionMatrixRenderer.java) emits additive radial rays using seed `245L`, 10 rays on fast graphics or 20 on fancy, and progress-scaled geometry.
- The [modern `TCInfusionMatrixRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCInfusionMatrixRenderer.java) invokes `TCInfusionClientEffects.noteCraftingMatrix` when crafting, while rendering the eight base cubes and emissive overlays itself.
- [`TCInfusionClientEffects.renderCraftingHalos`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/TCInfusionClientEffects.java) applies additive alpha/one blending, the same deterministic seed and 10/20 ray count, generating three triangles per ray.
- [`TCClientEvents.onRenderLevelStage`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java) calls `TCInfusionClientEffects.render` at `AFTER_PARTICLES`. The halo queue is then cleared. Whether the BER supplies positions *before* that render stage in actual 1.21.1 client execution, and whether transparency/depth looks equivalent, are still **open runtime checks**.

Do not reopen “halo absent” solely because `drawHalo` is not inside the new BER.

### F24 — Infusion Pedestal: float-item transforms differ; visual result not yet measured

**Status:** `confirmed_code` for the input transform/display differences; `requires_client` for whether the item is visibly mispositioned or wrongly sized.

- [Legacy `TilePedestalRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TilePedestalRenderer.java): translate to `y + 0.75`, scale `1.25`, rotate by `ticks % 360`, then render an `EntityItem` with zeroed `hoverStart`.
- [Modern `TCInfusionPedestalRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCInfusionPedestalRenderer.java): translate to `y + 1.08`, scale `0.5`, rotate by `(gameTime + partialTick) * 2`, render `ItemDisplayContext.GROUND` without an entity renderer.
- These are **input matrix/API differences**, not a demonstrated 0.33-block screen-space error: old entity-item internal transforms and new display transforms are not identical. Compare matched stack models, dropped/flat item, pedestal/recharge variants and rotating frames.
- [Legacy `TileThaumatoriumRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java) versus [modern `TCThaumatoriumRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCThaumatoriumRenderer.java) provides a useful control: the facing-dependent preview position and `0.75` scale were explicitly retained, although the item render implementation changed.

### F25 — BER negative controls: original mechanisms were substantially reimplemented

**Status:** `negative_control` for missing *core mechanism* only, not a certification of pixel parity.

1. **Crucible:** [original](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileCrucibleRenderer.java) and [modern](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCCrucibleRenderer.java) both gate on nonempty water, use the fluid height and derive RGB tint from `visSize()/500`. Lighting and water atlas sampling still merit screenshots.
2. **Bellows:** [original](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java) and [modern](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBellowsRenderer.java) both animate bag/top/bottom planks and draw optional Tube Buffer nozzle extensions. Exact model-part geometry, lighting and orientation require runtime verification.
3. **Tube Valve:** [original](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileTubeValveRenderer.java) and [modern](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCTubeValveRenderer.java) share the `-rotation * 1.5` ring angle and `-0.03 - rotation/360 * 0.09` translation, and have ring+rod parts. Exact active/idle interpolation and orientation need an in-game check.
4. **Research Table:** [original](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileResearchTableRenderer.java) and [modern](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCResearchTableRenderer.java) both render the conditional scroll, inkwell and quill, including a 3-D quill reconstruction. The three declared `QUILL_COMPENSATE_*` constants in modern source do not appear in the actual render transform; assess any alignment issue with screenshots, **not as an established defect**. Do not conflate block BER with earlier Research Table *screen* blend/Knowledge Gain findings (F01/N09).
5. **Thaumatorium:** [original](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java) and [modern](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCThaumatoriumRenderer.java) have matching facing-position formulae and scaling for floating recipe output. The user-reported *held Thaumatorium item orientation* belongs to the item-model/display pipeline, not this placed-block BER.

**After F25:** the mapped BER pass has now reached all ten registrations. Next source-led work: audit unrelated client callbacks/particles for the twelve previously unpaired TESR types, then expand an active-ID manifest across block/item model display paths, entities and HUD. Still missing matched client capture and proof ledger for every active object.



### F26 — Cross-cutting block-event delivery risk: effect handlers are in block entities without a forwarding block

**Status:** `confirmed_code` for six missing block-level dispatch overrides; `probable_cause` of suppressed event-driven client particles/audio until server/client event tracing confirms actual delivery. This is **independent of BER registration**.

The modern code uses `Level.blockEvent(pos, block, id, data)` in several block entities, then puts client visuals in `BlockEntity.triggerEvent(id, data)`. Under 1.21.1 block-event dispatch, the *block's* `BlockState.triggerEvent` path controls whether an event is accepted/broadcast. `BaseEntityBlock` provides a dedicated override to pass an event on to its tile; merely declaring `extends Block implements EntityBlock` does not supply that override. API references: [NeoForge 1.21.1 `BlockBehaviour.triggerEvent`](https://lexxie.dev/neoforge/1.21.1/net/minecraft/world/level/block/state/BlockBehaviour.html) and [`BaseEntityBlock.triggerEvent` API](https://aldak.netlify.app/javadoc/1.21.1-21.1.x/net/minecraft/world/level/block/baseentityblock).

At the pinned source SHA, the following six blocks extend plain `Block` and declare `EntityBlock` but have **no `triggerEvent` override**. Their corresponding BEs emit block events:

| Block → BE | Emitted event / intended client behavior | Dispatch finding |
| --- | --- | --- |
| [`TCWaterJugBlock`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCWaterJugBlock.java) → [`TCWaterJugBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCWaterJugBlockEntity.java) | `1`, client target-zone/trail state | No block-level event forwarding; a second dead-trail gap is F27 |
| [`TCVoidSiphonBlock`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCVoidSiphonBlock.java) → [`TCVoidSiphonBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCVoidSiphonBlockEntity.java) | `5`, twelve `ParticleTypes.PORTAL` particles on successful rift drain | A particle handler exists but is probably unreachable through the queued block event; this *does not* replace the legacy animated portal mesh |
| [`TCCrucibleBlock`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCCrucibleBlock.java) → [`TCCrucibleBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCCrucibleBlockEntity.java) | `1` / `2` / `99`, `TCFXDispatcher` crucible bamf/boil and local sound | Event-driven visual/audio branch probably not reached; Crucible's separate water-surface BER remains present (F25) |
| [`TCSmelterBlock`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java) → [`TCSmelterBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java) | `1`, directional vent sound and smoke/particle burst | The **event-specific** vent burst is at risk; this does *not* imply all ambient smelter particles are absent |
| [`TCInfernalFurnaceBlock`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCInfernalFurnaceBlock.java) → [`TCInfernalFurnaceBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCInfernalFurnaceBlockEntity.java) | `3`, lava pop sounds and output-side lava particles | Event-driven burst at risk |
| [`TCMirrorBlock`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCMirrorBlock.java) → [`TCMirrorBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCMirrorBlockEntity.java) | `1` on item intake/ejection | No matching `TCMirrorBlockEntity.triggerEvent` handler found; this is an orphaned event emission, *not* yet evidence of a particular missing particle |

**Second independent guard:** several BE `triggerEvent` methods explicitly return the superclass result (normally `false`) on the server and only return `true` in their `level.isClientSide` branch. Under vanilla's acceptance/broadcast condition, even simply adding a forwarding block would not necessarily accept the server-side event. A correct eventual solution requires tracing *both* the block delegate and server-side return behavior, not merely calling the BE handler on clients.

**Negative control:** [`TCHungryChestBlock`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCHungryChestBlock.java) actually `extends BaseEntityBlock`. Its chest-opener event path therefore does not share the same *missing block-level override* problem; its wrong visible model and absent lid BER remain separately tracked in F11. The [`TCHungryChestBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCHungryChestBlockEntity.java) also does not implement a lid-state `triggerEvent` consumer.

**Required test before promoting cause to runtime-confirmed:** instrument a dev client/server with one event sent per target, record whether `BlockState.triggerEvent` and `BlockEntity.triggerEvent` run server/client and whether a `ClientboundBlockEventPacket` is sent. Do not modify live source during this research phase.

### F27 — Everfull Urn / Water Jug: original animated water trail has no active client-side consumer

**Status:** `confirmed_legacy_mismatch` for omitted trail-generation code; `probable_cause` for event non-delivery per F26; visual effects require client reproduction.

- In the original [`TileWaterJug.update`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/devices/TileWaterJug.java), a received client event `1` sets `zc` (target zone) and `tcount=5`; on client ticks the tile calls `FXDispatcher.INSTANCE.waterTrailFx` towards the target offset and decrements the timer.
- Modern [`TCWaterJugBlockEntity`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCWaterJugBlockEntity.java) still emits event `1` and has `triggerEvent` code setting `lastClientTrailZone` and `clientTrailTicks=5`; **neither field is subsequently decremented or consumed by a client tick, world renderer, particle dispatcher, or other inspected modern implementation**.
- Repository Java code search at this baseline finds `clientTrailTicks` and `lastClientTrailZone` only in their defining BE; no `waterTrailFx` equivalent call exists under the modern Java source tree. These searches corroborate the direct source inspection but cannot independently prove the absence of renamed/general-purpose visual effects.
- The event-dispatch gap F26 may prevent the values from being assigned at all, a separate and earlier failure. Even if event forwarding were repaired, the original rendered water stream would still be absent unless a client-side consumer is implemented.

**Reproduction:** place an active Everfull Urn adjacent to a valid refillable target at a nonzero zone offset, wait for a successful automatic transfer, compare original and port for an animated target-directed water stream.

### F28 — Focused search for alternate render paths for the unpaired TESR set

**Status:** `confirmed_code` for inspected references/registration paths; `not_checked` for all possible indirect resource-driven callbacks and client pixels.

A cross-check of current `thaumcraft/client` Java source symbols found **no direct client-world-renderer references** to `TCDioptraBlockEntity`, `TCCentrifugeBlockEntity`, `TCHungryChestBlockEntity`, `TCMirrorBlockEntity`, `TCMirrorEssentiaBlockEntity`, `TCVoidSiphonBlockEntity`, `TCAlembicBlockEntity`, `TCPatternCrafterBlockEntity` or `TCHoleBlockEntity`. `TCFocalManipulatorBlockEntity` is referenced by menus/networks but not by a client world-renderer. `TCLegacyTubeBlockEntity` is referenced by `TCTubeValveRenderer`, but that BER is only registered to the valve type rather than all tube variants. This corroborates the F10–F17 observation that dedicated world dynamic effects were not simply moved to a similarly named BER.

Eight inspected modern block classes (`TCFocalManipulatorBlock`, `TCDioptraBlock`, `TCHungryChestBlock`, `TCCentrifugeBlock`, `TCPatternCrafterBlock`, `TCVoidSiphonBlock`, `TCMirrorBlock`, `TCAlembicBlock`) had no `animateTick` override. This is a **bounded negative check**, not a global absence proof: entity-level event-driven FX (F26), generic particle dispatch, JSON model variants and vanilla render behavior can still contribute visuals.

One real alternative was found: Void Siphon has a client `ParticleTypes.PORTAL` branch in its BE (F26), although likely disconnected by block-event dispatch. Thus the correct defect description is **missing legacy animated portal mesh plus an at-risk modern particle branch**, not “Void Siphon has no FX code.”

**Next:** complete active registry and indirect render/resource dispatch coverage, extend item/equipment/entity comparison, and obtain controlled client screenshots. No existing code has been changed.


### F29 — Grappler icon: inactive legacy `type` predicates and missing loaded-state transitions

**Status:** `confirmed_code` / `confirmed_legacy_mismatch` for the item property and stored state; `probable_cause` for the user-observed icon; `requires_client` for exact rendering.

The original [ItemGrappleGun](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/tools/ItemGrappleGun.java) registers a legacy item property named `type`. It returns 1 when the held stack's `loaded` NBT byte is 1 and 0 otherwise. Firing sets `loaded=1`; when the grapple is no longer attached, the state returns to zero. The corresponding original model [grapple_gun.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/resources/assets/thaumcraft/models/item/grapple_gun.json) has a `parent: item/generated`, *no base textures*, and two predicate-selected complete models: `grapple_gun_1` (value 0) and `grapple_gun_2` (value 1).

The present [port model](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/grapple_gun.json) retains this same predicate structure **unchanged**. The two detailed 3D JSON models [variant 1](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/grapple_gun_1.json) and [variant 2](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/grapple_gun_2.json) exist. However, [TCItemProperties](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCItemProperties.java) registers `thaumcraft:type` only for `PRIMORDIAL_PEARL`, **not** `GRAPPLE_GUN`. Its model correctly requests `thaumcraft:type`; the Grappler model instead requests the unqualified legacy `type`, whose resolved key is `minecraft:type`. [TCGrappleGunItem](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/tools/TCGrappleGunItem.java) launches a projectile and handles charge but contains no `loaded` item state, matching property getter, or state reset.

This produces **two independent gaps**: (1) no registered predicate value for the active Grappler item, also with a legacy-vs-modern namespace mismatch; and (2) no item-specific loaded/idle state transition to drive the two 3D variants even if a predicate were registered. Because the top-level JSON defines no fallback layer texture, failed override selection is particularly high-risk. The missing icon's exact form needs an in-game render, not a claim based solely on JSON.

**Negative controls:** the Primordial Pearl uses `thaumcraft:type` consistently, the Caster `thaumcraft:focus`, and both Warded Jars `thaumcraft:fill`. This is a Grappler-specific registration/state issue, not proof all item predicates are broken.

**Client confirmation:** inspect inventory, dropped stack, item frame, hotbar, GUI, and first-/third-person poses at idle, launch, active tether, and detach. Record which of the two variants actually bakes and renders; do not conflate projectile gameplay with item icon parity.

### F30 — Cloth / Void Robe icon tint: alpha-bit risk; original layer order is not a regression

**Status:** `confirmed_code` for returned color values and model-layer comparisons; `probable_cause` for color/opacity defects; `requires_client` for pixel appearance. The previously reported Thaumaturge/Darkseer icon symptoms are treated as observations, not proof of one common rendering bug.

The six armor icon registrations in [TCColorHandlers](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java) use:

`ItemColor robeColor = (stack, tintIndex) -> tintIndex == 0 ? DyedItemColor.getOrDefault(stack, ItemRobeArmor.LEGACY_DEFAULT_COLOR) : WHITE;`

Here `WHITE=0xFFFFFF`, while the default dyed component and `DyedItemColor` colors supply 24-bit RGB. Therefore the callback returns values with the high byte `0x00` rather than guaranteed opaque `0xFF`. In the same modern class the jar, label, and essentia item handlers explicitly return `0xFF000000 | (color & 0xFFFFFF)`, and Nitor item handlers use literal `0xFFRRGGBB`. This creates an **inconsistent item tint representation**. It is a plausible shared mechanism for color/black-edge defects if the 1.21.1 rendering path consumes item tint values as ARGB; controlled screenshots and/or an instrumented item-tint trace are required before asserting a specific visible alpha outcome.

Original [ColorHandler](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/ColorHandler.java) returned the armor item's RGB or `-1` for the untinted layer. The modern callback preserves the original intended tint-index semantics but has not demonstrated the appropriate 1.21.1 color packing.

**Rejected alternative (negative control):** precise legacy vs modern item model comparison shows the **same** two-layer ordering for Cloth chest, legs and boots (`layer0` body, `layer1` overlay); Void Robe chest/legs both put the `_over` texture in `layer0` and body in `layer1`; Void Robe helmet has just `layer0` in both versions. Thus the layer reversal between armor families is **legacy behavior**, not a port-introduced reversal. The original `items/` texture paths were migrated to modern `item/`, as expected.

**Separation:** this callback applies to **item icons**. The equipped robe armor model/render pass may use a different tint/overlay pipeline, so nothing here proves that worn armor has the same defect.

**Client confirmation:** capture all six icons in default and custom-dyed color, with transparent and opaque UI backgrounds and GUI-scale/shader variations, then separately check worn armor on player and armor stand. Compare full RGBA samples and the original overlay position; avoid blanket fixes for item and worn-armor layers.

### F31 — Missing legacy dynamic item color handlers: focus, Caster focus, and golem item

**Status:** `confirmed_legacy_mismatch` for static item-color registration absence; `requires_client` for exact on-screen colors and item-model pipeline interactions.

The legacy [ColorHandler](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/ColorHandler.java) registers **three** distinct dynamic color callbacks:

1. `ItemsTC.casterBasic`: if an installed focus is present, use that focus's color for the higher-index focus layer.
2. `ItemsTC.focus1`, `focus2`, `focus3`: color the focus item with `ItemFocus.getFocusColor(stack)`.
3. `ItemsTC.golemPlacer`: read NBT `props` and tint the item according to the chosen golem material's `itemColor`.

Modern [TCColorHandlers](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java) registers **none of** `TCItems.CASTER_BASIC`, `TCItems.FOCUS_1/2/3`, or `TCItems.GOLEM_PLACER`. Their items remain registered in [TCItems](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java), so this is not a case of removed legacy content.

The data and model hooks otherwise still expose the intended visual variation:

- [modern ItemFocus](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/casters/ItemFocus.java) has `getFocusColor` reading the focus-package component, while [focus_1 model](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/focus_1.json) (and focus_2/3) supplies a generated tintable layer.
- [ItemCaster](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/casters/ItemCaster.java) exposes the installed focus stack. The modern [Caster model](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/caster_basic.json) selects a focus variant using registered `thaumcraft:focus`; [its focused gauntlet geometry](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/caster_gauntlet_focus.json) has faces with `tintindex: 1`. Model variant switching can therefore work while its intended focus color remains absent.
- [ItemGolemPlacer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/golems/ItemGolemPlacer.java) stores the golem `props` in modern custom stack data, and [golem item model](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/golem.json) is a generated tintable texture. Data preservation does not restore the missing color callback.

**Why this matters:** a scan that checks only missing textures or JSON validity misses the absence of a registry callback when all relevant resources and item IDs exist. Conversely, missing dynamic tint does **not** by itself prove an item renders black; vanilla untinted pixels and hardcoded texture colors still affect the visual result.

**Client confirmation:** vary focus package colors and Caster-mounted focus, compare all three focus item sizes, and vary golem material. Record whether the generated item and focused gauntlet respond to those values. Keep the base Caster item without a focus as a negative control.

### F32 — Broader 24-bit vs 32-bit ItemColor risk: candles, crystals, leaves; counterexamples

**Status:** `confirmed_code` for returned integer values and active model tint indices; `probable_cause` for visual alpha corruption; `requires_client` for actual screenshots. Distinguish **item** coloration from **block** coloration.

The same modern [TCColorHandlers](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java) uses mixed color conventions across items:

| Active item family | Source colors returned by item callback | Packed high byte | Checked item model or geometry | Interpretation |
| --- | --- | --- | --- | --- |
| Cloth/Void Robe pieces | `DyedItemColor.getOrDefault` / `0xFFFFFF` | `0x00` | generated robe layers | As F30; likely alpha-risk |
| Six primal + Flux crystals | `AIR=0xFFFF7E`, `FIRE=0xFF5A01`, `WATER=0x3CD4FC`, `EARTH=0x56C000`, `ORDER=0xD5D4EC`, `ENTROPY=0x404040`, `FLUX=0x800080` | `0x00` | e.g. `models/item/crystal_aer.json` generated `layer0: thaumcraft:item/crystal_planter` | Shared-risk item tints, not proof of every crystal appearance |
| Sixteen colored candles | `DYE_*` constants, `0xFFFFFF` for non-tinted layer | `0x00` on index 0 | `models/item/candle_red.json` inherits `thaumcraft:block/candle`, whose five elements expose 24 faces with `tintindex:0` and five untinted faces | A verified tintable path with a potentially wrong packed alpha byte |
| Greatwood/Silverwood leaf items | `FoliageColor.getDefaultColor()` / `WHITE=0xFFFFFF` | conventionally RGB / `0x00` for WHITE | active registered leaf items | Probe separately; foliage value source and world rendering differ |
| Nitor item family | explicit `0xFFRRGGBB` literals | `0xFF` | e.g. `models/item/nitor_red.json` | Negative control for color packing |
| Jar fill, jar label, dynamic aspect item variants | explicit `0xFF000000 | (color & 0xFFFFFF)` | jar/item tint layers | Negative control for dynamic ARGB packing |

The candle evidence is stronger than a mere suspicion based on a registry name: its shared block-model faces actually carry tint index 0. Nonetheless, the precise NeoForge 1.21.1 item tint path and its pixel interpretation must be validated before calling the alpha hypothesis proven. **Do not carry this item-color suspicion into world block colors without checking their distinct shader/renderer contract.**

**Suggested focused batch:** one candle per dye family plus shared parent, the seven crystal items, both leaves and one Nitor control on the same screen. Capture direct pixels and if possible trace the registered callback's returned value through to vertex color. This can distinguish `0x00RRGGBB` from unwanted texture alpha fringes, blend-state errors, and adjacent-quad overdraw.

**Research gate:** F29–F32 are recorded as diagnosis and test design only. No mod code, audit scripts, resource data, CI, or existing PR code branches were altered.



### F33 — Installed golem seals: original hand-gated world renderer has no port equivalent

**Status:** `confirmed_legacy_mismatch` for the static draw path; `requires_client` for the player's exact observed result.

The original [RenderEventHandler](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/lib/events/RenderEventHandler.java) listens to `RenderWorldLastEvent`. When either held item implements `ISealDisplayer`, it calls `drawSeals`. That function iterates `SealHandler.sealEntities` for the current dimension, limits visible seals to squared distance `<=256` (radius 16 blocks), reduces alpha with distance, and draws:

- The surface-aligned seal icon using `renderSeal` and the seal's `getSealIcon()` texture.
- A rotating, color-coded middle marker.
- A conditional area wireframe for seals implementing `ISealConfigArea`.
- A reduced/different depth presentation when the observing player sneaks.
- A redstone-inactive appearance and occasional spark, conditional on `isStoppedByRedstone`.

The current [TCClientEvents](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java) subscribes to `RenderLevelStageEvent` but only renders particle/infusion/wisp effects after particles and a Thaumometer aspect overlay after weather. Its client code contains no seal draw path. A code search for `drawSeals` and direct use of `SealHandler.sealEntities` found the original method and the modern storage declaration, respectively, but **no modern visual consumer**. This is backed by explicit render-stage inspection, not simply the absence of an identically named class.

The item-side gate is **still declared**: [ItemSealPlacer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/golems/seals/ItemSealPlacer.java), [ItemGolemBell](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/golems/ItemGolemBell.java) and [ItemGolemPlacer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/golems/ItemGolemPlacer.java) implement `ISealDisplayer`; the port's [ISealDisplayer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/api/golems/ISealDisplayer.java) describes the visibility behavior. These interfaces alone cannot render anything. The seal's icon and area metadata surviving in APIs do not constitute a drawing path.

**Client tests:** place the same seal on all six block faces; compare empty hands, seal item, Golem Bell, Golem Placer, main/offhand, near/far distance, sneaking, active/redstone-disabled, configured area and custom dye. Original visual gating **must not** be misreported as “seals always visible.” Establish inventory item/icon vs **placed seal world overlay** as separate test targets.

### F34 — Seal client synchronization is not implemented, independently of its absent renderer

**Status:** `confirmed_code` for empty synchronization method and missing original packet path; client state delivery otherwise `not_checked` outside inspected code, with negative source-search results.

In the original [SealHandler](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/golems/seals/SealHandler.java) / [PacketSealToClient](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/lib/network/misc/PacketSealToClient.java), seal creation/update/removal can notify clients. The packet includes the seal's position, face, type, configuration/area, color, priority, lock/redstone/owner and removal sentinel; its handler reconstructs the client's local `SealEntity`. [ChunkEvents.chunkWatch](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/lib/events/ChunkEvents.java) additionally sends seals to a player when their chunk becomes visible.

The port's [SealHandler](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/golems/seals/SealHandler.java) adds a seal to its static per-dimension map and invokes `sealEntity.syncToClient(level)` on placement. But [SealEntity.syncToClient](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/golems/seals/SealEntity.java) contains only a comment: `// Reserved for client sync packet when needed`. Repository source searches found no modern `PacketSealToClient`, no client-side `SealHandler.sealEntities` consumer, and no equivalent seal payload registration among the inspected [mod event-bus registrations](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/Thaumcraft.java). By contrast, the mod registers explicit payload handlers for Aura, Infusion, Thaumatorium, Knowledge, Warp, Research Table, Thaumonomicon, Focal Manipulator, Golem Builder and Entity FX.

**Interpretation:** the placed-seal symptom comprises at least **two separable failures**: the world overlay draw path is missing (F33), and the authoritative server state is not broadcast as in the original. A renderer-only change would still leave dedicated-server clients without reliable seal state. Do not mistake a shared static map under an integrated client/server JVM for a network protocol.

**Test matrix:** dedicated multiplayer with two clients, dimension transfer, login after seal placement, chunk unload/re-watch, configuration edits, removal, and unloaded-area re-entry. Confirm packet content/sequence *and* rendered state before labeling this solved.

### F35 — Seal chunk persistence and world-lifetime cleanup were not carried over

**Status:** `confirmed_legacy_mismatch` for the inspected persistence path; `probable_cause` for loss of placed seals after restart; `requires_client` for chunk/server lifecycle reproduction.

The original [ChunkEvents](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/lib/events/ChunkEvents.java) includes three distinct lifecycle hooks:

1. `chunkSave` serializes all seals in a chunk as NBT under `Thaumcraft.seals`, using each seal's `writeNBT()`.
2. `chunkLoad` reconstructs those `SealEntity` values via `readNBT()` and adds them back to the handler.
3. `chunkWatch` pushes the loaded seals to the observing player via the client packet (F34).

Original [WorldEvents.worldUnload](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/lib/events/WorldEvents.java) also removes the dimension's seal map on world unload.

Modern [SealEntity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/golems/seals/SealEntity.java) still implements `writeNBT`/`readNBT`, but **nothing in the inspected modern source calls them to save or restore a placed seal**. Modern [SealHandler](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/golems/seals/SealHandler.java) keeps a process-static map keyed by `ResourceKey<Level>`, and its code has no chunk serialization, persistent saved-data integration, or world-unload map cleanup. Code search for `sealEntities` in the modern module returned only this map implementation. [TCGolemEvents](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/golems/TCGolemEvents.java) ticks seals on the server and is registered by [Thaumcraft](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/Thaumcraft.java), proving that server-tick execution exists; it does **not** establish persistence or client state reconstruction.

The architectural implication is a possible **restart-loss / stale-map split**: seals are created and ticked in process memory but are not shown to enter any persistent world-save path, while long-lived static maps may survive world transitions in a running JVM. Treat this as an evidence-backed persistence gap and lifetime risk, not as an empirically observed data-loss event until tested.

**Runtime tests:** place/configure seals, save and shut down fully, relaunch dedicated server, re-enter chunk, and check retained count/properties/task execution. Separately unload/reload chunks without restarting and switch integrated worlds. Record server and each client independently, because an invisible seal, an unsynchronized seal and a non-persistent seal are three different outcomes.

**Research scope:** F33–F35 complete a targeted static trace for the user-reported missing placed seals, but not a complete audit of every seal type, resource texture, configuration UI, inventory variant or visual pixel. No production code, tests or auditing scripts have been modified.



### F36 — Research Table alpha-blending commits do not isolate a single cause for HUD borders

**Status:** `confirmed_code` for historical change; `negative_control` against treating the old fix as proof of the current HUD mechanism.

The historical chain has two distinct commits:

1. [`a0095771`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/commit/a0095771d3127bdab457cb3e2add5a14961a8f34) replaced many `TCResearchTableScreen` ordinary `GuiGraphics.blit(...)` calls with a shared helper that called `guiGraphics.blit(RenderType::guiTextured, ...)`. The commit described an alpha-rendering correction but changed the selected rendering overload along with GUI layout work.
2. [`d39be45`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/commit/d39be45db1b6d0c577bd85f78fc29c2b269b6a0d) replaced that helper with **both** `RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();` **and** the ordinary `guiGraphics.blit(...)` overload. The original `RenderType::guiTextured` path was removed in the *same* commit.

Therefore this history proves that an alpha-sensitive GUI problem was addressed, **not which of these two simultaneous changes was necessary**. No isolated A/B commit separately tests the blend state against the blit overload.

The current [Research Table helper](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java) still uses explicit blending and plain `blit`; the current [Instrument HUD](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCInstrumentHud.java) already uses plain `blit` but does not explicitly set the blend state. Crucially, the [official NeoForge 1.21.1 GuiGraphics documentation](https://docs.neoforged.net/docs/1.21.1/gui/screens/) shows ordinary `graphics.blit(texture, x, y, u, v, width, height)` as standard supported usage: using that overload is not intrinsically wrong and does not itself prove a blending failure.

**Practical evidence limit:** the earlier Research Table fix is a strong reason to inspect current render-state behavior, but **copying the same two lines cannot be justified as the verified fix** without an isolated render-state experiment. Record actual blend enable, equation and source/destination factors for the affected HUD draw calls.

### F37 — Pixel-level HUD atlas inspection: real low-alpha black edge pixels, with identical legacy and port PNG

**Status:** `confirmed_resource` for binary identity and precise RGBA measurements; `probable_cause` for thick black outlines under improper blending; `requires_client` for actual runtime blending state.

Three direct repository files were retrieved as binary base64 and compared: legacy source `textures/gui/hud.png`, self-decompiled `textures/gui/hud.png` and modern port `textures/gui/hud.png`. Their payloads match byte-for-byte and each has Git blob SHA `8dd9f0b75138e7e0be8dc07020ecf6a5b16d8114`. The image is 256×256 pixels in RGBA mode. This eliminates texture-file corruption, size drift, or changed original art as an explanation for a *port-only* thick outline.

Direct Pillow RGBA statistics for the atlas regions used by the two meters:

| Source region (pixel coordinates, exclusive end) | Pixels | Relevant alpha distribution | Implication |
| --- | ---: | --- | --- |
| Aura frame `[72,48..88,128)` | 1,280 | 462 alpha=0; 116 black pixels have alpha=33; ~100 black pixels have alpha=6; 68 alpha=255 | The sprite contains a wide band of very faint black fragments that should not be treated as opaque |
| Sanity back `[152,0..172,76)` | 1,520 | 1,360 alpha=0, 160 alpha=255 | Background is mostly fully transparent, with narrow opaque glyph fragments |
| Sanity foreground `[176,0..196,76)` | 1,520 | 454 alpha=0; 53 black pixels alpha=1, 39 black pixels alpha=6, 30 black pixels alpha=36, 367 alpha=255 | The foreground contains antialiased black edges and opaque ornamentation; the small-alpha edge pixels are a likely source of amplified black fringe |
| Aura source fill `[88,56..96,120)` | 512 | all 512 pixels alpha=255 | The original uses a fully opaque, grayscale **textured fill**, tinted via render color, not a flat semi-transparent colored rectangle |

Example of a real foreground pixel: atlas `(188,0)=(0,0,0,60)`; with ordinary source-alpha blending, that black pixel contributes roughly 24% opacity. At `(182,0)=(0,0,0,20)` it contributes under 8%. If source alpha were ignored or a mismatched blend configuration forced opaque rendering, these would become large dark shapes. **That is a quantified mechanism, not yet a proven current render-state failure.**

Original [HudHandler.renderHuds](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/lib/events/HudHandler.java) explicitly enables GL blending with `glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)` before drawing either meter. The modern [TCInstrumentHud](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCInstrumentHud.java) contains no matching explicit setup, but the caller's vanilla `GuiGraphics` render path may still establish correct blending. This remains the central uncertainty.

**Controlled test:** sample screenshot RGBA at the outer contour, record GL/render-pipeline state immediately before and during each meter blit, and compare the same HUD texture fragment rendered under explicitly verified alpha blending vs unchanged vanilla state. Keep GUI scale and shader pack fixed. Check the original model's intended faint dark shadow separately from the user's unexpectedly **thick** border.

### F38 — Meter compositing is also changed: aura frame occlusion, fills, additive effects, and max-Warp symbol

**Status:** `confirmed_legacy_mismatch` for draw sequence and effects; `requires_client` for contribution to border thickness.

The original [HudHandler](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/lib/events/HudHandler.java) draws Vis/Flux textured grayscale columns from atlas UV `(88,56)`, colors them via GL color, adds **moving translucent highlight quads** from UV `(96,56+offset)` and `(104,120-offset)` with temporary additive blending `glBlendFunc(770,1)`, restores normal blending, and draws the decorative frame from UV `(72,48)` **after** the fill and highlights. The capacity marker is then drawn from UV `(117,61)`.

Current [TCInstrumentHud.renderAura](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCInstrumentHud.java) draws the frame **before** its fill, uses `graphics.fill` flat `0xD0...` rectangles, does **not** reproduce the animated texture highlights or additive pass, and then draws the marker. Although the destination rectangle is positioned inside the source frame in ordinary geometry, the frame's antialiased inner pixels and fill compositing cannot be presumed identical after changing draw order. The screenshot consequence needs a pixel test.

For the Sanity/Warp meter, the original intentionally draws **two different atlas portions** (`[152,0]` back and `[176,0]` foreground) around a fill sourced from grayscale `[200,...]`. The port preserves two frame blits but changes the interior from grayscale textured color modulation to flat `0xD0...` fills. Thus **two frame layers are not an accidental duplicate**. The original also draws a jittered indicator from UV `[216,0..236,16)` when effective Warp reaches 100; the modern `renderSanity` has no corresponding draw call, another concrete missing original visual. Geometry and vertical stacking deltas are already detailed in N11; they are not reclassified here as a proven black-outline root.

**Separation of mechanisms:** the missing Vis/Flux glow, frame/fill draw order, and absent max-Warp symbol are confirmed parity differences. The **unwanted thick black contour**, however, still needs the blend-state/pixel experiment in F37. The underlying sprite itself intentionally includes some dark antialiasing, so simply removing all dark pixels would damage original artwork.

### F39 — Research Table Knowledge Gain overdraw precisely scoped; not a persistent Research Table background defect

**Status:** `confirmed_code` for repeated calls; `requires_client` for visibility/intensity and whether the user notices the artifact.

Current [TCResearchTableScreen](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java) invokes `TCKnowledgeGainHud.renderOverScreen(...)` **twice consecutively** at the end of `render(...)` and **twice consecutively** in its `renderTooltip(...)` override. This is four direct call sites, not a claim that exactly four draws occur under every possible `AbstractContainerScreen` call sequence. Depending on superclass tooltip dispatch, the effective count can vary.

The global [TCKnowledgeGainHud.renderLayer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java) intentionally returns early when the current screen is `TCResearchTableScreen`, so the extra calls are not the global layer also drawing on top. `renderInternal` advances Knowledge Gain progress through `tickOncePerClientTick`, which compares player `tickCount` to `lastClientTick`; repeated calls in one tick **do not advance progress multiple times**. They *can* re-blend the same `renderBookIcon` and tracker quads within a frame when `kgFade > 0`.

The final uncertainty explains why a user may report that the Research Table appears normal: the extra rendering happens **during an active Knowledge Gain/fade animation**, not on a static empty Research Table screen. It is separate from the historical alpha fix (F36) and from the persistent Instrument HUD outlines (F37–F38).

**Reproduction:** start a Knowledge Gain event with the Research Table open, capture a video/frame sequence including the icon animation at the screen corner, compare to the same event with a different inventory screen and in the world, and count actual draw invocations via instrumented trace. Do not label ordinary Research Table background as visually broken based on these calls alone.


## Black borders: competing causes

There is **no proven universal cause** yet. Similar black pixels can originate in unrelated systems.

| Observed symptom | Confirmed static evidence | Open hypotheses |
| --- | --- | --- |
| Thick black outline around Vis/Flux/Sanity | F37 directly confirms byte-identical legacy/port `hud.png` with numerous low-alpha black border pixels; F38 confirms changed layer order and fill effects; F36 shows Research Table history altered two variables together | Incorrect source-alpha blending remains the strongest measurable hypothesis, **not a proved root cause**; also test sampling, frame/fill overlap, GUI scale and the missing additive highlight |
| Stabilizing Inlay as a black plane | Loss of legacy render-layer semantics and dynamic tint; connection-based selection shape mismatch | Wrong solid/cutout/translucent path, transparent texels, z-order, layer composition; missing tint **alone** does not explain blackness |
| Dark Cloth/Voidseer/candle/crystal icons | Selected handlers return RGB under ARGB contract; model layers are tintable | Zero high alpha, different tint consumer, premultiplication, actual item render path |
| Incorrect mob textures | Original custom mesh/UV replaced by vanilla mesh for several mobs | UV and layer mapping, geometry scale, texture dimensions, animation; may have nothing to do with blending |
| Old Research Table GUI | Specific historic commits changed its alpha/blit implementation | Potentially analogous to HUD, **not automatically the same bug** |

**Criterion for closing an outline issue:** repeatable client screenshots under matched settings, isolated affected quad/texture, verified state changes, and an explanation tied to the exact pipeline. Do not reclassify a guess as a confirmed root cause merely because it is plausible across several elements.

## What remains before completion

The investigation is **not closeable** while active-ID coverage, matched client output, and render-state causality remain incomplete. The following independent work streams remain:

1. [ ] Build a **complete active registry manifest** (items, blocks, blockstates, JSON/OBJ models, textures, block entities/BERs, equipment, entities, renderers, GUIs, overlays, particles, FX and aliases), with exact legacy counterparts and active-vs-archived status.
2. [ ] For **every active item and variant**, follow `model → parent chain → textures → tint index → color handler → overrides → registered properties → dynamic components`; compare effective first/third-person, GUI, fixed and dropped transforms and names/translation keys.
3. [ ] Reconstruct the entire original **Creative item-stack order and variants** using the ten legacy screenshots and original registrations; compare actual modern runtime tab contents, duplicates, additions, omissions, aliases and standalone blueprints.
4. [ ] For **every active block**, distinguish rendered geometry, alpha material, `render_type`, BlockColor/BER, selection shape, collision shape, occlusion, raycast and subHit. Include Hungry Chest and all special animated block entities.
5. [ ] For **every GUI/HUD**, inspect texture dimensions, actual UVs, blend state, z-order, duplicate draw calls and overlays at several GUI scales. Reproduce the thick outline and separate Research Table overdraw from the instrument HUD.
6. [ ] Exhaustively compare **entities, equipment, Curios and FX**: meshes, UV layouts, armor layers, viewer-specific rendering, animations, particles, special item glow and first/third-person states.
7. [ ] Verify **seals and aspects** end-to-end: server authoritative state, serialization, network sync, `ISealDisplayer` conditions, visibility, Shift/tooltips and every legacy ItemStack variant including non-craftable items.
8. [ ] Maintain a **per-object proof ledger** with exact code links/SHA, excluded hypotheses, screenshot/test evidence, controlled repro steps and statuses such as `not_checked` and `negative_control`.
9. [ ] Only after the finding inventory and client comparisons are reasonably complete, design fail-closed audit improvements for deterministic checks, assess false positives, and separately approve code modifications.

**Not available yet:** complete matched legacy/port gameplay screenshots, an evidence row for every active object, or a verified common/separate cause for all thick black outlines. Therefore an overall "percentage complete" would be unjustified.

## Provenance and source archive

- Main repository: [`MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port), code baseline [`72eaac17a1e1c0c51b27cb151ef7f244944c89d5`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/commit/72eaac17a1e1c0c51b27cb151ef7f244944c89d5).
- Main current-status files: `06_docs/CURRENT_TASK.md`, `06_docs/current_port_status.md`, `06_docs/repo_map.md`, `06_docs/development_workflow_chatgpt_aih.md` and migration guides.
- Prior audit material: `06_docs/audits/generated/`, `06_docs/audits/item_block_parity_visual_equivalence_completion.md`, `06_docs/rendering/rendering_model_pipeline_audit.md`, `06_docs/resources/creative_tab_order_reference.md`, `tools/audits/item-block-parity/modules/`, `07_Test_Instance_and_Comparisons/aspect_parity/`.
- The archived original report from November 2 is included **in full** as Appendix A; the November 3 continuation is included **in full** as Appendix B. These preserve source-level details, counts and evidence tables without summary loss.
- The private user-provided `chat_history.txt` was consulted for the first-pass H-series facts but **was not copied verbatim into the public repository**.
- Code snapshot counts and previous tool outputs may use different filters. Keep source SHA, method and a reproducible coverage manifest with new counts.
- A user-observed screenshot issue is a valid investigation lead but not a confirmed static root cause; tests must establish the specific failure mechanism.

---

## Appendix A: Complete original November 2 research report

This is the **unaltered source material** (apart from the collapsible section label). It preserves D01–D13, underlying code links, technical comparisons, the alpha-rendering investigation, colors, HUD, Grappler, and the original entity-renderer matrix. Read chronological updates A → B → F when a hypothesis was later rejected or refined.

<details>
<summary>Expand the full original November 2 report (253 lines; Russian archival source)</summary>

# Thaumcraft 6 → NeoForge 1.21.1: восстановленный контекст исследования дефектов

Дата фиксации: 2026-11-02. Статус: **исследование продолжается; не является итоговым аудитом**.

## 1. Цель и неизменные ограничения

Цель: максимально полно выявить отклонения игрового поведения, внешнего вида и интерфейса порта от оригинального Thaumcraft 6 (Minecraft 1.12.2 Forge), включая ещё не замеченные пользователем дефекты и общие первопричины.

- **Пока не изменять код порта, существующие аудиторы, CI, ветки и PR.** В первую очередь собрать исходные данные, доказательства и полный перечень потенциально затронутых объектов.
- Не объявлять полное соответствие на основании сборки, наличия файлов, количества совпавших записей или зелёных отчётов.
- Указывать, что доказано прямым сравнением, что обоснованно предполагается, а что требует проверки в Minecraft-клиенте.
- Не запускать и не дублировать локальные задачи AIH. Локальные незакоммиченные worktree могут отличаться от GitHub `main`.

## 2. Зафиксированный источник истины

- Репозиторий: https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port
- Проверенный `main` SHA: `72eaac17a1e1c0c51b27cb151ef7f244944c89d5` (2026-11-02, GitHub connector и локальная проверка после clone).
- Локально скачан полный read-only исследовательский снимок репозитория (184 МБ, `git status --short` пуст). Этот путь является **временным** и сам по себе не гарантирует сохранения между сессиями.
- Насчитано 9646 файлов командой `rg --files` в скачанном снимке, 307 файлов внутри `06_docs/`. Ранее в переписке упоминался охват 9662 путей, но эти величины нельзя напрямую сравнивать без идентичной методики подсчёта.
- Рабочая реализация: `05_neoforge_port/`. Оригинальные материалы: `01_original_jar/`, `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/`, `04_api_reference/`.
- Сопоставления и скриншоты: `07_Test_Instance_and_Comparisons/`.

Обязательная документация:

1. `06_docs/CURRENT_TASK.md`
2. `06_docs/current_port_status.md`
3. `06_docs/repo_map.md`
4. `06_docs/development_workflow_chatgpt_aih.md`
5. `06_docs/migration/NeoForge_legacy_migration_guide.md`
6. `06_docs/migration/migration_matrix.md`
7. `06_docs/migration/porting_order.md`

Замечание о статусе: `current_port_status.md` фиксирует технические parity-результаты и прямо не заявляет измеренного pixel parity. Его выводы не отменяют дефекты, найденные позже при сравнении с legacy.

### Сохранившиеся исходные скриншоты

Все десять файлов `Thaumcraft_1.12.2_Inventory_1.png` ... `_10.png` находятся в `07_Test_Instance_and_Comparisons/02_Thaumcraft 1.12.2 Inventory Screenshots/`. Наличие каждого файла подтверждено в GitHub и локальном снимке. Полная визуальная разметка каждого слота и порядок всех элементов **ещё не подтверждены**.

## 3. Восстановленные результаты исследовательской переписки

**Обозначения:** `К` — конкретное подтверждение в коде снимка; `П` — вывод из предыдущего исследования, требующий повторной адресной сверки; `И` — игровое/визуальное подтверждение отсутствует. Эти записи являются начальным реестром, не итоговым каталогом всех ID.

| № | Группа | Известная находка и проверяемые источники | Уровень / что осталось |
| --- | --- | --- | --- |
| D01 | Grappler | `models/item/grapple_gun.json` использует предикат `type`, но `thaumcraft/client/TCItemProperties.java` регистрирует `type` только для `PRIMORDIAL_PEARL`. Legacy: `ItemGrappleGun.java`. | **К**; проверить все состояния и клиентскую иконку (**И**). |
| D02 | Цвет одежды | `TCColorHandlers.java` возвращает для robe `DyedItemColor.getOrDefault(...)` (RGB без добавления alpha); в NeoForge 1.21.1 tint ожидается в ARGB. Порядок слоёв Voidseer/Thaumaturge различается **и в оригинале**: это само по себе не дефект. | **К** по коду RGB и оригинальному JAR; проверить фактическую окраску и рецепты окрашивания (**П/И**). |
| D03 | Прочие цвета | В `TCColorHandlers.java` константы AIR/FIRE/etc. и многие dye-коды записаны как RGB, тогда как для Nitor и аспектов alpha явно добавляется; упоминались свечи, кристаллы, листва. | **К** по исходному коду; нужен полный inventory всех регистраций/слоёв, не переносить вывод на каждую грань автоматически. |
| D04 | Динамические цвета | В моделях `inlay_dot.json`, `stabilizer.json` есть `tintindex`, а в `TCColorHandlers.java` отсутствуют регистрации соответствующих BlockColor. Legacy: `thaumcraft/client/ColorHandler.java`. Аналогично проверить filter tube, focus, caster gauntlet, golem placer, banners. | **К** для Inlay/Stabilizer; остальным требуется адресная сверка (**П/И**). |
| D05 | HUD Vis/Flux/рассудка | `TCInstrumentHud.java` использует `graphics.fill` вместо текстурного заполнения legacy. У рассудка есть дополнительный blit рамки поверх заполнения, координаты `x=1` и `x+7`; пользователем отмечены чёрные контуры. Legacy: **`thaumcraft/client/lib/events/HudHandler.java`**. | **К** по коду; природа массовых чёрных контуров **не установлена**; проверить альфу PNG, UV, blend state, sampler/atlas, порядок слоёв, пиксельное смещение и client screenshots (**И**). |
| D06 | Blueprint и Creative | Порт регистрирует 4 отдельных blueprint в `TCItems.java` и добавляет их в `TCCreativeTabOrder.java` (строки ~715–721). В предыдущей проверке legacy `ConfigItems.java`/`ConfigBlocks.java` эти самостоятельные регистрации не обнаружены. | **К** для порта, **П** для полного legacy списка. Не путать отсутствующие предметы с настоящими страницами-чертежами Thaumonomicon; сравнить все десять Creative-скриншотов (**И**). |
| D07 | Печати | `SealEntity.syncToClient(Level)` содержит только комментарий; в предыдущем исследовании не найден эквивалент legacy `RenderEventHandler.drawSeals`. | **К** для заглушки; проверить пути сетевой синхронизации, видимости при `ISealDisplayer` и сохранение на диск (**П/И**). |
| D08 | Thaumatorium и сложные блоки | Для ряда сложных моделей/предметов ранее найдены несоответствия display-трансформаций и `VoxelShape`; `.noOcclusion()` не доказывает корректности коллизии или отрисовки. | **П**; нужна предметная матрица `blockstate/model/display/shape/collision/render layer` и игровая проверка (**И**). |
| D09 | Мобы и снаряды | Ранее отмечены Eldritch Guardian/Warden/Crab, Golem, Mind Spider, Focus Mine и Golem Dart: generic или invisible renderer вместо legacy-геометрии/UV/эффекта. Legacy `ModelEldritchGuardian.java`, `ModelEldritchCrab.java`, `RenderThaumcraftGolem.java`. | **П**; сравнить все активные entity registrations/renderer mappings и gameplay FX по каждому ID (**И**). |
| D10 | Броня и Curios | Часть специальных legacy armor-моделей не восстановлена; в Curios выявлены узкие регистрации для Goggles и Curiosity Band. Не вся бижутерия обязана иметь видимый рендерер. | **П**; сделать полный wearables parity и исключить ложные срабатывания (**И**). |
| D11 | Аспекты и tooltip | `TCAspectTooltipEvents.java` ограничивает показ аспектов (Shift, контейнер, непустой ItemStack и возвращённые аспекты). Предыдущая статистика: legacy 1798, port 1987, 1139 сопоставленных и 1139 совпавших, 398 legacy без надёжного сопоставления. | **П**; проверить data provenance в `07_Test_Instance_and_Comparisons/aspect_parity/`, варианты повреждений, metadata/компоненты, настройку `showTags`. |
| D12 | Прозрачность | В последней переписке сообщалось: 253 block-модели, из них 98 с альфа-пикселями на гранях; для 77 не найден подходящий `render_type`. Возможны false positive (неактивные модели, формат рендера и наследование). | **П**, категорически не 77 подтверждённых визуальных дефектов. Нужна проверка активности и material/render path, отдельных PNG и поведения клиента. |
| D13 | Имена, Creative и модельные aliases | Ранее отмечены пропущенные language-keys, активный ID vs устаревший alias (`pattern_crafter`/`patterncrafter`) и различия порядка вкладки. | **П**; построить точный реестр активных ID и сверить legacy mapping. |

**Ключевая гипотеза по чёрным контурам:** единая причина пока не доказана. Пересечения tint/alpha, оригинального PNG, tintindex, смешивания, неверного UV, наложения фоновых текстур и выборочного `render_type` могут создавать похожий внешний симптом разными способами. Нельзя без сравнения pixel output объявить все чёрные контуры одной ARGB-ошибкой.

## 4. Известные ограничения действующих аудитов

По предыдущему анализу:

- `creative_tabs.ps1` проверяет присутствие ID, но не полное legacy-содержимое, порядок, дубли и лишние записи.
- `visual_model_transforms.ps1` проверяет наличие display-данных, но не фактический legacy-ракурс каждого display context.
- `texture_color_parity.ps1` проверяет ресурсы/цветовые свойства, но не полный runtime tint/ARGB и все отсутствующие обработчики.
- `visual_equivalence_completion.ps1` преимущественно формирует отчёт; warning/review не обязательно ломает сборку.
- `legacy_shape_parity.ps1` не должен считать `noOcclusion` или неполную JSON-модель достаточным доказательством корректного `VoxelShape`.
- Нужны cross-resource проверки: активный ID → модель → parent chain → overrides/predicates → properties → textures → tint layers → renderer/material → screenshot.

**Не менять эти скрипты до конца текущего исследовательского этапа.**

## 5. Покрытие предыдущей работы и пределы доказательства

По истории исследования сообщалось о проверке 533 item models, 264 block models, 227 blockstates, 735 Java classes/files, 175 registered blocks, примерно 466 items, 59 block classes, 33 renderers и 56/56 совпадающих entity-textures. Для части чисел метод подсчёта и raw-output в одном самостоятельном файле не сохранён. Не интерпретировать эти числа как доказательство визуальной корректности всех перечисленных объектов.

В GitHub сохранились дополнительные отчёты и скрипты, которые нужно индексировать как предшествующие источники, а не как закрытие нового аудита:

- `06_docs/audits/generated/` (parity и runtime reports)
- `06_docs/audits/item_block_parity_visual_equivalence_completion.md`
- `06_docs/rendering/rendering_model_pipeline_audit.md`
- `06_docs/resources/creative_tab_order_reference.md`
- `07_Test_Instance_and_Comparisons/aspect_parity/{dumps,reports,input,tools}/`
- `tools/audits/item-block-parity/modules/`

## 6. Что отсутствует и чего не следует предполагать

1. Не обнаружен отдельный сохранённый **полный реестр всех дефектов 1–2 ноября** с объектными строками, evidence ID и client screenshots.
2. Нет доказательств, что доступны незакоммиченные изменения из локальных AIH worktree пользователя; снимок соответствует только публичному `main` SHA.
3. Все десять legacy Creative PNG присутствуют, но не проведена воспроизводимая pixel/slot-разметка всех предметов и современных экранов.
4. Не завершена реальная клиентская матрица для GUI scale, held item first/third person, equipment, block states, entity animations, particles и network-visible seals.
5. Не доказано, что динамические render-state проблемы имеют единую первопричину.

## 7. Следующий порядок работы (только исследование)

1. Зафиксировать полный scope-manifest активных registrations, моделей, textures, screens, GUI overlays, entities, block entities, particles, equipment и их legacy-контрчастей с исключениями.
2. Для каждого ID собрать link chain и diff legacy vs port; для 13 групп выше провести ретест доказательств и классифицировать `confirmed / probable / false positive / requires client`.
3. Creative: извлечь точный оригинальный порядок и варианты из legacy registrations + 10 снимков; сверить современные вкладки, имена, алиасы и четыре blueprint.
4. Rendering: пройти все tint handlers, ARGB, tintindex, модельные parent/override, display contexts, UV, alpha, render_type и совместное поведение GUI/HUD.
5. Entity/armor/block: сверить реальные geometry, UV, states, collision, overlays, save/network visibility и эффектные слои; вести полный registry-based coverage.
6. Получить одинаковые контрольные кадры legacy и NeoForge на заданном GUI scale/шейдерах/ресурс-паках и метаданных рендера. Там, где нет runtime, оставлять явный блокер.
7. Только после полноты перечня — предложить дизайн новых fail-closed аудитов и отдельно согласовать исправления. На текущем этапе **не запускать реализацию**.

## 8. Протокол дальнейшей фиксации

Для каждой записи дефекта сохранять: `ID`, `legacy path + line + SHA/reference`, `port path + line + SHA`, `asset IDs`, `симптом`, `механизм`, `доказательство`, `неоднозначности`, `coverage`, `степень уверенности`, `тест с ожидаемым результатом`, `status`. Отдельно вести `coverage` по всем активным объектам, включая строки `no defect found` и `requires runtime`; отсутствие строки не должно означать «проверено».

Этот документ — контрольная точка восстановления контекста, а **не утверждение о завершении исследования или подтверждение каждого предварительного вывода**.

## 9. Продолжение 2026-11-02: render_type, прозрачность и чёрные поверхности

Исследование выполнено **только чтением** снимка `72eaac17`. Сверена [документация NeoForge 1.21.1](https://docs.neoforged.net/docs/1.21.1/resources/client/models/): если не задан `render_type`, происходит fallback через стандартные назначения `ItemBlockRenderTypes`, затем `minecraft:solid`. В зависимости от нужной семантики используются `cutout`, `cutout_mipped` и `translucent`. Наличие альфа-пикселя само по себе не определяет правильный слой: приоритет у конкретного поведения legacy.

### От первоначальных 77 к более узким 37

1. Из 253 `models/block/*.json` прежний поиск по целому PNG давал 77 моделей с непрозрачным по умолчанию рендером и использованием PNG, имеющих альфу.
2. Новая read-only проверка разрешила parent-модели, ссылки `#texture`, разобрала реальные `elements[].faces`, заданные и автоматически выводимые UV, и проверила пиксели, в которые попадают центры текстурных выборок. Результат: **37** моделей с альфой на используемых гранях и без собственного/унаследованного `render_type`.
3. Из них **17** имеют частичную альфу, **20** — только полностью прозрачные пиксели. Все 37 ссылаются через blockstate на реально зарегистрированные блоки, вместе затрагивают **38 уникальных зарегистрированных ID** (16 оттенков свечей используют общую модель).
4. Это **технические кандидаты**, не 37 доказанных визуальных дефектов. Остаются Minecraft-parent геометрия, нестандартные BER, фактические baked-квады и runtime-проверка. Методика использует предположения о стандартных UV при их отсутствии.

Полный набор 37 model ID из этой проверки:

`inlay_extend`, `lamp_fertility_on`, `inlay_side0`, `taint_geyser`, `arcane_ear_toggle_off`, `inlay_dot_3`, `arcane_ear_toggle_on`, `taint_fibre`, `amber_block`, `taint_growth_2`, `recharge_pedestal`, `smelter_vent`, `taint_growth_1`, `condenser`, `inlay_dot_1`, `arcane_ear_off`, `alembic`, `stabilizer`, `taint_growth_4`, `inlay_side_alt1`, `pillar_ancient`, `void_siphon`, `vis_generator`, `pillar_eldritch`, `lamp_arcane_on`, `candle`, `condenser_off`, `inlay_side1`, `arcane_ear_on`, `lamp_growth_on`, `metal_alchemical_advanced`, `amber_brick`, `infernal_furnace`, `pillar_arcane`, `inlay_dot_0`, `inlay_side_alt0`, `inlay_dot_2`.

### Прямое legacy-сопоставление слоёв (более сильное доказательство)

| Семейство | Legacy | Порт на исследованном SHA | Статус |
| --- | --- | --- | --- |
| Amber Block и Amber Brick | `ConfigBlocks.java` регистрирует `BlockTranslucent`; его `getBlockLayer()` возвращает `TRANSLUCENT` | `TCBlocks.java:224–226` создаёт обычный `Block`, модели `amber_block.json`/`amber_brick.json` не задают `render_type` | **Подтверждённая потеря legacy-слоя** |
| Stabilizer | `BlockStabilizer.java:52–53` возвращает `CUTOUT` | `stabilizer.json` содержит `tintindex`, но не `render_type`; отдельного BlockColor для Stabilizer в `TCColorHandlers.java` нет | **Подтверждены две независимые потери: слой и tint** |
| Stabilizing Inlay | `BlockInlay.java:202–203` разрешает `CUTOUT` и `TRANSLUCENT` | Дочерние `inlay_*` JSON используют альфа-области и не задают эти слои, современный tint отсутствует | **Подтверждено несовпадение пути**, точное разделение baked-слоёв требует анализа |
| Taint Fibre | `BlockTaintFibre.java:203–204` возвращает `CUTOUT` | `taint_fibre.json` и варианты `taint_growth_*` без `render_type` | **Подтверждённое несовпадение** |
| Infernal Furnace | `BlockInfernalFurnace.java:57–58` возвращает `CUTOUT_MIPPED` | `infernal_furnace.json` без `render_type` | **Подтверждённое несовпадение** |
| Jar | `BlockJar.java` возвращает `TRANSLUCENT` | `jar_normal.json`/`jar_void.json` используют `minecraft:translucent` | **Контрольный пример переноса** |
| Leaves | `BlockLeavesTC.java` наследует слой vanilla leaves | `leaves_greatwood.json`/`leaves_silverwood.json` используют `minecraft:cutout_mipped` | **Контрольный пример переноса** |
| Redstone Relay | `BlockRedstoneRelay.java` возвращает `CUTOUT` | Есть legacy-подобные JSON, но отдельной актуальной регистрации `redstone_relay` в `TCBlocks.java` не найдено | **Не засчитывать как активный дефект без регистрации** |

Прямое сравнение с оригинальным `Thaumcraft-1.12.2-6.1.BETA26.jar`: **байт-в-байт совпадают 9 PNG**: `amber_side`, `amber_top`, `amber_brick`, `stabilizer`, `inlay_connect0`, `inlay_under`, `taint_fibres`, `condenser`, `hud.png`.

Pixel facts:

- `amber_side`, `amber_top`, `amber_brick`: все 256 пикселей каждой текстуры имеют alpha **232/255**.
- `inlay_connect0`: **227 из 256** пикселей имеют alpha 0 и RGB `(0,0,0)`.
- `inlay_under`: **512** пикселей имеют alpha 0 и RGB `(0,0,0)`; есть полупрозрачные пиксели.
- `stabilizer`: 1900 пикселей с alpha 0 имеют чёрный RGB; есть и другие прозрачные/полупрозрачные точки.

Отсюда технический вывод: когда используется `solid` вместо нужного альфа-совместимого пути, прозрачные участки с чёрным RGB могут визуально проявляться как чёрные плоскости или контуры. **Это правдоподобная системная причина части сообщений пользователя, но не достаточное доказательство того, что каждый чёрный контур имеет именно эту первопричину.**

### HUD как отдельный механизм

`hud.png` **побайтно идентичен** legacy. Оригинальный `thaumcraft/client/lib/events/HudHandler.java:286–344` рисует заполнение Sanity Checker текстурными фрагментами, затем рамку и особый значок при `tw >= 100`. В порте `TCInstrumentHud.java:94–120` вместо фрагментов рисуются цветные `graphics.fill`, используется отличающееся позиционирование и не воспроизводится отдельный значок максимального warp. Следовательно, для HUD есть прямое расхождение алгоритмов; связывать его чёрный контур с отсутствующим `render_type` блочных моделей **нельзя**.

### Незавершённые проверки этого этапа

1. Пройти оставшиеся 37 по legacy-семантике индивидуально и отделить реальные несовпадения от допустимой прозрачности в атласе/UV.
2. Проверить фактические baked render types для моделей со специальным block-entity renderer и материалами.
3. Сравнить специальные состояния, вращения, GUI/inventory модели и альфа-границы в запущенном клиенте с фиксацией одинаковых условий.
4. Расширить связанный поиск на иконки, entity renderers, GUI overlays, где дефект может происходить из ARGB, наложения UV, blend-mode или другого источника, а не из block JSON.

Код порта, GitHub и действующие аудиты **не изменялись**.

## 10. Перепроверка одежды: отсеян ложный кандидат, подтверждён ARGB

Прямое сравнение `models/item/void_robe_chest.json`, `void_robe_legs.json`, `void_robe_helm.json`, `cloth_chest.json`, `cloth_legs.json` с моделями внутри оригинального JAR показало, что индексы `layer0` и `layer1` **сохранены в исходном порядке** (хотя каталог ресурсов был переименован с `items/` на `item/`). У Voidseer `layer0` уже в оригинале указывал на `*_over`, а у Cloth Robes — на основную текстуру. Предыдущая версия гипотезы «порядок слоёв мог быть сломан при переносе» **не подтверждена**.

Legacy `thaumcraft/client/ColorHandler.java:186–189` применяет цвет при `tintIndex == 0`, в остальных случаях возвращает `-1` (то есть `0xFFFFFFFF`). В порте `TCColorHandlers.java` применяет тот же индекс, но возвращает `DyedItemColor.getOrDefault(...)` без выставления непрозрачного alpha и `WHITE = 0xFFFFFF` вместо `0xFFFFFFFF` для другого слоя. `ItemRobeArmor.LEGACY_DEFAULT_COLOR = 6961280` также 24-битный RGB. Это конкретное нарушение формата ARGB в NeoForge 1.21.1; визуальную степень влияния нужно подтвердить клиентом.

Такая же проверка применима к другим возвращаемым RGB константам в `TCColorHandlers` (кристаллы, свечи, листья), но не следует автоматически объявлять каждую иконку полностью невидимой без runtime-данных. Официальное основание: https://docs.neoforged.net/docs/1.21.1/resources/client/models/#tinting

## 11. Индивидуализация 37 кандидатов: исходные JSON, legacy-классы, исключения

Контроль выполнен по исходному JAR и текущим JSON порта: нормализованы только пути `thaumcraft:blocks/` → `thaumcraft:block/`, `thaumcraft:items/` → `thaumcraft:item/`. Сравнивались **одинаково названные block-model JSON**, а не blockstate-файлы или итоговые baked-модели:

- **14/37** структурно идентичны после нормализации: `inlay_extend`, `inlay_side0`, `inlay_dot_3`, `taint_fibre`, `taint_growth_2`, `taint_growth_1`, `inlay_dot_1`, `arcane_ear_off`, `taint_growth_4`, `inlay_side_alt1`, `inlay_side1`, `inlay_dot_0`, `inlay_side_alt0`, `inlay_dot_2`.
- **9/37** имеют одноимённый legacy JSON с отличиями: **8** добавляют стандартный parent `minecraft:block/block` к прежнему JSON без `parent` (`recharge_pedestal`, `smelter_vent`, `condenser`, `stabilizer`, `void_siphon`, `vis_generator`, `candle`, `arcane_ear_on`), у `infernal_furnace` отличаются пути vanilla-textures (`blocks/lava_still` → `minecraft:block/lava_still`, `blocks/fire_layer_0` → `minecraft:block/fire_0`). Технические изменения parent/path сами по себе не доказательство дефекта; существование vanilla texture и фактический baked-результат ещё подлежат проверке.
- **14/37** не имеют *одноимённого* файла в исходном `models/block/`: `lamp_fertility_on`, `taint_geyser`, `arcane_ear_toggle_off`, `arcane_ear_toggle_on`, `amber_block`, `alembic`, `pillar_ancient`, `pillar_eldritch`, `lamp_arcane_on`, `condenser_off`, `lamp_growth_on`, `metal_alchemical_advanced`, `amber_brick`, `pillar_arcane`. У ряда из них есть legacy blockstates, OBJ или другие родственные файлы, поэтому статус **не «утраченный original asset»**; необходима сверка структуры назначения модели.

**18 из 37** имеют прямую привязку к классам с *явной* legacy-семантикой альфа-слоя:

| Группа | Количество моделей из 37 | Legacy и привязка | Результат |
| --- | ---: | --- | --- |
| `inlay_*` | 9 | `BlockInlay.canRenderInLayer`: `CUTOUT` **или** `TRANSLUCENT` | Потерян multi-layer-путь; нельзя слепо назначить единственный слой без исследования двух проходов |
| `taint_fibre` и `taint_growth_{1,2,4}` | 4 | `BlockTaintFibre.getBlockLayer`: `CUTOUT`; ростовые варианты встречаются в современном `blockstates/taint_fibre.json` | Прямое несоответствие слоя |
| `taint_geyser` | 1 | `ConfigBlocks.java:258` создаёт `new BlockTaint("taint_geyser")`; `BlockTaint.java:70–71` возвращает `CUTOUT` | **Новая подтверждённая потеря legacy-слоя** |
| `amber_block`, `amber_brick` | 2 | `BlockTranslucent`: `TRANSLUCENT` | Подтверждено ранее |
| `stabilizer` | 1 | `BlockStabilizer`: `CUTOUT` | Подтверждено ранее |
| `infernal_furnace` | 1 | `BlockInfernalFurnace`: `CUTOUT_MIPPED` | Подтверждено ранее |
| **Итого** | **18** | Статический parity-анализ | **Не равно количеству проверенных клиентом дефектов** |

При подсчёте важно, что в наборе есть `growth_1`, `growth_2`, `growth_4`, но **нет** `growth_3` (он не попал в альфа-фильтр); вместе с `taint_fibre` это четыре варианта.

**Негативный контроль:** `candle.json` фигурирует в 37, но `BlockCandle.java` в оригинале **не** переопределяет `getBlockLayer()` (наследует обычный путь). Поэтому наличие alpha-пикселя на выбранной UV-области **само по себе не подтверждает миграционную регрессию свечей из-за render_type**. Свечи остаются отдельным подтверждённым кандидатом по ARGB-tint.

Оставшиеся 19 моделей требуют индивидуальной семантической проверки и/или клиентского сравнения; считать их автоматически сломанными было бы ошибкой.

## 12. Новые доказательства по HUD: изменён не только способ рисования

`thaumcraft/client/lib/events/HudHandler.java:207–275` нормирует `base`, `vis`, `flux` по **525**, а при `vis + flux > 1` дополнительно пропорционально уменьшает оба заполнения. Порт `TCInstrumentHud.java:71–86` выбирает `capacity = max(1, aura.base())`, затем использует `vis / capacity` и `flux / capacity`, ограничивая flux оставшейся высотой. Это **конкретная потеря алгоритмической parity**, независимо от текстур, альфы и чёрной обводки.

Воспроизводимый численный сценарий для сравнения: при `base=200`, `vis=200`, `flux=0` оригинал заполнит около `64×200/525 ≈ 24.4` пикселя по вертикали, порт — `64×200/200 = 64` пикселя. Ещё порт вызывает `graphics.blit(HUD, ...)` рамки **до** заполнения Vis/Flux, тогда как оригинал накладывает рамку **после** текстурных уровней. Это отдельный порядок наложения, важный для визуальной проверки границ.

## 13. Новые доказательства по предикатам и динамическим цветам

### Grappler: отсутствует также источник состояния `loaded`

Legacy `thaumcraft/common/items/tools/ItemGrappleGun.java:32–36` регистрирует предикат **`type`** со значением `1` при NBT-флаге `loaded == 1`. При успешном запуске якоря код ставит `loaded=1`, а `onUpdate` сбрасывает его, когда активный Grapple исчезает. В порте `TCGrappleGunItem.java` нет ни записи `loaded`, ни сброса, а `TCItemProperties.java` регистрирует `thaumcraft:type` **только** для `PRIMORDIAL_PEARL`; `models/item/grapple_gun.json` по-прежнему переключает `type=0/1`. Это **две взаимосвязанные потери**: источник состояния и регистрация визуального предиката. Простой registration-only fix не восстановит legacy-поведение.

Read-only перебор `overrides[].predicate` **всех современных `models/item/*.json`** нашёл также `caster_advanced.json` с `focus` и `verdant_charm.json` с `type`, но **одноимённых активных item-регистраций** в `TCItems.java` нет. Современные `verdant_heart`, `verdant_heart_life`, `verdant_heart_sustain` напрямую ссылаются на отдельные `verdant_charm_{0,1,2}`. Поэтому **не засчитывать** отсутствие предикатов у этих двух архивных/неиспользуемых корневых моделей как установленный пользовательский дефект. Активные `caster_basic` (`thaumcraft:focus`), `jar_normal`/`jar_void` (`thaumcraft:fill`), `primordial_pearl` (`thaumcraft:type`) имеют соответствующие регистрации.

### Системный цветовой аудит, сравнение с `ColorHandler.java`

В актуальном `TCColorHandlers.java` **не обнаружены** регистрации, эквивалентные legacy обработчикам для:

- `TCItems.FOCUS_1`, `FOCUS_2`, `FOCUS_3` — legacy применяет `ItemFocus.getFocusColor(stack)`. Современный `ItemFocus.java:40–41` **реализует** `getFocusColor()` из компонента пакета, но этот метод **не подключён к ItemColor**. Модели `focus_1/2/3` используют `item/generated` и окрашиваемый слой `layer0`.
- `TCItems.CASTER_BASIC` — в legacy `itemCasterColourHandler` красит грань `tintIndex>0` цветом установленного focus; современный `caster_gauntlet_focus.json` явно содержит грани с `tintindex:1`, но обработчика ItemColor нет. Вариант `thaumcraft:focus` выбирается модельным предикатом, что **не заменяет tint handler**.
- `TCItems.GOLEM_PLACER` (ID `golem`) — legacy ItemColor использовал `props.getMaterial().itemColor`; современный предмет активен, модель `item/generated` имеет цветовой индекс `0`, но сопоставимый обработчик не зарегистрирован. Реальное извлечение материала из современных компонентов требует отдельной проверки.
- `TCBlocks.TUBE_FILTER` — legacy BlockColor красил `tintIndex==1` по `TileTubeFilter.aspectFilter`; современный `TCColorHandlers` такой регистрации не имеет. Однако надо отдельно проверить, используется ли tint на актуальном block model/BER, прежде чем объявить визуальное проявление доказанным.

Ранее подтверждённые пропуски Inlay и Stabilizer сохраняются; banner/grass-ambient/Nitor нужно **привязывать к реальным портовым регистрациям и пути рендера**: например, Nitor в порте реализован как невидимый блок с клиентским эффектом, поэтому отсутствие block-tint обработчика само по себе не доказывает отсутствия нужной окраски.

Отдельно подтверждена **систематическая смешанная конвенция формата цветов** в одном файле: константы `AIR/FIRE/WATER/EARTH/ORDER/ENTROPY/FLUX`, все 16 `DYE_*`, `WHITE=0xFFFFFF`, возврат `DyedItemColor.getOrDefault`, `FoliageColor`/`BiomeColors` без очевидного заполнения старшего байта — RGB или потенциально RGB; в то же время 16 Nitor item colors, Jar и aspect colors явно используют `0xFF...`. Это затрагивает минимум обработчики **7 типов crystal items, 16 цветов свечей, 6 предметов одежды**; листья проверить в соответствующих baked-tint путях. NeoForge 1.21.1 требует `ARGB`, в том числе для `RegisterColorHandlersEvent.Block`/`.Item` (https://docs.neoforged.net/docs/1.21.1/resources/client/models/#tinting). Конкретный эффект на видимость/оттенок фиксировать по клиентским кадрам, поскольку он может зависеть от способа смешивания и типа модели.

Все результаты этого этапа получены **без изменения кода, моделей или существующих аудиторов**. Отдельный клиентский pixel-parity и полное индивидуальное закрытие 37 кандидатов ещё не проводились.

## 14. Независимый пересмотр сущностей: 9 invisible renderers не означают 9 дефектов

Прямо сопоставлены все девять `TCInvisibleEntityRenderer` из `TCEntityRenderers.java` с регистрациями `thaumcraft/proxies/ProxyEntities.java:112–137` и реальными методами legacy-рендереров:

| Entity type в порте | Оригинальный renderer | Фактическая legacy-семантика | Уточнение статуса |
| --- | --- | --- | --- |
| `GRAPPLE` | `RenderGrapple` | Рисует `ModelGrappler`, свечение и 3D-трос/верёвку (`CoreGLE.glePolyCone`) | **Подтверждена потеря геометрии и верёвки** |
| `FOCUSMINE` | `RenderFocusMine` | Рисует `ModelGrappler` с `textures/entity/mine.png` и меняет оттенок по таймеру | **Подтверждена потеря модели и анимации** |
| `GOLEM_DART` | `RenderDart` | Рисует геометрию снаряда | **Подтверждена потеря видимого снаряда** |
| `MIND_SPIDER` | `RenderMindSpider` | Рисует SpiderModel только адресному наблюдателю (`getViewer`), с прозрачностью до 0.1, с глазным слоем | **Подтверждена потеря управляемой видимости**, нельзя заменять просто обычным постоянно видимым пауком |
| `FOCUS_PROJECTILE` | `RenderNoProjectile` | Геометрию не рисует, но вызывает `EntityFocusProjectile.renderParticle()` | **Несовпадение FX-пути**: современный `TCFocusProjectileEntity.tick()` создаёт обычные `ParticleTypes.CRIT`, требуется сравнение семантики эффекта |
| `FOCUS_CLOUD` | `RenderFocusCloud` | `renderEntityAt` пустой | **Исключение: сама невидимость соответствует legacy**, проверять прочие части эффекта отдельно |
| `TAINT_SWARM` | `RenderTaintSwarm` | `doRender` пустой | **Исключение: сама невидимость соответствует legacy** |
| `ALUMENTUM` | `RenderNoProjectile` | Специальное действие renderer предусмотрено лишь для `EntityFocusProjectile`; для Alumentum визуальных квадов нет | **Исключение по renderer**, эффект взрыва/частиц отдельно не закрыт |
| `CAUSALITY_COLLAPSER` | `RenderNoProjectile` | Аналогично Alumentum | **Исключение по renderer**, общий FX parity не закрыт |

Таким образом: **4** явные потери видимой геометрии/эффекта, **1** несовпадение рендер-зависимых частиц, **4** случая, где пустой renderer по самой геометрии **не является ошибкой**. Отдельно проверять отсутствие client-side owner/visibility синхронизации, особенно для `MIND_SPIDER` и Grapple.

### Сохранённые PNG не означают сохранение геометрии мобов

- Современный `TCEldritchGuardianRenderer` и `TCEldritchWardenRenderer` наследуют `HumanoidMobRenderer` с `new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER))`; оригинальный `RenderEldritchGuardian` применял **`ModelEldritchGuardian`**, причём к Guardian и Warden, с особым alpha-blending и distance fade для Guardian. В порте нет этого специализированного модельного/alpha-пути.
- Современный `TCEldritchCrabRenderer` использует vanilla `SpiderModel(ModelLayers.SPIDER)`; оригинальный `RenderEldritchCrab` использовал собственный `ModelEldritchCrab` с хвостом, клешнями и несколькими сегментами.
- Современный `TCGolemRenderer` использует vanilla `HumanoidModel(ModelLayers.PLAYER)` и меняет лишь texture по материалу; legacy `common/golems/client/RenderThaumcraftGolem.java:53–91` загружает `models/obj/golem_base.obj`, кеширует составные модели и отдельно рисует части/оснастку. Даже сохранённые material PNG не способны воспроизвести OBJ-геометрию и комплектацию через vanilla player model.
- Исследованные `eldritch_guardian.png`, `eldritch_warden.png`, `crab.png` в порте имеют размер **256×128** и предназначались для специфической геометрии/UV (legacy `ModelEldritchCrab` задаёт `textureWidth=128`, `textureHeight=64`). Простая подстановка этих PNG в стандартный player/spider UV-atlas не эквивалентна оригиналу и может давать смешанные/чёрные фрагменты на модели. Причину **конкретных** скриншотных чёрных контуров без client кадра всё равно нельзя считать доказанной.

### Контроль целостности прямых JSON-ссылок

Отдельный перебор всех `models/**/*.json` порта проверил ссылки с namespace `thaumcraft:` в полях `parent`, `textures`, `overrides[].model` на существование соответствующего `models/...json` или `textures/...png`. **Необнаруженных прямых ссылок по этим трём категориям — 0**. Это положительный контроль, **не** тест неверного выбора модели, косвенных OBJ/MTL-ссылок, legacy-геометрии или клиентской функциональности.

</details>

## Appendix B: Complete November 3 continuation

This is the **unaltered follow-up source material**, including N01–N16, original detailed evidence and tables. Later F01–F09 entries in the English report extend and clarify these observations.

<details>
<summary>Expand the full November 3 continuation (309 lines; Russian archival source)</summary>

# Thaumcraft 6 -> NeoForge 1.21.1: продолжение исследования визуальных дефектов

Дата контрольной точки: 2026-11-03. Статус: **продолжающееся read-only исследование, не итоговая сертификация визуального соответствия**.

## 0. Назначение и преемственность

Этот документ дополняет, но не заменяет переданный `TC6_VISUAL_AUDIT_CONTEXT_RECOVERY_2026-11-02(1).md` (253 строки, 14 разделов) и `chat_history.txt` (1364 строки). Сохраняются все находки, оговорки и доказательства этих источников. Не следует считать перечисленные ниже повторно проверенные участки доказательством завершения остальных областей.

Источник истины: [MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port). Повторная проверка ветки `main` 2026-11-03: SHA `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`, идентичен историческому снимку 2026-11-02. Все приведённые ниже URL на GitHub фиксируют этот SHA, поэтому данные не зависят от будущего движения ветки.

Оригинал: `01_original_jar/`, `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/`, при необходимости `03_self_decompiled_check/`. Порт: `05_neoforge_port/`. Документы: `06_docs/`. Снимки: `07_Test_Instance_and_Comparisons/`.

**Важный запрет:** сейчас только собираем и проверяем сведения. Не изменять Java, JSON, модели, существующие аудиторы, Git-ветки, CI и PR; не запускать AIH. Не объявлять результат клиента доказанным без воспроизводимой клиентской проверки.

## 1. Полный исходный объём исследования (запрос пользователя)

1. Порядок, состав и лишние предметы в Creative, включая legacy metadata-варианты и четыре blueprint.
2. Предметные цвета, динамические tint, иконки (в том числе Grappler, Thaumaturge, Voidseer/Darkseer), повороты GUI/first-person/third-person.
3. Неотображаемые/неправильные локализованные названия предметов и блоков.
4. Броня и Curios: модель, UV, слои, окраска на теле, видимые аксессуары.
5. Неполные и сложные блоки: геометрия, `VoxelShape`, collision, occlusion, render layer.
6. GUI/HUD: толщина чёрного контура, порядок слоёв и смещение заполнения, Focus-иконки Auromancy и вопрос о квадратном фоне.
7. Невидимые установленные печати с очками/колокольчиком, их синхронизация и сохранение.
8. Stabilizing Inlay и другие чёрные плоскости, альфа и `tintindex`.
9. Видимость аспектов именно legacy-предметов и объяснение несовпадений mapping.
10. Все мобы, снаряды и специальные визуальные эффекты, а не только совпадение PNG.

Условие полноты: для каждого активного ID строится цепочка «legacy registration -> port registration -> model/state/parent -> PNG/UV/tint -> renderer -> runtime». Отдельный статус для каждого элемента: `confirmed`, `probable`, `false_positive_for_this_mechanism`, `requires_client`, `not_checked`. Наличие файла, успешная компиляция, нулевой счётчик missing refs не означают pixel parity.

## 2. Индекс результатов, перенесённых из контрольной точки 2026-11-02

| Старый ID | Направление | Состояние на начало текущего этапа |
| --- | --- | --- |
| D01 | Grappler | `type` модельный predicate без активной регистрации и без источника/сброса `loaded`; кодовая потеря установлена. |
| D02 | Thaumaturge/Voidseer | В ItemColor передаётся 24-bit RGB вместо ARGB; порядок PNG-слоёв в legacy и порте совпадает, гипотеза перестановки слоёв снята. |
| D03 | Прочие RGB/ARGB | Crystal, Candle, dye, Foliage и другие обработчики смешивают 24- и 32-битные цвета, реальный клиентский результат ещё не зафиксирован. |
| D04 | Отсутствующие tint handlers | Inlay, Stabilizer, Focus 1/2/3, Caster Basic, Golem Placer, возможный Tube Filter; часть случаев требует проверки активной модели. |
| D05 | HUD | Алгоритмы Vis/Flux и Warp/Sanity, масштаб 525 против base, порядок рамки, текстурные сегменты и maximum-warp indicator расходятся; причина чёрной обводки открыта. |
| D06 | Creative/Blueprint | Четыре отдельные blueprint регистрации и вставка в вкладку порта; см. усиление доказательства в N06 ниже. |
| D07 | Seals | `SealEntity.syncToClient` заглушка; визуализация установленных печатей не восстановлена, проверять сохранение и сетевую область. |
| D08 | Сложные блоки | Для каждого блока нужна таблица shape/model/display/render и клиентская проверка. |
| D09 | Entity | 4 потери видимой геометрии/эффекта в invisible renderer (Grapple, Focus Mine, Golem Dart, Mind Spider); 1 расхождение FX Focus Projectile; 4 валидных исключения. Специализированные модели Guardian/Warden/Crab/Golem потеряны. |
| D10 | Armor/Curios | Частичная потеря специальных armor-моделей; дополнено точным списком 16 ID (N01). |
| D11 | Aspect tooltip | Прошлые числа 1798 legacy, 1987 port, 1139 сопоставленных/совпавших, 398 несопоставленных; нужны provenance и runtime. |
| D12 | Прозрачность | 77 -> 37 кандидатов после UV-фильтра; 18 с прямой legacy alpha-layer семантикой, 19 оставалось неразобранными; закрыты именно в аспекте legacy-layer в N04. |
| D13 | Localization/aliases | Нужен registry-based полный языковой каталог, исключая архивные неактивные alias. |

### Важные результаты предыдущего этапа, которые нельзя потерять

- В портовом дереве прямые внутренние model/texture ссылки `parent`, `textures`, `overrides[].model` с `thaumcraft:` имели 0 отсутствующих целей. Это контроль целостности, не гарантия корректности.
- Legacy и портовые `hud.png`, `stabilizer`, две `inlay`-текстуры и янтарные PNG сравнивались побайтно: для отмеченного набора совпали. Похожий чёрный контур может порождаться несколькими механизмами (solid с alpha, RGB без alpha, UV, наложение, blending).
- Оригинальный `HudHandler` использует нормирование Vis/Flux по 525 и сначала заполнение, затем рамку; порт нормирует по `base`, рисует цветные прямоугольники и меняет порядок.
- Невидимость `FOCUS_CLOUD`, `TAINT_SWARM`, `ALUMENTUM` и `CAUSALITY_COLLAPSER` не является доказанным дефектом их *геометрии* на основании legacy renderer.
- `caster_advanced` и `verdant_charm` имеют архивные predicates без одноимённой активной регистрации. Не считать их новыми багами в отсутствие доказанного runtime пути.
- Все десять legacy Creative PNG находятся в репозитории, но точная разметка слотов и сравнение с 1.21.1 всё ещё не завершены.
- Прошлые количества просмотренных моделей и классов отражают ширину статического просмотра, не pixel parity; исходный глобальный реестр со скриншотами каждого ID не сохранился.

## 3. Новые проверенные результаты 2026-11-03

### N01. Специальные модели брони: не восстановлена геометрия 16 активных предметов

**Статус:** `confirmed_static_model_path_loss`; фактическая степень UV-искажения для каждой позы: `requires_client`.

Сверены все 11 legacy-классов под `common/items/armor/`, все современные классы этого каталога, `TCArmorMaterials.java`, `TCClientExtensions.java` и регистрации в `TCItems.java`. Legacy пять классов имели `getArmorModel(...)` и выбирали `ModelRobe`, `ModelKnightArmor`, `ModelLeaderArmor` или `ModelFortressArmor` с моделями для двух масштабов. Портовые `ArmorItem` классы этих семейств такой модели не предоставляют; `TCClientExtensions` регистрирует только два fluid extension. Поиск `getHumanoidArmorModel` по репозиторию ничего не нашёл; поиск `ModelRobe` даёт только legacy и контрольную декомпиляцию.

| Legacy armor class / model | Активные ID в порте | Количество |
| --- | --- | ---: |
| `ItemCultistPlateArmor` / `ModelKnightArmor` | `crimson_plate_helm`, `crimson_plate_chest`, `crimson_plate_legs` | 3 |
| `ItemCultistRobeArmor` / `ModelRobe` | `crimson_robe_helm`, `crimson_robe_chest`, `crimson_robe_legs` | 3 |
| `ItemCultistLeaderArmor` / `ModelLeaderArmor` | `crimson_praetor_helm`, `crimson_praetor_chest`, `crimson_praetor_legs` | 3 |
| `ItemFortressArmor` / `ModelFortressArmor` | `thaumium_fortress_helm`, `thaumium_fortress_helm_goggles`, `thaumium_fortress_chest`, `thaumium_fortress_legs` | 4 |
| `ItemVoidRobeArmor` / `ModelRobe` | `void_robe_helm`, `void_robe_chest`, `void_robe_legs` | 3 |
| **Итого** | **16 активных item ID** | **16** |

Имеются 26 modern armor PNG под `textures/models/armor/`, а также legacy PNG под `textures/entity/armor/`. Присутствие PNG и настройка `ArmorMaterial.Layer` не заменяют Java-геометрию. У `ItemRobeArmor` (обычная Thaumaturge Cloth) в legacy **не** было аналогичного `getArmorModel`; неверно автоматически причислять её к этим 16. Проблема иконок Cloth/Voidseer по ARGB остаётся отдельной.

Доказательства: [modern TCItems](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java), [modern TCArmorMaterials](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCArmorMaterials.java), [modern TCClientExtensions](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientExtensions.java), [legacy armor directory](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/armor).

**Будущий тест:** 16 предметов на игроке и armor stand, front/back, crouch/walk, inner/outer layer; сохранить GUI item render отдельно от модели на теле и проверить материал/свечение.

### N02. Inhabited Zombie экипируется неверным комплектом

**Статус:** `confirmed_code_behavior_mismatch`. В legacy `EntityInhabitedZombie.onInitialSpawn` на голову ставится `ItemsTC.crimsonPlateHelm`, на chest/legs с вероятностью от сложности `crimsonPlateChest/Legs`. В порте `TCInhabitedZombieEntity.populateLegacyArmor` ставит `TCItems.VOID_HELM`, `VOID_CHEST`, `VOID_LEGS`. Это меняет силуэт, текстуры и потенциально свойства предметов.

- [legacy EntityInhabitedZombie.java, около строк 48-57](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/monster/EntityInhabitedZombie.java)
- [modern TCInhabitedZombieEntity.java, строки 57-72](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCInhabitedZombieEntity.java)

Это отдельный дефект от специального `zombie_plate_armor.png`: legacy `ItemCultistPlateArmor.getArmorTexture` выбирал для `EntityInhabitedZombie` специальную текстуру, а текущий `ArmorMaterial.Layer` статический. Проверять оба условия раздельно.

### N03. Ритуальный Cultist Cleric: потеря парения и привязки к алтарю

**Статус:** `confirmed_renderer_feature_absence`.

Legacy `RenderCultist.doRender`, если entity имеет тип `EntityCultistCleric` с `getIsRitualist()==true`, добавляет вертикальное синусоидальное покачивание и рисует полупрозрачную анимированную wispy-ленту к home position. В порте `TCCultistRenderer` ограничивается `HumanoidMobRenderer`, бронёй и текстурой; нет соответствующих bob/line. Современный `TCCultistClericEntity` имеет синхронизированный флаг `RITUALIST`, поэтому отсутствующее визуальное поведение не объясняется удалением статуса. Дальше отдельно проверить home position и клиентскую синхронизацию координат.

- [legacy RenderCultist.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/mob/RenderCultist.java)
- [modern TCCultistRenderer.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCCultistRenderer.java)
- [modern TCCultistClericEntity.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCCultistClericEntity.java)

**Негативный контроль:** `RenderCultistLeader` содержит метод `drawFloatyLine`, но не вызывает его в `doRender`; нельзя объявлять такой же пропуск у лидера по одному присутствию неиспользуемого метода.

### N04. Giant Brainy Zombie: не перенесено изменение цвета при злости

**Статус:** `confirmed_renderer_behavior_mismatch`.

Legacy `RenderBrainyZombie.preRenderScale` масштабирует Giant Brainy Zombie на `1 + anger` и дополнительно применяет `glColor3f(1, 1-q, 1-q)`, где `q=min(1, anger)/2`. Современный `TCBrainyZombieRenderer.scale` сохранил только `poseStack.scale(1+anger,...)`, tint-эквивалента нет. Нужен клиентский сценарий для трёх уровней злости.

- [legacy RenderBrainyZombie.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/mob/RenderBrainyZombie.java)
- [modern TCBrainyZombieRenderer.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBrainyZombieRenderer.java)

**Негативный контроль:** legacy `ZOMBIE_VILLAGER_TEXTURES` объявлен, но `getEntityTexture` всегда возвращал `ZOMBIE_TEXTURES`. Отсутствие переключения на `bzombievil.png` в современном renderer само по себе **не** обнаруженная регрессия.

### N05. От 37 кандидатов alpha/solid к завершённой статической классификации именно render-layer семантики

**Статус:** `layer_classification_completed` для 37 **моделей**; `runtime_pixel_parity_open`.

Из переданного документа 18/37 имеют прямое legacy правило слоя: 9 `inlay_*` (CUTOUT и TRANSLUCENT), 4 `taint_fibre/growth`, 1 `taint_geyser`, 2 amber, 1 stabilizer, 1 infernal furnace. Это действительно потерянные либо изменённые layer contracts. Оставшиеся **19/37** проверены против соответствующих legacy-классов и цепочки наследования. У них **нет специального `getBlockLayer` или `canRenderInLayer`**, следовательно, один только отсутствующий `render_type` в 1.21.1 не доказывает отклонение от legacy для этой группы.

| Семейство и модельные ID из оставшихся 19 | Количество | Legacy-класс | Статический результат |
| --- | ---: | --- | --- |
| `lamp_arcane_on`, `lamp_growth_on`, `lamp_fertility_on` | 3 | `devices/BlockLamp` -> `BlockTCDevice` | Нет собственного слоя |
| `arcane_ear_off`, `arcane_ear_on`, `arcane_ear_toggle_off`, `arcane_ear_toggle_on` | 4 | `BlockArcaneEar`, `BlockArcaneEarToggle` | Нет собственного слоя |
| `recharge_pedestal` | 1 | `BlockRechargePedestal` | Нет собственного слоя |
| `condenser`, `condenser_off` | 2 | `BlockCondenser` | Нет собственного слоя |
| `vis_generator` | 1 | `BlockVisGenerator` | Нет собственного слоя |
| `void_siphon` | 1 | `BlockVoidSiphon` | Нет собственного слоя |
| `smelter_vent` | 1 | `essentia/BlockSmelterVent` | Нет собственного слоя |
| `alembic` | 1 | `essentia/BlockAlembic` | Нет собственного слоя |
| `pillar_ancient`, `pillar_eldritch`, `pillar_arcane` | 3 | `basic/BlockPillar` | Нет собственного слоя |
| `metal_alchemical_advanced` | 1 | `basic/BlockMetalTC` | Нет собственного слоя |
| `candle` | 1 | `basic/BlockCandle` | Нет собственного слоя |
| **Итого** | **19** | | **Не доказано layer-несовпадение** |

Проверены также родители `BlockTCDevice -> BlockTCTile -> BlockTC`; они не переопределяют render layer. В `BlockMetalTC` отсутствует отдельная альфа-семантика. Legacy `BlockCandle` ранее служил негативным контролем и теперь подтверждён повторно.

**Смысл исключения:** 19 записей снимаются с очереди «подтверждённое отсутствие render_type». Они **не исключаются** из проверки baked-модели, alpha-пикселей, геометрии, BER, tint, GUI/inventory и фактического вида. В частности, `condenser.json` содержит восемь элементов с неполным объёмом, тогда как `TCCondenserBlock.SHAPE` охватывает полный 16x16x16. Но legacy `BlockCondenser` не переопределял `getBoundingBox`, так что это **кандидат на неудобную коллизию**, не автоматически подтверждённый регрессией против оригинала.

Доказательства: [legacy device classes](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices), [legacy essentia classes](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/essentia), [modern model directory](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block).

### N06. Blueprint в legacy Creative: укреплён кодовый контроль

**Статус:** `confirmed_port_extra_item_registration`; независимая визуальная разметка десяти оригинальных скриншотов `requires_client_or_image_review`.

Повторно просмотрены целиком legacy `ConfigItems.java`, `ConfigBlocks.java`, `thaumcraft/api/items/ItemsTC.java` и `CreativeTabThaumcraft.java`. В них отсутствуют самостоятельные предметы или регистрации `golem_press_blueprint`, `infusion_altar_blueprint`, `infusion_altar_ancient_blueprint`, `infusion_altar_eldritch_blueprint`. Legacy вкладка не формирует произвольный список: её класс определяет только иконку `ItemsTC.goggles`. В современном `TCCreativeTabOrder` все четыре явно добавляются в блоке около строк 715-721. По совокупности source регистрации это **дополнения порта, не подтверждённые legacy Creative items**. Страницы/чертежи в Thaumonomicon не путать с `Item` в Creative.

Дальнейшая визуальная работа со скриншотами нужна для полного *порядка* legacy Creative, но не является предпосылкой для вывода об отсутствии этих четырёх самостоятельных item-регистраций.

### N07. HUD: исключена одна из возможных причин UV-смещения

**Статус:** `negative_control_confirmed`. В `TCInstrumentHud` используется шестипараметровая форма `GuiGraphics.blit` с неявным предположением о размере исходной текстуры 256x256. Повторно прочитанный PNG `assets/thaumcraft/textures/gui/hud.png` действительно имеет заголовок IHDR **256x256** (получен из байтов исходного GitHub asset). Поэтому **неправильная базовая размерность PNG в этом overload** не объясняет наблюдаемый чёрный контур. Подозреваемыми остаются UV-регионы, вызовы двух рамок, порядок `fill/blit`, alpha/blending, координаты рамки и различные GUI-scale.

Основание о неявном размере `blit`: [официальная документация NeoForge 1.21.1 по GUI](https://docs.neoforged.net/docs/1.21.1/gui/screens/). Проверка относится к `hud.png`; нельзя автоматически распространить её на остальные GUI PNG.

### N08. Проверка фактических размеров основных GUI PNG: неверная базовая размерность не подтверждена

**Статус:** `negative_control_confirmed` для просмотренных экранов; `runtime_gui_scale_open`.

Для 18 современных GUI-классов проверены задействованные пути `GuiGraphics.blit`, для соответствующих PNG прочитаны реальные заголовки IHDR. Обычные фоновые PNG, передаваемые в `blit(..., 256, 256)`, действительно **256×256**. Два файла Thaumonomicon — `gui_researchbook.png` и `gui_researchbook_overlay.png` — имеют **512×512**, но их Git blob SHA совпадают с исходными legacy ресурсами (`8bfcbcab70120db5fc4ce0f62589d90260463ea0` и `64d201ea2db7c86bbd74c700589177b865942a09` соответственно). Оригинальный `GuiResearchPage` тоже использовал 256-координатную схему `drawTexturedModalRect`, а современный `TCThaumonomiconEntryScreen` оставил `LEGACY_RESEARCH_TEXTURE_SIZE = 256`. Поэтому обнаружение физического 512×512 PNG **само по себе не доказывает новую ошибку порта**.

Источники: [legacy GuiResearchPage](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/gui/GuiResearchPage.java), [modern TCThaumonomiconEntryScreen](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCThaumonomiconEntryScreen.java).

Вывод ограничен координатным пространством и размером исходного PNG. Не проверяет корректность всех UV, состояния blend, фильтрацию, z-order и экранные снимки. Нельзя объяснять общий чёрный контур только несоответствием PNG 256/512.

### N09. Research Table: Knowledge Gain HUD рисуется повторно

**Статус:** `confirmed_repeated_render_call`; визуальная тяжесть эффекта `requires_client`.

В `TCResearchTableScreen.render` строки **165–166** подряд вызывают `TCKnowledgeGainHud.renderOverScreen(guiGraphics, TCClientRenderTime.guiPartialTick())`. Точно такая же пара находится в `renderTooltip`, строки **191–192**. У `TCKnowledgeGainHud.renderInternal` нет защиты от повторного *рисования*: однократность `tickOncePerClientTick` ограничивает лишь изменение состояния, но не вывод книжного значка и частиц. При активном `kgFade > 0` повторные вызовы реально накладывают графику, что может менять её непрозрачность и яркость; точное число отрисовок за кадр зависит от жизненного цикла `Screen` и требует трассировки клиента.

При этом глобальный `TCKnowledgeGainHud.renderLayer` специально пропускает `TCResearchTableScreen`. Дубли **внутри самого экрана** от этого не исчезают. Не установлено, что именно они вызывают толстую чёрную рамку шкал Vis/Warp: это разные участки HUD.

Источники: [TCResearchTableScreen.java, 149–199](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java), [TCKnowledgeGainHud.java, 80–132](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java).

### N10. Focus-узел в Thaumonomicon: квадратный фон в legacy существовал, ориентация текущего кода совпадает

**Статус:** `negative_control_confirmed` для общей рамки и поворота; `requires_client` для alpha/пиксельного вида.

В оригинальном `GuiResearchBrowser` строки **617–637** до прорисовки каждого research icon выводят базовую рамку/подложку из текстуры: стандартный вариант 32×32 с UV `(80,48)`, `ROUND` использует `(144,48)`, `HEX` — `(112,48)`, hidden сдвигает V ещё на 32, а `SPIKY` добавляет внешний декор. Современный `TCThaumonomiconBrowserScreen` строки **383–397** сохранил ветку выбора базовой рамки. Следовательно, обнаружить **какой-либо квадратный фон** за Focus-иконкой Auromancy не означает автоматически регрессию: стандартная квадратная подложка была и в legacy. Для доказательства лишнего *сплошного* прямоугольника надо сравнить сам PNG и framebuffer при конкретном состоянии узла.

В `TCResearchIconRenderer.renderFocus` есть `Axis.ZP.rotationDegrees(-90)`, что соответствует исходному `GuiFocalManipulator.drawPart` с `GL11.glRotatef(90,0,0,-1)`. Старое предположение о неправильном повороте здесь **не воспроизводится на данном SHA**. Нельзя называть ориентацию текущим подтверждённым дефектом по прошлому наблюдению. Нужен отдельный тест подложки (`_effect`/`_medium`), альфы и цвета glyph.

Источники: [legacy GuiResearchBrowser](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java), [legacy GuiFocalManipulator](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java), [TCThaumonomiconBrowserScreen](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCThaumonomiconBrowserScreen.java), [TCResearchIconRenderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchIconRenderer.java).

### N11. Количественная проверка HUD: смещение, условия показа и заполнение

**Статус:** `confirmed_static_coordinate_and_behavior_mismatch`; конечный кадр `requires_client`.

Оригинал: `HudHandler.renderHuds` строки **67–111**, `renderThaumometerHud` **207–282**, `renderSanityHud` **286–334**. Порт: `TCInstrumentHud` **40–119**. Для сравнения берётся только Thaumometer в руке, без HUD палочки и без другой внешней модификации GUI.

| Элемент | Legacy (пиксели GUI) | NeoForge 1.21.1 | Доказуемое отличие |
| --- | --- | --- | --- |
| Начало рамки Thaumometer | `(3,1)` (`translate(2,0)` + quad `(1,1)`) | `(2,2)` | X −1, Y +1 |
| Верхняя граница Vis при равной нормализованной доле `f` | `74 − 64f` | `76 − round(64f)` | Около +2 px по Y, отдельно от другого нормирования |
| Маркер base | `(4,72 − 64b)` при `b=clamp(base/525)` | `(3,74 − round(64b))` | X −1, около +2 px по Y |
| Рамка Sanity, если одна | `(1,1)` | `(1,2)` | Y +1 |
| Рамка Sanity после Thaumometer | `(1,81)` после `start += 80` | `(1,84)` после `y += 82` от 2 | Y +3 |
| Начало ширины заливки Sanity | X=7, ширина 8 | X=8, ширина 8 | X +1 |

Независимо от этих координат **алгоритм тоже отличается**: legacy делит Vis/Flux на 525, использует фрагменты HUD-текстуры и blend/additive glow; порт делит на `aura.base()`, закрашивает прямоугольники ARGB, лишён соответствующей анимации и при максимальном Warp не выводит особый значок. Эти пункты уже были в D05, здесь уточнены проверяемые пиксельные положения.

Ещё два условия показа: legacy Thaumometer HUD включается только при удержании предмета и `inGameHasFocus`, а порт включает aura при **удержании Thaumometer ИЛИ наличии Goggles**, и проверяет `hideGui`, но не повторяет явную legacy-проверку фокуса. Первое — статически подтверждённое изменение поведения (возможно намеренное); второе требует реального теста экранов и событий HUD.

**Вывод для «чёрного контура»:** разная геометрия и порядок draw способны дать заметный край даже при идентичном `hud.png`, но **не доказывают единую причину чёрных обводок на всех объектах**.

Источники: [legacy HudHandler.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/lib/events/HudHandler.java), [modern TCInstrumentHud.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCInstrumentHud.java).

### N12. Arcane Ear и Arcane Ear Toggle: формы выделения и столкновения не соответствуют legacy

**Статус:** `confirmed_voxel_shape_mismatch`; сравнение клиентских контуров `requires_client`.

В исходном `BlockArcaneEar.getBoundingBox` выбранная сторона определяет AABB размером **12×6×12** в 16-пиксельных блоковых координатах, например `UP [2,0,2]..[14,6,14]`, `NORTH [2,2,10]..[14,14,16]`. Современный `TCArcaneEarBlock` возвращает **10×7×10** (`UP [3,0,3]..[13,7,13]`, `NORTH [3,3,9]..[13,13,16]`). С каждой стороны уменьшено поперечное сечение на 2 пикселя и увеличена толщина вдоль facing на 1 пиксель. Для остальных четырёх ориентаций аналогично изменены границы. `TCArcaneEarToggleBlock` наследует форму `TCArcaneEarBlock`, оригинальный Toggle также наследовал базовую форму.

Это подтверждённое расхождение selection/collision geometry. Оно **не является доказанной причиной чёрной кромки в текстуре**: `VoxelShape` и рендер поверхности — разные механизмы.

Источники: [legacy BlockArcaneEar.java, 116–138](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockArcaneEar.java), [modern TCArcaneEarBlock.java, 43–76](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCArcaneEarBlock.java).

### N13. Pattern Crafter: потеря hit-test отдельной части, не просто «куб вместо модели»

**Статус:** `confirmed_interaction_semantics_loss`.

Legacy `crafting/BlockPatternCrafter.java` **82–136** использовал `RayTracer.retraceBlock`, вычислял `subHit` по `IndexedCuboid6`, отдельно подсвечивал точную часть механизма (`getSelectedBoundingBox`, `getCuboidByFacing`) и вызывал `TilePatternCrafter.cycle()` **только** при `subHit == 0`. Современный `crafting/TCPatternCrafterBlock.java` **85–94** вызывает `crafter.cycle()` при любом `useWithoutItem`, не проверяя часть устройства, а альтернативной трассировки subHit в классе нет.

**Негативный контроль:** legacy `getBoundingBox` самого Pattern Crafter возвращал `FULL_BLOCK_AABB`, так что наличие общей полной формы на современном блоке не является проблемой **само по себе**. Утрата касается выделения конкретного subpart и условия действия, а не общей базовой коробки.

Источники: [legacy BlockPatternCrafter.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/crafting/BlockPatternCrafter.java), [modern TCPatternCrafterBlock.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCPatternCrafterBlock.java).

### N14. Warded Jar (normal/void): неверная высота формы при корректном translucent render_type

**Статус:** `confirmed_voxel_shape_mismatch`; rendered PNG/layers `negative_control_confirmed`.

Legacy `essentia/BlockJar.getBoundingBox` возвращал `(3,0,3)..(13,12,13)` в пикселях. Современный `TCWardedJarBlock.SHAPE` имеет `(3,0,3)..(13,14,13)`: выделение и по умолчанию коллизия выше оригинала на **2/16 блока**. В `TCBlocks` оба активных `JAR_NORMAL` и `JAR_VOID` создаются через `wardedJarBlock(...)`, то есть отклонение затрагивает минимум оба ID.

Параллельный контроль ресурсов: у `models/block/jar_normal.json` и `jar_void.json` установлен `"render_type": "minecraft:translucent"`, как ожидается от legacy `BlockJar.getBlockLayer = TRANSLUCENT`; модельные `elements` доходят до Y=12. Следовательно, **нельзя** приписывать этим двум банкам отсутствие прозрачного слоя по примеру 18 других кандидатных моделей. Модерновый `TCBrainJarBlock` имеет корректную по высоте форму Y=12, что дополнительно локализует расхождение.

Источники: [legacy BlockJar.java, 54–65](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java), [modern TCWardedJarBlock.java, 41–84](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java), [modern jar_normal.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/jar_normal.json), [modern jar_void.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/jar_void.json).

### N15. Stabilizing Inlay: selection shape перестала учитывать соединения

**Статус:** `confirmed_voxel_shape_mismatch`; гипотеза «shape вызывает чёрную плоскость» **не доказана**.

Legacy `devices/BlockInlay.getBoundingBox` вычислял индекс по четырём связанным сторонам и выбирал одну из **16** коробок `REDSTONE_WIRE_AABB`. Изолированная Inlay имела `(3,0,3)..(13,1,13)`; по мере соединения форма расширялась в соответствующие направления вплоть до `(0,0,0)..(16,1,16)`. В `TCInlayBlock.getShape` всегда возвращается одна форма `(0,0,0)..(16,1,16)` **даже в изоляции**. При этом state `NORTH/EAST/SOUTH/WEST` и логика связи в современном классе сохранились, то есть данные для legacy-формы есть, но функция формы их не использует. Коллизия и там и здесь пуста (non-solid inlay), отличие касается **контуров выделения и raycast в зависимости от соединений**.

Эта находка дополняет старые D04/D12 о tint и слоях Inlay, но является **другим механизмом**, не объясняющим цвет пикселей чёрной плоскости без прямого рендер-теста.

Источники: [legacy BlockInlay.java, 56–70, 368–371](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockInlay.java), [modern TCInlayBlock.java, 24–39, 97–104, 117–132](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCInlayBlock.java).

### Контрольные случаи без ложного обвинения shape

- `TCLampBlock` имеет modern Y=2..14 и X/Z=4..12, совпадает с legacy `BlockLamp` AABB.
- `TCInfusionPillarBlock` повторяет особенность legacy `BlockPillar`: обычное выделение полублока, а коллизия расширяется до двух блоков по высоте.
- `TCVoidSiphonBlock` повторяет составные AABB legacy `BlockVoidSiphon`.
- `TCInfusionCandleBlock` сохраняет форму свечи и отсутствие коллизии.
- `TCCondenserBlock` сейчас использует полную коробку, но исходный `BlockCondenser` не переопределял `getBoundingBox`: это пока **не** shape-регрессия, даже если модель заметно меньше полного куба.

### N16. Системное изменение selection/collision для блоков, у которых legacy использовал полный куб

**Статус:** `confirmed_static_geometry_behavior_delta`; оценка как дефекта или допустимого UX-улучшения требует решения о строгости parity и клиентской проверки.

Проверены исходные `BlockTC`, `BlockTCTile`, `BlockTCDevice`: у них нет общего переопределения `getBoundingBox` или `addCollisionBoxToList`. Ниже проверенные дочерние классы также не задавали специальную общую форму, кроме **Crucible**, у которого отдельно задавалась *коллизия*:

| Активный блок | Legacy: selection / collision | Порт: selection / collision | Что именно изменилось |
| --- | --- | --- | --- |
| `crucible` | Выделение full cube; коллизия из основания Y=0..5 и четырёх стенок толщиной 2 px | `TCCrucibleBlock.SHAPE` из того же основания и стенок используется для `getShape`; коллизия по умолчанию наследует эту форму | Коллизия приблизительно сохраняет исходный контур; **выделение/луч в пустую середину** больше не full cube |
| `bellows` | Full cube / full cube по умолчанию | `TCBellowsBlock`: составной `CORE_SHAPE` плюс выход по шести ориентациям и явный `getCollisionShape=shapeFor(state)` | Выделение и коллизия сужены до видимых частей |
| `centrifuge` | Full cube / full cube | `TCCentrifugeBlock`: составные `(4,0,4)..(12,4,12)`, `(2,4,2)..(14,12,14)`, `(4,12,4)..(12,16,12)` | Выделение и коллизия не заполняют углы |
| `infusion_matrix` | Full cube / full cube | `TCInfusionMatrixBlock`: крестообразная композиция четырёх коробок, наружные края доходят до 0/16 | В пустых углах selection/collision отсутствуют |
| `table` | Full cube / full cube | `TCTableBlock`: столешница и ножки, полые промежутки | Выделение и коллизия только по деталям |
| `research_table` | Full cube / full cube | `TCResearchTableBlock`: столешница и ножки, полые промежутки | Аналогичное изменение |

Это **не** шесть доказанных ошибок текстур или моделей. Для некоторых блоков более точная современная форма может быть осознанным улучшением взаимодействия. Но при строгой цели воспроизводить поведение 1.12.2 это подтверждённое отличие, которое необходимо вынести на решение, а не молча считать соответствием оригиналу. Для `crucible` особенно важно не спутать две независимые формы: оригинал имел **полное выделение** при **полой коллизии**.

Источники: [legacy BlockCrucible](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/crafting/BlockCrucible.java), [modern TCCrucibleBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCCrucibleBlock.java), [legacy BlockBellows](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockBellows.java), [modern TCBellowsBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCBellowsBlock.java), [legacy BlockCentrifuge](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/essentia/BlockCentrifuge.java), [modern TCCentrifugeBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCCentrifugeBlock.java), [legacy BlockInfusionMatrix](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/crafting/BlockInfusionMatrix.java), [modern TCInfusionMatrixBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCInfusionMatrixBlock.java), [legacy BlockTable](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/basic/BlockTable.java), [modern TCTableBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/basic/TCTableBlock.java), [legacy BlockResearchTable](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/crafting/BlockResearchTable.java), [modern TCResearchTableBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCResearchTableBlock.java).

## 4. Что удалось проверить и что остаётся открытым

| Подмножество исследования | Текущий результат | Следующий доказательный шаг |
| --- | --- | --- |
| 37 alpha/solid block model candidates | 18 имеют явную потерю legacy layer; 19 не доказывают layer regression | Проверить фактический baked render, RGB/alpha, occlusion и одинаковые кадры |
| 11 legacy armor class families | 5 особых model-классов не подключены в 16 активных ID | Полный wearables/Curios и клиентская матрица всех комплектов |
| Inhabited Zombie | Неверно экипирован `void_*` вместо `crimson_plate_*` | Проверить внешний вид, отдельный `zombie_plate_armor` texture и AI drop |
| Cultist Cleric ritualist | Флаг сохранён, bob и алтарная wispy-линия не отрисованы | Spawn sync home position и клиенты 1.12/1.21 |
| Giant Brainy Zombie | Потерян anger RGB tint | Сравнение 0 / средняя / максимальная злость |
| 4 blueprint | Нет legacy standalone registrations; есть портовые записи | Инвентарные скриншоты и полный каталог других лишних ID |
| HUD/UI | N08 исключает PNG-size как универсальное объяснение; N09 подтверждает дубли Knowledge HUD; N10 снимает ложные гипотезы о Focus; N11 уточняет координаты HUD | GUI scale, UV/alpha, layer order, pixel capture; системная причина контура **не доказана** |
| Non-full shapes | N12 Arcane Ear(+Toggle), N14 jar normal/void, N15 Inlay: подтверждённые shape-отклонения; N13 Pattern Crafter: потеря subHit; N16: 6 системных selection/collision отличий | Полный реестр остальных блоков, клиентская проверка raycast и selection outline |
| Aspect tooltip и legacy variants | База mapping неполна | Реестр активных legacy ID и provenance |
| Остальные mobs, armor, block-entities и FX | Только частичный адресный просмотр | Full registry-based parity без автоматического исключения неподтверждённых случаев |

## 5. Следующая исследовательская очередь

1. **Model/renderer inventory:** все активные entity и armor model bindings; специально исследовать held-item layers, UV и отдельные FX рендереров, сравнивать с legacy. N01-N04 добавить в object ledger.
2. **GUI/HUD:** после N08-N11 собрать полную карту active screens/overlays, проверить двойные вызовы за один кадр, texture UV, порядок blend/z; отдельно воспроизвести thick black border, который пока нельзя свести к одной причине.
3. **Block shape/alpha:** продолжить системное сравнение selection/collision/render/occlusion на всех active ID по аналогии с N12-N15, включая блоки вне исходных 37. Сопоставить экранные выделения с legacy, разделяя форму и цвет/прозрачность.
4. **Creative order и names:** поднять все десять legacy PNG, разметить item/metadata и modern tabs, отделить дополнительные 1.21 ores, заглушки и alias.
5. **Tint/ARGB:** exhaustive matching model `tintindex` -> color registrations, включая динамическое извлечение компонентов, BlockColor/ItemColor/BER; проверить фактическую прозрачность.
6. **Seals/aspects:** проверить пути server -> client -> overlay, сохранение state и условия видимости. Тестировать только когда статические предпосылки полностью собраны.
7. **Методика аудита:** не менять существующие проверки до завершения исследования; после полного покрытия отдельно предложить fail-closed дизайн и запросить согласование исправлений.

Для будущего реестра: `ID | active ID | legacy source/path/line | port source/path/line | diff | root cause | screenshots/test | evidence | status | audit blind spot`. Утверждение `not_checked` обязательно отличать от `no_defect_found`.

## 6. Техническая достоверность и границы

- Все новые выводы N01-N16 получены прямым read-only чтением GitHub-коннектором на зафиксированном SHA; исследование не запускало клиент Minecraft и не создавало/изменяло PR или веток.
- Не запускались Gradle/build/tests: текущая задача исследовательская; игровой клиент и оригинальный JAR в файловой системе этой сессии не материализованы целиком.
- 37, 18, 19 относятся к **моделям**, а не количеству уникальных блоков или подтверждённых визуальных симптомов. 16 относится к **активным item ID** специальных комплектов.
- У портового `TCArmorMaterials` есть текстуры, но это не доказательство корректного UV на собственной legacy геометрии.
- Чёрный контур HUD, чернота Inlay и тёмные модельные фрагменты мобов могут иметь разные физические причины; объединение их в одну ошибку пока было бы недоказанной гипотезой.

**Следующая точка продолжения:** от N16 перейти к остальным non-full-cube блокам и model/renderer inventory, затем к full Creative/tint/localization/Seals/aspects. Утверждения о причинах чёрной обводки проверять **по отдельным объектам**, без универсальной гипотезы. Сохранять фиксированный SHA; не менять код/аудиторы до окончания исследования.

</details>

---

**Future updates:** keep the consolidated index and append dated evidence records with stable IDs, never erase the original checkpoints or silently backdate confidence levels. Verify the current main SHA and make changes through a separate PR; do not change mod code or the audit suite before this investigation phase is complete.
