# Bellows device boundary

Status: implemented focused device slice; keep covered by the dedicated Bellows audit.

## Scope

This document defines the Bellows slice after the item/block parity framework reached green report-only CI. The slice converts the existing `thaumcraft:bellows` block from a facing placeholder into a server-owned device plus client-rendered animated tile boundary without expanding unrelated caster, label/phial or broad rendering work.

## Current port targets

| Layer | File |
|---|---|
| Block identity/state | `05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java` |
| Device state | `05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCBellowsBlockEntity.java` |
| Vanilla furnace bridge | `05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCVanillaFurnaceBellowsAccessor.java` |
| BlockEntity registry | `05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java` |
| Client renderer | `05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBellowsRenderer.java` |
| Client renderer registration | `05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java` |
| Smelter consumer | `05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java` |
| Tube-buffer consumer | `05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java` |

## Implemented behavior

- Bellows remains a redstone-disabled facing block.
- Bellows now owns a BlockEntity for persistent device counters plus client animation state.
- The BlockEntity classifies its attached target as `smelter`, `tube_buffer`, `vanilla_furnace` or empty.
- Smelters continue to count enabled Bellows blocks when calculating smelt time.
- Buffer tubes now refresh enabled attached Bellows pressure and use that pressure in suction calculation.
- Vanilla furnace interaction follows legacy `TileBellows`: every two server ticks, an enabled Bellows facing a burning vanilla furnace advances current cook progress by one while the item is already cooking.
- Vanilla furnace cook progress is package-private in 1.21.1, so the reflective bridge is isolated in `TCVanillaFurnaceBellowsAccessor` and covered by `audit-bellows-device.ps1`.
- Client animation follows legacy `TileBellows`: the bag contracts by `0.075`, expands by `0.025`, starts from a randomized `0.35..0.90` inflation value on first client tick, and plays the quiet ghast-shoot puff when it reaches full inflation.
- The world block render path is `RenderShape.ENTITYBLOCK_ANIMATED`; the item/block JSON assets remain available for inventory/resource identity, while the placed block uses `TCBellowsRenderer`.
- The client renderer uses legacy `textures/blocks/bellows.png` and renders the tube-buffer extension with `textures/models/bore.png` when the attached target is a buffer tube.

## Out of scope

- Alembic label/phial transfer.
- Caster tube sub-part interaction.
- Tube vent/valve rendering polish.
- Broad rendering/BEWLR/shader work.
- Thaumatorium, mirrors, flux rifts or taint spread.
- Any client-authoritative machine mutation.

## Validation

Run the dedicated Bellows audit:

```powershell
pwsh -NoProfile -File .\tools\audits\runtime\audit-bellows-device.ps1 `
  -RepoRoot "D:\Thaumcraft_6_port_to_1.21.1"
```

For code changes, also run build/server smoke and the item/block framework verifier.

Current dedicated report target: `tools/reports/local/runtime/bellows_device_report.md`. A complete Bellows device batch should have `0` Bellows audit errors and no design-doc review row.

## Observed visual/collision repairs

Manual in-game review found two Bellows parity defects that were not caught by the first boundary audit:

- the inventory item used the block model without explicit display transforms, which made it appear as a flat/front-facing slot icon instead of a readable 3D block item;
- the block behaved like a full cube for collision/occlusion even though the model is non-full, causing incorrect collision and face-culling/x-ray style holes against neighbouring blocks.

The current repair adds explicit item display transforms, non-full directional VoxelShapes, an empty occlusion shape, and `noOcclusion()` on the block registration. This is a concrete implementation repair, not a Bellows-only audit expansion. The broader item/block framework should later gain generic visual/collision risk checks for registered non-full models.
