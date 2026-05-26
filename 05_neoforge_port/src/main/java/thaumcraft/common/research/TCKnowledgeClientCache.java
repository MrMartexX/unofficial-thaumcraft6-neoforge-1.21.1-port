package thaumcraft.common.research;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public final class TCKnowledgeClientCache {
    // Narrow client cache for Thaumometer highlight filtering. Full research UI sync is a separate future payload.
    private static Set<String> completedResearch = Set.of();

    private TCKnowledgeClientCache() {
    }

    public static void accept(TCKnowledgeSyncPayload payload) {
        TreeSet<String> keys = new TreeSet<>();

        for (String key : payload.completedResearchKeys()) {
            String normalized = TCPlayerKnowledge.normalizeResearchKey(key);
            if (!normalized.isBlank()) {
                keys.add(normalized);
            }
        }

        completedResearch = Set.copyOf(keys);
    }

    public static boolean hasResearch(String key) {
        String normalized = TCPlayerKnowledge.normalizeResearchKey(key);
        return !normalized.isBlank() && completedResearch.contains(normalized);
    }

    public static boolean hasUnknownResearch(Collection<String> keys) {
        for (String key : keys) {
            String normalized = TCPlayerKnowledge.normalizeResearchKey(key);
            if (!normalized.isBlank() && !hasResearch(normalized)) {
                return true;
            }
        }

        return false;
    }

    public static void clear() {
        completedResearch = Set.of();
    }
}
