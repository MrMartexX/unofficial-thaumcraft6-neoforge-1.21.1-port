package thaumcraft.common.research;

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
import java.util.TreeMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;

final class TCScanningAuditExporter {
    private static final List<Item> ENCHANTED_ITEM_BASES = List.of(
            Items.DIAMOND_SWORD,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_CHESTPLATE,
            Items.BOW,
            Items.FISHING_ROD
    );

    private TCScanningAuditExporter() {
    }

    static void onServerStarted(ServerStartedEvent event) {
        boolean dumpItems = Boolean.parseBoolean(System.getProperty("tc.scanDump", "false"));
        boolean dumpEntities = Boolean.parseBoolean(System.getProperty("tc.scanEntityDump", "false"));
        if (!dumpItems && !dumpEntities) {
            return;
        }

        try {
            ServerLevel overworld = event.getServer().overworld();
            ServerPlayer player = FakePlayerFactory.getMinecraft(overworld);
            if (dumpItems) {
                Path output = dumpItemAudit(player);
                Thaumcraft.LOGGER.info("Wrote NeoForge Thaumcraft scan item audit dump to {}", output);
            }
            if (dumpEntities) {
                Path output = dumpEntityAudit(player);
                Thaumcraft.LOGGER.info("Wrote NeoForge Thaumcraft scan entity audit dump to {}", output);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to write NeoForge Thaumcraft scan audit dump", e);
        } finally {
            event.getServer().halt(false);
        }
    }

    static Path dumpItemAudit(ServerPlayer player) throws IOException {
        String outputPath = System.getProperty(
                "tc.scanDumpPath",
                System.getProperty("tc.scanAuditPath", "scanning_parity/dumps/thaumcraft_1_21_scan_items.json")
        );
        Path output = Paths.get(outputPath).toAbsolutePath().normalize();
        dumpItemAudit(player, output);
        return output;
    }

    static Path dumpEntityAudit(ServerPlayer player) throws IOException {
        String outputPath = System.getProperty(
                "tc.scanEntityDumpPath",
                System.getProperty("tc.scanAuditEntitiesPath", "scanning_parity/dumps/thaumcraft_1_21_scan_entities.json")
        );
        Path output = Paths.get(outputPath).toAbsolutePath().normalize();
        dumpEntityAudit(player, output);
        return output;
    }

    private static void dumpItemAudit(ServerPlayer player, Path output) throws IOException {
        Path parent = output.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        TCScanningManager.bootstrap();
        TCScanningManager.ensureServerDynamicScannables(player.server.registryAccess());
        LinkedHashMap<String, AuditStack> stacks = collectStacks(player.server.registryAccess());
        List<IScanThing> predicates = ScanningManager.getScannableThings();

        try (BufferedWriter buffered = Files.newBufferedWriter(output, StandardCharsets.UTF_8);
             JsonWriter writer = new JsonWriter(buffered)) {
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("schema").value(1);
            writer.name("side").value("modern_1_21_1");
            writer.name("environment").beginObject();
            writer.name("minecraft").value("1.21.1");
            writer.name("thaumcraft").value("0.0.1-gate0");
            writer.endObject();
            writer.name("player_context").value(player.getGameProfile().getName());
            writer.name("predicate_count").value(predicates.size());
            writePredicateTypeSummary(writer, predicates);
            writer.name("entry_count").value(stacks.size());
            writer.name("entries").beginArray();
            for (AuditStack auditStack : stacks.values()) {
                writeEntry(writer, player, auditStack);
            }
            writer.endArray();
            writer.endObject();
        }
    }

    private static void dumpEntityAudit(ServerPlayer player, Path output) throws IOException {
        Path parent = output.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        TCScanningManager.bootstrap();
        TCScanningManager.ensureServerDynamicScannables(player.server.registryAccess());
        LinkedHashMap<String, AuditEntity> entities = collectEntities(player.serverLevel());
        List<IScanThing> predicates = ScanningManager.getScannableThings();

        try (BufferedWriter buffered = Files.newBufferedWriter(output, StandardCharsets.UTF_8);
             JsonWriter writer = new JsonWriter(buffered)) {
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("schema").value(1);
            writer.name("side").value("modern_1_21_1");
            writer.name("environment").beginObject();
            writer.name("minecraft").value("1.21.1");
            writer.name("thaumcraft").value("0.0.1-gate0");
            writer.endObject();
            writer.name("player_context").value(player.getGameProfile().getName());
            writer.name("predicate_count").value(predicates.size());
            writePredicateTypeSummary(writer, predicates);
            writer.name("entry_count").value(entities.size());
            writer.name("entries").beginArray();
            for (AuditEntity auditEntity : entities.values()) {
                writeEntityEntry(writer, player, auditEntity);
            }
            writer.endArray();
            writer.endObject();
        }
    }

    private static LinkedHashMap<String, AuditStack> collectStacks(HolderLookup.Provider registries) {
        LinkedHashMap<String, AuditStack> stacks = new LinkedHashMap<>();
        collectPlainStacks(stacks);
        collectDamageSamples(stacks);
        collectPotionStacks(stacks, registries);
        collectEnchantedBooks(stacks, registries);
        collectRepresentativeEnchantedItems(stacks, registries);
        return stacks;
    }

    private static LinkedHashMap<String, AuditEntity> collectEntities(ServerLevel level) {
        LinkedHashMap<String, AuditEntity> entities = new LinkedHashMap<>();

        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
            try {
                Entity entity = type.create(level);
                if (entity != null) {
                    addEntity(entities, "plain", "entity:" + id, id.toString(), "plain", entity);
                }
            } catch (RuntimeException ignored) {
            }
        }

        Creeper poweredCreeper = EntityType.CREEPER.create(level);
        if (poweredCreeper != null) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("powered", true);
            poweredCreeper.readAdditionalSaveData(tag);
            addEntity(entities, "state_variant", "entity:minecraft:creeper#powered", "minecraft:creeper", "powered", poweredCreeper);
        }

