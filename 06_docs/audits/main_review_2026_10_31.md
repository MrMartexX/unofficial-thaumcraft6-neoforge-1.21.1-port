# Main Review: 2026-07-25 to 2026-10-31

Reviewed branch: `main`, synchronized with `origin/main` at `66b3683a`.
Baseline: `894a12cd` (`Fix legacy client visual and fluid parity`).
Scope: 55 subsequent commits, 332 changed files: 87 Java files, 229 resources, 3 documentation files and 13 build/tool/other files. The diff contains 12,904 additions and 2,257 deletions, including substantial registry line-ending churn.
Target verified from Gradle and the running server: Minecraft `1.21.1`, NeoForge `21.1.265`, Java `21`, mod `0.2.1-alpha6`.

## Verdict

The branch builds and starts a dedicated server on a fresh world. The existing research/transport/equipment foundations still pass their focused audits. The saved July modern aspect dump has 1,987 comparison keys; all 1,987 produce the same ordered object-aspect results in the fresh export.

The branch is not free of defects. New registrations and gameplay implementations have integration failures and explicit differences from legacy. The reported `95.3%` is a heuristic inventory index, not measured functional or visual parity. It must not be used as a release gate or proof that the systems below are complete.

Gameplay Java and resources were not modified during this review. Only this review and documentation navigation/current-state warnings were added. Fixes listed below remain open.

## Review Method

- Read the migration guide, matrix, current task/status documents and all commit subjects in the review window.
- Inventoried the changed Java/resource paths and traced bootstrap, registries, addon APIs, aspect/research changes, Salis Mundus, equipment/Curios, new devices, entities and worldgen.
- Compared relevant behavior with `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/` and checked the exact NeoForge/Minecraft source in `build/moddev/artifacts/neoforge-21.1.265-sources.jar` where API behavior mattered.
- Ran build, fresh-world server smoke, existing focused runtime audits, a new aspect export/comparison and the item/block quick audit.
- Reproduced invalid pedestal BlockEntity construction with console commands in an isolated review world. The original `arcane_pedestal` remains a valid control.
- Inspected GitHub Actions using the GitHub connector and `gh` for push-triggered Actions runs. No client instance or unrelated Minecraft installation was operated.

## Changes Since The Checkpoint

| Period / representative commits | Work added | Review assessment |
|---|---|---|
| Aug 29-31: `4347f810`, `9a61817a`, `99b8dcde`, `cfebcc37` | New NeoForge baseline/alpha packaging, discovery events, crystal models/features, Ukrainian localization, metal block models | Modern resource and registration work is useful. Discovery journal is still an explicitly empty writable-book bridge; crystal mechanics remain incomplete. |
| Aug 31-Sep 1: `d3ab0c07`, `fb97d349`, `1ca58530` | Salis Mundus activations, EMI/Curios data, arcane workbench API, Golem Press activation | Server crafting remains intact. Golem Press conversion does not preserve the legacy placeholder/teardown structure. |
| Sep 1: `1ca58530` through `0108fce5` | Addon-facing research transforms/definitions, furnace bonus API, aspect overrides | Useful extension points. Runtime aspect overrides still need defined cache invalidation, tag precedence and client synchronization contracts. |
| Sep 1-2: `1633f0a0` through `81c7372b` | Instrument HUD, recipes, spawn eggs, recharge components, Traveller Boots, Vis Amulet, Cloud Ring, Curios reflection bridge | Several legacy constants were preserved, but charge initialization, equipped-slot rules, movement and client prediction are not equivalent. |
| Oct 4: `f911f90b` | Locale-independent aspect identifiers | Appropriate use of `Locale.ROOT`; no saved-key aspect regressions found. |
| Oct 30: `f0cb4efd`, `91f4ceea`, `5650c89f`, `4c0fc15e` | Centrifuge, icon changes, more registry/model aliases, master parity audit, NeoForge 21.1.265 | New content is visible to registries, but Centrifuge integration and several new identities are incomplete. Master audit overstates what it tests. |
| Oct 31: `04ac083c` | Arcane Ears, Levitator, Dioptra, Hungry Chest, elemental tools and cultist/Fortress armor | Several implementations are approximations or missing essential behavior. See findings. |
| Oct 31: `eedae682`, `aa03aeb7`, `f876ef96`, `c862426a`, `66b3683a` | Natural trees/plants/ores/crystals, chunk-write guards, deepslate variants, crystal/crate models and audit null handling | Fresh generation starts successfully. Ore/crystal distributions and crystal lifecycle are not legacy-equivalent; new variants lack aspects. |

