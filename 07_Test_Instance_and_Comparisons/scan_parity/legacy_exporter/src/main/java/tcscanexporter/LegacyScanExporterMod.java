package tcscanexporter;

import com.google.gson.stream.JsonWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;

@Mod(
        modid = LegacyScanExporterMod.MODID,
        name = "Thaumcraft Scan Legacy Exporter",
        version = "0.1.0",
        dependencies = "required-after:thaumcraft",
        acceptableRemoteVersions = "*"
)
public final class LegacyScanExporterMod {
    public static final String MODID = "tcscanexporter";

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        boolean dumpItems = Boolean.parseBoolean(System.getProperty("tc.scanDump", "false"));
        boolean dumpEntities = Boolean.parseBoolean(System.getProperty("tc.scanEntityDump", "false"));
        if (!dumpItems && !dumpEntities) {
            return;
        }

        try {
            if (dumpItems) {
                Path output = Paths.get(System.getProperty(
                        "tc.scanDumpPath",
                        "scan_parity/dumps/thaumcraft_1_12_scan_items.json"
                )).toAbsolutePath().normalize();
                dump(output);
                System.out.println("[tc-scan-exporter] Wrote legacy Thaumcraft scan item dump to " + output);
            }
            if (dumpEntities) {
                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                WorldServer world = server.getWorld(0);
                Path output = Paths.get(System.getProperty(
                        "tc.scanEntityDumpPath",
                        "scan_parity/dumps/thaumcraft_1_12_scan_entities.json"
                )).toAbsolutePath().normalize();
                dumpEntityAudit(output, world);
                System.out.println("[tc-scan-exporter] Wrote legacy Thaumcraft scan entity dump to " + output);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write legacy Thaumcraft scan dump", e);
        } finally {
            FMLCommonHandler.instance().getMinecraftServerInstance().initiateShutdown();
        }
    }

