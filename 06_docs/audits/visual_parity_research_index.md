# Visual Parity Research Index

Publication date: 2026-11-05. This is a documentation consolidation, not a new gameplay or client audit.

## Start Here

The [consolidated visual-parity research](thaumcraft6_visual_parity_research_master_2026_11_03.md)
is the preserved master investigation. It contains D01-D13, H01-H10, N01-N16,
F01-F136, negative controls, proposed repair batches, and the original Russian
reports in Appendices A and B. Read the English findings before the archival appendices.

The master already has useful structure. Its original text is preserved byte-for-byte;
this index adds navigation and interpretation without rewriting historical evidence.

## Snapshot And Status

| Layer | Source | Meaning |
|---|---|---|
| Investigated code | `main` at `72eaac17a1e1c0c51b27cb151ef7f244944c89d5` | Baseline of the findings, not necessarily the code now on `dev` |
| Preserved research | [research branch at a1f39e81](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/a1f39e81cd83ef349b4cf7b4a45d6766942a06a9) | Source of this imported master; findings retain their original IDs |
| Source inventory follow-up | [V0 inventory at 66d23966](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/66d239663355bd433f960b48540ae6fd8d3f1603/06_docs/audits) | Separate registry/resource/GUI/FX evidence, not automatic resolution of master findings |
| Later implementation checkpoint | [remediation at b0c972a2](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/b0c972a2/06_docs/audits) | Later focused changes and evidence on another branch; not assumed present in `dev` |

Publication in `dev` does not merge implementation from these branches. A historical
finding is neither automatically still broken nor automatically fixed. Revalidate
its active code path against the target commit before implementing a repair.
Do not interpret the number of records as the number of unique confirmed bugs.

## Reading Routes

| Question | Where to read in the master |
|---|---|
| What was investigated, and how strong is the evidence? | Evidence and confidence levels; Scope and coverage |
| Where are the original symptoms and recovered details? | D01-D13; H01-H10 |
| Armor, shapes, selection, HUD and first verified differences | N01-N16 |
| Item icons, colors, model transforms and registration routes | F-series source-history checks; follow the individual finding headings |
| Block entities, devices, seals and custom model rendering | N-series and F-series; retain the distinction between model data, registration and runtime rendering |
| Entities, projectiles and effect-specific visuals | F63-F70 and F115-F136; positive controls are evidence too |
| Ambient particles, block events and FX lifecycle | F88-F114 |
| Why can black surfaces or borders have different causes? | Black borders: competing causes |
| How should fixes be grouped and verified? | Proposed remediation batches and consolidated verification; What remains before completion |
| Where is the unabridged Russian investigation? | Appendices A and B |

Use the master's existing table of contents for section links. Search by stable
finding ID when discussing or fixing a specific record.

## Related Evidence Without Duplicating Large Dumps

The V0 branch contains complementary curated summaries and generated inventories.
Keep their pinned source links rather than importing every JSON dump into this
documentation-only change:

- [V0 inventory checkpoint](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/66d239663355bd433f960b48540ae6fd8d3f1603/06_docs/audits/visual_v0_inventory_first_checkpoint.md).
- [Full item registry checkpoint](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/66d239663355bd433f960b48540ae6fd8d3f1603/06_docs/audits/visual_v0_full_item_registry_checkpoint_2026_11_03.md).
- [Full block registry checkpoint](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/66d239663355bd433f960b48540ae6fd8d3f1603/06_docs/audits/visual_v0_full_block_registry_checkpoint_2026_11_03.md).
- [Resource graph checkpoint](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/66d239663355bd433f960b48540ae6fd8d3f1603/06_docs/audits/visual_v0_resource_graph_first_pass_2026_11_03.md).
- [GUI, FX and HUD census](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/66d239663355bd433f960b48540ae6fd8d3f1603/06_docs/audits/visual_v0_gui_fx_hud_census_2026_11_03.md).

These are source-level records. They do not replace matched in-game captures.

## Extending The Investigation

Append new evidence without renumbering old IDs or silently replacing the source
baseline. Each follow-up should record:

| Required field | Content |
|---|---|
| Identity | Finding ID, subsystem and active registry/resource ID |
| Versions | Exact legacy source/jar identity and target port commit |
| Evidence | Source path, actual call chain, relevant constants and asset hashes |
| Reproduction | Matched scene, state, camera, GUI scale, lighting and graphics settings |
| Classification | Confirmed mismatch, probable cause, negative control, requires client, or not checked |
| Resolution | Fix commit plus affected build/server/client checks; leave unverified parts explicit |

For comparisons with other ports, record their exact commit and distinguish TC6
parity from deliberate TC4 content or author additions. A better-looking screenshot
is a useful lead, not permission to copy code or proof of equivalent gameplay.
Check source permissions before importing implementation or assets.

This index is not a second task queue. Priorities stay in
[CURRENT_TASK.md](../CURRENT_TASK.md); migration decisions remain governed by the
[migration guide](../migration/NeoForge_legacy_migration_guide.md) and
[migration matrix](../migration/migration_matrix.md).
