# Thaumcraft 6 Aspects Design

Last reviewed branch: `main`
Last reviewed base commit: `70ec2f06ff06d53f7119f7db9adb83b792368874`
Target module: `05_neoforge_port`
Design status: first exact core/API slice implemented; gameplay integrations are still deferred

## Scope

This document defines the first NeoForge 1.21.1 design target for Thaumcraft aspects. It intentionally stops before aura, research, essentia transport, scanning UI, and gameplay effects.

Guide rule applied: aspects are a high-risk shared data layer. Preserve legacy gameplay semantics and public-facing ids first, then modernize loading/storage only when the exact behavior is known.

Current implementation note: the first code pass preserves the legacy `Aspect`, `AspectList`, and pure `AspectHelper` semantics closely because other Thaumcraft systems depend on those exact rules. This is not a license to approximate later systems. The reload-safe service/data-loader layer still needs a separate exact implementation pass.

## Legacy References

| Area | Legacy classes | What they do | Porting concern |
|---|---|---|---|
| Aspect definition | `thaumcraft.api.aspects.Aspect` | Defines static primal/compound aspects, colors, icons, components, chat color, blend mode, global `aspects` map, and combination lookup. | Static mutable global registry and constructor-side registration are unsafe for reloadable 1.21 data. |
| Aspect amounts | `thaumcraft.api.aspects.AspectList` | Mutable map of `Aspect` to amount, add/merge/remove/reduce helpers, NBT read/write. | Should become id-based and serialization-safe; do not store object references as persistent keys. |
| Public registration event | `thaumcraft.api.aspects.AspectRegistryEvent` and `AspectEventProxy` | Addons register item/block/entity aspects via event proxy. | Old Forge event timing and `OreDictionary` inputs need redesign. |
| Lookup helpers | `AspectHelper` | Resolves object/entity aspects, culls lists, reduces to primals, gets combinations. | Split pure algorithms from registry/service lookups. |
| Public container/source contracts | `IAspectContainer`, `IAspectSource`, `AspectSourceHelper`, `IEssentiaContainerItem`, `IEssentiaTransport` | Aspect storage and essentia transfer contracts. | These belong to later essentia/block-entity design, not the first aspect data pass. |
| Implementation registration | `thaumcraft.common.config.ConfigAspects` | Registers vanilla, Thaumcraft, ore dictionary, item, block, fluid, potion, and entity aspect assignments. Posts `AspectRegistryEvent`. | Use as reference data, not as direct code. Convert assignments into data files/tags/predicates in stages. |
| Internal storage | `thaumcraft.api.internal.CommonInternals.objectTags` and `scanEntities` | Stores item stack hash to `AspectList` and entity tag records. | Hashing serialized legacy `ItemStack` is not stable enough for a modern reloadable data layer. |
| API entry points | `ThaumcraftApi.registerObjectTag`, `registerComplexObjectTag`, `registerEntityTag`, `exists` | Public/deprecated addon-facing aspect assignment hooks. | Preserve the compatibility concept later, but first build an internal service with stable ids. |

## Modern Target

| Target piece | Design decision | Notes |
|---|---|---|
| Aspect id | Use stable `ResourceLocation` ids, e.g. `thaumcraft:aer`. | Keep legacy short names as path ids where practical. |
| Aspect definition object | Immutable value object: id, color, optional component ids, icon, optional display/sort metadata. | No constructor-side registration. |
| Aspect stack/list | Immutable or copy-on-write amount collection keyed by aspect id. | Provide add/merge helpers that return new values or controlled mutable builders. |
| Aspect definitions | Data-driven baseline with bundled Thaumcraft defaults. | Can start from generated JSON matching legacy defaults in `Aspect.java`. |
| Assignment model | Data-driven item/block/entity assignments loaded on resource reload/server reload where practical. | Item ids and item tags are implemented in `aspect_assignments`; vanilla entity targets are currently a small legacy Java table because entity predicates need runtime type/variant checks. A datapack entity format is a later cleanup. |
| Tag matching | Prefer item/block tags for common groups and exact ids for Thaumcraft-specific entries. | Replaces legacy `OreDictionary` strings like `oreAmber`, `gemAmber`, and `ingotCopper`. |
| Entity matching | Use entity type ids plus optional predicate data for variants. | Legacy NBT checks can become explicit predicate definitions later. |
| Reload safety | A server-owned aspect service rebuilds indexes from loaded data and exposes read-only lookup views. | Client receives only the data it needs for UI/rendering when those systems exist. |
| Public API | Defer addon-facing API until the internal data model is stable. | Avoid promising legacy `AspectRegistryEvent` behavior too early. |