    private static void dump(Path output) throws IOException, ReflectiveOperationException {
        Files.createDirectories(output.getParent());
        LinkedHashMap<String, DumpStack> stacks = collectStacks();
        List<IScanThing> predicates = getScanPredicates();

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
            writer.name("predicate_count").value(predicates.size());
            writePredicateTypeSummary(writer, predicates);
            writer.name("entry_count").value(stacks.size());
            writer.name("entries").beginArray();
            for (DumpStack dumpStack : stacks.values()) {
                writeEntry(writer, dumpStack, predicates);
            }
            writer.endArray();
            writer.endObject();
        }
    }

    private static void dumpEntityAudit(Path output, WorldServer world) throws IOException, ReflectiveOperationException {
        Files.createDirectories(output.getParent());
        LinkedHashMap<String, DumpEntity> entities = collectEntities(world);
        List<IScanThing> predicates = getScanPredicates();

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
            writer.name("predicate_count").value(predicates.size());
            writePredicateTypeSummary(writer, predicates);
            writer.name("entry_count").value(entities.size());
            writer.name("entries").beginArray();
            for (DumpEntity dumpEntity : entities.values()) {
                writeEntityEntry(writer, dumpEntity, predicates);
            }
            writer.endArray();
            writer.endObject();
        }
    }

    @SuppressWarnings("unchecked")
    private static List<IScanThing> getScanPredicates() throws ReflectiveOperationException {
        Field field = ScanningManager.class.getDeclaredField("things");
        field.setAccessible(true);
        Object value = field.get(null);
        if (value instanceof List) {
            return new ArrayList<>((List<IScanThing>) value);
        }
        return new ArrayList<>();
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

    private static LinkedHashMap<String, DumpEntity> collectEntities(WorldServer world) {
        LinkedHashMap<String, DumpEntity> entities = new LinkedHashMap<>();

        for (ResourceLocation id : EntityList.getEntityNameList()) {
            try {
                Entity entity = EntityList.createEntityByIDFromName(id, world);
                if (entity != null) {
                    addEntity(entities, "plain", "entity:" + id, id.toString(), "plain", entity);
                }
            } catch (RuntimeException ignored) {
            }
        }

        EntityCreeper poweredCreeper = new EntityCreeper(world);
        NBTTagCompound creeperTag = new NBTTagCompound();
        creeperTag.setBoolean("powered", true);
        poweredCreeper.readFromNBT(creeperTag);
        addEntity(entities, "state_variant", "entity:minecraft:creeper#powered", "minecraft:creeper", "powered", poweredCreeper);

        EntityGuardian elderGuardian = new EntityGuardian(world);
        NBTTagCompound guardianTag = new NBTTagCompound();
        guardianTag.setBoolean("Elder", true);
        elderGuardian.readFromNBT(guardianTag);
        addEntity(entities, "state_variant", "entity:minecraft:guardian#elder_nbt", "minecraft:guardian", "elder_nbt", elderGuardian);

        EntityItem itemEntity = new EntityItem(world, 0.0D, 0.0D, 0.0D, new ItemStack(Items.DIAMOND));
        addEntity(entities, "item_entity", "entity:minecraft:item#diamond_stack", "minecraft:item", "diamond_stack", itemEntity);

        return entities;
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

    private static void addEntity(Map<String, DumpEntity> entities, String category, String comparisonKey, String entityId, String variant, Entity entity) {
        if (entity == null) {
            return;
        }

        entities.putIfAbsent(comparisonKey, new DumpEntity(category, comparisonKey, entityId, variant, entity));
    }

    private static void writePredicateTypeSummary(JsonWriter writer, List<IScanThing> predicates) throws IOException {
        TreeMap<String, Integer> counts = new TreeMap<>();
        for (IScanThing predicate : predicates) {
            Integer current = counts.get(predicate.getClass().getName());
            counts.put(predicate.getClass().getName(), current == null ? 1 : current + 1);
        }

        writer.name("predicate_types").beginArray();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            writer.beginObject();
            writer.name("class").value(entry.getKey());
            writer.name("count").value(entry.getValue());
            writer.endObject();
        }
        writer.endArray();
    }

    private static void writeEntry(JsonWriter writer, DumpStack dumpStack, List<IScanThing> predicates) throws IOException {
        ItemStack stack = dumpStack.stack;
        ResourceLocation itemId = stack.getItem().getRegistryName();
        ScanEvaluation scan = evaluateScan(stack, predicates);
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
        writer.name("aspect_source").value(aspectSource(objectAspects, null));
        writeAspectResult(writer, "object_aspects", objectAspects);
        writeAspectResult(writer, "generated_aspects", null);
        writer.name("scan_found").value(scan.found);
        writer.name("still_scannable").value(!scan.researchKeys.isEmpty());
        writer.name("suppress_message").value(scan.suppressMessage);
        writer.name("matched_research_keys").beginArray();
        for (String key : scan.researchKeys) {
            writer.value(key);
        }
        writer.endArray();
        writer.name("matched_predicates").beginArray();
        for (String predicate : scan.predicates) {
            writer.value(predicate);
        }
        writer.endArray();
        writer.endObject();
    }

    private static void writeEntityEntry(JsonWriter writer, DumpEntity dumpEntity, List<IScanThing> predicates) throws IOException {
        Entity entity = dumpEntity.entity;
        ScanEvaluation scan = evaluateScan(entity, predicates);
        AspectList objectAspects = getScanTargetAspects(entity);
        String legacyEntityString = EntityList.getEntityString(entity);

        writer.beginObject();
        writer.name("category").value(dumpEntity.category);
        writer.name("comparison_key").value(dumpEntity.comparisonKey);
        writer.name("entity").value(dumpEntity.entityId);
        writer.name("legacy_entity_string");
        if (legacyEntityString == null) {
            writer.nullValue();
        } else {
            writer.value(legacyEntityString);
        }
        writer.name("variant").value(dumpEntity.variant);
        writer.name("display_name").value(entity.getDisplayName().getUnformattedText());
        if (entity instanceof EntityItem) {
            writer.name("item_stack").value(stackKey(((EntityItem) entity).getItem()));
        } else {
            writer.name("item_stack").nullValue();
        }
        writer.name("aspect_source").value(aspectSource(objectAspects, null));
        writeAspectResult(writer, "object_aspects", objectAspects);
        writeAspectResult(writer, "generated_aspects", null);
        writer.name("scan_found").value(scan.found);
        writer.name("still_scannable").value(!scan.researchKeys.isEmpty());
        writer.name("suppress_message").value(scan.suppressMessage);
        writer.name("matched_research_keys").beginArray();
        for (String key : scan.researchKeys) {
            writer.value(key);
        }
        writer.endArray();
        writer.name("matched_predicates").beginArray();
        for (String predicate : scan.predicates) {
            writer.value(predicate);
        }
        writer.endArray();
        writer.endObject();
    }

    private static ScanEvaluation evaluateScan(ItemStack stack, List<IScanThing> predicates) {
        return evaluateScan((Object) stack, predicates);
    }

    private static ScanEvaluation evaluateScan(Object object, List<IScanThing> predicates) {
        ScanEvaluation evaluation = new ScanEvaluation();
        for (IScanThing predicate : predicates) {
            boolean matches;
            try {
                matches = predicate.checkThing(null, object);
            } catch (RuntimeException e) {
                continue;
            }

            if (!matches) {
                continue;
            }

            evaluation.found = true;
            evaluation.predicates.add(predicate.getClass().getName());
            String key;
            try {
                key = predicate.getResearchKey(null, object);
            } catch (RuntimeException e) {
                continue;
            }
            if (key == null || key.isEmpty()) {
                evaluation.suppressMessage = true;
            } else {
                evaluation.researchKeys.add(key);
            }
        }
        return evaluation;
    }

    private static AspectList getScanTargetAspects(Entity entity) {
        if (entity instanceof EntityItem) {
            return AspectHelper.getObjectAspects(((EntityItem) entity).getItem());
        }

        return AspectHelper.getEntityAspects(entity);
    }

    private static String aspectSource(AspectList objectAspects, AspectList generatedAspects) {
        if (hasAspects(objectAspects)) {
            return "object";
        }
        if (hasAspects(generatedAspects)) {
            return "generated";
        }
        return "none";
    }

    private static boolean hasAspects(AspectList aspects) {
        return aspects != null && aspects.size() > 0;
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

    private static String stackKey(ItemStack stack) {
        ResourceLocation itemId = stack.getItem().getRegistryName();
        String id = itemId == null ? "unknown" : itemId.toString();
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : null;
        return id + "@" + stack.getItemDamage() + "@" + (tag == null ? "{}" : tag.toString());
    }

    private static final class ScanEvaluation {
        boolean found;
        boolean suppressMessage;
        final List<String> researchKeys = new ArrayList<>();
        final List<String> predicates = new ArrayList<>();
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

    private static final class DumpEntity {
        final String category;
        final String comparisonKey;
        final String entityId;
        final String variant;
        final Entity entity;

        DumpEntity(String category, String comparisonKey, String entityId, String variant, Entity entity) {
            this.category = category;
            this.comparisonKey = comparisonKey;
            this.entityId = entityId;
            this.variant = variant;
            this.entity = entity;
        }
    }
}
