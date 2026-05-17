# Scanning parity validation

Status: active validation harness for the current scanning/knowledge slice.

This document defines the verification method before Thaumometer scan rewards, research unlocks, or server-authoritative client knowledge sync are connected.

Current item-level result: the latest Forge 1.12.2 vs NeoForge 1.21.1 dump comparison reports `1139/1139` comparable item, potion, enchanted book and representative enchanted equipment rows as parity-ok. There are `0` scan key, scan found, aspect-value, order, amount, set, kind, or null/empty differences in the comparable set.

Current entity-level result: the latest Forge 1.12.2 vs NeoForge 1.21.1 entity dump comparison reports `83/85` comparable vanilla entity and state-variant rows as fully parity-ok, plus `2` expected modern entity aspect policy rows. There are `0` actionable scan key, scan found, entity-aspect, order, amount, set, kind, or null/empty gaps in the comparable set.

## Guide constraints applied

- Keep scanning validation server-authoritative.
- Use debug commands for research/scanning state inspection.
- Keep scannable definitions data-driven and reload-safe.
- Do not use legacy `SimpleNetworkWrapper`; server-authoritative knowledge sync is not part of this slice.
- Do not mutate player research/knowledge during audit dumps.

## Modern audit command

The NeoForge port exposes:

```text
/thaumcraft scan audit_items
/thaum scan audit_items
/tc scan audit_items
/thaumcraft scan audit_entities
/thaum scan audit_entities
/tc scan audit_entities
```

The command writes a deterministic JSON dump to:

```text
scanning_parity/dumps/thaumcraft_1_21_scan_items.json
scanning_parity/dumps/thaumcraft_1_21_scan_entities.json
```

The output path can be overridden with:

```text
-Dtc.scanAuditPath=<path>
-Dtc.scanEntityDumpPath=<path>
```

The command requires a player context because scan predicates use real player knowledge, tags, datapack registries, potion components, enchantment components, and `isThingStillScannable` state.

For automated modern server dumps, the port also supports:

```powershell
cd D:\Thaumcraft_6_port_to_1.21.1\05_neoforge_port
$dump='D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\scan_parity\dumps\thaumcraft_1_21_scan_items.json'
.\gradlew.bat runServer --no-daemon -PtcScanDump=true "-PtcScanDumpPath=$($dump)"

$entityDump='D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\scan_parity\dumps\thaumcraft_1_21_scan_entities.json'
.\gradlew.bat runServer --no-daemon -PtcScanEntityDump=true "-PtcScanEntityDumpPath=$($entityDump)"
```

This path uses a NeoForge fake player as a server-side validation context and shuts the server down after writing the dump.

## Dump coverage

The dump currently includes:

- every registered non-air item id as a plain stack;
- damage samples for damageable items;
- every registered potion as potion, splash potion, lingering potion, and tipped arrow;
- every enchanted book level for every registered enchantment;
- representative enchanted equipment stacks for supported enchantments;
- scan-resolved aspect lookup result;
- `generated_aspects` reserved as a null field in scan dumps;
- chosen aspect source: `object` or `none`;
- matched scan research keys;
- matched scan predicate classes;
- `scan_found`;
- `still_scannable`;
- `suppress_message`;
- active predicate count and predicate class summary.

The audit intentionally does not execute `ScanningManager.scanTheThing`, so it does not grant research keys and does not call scan success side effects.

The scan dump intentionally does not call `AspectHelper.generateTags`. The 1.12 implementation can mutate the shared legacy object-tag cache when `generateTags` is called, which makes later scan rows depend on exporter order instead of real gameplay lookup order. Normal object/generated aspect parity remains covered by the separate aspect runtime harness.

Entity aspect lookup is now validated through runtime entity fixture dumps on both sides. The modern entity dump also verifies the legacy `EntityItem` path on the modern side: the look-target code now permits `ItemEntity`, and the scan predicates unwrap its `ItemStack` just like 1.12.2.

Thaumometer target selection is not guessed from modern crosshair picking. The port uses a shared legacy-shaped resolver for scan/use targets: entity min range `1`, scan range `9`, inflated entity hitboxes, line-of-sight filtering, and dropped item support. The client held highlight deliberately uses the separate legacy visual ranges: entity range `16` with `padding=5`, plus range `16` wild block rays with random yaw/pitch spread.

The entity fixture harness includes:

