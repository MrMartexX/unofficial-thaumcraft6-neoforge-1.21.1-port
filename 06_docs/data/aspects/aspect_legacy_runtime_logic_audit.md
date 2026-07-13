# Thaumcraft 6 Legacy Aspect Runtime Logic Audit

Last reviewed branch: `main`
Target module: `05_neoforge_port`

## Purpose

This document records the runtime behavior that controls aspects in Thaumcraft 6 for Minecraft 1.12.2. It is the reference for deciding what the NeoForge 1.21.1 port must preserve exactly, what must be emulated through modern systems, and what cannot be trusted without a runtime parity dump.

Guide rule applied: old metadata, `ItemStack` NBT, `OreDictionary`, Forge events, and lookup-time recipe scans must not be copied blindly. Preserve behavior first, then replace the storage and reload mechanisms with NeoForge 1.21.1 equivalents.

## Sources Checked

| Source | Role | Notes |
|---|---|---|
| `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/api/aspects/*` | Public aspect API reference | Defines `Aspect`, `AspectList`, `AspectHelper`, public interfaces, and registry event proxy. |
| `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/api/ThaumcraftApi.java` | Public registration API | Contains deprecated object/entity registration entry points still used by Thaumcraft itself. |
| `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/api/internal/CommonInternals.java` | Legacy global state | Stores object aspect tags and entity scan tags. |
| `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/config/ConfigAspects.java` | Main built-in aspect data | Registers vanilla, Thaumcraft, ore dictionary, potion, and entity aspects during post-init. |
| `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java` | Object lookup and generated aspect logic | Contains the real lookup order, bonus rules, potion reagent recursion, and generated recipe formulas. |
| `03_self_decompiled_check/vineflower_thaumcraft6` | Original jar self-decompile check | Used to confirm key behavior against `01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar`. |
| `06_docs/migration/NeoForge_legacy_migration_guide.md` | Porting constraints | Confirms tag replacement for `OreDictionary`, Data Components for old stack NBT, and reload-safe data/service boundaries. |

## Critical Findings

| Finding | Impact |
|---|---|
| The authoritative source for final parity must be the original 1.12.2 jar running under Forge, not only decompiled source. | Decompiled source is good enough for implementation design, but exact parity for edge cases must be dumped from runtime. |
| `CommonInternals.objectTags` stores `Integer -> AspectList` keys produced from `ItemStack.serializeNBT().toString().hashCode()`. | A modern port cannot preserve that storage literally; it must preserve lookup semantics through stable ids, tags, and component-aware keys. |
| `ThaumcraftApi.exists(ItemStack)` looks up `CommonInternals.objectTags` using the serialized NBT string, while registrations store integer hash keys. This mismatch is present in TheDarkTower source and the Vineflower decompile of the original jar. | Runtime dumps must remain authoritative. The mapped dump shows some `registerComplexObjectTag` entries act like "generate then register", but later wildcard registrations can still be masked by exact generated entries during normal lookup. |
| Legacy lookup may return an empty `AspectList` for a non-empty stack with no visible aspects, while some modern code currently returns `null` for no-aspect exclusions. | Tooltips look identical, but API parity is different. The comparison harness must track both `null` and empty-list separately. |
| Tooltips are display consumers, not authoritative aspect data. | Shift tooltip screenshots cannot prove parity because tooltip visibility, sorting, config, and rendering can differ from API results. |

## Aspect Definitions

| Legacy piece | Behavior | 1.21.1 status |
|---|---|---|
| `Aspect` static registry | Registers 37 aspects in a `LinkedHashMap` in fixed order. Six primals have null components; compounds have two component references. | Port keeps the 37 ids, order, colors, components, image paths, chat colors, blend modes, and mix hash behavior. |
| Constructor side effects | Each aspect constructor registers a `ScanAspect("!" + tag, aspect)` with `ScanningManager`. | Deferred. Scanning/research is not ported yet, so the constructor side effect is intentionally not recreated as gameplay behavior. |
| Aspect ids | Legacy ids are short tags such as `aer`, `terra`, `praecantatio`. | Port uses stable ids equivalent to `thaumcraft:<legacy_tag>`. |
| Combination lookup | `AspectHelper.getCombinationResult` scans registered aspects and accepts reversed component order. | Port matches this pure helper behavior. |

