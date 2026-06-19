package thaumcraft.common.crafting.infusion;

public record TCInfusionStartResult(
        boolean started,
        String reason,
        String recipeId,
        TCInfusionValidationResult validation,
        TCInfusionCraftingPlan plan
) {
    public TCInfusionStartResult {
        reason = reason == null ? "" : reason;
        recipeId = recipeId == null ? "" : recipeId;
        validation = validation == null ? TCInfusionValidationResult.failed(reason) : validation;
    }

    public static TCInfusionStartResult started(TCInfusionCraftingPlan plan, TCInfusionValidationResult validation) {
        String recipeId = plan == null ? "" : plan.recipeId().toString();
        return new TCInfusionStartResult(true, "started", recipeId, validation, plan);
    }

    public static TCInfusionStartResult failed(String reason, TCInfusionValidationResult validation) {
        String recipeId = validation == null ? "" : validation.recipeId();
        return new TCInfusionStartResult(false, reason, recipeId, validation, null);
    }
}