- every registered entity type that can be constructed in each runtime;
- powered creeper state variant;
- legacy guardian elder-NBT probe;
- dropped diamond `EntityItem` / `ItemEntity` stack path;
- entity aspect lookup result;
- matched entity scan predicates and generic aspect keys.

Latest entity report snapshot:

- legacy entity dump entries: `129`;
- modern entity dump entries: `131`;
- legacy active predicates: `214`;
- modern active predicates: `205`;
- comparable normalized entity keys: `85`;
- fully parity-ok entity scan/aspect rows: `83`;
- expected modern entity aspect policy rows: `2` (`minecraft:elder_guardian`, `minecraft:zombie_villager`);
- actionable entity scan logic rows: `0`;
- expected legacy-only rows: `44`, all deferred Thaumcraft custom entities or the synthetic guardian elder-NBT probe;
- expected modern-only rows: `46`, all post-1.12 vanilla entities/display/helper types.

Important runtime finding: original Thaumcraft 6 registers an intended `Guardian` + `Elder` NBT aspect row in `ConfigAspects`, but real Forge 1.12.2 `minecraft:elder_guardian` resolves through `EntityList.getEntityString(entity)` as `ElderGuardian` and receives no aspects. The port now deliberately treats `minecraft:elder_guardian` as a modern living-mob policy correction using that legacy config intent. `minecraft:zombie_villager` is likewise a deliberate modern policy correction using zombie/villager hybrid semantics. The comparer classifies both rows as `EXPECTED_MODERN_ENTITY_ASPECT_POLICY`, not as parity bugs.

Legacy `Aspect` constructor behavior is preserved: each aspect registers a `ScanAspect("!"+tag)` predicate. The modern reload bootstrap re-adds these aspect predicates before `TCScanGeneric`, matching the legacy order while still surviving datapack reloads.

## Comparison method

For exact legacy parity, use the same structure as the aspect parity harness:

1. Run the legacy Forge 1.12.2 diagnostic addon against real Thaumcraft 6.
2. Export the equivalent scan dump by calling real legacy `ScanningManager` predicates on representative stacks/entities.
3. Run `/tc scan audit_items` in the NeoForge port.
4. Normalize ids through the existing legacy-to-modern stack map.
5. Diff by `comparison_key`, then inspect:
   - missing stack rows;
   - scan-resolved `object_aspects` differences;
   - missing or extra `matched_research_keys`;
   - `still_scannable` differences after seeding equivalent research keys;
   - component cases for potions and enchantments.

The comparison harness lives under:

```text
07_Test_Instance_and_Comparisons/scan_parity/
```

It mirrors the aspect parity harness: a separate Forge 1.12.2 diagnostic addon, `dumps/`, `reports/`, and `tools/compare_scan_dumps.py`.

Latest report snapshot:

- legacy dump entries: `1798`;
- modern dump entries: `1987`;
- comparable normalized keys: `1139`;
- fully parity-ok item-level scan/aspect rows: `1139`;
- aspect-dependent scan/aspect rows: `0`;
- actionable scan logic rows: `0`;
- legacy-only mapping-review rows: `398`;
- modern-only policy-review rows: `848`.

Entity diff artifacts:

```text
07_Test_Instance_and_Comparisons/scan_parity/dumps/thaumcraft_1_12_scan_entities.json
07_Test_Instance_and_Comparisons/scan_parity/dumps/thaumcraft_1_21_scan_entities.json
07_Test_Instance_and_Comparisons/scan_parity/reports/entity_scan_diff.json
07_Test_Instance_and_Comparisons/scan_parity/reports/entity_scan_diff.md
07_Test_Instance_and_Comparisons/scan_parity/tools/compare_entity_scan_dumps.py
```

## Known limits

- Block-only scans for blocks without item forms, material-only block predicates, and block-state fixtures are validated by direct command use and data definition review until block fixtures exist.
- `ScanSky` is only predicate-matched by real player view/world conditions; celestial-note side effects are deferred.
- Full scan rewards remain blocked by research visibility, completion, reward, and sync rules.

## Pass criteria before Thaumometer mutation

- Bundled scannable JSON reloads without warnings.
- Active predicate count is stable after `/reload`.
- Plain item dump has no unexpected `none` aspect sources for policy-covered vanilla/Thaumcraft ids.
- Potion rows produce mob-effect research keys matching modern registry ids.
- Enchanted book/equipment rows produce enchantment research keys matching modern registry ids.
- Known no-aspect legacy exclusions stay unscannable unless an explicit legacy scan predicate exists.
- No audit command mutates player knowledge.
