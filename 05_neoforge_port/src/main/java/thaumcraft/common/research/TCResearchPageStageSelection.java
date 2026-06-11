package thaumcraft.common.research;

import java.util.List;

record TCResearchPageStageSelection(
        TCResearchEntryDefinition entry,
        TCResearchStageDefinition stage,
        int stageIndex,
        boolean complete,
        List<TCResearchStageDefinition> visibleAddenda
) {
    TCResearchPageStageSelection {
        visibleAddenda = List.copyOf(visibleAddenda);
    }
}