## AspectList Semantics

| Method/behavior | Legacy rule | Port requirement |
|---|---|---|
| Storage | Mutable `LinkedHashMap<Aspect, Integer>`. Insertion order is observable. | Keep insertion-order behavior anywhere parity is visible or compared. |
| `add` | Adds to an existing amount or inserts a new key. It accepts zero and null keys indirectly. | Current port preserves the legacy mutable behavior. |
| `merge` | Keeps the higher amount when key exists; otherwise inserts. | Preserve exactly. Many bonus rules use `merge`, not `add`. |
| `reduce` | If enough amount exists, subtracts and keeps the key even when the new amount is zero. | Preserve exactly; some legacy code relies on later cleanup to remove non-positive amounts. |
| `remove(aspect, amount)` | Subtracts and removes the key if amount becomes zero or negative. | Preserve exactly. |
| Sorting by name/amount | Bubble-sort style; amount sort is descending and stable enough for equal amounts because only strict `>` swaps. | Tooltip parity depends on this. |
| NBT | Writes list entries with `key` and `amount`; unknown aspect ids can create a null key on read. | Current port intentionally keeps unknown-aspect null-key behavior for compatibility. |

## Registration Lifecycle

| Phase | Legacy behavior |
|---|---|
| `ConfigAspects.postInit()` | Clears `CommonInternals.objectTags`, calls `registerItemAspects()`, calls `registerEntityAspects()`, then posts `AspectRegistryEvent` on `MinecraftForge.EVENT_BUS`. |
| Built-in item aspects | Registered through `ThaumcraftApi.registerObjectTag`, `registerComplexObjectTag`, and string `OreDictionary` keys. |
| Built-in entity aspects | Registered into `CommonInternals.scanEntities` as entity string id plus optional NBT predicates. |
| Addon extension | Addons can use the event proxy after Thaumcraft built-ins. The API comment says addon aspects should be added after Thaumcraft adds its aspects. |

Modern decision: keep built-in data deterministic and reload-safe first. Do not expose public addon registration until the internal service has exact parity fixtures and a stable data format.

## Object Registration

| Legacy API | Actual behavior | Modern equivalent |
|---|---|---|
| `registerObjectTag(ItemStack, AspectList)` | Null aspect list becomes an empty `AspectList`; stack count is normalized to 1; full stack NBT and damage are included in the serialized hash. | Exact item id assignment for simple stacks; component-aware keys for stack-sensitive variants. Empty assignments need explicit support if API parity matters. |
| `registerObjectTag(String oreDict, AspectList)` | Expands old `OreDictionary` entries through `ThaumcraftApiHelper.getOresWithWildCards`, copies each stack, count-normalizes, and registers each stack separately. | Audited item/block tags. Use `c:` only for stable common material groups; use `thaumcraft:legacy_ore_dictionary/*` for exact legacy names. |
| `registerComplexObjectTag(ItemStack, AspectList)` | Intended to combine generated aspects and explicit extras. Because `exists()` appears to miss integer-keyed registrations, runtime behavior likely always takes the "not exists" branch unless proven otherwise. | Current port models complex extras as data read by the generated cache. Final behavior needs runtime confirmation. |
| `registerEntityTag` | Adds entity scan/aspect entry to `CommonInternals.scanEntities`. | Future entity aspect data with entity type ids and predicates. |

## Object Lookup Order

Legacy `ThaumcraftCraftingManager.getObjectTags(ItemStack, history)` resolves a stack in this exact sequence:

