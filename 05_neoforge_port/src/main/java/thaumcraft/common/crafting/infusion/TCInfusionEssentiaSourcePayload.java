package thaumcraft.common.crafting.infusion;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

/** Modern wire equivalent of legacy {@code PacketFXEssentiaSource}. */
public record TCInfusionEssentiaSourcePayload(
        BlockPos matrixPos,
        BlockPos sourcePos,
        int color,
        int extension
) implements CustomPacketPayload {
    public static final Type<TCInfusionEssentiaSourcePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "infusion_essentia_source_fx")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCInfusionEssentiaSourcePayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCInfusionEssentiaSourcePayload decode(RegistryFriendlyByteBuf buffer) {
                    return new TCInfusionEssentiaSourcePayload(
                            buffer.readBlockPos(),
                            buffer.readBlockPos(),
                            buffer.readInt(),
                            buffer.readVarInt()
                    );
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCInfusionEssentiaSourcePayload payload) {
                    buffer.writeBlockPos(payload.matrixPos());
                    buffer.writeBlockPos(payload.sourcePos());
                    buffer.writeInt(payload.color());
                    buffer.writeVarInt(payload.extension());
                }
            };

    public TCInfusionEssentiaSourcePayload {
        if (matrixPos == null || sourcePos == null) {
            throw new IllegalArgumentException("Infusion essentia FX positions cannot be null");
        }
        extension = Math.max(0, extension);
    }

    @Override
    public Type<TCInfusionEssentiaSourcePayload> type() {
        return TYPE;
    }
}
