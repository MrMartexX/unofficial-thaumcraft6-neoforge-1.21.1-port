# V0 source checkpoint: model geometry, UV contracts, OBJ/MTL, armor

**Date:** 2026-11-04. **Pinned source:** `main` at `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`. **Status:** static evidence only; client render/sign-off pending. This report extends PR #11 without changing gameplay or model files.

## Method and complete inventory

- Compared complete, non-truncated Git trees of legacy `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/resources/assets/thaumcraft/models/` and active `05_neoforge_port/src/main/resources/assets/thaumcraft/models/` at the pinned SHA.
- Legacy: **307** model blobs (**119** block JSON, **155** item JSON, **27** OBJ, **6** MTL). Modern: **827** model blobs; all **307** legacy model relative filenames retained.
- Parsed and recursively compared **274/274** legacy JSON models with their modern same-name counterparts. Only predictable namespace/path modernization (`thaumcraft:blocks/→thaumcraft:block/`, `thaumcraft:items/→thaumcraft:item/`, vanilla `item/` and `block/` names) was canonicalized.
- **225/274** JSON models are equivalent after those path rewrites: **82/119** blocks, **143/155** items. **49** retain source-level differences: **37** blocks, **12** items. The machine-readable companion stores every compared path, source SHA and field-level residual.
- All **27/27 legacy OBJ meshes are byte-identical** by Git blob SHA. **5/6 MTL files changed**, largely to convert machine-specific texture paths to resource locations; the sixth is unchanged.
- Geometry/UV/display fields were unaffected in all matching JSONs except the old zero-volume element removed from `block/empty.json`; `item/scribing_tools.json` changes its **parent** from `item/handheld` to `minecraft:item/generated`, which changes inherited transforms. This is a JSON-source property statement, not proof of pixel-level parity.

## Confirmed source findings

### MUV-001 (P1): Ancient stairs straight model visibly diverges from corners

- Active blockstate `blockstates/stairs_ancient.json` selects `block/ancient_stairs.json` for straight and `block/ancient_inner_stairs.json` / `block/ancient_outer_stairs.json` for corners.
- Legacy straight `bottom`, `top`, `side` all use `thaumcraft:block/ancient_tile`; modern straight uses `thaumcraft:block/ancient_stone_1`; both modern corner models retain `ancient_tile`.
- These are distinct actual PNG blobs: `ancient_tile.png` SHA `f695b23e...` versus `ancient_stone_1.png` SHA `15493daa...`. This is not a folder-alias difference; place all three shapes adjacent for client confirmation.

### MUV-002 (P1): Arcane straight stairs lose original top/side textures

- Active `blockstates/stairs_arcane.json` selects `block/arcane_stairs.json` for straight and the inner/outer sibling models for corners.
- Original and current corners use `arcane_stone_1` on bottom, `arcane_stone_2` on top and `arcane_stone_3` on sides. The modern straight model uses `arcane_stone_1` on all faces.
- The three source textures are distinct Git blobs (`42269b90...`, `c33bb30e...`, `8a369167...`). Compare straight, inner and outer stairs from each facing and lighting context.

### MUV-003 (P1): Legacy custom armor geometry/UV is not integrated in inspected modern client code

- Legacy `ModelFortressArmor`, `ModelKnightArmor`, `ModelLeaderArmor`, and `ModelRobe` have **64, 38, 50 and 58** source-level `.addBox(...)` call sites respectively (**210** total) with **128×64** atlas coordinates. These counts are call sites, not guaranteed runtime-visible cuboids.
- Legacy armor item hooks directly instantiate the specialized models: `ItemFortressArmor→ModelFortressArmor`, `ItemCultistPlateArmor→ModelKnightArmor`, `ItemCultistLeaderArmor→ModelLeaderArmor`, and `ItemCultistRobeArmor`/`ItemVoidRobeArmor→ModelRobe` with separate 1.0/0.5 scale variants.
- Modern `ItemFortressArmor` and `ItemCultistRobeArmor` extend `ArmorItem` and do not attach corresponding humanoid armor models; `TCArmorMaterials` registers texture layers. The inspected `TCClientExtensions` registers fluids only; repository search found no modern `getHumanoidArmorModel` or those four model implementations.
- Modern `models/armor/*_layer_{1,2}.png` are in several cases *byte-identical aliases of the original custom-geometry atlases*. Texture preservation alone therefore does **not** restore the silhouettes, moving ornaments/masks, or UV mapping on the default humanoid geometry. **A player-client A/B comparison is still required**; the source-level missing custom rig is strong evidence, not a measured screenshot delta.

## Other material/model differences to classify with targeted runtime scenes

