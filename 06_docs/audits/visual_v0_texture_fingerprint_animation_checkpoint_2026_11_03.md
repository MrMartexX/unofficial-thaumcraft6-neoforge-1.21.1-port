# V0 texture content, animation metadata and migrated aliases (2026-11-03)

> **Pinned static source check.** Original TC6 1.12.2 decompiled resource tree and NeoForge 1.21.1 port, both within `main` commit `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`. No original JAR client screenshots or modern client were run; no proof of visual or UV parity is implied. Extends [V0 resource graph](visual_v0_resource_graph_first_pass_2026_11_03.md) with **byte-content fingerprints**, metadata JSON parsing and sprite-sheet bounds instead of mere path presence.

## Source provenance

- Legacy `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/resources/assets/thaumcraft/textures/`: Git tree `7468821539f2218a776375a70f75f3d701eda025`.
- Port `05_neoforge_port/src/main/resources/assets/thaumcraft/textures/`: Git tree `1baa3bbd96112d2fb8d64f9f6fe02b63d7e08e6c`.
- Both trees queried through the GitHub Git Trees API with `recursive=1`, `truncated=false`; compared every **blob path + Git blob SHA** (Git blob IDs identify exact file content).
- Each distinct `.mcmeta` blob was fetched and JSON-parsed. All animation-associated PNGs were independently downloaded as base64, and PNG IHDR dimensions read to check frame capacity.
- [Machine-readable checkpoint](generated/visual_v0_texture_fingerprint_animation_checkpoint_2026_11_03.json) records the full per-file lineage, metadata type, source Git hashes and frame checks.

## Results at a glance

| Check | Source evidence | Classification |
| --- | --- | --- |
| Original texture files in checked tree | **855** = 767 PNG + 81 `.mcmeta` + 7 JPG | Bounded complete source tree |
| Exact same relative path in port | **855/855** | No missing checked-in original texture path |
| Exact same relative path **and identical Git blob SHA** | **855/855** | **Byte-for-byte content identity** across all checked-in original textures |
| Port texture files | **1,390** = 1,259 PNG + 124 `.mcmeta` + 7 JPG | Resource inventory |
| Additional port-only files | **535** = 492 PNG + 43 `.mcmeta` | Port additions, not replacements of legacy content |
| Additional files reusing a legacy blob SHA | **498/535** | Byte-identical relocated/copied material |
| Direct `blocks/→block/` or `items/→item/` matching aliases | **451** | All 451 byte-identical to original renamed-path counterpart; **0 conflicts** |
| Other additional byte-identical source reuses | **47** | Includes renamed item textures, Caster skins and armor-layer copies |
| Additional files with no blob SHA anywhere in checked-in original tree | **37** (**32** distinct blob SHA) | New pixel/format content requiring scoped review |
| Metadata JSON parsing | **124 paths / 24 distinct blobs** | **0 JSON parse failures** |
| `animation` metadata | **74 paths**, representing 37 sprite-sheet image contents | **0 missing PNG**, **0 nonintegral frame slices**, **0 out-of-range explicit frame indices** |
| `ctm` metadata | **10 paths**, five legacy CTM families duplicated under `block/` | Metadata and target texture present, **runtime CTM consumer unproven** |
| `texture` metadata | **40 paths** (`blur:true`) | Three historical `.mcmeta` have no same-name PNG in either tree |

### T-001: original bitmap content is not the source of mass visual regressions

The **855 equal Git blob SHAs** include all original 767 PNG images, 81 metadata files and seven JPG backgrounds. This is stronger than the earlier “path exists” gate and rules out silent *in-place bytes changing* as the explanation for widespread texture mismatches in the checked-in baseline.

It **does not** certify selection of the right material, model parent, UV mapping, tint, shaders, connected textures, animation dispatch, transparency or resource reload. In particular, existing V0 F11/F61 (Hungry Chest), F52 (Scribing Tools transforms), F53 (Caster byte-identical skin), F65 (Flux Rift rendering) and F66 (Cultist Portal horizontal sprite UVs) remain logically independent.

### T-002: classification of the 535 additions

- **451** new `textures/block/*` or `textures/item/*` paths exactly match `textures/blocks/*` or `textures/items/*` by both expected basename and Git blob SHA. No paired `block/blocks` or `item/items` same-name SHA conflicts found.
- **47** other new paths reuse existing original blobs, including alternate names (e.g. `item/brass_ingot.png` from original `items/ingot_brass.png`, `item/thaumometer.png` from `items/scanner.png`), migrated Caster skins and modern `models/armor/*_layer_1/2.png` material copied from old `entity/armor/*` sources. Reused pixels do **not** certify body-layer UV layout correctness.
- **37** added files have content not byte-identical to any original tree blob: 7 block crystal sprites, 7 item crystal sprites, 6 deepslate-ore paths (three base contents duplicated into both block namespaces), 7 research-background PNGs, 9 mob-effect icons, 1 wispy-mote particle. These are **not 37 proven errors**. See the JSON `port_only` ledger.

