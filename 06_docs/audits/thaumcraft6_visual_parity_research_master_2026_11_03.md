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
6. [Additional source-history checks: F01–F09](#additional-source-history-checks-f01f09)
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


## Additional source-history checks: F01–F09

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

## Black borders: competing causes

There is **no proven universal cause** yet. Similar black pixels can originate in unrelated systems.

| Observed symptom | Confirmed static evidence | Open hypotheses |
| --- | --- | --- |
| Thick black outline around Vis/Flux/Sanity | `hud.png` unchanged; frame positions, fill algorithm and draw order differ; Research Table was once fixed by explicit blending | Blend state/equation, wrong alpha interpretation, overlap of quads, UV bleed/texture sampling, GUI scale, missing glow pass |
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