## Implemented First Slice

| Piece | New classes | Legacy parity status | Notes |
|---|---|---|---|
| Aspect definitions | `thaumcraft.api.aspects.Aspect` | Implemented for the 37 Thaumcraft 6 BETA26 aspects in legacy order with matching ids, colors, components, chat colors, blend modes, image paths, primal/compound list behavior, and combination hash side table. | Scanning side effects from the old constructor are not wired because scanning/research is not ported. |
| Aspect amount lists | `thaumcraft.api.aspects.AspectList` | Implemented with legacy mutable `LinkedHashMap<Aspect, Integer>` semantics, add/merge/remove/reduce behavior, name/amount sorting, vis size, copy, and modern `CompoundTag`/`ListTag` NBT read/write using legacy keys. | Unknown aspect ids still add a `null` key, matching the old behavior rather than hiding bad data. |
| Pure helper logic | `thaumcraft.api.aspects.AspectHelper` | Implemented for `cullTags`, `getCombinationResult`, `getRandomPrimal`, `reduceToPrimals`, `getPrimalAspects`, `getAuraAspects`, and cached `generateTags` lookup. | `generateTags` returns generated cache entries only; it does not scan recipes on lookup. |
| Entity helper | `AspectHelper#getEntityAspects` | Player branch implemented using the legacy deterministic name-seeded random aspect logic. Vanilla non-player entity assignments are implemented for exact 1.12 rows plus documented post-1.12 policy rows. | Thaumcraft custom entities and NBT-heavy Wisp/Pech-style rows remain deferred until those entities exist. |
| Direct object assignments | `thaumcraft.common.aspects.TCAspectAssignments` | Loads exact Thaumcraft assignments, legacy vanilla seeds, modern exact audit entries, and audited 1.21-only manual vanilla entries from data. | Current assignable vanilla item-id coverage is complete after server reload validation; stack component variants are handled by `TCAspectStackRules`. |
| Bootstrap validation | `thaumcraft.Thaumcraft`, `TCAspectParityValidator` | Loads the aspect registry at mod construction and validates exact legacy parity for aspect definitions, list behavior, helper algorithms, and direct assignments. | Client and dedicated server smoke checks reached startup with `parity validation passed` and no Thaumcraft warning/error lines. |
| OreDictionary/generateTags audit and fallback | `06_docs/data/aspects/aspect_assignment_tag_audit.md`, `06_docs/data/aspects/aspect_generate_tags_audit.md`, `TCAspectAssignments`, `TCAspectReloadValidator` | Audits current legacy `OreDictionaryEntries`, exact `generateTags` recipe-derived behavior, and `ConfigAspects` string keys; adds exact legacy compatibility tags plus tag-backed fallback only where the legacy source had a string-key aspect assignment. | Base lookup priority is `exact id > tag > generated`; stack-sensitive exclusions/specials/bonuses wrap that base lookup. |
| Reload-safe assignment data | `06_docs/data/aspects/aspect_assignment_data_format.md`, `data/thaumcraft/aspect_assignments/*.json`, `TCAspectAssignmentParser`, `TCAspectAssignmentReloadListener` | Current 676 exact, 46 audited tag, and 32 complex exact assignments are loaded from data resources at bootstrap and server reload, then parity-validated. | Format is internal and strict. It covers item ids, item tags, complex extras, manual 1.21 vanilla coverage, dump-derived runtime parity overrides for legacy-equivalent vanilla stacks, and current registered Thaumcraft runtime parity values including table/research-table ids; no public addon API yet. |
| Generated recipe cache | `06_docs/data/aspects/aspect_generated_cache_design.md`, `TCAspectStackKey`, `TCGeneratedAspectCache`, `TCGeneratedAspectRecipeGenerator` | Adds count-insensitive stack keys based on item id plus data component patch, clears generated cache on assignment reload, rebuilds it after server data/tag reload from `RecipeType.CRAFTING`, and validates `exact > tag > generated` priority. | Latest reload produced 476 generated `minecraft:*`/`thaumcraft:*` standard crafting entries after exact runtime parity overrides took ownership of legacy-equivalent flattened vanilla ids and current registered Thaumcraft overrides. Custom Thaumcraft recipe types remain deferred. |
| Stack-sensitive lookup | `TCAspectStackRules` | Implements legacy no-aspect spawn eggs, `POTION_CONTENTS` potion carrier rules, equipment/tool/dye bonuses, and enchantment/stored-enchantment bonuses. | Essentia container items are deferred until essentia systems exist. |
| Read-only inventory tooltip | `TCAspectTooltipEvents`, `AspectTooltipComponent`, `ClientAspectTooltipComponent`, `TCClientTooltipComponents` | Adds the legacy-default Shift behavior in container/inventory tooltips: when Shift is held, resolved object aspects are rendered as one sorted row of tinted 16x16 aspect icons with amount text. | This is a client-only display consumer. It does not change aspect assignment logic, scanning state, research progression, or gameplay effects. |

