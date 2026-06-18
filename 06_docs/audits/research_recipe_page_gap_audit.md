# Research Recipe Page Gap Audit

Generated: 2026-06-18 14:43:15 +03:00

## Summary

| Metric | Count |
|---|---:|
| Recipe JSON files scanned | 272 |
| Research JSON files scanned | 8 |
| Stage/addendum recipe page references | 253 |
| Resolved recipe page references | 160 |
| Missing recipe page references | 93 |
| Required craft references | 29 |
| Required item references | 30 |
| Icon references | 156 |

## Missing recipe page references by class

| Class | Count |
|---|---:|
| GOLEMANCY_PAGE_DEFERRED | 19 |
| FAKE_OR_SYNTHETIC_PAGE | 15 |
| INFUSION_PAGE_DEFERRED | 14 |
| INFUSION_RESEARCH_LEGACY_PAGE_KEY | 11 |
| LEGACY_PAGE_KEY_OR_MISSING_RECIPE | 9 |
| AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | 8 |
| ARTIFICE_BEHAVIOR_PAGE_DEFERRED | 7 |
| ELDRITCH_PAGE_DEFERRED | 7 |
| ALCHEMY_RESEARCH_LEGACY_PAGE_KEY | 3 |

## Missing recipe page references by research file

| Research file | Count |
|---|---:|
| 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | 36 |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | 19 |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | 12 |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | 8 |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | 8 |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | 7 |
| 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | 3 |

## Resolved recipe page references

