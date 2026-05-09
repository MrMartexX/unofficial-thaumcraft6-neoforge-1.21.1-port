package thaumcraft.common.aspects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

final class TCAspectAssignmentParser {
    static final Gson GSON = new Gson();
    static final String DIRECTORY = "aspect_assignments";

    private static final String[] BUNDLED_DEFAULTS = new String[] {
            "data/thaumcraft/aspect_assignments/current_registered.json",
            "data/thaumcraft/aspect_assignments/current_registered_runtime_parity.json",
            "data/thaumcraft/aspect_assignments/legacy_vanilla_core.json",
            "data/thaumcraft/aspect_assignments/legacy_vanilla_materials.json",
            "data/thaumcraft/aspect_assignments/legacy_vanilla_modern_exact.json",
            "data/thaumcraft/aspect_assignments/legacy_vanilla_modern_manual.json",
            "data/thaumcraft/aspect_assignments/legacy_vanilla_runtime_parity.json",
            "data/thaumcraft/aspect_assignments/legacy_vanilla_complex.json"
    };

    static TCAspectAssignmentData loadBundledDefaults() {
        ClassLoader loader = TCAspectAssignmentParser.class.getClassLoader();
        LinkedHashMap<ResourceLocation, JsonElement> files = new LinkedHashMap<>();
        for (String path : BUNDLED_DEFAULTS) {
            try (InputStream stream = loader.getResourceAsStream(path)) {
                if (stream == null) {
                    throw new IllegalStateException("Missing bundled aspect assignment data: " + path);
                }

                try (JsonReader reader = new JsonReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    String fileName = path.substring(path.lastIndexOf('/') + 1, path.length() - ".json".length());
                    files.put(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, fileName), GSON.fromJson(reader, JsonElement.class));
                }
            } catch (Exception e) {
                throw new IllegalStateException("Failed to load bundled aspect assignment data: " + path, e);
            }
        }
        return parse(files);
    }

    static TCAspectAssignmentData parse(Map<ResourceLocation, JsonElement> files) {
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

        JsonArray assignments = GsonHelper.getAsJsonArray(root, "assignments");
        boolean overrides = GsonHelper.getAsBoolean(root, "overrides", false);
        for (int i = 0; i < assignments.size(); i++) {
            JsonObject assignment = GsonHelper.convertToJsonObject(assignments.get(i), fileId + " assignment " + i);
            parseAssignment(fileId, i, assignment, builder, overrides);
        }
    }

    private static void parseAssignment(ResourceLocation fileId, int index, JsonObject assignment, Builder builder, boolean overrides) {
        boolean hasItem = assignment.has("item");
        boolean hasTag = assignment.has("tag");
        boolean hasComplexItem = assignment.has("complex_item");
        boolean hasComplexTag = assignment.has("complex_tag");
        int targetCount = (hasItem ? 1 : 0) + (hasTag ? 1 : 0) + (hasComplexItem ? 1 : 0) + (hasComplexTag ? 1 : 0);
        if (targetCount != 1) {
            throw new JsonParseException(fileId + " assignment " + index + " must define exactly one of item, tag, complex_item or complex_tag");
        }

        AspectList aspects = parseAspects(fileId, index, GsonHelper.getAsJsonArray(assignment, "aspects"));
        if (aspects.size() == 0 && (!overrides || !hasItem)) {
            throw new JsonParseException(fileId + " assignment " + index + " has an empty aspect list; only exact item runtime parity overrides may be empty");
        }
        if (hasItem) {
            ResourceLocation itemId = ResourceLocation.parse(GsonHelper.getAsString(assignment, "item"));
            builder.addDirect(fileId, itemId, aspects, overrides);
        } else if (hasTag) {
            ResourceLocation tagId = ResourceLocation.parse(GsonHelper.getAsString(assignment, "tag"));
            builder.addTag(fileId, TagKey.create(Registries.ITEM, tagId), aspects, overrides);
        } else if (hasComplexItem) {
            ResourceLocation itemId = ResourceLocation.parse(GsonHelper.getAsString(assignment, "complex_item"));
            builder.addComplexDirect(fileId, itemId, aspects, overrides);
        } else {
            ResourceLocation tagId = ResourceLocation.parse(GsonHelper.getAsString(assignment, "complex_tag"));
            builder.addComplexTag(fileId, TagKey.create(Registries.ITEM, tagId), aspects, overrides);
        }
    }

    private static AspectList parseAspects(ResourceLocation fileId, int assignmentIndex, JsonArray elements) {
        AspectList aspects = new AspectList();
        for (int i = 0; i < elements.size(); i++) {
            JsonObject entry = GsonHelper.convertToJsonObject(elements.get(i), fileId + " assignment " + assignmentIndex + " aspect " + i);
            ResourceLocation aspectId = ResourceLocation.parse(GsonHelper.getAsString(entry, "aspect"));
            if (!Thaumcraft.MODID.equals(aspectId.getNamespace())) {
                throw new JsonParseException(fileId + " assignment " + assignmentIndex + " uses non-Thaumcraft aspect id " + aspectId);
            }

            Aspect aspect = Aspect.getAspect(aspectId.getPath());
            if (aspect == null) {
                throw new JsonParseException(fileId + " assignment " + assignmentIndex + " references unknown aspect " + aspectId);
            }

            int amount = GsonHelper.getAsInt(entry, "amount");
            if (amount <= 0) {
                throw new JsonParseException(fileId + " assignment " + assignmentIndex + " aspect " + aspectId + " must have a positive amount");
            }
            aspects.add(aspect, amount);
        }

        return aspects;
    }

    private static final class Builder {
        private final LinkedHashMap<ResourceLocation, AspectList> direct = new LinkedHashMap<>();
        private final LinkedHashMap<TagKey<Item>, AspectList> tags = new LinkedHashMap<>();
        private final LinkedHashMap<ResourceLocation, AspectList> complexDirect = new LinkedHashMap<>();
        private final LinkedHashMap<TagKey<Item>, AspectList> complexTags = new LinkedHashMap<>();

        void clear() {
            direct.clear();
            tags.clear();
            complexDirect.clear();
            complexTags.clear();
        }

        void addDirect(ResourceLocation fileId, ResourceLocation itemId, AspectList aspects, boolean overrides) {
            if (overrides) {
                direct.put(itemId, aspects.copy());
                return;
            }
            AspectList previous = direct.putIfAbsent(itemId, aspects.copy());
            if (previous != null) {
                throw new JsonParseException(fileId + " duplicates direct aspect assignment for item " + itemId);
            }
        }

        void addTag(ResourceLocation fileId, TagKey<Item> tag, AspectList aspects, boolean overrides) {
            if (overrides) {
                tags.put(tag, aspects.copy());
                return;
            }
            AspectList previous = tags.putIfAbsent(tag, aspects.copy());
            if (previous != null) {
                throw new JsonParseException(fileId + " duplicates tag aspect assignment for #" + tag.location());
            }
        }

        void addComplexDirect(ResourceLocation fileId, ResourceLocation itemId, AspectList aspects, boolean overrides) {
            if (overrides) {
                complexDirect.put(itemId, aspects.copy());
                return;
            }
            AspectList previous = complexDirect.putIfAbsent(itemId, aspects.copy());
            if (previous != null) {
                throw new JsonParseException(fileId + " duplicates complex direct aspect assignment for item " + itemId);
            }
        }

        void addComplexTag(ResourceLocation fileId, TagKey<Item> tag, AspectList aspects, boolean overrides) {
            if (overrides) {
                complexTags.put(tag, aspects.copy());
                return;
            }
            AspectList previous = complexTags.putIfAbsent(tag, aspects.copy());
            if (previous != null) {
                throw new JsonParseException(fileId + " duplicates complex tag aspect assignment for #" + tag.location());
            }
        }

        TCAspectAssignmentData build() {
            return new TCAspectAssignmentData(direct, tags, complexDirect, complexTags);
        }
    }

    private TCAspectAssignmentParser() {
    }
}
