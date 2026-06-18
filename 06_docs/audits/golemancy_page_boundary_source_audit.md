# Golemancy Page Boundary Source Audit

Generated: 2026-06-18 16:02:10 +03:00

## Purpose

This document extracts legacy source evidence for current `GOLEMANCY_PAGE_DEFERRED` recipe-page references. It is an analysis artifact only; do not treat every hit as a recipe implementation target.

## Summary

| Metric | Count |
|---|---:|
| Golemancy deferred references | 6 |
| References with at least one source hit | 6 |
| References without source hit | 0 |
| Legacy Java files scanned | 902 |
| Source hits | 719 |

## Boundary classification

| Classification | Count |
|---|---:|
| GOLEM_MACHINE_BOUNDARY | 1 |
| JAR_BRAIN_BOUNDARY | 1 |
| MIND_COMPONENT_BOUNDARY | 1 |
| SEAL_BOUNDARY | 3 |

## Source hit kind distribution

| Hit kind | Count |
|---|---:|
| ARCANE_RECIPE_SOURCE | 1 |
| GOLEM_BLOCK_OR_ITEM_SOURCE | 81 |
| INFUSION_RECIPE_SOURCE | 4 |
| SEAL_BEHAVIOR_SOURCE | 41 |
| TEXT_HIT | 592 |

## Deferred reference overview

| Reference | Classification | Research file | JSON path | Source hits |
|---|---|---|---|---:|
| thaumcraft:GolemPress | GOLEM_MACHINE_BOUNDARY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[8].stages[2].recipes[1] | 3 |
| thaumcraft:JarBrain | JAR_BRAIN_BOUNDARY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[1].stages[1].recipes[0] | 37 |
| thaumcraft:MindBiothaumic | MIND_COMPONENT_BOUNDARY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[12].stages[1].recipes[0] | 9 |
| thaumcraft:SealBreak | SEAL_BOUNDARY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[22].stages[1].recipes[0] | 568 |
| thaumcraft:SealButcher | SEAL_BOUNDARY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[19].stages[1].recipes[0] | 7 |
| thaumcraft:SealHarvest | SEAL_BOUNDARY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[21].stages[1].recipes[0] | 95 |

## Source hit overview

| Reference | Hit kind | File | Line | Matched term |
|---|---|---|---:|---|
| thaumcraft:GolemPress | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 122 | GolemPress |
| thaumcraft:GolemPress | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 123 | GolemPress |
| thaumcraft:GolemPress | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 124 | thaumcraft:GolemPress |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/api/blocks/BlocksTC.java | 135 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java | 24 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java | 49 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java | 50 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java | 141 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 38 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 91 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 92 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 103 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 104 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 128 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 157 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 158 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 160 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 162 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 163 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 236 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 245 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 256 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 257 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 258 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJarBrainItem.java | 15 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJarBrainItem.java | 18 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJarBrainItem.java | 20 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJarBrainItem.java | 28 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJarBrainItem.java | 29 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 71 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 119 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 326 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 392 | JarBrain |
| thaumcraft:JarBrain | INFUSION_RECIPE_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 280 | thaumcraft:JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/config/ConfigResearch.java | 278 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/lib/research/theorycraft/AidBrainInAJar.java | 11 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/tiles/devices/TileJarBrain.java | 14 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/tiles/devices/TileJarBrain.java | 27 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/tiles/devices/TileJarBrain.java | 84 | JarBrain |
| thaumcraft:JarBrain | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/tiles/devices/TileJarBrain.java | 87 | JarBrain |
| thaumcraft:MindBiothaumic | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 168 | MindBiothaumic |
| thaumcraft:MindBiothaumic | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 170 | MindBiothaumic |
| thaumcraft:MindBiothaumic | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 172 | MindBiothaumic |
| thaumcraft:MindBiothaumic | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 176 | MindBiothaumic |
| thaumcraft:MindBiothaumic | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 179 | MindBiothaumic |
| thaumcraft:MindBiothaumic | INFUSION_RECIPE_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 334 | thaumcraft:MindBiothaumic |
| thaumcraft:MindBiothaumic | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/GolemProperties.java | 207 | MindBiothaumic |
| thaumcraft:MindBiothaumic | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/GolemProperties.java | 208 | MindBiothaumic |
| thaumcraft:MindBiothaumic | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/GolemProperties.java | 210 | MindBiothaumic |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/com/sasmaster/glelwjgl/java/CoreGLE.java | 165 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/com/sasmaster/glelwjgl/java/CoreGLE.java | 169 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/com/sasmaster/glelwjgl/java/CoreGLE.java | 174 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/com/sasmaster/glelwjgl/java/CoreGLE.java | 458 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/aspects/Aspect.java | 69 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/aspects/AspectList.java | 84 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/aspects/AspectList.java | 112 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/casters/FocusEngine.java | 115 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/casters/FocusEngine.java | 125 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/casters/FocusEngine.java | 137 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/casters/FocusPackage.java | 99 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/casters/FocusPackage.java | 148 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/casters/FocusPackage.java | 191 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/api/golems/EnumGolemTrait.java | 22 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/research/ScanningManager.java | 75 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/research/theorycraft/CardPonder.java | 41 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/research/theorycraft/CardRethink.java | 47 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/research/theorycraft/ResearchTableData.java | 277 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/ThaumcraftApi.java | 423 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/ThaumcraftApi.java | 424 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/ThaumcraftApi.java | 425 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/api/ThaumcraftInvHelper.java | 68 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/beams/FXBeamBore.java | 177 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/beams/FXBeamBore.java | 181 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/beams/FXBeamBore.java | 185 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/beams/FXBeamBore.java | 189 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/beams/FXBeamWand.java | 196 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/beams/FXBeamWand.java | 200 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/beams/FXBeamWand.java | 204 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/beams/FXBeamWand.java | 208 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/FXDispatcher.java | 39 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/FXDispatcher.java | 632 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/FXDispatcher.java | 646 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/FXDispatcher.java | 667 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/FXDispatcher.java | 687 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/FXDispatcher.java | 700 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/FXDispatcher.java | 792 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/FXDispatcher.java | 933 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/FXDispatcher.java | 937 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/ParticleEngine.java | 79 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/ParticleEngine.java | 83 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/ParticleEngine.java | 143 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/ParticleEngine.java | 147 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/ParticleEngine.java | 152 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/ParticleEngine.java | 157 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/ParticleEngine.java | 199 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java | 2 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java | 14 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java | 16 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java | 20 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java | 24 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java | 63 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java | 64 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java | 65 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 236 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 251 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 512 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 571 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 669 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 674 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 676 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 734 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 900 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 933 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 938 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 940 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 944 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java | 947 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/client/gui/GuiGolemBuilder.java | 329 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiLogistics.java | 117 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java | 481 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java | 592 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java | 829 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java | 844 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java | 849 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java | 1127 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 283 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 1082 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 1599 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 1653 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 1841 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiResearchTable.java | 446 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/client/gui/GuiResearchTable.java | 708 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiResearchTable.java | 739 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 148 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 161 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 187 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 206 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 213 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 271 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 278 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/gui/plugins/GuiHoverButton.java | 101 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/lib/events/RenderEventHandler.java | 275 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/lib/events/WandRenderingHandler.java | 293 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/lib/obj/MaterialLibrary.java | 70 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/lib/obj/MeshLoader.java | 111 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/lib/UtilsFX.java | 356 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/models/gear/ModelCustomArmor.java | 80 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/models/gear/ModelCustomArmor.java | 85 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/models/gear/ModelCustomArmor.java | 90 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/models/gear/ModelCustomArmor.java | 96 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/models/gear/ModelCustomArmor.java | 101 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/models/gear/ModelCustomArmor.java | 106 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java | 37 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java | 41 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java | 45 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java | 73 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java | 78 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java | 82 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java | 86 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java | 90 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java | 94 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java | 56 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java | 61 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java | 65 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java | 69 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java | 73 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java | 77 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileGolemBuilderRenderer.java | 58 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileGolemBuilderRenderer.java | 62 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/client/renderers/tile/TileGolemBuilderRenderer.java | 66 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java | 74 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java | 78 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java | 82 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java | 548 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java | 552 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java | 556 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java | 560 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java | 564 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java | 568 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TilePatternCrafterRenderer.java | 36 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TilePatternCrafterRenderer.java | 40 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TilePatternCrafterRenderer.java | 44 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileResearchTableRenderer.java | 34 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileResearchTableRenderer.java | 38 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileResearchTableRenderer.java | 42 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java | 39 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java | 43 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java | 47 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/lighting/LC.java | 54 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/lighting/LC.java | 58 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/lighting/LC.java | 62 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/lighting/LC.java | 66 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/lighting/LC.java | 70 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/lighting/LC.java | 74 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java | 52 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java | 56 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java | 60 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java | 64 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java | 68 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java | 72 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java | 84 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java | 91 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java | 98 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/render/CCModel.java | 788 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/render/CCModel.java | 793 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/render/CCModel.java | 798 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/render/CCModel.java | 803 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/render/CCModel.java | 808 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/render/CCModel.java | 813 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/BlockCoord.java | 172 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/BlockCoord.java | 177 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/BlockCoord.java | 182 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/Cuboid6.java | 205 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/Cuboid6.java | 209 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/Cuboid6.java | 213 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/Cuboid6.java | 217 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/Cuboid6.java | 221 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/Cuboid6.java | 225 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/CuboidCoord.java | 104 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/CuboidCoord.java | 108 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/CuboidCoord.java | 112 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/CuboidCoord.java | 116 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/CuboidCoord.java | 120 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/CuboidCoord.java | 124 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/Vector3.java | 128 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/Vector3.java | 133 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/codechicken/lib/vec/Vector3.java | 138 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/basic/BlockPavingStone.java | 84 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/basic/BlockPillar.java | 81 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/basic/BlockPillar.java | 91 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/BlockTCTile.java | 54 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/BlockTCTile.java | 63 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/crafting/BlockArcaneWorkbench.java | 42 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/crafting/BlockArcaneWorkbench.java | 47 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/crafting/BlockCrucible.java | 93 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/crafting/BlockCrucible.java | 98 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/crafting/BlockGolemBuilder.java | 62 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/crafting/BlockGolemBuilder.java | 64 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java | 92 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java | 99 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java | 174 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java | 178 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java | 182 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java | 186 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java | 190 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java | 194 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockHungryChest.java | 93 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockHungryChest.java | 99 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockInfernalFurnace.java | 100 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockInfernalFurnace.java | 102 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockInlay.java | 331 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockInlay.java | 332 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockLamp.java | 66 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockLamp.java | 71 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockPedestal.java | 115 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockPedestal.java | 116 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockRedstoneRelay.java | 139 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockRedstoneRelay.java | 140 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 80 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 82 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/essentia/BlockSmelter.java | 105 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/essentia/BlockSmelter.java | 111 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java | 168 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java | 172 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java | 176 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java | 180 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java | 184 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java | 188 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java | 212 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java | 225 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/misc/BlockHole.java | 31 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/misc/BlockPlaceholder.java | 111 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/misc/BlockPlaceholder.java | 120 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/misc/BlockPlaceholder.java | 133 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/misc/BlockPlaceholder.java | 140 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/plants/BlockLogsTC.java | 79 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/plants/BlockSaplingTC.java | 83 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/taint/BlockTaint.java | 113 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/taint/BlockTaint.java | 117 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintFeature.java | 43 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintFeature.java | 54 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintLog.java | 99 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigItems.java | 35 | SealBreak |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigItems.java | 36 | SealBreak |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/config/ConfigItems.java | 76 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/config/ConfigItems.java | 299 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigItems.java | 321 | SealBreak |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigItems.java | 325 | SealBreak |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 177 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 179 | thaumcraft:SealBreak |
| thaumcraft:SealBreak | INFUSION_RECIPE_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 270 | thaumcraft:SealBreak |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/container/ContainerArcaneWorkbench.java | 123 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/container/ContainerArcaneWorkbench.java | 190 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/container/ContainerFocalManipulator.java | 18 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/container/ContainerHandMirror.java | 168 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/container/ContainerLogistics.java | 100 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/container/ContainerPech.java | 130 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/container/ContainerPotionSprayer.java | 15 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/container/ContainerResearchTable.java | 58 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/container/ContainerSpa.java | 16 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 80 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 101 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 305 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 315 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 320 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 321 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 329 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityTurretCrossbow.java | 189 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/ItemTurretPlacer.java | 52 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/ItemTurretPlacer.java | 56 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/ItemTurretPlacer.java | 60 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/EntityFluxRift.java | 345 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/EntityFluxRift.java | 347 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/EntityFluxRift.java | 357 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/EntityFluxRift.java | 359 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/EntityFluxRift.java | 376 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/EntityFluxRift.java | 378 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/EntityFluxRift.java | 394 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/EntityFluxRift.java | 396 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/EntityFluxRift.java | 400 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/EntityFluxRift.java | 497 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java | 126 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java | 130 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java | 134 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java | 138 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java | 159 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java | 163 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java | 182 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java | 187 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java | 193 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/cult/EntityCultist.java | 27 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/EntityEldritchCrab.java | 55 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java | 66 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/EntityPech.java | 239 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/EntityPech.java | 243 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/EntityPech.java | 247 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/EntityPech.java | 251 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/EntityPech.java | 255 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/EntityPech.java | 259 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/EntityPech.java | 263 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/EntityPech.java | 267 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/EntityPech.java | 275 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/monster/tainted/EntityTaintSwarm.java | 113 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/projectile/EntityBottleTaint.java | 41 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/projectile/EntityFocusProjectile.java | 202 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/projectile/EntityFocusProjectile.java | 206 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/entities/projectile/EntityHomingShard.java | 140 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/ai/GolemNodeProcessor.java | 196 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/ai/GolemNodeProcessor.java | 206 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/ai/GolemNodeProcessor.java | 218 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/ai/PathNavigateGolemAir.java | 41 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/client/gui/SealBaseContainer.java | 70 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 104 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 111 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 113 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 122 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 142 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 144 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 162 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 164 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 211 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 213 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 225 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 227 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 236 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 241 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java | 251 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/client/PartModelBreakers.java | 9 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/client/PartModelBreakers.java | 13 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/EntityThaumcraftGolem.java | 679 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/GolemProperties.java | 23 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/GolemProperties.java | 214 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealBreaker.java | 38 | SealBreak |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealBreaker.java | 45 | SealBreak |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealBreaker.java | 48 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealBreaker.java | 54 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealBreaker.java | 125 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealBreaker.java | 184 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealBreakerAdvanced.java | 7 | SealBreak |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealBreakerAdvanced.java | 12 | SealBreak |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealBreakerAdvanced.java | 13 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/seals/SealBreakerAdvanced.java | 19 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealBreakerAdvanced.java | 44 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/seals/SealButcher.java | 77 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealHandler.java | 108 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealLumber.java | 80 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealLumber.java | 145 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealPickup.java | 68 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealPickup.java | 117 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealPickup.java | 119 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/golems/seals/SealProvide.java | 91 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java | 60 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusEffectBreak.java | 24 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusEffectBreak.java | 28 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusEffectBreak.java | 33 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusEffectBreak.java | 56 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusEffectBreak.java | 69 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusEffectEarth.java | 62 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusEffectRift.java | 68 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusEffectRift.java | 71 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java | 123 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java | 129 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java | 135 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java | 145 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java | 151 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java | 157 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java | 230 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java | 239 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java | 248 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusMediumProjectile.java | 30 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/foci/FocusMediumProjectile.java | 35 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 111 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 118 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 127 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 147 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 157 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 165 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 169 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 181 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 186 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 265 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 279 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 284 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 289 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 302 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 315 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/casters/ItemCaster.java | 484 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/curios/ItemCurio.java | 54 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/items/curios/ItemCurio.java | 59 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/items/curios/ItemCurio.java | 64 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/curios/ItemCurio.java | 71 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/curios/ItemCurio.java | 76 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/curios/ItemCurio.java | 81 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/curios/ItemCurio.java | 97 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/resources/ItemMagicDust.java | 51 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/tools/ItemElementalShovel.java | 145 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/tools/ItemElementalShovel.java | 154 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/tools/ItemElementalShovel.java | 162 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/items/tools/ItemElementalShovel.java | 165 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/CommandThaumcraft.java | 222 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/DustTriggerMultiblock.java | 80 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/DustTriggerMultiblock.java | 83 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/DustTriggerOre.java | 39 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java | 61 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java | 70 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java | 74 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java | 78 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java | 82 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java | 94 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java | 98 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java | 102 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/lib/crafting/ShapedArcaneVoidJar.java | 24 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java | 253 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java | 332 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java | 387 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java | 45 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java | 74 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java | 119 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java | 146 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java | 171 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java | 197 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/PlayerEvents.java | 179 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 63 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 152 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 186 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 317 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 319 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 320 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 323 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 334 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 336 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 337 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 341 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 349 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 356 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 360 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 375 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 381 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 386 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 388 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 389 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 391 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 392 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 420 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 432 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 445 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 100 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 156 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 176 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/WarpEvents.java | 262 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/WarpEvents.java | 281 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/WarpEvents.java | 302 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/network/fx/PacketFXScanSource.java | 136 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/network/misc/PacketMiscEvent.java | 71 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/network/misc/PacketMiscEvent.java | 73 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/network/misc/PacketMiscEvent.java | 78 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/network/misc/PacketMiscEvent.java | 84 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/network/misc/PacketMiscEvent.java | 86 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 61 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 72 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/network/playerdata/PacketPlayerFlagToServer.java | 40 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/research/ResearchManager.java | 189 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/research/ResearchManager.java | 539 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java | 90 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java | 94 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java | 98 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java | 102 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java | 106 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java | 110 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java | 115 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java | 119 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/research/theorycraft/CardSynergy.java | 55 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/SoundsTC.java | 71 | Break |
| thaumcraft:SealBreak | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/lib/SoundsTC.java | 85 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/SoundsTC.java | 150 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 26 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 86 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 127 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 141 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 178 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 336 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 340 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/EntityUtils.java | 380 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/EntityUtils.java | 386 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/EntityUtils.java | 391 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java | 88 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java | 140 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java | 267 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/Utils.java | 317 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/Utils.java | 321 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/Utils.java | 325 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/Utils.java | 480 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileArcaneWorkbench.java | 96 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileArcaneWorkbench.java | 99 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileFocalManipulator.java | 122 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileGolemBuilder.java | 303 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 392 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 398 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 404 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 409 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 414 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 419 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 423 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 427 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 432 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 437 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 441 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 445 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 559 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 578 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 734 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 737 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java | 982 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 99 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 103 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 108 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 112 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 117 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 122 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 126 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 141 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 154 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 171 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 175 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 181 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 187 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 195 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 201 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 207 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 213 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 221 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java | 229 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileResearchTable.java | 174 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileResearchTable.java | 180 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 181 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 194 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 461 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileArcaneEar.java | 94 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileBellows.java | 96 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileCondenser.java | 129 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileCondenser.java | 193 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileHungryChest.java | 18 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileInfernalFurnace.java | 110 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileInfernalFurnace.java | 124 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileInfernalFurnace.java | 254 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileInfernalFurnace.java | 256 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileLampFertility.java | 99 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileLevitator.java | 118 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TilePotionSprayer.java | 71 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileRedstoneRelay.java | 77 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileRedstoneRelay.java | 81 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileRedstoneRelay.java | 85 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileRedstoneRelay.java | 96 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileRedstoneRelay.java | 100 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileRedstoneRelay.java | 104 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileSpa.java | 118 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/devices/TileWaterJug.java | 127 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/essentia/TileCentrifuge.java | 234 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java | 106 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java | 116 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/essentia/TileTube.java | 362 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/essentia/TileTubeValve.java | 90 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/misc/TileHole.java | 59 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/misc/TileHole.java | 67 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/tiles/misc/TileHole.java | 75 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/world/aura/AuraThread.java | 47 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/world/biomes/BiomeGenMagicalForest.java | 91 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/world/objects/WorldGenGreatwoodTrees.java | 327 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/world/objects/WorldGenMound.java | 2503 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/world/objects/WorldGenMound.java | 2507 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/world/objects/WorldGenMound.java | 2511 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/world/objects/WorldGenMound.java | 2521 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/world/objects/WorldGenMound.java | 2525 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/world/objects/WorldGenMound.java | 2529 | Break |
| thaumcraft:SealBreak | TEXT_HIT | src/main/java/thaumcraft/common/world/ThaumcraftWorldGenerator.java | 209 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/proxies/ProxyGUI.java | 118 | Break |
| thaumcraft:SealBreak | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/proxies/ProxyGUI.java | 187 | Break |
| thaumcraft:SealButcher | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigItems.java | 37 | SealButcher |
| thaumcraft:SealButcher | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigItems.java | 317 | SealButcher |
| thaumcraft:SealButcher | INFUSION_RECIPE_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 269 | thaumcraft:SealButcher |
| thaumcraft:SealButcher | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealButcher.java | 35 | SealButcher |
| thaumcraft:SealButcher | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealButcher.java | 41 | SealButcher |
| thaumcraft:SealButcher | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealButcher.java | 44 | Butcher |
| thaumcraft:SealButcher | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealButcher.java | 49 | Butcher |
| thaumcraft:SealHarvest | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/api/ThaumcraftApi.java | 456 | Harvest |
| thaumcraft:SealHarvest | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/api/ThaumcraftApi.java | 461 | Harvest |
| thaumcraft:SealHarvest | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/api/ThaumcraftApi.java | 468 | Harvest |
| thaumcraft:SealHarvest | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/api/ThaumcraftApi.java | 470 | Harvest |
| thaumcraft:SealHarvest | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/api/ThaumcraftApi.java | 473 | Harvest |
| thaumcraft:SealHarvest | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/api/ThaumcraftApi.java | 475 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/basic/BlockBannerTC.java | 150 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/basic/BlockBannerTC.java | 162 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/basic/BlockPavingStone.java | 37 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/basic/BlockPlanksTC.java | 14 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/basic/BlockTable.java | 38 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/basic/BlockTranslucent.java | 29 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/BlockTCTile.java | 30 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockBrainBox.java | 40 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java | 120 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java | 121 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockHungryChest.java | 59 | Harvest |
| thaumcraft:SealHarvest | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/devices/BlockMirror.java | 37 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockMirror.java | 52 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockMirror.java | 135 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/devices/BlockMirror.java | 140 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/essentia/BlockEssentiaTransport.java | 36 | Harvest |
| thaumcraft:SealHarvest | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 99 | Harvest |
| thaumcraft:SealHarvest | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java | 107 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/essentia/BlockSmelterAux.java | 39 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/essentia/BlockSmelterVent.java | 36 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/misc/BlockPlaceholder.java | 41 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/BlockLoot.java | 43 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/BlockLoot.java | 47 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/ore/BlockCrystal.java | 80 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/ore/BlockCrystal.java | 302 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/plants/BlockLogsTC.java | 28 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/taint/BlockTaint.java | 138 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/taint/BlockTaint.java | 210 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintFeature.java | 39 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintFeature.java | 88 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintFibre.java | 108 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintLog.java | 29 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 155 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 156 | Harvest |
| thaumcraft:SealHarvest | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigItems.java | 45 | SealHarvest |
| thaumcraft:SealHarvest | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/config/ConfigItems.java | 316 | SealHarvest |
| thaumcraft:SealHarvest | ARCANE_RECIPE_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 268 | thaumcraft:SealHarvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 241 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 245 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 246 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 248 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 249 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 252 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 256 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java | 278 | Harvest |
| thaumcraft:SealHarvest | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealBreaker.java | 130 | Harvest |
| thaumcraft:SealHarvest | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealHarvest.java | 57 | SealHarvest |
| thaumcraft:SealHarvest | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealHarvest.java | 65 | SealHarvest |
| thaumcraft:SealHarvest | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealHarvest.java | 69 | Harvest |
| thaumcraft:SealHarvest | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealHarvest.java | 75 | Harvest |
| thaumcraft:SealHarvest | SEAL_BEHAVIOR_SOURCE | src/main/java/thaumcraft/common/golems/seals/SealHarvest.java | 132 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/items/curios/ItemEnchantmentPlaceholder.java | 42 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/items/curios/ItemEnchantmentPlaceholder.java | 46 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/items/curios/ItemEnchantmentPlaceholder.java | 50 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/items/tools/ItemPrimalCrusher.java | 63 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java | 227 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 250 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ServerEvents.java | 339 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 187 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 191 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 192 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 212 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 214 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 216 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 238 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 239 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 240 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 243 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 245 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 252 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 253 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 256 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/events/ToolEvents.java | 257 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 61 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 63 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 73 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 74 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 77 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 78 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 81 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 105 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 123 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/BlockUtils.java | 183 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java | 483 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java | 484 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java | 487 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/proxies/CommonProxy.java | 110 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/proxies/CommonProxy.java | 114 | Harvest |
| thaumcraft:SealHarvest | TEXT_HIT | src/main/java/thaumcraft/proxies/CommonProxy.java | 118 | Harvest |

## Extracted source contexts

### thaumcraft:GolemPress @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:122

- Classification: GOLEM_MACHINE_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 118

```java
        Part GP2 = new Part(Blocks.CAULDRON, new ItemStack(BlocksTC.placeholderCauldron));
        Part GP3 = new Part(Blocks.PISTON.getDefaultState().withProperty((IProperty)BlockPistonBase.FACING, (Comparable)EnumFacing.UP), BlocksTC.golemBuilder);
        Part GP4 = new Part(Blocks.ANVIL, new ItemStack(BlocksTC.placeholderAnvil));
        Part GP5 = new Part(BlocksTC.tableStone, new ItemStack(BlocksTC.placeholderTable));
        Part[][][] golempressBlueprint = { { { null, null }, { GP1, null } }, { { GP2, GP4 }, { GP3, GP5 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("MINDCLOCKWORK", golempressBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("thaumcraft:GolemPress"), new ThaumcraftApi.BluePrint("MINDCLOCKWORK", new ItemStack(BlocksTC.golemBuilder), golempressBlueprint, new ItemStack(Blocks.IRON_BARS), new ItemStack(Items.CAULDRON), new ItemStack(Blocks.PISTON), new ItemStack(Blocks.ANVIL), new ItemStack(BlocksTC.tableStone)));
    }

```

### thaumcraft:GolemPress @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:123

- Classification: GOLEM_MACHINE_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 119

```java
        Part GP3 = new Part(Blocks.PISTON.getDefaultState().withProperty((IProperty)BlockPistonBase.FACING, (Comparable)EnumFacing.UP), BlocksTC.golemBuilder);
        Part GP4 = new Part(Blocks.ANVIL, new ItemStack(BlocksTC.placeholderAnvil));
        Part GP5 = new Part(BlocksTC.tableStone, new ItemStack(BlocksTC.placeholderTable));
        Part[][][] golempressBlueprint = { { { null, null }, { GP1, null } }, { { GP2, GP4 }, { GP3, GP5 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("MINDCLOCKWORK", golempressBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("thaumcraft:GolemPress"), new ThaumcraftApi.BluePrint("MINDCLOCKWORK", new ItemStack(BlocksTC.golemBuilder), golempressBlueprint, new ItemStack(Blocks.IRON_BARS), new ItemStack(Items.CAULDRON), new ItemStack(Blocks.PISTON), new ItemStack(Blocks.ANVIL), new ItemStack(BlocksTC.tableStone)));
    }

    public static void initializeAlchemyRecipes() {
```

### thaumcraft:GolemPress @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:124

- Classification: GOLEM_MACHINE_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 120

```java
        Part GP4 = new Part(Blocks.ANVIL, new ItemStack(BlocksTC.placeholderAnvil));
        Part GP5 = new Part(BlocksTC.tableStone, new ItemStack(BlocksTC.placeholderTable));
        Part[][][] golempressBlueprint = { { { null, null }, { GP1, null } }, { { GP2, GP4 }, { GP3, GP5 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("MINDCLOCKWORK", golempressBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("thaumcraft:GolemPress"), new ThaumcraftApi.BluePrint("MINDCLOCKWORK", new ItemStack(BlocksTC.golemBuilder), golempressBlueprint, new ItemStack(Blocks.IRON_BARS), new ItemStack(Items.CAULDRON), new ItemStack(Blocks.PISTON), new ItemStack(Blocks.ANVIL), new ItemStack(BlocksTC.tableStone)));
    }

    public static void initializeAlchemyRecipes() {
        ResourceLocation visCrystalGroup = new ResourceLocation("thaumcraft:viscrystalgroup");
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/api/blocks/BlocksTC.java:135

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 131

```java
	public static Block centrifuge;
	public static Block hungryChest;
	public static Block jarNormal;
	public static Block jarVoid;
	public static Block jarBrain;
	public static Block bellows;
	public static Block smelterBasic;
	public static Block smelterThaumium;
	public static Block smelterVoid;
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java:24

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 20

```java
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.block.ModelBrain;
import thaumcraft.client.renderers.models.block.ModelJar;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.tiles.devices.TileJarBrain;
import thaumcraft.common.tiles.essentia.TileJar;
import thaumcraft.common.tiles.essentia.TileJarFillable;


```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java:49

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 45