| Reference | Recipe type | Class | Research file | JSON path |
|---|---|---|---|---|
| thaumcraft:activatorrail | thaumcraft:arcane_shapeless | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[15].stages[1].recipes[1] |
| thaumcraft:AdvAlchemyConstruct | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[14].stages[0].recipes[0] |
| thaumcraft:AdvAlchemyConstruct | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[14].stages[1].recipes[0] |
| thaumcraft:AdvancedCrossbow | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[16].stages[1].recipes[0] |
| thaumcraft:AlchemicalConstruct | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[12].stages[1].recipes[7] |
| thaumcraft:Alembic | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[10].stages[3].recipes[1] |
| thaumcraft:AncientPedestal | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[4].stages[1].recipes[0] |
| thaumcraft:ArcaneEar | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[6].stages[1].recipes[0] |
| thaumcraft:ArcaneLamp | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[11].stages[1].recipes[0] |
| thaumcraft:ArcanePedestal | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[1].stages[2].recipes[1] |
| thaumcraft:ArcaneSpa | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[9].stages[1].recipes[0] |
| thaumcraft:AutomatedCrossbow | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[15].stages[1].recipes[0] |
| thaumcraft:Bellows | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[4].stages[1].recipes[0] |
| thaumcraft:caster_basic | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[0].recipes[1] |
| thaumcraft:caster_basic | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[1].recipes[1] |
| thaumcraft:caster_basic | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[10].stages[2].recipes[1] |
| thaumcraft:caster_basic | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[10].stages[1].recipes[1] |
| thaumcraft:caster_basic | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[2].recipes[1] |
| thaumcraft:Centrifuge | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[17].stages[1].recipes[0] |
| thaumcraft:Condenser | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[17].stages[1].recipes[0] |
| thaumcraft:CondenserLattice | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[17].stages[1].recipes[1] |
| thaumcraft:dioptra | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[7].stages[1].recipes[0] |
| thaumcraft:EldritchPedestal | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[5].stages[1].recipes[0] |
| thaumcraft:EnchantedFabric | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[12].stages[1].recipes[0] |
| thaumcraft:EssentiaSmelter | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[10].stages[2].recipes[0] |
| thaumcraft:EssentiaSmelter | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[10].stages[3].recipes[0] |
| thaumcraft:EssentiaSmelterThaumium | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[13].stages[1].recipes[0] |
| thaumcraft:EssentiaSmelterVoid | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[14].stages[1].recipes[1] |
| thaumcraft:EssentiaTransportIn | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[19].stages[1].recipes[0] |
| thaumcraft:EssentiaTransportOut | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[19].stages[1].recipes[1] |
| thaumcraft:Filter | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[0].stages[0].recipes[4] |
| thaumcraft:FocusPouch | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[21].stages[1].recipes[0] |
| thaumcraft:Goggles | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[0].stages[0].recipes[0] |
| thaumcraft:Goggles | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[11].stages[1].recipes[0] |
| thaumcraft:GrappleGun | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[18].stages[1].recipes[2] |
| thaumcraft:GrappleGunSpool | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[18].stages[1].recipes[1] |
| thaumcraft:GrappleGunTip | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[18].stages[1].recipes[0] |
| thaumcraft:HungryChest | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[1].stages[1].recipes[0] |
| thaumcraft:InfusionMatrix | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[1].stages[2].recipes[0] |
| thaumcraft:InfusionMatrix | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[1].stages[1].recipes[0] |
| thaumcraft:JarVoid | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[11].stages[0].recipes[1] |
| thaumcraft:Levitator | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[2].stages[1].recipes[0] |
| thaumcraft:MatrixCost | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[3].stages[1].recipes[1] |
| thaumcraft:MatrixMotion | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[3].stages[1].recipes[0] |
| thaumcraft:mechanism_complex | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[0].stages[0].recipes[2] |
| thaumcraft:mechanism_simple | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[0].stages[0].recipes[1] |
| thaumcraft:MindClockwork | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[8].stages[2].recipes[0] |
| thaumcraft:MindClockwork | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[8].stages[1].recipes[0] |
| thaumcraft:mirrorglass | thaumcraft:arcane_shapeless | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[0].stages[0].recipes[3] |
| thaumcraft:MnemonicMatrix | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[18].stages[1].recipes[1] |
| thaumcraft:modaggression | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[18].stages[1].recipes[1] |
| thaumcraft:modvision | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[24].stages[1].recipes[0] |
| thaumcraft:MorphicResonator | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[0].stages[0].recipes[5] |
| thaumcraft:patterncrafter | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[14].stages[1].recipes[0] |
| thaumcraft:PaveBarrier | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[6].stages[1].recipes[0] |
| thaumcraft:PaveTravel | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[6].stages[1].recipes[1] |
| thaumcraft:PotionSprayer | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[21].stages[1].recipes[0] |
| thaumcraft:rechargepedestal | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[18].stages[1].recipes[0] |
| thaumcraft:RedstoneInlay | thaumcraft:arcane_shapeless | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[2].stages[1].recipes[0] |
| thaumcraft:RedstoneRelay | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[5].stages[1].recipes[0] |
| thaumcraft:Resonator | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[12].stages[1].recipes[0] |
| thaumcraft:RobeBoots | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[12].stages[1].recipes[3] |
| thaumcraft:RobeChest | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[12].stages[1].recipes[1] |
| thaumcraft:RobeLegs | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[12].stages[1].recipes[2] |
| thaumcraft:sanitychecker | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[4].stages[0].recipes[0] |
| thaumcraft:SealBlank | thaumcraft:arcane_shapeless | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[9].stages[0].recipes[1] |
| thaumcraft:SmelterAux | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[15].stages[1].recipes[0] |
| thaumcraft:SmelterVent | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[16].stages[1].recipes[0] |
| thaumcraft:Stabilizer | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[2].stages[1].recipes[1] |
| thaumcraft:thaumometer | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].stages[2].recipes[0] |
| thaumcraft:thaumometer | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].stages[1].recipes[0] |
| thaumcraft:Tube | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[12].stages[1].recipes[1] |
| thaumcraft:TubeBuffer | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[12].stages[1].recipes[6] |
| thaumcraft:TubeFilter | thaumcraft:arcane_shapeless | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[12].stages[1].recipes[3] |
| thaumcraft:TubeOneway | thaumcraft:arcane_shapeless | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[12].stages[1].recipes[5] |
| thaumcraft:TubeRestrict | thaumcraft:arcane_shapeless | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[12].stages[1].recipes[4] |
| thaumcraft:TubeValve | thaumcraft:arcane_shapeless | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[12].stages[1].recipes[2] |
| thaumcraft:vis_resonator | thaumcraft:arcane_shapeless | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[2].recipes[0] |
| thaumcraft:vis_resonator | thaumcraft:arcane_shapeless | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[0].recipes[0] |
| thaumcraft:vis_resonator | thaumcraft:arcane_shapeless | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[1].recipes[0] |
| thaumcraft:vis_resonator | thaumcraft:arcane_shapeless | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[10].stages[1].recipes[0] |
| thaumcraft:vis_resonator | thaumcraft:arcane_shapeless | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[10].stages[2].recipes[0] |
| thaumcraft:VisBattery | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[22].stages[1].recipes[0] |
| thaumcraft:VisGenerator | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[19].stages[1].recipes[0] |
| thaumcraft:wand_workbench | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[2].recipes[4] |
| thaumcraft:wand_workbench | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[1].recipes[4] |
| thaumcraft:WardedJar | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[11].stages[0].recipes[0] |
| thaumcraft:workbenchcharger | thaumcraft:arcane_shaped | ARCANE_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[20].stages[1].recipes[0] |
| thaumcraft:Banners | research_page_catalog:group | CATALOG_GROUP_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[0].stages[0].recipes[0] |
| thaumcraft:baubles_stuff | research_page_catalog:group | CATALOG_GROUP_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[0].stages[0].recipes[1] |
| thaumcraft:brass_stuff | research_page_catalog:group | CATALOG_GROUP_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[2].stages[1].recipes[1] |
| thaumcraft:brass_stuff | research_page_catalog:group | CATALOG_GROUP_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[2].stages[2].recipes[1] |
| thaumcraft:inkwell | research_page_catalog:group | CATALOG_GROUP_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[2].stages[0].recipes[1] |
| thaumcraft:inkwell | research_page_catalog:group | CATALOG_GROUP_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[2].stages[1].recipes[1] |
| thaumcraft:nitorgroup | research_page_catalog:group | CATALOG_GROUP_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[0].stages[0].recipes[1] |
| thaumcraft:tallowcandles | research_page_catalog:group | CATALOG_GROUP_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[3].recipes[1] |
| thaumcraft:tallowcandles | research_page_catalog:group | CATALOG_GROUP_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].recipes[1] |
| thaumcraft:tallowcandles | research_page_catalog:group | CATALOG_GROUP_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].recipes[1] |
| thaumcraft:thaumium_stuff | research_page_catalog:group | CATALOG_GROUP_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[2].stages[2].recipes[3] |
| thaumcraft:viscrystalgroup | research_page_catalog:group | CATALOG_GROUP_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[0].stages[0].recipes[2] |
| thaumcraft:void_stuff | research_page_catalog:group | CATALOG_GROUP_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[0].stages[1].recipes[1] |
| thaumcraft:alumentum | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[1].stages[1].recipes[0] |
| thaumcraft:BathSalts | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[7].stages[1].recipes[0] |
| thaumcraft:BottleTaint | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[6].stages[0].recipes[0] |
| thaumcraft:brassingot | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[2].stages[1].recipes[0] |
| thaumcraft:brassingot | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[2].stages[2].recipes[0] |
| thaumcraft:brassingot | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[2].stages[0].recipes[0] |
| thaumcraft:hedge_clay | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].recipes[7] |
| thaumcraft:hedge_clay | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[3].recipes[7] |
| thaumcraft:hedge_dye | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[3].recipes[5] |
| thaumcraft:hedge_dye | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].recipes[5] |
| thaumcraft:hedge_dye | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].recipes[5] |
| thaumcraft:hedge_glowstone | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].recipes[6] |
| thaumcraft:hedge_glowstone | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[3].recipes[6] |
| thaumcraft:hedge_glowstone | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].recipes[6] |
| thaumcraft:hedge_gunpowder | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].recipes[3] |
| thaumcraft:hedge_gunpowder | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[3].recipes[3] |
| thaumcraft:hedge_gunpowder | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].recipes[3] |
| thaumcraft:hedge_lava | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].recipes[10] |
| thaumcraft:hedge_lava | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[3].recipes[10] |
| thaumcraft:hedge_leather | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[3].recipes[2] |
| thaumcraft:hedge_leather | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].recipes[2] |
| thaumcraft:hedge_leather | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[0].recipes[1] |
| thaumcraft:hedge_leather | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].recipes[2] |
| thaumcraft:hedge_slime | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].recipes[4] |
| thaumcraft:hedge_slime | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].recipes[4] |
| thaumcraft:hedge_slime | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[3].recipes[4] |
| thaumcraft:hedge_string | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[3].recipes[8] |
| thaumcraft:hedge_string | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].recipes[8] |
| thaumcraft:hedge_tallow | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[3].recipes[0] |
| thaumcraft:hedge_tallow | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].recipes[0] |
| thaumcraft:hedge_tallow | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].recipes[0] |
| thaumcraft:hedge_tallow | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[0].recipes[0] |
| thaumcraft:hedge_web | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].recipes[9] |
| thaumcraft:hedge_web | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[3].recipes[9] |
| thaumcraft:LiquidDeath | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[5].stages[1].recipes[0] |
| thaumcraft:metal_purification_cinnabar | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[3].stages[0].recipes[2] |
| thaumcraft:metal_purification_copper | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[3].stages[0].recipes[3] |
| thaumcraft:metal_purification_gold | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[3].stages[0].recipes[1] |
| thaumcraft:metal_purification_iron | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[3].stages[0].recipes[0] |
| thaumcraft:metal_purification_lead | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[3].stages[0].recipes[6] |
| thaumcraft:metal_purification_silver | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[3].stages[0].recipes[5] |
| thaumcraft:metal_purification_tin | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[3].stages[0].recipes[4] |
| thaumcraft:nitor | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[0].stages[0].recipes[0] |
| thaumcraft:nitor | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[9].stages[2].recipes[0] |
| thaumcraft:SaneSoap | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[8].stages[1].recipes[0] |
| thaumcraft:thaumiumingot | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[2].stages[2].recipes[2] |
| thaumcraft:thaumiumingot | thaumcraft:crucible | CRUCIBLE_PAGE_READY_NO_GAMEPLAY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[2].stages[1].recipes[2] |
| thaumcraft:ArcaneEarToggle | minecraft:crafting_shapeless | VANILLA_OR_STANDARD_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[6].stages[1].recipes[1] |
| thaumcraft:BrassBrace | minecraft:crafting_shapeless | VANILLA_OR_STANDARD_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[11].stages[0].recipes[2] |
| thaumcraft:BrickArcane | minecraft:crafting_shaped | VANILLA_OR_STANDARD_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].stages[2].recipes[3] |
| thaumcraft:GolemBell | minecraft:crafting_shapeless | VANILLA_OR_STANDARD_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[9].stages[0].recipes[0] |
| thaumcraft:ironplate | minecraft:crafting_shaped | VANILLA_OR_STANDARD_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[0].stages[0].recipes[4] |
| thaumcraft:JarLabel | minecraft:crafting_shapeless | VANILLA_OR_STANDARD_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[11].stages[0].recipes[3] |
| thaumcraft:phial | minecraft:crafting_shapeless | VANILLA_OR_STANDARD_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[0].stages[0].recipes[3] |
| thaumcraft:StoneArcane | minecraft:crafting_shaped | VANILLA_OR_STANDARD_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].stages[2].recipes[2] |
| thaumcraft:tablestone | minecraft:crafting_shaped | VANILLA_OR_STANDARD_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[2].recipes[3] |
| thaumcraft:tablestone | minecraft:crafting_shaped | VANILLA_OR_STANDARD_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[1].recipes[3] |
| thaumcraft:tablewood | minecraft:crafting_shaped | VANILLA_OR_STANDARD_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[2].stages[1].recipes[0] |
| thaumcraft:tablewood | minecraft:crafting_shaped | VANILLA_OR_STANDARD_READY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[2].stages[0].recipes[0] |

