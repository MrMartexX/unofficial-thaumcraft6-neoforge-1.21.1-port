# Focus / Caster / Focal Manipulator Core Design

Last updated: 2026-07-02

## Legacy References

| Area | Legacy classes | Notes |
|---|---|---|
| Caster item | `thaumcraft.common.items.casters.ItemCaster`, `CasterManager` | TC6 registers `caster_basic` with area `0`. Vis consumption uses player discount gear, clamps the caster modifier to a minimum of `10%`, drains current chunk for area `0`, cross-shaped neighboring chunks for area `1`, and a `3 x 3` chunk area for area `2`. |
| Focus item | `thaumcraft.common.items.casters.ItemFocus` | TC6 registers `focus_1`, `focus_2`, `focus_3` with max complexities `15`, `25`, `50`. Focus vis cost is `complexity / 5.0F`; activation time is `max(5, complexity / 5 * (complexity / 4))`. |
| Focus package | `thaumcraft.api.casters.FocusPackage`, `FocusNode`, `FocusModSplit*`, `FocusEffect*`, `FocusMedium*` | Node complexity, color contribution, crystal aspect costs and sorting hash are behavior references. The port stores the modern package as a Data Component instead of copying the old NBT/classes directly. |
| Focal Manipulator | `thaumcraft.common.tiles.crafting.TileFocalManipulator`, `GuiFocalManipulator`, `ContainerFocalManipulator` | TC6 uses the public block id `wand_workbench`. It consumes XP and crystal essence up front, then drains aura in 20-vis chunks over time before writing the focus package/name. |
| Focus pouch | `ItemFocusPouch` | The real legacy item id is `focus_pouch`; legacy `FocusPouch` recipe naming is not the item registry id. |

## Implemented Core Boundary

| Area | Modern implementation | Status |
|---|---|---|
| Public caster contract | `thaumcraft.api.casters.ICaster` | Added as a small modern contract for current caster items without importing the old API package wholesale. |
| Focus package component | `TCFocusPackageComponent`, `TCFocusPackageHelper`, `TCFocusElementDefinition`, `TCFocusElements` | Stores encoded nodes, total complexity, color and sorting hash. Includes legacy node identities, max/default setting behavior, duplicate-complexity multiplier, crystal cost extraction and roundtrip audit coverage. |
| Caster focus component | `TCCasterFocusComponent` | Stores the selected focus stack identity, name and package on the caster item stack. |
| Items | `ItemCaster`, `ItemFocus`, `ItemFocusPouch` | `caster_basic`, `focus_1`, `focus_2`, `focus_3` and `focus_pouch` are real items. Caster use consumes aura, applies cooldown and can execute the first audited focus effect slice. |
| Aura/discount cost path | `CasterManager` | Uses raw `IVisDiscountGear` percent for caster cost, clamps to legacy `10%` minimum, and keeps the existing capped public Arcane Workbench discount path separate. |
| Cast effect execution | `TCFocusCastExecutor` | Implements the first server-owned legacy execution slice: `ROOT -> TOUCH -> FIRE`. The touch source vector follows legacy `ItemCaster.generateSourceVector`; the touch medium resolves entities before blocks; unsupported packages fail before vis/aura drain; fire uses legacy damage/fire-duration formulas. |
| Focal Manipulator BE/menu/screen | `TCFocalManipulatorBlockEntity`, `TCFocalManipulatorMenu`, `TCFocalManipulatorScreen` | `wand_workbench` now has a BlockEntity, one focus slot, start cycle, saved state, minimal usable medium/effect design controls, and legacy GUI asset background. |
| Server-authoritative design intent | `TCFocalManipulatorDesignPayload`, `TCFocalManipulatorNetwork` | Client sends encoded design/name intent for the currently open manipulator. Server validates menu position, known node definitions, research at start time, focus max complexity, XP, crystals and aura. |
| Validation | `TCFocusCasterCoreAudit`, `TCFocusCastExecutionAudit` | Core runtime audit passes `10/10`. Cast execution runtime audit passes `5/5` and writes `06_docs/audits/generated/thaumcraft_1_21_focus_cast_execution_audit.md`. |

## Explicitly Deferred

- Remaining focus effect execution: bolt/projectile/cloud/mine/plan/spellbat behavior, block breaking, exchange, rift, curse, heal, scatter and split execution.
- Projectile/entity classes and render/FX for cast effects.
- Final Focal Manipulator editor visual parity and all setting widgets.
- Focus Pouch inventory/menu behavior and optional accessory/Curios integration.
- Rechargeable caster tiers beyond currently registered `caster_basic`.
- Thaumonomicon focus recipe/detail page polish beyond existing recipe/catalog snapshots.

## Validation Commands

```powershell
cd D:\Thaumcraft_6_port_to_1.21.1\05_neoforge_port
.\gradlew.bat build --no-daemon
.\gradlew.bat runServer --no-daemon -PtcFocusCasterCoreAudit=true "-PtcFocusCasterCoreAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\focus_caster\thaumcraft_1_21_focus_caster_core_audit.md"
.\gradlew.bat runServer --no-daemon -PtcFocusCastExecutionAudit=true "-PtcFocusCastExecutionAuditPath=D:\Thaumcraft_6_port_to_1.21.1\06_docs\audits\generated\thaumcraft_1_21_focus_cast_execution_audit.md"
```

Current result: focus/caster core runtime audit passed `10/10`; first cast execution runtime audit passed `5/5`.

## Next Slice

The next row-7 slice should expand cast execution from the audited `TOUCH/FIRE` path into one additional medium/effect family with its own runtime audit. Projectile, cloud and mine behavior should still be introduced as separate target-source batches so cooldown, aura cost, target resolution, entity/projectile lifetime and failure behavior stay auditable.
