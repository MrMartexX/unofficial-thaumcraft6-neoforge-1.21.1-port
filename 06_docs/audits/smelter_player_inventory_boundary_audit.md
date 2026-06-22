# Smelter Player/Inventory Boundary Audit

Generated: 2026-06-22 04:45:16 +03:00

## Purpose

This audit records the current player, menu, inventory and automation boundary for the TC6 smelter port.

The goal is maximum safe legacy parity: the smelter must behave as a two-slot machine with one input slot, one fuel slot, server-owned state, controlled player access and sided automation rules. This document intentionally does not claim final visual parity or Bellows dynamic BlockEntity parity.

## Legacy authority

Primary legacy behavior reference:

- `TileSmelter`
- legacy smelter two-slot inventory shape
- legacy input/fuel separation
- legacy furnace-like burn/cook progress
- legacy sided inventory behavior
- legacy smelter GUI slot layout and progress gauges

## Current implementation boundary

| Area | Confirmed port boundary |
|---|---|
| Inventory shape | `TCSmelterBlockEntity` owns exactly two slots: `SLOT_INPUT = 0`, `SLOT_FUEL = 1`. |
| Server authority | `TCSmelterBlockEntity` implements `WorldlyContainer` and `MenuProvider`; state lives on the BlockEntity. |
| Player menu | `TCSmelterMenu` opens from the BlockEntity and exposes only the two smelter slots plus player inventory. |
| Slot coordinates | Input slot uses legacy coordinate `80,8`; fuel slot uses legacy coordinate `80,48`. |
| Input rule | Input slot accepts only stacks with non-empty `AspectList`. |
| Fuel rule | Fuel slot accepts only stacks with positive burn time. |
| Data sync | Menu syncs cook time, burn time, current item burn time, stored vis and smelt time through `DataSlot`. |
| Quick move | Shift-click routes burnable stacks toward fuel first, then aspect-bearing stacks toward input. |
| Automation capability | `TCMachineCapabilities` exposes `Capabilities.ItemHandler.BLOCK` for Basic, Thaumium and Void smelters. |
| Sided automation | Bottom exposes fuel/remainder lane, top exposes no insertion slots, horizontal sides expose input. |
| Essentia capability | Smelters are not exposed through `TCEssentiaCapabilities.BLOCK`; they output through Alembic routing, not as transport endpoints. |
| Removal | Smelter drops its inventory through `dropContents` on block removal. |
| Persistence | Inventory and machine state are saved through BlockEntity NBT. |

## Validation

Run:

```powershell
.\tools\audits\audit-smelter-player-inventory-boundary.ps1 -RepoRoot "D:\Thaumcraft_6_port_to_1.21.1"
```

The audit statically checks:

- two-slot legacy inventory ownership;
- input/fuel slot validation;
- menu slot coordinates;
- quick-move behavior;
- five synced progress fields;
- sided automation through NeoForge item handler capability;
- no accidental smelter exposure through the essentia transport capability;
- server-side menu opening;
- inventory drop and NBT persistence anchors.

## Deferred owning slices

- Final measured smelter GUI visual parity.
- Bellows dynamic BlockEntity behavior and animation.
- Broader machine automation integration beyond the current smelter item handler boundary.
- Any future Thaumatorium/importer/exporter behavior that depends on smelter output products.

## Expected result

```text
[tc-port] Smelter player/inventory boundary audit passed.
```
