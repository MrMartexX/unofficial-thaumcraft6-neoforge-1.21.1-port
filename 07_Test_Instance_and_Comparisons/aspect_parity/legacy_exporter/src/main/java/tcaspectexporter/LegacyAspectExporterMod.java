package tcaspectexporter;

import com.google.gson.stream.JsonWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

@Mod(
        modid = LegacyAspectExporterMod.MODID,
        name = "Thaumcraft Aspect Legacy Exporter",
        version = "0.1.0",
        dependencies = "required-after:thaumcraft",
        acceptableRemoteVersions = "*"
)
public final class LegacyAspectExporterMod {
    public static final String MODID = "tcaspectexporter";

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.aspectDump", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.aspectDumpPath",
                    "aspect_parity/dumps/thaumcraft_1_12_aspects.json"
            )).toAbsolutePath().normalize();
            dump(output);
            System.out.println("[tc-aspect-exporter] Wrote legacy Thaumcraft aspect dump to " + output);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write legacy Thaumcraft aspect dump", e);
        } finally {
            FMLCommonHandler.instance().getMinecraftServerInstance().initiateShutdown();
        }
    }

    private static void dump(Path output) throws IOException {
        Files.createDirectories(output.getParent());
        LinkedHashMap<String, DumpStack> stacks = collectStacks();
        try (BufferedWriter buffered = Files.newBufferedWriter(output, StandardCharsets.UTF_8);
             JsonWriter writer = new JsonWriter(buffered)) {
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("schema").value(1);
            writer.name("side").value("legacy_1_12_2");
            writer.name("environment").beginObject();
            writer.name("minecraft").value("1.12.2");
            writer.name("forge").value("14.23.5.2860");
            writer.name("thaumcraft").value("6.1.BETA26");
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

    private static LinkedHashMap<String, DumpStack> collectStacks() {
        LinkedHashMap<String, DumpStack> stacks = new LinkedHashMap<>();
        collectPlainStacks(stacks);
        collectCreativeStacks(stacks);
        collectDamageSamples(stacks);
        collectPotionStacks(stacks);
        collectEnchantedBooks(stacks);
        collectRepresentativeEnchantedItems(stacks);
        return stacks;
    }

    private static void collectPlainStacks(Map<String, DumpStack> stacks) {
        for (Item item : Item.REGISTRY) {
            ResourceLocation id = item.getRegistryName();
            addStack(stacks, "plain", id == null ? null : "plain:" + id, new ItemStack(item));
        }
    }

    private static void collectCreativeStacks(Map<String, DumpStack> stacks) {
        for (Item item : Item.REGISTRY) {
            NonNullList<ItemStack> subItems = NonNullList.create();
            try {
                item.getSubItems(CreativeTabs.SEARCH, subItems);
            } catch (RuntimeException ignored) {
            }

            if (subItems.isEmpty()) {
                continue;
            }

            for (ItemStack stack : subItems) {
                addStack(stacks, "creative", comparisonKeyForPlain(stack), stack);
            }
        }
    }

    private static void collectDamageSamples(Map<String, DumpStack> stacks) {
        for (Item item : Item.REGISTRY) {
            ItemStack base = new ItemStack(item);
            if (!base.isItemStackDamageable()) {
                continue;
            }

            int max = base.getMaxDamage();
            int[] samples = new int[] {0, 1, Math.max(1, max / 2), Math.max(1, max - 1)};
            for (int damage : samples) {
                ItemStack stack = new ItemStack(item);
                stack.setItemDamage(Math.min(damage, max));
                ResourceLocation id = item.getRegistryName();
                addStack(stacks, "damage_sample", id == null ? null : "damage:" + id + ":" + stack.getItemDamage(), stack);
            }
        }
    }

    private static void collectPotionStacks(Map<String, DumpStack> stacks) {
        for (PotionType potion : PotionType.REGISTRY) {
            ResourceLocation potionId = potion.getRegistryName();
            if (potionId == null) {
                continue;
            }
            addPotionStack(stacks, "potion", Items.POTIONITEM, potion, potionId);
            addPotionStack(stacks, "splash_potion", Items.SPLASH_POTION, potion, potionId);
            addPotionStack(stacks, "lingering_potion", Items.LINGERING_POTION, potion, potionId);
            addPotionStack(stacks, "tipped_arrow", Items.TIPPED_ARROW, potion, potionId);
        }
    }

    private static void addPotionStack(Map<String, DumpStack> stacks, String carrier, Item item, PotionType potion, ResourceLocation potionId) {
        ItemStack stack = PotionUtils.addPotionToItemStack(new ItemStack(item), potion);
        addStack(stacks, carrier, "potion:" + carrier + ":" + potionId, stack);
    }

    private static void collectEnchantedBooks(Map<String, DumpStack> stacks) {
        for (Enchantment enchantment : Enchantment.REGISTRY) {
            ResourceLocation enchantmentId = enchantment.getRegistryName();
            if (enchantmentId == null) {
                continue;
            }
            for (int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); level++) {
                ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
                ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchantment, level));
                addStack(stacks, "enchanted_book", "enchanted_book:" + enchantmentId + ":" + level, stack);
            }
        }
    }

    private static void collectRepresentativeEnchantedItems(Map<String, DumpStack> stacks) {
        List<ItemStack> bases = new ArrayList<>();
        bases.add(new ItemStack(Items.DIAMOND_SWORD));
        bases.add(new ItemStack(Items.DIAMOND_PICKAXE));
        bases.add(new ItemStack(Items.DIAMOND_CHESTPLATE));
        bases.add(new ItemStack(Items.BOW));
        bases.add(new ItemStack(Items.FISHING_ROD));

        for (Enchantment enchantment : Enchantment.REGISTRY) {
            ResourceLocation enchantmentId = enchantment.getRegistryName();
            if (enchantmentId == null) {
                continue;
            }
            for (ItemStack base : bases) {
                if (!canApply(enchantment, base)) {
                    continue;
                }
                for (int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); level++) {
                    ItemStack stack = base.copy();
                    stack.addEnchantment(enchantment, level);
                    ResourceLocation baseId = base.getItem().getRegistryName();
                    if (baseId != null) {
                        addStack(stacks, "enchanted_item", "enchanted_item:" + baseId + ":" + enchantmentId + ":" + level, stack);
                    }
                }
            }
        }
    }

    private static boolean canApply(Enchantment enchantment, ItemStack stack) {
        try {
            return enchantment.canApply(stack);
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static String comparisonKeyForPlain(ItemStack stack) {
        ResourceLocation id = stack.getItem().getRegistryName();
        if (id == null || stack.getItemDamage() != 0 || stack.hasTagCompound()) {
            return null;
        }
        return "plain:" + id;
    }

    private static void addStack(Map<String, DumpStack> stacks, String category, String comparisonKey, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        ItemStack copy = stack.copy();
        copy.setCount(1);
        String key = stackKey(copy);
        DumpStack existing = stacks.get(key);
        if (existing == null || existing.comparisonKey == null && comparisonKey != null) {
            stacks.put(key, new DumpStack(category, comparisonKey, copy));
        }
    }

    private static String stackKey(ItemStack stack) {
        ResourceLocation itemId = stack.getItem().getRegistryName();
        String id = itemId == null ? "unknown" : itemId.toString();
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : null;
        return id + "@" + stack.getItemDamage() + "@" + (tag == null ? "{}" : tag.toString());
    }

    private static void writeEntry(JsonWriter writer, DumpStack dumpStack) throws IOException {
        ItemStack stack = dumpStack.stack;
        ResourceLocation itemId = stack.getItem().getRegistryName();
        AspectList objectAspects = AspectHelper.getObjectAspects(stack);

        writer.beginObject();
        writer.name("category").value(dumpStack.category);
        writer.name("comparison_key");
        if (dumpStack.comparisonKey == null) {
            writer.nullValue();
        } else {
            writer.value(dumpStack.comparisonKey);
        }
        writer.name("stack_key").value(stackKey(stack));
        writer.name("item").value(itemId == null ? "unknown" : itemId.toString());
        writer.name("meta").value(stack.getItemDamage());
        writer.name("nbt");
        if (stack.hasTagCompound()) {
            writer.value(stack.getTagCompound().toString());
        } else {
            writer.nullValue();
        }
        writeAspectResult(writer, "object_aspects", objectAspects);
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

    private static final class DumpStack {
        final String category;
        final String comparisonKey;
        final ItemStack stack;

        DumpStack(String category, String comparisonKey, ItemStack stack) {
            this.category = category;
            this.comparisonKey = comparisonKey;
            this.stack = stack;
        }
    }
}