| Order | Lookup | Detail |
|---:|---|---|
| 0 | Empty stack guard | Empty stack returns `null` immediately. |
| 1 | Exact key | Full stack id from count-normalized serialized NBT hash. Damage and NBT are included. |
| 2 | Wildcard damage key | Copy stack, set damage to `32767`, then lookup full NBT hash. |
| 3 | Wildcard input scan | If the input stack itself has damage `32767`, scan damage values `0..15` and return the first registered match. |
| 4 | Stripped exact key | Copy stack, remove tag compound, lookup count-normalized hash. |
| 5 | Stripped wildcard key | Copy stack, remove tag compound, set damage `32767`, lookup hash. |
| 6 | Generated fallback | Call `generateTags(itemstack, history)` only after all registered lookup variants fail. |
| 7 | Bonus pass | Call `getBonusTags(itemstack, tmp)`, then cap all amounts to `500`. |

The modern service currently uses `exact item id > tag > generated cache > stack bonuses`, plus potion/no-aspect special cases. This is close for flattened simple items, but it does not yet implement legacy wildcard damage, stripped NBT, or empty-list-vs-null API parity.

## Bonus Aspect Rules

`getBonusTags` is part of normal object lookup, not an optional display layer.

| Bonus source | Legacy behavior | Current 1.21.1 status |
|---|---|---|
| `IEssentiaContainerItem` | If the item does not ignore contained aspects, base source aspects are cleared and the contained aspects from the item become the output. Non-positive entries are removed. | Deferred until essentia containers and item data are designed. |
| Existing base aspects | If source aspects are present, all base aspects are added into a new output list before runtime bonuses. | Implemented for current simple stacks. |
| Armor | `PROTECT = damageReduceAmount * 4`, merged into output. | Implemented with modern armor defense. |
| Sword | `AVERSION = (attackDamage + 1) * 4`, merged. | Implemented approximately through modern tier attack bonus and base sword behavior. Runtime dump is needed for all 1.12 sword variants. |
| Bow | Adds `AVERSION 10` and `FLIGHT 5`. | Implemented for vanilla bow. Crossbows/tridents are 1.21-only policy items, not exact legacy. |
| Tools | `ItemTool`: `TOOL = (harvestLevel + 1) * 4`. | Implemented for `DiggerItem`/`TieredItem`, with netherite as a policy extension. |
| Shears/hoes | Uses max durability thresholds: wood -> 4, stone/gold -> 8, iron -> 12, otherwise -> 16. | Implemented. Threshold oddities should stay because they are legacy behavior. |
| Dyes | Any stack with an ore dictionary name matching `dye*` gets `SENSES 5`. | Implemented via `DyeItem`. |
| Normal enchantments | Reads stack enchantment NBT. Level is multiplied by `3`, then mapped to aspect bonuses. | Implemented with modern enchantment components for legacy enchantments plus documented 1.21-only policies. |
| Enchanted books | Uses stored enchantment NBT instead of normal item enchantments. | Implemented with `STORED_ENCHANTMENTS`. |
| Magic bonus | Adds rarity bonus (`uncommon +2`, `rare +4`, `very rare +6`) plus the multiplied enchantment level; merges `MAGIC`. | Implemented by modern weight mapping. Needs runtime parity for every legacy enchantment level. |
| Cull | After all bonuses, `AspectHelper.cullTags(tmp)` limits visible/stored aspects to 7 using legacy weighted removal. | Implemented. |

Legacy enchantment aspect mapping:

