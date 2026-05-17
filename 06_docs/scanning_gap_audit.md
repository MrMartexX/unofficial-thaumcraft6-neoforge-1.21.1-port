# Scanning Gap Audit

Status: active audit for the current research/knowledge/scanning slice.

Guide rule applied: scanning must stay server-authoritative, data-driven where practical, and must not depend on client-only rendering or legacy `SimpleNetworkWrapper`. Scan-key mutation now goes through the current server research progression layer; full rewards, recipe unlocks, warp, and Thaumonomicon UI remain blocked until their subsystems exist.

## Legacy 1.12.2 Flow

| Area | Legacy behavior | Source |
|---|---|---|
| Thaumometer right-click | Client plays scan sound and particles; server scans target. | `ItemThaumometer.onItemRightClick` |
| Target order | Entity target first via `EntityUtils.getPointedEntity(world, player, 1.0, 9.0, 0.0f, true)`, then block ray trace at normal item reach, then sky/null scan. Entity selection is an AABB/line-of-sight zone pick, not a single crosshair pixel. | `ItemThaumometer.doScan`, `EntityUtils.getPointedEntity` |
| Dropped items | `EntityItem` is passed to `ScanningManager`; `ScanItem`, `ScanAspect`, `ScanOreDictionary`, and `ScanGeneric` unwrap its `ItemStack`. | `ScanningManager.getItemFromParms`, scan predicate classes |
| Aspect-trigger scans | Every `Aspect` registers `ScanAspect("!" + tag)`. | `Aspect` constructor / `ScanAspect` |
| Generic scans | Any item/block/entity with aspects grants a generic `!id` research key and observation points by research category formula. | `ScanGeneric` |
| Inventory scan side effect | Scanning a block position also scans up to `100` item handler slots above that block. | `ScanningManager.scanTheThing` |
| Sky scan | Requires overworld, sky visibility, looking up, and `CELESTIALSCANNING`; creates celestial notes when paper and scribing tools exist. | `ScanSky` |
| Held update | While held, periodically syncs aura, starts `FLUX` research warning, and highlights still-scannable targets client-side. Entity highlight uses range `16` with `padding=5`; block highlight uses a separate wild ray at range `16` with random yaw/pitch offsets, so the visible highlight sweeps an area around the crosshair. | `ItemThaumometer.onUpdate`, `ItemThaumometer.getRayTraceResultFromPlayerWild` |

## Current Port Status

| Area | Status | Notes |
|---|---|---|
| Read-only held scan | Implemented | `/tc scan held` uses current stack aspect and scan predicate resolution. |
| Read-only looking scan | Implemented | Entity, block, and gated sky predicate paths exist. Entity/item targeting now uses the shared legacy-shaped zone resolver: min range `1`, scan range `9`, inflated entity hitboxes, and line-of-sight checks. |
| Dropped item target | Fixed | `TCScanningManager` allows `ItemEntity` in the look target finder and evaluates it through the legacy item-stack predicate path. |
| Item/potion/enchantment parity dump | Implemented | Latest comparable item-level report remains `1139/1139` parity-ok. |
| Entity audit dump | Implemented for modern side | `/tc scan audit_entities` and `-PtcScanEntityDump=true` write entity scan/aspect fixtures, including charged creeper and dropped diamond item. |
| Data-driven scannables | Implemented for available ids | `legacy_core.json` covers currently registered blocks/items/entities only. Missing Thaumcraft content is intentionally deferred. |
| Dynamic potion/enchantment scans | Implemented | Uses modern mob-effect and enchantment registries. |
| Sky predicate | Implemented as predicate only | Celestial-note item creation is deferred. |
| Client scan FX/highlight | Started, legacy target resolver wired | Right-click rune particles, held-target sparkle highlight, and living-mob aspect icon overlay exist. Held entity highlight uses range `16` plus `padding=5`; block highlight uses legacy wild block rays at range `16`; click scan visuals use the shorter range `9` entity target and normal block reach. Aspect icons now follow legacy UV order, use byte-identical PNGs, and are dimmed by the legacy brightness compensation path; amount text uses the second legacy `Z=90` rotation. Client-side potential scannables include data scannables as well as aspect-bearing objects, so no-aspect scan predicates such as arrows/fireballs can highlight. Completed research keys are synced to the client and used to suppress already-known sparkle targets, while living-mob aspect icons stay visible for aspect-bearing mobs like legacy. |
| Right-click scan mutation | Implemented for current predicate layer | Thaumometer right-click now calls `ScanningManager.scanTheThing` through the same server target path, uses `TCResearchManager.progressResearch` instead of raw key insertion, grants research keys only when they were not already known and requisites allow progress, triggers current `ScanAspect` observation rewards only on successful new/suppressed scans, suppresses blank-key status messages, and sends legacy `tc.knownobject` / `tc.unknownobject` status messages otherwise. |
| Generic observation rewards | Implemented | `ScanAspect` now grants the legacy raw `+1` observation unit to AUROMANCY/BASICS/ALCHEMY instead of a full player-facing point. `TCScanGeneric.onSuccess` now applies each research category formula to resolved aspects and grants raw OBSERVATION units in one server-side knowledge mutation. |
| Held aura sync | Implemented minimal legacy behavior | `inventoryTick` sends the current aura chunk every 20 ticks while selected or in hotbar slot 0, matching the legacy `isSelected || itemSlot == 0` readiness rule. Flux warning starts `FLUX` through `TCResearchManager.startResearchWithPopup` when current aura flux exceeds legacy thresholds. |
| Block inventory side effect | Partly implemented | Scanning a block now also scans up to 100 slots in a vanilla `Container` block entity above it. NeoForge item-handler capability support remains a future block-entity/capability integration detail. |

