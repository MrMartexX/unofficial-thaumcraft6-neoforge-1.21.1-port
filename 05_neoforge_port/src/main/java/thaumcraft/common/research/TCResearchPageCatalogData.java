package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

record TCResearchPageCatalogData(Map<ResourceLocation, TCResearchPageCatalogEntry> entries) {
    TCResearchPageCatalogData {
        entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
    }

    static TCResearchPageCatalogData empty() {
        return new TCResearchPageCatalogData(Map.of());
    }

    TCResearchPageCatalogValidationReport validate(TCResearchData researchData) {
        ArrayList<String> missingResearchReferences = new ArrayList<>();
        ArrayList<String> unresolvedGroupTargets = new ArrayList<>();
        ArrayList<String> cyclicGroups = new ArrayList<>();
        int researchOccurrenceCount = 0;

        for (TCResearchEntryDefinition entry : researchData.entries().values()) {
            researchOccurrenceCount += validateStages(
                    entry.key(),
                    "stage",
                    entry.stages(),
                    missingResearchReferences
            );
            researchOccurrenceCount += validateStages(
                    entry.key(),
                    "addendum",
                    entry.addenda(),
                    missingResearchReferences
            );
        }

        for (TCResearchPageCatalogEntry entry : entries.values()) {
            if (entry.kind() != TCResearchPageKind.GROUP) {
                continue;
            }
            for (ResourceLocation target : entry.targets()) {
                if (!entries.containsKey(target)) {
                    unresolvedGroupTargets.add(entry.id() + " -> " + target);
                }
            }
            if (hasCycle(entry.id(), entry.id(), new HashSet<>())) {
                cyclicGroups.add(entry.id().toString());
            }
        }

        Collections.sort(missingResearchReferences);
        Collections.sort(unresolvedGroupTargets);
        Collections.sort(cyclicGroups);
        int directReferenceCount = (int) entries.values().stream()
                .filter(TCResearchPageCatalogEntry::directReference)
                .count();
        return new TCResearchPageCatalogValidationReport(
                directReferenceCount,
                entries.size(),
                researchOccurrenceCount,
                missingResearchReferences,
                unresolvedGroupTargets,
                cyclicGroups
        );
    }

    private int validateStages(
            String owner,
            String section,
            List<TCResearchStageDefinition> stages,
            List<String> missingReferences
    ) {
        int occurrenceCount = 0;
        for (int stageIndex = 0; stageIndex < stages.size(); stageIndex++) {
            List<String> recipes = stages.get(stageIndex).recipes();
            occurrenceCount += recipes.size();
            for (int recipeIndex = 0; recipeIndex < recipes.size(); recipeIndex++) {
                ResourceLocation id = TCResearchPageCatalogManager.canonicalId(recipes.get(recipeIndex));
                TCResearchPageCatalogEntry catalogEntry = entries.get(id);
                if (catalogEntry == null || !catalogEntry.directReference()) {
                    missingReferences.add(
                            owner + " " + section + "[" + stageIndex + "].recipes[" + recipeIndex + "] -> " + id
                    );
                }
            }
        }
        return occurrenceCount;
    }

    private boolean hasCycle(ResourceLocation root, ResourceLocation current, Set<ResourceLocation> visiting) {
        if (!visiting.add(current)) {
            return current.equals(root);
        }

        TCResearchPageCatalogEntry entry = entries.get(current);
        if (entry != null && entry.kind() == TCResearchPageKind.GROUP) {
            for (ResourceLocation target : entry.targets()) {
                if (target.equals(root) || hasCycle(root, target, visiting)) {
                    return true;
                }
            }
        }
        visiting.remove(current);
        return false;
    }
}
