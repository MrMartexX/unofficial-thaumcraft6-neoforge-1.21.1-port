# Thaumcraft 6 NeoForge 1.21.1 Porting Status

Last updated: 2026-06-04

This file tracks the current porting checkpoint for the NeoForge 1.21.1 port. It is intentionally gate-based: a subsystem is marked complete only when it has passed compile/runtime/audit checks, not merely when code exists.

## Current checkpoint

The current work branch has reached a stable baseline for research loading, exact progression semantics, the permanent research recipe/page catalog, the server-authoritative Thaumonomicon index/entry/action protocol foundation, data/resource integrity, current menu/network handling, and the existing Research Table BlockEntity path. The next research milestone is the Thaumonomicon item/open flow plus the first real browser and entry screens over the authoritative protocol.

## Confirmed OK

### Build and runtime startup

- `compileJava`: OK after research stage warp reward patch.
- `runClient`: OK.
- `runServer`: startup OK; server remains running as expected until manually stopped.
- Dedicated-server client import audit: OK.

### Research and knowledge systems

- Research data reload: OK.
- Research reference validation: OK.
- Scan predicate rebuild: OK.
- Required craft marker flow: OK for current implementation.
- Required craft bridge recipe coverage: OK; all detected `required_craft` targets have a recipe output path.
- Research stage `warp` parsing: OK.
- Research stage advancement and `warp` reward logic now preserve the exact legacy empty-gate, double-add, final-stage combination, `PERMANENT/NORMAL` split, and `wussMode` behavior.
- Checked stage completion plans item and knowledge consumption before applying inventory/knowledge/stage mutation.
- Completion flags, entry rewards, addendum `PAGE` notifications, siblings, XP, and final knowledge sync ordering are implemented.
- Research data parity audit: `148/148` entries, `7/7` categories, `0` source differences, `0` runtime parser differences, and `10/10` progression/parser semantic checks passed.
- Research `recipes` arrays are covered by an exact legacy runtime catalog: `253` occurrences, `203` direct references, and `325` entries including group members, with `0` comparison differences.
- The server-authoritative Thaumonomicon protocol returns only visible categories, entries, current stages, visible addenda, current requirement state, and visible recipe/page bookmarks.
- Thaumonomicon start, stage-advance, and entry-acknowledgement actions validate and mutate on the server. Start preserves legacy `first=true`/`checks=false`/`noFlags=true`, stage advance preserves `first=false`/`checks=true`/`noFlags=true`, and acknowledgement clears the legacy `RESEARCH`/`PAGE` flags before attempting the legacy known-entry final-stage checked progression with `noFlags=false`.

### Aspect data

- Aspect assignments reload: OK.
- Aspect parity validation: OK.
- Generated aspect cache rebuild: OK.
- Aspect coverage audit: OK; 1230/1230 detected Minecraft item ids had non-empty aspects in the latest runtime audit.
- Aspect tag reload validation: OK.

### BlockEntity persistence and sync

- Current BlockEntity persistence/sync static audit: OK for the existing Research Table BlockEntity path.
- No high or medium persistence/sync risk remained in the latest audit.
- Note: this only covers currently implemented BlockEntity systems. Future machines, inventories, fluids, energy, or vis storage must rerun this gate.

### Menus and screens

- Menu/screen sync static audit: OK.
- High menu risks: 0.
- Low menu risks: 0.
- One medium warning was reviewed as a false positive because the Research Table menu uses `AbstractContainerMenu.stillValid(...)` with `ContainerLevelAccess` and the Research Table block.
- `quickMoveStack` is implemented for the current Research Table menu.

### Networking

- Network authority audit: OK for the currently implemented payloads.
- High authority risks: 0.
- Current serverbound Research Table action path validates server player/menu state before applying actions.
- Current serverbound Thaumonomicon requests/actions validate player visibility and stage requirements before returning or mutating state.
- Thaumonomicon index refresh invalidates detailed client entry views; after a stage mutation the server sends the refreshed index before the refreshed entry view.
- Current knowledge sync path is clientbound or client cache acceptance only.
- Future gameplay packets must keep the same rule: client sends intent; server validates and mutates state.

### Resources, loot, tags, and data references

- Item/block model coverage: OK.
- Blockstate coverage: OK.
- Block item model coverage: OK.
- Lang key coverage: OK.
- Block loot table coverage: OK.
- Thaumcraft texture reference audit: OK.
- Mining tags: OK.
  - `mineable/pickaxe`: OK.
  - `mineable/axe`: OK.
  - `needs_iron_tool`: OK.
- Data reference integrity audit: OK.
  - Thaumcraft tag/recipe/loot references resolved against currently registered Thaumcraft item/block ids.
  - Problems found: 0 in the latest audit.
- Runtime `/reload`: OK after resource, loot, and mining tag fixes.

## Known backlog

### Research recipe/page catalog

The permanent catalog now preserves the exact Forge 1.12.2 resolver result for every built-in research recipe/page occurrence.

