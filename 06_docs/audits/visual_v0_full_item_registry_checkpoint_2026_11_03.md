# V0: complete source item-registration ID crosswalk

**Pinned baseline:** `main` `72eaac17a1e1c0c51b27cb151ef7f244944c89d5` (2026-11-03).  
**Working pull request:** [draft PR #11](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/pull/11), no changes to active mod source.  
**Full data:** [bidirectional 108/487 source item registry map](generated/visual_v0_full_legacy_items_bidirectional_crosswalk_2026_11_03.json).

## Scope and identity boundary

The original [`ConfigItems.initItems`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/config/ConfigItems.java) contains 108 item construction/registration assignments. We resolved all **108 source-level legacy registration paths**, including constructor inheritance, literal name parameters and direct `setRegistryName` calls. The modern [`TCItems`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java) declares 487 modern item entries; 487 includes block items, spawn eggs, split metadata variants and catalog placeholders.

**This is a registry-path and candidate-correspondence audit, not 108/108 runtime parity.** An exact item path says nothing about visual pixel fidelity, source item subtypes, NBT/Data Component translation, custom models or gameplay behavior. No original JAR registry was executed.

## Original 108 declarations: completed source-name census

| Source-level item bucket | Original item declarations | Meaning |
| --- | ---: | --- |
| Exact same modern registry path | **83** | Only registration ID is verified, not behavior/visuals |
| Metadata family or replacement item candidates | **13** | Split into modern item IDs, needs per-subtype reconciliation |
| Renamed item path candidates | **7** | Name differs; equivalence is not assumed |
| No established modern item path | **5** | Must inspect feature representation or documented deferral |
| **Total examined** | **108** | Every registered original ConfigItems constructor field represented |

Of the **41 previously only symbol-inferred** original item declarations, direct class inspection verified **40 exact paths**. One false positive is resolved: `ItemsTC.turretPlacer` was originally registered as `thaumcraft:turret`, **not** `thaumcraft:turret_placer`. The 50 previously literal-based declarations were also validated through the 10 relevant parameterized original Java classes, `ItemTCBase` and constructor-name forwarding. The earlier 17 unresolved constructor names are incorporated from the existing delta.

## The 25 non-exact legacy paths: review queue

The following are not a list of removed features. Each counterpart is a **name candidate**; even `ItemFortressArmor` with a matching modern class must still pass state/render review.

| Original path | Classification | Modern item ID candidate(s) | Original class |
| --- | --- | --- | --- |
| `celestial_notes` | Review: no obvious ID | none identified | [`ItemCelestialNotes`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/curios/ItemCelestialNotes.java) |
| `creative_flux_sponge` | Review: no obvious ID | none identified | [`ItemCreativeFluxSponge`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/misc/ItemCreativeFluxSponge.java) |
| `module` | Review: no obvious ID | none identified | [`ItemTCBase`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/ItemTCBase.java) |
| `verdant_charm` | Review: no obvious ID | none identified | [`ItemVerdantCharm`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/baubles/ItemVerdantCharm.java) |
| `voidseer_charm` | Review: no obvious ID | none identified | [`ItemVoidseerCharm`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/baubles/ItemVoidseerCharm.java) |
| `amulet_vis` | Rename candidate | `vis_amulet` | [`ItemAmuletVis`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java) |
| `fortress_chest` | Rename candidate | `thaumium_fortress_chest` | [`ItemFortressArmor`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/armor/ItemFortressArmor.java) |
| `fortress_helm` | Rename candidate | `thaumium_fortress_helm`, `thaumium_fortress_helm_goggles` | [`ItemFortressArmor`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/armor/ItemFortressArmor.java) |
| `fortress_legs` | Rename candidate | `thaumium_fortress_legs` | [`ItemFortressArmor`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/armor/ItemFortressArmor.java) |
| `jar_brace` | Rename candidate | `brass_brace` | [`ItemTCBase`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/ItemTCBase.java) |
| `label` | Rename candidate | `jar_label`, `jar_label_essence` | [`ItemLabel`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/consumables/ItemLabel.java) |
| `sanity_soap` | Rename candidate | `sane_soap` | [`ItemSanitySoap`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/consumables/ItemSanitySoap.java) |
| `baubles` | Split/variant candidate | `bauble_amulet`, `bauble_ring`, `bauble_girdle`, `ring_apprentice` (+3) | [`ItemBaubles`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/baubles/ItemBaubles.java) |
| `chunk` | Split/variant candidate | `chunk_chicken`, `chunk_beef`, `chunk_pork`, `chunk_fish` (+2) | [`ItemChunksEdible`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/consumables/ItemChunksEdible.java) |
| `cluster` | Split/variant candidate | `cluster_iron`, `cluster_gold`, `cluster_copper`, `cluster_tin` (+3) | [`ItemTCBase`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/ItemTCBase.java) |
| `crystal_essence` | Split/variant candidate | `crystal_essence_aer`, `crystal_essence_alienis`, `crystal_essence_alkimia`, `crystal_essence_aqua` (+33) | [`ItemCrystalEssence`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/resources/ItemCrystalEssence.java) |
| `curio` | Split/variant candidate | `curio_arcane`, `curio_preserved`, `curio_ancient`, `curio_eldritch` (+3) | [`ItemCurio`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/curios/ItemCurio.java) |
| `enchanted_placeholder` | Split/variant candidate | `enchanted_placeholder_protection_1`, `enchanted_placeholder_sharpness_1`, `enchanted_placeholder_silk_touch_1`, `enchanted_placeholder_fortune_1` | [`ItemEnchantmentPlaceholder`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/curios/ItemEnchantmentPlaceholder.java) |
| `ingot` | Split/variant candidate | `thaumium_ingot`, `void_ingot`, `brass_ingot` | [`ItemTCBase`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/ItemTCBase.java) |
| `loot_bag` | Split/variant candidate | `loot_bag_common`, `loot_bag_uncommon`, `loot_bag_rare` | [`ItemLootBag`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/curios/ItemLootBag.java) |
| `mind` | Split/variant candidate | `mindclockwork`, `mindclockwork_advanced` | [`ItemTCBase`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/ItemTCBase.java) |
| `nugget` | Split/variant candidate | `quicksilver_nugget`, `brass_nugget`, `copper_nugget`, `tin_nugget` (+5) | [`ItemTCBase`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/ItemTCBase.java) |
| `plate` | Split/variant candidate | `brass_plate`, `iron_plate`, `thaumium_plate`, `void_plate` | [`ItemTCBase`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/ItemTCBase.java) |
| `seal` | Split/variant candidate | `seal_breaker`, `seal_breaker_advanced`, `seal_butcher`, `seal_empty` (+13) | [`ItemSealPlacer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/golems/seals/ItemSealPlacer.java) |
| `turret` | Split/variant candidate | `automatedcrossbow`, `advancedcrossbow`, `arcane_bore`, `turret_placer` | [`ItemTurretPlacer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/construct/ItemTurretPlacer.java) |

Important cases to verify before closing a single row:

1. **Turrets and Arcane Bore.** Original [`ItemTurretPlacer`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/construct/ItemTurretPlacer.java) selects `basic`, `advanced` and `bore` using metadata 0/1/2. Modern `TCItems` separately registers `automatedcrossbow` (`ItemTurretPlacer(false)`), `advancedcrossbow` (`true`), `arcane_bore` (`ItemArcaneBore`) and `turret_placer` (also `false`). All three broad placement branches therefore have plausible new representatives; the extra basic registration and exact original behavior require review. Modern implementations: [turret item](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/construct/ItemTurretPlacer.java) and [Arcane Bore item](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/tools/ItemArcaneBore.java).
2. **Six `ItemTCBase` metadata families.** Existing first-pass evidence enumerates 30 metadata variants and 24 simple name candidates. The unresolved variants are `nugget:iron`, `nugget:rareearth`, `cluster:quartz`, `mind:biothaumic`, `module:vision`, `module:aggression`. `iron` could be intentionally vanilla. Do not equate missing `thaumcraft` registration with missing function.
3. **Mind/golem catalog bridge.** `mindclockwork` is currently registered as a placeholder `new Item(...)`, so item-ID presence does **not** establish golem mind behavior. `mindclockwork_advanced` is another name candidate, not evidence for `biothaumic`.
4. **Fortress gear.** Original `fortress_helm/chest/legs` differ from current `thaumium_fortress_*` IDs. Current headgear also has `thaumium_fortress_helm_goggles`; compare armor model layers, original metadata and visuals, not only the registry path.
5. **Crystal essence and seal variants.** Modern source exposes 37 `crystal_essence_*` item names and 17 individual seal item names. Original crystals use an essentia container, and original seals use dynamically registered `SealHandler` metadata. These are **unverified candidate families**, not 54 certified conversions.
6. **No obvious name mapping.** `celestial_notes`, `module`, `verdant_charm`, `voidseer_charm`, `creative_flux_sponge` are high-priority review entries, not automatically defects.

## Reverse modern 487 registry classification

| Modern registration bucket | Count | Status |
| --- | ---: | --- |
| Exact original `ConfigItems` path | **83** | Name only; needs parity checks |
| Candidate for a nonexact original item | **119** | Includes split/renamed, may include extra variants |
| Registered `blockItem` | **165** | Requires original `ConfigBlocks`/BlockItem crosswalk |
| Registered `spawnEgg` | **21** | Requires entity/spawn family crosswalk |
| Other active modern item registrations | **99** | Still need original-family, alias, catalog/placeholder classification |
| **Total** | **487** | All current source-declared registrations partitioned once |

The previous first-pass figure of **406 reverse-unclassified items** was a *parser limitation*. This second pass assigns all 487 modern registrations to explicit review buckets. It does **not** prove that any of the 99 residual IDs are newly invented or missing from the original: nested block/alias/catalog registration cases require a separate, source-backed review.

## Acceptance gates for this item checkpoint

- [x] Every source declaration in `ConfigItems.initItems` has a constructor-derived registry path, direct-setter path or proven name-forwarding chain.
- [x] Compare 108 original paths against all 487 current item registrations; preserve 25 nonexact names as review statuses.
- [x] Partition all 487 reverse entries into explicit, non-overlapping **source-level** review buckets.
- [ ] Verify original packaged JAR and registry log; reconcile all metadata/NBT and dynamically registered subtypes.
- [ ] Verify modern class behavior, renderer, model transforms and item-display state for each matching or split candidate.
- [ ] Cover original block-item wrappers and modern block items in the separate complete **block** bidirectional census.
- [ ] Check screenshot/model-pixel parity and client GUI/animation behavior.

**Validation scope:** source files and generated JSON only. The PowerShell extractor, original JAR, Gradle build, dedicated server and Minecraft client were **not executed**. The working PR remains **draft**.
