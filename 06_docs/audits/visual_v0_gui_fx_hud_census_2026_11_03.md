# V0 GUI / HUD / client FX source checkpoint (2026-11-03)

> **Source-level inventory only**, pinned to `main` `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`. File presence, event registration and method names do not establish visual or gameplay parity.

## Scope and source provenance

Read legacy [ProxyGUI.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/proxies/ProxyGUI.java), [HudHandler.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/lib/events/HudHandler.java), [RenderEventHandler.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/lib/events/RenderEventHandler.java), [WandRenderingHandler.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/lib/events/WandRenderingHandler.java) and [FXDispatcher.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/fx/FXDispatcher.java) against current [TCMenus.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCMenus.java), [TCMenuScreens.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCMenuScreens.java), [TCInstrumentHud.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCInstrumentHud.java), [TCKnowledgeGainHud.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java), [TCFXDispatcher.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/lib/fx/TCFXDispatcher.java) and client FX implementations. Enumerated all texture files by exact relative path in both complete Git trees.

## Resource conservation

- **855/855 legacy texture files** from the checked-in original `assets/thaumcraft/textures/` subtree are present in the current port under identical paths (767 PNG, 81 `.mcmeta`, 7 JPG).
- The current port has **1,390 texture files**, 535 more paths than the original snapshot (includes duplicated modern `block/` and `item/` texture organization).
- All **34/34 original `textures/gui/*.png`** files are present; current GUI directory contains seven additional research-background PNGs.
- **Limit:** no image decoding, animation playback, GUI UV inspection, alpha/shader check or screenshot comparison was performed. A preserved texture is not proof of a working GUI or effect.

## GUI routing: 18 original client GUI IDs

Legacy `ProxyGUI.getClientGuiElement` handles 18 numbered client GUI cases. Modern `TCMenus` registers **13 menus**, and `TCMenuScreens` registers all **13 corresponding screens**. Thaumonomicon research browser is a modern standalone screen, not a container menu; its entry/page route also exists separately.

**Four legacy GUI families lack a corresponding central modern menu/screen registration:**

| Legacy GUI ID | Legacy GUI | Port evidence and actionable review |
| ---: | --- | --- |
| 6 | `GuiSpa` | Modern `TCArcaneSpaBlock` is implemented at block level but its class documentation explicitly defers the full GUI; no `SpaMenu`/`SpaScreen` in this registry |
| 21 | `GuiPotionSprayer` | `TCPotionSprayerBlock` provides in-world item interactions, but not the legacy container UI |
| 20 | `GuiLogistics` | No corresponding `TCLogisticsMenu` or `TCLogisticsScreen` in the current port source tree/central registration |
| 18 | dynamic Seal GUI | Legacy calls a seal-specific `returnGui` / `returnContainer`; modern `ItemGolemBell` invokes `SealHandler.openSealGui`, which currently displays **seal key and priority as a chat/actionbar message**, not a configuration screen |

Relevant code: [TCArcaneSpaBlock.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCArcaneSpaBlock.java), [TCPotionSprayerBlock.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCPotionSprayerBlock.java), [SealHandler.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/golems/seals/SealHandler.java).

**Scope caution:** absence of a menu is not evidence that the entire machine's server behavior is missing. Port mechanisms can be controlled by in-world interactions; GUI behavior and functional configuration still require independent acceptance tests.

## HUD and world overlays

Legacy `HudHandler` contains separate paths for:

- Thaumometer aura/vis meter, Sanity Checker meter and Knowledge Gain indicators.
- Casting-wand HUD and wand-trade HUD.

Current implementation evidence:

