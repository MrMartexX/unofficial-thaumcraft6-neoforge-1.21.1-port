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
| core_card_registry_count | PASS | First core-card slice should register exactly 9 legacy public/API card ids. |
| card_analyze_deferred_by_legacy_bug | PASS | Legacy decompiled CardAnalyze initializes from a null category lookup; kept out of random draws until corrected from a stronger source. |
| sync_payload_roundtrip | PASS | Client cache payload can reconstruct theory data. |

Passed: 27
Failed: 0