## Missing recipe page references

| Class | Reference | Research file | JSON path |
|---|---|---|---|
| ALCHEMY_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:EverfullUrn | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[20].stages[1].recipes[0] |
| ALCHEMY_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:JarLabelEssence | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[11].stages[0].recipes[4] |
| ALCHEMY_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:Thaumatorium | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[18].stages[1].recipes[0] |
| ARTIFICE_BEHAVIOR_PAGE_DEFERRED | thaumcraft:ArcaneBore | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[17].stages[1].recipes[0] |
| ARTIFICE_BEHAVIOR_PAGE_DEFERRED | thaumcraft:infernalfurnace | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[3].stages[1].recipes[0] |
| ARTIFICE_BEHAVIOR_PAGE_DEFERRED | thaumcraft:LampFertility | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[13].stages[1].recipes[0] |
| ARTIFICE_BEHAVIOR_PAGE_DEFERRED | thaumcraft:LampGrowth | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[12].stages[1].recipes[0] |
| ARTIFICE_BEHAVIOR_PAGE_DEFERRED | thaumcraft:Mirror | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[8].stages[1].recipes[0] |
| ARTIFICE_BEHAVIOR_PAGE_DEFERRED | thaumcraft:MirrorEssentia | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[10].stages[1].recipes[0] |
| ARTIFICE_BEHAVIOR_PAGE_DEFERRED | thaumcraft:MirrorHand | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[9].stages[1].recipes[0] |
| AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | thaumcraft:focus_1 | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[0].recipes[2] |
| AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | thaumcraft:focus_1 | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[1].recipes[2] |
| AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | thaumcraft:focus_1 | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[2].recipes[2] |
| AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | thaumcraft:focus_2 | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[1].stages[1].recipes[0] |
| AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | thaumcraft:focus_2 | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[1].stages[0].recipes[0] |
| AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | thaumcraft:focus_3 | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[2].stages[1].recipes[0] |
| AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | thaumcraft:focus_3 | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[2].stages[0].recipes[0] |
| AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | thaumcraft:VisAmulet | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[19].stages[1].recipes[0] |
| ELDRITCH_PAGE_DEFERRED | thaumcraft:PrimalCrusher | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[4].stages[1].recipes[0] |
| ELDRITCH_PAGE_DEFERRED | thaumcraft:voidingot | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[0].stages[1].recipes[0] |
| ELDRITCH_PAGE_DEFERRED | thaumcraft:VoidRobeChest | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[3].stages[1].recipes[1] |
| ELDRITCH_PAGE_DEFERRED | thaumcraft:VoidRobeHelm | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[3].stages[1].recipes[0] |
| ELDRITCH_PAGE_DEFERRED | thaumcraft:VoidRobeLegs | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[3].stages[1].recipes[2] |
| ELDRITCH_PAGE_DEFERRED | thaumcraft:VoidseerPearl | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[2].stages[1].recipes[0] |
| ELDRITCH_PAGE_DEFERRED | thaumcraft:VoidSiphon | 05_neoforge_port/src/main/resources/data/thaumcraft/research/eldritch.json | $.entries[1].stages[1].recipes[0] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:triplemeattreatfake | 05_neoforge_port/src/main/resources/data/thaumcraft/research/artifice.json | $.entries[3].stages[1].recipes[1] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:salismundusfake | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].stages[2].recipes[1] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:salismundusfake | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].stages[1].recipes[1] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:salismundusfake | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].stages[0].recipes[0] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:IEARCINGFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[5] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:IEBURROWINGFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[0] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:IECOLLECTORFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[1] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:IEDESTRUCTIVEFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[2] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:IEESSENCEFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[6] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:IELAMPLIGHTFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[7] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:IEREFININGFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[3] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:IESOUNDINGFAKE | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[9].stages[1].recipes[4] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:RunicArmorFake0 | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[10].stages[1].recipes[0] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:RunicArmorFake1 | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[10].stages[1].recipes[1] |
| FAKE_OR_SYNTHETIC_PAGE | thaumcraft:RunicArmorFake2 | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[10].stages[1].recipes[2] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:GolemPress | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[8].stages[2].recipes[1] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:JarBrain | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[1].stages[1].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:MindBiothaumic | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[12].stages[1].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealBreak | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[22].stages[1].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealBreakAdv | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[22].addenda[0].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealButcher | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[19].stages[1].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealCollect | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[13].stages[0].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealCollectAdv | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[13].addenda[0].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealEmpty | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[15].stages[1].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealEmptyAdv | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[15].addenda[0].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealGuard | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[18].stages[1].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealGuardAdv | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[18].addenda[0].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealHarvest | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[21].stages[1].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealLumber | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[23].stages[1].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealProvide | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[16].stages[1].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealStock | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[17].stages[1].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealStore | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[14].stages[0].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealStoreAdv | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[14].addenda[0].recipes[0] |
| GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealUse | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[20].stages[1].recipes[0] |
| INFUSION_PAGE_DEFERRED | thaumcraft:BootsTraveller | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[7].stages[1].recipes[0] |
| INFUSION_PAGE_DEFERRED | thaumcraft:CHARMUNDYING | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[16].stages[1].recipes[0] |
| INFUSION_PAGE_DEFERRED | thaumcraft:CLOUDRING | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[13].stages[1].recipes[0] |
| INFUSION_PAGE_DEFERRED | thaumcraft:ElementalAxe | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].stages[1].recipes[0] |
| INFUSION_PAGE_DEFERRED | thaumcraft:ElementalHoe | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].stages[1].recipes[4] |
| INFUSION_PAGE_DEFERRED | thaumcraft:ElementalPick | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].stages[1].recipes[2] |
| INFUSION_PAGE_DEFERRED | thaumcraft:ElementalShovel | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].stages[1].recipes[3] |
| INFUSION_PAGE_DEFERRED | thaumcraft:ElementalSword | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].stages[1].recipes[1] |
| INFUSION_PAGE_DEFERRED | thaumcraft:infusionaltar | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[1].stages[2].recipes[2] |
| INFUSION_PAGE_DEFERRED | thaumcraft:infusionaltarancient | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[4].stages[1].recipes[1] |
| INFUSION_PAGE_DEFERRED | thaumcraft:infusionaltareldritch | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[5].stages[1].recipes[1] |
| INFUSION_PAGE_DEFERRED | thaumcraft:MaskAngryGhost | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[12].stages[1].recipes[2] |
| INFUSION_PAGE_DEFERRED | thaumcraft:MaskGrinningDevil | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[12].stages[1].recipes[1] |
| INFUSION_PAGE_DEFERRED | thaumcraft:MaskSippingFiend | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[12].stages[1].recipes[3] |
| INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:arcane_brick | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[6].stages[1].recipes[3] |
| INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:arcane_stone | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[6].stages[1].recipes[2] |
| INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:arcane_stone | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[6].stages[0].recipes[0] |
| INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:CuriosityBand | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[14].stages[1].recipes[0] |
| INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:HelmGoggles | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[12].stages[1].recipes[0] |
| INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:ThaumiumFortressChest | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[11].stages[1].recipes[1] |
| INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:ThaumiumFortressHelm | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[11].stages[1].recipes[0] |
| INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:ThaumiumFortressLegs | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[11].stages[1].recipes[2] |
| INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:VerdantHeart | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[15].stages[1].recipes[0] |
| INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:VerdantHeartLife | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[15].stages[1].recipes[1] |
| INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:VerdantHeartSustain | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[15].stages[1].recipes[2] |
| LEGACY_PAGE_KEY_OR_MISSING_RECIPE | thaumcraft:CausalityCollapser | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[19].stages[1].recipes[0] |
| LEGACY_PAGE_KEY_OR_MISSING_RECIPE | thaumcraft:CrystalClusterAir | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[7].stages[1].recipes[0] |
| LEGACY_PAGE_KEY_OR_MISSING_RECIPE | thaumcraft:CrystalClusterEarth | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[7].stages[1].recipes[3] |
| LEGACY_PAGE_KEY_OR_MISSING_RECIPE | thaumcraft:CrystalClusterEntropy | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[7].stages[1].recipes[5] |
| LEGACY_PAGE_KEY_OR_MISSING_RECIPE | thaumcraft:CrystalClusterFire | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[7].stages[1].recipes[1] |
| LEGACY_PAGE_KEY_OR_MISSING_RECIPE | thaumcraft:CrystalClusterFlux | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[7].stages[1].recipes[6] |
| LEGACY_PAGE_KEY_OR_MISSING_RECIPE | thaumcraft:CrystalClusterOrder | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[7].stages[1].recipes[4] |
| LEGACY_PAGE_KEY_OR_MISSING_RECIPE | thaumcraft:CrystalClusterWater | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[7].stages[1].recipes[2] |
| LEGACY_PAGE_KEY_OR_MISSING_RECIPE | thaumcraft:nitorcolor | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[9].stages[2].recipes[1] |

