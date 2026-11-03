# Paired Flat Comparison Worlds

Prepared on 2026-11-03. No gameplay source or original mod jars were modified.

## Open These Worlds

| Version | World in Singleplayer | Instance |
| --- | --- | --- |
| Original TC6 BETA26 / Forge 1.12.2 | `TC6_PARITY_FLAT_1_12_2` | Launcher installation `Thaumcraft 6 - Legacy reference 1.12.2` |
| Current NeoForge 1.21.1 port | `TC6_PARITY_FLAT_1_21_1` | Repository `run-client.bat`, then Singleplayer |

The saves are already installed in each instance's own `saves` directory.
Do not open one version's save in the other version.

Both worlds use a 64-block-deep flat plains preset, seed `61222121`, creative,
cheats enabled, normal difficulty, fixed noon, clear weather, no natural mob
spawning and `randomTickSpeed=0`. The seed does not promise identical aura or
other version-specific procedural output. These are controlled comparison worlds,
not natural-world ecology, crop-growth or survival-balance acceptance tests.

Spawn/camera reset is `0.5 64 -12.5`, yaw/pitch `0 0`, looking south.
Use the same FOV, GUI scale, window resolution and graphics settings in both
clients; the fixture intentionally does not overwrite instance-wide preferences.

## Equipment

The first login supplies nine hotbar stacks: Thaumonomicon, Thaumometer,
Golemancer's Bell, three configured caster gauntlets (normal Exchange, Silk,
Fortune IV), oak planks, stone and chests. The caster's selected replacement is
already oak planks. The selected-block gesture can still change it normally.

Research is deliberately fresh. It is NOT auto-completed, so locked-start book
behavior can be compared. For an all-research test, run the SAME command in either
world: `/function tc_parity:unlock`. This is debug setup, not real progression.

| Station | Location | Ready content |
| --- | --- | --- |
| Blue: Exchange | northwest, `-28..-2 / -2..14` | Ten source blocks, supply chest at `-26 64 -7`, replacement-test chest at `-20 64 12` containing exactly four diamonds |
| Yellow: golems | northeast, `2..28 / -2..14` | Golem chest at `4 64 -7`, populated source and empty destination chests, four-block climbing wall |
| Green: seals | southwest, `-28..-2 / 18..38` | All sixteen seal types at `-26 64 17`, six placement blocks and a test inventory |
| Red: combat/reveal | southeast, `2..28 / 18..38` | Shaded, stationary zombie; stationary cow for scanning; stone wall for occlusion tests |

Station coordinates are `x / z`; the platform surface is always `y=63`.
The six prepared golems are Wood walker, Iron smart/fine, Clay climber,
Brass flyer, Thaumium claw fighter and Void dart fighter, all rank zero.
Their packed material/head/arms/legs/addon/rank bytes match between versions.
Place them yourself: ownership must bind to YOUR player, not the diagnostic user.
Place the desired seals yourself too; this preserves authentic owner/UI behavior.
Do not treat the stationary target's disabled AI as a normal combat-AI test.

## Quick Commands

- `/function tc_parity:home`: exact comparison camera/spawn position.
- `/function tc_parity:refill`: give another starter kit; does not clear inventory.
- `/function tc_parity:unlock`: debug-complete research for the current player.
- `/gamemode survival`: check real material/vis consumption; creative skips consumption.
- `/gamemode creative`: return to setup mode.

Do not manually run `tc_parity:build` after beginning tests: it reconstructs
station blocks/chests. Restore a pristine copy instead.

## Restore Without Losing A Test

Double-click **Restore clean test worlds.bat** here. It restores BOTH pristine
archives into NEW timestamped `..._RESET_...` saves, renames their menu titles,
and leaves every current world untouched. Close/reopen the Singleplayer list.
No existing save is deleted, moved or overwritten. For a clean comparison, use
the two copies with the same reset timestamp.

`*_PRISTINE.zip` are compact, uninitialized native saves with their version-specific
setup functions. Stations are built by Minecraft once on first load; the starter
kit is given once per new player. They contain no imported account credentials.
`*_READY.zip` are local full snapshots after actual in-game construction and
verification; they are intentionally excluded from Git.

The reset button uses the configured local Python runtime and `nbtlib` installed
under ignored `tools/reports/local/fixture-python`. On another PC install nbtlib
and run `tools/fixtures/restore-parity-worlds.py` with that Python instead.

## Verification

Legacy was opened in the original unmodified jar; its functions built the stations
and gave all nine starter stacks. It was then saved and closed normally.
Modern was built/saved by a dedicated server using the current port; its functions
parsed after the container-slot codec correction and loaded all three configured
casters. Startup-attempt errors before that correction are not hidden or treated
as successful validation.

`tools/fixtures/verify_parity_worlds.py` reads actual region NBT in BOTH saves,
checks supplies/casters, all sixteen seals, six golem property sets, transfer
chests and four chest diamonds, and tests pristine ZIP integrity. Exchange-area aura is present and
usable in both saved worlds, and the legacy player's starter inventory is saved.
The paired reset was tested in isolated directories without replacing either
installed world. These checks do not claim that gameplay or visuals already
match. Existing user worlds were untouched.