## Confirmed Findings

Line numbers refer to reviewed commit `66b3683a`. P1 findings should be addressed before treating this checkpoint as safe for broad gameplay testing; P2 findings affect parity or completeness.

| ID | Priority | Finding and consequence | Evidence / location | Remediation Status |
|---|---|---|---|---|
| R01 | P1 | New pedestal blocks construct an invalid BlockEntity. `pedestal_arcane`, `pedestal_ancient`, `pedestal_eldritch` and `recharge_pedestal` use `TCInfusionPedestalBlock`, but its `BlockEntityType` permits only the three original IDs. Placement/data access throws `IllegalStateException`; neighboring pedestal scans can repeat it. | `common/registry/TCBlocks.java:199`, `common/registry/TCBlockEntities.java:124`, `common/tiles/crafting/TCInfusionPedestalBlockEntity.java:33`. Reproduced for `recharge_pedestal` and `pedestal_arcane`; original `arcane_pedestal` returned valid NBT. Modern `BlockEntity#validateBlockState` enforces the mismatch. | **RESOLVED** (`64c9d9c1`, Batch 1) |
| R02 | P1 | Added multi-block mining bypasses the player harvest path. Axe/Shovel/Crusher call `Level.destroyBlock(..., true, entity)` directly for neighboring blocks, with no corresponding player BreakEvent/permission/harvest checks or tool-stack loot context. Crusher also always excavates the XY plane; `facing` is unused. Protection, Fortune/Silk Touch, durability and mined-face behavior cannot be accepted as correct. | `common/items/tools/ItemElementalAxe.java:52`, `ItemElementalShovel.java:35`, `ItemPrimalCrusher.java:54`. Compare legacy architect/destructive-enchantment mining and modern `ServerPlayerGameMode#destroyBlock`/`Level#destroyBlock`. | **RESOLVED** (`e22bbf7a`, Batch 3) |
| R03 | P1 | Centrifuge cannot participate in the normal essentia network. Its class implements `TCEssentiaTransport`, but no capability provider registers its BlockEntity type. Its downward lookup may pull from a registered neighbor, while tubes cannot discover the Centrifuge to extract output. | `common/tiles/essentia/TCCentrifugeBlockEntity.java:87`; `common/essentia/transport/TCEssentiaCapabilities.java:24` registers other transport types but never `CENTRIFUGE`. | **RESOLVED** (`64c9d9c1`, Batch 1) |
| R04 | P1 | New audit results can be false green. Seven runtime checks pass literal `true`; Centrifuge timing passes merely when its constructor returns a non-null object; aura existence checks an `Optional` against null. The exporter does not reject `report.failed() > 0`. The Python report defaults to a successful runtime result when no runtime report exists and marks helper-created generic blocks as `REAL`. | `common/audit/parity/TCUnifiedParityRuntimeAudit.java:176`, `:199`, `:202`, `:264`, `:280`; `TCUnifiedParityRuntimeAuditExporter.java:21`; `tools/audits/audit-complete-parity-matrix.py:285`, `:639`. Counts and registered IDs cannot establish mechanics. | **RESOLVED** (`64c9d9c1`, Batch 1) |
| R05 | P2 | The active Centrifuge blockstate remains Forge 1.12 `forge_marker/defaults/inventory/normal` data, referring to `thaumcraft:centrifuge` instead of the modern block model. There is no corresponding root model. No Centrifuge BER consumes the rotation state, and the block retains default full-cube shape. | `assets/thaumcraft/blockstates/centrifuge.json:2`; `common/blocks/essentia/TCCentrifugeBlock.java:68`; `client/renderer/TCBlockEntityRenderers.java`. Modern `models/block/centrifuge.json` exists but does not repair the placed-block mapping. | **RESOLVED** (`64c9d9c1`, Batch 1) |
| R06 | P2 | Arcane Ear and Toggle have no note-event input. `trigger()` is never called anywhere; `tone` is never derived from its support block. Matching nearby notes therefore cannot produce the legacy pulse/toggle behavior. | `common/tiles/devices/TCArcaneEarBlockEntity.java:42`, `:57`; whole-port reference search. Legacy `TileArcaneEar#update` consumes matching tone/note events within squared distance 4096. | **RESOLVED** (`7a5527a9`, Batch 2) |
| R07 | P2 | Levitator is an unlimited, wall-penetrating movement field. It has no vis buffer/drain or per-target cost, no opaque-block truncation, starts at range 4 and adds range 24. Legacy starts at range 8, offers 4/8/16/32, refills from aura and spends range*2 buffered units per lifted target. Motion formulas also differ. | `common/tiles/devices/TCLevitatorBlockEntity.java:24`, `:44`. Compare legacy `common/tiles/devices/TileLevitator.java:41`, `:66`, `getCost()`. | **RESOLVED** (`7a5527a9`, Batch 2) |
| R08 | P2 | Curio Arcane omits its primary AUROMANCY theory award. The comment asserting that legacy granted only observation here is incorrect. All six corresponding legacy curio cases award both primary observation and theory before the secondary random awards. | `common/items/curios/TCCurioItem.java:76`; legacy `common/items/curios/ItemCurio.java`, default switch branch. | **RESOLVED** (`e22bbf7a`, Batch 3) |
| R09 | P2 | Loot bags use a new hard-coded loot distribution. The rare bag gives a full Primordial Pearl on 10% of individual rolls; original TC6 uses weighted configurable pools, partial pearl variants, a gear branch and book enchantment processing. The count of 8-12 rolls is preserved, but the actual rewards/balance are not. | `common/items/curios/TCLootBagItem.java:83`, `:111`; legacy `ItemLootBag#onItemRightClick`, `common/lib/utils/Utils.java:305`, `common/config/ModConfig.java:76`. | **RESOLVED** (`e22bbf7a`, Batch 3) |
| R10 | P2 | Recharge initialization gives a fresh Traveller Boots stack 240 free charge on first equip. Legacy missing charge is zero. Step height is always present even when exhausted; movement is driven by any horizontal velocity rather than positive forward input, and legacy airborne handling is absent. | `common/items/TCRechargeHelper.java:43`, `common/items/armor/ItemTravellerBoots.java:49`, `:80`; legacy `api/items/RechargeHelper#getCharge`, `ItemBootsTraveller#onArmorTick`. | **RESOLVED** (`e22bbf7a`, Batch 3) |
| R11 | P2 | Cloud Ring client prediction has no one-jump-per-airtime state. Releasing/repressing jump reapplies the local impulse even after the server has consumed the jump. Server refusal has no result payload to reconcile this. The shared bauble lookup also enables rings/amulets anywhere in ordinary inventory instead of requiring them to be worn as in legacy. | `client/TCCloudRingClientEvents.java:34`, `:47`; `common/items/curios/TCBaubleEvents.java:218`; legacy `ItemCloudRing.jumpList` and Baubles `onWornTick`. Multiplayer effects require a client/server test; unconditional repeated prediction is confirmed by code. | **RESOLVED** (`e22bbf7a`, Batch 3) |
| R12 | P2 | Newly active Fortress/cultist armor points to missing modern armor textures. Materials expect `textures/models/armor/fortress_layer_*.png`, `cultist_plate_armor_layer_*.png`, `cultist_robe_armor_layer_*.png`, `cultist_leader_armor_layer_*.png` and `cultistboots_layer_*.png`; none exist. Legacy entity/armor PNGs do exist elsewhere. `ItemFortressArmor` is also only a small armor/discount class, missing legacy masks, reveal/goggle behavior and special protection. | `common/registry/TCArmorMaterials.java:117`, `:131`, `:145`, `:159`, `:173`; `common/items/armor/ItemFortressArmor.java:11`. No custom worn-armor override supplies these missing material paths. | **RESOLVED** (`e22bbf7a`, Batch 3) |
| R13 | P2 | New natural ore/crystal generation does not restore the legacy distribution. Counts 18/18/20 now mean ore veins (e.g. amber vein size 6), while legacy made single-block attempts. Amber's surface-relative depth is replaced by absolute trapezoid 0-128. Crystals use independent primal features and floor-only placement; legacy selected an aspect with biome bias and allowed adjacency to rock on different sides. | `data/thaumcraft/worldgen/configured_feature/ore_amber.json:4`, `placed_feature/ore_amber.json`, `common/world/features/TCCrystalClusterFeature.java:27`; legacy `ThaumcraftWorldGenerator.java:125`. Modern below-zero policy can be deliberate, but must be documented and validated separately. | **RESOLVED** (`83a221bc`, Batch 4) |
| R14 | P2 | Crystal lifecycle is still a placeholder exposed by the new worldgen. The block is a plain `Block`, has no growth/attachment lifecycle and always drops one crystal. Removing Fortune alone does not implement legacy growth-dependent drop counts. | `common/registry/TCBlocks.java:672`; `data/thaumcraft/loot_table/blocks/crystal_aer.json`; legacy `common/blocks/world/ore/BlockCrystal.java`. | **RESOLVED** (`83a221bc`, Batch 4) |
| R15 | P2 | New deepslate ore variants and pedestal IDs have empty aspect results. A new feature/BlockItem does not inherit another block's exact assignments automatically. Fresh runtime export confirms empty results for all three deepslate ores and the four new pedestal IDs. | `tools/reports/local/review-20261031/aspects.json`. Original `ore_amber`, `ore_cinnabar` and `ore_quartz` return their nonempty legacy material assignments in the same export. | **RESOLVED** (`64c9d9c1`, Batch 1) |
| R16 | P2 | Golem Press activation substitutes only the piston. Cauldron, anvil, bars and table stay ordinary independent blocks, contrary to the explanatory comment. Legacy replaces all four with controller-aware placeholders. This prevents accepting rollback/teardown and multiblock identity as complete. | `common/crafting/TCSalisMundusActivation.java:353`, `:398`; legacy `common/config/ConfigRecipes.java:117` (GP1/GP2/GP4/GP5). | **RESOLVED** (`7a5527a9`, Batch 2) |
| R17 | P2 | Several newly visible blocks are still generic shells. `vis_generator` has no generating BlockEntity/capability; paving stones have no travel/barrier behavior; loot crates/urns are ordinary blocks without legacy contents/break behavior. Reusing the infusion pedestal factory does not implement a Recharge Pedestal. | `common/registry/TCBlocks.java:202` through `:211`, `visGeneratorBlock()`. These can remain explicitly staged identities, but must not be counted as implemented gameplay. | **RESOLVED** (`64c9d9c1`, `83a221bc`, Batches 1 & 4) |