```java
        GL11.glDisable(2884);
        GL11.glTranslatef((float)x + 0.5f, (float)y + 0.01f, (float)z + 0.5f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (tile instanceof TileJarBrain) {
            renderBrain((TileJarBrain)tile, x, y, z, f);
        }
        else if (tile instanceof TileJarFillable) {
            GL11.glDisable(2896);
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java:50

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 46

```java
        GL11.glTranslatef((float)x + 0.5f, (float)y + 0.01f, (float)z + 0.5f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (tile instanceof TileJarBrain) {
            renderBrain((TileJarBrain)tile, x, y, z, f);
        }
        else if (tile instanceof TileJarFillable) {
            GL11.glDisable(2896);
            if (((TileJarFillable)tile).blocked) {
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java:141

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 137

```java
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void renderBrain(TileJarBrain te, double x, double y, double z, float f) {
        float bob = MathHelper.sin(Minecraft.getMinecraft().player.ticksExisted / 14.0f) * 0.03f + 0.03f;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, -0.8f + bob, 0.0f);
        float f2;
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:38

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 34

```java
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTCTile;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.devices.TileJarBrain;
import thaumcraft.common.tiles.essentia.TileJarFillable;


public class BlockJar extends BlockTCTile implements ILabelable
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:91

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 87

```java
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileJarFillable) {
            spawnFilledJar(worldIn, pos, state, (TileJarFillable)te);
        }
        else if (te instanceof TileJarBrain) {
            spawnBrainJar(worldIn, pos, state, (TileJarBrain)te);
        }
        else {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:92

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 88

```java
        if (te instanceof TileJarFillable) {
            spawnFilledJar(worldIn, pos, state, (TileJarFillable)te);
        }
        else if (te instanceof TileJarBrain) {
            spawnBrainJar(worldIn, pos, state, (TileJarBrain)te);
        }
        else {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:103

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 99

```java
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if (te instanceof TileJarFillable) {
            spawnFilledJar(worldIn, pos, state, (TileJarFillable)te);
        }
        else if (te instanceof TileJarBrain) {
            spawnBrainJar(worldIn, pos, state, (TileJarBrain)te);
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, null, stack);
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:104

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 100

```java
        if (te instanceof TileJarFillable) {
            spawnFilledJar(worldIn, pos, state, (TileJarFillable)te);
        }
        else if (te instanceof TileJarBrain) {
            spawnBrainJar(worldIn, pos, state, (TileJarBrain)te);
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, null, stack);
        }
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:128

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 124

```java
        }
        spawnAsEntity(world, pos, drop);
    }

    private void spawnBrainJar(World world, BlockPos pos, IBlockState state, TileJarBrain te) {
        ItemStack drop = new ItemStack(this, 1, getMetaFromState(state));
        if (te.xp > 0) {
            drop.setTagInfo("xp", new NBTTagInt(te.xp));
        }
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:157

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 153

```java
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileJarBrain) {
            ((TileJarBrain)te).eatDelay = 40;
            if (!world.isRemote) {
                int var6 = world.rand.nextInt(Math.min(((TileJarBrain)te).xp + 1, 64));
                if (var6 > 0) {
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:158

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 154

```java

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileJarBrain) {
            ((TileJarBrain)te).eatDelay = 40;
            if (!world.isRemote) {
                int var6 = world.rand.nextInt(Math.min(((TileJarBrain)te).xp + 1, 64));
                if (var6 > 0) {
                    TileJarBrain tileJarBrain = (TileJarBrain)te;
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:160

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 156

```java
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileJarBrain) {
            ((TileJarBrain)te).eatDelay = 40;
            if (!world.isRemote) {
                int var6 = world.rand.nextInt(Math.min(((TileJarBrain)te).xp + 1, 64));
                if (var6 > 0) {
                    TileJarBrain tileJarBrain = (TileJarBrain)te;
                    tileJarBrain.xp -= var6;
                    int xp = var6;
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:162

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 158

```java
            ((TileJarBrain)te).eatDelay = 40;
            if (!world.isRemote) {
                int var6 = world.rand.nextInt(Math.min(((TileJarBrain)te).xp + 1, 64));
                if (var6 > 0) {
                    TileJarBrain tileJarBrain = (TileJarBrain)te;
                    tileJarBrain.xp -= var6;
                    int xp = var6;
                    while (xp > 0) {
                        int var7 = EntityXPOrb.getXPSplit(xp);
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:163

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 159

```java
            if (!world.isRemote) {
                int var6 = world.rand.nextInt(Math.min(((TileJarBrain)te).xp + 1, 64));
                if (var6 > 0) {
                    TileJarBrain tileJarBrain = (TileJarBrain)te;
                    tileJarBrain.xp -= var6;
                    int xp = var6;
                    while (xp > 0) {
                        int var7 = EntityXPOrb.getXPSplit(xp);
                        xp -= var7;
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:236

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 232

```java
    }

    public float getEnchantPowerBonus(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileJarBrain) {
            return 5.0f;
        }
        return super.getEnchantPowerBonus(world, pos);
    }
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:245

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 241

```java

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileJarBrain && ((TileJarBrain)tile).xp >= ((TileJarBrain)tile).xpMax) {
            FXDispatcher.INSTANCE.spark(pos.getX() + 0.5f, pos.getY() + 0.8f, pos.getZ() + 0.5f, 3.0f, 0.2f + rand.nextFloat() * 0.2f, 1.0f, 0.3f + rand.nextFloat() * 0.2f, 0.5f);
        }
    }

```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:256

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 252

```java
    }

    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileJarBrain) {
            float r = ((TileJarBrain)tile).xp / (float)((TileJarBrain)tile).xpMax;
            return MathHelper.floor(r * 14.0f) + ((((TileJarBrain)tile).xp > 0) ? 1 : 0);
        }
        if (tile != null && tile instanceof TileJarFillable) {
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:257

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 253

```java

    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileJarBrain) {
            float r = ((TileJarBrain)tile).xp / (float)((TileJarBrain)tile).xpMax;
            return MathHelper.floor(r * 14.0f) + ((((TileJarBrain)tile).xp > 0) ? 1 : 0);
        }
        if (tile != null && tile instanceof TileJarFillable) {
            float n = (float)((TileJarFillable)tile).amount;
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:258

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 254

```java
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileJarBrain) {
            float r = ((TileJarBrain)tile).xp / (float)((TileJarBrain)tile).xpMax;
            return MathHelper.floor(r * 14.0f) + ((((TileJarBrain)tile).xp > 0) ? 1 : 0);
        }
        if (tile != null && tile instanceof TileJarFillable) {
            float n = (float)((TileJarFillable)tile).amount;
            TileJarFillable tileJarFillable = (TileJarFillable)tile;
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJarBrainItem.java:15

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 11

```java
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.tiles.devices.TileJarBrain;


public class BlockJarBrainItem extends ItemBlock
{
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJarBrainItem.java:18

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 14

```java
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.tiles.devices.TileJarBrain;


public class BlockJarBrainItem extends ItemBlock
{
    public BlockJarBrainItem(Block block) {
        super(block);
    }
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJarBrainItem.java:20

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 16

```java


public class BlockJarBrainItem extends ItemBlock
{
    public BlockJarBrainItem(Block block) {
        super(block);
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJarBrainItem.java:28

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 24

```java
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean b = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (b && !world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileJarBrain) {
                TileJarBrain jar = (TileJarBrain)te;
                if (stack.hasTagCompound()) {
                    jar.xp = stack.getTagCompound().getInteger("xp");
                }
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/blocks/essentia/BlockJarBrainItem.java:29

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 25

```java
        boolean b = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (b && !world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileJarBrain) {
                TileJarBrain jar = (TileJarBrain)te;
                if (stack.hasTagCompound()) {
                    jar.xp = stack.getTagCompound().getInteger("xp");
                }
                te.markDirty();
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/config/ConfigBlocks.java:71

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 67

```java
import thaumcraft.common.blocks.essentia.BlockAlembic;
import thaumcraft.common.blocks.essentia.BlockCentrifuge;
import thaumcraft.common.blocks.essentia.BlockEssentiaTransport;
import thaumcraft.common.blocks.essentia.BlockJar;
import thaumcraft.common.blocks.essentia.BlockJarBrainItem;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.blocks.essentia.BlockSmelter;
import thaumcraft.common.blocks.essentia.BlockSmelterAux;
import thaumcraft.common.blocks.essentia.BlockSmelterVent;
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/config/ConfigBlocks.java:119

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 115

```java
import thaumcraft.common.tiles.devices.TileCondenser;
import thaumcraft.common.tiles.devices.TileDioptra;
import thaumcraft.common.tiles.devices.TileHungryChest;
import thaumcraft.common.tiles.devices.TileInfernalFurnace;
import thaumcraft.common.tiles.devices.TileJarBrain;
import thaumcraft.common.tiles.devices.TileLampArcane;
import thaumcraft.common.tiles.devices.TileLampFertility;
import thaumcraft.common.tiles.devices.TileLampGrowth;
import thaumcraft.common.tiles.devices.TileLevitator;
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/config/ConfigBlocks.java:326

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 322

```java
        BlocksTC.tubeFilter = registerBlock(new BlockTube(TileTubeFilter.class, "tube_filter"));
        BlocksTC.tubeBuffer = registerBlock(new BlockTube(TileTubeBuffer.class, "tube_buffer"));
        BlocksTC.jarNormal = registerBlock(new BlockJar(TileJarFillable.class, "jar_normal"), BlockJarItem.class);
        BlocksTC.jarVoid = registerBlock(new BlockJar(TileJarFillableVoid.class, "jar_void"), BlockJarItem.class);
        BlocksTC.jarBrain = registerBlock(new BlockJar(TileJarBrain.class, "jar_brain"), BlockJarBrainItem.class);
        BlocksTC.infusionMatrix = registerBlock(new BlockInfusionMatrix());
        BlocksTC.infernalFurnace = registerBlock(new BlockInfernalFurnace());
        BlocksTC.everfullUrn = registerBlock(new BlockWaterJug());
        BlocksTC.thaumatorium = registerBlock(new BlockThaumatorium(false));
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/config/ConfigBlocks.java:392

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 388

```java
        GameRegistry.registerTileEntity(TileHungryChest.class, "thaumcraft:TileChestHungry");
        GameRegistry.registerTileEntity(TileCentrifuge.class, "thaumcraft:TileCentrifuge");
        GameRegistry.registerTileEntity(TileJarFillable.class, "thaumcraft:TileJar");
        GameRegistry.registerTileEntity(TileJarFillableVoid.class, "thaumcraft:TileJarVoid");
        GameRegistry.registerTileEntity(TileJarBrain.class, "thaumcraft:TileJarBrain");
        GameRegistry.registerTileEntity(TileBellows.class, "thaumcraft:TileBellows");
        GameRegistry.registerTileEntity(TileSmelter.class, "thaumcraft:TileSmelter");
        GameRegistry.registerTileEntity(TileAlembic.class, "thaumcraft:TileAlembic");
        GameRegistry.registerTileEntity(TileInfusionMatrix.class, "thaumcraft:TileInfusionMatrix");
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:280

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: INFUSION_RECIPE_SOURCE
- Context start line: 276

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterEntropy"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalEntropy), 0, new AspectList().add(Aspect.ENTROPY, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterFlux"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalTaint), 4, new AspectList().add(Aspect.FLUX, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:focus_2"), new InfusionRecipe("FOCUSADVANCED@1", new ItemStack(ItemsTC.focus2), 3, new AspectList().add(Aspect.MAGIC, 25).add(Aspect.ORDER, 50), new ItemStack(ItemsTC.focus1), new ItemStack(ItemsTC.quicksilver), "gemDiamond", new ItemStack(ItemsTC.quicksilver), new ItemStack(Items.ENDER_PEARL)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:focus_3"), new InfusionRecipe("FOCUSGREATER@1", new ItemStack(ItemsTC.focus3), 5, new AspectList().add(Aspect.MAGIC, 25).add(Aspect.ORDER, 50).add(Aspect.VOID, 100), new ItemStack(ItemsTC.focus2), new ItemStack(ItemsTC.quicksilver), Ingredient.fromItem(ItemsTC.primordialPearl), new ItemStack(ItemsTC.quicksilver), new ItemStack(Items.NETHER_STAR)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:JarBrain"), new InfusionRecipe("JARBRAIN", new ItemStack(BlocksTC.jarBrain), 4, new AspectList().add(Aspect.MIND, 25).add(Aspect.SENSES, 25).add(Aspect.UNDEAD, 25), new ItemStack(BlocksTC.jarNormal), new ItemStack(ItemsTC.brain), new ItemStack(Items.SPIDER_EYE), new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.SPIDER_EYE)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VisAmulet"), new InfusionRecipe("VISAMULET", new ItemStack(ItemsTC.amuletVis, 1, 1), 6, new AspectList().add(Aspect.AURA, 50).add(Aspect.ENERGY, 100).add(Aspect.VOID, 50), new ItemStack(ItemsTC.baubles, 1, 0), new ItemStack(ItemsTC.visResonator), ThaumcraftApiHelper.makeCrystal(Aspect.AIR), ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), ThaumcraftApiHelper.makeCrystal(Aspect.WATER), ThaumcraftApiHelper.makeCrystal(Aspect.EARTH), ThaumcraftApiHelper.makeCrystal(Aspect.ORDER)));
        InfusionRunicAugmentRecipe ra = new InfusionRunicAugmentRecipe();
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:RunicArmor"), ra);
        for (int a = 0; a < 3; ++a) {
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/config/ConfigResearch.java:278

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 274

```java
        TheorycraftManager.registerCard(CardRealization.class);
    }

    private static void initWarp() {
        ThaumcraftApi.addWarpToItem(new ItemStack(BlocksTC.jarBrain), 1);
    }

    private static void initGolemancyResearch() {
    }
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/lib/research/theorycraft/AidBrainInAJar.java:11

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 7

```java
public class AidBrainInAJar implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return BlocksTC.jarBrain;
    }

    @Override
    public Class<TheorycraftCard>[] getCards() {
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/tiles/devices/TileJarBrain.java:14

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 10

```java
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.essentia.TileJar;


public class TileJarBrain extends TileJar
{
    public float field_40063_b;
    public float field_40061_d;
    public float field_40059_f;
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/tiles/devices/TileJarBrain.java:27

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 23

```java
    public int xpMax;
    public int eatDelay;
    long lastsigh;

    public TileJarBrain() {
        xp = 0;
        xpMax = 2000;
        eatDelay = 0;
        lastsigh = System.currentTimeMillis() + 1500L;
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/tiles/devices/TileJarBrain.java:84

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 80

```java
                double d = entity.posX - (pos.getX() + 0.5f);
                double d2 = entity.posZ - (pos.getZ() + 0.5f);
                field_40066_q = (float)Math.atan2(d2, d);
                field_40059_f += 0.1f;
                if (field_40059_f < 0.5f || TileJarBrain.rand.nextInt(40) == 0) {
                    float f3 = field_40061_d;
                    do {
                        field_40061_d += TileJarBrain.rand.nextInt(4) - TileJarBrain.rand.nextInt(4);
                    } while (f3 == field_40061_d);
```

### thaumcraft:JarBrain @ src/main/java/thaumcraft/common/tiles/devices/TileJarBrain.java:87

- Classification: JAR_BRAIN_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 83

```java
                field_40059_f += 0.1f;
                if (field_40059_f < 0.5f || TileJarBrain.rand.nextInt(40) == 0) {
                    float f3 = field_40061_d;
                    do {
                        field_40061_d += TileJarBrain.rand.nextInt(4) - TileJarBrain.rand.nextInt(4);
                    } while (f3 == field_40061_d);
                }
            }
            else {
```

### thaumcraft:MindBiothaumic @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:168

- Classification: MIND_COMPONENT_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 164

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:BottleTaint"), new CrucibleRecipe("BOTTLETAINT", new ItemStack(ItemsTC.bottleTaint), ItemPhial.makeFilledPhial(Aspect.FLUX), new AspectList().add(Aspect.FLUX, 30).add(Aspect.WATER, 30)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:BathSalts"), new CrucibleRecipe("BATHSALTS", new ItemStack(ItemsTC.bathSalts), new ItemStack(ItemsTC.salisMundus), new AspectList().add(Aspect.MIND, 40).add(Aspect.AIR, 40).add(Aspect.ORDER, 40).add(Aspect.LIFE, 40)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SaneSoap"), new CrucibleRecipe("SANESOAP", new ItemStack(ItemsTC.sanitySoap), new ItemStack(BlocksTC.fleshBlock), new AspectList().add(Aspect.MIND, 75).add(Aspect.ELDRITCH, 50).add(Aspect.ORDER, 75).add(Aspect.LIFE, 50)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealCollect"), new CrucibleRecipe("SEALCOLLECT", GolemHelper.getSealStack("thaumcraft:pickup"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealCollectAdv"), new CrucibleRecipe("SEALCOLLECT&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:pickup_advanced"), GolemHelper.getSealStack("thaumcraft:pickup"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStore"), new CrucibleRecipe("SEALSTORE", GolemHelper.getSealStack("thaumcraft:fill"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStoreAdv"), new CrucibleRecipe("SEALSTORE&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:fill_advanced"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmpty"), new CrucibleRecipe("SEALEMPTY", GolemHelper.getSealStack("thaumcraft:empty"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.VOID, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmptyAdv"), new CrucibleRecipe("SEALEMPTY&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:empty_advanced"), GolemHelper.getSealStack("thaumcraft:empty"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
```

### thaumcraft:MindBiothaumic @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:170

- Classification: MIND_COMPONENT_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 166

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SaneSoap"), new CrucibleRecipe("SANESOAP", new ItemStack(ItemsTC.sanitySoap), new ItemStack(BlocksTC.fleshBlock), new AspectList().add(Aspect.MIND, 75).add(Aspect.ELDRITCH, 50).add(Aspect.ORDER, 75).add(Aspect.LIFE, 50)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealCollect"), new CrucibleRecipe("SEALCOLLECT", GolemHelper.getSealStack("thaumcraft:pickup"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealCollectAdv"), new CrucibleRecipe("SEALCOLLECT&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:pickup_advanced"), GolemHelper.getSealStack("thaumcraft:pickup"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStore"), new CrucibleRecipe("SEALSTORE", GolemHelper.getSealStack("thaumcraft:fill"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStoreAdv"), new CrucibleRecipe("SEALSTORE&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:fill_advanced"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmpty"), new CrucibleRecipe("SEALEMPTY", GolemHelper.getSealStack("thaumcraft:empty"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.VOID, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmptyAdv"), new CrucibleRecipe("SEALEMPTY&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:empty_advanced"), GolemHelper.getSealStack("thaumcraft:empty"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealProvide"), new CrucibleRecipe("SEALPROVIDE", GolemHelper.getSealStack("thaumcraft:provider"), GolemHelper.getSealStack("thaumcraft:empty_advanced"), new AspectList().add(Aspect.EXCHANGE, 10).add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStock"), new CrucibleRecipe("SEALSTOCK", GolemHelper.getSealStack("thaumcraft:stock"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.MIND, 10).add(Aspect.DESIRE, 10)));
```

### thaumcraft:MindBiothaumic @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:172

- Classification: MIND_COMPONENT_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 168

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealCollectAdv"), new CrucibleRecipe("SEALCOLLECT&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:pickup_advanced"), GolemHelper.getSealStack("thaumcraft:pickup"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStore"), new CrucibleRecipe("SEALSTORE", GolemHelper.getSealStack("thaumcraft:fill"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStoreAdv"), new CrucibleRecipe("SEALSTORE&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:fill_advanced"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmpty"), new CrucibleRecipe("SEALEMPTY", GolemHelper.getSealStack("thaumcraft:empty"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.VOID, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmptyAdv"), new CrucibleRecipe("SEALEMPTY&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:empty_advanced"), GolemHelper.getSealStack("thaumcraft:empty"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealProvide"), new CrucibleRecipe("SEALPROVIDE", GolemHelper.getSealStack("thaumcraft:provider"), GolemHelper.getSealStack("thaumcraft:empty_advanced"), new AspectList().add(Aspect.EXCHANGE, 10).add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStock"), new CrucibleRecipe("SEALSTOCK", GolemHelper.getSealStack("thaumcraft:stock"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.MIND, 10).add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuard"), new CrucibleRecipe("SEALGUARD", GolemHelper.getSealStack("thaumcraft:guard"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 20).add(Aspect.PROTECT, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuardAdv"), new CrucibleRecipe("SEALGUARD&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:guard_advanced"), GolemHelper.getSealStack("thaumcraft:guard"), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MIND, 20)));
```

### thaumcraft:MindBiothaumic @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:176

- Classification: MIND_COMPONENT_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 172

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmptyAdv"), new CrucibleRecipe("SEALEMPTY&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:empty_advanced"), GolemHelper.getSealStack("thaumcraft:empty"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealProvide"), new CrucibleRecipe("SEALPROVIDE", GolemHelper.getSealStack("thaumcraft:provider"), GolemHelper.getSealStack("thaumcraft:empty_advanced"), new AspectList().add(Aspect.EXCHANGE, 10).add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStock"), new CrucibleRecipe("SEALSTOCK", GolemHelper.getSealStack("thaumcraft:stock"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.MIND, 10).add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuard"), new CrucibleRecipe("SEALGUARD", GolemHelper.getSealStack("thaumcraft:guard"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 20).add(Aspect.PROTECT, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuardAdv"), new CrucibleRecipe("SEALGUARD&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:guard_advanced"), GolemHelper.getSealStack("thaumcraft:guard"), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MIND, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealLumber"), new CrucibleRecipe("SEALLUMBER", GolemHelper.getSealStack("thaumcraft:lumber"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.PLANT, 40).add(Aspect.SENSES, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealUse"), new CrucibleRecipe("SEALUSE", GolemHelper.getSealStack("thaumcraft:use"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.CRAFT, 20).add(Aspect.SENSES, 10).add(Aspect.MIND, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealBreakAdv"), new CrucibleRecipe("SEALBREAK&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:breaker_advanced"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10).add(Aspect.TOOL, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:EverfullUrn"), new CrucibleRecipe("EVERFULLURN", new ItemStack(BlocksTC.everfullUrn), new ItemStack(Items.FLOWER_POT), new AspectList().add(Aspect.WATER, 30).add(Aspect.CRAFT, 10).add(Aspect.EARTH, 10)));
```

### thaumcraft:MindBiothaumic @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:179

- Classification: MIND_COMPONENT_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 175

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuard"), new CrucibleRecipe("SEALGUARD", GolemHelper.getSealStack("thaumcraft:guard"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 20).add(Aspect.PROTECT, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuardAdv"), new CrucibleRecipe("SEALGUARD&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:guard_advanced"), GolemHelper.getSealStack("thaumcraft:guard"), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MIND, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealLumber"), new CrucibleRecipe("SEALLUMBER", GolemHelper.getSealStack("thaumcraft:lumber"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.PLANT, 40).add(Aspect.SENSES, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealUse"), new CrucibleRecipe("SEALUSE", GolemHelper.getSealStack("thaumcraft:use"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.CRAFT, 20).add(Aspect.SENSES, 10).add(Aspect.MIND, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealBreakAdv"), new CrucibleRecipe("SEALBREAK&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:breaker_advanced"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10).add(Aspect.TOOL, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:EverfullUrn"), new CrucibleRecipe("EVERFULLURN", new ItemStack(BlocksTC.everfullUrn), new ItemStack(Items.FLOWER_POT), new AspectList().add(Aspect.WATER, 30).add(Aspect.CRAFT, 10).add(Aspect.EARTH, 10)));
    }

    public static void initializeArcaneRecipes(IForgeRegistry<IRecipe> iForgeRegistry) {
```

### thaumcraft:MindBiothaumic @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:334

- Classification: MIND_COMPONENT_BOUNDARY
- Hit kind: INFUSION_RECIPE_SOURCE
- Context start line: 330

```java
        InfusionEnchantmentRecipe IELAMPLIGHT = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.LAMPLIGHT, new AspectList().add(Aspect.LIGHT, 80).add(Aspect.AIR, 20), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), "nitor");
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IELAMPLIGHT"), IELAMPLIGHT);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IELAMPLIGHTFAKE"), new InfusionEnchantmentRecipe(IELAMPLIGHT, new ItemStack(Items.GOLDEN_PICKAXE)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:BootsTraveller"), new InfusionRecipe("BOOTSTRAVELLER", new ItemStack(ItemsTC.travellerBoots), 1, new AspectList().add(Aspect.FLIGHT, 100).add(Aspect.MOTION, 100), new ItemStack(Items.LEATHER_BOOTS, 1, 32767), ConfigItems.AIR_CRYSTAL, ConfigItems.AIR_CRYSTAL, new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(Items.FEATHER), new ItemStack(Items.FISH, 1, 32767)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MindBiothaumic"), new InfusionRecipe("MINDBIOTHAUMIC", new ItemStack(ItemsTC.mind, 1, 1), 4, new AspectList().add(Aspect.MIND, 50).add(Aspect.MECHANISM, 25), new ItemStack(ItemsTC.mind, 1, 0), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.mechanismComplex)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ArcaneBore"), new InfusionRecipe("ARCANEBORE", new ItemStack(ItemsTC.turretPlacer, 1, 2), 4, new AspectList().add(Aspect.ENERGY, 25).add(Aspect.EARTH, 25).add(Aspect.MECHANISM, 100).add(Aspect.VOID, 25).add(Aspect.MOTION, 25), new ItemStack(ItemsTC.turretPlacer), new ItemStack(BlocksTC.plankGreatwood), new ItemStack(BlocksTC.plankGreatwood), new ItemStack(ItemsTC.mechanismComplex), "plateBrass", Ingredient.fromItem(Items.DIAMOND_PICKAXE), Ingredient.fromItem(Items.DIAMOND_SHOVEL), new ItemStack(ItemsTC.morphicResonator), new ItemStack(ItemsTC.nuggets, 1, 10)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:LampGrowth"), new InfusionRecipe("LAMPGROWTH", new ItemStack(BlocksTC.lampGrowth), 4, new AspectList().add(Aspect.PLANT, 20).add(Aspect.LIGHT, 15).add(Aspect.LIFE, 15).add(Aspect.TOOL, 15), new ItemStack(BlocksTC.lampArcane), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.DYE, 1, 15), ConfigItems.EARTH_CRYSTAL, new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.DYE, 1, 15), ConfigItems.EARTH_CRYSTAL));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:LampFertility"), new InfusionRecipe("LAMPFERTILITY", new ItemStack(BlocksTC.lampFertility), 4, new AspectList().add(Aspect.BEAST, 20).add(Aspect.LIGHT, 15).add(Aspect.LIFE, 15).add(Aspect.DESIRE, 15), new ItemStack(BlocksTC.lampArcane), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.WHEAT), ConfigItems.FIRE_CRYSTAL, new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.CARROT), ConfigItems.FIRE_CRYSTAL));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ThaumiumFortressHelm"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressHelm), 3, new AspectList().add(Aspect.METAL, 50).add(Aspect.PROTECT, 20).add(Aspect.ENERGY, 25), new ItemStack(ItemsTC.thaumiumHelm, 1, 32767), "plateThaumium", "plateThaumium", new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.EMERALD)));
```

### thaumcraft:MindBiothaumic @ src/main/java/thaumcraft/common/golems/GolemProperties.java:207

- Classification: MIND_COMPONENT_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 203

```java
        GolemMaterial.register(new GolemMaterial("BRASS", new String[] { "MATSTUDBRASS" }, new ResourceLocation("thaumcraft", "textures/entity/golems/mat_brass.png"), 15638812, 16, 6, 3, new ItemStack(ItemsTC.plate, 1, 0), new ItemStack(ItemsTC.mechanismSimple), new EnumGolemTrait[] { EnumGolemTrait.LIGHT }));
        GolemMaterial.register(new GolemMaterial("THAUMIUM", new String[] { "MATSTUDTHAUMIUM" }, new ResourceLocation("thaumcraft", "textures/entity/golems/mat_thaumium.png"), 5257074, 24, 10, 4, new ItemStack(ItemsTC.plate, 1, 2), new ItemStack(ItemsTC.mechanismSimple), new EnumGolemTrait[] { EnumGolemTrait.HEAVY, EnumGolemTrait.FIREPROOF, EnumGolemTrait.BLASTPROOF }));
        GolemMaterial.register(new GolemMaterial("VOID", new String[] { "MATSTUDVOID" }, new ResourceLocation("thaumcraft", "textures/entity/golems/mat_void.png"), 1445161, 20, 6, 4, new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsTC.mechanismSimple), new EnumGolemTrait[] { EnumGolemTrait.REPAIR }));
        GolemHead.register(new GolemHead("BASIC", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_basic.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_basic.obj"), null, PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 0) }, new EnumGolemTrait[0]));
        GolemHead.register(new GolemHead("SMART", new String[] { "MINDBIOTHAUMIC" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_smart.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_smart.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 1) }, new EnumGolemTrait[] { EnumGolemTrait.SMART, EnumGolemTrait.FRAGILE }));
        GolemHead.register(new GolemHead("SMART_ARMORED", new String[] { "MINDBIOTHAUMIC", "GOLEMCOMBATADV" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_smartarmor.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_smart_armor.obj"), null, PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 1), new ItemStack(ItemsTC.plate), "base", new ItemStack(Blocks.WOOL) }, new EnumGolemTrait[] { EnumGolemTrait.SMART }));
        GolemHead.register(new GolemHead("SCOUT", new String[] { "GOLEMVISION" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_scout.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_scout.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 0), new ItemStack(ItemsTC.modules) }, new EnumGolemTrait[] { EnumGolemTrait.SCOUT, EnumGolemTrait.FRAGILE }));
        GolemHead.register(new GolemHead("SMART_SCOUT", new String[] { "GOLEMVISION", "MINDBIOTHAUMIC" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_smartscout.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_scout_smart.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 1), new ItemStack(ItemsTC.modules) }, new EnumGolemTrait[] { EnumGolemTrait.SCOUT, EnumGolemTrait.SMART, EnumGolemTrait.FRAGILE }));
        GolemArm.register(new GolemArm("BASIC", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_basic.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_arms_basic.obj"), null, PartModel.EnumAttachPoint.ARMS), new Object[0], new EnumGolemTrait[0]));
```

### thaumcraft:MindBiothaumic @ src/main/java/thaumcraft/common/golems/GolemProperties.java:208

- Classification: MIND_COMPONENT_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 204

```java
        GolemMaterial.register(new GolemMaterial("THAUMIUM", new String[] { "MATSTUDTHAUMIUM" }, new ResourceLocation("thaumcraft", "textures/entity/golems/mat_thaumium.png"), 5257074, 24, 10, 4, new ItemStack(ItemsTC.plate, 1, 2), new ItemStack(ItemsTC.mechanismSimple), new EnumGolemTrait[] { EnumGolemTrait.HEAVY, EnumGolemTrait.FIREPROOF, EnumGolemTrait.BLASTPROOF }));
        GolemMaterial.register(new GolemMaterial("VOID", new String[] { "MATSTUDVOID" }, new ResourceLocation("thaumcraft", "textures/entity/golems/mat_void.png"), 1445161, 20, 6, 4, new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsTC.mechanismSimple), new EnumGolemTrait[] { EnumGolemTrait.REPAIR }));
        GolemHead.register(new GolemHead("BASIC", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_basic.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_basic.obj"), null, PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 0) }, new EnumGolemTrait[0]));
        GolemHead.register(new GolemHead("SMART", new String[] { "MINDBIOTHAUMIC" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_smart.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_smart.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 1) }, new EnumGolemTrait[] { EnumGolemTrait.SMART, EnumGolemTrait.FRAGILE }));
        GolemHead.register(new GolemHead("SMART_ARMORED", new String[] { "MINDBIOTHAUMIC", "GOLEMCOMBATADV" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_smartarmor.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_smart_armor.obj"), null, PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 1), new ItemStack(ItemsTC.plate), "base", new ItemStack(Blocks.WOOL) }, new EnumGolemTrait[] { EnumGolemTrait.SMART }));
        GolemHead.register(new GolemHead("SCOUT", new String[] { "GOLEMVISION" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_scout.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_scout.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 0), new ItemStack(ItemsTC.modules) }, new EnumGolemTrait[] { EnumGolemTrait.SCOUT, EnumGolemTrait.FRAGILE }));
        GolemHead.register(new GolemHead("SMART_SCOUT", new String[] { "GOLEMVISION", "MINDBIOTHAUMIC" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_smartscout.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_scout_smart.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 1), new ItemStack(ItemsTC.modules) }, new EnumGolemTrait[] { EnumGolemTrait.SCOUT, EnumGolemTrait.SMART, EnumGolemTrait.FRAGILE }));
        GolemArm.register(new GolemArm("BASIC", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_basic.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_arms_basic.obj"), null, PartModel.EnumAttachPoint.ARMS), new Object[0], new EnumGolemTrait[0]));
        GolemArm.register(new GolemArm("FINE", new String[] { "MATSTUDBRASS" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_fine.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_arms_fine.obj"), null, PartModel.EnumAttachPoint.ARMS), new Object[] { new ItemStack(ItemsTC.mechanismSimple), "base" }, new EnumGolemTrait[] { EnumGolemTrait.DEFT, EnumGolemTrait.FRAGILE }));
```

### thaumcraft:MindBiothaumic @ src/main/java/thaumcraft/common/golems/GolemProperties.java:210

- Classification: MIND_COMPONENT_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 206

```java
        GolemHead.register(new GolemHead("BASIC", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_basic.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_basic.obj"), null, PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 0) }, new EnumGolemTrait[0]));
        GolemHead.register(new GolemHead("SMART", new String[] { "MINDBIOTHAUMIC" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_smart.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_smart.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 1) }, new EnumGolemTrait[] { EnumGolemTrait.SMART, EnumGolemTrait.FRAGILE }));
        GolemHead.register(new GolemHead("SMART_ARMORED", new String[] { "MINDBIOTHAUMIC", "GOLEMCOMBATADV" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_smartarmor.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_smart_armor.obj"), null, PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 1), new ItemStack(ItemsTC.plate), "base", new ItemStack(Blocks.WOOL) }, new EnumGolemTrait[] { EnumGolemTrait.SMART }));
        GolemHead.register(new GolemHead("SCOUT", new String[] { "GOLEMVISION" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_scout.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_scout.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 0), new ItemStack(ItemsTC.modules) }, new EnumGolemTrait[] { EnumGolemTrait.SCOUT, EnumGolemTrait.FRAGILE }));
        GolemHead.register(new GolemHead("SMART_SCOUT", new String[] { "GOLEMVISION", "MINDBIOTHAUMIC" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_smartscout.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_scout_smart.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 1), new ItemStack(ItemsTC.modules) }, new EnumGolemTrait[] { EnumGolemTrait.SCOUT, EnumGolemTrait.SMART, EnumGolemTrait.FRAGILE }));
        GolemArm.register(new GolemArm("BASIC", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_basic.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_arms_basic.obj"), null, PartModel.EnumAttachPoint.ARMS), new Object[0], new EnumGolemTrait[0]));
        GolemArm.register(new GolemArm("FINE", new String[] { "MATSTUDBRASS" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_fine.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_arms_fine.obj"), null, PartModel.EnumAttachPoint.ARMS), new Object[] { new ItemStack(ItemsTC.mechanismSimple), "base" }, new EnumGolemTrait[] { EnumGolemTrait.DEFT, EnumGolemTrait.FRAGILE }));
        GolemArm.register(new GolemArm("CLAWS", new String[] { "GOLEMCOMBATADV" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_claws.png"), new PartModelClaws(new ResourceLocation("thaumcraft", "models/obj/golem_arms_claws.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_arms_claws.png"), PartModel.EnumAttachPoint.ARMS), new Object[] { new ItemStack(ItemsTC.modules, 1, 1), new ItemStack(Items.SHEARS, 2), "base" }, new EnumGolemTrait[] { EnumGolemTrait.FIGHTER, EnumGolemTrait.CLUMSY, EnumGolemTrait.BRUTAL }));
        GolemArm.register(new GolemArm("BREAKERS", new String[] { "GOLEMBREAKER" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_breakers.png"), new PartModelBreakers(new ResourceLocation("thaumcraft", "models/obj/golem_arms_breakers.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_arms_breakers.png"), PartModel.EnumAttachPoint.ARMS), new Object[] { new ItemStack(Items.DIAMOND, 2), "base", new ItemStack(Blocks.PISTON, 2) }, new EnumGolemTrait[] { EnumGolemTrait.BREAKER, EnumGolemTrait.CLUMSY, EnumGolemTrait.BRUTAL }));
```

### thaumcraft:SealBreak @ src/main/java/com/sasmaster/glelwjgl/java/CoreGLE.java:165

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 161

```java
        context_.xformArray = xformArray;
        switch (gleGetJoinStyle() & 0xF) {
            case 1: {
                extrusion_raw_join(ncp, contour, contourNormal, up, npoints, pointArray, colourArray, xformArray);
                break;
            }
            case 2: {
                extrusion_angle_join(ncp, contour, contourNormal, up, npoints, pointArray, colourArray, xformArray);
                break;
```

### thaumcraft:SealBreak @ src/main/java/com/sasmaster/glelwjgl/java/CoreGLE.java:169

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 165

```java
                break;
            }
            case 2: {
                extrusion_angle_join(ncp, contour, contourNormal, up, npoints, pointArray, colourArray, xformArray);
                break;
            }
            case 3:
            case 4: {
                extrusion_round_or_cut_join(ncp, contour, contourNormal, up, npoints, pointArray, colourArray, xformArray);
```

### thaumcraft:SealBreak @ src/main/java/com/sasmaster/glelwjgl/java/CoreGLE.java:174

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 170

```java
            }
            case 3:
            case 4: {
                extrusion_round_or_cut_join(ncp, contour, contourNormal, up, npoints, pointArray, colourArray, xformArray);
                break;
            }
            default: {
                throw new GLEException("Join style is complete rubbish!");
            }
```

### thaumcraft:SealBreak @ src/main/java/com/sasmaster/glelwjgl/java/CoreGLE.java:458

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 454

```java
            for (int i = 1; i < npoints - 2; ++i) {
                diff = matrix.VEC_DIFF(pointArray[i + 1], pointArray[i]);
                len = matrix.VEC_LENGTH(diff);
                if (len != 0.0) {
                    break;
                }
            }
        }
        len = 1.0 / len;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/aspects/Aspect.java:69

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 65

```java
	}

	/**
	 * Shortcut constructor I use for the primal aspects -
	 * you shouldn't use this as making your own primal aspects will break all the things.
	 */
	public Aspect(String tag, int color, String chatcolor, int blend) {
		this(tag,color,(Aspect[])null, blend);
		setChatcolor(chatcolor);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/aspects/AspectList.java:84

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 80

```java
					if (e1!=null && e2!=null && e1.getTag().compareTo(e2.getTag())>0) {
						out[a] = e2;
						out[a+1] = e1;
						change = true;
						break;
					}
				}
			} while (change==true);
			return out;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/aspects/AspectList.java:112

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 108

```java
						Aspect eb = out[a+1];
						out[a] = eb;
						out[a+1] = ea;
						change = true;
						break;
					}
				}
			} while (change==true);
			return out;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/casters/FocusEngine.java:115

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 111

```java
				}

				if (node instanceof FocusPackage) {
					runFocusPackage((FocusPackage) node, prevTrajectories, prevTargets);
					break;
				}
				else
				if (node instanceof FocusMedium) {
					FocusMedium medium = (FocusMedium) node;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/casters/FocusEngine.java:125

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 121

```java
						for (Trajectory trajectory : prevTrajectories) {
							medium.execute(trajectory);
						}

					if (medium.hasIntermediary()) break;
				}
				else
				if (node instanceof FocusMod) {
					if (node instanceof FocusModSplit) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/casters/FocusEngine.java:137

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 133

```java
							sp.multiplyPower(focusPackage.getPower());
							split.execute();
							/*returnLast =*/ runFocusPackage(sp,split.supplyTrajectories(),split.supplyTargets());
						}
						break;
					} else {
						((FocusMod) node).execute();
					}
				}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/casters/FocusPackage.java:99

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 95

```java
			if (caster==null) {
				for (EntityLivingBase e : world.getEntities(EntityLivingBase.class, EntitySelectors.IS_ALIVE)) {
					if (getCasterUUID().equals(e.getUniqueID())) {
						caster = e;
						break;
					}
				}
			}
		} catch (Exception e) {}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/casters/FocusPackage.java:148

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 144

```java
				if (ut==EnumUnitType.PACKAGE) {
					FocusPackage fp = new FocusPackage();
					fp.deserialize(nodenbt.getCompoundTag("package"));
					nodes.add(fp);
					break;
				} else {
					IFocusElement fn = FocusEngine.getElement(nodenbt.getString("key"));
					if (fn!=null) {
						if (fn instanceof FocusNode) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/casters/FocusPackage.java:191

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 187

```java
				nodenbt.setString("key", node.getKey());
				if (node.getType()==EnumUnitType.PACKAGE) {
					nodenbt.setTag("package", ((FocusPackage)node).serialize());
					nodelist.appendTag(nodenbt);
					break;
				} else {
					if (node instanceof FocusNode && ((FocusNode)node).getSettingList()!=null)
						for (String ns : ((FocusNode)node).getSettingList()) {
							nodenbt.setInteger("setting."+ns, ((FocusNode)node).getSettingValue(ns));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/golems/EnumGolemTrait.java:22

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 18

```java
	SCOUT(new ResourceLocation("thaumcraft","textures/misc/golem/tag_scout.png")),
	ARMORED(new ResourceLocation("thaumcraft","textures/misc/golem/tag_armored.png")),
	BRUTAL(new ResourceLocation("thaumcraft","textures/misc/golem/tag_brutal.png")),
	FIREPROOF(new ResourceLocation("thaumcraft","textures/misc/golem/tag_fireproof.png")),
	BREAKER(new ResourceLocation("thaumcraft","textures/misc/golem/tag_breaker.png")),
	HAULER(new ResourceLocation("thaumcraft","textures/misc/golem/tag_hauler.png")),
	RANGED(new ResourceLocation("thaumcraft","textures/misc/golem/tag_ranged.png")),
	BLASTPROOF(new ResourceLocation("thaumcraft","textures/misc/golem/tag_blastproof.png"));

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/research/ScanningManager.java:75

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 71

```java
						scanned++;
					}
					if (scanned>=100) {
						player.sendStatusMessage(new TextComponentString("\u00a75\u00a7o"+I18n.translateToLocal("tc.invtoolarge")),true);
						break; // to prevent lag with massive inventories
					}
				}
			}
			return;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/research/theorycraft/CardPonder.java:41

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 37

```java
					continue;
				}
				data.addTotal(category, 1);
				a--;
				if (a<=0) break;
			}
		}
		data.addTotal("BASICS", 5);
		data.bonusDraws++;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/research/theorycraft/CardRethink.java:47

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 43

```java
			tries++;
			for (String category:data.categoryTotals.keySet()) {
				data.addTotal(category, -1);
				a--;
				if (a<=0 || !data.hasTotal(category)) break;
			}
		}
		data.bonusDraws++;
		data.addTotal("BASICS", MathHelper.getInt(player.getRNG(), 1, 10));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/research/theorycraft/ResearchTableData.java:277

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 273

```java
						boolean found=false;
						for (String cn:availCats) {
							if (cn.equals(card.getResearchCategory())) {
								found=true;
								break;
							}
						}
						if (!found) continue;
					}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/ThaumcraftApi.java:423

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 419

```java
			WeightedRandomLoot.lootBagCommon.add(new WeightedRandomLoot(item,weight));
		else {
			for (int rarity:bagTypes) {
				switch(rarity) {
					case 0: WeightedRandomLoot.lootBagCommon.add(new WeightedRandomLoot(item,weight)); break;
					case 1: WeightedRandomLoot.lootBagUncommon.add(new WeightedRandomLoot(item,weight)); break;
					case 2: WeightedRandomLoot.lootBagRare.add(new WeightedRandomLoot(item,weight)); break;
				}
			}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/ThaumcraftApi.java:424

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 420

```java
		else {
			for (int rarity:bagTypes) {
				switch(rarity) {
					case 0: WeightedRandomLoot.lootBagCommon.add(new WeightedRandomLoot(item,weight)); break;
					case 1: WeightedRandomLoot.lootBagUncommon.add(new WeightedRandomLoot(item,weight)); break;
					case 2: WeightedRandomLoot.lootBagRare.add(new WeightedRandomLoot(item,weight)); break;
				}
			}
		}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/ThaumcraftApi.java:425

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 421

```java
			for (int rarity:bagTypes) {
				switch(rarity) {
					case 0: WeightedRandomLoot.lootBagCommon.add(new WeightedRandomLoot(item,weight)); break;
					case 1: WeightedRandomLoot.lootBagUncommon.add(new WeightedRandomLoot(item,weight)); break;
					case 2: WeightedRandomLoot.lootBagRare.add(new WeightedRandomLoot(item,weight)); break;
				}
			}
		}
	}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/api/ThaumcraftInvHelper.java:68

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 64

```java
	}

	/**
		 * Unlike the normal nbt comparison used by itemstacks, this method only checks if all the tags in stackA is present and equal in stackB. Any extra tags in stackB is ignored.
		 * Some mods love adding their own nbt data to itemstacks which ends up breaking a lot of crafting recipes or similar checks
		 * This version of the check ignores capabilities as this method is primarily used on my side by things that do not have capabilities in any case.
		 * @param prime
		 * @param other
		 * @return
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/beams/FXBeamBore.java:177

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 173

```java
        }
        switch (type) {
            default: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam);
                break;
            }
            case 1: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam1);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/beams/FXBeamBore.java:181

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 177

```java
                break;
            }
            case 1: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam1);
                break;
            }
            case 2: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam2);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/beams/FXBeamBore.java:185

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 181

```java
                break;
            }
            case 2: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam2);
                break;
            }
            case 3: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam3);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/beams/FXBeamBore.java:189

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 185

```java
                break;
            }
            case 3: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam3);
                break;
            }
        }
        GL11.glTexParameterf(3553, 10242, 10497.0f);
        GL11.glTexParameterf(3553, 10243, 10497.0f);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/beams/FXBeamWand.java:196

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 192

```java
        }
        switch (type) {
            default: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam);
                break;
            }
            case 1: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam1);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/beams/FXBeamWand.java:200

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 196

