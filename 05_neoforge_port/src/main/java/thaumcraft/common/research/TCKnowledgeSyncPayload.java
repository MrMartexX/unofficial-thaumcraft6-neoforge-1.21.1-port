package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCKnowledgeSyncPayload(List<String> completedResearchKeys) implements CustomPacketPayload {
    public static final Type<TCKnowledgeSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "knowledge_sync")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TCKnowledgeSyncPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public TCKnowledgeSyncPayload decode(RegistryFriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            ArrayList<String> keys = new ArrayList<>(size);

            for (int index = 0; index < size; index++) {
                keys.add(buffer.readUtf());
            }

            return new TCKnowledgeSyncPayload(keys);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, TCKnowledgeSyncPayload payload) {
            buffer.writeVarInt(payload.completedResearchKeys.size());

            for (String key : payload.completedResearchKeys) {
                buffer.writeUtf(key);
            }
        }
    };

    public TCKnowledgeSyncPayload {
        completedResearchKeys = List.copyOf(completedResearchKeys);
    }

    static TCKnowledgeSyncPayload from(TCPlayerKnowledge knowledge) {
        return new TCKnowledgeSyncPayload(new ArrayList<>(knowledge.completedResearch()));
    }

    @Override
    public Type<TCKnowledgeSyncPayload> type() {
        return TYPE;
    }
}
