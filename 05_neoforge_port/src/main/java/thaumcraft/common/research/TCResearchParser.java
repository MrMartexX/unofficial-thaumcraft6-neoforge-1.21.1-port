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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

final class TCResearchParser {
    static final Gson GSON = new Gson();
    static final String DIRECTORY = "research";

    private TCResearchParser() {
    }

    static TCResearchData parse(Map<ResourceLocation, JsonElement> files) {
        ArrayList<TCResearchEntryDefinition> entries = new ArrayList<>();
        files.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                .forEach(entry -> parseFile(entry.getKey(), entry.getValue(), entries));
        return TCResearchData.empty().withEntries(entries);
    }

    private static void parseFile(ResourceLocation fileId, JsonElement element, List<TCResearchEntryDefinition> entries) {
        JsonObject root = GsonHelper.convertToJsonObject(element, fileId.toString());
        JsonArray entryArray = GsonHelper.getAsJsonArray(root, "entries");
        for (int i = 0; i < entryArray.size(); i++) {
            JsonObject entry = GsonHelper.convertToJsonObject(entryArray.get(i), fileId + " entry " + i);
            entries.add(parseEntry(fileId, i, entry));
        }
    }

    private static TCResearchEntryDefinition parseEntry(ResourceLocation fileId, int index, JsonObject entry) {
        String key = requiredString(fileId, index, entry, "key");
        String name = GsonHelper.getAsString(entry, "name", "");
        String category = GsonHelper.getAsString(entry, "category", "BASICS");
        int[] location = parseLocation(fileId, index, entry);

        return new TCResearchEntryDefinition(
                key,
                name,
                parseStringsOptional(entry, "icons"),
                category,
                location[0],
                location[1],
                parseStringsOptional(entry, "parents"),
                parseStringsOptional(entry, "siblings"),
                parseStringsOptional(entry, "meta"),
                parseStages(fileId, index, entry, "stages"),
                parseStages(fileId, index, entry, "addenda")
        );
    }

    private static String requiredString(ResourceLocation fileId, int index, JsonObject entry, String key) {
        String value = GsonHelper.getAsString(entry, key, "");
        if (value.isBlank()) {
            throw new JsonParseException(fileId + " entry " + index + " has blank " + key);
        }
        return value;
    }

    private static int[] parseLocation(ResourceLocation fileId, int index, JsonObject entry) {
        if (!entry.has("location")) {
            return new int[] {0, 0};
        }

        JsonArray location = GsonHelper.getAsJsonArray(entry, "location");
        if (location.size() != 2) {
            throw new JsonParseException(fileId + " entry " + index + " location must have exactly two values");
        }

        return new int[] {location.get(0).getAsInt(), location.get(1).getAsInt()};
    }

    private static List<TCResearchStageDefinition> parseStages(ResourceLocation fileId, int entryIndex, JsonObject entry, String name) {
        if (!entry.has(name)) {
            return List.of();
        }

        JsonArray array = GsonHelper.getAsJsonArray(entry, name);
        ArrayList<TCResearchStageDefinition> stages = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject stage = GsonHelper.convertToJsonObject(array.get(i), fileId + " entry " + entryIndex + " " + name + " " + i);
            stages.add(parseStage(stage));
        }
        return stages;
    }

    private static TCResearchStageDefinition parseStage(JsonObject stage) {
        return new TCResearchStageDefinition(
                GsonHelper.getAsString(stage, "text", ""),
                parseStringsOptional(stage, "required_research"),
                parseStringsOptional(stage, "required_craft"),
                parseStringsOptional(stage, "required_item"),
                parseStringsOptional(stage, "required_knowledge"),
                parseStringsOptional(stage, "recipes")
        );
    }

    private static List<String> parseStringsOptional(JsonObject object, String arrayName) {
        if (!object.has(arrayName)) {
            return List.of();
        }

        JsonArray array = GsonHelper.getAsJsonArray(object, arrayName);
        ArrayList<String> values = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            values.add(GsonHelper.convertToString(array.get(i), arrayName + "[" + i + "]"));
        }
        return values;
    }
}
