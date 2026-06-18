# Sapling and tree generation research

Generated: 2026-05-05

Purpose: collect all legacy and current code related to Greatwood and Silverwood saplings before implementing full sapling behavior in the NeoForge 1.21.1 port.

This document is research only. It should be used before writing the implementation patch.

## Required legacy behavior to verify

* Sapling block class and properties.
* Stage property values and transition logic.
* Random tick growth chance.
* Bonemeal acceptance and success chance.
* Light requirement.
* Greatwood 2x2 sapling detection and replacement during generation.
* Silverwood single sapling growth.
* Exact tree generator classes and generation algorithms.
* Blocks placed by each generator.
* Leaves/log metadata or state mapping.
* Any extra blocks generated around Silverwood, such as Shimmerleaf or other worldgen-specific blocks.
* Loot/drops and survival behavior.
* Current port registry names and resource files.

## Legacy code files matching sapling/tree keywords

* src/main/java/thaumcraft/api/blocks/BlocksTC.java
* src/main/java/thaumcraft/api/golems/GolemHelper.java
* src/main/java/thaumcraft/api/OreDictionaryEntries.java
* src/main/java/thaumcraft/api/ThaumcraftApi.java
* src/main/java/thaumcraft/client/ColorHandler.java
* src/main/java/thaumcraft/client/fx/FXDispatcher.java
* src/main/java/thaumcraft/client/fx/other/FXBoreStream.java
* src/main/java/thaumcraft/client/fx/other/FXEssentiaStream.java
* src/main/java/thaumcraft/client/fx/other/FXVoidStream.java
* src/main/java/thaumcraft/client/lib/events/RenderEventHandler.java
* src/main/java/thaumcraft/client/renderers/block/CrystalModel.java
* src/main/java/thaumcraft/common/blocks/basic/BlockPavingStone.java
* src/main/java/thaumcraft/common/blocks/devices/BlockLamp.java
* src/main/java/thaumcraft/common/blocks/world/ore/BlockCrystal.java
* src/main/java/thaumcraft/common/blocks/world/plants/BlockLeavesTC.java
* src/main/java/thaumcraft/common/blocks/world/plants/BlockLogsTC.java
* src/main/java/thaumcraft/common/blocks/world/plants/BlockPlantShimmerleaf.java
* src/main/java/thaumcraft/common/blocks/world/plants/BlockSaplingTC.java
* src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintFibre.java
* src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintLog.java
* src/main/java/thaumcraft/common/blocks/world/taint/TaintHelper.java
* src/main/java/thaumcraft/common/config/ConfigAspects.java
* src/main/java/thaumcraft/common/config/ConfigBlocks.java
* src/main/java/thaumcraft/common/config/ConfigEntities.java
* src/main/java/thaumcraft/common/config/ConfigRecipes.java
* src/main/java/thaumcraft/common/config/ConfigResearch.java
* src/main/java/thaumcraft/common/config/ModConfig.java
* src/main/java/thaumcraft/common/container/ContainerLogistics.java
* src/main/java/thaumcraft/common/container/ContainerPech.java
* src/main/java/thaumcraft/common/container/slot/SlotCraftingArcaneWorkbench.java
* src/main/java/thaumcraft/common/entities/ai/combat/AICultistHurtByTarget.java
* src/main/java/thaumcraft/common/entities/ai/pech/AIPechItemEntityGoto.java
* src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java
* src/main/java/thaumcraft/common/entities/construct/EntityTurretCrossbow.java
* src/main/java/thaumcraft/common/entities/construct/EntityTurretCrossbowAdvanced.java
* src/main/java/thaumcraft/common/entities/EntityFluxRift.java
* src/main/java/thaumcraft/common/entities/monster/cult/EntityCultistPortalLesser.java
* src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java
* src/main/java/thaumcraft/common/entities/monster/EntityInhabitedZombie.java
* src/main/java/thaumcraft/common/entities/monster/EntityPech.java
* src/main/java/thaumcraft/common/entities/monster/EntityWisp.java
* src/main/java/thaumcraft/common/entities/monster/tainted/EntityTaintCrawler.java
* src/main/java/thaumcraft/common/entities/projectile/EntityBottleTaint.java
* src/main/java/thaumcraft/common/entities/projectile/EntityEldritchOrb.java
* src/main/java/thaumcraft/common/entities/projectile/EntityRiftBlast.java
* src/main/java/thaumcraft/common/golems/ai/AINearestValidTarget.java
* src/main/java/thaumcraft/common/golems/EntityThaumcraftGolem.java
* src/main/java/thaumcraft/common/golems/GolemProperties.java
* src/main/java/thaumcraft/common/golems/seals/SealHarvest.java
* src/main/java/thaumcraft/common/items/casters/foci/FocusEffectFlux.java
* src/main/java/thaumcraft/common/items/casters/foci/FocusEffectHeal.java
* src/main/java/thaumcraft/common/items/tools/ItemElementalHoe.java
* src/main/java/thaumcraft/common/items/tools/ItemElementalSword.java
* src/main/java/thaumcraft/common/items/tools/ItemPrimalCrusher.java
* src/main/java/thaumcraft/common/lib/events/CraftingEvents.java
* src/main/java/thaumcraft/common/lib/events/ServerEvents.java
* src/main/java/thaumcraft/common/lib/events/ToolEvents.java
* src/main/java/thaumcraft/common/lib/events/WarpEvents.java
* src/main/java/thaumcraft/common/lib/potions/PotionInfectiousVisExhaust.java
* src/main/java/thaumcraft/common/lib/utils/CropUtils.java
* src/main/java/thaumcraft/common/lib/utils/EntityUtils.java
* src/main/java/thaumcraft/common/lib/utils/Utils.java
* src/main/java/thaumcraft/common/tiles/crafting/TileGolemBuilder.java
* src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java
* src/main/java/thaumcraft/common/tiles/devices/TileJarBrain.java
* src/main/java/thaumcraft/common/tiles/devices/TileLampFertility.java
* src/main/java/thaumcraft/common/tiles/devices/TileLampGrowth.java
* src/main/java/thaumcraft/common/tiles/devices/TileRechargePedestal.java
* src/main/java/thaumcraft/common/tiles/devices/TileStabilizer.java
* src/main/java/thaumcraft/common/tiles/misc/TileBarrierStone.java
* src/main/java/thaumcraft/common/world/biomes/BiomeGenMagicalForest.java
* src/main/java/thaumcraft/common/world/biomes/BiomeHandler.java
* src/main/java/thaumcraft/common/world/objects/WorldGenBigMagicTree.java
* src/main/java/thaumcraft/common/world/objects/WorldGenCustomFlowers.java
* src/main/java/thaumcraft/common/world/objects/WorldGenGreatwoodTrees.java
* src/main/java/thaumcraft/common/world/objects/WorldGenMound.java
* src/main/java/thaumcraft/common/world/objects/WorldGenSilverwoodTrees.java
* src/main/java/thaumcraft/common/world/ThaumcraftWorldGenerator.java
* src/main/java/thaumcraft/proxies/CommonProxy.java
* src/main/java/thaumcraft/proxies/ProxyBlock.java

## Current port code files matching sapling/tree keywords

* src/main/java/thaumcraft/client/TCColorHandlers.java
* src/main/java/thaumcraft/common/blocks/world/plants/TCPlantBlock.java
* src/main/java/thaumcraft/common/lib/fx/TCFXDispatcher.java
* src/main/java/thaumcraft/common/registry/TCBlocks.java
* src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java
* src/main/java/thaumcraft/common/registry/TCItems.java

## Legacy resource files matching tree block keywords

* src/main/resources/assets/thaumcraft/blockstates/leaves_greatwood.json
* src/main/resources/assets/thaumcraft/blockstates/leaves_silverwood.json
* src/main/resources/assets/thaumcraft/blockstates/log_greatwood.json
* src/main/resources/assets/thaumcraft/blockstates/log_silverwood.json
* src/main/resources/assets/thaumcraft/blockstates/plank_greatwood.json
* src/main/resources/assets/thaumcraft/blockstates/plank_silverwood.json
* src/main/resources/assets/thaumcraft/blockstates/sapling_greatwood.json
* src/main/resources/assets/thaumcraft/blockstates/sapling_silverwood.json
* src/main/resources/assets/thaumcraft/blockstates/shimmerleaf.json
* src/main/resources/assets/thaumcraft/blockstates/slab_double_greatwood.json
* src/main/resources/assets/thaumcraft/blockstates/slab_double_silverwood.json
* src/main/resources/assets/thaumcraft/blockstates/slab_greatwood.json
* src/main/resources/assets/thaumcraft/blockstates/slab_silverwood.json
* src/main/resources/assets/thaumcraft/blockstates/stairs_greatwood.json
* src/main/resources/assets/thaumcraft/blockstates/stairs_silverwood.json
* src/main/resources/assets/thaumcraft/lang/de_de.lang
* src/main/resources/assets/thaumcraft/lang/en_us.lang
* src/main/resources/assets/thaumcraft/lang/fr_fr.lang
* src/main/resources/assets/thaumcraft/lang/ja_jp.lang
* src/main/resources/assets/thaumcraft/lang/ko_kr.lang
* src/main/resources/assets/thaumcraft/lang/nl_NL.lang
* src/main/resources/assets/thaumcraft/lang/ru_ru.lang
* src/main/resources/assets/thaumcraft/lang/zh_cn.lang
* src/main/resources/assets/thaumcraft/lang/zh_tw.lang
* src/main/resources/assets/thaumcraft/models/block/greatwood_inner_stairs.json
* src/main/resources/assets/thaumcraft/models/block/greatwood_outer_stairs.json
* src/main/resources/assets/thaumcraft/models/block/greatwood_stairs.json
* src/main/resources/assets/thaumcraft/models/block/silverwood_inner_stairs.json
* src/main/resources/assets/thaumcraft/models/block/silverwood_outer_stairs.json
* src/main/resources/assets/thaumcraft/models/block/silverwood_stairs.json
* src/main/resources/assets/thaumcraft/models/item/greatwood_stairs.json
* src/main/resources/assets/thaumcraft/models/item/silverwood_stairs.json
* src/main/resources/assets/thaumcraft/research/basics.json
* src/main/resources/assets/thaumcraft/research/golemancy.json
* src/main/resources/assets/thaumcraft/textures/blocks/leaves_greatwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/leaves_silverwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/log_greatwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/log_greatwood_top.png
* src/main/resources/assets/thaumcraft/textures/blocks/log_silverwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/log_silverwood_top.png
* src/main/resources/assets/thaumcraft/textures/blocks/plank_greatwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/plank_silverwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/plank_silverwood.png.mcmeta
* src/main/resources/assets/thaumcraft/textures/blocks/plank_silverwood_ctm.png
* src/main/resources/assets/thaumcraft/textures/blocks/sapling_greatwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/sapling_silverwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/shimmerleaf.png

## Current resource files matching tree block keywords

* src/main/resources/assets/thaumcraft/blockstates/leaves_greatwood.json
* src/main/resources/assets/thaumcraft/blockstates/leaves_silverwood.json
* src/main/resources/assets/thaumcraft/blockstates/log_greatwood.json
* src/main/resources/assets/thaumcraft/blockstates/log_silverwood.json
* src/main/resources/assets/thaumcraft/blockstates/plank_greatwood.json
* src/main/resources/assets/thaumcraft/blockstates/plank_silverwood.json
* src/main/resources/assets/thaumcraft/blockstates/sapling_greatwood.json
* src/main/resources/assets/thaumcraft/blockstates/sapling_silverwood.json
* src/main/resources/assets/thaumcraft/blockstates/shimmerleaf.json
* src/main/resources/assets/thaumcraft/blockstates/slab_greatwood.json
* src/main/resources/assets/thaumcraft/blockstates/slab_silverwood.json
* src/main/resources/assets/thaumcraft/blockstates/stairs_greatwood.json
* src/main/resources/assets/thaumcraft/blockstates/stairs_silverwood.json
* src/main/resources/assets/thaumcraft/lang/en_us.json
* src/main/resources/assets/thaumcraft/models/block/greatwood_inner_stairs.json
* src/main/resources/assets/thaumcraft/models/block/greatwood_outer_stairs.json
* src/main/resources/assets/thaumcraft/models/block/greatwood_stairs.json
* src/main/resources/assets/thaumcraft/models/block/leaves_greatwood.json
* src/main/resources/assets/thaumcraft/models/block/leaves_silverwood.json
* src/main/resources/assets/thaumcraft/models/block/log_greatwood.json
* src/main/resources/assets/thaumcraft/models/block/log_silverwood.json
* src/main/resources/assets/thaumcraft/models/block/plank_greatwood.json
* src/main/resources/assets/thaumcraft/models/block/plank_silverwood.json
* src/main/resources/assets/thaumcraft/models/block/sapling_greatwood.json
* src/main/resources/assets/thaumcraft/models/block/sapling_silverwood.json
* src/main/resources/assets/thaumcraft/models/block/shimmerleaf.json
* src/main/resources/assets/thaumcraft/models/block/silverwood_inner_stairs.json
* src/main/resources/assets/thaumcraft/models/block/silverwood_outer_stairs.json
* src/main/resources/assets/thaumcraft/models/block/silverwood_stairs.json
* src/main/resources/assets/thaumcraft/models/block/slab_greatwood.json
* src/main/resources/assets/thaumcraft/models/block/slab_greatwood_top.json
* src/main/resources/assets/thaumcraft/models/block/slab_silverwood.json
* src/main/resources/assets/thaumcraft/models/block/slab_silverwood_top.json
* src/main/resources/assets/thaumcraft/models/item/leaves_greatwood.json
* src/main/resources/assets/thaumcraft/models/item/leaves_silverwood.json
* src/main/resources/assets/thaumcraft/models/item/log_greatwood.json
* src/main/resources/assets/thaumcraft/models/item/log_silverwood.json
* src/main/resources/assets/thaumcraft/models/item/plank_greatwood.json
* src/main/resources/assets/thaumcraft/models/item/plank_silverwood.json
* src/main/resources/assets/thaumcraft/models/item/sapling_greatwood.json
* src/main/resources/assets/thaumcraft/models/item/sapling_silverwood.json
* src/main/resources/assets/thaumcraft/models/item/shimmerleaf.json
* src/main/resources/assets/thaumcraft/models/item/slab_greatwood.json
* src/main/resources/assets/thaumcraft/models/item/slab_silverwood.json
* src/main/resources/assets/thaumcraft/models/item/stairs_greatwood.json
* src/main/resources/assets/thaumcraft/models/item/stairs_silverwood.json
* src/main/resources/assets/thaumcraft/textures/block/leaves_greatwood.png
* src/main/resources/assets/thaumcraft/textures/block/leaves_silverwood.png
* src/main/resources/assets/thaumcraft/textures/block/log_greatwood.png
* src/main/resources/assets/thaumcraft/textures/block/log_greatwood_top.png
* src/main/resources/assets/thaumcraft/textures/block/log_silverwood.png
* src/main/resources/assets/thaumcraft/textures/block/log_silverwood_top.png
* src/main/resources/assets/thaumcraft/textures/block/plank_greatwood.png
* src/main/resources/assets/thaumcraft/textures/block/plank_silverwood.png
* src/main/resources/assets/thaumcraft/textures/block/sapling_greatwood.png
* src/main/resources/assets/thaumcraft/textures/block/sapling_silverwood.png
* src/main/resources/assets/thaumcraft/textures/block/shimmerleaf.png
* src/main/resources/assets/thaumcraft/textures/blocks/leaves_greatwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/leaves_silverwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/log_greatwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/log_greatwood_top.png
* src/main/resources/assets/thaumcraft/textures/blocks/log_silverwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/log_silverwood_top.png
* src/main/resources/assets/thaumcraft/textures/blocks/plank_greatwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/plank_silverwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/plank_silverwood.png.mcmeta
* src/main/resources/assets/thaumcraft/textures/blocks/plank_silverwood_ctm.png
* src/main/resources/assets/thaumcraft/textures/blocks/sapling_greatwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/sapling_silverwood.png
* src/main/resources/assets/thaumcraft/textures/blocks/shimmerleaf.png

## Extracted legacy methods with sapling or tree logic

### src/main/java/thaumcraft/api/golems/GolemHelper.java

~~~java
public static AxisAlignedBB getBoundsForArea(ISealEntity seal) {
		return new AxisAlignedBB(
				seal.getSealPos().pos.getX(), seal.getSealPos().pos.getY(), seal.getSealPos().pos.getZ(), 
				seal.getSealPos().pos.getX()+1, seal.getSealPos().pos.getY()+1, seal.getSealPos().pos.getZ()+1)
				.offset(
					seal.getSealPos().face.getFrontOffsetX(), 
					seal.getSealPos().face.getFrontOffsetY(), 
					seal.getSealPos().face.getFrontOffsetZ())
				.expand(
					seal.getSealPos().face.getFrontOffsetX()!=0?(seal.getArea().getX()-1) * seal.getSealPos().face.getFrontOffsetX():0, 
					seal.getSealPos().face.getFrontOffsetY()!=0?(seal.getArea().getY()-1) * seal.getSealPos().face.getFrontOffsetY():0, 
					seal.getSealPos().face.getFrontOffsetZ()!=0?(seal.getArea().getZ()-1) * seal.getSealPos().face.getFrontOffsetZ():0)
				.grow(
					seal.getSealPos().face.getFrontOffsetX()==0?seal.getArea().getX()-1:0,
					seal.getSealPos().face.getFrontOffsetY()==0?seal.getArea().getY()-1:0,
					seal.getSealPos().face.getFrontOffsetZ()==0?seal.getArea().getZ()-1:0 );
	}
~~~

### src/main/java/thaumcraft/api/OreDictionaryEntries.java

~~~java
public static void initializeOreDictionary() {
		OreDictionary.registerOre("oreAmber", new ItemStack(BlocksTC.oreAmber));
		OreDictionary.registerOre("oreCinnabar", new ItemStack(BlocksTC.oreCinnabar));
		OreDictionary.registerOre("oreQuartz", new ItemStack(BlocksTC.oreQuartz));
		
		OreDictionary.registerOre("oreCrystalAir", new ItemStack(BlocksTC.crystalAir,1,OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("oreCrystalEarth", new ItemStack(BlocksTC.crystalEarth,1,OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("oreCrystalWater", new ItemStack(BlocksTC.crystalWater,1,OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("oreCrystalFire", new ItemStack(BlocksTC.crystalFire,1,OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("oreCrystalOrder", new ItemStack(BlocksTC.crystalOrder,1,OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("oreCrystalEntropy", new ItemStack(BlocksTC.crystalEntropy,1,OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("oreCrystalTaint", new ItemStack(BlocksTC.crystalTaint,1,OreDictionary.WILDCARD_VALUE));
		
		OreDictionary.registerOre("logWood", new ItemStack(BlocksTC.logGreatwood));
		OreDictionary.registerOre("logWood", new ItemStack(BlocksTC.logSilverwood));
		OreDictionary.registerOre("plankWood", new ItemStack(BlocksTC.plankGreatwood));
		OreDictionary.registerOre("plankWood", new ItemStack(BlocksTC.plankSilverwood));
		OreDictionary.registerOre("slabWood", new ItemStack(BlocksTC.slabGreatwood));
		OreDictionary.registerOre("slabWood", new ItemStack(BlocksTC.slabSilverwood));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlocksTC.saplingGreatwood));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlocksTC.saplingSilverwood));		
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlocksTC.leafGreatwood, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlocksTC.leafSilverwood, 1, OreDictionary.WILDCARD_VALUE));
				
		for (Block b:BlocksTC.nitor.values())
			OreDictionary.registerOre("nitor", new ItemStack(b));
		
		OreDictionary.registerOre("gemAmber", new ItemStack(ItemsTC.amber));
		OreDictionary.registerOre("quicksilver", new ItemStack(ItemsTC.quicksilver));
		
		OreDictionary.registerOre("nuggetIron", new ItemStack(ItemsTC.nuggets,1,0));
		OreDictionary.registerOre("nuggetCopper", new ItemStack(ItemsTC.nuggets,1,1));
		OreDictionary.registerOre("nuggetTin", new ItemStack(ItemsTC.nuggets,1,2));
		OreDictionary.registerOre("nuggetSilver", new ItemStack(ItemsTC.nuggets,1,3));
		OreDictionary.registerOre("nuggetLead", new ItemStack(ItemsTC.nuggets,1,4));
		OreDictionary.registerOre("nuggetQuicksilver", new ItemStack(ItemsTC.nuggets,1,5));
		OreDictionary.registerOre("nuggetThaumium", new ItemStack(ItemsTC.nuggets,1,6));
		OreDictionary.registerOre("nuggetVoid", new ItemStack(ItemsTC.nuggets,1,7));
		OreDictionary.registerOre("nuggetBrass", new ItemStack(ItemsTC.nuggets,1,8));
		OreDictionary.registerOre("nuggetQuartz", new ItemStack(ItemsTC.nuggets,1,9));		
		
		OreDictionary.registerOre("nuggetMeat", new ItemStack(ItemsTC.chunks,1,0));
		OreDictionary.registerOre("nuggetMeat", new ItemStack(ItemsTC.chunks,1,1));
		OreDictionary.registerOre("nuggetMeat", new ItemStack(ItemsTC.chunks,1,2));
		OreDictionary.registerOre("nuggetMeat", new ItemStack(ItemsTC.chunks,1,3));
		OreDictionary.registerOre("nuggetMeat", new ItemStack(ItemsTC.chunks,1,4));
		OreDictionary.registerOre("nuggetMeat", new ItemStack(ItemsTC.chunks,1,5));
		
		OreDictionary.registerOre("ingotThaumium", new ItemStack(ItemsTC.ingots,1,0));
		OreDictionary.registerOre("ingotVoid", new ItemStack(ItemsTC.ingots,1,1));
		OreDictionary.registerOre("ingotBrass", new ItemStack(ItemsTC.ingots,1,2));
		
		OreDictionary.registerOre("blockThaumium", new ItemStack(BlocksTC.metalBlockThaumium));
		OreDictionary.registerOre("blockVoid", new ItemStack(BlocksTC.metalBlockVoid));
		OreDictionary.registerOre("blockBrass", new ItemStack(BlocksTC.metalBlockBrass));
		
		OreDictionary.registerOre("plateIron", new ItemStack(ItemsTC.plate,1,1));
		OreDictionary.registerOre("plateBrass", new ItemStack(ItemsTC.plate,1,0));
		OreDictionary.registerOre("plateThaumium", new ItemStack(ItemsTC.plate,1,2));
		OreDictionary.registerOre("plateVoid", new ItemStack(ItemsTC.plate,1,3));
				
		OreDictionary.registerOre("clusterIron", new ItemStack(ItemsTC.clusters,1,0));
		OreDictionary.registerOre("clusterGold", new ItemStack(ItemsTC.clusters,1,1));	
		OreDictionary.registerOre("clusterCopper", new ItemStack(ItemsTC.clusters,1,2));
		OreDictionary.registerOre("clusterTin", new ItemStack(ItemsTC.clusters,1,3));
		OreDictionary.registerOre("clusterSilver", new ItemStack(ItemsTC.clusters,1,4));
		OreDictionary.registerOre("clusterLead", new ItemStack(ItemsTC.clusters,1,5));
		OreDictionary.registerOre("clusterCinnabar", new ItemStack(ItemsTC.clusters,1,6));
		OreDictionary.registerOre("clusterQuartz", new ItemStack(ItemsTC.clusters,1,7));
		
		// for mod recipe compatibility
		OreDictionary.registerOre("trapdoorWood", new ItemStack(Blocks.TRAPDOOR));
	
	}
~~~

### src/main/java/thaumcraft/client/ColorHandler.java

~~~java
private static void registerBlockColourHandlers(BlockColors blockColors) {
        IBlockColor basicColourHandler = (state, blockAccess, pos, tintIndex) -> state.getBlock().getMapColor(state, blockAccess, pos).colorValue;
        Block[] basicBlocks = new Block[BlocksTC.candles.size() + BlocksTC.banners.size() + BlocksTC.nitor.size()];
        int i = 0;
        for (Block b : BlocksTC.candles.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        for (Block b : BlocksTC.banners.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        for (Block b : BlocksTC.nitor.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        blockColors.registerBlockColorHandler(basicColourHandler, basicBlocks);
        IBlockColor grassColourHandler = (state, blockAccess, pos, tintIndex) -> (blockAccess != null && pos != null) ? BiomeColorHelper.getGrassColorAtPos(blockAccess, pos) : ColorizerGrass.getGrassColor(0.5, 1.0);
        blockColors.registerBlockColorHandler(grassColourHandler, BlocksTC.grassAmbient);
        IBlockColor leafColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() == BlocksTC.leafSilverwood) {
                return 16777215;
            }
            if (blockAccess != null && pos != null) {
                return BiomeColorHelper.getFoliageColorAtPos(blockAccess, pos);
            }
            return ColorizerFoliage.getFoliageColorBasic();
        };
        blockColors.registerBlockColorHandler(leafColourHandler, BlocksTC.leafGreatwood, BlocksTC.leafSilverwood);
        IBlockColor crystalColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockCrystal) {
                return ((BlockCrystal)state.getBlock()).aspect.getColor();
            }
            return 16777215;
        };
        blockColors.registerBlockColorHandler(crystalColourHandler, BlocksTC.crystalAir, BlocksTC.crystalEarth, BlocksTC.crystalFire, BlocksTC.crystalWater, BlocksTC.crystalEntropy, BlocksTC.crystalOrder, BlocksTC.crystalTaint);
        IBlockColor tubeFilterColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockTube && tintIndex == 1) {
                TileEntity te = blockAccess.getTileEntity(pos);
                if (te != null && te instanceof TileTubeFilter && ((TileTubeFilter)te).aspectFilter != null) {
                    return ((TileTubeFilter)te).aspectFilter.getColor();
                }
            }
            return 16777215;
        };
        blockColors.registerBlockColorHandler(tubeFilterColourHandler, BlocksTC.tubeFilter);
        IBlockColor inlayColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockInlay && tintIndex == 0) {
                BlockInlay blockInlay = (BlockInlay)state.getBlock();
                return BlockInlay.colorMultiplier(state.getBlock().getMetaFromState(state));
            }
            return 16777215;
        };
        blockColors.registerBlockColorHandler(inlayColourHandler, BlocksTC.inlay);
        IBlockColor stabilizerColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockStabilizer && tintIndex == 0) {
                int charge = 0;
                TileEntity te = blockAccess.getTileEntity(pos);
                if (te != null && te instanceof TileStabilizer) {
                    charge = ((TileStabilizer)te).getEnergy();
                }
                BlockStabilizer blockStabilizer = (BlockStabilizer)state.getBlock();
                return BlockStabilizer.colorMultiplier(charge);
            }
            return 16777215;
        };
        blockColors.registerBlockColorHandler(stabilizerColourHandler, BlocksTC.stabilizer);
    }
~~~

~~~java
private static void registerItemColourHandlers(BlockColors blockColors, ItemColors itemColors) {
        IItemColor itemBlockColourHandler = (stack, tintIndex) -> {
            IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
            return blockColors.colorMultiplier(state, null, null, tintIndex);
        };
        Block[] basicBlocks = new Block[BlocksTC.candles.size() + BlocksTC.nitor.size() + 3];
        int i = 0;
        for (Block b : BlocksTC.candles.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        for (Block b : BlocksTC.nitor.values()) {
            basicBlocks[i] = b;
            ++i;
        }
        basicBlocks[i] = BlocksTC.leafGreatwood;
        ++i;
        basicBlocks[i] = BlocksTC.leafSilverwood;
        ++i;
        basicBlocks[i] = BlocksTC.grassAmbient;
        ++i;
        itemColors.registerItemColorHandler(itemBlockColourHandler, basicBlocks);
        IItemColor itemEssentiaColourHandler = (stack, tintIndex) -> {
            ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer)stack.getItem();
            try {
                if (item != null && item.getAspects(stack) != null) {
                    return item.getAspects(stack).getAspects()[0].getColor();
                }
            }
            catch (Exception ex) {}
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemEssentiaColourHandler, ItemsTC.crystalEssence);
        IItemColor itemJarColourHandler = (stack, tintIndex) -> {
            BlockJarItem item = (BlockJarItem)stack.getItem();
            try {
                if (item.getAspects(stack) != null && tintIndex == 1) {
                    return item.getAspects(stack).getAspects()[0].getColor();
                }
            }
            catch (Exception ex) {}
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemJarColourHandler, BlocksTC.jarNormal);
        itemColors.registerItemColorHandler(itemJarColourHandler, BlocksTC.jarVoid);
        IItemColor itemCrystalPlanterColourHandler = (stack, tintIndex) -> {
            Item item = stack.getItem();
            if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() instanceof BlockCrystal) {
                return ((BlockCrystal)((ItemBlock)item).getBlock()).aspect.getColor();
            }
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalAir);
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalEarth);
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalFire);
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalWater);
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalEntropy);
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalOrder);
        itemColors.registerItemColorHandler(itemCrystalPlanterColourHandler, BlocksTC.crystalTaint);
        IItemColor itemEssentiaAltColourHandler = (stack, tintIndex) -> {
            ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer)stack.getItem();
            if (stack.getItemDamage() == 1 && item.getAspects(stack) != null && tintIndex == 1) {
                return item.getAspects(stack).getAspects()[0].getColor();
            }
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemEssentiaAltColourHandler, ItemsTC.phial, ItemsTC.label);
        IItemColor itemArmorColourHandler = (stack, tintIndex) -> {
            ItemArmor item = (ItemArmor)stack.getItem();
            return (tintIndex > 0) ? -1 : item.getColor(stack);
        };
        itemColors.registerItemColorHandler(itemArmorColourHandler, ItemsTC.voidRobeChest, ItemsTC.voidRobeHelm, ItemsTC.voidRobeLegs, ItemsTC.clothChest, ItemsTC.clothLegs, ItemsTC.clothBoots);
        IItemColor itemCasterColourHandler = (stack, tintIndex) -> {
            ItemCaster item = (ItemCaster)stack.getItem();
            ItemFocus focus = item.getFocus(stack);
            return (tintIndex > 0 && focus != null) ? focus.getFocusColor(item.getFocusStack(stack)) : -1;
        };
        itemColors.registerItemColorHandler(itemCasterColourHandler, ItemsTC.casterBasic);
        IItemColor itemFocusColourHandler = (stack, tintIndex) -> {
            ItemFocus item = (ItemFocus)stack.getItem();
            int color = item.getFocusColor(stack);
            return color;
        };
        itemColors.registerItemColorHandler(itemFocusColourHandler, ItemsTC.focus1);
        itemColors.registerItemColorHandler(itemFocusColourHandler, ItemsTC.focus2);
        itemColors.registerItemColorHandler(itemFocusColourHandler, ItemsTC.focus3);
        IItemColor itemGolemColourHandler = (stack, tintIndex) -> {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("props")) {
                IGolemProperties props = GolemProperties.fromLong(stack.getTagCompound().getLong("props"));
                return props.getMaterial().itemColor;
            }
            return 16777215;
        };
        itemColors.registerItemColorHandler(itemGolemColourHandler, ItemsTC.golemPlacer);
        IItemColor itemBannerColourHandler = (stack, tintIndex) -> {
            if (tintIndex == 1) {
                IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
                return blockColors.colorMultiplier(state, null, null, tintIndex);
            }
            if (tintIndex != 2) {
                return 16777215;
            }
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("aspect") && stack.getTagCompound().getString("aspect") != null) {
                return Aspect.getAspect(stack.getTagCompound().getString("aspect")).getColor();
            }
            IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
            return blockColors.colorMultiplier(state, null, null, tintIndex);
        };
        Block[] bannerBlocks = new Block[BlocksTC.banners.size()];
        i = 0;
        for (Block b2 : BlocksTC.banners.values()) {
            bannerBlocks[i] = b2;
            ++i;
        }
        itemColors.registerItemColorHandler(itemBannerColourHandler, bannerBlocks);
    }
~~~

### src/main/java/thaumcraft/client/fx/FXDispatcher.java

~~~java
public void drawBlockSparkles(BlockPos p, Vec3d start) {
        AxisAlignedBB bs = getWorld().getBlockState(p).getBoundingBox(getWorld(), p);
        bs.grow(0.1, 0.1, 0.1);
        int num = (int)(bs.getAverageEdgeLength() * 20.0);
        for (EnumFacing face : EnumFacing.values()) {
            IBlockState state = getWorld().getBlockState(p.offset(face));
            if (!state.isOpaqueCube()) {
                if (!state.isSideSolid(getWorld(), p.offset(face), face.getOpposite())) {
                    boolean rx = face.getFrontOffsetX() == 0;
                    boolean ry = face.getFrontOffsetY() == 0;
                    boolean rz = face.getFrontOffsetZ() == 0;
                    double mx = 0.5 + face.getFrontOffsetX() * 0.51;
                    double my = 0.5 + face.getFrontOffsetY() * 0.51;
                    double mz = 0.5 + face.getFrontOffsetZ() * 0.51;
                    for (int a = 0; a < num * 2; ++a) {
                        double x = mx;
                        double y = my;
                        double z = mz;
                        if (rx) {
                            x += getWorld().rand.nextGaussian() * 0.6;
                        }
                        if (ry) {
                            y += getWorld().rand.nextGaussian() * 0.6;
                        }
                        if (rz) {
                            z += getWorld().rand.nextGaussian() * 0.6;
                        }
                        x = MathHelper.clamp(x, bs.minX, bs.maxX);
                        y = MathHelper.clamp(y, bs.minY, bs.maxY);
                        z = MathHelper.clamp(z, bs.minZ, bs.maxZ);
                        float r = MathHelper.getInt(getWorld().rand, 255, 255) / 255.0f;
                        float g = MathHelper.getInt(getWorld().rand, 189, 255) / 255.0f;
                        float b = MathHelper.getInt(getWorld().rand, 64, 255) / 255.0f;
                        Vec3d v1 = new Vec3d(p.getX() + x, p.getY() + y, p.getZ() + z);
                        double delay = getWorld().rand.nextInt(5) + v1.distanceTo(start) * 16.0;
                        drawSimpleSparkle(getWorld().rand, p.getX() + x, p.getY() + y, p.getZ() + z, 0.0, 0.0025, 0.0, 0.4f + (float) getWorld().rand.nextGaussian() * 0.1f, r, g, b, (int)delay, 1.0f, 0.01f, 16);
                    }
                }
            }
        }
    }
~~~

### src/main/java/thaumcraft/client/fx/other/FXBoreStream.java

~~~java
public FXBoreStream(World w, double par2, double par4, double par6, Entity target, int count, int color, float scale, int extend, double my) {
        super(w, par2, par4, par6, 0.0, 0.0, 0.0);
        this.count = 0;
        length = 5;
        key = "";
        startPos = null;
        gle = new CoreGLE();
        layer = 1;
        growing = -1;
        vecs = new ArrayList<Quat>();
        particleScale = (float)(scale * (1.0 + rand.nextGaussian() * 0.15000000596046448));
        length = Math.max(5, extend);
        this.count = count;
        this.target = target;
        particleMaxAge = length * 10;
        motionX = MathHelper.sin(count / 4.0f) * 0.15f;
        motionY = my + MathHelper.sin(count / 3.0f) * 0.15f;
        motionZ = MathHelper.sin(count / 2.0f) * 0.15f;
        Color c = new Color(color);
        particleRed = c.getRed() / 255.0f;
        particleGreen = c.getGreen() / 255.0f;
        particleBlue = c.getBlue() / 255.0f;
        particleGravity = 0.2f;
        vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        startX = posX;
        startY = posY;
        startZ = posZ;
        startPos = new BlockPos(startX, startY, startZ);
    }
~~~

~~~java
public void renderParticle(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        double ePX = startX - FXBoreStream.interpPosX;
        double ePY = startY - FXBoreStream.interpPosY;
        double ePZ = startZ - FXBoreStream.interpPosZ;
        GL11.glTranslated(ePX, ePY, ePZ);
        if (points != null && points.length > 2) {
            Minecraft.getMinecraft().renderEngine.bindTexture(FXBoreStream.TEX0);
            gle.set_POLYCYL_TESS(8);
            gle.set__ROUND_TESS_PIECES(1);
            gle.gleSetJoinStyle(1042);
            gle.glePolyCone(points.length, points, colours, radii, 0.075f, (growing < 0) ? 0.0f : (0.075f * (particleAge - growing + f)));
        }
        GL11.glPopMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(ParticleEngine.particleTexture);
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
~~~

~~~java
public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge || length < 1) {
            setExpired();
            return;
        }
        motionY += 0.01 * particleGravity;
        move(motionX, motionY, motionZ);
        motionX *= 0.985;
        motionY *= 0.985;
        motionZ *= 0.985;
        double dx = target.posX - posX;
        double dy = target.posY + target.getEyeHeight() - posY;
        double dz = target.posZ - posZ;
        double d11 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        double clamp = d11 / 10.0;
        motionX = MathHelper.clamp((float) motionX, -clamp, clamp);
        motionY = MathHelper.clamp((float) motionY, -clamp, clamp);
        motionZ = MathHelper.clamp((float) motionZ, -clamp, clamp);
        dx /= d11;
        dy /= d11;
        dz /= d11;
        motionX += dx * (clamp / Math.min(1.0, d11));
        motionY += dy * (clamp / Math.min(1.0, d11));
        motionZ += dz * (clamp / Math.min(1.0, d11));
        float scale = particleScale * (0.75f + MathHelper.sin((count + particleAge) / 2.0f) * 0.25f);
        if (d11 < 1.0) {
            float f = MathHelper.sin((float)(d11 * 1.5707963267948966));
            scale *= f;
            particleScale *= f;
        }
        if (particleScale > 0.001) {
            vecs.add(new Quat(scale, posX - startX, posY - startY, posZ - startZ));
        }
        else {
            if (growing < 0) {
                growing = particleAge;
            }
            --length;
        }
        if (vecs.size() > length) {
            vecs.remove(0);
        }
        points = new double[vecs.size()][3];
        colours = new float[vecs.size()][4];
        radii = new double[vecs.size()];
        int c = vecs.size();
        for (Quat v : vecs) {
            --c;
            float variance = 1.0f + MathHelper.sin((c + particleAge) / 3.0f) * 0.2f;
            float xx = MathHelper.sin((c + particleAge) / 6.0f) * 0.03f;
            float yy = MathHelper.sin((c + particleAge) / 7.0f) * 0.03f;
            float zz = MathHelper.sin((c + particleAge) / 8.0f) * 0.03f;
            points[c][0] = v.x + xx;
            points[c][1] = v.y + yy;
            points[c][2] = v.z + zz;
            radii[c] = v.s * variance;
            if (c > vecs.size() - 10) {
                double[] radii = this.radii;
                int n = c;
                radii[n] *= MathHelper.cos((float)((c - (vecs.size() - 12)) / 10.0f * 1.5707963267948966));
            }
            if (c == 0) {
                radii[c] = 0.0;
            }
            else if (c == 1) {
                radii[c] = 0.0;
            }
            else if (c == 2) {
                radii[c] = (particleScale * 0.5 + radii[c]) / 2.0;
            }
            else if (c == 3) {
                radii[c] = (particleScale + radii[c]) / 2.0;
            }
            else if (c == 4) {
                radii[c] = (particleScale + radii[c] * 2.0) / 3.0;
            }
            float v2 = 1.0f - MathHelper.sin((c + particleAge) / 2.0f) * 0.1f;
            colours[c][0] = particleRed * v2;
            colours[c][1] = particleGreen * v2;
            colours[c][2] = particleBlue * v2;
            colours[c][3] = 1.0f;
        }
    }
~~~

### src/main/java/thaumcraft/client/fx/other/FXEssentiaStream.java

~~~java
public FXEssentiaStream(World w, double par2, double par4, double par6, double tx, double ty, double tz, int count, int color, float scale, int extend, double my) {
        super(w, par2, par4, par6, 0.0, 0.0, 0.0);
        this.count = 0;
        length = 20;
        key = "";
        startPos = null;
        endPos = null;
        gle = new CoreGLE();
        layer = 1;
        growing = -1;
        vecs = new ArrayList<Quat>();
        particleScale = (float)(scale * (1.0 + rand.nextGaussian() * 0.15000000596046448));
        length = Math.max(20, extend);
        this.count = count;
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        BlockPos bp1 = new BlockPos(posX, posY, posZ);
        BlockPos bp2 = new BlockPos(targetX, targetY, targetZ);
        IBlockState bs = w.getBlockState(bp1);
        if (bs.getBlock() instanceof BlockEssentiaTransport) {
            EnumFacing f = BlockStateUtils.getFacing(bs);
            posX += f.getFrontOffsetX() * 0.05f;
            posY += f.getFrontOffsetY() * 0.05f;
            posZ += f.getFrontOffsetZ() * 0.05f;
        }
        double dx = tx - posX;
        double dy = ty - posY;
        double dz = tz - posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 21.0f);
        if (base < 1) {
            base = 1;
        }
        particleMaxAge = base;
        String k = bp1.toLong() + "" + bp2.toLong() + "" + color;
        if (FXEssentiaStream.pt.containsKey(k)) {
            FXEssentiaStream trail2 = FXEssentiaStream.pt.get(k);
            if (!trail2.isExpired && trail2.vecs.size() < trail2.length) {
                FXEssentiaStream fxEssentiaStream = trail2;
                fxEssentiaStream.length += Math.max(extend, 5);
                FXEssentiaStream fxEssentiaStream2 = trail2;
                fxEssentiaStream2.particleMaxAge += Math.max(extend, 5);
                particleMaxAge = 0;
            }
        }
        if (particleMaxAge > 0) {
            FXEssentiaStream.pt.put(k, this);
            key = k;
        }
        motionX = MathHelper.sin(count / 4.0f) * 0.015f;
        motionY = my + MathHelper.sin(count / 3.0f) * 0.015f;
        motionZ = MathHelper.sin(count / 2.0f) * 0.015f;
        Color c = new Color(color);
        particleRed = c.getRed() / 255.0f;
        particleGreen = c.getGreen() / 255.0f;
        particleBlue = c.getBlue() / 255.0f;
        particleGravity = 0.2f;
        vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        startX = posX;
        startY = posY;
        startZ = posZ;
        startPos = new BlockPos(startX, startY, startZ);
        endPos = bp2;
    }
~~~

~~~java
public void renderParticle(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        double ePX = startX - FXEssentiaStream.interpPosX;
        double ePY = startY - FXEssentiaStream.interpPosY;
        double ePZ = startZ - FXEssentiaStream.interpPosZ;
        GL11.glTranslated(ePX, ePY, ePZ);
        if (points != null && points.length > 2) {
            Minecraft.getMinecraft().renderEngine.bindTexture(FXEssentiaStream.TEX0);
            gle.set_POLYCYL_TESS(8);
            gle.set__ROUND_TESS_PIECES(1);
            gle.gleSetJoinStyle(1042);
            gle.glePolyCone(points.length, points, colours, radii, 0.075f, (growing < 0) ? 0.0f : (0.075f * (particleAge - growing + f)));
        }
        GL11.glPopMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(ParticleEngine.particleTexture);
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
~~~

~~~java
public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge || length < 1) {
            setExpired();
            if (FXEssentiaStream.pt.containsKey(key) && FXEssentiaStream.pt.get(key).isExpired) {
                FXEssentiaStream.pt.remove(key);
            }
            return;
        }
        motionY += 0.01 * particleGravity;
        move(motionX, motionY, motionZ);
        motionX *= 0.985;
        motionY *= 0.985;
        motionZ *= 0.985;
        motionX = MathHelper.clamp((float) motionX, -0.05f, 0.05f);
        motionY = MathHelper.clamp((float) motionY, -0.05f, 0.05f);
        motionZ = MathHelper.clamp((float) motionZ, -0.05f, 0.05f);
        double dx = targetX - posX;
        double dy = targetY - posY;
        double dz = targetZ - posZ;
        double d13 = 0.01;
        double d14 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= d14;
        dy /= d14;
        dz /= d14;
        motionX += dx * (d13 / Math.min(1.0, d14));
        motionY += dy * (d13 / Math.min(1.0, d14));
        motionZ += dz * (d13 / Math.min(1.0, d14));
        float scale = particleScale * (0.75f + MathHelper.sin((count + particleAge) / 2.0f) * 0.25f);
        if (d14 < 1.0) {
            float f = MathHelper.sin((float)(d14 * 1.5707963267948966));
            scale *= f;
            particleScale *= f;
        }
        if (particleScale > 0.001) {
            vecs.add(new Quat(scale, posX - startX, posY - startY, posZ - startZ));
        }
        else {
            if (growing < 0) {
                growing = particleAge;
            }
            --length;
            FXDispatcher.INSTANCE.essentiaDropFx(targetX + rand.nextGaussian() * 0.07500000298023224, targetY + rand.nextGaussian() * 0.07500000298023224, targetZ + rand.nextGaussian() * 0.07500000298023224, particleRed, particleGreen, particleBlue, 0.5f);
        }
        if (vecs.size() > length) {
            vecs.remove(0);
        }
        points = new double[vecs.size()][3];
        colours = new float[vecs.size()][4];
        radii = new double[vecs.size()];
        int c = vecs.size();
        for (Quat v : vecs) {
            --c;
            float variance = 1.0f + MathHelper.sin((c + particleAge) / 3.0f) * 0.2f;
            float xx = MathHelper.sin((c + particleAge) / 6.0f) * 0.03f;
            float yy = MathHelper.sin((c + particleAge) / 7.0f) * 0.03f;
            float zz = MathHelper.sin((c + particleAge) / 8.0f) * 0.03f;
            points[c][0] = v.x + xx;
            points[c][1] = v.y + yy;
            points[c][2] = v.z + zz;
            radii[c] = v.s * variance;
            if (c > vecs.size() - 10) {
                double[] radii = this.radii;
                int n = c;
                radii[n] *= MathHelper.cos((float)((c - (vecs.size() - 12)) / 10.0f * 1.5707963267948966));
            }
            if (c == 0) {
                radii[c] = 0.0;
            }
            else if (c == 1) {
                radii[c] = 0.0;
            }
            else if (c == 2) {
                radii[c] = (particleScale * 0.5 + radii[c]) / 2.0;
            }
            else if (c == 3) {
                radii[c] = (particleScale + radii[c]) / 2.0;
            }
            else if (c == 4) {
                radii[c] = (particleScale + radii[c] * 2.0) / 3.0;
            }
            float v2 = 1.0f - MathHelper.sin((c + particleAge) / 2.0f) * 0.1f;
            colours[c][0] = particleRed * v2;
            colours[c][1] = particleGreen * v2;
            colours[c][2] = particleBlue * v2;
            colours[c][3] = 1.0f;
        }
        if (vecs.size() > 2 && rand.nextBoolean()) {
            int q = rand.nextInt(3);
            if (rand.nextBoolean()) {
                q = vecs.size() - 2;
            }
            FXDispatcher.INSTANCE.essentiaDropFx(vecs.get(q).x + startX, vecs.get(q).y + startY, vecs.get(q).z + startZ, particleRed, particleGreen, particleBlue, 0.5f);
        }
    }
~~~

### src/main/java/thaumcraft/client/fx/other/FXVoidStream.java

~~~java
public FXVoidStream(World w, double par2, double par4, double par6, double tx, double ty, double tz, int seed, float scale) {
        super(w, par2, par4, par6, 0.0, 0.0, 0.0);
        this.seed = 0;
        length = 20;
        gle = new CoreGLE();
        layer = 1;
        growing = -1;
        vecs = new ArrayList<Quat>();
        shaderCallback = new ShaderCallback() {
            @Override
            public void call(int shader) {
                Minecraft mc = Minecraft.getMinecraft();
                int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
                ARBShaderObjects.glUniform1fARB(x, (float)(mc.player.rotationYaw * 2.0f * 3.141592653589793 / 360.0));
                int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
                ARBShaderObjects.glUniform1fARB(z, -(float)(mc.player.rotationPitch * 2.0f * 3.141592653589793 / 360.0));
            }
        };
        particleScale = (float)(scale * (1.0 + rand.nextGaussian() * 0.15000000596046448));
        length = 40;
        this.seed = seed;
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        double dx = tx - posX;
        double dy = ty - posY;
        double dz = tz - posZ;
        int base = (int)(MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 21.0f);
        if (base < 1) {
            base = 1;
        }
        particleMaxAge = base * 2;
        motionX = MathHelper.sin(seed / 4.0f) * 0.025f;
        motionY = MathHelper.sin(seed / 3.0f) * 0.025f;
        motionZ = MathHelper.sin(seed / 2.0f) * 0.025f;
        particleGravity = 0.2f;
        vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
        startX = posX;
        startY = posY;
        startZ = posZ;
    }
~~~

~~~java
public void renderParticle(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Tessellator.getInstance().draw();
        GL11.glPushMatrix();
        double ePX = startX - FXVoidStream.interpPosX;
        double ePY = startY - FXVoidStream.interpPosY;
        double ePZ = startZ - FXVoidStream.interpPosZ;
        GL11.glTranslated(ePX, ePY, ePZ);
        for (int q = 0; q <= 1; ++q) {
            if (q < 1) {
                GlStateManager.depthMask(false);
            }
            GL11.glBlendFunc(770, (q < 1) ? 1 : 771);
            if (points != null && points.length > 2) {
                Minecraft.getMinecraft().renderEngine.bindTexture(FXVoidStream.starsTexture);
                ShaderHelper.useShader(ShaderHelper.endShader, shaderCallback);
                double[] r2 = new double[radii.length];
                int ri = 0;
                float m = (1.5f - q) / 1.0f;
                for (double d : radii) {
                    r2[ri] = radii[ri] * m;
                    ++ri;
                }
                gle.set_POLYCYL_TESS(3);
                gle.set__ROUND_TESS_PIECES(1);
                gle.gleSetJoinStyle(1042);
                gle.glePolyCone(points.length, points, colours, r2, 0.075f, (growing < 0) ? 0.0f : (0.075f * (particleAge - growing + f)));
                ShaderHelper.releaseShader();
            }
            if (q < 1) {
                GlStateManager.depthMask(true);
            }
        }
        GlStateManager.depthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glPopMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(ParticleEngine.particleTexture);
        wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }
~~~

~~~java
public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge || length < 1) {
            setExpired();
            return;
        }
        motionY += 0.01 * particleGravity;
        move(motionX, motionY, motionZ);
        motionX *= 0.985;
        motionY *= 0.985;
        motionZ *= 0.985;
        motionX = MathHelper.clamp((float) motionX, -0.04f, 0.04f);
        motionY = MathHelper.clamp((float) motionY, -0.04f, 0.04f);
        motionZ = MathHelper.clamp((float) motionZ, -0.04f, 0.04f);
        double dx = targetX - posX;
        double dy = targetY - posY;
        double dz = targetZ - posZ;
        double d13 = 0.01;
        double d14 = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= d14;
        dy /= d14;
        dz /= d14;
        motionX += dx * (d13 / Math.min(1.0, d14)) + rand.nextGaussian() * 0.014999999664723873;
        motionY += dy * (d13 / Math.min(1.0, d14)) + rand.nextGaussian() * 0.014999999664723873;
        motionZ += dz * (d13 / Math.min(1.0, d14)) + rand.nextGaussian() * 0.014999999664723873;
        float scale = particleScale * (0.75f + MathHelper.sin((seed + particleAge) / 2.0f) * 0.25f);
        if (d14 < 0.5) {
            float f = MathHelper.sin((float)(d14 * 1.5707963267948966));
            scale *= f;
            particleScale *= f;
        }
        if (particleScale > 0.001) {
            vecs.add(new Quat(scale, posX - startX, posY - startY, posZ - startZ));
        }
        else {
            if (growing < 0) {
                growing = particleAge;
            }
            --length;
        }
        if (vecs.size() > length) {
            vecs.remove(0);
        }
        points = new double[vecs.size()][3];
        colours = new float[vecs.size()][4];
        radii = new double[vecs.size()];
        int c = vecs.size();
        for (Quat v : vecs) {
            --c;
            float variance = 1.0f + MathHelper.sin((c + particleAge) / 3.0f) * 0.2f;
            float xx = MathHelper.sin((c + particleAge) / 6.0f) * 0.01f;
            float yy = MathHelper.sin((c + particleAge) / 7.0f) * 0.01f;
            float zz = MathHelper.sin((c + particleAge) / 8.0f) * 0.01f;
            points[c][0] = v.x + xx;
            points[c][1] = v.y + yy;
            points[c][2] = v.z + zz;
            radii[c] = v.s * variance;
            if (c > vecs.size() - 10) {
                double[] radii = this.radii;
                int n = c;
                radii[n] *= MathHelper.cos((float)((c - (vecs.size() - 12)) / 10.0f * 1.5707963267948966));
            }
            if (c == 0) {
                radii[c] = 0.0;
            }
            else if (c == 1) {
                radii[c] = 0.0;
            }
            else if (c == 2) {
                radii[c] = (particleScale * 0.5 + radii[c]) / 2.0;
            }
            else if (c == 3) {
                radii[c] = (particleScale + radii[c]) / 2.0;
            }
            else if (c == 4) {
                radii[c] = (particleScale + radii[c] * 2.0) / 3.0;
            }
            colours[c][0] = 1.0f;
            colours[c][1] = 1.0f;
            colours[c][2] = 1.0f;
            colours[c][3] = 1.0f;
        }
        if (vecs.size() > 2 && rand.nextBoolean()) {
            int q = rand.nextInt(3);
            if (rand.nextBoolean()) {
                q = vecs.size() - 2;
            }
        }
    }
~~~

### src/main/java/thaumcraft/client/lib/events/RenderEventHandler.java

~~~java
private static void drawSealArea(EntityPlayer player, ISealEntity seal, float alpha, float partialTicks) {
        GL11.glPushMatrix();
        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;
        if (seal.getColor() > 0) {
            Color c = new Color(EnumDyeColor.byMetadata(seal.getColor() - 1).getColorValue());
            r = c.getRed() / 255.0f;
            g = c.getGreen() / 255.0f;
            b = c.getBlue() / 255.0f;
        }
        else {
            r = 0.7f + MathHelper.sin((player.ticksExisted + partialTicks + seal.getSealPos().pos.getX()) / 4.0f) * 0.1f;
            g = 0.7f + MathHelper.sin((player.ticksExisted + partialTicks + seal.getSealPos().pos.getY()) / 5.0f) * 0.1f;
            b = 0.7f + MathHelper.sin((player.ticksExisted + partialTicks + seal.getSealPos().pos.getZ()) / 6.0f) * 0.1f;
        }
        GL11.glPushMatrix();
        GL11.glTranslated(seal.getSealPos().pos.getX() + 0.5, seal.getSealPos().pos.getY() + 0.5, seal.getSealPos().pos.getZ() + 0.5);
        GL11.glRotatef(90.0f, (float)(-seal.getSealPos().face.getFrontOffsetY()), (float)seal.getSealPos().face.getFrontOffsetX(), (float)(-seal.getSealPos().face.getFrontOffsetZ()));
        if (seal.getSealPos().face.getFrontOffsetZ() < 0) {
            GL11.glTranslated(0.0, 0.0, -0.5099999904632568);
        }
        else {
            GL11.glTranslated(0.0, 0.0, 0.5099999904632568);
        }
        GL11.glRotatef(player.ticksExisted % 360 + partialTicks, 0.0f, 0.0f, 1.0f);
        UtilsFX.renderQuadCentered(RenderEventHandler.MIDDLE, 0.9f, r, g, b, 200, 771, alpha * 0.8f);
        GL11.glPopMatrix();
        if (seal.getSeal() instanceof ISealConfigArea) {
            GL11.glDepthMask(false);
            AxisAlignedBB area = new AxisAlignedBB(seal.getSealPos().pos.getX(), seal.getSealPos().pos.getY(), seal.getSealPos().pos.getZ(), seal.getSealPos().pos.getX() + 1, seal.getSealPos().pos.getY() + 1, seal.getSealPos().pos.getZ() + 1).offset(seal.getSealPos().face.getFrontOffsetX(), seal.getSealPos().face.getFrontOffsetY(), seal.getSealPos().face.getFrontOffsetZ()).expand((seal.getSealPos().face.getFrontOffsetX() != 0) ? ((double)((seal.getArea().getX() - 1) * seal.getSealPos().face.getFrontOffsetX())) : 0.0, (seal.getSealPos().face.getFrontOffsetY() != 0) ? ((double)((seal.getArea().getY() - 1) * seal.getSealPos().face.getFrontOffsetY())) : 0.0, (seal.getSealPos().face.getFrontOffsetZ() != 0) ? ((double)((seal.getArea().getZ() - 1) * seal.getSealPos().face.getFrontOffsetZ())) : 0.0).grow((seal.getSealPos().face.getFrontOffsetX() == 0) ? ((double)(seal.getArea().getX() - 1)) : 0.0, (seal.getSealPos().face.getFrontOffsetY() == 0) ? ((double)(seal.getArea().getY() - 1)) : 0.0, (seal.getSealPos().face.getFrontOffsetZ() == 0) ? ((double)(seal.getArea().getZ() - 1)) : 0.0);
            double[][] locs = { { area.minX, area.minY, area.minZ }, { area.minX, area.maxY - 1.0, area.minZ }, { area.maxX - 1.0, area.minY, area.minZ }, { area.maxX - 1.0, area.maxY - 1.0, area.minZ }, { area.maxX - 1.0, area.minY, area.maxZ - 1.0 }, { area.maxX - 1.0, area.maxY - 1.0, area.maxZ - 1.0 }, { area.minX, area.minY, area.maxZ - 1.0 }, { area.minX, area.maxY - 1.0, area.maxZ - 1.0 } };
            int q = 0;
            for (double[] loc : locs) {
                GL11.glPushMatrix();
                GL11.glTranslated(loc[0] + 0.5, loc[1] + 0.5, loc[2] + 0.5);
                int w = 0;
                for (EnumFacing face : RenderEventHandler.rotfaces[q]) {
                    GL11.glPushMatrix();
                    GL11.glRotatef(90.0f, (float)(-face.getFrontOffsetY()), (float)face.getFrontOffsetX(), (float)(-face.getFrontOffsetZ()));
                    if (face.getFrontOffsetZ() < 0) {
                        GL11.glTranslated(0.0, 0.0, -0.49000000953674316);
                    }
                    else {
                        GL11.glTranslated(0.0, 0.0, 0.49000000953674316);
                    }
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
                    GL11.glRotatef((float)RenderEventHandler.rotmat[q][w], 0.0f, 0.0f, 1.0f);
                    UtilsFX.renderQuadCentered(RenderEventHandler.CFRAME, 1.0f, r, g, b, 200, 771, alpha * 0.7f);
                    GL11.glPopMatrix();
                    ++w;
                }
                GL11.glPopMatrix();
                ++q;
            }
            GL11.glDepthMask(true);
        }
        GL11.glPopMatrix();
    }
~~~

~~~java
public static void textureStitchEventPre(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "research/quill"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/crystal"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/taint_growth_1"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/taint_growth_2"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/taint_growth_3"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/taint_growth_4"));
    }
~~~

### src/main/java/thaumcraft/client/renderers/block/CrystalModel.java

~~~java
public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (side == null && state instanceof IExtendedBlockState) {
            List<BakedQuad> ret = new ArrayList<BakedQuad>();
            IExtendedBlockState es = (IExtendedBlockState)state;
            int m = ((BlockCrystal)state.getBlock()).getGrowth(state) + 1;
            MeshModel mm = sourceMesh.clone();
            try {
                if (es != null) {
                    if (!(boolean)es.getValue(BlockCrystal.UP) || !(boolean)es.getValue(BlockCrystal.DOWN) || !(boolean)es.getValue(BlockCrystal.EAST) || !(boolean)es.getValue(BlockCrystal.WEST) || !(boolean)es.getValue(BlockCrystal.NORTH) || !(boolean)es.getValue(BlockCrystal.SOUTH)) {
                        if (es.getValue(BlockCrystal.UP)) {
                            List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                            Collections.shuffle(c, new Random(rand));
                            mm.parts.clear();
                            for (int a = 0; a < m; ++a) {
                                mm.parts.add(sourceMesh.parts.get(c.get(a)));
                            }
                            MeshModel mod = mm.clone();
                            mod.rotate(Math.toRadians(180.0), new Vector3(1.0, 0.0, 0.0), new Vector3(0.0, 1.0, 1.0));
                            for (BakedQuad quad : mod.bakeModel(getParticleTexture())) {
                                ret.add(quad);
                            }
                        }
                        if (es.getValue(BlockCrystal.DOWN)) {
                            List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                            Collections.shuffle(c, new Random(rand + 5L));
                            mm.parts.clear();
                            for (int a = 0; a < m; ++a) {
                                mm.parts.add(sourceMesh.parts.get(c.get(a)));
                            }
                            for (BakedQuad quad2 : mm.bakeModel(getParticleTexture())) {
                                ret.add(quad2);
                            }
                        }
                        if (es.getValue(BlockCrystal.EAST)) {
                            List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                            Collections.shuffle(c, new Random(rand + 10L));
                            mm.parts.clear();
                            for (int a = 0; a < m; ++a) {
                                mm.parts.add(sourceMesh.parts.get(c.get(a)));
                            }
                            MeshModel mod = mm.clone();
                            mod.rotate(Math.toRadians(90.0), new Vector3(1.0, 0.0, 0.0), new Vector3(0.0, 0.0, 0.0));
                            mod.rotate(Math.toRadians(270.0), new Vector3(0.0, 1.0, 0.0), new Vector3(1.0, 1.0, 0.0));
                            for (BakedQuad quad : mod.bakeModel(getParticleTexture())) {
                                ret.add(quad);
                            }
                        }
                        if (es.getValue(BlockCrystal.WEST)) {
                            List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                            Collections.shuffle(c, new Random(rand + 15L));
                            mm.parts.clear();
                            for (int a = 0; a < m; ++a) {
                                mm.parts.add(sourceMesh.parts.get(c.get(a)));
                            }
                            MeshModel mod = mm.clone();
                            mod.rotate(Math.toRadians(90.0), new Vector3(1.0, 0.0, 0.0), new Vector3(0.0, 0.0, 0.0));
                            mod.rotate(Math.toRadians(90.0), new Vector3(0.0, 1.0, 0.0), new Vector3(0.0, 1.0, 1.0));
                            for (BakedQuad quad : mod.bakeModel(getParticleTexture())) {
                                ret.add(quad);
                            }
                        }
                        if (es.getValue(BlockCrystal.NORTH)) {
                            List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                            Collections.shuffle(c, new Random(rand + 20L));
                            mm.parts.clear();
                            for (int a = 0; a < m; ++a) {
                                mm.parts.add(sourceMesh.parts.get(c.get(a)));
                            }
                            MeshModel mod = mm.clone();
                            mod.rotate(Math.toRadians(90.0), new Vector3(1.0, 0.0, 0.0), new Vector3(0.0, 1.0, 0.0));
                            for (BakedQuad quad : mod.bakeModel(getParticleTexture())) {
                                ret.add(quad);
                            }
                        }
                        if (es.getValue(BlockCrystal.SOUTH)) {
                            List<Integer> c = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
                            Collections.shuffle(c, new Random(rand + 25L));
                            mm.parts.clear();
                            for (int a = 0; a < m; ++a) {
                                mm.parts.add(sourceMesh.parts.get(c.get(a)));
                            }
                            MeshModel mod = mm.clone();
                            mod.rotate(Math.toRadians(90.0), new Vector3(1.0, 0.0, 0.0), new Vector3(0.0, 0.0, 0.0));
                            mod.rotate(Math.toRadians(180.0), new Vector3(0.0, 1.0, 0.0), new Vector3(1.0, 1.0, 1.0));
                            for (BakedQuad quad : mod.bakeModel(getParticleTexture())) {
                                ret.add(quad);
                            }
                        }
                    }
                }
            }
            catch (Exception ex) {}
            return ret;
        }
        return ImmutableList.of();
    }
~~~

### src/main/java/thaumcraft/common/blocks/basic/BlockPavingStone.java

~~~java
public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        if (state.getBlock() == BlocksTC.pavingStoneBarrier) {
            if (world.isBlockIndirectlyGettingPowered(pos) > 0) {
                for (int a = 0; a < 4; ++a) {
                    FXDispatcher.INSTANCE.blockRunes(pos.getX(), pos.getY() + 0.7f, pos.getZ(), 0.2f + random.nextFloat() * 0.4f, random.nextFloat() * 0.3f, 0.8f + random.nextFloat() * 0.2f, 20, -0.02f);
                }
            }
            else if ((world.getBlockState(pos.up(1)) == BlocksTC.barrier.getDefaultState() && world.getBlockState(pos.up(1)).getBlock().isPassable(world, pos.up(1))) || (world.getBlockState(pos.up(2)) == BlocksTC.barrier.getDefaultState() && world.getBlockState(pos.up(2)).getBlock().isPassable(world, pos.up(2)))) {
                for (int a = 0; a < 6; ++a) {
                    FXDispatcher.INSTANCE.blockRunes(pos.getX(), pos.getY() + 0.7f, pos.getZ(), 0.9f + random.nextFloat() * 0.1f, random.nextFloat() * 0.3f, random.nextFloat() * 0.3f, 24, -0.02f);
                }
            }
            else {
                List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(1.0, 1.0, 1.0));
                if (!list.isEmpty()) {
                    for (Entity entity : list) {
                        if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer)) {
                            FXDispatcher.INSTANCE.blockRunes(pos.getX(), pos.getY() + 0.6f + random.nextFloat() * Math.max(0.8f, entity.getEyeHeight()), pos.getZ(), 0.6f + random.nextFloat() * 0.4f, 0.0f, 0.3f + random.nextFloat() * 0.7f, 20, 0.0f);
                            break;
                        }
                    }
                }
            }
        }
    }
~~~

### src/main/java/thaumcraft/common/blocks/devices/BlockLamp.java

~~~java
public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
        if (worldIn.isAirBlock(pos.offset(BlockStateUtils.getFacing(state)))) {
            dropBlockAsItem(worldIn, pos, getDefaultState(), 0);
            worldIn.setBlockToAir(pos);
            return;
        }
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileLampArcane && BlockStateUtils.isEnabled(state) && worldIn.isBlockPowered(pos)) {
            ((TileLampArcane)te).removeLights();
        }
        boolean checkUpdate = true;
        if (te != null && te instanceof TileLampGrowth && ((TileLampGrowth)te).charges <= 0) {
            checkUpdate = false;
        }
        if (te != null && te instanceof TileLampFertility && ((TileLampFertility)te).charges <= 0) {
            checkUpdate = false;
        }
        if (checkUpdate) {
            super.neighborChanged(state, worldIn, pos, blockIn, pos2);
        }
    }
~~~

### src/main/java/thaumcraft/common/blocks/world/ore/BlockCrystal.java

~~~java
public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> ret = new ArrayList<ItemStack>();
        for (int count = getGrowth(state) + 1, i = 0; i < count; ++i) {
            ret.add(ThaumcraftApiHelper.makeCrystal(aspect));
        }
        return ret;
    }
~~~

~~~java
public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote && rand.nextInt(3 + getGeneration(state)) == 0) {
            int threshold = 10;
            int growth = getGrowth(state);
            int generation = getGeneration(state);
            if (aspect != Aspect.FLUX) {
                if (AuraHelper.getVis(worldIn, pos) <= threshold) {
                    if (growth > 0) {
                        worldIn.setBlockState(pos, state.withProperty((IProperty)BlockCrystal.SIZE, (Comparable)(growth - 1)));
                        AuraHelper.addVis(worldIn, pos, (float)threshold);
                    }
                    else if (BlockUtils.isBlockTouching(worldIn, pos, this)) {
                        worldIn.setBlockToAir(pos);
                        AuraHandler.addVis(worldIn, pos, (float)threshold);
                    }
                }
                else if (AuraHelper.getVis(worldIn, pos) > AuraHandler.getAuraBase(worldIn, pos) + threshold) {
                    if (growth < 3 && growth < 5 - generation + pos.toLong() % 3L) {
                        if (AuraHelper.drainVis(worldIn, pos, (float)threshold, false) > 0.0f) {
                            worldIn.setBlockState(pos, state.withProperty((IProperty)BlockCrystal.SIZE, (Comparable)(growth + 1)));
                        }
                    }
                    else if (generation < 4) {
                        BlockPos p2 = spreadCrystal(worldIn, pos);
                        if (p2 != null && AuraHelper.drainVis(worldIn, pos, (float)threshold, false) > 0.0f) {
                            if (rand.nextInt(6) == 0) {
                                --generation;
                            }
                            worldIn.setBlockState(p2, getDefaultState().withProperty((IProperty)BlockCrystal.GENERATION, (Comparable)(generation + 1)));
                        }
                    }
                }
            }
            else if (AuraHelper.getFlux(worldIn, pos) <= threshold) {
                if (growth > 0) {
                    worldIn.setBlockState(pos, state.withProperty((IProperty)BlockCrystal.SIZE, (Comparable)(growth - 1)));
                    AuraHelper.polluteAura(worldIn, pos, (float)threshold, false);
                }
                else if (BlockUtils.isBlockTouching(worldIn, pos, this)) {
                    worldIn.setBlockToAir(pos);
                    AuraHelper.polluteAura(worldIn, pos, (float)threshold, false);
                }
            }
            else if (AuraHelper.getFlux(worldIn, pos) > AuraHandler.getAuraBase(worldIn, pos) + threshold) {
                if (growth < 3 && growth < 5 - generation + pos.toLong() % 3L) {
                    if (AuraHelper.drainFlux(worldIn, pos, (float)threshold, false) > 0.0f) {
                        worldIn.setBlockState(pos, state.withProperty((IProperty)BlockCrystal.SIZE, (Comparable)(growth + 1)));
                    }
                }
                else if (generation < 4) {
                    BlockPos p2 = spreadCrystal(worldIn, pos);
                    if (p2 != null && AuraHelper.drainFlux(worldIn, pos, (float)threshold, false) > 0.0f) {
                        if (rand.nextInt(6) == 0) {
                            --generation;
                        }
                        worldIn.setBlockState(p2, getDefaultState().withProperty((IProperty)BlockCrystal.GENERATION, (Comparable)(generation + 1)));
                    }
                }
            }
        }
    }
~~~

~~~java
public int getGrowth(IBlockState state) {
        return getMetaFromState(state) & 0x3;
    }
~~~

### src/main/java/thaumcraft/common/blocks/world/plants/BlockLeavesTC.java

~~~java
public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return (state.getBlock() == BlocksTC.leafSilverwood) ? MapColor.LIGHT_BLUE : super.getMapColor(state, worldIn, pos);
    }
~~~

~~~java
public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote && state.getBlock() == BlocksTC.leafSilverwood && (boolean)state.getValue((IProperty)BlockLeavesTC.DECAYABLE) && AuraHandler.getVis(worldIn, pos) < AuraHandler.getAuraBase(worldIn, pos)) {
            AuraHandler.addVis(worldIn, pos, 0.01f);
        }
        super.updateTick(worldIn, pos, state, rand);
    }
~~~

~~~java
protected int getSaplingDropChance(IBlockState state) {
        return 75;
    }
~~~

~~~java
protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {
        if (state.getBlock() == BlocksTC.leafSilverwood && worldIn.rand.nextInt((int)(chance * 0.75)) == 0) {
            spawnAsEntity(worldIn, pos, new ItemStack(ItemsTC.nuggets, 1, 5));
        }
    }
~~~

~~~java
public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return (state.getBlock() == BlocksTC.leafSilverwood) ? Item.getItemFromBlock(BlocksTC.saplingSilverwood) : Item.getItemFromBlock(BlocksTC.saplingGreatwood);
    }
~~~

### src/main/java/thaumcraft/common/blocks/world/plants/BlockLogsTC.java

~~~java
public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return (state.getBlock() == BlocksTC.logSilverwood) ? 5 : super.getLightValue(state, world, pos);
    }
~~~

### src/main/java/thaumcraft/common/blocks/world/plants/BlockSaplingTC.java

~~~java
public BlockSaplingTC(String name) {
        setUnlocalizedName(name);
        setRegistryName("thaumcraft", name);
        setDefaultState(blockState.getBaseState().withProperty((IProperty)BlockSaplingTC.STAGE, (Comparable)0));
        setCreativeTab(ConfigItems.TABTC);
        setSoundType(SoundType.PLANT);
    }
~~~

~~~java
public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BlockSaplingTC.SAPLING_AABB;
    }
~~~

~~~java
public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            super.updateTick(worldIn, pos, state, rand);
            if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
                grow(worldIn, pos, state, rand);
            }
        }
    }
~~~

~~~java
public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if ((int)state.getValue((IProperty)BlockSaplingTC.STAGE) == 0) {
            worldIn.setBlockState(pos, state.cycleProperty((IProperty)BlockSaplingTC.STAGE), 4);
        }
        else {
            generateTree(worldIn, pos, state, rand);
        }
    }
~~~

~~~java
public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!TerrainGen.saplingGrowTree(worldIn, rand, pos)) {
            return;
        }
        Object object = null;
        int i = 0;
        int j = 0;
        boolean flag = false;
        Label_0111: {
            if (state.getBlock() == BlocksTC.saplingGreatwood) {
                for (i = 0; i >= -1; --i) {
                    for (j = 0; j >= -1; --j) {
                        if (isTwoByTwoOfType(worldIn, pos, i, j, BlocksTC.saplingGreatwood)) {
                            object = new WorldGenGreatwoodTrees(true, false);
                            flag = true;
                            break Label_0111;
                        }
                    }
                }
            }
            else {
                object = new WorldGenSilverwoodTrees(true, 7, 4);
            }
        }
        if (object == null) {
            return;
        }
        IBlockState iblockstate1 = Blocks.AIR.getDefaultState();
        if (flag) {
            worldIn.setBlockState(pos.add(i, 0, j), iblockstate1, 4);
            worldIn.setBlockState(pos.add(i + 1, 0, j), iblockstate1, 4);
            worldIn.setBlockState(pos.add(i, 0, j + 1), iblockstate1, 4);
            worldIn.setBlockState(pos.add(i + 1, 0, j + 1), iblockstate1, 4);
        }
        else {
            worldIn.setBlockState(pos, iblockstate1, 4);
        }
        if (!((WorldGenerator)object).generate(worldIn, rand, pos.add(i, 0, j))) {
            if (flag) {
                worldIn.setBlockState(pos.add(i, 0, j), state, 4);
                worldIn.setBlockState(pos.add(i + 1, 0, j), state, 4);
                worldIn.setBlockState(pos.add(i, 0, j + 1), state, 4);
                worldIn.setBlockState(pos.add(i + 1, 0, j + 1), state, 4);
            }
            else {
                worldIn.setBlockState(pos.add(i, 0, j), state, 4);
            }
        }
    }
~~~

~~~java
public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }
~~~

~~~java
public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return worldIn.rand.nextFloat() < 0.25;
    }
~~~

~~~java
public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        grow(worldIn, pos, state, rand);
    }
~~~

~~~java
public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty((IProperty)BlockSaplingTC.STAGE, (Comparable)((meta & 0x8) >> 3));
    }
~~~

~~~java
public int getMetaFromState(IBlockState state) {
        int i = 0;
        i |= (int)state.getValue((IProperty)BlockSaplingTC.STAGE) << 3;
        return i;
    }
~~~

~~~java
protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockSaplingTC.STAGE);
    }
~~~

### src/main/java/thaumcraft/common/blocks/world/taint/BlockTaintFibre.java

~~~java
public BlockTaintFibre() {
        super(ThaumcraftMaterials.MATERIAL_TAINT);
        rayTracer = new RayTracer();
        setUnlocalizedName("taint_fibre");
        setRegistryName("thaumcraft", "taint_fibre");
        setHardness(1.0f);
        setSoundType(SoundsTC.GORE);
        setTickRandomly(true);
        setCreativeTab(ConfigItems.TABTC);
        setDefaultState(blockState.getBaseState().withProperty(BlockTaintFibre.NORTH, false).withProperty(BlockTaintFibre.EAST, false).withProperty(BlockTaintFibre.SOUTH, false).withProperty(BlockTaintFibre.WEST, false).withProperty(BlockTaintFibre.UP, false).withProperty(BlockTaintFibre.DOWN, false).withProperty(BlockTaintFibre.GROWTH1, false).withProperty(BlockTaintFibre.GROWTH2, false).withProperty(BlockTaintFibre.GROWTH3, false).withProperty(BlockTaintFibre.GROWTH4, false));
    }
~~~

~~~java
public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        state = getActualState(state, worldIn, pos);
        if (state instanceof IBlockState && state.getValue(BlockTaintFibre.GROWTH3)) {
            if (worldIn.rand.nextInt(5) <= fortune) {
                spawnAsEntity(worldIn, pos, ConfigItems.FLUX_CRYSTAL.copy());
            }
            AuraHelper.polluteAura(worldIn, pos, 1.0f, true);
        }
    }
~~~

~~~java
public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!world.isRemote) {
            state = getActualState(state, world, pos);
            if (state instanceof IBlockState) {
                if (!(boolean)state.getValue(BlockTaintFibre.GROWTH1) && !(boolean)state.getValue(BlockTaintFibre.GROWTH2) && !(boolean)state.getValue(BlockTaintFibre.GROWTH3) && !(boolean)state.getValue(BlockTaintFibre.GROWTH4) && isOnlyAdjacentToTaint(world, pos)) {
                    die(world, pos, state);
                }
                else if (!TaintHelper.isNearTaintSeed(world, pos)) {
                    die(world, pos, state);
                }
                else {
                    TaintHelper.spreadFibres(world, pos);
                }
            }
        }
    }
~~~

~~~java
public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
        state = getActualState(state, worldIn, pos);
        if (state instanceof IBlockState && !(boolean)state.getValue(BlockTaintFibre.GROWTH1) && !(boolean)state.getValue(BlockTaintFibre.GROWTH2) && !(boolean)state.getValue(BlockTaintFibre.GROWTH3) && !(boolean)state.getValue(BlockTaintFibre.GROWTH4) && isOnlyAdjacentToTaint(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
        }
    }
~~~

~~~java
public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        if (drawAt(world, pos.up(), EnumFacing.UP)) {
            cuboids.add(new IndexedCuboid6(0, new Cuboid6(BlockTaintFibre.AABB_UP.offset(pos))));
        }
        if (drawAt(world, pos.down(), EnumFacing.DOWN)) {
            cuboids.add(new IndexedCuboid6(1, new Cuboid6(BlockTaintFibre.AABB_DOWN.offset(pos))));
        }
        if (drawAt(world, pos.east(), EnumFacing.EAST)) {
            cuboids.add(new IndexedCuboid6(2, new Cuboid6(BlockTaintFibre.AABB_EAST.offset(pos))));
        }
        if (drawAt(world, pos.west(), EnumFacing.WEST)) {
            cuboids.add(new IndexedCuboid6(3, new Cuboid6(BlockTaintFibre.AABB_WEST.offset(pos))));
        }
        if (drawAt(world, pos.south(), EnumFacing.SOUTH)) {
            cuboids.add(new IndexedCuboid6(4, new Cuboid6(BlockTaintFibre.AABB_SOUTH.offset(pos))));
        }
        if (drawAt(world, pos.north(), EnumFacing.NORTH)) {
            cuboids.add(new IndexedCuboid6(5, new Cuboid6(BlockTaintFibre.AABB_NORTH.offset(pos))));
        }
        IBlockState ss = getActualState(world.getBlockState(pos), world, pos);
        if (ss.getBlock() == this && ss instanceof IBlockState) {
            if (ss.getValue(BlockTaintFibre.GROWTH1)) {
                cuboids.add(new IndexedCuboid6(6, new Cuboid6(new AxisAlignedBB(0.10000000149011612, 0.0, 0.10000000149011612, 0.8999999761581421, 0.4000000059604645, 0.8999999761581421).offset(pos))));
            }
            else if (ss.getValue(BlockTaintFibre.GROWTH2)) {
                cuboids.add(new IndexedCuboid6(6, new Cuboid6(new AxisAlignedBB(0.20000000298023224, 0.0, 0.20000000298023224, 0.800000011920929, 1.0, 0.800000011920929).offset(pos))));
            }
            else if (ss.getValue(BlockTaintFibre.GROWTH3)) {
                cuboids.add(new IndexedCuboid6(6, new Cuboid6(new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.3125, 0.75).offset(pos))));
            }
            else if (ss.getValue(BlockTaintFibre.GROWTH4)) {
                cuboids.add(new IndexedCuboid6(6, new Cuboid6(new AxisAlignedBB(0.10000000149011612, 0.30000001192092896, 0.10000000149011612, 0.8999999761581421, 1.0, 0.8999999761581421).offset(pos))));
            }
        }
        ArrayList<ExtendedMOP> list = new ArrayList<ExtendedMOP>();
        rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(pos), this, list);
        return (list.size() > 0) ? list.get(0) : super.collisionRayTrace(state, world, pos, start, end);
    }
~~~

~~~java
public AxisAlignedBB getSelectedBoundingBox(IBlockState s, World world, BlockPos pos) {
        IBlockState state = getActualState(world.getBlockState(pos), world, pos);
        if (state.getBlock() == this && state instanceof IBlockState) {
            if (state.getValue(BlockTaintFibre.GROWTH1)) {
                return new AxisAlignedBB(0.10000000149011612, 0.0, 0.10000000149011612, 0.8999999761581421, 0.4000000059604645, 0.8999999761581421).offset(pos);
            }
            if (state.getValue(BlockTaintFibre.GROWTH2)) {
                return new AxisAlignedBB(0.20000000298023224, 0.0, 0.20000000298023224, 0.800000011920929, 1.0, 0.800000011920929).offset(pos);
            }
            if (state.getValue(BlockTaintFibre.GROWTH3)) {
                return new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.3125, 0.75).offset(pos);
            }
            if (state.getValue(BlockTaintFibre.GROWTH4)) {
                return new AxisAlignedBB(0.10000000149011612, 0.30000001192092896, 0.10000000149011612, 0.8999999761581421, 1.0, 0.8999999761581421).offset(pos);
            }
        }
        RayTraceResult hit = RayTracer.retraceBlock(world, Minecraft.getMinecraft().player, pos);
        if (hit != null) {
            switch (hit.subHit) {
                case 0: {
                    return BlockTaintFibre.AABB_UP.offset(pos);
                }
                case 1: {
                    return BlockTaintFibre.AABB_DOWN.offset(pos);
                }
                case 2: {
                    return BlockTaintFibre.AABB_EAST.offset(pos);
                }
                case 3: {
                    return BlockTaintFibre.AABB_WEST.offset(pos);
                }
                case 4: {
                    return BlockTaintFibre.AABB_SOUTH.offset(pos);
                }
                case 5: {
                    return BlockTaintFibre.AABB_NORTH.offset(pos);
                }
            }
        }
        return BlockTaintFibre.AABB_EMPTY;
    }
~~~

~~~java
public int getLightValue(IBlockState state2, IBlockAccess world, BlockPos pos) {
        IBlockState state3 = getActualState(world.getBlockState(pos), world, pos);
        if (state3.getBlock() == this && state3 instanceof IBlockState) {
            return state3.getValue(BlockTaintFibre.GROWTH3) ? 12 : ((state3.getValue(BlockTaintFibre.GROWTH2) || state3.getValue(BlockTaintFibre.GROWTH4)) ? 6 : super.getLightValue(state2, world, pos));
        }
        return super.getLightValue(state2, world, pos);
    }
~~~

~~~java
public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        Boolean[] cons = makeConnections(state, worldIn, pos);
        boolean d = drawAt(worldIn, pos.down(), EnumFacing.DOWN);
        boolean u = drawAt(worldIn, pos.up(), EnumFacing.UP);
        int growth = 0;
        Random rand = new Random(pos.toLong());
        int q = rand.nextInt(50);
        if (d) {
            if (q < 4) {
                growth = 1;
            }
            else if (q == 4 || q == 5) {
                growth = 2;
            }
            else if (q == 6) {
                growth = 3;
            }
        }
        if (u && q > 47) {
            growth = 4;
        }
        try {
            return state.withProperty(BlockTaintFibre.DOWN, cons[0]).withProperty(BlockTaintFibre.UP, cons[1]).withProperty(BlockTaintFibre.NORTH, cons[2]).withProperty(BlockTaintFibre.SOUTH, cons[3]).withProperty(BlockTaintFibre.WEST, cons[4]).withProperty(BlockTaintFibre.EAST, cons[5]).withProperty(BlockTaintFibre.GROWTH1, (growth == 1)).withProperty(BlockTaintFibre.GROWTH2, (growth == 2)).withProperty(BlockTaintFibre.GROWTH3, (growth == 3)).withProperty(BlockTaintFibre.GROWTH4, (growth == 4));
        }
        catch (Exception e) {
            return state;
        }
    }
~~~

~~~java
protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockTaintFibre.NORTH, BlockTaintFibre.EAST, BlockTaintFibre.SOUTH, BlockTaintFibre.WEST, BlockTaintFibre.UP, BlockTaintFibre.DOWN, BlockTaintFibre.GROWTH1, BlockTaintFibre.GROWTH2, BlockTaintFibre.GROWTH3, BlockTaintFibre.GROWTH4);
    }
~~~

### src/main/java/thaumcraft/common/config/ConfigAspects.java

~~~java
private static void registerItemAspects() {
        ThaumcraftApi.registerObjectTag("oreLapis", new AspectList().add(Aspect.EARTH, 5).add(Aspect.SENSES, 15));
        ThaumcraftApi.registerObjectTag("oreDiamond", new AspectList().add(Aspect.EARTH, 5).add(Aspect.DESIRE, 15).add(Aspect.CRYSTAL, 15));
        ThaumcraftApi.registerObjectTag("gemDiamond", new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.DESIRE, 15));
        ThaumcraftApi.registerObjectTag("oreRedstone", new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENERGY, 15));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.LIT_REDSTONE_ORE), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENERGY, 15));
        ThaumcraftApi.registerObjectTag("oreEmerald", new AspectList().add(Aspect.EARTH, 5).add(Aspect.DESIRE, 10).add(Aspect.CRYSTAL, 15));
        ThaumcraftApi.registerObjectTag("gemEmerald", new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag("oreQuartz", new AspectList().add(Aspect.EARTH, 5).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag("gemQuartz", new AspectList().add(Aspect.CRYSTAL, 5));
        ThaumcraftApi.registerObjectTag("oreIron", new AspectList().add(Aspect.EARTH, 5).add(Aspect.METAL, 15));
        ThaumcraftApi.registerObjectTag("dustIron", new AspectList().add(Aspect.METAL, 15).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag("ingotIron", new AspectList().add(Aspect.METAL, 15));
        ThaumcraftApi.registerObjectTag("oreGold", new AspectList().add(Aspect.EARTH, 5).add(Aspect.METAL, 10).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag("dustGold", new AspectList().add(Aspect.METAL, 10).add(Aspect.DESIRE, 10).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag("ingotGold", new AspectList().add(Aspect.METAL, 10).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.COAL_ORE), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENERGY, 15).add(Aspect.FIRE, 15));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.COAL, 1, 32767), new AspectList().add(Aspect.ENERGY, 10).add(Aspect.FIRE, 10));
        ThaumcraftApi.registerObjectTag("dustRedstone", new AspectList().add(Aspect.ENERGY, 10));
        ThaumcraftApi.registerObjectTag("dustGlowstone", new AspectList().add(Aspect.SENSES, 5).add(Aspect.LIGHT, 10));
        ThaumcraftApi.registerObjectTag("glowstone", new AspectList(new ItemStack(Blocks.GLOWSTONE)));
        ThaumcraftApi.registerObjectTag("ingotCopper", new AspectList().add(Aspect.METAL, 10).add(Aspect.EXCHANGE, 5));
        ThaumcraftApi.registerObjectTag("dustCopper", new AspectList().add(Aspect.METAL, 10).add(Aspect.ENTROPY, 1).add(Aspect.EXCHANGE, 5));
        ThaumcraftApi.registerObjectTag("oreCopper", new AspectList().add(Aspect.METAL, 10).add(Aspect.EARTH, 5).add(Aspect.EXCHANGE, 5));
        ThaumcraftApi.registerObjectTag("clusterCopper", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5).add(Aspect.EXCHANGE, 10));
        ThaumcraftApi.registerObjectTag("ingotTin", new AspectList().add(Aspect.METAL, 10).add(Aspect.CRYSTAL, 5));
        ThaumcraftApi.registerObjectTag("dustTin", new AspectList().add(Aspect.METAL, 10).add(Aspect.ENTROPY, 1).add(Aspect.CRYSTAL, 5));
        ThaumcraftApi.registerObjectTag("oreTin", new AspectList().add(Aspect.METAL, 10).add(Aspect.EARTH, 5).add(Aspect.CRYSTAL, 5));
        ThaumcraftApi.registerObjectTag("clusterTin", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag("ingotSilver", new AspectList().add(Aspect.METAL, 10).add(Aspect.DESIRE, 5));
        ThaumcraftApi.registerObjectTag("dustSilver", new AspectList().add(Aspect.METAL, 10).add(Aspect.ENTROPY, 1).add(Aspect.DESIRE, 5));
        ThaumcraftApi.registerObjectTag("oreSilver", new AspectList().add(Aspect.METAL, 10).add(Aspect.EARTH, 5).add(Aspect.DESIRE, 5));
        ThaumcraftApi.registerObjectTag("clusterSilver", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag("ingotLead", new AspectList().add(Aspect.METAL, 10).add(Aspect.ORDER, 5));
        ThaumcraftApi.registerObjectTag("dustLead", new AspectList().add(Aspect.METAL, 10).add(Aspect.ENTROPY, 1).add(Aspect.ORDER, 5));
        ThaumcraftApi.registerObjectTag("oreLead", new AspectList().add(Aspect.METAL, 10).add(Aspect.EARTH, 5).add(Aspect.ORDER, 5));
        ThaumcraftApi.registerObjectTag("clusterLead", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5).add(Aspect.ORDER, 10));
        ThaumcraftApi.registerObjectTag("ingotBrass", new AspectList().add(Aspect.METAL, 10).add(Aspect.TOOL, 5));
        ThaumcraftApi.registerObjectTag("dustBrass", new AspectList().add(Aspect.METAL, 10).add(Aspect.ENTROPY, 1).add(Aspect.TOOL, 5));
        ThaumcraftApi.registerObjectTag("ingotBronze", new AspectList().add(Aspect.METAL, 10).add(Aspect.TOOL, 5));
        ThaumcraftApi.registerObjectTag("dustBronze", new AspectList().add(Aspect.METAL, 10).add(Aspect.ENTROPY, 1).add(Aspect.TOOL, 5));
        ThaumcraftApi.registerObjectTag("oreUranium", new AspectList().add(Aspect.METAL, 10).add(Aspect.DEATH, 5).add(Aspect.ENERGY, 10));
        ThaumcraftApi.registerObjectTag("itemDropUranium", new AspectList().add(Aspect.METAL, 10).add(Aspect.DEATH, 5).add(Aspect.ENERGY, 10));
        ThaumcraftApi.registerObjectTag("ingotUranium", new AspectList().add(Aspect.METAL, 10).add(Aspect.DEATH, 5).add(Aspect.ENERGY, 10));
        ThaumcraftApi.registerObjectTag("gemRuby", new AspectList().add(Aspect.CRYSTAL, 10).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag("gemGreenSapphire", new AspectList().add(Aspect.CRYSTAL, 10).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag("gemSapphire", new AspectList().add(Aspect.CRYSTAL, 10).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag("ingotSteel", new AspectList().add(Aspect.METAL, 15).add(Aspect.ORDER, 5));
        ThaumcraftApi.registerObjectTag("itemRubber", new AspectList().add(Aspect.MOTION, 5).add(Aspect.TOOL, 5));
        ThaumcraftApi.registerObjectTag("stone", new AspectList().add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag("stoneGranite", new AspectList().add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag("stoneDiorite", new AspectList().add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag("stoneAndesite", new AspectList().add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag("cobblestone", new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.BEDROCK), new AspectList().add(Aspect.VOID, 25).add(Aspect.ENTROPY, 25).add(Aspect.EARTH, 25).add(Aspect.DARKNESS, 25));
        ThaumcraftApi.registerObjectTag("dirt", new AspectList().add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.DIRT, 1, 2), new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.FARMLAND, 1, 32767), new AspectList().add(Aspect.EARTH, 5).add(Aspect.WATER, 2).add(Aspect.ORDER, 2));
        ThaumcraftApi.registerObjectTag("sand", new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5));
        ThaumcraftApi.registerObjectTag("grass", new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.GRASS_PATH), new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 2).add(Aspect.ORDER, 2));
        ThaumcraftApi.registerObjectTag("endstone", new AspectList().add(Aspect.EARTH, 5).add(Aspect.DARKNESS, 5));
        ThaumcraftApi.registerObjectTag("gravel", new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.MYCELIUM), new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 1).add(Aspect.FLUX, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.CLAY_BALL, 1, 32767), new AspectList().add(Aspect.WATER, 5).add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.HARDENED_CLAY, 1, 32767), new AspectList(new ItemStack(Blocks.CLAY)).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, 32767), new AspectList(new ItemStack(Blocks.CLAY)).add(Aspect.FIRE, 1).add(Aspect.SENSES, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.BRICK, 1, 32767), new AspectList(new ItemStack(Items.CLAY_BALL)).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag("netherrack", new AspectList().add(Aspect.EARTH, 5).add(Aspect.FIRE, 2));
        ThaumcraftApi.registerObjectTag("ingotBrickNether", new AspectList(new ItemStack(Blocks.NETHERRACK)).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.SOUL_SAND, 1, 32767), new AspectList().add(Aspect.EARTH, 3).add(Aspect.TRAP, 1).add(Aspect.SOUL, 3));
        ThaumcraftApi.registerObjectTag("blockGlass", new AspectList().add(Aspect.CRYSTAL, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.MOSSY_COBBLESTONE, 1, 32767), new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 3).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag("obsidian", new AspectList().add(Aspect.EARTH, 5).add(Aspect.FIRE, 5).add(Aspect.DARKNESS, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.STONEBRICK, 1, 1), new AspectList(new ItemStack(Blocks.STONEBRICK)).add(Aspect.PLANT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.STONEBRICK, 1, 2), new AspectList(new ItemStack(Blocks.STONEBRICK)).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.STONEBRICK, 1, 3), new AspectList(new ItemStack(Blocks.STONEBRICK)).add(Aspect.ORDER, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.SANDSTONE, 1, 1), new AspectList(new ItemStack(Blocks.SANDSTONE)).add(Aspect.ORDER, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.SANDSTONE, 1, 2), new AspectList(new ItemStack(Blocks.SANDSTONE)).add(Aspect.ORDER, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.CONCRETE, 1, 32767), new AspectList(new ItemStack(Blocks.CONCRETE_POWDER)).add(Aspect.WATER, 1).add(Aspect.ORDER, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.BLACK_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.BLUE_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.BROWN_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.CYAN_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.GRAY_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.GREEN_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.LIME_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.MAGENTA_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.ORANGE_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.PINK_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.PURPLE_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.RED_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.SILVER_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.WHITE_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.YELLOW_GLAZED_TERRACOTTA), new AspectList(new ItemStack(Blocks.STAINED_HARDENED_CLAY)).add(Aspect.SENSES, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag("logWood", new AspectList().add(Aspect.PLANT, 20));
        ThaumcraftApi.registerObjectTag("treeSapling", new AspectList().add(Aspect.PLANT, 15).add(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag("treeLeaves", new AspectList().add(Aspect.PLANT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.TALLGRASS, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.DOUBLE_PLANT, 1, 0), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1).add(Aspect.SENSES, 5).add(Aspect.LIFE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.DOUBLE_PLANT, 1, 1), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1).add(Aspect.SENSES, 5).add(Aspect.LIFE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.DOUBLE_PLANT, 1, 2), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.DOUBLE_PLANT, 1, 3), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.DOUBLE_PLANT, 1, 4), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1).add(Aspect.SENSES, 5).add(Aspect.LIFE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.DOUBLE_PLANT, 1, 5), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AIR, 1).add(Aspect.SENSES, 5).add(Aspect.LIFE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.WATERLILY, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.WATER, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.DEADBUSH, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag("vine", new AspectList().add(Aspect.PLANT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.WHEAT_SEEDS, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.MELON_SEEDS, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.PUMPKIN_SEEDS, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.BEETROOT_SEEDS, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 1));
        ThaumcraftApi.registerObjectTag("cropNetherWart", new AspectList().add(Aspect.PLANT, 1).add(Aspect.FLUX, 2).add(Aspect.ALCHEMY, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.RED_FLOWER, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 1).add(Aspect.SENSES, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.YELLOW_FLOWER, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 1).add(Aspect.SENSES, 5));
        ThaumcraftApi.registerObjectTag("blockCactus", new AspectList().add(Aspect.PLANT, 5).add(Aspect.WATER, 5).add(Aspect.AVERSION, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.BROWN_MUSHROOM), new AspectList().add(Aspect.PLANT, 5).add(Aspect.DARKNESS, 2).add(Aspect.EARTH, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.RED_MUSHROOM), new AspectList().add(Aspect.PLANT, 5).add(Aspect.DARKNESS, 2).add(Aspect.FIRE, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.DARKNESS, 2).add(Aspect.EARTH, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.RED_MUSHROOM_BLOCK, 1, 32767), new AspectList().add(Aspect.PLANT, 5).add(Aspect.DARKNESS, 2).add(Aspect.FIRE, 2));
        ThaumcraftApi.registerObjectTag("sugarcane", new AspectList().add(Aspect.PLANT, 5).add(Aspect.WATER, 3).add(Aspect.AIR, 2));
        ThaumcraftApi.registerObjectTag("cropWheat", new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.APPLE), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag("cropCarrot", new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 5).add(Aspect.SENSES, 5));
        ThaumcraftApi.registerObjectTag("cropPotato", new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 5).add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.BEETROOT), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 5).add(Aspect.DESIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.BAKED_POTATO), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.POISONOUS_POTATO), new AspectList().add(Aspect.PLANT, 5).add(Aspect.DEATH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.PUMPKIN), new AspectList().add(Aspect.PLANT, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.MELON_BLOCK, 1, 32767), new AspectList().add(Aspect.PLANT, 10).remove(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.MELON), new AspectList().add(Aspect.PLANT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.SPONGE, 1, 0), new AspectList().add(Aspect.EARTH, 5).add(Aspect.TRAP, 5).add(Aspect.VOID, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.SPONGE, 1, 1), new AspectList().add(Aspect.EARTH, 5).add(Aspect.TRAP, 5).add(Aspect.WATER, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.SUGAR), new AspectList().add(Aspect.DESIRE, 1).add(Aspect.ENERGY, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.CAKE), new AspectList().add(Aspect.DESIRE, 1).add(Aspect.LIFE, 2));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.PUMPKIN_PIE), new AspectList().add(Aspect.DESIRE, 1).add(Aspect.LIFE, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.WOOL), new AspectList().add(Aspect.BEAST, 15).add(Aspect.CRAFT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.EXPERIENCE_BOTTLE), new AspectList().add(Aspect.MIND, 20));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.NAME_TAG, 1, 32767), new AspectList().add(Aspect.MIND, 10).add(Aspect.BEAST, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.IRON_HORSE_ARMOR, 1, 32767), new AspectList().add(Aspect.METAL, 15).add(Aspect.PROTECT, 10).add(Aspect.BEAST, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.GOLDEN_HORSE_ARMOR, 1, 32767), new AspectList().add(Aspect.METAL, 10).add(Aspect.PROTECT, 15).add(Aspect.BEAST, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.DIAMOND_HORSE_ARMOR, 1, 32767), new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.PROTECT, 20).add(Aspect.BEAST, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.FIRE, 1, 32767), new AspectList().add(Aspect.FIRE, 20));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.MAGMA, 1, 32767), new AspectList().add(Aspect.FIRE, 10).add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.CHORUS_FLOWER, 1, 32767), new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.SENSES, 5).add(Aspect.PLANT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.CHORUS_PLANT, 1, 32767), new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.PLANT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.CHORUS_FRUIT), new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.SENSES, 5).add(Aspect.PLANT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.CHORUS_FRUIT_POPPED), new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.SENSES, 5).add(Aspect.PLANT, 4).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.ICE, 1, 32767), new AspectList().add(Aspect.COLD, 20));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.PACKED_ICE, 1, 32767), new AspectList().add(Aspect.COLD, 15).add(Aspect.ORDER, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SNOWBALL, 1, 32767), new AspectList().add(Aspect.COLD, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.COOKIE, 1, 32767), new AspectList().add(Aspect.DESIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.POTIONITEM), new AspectList().add(Aspect.WATER, 5).add(Aspect.CRYSTAL, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.TORCH, 1, 32767), new AspectList().add(Aspect.LIGHT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.WEB, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.BEAST, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.FLINT, 1, 32767), new AspectList().add(Aspect.EARTH, 5).add(Aspect.TOOL, 5));
        ThaumcraftApi.registerObjectTag("string", new AspectList().add(Aspect.BEAST, 5).add(Aspect.CRAFT, 1));
        ThaumcraftApi.registerObjectTag("slimeball", new AspectList().add(Aspect.WATER, 5).add(Aspect.LIFE, 5).add(Aspect.ALCHEMY, 1));
        ThaumcraftApi.registerObjectTag("leather", new AspectList().add(Aspect.BEAST, 5).add(Aspect.PROTECT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.ROTTEN_FLESH, 1, 32767), new AspectList().add(Aspect.MAN, 5).add(Aspect.LIFE, 5).add(Aspect.ENTROPY, 5));
        ThaumcraftApi.registerObjectTag("feather", new AspectList().add(Aspect.FLIGHT, 5).add(Aspect.AIR, 5));
        ThaumcraftApi.registerObjectTag("bone", new AspectList().add(Aspect.DEATH, 5).add(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag("egg", new AspectList().add(Aspect.LIFE, 5).add(Aspect.BEAST, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SPIDER_EYE, 1, 32767), new AspectList().add(Aspect.SENSES, 5).add(Aspect.BEAST, 5).add(Aspect.DEATH, 5));
        ThaumcraftApi.registerObjectTag("gunpowder", new AspectList().add(Aspect.FIRE, 10).add(Aspect.ENTROPY, 10).add(Aspect.ALCHEMY, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.FISH, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.WATER, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.COOKED_FISH, 1, 32767), new AspectList().add(Aspect.CRAFT, 1).add(Aspect.BEAST, 5).add(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.CHICKEN, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.AIR, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.COOKED_CHICKEN, 1, 32767), new AspectList().add(Aspect.CRAFT, 1).add(Aspect.BEAST, 5).add(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.PORKCHOP, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.COOKED_PORKCHOP, 1, 32767), new AspectList().add(Aspect.CRAFT, 1).add(Aspect.BEAST, 5).add(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.BEEF, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.COOKED_BEEF, 1, 32767), new AspectList().add(Aspect.CRAFT, 1).add(Aspect.BEAST, 5).add(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.MUTTON, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.COOKED_MUTTON, 1, 32767), new AspectList().add(Aspect.CRAFT, 1).add(Aspect.BEAST, 5).add(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RABBIT, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.COOKED_RABBIT, 1, 32767), new AspectList().add(Aspect.CRAFT, 1).add(Aspect.BEAST, 5).add(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RABBIT_HIDE, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.PROTECT, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RABBIT_FOOT, 1, 32767), new AspectList().add(Aspect.BEAST, 5).add(Aspect.PROTECT, 5).add(Aspect.MOTION, 10).add(Aspect.ALCHEMY, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.BLAZE_ROD, 1, 32767), new AspectList().add(Aspect.FIRE, 15).add(Aspect.ENERGY, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SADDLE, 1, 32767), new AspectList().add(Aspect.BEAST, 10).add(Aspect.MOTION, 10).add(Aspect.ORDER, 5));
        ThaumcraftApi.registerObjectTag("enderpearl", new AspectList().add(Aspect.ELDRITCH, 10).add(Aspect.MOTION, 15));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.GHAST_TEAR, 1, 32767), new AspectList().add(Aspect.UNDEAD, 5).add(Aspect.SOUL, 10).add(Aspect.ALCHEMY, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SKULL, 1, 0), new AspectList().add(Aspect.DEATH, 10).add(Aspect.SOUL, 10).add(Aspect.UNDEAD, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SKULL, 1, 1), new AspectList().add(Aspect.DEATH, 10).add(Aspect.SOUL, 10).add(Aspect.UNDEAD, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SKULL, 1, 2), new AspectList().add(Aspect.DEATH, 10).add(Aspect.SOUL, 10).add(Aspect.MAN, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SKULL, 1, 3), new AspectList().add(Aspect.DEATH, 10).add(Aspect.SOUL, 10).add(Aspect.MAN, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SKULL, 1, 4), new AspectList().add(Aspect.DEATH, 10).add(Aspect.SOUL, 10).add(Aspect.ENTROPY, 5).add(Aspect.FIRE, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SKULL, 1, 5), new AspectList().add(Aspect.DEATH, 10).add(Aspect.SOUL, 10).add(Aspect.FIRE, 10).add(Aspect.DARKNESS, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.DRAGON_BREATH), new AspectList().add(Aspect.DARKNESS, 10).add(Aspect.ENTROPY, 10).add(Aspect.FIRE, 10).add(Aspect.ALCHEMY, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.CAULDRON), new AspectList().add(Aspect.ALCHEMY, 15));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.FERMENTED_SPIDER_EYE), new AspectList().add(Aspect.ALCHEMY, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.BLAZE_POWDER), new AspectList().add(Aspect.ALCHEMY, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.SPECKLED_MELON), new AspectList().add(Aspect.ALCHEMY, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.MAGMA_CREAM), new AspectList().add(Aspect.ALCHEMY, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.TOTEM_OF_UNDYING), new AspectList().add(Aspect.ORDER, 10).add(Aspect.ENTROPY, 10).add(Aspect.LIFE, 25).add(Aspect.UNDEAD, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.SHULKER_SHELL), new AspectList().add(Aspect.PROTECT, 10).add(Aspect.ELDRITCH, 5).add(Aspect.BEAST, 5).add(Aspect.VOID, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.BLACK_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.BLUE_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.GREEN_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.RED_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.YELLOW_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.SILVER_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.WHITE_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.ORANGE_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.PINK_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.GRAY_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.CYAN_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.LIGHT_BLUE_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.LIME_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.MAGENTA_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.BROWN_SHULKER_BOX), new AspectList(new ItemStack(Blocks.PURPLE_SHULKER_BOX)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RECORD_11), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.DESIRE, 15));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RECORD_13), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.WATER, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RECORD_CAT), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.BEAST, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RECORD_CHIRP), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.EARTH, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RECORD_FAR), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.ELDRITCH, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RECORD_MALL), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.MAN, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RECORD_MELLOHI), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.CRAFT, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RECORD_STAL), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.DARKNESS, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RECORD_STRAD), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.ENERGY, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RECORD_WARD), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.LIFE, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RECORD_BLOCKS), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.TOOL, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.RECORD_WAIT), new AspectList().add(Aspect.SENSES, 15).add(Aspect.AIR, 5).add(Aspect.TRAP, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag("netherStar", new AspectList().add(Aspect.ELDRITCH, 10).add(Aspect.MAGIC, 20).add(Aspect.ORDER, 20).add(Aspect.AURA, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.CHAINMAIL_HELMET, 1, 32767), new AspectList().add(Aspect.METAL, 42));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.CHAINMAIL_CHESTPLATE, 1, 32767), new AspectList().add(Aspect.METAL, 67));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.CHAINMAIL_LEGGINGS, 1, 32767), new AspectList().add(Aspect.METAL, 58));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.CHAINMAIL_BOOTS, 1, 32767), new AspectList().add(Aspect.METAL, 33));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.PAPER), new AspectList().add(Aspect.MIND, 2));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.ENCHANTED_BOOK), new AspectList(new ItemStack(Items.BOOK)));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.BOOKSHELF), new AspectList().add(Aspect.MIND, 8));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.DRAGON_EGG), new AspectList().add(Aspect.ELDRITCH, 15).add(Aspect.BEAST, 15).add(Aspect.DARKNESS, 15).add(Aspect.MOTION, 15).add(Aspect.MAGIC, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.PORTAL, 1, 32767), new AspectList().add(Aspect.FIRE, 10).add(Aspect.MOTION, 20).add(Aspect.MAGIC, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.END_PORTAL, 1, 32767), new AspectList().add(Aspect.ELDRITCH, 10).add(Aspect.MOTION, 20).add(Aspect.MAGIC, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.END_PORTAL_FRAME, 1, 32767), new AspectList().add(Aspect.ELDRITCH, 10).add(Aspect.ENERGY, 10).add(Aspect.MOTION, 10).add(Aspect.MAGIC, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.MOB_SPAWNER, 1, 32767), new AspectList().add(Aspect.BEAST, 20).add(Aspect.MOTION, 20).add(Aspect.UNDEAD, 20).add(Aspect.MAGIC, 20));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.PRISMARINE_SHARD), new AspectList().add(Aspect.WATER, 5).add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.PRISMARINE_CRYSTALS), new AspectList().add(Aspect.WATER, 5).add(Aspect.CRYSTAL, 5).add(Aspect.LIGHT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.ELYTRA), new AspectList().add(Aspect.FLIGHT, 20).add(Aspect.MOTION, 15));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.END_ROD, 1, 32767), new AspectList().add(Aspect.FIRE, 1).add(Aspect.LIGHT, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.CHEST, 1, 32767), new AspectList().add(Aspect.VOID, 15));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.TRAPPED_CHEST, 1, 32767), new AspectList().add(Aspect.TRAP, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.ENDER_EYE), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MAGIC, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.ARROW), new AspectList().add(Aspect.AVERSION, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.GLASS_BOTTLE), new AspectList().add(Aspect.VOID, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.GOLDEN_APPLE, 1, 0), new AspectList().add(Aspect.MAGIC, 5).add(Aspect.LIFE, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.GOLDEN_APPLE, 1, 1), new AspectList().add(Aspect.MAGIC, 5).add(Aspect.LIFE, 15).add(Aspect.PROTECT, 15));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.BOWL), new AspectList().add(Aspect.VOID, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.MUSHROOM_STEW), new AspectList().add(Aspect.LIFE, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.MINECART), new AspectList().add(Aspect.MOTION, 15));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.IRON_DOOR), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.ACACIA_DOOR), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.DARK_OAK_DOOR), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.JUNGLE_DOOR), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.OAK_DOOR), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.SPRUCE_DOOR), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.BIRCH_DOOR), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.BOAT), new AspectList().add(Aspect.WATER, 10).add(Aspect.MOTION, 15));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.ACACIA_BOAT), new AspectList().add(Aspect.WATER, 10).add(Aspect.MOTION, 15));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.BIRCH_BOAT), new AspectList().add(Aspect.WATER, 10).add(Aspect.MOTION, 15));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.DARK_OAK_BOAT), new AspectList().add(Aspect.WATER, 10).add(Aspect.MOTION, 15));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.JUNGLE_BOAT), new AspectList().add(Aspect.WATER, 10).add(Aspect.MOTION, 15));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.SPRUCE_BOAT), new AspectList().add(Aspect.WATER, 10).add(Aspect.MOTION, 15));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.FLINT_AND_STEEL, 1, 32767), new AspectList().add(Aspect.FIRE, 10).add(Aspect.TOOL, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.FISHING_ROD, 1, 32767), new AspectList().add(Aspect.WATER, 10).add(Aspect.TOOL, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.SHIELD, 1, 32767), new AspectList().add(Aspect.PROTECT, 20));
        for (int a = 0; a < 16; ++a) {
            ItemStack sis = new ItemStack(Items.SHIELD, 1, 32767);
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("Base", a);
            sis.setTagInfo("BlockEntityTag", nbttagcompound);
            ThaumcraftApi.registerComplexObjectTag(sis, new AspectList().merge(Aspect.PROTECT, 20));
        }
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.SPECTRAL_ARROW, 1, 32767), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MAGIC, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.BUCKET), new AspectList().add(Aspect.VOID, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ForgeModContainer.getInstance().universalBucket), new AspectList(new ItemStack(Items.BUCKET)));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.WATER_BUCKET), new AspectList(new ItemStack(Items.BUCKET)).add(Aspect.WATER, 20));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.LAVA_BUCKET), new AspectList(new ItemStack(Items.BUCKET)).add(Aspect.FIRE, 15).add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.MILK_BUCKET), new AspectList(new ItemStack(Items.BUCKET)).add(Aspect.LIFE, 10).add(Aspect.BEAST, 5).add(Aspect.WATER, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.BREWING_STAND), new AspectList().add(Aspect.CRAFT, 15).add(Aspect.ALCHEMY, 25));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.STONE_BUTTON), new AspectList().add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.RAIL, 1, 32767), new AspectList().add(Aspect.MOTION, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.DETECTOR_RAIL, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5).add(Aspect.SENSES, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.GOLDEN_RAIL, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5).add(Aspect.ENERGY, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.ACTIVATOR_RAIL, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.ACACIA_FENCE_GATE, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.DARK_OAK_FENCE_GATE, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.JUNGLE_FENCE_GATE, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.DARK_OAK_FENCE_GATE, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.SPRUCE_FENCE_GATE, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.BIRCH_FENCE_GATE, 1, 32767), new AspectList().add(Aspect.TRAP, 5).add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5).add(Aspect.SENSES, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.STONE_PRESSURE_PLATE, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5).add(Aspect.SENSES, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5).add(Aspect.SENSES, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5).add(Aspect.SENSES, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.LEVER, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.PISTON, 1, 32767), new AspectList().add(Aspect.MECHANISM, 10).add(Aspect.MOTION, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.STICKY_PISTON, 1, 32767), new AspectList().add(Aspect.MECHANISM, 10).add(Aspect.MOTION, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.JUKEBOX), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MECHANISM, 10).add(Aspect.AIR, 15));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.NOTEBLOCK), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MECHANISM, 10).add(Aspect.AIR, 15));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.TRAPDOOR, 1, 32767), new AspectList().add(Aspect.MOTION, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.FURNACE, 1, 32767), new AspectList().add(Aspect.FIRE, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.ENCHANTING_TABLE), new AspectList().add(Aspect.MAGIC, 25).add(Aspect.CRAFT, 15));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.CRAFTING_TABLE), new AspectList().add(Aspect.CRAFT, 20));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.CLOCK), new AspectList().add(Aspect.MECHANISM, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.BEACON), new AspectList().add(Aspect.AURA, 10).add(Aspect.MAGIC, 10).add(Aspect.EXCHANGE, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.WOODEN_BUTTON, 1, 32767), new AspectList().add(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.CARROT_ON_A_STICK, 1, 32767), new AspectList().add(Aspect.MOTION, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.FLOWER_POT), new AspectList().add(Aspect.VOID, 5).add(Aspect.PLANT, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.GOLDEN_CARROT), new AspectList().add(Aspect.SENSES, 10).add(Aspect.ALCHEMY, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.ENDER_CHEST, 1, 32767), new AspectList().merge(Aspect.EXCHANGE, 10).merge(Aspect.MOTION, 10).merge(Aspect.VOID, 20));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.COMPARATOR, 1, 32767), new AspectList().merge(Aspect.MECHANISM, 15).merge(Aspect.ORDER, 5).merge(Aspect.SENSES, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Items.REPEATER, 1, 32767), new AspectList().merge(Aspect.MECHANISM, 15).merge(Aspect.ENERGY, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.HOPPER, 1, 32767), new AspectList().merge(Aspect.MECHANISM, 5).merge(Aspect.EXCHANGE, 10).merge(Aspect.VOID, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.DROPPER, 1, 32767), new AspectList().merge(Aspect.MECHANISM, 5).merge(Aspect.EXCHANGE, 10).merge(Aspect.VOID, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.DISPENSER, 1, 32767), new AspectList().merge(Aspect.MECHANISM, 5).merge(Aspect.EXCHANGE, 10).merge(Aspect.VOID, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.TRIPWIRE_HOOK, 1, 32767), new AspectList().add(Aspect.SENSES, 5).add(Aspect.MECHANISM, 5).add(Aspect.TRAP, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.TRIPWIRE, 1, 32767), new AspectList().merge(Aspect.SENSES, 5).merge(Aspect.MECHANISM, 5).merge(Aspect.TRAP, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(Blocks.DAYLIGHT_DETECTOR, 1, 32767), new AspectList().merge(Aspect.SENSES, 10).merge(Aspect.LIGHT, 10).merge(Aspect.MECHANISM, 5));
        ThaumcraftApi.registerComplexObjectTag("gear*", new AspectList().add(Aspect.MECHANISM, 5));
        for (PotionType potiontype : PotionType.REGISTRY) {
            ItemStack stack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), potiontype);
            ThaumcraftApi.registerObjectTag(stack, getPotionAspects(stack).add(Aspect.WATER, 5));
            ItemStack stack2 = PotionUtils.addPotionToItemStack(new ItemStack(Items.TIPPED_ARROW), potiontype);
            ThaumcraftApi.registerObjectTag(stack2, getPotionAspects(stack2).add(Aspect.AVERSION, 5));
            ItemStack stack3 = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potiontype);
            ThaumcraftApi.registerObjectTag(stack3, getPotionAspects(stack3).add(Aspect.ENERGY, 5));
            ItemStack stack4 = PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), potiontype);
            ThaumcraftApi.registerObjectTag(stack4, getPotionAspects(stack4).add(Aspect.TRAP, 5));
        }
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.DYE, 1, 0), new AspectList().add(Aspect.WATER, 2).add(Aspect.BEAST, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.DYE, 1, 2), new AspectList(new ItemStack(Blocks.CACTUS)).add(Aspect.WATER, 1).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.DYE, 1, 3), new AspectList().add(Aspect.DESIRE, 2).add(Aspect.ENERGY, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.DYE, 1, 4), new AspectList().add(Aspect.EARTH, 2).add(Aspect.DESIRE, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.DYE, 1, 15), new AspectList().add(Aspect.LIFE, 2).add(Aspect.DEATH, 1).add(Aspect.PLANT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.phial, 1, 0), new AspectList().add(Aspect.VOID, 3));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.phial, 1, 1), new AspectList());
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.grassAmbient), new AspectList(new ItemStack(Blocks.GRASS)).add(Aspect.LIGHT, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(BlocksTC.tableWood), new AspectList().add(Aspect.TOOL, 1));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(BlocksTC.tableStone), new AspectList().add(Aspect.TOOL, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.arcaneWorkbench), new AspectList(new ItemStack(Blocks.CRAFTING_TABLE)).add(Aspect.MAGIC, 5).add(Aspect.AURA, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.tripleMeatTreat), new AspectList().add(Aspect.LIFE, 10).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(FluidUtil.getFilledBucket(new FluidStack(ConfigBlocks.FluidPure.instance, 1000)), new AspectList(new ItemStack(Items.BUCKET)).add(Aspect.MIND, 15).add(Aspect.ORDER, 15));
        ThaumcraftApi.registerObjectTag(FluidUtil.getFilledBucket(new FluidStack(ConfigBlocks.FluidDeath.instance, 1000)), new AspectList(new ItemStack(Items.BUCKET)).add(Aspect.DEATH, 15).add(Aspect.ENTROPY, 15));
        ThaumcraftApi.registerObjectTag("clusterIron", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag("clusterGold", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5).add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag("clusterCinnabar", new AspectList().add(Aspect.ORDER, 5).add(Aspect.METAL, 15).add(Aspect.EARTH, 5).add(Aspect.ALCHEMY, 5).add(Aspect.DEATH, 5));
        ThaumcraftApi.registerObjectTag("clusterQuartz", new AspectList().add(Aspect.ORDER, 5).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag("oreCinnabar", new AspectList().add(Aspect.EARTH, 5).add(Aspect.METAL, 10).add(Aspect.ALCHEMY, 5).add(Aspect.DEATH, 5));
        ThaumcraftApi.registerObjectTag("oreAmber", new AspectList().add(Aspect.EARTH, 5).add(Aspect.TRAP, 10).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag("quicksilver", new AspectList().add(Aspect.METAL, 10).add(Aspect.DEATH, 5).add(Aspect.ALCHEMY, 5));
        ThaumcraftApi.registerObjectTag("gemAmber", new AspectList().add(Aspect.TRAP, 10).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.nuggets, 1, 10), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ORDER, 5).add(Aspect.METAL, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalAir, 1, 32767), new AspectList().add(Aspect.AIR, 15).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalFire, 1, 32767), new AspectList().add(Aspect.FIRE, 15).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalWater, 1, 32767), new AspectList().add(Aspect.WATER, 15).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalEarth, 1, 32767), new AspectList().add(Aspect.EARTH, 15).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalOrder, 1, 32767), new AspectList().add(Aspect.ORDER, 15).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalEntropy, 1, 32767), new AspectList().add(Aspect.ENTROPY, 15).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crystalTaint, 1, 32767), new AspectList().add(Aspect.FLUX, 15).add(Aspect.CRYSTAL, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.taintFibre), new AspectList().add(Aspect.PLANT, 5).add(Aspect.FLUX, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.taintCrust), new AspectList().add(Aspect.LIFE, 5).add(Aspect.FLUX, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.taintSoil), new AspectList().add(Aspect.EARTH, 5).add(Aspect.FLUX, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.taintGeyser), new AspectList().add(Aspect.AURA, 5).add(Aspect.WATER, 5).add(Aspect.FLUX, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.taintRock), new AspectList().add(Aspect.EARTH, 10).add(Aspect.FLUX, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.taintFeature, 1, 0), new AspectList().add(Aspect.AURA, 5).add(Aspect.BEAST, 5).add(Aspect.FLUX, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.taintLog, 1, 0), new AspectList().add(Aspect.PLANT, 5).add(Aspect.FLUX, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.logGreatwood), new AspectList().add(Aspect.PLANT, 20).add(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.logSilverwood), new AspectList().add(Aspect.PLANT, 20).add(Aspect.AURA, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.leafGreatwood), new AspectList().add(Aspect.PLANT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.leafSilverwood), new AspectList().add(Aspect.PLANT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.saplingGreatwood), new AspectList().add(Aspect.PLANT, 15).add(Aspect.LIFE, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.saplingSilverwood), new AspectList().add(Aspect.PLANT, 15).add(Aspect.AURA, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.shimmerleaf), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AURA, 10).add(Aspect.ENERGY, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.cinderpearl), new AspectList().add(Aspect.PLANT, 5).add(Aspect.AURA, 5).add(Aspect.FIRE, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.vishroom), new AspectList().add(Aspect.PLANT, 2).add(Aspect.DEATH, 1).add(Aspect.MAGIC, 1).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.stoneAncient), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ELDRITCH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.stoneAncientTile), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ELDRITCH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.stoneAncientRock), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ELDRITCH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.stoneEldritchTile), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ELDRITCH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.stoneAncientDoorway), new AspectList().add(Aspect.METAL, 5).add(Aspect.ELDRITCH, 5).add(Aspect.TRAP, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.stoneAncientGlyphed), new AspectList().add(Aspect.METAL, 5).add(Aspect.ELDRITCH, 5).add(Aspect.MIND, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.stonePorous), new AspectList().add(Aspect.EARTH, 5).add(Aspect.VOID, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.researchTable, 1, 32767), new AspectList(new ItemStack(BlocksTC.tableWood)).add(Aspect.MIND, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.brain), new AspectList().add(Aspect.LIFE, 5).add(Aspect.MIND, 20).add(Aspect.UNDEAD, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.lootBag, 1, 0), new AspectList().add(Aspect.DESIRE, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.lootBag, 1, 1), new AspectList().add(Aspect.DESIRE, 20));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.lootBag, 1, 2), new AspectList().add(Aspect.DESIRE, 30));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.lootUrnCommon), new AspectList().add(Aspect.DESIRE, 10).add(Aspect.EARTH, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.lootUrnUncommon), new AspectList().add(Aspect.DESIRE, 20).add(Aspect.EARTH, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.lootUrnRare), new AspectList().add(Aspect.DESIRE, 30).add(Aspect.EARTH, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.lootCrateCommon), new AspectList().add(Aspect.DESIRE, 10).add(Aspect.PLANT, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.lootCrateUncommon), new AspectList().add(Aspect.DESIRE, 20).add(Aspect.PLANT, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.lootCrateRare), new AspectList().add(Aspect.DESIRE, 30).add(Aspect.PLANT, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.chunks, 1, 32767), new AspectList().add(Aspect.LIFE, 5).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.salisMundus), new AspectList().add(Aspect.MAGIC, 5).add(Aspect.ENERGY, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.crucible), new AspectList(new ItemStack(Items.CAULDRON, 1, 32767)).add(Aspect.CRAFT, 20).add(Aspect.ALCHEMY, 20));
        for (Block ca : BlocksTC.candles.values()) {
            ThaumcraftApi.registerComplexObjectTag(new ItemStack(ca), new AspectList().add(Aspect.LIGHT, 5));
        }
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.thaumonomicon, 1, 32767), new AspectList(new ItemStack(Blocks.BOOKSHELF)).merge(Aspect.MAGIC, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(BlocksTC.pedestalArcane, 1, 0), new AspectList().add(Aspect.MAGIC, 3).add(Aspect.AIR, 3));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(BlocksTC.pedestalAncient, 1, 1), new AspectList().add(Aspect.MAGIC, 3).add(Aspect.ELDRITCH, 3));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(BlocksTC.pedestalEldritch, 1, 2), new AspectList().add(Aspect.MAGIC, 3).add(Aspect.ELDRITCH, 3));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(ItemsTC.thaumometer, 1, 32767), new AspectList().add(Aspect.SENSES, 10).add(Aspect.AURA, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(ItemsTC.goggles, 1, 32767), new AspectList().merge(Aspect.SENSES, 10).merge(Aspect.AURA, 10));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(BlocksTC.arcaneEar), new AspectList().add(Aspect.SENSES, 20));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.amuletVis, 1, 0), new AspectList().add(Aspect.AURA, 20).add(Aspect.METAL, 5).add(Aspect.MAGIC, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.baubles, 1, 3), new AspectList().add(Aspect.AURA, 5).add(Aspect.METAL, 5).add(Aspect.MAGIC, 20));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.crimsonPlateChest, 1, 32767), new AspectList(new ItemStack(Items.IRON_CHESTPLATE)).add(Aspect.ELDRITCH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.crimsonPraetorChest, 1, 32767), new AspectList(new ItemStack(Items.IRON_CHESTPLATE)).add(Aspect.ELDRITCH, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.crimsonRobeChest, 1, 32767), new AspectList(new ItemStack(Items.LEATHER_CHESTPLATE)).add(Aspect.MAGIC, 5).add(Aspect.ELDRITCH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.crimsonPlateLegs, 1, 32767), new AspectList(new ItemStack(Items.IRON_LEGGINGS)).add(Aspect.ELDRITCH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.crimsonPraetorLegs, 1, 32767), new AspectList(new ItemStack(Items.IRON_LEGGINGS)).add(Aspect.ELDRITCH, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.crimsonRobeLegs, 1, 32767), new AspectList(new ItemStack(Items.LEATHER_LEGGINGS)).add(Aspect.MAGIC, 5).add(Aspect.ELDRITCH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.crimsonPlateHelm, 1, 32767), new AspectList(new ItemStack(Items.IRON_HELMET)).add(Aspect.ELDRITCH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.crimsonPraetorHelm, 1, 32767), new AspectList(new ItemStack(Items.IRON_HELMET)).add(Aspect.ELDRITCH, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.crimsonRobeHelm, 1, 32767), new AspectList(new ItemStack(Items.LEATHER_HELMET)).add(Aspect.MAGIC, 5).add(Aspect.ELDRITCH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.crimsonBoots, 1, 32767), new AspectList(new ItemStack(Items.IRON_BOOTS)).add(Aspect.ELDRITCH, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.crimsonBlade, 1, 32767), new AspectList(new ItemStack(Items.IRON_SWORD)).add(Aspect.ELDRITCH, 10).add(Aspect.DEATH, 10));
        for (Block ca : BlocksTC.banners.values()) {
            ThaumcraftApi.registerComplexObjectTag(new ItemStack(ca), new AspectList().add(Aspect.ELDRITCH, 5));
        }
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.eldritchEye), new AspectList().add(Aspect.ELDRITCH, 15).add(Aspect.AURA, 15).add(Aspect.SENSES, 15).add(Aspect.SOUL, 15));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.curio, 1, 0), new AspectList().add(Aspect.MIND, 15).add(Aspect.MAGIC, 15));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.curio, 1, 1), new AspectList().add(Aspect.MIND, 15).add(Aspect.BEAST, 15));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.curio, 1, 2), new AspectList().add(Aspect.MIND, 15).add(Aspect.DEATH, 15));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.curio, 1, 3), new AspectList().add(Aspect.MIND, 15).add(Aspect.ELDRITCH, 15));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.curio, 1, 4), new AspectList().add(Aspect.MIND, 30));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.curio, 1, 5), new AspectList().add(Aspect.MIND, 15).add(Aspect.FLUX, 15));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.curio, 1, 6), new AspectList().add(Aspect.MIND, 15).add(Aspect.ELDRITCH, 5).add(Aspect.SOUL, 5).add(Aspect.MAGIC, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.runedTablet), new AspectList().add(Aspect.TRAP, 15).add(Aspect.MIND, 15).add(Aspect.MECHANISM, 15));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.celestialNotes, 1, 32767), new AspectList().add(Aspect.MIND, 5).add(Aspect.DARKNESS, 5).add(Aspect.LIGHT, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(ItemsTC.primordialPearl, 1, 32767), new AspectList().add(Aspect.AIR, 10).add(Aspect.FIRE, 10).add(Aspect.WATER, 10).add(Aspect.EARTH, 10).add(Aspect.ORDER, 10).add(Aspect.ENTROPY, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.eldritch, 1, 0), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.eldritch, 1, 1), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.eldritch, 1, 2), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.eldritch, 1, 3), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.eldritch, 1, 4), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10).add(Aspect.MECHANISM, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.eldritch, 1, 5), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.eldritch, 1, 6), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10).add(Aspect.MOTION, 15));
        ThaumcraftApi.registerObjectTag(new ItemStack(BlocksTC.eldritch, 1, 7), new AspectList().add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 10).add(Aspect.BEAST, 15));
    }
~~~

### src/main/java/thaumcraft/common/config/ConfigBlocks.java

~~~java
public static void initBlocks(IForgeRegistry<Block> iForgeRegistry) {
        BlocksTC.oreAmber = registerBlock(new BlockOreTC("ore_amber").setHardness(1.5f));
        BlocksTC.oreCinnabar = registerBlock(new BlockOreTC("ore_cinnabar").setHardness(2.0f));
        BlocksTC.oreQuartz = registerBlock(new BlockOreTC("ore_quartz").setHardness(3.0f));
        BlocksTC.crystalAir = registerBlock(new BlockCrystal("crystal_aer", Aspect.AIR));
        BlocksTC.crystalFire = registerBlock(new BlockCrystal("crystal_ignis", Aspect.FIRE));
        BlocksTC.crystalWater = registerBlock(new BlockCrystal("crystal_aqua", Aspect.WATER));
        BlocksTC.crystalEarth = registerBlock(new BlockCrystal("crystal_terra", Aspect.EARTH));
        BlocksTC.crystalOrder = registerBlock(new BlockCrystal("crystal_ordo", Aspect.ORDER));
        BlocksTC.crystalEntropy = registerBlock(new BlockCrystal("crystal_perditio", Aspect.ENTROPY));
        BlocksTC.crystalTaint = registerBlock(new BlockCrystal("crystal_vitium", Aspect.FLUX));
        ShardType.AIR.setOre(BlocksTC.crystalAir);
        ShardType.FIRE.setOre(BlocksTC.crystalFire);
        ShardType.WATER.setOre(BlocksTC.crystalWater);
        ShardType.EARTH.setOre(BlocksTC.crystalEarth);
        ShardType.ORDER.setOre(BlocksTC.crystalOrder);
        ShardType.ENTROPY.setOre(BlocksTC.crystalEntropy);
        ShardType.FLUX.setOre(BlocksTC.crystalTaint);
        BlocksTC.stoneArcane = registerBlock(new BlockStoneTC("stone_arcane", true));
        BlocksTC.stoneArcaneBrick = registerBlock(new BlockStoneTC("stone_arcane_brick", true));
        BlocksTC.stoneAncient = registerBlock(new BlockStoneTC("stone_ancient", true));
        BlocksTC.stoneAncientTile = registerBlock(new BlockStoneTC("stone_ancient_tile", false));
        BlocksTC.stoneAncientRock = registerBlock(new BlockStoneTC("stone_ancient_rock", false).setHardness(-1.0f));
        BlocksTC.stoneAncientGlyphed = registerBlock(new BlockStoneTC("stone_ancient_glyphed", false));
        BlocksTC.stoneAncientDoorway = registerBlock(new BlockStoneTC("stone_ancient_doorway", false).setHardness(-1.0f));
        BlocksTC.stoneEldritchTile = registerBlock(new BlockStoneTC("stone_eldritch_tile", true).setHardness(15.0f).setResistance(1000.0f));
        BlocksTC.stonePorous = registerBlock(new BlockStonePorous());
        BlocksTC.stairsArcane = registerBlock(new BlockStairsTC("stairs_arcane", BlocksTC.stoneArcane.getDefaultState()));
        BlocksTC.stairsArcaneBrick = registerBlock(new BlockStairsTC("stairs_arcane_brick", BlocksTC.stoneArcaneBrick.getDefaultState()));
        BlocksTC.stairsAncient = registerBlock(new BlockStairsTC("stairs_ancient", BlocksTC.stoneAncient.getDefaultState()));
        BlocksTC.slabArcaneStone = (BlockSlab)new BlockSlabTC.Half("slab_arcane_stone", null, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.doubleSlabArcaneStone = (BlockSlab)new BlockSlabTC.Double("slab_double_arcane_stone", BlocksTC.slabArcaneStone, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.slabArcaneBrick = (BlockSlab)new BlockSlabTC.Half("slab_arcane_brick", null, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.doubleSlabArcaneBrick = (BlockSlab)new BlockSlabTC.Double("slab_double_arcane_brick", BlocksTC.slabArcaneBrick, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.slabAncient = (BlockSlab)new BlockSlabTC.Half("slab_ancient", null, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.doubleSlabAncient = (BlockSlab)new BlockSlabTC.Double("slab_double_ancient", BlocksTC.slabAncient, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.slabEldritch = (BlockSlab)new BlockSlabTC.Half("slab_eldritch", null, false).setHardness(2.0f).setResistance(10.0f);
        BlocksTC.doubleSlabEldritch = (BlockSlab)new BlockSlabTC.Double("slab_double_eldritch", BlocksTC.slabEldritch, false).setHardness(2.0f).setResistance(10.0f);
        ForgeRegistries.BLOCKS.register(BlocksTC.slabArcaneStone);
        ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabArcaneStone);
        ForgeRegistries.BLOCKS.register(BlocksTC.slabArcaneBrick);
        ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabArcaneBrick);
        ForgeRegistries.BLOCKS.register(BlocksTC.slabAncient);
        ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabAncient);
        ForgeRegistries.BLOCKS.register(BlocksTC.slabEldritch);
        ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabEldritch);
        ForgeRegistries.ITEMS.register(new ItemSlab(BlocksTC.slabArcaneStone, BlocksTC.slabArcaneStone, BlocksTC.doubleSlabArcaneStone).setRegistryName(BlocksTC.slabArcaneStone.getRegistryName()));
        ForgeRegistries.ITEMS.register(new ItemSlab(BlocksTC.slabArcaneBrick, BlocksTC.slabArcaneBrick, BlocksTC.doubleSlabArcaneBrick).setRegistryName(BlocksTC.slabArcaneBrick.getRegistryName()));
        ForgeRegistries.ITEMS.register(new ItemSlab(BlocksTC.slabAncient, BlocksTC.slabAncient, BlocksTC.doubleSlabAncient).setRegistryName(BlocksTC.slabAncient.getRegistryName()));
        ForgeRegistries.ITEMS.register(new ItemSlab(BlocksTC.slabEldritch, BlocksTC.slabEldritch, BlocksTC.doubleSlabEldritch).setRegistryName(BlocksTC.slabEldritch.getRegistryName()));
        BlocksTC.saplingGreatwood = registerBlock(new BlockSaplingTC("sapling_greatwood"));
        BlocksTC.saplingSilverwood = registerBlock(new BlockSaplingTC("sapling_silverwood"));
        BlocksTC.logGreatwood = registerBlock(new BlockLogsTC("log_greatwood"));
        BlocksTC.logSilverwood = registerBlock(new BlockLogsTC("log_silverwood"));
        BlocksTC.leafGreatwood = registerBlock(new BlockLeavesTC("leaves_greatwood"));
        BlocksTC.leafSilverwood = registerBlock(new BlockLeavesTC("leaves_silverwood"));
        BlocksTC.shimmerleaf = registerBlock(new BlockPlantShimmerleaf());
        BlocksTC.cinderpearl = registerBlock(new BlockPlantCinderpearl());
        BlocksTC.vishroom = registerBlock(new BlockPlantVishroom());
        BlocksTC.plankGreatwood = registerBlock(new BlockPlanksTC("plank_greatwood"));
        BlocksTC.plankSilverwood = registerBlock(new BlockPlanksTC("plank_silverwood"));
        BlocksTC.stairsGreatwood = registerBlock(new BlockStairsTC("stairs_greatwood", BlocksTC.plankGreatwood.getDefaultState()));
        BlocksTC.stairsSilverwood = registerBlock(new BlockStairsTC("stairs_silverwood", BlocksTC.plankSilverwood.getDefaultState()));
        BlocksTC.slabGreatwood = (BlockSlab)new BlockSlabTC.Half("slab_greatwood", null, true).setHardness(1.2f).setResistance(2.0f);
        BlocksTC.doubleSlabGreatwood = (BlockSlab)new BlockSlabTC.Double("slab_double_greatwood", BlocksTC.slabGreatwood, true).setHardness(1.2f).setResistance(2.0f);
        BlocksTC.slabSilverwood = (BlockSlab)new BlockSlabTC.Half("slab_silverwood", null, true).setHardness(1.0f).setResistance(2.0f);
        BlocksTC.doubleSlabSilverwood = (BlockSlab)new BlockSlabTC.Double("slab_double_silverwood", BlocksTC.slabSilverwood, true).setHardness(1.0f).setResistance(2.0f);
        ForgeRegistries.BLOCKS.register(BlocksTC.slabGreatwood);
        ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabGreatwood);
        ForgeRegistries.BLOCKS.register(BlocksTC.slabSilverwood);
        ForgeRegistries.BLOCKS.register(BlocksTC.doubleSlabSilverwood);
        ForgeRegistries.ITEMS.register(new ItemSlab(BlocksTC.slabGreatwood, BlocksTC.slabGreatwood, BlocksTC.doubleSlabGreatwood).setRegistryName(BlocksTC.slabGreatwood.getRegistryName()));
        ForgeRegistries.ITEMS.register(new ItemSlab(BlocksTC.slabSilverwood, BlocksTC.slabSilverwood, BlocksTC.doubleSlabSilverwood).setRegistryName(BlocksTC.slabSilverwood.getRegistryName()));
        BlocksTC.amberBlock = registerBlock(new BlockTranslucent("amber_block"));
        BlocksTC.amberBrick = registerBlock(new BlockTranslucent("amber_brick"));
        BlocksTC.fleshBlock = registerBlock(new BlockFlesh());
        BlocksTC.lootCrateCommon = registerBlock(new BlockLoot(Material.WOOD, "loot_crate_common", BlockLoot.LootType.COMMON));
        BlocksTC.lootCrateUncommon = registerBlock(new BlockLoot(Material.WOOD, "loot_crate_uncommon", BlockLoot.LootType.UNCOMMON));
        BlocksTC.lootCrateRare = registerBlock(new BlockLoot(Material.WOOD, "loot_crate_rare", BlockLoot.LootType.RARE));
        BlocksTC.lootUrnCommon = registerBlock(new BlockLoot(Material.ROCK, "loot_urn_common", BlockLoot.LootType.COMMON));
        BlocksTC.lootUrnUncommon = registerBlock(new BlockLoot(Material.ROCK, "loot_urn_uncommon", BlockLoot.LootType.UNCOMMON));
        BlocksTC.lootUrnRare = registerBlock(new BlockLoot(Material.ROCK, "loot_urn_rare", BlockLoot.LootType.RARE));
        BlocksTC.taintFibre = registerBlock(new BlockTaintFibre());
        BlocksTC.taintCrust = registerBlock(new BlockTaint("taint_crust"));
        BlocksTC.taintSoil = registerBlock(new BlockTaint("taint_soil"));
        BlocksTC.taintRock = registerBlock(new BlockTaint("taint_rock"));
        BlocksTC.taintGeyser = registerBlock(new BlockTaint("taint_geyser"));
        BlocksTC.taintFeature = registerBlock(new BlockTaintFeature());
        BlocksTC.taintLog = registerBlock(new BlockTaintLog());
        BlocksTC.grassAmbient = registerBlock(new BlockGrassAmbient());
        BlocksTC.tableWood = registerBlock(new BlockTable(Material.WOOD, "table_wood", SoundType.WOOD).setHardness(2.0f));
        BlocksTC.tableStone = registerBlock(new BlockTable(Material.ROCK, "table_stone", SoundType.STONE).setHardness(2.5f));
        BlocksTC.pedestalArcane = registerBlock(new BlockPedestal("pedestal_arcane"));
        BlocksTC.pedestalAncient = registerBlock(new BlockPedestal("pedestal_ancient"));
        BlocksTC.pedestalEldritch = registerBlock(new BlockPedestal("pedestal_eldritch"));
        BlocksTC.metalBlockBrass = registerBlock(new BlockMetalTC("metal_brass"));
        BlocksTC.metalBlockThaumium = registerBlock(new BlockMetalTC("metal_thaumium"));
        BlocksTC.metalBlockVoid = registerBlock(new BlockMetalTC("metal_void"));
        BlocksTC.metalAlchemical = registerBlock(new BlockMetalTC("metal_alchemical"));
        BlocksTC.metalAlchemicalAdvanced = registerBlock(new BlockMetalTC("metal_alchemical_advanced"));
        BlocksTC.pavingStoneTravel = registerBlock(new BlockPavingStone("paving_stone_travel"));
        BlocksTC.pavingStoneBarrier = registerBlock(new BlockPavingStone("paving_stone_barrier"));
        BlocksTC.pillarArcane = registerBlock(new BlockPillar("pillar_arcane"));
        BlocksTC.pillarAncient = registerBlock(new BlockPillar("pillar_ancient"));
        BlocksTC.pillarEldritch = registerBlock(new BlockPillar("pillar_eldritch"));
        BlocksTC.matrixSpeed = registerBlock(new BlockStoneTC("matrix_speed", false));
        BlocksTC.matrixCost = registerBlock(new BlockStoneTC("matrix_cost", false));
        for (EnumDyeColor dye : EnumDyeColor.values()) {
            BlocksTC.candles.put(dye, registerBlock(new BlockCandle("candle_" + dye.getUnlocalizedName().toLowerCase(), dye)));
        }
        for (EnumDyeColor dye : EnumDyeColor.values()) {
            BlockBannerTC block = new BlockBannerTC("banner_" + dye.getUnlocalizedName().toLowerCase(), dye);
            ForgeRegistries.BLOCKS.register(block);
            ForgeRegistries.ITEMS.register(new BlockBannerTCItem(block).setRegistryName(block.getRegistryName()));
            BlocksTC.banners.put(dye, block);
        }
        BlocksTC.bannerCrimsonCult = new BlockBannerTC("banner_crimson_cult", null);
        ForgeRegistries.BLOCKS.register(BlocksTC.bannerCrimsonCult);
        ForgeRegistries.ITEMS.register(new BlockBannerTCItem((BlockBannerTC)BlocksTC.bannerCrimsonCult).setRegistryName(BlocksTC.bannerCrimsonCult.getRegistryName()));
        for (EnumDyeColor dye : EnumDyeColor.values()) {
            BlocksTC.nitor.put(dye, registerBlock(new BlockNitor("nitor_" + dye.getUnlocalizedName().toLowerCase(), dye)));
        }
        BlocksTC.visBattery = registerBlock(new BlockVisBattery());
        BlocksTC.inlay = registerBlock(new BlockInlay());
        BlocksTC.arcaneWorkbench = registerBlock(new BlockArcaneWorkbench());
        BlocksTC.arcaneWorkbenchCharger = registerBlock(new BlockArcaneWorkbenchCharger());
        BlocksTC.dioptra = registerBlock(new BlockDioptra());
        BlocksTC.researchTable = registerBlock(new BlockResearchTable());
        BlocksTC.crucible = registerBlock(new BlockCrucible());
        BlocksTC.arcaneEar = registerBlock(new BlockArcaneEar("arcane_ear"));
        BlocksTC.arcaneEarToggle = registerBlock(new BlockArcaneEarToggle());
        BlocksTC.lampArcane = registerBlock(new BlockLamp(TileLampArcane.class, "lamp_arcane"));
        BlocksTC.lampFertility = registerBlock(new BlockLamp(TileLampFertility.class, "lamp_fertility"));
        BlocksTC.lampGrowth = registerBlock(new BlockLamp(TileLampGrowth.class, "lamp_growth"));
        BlocksTC.levitator = registerBlock(new BlockLevitator());
        BlocksTC.centrifuge = registerBlock(new BlockCentrifuge());
        BlocksTC.bellows = registerBlock(new BlockBellows());
        BlocksTC.smelterBasic = registerBlock(new BlockSmelter("smelter_basic"));
        BlocksTC.smelterThaumium = registerBlock(new BlockSmelter("smelter_thaumium"));
        BlocksTC.smelterVoid = registerBlock(new BlockSmelter("smelter_void"));
        BlocksTC.smelterAux = registerBlock(new BlockSmelterAux());
        BlocksTC.smelterVent = registerBlock(new BlockSmelterVent());
        BlocksTC.alembic = registerBlock(new BlockAlembic());
        BlocksTC.rechargePedestal = registerBlock(new BlockRechargePedestal());
        BlocksTC.wandWorkbench = registerBlock(new BlockFocalManipulator());
        BlocksTC.hungryChest = registerBlock(new BlockHungryChest());
        BlocksTC.tube = registerBlock(new BlockTube(TileTube.class, "tube"));
        BlocksTC.tubeValve = registerBlock(new BlockTube(TileTubeValve.class, "tube_valve"));
        BlocksTC.tubeRestrict = registerBlock(new BlockTube(TileTubeRestrict.class, "tube_restrict"));
        BlocksTC.tubeOneway = registerBlock(new BlockTube(TileTubeOneway.class, "tube_oneway"));
        BlocksTC.tubeFilter = registerBlock(new BlockTube(TileTubeFilter.class, "tube_filter"));
        BlocksTC.tubeBuffer = registerBlock(new BlockTube(TileTubeBuffer.class, "tube_buffer"));
        BlocksTC.jarNormal = registerBlock(new BlockJar(TileJarFillable.class, "jar_normal"), BlockJarItem.class);
        BlocksTC.jarVoid = registerBlock(new BlockJar(TileJarFillableVoid.class, "jar_void"), BlockJarItem.class);
        BlocksTC.jarBrain = registerBlock(new BlockJar(TileJarBrain.class, "jar_brain"), BlockJarBrainItem.class);
        BlocksTC.infusionMatrix = registerBlock(new BlockInfusionMatrix());
        BlocksTC.infernalFurnace = registerBlock(new BlockInfernalFurnace());
        BlocksTC.everfullUrn = registerBlock(new BlockWaterJug());
        BlocksTC.thaumatorium = registerBlock(new BlockThaumatorium(false));
        BlocksTC.thaumatoriumTop = registerBlock(new BlockThaumatorium(true));
        BlocksTC.brainBox = registerBlock(new BlockBrainBox());
        BlocksTC.spa = registerBlock(new BlockSpa());
        BlocksTC.golemBuilder = registerBlock(new BlockGolemBuilder());
        BlocksTC.mirror = registerBlock(new BlockMirror(TileMirror.class, "mirror"), BlockMirrorItem.class);
        BlocksTC.mirrorEssentia = registerBlock(new BlockMirror(TileMirrorEssentia.class, "mirror_essentia"), BlockMirrorItem.class);
        BlocksTC.essentiaTransportInput = registerBlock(new BlockEssentiaTransport(TileEssentiaInput.class, "essentia_input"));
        BlocksTC.essentiaTransportOutput = registerBlock(new BlockEssentiaTransport(TileEssentiaOutput.class, "essentia_output"));
        BlocksTC.redstoneRelay = registerBlock(new BlockRedstoneRelay());
        BlocksTC.patternCrafter = registerBlock(new BlockPatternCrafter());
        BlocksTC.potionSprayer = registerBlock(new BlockPotionSprayer());
        BlocksTC.activatorRail = registerBlock(new BlockRailPowered().setHardness(0.7f).setCreativeTab(ConfigItems.TABTC).setRegistryName("thaumcraft", "activator_rail").setUnlocalizedName("activator_rail"));
        BlocksTC.stabilizer = registerBlock(new BlockStabilizer());
        BlocksTC.visGenerator = registerBlock(new BlockVisGenerator());
        BlocksTC.condenser = registerBlock(new BlockCondenser());
        BlocksTC.condenserlattice = registerBlock(new BlockCondenserLattice(false));
        BlocksTC.condenserlatticeDirty = registerBlock(new BlockCondenserLattice(true));
        BlocksTC.voidSiphon = registerBlock(new BlockVoidSiphon());
        FluidRegistry.registerFluid(FluidFluxGoo.instance);
        iForgeRegistry.register((BlocksTC.fluxGoo = new BlockFluxGoo()));
        FluidRegistry.registerFluid(FluidDeath.instance);
        FluidRegistry.addBucketForFluid(FluidDeath.instance);
        iForgeRegistry.register((BlocksTC.liquidDeath = new BlockFluidDeath()));
        FluidRegistry.registerFluid(FluidPure.instance);
        FluidRegistry.addBucketForFluid(FluidPure.instance);
        iForgeRegistry.register((BlocksTC.purifyingFluid = new BlockFluidPure()));
        BlocksTC.hole = registerBlock(new BlockHole());
        BlocksTC.effectShock = registerBlock(new BlockEffect("effect_shock"));
        BlocksTC.effectSap = registerBlock(new BlockEffect("effect_sap"));
        BlocksTC.effectGlimmer = registerBlock(new BlockEffect("effect_glimmer"));
        BlocksTC.placeholderNetherbrick = registerBlock(new BlockPlaceholder("placeholder_brick"));
        BlocksTC.placeholderObsidian = registerBlock(new BlockPlaceholder("placeholder_obsidian"));
        BlocksTC.placeholderBars = registerBlock(new BlockPlaceholder("placeholder_bars"));
        BlocksTC.placeholderAnvil = registerBlock(new BlockPlaceholder("placeholder_anvil"));
        BlocksTC.placeholderCauldron = registerBlock(new BlockPlaceholder("placeholder_cauldron"));
        BlocksTC.placeholderTable = registerBlock(new BlockPlaceholder("placeholder_table"));
        BlocksTC.empty = registerBlock(new BlockTranslucent("empty"));
        BlocksTC.barrier = registerBlock(new BlockBarrier());
    }
~~~

~~~java
public static void initTileEntities() {
        GameRegistry.registerTileEntity(TileArcaneWorkbench.class, "thaumcraft:TileArcaneWorkbench");
        GameRegistry.registerTileEntity(TileDioptra.class, "thaumcraft:TileDioptra");
        GameRegistry.registerTileEntity(TileArcaneEar.class, "thaumcraft:TileArcaneEar");
        GameRegistry.registerTileEntity(TileLevitator.class, "thaumcraft:TileLevitator");
        GameRegistry.registerTileEntity(TileCrucible.class, "thaumcraft:TileCrucible");
        GameRegistry.registerTileEntity(TileNitor.class, "thaumcraft:TileNitor");
        GameRegistry.registerTileEntity(TileFocalManipulator.class, "thaumcraft:TileFocalManipulator");
        GameRegistry.registerTileEntity(TilePedestal.class, "thaumcraft:TilePedestal");
        GameRegistry.registerTileEntity(TileRechargePedestal.class, "thaumcraft:TileRechargePedestal");
        GameRegistry.registerTileEntity(TileResearchTable.class, "thaumcraft:TileResearchTable");
        GameRegistry.registerTileEntity(TileTube.class, "thaumcraft:TileTube");
        GameRegistry.registerTileEntity(TileTubeValve.class, "thaumcraft:TileTubeValve");
        GameRegistry.registerTileEntity(TileTubeFilter.class, "thaumcraft:TileTubeFilter");
        GameRegistry.registerTileEntity(TileTubeRestrict.class, "thaumcraft:TileTubeRestrict");
        GameRegistry.registerTileEntity(TileTubeOneway.class, "thaumcraft:TileTubeOneway");
        GameRegistry.registerTileEntity(TileTubeBuffer.class, "thaumcraft:TileTubeBuffer");
        GameRegistry.registerTileEntity(TileHungryChest.class, "thaumcraft:TileChestHungry");
        GameRegistry.registerTileEntity(TileCentrifuge.class, "thaumcraft:TileCentrifuge");
        GameRegistry.registerTileEntity(TileJarFillable.class, "thaumcraft:TileJar");
        GameRegistry.registerTileEntity(TileJarFillableVoid.class, "thaumcraft:TileJarVoid");
        GameRegistry.registerTileEntity(TileJarBrain.class, "thaumcraft:TileJarBrain");
        GameRegistry.registerTileEntity(TileBellows.class, "thaumcraft:TileBellows");
        GameRegistry.registerTileEntity(TileSmelter.class, "thaumcraft:TileSmelter");
        GameRegistry.registerTileEntity(TileAlembic.class, "thaumcraft:TileAlembic");
        GameRegistry.registerTileEntity(TileInfusionMatrix.class, "thaumcraft:TileInfusionMatrix");
        GameRegistry.registerTileEntity(TileWaterJug.class, "thaumcraft:TileWaterJug");
        GameRegistry.registerTileEntity(TileInfernalFurnace.class, "thaumcraft:TileInfernalFurnace");
        GameRegistry.registerTileEntity(TileThaumatorium.class, "thaumcraft:TileThaumatorium");
        GameRegistry.registerTileEntity(TileThaumatoriumTop.class, "thaumcraft:TileThaumatoriumTop");
        GameRegistry.registerTileEntity(TileSpa.class, "thaumcraft:TileSpa");
        GameRegistry.registerTileEntity(TileLampGrowth.class, "thaumcraft:TileLampGrowth");
        GameRegistry.registerTileEntity(TileLampArcane.class, "thaumcraft:TileLampArcane");
        GameRegistry.registerTileEntity(TileLampFertility.class, "thaumcraft:TileLampFertility");
        GameRegistry.registerTileEntity(TileMirror.class, "thaumcraft:TileMirror");
        GameRegistry.registerTileEntity(TileMirrorEssentia.class, "thaumcraft:TileMirrorEssentia");
        GameRegistry.registerTileEntity(TileRedstoneRelay.class, "thaumcraft:TileRedstoneRelay");
        GameRegistry.registerTileEntity(TileGolemBuilder.class, "thaumcraft:TileGolemBuilder");
        GameRegistry.registerTileEntity(TileEssentiaInput.class, "thaumcraft:TileEssentiaInput");
        GameRegistry.registerTileEntity(TileEssentiaOutput.class, "thaumcraft:TileEssentiaOutput");
        GameRegistry.registerTileEntity(TilePatternCrafter.class, "thaumcraft:TilePatternCrafter");
        GameRegistry.registerTileEntity(TilePotionSprayer.class, "thaumcraft:TilePotionSprayer");
        GameRegistry.registerTileEntity(TileVisGenerator.class, "thaumcraft:TileVisGenerator");
        GameRegistry.registerTileEntity(TileStabilizer.class, "thaumcraft:TileStabilizer");
        GameRegistry.registerTileEntity(TileCondenser.class, "thaumcraft:TileCondenser");
        GameRegistry.registerTileEntity(TileVoidSiphon.class, "thaumcraft:TileVoidSiphon");
        GameRegistry.registerTileEntity(TileBanner.class, "thaumcraft:TileBanner");
        GameRegistry.registerTileEntity(TileHole.class, "thaumcraft:TileHole");
        GameRegistry.registerTileEntity(TileBarrierStone.class, "thaumcraft:TileBarrierStone");
    }
~~~

### src/main/java/thaumcraft/common/config/ConfigEntities.java

~~~java
public static void initEntities(IForgeRegistry<EntityEntry> iForgeRegistry) {
        int id = 0;
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistPortalGreater"), EntityCultistPortalGreater.class, "CultistPortalGreater", id++, Thaumcraft.instance, 64, 20, false, 6842578, 32896);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistPortalLesser"), EntityCultistPortalLesser.class, "CultistPortalLesser", id++, Thaumcraft.instance, 64, 20, false, 9438728, 6316242);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FluxRift"), EntityFluxRift.class, "FluxRift", id++, Thaumcraft.instance, 64, 20, false);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "SpecialItem"), EntitySpecialItem.class, "SpecialItem", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FollowItem"), EntityFollowingItem.class, "FollowItem", id++, Thaumcraft.instance, 64, 20, false);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FallingTaint"), EntityFallingTaint.class, "FallingTaint", id++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Alumentum"), EntityAlumentum.class, "Alumentum", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "GolemDart"), EntityGolemDart.class, "GolemDart", id++, Thaumcraft.instance, 64, 20, false);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchOrb"), EntityEldritchOrb.class, "EldritchOrb", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "BottleTaint"), EntityBottleTaint.class, "BottleTaint", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "GolemOrb"), EntityGolemOrb.class, "GolemOrb", id++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Grapple"), EntityGrapple.class, "Grapple", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CausalityCollapser"), EntityCausalityCollapser.class, "CausalityCollapser", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FocusProjectile"), EntityFocusProjectile.class, "FocusProjectile", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FocusCloud"), EntityFocusCloud.class, "FocusCloud", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Focusmine"), EntityFocusMine.class, "Focusmine", id++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TurretBasic"), EntityTurretCrossbow.class, "TurretBasic", id++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TurretAdvanced"), EntityTurretCrossbowAdvanced.class, "TurretAdvanced", id++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "ArcaneBore"), EntityArcaneBore.class, "ArcaneBore", id++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Golem"), EntityThaumcraftGolem.class, "Golem", id++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchWarden"), EntityEldritchWarden.class, "EldritchWarden", id++, Thaumcraft.instance, 64, 3, true, 6842578, 8421504);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchGolem"), EntityEldritchGolem.class, "EldritchGolem", id++, Thaumcraft.instance, 64, 3, true, 6842578, 8947848);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistLeader"), EntityCultistLeader.class, "CultistLeader", id++, Thaumcraft.instance, 64, 3, true, 6842578, 9438728);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintacleGiant"), EntityTaintacleGiant.class, "TaintacleGiant", id++, Thaumcraft.instance, 96, 3, false, 6842578, 10618530);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "BrainyZombie"), EntityBrainyZombie.class, "BrainyZombie", id++, Thaumcraft.instance, 64, 3, true, -16129, -16744448);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "GiantBrainyZombie"), EntityGiantBrainyZombie.class, "GiantBrainyZombie", id++, Thaumcraft.instance, 64, 3, true, -16129, -16760832);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Wisp"), EntityWisp.class, "Wisp", id++, Thaumcraft.instance, 64, 3, false, -16129, -1);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Firebat"), EntityFireBat.class, "Firebat", id++, Thaumcraft.instance, 64, 3, false, -16129, -806354944);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Spellbat"), EntitySpellBat.class, "Spellbat", id++, Thaumcraft.instance, 64, 3, false, -16129, -806354944);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Pech"), EntityPech.class, "Pech", id++, Thaumcraft.instance, 64, 3, true, -16129, -12582848);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "MindSpider"), EntityMindSpider.class, "MindSpider", id++, Thaumcraft.instance, 64, 3, true, 4996656, 4473924);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchGuardian"), EntityEldritchGuardian.class, "EldritchGuardian", id++, Thaumcraft.instance, 64, 3, true, 8421504, 0);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistKnight"), EntityCultistKnight.class, "CultistKnight", id++, Thaumcraft.instance, 64, 3, true, 9438728, 128);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistCleric"), EntityCultistCleric.class, "CultistCleric", id++, Thaumcraft.instance, 64, 3, true, 9438728, 8388608);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchCrab"), EntityEldritchCrab.class, "EldritchCrab", id++, Thaumcraft.instance, 64, 3, true, 8421504, 5570560);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "InhabitedZombie"), EntityInhabitedZombie.class, "InhabitedZombie", id++, Thaumcraft.instance, 64, 3, true, 8421504, 5570560);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "ThaumSlime"), EntityThaumicSlime.class, "ThaumSlime", id++, Thaumcraft.instance, 64, 3, true, 10618530, -32513);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintCrawler"), EntityTaintCrawler.class, "TaintCrawler", id++, Thaumcraft.instance, 64, 3, true, 10618530, 3158064);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Taintacle"), EntityTaintacle.class, "Taintacle", id++, Thaumcraft.instance, 64, 3, false, 10618530, 4469572);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintacleTiny"), EntityTaintacleSmall.class, "TaintacleTiny", id++, Thaumcraft.instance, 64, 3, false);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintSwarm"), EntityTaintSwarm.class, "TaintSwarm", id++, Thaumcraft.instance, 64, 3, false, 10618530, 16744576);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintSeed"), EntityTaintSeed.class, "TaintSeed", id++, Thaumcraft.instance, 64, 20, false, 10618530, 4465237);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintSeedPrime"), EntityTaintSeedPrime.class, "TaintSeedPrime", id++, Thaumcraft.instance, 64, 20, false, 10618530, 5583718);
        EntityPech.valuedItems.put(Item.getIdFromItem(Items.ENDER_PEARL), 15);
        ArrayList<List> forInv = new ArrayList<List>();
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 0)));
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 1)));
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 6)));
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 7)));
        if (ModConfig.foundCopperIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 2)));
        }
        if (ModConfig.foundTinIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 3)));
        }
        if (ModConfig.foundSilverIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 4)));
        }
        if (ModConfig.foundLeadIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 5)));
        }
        forInv.add(Arrays.asList(2, new ItemStack(Items.BLAZE_ROD)));
        forInv.add(Arrays.asList(2, new ItemStack(BlocksTC.saplingGreatwood)));
        forInv.add(Arrays.asList(2, new ItemStack(Items.DRAGON_BREATH)));
        forInv.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forInv.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forInv.add(Arrays.asList(3, new ItemStack(Items.GOLDEN_APPLE, 1, 0)));
        forInv.add(Arrays.asList(4, new ItemStack(ItemsTC.thaumiumPick)));
        forInv.add(Arrays.asList(4, new ItemStack(ItemsTC.thaumiumAxe)));
        forInv.add(Arrays.asList(4, new ItemStack(ItemsTC.thaumiumHoe)));
        forInv.add(Arrays.asList(5, new ItemStack(Items.GOLDEN_APPLE, 1, 1)));
        forInv.add(Arrays.asList(5, new ItemStack(BlocksTC.saplingSilverwood)));
        forInv.add(Arrays.asList(5, new ItemStack(ItemsTC.curio, 1, 4)));
        EntityPech.tradeInventory.put(0, forInv);
        ArrayList<List> forMag = new ArrayList<List>();
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.AIR)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.EARTH)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.FIRE)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.WATER)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.ORDER)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY)));
        forMag.add(Arrays.asList(2, new ItemStack(Items.POTIONITEM, 1, 8193)));
        forMag.add(Arrays.asList(2, new ItemStack(Items.POTIONITEM, 1, 8261)));
        forMag.add(Arrays.asList(2, ThaumcraftApiHelper.makeCrystal(Aspect.FLUX)));
        forMag.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forMag.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forMag.add(Arrays.asList(3, ThaumcraftApiHelper.makeCrystal(Aspect.AURA)));
        forMag.add(Arrays.asList(3, new ItemStack(Items.GOLDEN_APPLE, 1, 0)));
        forMag.add(Arrays.asList(4, new ItemStack(ItemsTC.clothBoots)));
        forMag.add(Arrays.asList(4, new ItemStack(ItemsTC.clothChest)));
        forMag.add(Arrays.asList(4, new ItemStack(ItemsTC.clothLegs)));
        forMag.add(Arrays.asList(5, new ItemStack(Items.GOLDEN_APPLE, 1, 1)));
        forMag.add(Arrays.asList(5, new ItemStack(ItemsTC.pechWand)));
        forMag.add(Arrays.asList(5, new ItemStack(ItemsTC.curio, 1, 4)));
        forMag.add(Arrays.asList(5, new ItemStack(ItemsTC.amuletVis, 1, 0)));
        forInv.add(Arrays.asList(5, new ItemStack(Items.TOTEM_OF_UNDYING)));
        EntityPech.tradeInventory.put(1, forMag);
        ArrayList<List> forArc = new ArrayList<List>();
        for (int a = 0; a < 15; ++a) {
            forArc.add(Arrays.asList(1, new ItemStack(BlocksTC.candles.get(EnumDyeColor.byDyeDamage(a)))));
        }
        forArc.add(Arrays.asList(2, new ItemStack(Items.GHAST_TEAR)));
        forInv.add(Arrays.asList(2, new ItemStack(Items.COMPASS)));
        forArc.add(Arrays.asList(2, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(Enchantments.POWER, 1))));
        forArc.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forArc.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forArc.add(Arrays.asList(3, new ItemStack(Items.GOLDEN_APPLE, 1, 0)));
        forArc.add(Arrays.asList(4, new ItemStack(ItemsTC.eldritchEye)));
        forArc.add(Arrays.asList(4, new ItemStack(Items.GOLDEN_APPLE, 1, 1)));
        forInv.add(Arrays.asList(4, new ItemStack(Items.SPECTRAL_ARROW)));
        forArc.add(Arrays.asList(5, new ItemStack(ItemsTC.baubles, 1, 3)));
        forArc.add(Arrays.asList(5, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(Enchantments.FLAME, 1))));
        forArc.add(Arrays.asList(5, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(Enchantments.INFINITY, 1))));
        forArc.add(Arrays.asList(5, new ItemStack(ItemsTC.curio, 1, 4)));
        EntityPech.tradeInventory.put(2, forArc);
    }
~~~

### src/main/java/thaumcraft/common/config/ConfigRecipes.java

~~~java
public static void initializeArcaneRecipes(IForgeRegistry<IRecipe> iForgeRegistry) {
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:mechanism_simple"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEARTIFICE", 10, new AspectList().add(Aspect.FIRE, 1).add(Aspect.WATER, 1), ItemsTC.mechanismSimple, " B ", "ISI", " B ", 'B', "plateBrass", 'I', "plateIron", 'S', "stickWood"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:mechanism_complex"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEARTIFICE", 50, new AspectList().add(Aspect.FIRE, 1).add(Aspect.WATER, 1), ItemsTC.mechanismComplex, " M ", "TQT", " M ", 'T', "plateThaumium", 'Q', Blocks.PISTON, 'M', new ItemStack(ItemsTC.mechanismSimple)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:vis_resonator"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKAUROMANCY@2", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1), ItemsTC.visResonator, "plateIron", "gemQuartz"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:activatorrail"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "FIRSTSTEPS", 10, null, BlocksTC.activatorRail, new ItemStack(Blocks.ACTIVATOR_RAIL)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:thaumometer"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "FIRSTSTEPS@2", 20, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), ItemsTC.thaumometer, " I ", "IGI", " I ", 'I', "ingotGold", 'G', new ItemStack(Blocks.GLASS_PANE)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:sanitychecker"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "WARP", 20, new AspectList().add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), ItemsTC.sanityChecker, "BN ", "M N", "BN ", 'N', "nuggetBrass", 'B', new ItemStack(ItemsTC.brain), 'M', new ItemStack(ItemsTC.mirroredGlass)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:rechargepedestal"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "RECHARGEPEDESTAL", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.ORDER, 1), BlocksTC.rechargePedestal, " R ", "DID", "SSS", 'I', "ingotGold", 'D', "gemDiamond", 'R', new ItemStack(ItemsTC.visResonator), 'S', "stone"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:workbenchcharger"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "WORKBENCHCHARGER", 200, new AspectList().add(Aspect.AIR, 2).add(Aspect.ORDER, 2), new ItemStack(BlocksTC.arcaneWorkbenchCharger), " R ", "W W", "I I", 'I', "ingotIron", 'R', new ItemStack(ItemsTC.visResonator), 'W', new ItemStack(BlocksTC.plankGreatwood)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:wand_workbench"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEAUROMANCY@2", 100, new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.wandWorkbench), "ISI", "BRB", "GTG", 'S', new ItemStack(BlocksTC.slabArcaneStone), 'T', new ItemStack(BlocksTC.tableStone), 'R', new ItemStack(ItemsTC.visResonator), 'B', new ItemStack(BlocksTC.stoneArcane), 'G', "ingotGold", 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:caster_basic"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKAUROMANCY@2", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), new ItemStack(ItemsTC.casterBasic), "III", "LRL", "LTL", 'T', new ItemStack(ItemsTC.thaumometer), 'R', new ItemStack(ItemsTC.visResonator), 'L', "leather", 'I', "ingotIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EnchantedFabric"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKINFUSION", 5, null, new ItemStack(ItemsTC.fabric), " S ", "SCS", " S ", 'S', "string", 'C', new ItemStack(Blocks.WOOL, 1, 32767)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:RobeChest"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKINFUSION", 100, null, new ItemStack(ItemsTC.clothChest, 1), "I I", "III", "III", 'I', new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:RobeLegs"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKINFUSION", 100, null, new ItemStack(ItemsTC.clothLegs, 1), "III", "I I", "I I", 'I', new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:RobeBoots"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKINFUSION", 100, null, new ItemStack(ItemsTC.clothBoots, 1), "I I", "I I", 'I', new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Goggles"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "UNLOCKARTIFICE", 50, null, new ItemStack(ItemsTC.goggles), "LGL", "L L", "TGT", 'T', new ItemStack(ItemsTC.thaumometer), 'G', "ingotBrass", 'L', "leather"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:SealBlank"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "CONTROLSEALS", 20, new AspectList().add(Aspect.AIR, 1), new ItemStack(ItemsTC.seals, 3), new Object[] { new ItemStack(Items.CLAY_BALL), new ItemStack(ItemsTC.tallow), "dyeRed", "nitor" }));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:modvision"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "GOLEMVISION", 50, new AspectList().add(Aspect.WATER, 1), new ItemStack(ItemsTC.modules, 1, 0), "B B", "E E", "PGP", 'B', new ItemStack(Items.GLASS_BOTTLE), 'E', new ItemStack(Items.FERMENTED_SPIDER_EYE), 'P', "plateBrass", 'G', new ItemStack(ItemsTC.mechanismSimple)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:modaggression"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "SEALGUARD", 50, new AspectList().add(Aspect.FIRE, 1), new ItemStack(ItemsTC.modules, 1, 1), " R ", "RTR", "PGP", 'R', "paneGlass", 'T', new ItemStack(Items.BLAZE_POWDER), 'P', "plateBrass", 'G', new ItemStack(ItemsTC.mechanismSimple)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:mirrorglass"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "BASEARTIFICE", 50, new AspectList().add(Aspect.WATER, 1).add(Aspect.ORDER, 1), new ItemStack(ItemsTC.mirroredGlass), new Object[] { new ItemStack(ItemsTC.quicksilver), "paneGlass" }));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:ArcaneSpa"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ARCANESPA", 50, new AspectList().add(Aspect.WATER, 1), new ItemStack(BlocksTC.spa), "QIQ", "SJS", "SPS", 'P', new ItemStack(ItemsTC.mechanismSimple), 'J', new ItemStack(BlocksTC.jarNormal), 'S', new ItemStack(BlocksTC.stoneArcane), 'Q', new ItemStack(Blocks.QUARTZ_BLOCK), 'I', new ItemStack(Blocks.IRON_BARS)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Tube"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, null, new ItemStack(BlocksTC.tube, 8, 0), " Q ", "IGI", " B ", 'I', "plateIron", 'B', "nuggetBrass", 'G', "blockGlass", 'Q', "nuggetQuicksilver"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Resonator"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 50, null, new ItemStack(ItemsTC.resonator), "I I", "INI", " S ", 'I', "plateIron", 'N', Items.QUARTZ, 'S', "stickWood"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:TubeValve"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, null, new ItemStack(BlocksTC.tubeValve), new Object[] { new ItemStack(BlocksTC.tube), new ItemStack(Blocks.LEVER) }));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:TubeFilter"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, null, new ItemStack(BlocksTC.tubeFilter), new Object[] { new ItemStack(BlocksTC.tube, 1, 0), new ItemStack(ItemsTC.filter) }));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:TubeRestrict"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, new AspectList().add(Aspect.EARTH, 1), new ItemStack(BlocksTC.tubeRestrict), new Object[] { new ItemStack(BlocksTC.tube) }));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:TubeOneway"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 10, new AspectList().add(Aspect.WATER, 1), new ItemStack(BlocksTC.tubeOneway), new Object[] { new ItemStack(BlocksTC.tube) }));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:TubeBuffer"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 25, null, new ItemStack(BlocksTC.tubeBuffer), "PVP", "TWT", "PRP", 'T', new ItemStack(BlocksTC.tube), 'V', new ItemStack(BlocksTC.tubeValve), 'W', "plateIron", 'R', new ItemStack(BlocksTC.tubeRestrict), 'P', new ItemStack(ItemsTC.phial)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:WardedJar"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "WARDEDJARS", 5, null, new ItemStack(BlocksTC.jarNormal), "GWG", "G G", "GGG", 'W', "slabWood", 'G', "paneGlass"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:JarVoid"), new ShapedArcaneVoidJar(ConfigRecipes.defaultGroup, "WARDEDJARS", 50, new AspectList().add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.jarVoid), "J", 'J', new ItemStack(BlocksTC.jarNormal)));
        ResourceLocation bannerGroup = new ResourceLocation("thaumcraft", "banners");
        int a = 0;
        for (EnumDyeColor d : EnumDyeColor.values()) {
            ItemStack banner = new ItemStack(BlocksTC.banners.get(d));
            ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Banner" + d.getUnlocalizedName().toLowerCase()), new ShapedArcaneRecipe(bannerGroup, "BASEINFUSION", 10, null, banner, "WS", "WS", "WB", 'W', new ItemStack(Blocks.WOOL, 1, a), 'S', "stickWood", 'B', "slabWood"));
            ++a;
        }
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:PaveBarrier"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "PAVINGSTONES", 50, new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.pavingStoneBarrier, 4), "SS", "SS", 'S', new ItemStack(BlocksTC.stoneArcaneBrick)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:PaveTravel"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "PAVINGSTONES", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1), new ItemStack(BlocksTC.pavingStoneTravel, 4), "SS", "SS", 'S', new ItemStack(BlocksTC.stoneArcaneBrick)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:ArcaneLamp"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ARCANELAMP", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1), new ItemStack(BlocksTC.lampArcane), " I ", "IAI", " I ", 'A', new ItemStack(BlocksTC.amberBlock), 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Levitator"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "LEVITATOR", 35, new AspectList().add(Aspect.AIR, 1), new ItemStack(BlocksTC.levitator), "WIW", "BNB", "WGW", 'I', "plateThaumium", 'N', "nitor", 'W', "plankWood", 'B', "plateIron", 'G', new ItemStack(ItemsTC.mechanismSimple)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:RedstoneRelay"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "REDSTONERELAY", 10, new AspectList().add(Aspect.ORDER, 1), new ItemStack(BlocksTC.redstoneRelay), "   ", "TGT", "SSS", 'T', new ItemStack(Blocks.REDSTONE_TORCH), 'G', new ItemStack(ItemsTC.mechanismSimple), 'S', new ItemStack(Blocks.STONE_SLAB)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:ArcaneEar"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ARCANEEAR", 15, new AspectList().add(Aspect.AIR, 1), new ItemStack(BlocksTC.arcaneEar), "P P", " G ", "WRW", 'W', "slabWood", 'R', Items.REDSTONE, 'G', new ItemStack(ItemsTC.mechanismSimple), 'P', "plateBrass"));
        shapelessOreDictRecipe("ArcaneEarToggle", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.arcaneEarToggle), new Object[] { new ItemStack(BlocksTC.arcaneEar), new ItemStack(Blocks.LEVER) });
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:InfusionMatrix"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSION@2", 150, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.infusionMatrix), "S S", " N ", "S S", 'S', new ItemStack(BlocksTC.stoneArcaneBrick), 'N', "nitor"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:MatrixMotion"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONBOOST", 500, new AspectList().add(Aspect.AIR, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.matrixSpeed), "SNS", "NGN", "SNS", 'S', new ItemStack(BlocksTC.stoneArcane), 'N', "nitor", 'G', new ItemStack(Blocks.DIAMOND_BLOCK)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:MatrixCost"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONBOOST", 500, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.matrixCost), "SAS", "AGA", "SAS", 'S', new ItemStack(BlocksTC.stoneArcane), 'A', new ItemStack(ItemsTC.alumentum), 'G', new ItemStack(Blocks.DIAMOND_BLOCK)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:ArcanePedestal"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSION", 10, null, new ItemStack(BlocksTC.pedestalArcane), "SSS", " B ", "SSS", 'S', new ItemStack(BlocksTC.slabArcaneStone), 'B', new ItemStack(BlocksTC.stoneArcane)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:AncientPedestal"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONANCIENT", 150, null, new ItemStack(BlocksTC.pedestalAncient), "SSS", " B ", "SSS", 'S', new ItemStack(BlocksTC.slabAncient), 'B', new ItemStack(BlocksTC.stoneAncient)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EldritchPedestal"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONELDRITCH", 150, null, new ItemStack(BlocksTC.pedestalEldritch), "SSS", " B ", "SSS", 'S', new ItemStack(BlocksTC.slabEldritch), 'B', new ItemStack(BlocksTC.stoneEldritchTile)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:FocusPouch"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "FOCUSPOUCH", 25, null, new ItemStack(ItemsTC.focusPouch), "LGL", "LBL", "LLL", 'B', new ItemStack(ItemsTC.baubles, 1, 2), 'L', "leather", 'G', Items.GOLD_INGOT));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:dioptra"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "DIOPTRA", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.dioptra), "APA", "IGI", "AAA", 'A', new ItemStack(BlocksTC.stoneArcane), 'G', new ItemStack(ItemsTC.thaumometer), 'P', new ItemStack(ItemsTC.visResonator), 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:HungryChest"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "HUNGRYCHEST", 15, new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.hungryChest), "WTW", "W W", "WWW", 'W', new ItemStack(BlocksTC.plankGreatwood), 'T', "trapdoorWood"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Filter"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEALCHEMY", 15, new AspectList().add(Aspect.WATER, 1), new ItemStack(ItemsTC.filter, 2, 0), "GWG", 'G', Items.GOLD_INGOT, 'W', new ItemStack(BlocksTC.plankSilverwood)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:MorphicResonator"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASEALCHEMY", 50, new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1), new ItemStack(ItemsTC.morphicResonator), " G ", "BSB", " G ", 'G', "paneGlass", 'B', "plateBrass", 'S', new ItemStack(ItemsTC.nuggets, 1, 10)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Alembic"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTER", 50, new AspectList().add(Aspect.WATER, 1), new ItemStack(BlocksTC.alembic), "WFW", "SBS", "WFW", 'W', new ItemStack(BlocksTC.plankGreatwood), 'B', Items.BUCKET, 'F', new ItemStack(ItemsTC.filter), 'S', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EssentiaSmelter"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTER@2", 50, new AspectList().add(Aspect.FIRE, 1), new ItemStack(BlocksTC.smelterBasic), "BCB", "SFS", "SSS", 'C', new ItemStack(BlocksTC.crucible), 'F', new ItemStack(Blocks.FURNACE), 'S', "cobblestone", 'B', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EssentiaSmelterThaumium"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTERTHAUMIUM", 250, new AspectList().add(Aspect.FIRE, 2), new ItemStack(BlocksTC.smelterThaumium), "BFB", "IGI", "III", 'F', new ItemStack(BlocksTC.smelterBasic), 'G', new ItemStack(BlocksTC.metalAlchemical), 'I', "plateThaumium", 'B', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EssentiaSmelterVoid"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTERVOID", 750, new AspectList().add(Aspect.FIRE, 3), new ItemStack(BlocksTC.smelterVoid), "BFB", "IGI", "III", 'F', new ItemStack(BlocksTC.smelterBasic), 'G', new ItemStack(BlocksTC.metalAlchemicalAdvanced), 'I', "plateVoid", 'B', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:AlchemicalConstruct"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "TUBES", 75, new AspectList().add(Aspect.WATER, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.metalAlchemical, 2), "IVI", "TWT", "IVI", 'W', new ItemStack(BlocksTC.plankGreatwood), 'V', new ItemStack(BlocksTC.tubeValve), 'T', new ItemStack(BlocksTC.tube), 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:AdvAlchemyConstruct"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIASMELTERVOID@1", 200, new AspectList().add(Aspect.EARTH, 1).add(Aspect.FIRE, 1), new ItemStack(BlocksTC.metalAlchemicalAdvanced), " A ", "VPV", " A ", 'A', new ItemStack(BlocksTC.metalAlchemical), 'V', "plateVoid", 'P', Ingredient.fromItem(ItemsTC.primordialPearl)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:PotionSprayer"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "POTIONSPRAYER", 75, new AspectList().add(Aspect.WATER, 1).add(Aspect.FIRE, 1), new ItemStack(BlocksTC.potionSprayer), "BDB", "IAI", "ICI", 'B', "plateBrass", 'I', "plateIron", 'A', new ItemStack(Items.BREWING_STAND), 'D', new ItemStack(Blocks.DISPENSER), 'C', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:SmelterAux"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "IMPROVEDSMELTING", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.EARTH, 1), new ItemStack(BlocksTC.smelterAux), "WTW", "RGR", "IBI", 'W', new ItemStack(BlocksTC.plankGreatwood), 'B', new ItemStack(BlocksTC.bellows), 'R', "plateBrass", 'T', new ItemStack(BlocksTC.tubeFilter), 'I', "plateIron", 'G', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:SmelterVent"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "IMPROVEDSMELTING2", 150, new AspectList().add(Aspect.AIR, 1), new ItemStack(BlocksTC.smelterVent), "IBI", "MGF", "IBI", 'I', "plateIron", 'B', "plateBrass", 'F', new ItemStack(ItemsTC.filter), 'M', new ItemStack(ItemsTC.filter), 'G', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EssentiaTransportIn"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIATRANSPORT", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.essentiaTransportInput), "   ", "BQB", "IGI", 'I', "plateIron", 'B', "plateBrass", 'Q', new ItemStack(Blocks.DISPENSER), 'G', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:EssentiaTransportOut"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ESSENTIATRANSPORT", 100, new AspectList().add(Aspect.AIR, 1).add(Aspect.WATER, 1), new ItemStack(BlocksTC.essentiaTransportOutput), "   ", "BQB", "IGI", 'I', "plateIron", 'B', "plateBrass", 'Q', new ItemStack(Blocks.HOPPER), 'G', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Bellows"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BELLOWS", 25, new AspectList().add(Aspect.AIR, 1), new ItemStack(BlocksTC.bellows), "WW ", "LLI", "WW ", 'W', "plankWood", 'I', "ingotIron", 'L', "leather"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Centrifuge"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "CENTRIFUGE", 100, new AspectList().add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.centrifuge), " T ", "RCP", " T ", 'T', new ItemStack(BlocksTC.tube), 'P', new ItemStack(ItemsTC.mechanismSimple), 'R', new ItemStack(ItemsTC.morphicResonator), 'C', new ItemStack(BlocksTC.metalAlchemical)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:MnemonicMatrix"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "THAUMATORIUM", 50, new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.brainBox), "IAI", "ABA", "IAI", 'B', new ItemStack(ItemsTC.mind, 1, 0), 'A', "gemAmber", 'I', "plateIron"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:MindClockwork"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "MINDCLOCKWORK@2", 25, new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1), new ItemStack(ItemsTC.mind, 1, 0), " P ", "PGP", "BCB", 'G', new ItemStack(ItemsTC.mechanismSimple), 'B', "plateBrass", 'P', "paneGlass", 'C', new ItemStack(Items.COMPARATOR)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:AutomatedCrossbow"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "BASICTURRET", 100, new AspectList().add(Aspect.AIR, 1), new ItemStack(ItemsTC.turretPlacer, 1, 0), "BGI", "WMW", "S S", 'G', new ItemStack(ItemsTC.mechanismSimple), 'I', "plateIron", 'S', "stickWood", 'M', new ItemStack(ItemsTC.mind), 'B', Ingredient.fromItem(Items.BOW), 'W', new ItemStack(BlocksTC.plankGreatwood)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:AdvancedCrossbow"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ADVANCEDTURRET", 150, new AspectList().add(Aspect.AIR, 2), new ItemStack(ItemsTC.turretPlacer, 1, 1), "PMP", "PTP", "   ", 'T', new ItemStack(ItemsTC.turretPlacer, 1, 0), 'P', "plateIron", 'M', new ItemStack(ItemsTC.mind, 1, 1)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:patterncrafter"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "ARCANEPATTERNCRAFTER", 50, new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.patternCrafter), "VH ", "GCG", " W ", 'H', new ItemStack(Blocks.HOPPER), 'W', new ItemStack(BlocksTC.plankGreatwood), 'G', new ItemStack(ItemsTC.mechanismSimple), 'V', new ItemStack(ItemsTC.visResonator), 'C', "workbench"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:GrappleGunTip"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "GRAPPLEGUN", 25, new AspectList().add(Aspect.EARTH, 1), new ItemStack(ItemsTC.grappleGunTip), "BRB", "RHR", "BRB", 'B', "plateBrass", 'R', new ItemStack(ItemsTC.nuggets, 1, 10), 'H', new ItemStack(Blocks.TRIPWIRE_HOOK)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:GrappleGunSpool"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "GRAPPLEGUN", 25, new AspectList().add(Aspect.WATER, 1), new ItemStack(ItemsTC.grappleGunSpool), "SHS", "SGS", "SSS", 'G', new ItemStack(ItemsTC.mechanismSimple), 'S', "string", 'H', new ItemStack(Blocks.TRIPWIRE_HOOK)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:GrappleGun"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "GRAPPLEGUN", 75, new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1), new ItemStack(ItemsTC.grappleGun), "  S", "TII", " BW", 'B', "plateBrass", 'I', "plateIron", 'T', new ItemStack(ItemsTC.grappleGunTip), 'W', "plankWood", 'S', new ItemStack(ItemsTC.grappleGunSpool)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:VisBattery"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "VISBATTERY", 50, new AspectList().add(Aspect.AIR, 2).add(Aspect.EARTH, 2).add(Aspect.WATER, 2).add(Aspect.FIRE, 2).add(Aspect.ORDER, 2).add(Aspect.ENTROPY, 2), new ItemStack(BlocksTC.visBattery), "SSS", "SRS", "SSS", 'R', new ItemStack(ItemsTC.visResonator), 'S', new ItemStack(BlocksTC.slabArcaneStone)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:VisGenerator"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "VISGENERATOR", 25, new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1), new ItemStack(BlocksTC.visGenerator), "WSW", "EPE", "WRW", 'R', new ItemStack(ItemsTC.visResonator), 'E', new ItemStack(ItemsTC.nuggets, 1, 10), 'S', "dustRedstone", 'P', new ItemStack(Blocks.PISTON), 'W', "plankWood"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Condenser"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "FLUXCLEANUP", 500, new AspectList().add(Aspect.AIR, 5).add(Aspect.WATER, 5).add(Aspect.ENTROPY, 5), new ItemStack(BlocksTC.condenser), "BCB", "WMW", "BTB", 'T', new ItemStack(BlocksTC.tube), 'C', new ItemStack(ItemsTC.morphicResonator), 'W', "plankWood", 'M', new ItemStack(ItemsTC.mechanismComplex), 'B', "plateBrass"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:CondenserLattice"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "FLUXCLEANUP", 100, new AspectList().add(Aspect.EARTH, 3).add(Aspect.AIR, 3), new ItemStack(BlocksTC.condenserlattice), "QTQ", "QFQ", "QTQ", 'T', "plateThaumium", 'F', new ItemStack(ItemsTC.filter), 'Q', "gemQuartz"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:Stabilizer"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONSTABLE", 250, new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.ENTROPY, 1), new ItemStack(BlocksTC.stabilizer), "SRS", "BVB", "IMI", 'R', "blockRedstone", 'S', BlocksTC.slabArcaneStone, 'B', BlocksTC.stoneArcane, 'M', new ItemStack(ItemsTC.mechanismComplex), 'V', new ItemStack(ItemsTC.visResonator), 'I', new ItemStack(BlocksTC.inlay)));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:RedstoneInlay"), new ShapelessArcaneRecipe(ConfigRecipes.defaultGroup, "INFUSIONSTABLE", 25, new AspectList().add(Aspect.WATER, 1), new ItemStack(BlocksTC.inlay, 2), new Object[] { "dustRedstone", "ingotGold" }));
    }
~~~

~~~java
public static void initializeInfusionRecipes() {
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealHarvest"), new InfusionRecipe("SEALHARVEST", GolemHelper.getSealStack("thaumcraft:harvest"), 0, new AspectList().add(Aspect.PLANT, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(Items.MELON_SEEDS), new ItemStack(Items.BEETROOT_SEEDS), new ItemStack(Items.REEDS), new ItemStack(Blocks.CACTUS)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealButcher"), new InfusionRecipe("SEALBUTCHER", GolemHelper.getSealStack("thaumcraft:butcher"), 0, new AspectList().add(Aspect.BEAST, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), GolemHelper.getSealStack("thaumcraft:guard"), "leather", new ItemStack(Blocks.WOOL, 1, 32767), new ItemStack(Items.RABBIT_HIDE), new ItemStack(Items.PORKCHOP), new ItemStack(Items.MUTTON), new ItemStack(Items.BEEF)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealBreak"), new InfusionRecipe("SEALBREAK", GolemHelper.getSealStack("thaumcraft:breaker"), 1, new AspectList().add(Aspect.TOOL, 10).add(Aspect.ENTROPY, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), Ingredient.fromItem(Items.GOLDEN_AXE), Ingredient.fromItem(Items.GOLDEN_PICKAXE), Ingredient.fromItem(Items.GOLDEN_SHOVEL)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterAir"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalAir), 0, new AspectList().add(Aspect.AIR, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.AIR), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterFire"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalFire), 0, new AspectList().add(Aspect.FIRE, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterWater"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalWater), 0, new AspectList().add(Aspect.WATER, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.WATER), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterEarth"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalEarth), 0, new AspectList().add(Aspect.EARTH, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.EARTH), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterOrder"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalOrder), 0, new AspectList().add(Aspect.ORDER, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.ORDER), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterEntropy"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalEntropy), 0, new AspectList().add(Aspect.ENTROPY, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterFlux"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalTaint), 4, new AspectList().add(Aspect.FLUX, 10).add(Aspect.CRYSTAL, 10).add(Aspect.TRAP, 5), ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:focus_2"), new InfusionRecipe("FOCUSADVANCED@1", new ItemStack(ItemsTC.focus2), 3, new AspectList().add(Aspect.MAGIC, 25).add(Aspect.ORDER, 50), new ItemStack(ItemsTC.focus1), new ItemStack(ItemsTC.quicksilver), "gemDiamond", new ItemStack(ItemsTC.quicksilver), new ItemStack(Items.ENDER_PEARL)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:focus_3"), new InfusionRecipe("FOCUSGREATER@1", new ItemStack(ItemsTC.focus3), 5, new AspectList().add(Aspect.MAGIC, 25).add(Aspect.ORDER, 50).add(Aspect.VOID, 100), new ItemStack(ItemsTC.focus2), new ItemStack(ItemsTC.quicksilver), Ingredient.fromItem(ItemsTC.primordialPearl), new ItemStack(ItemsTC.quicksilver), new ItemStack(Items.NETHER_STAR)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:JarBrain"), new InfusionRecipe("JARBRAIN", new ItemStack(BlocksTC.jarBrain), 4, new AspectList().add(Aspect.MIND, 25).add(Aspect.SENSES, 25).add(Aspect.UNDEAD, 25), new ItemStack(BlocksTC.jarNormal), new ItemStack(ItemsTC.brain), new ItemStack(Items.SPIDER_EYE), new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.SPIDER_EYE)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VisAmulet"), new InfusionRecipe("VISAMULET", new ItemStack(ItemsTC.amuletVis, 1, 1), 6, new AspectList().add(Aspect.AURA, 50).add(Aspect.ENERGY, 100).add(Aspect.VOID, 50), new ItemStack(ItemsTC.baubles, 1, 0), new ItemStack(ItemsTC.visResonator), ThaumcraftApiHelper.makeCrystal(Aspect.AIR), ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), ThaumcraftApiHelper.makeCrystal(Aspect.WATER), ThaumcraftApiHelper.makeCrystal(Aspect.EARTH), ThaumcraftApiHelper.makeCrystal(Aspect.ORDER)));
        InfusionRunicAugmentRecipe ra = new InfusionRunicAugmentRecipe();
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:RunicArmor"), ra);
        for (int a = 0; a < 3; ++a) {
            ItemStack in = new ItemStack(ItemsTC.baubles, 1, 1);
            if (a > 0) {
                in.setTagInfo("TC.RUNIC", new NBTTagByte((byte)a));
            }
            ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:RunicArmorFake" + a), new InfusionRunicAugmentRecipe(in));
        }
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:Mirror"), new InfusionRecipe("MIRROR", new ItemStack(BlocksTC.mirror), 1, new AspectList().add(Aspect.MOTION, 25).add(Aspect.DARKNESS, 25).add(Aspect.EXCHANGE, 25), new ItemStack(ItemsTC.mirroredGlass), "ingotGold", "ingotGold", "ingotGold", new ItemStack(Items.ENDER_PEARL)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MirrorHand"), new InfusionRecipe("MIRRORHAND", new ItemStack(ItemsTC.handMirror), 5, new AspectList().add(Aspect.TOOL, 50).add(Aspect.MOTION, 50), new ItemStack(BlocksTC.mirror), "stickWood", new ItemStack(Items.COMPASS), new ItemStack(Items.MAP)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MirrorEssentia"), new InfusionRecipe("MIRRORESSENTIA", new ItemStack(BlocksTC.mirrorEssentia), 2, new AspectList().add(Aspect.MOTION, 25).add(Aspect.WATER, 25).add(Aspect.EXCHANGE, 25), new ItemStack(ItemsTC.mirroredGlass), "ingotIron", "ingotIron", "ingotIron", new ItemStack(Items.ENDER_PEARL)));
        ItemStack isEA = new ItemStack(ItemsTC.elementalAxe);
        EnumInfusionEnchantment.addInfusionEnchantment(isEA, EnumInfusionEnchantment.COLLECTOR, 1);
        EnumInfusionEnchantment.addInfusionEnchantment(isEA, EnumInfusionEnchantment.BURROWING, 1);
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalAxe"), new InfusionRecipe("ELEMENTALTOOLS", isEA, 1, new AspectList().add(Aspect.WATER, 60).add(Aspect.PLANT, 30), new ItemStack(ItemsTC.thaumiumAxe, 1, 32767), ConfigItems.WATER_CRYSTAL, ConfigItems.WATER_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
        ItemStack isEP = new ItemStack(ItemsTC.elementalPick);
        EnumInfusionEnchantment.addInfusionEnchantment(isEP, EnumInfusionEnchantment.REFINING, 1);
        EnumInfusionEnchantment.addInfusionEnchantment(isEP, EnumInfusionEnchantment.SOUNDING, 2);
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalPick"), new InfusionRecipe("ELEMENTALTOOLS", isEP, 1, new AspectList().add(Aspect.FIRE, 30).add(Aspect.METAL, 30).add(Aspect.SENSES, 30), new ItemStack(ItemsTC.thaumiumPick, 1, 32767), ConfigItems.FIRE_CRYSTAL, ConfigItems.FIRE_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
        ItemStack isESW = new ItemStack(ItemsTC.elementalSword);
        EnumInfusionEnchantment.addInfusionEnchantment(isESW, EnumInfusionEnchantment.ARCING, 2);
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalSword"), new InfusionRecipe("ELEMENTALTOOLS", isESW, 1, new AspectList().add(Aspect.AIR, 30).add(Aspect.MOTION, 30).add(Aspect.AVERSION, 30), new ItemStack(ItemsTC.thaumiumSword, 1, 32767), ConfigItems.AIR_CRYSTAL, ConfigItems.AIR_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
        ItemStack isES = new ItemStack(ItemsTC.elementalShovel);
        EnumInfusionEnchantment.addInfusionEnchantment(isES, EnumInfusionEnchantment.DESTRUCTIVE, 1);
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalShovel"), new InfusionRecipe("ELEMENTALTOOLS", isES, 1, new AspectList().add(Aspect.EARTH, 60).add(Aspect.CRAFT, 30), new ItemStack(ItemsTC.thaumiumShovel, 1, 32767), ConfigItems.EARTH_CRYSTAL, ConfigItems.EARTH_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalHoe"), new InfusionRecipe("ELEMENTALTOOLS", new ItemStack(ItemsTC.elementalHoe), 1, new AspectList().add(Aspect.ORDER, 30).add(Aspect.PLANT, 30).add(Aspect.ENTROPY, 30), new ItemStack(ItemsTC.thaumiumHoe, 1, 32767), ConfigItems.ORDER_CRYSTAL, ConfigItems.ENTROPY_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
        InfusionEnchantmentRecipe IEBURROWING = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.BURROWING, new AspectList().add(Aspect.SENSES, 80).add(Aspect.EARTH, 150), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Items.RABBIT_FOOT));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEBURROWING"), IEBURROWING);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IEBURROWINGFAKE"), new InfusionEnchantmentRecipe(IEBURROWING, new ItemStack(Items.WOODEN_PICKAXE)));
        InfusionEnchantmentRecipe IECOLLECTOR = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.COLLECTOR, new AspectList().add(Aspect.DESIRE, 80).add(Aspect.WATER, 100), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Items.LEAD));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IECOLLECTOR"), IECOLLECTOR);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IECOLLECTORFAKE"), new InfusionEnchantmentRecipe(IECOLLECTOR, new ItemStack(Items.STONE_AXE)));
        InfusionEnchantmentRecipe IEDESTRUCTIVE = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.DESTRUCTIVE, new AspectList().add(Aspect.AVERSION, 200).add(Aspect.ENTROPY, 250), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Blocks.TNT));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEDESTRUCTIVE"), IEDESTRUCTIVE);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IEDESTRUCTIVEFAKE"), new InfusionEnchantmentRecipe(IEDESTRUCTIVE, new ItemStack(Items.STONE_PICKAXE)));
        InfusionEnchantmentRecipe IEREFINING = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.REFINING, new AspectList().add(Aspect.ORDER, 80).add(Aspect.EXCHANGE, 60), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(ItemsTC.salisMundus));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEREFINING"), IEREFINING);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IEREFININGFAKE"), new InfusionEnchantmentRecipe(IEREFINING, new ItemStack(Items.IRON_PICKAXE)));
        InfusionEnchantmentRecipe IESOUNDING = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.SOUNDING, new AspectList().add(Aspect.SENSES, 40).add(Aspect.FIRE, 60), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Items.MAP));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IESOUNDING"), IESOUNDING);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IESOUNDINGFAKE"), new InfusionEnchantmentRecipe(IESOUNDING, new ItemStack(Items.GOLDEN_PICKAXE)));
        InfusionEnchantmentRecipe IEARCING = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.ARCING, new AspectList().add(Aspect.ENERGY, 40).add(Aspect.AIR, 60), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(Blocks.REDSTONE_BLOCK));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEARCING"), IEARCING);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IEARCINGFAKE"), new InfusionEnchantmentRecipe(IEARCING, new ItemStack(Items.WOODEN_SWORD)));
        InfusionEnchantmentRecipe IEESSENCE = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.ESSENCE, new AspectList().add(Aspect.BEAST, 40).add(Aspect.FLUX, 60), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), new ItemStack(ItemsTC.crystalEssence));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEESSENCE"), IEESSENCE);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IEESSENCEFAKE"), new InfusionEnchantmentRecipe(IEESSENCE, new ItemStack(Items.STONE_SWORD)));
        InfusionEnchantmentRecipe IELAMPLIGHT = new InfusionEnchantmentRecipe(EnumInfusionEnchantment.LAMPLIGHT, new AspectList().add(Aspect.LIGHT, 80).add(Aspect.AIR, 20), new IngredientNBTTC(new ItemStack(Items.ENCHANTED_BOOK)), "nitor");
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IELAMPLIGHT"), IELAMPLIGHT);
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:IELAMPLIGHTFAKE"), new InfusionEnchantmentRecipe(IELAMPLIGHT, new ItemStack(Items.GOLDEN_PICKAXE)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:BootsTraveller"), new InfusionRecipe("BOOTSTRAVELLER", new ItemStack(ItemsTC.travellerBoots), 1, new AspectList().add(Aspect.FLIGHT, 100).add(Aspect.MOTION, 100), new ItemStack(Items.LEATHER_BOOTS, 1, 32767), ConfigItems.AIR_CRYSTAL, ConfigItems.AIR_CRYSTAL, new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(Items.FEATHER), new ItemStack(Items.FISH, 1, 32767)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MindBiothaumic"), new InfusionRecipe("MINDBIOTHAUMIC", new ItemStack(ItemsTC.mind, 1, 1), 4, new AspectList().add(Aspect.MIND, 50).add(Aspect.MECHANISM, 25), new ItemStack(ItemsTC.mind, 1, 0), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.mechanismComplex)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ArcaneBore"), new InfusionRecipe("ARCANEBORE", new ItemStack(ItemsTC.turretPlacer, 1, 2), 4, new AspectList().add(Aspect.ENERGY, 25).add(Aspect.EARTH, 25).add(Aspect.MECHANISM, 100).add(Aspect.VOID, 25).add(Aspect.MOTION, 25), new ItemStack(ItemsTC.turretPlacer), new ItemStack(BlocksTC.plankGreatwood), new ItemStack(BlocksTC.plankGreatwood), new ItemStack(ItemsTC.mechanismComplex), "plateBrass", Ingredient.fromItem(Items.DIAMOND_PICKAXE), Ingredient.fromItem(Items.DIAMOND_SHOVEL), new ItemStack(ItemsTC.morphicResonator), new ItemStack(ItemsTC.nuggets, 1, 10)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:LampGrowth"), new InfusionRecipe("LAMPGROWTH", new ItemStack(BlocksTC.lampGrowth), 4, new AspectList().add(Aspect.PLANT, 20).add(Aspect.LIGHT, 15).add(Aspect.LIFE, 15).add(Aspect.TOOL, 15), new ItemStack(BlocksTC.lampArcane), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.DYE, 1, 15), ConfigItems.EARTH_CRYSTAL, new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.DYE, 1, 15), ConfigItems.EARTH_CRYSTAL));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:LampFertility"), new InfusionRecipe("LAMPFERTILITY", new ItemStack(BlocksTC.lampFertility), 4, new AspectList().add(Aspect.BEAST, 20).add(Aspect.LIGHT, 15).add(Aspect.LIFE, 15).add(Aspect.DESIRE, 15), new ItemStack(BlocksTC.lampArcane), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.WHEAT), ConfigItems.FIRE_CRYSTAL, new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.CARROT), ConfigItems.FIRE_CRYSTAL));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ThaumiumFortressHelm"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressHelm), 3, new AspectList().add(Aspect.METAL, 50).add(Aspect.PROTECT, 20).add(Aspect.ENERGY, 25), new ItemStack(ItemsTC.thaumiumHelm, 1, 32767), "plateThaumium", "plateThaumium", new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.EMERALD)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ThaumiumFortressChest"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressChest), 3, new AspectList().add(Aspect.METAL, 50).add(Aspect.PROTECT, 30).add(Aspect.ENERGY, 25), new ItemStack(ItemsTC.thaumiumChest, 1, 32767), "plateThaumium", "plateThaumium", "plateThaumium", "plateThaumium", new ItemStack(Items.GOLD_INGOT), "leather"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ThaumiumFortressLegs"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressLegs), 3, new AspectList().add(Aspect.METAL, 50).add(Aspect.PROTECT, 25).add(Aspect.ENERGY, 25), new ItemStack(ItemsTC.thaumiumLegs, 1, 32767), "plateThaumium", "plateThaumium", "plateThaumium", new ItemStack(Items.GOLD_INGOT), "leather"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeHelm"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeHelm), 6, new AspectList().add(Aspect.METAL, 25).add(Aspect.SENSES, 25).add(Aspect.PROTECT, 25).add(Aspect.ENERGY, 25).add(Aspect.ELDRITCH, 25).add(Aspect.VOID, 25), new ItemStack(ItemsTC.voidHelm), new ItemStack(ItemsTC.goggles, 1, 32767), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeChest"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeChest), 6, new AspectList().add(Aspect.METAL, 35).add(Aspect.PROTECT, 35).add(Aspect.ENERGY, 25).add(Aspect.ELDRITCH, 25).add(Aspect.VOID, 35), new ItemStack(ItemsTC.voidChest), new ItemStack(ItemsTC.clothChest), "plateVoid", "plateVoid", new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.fabric), "leather"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeLegs"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeLegs), 6, new AspectList().add(Aspect.METAL, 30).add(Aspect.PROTECT, 30).add(Aspect.ENERGY, 25).add(Aspect.ELDRITCH, 25).add(Aspect.VOID, 30), new ItemStack(ItemsTC.voidLegs), new ItemStack(ItemsTC.clothLegs), "plateVoid", "plateVoid", new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.fabric), "leather"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:HelmGoggles"), new InfusionRecipe("FORTRESSMASK", new Object[] { "goggles", new NBTTagByte((byte)1) }, 5, new AspectList().add(Aspect.SENSES, 40).add(Aspect.AURA, 20).add(Aspect.PROTECT, 20), new ItemStack(ItemsTC.fortressHelm, 1, 32767), new ItemStack(Items.SLIME_BALL), new ItemStack(ItemsTC.goggles, 1, 32767)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskGrinningDevil"), new InfusionRecipe("FORTRESSMASK", new Object[] { "mask", new NBTTagInt(0) }, 8, new AspectList().add(Aspect.MIND, 80).add(Aspect.LIFE, 80).add(Aspect.PROTECT, 20), new ItemStack(ItemsTC.fortressHelm, 1, 32767), new ItemStack(Items.DYE, 1, 0), "plateIron", "leather", new ItemStack(BlocksTC.shimmerleaf), new ItemStack(ItemsTC.brain), "plateIron"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskAngryGhost"), new InfusionRecipe("FORTRESSMASK", new Object[] { "mask", new NBTTagInt(1) }, 8, new AspectList().add(Aspect.ENTROPY, 80).add(Aspect.DEATH, 80).add(Aspect.PROTECT, 20), new ItemStack(ItemsTC.fortressHelm, 1, 32767), new ItemStack(Items.DYE, 1, 15), "plateIron", "leather", new ItemStack(Items.POISONOUS_POTATO), new ItemStack(Items.SKULL, 1, 1), "plateIron"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskSippingFiend"), new InfusionRecipe("FORTRESSMASK", new Object[] { "mask", new NBTTagInt(2) }, 8, new AspectList().add(Aspect.UNDEAD, 80).add(Aspect.LIFE, 80).add(Aspect.PROTECT, 20), new ItemStack(ItemsTC.fortressHelm, 1, 32767), new ItemStack(Items.DYE, 1, 1), "plateIron", "leather", new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.MILK_BUCKET), "plateIron"));
        ItemStack isPC = new ItemStack(ItemsTC.primalCrusher);
        EnumInfusionEnchantment.addInfusionEnchantment(isPC, EnumInfusionEnchantment.DESTRUCTIVE, 1);
        EnumInfusionEnchantment.addInfusionEnchantment(isPC, EnumInfusionEnchantment.REFINING, 1);
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:PrimalCrusher"), new InfusionRecipe("PRIMALCRUSHER", isPC, 6, new AspectList().add(Aspect.EARTH, 75).add(Aspect.TOOL, 75).add(Aspect.ENTROPY, 50).add(Aspect.VOID, 50).add(Aspect.AVERSION, 50).add(Aspect.ELDRITCH, 50).add(Aspect.DESIRE, 50), Ingredient.fromItem(ItemsTC.primordialPearl), Ingredient.fromItem(ItemsTC.voidPick), Ingredient.fromItem(ItemsTC.voidShovel), Ingredient.fromItem(ItemsTC.elementalPick), Ingredient.fromItem(ItemsTC.elementalShovel)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeart"), new InfusionRecipe("VERDANTCHARMS", new ItemStack(ItemsTC.charmVerdant), 5, new AspectList().add(Aspect.LIFE, 60).add(Aspect.ORDER, 30).add(Aspect.PLANT, 60), new ItemStack(ItemsTC.baubles, 1, 4), new ItemStack(ItemsTC.nuggets, 1, 10), ThaumcraftApiHelper.makeCrystal(Aspect.LIFE), new ItemStack(Items.MILK_BUCKET), ThaumcraftApiHelper.makeCrystal(Aspect.PLANT)));
        ItemStack pis1 = new ItemStack(Items.POTIONITEM);
        pis1.setTagInfo("Potion", new NBTTagString("minecraft:strong_healing"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeartLife"), new InfusionRecipe("VERDANTCHARMS", new Object[] { "type", new NBTTagByte((byte)1) }, 5, new AspectList().add(Aspect.LIFE, 80).add(Aspect.MAN, 80), new ItemStack(ItemsTC.charmVerdant), new ItemStack(Items.GOLDEN_APPLE), ThaumcraftApiHelper.makeCrystal(Aspect.LIFE), pis1, ThaumcraftApiHelper.makeCrystal(Aspect.MAN)));
        ItemStack pis2 = new ItemStack(Items.POTIONITEM);
        pis2.setTagInfo("Potion", new NBTTagString("minecraft:strong_regeneration"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeartSustain"), new InfusionRecipe("VERDANTCHARMS", new Object[] { "type", new NBTTagByte((byte)2) }, 5, new AspectList().add(Aspect.DESIRE, 80).add(Aspect.AIR, 80), new ItemStack(ItemsTC.charmVerdant), new ItemStack(ItemsTC.tripleMeatTreat), ThaumcraftApiHelper.makeCrystal(Aspect.DESIRE), pis2, ThaumcraftApiHelper.makeCrystal(Aspect.AIR)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CLOUDRING"), new InfusionRecipe("CLOUDRING", new ItemStack(ItemsTC.ringCloud), 1, new AspectList().add(Aspect.AIR, 50), new ItemStack(ItemsTC.baubles, 1, 1), ConfigItems.AIR_CRYSTAL, new ItemStack(Items.FEATHER)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CuriosityBand"), new InfusionRecipe("CURIOSITYBAND", new ItemStack(ItemsTC.bandCuriosity), 5, new AspectList().add(Aspect.MIND, 150).add(Aspect.VOID, 50).add(Aspect.TRAP, 100), new ItemStack(ItemsTC.baubles, 1, 6), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CHARMUNDYING"), new InfusionRecipe("CHARMUNDYING", new ItemStack(ItemsTC.charmUndying), 2, new AspectList().add(Aspect.LIFE, 25), new ItemStack(Items.TOTEM_OF_UNDYING), "plateBrass"));
        int a2 = 0;
        ItemStack[] nitorStacks = new ItemStack[16];
        for (EnumDyeColor d : EnumDyeColor.values()) {
            nitorStacks[a2] = new ItemStack(BlocksTC.nitor.get(d));
            ++a2;
        }
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CausalityCollapser"), new InfusionRecipe("RIFTCLOSER", new ItemStack(ItemsTC.causalityCollapser), 8, new AspectList().add(Aspect.ELDRITCH, 50).add(Aspect.FLUX, 50), new ItemStack(Blocks.TNT), new ItemStack(ItemsTC.morphicResonator), new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(ItemsTC.alumentum), Ingredient.fromStacks(nitorStacks), new ItemStack(ItemsTC.visResonator), new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(ItemsTC.alumentum), Ingredient.fromStacks(nitorStacks)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidSiphon"), new InfusionRecipe("VOIDSIPHON", new ItemStack(BlocksTC.voidSiphon), 7, new AspectList().add(Aspect.ELDRITCH, 50).add(Aspect.ENTROPY, 50).add(Aspect.VOID, 100).add(Aspect.CRAFT, 50), new ItemStack(BlocksTC.metalBlockVoid), new ItemStack(BlocksTC.stoneArcane), new ItemStack(BlocksTC.stoneArcane), new ItemStack(ItemsTC.mechanismComplex), "plateBrass", "plateBrass", new ItemStack(Items.NETHER_STAR)));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidseerPearl"), new InfusionRecipe("VOIDSEERPEARL", new ItemStack(ItemsTC.charmVoidseer), 8, new AspectList().add(Aspect.MIND, 150).add(Aspect.VOID, 150).add(Aspect.MAGIC, 100), new ItemStack(ItemsTC.baubles, 1, 4), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.voidSeed), new ItemStack(ItemsTC.brain), Ingredient.fromItem(ItemsTC.primordialPearl)));
    }
~~~

~~~java
public static void initializeNormalRecipes(IForgeRegistry<IRecipe> iForgeRegistry) {
        ResourceLocation brassGroup = new ResourceLocation("thaumcraft", "brass_stuff");
        ResourceLocation thaumiumGroup = new ResourceLocation("thaumcraft", "thaumium_stuff");
        ResourceLocation voidGroup = new ResourceLocation("thaumcraft", "void_stuff");
        ResourceLocation baublesGroup = new ResourceLocation("thaumcraft", "baubles_stuff");
        iForgeRegistry.register(new RecipesRobeArmorDyes().setRegistryName("robedye"));
        iForgeRegistry.register(new RecipesVoidRobeArmorDyes().setRegistryName("voidarmordye"));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "ironnuggetconvert"), ConfigRecipes.defaultGroup, new ItemStack(Items.IRON_NUGGET), "#", '#', new ItemStack(ItemsTC.nuggets, 1, 0));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "thaumiumtonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 6), "#", '#', new ItemStack(ItemsTC.ingots, 1, 0));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "voidtonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 7), "#", '#', new ItemStack(ItemsTC.ingots, 1, 1));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "brasstonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 8), "#", '#', new ItemStack(ItemsTC.ingots, 1, 2));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "quartztonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 9), "#", '#', new ItemStack(Items.QUARTZ));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "quicksilvertonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 5), "#", '#', new ItemStack(ItemsTC.quicksilver));
        oreDictRecipe("nuggetstothaumium", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.ingots, 1, 0), new Object[] { "###", "###", "###", '#', "nuggetThaumium" });
        oreDictRecipe("nuggetstovoid", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.ingots, 1, 1), new Object[] { "###", "###", "###", '#', "nuggetVoid" });
        oreDictRecipe("nuggetstobrass", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.ingots, 1, 2), new Object[] { "###", "###", "###", '#', "nuggetBrass" });
        oreDictRecipe("nuggetstoquicksilver", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.quicksilver), new Object[] { "###", "###", "###", '#', "nuggetQuicksilver" });
        oreDictRecipe("thaumiumingotstoblock", thaumiumGroup, new ItemStack(BlocksTC.metalBlockThaumium), new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.ingots, 1, 0) });
        oreDictRecipe("thaumiumblocktoingots", thaumiumGroup, new ItemStack(ItemsTC.ingots, 9, 0), new Object[] { "#", '#', new ItemStack(BlocksTC.metalBlockThaumium) });
        oreDictRecipe("voidingotstoblock", voidGroup, new ItemStack(BlocksTC.metalBlockVoid), new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.ingots, 1, 1) });
        oreDictRecipe("voidblocktoingots", voidGroup, new ItemStack(ItemsTC.ingots, 9, 1), new Object[] { "#", '#', new ItemStack(BlocksTC.metalBlockVoid) });
        oreDictRecipe("brassingotstoblock", brassGroup, new ItemStack(BlocksTC.metalBlockBrass), new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.ingots, 1, 2) });
        oreDictRecipe("brassblocktoingots", brassGroup, new ItemStack(ItemsTC.ingots, 9, 2), new Object[] { "#", '#', new ItemStack(BlocksTC.metalBlockBrass) });
        oreDictRecipe("fleshtoblock", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.fleshBlock), new Object[] { "###", "###", "###", '#', Items.ROTTEN_FLESH });
        oreDictRecipe("blocktoflesh", ConfigRecipes.defaultGroup, new ItemStack(Items.ROTTEN_FLESH, 9, 0), new Object[] { "#", '#', BlocksTC.fleshBlock });
        oreDictRecipe("ambertoblock", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.amberBlock), new Object[] { "##", "##", '#', "gemAmber" });
        oreDictRecipe("amberblocktobrick", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.amberBrick, 4), new Object[] { "##", "##", '#', new ItemStack(BlocksTC.amberBlock) });
        oreDictRecipe("amberbricktoblock", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.amberBlock, 4), new Object[] { "##", "##", '#', new ItemStack(BlocksTC.amberBrick) });
        oreDictRecipe("amberblocktoamber", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.amber, 4), new Object[] { "#", '#', new ItemStack(BlocksTC.amberBlock) });
        oreDictRecipe("ironplate", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.plate, 3, 1), new Object[] { "BBB", 'B', "ingotIron" });
        oreDictRecipe("brassplate", brassGroup, new ItemStack(ItemsTC.plate, 3, 0), new Object[] { "BBB", 'B', "ingotBrass" });
        oreDictRecipe("thaumiumplate", thaumiumGroup, new ItemStack(ItemsTC.plate, 3, 2), new Object[] { "BBB", 'B', "ingotThaumium" });
        oreDictRecipe("thaumiumhelm", thaumiumGroup, new ItemStack(ItemsTC.thaumiumHelm, 1), new Object[] { "III", "I I", 'I', "ingotThaumium" });
        oreDictRecipe("thaumiumchest", thaumiumGroup, new ItemStack(ItemsTC.thaumiumChest, 1), new Object[] { "I I", "III", "III", 'I', "ingotThaumium" });
        oreDictRecipe("thaumiumlegs", thaumiumGroup, new ItemStack(ItemsTC.thaumiumLegs, 1), new Object[] { "III", "I I", "I I", 'I', "ingotThaumium" });
        oreDictRecipe("thaumiumboots", thaumiumGroup, new ItemStack(ItemsTC.thaumiumBoots, 1), new Object[] { "I I", "I I", 'I', "ingotThaumium" });
        oreDictRecipe("thaumiumshovel", thaumiumGroup, new ItemStack(ItemsTC.thaumiumShovel, 1), new Object[] { "I", "S", "S", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("thaumiumpick", thaumiumGroup, new ItemStack(ItemsTC.thaumiumPick, 1), new Object[] { "III", " S ", " S ", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("thaumiumaxe", thaumiumGroup, new ItemStack(ItemsTC.thaumiumAxe, 1), new Object[] { "II", "SI", "S ", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("thaumiumhoe", thaumiumGroup, new ItemStack(ItemsTC.thaumiumHoe, 1), new Object[] { "II", "S ", "S ", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("thaumiumsword", thaumiumGroup, new ItemStack(ItemsTC.thaumiumSword, 1), new Object[] { "I", "I", "S", 'I', "ingotThaumium", 'S', "stickWood" });
        oreDictRecipe("voidplate", voidGroup, new ItemStack(ItemsTC.plate, 3, 3), new Object[] { "BBB", 'B', "ingotVoid" });
        oreDictRecipe("voidhelm", voidGroup, new ItemStack(ItemsTC.voidHelm, 1), new Object[] { "III", "I I", 'I', "ingotVoid" });
        oreDictRecipe("voidchest", voidGroup, new ItemStack(ItemsTC.voidChest, 1), new Object[] { "I I", "III", "III", 'I', "ingotVoid" });
        oreDictRecipe("voidlegs", voidGroup, new ItemStack(ItemsTC.voidLegs, 1), new Object[] { "III", "I I", "I I", 'I', "ingotVoid" });
        oreDictRecipe("voidboots", voidGroup, new ItemStack(ItemsTC.voidBoots, 1), new Object[] { "I I", "I I", 'I', "ingotVoid" });
        oreDictRecipe("voidshovel", voidGroup, new ItemStack(ItemsTC.voidShovel, 1), new Object[] { "I", "S", "S", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("voidpick", voidGroup, new ItemStack(ItemsTC.voidPick, 1), new Object[] { "III", " S ", " S ", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("voidaxe", voidGroup, new ItemStack(ItemsTC.voidAxe, 1), new Object[] { "II", "SI", "S ", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("voidhoe", voidGroup, new ItemStack(ItemsTC.voidHoe, 1), new Object[] { "II", "S ", "S ", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("voidsword", voidGroup, new ItemStack(ItemsTC.voidSword, 1), new Object[] { "I", "I", "S", 'I', "ingotVoid", 'S', "stickWood" });
        oreDictRecipe("babuleamulet", baublesGroup, new ItemStack(ItemsTC.baubles, 1, 0), new Object[] { " S ", "S S", " I ", 'S', "string", 'I', "ingotBrass" });
        oreDictRecipe("babulering", baublesGroup, new ItemStack(ItemsTC.baubles, 1, 1), new Object[] { "NNN", "N N", "NNN", 'N', "nuggetBrass" });
        oreDictRecipe("babulegirdle", baublesGroup, new ItemStack(ItemsTC.baubles, 1, 2), new Object[] { " L ", "L L", " I ", 'L', "leather", 'I', "ingotBrass" });
        oreDictRecipe("babuleamuletfancy", baublesGroup, new ItemStack(ItemsTC.baubles, 1, 4), new Object[] { " S ", "SGS", " I ", 'S', "string", 'G', "gemDiamond", 'I', "ingotGold" });
        oreDictRecipe("babuleringfancy", baublesGroup, new ItemStack(ItemsTC.baubles, 1, 5), new Object[] { "NGN", "N N", "NNN", 'G', "gemDiamond", 'N', "nuggetGold" });
        oreDictRecipe("babulegirdlefancy", baublesGroup, new ItemStack(ItemsTC.baubles, 1, 6), new Object[] { " L ", "LGL", " I ", 'L', "leather", 'G', "gemDiamond", 'I', "ingotGold" });
        iForgeRegistry.register(new RecipeTripleMeatTreat().setRegistryName("triplemeattreat"));
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:triplemeattreatfake"), new ShapelessOreRecipe(ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.tripleMeatTreat), "nuggetMeat", "nuggetMeat", "nuggetMeat", new ItemStack(Items.SUGAR)));
        iForgeRegistry.register(new RecipeMagicDust().setRegistryName("salismundus"));
        ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:salismundusfake"), new ShapelessOreRecipe(ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.salisMundus), Items.FLINT, Items.BOWL, Items.REDSTONE, new ItemStack(ItemsTC.crystalEssence, 1, 32767), new ItemStack(ItemsTC.crystalEssence, 1, 32767), new ItemStack(ItemsTC.crystalEssence, 1, 32767)));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "shimmerleaftoquicksilver"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.quicksilver), "#", '#', BlocksTC.shimmerleaf);
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "cinderpearltoblazepowder"), ConfigRecipes.defaultGroup, new ItemStack(Items.BLAZE_POWDER), "#", '#', BlocksTC.cinderpearl);
        ResourceLocation labelsGroup = new ResourceLocation("thaumcraft", "jarlabels");
        shapelessOreDictRecipe("JarLabel", labelsGroup, new ItemStack(ItemsTC.label, 4, 0), new Object[] { "dyeBlack", "slimeball", Items.PAPER, Items.PAPER, Items.PAPER, Items.PAPER });
        int count = 0;
        for (Aspect aspect : Aspect.aspects.values()) {
            ItemStack output = new ItemStack(ItemsTC.label, 1, 1);
            ((IEssentiaContainerItem)output.getItem()).setAspects(output, new AspectList().add(aspect, 1));
            shapelessOreDictRecipe("label_" + aspect.getTag(), labelsGroup, output, new Object[] { new ItemStack(ItemsTC.label), new IngredientNBTTC(ItemPhial.makeFilledPhial(aspect)) });
        }
        shapelessOreDictRecipe("JarLabelNull", labelsGroup, new ItemStack(ItemsTC.label), new Object[] { new ItemStack(ItemsTC.label, 1, 1) });
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "PlankGreatwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.plankGreatwood, 4), "W", 'W', new ItemStack(BlocksTC.logGreatwood));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "PlankSilverwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.plankSilverwood, 4), "W", 'W', new ItemStack(BlocksTC.logSilverwood));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsGreatwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsGreatwood, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.plankGreatwood));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsSilverwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsSilverwood, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.plankSilverwood));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsArcane"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsArcane, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.stoneArcane));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsArcaneBrick"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsArcaneBrick, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.stoneArcaneBrick));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsAncient"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsAncient, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.stoneAncient));
        oreDictRecipe("StoneArcane", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stoneArcane, 9), new Object[] { "KKK", "KCK", "KKK", 'K', "stone", 'C', new ItemStack(ItemsTC.crystalEssence) });
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "BrickArcane"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stoneArcaneBrick, 4), "KK", "KK", 'K', new ItemStack(BlocksTC.stoneArcane));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabGreatwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabGreatwood, 6), "KKK", 'K', new ItemStack(BlocksTC.plankGreatwood));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabSilverwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabSilverwood, 6), "KKK", 'K', new ItemStack(BlocksTC.plankSilverwood));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabArcaneStone"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabArcaneStone, 6), "KKK", 'K', new ItemStack(BlocksTC.stoneArcane));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabArcaneBrick"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabArcaneBrick, 6), "KKK", 'K', new ItemStack(BlocksTC.stoneArcaneBrick));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabAncient"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabAncient, 6), "KKK", 'K', new ItemStack(BlocksTC.stoneAncient));
        GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabEldritch"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabEldritch, 6), "KKK", 'K', new ItemStack(BlocksTC.stoneEldritchTile));
        oreDictRecipe("phial", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.phial, 8, 0), new Object[] { " C ", "G G", " G ", 'G', "blockGlass", 'C', Items.CLAY_BALL });
        oreDictRecipe("tablewood", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.tableWood), new Object[] { "SSS", "W W", 'S', "slabWood", 'W', "plankWood" });
        oreDictRecipe("tablestone", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.tableStone), new Object[] { "SSS", "W W", 'S', new ItemStack(Blocks.STONE_SLAB), 'W', "stone" });
        ResourceLocation inkwellGroup = new ResourceLocation("thaumcraft", "inkwell");
        shapelessOreDictRecipe("scribingtoolscraft1", inkwellGroup, new ItemStack(ItemsTC.scribingTools), new Object[] { new ItemStack(ItemsTC.phial, 1, 0), Items.FEATHER, "dyeBlack" });
        shapelessOreDictRecipe("scribingtoolscraft2", inkwellGroup, new ItemStack(ItemsTC.scribingTools), new Object[] { Items.GLASS_BOTTLE, Items.FEATHER, "dyeBlack" });
        shapelessOreDictRecipe("scribingtoolsrefill", inkwellGroup, new ItemStack(ItemsTC.scribingTools), new Object[] { new ItemStack(ItemsTC.scribingTools, 1, 32767), "dyeBlack" });
        oreDictRecipe("GolemBell", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.golemBell), new Object[] { " QQ", " QQ", "S  ", 'S', "stickWood", 'Q', "gemQuartz" });
        ResourceLocation candlesGroup = new ResourceLocation("thaumcraft", "tallowcandles");
        oreDictRecipe("TallowCandle", candlesGroup, new ItemStack(BlocksTC.candles.get(EnumDyeColor.WHITE), 3), new Object[] { " S ", " T ", " T ", 'S', "string", 'T', new ItemStack(ItemsTC.tallow) });
        IRecipe[] trs = new IRecipe[16];
        int a = 0;
        for (EnumDyeColor d : EnumDyeColor.values()) {
            trs[a] = shapelessOreDictRecipe("TallowCandle" + d.getUnlocalizedName().toLowerCase(), candlesGroup, new ItemStack(BlocksTC.candles.get(d)), new Object[] { ConfigAspects.dyes[15 - a], ingredientsFromBlocks(BlocksTC.candles.values().toArray(new Block[0])) });
            ++a;
        }
        oreDictRecipe("BrassBrace", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.jarBrace, 2), new Object[] { "NSN", "S S", "NSN", 'N', "nuggetBrass", 'S', "stickWood" });
    }
~~~

~~~java
public static void initializeSmelting() {
        GameRegistry.addSmelting(BlocksTC.oreCinnabar, new ItemStack(ItemsTC.quicksilver), 1.0f);
        GameRegistry.addSmelting(BlocksTC.oreAmber, new ItemStack(ItemsTC.amber), 1.0f);
        GameRegistry.addSmelting(BlocksTC.oreQuartz, new ItemStack(Items.QUARTZ), 1.0f);
        GameRegistry.addSmelting(BlocksTC.logGreatwood, new ItemStack(Items.COAL, 1, 1), 0.5f);
        GameRegistry.addSmelting(BlocksTC.logSilverwood, new ItemStack(Items.COAL, 1, 1), 0.5f);
        GameRegistry.addSmelting(new ItemStack(ItemsTC.clusters, 1, 0), new ItemStack(Items.IRON_INGOT, 2, 0), 1.0f);
        GameRegistry.addSmelting(new ItemStack(ItemsTC.clusters, 1, 1), new ItemStack(Items.GOLD_INGOT, 2, 0), 1.0f);
        GameRegistry.addSmelting(new ItemStack(ItemsTC.clusters, 1, 6), new ItemStack(ItemsTC.quicksilver, 2, 0), 1.0f);
        GameRegistry.addSmelting(new ItemStack(ItemsTC.clusters, 1, 7), new ItemStack(Items.QUARTZ, 2, 0), 1.0f);
        ThaumcraftApi.addSmeltingBonus("oreGold", new ItemStack(Items.GOLD_NUGGET));
        ThaumcraftApi.addSmeltingBonus("oreIron", new ItemStack(Items.IRON_NUGGET));
        ThaumcraftApi.addSmeltingBonus("oreCinnabar", new ItemStack(ItemsTC.nuggets, 1, 5));
        ThaumcraftApi.addSmeltingBonus("oreCopper", new ItemStack(ItemsTC.nuggets, 1, 1));
        ThaumcraftApi.addSmeltingBonus("oreTin", new ItemStack(ItemsTC.nuggets, 1, 2));
        ThaumcraftApi.addSmeltingBonus("oreSilver", new ItemStack(ItemsTC.nuggets, 1, 3));
        ThaumcraftApi.addSmeltingBonus("oreLead", new ItemStack(ItemsTC.nuggets, 1, 4));
        ThaumcraftApi.addSmeltingBonus("oreQuartz", new ItemStack(ItemsTC.nuggets, 1, 9));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 0), new ItemStack(Items.IRON_NUGGET));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 1), new ItemStack(Items.GOLD_NUGGET));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 6), new ItemStack(ItemsTC.nuggets, 1, 5));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 2), new ItemStack(ItemsTC.nuggets, 1, 1));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 3), new ItemStack(ItemsTC.nuggets, 1, 2));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 4), new ItemStack(ItemsTC.nuggets, 1, 3));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 5), new ItemStack(ItemsTC.nuggets, 1, 4));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 7), new ItemStack(ItemsTC.nuggets, 1, 9));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.BEEF), new ItemStack(ItemsTC.chunks, 1, 0));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.CHICKEN), new ItemStack(ItemsTC.chunks, 1, 1));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.PORKCHOP), new ItemStack(ItemsTC.chunks, 1, 2));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.FISH, 1, 32767), new ItemStack(ItemsTC.chunks, 1, 3));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.RABBIT), new ItemStack(ItemsTC.chunks, 1, 4));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.MUTTON), new ItemStack(ItemsTC.chunks, 1, 5));
        ThaumcraftApi.addSmeltingBonus("oreDiamond", new ItemStack(ItemsTC.nuggets, 1, 10), 0.025f);
        ThaumcraftApi.addSmeltingBonus("oreRedstone", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreLapis", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreEmerald", new ItemStack(ItemsTC.nuggets, 1, 10), 0.025f);
        ThaumcraftApi.addSmeltingBonus("oreGold", new ItemStack(ItemsTC.nuggets, 1, 10), 0.02f);
        ThaumcraftApi.addSmeltingBonus("oreIron", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreCinnabar", new ItemStack(ItemsTC.nuggets, 1, 10), 0.025f);
        ThaumcraftApi.addSmeltingBonus("oreCopper", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreTin", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreSilver", new ItemStack(ItemsTC.nuggets, 1, 10), 0.02f);
        ThaumcraftApi.addSmeltingBonus("oreLead", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus("oreQuartz", new ItemStack(ItemsTC.nuggets, 1, 10), 0.01f);
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ItemsTC.clusters, 1, 32767), new ItemStack(ItemsTC.nuggets, 1, 10), 0.02f);
    }
~~~

### src/main/java/thaumcraft/common/config/ConfigResearch.java

~~~java
private static void initScannables() {
        ScanningManager.addScannableThing(new ScanGeneric());
        for (ResourceLocation loc : Enchantment.REGISTRY.getKeys()) {
            Enchantment ench = Enchantment.REGISTRY.getObject(loc);
            ScanningManager.addScannableThing(new ScanEnchantment(ench));
        }
        for (ResourceLocation loc : Potion.REGISTRY.getKeys()) {
            Potion pot = Potion.REGISTRY.getObject(loc);
            ScanningManager.addScannableThing(new ScanPotion(pot));
        }
        ScanningManager.addScannableThing(new ScanEntity("!Wisp", EntityWisp.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!ThaumSlime", EntityThaumicSlime.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!Firebat", EntityFireBat.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!Pech", EntityPech.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!BrainyZombie", EntityBrainyZombie.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!EldritchCrab", EntityEldritchCrab.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!EldritchCrab", EntityInhabitedZombie.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!CrimsonCultist", EntityCultist.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!EldritchGuardian", EntityEldritchGuardian.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!TaintCrawler", EntityTaintCrawler.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!Taintacle", EntityTaintacle.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!TaintSeed", EntityTaintSeed.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!TaintSwarm", EntityTaintSwarm.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_toomuchflux", EntityFluxRift.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!FluxRift", EntityFluxRift.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_golem", EntityGolem.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_golem", EntityOwnedConstruct.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_SPIDER", EntitySpider.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_BAT", EntityBat.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_BAT", EntityFireBat.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", EntityBat.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", EntityParrot.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", EntityFireBat.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", EntityTaintSwarm.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", EntityWisp.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", EntityGhast.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_FLY", EntityBlaze.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!ORMOB", IEldritchMob.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!ORBOSS", EntityThaumcraftBoss.class, true));
        ScanningManager.addScannableThing(new ScanBlock("!ORBLOCK1", BlocksTC.stoneAncient, BlocksTC.stoneAncientTile));
        ScanningManager.addScannableThing(new ScanBlock("!ORBLOCK2", BlocksTC.stoneEldritchTile));
        ScanningManager.addScannableThing(new ScanBlock("!ORBLOCK3", BlocksTC.stoneAncientGlyphed));
        ScanningManager.addScannableThing(new ScanBlock("ORE", BlocksTC.oreAmber, BlocksTC.oreCinnabar, BlocksTC.crystalAir, BlocksTC.crystalFire, BlocksTC.crystalWater, BlocksTC.crystalEarth, BlocksTC.crystalOrder, BlocksTC.crystalEntropy, BlocksTC.crystalTaint));
        ScanningManager.addScannableThing(new ScanBlock("!OREAMBER", BlocksTC.oreAmber));
        ScanningManager.addScannableThing(new ScanBlock("!ORECINNABAR", BlocksTC.oreCinnabar));
        ScanningManager.addScannableThing(new ScanBlock("!ORECRYSTAL", BlocksTC.crystalAir, BlocksTC.crystalFire, BlocksTC.crystalWater, BlocksTC.crystalEarth, BlocksTC.crystalOrder, BlocksTC.crystalEntropy, BlocksTC.crystalTaint));
        ScanningManager.addScannableThing(new ScanBlock("PLANTS", BlocksTC.logGreatwood, BlocksTC.logSilverwood, BlocksTC.saplingGreatwood, BlocksTC.saplingSilverwood, BlocksTC.cinderpearl, BlocksTC.shimmerleaf, BlocksTC.vishroom));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTWOOD", BlocksTC.logGreatwood));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTWOOD", BlocksTC.logSilverwood));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTWOOD", BlocksTC.saplingGreatwood));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTWOOD", BlocksTC.saplingSilverwood));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTCINDERPEARL", BlocksTC.cinderpearl));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTSHIMMERLEAF", BlocksTC.shimmerleaf));
        ScanningManager.addScannableThing(new ScanBlock("!PLANTVISHROOM", BlocksTC.vishroom));
        ScanningManager.addScannableThing(new ScanItem("PRIMPEARL", new ItemStack(ItemsTC.primordialPearl, 1, 32767)));
        ScanningManager.addScannableThing(new ScanItem("!DRAGONBREATH", new ItemStack(Items.DRAGON_BREATH)));
        ScanningManager.addScannableThing(new ScanItem("!TOTEMUNDYING", new ItemStack(Items.TOTEM_OF_UNDYING)));
        ScanningManager.addScannableThing(new ScanBlock("f_TELEPORT", Blocks.PORTAL, Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME));
        ScanningManager.addScannableThing(new ScanItem("f_TELEPORT", new ItemStack(Items.ENDER_PEARL)));
        ScanningManager.addScannableThing(new ScanEntity("f_TELEPORT", EntityEnderman.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_BRAIN", EntityBrainyZombie.class, true));
        ScanningManager.addScannableThing(new ScanItem("f_BRAIN", new ItemStack(ItemsTC.brain)));
        ScanningManager.addScannableThing(new ScanBlock("f_DISPENSER", Blocks.DISPENSER));
        ScanningManager.addScannableThing(new ScanItem("f_DISPENSER", new ItemStack(Blocks.DISPENSER)));
        ScanningManager.addScannableThing(new ScanItem("f_MATCLAY", new ItemStack(Items.CLAY_BALL)));
        ScanningManager.addScannableThing(new ScanBlock("f_MATCLAY", Blocks.HARDENED_CLAY, Blocks.STAINED_HARDENED_CLAY));
        ScanningManager.addScannableThing(new ScanMaterial("f_MATCLAY", Material.CLAY));
        ScanningManager.addScannableThing(new ScanOreDictionary("f_MATIRON", "oreIron", "ingotIron", "blockIron", "plateIron"));
        ScanningManager.addScannableThing(new ScanOreDictionary("f_MATBRASS", "ingotBrass", "blockBrass", "plateBrass"));
        ScanningManager.addScannableThing(new ScanOreDictionary("f_MATTHAUMIUM", "ingotThaumium", "blockThaumium", "plateThaumium"));
        ScanningManager.addScannableThing(new ScanOreDictionary("f_MATVOID", "ingotVoid", "blockVoid", "plateVoid"));
        ScanningManager.addScannableThing(new ScanEntity("f_arrow", EntityArrow.class, true));
        ScanningManager.addScannableThing(new ScanItem("f_arrow", new ItemStack(Items.ARROW)));
        ScanningManager.addScannableThing(new ScanEntity("f_fireball", EntityFireball.class, true));
        ScanningManager.addScannableThing(new ScanEntity("f_spit", EntityLlamaSpit.class, true));
        ScanningManager.addScannableThing(new ScanItem("!Pechwand", new ItemStack(ItemsTC.pechWand)));
        ScanningManager.addScannableThing(new ScanItem("f_VOIDSEED", new ItemStack(ItemsTC.voidSeed)));
        ScanningManager.addScannableThing(new ScanSky());
    }
~~~

### src/main/java/thaumcraft/common/container/ContainerLogistics.java

~~~java
public void refreshItemList(boolean full) {
        int newTotal = lastTotal;
        TreeMap<String, ItemStack> ti = new TreeMap<String, ItemStack>();
        if (full) {
            newTotal = 0;
            CopyOnWriteArrayList<SealEntity> seals = SealHandler.getSealsInRange(worldObj, player.getPosition(), 32);
            for (SealEntity seal : seals) {
                if (seal.getSeal() instanceof SealProvide && seal.getOwner().equals(player.getUniqueID().toString())) {
                    IItemHandler handler = ThaumcraftInvHelper.getItemHandlerAt(worldObj, seal.getSealPos().pos, seal.getSealPos().face);
                    for (int slot = 0; slot < handler.getSlots(); ++slot) {
                        ItemStack stack = handler.getStackInSlot(slot).copy();
                        if (((SealProvide)seal.getSeal()).matchesFilters(stack)) {
                            if (searchText.isEmpty() || stack.getDisplayName().toLowerCase().contains(searchText.toLowerCase())) {
                                String key = stack.getDisplayName() + stack.getItemDamage() + stack.getTagCompound();
                                if (ti.containsKey(key)) {
                                    stack.grow(ti.get(key).getCount());
                                }
                                ti.put(key, stack);
                                newTotal += stack.getCount();
                            }
                        }
                    }
                }
            }
        }
        if (lastTotal != newTotal || start != lastStart) {
            lastTotal = newTotal;
            if (full) {
                items = ti;
            }
            input.clear();
            int j = 0;
            int q = 0;
            for (String key2 : items.keySet()) {
                if (++j <= start * 9) {
                    continue;
                }
                input.setInventorySlotContents(q, items.get(key2));
                if (++q >= input.getSizeInventory()) {
                    break;
                }
            }
            end = items.size() / 9 - 8;
        }
    }
~~~

### src/main/java/thaumcraft/common/container/ContainerPech.java

~~~java
public boolean enchantItem(EntityPlayer par1EntityPlayer, int par2) {
        if (par2 == 0) {
            generateContents();
            return true;
        }
        return super.enchantItem(par1EntityPlayer, par2);
    }
~~~

~~~java
private void generateContents() {
        if (!theWorld.isRemote && !inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(1).isEmpty() && inventory.getStackInSlot(2).isEmpty() && inventory.getStackInSlot(3).isEmpty() && inventory.getStackInSlot(4).isEmpty() && pech.isValued(inventory.getStackInSlot(0))) {
            int value = pech.getValue(inventory.getStackInSlot(0));
            if (theWorld.rand.nextInt(100) <= value / 2) {
                pech.setTamed(false);
                pech.playSound(SoundsTC.pech_trade, 0.4f, 1.0f);
            }
            if (theWorld.rand.nextInt(5) == 0) {
                value += theWorld.rand.nextInt(3);
            }
            else if (theWorld.rand.nextBoolean()) {
                value -= theWorld.rand.nextInt(3);
            }
            EntityPech pech = this.pech;
            ArrayList<List> pos = EntityPech.tradeInventory.get(this.pech.getPechType());
            while (value > 0) {
                int am = Math.min(5, Math.max((value + 1) / 2, theWorld.rand.nextInt(value) + 1));
                value -= am;
                if (am == 1 && theWorld.rand.nextBoolean() && hasStuffInPack()) {
                    ArrayList<Integer> loot = new ArrayList<Integer>();
                    for (int a = 0; a < this.pech.loot.size(); ++a) {
                        if (this.pech.loot.get(a) != null && !this.pech.loot.get(a).isEmpty() && this.pech.loot.get(a).getCount() > 0) {
                            loot.add(a);
                        }
                    }
                    int r = loot.get(theWorld.rand.nextInt(loot.size()));
                    ItemStack is = this.pech.loot.get(r).copy();
                    is.setCount(1);
                    addStack(is);
                    this.pech.loot.get(r).shrink(1);
                    if (this.pech.loot.get(r).getCount() > 0) {
                        continue;
                    }
                    this.pech.loot.set(r, ItemStack.EMPTY);
                }
                else {
                    if (am >= 4 && theWorld.rand.nextBoolean()) {
                        continue;
                    }
                    List it = null;
                    do {
                        it = pos.get(theWorld.rand.nextInt(pos.size()));
                    } while ((int)it.get(0) != am);
                    ItemStack is2 = ((ItemStack)it.get(1)).copy();
                    is2.onCrafting(theWorld, player, 0);
                    addStack(is2);
                }
            }
            inventory.decrStackSize(0, 1);
        }
    }
~~~

~~~java
private void addStack(ItemStack s) {
        for (int a = 1; a < 5; ++a) {
            if (inventory.getStackInSlot(a).isEmpty()) {
                inventory.setInventorySlotContents(a, s);
                break;
            }
            if (inventory.getStackInSlot(a).isItemEqual(s) && inventory.getStackInSlot(a).getCount() + s.getCount() < inventory.getStackInSlot(a).getMaxStackSize()) {
                inventory.getStackInSlot(a).grow(s.getCount());
            }
        }
    }
~~~

### src/main/java/thaumcraft/common/container/slot/SlotCraftingArcaneWorkbench.java

~~~java
public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
        onCrafting(stack);
        IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(craftMatrix, thePlayer);
        InventoryCrafting ic = craftMatrix;
        ForgeHooks.setCraftingPlayer(thePlayer);
        NonNullList<ItemStack> nonnulllist;
        if (recipe != null) {
            nonnulllist = CraftingManager.getRemainingItems(craftMatrix, thePlayer.world);
        }
        else {
            ic = new InventoryCrafting(new ContainerDummy(), 3, 3);
            for (int a = 0; a < 9; ++a) {
                ic.setInventorySlotContents(a, craftMatrix.getStackInSlot(a));
            }
            ic.eventHandler = craftMatrix.eventHandler;
            nonnulllist = CraftingManager.getRemainingItems(ic, thePlayer.world);
        }
        ForgeHooks.setCraftingPlayer(null);
        int vis = 0;
        AspectList crystals = null;
        if (recipe != null) {
            vis = recipe.getVis();
            vis *= (int)(1.0f - CasterManager.getTotalVisDiscount(thePlayer));
            crystals = recipe.getCrystals();
            if (vis > 0) {
                tile.getAura();
                tile.spendAura(vis);
            }
        }
        for (int i = 0; i < Math.min(9, nonnulllist.size()); ++i) {
            ItemStack itemstack = ic.getStackInSlot(i);
            ItemStack itemstack2 = nonnulllist.get(i);
            if (!itemstack.isEmpty()) {
                craftMatrix.decrStackSize(i, 1);
                itemstack = ic.getStackInSlot(i);
            }
            if (!itemstack2.isEmpty()) {
                if (itemstack.isEmpty()) {
                    craftMatrix.setInventorySlotContents(i, itemstack2);
                }
                else if (ItemStack.areItemsEqual(itemstack, itemstack2) && ItemStack.areItemStackTagsEqual(itemstack, itemstack2)) {
                    itemstack2.grow(itemstack.getCount());
                    craftMatrix.setInventorySlotContents(i, itemstack2);
                }
                else if (!player.inventory.addItemStackToInventory(itemstack2)) {
                    player.dropItem(itemstack2, false);
                }
            }
        }
        if (crystals != null) {
            for (Aspect aspect : crystals.getAspects()) {
                ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect));
                for (int j = 0; j < 6; ++j) {
                    ItemStack itemstack3 = craftMatrix.getStackInSlot(9 + j);
                    if (itemstack3.getItem() == ItemsTC.crystalEssence && ItemStack.areItemStackTagsEqual(cs, itemstack3)) {
                        craftMatrix.decrStackSize(9 + j, cs.getCount());
                    }
                }
            }
        }
        return stack;
    }
~~~

### src/main/java/thaumcraft/common/entities/ai/combat/AICultistHurtByTarget.java

~~~java
protected void alertOthers() {
        double d0 = getTargetDistance();
        for (EntityCreature entitycreature : taskOwner.world.getEntitiesWithinAABB(EntityCultist.class, new AxisAlignedBB(taskOwner.posX, taskOwner.posY, taskOwner.posZ, taskOwner.posX + 1.0, taskOwner.posY + 1.0, taskOwner.posZ + 1.0).grow(d0, 10.0, d0))) {
            if (taskOwner != entitycreature && entitycreature.getAttackTarget() == null && (!(taskOwner instanceof EntityTameable) || ((EntityTameable) taskOwner).getOwner() == ((EntityTameable)entitycreature).getOwner()) && !entitycreature.isOnSameTeam(taskOwner.getRevengeTarget())) {
                setEntityAttackTarget(entitycreature, taskOwner.getRevengeTarget());
            }
        }
    }
~~~

### src/main/java/thaumcraft/common/entities/ai/pech/AIPechItemEntityGoto.java

~~~java
public boolean shouldExecute() {
        int count = this.count - 1;
        this.count = count;
        if (count > 0) {
            return false;
        }
        double range = Double.MAX_VALUE;
        List<Entity> targets = pech.world.getEntitiesWithinAABBExcludingEntity(pech, pech.getEntityBoundingBox().grow(maxTargetDistance, maxTargetDistance, maxTargetDistance));
        if (targets.size() == 0) {
            return false;
        }
        for (Entity e : targets) {
            if (e instanceof EntityItem && pech.canPickup(((EntityItem)e).getItem())) {
                NBTTagCompound itemData = e.getEntityData();
                String username = ((EntityItem)e).getThrower();
                if (username != null && username.equals("PechDrop")) {
                    continue;
                }
                double distance = e.getDistanceSq(pech.posX, pech.posY, pech.posZ);
                if (distance >= range || distance > maxTargetDistance * maxTargetDistance) {
                    continue;
                }
                range = distance;
                targetEntity = e;
            }
        }
        return targetEntity != null;
    }
~~~

### src/main/java/thaumcraft/common/entities/construct/EntityArcaneBore.java

~~~java
private boolean dig() {
        boolean b = false;
        if (digTarget != null && !world.isAirBlock(digTarget)) {
            IBlockState digBs = world.getBlockState(digTarget);
            if (!digBs.getBlock().isAir(digBs, world, digTarget)) {
                boolean silktouch = false;
                int fortune = getFortune();
                if (canSilkTouch(digTarget, digBs)) {
                    silktouch = true;
                    fortune = 0;
                }
                FakePlayer fp = FakePlayerFactory.get((WorldServer) world, new GameProfile(null, "FakeThaumcraftBore"));
                fp.connection = new FakeNetHandlerPlayServer(fp.mcServer, new NetworkManager(EnumPacketDirection.CLIENTBOUND), fp);
                fp.arrowHitTimer = getEntityId();
                fp.xpCooldown = 1;
                fp.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
                fp.setHeldItem(EnumHand.MAIN_HAND, getHeldItemMainhand());
                if (BlockUtils.harvestBlock(getEntityWorld(), fp, digTarget, false, false, fortune, false)) {
                    ArrayList<ItemStack> items = EntityArcaneBore.drops.get(getEntityId());
                    if (items == null) {
                        items = new ArrayList<ItemStack>();
                    }
                    List<EntityItem> targets = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(digTarget.getX(), digTarget.getY(), digTarget.getZ(), digTarget.getX() + 1, digTarget.getY() + 1, digTarget.getZ() + 1).grow(1.5, 1.5, 1.5));
                    if (targets.size() > 0) {
                        for (EntityItem e : targets) {
                            items.add(e.getItem().copy());
                            e.setDead();
                        }
                    }
                    int refining = getRefining();
                    if (items.size() > 0) {
                        for (ItemStack is : items) {
                            ItemStack dropped = is.copy();
                            if (!silktouch && refining > 0) {
                                dropped = Utils.findSpecialMiningResult(is, (refining + 1) * 0.125f, world.rand);
                            }
                            if (dropped != null && !dropped.isEmpty()) {
                                boolean e2 = false;
                                for (EnumFacing f : EnumFacing.VALUES) {
                                    BlockPos p = getPosition().offset(f);
                                    IItemHandler inventory = ThaumcraftInvHelper.getItemHandlerAt(getEntityWorld(), p, f);
                                    if (inventory != null) {
                                        InventoryUtils.ejectStackAt(getEntityWorld(), getPosition(), f, dropped);
                                        e2 = true;
                                        break;
                                    }
                                }
                                if (e2) {
                                    continue;
                                }
                                InventoryUtils.ejectStackAt(getEntityWorld(), getPosition(), getFacing().getOpposite(), dropped);
                            }
                        }
                    }
                    breakCounter += fp.xpCooldown;
                    items.clear();
                }
            }
            if (getHeldItemMainhand() != null && !getHeldItemMainhand().isEmpty()) {
                if (breakCounter >= 50) {
                    breakCounter -= 50;
                    getHeldItemMainhand().damageItem(1, this);
                }
                if (getHeldItemMainhand().getCount() <= 0) {
                    setHeldItem(getActiveHand(), ItemStack.EMPTY);
                }
            }
            else {
                breakCounter = 0;
            }
            b = world.setBlockToAir(digTarget);
        }
        digTarget = null;
        return b;
    }
~~~

~~~java
protected void dropFewItems(boolean p_70628_1_, int treasure) {
        float b = treasure * 0.15f;
        if (rand.nextFloat() < 0.2f + b) {
            entityDropItem(new ItemStack(ItemsTC.mind), 0.5f);
        }
        if (rand.nextFloat() < 0.2f + b) {
            entityDropItem(new ItemStack(ItemsTC.morphicResonator), 0.5f);
        }
        if (rand.nextFloat() < 0.2f + b) {
            entityDropItem(new ItemStack(BlocksTC.crystalAir), 0.5f);
        }
        if (rand.nextFloat() < 0.2f + b) {
            entityDropItem(new ItemStack(BlocksTC.crystalEarth), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(ItemsTC.mechanismSimple), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(ItemsTC.plate), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
    }
~~~

### src/main/java/thaumcraft/common/entities/construct/EntityTurretCrossbow.java

~~~java
protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        float b = p_70628_2_ * 0.15f;
        if (rand.nextFloat() < 0.2f + b) {
            entityDropItem(new ItemStack(ItemsTC.mind), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(ItemsTC.mechanismSimple), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
    }
~~~

~~~java
public boolean shouldExecute() {
            if (targetChance > 0 && taskOwner.getRNG().nextInt(targetChance) != 0) {
                return false;
            }
            double d0 = getTargetDistance();
            List<EntityLivingBase> list = taskOwner.world.getEntitiesWithinAABB(targetClass, taskOwner.getEntityBoundingBox().grow(d0, 4.0, d0), Predicates.and(targetEntitySelector, EntitySelectors.NOT_SPECTATING));
            Collections.sort(list, theNearestAttackableTargetSorter);
            if (list.isEmpty()) {
                return false;
            }
            targetEntity = list.get(0);
            return true;
        }
~~~

### src/main/java/thaumcraft/common/entities/construct/EntityTurretCrossbowAdvanced.java

~~~java
protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        float b = p_70628_2_ * 0.15f;
        if (rand.nextFloat() < 0.2f + b) {
            entityDropItem(new ItemStack(ItemsTC.mind, 1, 1), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(ItemsTC.mechanismSimple), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
        if (rand.nextFloat() < 0.5f + b) {
            entityDropItem(new ItemStack(BlocksTC.plankGreatwood), 0.5f);
        }
        if (rand.nextFloat() < 0.3f + b) {
            entityDropItem(new ItemStack(ItemsTC.plate, 1, 0), 0.5f);
        }
        if (rand.nextFloat() < 0.4f + b) {
            entityDropItem(new ItemStack(ItemsTC.plate, 1, 1), 0.5f);
        }
        if (rand.nextFloat() < 0.4f + b) {
            entityDropItem(new ItemStack(ItemsTC.plate, 1, 1), 0.5f);
        }
    }
~~~

~~~java
public boolean shouldExecute() {
            if (targetChance > 0 && taskOwner.getRNG().nextInt(targetChance) != 0) {
                return false;
            }
            double d0 = getTargetDistance();
            List<EntityLivingBase> list = taskOwner.world.getEntitiesWithinAABB(targetClass, taskOwner.getEntityBoundingBox().grow(d0, 4.0, d0), Predicates.and(targetEntitySelector, EntitySelectors.NOT_SPECTATING));
            Collections.sort(list, theNearestAttackableTargetSorter);
            if (list.isEmpty()) {
                return false;
            }
            targetEntity = list.get(0);
            return true;
        }
~~~

### src/main/java/thaumcraft/common/entities/EntityFluxRift.java

~~~java
public static void createRift(World world, BlockPos pos) {
        pos = pos.add(world.rand.nextInt(16), 0, world.rand.nextInt(16));
        BlockPos p2 = world.getPrecipitationHeight(pos);
        if (!world.provider.hasSkyLight()) {
            for (p2 = new BlockPos(p2.getX(), 10, p2.getZ()); !world.isAirBlock(p2); p2 = p2.up(world.rand.nextInt(5) + 1)) {
                if (p2.getY() > world.getActualHeight() - 5) {
                    return;
                }
            }
        }
        if (p2.getY() < world.getActualHeight() - 4) {
            if (EntityUtils.getEntitiesInRange(world, p2, null, (Class<? extends Entity>)EntityFluxRift.class, 32.0).size() > 0) {
                return;
            }
            EntityFluxRift rift = new EntityFluxRift(world);
            rift.setRiftSeed(world.rand.nextInt());
            rift.setLocationAndAngles(p2.getX() + 0.5, p2.getY() + 0.5, p2.getZ() + 0.5, (float)world.rand.nextInt(360), 0.0f);
            float taint = AuraHandler.getFlux(world, p2);
            double size = Math.sqrt(taint * 3.0f);
            if (size > 5.0 && world.spawnEntity(rift)) {
                rift.setRiftSize((int)size);
                AuraHandler.drainFlux(world, p2, (float)size, false);
                List<EntityPlayer> targets2 = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(p2.getX(), p2.getY(), p2.getZ(), p2.getX() + 1, p2.getY() + 1, p2.getZ() + 1).grow(32.0, 32.0, 32.0));
                if (targets2 != null && targets2.size() > 0) {
                    for (EntityPlayer target : targets2) {
                        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(target);
                        if (!knowledge.isResearchKnown("f_toomuchflux")) {
                            target.sendStatusMessage(new TextComponentString("Â§5Â§o" + I18n.translateToLocal("tc.fluxevent.3")), true);
                            ThaumcraftApi.internalMethods.completeResearch(target, "f_toomuchflux");
                        }
                    }
                }
            }
        }
    }
~~~

~~~java
private void executeRiftEvent() {
        RandomItemChooser ric = new RandomItemChooser();
        FluxEventEntry ei = (FluxEventEntry)ric.chooseOnWeight(EntityFluxRift.events);
        if (ei == null) {
            return;
        }
        if (!ei.nearTaintAllowed && TaintHelper.isNearTaintSeed(world, getPosition())) {
            return;
        }
        boolean didit = false;
        switch (ei.event) {
            case 0: {
                EntityWisp wisp = new EntityWisp(world);
                wisp.setLocationAndAngles(posX + rand.nextGaussian() * 5.0, posY + rand.nextGaussian() * 5.0, posZ + rand.nextGaussian() * 5.0, 0.0f, 0.0f);
                if (world.rand.nextInt(5) == 0) {
                    wisp.setType(Aspect.FLUX.getTag());
                }
                if (wisp.getCanSpawnHere() && world.spawnEntity(wisp)) {
                    didit = true;
                    break;
                }
                break;
            }
            case 1: {
                EntityTaintSeedPrime seed = new EntityTaintSeedPrime(world);
                seed.setLocationAndAngles((int)(posX + rand.nextGaussian() * 5.0) + 0.5, (int)(posY + rand.nextGaussian() * 5.0), (int)(posZ + rand.nextGaussian() * 5.0) + 0.5, (float) world.rand.nextInt(360), 0.0f);
                if (seed.getCanSpawnHere() && world.spawnEntity(seed)) {
                    didit = true;
                    seed.boost = getRiftSize();
                    AuraHelper.polluteAura(getEntityWorld(), getPosition(), (float)(getRiftSize() / 2), true);
                    setDead();
                    break;
                }
                break;
            }
            case 2: {
                List<EntityLivingBase> targets2 = world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(16.0, 16.0, 16.0));
                if (targets2 != null && targets2.size() > 0) {
                    for (EntityLivingBase target : targets2) {
                        didit = true;
                        if (target instanceof EntityPlayer) {
                            ((EntityPlayer)target).sendStatusMessage(new TextComponentString("Â§5Â§o" + I18n.translateToLocal("tc.fluxevent.2")), true);
                        }
                        PotionEffect pe = new PotionEffect(PotionInfectiousVisExhaust.instance, 3000, 2);
                        pe.getCurativeItems().clear();
                        try {
                            target.addPotionEffect(pe);
                        }
                        catch (Exception ex) {}
                    }
                    break;
                }
                break;
            }
            case 3: {
                EntityPlayer target2 = world.getClosestPlayerToEntity(this, 16.0);
                if (target2 != null) {
                    FocusPackage p = new FocusPackage(target2);
                    FocusMediumRoot root = new FocusMediumRoot();
                    root.setupFromCasterToTarget(target2, target2, 0.5);
                    p.addNode(root);
                    FocusMediumCloud fp = new FocusMediumCloud();
                    fp.initialize();
                    fp.getSetting("radius").setValue(MathHelper.getInt(rand, 1, 3));
                    fp.getSetting("duration").setValue(MathHelper.getInt(rand, Math.min(getRiftSize() / 2, 30), Math.min(getRiftSize(), 120)));
                    p.addNode(fp);
                    p.addNode(new FocusEffectFlux());
                    FocusEngine.castFocusPackage(target2, p, true);
                    break;
                }
                break;
            }
            case 4: {
                setCollapse(true);
                break;
            }
        }
        if (didit) {
            setRiftStability(getRiftStability() + ei.cost);
        }
    }
~~~

### src/main/java/thaumcraft/common/entities/monster/cult/EntityCultistPortalLesser.java

~~~java
public void onUpdate() {
        super.onUpdate();
        if (isActive()) {
            ++activeCounter;
        }
        if (!world.isRemote) {
            if (!isActive()) {
                if (ticksExisted % 10 == 0) {
                    EntityPlayer p = world.getClosestPlayerToEntity(this, 32.0);
                    if (p != null) {
                        setActive(true);
                        playSound(SoundsTC.craftstart, 1.0f, 1.0f);
                    }
                }
            }
            else if (stagecounter-- <= 0) {
                EntityPlayer p = world.getClosestPlayerToEntity(this, 32.0);
                if (p != null && canEntityBeSeen(p)) {
                    int count = (world.getDifficulty() == EnumDifficulty.HARD) ? 6 : ((world.getDifficulty() == EnumDifficulty.NORMAL) ? 4 : 2);
                    try {
                        List l = world.getEntitiesWithinAABB(EntityCultist.class, getEntityBoundingBox().grow(32.0, 32.0, 32.0));
                        if (l != null) {
                            count -= l.size();
                        }
                    }
                    catch (Exception ex) {}
                    if (count > 0) {
                        world.setEntityState(this, (byte)16);
                        spawnMinions();
                    }
                }
                stagecounter = 50 + rand.nextInt(50);
            }
        }
        if (pulse > 0) {
            --pulse;
        }
    }
~~~

### src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java

~~~java
public boolean getCanSpawnHere() {
        List ents = world.getEntitiesWithinAABB(EntityEldritchGuardian.class, new AxisAlignedBB(posX, posY, posZ, posX + 1.0, posY + 1.0, posZ + 1.0).grow(32.0, 16.0, 32.0));
        return ents.size() <= 0 && super.getCanSpawnHere();
    }
~~~

### src/main/java/thaumcraft/common/entities/monster/EntityInhabitedZombie.java

~~~java
public boolean getCanSpawnHere() {
        List ents = world.getEntitiesWithinAABB(EntityInhabitedZombie.class, new AxisAlignedBB(posX, posY, posZ, posX + 1.0, posY + 1.0, posZ + 1.0).grow(32.0, 16.0, 32.0));
        return ents.size() <= 0 && super.getCanSpawnHere();
    }
~~~

### src/main/java/thaumcraft/common/entities/monster/EntityPech.java

~~~java
public boolean getCanSpawnHere() {
        Biome biome = world.getBiome(new BlockPos(this));
        boolean magicBiome = false;
        if (biome != null) {
            magicBiome = BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL);
        }
        int count = 0;
        try {
            List l = world.getEntitiesWithinAABB(EntityPech.class, getEntityBoundingBox().grow(16.0, 16.0, 16.0));
            if (l != null) {
                count = l.size();
            }
        }
        catch (Exception ex) {}
        if (world.provider.getDimension() != ModConfig.CONFIG_WORLD.overworldDim && biome != BiomeHandler.MAGICAL_FOREST && biome != BiomeHandler.EERIE) {
            magicBiome = false;
        }
        return count < 4 && magicBiome && super.getCanSpawnHere();
    }
~~~

~~~java
public void playLivingSound() {
        if (!world.isRemote) {
            if (rand.nextInt(3) == 0) {
                List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(4.0, 2.0, 4.0));
                for (int i = 0; i < list.size(); ++i) {
                    Entity entity1 = list.get(i);
                    if (entity1 instanceof EntityPech) {
                        world.setEntityState(this, (byte)17);
                        playSound(SoundsTC.pech_trade, getSoundVolume(), getSoundPitch());
                        return;
                    }
                }
            }
            world.setEntityState(this, (byte)16);
        }
        super.playLivingSound();
    }
~~~

~~~java
public boolean attackEntityFrom(DamageSource damSource, float par2) {
        if (isEntityInvulnerable(damSource)) {
            return false;
        }
        Entity entity = damSource.getTrueSource();
        if (entity instanceof EntityPlayer) {
            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(32.0, 16.0, 32.0));
            for (int i = 0; i < list.size(); ++i) {
                Entity entity2 = list.get(i);
                if (entity2 instanceof EntityPech) {
                    EntityPech entitypech = (EntityPech)entity2;
                    entitypech.becomeAngryAt(entity);
                }
            }
            becomeAngryAt(entity);
        }
        return super.attackEntityFrom(damSource, par2);
    }
~~~

~~~java
public ItemStack pickupItem(ItemStack entityItem) {
        if (entityItem == null || entityItem.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (isTamed() || !isValued(entityItem)) {
            for (int a = 0; a < loot.size(); ++a) {
                if (loot.get(a) != null && loot.get(a).getCount() <= 0) {
                    loot.set(a, ItemStack.EMPTY);
                }
                if (entityItem != null && !entityItem.isEmpty() && entityItem.getCount() > 0 && !loot.get(a).isEmpty() && loot.get(a).getCount() < loot.get(a).getMaxStackSize() && InventoryUtils.areItemStacksEqualStrict(entityItem, loot.get(a))) {
                    if (entityItem.getCount() + loot.get(a).getCount() <= loot.get(a).getMaxStackSize()) {
                        loot.get(a).grow(entityItem.getCount());
                        return ItemStack.EMPTY;
                    }
                    int sz = Math.min(entityItem.getCount(), loot.get(a).getMaxStackSize() - loot.get(a).getCount());
                    loot.get(a).grow(sz);
                    entityItem.shrink(sz);
                }
                if (entityItem != null && !entityItem.isEmpty() && entityItem.getCount() <= 0) {
                    entityItem = ItemStack.EMPTY;
                }
            }
            for (int a = 0; a < loot.size(); ++a) {
                if (!loot.get(a).isEmpty() && loot.get(a).getCount() <= 0) {
                    loot.set(a, ItemStack.EMPTY);
                }
                if (entityItem != null && entityItem.getCount() > 0 && loot.get(a).isEmpty()) {
                    loot.set(a, entityItem.copy());
                    return null;
                }
            }
            if (entityItem != null && !entityItem.isEmpty() && entityItem.getCount() <= 0) {
                entityItem = ItemStack.EMPTY;
            }
            return entityItem;
        }
        if (rand.nextInt(10) < getValue(entityItem)) {
            setTamed(true);
            setCombatTask();
            world.setEntityState(this, (byte)18);
        }
        entityItem.shrink(1);
        if (entityItem.getCount() <= 0) {
            return ItemStack.EMPTY;
        }
        return entityItem;
    }
~~~

### src/main/java/thaumcraft/common/entities/monster/EntityWisp.java

~~~java
public boolean getCanSpawnHere() {
        int count = 0;
        try {
            List l = world.getEntitiesWithinAABB(EntityWisp.class, getEntityBoundingBox().grow(16.0, 16.0, 16.0));
            if (l != null) {
                count = l.size();
            }
        }
        catch (Exception ex) {}
        return count < 8 && world.getDifficulty() != EnumDifficulty.PEACEFUL && isValidLightLevel() && super.getCanSpawnHere();
    }
~~~

### src/main/java/thaumcraft/common/entities/projectile/EntityBottleTaint.java

~~~java
protected void onImpact(RayTraceResult ray) {
        if (!world.isRemote) {
            List ents = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(posX, posY, posZ, posX, posY, posZ).grow(5.0, 5.0, 5.0));
            if (ents.size() > 0) {
                for (Object ent : ents) {
                    EntityLivingBase el = (EntityLivingBase)ent;
                    if (!(el instanceof ITaintedMob) && !el.isEntityUndead()) {
                        el.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 100, 0, false, true));
                    }
                }
            }
            for (int a = 0; a < 10; ++a) {
                int xx = (int)((rand.nextFloat() - rand.nextFloat()) * 4.0f);
                int zz = (int)((rand.nextFloat() - rand.nextFloat()) * 4.0f);
                BlockPos p = getPosition().add(xx, 0, zz);
                if (world.rand.nextBoolean()) {
                    if (world.isBlockNormalCube(p.down(), false) && world.getBlockState(p).getBlock().isReplaceable(world, p)) {
                        world.setBlockState(p, BlocksTC.fluxGoo.getDefaultState());
                    }
                    else {
                        p = p.down();
                        if (world.isBlockNormalCube(p.down(), false) && world.getBlockState(p).getBlock().isReplaceable(world, p)) {
                            world.setBlockState(p, BlocksTC.fluxGoo.getDefaultState());
                        }
                    }
                }
            }
            world.setEntityState(this, (byte)3);
            setDead();
        }
    }
~~~

### src/main/java/thaumcraft/common/entities/projectile/EntityEldritchOrb.java

~~~java
protected void onImpact(RayTraceResult mop) {
        if (!world.isRemote && getThrower() != null) {
            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(getThrower(), getEntityBoundingBox().grow(2.0, 2.0, 2.0));
            for (int i = 0; i < list.size(); ++i) {
                Entity entity1 = list.get(i);
                if (entity1 != null && entity1 instanceof EntityLivingBase && !((EntityLivingBase)entity1).isEntityUndead()) {
                    entity1.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, getThrower()), (float) getThrower().getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 0.666f);
                    try {
                        ((EntityLivingBase)entity1).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 160, 0));
                    }
                    catch (Exception ex) {}
                }
            }
            playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.5f, 2.6f + (rand.nextFloat() - rand.nextFloat()) * 0.8f);
            setDead();
        }
    }
~~~

### src/main/java/thaumcraft/common/entities/projectile/EntityRiftBlast.java

~~~java
public EntityRiftBlast(World par1World) {
        super(par1World);
        targetID = 0;
        red = false;
        growing = -1;
        vecs = new ArrayList<Quat>();
    }
~~~

~~~java
public EntityRiftBlast(World par1World, EntityLivingBase par2EntityLiving, EntityLivingBase t, boolean r) {
        super(par1World, par2EntityLiving);
        targetID = 0;
        red = false;
        growing = -1;
        vecs = new ArrayList<Quat>();
        target = t;
        red = r;
    }
~~~

### src/main/java/thaumcraft/common/golems/ai/AINearestValidTarget.java

~~~java
public boolean shouldExecute() {
        if (targetChance > 0 && taskOwner.getRNG().nextInt(targetChance) != 0) {
            return false;
        }
        double d0 = getTargetDistance();
        List<EntityLivingBase> list = taskOwner.world.getEntitiesWithinAABB(targetClass, taskOwner.getEntityBoundingBox().grow(d0, 4.0, d0), Predicates.and(targetEntitySelector, EntitySelectors.NOT_SPECTATING));
        Collections.sort(list, theNearestAttackableTargetSorter);
        if (list.isEmpty()) {
            return false;
        }
        targetEntity = list.get(0);
        return true;
    }
~~~

### src/main/java/thaumcraft/common/golems/EntityThaumcraftGolem.java

~~~java
protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        float b = p_70628_2_ * 0.15f;
        for (ItemStack stack : getProperties().generateComponents()) {
            ItemStack s = stack.copy();
            if (rand.nextFloat() < 0.3f + b) {
                if (s.getCount() > 0) {
                    s.shrink(rand.nextInt(s.getCount()));
                }
                entityDropItem(s, 0.25f);
            }
        }
    }
~~~

~~~java
public ItemStack holdItem(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getCount() <= 0) {
            return stack;
        }
        for (int a = 0; a < (getProperties().hasTrait(EnumGolemTrait.HAULER) ? 2 : 1); ++a) {
            if (getItemStackFromSlot(EntityEquipmentSlot.values()[a]) == null || getItemStackFromSlot(EntityEquipmentSlot.values()[a]).isEmpty()) {
                setItemStackToSlot(EntityEquipmentSlot.values()[a], stack);
                return ItemStack.EMPTY;
            }
            if (getItemStackFromSlot(EntityEquipmentSlot.values()[a]).getCount() < getItemStackFromSlot(EntityEquipmentSlot.values()[a]).getMaxStackSize() && ItemStack.areItemsEqual(getItemStackFromSlot(EntityEquipmentSlot.values()[a]), stack) && ItemStack.areItemStackTagsEqual(getItemStackFromSlot(EntityEquipmentSlot.values()[a]), stack)) {
                int d = Math.min(stack.getCount(), getItemStackFromSlot(EntityEquipmentSlot.values()[a]).getMaxStackSize() - getItemStackFromSlot(EntityEquipmentSlot.values()[a]).getCount());
                stack.shrink(d);
                getItemStackFromSlot(EntityEquipmentSlot.values()[a]).grow(d);
                if (stack.getCount() <= 0) {
                    stack = ItemStack.EMPTY;
                }
            }
        }
        return stack;
    }
~~~

### src/main/java/thaumcraft/common/golems/GolemProperties.java

~~~java
public ItemStack[] generateComponents() {
        ArrayList<ItemStack> comps = new ArrayList<ItemStack>();
        ItemStack base = getMaterial().componentBase;
        ItemStack mech = getMaterial().componentMechanism;
        addToList(comps, base, 2);
        addToList(comps, mech, 1);
        addToListFromComps(comps, getArms().components, getMaterial());
        addToListFromComps(comps, getLegs().components, getMaterial());
        addToListFromComps(comps, getHead().components, getMaterial());
        addToListFromComps(comps, getAddon().components, getMaterial());
        return comps.toArray(new ItemStack[0]);
    }
~~~

~~~java
private static void addToList(ArrayList<ItemStack> comps, ItemStack newItem, int mult) {
        for (ItemStack stack : comps) {
            if (stack.isItemEqual(newItem) && ItemStack.areItemStackTagsEqual(stack, newItem)) {
                stack.grow(newItem.getCount() * mult);
                return;
            }
        }
        ItemStack stack2 = newItem.copy();
        stack2.setCount(stack2.getCount() * mult);
        comps.add(stack2);
    }
~~~

### src/main/java/thaumcraft/common/golems/seals/SealHarvest.java

~~~java
public void tickSeal(World world, ISealEntity seal) {
        if (delay % 100 == 0) {
            AxisAlignedBB area = GolemHelper.getBoundsForArea(seal);
            Iterator<Long> rt = replantTasks.keySet().iterator();
            while (rt.hasNext()) {
                BlockPos pp = BlockPos.fromLong(rt.next());
                if (!area.contains(new Vec3d(pp.getX() + 0.5, pp.getY() + 0.5, pp.getZ() + 0.5))) {
                    if (replantTasks.get(rt) != null) {
                        Task tt = TaskHandler.getTask(world.provider.getDimension(), replantTasks.get(rt).taskid);
                        if (tt != null) {
                            tt.setSuspended(true);
                        }
                    }
                    rt.remove();
                }
            }
        }
        if (delay++ % 5 != 0) {
            return;
        }
        BlockPos p = GolemHelper.getPosInArea(seal, count++);
        if (CropUtils.isGrownCrop(world, p)) {
            Task task = new Task(seal.getSealPos(), p);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(world.provider.getDimension(), task);
        }
        else if (getToggles()[0].value && replantTasks.containsKey(p.toLong()) && world.isAirBlock(p)) {
            Task t = TaskHandler.getTask(world.provider.getDimension(), replantTasks.get(p.toLong()).taskid);
            if (t == null) {
                Task tt2 = new Task(seal.getSealPos(), replantTasks.get(p.toLong()).pos);
                tt2.setPriority(seal.getPriority());
                TaskHandler.addTask(world.provider.getDimension(), tt2);
                replantTasks.get(p.toLong()).taskid = tt2.getId();
            }
        }
    }
~~~

~~~java
public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
        if (CropUtils.isGrownCrop(world, task.getPos())) {
            FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile(null, "FakeThaumcraftGolem"));
            fp.connection = new FakeNetHandlerPlayServer(fp.mcServer, new NetworkManager(EnumPacketDirection.CLIENTBOUND), fp);
            fp.setPosition(golem.getGolemEntity().posX, golem.getGolemEntity().posY, golem.getGolemEntity().posZ);
            EnumFacing face = EnumFacing.getDirectionFromEntityLiving(task.getPos(), golem.getGolemEntity());
            IBlockState bs = world.getBlockState(task.getPos());
            if (CropUtils.clickableCrops.contains(bs.getBlock().getUnlocalizedName() + bs.getBlock().getMetaFromState(bs))) {
                bs.getBlock().onBlockActivated(world, task.getPos(), bs, fp, EnumHand.MAIN_HAND, face, 0.0f, 0.0f, 0.0f);
                golem.addRankXp(1);
                golem.swingArm();
            }
            else {
                GolemInteractionHelper.golemClick(world, golem, task.getPos(), task.getSealPos().face, ItemStack.EMPTY, false, true);
                if (CropUtils.isGrownCrop(world, task.getPos())) {
                    BlockUtils.harvestBlock(world, fp, task.getPos(), true, false, 0, true);
                    golem.addRankXp(1);
                    golem.swingArm();
                    if (getToggles()[0].value) {
                        ItemStack seed = ThaumcraftApi.getSeed(bs.getBlock());
                        if (seed != null && !seed.isEmpty()) {
                            IBlockState bb = world.getBlockState(task.getPos().down());
                            EnumFacing rf = null;
                            if (seed.getItem() instanceof IPlantable && bb.getBlock().canSustainPlant(bb, world, task.getPos().down(), EnumFacing.UP, (IPlantable)seed.getItem())) {
                                rf = EnumFacing.DOWN;
                            }
                            else if (!(seed.getItem() instanceof IPlantable) && bs.getBlock() instanceof BlockDirectional) {
                                rf = (EnumFacing)bs.getValue((IProperty)BlockDirectional.FACING);
                            }
                            if (rf != null) {
                                Task tt = new Task(task.getSealPos(), task.getPos());
                                tt.setPriority(task.getPriority());
                                tt.setLifespan((short)300);
                                replantTasks.put(tt.getPos().toLong(), new ReplantInfo(tt.getPos(), rf, tt.getId(), seed.copy(), bb.getBlock() instanceof BlockFarmland));
                                TaskHandler.addTask(world.provider.getDimension(), tt);
                            }
                        }
                    }
                }
            }
        }
        else if (replantTasks.containsKey(task.getPos().toLong()) && replantTasks.get(task.getPos().toLong()).taskid == task.getId() && world.isAirBlock(task.getPos()) && golem.isCarrying(replantTasks.get(task.getPos().toLong()).stack)) {
            FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile(null, "FakeThaumcraftGolem"));
            fp.setPosition(golem.getGolemEntity().posX, golem.getGolemEntity().posY, golem.getGolemEntity().posZ);
            IBlockState bb2 = world.getBlockState(task.getPos().down());
            ReplantInfo ri = replantTasks.get(task.getPos().toLong());
            if ((bb2.getBlock() instanceof BlockDirt || bb2.getBlock() instanceof BlockGrass) && ri.farmland) {
                Items.DIAMOND_HOE.onItemUse(fp, world, task.getPos().down(), EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
            }
            ItemStack seed = ri.stack.copy();
            seed.setCount(1);
            if (seed.getItem().onItemUse(fp, world, task.getPos().offset(ri.face), EnumHand.MAIN_HAND, ri.face.getOpposite(), 0.5f, 0.5f, 0.5f) == EnumActionResult.SUCCESS) {
                world.playBroadcastSound(2001, task.getPos(), Block.getStateId(world.getBlockState(task.getPos())));
                golem.dropItem(seed);
                golem.addRankXp(1);
                golem.swingArm();
            }
        }
        task.setSuspended(true);
        return true;
    }
~~~

### src/main/java/thaumcraft/common/items/casters/foci/FocusEffectFlux.java

~~~java
public void onCast(Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.PLAYERS, 2.0f, 2.0f + (float)(caster.world.rand.nextGaussian() * 0.10000000149011612));
    }
~~~

### src/main/java/thaumcraft/common/items/casters/foci/FocusEffectHeal.java

~~~java
public void onCast(Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.PLAYERS, 2.0f, 2.0f + (float)(caster.world.rand.nextGaussian() * 0.10000000149011612));
    }
~~~

### src/main/java/thaumcraft/common/items/tools/ItemElementalHoe.java

~~~java
public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }
        boolean did = false;
        for (int xx = -1; xx <= 1; ++xx) {
            for (int zz = -1; zz <= 1; ++zz) {
                if (super.onItemUse(player, world, pos.add(xx, 0, zz), hand, facing, hitX, hitY, hitZ) == EnumActionResult.SUCCESS) {
                    if (world.isRemote) {
                        BlockPos pp = pos.add(xx, 0, zz);
                        FXDispatcher.INSTANCE.drawBamf(pp.getX() + 0.5, pp.getY() + 1.01, pp.getZ() + 0.5, 0.3f, 0.12f, 0.1f, xx == 0 && zz == 0, false, EnumFacing.UP);
                    }
                    if (!did) {
                        did = true;
                    }
                }
            }
        }
        if (!did) {
            did = Utils.useBonemealAtLoc(world, player, pos);
            if (did) {
                player.getHeldItem(hand).damageItem(3, player);
                if (!world.isRemote) {
                    world.playBroadcastSound(2005, pos, 0);
                }
                else {
                    FXDispatcher.INSTANCE.drawBlockMistParticles(pos, 4259648);
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }
~~~

### src/main/java/thaumcraft/common/items/tools/ItemElementalSword.java

~~~java
public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        super.onUsingTick(stack, player, count);
        int ticks = getMaxItemUseDuration(stack) - count;
        if (player.motionY < 0.0) {
            player.motionY /= 1.2000000476837158;
            player.fallDistance /= 1.2f;
        }
        player.motionY += 0.07999999821186066;
        if (player.motionY > 0.5) {
            player.motionY = 0.20000000298023224;
        }
        if (player instanceof EntityPlayerMP) {
            EntityUtils.resetFloatCounter((EntityPlayerMP)player);
        }
        List<Entity> targets = player.world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().grow(2.5, 2.5, 2.5));
        if (targets.size() > 0) {
            for (int var9 = 0; var9 < targets.size(); ++var9) {
                Entity entity = targets.get(var9);
                if (!(entity instanceof EntityPlayer)) {
                    if (entity instanceof EntityLivingBase) {
                        if (!entity.isDead) {
                            if (player.getRidingEntity() == null || player.getRidingEntity() != entity) {
                                Vec3d p = new Vec3d(player.posX, player.posY, player.posZ);
                                Vec3d t = new Vec3d(entity.posX, entity.posY, entity.posZ);
                                double distance = p.distanceTo(t) + 0.1;
                                Vec3d r = new Vec3d(t.x - p.x, t.y - p.y, t.z - p.z);
                                Entity entity2 = entity;
                                entity2.motionX += r.x / 2.5 / distance;
                                Entity entity3 = entity;
                                entity3.motionY += r.y / 2.5 / distance;
                                Entity entity4 = entity;
                                entity4.motionZ += r.z / 2.5 / distance;
                            }
                        }
                    }
                }
            }
        }
        if (player.world.isRemote) {
            int miny = (int)(player.getEntityBoundingBox().minY - 2.0);
            if (player.onGround) {
                miny = MathHelper.floor(player.getEntityBoundingBox().minY);
            }
            for (int a = 0; a < 5; ++a) {
                FXDispatcher.INSTANCE.smokeSpiral(player.posX, player.getEntityBoundingBox().minY + player.height / 2.0f, player.posZ, 1.5f, player.world.rand.nextInt(360), miny, 14540253);
            }
            if (player.onGround) {
                float r2 = player.world.rand.nextFloat() * 360.0f;
                float mx = -MathHelper.sin(r2 / 180.0f * 3.1415927f) / 5.0f;
                float mz = MathHelper.cos(r2 / 180.0f * 3.1415927f) / 5.0f;
                player.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, player.posX, player.getEntityBoundingBox().minY + 0.10000000149011612, player.posZ, mx, 0.0, mz);
            }
        }
        else if (ticks == 0 || ticks % 20 == 0) {
            player.playSound(SoundsTC.wind, 0.5f, 0.9f + player.world.rand.nextFloat() * 0.2f);
        }
        if (ticks % 20 == 0) {
            stack.damageItem(1, player);
        }
    }
~~~

### src/main/java/thaumcraft/common/lib/events/CraftingEvents.java

~~~java
public int getBurnTime(ItemStack fuel) {
        if (fuel.isItemEqual(new ItemStack(ItemsTC.alumentum))) {
            return 4800;
        }
        if (fuel.isItemEqual(new ItemStack(BlocksTC.logGreatwood))) {
            return 500;
        }
        if (fuel.isItemEqual(new ItemStack(BlocksTC.logSilverwood))) {
            return 400;
        }
        return 0;
    }
~~~

~~~java
public static void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        int warp = ThaumcraftApi.getWarp(event.crafting);
        if (!ModConfig.CONFIG_MISC.wussMode && warp > 0 && !event.player.world.isRemote) {
            ThaumcraftApi.internalMethods.addWarpToPlayer(event.player, warp, IPlayerWarp.EnumWarpType.NORMAL);
        }
        if (event.crafting.getItem() == ItemsTC.label && event.crafting.hasTagCompound()) {
            for (int var2 = 0; var2 < 9; ++var2) {
                ItemStack var3 = event.craftMatrix.getStackInSlot(var2);
                if (var3 != null && var3.getItem() instanceof ItemPhial) {
                    var3.grow(1);
                    event.craftMatrix.setInventorySlotContents(var2, var3);
                }
            }
        }
        if (event.player != null && !event.player.world.isRemote) {
            int stackHash = ResearchManager.createItemStackHash(event.crafting.copy());
            if (ResearchManager.craftingReferences.contains(stackHash)) {
                ResearchManager.completeResearch(event.player, "[#]" + stackHash);
            }
            else {
                stackHash = ResearchManager.createItemStackHash(new ItemStack(event.crafting.getItem(), event.crafting.getCount(), event.crafting.getItemDamage()));
                if (ResearchManager.craftingReferences.contains(stackHash)) {
                    ResearchManager.completeResearch(event.player, "[#]" + stackHash);
                }
            }
            try {
                int[] oreIDs;
                int[] ids = oreIDs = OreDictionary.getOreIDs(event.crafting.copy());
                for (int id : oreIDs) {
                    String ore = OreDictionary.getOreName(id);
                    if (ore != null) {
                        int cd = ("oredict:" + ore).hashCode();
                        if (ore != null && ResearchManager.craftingReferences.contains(cd)) {
                            ResearchManager.completeResearch(event.player, "[#]" + cd);
                        }
                    }
                }
            }
            catch (Exception ex) {}
        }
    }
~~~

### src/main/java/thaumcraft/common/lib/events/ServerEvents.java

~~~java
public static void tickChunkRegeneration(TickEvent.WorldTickEvent event) {
        int dim = event.world.provider.getDimension();
        int count = 0;
        ArrayList<ChunkPos> chunks = ServerEvents.chunksToGenerate.get(dim);
        if (chunks != null && chunks.size() > 0) {
            for (int a = 0; a < 10; ++a) {
                chunks = ServerEvents.chunksToGenerate.get(dim);
                if (chunks == null || chunks.size() <= 0) {
                    break;
                }
                ++count;
                ChunkPos loc = chunks.get(0);
                long worldSeed = event.world.getSeed();
                Random fmlRandom = new Random(worldSeed);
                long xSeed = fmlRandom.nextLong() >> 3;
                long zSeed = fmlRandom.nextLong() >> 3;
                fmlRandom.setSeed(xSeed * loc.x + zSeed * loc.z ^ worldSeed);
                ThaumcraftWorldGenerator.INSTANCE.worldGeneration(fmlRandom, loc.x, loc.z, event.world, false);
                chunks.remove(0);
                ServerEvents.chunksToGenerate.put(dim, chunks);
            }
        }
        if (count > 0) {
            FMLCommonHandler.instance().getFMLLogger().log(Level.INFO, "[Thaumcraft] Regenerated " + count + " chunks. " + Math.max(0, chunks.size()) + " chunks left");
        }
    }
~~~

### src/main/java/thaumcraft/common/lib/events/ToolEvents.java

~~~java
public static void playerAttack(AttackEntityEvent event) {
        if (event.getEntityPlayer().getActiveHand() == null) {
            return;
        }
        ItemStack heldItem = event.getEntityPlayer().getHeldItem(event.getEntityPlayer().getActiveHand());
        if (heldItem != null && !heldItem.isEmpty()) {
            List<EnumInfusionEnchantment> list = EnumInfusionEnchantment.getInfusionEnchantments(heldItem);
            if (list.contains(EnumInfusionEnchantment.ARCING) && event.getTarget().isEntityAlive()) {
                int rank = EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldItem, EnumInfusionEnchantment.ARCING);
                List<Entity> targets = event.getEntityPlayer().world.getEntitiesWithinAABBExcludingEntity(event.getEntityPlayer(), event.getTarget().getEntityBoundingBox().grow(1.5 + rank, 1.0f + rank / 2.0f, 1.5 + rank));
                int count = 0;
                if (targets.size() > 1) {
                    for (int var9 = 0; var9 < targets.size(); ++var9) {
                        Entity var10 = targets.get(var9);
                        if (!var10.isDead) {
                            if (!EntityUtils.isFriendly(event.getEntity(), var10)) {
                                if (var10 instanceof EntityLiving && var10.getEntityId() != event.getTarget().getEntityId()) {
                                    if (!(var10 instanceof EntityPlayer) || var10.getName() != event.getEntityPlayer().getName()) {
                                        if (var10.isEntityAlive()) {
                                            float f = (float)event.getEntityPlayer().getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                                            event.getEntityPlayer().attackEntityAsMob(var10);
                                            if (var10.attackEntityFrom(DamageSource.causePlayerDamage(event.getEntityPlayer()), f * 0.5f)) {
                                                try {
                                                    if (var10 instanceof EntityLivingBase) {
                                                        EnchantmentHelper.applyThornEnchantments((EntityLivingBase)var10, event.getEntityPlayer());
                                                    }
                                                }
                                                catch (Exception ex) {}
                                                var10.addVelocity(-MathHelper.sin(event.getEntityPlayer().rotationYaw * 3.1415927f / 180.0f) * 0.5f, 0.1, MathHelper.cos(event.getEntityPlayer().rotationYaw * 3.1415927f / 180.0f) * 0.5f);
                                                ++count;
                                                if (!event.getEntityPlayer().world.isRemote) {
                                                    PacketHandler.INSTANCE.sendToAllAround(new PacketFXSlash(event.getTarget().getEntityId(), var10.getEntityId()), new NetworkRegistry.TargetPoint(event.getEntityPlayer().world.provider.getDimension(), event.getTarget().posX, event.getTarget().posY, event.getTarget().posZ, 64.0));
                                                }
                                            }
                                        }
                                    }
                                }
                                if (count >= rank) {
                                    break;
                                }
                            }
                        }
                    }
                    if (count > 0 && !event.getEntityPlayer().world.isRemote) {
                        event.getEntityPlayer().playSound(SoundsTC.wind, 1.0f, 0.9f + event.getEntityPlayer().world.rand.nextFloat() * 0.2f);
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXSlash(event.getEntityPlayer().getEntityId(), event.getTarget().getEntityId()), new NetworkRegistry.TargetPoint(event.getEntityPlayer().world.provider.getDimension(), event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, 64.0));
                    }
                }
            }
        }
    }
~~~

### src/main/java/thaumcraft/common/lib/events/WarpEvents.java

~~~java
public static void checkDeathGaze(EntityPlayer player) {
        PotionEffect pe = player.getActivePotionEffect(PotionDeathGaze.instance);
        if (pe == null) {
            return;
        }
        int level = pe.getAmplifier();
        int range = Math.min(8 + level * 3, 24);
        List<Entity> list = player.world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().grow(range, range, range));
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (entity.canBeCollidedWith() && entity instanceof EntityLivingBase) {
                if (entity.isEntityAlive()) {
                    if (EntityUtils.isVisibleTo(0.75f, player, entity, (float)range)) {
                        if (entity != null && player.canEntityBeSeen(entity) && (!(entity instanceof EntityPlayer) || FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()) && !((EntityLivingBase)entity).isPotionActive(MobEffects.WITHER)) {
                            ((EntityLivingBase)entity).setRevengeTarget(player);
                            ((EntityLivingBase)entity).setLastAttackedEntity(player);
                            if (entity instanceof EntityCreature) {
                                ((EntityCreature)entity).setAttackTarget(player);
                            }
                            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.WITHER, 80));
                        }
                    }
                }
            }
        }
    }
~~~

### src/main/java/thaumcraft/common/lib/potions/PotionInfectiousVisExhaust.java

~~~java
public void performEffect(EntityLivingBase target, int par2) {
        List<EntityLivingBase> targets = target.world.getEntitiesWithinAABB(EntityLivingBase.class, target.getEntityBoundingBox().grow(4.0, 4.0, 4.0));
        if (targets.size() > 0) {
            for (EntityLivingBase e : targets) {
                if (!e.isPotionActive(PotionInfectiousVisExhaust.instance)) {
                    if (par2 > 0) {
                        e.addPotionEffect(new PotionEffect(PotionInfectiousVisExhaust.instance, 6000, par2 - 1, false, true));
                    }
                    else {
                        e.addPotionEffect(new PotionEffect(PotionVisExhaust.instance, 6000, 0, false, true));
                    }
                }
            }
        }
    }
~~~

### src/main/java/thaumcraft/common/lib/utils/CropUtils.java

~~~java
public static void addStandardCrop(ItemStack stack, int grownMeta) {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (block == null) {
            return;
        }
        addStandardCrop(block, grownMeta);
    }
~~~

~~~java
public static void addStandardCrop(Block block, int grownMeta) {
        if (grownMeta == 32767) {
            for (int a = 0; a < 16; ++a) {
                CropUtils.standardCrops.add(block.getUnlocalizedName() + a);
            }
        }
        else {
            CropUtils.standardCrops.add(block.getUnlocalizedName() + grownMeta);
        }
        if (block instanceof BlockCrops && grownMeta != 7) {
            CropUtils.standardCrops.add(block.getUnlocalizedName() + "7");
        }
    }
~~~

~~~java
public static void addClickableCrop(ItemStack stack, int grownMeta) {
        if (Block.getBlockFromItem(stack.getItem()) == null) {
            return;
        }
        if (grownMeta == 32767) {
            for (int a = 0; a < 16; ++a) {
                CropUtils.clickableCrops.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + a);
            }
        }
        else {
            CropUtils.clickableCrops.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + grownMeta);
        }
        if (Block.getBlockFromItem(stack.getItem()) instanceof BlockCrops && grownMeta != 7) {
            CropUtils.clickableCrops.add(Block.getBlockFromItem(stack.getItem()).getUnlocalizedName() + "7");
        }
    }
~~~

~~~java
public static void addStackedCrop(ItemStack stack, int grownMeta) {
        if (Block.getBlockFromItem(stack.getItem()) == null) {
            return;
        }
        addStackedCrop(Block.getBlockFromItem(stack.getItem()), grownMeta);
    }
~~~

~~~java
public static void addStackedCrop(Block block, int grownMeta) {
        if (grownMeta == 32767) {
            for (int a = 0; a < 16; ++a) {
                CropUtils.stackedCrops.add(block.getUnlocalizedName() + a);
            }
        }
        else {
            CropUtils.stackedCrops.add(block.getUnlocalizedName() + grownMeta);
        }
        if (block instanceof BlockCrops && grownMeta != 7) {
            CropUtils.stackedCrops.add(block.getUnlocalizedName() + "7");
        }
    }
~~~

~~~java
public static boolean isGrownCrop(World world, BlockPos pos) {
        if (world.isAirBlock(pos)) {
            return false;
        }
        boolean found = false;
        IBlockState bs = world.getBlockState(pos);
        Block bi = bs.getBlock();
        int md = bi.getMetaFromState(bs);
        if (CropUtils.standardCrops.contains(bi.getUnlocalizedName() + md) || CropUtils.clickableCrops.contains(bi.getUnlocalizedName() + md) || CropUtils.stackedCrops.contains(bi.getUnlocalizedName() + md)) {
            found = true;
        }
        Block biB = world.getBlockState(pos.down()).getBlock();
        return (bi instanceof IGrowable && !((IGrowable)bi).canGrow(world, pos, world.getBlockState(pos), world.isRemote) && !(bi instanceof BlockStem)) || (bi instanceof BlockCrops && md == 7 && !found) || CropUtils.standardCrops.contains(bi.getUnlocalizedName() + md) || CropUtils.clickableCrops.contains(bi.getUnlocalizedName() + md) || (CropUtils.stackedCrops.contains(bi.getUnlocalizedName() + md) && biB == bi);
    }
~~~

~~~java
public static boolean doesLampGrow(World world, BlockPos pos) {
        Block bi = world.getBlockState(pos).getBlock();
        int md = bi.getMetaFromState(world.getBlockState(pos));
        return !CropUtils.lampBlacklist.contains(bi.getUnlocalizedName() + md);
    }
~~~

### src/main/java/thaumcraft/common/lib/utils/EntityUtils.java

~~~java
public static Entity getPointedEntity(World world, RayTraceResult ray, Vec3d lookVec, double minrange, double range, float padding, boolean nonCollide) {
        Entity pointedEntity = null;
        double d = range;
        Vec3d entityVec = new Vec3d(ray.hitVec.x, ray.hitVec.y, ray.hitVec.z);
        Vec3d vec3d2 = entityVec.addVector(lookVec.x * d, lookVec.y * d, lookVec.z * d);
        float f1 = padding;
        AxisAlignedBB bb = (ray.entityHit != null) ? ray.entityHit.getEntityBoundingBox() : new AxisAlignedBB(ray.hitVec.x, ray.hitVec.y, ray.hitVec.z, ray.hitVec.x, ray.hitVec.y, ray.hitVec.z).grow(0.5);
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(ray.entityHit, bb.expand(lookVec.x * d, lookVec.y * d, lookVec.z * d).grow(f1, f1, f1));
        double d2 = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (ray.hitVec.distanceTo(entity.getPositionVector()) >= minrange) {
                if (entity.canBeCollidedWith() || nonCollide) {
                    if (world.rayTraceBlocks(ray.hitVec, new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), false, true, false) == null) {
                        float f2 = Math.max(0.8f, entity.getCollisionBorderSize());
                        AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(f2, f2, f2);
                        RayTraceResult RayTraceResult = axisalignedbb.calculateIntercept(entityVec, vec3d2);
                        if (axisalignedbb.contains(entityVec)) {
                            if (0.0 < d2 || d2 == 0.0) {
                                pointedEntity = entity;
                                d2 = 0.0;
                            }
                        }
                        else if (RayTraceResult != null) {
                            double d3 = entityVec.distanceTo(RayTraceResult.hitVec);
                            if (d3 < d2 || d2 == 0.0) {
                                pointedEntity = entity;
                                d2 = d3;
                            }
                        }
                    }
                }
            }
        }
        return pointedEntity;
    }
~~~

~~~java
public static RayTraceResult getPointedEntityRay(World world, Entity ignoreEntity, Vec3d startVec, Vec3d lookVec, double minrange, double range, float padding, boolean nonCollide) {
        RayTraceResult pointedEntityRay = null;
        double d = range;
        Vec3d vec3d2 = startVec.addVector(lookVec.x * d, lookVec.y * d, lookVec.z * d);
        float f1 = padding;
        AxisAlignedBB bb = (ignoreEntity != null) ? ignoreEntity.getEntityBoundingBox() : new AxisAlignedBB(startVec.x, startVec.y, startVec.z, startVec.x, startVec.y, startVec.z).grow(0.5);
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(ignoreEntity, bb.expand(lookVec.x * d, lookVec.y * d, lookVec.z * d).grow(f1, f1, f1));
        double d2 = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (startVec.distanceTo(entity.getPositionVector()) >= minrange) {
                if (entity.canBeCollidedWith() || nonCollide) {
                    if (world.rayTraceBlocks(startVec, new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), false, true, false) == null) {
                        float f2 = Math.max(0.8f, entity.getCollisionBorderSize());
                        AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(f2, f2, f2);
                        RayTraceResult rayTraceResult = axisalignedbb.calculateIntercept(startVec, vec3d2);
                        if (axisalignedbb.contains(startVec)) {
                            if (0.0 < d2 || d2 == 0.0) {
                                pointedEntityRay = new RayTraceResult(entity, rayTraceResult.hitVec);
                                d2 = 0.0;
                            }
                        }
                        else if (rayTraceResult != null) {
                            double d3 = startVec.distanceTo(rayTraceResult.hitVec);
                            if (d3 < d2 || d2 == 0.0) {
                                pointedEntityRay = new RayTraceResult(entity, rayTraceResult.hitVec);
                                d2 = d3;
                            }
                        }
                    }
                }
            }
        }
        return pointedEntityRay;
    }
~~~

~~~java
public static Entity getPointedEntity(World world, EntityLivingBase player, double range, Class<?> clazz) {
        Entity pointedEntity = null;
        double d = range;
        Vec3d vec3d = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d vec3d2 = player.getLookVec();
        Vec3d vec3d3 = vec3d.addVector(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        float f1 = 1.1f;
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().expand(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d).grow(f1, f1, f1));
        double d2 = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (entity.canBeCollidedWith() && world.rayTraceBlocks(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), false, true, false) == null) {
                if (!clazz.isInstance(entity)) {
                    float f2 = Math.max(0.8f, entity.getCollisionBorderSize());
                    AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(f2, f2, f2);
                    RayTraceResult RayTraceResult = axisalignedbb.calculateIntercept(vec3d, vec3d3);
                    if (axisalignedbb.contains(vec3d)) {
                        if (0.0 < d2 || d2 == 0.0) {
                            pointedEntity = entity;
                            d2 = 0.0;
                        }
                    }
                    else if (RayTraceResult != null) {
                        double d3 = vec3d.distanceTo(RayTraceResult.hitVec);
                        if (d3 < d2 || d2 == 0.0) {
                            pointedEntity = entity;
                            d2 = d3;
                        }
                    }
                }
            }
        }
        return pointedEntity;
    }
~~~

~~~java
public static <T extends Entity> List<T> getEntitiesInRange(World world, double x, double y, double z, Entity entity, Class<? extends T> classEntity, double range) {
        ArrayList<T> out = new ArrayList<T>();
        List list = world.getEntitiesWithinAABB(classEntity, new AxisAlignedBB(x, y, z, x, y, z).grow(range, range, range));
        if (list.size() > 0) {
            for (Object e : list) {
                Entity ent = (Entity)e;
                if (entity != null && entity.getEntityId() == ent.getEntityId()) {
                    continue;
                }
                out.add((T)ent);
            }
        }
        return out;
    }
~~~

~~~java
public static void makeChampion(EntityMob entity, boolean persist) {
        try {
            if (entity.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() > -2.0) {
                return;
            }
        }
        catch (Exception e) {
            return;
        }
        int type = entity.world.rand.nextInt(ChampionModifier.mods.length);
        if (entity instanceof EntityCreeper) {
            type = 0;
        }
        IAttributeInstance modai = entity.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD);
        modai.removeModifier(ChampionModifier.mods[type].attributeMod);
        modai.applyModifier(ChampionModifier.mods[type].attributeMod);
        if (!(entity instanceof EntityThaumcraftBoss)) {
            IAttributeInstance iattributeinstance = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            iattributeinstance.removeModifier(EntityUtils.CHAMPION_HEALTH);
            iattributeinstance.applyModifier(EntityUtils.CHAMPION_HEALTH);
            IAttributeInstance iattributeinstance2 = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            iattributeinstance2.removeModifier(EntityUtils.CHAMPION_DAMAGE);
            iattributeinstance2.applyModifier(EntityUtils.CHAMPION_DAMAGE);
            entity.heal(25.0f);
            entity.setCustomNameTag(ChampionModifier.mods[type].getModNameLocalized() + " " + entity.getName());
        }
        else {
            ((EntityThaumcraftBoss)entity).generateName();
        }
        if (persist) {
            entity.enablePersistence();
        }
        switch (type) {
            case 0: {
                IAttributeInstance sai = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                sai.removeModifier(EntityUtils.BOLDBUFF);
                sai.applyModifier(EntityUtils.BOLDBUFF);
                break;
            }
            case 3: {
                IAttributeInstance mai = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                mai.removeModifier(EntityUtils.MIGHTYBUFF);
                mai.applyModifier(EntityUtils.MIGHTYBUFF);
                break;
            }
            case 5: {
                int bh = (int)entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() / 2;
                entity.setAbsorptionAmount(entity.getAbsorptionAmount() + bh);
                break;
            }
        }
    }
~~~

~~~java
public static void makeTainted(EntityLivingBase target) {
        try {
            if (target.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD) != null && target.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() > -1.0) {
                return;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int type = 13;
        IAttributeInstance modai = target.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD);
        if (modai == null) {
            return;
        }
        if (target.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() == -1.0) {
            modai.applyModifier(ChampionModifier.ATTRIBUTE_MINUS_ONE);
        }
        modai.removeModifier(ChampionModifier.mods[type].attributeMod);
        modai.applyModifier(ChampionModifier.mods[type].attributeMod);
        if (!(target instanceof EntityThaumcraftBoss)) {
            IAttributeInstance iattributeinstance = target.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            iattributeinstance.removeModifier(EntityUtils.HPBUFF[5]);
            iattributeinstance.applyModifier(EntityUtils.HPBUFF[5]);
            IAttributeInstance iattributeinstance2 = target.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            if (iattributeinstance2 == null) {
                target.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                target.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Math.max(2.0f, (target.height + target.width) * 2.0f));
            }
            else {
                iattributeinstance2.removeModifier(EntityUtils.DMGBUFF[0]);
                iattributeinstance2.applyModifier(EntityUtils.DMGBUFF[0]);
            }
            target.heal(25.0f);
        }
        else {
            ((EntityThaumcraftBoss)target).generateName();
        }
    }
~~~

### src/main/java/thaumcraft/common/lib/utils/Utils.java

~~~java
public static boolean useBonemealAtLoc(World world, EntityPlayer player, BlockPos pos) {
        ItemStack is = new ItemStack(Items.DYE, 1, 15);
        ItemDye itemDye = (ItemDye)Items.DYE;
        return ItemDye.applyBonemeal(is, world, pos, player, EnumHand.MAIN_HAND);
    }
~~~

~~~java
public static ItemStack generateLoot(int rarity, Random rand) {
        ItemStack is = ItemStack.EMPTY;
        if (rarity > 0 && rand.nextFloat() < 0.025f * rarity) {
            is = genGear(rarity, rand);
            if (is.isEmpty()) {
                is = generateLoot(rarity, rand);
            }
        }
        else {
            switch (rarity) {
                default: {
                    is = ((WeightedRandomLoot)WeightedRandom.getRandomItem(rand, (List)WeightedRandomLoot.lootBagCommon)).item;
                    break;
                }
                case 1: {
                    is = ((WeightedRandomLoot)WeightedRandom.getRandomItem(rand, (List)WeightedRandomLoot.lootBagUncommon)).item;
                    break;
                }
                case 2: {
                    is = ((WeightedRandomLoot)WeightedRandom.getRandomItem(rand, (List)WeightedRandomLoot.lootBagRare)).item;
                    break;
                }
            }
        }
        if (is.getItem() == Items.BOOK) {
            EnchantmentHelper.addRandomEnchantment(rand, is, (int)(5.0f + rarity * 0.75f * rand.nextInt(18)), false);
        }
        return is.copy();
    }
~~~

### src/main/java/thaumcraft/common/tiles/crafting/TileGolemBuilder.java

~~~java
public void readSyncNBT(NBTTagCompound nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        golem = nbttagcompound.getLong("golem");
        cost = nbttagcompound.getInteger("cost");
        maxCost = nbttagcompound.getInteger("mcost");
        if (golem >= 0L) {
            try {
                props = GolemProperties.fromLong(golem);
                components = props.generateComponents();
            }
            catch (Exception e) {
                props = null;
                components = null;
                cost = 0;
                golem = -1L;
            }
        }
    }
~~~

~~~java
public void update() {
        super.update();
        boolean complete = false;
        if (!world.isRemote) {
            ++ticks;
            if (ticks % 5 == 0 && !complete && cost > 0 && golem >= 0L) {
                if (bufferedEssentia || drawEssentia()) {
                    bufferedEssentia = false;
                    --cost;
                    markDirty();
                }
                if (cost <= 0) {
                    ItemStack placer = new ItemStack(ItemsTC.golemPlacer);
                    placer.setTagInfo("props", new NBTTagLong(golem));
                    if (getStackInSlot(0).isEmpty() || (getStackInSlot(0).getCount() < getStackInSlot(0).getMaxStackSize() && getStackInSlot(0).isItemEqual(placer) && ItemStack.areItemStackTagsEqual(getStackInSlot(0), placer))) {
                        if (getStackInSlot(0) == null || getStackInSlot(0).isEmpty()) {
                            setInventorySlotContents(0, placer.copy());
                        }
                        else {
                            getStackInSlot(0).grow(1);
                        }
                        complete = true;
                        world.playSound(null, pos, SoundsTC.wand, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    }
                }
            }
        }
        else {
            if (press < 90 && cost > 0 && golem > 0L) {
                press += 6;
                if (press >= 60) {
                    world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.66f, 1.0f + world.rand.nextFloat() * 0.1f, false);
                    for (int a = 0; a < 16; ++a) {
                        FXDispatcher.INSTANCE.drawVentParticles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, world.rand.nextGaussian() * 0.1, 0.0, world.rand.nextGaussian() * 0.1, 11184810);
                    }
                }
            }
            if (press >= 90 && world.rand.nextInt(8) == 0) {
                FXDispatcher.INSTANCE.drawVentParticles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, world.rand.nextGaussian() * 0.1, 0.0, world.rand.nextGaussian() * 0.1, 11184810);
                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.1f, 1.0f + world.rand.nextFloat() * 0.1f, false);
            }
            if (press > 0 && (cost <= 0 || golem == -1L)) {
                if (press >= 90) {
                    for (int a = 0; a < 10; ++a) {
                        FXDispatcher.INSTANCE.drawVentParticles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, world.rand.nextGaussian() * 0.1, 0.0, world.rand.nextGaussian() * 0.1, 11184810);
                    }
                }
                press -= 3;
            }
        }
        if (complete) {
            cost = 0;
            golem = -1L;
            syncTile(false);
            markDirty();
        }
    }
~~~

~~~java
public boolean[] checkCraft(long id) {
        IGolemProperties props = GolemProperties.fromLong(id);
        ItemStack[] cc = props.generateComponents();
        boolean[] ret = new boolean[cc.length];
        int a = 0;
        for (ItemStack stack : props.generateComponents()) {
            ret[a] = InventoryUtils.checkAdjacentChests(world, pos, stack);
            ++a;
        }
        return ret;
    }
~~~

~~~java
public boolean startCraft(long id, EntityPlayer p) {
        ItemStack placer = new ItemStack(ItemsTC.golemPlacer);
        placer.setTagInfo("props", new NBTTagLong(id));
        if (getStackInSlot(0) != null && !getStackInSlot(0).isEmpty() && (getStackInSlot(0).getCount() >= getStackInSlot(0).getMaxStackSize() || !getStackInSlot(0).isItemEqual(placer) || !ItemStack.areItemStackTagsEqual(getStackInSlot(0), placer))) {
            cost = 0;
            props = null;
            components = null;
            golem = -1L;
            return false;
        }
        golem = id;
        props = GolemProperties.fromLong(golem);
        components = props.generateComponents();
        if (!InventoryUtils.consumeItemsFromAdjacentInventoryOrPlayer(getWorld(), getPos(), p, true, components)) {
            cost = 0;
            props = null;
            components = null;
            golem = -1L;
            return false;
        }
        cost = props.getTraits().size() * 2;
        for (ItemStack stack : components) {
            cost += stack.getCount();
        }
        InventoryUtils.consumeItemsFromAdjacentInventoryOrPlayer(getWorld(), getPos(), p, false, components);
        maxCost = cost;
        markDirty();
        syncTile(false);
        world.playSound(null, pos, SoundsTC.wand, SoundCategory.BLOCKS, 0.25f, 1.0f);
        return true;
    }
~~~

### src/main/java/thaumcraft/common/tiles/crafting/TileInfusionMatrix.java

~~~java
public void craftCycle() {
        boolean valid = false;
        float ff = world.rand.nextFloat() * getLossPerCycle();
        stability -= ff;
        stability += stabilityReplenish;
        if (stability < -100.0f) {
            stability = -100.0f;
        }
        if (stability > stabilityCap) {
            stability = (float) stabilityCap;
        }
        TileEntity te = world.getTileEntity(pos.down(2));
        if (te != null && te instanceof TilePedestal) {
            TilePedestal ped = (TilePedestal)te;
            if (!ped.getStackInSlot(0).isEmpty()) {
                ItemStack i2 = ped.getStackInSlot(0).copy();
                if (recipeInput.getItemDamage() == 32767) {
                    i2.setItemDamage(32767);
                }
                if (ThaumcraftInvHelper.areItemStacksEqualForCrafting(i2, recipeInput)) {
                    valid = true;
                }
            }
        }
        if (!valid || (stability < 0.0f && world.rand.nextInt(1500) <= Math.abs(stability))) {
            switch (world.rand.nextInt(24)) {
                case 0:
                case 1:
                case 2:
                case 3: {
                    inEvEjectItem(0);
                    break;
                }
                case 4:
                case 5:
                case 6: {
                    inEvWarp();
                    break;
                }
                case 7:
                case 8:
                case 9: {
                    inEvZap(false);
                    break;
                }
                case 10:
                case 11: {
                    inEvZap(true);
                    break;
                }
                case 12:
                case 13: {
                    inEvEjectItem(1);
                    break;
                }
                case 14:
                case 15: {
                    inEvEjectItem(2);
                    break;
                }
                case 16: {
                    inEvEjectItem(3);
                    break;
                }
                case 17: {
                    inEvEjectItem(4);
                    break;
                }
                case 18:
                case 19: {
                    inEvHarm(false);
                    break;
                }
                case 20:
                case 21: {
                    inEvEjectItem(5);
                    break;
                }
                case 22: {
                    inEvHarm(true);
                    break;
                }
                case 23: {
                    world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.5f + world.rand.nextFloat(), false);
                    break;
                }
            }
            stability += 5.0f + world.rand.nextFloat() * 5.0f;
            inResAdd();
            if (valid) {
                return;
            }
        }
        if (!valid) {
            crafting = false;
            recipeEssentia = new AspectList();
            recipeInstability = 0;
            syncTile(false);
            world.playSound(null, pos, SoundsTC.craftfail, SoundCategory.BLOCKS, 1.0f, 0.6f);
            markDirty();
            return;
        }
        if (recipeType == 1 && recipeXP > 0) {
            List<EntityPlayer> targets = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0, 10.0, 10.0));
            if (targets != null && targets.size() > 0) {
                for (EntityPlayer target : targets) {
                    if (target.capabilities.isCreativeMode || target.experienceLevel > 0) {
                        if (!target.capabilities.isCreativeMode) {
                            target.addExperienceLevel(-1);
                        }
                        --recipeXP;
                        target.attackEntityFrom(DamageSource.MAGIC, (float) world.rand.nextInt(2));
                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXInfusionSource(pos, pos, target.getEntityId()), new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
                        target.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 1.0f, 2.0f + world.rand.nextFloat() * 0.4f);
                        countDelay = cycleTime;
                        return;
                    }
                }
                Aspect[] ingEss = recipeEssentia.getAspects();
                if (ingEss != null && ingEss.length > 0 && world.rand.nextInt(3) == 0) {
                    Aspect as = ingEss[world.rand.nextInt(ingEss.length)];
                    recipeEssentia.add(as, 1);
                    stability -= 0.25f;
                    syncTile(false);
                    markDirty();
                }
            }
            return;
        }
        if (recipeType == 1 && recipeXP == 0) {
            countDelay = cycleTime / 2;
        }
        if (countDelay < 1) {
            countDelay = 1;
        }
        if (recipeEssentia.visSize() > 0) {
            for (Aspect aspect : recipeEssentia.getAspects()) {
                int na = recipeEssentia.getAmount(aspect);
                if (na > 0) {
                    if (EssentiaHandler.drainEssentia(this, aspect, null, 12, (na > 1) ? countDelay : 0)) {
                        recipeEssentia.reduce(aspect, 1);
                        syncTile(false);
                        markDirty();
                        return;
                    }
                    stability -= 0.25f;
                    syncTile(false);
                    markDirty();
                }
            }
            checkSurroundings = true;
            return;
        }
        if (recipeIngredients.size() > 0) {
            for (int a = 0; a < recipeIngredients.size(); ++a) {
                for (BlockPos cc : pedestals) {
                    te = world.getTileEntity(cc);
                    if (te != null && te instanceof TilePedestal && ((TilePedestal)te).getStackInSlot(0) != null && !((TilePedestal)te).getStackInSlot(0).isEmpty() && ThaumcraftInvHelper.areItemStacksEqualForCrafting(((TilePedestal)te).getStackInSlot(0), recipeIngredients.get(a))) {
                        if (itemCount == 0) {
                            itemCount = 5;
                            PacketHandler.INSTANCE.sendToAllAround(new PacketFXInfusionSource(pos, cc, 0), new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
                        }
                        else if (itemCount-- <= 1) {
                            ItemStack is = ((TilePedestal)te).getStackInSlot(0).getItem().getContainerItem(((TilePedestal)te).getStackInSlot(0));
                            ((TilePedestal)te).setInventorySlotContents(0, (is == null || is.isEmpty()) ? ItemStack.EMPTY : is.copy());
                            te.markDirty();
                            ((TilePedestal)te).syncTile(false);
                            recipeIngredients.remove(a);
                            markDirty();
                        }
                        return;
                    }
                }
                Aspect[] ingEss = recipeEssentia.getAspects();
                if (ingEss != null && ingEss.length > 0 && world.rand.nextInt(1 + a) == 0) {
                    Aspect as = ingEss[world.rand.nextInt(ingEss.length)];
                    recipeEssentia.add(as, 1);
                    stability -= 0.25f;
                    syncTile(false);
                    markDirty();
                }
            }
            return;
        }
        crafting = false;
        craftingFinish(recipeOutput, recipeOutputLabel);
        recipeOutput = null;
        syncTile(false);
        markDirty();
    }
~~~

~~~java
private void inEvZap(boolean all) {
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0, 10.0, 10.0));
        if (targets != null && targets.size() > 0) {
            for (EntityLivingBase target : targets) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(pos, target, 0.3f - world.rand.nextFloat() * 0.1f, 0.0f, 0.3f - world.rand.nextFloat() * 0.1f), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
                target.attackEntityFrom(DamageSource.MAGIC, (float)(4 + world.rand.nextInt(4)));
                if (!all) {
                    break;
                }
            }
        }
    }
~~~

~~~java
private void inEvHarm(boolean all) {
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0, 10.0, 10.0));
        if (targets != null && targets.size() > 0) {
            for (EntityLivingBase target : targets) {
                if (world.rand.nextBoolean()) {
                    target.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 120, 0, false, true));
                }
                else {
                    PotionEffect pe = new PotionEffect(PotionVisExhaust.instance, 2400, 0, true, true);
                    pe.getCurativeItems().clear();
                    target.addPotionEffect(pe);
                }
                if (!all) {
                    break;
                }
            }
        }
    }
~~~

~~~java
private void inResAdd() {
        List<EntityPlayer> targets = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0));
        if (targets != null && targets.size() > 0) {
            for (EntityPlayer player : targets) {
                IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
                if (!knowledge.isResearchKnown("!INSTABILITY")) {
                    knowledge.addResearch("!INSTABILITY");
                    knowledge.sync((EntityPlayerMP)player);
                    player.sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("got.instability")), true);
                }
            }
        }
    }
~~~

~~~java
private void inEvWarp() {
        List<EntityPlayer> targets = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(10.0));
        if (targets != null && targets.size() > 0) {
            EntityPlayer target = targets.get(world.rand.nextInt(targets.size()));
            if (world.rand.nextFloat() < 0.25f) {
                ThaumcraftApi.internalMethods.addWarpToPlayer(target, 1, IPlayerWarp.EnumWarpType.NORMAL);
            }
            else {
                ThaumcraftApi.internalMethods.addWarpToPlayer(target, 2 + world.rand.nextInt(4), IPlayerWarp.EnumWarpType.TEMPORARY);
            }
        }
    }
~~~

### src/main/java/thaumcraft/common/tiles/devices/TileJarBrain.java

~~~java
public Entity getClosestXPOrb() {
        double cdist = Double.MAX_VALUE;
        Entity orb = null;
        List ents = world.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(8.0, 8.0, 8.0));
        if (ents.size() > 0) {
            for (Object ent : ents) {
                EntityXPOrb eo = (EntityXPOrb)ent;
                double d = getDistanceSq(eo.posX, eo.posY, eo.posZ);
                if (d < cdist) {
                    orb = eo;
                    cdist = d;
                }
            }
        }
        return orb;
    }
~~~

### src/main/java/thaumcraft/common/tiles/devices/TileLampFertility.java

~~~java
private void updateAnimals() {
        int distance = 7;
        List<EntityAnimal> var5 = world.getEntitiesWithinAABB(EntityAnimal.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(distance, distance, distance));
    Label_0314:
        for (EntityLivingBase var8 : var5) {
            EntityAnimal var7 = (EntityAnimal)var8;
            if (var7.getGrowingAge() == 0) {
                if (var7.isInLove()) {
                    continue;
                }
                ArrayList<EntityAnimal> sa = new ArrayList<EntityAnimal>();
                for (EntityLivingBase var9 : var5) {
                    if (var9.getClass().equals(var8.getClass())) {
                        sa.add((EntityAnimal)var9);
                    }
                }
                if (sa != null && sa.size() > 9) {
                    continue;
                }
                Iterator<EntityAnimal> var10 = sa.iterator();
                EntityAnimal partner = null;
                while (var10.hasNext()) {
                    EntityAnimal var11 = var10.next();
                    if (var11.getGrowingAge() == 0) {
                        if (var11.isInLove()) {
                            continue;
                        }
                        if (partner != null) {
                            charges -= 5;
                            var11.setInLove(null);
                            partner.setInLove(null);
                            break Label_0314;
                        }
                        partner = var11;
                    }
                }
            }
        }
    }
~~~

### src/main/java/thaumcraft/common/tiles/devices/TileLampGrowth.java

~~~java
public TileLampGrowth() {
        reserve = false;
        charges = -1;
        maxCharges = 20;
        lx = 0;
        ly = 0;
        lz = 0;
        lid = Blocks.AIR;
        lmd = 0;
        checklist = new ArrayList<BlockPos>();
        drawDelay = 0;
    }
~~~

~~~java
private void updatePlant() {
        IBlockState bs = world.getBlockState(new BlockPos(lx, ly, lz));
        if (lid != bs.getBlock() || lmd != bs.getBlock().getMetaFromState(bs)) {
            EntityPlayer p = world.getClosestPlayer(lx, ly, lz, 32.0, false);
            if (p != null) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockMist(new BlockPos(lx, ly, lz), 4259648), new NetworkRegistry.TargetPoint(world.provider.getDimension(), lx, ly, lz, 32.0));
            }
            lid = bs.getBlock();
            lmd = bs.getBlock().getMetaFromState(bs);
        }
        int distance = 6;
        if (checklist.size() == 0) {
            for (int a = -distance; a <= distance; ++a) {
                for (int b = -distance; b <= distance; ++b) {
                    checklist.add(getPos().add(a, distance, b));
                }
            }
            Collections.shuffle(checklist, world.rand);
        }
        int x = checklist.get(0).getX();
        int y = checklist.get(0).getY();
        int z = checklist.get(0).getZ();
        checklist.remove(0);
        while (y >= pos.getY() - distance) {
            BlockPos bp = new BlockPos(x, y, z);
            if (!world.isAirBlock(bp) && isPlant(bp) && getDistanceSq(x + 0.5, y + 0.5, z + 0.5) < distance * distance && !CropUtils.isGrownCrop(world, bp) && CropUtils.doesLampGrow(world, bp)) {
                --charges;
                lx = x;
                ly = y;
                lz = z;
                IBlockState bs2 = world.getBlockState(bp);
                lid = bs2.getBlock();
                lmd = bs2.getBlock().getMetaFromState(bs2);
                world.scheduleUpdate(bp, lid, 1);
                return;
            }
            --y;
        }
    }
~~~

### src/main/java/thaumcraft/common/tiles/devices/TileRechargePedestal.java

~~~java
public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1).grow(2.0, 2.0, 2.0);
    }
~~~

### src/main/java/thaumcraft/common/tiles/devices/TileStabilizer.java

~~~java
private void tryAddStability() {
        EnumFacing facing = BlockStateUtils.getFacing(getBlockMetadata());
        List<EntityFluxRift> targets = world.getEntitiesWithinAABB(EntityFluxRift.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(8.0));
        if (targets.size() > 0) {
            for (EntityFluxRift e : targets) {
                if (e.isDead) {
                    continue;
                }
                if (e.getStability() == EntityFluxRift.EnumStability.VERY_STABLE || !mitigate(1)) {
                    continue;
                }
                e.addStability();
                delay += 5;
                if (energy <= 0) {
                    return;
                }
            }
        }
    }
~~~

### src/main/java/thaumcraft/common/tiles/misc/TileBarrierStone.java

~~~java
public void update() {
        if (!world.isRemote) {
            if (count == 0) {
                count = world.rand.nextInt(100);
            }
            if (count % 5 == 0 && !gettingPower()) {
                List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 3, pos.getZ() + 1).grow(0.1, 0.1, 0.1));
                if (targets.size() > 0) {
                    for (EntityLivingBase e : targets) {
                        if (!e.onGround && !(e instanceof EntityPlayer)) {
                            e.addVelocity(-MathHelper.sin((e.rotationYaw + 180.0f) * 3.1415927f / 180.0f) * 0.2f, -0.1, MathHelper.cos((e.rotationYaw + 180.0f) * 3.1415927f / 180.0f) * 0.2f);
                        }
                    }
                }
            }
            if (++count % 100 == 0) {
                if (world.getBlockState(pos.up(1)) != BlocksTC.barrier.getDefaultState() && world.isAirBlock(pos.up(1))) {
                    world.setBlockState(pos.up(1), BlocksTC.barrier.getDefaultState(), 3);
                }
                if (world.getBlockState(pos.up(2)) != BlocksTC.barrier.getDefaultState() && world.isAirBlock(pos.up(2))) {
                    world.setBlockState(pos.up(2), BlocksTC.barrier.getDefaultState(), 3);
                }
            }
        }
    }
~~~

### src/main/java/thaumcraft/common/world/biomes/BiomeGenMagicalForest.java

~~~java
public WorldGenAbstractTree getRandomTreeFeature(Random par1Random) {
        return (par1Random.nextInt(18) == 0) ? new WorldGenSilverwoodTrees(false, 8, 5) : ((par1Random.nextInt(12) == 0) ? new WorldGenGreatwoodTrees(false, par1Random.nextInt(8) == 0) : bigTree);
    }
~~~

~~~java
public void decorate(World world, Random random, BlockPos pos) {
        for (int a = 0; a < 3; ++a) {
            BlockPos pp;
            for (pp = new BlockPos(pos), pp = pp.add(4 + random.nextInt(8), 0, 4 + random.nextInt(8)), pp = world.getHeight(pp); pp.getY() > 30 && world.getBlockState(pp).getBlock() != Blocks.GRASS; pp = pp.down()) {}
            Block l1 = world.getBlockState(pp).getBlock();
            if (l1 == Blocks.GRASS) {
                world.setBlockState(pp, BlocksTC.grassAmbient.getDefaultState(), 2);
                break;
            }
        }
        for (int k = random.nextInt(3), i = 0; i < k; ++i) {
            BlockPos p2 = new BlockPos(pos);
            p2 = p2.add(random.nextInt(16) + 8, 0, random.nextInt(16) + 8);
            p2 = world.getHeight(p2);
            BiomeGenMagicalForest.blobs.generate(world, random, p2);
        }
        for (int k = 0; k < 4; ++k) {
            for (int i = 0; i < 4; ++i) {
                if (random.nextInt(40) == 0) {
                    BlockPos p2 = new BlockPos(pos);
                    p2 = p2.add(k * 4 + 1 + 8 + random.nextInt(3), 0, i * 4 + 1 + 8 + random.nextInt(3));
                    p2 = world.getHeight(p2);
                    WorldGenBigMushroom worldgenbigmushroom = new WorldGenBigMushroom();
                    worldgenbigmushroom.generate(world, random, p2);
                }
            }
        }
        try {
            super.decorate(world, random, pos);
        }
        catch (Exception ex) {}
        for (int a = 0; a < 8; ++a) {
            BlockPos p3;
            for (p3 = new BlockPos(pos), p3 = p3.add(random.nextInt(16), 0, random.nextInt(16)), p3 = world.getHeight(p3); p3.getY() > 50 && world.getBlockState(p3).getBlock() != Blocks.GRASS; p3 = p3.down()) {}
            Block l2 = world.getBlockState(p3).getBlock();
            if (l2 == Blocks.GRASS && world.getBlockState(p3.up()).getBlock().isReplaceable(world, p3.up()) && isBlockAdjacentToWood(world, p3.up())) {
                world.setBlockState(p3.up(), BlocksTC.vishroom.getDefaultState(), 2);
            }
        }
    }
~~~

### src/main/java/thaumcraft/common/world/biomes/BiomeHandler.java

~~~java
public static void registerBiomeInfo(BiomeDictionary.Type type, float auraLevel, Aspect tag, boolean greatwood, float greatwoodchance) {
        BiomeHandler.biomeInfo.put(type, Arrays.asList(auraLevel, tag, greatwood, greatwoodchance));
    }
~~~

~~~java
public static float getBiomeSupportsGreatwood(int biomeId) {
        try {
            Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(Biome.getBiome(biomeId));
            for (BiomeDictionary.Type type : types) {
                if ((boolean)BiomeHandler.biomeInfo.get(type).get(2)) {
                    return (float) BiomeHandler.biomeInfo.get(type).get(3);
                }
            }
        }
        catch (Exception ex) {}
        return 0.0f;
    }
~~~

### src/main/java/thaumcraft/common/world/objects/WorldGenBigMagicTree.java

~~~java
public boolean generate(World worldIn, Random p_180709_2_, BlockPos p_180709_3_) {
        world = worldIn;
        basePos = p_180709_3_;
        rand = new Random(p_180709_2_.nextLong());
        if (heightLimit == 0) {
            heightLimit = 11 + rand.nextInt(heightLimitLimit);
        }
        if (!validTreeLocation()) {
            world = null;
            return false;
        }
        generateLeafNodeList();
        generateLeaves();
        generateTrunk();
        generateLeafNodeBases();
        world = null;
        return true;
    }
~~~

~~~java
private boolean validTreeLocation() {
        BlockPos down = basePos.down();
        IBlockState state = world.getBlockState(down);
        boolean isSoil = state.getBlock().canSustainPlant(state, world, down, EnumFacing.UP, (IPlantable)Blocks.SAPLING);
        if (!isSoil) {
            return false;
        }
        int i = checkBlockLine(basePos, basePos.up(heightLimit - 1));
        if (i == -1) {
            return true;
        }
        if (i < 6) {
            return false;
        }
        heightLimit = i;
        return true;
    }
~~~

### src/main/java/thaumcraft/common/world/objects/WorldGenCustomFlowers.java

~~~java
public boolean generate(World world, Random par2Random, BlockPos pos) {
        for (int var6 = 0; var6 < 18; ++var6) {
            int var7 = pos.getX() + par2Random.nextInt(8) - par2Random.nextInt(8);
            int var8 = pos.getY() + par2Random.nextInt(4) - par2Random.nextInt(4);
            int var9 = pos.getZ() + par2Random.nextInt(8) - par2Random.nextInt(8);
            BlockPos bp = new BlockPos(var7, var8, var9);
            if (world.isAirBlock(bp) && (world.getBlockState(bp.down()).getBlock() == Blocks.GRASS || world.getBlockState(bp.down()).getBlock() == Blocks.SAND)) {
                world.setBlockState(bp, plantBlock.getStateFromMeta(plantBlockMeta), 3);
            }
        }
        return true;
    }
~~~

### src/main/java/thaumcraft/common/world/objects/WorldGenGreatwoodTrees.java

~~~java
public WorldGenGreatwoodTrees(boolean par1, boolean spiders) {
        super(par1);
        rand = new Random();
        basePos = new int[] { 0, 0, 0 };
        heightLimit = 0;
        heightAttenuation = 0.618;
        branchDensity = 1.0;
        branchSlope = 0.38;
        scaleWidth = 1.2;
        leafDensity = 0.9;
        trunkSize = 2;
        heightLimitLimit = 11;
        leafDistanceLimit = 4;
        this.spiders = false;
        this.spiders = spiders;
    }
~~~

~~~java
public boolean generate(World par1World, Random par2Random, BlockPos pos) {
        world = par1World;
        long var6 = par2Random.nextLong();
        rand.setSeed(var6);
        basePos[0] = pos.getX();
        basePos[1] = pos.getY();
        basePos[2] = pos.getZ();
        if (heightLimit == 0) {
            heightLimit = heightLimitLimit + rand.nextInt(heightLimitLimit);
        }
        int x = 0;
        int z = 0;
        for (x = 0; x < trunkSize; ++x) {
            for (z = 0; z < trunkSize; ++z) {
                if (!validTreeLocation(x, z)) {
                    world = null;
                    return false;
                }
            }
        }
        world.setBlockToAir(pos);
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        generateTrunk();
        scaleWidth = 1.66;
        basePos[0] = pos.getX();
        basePos[1] = pos.getY() + height;
        basePos[2] = pos.getZ();
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        generateTrunk();
        if (spiders) {
            world.setBlockState(pos.down(), Blocks.MOB_SPAWNER.getDefaultState());
            TileEntityMobSpawner var7 = (TileEntityMobSpawner)par1World.getTileEntity(pos.down());
            if (var7 != null) {
                var7.getSpawnerBaseLogic().setEntityId(EntityList.getKey(EntityCaveSpider.class));
                for (int a = 0; a < 50; ++a) {
                    int xx = pos.getX() - 7 + par2Random.nextInt(14);
                    int yy = pos.getY() + par2Random.nextInt(10);
                    int zz = pos.getZ() - 7 + par2Random.nextInt(14);
                    if (par1World.isAirBlock(new BlockPos(xx, yy, zz)) && (BlockUtils.isBlockTouching(par1World, new BlockPos(xx, yy, zz), BlocksTC.leafGreatwood) || BlockUtils.isBlockTouching(par1World, new BlockPos(xx, yy, zz), BlocksTC.logGreatwood))) {
                        world.setBlockState(new BlockPos(xx, yy, zz), Blocks.WEB.getDefaultState());
                    }
                }
                par1World.setBlockState(pos.down(2), Blocks.CHEST.getDefaultState());
                TileEntityChest var8 = (TileEntityChest)par1World.getTileEntity(pos.down(2));
                if (var8 != null) {
                    var8.setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
                }
            }
        }
        world = null;
        return true;
    }
~~~

### src/main/java/thaumcraft/common/world/objects/WorldGenMound.java

~~~java
public boolean generate(World world, Random rand, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (!LocationIsValidSpawn(world, new BlockPos(i + 9, j + 9, k + 9)) || !LocationIsValidSpawn(world, new BlockPos(i, j + 9, k)) || !LocationIsValidSpawn(world, new BlockPos(i + 18, j + 9, k)) || !LocationIsValidSpawn(world, new BlockPos(i + 18, j + 9, k + 18)) || !LocationIsValidSpawn(world, new BlockPos(i, j + 9, k + 18))) {
            return false;
        }
        world.setBlockState(new BlockPos(i + 0, j + 8, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 8, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 8, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 8, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 8, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 8, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 8, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 8, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 8, k + 12), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 8, k + 13), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 8, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 9, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 9, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 9, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 9, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 9, k + 8), Blocks.STONE_STAIRS.getStateFromMeta(0));
        world.setBlockState(new BlockPos(i + 0, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 9, k + 10), Blocks.STONE_STAIRS.getStateFromMeta(0));
        world.setBlockState(new BlockPos(i + 0, j + 9, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 9, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 9, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 9, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 10, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 10, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 8, k + 3), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 8, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 8, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 8, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 8, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 8, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 8, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 8, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 8, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 8, k + 12), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 8, k + 13), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 8, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 8, k + 15), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 9, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 9, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 9, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 9, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 9, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 9, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 9, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 9, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 9, k + 12), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 9, k + 13), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 9, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 9, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 10, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 10, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 10, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 10, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 10, k + 8), Blocks.STONE_STAIRS.getStateFromMeta(0));
        world.setBlockState(new BlockPos(i + 1, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 10, k + 10), Blocks.STONE_STAIRS.getStateFromMeta(0));
        world.setBlockState(new BlockPos(i + 1, j + 10, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 10, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 10, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 10, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 11, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 11, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 11, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 11, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 1, j + 11, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 3), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 9), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 12), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 13), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 15), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 3), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 12), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 13), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 15), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 9, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 10, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 10, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 10, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 10, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 10, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 10, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 10, k + 10), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 10, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 10, k + 12), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 10, k + 13), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 10, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 10, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 11, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 11, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 11, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 11, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 11, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 11, k + 9), Blocks.STONEBRICK.getStateFromMeta(3));
        world.setBlockState(new BlockPos(i + 2, j + 11, k + 10), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 11, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 11, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 11, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 11, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 12, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 12, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 12, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 12, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 12, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 2, j + 12, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 4, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 4, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 4, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 4, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 4, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 4, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 4, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 4, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 4, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 4, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 4, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 4, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 4, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 5, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 5, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 5, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 5, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 5, k + 7), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 5, k + 8), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 5, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 5, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 5, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 5, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 5, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 5, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 5, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 6, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 6, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 6, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 6, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 6, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 6, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 6, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 6, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 6, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 6, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 6, k + 13), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 6, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 6, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 7, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 7, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 7, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 7, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 7, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 7, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 7, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 7, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 7, k + 11), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 7, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 7, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 7, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 7, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 7), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 8, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 1), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 9), Blocks.IRON_BARS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 12), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 9, k + 17), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 4), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 5), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 7), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 9), Blocks.IRON_BARS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 10, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 11, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 11, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 11, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 11, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 11, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 11, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 11, k + 9), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 11, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 11, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 11, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 11, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 11, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 11, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 12, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 12, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 12, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 12, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 12, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 12, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 12, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 12, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 12, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 12, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 3, j + 12, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 4, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 4, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 4, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 4, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 4, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 4, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 4, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 4, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 4, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 4, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 4, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 4, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 4, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 5, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 5, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 6, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 6, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 7, k + 3), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 7, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 0), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 4), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 5), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 9), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 10), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 8, k + 18), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 0), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 18), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 1), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 15), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 17), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 3), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 14), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 15), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 12, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 12, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 12, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 12, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 12, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 12, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 12, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 12, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 12, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 12, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 12, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 12, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 12, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 13, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 13, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 13, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 13, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 13, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 13, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 13, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 13, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 13, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 13, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 4, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 4, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 4, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 4, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 4, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 4, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 4, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 4, k + 10), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 4, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 4, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 4, k + 13), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 4, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 4, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 5, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 5, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 6, k + 3), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 6, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 7, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 7, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 0), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 4), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 13), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 8, k + 18), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 0), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 18), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 1), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 17), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 6), Blocks.STONE_STAIRS.getStateFromMeta(4));
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 12), Blocks.STONE_STAIRS.getStateFromMeta(4));
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 12), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 12, k + 15), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 0, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 0, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 0, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 0, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 0, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 0, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 0, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 1, k + 6), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 1, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 1, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 1, k + 9), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 1, k + 10), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 1, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 1, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 2, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 2, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 2, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 2, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 2, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 2, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 2, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 3, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 3, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 3, k + 8), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 3, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 3, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 3, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 3, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 4, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 4, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 4, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 4, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 4, k + 7), Blocks.STONE_STAIRS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 6, j + 4, k + 8), Blocks.STONE_STAIRS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 6, j + 4, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 4, k + 10), Blocks.STONE_STAIRS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 6, j + 4, k + 11), Blocks.STONE_STAIRS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 6, j + 4, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 4, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 4, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 4, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 5, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 5, k + 6), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 5, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 5, k + 15), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 6, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 6, k + 6), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 6, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 6, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 7, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 7, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 7, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 7, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 0), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 7), Blocks.STONE_STAIRS.getStateFromMeta(5));
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 8), Blocks.STONE_STAIRS.getStateFromMeta(5));
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 9), Blocks.STONE_STAIRS.getStateFromMeta(5));
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 10), Blocks.STONE_STAIRS.getStateFromMeta(5));
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 11), Blocks.STONE_STAIRS.getStateFromMeta(5));
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 12), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 8, k + 18), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 0), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 6), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 18), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 1), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 12), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 17), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 5), Blocks.STONE_STAIRS.getStateFromMeta(6));
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 7), Blocks.STONE_STAIRS.getStateFromMeta(7));
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 11), Blocks.STONE_STAIRS.getStateFromMeta(6));
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 13), Blocks.STONE_STAIRS.getStateFromMeta(7));
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 15), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 12, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 12, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 12, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 12, k + 6), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 12, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 12, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 12, k + 9), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 12, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 12, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 12, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 12, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 12, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 12, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 13, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 13, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 13, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 13, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 13, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 13, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 13, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 13, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 13, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 13, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 13, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 0, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 0, k + 7), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 0, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 0, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 0, k + 10), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 0, k + 11), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 0, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 1, k + 6), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 1, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 2, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 2, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 3, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 3, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 4, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 4, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 4, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 4, k + 6), Blocks.STONE_STAIRS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 4, k + 12), Blocks.STONE_STAIRS.getStateFromMeta(2));
        world.setBlockState(new BlockPos(i + 7, j + 4, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 4, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 4, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 5, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 5, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 6, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 6, k + 15), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 7, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 7, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 0), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 6), Blocks.STONE_STAIRS.getStateFromMeta(7));
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 12), Blocks.STONE_STAIRS.getStateFromMeta(6));
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 14), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 18), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 0), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 18), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 1), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 3), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 6), Blocks.STONE_STAIRS.getStateFromMeta(5));
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 12), Blocks.STONE_STAIRS.getStateFromMeta(5));
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 17), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 9), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 13, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 13, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 13, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 13, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 13, k + 8), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 13, k + 9), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 13, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 13, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 13, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 13, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 13, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 14, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 14, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 14, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 0, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 0, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 0, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 0, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 0, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 0, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 0, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 1, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 1, k + 12), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 2, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 2, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 3, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 3, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 4, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 4, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 4, k + 5), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 4, k + 6), Blocks.STONE_STAIRS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 4, k + 12), Blocks.STONE_STAIRS.getStateFromMeta(2));
        world.setBlockState(new BlockPos(i + 8, j + 4, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 4, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 4, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 5, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 5, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 6, k + 3), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 6, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 7, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 7, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 0), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 5), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 6), Blocks.STONE_STAIRS.getStateFromMeta(7));
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 12), Blocks.STONE_STAIRS.getStateFromMeta(6));
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 18), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 0), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 18), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 1), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 3), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 15), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 17), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 12, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 12, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 12, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 12, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 12, k + 7), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 12, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 12, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 12, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 12, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 12, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 12, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 12, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 12, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 13, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 13, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 13, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 13, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 13, k + 8), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 13, k + 9), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 13, k + 10), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 13, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 13, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 13, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 13, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 14, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 14, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 14, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 14, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 14, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 0, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 0, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 0, k + 8), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 0, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 0, k + 10), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 0, k + 11), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 0, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 1, k + 6), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 1, k + 12), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 2, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 2, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 3, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 3, k + 12), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 4, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 4, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 4, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 4, k + 6), Blocks.STONE_STAIRS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 4, k + 12), Blocks.STONE_STAIRS.getStateFromMeta(2));
        world.setBlockState(new BlockPos(i + 9, j + 4, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 4, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 4, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 5, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 5, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 6, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 6, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 7, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 7, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 0), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 6), Blocks.STONE_STAIRS.getStateFromMeta(7));
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 12), Blocks.STONE_STAIRS.getStateFromMeta(6));
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 13), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 18), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 0), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 15), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 18), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 0), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 3), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 15), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 17), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 1), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 7), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 12, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 13, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 13, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 13, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 13, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 13, k + 8), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 13, k + 9), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 13, k + 10), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 13, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 13, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 13, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 13, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 14, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 14, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 14, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 14, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 14, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 14, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 0, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 0, k + 7), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 0, k + 8), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 0, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 0, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 0, k + 11), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 0, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 1, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 1, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 2, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 2, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 3, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 3, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 4, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 4, k + 4), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 4, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 4, k + 6), Blocks.STONE_STAIRS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 4, k + 12), Blocks.STONE_STAIRS.getStateFromMeta(2));
        world.setBlockState(new BlockPos(i + 10, j + 4, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 4, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 4, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 5, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 5, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 6, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 6, k + 15), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 7, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 7, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 0), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 4), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 6), Blocks.STONE_STAIRS.getStateFromMeta(7));
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 12), Blocks.STONE_STAIRS.getStateFromMeta(6));
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 18), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 0), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 18), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 0), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 17), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 1), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 6), Blocks.STONE_STAIRS.getStateFromMeta(4));
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 12), Blocks.STONE_STAIRS.getStateFromMeta(4));
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 9), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 12), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 13, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 13, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 13, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 13, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 13, k + 8), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 13, k + 9), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 13, k + 10), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 13, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 13, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 13, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 13, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 14, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 14, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 14, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 14, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 14, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 14, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 0, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 0, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 0, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 0, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 0, k + 10), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 0, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 0, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 1, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 1, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 1, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 1, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 1, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 1, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 1, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 2, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 2, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 2, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 2, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 2, k + 10), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 2, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 2, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 3, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 3, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 3, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 3, k + 9), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 3, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 3, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 3, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 4, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 4, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 4, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 4, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 4, k + 7), Blocks.STONE_STAIRS.getStateFromMeta(0));
        world.setBlockState(new BlockPos(i + 11, j + 4, k + 8), Blocks.STONE_STAIRS.getStateFromMeta(0));
        world.setBlockState(new BlockPos(i + 11, j + 4, k + 9), Blocks.STONE_STAIRS.getStateFromMeta(0));
        world.setBlockState(new BlockPos(i + 11, j + 4, k + 10), Blocks.STONE_STAIRS.getStateFromMeta(0));
        world.setBlockState(new BlockPos(i + 11, j + 4, k + 11), Blocks.STONE_STAIRS.getStateFromMeta(0));
        world.setBlockState(new BlockPos(i + 11, j + 4, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 4, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 4, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 4, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 5, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 5, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 5, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 5, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 6, k + 3), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 6, k + 6), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 6, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 6, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 7, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 7, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 7, k + 12), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 7, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 0), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 7), Blocks.STONE_STAIRS.getStateFromMeta(4));
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 8), Blocks.STONE_STAIRS.getStateFromMeta(4));
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 9), Blocks.STONE_STAIRS.getStateFromMeta(4));
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 10), Blocks.STONE_STAIRS.getStateFromMeta(4));
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 11), Blocks.STONE_STAIRS.getStateFromMeta(4));
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 8, k + 18), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 0), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 12), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 18), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 12), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 15), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 1), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 3), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 5), Blocks.STONE_STAIRS.getStateFromMeta(6));
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 7), Blocks.STONE_STAIRS.getStateFromMeta(7));
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 11), Blocks.STONE_STAIRS.getStateFromMeta(6));
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 13), Blocks.STONE_STAIRS.getStateFromMeta(7));
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 17), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 12, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 12, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 12, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 12, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 12, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 12, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 12, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 12, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 12, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 12, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 12, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 12, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 12, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 13, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 13, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 13, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 13, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 13, k + 8), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 13, k + 9), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 13, k + 10), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 13, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 13, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 13, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 13, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 14, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 14, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 14, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 14, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 14, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 0, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 0, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 0, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 0, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 0, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 0, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 0, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 1, k + 6), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 1, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 1, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 1, k + 9), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 1, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 1, k + 11), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 1, k + 12), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 2, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 2, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 2, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 2, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 2, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 2, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 2, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 3, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 3, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 3, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 3, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 3, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 3, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 3, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 4, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 4, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 4, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 4, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 4, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 4, k + 8), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 4, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 4, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 4, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 4, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 4, k + 13), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 4, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 4, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 5, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 5, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 6, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 6, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 7, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 7, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 0), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 4), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 7), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 8), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 14), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 8, k + 18), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 0), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 18), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 1), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 6), Blocks.STONE_STAIRS.getStateFromMeta(5));
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 12), Blocks.STONE_STAIRS.getStateFromMeta(5));
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 17), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 6), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 11), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 12, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 13, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 13, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 13, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 13, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 13, k + 8), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 13, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 13, k + 10), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 13, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 13, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 13, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 13, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 14, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 14, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 14, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 4, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 4, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 4, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 4, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 4, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 4, k + 8), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 4, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 4, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 4, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 4, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 4, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 4, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 4, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 5, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 5, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 6, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 6, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 7, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 7, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 0), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 11), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 14), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 8, k + 18), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 0), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 18), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 1), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 3), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 17), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 13, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 13, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 13, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 13, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 13, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 13, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 13, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 13, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 13, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 13, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 13, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 4, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 4, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 4, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 4, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 4, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 4, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 4, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 4, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 4, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 4, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 4, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 4, k + 14), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 4, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 5, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 5, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 5, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 5, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 5, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 5, k + 8), Blocks.STONE_STAIRS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 5, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 6, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 6, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 6, k + 5), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 6, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 6, k + 7), Blocks.STONE_STAIRS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 6, k + 15), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 7, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 7, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 7, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 7, k + 6), Blocks.STONE_STAIRS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 7, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 0), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 5), Blocks.STONE_STAIRS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 11), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 12), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 18), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 0), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 18), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 1), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 17), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 3), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 13, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 13, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 13, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 13, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 13, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 13, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 13, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 13, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 13, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 4, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 4, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 4, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 4, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 4, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 4, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 4, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 4, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 4, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 4, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 4, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 4, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 4, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 5, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 5, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 5, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 5, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 5, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 5, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 5, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 5, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 5, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 5, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 5, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 5, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 5, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 6, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 6, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 6, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 6, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 6, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 6, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 6, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 6, k + 10), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 6, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 6, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 6, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 6, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 6, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 7, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 7, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 7, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 7, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 7, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 7, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 7, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 7, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 7, k + 11), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 7, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 7, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 7, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 7, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 1), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 8, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 1), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 5), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 7), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 10), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 14), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 9, k + 17), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 3), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 4), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 5), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 8), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 10), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 11), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 14), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 15), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 10, k + 17), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 3), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 5), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 6), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 7), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 8), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 9), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 10), Blocks.MOSSY_COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 11), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 12), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 13), Blocks.COBBLESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 11, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 9), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 10), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 13, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 15, j + 13, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 2), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 3), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 8), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 9), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 10), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 12), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 13), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 15), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 8, k + 16), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 2), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 3), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 8), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 9), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 10), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 12), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 13), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 15), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 9, k + 16), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 10, k + 3), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 10, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 10, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 10, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 10, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 10, k + 8), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 10, k + 9), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 10, k + 10), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 10, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 10, k + 12), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 10, k + 13), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 10, k + 14), Blocks.DIRT.getDefaultState());
        generate2(world, rand, i, j, k);
        return true;
    }
~~~

~~~java
public boolean generate2(World world, Random rand, int i, int j, int k) {
        world.setBlockState(new BlockPos(i + 16, j + 10, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 11, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 11, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 11, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 11, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 11, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 11, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 11, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 11, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 11, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 11, k + 12), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 11, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 11, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 12, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 12, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 12, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 16, j + 12, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 8, k + 3), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 8, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 8, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 8, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 8, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 8, k + 8), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 8, k + 9), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 8, k + 10), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 8, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 8, k + 12), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 8, k + 13), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 8, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 8, k + 15), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 9, k + 3), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 9, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 9, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 9, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 9, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 9, k + 8), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 9, k + 9), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 9, k + 10), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 9, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 9, k + 12), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 9, k + 13), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 9, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 9, k + 15), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 10, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 10, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 10, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 10, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 10, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 10, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 10, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 10, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 10, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 10, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 10, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 17, j + 11, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 8, k + 4), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 8, k + 5), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 8, k + 6), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 8, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 8, k + 8), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 8, k + 9), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 8, k + 10), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 8, k + 11), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 8, k + 12), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 8, k + 13), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 8, k + 14), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 9, k + 4), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 9, k + 5), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 9, k + 6), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 9, k + 7), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 9, k + 8), Blocks.DIRT.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 9, k + 9), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 9, k + 10), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 9, k + 11), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 9, k + 12), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 9, k + 13), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 9, k + 14), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 10, k + 7), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 18, j + 10, k + 8), Blocks.GRASS.getDefaultState());
        world.setBlockState(new BlockPos(i + 0, j + 10, k + 4), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 0, j + 10, k + 5), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 1, j + 10, k + 3), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 1, j + 10, k + 15), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 1, j + 11, k + 4), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 1, j + 11, k + 5), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 1, j + 11, k + 12), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 1, j + 11, k + 13), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 1, j + 11, k + 14), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 2, j + 11, k + 3), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 2, j + 12, k + 14), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 3, j + 13, k + 7), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 3, j + 13, k + 8), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 4, j + 5, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 5, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 5, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 5, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 5, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 5, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 5, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 5, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 5, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 6, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 6, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 6, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 6, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 6, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 6, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 6, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 6, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 6, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 6, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 6, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 7, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 7, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 7, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 7, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 7, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 7, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 7, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 7, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 7, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 7, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 7, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 9, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 10, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 11, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 13, k + 3), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 4, j + 13, k + 4), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 4, j + 14, k + 6), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 4, j + 14, k + 7), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 4, j + 14, k + 9), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 4, j + 14, k + 10), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 4, j + 14, k + 12), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 4, j + 14, k + 13), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 4, j + 14, k + 14), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 5, j + 5, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 5, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 5, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 5, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 5, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 5, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 5, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 5, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 5, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 5, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 5, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 6, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 6, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 6, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 6, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 6, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 6, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 6, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 6, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 6, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 6, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 6, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 7, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 7, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 7, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 7, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 7, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 7, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 7, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 7, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 7, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 7, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 7, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 9, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 10, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 11, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 2), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 5, j + 13, k + 3), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 5, j + 14, k + 11), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 5, j + 14, k + 12), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 5, j + 14, k + 13), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 5, j + 14, k + 14), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 5, j + 14, k + 15), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 6, j + 5, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 5, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 5, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 5, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 5, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 5, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 5, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 5, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 5, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 6, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 6, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 6, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 6, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 6, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 6, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 6, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 6, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 6, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 7, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 7, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 7, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 7, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 7, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 7, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 7, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 7, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 7, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 9, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 10, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 6, j + 11, k + 17), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 6, j + 14, k + 12), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 6, j + 14, k + 13), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 6, j + 14, k + 14), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 7, j + 1, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 1, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 1, k + 9), Blocks.LADDER.getStateFromMeta(5));
        world.setBlockState(new BlockPos(i + 7, j + 1, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 1, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 2, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 2, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 2, k + 9), Blocks.LADDER.getStateFromMeta(5));
        world.setBlockState(new BlockPos(i + 7, j + 2, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 2, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 3, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 3, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 3, k + 9), Blocks.LADDER.getStateFromMeta(5));
        world.setBlockState(new BlockPos(i + 7, j + 3, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 3, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 4, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 4, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 4, k + 9), Blocks.LADDER.getStateFromMeta(5));
        world.setBlockState(new BlockPos(i + 7, j + 4, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 4, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 5, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 5, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 5, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 5, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 5, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 5, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 5, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 5, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 5, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 5, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 5, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 6, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 6, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 6, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 6, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 6, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 6, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 6, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 6, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 6, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 6, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 6, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 7, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 7, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 7, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 7, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 7, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 7, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 7, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 7, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 7, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 7, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 7, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 8, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 9, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 10, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 11, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 7, j + 12, k + 2), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 7, j + 14, k + 12), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 7, j + 14, k + 14), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 8, j + 1, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 1, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 1, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 1, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 1, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 2, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 2, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 2, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 2, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 2, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 3, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 3, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 3, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 3, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 3, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 4, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 4, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 4, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 4, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 4, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 5, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 5, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 5, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 5, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 5, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 5, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 5, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 5, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 5, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 5, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 5, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 6, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 6, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 6, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 6, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 6, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 6, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 6, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 6, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 6, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 6, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 6, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 7, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 7, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 7, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 7, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 7, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 7, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 7, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 7, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 7, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 7, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 7, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 8, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 9, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 10, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 11, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 8, j + 14, k + 12), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 8, j + 14, k + 13), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 8, j + 14, k + 14), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 8, j + 15, k + 8), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 8, j + 15, k + 9), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 9, j + 1, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 1, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 1, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 2, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 2, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 2, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 2, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 2, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 3, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 3, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 3, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 3, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 3, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 4, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 4, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 4, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 4, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 4, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 5, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 5, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 5, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 5, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 5, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 5, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 5, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 5, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 5, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 5, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 5, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 6, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 6, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 6, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 6, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 6, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 6, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 6, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 6, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 6, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 6, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 6, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 7, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 7, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 7, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 7, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 7, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 7, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 7, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 7, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 7, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 7, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 7, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 8, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 9, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 10, k + 18), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 11, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 9, j + 13, k + 2), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 9, j + 13, k + 3), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 9, j + 14, k + 4), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 9, j + 14, k + 13), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 9, j + 15, k + 8), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 9, j + 15, k + 9), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 9, j + 15, k + 10), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 10, j + 1, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 1, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 1, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 1, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 2, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 2, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 2, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 2, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 2, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 3, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 3, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 3, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 3, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 3, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 4, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 4, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 4, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 4, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 4, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 5, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 5, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 5, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 5, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 5, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 5, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 5, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 5, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 5, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 5, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 5, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 6, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 6, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 6, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 6, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 6, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 6, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 6, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 6, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 6, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 6, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 6, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 7, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 7, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 7, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 7, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 7, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 7, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 7, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 7, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 7, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 7, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 7, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 8, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 9, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 10, k + 18), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 10, j + 11, k + 17), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 10, j + 12, k + 2), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 10, j + 13, k + 3), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 10, j + 14, k + 12), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 10, j + 15, k + 6), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 10, j + 15, k + 8), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 10, j + 15, k + 9), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 10, j + 15, k + 10), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 11, j + 5, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 5, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 5, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 5, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 5, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 5, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 5, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 5, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 5, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 6, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 6, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 6, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 6, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 6, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 6, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 6, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 6, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 6, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 7, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 7, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 7, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 7, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 7, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 7, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 7, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 7, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 7, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 9, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 10, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 11, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 11, j + 13, k + 3), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 11, j + 14, k + 12), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 11, j + 14, k + 13), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 11, j + 15, k + 8), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 11, j + 15, k + 9), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 11, j + 15, k + 10), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 12, j + 5, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 5, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 5, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 5, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 5, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 5, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 5, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 5, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 5, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 5, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 5, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 6, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 6, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 6, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 6, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 6, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 6, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 6, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 6, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 6, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 6, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 6, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 7, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 7, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 7, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 7, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 7, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 7, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 7, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 7, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 7, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 7, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 7, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 9, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 10, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 11, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 12, j + 14, k + 11), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 12, j + 14, k + 12), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 12, j + 14, k + 13), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 12, j + 14, k + 14), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 12, j + 15, k + 10), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 13, j + 5, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 5, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 5, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 5, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 5, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 5, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 5, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 5, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 5, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 5, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 5, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 6, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 6, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 6, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 6, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 6, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 6, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 6, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 6, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 6, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 6, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 6, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 7, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 7, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 7, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 7, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 7, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 7, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 7, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 7, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 7, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 7, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 7, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 9, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 0), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 10, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 1), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 11, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 13, j + 12, k + 17), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 13, j + 14, k + 11), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 13, j + 14, k + 12), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 13, j + 14, k + 13), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 14, j + 5, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 5, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 5, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 5, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 5, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 5, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 6, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 6, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 6, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 6, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 6, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 6, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 6, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 7, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 7, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 7, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 7, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 7, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 7, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 7, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 7, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 8, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 9, k + 14), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 4), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 10, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 1), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 5), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 6), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 7), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 8), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 9), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 10), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 11), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 12), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 11, k + 13), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 15), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 14, j + 12, k + 16), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 14, j + 14, k + 11), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 14, j + 14, k + 12), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 14, j + 14, k + 13), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 2), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 15), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 15, j + 12, k + 16), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 15, j + 13, k + 3), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 15, j + 13, k + 4), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 15, j + 13, k + 7), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 15, j + 13, k + 12), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 15, j + 13, k + 13), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 15, j + 13, k + 14), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 16, j + 12, k + 3), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 16, j + 12, k + 6), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 16, j + 12, k + 7), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 16, j + 12, k + 8), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 16, j + 12, k + 10), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 17, j + 11, k + 7), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 18, j + 10, k + 5), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 18, j + 10, k + 12), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 18, j + 10, k + 13), Blocks.TALLGRASS.getStateFromMeta(1));
        world.setBlockState(new BlockPos(i + 18, j + 10, k + 14), Blocks.TALLGRASS.getStateFromMeta(1));
        float rr = rand.nextFloat();
        int md = (rr < 0.1f) ? 2 : ((rr < 0.33f) ? 1 : 0);
        Block b = BlocksTC.lootCrateCommon;
        switch (md) {
            case 0: {
                b = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateCommon : BlocksTC.lootUrnCommon);
                break;
            }
            case 1: {
                b = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateUncommon : BlocksTC.lootUrnUncommon);
                break;
            }
            case 2: {
                b = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateRare : BlocksTC.lootUrnRare);
                break;
            }
        }
        world.setBlockState(new BlockPos(i + 9, j + 1, k + 7), b.getDefaultState());
        rr = rand.nextFloat();
        md = ((rr < 0.1f) ? 2 : ((rr < 0.33f) ? 1 : 0));
        Block b2 = BlocksTC.lootCrateCommon;
        switch (md) {
            case 0: {
                b2 = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateCommon : BlocksTC.lootUrnCommon);
                break;
            }
            case 1: {
                b2 = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateUncommon : BlocksTC.lootUrnUncommon);
                break;
            }
            case 2: {
                b2 = ((world.rand.nextFloat() < 0.3f) ? BlocksTC.lootCrateRare : BlocksTC.lootUrnRare);
                break;
            }
        }
        world.setBlockState(new BlockPos(i + 9, j + 1, k + 11), b2.getDefaultState());
        if (rand.nextInt(3) == 0) {
            world.setBlockState(new BlockPos(i + 10, j + 1, k + 9), Blocks.TRAPPED_CHEST.getStateFromMeta(4));
            world.setBlockState(new BlockPos(i + 10, j - 1, k + 9), Blocks.TNT.getDefaultState());
        }
        else {
            world.setBlockState(new BlockPos(i + 10, j + 1, k + 9), Blocks.CHEST.getStateFromMeta(4));
        }
        TileEntityChest chest = (TileEntityChest)world.getTileEntity(new BlockPos(i + 10, j + 1, k + 9));
        if (chest != null) {
            chest.setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
        }
        world.setBlockState(new BlockPos(i + 4, j + 5, k + 4), Blocks.MOB_SPAWNER.getDefaultState());
        world.setBlockState(new BlockPos(i + 4, j + 5, k + 14), Blocks.MOB_SPAWNER.getDefaultState());
        TileEntityMobSpawner var12 = (TileEntityMobSpawner)world.getTileEntity(new BlockPos(i + 4, j + 5, k + 4));
        if (var12 != null) {
            var12.getSpawnerBaseLogic().setEntityId(new ResourceLocation("minecraft:Skeleton"));
        }
        var12 = (TileEntityMobSpawner)world.getTileEntity(new BlockPos(i + 4, j + 5, k + 14));
        if (var12 != null) {
            var12.getSpawnerBaseLogic().setEntityId(new ResourceLocation("minecraft:Zombie"));
        }
        return true;
    }
~~~

### src/main/java/thaumcraft/common/world/objects/WorldGenSilverwoodTrees.java

~~~java
public WorldGenSilverwoodTrees(boolean doBlockNotify, int minTreeHeight, int randomTreeHeight) {
        super(doBlockNotify);
        worldgen = false;
        worldgen = !doBlockNotify;
        this.minTreeHeight = minTreeHeight;
        this.randomTreeHeight = randomTreeHeight;
    }
~~~

~~~java
public boolean generate(World world, Random random, BlockPos pos) {
        int height = random.nextInt(randomTreeHeight) + minTreeHeight;
        boolean flag = true;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (y < 1 || y + height + 1 > 256) {
            return false;
        }
        for (int i1 = y; i1 <= y + 1 + height; ++i1) {
            byte spread = 1;
            if (i1 == y) {
                spread = 0;
            }
            if (i1 >= y + 1 + height - 2) {
                spread = 3;
            }
            for (int j1 = x - spread; j1 <= x + spread && flag; ++j1) {
                for (int k1 = z - spread; k1 <= z + spread && flag; ++k1) {
                    if (i1 >= 0 && i1 < 256) {
                        IBlockState state = world.getBlockState(new BlockPos(j1, i1, k1));
                        Block block = state.getBlock();
                        if (!block.isAir(state, world, new BlockPos(j1, i1, k1)) && !block.isLeaves(state, world, new BlockPos(j1, i1, k1)) && !block.isReplaceable(world, new BlockPos(j1, i1, k1)) && i1 > y) {
                            flag = false;
                        }
                    }
                    else {
                        flag = false;
                    }
                }
            }
        }
        if (!flag) {
            return false;
        }
        IBlockState state2 = world.getBlockState(new BlockPos(x, y - 1, z));
        Block block2 = state2.getBlock();
        boolean isSoil = block2.canSustainPlant(state2, world, new BlockPos(x, y - 1, z), EnumFacing.UP, (IPlantable)Blocks.SAPLING);
        if (isSoil && y < 256 - height - 1) {
            block2.onPlantGrow(state2, world, new BlockPos(x, y - 1, z), new BlockPos(x, y, z));
            int start = y + height - 5;
            for (int end = y + height + 3 + random.nextInt(3), k2 = start; k2 <= end; ++k2) {
                int cty = MathHelper.clamp(k2, y + height - 3, y + height);
                for (int xx = x - 5; xx <= x + 5; ++xx) {
                    for (int zz = z - 5; zz <= z + 5; ++zz) {
                        double d3 = xx - x;
                        double d4 = k2 - cty;
                        double d5 = zz - z;
                        double dist = d3 * d3 + d4 * d4 + d5 * d5;
                        IBlockState s2 = world.getBlockState(new BlockPos(xx, k2, zz));
                        if (dist < 10 + random.nextInt(8) && s2.getBlock().canBeReplacedByLeaves(s2, world, new BlockPos(xx, k2, zz))) {
                            setBlockAndNotifyAdequately(world, new BlockPos(xx, k2, zz), BlocksTC.leafSilverwood.getStateFromMeta(1));
                        }
                    }
                }
            }
            int k2;
            for (k2 = 0; k2 < height; ++k2) {
                IBlockState s3 = world.getBlockState(new BlockPos(x, y + k2, z));
                Block block3 = s3.getBlock();
                if (block3.isAir(s3, world, new BlockPos(x, y + k2, z)) || block3.isLeaves(s3, world, new BlockPos(x, y + k2, z)) || block3.isReplaceable(world, new BlockPos(x, y + k2, z))) {
                    setBlockAndNotifyAdequately(world, new BlockPos(x, y + k2, z), BlocksTC.logSilverwood.getStateFromMeta(1));
                    setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + k2, z), BlocksTC.logSilverwood.getStateFromMeta(1));
                    setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + k2, z), BlocksTC.logSilverwood.getStateFromMeta(1));
                    setBlockAndNotifyAdequately(world, new BlockPos(x, y + k2, z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
                    setBlockAndNotifyAdequately(world, new BlockPos(x, y + k2, z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
                }
            }
            setBlockAndNotifyAdequately(world, new BlockPos(x, y + k2, z), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y, z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y, z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y, z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y, z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            if (random.nextInt(3) != 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + 1, z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            if (random.nextInt(3) != 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + 1, z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            if (random.nextInt(3) != 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + 1, z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            if (random.nextInt(3) != 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + 1, z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            setBlockAndNotifyAdequately(world, new BlockPos(x - 2, y, z), BlocksTC.logSilverwood.getStateFromMeta(0));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 2, y, z), BlocksTC.logSilverwood.getStateFromMeta(0));
            setBlockAndNotifyAdequately(world, new BlockPos(x, y, z - 2), BlocksTC.logSilverwood.getStateFromMeta(2));
            setBlockAndNotifyAdequately(world, new BlockPos(x, y, z + 2), BlocksTC.logSilverwood.getStateFromMeta(2));
            setBlockAndNotifyAdequately(world, new BlockPos(x - 2, y - 1, z), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 2, y - 1, z), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x, y - 1, z - 2), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x, y - 1, z + 2), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + (height - 4), z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + (height - 4), z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + (height - 4), z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + (height - 4), z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            if (random.nextInt(3) == 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + (height - 5), z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            if (random.nextInt(3) == 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + (height - 5), z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            if (random.nextInt(3) == 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x - 1, y + (height - 5), z + 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            if (random.nextInt(3) == 0) {
                setBlockAndNotifyAdequately(world, new BlockPos(x + 1, y + (height - 5), z - 1), BlocksTC.logSilverwood.getStateFromMeta(1));
            }
            setBlockAndNotifyAdequately(world, new BlockPos(x - 2, y + (height - 4), z), BlocksTC.logSilverwood.getStateFromMeta(0));
            setBlockAndNotifyAdequately(world, new BlockPos(x + 2, y + (height - 4), z), BlocksTC.logSilverwood.getStateFromMeta(0));
            setBlockAndNotifyAdequately(world, new BlockPos(x, y + (height - 4), z - 2), BlocksTC.logSilverwood.getStateFromMeta(2));
            setBlockAndNotifyAdequately(world, new BlockPos(x, y + (height - 4), z + 2), BlocksTC.logSilverwood.getStateFromMeta(2));
            if (worldgen) {
                WorldGenerator flowers = new WorldGenCustomFlowers(BlocksTC.shimmerleaf, 0);
                flowers.generate(world, random, new BlockPos(x, y, z));
            }
            return true;
        }
        return false;
    }
~~~

### src/main/java/thaumcraft/common/world/ThaumcraftWorldGenerator.java

~~~java
public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        worldGeneration(random, chunkX, chunkZ, world, true);
        AuraHandler.generateAura(chunkProvider.provideChunk(chunkX, chunkZ), random);
    }
~~~

~~~java
public void worldGeneration(Random random, int chunkX, int chunkZ, World world, boolean newGen) {
        if (world.provider.getDimension() == ModConfig.CONFIG_WORLD.dimensionOuterId) {
            world.getChunkFromChunkCoords(chunkX, chunkZ).markDirty();
        }
        else {
            generateAll(world, random, chunkX, chunkZ, newGen);
            if (world.provider.getDimension() == -1) {
                generateNether(world, random, chunkX, chunkZ, newGen);
            }
            else if (world.provider.getDimension() == ModConfig.CONFIG_WORLD.overworldDim) {
                generateSurface(world, random, chunkX, chunkZ, newGen);
            }
            if (!newGen) {
                world.getChunkFromChunkCoords(chunkX, chunkZ).markDirty();
            }
        }
    }
~~~

~~~java
private void generateSurface(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
        int blacklist = BiomeHandler.getDimBlacklist(world.provider.getDimension());
        if (blacklist == -1 && ModConfig.CONFIG_WORLD.generateStructure && world.provider.getDimension() == ModConfig.CONFIG_WORLD.overworldDim && !world.getWorldInfo().getTerrainType().getName().startsWith("flat") && (newGen || ModConfig.CONFIG_WORLD.regenStructure)) {
            int randPosX = chunkX * 16 + 8 + MathHelper.getInt(random, -4, 4);
            int randPosZ = chunkZ * 16 + 8 + MathHelper.getInt(random, -4, 4);
            BlockPos p = world.getPrecipitationHeight(new BlockPos(randPosX, 0, randPosZ)).down(9);
            if (p.getY() < world.getActualHeight()) {
                if (random.nextInt(100) == 0) {
                    WorldGenerator mound = new WorldGenMound();
                    mound.generate(world, random, p);
                }
                else if (random.nextInt(500) == 0) {
                    BlockPos p2 = p.up(8);
                    IBlockState bs = world.getBlockState(p2);
                    if (bs.getMaterial() == Material.GROUND || bs.getMaterial() == Material.ROCK || bs.getMaterial() == Material.SAND || bs.getMaterial() == Material.SNOW) {
                        EntityCultistPortalLesser eg = new EntityCultistPortalLesser(world);
                        eg.setPosition(p2.getX() + 0.5, p2.getY() + 1, p2.getZ() + 0.5);
                        eg.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(eg)), null);
                        world.spawnEntity(eg);
                    }
                }
            }
        }
    }
~~~

~~~java
private void generateNodes(World world, Random random, int chunkX, int chunkZ, boolean newGen, int blacklist) {
        if (blacklist != 0 && blacklist != 2 && ModConfig.CONFIG_WORLD.generateAura && (newGen || ModConfig.CONFIG_WORLD.regenAura)) {
            BlockPos var7 = null;
            try {
                var7 = new MapGenScatteredFeature().getNearestStructurePos(world, world.getHeight(new BlockPos(chunkX * 16 + 8, 64, chunkZ * 16 + 8)), true);
            }
            catch (Exception ex) {}
        }
    }
~~~

~~~java
private void generateVegetation(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
        Biome bgb = world.getBiome(new BlockPos(chunkX * 16 + 8, 50, chunkZ * 16 + 8));
        if (BiomeHandler.getBiomeBlacklist(Biome.getIdForBiome(bgb)) != -1) {
            return;
        }
        if (random.nextInt(80) == 3) {
            generateSilverwood(world, random, chunkX, chunkZ);
        }
        if (random.nextInt(25) == 7) {
            generateGreatwood(world, random, chunkX, chunkZ);
        }
        int randPosX = chunkX * 16 + 8;
        int randPosZ = chunkZ * 16 + 8;
        BlockPos bp = world.getHeight(new BlockPos(randPosX, 0, randPosZ));
        if (world.getBiome(bp).topBlock.getBlock() == Blocks.SAND && world.getBiome(bp).getTemperature(bp) > 1.0f && random.nextInt(30) == 0) {
            generateFlowers(world, random, bp, BlocksTC.cinderpearl, 0);
        }
    }
~~~

~~~java
private void generateOres(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
        Biome bgb = world.getBiome(new BlockPos(chunkX * 16 + 8, 50, chunkZ * 16 + 8));
        if (BiomeHandler.getBiomeBlacklist(Biome.getIdForBiome(bgb)) == 0 || BiomeHandler.getBiomeBlacklist(Biome.getIdForBiome(bgb)) == 2) {
            return;
        }
        float density = ModConfig.CONFIG_WORLD.oreDensity / 100.0f;
        if (world.provider.getDimension() == -1) {
            return;
        }
        if (ModConfig.CONFIG_WORLD.generateCinnabar && (newGen || ModConfig.CONFIG_WORLD.regenCinnabar)) {
            for (int i = 0; i < Math.round(18.0f * density); ++i) {
                int randPosX = chunkX * 16 + 8 + MathHelper.getInt(random, -6, 6);
                int randPosY = random.nextInt(world.getHeight() / 5);
                int randPosZ = chunkZ * 16 + 8 + MathHelper.getInt(random, -6, 6);
                BlockPos pos = new BlockPos(randPosX, randPosY, randPosZ);
                IBlockState block = world.getBlockState(pos);
                if (block.getBlock().isReplaceableOreGen(block, world, pos, predicate)) {
                    world.setBlockState(pos, BlocksTC.oreCinnabar.getDefaultState(), 2);
                }
            }
        }
        if (ModConfig.CONFIG_WORLD.generateQuartz && (newGen || ModConfig.CONFIG_WORLD.regenQuartz)) {
            for (int i = 0; i < Math.round(18.0f * density); ++i) {
                int randPosX = chunkX * 16 + 8 + MathHelper.getInt(random, -6, 6);
                int randPosY = random.nextInt(world.getHeight() / 4);
                int randPosZ = chunkZ * 16 + 8 + MathHelper.getInt(random, -6, 6);
                BlockPos pos = new BlockPos(randPosX, randPosY, randPosZ);
                IBlockState block = world.getBlockState(pos);
                if (block.getBlock().isReplaceableOreGen(block, world, pos, predicate)) {
                    world.setBlockState(pos, BlocksTC.oreQuartz.getDefaultState(), 2);
                }
            }
        }
        if (ModConfig.CONFIG_WORLD.generateAmber && (newGen || ModConfig.CONFIG_WORLD.regenAmber)) {
            for (int i = 0; i < Math.round(20.0f * density); ++i) {
                int randPosX = chunkX * 16 + 8 + MathHelper.getInt(random, -6, 6);
                int randPosZ2 = chunkZ * 16 + 8 + MathHelper.getInt(random, -6, 6);
                int randPosY2 = world.getHeight(new BlockPos(randPosX, 0, randPosZ2)).getY() - random.nextInt(25);
                BlockPos pos = new BlockPos(randPosX, randPosY2, randPosZ2);
                IBlockState block = world.getBlockState(pos);
                if (block.getBlock().isReplaceableOreGen(block, world, pos, predicate)) {
                    world.setBlockState(pos, BlocksTC.oreAmber.getDefaultState(), 2);
                }
            }
        }
        if (ModConfig.CONFIG_WORLD.generateCrystals && (newGen || ModConfig.CONFIG_WORLD.regenCrystals)) {
            int t = 8;
            int maxCrystals = Math.round(64.0f * density);
            int cc = 0;
            if (world.provider.getDimension() == -1) {
                t = 1;
            }
            for (int j = 0; j < Math.round(t * density); ++j) {
                int randPosX2 = chunkX * 16 + 8 + MathHelper.getInt(random, -6, 6);
                int randPosZ3 = chunkZ * 16 + 8 + MathHelper.getInt(random, -6, 6);
                int randPosY3 = random.nextInt(Math.max(5, world.getHeight(new BlockPos(randPosX2, 0, randPosZ3)).getY() - 5));
                BlockPos bp = new BlockPos(randPosX2, randPosY3, randPosZ3);
                int md = random.nextInt(6);
                if (random.nextInt(3) == 0) {
                    Aspect tag = BiomeHandler.getRandomBiomeTag(Biome.getIdForBiome(world.getBiome(bp)), random);
                    if (tag == null) {
                        md = random.nextInt(6);
                    }
                    else {
                        md = ShardType.getMetaByAspect(tag);
                    }
                }
                Block oreBlock = ShardType.byMetadata(md).getOre();
                for (int xx = -1; xx <= 1; ++xx) {
                    for (int yy = -1; yy <= 1; ++yy) {
                        for (int zz = -1; zz <= 1; ++zz) {
                            if (random.nextInt(3) != 0) {
                                IBlockState bs = world.getBlockState(bp.add(xx, yy, zz));
                                Material bm = bs.getMaterial();
                                if (!bm.isLiquid() && (world.isAirBlock(bp.add(xx, yy, zz)) || bs.getBlock().isReplaceable(world, bp.add(xx, yy, zz))) && BlockUtils.isBlockTouching(world, bp.add(xx, yy, zz), Material.ROCK, true)) {
                                    int amt = 1 + random.nextInt(3);
                                    world.setBlockState(bp.add(xx, yy, zz), oreBlock.getStateFromMeta(amt), 0);
                                    cc += amt;
                                }
                            }
                        }
                    }
                }
                if (cc > maxCrystals) {
                    break;
                }
            }
        }
    }
~~~

~~~java
private void generateAll(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
        boolean auraGen = false;
        int blacklist = BiomeHandler.getDimBlacklist(world.provider.getDimension());
        if (blacklist == -1 && ModConfig.CONFIG_WORLD.generateTrees && !world.getWorldInfo().getTerrainType().getName().startsWith("flat") && (newGen || ModConfig.CONFIG_WORLD.regenTrees)) {
            generateVegetation(world, random, chunkX, chunkZ, newGen);
        }
        if (blacklist != 0 && blacklist != 2) {
            generateOres(world, random, chunkX, chunkZ, newGen);
        }
    }
~~~

~~~java
private void generateNether(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
        boolean auraGen = false;
    }
~~~

~~~java
public static boolean generateFlowers(World world, Random random, BlockPos pos, Block block, int md) {
        WorldGenerator flowers = new WorldGenCustomFlowers(block, md);
        return flowers.generate(world, random, pos);
    }
~~~

~~~java
public static boolean generateGreatwood(World world, Random random, int chunkX, int chunkZ) {
        int x = chunkX * 16 + 8 + MathHelper.getInt(random, -4, 4);
        int z = chunkZ * 16 + 8 + MathHelper.getInt(random, -4, 4);
        BlockPos bp = world.getPrecipitationHeight(new BlockPos(x, 0, z));
        int bio = Biome.getIdForBiome(world.getBiome(bp));
        if (BiomeHandler.getBiomeSupportsGreatwood(bio) > random.nextFloat()) {
            boolean t = new WorldGenGreatwoodTrees(false, random.nextInt(8) == 0).generate(world, random, bp);
            return t;
        }
        return false;
    }
~~~

~~~java
public static boolean generateSilverwood(World world, Random random, int chunkX, int chunkZ) {
        int x = chunkX * 16 + 8 + MathHelper.getInt(random, -4, 4);
        int z = chunkZ * 16 + 8 + MathHelper.getInt(random, -4, 4);
        BlockPos bp = world.getPrecipitationHeight(new BlockPos(x, 0, z));
        Biome bio = world.getBiome(bp);
        int bi = Biome.getIdForBiome(world.getBiome(bp));
        if (BiomeHandler.getBiomeSupportsGreatwood(bi) / 2.0f > random.nextFloat() || (!bio.equals(BiomeHandler.MAGICAL_FOREST) && BiomeDictionary.hasType(bio, BiomeDictionary.Type.MAGICAL)) || bio == Biome.getBiome(18) || bio == Biome.getBiome(28)) {
            boolean t = new WorldGenSilverwoodTrees(false, 7, 4).generate(world, random, bp);
            return t;
        }
        return false;
    }
~~~

### src/main/java/thaumcraft/proxies/ProxyBlock.java

~~~java
public static void setupBlocksClient(IForgeRegistry<Block> iForgeRegistry) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlocksTC.slabAncient), 0, new ModelResourceLocation(new ResourceLocation("thaumcraft:slab_ancient"), "half=bottom,variant=default"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlocksTC.slabArcaneStone), 0, new ModelResourceLocation(new ResourceLocation("thaumcraft:slab_arcane_stone"), "half=bottom,variant=default"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlocksTC.slabArcaneBrick), 0, new ModelResourceLocation(new ResourceLocation("thaumcraft:slab_arcane_brick"), "half=bottom,variant=default"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlocksTC.slabEldritch), 0, new ModelResourceLocation(new ResourceLocation("thaumcraft:slab_eldritch"), "half=bottom,variant=default"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlocksTC.slabGreatwood), 0, new ModelResourceLocation(new ResourceLocation("thaumcraft:slab_greatwood"), "half=bottom,variant=default"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlocksTC.slabSilverwood), 0, new ModelResourceLocation(new ResourceLocation("thaumcraft:slab_silverwood"), "half=bottom,variant=default"));
        for (int a = 0; a < ShardType.values().length; ++a) {
            ProxyBlock.crystals[a] = new ModelResourceLocation(iForgeRegistry.getKey(ShardType.values()[a].getOre()), "normal");
            ModelResourceLocation mrl = ProxyBlock.crystals[a];
            ModelLoader.setCustomStateMapper(ShardType.values()[a].getOre(), new StateMapperBase() {
                protected ModelResourceLocation getModelResourceLocation(IBlockState p_178132_1_) {
                    return mrl;
                }
            });
        }
        for (Block b : BlocksTC.banners.values()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(new ResourceLocation("thaumcraft:banner"), "inventory"));
        }
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlocksTC.bannerCrimsonCult), 0, new ModelResourceLocation(new ResourceLocation("thaumcraft:banner_crimson_cult"), "inventory"));
        for (Block b : BlocksTC.nitor.values()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(new ResourceLocation("thaumcraft:nitor"), "inventory"));
        }
        ModelBakery.registerItemVariants(Item.getItemFromBlock(BlocksTC.mirror), new ResourceLocation("thaumcraft:mirror"), new ResourceLocation("thaumcraft:mirror_on"));
        ModelBakery.registerItemVariants(Item.getItemFromBlock(BlocksTC.mirrorEssentia), new ResourceLocation("thaumcraft:mirror_essentia"), new ResourceLocation("thaumcraft:mirror_essentia_on"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlocksTC.mirror), 1, new ModelResourceLocation(new ResourceLocation("thaumcraft:mirror_on"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlocksTC.mirrorEssentia), 1, new ModelResourceLocation(new ResourceLocation("thaumcraft:mirror_essentia_on"), "inventory"));
        Item fluxGooItem = Item.getItemFromBlock(BlocksTC.fluxGoo);
        ModelBakery.registerItemVariants(fluxGooItem);
        ModelLoader.setCustomMeshDefinition(fluxGooItem, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return ProxyBlock.fluidGooLocation;
            }
        });
        ModelLoader.setCustomStateMapper(BlocksTC.fluxGoo, new StateMapperBase() {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return ProxyBlock.fluidGooLocation;
            }
        });
        Item liquidDeathItem = Item.getItemFromBlock(BlocksTC.liquidDeath);
        ModelBakery.registerItemVariants(liquidDeathItem);
        ModelLoader.setCustomMeshDefinition(liquidDeathItem, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return ProxyBlock.fluidDeathLocation;
            }
        });
        ModelLoader.setCustomStateMapper(BlocksTC.liquidDeath, new StateMapperBase() {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return ProxyBlock.fluidDeathLocation;
            }
        });
        Item purifyingFluidItem = Item.getItemFromBlock(BlocksTC.purifyingFluid);
        ModelBakery.registerItemVariants(purifyingFluidItem);
        ModelLoader.setCustomMeshDefinition(purifyingFluidItem, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return ProxyBlock.fluidPureLocation;
            }
        });
        ModelLoader.setCustomStateMapper(BlocksTC.purifyingFluid, new StateMapperBase() {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return ProxyBlock.fluidPureLocation;
            }
        });
    }
~~~

## Extracted current methods with sapling or tree logic

### src/main/java/thaumcraft/client/TCColorHandlers.java

~~~java
public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        BlockColor leafColor = (state, level, pos, tintIndex) -> {
            if (state.is(TCBlocks.LEAVES_SILVERWOOD.get())) {
                return WHITE;
            }

            if (level != null && pos != null) {
                return BiomeColors.getAverageFoliageColor(level, pos);
            }

            return FoliageColor.getDefaultColor();
        };

        event.register(leafColor,
                TCBlocks.LEAVES_GREATWOOD.get(),
                TCBlocks.LEAVES_SILVERWOOD.get());

        event.register((state, level, pos, tintIndex) -> crystalColor(state.getBlock()),
                TCBlocks.CRYSTAL_AER.get(),
                TCBlocks.CRYSTAL_IGNIS.get(),
                TCBlocks.CRYSTAL_AQUA.get(),
                TCBlocks.CRYSTAL_TERRA.get(),
                TCBlocks.CRYSTAL_ORDO.get(),
                TCBlocks.CRYSTAL_PERDITIO.get(),
                TCBlocks.CRYSTAL_VITIUM.get());
    }
~~~

~~~java
public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        ItemColor greatwoodLeavesItem = (stack, tintIndex) -> FoliageColor.getDefaultColor();
        ItemColor silverwoodLeavesItem = (stack, tintIndex) -> WHITE;

        event.register(greatwoodLeavesItem, TCBlocks.LEAVES_GREATWOOD.get());
        event.register(silverwoodLeavesItem, TCBlocks.LEAVES_SILVERWOOD.get());

        event.register((stack, tintIndex) -> AIR, TCBlocks.CRYSTAL_AER.get());
        event.register((stack, tintIndex) -> FIRE, TCBlocks.CRYSTAL_IGNIS.get());
        event.register((stack, tintIndex) -> WATER, TCBlocks.CRYSTAL_AQUA.get());
        event.register((stack, tintIndex) -> EARTH, TCBlocks.CRYSTAL_TERRA.get());
        event.register((stack, tintIndex) -> ORDER, TCBlocks.CRYSTAL_ORDO.get());
        event.register((stack, tintIndex) -> ENTROPY, TCBlocks.CRYSTAL_PERDITIO.get());
        event.register((stack, tintIndex) -> FLUX, TCBlocks.CRYSTAL_VITIUM.get());
    }
~~~

### src/main/java/thaumcraft/common/blocks/world/plants/TCPlantBlock.java

~~~java
public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (kind) {
            case SAPLING -> SAPLING_SHAPE;
            case SHIMMERLEAF -> SHIMMERLEAF_SHAPE;
            case CINDERPEARL -> CINDERPEARL_SHAPE;
            case VISHROOM -> VISHROOM_SHAPE;
        };
    }
~~~

~~~java
protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return switch (kind) {
            case SHIMMERLEAF -> isLegacyGrassOrDirt(state);

            case CINDERPEARL -> state.is(Blocks.SAND)
                    || state.is(Blocks.RED_SAND)
                    || isLegacyDirt(state)
                    || state.is(Blocks.TERRACOTTA)
                    || state.is(BlockTags.TERRACOTTA);

            case VISHROOM -> isLegacyGrassOrDirt(state)
                    || state.is(Blocks.PODZOL)
                    || state.is(Blocks.MOSS_BLOCK)
                    || state.is(Blocks.MYCELIUM)
                    || state.is(Blocks.STONE)
                    || state.is(Blocks.DEEPSLATE)
                    || state.is(Blocks.TUFF)
                    || state.is(BlockTags.BASE_STONE_OVERWORLD);

            case SAPLING -> isLegacyGrassOrDirt(state)
                    || state.is(Blocks.PODZOL)
                    || state.is(Blocks.MOSS_BLOCK);
        };
    }
~~~

### src/main/java/thaumcraft/common/registry/TCBlocks.java

~~~java
private static Block logBlock(boolean silverwood) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
                .strength(2.0F, 5.0F);
        if (silverwood) {
            properties = properties.lightLevel(state -> 5);
        }
        return new RotatedPillarBlock(properties);
    }
~~~

### src/main/java/thaumcraft/common/registry/TCCreativeTabOrder.java

~~~java
public static void addThaumcraftItems(CreativeModeTab.Output output) {
        output.accept(TCItems.ORE_AMBER.get());
        output.accept(TCItems.ORE_CINNABAR.get());
        output.accept(TCItems.ORE_QUARTZ.get());

        output.accept(TCItems.CRYSTAL_AER.get());
        output.accept(TCItems.CRYSTAL_IGNIS.get());
        output.accept(TCItems.CRYSTAL_AQUA.get());
        output.accept(TCItems.CRYSTAL_TERRA.get());
        output.accept(TCItems.CRYSTAL_ORDO.get());
        output.accept(TCItems.CRYSTAL_PERDITIO.get());
        output.accept(TCItems.CRYSTAL_VITIUM.get());

        output.accept(TCItems.STONE_ARCANE.get());
        output.accept(TCItems.STONE_ARCANE_BRICK.get());
        output.accept(TCItems.STONE_ANCIENT.get());
        output.accept(TCItems.STONE_ANCIENT_TILE.get());
        output.accept(TCItems.STONE_ANCIENT_ROCK.get());
        output.accept(TCItems.STONE_ANCIENT_GLYPHED.get());
        output.accept(TCItems.STONE_ANCIENT_DOORWAY.get());
        output.accept(TCItems.STONE_ELDRITCH_TILE.get());
        output.accept(TCItems.STONE_POROUS.get());

        output.accept(TCItems.STAIRS_ARCANE.get());
        output.accept(TCItems.STAIRS_ARCANE_BRICK.get());
        output.accept(TCItems.STAIRS_ANCIENT.get());

        output.accept(TCItems.SLAB_ARCANE_STONE.get());
        output.accept(TCItems.SLAB_ARCANE_BRICK.get());
        output.accept(TCItems.SLAB_ANCIENT.get());
        output.accept(TCItems.SLAB_ELDRITCH.get());

        output.accept(TCItems.LOG_GREATWOOD.get());
        output.accept(TCItems.LOG_SILVERWOOD.get());
        output.accept(TCItems.LEAVES_GREATWOOD.get());
        output.accept(TCItems.LEAVES_SILVERWOOD.get());
        output.accept(TCItems.SAPLING_GREATWOOD.get());
        output.accept(TCItems.SAPLING_SILVERWOOD.get());
        output.accept(TCItems.SHIMMERLEAF.get());
        output.accept(TCItems.CINDERPEARL.get());
        output.accept(TCItems.VISHROOM.get());
        output.accept(TCItems.PLANK_GREATWOOD.get());
        output.accept(TCItems.PLANK_SILVERWOOD.get());
        output.accept(TCItems.STAIRS_GREATWOOD.get());
        output.accept(TCItems.STAIRS_SILVERWOOD.get());
        output.accept(TCItems.SLAB_GREATWOOD.get());
        output.accept(TCItems.SLAB_SILVERWOOD.get());

        output.accept(TCItems.AMBER_BLOCK.get());
        output.accept(TCItems.AMBER_BRICK.get());
    }
~~~

## Full current files most likely involved

### Current likely file

Path: src/main/java/thaumcraft/common/blocks/world/plants/TCPlantBlock.java

~~~java
package thaumcraft.common.blocks.world.plants;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.lib.fx.TCFXDispatcher;

public class TCPlantBlock extends BushBlock {
    public static final MapCodec<TCPlantBlock> CODEC = simpleCodec(properties ->
            new TCPlantBlock(Kind.SAPLING, properties)
    );

    private static final VoxelShape SAPLING_SHAPE = Block.box(1.6D, 0.0D, 1.6D, 14.4D, 12.8D, 14.4D);
    private static final VoxelShape SHIMMERLEAF_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
    private static final VoxelShape CINDERPEARL_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);
    private static final VoxelShape VISHROOM_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 10.0D, 13.0D);

    public enum Kind {
        SAPLING,
        SHIMMERLEAF,
        CINDERPEARL,
        VISHROOM
    }

    private final Kind kind;

    public TCPlantBlock(Kind kind, BlockBehaviour.Properties properties) {
        super(properties);
        this.kind = kind;
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (kind) {
            case SAPLING -> SAPLING_SHAPE;
            case SHIMMERLEAF -> SHIMMERLEAF_SHAPE;
            case CINDERPEARL -> CINDERPEARL_SHAPE;
            case VISHROOM -> VISHROOM_SHAPE;
        };
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return switch (kind) {
            case SHIMMERLEAF -> isLegacyGrassOrDirt(state);

            case CINDERPEARL -> state.is(Blocks.SAND)
                    || state.is(Blocks.RED_SAND)
                    || isLegacyDirt(state)
                    || state.is(Blocks.TERRACOTTA)
                    || state.is(BlockTags.TERRACOTTA);

            case VISHROOM -> isLegacyGrassOrDirt(state)
                    || state.is(Blocks.PODZOL)
                    || state.is(Blocks.MOSS_BLOCK)
                    || state.is(Blocks.MYCELIUM)
                    || state.is(Blocks.STONE)
                    || state.is(Blocks.DEEPSLATE)
                    || state.is(Blocks.TUFF)
                    || state.is(BlockTags.BASE_STONE_OVERWORLD);

            case SAPLING -> isLegacyGrassOrDirt(state)
                    || state.is(Blocks.PODZOL)
                    || state.is(Blocks.MOSS_BLOCK);
        };
    }

    private static boolean isLegacyGrassOrDirt(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || isLegacyDirt(state);
    }

    private static boolean isLegacyDirt(BlockState state) {
        return state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.ROOTED_DIRT);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (kind == Kind.CINDERPEARL) {
            spawnCinderpearlParticles(level, pos, random);
            return;
        }

        if (kind == Kind.SHIMMERLEAF) {
            TCFXDispatcher.drawShimmerleafMote(level, pos, random);
            return;
        }

        if (kind == Kind.VISHROOM) {
            TCFXDispatcher.drawVishroomMote(level, pos, random);
        }
    }

    private static void spawnCinderpearlParticles(Level level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide() || !random.nextBoolean()) {
            return;
        }

        double x = pos.getX() + 0.5D + (random.nextFloat() - random.nextFloat()) * 0.1D;
        double y = pos.getY() + 0.6D + (random.nextFloat() - random.nextFloat()) * 0.1D;
        double z = pos.getZ() + 0.5D + (random.nextFloat() - random.nextFloat()) * 0.1D;

        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        if (kind == Kind.VISHROOM
                && !level.isClientSide()
                && entity instanceof LivingEntity living
                && level.random.nextInt(5) == 0) {
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        }
    }
}
~~~

### Current likely file

Path: src/main/java/thaumcraft/common/registry/TCBlocks.java

~~~java
package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import thaumcraft.common.blocks.world.plants.TCPlantBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class TCBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Thaumcraft.MODID);

    public static final Supplier<Block> ORE_AMBER = BLOCKS.register("ore_amber", () -> stoneBlock(1.5F, 3.0F));
    public static final Supplier<Block> ORE_CINNABAR = BLOCKS.register("ore_cinnabar", () -> stoneBlock(2.0F, 3.0F));
    public static final Supplier<Block> ORE_QUARTZ = BLOCKS.register("ore_quartz", () -> stoneBlock(3.0F, 3.0F));

    public static final Supplier<Block> CRYSTAL_AER = BLOCKS.register("crystal_aer", () -> crystalPlaceholder());
    public static final Supplier<Block> CRYSTAL_IGNIS = BLOCKS.register("crystal_ignis", () -> crystalPlaceholder());
    public static final Supplier<Block> CRYSTAL_AQUA = BLOCKS.register("crystal_aqua", () -> crystalPlaceholder());
    public static final Supplier<Block> CRYSTAL_TERRA = BLOCKS.register("crystal_terra", () -> crystalPlaceholder());
    public static final Supplier<Block> CRYSTAL_ORDO = BLOCKS.register("crystal_ordo", () -> crystalPlaceholder());
    public static final Supplier<Block> CRYSTAL_PERDITIO = BLOCKS.register("crystal_perditio", () -> crystalPlaceholder());
    public static final Supplier<Block> CRYSTAL_VITIUM = BLOCKS.register("crystal_vitium", () -> crystalPlaceholder());

    public static final Supplier<Block> STONE_ARCANE = BLOCKS.register("stone_arcane", () -> stoneBlock(2.0F, 10.0F));
    public static final Supplier<Block> STONE_ARCANE_BRICK = BLOCKS.register("stone_arcane_brick", () -> stoneBlock(2.0F, 10.0F));
    public static final Supplier<Block> STONE_ANCIENT = BLOCKS.register("stone_ancient", () -> stoneBlock(2.0F, 10.0F));
    public static final Supplier<Block> STONE_ANCIENT_TILE = BLOCKS.register("stone_ancient_tile", () -> stoneBlock(2.0F, 10.0F));
    public static final Supplier<Block> STONE_ANCIENT_ROCK = BLOCKS.register("stone_ancient_rock", () -> stoneBlock(50.0F, 1200.0F));
    public static final Supplier<Block> STONE_ANCIENT_GLYPHED = BLOCKS.register("stone_ancient_glyphed", () -> stoneBlock(2.0F, 10.0F));
    public static final Supplier<Block> STONE_ANCIENT_DOORWAY = BLOCKS.register("stone_ancient_doorway", () -> stoneBlock(50.0F, 1200.0F));
    public static final Supplier<Block> STONE_ELDRITCH_TILE = BLOCKS.register("stone_eldritch_tile", () -> stoneBlock(15.0F, 1000.0F));
    public static final Supplier<Block> STONE_POROUS = BLOCKS.register("stone_porous", () -> stoneBlock(1.5F, 6.0F));

    public static final Supplier<Block> STAIRS_ARCANE = BLOCKS.register("stairs_arcane", () -> stoneStairBlock(STONE_ARCANE.get().defaultBlockState(), 2.0F, 10.0F));
    public static final Supplier<Block> STAIRS_ARCANE_BRICK = BLOCKS.register("stairs_arcane_brick", () -> stoneStairBlock(STONE_ARCANE_BRICK.get().defaultBlockState(), 2.0F, 10.0F));
    public static final Supplier<Block> STAIRS_ANCIENT = BLOCKS.register("stairs_ancient", () -> stoneStairBlock(STONE_ANCIENT.get().defaultBlockState(), 2.0F, 10.0F));

    public static final Supplier<Block> SLAB_ARCANE_STONE = BLOCKS.register("slab_arcane_stone", () -> stoneSlabBlock(2.0F, 10.0F));
    public static final Supplier<Block> SLAB_ARCANE_BRICK = BLOCKS.register("slab_arcane_brick", () -> stoneSlabBlock(2.0F, 10.0F));
    public static final Supplier<Block> SLAB_ANCIENT = BLOCKS.register("slab_ancient", () -> stoneSlabBlock(2.0F, 10.0F));
    public static final Supplier<Block> SLAB_ELDRITCH = BLOCKS.register("slab_eldritch", () -> stoneSlabBlock(2.0F, 10.0F));

    public static final Supplier<Block> AMBER_BLOCK = BLOCKS.register("amber_block", () -> amberBlock());
    public static final Supplier<Block> AMBER_BRICK = BLOCKS.register("amber_brick", () -> amberBlock());

    public static final Supplier<Block> LOG_GREATWOOD = BLOCKS.register("log_greatwood", () -> logBlock(false));
    public static final Supplier<Block> LOG_SILVERWOOD = BLOCKS.register("log_silverwood", () -> logBlock(true));

    public static final Supplier<Block> LEAVES_GREATWOOD = BLOCKS.register("leaves_greatwood", () -> leavesBlock());
    public static final Supplier<Block> LEAVES_SILVERWOOD = BLOCKS.register("leaves_silverwood", () -> leavesBlock());

    public static final Supplier<Block> SAPLING_GREATWOOD = BLOCKS.register("sapling_greatwood", () -> plantBlock(TCPlantBlock.Kind.SAPLING, 0));
    public static final Supplier<Block> SAPLING_SILVERWOOD = BLOCKS.register("sapling_silverwood", () -> plantBlock(TCPlantBlock.Kind.SAPLING, 0));

    public static final Supplier<Block> SHIMMERLEAF = BLOCKS.register("shimmerleaf", () -> plantBlock(TCPlantBlock.Kind.SHIMMERLEAF, 6));
    public static final Supplier<Block> CINDERPEARL = BLOCKS.register("cinderpearl", () -> plantBlock(TCPlantBlock.Kind.CINDERPEARL, 8));
    public static final Supplier<Block> VISHROOM = BLOCKS.register("vishroom", () -> plantBlock(TCPlantBlock.Kind.VISHROOM, 6));

    public static final Supplier<Block> PLANK_GREATWOOD = BLOCKS.register("plank_greatwood", () -> woodBlock());
    public static final Supplier<Block> PLANK_SILVERWOOD = BLOCKS.register("plank_silverwood", () -> woodBlock());

    public static final Supplier<Block> STAIRS_GREATWOOD = BLOCKS.register("stairs_greatwood", () -> woodStairBlock(PLANK_GREATWOOD.get().defaultBlockState()));
    public static final Supplier<Block> STAIRS_SILVERWOOD = BLOCKS.register("stairs_silverwood", () -> woodStairBlock(PLANK_SILVERWOOD.get().defaultBlockState()));

    public static final Supplier<Block> SLAB_GREATWOOD = BLOCKS.register("slab_greatwood", () -> woodSlabBlock());
    public static final Supplier<Block> SLAB_SILVERWOOD = BLOCKS.register("slab_silverwood", () -> woodSlabBlock());

    private static Block stoneBlock(float strength, float resistance) {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(strength, resistance)
                .requiresCorrectToolForDrops());
    }

    private static Block woodBlock() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                .strength(2.0F, 5.0F));
    }

    private static Block amberBlock() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(0.5F, 3.0F)
                .sound(SoundType.STONE)
                .noOcclusion());
    }

    private static Block logBlock(boolean silverwood) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
                .strength(2.0F, 5.0F);
        if (silverwood) {
            properties = properties.lightLevel(state -> 5);
        }
        return new RotatedPillarBlock(properties);
    }

    private static Block leavesBlock() {
        return new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                .strength(0.2F)
                .noOcclusion());
    }

    private static Block plantBlock(TCPlantBlock.Kind kind, int lightLevel) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)
                .sound(SoundType.GRASS)
                .noCollission()
                .instabreak()
                .offsetType(BlockBehaviour.OffsetType.NONE);

        if (kind == TCPlantBlock.Kind.SHIMMERLEAF || kind == TCPlantBlock.Kind.CINDERPEARL) {
            properties = properties.offsetType(BlockBehaviour.OffsetType.XZ);
        }

        if (lightLevel > 0) {
            properties = properties.lightLevel(state -> lightLevel);
        }

        return new TCPlantBlock(kind, properties);
    }

    private static Block stoneStairBlock(net.minecraft.world.level.block.state.BlockState baseState, float strength, float resistance) {
        return new StairBlock(baseState, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS)
                .strength(strength, resistance)
                .requiresCorrectToolForDrops());
    }

    private static Block woodStairBlock(net.minecraft.world.level.block.state.BlockState baseState) {
        return new StairBlock(baseState, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)
                .strength(2.0F, 5.0F));
    }

    private static Block stoneSlabBlock(float strength, float resistance) {
        return new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB)
                .strength(strength, resistance)
                .requiresCorrectToolForDrops());
    }

    private static Block woodSlabBlock() {
        return new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB)
                .strength(2.0F, 5.0F));
    }

    private static Block crystalPlaceholder() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(0.25F, 0.25F)
                .noOcclusion()
                .noCollission()
                .lightLevel(state -> 1));
    }

    private TCBlocks() {
    }
}
~~~

### Current likely file

Path: src/main/java/thaumcraft/common/registry/TCItems.java

~~~java
package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class TCItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Thaumcraft.MODID);

    public static final Supplier<BlockItem> ORE_AMBER = blockItem("ore_amber", TCBlocks.ORE_AMBER);
    public static final Supplier<BlockItem> ORE_CINNABAR = blockItem("ore_cinnabar", TCBlocks.ORE_CINNABAR);
    public static final Supplier<BlockItem> ORE_QUARTZ = blockItem("ore_quartz", TCBlocks.ORE_QUARTZ);

    public static final Supplier<BlockItem> CRYSTAL_AER = blockItem("crystal_aer", TCBlocks.CRYSTAL_AER);
    public static final Supplier<BlockItem> CRYSTAL_IGNIS = blockItem("crystal_ignis", TCBlocks.CRYSTAL_IGNIS);
    public static final Supplier<BlockItem> CRYSTAL_AQUA = blockItem("crystal_aqua", TCBlocks.CRYSTAL_AQUA);
    public static final Supplier<BlockItem> CRYSTAL_TERRA = blockItem("crystal_terra", TCBlocks.CRYSTAL_TERRA);
    public static final Supplier<BlockItem> CRYSTAL_ORDO = blockItem("crystal_ordo", TCBlocks.CRYSTAL_ORDO);
    public static final Supplier<BlockItem> CRYSTAL_PERDITIO = blockItem("crystal_perditio", TCBlocks.CRYSTAL_PERDITIO);
    public static final Supplier<BlockItem> CRYSTAL_VITIUM = blockItem("crystal_vitium", TCBlocks.CRYSTAL_VITIUM);

    public static final Supplier<BlockItem> STONE_ARCANE = blockItem("stone_arcane", TCBlocks.STONE_ARCANE);
    public static final Supplier<BlockItem> STONE_ARCANE_BRICK = blockItem("stone_arcane_brick", TCBlocks.STONE_ARCANE_BRICK);
    public static final Supplier<BlockItem> STONE_ANCIENT = blockItem("stone_ancient", TCBlocks.STONE_ANCIENT);
    public static final Supplier<BlockItem> STONE_ANCIENT_TILE = blockItem("stone_ancient_tile", TCBlocks.STONE_ANCIENT_TILE);
    public static final Supplier<BlockItem> STONE_ANCIENT_ROCK = blockItem("stone_ancient_rock", TCBlocks.STONE_ANCIENT_ROCK);
    public static final Supplier<BlockItem> STONE_ANCIENT_GLYPHED = blockItem("stone_ancient_glyphed", TCBlocks.STONE_ANCIENT_GLYPHED);
    public static final Supplier<BlockItem> STONE_ANCIENT_DOORWAY = blockItem("stone_ancient_doorway", TCBlocks.STONE_ANCIENT_DOORWAY);
    public static final Supplier<BlockItem> STONE_ELDRITCH_TILE = blockItem("stone_eldritch_tile", TCBlocks.STONE_ELDRITCH_TILE);
    public static final Supplier<BlockItem> STONE_POROUS = blockItem("stone_porous", TCBlocks.STONE_POROUS);

    public static final Supplier<BlockItem> STAIRS_ARCANE = blockItem("stairs_arcane", TCBlocks.STAIRS_ARCANE);
    public static final Supplier<BlockItem> STAIRS_ARCANE_BRICK = blockItem("stairs_arcane_brick", TCBlocks.STAIRS_ARCANE_BRICK);
    public static final Supplier<BlockItem> STAIRS_ANCIENT = blockItem("stairs_ancient", TCBlocks.STAIRS_ANCIENT);

    public static final Supplier<BlockItem> SLAB_ARCANE_STONE = blockItem("slab_arcane_stone", TCBlocks.SLAB_ARCANE_STONE);
    public static final Supplier<BlockItem> SLAB_ARCANE_BRICK = blockItem("slab_arcane_brick", TCBlocks.SLAB_ARCANE_BRICK);
    public static final Supplier<BlockItem> SLAB_ANCIENT = blockItem("slab_ancient", TCBlocks.SLAB_ANCIENT);
    public static final Supplier<BlockItem> SLAB_ELDRITCH = blockItem("slab_eldritch", TCBlocks.SLAB_ELDRITCH);

    public static final Supplier<BlockItem> AMBER_BLOCK = blockItem("amber_block", TCBlocks.AMBER_BLOCK);
    public static final Supplier<BlockItem> AMBER_BRICK = blockItem("amber_brick", TCBlocks.AMBER_BRICK);
    public static final Supplier<BlockItem> LOG_GREATWOOD = blockItem("log_greatwood", TCBlocks.LOG_GREATWOOD);
    public static final Supplier<BlockItem> LOG_SILVERWOOD = blockItem("log_silverwood", TCBlocks.LOG_SILVERWOOD);
    public static final Supplier<BlockItem> LEAVES_GREATWOOD = blockItem("leaves_greatwood", TCBlocks.LEAVES_GREATWOOD);
    public static final Supplier<BlockItem> LEAVES_SILVERWOOD = blockItem("leaves_silverwood", TCBlocks.LEAVES_SILVERWOOD);
    public static final Supplier<BlockItem> SAPLING_GREATWOOD = blockItem("sapling_greatwood", TCBlocks.SAPLING_GREATWOOD);
    public static final Supplier<BlockItem> SAPLING_SILVERWOOD = blockItem("sapling_silverwood", TCBlocks.SAPLING_SILVERWOOD);
    public static final Supplier<BlockItem> SHIMMERLEAF = blockItem("shimmerleaf", TCBlocks.SHIMMERLEAF);
    public static final Supplier<BlockItem> CINDERPEARL = blockItem("cinderpearl", TCBlocks.CINDERPEARL);
    public static final Supplier<BlockItem> VISHROOM = blockItem("vishroom", TCBlocks.VISHROOM);
    public static final Supplier<BlockItem> PLANK_GREATWOOD = blockItem("plank_greatwood", TCBlocks.PLANK_GREATWOOD);
    public static final Supplier<BlockItem> PLANK_SILVERWOOD = blockItem("plank_silverwood", TCBlocks.PLANK_SILVERWOOD);
    public static final Supplier<BlockItem> STAIRS_GREATWOOD = blockItem("stairs_greatwood", TCBlocks.STAIRS_GREATWOOD);
    public static final Supplier<BlockItem> STAIRS_SILVERWOOD = blockItem("stairs_silverwood", TCBlocks.STAIRS_SILVERWOOD);
    public static final Supplier<BlockItem> SLAB_GREATWOOD = blockItem("slab_greatwood", TCBlocks.SLAB_GREATWOOD);
    public static final Supplier<BlockItem> SLAB_SILVERWOOD = blockItem("slab_silverwood", TCBlocks.SLAB_SILVERWOOD);

    public static final Supplier<Item> GOGGLES = ITEMS.register("goggles", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> AMBER = ITEMS.register("amber", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> QUICKSILVER = ITEMS.register("quicksilver", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> FABRIC = ITEMS.register("fabric", () -> new Item(new Item.Properties()));

    private static Supplier<BlockItem> blockItem(String id, Supplier<? extends net.minecraft.world.level.block.Block> block) {
        return ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private TCItems() {
    }
}
~~~

## Port design questions before implementation

1. What was the exact legacy sapling class name and its inheritance?
2. Was Greatwood generation always 2x2 or did it support single saplings in any condition?
3. What was the exact bonemeal success chance?
4. Did the first growth attempt only advance stage from 0 to 1?
5. What block states must be temporarily cleared before tree generation?
6. How does the generator restore saplings if generation fails?
7. What exact block names/states does Greatwood generator place?
8. What exact block names/states does Silverwood generator place?
9. Does Silverwood generation create Shimmerleaf or any aura-related side effects in normal sapling growth, or only worldgen?
10. Does the current port already register all blocks required by the generators?
11. Are loot tables for logs, leaves and saplings already correct?
12. Should the NeoForge implementation use TreeGrower, or should it use a direct custom generator for closer legacy behavior?

## Recommended implementation split

1. Research and documentation only.
2. TCSaplingBlock with stage, random tick and bonemeal, but generator stub or minimal fail-safe.
3. Silverwood generator port.
4. Greatwood generator port.
5. Resource/data cleanup: loot tables, blockstates and model state consistency.
6. Final visual and survival validation.
