package thaumcraft.common.research;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

final class TCResearchDataAudit {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private TCResearchDataAudit() {
    }

    static Report writeJson(Path output) throws IOException {
        JsonObject root = new JsonObject();
        root.addProperty("schema", 1);
        root.addProperty("source", "thaumcraft-neoforge-runtime");
        root.add("categories", categories());
        root.add("entries", entries());

        List<Check> checks = progressionChecks();
        JsonArray checkArray = new JsonArray();
        for (Check check : checks) {
            JsonObject object = new JsonObject();
            object.addProperty("name", check.name());
            object.addProperty("passed", check.passed());
            object.addProperty("actual", check.actual());
            object.addProperty("expected", check.expected());
            checkArray.add(object);
        }
        root.add("progression_checks", checkArray);

        Path parent = output.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(output, GSON.toJson(root) + System.lineSeparator(), StandardCharsets.UTF_8);
        long passed = checks.stream().filter(Check::passed).count();
        return new Report(
                TCResearchManager.categories().size(),
                TCResearchManager.entries().size(),
                TCResearchManager.data().stageCount(),
                TCResearchManager.data().addendumCount(),
                (int) passed,
                checks.size() - (int) passed
        );
    }

    private static JsonArray categories() {
        JsonArray categories = new JsonArray();
        TCResearchManager.categories().stream()
                .sorted(Comparator.comparing(TCResearchCategoryDefinition::key))
                .forEach(category -> {
                    JsonObject object = new JsonObject();
                    object.addProperty("key", category.key());
                    object.addProperty("required_research", category.requiredResearch());
                    object.add("formula", aspectList(category.formula()));
                    object.addProperty("icon", location(category.icon()));
                    object.addProperty("background", location(category.background()));
                    object.addProperty("overlay", location(category.overlay()));
                    categories.add(object);
                });
        return categories;
    }

    private static JsonArray entries() {
        JsonArray entries = new JsonArray();
        TCResearchManager.entries().stream()
                .sorted(Comparator.comparing(TCResearchEntryDefinition::key))
                .forEach(entry -> {
                    JsonObject object = new JsonObject();
                    object.addProperty("key", entry.key());
                    object.addProperty("name", entry.name());
                    object.addProperty("category", entry.category());
                    JsonArray location = new JsonArray();
                    location.add(entry.locationX());
                    location.add(entry.locationY());
                    object.add("location", location);
                    object.add("icons", strings(entry.icons()));
                    object.add("parents", strings(entry.parents()));
                    object.add("siblings", strings(entry.siblings()));
                    object.add("meta", strings(entry.meta()));
                    object.add("reward_item", strings(entry.rewardItems()));
                    object.add("reward_knowledge", strings(entry.rewardKnowledge()));
                    object.add("stages", stages(entry.stages()));
                    object.add("addenda", stages(entry.addenda()));
                    entries.add(object);
                });
        return entries;
    }

    private static JsonArray stages(List<TCResearchStageDefinition> stages) {
        JsonArray array = new JsonArray();
        for (TCResearchStageDefinition stage : stages) {
            JsonObject object = new JsonObject();
            object.addProperty("text", stage.text());
            object.add("required_research", strings(stage.requiredResearch()));
            object.add("required_craft", strings(stage.requiredCraft()));
            object.add("required_item", strings(stage.requiredItem()));
            object.add("required_knowledge", strings(stage.requiredKnowledge()));
            object.add("recipes", strings(stage.recipes()));
            object.addProperty("warp", stage.warp());
            array.add(object);
        }
        return array;
    }

    private static JsonArray strings(List<String> values) {
        JsonArray array = new JsonArray();
        values.forEach(array::add);
        return array;
    }

    private static JsonObject aspectList(AspectList list) {
        JsonObject object = new JsonObject();
        for (Aspect aspect : list.getAspectsSortedByName()) {
            object.addProperty(aspect.getTag(), list.getAmount(aspect));
        }
        return object;
    }

    private static String location(Object value) {
        return value == null ? "" : value.toString();
    }

