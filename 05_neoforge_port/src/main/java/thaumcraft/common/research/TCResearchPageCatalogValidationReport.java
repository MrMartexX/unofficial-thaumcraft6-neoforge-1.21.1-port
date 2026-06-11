package thaumcraft.common.research;

import java.util.List;

public record TCResearchPageCatalogValidationReport(
        int directReferenceCount,
        int totalEntryCount,
        int researchOccurrenceCount,
        List<String> missingResearchReferences,
        List<String> unresolvedGroupTargets,
        List<String> cyclicGroups
) {
    public TCResearchPageCatalogValidationReport {
        missingResearchReferences = List.copyOf(missingResearchReferences);
        unresolvedGroupTargets = List.copyOf(unresolvedGroupTargets);
        cyclicGroups = List.copyOf(cyclicGroups);
    }

    public boolean isValid() {
        return missingResearchReferences.isEmpty()
                && unresolvedGroupTargets.isEmpty()
                && cyclicGroups.isEmpty();
    }
}
