package thaumcraft.common.warp;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCWarpSyncPayload(int permanent, int normal, int temporary, int counter) implements CustomPacketPayload {
    public static final Type<TCWarpSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "warp_sync")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCWarpSyncPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCWarpSyncPayload decode(RegistryFriendlyByteBuf buffer) {
                    return new TCWarpSyncPayload(
                            buffer.readVarInt(),
                            buffer.readVarInt(),
                            buffer.readVarInt(),
                            buffer.readVarInt()
                    );
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCWarpSyncPayload payload) {
                    buffer.writeVarInt(payload.permanent());
                    buffer.writeVarInt(payload.normal());
                    buffer.writeVarInt(payload.temporary());
                    buffer.writeVarInt(payload.counter());
                }
            };

    public TCWarpSyncPayload {
        permanent = clamp(permanent);
        normal = clamp(normal);
        temporary = clamp(temporary);
        counter = Math.max(0, counter);
    }

    public static TCWarpSyncPayload from(TCPlayerWarp warp) {
        return new TCWarpSyncPayload(
                warp.get(TCWarpType.PERMANENT),
                warp.get(TCWarpType.NORMAL),
                warp.get(TCWarpType.TEMPORARY),
                warp.getCounter()
        );
    }

    public TCPlayerWarp toWarp() {
        TCPlayerWarp warp = new TCPlayerWarp();
        warp.set(TCWarpType.PERMANENT, permanent);
        warp.set(TCWarpType.NORMAL, normal);
        warp.set(TCWarpType.TEMPORARY, temporary);
        warp.setCounter(counter);
        return warp;
    }

    @Override
    public Type<TCWarpSyncPayload> type() {
        return TYPE;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(TCPlayerWarp.MAX_WARP, value));
    }
}