    private static List<Check> progressionChecks() {
        ArrayList<Check> checks = new ArrayList<>();
        TCResearchStageDefinition gatedA = stage(2, true);
        TCResearchStageDefinition gatedB = stage(5, true);
        TCResearchStageDefinition empty = stage(3, false);

        addAdvanceCheck(checks, "start_gated_stage", List.of(gatedA, gatedB), 0, 1, false, 0);
        addAdvanceCheck(checks, "advance_non_final_stage_double_warp", List.of(gatedA, gatedB), 1, 2, false, 4);
        addAdvanceCheck(checks, "advance_final_stage_double_warp", List.of(gatedA, gatedB), 2, 3, true, 10);
        addAdvanceCheck(checks, "single_empty_stage_auto_complete", List.of(empty), 0, 2, true, 3);
        addAdvanceCheck(checks, "final_empty_stage_combines_warp", List.of(gatedA, empty), 1, 3, true, 5);
        addWarpCheck(checks, 1, 1, 0);
        addWarpCheck(checks, 2, 1, 1);
        addWarpCheck(checks, 3, 2, 1);
        addWarpCheck(checks, 5, 3, 2);
        checks.add(parserRewardFieldsCheck());
        return List.copyOf(checks);
    }

    private static Check parserRewardFieldsCheck() {
        String fixture = """
                {
                  "entries": [
                    {
                      "key": "AUDIT_REWARD_FIELDS",
                      "name": "audit.reward",
                      "category": "BASICS",
                      "reward_item": ["minecraft:stick;2"],
                      "reward_knowledge": ["OBSERVATION;BASICS;1"],
                      "stages": [{"text": "audit.stage"}],
                      "addenda": [{"text": "audit.addendum", "required_research": ["AUDIT_TRIGGER"]}]
                    }
                  ]
                }
                """;
        TCResearchData parsed = TCResearchParser.parse(Map.of(
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "audit_reward_fields"),
                JsonParser.parseString(fixture)
        ));
        TCResearchEntryDefinition entry = parsed.entries().get("AUDIT_REWARD_FIELDS");
        boolean passed = entry != null
                && entry.rewardItems().equals(List.of("minecraft:stick;2"))
                && entry.rewardKnowledge().equals(List.of("OBSERVATION;BASICS;1"))
                && entry.stages().size() == 1
                && entry.addenda().size() == 1
                && entry.addenda().getFirst().requiredResearch().equals(List.of("AUDIT_TRIGGER"));
        return new Check(
                "parser_preserves_reward_and_addendum_fields",
                passed,
                passed ? "preserved" : "missing_or_changed",
                "preserved"
        );
    }

    private static TCResearchStageDefinition stage(int warp, boolean gated) {
        return new TCResearchStageDefinition(
                "",
                gated ? List.of("GATE") : List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                warp
        );
    }

    private static void addAdvanceCheck(
            List<Check> checks,
            String name,
            List<TCResearchStageDefinition> stages,
            int currentStage,
            int expectedStage,
            boolean expectedComplete,
            int expectedWarp
    ) {
        TCResearchProgressionSemantics.Advance actual = TCResearchProgressionSemantics.calculate(stages, currentStage);
        String actualText = actual.updatedStage() + "/" + actual.completed() + "/" + actual.warp();
        String expectedText = expectedStage + "/" + expectedComplete + "/" + expectedWarp;
        checks.add(new Check(name, actualText.equals(expectedText), actualText, expectedText));
    }

    private static void addWarpCheck(List<Check> checks, int warp, int expectedPermanent, int expectedNormal) {
        TCResearchProgressionSemantics.WarpAward actual = TCResearchProgressionSemantics.splitWarp(warp);
        String actualText = actual.permanent() + "/" + actual.normal();
        String expectedText = expectedPermanent + "/" + expectedNormal;
        checks.add(new Check("split_warp_" + warp, actualText.equals(expectedText), actualText, expectedText));
    }

    record Report(
            int categories,
            int entries,
            int stages,
            int addenda,
            int progressionChecksPassed,
            int progressionChecksFailed
    ) {
    }

    private record Check(String name, boolean passed, String actual, String expected) {
    }
}