| Enchantment | Aspects |
|---|---|
| Aqua Affinity | `AQUA level` |
| Bane of Arthropods | `BESTIA level/2`, `AVERSIO level/2` |
| Blast Protection | `PRAEMUNIO level/2`, `PERDITIO level/2` |
| Efficiency | `INSTRUMENTUM level` |
| Feather Falling | `VOLATUS level` |
| Fire Aspect | `IGNIS level/2`, `AVERSIO level/2` |
| Fire Protection | `PRAEMUNIO level/2`, `IGNIS level/2` |
| Flame | `IGNIS level` |
| Fortune | `DESIDERIUM level` |
| Infinity | `FABRICO level` |
| Knockback | `AER level` |
| Looting | `DESIDERIUM level` |
| Power | `AVERSIO level` |
| Projectile Protection | `PRAEMUNIO level` |
| Protection | `PRAEMUNIO level` |
| Punch | `AER level` |
| Respiration | `AER level` |
| Sharpness | `AVERSIO level` |
| Silk Touch | `PERMUTATIO level` |
| Thorns | `AVERSIO level` |
| Smite | `EXANIMIS level/2`, `AVERSIO level/2` |
| Unbreaking | `TERRA level` |
| Depth Strider | `AQUA level` |
| Luck of the Sea | `DESIDERIUM level` |
| Lure | `BESTIA level` |
| Frost Walker | `GELUM level` |
| Mending | `FABRICO level` |

## Potion Aspect Rules

Legacy potion handling is not a plain item assignment. `ConfigAspects` iterates every `PotionType` and registers concrete potion stacks:

| Carrier | Legacy addition after potion content aspects |
|---|---|
| `Items.POTIONITEM` | Adds `AQUA 5`. |
| `Items.TIPPED_ARROW` | Adds `AVERSIO 5`. |
| `Items.SPLASH_POTION` | Adds `POTENTIA 5`. |
| `Items.LINGERING_POTION` | Adds `VINCULUM 5`. |

`getPotionAspects` resolves the content:

| Case | Behavior |
|---|---|
| Water potion content | Adds `AQUA 5`. The regular potion carrier then adds another `AQUA 5`, so a water potion stack has `AQUA 10`. |
| Non-water potion with reagent path | Reflectively walks `PotionHelper.POTION_TYPE_CONVERSIONS`, takes the first matching path, adds reagent object aspects, adds `ALKIMIA 3` per reagent, then removes `66%` of every accumulated amount. |
| Non-water potion with no reagent path | Adds `PRAECANTATIO 5` and `ALKIMIA 5`. |

Modern status: `TCAspectStackRules` handles `POTION_CONTENTS` and carrier additions. The brewing path is currently encoded for 1.21.1 and must be checked against a 1.12 runtime dump for legacy potion types.

## Generated Aspect Rules

`generateTags` is used only after registered lookup misses.

| Step | Legacy behavior |
|---|---|
| Normalization | Copy stack, set count to 1. If the item is damageable or has no subtypes, set damage to `32767`. If the resulting stack is already registered according to `ThaumcraftApi.exists`, return normal lookup. |
| Recursion guard | History key is `stack.serializeNBT().toString()`. Stop if already seen or if history size reaches 100. |
| Damage reset | If normalized damage is wildcard, set it back to `0` before recipe generation. |
| Recipe priority | Try crucible first, infusion second, normal crafting last. Stop at the first non-null category. |
| Cache side effect | Cap result to 500, register generated aspects back into object tags, then return result. |

Recipe formulas:

| Recipe type | Formula |
|---|---|
| Crucible | Start from catalyst aspects. Add each recipe aspect as `int(sqrt(amount) / outputCount)`. Remove non-positive values. |
| Infusion | Use central input plus component aspects through the common ingredient formula. Add each recipe aspect as `int(sqrt(amount) / outputCount)`. Remove non-positive values. |
| Crafting | Scan all crafting recipes with matching output item and damage. For each candidate, derive ingredient aspects, add arcane `MAGIC` if the recipe is `IArcaneRecipe`, remove non-positive values, and choose the lowest positive total `visSize()`. |
| Ingredients | Take the first matching stack from each ingredient. Add its object aspects. Subtract aspects of remaining items. Output each aspect as `int(sum * 0.75 / outputCount)`, except values in `(0.75, 1.0)` become `1`. |

