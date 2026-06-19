# Infusion Tag Input Expansion Audit

Generated: 2026-06-19 23:35:36 +03:00

## Summary

| Metric | Count |
|---|---:|
| Infusion tag input references | 8 |
| Tag references with locally expanded known remainder items | 0 |
| External or missing tag references | 1 |

## Expanded tag references

| Recipe id | Role | Tag | Status | Local items | Missing nested tags | File |
|---|---|---|---|---|---|---|
| thaumcraft:charmundying | component[0] | c:plates/brass | local | thaumcraft:brass_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/charmundying.json |
| thaumcraft:maskangryghost | component[1] | c:plates/iron | local | thaumcraft:iron_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskangryghost.json |
| thaumcraft:maskangryghost | component[5] | c:plates/iron | local | thaumcraft:iron_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskangryghost.json |
| thaumcraft:maskgrinningdevil | component[1] | c:plates/iron | local | thaumcraft:iron_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskgrinningdevil.json |
| thaumcraft:maskgrinningdevil | component[5] | c:plates/iron | local | thaumcraft:iron_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskgrinningdevil.json |
| thaumcraft:masksippingfiend | component[1] | c:plates/iron | local | thaumcraft:iron_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/masksippingfiend.json |
| thaumcraft:masksippingfiend | component[5] | c:plates/iron | local | thaumcraft:iron_plate |  | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/masksippingfiend.json |
| thaumcraft:sealbutcher | component[1] | minecraft:wool | external_or_missing |  | minecraft:wool | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealbutcher.json |

## Known remainder items found through local tag expansion

No known bucket/bottle/bowl-style remainder items were found through locally expanded infusion tag inputs.

## Porting conclusion

- Locally resolvable infusion tag inputs must not expand to unhandled container/remainder items.
- External or built-in tags still require runtime/pack validation when player-facing completion is enabled.
- Re-run this audit whenever local tag files, tag references, or accepted ingredient forms change.