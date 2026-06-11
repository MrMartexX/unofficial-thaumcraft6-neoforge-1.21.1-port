package thaumcraft.common.research;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

final class TCScannableParser {
    static final Gson GSON = new Gson();
    static final String DIRECTORY = "scannables";

    private TCScannableParser() {
    }

    static TCScannableData parse(Map<ResourceLocation, JsonElement> files) {
        Builder builder = new Builder();
        files.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                .forEach(entry -> parseFile(entry.getKey(), entry.getValue(), builder));
        return builder.build();
    }

    private static void parseFile(ResourceLocation fileId, JsonElement element, Builder builder) {
        JsonObject root = GsonHelper.convertToJsonObject(element, fileId.toString());
        if (GsonHelper.getAsBoolean(root, "replace", false)) {
            builder.clear();
        }

        JsonArray entries = GsonHelper.getAsJsonArray(root, "scannables");
        for (int i = 0; i < entries.size(); i++) {
            JsonObject entry = GsonHelper.convertToJsonObject(entries.get(i), fileId + " scannable " + i);
            parseScannable(fileId, i, entry, builder);
        }
    }

    private static void parseScannable(ResourceLocation fileId, int index, JsonObject entry, Builder builder) {
        String type = GsonHelper.getAsString(entry, "type");
        String research = GsonHelper.getAsString(entry, "research");
        if (research.isBlank()) {
            throw new JsonParseException(fileId + " scannable " + index + " has blank research key");
        }

        switch (type) {
            case "item" -> builder.add(new TCScannableDefinition.ItemDefinition(research, parseItems(fileId, index, entry)));
            case "block" -> builder.add(new TCScannableDefinition.BlockDefinition(research, parseBlocks(fileId, index, entry)));
            case "entity" -> builder.add(new TCScannableDefinition.EntityDefinition(research, parseEntities(fileId, index, entry)));
            case "ore_dictionary" -> builder.add(new TCScannableDefinition.OreDictionaryDefinition(research, parseStrings(fileId, index, entry, "entries")));
            case "tag" -> builder.add(new TCScannableDefinition.TagDefinition(
                    research,
                    parseItemTags(fileId, index, entry),
                    parseBlockTags(fileId, index, entry)));
            default -> throw new JsonParseException(fileId + " scannable " + index + " has unknown type " + type);
        }
    }

    private static List<Item> parseItems(ResourceLocation fileId, int index, JsonObject entry) {
        ArrayList<Item> items = new ArrayList<>();
        for (String value : parseStrings(fileId, index, entry, "items")) {
            ResourceLocation id = ResourceLocation.parse(value);
            Item item = BuiltInRegistries.ITEM.getOptional(id)
                    .orElseThrow(() -> new JsonParseException(fileId + " scannable " + index + " references missing item " + id));
            items.add(item);
        }
        return items;
    }

    private static List<Block> parseBlocks(ResourceLocation fileId, int index, JsonObject entry) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (String value : parseStrings(fileId, index, entry, "blocks")) {
            ResourceLocation id = ResourceLocation.parse(value);
            Block block = BuiltInRegistries.BLOCK.getOptional(id)
                    .orElseThrow(() -> new JsonParseException(fileId + " scannable " + index + " references missing block " + id));
            blocks.add(block);
        }
        return blocks;
    }

    private static List<EntityType<?>> parseEntities(ResourceLocation fileId, int index, JsonObject entry) {
        ArrayList<EntityType<?>> entities = new ArrayList<>();
        for (String value : parseStrings(fileId, index, entry, "entities")) {
            ResourceLocation id = ResourceLocation.parse(value);
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(id)
                    .orElseThrow(() -> new JsonParseException(fileId + " scannable " + index + " references missing entity type " + id));
            entities.add(entityType);
        }
        return entities;
    }

    private static List<TagKey<Item>> parseItemTags(ResourceLocation fileId, int index, JsonObject entry) {
        ArrayList<TagKey<Item>> tags = new ArrayList<>();
        for (String value : parseStringsOptional(entry, "item_tags")) {
            tags.add(TagKey.create(Registries.ITEM, ResourceLocation.parse(value)));
        }
        if (tags.isEmpty() && !entry.has("block_tags")) {
            throw new JsonParseException(fileId + " scannable " + index + " tag definition must define item_tags or block_tags");
        }
        return tags;
    }

    private static List<TagKey<Block>> parseBlockTags(ResourceLocation fileId, int index, JsonObject entry) {
        ArrayList<TagKey<Block>> tags = new ArrayList<>();
        for (String value : parseStringsOptional(entry, "block_tags")) {
            tags.add(TagKey.create(Registries.BLOCK, ResourceLocation.parse(value)));
        }
        if (tags.isEmpty() && !entry.has("item_tags")) {
            throw new JsonParseException(fileId + " scannable " + index + " tag definition must define item_tags or block_tags");
        }
        return tags;
    }

    private static List<String> parseStrings(ResourceLocation fileId, int index, JsonObject entry, String arrayName) {
        List<String> values = parseStringsOptional(entry, arrayName);
        if (values.isEmpty()) {
            throw new JsonParseException(fileId + " scannable " + index + " must define non-empty " + arrayName);
        }
        return values;
    }

    private static List<String> parseStringsOptional(JsonObject entry, String arrayName) {
        if (!entry.has(arrayName)) {
            return List.of();
        }

        JsonArray array = GsonHelper.getAsJsonArray(entry, arrayName);
        ArrayList<String> values = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            values.add(GsonHelper.convertToString(array.get(i), arrayName + "[" + i + "]"));
        }
        return values;
    }

    private static final class Builder {
        private final ArrayList<TCScannableDefinition> definitions = new ArrayList<>();

        void clear() {
            definitions.clear();
        }

        void add(TCScannableDefinition definition) {
            definitions.add(definition);
        }

        TCScannableData build() {
            return new TCScannableData(definitions);
        }
    }
}
