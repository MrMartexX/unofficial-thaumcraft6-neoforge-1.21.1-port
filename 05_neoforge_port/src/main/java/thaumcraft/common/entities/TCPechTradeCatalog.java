package thaumcraft.common.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.TCAspectVariantStacks;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;

public final class TCPechTradeCatalog {
    private static final EnumMap<TCPechEntity.PechType, List<TradeEntry>> ENTRIES = createEntries();

    private TCPechTradeCatalog() {
    }

    public static List<TradeEntry> entries(TCPechEntity.PechType type) {
        return ENTRIES.getOrDefault(type, List.of());
    }

    public static ItemStack randomStack(TCPechEntity.PechType type, int tier, RandomSource random) {
        List<TradeEntry> matches = entries(type).stream()
                .filter(entry -> entry.tier() == tier)
                .toList();
        if (matches.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return matches.get(random.nextInt(matches.size())).stack().copy();
    }

    public static int entryCountForValidation(TCPechEntity.PechType type) {
        return entries(type).size();
    }

    public static boolean hasTierForValidation(TCPechEntity.PechType type, int tier) {
        return entries(type).stream().anyMatch(entry -> entry.tier() == tier);
    }

    private static EnumMap<TCPechEntity.PechType, List<TradeEntry>> createEntries() {
        EnumMap<TCPechEntity.PechType, List<TradeEntry>> map = new EnumMap<>(TCPechEntity.PechType.class);
        ArrayList<TradeEntry> forager = new ArrayList<>();
        forager.add(entry(1, TCItems.CLUSTER_IRON.get()));
        forager.add(entry(1, TCItems.CLUSTER_GOLD.get()));
        forager.add(entry(1, TCItems.CLUSTER_CINNABAR.get()));
        forager.add(entry(1, TCItems.QUARTZ_NUGGET.get()));
        forager.add(entry(1, TCItems.CLUSTER_COPPER.get()));
        forager.add(entry(1, TCItems.CLUSTER_TIN.get()));
        forager.add(entry(1, TCItems.CLUSTER_SILVER.get()));
        forager.add(entry(1, TCItems.CLUSTER_LEAD.get()));
        forager.add(entry(2, Items.BLAZE_ROD));
        forager.add(entry(2, TCBlocks.SAPLING_GREATWOOD.get()));
        forager.add(entry(2, Items.DRAGON_BREATH));
        forager.add(entry(2, Items.COMPASS));
        forager.add(entry(3, Items.EXPERIENCE_BOTTLE));
        forager.add(entry(3, Items.EXPERIENCE_BOTTLE));
        forager.add(entry(3, Items.GOLDEN_APPLE));
        forager.add(entry(4, TCItems.THAUMIUM_PICK.get()));
        forager.add(entry(4, TCItems.THAUMIUM_AXE.get()));
        forager.add(entry(4, TCItems.THAUMIUM_HOE.get()));
        forager.add(entry(4, Items.SPECTRAL_ARROW));
        forager.add(entry(5, Items.ENCHANTED_GOLDEN_APPLE));
        forager.add(entry(5, TCBlocks.SAPLING_SILVERWOOD.get()));
        forager.add(entry(5, TCItems.RARE_EARTH.get()));
        forager.add(entry(5, Items.TOTEM_OF_UNDYING));
        map.put(TCPechEntity.PechType.FORAGER, List.copyOf(forager));

        ArrayList<TradeEntry> mage = new ArrayList<>();
        mage.add(new TradeEntry(1, TCAspectVariantStacks.crystal(Aspect.AIR)));
        mage.add(new TradeEntry(1, TCAspectVariantStacks.crystal(Aspect.EARTH)));
        mage.add(new TradeEntry(1, TCAspectVariantStacks.crystal(Aspect.FIRE)));
        mage.add(new TradeEntry(1, TCAspectVariantStacks.crystal(Aspect.WATER)));
        mage.add(new TradeEntry(1, TCAspectVariantStacks.crystal(Aspect.ORDER)));
        mage.add(new TradeEntry(1, TCAspectVariantStacks.crystal(Aspect.ENTROPY)));
        mage.add(entry(2, Items.POTION));
        mage.add(entry(2, Items.POTION));
        mage.add(new TradeEntry(2, TCAspectVariantStacks.crystal(Aspect.FLUX)));
        mage.add(entry(3, Items.EXPERIENCE_BOTTLE));
        mage.add(entry(3, Items.EXPERIENCE_BOTTLE));
        mage.add(new TradeEntry(3, TCAspectVariantStacks.crystal(Aspect.AURA)));
        mage.add(entry(3, Items.GOLDEN_APPLE));
        mage.add(entry(4, TCItems.CLOTH_BOOTS.get()));
        mage.add(entry(4, TCItems.CLOTH_CHEST.get()));
        mage.add(entry(4, TCItems.CLOTH_LEGS.get()));
        mage.add(entry(5, Items.ENCHANTED_GOLDEN_APPLE));
        mage.add(entry(5, TCItems.PECH_WAND.get()));
        mage.add(entry(5, TCItems.RARE_EARTH.get()));
        mage.add(entry(5, TCItems.VIS_AMULET.get()));
        map.put(TCPechEntity.PechType.MAGE, List.copyOf(mage));

        ArrayList<TradeEntry> stalker = new ArrayList<>();
        stalker.add(entry(1, TCBlocks.CANDLE_BLACK.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_BLUE.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_BROWN.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_CYAN.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_GRAY.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_GREEN.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_LIGHTBLUE.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_LIME.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_MAGENTA.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_ORANGE.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_PINK.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_PURPLE.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_RED.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_SILVER.get()));
        stalker.add(entry(1, TCBlocks.CANDLE_YELLOW.get()));
        stalker.add(entry(2, Items.GHAST_TEAR));
        stalker.add(entry(2, TCItems.ENCHANTED_PLACEHOLDER_SHARPNESS_1.get()));
        stalker.add(entry(3, Items.EXPERIENCE_BOTTLE));
        stalker.add(entry(3, Items.EXPERIENCE_BOTTLE));
        stalker.add(entry(3, Items.GOLDEN_APPLE));
        stalker.add(entry(4, TCItems.VOIDSEER_PEARL.get()));
        stalker.add(entry(4, Items.ENCHANTED_GOLDEN_APPLE));
        stalker.add(entry(5, TCItems.BAUBLE_CHARM.get()));
        stalker.add(entry(5, TCItems.ENCHANTED_PLACEHOLDER_FORTUNE_1.get()));
        stalker.add(entry(5, TCItems.ENCHANTED_PLACEHOLDER_SILK_TOUCH_1.get()));
        stalker.add(entry(5, TCItems.RARE_EARTH.get()));
        map.put(TCPechEntity.PechType.STALKER, List.copyOf(stalker));

        return map;
    }

    private static TradeEntry entry(int tier, net.minecraft.world.level.ItemLike item) {
        return new TradeEntry(tier, new ItemStack(item));
    }

    public record TradeEntry(int tier, ItemStack stack) {
        public TradeEntry {
            stack = stack == null ? ItemStack.EMPTY : stack.copy();
        }
    }
}
