# Infusion Real Source Policy Design

## Scope

This document defines the boundary for the future real aspect/essentia source policy used by in-world infusion completion.

Current implemented state is still audit-only:

- `TCInfusionRecipeMatcher` validates research, catalyst, exact unordered components, and required aspects.
- `TCInfusionCraftingPlan` stores a server-owned immutable start snapshot.
- `TCInfusionCompletionPlan` verifies current world state and required aspects without mutating items.
- `TCInfusionMutationExecutor` can complete an already-audited plan in tests.
- `TCInfusionAspectSource` is an interface with an in-memory audit implementation.
- `TCInfusionAspectSourceResolver` is the named future entry point for real source discovery and currently returns no source.
- `TCInfusionMatrixBlock.isPlayerFacingCompletionEnabled()` is false, so normal caster interaction remains validation/status-only.

## Required policy before player-facing completion

Player-facing completion must not be enabled until all of the following are true:

1. A real source resolver exists and returns a source only from explicitly supported nearby source blocks or systems.
2. The source drain is all-or-nothing and happens before item mutation.
3. A failed source drain leaves source state, center catalyst, component pedestals and active plan unchanged.
4. Component container remainders are preserved on their original pedestals.
5. Catalyst container remainders either have an explicit result/remainder placement policy or remain blocked.
6. External and built-in tag inputs are audited for container/remainder risks.
7. The player-facing matrix interaction calls only the resolver-backed path, never `TCInfusionAspectSource.memory(...)`.

## First allowed implementation slice

The first real source implementation should be intentionally narrow:

- Add a source adapter for one explicitly supported local source type only, after verifying the block/entity exists and has stable storage semantics in the current port.
- Do not implement tube networks, aura drain, alembic automation, multi-block beam logic, instability events, particles, sounds, UI, automation or redstone behavior in the same batch.
- Keep the resolver fail-closed. Unknown blocks and missing block entities must return no source.
- Keep player-facing completion disabled until runtime audits prove success, insufficient-source no-op, missing-source no-op and source/item atomicity.

## Validation requirements

Every real source policy change must run:

- Gradle build.
- Dedicated server smoke.
- `tools/audits/audit-infusion-behavior.ps1`.
- `tools/audits/audit-infusion-tag-input-expansion.ps1` whenever tag inputs or accepted ingredient forms change.

## Deferred behavior

The following remain outside this boundary:

- Essentia tube transport.
- Jar suction and network routing.
- Alembic integration.
- Aura/vis/flux as a source.
- Infusion instability events.
- Beam, particle, sound and animation parity.
- Automation and hopper-style behavior.
- Full player-facing completion trigger.
## First transport-backed source adapter checkpoint

- Added `TCTransportInfusionAspectSource` as the first narrow real-source adapter boundary.
- The adapter is intentionally single-aspect only because the current transport API exposes one face-visible essentia stack.
- The resolver may discover adjacent `TCEssentiaTransport` block entities, but player-facing matrix completion remains disabled.
- Multi-aspect infusion completion remains fail-closed until a broader storage-backed source policy is audited.