Modern status: standard `RecipeType.CRAFTING` generation is implemented as a reload-built cache. Crucible, infusion, and arcane crafting generation are not implemented yet and must wait for their recipe systems.

## Entity And Scanning Logic

| Area | Legacy behavior | Modern status |
|---|---|---|
| Entity aspects | `ConfigAspects.registerEntityAspects()` registers vanilla and Thaumcraft entity names with aspect lists and optional NBT predicates. | Vanilla/player rows and active Thaumcraft custom entity rows are implemented in `TCEntityAspectAssignments`, including Wisp dynamic type aspects, Firebat and the current taint/WarpEvents/cultist foundations. Pech-style and future NBT-predicate rows remain deferred until their entity families exist. |
| Player aspects | `AspectHelper.getEntityAspects(EntityPlayer)` returns `HUMANUS 4` plus three random aspects at amount `15`, seeded by player name hash. | Implemented for player branch. |
| Non-player aspects | Scans `CommonInternals.scanEntities`; if NBT predicates match, uses the matching aspect list. Later entries can override earlier ones because the loop does not break. | Needs entity type/predicate data loader. |
| `ScanGeneric` | Item/block/entity can be scanned when object/entity aspects are non-null and non-empty. Success adds observation knowledge to all research categories by formula. | Deferred. |
| `ScanAspect` | Every aspect constructor adds a scannable trigger `!<aspect>`. The scan succeeds if the object/entity has that aspect amount > 0. | Deferred. |

## Tooltip Logic

Legacy GUI tooltip logic is a client-side consumer:

| Behavior | Detail |
|---|---|
| Visibility | In container GUI, aspects show when `GuiScreen.isShiftKeyDown() != ModConfig.CONFIG_GRAPHICS.showTags`. Default behavior is effectively Shift-to-show unless config inverts it. |
| Data source | Calls `ThaumcraftCraftingManager.getObjectTags(event.getItemStack())`, so it includes generated and bonus aspects. |
| Display order | Uses `getAspectsSortedByAmount()` in the overlay renderer. |
| Empty/no aspect | If tags are `null` or size `0`, no icons are drawn. |

Modern status: the current Shift tooltip is a useful visual check, but it is not enough for parity. API dumps must compare the resolved `AspectList`.

## Current 1.21.1 Parity Status

| Area | Current status | Gap |
|---|---|---|
| Aspect definitions | Close/exact for built-in 37 aspects. | Constructor-side scanning registration intentionally deferred. |
| `AspectList` | Close/exact for core mutable semantics. | Need parity dump for null-key/zero-amount edge cases if external API compatibility matters. |
| `AspectHelper` pure methods | Close/exact for cull, combination, primals, aura-aspects, random primal. | Entity aspects now include player branch and vanilla entity assignment lookup. |
| Built-in direct/tag assignments | Data-driven and validated for current registered ids and vanilla item-id coverage. | Current counts are `702` exact, `46` tag, and `32` complex exact assignments after server reload. Full legacy catalog still depends on unported Thaumcraft items and modded material policy. |
| Object lookup | Modern id/tag/generated/bonus service matches the current mapped runtime target. | Wildcard metadata and stripped-NBT behavior are represented only through audited flattened ids or component-aware rules so far. Addon event timing and future stateful/custom stacks remain deferred. |
| Spawn eggs | Visible no-aspect behavior implemented. | API-level `null` vs empty-list parity must be decided by runtime dump. |
| Potions | Component-aware carrier behavior implemented, including dump-derived final 1.12 potion carrier/content overrides for comparable regular, splash, lingering, and tipped-arrow stacks. | New 1.21-only potion/component samples remain policy review, not 1.12 parity targets. |
| Enchantments | Legacy enchantment mapping implemented for current components, including the mapped `sweeping` -> `sweeping_edge` stored-book parity case. | New 1.21-only enchantments remain policy review. |
| Generated crafting | Standard crafting plus current arcane recipe cache implemented and reload-safe; latest server reload rebuilt `636` generated object assignments after exact runtime parity overrides took ownership of legacy-equivalent flattened vanilla ids and current registered Thaumcraft overrides. | Crucible, infusion, wildcard/NBT generation for stateful stacks, and third-party namespaces deferred. |

