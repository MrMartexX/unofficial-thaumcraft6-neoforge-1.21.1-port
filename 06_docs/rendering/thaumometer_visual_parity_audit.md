# Thaumometer Visual Parity Audit

Scope: authoritative legacy data and modern port mapping for the Thaumometer item model. This follows `NeoForge_legacy_migration_guide.md`: static assets stay in model JSON/texture resources, while dynamic client-only rendering should be added only when static model data cannot reproduce legacy behavior.

## Legacy Sources

| Area | Legacy source | Notes |
| --- | --- | --- |
| Item class | `thaumcraft.common.items.tools.ItemThaumometer` | No custom item renderer. Handles scan action, aura update packet, scan sound, scan highlight. |
| Item registration | `thaumcraft.common.config.ConfigItems#initModelAndVariants` | Registers non-subtype item model as `ModelResourceLocation(item registry name, null)`, so `thaumcraft:thaumometer` resolves through normal model/blockstate data. |
| Model/blockstate | `assets/thaumcraft/blockstates/thaumometer.json` | Forge 1.12 `forge_marker` blockstate points at `thaumcraft:scanner.obj` with `#body/#pane` texture aliases and `flip-v`. |
| Geometry | `assets/thaumcraft/models/item/scanner.obj` | Original jar/Vineflower copy is authoritative for parity; port uses this copy. Bounding box: `x -1.4..1.4`, `y -0.1..0.1`, `z -1.2124..1.2124`, 36 vertices. |
| Material | `assets/thaumcraft/models/item/scanner.mtl` | Port rewrites only texture paths to NeoForge aliases `#body/#pane`; visual material values remain legacy-compatible. |
| Body texture | `assets/thaumcraft/textures/items/scanner.png` | Port copy: `textures/item/thaumometer.png`, same SHA-256 prefix `6267EA1EDE77E04F`. |
| Pane texture | `assets/thaumcraft/textures/items/scanscreen.png` | Port copy: `textures/item/scanscreen.png`, same SHA-256 prefix `68DE90DAFD6A72AE`. |
| Aura HUD | `thaumcraft.client.lib.events.HudHandler#renderThaumometerHud` | Separate overlay at left side of screen, not part of item model transform. |

## Legacy Transform Values

Forge 1.12 values from `blockstates/thaumometer.json`:

| Context | Legacy transform |
| --- | --- |
| First person right | translation `[-0.06, 0.006, -0.4]`, rotation `x=90`, implicit scale `1.0` |
| First person left | translation `[-1.06, 0.006, -0.4]`, rotation `x=90`, implicit scale `1.0` |
| Third person right | translation `[0, 0, -0.1]`, scale `0.2`, rotation `y=90`, then `x=90` |
| Third person left | translation `[0, -0.1, 0]`, scale `0.2`, rotation `y=90`, then `x=90` |
| GUI | translation `[0.175, -0.175, 0]`, scale `0.35`, rotation `x=90` |
| Ground | translation `[0.1, 0.1, 0.1]`, scale `0.2`, rotation `x=90` |
| Fixed | translation `[0.2, 0.2, -0.17]`, scale `0.4`, rotation `x=-90` |

Modern `models/item/thaumometer.json` now maps these directly into item `display` coordinates by applying the standard JSON translation scale of `16` for legacy translations. If this still does not visually match 1.12.2 in first-person/third-person, the remaining gap is not missing legacy data: it is a renderer pipeline difference between Forge 1.12 item transforms and Minecraft/NeoForge 1.21 item-in-hand transforms. At that point the correct fix is a small client-only custom item renderer/BEWLR for the Thaumometer, not more random JSON tuning.

## Legacy Scan Visual Targeting

| Visual path | Legacy target logic | Current port mapping |
|---|---|---|
| Right-click scan target | `drawFX` first checks `EntityUtils.getPointedEntity(world, player, 1.0, 9.0, 0.0f, true)`, then exact item block ray. | `TCThaumometerClientEffects.onUse` uses `TCScanTargeting` with the same entity range/min-range/padding and a normal block ray. |
| Server scan target | `doScan` uses the same entity-first range `9` path, then block, then sky/null. | `TCScanningManager.scanLooking` uses the same shared target resolver, including dropped item entities. |
| Held readiness | `held = isSelected || itemSlot == 0`. The periodic aura/highlight pass does not treat an arbitrary offhand stack as selected. | Server `inventoryTick` and client visual readiness use main-hand selected or inventory slot `0`. Offhand use can still right-click scan through modern hand handling, but it does not keep the legacy held-highlight pass alive by itself. |
| Held entity highlight | Every 5 client ticks, `onUpdate` calls `EntityUtils.getPointedEntity(world, player, 1.0, 16.0, 5.0f, true)`. This is a broad AABB/line-of-sight zone pick. | `TCThaumometerClientEffects.onClientTick` uses range `16`, min range `1`, padding `5`, inflated hitboxes, and line-of-sight checks. |
| Held block highlight | Every 5 client ticks, `onUpdate` also fires `getRayTraceResultFromPlayerWild` with range `16` and random yaw/pitch offsets around the view direction. | `TCScanTargeting.wildRayTrace` uses the same range and random `nextInt(25)-nextInt(25)` yaw/pitch spread, so the sparkles sweep nearby scannable blocks instead of only one crosshair block. |
| Highlight eligibility | Legacy calls `ScanningManager.isThingStillScannable`, so data scan predicates without visible aspects can still highlight. | The port derives potential scan keys from aspect lookup, data scannables, potion/effect scans, and enchantment scans, then suppresses targets whose keys are already present in the synced client knowledge cache after real Thaumometer scan mutation. |
| Mob aspect overlay | Legacy assigns `RenderEventHandler.thaumTarget = target` even when the target is already known; only sparkle highlighting is gated by `isThingStillScannable`. | The port renders aspect icons for normal aspect-bearing living mobs while targeted through the Thaumometer, regardless of already-known scan keys. Sparkle highlighting remains gated by unknown research keys. |
| Scan success activity | Legacy `scanTheThing` only counts a scan as found when the key is blank/suppressed or `progressResearch` actually adds a new key. Already-known scan keys do not trigger success/onSuccess again. | Modern `ScanningManager.scanTheThing` now follows that rule: known targets return the legacy unknown/nothing-new status, blank-key scans suppress status messages, and `onSuccess` only runs for newly progressed or suppressed scans. |

