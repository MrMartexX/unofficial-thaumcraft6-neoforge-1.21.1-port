# Bellows device boundary

Status: active implementation boundary for the first real Bellows device slice.

## Scope

This document defines the Bellows slice after the item/block parity framework reached green report-only CI. The slice converts the existing `thaumcraft:bellows` block from a facing placeholder into a server-owned device boundary without expanding unrelated essentia, caster, label/phial or broad rendering work.

## Current port targets

| Layer | File |
|---|---|
| Block identity/state | `05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java` |
| Device state | `05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCBellowsBlockEntity.java` |
| BlockEntity registry | `05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java` |
| Smelter consumer | `05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java` |
| Tube-buffer consumer | `05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java` |

## Implemented behavior

- Bellows remains a redstone-disabled facing block.
- Bellows now owns a BlockEntity for persistent device/animation state.
- The BlockEntity classifies its attached target as `smelter`, `tube_buffer`, `vanilla_furnace` or empty.
- Smelters continue to count enabled Bellows blocks when calculating smelt time.
- Buffer tubes now refresh enabled attached Bellows pressure and use that pressure in suction calculation.
- Vanilla furnace interaction is intentionally limited to target classification in this slice because a safe modern mutation hook requires a separate supported-API design.

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

## Observed visual/collision repairs

Manual in-game review found two Bellows parity defects that were not caught by the first boundary audit:

- the inventory item used the block model without explicit display transforms, which made it appear as a flat/front-facing slot icon instead of a readable 3D block item;
- the block behaved like a full cube for collision/occlusion even though the model is non-full, causing incorrect collision and face-culling/x-ray style holes against neighbouring blocks.

The current repair adds explicit item display transforms, non-full directional VoxelShapes, an empty occlusion shape, and `noOcclusion()` on the block registration. This is a concrete implementation repair, not a Bellows-only audit expansion. The broader item/block framework should later gain generic visual/collision risk checks for registered non-full models.
