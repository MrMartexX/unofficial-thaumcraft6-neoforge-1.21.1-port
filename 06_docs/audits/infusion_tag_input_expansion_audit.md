# Infusion Tag Input Expansion Audit

Generated: 2026-06-19 23:42:51 +03:00

## Summary

| Metric | Count |
|---|---:|
| Infusion tag input references | 8 |
| Tag references with locally expanded known remainder items | 0 |
| External or missing tag references | 0 |
| Built-in fallback tag references | 1 |

## Expanded tag references

| Recipe id | Role | Tag | Status | Expanded items | Missing nested tags | File |
|---|---|---|---|---|---|---|
| thaumcraft:charmundying | component[0] | c:plates/brass | local | thaumcraft:brass_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/charmundying.json |
| thaumcraft:maskangryghost | component[1] | c:plates/iron | local | thaumcraft:iron_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskangryghost.json |
| thaumcraft:maskangryghost | component[5] | c:plates/iron | local | thaumcraft:iron_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskangryghost.json |
| thaumcraft:maskgrinningdevil | component[1] | c:plates/iron | local | thaumcraft:iron_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskgrinningdevil.json |
| thaumcraft:maskgrinningdevil | component[5] | c:plates/iron | local | thaumcraft:iron_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskgrinningdevil.json |
| thaumcraft:masksippingfiend | component[1] | c:plates/iron | local | thaumcraft:iron_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/masksippingfiend.json |
| thaumcraft:masksippingfiend | component[5] | c:plates/iron | local | thaumcraft:iron_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/masksippingfiend.json |
| thaumcraft:sealbutcher | component[1] | minecraft:wool | builtin_fallback | minecraft:white_wool, minecraft:orange_wool, minecraft:magenta_wool, minecraft:light_blue_wool, minecraft:yellow_wool, minecraft:lime_wool, minecraft:pink_wool, minecraft:gray_wool, minecraft:light_gray_wool, minecraft:cyan_wool, minecraft:purple_wool, minecraft:blue_wool, minecraft:brown_wool, minecraft:green_wool, minecraft:red_wool, minecraft:black_wool |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealbutcher.json |

## Known remainder items found through tag expansion

No known bucket/bottle/bowl-style remainder items were found through local or built-in fallback tag expansion.

## Porting conclusion

- Locally resolvable and known built-in fallback infusion tag inputs do not currently expand to known bucket/bottle/bowl-style remainder items.
- External tags still require runtime/pack validation if new tag namespaces are introduced.
- Re-run this audit whenever local tag files, tag references, accepted ingredient forms, or built-in fallback assumptions change.