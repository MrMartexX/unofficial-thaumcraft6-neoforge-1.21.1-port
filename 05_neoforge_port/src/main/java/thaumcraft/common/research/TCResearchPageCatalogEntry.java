package thaumcraft.common.research;

import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record TCResearchPageCatalogEntry(
        ResourceLocation id,
        TCResearchPageLegacySource legacySource,
        TCResearchPageKind kind,
        String legacyClass,
        String requiredResearch,
        String legacyGroup,
        List<ResourceLocation> targets,
        Optional<TCResearchPageLegacyOutput> legacyOutput,
        boolean directReference,
        int occurrenceCount
) {
    public TCResearchPageCatalogEntry {
        legacyClass = legacyClass == null ? "" : legacyClass.trim();
        requiredResearch = requiredResearch == null ? "" : requiredResearch.trim();
        legacyGroup = legacyGroup == null ? "" : legacyGroup.trim();
        targets = List.copyOf(targets);
        legacyOutput = legacyOutput == null ? Optional.empty() : legacyOutput;
        occurrenceCount = Math.max(0, occurrenceCount);
    }
}
