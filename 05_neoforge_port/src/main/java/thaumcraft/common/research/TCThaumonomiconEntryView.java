package thaumcraft.common.research;

import java.util.List;

public record TCThaumonomiconEntryView(
        TCThaumonomiconResearchView research,
        int selectedStage,
        boolean complete,
        String stageText,
        List<String> addendumTexts,
        List<String> requiredResearch,
        List<String> requiredCraft,
        List<String> requiredItem,
        List<String> requiredKnowledge,
        int warp,
        List<String> satisfiedRequirements,
        List<String> missingRequirements,
        List<String> blockedRequirements,
        List<TCResearchPageBookmark> bookmarks
) {
    public TCThaumonomiconEntryView {
        selectedStage = Math.max(0, selectedStage);
        stageText = stageText == null ? "" : stageText;
        addendumTexts = List.copyOf(addendumTexts);
        requiredResearch = List.copyOf(requiredResearch);
        requiredCraft = List.copyOf(requiredCraft);
        requiredItem = List.copyOf(requiredItem);
        requiredKnowledge = List.copyOf(requiredKnowledge);
        warp = Math.max(0, warp);
        satisfiedRequirements = List.copyOf(satisfiedRequirements);
        missingRequirements = List.copyOf(missingRequirements);
        blockedRequirements = List.copyOf(blockedRequirements);
        bookmarks = List.copyOf(bookmarks);
    }
}
