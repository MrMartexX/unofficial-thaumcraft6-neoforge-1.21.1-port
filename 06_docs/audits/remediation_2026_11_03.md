# Review remediation checkpoint

Date: 2026-11-03. Baseline: `72eaac17` plus the current uncommitted remediation.

This is an implementation/evidence checkpoint, not a declaration of full parity.
Migration guide sections 10.3, 11 and 12 govern inventory operations, persisted
world state, components and server-authoritative interaction.

## Implemented

- Pouch contents are bound to their original stack; the open pouch cannot be
  picked up or used as a hotbar/offhand swap source in its menu.
- Pattern Crafter uses cumulative destination-capacity planning and retains
  unexpected leftovers by returning or dropping them instead of deleting them.
- Golem and seal interactions check ownership. Golem Press rejects active-job
  replacement, nonzero starting rank, invalid packed IDs and unresearched parts.
- Selected Exchange material is a persisted/synced immutable component, picked
  with the legacy sneak interaction and only exposed by a block-picker focus.
- Exchange uses that selection, validates permissions/material/hardness, honors
  cancellation, supplies silk/fortune loot context, and charges replacement vis.
  It does not use an arbitrary offhand block or destroy the source first.
- Runic capacity uses a scoped transient max-absorption modifier, preserves
  external capacity, and does not remove existing charge when capacity grows.
- Server seals belong to per-dimension SavedData rather than a static server map.
  Load creates independent seal instances; shutdown clears transient task/client
  maps, and unloaded chunks do not tick seals.
- Advanced turret animal targeting no longer bypasses owned-pet protection.
- Frost amplifier, Curse probabilities/potency/effect set and Earth hardness
  gating have been corrected against their individual legacy effect classes.
- Crystals now carry generations 1-4, generation-dependent tick/growth limits,
  base-relative flux growth, neighbor-gated depletion and flux-funded spreading.
- Artifice audit properties are connected to the Gradle server run.
- The asset linter requires exact runtime resource locations. The client smoke
  script refuses global Java/Minecraft cleanup and no longer calls an early
  startup signal verified rendering.
- Queued golem jobs now have acquisition, reservation, tag/lock/color gates,
  navigation, seal callbacks and expiry. Fill/Empty transfer real stacks;
  Stock/Provider create and execute pickup/delivery jobs. Use executes its default
  right-click behavior with a fake player and returns unused items to the golem.
- Task IDs are monotonic instead of millisecond hashes, avoiding replacement of
  jobs created for the same position in the same millisecond.
- Filter size limits are saved along with the filter items and blacklist mode.
- Client smoke now waits for an actual post-title-render marker and five-second
  grace period, inspects stdout and stderr, and only stops its own process tree.

## Confirmed runtime evidence

- Fresh-JVM seal write -> shutdown/save -> fresh-JVM read: PASS, including owner.
- Seal serialization round trip and independent restored instances: PASS.
- Exchange selected-block replacement, bedrock rejection and canceled placement:
  PASS in the focused server audit.
- Runic absorption capacity, repeated updates, capacity growth and preservation
  of external capacity on removal: PASS; equipment audit 23/23.
- Turret owned-pet protection and explicit friendly-target opt-in: PASS.
- Artifice audit 14/14, including cumulative honey/bottle destination capacity.
- Survival Exchange rejects missing vis without consuming material, consumes one
  replacement and 0.25 vis for normal exchange, returns cobblestone normally and
  stone with Silk Touch. The focused server audit passes these cases.
- Golem Press rejects visible but extraction-locked ingredients; the focused
  handler test passes without changing the locked inventory.
- Pouch menu rejects hotbar swapping of the open pouch and prevents picking up
  its slot; the focused server menu audit passes.
- Previous corrected master audit 31/31. This does not certify every behavior.

Raw outputs are ignored under `tools/reports/local/review-20261102/`.
No player save or unrelated Minecraft instance was used.

The follow-up golem runtime audit passes 17 checks, including actual navigation
from nine blocks away using the registered AI (not a direct goal call), three-item
pickup, real chest delivery/extraction, Stock/Provider replenishment, and Use
placing a block while keeping its two unused blocks. Lock/color rejection is
tested separately. These do not certify advanced toggles or the client UI.

The rewritten client smoke was run twice. Both runs reached the rendered-title
marker and survived its grace period. The second run no longer reports the active
effect-glimmer missing texture or Thaumcraft's missing runic recharge sound.

## Still incomplete

- Full golem path-recovery/home/follow parity and advanced seal options; the core
  queued operations and default actions above now have actual behavior tests.
- Seal client sync, configuration GUI and full logistics acceptance.
- Authored focus graph traversal, nested mediums, Scatter and split semantics.
- Modular golem renderer and measured client visual parity.
- Full crystal ecology/distribution comparison, including attachment/support
  policy, not merely registration/state/drop checks.
- Remaining Exchange fortune distributions, compatibility cases and legacy swap
  FX/timing; the basic survival/material/vis/Silk Touch cases above are verified.
- End-to-end press component transactions against extraction-restricting handlers.
- In-world client visual acceptance, beyond the verified rendered title screen.
- The strict resource triage is recorded in `resource_triage_2026_11_03.md`;
  preserved unregistered reference resources are not active runtime defects.

The historical all-findings-resolved/aggregate-parity claims are not acceptance
criteria. Keep these open boundaries explicit until their actual scenarios pass.
