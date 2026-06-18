# Thaumonomicon Protocol Foundation Audit

| Check | Status | Detail |
|---|---|---|
| `index_has_visible_category` | `PASS` | count=1 |
| `index_has_visible_entry` | `PASS` | count=4 |
| `index_revision_matches_server_state` | `PASS` | revision=-1558952512 |
| `category_visibility_server_filtered` | `PASS` | visible=1 |
| `entry_visibility_server_filtered` | `PASS` | visible=4 |
| `unlockable_state_server_owned` | `PASS` | visible=4 |
| `research_flags_server_owned` | `PASS` | visible=4 |
| `visible_entry_view_available` | `PASS` | inspected=4 |
| `legacy_missing_pages_filtered` | `PASS` | pages=1 |
| `ready_page_views_have_server_crafting_snapshots` | `PASS` | pages=1 |
| `ready_page_views_have_server_arcane_snapshots` | `PASS` | pages=1 |
| `ready_page_views_use_matching_snapshot_kind` | `PASS` | pages=1 |
| `non_ready_page_views_have_no_recipe_snapshots` | `PASS` | pages=1 |
| `ready_crafting_catalog_entries_have_valid_server_snapshots` | `PASS` | ready_crafting_entries=79, fake_crafting_entries=2, deferred_crafting_entries=0 |
| `fake_crafting_catalog_entries_are_classified` | `PASS` | fake_crafting_entries=2 |
| `ready_arcane_catalog_entries_have_valid_server_snapshots` | `PASS` | ready_arcane_entries=89, deferred_arcane_entries=0 |
| `deferred_arcane_catalog_entries_are_classified` | `PASS` | classified=0, deferred_arcane_entries=0 |
| `unknown_entry_rejected` | `PASS` | key=AUDIT_MISSING_RESEARCH |
| `legacy_resource_location_canonicalization` | `PASS` | uppercase/lowercase lookup |
| `client_cache_accepts_authoritative_views` | `PASS` | sample_present=true |
| `client_cache_stores_authoritative_revision` | `PASS` | revision=-1558952512 |
| `index_refresh_invalidates_entry_cache` | `PASS` | sample_present=true |
| `explicit_open_intent_is_separate_from_refresh` | `PASS` | open_once=true, refresh_open=false |
| `stale_action_revision_rejected_without_mutation` | `PASS` | candidate=FIRSTSTEPS |
| `legacy_start_action_semantics` | `PASS` | candidate=FIRSTSTEPS |
| `legacy_acknowledge_action_semantics` | `PASS` | candidate=FIRSTSTEPS |
| `legacy_known_entry_final_stage_auto_progression` | `PASS` | candidate=ALUMENTUM |

- Visible categories in empty-knowledge index: `1`
- Visible research entries in empty-knowledge index: `4`
- Entry views inspected: `4`
- Bookmarks inspected: `1`
- Pages inspected: `1`
- Ready crafting catalog entries: `79`
- Fake crafting catalog entries: `2`
- Deferred crafting catalog entries: `0`
- Ready arcane catalog entries: `89`
- Deferred arcane catalog entries: `0`
- Deferred arcane decorative/asset catalog entries: `0`
- Deferred arcane blockentity catalog entries: `0`
- Deferred arcane gameplay catalog entries: `0`
- Deferred arcane transport/essentia catalog entries: `0`
- Deferred arcane uncategorized catalog entries: `0`

## Fake crafting catalog ids

- `thaumcraft:salismundusfake`
- `thaumcraft:triplemeattreatfake`
