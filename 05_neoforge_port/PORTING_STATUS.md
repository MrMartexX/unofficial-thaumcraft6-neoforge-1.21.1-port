# Thaumcraft 6 NeoForge 1.21.1 Porting Status

Last updated: 2026-06-04

This file tracks the current porting checkpoint for the NeoForge 1.21.1 port. It is intentionally gate-based: a subsystem is marked complete only when it has passed compile/runtime/audit checks, not merely when code exists.

## Current checkpoint

The current work branch has reached a stable baseline for research loading, data/resource integrity, current menu/network handling, and the existing Research Table BlockEntity path. The next major subsystem to validate is world generation.

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
- Research stage `warp` reward logic: compile OK and runtime reload OK.
- Research `recipes` arrays were audited but are not yet wired into recipe unlocks or recipe page rendering.

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

### Research recipe refs

The research JSON `recipes` arrays are not yet functionally wired.

Latest audit summary:

- Research recipe refs: 248.
- Exact modern recipe id refs: 4.
- Refs matching recipe output item id: 21.
- Unresolved refs: 223.
- Unique unresolved refs: 189.

Conclusion: these are mostly legacy Thaumcraft recipe/page identifiers, not current Minecraft 1.21.1 recipe ids. Do not implement a naive `awardRecipes` patch yet. The correct future work is a mapping layer:

```text
legacy research recipe ref -> modern recipe id / recipe page / recipe group
```

### Thaumonomicon / recipe visibility

- Full Thaumonomicon recipe page rendering is not complete.
- Recipe visibility/unlock semantics are not complete.
- Legacy research recipe refs need mapping before they can be displayed or unlocked reliably.

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

World generation is the next major subsystem to validate.

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

### Research warp rewards

Completed:

- Added stage `warp` parsing to research stages.
- Added non-negative clamp for parsed warp values.
- Applied permanent warp reward when advancing through completed stages.
- Compile check passed.
- Runtime `/reload` passed.

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

Run the worldgen integration audit and use its results to decide whether to implement ore/tree/plant generation through data-driven configured/placed features and NeoForge biome modifiers.

Expected next command locally:

```powershell
cd D:\Thaumcraft_6_port_to_1.21.1
.\audit_worldgen_integration.ps1 -RepoRoot "D:\Thaumcraft_6_port_to_1.21.1"
```

After worldgen implementation, the required runtime tests are:

```powershell
cd D:\Thaumcraft_6_port_to_1.21.1\05_neoforge_port
.\gradlew compileJava
.\gradlew runClient
.\gradlew runServer
```

Then verify a fresh world and `/reload` logs.
