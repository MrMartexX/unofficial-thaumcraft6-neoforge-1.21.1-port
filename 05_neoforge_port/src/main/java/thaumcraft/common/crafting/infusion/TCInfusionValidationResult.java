package thaumcraft.common.crafting.infusion;

import thaumcraft.api.aspects.AspectList;

public record TCInfusionValidationResult(
        boolean valid,
        String reason,
        String recipeId,
        AspectList remainingAspects,
        int requiredComponentCount,
        int suppliedComponentCount
) {
    public TCInfusionValidationResult {
        reason = reason == null ? "" : reason;
        recipeId = recipeId == null ? "" : recipeId;
        remainingAspects = remainingAspects == null ? new AspectList() : remainingAspects.copy();
    }

    public static TCInfusionValidationResult valid(AspectList remainingAspects, int requiredComponentCount, int suppliedComponentCount) {
        return new TCInfusionValidationResult(true, "valid", "", remainingAspects, requiredComponentCount, suppliedComponentCount);
    }

    public static TCInfusionValidationResult failed(String reason) {
        return new TCInfusionValidationResult(false, reason, "", new AspectList(), 0, 0);
    }

    public TCInfusionValidationResult withRecipeId(String recipeId) {
        return new TCInfusionValidationResult(valid, reason, recipeId, remainingAspects, requiredComponentCount, suppliedComponentCount);
    }
}
