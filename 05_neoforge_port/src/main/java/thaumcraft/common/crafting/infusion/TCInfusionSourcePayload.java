package thaumcraft.common.crafting.infusion;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

/** Modern wire equivalent of legacy {@code PacketFXInfusionSource}. */
public record TCInfusionSourcePayload(BlockPos matrixPos, BlockPos targetPos, int color)
        implements CustomPacketPayload {
    public static final Type<TCInfusionSourcePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "infusion_source_fx")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCInfusionSourcePayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCInfusionSourcePayload decode(RegistryFriendlyByteBuf buffer) {
                    return new TCInfusionSourcePayload(buffer.readBlockPos(), buffer.readBlockPos(), buffer.readInt());
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCInfusionSourcePayload payload) {
                    buffer.writeBlockPos(payload.matrixPos());
                    buffer.writeBlockPos(payload.targetPos());
                    buffer.writeInt(payload.color());
                }
            };

    public TCInfusionSourcePayload {
        if (matrixPos == null || targetPos == null) {
            throw new IllegalArgumentException("Infusion source FX positions cannot be null");
        }
    }

    @Override
    public Type<TCInfusionSourcePayload> type() {
        return TYPE;
    }
}
