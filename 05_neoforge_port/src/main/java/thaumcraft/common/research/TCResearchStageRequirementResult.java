package thaumcraft.common.research;

import java.util.List;

record TCResearchStageRequirementResult(
        String researchKey,
        int stageIndex,
        int totalStages,
        List<String> satisfied,
        List<String> missing,
        List<String> blocked
) {
    TCResearchStageRequirementResult {
        researchKey = TCPlayerKnowledge.baseResearchKey(researchKey);
        satisfied = List.copyOf(satisfied);
        missing = List.copyOf(missing);
        blocked = List.copyOf(blocked);
    }

    boolean hasStage() {
        return stageIndex >= 0 && stageIndex < totalStages;
    }

    boolean passed() {
        return missing.isEmpty() && blocked.isEmpty();
    }
}
