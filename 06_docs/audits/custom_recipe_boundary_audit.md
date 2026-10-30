# Custom Recipe Boundary Audit

Generated: 2026-09-01 22:31:26 +03:00

## Summary

| Metric | Count |
|---|---:|
| Recipe JSON files scanned | 371 |
| Research JSON files scanned | 8 |
| Research recipe-like references found | 423 |
| Resolved recipe references | 247 |
| Missing recipe references | 176 |
| Custom/review recipe files | 0 |
| Blocked/review research recipe references | 0 |
| JSON parse errors | 0 |

## Boundary rule

- VANILLA_OR_STANDARD_READY means a normal Minecraft recipe type is already a data-only recipe.
- ARCANE_READY means the current Thaumcraft arcane recipe boundary is already implemented and audited.
- CRUCIBLE_PAGE_READY_NO_GAMEPLAY means the current Thaumcraft crucible recipe serializer/page snapshot boundary exists, but in-world crucible block behavior is still deferred.
- INFUSION_PAGE_READY_NO_GAMEPLAY means the current Thaumcraft infusion recipe serializer/page snapshot boundary exists, but in-world infusion altar behavior is still deferred.
- CUSTOM_BLOCKED_REQUIRES_DESIGN means fake, blueprint, special or similar custom behavior must not be copied directly from legacy classes. It needs a small serializer/page/behavior design slice first.
- THAUMCRAFT_CUSTOM_REVIEW means the recipe uses a Thaumcraft namespace type that is not recognized as the current arcane type and must be explicitly reviewed.

## Recipe class distribution

| Class | Count |
|---|---:|
| VANILLA_OR_STANDARD_READY | 164 |
| ARCANE_READY | 89 |
| CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 78 |
| INFUSION_PAGE_READY_NO_GAMEPLAY | 40 |

## Recipe type distribution

| Type | Count |
|---|---:|
| minecraft:crafting_shapeless | 92 |
| thaumcraft:arcane_shaped | 80 |
| thaumcraft:crucible | 78 |
| minecraft:crafting_shaped | 65 |
| thaumcraft:infusion | 40 |
| thaumcraft:arcane_shapeless | 9 |
| minecraft:smelting | 7 |

## Custom or review recipe files

No custom/review recipe JSON files were found in current data resources.

## Blocked or review research recipe references

No research recipe references currently resolve to blocked/review custom recipe JSON files.

## Missing recipe-like research references

