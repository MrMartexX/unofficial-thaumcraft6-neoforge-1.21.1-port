package thaumcraft.common.research;

import java.util.List;

public record TCResearchStageDefinition(
        String text,
        List<String> requiredResearch,
        List<String> requiredCraft,
        List<String> requiredItem,
        List<String> requiredKnowledge,
        List<String> recipes,
        int warp
) {
    public TCResearchStageDefinition {
        text = text == null ? "" : text.trim();
        requiredResearch = List.copyOf(requiredResearch);
        requiredCraft = List.copyOf(requiredCraft);
        requiredItem = List.copyOf(requiredItem);
        requiredKnowledge = List.copyOf(requiredKnowledge);
        recipes = List.copyOf(recipes);
        warp = Math.max(0, warp);
    }
}
