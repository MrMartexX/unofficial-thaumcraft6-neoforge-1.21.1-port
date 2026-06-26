# Item/block minimal GameTest fixture

Batch 52 adds a minimal opt-in scripted GameTest/runtime fixture for the item/block parity framework.

## Files

- `05_neoforge_port/src/main/java/thaumcraft/common/runtime/TCMinimalGameTestFixture.java`
- `05_neoforge_port/src/main/java/thaumcraft/common/runtime/TCMinimalGameTestFixtureExporter.java`

## Purpose

The fixture is intentionally small. It validates that a dedicated server reaches `ServerStartedEvent` and that representative Thaumcraft registry entries resolve after bootstrap:

- `thaumcraft:arcane_workbench`
- `thaumcraft:research_table`
- `thaumcraft:crucible`
- `thaumcraft:thaumonomicon`
- `thaumcraft:thaumometer`

This is a runtime smoke fixture, not a gameplay parity claim.

## Opt-in command

```powershell
.\gradlew.bat -p 05_neoforge_port runServer -PtcRunServerWorld=TC_GAMETEST_MINIMAL -Dtc.minimalGameTestFixture=true -Dtc.minimalGameTestFixturePath=../tools/reports/local/runtime/minimal_gametest_fixture.md
```

The exporter halts the server after writing the report. This keeps the fixture suitable for local or CI opt-in execution without turning every verifier run into a Minecraft runtime launch.

## Framework boundary

The `game_test_smoke` audit remains report-only. It now inventories the minimal fixture source, exporter wiring, Gradle opt-in properties and recommended command. Strict runtime execution remains a later policy decision.
