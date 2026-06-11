# Thaumcraft Aspect Dump Diff

- Legacy entries: `1798`
- Modern entries: `1987`
- Legacy comparison keys: `1537`
- Modern comparison keys: `1987`
- Comparable keys: `1139`
- Policy file: `D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\aspect_parity\input\legacy_to_modern_stack_map.json`
- Legacy key aliases configured: `82`
- Legacy stack-key aliases configured: `201`
- Legacy entries mapped through aliases: `283`

## Raw Summary

| Category | Count |
|---|---:|
| `IDENTICAL` | 1139 |
| `ORDER_ONLY_DIFF` | 0 |
| `AMOUNT_DIFF` | 0 |
| `ASPECT_SET_DIFF` | 0 |
| `NULL_EMPTY_DIFF` | 0 |
| `RESULT_KIND_DIFF` | 0 |
| `LEGACY_ONLY` | 398 |
| `MODERN_ONLY` | 848 |

## Classification Summary

| Classification | Count |
|---|---:|
| `EXPECTED_VERSION_FLATTENED_STACK` | 332 |
| `EXPECTED_VERSION_MODERN_ADDITION` | 93 |
| `LEGACY_ONLY_MAPPING_REVIEW` | 3 |
| `LEGACY_ONLY_THAUMCRAFT_UNPORTED` | 395 |
| `MODERN_ONLY_COMPONENT_POLICY_REVIEW` | 40 |
| `MODERN_ONLY_POLICY_REVIEW` | 383 |
| `PARITY_OK` | 856 |
| `PARITY_OK_LEGACY_TO_MODERN_MAP` | 283 |

## Root Cause Buckets

| Classification | Count | Root cause |
|---|---:|---|
| `LEGACY_ONLY_THAUMCRAFT_UNPORTED` | 395 | The legacy stack belongs to Thaumcraft content that is not fully registered or flattened in the NeoForge port yet. |
| `MODERN_ONLY_POLICY_REVIEW` | 383 | The stack exists only in Minecraft 1.21.1 or is exposed by a 1.21-only component/sample manifest. |
| `PARITY_OK_LEGACY_TO_MODERN_MAP` | 283 | Resolved runtime AspectList matches after an explicit legacy-to-modern id or metadata mapping. |
| `EXPECTED_VERSION_FLATTENED_STACK` | 252 | The stack is exposed as its own 1.21 id instead of the 1.12 registry-id/meta shape, or it was added after 1.12.2. |
| `EXPECTED_VERSION_FLATTENED_STACK` | 80 | Minecraft 1.21 exposes per-entity spawn egg ids; legacy 1.12 used a generic spawn_egg stack and Thaumcraft returned no aspects for it. |
| `EXPECTED_VERSION_MODERN_ADDITION` | 48 | The stack is a damageable item added after Minecraft 1.12.2, so there is no exact legacy runtime target. |
| `MODERN_ONLY_COMPONENT_POLICY_REVIEW` | 40 | 1.21.1 has additional potion or enchantment ids, or the sampled component form has no 1.12 equivalent. |
| `EXPECTED_VERSION_MODERN_ADDITION` | 38 | The enchantment id does not exist in Minecraft 1.12.2 or only exists under an explicitly mapped legacy name. |
| `EXPECTED_VERSION_MODERN_ADDITION` | 7 | The music disc id was added after Minecraft 1.12.2. |
| `LEGACY_ONLY_MAPPING_REVIEW` | 3 | The key exists only in the 1.12.2 dump. |

## First Actionable Port Gaps

- None.

## First Non-Identical Entries By Raw Category

### LEGACY_ONLY

- `damage:thaumcraft:cloth_boots:1` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:cloth_boots:162` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:cloth_boots:324` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:cloth_chest:1` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:cloth_chest:200` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:cloth_chest:399` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:cloth_legs:1` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:cloth_legs:187` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:cloth_legs:374` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_blade:1` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_blade:100` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_blade:199` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_boots:1` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_boots:194` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_boots:97` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_plate_chest:1` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_plate_chest:144` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_plate_chest:287` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_plate_helm:1` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_plate_helm:197` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_plate_helm:99` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_plate_legs:1` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_plate_legs:135` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_plate_legs:269` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`
- `damage:thaumcraft:crimson_praetor_chest:1` -> `LEGACY_ONLY_THAUMCRAFT_UNPORTED`

### MODERN_ONLY

- `damage:minecraft:brush:1` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:brush:32` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:brush:63` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:crossbow:1` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:crossbow:232` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:crossbow:464` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:mace:1` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:mace:250` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:mace:499` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_axe:1` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_axe:1015` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_axe:2030` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_boots:1` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_boots:240` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_boots:480` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_chestplate:1` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_chestplate:296` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_chestplate:591` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_helmet:1` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_helmet:203` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_helmet:406` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_hoe:1` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_hoe:1015` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_hoe:2030` -> `EXPECTED_VERSION_MODERN_ADDITION`
- `damage:minecraft:netherite_leggings:1` -> `EXPECTED_VERSION_MODERN_ADDITION`

