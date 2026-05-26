# Legacy material-family research requirement mapping

Status: planning document for legacy metadata-backed material-family requirements used by research-stage `required_item` and `required_craft` gates.

This document records the known legacy ids and metadata values seen in the research requirement audit, plus the current inferred semantic targets from surrounding research recipe context. These semantic targets are now registry-resolved as bridge identities, but the report still flags them until their final material items, recipes and subsystem semantics are complete.

## Why this exists

Thaumcraft 6 1.12 used metadata-backed item families such as `thaumcraft:ingot`, `thaumcraft:metal`, `thaumcraft:plate`, and `thaumcraft:nugget`. Minecraft 1.21.1 uses flattened item ids, so the legacy id alone is not enough. The metadata/damage value is part of the semantic identity.

The shared `TCResearchRequirementResolver` maps the currently known family metadata values to flattened ids. The non-interactive audit still reports them as `legacy metadata material-family bridge` because registry identity resolution is not the same as final material/recipe parity.

## Current audit snapshot

Command:

```powershell
.\gradlew.bat runServer --no-daemon -PtcResearchRequirementAudit=true "-PtcResearchRequirementAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_requirement_audit\thaumcraft_1_21_research_requirements.md" -PtcResearchRequirementAuditDetailLimit=200
```

Current material-family bridge warnings:

| Requirement source | Requirement type | Legacy raw requirement | Observed metadata/damage | Current state |
|---|---|---|---:|---|
| `METALLURGY stage 1` | `required_craft` | `thaumcraft:ingot;1;2` | `2` | maps to `thaumcraft:brass_ingot`; bridge until final material recipe/source parity |
| `METALLURGY stage 2` | `required_craft` | `thaumcraft:ingot;1;0` | `0` | maps to `thaumcraft:thaumium_ingot`; bridge until final material recipe/source parity |
| `ESSENTIASMELTERTHAUMIUM stage 1` | `required_item` | `thaumcraft:plate;1;2` | `2` | maps to `thaumcraft:thaumium_plate`; bridge until final material recipe/source parity |
| `ESSENTIASMELTERTHAUMIUM stage 1` | `required_craft` | `thaumcraft:metal;1;2` | `2` | maps to `thaumcraft:metal_thaumium`; bridge until block/material parity |
| `ESSENTIASMELTERVOID stage 1` | `required_item` | `thaumcraft:plate;1;3` | `3` | maps to `thaumcraft:void_plate`; bridge until final material recipe/source parity |
| `ESSENTIASMELTERVOID stage 1` | `required_craft` | `thaumcraft:metal;1;3` | `3` | maps to `thaumcraft:metal_void`; bridge until block/material parity |
| `GRAPPLEGUN stage 1` | `required_item` | `thaumcraft:nugget;1;10` | `10` | maps to `thaumcraft:rare_earth`; bridge until source/drop/recipe parity |

## Context used for current semantic targets

The current targets are inferred from the legacy research recipe context, not from a full 1.12 item registry dump:

- `METALLURGY stage 1` requires `thaumcraft:ingot;1;2` and unlocks/uses the `thaumcraft:brassingot` recipe, so metadata `2` is currently treated as brass ingot target.
- `METALLURGY stage 2` requires `thaumcraft:ingot;1;0` and unlocks/uses the `thaumcraft:thaumiumingot` recipe, so metadata `0` is currently treated as thaumium ingot target.
- `ESSENTIASMELTERTHAUMIUM` uses `plate;1;2` and `metal;1;2` in a stage named for thaumium smelter progression, so metadata `2` is currently treated as thaumium material target for those families.
- `ESSENTIASMELTERVOID` uses `plate;1;3` and `metal;1;3` in a stage named for void smelter progression and is gated by `BASEELDRITCH`, whose recipes include void ingot/void stuff, so metadata `3` is currently treated as void material target.

These inferences are now encoded as bridge mappings, not final parity claims. If a 1.12 runtime item-stack exporter later disproves any metadata target, update `TCLegacyMaterialFamilyMappings` and regenerate the requirement audit.

## Required confirmation source

Before final parity implementation, confirm each mapping from at least one of these:

1. Legacy Thaumcraft 6 source registration / item enum definitions.
2. 1.12 runtime registry dump with item id + metadata + display/unlocalized name.
3. Existing validated exporter output dedicated to material-family item stacks.

## Mapping table

| Legacy raw id | Metadata/damage | Current semantic target | Planned modern item id | Safe for `required_item` consumption? | Safe for `required_craft` marker matching? | Current confirmation source |
|---|---:|---|---|---|---|---|
| `thaumcraft:ingot` | `0` | thaumium ingot | `thaumcraft:thaumium_ingot` | yes as bridge identity | yes as modern-matchable bridge | inferred from `METALLURGY` recipe context |
| `thaumcraft:ingot` | `2` | brass ingot | `thaumcraft:brass_ingot` | yes as bridge identity | yes as modern-matchable bridge | inferred from `METALLURGY` recipe context |
| `thaumcraft:metal` | `2` | thaumium metal block | `thaumcraft:metal_thaumium` | yes as bridge identity | yes as modern-matchable bridge | inferred from `ESSENTIASMELTERTHAUMIUM` context and registered block id |
| `thaumcraft:metal` | `3` | void metal block | `thaumcraft:metal_void` | yes as bridge identity | yes as modern-matchable bridge | inferred from `ESSENTIASMELTERVOID` + `BASEELDRITCH` context and registered block id |
| `thaumcraft:plate` | `2` | thaumium plate | `thaumcraft:thaumium_plate` | yes as bridge identity | yes as modern-matchable bridge | inferred from `ESSENTIASMELTERTHAUMIUM` context |
| `thaumcraft:plate` | `3` | void plate | `thaumcraft:void_plate` | yes as bridge identity | yes as modern-matchable bridge | inferred from `ESSENTIASMELTERVOID` + `BASEELDRITCH` context |
| `thaumcraft:nugget` | `10` | rare earth | `thaumcraft:rare_earth` | yes as bridge identity | yes as modern-matchable bridge | current mapping policy; still needs legacy item-stack confirmation |

## Implementation rule

`TCLegacyMaterialFamilyMappings` may return resolved mappings for these entries because the modern ids now exist. The audit must still keep them in bridge warnings until their source recipes, drops and material semantics are implemented and validated.

## Pass criteria

A material-family requirement can leave the bridge-warning bucket only when all of the following are true:

- the modern item is registered;
- the legacy metadata value maps to exactly one modern semantic material;
- `required_item` consumption cannot consume the wrong material variant;
- `required_craft` marker matching is documented as either modern-matchable or still requiring exact 1.12 craft-hash parity;
- the source recipe/drop or subsystem that should produce the item exists;
- the mapping is documented in this file.