```java
                break;
            }
            case 1: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam1);
                break;
            }
            case 2: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam2);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/beams/FXBeamWand.java:204

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 200

```java
                break;
            }
            case 2: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam2);
                break;
            }
            case 3: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam3);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/beams/FXBeamWand.java:208

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 204

```java
                break;
            }
            case 3: {
                Minecraft.getMinecraft().renderEngine.bindTexture(beam3);
                break;
            }
        }
        GL11.glTexParameterf(3553, 10242, 10497.0f);
        GL11.glTexParameterf(3553, 10243, 10497.0f);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/FXDispatcher.java:39

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 35

```java
import thaumcraft.client.fx.other.FXVoidStream;
import thaumcraft.client.fx.particles.FXBlockRunes;
import thaumcraft.client.fx.particles.FXBoreParticles;
import thaumcraft.client.fx.particles.FXBoreSparkle;
import thaumcraft.client.fx.particles.FXBreakingFade;
import thaumcraft.client.fx.particles.FXFireMote;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.client.fx.particles.FXGenericGui;
import thaumcraft.client.fx.particles.FXGenericP2E;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/FXDispatcher.java:632

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 628

```java
        float f = getWorld().rand.nextFloat() * 3.1415927f * 2.0f;
        float f2 = getWorld().rand.nextFloat() * 0.5f + 0.5f;
        float f3 = MathHelper.sin(f) * 2.0f * 0.5f * f2;
        float f4 = MathHelper.cos(f) * 2.0f * 0.5f * f2;
        FXBreakingFade fx = new FXBreakingFade(getWorld(), e.posX + f3, e.posY + getWorld().rand.nextFloat() * e.height, e.posZ + f4, Items.SLIME_BALL, 0);
        if (getWorld().rand.nextBoolean()) {
            fx.setRBGColorF(0.6f, 0.0f, 0.3f);
            fx.setAlphaF(0.4f);
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/FXDispatcher.java:646

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 642

```java
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
    }

    public void taintsplosionFX(Entity e) {
        FXBreakingFade fx = new FXBreakingFade(getWorld(), e.posX, e.posY + getWorld().rand.nextFloat() * e.height, e.posZ, Items.SLIME_BALL);
        if (getWorld().rand.nextBoolean()) {
            fx.setRBGColorF(0.6f, 0.0f, 0.3f);
            fx.setAlphaF(0.4f);
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/FXDispatcher.java:667

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 663

```java
            float f = getWorld().rand.nextFloat() * 3.1415927f * e.height;
            float f2 = getWorld().rand.nextFloat() * 0.5f + 0.5f;
            float f3 = MathHelper.sin(f) * e.height * 0.25f * f2;
            float f4 = MathHelper.cos(f) * e.height * 0.25f * f2;
            FXBreakingFade fx = new FXBreakingFade(getWorld(), e.posX + f3, e.posY, e.posZ + f4, Items.SLIME_BALL);
            fx.setRBGColorF(0.4f, 0.0f, 0.4f);
            fx.setAlphaF(0.5f);
            fx.setParticleMaxAge((int)(66.0f / (getWorld().rand.nextFloat() * 0.9f + 0.1f)));
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/FXDispatcher.java:687

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 683

```java
        float f = getWorld().rand.nextFloat() * 3.1415927f * 2.0f;
        float f2 = getWorld().rand.nextFloat() * 0.5f + 0.5f;
        float f3 = MathHelper.sin(f) * i * 0.5f * f2;
        float f4 = MathHelper.cos(f) * i * 0.5f * f2;
        FXBreakingFade fx = new FXBreakingFade(getWorld(), e.posX + f3, (e.getEntityBoundingBox().minY + e.getEntityBoundingBox().maxY) / 2.0, e.posZ + f4, Items.SLIME_BALL, 0);
        fx.setRBGColorF(0.7f, 0.0f, 1.0f);
        fx.setAlphaF(0.4f);
        fx.setParticleMaxAge((int)(66.0f / (getWorld().rand.nextFloat() * 0.9f + 0.1f)));
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/FXDispatcher.java:700

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 696

```java
        float f2 = getWorld().rand.nextFloat() * 0.5f + 0.5f;
        float f3 = MathHelper.sin(f) * 2.0f * 0.5f * f2;
        float f4 = MathHelper.cos(f) * 2.0f * 0.5f * f2;
        if (getWorld().isRemote) {
            FXBreakingFade fx = new FXBreakingFade(getWorld(), e.posX + f3, (e.getEntityBoundingBox().minY + e.getEntityBoundingBox().maxY) / 2.0, e.posZ + f4, Items.SLIME_BALL);
            fx.setRBGColorF(0.1f, 0.0f, 0.1f);
            fx.setAlphaF(0.4f);
            fx.setParticleMaxAge((int)(66.0f / (getWorld().rand.nextFloat() * 0.9f + 0.1f)));
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/FXDispatcher.java:792

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 788

```java
    }

    public void excavateFX(BlockPos pos, EntityLivingBase p, int progress) {
        RenderGlobal rg = Minecraft.getMinecraft().renderGlobal;
        rg.sendBlockBreakProgress(p.getEntityId(), pos, progress);
    }

    public Object beamCont(EntityLivingBase p, double tx, double ty, double tz, int type, int color, boolean reverse, float endmod, Object input, int impact) {
        FXBeamWand beamcon = null;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/FXDispatcher.java:933

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 929

```java
        ParticleEngine.addEffect(getWorld(), fx);
        return fx;
    }

    public void bottleTaintBreak(double x, double y, double z) {
        for (int k1 = 0; k1 < 8; ++k1) {
            getWorld().spawnParticle(EnumParticleTypes.ITEM_CRACK, x, y, z, getWorld().rand.nextGaussian() * 0.15, getWorld().rand.nextDouble() * 0.2, getWorld().rand.nextGaussian() * 0.15, Item.getIdFromItem(ItemsTC.bottleTaint));
        }
        getWorld().playSound(x, y, z, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0f, getWorld().rand.nextFloat() * 0.1f + 0.9f, false);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/FXDispatcher.java:937

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 933

```java
    public void bottleTaintBreak(double x, double y, double z) {
        for (int k1 = 0; k1 < 8; ++k1) {
            getWorld().spawnParticle(EnumParticleTypes.ITEM_CRACK, x, y, z, getWorld().rand.nextGaussian() * 0.15, getWorld().rand.nextDouble() * 0.2, getWorld().rand.nextGaussian() * 0.15, Item.getIdFromItem(ItemsTC.bottleTaint));
        }
        getWorld().playSound(x, y, z, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0f, getWorld().rand.nextFloat() * 0.1f + 0.9f, false);
    }

    public void arcLightning(double x, double y, double z, double tx, double ty, double tz, float r, float g, float b, float h) {
        if (h <= 0.0f) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/ParticleEngine.java:79

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 75

```java
                    if (parts.size() != 0) {
                        switch (layer) {
                            case 4: {
                                GlStateManager.blendFunc(770, 1);
                                break;
                            }
                            case 5: {
                                GlStateManager.blendFunc(770, 771);
                                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/ParticleEngine.java:83

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 79

```java
                                break;
                            }
                            case 5: {
                                GlStateManager.blendFunc(770, 771);
                                break;
                            }
                        }
                        Tessellator tessellator = Tessellator.getInstance();
                        BufferBuilder buffer = tessellator.getBuffer();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/ParticleEngine.java:143

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 139

```java
                if (parts.size() != 0) {
                    switch (layer) {
                        case 0: {
                            GlStateManager.blendFunc(770, 1);
                            break;
                        }
                        case 1: {
                            GlStateManager.blendFunc(770, 771);
                            break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/ParticleEngine.java:147

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 143

```java
                            break;
                        }
                        case 1: {
                            GlStateManager.blendFunc(770, 771);
                            break;
                        }
                        case 2: {
                            GlStateManager.blendFunc(770, 1);
                            GlStateManager.disableDepth();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/ParticleEngine.java:152

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 148

```java
                        }
                        case 2: {
                            GlStateManager.blendFunc(770, 1);
                            GlStateManager.disableDepth();
                            break;
                        }
                        case 3: {
                            GlStateManager.blendFunc(770, 771);
                            GlStateManager.disableDepth();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/ParticleEngine.java:157

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 153

```java
                        }
                        case 3: {
                            GlStateManager.blendFunc(770, 771);
                            GlStateManager.disableDepth();
                            break;
                        }
                    }
                    float f1 = ActiveRenderInfo.getRotationX();
                    float f2 = ActiveRenderInfo.getRotationZ();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/ParticleEngine.java:199

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 195

```java
                    switch (layer) {
                        case 2:
                        case 3: {
                            GlStateManager.enableDepth();
                            break;
                        }
                    }
                }
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java:2

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 1

```java
package thaumcraft.client.fx.particles;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java:14

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 10

```java
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class FXBreakingFade extends ParticleBreaking
{
    public FXBreakingFade(World worldIn, double p_i1197_2_, double p_i1197_4_, double p_i1197_6_, double p_i1197_8_, double p_i1197_10_, double p_i1197_12_, Item p_i1197_14_, int p_i1197_15_) {
        super(worldIn, p_i1197_2_, p_i1197_4_, p_i1197_6_, p_i1197_8_, p_i1197_10_, p_i1197_12_, p_i1197_14_, p_i1197_15_);
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java:16

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 12

```java

@SideOnly(Side.CLIENT)
public class FXBreakingFade extends ParticleBreaking
{
    public FXBreakingFade(World worldIn, double p_i1197_2_, double p_i1197_4_, double p_i1197_6_, double p_i1197_8_, double p_i1197_10_, double p_i1197_12_, Item p_i1197_14_, int p_i1197_15_) {
        super(worldIn, p_i1197_2_, p_i1197_4_, p_i1197_6_, p_i1197_8_, p_i1197_10_, p_i1197_12_, p_i1197_14_, p_i1197_15_);
    }

    public FXBreakingFade(World worldIn, double p_i1196_2_, double p_i1196_4_, double p_i1196_6_, Item p_i1196_8_, int p_i1196_9_) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java:20

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 16

```java
    public FXBreakingFade(World worldIn, double p_i1197_2_, double p_i1197_4_, double p_i1197_6_, double p_i1197_8_, double p_i1197_10_, double p_i1197_12_, Item p_i1197_14_, int p_i1197_15_) {
        super(worldIn, p_i1197_2_, p_i1197_4_, p_i1197_6_, p_i1197_8_, p_i1197_10_, p_i1197_12_, p_i1197_14_, p_i1197_15_);
    }

    public FXBreakingFade(World worldIn, double p_i1196_2_, double p_i1196_4_, double p_i1196_6_, Item p_i1196_8_, int p_i1196_9_) {
        super(worldIn, p_i1196_2_, p_i1196_4_, p_i1196_6_, p_i1196_8_, p_i1196_9_);
    }

    public FXBreakingFade(World worldIn, double p_i1195_2_, double p_i1195_4_, double p_i1195_6_, Item p_i1195_8_) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java:24

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 20

```java
    public FXBreakingFade(World worldIn, double p_i1196_2_, double p_i1196_4_, double p_i1196_6_, Item p_i1196_8_, int p_i1196_9_) {
        super(worldIn, p_i1196_2_, p_i1196_4_, p_i1196_6_, p_i1196_8_, p_i1196_9_);
    }

    public FXBreakingFade(World worldIn, double p_i1195_2_, double p_i1195_4_, double p_i1195_6_, Item p_i1195_8_) {
        super(worldIn, p_i1195_2_, p_i1195_4_, p_i1195_6_, p_i1195_8_);
    }

    public void setParticleMaxAge(int particleMaxAge) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java:63

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 59

```java
        }
        int i = getBrightnessForRender(p_180434_3_);
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        float f11 = (float)(prevPosX + (posX - prevPosX) * p_180434_3_ - FXBreakingFade.interpPosX);
        float f12 = (float)(prevPosY + (posY - prevPosY) * p_180434_3_ - FXBreakingFade.interpPosY);
        float f13 = (float)(prevPosZ + (posZ - prevPosZ) * p_180434_3_ - FXBreakingFade.interpPosZ);
        p_180434_1_.pos(f11 - p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 - p_180434_6_ * f10 - p_180434_8_ * f10).tex(f6, f9).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 - p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 - p_180434_6_ * f10 + p_180434_8_ * f10).tex(f6, f8).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java:64

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 60

```java
        int i = getBrightnessForRender(p_180434_3_);
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        float f11 = (float)(prevPosX + (posX - prevPosX) * p_180434_3_ - FXBreakingFade.interpPosX);
        float f12 = (float)(prevPosY + (posY - prevPosY) * p_180434_3_ - FXBreakingFade.interpPosY);
        float f13 = (float)(prevPosZ + (posZ - prevPosZ) * p_180434_3_ - FXBreakingFade.interpPosZ);
        p_180434_1_.pos(f11 - p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 - p_180434_6_ * f10 - p_180434_8_ * f10).tex(f6, f9).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 - p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 - p_180434_6_ * f10 + p_180434_8_ * f10).tex(f6, f8).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 + p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 + p_180434_6_ * f10 + p_180434_8_ * f10).tex(f7, f8).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/fx/particles/FXBreakingFade.java:65

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 61

```java
        int j = i >> 16 & 0xFFFF;
        int k = i & 0xFFFF;
        float f11 = (float)(prevPosX + (posX - prevPosX) * p_180434_3_ - FXBreakingFade.interpPosX);
        float f12 = (float)(prevPosY + (posY - prevPosY) * p_180434_3_ - FXBreakingFade.interpPosY);
        float f13 = (float)(prevPosZ + (posZ - prevPosZ) * p_180434_3_ - FXBreakingFade.interpPosZ);
        p_180434_1_.pos(f11 - p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 - p_180434_6_ * f10 - p_180434_8_ * f10).tex(f6, f9).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 - p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 - p_180434_6_ * f10 + p_180434_8_ * f10).tex(f6, f8).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 + p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 + p_180434_6_ * f10 + p_180434_8_ * f10).tex(f7, f8).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
        p_180434_1_.pos(f11 + p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 + p_180434_6_ * f10 - p_180434_8_ * f10).tex(f7, f9).color(particleRed, particleGreen, particleBlue, particleAlpha * fade).lightmap(j, k).endVertex();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:236

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 232

```java
            FocusNode node = (FocusNode)FocusEngine.getElement(sk);
            drawPart(node, gx + 38, 43 + gy + 25 * index, (node.getType() == IFocusElement.EnumUnitType.MOD) ? 24.0f : 32.0f, 220, isPointInRegion(gx + 38 - 10, 32 + gy + 24 * index, 20, 20, mx + guiLeft, my + guiTop));
            GL11.glTranslated(0.0, 0.0, -5.0);
            if (++index > 5) {
                break;
            }
        }
        count = 0;
        index = 0;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:251

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 247

```java
                List list = genPartText(node, -1);
                drawHoveringTextFixed(list, gx + 44, 46 + gy + 24 * index, fontRenderer, width - (guiLeft + xSize - 16));
            }
            if (++index > 5) {
                break;
            }
        }
        if (lastNodeHover >= 0) {
            FocusElementNode fn = table.data.get(lastNodeHover);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:512

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 508

```java
        RenderHelper.disableStandardItemLighting();
        for (GuiButton guibutton : buttonList) {
            if (guibutton.isMouseOver()) {
                guibutton.drawButtonForegroundLayer(mouseX - guiLeft, mouseY - guiTop);
                break;
            }
        }
        RenderHelper.enableGUIStandardItemLighting();
        if (scrollbarMainSide != null) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:571

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 567

```java
                        addNodeAt(FocusEngine.elements.get(sk), selectedNode, true);
                        return;
                    }
                    if (++index > 5) {
                        break;
                    }
                }
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:669

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 665

```java
        float b = color.getBlue() / 255.0f;
        switch (node.getType()) {
            case EFFECT: {
                UtilsFX.renderQuadCentered(GuiFocalManipulator.iEffect, (float)(scale * 0.9 + (mouseover ? 2 : 0)), r, g, b, 220, 771, 1.0f);
                break;
            }
            case MEDIUM: {
                if (!root) {
                    UtilsFX.renderQuadCentered(GuiFocalManipulator.iMedium, (float)(scale * 0.9 + (mouseover ? 2 : 0)), r, g, b, 220, 771, 1.0f);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:674

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 670

```java
            }
            case MEDIUM: {
                if (!root) {
                    UtilsFX.renderQuadCentered(GuiFocalManipulator.iMedium, (float)(scale * 0.9 + (mouseover ? 2 : 0)), r, g, b, 220, 771, 1.0f);
                    break;
                }
                break;
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:676

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 672

```java
                if (!root) {
                    UtilsFX.renderQuadCentered(GuiFocalManipulator.iMedium, (float)(scale * 0.9 + (mouseover ? 2 : 0)), r, g, b, 220, 771, 1.0f);
                    break;
                }
                break;
            }
        }
        GL11.glTranslated(0.0, 0.0, 1.0);
        UtilsFX.renderQuadCentered(FocusEngine.getElementIcon(node.getKey()), scale / 2.0f + (mouseover ? 2 : 0), 1.0f, 1.0f, 1.0f, bright, 771, 1.0f);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:734

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 730

```java
                    int[] c = table.data.get(previous.parent).children;
                    for (int a = 0; a < c.length; ++a) {
                        if (c[a] == previous.id) {
                            table.data.get(previous.parent).children[a] = fn.id;
                            break;
                        }
                    }
                }
                fn.target = node.canSupply(FocusNode.EnumSupplyType.TARGET);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:900

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 896

```java
                if (fn.node != null && fn.node instanceof FocusMedium) {
                    hasMedium = !(fn.node instanceof FocusMediumRoot);
                    if (fn.node.isExclusive()) {
                        hasExlusive = true;
                        break;
                    }
                }
                if (fn.node != null && fn.node.isExclusive()) {
                    excluded.add(fn.node.getKey());
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:933

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 929

```java
                        if (parent.node.canSupply(type)) {
                            switch (element.getType()) {
                                case EFFECT: {
                                    pEff.add(key);
                                    break;
                                }
                                case MEDIUM: {
                                    if (!hasExlusive && (!((FocusMedium)element).isExclusive() || !hasMedium)) {
                                        pMed.add(key);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:938

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 934

```java
                                }
                                case MEDIUM: {
                                    if (!hasExlusive && (!((FocusMedium)element).isExclusive() || !hasMedium)) {
                                        pMed.add(key);
                                        break;
                                    }
                                    break;
                                }
                                case MOD: {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:940

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 936

```java
                                    if (!hasExlusive && (!((FocusMedium)element).isExclusive() || !hasMedium)) {
                                        pMed.add(key);
                                        break;
                                    }
                                    break;
                                }
                                case MOD: {
                                    pMod.add(key);
                                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:944

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 940

```java
                                    break;
                                }
                                case MOD: {
                                    pMod.add(key);
                                    break;
                                }
                            }
                            break;
                        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java:947

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 943

```java
                                    pMod.add(key);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiGolemBuilder.java:329

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 325

```java
        drawTexturedModalRect(108, 60, 228, 124, 24, 24);
        for (GuiButton guibutton : buttonList) {
            if (guibutton.isMouseOver()) {
                guibutton.drawButtonForegroundLayer(mouseX - guiLeft, mouseY - guiTop);
                break;
            }
        }
        if (ContainerGolemBuilder.redo) {
            redoComps();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiLogistics.java:117

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 113

```java
            selectedSlot = -1;
            for (Slot slot : inventorySlots.inventorySlots) {
                if (selectedStack.isItemEqual(slot.getStack()) && ItemStack.areItemStackTagsEqual(selectedStack, slot.getStack())) {
                    selectedSlot = slot.slotNumber;
                    break;
                }
            }
        }
        if (selectedSlot >= 0 && !inventorySlots.getSlot(selectedSlot).getHasStack()) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java:481

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 477

```java
                fontRenderer.drawString((String)p.getLeft(), 32, 32 + q * 10, color);
                ++q;
                if (32 + (q + 1) * 10 > screenY) {
                    fontRenderer.drawString(I18n.translateToLocalFormatted("tc.search.more"), 22, 34 + q * 10, 11184810);
                    break;
                }
            }
        }
        genResearchBackgroundFixedPost(mx, my, par3, locX, locY);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java:592

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 588

```java
            if (iconResearch.getStages() != null) {
                for (ResearchStage stage : iconResearch.getStages()) {
                    if (stage.getWarp() > 0) {
                        hasWarp = true;
                        break;
                    }
                }
            }
            int var25 = iconResearch.getDisplayColumn() * 24 - locX;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java:829

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 825

```java
                SearchResult sr = (SearchResult)p.getRight();
                if (mx > 22 && mx < 18 + screenX && my >= 32 + q * 10 && my < 40 + q * 10) {
                    if (ThaumcraftCapabilities.knowsResearch(player, sr.key) && !sr.cat) {
                        mc.displayGuiScreen(new GuiResearchPage(ResearchCategories.getResearch(sr.key), sr.recipe, guiMapX, guiMapY));
                        break;
                    }
                    if (categoriesTC.contains(sr.key) || categoriesOther.contains(sr.key)) {
                        GuiResearchBrowser.searching = false;
                        searchField.setVisible(false);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java:844

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 840

```java
                        guiMapX = n;
                        double n2 = (GuiResearchBrowser.guiBoundsBottom + GuiResearchBrowser.guiBoundsTop) / 2;
                        tempMapY = n2;
                        guiMapY = n2;
                        break;
                    }
                }
                ++q;
                if (32 + (q + 1) * 10 > screenY) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java:849

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 845

```java
                    }
                }
                ++q;
                if (32 + (q + 1) * 10 > screenY) {
                    break;
                }
            }
        }
        try {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java:1127

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 1123

```java
                        if (!np && ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(rk, IPlayerKnowledge.EnumResearchFlag.PAGE)) {
                            np = true;
                        }
                        if (nr && np) {
                            break;
                        }
                        continue;
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchPage.java:283

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 279

```java
            if ((current == page || current == page + 1) && current < maxPages) {
                drawPage(pages.get(a), current % 2, sw, sh - 10, par1, par2);
            }
            if (++current > page + 1) {
                break;
            }
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchPage.java:1082

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 1078

```java
            int start = GuiResearchPage.aspectsPage * 5;
            for (Aspect aspect : knownPlayerAspects.getAspectsSortedByName()) {
                if (++count >= start) {
                    if (count > start + 4) {
                        break;
                    }
                    if (aspect.getImage() != null) {
                        int tx = x;
                        int ty = y + count % 5 * 40;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchPage.java:1599

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 1595

```java
                    showingKnowledge = false;
                    blockAccess = null;
                    GuiResearchPage.history.clear();
                    Minecraft.getMinecraft().player.playSound(SoundsTC.page, 0.7f, 0.9f);
                    break;
                }
                ++aa;
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchPage.java:1653

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 1649

```java
                    if (!drilldownLists.containsKey(GuiResearchPage.shownRecipe)) {
                        addRecipesToList(GuiResearchPage.shownRecipe, drilldownLists, new LinkedHashMap<ResourceLocation, ArrayList>(), GuiResearchPage.shownRecipe);
                    }
                    blockAccess = null;
                    break;
                }
            }
        }
        try {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchPage.java:1841

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 1837

```java
                    }
                    amt = playerKnowledge.getKnowledgeRaw(type, category);
                    if (amt > 0) {
                        ++tc;
                        break;
                    }
                }
            }
            heightRemaining -= 20 * tc;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchTable.java:446

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 442

```java
                    playButtonWrite();
                    cardChoices.clear();
                    cardSelected = false;
                    lastDraw = table.data.lastDraw;
                    break;
                }
                ++a3;
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchTable.java:708

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 704

```java
                    int var7 = mx - (5 + sx - 55 * cardChoices.size() + xx + cw * a);
                    int var8 = my - (100 + yy - 60);
                    if (cardZoomOut[a] >= 0.95 && !cardSelected && var7 >= 0 && var8 >= 0 && var7 < 100 && var8 < 120) {
                        pressed = a;
                        break;
                    }
                }
                if (pressed >= 0 && table.getStackInSlot(0) != null && table.getStackInSlot(0).getItemDamage() != table.getStackInSlot(0).getMaxDamage()) {
                    mc.playerController.sendEnchantPacket(inventorySlots.windowId, 4 + pressed);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiResearchTable.java:739

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 735

```java
                        }
                        cardSelected = true;
                        playButtonPageSelect();
                        mc.playerController.sendEnchantPacket(inventorySlots.windowId, 1);
                        break;
                    }
                }
                catch (Exception ex) {}
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiThaumatorium.java:148

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 144

```java
                    px = 0;
                    ++py;
                }
                if (++count >= 8) {
                    break;
                }
            }
            count = 0;
            px = 0;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiThaumatorium.java:161

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 157

```java
                    px = 0;
                    ++py;
                }
                if (++count >= 8) {
                    break;
                }
            }
        }
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiThaumatorium.java:187

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 183

```java
                px = 0;
                ++py;
            }
            if (++idx >= 6) {
                break;
            }
        }
        px = 0;
        py = 0;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiThaumatorium.java:206

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 202

```java
            int xx = mx - (x + 48 + px * 16);
            int yy = my - (y + 56 + py * 16);
            if (xx >= 0 && yy >= 0 && xx < 16 && yy < 16) {
                renderToolTip(cr.getRecipeOutput(), mx, my);
                break;
            }
            if (++px > 1) {
                px = 0;
                ++py;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiThaumatorium.java:213

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 209

```java
                px = 0;
                ++py;
            }
            if (++idx >= 6) {
                break;
            }
        }
        GL11.glDisable(3042);
        GL11.glDisable(2896);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiThaumatorium.java:271

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 267

```java
            if (x >= 0 && y >= 0 && x < 16 && y < 16) {
                PacketHandler.INSTANCE.sendToServer(new PacketSelectThaumotoriumRecipeToServer(player, inventory.getPos(), hash));
                playButtonSelect();
                lastHLUpdate = 0L;
                break;
            }
            if (++px > 1) {
                px = 0;
                ++py;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/GuiThaumatorium.java:278

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 274

```java
                px = 0;
                ++py;
            }
            if (++idx >= 6) {
                break;
            }
        }
        if (hashList.size() > 6) {
            if (index > 0) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/gui/plugins/GuiHoverButton.java:101

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 97

```java
            int qq = 0;
            for (String s : text) {
                if (s.endsWith(" " + TextFormatting.RESET)) {
                    text = text.subList(0, qq);
                    break;
                }
                ++qq;
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/lib/events/RenderEventHandler.java:275

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 271

```java
                        bot -= 10;
                    }
                    else if (a > 0 && event.getLines().get(a - 1) != null && event.getLines().get(a - 1).contains("    ")) {
                        RenderEventHandler.hudHandler.renderAspectsInGui((GuiContainer)gui, mc.player, event.getStack(), bot, event.getX(), event.getY());
                        break;
                    }
                }
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/lib/events/WandRenderingHandler.java:293

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 289

