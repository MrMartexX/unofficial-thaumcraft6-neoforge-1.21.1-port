# Thaumcraft Aspect Parity Runtime Dumps

This folder contains the first runtime parity harness for comparing original Thaumcraft 6 Forge 1.12.2 aspect output with the NeoForge 1.21.1 port.

## Current Artifacts

| Artifact | Purpose |
|---|---|
| `legacy_exporter/` | Standalone Forge 1.12.2 diagnostic addon source. |
| `dumps/thaumcraft_1_12_aspects.json` | Runtime dump from original `Thaumcraft-1.12.2-6.1.BETA26.jar`. |
| `dumps/thaumcraft_1_21_aspects.json` | Runtime dump from the NeoForge 1.21.1 port. |
| `tools/compare_aspect_dumps.py` | Deterministic JSON/Markdown diff generator. |
| `reports/aspect_diff.json` | Machine-readable diff report. |
| `reports/aspect_diff.md` | Human-readable diff summary. |

`legacy_server/` is a generated local Forge server folder and is intentionally ignored.

## First Run Summary

| Metric | Count |
|---|---:|
| Legacy 1.12 dump entries | `1792` |
| Modern 1.21 dump entries | `1986` |
| Legacy comparison keys | `1117` |
| Modern comparison keys | `1986` |
| Comparable keys | `634` |
| Identical comparable keys | `224` |
| Amount diffs | `41` |
| Aspect-set diffs | `339` |
| Null/empty diffs | `1` |
| Result-kind diffs | `29` |
| Legacy-only keys | `483` |
| Modern-only keys | `1352` |

The first run proves the harness works and exposes real parity gaps. It is not yet a final balance decision because many `LEGACY_ONLY` and `MODERN_ONLY` entries are expected from version differences and unported Thaumcraft items.

## Commands Used

Build the 1.12 exporter:

```powershell
$env:GRADLE_OPTS='--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.invoke=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED'
& 'D:\Thaumcraft_6_port_to_1.21.1\02_existing_decompiled_repo\Thaumcraft-6-Source-Code-master\gradlew.bat' -p 'D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\aspect_parity\legacy_exporter' build --no-daemon
```

Run the NeoForge 1.21 dump:

```powershell
$dump='D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\aspect_parity\dumps\thaumcraft_1_21_aspects.json'
.\gradlew.bat runServer --no-daemon -PtcAspectDump=true "-PtcAspectDumpPath=$dump"
```

Run the Forge 1.12 dump:

```powershell
$dump='D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\aspect_parity\dumps\thaumcraft_1_12_aspects.json'
& 'C:\Users\Martin\AppData\Roaming\.minecraft\runtime\jre-legacy\windows\jre-legacy\bin\java.exe' -Xmx3G '-Dtc.aspectDump=true' "-Dtc.aspectDumpPath=$dump" -jar 'forge-1.12.2-14.23.5.2860.jar' nogui
```

Generate the diff:

```powershell
python 'D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\aspect_parity\tools\compare_aspect_dumps.py'
```