| ID | Status | Specific source evidence |
| --- | --- | --- |
| MUV-004 / P2 | Display-transform candidate | `item/scribing_tools.json` switches `item/handheld` parent to `minecraft:item/generated`; compare held item / ground / GUI transforms. |
| MUV-005 / P2 | Asset-substitution candidate | `item/bucket_pure.json` points to `minecraft:item/water_bucket` instead of legacy `thaumcraft:items/bucket_pure`; a dedicated `bucket_pure.png` does **not** occur in the checked-in legacy texture tree, so the old reference may already have been incomplete. |
| MUV-006 / P2 | Reachability unproven | `block/redstone_relay_{on,off}.json` replaces vanilla `stone_slab_top` with `smooth_stone`, and renames vanilla torch textures. `blockstates/redstone_relay.json` still uses legacy `forge_marker`/old model-path syntax; do not claim current in-game reachability. |
| MUV-007 / P2 | Reachability unproven | `block/empty.json` drops a legacy degenerate element and now inherits `minecraft:block/cube_all` with barrier texture. `blockstates/empty.json` also retains old `forge_marker`. Confirm registration/loader routing first. |
| MUV-008 / P2 | MTL numeric exception | `models/obj/crystal.mtl` changes `Material__25` `Ka/Kd` RGB triplets `0.5880` to `1.0000`. Active `block/crystal_aer.json` uses a **per-aspect** `mtl_override` (`crystal_aer.mtl`), so effective impact of generic `crystal.mtl` remains unknown. |

### MTL normalization positive checks

- `models/block/arcane_workbench_charger.mtl`, `golembuilder.mtl`, and `thaumatorium.mtl`: only `map_Ka`/`map_Kd` changed, replacing legacy absolute Windows filesystem paths with modern `thaumcraft:block/...` locations; numeric material coefficients stayed the same.
- `models/item/scanner.mtl`: only `map_Ka`/`map_Kd` redirected to `#body` and `#pane`; active `models/item/thaumometer.json` is a composite OBJ model that provides matching `body`/`pane` texture aliases and separate translucent pane rendering.
- `models/obj/crystal.mtl` is the **one changed numeric MTL**, not safe to mark a visual regression until model loader path and `mtl_override` priority are verified in-game.
- `block/chest.json` texture reference moved from `thaumcraft:models/chesthungry` to `thaumcraft:block/chesthungry`; both PNG files have the same Git blob SHA. `item/caster_*` skin references are similarly direct copies of the original armor PNGs. Predicate namespacing in `caster_basic`, `jar_{normal,void}` and `primordial_pearl` must be checked against the registered item-property providers, not treated as changed artwork.

## Runtime acceptance scenes (one grouped client batch)

1. Place ancient and arcane stairs as straight, inner-left/right, outer-left/right. Match weather/time/light, rotations, inventory icon and adjacent transitions against 1.12.2.
2. Equip Fortress helmet with goggles and each mask type; Cultist Knight, Leader, cultist robe and void robe; compare silhouettes, ornaments, body part animation and the 128×64 texture UV in third-person. Repeat after walking/crouching.
3. Compare scribing tools held and GUI views, pure bucket icon, Thaumometer body/pane and animated screen; keep alpha/render-order checks separate from static material checks.
4. Check per-aspect crystals and the OBJ machines (Arcane Workbench Charger, Golem Builder, Thaumatorium) under identical lighting, including emissive and face culling.
5. Determine whether old `empty` and redstone relay blockstates are reachable in current registered gameplay before drawing visual conclusions.

## Decisions and next work

- **No model/resource corrections** are made inside this audit PR. Open focused implementation PR(s) for validated changes; preserve original textures and geometry, then run a grouped Gradle + client smoke batch and comparison captures.
- MUV-001/002 are active-state source-level substitutions; these can be proposed for minimal, isolated texture fixes after owner acceptance. MUV-003 requires an architecture/design pass for NeoForge model layers or equivalent custom humanoid armor rendering; copying old Forge `ModelRenderer` is not viable.
- Remaining V0 gates: live client A/B screenshots, dynamic armor rig/UV checks, shader/render-layer validation, inherited legacy blockstate reachability, and acceptance review. Green CI and source inventories do not close these gates.

**Verification:** exhaustive SHA/tree and JSON/MTL parsing performed remotely; source files and registrations inspected. No Gradle build, PowerShell execution, GitHub Actions run, game launch or screenshots in this checkpoint. PR #11 stays draft. Relative paths in this report are under the modern `05_neoforge_port/src/main/resources/assets/thaumcraft/` root unless otherwise prefixed.

**Companion dataset:** `06_docs/audits/generated/visual_v0_model_geometry_uv_material_checkpoint_2026_11_04.json` (per-path source hashes and all normalized differences).
