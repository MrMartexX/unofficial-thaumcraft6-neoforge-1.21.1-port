# Research requirement blocker audit

Status: current blocker audit for the `research-knowledge-scanning-design` branch after the first working research/scanning slice and the shared requirement resolver refactor.

This document records the current `/tc research requirements` output and explains which unresolved research-stage requirements are real implementation blockers rather than parser bugs.

## Latest command snapshot

Command:

```text
/tc research requirements 50
```

Current summary after legacy requirement classification:

| Requirement class | Result |
|---|---:|
| `required_item` | `29/69` resolved |
| `required_craft` | `10/34` modern-matchable |
| `required_knowledge` | `170/170` resolved |
| Total unresolved item/craft/knowledge requirements | `64` |

Interpretation: the knowledge parser and legacy point conversion are currently clean for every loaded research-stage knowledge requirement. The remaining blockers are item/craft identity, legacy NBT/container semantics, and subsystem implementation issues.

## Resolved safe identity mapping

| Legacy requirement id | Current port id | Reason this mapping is allowed |
|---|---|---|
| `thaumcraft:arcane_stone` | `thaumcraft:stone_arcane` | The port already registers the arcane stone block/item as `stone_arcane`. This is a stable renamed identity mapping, not a dummy placeholder. |

## Highest-priority blockers

| Count | Requirement type | Blocked family | Interpretation |
|---:|---|---|---|
| 23 | `required_item` | legacy aspect crystal essence | Legacy aspect-carrying crystal item. Do not blindly map this to current `crystal_aer`/`crystal_*` block items until the item/component semantics are designed. Compound-aspect requirements also exist. |
| 7 | `required_item` | legacy essentia phial | Legacy essentia phial requirement with aspect NBT. This belongs to the essentia/container item slice, not plain item registration. |
| 4 | `required_item` | legacy enchanted placeholder | Legacy enchanted-placeholder requirement. Needs explicit modern enchantment-component mapping, likely to an enchanted book or matching enchanted tool stack. |
| 2 | `required_craft` | legacy material family: `thaumcraft:ingot` | Legacy metadata-backed material family. Needs explicit material id mapping before stage crafting markers can be trusted. |
| 2 | `required_craft` | legacy material family: `thaumcraft:metal` | Legacy metadata-backed metal family. Needs explicit material id mapping. |
| 2 | `required_item` | legacy material family: `thaumcraft:plate` | Legacy metadata-backed plate family. Needs material/item slice. |

## Single-reference subsystem blockers

These are currently one reference each, but many are major subsystem entry points:

### Research/crafting station

- `thaumcraft:arcane_workbench`
- `thaumcraft:research_table`
- `thaumcraft:scribing_tools`
- `thaumcraft:wand_workbench`

### Auromancy

- `thaumcraft:caster_basic`
- `thaumcraft:focus_1`
- `thaumcraft:focus_2`
- `thaumcraft:focus_3`
- `thaumcraft:vis_resonator`

### Alchemy

- `thaumcraft:crucible`
- `thaumcraft:leather`
- `thaumcraft:nitor`
- `thaumcraft:smelter_basic`
- `thaumcraft:tallow`

### Infusion

- `thaumcraft:infusion_matrix`

### Thaumium tools and material-family item

- `thaumcraft:thaumium_axe`
- `thaumcraft:thaumium_hoe`
- `thaumcraft:thaumium_pick`
- `thaumcraft:thaumium_shovel`
- `thaumcraft:thaumium_sword`
- `thaumcraft:nugget`

### Other families

- `thaumcraft:brain`
- `thaumcraft:curio`
- `thaumcraft:mirrored_glass`

Some of these are simple identity/registry blockers. Others are not safe to resolve as plain items because they represent larger systems: arcane crafting, crucible, focus crafting, infusion, essentia, or research table workflows.

## Current safe conclusions

- `required_knowledge` is not the active bottleneck.
- The shared `TCResearchRequirementResolver` is now the single source for research-stage item and knowledge requirement interpretation.
- Most unresolved stage gates are expected because the corresponding items, blocks, container items, or subsystem flows do not exist in the current port yet.
- `crystal_essence`, `phial`, and enchanted-placeholder requirements must not be solved by registering dummy placeholder items. Doing that would make research stages appear fulfillable while the real item semantics are absent.
- Metadata-backed legacy families such as `ingot`, `metal`, `plate`, and `nugget` need an explicit legacy-to-modern material mapping table before they can safely participate in item consumption or craft marker checks.
- Exact legacy `ItemStack.toString().hashCode()` craft marker parity remains unresolved and still requires a dedicated 1.12 exporter/mapping pass.

## Recommended next implementation order

1. Keep `TCResearchRequirementResolver` as the only place for item/knowledge research requirement interpretation.
2. Add explicit mapping entries only for already-implemented renamed identities. `thaumcraft:arcane_stone -> thaumcraft:stone_arcane` is the first accepted example.
3. Create a dedicated material-family mapping note before resolving `thaumcraft:ingot`, `thaumcraft:metal`, `thaumcraft:plate`, or `thaumcraft:nugget`.
4. Defer `crystal_essence` and `phial` until aspect-container item semantics are designed.
5. Defer `arcane_workbench`, `wand_workbench`, focus items, crucible, infusion matrix, and research table until their subsystem slices exist.
6. Add the 1.12 craft-hash exporter before relying on exact legacy direct craft marker ids.

## Pass criteria for this audit to improve

- The unresolved count may decrease only when the corresponding modern item behavior is real or the mapping is explicitly documented.
- A lower unresolved count is not automatically better if it was achieved by dummy registrations.
- Every new mapping must be safe for both requirement checking and requirement consumption.
- Audit output should keep describing unresolved requirements by semantic family, not just by missing registry id.