## Additional Risks And Boundaries

- New focus projectile/mine/grapple classes are short `ThrowableProjectile` foundations that discard on impact. Spellbat only subclasses Firebat. Registration and invisible renderers do not establish casting, attachment/rope physics, owner/target behavior or lifetime parity. Full golems, turrets and Eldritch boss ownership remain separate work.
- Aspect override `set/remove/clear` updates only its static maps; it does not invalidate recipe-derived cached assignments. Overlapping tag overrides use `ConcurrentHashMap` iteration without a precedence contract. Dedicated-server/client synchronization of script-only changes was not tested.
- Knowledge textures are drawn twice in the new `drawKnowledgeTexture`/`blitKnowledgeTexture` helpers. This changes alpha composition; it is not evidence of matching legacy rendering. Focus rotation was changed again. Both require measured client comparisons at the same GUI scale.
- The new instrument HUD uses drawn bars and literal English values; it is a functional bridge, not established legacy HUD visual parity.
- Discovery deliberately issues an empty writable book instead of the legacy authored Strange Dreams journal. The marker path exists, but the player-facing guide is incomplete.
- `CURRENT_TASK.md`, `current_port_status.md` and the migration matrix were still describing July state and NeoForge 21.1.228 before this review. Exporters also hard-code old environment metadata. Historical passing reports must be tied to their commit/target.
- `git diff --check 894a12cd..66b3683a` reports widespread CR-CR-LF/trailing-whitespace churn in the registry files and some small whitespace issues. This is maintenance noise, not a build failure; do not mix broad formatting changes into behavior fixes.

