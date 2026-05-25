# Thaumcraft Scan Parity Runtime Dumps

This folder contains the runtime parity harness for comparing original Thaumcraft 6 Forge 1.12.2 scan matching with the NeoForge 1.21.1 port.

## Artifact policy

The `dumps/*.json` and `reports/*.json` files are generated audit artifacts. They are committed for reproducibility and historical parity review, but they are not hand-authored runtime source and should not be edited manually.

If scan/aspect behavior changes, regenerate the dumps with the commands below, regenerate the Markdown/JSON reports, and review the human-readable `reports/*.md` summaries first. The repository `.gitattributes` marks the generated JSON artifacts as `linguist-generated=true` and disables normal inline diffs for them to keep review noise manageable.

## Artifacts

| Artifact | Purpose |
|---|---|
| `legacy_exporter/` | Standalone Forge 1.12.2 diagnostic addon source. |
| `dumps/thaumcraft_1_12_scan_items.json` | Runtime item/potion/enchantment scan dump from original Thaumcraft 6. Generated audit artifact. |
| `dumps/thaumcraft_1_12_scan_entities.json` | Runtime entity/state-variant scan dump from original Thaumcraft 6. Generated audit artifact. |
| `dumps/thaumcraft_1_21_scan_items.json` | Runtime item/potion/enchantment scan dump from the NeoForge port. Generated audit artifact. |
| `dumps/thaumcraft_1_21_scan_entities.json` | Runtime entity scan dump from the NeoForge port. Generated audit artifact. |
| `tools/compare_scan_dumps.py` | Deterministic JSON/Markdown diff generator. |
| `tools/compare_entity_scan_dumps.py` | Deterministic entity/state-variant JSON/Markdown diff generator. |
| `reports/scan_diff.json` | Machine-readable generated diff report. |
| `reports/scan_diff.md` | Human-readable diff summary. |
| `reports/entity_scan_diff.json` | Machine-readable generated entity diff report. |
| `reports/entity_scan_diff.md` | Human-readable entity diff summary. |

`legacy_server/` is a generated local Forge server folder and is intentionally ignored.

## Build the 1.12 exporter

```powershell
$env:GRADLE_OPTS='--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.invoke=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED'
& 'D:\Thaumcraft_6_port_to_1.21.1\02_existing_decompiled_repo\Thaumcraft-6-Source-Code-master\gradlew.bat' -p 'D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\scan_parity\legacy_exporter' build --no-daemon
```

## Run the NeoForge 1.21 dump

```powershell
cd 'D:\Thaumcraft_6_port_to_1.21.1\05_neoforge_port'
$dump='D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\scan_parity\dumps\thaumcraft_1_21_scan_items.json'
.\gradlew.bat runServer --no-daemon -PtcScanDump=true "-PtcScanDumpPath=$($dump)"

$entityDump='D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\scan_parity\dumps\thaumcraft_1_21_scan_entities.json'
.\gradlew.bat runServer --no-daemon -PtcScanEntityDump=true "-PtcScanEntityDumpPath=$($entityDump)"
```

This uses a NeoForge fake player as the server-side scan context and shuts the server down after the dump is written.

## Run the Forge 1.12 dump

```powershell
cd 'D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\scan_parity\legacy_server'
$dump='D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\scan_parity\dumps\thaumcraft_1_12_scan_items.json'
& 'C:\Users\Martin\AppData\Roaming\.minecraft\runtime\jre-legacy\windows\jre-legacy\bin\java.exe' -Xmx3G '-Dtc.scanDump=true' "-Dtc.scanDumpPath=$($dump)" -jar 'forge-1.12.2-14.23.5.2860.jar' nogui

$entityDump='D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\scan_parity\dumps\thaumcraft_1_12_scan_entities.json'
& 'C:\Users\Martin\AppData\Roaming\.minecraft\runtime\jre-legacy\windows\jre-legacy\bin\java.exe' -Xmx3G '-Dtc.scanEntityDump=true' "-Dtc.scanEntityDumpPath=$($entityDump)" -jar 'forge-1.12.2-14.23.5.2860.jar' nogui
```

## Generate the diff

```powershell
python 'D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\scan_parity\tools\compare_scan_dumps.py'
python 'D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\scan_parity\tools\compare_entity_scan_dumps.py'
```

The scan comparer reuses `aspect_parity/input/legacy_to_modern_stack_map.json` for legacy metadata-to-modern flattened item mappings, then compares scan-resolved aspect lookup output and matched scan research keys. Aspect order is canonicalized in this scan harness because item scan predicates care about aspect set/amount and research keys; strict display/order parity remains covered by the aspect runtime harness.

The current item-level report has `1139/1139` comparable rows classified as `PARITY_OK`, with `0` scan key, scan-found, aspect-value, order, amount, set, kind, or null/empty differences. The legacy exporter intentionally does not call `AspectHelper.generateTags` during scan dumps because the 1.12 implementation mutates the shared object-tag cache and can poison later diagnostic rows.

The current entity report has `83/85` comparable vanilla entity/state rows classified as `PARITY_OK`, plus `2` rows classified as `EXPECTED_MODERN_ENTITY_ASPECT_POLICY` for deliberate living-mob corrections (`elder_guardian`, `zombie_villager`). There are `0` actionable scan key, scan-found, aspect-value, order, amount, set, kind, or null/empty gaps. The expected-only buckets are deferred Thaumcraft custom entities, the synthetic legacy guardian elder-NBT probe, and post-1.12 vanilla/display/helper entity types.
