# V0 legacy machine TESR semantic diff (2026-11-04)

> Source-only evidence checkpoint. This is neither a rendered client comparison nor permission to modify game code. Source ref: `main` at `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`. Draft PR #11 remains the review surface.

## Method and confidence

- Read all eight original 1.12.2 renderer implementations and the corresponding 1.21.1 block entities, block classes, current `TCBlockEntityRenderers`, `TCBlockEntities`, blockstate JSON and block model JSON.
- Original `TileEntitySpecialRenderer` effects are conditional (inventory, neighbor connectivity, aspect filters, energy). A registered static model is not equivalent to all runtime states.
- Confirmed modern BER registry has ten explicit entries, with `TCTubeValveRenderer` bound **only** to `TUBE_VALVE`. Each of these eight types lacks a dedicated modern BER. In reviewed code paths the original dynamic effects are not reproduced. Other uninspected custom render pathways cannot be categorically excluded, and no in-game client comparison has run.
- Do not equate rendering deficiencies with gameplay/data loss. For example, Centrifuge rotation values and Dioptra grid samples still exist in ported block entities.

## Findings (visual source-level risk)

| ID | Original TESR feature | Modern evidence | Priority |
| --- | --- | --- | --- |
| V0-MCH-01 | Dioptra | dynamic grid unrendered | P1 |
| V0-MCH-02 | Focal Manipulator | focus and orbit fx unrendered | P1 |
| V0-MCH-03 | Hungry Chest | custom chest silhouette and lid missing | P1 |
| V0-MCH-04 | Tube Oneway | directional blue rod missing | P2 |
| V0-MCH-05 | Tube Buffer | choke red blue rods missing | P1 |
| V0-MCH-06 | Alembic | aspect label and dynamic nozzle missing | P1 |
| V0-MCH-07 | Centrifuge | rotation computed but not drawn | P1 |
| V0-MCH-08 | Pattern Crafter | mode icon and rotating gears missing | P1 |

## Evidence by mechanism

### V0-MCH-01: Dioptra (P1)

Original uses a 12×12 quad mesh sampled from 13×13 grid_amt, additive tint and oscillating perimeter; current BE computes/synchronizes 169-byte grid, but enabled/disabled baked models have no grid render path.

Evidence:
- Legacy: [TileDioptraRenderer.java, lines 28-105](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileDioptraRenderer.java).
- Ported state: [TCDioptraBlockEntity.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCDioptraBlockEntity.java).
- Current assets: [dioptra.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/dioptra.json) and [dioptra.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/dioptra.json).
- Registry comparison: [TCBlockEntityRenderers.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java) and [TCBlockEntities.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java).
- Client gate: Place Dioptra across two aura/flux gradients, switch mode, compare 3D height-field/waves, top and side tints.

### V0-MCH-02: Focal Manipulator (P1)

Original floats and spins focus slot item, orbiting colored crystal items and additive billboards with two rays each. Current wand_workbench baked model and GUI implement workstation/UI, but no corresponding BER; BE has crystalCosts map and no explicit BE update packet/tag overrides in inspected class.

Evidence:
- Legacy: [TileFocalManipulatorRenderer.java, lines 35-141](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileFocalManipulatorRenderer.java).
- Ported state: [TCFocalManipulatorBlockEntity.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCFocalManipulatorBlockEntity.java).
- Current assets: [wand_workbench.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/wand_workbench.json) and [wand_workbench.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/wand_workbench.json).
- Registry comparison: [TCBlockEntityRenderers.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java) and [TCBlockEntities.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java).
- Client gate: Insert focus, start crafting with >1 aspect crystal, compare hovering focus, orbits, colored billboards/rays; test GUI independently.

### V0-MCH-03: Hungry Chest (P1)

Original ModelChest with chesthungry.png, opening lid animation (prevLidAngle/lidAngle). Current baked model is one 14×14×14 oak_planks-textured cuboid; block RenderShape.MODEL and no BER, no lid state in BE. Open/close sound and storage exist separately.

