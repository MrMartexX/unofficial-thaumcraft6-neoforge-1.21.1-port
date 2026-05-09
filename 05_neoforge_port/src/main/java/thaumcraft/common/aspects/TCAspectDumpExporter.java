package thaumcraft.common.aspects;

import com.google.gson.stream.JsonWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

public final class TCAspectDumpExporter {
    private static final List<Item> ENCHANTED_ITEM_BASES = List.of(
            Items.DIAMOND_SWORD,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_CHESTPLATE,
            Items.BOW,
            Items.FISHING_ROD
    );

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.aspectDump", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.aspectDumpPath",
                    "aspect_parity/dumps/thaumcraft_1_21_aspects.json"
            )).toAbsolutePath().normalize();
            dump(output, event.getServer().registryAccess());
            Thaumcraft.LOGGER.info("Wrote NeoForge Thaumcraft aspect dump to {}", output);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to write NeoForge Thaumcraft aspect dump", e);
        } finally {
            event.getServer().halt(false);
        }
    }

    private static void dump(Path output, HolderLookup.Provider registries) throws IOException {
        Files.createDirectories(output.getParent());
        LinkedHashMap<String, DumpStack> stacks = collectStacks(registries);
        try (BufferedWriter buffered = Files.newBufferedWriter(output, StandardCharsets.UTF_8);
             JsonWriter writer = new JsonWriter(buffered)) {
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("schema").value(1);
            writer.name("side").value("modern_1_21_1");
            writer.name("environment").beginObject();
            writer.name("minecraft").value("1.21.1");
            writer.name("neoforge").value("21.1.228");
            writer.name("thaumcraft").value("0.0.1-gate0");
            writer.endObject();
            writer.name("entry_count").value(stacks.size());
            writer.name("entries").beginArray();
            for (DumpStack dumpStack : stacks.values()) {
                writeEntry(writer, dumpStack);
            }
            writer.endArray();
            writer.endObject();
        }
    }

    private static LinkedHashMap<String, DumpStack> collectStacks(HolderLookup.Provider registries) {
        LinkedHashMap<String, DumpStack> stacks = new LinkedHashMap<>();
        collectPlainStacks(stacks);
        collectDamageSamples(stacks);
        collectPotionStacks(stacks, registries);
        collectEnchantedBooks(stacks, registries);
        collectRepresentativeEnchantedItems(stacks, registries);
        return stacks;
    }

    private static void collectPlainStacks(Map<String, DumpStack> stacks) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item == Items.AIR) {
                continue;
            }
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            addStack(stacks, "plain", "plain:" + id, new ItemStack(item));
        }
    }

    private static void collectDamageSamples(Map<String, DumpStack> stacks) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item == Items.AIR) {
                continue;
            }
            ItemStack base = new ItemStack(item);
            if (!base.isDamageableItem()) {
                continue;
            }

            int max = base.getMaxDamage();
            int[] samples = new int[] {0, 1, Math.max(1, max / 2), Math.max(1, max - 1)};
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            for (int damage : samples) {
                ItemStack stack = new ItemStack(item);
                stack.setDamageValue(Math.min(damage, max));
                addStack(stacks, "damage_sample", "damage:" + id + ":" + stack.getDamageValue(), stack);
            }
        }
    }

    private static void collectPotionStacks(Map<String, DumpStack> stacks, HolderLookup.Provider registries) {
        HolderLookup.RegistryLookup<Potion> potions = registries.lookupOrThrow(Registries.POTION);
        potions.listElements().forEach(potion -> {
            ResourceLocation id = potion.key().location();
            addPotionStack(stacks, "potion", Items.POTION, potion, id);
            addPotionStack(stacks, "splash_potion", Items.SPLASH_POTION, potion, id);
            addPotionStack(stacks, "lingering_potion", Items.LINGERING_POTION, potion, id);
            addPotionStack(stacks, "tipped_arrow", Items.TIPPED_ARROW, potion, id);
        });
    }

    private static void addPotionStack(Map<String, DumpStack> stacks, String carrier, Item item, Holder<Potion> potion, ResourceLocation potionId) {
        ItemStack stack = PotionContents.createItemStack(item, potion);
        addStack(stacks, carrier, "potion:" + carrier + ":" + potionId, stack);
    }

    private static void collectEnchantedBooks(Map<String, DumpStack> stacks, HolderLookup.Provider registries) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);
        enchantments.listElements().forEach(enchantment -> {
            ResourceLocation id = enchantment.key().location();
            for (int level = 1; level <= enchantment.value().getMaxLevel(); level++) {
                ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
                ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                mutable.set(enchantment, level);
                stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
                addStack(stacks, "enchanted_book", "enchanted_book:" + id + ":" + level, stack);
            }
        });
    }

    private static void collectRepresentativeEnchantedItems(Map<String, DumpStack> stacks, HolderLookup.Provider registries) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);
        enchantments.listElements().forEach(enchantment -> {
            ResourceLocation enchantmentId = enchantment.key().location();
            for (Item item : ENCHANTED_ITEM_BASES) {
                ItemStack base = new ItemStack(item);
                if (!isSupported(enchantment, base)) {
                    continue;
                }
                ResourceLocation baseId = BuiltInRegistries.ITEM.getKey(item);
                for (int level = 1; level <= enchantment.value().getMaxLevel(); level++) {
                    ItemStack stack = base.copy();
                    ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                    mutable.set(enchantment, level);
                    stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
                    addStack(stacks, "enchanted_item", "enchanted_item:" + baseId + ":" + enchantmentId + ":" + level, stack);
                }
            }
        });
    }

    private static boolean isSupported(Holder<Enchantment> enchantment, ItemStack stack) {
        try {
            return enchantment.value().isSupportedItem(stack);
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static void addStack(Map<String, DumpStack> stacks, String category, String comparisonKey, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        ItemStack copy = stack.copy();
        copy.setCount(1);
        stacks.putIfAbsent(stackKey(copy), new DumpStack(category, comparisonKey, copy));
    }

    private static String stackKey(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return itemId + "@" + stack.getComponentsPatch();
    }

    private static void writeEntry(JsonWriter writer, DumpStack dumpStack) throws IOException {
        ItemStack stack = dumpStack.stack;
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        AspectList objectAspects = AspectHelper.getObjectAspects(stack);
        AspectList generatedAspects = AspectHelper.generateTags(stack);

        writer.beginObject();
        writer.name("category").value(dumpStack.category);
        writer.name("comparison_key").value(dumpStack.comparisonKey);
        writer.name("stack_key").value(stackKey(stack));
        writer.name("item").value(itemId.toString());
        writer.name("components").value(stack.getComponentsPatch().toString());
        writeAspectResult(writer, "object_aspects", objectAspects);
        writeAspectResult(writer, "generated_aspects", generatedAspects);
        writer.endObject();
    }

    private static void writeAspectResult(JsonWriter writer, String name, AspectList aspects) throws IOException {
        writer.name(name).beginObject();
        if (aspects == null) {
            writer.name("result_kind").value("null");
            writer.name("aspects").beginArray().endArray();
        } else if (aspects.size() == 0) {
            writer.name("result_kind").value("empty");
            writer.name("aspects").beginArray().endArray();
        } else {
            writer.name("result_kind").value("aspects");
            writer.name("aspects").beginArray();
            for (Aspect aspect : aspects.getAspects()) {
                writer.beginObject();
                writer.name("id");
                if (aspect == null) {
                    writer.nullValue();
                } else {
                    writer.value(aspect.getTag());
                }
                writer.name("amount").value(aspects.getAmount(aspect));
                writer.endObject();
            }
            writer.endArray();
        }
        writer.endObject();
    }

    private record DumpStack(String category, String comparisonKey, ItemStack stack) {
    }

    private TCAspectDumpExporter() {
    }
}