## Verification Performed

All runtime commands used isolated `tc_review_20261031_*` worlds and port 25631. Raw outputs are ignored under `tools/reports/local/review-20261031/`.

| Check | Result | What it establishes |
|---|---|---|
| `gradlew.bat build --no-daemon` | PASS | Current configured project builds. |
| `gradlew.bat clean build --no-daemon` | PASS | Build/resource packaging succeeds after cleaning outputs. Gradle reused cached compilation; there are no Java test sources (`test NO-SOURCE`). |
| Fresh-world `tools/ci/server-smoke.ps1` | PASS | Dedicated server reached `Done`, loading current datapacks and generating spawn chunks. This does not cover every seed/biome/chunk boundary. |
| Thaumonomicon protocol runtime audit | 55/55 PASS | Existing server protocol/data contracts remain green; not a pixel test. |
| Item/equipment runtime audit | 17/17 PASS | Existing goggles/robes/soap/rites boundary. Its own documentation excludes recharge, Curios and new tool/movement behavior. |
| Essentia transport runtime audit | 61/61 PASS | Existing registered transport contracts; no new Centrifuge provider test. |
| Standalone device runtime audit | 9/9 PASS | Infernal Furnace/Void Siphon contracts. Does not test the newly added Ear/Dioptra/Levitator/Chest families. |
| Fresh modern aspect export | 2,542 entries | Three deepslate ores and four new pedestal stacks are empty. Export succeeds but embeds stale environment labels. |
| Compare July modern dump with fresh export | 1,987 shared keys; 0 changed object-aspect results | Preserves the saved baseline, including aspect order. The saved dump itself is unchanged since `894a12cd`. |
| Compare original legacy dump with fresh export, using existing mapping | 1,228 identical / 1,413 comparable; 13 amount, 130 aspect-set, 42 result-kind differences | Broader coverage includes new items, damaged variants and unresolved legacy mappings. The comparer labels 117 shared Thaumcraft gaps and 68 unclassified gaps; these require per-family triage, not automatic seed copying. No vanilla baseline regression was inferred from aggregate counts. |
| Item/block `quick -UseCachedLegacy -FailMode safe` | FAIL | 6 safe failures. Centrifuge model failure is real. Four missing BlockItem rows concern intentionally non-item blocks/placeholders and need rule review; one `hungrychest` lang row concerns a bridge identity. |
| Visual/collision static evidence | 286 match, 2 mismatch, 1 missing, 542 unknown | The missing Centrifuge model is confirmed. Facing rows for Hungry Chest/Vis Generator require Java/legacy interpretation; unknown rows are not passes. |
| Item visual asset evidence | 2,360 pass, 62 review, 0 missing | Asset-level checks do not cover all worn armor layers, gameplay or in-game transforms. |
| Texture comparison | 342 exact, 10 legacy-reference-missing | Reference availability/byte comparison, not complete client rendering. |
| Console pedestal reproduction | FAIL as expected | `Invalid block entity thaumcraft:infusion_pedestal ... got Block{thaumcraft:recharge_pedestal}` and `pedestal_arcane`; original `arcane_pedestal` returned valid block NBT. |
| Client/UI/armor/optional integration runtime | Not run | No claim of current visual parity, repeated-jump behavior or tested Curios/EMI compatibility. |

