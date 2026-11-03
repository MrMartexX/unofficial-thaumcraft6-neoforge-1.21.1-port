package thaumcraft.common.golems.seals;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/** One seal per message; chunk reset/removal messages contain no inventory data. */
public record TCSealSyncPayload(ResourceKey<Level> dimension, int operation, CompoundTag data) implements CustomPacketPayload {
    public static final Type<TCSealSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("thaumcraft", "seal_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TCSealSyncPayload> STREAM_CODEC = new StreamCodec<>() {
        public TCSealSyncPayload decode(RegistryFriendlyByteBuf buffer) {
            return new TCSealSyncPayload(ResourceKey.create(Registries.DIMENSION, buffer.readResourceLocation()),
                    buffer.readUnsignedByte(), buffer.readNbt());
        }
        public void encode(RegistryFriendlyByteBuf buffer, TCSealSyncPayload payload) {
            buffer.writeResourceLocation(payload.dimension.location());
            buffer.writeByte(payload.operation);
            buffer.writeNbt(payload.data);
        }
    };
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
