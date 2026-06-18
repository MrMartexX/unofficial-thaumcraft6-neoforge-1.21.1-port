# Rendering and Model Pipeline Audit

Scope: why legacy Thaumcraft 6 item/block/entity models do not directly survive the Forge 1.12.2 -> NeoForge 1.21.1 port, and how to triage the current resource problems without overwriting adapted 1.21 assets.

This document follows `NeoForge_legacy_migration_guide.md`: keep static JSON assets, dynamic renderers and client-only registration separated; do not port old `TileEntitySpecialRenderer`, `IIcon/registerIcons`, `forge_marker` blockstates, or raw GL state directly.

## Official NeoForge 1.21.1 Rules That Matter

| Area | 1.12 legacy pattern | 1.21.1 target | Impact on Thaumcraft assets |
|---|---|---|---|
| OBJ model loader id | Forge loader and `forge_marker` blockstates | JSON model root uses `"loader": "neoforge:obj"` | Old blockstate files that point to `.obj` through Forge syntax are reference-only until rewritten. |
| OBJ file location | Often referenced as `thaumcraft:scanner.obj` from old blockstates | NeoForge OBJ docs require the `.obj` under `models` or a subfolder; JSON `model` path is namespace-root relative | Use paths like `thaumcraft:models/item/scanner.obj`, not old shorthand. |
| MTL texture references | Legacy `.mtl` often uses `thaumcraft:items\scanner.png` or direct old folder paths | NeoForge OBJ docs support JSON `textures` aliases referenced from `.mtl` as `#texture0`, `#particle`, etc. | Prefer alias mapping such as `#body` / `#pane`; do not rely on old `textures/items` paths. |
| Render type / transparency | Many old assets assume fixed GL state or TESR state | Static models need correct baked model/render type; dynamic transparency belongs in BER/BEWLR or render layers | Thaumometer pane needs translucent model/render handling; jars/mirrors/essentia visuals likely need dynamic renderers. |
| Dynamic block renderers | `TileEntitySpecialRenderer` and raw GL calls | `BlockEntityRenderer` registered on client mod event bus | Crucible, jars, infusion matrix, thaumatorium, alembic, mirrors and similar blocks cannot be solved by JSON alone. |
| Dynamic item renderers | `ItemStackTileEntityRenderer`/custom legacy item render code | `BlockEntityWithoutLevelRenderer` exposed through `IClientItemExtensions` and `RegisterClientExtensionsEvent` | Thaumonomicon, goggles overlays, animated items or item-with-internal-state need client extension design. |
| Client-only code | Common proxies and static `Minecraft.getMinecraft()` references | Client-only classes/events, no client objects in common singletons | Rendering setup must stay outside common initialization except registry-safe references. |

NeoForge docs used:

- Custom model loaders / OBJ loader: `https://docs.neoforged.net/docs/1.21.1/resources/client/models/modelloaders/`
- BlockEntityRenderer and BlockEntityWithoutLevelRenderer: `https://docs.neoforged.net/docs/1.21.1/blockentities/ber/`

## Current Project Findings

| Finding | Current state | Risk | Action |
|---|---|---|---|
| Thaumometer item model | Active `models/item/thaumometer.json` uses `neoforge:obj` and `models/item/scanner.obj` | Was too close to a raw imported OBJ/MTL setup; pane transparency/texture aliases and 1.12 display transforms were fragile in modern item contexts | Updated model JSON to explicit `textures` aliases, `minecraft:translucent`, `flip_v`, front GUI lighting, hidden duplicate back pane, and legacy-derived transforms with targeted GUI/third-person corrections; updated MTL to use `#body` and `#pane`. See `thaumometer_visual_parity_audit.md`. |
| Legacy thaumometer blockstate | `assets/thaumcraft/blockstates/thaumometer.json` still contains `forge_marker`, old shorthand model path and `textures/items` aliases | If/when a thaumometer block is registered, this file will fail modern expectations | Leave as reference until the block exists; do not use it for the active item. |
| Legacy OBJ blockstates | Many imported blockstates contain `forge_marker`, `inventory`, `custom`, old `forge:default-block`, old texture folders | These are not valid modern blockstate/model definitions | Do not mass-fix until the corresponding block is registered; generate modern blockstates/models per active block. |
| Imported `textures/items` and `textures/blocks` | Preserved from 1.12 import | Modern registered content should use `textures/item` and `textures/block` | Keep legacy folders as reference/base. Copy or alias only deliberate active resources into 1.21 paths. |
| OBJ block assets | `models/block/*.obj` and `models/obj/*.obj` exist | OBJ geometry may load, but transform, culling, material and transparency assumptions changed | Each active OBJ block needs a dedicated modern model JSON and likely BER if dynamic. |
| Runtime log | Current run has no Thaumcraft missing texture/model/OBJ errors in filtered log | Visual problems can still exist without loader errors: wrong transform, wrong render type, wrong culling, wrong legacy state mapping | Use in-game visual checks and targeted screenshots after each active model rewrite. Thaumometer transform tuning is expected to be screenshot-driven because 1.12 Forge OBJ item transforms do not map one-to-one to 1.21 display contexts. |