## Current Runtime Diff Lessons

| Lesson | Evidence | Port implication |
|---|---|---|
| Legacy-to-modern id/meta mapping must be explicit. | The mapped comparer now reports `283` exact parity entries only after aliasing old ids/meta stacks to flattened 1.21 ids. | Keep `legacy_to_modern_stack_map.json` as part of the parity harness. Do not count mapped matches as accidental success. |
| Some earlier port gaps were real data/logic mistakes. | `fire_charge`, `end_crystal`, `elytra`, `firework_star`, `firework_rocket`, infested blocks, stained glass, melon, buckets, boats, doors, chests, arrows, and color/meta families changed from gaps to parity/expected buckets after assignment, complex, no-aspect, and runtime-parity fixes. | Fix root causes in assignment/data/stack rules, then rerun both server dump and comparer. |
| `registerComplexObjectTag` cannot be bulk-copied from source lines. | Legacy `chest` resolves as generated `herba 18`; similar families can be masked by exact generated entries, wildcard damage, or metadata lookup order. | The shared plain vanilla set is now closed through dump-derived runtime parity values. Apply the same trace-first rule to future Thaumcraft/stateful/addon stacks. |
| Potion parity is not a carrier-only problem. | The previous `48` potion gaps closed only after using dump-derived final runtime outputs for the comparable carrier/content variants. | Do not replace those values with heuristic reagent guesses unless a full 1.12 conversion-order trace proves identical output. |
| Registered Thaumcraft ids must be treated separately from vanilla parity. | The previous `11` registered Thaumcraft ids now match through `current_registered_runtime_parity.json`. | Defer unregistered Thaumcraft legacy-only ids until their content is ported. |
| Essentia container item aspects | Not implemented. | Requires essentia item/container design. |
| Entity aspects | Player branch, vanilla entity rows and active Thaumcraft custom entity rows. | Pech-style and future unported custom NBT predicates deferred. |

## Preservation Decisions

| Legacy behavior | Decision |
|---|---|
| Aspect ids, colors, order, component graph, and core `AspectList` math | Preserve exactly. |
| `getObjectTags` final values for legacy-equivalent stacks | Preserve exactly where a 1.21 stack equivalent exists. |
| Metadata and wildcard matching | Emulate through explicit flattened ids, tags, or component-aware keys. Do not keep metadata as hidden state. |
| Old stack NBT matching | Recreate through reviewed Data Components only when the legacy behavior is known. |
| `OreDictionary` strings | Recreate through audited tags; never infer broad tags without a source line and validation. |
| Lookup-time recipe scanning | Replace with reload-safe generated caches while preserving formula and priority. |
| `ThaumcraftApi.exists()` bug/quirk | Confirm through runtime instrumentation before intentionally preserving or correcting. |
| Addon `AspectRegistryEvent` | Redesign later as datapack/API extension after internal parity is proven. |

## Next Required Work

1. Keep `runServer -PtcAspectDump=true` plus `compare_aspect_dumps.py` at `0` real `PORT_GAP_*` entries after each aspect assignment or stack-rule batch.
2. Expand the mapping file only for clear legacy-to-modern equivalents; leave uncertain stacks in mapping or policy review buckets.
3. Add custom arcane/crucible/infusion recipe parity only after those serializers and runtime paths exist.
4. Do not connect aspects to scanning, research, aura, essentia, or gameplay costs until the mapped dump remains clean after the next content batch.
