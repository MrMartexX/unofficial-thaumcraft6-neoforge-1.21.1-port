package thaumcraft.common.research;

import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record TCResearchPageView(
        ResourceLocation id,
        TCResearchPageKind kind,
        TCResearchPageAvailability availability,
        String requiredResearch,
        Optional<TCResearchPageLegacyOutput> legacyOutput
) {
    public TCResearchPageView {
        requiredResearch = requiredResearch == null ? "" : requiredResearch;
        legacyOutput = legacyOutput == null ? Optional.empty() : legacyOutput;
    }
}
