# Bellows Legacy Device Source Audit

Generated: 2026-06-22 05:03:44 +03:00

## Purpose

This audit captures legacy Bellows source evidence before implementing the real NeoForge Bellows device batch.
The goal is maximum safe legacy parity: do not turn Bellows into a generic speed block, and do not expand behavior without source evidence.

## Source files

- Legacy search root: $legacy
- TileBellows.java: $(D:\Thaumcraft_6_port_to_1.21.1\02_existing_decompiled_repo\Thaumcraft-6-Source-Code-master\src\main\java\thaumcraft\common\tiles\devices\TileBellows.java.FullName)
- BlockBellows.java: $(D:\Thaumcraft_6_port_to_1.21.1\02_existing_decompiled_repo\Thaumcraft-6-Source-Code-master\src\main\java\thaumcraft\common\blocks\devices\BlockBellows.java.FullName)

## Evidence summary

| Scope | Found | Evidence |
|---|---:|---|
| legacy TileBellows source exists | yes | D:\Thaumcraft_6_port_to_1.21.1\02_existing_decompiled_repo\Thaumcraft-6-Source-Code-master\src\main\java\thaumcraft\common\tiles\devices\TileBellows.java |
| legacy BlockBellows source exists | yes | D:\Thaumcraft_6_port_to_1.21.1\02_existing_decompiled_repo\Thaumcraft-6-Source-Code-master\src\main\java\thaumcraft\common\blocks\devices\BlockBellows.java |
| legacy tile ticking/update behavior signal | yes | Search: updateEntity/update/tick/ITickable/serverTick |
| legacy inflation/animation state signal | yes | Search: inflation/inflate/extension/press/rotation/last |
| legacy tube-buffer interaction signal | no | Search: TubeBuffer/tube/suction/buffer |
| legacy vanilla-furnace interaction signal | yes | Search: Furnace/furnace/TileEntityFurnace/AbstractFurnace |
| legacy facing/placement signal | yes | Search: facing/getStateForPlacement/metadata |
| port Bellows blockstate exists | yes | TCBellowsBlock has FACING and ENABLED blockstate |
| port Bellows BlockEntity present | no | Current expected gap before real Bellows device batch |
| port smelter already consumes Bellows count | yes | TCSmelterBlockEntity refreshBellows/smeltTimeForVis bridge |
| port registry has Bellows BlockEntity | no | TCBlockEntities BELLOWS registration state |

## Current port boundary

- TCBellowsBlock currently owns facing and redstone-enabled blockstate only.
- TCSmelterBlockEntity already reads enabled facing Bellows around the smelter and applies the count to smelt duration.
- A dedicated Bellows BlockEntity is still the next focused device boundary unless a later commit has already added it.
- Existing smelter runtime audit must stay at 37/37 after the Bellows batch.

## Next implementation boundary

1. Add TCBellowsBlockEntity only after mapping legacy TileBellows state/tick behavior.
2. Preserve the existing smelter Bellows count behavior while moving dynamic device state into the BlockEntity.
3. Add tube-buffer and vanilla-furnace interactions only if the legacy source evidence rows above are confirmed enough for an audited implementation.
4. Keep client animation/rendering as a separate client-safe slice unless server state is required for parity.

## Validation

Run:

```powershell
.\tools\audits\audit-legacy-bellows-device-source.ps1 -RepoRoot "D:\Thaumcraft_6_port_to_1.21.1"
```

The audit is intentionally source/documentation-focused. It does not implement Bellows behavior.
