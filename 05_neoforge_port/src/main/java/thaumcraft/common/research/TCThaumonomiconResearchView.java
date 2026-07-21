package thaumcraft.common.research;

import java.util.List;

public record TCThaumonomiconResearchView(
        String key,
        String name,
        List<String> icons,
        String category,
        int locationX,
        int locationY,
        List<String> parents,
        List<String> siblings,
        List<String> meta,
        TCResearchStatus status,
        boolean unlockable,
        List<TCResearchFlag> flags,
        int currentStage,
        int totalStages,
        List<TCThaumonomiconRecipeSearchView> recipeSearch
) {
    public TCThaumonomiconResearchView {
        key = safe(key);
        name = safe(name);
        icons = List.copyOf(icons);
        category = safe(category);
        parents = List.copyOf(parents);
        siblings = List.copyOf(siblings);
        meta = List.copyOf(meta);
        flags = List.copyOf(flags);
        currentStage = Math.max(0, currentStage);
        totalStages = Math.max(0, totalStages);
        recipeSearch = List.copyOf(recipeSearch);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
