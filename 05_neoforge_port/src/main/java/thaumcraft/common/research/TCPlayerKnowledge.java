package thaumcraft.common.research;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.minecraft.nbt.CompoundTag;

public final class TCPlayerKnowledge {
    private static final String TAG_KNOWLEDGE = "knowledge";
    private static final String TAG_RESEARCH = "research";
    private static final String TAG_RESEARCH_STAGES = "research_stages";
    private static final String TAG_RESEARCH_FLAGS = "research_flags";

    private final EnumMap<TCKnowledgeType, TreeMap<String, Integer>> knowledgeRaw = new EnumMap<>(TCKnowledgeType.class);
    private final LinkedHashSet<String> completedResearch = new LinkedHashSet<>();
    private final LinkedHashMap<String, Integer> researchStages = new LinkedHashMap<>();
    private final LinkedHashMap<String, EnumSet<TCResearchFlag>> researchFlags = new LinkedHashMap<>();

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

    public boolean addRaw(TCKnowledgeType type, String category, int rawAmount) {
        String key = normalizeCategory(category);
        int raw = getRaw(type, key);
        if (raw + rawAmount < 0) {
            return false;
        }

        setRaw(type, key, raw + rawAmount);
        return rawAmount != 0;
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

    public boolean isResearchKnown(String key) {
        if (key == null) {
            return false;
        }
        if (key.isBlank()) {
            return true;
        }

        String normalized = normalizeResearchKey(key);
        int stageSeparator = normalized.indexOf('@');
        if (stageSeparator >= 0) {
            String baseKey = normalized.substring(0, stageSeparator);
            int requiredStage = parseStage(normalized.substring(stageSeparator + 1));
            return completedResearch.contains(baseKey) && getResearchStage(baseKey) >= requiredStage;
        }

        return completedResearch.contains(normalized);
    }

    public boolean addResearch(String key) {
        String normalized = baseResearchKey(key);
        return !normalized.isBlank() && completedResearch.add(normalized);
    }

    public boolean revokeResearch(String key) {
        String normalized = baseResearchKey(key);
        researchStages.remove(normalized);
        researchFlags.remove(normalized);
        return completedResearch.remove(normalized);
    }

    public void clearResearch() {
        completedResearch.clear();
        researchStages.clear();
        researchFlags.clear();
    }

    public int getResearchStage(String key) {
        String normalized = baseResearchKey(key);
        if (normalized.isBlank() || !completedResearch.contains(normalized)) {
            return -1;
        }
        return researchStages.getOrDefault(normalized, 0);
    }

    public boolean setResearchStage(String key, int stage) {
        String normalized = baseResearchKey(key);
        if (normalized.isBlank() || !completedResearch.contains(normalized) || stage <= 0) {
            return false;
        }
        researchStages.put(normalized, stage);
        return true;
    }

    public Map<String, Integer> researchStages() {
        return Collections.unmodifiableMap(researchStages);
    }

    public boolean setResearchFlag(String key, TCResearchFlag flag) {
        String normalized = baseResearchKey(key);
        if (normalized.isBlank() || flag == null) {
            return false;
        }
        return researchFlags.computeIfAbsent(normalized, ignored -> EnumSet.noneOf(TCResearchFlag.class)).add(flag);
    }

    public boolean clearResearchFlag(String key, TCResearchFlag flag) {
        String normalized = baseResearchKey(key);
        EnumSet<TCResearchFlag> flags = researchFlags.get(normalized);
        if (flags == null || flag == null) {
            return false;
        }
        boolean removed = flags.remove(flag);
        if (flags.isEmpty()) {
            researchFlags.remove(normalized);
        }
        return removed;
    }

    public boolean hasResearchFlag(String key, TCResearchFlag flag) {
        String normalized = baseResearchKey(key);
        EnumSet<TCResearchFlag> flags = researchFlags.get(normalized);
        return flags != null && flags.contains(flag);
    }

    public Map<String, Set<TCResearchFlag>> researchFlags() {
        LinkedHashMap<String, Set<TCResearchFlag>> out = new LinkedHashMap<>();
        for (Map.Entry<String, EnumSet<TCResearchFlag>> entry : researchFlags.entrySet()) {
            out.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
        }
        return Collections.unmodifiableMap(out);
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

        CompoundTag stageTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : researchStages.entrySet()) {
            if (entry.getValue() > 0 && completedResearch.contains(entry.getKey())) {
                stageTag.putInt(entry.getKey(), entry.getValue());
            }
        }

        CompoundTag flagTag = new CompoundTag();
        for (Map.Entry<String, EnumSet<TCResearchFlag>> entry : researchFlags.entrySet()) {
            if (!entry.getValue().isEmpty() && completedResearch.contains(entry.getKey())) {
                StringBuilder builder = new StringBuilder();
                for (TCResearchFlag flag : entry.getValue()) {
                    if (!builder.isEmpty()) {
                        builder.append(',');
                    }
                    builder.append(flag.name());
                }
                flagTag.putString(entry.getKey(), builder.toString());
            }
        }

        root.put(TAG_KNOWLEDGE, knowledgeTag);
        root.put(TAG_RESEARCH, researchTag);
        root.put(TAG_RESEARCH_STAGES, stageTag);
        root.put(TAG_RESEARCH_FLAGS, flagTag);
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
                knowledge.completedResearch.add(baseResearchKey(researchKey));
            }
        }

        CompoundTag stageTag = root.getCompound(TAG_RESEARCH_STAGES);
        for (String researchKey : stageTag.getAllKeys()) {
            String normalized = baseResearchKey(researchKey);
            int stage = stageTag.getInt(researchKey);
            if (stage > 0 && knowledge.completedResearch.contains(normalized)) {
                knowledge.researchStages.put(normalized, stage);
            }
        }

        CompoundTag flagTag = root.getCompound(TAG_RESEARCH_FLAGS);
        for (String researchKey : flagTag.getAllKeys()) {
            String normalized = baseResearchKey(researchKey);
            if (!knowledge.completedResearch.contains(normalized)) {
                continue;
            }

            EnumSet<TCResearchFlag> flags = EnumSet.noneOf(TCResearchFlag.class);
            for (String rawFlag : flagTag.getString(researchKey).split(",")) {
                try {
                    flags.add(TCResearchFlag.valueOf(rawFlag.trim()));
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (!flags.isEmpty()) {
                knowledge.researchFlags.put(normalized, flags);
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

        return key.trim();
    }

    public static String baseResearchKey(String key) {
        String normalized = normalizeResearchKey(key);
        int stageSeparator = normalized.indexOf('@');
        return stageSeparator >= 0 ? normalized.substring(0, stageSeparator) : normalized;
    }

    private static int parseStage(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
