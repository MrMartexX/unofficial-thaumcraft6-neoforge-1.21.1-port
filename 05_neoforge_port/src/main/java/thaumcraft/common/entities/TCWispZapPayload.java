package thaumcraft.common.entities;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCWispZapPayload(int sourceEntityId, int targetEntityId) implements CustomPacketPayload {
    public static final Type<TCWispZapPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "wisp_zap_fx")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCWispZapPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCWispZapPayload decode(RegistryFriendlyByteBuf buffer) {
                    return new TCWispZapPayload(buffer.readVarInt(), buffer.readVarInt());
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCWispZapPayload payload) {
                    buffer.writeVarInt(payload.sourceEntityId());
                    buffer.writeVarInt(payload.targetEntityId());
                }
            };

    @Override
    public Type<TCWispZapPayload> type() {
        return TYPE;
    }
}