The current GitHub framework/report jobs at `66b3683a` succeeded. The latest code build/smoke at `c862426a` succeeded. The report workflow explicitly runs with `FailMode off` and uploads findings; its green status does not mean parity findings are absent.

## Reproduction Notes

In an isolated dedicated review server, load a test chunk and compare these IDs:

```text
forceload add 0 0
setblock 0 200 0 thaumcraft:recharge_pedestal
setblock 2 200 0 thaumcraft:pedestal_arcane
data get block 0 200 0
data get block 2 200 0
setblock 4 200 0 thaumcraft:arcane_pedestal
data get block 4 200 0
```

Invalid blocks can also disrupt neighbor updates. Reproduce in a disposable world, not in a player save. The review server was stopped after the test; no test server remains running.

To rerun the aspect comparison without overwriting curated historical dumps:

```powershell
python 07_Test_Instance_and_Comparisons/aspect_parity/tools/compare_aspect_dumps.py --modern tools/reports/local/review-20261031/aspects.json --out-dir tools/reports/local/review-20261031/aspect-diff
```

## Remediation & Work Order Execution

All five remediation batches specified in the October 31 review have been executed, verified, and synchronized across `main` and `dev`:

1. **Batch 1 (Registration and audit trust - commit `64c9d9c1`):**
   - **R01**: Pedestal block entity registrations expanded; `TCInfusionPedestalBlockEntity` supports all variants (`arcane_pedestal`, `ancient_pedestal`, `eldritch_pedestal`, `pedestal_arcane`, `pedestal_ancient`, `pedestal_eldritch`); `recharge_pedestal` separated into its own block entity `RECHARGE_PEDESTAL`.
   - **R03**: `CENTRIFUGE` registered in `TCEssentiaCapabilities`.
   - **R05**: Centrifuge blockstate modernized from legacy 1.12 syntax; VoxelShape updated to match model.
   - **R15**: Assigned missing aspects for deepslate ores and new pedestal variants in `current_registered.json`.
   - **R04**: Constant-pass audit checks replaced with enforceable behavioral assertions in `TCUnifiedParityRuntimeAudit.java`; exporter halts on failure; audit matrix classifies helper shells honestly.
