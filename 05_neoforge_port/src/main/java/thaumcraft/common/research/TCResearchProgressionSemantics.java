package thaumcraft.common.research;

import java.util.List;

final class TCResearchProgressionSemantics {
    private TCResearchProgressionSemantics() {
    }

    static Advance calculate(List<TCResearchStageDefinition> stages, int currentStage) {
        if (stages == null || stages.isEmpty()) {
            return new Advance(currentStage, true, 0);
        }

        int stageCount = stages.size();
        int stage = Math.max(0, currentStage);
        TCResearchStageDefinition current = stage > 0
                ? stages.get(Math.min(stage, stageCount) - 1)
                : null;

        if (stageCount == 1 && stage == 0 && isEmptyGateStage(stages.getFirst())) {
            stage++;
        } else if (stageCount > 1
                && stageCount <= stage + 1
                && stage < stageCount
                && isEmptyGateStage(stages.get(stage))) {
            stage++;
        }

        int updatedStage = Math.min(stageCount + 1, stage + 1);
        boolean completed = stage >= stageCount;
        int warp = current == null ? 0 : current.warp();

        if (completed) {
            current = stages.get(Math.min(stage, stageCount) - 1);
        }
        if (current != null) {
            warp += current.warp();
        }

        return new Advance(updatedStage, completed, warp);
    }

    static WarpAward splitWarp(int warp) {
        int amount = Math.max(0, warp);
        if (amount <= 1) {
            return new WarpAward(amount, 0);
        }
        int normal = amount / 2;
        return new WarpAward(amount - normal, normal);
    }

    private static boolean isEmptyGateStage(TCResearchStageDefinition stage) {
        return stage.requiredCraft().isEmpty()
                && stage.requiredItem().isEmpty()
                && stage.requiredKnowledge().isEmpty()
                && stage.requiredResearch().isEmpty();
    }

    record Advance(int updatedStage, boolean completed, int warp) {
    }

    record WarpAward(int permanent, int normal) {
    }
}