```java
                    if (k == 0) {
                        KeyHandler.radialActive = false;
                        KeyHandler.radialLock = true;
                        PacketHandler.INSTANCE.sendToServer(new PacketFocusChangeToServer(key));
                        break;
                    }
                }
                else {
                    fociHover.put(key, false);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/lib/obj/MaterialLibrary.java:70

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 66

```java
        BufferedReader lineReader = new BufferedReader(lineStream);
        while (true) {
            String currentLine = lineReader.readLine();
            if (currentLine == null) {
                break;
            }
            if (currentLine.length() == 0) {
                continue;
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/lib/obj/MeshLoader.java:111

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 107

```java
        currentMatLib = new MaterialLibrary();
        while (true) {
            String currentLine = lineReader.readLine();
            if (currentLine == null) {
                break;
            }
            if (currentLine.length() == 0) {
                continue;
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/lib/UtilsFX.java:356

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 352

```java
            while (textLineEntry.hasNext()) {
                String textLine = textLineEntry.next();
                if (fr.getStringWidth(textLine) > max) {
                    b = true;
                    break;
                }
            }
            if (b) {
                List tl = new ArrayList();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/models/gear/ModelCustomArmor.java:80

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 76

```java
            bipedRightArm.rotateAngleZ = 0.0f;
            switch (leftArmPose) {
                case EMPTY: {
                    bipedLeftArm.rotateAngleY = 0.0f;
                    break;
                }
                case BLOCK: {
                    bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5f - 0.9424779f;
                    bipedLeftArm.rotateAngleY = 0.5235988f;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/models/gear/ModelCustomArmor.java:85

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 81

```java
                }
                case BLOCK: {
                    bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5f - 0.9424779f;
                    bipedLeftArm.rotateAngleY = 0.5235988f;
                    break;
                }
                case ITEM: {
                    bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5f - 0.31415927f;
                    bipedLeftArm.rotateAngleY = 0.0f;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/models/gear/ModelCustomArmor.java:90

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 86

```java
                }
                case ITEM: {
                    bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5f - 0.31415927f;
                    bipedLeftArm.rotateAngleY = 0.0f;
                    break;
                }
            }
            switch (rightArmPose) {
                case EMPTY: {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/models/gear/ModelCustomArmor.java:96

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 92

```java
            }
            switch (rightArmPose) {
                case EMPTY: {
                    bipedRightArm.rotateAngleY = 0.0f;
                    break;
                }
                case BLOCK: {
                    bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5f - 0.9424779f;
                    bipedRightArm.rotateAngleY = -0.5235988f;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/models/gear/ModelCustomArmor.java:101

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 97

```java
                }
                case BLOCK: {
                    bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5f - 0.9424779f;
                    bipedRightArm.rotateAngleY = -0.5235988f;
                    break;
                }
                case ITEM: {
                    bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5f - 0.31415927f;
                    bipedRightArm.rotateAngleY = 0.0f;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/models/gear/ModelCustomArmor.java:106

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 102

```java
                }
                case ITEM: {
                    bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5f - 0.31415927f;
                    bipedRightArm.rotateAngleY = 0.0f;
                    break;
                }
            }
            if (swingProgress > 0.0f) {
                EnumHandSide enumhandside = getMainHand(entityIn);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java:37

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 33

```java
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            switch (tile.facing) {
                case 5: {
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 3: {
                    GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java:41

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 37

```java
                    break;
                }
                case 3: {
                    GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 2: {
                    GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java:45

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 41

```java
                    break;
                }
                case 2: {
                    GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
            }
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f, 0.5f, -0.376f);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java:73

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 69

```java
                        switch (dir.ordinal()) {
                            case 0: {
                                GL11.glTranslatef(-0.5f, 0.5f, 0.0f);
                                GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
                                break;
                            }
                            case 1: {
                                GL11.glTranslatef(0.5f, 0.5f, 0.0f);
                                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java:78

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 74

```java
                            }
                            case 1: {
                                GL11.glTranslatef(0.5f, 0.5f, 0.0f);
                                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                                break;
                            }
                            case 2: {
                                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java:82

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 78

```java
                                break;
                            }
                            case 2: {
                                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                                break;
                            }
                            case 3: {
                                GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java:86

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 82

```java
                                break;
                            }
                            case 3: {
                                GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                                break;
                            }
                            case 4: {
                                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java:90

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 86

```java
                                break;
                            }
                            case 4: {
                                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                                break;
                            }
                            case 5: {
                                GL11.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
                                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileAlembicRenderer.java:94

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 90

```java
                                break;
                            }
                            case 5: {
                                GL11.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
                                break;
                            }
                        }
                        modelBore.renderNozzle();
                        GL11.glPopMatrix();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java:56

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 52

```java
            switch (dir.getOpposite().ordinal()) {
                case 0: {
                    GL11.glTranslatef(-0.5f, 0.5f, 0.0f);
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
                    break;
                }
                case 1: {
                    GL11.glTranslatef(0.5f, 0.5f, 0.0f);
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java:61

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 57

```java
                }
                case 1: {
                    GL11.glTranslatef(0.5f, 0.5f, 0.0f);
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                    break;
                }
                case 2: {
                    GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java:65

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 61

```java
                    break;
                }
                case 2: {
                    GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 3: {
                    GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java:69

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 65

```java
                    break;
                }
                case 3: {
                    GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 4: {
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java:73

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 69

```java
                    break;
                }
                case 4: {
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 5: {
                    GL11.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileBellowsRenderer.java:77

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 73

```java
                    break;
                }
                case 5: {
                    GL11.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
            }
            model2.renderNozzle();
            GL11.glPopMatrix();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileGolemBuilderRenderer.java:58

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 54

```java
        if (tile.getWorld() != null) {
            switch (facing.ordinal()) {
                case 5: {
                    GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 4: {
                    GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileGolemBuilderRenderer.java:62

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 58

```java
                    break;
                }
                case 4: {
                    GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 3: {
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileGolemBuilderRenderer.java:66

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 62

```java
                    break;
                }
                case 3: {
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
            }
        }
        model.renderAllExcept("press");
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java:74

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 70

```java
                GL11.glBlendFunc(770, 771);
                switch (((TileJarFillable)tile).facing) {
                    case 3: {
                        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    }
                    case 5: {
                        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java:78

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 74

```java
                        break;
                    }
                    case 5: {
                        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    }
                    case 4: {
                        GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java:82

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 78

```java
                        break;
                    }
                    case 4: {
                        GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    }
                }
                float rot = (float)((((TileJarFillable)tile).aspectFilter.getTag().hashCode() + tile.getPos().getX() + ((TileJarFillable)tile).facing) % 4 - 2);
                GL11.glPushMatrix();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java:548

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 544

```java
            GL11.glPushMatrix();
            switch (dir) {
                case DOWN: {
                    drawPlaneYPos(te, x, y, z, partialTicks);
                    break;
                }
                case UP: {
                    drawPlaneYNeg(te, x, y, z, partialTicks);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java:552

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 548

```java
                    break;
                }
                case UP: {
                    drawPlaneYNeg(te, x, y, z, partialTicks);
                    break;
                }
                case WEST: {
                    drawPlaneXPos(te, x, y, z, partialTicks);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java:556

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 552

```java
                    break;
                }
                case WEST: {
                    drawPlaneXPos(te, x, y, z, partialTicks);
                    break;
                }
                case EAST: {
                    drawPlaneXNeg(te, x, y, z, partialTicks);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java:560

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 556

```java
                    break;
                }
                case EAST: {
                    drawPlaneXNeg(te, x, y, z, partialTicks);
                    break;
                }
                case NORTH: {
                    drawPlaneZPos(te, x, y, z, partialTicks);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java:564

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 560

```java
                    break;
                }
                case NORTH: {
                    drawPlaneZPos(te, x, y, z, partialTicks);
                    break;
                }
                case SOUTH: {
                    drawPlaneZNeg(te, x, y, z, partialTicks);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java:568

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 564

```java
                    break;
                }
                case SOUTH: {
                    drawPlaneZNeg(te, x, y, z, partialTicks);
                    break;
                }
            }
            GL11.glPopMatrix();
            GL11.glPushMatrix();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TilePatternCrafterRenderer.java:36

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 32

```java
        GL11.glTranslatef((float)x + 0.5f, (float)y + 0.75f, (float)z + 0.5f);
        switch (f) {
            case 5: {
                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case 4: {
                GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TilePatternCrafterRenderer.java:40

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 36

```java
                break;
            }
            case 4: {
                GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case 2: {
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TilePatternCrafterRenderer.java:44

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 40

```java
                break;
            }
            case 2: {
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
        }
        GL11.glPushMatrix();
        GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileResearchTableRenderer.java:34

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 30

```java
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        switch (BlockStateUtils.getFacing(table.getBlockMetadata())) {
            case EAST: {
                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case WEST: {
                GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileResearchTableRenderer.java:38

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 34

```java
                break;
            }
            case WEST: {
                GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case SOUTH: {
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileResearchTableRenderer.java:42

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 38

```java
                break;
            }
            case SOUTH: {
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
        }
        if (table.data != null) {
            tableModel.renderScroll(Aspect.ALCHEMY.getColor());
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java:39

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 35

```java
                GL11.glTranslatef((float)par2 + 0.5f + facing.getFrontOffsetX() / 1.99f, (float)par4 + 1.125f, (float)par6 + 0.5f + facing.getFrontOffsetZ() / 1.99f);
                switch (facing.ordinal()) {
                    case 5: {
                        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    }
                    case 4: {
                        GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java:43

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 39

```java
                        break;
                    }
                    case 4: {
                        GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    }
                    case 2: {
                        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java:47

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 43

```java
                        break;
                    }
                    case 2: {
                        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    }
                }
                GL11.glScaled(0.75, 0.75, 0.75);
                ItemStack is = recipe.getRecipeOutput().copy();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/lighting/LC.java:54

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 50

```java
        boolean offset = false;
        switch (side) {
            case 0: {
                offset = (vec.y <= 0.0);
                break;
            }
            case 1: {
                offset = (vec.y >= 1.0);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/lighting/LC.java:58

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 54

```java
                break;
            }
            case 1: {
                offset = (vec.y >= 1.0);
                break;
            }
            case 2: {
                offset = (vec.z <= 0.0);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/lighting/LC.java:62

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 58

```java
                break;
            }
            case 2: {
                offset = (vec.z <= 0.0);
                break;
            }
            case 3: {
                offset = (vec.z >= 1.0);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/lighting/LC.java:66

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 62

```java
                break;
            }
            case 3: {
                offset = (vec.z >= 1.0);
                break;
            }
            case 4: {
                offset = (vec.x <= 0.0);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/lighting/LC.java:70

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 66

```java
                break;
            }
            case 4: {
                offset = (vec.x <= 0.0);
                break;
            }
            case 5: {
                offset = (vec.x >= 1.0);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/lighting/LC.java:74

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 70

```java
                break;
            }
            case 5: {
                offset = (vec.x >= 1.0);
                break;
            }
        }
        if (!offset) {
            side += 6;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java:52

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 48

```java
        Vector3 hit = null;
        switch (side) {
            case 0: {
                hit = vec.XZintercept(end, cuboid.min.y);
                break;
            }
            case 1: {
                hit = vec.XZintercept(end, cuboid.max.y);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java:56

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 52

```java
                break;
            }
            case 1: {
                hit = vec.XZintercept(end, cuboid.max.y);
                break;
            }
            case 2: {
                hit = vec.XYintercept(end, cuboid.min.z);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java:60

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 56

```java
                break;
            }
            case 2: {
                hit = vec.XYintercept(end, cuboid.min.z);
                break;
            }
            case 3: {
                hit = vec.XYintercept(end, cuboid.max.z);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java:64

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 60

```java
                break;
            }
            case 3: {
                hit = vec.XYintercept(end, cuboid.max.z);
                break;
            }
            case 4: {
                hit = vec.YZintercept(end, cuboid.min.x);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java:68

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 64

```java
                break;
            }
            case 4: {
                hit = vec.YZintercept(end, cuboid.min.x);
                break;
            }
            case 5: {
                hit = vec.YZintercept(end, cuboid.max.x);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java:72

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 68

```java
                break;
            }
            case 5: {
                hit = vec.YZintercept(end, cuboid.max.x);
                break;
            }
        }
        if (hit == null) {
            return;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java:84

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 80

```java
            case 1: {
                if (!MathHelper.between(cuboid.min.x, hit.x, cuboid.max.x) || !MathHelper.between(cuboid.min.z, hit.z, cuboid.max.z)) {
                    return;
                }
                break;
            }
            case 2:
            case 3: {
                if (!MathHelper.between(cuboid.min.x, hit.x, cuboid.max.x) || !MathHelper.between(cuboid.min.y, hit.y, cuboid.max.y)) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java:91

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 87

```java
            case 3: {
                if (!MathHelper.between(cuboid.min.x, hit.x, cuboid.max.x) || !MathHelper.between(cuboid.min.y, hit.y, cuboid.max.y)) {
                    return;
                }
                break;
            }
            case 4:
            case 5: {
                if (!MathHelper.between(cuboid.min.y, hit.y, cuboid.max.y) || !MathHelper.between(cuboid.min.z, hit.z, cuboid.max.z)) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/raytracer/RayTracer.java:98

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 94

```java
            case 5: {
                if (!MathHelper.between(cuboid.min.y, hit.y, cuboid.max.y) || !MathHelper.between(cuboid.min.z, hit.z, cuboid.max.z)) {
                    return;
                }
                break;
            }
        }
        double dist = vec2.set(hit).subtract(start).magSquared();
        if (dist < s_dist) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/render/CCModel.java:788

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 784

```java
            switch (findSide(normal)) {
                case 0: {
                    Vector3 vec = vert.vec;
                    vec.y += offsets.min.y;
                    break;
                }
                case 1: {
                    Vector3 vec2 = vert.vec;
                    vec2.y += offsets.max.y;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/render/CCModel.java:793

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 789

```java
                }
                case 1: {
                    Vector3 vec2 = vert.vec;
                    vec2.y += offsets.max.y;
                    break;
                }
                case 2: {
                    Vector3 vec3 = vert.vec;
                    vec3.z += offsets.min.z;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/render/CCModel.java:798

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 794

```java
                }
                case 2: {
                    Vector3 vec3 = vert.vec;
                    vec3.z += offsets.min.z;
                    break;
                }
                case 3: {
                    Vector3 vec4 = vert.vec;
                    vec4.z += offsets.max.z;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/render/CCModel.java:803

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 799

```java
                }
                case 3: {
                    Vector3 vec4 = vert.vec;
                    vec4.z += offsets.max.z;
                    break;
                }
                case 4: {
                    Vector3 vec5 = vert.vec;
                    vec5.x += offsets.min.x;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/render/CCModel.java:808

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 804

```java
                }
                case 4: {
                    Vector3 vec5 = vert.vec;
                    vec5.x += offsets.min.x;
                    break;
                }
                case 5: {
                    Vector3 vec6 = vert.vec;
                    vec6.x += offsets.max.x;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/render/CCModel.java:813

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 809

```java
                }
                case 5: {
                    Vector3 vec6 = vert.vec;
                    vec6.x += offsets.max.x;
                    break;
                }
            }
        }
        return this;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/BlockCoord.java:172

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 168

```java
        switch (s) {
            case 0:
            case 1: {
                y = v;
                break;
            }
            case 2:
            case 3: {
                z = v;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/BlockCoord.java:177

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 173

```java
            }
            case 2:
            case 3: {
                z = v;
                break;
            }
            case 4:
            case 5: {
                x = v;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/BlockCoord.java:182

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 178

```java
            }
            case 4:
            case 5: {
                x = v;
                break;
            }
            default: {
                throw new IndexOutOfBoundsException("Switch Falloff");
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/Cuboid6.java:205

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 201

```java
    public Cuboid6 setSide(int s, double d) {
        switch (s) {
            case 0: {
                min.y = d;
                break;
            }
            case 1: {
                max.y = d;
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/Cuboid6.java:209

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 205

```java
                break;
            }
            case 1: {
                max.y = d;
                break;
            }
            case 2: {
                min.z = d;
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/Cuboid6.java:213

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 209

```java
                break;
            }
            case 2: {
                min.z = d;
                break;
            }
            case 3: {
                max.z = d;
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/Cuboid6.java:217

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 213

```java
                break;
            }
            case 3: {
                max.z = d;
                break;
            }
            case 4: {
                min.x = d;
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/Cuboid6.java:221

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 217

```java
                break;
            }
            case 4: {
                min.x = d;
                break;
            }
            case 5: {
                max.x = d;
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/Cuboid6.java:225

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 221

```java
                break;
            }
            case 5: {
                max.x = d;
                break;
            }
            default: {
                throw new IndexOutOfBoundsException("Switch Falloff");
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/CuboidCoord.java:104

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 100

```java
    public CuboidCoord setSide(int s, int v) {
        switch (s) {
            case 0: {
                min.y = v;
                break;
            }
            case 1: {
                max.y = v;
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/CuboidCoord.java:108

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 104

```java
                break;
            }
            case 1: {
                max.y = v;
                break;
            }
            case 2: {
                min.z = v;
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/CuboidCoord.java:112

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 108

```java
                break;
            }
            case 2: {
                min.z = v;
                break;
            }
            case 3: {
                max.z = v;
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/CuboidCoord.java:116

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 112

```java
                break;
            }
            case 3: {
                max.z = v;
                break;
            }
            case 4: {
                min.x = v;
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/CuboidCoord.java:120

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 116

```java
                break;
            }
            case 4: {
                min.x = v;
                break;
            }
            case 5: {
                max.x = v;
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/CuboidCoord.java:124

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 120

```java
                break;
            }
            case 5: {
                max.x = v;
                break;
            }
            default: {
                throw new IndexOutOfBoundsException("Switch Falloff");
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/Vector3.java:128

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 124

```java
        switch (s) {
            case 0:
            case 1: {
                y = v;
                break;
            }
            case 2:
            case 3: {
                z = v;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/Vector3.java:133

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 129

```java
            }
            case 2:
            case 3: {
                z = v;
                break;
            }
            case 4:
            case 5: {
                x = v;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/codechicken/lib/vec/Vector3.java:138

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 134

```java
            }
            case 4:
            case 5: {
                x = v;
                break;
            }
            default: {
                throw new IndexOutOfBoundsException("Switch Falloff");
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/basic/BlockPavingStone.java:84

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 80

```java
                if (!list.isEmpty()) {
                    for (Entity entity : list) {
                        if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer)) {
                            FXDispatcher.INSTANCE.blockRunes(pos.getX(), pos.getY() + 0.6f + random.nextFloat() * Math.max(0.8f, entity.getEyeHeight()), pos.getZ(), 0.6f + random.nextFloat() * 0.4f, 0.0f, 0.3f + random.nextFloat() * 0.7f, 20, 0.0f);
                            break;
                        }
                    }
                }
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/basic/BlockPillar.java:81

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 77

```java
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemById(0);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getBlock() == BlocksTC.pillarArcane) {
            spawnAsEntity(worldIn, pos, new ItemStack(BlocksTC.stoneArcane, 2));
        }
        if (state.getBlock() == BlocksTC.pillarAncient) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/basic/BlockPillar.java:91

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 87

```java
        }
        if (state.getBlock() == BlocksTC.pillarEldritch) {
            spawnAsEntity(worldIn, pos, new ItemStack(BlocksTC.stoneEldritchTile, 2));
        }
        super.breakBlock(worldIn, pos, state);
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getHorizontal(meta);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/BlockTCTile.java:54

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 50

```java
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        InventoryUtils.dropItems(worldIn, pos);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity != null && tileentity instanceof IEssentiaTransport && BlockTCTile.spillEssentia && !worldIn.isRemote) {
            int ess = ((IEssentiaTransport)tileentity).getEssentiaAmount(EnumFacing.UP);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/BlockTCTile.java:63

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 59

```java
            if (ess > 0) {
                AuraHelper.polluteAura(worldIn, pos, (float)ess, true);
            }
        }
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/crafting/BlockArcaneWorkbench.java:42

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 38

```java
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileArcaneWorkbench) {
            InventoryHelper.dropInventoryItems(world, pos, ((TileArcaneWorkbench)tileEntity).inventoryCraft);
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/crafting/BlockArcaneWorkbench.java:47

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 43

```java
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileArcaneWorkbench) {
            InventoryHelper.dropInventoryItems(world, pos, ((TileArcaneWorkbench)tileEntity).inventoryCraft);
        }
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }
}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/crafting/BlockCrucible.java:93

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 89

```java
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileCrucible) {
            ((TileCrucible)te).spillRemnants();
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/crafting/BlockCrucible.java:98

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 94

```java
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileCrucible) {
            ((TileCrucible)te).spillRemnants();
        }
        super.breakBlock(worldIn, pos, state);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/crafting/BlockGolemBuilder.java:62

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 58

```java
        return 0;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        destroy(worldIn, pos, state, pos);
        super.breakBlock(worldIn, pos, state);
    }

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/crafting/BlockGolemBuilder.java:64

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 60

```java

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        destroy(worldIn, pos, state, pos);
        super.breakBlock(worldIn, pos, state);
    }

    public static void destroy(World worldIn, BlockPos pos, IBlockState state, BlockPos startpos) {
        if (BlockGolemBuilder.ignore || worldIn.isRemote) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java:92

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 88

```java
        return 0;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (top && worldIn.getBlockState(pos.down()).getBlock() == BlocksTC.thaumatorium) {
            worldIn.setBlockState(pos.down(), BlocksTC.metalAlchemical.getDefaultState());
        }
        if (!top && worldIn.getBlockState(pos.up()).getBlock() == BlocksTC.thaumatoriumTop) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java:99

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 95

```java
        }
        if (!top && worldIn.getBlockState(pos.up()).getBlock() == BlocksTC.thaumatoriumTop) {
            worldIn.setBlockState(pos.up(), BlocksTC.metalAlchemical.getDefaultState());
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java:174

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 170

```java
            if (b instanceof BlockCondenserLattice || (fd == EnumFacing.DOWN && b == BlocksTC.condenser)) {
                switch (side) {
                    case 0: {
                        miny = 0.0f;
                        break;
                    }
                    case 1: {
                        maxy = 1.0f;
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java:178

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 174

```java
                        break;
                    }
                    case 1: {
                        maxy = 1.0f;
                        break;
                    }
                    case 2: {
                        minz = 0.0f;
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java:182

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 178

```java
                        break;
                    }
                    case 2: {
                        minz = 0.0f;
                        break;
                    }
                    case 3: {
                        maxz = 1.0f;
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java:186

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 182

```java
                        break;
                    }
                    case 3: {
                        maxz = 1.0f;
                        break;
                    }
                    case 4: {
                        minx = 0.0f;
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java:190

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 186

```java
                        break;
                    }
                    case 4: {
                        minx = 0.0f;
                        break;
                    }
                    case 5: {
                        maxx = 1.0f;
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java:194

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 190

```java
                        break;
                    }
                    case 5: {
                        maxx = 1.0f;
                        break;
                    }
                }
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockHungryChest.java:93

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 89

```java
        state = state.withProperty((IProperty)BlockHungryChest.FACING, (Comparable)enumfacing);
        worldIn.setBlockState(pos, state, 3);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockHungryChest.java:99

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 95

```java
        if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockInfernalFurnace.java:100

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 96

```java
        return Item.getItemById(0);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        destroyFurnace(worldIn, pos, state, pos);
        super.breakBlock(worldIn, pos, state);
    }

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockInfernalFurnace.java:102

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 98

```java

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        destroyFurnace(worldIn, pos, state, pos);
        super.breakBlock(worldIn, pos, state);
    }

    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (entity.posX < pos.getX() + 0.3f) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockInlay.java:331

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 327

```java
        }
        return state;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if (!worldIn.isRemote) {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockInlay.java:332

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 328

```java
        return state;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if (!worldIn.isRemote) {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockLamp.java:66

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 62

```java
        return bs;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileLampArcane) {
            ((TileLampArcane)te).removeLights();
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockLamp.java:71

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 67

```java
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileLampArcane) {
            ((TileLampArcane)te).removeLights();
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockPedestal.java:115

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 111

```java
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if (!worldIn.isRemote) {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockPedestal.java:116

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 112

```java
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if (!worldIn.isRemote) {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockRedstoneRelay.java:139

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 135

```java
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        notifyNeighbors(worldIn, pos, state);
    }

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/devices/BlockRedstoneRelay.java:140

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 136

```java
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        notifyNeighbors(worldIn, pos, state);
    }

    @SideOnly(Side.CLIENT)
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:80

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 76

```java
        return getStateFromMeta(meta);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        BlockJar.spillEssentia = false;
        super.breakBlock(worldIn, pos, state);
        BlockJar.spillEssentia = true;
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:82

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 78

```java

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        BlockJar.spillEssentia = false;
        super.breakBlock(worldIn, pos, state);
        BlockJar.spillEssentia = true;
    }

    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/essentia/BlockSmelter.java:105

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 101

```java
        BlockSmelter.keepInventory = false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileSmelter && !worldIn.isRemote && ((TileSmelter)tileentity).vis > 0) {
            int ess = ((TileSmelter)tileentity).vis;
            AuraHelper.polluteAura(worldIn, pos, (float)ess, true);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/essentia/BlockSmelter.java:111

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 107

```java
        if (tileentity instanceof TileSmelter && !worldIn.isRemote && ((TileSmelter)tileentity).vis > 0) {
            int ess = ((TileSmelter)tileentity).vis;
            AuraHelper.polluteAura(worldIn, pos, (float)ess, true);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World w, BlockPos pos, Random r) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java:168

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 164

```java
            if (te != null) {
                switch (side) {
                    case 0: {
                        miny = 0.0f;
                        break;
                    }
                    case 1: {
                        maxy = 1.0f;
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java:172

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 168

```java
                        break;
                    }
                    case 1: {
                        maxy = 1.0f;
                        break;
                    }
                    case 2: {
                        minz = 0.0f;
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java:176

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 172

```java
                        break;
                    }
                    case 2: {
                        minz = 0.0f;
                        break;
                    }
                    case 3: {
                        maxz = 1.0f;
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java:180

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 176

```java
                        break;
                    }
                    case 3: {
                        maxz = 1.0f;
                        break;
                    }
                    case 4: {
                        minx = 0.0f;
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java:184

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 180

```java
                        break;
                    }
                    case 4: {
                        minx = 0.0f;
                        break;
                    }
                    case 5: {
                        maxx = 1.0f;
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java:188

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 184

```java
                        break;
                    }
                    case 5: {
                        maxx = 1.0f;
                        break;
                    }
                }
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java:212

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 208

```java
        return 0;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileTube && ((TileTube)te).getEssentiaAmount(EnumFacing.UP) > 0) {
            if (!worldIn.isRemote) {
                AuraHelper.polluteAura(worldIn, pos, (float)((TileTube)te).getEssentiaAmount(EnumFacing.UP), true);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java:225

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 221

```java
                    FXDispatcher.INSTANCE.drawVentParticles(pos.getX() + 0.33 + worldIn.rand.nextFloat() * 0.33, pos.getY() + 0.33 + worldIn.rand.nextFloat() * 0.33, pos.getZ() + 0.33 + worldIn.rand.nextFloat() * 0.33, 0.0, 0.0, 0.0, Aspect.FLUX.getColor());
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (state.getBlock() == BlocksTC.tubeValve) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/misc/BlockHole.java:31

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 27

```java
    public BlockHole() {
        super(Material.ROCK);
        setUnlocalizedName("hole");
        setRegistryName("thaumcraft", "hole");
        setBlockUnbreakable();
        setResistance(6000000.0f);
        setSoundType(SoundType.CLOTH);
        setLightLevel(0.7f);
        setTickRandomly(true);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/misc/BlockPlaceholder.java:111

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 107

```java
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        Label_0265: {
            if ((state.getBlock() == BlocksTC.placeholderNetherbrick || state.getBlock() == BlocksTC.placeholderObsidian) && !BlockInfernalFurnace.ignore && !worldIn.isRemote) {
                for (int a = -1; a <= 1; ++a) {
                    for (int b = -1; b <= 1; ++b) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/misc/BlockPlaceholder.java:120

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 116

```java
                        for (int c = -1; c <= 1; ++c) {
                            IBlockState s = worldIn.getBlockState(pos.add(a, b, c));
                            if (s.getBlock() == BlocksTC.infernalFurnace) {
                                BlockInfernalFurnace.destroyFurnace(worldIn, pos.add(a, b, c), s, pos);
                                break Label_0265;
                            }
                        }
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/misc/BlockPlaceholder.java:133

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 129

```java
                        for (int c = -1; c <= 1; ++c) {
                            IBlockState s = worldIn.getBlockState(pos.add(a, b, c));
                            if (s.getBlock() == BlocksTC.golemBuilder) {
                                BlockGolemBuilder.destroy(worldIn, pos.add(a, b, c), s, pos);
                                break Label_0265;
                            }
                        }
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/misc/BlockPlaceholder.java:140

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 136

```java
                    }
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/world/plants/BlockLogsTC.java:79

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 75

```java
    public boolean isWood(IBlockAccess world, BlockPos pos) {
        return true;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        byte b0 = 4;
        int i = b0 + 1;
        if (worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
            for (BlockPos blockpos1 : BlockPos.getAllInBox(pos.add(-b0, -b0, -b0), pos.add(b0, b0, b0))) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/world/plants/BlockSaplingTC.java:83

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 79

```java
                    for (j = 0; j >= -1; --j) {
                        if (isTwoByTwoOfType(worldIn, pos, i, j, BlocksTC.saplingGreatwood)) {
                            object = new WorldGenGreatwoodTrees(true, false);
                            flag = true;
                            break Label_0111;
                        }
                    }
                }
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/world/taint/BlockTaint.java:113

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 109

```java
                    EnumFacing dir = EnumFacing.HORIZONTALS[random.nextInt(4)];
                    for (int a = 1; a < 4; ++a) {
                        if (!world.isAirBlock(pos.offset(dir).down(a))) {
                            doIt = false;
                            break;
                        }
                        if (world.getBlockState(pos.down(a)).getBlock() != this) {
                            doIt = false;
                            break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/world/taint/BlockTaint.java:117

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 113

```java
                            break;
                        }
                        if (world.getBlockState(pos.down(a)).getBlock() != this) {
                            doIt = false;
                            break;
                        }
                    }
                    if (doIt && tryToFall(world, pos, pos.offset(dir))) {
                        return;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintFeature.java:43

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 39

```java
    protected boolean canSilkHarvest() {
        return false;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            if (worldIn.rand.nextFloat() < 0.333f) {
                Entity e = new EntityTaintCrawler(worldIn);
                e.setLocationAndAngles(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, (float)worldIn.rand.nextInt(360), 0.0f);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintFeature.java:54

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 50

```java
            else {
                AuraHelper.polluteAura(worldIn, pos, 1.0f, true);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintLog.java:99

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 95

```java
    public boolean isWood(IBlockAccess world, BlockPos pos) {
        return true;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        byte b0 = 4;
        int i = b0 + 1;
        if (worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
            for (BlockPos blockpos1 : BlockPos.getAllInBox(pos.add(-b0, -b0, -b0), pos.add(b0, b0, b0))) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/config/ConfigItems.java:35

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 31

```java
import thaumcraft.common.entities.construct.ItemTurretPlacer;
import thaumcraft.common.golems.ItemGolemBell;
import thaumcraft.common.golems.ItemGolemPlacer;
import thaumcraft.common.golems.seals.ItemSealPlacer;
import thaumcraft.common.golems.seals.SealBreaker;
import thaumcraft.common.golems.seals.SealBreakerAdvanced;
import thaumcraft.common.golems.seals.SealButcher;
import thaumcraft.common.golems.seals.SealEmpty;
import thaumcraft.common.golems.seals.SealEmptyAdvanced;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/config/ConfigItems.java:36

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 32

```java
import thaumcraft.common.golems.ItemGolemBell;
import thaumcraft.common.golems.ItemGolemPlacer;
import thaumcraft.common.golems.seals.ItemSealPlacer;
import thaumcraft.common.golems.seals.SealBreaker;
import thaumcraft.common.golems.seals.SealBreakerAdvanced;
import thaumcraft.common.golems.seals.SealButcher;
import thaumcraft.common.golems.seals.SealEmpty;
import thaumcraft.common.golems.seals.SealEmptyAdvanced;
import thaumcraft.common.golems.seals.SealFill;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/config/ConfigItems.java:76

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 72

```java
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.ItemFocusPouch;
import thaumcraft.common.items.casters.foci.FocusEffectAir;
import thaumcraft.common.items.casters.foci.FocusEffectBreak;
import thaumcraft.common.items.casters.foci.FocusEffectCurse;
import thaumcraft.common.items.casters.foci.FocusEffectEarth;
import thaumcraft.common.items.casters.foci.FocusEffectExchange;
import thaumcraft.common.items.casters.foci.FocusEffectFire;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/config/ConfigItems.java:299

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 295

```java
        FocusEngine.registerElement(FocusEffectFrost.class, new ResourceLocation("thaumcraft", "textures/foci/frost.png"), 14811135);
        FocusEngine.registerElement(FocusEffectAir.class, new ResourceLocation("thaumcraft", "textures/foci/air.png"), 16777086);
        FocusEngine.registerElement(FocusEffectEarth.class, new ResourceLocation("thaumcraft", "textures/foci/earth.png"), 5685248);
        FocusEngine.registerElement(FocusEffectFlux.class, new ResourceLocation("thaumcraft", "textures/foci/flux.png"), 8388736);
        FocusEngine.registerElement(FocusEffectBreak.class, new ResourceLocation("thaumcraft", "textures/foci/break.png"), 9063176);
        FocusEngine.registerElement(FocusEffectRift.class, new ResourceLocation("thaumcraft", "textures/foci/rift.png"), 3084645);
        FocusEngine.registerElement(FocusEffectExchange.class, new ResourceLocation("thaumcraft", "textures/foci/exchange.png"), 5735255);
        FocusEngine.registerElement(FocusEffectCurse.class, new ResourceLocation("thaumcraft", "textures/foci/curse.png"), 6946821);
        FocusEngine.registerElement(FocusEffectHeal.class, new ResourceLocation("thaumcraft", "textures/foci/heal.png"), 14548997);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/config/ConfigItems.java:321

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 317

```java
        SealHandler.registerSeal(new SealButcher());
        SealHandler.registerSeal(new SealGuard());
        SealHandler.registerSeal(new SealGuardAdvanced());
        SealHandler.registerSeal(new SealLumber());
        SealHandler.registerSeal(new SealBreaker());
        SealHandler.registerSeal(new SealUse());
        SealHandler.registerSeal(new SealProvide());
        SealHandler.registerSeal(new SealStock());
        SealHandler.registerSeal(new SealBreakerAdvanced());
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/config/ConfigItems.java:325

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 321

```java
        SealHandler.registerSeal(new SealBreaker());
        SealHandler.registerSeal(new SealUse());
        SealHandler.registerSeal(new SealProvide());
        SealHandler.registerSeal(new SealStock());
        SealHandler.registerSeal(new SealBreakerAdvanced());
    }

    @SideOnly(Side.CLIENT)
    public static void initModelsAndVariants() {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:177

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 173

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealProvide"), new CrucibleRecipe("SEALPROVIDE", GolemHelper.getSealStack("thaumcraft:provider"), GolemHelper.getSealStack("thaumcraft:empty_advanced"), new AspectList().add(Aspect.EXCHANGE, 10).add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStock"), new CrucibleRecipe("SEALSTOCK", GolemHelper.getSealStack("thaumcraft:stock"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.MIND, 10).add(Aspect.DESIRE, 10)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuard"), new CrucibleRecipe("SEALGUARD", GolemHelper.getSealStack("thaumcraft:guard"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 20).add(Aspect.PROTECT, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuardAdv"), new CrucibleRecipe("SEALGUARD&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:guard_advanced"), GolemHelper.getSealStack("thaumcraft:guard"), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MIND, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealLumber"), new CrucibleRecipe("SEALLUMBER", GolemHelper.getSealStack("thaumcraft:lumber"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.PLANT, 40).add(Aspect.SENSES, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealUse"), new CrucibleRecipe("SEALUSE", GolemHelper.getSealStack("thaumcraft:use"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.CRAFT, 20).add(Aspect.SENSES, 10).add(Aspect.MIND, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealBreakAdv"), new CrucibleRecipe("SEALBREAK&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:breaker_advanced"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10).add(Aspect.TOOL, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:EverfullUrn"), new CrucibleRecipe("EVERFULLURN", new ItemStack(BlocksTC.everfullUrn), new ItemStack(Items.FLOWER_POT), new AspectList().add(Aspect.WATER, 30).add(Aspect.CRAFT, 10).add(Aspect.EARTH, 10)));
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:179

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 175

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuard"), new CrucibleRecipe("SEALGUARD", GolemHelper.getSealStack("thaumcraft:guard"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 20).add(Aspect.PROTECT, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuardAdv"), new CrucibleRecipe("SEALGUARD&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:guard_advanced"), GolemHelper.getSealStack("thaumcraft:guard"), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MIND, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealLumber"), new CrucibleRecipe("SEALLUMBER", GolemHelper.getSealStack("thaumcraft:lumber"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.PLANT, 40).add(Aspect.SENSES, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealUse"), new CrucibleRecipe("SEALUSE", GolemHelper.getSealStack("thaumcraft:use"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.CRAFT, 20).add(Aspect.SENSES, 10).add(Aspect.MIND, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealBreakAdv"), new CrucibleRecipe("SEALBREAK&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:breaker_advanced"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10).add(Aspect.TOOL, 20)));
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:EverfullUrn"), new CrucibleRecipe("EVERFULLURN", new ItemStack(BlocksTC.everfullUrn), new ItemStack(Items.FLOWER_POT), new AspectList().add(Aspect.WATER, 30).add(Aspect.CRAFT, 10).add(Aspect.EARTH, 10)));
    }

    public static void initializeArcaneRecipes(IForgeRegistry<IRecipe> iForgeRegistry) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:270

- Classification: SEAL_BOUNDARY
- Hit kind: INFUSION_RECIPE_SOURCE
- Context start line: 266

```java

    public static void initializeInfusionRecipes() {
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealHarvest"), new InfusionRecipe("SEALHARVEST", GolemHelper.getSealStack("thaumcraft:harvest"), 0, new AspectList().add(Aspect.PLANT, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(Items.MELON_SEEDS), new ItemStack(Items.BEETROOT_SEEDS), new ItemStack(Items.REEDS), new ItemStack(Blocks.CACTUS)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealButcher"), new InfusionRecipe("SEALBUTCHER", GolemHelper.getSealStack("thaumcraft:butcher"), 0, new AspectList().add(Aspect.BEAST, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), GolemHelper.getSealStack("thaumcraft:guard"), "leather", new ItemStack(Blocks.WOOL, 1, 32767), new ItemStack(Items.RABBIT_HIDE), new ItemStack(Items.PORKCHOP), new ItemStack(Items.MUTTON), new ItemStack(Items.BEEF)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealBreak"), new InfusionRecipe("SEALBREAK", GolemHelper.getSealStack("thaumcraft:breaker"), 1, new AspectList().add(Aspect.TOOL, 10).add(Aspect.ENTROPY, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), Ingredient.fromItem(Items.GOLDEN_AXE), Ingredient.fromItem(Items.GOLDEN_PICKAXE), Ingredient.fromItem(Items.GOLDEN_SHOVEL)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterAir"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalAir), 0, new AspectList().add(Aspect.AIR, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.AIR), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterFire"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalFire), 0, new AspectList().add(Aspect.FIRE, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterWater"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalWater), 0, new AspectList().add(Aspect.WATER, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.WATER), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterEarth"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalEarth), 0, new AspectList().add(Aspect.EARTH, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.EARTH), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/container/ContainerArcaneWorkbench.java:123

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 119

```java
            if (crystals != null && crystals.size() > 0) {
                for (Aspect aspect : crystals.getAspects()) {
                    if (ThaumcraftInvHelper.countTotalItemsIn(ThaumcraftInvHelper.wrapInventory(tileEntity.inventoryCraft, EnumFacing.UP), ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect)), ThaumcraftInvHelper.InvFilter.STRICT) < crystals.getAmount(aspect)) {
                        hasCrystals = false;
                        break;
                    }
                }
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/container/ContainerArcaneWorkbench.java:190

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 186

```java
                            if (!mergeItemStack(var4, 10 + st.getMetadata(), 11 + st.getMetadata(), false)) {
                                return ItemStack.EMPTY;
                            }
                            if (var4.getCount() == 0) {
                                break;
                            }
                        }
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/container/ContainerFocalManipulator.java:18

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 14

```java

public class ContainerFocalManipulator extends Container
{
    private TileFocalManipulator table;
    private int lastBreakTime;

    public ContainerFocalManipulator(InventoryPlayer inventoryPlayer, TileFocalManipulator tileEntity) {
        table = tileEntity;
        addSlotToContainer(new SlotFocus(tileEntity, 0, 31, 191));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/container/ContainerHandMirror.java:168

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 164

```java
                    var7.putStack(res);
                    var7.onSlotChanged();
                    stackin.shrink(res.getCount());
                    var5 = true;
                    break;
                }
                if (par4) {
                    --var6;
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/container/ContainerLogistics.java:100

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 96

```java
                    continue;
                }
                input.setInventorySlotContents(q, items.get(key2));
                if (++q >= input.getSizeInventory()) {
                    break;
                }
            }
            end = items.size() / 9 - 8;
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/container/ContainerPech.java:130

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 126

```java
    private void addStack(ItemStack s) {
        for (int a = 1; a < 5; ++a) {
            if (inventory.getStackInSlot(a).isEmpty()) {
                inventory.setInventorySlotContents(a, s);
                break;
            }
            if (inventory.getStackInSlot(a).isItemEqual(s) && inventory.getStackInSlot(a).getCount() + s.getCount() < inventory.getStackInSlot(a).getMaxStackSize()) {
                inventory.getStackInSlot(a).grow(s.getCount());
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/container/ContainerPotionSprayer.java:15

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 11

```java

public class ContainerPotionSprayer extends Container
{
    private TilePotionSprayer sprayer;
    private int lastBreakTime;

    public ContainerPotionSprayer(InventoryPlayer par1InventoryPlayer, TilePotionSprayer tilePotionSprayer) {
        sprayer = tilePotionSprayer;
        addSlotToContainer(new SlotPotion(tilePotionSprayer, 0, 56, 64));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/container/ContainerResearchTable.java:58

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 54

```java
            }
            for (ResearchTableData.CardChoice cc : tileEntity.data.cardChoices) {
                if (cc.selected) {
                    tileEntity.data.lastDraw = cc;
                    break;
                }
            }
            tileEntity.data.cardChoices.clear();
            tileEntity.syncTile(false);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/container/ContainerSpa.java:16

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 12

```java

public class ContainerSpa extends Container
{
    private TileSpa spa;
    private int lastBreakTime;

    public ContainerSpa(InventoryPlayer par1InventoryPlayer, TileSpa tileEntity) {
        spa = tileEntity;
        addSlotToContainer(new SlotLimitedByClass(ItemBathSalts.class, tileEntity, 0, 65, 31));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:80

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 76

```java
    long soundDelay;
    Object beam1;
    double beamLength;
    private static HashMap<Integer, ArrayList<ItemStack>> drops;
    int breakCounter;
    int digDelay;
    int digDelayMax;
    float radInc;
    public int spiral;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:101

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 97

```java
        maxPause = 100;
        soundDelay = 0L;
        beam1 = null;
        beamLength = 0.0;
        breakCounter = 0;
        digDelay = 0;
        digDelayMax = 0;
        radInc = 0.0f;
        spiral = 0;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:305

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 301

```java
                                    IItemHandler inventory = ThaumcraftInvHelper.getItemHandlerAt(getEntityWorld(), p, f);
                                    if (inventory != null) {
                                        InventoryUtils.ejectStackAt(getEntityWorld(), getPosition(), f, dropped);
                                        e2 = true;
                                        break;
                                    }
                                }
                                if (e2) {
                                    continue;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:315

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 311

```java
                                InventoryUtils.ejectStackAt(getEntityWorld(), getPosition(), getFacing().getOpposite(), dropped);
                            }
                        }
                    }
                    breakCounter += fp.xpCooldown;
                    items.clear();
                }
            }
            if (getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty()) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:320

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 316

```java
                    items.clear();
                }
            }
            if (getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty()) {
                if (breakCounter >= 50) {
                    breakCounter -= 50;
                    getHeldItemMainhand().damageItem(1, this);
                }
                if (getHeldItemMainhand().getCount() <= 0) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:321

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 317

```java
                }
            }
            if (getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty()) {
                if (breakCounter >= 50) {
                    breakCounter -= 50;
                    getHeldItemMainhand().damageItem(1, this);
                }
                if (getHeldItemMainhand().getCount() <= 0) {
                    setHeldItem(getActiveHand(), ItemStack.EMPTY);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:329

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 325

```java
                    setHeldItem(getActiveHand(), ItemStack.EMPTY);
                }
            }
            else {
                breakCounter = 0;
            }
            b = world.setBlockToAir(digTarget);
        }
        digTarget = null;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/construct/EntityTurretCrossbow.java:189

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 185

```java
                    if (d.getStackInSlot(a) != null && !d.getStackInSlot(a).isEmpty() && d.getStackInSlot(a).getItem() instanceof ItemArrow) {
                        setHeldItem(EnumHand.MAIN_HAND, d.decrStackSize(a, d.getStackInSlot(a).getCount()));
                        playSound(SoundsTC.ticks, 1.0f, 1.0f);
                        world.setEntityState(this, (byte)17);
                        break;
                    }
                }
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/construct/ItemTurretPlacer.java:52

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 48

```java
            EntityOwnedConstruct turret = null;
            switch (player.getHeldItem(hand).getItemDamage()) {
                case 0: {
                    turret = new EntityTurretCrossbow(world, blockpos);
                    break;
                }
                case 1: {
                    turret = new EntityTurretCrossbowAdvanced(world, blockpos);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/construct/ItemTurretPlacer.java:56

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 52

```java
                    break;
                }
                case 1: {
                    turret = new EntityTurretCrossbowAdvanced(world, blockpos);
                    break;
                }
                case 2: {
                    turret = new EntityArcaneBore(world, blockpos, player.getHorizontalFacing());
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/construct/ItemTurretPlacer.java:60

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 56

```java
                    break;
                }
                case 2: {
                    turret = new EntityArcaneBore(world, blockpos, player.getHorizontalFacing());
                    break;
                }
            }
            if (turret != null) {
                world.spawnEntity(turret);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/EntityFluxRift.java:345

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 341

```java
                    wisp.setType(Aspect.FLUX.getTag());
                }
                if (wisp.getCanSpawnHere() && world.spawnEntity(wisp)) {
                    didit = true;
                    break;
                }
                break;
            }
            case 1: {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/EntityFluxRift.java:347

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 343

```java
                if (wisp.getCanSpawnHere() && world.spawnEntity(wisp)) {
                    didit = true;
                    break;
                }
                break;
            }
            case 1: {
                EntityTaintSeedPrime seed = new EntityTaintSeedPrime(world);
                seed.setLocationAndAngles((int)(posX + rand.nextGaussian() * 5.0) + 0.5, (int)(posY + rand.nextGaussian() * 5.0), (int)(posZ + rand.nextGaussian() * 5.0) + 0.5, (float) world.rand.nextInt(360), 0.0f);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/EntityFluxRift.java:357

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 353

```java
                    didit = true;
                    seed.boost = getRiftSize();
                    AuraHelper.polluteAura(getEntityWorld(), getPosition(), (float)(getRiftSize() / 2), true);
                    setDead();
                    break;
                }
                break;
            }
            case 2: {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/EntityFluxRift.java:359

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 355

```java
                    AuraHelper.polluteAura(getEntityWorld(), getPosition(), (float)(getRiftSize() / 2), true);
                    setDead();
                    break;
                }
                break;
            }
            case 2: {
                List<EntityLivingBase> targets2 = world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(16.0, 16.0, 16.0));
                if (targets2 != null && targets2.size() > 0) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/EntityFluxRift.java:376

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 372

```java
                            target.addPotionEffect(pe);
                        }
                        catch (Exception ex) {}
                    }
                    break;
                }
                break;
            }
            case 3: {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/EntityFluxRift.java:378

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 374

```java
                        catch (Exception ex) {}
                    }
                    break;
                }
                break;
            }
            case 3: {
                EntityPlayer target2 = world.getClosestPlayerToEntity(this, 16.0);
                if (target2 != null) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/EntityFluxRift.java:394

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 390

```java
                    fp.getSetting("duration").setValue(MathHelper.getInt(rand, Math.min(getRiftSize() / 2, 30), Math.min(getRiftSize(), 120)));
                    p.addNode(fp);
                    p.addNode(new FocusEffectFlux());
                    FocusEngine.castFocusPackage(target2, p, true);
                    break;
                }
                break;
            }
            case 4: {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/EntityFluxRift.java:396

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 392

```java
                    p.addNode(new FocusEffectFlux());
                    FocusEngine.castFocusPackage(target2, p, true);
                    break;
                }
                break;
            }
            case 4: {
                setCollapse(true);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/EntityFluxRift.java:400

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 396

```java
                break;
            }
            case 4: {
                setCollapse(true);
                break;
            }
        }
        if (didit) {
            setRiftStability(getRiftStability() + ei.cost);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/EntityFluxRift.java:497

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 493

```java
                        ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)p, w, IPlayerWarp.EnumWarpType.NORMAL);
                        ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)p, w, IPlayerWarp.EnumWarpType.TEMPORARY);
                    }
                }
                break;
            }
        }
        setDead();
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java:126

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 122

```java
                            int face = 0;
                            switch (dir.ordinal()) {
                                case 2: {
                                    face = 8;
                                    break;
                                }
                                case 3: {
                                    face = 0;
                                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java:130

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 126

```java
                                    break;
                                }
                                case 3: {
                                    face = 0;
                                    break;
                                }
                                case 4: {
                                    face = 12;
                                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java:134

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 130

```java
                                    break;
                                }
                                case 4: {
                                    face = 12;
                                    break;
                                }
                                case 5: {
                                    face = 4;
                                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java:138

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 134

```java
                                    break;
                                }
                                case 5: {
                                    face = 4;
                                    break;
                                }
                            }
                            ((TileBanner)te).setBannerFacing((byte)face);
                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(new BlockPos((int) posX - dir.getFrontOffsetX() * 6, (int) posY, (int) posZ + dir.getFrontOffsetZ() * 6), this, 0.5f + rand.nextFloat() * 0.2f, 0.0f, 0.0f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 32.0));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java:159

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 155

```java
                        Block bb = BlocksTC.lootCrateCommon;
                        switch (md) {
                            case 1: {
                                bb = BlocksTC.lootCrateUncommon;
                                break;
                            }
                            case 2: {
                                bb = BlocksTC.lootCrateRare;
                                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java:163

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 159

```java
                                break;
                            }
                            case 2: {
                                bb = BlocksTC.lootCrateRare;
                                break;
                            }
                        }
                        world.setBlockState(bp2, bb.getDefaultState());
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(new BlockPos(a, (int) posY, b), this, 0.5f + rand.nextFloat() * 0.2f, 0.0f, 0.0f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 32.0));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java:182

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 178

```java
                    case 3:
                    case 4: {
                        stagecounter = 15 + rand.nextInt(10 - stage) - stage;
                        spawnMinions();
                        break;
                    }
                    case 12: {
                        stagecounter = 50 + getTiming() * 2 + rand.nextInt(50);
                        spawnBoss();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java:187

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 183

```java
                    }
                    case 12: {
                        stagecounter = 50 + getTiming() * 2 + rand.nextInt(50);
                        spawnBoss();
                        break;
                    }
                    default: {
                        int t = getTiming();
                        stagecounter = t + rand.nextInt(5 + t / 3);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortalGreater.java:193

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 189

```java
                    default: {
                        int t = getTiming();
                        stagecounter = t + rand.nextInt(5 + t / 3);
                        spawnMinions();
                        break;
                    }
                }
                ++stage;
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/cult/EntityCultist.java:27

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 23

```java
    public EntityCultist(World p_i1745_1_) {
        super(p_i1745_1_);
        setSize(0.6f, 1.8f);
        experienceValue = 10;
        ((PathNavigateGround) getNavigator()).setBreakDoors(true);
        setDropChance(EntityEquipmentSlot.CHEST, 0.05f);
        setDropChance(EntityEquipmentSlot.FEET, 0.05f);
        setDropChance(EntityEquipmentSlot.HEAD, 0.05f);
        setDropChance(EntityEquipmentSlot.LEGS, 0.05f);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/EntityEldritchCrab.java:55

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 51

```java
        super(par1World);
        attackTime = 0;
        setSize(0.8f, 0.6f);
        experienceValue = 6;
        ((PathNavigateGround) getNavigator()).setBreakDoors(true);
    }

    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java:66

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 62

```java
        super(p_i1745_1_);
        armLiftL = 0.0f;
        armLiftR = 0.0f;
        lastBlast = false;
        ((PathNavigateGround) getNavigator()).setBreakDoors(true);
        setSize(0.8f, 2.25f);
        experienceValue = 20;
    }

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/EntityPech.java:239

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 235

```java
        switch (rand.nextInt(20)) {
            case 0:
            case 12: {
                setHeldItem(getActiveHand(), new ItemStack(ItemsTC.pechWand));
                break;
            }
            case 1: {
                setHeldItem(getActiveHand(), new ItemStack(Items.STONE_SWORD));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/EntityPech.java:243

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 239

```java
                break;
            }
            case 1: {
                setHeldItem(getActiveHand(), new ItemStack(Items.STONE_SWORD));
                break;
            }
            case 3: {
                setHeldItem(getActiveHand(), new ItemStack(Items.STONE_AXE));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/EntityPech.java:247

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 243

```java
                break;
            }
            case 3: {
                setHeldItem(getActiveHand(), new ItemStack(Items.STONE_AXE));
                break;
            }
            case 5: {
                setHeldItem(getActiveHand(), new ItemStack(Items.IRON_SWORD));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/EntityPech.java:251

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 247

```java
                break;
            }
            case 5: {
                setHeldItem(getActiveHand(), new ItemStack(Items.IRON_SWORD));
                break;
            }
            case 6: {
                setHeldItem(getActiveHand(), new ItemStack(Items.IRON_AXE));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/EntityPech.java:255

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 251

```java
                break;
            }
            case 6: {
                setHeldItem(getActiveHand(), new ItemStack(Items.IRON_AXE));
                break;
            }
            case 7: {
                setHeldItem(getActiveHand(), new ItemStack(Items.FISHING_ROD));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/EntityPech.java:259

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 255

```java
                break;
            }
            case 7: {
                setHeldItem(getActiveHand(), new ItemStack(Items.FISHING_ROD));
                break;
            }
            case 8: {
                setHeldItem(getActiveHand(), new ItemStack(Items.STONE_PICKAXE));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/EntityPech.java:263

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 259

```java
                break;
            }
            case 8: {
                setHeldItem(getActiveHand(), new ItemStack(Items.STONE_PICKAXE));
                break;
            }
            case 9: {
                setHeldItem(getActiveHand(), new ItemStack(Items.IRON_PICKAXE));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/EntityPech.java:267

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 263

```java
                break;
            }
            case 9: {
                setHeldItem(getActiveHand(), new ItemStack(Items.IRON_PICKAXE));
                break;
            }
            case 2:
            case 4:
            case 10:
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/EntityPech.java:275

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 271

```java
            case 10:
            case 11:
            case 13: {
                setHeldItem(getActiveHand(), new ItemStack(Items.BOW));
                break;
            }
        }
    }

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/monster/tainted/EntityTaintSwarm.java:113

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 109

```java
        if (world.isRemote) {
            for (int a = 0; a < swarm.size(); ++a) {
                if (swarm.get(a) == null || !swarm.get(a).isAlive()) {
                    swarm.remove(a);
                    break;
                }
            }
            if (swarm.size() < 30) {
                swarm.add(FXDispatcher.INSTANCE.swarmParticleFX(this, 0.22f, 15.0f, 0.08f));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/projectile/EntityBottleTaint.java:41

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 37

```java
        if (id == 3) {
            for (int a = 0; a < 100; ++a) {
                FXDispatcher.INSTANCE.taintsplosionFX(this);
            }
            FXDispatcher.INSTANCE.bottleTaintBreak(posX, posY, posZ);
        }
    }

    protected void onImpact(RayTraceResult ray) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/projectile/EntityFocusProjectile.java:202

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 198

```java
                    }
                    boolean f = EntityUtils.isFriendly(getThrower(), pt);
                    if (f && getSpecial() == 3) {
                        target = pt;
                        break;
                    }
                    if (!f && getSpecial() == 2) {
                        target = pt;
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/projectile/EntityFocusProjectile.java:206

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 202

```java
                        break;
                    }
                    if (!f && getSpecial() == 2) {
                        target = pt;
                        break;
                    }
                    continue;
                }
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/entities/projectile/EntityHomingShard.java:140

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 136

```java
                List<Entity> es = EntityUtils.getEntitiesInRange(world, posX, posY, posZ, this, (Class<? extends Entity>) tclass, 16.0);
                for (Entity e : es) {
                    if (e instanceof EntityLivingBase && !e.isDead && (getThrower() == null || e.getEntityId() != getThrower().getEntityId())) {
                        target = (EntityLivingBase)e;
                        break;
                    }
                }
            }
            if (target == null || target.isDead) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/ai/GolemNodeProcessor.java:196

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 192

```java
                if (pathnodetype != PathNodeType.OPEN && f >= 0.0f) {
                    pathpoint = openPoint(x, y, z);
                    pathpoint.nodeType = pathnodetype;
                    pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                    break;
                }
                if (f < 0.0f) {
                    return null;
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/ai/GolemNodeProcessor.java:206

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 202

```java
        }
        return pathpoint;
    }

    public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z, EntityLiving entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathnodetype = PathNodeType.BLOCKED;
        double d0 = entitylivingIn.width / 2.0;
        BlockPos blockpos = new BlockPos(entitylivingIn);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/ai/GolemNodeProcessor.java:218

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 214

```java
                    int l = i + x;
                    int i2 = j + y;
                    int j2 = k + z;
                    PathNodeType pathnodetype2 = getPathNodeType(blockaccessIn, l, i2, j2);
                    if (pathnodetype2 == PathNodeType.DOOR_WOOD_CLOSED && canBreakDoorsIn && canEnterDoorsIn) {
                        pathnodetype2 = PathNodeType.WALKABLE;
                    }
                    if (pathnodetype2 == PathNodeType.DOOR_OPEN && !canEnterDoorsIn) {
                        pathnodetype2 = PathNodeType.BLOCKED;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/ai/PathNavigateGolemAir.java:41

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 37

```java
        for (int i = Math.min(currentPath.getCurrentPathIndex() + b0, currentPath.getCurrentPathLength() - 1); i > currentPath.getCurrentPathIndex(); --i) {
            Vec3d vec4 = currentPath.getVectorFromIndex(entity, i);
            if (vec4.squareDistanceTo(vec3) <= 36.0 && isDirectPathBetweenPoints(vec3, vec4, 0, 0, 0)) {
                currentPath.setCurrentPathIndex(i);
                break;
            }
        }
        checkForStuck(vec3);
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseContainer.java:70

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 66

```java
        }
        switch (category) {
            case 1: {
                setupFilterInventory();
                break;
            }
        }
        bindPlayerInventory(pinv);
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:104

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 100

```java
                buttonList.add(new GuiPlusMinusButton(81, guiLeft + middleX - 5 + 14, guiTop + middleY - 17, 10, 10, false));
                buttonList.add(new GuiPlusMinusButton(82, guiLeft + middleX + 18 - 12, guiTop + middleY + 4, 10, 10, true));
                buttonList.add(new GuiPlusMinusButton(83, guiLeft + middleX + 18 + 11, guiTop + middleY + 4, 10, 10, false));
                buttonList.add(new GuiGolemLockButton(25, guiLeft + middleX - 32, guiTop + middleY, 16, 16, seal));
                break;
            }
            case 1: {
                if (seal.getSeal() instanceof ISealConfigFilter) {
                    int s = ((ISealConfigFilter) seal.getSeal()).getFilterSize();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:111

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 107

```java
                if (seal.getSeal() instanceof ISealConfigFilter) {
                    int s = ((ISealConfigFilter) seal.getSeal()).getFilterSize();
                    int sy = 16 + (s - 1) / 3 * 12;
                    buttonList.add(new GuiGolemBWListButton(20, guiLeft + middleX - 8, guiTop + middleY + (s - 1) / 3 * 24 - sy + 27, 16, 16, (ISealConfigFilter) seal.getSeal()));
                    break;
                }
                break;
            }
            case 2: {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:113

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 109

```java
                    int sy = 16 + (s - 1) / 3 * 12;
                    buttonList.add(new GuiGolemBWListButton(20, guiLeft + middleX - 8, guiTop + middleY + (s - 1) / 3 * 24 - sy + 27, 16, 16, (ISealConfigFilter) seal.getSeal()));
                    break;
                }
                break;
            }
            case 2: {
                buttonList.add(new GuiPlusMinusButton(90, guiLeft + middleX - 5 - 14, guiTop + middleY - 25, 10, 10, true));
                buttonList.add(new GuiPlusMinusButton(91, guiLeft + middleX - 5 + 14, guiTop + middleY - 25, 10, 10, false));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:122

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 118

```java
                buttonList.add(new GuiPlusMinusButton(92, guiLeft + middleX - 5 - 14, guiTop + middleY, 10, 10, true));
                buttonList.add(new GuiPlusMinusButton(93, guiLeft + middleX - 5 + 14, guiTop + middleY, 10, 10, false));
                buttonList.add(new GuiPlusMinusButton(94, guiLeft + middleX - 5 - 14, guiTop + middleY + 25, 10, 10, true));
                buttonList.add(new GuiPlusMinusButton(95, guiLeft + middleX - 5 + 14, guiTop + middleY + 25, 10, 10, false));
                break;
            }
            case 3: {
                if (seal.getSeal() instanceof ISealConfigToggles) {
                    ISealConfigToggles cp = (ISealConfigToggles) seal.getSeal();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:142

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 138

```java
                    for (ISealConfigToggles.SealToggle prop2 : cp.getToggles()) {
                        buttonList.add(new GuiGolemPropButton(30 + p, guiLeft + middleX - w, guiTop + middleY - 5 - h + p * (s2 * 2), 8, 8, prop2.getName(), prop2));
                        ++p;
                    }
                    break;
                }
                break;
            }
            case 4: {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:144

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 140

```java
                        ++p;
                    }
                    break;
                }
                break;
            }
            case 4: {
                EnumGolemTrait[] tags = seal.getSeal().getRequiredTags();
                if (tags != null && tags.length > 0) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:162

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 158

```java
                    for (EnumGolemTrait tag : tags) {
                        buttonList.add(new GuiHoverButton(this, 600 + p2, guiLeft + middleX + p2 * 18 - (tags.length - 1) * 9, guiTop + middleY + 24, 16, 16, tag.getLocalizedName(), tag.getLocalizedDescription(), tag.icon));
                        ++p2;
                    }
                    break;
                }
                break;
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:164

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 160

```java
                        ++p2;
                    }
                    break;
                }
                break;
            }
        }
    }

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:211

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 207

