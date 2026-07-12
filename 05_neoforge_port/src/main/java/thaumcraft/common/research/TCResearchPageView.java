package thaumcraft.common.research;

import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record TCResearchPageView(
        ResourceLocation id,
        TCResearchPageKind kind,
        TCResearchPageAvailability availability,
        String requiredResearch,
        Optional<TCResearchPageLegacyOutput> legacyOutput,
        Optional<TCCraftingRecipePageView> craftingRecipe,
        Optional<TCArcaneRecipePageView> arcaneRecipe,
        Optional<TCCrucibleRecipePageView> crucibleRecipe,
        Optional<TCInfusionRecipePageView> infusionRecipe,
        Optional<TCDisplayRecipePageView> displayRecipe
) {
    public TCResearchPageView(
            ResourceLocation id,
            TCResearchPageKind kind,
            TCResearchPageAvailability availability,
            String requiredResearch,
            Optional<TCResearchPageLegacyOutput> legacyOutput,
            Optional<TCCraftingRecipePageView> craftingRecipe,
            Optional<TCArcaneRecipePageView> arcaneRecipe
    ) {
        this(id, kind, availability, requiredResearch, legacyOutput, craftingRecipe, arcaneRecipe, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public TCResearchPageView {
        requiredResearch = requiredResearch == null ? "" : requiredResearch;
        legacyOutput = legacyOutput == null ? Optional.empty() : legacyOutput;
        craftingRecipe = craftingRecipe == null ? Optional.empty() : craftingRecipe;
        arcaneRecipe = arcaneRecipe == null ? Optional.empty() : arcaneRecipe;
        crucibleRecipe = crucibleRecipe == null ? Optional.empty() : crucibleRecipe;
        infusionRecipe = infusionRecipe == null ? Optional.empty() : infusionRecipe;
        displayRecipe = displayRecipe == null ? Optional.empty() : displayRecipe;
    }
}
