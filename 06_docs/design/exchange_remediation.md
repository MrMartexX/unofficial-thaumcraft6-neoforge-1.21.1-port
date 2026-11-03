# Exchange Remediation

References: FocusEffectExchange.execute, ServerEvents.tickBlockSwap/addSwapper,
PacketFXBlockBamf, FXDispatcher.drawBamf/drawCurlyWisp from primary BETA26 source.
Follow migration-guide inventory, entity-state and payload ownership boundaries.

Gameplay enqueues a server-owned source/selection snapshot and performs it at
LevelTickEvent.Post, never by a client request. Reject changed sources, unloaded
chunks and departed players; clear pending work on world/server unload. Keep the
immediate helper as a transaction primitive for other server consumers/tests.

Use modern loot tables as the version-specific adapter. Synthetic loot tools may
carry both silk and fortune: supported silk branches win, unsupported silk paths
still see fortune. No XP is awarded by legacy getDrops. Creative requires enough
available vis but consumes neither vis nor material and receives no block loot.

Recheck source/material after callbacks and loot calculation; escrow the selected
material before invoking block replacement callbacks. Return escrow/vis on failed
replacement. This does not promise atomic rollback of arbitrary external mod
callbacks or their own side effects. Reject double-block placement shapes that
cannot be represented by the single-state legacy swap transaction.

Successful replacement alone emits the original32-block-range bamf: color8038177,
sound=true, flair=false, side=null. Preserve per-iteration random loop bounds,
atlas cells, color/lifetime/scale/motion, initial roll and degree-based angular
speed. Optional particle orientation must not change existing effects by default.

Acceptance: queued timing/stale source/lifecycle; costs, creative/no-vis,
permissions and reentry; container contents; callback material-slot changes;
Fortune against the modern vanilla loot oracle with matched random state,
including silk-unsupported mature wheat and silk-supported gravel/ore;
FX descriptor/codec and client pipeline checks.
Version-specific loot changes and pixel-identical lighting remain distinct claims.

## Server Evidence

The opt-in focus-medium/effect audit passes 30/30 (14 added Exchange checks).
The actual Post-event handler is exercised without ticking unrelated game systems.
Fixtures verify deferred execution, selection snapshot, stale-source rejection,
creative minimum vis, Fortune IV gravel, silk ore, unsafe door rejection,
recursive canceled BreakEvent, codec round trip, 128 seeded FX descriptors,
32 mature-wheat drops matched to Fortune-only vanilla using the same loot random
sequence, chest contents and a real neighbor callback moving the material slot.
Full-inventory overflow uses an actual item-join callback to check exact center,
quantity and immediate pickup; the actual unload handler clears queued work.
Gravel is explicitly NOT an unsupported-silk example: its vanilla silk branch
returns gravel. Mature wheat exercises the fallback that previously lost Fortune.

Reports are generated under ignored `tools/reports/local/review-20261102/`.
Run the combined server evidence gate and disposable-world client probe; neither
a green build nor bounded FX descriptors certify matching legacy pixels.

## Combined Acceptance

Final build and dedicated-server run passed: golems 71/71, focus/Exchange 30/30,
aggregate 40/40. `test-golem-remediation-evidence.ps1` checks fresh reports and
successful exporter markers rather than relying on Gradle's exit status.

The disposable integrated-client probe completed all ten captures, server menus,
ghost clicks, watch/unwatch, disconnect and persisted reopen. Exchange's real
client renderer emitted 33,904 finite vertices across 8,476 lifetime samples with
valid atlas UVs and expiration. An actual server-to-client Exchange payload
reached its dimension-checked handler and emitted 11 particles through the engine.
`test-golem-visual-evidence.ps1` now requires those markers too. This is geometry,
codec, network and pipeline evidence, not matched-frame visual certification.

The modern BreakEvent veto is intentionally retained in addition to legacy's
placement veto for protection compatibility. Unsafe multi-block targets are
rejected rather than silently manufacturing half a structure. Arbitrary addon
side effects and different-version loot tables are not asserted to be identical.
