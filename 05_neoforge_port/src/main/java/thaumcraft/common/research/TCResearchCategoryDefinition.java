package thaumcraft.common.research;

import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public record TCResearchCategoryDefinition(
        String key,
        String requiredResearch,
        AspectList formula,
        ResourceLocation icon,
        ResourceLocation background,
        ResourceLocation overlay
) {
    TCResearchCategoryDefinition {
        key = TCPlayerKnowledge.normalizeCategory(key);
        requiredResearch = TCPlayerKnowledge.normalizeResearchKey(requiredResearch);
        formula = formula == null ? new AspectList() : formula.copy();
    }

    @Override
    public AspectList formula() {
        return formula.copy();
    }

    int applyFormula(AspectList aspects) {
        return applyFormula(aspects, 1.0D);
    }

    int applyFormula(AspectList aspects, double modifier) {
        if (aspects == null || formula == null) {
            return 0;
        }

        double total = 0.0D;
        for (Aspect aspect : formula.getAspects()) {
            total += modifier * modifier * aspects.getAmount(aspect) * (formula.getAmount(aspect) / 10.0D);
        }

        if (total > 0.0D) {
            total = Math.sqrt(total);
        }

        return (int) Math.ceil(total);
    }
}
