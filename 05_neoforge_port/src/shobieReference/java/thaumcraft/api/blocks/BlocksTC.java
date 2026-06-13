package thaumcraft.api.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import thaumcraft.init.ModBlocks;

/**
 * References to Thaumcraft blocks.
 * For 1.20.1, these point to the RegistryObjects in ModBlocks.
 * 
 * Note: Accessing .get() on RegistryObjects is only safe after the block registry event has fired.
 */
public class BlocksTC {

    // Stone Blocks
    public static final RegistryObject<Block> stoneArcane = ModBlocks.ARCANE_STONE;
    public static final RegistryObject<Block> stoneArcaneBrick = ModBlocks.ARCANE_STONE_BRICK;
    public static final RegistryObject<Block> stoneAncient = ModBlocks.ANCIENT_STONE;
    public static final RegistryObject<Block> stoneAncientTile = ModBlocks.ANCIENT_STONE_TILE;
    public static final RegistryObject<Block> stoneAncientRock = ModBlocks.ANCIENT_STONE_ROCK;
    public static final RegistryObject<Block> stoneAncientGlyphed = ModBlocks.ANCIENT_STONE_GLYPHED;
    public static final RegistryObject<Block> stoneAncientDoorway = ModBlocks.ANCIENT_STONE_DOORWAY;
    public static final RegistryObject<Block> stoneEldritchTile = ModBlocks.ELDRITCH_STONE_TILE;
    public static final RegistryObject<Block> stonePorous = ModBlocks.POROUS_STONE;

    // Stairs
    public static final RegistryObject<Block> stairsArcane = ModBlocks.ARCANE_STONE_STAIRS;
    public static final RegistryObject<Block> stairsArcaneBrick = ModBlocks.ARCANE_STONE_BRICK_STAIRS;
    public static final RegistryObject<Block> stairsAncient = ModBlocks.ANCIENT_STONE_STAIRS;
    
    // Slabs
    public static final RegistryObject<Block> slabArcaneStone = ModBlocks.ARCANE_STONE_SLAB;
    public static final RegistryObject<Block> slabArcaneBrick = ModBlocks.ARCANE_STONE_BRICK_SLAB;
    public static final RegistryObject<Block> slabAncient = ModBlocks.ANCIENT_STONE_SLAB;

    // Pillars
    public static final RegistryObject<Block> pillarArcane = ModBlocks.ARCANE_PILLAR;
    public static final RegistryObject<Block> pillarAncient = ModBlocks.ANCIENT_PILLAR;
    public static final RegistryObject<Block> pillarEldritch = ModBlocks.ELDRITCH_PILLAR;

    // Wood Blocks
    public static final RegistryObject<Block> logGreatwood = ModBlocks.GREATWOOD_LOG;
    public static final RegistryObject<Block> logSilverwood = ModBlocks.SILVERWOOD_LOG;
    public static final RegistryObject<Block> plankGreatwood = ModBlocks.GREATWOOD_PLANKS;
    public static final RegistryObject<Block> plankSilverwood = ModBlocks.SILVERWOOD_PLANKS;
    public static final RegistryObject<Block> stairsGreatwood = ModBlocks.GREATWOOD_STAIRS;
    public static final RegistryObject<Block> stairsSilverwood = ModBlocks.SILVERWOOD_STAIRS;
    public static final RegistryObject<Block> slabGreatwood = ModBlocks.GREATWOOD_SLAB;
    public static final RegistryObject<Block> slabSilverwood = ModBlocks.SILVERWOOD_SLAB;
    public static final RegistryObject<Block> leafGreatwood = ModBlocks.GREATWOOD_LEAVES;
    public static final RegistryObject<Block> leafSilverwood = ModBlocks.SILVERWOOD_LEAVES;
    public static final RegistryObject<Block> saplingGreatwood = ModBlocks.GREATWOOD_SAPLING;
    public static final RegistryObject<Block> saplingSilverwood = ModBlocks.SILVERWOOD_SAPLING;

