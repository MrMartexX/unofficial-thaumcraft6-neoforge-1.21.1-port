# Infusion Container Item Remainder Audit

Generated: 2026-06-19 23:33:09 +03:00

## Summary

| Metric | Count |
|---|---:|
| Infusion recipes scanned | 42 |
| Component-side known remainder inputs handled by current policy | 5 |
| Catalyst-side known remainder inputs still blocked | 0 |
| Tag-based inputs requiring future expansion check | 8 |

## Component-side known remainder inputs handled by current policy

| Recipe id | Role | Item | Policy note | File |
|---|---|---|---|---|
| thaumcraft:jarbrain | component[2] | minecraft:water_bucket | handled component remainder: bucket | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/jarbrain.json |
| thaumcraft:masksippingfiend | component[4] | minecraft:milk_bucket | handled component remainder: bucket | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/masksippingfiend.json |
| thaumcraft:verdantheart | component[2] | minecraft:milk_bucket | handled component remainder: bucket | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/verdantheart.json |
| thaumcraft:verdantheartlife | component[2] | minecraft:potion | handled component remainder: glass_bottle | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/verdantheartlife.json |
| thaumcraft:verdantheartsustain | component[2] | minecraft:potion | handled component remainder: glass_bottle | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/verdantheartsustain.json |

## Catalyst-side known remainder inputs still blocked

No known catalyst-side remainder inputs were found in current infusion data.

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

- Current infusion data has no known catalyst-side container/remainder input blockers.
- Current component-side known bucket/bottle/bowl-style remainder inputs are covered by the audit-only component remainder policy.
- Tag inputs still require future expansion checks whenever tags or tag membership change.
- Player-facing infusion completion remains disabled until real essentia source and final catalyst/output policies are implemented.