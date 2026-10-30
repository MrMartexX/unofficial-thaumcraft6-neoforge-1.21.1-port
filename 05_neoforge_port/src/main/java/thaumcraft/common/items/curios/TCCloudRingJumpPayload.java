package thaumcraft.common.items.curios;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCCloudRingJumpPayload() implements CustomPacketPayload {
    public static final Type<TCCloudRingJumpPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "cloud_ring_jump")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TCCloudRingJumpPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCCloudRingJumpPayload decode(RegistryFriendlyByteBuf buffer) {
                    return new TCCloudRingJumpPayload();
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCCloudRingJumpPayload payload) {
                    // Marker packet only: the server validates the equipped/held Cloud Ring state.
                }
            };

    @Override
    public Type<TCCloudRingJumpPayload> type() {
        return TYPE;
    }
}
