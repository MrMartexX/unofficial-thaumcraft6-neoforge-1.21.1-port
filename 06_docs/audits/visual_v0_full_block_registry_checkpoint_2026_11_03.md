# V0: complete original block-registration ID census and reverse port block classification

**Pinned baseline:** `main` `72eaac17a1e1c0c51b27cb151ef7f244944c89d5` (2026-11-03).  
**Working PR:** [draft PR #11](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/pull/11).  
**Machine-readable census:** [200 original / 175 modern block source registry crosswalk](generated/visual_v0_full_legacy_blocks_bidirectional_crosswalk_2026_11_03.json).

## 200 original source declarations resolved

Original [`ConfigBlocks.initBlocks`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/config/ConfigBlocks.java) contains 152 field construction/registration declarations, plus three `EnumDyeColor` loops producing 16 candle, 16 banner and 16 Nitor names: **200 source-declared block registry paths**. The former `42` field-symbol-only cases are now confirmed from all 41 relevant original Java classes, including inheritance through [`BlockTC`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/BlockTC.java) and [`BlockTCTile`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/BlockTCTile.java). No false ID was found in that 42-class set. The 104 literal-constructor cases were verified against 28 classes or the explicit `BlockRailPowered.setRegistryName` call. Six original field IDs had been resolved in the previous constructor delta.

**Source-level identities, not runtime-JAR counts.** We have not proved these 200 are all instantiated/registered successfully by the packaged original mod, nor measured model, behavior or pixel parity.

| Original declaration bucket | Count | Interpretation |
| --- | ---: | --- |
| Same modern registered block ID | **167** | Registration path only, not implementation equivalence |
| Different or absent modern ID | **33** | Must review representation, folding, deferrals or missing functionality |
| **Total original source registrations** | **200** | Every first-pass source declaration has an evidence-derived path |

### The 33 nonexact original block paths

| Group | Count | Why it needs review |
| --- | ---: | --- |
| Legacy double slabs | 6 | Separate original registry names, potentially folded into modern DOUBLE slab states |
| Banners | 17 | 16 colors and crimson cult variant, no same-name registered modern world block |
| Transport endpoints | 2 | essentia_input/output → essentiatransportin/out name candidates |
| Ambient, brain box, relay, barrier | 4 | No same-name registered block, representation needs review |
| Internal/helper blocks | 4 | activator_rail, effect_shock, effect_sap, empty |

The original 16 `banner_<dye>` blocks plus `banner_crimson_cult` use actual [`BlockBannerTC`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/basic/BlockBannerTC.java) world-block registrations and custom banner ItemBlocks. The port currently has item catalogs/placeholders but no **same-name world block registration** for these 17 IDs. This is a **high-priority visual and behavior gap candidate**, not yet definitive proof that no alternative representation exists.

The six separately registered original `slab_double_*` blocks may be represented as `type=double` in the corresponding six modern slab IDs, so treating absent standalone registry IDs as missing blocks would be misleading. Verify state, placement, survival, drops and model variants.

Other nonexact entries requiring individual evidence:

| Original block ID | Modern ID candidate | Source class |
| --- | --- | --- |
| `grass_ambient` | none confirmed | [`BlockGrassAmbient`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/world/BlockGrassAmbient.java) |
| `brain_box` | none confirmed | [`BlockBrainBox`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockBrainBox.java) |
| `redstone_relay` | none confirmed | [`BlockRedstoneRelay`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockRedstoneRelay.java) |
| `barrier` | none confirmed | [`BlockBarrier`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/misc/BlockBarrier.java) |
| `essentia_input` | `essentiatransportin` | [`BlockEssentiaTransport`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/essentia/BlockEssentiaTransport.java) |
| `essentia_output` | `essentiatransportout` | [`BlockEssentiaTransport`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/essentia/BlockEssentiaTransport.java) |
| `activator_rail` | none confirmed | [`BlockRailPowered`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/config/ConfigBlocks.java) |
| `effect_shock` | none confirmed | [`BlockEffect`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/misc/BlockEffect.java) |
| `effect_sap` | none confirmed | [`BlockEffect`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/misc/BlockEffect.java) |
| `empty` | none confirmed | [`BlockTranslucent`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/basic/BlockTranslucent.java) |

## Reverse classification of all 175 modern registered blocks

| Source-level modern bucket | Count | Meaning |
| --- | ---: | --- |
| Same original block registry path | **167** | Exact ID only |
| Candidate renamed legacy transport endpoint | **2** | Semantic equivalence pending |
| Modern deepslate ore variants | **3** | Post-1.12 worldgen extension, no exact original source path |
| Secondary set of pedestal IDs | **3** | Must establish canonical IDs and resource/behavior relationships |
| **Total** | **175** | Every active port block ID is accounted for |

The eight modern IDs without exact original source paths:

| Modern block ID | Classification | Original candidate |
| --- | --- | --- |
| `deepslate_amber_ore` | MODERN_DEEPSLATE_EXTENSION | No exact original ID |
| `deepslate_cinnabar_ore` | MODERN_DEEPSLATE_EXTENSION | No exact original ID |
| `deepslate_quartz_ore` | MODERN_DEEPSLATE_EXTENSION | No exact original ID |
| `essentiatransportin` | LEGACY_ALTERNATE_CANDIDATE | `essentia_input` |
| `essentiatransportout` | LEGACY_ALTERNATE_CANDIDATE | `essentia_output` |
| `arcane_pedestal` | MODERN_SECONDARY_PEDESTAL_ID_REVIEW | No exact original ID |
| `ancient_pedestal` | MODERN_SECONDARY_PEDESTAL_ID_REVIEW | No exact original ID |
| `eldritch_pedestal` | MODERN_SECONDARY_PEDESTAL_ID_REVIEW | No exact original ID |

**Pedestal risk:** Modern [`TCBlocks`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlocks.java) registers both `pedestal_arcane/ancient/eldritch` (matching original IDs) **and** `arcane_pedestal/ancient_pedestal/eldritch_pedestal` as separate block instances, currently using the same factory. These may be compatibility IDs or duplicated content. Resolve canonical item, blockstate, creative tab, recipes, loot, saved-state migration and textures before changing them. Do not delete aliases without proof.

**Transport ID risk:** Original `essentia_input` and `essentia_output` map by name to modern `essentiatransportin` and `essentiatransportout`; confirm corresponding BlockEntity, faces, NeoForge capability and models. Current registration names alone are insufficient.

## Next acceptance gates

1. Compare all 33 nonexact original block IDs with **packaged original JAR**, original asset/blockstate registry and modern implementations. Record `MATCHED`, intentional modern policy, `PLACEHOLDER` or actual `MISSING` only with evidence.
2. Audit 17 banner blocks and items as placed world objects (TileEntity/BlockEntity, color, pole/wall model, animations and persistence).
3. Audit six folded double slab state/model/drop paths and three duplicate pedestal families.
4. Close original block-item wrappers in the **item** reverse census, including original `BlockBannerTCItem` and `ItemSlab`, so 108 original `ConfigItems` fields are not conflated with the total original item registry.
5. Confirm original JAR/runtime registration counts, all active model parent/texture dependencies, GUI/HUD and entity/BER branches. Batch running of PowerShell, Gradle and dedicated/client tests is still pending.

## Verification

This checkpoint uses pinned `ConfigBlocks`/class source, 200 first-pass declaration objects and the independent 175-entry current block registry source snapshot. Class-level name forwarding for candle and Nitor was checked explicitly. **No PowerShell script, Gradle build, original JAR, server or Minecraft client was executed.** This draft PR is audit-only; model/gameplay parity has not been established.
