package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.ItemAspectVariant;
import thaumcraft.common.items.ItemLegacyPlaceholder;
import thaumcraft.common.items.consumables.ItemZombieBrain;
import thaumcraft.common.items.tools.ItemScribingTools;
import thaumcraft.common.items.tools.ItemThaumometer;

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
    public static final Supplier<BlockItem> NITOR_YELLOW = blockItem("nitor_yellow", TCBlocks.NITOR_YELLOW);
    public static final Supplier<BlockItem> ARCANE_WORKBENCH = blockItem("arcane_workbench", TCBlocks.ARCANE_WORKBENCH);
    public static final Supplier<BlockItem> RESEARCH_TABLE = blockItem("research_table", TCBlocks.RESEARCH_TABLE);
    public static final Supplier<BlockItem> CRUCIBLE = blockItem("crucible", TCBlocks.CRUCIBLE);
    public static final Supplier<BlockItem> SMELTER_BASIC = blockItem("smelter_basic", TCBlocks.SMELTER_BASIC);
    public static final Supplier<BlockItem> WAND_WORKBENCH = blockItem("wand_workbench", TCBlocks.WAND_WORKBENCH);
    public static final Supplier<BlockItem> INFUSION_MATRIX = blockItem("infusion_matrix", TCBlocks.INFUSION_MATRIX);
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

    public static final Supplier<Item> THAUMOMETER = ITEMS.register("thaumometer", ItemThaumometer::new);
    public static final Supplier<Item> GOGGLES = ITEMS.register("goggles", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> AMBER = ITEMS.register("amber", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> QUICKSILVER = ITEMS.register("quicksilver", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> FABRIC = ITEMS.register("fabric", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> THAUMIUM_INGOT = simpleItem("thaumium_ingot");
    public static final Supplier<Item> BRASS_INGOT = simpleItem("brass_ingot");
    public static final Supplier<Item> THAUMIUM_PLATE = simpleItem("thaumium_plate");
    public static final Supplier<Item> VOID_PLATE = simpleItem("void_plate");
    public static final Supplier<Item> RARE_EARTH = simpleItem("rare_earth");
    public static final Supplier<Item> TALLOW = simpleItem("tallow");
    public static final Supplier<Item> VIS_RESONATOR = simpleItem("vis_resonator");
    public static final Supplier<Item> MIRRORED_GLASS = simpleItem("mirrored_glass");
    public static final Supplier<Item> BRAIN = simpleItem("brain");
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
            case "enchanted_placeholder_protection_1",
                 "enchanted_placeholder_sharpness_1",
                 "enchanted_placeholder_silk_touch_1",
                 "enchanted_placeholder_fortune_1" -> new ItemLegacyPlaceholder(
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE),
                    "tc.placeholder.enchanted",
                    true
            );
            default -> new Item(new Item.Properties());
        };
    }

    private static Supplier<BlockItem> blockItem(String id, Supplier<? extends net.minecraft.world.level.block.Block> block) {
        return ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private TCItems() {
    }
}
