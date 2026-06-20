# Infusion Real Source Policy Design

Last updated: 2026-06-20

## Scope

This document defines the reviewed aspect-container source boundary for in-world infusion. It follows the migration guide rule that legacy subsystem roles are preserved while storage, persistence and server authority are rebuilt for NeoForge 1.21.1.

Normal player-facing completion remains disabled. The current implementation is a runtime-audited server boundary, not a completed infusion altar.

## Legacy evidence

Legacy `TileInfusionMatrix.craftCycle()` calls `EssentiaHandler.drainEssentia(this, aspect, null, 12, ...)`.

`EssentiaHandler` then:

- scans `x=-12..12`, `z=-12..12`, `y=-12..11` around the matrix;
- discovers `IAspectSource` BlockEntities, not arbitrary tube transport buffers;
- sorts sources by squared distance from the matrix;
- skips blocked sources;
- drains one point of the current aspect per craft cycle;
- refreshes the source cache after a failed pass.

The first legacy source implemented by the port is `thaumcraft:jar_normal`, the Warded Jar. The legacy jar stores one aspect, has capacity `250`, supports a blocked state, exposes comparator fullness, and connects to essentia transport from its top face.

## Implemented boundary

- `TCAspectSourceContainer` is the modern server-side equivalent of the legacy source-container role.
- `TCWardedJarBlockEntity` provides persistent one-aspect storage, capacity `250`, blocked/filter state, simulated/exact drain, top-face transport access, the legacy one-point-per-five-ticks pull cadence, sync and comparator output.
- `TCInfusionAspectSourceResolver` scans the exact legacy range volume and sorts container candidates nearest-first.
- `TCContainerInfusionAspectSource` plans and simulates all allocations before an audit-only full-plan drain.
- `TCTransportInfusionAspectSource` remains an isolated transport simulation test adapter. The resolver never selects it.
- Unknown sources, missing BlockEntities and worlds without supported containers fail closed.
- `TCInfusionMatrixBlock.isPlayerFacingCompletionEnabled()` remains `false`.

Runtime audit coverage verifies:

- adjacent tube buffers are not selected;
- the nearest jar drains first;
- blocked jars are skipped;
- a jar at distance `13` is outside the source boundary;
- an insufficient multi-jar plan mutates no jar;
- a jar pulls exactly one compatible essentia point from the transport above it on its fifth server tick;
- null/missing source resolution fails closed.

## Remaining parity gap

The audit executor currently drains a complete aspect plan before item mutation. Legacy gameplay drains exactly one essentia point per matrix craft cycle and emits source FX for that point. Therefore the current full-plan source adapter must not be wired to normal matrix interaction.

The Warded Jar is implemented only to the persistent storage/source/transport boundary needed here. Label interaction, item-form content preservation, filled-level item models, manual phial transfer and full jar rendering remain separate jar gameplay/rendering work.

Before player-facing completion can be enabled, the matrix needs a persisted server cycle state that records:

1. remaining essentia per aspect;
2. remaining recipe components;
3. cycle delay and component beam countdown;
4. catalyst/component revalidation on every cycle;
5. one-point nearest-source drain and source FX;
6. component consumption/remainder timing;
7. instability progression and failure behavior;
8. final output and crafting event semantics.

## Validation requirements

Every source-policy change must run:

- `gradlew.bat build --no-daemon`;
- `tools/ci/server-smoke.ps1`;
- `tools/audits/audit-infusion-behavior.ps1`;
- `tools/audits/audit-infusion-recipe-data.ps1`;
- `tools/audits/audit-infusion-tag-input-expansion.ps1` when accepted ingredient forms change.

## Deferred source types

- essentia mirrors;
- alembics and other storage machines;
- Brain in a Jar;
- Thaumatorium automation;
- aura/vis/flux sources;
- addon-defined source containers.

Each source type requires a separate storage, persistence and runtime parity audit before resolver registration.
