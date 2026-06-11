package thaumcraft.common.research;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

final class TCResearchPageCatalogParser {
    static final Gson GSON = new Gson();
    static final String DIRECTORY = "research_page_catalog";

    private TCResearchPageCatalogParser() {
    }

    static TCResearchPageCatalogData parse(Map<ResourceLocation, JsonElement> files) {
        LinkedHashMap<ResourceLocation, TCResearchPageCatalogEntry> entries = new LinkedHashMap<>();
        files.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                .forEach(file -> parseFile(file.getKey(), file.getValue(), entries));
        return new TCResearchPageCatalogData(entries);
    }

    private static void parseFile(
            ResourceLocation fileId,
            JsonElement element,
            Map<ResourceLocation, TCResearchPageCatalogEntry> entries
    ) {
        JsonObject root = GsonHelper.convertToJsonObject(element, fileId.toString());
        JsonArray array = GsonHelper.getAsJsonArray(root, "entries");
        for (int index = 0; index < array.size(); index++) {
            JsonObject object = GsonHelper.convertToJsonObject(array.get(index), fileId + " entry " + index);
            TCResearchPageCatalogEntry entry = parseEntry(object);
            TCResearchPageCatalogEntry previous = entries.put(entry.id(), entry);
            if (previous != null) {
                throw new JsonParseException("Duplicate research page catalog id " + entry.id());
            }
        }
    }

    private static TCResearchPageCatalogEntry parseEntry(JsonObject object) {
        ResourceLocation id = TCResearchPageCatalogManager.canonicalId(GsonHelper.getAsString(object, "id"));
        return new TCResearchPageCatalogEntry(
                id,
                TCResearchPageLegacySource.parse(GsonHelper.getAsString(object, "legacy_source")),
                TCResearchPageKind.parse(GsonHelper.getAsString(object, "kind")),
                GsonHelper.getAsString(object, "legacy_class", ""),
                GsonHelper.getAsString(object, "required_research", ""),
                GsonHelper.getAsString(object, "legacy_group", ""),
                resourceLocations(object, "targets"),
                legacyOutput(object),
                GsonHelper.getAsBoolean(object, "direct_reference", false),
                GsonHelper.getAsInt(object, "occurrence_count", 0)
        );
    }

    private static List<ResourceLocation> resourceLocations(JsonObject object, String name) {
        if (!object.has(name)) {
            return List.of();
        }
        JsonArray array = GsonHelper.getAsJsonArray(object, name);
        ArrayList<ResourceLocation> values = new ArrayList<>();
        for (int index = 0; index < array.size(); index++) {
            values.add(TCResearchPageCatalogManager.canonicalId(array.get(index).getAsString()));
        }
        return values;
    }

    private static Optional<TCResearchPageLegacyOutput> legacyOutput(JsonObject object) {
        if (!object.has("legacy_output") || object.get("legacy_output").isJsonNull()) {
            return Optional.empty();
        }
        JsonObject output = GsonHelper.getAsJsonObject(object, "legacy_output");
        return Optional.of(new TCResearchPageLegacyOutput(
                TCResearchPageCatalogManager.canonicalId(GsonHelper.getAsString(output, "item")),
                GsonHelper.getAsInt(output, "metadata", 0),
                GsonHelper.getAsInt(output, "count", 1),
                GsonHelper.getAsString(output, "nbt", "")
        ));
    }
}
