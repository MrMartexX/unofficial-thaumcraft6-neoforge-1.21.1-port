package thaumcraft.common.aspects;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;

public final class TCAspectReloadValidator {
    private static final TagKey<Item> ITEM_ORES_AMBER = commonItemTag("ores/amber");
    private static final TagKey<Item> ITEM_ORES_CINNABAR = commonItemTag("ores/cinnabar");
    private static final TagKey<Item> ITEM_ORES_QUARTZ = commonItemTag("ores/quartz");
    private static final TagKey<Item> ITEM_ORES_COPPER = commonItemTag("ores/copper");
    private static final TagKey<Item> ITEM_ORES_DIAMOND = commonItemTag("ores/diamond");
    private static final TagKey<Item> ITEM_ORES_EMERALD = commonItemTag("ores/emerald");
    private static final TagKey<Item> ITEM_ORES_GOLD = commonItemTag("ores/gold");
    private static final TagKey<Item> ITEM_ORES_IRON = commonItemTag("ores/iron");
    private static final TagKey<Item> ITEM_ORES_LAPIS = commonItemTag("ores/lapis");
    private static final TagKey<Item> ITEM_ORES_REDSTONE = commonItemTag("ores/redstone");
    private static final TagKey<Item> ITEM_GEMS_AMBER = commonItemTag("gems/amber");
    private static final TagKey<Item> ITEM_GEMS_DIAMOND = commonItemTag("gems/diamond");
    private static final TagKey<Item> ITEM_GEMS_EMERALD = commonItemTag("gems/emerald");
    private static final TagKey<Item> ITEM_GEMS_QUARTZ = commonItemTag("gems/quartz");
    private static final TagKey<Item> ITEM_INGOTS_COPPER = commonItemTag("ingots/copper");
    private static final TagKey<Item> ITEM_INGOTS_GOLD = commonItemTag("ingots/gold");
    private static final TagKey<Item> ITEM_INGOTS_IRON = commonItemTag("ingots/iron");
    private static final TagKey<Item> ITEM_RAW_MATERIALS_COPPER = commonItemTag("raw_materials/copper");
    private static final TagKey<Item> ITEM_RAW_MATERIALS_GOLD = commonItemTag("raw_materials/gold");
    private static final TagKey<Item> ITEM_RAW_MATERIALS_IRON = commonItemTag("raw_materials/iron");
    private static final TagKey<Item> ITEM_DUSTS_GLOWSTONE = commonItemTag("dusts/glowstone");
    private static final TagKey<Item> ITEM_DUSTS_REDSTONE = commonItemTag("dusts/redstone");

    private static final TagKey<Block> BLOCK_ORES_AMBER = commonBlockTag("ores/amber");
    private static final TagKey<Block> BLOCK_ORES_CINNABAR = commonBlockTag("ores/cinnabar");
    private static final TagKey<Block> BLOCK_ORES_QUARTZ = commonBlockTag("ores/quartz");
    private static final TagKey<Block> BLOCK_ORES_COPPER = commonBlockTag("ores/copper");
    private static final TagKey<Block> BLOCK_ORES_DIAMOND = commonBlockTag("ores/diamond");
    private static final TagKey<Block> BLOCK_ORES_EMERALD = commonBlockTag("ores/emerald");
    private static final TagKey<Block> BLOCK_ORES_GOLD = commonBlockTag("ores/gold");
    private static final TagKey<Block> BLOCK_ORES_IRON = commonBlockTag("ores/iron");
    private static final TagKey<Block> BLOCK_ORES_LAPIS = commonBlockTag("ores/lapis");
    private static final TagKey<Block> BLOCK_ORES_REDSTONE = commonBlockTag("ores/redstone");

    private static final TagKey<Item> ITEM_LEGACY_ORE_AMBER = legacyItemTag("ore_amber");
    private static final TagKey<Item> ITEM_LEGACY_ORE_CINNABAR = legacyItemTag("ore_cinnabar");
    private static final TagKey<Item> ITEM_LEGACY_ORE_QUARTZ = legacyItemTag("ore_quartz");
    private static final TagKey<Item> ITEM_LEGACY_ORE_CRYSTAL_AIR = legacyItemTag("ore_crystal_air");
    private static final TagKey<Item> ITEM_LEGACY_ORE_CRYSTAL_EARTH = legacyItemTag("ore_crystal_earth");
    private static final TagKey<Item> ITEM_LEGACY_ORE_CRYSTAL_WATER = legacyItemTag("ore_crystal_water");
    private static final TagKey<Item> ITEM_LEGACY_ORE_CRYSTAL_FIRE = legacyItemTag("ore_crystal_fire");
    private static final TagKey<Item> ITEM_LEGACY_ORE_CRYSTAL_ORDER = legacyItemTag("ore_crystal_order");
    private static final TagKey<Item> ITEM_LEGACY_ORE_CRYSTAL_ENTROPY = legacyItemTag("ore_crystal_entropy");
    private static final TagKey<Item> ITEM_LEGACY_ORE_CRYSTAL_TAINT = legacyItemTag("ore_crystal_taint");
    private static final TagKey<Item> ITEM_LEGACY_LOG_WOOD = legacyItemTag("log_wood");
    private static final TagKey<Item> ITEM_LEGACY_PLANK_WOOD = legacyItemTag("plank_wood");
    private static final TagKey<Item> ITEM_LEGACY_SLAB_WOOD = legacyItemTag("slab_wood");
    private static final TagKey<Item> ITEM_LEGACY_TREE_SAPLING = legacyItemTag("tree_sapling");
    private static final TagKey<Item> ITEM_LEGACY_TREE_LEAVES = legacyItemTag("tree_leaves");
    private static final TagKey<Item> ITEM_LEGACY_GEM_AMBER = legacyItemTag("gem_amber");
    private static final TagKey<Item> ITEM_LEGACY_QUICKSILVER = legacyItemTag("quicksilver");

