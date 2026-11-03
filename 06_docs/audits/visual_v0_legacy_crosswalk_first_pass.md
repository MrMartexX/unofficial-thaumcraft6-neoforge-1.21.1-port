# V0: legacy → port two-way source crosswalk, first pass

**Pinned `main`:** `72eaac17a1e1c0c51b27cb151ef7f244944c89d5` (2026-11-03).  
**Working PR:** [V0 draft PR #11](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/pull/11).  
**Machine-readable review queue:** [legacy source candidate crosswalk](generated/visual_v0_legacy_port_crosswalk_candidates_2026_11_03.json).

## What is independently reconciled

Original `ConfigEntities.initEntities` contains 43 explicit `registerModEntity` calls. Current `TCEntityTypes` contains 43 `LegacyEntitySpec` mappings and 43 actual `ENTITY_TYPES.register` declarations. A direct comparison of original legacy ID, Java class, mapping source line and current registered ID yields **43/43 consistent entity type mappings, no extra or unpaired registered entity types**.

This is a complete **registration identity crosswalk** for the examined entity registry entrypoints, not a visual equivalence pass. Known invisible renderers such as Mind Spider, Focus Mine, Golem Dart and Grapple remain visual issues (F128, F134–F136). Every `LegacyEntitySpec` currently says `registered_foundation`, which does not assert gameplay or renderer parity.

## Blocks: reverse registration candidate census

The legacy `ConfigBlocks` source contains 152 assignments of `BlocksTC` fields to constructed blocks, plus three 16-color loops for candle/banner/Nitor: **200 source-declared block candidates**. This number is an extraction result, not a proven runtime registry size. The current port has **175 active block registry declarations**. The two numbers are not one-to-one comparable: double slabs, old helper blocks, special-case IDs, modern-only blocks and split/combined states require explicit decisions.

| Static evidence bucket | Legacy candidates | Meaning |
| --- | ---: | --- |
| Constructor literal overlaps a modern block ID | 91 | Candidate only until constructor registry ID is confirmed |
| No constructor ID literal, symbol-derived ID exists | 42 | Weaker inference; must inspect original class |
| Constructor literal not found as modern ID | 13 | Review rename, removal, state folding or equivalent representation |
| No literal and no simple symbol-derived counterpart | 6 | Must inspect original registry constructors |
| Algorithmic dye ID overlaps modern block ID | 32 | All 16 candle and all 16 Nitor colors have same-name block candidates |
| Algorithmic dye ID absent from modern block registry | 16 | All 16 original `banner_*` color blocks; separate current placeholder item registrations are not block parity |

Within the 13 literal nonmatches, `banner_crimson_cult` is also absent from the modern block registry. Six legacy double-slab IDs similarly lack standalone modern IDs, but modern slab blockstates may represent double slabs. Do not classify every nonmatch as missing gameplay.

The reverse modern view still has **10 active block IDs without a direct source-literal/symbol candidate** under this limited parser; they require explicit new/renamed classification. These are not automatically extras.

## Items: base IDs versus 1.12 metadata families

Original `ConfigItems.initItems` has **108 item-field construction assignments** scanned here. Modern `TCItems` has **487 declared item registrations**, including block items, spawn eggs and split variants. The 108 and 487 are **different units** and must not be used as a migration percentage.

| Static evidence bucket | Legacy item constructors |
| --- | ---: |
| Constructor literal overlaps modern ID | 40 |
| Source-field name suggests a modern ID | 41 |
| Constructor literal not found as same modern ID | 10 |
| Neither a literal nor a simple symbol candidate | 17 |

A focused `ItemTCBase` subtype scan found six explicit multi-variant families containing **30 variants**. Of these, **24** have candidate modern IDs by direct `base_variant`, `variant_base` or concatenation. **Six** do not:

- `nugget:iron` (could be represented by the vanilla item);
- `nugget:rareearth`;
- `cluster:quartz`;
- `mind:biothaumic`;
- `module:vision`;
- `module:aggression`.

These are **unresolved mappings**, not verified missing items. The original family `ingot` maps to candidate items `thaumium_ingot`, `void_ingot` and `brass_ingot`; `plate` similarly splits into four candidates. Comparing only the base ID would falsely flag all these as missing.

The reverse modern view has **406 active item IDs without a direct base-item candidate** under this narrow source parser. That is primarily a sign of missing variant/block-item/placeholder analysis and **not** a claim that 406 objects are additional to the original.

## Trust boundary and next actions

- Source-literal values used as constructor arguments are *candidate IDs*, not automatically verified Forge registry names. Confirm constructor implementation and packaged original JAR before promoting a candidate to `MATCHED`.
- Reuse `tools/audits/item-block-parity/extract-legacy-primary-manifest.ps1` and the existing `legacy_mapping_review.ps1` for a complete class/identity comparison; avoid duplicating their output and address inferred-symbol false positives.
- Explicitly enumerate ItemStack metadata, NBT, model overrides, Creative tab ordering and custom client rendering. Then reconcile legacy-only and port-only entries in both directions, including original block item wrappers.
- Cross-check direct constructors with the original JAR and secondary decompilation where source strings are ambiguous. Retain `REVIEW` until resolved.
- Re-run the source registry extraction after any `main` SHA change. The JSON is a **frozen evidence snapshot**, not a live query and not a gameplay test.

**Verification this cycle:** GitHub source at pinned SHA and independent source-parser consistency (43 entities and code-class pairs, 200 block declaration candidates, 108 item assignments, six variant families). No PowerShell, Gradle or client runtime was executed. No gameplay code or existing audit suite changed.

**V0 remains open:** completion requires runtime registry reconciliation, a JAR-backed bidirectional mapping, variant expansion and all active visual branches with evidence statuses.
