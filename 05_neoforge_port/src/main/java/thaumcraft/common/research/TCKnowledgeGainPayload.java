package thaumcraft.common.research;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

/**
 * NeoForge equivalent of legacy PacketKnowledgeGain.
 */
public record TCKnowledgeGainPayload(TCKnowledgeType knowledgeType, String category) implements CustomPacketPayload {
    public static final Type<TCKnowledgeGainPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "knowledge_gain")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TCKnowledgeGainPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public TCKnowledgeGainPayload decode(RegistryFriendlyByteBuf buffer) {
            TCKnowledgeType type = TCKnowledgeType.parse(buffer.readUtf(32));
            if (type == null) {
                throw new IllegalArgumentException("Invalid Thaumcraft knowledge type id");
            }

            return new TCKnowledgeGainPayload(type, buffer.readUtf(256));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, TCKnowledgeGainPayload payload) {
            buffer.writeUtf(payload.knowledgeType().id(), 32);
            buffer.writeUtf(payload.category(), 256);
        }
    };

    public TCKnowledgeGainPayload {
        if (knowledgeType == null) {
            throw new IllegalArgumentException("Knowledge gain type cannot be null");
        }

        category = TCPlayerKnowledge.normalizeCategory(category);
    }

    @Override
    public Type<TCKnowledgeGainPayload> type() {
        return TYPE;
    }
}
