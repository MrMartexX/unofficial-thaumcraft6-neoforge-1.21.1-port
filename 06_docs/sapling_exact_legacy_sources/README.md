# Sapling exact legacy sources

Generated: 2026-05-05

Purpose: focused source bundle for implementing Thaumcraft Greatwood and Silverwood saplings in the NeoForge 1.21.1 port.

Main files to review:

1. legacy/src/main/java/thaumcraft/common/blocks/world/plants/BlockSaplingTC.java
2. legacy/src/main/java/thaumcraft/common/world/objects/WorldGenSilverwoodTrees.java
3. legacy/src/main/java/thaumcraft/common/world/objects/WorldGenGreatwoodTrees.java
4. current/src/main/java/thaumcraft/common/registry/TCBlocks.java
5. current/src/main/java/thaumcraft/common/blocks/world/plants/TCPlantBlock.java

Known implementation facts:

- Legacy saplings use BlockSaplingTC.
- Saplings have STAGE property with values 0 and 1.
- Random growth requires light above sapling >= 9 and chance 1/7.
- First growth changes stage 0 to 1.
- Second growth attempts tree generation.
- Bonemeal can be applied, but succeeds with chance 25%.
- Greatwood requires 2x2 Greatwood saplings.
- Silverwood uses single sapling generation.
- Greatwood generator used from sapling is WorldGenGreatwoodTrees(true, false).
- Silverwood generator used from sapling is WorldGenSilverwoodTrees(true, 7, 4).

Copied files:

- current-explicit: current/src/main/java/thaumcraft/client/TCColorHandlers.java
- current-name-match: current/src/main/java/thaumcraft/common/blocks/world/plants/TCPlantBlock.java
- current-explicit: current/src/main/java/thaumcraft/common/lib/fx/TCFXDispatcher.java
- current-explicit: current/src/main/java/thaumcraft/common/registry/TCBlocks.java
- current-explicit: current/src/main/java/thaumcraft/common/registry/TCItems.java
- current-resource: current/src/main/resources/assets/thaumcraft/blockstates/leaves_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/blockstates/leaves_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/blockstates/log_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/blockstates/log_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/blockstates/plank_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/blockstates/plank_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/blockstates/sapling_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/blockstates/sapling_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/blockstates/shimmerleaf.json
- current-resource: current/src/main/resources/assets/thaumcraft/blockstates/slab_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/blockstates/slab_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/blockstates/stairs_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/blockstates/stairs_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/greatwood_inner_stairs.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/greatwood_outer_stairs.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/greatwood_stairs.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/leaves_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/leaves_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/log_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/log_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/plank_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/plank_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/sapling_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/sapling_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/shimmerleaf.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/silverwood_inner_stairs.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/silverwood_outer_stairs.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/silverwood_stairs.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/slab_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/slab_greatwood_top.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/slab_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/block/slab_silverwood_top.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/item/leaves_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/item/leaves_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/item/log_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/item/log_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/item/plank_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/item/plank_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/item/sapling_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/item/sapling_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/item/shimmerleaf.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/item/slab_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/item/slab_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/item/stairs_greatwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/models/item/stairs_silverwood.json
- current-resource: current/src/main/resources/assets/thaumcraft/textures/block/leaves_greatwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/block/leaves_silverwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/block/log_greatwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/block/log_greatwood_top.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/block/log_silverwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/block/log_silverwood_top.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/block/plank_greatwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/block/plank_silverwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/block/sapling_greatwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/block/sapling_silverwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/block/shimmerleaf.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/blocks/leaves_greatwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/blocks/leaves_silverwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/blocks/log_greatwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/blocks/log_greatwood_top.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/blocks/log_silverwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/blocks/log_silverwood_top.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/blocks/plank_greatwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/blocks/plank_silverwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/blocks/plank_silverwood.png.mcmeta
- current-resource: current/src/main/resources/assets/thaumcraft/textures/blocks/plank_silverwood_ctm.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/blocks/sapling_greatwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/blocks/sapling_silverwood.png
- current-resource: current/src/main/resources/assets/thaumcraft/textures/blocks/shimmerleaf.png
- legacy-explicit: legacy/src/main/java/thaumcraft/api/blocks/BlocksTC.java
- legacy-explicit: legacy/src/main/java/thaumcraft/common/blocks/world/plants/BlockLeavesTC.java
- legacy-name-match: legacy/src/main/java/thaumcraft/common/blocks/world/plants/BlockLogsTC.java
- legacy-explicit: legacy/src/main/java/thaumcraft/common/blocks/world/plants/BlockSaplingTC.java
- legacy-explicit: legacy/src/main/java/thaumcraft/common/config/ConfigBlocks.java
- legacy-explicit: legacy/src/main/java/thaumcraft/common/world/biomes/BiomeGenMagicalForest.java
- legacy-explicit: legacy/src/main/java/thaumcraft/common/world/biomes/BiomeHandler.java
- legacy-name-match: legacy/src/main/java/thaumcraft/common/world/objects/WorldGenBigMagicTree.java
- legacy-name-match: legacy/src/main/java/thaumcraft/common/world/objects/WorldGenGreatwoodTrees.java
- legacy-name-match: legacy/src/main/java/thaumcraft/common/world/objects/WorldGenSilverwoodTrees.java
- legacy-explicit: legacy/src/main/java/thaumcraft/common/world/ThaumcraftWorldGenerator.java
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/leaves_greatwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/leaves_silverwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/log_greatwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/log_silverwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/plank_greatwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/plank_silverwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/sapling_greatwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/sapling_silverwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/shimmerleaf.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/slab_double_greatwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/slab_double_silverwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/slab_greatwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/slab_silverwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/stairs_greatwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/blockstates/stairs_silverwood.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/models/block/greatwood_inner_stairs.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/models/block/greatwood_outer_stairs.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/models/block/greatwood_stairs.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/models/block/silverwood_inner_stairs.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/models/block/silverwood_outer_stairs.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/models/block/silverwood_stairs.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/models/item/greatwood_stairs.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/models/item/silverwood_stairs.json
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/textures/blocks/leaves_greatwood.png
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/textures/blocks/leaves_silverwood.png
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/textures/blocks/log_greatwood.png
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/textures/blocks/log_greatwood_top.png
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/textures/blocks/log_silverwood.png
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/textures/blocks/log_silverwood_top.png
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/textures/blocks/plank_greatwood.png
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/textures/blocks/plank_silverwood.png
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/textures/blocks/plank_silverwood.png.mcmeta
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/textures/blocks/plank_silverwood_ctm.png
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/textures/blocks/sapling_greatwood.png
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/textures/blocks/sapling_silverwood.png
- legacy-resource: legacy/src/main/resources/assets/thaumcraft/textures/blocks/shimmerleaf.png