```java
                drawCenteredString(fontRenderer, I18n.translateToLocal("golem.prop.priority"), guiLeft + middleX, guiTop + middleY - 28, 12299007);
                drawCenteredString(fontRenderer, "" + seal.getPriority(), guiLeft + middleX, guiTop + middleY - 16, 16777215);
                if (seal.getOwner().equals(mc.player.getUniqueID().toString())) {
                    drawCenteredString(fontRenderer, I18n.translateToLocal("golem.prop.owner"), guiLeft + middleX, guiTop + middleY + 32, 12299007);
                    break;
                }
                break;
            }
            case 1: {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:213

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 209

```java
                if (seal.getOwner().equals(mc.player.getUniqueID().toString())) {
                    drawCenteredString(fontRenderer, I18n.translateToLocal("golem.prop.owner"), guiLeft + middleX, guiTop + middleY + 32, 12299007);
                    break;
                }
                break;
            }
            case 1: {
                if (seal.getSeal() instanceof ISealConfigFilter) {
                    int s3 = ((ISealConfigFilter) seal.getSeal()).getFilterSize();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:225

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 221

```java
                        int x = a % 3;
                        int y = a / 3;
                        drawTexturedModalRect(guiLeft + middleX + x * 24 - sx, guiTop + middleY + y * 24 - sy, 0, 56, 32, 32);
                    }
                    break;
                }
                break;
            }
            case 2: {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:227

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 223

```java
                        drawTexturedModalRect(guiLeft + middleX + x * 24 - sx, guiTop + middleY + y * 24 - sy, 0, 56, 32, 32);
                    }
                    break;
                }
                break;
            }
            case 2: {
                drawCenteredString(fontRenderer, I18n.translateToLocal("button.caption.y"), guiLeft + middleX, guiTop + middleY - 24 - 9, 14540253);
                drawCenteredString(fontRenderer, I18n.translateToLocal("button.caption.x"), guiLeft + middleX, guiTop + middleY - 9, 14540253);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:236

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 232

```java
                drawCenteredString(fontRenderer, I18n.translateToLocal("button.caption.z"), guiLeft + middleX, guiTop + middleY + 24 - 9, 14540253);
                drawCenteredString(fontRenderer, "" + seal.getArea().getY(), guiLeft + middleX, guiTop + middleY - 24, 16777215);
                drawCenteredString(fontRenderer, "" + seal.getArea().getX(), guiLeft + middleX, guiTop + middleY, 16777215);
                drawCenteredString(fontRenderer, "" + seal.getArea().getZ(), guiLeft + middleX, guiTop + middleY + 24, 16777215);
                break;
            }
            case 4: {
                drawCenteredString(fontRenderer, I18n.translateToLocal("button.caption.required"), guiLeft + middleX, guiTop + middleY - 26, 14540253);
                drawCenteredString(fontRenderer, I18n.translateToLocal("button.caption.forbidden"), guiLeft + middleX, guiTop + middleY + 6, 14540253);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:241

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 237

```java
            }
            case 4: {
                drawCenteredString(fontRenderer, I18n.translateToLocal("button.caption.required"), guiLeft + middleX, guiTop + middleY - 26, 14540253);
                drawCenteredString(fontRenderer, I18n.translateToLocal("button.caption.forbidden"), guiLeft + middleX, guiTop + middleY + 6, 14540253);
                break;
            }
        }
    }

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/gui/SealBaseGUI.java:251

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 247

```java
        RenderHelper.disableStandardItemLighting();
        for (GuiButton guibutton : buttonList) {
            if (guibutton.isMouseOver()) {
                guibutton.drawButtonForegroundLayer(mouseX - guiLeft, mouseY - guiTop);
                break;
            }
        }
        RenderHelper.enableGUIStandardItemLighting();
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/PartModelBreakers.java:9

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 5

```java
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.PartModel;


public class PartModelBreakers extends PartModel
{
    private HashMap<Integer, Float[]> ani;

    public PartModelBreakers(ResourceLocation objModel, ResourceLocation objTexture, EnumAttachPoint attachPoint) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/client/PartModelBreakers.java:13

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 9

```java
public class PartModelBreakers extends PartModel
{
    private HashMap<Integer, Float[]> ani;

    public PartModelBreakers(ResourceLocation objModel, ResourceLocation objTexture, EnumAttachPoint attachPoint) {
        super(objModel, objTexture, attachPoint);
        ani = new HashMap<Integer, Float[]>();
    }

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/EntityThaumcraftGolem.java:679

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 675

```java
                            setItemStackToSlot(EntityEquipmentSlot.values()[a], ItemStack.EMPTY);
                        }
                    }
                    if (out != null && !out.isEmpty()) {
                        break;
                    }
                }
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/GolemProperties.java:23

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 19