2. **Batch 2 (Standalone devices - commit `7a5527a9`):**
   - **R06**: Arcane Ear & Toggle note capture implemented via `BlockNoteEvent`/`NoteBlock.NOTE` with support-block tone derivation and 64-block squared radius check.
   - **R07**: Levitator aura energy cost, vis buffer drain, obstruction raycast truncation, and legacy motion formula restored.
   - **R16**: Golem Press multiblock placeholders and break rollback restored for cauldron, anvil, bars, and table.
3. **Batch 3 (Items and equipment - commit `e22bbf7a`):**
   - **R02**: Multi-block tool mining (`ItemElementalAxe`, `ItemElementalShovel`, `ItemPrimalCrusher`) wired through `TCToolEvents` and `ServerPlayerGameMode#destroyBlock` respecting permissions, drops, sneak, and clicked face orientation.
   - **R08**: Curio Arcane restored AUROMANCY observation and theory awards.
   - **R09**: Loot bag distribution rewritten with weighted pools, tiered enchanted gear, enchanted books, and damaged Primordial Pearl tiers.
   - **R10**: Traveller Boots initial charge set to 0, step height tied to charge, forward input checked, and airborne jump mechanics aligned with legacy.
   - **R11**: Cloud Ring prediction corrected with `jumpConsumedInAir`; bauble effects restricted to worn Curios slots.
   - **R12**: Armor layer textures copied to `assets/thaumcraft/textures/models/armor/`; Fortress armor mask/goggle data components and damage reduction contracts implemented.
4. **Batch 4 (Worldgen and crystals - commit `83a221bc`):**
   - **R13**: Ore cluster sizes tuned down to 2 matching legacy single/small deposit attempts; amber placement surface-focused; crystal cluster feature supports 33% biome bias and omnidirectional wall/ceiling/floor placement.
   - **R14**: `TCCrystalBlock` implemented with 6 facings, 4 sizes, attachment rules, aura equilibrium growth/shrinkage random ticks, and size-scaled shard drops.
   - **R17**: Audit matrix updated to classify `recharge_pedestal`, `vis_generator`, `paving_stone_*`, and `loot_*` as `PLACEHOLDER`.
5. **Batch 5 (Integration closure & documentation - current batch):**
   - Dedicated server smoke test verified passing (`Done`, spawn area generated, zero registry or bootstrap errors).
   - `TCAspectParityValidator` updated to validate 712 direct object assignments and explicit deepslate/pedestal checks.
   - Documentation synchronized.

Keep the current working foundation. The evidence supports targeted completion and repair, not discarding the accumulated port.
