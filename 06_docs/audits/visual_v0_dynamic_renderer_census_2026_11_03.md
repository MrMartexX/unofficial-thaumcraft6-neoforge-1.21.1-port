# V0: dynamic render / BER / entity parity source checkpoint (2026-11-03)

> **Static source audit.** Pinned `main` SHA: `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`. Do not interpret a registration count as rendering fidelity, and do not interpret lack of a directly named BER as proof of an absent visual: block models, particles and other client event hooks may contribute.

## Scope and source anchors

Compared original [`ProxyTESR.java`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/proxies/ProxyTESR.java) and [`ProxyEntities.java`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/proxies/ProxyEntities.java) with current [`TCBlockEntityRenderers.java`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java), [`TCEntityRenderers.java`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCEntityRenderers.java), [`TCBlockEntities.java`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java) and the relevant original renderers/current entities. This is the **first pass**, not a full behavioral code review of all 43 entity renderers or all client FX paths.

Source registries contain:

| Registry | Original TC6 / Forge 1.12.2 | Current NeoForge 1.21.1 |
| --- | ---: | ---: |
| Entity renderer bindings | 43 | 43 |
| Tile/Block Entity Renderer bindings | 23 (22 renderer classes; original mirror renderer shared) | 10 (9 renderer classes; current jar renderer shared) |

These differences require **per-feature analysis**, not subtraction to derive “missing mechanics”.

## Entity rendering: nine intentionally invisible assignments need classification

The modern `TCInvisibleEntityRenderer` explicitly describes itself as a safe placeholder. Current nine bindings were independently cross-checked with the legacy implementations:

| Modern entity registry | Legacy renderer | Source-level conclusion |
| --- | --- | --- |
| `focus_cloud` | `RenderFocusCloud` | Legacy renderer itself has an empty drawing method; examine separate FX |
| `taint_swarm` | `RenderTaintSwarm` | Legacy renderer itself has an empty drawing method; examine separate FX |
| `focus_projectile` | `RenderNoProjectile` | Legacy uses `RenderNoProjectile`; invisible entity renderer is not by itself a regression |
| `focusmine` | `RenderFocusMine` | **Source-level visual gap**: textured ModelGrappler and colored pulsation |
| `golem_dart` | `RenderDart` | **Source-level visual gap**: textured 3D arrow geometry |
| `grapple` | `RenderGrapple` | **Source-level visual gap**: ModelGrappler plus dynamic rope polycone |
| `alumentum` | `RenderNoProjectile` | Legacy uses `RenderNoProjectile`; invisible entity renderer is not by itself a regression |
| `causality_collapser` | `RenderNoProjectile` | Legacy uses `RenderNoProjectile`; invisible entity renderer is not by itself a regression |
| `mind_spider` | `RenderMindSpider` | **Source-level visual gap**: translucent scaled spider model with viewer gate |

**Four concrete source-level geometry gaps**: `focusmine`, `golem_dart`, `grapple`, `mind_spider`.

- Legacy [`RenderFocusMine`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/projectile/RenderFocusMine.java) renders a textured `ModelGrappler` with changing color/light. Current `TCFocusmineEntity` does emit vanilla witch particles, but has no corresponding rendered model.
- Legacy [`RenderDart`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/projectile/RenderDart.java) constructs textured arrow geometry. Modern `golem_dart` is invisible.
- Legacy [`RenderGrapple`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/projectile/RenderGrapple.java) draws a grappling head, billboard and dynamic segmented rope to the thrower. Modern `grapple` is invisible despite having physics/particles.
- Legacy [`RenderMindSpider`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/mob/RenderMindSpider.java) draws a small translucent spider with viewer-based visibility logic. Current `mind_spider` is always assigned the invisible placeholder.

**Do not misclassify**: original `RenderFocusCloud` and `RenderTaintSwarm` have empty drawing methods; modern `TCFocusCloudEntity.tickClientParticles()` explicitly dispatches cloud particles. Legacy `RenderNoProjectile` is also the source binding for `focus_projectile`, `alumentum` and `causality_collapser`. Their particle/effect parity remains unverified.

The first static resource census still applies: source files `textures/entity/mine.png`, `textures/entity/grappler.png` and `textures/misc/rope.png` are **present** in the port asset tree. The missing piece is not these PNG paths alone.

## Original TESR vs current BER: triage instead of automatic rewrite

The port directly binds corresponding modern BER functionality for the original crucible, infusion pedestal, infusion matrix, bellows, tube valve, jar, research table, thaumatorium and golem builder (**9 legacy renderer families**). These remain unverified against original transforms/visuals.