Validation note, 2026-05-14: `.\gradlew.bat runClient --no-daemon --no-configuration-cache` reached atlas creation after the Thaumometer transform adjustment. No Thaumcraft `missing model`, `missing texture`, `OBJ`, `MTL`, `scanner`, or `thaumometer` resource-load errors were present in the run output. Remaining warnings were vanilla/NeoForge environment warnings (`goat_horn` sound and `rendertype_entity_translucent_emissive` sampler).

## Thaumometer-Specific Status

| Piece | Legacy behavior | Current port |
|---|---|---|
| Static item shape | 3D `scanner.obj` with body and glass pane | Present as active OBJ item model. |
| Body texture | `textures/items/scanner.png` | Modern copy is `textures/item/thaumometer.png`; legacy source copy remains in `textures/items/scanner.png`. |
| Pane texture | `textures/items/scanscreen.png`, animated metadata | Modern copy is `textures/item/scanscreen.png`; MTL now references it through `#pane`. |
| Right-click sound | `SoundsTC.scan` client sound | `thaumcraft:scan` sound event is registered and played from server use. |
| Right-click scan FX | Legacy `drawFX` runes on entity/block | Started through `TCFXDispatcher.blockRunes` and `TCThaumometerClientEffects.onUse`; uses legacy particle atlas frames `224-239` and legacy purple randomization. |
| Hold highlight | Every 5 ticks client-side highlight for still-scannable target | Started through `TCThaumometerClientEffects.onClientTick`; uses the legacy entity highlight range `16` with `padding=5` and separate wild block rays at range `16`, so the visual search area is wider than the right-click scan. Highlights potential scannables from aspect lookup, data scannables, potion/effect scans and enchantment scans, then filters already-known keys through the synced client knowledge cache. Final exactness still depends on enabling real Thaumometer scan mutation. |
| Living-mob aspect overlay | Legacy `RenderEventHandler.thaumTarget` draws aspect icons plus amounts above target | Started through `TCThaumometerClientEffects.renderAspectOverlay`; limited to normal `LivingEntity` targets and excludes players/object entities. |
| Aura packet while held | Every 20 ticks server-side aura update, plus `FLUX` warning | Blocked until aura packet/UI integration is stable. |
| Permanent scan reward | Calls `ScanningManager.scanTheThing` | Still dry-run by design until research rewards/categories/sync match legacy. |

## Next Rendering Work Order

1. Keep a visual whitelist of active registered content only: currently the active Thaumcraft blocks/items in `TCBlocks`/`TCItems`.
2. For each active simple block/item, ensure modern `blockstates`, `models/block`, `models/item`, `textures/block`, `textures/item`.
3. For active OBJ/static models, convert through explicit model JSON with `neoforge:obj`, `textures` aliases, chosen `render_type`, and display transforms.
4. For dynamic blocks/items, do not fake with JSON if the legacy asset depended on runtime state. Design BER/BEWLR first.
5. After each batch, run `runClient` and filter `latest.log` for `thaumcraft`, `missing`, `model`, `texture`, `OBJ`, `MTL`, `shader`; then do visual inspection in-game.