    private static final TagKey<Block> BLOCK_LEGACY_ORE_AMBER = legacyBlockTag("ore_amber");
    private static final TagKey<Block> BLOCK_LEGACY_ORE_CINNABAR = legacyBlockTag("ore_cinnabar");
    private static final TagKey<Block> BLOCK_LEGACY_ORE_QUARTZ = legacyBlockTag("ore_quartz");
    private static final TagKey<Block> BLOCK_LEGACY_ORE_CRYSTAL_AIR = legacyBlockTag("ore_crystal_air");
    private static final TagKey<Block> BLOCK_LEGACY_ORE_CRYSTAL_EARTH = legacyBlockTag("ore_crystal_earth");
    private static final TagKey<Block> BLOCK_LEGACY_ORE_CRYSTAL_WATER = legacyBlockTag("ore_crystal_water");
    private static final TagKey<Block> BLOCK_LEGACY_ORE_CRYSTAL_FIRE = legacyBlockTag("ore_crystal_fire");
    private static final TagKey<Block> BLOCK_LEGACY_ORE_CRYSTAL_ORDER = legacyBlockTag("ore_crystal_order");
    private static final TagKey<Block> BLOCK_LEGACY_ORE_CRYSTAL_ENTROPY = legacyBlockTag("ore_crystal_entropy");
    private static final TagKey<Block> BLOCK_LEGACY_ORE_CRYSTAL_TAINT = legacyBlockTag("ore_crystal_taint");
    private static final TagKey<Block> BLOCK_LEGACY_LOG_WOOD = legacyBlockTag("log_wood");
    private static final TagKey<Block> BLOCK_LEGACY_PLANK_WOOD = legacyBlockTag("plank_wood");
    private static final TagKey<Block> BLOCK_LEGACY_SLAB_WOOD = legacyBlockTag("slab_wood");
    private static final TagKey<Block> BLOCK_LEGACY_TREE_SAPLING = legacyBlockTag("tree_sapling");
    private static final TagKey<Block> BLOCK_LEGACY_TREE_LEAVES = legacyBlockTag("tree_leaves");

    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.getUpdateCause() != TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD || !event.shouldUpdateStaticData()) {
            return;
        }

        validateLoadedTagsAndLookup(event.getRegistryAccess());
    }

    private static void validateLoadedTagsAndLookup(HolderLookup.Provider registries) {
        expectItemTag(TCItems.ORE_AMBER.get(), ITEM_ORES_AMBER, "thaumcraft:ore_amber");
        expectItemTag(TCItems.ORE_CINNABAR.get(), ITEM_ORES_CINNABAR, "thaumcraft:ore_cinnabar");
        expectItemTag(TCItems.ORE_QUARTZ.get(), ITEM_ORES_QUARTZ, "thaumcraft:ore_quartz");
        expectItemTag(TCItems.AMBER.get(), ITEM_GEMS_AMBER, "thaumcraft:amber");
        expectItemTag(Blocks.NETHER_QUARTZ_ORE.asItem(), ITEM_ORES_QUARTZ, "minecraft:nether_quartz_ore");
        expectItemTag(Blocks.COPPER_ORE.asItem(), ITEM_ORES_COPPER, "minecraft:copper_ore legacy oreCopper");
        expectItemTag(Blocks.DEEPSLATE_COPPER_ORE.asItem(), ITEM_ORES_COPPER, "minecraft:deepslate_copper_ore legacy oreCopper");
        expectItemTag(Items.COPPER_INGOT, ITEM_INGOTS_COPPER, "minecraft:copper_ingot legacy ingotCopper");
        expectItemTag(Blocks.DIAMOND_ORE.asItem(), ITEM_ORES_DIAMOND, "minecraft:diamond_ore legacy oreDiamond");
        expectItemTag(Blocks.DEEPSLATE_DIAMOND_ORE.asItem(), ITEM_ORES_DIAMOND, "minecraft:deepslate_diamond_ore legacy oreDiamond");
        expectItemTag(Blocks.EMERALD_ORE.asItem(), ITEM_ORES_EMERALD, "minecraft:emerald_ore legacy oreEmerald");
        expectItemTag(Blocks.GOLD_ORE.asItem(), ITEM_ORES_GOLD, "minecraft:gold_ore legacy oreGold");
        expectItemTag(Blocks.IRON_ORE.asItem(), ITEM_ORES_IRON, "minecraft:iron_ore legacy oreIron");
        expectItemTag(Blocks.LAPIS_ORE.asItem(), ITEM_ORES_LAPIS, "minecraft:lapis_ore legacy oreLapis");
        expectItemTag(Blocks.REDSTONE_ORE.asItem(), ITEM_ORES_REDSTONE, "minecraft:redstone_ore legacy oreRedstone");
        expectItemTag(Items.DIAMOND, ITEM_GEMS_DIAMOND, "minecraft:diamond legacy gemDiamond");
        expectItemTag(Items.EMERALD, ITEM_GEMS_EMERALD, "minecraft:emerald legacy gemEmerald");
        expectItemTag(Items.QUARTZ, ITEM_GEMS_QUARTZ, "minecraft:quartz legacy gemQuartz");
        expectItemTag(Items.GOLD_INGOT, ITEM_INGOTS_GOLD, "minecraft:gold_ingot legacy ingotGold");
        expectItemTag(Items.IRON_INGOT, ITEM_INGOTS_IRON, "minecraft:iron_ingot legacy ingotIron");
        expectItemTag(Items.RAW_COPPER, ITEM_RAW_MATERIALS_COPPER, "minecraft:raw_copper ore-derived 1.21 raw material");
        expectItemTag(Items.RAW_GOLD, ITEM_RAW_MATERIALS_GOLD, "minecraft:raw_gold ore-derived 1.21 raw material");
        expectItemTag(Items.RAW_IRON, ITEM_RAW_MATERIALS_IRON, "minecraft:raw_iron ore-derived 1.21 raw material");
        expectItemTag(Items.GLOWSTONE_DUST, ITEM_DUSTS_GLOWSTONE, "minecraft:glowstone_dust legacy dustGlowstone");
        expectItemTag(Items.REDSTONE, ITEM_DUSTS_REDSTONE, "minecraft:redstone legacy dustRedstone");

        expectItemTag(TCItems.ORE_AMBER.get(), ITEM_LEGACY_ORE_AMBER, "thaumcraft:ore_amber legacy oreAmber");
        expectItemTag(TCItems.ORE_CINNABAR.get(), ITEM_LEGACY_ORE_CINNABAR, "thaumcraft:ore_cinnabar legacy oreCinnabar");
        expectItemTag(TCItems.ORE_QUARTZ.get(), ITEM_LEGACY_ORE_QUARTZ, "thaumcraft:ore_quartz legacy oreQuartz");
        expectItemTag(TCItems.CRYSTAL_AER.get(), ITEM_LEGACY_ORE_CRYSTAL_AIR, "thaumcraft:crystal_aer legacy oreCrystalAir");
        expectItemTag(TCItems.CRYSTAL_TERRA.get(), ITEM_LEGACY_ORE_CRYSTAL_EARTH, "thaumcraft:crystal_terra legacy oreCrystalEarth");
        expectItemTag(TCItems.CRYSTAL_AQUA.get(), ITEM_LEGACY_ORE_CRYSTAL_WATER, "thaumcraft:crystal_aqua legacy oreCrystalWater");
        expectItemTag(TCItems.CRYSTAL_IGNIS.get(), ITEM_LEGACY_ORE_CRYSTAL_FIRE, "thaumcraft:crystal_ignis legacy oreCrystalFire");
        expectItemTag(TCItems.CRYSTAL_ORDO.get(), ITEM_LEGACY_ORE_CRYSTAL_ORDER, "thaumcraft:crystal_ordo legacy oreCrystalOrder");
        expectItemTag(TCItems.CRYSTAL_PERDITIO.get(), ITEM_LEGACY_ORE_CRYSTAL_ENTROPY, "thaumcraft:crystal_perditio legacy oreCrystalEntropy");
        expectItemTag(TCItems.CRYSTAL_VITIUM.get(), ITEM_LEGACY_ORE_CRYSTAL_TAINT, "thaumcraft:crystal_vitium legacy oreCrystalTaint");
        expectItemTag(TCItems.LOG_GREATWOOD.get(), ITEM_LEGACY_LOG_WOOD, "thaumcraft:log_greatwood legacy logWood");
        expectItemTag(TCItems.LOG_SILVERWOOD.get(), ITEM_LEGACY_LOG_WOOD, "thaumcraft:log_silverwood legacy logWood");
        expectItemTag(TCItems.PLANK_GREATWOOD.get(), ITEM_LEGACY_PLANK_WOOD, "thaumcraft:plank_greatwood legacy plankWood");
        expectItemTag(TCItems.PLANK_SILVERWOOD.get(), ITEM_LEGACY_PLANK_WOOD, "thaumcraft:plank_silverwood legacy plankWood");
        expectItemTag(TCItems.SLAB_GREATWOOD.get(), ITEM_LEGACY_SLAB_WOOD, "thaumcraft:slab_greatwood legacy slabWood");
        expectItemTag(TCItems.SLAB_SILVERWOOD.get(), ITEM_LEGACY_SLAB_WOOD, "thaumcraft:slab_silverwood legacy slabWood");
        expectItemTag(TCItems.SAPLING_GREATWOOD.get(), ITEM_LEGACY_TREE_SAPLING, "thaumcraft:sapling_greatwood legacy treeSapling");
        expectItemTag(TCItems.SAPLING_SILVERWOOD.get(), ITEM_LEGACY_TREE_SAPLING, "thaumcraft:sapling_silverwood legacy treeSapling");
        expectItemTag(TCItems.LEAVES_GREATWOOD.get(), ITEM_LEGACY_TREE_LEAVES, "thaumcraft:leaves_greatwood legacy treeLeaves");
        expectItemTag(TCItems.LEAVES_SILVERWOOD.get(), ITEM_LEGACY_TREE_LEAVES, "thaumcraft:leaves_silverwood legacy treeLeaves");
        expectItemTag(TCItems.AMBER.get(), ITEM_LEGACY_GEM_AMBER, "thaumcraft:amber legacy gemAmber");
        expectItemTag(TCItems.QUICKSILVER.get(), ITEM_LEGACY_QUICKSILVER, "thaumcraft:quicksilver legacy quicksilver");

        expectBlockTag(TCBlocks.ORE_AMBER.get(), BLOCK_ORES_AMBER, "thaumcraft:ore_amber");
        expectBlockTag(TCBlocks.ORE_CINNABAR.get(), BLOCK_ORES_CINNABAR, "thaumcraft:ore_cinnabar");
        expectBlockTag(TCBlocks.ORE_QUARTZ.get(), BLOCK_ORES_QUARTZ, "thaumcraft:ore_quartz");
        expectBlockTag(Blocks.NETHER_QUARTZ_ORE, BLOCK_ORES_QUARTZ, "minecraft:nether_quartz_ore");
        expectBlockTag(Blocks.COPPER_ORE, BLOCK_ORES_COPPER, "minecraft:copper_ore legacy oreCopper");
        expectBlockTag(Blocks.DEEPSLATE_COPPER_ORE, BLOCK_ORES_COPPER, "minecraft:deepslate_copper_ore legacy oreCopper");
        expectBlockTag(Blocks.DIAMOND_ORE, BLOCK_ORES_DIAMOND, "minecraft:diamond_ore legacy oreDiamond");
        expectBlockTag(Blocks.EMERALD_ORE, BLOCK_ORES_EMERALD, "minecraft:emerald_ore legacy oreEmerald");
        expectBlockTag(Blocks.GOLD_ORE, BLOCK_ORES_GOLD, "minecraft:gold_ore legacy oreGold");
        expectBlockTag(Blocks.IRON_ORE, BLOCK_ORES_IRON, "minecraft:iron_ore legacy oreIron");
        expectBlockTag(Blocks.LAPIS_ORE, BLOCK_ORES_LAPIS, "minecraft:lapis_ore legacy oreLapis");
        expectBlockTag(Blocks.REDSTONE_ORE, BLOCK_ORES_REDSTONE, "minecraft:redstone_ore legacy oreRedstone");

        expectBlockTag(TCBlocks.ORE_AMBER.get(), BLOCK_LEGACY_ORE_AMBER, "thaumcraft:ore_amber legacy oreAmber");
        expectBlockTag(TCBlocks.ORE_CINNABAR.get(), BLOCK_LEGACY_ORE_CINNABAR, "thaumcraft:ore_cinnabar legacy oreCinnabar");
        expectBlockTag(TCBlocks.ORE_QUARTZ.get(), BLOCK_LEGACY_ORE_QUARTZ, "thaumcraft:ore_quartz legacy oreQuartz");
        expectBlockTag(TCBlocks.CRYSTAL_AER.get(), BLOCK_LEGACY_ORE_CRYSTAL_AIR, "thaumcraft:crystal_aer legacy oreCrystalAir");
        expectBlockTag(TCBlocks.CRYSTAL_TERRA.get(), BLOCK_LEGACY_ORE_CRYSTAL_EARTH, "thaumcraft:crystal_terra legacy oreCrystalEarth");
        expectBlockTag(TCBlocks.CRYSTAL_AQUA.get(), BLOCK_LEGACY_ORE_CRYSTAL_WATER, "thaumcraft:crystal_aqua legacy oreCrystalWater");
        expectBlockTag(TCBlocks.CRYSTAL_IGNIS.get(), BLOCK_LEGACY_ORE_CRYSTAL_FIRE, "thaumcraft:crystal_ignis legacy oreCrystalFire");
        expectBlockTag(TCBlocks.CRYSTAL_ORDO.get(), BLOCK_LEGACY_ORE_CRYSTAL_ORDER, "thaumcraft:crystal_ordo legacy oreCrystalOrder");
        expectBlockTag(TCBlocks.CRYSTAL_PERDITIO.get(), BLOCK_LEGACY_ORE_CRYSTAL_ENTROPY, "thaumcraft:crystal_perditio legacy oreCrystalEntropy");
        expectBlockTag(TCBlocks.CRYSTAL_VITIUM.get(), BLOCK_LEGACY_ORE_CRYSTAL_TAINT, "thaumcraft:crystal_vitium legacy oreCrystalTaint");
        expectBlockTag(TCBlocks.LOG_GREATWOOD.get(), BLOCK_LEGACY_LOG_WOOD, "thaumcraft:log_greatwood legacy logWood");
        expectBlockTag(TCBlocks.LOG_SILVERWOOD.get(), BLOCK_LEGACY_LOG_WOOD, "thaumcraft:log_silverwood legacy logWood");
        expectBlockTag(TCBlocks.PLANK_GREATWOOD.get(), BLOCK_LEGACY_PLANK_WOOD, "thaumcraft:plank_greatwood legacy plankWood");
        expectBlockTag(TCBlocks.PLANK_SILVERWOOD.get(), BLOCK_LEGACY_PLANK_WOOD, "thaumcraft:plank_silverwood legacy plankWood");
        expectBlockTag(TCBlocks.SLAB_GREATWOOD.get(), BLOCK_LEGACY_SLAB_WOOD, "thaumcraft:slab_greatwood legacy slabWood");
        expectBlockTag(TCBlocks.SLAB_SILVERWOOD.get(), BLOCK_LEGACY_SLAB_WOOD, "thaumcraft:slab_silverwood legacy slabWood");
        expectBlockTag(TCBlocks.SAPLING_GREATWOOD.get(), BLOCK_LEGACY_TREE_SAPLING, "thaumcraft:sapling_greatwood legacy treeSapling");
        expectBlockTag(TCBlocks.SAPLING_SILVERWOOD.get(), BLOCK_LEGACY_TREE_SAPLING, "thaumcraft:sapling_silverwood legacy treeSapling");
        expectBlockTag(TCBlocks.LEAVES_GREATWOOD.get(), BLOCK_LEGACY_TREE_LEAVES, "thaumcraft:leaves_greatwood legacy treeLeaves");
        expectBlockTag(TCBlocks.LEAVES_SILVERWOOD.get(), BLOCK_LEGACY_TREE_LEAVES, "thaumcraft:leaves_silverwood legacy treeLeaves");

        expectAspects(new ItemStack(TCItems.ORE_AMBER.get()), "thaumcraft:ore_amber exact lookup",
                amount(Aspect.EARTH, 5), amount(Aspect.TRAP, 10), amount(Aspect.CRYSTAL, 10));
        expectAspects(new ItemStack(TCItems.QUICKSILVER.get()), "thaumcraft:quicksilver exact lookup",
                amount(Aspect.METAL, 10), amount(Aspect.DEATH, 5), amount(Aspect.ALCHEMY, 5));
        expectAspects(new ItemStack(Blocks.NETHER_QUARTZ_ORE), "minecraft:nether_quartz_ore tag fallback",
                amount(Aspect.EARTH, 5), amount(Aspect.CRYSTAL, 10));
        expectAspects(new ItemStack(Blocks.COAL_ORE), "minecraft:coal_ore exact legacy block lookup",
                amount(Aspect.EARTH, 5), amount(Aspect.ENERGY, 15), amount(Aspect.FIRE, 15));
        expectAspects(new ItemStack(Items.COAL), "minecraft:coal exact legacy lookup",
                amount(Aspect.ENERGY, 10), amount(Aspect.FIRE, 10));
        expectAspects(new ItemStack(Items.CHARCOAL), "minecraft:charcoal exact legacy wildcard coal lookup",
                amount(Aspect.ENERGY, 10), amount(Aspect.FIRE, 10));
        expectAspects(new ItemStack(Items.BUCKET), "minecraft:bucket exact legacy runtime value",
                amount(Aspect.VOID, 5), amount(Aspect.METAL, 33));
        expectAspects(new ItemStack(Items.WATER_BUCKET), "minecraft:water_bucket exact legacy runtime value",
                amount(Aspect.VOID, 5), amount(Aspect.METAL, 33), amount(Aspect.WATER, 20));
        expectAspects(new ItemStack(Items.LAVA_BUCKET), "minecraft:lava_bucket exact legacy runtime value",
                amount(Aspect.VOID, 5), amount(Aspect.METAL, 33), amount(Aspect.FIRE, 15), amount(Aspect.EARTH, 5));
        expectAspects(new ItemStack(Items.MILK_BUCKET), "minecraft:milk_bucket exact legacy runtime value",
                amount(Aspect.VOID, 5), amount(Aspect.METAL, 33), amount(Aspect.LIFE, 10), amount(Aspect.BEAST, 5), amount(Aspect.WATER, 5));
        expectAspects(new ItemStack(Items.LEATHER), "minecraft:leather exact legacy lookup",
                amount(Aspect.BEAST, 5), amount(Aspect.PROTECT, 5));
        expectAspects(new ItemStack(Items.COPPER_INGOT), "minecraft:copper_ingot legacy ingotCopper tag fallback",
                amount(Aspect.METAL, 10), amount(Aspect.EXCHANGE, 5));
        expectAspects(new ItemStack(Blocks.COPPER_ORE), "minecraft:copper_ore legacy oreCopper tag fallback",
                amount(Aspect.METAL, 10), amount(Aspect.EARTH, 5), amount(Aspect.EXCHANGE, 5));
        expectAspects(new ItemStack(Blocks.DEEPSLATE_COAL_ORE), "minecraft:deepslate_coal_ore legacy oreCoal tag fallback",
                amount(Aspect.EARTH, 5), amount(Aspect.ENERGY, 15), amount(Aspect.FIRE, 15));
        expectAspects(new ItemStack(Blocks.DIAMOND_ORE), "minecraft:diamond_ore legacy oreDiamond tag fallback",
                amount(Aspect.EARTH, 5), amount(Aspect.DESIRE, 15), amount(Aspect.CRYSTAL, 15));
        expectAspects(new ItemStack(Items.DIAMOND), "minecraft:diamond legacy gemDiamond tag fallback",
                amount(Aspect.CRYSTAL, 15), amount(Aspect.DESIRE, 15));
        expectAspects(new ItemStack(Items.IRON_INGOT), "minecraft:iron_ingot legacy ingotIron tag fallback",
                amount(Aspect.METAL, 15));
        expectAspects(new ItemStack(Items.RAW_COPPER), "minecraft:raw_copper ore-derived 1.21 raw material fallback",
                amount(Aspect.METAL, 10), amount(Aspect.EARTH, 5), amount(Aspect.EXCHANGE, 5));
        expectAspects(new ItemStack(Items.RAW_IRON), "minecraft:raw_iron ore-derived 1.21 raw material fallback",
                amount(Aspect.EARTH, 5), amount(Aspect.METAL, 15));
        expectAspects(new ItemStack(Items.REDSTONE), "minecraft:redstone legacy dustRedstone tag fallback",
                amount(Aspect.ENERGY, 10));
        expectAspects(new ItemStack(Items.GLOWSTONE_DUST), "minecraft:glowstone_dust legacy dustGlowstone tag fallback",
                amount(Aspect.SENSES, 5), amount(Aspect.LIGHT, 10));
        expectAspects(new ItemStack(Blocks.GLOWSTONE), "minecraft:glowstone legacy recursive dustGlowstone lookup",
                amount(Aspect.SENSES, 15), amount(Aspect.LIGHT, 30));
        expectAspects(new ItemStack(Blocks.STONE), "minecraft:stone legacy stone tag fallback",
                amount(Aspect.EARTH, 5));
        expectAspects(new ItemStack(Blocks.COBBLESTONE), "minecraft:cobblestone legacy cobblestone tag fallback",
                amount(Aspect.EARTH, 5), amount(Aspect.ENTROPY, 1));
        expectAspects(new ItemStack(Blocks.SAND), "minecraft:sand legacy sand tag fallback",
                amount(Aspect.EARTH, 5), amount(Aspect.ENTROPY, 5));
        expectAspects(new ItemStack(Blocks.GRAVEL), "minecraft:gravel legacy gravel tag fallback",
                amount(Aspect.EARTH, 5), amount(Aspect.ENTROPY, 2));
        expectAspects(new ItemStack(Blocks.OAK_LOG), "minecraft:oak_log legacy logWood tag fallback",
                amount(Aspect.PLANT, 20));
        expectAspects(new ItemStack(Blocks.OAK_SAPLING), "minecraft:oak_sapling legacy treeSapling tag fallback",
                amount(Aspect.PLANT, 15), amount(Aspect.LIFE, 5));
        expectAspects(new ItemStack(Blocks.OAK_LEAVES), "minecraft:oak_leaves legacy treeLeaves tag fallback",
                amount(Aspect.PLANT, 5));
        expectAspects(new ItemStack(Blocks.BLACK_WOOL), "minecraft:black_wool legacy runtime metadata color value",
                amount(Aspect.WATER, 1), amount(Aspect.BEAST, 12), amount(Aspect.SENSES, 3), amount(Aspect.CRAFT, 3));
        expectAspects(new ItemStack(Blocks.BLACK_CARPET), "minecraft:black_carpet legacy runtime metadata color value",
                amount(Aspect.BEAST, 6), amount(Aspect.SENSES, 1), amount(Aspect.CRAFT, 1));
        expectAspects(new ItemStack(Items.BLACK_BED), "minecraft:black_bed legacy runtime metadata color value",
                amount(Aspect.BEAST, 26), amount(Aspect.CRAFT, 8), amount(Aspect.PLANT, 4), amount(Aspect.WATER, 1), amount(Aspect.SENSES, 3));
        expectAspects(new ItemStack(Items.BLACK_BANNER), "minecraft:black_banner legacy runtime metadata color value",
                amount(Aspect.WATER, 4), amount(Aspect.BEAST, 54), amount(Aspect.SENSES, 13), amount(Aspect.CRAFT, 13));
        expectAspects(new ItemStack(Blocks.BLACK_CONCRETE), "minecraft:black_concrete legacy runtime metadata color value",
                amount(Aspect.EARTH, 3), amount(Aspect.ENTROPY, 2), amount(Aspect.WATER, 1), amount(Aspect.ORDER, 1));
        expectAspects(new ItemStack(Blocks.GRAY_CONCRETE_POWDER), "minecraft:gray_concrete_powder legacy runtime insertion order value",
                amount(Aspect.EARTH, 3), amount(Aspect.ENTROPY, 2));
        expectAspects(new ItemStack(Blocks.BLACK_GLAZED_TERRACOTTA), "minecraft:black_glazed_terracotta legacy runtime metadata color value",
                amount(Aspect.WATER, 15), amount(Aspect.EARTH, 15), amount(Aspect.FIRE, 2), amount(Aspect.SENSES, 2));
        expectAspects(new ItemStack(Blocks.BLACK_SHULKER_BOX), "minecraft:black_shulker_box legacy runtime metadata color value",
                amount(Aspect.PROTECT, 15), amount(Aspect.ELDRITCH, 7), amount(Aspect.BEAST, 7), amount(Aspect.VOID, 7), amount(Aspect.PLANT, 13));
        expectAspects(new ItemStack(Items.INK_SAC), "minecraft:ink_sac legacy dye metadata value",
                amount(Aspect.WATER, 2), amount(Aspect.BEAST, 2), amount(Aspect.SENSES, 5));
        expectAspects(new ItemStack(Items.BONE_MEAL), "minecraft:bone_meal legacy dye metadata value",
                amount(Aspect.LIFE, 2), amount(Aspect.DEATH, 1), amount(Aspect.PLANT, 1), amount(Aspect.SENSES, 5));
        expectAspects(new ItemStack(Blocks.OAK_PLANKS), "minecraft:oak_planks legacy plank generated value",
                amount(Aspect.PLANT, 3));
        expectAspects(new ItemStack(Items.COOKIE), "minecraft:cookie legacy complex generated value",
                amount(Aspect.DESIRE, 1), amount(Aspect.PLANT, 1), amount(Aspect.LIFE, 1));
        expectAspects(new ItemStack(Items.BOWL), "minecraft:bowl legacy complex generated value",
                amount(Aspect.VOID, 5), amount(Aspect.PLANT, 1));
        expectAspects(new ItemStack(Items.FLINT_AND_STEEL), "minecraft:flint_and_steel legacy complex generated value",
                amount(Aspect.FIRE, 10), amount(Aspect.TOOL, 8), amount(Aspect.METAL, 11), amount(Aspect.EARTH, 3));
        expectAspects(new ItemStack(Items.FISHING_ROD), "minecraft:fishing_rod legacy complex generated value",
                amount(Aspect.WATER, 10), amount(Aspect.TOOL, 5), amount(Aspect.PLANT, 2), amount(Aspect.BEAST, 7), amount(Aspect.CRAFT, 1));
        expectAspects(new ItemStack(Items.CARROT_ON_A_STICK), "minecraft:carrot_on_a_stick legacy complex generated value",
                amount(Aspect.MOTION, 5), amount(Aspect.DESIRE, 10), amount(Aspect.WATER, 7), amount(Aspect.TOOL, 3), amount(Aspect.PLANT, 5), amount(Aspect.BEAST, 5), amount(Aspect.SENSES, 3));
        expectAspects(new ItemStack(Blocks.CRAFTING_TABLE), "minecraft:crafting_table legacy complex generated value",
                amount(Aspect.CRAFT, 20), amount(Aspect.PLANT, 9));
        expectAspects(new ItemStack(Blocks.STONE_BUTTON), "minecraft:stone_button legacy complex generated value",
                amount(Aspect.MECHANISM, 5), amount(Aspect.EARTH, 3));
        expectAspects(new ItemStack(Items.MINECART), "minecraft:minecart legacy complex generated value",
                amount(Aspect.MOTION, 15), amount(Aspect.METAL, 56));
        expectAspects(new ItemStack(Blocks.RAIL), "minecraft:rail legacy complex generated value",
                amount(Aspect.MOTION, 10), amount(Aspect.METAL, 4));
        expectAspects(new ItemStack(Blocks.DETECTOR_RAIL), "minecraft:detector_rail legacy complex generated value",
                amount(Aspect.MECHANISM, 5), amount(Aspect.SENSES, 1), amount(Aspect.METAL, 11), amount(Aspect.EARTH, 1), amount(Aspect.ENERGY, 1));
        expectAspects(new ItemStack(Blocks.ACTIVATOR_RAIL), "minecraft:activator_rail legacy complex generated value",
                amount(Aspect.MECHANISM, 5), amount(Aspect.METAL, 11), amount(Aspect.ENERGY, 1));
        expectAspects(new ItemStack(Items.BLAZE_POWDER), "minecraft:blaze_powder legacy complex generated value",
                amount(Aspect.ALCHEMY, 5), amount(Aspect.FIRE, 5), amount(Aspect.ENERGY, 1));
        expectAspects(new ItemStack(Items.ENDER_EYE), "minecraft:ender_eye legacy complex generated value",
                amount(Aspect.SENSES, 10), amount(Aspect.MAGIC, 5), amount(Aspect.ELDRITCH, 7), amount(Aspect.MOTION, 11), amount(Aspect.ALCHEMY, 3), amount(Aspect.FIRE, 3));
        expectAspects(new ItemStack(Items.OAK_BOAT), "minecraft:oak_boat legacy complex generated value",
                amount(Aspect.WATER, 10), amount(Aspect.MOTION, 15), amount(Aspect.PLANT, 11));
        expectAspects(new ItemStack(Items.OAK_DOOR), "minecraft:oak_door legacy complex generated value",
                amount(Aspect.TRAP, 5), amount(Aspect.MECHANISM, 5), amount(Aspect.PLANT, 4));
        expectAspects(new ItemStack(Blocks.ACACIA_FENCE_GATE), "minecraft:acacia_fence_gate legacy complex generated value",
                amount(Aspect.TRAP, 5), amount(Aspect.MECHANISM, 5), amount(Aspect.PLANT, 7));
        expectAspects(new ItemStack(Items.IRON_DOOR), "minecraft:iron_door legacy complex generated value",
                amount(Aspect.TRAP, 5), amount(Aspect.MECHANISM, 5), amount(Aspect.METAL, 22));
        expectAspects(new ItemStack(Blocks.CHEST), "minecraft:chest legacy generated exact value",
                amount(Aspect.PLANT, 18));
        expectAspects(new ItemStack(Items.CHEST_MINECART), "minecraft:chest_minecart legacy generated value",
                amount(Aspect.PLANT, 13), amount(Aspect.MOTION, 11), amount(Aspect.METAL, 42));
        expectAspects(new ItemStack(Blocks.TRAPPED_CHEST), "minecraft:trapped_chest legacy generated exact value",
                amount(Aspect.TRAP, 10), amount(Aspect.PLANT, 14), amount(Aspect.METAL, 3));
        expectAspects(new ItemStack(Blocks.ENDER_CHEST), "minecraft:ender_chest legacy complex generated value",
                amount(Aspect.EXCHANGE, 10), amount(Aspect.MOTION, 18), amount(Aspect.VOID, 20), amount(Aspect.EARTH, 30), amount(Aspect.FIRE, 32), amount(Aspect.DARKNESS, 30), amount(Aspect.SENSES, 7));
        expectAspects(new ItemStack(Items.ARROW), "minecraft:arrow legacy generated exact value",
                amount(Aspect.AVERSION, 5), amount(Aspect.EARTH, 1), amount(Aspect.TOOL, 1), amount(Aspect.FLIGHT, 1), amount(Aspect.AIR, 1));
        expectAspects(new ItemStack(Items.SPECTRAL_ARROW), "minecraft:spectral_arrow legacy generated exact value",
                amount(Aspect.SENSES, 17), amount(Aspect.MAGIC, 5), amount(Aspect.LIGHT, 15), amount(Aspect.AVERSION, 1));
        expectAspects(new ItemStack(Items.DIAMOND_SWORD), "minecraft:diamond_sword legacy generated plus sword bonus",
                amount(Aspect.CRYSTAL, 22), amount(Aspect.DESIRE, 22), amount(Aspect.AVERSION, 16));
        expectAspects(new ItemStack(Items.GOLDEN_SWORD), "minecraft:golden_sword legacy generated plus sword bonus",
                amount(Aspect.METAL, 15), amount(Aspect.DESIRE, 15), amount(Aspect.AVERSION, 4));
        expectAspects(new ItemStack(Items.LEATHER_BOOTS), "minecraft:leather_boots legacy generated plus armor bonus",
                amount(Aspect.BEAST, 15), amount(Aspect.PROTECT, 15));
        expectAspects(new ItemStack(Items.ELYTRA), "minecraft:elytra undamaged legacy exact value",
                amount(Aspect.FLIGHT, 20), amount(Aspect.MOTION, 15));
        ItemStack damagedElytra = new ItemStack(Items.ELYTRA);
        damagedElytra.setDamageValue(1);
        expectNoAspects(damagedElytra, "minecraft:elytra damaged legacy no-aspect value");
        expectAspects(new ItemStack(Blocks.BLUE_ORCHID), "minecraft:blue_orchid legacy flower tag fallback",
                amount(Aspect.PLANT, 5), amount(Aspect.LIFE, 1), amount(Aspect.SENSES, 5));
        expectAspects(new ItemStack(Blocks.SOUL_SOIL), "minecraft:soul_soil soul fire base fallback",
                amount(Aspect.EARTH, 3), amount(Aspect.TRAP, 1), amount(Aspect.SOUL, 3));
        expectAspects(new ItemStack(Blocks.WHITE_TERRACOTTA), "minecraft:white_terracotta legacy runtime metadata color value",
                amount(Aspect.WATER, 15), amount(Aspect.EARTH, 15), amount(Aspect.FIRE, 1), amount(Aspect.SENSES, 1));
        expectAspects(new ItemStack(Blocks.WHITE_STAINED_GLASS), "minecraft:white_stained_glass legacy blockGlass tag fallback",
                amount(Aspect.CRYSTAL, 5));
        expectAspects(new ItemStack(Blocks.MELON), "minecraft:melon legacy melon_block exact value",
                amount(Aspect.PLANT, 10));
        expectAspects(new ItemStack(Blocks.CHIPPED_ANVIL), "minecraft:chipped_anvil legacy anvil tag fallback",
                amount(Aspect.METAL, 33));
        expectAspects(new ItemStack(Blocks.TUFF_STAIRS), "minecraft:tuff_stairs generated from c:stones tuff",
                amount(Aspect.EARTH, 5));
        expectAspects(new ItemStack(Blocks.TUBE_CORAL), "minecraft:tube_coral modern coral policy",
                amount(Aspect.PLANT, 5), amount(Aspect.WATER, 5));
        expectAspects(new ItemStack(Blocks.TUBE_CORAL_BLOCK), "minecraft:tube_coral_block modern coral block policy",
                amount(Aspect.PLANT, 10), amount(Aspect.WATER, 10));
        expectAspects(new ItemStack(Blocks.DEAD_TUBE_CORAL), "minecraft:dead_tube_coral modern dead coral policy",
                amount(Aspect.EARTH, 2), amount(Aspect.WATER, 2), amount(Aspect.DEATH, 3));
        expectAspects(new ItemStack(Blocks.DEAD_TUBE_CORAL_BLOCK), "minecraft:dead_tube_coral_block double dead coral policy",
                amount(Aspect.EARTH, 4), amount(Aspect.WATER, 4), amount(Aspect.DEATH, 6));
        expectAspects(new ItemStack(Blocks.DRIPSTONE_BLOCK), "minecraft:dripstone_block mineral crystal policy",
                amount(Aspect.EARTH, 5), amount(Aspect.CRYSTAL, 1));
        expectAspects(new ItemStack(Items.POINTED_DRIPSTONE), "minecraft:pointed_dripstone mineral crystal policy",
                amount(Aspect.EARTH, 5), amount(Aspect.CRYSTAL, 1));
        expectAspects(new ItemStack(Blocks.OCHRE_FROGLIGHT), "minecraft:ochre_froglight two-aspect light policy",
                amount(Aspect.LIGHT, 10), amount(Aspect.BEAST, 5));
        expectAspects(new ItemStack(Items.CHERRY_BOAT), "minecraft:cherry_boat boat role policy",
                amount(Aspect.WATER, 10), amount(Aspect.MOTION, 15), amount(Aspect.PLANT, 11));
        expectAspects(new ItemStack(Items.MANGROVE_CHEST_BOAT), "minecraft:mangrove_chest_boat boat role policy",
                amount(Aspect.PLANT, 21), amount(Aspect.WATER, 7), amount(Aspect.MOTION, 11));
        expectAspects(new ItemStack(Items.BAMBOO_RAFT), "minecraft:bamboo_raft raft role policy",
                amount(Aspect.WATER, 10), amount(Aspect.MOTION, 15), amount(Aspect.PLANT, 26));
        expectAspects(new ItemStack(Items.SWEET_BERRIES), "minecraft:sweet_berries plant food desire policy",
                amount(Aspect.PLANT, 5), amount(Aspect.LIFE, 5), amount(Aspect.DESIRE, 1));
        expectAspects(new ItemStack(Items.STICK), "minecraft:stick generated from vanilla planks",
                amount(Aspect.PLANT, 1));
        expectAspects(new ItemStack(Items.FIRE_CHARGE), "minecraft:fire_charge legacy generated value",
                amount(Aspect.FIRE, 6), amount(Aspect.ENTROPY, 2), amount(Aspect.ALCHEMY, 2), amount(Aspect.ENERGY, 2));
        expectAspects(new ItemStack(Items.END_CRYSTAL), "minecraft:end_crystal legacy generated value",
                amount(Aspect.CRYSTAL, 26), amount(Aspect.SENSES, 7), amount(Aspect.ELDRITCH, 5), amount(Aspect.MOTION, 8), amount(Aspect.ALCHEMY, 9), amount(Aspect.UNDEAD, 3), amount(Aspect.SOUL, 7));
        expectNoAspects(new ItemStack(Items.ZOMBIE_SPAWN_EGG), "minecraft:zombie_spawn_egg legacy no-aspect spawn egg");
        expectNoAspects(new ItemStack(Items.FIREWORK_ROCKET), "minecraft:firework_rocket legacy no-aspect firework");
        expectNoAspects(new ItemStack(Items.FIREWORK_STAR), "minecraft:firework_star legacy no-aspect firework");
        expectNoAspects(new ItemStack(Blocks.INFESTED_STONE), "minecraft:infested_stone legacy no-aspect monster_egg");
        expectAspects(new ItemStack(Items.POTION), "minecraft:potion base empty legacy lookup",
                amount(Aspect.MAGIC, 5), amount(Aspect.ALCHEMY, 5), amount(Aspect.WATER, 5));
        expectAspects(PotionContents.createItemStack(Items.POTION, Potions.WATER), "minecraft:potion water component legacy potion lookup",
                amount(Aspect.WATER, 10));
        expectAspects(PotionContents.createItemStack(Items.SPLASH_POTION, Potions.WATER), "minecraft:splash_potion water component legacy potion lookup",
                amount(Aspect.WATER, 5), amount(Aspect.ENERGY, 5));
        expectAspects(PotionContents.createItemStack(Items.TIPPED_ARROW, Potions.WATER), "minecraft:tipped_arrow water component legacy potion lookup",
                amount(Aspect.WATER, 5), amount(Aspect.AVERSION, 5));
        expectAspects(PotionContents.createItemStack(Items.LINGERING_POTION, Potions.WATER), "minecraft:lingering_potion water component legacy potion lookup",
                amount(Aspect.WATER, 5), amount(Aspect.TRAP, 5));
        expectAspects(new ItemStack(Items.ENCHANTED_BOOK), "minecraft:enchanted_book base legacy book lookup",
                amount(Aspect.MIND, 4), amount(Aspect.PLANT, 6), amount(Aspect.WATER, 4), amount(Aspect.AIR, 2), amount(Aspect.BEAST, 3), amount(Aspect.PROTECT, 3));
        expectAspects(enchantedBook(registries, Enchantments.SHARPNESS, 1), "minecraft:enchanted_book stored sharpness I legacy bonus lookup",
                amount(Aspect.MIND, 4), amount(Aspect.PLANT, 6), amount(Aspect.WATER, 4), amount(Aspect.BEAST, 3), amount(Aspect.PROTECT, 3), amount(Aspect.AVERSION, 3), amount(Aspect.MAGIC, 3));
        expectAspects(enchantedBook(registries, Enchantments.SWEEPING_EDGE, 3), "minecraft:enchanted_book stored sweeping edge III legacy bonus lookup",
                amount(Aspect.MIND, 4), amount(Aspect.PLANT, 6), amount(Aspect.WATER, 4), amount(Aspect.AIR, 2), amount(Aspect.BEAST, 3), amount(Aspect.PROTECT, 3), amount(Aspect.MAGIC, 13));
        expectAspects(enchantedBook(registries, Enchantments.BINDING_CURSE, 1), "minecraft:enchanted_book stored binding curse I legacy bonus lookup",
                amount(Aspect.MIND, 4), amount(Aspect.PLANT, 6), amount(Aspect.WATER, 4), amount(Aspect.AIR, 2), amount(Aspect.BEAST, 3), amount(Aspect.PROTECT, 3), amount(Aspect.MAGIC, 9));
        validateGeneratedCraftingAspects();
        validateCraftingFormulaFixtures();
        validateGeneratedFallbackPriority();
        logMinecraftItemCoverage();

        Thaumcraft.LOGGER.info("Thaumcraft aspect tag reload validation passed.");
    }

    private static void logMinecraftItemCoverage() {
        int total = 0;
        int covered = 0;
        List<ResourceLocation> missing = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (!"minecraft".equals(id.getNamespace()) || item == Items.AIR) {
                continue;
            }

            ItemStack stack = new ItemStack(item);
            if (TCAspectStackRules.isLegacyNoAspectStack(stack) || TCAspectStackRules.isComponentSensitiveWithoutLegacyBase(stack)) {
                continue;
            }

            total++;
            AspectList aspects = TCAspectAssignments.getObjectAspects(stack);
            if (aspects != null && aspects.size() > 0) {
                covered++;
            } else {
                missing.add(id);
            }
        }

        Thaumcraft.LOGGER.info("Thaumcraft aspect coverage audit: {} of {} minecraft item ids have non-empty aspects; {} empty.", covered, total, missing.size());
        if (!missing.isEmpty()) {
            Thaumcraft.LOGGER.info("Thaumcraft aspect coverage audit empty minecraft item ids: {}", missing);
        }
    }

    private static void validateGeneratedCraftingAspects() {
        expectAspects(new ItemStack(TCItems.PLANK_GREATWOOD.get()), "thaumcraft:plank_greatwood generated crafting lookup",
                amount(Aspect.PLANT, 3), amount(Aspect.LIFE, 1));
        expectAspects(new ItemStack(TCItems.PLANK_SILVERWOOD.get()), "thaumcraft:plank_silverwood generated crafting lookup",
                amount(Aspect.PLANT, 3), amount(Aspect.AURA, 1));
        expectAspects(new ItemStack(TCItems.SLAB_GREATWOOD.get()), "thaumcraft:slab_greatwood generated crafting lookup",
                amount(Aspect.PLANT, 1));
        expectAspects(new ItemStack(TCItems.SLAB_SILVERWOOD.get()), "thaumcraft:slab_silverwood generated crafting lookup",
                amount(Aspect.PLANT, 1));
        expectAspects(new ItemStack(TCItems.STAIRS_GREATWOOD.get()), "thaumcraft:stairs_greatwood generated crafting lookup",
                amount(Aspect.PLANT, 3), amount(Aspect.LIFE, 1));
        expectAspects(new ItemStack(TCItems.STAIRS_SILVERWOOD.get()), "thaumcraft:stairs_silverwood generated crafting lookup",
                amount(Aspect.PLANT, 3), amount(Aspect.AURA, 1));
        expectAspects(new ItemStack(TCItems.AMBER_BLOCK.get()), "thaumcraft:amber_block generated crafting lookup",
                amount(Aspect.TRAP, 30), amount(Aspect.CRYSTAL, 30));
        expectNoAspects(new ItemStack(TCItems.AMBER_BRICK.get()), "thaumcraft:amber_brick legacy empty runtime lookup");
        expectAspects(new ItemStack(TCItems.FABRIC.get()), "thaumcraft:fabric legacy runtime lookup",
                amount(Aspect.BEAST, 26), amount(Aspect.CRAFT, 6), amount(Aspect.MAGIC, 1));
        expectAspects(new ItemStack(TCItems.GOGGLES.get()), "thaumcraft:goggles legacy runtime lookup",
                amount(Aspect.SENSES, 25), amount(Aspect.AURA, 25), amount(Aspect.BEAST, 15), amount(Aspect.PROTECT, 15), amount(Aspect.METAL, 60), amount(Aspect.DESIRE, 45), amount(Aspect.MAGIC, 9));
        expectAspects(new ItemStack(TCItems.FILTER.get()), "thaumcraft:filter arcane generated lookup",
                amount(Aspect.METAL, 7), amount(Aspect.DESIRE, 7), amount(Aspect.PLANT, 1), amount(Aspect.MAGIC, 1));
        expectAspects(new ItemStack(TCItems.MORPHIC_RESONATOR.get()), "thaumcraft:morphic_resonator arcane generated lookup",
                amount(Aspect.CRYSTAL, 1), amount(Aspect.METAL, 14), amount(Aspect.TOOL, 4), amount(Aspect.EARTH, 3), amount(Aspect.ORDER, 3), amount(Aspect.MAGIC, 5));
        expectAspects(new ItemStack(TCItems.STONE_ARCANE.get()), "thaumcraft:stone_arcane legacy runtime lookup",
                amount(Aspect.EARTH, 3));
        expectAspects(new ItemStack(TCItems.STONE_ARCANE_BRICK.get()), "thaumcraft:stone_arcane_brick legacy runtime lookup",
                amount(Aspect.EARTH, 2));
        expectAspects(new ItemStack(TCItems.SLAB_ARCANE_STONE.get()), "thaumcraft:slab_arcane_stone legacy runtime lookup",
                amount(Aspect.EARTH, 1));
        expectAspects(new ItemStack(TCItems.SLAB_ANCIENT.get()), "thaumcraft:slab_ancient legacy runtime lookup",
                amount(Aspect.EARTH, 1), amount(Aspect.ELDRITCH, 1));
        expectAspects(new ItemStack(TCItems.SLAB_ELDRITCH.get()), "thaumcraft:slab_eldritch legacy runtime lookup",
                amount(Aspect.EARTH, 1), amount(Aspect.ELDRITCH, 1));
        expectAspects(new ItemStack(TCItems.STAIRS_ARCANE.get()), "thaumcraft:stairs_arcane legacy runtime lookup",
                amount(Aspect.EARTH, 3));
        expectAspects(new ItemStack(TCItems.STAIRS_ARCANE_BRICK.get()), "thaumcraft:stairs_arcane_brick legacy runtime lookup",
                amount(Aspect.EARTH, 2));
        expectAspects(new ItemStack(TCItems.STAIRS_ANCIENT.get()), "thaumcraft:stairs_ancient legacy runtime lookup",
                amount(Aspect.EARTH, 5), amount(Aspect.ELDRITCH, 5));
        expectGeneratedAspects(new ItemStack(TCItems.PLANK_GREATWOOD.get()), "thaumcraft:plank_greatwood generateTags cache lookup",
                amount(Aspect.PLANT, 3), amount(Aspect.LIFE, 1));
        expectGeneratedAspects(new ItemStack(TCItems.FILTER.get()), "thaumcraft:filter arcane generateTags cache lookup",
                amount(Aspect.METAL, 7), amount(Aspect.DESIRE, 7), amount(Aspect.PLANT, 1), amount(Aspect.MAGIC, 1));
        expectGeneratedAspects(new ItemStack(TCItems.MORPHIC_RESONATOR.get()), "thaumcraft:morphic_resonator arcane generateTags cache lookup",
                amount(Aspect.CRYSTAL, 1), amount(Aspect.METAL, 14), amount(Aspect.TOOL, 4), amount(Aspect.EARTH, 3), amount(Aspect.ORDER, 3), amount(Aspect.MAGIC, 5));
        expectGeneratedAspects(new ItemStack(TCItems.FOCUS_1.get()), "thaumcraft:focus_1 crucible generateTags cache lookup",
                amount(Aspect.ORDER, 15), amount(Aspect.CRYSTAL, 14), amount(Aspect.MAGIC, 3), amount(Aspect.AURA, 2));
        expectGeneratedAspects(new ItemStack(TCItems.FOCUS_2.get()), "thaumcraft:focus_2 infusion generateTags cache lookup",
                amount(Aspect.ORDER, 18), amount(Aspect.CRYSTAL, 21), amount(Aspect.MAGIC, 7), amount(Aspect.AURA, 1), amount(Aspect.METAL, 15),
                amount(Aspect.DEATH, 7), amount(Aspect.ALCHEMY, 7), amount(Aspect.DESIRE, 11), amount(Aspect.ELDRITCH, 7), amount(Aspect.MOTION, 11));
        expectGeneratedAspects(new ItemStack(TCItems.FOCUS_3.get()), "thaumcraft:focus_3 infusion generateTags cache lookup",
                amount(Aspect.ORDER, 35), amount(Aspect.CRYSTAL, 15), amount(Aspect.METAL, 26), amount(Aspect.ALCHEMY, 12), amount(Aspect.DESIRE, 8),
                amount(Aspect.ELDRITCH, 12), amount(Aspect.MOTION, 8), amount(Aspect.DEATH, 7), amount(Aspect.MAGIC, 20), amount(Aspect.AURA, 7),
                amount(Aspect.VOID, 10));
        expectGeneratedAspects(new ItemStack(TCItems.CLUSTER_IRON.get()), "thaumcraft:cluster_iron crucible generateTags cache lookup",
                amount(Aspect.EARTH, 5), amount(Aspect.METAL, 17), amount(Aspect.ORDER, 2));
    }

    private static void validateCraftingFormulaFixtures() {
        ShapelessRecipe shapeless = new ShapelessRecipe(
                "",
                CraftingBookCategory.MISC,
                new ItemStack(Items.PAPER),
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(TCItems.AMBER.get()), Ingredient.of(TCItems.QUICKSILVER.get())));
        expectAspectList(
                TCGeneratedAspectRecipeGenerator.calculateCraftingRecipeAspectsForValidation(shapeless, new ItemStack(Items.PAPER), null),
                "validation shapeless recipe formula",
                amount(Aspect.TRAP, 7), amount(Aspect.CRYSTAL, 7), amount(Aspect.METAL, 7), amount(Aspect.DEATH, 3), amount(Aspect.ALCHEMY, 3));

        ShapelessRecipe remainingItem = new ShapelessRecipe(
                "",
                CraftingBookCategory.MISC,
                new ItemStack(Items.PAPER),
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.MILK_BUCKET)));
        expectAspectList(
                TCGeneratedAspectRecipeGenerator.calculateCraftingRecipeAspectsForValidation(remainingItem, new ItemStack(Items.PAPER), null),
                "validation remaining-item subtraction formula",
                amount(Aspect.LIFE, 7), amount(Aspect.BEAST, 3), amount(Aspect.WATER, 3));
    }

    private static void validateGeneratedFallbackPriority() {
        Map<TCAspectStackKey, AspectList> previous = TCGeneratedAspectCache.snapshot();
        ItemStack generatedFallbackStack = findGeneratedFallbackValidationStack();
        if (generatedFallbackStack.isEmpty()) {
            Thaumcraft.LOGGER.warn("Skipping generated aspect fallback validation because every registered non-air item already has explicit aspects.");
            return;
        }

        TCAspectStackKey generatedFallbackKey = TCAspectStackKey.from(generatedFallbackStack);
        TCGeneratedAspectCache.replaceForValidation(Map.of(
                TCAspectStackKey.from(new ItemStack(TCItems.ORE_AMBER.get())), new AspectList().add(Aspect.AIR, 99),
                TCAspectStackKey.from(new ItemStack(Blocks.NETHER_QUARTZ_ORE)), new AspectList().add(Aspect.FIRE, 99),
                generatedFallbackKey, new AspectList().add(Aspect.WATER, 2)));
        try {
            expectAspects(new ItemStack(TCItems.ORE_AMBER.get()), "thaumcraft:ore_amber exact lookup wins over generated cache",
                    amount(Aspect.EARTH, 5), amount(Aspect.TRAP, 10), amount(Aspect.CRYSTAL, 10));
            expectAspects(new ItemStack(Blocks.NETHER_QUARTZ_ORE), "minecraft:nether_quartz_ore tag lookup wins over generated cache",
                    amount(Aspect.EARTH, 5), amount(Aspect.CRYSTAL, 10));
            expectAspects(generatedFallbackStack, "generated cache fallback for explicit-unassigned stack",
                    amount(Aspect.WATER, 2));
        } finally {
            TCGeneratedAspectCache.replaceForValidation(previous);
        }
    }

    private static ItemStack findGeneratedFallbackValidationStack() {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item == Items.AIR) {
                continue;
            }

            ItemStack stack = new ItemStack(item);
            if (TCAspectAssignments.getExplicitObjectAspects(stack) == null) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack enchantedBook(HolderLookup.Provider registries, net.minecraft.resources.ResourceKey<Enchantment> key, int level) {
        Holder<Enchantment> enchantment = registries.lookupOrThrow(Registries.ENCHANTMENT)
                .get(key)
                .orElseThrow(() -> new IllegalStateException("Missing enchantment " + key.location()));
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(enchantment, level);
        stack.set(DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable());
        return stack;
    }

    private static void expectItemTag(Item item, TagKey<Item> tag, String label) {
        if (!new ItemStack(item).is(tag)) {
            throw new IllegalStateException("Aspect tag reload validation failed: item " + label + " is missing tag #" + tag.location());
        }
    }

    private static void expectBlockTag(Block block, TagKey<Block> tag, String label) {
        if (!block.defaultBlockState().is(tag)) {
            throw new IllegalStateException("Aspect tag reload validation failed: block " + label + " is missing tag #" + tag.location());
        }
    }

    private static void expectAspects(ItemStack stack, String label, Amount... expected) {
        AspectList actual = TCAspectAssignments.getObjectAspects(stack);
        expectAspectList(actual, label, expected);
    }

    private static void expectAspectList(AspectList actual, String label, Amount... expected) {
        if (actual == null) {
            throw new IllegalStateException("Aspect tag reload validation failed: " + label + " returned null aspects");
        }
        Aspect[] actualAspects = actual.getAspects();
        if (actualAspects.length != expected.length) {
            throw new IllegalStateException("Aspect tag reload validation failed: " + label + " expected " + expected.length + " aspects, got " + actualAspects.length);
        }
        for (int i = 0; i < expected.length; i++) {
            if (actualAspects[i] != expected[i].aspect()) {
                throw new IllegalStateException("Aspect tag reload validation failed: " + label + " aspect order mismatch at index " + i + "; actual " + formatAspects(actual));
            }
            if (actual.getAmount(expected[i].aspect()) != expected[i].amount()) {
                throw new IllegalStateException("Aspect tag reload validation failed: " + label + " expected " + expected[i].amount() + " " + expected[i].aspect().getTag()
                        + ", got " + actual.getAmount(expected[i].aspect()) + "; actual " + formatAspects(actual));
            }
        }
    }

    private static void expectNoAspects(ItemStack stack, String label) {
        AspectList actual = TCAspectAssignments.getObjectAspects(stack);
        if (actual == null || actual.size() != 0) {
            throw new IllegalStateException("Aspect tag reload validation failed: " + label + " unexpectedly returned aspects");
        }
    }

    private static void expectGeneratedAspects(ItemStack stack, String label, Amount... expected) {
        AspectList actual = AspectHelper.generateTags(stack);
        expectAspectList(actual, label, expected);
    }

    private static TagKey<Item> commonItemTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    private static TagKey<Block> commonBlockTag(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    private static TagKey<Item> legacyItemTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "legacy_ore_dictionary/" + path));
    }

    private static TagKey<Block> legacyBlockTag(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "legacy_ore_dictionary/" + path));
    }

    private static Amount amount(Aspect aspect, int amount) {
        return new Amount(aspect, amount);
    }

    private static String formatAspects(AspectList aspects) {
        if (aspects == null) {
            return "<null>";
        }
        List<String> entries = new ArrayList<>();
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect != null) {
                entries.add(aspect.getTag() + "=" + aspects.getAmount(aspect));
            }
        }
        return entries.toString();
    }

    private record Amount(Aspect aspect, int amount) {
    }

    private TCAspectReloadValidator() {
    }
}
