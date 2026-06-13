# Shobie 1.20.1 Unsafe Full Merge Pass

Branch: `codex/experiment-shobie-1-20-merge`

This document records the experimental unsafe merge pass requested after the
safe Shobie data merge. The goal is to keep the 1.21.1 port buildable while
placing the remaining Shobie content in the repo for inspection and targeted
subsystem porting.

## Imported Runtime Data

The previously deferred Shobie recipe rows are now represented under
`05_neoforge_port/src/main/resources/data/thaumcraft/recipe/unsafe_shobie/`.

| Type | Count | Runtime treatment |
|---|---:|---|
| `thaumcraft:arcane_workbench_shaped` | 1 | Translated to `thaumcraft:arcane_shaped`; Shobie id kept under `unsafe_shobie/arcane`. |
| `thaumcraft:crucible` | 14 | Translated to the current `thaumcraft:crucible` serializer. Legacy seal NBT identity is flattened to `blank_seal` until a real seal DataComponent exists. |
| `thaumcraft:infusion` | 24 | Translated to the current `thaumcraft:infusion` serializer. Legacy `center`, `centerItem`, `input`, and `ingredients` fields are normalized to `central` and `components`. |

Catalog state after this pass:

- `0` Shobie recipe rows remain in `defer_behavior_or_unconfirmed_mapping`.
- `39` rows are marked `imported_unsafe_translated_current_serializer`.
- The unsafe recipe ids are separated from legacy-correct ids so they do not
  overwrite the validated TC6 recipe set.

## Translation Corrections

The active unsafe recipes are not raw Shobie files. They were normalized for the
current NeoForge 1.21.1 data model:

- `thaumcraft:arcane_workbench_shaped` -> `thaumcraft:arcane_shaped`;
- `item` result keys -> `id` result keys;
- `center`, `centerItem`, `input` -> `central`;
- `ingredients` -> `components`;
- `forge:*` tags -> current `c:*` tags where a clear bridge exists;
- Shobie shard ids such as `shard_air` -> current crystal ids such as
  `crystal_aer`;
- Shobie-only `visum` aspect -> TC6 `sensus`.

The exact dropped or mapped fields are listed in
`06_docs/shobie_1_20_unsafe_recipe_translation_notes.txt`.

## Reference-Only Import

The full Shobie source/resource corpus was copied to
`05_neoforge_port/src/shobieReference/`.

| Reference area | Count | Status |
|---|---:|---|
| Java files | 744 | Reference-only; not compiled. |
| Resource files | 2447 | Reference-only; not processed as runtime resources. |
| Worldgen JSON | 41 | Reference-only pending 1.21.1 worldgen design. |
| Biome modifiers | 13 | Reference-only pending NeoForge 1.21.1 conversion. |

This follows the migration guide rule that legacy/old-platform implementation
code must not be mass-copied into active 1.21.1 code. The reference tree exists
to speed comparison, not to define runtime behavior.

## Known Unsafe Semantics

| Area | Current unsafe behavior | Required real port work |
|---|---|---|
| Seal recipes | Seal NBT variants are flattened to `blank_seal`. Multiple recipes can therefore share the same plain catalyst/output. | Add seal DataComponents, seal item variant display, and exact catalyst/output matching. |
| Infusion variants | Shobie alternate recipes are loaded as separate unsafe ids and do not prove legacy parity. | Implement Infusion Matrix, pedestal scan, stability, aspect consumption, instability and FX. |
| Crucible behavior | Unsafe recipes only load as data. They do not prove in-world crucible behavior. | Implement crucible BlockEntity, heat/water/catalyst loop, pollution/flux and FX. |
| Worldgen | Raw Shobie worldgen is present only as reference. | Convert configured/placed features and biome modifiers with a dedicated 1.21.1 design. |
| Java behavior | Raw Shobie Java is present only as reference. | Port subsystem-by-subsystem using current registries, components, menus, payloads and render APIs. |

## Validation Target

After this pass, the branch must still pass:

```powershell
cd D:\Thaumcraft_6_port_to_1.21.1\05_neoforge_port
.\gradlew.bat build --no-daemon
.\gradlew.bat runServer --no-daemon
.\gradlew.bat runClient --no-daemon
```

The server reload should validate the unsafe recipe JSON without unresolved
research references or recipe codec failures. Client warnings from reference-only
files are not expected, because `src/shobieReference` is not a runtime resource
source set.
