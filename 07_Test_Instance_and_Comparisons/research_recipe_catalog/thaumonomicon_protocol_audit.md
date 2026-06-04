# Thaumonomicon Protocol Foundation Audit

| Check | Status | Detail |
|---|---|---|
| `index_has_visible_category` | `PASS` | count=1 |
| `index_has_visible_entry` | `PASS` | count=4 |
| `category_visibility_server_filtered` | `PASS` | visible=1 |
| `entry_visibility_server_filtered` | `PASS` | visible=4 |
| `unlockable_state_server_owned` | `PASS` | visible=4 |
| `research_flags_server_owned` | `PASS` | visible=4 |
| `visible_entry_view_available` | `PASS` | inspected=4 |
| `legacy_missing_pages_filtered` | `PASS` | pages=1 |
| `unknown_entry_rejected` | `PASS` | key=AUDIT_MISSING_RESEARCH |
| `legacy_resource_location_canonicalization` | `PASS` | uppercase/lowercase lookup |
| `client_cache_accepts_authoritative_views` | `PASS` | sample_present=true |
| `index_refresh_invalidates_entry_cache` | `PASS` | sample_present=true |
| `legacy_start_action_semantics` | `PASS` | candidate=FIRSTSTEPS |
| `legacy_acknowledge_action_semantics` | `PASS` | candidate=FIRSTSTEPS |
| `legacy_known_entry_final_stage_auto_progression` | `PASS` | candidate=ALUMENTUM |

- Visible categories in empty-knowledge index: `1`
- Visible research entries in empty-knowledge index: `4`
- Entry views inspected: `4`
- Bookmarks inspected: `1`
- Pages inspected: `1`