        ItemEntity itemEntity = new ItemEntity(level, 0.0D, 0.0D, 0.0D, new ItemStack(Items.DIAMOND));
        addEntity(entities, "item_entity", "entity:minecraft:item#diamond_stack", "minecraft:item", "diamond_stack", itemEntity);

        return entities;
    }

    private static void collectPlainStacks(Map<String, AuditStack> stacks) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item == Items.AIR) {
                continue;
            }
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            addStack(stacks, "plain", "plain:" + id, new ItemStack(item));
        }
    }

    private static void collectDamageSamples(Map<String, AuditStack> stacks) {
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

    private static void collectPotionStacks(Map<String, AuditStack> stacks, HolderLookup.Provider registries) {
        HolderLookup.RegistryLookup<Potion> potions = registries.lookupOrThrow(Registries.POTION);
        potions.listElements().forEach(potion -> {
            ResourceLocation id = potion.key().location();
            addPotionStack(stacks, "potion", Items.POTION, potion, id);
            addPotionStack(stacks, "splash_potion", Items.SPLASH_POTION, potion, id);
            addPotionStack(stacks, "lingering_potion", Items.LINGERING_POTION, potion, id);
            addPotionStack(stacks, "tipped_arrow", Items.TIPPED_ARROW, potion, id);
        });
    }

    private static void addPotionStack(Map<String, AuditStack> stacks, String carrier, Item item, Holder<Potion> potion, ResourceLocation potionId) {
        ItemStack stack = PotionContents.createItemStack(item, potion);
        addStack(stacks, carrier, "potion:" + carrier + ":" + potionId, stack);
    }

    private static void collectEnchantedBooks(Map<String, AuditStack> stacks, HolderLookup.Provider registries) {
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

    private static void collectRepresentativeEnchantedItems(Map<String, AuditStack> stacks, HolderLookup.Provider registries) {
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

    private static void addStack(Map<String, AuditStack> stacks, String category, String comparisonKey, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        ItemStack copy = stack.copy();
        copy.setCount(1);
        stacks.putIfAbsent(stackKey(copy), new AuditStack(category, comparisonKey, copy));
    }

    private static void addEntity(Map<String, AuditEntity> entities, String category, String comparisonKey, String entityId, String variant, Entity entity) {
        if (entity == null) {
            return;
        }

        entities.putIfAbsent(comparisonKey, new AuditEntity(category, comparisonKey, entityId, variant, entity));
    }

    private static void writePredicateTypeSummary(JsonWriter writer, List<IScanThing> predicates) throws IOException {
        TreeMap<String, Integer> counts = new TreeMap<>();
        for (IScanThing predicate : predicates) {
            counts.merge(predicate.getClass().getName(), 1, Integer::sum);
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

    private static void writeEntry(JsonWriter writer, ServerPlayer player, AuditStack auditStack) throws IOException {
        ItemStack stack = auditStack.stack;
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        ScanningManager.ScanEvaluation scan = ScanningManager.evaluateScan(player, stack);
        boolean stillScannable = ScanningManager.isThingStillScannable(player, stack);
        AspectList objectAspects = AspectHelper.getScanAspects(stack);

        writer.beginObject();
        writer.name("category").value(auditStack.category);
        writer.name("comparison_key").value(auditStack.comparisonKey);
        writer.name("stack_key").value(stackKey(stack));
        writer.name("item").value(itemId.toString());
        writer.name("components").value(stack.getComponentsPatch().toString());
        writer.name("display_name").value(stack.getHoverName().getString());
        writer.name("aspect_source").value(aspectSource(objectAspects, null));
        writeAspectResult(writer, "object_aspects", objectAspects);
        writeAspectResult(writer, "generated_aspects", null);
        writer.name("scan_found").value(scan.found());
        writer.name("still_scannable").value(stillScannable);
        writer.name("suppress_message").value(scan.suppressMessage());
        writer.name("matched_research_keys").beginArray();
        for (String key : scan.researchKeys()) {
            writer.value(key);
        }
        writer.endArray();
        writer.name("matched_predicates").beginArray();
        for (IScanThing predicate : scan.matchedThings()) {
            writer.value(predicate.getClass().getName());
        }
        writer.endArray();
        writer.endObject();
    }

    private static void writeEntityEntry(JsonWriter writer, ServerPlayer player, AuditEntity auditEntity) throws IOException {
        Entity entity = auditEntity.entity;
        ScanningManager.ScanEvaluation scan = ScanningManager.evaluateScan(player, entity);
        boolean stillScannable = ScanningManager.isThingStillScannable(player, entity);
        AspectList objectAspects = getScanTargetAspects(entity);
        AspectList generatedAspects = getGeneratedFallbackAspects(entity, objectAspects);

        writer.beginObject();
        writer.name("category").value(auditEntity.category);
        writer.name("comparison_key").value(auditEntity.comparisonKey);
        writer.name("entity").value(auditEntity.entityId);
        writer.name("variant").value(auditEntity.variant);
        writer.name("display_name").value(entity.getDisplayName().getString());
        if (entity instanceof ItemEntity itemEntity) {
            writer.name("item_stack").value(stackKey(itemEntity.getItem()));
        } else {
            writer.name("item_stack").nullValue();
        }
        writer.name("aspect_source").value(aspectSource(objectAspects, generatedAspects));
        writeAspectResult(writer, "object_aspects", objectAspects);
        writeAspectResult(writer, "generated_aspects", generatedAspects);
        writer.name("scan_found").value(scan.found());
        writer.name("still_scannable").value(stillScannable);
        writer.name("suppress_message").value(scan.suppressMessage());
        writer.name("matched_research_keys").beginArray();
        for (String key : scan.researchKeys()) {
            writer.value(key);
        }
        writer.endArray();
        writer.name("matched_predicates").beginArray();
        for (IScanThing predicate : scan.matchedThings()) {
            writer.value(predicate.getClass().getName());
        }
        writer.endArray();
        writer.endObject();
    }

    private static AspectList getScanTargetAspects(Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            return AspectHelper.getScanAspects(itemEntity.getItem());
        }

        return AspectHelper.getEntityAspects(entity);
    }

    private static AspectList getGeneratedFallbackAspects(Entity entity, AspectList objectAspects) {
        if (!(entity instanceof ItemEntity itemEntity) || hasAspects(objectAspects)) {
            return null;
        }

        return AspectHelper.generateTags(itemEntity.getItem());
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
        return aspects != null && aspects.size() > 0 && aspects.visSize() > 0;
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
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return itemId + "@" + stack.getComponentsPatch();
    }

    private record AuditStack(String category, String comparisonKey, ItemStack stack) {
    }

    private record AuditEntity(String category, String comparisonKey, String entityId, String variant, Entity entity) {
    }
}
