# V0 visual parity: full static resource graph, first pass (2026-11-03)

> **Source-only checkpoint**, not a runtime acceptance test and not a claim of visual equivalence to Thaumcraft 6. Source commit: `72eaac17a1e1c0c51b27cb151ef7f244944c89d5` (`main`). All listed paths are relative to `05_neoforge_port/src/main/resources/assets/thaumcraft/` unless qualified otherwise.

## Scope and reproducibility

- Enumerated the Git trees under `blockstates/`, `models/` and `textures/` at the pinned commit, fetched and parsed **all 227 blockstate JSONs** and **all 784 model JSONs** (253 block models, 531 item models). No JSON syntax/fetch errors in this census.
- Inspected 43 model asset files (`.obj`/`.mtl`) by directory-path presence and 1,390 texture files by directory-path presence (1,259 PNG, 124 animation `.mcmeta` and 7 JPG).
- Joined filenames with the separately captured source registry inventory: **175/175 active blocks** have exact-name `blockstates` JSON and **487/487 active items** have exact-name `models/item` JSON.
- Machine-readable record, including all orphan filenames, source tree SHAs, per-blockstate SHA, slab and pedestal crosswalks: [JSON checkpoint](generated/visual_v0_resource_graph_first_pass_2026_11_03.json).

### Static linkage checks

| Check | Result | Caveat |
| --- | ---: | --- |
| Model references emitted by active blockstate JSON | 862 | 857 local `thaumcraft:`, 5 external `minecraft:` |
| Missing local JSON model targets in active blockstates | **0** | Two legacy `hole` refs use old `thaumcraft:empty`; inventory triage resolves to `models/block/empty.json`, not a modern loader guarantee |
| Local `thaumcraft:` parent links in model JSON | 214 | Zero missing parent targets and zero cycles in this graph |
| Other/vanilla model-parent references | 524 | Their actual loader/runtime resolution was not tested |
| Model override reference occurrences | 22 | All locally targeted override models resolve |
| `neoforge:obj` loader declarations | 15 | Includes children of one `neoforge:composite` model |
| Referenced model asset occurrences (`.obj`/`.mtl`) | 28 | All paths present in repository; mesh/material rendering not verified |
| Explicit `thaumcraft:` texture references from model JSON | 966 (396 unique) | All corresponding PNG paths present; aliases, atlases, animation playback and visual result not verified |

## Orphan and legacy-format blockstates

**52 of 227** `blockstates/*.json` filenames have no exact active block ID. **51/52** match a legacy-format syntax heuristic (`forge_marker`, `defaults` or old selector keys such as `inventory`/`normal`/`found`). The remaining file is `activator_rail.json`, also requiring modernization/ownership review, but not flagged by this narrow heuristic.

**17** orphan blockstates are banner files: `banner_black`, `banner_blue`, `banner_brown`, `banner_crimson_cult`, `banner_cyan`, `banner_gray`, `banner_green`, `banner_lightblue`, `banner_lime`, `banner_magenta`, `banner_orange`, `banner_pink`, `banner_purple`, `banner_red`, `banner_silver`, `banner_white`, `banner_yellow`. Their file presence does **not** restore the original world-placed banners. The current block registry has no same-name banner blocks. Do not delete the files or count them as working content without classifying legacy save compatibility and actual use.

Six orphan `slab_double_*.json` files mirror the six old distinct double-slab IDs. Unlike the banners, there is positive **current blockstate** evidence of an alternative: the six registered single-slab IDs below all have a `type=double` state with a model reference:

- `blockstates/slab_ancient.json`: double state -> `thaumcraft:block/slab_ancient_double`; model path(s) present.
- `blockstates/slab_arcane_brick.json`: double state -> `thaumcraft:block/stone_arcane_brick`; model path(s) present.
- `blockstates/slab_arcane_stone.json`: double state -> `thaumcraft:block/stone_arcane`; model path(s) present.
- `blockstates/slab_eldritch.json`: double state -> `thaumcraft:block/stone_eldritch_tile`; model path(s) present.
- `blockstates/slab_greatwood.json`: double state -> `thaumcraft:block/plank_greatwood`; model path(s) present.
- `blockstates/slab_silverwood.json`: double state -> `thaumcraft:block/plank_silverwood`; model path(s) present.

This supports **state-folding as an implementation candidate**, not established old-world block-ID remapping, shape/collision, drops, placement or visual fidelity.

The orphan `blockstates/thaumometer.json` uses old `thaumcraft:scanner.obj` as a JSON `model` value (not a resolvable modern JSON-model path). The active `models/item/thaumometer.json` is instead a `neoforge:composite` that references the present `models/item/scanner.obj`. This is **not** an active missing-item-model verdict.

The **one active** blockstate with retained Forge-style syntax is `blockstates/hole.json` (`forge_marker:1`, `inventory` and `normal` variants, `thaumcraft:empty` model refs). The block is registered; `TCHoleBlock.getRenderShape()` returns `RenderShape.INVISIBLE`. This is a loader-compatibility/data-hygiene review item, **not** an observed missing-texture/visible-regression report.

## Pedestal registration pairs

Three pairs of **distinct registered block IDs** reference precisely the same blockstate model:

| IDs | Shared model |
| --- | --- |
| `arcane_pedestal` / `pedestal_arcane` | `thaumcraft:block/pedestal_normal` |
| `ancient_pedestal` / `pedestal_ancient` | `thaumcraft:block/pedestal_ancient` |
| `eldritch_pedestal` / `pedestal_eldritch` | `thaumcraft:block/pedestal_eldritch` |

Matching a model reference does not prove matching block entities, interaction semantics, compatibility with old saves or recipes. **No alias removal or registry renaming is authorized by this result.**

## Interpretation and follow-up acceptance gates

1. **Static resource graph:** within the explicitly checked paths, no missing active local JSON model, OBJ/MTL asset, or explicit local PNG target was found. This **does not** certify rendering.
2. **Triage:** classify 52 orphans into intentional legacy compatibility, vestigial unused resources, and assets needed for unported features. Prioritize 17 world-banner IDs; confirm six slab old-save mappings and the duplicated pedestal IDs before proposing code changes.
3. **Modern loader:** compile/load with NeoForge 1.21.1, check resource reload logs for missing models/loader errors, especially legacy `hole.json`, custom OBJ/composite models, overrides and external parents.
4. **Rendered original-vs-port comparison:** capture inventory/held/world renders, GUI/HUD, dynamic BER and entities, particles, texture animations, transforms, light/emissive/transparency and interaction states with stable comparison cases. Original Thaumcraft source/JAR screenshots are needed for fidelity verdicts.
5. **Runtime/client gates:** Gradle build, dedicated-server boot, client resource reload, representative world interactions, saves from old versions where supported, and visual screenshots. No such tests were executed during this checkpoint.

Source anchors: [TCBlocks.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java); [TCHoleBlock.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/misc/TCHoleBlock.java); [assets tree](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft).

No gameplay files changed; no deletion, CI change, branch merge or production parity claim.
