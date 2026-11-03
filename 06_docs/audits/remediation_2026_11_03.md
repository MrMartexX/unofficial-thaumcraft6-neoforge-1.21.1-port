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
- Authored focus node graph traversal, nested mediums (such as Projectile -> Cloud -> Fire),
  ModScatter trajectory forking (scaled by 2.0 / forks), and ModSplit branching (scaled
  by 0.75 multiplier) with recursive downstream execution on impact, detonation, or ticks.

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

The expanded focus runtime audit passes 16 checks, including nested medium execution
(Projectile -> Cloud -> Fire spawning a TCFocusCloudEntity with downstream Fire and 0.5x
power at impact), SplitTarget branch execution delivering 0.75x scaled effects to both
Fire and Frost branches, and Scatter trajectory forking launching 3 distinct projectiles
with 2/forks power scaling.

The rewritten client smoke was run twice. Both runs reached the rendered-title
marker and survived its grace period. The second run no longer reports the active
effect-glimmer missing texture or Thaumcraft's missing runic recharge sound.

## Still incomplete

### Follow-up On 2026-11-03

The subsequent worldgen work is present in the working tree and is not certified
as complete by this remediation report. A current dedicated-server attempt caught
an unknown `thaumcraft:grass_ambient` block in the new configured feature despite
a successful build. The missing legacy `BlockGrassAmbient` now has a registered
`GrassBlock` implementation, copied grass loot, modern snowy model variants,
biome grass tint, and the legacy night/light/position/lifetime particle formula.
The pending placed feature also needs a minus-one Y offset after its heightmap
so its grass predicate tests the surface block rather than the air above it.

The nested-cloud audit initially failed because its hard-coded impact position
was outside accessible entity sections. Merely obtaining the chunk was not
sufficient. The fixture now uses the prepared spawn area and reports observed
cloud power/downstream nodes; all 16 focus checks pass again without changing the
focus execution logic. The 17 golem checks and rendered-title client smoke also
pass on the current local tree. The expanded aggregate audit passes 40/40 checks
(including registration checks for the new worldgen work). These checks do not certify arbitrary worldgen
or full client/gameplay parity.

The original Forge 1.12.2 comparison instance was found and its official-launcher
profile restored with the current D: path and Java 8. JEI 4.16.1.302 was installed
from its published Modrinth release with SHA-512 verification. A diagnostic launch
loaded all seven mods, including Thaumcraft/Baubles/JEI; the wrapper's different
expected JEI message caused a timeout, not a demonstrated mod-loading failure.
No world was opened. See the comparison instance README for launch instructions
and unmodified-original resource warnings.

### Remaining Acceptance Boundaries

The subsequent [seal/golem/press slice](seal_golem_remediation_2026_11_03.md)
implements real menu/sync, articulated OBJ rendering, home/follow foundations and
press extraction compensation, advanced logistics options and real Breaker mining.
It adds 71 passing server checks (including 7200 part/rank attribute combinations
and the restored Dart projectile/cadence)
and live-client menu/cache acknowledgement plus
ten rendered captures. The list below is
narrowed accordingly; it does not erase that slice's explicit limitations.

- Unmapped addon OreDictionary/state metadata and broad terrain/addon-target
  campaigns beyond the now-tested flight, wall ascent, owner combat and controlled
  follow-teleport fallback.
- Remote multi-client and full logistics acceptance. An actual integrated-client
  chunk unwatch/rewatch and saved-world disconnect/reconnect now passes.
- Measured legacy-frame equality. Emote definitions and through-wall reveal are
  implemented; real renderer checks now cover reveal and vanilla leash geometry.
- Full crystal ecology/distribution comparison, including attachment/support
  policy, not merely registration/state/drop checks.
- Exchange follow-up now covers end-tick timing, selection/source snapshots,
  creative vis gating, seeded unsupported-silk Fortune loot, container contents,
  cancellation/reentry, material-slot mutation, centered overflow and unload
  cleanup (focused audit 30/30). See
  [Exchange acceptance](../design/exchange_remediation.md). Arbitrary addon loot
  distributions/callbacks and exact 1.12 visual frames remain unverified.
- Strict cross-mod inventory atomicity beyond the now-tested press escrow/refund
  boundary; reinsertion-rejecting handlers use an explicit conservation fallback.
- Remaining in-world visual scenarios beyond the ten captured golem/menu phases.
- The strict resource triage is recorded in `resource_triage_2026_11_03.md`;
  preserved unregistered reference resources are not active runtime defects.

The historical all-findings-resolved/aggregate-parity claims are not acceptance
criteria. Keep these open boundaries explicit until their actual scenarios pass.
