# Post-1.12 vanilla scanning policy

Status: active policy for the research/knowledge/scanning branch.

## Purpose

Minecraft 1.13 through 1.21.1 added many vanilla items, blocks, effects and enchantments that did not exist in Thaumcraft 6's 1.12.2 target. The port must not invent research progression randomly for those entries.

This document separates aspect discovery from research unlocks:

- Aspect values for modern-only vanilla stacks are handled by the aspect assignment system and documented in `vanilla_post_1_12_aspect_rationale.md`.
- Scanning an aspect-bearing stack may be valid through generic scanning.
- Separate scannable research keys require explicit design, matching either a legacy family or a future research entry.

## Current rule

| Modern content | Scanning behavior | Reason |
|---|---|---|
| New item/block with non-empty aspects | Generic scan can match it and produce a fake `!namespace:id` key | Mirrors legacy `ScanGeneric` for aspect-bearing objects. |
| New item/block with deliberate no aspects | Not scannable through generic scan | Matches exclusions such as spawn eggs, fireworks and infested blocks. |
| New potion/effect | Dynamic mob-effect scan predicate can match potion stacks or affected entities | Legacy `ScanPotion` registered every old `Potion` effect; modern equivalent is `MobEffect`. |
| New enchantment | Dynamic enchantment scan predicate can match enchanted items/books | Legacy `ScanEnchantment` registered every enchantment. |
| New entity with entity aspects not yet implemented | Not generic-scannable unless a data scannable exists | Entity aspect assignments are not ported yet except player handling. |
| New entity with clear legacy family role | Add explicit data scannable only when the target entity id exists and the research key is documented | Prevents false parity. |

## Aspect source

New vanilla item aspects are not guessed during scanning. The scan service asks `AspectHelper.getObjectAspects` and `AspectHelper.generateTags`, which means values come from:

- exact legacy mappings;
- tag/OreDictionary bridges;
- generated crafting cache;
- stack-sensitive potion/enchantment/damage rules;
- documented manual 1.21-only assignments.

The current aspect audit documents `848` modern-only stack rows and `818` unique vanilla item ids. Runtime validation reports `1230/1230` vanilla item ids with non-empty aspects, excluding deliberate no-aspect policies.

## Research key policy

Do not add a new permanent research unlock just because a 1.21 item exists.

Allowed key sources:

- legacy keys copied from `ConfigResearch.initScannables`, for example `f_TELEPORT`, `f_MATIRON`, `!ORECRYSTAL`;
- generic fake keys generated from stable ids, for example `!minecraft:sculk_sensor`;
- future research data entries once the research category/entry loader exists.

Deferred:

- mapping every modern item family to bespoke Thaumcraft research;
- granting observation/theory rewards from generic scan;
- treating modern-only fake keys as visible Thaumonomicon content before the research data model exists.

## Consequences for current implementation

The current port should do this:

1. Keep all modern vanilla aspect values in aspect JSON/policy docs.
2. Let generic scan cover new aspect-bearing items and blocks.
3. Let dynamic effect/enchantment scans cover all registered modern mob effects and enchantments.
4. Add explicit scannable JSON only for legacy families or clearly documented future research gates.
5. Keep no-aspect exclusions unscannable unless a later design intentionally changes that behavior.
