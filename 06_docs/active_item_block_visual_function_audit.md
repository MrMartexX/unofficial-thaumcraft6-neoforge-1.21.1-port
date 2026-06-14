# Active Item / Block Visual and Function Audit

Date: 2026-06-14
Branch: `codex/experiment-shobie-1-20-merge`

This audit covers active registered `TCItems` and `TCBlocks` only. Imported legacy
and Shobie reference resources are not treated as runtime bugs unless an active
registered id points at them.

## Checks performed

- Compared active item model parents against legacy 1.12.2 assets and the
  Shobie 1.20.1 reference tree.
- Checked every active `TCItems` id has a modern item model.
- Checked every active `TCBlocks` id has a blockstate file.
- Checked active model texture references use modern `textures/item` or
  `textures/block` paths, not legacy `textures/items` or `textures/blocks`.
- Checked model parent and blockstate model references for missing active
  resources.
- Smoke-tested build, client startup, and dedicated server startup logs for
  missing model, missing texture, OBJ/MTL, and registry failures.

## Fixes applied

| Area | Legacy reference | Port fix | Notes |
|---|---|---|---|
| `scribing_tools` item model | `models/item/scribing_tools.json` uses `item/handheld` | Changed active model parent from `minecraft:item/generated` to `minecraft:item/handheld` | Confirmed against both legacy and Shobie. |
| Thaumium tool tier | `ThaumcraftMaterials.TOOLMAT_THAUMIUM`: level 3, 500 uses, 7 speed, 2.5 damage, 22 enchant | Updated `TCToolTiers.THAUMIUM` from 400/2.0 to 500/2.5 | Direct parity correction. |
| Void tools | Legacy `ItemVoid*` tool classes use `TOOLMAT_VOID`, self-repair and Weakness on hit | Added real 1.21 void axe/hoe/pick/shovel/sword item classes with durability, attributes, self-repair, and Weakness | Full warp integration remains blocked until warp effects/events are ported. |
| Elemental tools | Legacy `ItemElemental*` classes use `TOOLMAT_ELEMENTAL` and are rare tools | Registered elemental axe/hoe/pick/shovel/sword as real 1.21 tool items with legacy tier values and rare rarity | Special right-click/infusion-enchantment behavior is deferred by tool family. |
| `primal_crusher` | Legacy hybrid pickaxe/shovel `ItemTool`, PRIMALVOID tier, self-repair, Weakness | Added a real 1.21 `DiggerItem` with pickaxe+shovel destroy speed/drop behavior, PRIMALVOID tier, self-repair, and Weakness | Destructive/refining infusion-enchantment behavior remains deferred. |

## Findings intentionally not changed

| Area | Finding | Decision |
|---|---|---|
| Shimmerleaf, cinderpearl, vishroom item models | Shobie uses block-model parents, but legacy inventory variants used `forge:item-layer` flat plant textures | Kept current `minecraft:item/generated` models as closer to legacy 1.12.2. |
| Nitor block models | Shobie uses cube-like block parents for several nitor colors | Kept current invisible placed block. Legacy `BlockNitor#getRenderType` is `INVISIBLE`, with a small bounding box and tile-entity FX. |
| Nitor inventory model | Legacy mapped all nitor colors to common `thaumcraft:nitor` inventory model with layered `nitor` and `nitor_core` textures | Kept current layered item models for active `nitor_*` ids. |
| `condenser_lattice` item model | Current and legacy both parent to `condenser_lattice_core`; Shobie differs | Kept current model because it matches legacy. |
| OBJ blockstate "missing" reports | Scanner, pillar and thaumatorium legacy blockstates reference OBJ names that are not JSON model parents | Treated as OBJ/reference-only or active OBJ loader cases, not JSON missing-model errors. |
| Armor placeholders | Armor can be made equipable with 1.21 `ArmorItem`, but legacy textures are under `textures/entity/armor/*_1/*_2`, while vanilla 1.21 material layers expect the modern armor texture pipeline | Deferred until an armor material/render texture mapping pass, to avoid creating equipable armor with wrong/missing visuals. |

## Validation

- `.\gradlew.bat build --no-daemon`: passed.
- Short `runClient` smoke: reached resource reload/model atlas creation; no matching ERROR/FATAL/missing-model/missing-texture/OBJ failure lines.
- Short `runServer` smoke: reached `Done`; loaded 1572 recipes; aspect assignment, research data, research references, page catalog and aspect tag validation passed.
- Server dynamic scan predicates registered; current active predicates: 205.

Known non-blocking runtime warnings:

- Vanilla/NeoForge command ambiguity warnings.
- Vanilla goat horn missing sound warnings in client smoke.
- NeoForge dev warning about legacy `forge` tags. No active `data/forge/tags`
  directory is present in this port; the remaining `forge:` text hit is a legacy
  research page reference (`forge:bucketfilled`) and should be resolved in the
  research recipe/page catalog work, not by blind tag rewriting.

## Next item/block work

1. Port armor as equipable items only after defining the 1.21 armor material and
   texture-layer mapping for legacy `textures/entity/armor` assets.
2. Port individual elemental tool special behaviors by family, with one parity
   fixture per behavior.
3. Keep focus/caster/curio/bauble behavior as explicit subsystem work; these are
   not simple item fixes.
4. Keep Shobie item classes as reference-only unless a class is reimplemented
   against NeoForge 1.21.1 APIs and validated against legacy behavior.