## Remaining Work

| Gap | Blocker | Decision |
|---|---|---|
| Full research progression after scan | Rewards, recipes, warp, and UI | Research stage storage, flags, parent requisites, `progressResearch`, `completeResearch`, sibling propagation, and known-vs-new scan success are active. Reward items/knowledge, warp, recipe unlocks, addendum notifications, and GUI page handling are deferred. |
| `ScanGeneric` observation rewards | Done for current research categories | Keep validating this through real scan use once Thaumonomicon pages display knowledge costs; the formula is now implemented on `TCResearchCategoryDefinition`. |
| NeoForge item-handler inventory scan side effect | Modern inventory capability policy | Vanilla `Container` above-block scanning is active. NeoForge item-handler capability scanning should be added when block entities/capabilities are ported. |
| Sky celestial-note side effects | `celestial_notes`, `scribing_tools`, paper consumption and daily `CEL_*` cleanup semantics | Defer until those items and research entries exist. |
| Exact client `isThingStillScannable` suppression | Future server-only scannable predicates | Completed research keys are synced on login/respawn/dimension change and after server-side knowledge mutations. Current highlight suppresses potential targets when all locally derived scan keys are already known. Future server-only scannable predicates must be mirrored or safely synced before they can drive client sparkles. |
| Aura HUD while holding Thaumometer | Aura payload/client display design | Minimal held aura sync is active. The legacy left-side aura meter/HUD rendering is deferred to a focused aura client-display slice. |
| Thaumcraft custom entity scans | Custom entity registrations | Add rows when entities exist; do not create fake ids. |
| Legacy entity dump comparison | Done for current comparable fixtures | Forge 1.12 and NeoForge 1.21 entity/state dumps compare at `83/85` fully parity-ok rows plus `2` documented modern policy rows, with `0` actionable gaps. |
| Stack-specific `ScanItem` matching | Future scannables with Data Components or exact variant state | Keep simple id matching for current definitions; redesign before adding component-sensitive scan JSON. |

## Validation Commands

```powershell
cd D:\Thaumcraft_6_port_to_1.21.1\05_neoforge_port
.\gradlew.bat compileJava --no-daemon --no-configuration-cache

$dump='D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\scan_parity\dumps\thaumcraft_1_21_scan_entities.json'
.\gradlew.bat runServer --no-daemon --no-configuration-cache -PtcScanEntityDump=true "-PtcScanEntityDumpPath=$dump"
```

Latest modern entity dump result:

- entries: `131`;
- active scan predicates: `205`;
- scan-found entries: `96`;
- aspect-bearing entries: `89`;
- empty/unscannable entries: `35`;
- representative exact checks pass for spider, bat, enderman, creeper, charged creeper, elder guardian, zombie villager, and dropped diamond item.