    // Metal Blocks
    public static final RegistryObject<Block> metalBlockBrass = ModBlocks.BRASS_BLOCK;
    public static final RegistryObject<Block> metalBlockThaumium = ModBlocks.THAUMIUM_BLOCK;
    public static final RegistryObject<Block> metalBlockVoid = ModBlocks.VOID_METAL_BLOCK;
    public static final RegistryObject<Block> metalAlchemical = ModBlocks.ALCHEMICAL_BRASS_BLOCK;
    public static final RegistryObject<Block> metalAlchemicalAdvanced = ModBlocks.ALCHEMICAL_BRASS_ADVANCED_BLOCK;

    // Amber
    public static final RegistryObject<Block> amberBlock = ModBlocks.AMBER_BLOCK;
    public static final RegistryObject<Block> amberBrick = ModBlocks.AMBER_BRICK;

    // Matrix
    public static final RegistryObject<Block> matrixSpeed = ModBlocks.MATRIX_SPEED;
    public static final RegistryObject<Block> matrixCost = ModBlocks.MATRIX_COST;

    // Crafting
    public static final RegistryObject<Block> arcaneWorkbench = ModBlocks.ARCANE_WORKBENCH;
    public static final RegistryObject<Block> crucible = ModBlocks.CRUCIBLE;
    public static final RegistryObject<Block> researchTable = ModBlocks.RESEARCH_TABLE;
    public static final RegistryObject<Block> infusionMatrix = ModBlocks.INFUSION_MATRIX;
    public static final RegistryObject<Block> focalManipulator = ModBlocks.FOCAL_MANIPULATOR;
    public static final RegistryObject<Block> thaumatorium = ModBlocks.THAUMATORIUM;
    public static final RegistryObject<Block> patternCrafter = ModBlocks.PATTERN_CRAFTER;
    public static final RegistryObject<Block> golemBuilder = ModBlocks.GOLEM_BUILDER;

    // Ores
    public static final RegistryObject<Block> oreAmber = ModBlocks.AMBER_ORE;
    public static final RegistryObject<Block> oreCinnabar = ModBlocks.CINNABAR_ORE;
    public static final RegistryObject<Block> oreQuartz = ModBlocks.QUARTZ_ORE;

    // Deepslate Ores
    public static final RegistryObject<Block> deepslateOreAmber = ModBlocks.DEEPSLATE_AMBER_ORE;
    public static final RegistryObject<Block> deepslateOreCinnabar = ModBlocks.DEEPSLATE_CINNABAR_ORE;
    public static final RegistryObject<Block> deepslateOreQuartz = ModBlocks.DEEPSLATE_QUARTZ_ORE;

    // Crystals
    public static final RegistryObject<Block> crystalAir = ModBlocks.CRYSTAL_AIR;
    public static final RegistryObject<Block> crystalFire = ModBlocks.CRYSTAL_FIRE;
    public static final RegistryObject<Block> crystalWater = ModBlocks.CRYSTAL_WATER;
    public static final RegistryObject<Block> crystalEarth = ModBlocks.CRYSTAL_EARTH;
    public static final RegistryObject<Block> crystalOrder = ModBlocks.CRYSTAL_ORDER;
    public static final RegistryObject<Block> crystalEntropy = ModBlocks.CRYSTAL_ENTROPY;
    public static final RegistryObject<Block> crystalTaint = ModBlocks.CRYSTAL_FLUX;

    // Plants
    public static final RegistryObject<Block> shimmerleaf = ModBlocks.SHIMMERLEAF;
    public static final RegistryObject<Block> cinderpearl = ModBlocks.CINDERPEARL;
    public static final RegistryObject<Block> vishroom = ModBlocks.VISHROOM;