Evidence:
- Legacy: [TileHungryChestRenderer.java, lines 17-82](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileHungryChestRenderer.java).
- Ported state: [TCHungryChestBlockEntity.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/devices/TCHungryChestBlockEntity.java).
- Current assets: [hungry_chest.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/hungry_chest.json) and [hungry_chest.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/hungry_chest.json).
- Registry comparison: [TCBlockEntityRenderers.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java) and [TCBlockEntities.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java).
- Client gate: Place/open/close Hungry Chest and watch lid, lock/seam, wood/teeth texture and item-eating state from 4 directions.

### V0-MCH-04: Tube Oneway (P2)

Original TESR draws blue-tinted ModelTubeValve rod only when matching neighboring transport exists, oriented by tube facing. Current multipart tube_side/tube_core geometry has connection arms but no data-driven rod; TCTubeValveRenderer is only bound to TUBE_VALVE, a distinct BE type.

Evidence:
- Legacy: [TileTubeOnewayRenderer.java, lines 19-50](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileTubeOnewayRenderer.java).
- Ported state: [TCLegacyTubeBlockEntity.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java).
- Current assets: [tube_oneway.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/tube_oneway.json) and [tube_core.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/tube_core.json).
- Registry comparison: [TCBlockEntityRenderers.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java) and [TCBlockEntities.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java).
- Client gate: Test one-way tube in all 6 orientations, with and without attached compatible transporter; compare directional blue indicator.

### V0-MCH-05: Tube Buffer (P1)

Original TESR renders red/blue valve rods for each connected, open, choked side (two choke modes). Current BE persists openSides/chokedSides and multipart baked model represents arms/central body only; no TUBE_BUFFER BER. VALVE BER does not cover buffer type.

Evidence:
- Legacy: [TileTubeBufferRenderer.java, lines 21-58](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileTubeBufferRenderer.java).
- Ported state: [TCLegacyTubeBlockEntity.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java).
- Current assets: [tube_buffer.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/tube_buffer.json) and [tube_buffer_core.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/tube_buffer_core.json).
- Registry comparison: [TCBlockEntityRenderers.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java) and [TCBlockEntities.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java).
- Client gate: Cycle open/choke state 0/1/2 per connected side, rotate tube/neighbor and compare blue vs red rod indicators.

### V0-MCH-06: Alembic (P1)

Original TESR draws a face-facing aspect label/tag when filtered, plus ModelBoreBase nozzle for neighboring compatible transport outputs. Current BE persists and syncs aspectFilter/labelFacing; block renders a static model and no ALEMBIC BER, so state-specific overlay and nozzle not represented by inspected resource path.

Evidence:
- Legacy: [TileAlembicRenderer.java, lines 27-107](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java).
- Ported state: [TCAlembicBlockEntity.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCAlembicBlockEntity.java).
- Current assets: [alembic.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/alembic.json) and [alembic_normal.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/alembic_normal.json).
- Registry comparison: [TCBlockEntityRenderers.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java) and [TCBlockEntities.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java).
- Client gate: Apply/remove labels on horizontal faces, change aspect, connect/disconnect transport; compare rendered tag, facing and nozzle.

### V0-MCH-07: Centrifuge (P1)

Original ModelCentrifuge renders spinny bit rotated by cf.rotation. Modern clientTick updates rotation/rotationSpeed and block wires client ticker, but baked 6-element static centrifuge model and no CENTRIFUGE BER cannot consume rotation.

Evidence:
- Legacy: [TileCentrifugeRenderer.java, lines 16-46](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileCentrifugeRenderer.java).
- Ported state: [TCCentrifugeBlockEntity.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCCentrifugeBlockEntity.java).
- Current assets: [centrifuge.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/centrifuge.json) and [centrifuge.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/centrifuge.json).
- Registry comparison: [TCBlockEntityRenderers.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java) and [TCBlockEntities.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java).
- Client gate: Run centrifuge with and without essentia, compare rotor rotation, accel/decel timing and sound.

