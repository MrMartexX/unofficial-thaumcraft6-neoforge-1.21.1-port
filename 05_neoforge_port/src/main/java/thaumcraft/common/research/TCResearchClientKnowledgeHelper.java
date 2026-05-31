package thaumcraft.common.research;

public final class TCResearchClientKnowledgeHelper {
    private TCResearchClientKnowledgeHelper() {
    }

    public static int availableTheoryInspiration() {
        float total = 5.0F;
        for (TCResearchEntryDefinition entry : TCResearchManager.entries()) {
            if (!TCKnowledgeClientCache.hasResearch(entry.key())) {
                continue;
            }
            for (String meta : entry.meta()) {
                if (meta.equalsIgnoreCase("SPIKY")) {
                    total += 0.5F;
                } else if (meta.equalsIgnoreCase("HIDDEN")) {
                    total += 0.1F;
                }
            }
        }
        return Math.min(15, Math.round(total));
    }
}
