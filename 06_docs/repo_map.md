# Repository map

Last updated: 2026-06-10

This file is a quick orientation map for the Thaumcraft 6 -> NeoForge 1.21.1 port. Use it when context is lost or when deciding where a new change belongs.

## Main rule

The active port lives in `05_neoforge_port/`. The legacy sources, jar, API reference, docs and test-comparison folders are supporting material. Do not copy legacy classes line-by-line; port subsystem behavior and public-facing identity into modern NeoForge 1.21.1 patterns.

## Top-level folders

| Path | Role | How to use it |
|---|---|---|
| `01_original_jar/` | Original Thaumcraft 6 jar reference | Use for original packaged assets/resources and as a fallback source of truth when decompiled sources or imported assets are unclear. |
| `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/` | Decompiled TC6 1.12.2 source reference | Use to compare legacy behavior, formulas, IDs, render logic, recipes, particle behavior and class roles. Do not copy whole classes directly into the port. |
| `04_api_reference/thaumcraft-api-master/` | Legacy public API reference | Use when recreating API-facing names, marker interfaces or compatibility shapes. Keep modern implementation internal until stable. |
| `05_neoforge_port/` | Active NeoForge 1.21.1 Gradle module | Main code/resources/data target. GitHub Actions build and smoke tests focus on this folder. |
| `06_docs/` | Main documentation area | Keep current status, migration notes, decisions, curated audits and repo orientation here. |
| `07_Test_Instance_and_Comparisons/` | Runtime comparison artifacts and screenshots | Use for legacy vs modern dumps, screenshot references, aspect/scan/research parity data and visual comparison material. |
| `tools/` | Reusable helper scripts | Put reusable CI/audit scripts here. Do not keep one-off patch scripts in the repo root. |
| `.github/workflows/` | GitHub Actions CI workflows | Build/smoke/audit automation lives here. Workflows should be path-filtered so unrelated docs changes do not waste CI runs. |

## Active port module: `05_neoforge_port/`

| Path | Role |
|---|---|
| `build.gradle` | ModDevGradle/NeoForge configuration, run configs, audit system properties and generated metadata setup. |
| `gradle.properties` | Minecraft/NeoForge/mod metadata and local Java toolchain settings. CI patches Java path dynamically. |
| `src/main/java/thaumcraft/` | Active Java implementation. |
| `src/main/resources/assets/thaumcraft/` | Client assets: models, textures, lang, blockstates and render-related JSON. |
| `src/main/resources/data/thaumcraft/` | Datapack content: recipes, loot tables, tags, aspect data and research data. |
| `src/generated/resources/` | Generated resources used by Gradle resources source set. |
| `run/` | Local runtime folder. Do not treat it as source material. |
| `build/` | Gradle output and CI logs. Do not commit generated build output. |

## Important Java areas

| Area | Typical package/path | Notes |
|---|---|---|
| Main mod bootstrap | `thaumcraft.Thaumcraft` | Registers current systems and event listeners. Keep side-safe. |
| Registries | `thaumcraft.common.registry` | Blocks, items, block entities, menus, sounds, recipes, creative tabs and data components. |
| Aspects | `thaumcraft.api.aspects`, `thaumcraft.common.aspects` | Core aspect model, data loading and parity-sensitive logic. |
| Research/scanning | `thaumcraft.common.research`, `thaumcraft.common.scanning`, related client packages | Server-owned progression/knowledge/scanning with client display layers. |
| Aura | `thaumcraft.common.aura` | Server-side aura storage/query/tick logic. |
| Menus/screens | `thaumcraft.common.container`, `thaumcraft.client.gui` | NeoForge menu/screen replacements for legacy GUIs. |
| Networking | `thaumcraft.common.network` and payload classes | Custom payloads replace legacy SimpleNetworkWrapper/IMessage. |
| Rendering/FX | `thaumcraft.client.render`, `thaumcraft.client.fx` | High-risk visual parity area. Avoid raw GL copy-paste; document deliberate legacy emulation decisions. |

## Documentation entry points

Read in this order when starting a new session:

1. `06_docs/CURRENT_TASK.md` - current focus, branch and guardrails.
2. `06_docs/current_port_status.md` - current implementation status and document priority list.
3. `06_docs/migration_matrix.md` - gate sequencing, subsystem scope and risk rules.
4. `06_docs/porting_order.md` - staged roadmap.
5. Specific subsystem docs depending on the work.

## Current workflow

1. Keep `main` as the active branch unless a separate feature branch is explicitly needed.
2. Make small focused changes.
3. For code/resource changes under `05_neoforge_port/`, GitHub Actions should run build and dedicated server smoke test.
4. For visual changes, CI is not enough: confirm with local `runClient` screenshots or runtime checks.
5. Put reusable audit scripts under `tools/audits/` and local/generated output under `tools/reports/local/`.
6. Curate only important audit summaries into `06_docs/audits/`.

## Current CI scope

The main GitHub Actions workflow should run for:

- `05_neoforge_port/**`
- `tools/ci/**`
- `.github/workflows/build.yml`
- manual `workflow_dispatch`

It should not run only because ordinary documentation files changed.

## Useful next automation ideas

- Static asset reference audit: verify blockstates, item models, block models and textures reference existing files.
- Client-import/server-safety audit: fail when common/server code imports client-only classes.
- Curated audit artifact upload: run selected safe audits and upload reports without committing raw logs.
- Docs link/index check: verify important docs referenced from `06_docs/README.md` still exist.
