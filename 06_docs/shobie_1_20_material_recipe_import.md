# Shobie 1.20.1 Material Recipe Import

Date: 2026-06-13
Branch: `codex/experiment-shobie-1-20-merge`

## Scope

This pass imports a narrow material/equipment recipe layer from the Shobie
1.20.1 port into the current NeoForge 1.21.1 project. The current port remains
the base. Shobie recipes are treated as reference data and are translated into
the current registry ids, current `data/thaumcraft/recipe` path, modern result
syntax, and current common tag policy.

Migration guide rule applied: port the subsystem role and data intent, not the
old Forge implementation. No Shobie Java gameplay logic, armor behavior, tool
behavior, crucible serializer, or infusion serializer is imported here.

## Added Registry Identities

These ids are now registered so recipe/resource dependencies can resolve:

| Group | Ids | Status |
|---|---|---|
| Materials | `void_metal_ingot`, `thaumium_nugget`, `brass_nugget`, `void_metal_nugget`, `quicksilver_nugget`, `quartz_nugget` | Simple material items with legacy-family metadata where applicable. |
| Components | `candle_white`, `jar_brace`, `golem_bell` | Placeholder component items; behavior is not implemented. |
| Thaumium armor | `thaumium_helm`, `thaumium_chest`, `thaumium_legs`, `thaumium_boots` | Placeholder armor identities; not real armor behavior yet. |
| Void equipment | `void_axe`, `void_hoe`, `void_pick`, `void_shovel`, `void_sword`, `void_helm`, `void_chest`, `void_legs`, `void_boots` | Placeholder equipment identities; not real void tool/armor behavior yet. |

## Added Tags

| Tag | Purpose |
|---|---|
| `c:ingots/void` | Common bridge for void metal ingot recipes. |
| `c:nuggets` | Parent common nugget tag for current imported nuggets. |
| `c:nuggets/brass` | Brass nugget material bridge. |
| `c:nuggets/quartz` | Quartz nugget material bridge. |
| `c:nuggets/quicksilver` | Quicksilver nugget material bridge. |
| `c:nuggets/thaumium` | Thaumium nugget material bridge. |
| `c:nuggets/void` | Void metal nugget material bridge. |

## Added Recipes

This pass adds 32 standard crafting recipes:

| Family | Recipes |
|---|---|
| Nugget conversion | `brass_ingot_to_nuggets`, `brass_nuggets_to_ingot`, `thaumium_ingot_to_nuggets`, `thaumium_nuggets_to_ingot`, `void_ingot_to_nuggets`, `void_nuggets_to_ingot`, `quartz_to_nuggets`, `quartz_nuggets_to_quartz`, `quicksilver_to_nuggets`, `quicksilver_nuggets_to_quicksilver` |
| Blocks/materials | `metal_thaumium`, `thaumium_ingot_from_metal_thaumium`, `metal_void`, `void_metal_ingot_from_metal_void`, `plate_void` |
| Components | `phial_empty`, `jar_brace`, `golem_bell`, `candle_white` |
| Thaumium armor | `thaumium_helm`, `thaumium_chest`, `thaumium_legs`, `thaumium_boots` |
| Void armor | `void_helm`, `void_chest`, `void_legs`, `void_boots` |
| Void tools | `void_axe`, `void_hoe`, `void_pick`, `void_shovel`, `void_sword` |

## Aspect Notes

`void_metal_ingot` is now an explicit registered runtime-parity seed:

| Item | Aspects | Reason |
|---|---|---|
| `thaumcraft:void_metal_ingot` | `metallum 10`, `vacuos 10`, `tenebrae 5` | Avoids a circular recipe-derived graph between `metal_void` and the ingot. This value follows the legacy/Shobie material intent and must later be checked against a focused 1.12 runtime exporter row when the void material family is fully audited. |

The direct assignment validator count is now `688`.

## Validation

| Check | Result | Notes |
|---|---|---|
| `.\gradlew.bat build --no-daemon` | Passed | Java/resources compile after registry and recipe additions. |
| `.\gradlew.bat runServer --no-daemon` | Passed | Dedicated server reached `Done`, loaded `1452` recipes, rebuilt `576` generated object assignments, and reported `1230/1230` current vanilla item ids with aspects. |
| `.\gradlew.bat runClient --no-daemon` | Startup/resource smoke passed | Client reached OpenAL and texture atlas creation. Reviewed smoke log had no missing texture/model, model loading exception, file-not-found, or thrown exception matches for this import. |

## Deferred

- Real thaumium armor behavior.
- Real void tool and armor behavior.
- Full equipment/Curios discount integration.
- Crucible and infusion recipe serializers.
- Any direct Shobie Java machine, item, GUI, or renderer implementation.