## Active Modern Corrections

After in-game comparison, the active item JSON intentionally deviates from raw legacy mapping in three narrow places:

| Context | Correction | Reason |
| --- | --- | --- |
| GUI/hotbar | Centered translation and slightly smaller scale | Modern GUI origin differs enough that raw legacy `0.175/-0.175` offset places the item off-center and a little too large. |
| GUI lighting | `gui_light: front` | The 3D OBJ was darker than the 1.12 hotbar item under modern GUI lighting. |
| Body/pane render passes | `neoforge:composite` with `body` in `minecraft:solid` and `pane` in `minecraft:translucent` | Rendering the whole OBJ as translucent made solid frame faces compete with the pane during item-pass sorting. Splitting the same OBJ into visible components keeps the frame solid and draws glass after it. |
| Pane back surface | `visibility.screen_back: false` inside the pane child | The OBJ has front and back translucent pane surfaces very close together; 1.21 translucent sorting can expose a missing-triangle artifact in first-person. Keeping the front pane removes the z/sorting conflict while retaining the lens. |
| Third person | Small right/left offset and slight Z rotation | Raw legacy transform floats away from the modern hand anchor. This remains screenshot-driven unless replaced by a dedicated item renderer. |
| Use animation | `ItemThaumometer#use` returns `InteractionResultHolder.consume` | In Minecraft 1.21 `SUCCESS.shouldSwing()` is true and triggers a strong hand swing. Legacy 1.12 scan used the item action without a mining-like arm swing, so the port consumes the action without requesting swing. |

## Client Scan Visuals

| Piece | Legacy source | Port status |
|---|---|---|
| Right-click runes | `ItemThaumometer.drawFX` -> `FXDispatcher.blockRunes` | Implemented first pass. The port spawns legacy atlas rune frames `224-239` with the same purple randomization on entity/block use targets. |
| Held target sparkle | `ItemThaumometer.onUpdate` -> `FXDispatcher.scanHighlight` every 5 ticks | Implemented first pass with legacy targeting. The port highlights broad entity targets and wild-ray block hits with legacy-shaped blue sparkles through the shared legacy FX engine. |
| Mob aspect overlay | `RenderEventHandler.thaumTarget` -> `drawTagsOnContainer` | Implemented first pass. Aspect icons and amounts render above normal living mobs while the thaumometer is ready, even after the mob's scan keys are known. Object entities, projectiles, item frames, and players are excluded from this visual overlay. Aspect PNGs are byte-identical to the original jar. The port renders the overlay after weather/clouds so clouds cannot draw over the icons. Aspect icons use an unlit textured quad with the raw legacy aspect color and alpha `0.75`; `bright=220` is not applied as an RGB/alpha multiplier because that made already-correct icons too transparent, and the modern entity light/fog path made icons too dark. The renderer explicitly resets shader color to white before drawing because modern `ColorModulator` state can leak from earlier world passes. Text amounts are drawn as tiny pixel-geometry digits instead of through the modern font atlas, avoiding angle-dependent glyph texture artifacts while keeping the legacy black shadow plus white foreground layout. |
| Learned/unlearned gating | `ScanningManager.isThingStillScannable` used client-side in 1.12 | Implemented for the current predicate layer. Completed research keys sync to the client and suppress known potential sparkle targets; scan mutation is active for completed research keys, while full research-stage/reward side effects remain deferred. |

## Live Tuning Workflow

`runClient` uses the processed resource output under `05_neoforge_port/build/resources/main`, not the source file directly. When tuning `src/main/resources/assets/thaumcraft/models/item/thaumometer.json` while the game is already open:

1. Edit the source JSON.
2. Run `.\tools\sync_live_resources.ps1` from the repository root.
3. Press `F3+T` in the running client.

If step 2 is skipped, `F3+T` will reload the stale build copy and the in-game item will look unchanged.