Latest audit summary:

- Research occurrences: `253`.
- Direct references: `203`.
- Catalog entries including group members: `325`.
- Field differences against the legacy runtime exporter: `0`.
- Structural errors: `0`.
- Currently render-ready normal crafting pages: `2`.
- Explicitly deferred custom/fake/blueprint pages: `193`.
- References missing even in the original runtime baseline: `8`.

The catalog keeps legacy page identifiers separate from modern recipe unlocks. Arcane, crucible, infusion, blueprint, and fake/display-only pages remain explicit deferred entries until their own recipe types and renderers exist.

### Thaumonomicon / recipe visibility

- Server-authoritative index, entry, current-stage, requirement-state, bookmark, start, stage-advance, and entry-acknowledgement payloads are implemented.
- The protocol foundation audit passes `15/15` checks.
- Full Thaumonomicon browser/entry rendering is not complete.
- Arcane, crucible, infusion, blueprint, fake/display-only, and grouped recipe-page renderers are not complete.
- Recipe existence, page visibility, and research completion remain separate states.

### Capabilities and machines

- Full capability coverage is not complete because most machine/inventory/fluid/energy/vis subsystems are not fully ported yet.
- Future machine BlockEntities must be checked for:
  - save/load correctness;
  - `setChanged()` usage;
  - menu sync;
  - capability exposure;
  - recipe cache invalidation;
  - sided access rules.

### Entities and rendering

- Entity systems are not fully audited.
- Rendering parity for entities is not complete.
- Existing GUI/Fx rendering work has been partially validated, but broader entity renderers remain a future gate.

### World generation

World generation remains an independent future subsystem to validate after the current research milestone.

Registered worldgen-related content exists:

- Ore blocks: amber, cinnabar, quartz.
- Greatwood and silverwood logs/leaves/saplings.
- Plants: shimmerleaf, cinderpearl, vishroom.

Pending worldgen checks:

- Java worldgen pipeline detection.
- Configured feature JSON files.
- Placed feature JSON files.
- Biome modifier JSON files.
- New world generation test.
- Dedicated server worldgen test.

### Performance and regression

Not complete yet.

Future gates:

- server/client startup regression;
- `/reload` regression;
- world save/load regression;
- block place/break/drop regression;
- Research Table interaction regression;
- scanning and knowledge progression regression;
- memory/performance check after content expansion.

## Recently completed checkpoints

### Research progression parity and data audit

Completed:

- Added stage `warp` parsing to research stages.
- Added non-negative clamp for parsed warp values.
- Preserved exact legacy stage advancement and research warp calculation/split behavior.
- Added `wussMode` config parity.
- Added planned checked-stage item/knowledge consumption before mutation.
- Added entry reward and addendum completion behavior.
- Added source/runtime/category research data exporter and comparer.
- Latest data parity result is exact, and runtime progression/parser checks are `10/10`.

### Research recipe/page catalog and Thaumonomicon protocol

Completed:

- Added a Forge 1.12.2 runtime exporter for exact research recipe/page resolution.
- Added a reproducible generator/comparer and permanent reload-safe NeoForge page catalog.
- Preserved resolver precedence, canonical legacy ids, ordered group members, required-research gates, legacy outputs, and original missing references.
- Added server-authoritative index, entry, start, stage-advance, and entry-acknowledgement payloads plus an invalidating client view cache.
- Added runtime catalog and protocol audits. Latest results are exact catalog parity and `15/15` protocol checks.

### Required craft bridge recipes

Completed:

- Added/validated bridge recipes for current `required_craft` outputs.
- Runtime recipe manager loaded 1341 recipes.
- Required craft output audit found 0 missing output recipes.

### Resource coverage repair

Completed:

- Added missing block models for metal blocks, nitor, smelter, and stair variants.
- Added missing loot tables for current craft/research-related blocks.
- Repaired missing texture references in placeholder/legacy models.
- Resource coverage audit reached 0 missing files/refs.
- Runtime `/reload` passed.

### Mining tags

Completed:

- Added missing `mineable/pickaxe` entries.
- Added missing `mineable/axe` entries.
- Added missing `needs_iron_tool` entries.
- Mining tag audit reached 0 missing expected tags.
- Runtime `/reload` passed.

### Data reference integrity

Completed:

- Scanned tags, recipes, loot files, and relevant data references.
- Latest result: 0 problems found.

## Recommended next gate

Implement the Thaumonomicon item/open flow and the first real browser/entry screens over the server-authoritative view models. The client may render and navigate returned data, but it must not resolve raw research recipes, decide visibility, or advance stages locally. Keep custom recipe pages explicitly deferred until each custom recipe type and renderer has its own validated subsystem slice.

Required regression commands:

```powershell
cd D:\Thaumcraft_6_port_to_1.21.1\05_neoforge_port
.\gradlew compileJava
.\gradlew runClient
.\gradlew runServer
```

Then verify a fresh world and `/reload` logs.
