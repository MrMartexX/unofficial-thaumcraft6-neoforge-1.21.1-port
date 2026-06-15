package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.ItemAspectVariant;
import thaumcraft.common.items.ItemLegacyPlaceholder;
import thaumcraft.common.items.armor.ItemGoggles;
import thaumcraft.common.items.components.TCLegacyItemComponent;
import thaumcraft.common.items.components.TCStoredEnchantComponent;
import thaumcraft.common.items.consumables.ItemZombieBrain;
import thaumcraft.common.items.curios.ItemThaumonomicon;
import thaumcraft.common.items.tools.ItemScribingTools;
import thaumcraft.common.items.tools.ItemThaumometer;
import thaumcraft.common.items.tools.TCToolTiers;

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
    public static final Supplier<BlockItem> METAL_BRASS = blockItem("metal_brass", TCBlocks.METAL_BRASS);
    public static final Supplier<BlockItem> METAL_THAUMIUM = blockItem("metal_thaumium", TCBlocks.METAL_THAUMIUM);
    public static final Supplier<BlockItem> METAL_VOID = blockItem("metal_void", TCBlocks.METAL_VOID);
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
    public static final Supplier<BlockItem> TALLOW_CANDLE = blockItem("tallow_candle", TCBlocks.TALLOW_CANDLE);
    public static final Supplier<BlockItem> TALLOW_CANDLE_BLACK = blockItem("tallow_candle_black", TCBlocks.TALLOW_CANDLE_BLACK);
    public static final Supplier<BlockItem> TALLOW_CANDLE_BLUE = blockItem("tallow_candle_blue", TCBlocks.TALLOW_CANDLE_BLUE);
    public static final Supplier<BlockItem> TALLOW_CANDLE_BROWN = blockItem("tallow_candle_brown", TCBlocks.TALLOW_CANDLE_BROWN);
    public static final Supplier<BlockItem> TALLOW_CANDLE_CYAN = blockItem("tallow_candle_cyan", TCBlocks.TALLOW_CANDLE_CYAN);
    public static final Supplier<BlockItem> TALLOW_CANDLE_GRAY = blockItem("tallow_candle_gray", TCBlocks.TALLOW_CANDLE_GRAY);
    public static final Supplier<BlockItem> TALLOW_CANDLE_GREEN = blockItem("tallow_candle_green", TCBlocks.TALLOW_CANDLE_GREEN);
    public static final Supplier<BlockItem> TALLOW_CANDLE_LIGHTBLUE = blockItem("tallow_candle_lightblue", TCBlocks.TALLOW_CANDLE_LIGHTBLUE);
    public static final Supplier<BlockItem> TALLOW_CANDLE_LIME = blockItem("tallow_candle_lime", TCBlocks.TALLOW_CANDLE_LIME);
    public static final Supplier<BlockItem> TALLOW_CANDLE_MAGENTA = blockItem("tallow_candle_magenta", TCBlocks.TALLOW_CANDLE_MAGENTA);
    public static final Supplier<BlockItem> TALLOW_CANDLE_ORANGE = blockItem("tallow_candle_orange", TCBlocks.TALLOW_CANDLE_ORANGE);
    public static final Supplier<BlockItem> TALLOW_CANDLE_PINK = blockItem("tallow_candle_pink", TCBlocks.TALLOW_CANDLE_PINK);
    public static final Supplier<BlockItem> TALLOW_CANDLE_PURPLE = blockItem("tallow_candle_purple", TCBlocks.TALLOW_CANDLE_PURPLE);
    public static final Supplier<BlockItem> TALLOW_CANDLE_RED = blockItem("tallow_candle_red", TCBlocks.TALLOW_CANDLE_RED);
    public static final Supplier<BlockItem> TALLOW_CANDLE_SILVER = blockItem("tallow_candle_silver", TCBlocks.TALLOW_CANDLE_SILVER);
    public static final Supplier<BlockItem> TALLOW_CANDLE_WHITE = blockItem("tallow_candle_white", TCBlocks.TALLOW_CANDLE_WHITE);
    public static final Supplier<BlockItem> TALLOW_CANDLE_YELLOW = blockItem("tallow_candle_yellow", TCBlocks.TALLOW_CANDLE_YELLOW);
    public static final Supplier<BlockItem> TABLE_WOOD = blockItem("table_wood", TCBlocks.TABLE_WOOD);
    public static final Supplier<BlockItem> TABLE_STONE = blockItem("table_stone", TCBlocks.TABLE_STONE);
    public static final Supplier<BlockItem> ARCANE_WORKBENCH = blockItem("arcane_workbench", TCBlocks.ARCANE_WORKBENCH);
    public static final Supplier<BlockItem> ARCANE_WORKBENCH_CHARGER = blockItem("arcane_workbench_charger", TCBlocks.ARCANE_WORKBENCH_CHARGER);
    public static final Supplier<BlockItem> RESEARCH_TABLE = blockItem("research_table", TCBlocks.RESEARCH_TABLE);
    public static final Supplier<BlockItem> CRUCIBLE = blockItem("crucible", TCBlocks.CRUCIBLE);
    public static final Supplier<BlockItem> SMELTER_BASIC = blockItem("smelter_basic", TCBlocks.SMELTER_BASIC);
    public static final Supplier<BlockItem> WAND_WORKBENCH = blockItem("wand_workbench", TCBlocks.WAND_WORKBENCH);
    public static final Supplier<BlockItem> INFUSION_MATRIX = blockItem("infusion_matrix", TCBlocks.INFUSION_MATRIX);
    public static final Supplier<BlockItem> GOLEM_BUILDER = blockItem("golem_builder", TCBlocks.GOLEM_BUILDER);
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
    public static final Supplier<Item> FABRIC = ITEMS.register("fabric", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> THAUMIUM_INGOT = simpleItem("thaumium_ingot");
    public static final Supplier<Item> BRASS_INGOT = simpleItem("brass_ingot");
    public static final Supplier<Item> BRASS_PLATE = simpleItem("brass_plate");
    public static final Supplier<Item> IRON_PLATE = simpleItem("iron_plate");
    public static final Supplier<Item> THAUMIUM_PLATE = simpleItem("thaumium_plate");
    public static final Supplier<Item> VOID_PLATE = simpleItem("void_plate");
    public static final Supplier<Item> FILTER = simpleItem("filter");
    public static final Supplier<Item> MORPHIC_RESONATOR = simpleItem("morphic_resonator");
    public static final Supplier<Item> RARE_EARTH = simpleItem("rare_earth");
    public static final Supplier<Item> SALIS_MUNDUS = simpleItem("salis_mundus");
    public static final Supplier<Item> TALLOW = simpleItem("tallow");
    public static final Supplier<Item> MECHANISM_SIMPLE = simpleItem("mechanism_simple");
    public static final Supplier<Item> MECHANISM_COMPLEX = simpleItem("mechanism_complex");
    public static final Supplier<Item> VIS_RESONATOR = simpleItem("vis_resonator");
    public static final Supplier<Item> MIRRORED_GLASS = simpleItem("mirrored_glass");
    public static final Supplier<Item> BRAIN = simpleItem("brain");
    public static final Supplier<Item> ALUMENTUM = simpleItem("alumentum");
    public static final Supplier<Item> CURIO_RITES = simpleItem("curio_rites");
    public static final Supplier<Item> SCRIBING_TOOLS = simpleItem("scribing_tools");
    public static final Supplier<Item> CASTER_BASIC = simpleItem("caster_basic");
    public static final Supplier<Item> FOCUS_1 = simpleItem("focus_1");
    public static final Supplier<Item> FOCUS_2 = simpleItem("focus_2");
    public static final Supplier<Item> FOCUS_3 = simpleItem("focus_3");
    public static final Supplier<Item> THAUMIUM_AXE = simpleItem("thaumium_axe");
    public static final Supplier<Item> THAUMIUM_HOE = simpleItem("thaumium_hoe");
    public static final Supplier<Item> THAUMIUM_PICK = simpleItem("thaumium_pick");
    public static final Supplier<Item> THAUMIUM_SHOVEL = simpleItem("thaumium_shovel");
    public static final Supplier<Item> THAUMIUM_SWORD = simpleItem("thaumium_sword");
    public static final Supplier<Item> PHIAL = simpleItem("phial");
    public static final Supplier<Item> JAR_LABEL = simpleItem("jar_label");
    public static final Supplier<Item> GOLEM_BELL = simpleItem("golem_bell");
    public static final Supplier<Item> ARCANE_EAR_TOGGLE = simpleItem("arcane_ear_toggle");
    public static final Supplier<Item> BAUBLE_AMULET = simpleItem("bauble_amulet");
    public static final Supplier<Item> BAUBLE_AMULET_FANCY = simpleItem("bauble_amulet_fancy");
    public static final Supplier<Item> BAUBLE_GIRDLE = simpleItem("bauble_girdle");
    public static final Supplier<Item> BAUBLE_GIRDLE_FANCY = simpleItem("bauble_girdle_fancy");
    public static final Supplier<Item> BAUBLE_RING = simpleItem("bauble_ring");
    public static final Supplier<Item> BAUBLE_RING_FANCY = simpleItem("bauble_ring_fancy");
    public static final Supplier<Item> BRASS_BRACE = simpleItem("brass_brace");
    public static final Supplier<Item> THAUMIUM_HELM = simpleItem("thaumium_helm");
    public static final Supplier<Item> THAUMIUM_CHEST = simpleItem("thaumium_chest");
    public static final Supplier<Item> THAUMIUM_LEGS = simpleItem("thaumium_legs");
    public static final Supplier<Item> THAUMIUM_BOOTS = simpleItem("thaumium_boots");
    public static final Supplier<Item> VOID_INGOT = simpleItem("void_ingot");
    public static final Supplier<Item> VOID_AXE = simpleItem("void_axe");
    public static final Supplier<Item> VOID_HOE = simpleItem("void_hoe");
    public static final Supplier<Item> VOID_PICK = simpleItem("void_pick");
    public static final Supplier<Item> VOID_SHOVEL = simpleItem("void_shovel");
    public static final Supplier<Item> VOID_SWORD = simpleItem("void_sword");
    public static final Supplier<Item> VOID_HELM = simpleItem("void_helm");
    public static final Supplier<Item> VOID_CHEST = simpleItem("void_chest");
    public static final Supplier<Item> VOID_LEGS = simpleItem("void_legs");
    public static final Supplier<Item> VOID_BOOTS = simpleItem("void_boots");

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
            case "brass_plate" -> legacyItem("plate", "brass", 0);
            case "iron_plate" -> legacyItem("plate", "iron", 1);
            case "thaumium_plate" -> legacyItem("plate", "thaumium", 2);
            case "void_plate" -> legacyItem("plate", "void", 3);
            case "rare_earth" -> legacyItem("nugget", "rare_earth", 10);
            case "curio_rites" -> legacyItem("curio", "rites", 6);
            case "salis_mundus" -> new ItemLegacyPlaceholder(
                    new Item.Properties(),
                    "tc.placeholder.salis_mundus"
            );
            case "alumentum" -> new ItemLegacyPlaceholder(
                    new Item.Properties(),
                    "tc.placeholder.alumentum"
            );
            case "thaumium_axe" -> new AxeItem(TCToolTiers.THAUMIUM, new Item.Properties().attributes(AxeItem.createAttributes(TCToolTiers.THAUMIUM, 5.0F, -3.0F)));
            case "thaumium_hoe" -> new HoeItem(TCToolTiers.THAUMIUM, new Item.Properties().attributes(HoeItem.createAttributes(TCToolTiers.THAUMIUM, -2.0F, -1.0F)));
            case "thaumium_pick" -> new PickaxeItem(TCToolTiers.THAUMIUM, new Item.Properties().attributes(PickaxeItem.createAttributes(TCToolTiers.THAUMIUM, 1.0F, -2.8F)));
            case "thaumium_shovel" -> new ShovelItem(TCToolTiers.THAUMIUM, new Item.Properties().attributes(ShovelItem.createAttributes(TCToolTiers.THAUMIUM, 1.5F, -3.0F)));
            case "thaumium_sword" -> new SwordItem(TCToolTiers.THAUMIUM, new Item.Properties().attributes(SwordItem.createAttributes(TCToolTiers.THAUMIUM, 3, -2.4F)));
            case "caster_basic" -> new ItemLegacyPlaceholder(
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON),
                    "tc.placeholder.caster_basic"
            );
            case "focus_1" -> new ItemLegacyPlaceholder(
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON),
                    "tc.placeholder.focus"
            );
            case "focus_2", "focus_3" -> new ItemLegacyPlaceholder(
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE),
                    "tc.placeholder.focus"
            );
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

    private TCItems() {
    }
}

