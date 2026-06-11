# Scannable data format

Status: active for the research/knowledge/scanning branch.

The modern port uses reloadable JSON files under `data/thaumcraft/scannables/*.json` for legacy scan predicate definitions. This replaces direct hardcoded calls in legacy `ConfigResearch.initScannables` where possible.

## Root format

```json
{
  "replace": false,
  "scannables": []
}
```

`replace` clears definitions loaded from earlier files. Normal bundled files should keep it `false`.

## Entry types

| Type | Fields | Legacy equivalent | Notes |
|---|---|---|---|
| `item` | `research`, `items` | `ScanItem` | Exact item id match. |
| `block` | `research`, `blocks` | `ScanBlock` | Also registers item-form scans like legacy `ScanBlock` constructor. |
| `entity` | `research`, `entities` | `ScanEntity` | Exact `EntityType` id match. Class inheritance/NBT matching is deferred. |
| `ore_dictionary` | `research`, `entries` | `ScanOreDictionary` | Maps legacy names to `c:*` tags and `thaumcraft:legacy_ore_dictionary/*` tags. |
| `tag` | `research`, `item_tags`, `block_tags` | Modern replacement for material/tag scan cases | Used for entries like clay/terracotta that were legacy material checks. |

Dynamic predicates are not declared in JSON:

- mob effects are registered from `Registries.MOB_EFFECT`, replacing legacy `ScanPotion`;
- enchantments are registered from `Registries.ENCHANTMENT`, replacing legacy `ScanEnchantment`;
- sky scanning is registered as a special predicate and stays gated by `CELESTIALSCANNING`.

## Current bundled data

`data/thaumcraft/scannables/legacy_core.json` covers only entries whose target ids or tags exist now:

- current Thaumcraft ore/crystal/plant/ancient block scans;
- vanilla dragon breath, totem of undying, ender pearl, dispenser, clay, arrows and common mob/projectile scans;
- legacy OreDictionary material scans for iron, brass, thaumium and void via modern tags;
- tag-based clay/terracotta scan bridge.

The server currently reports `32` data-driven definitions and `77` active predicates after JSON reload. Active predicates are higher because legacy `ScanBlock` also registers item-form `ScanItem` predicates.

After server dynamic predicates are registered, the active predicate count is currently `159`. The additional predicates come from modern mob effects, modern enchantments and the special sky scan predicate.

## Deferred legacy entries

Do not add fake ids for content that is not registered yet. These remain deferred until their subsystem exists:

- Thaumcraft mobs and bosses: wisps, pech, eldritch crab, cultists, taint entities, flux rifts, golems;
- missing Thaumcraft items: primordial pearl, pech wand, void seed, brain, celestial notes and scribing tools;
- `ScanSky` celestial-note item creation side effects;
- entity NBT predicate matching;
- scan success rewards and recursive research progression.

## Validation

Required checks after changing scannable data:

```powershell
cd D:\Thaumcraft_6_port_to_1.21.1\05_neoforge_port
.\gradlew.bat build --no-daemon
.\gradlew.bat runServer --no-daemon
```

The server log must include `Thaumcraft scannables reloaded` and reach `Done`.

For item/potion/enchantment scan coverage, run the read-only audit command documented in `scanning_parity_validation.md`:

```text
/tc scan audit_items
```