    // Devices
    public static final RegistryObject<Block> pedestalArcane = ModBlocks.PEDESTAL_ARCANE;
    public static final RegistryObject<Block> pedestalAncient = ModBlocks.PEDESTAL_ANCIENT;
    public static final RegistryObject<Block> pedestalEldritch = ModBlocks.PEDESTAL_ELDRITCH;
    public static final RegistryObject<Block> tableWood = ModBlocks.TABLE_WOOD;
    public static final RegistryObject<Block> tableStone = ModBlocks.TABLE_STONE;
    public static final RegistryObject<Block> rechargePedestal = ModBlocks.RECHARGE_PEDESTAL;
    public static final RegistryObject<Block> bellows = ModBlocks.BELLOWS;
    public static final RegistryObject<Block> hungryChest = ModBlocks.HUNGRY_CHEST;
    public static final RegistryObject<Block> mirror = ModBlocks.MIRROR_ITEM;
    public static final RegistryObject<Block> mirrorEssentia = ModBlocks.MIRROR_ESSENTIA;
    public static final RegistryObject<Block> stabilizer = ModBlocks.STABILIZER;
    public static final RegistryObject<Block> visGenerator = ModBlocks.VIS_GENERATOR;
    public static final RegistryObject<Block> condenser = ModBlocks.CONDENSER;
    public static final RegistryObject<Block> arcaneEar = ModBlocks.ARCANE_EAR;
    public static final RegistryObject<Block> arcaneEarToggle = ModBlocks.ARCANE_EAR_TOGGLE;
    public static final RegistryObject<Block> redstoneRelay = ModBlocks.REDSTONE_RELAY;
    public static final RegistryObject<Block> levitator = ModBlocks.LEVITATOR;
    public static final RegistryObject<Block> dioptra = ModBlocks.DIOPTRA;
    public static final RegistryObject<Block> voidSiphon = ModBlocks.VOID_SIPHON;
    public static final RegistryObject<Block> potionSprayer = ModBlocks.POTION_SPRAYER;
    public static final RegistryObject<Block> everfullUrn = ModBlocks.EVERFULL_URN;

    // Essentia
    public static final RegistryObject<Block> jarNormal = ModBlocks.JAR_NORMAL;
    public static final RegistryObject<Block> jarVoid = ModBlocks.JAR_VOID;
    public static final RegistryObject<Block> jarBrain = ModBlocks.JAR_BRAIN;
    public static final RegistryObject<Block> tube = ModBlocks.TUBE_NORMAL;
    public static final RegistryObject<Block> tubeValve = ModBlocks.TUBE_VALVE;
    public static final RegistryObject<Block> tubeRestrict = ModBlocks.TUBE_RESTRICTED;
    public static final RegistryObject<Block> tubeOneway = ModBlocks.TUBE_ONEWAY;
    public static final RegistryObject<Block> tubeFilter = ModBlocks.TUBE_FILTER;
    public static final RegistryObject<Block> tubeBuffer = ModBlocks.TUBE_BUFFER;
    public static final RegistryObject<Block> alembic = ModBlocks.ALEMBIC;
    public static final RegistryObject<Block> smelterBasic = ModBlocks.SMELTER;
    public static final RegistryObject<Block> centrifuge = ModBlocks.CENTRIFUGE;
    public static final RegistryObject<Block> infernalFurnace = ModBlocks.INFERNAL_FURNACE;
    public static final RegistryObject<Block> essentiaReservoir = ModBlocks.ESSENTIA_RESERVOIR;
    public static final RegistryObject<Block> spa = ModBlocks.SPA;
    public static final RegistryObject<Block> fluxScrubber = ModBlocks.FLUX_SCRUBBER;
    public static final RegistryObject<Block> visRelay = ModBlocks.VIS_RELAY;

    // Lamps
    public static final RegistryObject<Block> lampArcane = ModBlocks.LAMP_ARCANE;
    public static final RegistryObject<Block> lampGrowth = ModBlocks.LAMP_GROWTH;
    public static final RegistryObject<Block> lampFertility = ModBlocks.LAMP_FERTILITY;

    // Candles & Nitor
    public static final RegistryObject<Block> candleWhite = ModBlocks.CANDLE_WHITE;
    public static final RegistryObject<Block> nitorWhite = ModBlocks.NITOR_WHITE;
    // ... mapping for all colors would go here, omitting for brevity in this initial pass ...

    // Infusion Support
    public static final RegistryObject<Block> inlay = ModBlocks.INLAY;

    // Effects & Misc
    public static final RegistryObject<Block> effectSap = ModBlocks.EFFECT_SAP;
    public static final RegistryObject<Block> effectShock = ModBlocks.EFFECT_SHOCK;
    public static final RegistryObject<Block> effectGlimmer = ModBlocks.EFFECT_GLIMMER;
    public static final RegistryObject<Block> hole = ModBlocks.HOLE;
    public static final RegistryObject<Block> fluxGoo = ModBlocks.FLUX_GOO;
    public static final RegistryObject<Block> taintFibre = ModBlocks.TAINT_FIBRE;

}
