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
6. [Additional source-history checks: F01–F133](#additional-source-history-checks-f01f133)
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


## Additional source-history checks: F01–F133

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



### F40 — Legacy Creative catalog: ten consecutive screenshot pages actually inspected

**Status:** `confirmed_code` for image presence, order markers and visible icon groups; `requires_client` for a named, slot-for-slot parity matrix.

All ten original images `Thaumcraft_1.12.2_Inventory_1.png` through `Thaumcraft_1.12.2_Inventory_10.png` were fetched at the pinned source commit and visually inspected (not merely found by filename). The repository's [screenshot README](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/07_Test_Instance_and_Comparisons/02_Thaumcraft%201.12.2%20Inventory%20Screenshots/README.md) identifies page 1 as the beginning and page 10 as the end of the original creative list. These images show multiple contiguous groups of variant icons and support the more specific controls below.

- Original [page 2](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/07_Test_Instance_and_Comparisons/02_Thaumcraft%201.12.2%20Inventory%20Screenshots/Thaumcraft_1.12.2_Inventory_2.png) / [page 3](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/07_Test_Instance_and_Comparisons/02_Thaumcraft%201.12.2%20Inventory%20Screenshots/Thaumcraft_1.12.2_Inventory_3.png): colored candles followed by colored banners and the Crimson Cult banner. The dark candle precedes the first banner; the banner group occupies page 3's first two rows.
- Original [page 5](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/07_Test_Instance_and_Comparisons/02_Thaumcraft%201.12.2%20Inventory%20Screenshots/Thaumcraft_1.12.2_Inventory_5.png) / [page 6](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/07_Test_Instance_and_Comparisons/02_Thaumcraft%201.12.2%20Inventory%20Screenshots/Thaumcraft_1.12.2_Inventory_6.png): mineral and crafting-material icon sequences continue across a page boundary, so checking only one screenshot loses legitimate variants.
- Original [page 6](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/07_Test_Instance_and_Comparisons/02_Thaumcraft%201.12.2%20Inventory%20Screenshots/Thaumcraft_1.12.2_Inventory_6.png) through [page 8](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/07_Test_Instance_and_Comparisons/02_Thaumcraft%201.12.2%20Inventory%20Screenshots/Thaumcraft_1.12.2_Inventory_8.png): aspect crystal and phial sequences extend over multiple pages; filled phials and the empty phial need stack-level distinction.
- Original [page 10](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/07_Test_Instance_and_Comparisons/02_Thaumcraft%201.12.2%20Inventory%20Screenshots/Thaumcraft_1.12.2_Inventory_10.png): distinct golem item icons and seal icons are visible. Registration and `getSubItems` code, not icon resemblance alone, identifies which stacks they represent.

**Limitation:** These compact original screenshots have no hover tooltip/registry-ID overlay, and they do not show a matching 1.21.1 runtime view. An icon-only comparison cannot establish exact names, NBT/component equality, slot numbers, or absence of hidden/invisible-stack entries. This finding validates a visual reference corpus, **not** a finished 400+ slot mapping.

### F41 — Sixteen dyed banners and the Crimson Cult banner absent as functional port blocks

**Status:** `confirmed_legacy_mismatch` for active static registrations and the tab output; `requires_client` for visual demonstration.

The original [ConfigBlocks.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/common/config/ConfigBlocks.java) registers **16** `BlockBannerTC` blocks and `BlockBannerTCItem` items by iterating `EnumDyeColor.values()` (lines 315–320), followed by a separate `banner_crimson_cult` block and item (lines 322–325). All **17** distinctive banner icons form the colored-banner sequence on original Creative screenshot 3.

The current [TCBlocks.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java) has **no `BANNER` block declarations**. [TCItems.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java) has 16 `CATALOG_PLACEHOLDER_BANNER*` **generic Item registrations**, without a Crimson Cult-banner counterpart in this family. [TCCreativeTabOrder.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java) does not emit these placeholders. A generic item placeholder is not a banner block, not a functional `BlockBannerTCItem`, and not a visible legacy Creative counterpart. This is a larger omission than a mere reorder or wrong model.

**Follow-up:** inventory each exact dye ID and Crimson Cult, resource sprites, banner block geometry/rotation, tile/BE renderer, placement, drops and inventory icons; match 1.12.2 and 1.21.1 client pages. Do not mistake the existence of placeholder registry IDs for restored block behavior.

### F42 — Candles and Nitor: source-based dye ordering disagrees with the explicit modern tab order

**Status:** `confirmed_legacy_mismatch` for source sequence; screenshot-backed for the candle end-points; `requires_client` for exact final slot grid.

Original [ConfigBlocks.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/common/config/ConfigBlocks.java) creates candles (lines 311–313) and Nitor (lines 327–329) by iterating the **1.12.2 `EnumDyeColor.values()`** palette order. Original screenshot 2 ends the candle sequence at page 3's first (black-candle) slot, consistent with a white-first / black-last sequence. Modern [TCCreativeTabOrder.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java) explicitly enumerates **`CANDLE_BLACK` first, `CANDLE_WHITE` last** (lines 155–185), and **`NITOR_BLACK` first, `NITOR_WHITE` last** (lines 207–240), along with a different intermediate order.

This demonstrates a creative **ordering** mismatch even if each individual colored item is registered and displayed correctly. The Nitor icon sequence requires its own screenshot-by-slot identification; do not assume its exact original slot positions solely from the candle row.

**Follow-up:** build a normalized 16-color table (legacy metadata/dye key → modern item ID) and test actual tab order and displayed tint. Do not conflate ordering with the separate incorrect-tint defects elsewhere in this report.

### F43 — The four legacy configured golem inventory variants are not emitted in the modern tab

**Status:** `confirmed_legacy_mismatch` for the creative output; `requires_client` for appearance and NBT/component equivalence.

Original [ItemGolemPlacer.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/common/golems/ItemGolemPlacer.java) overrides `getSubItems` (obfuscated as `func_150895_a`) and adds **four distinct `ItemStack`s** with `props` long tags: the default configuration and three deliberately modified heads/arms/materials. Original screenshot 10 visibly shows a four-icon golem group.

Modern [TCItems.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java) **does register** `GOLEM_PLACER` as `golem`. Modern [ItemGolemPlacer.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/golems/ItemGolemPlacer.java) can read/write `props` through `DataComponents.CUSTOM_DATA`; however, the full [Creative tab emitter](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java) **never calls** `acceptVisible(output, TCItems.GOLEM_PLACER.get())`, nor constructs any of the four configured stacks.

Thus it is not accurate to say the golem item is unregistered. Its **four original creative sample stacks are absent from this tab builder**, despite port-side component support. Another mod/event could affect runtime tabs; the final runtime result is not claimed here.

### F44 — Blank golem seal is registered as a hidden placeholder; the original tab exposes 17 seal stacks

**Status:** `confirmed_legacy_mismatch` in tab construction; `requires_client` for displayed icons and in-world effects.

Original [ItemSealPlacer.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/common/golems/seals/ItemSealPlacer.java) constructs the `seal` item with an initial `blank` variant, then adds one stack per registered seal via `getVariantNames()` / `getSubItems()`. Original [ConfigItems.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/common/config/ConfigItems.java) registers **16** seal types, so **17** Creative entries are expected, **blank first**. Modern [tab builder](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java) emits the **16** functional `SEAL_*` items but not a blank seal. [item registry](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java) registers `CATALOG_PLACEHOLDER_SEALBLANK`, but that is a generic placeholder and is not emitted.

This finding concerns the inventory catalog; it does **not** replace separate F33–F35 evidence of missing **placed seal world rendering**, synchronization and persistence.

### F45 — Missing quartz cluster entry and legacy iron-nugget tab slot

**Status:** `confirmed_legacy_mismatch` for the tab/registrations; `requires_client` for icon, stack name and behavioral mapping.

Original [ConfigItems.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/common/config/ConfigItems.java) registers `cluster` as `ItemTCBase("cluster", "iron", "gold", "copper", "tin", "silver", "lead", "cinnabar", "quartz")` (**eight** visible metadata variants). The [modern registry](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java) defines only seven standalone `CLUSTER_*` items (no `CLUSTER_QUARTZ`), and [the tab](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java) emits those seven. Existing legacy-model resources referencing `cluster_quartz` do **not** by themselves establish a registered inventory counterpart.

Original `ItemTCBase("nugget", ...)` also includes **`iron`** among its eleven visible nugget metadata variants. The modern tab emits ten corresponding material/nugget entries, but no dedicated or vanilla **iron nugget** slot there. The vanilla item already exists in 1.21.1, so this is an **original-tab membership/ordering mismatch**, not evidence that iron nuggets are absent from Minecraft.

### F46 — Empty phial appears after the aspect variants, rather than before them

**Status:** `confirmed_legacy_mismatch` in emitted Creative order; `negative_control` for a wholesale missing phial family.

Original [ItemPhial.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/common/items/consumables/ItemPhial.java) `getSubItems` **first** appends the empty metadata-0 phial and **then** one filled stack per `Aspect.aspects.values()`. The modern [tab](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java) executes `addPhialVariants(output)` (37 filled aspect variants) at line 534 and only afterwards emits `TCItems.PHIAL` at line 536. This reverses the empty/filled group boundary.

The modern registry intentionally flattens the 37 filled variants into `ItemAspectVariant` IDs backed by data components ([ItemAspectVariant.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/ItemAspectVariant.java)); therefore a one-ID-to-one-ID comparison is the wrong check. The group is largely present, but its **stack construction, semantics and order** are distinct dimensions requiring separate tests.

### F47 — Legacy golem mind and golem module metadata variants not faithfully represented in the tab

**Status:** `confirmed_legacy_mismatch` for explicit creative entries; `requires_client` for semantic/visual replacement analysis.

Original [ConfigItems.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/common/config/ConfigItems.java) registers `ItemTCBase("mind", "clockwork", "biothaumic")` and `ItemTCBase("module", "vision", "aggression")`. Because the inspected [ItemTCBase.getSubItems()](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/common/items/ItemTCBase.java) emits every metadata variant, the original catalog includes **two mind variants and two module variants**.

The modern [registry](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java) contains generic compatibility placeholders `mindclockwork`, `modvision` and `modaggression`, but these are absent from [the explicit tab output](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java); no equivalent `mindbiothaumic` field was found in inspected `TCItems.java`. The tab includes `MIND_CLOCKWORK_ADVANCED` as a separate item, which must **not** be assumed to replace either ordinary legacy mind variant without component and gameplay evidence.

### F48 — Two same-item duplicate emissions in the explicit Creative tab construction

**Status:** `confirmed_code` for duplicate source calls; `requires_client` for effective visual duplicate/exception.

Static extraction of every `acceptVisible(output, TCItems.X.get())` in the pinned [tab builder](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java) yields **422 explicit accept calls but 420 distinct symbolic item fields**. The repeat calls are:

| Symbol | Source calls | Issue to test |
| --- | --- | --- |
| `VIS_GENERATOR` | lines 300 and 352 | Same registry item accepted in two device positions |
| `ARCANE_EAR_TOGGLE` | lines 360 and 740 | Same registry item accepted in the device and later equipment/item sections |

The source evidence **does not prove** that NeoForge shows two visible slots: the tab output implementation may deduplicate, reject or handle repeated stacks differently. Verify actual client behavior and builder error logs before treating these as user-visible bugs.

### F49 — Blueprint negative control now informed by the full ten-page original screenshot sequence

**Status:** `confirmed_code` for registrations; `negative_control` on any claim that the four standalone IDs are reproduced from original `ConfigItems`; `requires_client` for definitive tooltip-based original slot labeling.

Modern [tab builder](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java) includes four standalone items at lines 715–721: `golem_press_blueprint`, `infusion_altar_blueprint`, `infusion_altar_ancient_blueprint`, `infusion_altar_eldritch_blueprint`. The original [item registration](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/common/config/ConfigItems.java) has no corresponding independent registered items. Original screenshot pages 1–10 were also examined, but their pixels lack tooltips/IDs; **they cannot conclusively prove that a similarly drawn non-blueprint item is not a blueprint**. This reinforces N06's concern without falsely claiming a complete item-by-item visual identification.

Retain two separate checks: (1) whether those four standalone registry identities were present in the original game (the inspected registrations say **no**); and (2) whether existing blueprint-like gameplay concepts should be preserved as research data, book content, or multiblock mechanics (not answered by a Creative icon screenshot).

### F50 — Creative coverage boundary and the next reproducible comparison

**Status:** `confirmed_code` for bounded source counts; `not_checked` for full runtime equivalence.

The modern tab is an explicit ordered emitter, not an automatic listing of every registered item. Its source comment says registered compatibility placeholders remain visible, yet **none of the 40 `CATALOG_PLACEHOLDER_*` fields in inspected `TCItems.java` are referenced by the current `TCCreativeTabOrder.java`**. A subset of those aliases represent active original catalog features with no functional modern replacement (especially banners), while others may intentionally be hidden aliases. Do **not** equate all 40 placeholders with 40 independent missing gameplay objects.

Next reproducible artifact: a page/row/column ledger of legacy screenshots 1–10, with item namespace ID, subtype metadata, NBT, translated name, model/tint, and the matched modern item plus components. Then capture the actual port tab at the **same GUI scale** with hover tooltip and command-sourced IDs for ambiguous icons. Static evidence above supports specific misses; it **does not justify** calling the entire creative catalog finished.



### F51 — Full modern item/block JSON-model structure audit: no direct resource-path absence

**Status:** `negative_control` for a missing modern JSON file, JSON parse error, or broken direct internal parent/texture reference as a *universal* explanation; `not_checked` for recursive runtime/model-bakery/texture-appearance parity.

On the pinned snapshot, the modern asset directories contain **533 item-model entries** (**531 JSON** plus two non-JSON assets) and **264 block-model entries** (**253 JSON** plus other resources). Every one of the **531 + 253 JSON** files was fetched and parsed successfully. Every one of the **420 distinct active item identities** explicitly emitted by `TCCreativeTabOrder` has its expected `models/item/<registry-id>.json` file (422 calls with two repeated identities; see F48). For parsed models, every direct `thaumcraft:item/*` or `thaumcraft:block/*` parent target exists in the inspected item/block model directories. Direct nonnested `thaumcraft:item/*` and `thaumcraft:block/*` texture names referenced in the inspected model JSON also exist; the nested item `seals/*` texture directory was separately listed and checked.

**Important scope:** These are **filename/direct-reference** tests. They do not validate model loader execution, resource-pack precedence, complete recursive inheritance or UV correctness, missing dynamic properties, GUI display transform equivalence, `BlockEntityRenderer` paths, tint correctness, optional dependencies, or screenshot results. Missing rendered geometry remains real despite all JSON files existing (e.g. Hungry Chest F11/F61).

A second bounded historical comparison found **157 legacy item-model directory entries** (**155 JSON** and two OBJ/MTL assets). All 157 names exist in the current item-model directory. The 155 JSON originals were fetched and structurally compared against their same-name modern counterparts. Normalize only the routine resource spelling migration `thaumcraft:items/* → thaumcraft:item/*` and the standard generated/handheld namespace; **do not infer identical visuals from a matching basename**. This negative control narrows future model audits toward content, loader behavior and render state, not wholesale file absence.

**Paths:** [modern item models](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/) · [modern block models](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/) · [legacy item models](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/resources/assets/thaumcraft/models/item/) · [visible-item emitter](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java).

### F52 — Scribing Tools migrated from the handheld preset to generated-item transforms

**Status:** `confirmed_legacy_mismatch` in item model parent; `requires_client` for measured in-hand impact.

The original [scribing_tools.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/resources/assets/thaumcraft/models/item/scribing_tools.json) has `"parent": "item/handheld"`, while [the active modern model](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/scribing_tools.json) uses `"parent": "minecraft:item/generated"`. This is **not** a mere namespace conversion: `handheld` and `generated` carry different standard transform presets. The texture name was appropriately migrated from `thaumcraft:items/scribing_tools` to `thaumcraft:item/scribing_tools`; the substantive difference is the preset.

Of the **155 comparable original JSON item models**, this was the **only parent change not explained by ordinary `item/*` → `minecraft:item/*` namespace qualification** in the source-field comparison. Do not extrapolate this to every 420 visible modern item, since many have no one-to-one legacy JSON name.

**Follow-up:** compare first-person, third-person, GUI, fixed and dropped appearances for Scribing Tools under matched client settings; establish whether a deliberate transform override elsewhere compensates for the changed parent.

### F53 — Caster texture-path migration passes a byte-level negative control

**Status:** `negative_control` for the hypothesis that the five changed Caster item-model skin locations introduced different source bitmaps; `requires_client` for final appearance.

Six original/modern same-name model JSON files change the Caster skin from an entity armor texture reference to `thaumcraft:item/caster_basic_model` or `caster_advanced_model`: `caster_basic_model`, `caster_basic_focus_model`, `caster_advanced_model`, `caster_advanced_focus_model`, and the shared `caster_gauntlet` / `caster_gauntlet_focus` parent models. Direct Git blob SHA comparisons confirm that the relocated assets are **byte-identical** to the corresponding legacy armor-skin textures:

| Skin | Legacy entity-armour texture SHA | Modern item texture SHA | Result |
| --- | --- | --- | --- |
| Basic Caster | `d4209fe2c8ba8f887d2339b0e54ffa1fea427884` | `d4209fe2c8ba8f887d2339b0e54ffa1fea427884` | Equal |
| Advanced Caster | `c2da2725b23bfe79f509e7b98877817ed8fa490e` | `c2da2725b23bfe79f509e7b98877817ed8fa490e` | Equal |

This rejects one texture-content explanation for bad Caster visuals, **not** model UV/parent/display defects. The model overrides of `caster_basic` were correctly namespaced to `thaumcraft:focus` and are registered in [TCItemProperties](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCItemProperties.java); likewise `thaumcraft:fill` for jar models and `thaumcraft:type` for Primordial Pearl. These are useful negative controls when distinguishing the independently **missing Grappler predicate** in F29.

**Sources:** [modern basic model](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/caster_basic_model.json) · [legacy basic model](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/resources/assets/thaumcraft/models/item/caster_basic_model.json) · [modern advanced model](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/caster_advanced_model.json).

### F54 — Purifying Fluid Bucket points to a vanilla water-bucket icon; historical reference is inconclusive

**Status:** `confirmed_code` for the modern model reference; `requires_client` / `not_checked` for whether it is a genuine legacy-parity defect.

Original [bucket_pure.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/resources/assets/thaumcraft/models/item/bucket_pure.json) requests `thaumcraft:items/bucket_pure`; current [bucket_pure.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/bucket_pure.json) requests `minecraft:item/water_bucket`. The original asset directory visible in this repository **also lacks a `bucket_pure.png`**, and the modern item directory lacks it too. Consequently the current substitution is **measurable**, but it is **not safe to infer** that the original game displayed a valid unique icon without inspecting the actual original JAR/runtime resource packs. The port may be compensating for a missing imported resource. Keep this as an evidence-backed unresolved rendering choice.

### F55 — Two banner JSON models retain obsolete first/third-person transform keys

**Status:** `confirmed_code` for old key spelling in resource files; **not currently a visible active-item regression**, because the functional banners are absent (F41).

Among all **531** modern item-model JSON files, only `banner.json` and `banner_crimson_cult.json` use the legacy `display.thirdperson` and `display.firstperson` keys. These were retained when the texture references were migrated to modern paths. The current usable 1.21.1 transform naming uses explicit `firstperson_righthand`, `firstperson_lefthand`, `thirdperson_righthand`, etc. This is a **dormant migration issue** to check when restoring banners, not proof of a present in-game symptom while they are not active entries.

**Sources:** [banner.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/banner.json) · [banner_crimson_cult.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/banner_crimson_cult.json) · F41.

### F56 — Nine visible Creative entries lack the effective English registry-name translation key

**Status:** `confirmed_code` for the key/registry mismatch; `requires_client` for exact final tooltip presentation and possible resource-pack overrides.

A deterministic join of (a) **420 unique** `TCCreativeTabOrder` item fields, (b) their `TCItems` registry IDs/types (including aliases) and (c) current [en_us.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/lang/en_us.json) finds **nine** entries without their normal `item.thaumcraft.<id>` or, for the actual `BlockItem`'s block, `block.thaumcraft.<id>` key. Direct inspection of [TCItems.blockItem()](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java) confirms these six block items use ordinary `new BlockItem(...)` registration; their actual [block registry IDs](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java) match the listed underscored names. No relevant `getDescriptionId` override was found in the 735 inspected mod Java sources.

| Active item ID | Effective expected translation key (absent) | Existing legacy-style alternate key, if any |
| --- | --- | --- |
| `vis_battery` | `block.thaumcraft.vis_battery` | `item.thaumcraft.visbattery` |
| `condenser` | `block.thaumcraft.condenser` | `item.thaumcraft.condenser` |
| `condenser_lattice` | `block.thaumcraft.condenser_lattice` | `item.thaumcraft.condenserlattice` |
| `condenser_lattice_dirty` | `block.thaumcraft.condenser_lattice_dirty` | no matching active key |
| `pattern_crafter` | `block.thaumcraft.pattern_crafter` | `item.thaumcraft.patterncrafter` |
| `potion_sprayer` | `block.thaumcraft.potion_sprayer` | `item.thaumcraft.potionsprayer` |
| `grapple_gun` | `item.thaumcraft.grapple_gun` | `item.thaumcraft.grapplegun` |
| `grapple_gun_tip` | `item.thaumcraft.grapple_gun_tip` | `item.thaumcraft.grappleguntip` |
| `grapple_gun_spool` | `item.thaumcraft.grapple_gun_spool` | `item.thaumcraft.grapplegunspool` |

The presence of a *similar-looking key* in JSON does not translate the active registry ID. This provides a concrete cause of raw technical names in affected inventory contexts. It is a **separate** issue from Grappler's missing model predicate (F29).

### F57 — Active Java tooltip/GUI translation calls include 23 missing literal keys

**Status:** `confirmed_code` for exact missing keys in current English JSON; `requires_client` for which paths are reachable and visible.

A source-backed static scan fetched all **735** `05_neoforge_port/src/main/java/thaumcraft/**/*.java` files (API, client, common and main entry class). It found **266** `Component.translatable(...)` calls, of which **237** start with a literal; **27** of those literal-start calls concatenate dynamic suffixes, so they are **not** counted as exact keys. Of the **210** exact literal calls, **26 call sites** referencing **23 unique keys** have no entry in `en_us.json`. Examples:

| UI / feature | Exact missing keys in active source |
| --- | --- |
| Golem Builder | `gui.golem_builder.craft`, `gui.golem_builder.missing_materials`, `gui.golem_builder.crafting`, `gui.golem_builder.ready` |
| Research Table / Thaumonomicon | `tc.card.unknown`, `tc.search.more` |
| Caster / Focus | `tc.caster.no_focus`, `tc.caster.focus`, `tc.focus.vis_cost`, `tc.focus.max_complexity`, `tc.focus.complexity`, `tc.focus_pouch.foci` |
| Golem tooltip | `golem.rank` |
| Other equipment | `item.goggles.name`, `item.charm_undying.text`, `item.charge`, `tc.lootbag`, `enchantment.special.sapgreat` |
| Special content | `tc.potion_sprayer.charges`, `tc.fluxevent.2`, `card.truth.name`, `card.truth.text`, `tile.golem_builder.name` |

Several missing keys **exist in the old `en_us.lang`** but not in modern JSON; others are brand-new modern strings with no legacy key. Source call-site verification must be followed by context-specific in-game checks; not every branch or optional screen is necessarily reachable.

**Representative sources:** [Golem Builder](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCGolemBuilderScreen.java) · [Research Table](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java) · [Focus tooltips](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/casters/ItemFocus.java) · [Grappler tooltip](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/tools/TCGrappleGunItem.java) · [Caster tooltips](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/casters/ItemCaster.java).

### F58 — All 114 original `golem.*` localization entries are missing from the modern JSON

**Status:** `confirmed_code` for complete namespace absence and active lookup construction; `requires_client` for individual UI surfaces.

The legacy [English language file](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/resources/assets/thaumcraft/lang/en_us.lang) contains **114 `golem.*` translation keys** for traits, heads, arms, legs, materials, addons, descriptions, ranks and other golem strings. The current [English JSON](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/lang/en_us.json) contains **zero** keys starting with `golem.`. This is not just an unused old namespace: current [EnumGolemTrait](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/api/golems/EnumGolemTrait.java) and part classes [GolemHead](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/api/golems/parts/GolemHead.java), [GolemArm](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/api/golems/parts/GolemArm.java), [GolemLeg](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/api/golems/parts/GolemLeg.java), [GolemMaterial](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/api/golems/parts/GolemMaterial.java), [GolemAddon](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/api/golems/parts/GolemAddon.java) actively assemble `golem.<part>.<key>` / `golem.<part>.text.<key>` and call `Component.translatable`.

Therefore the modern JSON does not supply names/descriptions to those live lookups. This is highly relevant to the user's technical-name complaint and golem equipment/variant auditing. Do not invent which entire UI panels are visible until the client pathways are exercised.

### F59 — Dynamic translation-prefix scan flags fortress masks and Pattern Crafter modes

**Status:** `confirmed_code` for missing key families; `requires_client` for exact runtime-generated suffix values.

Of **27** translation calls beginning with literal prefixes concatenated with runtime data, `tc.aspect.*`, `research.*`, `tc.research_category.*`, `tc.forbidden.level.*` and `tc.type.*` already have corresponding prefix families in `en_us.json` (this is **not** proof every computed suffix resolves). Two inspected families have **zero** JSON entries:

- [ItemFortressArmor](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/armor/ItemFortressArmor.java) builds `item.fortress_helm.mask.<index>`; the original English `.lang` includes the three mask names `.0`, `.1`, `.2`, but modern JSON has none.
- [TCPatternCrafterBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCPatternCrafterBlock.java) builds `tc.pattern_crafter.mode.<mode>`; there are no matching modern JSON keys. The legacy `.lang` also lacks this newly introduced family.

This means technical labels may arise even when the item ID's own display-name translation is present. Validate runtime suffix enumeration before counting how many exact labels are missing.

### F60 — Legacy language files are retained as `.lang`, not active 1.21.1 JSON localizations

**Status:** `confirmed_code` for resource-file formats; `confirmed_legacy_mismatch` for supplied locale coverage; excludes external resource packs.

The modern `assets/thaumcraft/lang/` directory contains **only two native JSON localizations**: `en_us.json` and `uk_ua.json`. The original `en_us.lang`, `ru_ru.lang` and other `*.lang` files remain verbatim; the compared legacy and modern `en_us.lang` are **byte-identical** (Git SHA `3083a2a5e01178c1a877b0b4bd0c5e04727b8224`). NeoForge 1.21.1 expects language mappings under `assets/<modid>/lang/<locale>.json` and falls back to US English for keys unavailable in the selected locale ([official NeoForge 1.21.1 language documentation](https://docs.neoforged.net/docs/1.21.1/resources/client/i18n/)).

Therefore the bundled `ru_ru.lang` cannot itself restore Russian-localized item names on a 1.21.1 client, and the incomplete `en_us.json` keys in F56–F59 still matter under fallback. Do not claim *no* Russian text can appear (the base game or separate resource packs may provide some). Treat localization parity as **key-level, locale-level and screen-level** audits rather than a boolean file-existence test.

### F61 — Hungry Chest wooden-cube appearance is directly specified by its active item/block model

**Status:** `confirmed_code` for active mesh and texture assignment; reinforces the missing BER diagnosis in F11; `requires_client` for final animation comparison.

Current [item model](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item/hungry_chest.json) inherits the [block model](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/hungry_chest.json), which explicitly sets both `particle` and `all` to `minecraft:block/oak_planks`. Its principal cuboid runs from `[1, 0, 1]` to `[15, 14, 15]`, with all six visible faces using the same `#all` texture. It is therefore **a non-full-size oak-plank cuboid**, not a visually articulated chest in that model path. This directly explains why the user sees a plank-like block when the client uses that model, while F11 separately established the missing legacy Hungry Chest lid/body block-entity rendering path.

A static model alone does not prove every runtime layer; compare placed-world, inventory item, breaking particles and lid animation separately. Do not mistake this texture choice for a fix to the HUD black-border problem.

### F62 — Remaining checks after the extended model/localization scan

**Status:** `not_checked` for parity completion; **no mod code or auditors changed**.

The above are carefully bounded structural results, **not a claim of 100% visual parity or perfect inventory coverage**. Remaining evidence gates now include:

1. Resolve the exact client runtime Creative stack order (including F41–F50 discrepancies) and recorded tooltip keys with identical GUI scale.
2. For visual models, verify resolved inherited transforms, `neoforge:obj` loaders, display contexts and GPU/render-type behavior despite all direct JSON references existing.
3. Reproduce Caster and Scribing Tools first-/third-person appearances, mobile/dynamic model property changes, color handlers and the phantom black contours.
4. Verify which of the 23 missing static labels and dynamic golem/mask/mode families appear in real active GUI flows; check localization files in all selected languages.
5. Preserve F29 and F33–F39 outstanding Grappler/HUD/seal causes; complete block, entity, special-equipment and particle parity by active ID.

**Cross-checks:** no reported byte-identical source texture or passing path existence test should be upgraded into a runtime visual pass. The missing-name findings require translation-key checks, not a mere `en_us.json` parse test.



### F63 — Entity-renderer registration completeness is not a visual-completeness test

**Status:** `confirmed_code` as a registration-level negative control; **not** 43/43 visual-parity completion.

A direct count at the pinned SHA finds **43 distinct legacy registrations** in [ProxyEntities.setupEntityRenderers](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/proxies/ProxyEntities.java) and **43 modern registrations** in [TCEntityRenderers.onRegisterRenderers](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCEntityRenderers.java). All 43 modern entity slots receive some renderer factory. **Nine** choose the same [invisible no-op placeholder](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCInvisibleEntityRenderer.java); **three** use vanilla `ItemEntityRenderer` or `ThrownItemRenderer`. The other 31 registrations use port-owned renderers. This is positive evidence against a simple missing-registration cause but not against missing geometry, particle calls, held items or animation.

The previously archived source-level classification still holds: **four** definite losses of original entity geometry or visibility semantics (`GRAPPLE`, `FOCUSMINE`, `GOLEM_DART`, `MIND_SPIDER`); **one** FX-path difference (`FOCUS_PROJECTILE`, where original focus-colored particles are now generic `CRIT`); and **four** legitimate invisible renderer geometries (`FOCUS_CLOUD`, `TAINT_SWARM`, `ALUMENTUM`, `CAUSALITY_COLLAPSER`), whose client-side effects require separate validation. These were already recorded in the archived Russian investigation; **do not count them as newly discovered independent defects** here.

**Guardrail:** the 43/43 number concerns renderer registrations, not matching entities' runtime spawns, texture UVs, shadow, animations or client deserialization.

### F64 — Arcane Bore retains its solid model but loses the digging laser and luminous head effect

**Status:** `confirmed_renderer_behavior_mismatch` for this renderer's absent geometry; `requires_client` for full scene composition and impact under normal digging.

Legacy [RenderArcaneBore](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/client/renderers/entity/construct/RenderArcaneBore.java) first renders its `ModelArcaneBore`, then **only when** `clientDigging && isActive() && validInventory()` constructs a glowing head sprite and an approximately five-block beam with three rotating textured quads. It draws with additive blend (`SRC_ALPHA, ONE`), depth-write disabled and `textures/misc/beam1.png`. The modern [TCArcaneBoreRenderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCArcaneBoreRenderer.java) draws legs, tripod and body parts, then exits; it contains no beam, particle quad or condition for active digging. The [modern Arcane Bore entity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCArcaneBoreEntity.java) includes digging state and world block-break events but no directly equivalent client beam renderer.

The original `beam1.png` image is **still present** under [modern assets](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/textures/misc/beam1.png); this is a missing consumer/effect, not an absent texture. Confirm under active mining, idle, obstructed and no-tool states; compare light and occlusion separately from the model's dimensions.

### F65 — Flux Rift geometry, shader, goggles visibility and stability-reactive movement are materially simplified

**Status:** `confirmed_renderer_behavior_mismatch` for inspected algorithms; `requires_client` for overall visual severity.

Legacy [RenderFluxRift](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/client/renderers/entity/RenderFluxRift.java) executes **four** rendering passes over a **three-dimensional polycone** (`CoreGLE.glePolyCone`): three additive inner/glow passes and one alpha-blended pass. It uses `ShaderHelper.endShader`, a shader callback for camera yaw and pitch, animation offsets and changing radius tied to rift stability, and optional temporary depth-test disable when the viewer wears revealing goggles.

Modern [TCFluxRiftRenderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCFluxRiftRenderer.java) draws **one four-vertex ribbon quad per segment** with `entityTranslucentEmissive`, a fixed magenta tint, static segment width scaling and vanilla end-portal texture. The renderer contains no four-pass volume, shader callback, stability-based oscillation or viewer-goggles depth behavior. It retains the concept of a segmented rift and a textured bright surface: this is **not** evidence that the Rift is entirely invisible.

**Reproduction:** equal stability, age and segment count; orbit camera around each rift, record with/without goggles and at low/high stability. A single front-facing still cannot verify three-dimensional volume parity.

### F66 — Lesser/Greater Cultist Portal lose legacy 16-frame atlas playback and state-dependent transparency

**Status:** `confirmed_renderer_behavior_mismatch` for texture UV selection, alpha math and blend mode; `requires_client` for observed appearance.

The legacy [Lesser](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/client/renderers/entity/construct/RenderCultistPortalLesser.java) and [Greater](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/client/renderers/entity/construct/RenderCultistPortalGreater.java) portal renderers select one of **16 horizontal texture frames** (`frame = 15 - (time % 16)`; U interval `frame / 16` through `(frame + 1) / 16`). Both modulate portal size, vertical stretch and opacity using activation, damage, pulse and health, and use conventional `SRC_ALPHA, ONE_MINUS_SRC_ALPHA` blending. Lesser also gates all rendering on `isActive()`.

Modern [common portal renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCCultistPortalRenderer.java) uses the same `cultist_portal.png` image but **samples the entire [0,1] U range in every frame** rather than selecting a 1/16 stripe. Its alpha is mostly fixed around `0.72 + 0.02 * pulse`, its size is based on hitbox plus pulse, and its blend is additive `SRC_ALPHA, ONE`. The modern Lesser retains a visibility gate (`isActive() || pulseForValidation() > 0`), so the error is not simply that both portals always render.

**Expected risk:** apparent frame-striping/compression, different transparency and missing health/damage-driven pulsation. Test both portal types at spawn, steady state, damaged/pulsing and despawn; the exact screenshot appearance still needs runtime confirmation.

### F67 — Advanced Crossbow Turret is rendered with the basic cuboid model instead of its dedicated animated OBJ assembly

**Status:** `confirmed_renderer_behavior_mismatch` for advanced geometry and moving parts; `requires_client` for visual screenshots.

The original [advanced turret renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/client/renderers/entity/construct/RenderTurretCrossbowAdvanced.java) loads `models/obj/crossbow_advanced.obj` and separately renders `legs`, `mech`, `box`, `shield`, `brain`, `loader`, `bow1`, `bow2`. It rotates the weapon with interpolated aim and animates the loader/bow halves using draw progress; hit wobble and minecart mounting scale are also considered. By contrast, the [original basic turret](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/client/renderers/entity/construct/RenderTurretCrossbow.java) uses `ModelCrossbow`.

The modern [TCTurretRenderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCTurretRenderer.java) binds **both** `TURRET_BASIC` and `TURRET_ADVANCED` from [the registry](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCEntityRenderers.java) to one generic crossbow `ModelPart` assembly. It switches only between `crossbow.png` and `crossbow_advanced.png`; the modern `head` rotates with aim but has no separately animated loader, bow halves, shield, brain or damage wobble. The advanced OBJ [exists in the port resources](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/obj/crossbow_advanced.obj).

The continued presence of both textures and the advanced OBJ should not be counted as visual parity. Validate basic and advanced variants separately, including reloading, firing, damage and mounting.

### F68 — Pech custom body geometry survives, but held items and Pech-specific item poses do not

**Status:** `confirmed_renderer_behavior_mismatch` for missing held-item rendering path; `requires_client` for live trade/combat poses.

The legacy [RenderPech](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/client/renderers/entity/mob/RenderPech.java) adds a dedicated [LayerHeldItemPech](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/client/renderers/entity/mob/LayerHeldItemPech.java) to its custom `ModelPech`. That layer draws both hand stacks with left/right-specific transforms, special bow positioning and adjusted offsets when sneaking. The modern [TCPechRenderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCPechRenderer.java) **does** reconstruct and animate a detailed original-inspired Pech model and chooses the three correct type-specific texture paths, but its complete `render` routine draws only the nine model parts and never calls a held-item renderer or equivalent hand-item layer.

This is a targeted loss on an otherwise substantially custom renderer, **not** a claim that Pech was replaced wholesale with a vanilla entity. Compare occupied/empty hands, bow and non-bow items, both hands, sneaking and walking; verify separately whether client entity equipment synchronization supplies the stacks.

### F69 — Taint Seed's recursive segmented motion is replaced by a fixed four-direction cross

**Status:** `confirmed_renderer_behavior_mismatch` for topology and time-dependent local transformations; `requires_client` for the final animated silhouette.

The legacy [ModelTaintSeed](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/client/renderers/models/entity/ModelTaintSeed.java) creates an **eight-segment tentacle chain** (main root plus seven linked children), applies per-segment pitch/roll wave motion, and changes overall height/scale during the early lifetime. Its recursive [ModelRendererTaintSeed](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/client/renderers/models/entity/ModelRendererTaintSeed.java) scales nested parts with trigonometric pulse and phase offsets. Both normal and Prime Seeds receive this model through [RenderTaintSeed](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/client/renderers/entity/mob/RenderTaintSeed.java).

Modern [TCTaintSeedRenderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCTaintSeedRenderer.java) builds a central cuboid plus **four fixed cardinal protrusions**. It rotates the whole root slightly and changes a single global scale based on `seed.getArea() > 1`, but does not render the original linked tentacle topology or per-child wave/scale transforms. The material texture path `textures/entity/taintseed.png` remains shared. Compare normal/Prime at spawn, idle, attacking and late-life animation; texture availability by itself is not equivalent model behavior.

### F70 — Renderer evidence gates and a resource-presence trap

**Status:** `confirmed_code` for present assets and consumers; `not_checked` for client parity.

These source-level results are additive to earlier F29–F62 and the archived D09/N04 entity checks. This pass specifically upgrades **Bore beam, Rift volume/shader, portal atlas animation, Advanced Turret mechanics, Pech held items and Taint Seed segmentation** from generic risk categories into linked and falsifiable code differences. It does **not** reopen previously resolved legitimate invisible/no-op renderers.

| Existing port resource | Legacy visible consumer | Modern corresponding renderer outcome |
| --- | --- | --- |
| `textures/misc/beam1.png` | Arcane Bore rotating digging beam | Model drawn; beam path absent in the inspected renderer |
| `models/obj/crossbow_advanced.obj` | Advanced turret articulated machinery | Both turret types use common cuboid model; OBJ not referenced there |
| `textures/misc/cultist_portal.png` | 16-frame animated portal atlas | Whole texture UV `[0,1]` used as one billboard |
| `textures/entity/taintseed.png` | Animated linked tentacle model | Same texture onto a different static cross-shaped mesh |
| `textures/entity/mine.png` | Legacy Focus Mine model | Already established: modern `FOCUSMINE` invisible renderer (archive Section 14) |

**Minimum next evidence:** paired 1.12.2/1.21.1 captures or reproducible client-state replay for every listed active entity, with camera angle, FPS, shader settings, alpha, graphics quality, equipment and animation state held constant. No runtime captures were produced in this source-only pass. The source study does **not** prove a single shared cause for thick black HUD outlines; blend changes are known in multiple independent renderers but their onscreen symptoms require separate tests.



## Focused block-geometry continuation: F71–F75 (November 3, 2026)

The following findings extend, rather than replace, N12–N16 and F10–F28. The pinned **main** code baseline is `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`; the research branch is documentation-only. These findings concern selection/raycast geometry or a branch-specific asset check. They do **not** establish pixel-color, shader, or black-border causes. Confirmed interaction differences may be deliberate modern UX choices, but strict legacy behavior must be evaluated explicitly.

### F71 — Condenser Lattice: multi-arm outline is no longer the original rectangular bounding envelope

**Active IDs:** `thaumcraft:condenser_lattice`, `thaumcraft:condenser_lattice_dirty` (both registered in `TCBlocks.java:437–438`). **Status:** `confirmed_legacy_mismatch` for the block selection shape; screenshot outcome `requires_client`.

Legacy `BlockCondenserLattice.getBoundingBox` starts from `[5,5,5]..[11,11,11]` in sixteenths, then expands the **one enclosing AABB** to the corresponding block boundary for every connected lattice side or a condenser directly below (`BlockCondenserLattice.java:159–199`). Modern `TCCondenserLatticeBlock.getShape` instead unions a 6×6×6 center with six independently connected 6-pixel-wide arms (`TCCondenserLatticeBlock.java:47–53, 86–93, 124–134`). Its six directional blockstate connections are updated, so this is not a claim that connections or all arms are absent.

With **NORTH + EAST** connections, the legacy outline encloses the entire box `[5,5,0]..[16,11,11]`; the modern outline is the union of the core, north arm and east arm, excluding the diagonal upper-right corner of that box. The difference is invisible for an isolated node or a single arm and is easy to miss in a single-position audit. For a node also connected downward, Y may extend to zero, but the diagonal X/Z gap remains. Selection/raycast behavior is code-confirmed; exact collision semantics and in-game selection outlines must be separately measured.

**Alternative explanation:** The modern L-shaped outline is an intentional tighter hitbox aligned with the model, not a broken rendering layer. **Repro:** place condenser at `(0,0,0)`, lattice at `(0,1,0)`, adjacent lattice at `(0,1,-1)` and `(1,1,0)`; compare crosshair target/outline against the original in the diagonal X/Z gap at the center node. Check connected-state stabilization, both clean/dirty versions, and one-arm negative controls.

**Sources:** [legacy lattice](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java), [modern lattice](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCCondenserLatticeBlock.java), [active registrations](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java). **Audit blind spot:** a one-block or one-arm VoxelShape test cannot detect the multi-arm envelope difference.

### F72 — Levitator: six-facing outline and control-face subHit gating lost

**Active ID:** `thaumcraft:levitator` (`TCBlocks.java:450`). **Status:** `confirmed_legacy_mismatch` for selection and interaction gating, `requires_client` for precise outline presentation.

Legacy `BlockLevitator.getBoundingBox` contracts the AABB by 2/16 on the side indicated by its `facing` (`BlockLevitator.java:93–102`). Modern `TCLevitatorBlock.getShape` always returns the full `[0,0,0]..[16,16,16]` cube regardless of facing (`TCLevitatorBlock.java:36, 55–58`). The original also uses `RayTracer.retraceBlock`, `collisionRayTrace` over `IndexedCuboid6`, `getSelectedBoundingBox`, and `subHit == 0` to gate a range adjustment (`BlockLevitator.java:64–75, 78–90, 113–125`). `TileLevitator.addTraceableCuboids` identifies the tiny control region for all six orientations (`TileLevitator.java:181–206`). The port simply calls `levitator.increaseRange(player)` on any `useWithoutItem`, without a sub-part check (`TCLevitatorBlock.java:95–103`).

**Expected original behavior:** the outline changes with orientation; the range cycles only after a hit on the specific control surface. **Current code:** full-cube outline, generic interaction across the targetable surface. **Alternative explanation:** simplified controls may be an intentional accessibility change; they are still a strict-parity delta. **Repro:** six facings, click the center control and a visually distinct housing surface, record range/message changes and compare selection outlines to 1.12.2. Perform the test with an empty hand to isolate `useWithoutItem` and do not infer the exact framebuffer highlight from static geometry alone.

**Sources:** [legacy Levitator block](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockLevitator.java), [legacy Levitator tile](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/devices/TileLevitator.java), [modern block](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCLevitatorBlock.java). **Audit blind spot:** registration, rendered-model, and generic right-click tests do not verify sub-part hit semantics.

### F73 — Infusion/Recharge Pedestals: tight multipart modern selection replaces full-cube legacy selection

**Active original counterparts:** `thaumcraft:arcane_pedestal`, `thaumcraft:ancient_pedestal`, `thaumcraft:eldritch_pedestal`, and `thaumcraft:recharge_pedestal` (`TCBlocks.java:404–416`). Modern extra `pedestal_arcane/ancient/eldritch` aliases exist, but are not counted as separate confirmed original counterparts here. **Status:** `confirmed_legacy_mismatch` for the default outline, `requires_client` for screenshots and intended-parity decision.

Original `BlockPedestal` and `BlockRechargePedestal` have `isFullCube=false` and `isOpaqueCube=false` but do **not** override `getBoundingBox`; neither `BlockTCTile` nor `BlockTCDevice` replaces the vanilla selection shape. Their legacy general outline is therefore a full cube. Modern `TCInfusionPedestalBlock.SHAPE` is a union of full-width base `[0,0,0]..[16,4,16]`, center column `[4,4,4]..[12,12,12]`, and upper cap `[2,12,2]..[14,16,14]` (`TCInfusionPedestalBlock.java:33–39, 120–123`). These are separate from F18/F24, which concern a **present** BER and a pending floating-item transform comparison.

**Repro:** point at the empty side gap above the base of each original/port pedestal, including Recharge, and compare targetability/outline. Independently test item placement/removal; a geometry mismatch does not alone prove a failed GUI, BER, or inventory action. **Alternative explanation:** tighter current geometry could be intentional. **Audit blind spot:** a `BER exists` check or `noOcclusion` flag does not certify raycast/selection behavior.

**Sources:** [legacy Pedestal](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockPedestal.java), [legacy Recharge Pedestal](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockRechargePedestal.java), [modern Pedestal](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCInfusionPedestalBlock.java).

### F74 — Negative controls from the adjacent block-shape sweep

**Status:** `negative_control` **for the specified geometry/render-shape mechanism only**; not full visual parity.

- **Everfull Urn / Water Jug:** legacy `BlockWaterJug.getBoundingBox` is `[3,0,3]..[13,16,13]`; port `TCWaterJugBlock.LEGACY_SHAPE` is numerically equal. This does not resolve F27, the separate missing water-trail FX.
- **Infernal Furnace:** original `BlockInfernalFurnace.getBoundingBox` is `[0,0,0]..[16,8,16]`; modern `TCInfernalFurnaceBlock.SHAPE` matches and is explicitly used for selection and collision.
- **Mirrors:** the original `BlockMirror` six-direction selection boxes are 2/16-thick, and `TCMirrorBlock` defines the same six corresponding boxes. This does not restore the F15 animated portal-facing visual mechanism.
- **Thaumatorium:** legacy `BlockThaumatorium.getRenderType` makes the top block `INVISIBLE` and the lower block `MODEL`; modern `TCThaumatoriumBlock.getRenderShape` preserves this split. Both modern positions use full `Shapes.block()` selection. An invisible top is not automatically a missing renderer.
- **Condenser:** legacy `BlockCondenser` has no special bounding override (despite non-full visual flags), and the modern `TCCondenserBlock` explicitly keeps a full-cube outline. Do not infer the opposite solely from the condenser model.

**Sources:** [legacy devices directory](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices), [modern devices directory](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices), [modern crafting blocks](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting). **Audit blind spot:** matching shape constants do not validate alpha blending, dynamic models, particles, or GUI.

### F75 — The unmerged remediation branch changes Hole JSON, but not the pinned visual baseline

**Status:** `confirmed_code` branch-specific difference, **not** confirmed resolution of F15. As checked on November 3, `main` remains `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`, the research head `88a7d7ddf4f2785065c0e431345c4850aaee1dc7` before this documentation update, and `codex/review-remediation-20261103` has advanced to `4515ad3f5d08fb317f1689fd49d3e3a916995da9` (**11 commits ahead of main**). This is a separate code branch; its changes are not silently included in the F71–F74 baseline.

The base `assets/thaumcraft/blockstates/hole.json` still uses legacy `forge_marker`, `inventory/normal` variants and `thaumcraft:empty`; the other branch replaces this with a 1.21-style empty variant pointing to `thaumcraft:block/hole`, and adds a model with `"elements":[]`. However `TCHoleBlock.getRenderShape` on the pinned baseline returns `RenderShape.INVISIBLE`, while the original `BlockHole` has no collision box and a zero-sized selected bounding box. The changed JSON **alone** cannot establish that the original animated Hole appearance is restored; the actual client effect path remains a separate F15 test.

The branch comparison reports changes in golem/seal rendering, network and saved-data code, including `TCSealSavedData`. Therefore F33–F35 must be interpreted as **main-baseline findings** pending independent reconciliation against that branch, not automatically called fixed. No F71–F74 target block class appears in the 132-file change list.

**Sources:** [base Hole blockstate](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/hole.json), [remediation Hole blockstate](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/4515ad3f5d08fb317f1689fd49d3e3a916995da9/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/hole.json), [remediation Hole model](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/4515ad3f5d08fb317f1689fd49d3e3a916995da9/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/hole.json), [modern Hole block](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/misc/TCHoleBlock.java). **Repro:** compare matched active Hole effects in both branches after confirming client resource loads; do not use an empty JSON element list as evidence of portal animation.

---

## Active blockstate and placement continuation: F76–F78 (November 3, 2026)

This pass inspected the contents of **all 227** `assets/thaumcraft/blockstates/*.json` files on the pinned main snapshot, not just code-search excerpts. The active-ID set was derived from the **175 distinct** `BLOCKS.register("...")` calls in `TCBlocks.java`. Direct model existence was checked against the complete top-level `models/block/` (264 JSON) and `models/item/` (533 JSON) directory listings; the `models/` root has no direct model JSON files. The distinction between direct references, chained model parents, renderer execution and actual screenshots remains essential.

### F76 — All 175 active blocks have blockstate JSON; legacy Forge syntax and unresolved direct models are almost entirely inactive

**Status:** `confirmed_code` inventory, `negative_control` against broad “every legacy blockstate file is a current visual defect” assumptions; one active exception `requires_client`.

Results of a complete pinned-file scan:

| Check | Count | Interpretation |
| --- | ---: | --- |
| Blockstate JSON files / successfully parsed JSON | 227 / 227 | Syntactic JSON integrity only; schema/runtime acceptance is separate |
| Active registered block IDs / matching blockstate files | 175 / 175 | No active-ID-to-blockstate filename absence in this inventory |
| Blockstate files without an identically named active registration | 52 | Legacy/unused or other-purpose candidates; **not all proven unreachable from every code path** |
| Files still declaring legacy `forge_marker` | 35 | 34 lack an active same-name block registration |
| Active `forge_marker` file | 1 | `hole.json`, already isolated in F75 |
| Direct `model` references across 227 files | 1,036 | Repeated entries in variants and multipart clauses, not unique models |
| Thaumcraft-namespace direct references | 940 | Checked against present block/item model filenames |
| Unresolved Thaumcraft direct model references | 85 in 30 files | **83 are in non-active-name blockstate files**; the remaining two are `thaumcraft:empty` in active `hole.json` |

The `hole.json` reference uses the old namespace-root model path `thaumcraft:empty`, while the present modern asset is `models/block/empty.json`; the same source also uses obsolete Forge-specific `normal`, `inventory` and `transform` keys. However the active `TCHoleBlock.getRenderShape` returns `RenderShape.INVISIBLE`. Thus this is a genuine stale **active resource schema** in main, but one cannot conclude that a visible cube is rendered or that it explains the missing portal FX. F75 records a separate unmerged branch correction.

**Blind spots:** existence cannot certify baked-model success, parent chains, dynamic tint, UVs, render layers, model transforms, or in-game output. Files without a matching registry name may still be indirectly referenced; do not delete them based solely on this inventory. A source/path classification is needed before deciding which 34 legacy files should eventually be retired.

**Reproduction:** run a 1.21.1 client resource reload and capture resource/model warnings for `thaumcraft:hole`; confirm that hidden/no-render block paths are handled as intended. Repeat the direct-resource scan on the remediation SHA, but do not mix its results into the pinned baseline.

**Sources:** [modern blockstates directory](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates), [modern block models](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block), [modern item models](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/item), [active registration source](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java).

### F77 — State-cardinality controls for Levitator, Mirrors, Vis Battery and both Lattice variants

**Status:** `negative_control` for missing state-model **entries** on the named objects only; transforms, geometry, dynamic visuals and legacy behavior are **not** certified.

- `levitator.json` enumerates all **12** current combinations of six `facing` values and two `enabled` values, selecting `levitator_on/off`. F72's missing control-region targeting is not explained by an absent blockstate variant.
- `mirror.json` and `mirror_essentia.json` each enumerate all **six** directions for the modern `TCMirrorBlock.FACING`. This does not restore the F15 portal/texture dynamics.
- `vis_battery.json` enumerates **11** `charge=0..10` values corresponding to `TCVisBatteryBlock.CHARGE`. Model identity and transitions must still be compared with the legacy source.
- `condenser_lattice.json` and `condenser_lattice_dirty.json` each have **seven** multipart clauses: one core plus conditionally rendered N/S/E/W/UP/DOWN arms. The dirty variant uses a separate `condenser_lattice_core_dirty`, while both refer to `condenser_lattice_side` for arms. A shared side is a factual resource choice, not by itself a confirmed dirty-texture regression. F71's **selection envelope** divergence remains independent of these present multipart model clauses.

**Test gate:** render all Levitator orientations and enabled states, both Mirror kinds at all facings, Vis Battery charges 0/5/10, and isolated/one-arm/multi-arm clean and dirty lattice positions. Compare frame captures and model rotations against 1.12.2. An exact blockstate coverage count is not pixel-level equivalence.

**Sources:** [Levitator blockstate](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/levitator.json), [Vis Battery blockstate](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/vis_battery.json), [dirty lattice blockstate](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/condenser_lattice_dirty.json).

### F78 — Levitator facing at placement: player-relative legacy rule replaced by clicked-face rule

**Active ID:** `thaumcraft:levitator`. **Status:** `confirmed_legacy_mismatch` in placement orientation input, `requires_client` for visual outcome. This is distinct from F72's outline and clickable control.

The original `BlockLevitator` inherits `BlockTCDevice.getStateForPlacement` because it implements `IBlockFacing` and does not override placement. That legacy helper chooses `EnumFacing.getDirectionFromEntityLiving(pos, placer)` when the player is not sneaking, and its **opposite** when sneaking (`BlockTCDevice.java:54–65`). The modern `TCLevitatorBlock.getStateForPlacement` instead assigns `FACING=context.getClickedFace()`, with no sneaking inversion (`TCLevitatorBlock.java:65–72`). Its subsequent effect direction uses the resulting `FACING` property in the block-entity tick, so this is not merely an inventory display-state difference.

**Alternative explanation:** a modern intuitive “face clicked” placement policy could be deliberate, but the strict legacy control scheme has changed. **Repro:** from a fixed player position and camera inclination, place on different target block faces; then repeat with sneak held. Compare resulting `facing`, visible model orientation and the Levitator effect direction in matched versions. Do not infer exact orientation from the JSON rotations alone: the placement rule is a separate data source.

**Sources:** [legacy Levitator](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockLevitator.java), [legacy device placement helper](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/BlockTCDevice.java), [modern Levitator](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCLevitatorBlock.java).

---

## Directional devices and Vis Generator continuation: F79–F83 (November 3, 2026)

This source-level pass checked additional placement-direction rules against the legacy `BlockTCDevice` base class rather than treating every 1.21.1 `FACING` property or model variant as equivalent. All links are pinned to `main` at `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`. The independent remediation branch had advanced to `5cfc7f35926e3f2b2665b63d5a7d19a247e0aa39` (12 commits ahead); its file-change list did not include the targeted Vis Generator, Potion Sprayer or Levitator block classes.

### F79 — Potion Sprayer lost the original sneak-reversed placement orientation

**Active ID:** `thaumcraft:potion_sprayer`. **Status:** `confirmed_legacy_mismatch` for the placement input rule; `requires_client` for visible orientation.

Legacy `BlockPotionSprayer` extends `BlockTCDevice` and implements `IBlockFacing`, without overriding `getStateForPlacement`. The inherited implementation sets `FACING` to `EnumFacing.getDirectionFromEntityLiving(pos, placer)` normally and its **opposite** when sneaking (`BlockTCDevice.java:54–65`). Modern `TCPotionSprayerBlock.getStateForPlacement` unconditionally uses `context.getNearestLookingDirection().getOpposite()` and sets `ENABLED=true` (`TCPotionSprayerBlock.java:61–67`). It does not inspect the secondary-use/sneaking flag.

The legacy direction derives from player/block position and posture; the new rule derives from the nearest looking direction, with no sneaking inversion. These may coincide in some normal placements but cannot be assumed equivalent in all cases. The six facing variants in `potion_sprayer.json` are present, so this is **not** missing JSON coverage.

**Repro:** in matched 1.12.2 and 1.21.1 worlds, place facing upward, downward and sideways from the same player position, first normally and then while sneaking. Compare resulting `facing`, model rotation and emitted spray direction; confirm not just the inventory icon. **Alternative:** modern placement UX could be deliberate but must be documented as a strict-parity decision.

**Sources:** [original Sprayer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockPotionSprayer.java), [legacy placement base](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/BlockTCDevice.java), [modern Sprayer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCPotionSprayerBlock.java).

### F80 — Vis Generator auto-connect priority survives, but the placement fallback changes direction semantics

**Active ID:** `thaumcraft:vis_generator`. **Status:** `confirmed_legacy_mismatch` for the no-receiver fallback; `negative_control` for the existence of neighbor capability scanning; `requires_client` for placement demonstrations.

Original `BlockVisGenerator.getStateForPlacement` scans each neighboring facing for a tile with an energy capability that can receive FE and automatically faces such a receiver (`BlockVisGenerator.java:53–67`). **Only if no receiver exists**, it delegates to `BlockTCDevice.getStateForPlacement`, inheriting player-relative facing plus the sneaking reversal. Modern `TCVisGeneratorBlock.getStateForPlacement` still scans neighbors for a receiving energy capability (`TCVisGeneratorBlock.java:74–84`) but uses `context.getClickedFace().getOpposite()` as its fallback (`:85`), without the original posture-dependent rule.

**Repro:** place with no FE receiver nearby, repeat with and without sneak and different target faces, then install a receiver on each of six sides one at a time. Record model orientation and energy-output direction. Do not mislabel the existing auto-connect capability scan as missing. **Alternative:** the fallback could have been intentionally made face-relative. A single neighbor-present placement test will mask the difference.

**Sources:** [legacy Generator block](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockVisGenerator.java), [modern Generator block](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCVisGeneratorBlock.java), [legacy placement base](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/BlockTCDevice.java).

### F81 — Vis Generator sparks exist, but the custom legacy visual is replaced by a vanilla particle

**Active ID:** `thaumcraft:vis_generator`. **Status:** `confirmed_code` effect-type divergence; pixel-level presentation `requires_client`. **Explicit negative control:** the port is **not missing sparks altogether**.

Legacy `BlockVisGenerator.randomDisplayTick` checks the facing energy capability's stored energy and dispatches `FXDispatcher.INSTANCE.spark` with randomized position and characteristic explicit color/size parameters (`BlockVisGenerator.java:70–87`). Modern `TCVisGeneratorBlock.animateTick` checks `TCVisGeneratorBlockEntity.getEnergy()>0` and emits vanilla `ParticleTypes.ELECTRIC_SPARK` with three small random velocity components (`TCVisGeneratorBlock.java:105–120`). The code path is active, but it uses a different particle implementation rather than the custom legacy `FXDispatcher.spark`. The exact appearance, lifetime and color difference needs matched frame capture; the source proves a changed mechanism, not a precise pixel delta.

**Sync control:** the modern block entity includes `energy` in `getUpdateTag`, supplies `ClientboundBlockEntityDataPacket`, and sends block updates at refilling/zero transitions (`TCVisGeneratorBlockEntity.java:46–69, 104–115`). Therefore do **not** classify this as an obviously absent energy sync path. Inspect whether actual client energy values and particle cadence match only with runtime tracing.

**Repro:** compare active output in daylight and darkness, with identical direction, nearby receiver, aura and energy states; capture spark color, size, duration, velocity and distribution over several frames. Also compare idle/empty state as a negative control.

**Sources:** [legacy FX call](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockVisGenerator.java), [modern particle](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCVisGeneratorBlock.java), [modern energy and sync](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCVisGeneratorBlockEntity.java). **Audit blind spot:** presence of `animateTick` is not equivalent to a comparison of the particle implementation.

### F82 — Vis Generator redstone-disable state and energy-production gate are absent in the modern block

**Active ID:** `thaumcraft:vis_generator`. **Status:** `confirmed_legacy_mismatch` in functional state/behavior, discovered during visual-state tracing. The visual outcome itself is `requires_client`.

Legacy `BlockVisGenerator` implements `IBlockEnabled`; its base `BlockTCDevice.updateState` sets `ENABLED = !world.isBlockPowered(pos)` during block updates (`BlockTCDevice.java:68–74`). Legacy `TileVisGenerator.update` performs recharge and energy forwarding **only** when `!world.isRemote && BlockStateUtils.isEnabled(getBlockMetadata())` (`TileVisGenerator.java:17–41`). Thus powering the legacy generator disables its operational tick path.

The modern `TCVisGeneratorBlock` declares only `FACING`, registers no `ENABLED` state, and does not implement the original redstone-update gate. Its `TCVisGeneratorBlockEntity.tickServer` calls `tickServer` regardless of neighboring redstone power, with no equivalent early return (`TCVisGeneratorBlockEntity.java:35–73`). The modern `vis_generator.json` accordingly lists only six `facing` variants. This is **not** a case of missing `enabled` model variants for a registered property; the property and its energy gating are absent altogether.

**Expected:** with redstone power applied, legacy generation/export stops; on power removal it can resume. **Modern code:** no such gate before aura drain or FE forwarding. **Repro:** use a receiving FE storage with measurable energy and a powered/unpowered lever, record energy and aura deltas over a fixed number of server ticks in both versions; check the blockstate, not merely particles. Test stored energy before applying power to isolate export gating from recharge. **Risk:** this is a real gameplay/balance difference, not only a model concern. Preserve the research-only boundary; any implementation fix requires separate authorization.

**Sources:** [legacy Generator block](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockVisGenerator.java), [legacy device state gate](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/BlockTCDevice.java), [legacy Generator tile](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/devices/TileVisGenerator.java), [modern Generator tile](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCVisGeneratorBlockEntity.java).

### F83 — Placement-rule negative controls prevent a blanket “all directional blocks lost sneak parity” conclusion

**Status:** `negative_control` for placement-rule differences on the named paths only; not full rendering parity.

- **Golem Builder:** legacy inherits horizontal player-relative `BlockTCDevice` placement, including sneaking reversal. Modern `TCGolemBuilderBlock.getStateForPlacement` explicitly tests `player.isShiftKeyDown()` and selects forward versus opposite horizontal direction. The posture inversion is present.
- **Thaumatorium:** the original inherited horizontal placement behavior; `TCThaumatoriumBlock.getStateForPlacement` uses `context.isSecondaryUseActive()` to choose the opposite or same horizontal direction. The control inversion is explicitly represented. Its separate support/multiblock preconditions are not certified by this result.
- **Smelter and Infernal Furnace:** both legacy classes override placement to face opposite the player's horizontal direction without a sneak inversion. The corresponding modern placement uses `context.getHorizontalDirection().getOpposite()`; no missing sneak toggle should be alleged.
- **Arcane Ear and Lamp:** original classes explicitly chose the clicked face (Lamp takes its opposite); modern `TCArcaneEarBlock` and `TCLampBlock` preserve those relationships at placement. This does not negate N12's Ear shape defect or prove complete model parity.

**Blind spot:** a generic “implements `IBlockFacing`” search is insufficient: overridden legacy placement methods must be checked before alleging a regression. Test each identified class's use of the base helper and compare the actual modern rule.

**Sources:** [legacy device base](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/BlockTCDevice.java), [modern Golem Builder](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCGolemBuilderBlock.java), [modern Thaumatorium](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCThaumatoriumBlock.java).

---

## Redstone-driven visual-state audit: F84–F87 (November 3, 2026)

This pass uses the original `IBlockEnabled` implementers and the actual modern block/BE consumers, not an assumption that an `enabled` JSON variant is automatically necessary. The pinned code baseline remains `main` at `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`. Results are source-level; no world was launched. The separate remediation branch at `5cfc7f35926e3f2b2665b63d5a7d19a247e0aa39` remains outside this baseline.

### F84 — Eleven active legacy enabled-block families: ten keep a modern enabled property, Vis Generator is the known exception

**Status:** `confirmed_code` inventory and `negative_control` against a claim of broad enabled-property disappearance. F82 remains the confirmed exception.

A targeted `implements IBlockEnabled` search and active registry reconciliation identifies these **eleven active legacy class families**: Arcane Ear (including Toggle), Bellows, Condenser, Dioptra, Lamp (Arcane/Growth/Fertility), Levitator, Pattern Crafter, Potion Sprayer, Smelter (Basic/Thaumium/Void), Vis Generator, and Void Siphon. The modern classes for the first **ten** declare and include `ENABLED` in their blockstate definitions. The modern `TCVisGeneratorBlock` declares only `FACING`, as separately established in F82. Legacy `BlockBrainBox` and `BlockRedstoneRelay` also implement `IBlockEnabled` but lack matching active modern block registrations and must not inflate the active-family count.

A blockstate property does **not** by itself establish correct Redstone behavior. Consumers and lifecycle events matter, and some original classes deliberately override the generic `BlockTCDevice` behavior. Test coverage must therefore record `initial placement | redstone transition | updateState override | server tick gate | saved state | model/BER/FX` per family.

**Sources:** [legacy device classes](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices), [legacy crafting blocks](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/crafting), [modern block registration](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java).

### F85 — Enabled-state consumer controls: Dioptra is manually switched; several device gates survive

**Status:** `negative_control` for the specific inspected Redstone or manual state paths. This does not certify visual animations or all transitions.

- **Dioptra:** original `BlockDioptra.updateState` is **intentionally empty** (`BlockDioptra.java:51–53`), suppressing the base `IBlockEnabled` Redstone auto-update. Original `onBlockActivated` manually toggles `ENABLED`; modern `TCDioptraBlock.useWithoutItem` also toggles `ENABLED` and has no generic Redstone neighbor handler. Calling the absent modern redstone handler a defect here would be a false positive. F12's missing 13×13 height-field visual remains independent.
- **Condenser:** modern `TCCondenserBlock.neighborChanged` computes `ENABLED=!hasNeighborSignal`, and `TCCondenserBlockEntity.tick` explicitly gates its lattice/aura work by the resulting state. The neighbor-transition and consumer gate exist.
- **Pattern Crafter:** modern `TCPatternCrafterBlock.neighborChanged` updates `ENABLED`, and the block entity checks it before server work. This does not restore F16's missing dynamic gear and mode-indicator rendering, nor N13's sub-part-specific interaction.
- **Bellows:** modern placement reads `hasNeighborSignal`, neighbor updates keep `ENABLED` consistent, and the block entity uses it in server/client animation and attached-device calculations. Missing redstone support must not be alleged solely because the 12 blockstate JSON variants reference one static `bellows` model.
- **Void Siphon:** modern placement and neighbor updates check Redstone; the server tick checks `ENABLED` before draining rifts. The separate F15 portal-style rendering gap is not resolved by this state gate.
- **Potion Sprayer:** legacy `TilePotionSprayer.update` activates spray when `!ENABLED`; modern `TCPotionSprayerBlockEntity.tickServer` retains the same **active-low** trigger condition. A powered pulse causing the spray is intentional, not a reversed-state bug.
- **Smelter:** original `BlockSmelter` overrides placement, block-added, and neighbor handlers rather than simply inheriting the base Redstone toggle. Do not generalize `BlockTCDevice.updateState` to its operation without tracing its furnace-state changes.

These are narrowly scoped checks of **code pathways**, not client-screenshot evidence. Explicitly separate state availability from trigger semantics and from animation/particles.

**Sources:** [original Dioptra](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockDioptra.java), [modern Dioptra](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCDioptraBlock.java), [modern Bellows BE](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCBellowsBlockEntity.java), [modern Potion Sprayer BE](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCPotionSprayerBlockEntity.java).

### F86 — Possible initial-powered placement divergence in Condenser, Pattern Crafter and Potion Sprayer

**Status:** `confirmed_code` for different placement/update hooks; `probable_cause` / `requires_client` for a visible or functional initial-state mismatch. **Do not call a reproduced defect yet.**

Original `BlockTCDevice.onBlockAdded` calls `updateState(world,pos,state)`, which sets `ENABLED=!world.isBlockPowered(pos)` on its `IBlockEnabled` subclasses (`BlockTCDevice.java:44–46, 68–74`). Legacy Condenser, Pattern Crafter and Potion Sprayer do not override this placement-time behavior. In the port:

1. `TCCondenserBlock` initializes `ENABLED=true`, has no `getStateForPlacement` or `onPlace` power test, and updates `ENABLED` only from `neighborChanged`.
2. `TCPatternCrafterBlock.getStateForPlacement` sets `ENABLED=true` without reading power; likewise its state correction is in `neighborChanged`.
3. `TCPotionSprayerBlock.getStateForPlacement` sets `ENABLED=true` without reading power; likewise it corrects on `neighborChanged`.

This establishes a **different source-level initialization contract**. Whether Minecraft/NeoForge placement event ordering happens to deliver a corrective neighbor notification to the just-placed block must be verified, not assumed. An already-powered adjacent block may expose an initially wrong `ENABLED` state, incorrect Condenser on/off model, or an incorrect initial device tick; those are hypotheses pending a controlled test. Conversely, placing an unpowered block and later toggling the neighbor is likely to exercise the implemented `neighborChanged` logic and can mask an initialization bug.

**Repro:** before placement, provide stable Redstone power at the target location; place each device; read `ENABLED` immediately after the placement event, after one server tick, and after a forced neighbor update. Repeat with power added **after** placement as a negative control. For Condenser capture the `condenser_off` vs `condenser` model, for Crafter measure actual work progression, and for Sprayer test a pre-charged unit to distinguish activation edges. Add an explicit block-placement lifecycle assertion only after proving the runtime effect. No code change is authorized in this research PR.

**Sources:** [legacy base onBlockAdded/updateState](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/BlockTCDevice.java), [modern Condenser block](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCCondenserBlock.java), [modern Pattern Crafter block](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCPatternCrafterBlock.java), [modern Potion Sprayer block](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCPotionSprayerBlock.java).

### F87 — The 16 active enabled-family blockstate files divide into eleven explicit on/off visuals and five shared-model cases

**Status:** `confirmed_code` resource inventory, `negative_control` against treating every missing `enabled` variant as a visible missing model. Cross-reference F82 for the one missing runtime property.

The **16 active blockstate JSON filenames** mapped to the eleven original enabled families were parsed individually:

| Class-family active IDs | JSON mapping | Interpretation |
| --- | --- | --- |
| `arcane_ear`, `arcane_ear_toggle` | 12 variants each, differentiated `enabled` and `facing`; separate on/off model files | Explicit visual state selection exists |
| `lamp_arcane`, `lamp_growth`, `lamp_fertility` | Eight multipart clauses each, conditional on enabled/facing and including the stand | Explicit on/off visuals exist |
| `smelter_basic`, `smelter_thaumium`, `smelter_void` | Eight variants each; separate on/off models | Explicit state visuals exist |
| `condenser`, `dioptra`, `levitator` | Two, two, twelve variants respectively; explicit enabled-dependent models | Explicit state visuals exist |
| `bellows`, `pattern_crafter`, `potion_sprayer`, `void_siphon` | Enabled is registered but blockstate JSON does not select distinct models by that property | Not automatically a defect: animated BER, separate particles or visually invariant geometry may own the change |
| `vis_generator` | Six facing-only variants | No registered `ENABLED` property; the functional state loss is F82, not a missing JSON variant for an existing property |

The resource count is **11 explicit enabled-selecting files and 5 without** (four still owning the runtime property, plus Vis Generator). This count measures model-selection declarations, not frame-level parity.

**Test gate:** compare on/off rendered states and particle/BER updates for each class family, with the enabled transition triggered both before/after placement and through actual controls (manual Dioptra versus Redstone-gated devices). Do not require every animated block to implement separate JSON models when the legacy effect can be faithfully handled elsewhere.

**Sources:** [active blockstates directory](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates), [modern Vis Generator](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCVisGeneratorBlock.java).

---

### F88 — Barrier Paving Stone has lost its three original context-sensitive rune-emission branches

**Status:** `confirmed_legacy_mismatch` for the inspected client particle-emission path; visual reproduction pending.

- Legacy `BlockPavingStone.randomDisplayTick` gates this behavior on `pavingStoneBarrier`. It spawns four colored runes when redstone-powered; six differently colored runes when a passable Barrier block appears one or two blocks above; otherwise, a nearby non-player living entity can trigger a further set of runes. The trigger cases and colors are distinct, not a generic idle particle.
- Active `PAVING_STONE_BARRIER` is registered with the plain `stoneBlock(2.0F, 10.0F)` helper, which instantiates `new Block(...)`, without a block `animateTick` implementation. No separate paving-stone emitter was identified in the inspected client FX/event paths.
- The existing static texture, including legacy CTM metadata, is not a substitute for these conditional, world-space particles.

**Test gate:** client capture under (1) unpowered/clear, (2) redstone-powered, (3) legacy Barrier-above configuration when the equivalent block exists, and (4) adjacent non-player mob; compare both sprite/color and conditions. Treat inapplicable Barrier-above content as an explicit missing prerequisite, not evidence that that trigger works.

**Sources:** [BlockPavingStone.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/basic/BlockPavingStone.java#L66); [TCBlocks.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java#L420).

---

### F89 — Charged Infusion Pedestals no longer emit the legacy below-top rune effect

**Status:** `confirmed_legacy_mismatch` for the local emission path; client screenshot still required. Distinct from F24 item-render transform and F73 selection geometry.

- Legacy `BlockPedestal.randomDisplayTick` reads `BlockInlay.CHARGE` and, for any positive charge, calls `FXDispatcher.INSTANCE.blockRunes2(x, y - 0.375, z, ..., lifetime=10, ...)`.
- Modern `TCInfusionPedestalBlock` still declares a 0–15 `CHARGE` blockstate, and `TCInlayNetwork` propagates a charge into that state; the underlying charge mechanic has not simply disappeared.
- The modern pedestal block has no `animateTick` or corresponding particle call. Its `TCInfusionPedestalRenderer` renders the stored floating item only, without reading charge or spawning rune particles. No equivalent charge-linked client emitter was identified in inspected client paths.

**Test gate:** compare the same pedestal at charge 0 versus a positive charge using a connected stabilizer/inlay network, with and without an item; inspect the rune origin below the upper platform as well as charge synchronization.

**Sources:** [BlockPedestal.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockPedestal.java#L98); [TCInfusionPedestalBlock.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCInfusionPedestalBlock.java#L34); [TCInlayNetwork.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCInlayNetwork.java#L100); [TCInfusionPedestalRenderer.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCInfusionPedestalRenderer.java#L32).

---

### F90 — Charged Stabilizing Inlay has lost its direction-dependent colored line sparkles

**Status:** `confirmed_legacy_mismatch` for inspected particle-emission code; runtime visibility unverified.

- In legacy `BlockInlay.randomDisplayTick`, `CHARGE > 0` gates a frequency of `1 / (20 - charge)` per display-tick attempt. After selecting a connected horizontal side, it calls `FXDispatcher.INSTANCE.drawLineSparkle` with small lateral velocity, a red/green-biased randomized palette and lifetime 16.
- The modern `TCInlayBlock` has attachment-direction properties and `CHARGE`, and the server `TCInlayNetwork` still propagates charge. However the block has no `animateTick`; inspected client dispatch and rendering paths contain no equivalent charge/direction-based line emission. Search for the legacy `drawLineSparkle` call did not identify an active port consumer.
- This is a dynamic particle omission, separate from F08/D04 inlay tint/model concerns and any static `charge` model-state representation.

**Test gate:** charge an isolated strip and an inlay-to-pedestal link, record the particle frequency at low/high charge and verify that spark movement follows only connected horizontal sides.

**Sources:** [BlockInlay.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockInlay.java#L185); [TCInlayBlock.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCInlayBlock.java#L29); [TCInlayNetwork.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCInlayNetwork.java#L100).

---

### F91 — Research Table no longer emits original study-state motes

**Status:** `confirmed_legacy_mismatch` for inspected block/BE/BER particle path; no client capture yet.

- Legacy `BlockResearchTable.randomDisplayTick` checks `TileResearchTable.data != null` and a one-in-five random branch, then inserts an `FXGeneric` particle with randomized velocity/color, a 40–59-tick lifetime, an alpha/scale gradient and a sprite from atlas indices 384–399.
- The modern `TCResearchTableBlock` has no `animateTick`, and `TCResearchTableBlockEntity` plus `TCResearchTableRenderer` have no corresponding particle producer. The BER continues to handle quill/scroll geometry; that presence does not restore the separate ambient motes.
- The original predicate is **research data present**, not simply a placed table or an open GUI. Do not collapse this issue into F36/F39 UI overlay/HUD effects.

**Test gate:** compare empty vs populated/usable table for 60–120 seconds on the same graphics settings, verify particle origin above table and suppression without study data; inspect whether the modern data lifecycle preserves an equivalent trigger.

**Sources:** [BlockResearchTable.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/crafting/BlockResearchTable.java#L66); [TCResearchTableBlock.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCResearchTableBlock.java#L31); [TCResearchTableBlockEntity.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCResearchTableBlockEntity.java#L38); [TCResearchTableRenderer.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCResearchTableRenderer.java#L30).

---

### F92 — Tallow/Infusion Candle has no matching per-tick smoke and flame

**Status:** `confirmed_legacy_mismatch` for inspected class-level ambient FX path; rendered light and model are not substitutes.

- Legacy `BlockCandle.randomDisplayTick` calls vanilla `SMOKE_NORMAL` and `FLAME` at approximately `(x + 0.5, y + 0.7, z + 0.5)` each client display-tick invocation.
- The active `TCInfusionCandleBlock` directly extends `Block`, not Minecraft's built-in `CandleBlock`, and implements shape/infusion-stabilization/place/remove behavior without `animateTick` or a particle call. The `tallowCandleBlock` factory copies candle-like properties but constructs this custom class; copying properties does not inherit `CandleBlock`'s animated behavior.
- A light-level property of 15 establishes emitted block light; it does **not** establish visible flame or smoke particles. Other candle variants using the same factory should be tested together.

**Test gate:** compare each dye variant at close range in darkness and daylight; distinguish static texture/light from actual continuously rising flame/smoke sprites.

**Sources:** [BlockCandle.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/basic/BlockCandle.java#L75); [TCInfusionCandleBlock.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/basic/TCInfusionCandleBlock.java#L17); [TCBlocks.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java).

---

### F93 — Full Brain Jar retains its XP threshold but replaces Thaumcraft sparks with vanilla enchant glyphs

**Status:** `confirmed_code` and `confirmed_legacy_mismatch` for chosen particle implementation; XP trigger is a `negative_control`.

- Original `BlockJar.randomDisplayTick` emits the custom `FXDispatcher.INSTANCE.spark` in a color/scale configuration only if its Brain Jar tile has `xp >= xpMax`.
- Port `TCBrainJarBlock.animateTick` correctly checks `jar.xp() >= TCBrainJarBlockEntity.XP_MAX`, but calls `level.addParticle(ParticleTypes.ENCHANT, ...)` with small randomized motion instead of the legacy spark effect. The full-XP predicate survives; the visual language, sprite and velocity parameters differ.
- Do not report this as a missing effect or an unconditional-particle bug; it is a specific **effect-substitution** fidelity issue.

**Test gate:** compare full and one-below-capacity jars, with front and oblique views, including particle color, motion and lifespan.

**Sources:** [BlockJar.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java#L243); [TCBrainJarBlock.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBrainJarBlock.java#L127).

---

### F94 — Ambient block-effect inventory: plant emitters are positive controls, not blanket omissions

**Status:** `confirmed_code` presence of current FX paths; exact per-frame parity not yet established.

- An initial scan of **18 relevant legacy block classes** with explicit `randomDisplayTick` found only **four obvious modern class-level `animateTick` implementations**. This count is a triage indicator, **not** an assertion that fourteen families lost FX: BER, custom client dispatch, and vanilla inherited behavior can replace old hooks.
- In particular, `TCPlantBlock.animateTick` dispatches `CINDERPEARL` to smoke+flame, `SHIMMERLEAF` to `TCFXDispatcher.drawShimmerleafMote`, and `VISHROOM` to `TCFXDispatcher.drawVishroomMote`. The original three plants also have explicit `randomDisplayTick` effects. The modern Cinderpearl still uses a one-half random branch, smoke and flame, with roughly equivalent emission positions.
- `TCBrainJarBlock`, `TCSmelterBlock` and `TCVisGeneratorBlock` are the other observed classes with direct `animateTick` overrides. Vis Generator effect substitution is already tracked in F81; do not double-count it. `BlockGrassAmbient`, old fluid/effect blocks, jars and crucible require their own active-registry and alternate-path checks before claiming an overall failure rate.

**Test gate:** compare legacy/port plant particles by kind, count, lifetime, alpha, and motion; search active BER/network/client FX consumers for each remaining legacy block individually. Keep obsolete legacy IDs out of the actionable defect count.

**Sources:** [TCPlantBlock.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/world/plants/TCPlantBlock.java#L99); [BlockPlantCinderpearl.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/world/plants/BlockPlantCinderpearl.java#L56); [BlockPlantShimmerleaf.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/world/plants/BlockPlantShimmerleaf.java); [BlockPlantVishroom.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/world/plants/BlockPlantVishroom.java); [TCBrainJarBlock.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBrainJarBlock.java#L127).

---

### F95 — Liquid Death lost its legacy purple slimy bubbles and occasional pop audio

**Status:** `confirmed_legacy_mismatch` for inspected effect/audio emission pathways; no matched game capture.

- Original `BlockFluidDeath.randomDisplayTick` has two separate random events: with probability 1/20 per display tick it constructs an `FXSlimyBubble` at a fluid-level-dependent height, purple/magenta RGB and alpha 0.8; with probability 1/50 it plays low-volume `BLOCK_LAVA_POP` at a random location on the surface.
- Modern `TCLiquidDeathBlock` handles entity slowing and dissolving damage but has no explicit `animateTick`, bubble creator or lava-pop audio. Its `TCFluids` registration constructs stock NeoForge `BaseFlowingFluid.Source` / `.Flowing` with water-like properties; the inspected client extensions and FX/event dispatch expose texture/tint, not a replacement purple-bubble emitter.
- The inherited vanilla/NeoForge liquid rendering and any default fluid ambient sound are not proof of faithful replication of this custom particle's sprite, color, fluid-level-dependent origin or original pop event. The original liquid's distinctive texture/tint is separately acknowledged in F100.

**Test gate:** compare source and shallow flowing states for bubble height/frequency, purple tint, surface animation and 1/50 random pop; make the sound test distinguish the original lava-pop sample from generic water ambience.

**Sources:** [legacy BlockFluidDeath](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/misc/BlockFluidDeath.java#L51); [port TCLiquidDeathBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/misc/TCLiquidDeathBlock.java#L14); [port TCFluids](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCFluids.java#L22); [port TCClientExtensions](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientExtensions.java#L24); [client events](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java).

---

### F96 — Purifying Fluid lost its original upward motes and occasional pop audio

**Status:** `confirmed_legacy_mismatch` for the inspected custom emitter and sound path; runtime capture pending.

- Original `BlockFluidPure.randomDisplayTick` spawns an `FXGeneric` with probability 1/10, at a height derived from the old fluid meta. The white translucent mote uses particle frame 64, final frames 65/66, negative gravity (-0.01), small randomized wandering, and a lifespan of 10–19 ticks. A separate 1/50 chance plays a quiet `BLOCK_LAVA_POP`.
- Modern `TCPurifyingFluidBlock` manages source consumption, warp-ward application and entity slowing, with no custom client particle/audio path. The registered fluid instances are `BaseFlowingFluid` and their client extension sets the legacy water textures/tint, but does not synthesize the original upward mote.
- This visual-FX omission and the distinct luminosity-source mismatch in F99 are not equivalent: restoring one would not restore the other.

**Test gate:** record particles above source and multiple flowing depths, validate white alpha/final-frame behavior and independent pop-sound frequency; test glow separately.

**Sources:** [legacy BlockFluidPure](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/misc/BlockFluidPure.java#L58); [port TCPurifyingFluidBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/misc/TCPurifyingFluidBlock.java#L18); [port TCFluids](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCFluids.java#L22); [port TCClientExtensions](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientExtensions.java#L24).

---

### F97 — Flux Goo lost its short magenta surface motes

**Status:** `confirmed_legacy_mismatch` for the inspected local particle-emission path; screenshot difference requires replication.

- Legacy `BlockFluxGoo.randomDisplayTick` generates a bright magenta `FXGeneric` at `y + 0.125 * level`, with 2–4-tick lifespan, alpha 0.25 and atlas frame 64 progressing to 65/66. Its trigger frequency is `(level + 1) / 44` for levels 0–7: **one in 44** at level 0 versus **eight in 44** at level 7.
- Modern `TCFluxGooBlock` preserves an eight-value level property, decays/pollutes aura via server `randomTick`, and applies entity effects, but has no `animateTick` and no client particle emitter. The inspected `TCClientEvents`/`TCFXDispatcher` do not identify a Flux-Goo-specific substitute.
- The active visual model is translucent (F101), yet a texture-driven surface is not equivalent to drifting short-lived magic motes.

**Test gate:** capture level 0/3/7 under identical particle settings, checking original emission probability and low/high particle origin independently of liquid surface geometry.

**Sources:** [legacy BlockFluxGoo](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/world/taint/BlockFluxGoo.java#L118); [port TCFluxGooBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/world/taint/TCFluxGooBlock.java#L26); [client events](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java); [port TCFXDispatcher](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/lib/fx/TCFXDispatcher.java).

---

### F98 — Crucible's froth/bubbles have a real client-tick replacement, but boiling ambient pop is separately missing

**Status:** `confirmed_code` **positive control** for three particle families; `confirmed_legacy_mismatch` for the inspected idle audio path.

- Legacy `TileCrucible.update` calls `drawEffects` client-side when water is present. It emits `crucibleFroth` when heat is above 150, four-direction `crucibleFrothDown` sprays when essentia exceeds the 500 cap, and colored `crucibleBubble` approximately one in six ticks when aspects are present.
- Current `TCCrucibleBlock.getTicker` explicitly selects `TCCrucibleBlockEntity.clientTick` on the client. That method retains the same water/heat/aspect gates and calls `TCFXDispatcher.crucibleFroth`, `.crucibleFrothDown` and `.crucibleBubble`. `TCCrucibleRenderer` separately draws the water surface. Therefore, **absence of a block `animateTick` is not evidence that these three Crucible effects are missing**.
- However, legacy `BlockCrucible.randomDisplayTick` separately attempts a quiet `BLOCK_LAVA_POP` at one-in-ten rate, provided the tank has fluid and heat is above 150. The modern block has no corresponding `animateTick`, and the client tick contains no audio call. The modern BE's `playBubbleSound()` is associated with crafting/interactions, not an idle random boiling-pop loop. A source search for `LAVA_POP` in the active port identifies Infernal Furnace, not Crucible.

**Test gate:** check that client BE ticking is active and synchronized (water, heat, aspect count), then compare froth/downflow/bubble dynamics and separately test idle boiling audio without item input. Keep `TCSounds.BUBBLE` interaction audio out of the ambient-pop verdict.

**Sources:** [legacy TileCrucible](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/crafting/TileCrucible.java#L127); [legacy BlockCrucible](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/crafting/BlockCrucible.java#L166); [port TCCrucibleBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCCrucibleBlock.java#L58); [port TCCrucibleBlockEntity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCCrucibleBlockEntity.java#L84).

---

### F99 — Purifying Fluid's original luminosity of five is not configured in the port

**Status:** `confirmed_code` missing configuration with `probable_legacy_mismatch` in emitted light pending a runtime light-level probe.

- Original `ConfigBlocks.FluidPure` explicitly calls `setLuminosity(5)` on the Forge fluid definition.
- Port `TCFluids.PURIFYING_FLUID_TYPE` uses `waterLikeProperties()` without a light-level setting. The corresponding `TCBlocks.purifyingFluidBlock()` copies water properties and does not call `.lightLevel(state -> 5)`. Neither the tint-bearing `IClientFluidTypeExtensions` nor the transparent surface texture represents a light-emission registration.
- The source-level **5 versus absent explicit setting** is confirmed. The actual rendered/blockstate light level must be measured on a clean client and server; do not infer a screenshot brightness from the ARGB fluid tint.

**Test gate:** place a purifying-fluid source in an enclosed dark room, read emitted block light using the debug screen or a small probe, compare to the original level 5 and inspect light propagation across flowing levels.

**Sources:** [legacy ConfigBlocks fluid definitions](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/config/ConfigBlocks.java#L448); [port TCFluids](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCFluids.java#L22); [port TCBlocks fluid factories](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java#L846); [port TCClientExtensions](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientExtensions.java#L24).

---

### F100 — Fluid surface texture and tint migration are positive controls despite missing ambient FX

**Status:** `confirmed_code` mapping-level positive control; GPU/atlas-frame visual match not yet proven.

- Legacy `ConfigBlocks.FluidDeath` references `thaumcraft:blocks/animatedglow` for both still and flowing sprites and returns ARGB **-263978855** (`0xF0440099`). Legacy `FluidPure` references vanilla water-still/flow sprites and returns **2013252778** (`0x77FFCCAA`).
- Modern `TCClientExtensions` maps Liquid Death to `thaumcraft:block/animatedglow` for still/flowing, with the same signed tint integer; it maps Purifying Fluid to `minecraft:block/water_still` and `minecraft:block/water_flow`, preserving the second tint integer. The namespace/path changes are modern resource-location conventions; they are not, by themselves, missing textures.
- This is **not** evidence that UV flow, alpha blending, atlas animation speed, particle systems, lighting or viscosity are parity-complete. The specific missing custom particle/audio behaviors remain F95/F96, and the light configuration is F99.

**Test gate:** verify that animatedglow is correctly stitched/animated, that flowing textures and tint alpha survive on a live GPU, and compare all depth/underwater camera cases.

**Sources:** [legacy ConfigBlocks fluid definitions](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/config/ConfigBlocks.java#L448); [port TCClientExtensions](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientExtensions.java#L24); [port TCFluids](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCFluids.java#L22).

---

### F101 — Flux Goo changed from a Forge fluid model to eight static full-width translucent cuboids

**Status:** `confirmed_code` model-strategy difference; `probable_cause` only for possible hard edges/seams, NOT a demonstrated in-game artifact.

- The legacy `flux_goo` blockstate delegates to the Forge `forge:fluid` model with a `fluid=flux_goo` custom binding. Its flowing-fluid rendering is controlled by that legacy pipeline.
- The modern active blockstate selects `thaumcraft:block/flux_goo_0` through `_7`. Individual `render_type: minecraft:translucent` models contain a solid 16×16 footprint, six textured faces and a fixed top height: `_0` is 2/16 tall and `_7` is 16/16 tall. `TCFluxGooBlock` reports `RenderShape.MODEL`.
- This establishes a structural replacement of flow-surface geometry and interpolation by discrete cuboids. Whether that produces visible horizontal seams, inappropriate internal faces or loss of slopes at neighboring unequal levels is **not yet proven**; special culling and context may still mask differences. Do not attribute black outlines (D04/F08) to this without reproduced evidence.

**Test gate:** compare isolated and adjacent Goo at levels 0, 1, 3, 7, including stepped neighboring levels, translucent backfaces, edge occlusion, top-height behavior and actual surface/side UVs; do not conflate missing F97 motes with model geometry.

**Sources:** [legacy flux_goo blockstate](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/resources/assets/thaumcraft/blockstates/flux_goo.json); [port flux_goo blockstate](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/flux_goo.json); [level-0 cuboid](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/flux_goo_0.json); [level-7 cuboid](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/flux_goo_7.json); [port TCFluxGooBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/world/taint/TCFluxGooBlock.java#L26).

---

### F102 — The original tile-event forwarder is absent from six plain-Block ports; five FX handlers reject server-side callbacks

**Status:** `confirmed_code` for the original/modern dispatch and return-path difference; `probable_cause` for event non-delivery pending an instrumented dedicated-server trace. Refines F26, does not count six new independent defects.

- Original `BlockTCTile.eventReceived(state, world, pos, id, param)` explicitly calls `world.getTileEntity(pos).receiveClientEvent(id, param)` and returns the tile's Boolean result. For example, legacy `TileSmelter.receiveClientEvent(1, data)` returns `true` whether the world is remote or server; drawing itself is client-gated.
- The six current target blocks catalogued in F26 (`TCWaterJugBlock`, `TCVoidSiphonBlock`, `TCCrucibleBlock`, `TCSmelterBlock`, `TCInfernalFurnaceBlock`, `TCMirrorBlock`) extend `Block implements EntityBlock` without an overriding block `triggerEvent`. The relevant modern five BEs place their visual `triggerEvent` handling inside `level.isClientSide` checks and return `super.triggerEvent` on the server. The mirror emits event 1 but has no matching BE handler.
- This yields **two distinct source-level gates**: (1) the missing block-level forwarding and (2) the BE's false server return if forwarding alone were added. On the standard server event-acceptance path, simply grafting a delegate into a block is therefore not an independently verified fix. A proper remediation would need to preserve acceptance semantics on the server, and execute visual work only on the client.
- These event-specific paths are separate from ambient block `animateTick`, ordinary BE `clientTick`, and dedicated infusion packets. Successful animation in one of those channels does not prove this event channel works.

**Test gate:** send a known event per block in a dedicated-server dev session and log server block-state result, server BE result, client packet arrival and client BE invocation. Assert expected `ClientboundBlockEventPacket` delivery; keep this `probable_cause` until the chain is instrumented.

**Sources:** [legacy BlockTCTile](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/BlockTCTile.java#L67); [legacy TileSmelter](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java#L343); [modern TCWaterJugBlockEntity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCWaterJugBlockEntity.java); [modern TCVoidSiphonBlockEntity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCVoidSiphonBlockEntity.java#L190); [modern TCCrucibleBlockEntity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCCrucibleBlockEntity.java); [modern TCSmelterBlockEntity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java#L513); [modern TCInfernalFurnaceBlockEntity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCInfernalFurnaceBlockEntity.java#L393); [modern TCMirrorBlockEntity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCMirrorBlockEntity.java#L209); [NeoForge 1.21.1 BaseEntityBlock API](https://aldak.netlify.app/javadoc/1.21.1-21.1.x/net/minecraft/world/level/block/baseentityblock).

---

### F103 — Void Siphon drain feedback substitutes local portal motes for original rift-to-siphon void streaks

**Status:** `confirmed_legacy_mismatch` for visual effect content; `probable_cause` of missing playback from F102 delivery gate.

- Legacy `TileVoidSiphon.receiveClientEvent(5, j)` finds every valid `EntityFluxRift` in range and calls `FXDispatcher.INSTANCE.voidStreak` **from each rift position to the siphon center**, with `j` as seed and scale 0.04. The dispatcher constructs an `FXVoidStream` object, so the arc/stream connects two world-space endpoints.
- The modern server still emits event `5` after a successful drain at eligible counter multiples of 40. Its client `triggerEvent(5, data)` instead spawns **twelve `ParticleTypes.PORTAL` particles distributed only around the siphon**, with small random velocities. It neither queries the rifts nor establishes source-to-target streak geometry.
- Even if block-event forwarding were fixed, these local portal motes would not reproduce the original connection visual. This is independent of F15's absent Void Siphon TESR portal mesh and F102's event reachability.

**Test gate:** place one and two rifts at different distances, trigger actual siphon drains, verify streak count/endpoints/seeded motion versus current local motes. Evaluate the event path separately from the FX replacement.

**Sources:** [legacy TileVoidSiphon](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/crafting/TileVoidSiphon.java#L126); [legacy FXDispatcher](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/FXDispatcher.java#L896); [modern TCVoidSiphonBlockEntity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCVoidSiphonBlockEntity.java#L190).

---

### F104 — Smelter vent burst changes custom FXVent/color and lava-extinguish audio to cloud particles and fire-extinguish audio

**Status:** `confirmed_legacy_mismatch` for source-level visual/audio choice; runtime event route still subject to F102.

- Original `TileSmelter.receiveClientEvent(1, facingIndex)` determines the outgoing side, plays `BLOCK_LAVA_EXTINGUISH` and draws four colored `FXVent` particles with RGB integer `11184810`, alpha 0.4 and side-directed jitter; `FXDispatcher.drawVentParticles` creates the dedicated `FXVent` implementation.
- Modern `TCSmelterBlockEntity.triggerEvent(1, data)` keeps directional four-particle emission but uses ordinary `ParticleTypes.CLOUD`, and plays `SoundEvents.FIRE_EXTINGUISH` rather than the original lava-extinguish sample. Side vectors are still computed, so this is a **particle/sound substitution**, not proof of missing orientation.
- The original event returned true on the server as well as client (drawing on client only); the modern handler returns the superclass result when not client-side. F102 and this content mismatch require separate tests and eventual fixes.

**Test gate:** activate a smelter with a correctly oriented vent, then compare the four particles' sprite, grayscale tone, alpha/lifespan and motion plus the sound event. A generic cloud particle does not establish equivalence to `FXVent`.

**Sources:** [legacy TileSmelter](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java#L343); [legacy FXDispatcher](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/FXDispatcher.java#L896); [modern TCSmelterBlockEntity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java#L513).

---

### F105 — Infernal Furnace retains the vanilla lava-particle family but changes the ejection origin and motion

**Status:** `confirmed_code` geometry/velocity difference; `confirmed_legacy_mismatch` for source-level particle trajectory, with client screenshot still pending. The particle family is a `negative_control`.

- Original `TileInfernalFurnace.receiveClientEvent(3, 0)` repeats five times: `FXDispatcher.furnaceLavaFx` plus a quiet lava-pop sound. That dispatcher builds vanilla `ParticleLava.Factory` particles **near the output-side adjacent block**, at `y + 0.3`, with randomized lateral jitter and velocity components up to roughly 0.15 horizontal / 0.2 upward.
- Port `TCInfernalFurnaceBlockEntity.triggerEvent(3, 0)` likewise repeats five times and uses vanilla `ParticleTypes.LAVA` with lava-pop audio. However the spawn position is `block center + 0.45 * output direction` and `y + 0.65`, and the velocity is fixed at approximately `0.03 * output direction`, `0.02` upward. It is both closer to the center and substantially less randomized.
- Do not label this a lost particle type: both pathways are vanilla lava-style. The visible difference is directional position/spread and dynamics, conditional on the event ever reaching the client (F102).

**Test gate:** capture output-side high-FPS sequences against the same facing/bellows/smelt setup, comparing five-particle count, spawn offset and trajectories. Test client event acceptance independently.

**Sources:** [legacy TileInfernalFurnace](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/tiles/devices/TileInfernalFurnace.java#L279); [legacy FXDispatcher](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/FXDispatcher.java#L896); [modern TCInfernalFurnaceBlockEntity](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCInfernalFurnaceBlockEntity.java#L393).

---

### F106 — Infusion Matrix visual source/essentia trails use an independent, connected custom packet/render pipeline

**Status:** `confirmed_code` end-to-end source-chain positive control; actual visual frame fidelity and multiplayer packet timing still need testing.

- `TCInfusionLegacyCycleExecutor` calls `TCInfusionNetwork.sendEssentiaSource` after draining an aspect and `sendComponentSource` when a pedestal component begins its countdown. `TCInfusionNetwork` registers both `playToClient` payload codecs and sends to nearby players within 32 blocks using `PacketDistributor.sendToPlayer`.
- `TCInfusionClientFXCache` accepts/deduplicates the payloads. `TCClientEvents.onClientTick` calls `TCInfusionClientEffects.tick`, which drains the cache and spawns/updates streams or component-source effects. The same client event class invokes `TCInfusionClientEffects.render` after particles. `TCInfusionMatrixRenderer` also marks active crafting halos for this rendering pass.
- The modern `EssentiaStream` uses its own textured geometry; component-source effects can generate sparkles and item debris. This is a genuine alternate path to the original `RenderEventHandler`/`FXDispatcher.essentiaTrailFx`, **not** evidence of a missing effect merely because the older method is uncalled.
- **Open caveat:** `acceptQueuedPayloads` discards a received stream/source payload when its matrix Block Entity is not present on the client at drain time. Chunk-load/observer-arrival timing must be tested before deciding whether that guard causes missed transient FX. Do not claim such a loss from source alone.

**Test gate:** confirm packet registration, two-player radius, aspect drain, component countdown, client FX cache and render pass in a dedicated server; test late chunk loading, matrix unloading and dimension changes. Compare stream mesh/texture/color and halo over time to the original.

**Sources:** [TCInfusionLegacyCycleExecutor](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionLegacyCycleExecutor.java#L106); [TCInfusionNetwork](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionNetwork.java#L36); [TCInfusionClientFXCache](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionClientFXCache.java); [TCInfusionClientEffects](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/TCInfusionClientEffects.java#L108); [TCClientEvents](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java#L34); [TCInfusionMatrixRenderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCInfusionMatrixRenderer.java); [legacy RenderEventHandler](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/lib/events/RenderEventHandler.java).

---

### F107 — Dormant legacy-named dispatcher helpers must be distinguished from active infusion effects and idle pedestal runes

**Status:** `confirmed_code` call-site inventory; `negative_control` against falsely counting active infusion as missing, while F89's idle charged-pedestal rune gap remains.

- The port defines `TCFXDispatcher.essentiaTrailFx` and `TCFXDispatcher.drawInfusionPedestalParticles`, but default-branch code search found no external active Java callers for either legacy-named helper. That fact alone does **not** show that infusion effects are absent: F106 traces an independently invoked `TCInfusionClientEffects` implementation that owns the visuals.
- `TCInfusionClientEffects.spawnPedestalSource` is triggered by the dedicated `TCInfusionSourcePayload` during crafting. It creates effect sprites/sparkles or source-item debris and has a finite source lifetime. That is not the same predicate or geometry as the legacy `BlockPedestal.randomDisplayTick` rune when `CHARGE > 0` outside infusion, already tracked as F89.
- Thus three different phenomena must be tested separately: (a) aspect streams, (b) crafting component-source particles, and (c) charge-linked idle pedestal runes. A passing test of (a) or (b) does not close (c).

**Test gate:** verify each of the three scenarios independently, and search new call sites when port source changes rather than deleting unused helpers by name alone.

**Sources:** [TCFXDispatcher](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/lib/fx/TCFXDispatcher.java#L831); [TCInfusionClientEffects](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/TCInfusionClientEffects.java#L108); [TCInfusionNetwork](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion/TCInfusionNetwork.java#L36); [legacy BlockTCTile](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/BlockTCTile.java#L67).

---

### F108 — Research Table GUI particles bypass frame interpolation by passing `partialTick = 0`

**Status:** `confirmed_code` source-level interpolation mismatch; perceived stutter requires client capture.

- The original `ParticleEngine.renderTick(RenderTickEvent.END)` passes the live `event.renderTickTime` to `Particle.renderParticle`, allowing between-tick position interpolation and time-dependent appearance.
- Current `TCResearchTableScreen.renderBg(..., float partialTick, ...)` receives the correct partial tick but invokes `TCLegacyParticleEngine.renderGui(guiGraphics, 0.0F)`. The engine relays this unchanged to `TCLegacyFXGenericGui.render`, which calls `Mth.lerp(partialTicks, xo, x)` / `Mth.lerp(partialTicks, yo, y)` and uses `(age + partialTicks) / maxAge` for color progression.
- Therefore those particle positions render at the **previous tick's coordinates** between simulation ticks and advance in discrete steps even when the client frame rate exceeds 20 FPS. This is a supported code-level difference from legacy rendering; its visual severity depends on particle movement speed and actual screen FPS.

**Test gate:** spawn Research Table GUI sparkle effects at stable 60/120 FPS and record at frame boundaries; compare `partialTick=0` with the live screen partial tick in a controlled test branch only after research approval.

**Sources:** [legacy ParticleEngine](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/ParticleEngine.java#L48); [TCResearchTableScreen](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java#L170); [modern TCLegacyParticleEngine](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java#L190); [TCLegacyFXGenericGui.render](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyFXGenericGui.java#L105).

---

### F109 — GUI particle updates are screen-scoped in the port, unlike the original global particle tick

**Status:** `confirmed_code` lifecycle difference; `probable_legacy_mismatch` for paused/reappearing effects pending UI reproduction.

- Legacy `ParticleEngine.updateParticles(ClientTickEvent.START)` ticks **all six layers**, including GUI layers 4 and 5, whenever a world is present. Its `RenderTickEvent.END` additionally draws GUI particles independently of which screen is open, until they expire.
- Modern `TCClientEvents.onClientTick` updates only `TCLegacyParticleEngine.tick()` for world particles. The sole inspected call to `TCLegacyParticleEngine.tickGui()` is `TCResearchTableScreen.containerTick`. Modern GUI FX rendering is likewise invoked only from `TCResearchTableScreen.renderBg`.
- GUI effects are stored in shared static `GUI_EFFECTS` and `DELAYED_GUI` collections, and `TCLegacyParticleEngine.clear()` is invoked from the general client handler when `Minecraft.level == null`, not specifically on Research Table screen close. No screen-specific cleanup call was identified. Thus, on closing a Research Table while GUI particles remain, their age/delay **stops advancing** until a future GUI tick or a full cache clear; reopening can resume stale effects instead of having them expire normally.

**Test gate:** initiate a long-lived GUI sparkle, close the table, wait longer than its remaining lifetime without disconnecting, then reopen; compare observed count/age with original Thaumcraft and confirm whether the old global-over-screen rendering intentionally persisted after close.

**Sources:** [legacy global particle tick](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/ParticleEngine.java#L249); [legacy ParticleEngine](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/ParticleEngine.java#L48); [TCClientEvents](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java#L34); [TCResearchTableScreen.containerTick](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java#L140); [modern GUI tick](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java#L145); [modern clear/layer arrays](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java#L65).

---

### F110 — World FX storage lost the original dimension-keyed isolation

**Status:** `confirmed_code` data-ownership difference; `test_required` potential cross-level trail persistence, NOT an observed bug.

- The original engine keeps a `HashMap` for each layer keyed by `world.provider.getDimension()`; add, tick and render all consult the current dimension key. Delayed particles also retain a dimension identifier.
- `TCLegacyParticleEngine` now uses single static per-layer lists of `TCLegacyFXGeneric` and delayed effects, without dimension or level identity in the containing keys. `TCLegacyFXGeneric` does hold a `Level` reference but its inspected `canRender`/`render` path does not reject a different active client level. The common `TCClientEvents` handler calls `clear()` only on client ticks where `Minecraft.level == null`.
- A client-level replacement without an intervening **null-level client tick** could therefore allow old-world particles to be rendered briefly in the new world, or delayed effects to be admitted there. Whether ordinary Nether/End travel always executes a null-level tick is not established here; do not mark this runtime-confirmed.

**Test gate:** produce slow/high-lifetime visible FX, switch dimension and reconnect rapidly; record client `level` transitions and active/delayed counts, ensuring no stale FX across worlds. Add a client lifecycle teardown hook only if reproduced.

**Sources:** [legacy global particle tick](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/ParticleEngine.java#L249); [legacy world renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/ParticleEngine.java#L124); [modern clear/layer arrays](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java#L65); [modern TCLegacyParticleEngine](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java#L190); [TCLegacyFXGeneric.canRender](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyFXGeneric.java#L70); [TCClientEvents](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java#L34).

---

### F111 — Six-layer additive/alpha and depth-test semantics are explicitly retained in the port

**Status:** `confirmed_code` positive control for blend/depth states; overall shader/GL-state parity still requires rendering tests.

- Legacy world FX render layers **3→0**: layers 0 and 2 use `SRC_ALPHA, ONE` (additive), layers 1 and 3 use `SRC_ALPHA, ONE_MINUS_SRC_ALPHA` (standard alpha), and layers 2/3 disable depth testing. The legacy GUI loop renders layers **5→4**, using additive blend for 4 and standard alpha for 5.
- Modern `TCLegacyParticleEngine` deliberately renders world layers **3→0**, GUI layers **5→4**, selects exactly the same four blend pairings and disables/restores depth testing for world layers 2/3. These are concrete source-backed controls against claiming that every particle's black/bright border comes from loss of its historical layer-blend selection.
- The rendering stage has changed from legacy `RenderWorldLastEvent` to NeoForge `RenderLevelStageEvent.AFTER_PARTICLES`; draw ordering relative to other mod render passes is not proved equal. The accurate blend functions alone do not establish correct texture sampling, depth-writing, atlas UVs or particles' per-frame visuals.

**Test gate:** compare one test sprite on each of six layers against dark/light/occluded backgrounds, and record depth behavior with a foreground solid wall.

**Sources:** [legacy world renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/ParticleEngine.java#L124); [legacy ParticleEngine](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/ParticleEngine.java#L48); [modern layer state](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java#L328); [modern TCLegacyParticleEngine](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java#L190).

---

### F112 — Ported particle shaders preserve the legacy `1/255` alpha cutoff but have an explicit shader-unavailable fallback

**Status:** `confirmed_code` shader and registration positive control; runtime shader-loading failure not established.

- Original `ParticleEngine` calls `glAlphaFunc(GREATER, 0.003921569)` for world and GUI particles. The modern `tc_legacy_particle.fsh` and `tc_legacy_particle_gui.fsh` both discard a fragment only below **0.003921569**, preserving soft low-alpha fringe pixels rather than applying a high cutoff.
- `TCLegacyShaders.registerShaders` registers two custom shaders under the `minecraft` core-shader namespace with matching resource files. `TCLegacyParticleEngine` selects the custom world shader if it is available, otherwise falls back to `GameRenderer.getPositionTexColorShader`.
- This confirms the intended alpha threshold in the bundled shader and an explicit fallback. It does **not** prove the shader always loads on every GPU/resource pack, or that dark outlines in unrelated HUD textures share the particle cutoff issue.

**Test gate:** verify successful shader registration in development logs, inspect the bound program during world and GUI effect rendering, and compare low-alpha fringe pixels with and without the fallback.

**Sources:** [legacy ParticleEngine](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/ParticleEngine.java#L48); [shader registration](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/lib/TCLegacyShaders.java#L21); [world particle fragment shader](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/minecraft/shaders/core/tc_legacy_particle.fsh); [GUI particle fragment shader](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/minecraft/shaders/core/tc_legacy_particle_gui.fsh); [modern TCLegacyParticleEngine](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java#L190).

---

### F113 — Research Table GUI FX drawing moved from global end-of-frame to the middle of `renderBg`

**Status:** `confirmed_code` draw-order difference; `probable_legacy_mismatch` only for particle overlap with later UI elements.

- Original `ParticleEngine.renderTick` draws GUI-layer particles during `RenderTickEvent.END`, after the normal in-world/screen rendering sequence.
- The port invokes `TCLegacyParticleEngine.renderGui` inside `TCResearchTableScreen.renderBg`, **after** the theory sheets/category panel but **before** `renderLegacyActionButtons`; `renderLabels` and tooltip/UI overlays can also be rendered later. This can place translucent GUI sparks underneath later button/tooltip surfaces instead of on top of the final screen, if they overlap in screen space.
- This concerns the GUI particle pass only, not the ambient Research Table world motes missing in F91 or the repeated Knowledge Gain HUD draws already tracked in F39.

**Test gate:** capture an active Research Table sparkle crossing a legacy action button or tooltip; compare sprite blending, occlusion and z-order at identical GUI scale. Confirm the original end-phase ordering under a real 1.12.2 client.

**Sources:** [legacy ParticleEngine](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/ParticleEngine.java#L48); [TCResearchTableScreen](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java#L170); [modern TCLegacyParticleEngine](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java#L190).

---

### F114 — Particle-setting culling and per-layer FIFO caps are preserved, with runtime FPS effects still testable

**Status:** `confirmed_code` positive control for count-based limits; no claim of identical random sequence or performance.

- Both original and port map particle settings to per-layer capacity **2000 (all), 1000 (decreased), 500 (minimal)**. Both apply the old culling rule using a random `nextInt(3)` against the particle setting, incrementing the effective culling level when reported FPS falls below 30.
- On overflow, both implementations remove the **oldest element at index 0** before appending the new particle. This exact FIFO behavior matters when visual scenes create many wispy motes or sparks.
- The original also partitions lists by dimension (F110), while modern has flat static lists. Matching the integer limits does not guarantee matched FPS, event frequency or random distribution; compare counts on an otherwise identical controlled scene.

**Test gate:** in the same effect-rich scene, record active particle counts and churn with settings All/Decreased/Minimal and client FPS above/below 30.

**Sources:** [legacy particle limit](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/ParticleEngine.java#L216); [legacy global particle tick](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/ParticleEngine.java#L249); [modern TCLegacyParticleEngine](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java#L190); [modern clear/layer arrays](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/legacy/TCLegacyParticleEngine.java#L65).

---


### F115 — Active call-site audit separates live visual behavior from legacy-named helper methods

**Status:** `confirmed_code` scoped active-path inventory; not a proof of global unused-code elimination.

- The modern `TCFXDispatcher` is a helper library, not a registry that automatically renders every effect it declares. A method's implementation or a parity audit's call is not evidence that an in-game producer invokes it. For example, `drawVentParticles`, `drawInfusionPedestalParticles`, and `essentiaTrailFx` have no non-dispatcher production caller in the searched main-branch code paths. The older Smelter vent and pedestal gaps already have independent evidence (F89, F104).
- Concrete active chains **do** exist: `TCWispEntity.tick → drawWispParticles`; `TCAlumentumEntity.tick` / `TCCausalityCollapserEntity.tick → drawAlumentum + drawAlumentumGenericTrail`; `TCFocusCloudEntity.tick → drawFocusCloudParticle + drawFluxFocusEffectParticle`; `TCCrucibleBlockEntity → crucibleBubble`; `TCInfusionClientEffects → drawSimpleSparkle`; and `ItemThaumometer.use → TCThaumometerClientEffects.onUse → blockRunes`. The Thaumometer also invokes `scanHighlight` while held (F120).
- Hence a blanket conclusion like “all moved dispatcher FX are dead” is contradicted by active entity and item sources. The significant parity questions concern *what* active calls emit and *which original producers disappeared*, not mere method presence.

**Test gate:** use in-client tracing to count emissions by specific source and renderer under representative scenarios, distinguishing client events, packets, BE tick paths and direct item use.

**Sources:** [modern dispatcher](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/lib/fx/TCFXDispatcher.java#L1); [modern Wisp](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCWispEntity.java#L124); [modern Alumentum](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCAlumentumEntity.java#L90); [modern Focus Cloud](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCFocusCloudEntity.java#L355); [Thaumometer item use](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java#L29).

---

### F116 — Alumentum and Causality Collapser trails emit an extra vanilla FLAME over both legacy particle families

**Status:** `confirmed_legacy_mismatch` in emission calls; the net visible brightness/density must be checked in-game.

- Original `EntityAlumentum.onUpdate` and `EntityCausalityCollapser.onUpdate` each perform **three** per-tick samples, emitting **one `drawAlumentum` particle and one white `drawGenericParticles`** particle per sample. The generic uses sprite range beginning at **448**, eight frames, eight-tick age, layer 1, scale 0.3 and alpha 0.7; neither original class adds a Minecraft flame particle in the loop.
- Both modern entities retain three `drawAlumentum` samples and reproduce the generic particle through `drawAlumentumGenericTrail`, with its matching sprite/frame/layer/alpha configuration. But **between these two preserved calls each modern sample also invokes `level().addParticle(ParticleTypes.FLAME, …)`**. Thus a vanilla flame is added alongside both original families, **three additional vanilla particles per entity tick** during the client-side flight trail.
- This is an additive visual difference, not a replacement or disappearance of the original trail. Do not “fix” it by removing either original custom particle. Compare pixel coverage, luminance and particle counts for both projectiles before deciding whether the vanilla overlay is intentional.

**Test gate:** record side-by-side original/port Alumentum and Causality Collapser in-flight captures; separately count `FLAME` and legacy custom particles for five moving ticks.

**Sources:** [legacy Alumentum](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/projectile/EntityAlumentum.java#L28); [legacy Collapser](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/projectile/EntityCausalityCollapser.java#L32); [modern Alumentum](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCAlumentumEntity.java#L90); [modern Collapser](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCCausalityCollapserEntity.java#L108); [generic trail adapter](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/lib/fx/TCFXDispatcher.java#L248).

---

### F117 — Player-cast focus clouds render Flux-specific motes even for other focus-effect nodes

**Status:** `confirmed_legacy_mismatch` for the general CLOUD casting path; Flux-only rift clouds are a separate compatible subset.

- Original `EntityFocusCloud` holds its `FocusPackage`; on the client it selects a random member from `focusPackage.getFocusEffects()` on each sample, colors `drawFocusCloudParticle` by **that effect's own element color**, then calls **`eff.renderParticleFX(…)`**. Mixed-effect clouds can therefore display multiple corresponding element colors and effect-specific particle types.
- The port's general `TCFocusCastExecutor` **does** spawn `TCFocusCloudEntity` for player CLOUD spells and carries an encoded `remainingNodes` payload and a package-level color. But `TCFocusCloudEntity.tickClientParticles` always pairs its generic cloud particle, tinted with one synchronized `EFFECT_COLOR`, with **`drawFluxFocusEffectParticle`** regardless of the spell nodes. The client particle loop does not read the effect list or choose effect-specific renderers. `effectKey` is initialized to `thaumcraft.FLUX` and is not changed by the general constructor.
- This is not merely a potential missing future feature: the audit's Frost cloud example and the active general CLOUD casting branch establish non-Flux configurations. The rift event's originally Flux-based cloud can still reasonably use Flux particles; that exception does not establish correctness for a Frost, Fire or mixed CLOUD spell.
- A parity solution needs an explicit **client-visible effect descriptor** (or another semantically equivalent source) and effect-specific render dispatch. Keep server-authoritative execution of `remainingNodes` separate from cosmetic client selection; never rely on server-only fields being visible on the client.

**Test gate:** compare cast CLOUD+FLUX, CLOUD+FROST, CLOUD+FIRE and mixed-effect CLOUD under stable camera/light; verify per-sample colors, effect-specific emitters and actual client synchronization.

**Sources:** [original Focus Cloud effect sampling](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/projectile/EntityFocusCloud.java#L143); [modern generic Cloud construction](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/casters/TCFocusCastExecutor.java#L167); [modern Focus Cloud constructors/data](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCFocusCloudEntity.java#L61); [modern unconditional Flux FX](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCFocusCloudEntity.java#L355); [Frost cloud validation fixture](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/casters/TCFocusMediumEffectParityAudit.java#L118).

---

### F118 — Wisp's continuous aspect-colored particle emission is wired to the legacy sprite path

**Status:** `confirmed_code` positive control for ambient Wisp trail; no claim of total entity visual parity.

- Original `EntityWisp.onUpdate` rolls `world.rand.nextBoolean()` client-side and, when an aspect exists, draws one `drawWispParticles` at the same ±0.7 randomized position with color `Aspect.getAspect(getType()).getColor()`, zero velocity and zero delay.
- Modern `TCWispEntity.tick` retains the 50%-chance, aspect lookup, color, position formula, zero velocity and zero-delay dispatcher call. Modern `TCFXDispatcher.drawWispParticles` also retains sprite **264**, eight frames, grid **64**, loop flag, 10–14-tick age, start alpha **0.5**, wind and random-movement magnitude from the legacy dispatcher.
- This active, matched trace is a useful control against treating the whole modern Wisp particle implementation as absent. Spawn/death bursts are **separate** original FX events (F119).

**Test gate:** inspect Wisp continuously for one minute, comparing count/color/scale of ambient motes across a selection of aspect types.

**Sources:** [original Wisp tick](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/monster/EntityWisp.java#L92); [legacy Wisp dispatcher](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/FXDispatcher.java#L976); [modern Wisp tick](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCWispEntity.java#L124); [modern dispatcher](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/lib/fx/TCFXDispatcher.java#L154).

---

### F119 — Wisp's birth and death bursts have no counterpart in its modern entity lifecycle

**Status:** `confirmed_code` entity-level omission; requires client to rule out a separate external death/spawn effect.

- Original `EntityWisp.onUpdate` emits `FXDispatcher.burst(x,y,z,10.0F)` during the initial one-tick appearance; `onDeath` emits a smaller `burst(x,y+0.45,z,1.0F)`. These are distinct from the per-tick aspect-colored particles preserved in F118.
- The modern `TCWispEntity` has a client `tick` path for ambient motes but **no birth-burst condition**, `die`/death-particle override, or `burst` call. Its normal death sound and drop handling are not replacements for that legacy client visual. No separate Wisp-specific burst chain has yet been verified, so mark an entity-level omission rather than claiming an exhaustive global absence.
- Because `FXDispatcher.burst` is historically a composite effect, parity should compare sprite count, size and timing, not substitute arbitrary vanilla death-smoke without an explicit design decision.

**Test gate:** spawn a Wisp and kill one in both versions at matching camera distance; observe initial tick and death frame, and trace entity event handling before implementation.

**Sources:** [legacy Wisp death and first tick](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/monster/EntityWisp.java#L85); [modern entity client tick](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCWispEntity.java#L124); [modern sound/death loot methods](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCWispEntity.java#L175).

---

### F120 — Thaumometer highlights and scan-use runes are active positive controls, with a scoped entity Y offset

**Status:** `confirmed_code` active source-backed visual path, with one coordinate mismatch; actual sprite appearance still requires client screenshots.

- The original `ItemThaumometer` performs held-client highlights every **five ticks** on still-scannable entities and blocks, and right-click `drawFX` emits **ten** `blockRunes` per entity or block scan. The modern `TCThaumometerClientEffects.onClientTick` likewise gates to every fifth tick, invokes `TCFXDispatcher.scanHighlight` for unknown-scannable targets, and its `onUse` emits ten `blockRunes`; `ItemThaumometer.use` directly invokes this client method. Thus neither helper should be classified unused.
- The original entity-use rune anchor is `target.posY + target.getEyeHeight()/2`; modern uses `target.getY() + target.getBbHeight()/2`. Those values differ for entities whose eye height differs from total bounding-box height, shifting runes vertically on some scan targets. The block-use anchor still adds **0.25** to Y, and both paths preserve the ten-sample pattern.
- This is a precise local spatial mismatch amid otherwise intact source-level feature wiring. Avoid extending that result to scanning progression, texture atlas parity, or matching the full item animation.

**Test gate:** hold the Thaumometer over unscanned mob/block to verify periodic outline, then scan entities with significantly different eye-height-to-bounds ratios; compare rune center Y, count and colors.

**Sources:** [legacy Thaumometer highlight and scan-use FX](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java#L56); [legacy drawFX placement](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java#L103); [modern highlight tick](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCThaumometerClientEffects.java#L68); [modern rune positions](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCThaumometerClientEffects.java#L101); [modern active item use](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java#L29).

---


### F121 — The ten original focus effects have distinct sprite/behavior families, not a generic universal cast particle

**Status:** `confirmed_code` original-render baseline; modern effect-element registration is a separate positive control.

- The original `FocusEffect.renderParticleFX` implementations are materially different by atlas frame, alpha curve, lifespan, gravity, scale and/or color. A generic particle type cannot reproduce all ten by changing RGB alone. The pinned modern `TCFocusElements` **does** register all ten effect keys for gameplay, but no corresponding effect-specific particle renderer is selected by its projectile, mine, Spellbat or cloud client particle loops (F117, F122–F124).
- Source-extracted legacy reference map (sprite indices refer to the original sprite grid, not Minecraft's vanilla particle registry):

| Effect | Original sprite indices | Distinctive rendering contract |
| --- | --- | --- |
| [Fire](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusEffectFire.java#L86) | 640–649 | Age 10, negative gravity −0.2, alpha 0.7, animated flame |
| [Frost](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusEffectFrost.java#L98) | 8 | Age 40–79, gravity 0.033, rotating and wandering snowflake |
| [Air](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusEffectAir.java#L79) | 337–341, grid 32 | Age 20–29, alpha fades 0.5→0, scale grows |
| [Earth](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusEffectEarth.java#L75) | 75–78 (one randomized sprite), layer 1 | Gravity 0.4, scale shrinks, alpha fades |
| [Flux](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusEffectFlux.java#L73) | 128–141 | Purple, looping, multistage alpha, random delayed spawn |
| [Curse](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusEffectCurse.java#L111) | 72–75 (one randomized sprite), grid 16 | Dark-magenta randomized RGBA, 8-tick pulse |
| [Heal](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusEffectHeal.java#L78) | 0 | White, 10–19 ticks, alpha 0→0.7→0 |
| [Break](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusEffectBreak.java#L74) | 704/707/710/713 starting index, 3 frames | Age 6–11, three-frame fragment animation |
| [Exchange](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusEffectExchange.java#L79) | 448–456 | Gray, age 9, 0→0.6→0 alpha |
| [Rift](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusEffectRift.java#L104) | 384–399 (one randomized sprite) | Blue, age 16–31, alpha fades, randomized movement |

- This is an implementation specification for restoring element-specific rendering, **not** a claim that these assets are absent. The exact source sprites, texture alpha, blend layers and movement updates still require independent port verification.

**Test gate:** exercise one focus of each of the ten effect keys through TOUCH and at least one persistent medium, capturing per-sprite appearance, color, age, alpha and motion under a fixed camera.

**Sources:** [legacy FocusEffect API](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/api/casters/FocusEffect.java#L1); [modern registered effect keys](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/casters/TCFocusElements.java#L1); [original Flux particle](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusEffectFlux.java#L73).

---

### F122 — Focus Projectile replaces render-driven, effect-colored particles with random vanilla CRIT

**Status:** `confirmed_legacy_mismatch`; comparative runtime density requires client capture.

- Original `EntityFocusProjectile.renderParticle(float coeff)`, called by `RenderNoProjectile` only when the partial-render-tick difference exceeds **0.2**, chooses a random `FocusEffect` from the projectile's `FocusPackage`. It emits `drawFireMote` using that effect's element color at an interpolated position, and once per entity tick adds the chosen effect's own `renderParticleFX`. This includes both a shared colored mote and the effect-specific sprite family (F121).
- The modern projectile is registered with an intentionally invisible entity renderer and instead emits `ParticleTypes.CRIT` from its client `tick()` with probability **0.8**. The projectile has a synchronized `COLOR` property, yet its current particle call does not read it. Its `remainingNodes` payload is stored as entity state, not a client-visible effect list driving rendering.
- A tick-based 80% random emission is not the original render-based, interpolated two-family emission. This is a confirmed source-level visual substitution for player-cast PROJECTILE, regardless of whether projectile movement and collision otherwise function.

**Test gate:** compare PROJECTILE+FIRE and PROJECTILE+FROST in motion at 30/60/120 FPS; verify interpolation, effect colors, sprite identity and particles per second.

**Sources:** [legacy projectile rendering](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/projectile/EntityFocusProjectile.java#L236); [legacy RenderNoProjectile callback](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/projectile/RenderNoProjectile.java#L18); [modern projectile tick](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCFocusProjectileEntity.java#L90); [modern renderer registration](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCEntityRenderers.java#L35); [modern PROJECTILE cast](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/casters/TCFocusCastExecutor.java#L156).

---

### F123 — Focus Mine replaces armed, delayed effect-specific particles with randomized WITCH, and arms on a different schedule

**Status:** `confirmed_legacy_mismatch` for visual source and timing; arming logic is also a nonvisual gameplay regression candidate.

- The original `EntityFocusMine` stores the focus package and initializes a **40-tick counter**. Collision calls `setIsArmed(true)`; only once armed and the counter reaches zero does its client emit `FocusEffect.renderParticleFX` **every five entity ticks**, choosing randomly among the configured effect nodes.
- The modern `TCFocusmineEntity` instead arms on the server after **`tickCount > 20`** regardless of block impact. Once its synced armed flag becomes true, the client has a **20% chance each tick** to spawn one vanilla `ParticleTypes.WITCH` at a fixed +0.1 Y offset. It does not inspect effect nodes and does not gate visuals with the legacy 40-tick counter. A 20%-chance process is not the same temporal sequence as a five-tick interval.
- This creates two separate review targets: **(a)** mine particle identity/sequence and **(b)** arming/trigger behavior. The latter should be evaluated by the gameplay team before a visual-only fix alters any collision logic.

**Test gate:** cast MINE+FLUX and MINE+FROST onto a block, timestamp first contact, arm transition, first particle, subsequent particle interval and detonation in both versions.

**Sources:** [original mine impact, counter and particles](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/projectile/EntityFocusMine.java#L34); [modern arm and WITCH path](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCFocusmineEntity.java#L79); [modern hit handling](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCFocusmineEntity.java#L117).

---

### F124 — Spellbat ambient FX ignores the configured focus effect and emits sparse ENCHANT particles

**Status:** `confirmed_legacy_mismatch` for ambient FX; bat body rendering is separate (F125).

- Original `EntitySpellBat.onUpdate` caches `focusPackage.getFocusEffects()` on the client, computes its aggregate color from each effect's element color, then **every surviving client tick** picks a random effect and runs `eff.renderParticleFX` around the bat with ±0.125 positional Gaussian jitter. This means Fire, Frost, Flux and mixed spellbats can have different particle families.
- Modern `TCSpellbatEntity.tick()` unconditionally uses a **30%-chance** vanilla `ParticleTypes.ENCHANT` with a horizontal ±0.2 uniform offset and upward 0.05 velocity. `remainingNodes` and spell power are stored for server execution but the client FX branch does not decode effect nodes or emit the effect-specific sprite, and no equivalent computed effect-color field is used by the renderer.
- This is a visible behavior mismatch on the active SPELLBAT medium, independent of AI target selection. Replacing the enchant motes must not regress server-authoritative effect execution.

**Test gate:** summon SPELLBAT+FIRE, SPELLBAT+FROST and mixed-effect SPELLBAT; compare frequency, sprite sheet, colors and velocity around each moving bat.

**Sources:** [original Spellbat effect sampling](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/monster/EntitySpellBat.java#L172); [modern Spellbat tick](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCSpellbatEntity.java#L79); [modern caster SPELLBAT construction](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/casters/TCFocusCastExecutor.java#L178).

---

### F125 — Spellbat renderer reuses opaque Firebat texture instead of its translucent, effect-colored original

**Status:** `confirmed_legacy_mismatch` renderer/texture/blending source-level; runtime screenshots required for exact perceived opacity.

- Original `RenderSpellBat` uses `textures/entity/spellbat.png`, the `ModelFireBat`, **alpha blending `SRC_ALPHA, ONE_MINUS_SRC_ALPHA`**, and entity `color` RGB with **alpha 0.5**. It also scales the model to **0.35** and adjusts its pose. The `color` field is computed from the spell's included effects on the original client (F124).
- In the port `TCEntityRenderers` registers `TCEntityTypes.SPELLBAT` with the **same `TCFirebatRenderer` as ordinary FIREBAT**. That renderer always chooses `textures/entity/firebat.png` and `RenderType.entityCutoutNoCull`, with no Spellbat-specific blending or element-color tint. It does preserve a 0.35 model scale, which is a positive geometric control, but that does not restore translucency or the distinct texture.
- This visual difference is independent of the particle issue in F124; a particle fix alone would leave the Spellbat body appearance incorrect.

**Test gate:** compare still and animated Spellbat silhouettes against white/black backgrounds, measuring material alpha, RGB tint and wing/body model geometry separately from surrounding particles.

**Sources:** [original RenderSpellBat](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/mob/RenderSpellBat.java#L19); [modern renderer registration](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCEntityRenderers.java#L32); [modern TCFirebatRenderer texture and render type](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCFirebatRenderer.java#L22); [modern Spellbat subclass](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCSpellbatEntity.java#L28).

---

### F126 — BOLT sends a synchronized FocusZap packet, but the only registered client handler discards it

**Status:** `confirmed_legacy_mismatch` broken active BOLT beam rendering chain; client reproduction remains an explicit gate.

- Original `FocusMediumBolt.execute` computes a target from ray tracing, averages the RGB of its focus effects, and broadcasts `PacketFXZap` with endpoints, color and width. The original packet's client handler invokes `FXDispatcher.arcBolt`, which produces the intended beam.
- Modern `TCFocusCastExecutor` calls `TCEntityFXNetwork.sendFocusZap` on BOLT casts. `TCFocusZapPayload` correctly declares a codec for the same endpoint/color/width data and the network helper distributes it to players within a 64-block range. **However, the `playToClient(TCFocusZapPayload.TYPE, …, (payload, context) -> { })` handler is empty.** Search of the current modern source found no second consumer of `TCFocusZapPayload`.
- This is a more severe condition than using an inaccurate particle: the current application-level BOLT beam data reaches a no-op handler. Contrast the Wisp zap payload in the same registrar, which delegates to a client sink installed by `TCClientEvents`. An analogous focus-specific client renderer must be provided and tested, without changing the server ray/hit determination.

**Test gate:** send BOLT+FIRE and BOLT+FROST casts in single-player and dedicated-server client; log payload receipt and beam render invocation; compare width, RGB and endpoints with original `arcBolt`.

**Sources:** [original Bolt cast packet](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusMediumBolt.java#L40); [original PacketFXZap client arcBolt](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/lib/network/fx/PacketFXZap.java#L48); [modern BOLT dispatch](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/casters/TCFocusCastExecutor.java#L149); [modern no-op packet registration](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCEntityFXNetwork.java#L28); [modern FocusZap codec](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/casters/TCFocusZapPayload.java#L9); [Wisp sink positive control](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java#L23).

---

### F127 — TOUCH cast and impact effect-specific feedback packets are not reproduced by the general modern executor

**Status:** `confirmed_legacy_mismatch` in inspected TOUCH/general effect execution; do not extrapolate to unrelated particle producers.

- Original `FocusMediumTouch.execute` gathers the effect keys of its `FocusPackage` and sends `PacketFXFocusEffect` to nearby clients at the casting source. The packet allocates `max(1, 10 / effectCount)` calls to each effect's `renderParticleFX`, with directional motion and random dispersion. Additionally, multiple original `FocusEffect.execute` implementations (notably Fire, Frost, Air, Earth, Flux, Heal, Break) broadcast `PacketFXFocusPartImpact` at the hit point, where the client makes `max(1, 15 / effectCount)` effect-specific particles. Curse has a distinct block BAMF route, so not every effect uses the same packet.
- The port's active `TCFocusCastExecutor.cast` routes TOUCH through `executeTouchAtTarget(..., true)`. That method emits **only eight vanilla `ParticleTypes.FLAME` for FIRE**, at the target, and no matching cast-source particle feedback for other effect keys. Its `executeSingleEffect` is a server-side gameplay operation; it does not encode and broadcast the original effect-keyed cast/impact particle families. BOLT's independent beam packet is also currently a no-op (F126).
- This report distinguishes **cast-source feedback**, **hit-point feedback**, and **persistent-medium ambient particles**. They are separate legacy events with different velocities and densities; porting a generic trail for one cannot establish parity for the other two.

**Test gate:** compare TOUCH+FIRE vs TOUCH+FROST at no-target, block-target and entity-target positions, logging cast-source and impact packets separately; then verify a multi-effect focus produces each effect family.

**Sources:** [original TOUCH emission](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusMediumTouch.java#L96); [legacy cast-source packet behavior](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/lib/network/fx/PacketFXFocusEffect.java#L75); [original Fire impact call](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/casters/foci/FocusEffectFire.java#L51); [legacy impact packet behavior](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/lib/network/fx/PacketFXFocusPartImpact.java#L65); [modern TOUCH branch](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/casters/TCFocusCastExecutor.java#L140); [modern eight-FLAME conditional](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/casters/TCFocusCastExecutor.java#L406).

---


### F128 — Grappling hook renderer and live rope are absent despite a functioning invisible hook entity

**Status:** `confirmed_legacy_mismatch` registered renderer path; no claim yet about equivalent rope rendering in unseen extension code.

- Original `RenderGrapple` draws **three separate visual elements**: the animated `ModelGrappler` using `textures/entity/grappler.png`; a luminous/additive billboard at the hook; and a textured **3D polycone rope** using `textures/misc/rope.png` from the hook back to the thrower. `calcPoints` interpolates both ends with partial ticks, shifts the hand anchor for first-person main/off-hand view, and applies a damped sinusoidal displacement using `grapple.ampl`.
- Modern `TCEntityRenderers` binds `TCEntityTypes.GRAPPLE` to `TCInvisibleEntityRenderer`, not a grapple-specific renderer. `TCGrappleEntity` does maintain a server/client pulling state and may spawn a small **30% CRIT** particle during pulling, but contains no hook model, rope mesh, interpolated rope points, or rendering source. The port also does not synchronize the original projectile's spawn-time `hand` field, limiting faithful first-person anchor reconstruction.
- This is a major silhouette/motion difference: a successfully pulling invisible entity does not reproduce a physically visible, moving rope. Treat the server pull mechanic as a separate contract; restoring only a spark or item predicate (F29) would not repair this world visual.

**Test gate:** fire Grapple from main and off hands in first- and third-person views, capture flight/attach/pull/release; inspect hook geometry, line attachment, sag/wave amplitude, and length against original.

**Sources:** [legacy renderer, hook and rope](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/projectile/RenderGrapple.java#L23); [legacy hand and spawn data](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/projectile/EntityGrapple.java#L17); [modern invisible registration](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCEntityRenderers.java#L39); [modern grapple tick](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCGrappleEntity.java#L23).

---

### F129 — Bottle of Taint's 100-fragment purple explosion and eight glass fragments become 24 ITEM plus 24 MYCELIUM particles

**Status:** `confirmed_legacy_mismatch` particle family, count and fade behavior; actual entity-event delivery still needs a client test.

- Original `EntityBottleTaint.handleStatusUpdate(3)` calls `FXDispatcher.taintsplosionFX` **100 times**, then `bottleTaintBreak`. Each `taintsplosionFX` spawns a `FXBreakingFade` using **SLIME_BALL** texture, purple/magenta tints, high-velocity burst and a randomized lifetime. `bottleTaintBreak` emits **eight** bottle item-crack fragments and plays `ENTITY_SPLASH_POTION_BREAK` locally.
- Modern `TCBottleTaintEntity.handleEntityEvent(3)` keeps the server-to-client break event **3** but instead loops **24 times**, spawning one `ParticleTypes.ITEM` bottle fragment and one `ParticleTypes.MYCELIUM` mote per iteration. The modern entity method does not explicitly play the legacy splash-potion-break sound. The server-side taint splash and Flux Goo placement paths exist separately and are not the subject of this visual finding.
- The source-level mismatch is not just density (**108 original spawned effects vs 48 modern particles**): the old primary 100 particles used purple slimeball material and a specialized fade/lifetime, while the replacement uses vanilla item and mycelium particles. The two paths cannot be considered visually interchangeable without comparative evidence.

**Test gate:** break the bottle over a solid floor and in air; count emission types/particles and record tint, frame, speed, fade duration and potion-break audio in both versions.

**Sources:** [legacy bottle client event](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/projectile/EntityBottleTaint.java#L35); [legacy taintsplosionFX](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/FXDispatcher.java#L645); [legacy bottleTaintBreak](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/FXDispatcher.java#L933); [modern event handler](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCBottleTaintEntity.java#L50); [modern broadcast event](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCBottleTaintEntity.java#L74).

---

### F130 — Golem Orb impact omits the original 31-frame burst while the flying orb renderer remains independent

**Status:** `confirmed_code` missing entity-level impact effect; separate external impact path not demonstrated.

- Original `EntityGolemOrb.onImpact` plays the shock sound and, if client-side, executes `FXDispatcher.burst(pos, 1.0F)`; the dispatcher creates an **age-31**, **grid-16**, **sprite-range 208–238** animated `FXGeneric` particle.
- Modern `TCGolemOrbEntity.onHit` retains the shock sound and server-side discard/damage, but has no client `burst` call, entity event payload or corresponding particle emission in its impact path. The dedicated `TCGolemOrbRenderer` is active during the projectile's lifetime (F131), but it does not itself draw an impact burst at removal.
- Do not infer that the orb body is absent: the missing lifecycle event is the *terminal burst*, not the projectile's persistent billboard.

**Test gate:** film red/blue orbs during hit and expiration, distinguishing sprite disappearance, original expanding burst and any generic Minecraft hit feedback.

**Sources:** [legacy Golem Orb impact](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/projectile/EntityGolemOrb.java#L61); [legacy FX burst implementation](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/FXDispatcher.java#L781); [modern Golem Orb hit](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCGolemOrbEntity.java#L76); [modern renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCGolemOrbRenderer.java#L27).

---

### F131 — Golem Orb's flying red/blue atlas sprite and additive blend are retained (positive control)

**Status:** `confirmed_code` source-level positive control; GL state, lighting and atlas sampling still require screenshots.

- Original `RenderElectricOrb` chooses one of **six horizontal frames** on a **32×32 atlas**, using red row **6** or blue row **7**, additive blending (`SRC_ALPHA, ONE`), depth-write disabled, full camera-facing quad and a `sin(age / 5)×0.2+0.2` bob/scale.
- Modern `TCGolemOrbRenderer` + `TCGolemOrbRenderContract` preserve the same sprite index arithmetic, distinct red/blue atlas rows, six-frame cycle, blend pair and sinusoidal scaling; render data flows from the synced modern `isRed()` state. This gives a concrete control case where direct historical render behavior was re-created instead of replaced by vanilla particles.
- The positive source comparison does **not** cancel F130's missing impact burst. The shader/texture coordinates should still be checked visually because legacy GL and modern RenderSystem are not equivalent by construction.

**Test gate:** freeze red and blue orbs on each of six successive frames against dark and occluded surfaces; compare atlas UVs, blend and bob independently of impact.

**Sources:** [legacy electric orb renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/projectile/RenderElectricOrb.java#L26); [modern renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCGolemOrbRenderer.java#L27); [modern sprite contract](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCGolemOrbRenderContract.java#L1).

---

### F132 — Eldritch Orb's seeded tendrils and animated central billboard are reproduced (positive control)

**Status:** `confirmed_code` positive for source-level geometry and frame parameters; no full equivalence assertion for shading or sampling.

- Original `RenderEldritchOrb` starts from the fixed random seed **187**, renders **12** additive tendrils that grow over the first ten ticks, and draws an alpha-blended central billboard selecting **13 frames** from the **64×64** sprite grid at V row **3**, scaled **0.75**.
- Modern `TCEldritchOrbRenderer` explicitly recreates a seeded 12-tendril triangle-fan path and a 13-frame, 64-grid, 0.75-scale billboard using `TCEldritchOrbRenderContract`. This is a significant positive reference for translating original bespoke GL renderers to NeoForge.
- The old renderer's fixed-function smooth shading, lighting toggles and face culling differ from NeoForge shaders; these still need actual client capture. The correct seeded topology is not evidence of pixel-perfect lighting.

**Test gate:** capture orb at ages 1, 5, 10 and 20 ticks with fixed pose, comparing tendril width, seeded shape, additive glow, lighting and billboard frame.

**Sources:** [original Eldritch renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/projectile/RenderEldritchOrb.java#L24); [modern tendril/billboard renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCEldritchOrbRenderer.java#L30); [modern render constants](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCEldritchOrbRenderContract.java#L1).

---

### F133 — Wisp zap has a complete client sink and FX renderer, unlike the inert FocusZap payload

**Status:** `confirmed_code` end-to-end source-backed positive control for Wisp zap transport/render; geometry remains subject to runtime comparison.

- Original `PacketFXWispZap` identifies two entities, tints an `arcBolt` according to the Wisp's aspect, and uses beam width **0.6**. The modern `TCEntityFXNetwork` registers `TCWispZapPayload` with a real callback to a Wisp client sink, and `TCClientEvents` binds that sink to `TCWispClientEffects.accept`.
- The modern client resolves both entity IDs, extracts the Wisp aspect RGB, builds a width-**0.6** `ZapBolt`, advances its short three-tick lifetime and renders its segmented additive beam during the client level render stage. This is a concrete alternate client path and must not be counted as missing merely because original `FXDispatcher.arcBolt` was not ported by name.
- In contrast, the FocusZap payload's handler is the empty lambda already identified in F126. Wisp zap's architecture provides an existing tested-in-source pattern for an independent focus-beam sink, **not** proof that both beams should have identical geometry or lifetime.

**Test gate:** trace Wisp zap packet receipt, client entity ID resolution, queue admission, three render frames and world-change cleanup; compare aspect-tinted path/width with the original bolt.

**Sources:** [legacy WispZap handler](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/lib/network/fx/PacketFXWispZap.java#L41); [modern Wisp network registration](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCEntityFXNetwork.java#L28); [client sink wiring](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientEvents.java#L23); [modern Wisp zap renderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/fx/TCWispClientEffects.java#L37).

---

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
