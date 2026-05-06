# Thaumcraft 6 Aspects Design

Last reviewed branch: `main`
Last reviewed base commit: `70ec2f06ff06d53f7119f7db9adb83b792368874`
Target module: `05_neoforge_port`
Design status: design only, no implementation in this pass

## Scope

This document defines the first NeoForge 1.21.1 design target for Thaumcraft aspects. It intentionally stops before aura, research, essentia transport, scanning UI, and gameplay effects.

Guide rule applied: aspects are a high-risk shared data layer. Do not copy the legacy mutable static API directly. Preserve public-facing concepts and ids, then rebuild storage, loading, and lookup around modern registries/data reload.

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
| Assignment model | Data-driven item/block/entity assignments loaded on resource reload/server reload. | Use explicit item ids, block ids, tags, and entity type ids instead of serialized stack hashes. |
| Tag matching | Prefer item/block tags for common groups and exact ids for Thaumcraft-specific entries. | Replaces legacy `OreDictionary` strings like `oreAmber`, `gemAmber`, and `ingotCopper`. |
| Entity matching | Use entity type ids plus optional predicate data for variants. | Legacy NBT checks can become explicit predicate definitions later. |
| Reload safety | A server-owned aspect service rebuilds indexes from loaded data and exposes read-only lookup views. | Client receives only the data it needs for UI/rendering when those systems exist. |
| Public API | Defer addon-facing API until the internal data model is stable. | Avoid promising legacy `AspectRegistryEvent` behavior too early. |

## Non-Goals For This Step

The following systems are explicitly not implemented as part of this design-only pass:

- Aura storage, chunk aura, flux, rifts, node-like behavior, or aura ticking.
- Research data, scanning progression, knowledge storage, warp, or thaumonomicon UI.
- Essentia containers, jars, tubes, transport, suction, or BlockEntity capabilities.
- Arcane crafting, crucible recipes, infusion recipes, or recipe aspect costs.
- Scanning UI, goggles overlays, thaumometer behavior, or client sync payloads.
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

1. Create internal aspect id/value classes without public API commitments.
2. Add bundled default aspect definitions matching legacy ids, colors, components, and icons.
3. Add codecs and validation for aspect definitions and aspect amount maps.
4. Add a reload-safe aspect service that exposes read-only lookup by aspect id and combination pair.
5. Add a small data-driven assignment loader for exact item/block ids and tags.
6. Seed only currently registered Thaumcraft ids first: `amber`, `quicksilver`, crystals, logs/leaves/saplings/plants, and simple stones/ores.
7. Add tests or a validation command for duplicate ids, missing component ids, invalid amounts, and missing assignment targets.
8. Build and run client/server before connecting the service to gameplay.
