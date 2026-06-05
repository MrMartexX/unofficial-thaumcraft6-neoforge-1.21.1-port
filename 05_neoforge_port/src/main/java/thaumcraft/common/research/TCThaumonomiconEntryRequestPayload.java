package thaumcraft.common.research;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCThaumonomiconEntryRequestPayload(String researchKey, int clientRevision) implements CustomPacketPayload {
    public static final Type<TCThaumonomiconEntryRequestPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "thaumonomicon_entry_request")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCThaumonomiconEntryRequestPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCThaumonomiconEntryRequestPayload decode(RegistryFriendlyByteBuf buffer) {
                    return new TCThaumonomiconEntryRequestPayload(
                            buffer.readUtf(TCThaumonomiconCodec.MAX_KEY_LENGTH),
                            buffer.readVarInt()
                    );
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCThaumonomiconEntryRequestPayload payload) {
                    buffer.writeUtf(payload.researchKey(), TCThaumonomiconCodec.MAX_KEY_LENGTH);
                    buffer.writeVarInt(payload.clientRevision());
                }
            };

    public TCThaumonomiconEntryRequestPayload {
        researchKey = TCPlayerKnowledge.baseResearchKey(researchKey);
    }

    public TCThaumonomiconEntryRequestPayload(String researchKey) {
        this(researchKey, 0);
    }

    @Override
    public Type<TCThaumonomiconEntryRequestPayload> type() {
        return TYPE;
    }
}
