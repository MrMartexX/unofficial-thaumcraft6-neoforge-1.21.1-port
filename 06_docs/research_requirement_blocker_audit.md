# Research requirement blocker audit

Status: current blocker audit for the `research-knowledge-scanning-design` branch after the first working research/scanning slice.

This document records the current `/tc research requirements` output and explains which unresolved research-stage requirements are real implementation blockers rather than parser bugs.

## Latest command snapshot

Command:

```text
/tc research requirements 50
```

Current summary:

| Requirement class | Result |
|---|---:|
| `required_item` | `29/69` resolved |
| `required_craft` | `9/34` modern-matchable |
| `required_knowledge` | `170/170` resolved |
| Total unresolved item/craft/knowledge requirements | `65` |

Interpretation: the knowledge parser and legacy point conversion are currently clean for every loaded research-stage knowledge requirement. The remaining blockers are item/craft identity and container/item-family implementation issues.

## Highest-priority blockers

| Count | Requirement type | Missing / blocked key | Interpretation |
|---:|---|---|---|
| 23 | `required_item` | `thaumcraft:crystal_essence` | Legacy aspect-carrying crystal item. Do not blindly map this to current `crystal_aer`/`crystal_*` block items until the item/component semantics are designed. Compound-aspect requirements also exist. |
| 7 | `required_item` | `thaumcraft:phial` | Legacy essentia phial requirement with aspect NBT. This belongs to the essentia/container item slice, not plain item registration. |
| 4 | `required_item` | legacy enchanted placeholder | Legacy enchanted-placeholder requirement. Needs explicit modern enchantment-component mapping, likely to an enchanted book or matching enchanted tool stack. |
| 2 | `required_craft` | `thaumcraft:ingot` | Legacy metadata-backed material family. Needs explicit material id mapping before stage crafting markers can be trusted. |
| 2 | `required_craft` | `thaumcraft:metal` | Legacy metadata-backed metal family. Needs explicit material id mapping. |
| 2 | `required_item` | `thaumcraft:plate` | Legacy metadata-backed plate family. Needs material/item slice. |

## Single-reference blockers

These are currently one reference each, but many are major subsystem entry points:

- `thaumcraft:arcane_stone`
- `thaumcraft:arcane_workbench`
- `thaumcraft:caster_basic`
- `thaumcraft:crucible`
- `thaumcraft:focus_1`
- `thaumcraft:focus_2`
- `thaumcraft:focus_3`
- `thaumcraft:infusion_matrix`
- `thaumcraft:leather`
- `thaumcraft:nitor`
- `thaumcraft:research_table`
- `thaumcraft:scribing_tools`
- `thaumcraft:smelter_basic`
- `thaumcraft:tallow`
- `thaumcraft:thaumium_axe`
- `thaumcraft:thaumium_hoe`
- `thaumcraft:thaumium_pick`
- `thaumcraft:thaumium_shovel`
- `thaumcraft:thaumium_sword`
- `thaumcraft:vis_resonator`
- `thaumcraft:wand_workbench`
- `thaumcraft:brain`
- `thaumcraft:curio`
- `thaumcraft:mirrored_glass`

Some of these are simple identity/registry blockers. Others are not safe to resolve as plain items because they represent larger systems: arcane crafting, crucible, focus crafting, infusion, essentia, or research table workflows.

## Current safe conclusions

- `required_knowledge` is not the active bottleneck.
- Most unresolved stage gates are expected because the corresponding items, blocks, or container items do not exist in the current port yet.
- `crystal_essence`, `phial`, and enchanted-placeholder requirements must not be solved by registering dummy placeholder items. Doing that would make research stages appear fulfillable while the real item semantics are absent.
- Metadata-backed legacy families such as `ingot`, `metal`, `plate`, and `nugget` need an explicit legacy-to-modern material mapping table before they can safely participate in item consumption or craft marker checks.
- Exact legacy `ItemStack.toString().hashCode()` craft marker parity remains unresolved and still requires a dedicated 1.12 exporter/mapping pass.

## Recommended next implementation order

1. Add a shared legacy research requirement resolver/mapping boundary so `TCResearchManager` and `/tc research requirements` do not duplicate mapping logic.
2. Add explicit mapping entries only for already-implemented renamed identities, for example legacy `thaumcraft:arcane_stone` to the current registered `thaumcraft:stone_arcane` if that mapping is confirmed against legacy data.
3. Create a dedicated material-family mapping note before resolving `thaumcraft:ingot`, `thaumcraft:metal`, `thaumcraft:plate`, or `thaumcraft:nugget`.
4. Defer `crystal_essence` and `phial` until aspect-container item semantics are designed.
5. Defer `arcane_workbench`, `wand_workbench`, focus items, crucible, infusion matrix, and research table until their subsystem slices exist.
6. Add the 1.12 craft-hash exporter before relying on exact legacy direct craft marker ids.

## Pass criteria for this audit to improve

- The unresolved count may decrease only when the corresponding modern item behavior is real or the mapping is explicitly documented.
- A lower unresolved count is not automatically better if it was achieved by dummy registrations.
- Every new mapping must be safe for both requirement checking and requirement consumption.
