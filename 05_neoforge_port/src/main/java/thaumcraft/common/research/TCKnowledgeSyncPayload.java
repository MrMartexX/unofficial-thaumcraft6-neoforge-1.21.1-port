package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCKnowledgeSyncPayload(
        List<String> completedResearchKeys,
        Map<String, Integer> researchStages,
        Map<String, List<String>> researchFlags,
        Map<String, Integer> observationRaw,
        Map<String, Integer> theoryRaw
) implements CustomPacketPayload {
    public static final Type<TCKnowledgeSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "knowledge_sync")
    );

    private static final int MAX_KEY_LENGTH = 256;
    private static final int MAX_RESEARCH_KEYS = 4096;
    private static final int MAX_RESEARCH_STAGE_ENTRIES = 4096;
    private static final int MAX_RESEARCH_FLAG_ENTRIES = 4096;
    private static final int MAX_RESEARCH_FLAGS_PER_ENTRY = 32;
    private static final int MAX_KNOWLEDGE_POINT_ENTRIES = 256;

    public static final StreamCodec<RegistryFriendlyByteBuf, TCKnowledgeSyncPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public TCKnowledgeSyncPayload decode(RegistryFriendlyByteBuf buffer) {
            return new TCKnowledgeSyncPayload(
                    readStringList(buffer, MAX_RESEARCH_KEYS, "completed research keys"),
                    readStringIntMap(buffer, MAX_RESEARCH_STAGE_ENTRIES, "research stages"),
                    readStringListMap(buffer),
                    readStringIntMap(buffer, MAX_KNOWLEDGE_POINT_ENTRIES, "observation knowledge"),
                    readStringIntMap(buffer, MAX_KNOWLEDGE_POINT_ENTRIES, "theory knowledge")
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, TCKnowledgeSyncPayload payload) {
            writeStringList(buffer, payload.completedResearchKeys, MAX_RESEARCH_KEYS, "completed research keys");
            writeStringIntMap(buffer, payload.researchStages, MAX_RESEARCH_STAGE_ENTRIES, "research stages");
            writeStringListMap(buffer, payload.researchFlags);
            writeStringIntMap(buffer, payload.observationRaw, MAX_KNOWLEDGE_POINT_ENTRIES, "observation knowledge");
            writeStringIntMap(buffer, payload.theoryRaw, MAX_KNOWLEDGE_POINT_ENTRIES, "theory knowledge");
        }
    };

    public TCKnowledgeSyncPayload {
        completedResearchKeys = List.copyOf(completedResearchKeys);
        researchStages = Map.copyOf(researchStages);
        researchFlags = copyStringListMap(researchFlags);
        observationRaw = Map.copyOf(observationRaw);
        theoryRaw = Map.copyOf(theoryRaw);
    }

    public static TCKnowledgeSyncPayload from(TCPlayerKnowledge knowledge) {
        return new TCKnowledgeSyncPayload(
                new ArrayList<>(knowledge.completedResearch()),
                knowledge.researchStages(),
                flagsToNames(knowledge.researchFlags()),
                knowledge.getRawByCategory(TCKnowledgeType.OBSERVATION),
                knowledge.getRawByCategory(TCKnowledgeType.THEORY)
        );
    }

    @Override
    public Type<TCKnowledgeSyncPayload> type() {
        return TYPE;
    }

    private static List<String> readStringList(RegistryFriendlyByteBuf buffer, int maxSize, String label) {
        int size = readBoundedSize(buffer, maxSize, label);
        ArrayList<String> values = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            values.add(buffer.readUtf(MAX_KEY_LENGTH));
        }
        return values;
    }

    private static void writeStringList(
            RegistryFriendlyByteBuf buffer,
            List<String> values,
            int maxSize,
            String label
    ) {
        writeBoundedSize(buffer, values.size(), maxSize, label);
        for (String value : values) {
            buffer.writeUtf(value, MAX_KEY_LENGTH);
        }
    }

    private static Map<String, Integer> readStringIntMap(RegistryFriendlyByteBuf buffer, int maxSize, String label) {
        int size = readBoundedSize(buffer, maxSize, label);
        LinkedHashMap<String, Integer> values = new LinkedHashMap<>();
        for (int index = 0; index < size; index++) {
            values.put(buffer.readUtf(MAX_KEY_LENGTH), buffer.readVarInt());
        }
        return values;
    }

    private static void writeStringIntMap(
            RegistryFriendlyByteBuf buffer,
            Map<String, Integer> values,
            int maxSize,
            String label
    ) {
        writeBoundedSize(buffer, values.size(), maxSize, label);
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            buffer.writeUtf(entry.getKey(), MAX_KEY_LENGTH);
            buffer.writeVarInt(entry.getValue());
        }
    }

    private static Map<String, List<String>> readStringListMap(RegistryFriendlyByteBuf buffer) {
        int size = readBoundedSize(buffer, MAX_RESEARCH_FLAG_ENTRIES, "research flag entries");
        LinkedHashMap<String, List<String>> values = new LinkedHashMap<>();
        for (int index = 0; index < size; index++) {
            values.put(
                    buffer.readUtf(MAX_KEY_LENGTH),
                    readStringList(buffer, MAX_RESEARCH_FLAGS_PER_ENTRY, "research flags")
            );
        }
        return values;
    }

    private static void writeStringListMap(RegistryFriendlyByteBuf buffer, Map<String, List<String>> values) {
        writeBoundedSize(buffer, values.size(), MAX_RESEARCH_FLAG_ENTRIES, "research flag entries");
        for (Map.Entry<String, List<String>> entry : values.entrySet()) {
            buffer.writeUtf(entry.getKey(), MAX_KEY_LENGTH);
            writeStringList(buffer, entry.getValue(), MAX_RESEARCH_FLAGS_PER_ENTRY, "research flags");
        }
    }

    private static int readBoundedSize(RegistryFriendlyByteBuf buffer, int maxSize, String label) {
        int size = buffer.readVarInt();
        if (size < 0 || size > maxSize) {
            throw new IllegalArgumentException("Invalid knowledge sync " + label + " count: " + size);
        }
        return size;
    }

    private static void writeBoundedSize(RegistryFriendlyByteBuf buffer, int size, int maxSize, String label) {
        if (size < 0 || size > maxSize) {
            throw new IllegalArgumentException("Too many knowledge sync " + label + ": " + size);
        }
        buffer.writeVarInt(size);
    }

    private static Map<String, List<String>> flagsToNames(Map<String, Set<TCResearchFlag>> flags) {
        LinkedHashMap<String, List<String>> values = new LinkedHashMap<>();
        for (Map.Entry<String, Set<TCResearchFlag>> entry : flags.entrySet()) {
            ArrayList<String> names = new ArrayList<>();
            for (TCResearchFlag flag : entry.getValue()) {
                names.add(flag.name());
            }
            values.put(entry.getKey(), names);
        }
        return values;
    }

    private static Map<String, List<String>> copyStringListMap(Map<String, List<String>> input) {
        LinkedHashMap<String, List<String>> values = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : input.entrySet()) {
            values.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return Map.copyOf(values);
    }
}