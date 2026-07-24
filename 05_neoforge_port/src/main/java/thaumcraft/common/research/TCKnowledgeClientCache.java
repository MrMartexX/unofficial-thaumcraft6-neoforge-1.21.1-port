package thaumcraft.common.research;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class TCKnowledgeClientCache {
    private static Set<String> completedResearch = Set.of();
    private static Map<String, Integer> researchStages = Map.of();
    private static Map<String, Set<String>> researchFlags = Map.of();
    private static Map<String, Integer> observationRaw = Map.of();
    private static Map<String, Integer> theoryRaw = Map.of();
    private static long snapshotVersion;
    private static final Queue<TCKnowledgeGainPayload> knowledgeGainQueue = new ConcurrentLinkedQueue<>();

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

        TreeMap<String, Integer> stages = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : payload.researchStages().entrySet()) {
            String normalized = TCPlayerKnowledge.baseResearchKey(entry.getKey());
            if (!normalized.isBlank() && entry.getValue() > 0) {
                stages.put(normalized, entry.getValue());
            }
        }
        researchStages = Map.copyOf(stages);

        TreeMap<String, Set<String>> flags = new TreeMap<>();
        for (Map.Entry<String, List<String>> entry : payload.researchFlags().entrySet()) {
            String normalized = TCPlayerKnowledge.baseResearchKey(entry.getKey());
            if (!normalized.isBlank() && !entry.getValue().isEmpty()) {
                flags.put(normalized, Set.copyOf(entry.getValue()));
            }
        }
        researchFlags = Map.copyOf(flags);

        observationRaw = normalizeRawMap(payload.observationRaw());
        theoryRaw = normalizeRawMap(payload.theoryRaw());
        snapshotVersion++;
    }

    public static void accept(TCKnowledgeGainPayload payload) {
        if (payload != null) {
            knowledgeGainQueue.offer(payload);
        }
    }

    public static TCKnowledgeGainPayload pollKnowledgeGain() {
        return knowledgeGainQueue.poll();
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

    public static int researchStage(String key) {
        return researchStages.getOrDefault(TCPlayerKnowledge.baseResearchKey(key), -1);
    }

    public static boolean hasResearchFlag(String key, TCResearchFlag flag) {
        if (flag == null) {
            return false;
        }
        Set<String> flags = researchFlags.get(TCPlayerKnowledge.baseResearchKey(key));
        return flags != null && flags.contains(flag.name());
    }

    public static int rawKnowledge(TCKnowledgeType type, String category) {
        if (type == null) {
            return 0;
        }
        return rawMap(type).getOrDefault(TCPlayerKnowledge.normalizeCategory(category), 0);
    }

    public static int knowledgePoints(TCKnowledgeType type, String category) {
        return type == null ? 0 : type.rawToPoints(rawKnowledge(type, category));
    }

    public static Map<String, Integer> rawKnowledgeByCategory(TCKnowledgeType type) {
        return rawMap(type);
    }

    public static long snapshotVersion() {
        return snapshotVersion;
    }

    public static void clear() {
        completedResearch = Set.of();
        researchStages = Map.of();
        researchFlags = Map.of();
        observationRaw = Map.of();
        theoryRaw = Map.of();
        knowledgeGainQueue.clear();
        snapshotVersion++;
    }

    private static Map<String, Integer> rawMap(TCKnowledgeType type) {
        return type == TCKnowledgeType.THEORY ? theoryRaw : observationRaw;
    }

    private static Map<String, Integer> normalizeRawMap(Map<String, Integer> input) {
        TreeMap<String, Integer> values = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : input.entrySet()) {
            String category = TCPlayerKnowledge.normalizeCategory(entry.getKey());
            if (entry.getValue() > 0) {
                values.put(category, entry.getValue());
            }
        }
        return Map.copyOf(values);
    }
}