### V0-MCH-08: Pattern Crafter (P1)

Original TESR draws 10-frame mode atlas indexed by pc.type and two 3D textured gears rotated by pc.rot. Modern BE syncs pattern type and block interaction displays text message, but baked static model and no PATTERN_CRAFTER BER do not render the per-mode icon/gears.

Evidence:
- Legacy: [TilePatternCrafterRenderer.java, lines 24-72](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TilePatternCrafterRenderer.java).
- Ported state: [TCPatternCrafterBlockEntity.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCPatternCrafterBlockEntity.java).
- Current assets: [pattern_crafter.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/pattern_crafter.json) and [pattern_crafter.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/pattern_crafter.json).
- Registry comparison: [TCBlockEntityRenderers.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java) and [TCBlockEntities.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java).
- Client gate: Cycle all ten modes, capture facing-dependent icon and compare gears idle/running.

## Cross-feature interpretation

1. **Hardest visible morphology issue:** the original animated Hungry Chest with `chesthungry.png` currently resolves to one oak-plank-textured cuboid. This is more than a missing opening animation.
2. **Existing state, missing visual consumer:** Dioptra keeps its `169`-byte `gridAmt` and Centrifuge maintains `rotation`/`rotationSpeed`; dynamic geometry is not rendered by a current BER.
3. **Existing server function, missing state overlay:** Alembic retains `aspectFilter` and `labelFacing`, Pattern Crafter cycles a ten-state pattern, and Tube Buffer persists per-side choke modes; their old visible clues remain unimplemented in the inspected renderer/model paths.
4. **Tube-family trap:** a functional valve renderer does **not** render rods for separate one-way or buffer BE types. Baked multipart arms do not expose blue directional rods or red/blue per-side choke status.
5. **Focal Manipulator:** current workstation screen is not a substitute for the old floating focus, orbiting aspect crystals and rays in the world. A future renderer must also obtain synchronized client-side focus/crystal data; do not build client visuals on server-only fields.

## Batched A/B runtime acceptance

- [ ] Run original 1.12.2 Forge TC6 and 1.21.1 NeoForge port with equivalent world lighting/FOV, capture front/side/top and enabled/disabled conditions.
- [ ] Dioptra: compare animated height-field under more than one aura/flux sample set and both vis/flux modes.
- [ ] Focal Manipulator: insert focus, craft with multiple aspect crystals, compare item bob/spin, aura billboards and rays.
- [ ] Hungry Chest: place/open/close, compare full silhouette, special UVs, lid hinge/angle, sound and item ingestion.
- [ ] One-way Tube: connected vs isolated, all facing orientations, check blue directional rod.
- [ ] Buffer Tube: attached side open/closed and choke modes 0/1/2, compare red/blue indicators.
- [ ] Alembic: filter label and aspect tag on four sides; connect/disconnect compatible essentia transport for nozzle.
- [ ] Centrifuge: idle → fed → spinning → stop, compare rotor interpolation and spin timing.
- [ ] Pattern Crafter: all 10 modes, facing variations, both gears idle and under operation.
- [ ] Save/reload and multiplayer client sync for data-dependent overlays; check no client/server cross-thread reliance.

## Scope guards and next action

- Preserve current machine behavior and block IDs. Visual fixes should be separate implementation PR(s), preferably grouped by independent BER and synchronization dependencies.
- These eight source findings resolve the *classification research* portion of watchlist W-02, **not** its visual acceptance portion.
- Existing major V0 blockers (Hole/Mirror/Siphon shaders, Arcane Bore beam, original 3D armor, entity effects and client scene matrix) remain tracked in the consolidated master register.
- Local PowerShell extractor, Gradle, full game, runtime and end-user visual parity gates were **not run here**. No source code, models, assets, main branch, or CI modified.
