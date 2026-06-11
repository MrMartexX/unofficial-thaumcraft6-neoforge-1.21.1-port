package thaumcraft.common.research;

import java.util.List;

record TCResearchValidationReport(
        int entryReferenceCount,
        int externalTriggerReferenceCount,
        List<TCResearchReference> unresolvedReferences
) {
    TCResearchValidationReport {
        unresolvedReferences = List.copyOf(unresolvedReferences);
    }

    int unresolvedReferenceCount() {
        return unresolvedReferences.size();
    }

    boolean hasUnresolvedReferences() {
        return !unresolvedReferences.isEmpty();
    }
}
