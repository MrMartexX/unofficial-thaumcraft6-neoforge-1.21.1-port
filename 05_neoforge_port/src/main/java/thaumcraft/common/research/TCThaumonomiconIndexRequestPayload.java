package thaumcraft.common.research;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCThaumonomiconIndexRequestPayload() implements CustomPacketPayload {
    public static final Type<TCThaumonomiconIndexRequestPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "thaumonomicon_index_request")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCThaumonomiconIndexRequestPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCThaumonomiconIndexRequestPayload decode(RegistryFriendlyByteBuf buffer) {
                    return new TCThaumonomiconIndexRequestPayload();
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCThaumonomiconIndexRequestPayload payload) {
                }
            };

    @Override
    public Type<TCThaumonomiconIndexRequestPayload> type() {
        return TYPE;
    }
}
