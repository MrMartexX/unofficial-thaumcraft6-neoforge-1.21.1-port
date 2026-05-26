# Legacy material-family research requirement mapping

Status: planning document for legacy metadata-backed material-family requirements used by research-stage `required_item` and `required_craft` gates.

This document deliberately does **not** resolve the requirements yet. It records the known legacy ids and metadata values seen in `/tc research requirements 50`, and defines the rule that these families must not be mapped until the modern material items and names are confirmed.

## Why this exists

Thaumcraft 6 1.12 used metadata-backed item families such as `thaumcraft:ingot`, `thaumcraft:metal`, `thaumcraft:plate`, and `thaumcraft:nugget`. Minecraft 1.21.1 uses flattened item ids, so the legacy id alone is not enough. The metadata/damage value is part of the semantic identity.

The shared `TCResearchRequirementResolver` now classifies these as `legacy material-family requirement` instead of treating them as ordinary missing registry ids.

## Current audit snapshot

Command:

```text
/tc research requirements 50
```

Current material-family blockers:

| Requirement source | Requirement type | Legacy raw requirement | Observed metadata/damage | Current state |
|---|---|---|---:|---|
| `METALLURGY stage 1` | `required_craft` | `thaumcraft:ingot;1;2` | `2` | blocked: material-family mapping required |
| `METALLURGY stage 2` | `required_craft` | `thaumcraft:ingot;1;0` | `0` | blocked: material-family mapping required |
| `ESSENTIASMELTERTHAUMIUM stage 1` | `required_item` | `thaumcraft:plate;1;2` | `2` | blocked: material-family mapping required |
| `ESSENTIASMELTERTHAUMIUM stage 1` | `required_craft` | `thaumcraft:metal;1;2` | `2` | blocked: material-family mapping required |
| `ESSENTIASMELTERVOID stage 1` | `required_item` | `thaumcraft:plate;1;3` | `3` | blocked: material-family mapping required |
| `ESSENTIASMELTERVOID stage 1` | `required_craft` | `thaumcraft:metal;1;3` | `3` | blocked: material-family mapping required |
| `GRAPPLEGUN stage 1` | `required_item` | `thaumcraft:nugget;1;10` | `10` | blocked: material-family mapping required |

## Do not guess these mappings

Do **not** map these directly until confirmed from legacy source/exported registry data:

| Legacy family | Metadata values currently observed | Why guessing is unsafe |
|---|---|---|
| `thaumcraft:ingot` | `0`, `2` | Different metadata values can represent different materials, and research progression/craft markers depend on exact identity. |
| `thaumcraft:metal` | `2`, `3` | Likely a block or item family with material variants; exact 1.12 semantics need confirmation. |
| `thaumcraft:plate` | `2`, `3` | Plate variants likely correspond to different material tiers; consumption must not accept the wrong plate. |
| `thaumcraft:nugget` | `10` | Nugget metadata may not correspond to the same material index as ingot/metal/plate. |

## Required confirmation source

Before implementation, confirm each mapping from at least one of these:

1. Legacy Thaumcraft 6 source registration / item enum definitions.
2. 1.12 runtime registry dump with item id + metadata + display/unlocalized name.
3. Existing validated exporter output dedicated to material-family item stacks.

## Mapping table template

Fill this only after confirmation:

| Legacy raw id | Metadata/damage | Legacy material name | Modern item id | Safe for `required_item` consumption? | Safe for `required_craft` marker matching? | Confirmation source |
|---|---:|---|---|---|---|---|
| `thaumcraft:ingot` | `0` | TODO | TODO | TODO | TODO | TODO |
| `thaumcraft:ingot` | `2` | TODO | TODO | TODO | TODO | TODO |
| `thaumcraft:metal` | `2` | TODO | TODO | TODO | TODO | TODO |
| `thaumcraft:metal` | `3` | TODO | TODO | TODO | TODO | TODO |
| `thaumcraft:plate` | `2` | TODO | TODO | TODO | TODO | TODO |
| `thaumcraft:plate` | `3` | TODO | TODO | TODO | TODO | TODO |
| `thaumcraft:nugget` | `10` | TODO | TODO | TODO | TODO | TODO |

## Implementation rule

Only after the mapping table is confirmed should `TCResearchRequirementResolver.legacyFlattenedItemId(...)` or a dedicated material-family resolver return modern item ids for these entries.

Until then, these blockers must remain unresolved and explicitly classified as `legacy material-family requirement`.

## Pass criteria

A material-family requirement can be marked resolved only when all of the following are true:

- the modern item is registered;
- the legacy metadata value maps to exactly one modern semantic material;
- `required_item` consumption cannot consume the wrong material variant;
- `required_craft` marker matching is documented as either modern-matchable or still requiring exact 1.12 craft-hash parity;
- the mapping is documented in this file.