| Legacy visual | Checked port route | First-pass assessment |
| --- | --- | --- |
| Thaumometer / aura meter | `TCInstrumentHud.renderAura` | Implemented HUD entrypoint; layout and formulas unverified |
| Sanity checker | `TCInstrumentHud.renderSanity` | Implemented HUD entrypoint; layout and behavior unverified |
| Knowledge gained | `TCKnowledgeGainHud` | Implemented GUI overlay; animations unverified |
| Thaumometer aspect highlights | `TCThaumometerClientEffects.renderAspectOverlay` | Implemented world overlay path; effects unverified |
| Casting-wand HUD | No direct analogue in inspected registered HUD classes | Source-level review target; repo-wide runtime behavior not certified |
| Wand-trade HUD | No direct analogue in inspected registered HUD classes | Source-level review target |
| Focus radial selector | Original `WandRenderingHandler.handleFociRadial`; no direct analogue in inspected central port overlay paths | Source-level review target |
| Seal area/selection overlays | Original `RenderEventHandler.drawSeals/drawSealArea`; none in inspected `TCClientEvents` | Review other event hooks before declaring missing |

Original `RenderEventHandler` also has shader-group, portal-vignette, world-highlight and fog hooks. Current `TCClientEvents` handles particle-engine and infusion/Wisp effects as well as thaumometer highlights. These are **not direct one-to-one event API mappings**; full world-overlay semantic parity remains open.

## FX architecture: do not equate Java class counts to lost effects

The original subtree `client/fx/` contains **31 Java classes** (beam classes, custom particle classes, `ParticleEngine` and `FXDispatcher`). The current `client/fx/` subtree contains six Java classes, but this is **not** a fair implementation coverage ratio. Current FX is split across `common/lib/fx/TCFXDispatcher`, `TCLegacyParticleEngine`, `TCInfusionClientEffects`, `TCNitorClientEffects`, `TCWispClientEffects`, `TCThaumometerClientEffects` and event/network systems.

As a **name-only API census**, original `FXDispatcher` declares 70 distinct method names, current `TCFXDispatcher` 25, with 15 exact names shared. The other 55 legacy method names are **not** established missing gameplay features: implementations can be in different classes and use different names.

Relevant review groups for the next parity passes:

1. **Beam and arc FX:** legacy `beamCont`, `beamBore`, `arcBolt`, `arcLightning`, `FXBeamWand/Bore` and `FXArc` vs current model/renderer and client FX entrypoints.
2. **Infusion sources/debris and crafting halos:** new `TCInfusionClientEffects` has actual stream/source/halo/debris rendering; compare emission cadence, colors, blend modes and orientation to original.
3. **Essentia / Wisp / Nitor and taint:** current dedicated implementation paths exist; compare individual emitters, server payload propagation and animations.
4. **GUI particles and tooltips:** `TCLegacyFXGenericGui` and `TCKnowledgeGainHud` exist; check old layering and animation timing.
5. **Original world effects:** `RenderEventHandler` block tags, seals, shader overlays, portal vignette, fog and warp scene effects require feature-based client checks.

## Batch acceptance gates (future code work)

1. Confirm the **four missing GUI routing families** against real user interactions, including seal configuration and current server-side functionality. Avoid constructing empty menus merely for registry count parity.
2. Capture paired original/port GUI screenshots at the same GUI scale for 13 registered menus, research browser and entry pages; check text alignment, slot coordinates, textures, tooltip overlays and keyboard/mouse interactions.
3. Capture HUDs for both hands and equipment (Thaumometer/Goggles, Sanity Checker, caster focus/radial choice, knowledge gain) under different GUI scales and states.
4. In **one client FX test batch**, exercise beam/arc, infusion, essentia, Nitor, Wisp, taint, warp and seal-area effects; inspect render order, particle lifetime, network state and shader warnings.
5. Only after evidence is collected split implementation into small coherent PRs and run consolidated tests.

**No Gradle, Minecraft, PowerShell or legacy-runtime tests were executed** for this checkpoint, and no gameplay files were changed. Data manifest: [GUI/FX/HUD source census](generated/visual_v0_gui_fx_hud_census_2026_11_03.json).