| Reference | Research file | JSON path |
|---|---|---|
| minecraft:clay_ball | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].required_craft[0] |
| minecraft:dye | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].required_craft[2] |
| minecraft:glowstone_dust | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].required_craft[3] |
| minecraft:gunpowder | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].required_craft[0] |
| minecraft:lava_bucket | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].required_craft[3] |
| minecraft:slime_ball | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].required_craft[1] |
| minecraft:string | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].required_craft[1] |
| minecraft:web | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].required_craft[2] |
| thaumcraft:amber | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[10].stages[0].required_item[1] |
| thaumcraft:arcane_ear | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[6].icons[0] |
| thaumcraft:arcane_workbench | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].stages[0].required_craft[0] |
| thaumcraft:arcane_workbench_charger | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[20].icons[0] |
| thaumcraft:Banners | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[0].stages[0].recipes[0] |
| thaumcraft:bath_salts | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[7].icons[0] |
| thaumcraft:baubles_stuff | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[0].stages[0].recipes[1] |
| thaumcraft:bottle_taint | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[6].icons[0] |
| thaumcraft:brain | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[1].stages[0].required_item[0] |
| thaumcraft:brass_stuff | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[2].stages[1].recipes[1] |
| thaumcraft:brass_stuff | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[2].stages[2].recipes[1] |
| thaumcraft:causality_collapser | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[19].icons[0] |
| thaumcraft:charm_undying | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[16].icons[0] |
| thaumcraft:cloud_ring | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[13].icons[0] |
| thaumcraft:crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[9].stages[1].required_craft[0] |
| thaumcraft:crystal_aer | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[7].icons[0] |
| thaumcraft:crystal_aqua | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[7].icons[2] |
| thaumcraft:crystal_ignis | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[7].icons[1] |
| thaumcraft:crystal_ordo | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[7].icons[4] |
| thaumcraft:crystal_perditio | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[7].icons[5] |
| thaumcraft:crystal_terra | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[7].icons[3] |
| thaumcraft:curiosity_band | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[14].icons[0] |
| thaumcraft:elemental_axe | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].icons[0] |
| thaumcraft:elemental_hoe | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].icons[4] |
| thaumcraft:elemental_pick | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].icons[2] |
| thaumcraft:elemental_shovel | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].icons[3] |
| thaumcraft:elemental_sword | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].icons[1] |
| thaumcraft:essentia_input | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[19].icons[0] |
| thaumcraft:everfull_urn | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[20].icons[0] |
| thaumcraft:focus_pouch | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[21].icons[0] |
| thaumcraft:fortress_helm | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[11].icons[0] |
| thaumcraft:grapple_gun | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[18].icons[0] |
| thaumcraft:hand_mirror | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[9].icons[0] |
| thaumcraft:hungry_chest | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[1].icons[0] |
| thaumcraft:IEARCINGFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[5] |
| thaumcraft:IEBURROWINGFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[0] |
| thaumcraft:IECOLLECTORFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[1] |
| thaumcraft:IEDESTRUCTIVEFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[2] |
| thaumcraft:IEESSENCEFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[6] |
| thaumcraft:IELAMPLIGHTFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[7] |
| thaumcraft:IEREFININGFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[3] |
| thaumcraft:IESOUNDINGFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[4] |
| thaumcraft:infernal_furnace | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[3].icons[0] |
| thaumcraft:infusion_matrix | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[1].icons[0] |
| thaumcraft:infusion_matrix | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[1].stages[1].required_craft[0] |
| thaumcraft:inkwell | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[2].stages[0].recipes[1] |
| thaumcraft:inkwell | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[2].stages[1].recipes[1] |
| thaumcraft:jar_brain | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[1].icons[0] |
| thaumcraft:jar_normal | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[11].icons[0] |
| thaumcraft:lamp_arcane | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[11].icons[0] |
| thaumcraft:lamp_fertility | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[13].icons[0] |
| thaumcraft:lamp_growth | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[12].icons[0] |
| thaumcraft:leather | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[0].required_craft[1] |
| thaumcraft:log_greatwood | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[2].icons[0] |
| thaumcraft:matrix_cost | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[3].icons[0] |
| thaumcraft:matrix_speed | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[3].icons[1] |
| thaumcraft:mind | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[8].icons[0] |
| thaumcraft:mirror_essentia | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[10].icons[0] |
| thaumcraft:mirrored_glass | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[8].stages[0].required_item[1] |
| thaumcraft:module | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[24].icons[0] |
| thaumcraft:nitorgroup | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[0].stages[0].recipes[1] |
| thaumcraft:pattern_crafter | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[14].icons[0] |
| thaumcraft:paving_stone_barrier | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[6].icons[0] |
| thaumcraft:paving_stone_travel | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[6].icons[1] |
| thaumcraft:plate | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[5].icons[0] |
| thaumcraft:potion_sprayer | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[21].icons[0] |
| thaumcraft:primal_crusher | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[4].icons[0] |
| thaumcraft:primordial_pearl | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[8].icons[0] |
| thaumcraft:recharge_pedestal | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[18].icons[0] |
| thaumcraft:redstone_relay | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[5].icons[0] |
| thaumcraft:research_table | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[2].stages[0].required_craft[1] |
| thaumcraft:RunicArmorFake0 | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[10].stages[1].recipes[0] |
| thaumcraft:RunicArmorFake1 | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[10].stages[1].recipes[1] |
| thaumcraft:RunicArmorFake2 | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[10].stages[1].recipes[2] |
| thaumcraft:salismundusfake | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].stages[0].recipes[0] |
| thaumcraft:salismundusfake | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].stages[1].recipes[1] |
| thaumcraft:salismundusfake | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].stages[2].recipes[1] |
| thaumcraft:sanity_soap | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[8].icons[0] |
| thaumcraft:sapling_greatwood | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[6].icons[0] |
| thaumcraft:sapling_silverwood | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[6].icons[1] |
| thaumcraft:scribing_tools | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[2].stages[0].required_craft[0] |
| thaumcraft:seal | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[9].icons[0] |
| thaumcraft:smelter_aux | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[15].icons[0] |
| thaumcraft:smelter_basic | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[10].icons[0] |
| thaumcraft:smelter_basic | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[10].stages[2].required_craft[0] |
| thaumcraft:smelter_thaumium | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[13].icons[0] |
| thaumcraft:smelter_vent | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[16].icons[0] |
| thaumcraft:smelter_void | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[14].icons[0] |
| thaumcraft:spa | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[9].icons[0] |
| thaumcraft:stone_ancient | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[4].icons[0] |
| thaumcraft:stone_ancient | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[4].stages[0].required_item[0] |
| thaumcraft:stone_eldritch_tile | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[5].icons[0] |
| thaumcraft:stone_eldritch_tile | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[5].stages[0].required_item[0] |
| thaumcraft:tallow | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[0].required_craft[0] |
| thaumcraft:tallowcandles | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].recipes[1] |
| thaumcraft:tallowcandles | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].recipes[1] |
| thaumcraft:tallowcandles | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[3].recipes[1] |
| thaumcraft:textures/items/alumentum.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[1].icons[0] |
| thaumcraft:textures/items/bucket_death.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[5].icons[0] |
| thaumcraft:textures/items/cluster_iron.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[3].icons[0] |
| thaumcraft:textures/items/ingot_brass.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[2].icons[0] |
| thaumcraft:textures/items/seals/seal_breaker.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[22].icons[0] |
| thaumcraft:textures/items/seals/seal_butcher.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[19].icons[0] |
| thaumcraft:textures/items/seals/seal_empty.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[15].icons[0] |
| thaumcraft:textures/items/seals/seal_fill.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[14].icons[0] |
| thaumcraft:textures/items/seals/seal_guard.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[18].icons[0] |
| thaumcraft:textures/items/seals/seal_harvest.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[21].icons[0] |
| thaumcraft:textures/items/seals/seal_lumber.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[23].icons[0] |
| thaumcraft:textures/items/seals/seal_pickup.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[13].icons[0] |
| thaumcraft:textures/items/seals/seal_provider.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[16].icons[0] |
| thaumcraft:textures/items/seals/seal_stock.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[17].icons[0] |
| thaumcraft:textures/items/seals/seal_use.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[20].icons[0] |
| thaumcraft:textures/items/tallow.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].icons[0] |
| thaumcraft:textures/items/thaumonomicon.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].icons[0] |
| thaumcraft:textures/misc/golem/addon_fighter.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[28].icons[0] |
| thaumcraft:textures/misc/golem/arms_breakers.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[27].icons[0] |
| thaumcraft:textures/misc/golem/head_basic.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[10].icons[0] |
| thaumcraft:textures/misc/golem/legs_climber.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[25].icons[0] |
| thaumcraft:textures/misc/golem/legs_flyer.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[26].icons[0] |
| thaumcraft:textures/research/cat_alchemy.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[0].icons[0] |
| thaumcraft:textures/research/cat_alchemy.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[9].icons[0] |
| thaumcraft:textures/research/cat_artifice.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[0].icons[0] |
| thaumcraft:textures/research/cat_artifice.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[11].icons[0] |
| thaumcraft:textures/research/cat_auromancy.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].icons[0] |
| thaumcraft:textures/research/cat_auromancy.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[10].icons[0] |
| thaumcraft:textures/research/cat_eldritch.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[15].icons[0] |
| thaumcraft:textures/research/cat_golemancy.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[13].icons[0] |
| thaumcraft:textures/research/cat_golemancy.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[0].icons[0] |
| thaumcraft:textures/research/cat_infusion.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[12].icons[0] |
| thaumcraft:textures/research/cat_infusion.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[0].icons[0] |
| thaumcraft:textures/research/golem_logistics.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[11].icons[0] |
| thaumcraft:textures/research/knowledge_theory.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[1].icons[0] |
| thaumcraft:textures/research/knowledge_theory.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[2].icons[0] |
| thaumcraft:textures/research/r_angryzombie.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/scans.json | $.entries[9].icons[0] |
| thaumcraft:textures/research/r_celestial.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[16].icons[0] |
| thaumcraft:textures/research/r_crab.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/scans.json | $.entries[11].icons[0] |
| thaumcraft:textures/research/r_cultist.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/scans.json | $.entries[8].icons[0] |
| thaumcraft:textures/research/r_eldritchguardian.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/scans.json | $.entries[10].icons[0] |
| thaumcraft:textures/research/r_firebat.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/scans.json | $.entries[3].icons[0] |
| thaumcraft:textures/research/r_flux.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[3].icons[0] |
| thaumcraft:textures/research/r_fluxrift.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[18].icons[0] |
| thaumcraft:textures/research/r_inf_enchant.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].icons[0] |
| thaumcraft:textures/research/r_mask0.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[12].icons[0] |
| thaumcraft:textures/research/r_mask1.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[12].icons[1] |
| thaumcraft:textures/research/r_mask2.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[12].icons[2] |
| thaumcraft:textures/research/r_pech.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/scans.json | $.entries[1].icons[0] |
| thaumcraft:textures/research/r_runicupg.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[10].icons[0] |
| thaumcraft:textures/research/r_taintacle.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/scans.json | $.entries[6].icons[0] |
| thaumcraft:textures/research/r_taintcrawler.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/scans.json | $.entries[5].icons[0] |
| thaumcraft:textures/research/r_taintseed.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/scans.json | $.entries[4].icons[0] |
| thaumcraft:textures/research/r_taintswarm.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/scans.json | $.entries[7].icons[0] |
| thaumcraft:textures/research/r_thaumslime.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/scans.json | $.entries[2].icons[0] |
| thaumcraft:textures/research/r_warp.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[4].icons[0] |
| thaumcraft:textures/research/r_wisp.png | 05_neoforge_port/src/main/resources/data/thaumcraft/research/scans.json | $.entries[0].icons[0] |
| thaumcraft:thaumatorium | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[18].icons[0] |
| thaumcraft:Thaumatorium | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[18].stages[1].recipes[0] |
| thaumcraft:thaumium_stuff | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[2].stages[2].recipes[3] |
| thaumcraft:traveller_boots | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[7].icons[0] |
| thaumcraft:triplemeattreatfake | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[3].stages[1].recipes[1] |
| thaumcraft:verdant_charm | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[15].icons[0] |
| thaumcraft:vis_battery | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[22].icons[0] |
| thaumcraft:vis_generator | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[19].icons[0] |
| thaumcraft:viscrystalgroup | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[0].stages[0].recipes[2] |
| thaumcraft:void_robe_helm | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[3].icons[0] |
| thaumcraft:void_seed | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[0].icons[0] |
| thaumcraft:void_siphon | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[1].icons[0] |
| thaumcraft:void_stuff | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[0].stages[1].recipes[1] |
| thaumcraft:voidseer_charm | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[2].icons[0] |