## Required craft references

| Reference | Resolves to recipe JSON | Recipe type | Research file | JSON path |
|---|---:|---|---|---|
| minecraft:clay_ball | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].required_craft[0] |
| minecraft:dye | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].required_craft[2] |
| minecraft:glowstone_dust | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].required_craft[3] |
| minecraft:gunpowder | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].required_craft[0] |
| minecraft:lava_bucket | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].required_craft[3] |
| minecraft:slime_ball | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[1].required_craft[1] |
| minecraft:string | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].required_craft[1] |
| minecraft:web | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[2].required_craft[2] |
| thaumcraft:leather | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[0].required_craft[1] |
| thaumcraft:smelter_basic | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[10].stages[2].required_craft[0] |
| thaumcraft:tallow | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/alchemy.json | $.entries[4].stages[0].required_craft[0] |
| thaumcraft:focus_1 | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[0].required_craft[0] |
| thaumcraft:focus_2 | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[1].stages[0].required_craft[0] |
| thaumcraft:focus_3 | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[2].stages[0].required_craft[0] |
| thaumcraft:wand_workbench | True | thaumcraft:arcane_shaped | 05_neoforge_port/src/main/resources/data/thaumcraft/research/auromancy.json | $.entries[0].stages[1].required_craft[0] |
| thaumcraft:arcane_workbench | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].stages[0].required_craft[0] |
| thaumcraft:caster_basic | True | thaumcraft:arcane_shaped | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[10].stages[1].required_craft[1] |
| thaumcraft:crucible | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[9].stages[1].required_craft[0] |
| thaumcraft:research_table | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[2].stages[0].required_craft[1] |
| thaumcraft:scribing_tools | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[2].stages[0].required_craft[0] |
| thaumcraft:thaumometer | True | thaumcraft:arcane_shaped | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[0].stages[1].required_craft[0] |
| thaumcraft:vis_resonator | True | thaumcraft:arcane_shapeless | 05_neoforge_port/src/main/resources/data/thaumcraft/research/basics.json | $.entries[10].stages[1].required_craft[0] |
| thaumcraft:arcane_stone | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[6].stages[0].required_craft[0] |
| thaumcraft:infusion_matrix | False |  | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[1].stages[1].required_craft[0] |
| thaumcraft:thaumium_axe | True | minecraft:crafting_shaped | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].stages[0].required_craft[0] |
| thaumcraft:thaumium_hoe | True | minecraft:crafting_shaped | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].stages[0].required_craft[4] |
| thaumcraft:thaumium_pick | True | minecraft:crafting_shaped | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].stages[0].required_craft[2] |
| thaumcraft:thaumium_shovel | True | minecraft:crafting_shaped | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].stages[0].required_craft[3] |
| thaumcraft:thaumium_sword | True | minecraft:crafting_shaped | 05_neoforge_port/src/main/resources/data/thaumcraft/research/infusion.json | $.entries[8].stages[0].required_craft[1] |

## Next implementation guidance

1. Use Missing recipe page references by class as the decision source for the next large implementation slice.
2. If ALCHEMY_CRUCIBLE_OR_SPECIAL_PAGE dominates, design a crucible/special alchemy recipe serializer and page snapshot before machine behavior.
3. If INFUSION_PAGE_DEFERRED or FAKE_OR_SYNTHETIC_PAGE dominates, design an infusion/fake recipe page boundary before infusion matrix behavior.
4. Do not treat ICON, REQUIRED_ITEM, or REQUIRED_CRAFT references as missing recipe pages unless their own requirement audit says they are unresolved.
5. Keep build and dedicated server smoke green after every page/serializer expansion.
