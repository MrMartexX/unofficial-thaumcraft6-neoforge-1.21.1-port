package thaumcraft.common.research;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.minecraft.nbt.CompoundTag;

public final class TCPlayerKnowledge {
    private static final String TAG_KNOWLEDGE = "knowledge";
    private static final String TAG_RESEARCH = "research";

    private final EnumMap<TCKnowledgeType, TreeMap<String, Integer>> knowledgeRaw = new EnumMap<>(TCKnowledgeType.class);
    private final LinkedHashSet<String> completedResearch = new LinkedHashSet<>();

    public TCPlayerKnowledge() {
        for (TCKnowledgeType type : TCKnowledgeType.values()) {
            knowledgeRaw.put(type, new TreeMap<>());
        }
    }

    public int getRaw(TCKnowledgeType type, String category) {
        return knowledgeRaw.get(type).getOrDefault(normalizeCategory(category), 0);
    }

    public int getPoints(TCKnowledgeType type, String category) {
        return type.rawToPoints(getRaw(type, category));
    }

    public Map<String, Integer> getRawByCategory(TCKnowledgeType type) {
        return Collections.unmodifiableMap(knowledgeRaw.get(type));
    }

    public void setPoints(TCKnowledgeType type, String category, int points) {
        setRaw(type, category, type.pointsToRaw(points));
    }

    public void addPoints(TCKnowledgeType type, String category, int points) {
        String key = normalizeCategory(category);
        int raw = getRaw(type, key);
        setRaw(type, key, raw + type.pointsToRaw(points));
    }

    public void setRaw(TCKnowledgeType type, String category, int raw) {
        String key = normalizeCategory(category);
        TreeMap<String, Integer> values = knowledgeRaw.get(type);

        if (raw <= 0) {
            values.remove(key);
            return;
        }

        values.put(key, raw);
    }

    public void clearAllKnowledge() {
        for (TCKnowledgeType type : TCKnowledgeType.values()) {
            knowledgeRaw.get(type).clear();
        }
    }

    public void clearKnowledge(TCKnowledgeType type) {
        knowledgeRaw.get(type).clear();
    }

    public void clearKnowledge(TCKnowledgeType type, String category) {
        knowledgeRaw.get(type).remove(normalizeCategory(category));
    }

    public Set<String> completedResearch() {
        return Collections.unmodifiableSet(completedResearch);
    }

    public boolean hasResearch(String key) {
        return completedResearch.contains(normalizeResearchKey(key));
    }

    public boolean addResearch(String key) {
        return completedResearch.add(normalizeResearchKey(key));
    }

    public boolean revokeResearch(String key) {
        return completedResearch.remove(normalizeResearchKey(key));
    }

    public void clearResearch() {
        completedResearch.clear();
    }

    public CompoundTag save() {
        CompoundTag root = new CompoundTag();
        CompoundTag knowledgeTag = new CompoundTag();

        for (TCKnowledgeType type : TCKnowledgeType.values()) {
            CompoundTag typeTag = new CompoundTag();

            for (Map.Entry<String, Integer> entry : knowledgeRaw.get(type).entrySet()) {
                if (entry.getValue() > 0) {
                    typeTag.putInt(entry.getKey(), entry.getValue());
                }
            }

            knowledgeTag.put(type.id(), typeTag);
        }

        CompoundTag researchTag = new CompoundTag();

        for (String researchKey : completedResearch) {
            researchTag.putBoolean(researchKey, true);
        }

        root.put(TAG_KNOWLEDGE, knowledgeTag);
        root.put(TAG_RESEARCH, researchTag);
        return root;
    }

    public static TCPlayerKnowledge load(CompoundTag root) {
        TCPlayerKnowledge knowledge = new TCPlayerKnowledge();

        if (root == null) {
            return knowledge;
        }

        CompoundTag knowledgeTag = root.getCompound(TAG_KNOWLEDGE);

        for (TCKnowledgeType type : TCKnowledgeType.values()) {
            CompoundTag typeTag = knowledgeTag.getCompound(type.id());

            for (String category : typeTag.getAllKeys()) {
                int raw = typeTag.getInt(category);

                if (raw > 0) {
                    knowledge.setRaw(type, category, raw);
                }
            }
        }

        CompoundTag researchTag = root.getCompound(TAG_RESEARCH);

        for (String researchKey : researchTag.getAllKeys()) {
            if (researchTag.getBoolean(researchKey)) {
                knowledge.completedResearch.add(normalizeResearchKey(researchKey));
            }
        }

        return knowledge;
    }

    public static String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "BASICS";
        }

        return category.trim().toUpperCase(Locale.ROOT);
    }

    public static String normalizeResearchKey(String key) {
        if (key == null || key.isBlank()) {
            return "";
        }

        return key.trim().toUpperCase(Locale.ROOT);
    }
}