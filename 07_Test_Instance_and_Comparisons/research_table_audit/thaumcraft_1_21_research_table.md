# Research Table Theorycraft Diagnostic

| Check | Status | Notes |
|---|---|---|
| legacy_nbt_key_player | PASS | ResearchTableData serialize() should preserve key `player`. |
| legacy_nbt_key_inspiration | PASS | ResearchTableData serialize() should preserve key `inspiration`. |
| legacy_nbt_key_inspirationStart | PASS | ResearchTableData serialize() should preserve key `inspirationStart`. |
| legacy_nbt_key_placedCards | PASS | ResearchTableData serialize() should preserve key `placedCards`. |
| legacy_nbt_key_bonusDraws | PASS | ResearchTableData serialize() should preserve key `bonusDraws`. |
| legacy_nbt_key_aidsChosen | PASS | ResearchTableData serialize() should preserve key `aidsChosen`. |
| legacy_nbt_key_penaltyStart | PASS | ResearchTableData serialize() should preserve key `penaltyStart`. |
| legacy_nbt_key_savedCards | PASS | ResearchTableData serialize() should preserve key `savedCards`. |
| legacy_nbt_key_categoriesBlocked | PASS | ResearchTableData serialize() should preserve key `categoriesBlocked`. |
| legacy_nbt_key_categoryTotals | PASS | ResearchTableData serialize() should preserve key `categoryTotals`. |
| legacy_nbt_key_aidCards | PASS | ResearchTableData serialize() should preserve key `aidCards`. |
| legacy_nbt_key_cardChoices | PASS | ResearchTableData serialize() should preserve key `cardChoices`. |
| legacy_optional_last_draw_absent | PASS | `lastDraw` is optional in legacy and absent when null. |
| roundtrip_player | PASS | player=Martin |
| roundtrip_inspiration | PASS | inspiration=3/6 |
| roundtrip_saved_cards | PASS | savedCards=[11, 22] |
| roundtrip_blocked_categories | PASS | categoriesBlocked=[ELDRITCH] |
| roundtrip_category_totals | PASS | categoryTotals={ALCHEMY=10, AUROMANCY=45, BASICS=100} |
| roundtrip_card_choice | PASS | cardChoices=1 |
| add_total_removes_zero_or_negative | PASS | Legacy addTotal removes category totals at zero or below. |
| add_inspiration_clamps_to_start | PASS | inspiration=6/6 |
| finish_theory_basics_raw | PASS | 100% BASICS should award one THEORY point = 32 raw. |
| finish_theory_second_category_raw | PASS | 45% AUROMANCY rounds to 14 raw. |
| finish_theory_penalty_raw | PASS | 10% ALCHEMY after penalty rounds down from 3 to 2 raw. |
| public_api_card_registry_count | PASS | The public/API theorycraft card slice should keep the original 9 card ids. |
| safe_bridge_card_registry_count | PASS | First common card bridge should add dependency-free, aspect-crystal/phial, vanilla XP/aid, table-inventory, vanilla-item Golemancy, Artifice item-option and basic Infusion option cards only. |
| card_analyze_deferred_by_legacy_bug | PASS | Legacy decompiled CardAnalyze initializes from a null category lookup; kept out of random draws until corrected from a stronger source. |
| bookshelf_aid_registry | PASS | Legacy AidBookshelf adds Balance, two Notation cards and three Study cards. |
| bookshelf_aid_initialize_data | PASS | Selected aids should reduce initial inspiration by one each and append their card ids to aidCards. |
| vanilla_aid_registry | PASS | The active vanilla aid slice should contain bookshelf, enchanting table, beacon and dragon egg aids. |
| basic_block_aid_registry | PASS | Legacy basic block aids now bridge crucible, arcane workbench and infusion matrix card injection. |
| basic_block_aid_initialize_data | PASS | Selected basic block aids should reduce initial inspiration and append their legacy card ids in selection order. |
| card_measure_activation | PASS | Legacy CardMeasure adds 15 INFUSION and one bonus draw. |
| card_calibrate_activation | PASS | Legacy CardCalibrate adds 15 ARTIFICE and one bonus draw. |
| card_focus_activation | PASS | Legacy CardFocus adds 15 AUROMANCY and one bonus draw. |
| card_synergy_activation | PASS | Legacy CardSynergy drains 15 from ARTIFICE/ALCHEMY/INFUSION, adds 30 GOLEMANCY and increments penaltyStart. |
| card_beacon_activation | PASS | Legacy CardBeacon is aid-only, restores two inspiration through negative cost, adds one bonus draw and increments penaltyStart. |
| card_channel_activation | PASS | Legacy CardChannel picks a compound aspect, requires the matching filled phial and adds 25 INFUSION. |
| card_infuse_activation | PASS | Legacy CardInfuse picks a compound aspect and option item, consumes both that item and the matching filled phial, then adds Infusion by option visSize. |
| card_sculpting_activation | PASS | Legacy CardSculpting consumes one clay ball, adds 20 GOLEMANCY and grants one bonus draw. |
| card_tinker_activation | PASS | Legacy CardTinker chooses from the Artifice item option list and awards random ARTIFICE from aspect visSize. |
| card_mind_over_matter_activation | PASS | Legacy CardMindOverMatter consumes one Artifice option item and awards ARTIFICE from 10 + sqrt(aspect visSize). |
| card_scripting_activation | PASS | Legacy CardScripting consumes one extra paper and one extra ink from the research table, then adds 25 GOLEMANCY. |
| card_dragon_egg_activation | PASS | Legacy CardDragonEgg is aid-only and performs ten random +2..+5 category-total grants. |
| card_concentrate_activation | PASS | Legacy CardConcentrate requires one aspect crystal, adds 15 ALCHEMY and one bonus draw. |
| card_reactions_activation | PASS | Legacy CardReactions requires two different aspect crystals and adds 25 ALCHEMY. |
| card_synthesis_activation | PASS | Legacy CardSynthesis consumes two component crystals, adds 40 ALCHEMY and creates the compound crystal when a player is present. |
| sync_payload_roundtrip | PASS | Client cache payload can reconstruct theory data. |

Passed: 48
Failed: 0