### T-003: animation metadata and sprite bounds

The port has **124 `.mcmeta` files** but they are not all animations. Classification, checked by parsing their **24 distinct JSON blobs**:

| Top-level metadata key | Port paths | Result |
| --- | ---: | --- |
| `animation` | **74** | 37 distinct PNG sprite content hashes, no missing images, valid PNG headers, integral frame slicing, all explicit frame IDs within the computed frame count |
| `ctm` | **10** | Five CTM definitions preserved in two texture directory aliases |
| `texture` | **40** | Blur metadata; three refer to PNGs absent already in the original tree |

The animation example `animatedglow.png` is **16×320**, has 20 vertical frames and a valid 38-entry forward/reverse playback list (largest referenced frame index 19). `lamp_on.png` is **16×64** with four available frames and valid sequence. Other animated sheets vary in number of frames.

**Limit:** passing sprite-sheet dimensions/indices does not prove Minecraft's modern atlas registers/plays the animations, especially when used via entity custom rendering, external shader or changed texture paths. Also, metadata values were not tested with an actual runtime resource reload.

### T-004: connected-texture metadata is a separate client gate

Five legacy CTM families (ten paths including aliases): `ancient_glyph`, `ancient_tile`, `paving_stone_barrier`, `paving_stone_travel`, `plank_silverwood`. Each `.png.mcmeta` declares:

```json
{"ctm": {"ctm_version": 1, "type": "CTM", "textures": ["thaumcraft:blocks/<name>_ctm"], "extra": {"ignore_states": "false"}}}
```

All five `thaumcraft:blocks/*_ctm.png` target paths **exist** in the original and port; their modern `block/*` aliases also exist. However, the inspected `05_neoforge_port/build.gradle` dependencies reference only optional EMI/JEI/Curios APIs and declare **no explicit CTM library**. A code search for `ConnectedTexture` found no modern Java consumer; only historical changelogs.

**Status: `UNPROVEN_RUNTIME_CTM`, not a confirmed missing visual.** Old `ctm` metadata is not evidence that the current client recognizes those specific rules. Test paired blocks in vanilla NeoForge client, then explicitly in a compatible CTM-enabled client if support is intended. Record rendered seam/edge joins and texture atlas behavior. If CTM support is required for original parity, decide explicitly whether to implement a supported loader, optional integration or fallback model.

### T-005: metadata-only historical orphans

The following `.mcmeta` paths lack an identically named PNG in both legacy and modern texture trees:

- `misc/nodes.png.mcmeta`
- `models/ripple.png.mcmeta`
- `models/scanscreen.png.mcmeta`

All three are `{"texture":{"blur":true}}`, **not** animation declarations. This is inherited resource state, not a newly introduced PNG regression. Triage actual renderer references before considering cleanup.

### T-006: seven used research backgrounds have changed format

Original `ConfigResearch.java` [L138–144](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/config/ConfigResearch.java#L138-L144) points at seven `textures/gui/gui_research_back_N.jpg`; modern `TCResearchData.java` [L123–137](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/research/TCResearchData.java#L123-L137) points at `gui_research_back_N.png`, choosing distinct N for each research category.

For **all N=1..7**, both the original JPEG and the new PNG have **1024×1024** dimensions (JPEG SOF and PNG IHDR inspected). The seven PNG blobs are necessarily byte-different from the JPG originals and are among the 37 newly introduced file contents. **Pixel color, compression artifacts, crop and tiling fidelity are not yet verified.** Capture each of the seven research pages using matched scroll and zoom positions. The old JPG files remain in the port unchanged; image-format conversion is not by itself a defect.

## Proposed batch gates for a single later client run

1. **Resource-load gate:** run a clean NeoForge client and `F3+T`, capture missing sprite/model and failed `.mcmeta` or CTM warnings with the mod list and resource-pack order.
2. **Animation gate:** film representative lamp, animated glow, overlay and seal-item sprites for at least 10 seconds; check speed/frame direction and item vs block atlas.
3. **CTM gate:** place adjacent same-type ancient tiles/glyphs, travel/barrier paving and silverwood planks, compare seams in stock/CTM-enabled clients; do not assume bundled support.
4. **Research gate:** capture all seven Thaumonomicon category backgrounds side-by-side with the original JAR at matching pan/zoom.
5. **UV/armor gate:** inspect first/third person, GUI and worn armor layers (especially copied modern `models/armor/*`) and Portal sprite strips, separating texture content from mesh/UV/renderer defects.

All five gates remain **`NOT_RUN`**. No build, PowerShell extractor, original client or NeoForge client was executed in this cycle; no code/assets were changed. PR #11 must remain draft.