**Important non-obvious shared binding**: original `TileRechargePedestalRenderer` has no uniquely named modern renderer, but modern `TCBlockEntities.INFUSION_PEDESTAL` includes `TCBlocks.RECHARGE_PEDESTAL`, and `TCInfusionPedestalRenderer` **already draws and rotates the stored item**. Thus it is **not a missing-renderer case**. Its scale, height and rotation rate still need in-game comparison with the original dedicated recharge renderer.

After removing that false positive, **12 legacy TESR renderer families** (13 original bindings because mirrors have two tile classes) have no directly applicable modern BER registration:

| Legacy TESR / tile family | Original dynamic visual | Source-only V0 queue |
| --- | --- | --- |
| `TileDioptraRenderer` | dynamic colored grid | Review alternate baked-model/particle implementation and visual state |
| `TileFocalManipulatorRenderer` | crystal billboards and tinted energy | Review alternate baked-model/particle implementation and visual state |
| `TileHungryChestRenderer` | animated chest lid | Review alternate baked-model/particle implementation and visual state |
| `TileTubeOnewayRenderer` | tinted moving rod | Review alternate baked-model/particle implementation and visual state |
| `TileTubeBufferRenderer` | tinted valve rods | Review alternate baked-model/particle implementation and visual state |
| `TileAlembicRenderer` | filter aspect tag and nozzle | Review alternate baked-model/particle implementation and visual state |
| `TileCentrifugeRenderer` | rotating top mechanism | Review alternate baked-model/particle implementation and visual state |
| `TileMirrorRenderer` | shader/portal surface | **High priority**: both mirror types registered, no BER |
| `TilePatternCrafterRenderer` | item/recipe icon overlay | Review alternate baked-model/particle implementation and visual state |
| `TileVoidSiphonRenderer` | animated star shader | **High priority**: enabled star/shader surface absent from explicit BER bindings |
| `TileBannerRenderer` | cloth/pole/dye/cult texture | No equivalent active banner world block registration |
| `TileHoleRenderer` | end-portal shader on neighbors | **High priority**: `hole` is `RenderShape.INVISIBLE`; no modern shader BER |

The originals contain non-empty rendering logic; e.g. [`TileHoleRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileHoleRenderer.java) draws end-portal-shader faces adjacent to opaque blocks, while current `TCHoleBlock` returns `RenderShape.INVISIBLE` and has no explicitly registered `HOLE` BER. This is stronger evidence of a **probable in-game missing effect** than merely counting absent BERs, but the client must still be inspected.

Legacy [`TileMirrorRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java) is large and uses animated portal effects; the current status document itself labels final mirror BER/pixel parity as remaining work. [`TileVoidSiphonRenderer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/tile/TileVoidSiphonRenderer.java) uses a star shader only when enabled. Legacy Hungry Chest interpolates a lid angle; Centrifuge rotates a separate component; Focal Manipulator shows animated crystals/energy; Dioptra draws a colored grid.

Corresponding legacy textures such as `textures/models/chesthungry.png`, `centrifuge.png`, `dioptra_side.png`, `valve.png` are also already present in the port, but a present image file is not a working animation.

## Proposed batch implementation order (separate future code PRs)

1. **Confirm scope with one targeted client smoke pass**: spawn the four flagged entities, place/toggle a Hole, Mirror/Essentia Mirror, Void Siphon, Hungry Chest and Centrifuge; capture logs/screenshots. One batch, not a full test after each edit.
2. **Entity visual slice**: implement the four original geometry behaviors with NeoForge render architecture; keep `focus_cloud` and `taint_swarm` classified by particle FX, not automatically rendered mobs.
3. **Portal and high-impact BER slice**: Hole, Mirrors, Void Siphon, with correct visibility, shaders/translucency, distance, and server-authoritative state.
4. **Mechanical/dynamic tile slice**: Hungry Chest lid, Centrifuge spin, Dioptra, Focal Manipulator, Alembic, tube variants, Pattern Crafter; test each under empty/active/full/different orientation states. Recharge Pedestal gets **transform comparison**, not a new registration.
5. **Banners**: reconcile legacy world-block/BE identity and old-save compatibility with the earlier 17-banner census before renderer implementation.

## Verification limits

- All analyses above are comparisons of checked-in original decompiled source and current checked-in Java, plus static asset path checks, at pinned SHA.
- No Gradle, Java compilation, dedicated-server, PowerShell or Minecraft client execution in this checkpoint.
- No scene captures, timing measurements, pixel comparisons, shader cross-platform validation or legacy save upgrades. Existing particles may cover part of the visual responsibility of an old renderer.
- **No gameplay/asset files edited and no PR merged**. Results are evidence and work queue, not an approved implementation.
- Machine-readable evidence: [dynamic rendering manifest](generated/visual_v0_dynamic_renderer_census_2026_11_03.json).
