# Infusion Container Item Remainder Audit

Generated: 2026-06-19 16:57:25 +03:00

## Summary

| Metric | Count |
|---|---:|
| Infusion recipes scanned | 42 |
| Known container/remainder input hits | 5 |
| Tag-based inputs requiring future expansion check | 8 |

## Known container/remainder input hits

| Recipe id | Role | Item | Policy note | File |
|---|---|---|---|---|
| thaumcraft:jarbrain | component[2] | minecraft:water_bucket | returns empty bucket / special fluid container | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/jarbrain.json |
| thaumcraft:masksippingfiend | component[4] | minecraft:milk_bucket | returns empty bucket | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/masksippingfiend.json |
| thaumcraft:verdantheart | component[2] | minecraft:milk_bucket | returns empty bucket | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/verdantheart.json |
| thaumcraft:verdantheartlife | component[2] | minecraft:potion | returns glass bottle in many crafting contexts | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/verdantheartlife.json |
| thaumcraft:verdantheartsustain | component[2] | minecraft:potion | returns glass bottle in many crafting contexts | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/verdantheartsustain.json |

## Tag inputs

| Recipe id | Role | Tag | File |
|---|---|---|---|
| thaumcraft:charmundying | component[0] | c:plates/brass | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/charmundying.json |
| thaumcraft:maskangryghost | component[1] | c:plates/iron | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskangryghost.json |
| thaumcraft:maskangryghost | component[5] | c:plates/iron | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskangryghost.json |
| thaumcraft:maskgrinningdevil | component[1] | c:plates/iron | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskgrinningdevil.json |
| thaumcraft:maskgrinningdevil | component[5] | c:plates/iron | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskgrinningdevil.json |
| thaumcraft:masksippingfiend | component[1] | c:plates/iron | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/masksippingfiend.json |
| thaumcraft:masksippingfiend | component[5] | c:plates/iron | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/masksippingfiend.json |
| thaumcraft:sealbutcher | component[1] | minecraft:wool | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealbutcher.json |

## Porting conclusion

- Current infusion data includes inputs that need an explicit container/remainder policy before player-facing execution.
- This audit does not prove generic future safety. Re-run it when infusion recipes, tags, or accepted ingredient forms change.
- Real essentia network/source drain and container item parity remain separate concerns from the first audit-only executor boundary.