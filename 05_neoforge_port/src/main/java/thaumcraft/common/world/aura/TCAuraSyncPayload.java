package thaumcraft.common.world.aura;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCAuraSyncPayload(int chunkX, int chunkZ, int base, float vis, float flux) implements CustomPacketPayload {
    public static final Type<TCAuraSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "aura_sync")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TCAuraSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            TCAuraSyncPayload::chunkX,
            ByteBufCodecs.INT,
            TCAuraSyncPayload::chunkZ,
            ByteBufCodecs.INT,
            TCAuraSyncPayload::base,
            ByteBufCodecs.FLOAT,
            TCAuraSyncPayload::vis,
            ByteBufCodecs.FLOAT,
            TCAuraSyncPayload::flux,
            TCAuraSyncPayload::new
    );

    static TCAuraSyncPayload from(AuraChunk chunk) {
        return new TCAuraSyncPayload(
                chunk.getChunkX(),
                chunk.getChunkZ(),
                chunk.getBase(),
                chunk.getVis(),
                chunk.getFlux()
        );
    }

    @Override
    public Type<TCAuraSyncPayload> type() {
        return TYPE;
    }
}
