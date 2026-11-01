package thaumcraft.common.registry;



import thaumcraft.common.items.tools.ItemElementalAxe;

import thaumcraft.common.items.tools.ItemElementalPickaxe;

import thaumcraft.common.items.tools.ItemElementalSword;

import thaumcraft.common.items.tools.ItemElementalShovel;

import thaumcraft.common.items.tools.ItemElementalHoe;

import thaumcraft.common.items.tools.ItemPrimalCrusher;

import thaumcraft.common.items.tools.ItemCrimsonBlade;

import thaumcraft.common.items.armor.ItemFortressArmor;

import thaumcraft.common.items.armor.ItemCultistPlateArmor;

import thaumcraft.common.items.armor.ItemCultistRobeArmor;

import thaumcraft.common.items.armor.ItemCultistLeaderArmor;

import thaumcraft.common.items.armor.ItemCultistBoots;

import thaumcraft.common.items.curios.TCCurioItem;

import thaumcraft.common.items.curios.TCLootBagItem;

import thaumcraft.common.items.curios.ItemAmuletVis;
import thaumcraft.common.items.curios.ItemCharmUndying;
import thaumcraft.common.items.curios.ItemCloudRing;
import thaumcraft.common.items.curios.ItemCuriosityBand;
import thaumcraft.common.items.curios.ItemVerdantHeart;
import thaumcraft.common.items.curios.ItemVoidseerPearl;
import thaumcraft.common.items.armor.ItemVisDiscountGear;
import thaumcraft.common.golems.ItemGolemBell;
import thaumcraft.common.golems.ItemGolemPlacer;
import thaumcraft.common.golems.seals.ItemSealPlacer;


import java.util.function.Supplier;

import net.minecraft.world.item.AxeItem;

import net.minecraft.world.item.ArmorItem;

import net.minecraft.world.item.BlockItem;

import net.minecraft.world.item.BucketItem;

import net.minecraft.world.item.HoeItem;

import net.minecraft.world.item.Item;

import net.minecraft.world.item.Items;

import net.minecraft.world.item.PickaxeItem;

import net.minecraft.world.item.Rarity;

import net.minecraft.world.item.ShovelItem;

import net.minecraft.world.item.SwordItem;

import net.minecraft.world.level.block.Block;

import net.neoforged.neoforge.registries.DeferredRegister;

import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import thaumcraft.Thaumcraft;

import thaumcraft.common.items.ItemAspectVariant;

import thaumcraft.common.items.ItemLegacyPlaceholder;

import thaumcraft.common.items.TCJarLabelItem;

import thaumcraft.common.items.TCMirrorBlockItem;

import thaumcraft.common.items.TCPhialItem;

import thaumcraft.common.items.TCBrainJarBlockItem;

import thaumcraft.common.items.TCWardedJarBlockItem;

import thaumcraft.common.items.armor.ItemGoggles;

import thaumcraft.common.items.armor.ItemRobeArmor;

import thaumcraft.common.items.armor.ItemThaumiumArmor;

import thaumcraft.common.items.armor.ItemTravellerBoots;

import thaumcraft.common.items.armor.ItemVoidArmor;

import thaumcraft.common.items.armor.ItemVoidRobeArmor;

import thaumcraft.common.items.components.TCLegacyItemComponent;

import thaumcraft.common.items.components.TCStoredEnchantComponent;

import thaumcraft.common.items.consumables.ItemBathSalts;

import thaumcraft.common.items.consumables.ItemAlumentum;

import thaumcraft.common.items.consumables.ItemBottleTaint;

import thaumcraft.common.items.consumables.ItemCausalityCollapser;

import thaumcraft.common.items.consumables.ItemSanitySoap;

import thaumcraft.common.items.consumables.ItemZombieBrain;

import thaumcraft.common.items.casters.ItemCaster;

import thaumcraft.common.items.casters.ItemFocus;

import thaumcraft.common.items.casters.ItemFocusPouch;

import thaumcraft.common.items.curios.ItemCurioRites;

import thaumcraft.common.items.curios.ItemPechWand;

import thaumcraft.common.items.resources.ItemSalisMundus;

import thaumcraft.common.items.curios.ItemThaumonomicon;

import thaumcraft.common.items.tools.ItemSanityChecker;

import thaumcraft.common.items.tools.ItemScribingTools;

import thaumcraft.common.items.tools.ItemArcaneBore;

import thaumcraft.common.items.tools.ItemHandMirror;

import thaumcraft.common.items.tools.ItemThaumometer;

import thaumcraft.common.items.tools.TCToolTiers;