## Custom recipe keyword hits in data resources

| Keyword | File |
|---|---|
| blueprint | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/golempress.json |
| blueprint | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/infusionaltar.json |
| blueprint | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/infusionaltarancient.json |
| blueprint | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/infusionaltareldritch.json |
| blueprint | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/artifice_behavior_page_recipes.json |
| blueprint | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/blueprint_page_placeholders.json |
| blueprint | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/remaining_nonfake_page_recipes.json |
| crucible | 05_neoforge_port/src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/loot_table/blocks/crucible.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/alumentum.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bathsalts.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bottletaint.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/brassingot.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/essentiasmelter.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/everfullurn.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/focus_1.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/hedge_clay.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/hedge_dye.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/hedge_glowstone.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/hedge_gunpowder.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/hedge_lava.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/hedge_leather.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/hedge_slime.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/hedge_string.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/hedge_tallow.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/hedge_web.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/liquiddeath.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/metal_purification_cinnabar.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/metal_purification_copper.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/metal_purification_gold.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/metal_purification_iron.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/metal_purification_lead.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/metal_purification_silver.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/metal_purification_tin.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/nitor.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/research_bridge/crucible.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sanesoap.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealbreakadv.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealcollect.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealcollectadv.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealempty.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealemptyadv.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealguard.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealguardadv.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/seallumber.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealprovide.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealstock.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealstore.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealstoreadv.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealuse.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/thaumiumingot.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_aer.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_alienis.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_alkimia.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_aqua.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_auram.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_aversio.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_bestia.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_cognitio.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_desiderium.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_exanimis.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_fabrico.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_gelum.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_herba.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_humanus.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_ignis.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_instrumentum.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_lux.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_machina.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_metallum.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_mortuus.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_motus.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_ordo.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_perditio.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_permutatio.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_potentia.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_praecantatio.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_praemunio.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_sensus.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_spiritus.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_tenebrae.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_terra.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_vacuos.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_victus.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_vinculum.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_vitium.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_vitreus.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/vis_crystal_volatus.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/voidingot.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/auromancy_focus_recipes.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/golemancy_seal_crucible.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/hedge_alchemy.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/metal_purification.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/remaining_nonfake_page_recipes.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/special_alchemy.json |
| crucible | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json |
| CrucibleRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/auromancy_focus_recipes.json |
| CrucibleRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/golemancy_seal_crucible.json |
| CrucibleRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/hedge_alchemy.json |
| CrucibleRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json |
| CrucibleRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/metal_purification.json |
| CrucibleRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/remaining_nonfake_page_recipes.json |
| CrucibleRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/special_alchemy.json |
| fake | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json |
| fake | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json |
| fake | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json |
| fake | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json |
| infusion | 05_neoforge_port/src/main/resources/data/minecraft/tags/block/mineable/pickaxe.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/loot_table/blocks/infusion_matrix.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/ancientpedestal.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/arcanebore.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/arcanepedestal.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannerblack.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannerblue.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannerbrown.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannercyan.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannergray.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannergreen.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannerlightblue.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannerlime.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannermagenta.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannerorange.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannerpink.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannerpurple.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannerred.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannersilver.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bannerwhite.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/banneryellow.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/bootstraveller.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/causalitycollapser.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/charmundying.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/cloudring.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/curiosityband.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/eldritchpedestal.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/elementalaxe.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/elementalhoe.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/elementalpick.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/elementalshovel.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/elementalsword.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/enchantedfabric.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/focus_2.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/focus_3.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/helmgoggles.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/infusionaltar.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/infusionaltarancient.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/infusionaltareldritch.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/infusionmatrix.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/jarbrain.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/lampfertility.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/lampgrowth.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskangryghost.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/maskgrinningdevil.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/masksippingfiend.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/matrixcost.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/matrixmotion.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/mindbiothaumic.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/mirror.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/mirroressentia.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/mirrorhand.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/primalcrusher.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/redstoneinlay.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/research_bridge/infusion_matrix.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/robeboots.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/robechest.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/robelegs.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealbreak.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealbutcher.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/sealharvest.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/stabilizer.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/thaumiumfortresschest.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/thaumiumfortresshelm.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/thaumiumfortresslegs.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/verdantheart.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/verdantheartlife.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/verdantheartsustain.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/visamulet.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/voidrobechest.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/voidrobehelm.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/voidrobelegs.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/voidseerpearl.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/recipe/voidsiphon.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/artifice_behavior_page_recipes.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/auromancy_focus_recipes.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/blueprint_page_placeholders.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/eldritch_infusion_recipes.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/golemancy_infusion_first.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/infusion_elemental_tools.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/infusion_fortress_armor.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/infusion_fortress_masks.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/infusion_utility_first.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/infusion_verdant_charms.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/remaining_nonfake_page_recipes.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/simple_legacy_page_recipes.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json |
| infusion | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json |
| InfusionRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/artifice_behavior_page_recipes.json |
| InfusionRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/auromancy_focus_recipes.json |
| InfusionRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/eldritch_infusion_recipes.json |
| InfusionRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/golemancy_infusion_first.json |
| InfusionRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/infusion_elemental_tools.json |
| InfusionRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/infusion_fortress_armor.json |
| InfusionRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/infusion_fortress_masks.json |
| InfusionRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/infusion_utility_first.json |
| InfusionRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/infusion_verdant_charms.json |
| InfusionRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/remaining_nonfake_page_recipes.json |
| InfusionRecipe | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/simple_legacy_page_recipes.json |
| ShapedArcane | 05_neoforge_port/src/main/resources/data/thaumcraft/research_page_catalog/legacy_builtin.json |

## Next implementation guidance

1. Do not implement fake, blueprint, special or remaining custom recipe behavior by copying legacy recipe classes directly; crucible and infusion currently have serializer/page boundaries only.
2. Pick the most referenced blocked custom type from this audit as the next serializer/page snapshot slice.
3. Keep machine behavior, inventory behavior, essentia networks and rendering deferred until the serializer/page boundary has its own audit coverage.
4. Keep server smoke and build green after every expansion.
