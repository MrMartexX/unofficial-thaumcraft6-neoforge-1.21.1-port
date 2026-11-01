package thaumcraft.common.items.casters;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCFocusZapPayload(
        double x1, double y1, double z1,
        double x2, double y2, double z2,
        int color,
        float width
) implements CustomPacketPayload {
    public static final Type<TCFocusZapPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "focus_zap_fx")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCFocusZapPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCFocusZapPayload decode(RegistryFriendlyByteBuf buffer) {
                    return new TCFocusZapPayload(
                            buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                            buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                            buffer.readInt(),
                            buffer.readFloat()
                    );
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCFocusZapPayload payload) {
                    buffer.writeDouble(payload.x1());
                    buffer.writeDouble(payload.y1());
                    buffer.writeDouble(payload.z1());
                    buffer.writeDouble(payload.x2());
                    buffer.writeDouble(payload.y2());
                    buffer.writeDouble(payload.z2());
                    buffer.writeInt(payload.color());
                    buffer.writeFloat(payload.width());
                }
            };

    @Override
    public Type<TCFocusZapPayload> type() {
        return TYPE;
    }
}
