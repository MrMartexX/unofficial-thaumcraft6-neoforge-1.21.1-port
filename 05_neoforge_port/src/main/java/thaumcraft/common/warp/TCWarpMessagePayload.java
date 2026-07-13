package thaumcraft.common.warp;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCWarpMessagePayload(TCWarpType warpType, int change) implements CustomPacketPayload {
    public static final Type<TCWarpMessagePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "warp_message")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCWarpMessagePayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCWarpMessagePayload decode(RegistryFriendlyByteBuf buffer) {
                    return new TCWarpMessagePayload(readType(buffer), buffer.readVarInt());
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCWarpMessagePayload payload) {
                    buffer.writeVarInt(payload.warpType().ordinal());
                    buffer.writeVarInt(payload.change());
                }
            };

    public TCWarpMessagePayload {
        warpType = warpType == null ? TCWarpType.NORMAL : warpType;
    }

    @Override
    public Type<TCWarpMessagePayload> type() {
        return TYPE;
    }

    private static TCWarpType readType(RegistryFriendlyByteBuf buffer) {
        int ordinal = buffer.readVarInt();
        TCWarpType[] values = TCWarpType.values();
        if (ordinal < 0 || ordinal >= values.length) {
            return TCWarpType.NORMAL;
        }
        return values[ordinal];
    }
}
