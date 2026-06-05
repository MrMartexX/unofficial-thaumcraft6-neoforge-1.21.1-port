package thaumcraft.common.research;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCThaumonomiconActionPayload(
        int actionId,
        String researchKey,
        int clientRevision
) implements CustomPacketPayload {
    public static final int ADVANCE_CURRENT_STAGE = 0;
    public static final int START_RESEARCH = 1;
    public static final int ACKNOWLEDGE_ENTRY = 2;
    public static final Type<TCThaumonomiconActionPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "thaumonomicon_action")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCThaumonomiconActionPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCThaumonomiconActionPayload decode(RegistryFriendlyByteBuf buffer) {
                    return new TCThaumonomiconActionPayload(
                            buffer.readVarInt(),
                            buffer.readUtf(TCThaumonomiconCodec.MAX_KEY_LENGTH),
                            buffer.readVarInt()
                    );
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCThaumonomiconActionPayload payload) {
                    buffer.writeVarInt(payload.actionId());
                    buffer.writeUtf(payload.researchKey(), TCThaumonomiconCodec.MAX_KEY_LENGTH);
                    buffer.writeVarInt(payload.clientRevision());
                }
            };

    public TCThaumonomiconActionPayload {
        researchKey = TCPlayerKnowledge.baseResearchKey(researchKey);
    }

    public TCThaumonomiconActionPayload(int actionId, String researchKey) {
        this(actionId, researchKey, 0);
    }

    @Override
    public Type<TCThaumonomiconActionPayload> type() {
        return TYPE;
    }
}
