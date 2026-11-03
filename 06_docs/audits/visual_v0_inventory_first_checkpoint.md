# V0 visual-coverage inventory: first source-backed checkpoint

**Status:** initial source-level checkpoint, not a completed inventory or visual-parity verdict.  
**Pinned `main` SHA:** `72eaac17a1e1c0c51b27cb151ef7f244944c89d5` (2026-11-03).  
**Research parent:** draft [visual parity PR #10](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/pull/10), findings D/H/N and F01–F136.

## Purpose

The legacy item/block audit already has an [item/block manifest extractor](../../tools/audits/item-block-parity/extract-port-manifest.ps1), but its manifest does not enumerate all renderer, menu and other visual boundaries. This V0 checkpoint adds a **separate** cross-category inventory so the existing audit remains unchanged. It introduces no mod/CI changes and makes no claim that source declarations prove runtime parity.

## Verified source-declaration counts

| Category | Registered IDs / explicit bindings | Important limitation |
| --- | ---: | --- |
| Items | 487 | Declarations via `ITEMS.register`, `blockItem`, `simpleItem`, `spawnEgg`; not the number of Creative ItemStacks |
| Blocks | 175 | Not their blockstate combinations, selected shapes or effective rendering |
| Entity types | 43 | An entity type is not its client visual |
| Block entity types | 45 | Many block entities legitimately have no special BER |
| Menu types | 13 | Menus outside the player-visible screen or overlays not inventoried |
| Creative tabs | 1 | Does not enumerate the displayed stack/variant order |
| Entity renderer registrations | 43 | Includes intended and unintended invisible renderers; must review semantics |
| Block entity renderer registrations | 10 | Only direct registered BERs; absence alone is not a defect |
| Menu screen registrations | 13 | Independent screens/HUD excluded |

**Static integrity observations:** 43/43 entity types have an explicit renderer registration; 13/13 menus have a registered screen; no unresolved symbol mappings or duplicate IDs were found among the extracted declarations. **35/45 block-entity types lack a direct BER binding**, which is a triage list, not 35 defects.

These numbers were extracted from the pinned source files using an independent exact-declaration parser; they are included in [the pinned snapshot](generated/visual_v0_port_registry_source_snapshot_2026_11_03.json). They must be recalculated on every new `main` SHA. A previous report's “approximately 466 item entries” is not comparable without reconciling item aliases, helper registrations, variants and date of inspection.

## Reproducible local extraction

Run from the repository root with PowerShell 7:

```powershell
pwsh -NoProfile -File tools/audits/visual-parity/extract-active-visual-registry-v0.ps1 -RepoRoot .
```

Optional fail-on-integrity mode:

```powershell
pwsh -NoProfile -File tools/audits/visual-parity/extract-active-visual-registry-v0.ps1 -RepoRoot . -FailOnIntegrityError
```

Default output: `tools/reports/local/visual-parity/v0-active-registry.json`.

The new extractor additionally enumerates files under `assets/thaumcraft` into resource families and fingerprints the source files with SHA-256. The committed GitHub snapshot is **source-only** and records GitHub blob SHAs instead, because this environment cannot execute PowerShell or traverse a local checkout. The committed snapshot is a genuine static baseline, **not** an execution log from the new script.

Before accepting the local extractor as an automation gate, run both commands on a clean main checkout, compare their source counts and bindings to the pinned snapshot, examine the asset-path list and save the complete output. If future Java registration style changes, adjust extraction rules; non-zero output alone does not guarantee a complete parser. Do not turn this into a failing CI gate before comparing with runtime registrations and excluding intentional no-renderer paths.

## Required next V0 passes

1. **Runtime registry truth:** compare actual active IDs and aliases against this source inventory and the existing item/block audit manifests. Every reported source-only/runtime-only ID needs a classification.
2. **Legacy two-way crosswalk:** from original 1.12.2 registrations and JAR, annotate each active modern ID as exact match, rename, intentionally new, removed, deferred, or unknown; reverse-check original objects that have no modern candidate.
3. **Dynamic stack and state expansion:** Creative inventory order, ItemStack components/NBT variants, blockstate permutations, equipment states, display transforms and conditional visibility. One registered ID is not necessarily one rendered object.
4. **Visual roots beyond registries:** enumerate non-menu screens, HUD, standalone renderer callbacks, particle and sound emission/consumers, shaders, textures and model parent chains. Identify orphan resources separately from missing active consumers.
5. **Evidence closure:** link each object/state to the applicable research finding or explicit `NOT_CHECKED`, then match original/port runtime captures; preserve positive controls and `requires_client` status.

**V0 exit criterion:** no unclassified active or legacy-only IDs, each applicable unique visual branch assigned a proof record, and independent source/runtime enumerations reconciled. A fully executed V0 inventory is necessary but not sufficient for overall visual parity.

## Test status

- **Performed:** source-registry parsing against the pinned GitHub source; duplicate-ID/unknown binding checks; cross-check of 43 renderer and 13 menu-screen associations.
- **Not performed:** executing PowerShell, Gradle build, Minecraft client/server or runtime resource discovery. No environment containing the full working repository and PowerShell 7 was available here.
- **No code changes:** only V0 audit tooling and documentation are proposed in this branch.