## Non-Goals For This Step

The following systems are explicitly not implemented as part of this design-only pass:

- Aura storage, chunk aura, flux, rifts, node-like behavior, or aura ticking.
- Research data, scanning progression, knowledge storage, warp, or thaumonomicon UI.
- Essentia containers, jars, tubes, transport, suction, or BlockEntity capabilities.
- Arcane crafting, crucible recipes, infusion recipes, or recipe aspect costs.
- Full scanning UI, goggles overlays, Thaumometer FX/highlight/aura behavior, permanent scan reward mutation, or client sync payloads. The implemented Shift tooltip is read-only and does not count as scanning UI.
- Gameplay effects from aspect values.

## Proposed Data Shapes

Aspect definitions:

```json
{
  "id": "thaumcraft:aer",
  "color": "ffff7e",
  "components": [],
  "icon": "thaumcraft:textures/aspects/aer.png",
  "primal": true
}
```

Compound aspect definition:

```json
{
  "id": "thaumcraft:vacuos",
  "color": "888888",
  "components": ["thaumcraft:aer", "thaumcraft:perditio"],
  "icon": "thaumcraft:textures/aspects/vacuos.png"
}
```

Item/block assignment:

```json
{
  "targets": [
    { "item": "thaumcraft:amber" },
    { "tag": "c:gems/amber" }
  ],
  "aspects": {
    "thaumcraft:vinculum": 10,
    "thaumcraft:vitreus": 10
  }
}
```

Entity assignment:

```json
{
  "entity_type": "minecraft:zombie",
  "aspects": {
    "thaumcraft:exanimis": 20,
    "thaumcraft:humanus": 10,
    "thaumcraft:terra": 5
  }
}
```

These shapes are initial design sketches. File locations, codecs, and validation errors should be finalized in the implementation pass.

## Legacy Data To Preserve First

| Legacy source | Keep | Redesign | Defer |
|---|---|---|---|
| Default aspect ids, colors, components, and icons from `Aspect` | Yes | Store as immutable data instead of static mutable objects | No |
| `AspectList` add/merge/reduce semantics | Partly | Use id-keyed value lists/builders and modern codecs | NBT compatibility until persistence exists |
| `ConfigAspects` Thaumcraft item/block assignments | Yes, as reference data | Convert to JSON and tag-based targets | Full vanilla/modded assignment catalog can be staged |
| `ConfigAspects` entity assignments | Yes, as reference data | Use entity type ids and predicates | Variant/NBT predicates can come later |
| `AspectRegistryEvent` addon API | Conceptually | Replace with modern datapack/API extension points later | Public compatibility API can wait |
| Essentia interfaces | No for this pass | Needs capability/service design | Defer to essentia transport stage |

## Implementation Checklist For Next Stage

1. Keep `TCAspectParityValidator` passing when aspect logic changes.
2. Keep tag-backed and manual aspect lookup limited to audited keys with priority `exact id > tag > generated`.
3. Keep `TCAspectReloadValidator` passing after server data load.
4. Keep generated cache rebuild owned by server data/tag reload; do not scan recipes from item lookup calls.
5. Keep current vanilla item-id coverage at `0 missing` after every data or generator change.
6. Keep component-aware stack handling covered by validation before relying on potion, enchantment, stored-content, or damaged-gear aspects in gameplay.
7. Extend direct assignments only when the corresponding item/block ids are actually registered, and add them through `data/thaumcraft/aspect_assignments`.
8. Do not connect aspects to aura, research, scanning, essentia transport, crafting costs, or gameplay UI until those systems have their own design notes. The inventory Shift tooltip may remain as a read-only aspect inspection surface.
9. Re-run `.\gradlew.bat build --no-daemon`, `runClient`, and `runServer` after each integration step.