public final class TCItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Thaumcraft.MODID);



    public static final Supplier<BlockItem> ORE_AMBER = blockItem("ore_amber", TCBlocks.ORE_AMBER);

    public static final Supplier<BlockItem> ORE_CINNABAR = blockItem("ore_cinnabar", TCBlocks.ORE_CINNABAR);

    public static final Supplier<BlockItem> ORE_QUARTZ = blockItem("ore_quartz", TCBlocks.ORE_QUARTZ);

    public static final Supplier<BlockItem> DEEPSLATE_AMBER_ORE = blockItem("deepslate_amber_ore", TCBlocks.DEEPSLATE_AMBER_ORE);

    public static final Supplier<BlockItem> DEEPSLATE_CINNABAR_ORE = blockItem("deepslate_cinnabar_ore", TCBlocks.DEEPSLATE_CINNABAR_ORE);

    public static final Supplier<BlockItem> DEEPSLATE_QUARTZ_ORE = blockItem("deepslate_quartz_ore", TCBlocks.DEEPSLATE_QUARTZ_ORE);



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

    public static final Supplier<BlockItem> METAL_BRASS = blockItem("metal_brass", TCBlocks.METAL_BRASS);

    public static final Supplier<BlockItem> METAL_THAUMIUM = blockItem("metal_thaumium", TCBlocks.METAL_THAUMIUM);

    public static final Supplier<BlockItem> METAL_VOID = blockItem("metal_void", TCBlocks.METAL_VOID);

    public static final Supplier<BlockItem> METAL_ALCHEMICAL = blockItem("metal_alchemical", TCBlocks.METAL_ALCHEMICAL);

    public static final Supplier<BlockItem> METAL_ALCHEMICAL_ADVANCED = blockItem("metal_alchemical_advanced", TCBlocks.METAL_ALCHEMICAL_ADVANCED);

    public static final Supplier<BlockItem> NITOR_BLACK = blockItem("nitor_black", TCBlocks.NITOR_BLACK);

    public static final Supplier<BlockItem> NITOR_BLUE = blockItem("nitor_blue", TCBlocks.NITOR_BLUE);

    public static final Supplier<BlockItem> NITOR_BROWN = blockItem("nitor_brown", TCBlocks.NITOR_BROWN);

    public static final Supplier<BlockItem> NITOR_CYAN = blockItem("nitor_cyan", TCBlocks.NITOR_CYAN);

    public static final Supplier<BlockItem> NITOR_GRAY = blockItem("nitor_gray", TCBlocks.NITOR_GRAY);

    public static final Supplier<BlockItem> NITOR_GREEN = blockItem("nitor_green", TCBlocks.NITOR_GREEN);

    public static final Supplier<BlockItem> NITOR_YELLOW = blockItem("nitor_yellow", TCBlocks.NITOR_YELLOW);

    public static final Supplier<BlockItem> NITOR_LIGHTBLUE = blockItem("nitor_lightblue", TCBlocks.NITOR_LIGHTBLUE);

    public static final Supplier<BlockItem> NITOR_LIME = blockItem("nitor_lime", TCBlocks.NITOR_LIME);

    public static final Supplier<BlockItem> NITOR_MAGENTA = blockItem("nitor_magenta", TCBlocks.NITOR_MAGENTA);

    public static final Supplier<BlockItem> NITOR_ORANGE = blockItem("nitor_orange", TCBlocks.NITOR_ORANGE);

    public static final Supplier<BlockItem> NITOR_PINK = blockItem("nitor_pink", TCBlocks.NITOR_PINK);

    public static final Supplier<BlockItem> NITOR_PURPLE = blockItem("nitor_purple", TCBlocks.NITOR_PURPLE);

    public static final Supplier<BlockItem> NITOR_RED = blockItem("nitor_red", TCBlocks.NITOR_RED);

    public static final Supplier<BlockItem> NITOR_SILVER = blockItem("nitor_silver", TCBlocks.NITOR_SILVER);

    public static final Supplier<BlockItem> NITOR_WHITE = blockItem("nitor_white", TCBlocks.NITOR_WHITE);

    public static final Supplier<BlockItem> CANDLE_BLACK = blockItem("candle_black", TCBlocks.CANDLE_BLACK);

    public static final Supplier<BlockItem> CANDLE_BLUE = blockItem("candle_blue", TCBlocks.CANDLE_BLUE);

    public static final Supplier<BlockItem> CANDLE_BROWN = blockItem("candle_brown", TCBlocks.CANDLE_BROWN);

    public static final Supplier<BlockItem> CANDLE_CYAN = blockItem("candle_cyan", TCBlocks.CANDLE_CYAN);

    public static final Supplier<BlockItem> CANDLE_GRAY = blockItem("candle_gray", TCBlocks.CANDLE_GRAY);

    public static final Supplier<BlockItem> CANDLE_GREEN = blockItem("candle_green", TCBlocks.CANDLE_GREEN);

    public static final Supplier<BlockItem> CANDLE_LIGHTBLUE = blockItem("candle_lightblue", TCBlocks.CANDLE_LIGHTBLUE);

    public static final Supplier<BlockItem> CANDLE_LIME = blockItem("candle_lime", TCBlocks.CANDLE_LIME);

    public static final Supplier<BlockItem> CANDLE_MAGENTA = blockItem("candle_magenta", TCBlocks.CANDLE_MAGENTA);

    public static final Supplier<BlockItem> CANDLE_ORANGE = blockItem("candle_orange", TCBlocks.CANDLE_ORANGE);

    public static final Supplier<BlockItem> CANDLE_PINK = blockItem("candle_pink", TCBlocks.CANDLE_PINK);

    public static final Supplier<BlockItem> CANDLE_PURPLE = blockItem("candle_purple", TCBlocks.CANDLE_PURPLE);

    public static final Supplier<BlockItem> CANDLE_RED = blockItem("candle_red", TCBlocks.CANDLE_RED);

    public static final Supplier<BlockItem> CANDLE_SILVER = blockItem("candle_silver", TCBlocks.CANDLE_SILVER);

    public static final Supplier<BlockItem> CANDLE_WHITE = blockItem("candle_white", TCBlocks.CANDLE_WHITE);

    public static final Supplier<BlockItem> CANDLE_YELLOW = blockItem("candle_yellow", TCBlocks.CANDLE_YELLOW);

    public static final Supplier<BlockItem> TABLE_WOOD = blockItem("table_wood", TCBlocks.TABLE_WOOD);

    public static final Supplier<BlockItem> TABLE_STONE = blockItem("table_stone", TCBlocks.TABLE_STONE);

    public static final Supplier<BlockItem> ARCANE_WORKBENCH = blockItem("arcane_workbench", TCBlocks.ARCANE_WORKBENCH);

    public static final Supplier<BlockItem> ARCANE_WORKBENCH_CHARGER = blockItem("arcane_workbench_charger", TCBlocks.ARCANE_WORKBENCH_CHARGER);

    public static final Supplier<BlockItem> RESEARCH_TABLE = blockItem("research_table", TCBlocks.RESEARCH_TABLE);

    public static final Supplier<BlockItem> CRUCIBLE = blockItem("crucible", TCBlocks.CRUCIBLE);

    public static final Supplier<BlockItem> SMELTER_BASIC = blockItem("smelter_basic", TCBlocks.SMELTER_BASIC);

    public static final Supplier<BlockItem> BELLOWS = blockItem("bellows", TCBlocks.BELLOWS);

    public static final Supplier<BlockItem> SMELTER_VENT = blockItem("smelter_vent", TCBlocks.SMELTER_VENT);

    public static final Supplier<BlockItem> SMELTER_AUX = blockItem("smelter_aux", TCBlocks.SMELTER_AUX);



    public static final Supplier<BlockItem> JAR_NORMAL = ITEMS.register("jar_normal", () -> new TCWardedJarBlockItem(TCBlocks.JAR_NORMAL.get(), new Item.Properties()));

    public static final Supplier<BlockItem> JAR_VOID = ITEMS.register("jar_void", () -> new TCWardedJarBlockItem(TCBlocks.JAR_VOID.get(), new Item.Properties()));

    public static final Supplier<BlockItem> CENTRIFUGE = blockItem("centrifuge", TCBlocks.CENTRIFUGE);

    public static final Supplier<BlockItem> ALEMBIC = blockItem("alembic", TCBlocks.ALEMBIC);

    public static final Supplier<BlockItem> ESSENTIA_TRANSPORT_IN = blockItem("essentiatransportin", TCBlocks.ESSENTIA_TRANSPORT_IN);

    public static final Supplier<BlockItem> ESSENTIA_TRANSPORT_OUT = blockItem("essentiatransportout", TCBlocks.ESSENTIA_TRANSPORT_OUT);

    public static final Supplier<BlockItem> SMELTER_THAUMIUM = blockItem("smelter_thaumium", TCBlocks.SMELTER_THAUMIUM);

    public static final Supplier<BlockItem> SMELTER_VOID = blockItem("smelter_void", TCBlocks.SMELTER_VOID);

    public static final Supplier<BlockItem> WAND_WORKBENCH = blockItem("wand_workbench", TCBlocks.WAND_WORKBENCH);

    public static final Supplier<BlockItem> INFUSION_MATRIX = blockItem("infusion_matrix", TCBlocks.INFUSION_MATRIX);

    public static final Supplier<BlockItem> PILLAR_ARCANE = blockItem("pillar_arcane", TCBlocks.PILLAR_ARCANE);

    public static final Supplier<BlockItem> PILLAR_ANCIENT = blockItem("pillar_ancient", TCBlocks.PILLAR_ANCIENT);

    public static final Supplier<BlockItem> PILLAR_ELDRITCH = blockItem("pillar_eldritch", TCBlocks.PILLAR_ELDRITCH);

    public static final Supplier<BlockItem> MATRIX_SPEED = blockItem("matrix_speed", TCBlocks.MATRIX_SPEED);

    public static final Supplier<BlockItem> MATRIX_COST = blockItem("matrix_cost", TCBlocks.MATRIX_COST);

    public static final Supplier<BlockItem> GOLEM_BUILDER = blockItem("golem_builder", TCBlocks.GOLEM_BUILDER);

    public static final Supplier<BlockItem> INLAY = blockItem("inlay", TCBlocks.INLAY);

    public static final Supplier<BlockItem> STABILIZER = blockItem("stabilizer", TCBlocks.STABILIZER);

    public static final Supplier<BlockItem> FLUX_GOO = blockItem("flux_goo", TCBlocks.FLUX_GOO);

    public static final Supplier<BlockItem> TAINT_FIBRE = blockItem("taint_fibre", TCBlocks.TAINT_FIBRE);

    public static final Supplier<BlockItem> TAINT_CRUST = blockItem("taint_crust", TCBlocks.TAINT_CRUST);

    public static final Supplier<BlockItem> TAINT_SOIL = blockItem("taint_soil", TCBlocks.TAINT_SOIL);

    public static final Supplier<BlockItem> TAINT_ROCK = blockItem("taint_rock", TCBlocks.TAINT_ROCK);

    public static final Supplier<BlockItem> TAINT_GEYSER = blockItem("taint_geyser", TCBlocks.TAINT_GEYSER);

    public static final Supplier<BlockItem> TAINT_LOG = blockItem("taint_log", TCBlocks.TAINT_LOG);

    public static final Supplier<BlockItem> TAINT_FEATURE = blockItem("taint_feature", TCBlocks.TAINT_FEATURE);

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



    public static final Supplier<Item> THAUMONOMICON = ITEMS.register("thaumonomicon", ItemThaumonomicon::new);

    public static final Supplier<Item> THAUMOMETER = ITEMS.register("thaumometer", ItemThaumometer::new);

    public static final Supplier<Item> GOGGLES = ITEMS.register("goggles", ItemGoggles::new);



    public static final Supplier<Item> AMBER = ITEMS.register("amber", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> QUICKSILVER = ITEMS.register("quicksilver", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> QUICKSILVER_NUGGET = simpleItem("quicksilver_nugget");

    public static final Supplier<Item> FABRIC = ITEMS.register("fabric", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> THAUMIUM_INGOT = simpleItem("thaumium_ingot");

    public static final Supplier<Item> BRASS_INGOT = simpleItem("brass_ingot");

    public static final Supplier<Item> BRASS_NUGGET = simpleItem("brass_nugget");

    public static final Supplier<Item> COPPER_NUGGET = simpleItem("copper_nugget");

    public static final Supplier<Item> TIN_NUGGET = simpleItem("tin_nugget");

    public static final Supplier<Item> SILVER_NUGGET = simpleItem("silver_nugget");

    public static final Supplier<Item> LEAD_NUGGET = simpleItem("lead_nugget");

    public static final Supplier<Item> THAUMIUM_NUGGET = simpleItem("thaumium_nugget");

    public static final Supplier<Item> VOID_NUGGET = simpleItem("void_nugget");

    public static final Supplier<Item> QUARTZ_NUGGET = simpleItem("quartz_nugget");

    public static final Supplier<Item> BRASS_PLATE = simpleItem("brass_plate");

    public static final Supplier<Item> IRON_PLATE = simpleItem("iron_plate");

    public static final Supplier<Item> THAUMIUM_PLATE = simpleItem("thaumium_plate");

    public static final Supplier<Item> VOID_PLATE = simpleItem("void_plate");

    public static final Supplier<Item> FILTER = simpleItem("filter");

    public static final Supplier<Item> MORPHIC_RESONATOR = simpleItem("morphic_resonator");

    public static final Supplier<Item> RARE_EARTH = simpleItem("rare_earth");

    public static final Supplier<Item> PRIMORDIAL_PEARL = simpleItem("primordial_pearl");

    public static final Supplier<Item> SALIS_MUNDUS = simpleItem("salis_mundus");

    public static final Supplier<Item> TALLOW = simpleItem("tallow");

    public static final Supplier<Item> CLUSTER_IRON = simpleItem("cluster_iron");

    public static final Supplier<Item> CLUSTER_GOLD = simpleItem("cluster_gold");

    public static final Supplier<Item> CLUSTER_COPPER = simpleItem("cluster_copper");

    public static final Supplier<Item> CLUSTER_TIN = simpleItem("cluster_tin");

    public static final Supplier<Item> CLUSTER_SILVER = simpleItem("cluster_silver");

    public static final Supplier<Item> CLUSTER_LEAD = simpleItem("cluster_lead");

    public static final Supplier<Item> CLUSTER_CINNABAR = simpleItem("cluster_cinnabar");

    public static final Supplier<Item> MECHANISM_SIMPLE = simpleItem("mechanism_simple");

    public static final Supplier<Item> MECHANISM_COMPLEX = simpleItem("mechanism_complex");

    public static final Supplier<Item> VIS_RESONATOR = simpleItem("vis_resonator");

    public static final Supplier<Item> MIRRORED_GLASS = simpleItem("mirrored_glass");

    public static final Supplier<Item> BRAIN = simpleItem("brain");

    public static final Supplier<Item> ALUMENTUM = simpleItem("alumentum");

    public static final Supplier<Item> CHUNK_CHICKEN = simpleItem("chunk_chicken");

    public static final Supplier<Item> CHUNK_BEEF = simpleItem("chunk_beef");

    public static final Supplier<Item> CHUNK_PORK = simpleItem("chunk_pork");

    public static final Supplier<Item> CHUNK_FISH = simpleItem("chunk_fish");

    public static final Supplier<Item> CHUNK_RABBIT = simpleItem("chunk_rabbit");

    public static final Supplier<Item> CHUNK_MUTTON = simpleItem("chunk_mutton");

    public static final Supplier<Item> BATH_SALTS = ITEMS.register("bath_salts", ItemBathSalts::new);

    public static final Supplier<Item> BOTTLE_TAINT = ITEMS.register("bottle_taint", ItemBottleTaint::new);

    public static final Supplier<Item> LIQUID_DEATH_BUCKET = ITEMS.register("liquid_death_bucket",

            () -> new BucketItem(TCFluids.LIQUID_DEATH.get(), new Item.Properties()

                    .craftRemainder(Items.BUCKET)

                    .stacksTo(1)

                    .rarity(Rarity.UNCOMMON)));

    public static final Supplier<Item> PURIFYING_FLUID_BUCKET = ITEMS.register("purifying_fluid_bucket",

            () -> new BucketItem(TCFluids.PURIFYING_FLUID.get(), new Item.Properties()

                    .craftRemainder(Items.BUCKET)

                    .stacksTo(1)

                    .rarity(Rarity.UNCOMMON)));

    public static final Supplier<Item> SANE_SOAP = ITEMS.register("sane_soap", ItemSanitySoap::new);

    public static final Supplier<Item> FLESH_BLOCK = simpleItem("flesh_block");

    public static final Supplier<Item> CURIO_RITES = ITEMS.register("curio_rites", ItemCurioRites::new);

    // Crimson Cult Equipment

    public static final Supplier<Item> CRIMSON_BLADE = ITEMS.register("crimson_blade", ItemCrimsonBlade::new);

    public static final Supplier<Item> CRIMSON_PLATE_HELM = ITEMS.register("crimson_plate_helm", () -> new ItemCultistPlateArmor(ArmorItem.Type.HELMET));

    public static final Supplier<Item> CRIMSON_PLATE_CHEST = ITEMS.register("crimson_plate_chest", () -> new ItemCultistPlateArmor(ArmorItem.Type.CHESTPLATE));

    public static final Supplier<Item> CRIMSON_PLATE_LEGS = ITEMS.register("crimson_plate_legs", () -> new ItemCultistPlateArmor(ArmorItem.Type.LEGGINGS));

    public static final Supplier<Item> CRIMSON_BOOTS = ITEMS.register("crimson_boots", ItemCultistBoots::new);

    public static final Supplier<Item> CRIMSON_ROBE_HELM = ITEMS.register("crimson_robe_helm", () -> new ItemCultistRobeArmor(ArmorItem.Type.HELMET));

    public static final Supplier<Item> CRIMSON_ROBE_CHEST = ITEMS.register("crimson_robe_chest", () -> new ItemCultistRobeArmor(ArmorItem.Type.CHESTPLATE));

    public static final Supplier<Item> CRIMSON_ROBE_LEGS = ITEMS.register("crimson_robe_legs", () -> new ItemCultistRobeArmor(ArmorItem.Type.LEGGINGS));

    public static final Supplier<Item> CRIMSON_PRAETOR_HELM = ITEMS.register("crimson_praetor_helm", () -> new ItemCultistLeaderArmor(ArmorItem.Type.HELMET));

    public static final Supplier<Item> CRIMSON_PRAETOR_CHEST = ITEMS.register("crimson_praetor_chest", () -> new ItemCultistLeaderArmor(ArmorItem.Type.CHESTPLATE));

    public static final Supplier<Item> CRIMSON_PRAETOR_LEGS = ITEMS.register("crimson_praetor_legs", () -> new ItemCultistLeaderArmor(ArmorItem.Type.LEGGINGS));



    // Curiosities

    public static final Supplier<Item> CURIO_ARCANE = ITEMS.register("curio_arcane", () -> new TCCurioItem(TCCurioItem.Kind.ARCANE));

    public static final Supplier<Item> CURIO_PRESERVED = ITEMS.register("curio_preserved", () -> new TCCurioItem(TCCurioItem.Kind.PRESERVED));

    public static final Supplier<Item> CURIO_ANCIENT = ITEMS.register("curio_ancient", () -> new TCCurioItem(TCCurioItem.Kind.ANCIENT));

    public static final Supplier<Item> CURIO_ELDRITCH = ITEMS.register("curio_eldritch", () -> new TCCurioItem(TCCurioItem.Kind.ELDRITCH));

    public static final Supplier<Item> CURIO_KNOWLEDGE = ITEMS.register("curio_knowledge", () -> new TCCurioItem(TCCurioItem.Kind.KNOWLEDGE));

    public static final Supplier<Item> CURIO_TWISTED = ITEMS.register("curio_twisted", () -> new TCCurioItem(TCCurioItem.Kind.TWISTED));



    // Loot Bags

    public static final Supplier<Item> LOOT_BAG_COMMON = ITEMS.register("loot_bag_common", () -> new TCLootBagItem(TCLootBagItem.Tier.COMMON));

    public static final Supplier<Item> LOOT_BAG_UNCOMMON = ITEMS.register("loot_bag_uncommon", () -> new TCLootBagItem(TCLootBagItem.Tier.UNCOMMON));

    public static final Supplier<Item> LOOT_BAG_RARE = ITEMS.register("loot_bag_rare", () -> new TCLootBagItem(TCLootBagItem.Tier.RARE));



    public static final Supplier<Item> PECH_WAND = ITEMS.register("pech_wand", ItemPechWand::new);

    public static final Supplier<Item> SANITY_CHECKER = ITEMS.register("sanity_checker", ItemSanityChecker::new);

    public static final Supplier<Item> SCRIBING_TOOLS = simpleItem("scribing_tools");

    public static final Supplier<Item> CASTER_BASIC = ITEMS.register("caster_basic", () -> new ItemCaster(0));

    public static final Supplier<Item> FOCUS_1 = ITEMS.register("focus_1", () -> new ItemFocus(15));

    public static final Supplier<Item> FOCUS_2 = ITEMS.register("focus_2", () -> new ItemFocus(25));

    public static final Supplier<Item> FOCUS_3 = ITEMS.register("focus_3", () -> new ItemFocus(50));

    public static final Supplier<Item> FOCUS_POUCH = ITEMS.register("focus_pouch", ItemFocusPouch::new);

    public static final Supplier<Item> THAUMIUM_AXE = simpleItem("thaumium_axe");

    public static final Supplier<Item> THAUMIUM_HOE = simpleItem("thaumium_hoe");

    public static final Supplier<Item> THAUMIUM_PICK = simpleItem("thaumium_pick");

    public static final Supplier<Item> THAUMIUM_SHOVEL = simpleItem("thaumium_shovel");

    public static final Supplier<Item> THAUMIUM_SWORD = simpleItem("thaumium_sword");

    public static final Supplier<Item> PHIAL = ITEMS.register("phial", TCPhialItem::new);

    public static final Supplier<Item> JAR_LABEL = ITEMS.register("jar_label", () -> new TCJarLabelItem(false));

    public static final Supplier<Item> GOLEM_PLACER = ITEMS.register("golem", () -> new ItemGolemPlacer(new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> GOLEM_BELL = ITEMS.register("golem_bell", () -> new ItemGolemBell(new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> SEAL_PICKUP = ITEMS.register("seal_pickup", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:pickup"));

    public static final Supplier<Item> SEAL_PICKUP_ADVANCED = ITEMS.register("seal_pickup_advanced", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:pickup_advanced"));

    public static final Supplier<Item> SEAL_FILL = ITEMS.register("seal_fill", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:fill"));

    public static final Supplier<Item> SEAL_FILL_ADVANCED = ITEMS.register("seal_fill_advanced", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:fill_advanced"));

    public static final Supplier<Item> SEAL_EMPTY = ITEMS.register("seal_empty", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:empty"));

    public static final Supplier<Item> SEAL_EMPTY_ADVANCED = ITEMS.register("seal_empty_advanced", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:empty_advanced"));

    public static final Supplier<Item> SEAL_GUARD = ITEMS.register("seal_guard", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:guard"));

    public static final Supplier<Item> SEAL_GUARD_ADVANCED = ITEMS.register("seal_guard_advanced", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:guard_advanced"));

    public static final Supplier<Item> SEAL_BREAKER = ITEMS.register("seal_breaker", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:breaker"));

    public static final Supplier<Item> SEAL_BREAKER_ADVANCED = ITEMS.register("seal_breaker_advanced", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:breaker_advanced"));

    public static final Supplier<Item> SEAL_LUMBER = ITEMS.register("seal_lumber", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:lumber"));

    public static final Supplier<Item> SEAL_PROVIDER = ITEMS.register("seal_provider", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:provider"));

    public static final Supplier<Item> SEAL_STOCK = ITEMS.register("seal_stock", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:stock"));

    public static final Supplier<Item> SEAL_USE = ITEMS.register("seal_use", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:use"));

    public static final Supplier<BlockItem> JAR_BRAIN = ITEMS.register("jar_brain", () -> new TCBrainJarBlockItem(TCBlocks.JAR_BRAIN.get(), new Item.Properties()));

    public static final Supplier<Item> SEAL_BUTCHER = ITEMS.register("seal_butcher", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:butcher"));

    public static final Supplier<Item> SEAL_HARVEST = ITEMS.register("seal_harvest", () -> new ItemSealPlacer(new Item.Properties(), "thaumcraft:harvest"));

    public static final Supplier<Item> TRAVELLER_BOOTS = ITEMS.register("traveller_boots", ItemTravellerBoots::new);

    public static final Supplier<Item> CLOUD_RING = ITEMS.register("cloud_ring", ItemCloudRing::new);
    public static final Supplier<Item> CHARM_UNDYING = ITEMS.register("charm_undying", ItemCharmUndying::new);
    public static final Supplier<Item> ELEMENTAL_AXE = ITEMS.register("elemental_axe", ItemElementalAxe::new);

    public static final Supplier<Item> ELEMENTAL_PICK = ITEMS.register("elemental_pick", ItemElementalPickaxe::new);

    public static final Supplier<Item> ELEMENTAL_SWORD = ITEMS.register("elemental_sword", ItemElementalSword::new);

    public static final Supplier<Item> ELEMENTAL_SHOVEL = ITEMS.register("elemental_shovel", ItemElementalShovel::new);

    public static final Supplier<Item> ELEMENTAL_HOE = ITEMS.register("elemental_hoe", ItemElementalHoe::new);

    public static final Supplier<Item> THAUMIUM_FORTRESS_HELM = ITEMS.register("thaumium_fortress_helm", () -> new ItemFortressArmor(ArmorItem.Type.HELMET));

    public static final Supplier<Item> THAUMIUM_FORTRESS_HELM_GOGGLES = ITEMS.register("thaumium_fortress_helm_goggles", () -> new ItemFortressArmor(ArmorItem.Type.HELMET));

    public static final Supplier<Item> THAUMIUM_FORTRESS_CHEST = ITEMS.register("thaumium_fortress_chest", () -> new ItemFortressArmor(ArmorItem.Type.CHESTPLATE));

    public static final Supplier<Item> THAUMIUM_FORTRESS_LEGS = ITEMS.register("thaumium_fortress_legs", () -> new ItemFortressArmor(ArmorItem.Type.LEGGINGS));

    public static final Supplier<Item> FORTRESS_HELM = THAUMIUM_FORTRESS_HELM;

    public static final Supplier<Item> FORTRESS_CHEST = THAUMIUM_FORTRESS_CHEST;

    public static final Supplier<Item> FORTRESS_LEGS = THAUMIUM_FORTRESS_LEGS;

    public static final Supplier<Item> VERDANT_HEART = ITEMS.register("verdant_heart", () -> new ItemVerdantHeart(ItemVerdantHeart.Type.BASE));
    public static final Supplier<Item> VERDANT_HEART_LIFE = ITEMS.register("verdant_heart_life", () -> new ItemVerdantHeart(ItemVerdantHeart.Type.LIFE));
    public static final Supplier<Item> VERDANT_HEART_SUSTAIN = ITEMS.register("verdant_heart_sustain", () -> new ItemVerdantHeart(ItemVerdantHeart.Type.SUSTAIN));
    public static final Supplier<Item> TRIPLE_MEAT_TREAT = simpleItem("triple_meat_treat");

    public static final Supplier<Item> MASK_GRINNING_DEVIL = simpleItem("mask_grinning_devil");

    public static final Supplier<Item> MASK_ANGRY_GHOST = simpleItem("mask_angry_ghost");

    public static final Supplier<Item> MASK_SIPPING_FIEND = simpleItem("mask_sipping_fiend");

    public static final Supplier<Item> MIND_CLOCKWORK_ADVANCED = simpleItem("mindclockwork_advanced");

    public static final Supplier<Item> GOLEM_PRESS_BLUEPRINT = simpleItem("golem_press_blueprint");

    public static final Supplier<Item> INFUSION_ALTAR_ELDRITCH_BLUEPRINT = simpleItem("infusion_altar_eldritch_blueprint");

    public static final Supplier<Item> INFUSION_ALTAR_ANCIENT_BLUEPRINT = simpleItem("infusion_altar_ancient_blueprint");

    public static final Supplier<Item> INFUSION_ALTAR_BLUEPRINT = simpleItem("infusion_altar_blueprint");

    public static final Supplier<BlockItem> ELDRITCH_PEDESTAL = blockItem("eldritch_pedestal", TCBlocks.ELDRITCH_PEDESTAL);

    public static final Supplier<BlockItem> ANCIENT_PEDESTAL = blockItem("ancient_pedestal", TCBlocks.ANCIENT_PEDESTAL);

    public static final Supplier<BlockItem> ARCANE_PEDESTAL = blockItem("arcane_pedestal", TCBlocks.ARCANE_PEDESTAL);

    public static final Supplier<BlockItem> PEDESTAL_ARCANE = blockItem("pedestal_arcane", TCBlocks.PEDESTAL_ARCANE);

    public static final Supplier<BlockItem> PEDESTAL_ANCIENT = blockItem("pedestal_ancient", TCBlocks.PEDESTAL_ANCIENT);

    public static final Supplier<BlockItem> PEDESTAL_ELDRITCH = blockItem("pedestal_eldritch", TCBlocks.PEDESTAL_ELDRITCH);

    public static final Supplier<BlockItem> RECHARGE_PEDESTAL = blockItem("recharge_pedestal", TCBlocks.RECHARGE_PEDESTAL);

    public static final Supplier<BlockItem> PAVING_STONE_TRAVEL = blockItem("paving_stone_travel", TCBlocks.PAVING_STONE_TRAVEL);

    public static final Supplier<BlockItem> PAVING_STONE_BARRIER = blockItem("paving_stone_barrier", TCBlocks.PAVING_STONE_BARRIER);

    public static final Supplier<BlockItem> LOOT_CRATE_COMMON = blockItem("loot_crate_common", TCBlocks.LOOT_CRATE_COMMON);

    public static final Supplier<BlockItem> LOOT_CRATE_UNCOMMON = blockItem("loot_crate_uncommon", TCBlocks.LOOT_CRATE_UNCOMMON);

    public static final Supplier<BlockItem> LOOT_CRATE_RARE = blockItem("loot_crate_rare", TCBlocks.LOOT_CRATE_RARE);

    public static final Supplier<BlockItem> LOOT_URN_COMMON = blockItem("loot_urn_common", TCBlocks.LOOT_URN_COMMON);

    public static final Supplier<BlockItem> LOOT_URN_UNCOMMON = blockItem("loot_urn_uncommon", TCBlocks.LOOT_URN_UNCOMMON);

    public static final Supplier<BlockItem> LOOT_URN_RARE = blockItem("loot_urn_rare", TCBlocks.LOOT_URN_RARE);

    public static final Supplier<BlockItem> VIS_GENERATOR = blockItem("vis_generator", TCBlocks.VIS_GENERATOR);
    public static final Supplier<BlockItem> VIS_BATTERY = blockItem("vis_battery", TCBlocks.VIS_BATTERY);
    public static final Supplier<BlockItem> CONDENSER = blockItem("condenser", TCBlocks.CONDENSER);
    public static final Supplier<BlockItem> CONDENSER_LATTICE = blockItem("condenser_lattice", TCBlocks.CONDENSER_LATTICE);
    public static final Supplier<BlockItem> CONDENSER_LATTICE_DIRTY = blockItem("condenser_lattice_dirty", TCBlocks.CONDENSER_LATTICE_DIRTY);
    public static final Supplier<BlockItem> PATTERN_CRAFTER = blockItem("pattern_crafter", TCBlocks.PATTERN_CRAFTER);
    public static final Supplier<BlockItem> POTION_SPRAYER = blockItem("potion_sprayer", TCBlocks.POTION_SPRAYER);
    public static final Supplier<Item> GRAPPLE_GUN = ITEMS.register("grapple_gun", () -> new thaumcraft.common.items.tools.TCGrappleGunItem(new Item.Properties()));
    public static final Supplier<Item> GRAPPLE_GUN_TIP = simpleItem("grapple_gun_tip");
    public static final Supplier<Item> GRAPPLE_GUN_SPOOL = simpleItem("grapple_gun_spool");


    public static final Supplier<Item> CAUSALITY_COLLAPSER = ITEMS.register("causality_collapser", ItemCausalityCollapser::new);



    // TC6 exposed spawn eggs for these mobs. Keeping the original two legacy

    // colors makes them easy to identify in the creative inventory and restores

    // the vanilla /give and dispenser behavior through DeferredSpawnEggItem.

    public static final Supplier<Item> CULTIST_LEADER_SPAWN_EGG = spawnEgg("cultist_leader_spawn_egg", TCEntityTypes.CULTIST_LEADER, 6842578, 9438728);

    public static final Supplier<Item> TAINTACLE_GIANT_SPAWN_EGG = spawnEgg("taintacle_giant_spawn_egg", TCEntityTypes.TAINTACLE_GIANT, 6842578, 10618530);

    public static final Supplier<Item> BRAINY_ZOMBIE_SPAWN_EGG = spawnEgg("brainy_zombie_spawn_egg", TCEntityTypes.BRAINY_ZOMBIE, -16129, -16744448);

    public static final Supplier<Item> GIANT_BRAINY_ZOMBIE_SPAWN_EGG = spawnEgg("giant_brainy_zombie_spawn_egg", TCEntityTypes.GIANT_BRAINY_ZOMBIE, -16129, -16760832);

    public static final Supplier<Item> WISP_SPAWN_EGG = spawnEgg("wisp_spawn_egg", TCEntityTypes.WISP, -16129, -1);

    public static final Supplier<Item> FIREBAT_SPAWN_EGG = spawnEgg("firebat_spawn_egg", TCEntityTypes.FIREBAT, -16129, -806354944);

    public static final Supplier<Item> PECH_SPAWN_EGG = spawnEgg("pech_spawn_egg", TCEntityTypes.PECH, -16129, -12582848);

    public static final Supplier<Item> MIND_SPIDER_SPAWN_EGG = spawnEgg("mind_spider_spawn_egg", TCEntityTypes.MIND_SPIDER, 4996656, 4473924);

    public static final Supplier<Item> ELDRITCH_GUARDIAN_SPAWN_EGG = spawnEgg("eldritch_guardian_spawn_egg", TCEntityTypes.ELDRITCH_GUARDIAN, 8421504, 0);

    public static final Supplier<Item> CULTIST_KNIGHT_SPAWN_EGG = spawnEgg("cultist_knight_spawn_egg", TCEntityTypes.CULTIST_KNIGHT, 9438728, 128);

    public static final Supplier<Item> CULTIST_CLERIC_SPAWN_EGG = spawnEgg("cultist_cleric_spawn_egg", TCEntityTypes.CULTIST_CLERIC, 9438728, 8388608);

    public static final Supplier<Item> ELDRITCH_CRAB_SPAWN_EGG = spawnEgg("eldritch_crab_spawn_egg", TCEntityTypes.ELDRITCH_CRAB, 8421504, 5570560);

    public static final Supplier<Item> INHABITED_ZOMBIE_SPAWN_EGG = spawnEgg("inhabited_zombie_spawn_egg", TCEntityTypes.INHABITED_ZOMBIE, 8421504, 5570560);

    public static final Supplier<Item> THAUM_SLIME_SPAWN_EGG = spawnEgg("thaum_slime_spawn_egg", TCEntityTypes.THAUM_SLIME, 10618530, -32513);

    public static final Supplier<Item> TAINT_CRAWLER_SPAWN_EGG = spawnEgg("taint_crawler_spawn_egg", TCEntityTypes.TAINT_CRAWLER, 10618530, 3158064);

    public static final Supplier<Item> TAINTACLE_SPAWN_EGG = spawnEgg("taintacle_spawn_egg", TCEntityTypes.TAINTACLE, 10618530, 4469572);

    public static final Supplier<Item> TAINT_SWARM_SPAWN_EGG = spawnEgg("taint_swarm_spawn_egg", TCEntityTypes.TAINT_SWARM, 10618530, 16744576);

    public static final Supplier<Item> TAINT_SEED_SPAWN_EGG = spawnEgg("taint_seed_spawn_egg", TCEntityTypes.TAINT_SEED, 10618530, 4465237);

    public static final Supplier<Item> TAINT_SEED_PRIME_SPAWN_EGG = spawnEgg("taint_seed_prime_spawn_egg", TCEntityTypes.TAINT_SEED_PRIME, 10618530, 5583718);

    public static final Supplier<BlockItem> VOID_SIPHON = blockItem("void_siphon", TCBlocks.VOID_SIPHON);

    public static final Supplier<BlockItem> SPA = blockItem("spa", TCBlocks.SPA);

    public static final Supplier<BlockItem> THAUMATORIUM = blockItem("thaumatorium", TCBlocks.THAUMATORIUM);

    public static final Supplier<Item> ESSENTIA_EXPORTER = simpleItem("essentia_exporter");

    public static final Supplier<Item> ESSENTIA_IMPORTER = simpleItem("essentia_importer");

    public static final Supplier<Item> ALCHEMICAL_CONSTRUCT = simpleItem("alchemical_construct");

    public static final Supplier<Item> JAR_LABEL_ESSENCE = ITEMS.register("jar_label_essence", () -> new TCJarLabelItem(true));

    public static final Supplier<BlockItem> EVERFULL_URN = blockItem("everfull_urn", TCBlocks.EVERFULL_URN);

    public static final Supplier<BlockItem> LAMP_ARCANE = blockItem("lamp_arcane", TCBlocks.LAMP_ARCANE);

    public static final Supplier<Item> HAND_MIRROR = ITEMS.register("hand_mirror", ItemHandMirror::new);

    public static final Supplier<BlockItem> MIRROR = ITEMS.register("mirror", () -> new TCMirrorBlockItem(TCBlocks.MIRROR.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final Supplier<BlockItem> MIRROR_ESSENTIA = ITEMS.register("mirror_essentia", () -> new TCMirrorBlockItem(TCBlocks.MIRROR_ESSENTIA.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final Supplier<BlockItem> LAMP_FERTILITY = blockItem("lamp_fertility", TCBlocks.LAMP_FERTILITY);

    public static final Supplier<BlockItem> LAMP_GROWTH = blockItem("lamp_growth", TCBlocks.LAMP_GROWTH);

    public static final Supplier<BlockItem> INFERNAL_FURNACE = blockItem("infernal_furnace", TCBlocks.INFERNAL_FURNACE);

    public static final Supplier<Item> ARCANE_BORE = ITEMS.register("arcane_bore", ItemArcaneBore::new);

    public static final Supplier<Item> PRIMAL_CRUSHER = ITEMS.register("primal_crusher", ItemPrimalCrusher::new);

    public static final Supplier<Item> VOIDSEER_PEARL = ITEMS.register("voidseer_pearl", ItemVoidseerPearl::new);
    public static final Supplier<Item> VOID_ROBE_LEGS = ITEMS.register("void_robe_legs", () -> new ItemVoidRobeArmor(ArmorItem.Type.LEGGINGS));

    public static final Supplier<Item> VOID_ROBE_CHEST = ITEMS.register("void_robe_chest", () -> new ItemVoidRobeArmor(ArmorItem.Type.CHESTPLATE));

    public static final Supplier<Item> VOID_ROBE_HELM = ITEMS.register("void_robe_helm", () -> new ItemVoidRobeArmor(ArmorItem.Type.HELMET));

    public static final Supplier<Item> CLOTH_LEGS = ITEMS.register("cloth_legs", () -> new ItemRobeArmor(ArmorItem.Type.LEGGINGS));

    public static final Supplier<Item> CLOTH_CHEST = ITEMS.register("cloth_chest", () -> new ItemRobeArmor(ArmorItem.Type.CHESTPLATE));

    public static final Supplier<Item> CLOTH_BOOTS = ITEMS.register("cloth_boots", () -> new ItemRobeArmor(ArmorItem.Type.BOOTS));

    public static final Supplier<Item> VOID_SEED = simpleItem("void_seed");

    public static final Supplier<BlockItem> ARCANE_EAR = blockItem("arcane_ear", TCBlocks.ARCANE_EAR);

    public static final Supplier<BlockItem> ARCANE_EAR_TOGGLE = blockItem("arcane_ear_toggle", TCBlocks.ARCANE_EAR_TOGGLE);

    public static final Supplier<BlockItem> HUNGRY_CHEST = blockItem("hungry_chest", TCBlocks.HUNGRY_CHEST);

    public static final Supplier<BlockItem> DIOPTRA = blockItem("dioptra", TCBlocks.DIOPTRA);

    public static final Supplier<BlockItem> LEVITATOR = blockItem("levitator", TCBlocks.LEVITATOR);

    public static final Supplier<Item> BAUBLE_AMULET = ITEMS.register("bauble_amulet", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> VIS_AMULET = ITEMS.register("vis_amulet", ItemAmuletVis::new);
    public static final Supplier<Item> BAUBLE_AMULET_FANCY = ITEMS.register("bauble_amulet_fancy", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final Supplier<Item> BAUBLE_GIRDLE = ITEMS.register("bauble_girdle", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> BAUBLE_GIRDLE_FANCY = ITEMS.register("bauble_girdle_fancy", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final Supplier<Item> BAUBLE_RING = ITEMS.register("bauble_ring", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> BAUBLE_CHARM = ITEMS.register("bauble_charm", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> BAUBLE_RING_FANCY = ITEMS.register("bauble_ring_fancy", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final Supplier<Item> RING_APPRENTICE = ITEMS.register("ring_apprentice", () -> new ItemVisDiscountGear(5, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final Supplier<Item> CURIOSITY_BAND = ITEMS.register("curiosity_band", ItemCuriosityBand::new);
    public static final Supplier<Item> BRASS_BRACE = simpleItem("brass_brace");

    public static final Supplier<Item> THAUMIUM_HELM = ITEMS.register("thaumium_helm", () -> new ItemThaumiumArmor(ArmorItem.Type.HELMET));

    public static final Supplier<Item> THAUMIUM_CHEST = ITEMS.register("thaumium_chest", () -> new ItemThaumiumArmor(ArmorItem.Type.CHESTPLATE));

    public static final Supplier<Item> THAUMIUM_LEGS = ITEMS.register("thaumium_legs", () -> new ItemThaumiumArmor(ArmorItem.Type.LEGGINGS));

    public static final Supplier<Item> THAUMIUM_BOOTS = ITEMS.register("thaumium_boots", () -> new ItemThaumiumArmor(ArmorItem.Type.BOOTS));

    public static final Supplier<Item> VOID_INGOT = simpleItem("void_ingot");

    public static final Supplier<Item> VOID_AXE = simpleItem("void_axe");

    public static final Supplier<Item> VOID_HOE = simpleItem("void_hoe");

    public static final Supplier<Item> VOID_PICK = simpleItem("void_pick");

    public static final Supplier<Item> VOID_SHOVEL = simpleItem("void_shovel");

    public static final Supplier<Item> VOID_SWORD = simpleItem("void_sword");

    public static final Supplier<Item> VOID_HELM = ITEMS.register("void_helm", () -> new ItemVoidArmor(ArmorItem.Type.HELMET));

    public static final Supplier<Item> VOID_CHEST = ITEMS.register("void_chest", () -> new ItemVoidArmor(ArmorItem.Type.CHESTPLATE));

    public static final Supplier<Item> VOID_LEGS = ITEMS.register("void_legs", () -> new ItemVoidArmor(ArmorItem.Type.LEGGINGS));

    public static final Supplier<Item> VOID_BOOTS = ITEMS.register("void_boots", () -> new ItemVoidArmor(ArmorItem.Type.BOOTS));



    public static final Supplier<Item> CRYSTAL_ESSENCE_AER = simpleItem("crystal_essence_aer");

    public static final Supplier<Item> CRYSTAL_ESSENCE_TERRA = simpleItem("crystal_essence_terra");

    public static final Supplier<Item> CRYSTAL_ESSENCE_IGNIS = simpleItem("crystal_essence_ignis");

    public static final Supplier<Item> CRYSTAL_ESSENCE_AQUA = simpleItem("crystal_essence_aqua");

    public static final Supplier<Item> CRYSTAL_ESSENCE_ORDO = simpleItem("crystal_essence_ordo");

    public static final Supplier<Item> CRYSTAL_ESSENCE_PERDITIO = simpleItem("crystal_essence_perditio");

    public static final Supplier<Item> CRYSTAL_ESSENCE_VACUOS = simpleItem("crystal_essence_vacuos");

    public static final Supplier<Item> CRYSTAL_ESSENCE_LUX = simpleItem("crystal_essence_lux");

    public static final Supplier<Item> CRYSTAL_ESSENCE_MOTUS = simpleItem("crystal_essence_motus");

    public static final Supplier<Item> CRYSTAL_ESSENCE_GELUM = simpleItem("crystal_essence_gelum");

    public static final Supplier<Item> CRYSTAL_ESSENCE_VITREUS = simpleItem("crystal_essence_vitreus");

    public static final Supplier<Item> CRYSTAL_ESSENCE_METALLUM = simpleItem("crystal_essence_metallum");

    public static final Supplier<Item> CRYSTAL_ESSENCE_VICTUS = simpleItem("crystal_essence_victus");

    public static final Supplier<Item> CRYSTAL_ESSENCE_MORTUUS = simpleItem("crystal_essence_mortuus");

    public static final Supplier<Item> CRYSTAL_ESSENCE_POTENTIA = simpleItem("crystal_essence_potentia");

    public static final Supplier<Item> CRYSTAL_ESSENCE_PERMUTATIO = simpleItem("crystal_essence_permutatio");

    public static final Supplier<Item> CRYSTAL_ESSENCE_PRAECANTATIO = simpleItem("crystal_essence_praecantatio");

    public static final Supplier<Item> CRYSTAL_ESSENCE_AURAM = simpleItem("crystal_essence_auram");

    public static final Supplier<Item> CRYSTAL_ESSENCE_ALKIMIA = simpleItem("crystal_essence_alkimia");

    public static final Supplier<Item> CRYSTAL_ESSENCE_VITIUM = simpleItem("crystal_essence_vitium");

    public static final Supplier<Item> CRYSTAL_ESSENCE_TENEBRAE = simpleItem("crystal_essence_tenebrae");

    public static final Supplier<Item> CRYSTAL_ESSENCE_ALIENIS = simpleItem("crystal_essence_alienis");

    public static final Supplier<Item> CRYSTAL_ESSENCE_VOLATUS = simpleItem("crystal_essence_volatus");

    public static final Supplier<Item> CRYSTAL_ESSENCE_HERBA = simpleItem("crystal_essence_herba");

    public static final Supplier<Item> CRYSTAL_ESSENCE_INSTRUMENTUM = simpleItem("crystal_essence_instrumentum");

    public static final Supplier<Item> CRYSTAL_ESSENCE_FABRICO = simpleItem("crystal_essence_fabrico");

    public static final Supplier<Item> CRYSTAL_ESSENCE_MACHINA = simpleItem("crystal_essence_machina");

    public static final Supplier<Item> CRYSTAL_ESSENCE_VINCULUM = simpleItem("crystal_essence_vinculum");

    public static final Supplier<Item> CRYSTAL_ESSENCE_SPIRITUS = simpleItem("crystal_essence_spiritus");

    public static final Supplier<Item> CRYSTAL_ESSENCE_COGNITIO = simpleItem("crystal_essence_cognitio");

    public static final Supplier<Item> CRYSTAL_ESSENCE_SENSUS = simpleItem("crystal_essence_sensus");

    public static final Supplier<Item> CRYSTAL_ESSENCE_AVERSIO = simpleItem("crystal_essence_aversio");

    public static final Supplier<Item> CRYSTAL_ESSENCE_PRAEMUNIO = simpleItem("crystal_essence_praemunio");

    public static final Supplier<Item> CRYSTAL_ESSENCE_DESIDERIUM = simpleItem("crystal_essence_desiderium");

    public static final Supplier<Item> CRYSTAL_ESSENCE_EXANIMIS = simpleItem("crystal_essence_exanimis");

    public static final Supplier<Item> CRYSTAL_ESSENCE_BESTIA = simpleItem("crystal_essence_bestia");

    public static final Supplier<Item> CRYSTAL_ESSENCE_HUMANUS = simpleItem("crystal_essence_humanus");



    public static final Supplier<Item> PHIAL_AER = simpleItem("phial_aer");

    public static final Supplier<Item> PHIAL_TERRA = simpleItem("phial_terra");

    public static final Supplier<Item> PHIAL_IGNIS = simpleItem("phial_ignis");

    public static final Supplier<Item> PHIAL_AQUA = simpleItem("phial_aqua");

    public static final Supplier<Item> PHIAL_ORDO = simpleItem("phial_ordo");

    public static final Supplier<Item> PHIAL_PERDITIO = simpleItem("phial_perditio");

    public static final Supplier<Item> PHIAL_VACUOS = simpleItem("phial_vacuos");

    public static final Supplier<Item> PHIAL_LUX = simpleItem("phial_lux");

    public static final Supplier<Item> PHIAL_MOTUS = simpleItem("phial_motus");

    public static final Supplier<Item> PHIAL_GELUM = simpleItem("phial_gelum");

    public static final Supplier<Item> PHIAL_VITREUS = simpleItem("phial_vitreus");

    public static final Supplier<Item> PHIAL_METALLUM = simpleItem("phial_metallum");

    public static final Supplier<Item> PHIAL_VICTUS = simpleItem("phial_victus");

    public static final Supplier<Item> PHIAL_MORTUUS = simpleItem("phial_mortuus");

    public static final Supplier<Item> PHIAL_POTENTIA = simpleItem("phial_potentia");

    public static final Supplier<Item> PHIAL_PERMUTATIO = simpleItem("phial_permutatio");

    public static final Supplier<Item> PHIAL_PRAECANTATIO = simpleItem("phial_praecantatio");

    public static final Supplier<Item> PHIAL_AURAM = simpleItem("phial_auram");

    public static final Supplier<Item> PHIAL_ALKIMIA = simpleItem("phial_alkimia");

    public static final Supplier<Item> PHIAL_VITIUM = simpleItem("phial_vitium");

    public static final Supplier<Item> PHIAL_TENEBRAE = simpleItem("phial_tenebrae");

    public static final Supplier<Item> PHIAL_ALIENIS = simpleItem("phial_alienis");

    public static final Supplier<Item> PHIAL_VOLATUS = simpleItem("phial_volatus");

    public static final Supplier<Item> PHIAL_HERBA = simpleItem("phial_herba");

    public static final Supplier<Item> PHIAL_INSTRUMENTUM = simpleItem("phial_instrumentum");

    public static final Supplier<Item> PHIAL_FABRICO = simpleItem("phial_fabrico");

    public static final Supplier<Item> PHIAL_MACHINA = simpleItem("phial_machina");

    public static final Supplier<Item> PHIAL_VINCULUM = simpleItem("phial_vinculum");

    public static final Supplier<Item> PHIAL_SPIRITUS = simpleItem("phial_spiritus");

    public static final Supplier<Item> PHIAL_COGNITIO = simpleItem("phial_cognitio");

    public static final Supplier<Item> PHIAL_SENSUS = simpleItem("phial_sensus");

    public static final Supplier<Item> PHIAL_AVERSIO = simpleItem("phial_aversio");

    public static final Supplier<Item> PHIAL_PRAEMUNIO = simpleItem("phial_praemunio");

    public static final Supplier<Item> PHIAL_DESIDERIUM = simpleItem("phial_desiderium");

    public static final Supplier<Item> PHIAL_EXANIMIS = simpleItem("phial_exanimis");

    public static final Supplier<Item> PHIAL_BESTIA = simpleItem("phial_bestia");

    public static final Supplier<Item> PHIAL_HUMANUS = simpleItem("phial_humanus");



    public static final Supplier<Item> ENCHANTED_PLACEHOLDER_PROTECTION_1 = simpleItem("enchanted_placeholder_protection_1");

    public static final Supplier<Item> ENCHANTED_PLACEHOLDER_SHARPNESS_1 = simpleItem("enchanted_placeholder_sharpness_1");

    public static final Supplier<Item> ENCHANTED_PLACEHOLDER_SILK_TOUCH_1 = simpleItem("enchanted_placeholder_silk_touch_1");

    public static final Supplier<Item> ENCHANTED_PLACEHOLDER_FORTUNE_1 = simpleItem("enchanted_placeholder_fortune_1");



    private static Supplier<Item> simpleItem(String id) {

        return ITEMS.register(id, () -> createSimpleItem(id));

    }



    private static Item createSimpleItem(String id) {

        if (id.startsWith("crystal_essence_")) {

            String aspect = id.substring("crystal_essence_".length());

            return new ItemAspectVariant(ItemAspectVariant.Kind.CRYSTAL_ESSENCE, aspect, 1);

        }

        if (id.startsWith("phial_")) {

            String aspect = id.substring("phial_".length());

            return new ItemAspectVariant(ItemAspectVariant.Kind.PHIAL, aspect, 10);

        }



        return switch (id) {

            case "brain" -> new ItemZombieBrain();

            case "scribing_tools" -> new ItemScribingTools();

            case "thaumium_ingot" -> legacyItem("ingot", "thaumium", 0);

            case "brass_ingot" -> legacyItem("ingot", "brass", 2);

            case "brass_nugget" -> legacyItem("nugget", "brass", 8);

            case "copper_nugget" -> legacyItem("nugget", "copper", 1);

            case "tin_nugget" -> legacyItem("nugget", "tin", 2);

            case "silver_nugget" -> legacyItem("nugget", "silver", 3);

            case "lead_nugget" -> legacyItem("nugget", "lead", 4);

            case "thaumium_nugget" -> legacyItem("nugget", "thaumium", 6);

            case "void_nugget" -> legacyItem("nugget", "void", 7);

            case "quartz_nugget" -> legacyItem("nugget", "quartz", 9);

            case "brass_plate" -> legacyItem("plate", "brass", 0);

            case "iron_plate" -> legacyItem("plate", "iron", 1);

            case "thaumium_plate" -> legacyItem("plate", "thaumium", 2);

            case "void_plate" -> legacyItem("plate", "void", 3);

            case "rare_earth" -> legacyItem("nugget", "rare_earth", 10);

            case "primordial_pearl" -> legacyItem("primordial_pearl", "normal", 0);

            case "quicksilver_nugget" -> legacyItem("nugget", "quicksilver", 5);

            case "mindclockwork_advanced" -> legacyItem("mind", "advanced", 1);

            case "chunk_chicken" -> legacyItem("chunk", "chicken", 1);

            case "chunk_beef" -> legacyItem("chunk", "beef", 0);

            case "chunk_pork" -> legacyItem("chunk", "pork", 2);

            case "chunk_fish" -> legacyItem("chunk", "fish", 3);

            case "chunk_rabbit" -> legacyItem("chunk", "rabbit", 4);

            case "chunk_mutton" -> legacyItem("chunk", "mutton", 5);

            case "salis_mundus" -> new ItemSalisMundus();

            case "alumentum" -> new ItemAlumentum();

            case "thaumium_axe" -> new AxeItem(TCToolTiers.THAUMIUM, new Item.Properties().attributes(AxeItem.createAttributes(TCToolTiers.THAUMIUM, 5.0F, -3.0F)));

            case "thaumium_hoe" -> new HoeItem(TCToolTiers.THAUMIUM, new Item.Properties().attributes(HoeItem.createAttributes(TCToolTiers.THAUMIUM, -2.0F, -1.0F)));

            case "thaumium_pick" -> new PickaxeItem(TCToolTiers.THAUMIUM, new Item.Properties().attributes(PickaxeItem.createAttributes(TCToolTiers.THAUMIUM, 1.0F, -2.8F)));

            case "thaumium_shovel" -> new ShovelItem(TCToolTiers.THAUMIUM, new Item.Properties().attributes(ShovelItem.createAttributes(TCToolTiers.THAUMIUM, 1.5F, -3.0F)));

            case "thaumium_sword" -> new SwordItem(TCToolTiers.THAUMIUM, new Item.Properties().attributes(SwordItem.createAttributes(TCToolTiers.THAUMIUM, 3, -2.4F)));

            case "enchanted_placeholder_protection_1" -> legacyMagicPlaceholder("protection");

            case "enchanted_placeholder_sharpness_1" -> legacyMagicPlaceholder("sharpness");

            case "enchanted_placeholder_silk_touch_1" -> legacyMagicPlaceholder("silk_touch");

            case "enchanted_placeholder_fortune_1" -> legacyMagicPlaceholder("fortune");

            default -> new Item(new Item.Properties());

        };

    }



    private static Item legacyItem(String family, String variant, int metadata) {

        return new Item(new Item.Properties().component(

                TCDataComponents.LEGACY_ITEM.get(),

                new TCLegacyItemComponent(family, variant, metadata)

        ));

    }



    private static ItemLegacyPlaceholder legacyMagicPlaceholder(String id) {

        return new ItemLegacyPlaceholder(

                new Item.Properties()

                        .stacksTo(1)

                        .rarity(Rarity.RARE)

                        .component(TCDataComponents.STORED_MAGIC.get(), new TCStoredEnchantComponent(id, 1)),

                "tc.placeholder.enchanted",

                true

        );

    }



    private static Supplier<BlockItem> blockItem(String id, Supplier<? extends Block> block) {

        return ITEMS.register(id, () -> new BlockItem(block.get(), legacyBlockItemProperties(id)));

    }



    private static Supplier<Item> spawnEgg(

            String id,

            Supplier<? extends net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob>> entityType,

            int backgroundColor,

            int highlightColor

    ) {

        return ITEMS.register(id, () -> new DeferredSpawnEggItem(

                entityType,

                backgroundColor,

                highlightColor,

                new Item.Properties()

        ));

    }



    private static Item.Properties legacyBlockItemProperties(String id) {

        return switch (id) {

            case "metal_thaumium" -> new Item.Properties().component(

                    TCDataComponents.LEGACY_ITEM.get(),

                    new TCLegacyItemComponent("metal", "thaumium", 2)

            );

            case "metal_void" -> new Item.Properties().component(

                    TCDataComponents.LEGACY_ITEM.get(),

                    new TCLegacyItemComponent("metal", "void", 3)

            );

            default -> new Item.Properties();

        };

    }





    // Catalog bridge placeholder output items. These keep Thaumonomicon recipe pages on thaumcraft:* outputs

    public static final Supplier<Item> CATALOG_PLACEHOLDER_ACTIVATORRAIL = ITEMS.register("activatorrail", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_ANCIENTPEDESTAL = ITEMS.register("ancientpedestal", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_ARCANEPEDESTAL = ITEMS.register("arcanepedestal", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERBLACK = ITEMS.register("bannerblack", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERBLUE = ITEMS.register("bannerblue", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERBROWN = ITEMS.register("bannerbrown", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERCYAN = ITEMS.register("bannercyan", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERGRAY = ITEMS.register("bannergray", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERGREEN = ITEMS.register("bannergreen", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERLIGHTBLUE = ITEMS.register("bannerlightblue", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERLIME = ITEMS.register("bannerlime", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERMAGENTA = ITEMS.register("bannermagenta", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERORANGE = ITEMS.register("bannerorange", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERPINK = ITEMS.register("bannerpink", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERPURPLE = ITEMS.register("bannerpurple", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERRED = ITEMS.register("bannerred", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERSILVER = ITEMS.register("bannersilver", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERWHITE = ITEMS.register("bannerwhite", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_BANNERYELLOW = ITEMS.register("banneryellow", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_ELDRITCHPEDESTAL = ITEMS.register("eldritchpedestal", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_PAVEBARRIER = ITEMS.register("pavebarrier", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_PAVETRAVEL = ITEMS.register("pavetravel", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_RECHARGEPEDESTAL = ITEMS.register("rechargepedestal", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_REDSTONEINLAY = ITEMS.register("redstoneinlay", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_ADVALCHEMYCONSTRUCT = ITEMS.register("advalchemyconstruct", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_ALCHEMICALCONSTRUCT = ITEMS.register("alchemicalconstruct", () -> new Item(new Item.Properties()));

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_CENTRIFUGE = CENTRIFUGE;

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_CONDENSER = CONDENSER;

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_CONDENSERLATTICE = blockItem("condenserlattice", TCBlocks.CONDENSER_LATTICE);

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_DIOPTRA = DIOPTRA;

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_HUNGRYCHEST = blockItem("hungrychest", TCBlocks.HUNGRY_CHEST);

    public static final Supplier<Item> CATALOG_PLACEHOLDER_JARVOID = ITEMS.register("jarvoid", () -> new Item(new Item.Properties()));

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_LEVITATOR = LEVITATOR;

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_MATRIXCOST = MATRIX_COST;

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_MATRIXMOTION = MATRIX_SPEED;

    public static final Supplier<Item> CATALOG_PLACEHOLDER_MNEMONICMATRIX = ITEMS.register("mnemonicmatrix", () -> new Item(new Item.Properties()));

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_PATTERNCRAFTER = blockItem("patterncrafter", TCBlocks.PATTERN_CRAFTER);

    public static final Supplier<Item> CATALOG_PLACEHOLDER_REDSTONERELAY = ITEMS.register("redstonerelay", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_RESONATOR = ITEMS.register("resonator", () -> new Item(new Item.Properties()));

public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_STABILIZER = STABILIZER;

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_VISBATTERY = blockItem("visbattery", TCBlocks.VIS_BATTERY);

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_VISGENERATOR = blockItem("visgenerator", TCBlocks.VIS_GENERATOR);

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_WARDEDJAR = JAR_NORMAL;

    public static final Supplier<Item> ADVANCEDCROSSBOW = ITEMS.register("advancedcrossbow", () -> new thaumcraft.common.entities.construct.ItemTurretPlacer(true, new Item.Properties()));
    public static final Supplier<Item> CATALOG_PLACEHOLDER_ADVANCEDCROSSBOW = ADVANCEDCROSSBOW;

    public static final Supplier<Item> CATALOG_PLACEHOLDER_ARCANEEAR = ITEMS.register("arcaneear", () -> new Item(new Item.Properties()));

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_ARCANESPA = SPA;

    public static final Supplier<Item> AUTOMATEDCROSSBOW = ITEMS.register("automatedcrossbow", () -> new thaumcraft.common.entities.construct.ItemTurretPlacer(false, new Item.Properties()));
    public static final Supplier<Item> TURRET_PLACER = ITEMS.register("turret_placer", () -> new thaumcraft.common.entities.construct.ItemTurretPlacer(false, new Item.Properties()));
    public static final Supplier<Item> CATALOG_PLACEHOLDER_AUTOMATEDCROSSBOW = AUTOMATEDCROSSBOW;

    public static final Supplier<Item> CATALOG_PLACEHOLDER_FOCUSPOUCH = ITEMS.register("focuspouch", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_GRAPPLEGUN = ITEMS.register("grapplegun", () -> new thaumcraft.common.items.tools.TCGrappleGunItem(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_GRAPPLEGUNSPOOL = simpleItem("grapplegunspool");

    public static final Supplier<Item> CATALOG_PLACEHOLDER_GRAPPLEGUNTIP = simpleItem("grappleguntip");

    public static final Supplier<Item> CATALOG_PLACEHOLDER_MINDCLOCKWORK = ITEMS.register("mindclockwork", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_MODAGGRESSION = ITEMS.register("modaggression", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_MODVISION = ITEMS.register("modvision", () -> new Item(new Item.Properties()));

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_POTIONSPRAYER = blockItem("potionsprayer", TCBlocks.POTION_SPRAYER);

    public static final Supplier<Item> CATALOG_PLACEHOLDER_ROBEBOOTS = ITEMS.register("robeboots", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_ROBECHEST = ITEMS.register("robechest", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_ROBELEGS = ITEMS.register("robelegs", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> CATALOG_PLACEHOLDER_SEALBLANK = ITEMS.register("sealblank", () -> new Item(new Item.Properties()));

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_ESSENTIASMELTERTHAUMIUM = SMELTER_THAUMIUM;

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_ESSENTIASMELTERVOID = SMELTER_VOID;

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_ESSENTIATRANSPORTIN = ESSENTIA_TRANSPORT_IN;

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_ESSENTIATRANSPORTOUT = ESSENTIA_TRANSPORT_OUT;

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_TUBE = blockItem("tube", TCBlocks.TUBE);

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_TUBEBUFFER = blockItem("tube_buffer", TCBlocks.TUBE_BUFFER);

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_TUBEFILTER = blockItem("tube_filter", TCBlocks.TUBE_FILTER);

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_TUBEONEWAY = blockItem("tube_oneway", TCBlocks.TUBE_ONEWAY);

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_TUBERESTRICT = blockItem("tube_restrict", TCBlocks.TUBE_RESTRICT);

    public static final Supplier<BlockItem> CATALOG_PLACEHOLDER_TUBEVALVE = blockItem("tube_valve", TCBlocks.TUBE_VALVE);



    private TCItems() {

    }

}
