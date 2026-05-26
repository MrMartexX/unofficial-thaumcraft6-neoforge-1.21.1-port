# Legacy material-family research requirement mapping

Status: planning document for legacy metadata-backed material-family requirements used by research-stage `required_item` and `required_craft` gates.

This document records the known legacy ids and metadata values seen in `/tc research requirements 50`, plus the current inferred semantic targets from surrounding research recipe context. These semantic targets are **not yet resolved** until the corresponding modern items are registered and safe for requirement matching/consumption.

## Why this exists

Thaumcraft 6 1.12 used metadata-backed item families such as `thaumcraft:ingot`, `thaumcraft:metal`, `thaumcraft:plate`, and `thaumcraft:nugget`. Minecraft 1.21.1 uses flattened item ids, so the legacy id alone is not enough. The metadata/damage value is part of the semantic identity.

The shared `TCResearchRequirementResolver` classifies these as `legacy material-family requirement` or, when the semantic target is known but the modern item does not exist yet, as `legacy material-family target not implemented`.

## Current audit snapshot

Command:

```text
/tc research requirements 50
```

Current material-family blockers:

| Requirement source | Requirement type | Legacy raw requirement | Observed metadata/damage | Current state |
|---|---|---|---:|---|
| `METALLURGY stage 1` | `required_craft` | `thaumcraft:ingot;1;2` | `2` | target inferred: brass ingot, item not implemented |
| `METALLURGY stage 2` | `required_craft` | `thaumcraft:ingot;1;0` | `0` | target inferred: thaumium ingot, item not implemented |
| `ESSENTIASMELTERTHAUMIUM stage 1` | `required_item` | `thaumcraft:plate;1;2` | `2` | target inferred: thaumium plate, item not implemented |
| `ESSENTIASMELTERTHAUMIUM stage 1` | `required_craft` | `thaumcraft:metal;1;2` | `2` | target inferred: thaumium metal, item not implemented |
| `ESSENTIASMELTERVOID stage 1` | `required_item` | `thaumcraft:plate;1;3` | `3` | target inferred: void plate, item not implemented |
| `ESSENTIASMELTERVOID stage 1` | `required_craft` | `thaumcraft:metal;1;3` | `3` | target inferred: void metal, item not implemented |
| `GRAPPLEGUN stage 1` | `required_item` | `thaumcraft:nugget;1;10` | `10` | still blocked: material-family mapping required |

## Context used for current semantic targets

The current targets are inferred from the legacy research recipe context, not from a full 1.12 item registry dump:

- `METALLURGY stage 1` requires `thaumcraft:ingot;1;2` and unlocks/uses the `thaumcraft:brassingot` recipe, so metadata `2` is currently treated as brass ingot target.
- `METALLURGY stage 2` requires `thaumcraft:ingot;1;0` and unlocks/uses the `thaumcraft:thaumiumingot` recipe, so metadata `0` is currently treated as thaumium ingot target.
- `ESSENTIASMELTERTHAUMIUM` uses `plate;1;2` and `metal;1;2` in a stage named for thaumium smelter progression, so metadata `2` is currently treated as thaumium material target for those families.
- `ESSENTIASMELTERVOID` uses `plate;1;3` and `metal;1;3` in a stage named for void smelter progression and is gated by `BASEELDRITCH`, whose recipes include void ingot/void stuff, so metadata `3` is currently treated as void material target.

These inferences must still be confirmed later against legacy source or a 1.12 runtime export before being treated as final parity data.

## Do not guess unresolved metadata

Do **not** map the following directly until confirmed from legacy source/exported registry data:

| Legacy family | Metadata values currently observed | Why guessing is unsafe |
|---|---|---|
| `thaumcraft:nugget` | `10` | Nugget metadata may not correspond to the same material index as ingot/metal/plate. |

## Required confirmation source

Before final parity implementation, confirm each mapping from at least one of these:

1. Legacy Thaumcraft 6 source registration / item enum definitions.
2. 1.12 runtime registry dump with item id + metadata + display/unlocalized name.
3. Existing validated exporter output dedicated to material-family item stacks.

## Mapping table

| Legacy raw id | Metadata/damage | Current semantic target | Planned modern item id | Safe for `required_item` consumption? | Safe for `required_craft` marker matching? | Current confirmation source |
|---|---:|---|---|---|---|---|
| `thaumcraft:ingot` | `0` | thaumium ingot | `thaumcraft:thaumium_ingot` | no: item not registered yet | no: item not registered yet | inferred from `METALLURGY` recipe context |
| `thaumcraft:ingot` | `2` | brass ingot | `thaumcraft:brass_ingot` | no: item not registered yet | no: item not registered yet | inferred from `METALLURGY` recipe context |
| `thaumcraft:metal` | `2` | thaumium metal | `thaumcraft:thaumium_metal` | no: item not registered yet | no: item not registered yet | inferred from `ESSENTIASMELTERTHAUMIUM` context |
| `thaumcraft:metal` | `3` | void metal | `thaumcraft:void_metal` | no: item not registered yet | no: item not registered yet | inferred from `ESSENTIASMELTERVOID` + `BASEELDRITCH` context |
| `thaumcraft:plate` | `2` | thaumium plate | `thaumcraft:thaumium_plate` | no: item not registered yet | no: item not registered yet | inferred from `ESSENTIASMELTERTHAUMIUM` context |
| `thaumcraft:plate` | `3` | void plate | `thaumcraft:void_plate` | no: item not registered yet | no: item not registered yet | inferred from `ESSENTIASMELTERVOID` + `BASEELDRITCH` context |
| `thaumcraft:nugget` | `10` | TODO | TODO | no | no | TODO |

## Implementation rule

Only after the modern target item exists should `TCLegacyMaterialFamilyMappings` return a resolved mapping for any of these entries.

Until then, these blockers must remain unresolved, but the audit output may report the planned target as `legacy material-family target not implemented`.

## Pass criteria

A material-family requirement can be marked resolved only when all of the following are true:

- the modern item is registered;
- the legacy metadata value maps to exactly one modern semantic material;
- `required_item` consumption cannot consume the wrong material variant;
- `required_craft` marker matching is documented as either modern-matchable or still requiring exact 1.12 craft-hash parity;
- the mapping is documented in this file.
