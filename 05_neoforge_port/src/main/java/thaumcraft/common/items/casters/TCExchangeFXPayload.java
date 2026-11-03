package thaumcraft.common.items.casters;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record TCExchangeFXPayload(ResourceKey<Level> dimension, BlockPos pos) implements CustomPacketPayload {
    public static final Type<TCExchangeFXPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("thaumcraft", "exchange_bamf"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TCExchangeFXPayload> STREAM_CODEC = new StreamCodec<>() {
        public TCExchangeFXPayload decode(RegistryFriendlyByteBuf buffer) {
            return new TCExchangeFXPayload(ResourceKey.create(Registries.DIMENSION, buffer.readResourceLocation()), buffer.readBlockPos());
        }
        public void encode(RegistryFriendlyByteBuf buffer, TCExchangeFXPayload value) { buffer.writeResourceLocation(value.dimension.location()); buffer.writeBlockPos(value.pos); }
    };
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
