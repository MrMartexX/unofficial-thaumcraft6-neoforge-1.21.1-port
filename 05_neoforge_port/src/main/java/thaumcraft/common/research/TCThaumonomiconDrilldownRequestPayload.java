package thaumcraft.common.research;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;

public record TCThaumonomiconDrilldownRequestPayload(
        ItemStack stack,
        int clientRevision
) implements CustomPacketPayload {
    public static final Type<TCThaumonomiconDrilldownRequestPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "thaumonomicon_drilldown_request")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCThaumonomiconDrilldownRequestPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCThaumonomiconDrilldownRequestPayload decode(RegistryFriendlyByteBuf buffer) {
                    return new TCThaumonomiconDrilldownRequestPayload(
                            ItemStack.STREAM_CODEC.decode(buffer),
                            buffer.readVarInt()
                    );
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCThaumonomiconDrilldownRequestPayload payload) {
                    ItemStack.STREAM_CODEC.encode(buffer, payload.stack());
                    buffer.writeVarInt(payload.clientRevision());
                }
            };

    public TCThaumonomiconDrilldownRequestPayload {
        stack = stack == null ? ItemStack.EMPTY : stack.copyWithCount(1);
    }

    @Override
    public Type<TCThaumonomiconDrilldownRequestPayload> type() {
        return TYPE;
    }
}