```java
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.api.golems.parts.PartModel;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.golems.client.PartModelBreakers;
import thaumcraft.common.golems.client.PartModelClaws;
import thaumcraft.common.golems.client.PartModelDarts;
import thaumcraft.common.golems.client.PartModelHauler;
import thaumcraft.common.golems.client.PartModelWheel;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/GolemProperties.java:214

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 210

```java
        GolemHead.register(new GolemHead("SMART_SCOUT", new String[] { "GOLEMVISION", "MINDBIOTHAUMIC" }, new ResourceLocation("thaumcraft", "textures/misc/golem/head_smartscout.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_head_scout_smart.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD), new Object[] { new ItemStack(ItemsTC.mind, 1, 1), new ItemStack(ItemsTC.modules) }, new EnumGolemTrait[] { EnumGolemTrait.SCOUT, EnumGolemTrait.SMART, EnumGolemTrait.FRAGILE }));
        GolemArm.register(new GolemArm("BASIC", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_basic.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_arms_basic.obj"), null, PartModel.EnumAttachPoint.ARMS), new Object[0], new EnumGolemTrait[0]));
        GolemArm.register(new GolemArm("FINE", new String[] { "MATSTUDBRASS" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_fine.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_arms_fine.obj"), null, PartModel.EnumAttachPoint.ARMS), new Object[] { new ItemStack(ItemsTC.mechanismSimple), "base" }, new EnumGolemTrait[] { EnumGolemTrait.DEFT, EnumGolemTrait.FRAGILE }));
        GolemArm.register(new GolemArm("CLAWS", new String[] { "GOLEMCOMBATADV" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_claws.png"), new PartModelClaws(new ResourceLocation("thaumcraft", "models/obj/golem_arms_claws.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_arms_claws.png"), PartModel.EnumAttachPoint.ARMS), new Object[] { new ItemStack(ItemsTC.modules, 1, 1), new ItemStack(Items.SHEARS, 2), "base" }, new EnumGolemTrait[] { EnumGolemTrait.FIGHTER, EnumGolemTrait.CLUMSY, EnumGolemTrait.BRUTAL }));
        GolemArm.register(new GolemArm("BREAKERS", new String[] { "GOLEMBREAKER" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_breakers.png"), new PartModelBreakers(new ResourceLocation("thaumcraft", "models/obj/golem_arms_breakers.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_arms_breakers.png"), PartModel.EnumAttachPoint.ARMS), new Object[] { new ItemStack(Items.DIAMOND, 2), "base", new ItemStack(Blocks.PISTON, 2) }, new EnumGolemTrait[] { EnumGolemTrait.BREAKER, EnumGolemTrait.CLUMSY, EnumGolemTrait.BRUTAL }));
        GolemArm.register(new GolemArm("DARTS", new String[] { "GOLEMCOMBATADV" }, new ResourceLocation("thaumcraft", "textures/misc/golem/arms_darts.png"), new PartModelDarts(new ResourceLocation("thaumcraft", "models/obj/golem_arms_darter.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_arms_darter.png"), PartModel.EnumAttachPoint.ARMS), new Object[] { new ItemStack(ItemsTC.modules, 1, 1), new ItemStack(Blocks.DISPENSER, 2), new ItemStack(Items.ARROW, 32), "mech" }, new GolemArmDart(), new EnumGolemTrait[] { EnumGolemTrait.FIGHTER, EnumGolemTrait.CLUMSY, EnumGolemTrait.RANGED, EnumGolemTrait.FRAGILE }));
        GolemLeg.register(new GolemLeg("WALKER", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/legs_walker.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_legs_walker.obj"), null, PartModel.EnumAttachPoint.LEGS), new Object[] { "base", "mech" }, new EnumGolemTrait[0]));
        GolemLeg.register(new GolemLeg("ROLLER", new String[] { "MINDCLOCKWORK" }, new ResourceLocation("thaumcraft", "textures/misc/golem/legs_roller.png"), new PartModelWheel(new ResourceLocation("thaumcraft", "models/obj/golem_legs_wheel.obj"), new ResourceLocation("thaumcraft", "textures/entity/golems/golem_legs_wheel.png"), PartModel.EnumAttachPoint.BODY), new Object[] { new ItemStack(Items.BOWL, 2), new ItemStack(Items.LEATHER), "mech" }, new GolemLegWheels(), new EnumGolemTrait[] { EnumGolemTrait.WHEELED }));
        GolemLeg.register(new GolemLeg("CLIMBER", new String[] { "GOLEMCLIMBER" }, new ResourceLocation("thaumcraft", "textures/misc/golem/legs_climber.png"), new PartModel(new ResourceLocation("thaumcraft", "models/obj/golem_legs_climber.obj"), new ResourceLocation("thaumcraft", "textures/blocks/base_metal.png"), PartModel.EnumAttachPoint.LEGS), new Object[] { new ItemStack(Items.FLINT, 4), "base", "mech", "mech" }, new EnumGolemTrait[] { EnumGolemTrait.CLIMBER }));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealBreaker.java:38

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 34

```java
import thaumcraft.common.lib.network.FakeNetHandlerPlayServer;
import thaumcraft.common.lib.utils.BlockUtils;


public class SealBreaker extends SealFiltered implements ISealConfigArea, ISealConfigToggles
{
    int delay;
    HashMap<Integer, Long> cache;
    ResourceLocation icon;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealBreaker.java:45

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 41

```java
    HashMap<Integer, Long> cache;
    ResourceLocation icon;
    protected SealToggle[] props;

    public SealBreaker() {
        delay = new Random(System.nanoTime()).nextInt(42);
        cache = new HashMap<Integer, Long>();
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_breaker");
        props = new SealToggle[] { new SealToggle(true, "pmeta", "golem.prop.meta") };
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealBreaker.java:48

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 44

```java

    public SealBreaker() {
        delay = new Random(System.nanoTime()).nextInt(42);
        cache = new HashMap<Integer, Long>();
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_breaker");
        props = new SealToggle[] { new SealToggle(true, "pmeta", "golem.prop.meta") };
    }

    @Override
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealBreaker.java:54

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 50

```java
    }

    @Override
    public String getKey() {
        return "thaumcraft:breaker";
    }

    @Override
    public void tickSeal(World world, ISealEntity seal) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealBreaker.java:125

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 121

```java
                float bh = bs.getBlockHardness(world, task.getPos()) * 10.0f;
                task.setLifespan((short)Math.max(task.getLifespan(), 10L));
                task.setData(task.getData() - bspd);
                int progress = (int)(9.0f * (1.0f - task.getData() / bh));
                world.playSound(null, task.getPos(), bs.getBlock().getSoundType().getBreakSound(), SoundCategory.BLOCKS, (bs.getBlock().getSoundType().getVolume() + 0.7f) / 8.0f, bs.getBlock().getSoundType().getPitch() * 0.5f);
                BlockUtils.destroyBlockPartially(world, golem.getGolemEntity().getEntityId(), task.getPos(), progress);
                return false;
            }
            BlockUtils.destroyBlockPartially(world, golem.getGolemEntity().getEntityId(), task.getPos(), 10);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealBreaker.java:184

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 180

```java
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.BREAKER };
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealBreakerAdvanced.java:7

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 3

```java
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.seals.ISealConfigToggles;


public class SealBreakerAdvanced extends SealBreaker
{
    ResourceLocation icon;
    protected ISealConfigToggles.SealToggle[] props;

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealBreakerAdvanced.java:12

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 8

```java
{
    ResourceLocation icon;
    protected ISealConfigToggles.SealToggle[] props;

    public SealBreakerAdvanced() {
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_breaker_advanced");
        props = new ISealConfigToggles.SealToggle[] { new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"), new ISealConfigToggles.SealToggle(false, "psilk", "golem.prop.silk") };
    }

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealBreakerAdvanced.java:13

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 9

```java
    ResourceLocation icon;
    protected ISealConfigToggles.SealToggle[] props;

    public SealBreakerAdvanced() {
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_breaker_advanced");
        props = new ISealConfigToggles.SealToggle[] { new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"), new ISealConfigToggles.SealToggle(false, "psilk", "golem.prop.silk") };
    }

    @Override
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealBreakerAdvanced.java:19

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 15

```java
    }

    @Override
    public String getKey() {
        return "thaumcraft:breaker_advanced";
    }

    @Override
    public int getFilterSize() {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealBreakerAdvanced.java:44

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 40

```java
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.BREAKER, EnumGolemTrait.SMART };
    }
}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealButcher.java:77

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 73

```java
                        task.setPriority(seal.getPriority());
                        task.setLifespan((short)10);
                        TaskHandler.addTask(world.provider.getDimension(), task);
                        wait = true;
                        break;
                    }
                    continue;
                }
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealHandler.java:108

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 104

```java
                    int indx = 1;
                    for (String s : rs) {
                        if (s.equals(seal.getSeal().getKey())) {
                            world.spawnEntity(new EntityItem(world, pos.pos.getX() + 0.5 + pos.face.getFrontOffsetX() / 1.7f, pos.pos.getY() + 0.5 + pos.face.getFrontOffsetY() / 1.7f, pos.pos.getZ() + 0.5 + pos.face.getFrontOffsetZ() / 1.7f, new ItemStack(ItemsTC.seals, 1, indx)));
                            break;
                        }
                        ++indx;
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealLumber.java:80

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 76

```java
            FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile(null, "FakeThaumcraftGolem"));
            fp.setPosition(golem.getGolemEntity().posX, golem.getGolemEntity().posY, golem.getGolemEntity().posZ);
            IBlockState bs = world.getBlockState(task.getPos());
            golem.swingArm();
            if (BlockUtils.breakFurthestBlock(world, task.getPos(), bs, fp)) {
                task.setLifespan((short)Math.max(task.getLifespan(), 10L));
                golem.addRankXp(1);
                return false;
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealLumber.java:145

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 141

```java
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.BREAKER, EnumGolemTrait.SMART };
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealPickup.java:68

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 64

```java
                        Task task = new Task(seal.getSealPos(), ent);
                        task.setPriority(seal.getPriority());
                        itemEntities.put(task.getId(), ent.getEntityId());
                        TaskHandler.addTask(world.provider.getDimension(), task);
                        break;
                    }
                    continue;
                }
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealPickup.java:117

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 113

```java
                ((EntityThaumcraftGolem)golem).setTask(ticket);
                ((EntityThaumcraftGolem)golem).getTask().setReserved(true);
                if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                    world.setEntityState((Entity)golem, (byte)5);
                    break;
                }
                break;
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealPickup.java:119

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 115

```java
                if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                    world.setEntityState((Entity)golem, (byte)5);
                    break;
                }
                break;
            }
        }
        return true;
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/golems/seals/SealProvide.java:91

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 87

```java
                        task.setLifespan((short)((pr2.getSeal() != null) ? 10 : 31000));
                        TaskHandler.addTask(world.provider.getDimension(), task);
                        pr2.setLinkedTask(task);
                        task.setLinkedProvision(pr2);
                        break;
                    }
                    continue;
                }
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:60

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 56

```java
                        if (RechargeHelper.rechargeItem(player.world, inv.get(a2), player.getPosition(), (EntityPlayer)player, 1) > 0.0f) {
                            return;
                        }
                    }
                    break;
                }
                if (RechargeHelper.rechargeItem(player.world, inv.get(a), player.getPosition(), (EntityPlayer)player, 1) > 0.0f) {
                    return;
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusEffectBreak.java:24

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 20

```java
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;


public class FocusEffectBreak extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSBREAK";
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusEffectBreak.java:28

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 24

```java
public class FocusEffectBreak extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSBREAK";
    }

    @Override
    public String getKey() {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusEffectBreak.java:33

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 29

```java
    }

    @Override
    public String getKey() {
        return "thaumcraft.BREAK";
    }

    @Override
    public Aspect getAspect() {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusEffectBreak.java:56

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 52

```java
            float strength = getSettingValue("power") * finalPower;
            float dur = getPackage().world.getBlockState(target.getBlockPos()).getBlockHardness(getPackage().world, target.getBlockPos()) * 100.0f;
            dur = (float)Math.sqrt(dur);
            if (getPackage().getCaster() instanceof EntityPlayer) {
                ServerEvents.addBreaker(getPackage().world, target.getBlockPos(), getPackage().world.getBlockState(target.getBlockPos()), (EntityPlayer) getPackage().getCaster(), true, silk, fortune, strength, dur, dur, (int)(dur / strength / 3.0f * num), 0.25f + (silk ? 0.25f : 0.0f) + fortune * 0.1f, null);
            }
            return true;
        }
        return true;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusEffectBreak.java:69

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 65

```java
        int[] silk = { 0, 1 };
        String[] silkDesc = { "focus.common.no", "focus.common.yes" };
        int[] fortune = { 0, 1, 2, 3, 4 };
        String[] fortuneDesc = { "focus.common.no", "I", "II", "III", "IV" };
        return new NodeSetting[] { new NodeSetting("power", "focus.break.power", new NodeSetting.NodeSettingIntRange(1, 5)), new NodeSetting("fortune", "focus.common.fortune", new NodeSetting.NodeSettingIntList(fortune, fortuneDesc)), new NodeSetting("silk", "focus.common.silk", new NodeSetting.NodeSettingIntList(silk, silkDesc)) };
    }

    @SideOnly(Side.CLIENT)
    @Override
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusEffectEarth.java:62

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 58

```java
        }
        if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = target.getBlockPos();
            if (getPackage().getCaster() instanceof EntityPlayer && getPackage().world.getBlockState(pos).getBlockHardness(getPackage().world, pos) <= getDamageForDisplay(finalPower) / 25.0f) {
                ServerEvents.addBreaker(getPackage().world, pos, getPackage().world.getBlockState(pos), (EntityPlayer) getPackage().getCaster(), false, false, 0, 1.0f, 0.0f, 1.0f, num, 0.1f, null);
            }
        }
        return false;
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusEffectRift.java:68

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 64

```java
        BlockPos pos = new BlockPos(target.getBlockPos());
        for (distance = 0; distance < maxdis; ++distance) {
            IBlockState bi = getPackage().world.getBlockState(pos);
            if (BlockUtils.isPortableHoleBlackListed(bi) || bi.getBlock() == Blocks.BEDROCK || bi.getBlock() == BlocksTC.hole || bi.getBlock().isAir(bi, getPackage().world, pos)) {
                break;
            }
            if (bi.getBlockHardness(getPackage().world, pos) == -1.0f) {
                break;
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusEffectRift.java:71

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 67

```java
            if (BlockUtils.isPortableHoleBlackListed(bi) || bi.getBlock() == Blocks.BEDROCK || bi.getBlock() == BlocksTC.hole || bi.getBlock().isAir(bi, getPackage().world, pos)) {
                break;
            }
            if (bi.getBlockHardness(getPackage().world, pos) == -1.0f) {
                break;
            }
            pos = pos.offset(target.sideHit.getOpposite());
        }
        createHole(getPackage().world, target.getBlockPos(), target.sideHit, (byte)Math.round((float)(distance + 1)), dur);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java:123

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 119

```java
                case Y: {
                    if (dim == 0 || dim == 3) {
                        return true;
                    }
                    break;
                }
                case Z: {
                    if (dim == 0 || dim == 2) {
                        return true;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java:129

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 125

```java
                case Z: {
                    if (dim == 0 || dim == 2) {
                        return true;
                    }
                    break;
                }
                case X: {
                    if (dim == 0 || dim == 1) {
                        return true;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java:135

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 131

```java
                case X: {
                    if (dim == 0 || dim == 1) {
                        return true;
                    }
                    break;
                }
            }
        }
        else {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java:145

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 141

```java
                case Y: {
                    if ((axis == EnumAxis.X && (dim == 0 || dim == 1)) || (axis == EnumAxis.Z && (dim == 0 || dim == 2))) {
                        return true;
                    }
                    break;
                }
                case Z: {
                    if ((axis == EnumAxis.Y && (dim == 0 || dim == 1)) || (axis == EnumAxis.X && (dim == 0 || dim == 2))) {
                        return true;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java:151

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 147

```java
                case Z: {
                    if ((axis == EnumAxis.Y && (dim == 0 || dim == 1)) || (axis == EnumAxis.X && (dim == 0 || dim == 2))) {
                        return true;
                    }
                    break;
                }
                case X: {
                    if ((axis == EnumAxis.Y && (dim == 0 || dim == 1)) || (axis == EnumAxis.Z && (dim == 0 || dim == 2))) {
                        return true;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java:157

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 153

```java
                case X: {
                    if ((axis == EnumAxis.Y && (dim == 0 || dim == 1)) || (axis == EnumAxis.Z && (dim == 0 || dim == 2))) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java:230

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 226

```java
                }
                if (Math.abs(pos2.getZ() - pos1.getZ()) > sizeZ) {
                    return;
                }
                break;
            }
            case Z: {
                if (Math.abs(pos2.getX() - pos1.getX()) > sizeX) {
                    return;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java:239

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 235

```java
                }
                if (Math.abs(pos2.getY() - pos1.getY()) > sizeZ) {
                    return;
                }
                break;
            }
            case X: {
                if (Math.abs(pos2.getY() - pos1.getY()) > sizeX) {
                    return;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusMediumPlan.java:248

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 244

```java
                }
                if (Math.abs(pos2.getZ() - pos1.getZ()) > sizeZ) {
                    return;
                }
                break;
            }
        }
        if (world.getBlockState(pos2) == bi && BlockUtils.isBlockExposed(world, pos2) && !world.isAirBlock(pos2)) {
            list.add(pos2);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusMediumProjectile.java:30

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 26

```java
        int c = 4 + (getSettingValue("speed") - 1) / 2;
        switch (getSettingValue("option")) {
            case 1: {
                c += 3;
                break;
            }
            case 2:
            case 3: {
                c += 5;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/foci/FocusMediumProjectile.java:35

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 31

```java
            }
            case 2:
            case 3: {
                c += 5;
                break;
            }
        }
        return c;
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:111

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 107

```java
        float tot = 0.0f;
        switch (area) {
            default: {
                tot = AuraHandler.getVis(player.world, player.getPosition());
                break;
            }
            case 1: {
                tot = AuraHandler.getVis(player.world, player.getPosition());
                for (EnumFacing face : EnumFacing.HORIZONTALS) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:118

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 114

```java
                tot = AuraHandler.getVis(player.world, player.getPosition());
                for (EnumFacing face : EnumFacing.HORIZONTALS) {
                    tot += AuraHandler.getVis(player.world, player.getPosition().offset(face, 16));
                }
                break;
            }
            case 2: {
                tot = 0.0f;
                for (int xx = -1; xx <= 1; ++xx) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:127

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 123

```java
                    for (int zz = -1; zz <= 1; ++zz) {
                        tot += AuraHandler.getVis(player.world, player.getPosition().add(xx * 16, 0, zz * 16));
                    }
                }
                break;
            }
        }
        return tot;
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:147

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 143

```java
        Label_0309: {
            switch (area) {
                default: {
                    amount -= AuraHandler.drainVis(player.world, player.getPosition(), amount, sim);
                    break;
                }
                case 1: {
                    float i = amount / 5.0f;
                    while (amount > 0.0f) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:157

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 153

```java
                            i = amount;
                        }
                        amount -= AuraHandler.drainVis(player.world, player.getPosition(), i, sim);
                        if (amount <= 0.0f) {
                            break;
                        }
                        if (i > amount) {
                            i = amount;
                        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:165

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 161

```java
                        }
                        for (EnumFacing face : EnumFacing.HORIZONTALS) {
                            amount -= AuraHandler.drainVis(player.world, player.getPosition().offset(face, 16), i, sim);
                            if (amount <= 0.0f) {
                                break Label_0309;
                            }
                        }
                    }
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:169

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 165

```java
                                break Label_0309;
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    float i = amount / 9.0f;
                    while (amount > 0.0f) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:181

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 177

```java
                        for (int xx = -1; xx <= 1; ++xx) {
                            for (int zz = -1; zz <= 1; ++zz) {
                                amount -= AuraHandler.drainVis(player.world, player.getPosition().add(xx * 16, 0, zz * 16), i, sim);
                                if (amount <= 0.0f) {
                                    break Label_0309;
                                }
                            }
                        }
                    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:186

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 182

```java
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return amount <= 0.0f;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:265

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 261

```java
        if (!w.isRemote && e.ticksExisted % 10 == 0 && e instanceof EntityPlayerMP) {
            for (ItemStack h : e.getHeldEquipment()) {
                if (h != null && !h.isEmpty() && h.getItem() instanceof ICaster) {
                    updateAura(is, w, (EntityPlayerMP)e);
                    break;
                }
            }
        }
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:279

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 275

```java
        switch (area) {
            default: {
                AuraChunk ac = AuraHandler.getAuraChunk(world.provider.getDimension(), (int)player.posX >> 4, (int)player.posZ >> 4);
                if (ac == null) {
                    break;
                }
                cv = ac.getVis();
                cf = ac.getFlux();
                bv = ac.getBase();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:284

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 280

```java
                }
                cv = ac.getVis();
                cf = ac.getFlux();
                bv = ac.getBase();
                break;
            }
            case 1: {
                AuraChunk ac = AuraHandler.getAuraChunk(world.provider.getDimension(), (int)player.posX >> 4, (int)player.posZ >> 4);
                if (ac == null) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:289

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 285

```java
            }
            case 1: {
                AuraChunk ac = AuraHandler.getAuraChunk(world.provider.getDimension(), (int)player.posX >> 4, (int)player.posZ >> 4);
                if (ac == null) {
                    break;
                }
                cv = ac.getVis();
                cf = ac.getFlux();
                bv = ac.getBase();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:302

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 298

```java
                        cf += ac.getFlux();
                        bv += ac.getBase();
                    }
                }
                break;
            }
            case 2: {
                for (int xx = -1; xx <= 1; ++xx) {
                    for (int zz = -1; zz <= 1; ++zz) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:315

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 311

```java
                            bv += ac.getBase();
                        }
                    }
                }
                break;
            }
        }
        PacketHandler.INSTANCE.sendTo(new PacketAuraToClient(new AuraChunk(null, bv, cv, cf)), player);
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/casters/ItemCaster.java:484

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 480

```java
                        try {
                            out = new ItemStack(stack.getTagCompound().getCompoundTag("picked"));
                        }
                        catch (Exception ex) {}
                        break;
                    }
                }
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/curios/ItemCurio.java:54

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 50

```java
            switch (player.getHeldItem(hand).getItemDamage()) {
                default: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("AUROMANCY"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("AUROMANCY"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
                    break;
                }
                case 1: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ALCHEMY"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ALCHEMY"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/curios/ItemCurio.java:59

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 55

```java
                }
                case 1: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ALCHEMY"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ALCHEMY"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
                    break;
                }
                case 2: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("GOLEMANCY"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("GOLEMANCY"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/curios/ItemCurio.java:64

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 60

```java
                }
                case 2: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("GOLEMANCY"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("GOLEMANCY"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
                    break;
                }
                case 3: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ELDRITCH"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ELDRITCH"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/curios/ItemCurio.java:71

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 67

```java
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ELDRITCH"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ELDRITCH"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, 5, IPlayerWarp.EnumWarpType.TEMPORARY);
                    break;
                }
                case 4: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("INFUSION"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("INFUSION"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/curios/ItemCurio.java:76

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 72

```java
                }
                case 4: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("INFUSION"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("INFUSION"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
                    break;
                }
                case 5: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ARTIFICE"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ARTIFICE"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/curios/ItemCurio.java:81

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 77

```java
                }
                case 5: {
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory("ARTIFICE"), MathHelper.getInt(player.getRNG(), oProg / 2, oProg));
                    ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("ARTIFICE"), MathHelper.getInt(player.getRNG(), tProg / 3, tProg / 2));
                    break;
                }
                case 6: {
                    int aw = ThaumcraftApi.internalMethods.getActualWarp(player);
                    if (aw > 20) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/curios/ItemCurio.java:97

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 93

```java
                        ThaumcraftApi.internalMethods.addWarpToPlayer(player, 5, IPlayerWarp.EnumWarpType.TEMPORARY);
                        if (player.getRNG().nextBoolean()) {
                            ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.PERMANENT);
                        }
                        break;
                    }
                    player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("fail.crimsonrites")));
                    return super.onItemRightClick(worldIn, player, hand);
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/resources/ItemMagicDust.java:51

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 47

```java
                }
                trigger.execute(world, player, pos, place, side);
                if (world.isRemote) {
                    doSparkles(player, world, pos, hitX, hitY, hitZ, hand, trigger, place);
                    break;
                }
                return EnumActionResult.SUCCESS;
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/tools/ItemElementalShovel.java:145

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 141

```java
                    BlockPos p2 = pos.offset(side).add(xx, yy, zz);
                    IBlockState b2 = world.getBlockState(p2);
                    if (bs.getBlock().canPlaceBlockAt(world, p2)) {
                        if (player.capabilities.isCreativeMode || InventoryUtils.consumePlayerItem(player, Item.getItemFromBlock(bs.getBlock()), bs.getBlock().getMetaFromState(bs))) {
                            world.playSound(p2.getX(), p2.getY(), p2.getZ(), bs.getBlock().getSoundType().getBreakSound(), SoundCategory.BLOCKS, 0.6f, 0.9f + world.rand.nextFloat() * 0.2f, false);
                            world.setBlockState(p2, bs);
                            player.getHeldItem(hand).damageItem(1, player);
                            if (world.isRemote) {
                                FXDispatcher.INSTANCE.drawBamf(p2, 8401408, false, false, side);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/tools/ItemElementalShovel.java:154

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 150

```java
                            }
                            player.swingArm(hand);
                        }
                        else if (bs.getBlock() == Blocks.GRASS && (player.capabilities.isCreativeMode || InventoryUtils.consumePlayerItem(player, Item.getItemFromBlock(Blocks.DIRT), 0))) {
                            world.playSound(p2.getX(), p2.getY(), p2.getZ(), bs.getBlock().getSoundType().getBreakSound(), SoundCategory.BLOCKS, 0.6f, 0.9f + world.rand.nextFloat() * 0.2f, false);
                            world.setBlockState(p2, Blocks.DIRT.getDefaultState());
                            player.getHeldItem(hand).damageItem(1, player);
                            if (world.isRemote) {
                                FXDispatcher.INSTANCE.drawBamf(p2, 8401408, false, false, side);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/tools/ItemElementalShovel.java:162

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 158

```java
                                FXDispatcher.INSTANCE.drawBamf(p2, 8401408, false, false, side);
                            }
                            player.swingArm(hand);
                            if (player.getHeldItem(hand).isEmpty()) {
                                break;
                            }
                            if (player.getHeldItem(hand).getCount() < 1) {
                                break;
                            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/items/tools/ItemElementalShovel.java:165

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 161

```java
                            if (player.getHeldItem(hand).isEmpty()) {
                                break;
                            }
                            if (player.getHeldItem(hand).getCount() < 1) {
                                break;
                            }
                        }
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/CommandThaumcraft.java:222

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 218

```java
                    if (ri.getStages() != null) {
                        for (ResearchStage stage : ri.getStages()) {
                            if (stage.getResearch() != null && Arrays.asList(stage.getResearch()).contains(research)) {
                                ThaumcraftCapabilities.getKnowledge(player).setResearchFlag(ri.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
                                break;
                            }
                        }
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/DustTriggerMultiblock.java:80

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 76

```java
                                if (!(matrix.matrix[x][z].getSource() instanceof Block) || bsWo.getBlock() == matrix.matrix[x][z].getSource()) {
                                    if (!(matrix.matrix[x][z].getSource() instanceof Material) || bsWo.getMaterial() == matrix.matrix[x][z].getSource()) {
                                        if (matrix.matrix[x][z].getSource() instanceof ItemStack) {
                                            if (bsWo.getBlock() != Block.getBlockFromItem(((ItemStack)matrix.matrix[x][z].getSource()).getItem())) {
                                                break Label_0382;
                                            }
                                            if (bsWo.getBlock().getMetaFromState(bsWo) != ((ItemStack)matrix.matrix[x][z].getSource()).getItemDamage()) {
                                                break Label_0382;
                                            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/DustTriggerMultiblock.java:83

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 79

```java
                                            if (bsWo.getBlock() != Block.getBlockFromItem(((ItemStack)matrix.matrix[x][z].getSource()).getItem())) {
                                                break Label_0382;
                                            }
                                            if (bsWo.getBlock().getMetaFromState(bsWo) != ((ItemStack)matrix.matrix[x][z].getSource()).getItemDamage()) {
                                                break Label_0382;
                                            }
                                        }
                                        if (!(matrix.matrix[x][z].getSource() instanceof IBlockState) || bsWo == matrix.matrix[x][z].getSource()) {
                                            continue;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/DustTriggerOre.java:39

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 35

```java
            int[] ods = oreIDs = OreDictionary.getOreIDs(new ItemStack(bs.getBlock(), 1, bs.getBlock().damageDropped(bs)));
            for (int q : oreIDs) {
                if (q == OreDictionary.getOreID(target)) {
                    b = true;
                    break;
                }
            }
        }
        catch (Exception ex) {}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java:61

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 57

```java
                Set<String> tcs = central.getItem().getToolClasses(central);
                for (String tc : tcs) {
                    if (enchantment.toolClasses.contains(tc)) {
                        cool = true;
                        break;
                    }
                }
            }
            if (!cool && central.getItem() instanceof ItemArmor) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java:70

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 66

```java
                String at = "none";
                switch (((ItemArmor)central.getItem()).armorType) {
                    case HEAD: {
                        at = "helm";
                        break;
                    }
                    case CHEST: {
                        at = "chest";
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java:74

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 70

```java
                        break;
                    }
                    case CHEST: {
                        at = "chest";
                        break;
                    }
                    case LEGS: {
                        at = "legs";
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java:78

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 74

```java
                        break;
                    }
                    case LEGS: {
                        at = "legs";
                        break;
                    }
                    case FEET: {
                        at = "boots";
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java:82

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 78

```java
                        break;
                    }
                    case FEET: {
                        at = "boots";
                        break;
                    }
                }
                if (enchantment.toolClasses.contains("armor") || enchantment.toolClasses.contains(at)) {
                    cool = true;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java:94

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 90

```java
                String at = "none";
                switch (((IBauble)central.getItem()).getBaubleType(central)) {
                    case AMULET: {
                        at = "amulet";
                        break;
                    }
                    case BELT: {
                        at = "belt";
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java:98

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 94

```java
                        break;
                    }
                    case BELT: {
                        at = "belt";
                        break;
                    }
                    case RING: {
                        at = "ring";
                        break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/InfusionEnchantmentRecipe.java:102

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 98

```java
                        break;
                    }
                    case RING: {
                        at = "ring";
                        break;
                    }
                }
                if (enchantment.toolClasses.contains("bauble") || enchantment.toolClasses.contains(at)) {
                    cool = true;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/ShapedArcaneVoidJar.java:24

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 20

```java
        NBTTagCompound nbt = null;
        for (int a = 0; a < var1.getSizeInventory(); ++a) {
            if (Block.getBlockFromItem(var1.getStackInSlot(a).getItem()) == BlocksTC.jarNormal) {
                nbt = var1.getStackInSlot(a).getTagCompound();
                break;
            }
        }
        ItemStack res = super.getCraftingResult(var1);
        res.setTagCompound(nbt);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:253

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 249

```java
                for (int od : ores) {
                    String s = OreDictionary.getOreName(od);
                    if (s != null && Arrays.binarySearch(dyes, s) >= 0) {
                        tmp.merge(Aspect.SENSES, 5);
                        break;
                    }
                }
            }
            NBTTagList ench = itemstack.getEnchantmentTagList();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:332

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 328

```java
                        }
                        else if (e == Enchantments.SMITE) {
                            tmp.merge(Aspect.UNDEAD, lvl / 2).merge(Aspect.AVERSION, lvl / 2);
                        }
                        else if (e == Enchantments.UNBREAKING) {
                            tmp.merge(Aspect.EARTH, lvl);
                        }
                        else if (e == Enchantments.DEPTH_STRIDER) {
                            tmp.merge(Aspect.WATER, lvl);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:387

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 383

```java
                        hashSet.add(((Ingredient)reagent.get(mixpre)).getMatchingStacks()[0]);
                        if (((IRegistryDelegate)input.get(mixpre)).get() != PotionTypes.WATER && ((IRegistryDelegate)input.get(mixpre)).get() instanceof PotionType) {
                            getPotionReagentsRecursive((PotionType)((IRegistryDelegate)input.get(mixpre)).get(), hashSet);
                        }
                        break;
                    }
                    catch (Exception ex) {}
                }
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java:45

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 41

```java
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        for (WorldCoordinates source : es) {
            TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (as.isBlocked()) {
                continue;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java:74

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 70

```java
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        for (WorldCoordinates source : es) {
            TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (as.isBlocked()) {
                continue;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java:119

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 115

```java
        ArrayList<WorldCoordinates> empties = new ArrayList<WorldCoordinates>();
        for (WorldCoordinates source : es) {
            TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (as.isBlocked()) {
                continue;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java:146

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 142

```java
                    continue;
                }
                TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
                if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                    break;
                }
                IAspectSource as = (IAspectSource)sourceTile;
                if (aspect != null && as.doesContainerAccept(aspect) && as.addToContainer(aspect, 1) <= 0) {
                    PacketHandler.INSTANCE.sendToAllAround(new PacketFXEssentiaSource(source.pos, (byte)(source.pos.getX() - tile.getPos().getX()), (byte)(source.pos.getY() - tile.getPos().getY()), (byte)(source.pos.getZ() - tile.getPos().getZ()), aspect.getColor(), ext), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 32.0));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java:171

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 167

```java
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        for (WorldCoordinates source : es) {
            TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            IAspectSource as = (IAspectSource)sourceTile;
            if (as.isBlocked()) {
                continue;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java:197

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 193

```java
        ArrayList<WorldCoordinates> es = EssentiaHandler.sources.get(tileLoc);
        for (WorldCoordinates source : es) {
            TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
                break;
            }
            if (ignoreMirror && sourceTile instanceof TileMirrorEssentia) {
                continue;
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/PlayerEvents.java:179

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 175

```java

    private static void handleMisc(EntityPlayer player) {
        if (player.world.provider.getDimension() == ModConfig.CONFIG_WORLD.dimensionOuterId && player.ticksExisted % 20 == 0 && !player.isSpectator() && !player.capabilities.isCreativeMode && player.capabilities.isFlying) {
            player.capabilities.isFlying = false;
            player.sendStatusMessage(new TextComponentString(TextFormatting.ITALIC + "" + TextFormatting.GRAY + I18n.translateToLocal("tc.break.fly")), true);
        }
    }

    @SideOnly(Side.CLIENT)
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:63

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 59

```java
    long lastcheck;
    static HashMap<Integer, Integer> serverTicks;
    public static ConcurrentHashMap<Integer, AuraThread> auraThreads;
    DecimalFormat myFormatter;
    public static HashMap<Integer, LinkedBlockingQueue<BreakData>> breakList;
    public static HashMap<Integer, LinkedBlockingQueue<VirtualSwapper>> swapList;
    public static HashMap<Integer, ArrayList<ChunkPos>> chunksToGenerate;
    public static Predicate<SwapperPredicate> DEFAULT_PREDICATE;
    private static HashMap<Integer, LinkedBlockingQueue<RunnableEntry>> serverRunList;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:152

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 148

```java
            }
            int ticks = ServerEvents.serverTicks.get(dim);
            tickChunkRegeneration(event);
            tickBlockSwap(event.world);
            tickBlockBreak(event.world);
            ArrayList<Integer[]> nbe = TileArcaneEar.noteBlockEvents.get(dim);
            if (nbe != null) {
                nbe.clear();
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:186

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 182

```java
        if (chunks != null && chunks.size() > 0) {
            for (int a = 0; a < 10; ++a) {
                chunks = ServerEvents.chunksToGenerate.get(dim);
                if (chunks == null || chunks.size() <= 0) {
                    break;
                }
                ++count;
                ChunkPos loc = chunks.get(0);
                long worldSeed = event.world.getSeed();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:317

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 313

```java
            ServerEvents.swapList.put(dim, queue2);
        }
    }

    private static void tickBlockBreak(World world) {
        int dim = world.provider.getDimension();
        LinkedBlockingQueue<BreakData> queue = ServerEvents.breakList.get(dim);
        LinkedBlockingQueue<BreakData> queue2 = new LinkedBlockingQueue<BreakData>();
        if (queue != null) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:319

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 315

```java
    }

    private static void tickBlockBreak(World world) {
        int dim = world.provider.getDimension();
        LinkedBlockingQueue<BreakData> queue = ServerEvents.breakList.get(dim);
        LinkedBlockingQueue<BreakData> queue2 = new LinkedBlockingQueue<BreakData>();
        if (queue != null) {
            while (!queue.isEmpty()) {
                BreakData vs = queue.poll();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:320

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 316

```java

    private static void tickBlockBreak(World world) {
        int dim = world.provider.getDimension();
        LinkedBlockingQueue<BreakData> queue = ServerEvents.breakList.get(dim);
        LinkedBlockingQueue<BreakData> queue2 = new LinkedBlockingQueue<BreakData>();
        if (queue != null) {
            while (!queue.isEmpty()) {
                BreakData vs = queue.poll();
                if (vs != null) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:323

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 319

```java
        LinkedBlockingQueue<BreakData> queue = ServerEvents.breakList.get(dim);
        LinkedBlockingQueue<BreakData> queue2 = new LinkedBlockingQueue<BreakData>();
        if (queue != null) {
            while (!queue.isEmpty()) {
                BreakData vs = queue.poll();
                if (vs != null) {
                    IBlockState bs = world.getBlockState(vs.pos);
                    if (bs == vs.source) {
                        if (vs.visCost > 0.0f && AuraHelper.getVis(world, vs.pos) < vs.visCost) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:334

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 330

```java
                        if (!world.canMineBlockBody(vs.player, vs.pos) || bs.getBlockHardness(world, vs.pos) < 0.0f) {
                            continue;
                        }
                        if (vs.fx) {
                            world.sendBlockBreakProgress(vs.pos.hashCode(), vs.pos, (int)((1.0f - vs.durabilityCurrent / vs.durabilityMax) * 10.0f));
                        }
                        BreakData breakData = vs;
                        breakData.durabilityCurrent -= vs.strength;
                        if (vs.durabilityCurrent <= 0.0f) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:336

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 332

```java
                        }
                        if (vs.fx) {
                            world.sendBlockBreakProgress(vs.pos.hashCode(), vs.pos, (int)((1.0f - vs.durabilityCurrent / vs.durabilityMax) * 10.0f));
                        }
                        BreakData breakData = vs;
                        breakData.durabilityCurrent -= vs.strength;
                        if (vs.durabilityCurrent <= 0.0f) {
                            BlockUtils.harvestBlock(world, vs.player, vs.pos, true, vs.silk, vs.fortune, false);
                            if (vs.fx) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:337

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 333

```java
                        if (vs.fx) {
                            world.sendBlockBreakProgress(vs.pos.hashCode(), vs.pos, (int)((1.0f - vs.durabilityCurrent / vs.durabilityMax) * 10.0f));
                        }
                        BreakData breakData = vs;
                        breakData.durabilityCurrent -= vs.strength;
                        if (vs.durabilityCurrent <= 0.0f) {
                            BlockUtils.harvestBlock(world, vs.player, vs.pos, true, vs.silk, vs.fortune, false);
                            if (vs.fx) {
                                world.sendBlockBreakProgress(vs.pos.hashCode(), vs.pos, -1);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:341

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 337

```java
                        breakData.durabilityCurrent -= vs.strength;
                        if (vs.durabilityCurrent <= 0.0f) {
                            BlockUtils.harvestBlock(world, vs.player, vs.pos, true, vs.silk, vs.fortune, false);
                            if (vs.fx) {
                                world.sendBlockBreakProgress(vs.pos.hashCode(), vs.pos, -1);
                            }
                            if (vs.visCost <= 0.0f) {
                                continue;
                            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:349

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 345

```java
                            }
                            ThaumcraftApi.internalMethods.drainVis(world, vs.pos, vs.visCost, false);
                        }
                        else {
                            queue2.offer(new BreakData(vs.strength, vs.durabilityCurrent, vs.durabilityMax, vs.pos, vs.source, vs.player, vs.fx, vs.silk, vs.fortune, vs.visCost));
                        }
                    }
                    else {
                        if (!vs.fx) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:356

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 352

```java
                    else {
                        if (!vs.fx) {
                            continue;
                        }
                        world.sendBlockBreakProgress(vs.pos.hashCode(), vs.pos, -1);
                    }
                }
            }
            ServerEvents.breakList.put(dim, queue2);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:360

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 356

```java
                        world.sendBlockBreakProgress(vs.pos.hashCode(), vs.pos, -1);
                    }
                }
            }
            ServerEvents.breakList.put(dim, queue2);
        }
    }

    public static void addSwapper(World world, BlockPos pos, Object source, ItemStack target, boolean consumeTarget, int life, EntityPlayer player, boolean fx, boolean fancy, int color, boolean pickup, boolean silk, int fortune, Predicate<SwapperPredicate> allowSwap, float visCost) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:375

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 371

```java
        queue.offer(new VirtualSwapper(pos, source, target, consumeTarget, life, player, fx, fancy, color, pickup, silk, fortune, allowSwap, visCost));
        ServerEvents.swapList.put(dim, queue);
    }

    public static void addBreaker(World world, BlockPos pos, IBlockState source, EntityPlayer player, boolean fx, boolean silk, int fortune, float str, float durabilityCurrent, float durabilityMax, int delay, float vis, Runnable run) {
        int dim = world.provider.getDimension();
        if (delay > 0) {
            addRunnableServer(world, new Runnable() {
                @Override
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:381

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 377

```java
        if (delay > 0) {
            addRunnableServer(world, new Runnable() {
                @Override
                public void run() {
                    ServerEvents.addBreaker(world, pos, source, player, fx, silk, fortune, str, durabilityCurrent, durabilityMax, 0, vis, run);
                }
            }, delay);
        }
        else {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:386

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 382

```java
                }
            }, delay);
        }
        else {
            LinkedBlockingQueue<BreakData> queue = ServerEvents.breakList.get(dim);
            if (queue == null) {
                ServerEvents.breakList.put(dim, new LinkedBlockingQueue<BreakData>());
                queue = ServerEvents.breakList.get(dim);
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:388

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 384

```java
        }
        else {
            LinkedBlockingQueue<BreakData> queue = ServerEvents.breakList.get(dim);
            if (queue == null) {
                ServerEvents.breakList.put(dim, new LinkedBlockingQueue<BreakData>());
                queue = ServerEvents.breakList.get(dim);
            }
            queue.offer(new BreakData(str, durabilityCurrent, durabilityMax, pos, source, player, fx, silk, fortune, vis));
            ServerEvents.breakList.put(dim, queue);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:389

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 385

```java
        else {
            LinkedBlockingQueue<BreakData> queue = ServerEvents.breakList.get(dim);
            if (queue == null) {
                ServerEvents.breakList.put(dim, new LinkedBlockingQueue<BreakData>());
                queue = ServerEvents.breakList.get(dim);
            }
            queue.offer(new BreakData(str, durabilityCurrent, durabilityMax, pos, source, player, fx, silk, fortune, vis));
            ServerEvents.breakList.put(dim, queue);
            if (run != null) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:391

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 387

```java
            if (queue == null) {
                ServerEvents.breakList.put(dim, new LinkedBlockingQueue<BreakData>());
                queue = ServerEvents.breakList.get(dim);
            }
            queue.offer(new BreakData(str, durabilityCurrent, durabilityMax, pos, source, player, fx, silk, fortune, vis));
            ServerEvents.breakList.put(dim, queue);
            if (run != null) {
                run.run();
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:392

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 388

```java
                ServerEvents.breakList.put(dim, new LinkedBlockingQueue<BreakData>());
                queue = ServerEvents.breakList.get(dim);
            }
            queue.offer(new BreakData(str, durabilityCurrent, durabilityMax, pos, source, player, fx, silk, fortune, vis));
            ServerEvents.breakList.put(dim, queue);
            if (run != null) {
                run.run();
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:420

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 416

```java

    static {
        ServerEvents.serverTicks = new HashMap<Integer, Integer>();
        ServerEvents.auraThreads = new ConcurrentHashMap<Integer, AuraThread>();
        ServerEvents.breakList = new HashMap<Integer, LinkedBlockingQueue<BreakData>>();
        ServerEvents.swapList = new HashMap<Integer, LinkedBlockingQueue<VirtualSwapper>>();
        ServerEvents.chunksToGenerate = new HashMap<Integer, ArrayList<ChunkPos>>();
        DEFAULT_PREDICATE = new Predicate<SwapperPredicate>() {
            public boolean apply(@Nullable SwapperPredicate pred) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:432

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 428

```java
        ServerEvents.serverRunList = new HashMap<Integer, LinkedBlockingQueue<RunnableEntry>>();
        ServerEvents.clientRunList = new LinkedBlockingQueue<RunnableEntry>();
    }

    public static class BreakData
    {
        float strength;
        float durabilityCurrent;
        float durabilityMax;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:445

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 441

```java
        boolean silk;
        int fortune;
        float visCost;

        public BreakData(float strength, float durabilityCurrent, float durabilityMax, BlockPos pos, IBlockState source, EntityPlayer player, boolean fx, boolean silk, int fortune, float vis) {
            this.strength = 0.0f;
            this.durabilityCurrent = 1.0f;
            this.durabilityMax = 1.0f;
            this.player = null;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:100

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 96

```java
                                        }
                                    }
                                }
                                if (count >= rank) {
                                    break;
                                }
                            }
                        }
                    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:156

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 152

```java
        list.remove(pos);
    }

    @SubscribeEvent
    public static void breakBlockEvent(BlockEvent.BreakEvent event) {
        if (ToolEvents.blockedBlocks.containsKey(event.getWorld().provider.getDimension())) {
            ArrayList<BlockPos> list = ToolEvents.blockedBlocks.get(event.getWorld().provider.getDimension());
            if (list == null) {
                list = new ArrayList<BlockPos>();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:176

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 172

```java
                    event.setCanceled(true);
                    if (!event.getPlayer().getName().equals("FakeThaumcraftBore")) {
                        heldItem.damageItem(1, event.getPlayer());
                    }
                    BlockUtils.breakFurthestBlock(event.getWorld(), event.getPos(), event.getState(), event.getPlayer());
                }
            }
        }
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/WarpEvents.java:262

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 258

```java
            if (player.world.getBlockState(new BlockPos(i2, j2 - 1, k2)).isOpaqueCube() && player.world.checkNoEntityCollision(eg.getEntityBoundingBox()) && player.world.getCollisionBoxes(eg, eg.getEntityBoundingBox()).isEmpty() && !player.world.containsAnyLiquid(eg.getEntityBoundingBox())) {
                eg.onInitialSpawn(player.world.getDifficultyForLocation(new BlockPos(eg)), null);
                player.world.spawnEntity(eg);
                player.sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("warp.text.16")), true);
                break;
            }
        }
    }

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/WarpEvents.java:281

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 277

```java
                eg.setPosition(i2, j2, k2);
                if (player.world.checkNoEntityCollision(eg.getEntityBoundingBox()) && player.world.getCollisionBoxes(eg, eg.getEntityBoundingBox()).isEmpty() && !player.world.containsAnyLiquid(eg.getEntityBoundingBox())) {
                    eg.setAttackTarget(player);
                    player.world.spawnEntity(eg);
                    break;
                }
            }
        }
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/events/WarpEvents.java:302

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 298

```java
                if (player.world.getBlockState(new BlockPos(i2, j2 - 1, k2)).isFullCube()) {
                    spider.setPosition(i2, j2, k2);
                    if (player.world.checkNoEntityCollision(spider.getEntityBoundingBox()) && player.world.getCollisionBoxes(spider, spider.getEntityBoundingBox()).isEmpty() && !player.world.containsAnyLiquid(spider.getEntityBoundingBox())) {
                        success = true;
                        break;
                    }
                }
            }
            if (success) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/network/fx/PacketFXScanSource.java:136

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 132

```java
                    if (ts.equals(bs) && positions.contains(t)) {
                        positions.remove(t);
                        coll.add(t);
                        if (positions.isEmpty()) {
                            break Label_0132;
                        }
                        calcGroup(world, t, coll, positions);
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/network/misc/PacketMiscEvent.java:71

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 67

```java
        switch (message.type) {
            case 0: {
                if (!ModConfig.CONFIG_GRAPHICS.nostress) {
                    p.world.playSound(p.posX, p.posY, p.posZ, SoundsTC.heartbeat, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
                    break;
                }
                break;
            }
            case 1: {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/network/misc/PacketMiscEvent.java:73

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 69

```java
                if (!ModConfig.CONFIG_GRAPHICS.nostress) {
                    p.world.playSound(p.posX, p.posY, p.posZ, SoundsTC.heartbeat, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
                    break;
                }
                break;
            }
            case 1: {
                RenderEventHandler.fogFiddled = true;
                RenderEventHandler.fogDuration = 2400;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/network/misc/PacketMiscEvent.java:78

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 74

```java
            }
            case 1: {
                RenderEventHandler.fogFiddled = true;
                RenderEventHandler.fogDuration = 2400;
                break;
            }
            case 2: {
                RenderEventHandler.fogFiddled = true;
                if (RenderEventHandler.fogDuration < 200) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/network/misc/PacketMiscEvent.java:84

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 80

```java
            case 2: {
                RenderEventHandler.fogFiddled = true;
                if (RenderEventHandler.fogDuration < 200) {
                    RenderEventHandler.fogDuration = 200;
                    break;
                }
                break;
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/network/misc/PacketMiscEvent.java:86

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 82

```java
                if (RenderEventHandler.fogDuration < 200) {
                    RenderEventHandler.fogDuration = 200;
                    break;
                }
                break;
            }
        }
    }
}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java:61

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 57

```java
                                thaumatorium.recipePlayer.remove(i);
                                thaumatorium.recipeHash.remove(i);
                                thaumatorium.currentCraft = -1;
                                flag = true;
                                break;
                            }
                            ++i;
                        }
                        if (!flag && thaumatorium.recipeHash.size() < thaumatorium.maxRecipes) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java:72

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 68

```java
                                    thaumatorium.recipeEssentia.add(cr.getAspects().copy());
                                    thaumatorium.recipePlayer.add(player.getName());
                                    thaumatorium.recipeHash.add(cr.hash);
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if (flag) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/network/playerdata/PacketPlayerFlagToServer.java:40

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 36

```java
                    EntityPlayer player = ctx.getServerHandler().player;
                    switch (message.flag) {
                        case 1: {
                            player.fallDistance = 0.0f;
                            break;
                        }
                    }
                }
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/research/ResearchManager.java:189

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 185

```java
                                if (addendum.getResearch() != null && Arrays.asList(addendum.getResearch()).contains(researchkey)) {
                                    ITextComponent text = new TextComponentTranslation("tc.addaddendum", ri.getLocalizedName());
                                    player.sendMessage(text);
                                    knowledge.setResearchFlag(ri.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
                                    break;
                                }
                            }
                        }
                    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/research/ResearchManager.java:539

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 535

```java
        for (int a = 1; a < split.length; ++a) {
            if (split[a].startsWith("{")) {
                nbt = split[a];
                nbt.replaceAll("'", "\"");
                break;
            }
            int q = -1;
            try {
                q = Integer.parseInt(split[a]);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java:90

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 86

```java
        String type = s2 = ((ItemCurio) getRequiredItems()[0].getItem()).getVariantNames()[getRequiredItems()[0].getItemDamage()];
        switch (s2) {
            case "arcane": {
                data.addTotal("AUROMANCY", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
            case "preserved": {
                data.addTotal("ALCHEMY", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java:94

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 90

```java
                break;
            }
            case "preserved": {
                data.addTotal("ALCHEMY", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
            case "ancient": {
                data.addTotal("GOLEMANCY", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java:98

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 94

```java
                break;
            }
            case "ancient": {
                data.addTotal("GOLEMANCY", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
            case "eldritch": {
                data.addTotal("ELDRITCH", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java:102

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 98

```java
                break;
            }
            case "eldritch": {
                data.addTotal("ELDRITCH", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
            case "knowledge": {
                data.addTotal("INFUSION", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java:106

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 102

```java
                break;
            }
            case "knowledge": {
                data.addTotal("INFUSION", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
            case "twisted": {
                data.addTotal("ARTIFICE", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java:110

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 106

```java
                break;
            }
            case "twisted": {
                data.addTotal("ARTIFICE", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
            case "rites": {
                data.addTotal("ELDRITCH", MathHelper.getInt(player.getRNG(), 15, 20));
                data.addTotal("AUROMANCY", MathHelper.getInt(player.getRNG(), 10, 15));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java:115

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 111

```java
            }
            case "rites": {
                data.addTotal("ELDRITCH", MathHelper.getInt(player.getRNG(), 15, 20));
                data.addTotal("AUROMANCY", MathHelper.getInt(player.getRNG(), 10, 15));
                break;
            }
            default: {
                data.addTotal("BASICS", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/research/theorycraft/CardCurio.java:119

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 115

```java
                break;
            }
            default: {
                data.addTotal("BASICS", MathHelper.getInt(player.getRNG(), 25, 35));
                break;
            }
        }
        if (player.getRNG().nextBoolean()) {
            ++data.bonusDraws;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/research/theorycraft/CardSynergy.java:55

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 51

```java
                for (String category : cats) {
                    if (data.getTotal(category) > 0) {
                        data.addTotal(category, -1);
                        if (--tot <= 0) {
                            break;
                        }
                    }
                }
            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/SoundsTC.java:71

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 67

```java
    public static SoundEvent crabdeath;
    public static SoundEvent crabtalk;
    public static SoundEvent chant;
    public static SoundEvent coins;
    public static SoundEvent urnbreak;
    public static SoundEvent evilportal;
    public static SoundEvent grind;
    public static SoundEvent dust;
    public static SoundEvent pageturn;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/SoundsTC.java:85

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 81

```java
    public static void registerSoundTypes() {
        SoundsTC.GORE = new SoundType(0.5f, 1.0f, SoundsTC.gore, SoundsTC.gore, SoundsTC.gore, SoundsTC.gore, SoundsTC.gore);
        SoundsTC.CRYSTAL = new SoundType(0.5f, 1.0f, SoundsTC.crystal, SoundsTC.crystal, SoundsTC.crystal, SoundsTC.crystal, SoundsTC.crystal);
        SoundsTC.JAR = new SoundType(0.5f, 1.0f, SoundsTC.jar, SoundsTC.jar, SoundsTC.jar, SoundsTC.jar, SoundsTC.jar);
        SoundsTC.URN = new SoundType(0.5f, 1.5f, SoundsTC.urnbreak, SoundsTC.urnbreak, SoundsTC.urnbreak, SoundsTC.urnbreak, SoundsTC.urnbreak);
    }

    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        SoundsTC.zap = getRegisteredSoundEvent(event, "thaumcraft:zap");
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/SoundsTC.java:150

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 146

```java
        SoundsTC.crabdeath = getRegisteredSoundEvent(event, "thaumcraft:crabdeath");
        SoundsTC.crabtalk = getRegisteredSoundEvent(event, "thaumcraft:crabtalk");
        SoundsTC.chant = getRegisteredSoundEvent(event, "thaumcraft:chant");
        SoundsTC.coins = getRegisteredSoundEvent(event, "thaumcraft:coins");
        SoundsTC.urnbreak = getRegisteredSoundEvent(event, "thaumcraft:urnbreak");
        SoundsTC.evilportal = getRegisteredSoundEvent(event, "thaumcraft:evilportal");
        SoundsTC.grind = getRegisteredSoundEvent(event, "thaumcraft:grind");
        SoundsTC.dust = getRegisteredSoundEvent(event, "thaumcraft:dust");
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:26

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 22

```java
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:86

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 82

```java
        if (world.isRemote || !(p instanceof EntityPlayerMP)) {
            return false;
        }
        EntityPlayerMP player = (EntityPlayerMP)p;
        int exp = skipEvent ? 0 : ForgeHooks.onBlockBreakEvent(world, player.interactionManager.getGameType(), player, pos);
        if (exp == -1) {
            return false;
        }
        IBlockState iblockstate = world.getBlockState(pos);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:127

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 123

```java
                iblockstate.getBlock().harvestBlock(world, player, pos, iblockstate, tileentity, fakeStack);
            }
        }
        if (!player.interactionManager.isCreative() && flag1 && exp > 0) {
            iblockstate.getBlock().dropXpOnBlockBreak(world, pos, exp);
        }
        return flag1;
    }

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:141

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 137

```java
                double d3 = pos.getZ() - player.posZ;
                if (d0 * d0 + d2 * d2 + d3 * d3 >= 1024.0) {
                    continue;
                }
                ((EntityPlayerMP)player).connection.sendPacket(new SPacketBlockBreakAnim(par1, pos, par5));
            }
        }
    }

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:178

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 174

```java
            }
        }
    }

    public static boolean breakFurthestBlock(World world, BlockPos pos, IBlockState block, EntityPlayer player) {
        BlockUtils.lastPos = new BlockPos(pos);
        BlockUtils.lastdistance = 0.0;
        int reach = Utils.isWoodLog(world, pos) ? 2 : 1;
        findBlocks(world, pos, block, reach);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:336

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 332

```java
        for (IProperty prop : state.getProperties().keySet()) {
            if (prop.getName().equals("axis")) {
                if (state.getValue(prop) instanceof BlockLog.EnumAxis) {
                    ax = ((state.getValue(prop) == BlockLog.EnumAxis.X) ? EnumFacing.Axis.X : ((state.getValue(prop) == BlockLog.EnumAxis.Y) ? EnumFacing.Axis.Y : ((state.getValue(prop) == BlockLog.EnumAxis.Z) ? EnumFacing.Axis.Z : EnumFacing.Axis.Y)));
                    break;
                }
                if (state.getValue(prop) instanceof EnumFacing.Axis) {
                    ax = (EnumFacing.Axis)state.getValue(prop);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:340

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 336

```java
                    break;
                }
                if (state.getValue(prop) instanceof EnumFacing.Axis) {
                    ax = (EnumFacing.Axis)state.getValue(prop);
                    break;
                }
                continue;
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/EntityUtils.java:380

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 376

```java
            case 0: {
                IAttributeInstance sai = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                sai.removeModifier(EntityUtils.BOLDBUFF);
                sai.applyModifier(EntityUtils.BOLDBUFF);
                break;
            }
            case 3: {
                IAttributeInstance mai = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                mai.removeModifier(EntityUtils.MIGHTYBUFF);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/EntityUtils.java:386

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 382

```java
            case 3: {
                IAttributeInstance mai = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                mai.removeModifier(EntityUtils.MIGHTYBUFF);
                mai.applyModifier(EntityUtils.MIGHTYBUFF);
                break;
            }
            case 5: {
                int bh = (int)entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() / 2;
                entity.setAbsorptionAmount(entity.getAbsorptionAmount() + bh);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/EntityUtils.java:391

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 387

```java
            }
            case 5: {
                int bh = (int)entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() / 2;
                entity.setAbsorptionAmount(entity.getAbsorptionAmount() + bh);
                break;
            }
        }
    }

```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java:88

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 84

```java
                if (!itemStack.isEmpty()) {
                    ItemStack os = removeStackFrom(world, pos.offset(face), face.getOpposite(), itemStack, ThaumcraftInvHelper.InvFilter.BASEORE, false);
                    itemStack.setCount(itemStack.getCount() - os.getCount());
                    if (itemStack.isEmpty()) {
                        break;
                    }
                }
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java:140

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 136

```java
                        removed += es.getCount();
                    }
                }
                if (removed >= amount) {
                    break;
                }
            }
        }
        if (removed == 0) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java:267

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 263

```java
                }
                b = true;
                if (en2.get(e2) < en.get(e)) {
                    b = false;
                    break Label_0181;
                }
            }
        }
        return b;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/Utils.java:317

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 313

```java
        else {
            switch (rarity) {
                default: {
                    is = ((WeightedRandomLoot)WeightedRandom.getRandomItem(rand, (List)WeightedRandomLoot.lootBagCommon)).item;
                    break;
                }
                case 1: {
                    is = ((WeightedRandomLoot)WeightedRandom.getRandomItem(rand, (List)WeightedRandomLoot.lootBagUncommon)).item;
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/Utils.java:321

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 317

```java
                    break;
                }
                case 1: {
                    is = ((WeightedRandomLoot)WeightedRandom.getRandomItem(rand, (List)WeightedRandomLoot.lootBagUncommon)).item;
                    break;
                }
                case 2: {
                    is = ((WeightedRandomLoot)WeightedRandom.getRandomItem(rand, (List)WeightedRandomLoot.lootBagRare)).item;
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/Utils.java:325

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 321

```java
                    break;
                }
                case 2: {
                    is = ((WeightedRandomLoot)WeightedRandom.getRandomItem(rand, (List)WeightedRandomLoot.lootBagRare)).item;
                    break;
                }
            }
        }
        if (is.getItem() == Items.BOOK) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/lib/utils/Utils.java:480

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 476

```java
                }
                if (quality == 6) {
                    return ItemsTC.voidSword;
                }
                break;
            }
        }
        return null;
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileArcaneWorkbench.java:96

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 92

```java
                                z = q;
                            }
                            q -= (int)AuraHandler.drainVis(getWorld(), getPos().add(xx * 16, 0, zz * 16), (float)z, false);
                            if (q <= 0) {
                                break Label_0144;
                            }
                            if (attempts > 1000) {
                                break Label_0144;
                            }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileArcaneWorkbench.java:99

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 95

```java
                            if (q <= 0) {
                                break Label_0144;
                            }
                            if (attempts > 1000) {
                                break Label_0144;
                            }
                        }
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileFocalManipulator.java:122

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 118

```java
                        z = q;
                    }
                    q -= AuraHandler.drainVis(getWorld(), getPos().add(xx * 16, 0, zz * 16), z, false);
                    if (q <= 0.0f) {
                        break Label_0110;
                    }
                }
            }
            return vis - q;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileGolemBuilder.java:303

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 299

```java
        }
        return 0;
    }

    public boolean canRenderBreaking() {
        return true;
    }
}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:392

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 388

```java
                case 1:
                case 2:
                case 3: {
                    inEvEjectItem(0);
                    break;
                }
                case 4:
                case 5:
                case 6: {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:398

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 394

```java
                case 4:
                case 5:
                case 6: {
                    inEvWarp();
                    break;
                }
                case 7:
                case 8:
                case 9: {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:404

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 400

```java
                case 7:
                case 8:
                case 9: {
                    inEvZap(false);
                    break;
                }
                case 10:
                case 11: {
                    inEvZap(true);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:409

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 405

```java
                }
                case 10:
                case 11: {
                    inEvZap(true);
                    break;
                }
                case 12:
                case 13: {
                    inEvEjectItem(1);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:414

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 410

```java
                }
                case 12:
                case 13: {
                    inEvEjectItem(1);
                    break;
                }
                case 14:
                case 15: {
                    inEvEjectItem(2);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:419

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 415

```java
                }
                case 14:
                case 15: {
                    inEvEjectItem(2);
                    break;
                }
                case 16: {
                    inEvEjectItem(3);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:423

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 419

```java
                    break;
                }
                case 16: {
                    inEvEjectItem(3);
                    break;
                }
                case 17: {
                    inEvEjectItem(4);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:427

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 423

```java
                    break;
                }
                case 17: {
                    inEvEjectItem(4);
                    break;
                }
                case 18:
                case 19: {
                    inEvHarm(false);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:432

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 428

```java
                }
                case 18:
                case 19: {
                    inEvHarm(false);
                    break;
                }
                case 20:
                case 21: {
                    inEvEjectItem(5);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:437

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 433

```java
                }
                case 20:
                case 21: {
                    inEvEjectItem(5);
                    break;
                }
                case 22: {
                    inEvHarm(true);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:441

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 437

```java
                    break;
                }
                case 22: {
                    inEvHarm(true);
                    break;
                }
                case 23: {
                    world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.5f + world.rand.nextFloat(), false);
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:445

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 441

```java
                    break;
                }
                case 23: {
                    world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.5f + world.rand.nextFloat(), false);
                    break;
                }
            }
            stability += 5.0f + world.rand.nextFloat() * 5.0f;
            inResAdd();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:559

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 555

```java
            for (EntityLivingBase target : targets) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(pos, target, 0.3f - world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
                target.attackEntityFrom(DamageSource.MAGIC, (float)(4 + world.rand.nextInt(4)));
                if (!all) {
                    break;
                }
            }
        }
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:578

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 574

```java
                    pe.getCurativeItems().clear();
                    target.addPotionEffect(pe);
                }
                if (!all) {
                    break;
                }
            }
        }
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:734

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 730

```java
            }
            while (!stuff.isEmpty()) {
                Long[] posArray = stuff.toArray(new Long[stuff.size()]);
                if (posArray == null) {
                    break;
                }
                if (posArray[0] == null) {
                    break;
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:737

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 733

```java
                if (posArray == null) {
                    break;
                }
                if (posArray[0] == null) {
                    break;
                }
                long lp = posArray[0];
                try {
                    BlockPos c1 = BlockPos.fromLong(lp);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java:982

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 978

```java
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }

    public boolean canRenderBreaking() {
        return true;
    }

    public String[] getIGogglesText() {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:99

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 95

```java
            int amt = 9;
            switch (type) {
                case 0: {
                    amt = 9;
                    break;
                }
                case 1: {
                    amt = 1;
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:103

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 99

```java
                    break;
                }
                case 1: {
                    amt = 1;
                    break;
                }
                case 2:
                case 3: {
                    amt = 2;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:108

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 104

```java
                }
                case 2:
                case 3: {
                    amt = 2;
                    break;
                }
                case 4: {
                    amt = 4;
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:112

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 108

```java
                    break;
                }
                case 4: {
                    amt = 4;
                    break;
                }
                case 5:
                case 6: {
                    amt = 3;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:117

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 113

```java
                }
                case 5:
                case 6: {
                    amt = 3;
                    break;
                }
                case 7:
                case 8: {
                    amt = 6;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:122

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 118

```java
                }
                case 7:
                case 8: {
                    amt = 6;
                    break;
                }
                case 9: {
                    amt = 8;
                    break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:126

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 122

```java
                    break;
                }
                case 9: {
                    amt = 8;
                    break;
                }
            }
            IItemHandler above = ThaumcraftInvHelper.getItemHandlerAt(getWorld(), getPos().up(), EnumFacing.DOWN);
            IItemHandler below = ThaumcraftInvHelper.getItemHandlerAt(getWorld(), getPos().down(), EnumFacing.UP);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:141

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 137

```java
                            boolean b = true;
                            for (int i = 0; i < 9; ++i) {
                                if (craftMatrix.getStackInSlot(i) != null && !ItemHandlerHelper.insertItem(below, craftMatrix.getStackInSlot(i).copy(), true).isEmpty()) {
                                    b = false;
                                    break;
                                }
                            }
                            if (b) {
                                ItemHandlerHelper.insertItem(below, outStack.copy(), false);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:154

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 150

```java
                                }
                                InventoryUtils.removeStackFrom(getWorld(), getPos().up(), EnumFacing.DOWN, testStack, ThaumcraftInvHelper.InvFilter.BASEORE, false);
                                world.addBlockEvent(getPos(), getBlockType(), 1, 0);
                                --power;
                                break;
                            }
                        }
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:171

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 167

```java
            case 0: {
                for (int a = 0; a < 9; ++a) {
                    craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 1: {
                craftMatrix.setInventorySlotContents(0, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:175

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 171

```java
                break;
            }
            case 1: {
                craftMatrix.setInventorySlotContents(0, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                break;
            }
            case 2: {
                for (int a = 0; a < 2; ++a) {
                    craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:181

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 177

```java
            case 2: {
                for (int a = 0; a < 2; ++a) {
                    craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 3: {
                for (int a = 0; a < 2; ++a) {
                    craftMatrix.setInventorySlotContents(a * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:187

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 183

```java
            case 3: {
                for (int a = 0; a < 2; ++a) {
                    craftMatrix.setInventorySlotContents(a * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 4: {
                for (int a = 0; a < 2; ++a) {
                    for (int b = 0; b < 2; ++b) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:195

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 191

```java
                    for (int b = 0; b < 2; ++b) {
                        craftMatrix.setInventorySlotContents(a + b * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                    }
                }
                break;
            }
            case 5: {
                for (int a = 0; a < 3; ++a) {
                    craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:201

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 197

```java
            case 5: {
                for (int a = 0; a < 3; ++a) {
                    craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 6: {
                for (int a = 0; a < 3; ++a) {
                    craftMatrix.setInventorySlotContents(a * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:207

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 203

```java
            case 6: {
                for (int a = 0; a < 3; ++a) {
                    craftMatrix.setInventorySlotContents(a * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 7: {
                for (int a = 0; a < 6; ++a) {
                    craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:213

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 209

```java
            case 7: {
                for (int a = 0; a < 6; ++a) {
                    craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                }
                break;
            }
            case 8: {
                for (int a = 0; a < 2; ++a) {
                    for (int b = 0; b < 3; ++b) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:221

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 217

```java
                    for (int b = 0; b < 3; ++b) {
                        craftMatrix.setInventorySlotContents(a + b * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                    }
                }
                break;
            }
            case 9: {
                for (int a = 0; a < 9; ++a) {
                    if (a != 4) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TilePatternCrafter.java:229

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 225

```java
                    if (a != 4) {
                        craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
                    }
                }
                break;
            }
        }
        IRecipe ir = CraftingManager.findMatchingRecipe(craftMatrix, world);
        if (ir == null) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileResearchTable.java:174

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 170

```java
            case 0: {
                if (itemstack.getItem() instanceof IScribeTools) {
                    return true;
                }
                break;
            }
            case 1: {
                if (itemstack.getItem() == Items.PAPER && itemstack.getItemDamage() == 0) {
                    return true;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileResearchTable.java:180

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 176

```java
            case 1: {
                if (itemstack.getItem() == Items.PAPER && itemstack.getItemDamage() == 0) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java:181

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 177

```java
                        CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(recipeHash.get(a));
                        if (recipe.catalystMatches(getStackInSlot(0))) {
                            currentCraft = a;
                            currentRecipe = recipe;
                            break;
                        }
                    }
                }
                if (currentCraft < 0 || currentCraft >= recipeHash.size()) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java:194

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 190

```java
                for (Aspect aspect : recipeEssentia.get(currentCraft).getAspectsSortedByName()) {
                    if (essentia.getAmount(aspect) < recipeEssentia.get(currentCraft).getAmount(aspect)) {
                        currentSuction = aspect;
                        done = false;
                        break;
                    }
                }
                if (done) {
                    completeRecipe();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java:461

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 457

```java
                        }
                        for (Integer hash : recipeHash) {
                            if (creps.hash == hash) {
                                recipesTemp.add(creps);
                                break;
                            }
                        }
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileArcaneEar.java:94

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 90

```java
                        world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
                        world.notifyNeighborsOfStateChange(pos.offset(facing2), getBlockType(), true);
                        IBlockState state3 = world.getBlockState(pos);
                        world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state3, state3, 3);
                        break;
                    }
                }
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileBellows.java:96

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 92

```java
        }
        return bellows;
    }

    public boolean canRenderBreaking() {
        return true;
    }
}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileCondenser.java:129

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 125

```java
                    }
                    syncTile(false);
                    markDirty();
                    if (essentia >= MAX) {
                        break;
                    }
                }
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileCondenser.java:193

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 189

```java
                                }
                                found = true;
                                performCheck(p2, false, clogged || latticeDirty);
                                if (skip) {
                                    break;
                                }
                            }
                        }
                    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileHungryChest.java:18

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 14

```java
            adjacentChestChecked = true;
        }
    }

    public boolean canRenderBreaking() {
        return true;
    }

    public void closeInventory(EntityPlayer player) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileInfernalFurnace.java:110

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 106

```java
                            if (getWorld().rand.nextInt(20) == 0) {
                                AuraHelper.polluteAura(getWorld(), getPos().offset(getFacing().getOpposite()), 1.0f, true);
                            }
                            decrStackSize(a, 1);
                            break;
                        }
                        setInventorySlotContents(a, ItemStack.EMPTY);
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileInfernalFurnace.java:124

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 120

```java
                for (int a = 0; a < getSizeInventory(); ++a) {
                    if (canSmelt(getStackInSlot(a))) {
                        furnaceMaxCookTime = calcCookTime();
                        furnaceCookTime = furnaceMaxCookTime;
                        break;
                    }
                }
            }
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileInfernalFurnace.java:254

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 250

```java
                            if (is2.getCount() < 1) {
                                is2.setCount(1);
                            }
                            out.add(is2);
                            break;
                        }
                        break;
                    }
                    else {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileInfernalFurnace.java:256

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 252

```java
                            }
                            out.add(is2);
                            break;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileLampFertility.java:99

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 95

```java
                        if (partner != null) {
                            charges -= 5;
                            var11.setInLove(null);
                            partner.setInLove(null);
                            break Label_0314;
                        }
                        partner = var11;
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileLevitator.java:118

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 114

```java
                    }
                    e.fallDistance = 0.0f;
                    vis -= getCost();
                    if (vis <= 0) {
                        break;
                    }
                }
            }
            drawFX(facing, 0.1);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TilePotionSprayer.java:71

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 67

```java
                for (Aspect aspect : recipe.getAspectsSortedByName()) {
                    if (recipeProgress.getAmount(aspect) < recipe.getAmount(aspect)) {
                        currentSuction = aspect;
                        done = false;
                        break;
                    }
                }
                if (done) {
                    recipeProgress = new AspectList();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileRedstoneRelay.java:77

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 73

```java
        Transformation rot = Rotation.quarterRotations[0];
        switch (facing) {
            case WEST: {
                rot = Rotation.quarterRotations[1];
                break;
            }
            case NORTH: {
                rot = Rotation.quarterRotations[2];
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileRedstoneRelay.java:81

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 77

```java
                break;
            }
            case NORTH: {
                rot = Rotation.quarterRotations[2];
                break;
            }
            case EAST: {
                rot = Rotation.quarterRotations[3];
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileRedstoneRelay.java:85

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 81

```java
                break;
            }
            case EAST: {
                rot = Rotation.quarterRotations[3];
                break;
            }
        }
        return new Cuboid6(-0.375, 0.0625, -0.375, -0.125, 0.25, -0.125).apply(rot).add(new Vector3(getPos().getX() + 0.5, getPos().getY(), getPos().getZ() + 0.5));
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileRedstoneRelay.java:96

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 92

```java
        Transformation rot = Rotation.quarterRotations[0];
        switch (facing) {
            case WEST: {
                rot = Rotation.quarterRotations[1];
                break;
            }
            case NORTH: {
                rot = Rotation.quarterRotations[2];
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileRedstoneRelay.java:100

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 96

```java
                break;
            }
            case NORTH: {
                rot = Rotation.quarterRotations[2];
                break;
            }
            case EAST: {
                rot = Rotation.quarterRotations[3];
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileRedstoneRelay.java:104

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 100

```java
                break;
            }
            case EAST: {
                rot = Rotation.quarterRotations[3];
                break;
            }
        }
        return new Cuboid6(-0.125, 0.0625, 0.125, 0.125, 0.25, 0.375).apply(rot).add(new Vector3(getPos().getX() + 0.5, getPos().getY(), getPos().getZ() + 0.5));
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileSpa.java:118

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 114

```java
                            if (isValidLocation(p, true, tb)) {
                                consumeIngredients();
                                world.setBlockState(p, tb.getDefaultState());
                                checkQuanta(p);
                                break Label_0267;
                            }
                        }
                    }
                }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/devices/TileWaterJug.java:127

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 123

```java
                        if (q > 0) {
                            drain(new FluidStack(FluidRegistry.WATER, q), true);
                            markDirty();
                            world.addBlockEvent(getPos(), getBlockType(), 1, zz);
                            break;
                        }
                    }
                }
                else if (tile != null && tile instanceof IPetalApothecary && tank.getFluidAmount() >= 1000) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/essentia/TileCentrifuge.java:234

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 230

```java
    @Override
    public void setAspects(AspectList aspects) {
    }

    public boolean canRenderBreaking() {
        return true;
    }
}
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java:106

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 102

```java
            if (count % speed == 0 && aspects.size() > 0) {
                for (Aspect aspect : aspects.getAspects()) {
                    if (aspects.getAmount(aspect) > 0 && TileAlembic.processAlembics(getWorld(), getPos(), aspect)) {
                        takeFromContainer(aspect, 1);
                        break;
                    }
                }
                for (EnumFacing face : EnumFacing.HORIZONTALS) {
                    if (BlockStateUtils.getFacing(getBlockMetadata()) != face) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/essentia/TileSmelter.java:116

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 112

```java
                        if (aux.getBlock() == BlocksTC.smelterAux && BlockStateUtils.getFacing(aux) == face.getOpposite()) {
                            for (Aspect aspect2 : aspects.getAspects()) {
                                if (aspects.getAmount(aspect2) > 0 && TileAlembic.processAlembics(getWorld(), getPos().offset(face), aspect2)) {
                                    takeFromContainer(aspect2, 1);
                                    break;
                                }
                            }
                        }
                    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/essentia/TileTube.java:362

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 358

```java
                    a %= 6;
                    facing = EnumFacing.VALUES[a];
                    syncTile(true);
                    markDirty();
                    break;
                }
            }
            return true;
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/essentia/TileTubeValve.java:90

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 86

```java
                    a %= 6;
                    facing = EnumFacing.VALUES[a];
                    syncTile(true);
                    markDirty();
                    break;
                }
            }
            return true;
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/misc/TileHole.java:59

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 55

```java
                            if (a / 3 != 1 || a % 3 != 1) {
                                FocusEffectRift.createHole(world, getPos().add(-1 + a / 3, 0, -1 + a % 3), null, (byte)1, countdownmax);
                            }
                        }
                        break;
                    }
                    case Z: {
                        for (int a = 0; a < 9; ++a) {
                            if (a / 3 != 1 || a % 3 != 1) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/misc/TileHole.java:67

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 63

```java
                            if (a / 3 != 1 || a % 3 != 1) {
                                FocusEffectRift.createHole(world, getPos().add(-1 + a / 3, -1 + a % 3, 0), null, (byte)1, countdownmax);
                            }
                        }
                        break;
                    }
                    case X: {
                        for (int a = 0; a < 9; ++a) {
                            if (a / 3 != 1 || a % 3 != 1) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/tiles/misc/TileHole.java:75

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 71

```java
                            if (a / 3 != 1 || a % 3 != 1) {
                                FocusEffectRift.createHole(world, getPos().add(0, -1 + a / 3, -1 + a % 3), null, (byte)1, countdownmax);
                            }
                        }
                        break;
                    }
                }
                if (!FocusEffectRift.createHole(world, getPos().offset(direction.getOpposite()), direction, (byte)(count - 1), countdownmax)) {
                    count = 0;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/world/aura/AuraThread.java:47

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 43

```java
        Thaumcraft.log.info("Starting aura thread for dim " + dim);
        while (!stop) {
            if (AuraHandler.auras.isEmpty()) {
                Thaumcraft.log.warn("No auras found!");
                break;
            }
            long startTime = System.currentTimeMillis();
            AuraWorld auraWorld = AuraHandler.getAuraWorld(dim);
            if (auraWorld != null) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/world/biomes/BiomeGenMagicalForest.java:91

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 87

```java
            for (pp = new BlockPos(pos), pp = pp.add(4 + random.nextInt(8), 0, 4 + random.nextInt(8)), pp = world.getHeight(pp); pp.getY() > 30 && world.getBlockState(pp).getBlock() != Blocks.GRASS; pp = pp.down()) {}
            Block l1 = world.getBlockState(pp).getBlock();
            if (l1 == Blocks.GRASS) {
                world.setBlockState(pp, BlocksTC.grassAmbient.getDefaultState(), 2);
                break;
            }
        }
        for (int k = random.nextInt(3), i = 0; i < k; ++i) {
            BlockPos p2 = new BlockPos(pos);
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/world/objects/WorldGenGreatwoodTrees.java:327

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 323

```java
            var11[var7] = MathHelper.floor(par1ArrayOfInteger[var7] + var12 * var10);
            try {
                Block var14 = world.getBlockState(new BlockPos(var11[0], var11[1], var11[2])).getBlock();
                if (var14 != Blocks.AIR && var14 != BlocksTC.leafGreatwood) {
                    break;
                }
            }
            catch (Exception ex) {}
        }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/world/objects/WorldGenMound.java:2503

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 2499

```java
        Block b = BlocksTC.lootCrateCommon;
        switch (md) {
            case 0: {
                b = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateCommon : BlocksTC.lootUrnCommon);
                break;
            }
            case 1: {
                b = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateUncommon : BlocksTC.lootUrnUncommon);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/world/objects/WorldGenMound.java:2507

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 2503

```java
                break;
            }
            case 1: {
                b = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateUncommon : BlocksTC.lootUrnUncommon);
                break;
            }
            case 2: {
                b = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateRare : BlocksTC.lootUrnRare);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/world/objects/WorldGenMound.java:2511

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 2507

```java
                break;
            }
            case 2: {
                b = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateRare : BlocksTC.lootUrnRare);
                break;
            }
        }
        world.setBlockState(new BlockPos(i + 9, j + 1, k + 7), b.getDefaultState());
        rr = rand.nextFloat();
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/world/objects/WorldGenMound.java:2521

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 2517

```java
        Block b2 = BlocksTC.lootCrateCommon;
        switch (md) {
            case 0: {
                b2 = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateCommon : BlocksTC.lootUrnCommon);
                break;
            }
            case 1: {
                b2 = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateUncommon : BlocksTC.lootUrnUncommon);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/world/objects/WorldGenMound.java:2525

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 2521

```java
                break;
            }
            case 1: {
                b2 = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateUncommon : BlocksTC.lootUrnUncommon);
                break;
            }
            case 2: {
                b2 = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateRare : BlocksTC.lootUrnRare);
                break;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/world/objects/WorldGenMound.java:2529

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 2525

```java
                break;
            }
            case 2: {
                b2 = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateRare : BlocksTC.lootUrnRare);
                break;
            }
        }
        world.setBlockState(new BlockPos(i + 9, j + 1, k + 11), b2.getDefaultState());
        if (rand.nextInt(3) == 0) {
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/common/world/ThaumcraftWorldGenerator.java:209

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 205

```java
                        }
                    }
                }
                if (cc > maxCrystals) {
                    break;
                }
            }
        }
    }
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/proxies/ProxyGUI.java:118

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 114

```java
                    ISealEntity se = ItemGolemBell.getSeal(player);
                    if (se != null) {
                        return se.getSeal().returnGui(world, player, new BlockPos(x, y, z), se.getSealPos().face, se);
                    }
                    break;
                }
                case 20: {
                    RayTraceResult ray = RayTracer.retrace(player);
                    BlockPos target = null;
```

### thaumcraft:SealBreak @ src/main/java/thaumcraft/proxies/ProxyGUI.java:187

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 183

```java
                ISealEntity se = ItemGolemBell.getSeal(player);
                if (se != null) {
                    return se.getSeal().returnContainer(world, player, new BlockPos(x, y, z), se.getSealPos().face, se);
                }
                break;
            }
            case 20: {
                return new ContainerLogistics(player.inventory, world);
            }
```

### thaumcraft:SealButcher @ src/main/java/thaumcraft/common/config/ConfigItems.java:37

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 33

```java
import thaumcraft.common.golems.ItemGolemPlacer;
import thaumcraft.common.golems.seals.ItemSealPlacer;
import thaumcraft.common.golems.seals.SealBreaker;
import thaumcraft.common.golems.seals.SealBreakerAdvanced;
import thaumcraft.common.golems.seals.SealButcher;
import thaumcraft.common.golems.seals.SealEmpty;
import thaumcraft.common.golems.seals.SealEmptyAdvanced;
import thaumcraft.common.golems.seals.SealFill;
import thaumcraft.common.golems.seals.SealFillAdvanced;
```

### thaumcraft:SealButcher @ src/main/java/thaumcraft/common/config/ConfigItems.java:317

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 313

```java
        SealHandler.registerSeal(new SealFillAdvanced());
        SealHandler.registerSeal(new SealEmpty());
        SealHandler.registerSeal(new SealEmptyAdvanced());
        SealHandler.registerSeal(new SealHarvest());
        SealHandler.registerSeal(new SealButcher());
        SealHandler.registerSeal(new SealGuard());
        SealHandler.registerSeal(new SealGuardAdvanced());
        SealHandler.registerSeal(new SealLumber());
        SealHandler.registerSeal(new SealBreaker());
```

### thaumcraft:SealButcher @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:269

- Classification: SEAL_BOUNDARY
- Hit kind: INFUSION_RECIPE_SOURCE
- Context start line: 265

```java
    }

    public static void initializeInfusionRecipes() {
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealHarvest"), new InfusionRecipe("SEALHARVEST", GolemHelper.getSealStack("thaumcraft:harvest"), 0, new AspectList().add(Aspect.PLANT, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(Items.MELON_SEEDS), new ItemStack(Items.BEETROOT_SEEDS), new ItemStack(Items.REEDS), new ItemStack(Blocks.CACTUS)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealButcher"), new InfusionRecipe("SEALBUTCHER", GolemHelper.getSealStack("thaumcraft:butcher"), 0, new AspectList().add(Aspect.BEAST, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), GolemHelper.getSealStack("thaumcraft:guard"), "leather", new ItemStack(Blocks.WOOL, 1, 32767), new ItemStack(Items.RABBIT_HIDE), new ItemStack(Items.PORKCHOP), new ItemStack(Items.MUTTON), new ItemStack(Items.BEEF)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealBreak"), new InfusionRecipe("SEALBREAK", GolemHelper.getSealStack("thaumcraft:breaker"), 1, new AspectList().add(Aspect.TOOL, 10).add(Aspect.ENTROPY, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), Ingredient.fromItem(Items.GOLDEN_AXE), Ingredient.fromItem(Items.GOLDEN_PICKAXE), Ingredient.fromItem(Items.GOLDEN_SHOVEL)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterAir"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalAir), 0, new AspectList().add(Aspect.AIR, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.AIR), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterFire"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalFire), 0, new AspectList().add(Aspect.FIRE, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterWater"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalWater), 0, new AspectList().add(Aspect.WATER, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.WATER), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
```

### thaumcraft:SealButcher @ src/main/java/thaumcraft/common/golems/seals/SealButcher.java:35

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 31

```java
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.tasks.TaskHandler;


public class SealButcher implements ISeal, ISealGui, ISealConfigArea
{
    int delay;
    boolean wait;
    ResourceLocation icon;
```

### thaumcraft:SealButcher @ src/main/java/thaumcraft/common/golems/seals/SealButcher.java:41

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 37

```java
    int delay;
    boolean wait;
    ResourceLocation icon;

    public SealButcher() {
        delay = new Random(System.nanoTime()).nextInt(200);
        wait = false;
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_butcher");
    }
```

### thaumcraft:SealButcher @ src/main/java/thaumcraft/common/golems/seals/SealButcher.java:44

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 40

```java

    public SealButcher() {
        delay = new Random(System.nanoTime()).nextInt(200);
        wait = false;
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_butcher");
    }

    @Override
    public String getKey() {
```

### thaumcraft:SealButcher @ src/main/java/thaumcraft/common/golems/seals/SealButcher.java:49

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 45

```java
    }

    @Override
    public String getKey() {
        return "thaumcraft:butcher";
    }

    @Override
    public void tickSeal(World world, ISealEntity seal) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/api/ThaumcraftApi.java:456

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 452

```java
	 * To define mod crops you need to use FMLInterModComms in your @Mod.Init method.
	 * There are two 'types' of crops you can add. Standard crops and clickable crops.
	 *
	 * Standard crops work like normal vanilla crops - they grow until a certain metadata
	 * value is reached and you harvest them by destroying the block and collecting the blocks.
	 * You need to create and ItemStack that tells the golem what block id and metadata represents
	 * the crop when fully grown. Sending a metadata of [OreDictionary.WILDCARD_VALUE] will mean the metadata won't get
	 * checked.
	 * Example for vanilla wheat:
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/api/ThaumcraftApi.java:461

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 457

```java
	 * You need to create and ItemStack that tells the golem what block id and metadata represents
	 * the crop when fully grown. Sending a metadata of [OreDictionary.WILDCARD_VALUE] will mean the metadata won't get
	 * checked.
	 * Example for vanilla wheat:
	 * FMLInterModComms.sendMessage("Thaumcraft", "harvestStandardCrop", new ItemStack(Block.crops,1,7));
	 *
	 * Clickable crops are crops that you right click to gather their bounty instead of destroying them.
	 * As for standard crops, you need to create and ItemStack that tells the golem what block id
	 * and metadata represents the crop when fully grown. The golem will trigger the blocks onBlockActivated method.
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/api/ThaumcraftApi.java:468

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 464

```java
	 * As for standard crops, you need to create and ItemStack that tells the golem what block id
	 * and metadata represents the crop when fully grown. The golem will trigger the blocks onBlockActivated method.
	 * Sending a metadata of [OreDictionary.WILDCARD_VALUE] will mean the metadata won't get checked.
	 * Example (this will technically do nothing since clicking wheat does nothing, but you get the idea):
	 * FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(Block.crops,1,7));
	 *
	 * Stacked crops (like reeds) are crops that you wish the bottom block should remain after harvesting.
	 * As for standard crops, you need to create and ItemStack that tells the golem what block id
	 * and metadata represents the crop when fully grown. Sending a metadata of [OreDictionary.WILDCARD_VALUE] will mean the actualy md won't get
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/api/ThaumcraftApi.java:470

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 466

```java
	 * Sending a metadata of [OreDictionary.WILDCARD_VALUE] will mean the metadata won't get checked.
	 * Example (this will technically do nothing since clicking wheat does nothing, but you get the idea):
	 * FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(Block.crops,1,7));
	 *
	 * Stacked crops (like reeds) are crops that you wish the bottom block should remain after harvesting.
	 * As for standard crops, you need to create and ItemStack that tells the golem what block id
	 * and metadata represents the crop when fully grown. Sending a metadata of [OreDictionary.WILDCARD_VALUE] will mean the actualy md won't get
	 * checked. If it has the order upgrade it will only harvest if the crop is more than one block high.
	 * Example:
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/api/ThaumcraftApi.java:473

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 469

```java
	 *
	 * Stacked crops (like reeds) are crops that you wish the bottom block should remain after harvesting.
	 * As for standard crops, you need to create and ItemStack that tells the golem what block id
	 * and metadata represents the crop when fully grown. Sending a metadata of [OreDictionary.WILDCARD_VALUE] will mean the actualy md won't get
	 * checked. If it has the order upgrade it will only harvest if the crop is more than one block high.
	 * Example:
	 * FMLInterModComms.sendMessage("Thaumcraft", "harvestStackedCrop", new ItemStack(Block.reed,1,7));
	 */

```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/api/ThaumcraftApi.java:475

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 471

```java
	 * As for standard crops, you need to create and ItemStack that tells the golem what block id
	 * and metadata represents the crop when fully grown. Sending a metadata of [OreDictionary.WILDCARD_VALUE] will mean the actualy md won't get
	 * checked. If it has the order upgrade it will only harvest if the crop is more than one block high.
	 * Example:
	 * FMLInterModComms.sendMessage("Thaumcraft", "harvestStackedCrop", new ItemStack(Block.reed,1,7));
	 */

	// PORTABLE HOLE BLACKLIST
	/**
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/basic/BlockBannerTC.java:150

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 146

```java
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
    }

    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if (te instanceof TileBanner) {
            ItemStack drop = new ItemStack(this);
            if (dye != null || ((TileBanner)te).getAspect() != null) {
                drop.setTagCompound(new NBTTagCompound());
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/basic/BlockBannerTC.java:162

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 158

```java
            }
            spawnAsEntity(worldIn, pos, drop);
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, null, stack);
        }
    }
}
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/basic/BlockPavingStone.java:37

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 33

```java
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);
    }

    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    public boolean hasTileEntity(IBlockState state) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/basic/BlockPlanksTC.java:14

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 10

```java
public class BlockPlanksTC extends BlockTC
{
    public BlockPlanksTC(String name) {
        super(Material.WOOD, name);
        setHarvestLevel("axe", 0);
        setHardness(2.0f);
        setSoundType(SoundType.WOOD);
    }

```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/basic/BlockTable.java:38

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 34

```java
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    public boolean isOpaqueCube(IBlockState state) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/basic/BlockTranslucent.java:29

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 25

```java
    public boolean isBeaconBase(IBlockAccess world, BlockPos pos, BlockPos beacon) {
        return true;
    }

    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    public EnumPushReaction getMobilityFlag(IBlockState state) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/BlockTCTile.java:30

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 26

```java
        setResistance(20.0f);
        tileClass = tc;
    }

    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/devices/BlockBrainBox.java:40

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 36

```java
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java:120

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 116

```java
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        triggerUpdate(worldIn, pos);
    }

```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/devices/BlockCondenserLattice.java:121

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 117

```java
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        triggerUpdate(worldIn, pos);
    }

    public void triggerUpdate(World world, BlockPos pos) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/devices/BlockHungryChest.java:59

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 55

```java
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/devices/BlockMirror.java:37

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 33

```java
    public BlockMirror(Class cls, String name) {
        super(Material.IRON, cls, name);
        setSoundType(SoundsTC.JAR);
        setHardness(0.1f);
        setHarvestLevel(null, 0);
        IBlockState bs = blockState.getBaseState();
        bs.withProperty((IProperty)IBlockFacing.FACING, (Comparable)EnumFacing.UP);
        setDefaultState(bs);
    }
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/devices/BlockMirror.java:52

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 48

```java
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/devices/BlockMirror.java:135

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 131

```java
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
    }

    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if (te instanceof TileMirror || te instanceof TileMirrorEssentia) {
            dropMirror(worldIn, pos, state, te);
        }
        else {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/devices/BlockMirror.java:140

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 136

```java
        if (te instanceof TileMirror || te instanceof TileMirrorEssentia) {
            dropMirror(worldIn, pos, state, te);
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, null, stack);
        }
    }

    public void dropMirror(World world, BlockPos pos, IBlockState state, TileEntity te) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/essentia/BlockEssentiaTransport.java:36

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 32

```java
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    public boolean isOpaqueCube(IBlockState state) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:99

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 95

```java
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
    }

    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if (te instanceof TileJarFillable) {
            spawnFilledJar(worldIn, pos, state, (TileJarFillable)te);
        }
        else if (te instanceof TileJarBrain) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java:107

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 103

```java
        else if (te instanceof TileJarBrain) {
            spawnBrainJar(worldIn, pos, state, (TileJarBrain)te);
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, null, stack);
        }
    }

    private void spawnFilledJar(World world, BlockPos pos, IBlockState state, TileJarFillable te) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/essentia/BlockSmelterAux.java:39

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 35

```java
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/essentia/BlockSmelterVent.java:36

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 32

```java
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return false;
    }

    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/misc/BlockPlaceholder.java:41

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 37

```java
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    protected boolean canSilkHarvest() {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/world/BlockLoot.java:43

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 39

```java
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    protected boolean canSilkHarvest() {
        return true;
    }

    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/world/BlockLoot.java:47

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 43

```java
    protected boolean canSilkHarvest() {
        return true;
    }

    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/world/ore/BlockCrystal.java:80

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 76

```java
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemById(0);
    }

    protected boolean canSilkHarvest() {
        return false;
    }

    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/world/ore/BlockCrystal.java:302

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 298

```java
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }

    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return false;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/world/plants/BlockLogsTC.java:28

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 24

```java
    public static PropertyEnum AXIS;

    public BlockLogsTC(String name) {
        super(Material.WOOD, name);
        setHarvestLevel("axe", 0);
        setHardness(2.0f);
        setResistance(5.0f);
        setSoundType(SoundType.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(BlockLogsTC.AXIS, (Comparable)EnumFacing.Axis.Y));
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/world/taint/BlockTaint.java:138

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 134

```java
            }
        }
    }

    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/world/taint/BlockTaint.java:210

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 206

```java
        }
        return super.getDrops(world, pos, state, fortune);
    }

    protected boolean canSilkHarvest() {
        return false;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintFeature.java:39

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 35

```java
        setDefaultState(bs);
        setTickRandomly(true);
    }

    protected boolean canSilkHarvest() {
        return false;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintFeature.java:88

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 84

```java
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemById(0);
    }

    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintFibre.java:108

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 104

```java
    public void die(World world, BlockPos pos, IBlockState blockState) {
        world.setBlockToAir(pos);
    }

    protected boolean canSilkHarvest() {
        return false;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintLog.java:29

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 25

```java
    public static PropertyEnum AXIS;

    public BlockTaintLog() {
        super(ThaumcraftMaterials.MATERIAL_TAINT, "taint_log");
        setHarvestLevel("axe", 0);
        setHardness(3.0f);
        setResistance(100.0f);
        setSoundType(SoundsTC.GORE);
        setDefaultState(blockState.getBaseState().withProperty(BlockTaintLog.AXIS, (Comparable)EnumFacing.Axis.Y));
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/config/ConfigBlocks.java:155

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 151

```java

public class ConfigBlocks
{
    public static void initMisc() {
        BlocksTC.oreAmber.setHarvestLevel("pickaxe", 1);
        BlocksTC.oreCinnabar.setHarvestLevel("pickaxe", 2);
        BlockUtils.portableHoleBlackList.add("minecraft:bed");
        BlockUtils.portableHoleBlackList.add("minecraft:piston");
        BlockUtils.portableHoleBlackList.add("minecraft:piston_head");
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/config/ConfigBlocks.java:156

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 152

```java
public class ConfigBlocks
{
    public static void initMisc() {
        BlocksTC.oreAmber.setHarvestLevel("pickaxe", 1);
        BlocksTC.oreCinnabar.setHarvestLevel("pickaxe", 2);
        BlockUtils.portableHoleBlackList.add("minecraft:bed");
        BlockUtils.portableHoleBlackList.add("minecraft:piston");
        BlockUtils.portableHoleBlackList.add("minecraft:piston_head");
        BlockUtils.portableHoleBlackList.add("minecraft:sticky_piston");
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/config/ConfigItems.java:45

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 41

```java
import thaumcraft.common.golems.seals.SealFillAdvanced;
import thaumcraft.common.golems.seals.SealGuard;
import thaumcraft.common.golems.seals.SealGuardAdvanced;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.seals.SealHarvest;
import thaumcraft.common.golems.seals.SealLumber;
import thaumcraft.common.golems.seals.SealPickup;
import thaumcraft.common.golems.seals.SealPickupAdvanced;
import thaumcraft.common.golems.seals.SealProvide;
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/config/ConfigItems.java:316

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 312

```java
        SealHandler.registerSeal(new SealFill());
        SealHandler.registerSeal(new SealFillAdvanced());
        SealHandler.registerSeal(new SealEmpty());
        SealHandler.registerSeal(new SealEmptyAdvanced());
        SealHandler.registerSeal(new SealHarvest());
        SealHandler.registerSeal(new SealButcher());
        SealHandler.registerSeal(new SealGuard());
        SealHandler.registerSeal(new SealGuardAdvanced());
        SealHandler.registerSeal(new SealLumber());
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:268

- Classification: SEAL_BOUNDARY
- Hit kind: ARCANE_RECIPE_SOURCE
- Context start line: 264

```java
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:RedstoneInlay"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONSTABLE", 25, new AspectList().add(Aspect.WATER, 1), new ItemStack(BlocksTC.inlay, 2), new Object[] { "dustRedstone", "ingotGold" }));
    }

    public static void initializeInfusionRecipes() {
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealHarvest"), new InfusionRecipe("SEALHARVEST", GolemHelper.getSealStack("thaumcraft:harvest"), 0, new AspectList().add(Aspect.PLANT, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(Items.MELON_SEEDS), new ItemStack(Items.BEETROOT_SEEDS), new ItemStack(Items.REEDS), new ItemStack(Blocks.CACTUS)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealButcher"), new InfusionRecipe("SEALBUTCHER", GolemHelper.getSealStack("thaumcraft:butcher"), 0, new AspectList().add(Aspect.BEAST, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), GolemHelper.getSealStack("thaumcraft:guard"), "leather", new ItemStack(Blocks.WOOL, 1, 32767), new ItemStack(Items.RABBIT_HIDE), new ItemStack(Items.PORKCHOP), new ItemStack(Items.MUTTON), new ItemStack(Items.BEEF)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealBreak"), new InfusionRecipe("SEALBREAK", GolemHelper.getSealStack("thaumcraft:breaker"), 1, new AspectList().add(Aspect.TOOL, 10).add(Aspect.ENTROPY, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), Ingredient.fromItem(Items.GOLDEN_AXE), Ingredient.fromItem(Items.GOLDEN_PICKAXE), Ingredient.fromItem(Items.GOLDEN_SHOVEL)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterAir"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalAir), 0, new AspectList().add(Aspect.AIR, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.AIR), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterFire"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalFire), 0, new AspectList().add(Aspect.FIRE, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:241

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 237

```java
        return getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, getHeldItemMainhand()) > 0;
    }

    private boolean canSilkTouch(BlockPos pos, IBlockState state) {
        return hasSilkTouch() && state.getBlock().canSilkHarvest(world, pos, state, null);
    }

    @SubscribeEvent
    public static void harvestBlockEvent(BlockEvent.HarvestDropsEvent event) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:245

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 241

```java
        return hasSilkTouch() && state.getBlock().canSilkHarvest(world, pos, state, null);
    }

    @SubscribeEvent
    public static void harvestBlockEvent(BlockEvent.HarvestDropsEvent event) {
        if (event.getHarvester() != null && event.getHarvester().getName().equals("FakeThaumcraftBore")) {
            ArrayList<ItemStack> droplist = new ArrayList<ItemStack>();
            if (EntityArcaneBore.drops.containsKey(event.getHarvester().arrowHitTimer) && EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer) != null) {
                droplist = EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer);
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:246

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 242

```java
    }

    @SubscribeEvent
    public static void harvestBlockEvent(BlockEvent.HarvestDropsEvent event) {
        if (event.getHarvester() != null && event.getHarvester().getName().equals("FakeThaumcraftBore")) {
            ArrayList<ItemStack> droplist = new ArrayList<ItemStack>();
            if (EntityArcaneBore.drops.containsKey(event.getHarvester().arrowHitTimer) && EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer) != null) {
                droplist = EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer);
            }
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:248

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 244

```java
    @SubscribeEvent
    public static void harvestBlockEvent(BlockEvent.HarvestDropsEvent event) {
        if (event.getHarvester() != null && event.getHarvester().getName().equals("FakeThaumcraftBore")) {
            ArrayList<ItemStack> droplist = new ArrayList<ItemStack>();
            if (EntityArcaneBore.drops.containsKey(event.getHarvester().arrowHitTimer) && EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer) != null) {
                droplist = EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer);
            }
            for (ItemStack s : event.getDrops()) {
                if (event.getHarvester().world.rand.nextFloat() <= event.getDropChance()) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:249

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 245

```java
    public static void harvestBlockEvent(BlockEvent.HarvestDropsEvent event) {
        if (event.getHarvester() != null && event.getHarvester().getName().equals("FakeThaumcraftBore")) {
            ArrayList<ItemStack> droplist = new ArrayList<ItemStack>();
            if (EntityArcaneBore.drops.containsKey(event.getHarvester().arrowHitTimer) && EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer) != null) {
                droplist = EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer);
            }
            for (ItemStack s : event.getDrops()) {
                if (event.getHarvester().world.rand.nextFloat() <= event.getDropChance()) {
                    droplist.add(s);
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:252

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 248

```java
            if (EntityArcaneBore.drops.containsKey(event.getHarvester().arrowHitTimer) && EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer) != null) {
                droplist = EntityArcaneBore.drops.get(event.getHarvester().arrowHitTimer);
            }
            for (ItemStack s : event.getDrops()) {
                if (event.getHarvester().world.rand.nextFloat() <= event.getDropChance()) {
                    droplist.add(s);
                }
            }
            EntityArcaneBore.drops.put(event.getHarvester().arrowHitTimer, droplist);
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:256

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 252

```java
                if (event.getHarvester().world.rand.nextFloat() <= event.getDropChance()) {
                    droplist.add(s);
                }
            }
            EntityArcaneBore.drops.put(event.getHarvester().arrowHitTimer, droplist);
            event.getDrops().clear();
        }
    }

```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java:278

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 274

```java
                fp.arrowHitTimer = getEntityId();
                fp.xpCooldown = 1;
                fp.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
                fp.setHeldItem(EnumHand.MAIN_HAND, getHeldItemMainhand());
                if (BlockUtils.harvestBlock(getEntityWorld(), fp, digTarget, false, false, fortune, false)) {
                    ArrayList<ItemStack> items = EntityArcaneBore.drops.get(getEntityId());
                    if (items == null) {
                        items = new ArrayList<ItemStack>();
                    }
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/golems/seals/SealBreaker.java:130

- Classification: SEAL_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 126

```java
                BlockUtils.destroyBlockPartially(world, golem.getGolemEntity().getEntityId(), task.getPos(), progress);
                return false;
            }
            BlockUtils.destroyBlockPartially(world, golem.getGolemEntity().getEntityId(), task.getPos(), 10);
            BlockUtils.harvestBlock(world, fp, task.getPos(), true, silky, 0, true);
            golem.addRankXp(1);
            cache.remove(task.getId());
        }
        task.setSuspended(true);
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/golems/seals/SealHarvest.java:57

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 53

```java
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.CropUtils;


public class SealHarvest implements ISeal, ISealGui, ISealConfigArea, ISealConfigToggles
{
    int delay;
    int count;
    HashMap<Long, ReplantInfo> replantTasks;
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/golems/seals/SealHarvest.java:65

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 61

```java
    HashMap<Long, ReplantInfo> replantTasks;
    ResourceLocation icon;
    protected SealToggle[] props;

    public SealHarvest() {
        delay = new Random(System.nanoTime()).nextInt(33);
        count = 0;
        replantTasks = new HashMap<Long, ReplantInfo>();
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_harvest");
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/golems/seals/SealHarvest.java:69

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 65

```java
    public SealHarvest() {
        delay = new Random(System.nanoTime()).nextInt(33);
        count = 0;
        replantTasks = new HashMap<Long, ReplantInfo>();
        icon = new ResourceLocation("thaumcraft", "items/seals/seal_harvest");
        props = new SealToggle[] { new SealToggle(true, "prep", "golem.prop.replant"), new SealToggle(false, "ppro", "golem.prop.provision") };
    }

    @Override
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/golems/seals/SealHarvest.java:75

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 71

```java
    }

    @Override
    public String getKey() {
        return "thaumcraft:harvest";
    }

    @Override
    public void tickSeal(World world, ISealEntity seal) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/golems/seals/SealHarvest.java:132

- Classification: SEAL_BOUNDARY
- Hit kind: SEAL_BEHAVIOR_SOURCE
- Context start line: 128

```java
            }
            else {
                GolemInteractionHelper.golemClick(world, golem, task.getPos(), task.getSealPos().face, ItemStack.EMPTY, false, true);
                if (CropUtils.isGrownCrop(world, task.getPos())) {
                    BlockUtils.harvestBlock(world, fp, task.getPos(), true, false, 0, true);
                    golem.addRankXp(1);
                    golem.swingArm();
                    if (getToggles()[0].value) {
                        ItemStack seed = ThaumcraftApi.getSeed(bs.getBlock());
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/items/curios/ItemEnchantmentPlaceholder.java:42

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 38

```java
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.ITALIC + "" + TextFormatting.DARK_AQUA + I18n.translateToLocal("item.enchanted_placeholder.text"));
    }

    public boolean canHarvestBlock(IBlockState blockIn) {
        return true;
    }

    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/items/curios/ItemEnchantmentPlaceholder.java:46

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 42

```java
    public boolean canHarvestBlock(IBlockState blockIn) {
        return true;
    }

    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        return true;
    }

    public int getHarvestLevel(ItemStack stack, String toolClass, EntityPlayer player, IBlockState blockState) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/items/curios/ItemEnchantmentPlaceholder.java:50

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 46

```java
    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        return true;
    }

    public int getHarvestLevel(ItemStack stack, String toolClass, EntityPlayer player, IBlockState blockState) {
        return 99;
    }
}
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/items/tools/ItemPrimalCrusher.java:63

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 59

```java
    public ModelResourceLocation getCustomModelResourceLocation(String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }

    public boolean canHarvestBlock(IBlockState p_150897_1_) {
        return p_150897_1_.getMaterial() != Material.WOOD && p_150897_1_.getMaterial() != Material.LEAVES && p_150897_1_.getMaterial() != Material.PLANTS;
    }

    public float getDestroySpeed(ItemStack stack, IBlockState state) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:227

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 223

```java
            else if (item instanceof ItemTool) {
                String mat = ((ItemTool)item).getToolMaterialName();
                for (Item.ToolMaterial tm : Item.ToolMaterial.values()) {
                    if (tm.toString().equals(mat)) {
                        tmp.merge(Aspect.TOOL, (tm.getHarvestLevel() + 1) * 4);
                    }
                }
            }
            else if (item instanceof ItemShears || item instanceof ItemHoe) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:250

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 246

```java
                            vs.player.inventory.decrStackSize(slot, 1);
                        }
                        if (vs.pickup) {
                            List<ItemStack> ret = new ArrayList<ItemStack>();
                            if (vs.silk && bs.getBlock().canSilkHarvest(world, vs.pos, bs, vs.player)) {
                                ItemStack itemstack = BlockUtils.getSilkTouchDrop(bs);
                                if (itemstack != null && !itemstack.isEmpty()) {
                                    ret.add(itemstack);
                                }
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ServerEvents.java:339

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 335

```java
                        }
                        BreakData breakData = vs;
                        breakData.durabilityCurrent -= vs.strength;
                        if (vs.durabilityCurrent <= 0.0f) {
                            BlockUtils.harvestBlock(world, vs.player, vs.pos, true, vs.silk, vs.fortune, false);
                            if (vs.fx) {
                                world.sendBlockBreakProgress(vs.pos.hashCode(), vs.pos, -1);
                            }
                            if (vs.visCost <= 0.0f) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:187

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 183

```java
        return Utils.isWoodLog(world, pos) || Utils.isOreBlock(world, pos);
    }

    @SubscribeEvent
    public static void harvestBlockEvent(BlockEvent.HarvestDropsEvent event) {
        if (!event.getWorld().isRemote && !event.isSilkTouching() && event.getState().getBlock() != null && ((event.getState().getBlock() == Blocks.DIAMOND_ORE && event.getWorld().rand.nextFloat() < 0.05) || (event.getState().getBlock() == Blocks.EMERALD_ORE && event.getWorld().rand.nextFloat() < 0.075) || (event.getState().getBlock() == Blocks.LAPIS_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == Blocks.COAL_ORE && event.getWorld().rand.nextFloat() < 0.001) || (event.getState().getBlock() == Blocks.LIT_REDSTONE_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == Blocks.REDSTONE_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == Blocks.QUARTZ_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == BlocksTC.oreAmber && event.getWorld().rand.nextFloat() < 0.05) || (event.getState().getBlock() == BlocksTC.oreQuartz && event.getWorld().rand.nextFloat() < 0.05))) {
            event.getDrops().add(new ItemStack(ItemsTC.nuggets, 1, 10));
        }
        if (!event.getWorld().isRemote && event.getHarvester() != null) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:191

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 187

```java
    public static void harvestBlockEvent(BlockEvent.HarvestDropsEvent event) {
        if (!event.getWorld().isRemote && !event.isSilkTouching() && event.getState().getBlock() != null && ((event.getState().getBlock() == Blocks.DIAMOND_ORE && event.getWorld().rand.nextFloat() < 0.05) || (event.getState().getBlock() == Blocks.EMERALD_ORE && event.getWorld().rand.nextFloat() < 0.075) || (event.getState().getBlock() == Blocks.LAPIS_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == Blocks.COAL_ORE && event.getWorld().rand.nextFloat() < 0.001) || (event.getState().getBlock() == Blocks.LIT_REDSTONE_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == Blocks.REDSTONE_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == Blocks.QUARTZ_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == BlocksTC.oreAmber && event.getWorld().rand.nextFloat() < 0.05) || (event.getState().getBlock() == BlocksTC.oreQuartz && event.getWorld().rand.nextFloat() < 0.05))) {
            event.getDrops().add(new ItemStack(ItemsTC.nuggets, 1, 10));
        }
        if (!event.getWorld().isRemote && event.getHarvester() != null) {
            ItemStack heldItem = event.getHarvester().getHeldItem(event.getHarvester().getActiveHand());
            if (heldItem != null && !heldItem.isEmpty()) {
                List<EnumInfusionEnchantment> list = EnumInfusionEnchantment.getInfusionEnchantments(heldItem);
                if (event.isSilkTouching() || ForgeHooks.isToolEffective(event.getWorld(), event.getPos(), heldItem) || (heldItem.getItem() instanceof ItemTool && heldItem.getItem().getDestroySpeed(heldItem, event.getState()) > 1.0f)) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:192

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 188

```java
        if (!event.getWorld().isRemote && !event.isSilkTouching() && event.getState().getBlock() != null && ((event.getState().getBlock() == Blocks.DIAMOND_ORE && event.getWorld().rand.nextFloat() < 0.05) || (event.getState().getBlock() == Blocks.EMERALD_ORE && event.getWorld().rand.nextFloat() < 0.075) || (event.getState().getBlock() == Blocks.LAPIS_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == Blocks.COAL_ORE && event.getWorld().rand.nextFloat() < 0.001) || (event.getState().getBlock() == Blocks.LIT_REDSTONE_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == Blocks.REDSTONE_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == Blocks.QUARTZ_ORE && event.getWorld().rand.nextFloat() < 0.01) || (event.getState().getBlock() == BlocksTC.oreAmber && event.getWorld().rand.nextFloat() < 0.05) || (event.getState().getBlock() == BlocksTC.oreQuartz && event.getWorld().rand.nextFloat() < 0.05))) {
            event.getDrops().add(new ItemStack(ItemsTC.nuggets, 1, 10));
        }
        if (!event.getWorld().isRemote && event.getHarvester() != null) {
            ItemStack heldItem = event.getHarvester().getHeldItem(event.getHarvester().getActiveHand());
            if (heldItem != null && !heldItem.isEmpty()) {
                List<EnumInfusionEnchantment> list = EnumInfusionEnchantment.getInfusionEnchantments(heldItem);
                if (event.isSilkTouching() || ForgeHooks.isToolEffective(event.getWorld(), event.getPos(), heldItem) || (heldItem.getItem() instanceof ItemTool && heldItem.getItem().getDestroySpeed(heldItem, event.getState()) > 1.0f)) {
                    if (list.contains(EnumInfusionEnchantment.REFINING)) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:212

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 208

```java
                        if (b) {
                            event.getWorld().playSound(null, event.getPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.2f, 0.7f + event.getWorld().rand.nextFloat() * 0.2f);
                        }
                    }
                    if (!ToolEvents.blockDestructiveRecursion && list.contains(EnumInfusionEnchantment.DESTRUCTIVE) && !event.getHarvester().isSneaking()) {
                        ToolEvents.blockDestructiveRecursion = true;
                        EnumFacing face = ToolEvents.lastFaceClicked.get(event.getHarvester().getEntityId());
                        if (face == null) {
                            face = EnumFacing.getDirectionFromEntityLiving(event.getPos(), event.getHarvester());
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:214

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 210

```java
                        }
                    }
                    if (!ToolEvents.blockDestructiveRecursion && list.contains(EnumInfusionEnchantment.DESTRUCTIVE) && !event.getHarvester().isSneaking()) {
                        ToolEvents.blockDestructiveRecursion = true;
                        EnumFacing face = ToolEvents.lastFaceClicked.get(event.getHarvester().getEntityId());
                        if (face == null) {
                            face = EnumFacing.getDirectionFromEntityLiving(event.getPos(), event.getHarvester());
                        }
                        for (int aa = -1; aa <= 1; ++aa) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:216

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 212

```java
                    if (!ToolEvents.blockDestructiveRecursion && list.contains(EnumInfusionEnchantment.DESTRUCTIVE) && !event.getHarvester().isSneaking()) {
                        ToolEvents.blockDestructiveRecursion = true;
                        EnumFacing face = ToolEvents.lastFaceClicked.get(event.getHarvester().getEntityId());
                        if (face == null) {
                            face = EnumFacing.getDirectionFromEntityLiving(event.getPos(), event.getHarvester());
                        }
                        for (int aa = -1; aa <= 1; ++aa) {
                            for (int bb = -1; bb <= 1; ++bb) {
                                if (aa != 0 || bb != 0) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:238

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 234

```java
                                        yy = bb;
                                    }
                                    IBlockState bl = event.getWorld().getBlockState(event.getPos().add(xx, yy, zz));
                                    if (bl.getBlockHardness(event.getWorld(), event.getPos().add(xx, yy, zz)) >= 0.0f && (ForgeHooks.isToolEffective(event.getWorld(), event.getPos().add(xx, yy, zz), heldItem) || (heldItem.getItem() instanceof ItemTool && heldItem.getItem().getDestroySpeed(heldItem, bl) > 1.0f))) {
                                        if (event.getHarvester().getName().equals("FakeThaumcraftBore")) {
                                            EntityPlayer harvester = event.getHarvester();
                                            ++harvester.xpCooldown;
                                        }
                                        else {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:239

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 235

```java
                                    }
                                    IBlockState bl = event.getWorld().getBlockState(event.getPos().add(xx, yy, zz));
                                    if (bl.getBlockHardness(event.getWorld(), event.getPos().add(xx, yy, zz)) >= 0.0f && (ForgeHooks.isToolEffective(event.getWorld(), event.getPos().add(xx, yy, zz), heldItem) || (heldItem.getItem() instanceof ItemTool && heldItem.getItem().getDestroySpeed(heldItem, bl) > 1.0f))) {
                                        if (event.getHarvester().getName().equals("FakeThaumcraftBore")) {
                                            EntityPlayer harvester = event.getHarvester();
                                            ++harvester.xpCooldown;
                                        }
                                        else {
                                            heldItem.damageItem(1, event.getHarvester());
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:240

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 236

```java
                                    IBlockState bl = event.getWorld().getBlockState(event.getPos().add(xx, yy, zz));
                                    if (bl.getBlockHardness(event.getWorld(), event.getPos().add(xx, yy, zz)) >= 0.0f && (ForgeHooks.isToolEffective(event.getWorld(), event.getPos().add(xx, yy, zz), heldItem) || (heldItem.getItem() instanceof ItemTool && heldItem.getItem().getDestroySpeed(heldItem, bl) > 1.0f))) {
                                        if (event.getHarvester().getName().equals("FakeThaumcraftBore")) {
                                            EntityPlayer harvester = event.getHarvester();
                                            ++harvester.xpCooldown;
                                        }
                                        else {
                                            heldItem.damageItem(1, event.getHarvester());
                                        }
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:243

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 239

```java
                                            EntityPlayer harvester = event.getHarvester();
                                            ++harvester.xpCooldown;
                                        }
                                        else {
                                            heldItem.damageItem(1, event.getHarvester());
                                        }
                                        BlockUtils.harvestBlock(event.getWorld(), event.getHarvester(), event.getPos().add(xx, yy, zz));
                                    }
                                }
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:245

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 241

```java
                                        }
                                        else {
                                            heldItem.damageItem(1, event.getHarvester());
                                        }
                                        BlockUtils.harvestBlock(event.getWorld(), event.getHarvester(), event.getPos().add(xx, yy, zz));
                                    }
                                }
                            }
                        }
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:252

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 248

```java
                            }
                        }
                        ToolEvents.blockDestructiveRecursion = false;
                    }
                    if (list.contains(EnumInfusionEnchantment.COLLECTOR) && !event.getHarvester().isSneaking()) {
                        InventoryUtils.dropHarvestsAtPos(event.getWorld(), event.getPos(), event.getDrops(), true, 10, event.getHarvester());
                        event.getDrops().clear();
                    }
                    if (list.contains(EnumInfusionEnchantment.LAMPLIGHT) && !event.getHarvester().isSneaking() && event.getHarvester() instanceof EntityPlayerMP) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:253

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 249

```java
                        }
                        ToolEvents.blockDestructiveRecursion = false;
                    }
                    if (list.contains(EnumInfusionEnchantment.COLLECTOR) && !event.getHarvester().isSneaking()) {
                        InventoryUtils.dropHarvestsAtPos(event.getWorld(), event.getPos(), event.getDrops(), true, 10, event.getHarvester());
                        event.getDrops().clear();
                    }
                    if (list.contains(EnumInfusionEnchantment.LAMPLIGHT) && !event.getHarvester().isSneaking() && event.getHarvester() instanceof EntityPlayerMP) {
                        IThreadListener mainThread = ((EntityPlayerMP)event.getHarvester()).getServerWorld();
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:256

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 252

```java
                    if (list.contains(EnumInfusionEnchantment.COLLECTOR) && !event.getHarvester().isSneaking()) {
                        InventoryUtils.dropHarvestsAtPos(event.getWorld(), event.getPos(), event.getDrops(), true, 10, event.getHarvester());
                        event.getDrops().clear();
                    }
                    if (list.contains(EnumInfusionEnchantment.LAMPLIGHT) && !event.getHarvester().isSneaking() && event.getHarvester() instanceof EntityPlayerMP) {
                        IThreadListener mainThread = ((EntityPlayerMP)event.getHarvester()).getServerWorld();
                        mainThread.addScheduledTask(new Runnable() {
                            @Override
                            public void run() {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/events/ToolEvents.java:257

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 253

```java
                        InventoryUtils.dropHarvestsAtPos(event.getWorld(), event.getPos(), event.getDrops(), true, 10, event.getHarvester());
                        event.getDrops().clear();
                    }
                    if (list.contains(EnumInfusionEnchantment.LAMPLIGHT) && !event.getHarvester().isSneaking() && event.getHarvester() instanceof EntityPlayerMP) {
                        IThreadListener mainThread = ((EntityPlayerMP)event.getHarvester()).getServerWorld();
                        mainThread.addScheduledTask(new Runnable() {
                            @Override
                            public void run() {
                                if (event.getWorld().isAirBlock(event.getPos()) && event.getWorld().getBlockState(event.getPos()) != BlocksTC.effectGlimmer.getDefaultState() && event.getWorld().getLight(event.getPos()) < 10) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:61

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 57

```java
    private static boolean removeBlock(EntityPlayer player, BlockPos pos) {
        return removeBlock(player, pos, false);
    }

    private static boolean removeBlock(EntityPlayer player, BlockPos pos, boolean canHarvest) {
        IBlockState iblockstate = player.world.getBlockState(pos);
        boolean flag = iblockstate.getBlock().removedByPlayer(iblockstate, player.world, pos, player, canHarvest);
        if (flag) {
            try {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:63

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 59

```java
    }

    private static boolean removeBlock(EntityPlayer player, BlockPos pos, boolean canHarvest) {
        IBlockState iblockstate = player.world.getBlockState(pos);
        boolean flag = iblockstate.getBlock().removedByPlayer(iblockstate, player.world, pos, player, canHarvest);
        if (flag) {
            try {
                iblockstate.getBlock().onBlockDestroyedByPlayer(player.world, pos, iblockstate);
            }
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:73

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 69

```java
        }
        return flag;
    }

    public static boolean harvestBlockSkipCheck(World world, EntityPlayer player, BlockPos pos) {
        return harvestBlock(world, player, pos, false, false, 0, true);
    }

    public static boolean harvestBlock(World world, EntityPlayer player, BlockPos pos) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:74

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 70

```java
        return flag;
    }

    public static boolean harvestBlockSkipCheck(World world, EntityPlayer player, BlockPos pos) {
        return harvestBlock(world, player, pos, false, false, 0, true);
    }

    public static boolean harvestBlock(World world, EntityPlayer player, BlockPos pos) {
        return harvestBlock(world, player, pos, false, false, 0, false);
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:77

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 73

```java
    public static boolean harvestBlockSkipCheck(World world, EntityPlayer player, BlockPos pos) {
        return harvestBlock(world, player, pos, false, false, 0, true);
    }

    public static boolean harvestBlock(World world, EntityPlayer player, BlockPos pos) {
        return harvestBlock(world, player, pos, false, false, 0, false);
    }

    public static boolean harvestBlock(World world, EntityPlayer p, BlockPos pos, boolean alwaysDrop, boolean silkOverride, int fortuneOverride, boolean skipEvent) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:78

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 74

```java
        return harvestBlock(world, player, pos, false, false, 0, true);
    }

    public static boolean harvestBlock(World world, EntityPlayer player, BlockPos pos) {
        return harvestBlock(world, player, pos, false, false, 0, false);
    }

    public static boolean harvestBlock(World world, EntityPlayer p, BlockPos pos, boolean alwaysDrop, boolean silkOverride, int fortuneOverride, boolean skipEvent) {
        if (world.isRemote || !(p instanceof EntityPlayerMP)) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:81

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 77

```java
    public static boolean harvestBlock(World world, EntityPlayer player, BlockPos pos) {
        return harvestBlock(world, player, pos, false, false, 0, false);
    }

    public static boolean harvestBlock(World world, EntityPlayer p, BlockPos pos, boolean alwaysDrop, boolean silkOverride, int fortuneOverride, boolean skipEvent) {
        if (world.isRemote || !(p instanceof EntityPlayerMP)) {
            return false;
        }
        EntityPlayerMP player = (EntityPlayerMP)p;
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:105

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 101

```java
            player.interactionManager.player.connection.sendPacket(new SPacketBlockChange(world, pos));
        }
        else {
            ItemStack itemstack1 = player.getHeldItemMainhand();
            boolean flag2 = alwaysDrop || iblockstate.getBlock().canHarvestBlock(world, pos, player);
            flag1 = removeBlock(player, pos, flag2);
            if (flag1 && flag2) {
                ItemStack fakeStack = itemstack1.copy();
                if (silkOverride || fortuneOverride > EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FORTUNE, player)) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:123

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 119

```java
                        enchMap.put(Enchantments.FORTUNE, fort);
                    }
                    EnchantmentHelper.setEnchantments(enchMap, fakeStack);
                }
                iblockstate.getBlock().harvestBlock(world, player, pos, iblockstate, tileentity, fakeStack);
            }
        }
        if (!player.interactionManager.isCreative() && flag1 && exp > 0) {
            iblockstate.getBlock().dropXpOnBlockBreak(world, pos, exp);
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/utils/BlockUtils.java:183

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 179

```java
        BlockUtils.lastPos = new BlockPos(pos);
        BlockUtils.lastdistance = 0.0;
        int reach = Utils.isWoodLog(world, pos) ? 2 : 1;
        findBlocks(world, pos, block, reach);
        boolean worked = harvestBlockSkipCheck(world, player, BlockUtils.lastPos);
        world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), block, block, 3);
        if (worked && Utils.isWoodLog(world, pos)) {
            world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), block, block, 3);
            for (int xx = -3; xx <= 3; ++xx) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java:483

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 479

```java
        boolean t2 = !filter.igDmg && stack0.getItemDamage() != stack1.getItemDamage();
        return stack0.getItem() == stack1.getItem() && !t2 && t1;
    }

    public static void dropHarvestsAtPos(World worldIn, BlockPos pos, List<ItemStack> list) {
        dropHarvestsAtPos(worldIn, pos, list, false, 0, null);
    }

    public static void dropHarvestsAtPos(World worldIn, BlockPos pos, List<ItemStack> list, boolean followItem, int color, Entity target) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java:484

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 480

```java
        return stack0.getItem() == stack1.getItem() && !t2 && t1;
    }

    public static void dropHarvestsAtPos(World worldIn, BlockPos pos, List<ItemStack> list) {
        dropHarvestsAtPos(worldIn, pos, list, false, 0, null);
    }

    public static void dropHarvestsAtPos(World worldIn, BlockPos pos, List<ItemStack> list, boolean followItem, int color, Entity target) {
        for (ItemStack item : list) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/common/lib/utils/InventoryUtils.java:487

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 483

```java
    public static void dropHarvestsAtPos(World worldIn, BlockPos pos, List<ItemStack> list) {
        dropHarvestsAtPos(worldIn, pos, list, false, 0, null);
    }

    public static void dropHarvestsAtPos(World worldIn, BlockPos pos, List<ItemStack> list, boolean followItem, int color, Entity target) {
        for (ItemStack item : list) {
            if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops") && !worldIn.restoringBlockSnapshots) {
                float f = 0.5f;
                double d0 = worldIn.rand.nextFloat() * f + (1.0f - f) * 0.5;
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/proxies/CommonProxy.java:110

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 106

```java
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if (message.key.equals("portableHoleBlacklist") && message.isStringMessage()) {
                BlockUtils.portableHoleBlackList.add(message.getStringValue());
            }
            if (message.key.equals("harvestStandardCrop") && message.isItemStackMessage()) {
                ItemStack crop = message.getItemStackValue();
                CropUtils.addStandardCrop(crop, crop.getItemDamage());
            }
            if (message.key.equals("harvestClickableCrop") && message.isItemStackMessage()) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/proxies/CommonProxy.java:114

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 110

```java
            if (message.key.equals("harvestStandardCrop") && message.isItemStackMessage()) {
                ItemStack crop = message.getItemStackValue();
                CropUtils.addStandardCrop(crop, crop.getItemDamage());
            }
            if (message.key.equals("harvestClickableCrop") && message.isItemStackMessage()) {
                ItemStack crop = message.getItemStackValue();
                CropUtils.addClickableCrop(crop, crop.getItemDamage());
            }
            if (message.key.equals("harvestStackedCrop") && message.isItemStackMessage()) {
```

### thaumcraft:SealHarvest @ src/main/java/thaumcraft/proxies/CommonProxy.java:118

- Classification: SEAL_BOUNDARY
- Hit kind: TEXT_HIT
- Context start line: 114

```java
            if (message.key.equals("harvestClickableCrop") && message.isItemStackMessage()) {
                ItemStack crop = message.getItemStackValue();
                CropUtils.addClickableCrop(crop, crop.getItemDamage());
            }
            if (message.key.equals("harvestStackedCrop") && message.isItemStackMessage()) {
                ItemStack crop = message.getItemStackValue();
                CropUtils.addStackedCrop(crop, crop.getItemDamage());
            }
            if (message.key.equals("nativeCluster") && message.isStringMessage()) {
```

## Next implementation guidance

1. Do not implement all golemancy deferred references as recipes blindly.
2. Separate seal behavior placeholders from actual crafting/arcane recipes.
3. Treat `GolemPress`, `JarBrain`, and `MindBiothaumic` as block/item/machine boundaries until verified against exact source.
4. Choose one narrow family per batch, then re-run research recipe page gap audit.
