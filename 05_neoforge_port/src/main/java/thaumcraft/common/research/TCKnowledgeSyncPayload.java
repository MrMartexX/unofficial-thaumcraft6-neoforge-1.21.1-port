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

    public static final StreamCodec<RegistryFriendlyByteBuf, TCKnowledgeSyncPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public TCKnowledgeSyncPayload decode(RegistryFriendlyByteBuf buffer) {
            return new TCKnowledgeSyncPayload(
                    readStringList(buffer),
                    readStringIntMap(buffer),
                    readStringListMap(buffer),
                    readStringIntMap(buffer),
                    readStringIntMap(buffer)
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, TCKnowledgeSyncPayload payload) {
            writeStringList(buffer, payload.completedResearchKeys);
            writeStringIntMap(buffer, payload.researchStages);
            writeStringListMap(buffer, payload.researchFlags);
            writeStringIntMap(buffer, payload.observationRaw);
            writeStringIntMap(buffer, payload.theoryRaw);
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

    private static List<String> readStringList(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        ArrayList<String> values = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            values.add(buffer.readUtf(256));
        }
        return values;
    }

    private static void writeStringList(RegistryFriendlyByteBuf buffer, List<String> values) {
        buffer.writeVarInt(values.size());
        for (String value : values) {
            buffer.writeUtf(value, 256);
        }
    }

    private static Map<String, Integer> readStringIntMap(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        LinkedHashMap<String, Integer> values = new LinkedHashMap<>();
        for (int index = 0; index < size; index++) {
            values.put(buffer.readUtf(256), buffer.readVarInt());
        }
        return values;
    }

    private static void writeStringIntMap(RegistryFriendlyByteBuf buffer, Map<String, Integer> values) {
        buffer.writeVarInt(values.size());
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            buffer.writeUtf(entry.getKey(), 256);
            buffer.writeVarInt(entry.getValue());
        }
    }

    private static Map<String, List<String>> readStringListMap(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        LinkedHashMap<String, List<String>> values = new LinkedHashMap<>();
        for (int index = 0; index < size; index++) {
            values.put(buffer.readUtf(256), readStringList(buffer));
        }
        return values;
    }

    private static void writeStringListMap(RegistryFriendlyByteBuf buffer, Map<String, List<String>> values) {
        buffer.writeVarInt(values.size());
        for (Map.Entry<String, List<String>> entry : values.entrySet()) {
            buffer.writeUtf(entry.getKey(), 256);
            writeStringList(buffer, entry.getValue());
        }
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
