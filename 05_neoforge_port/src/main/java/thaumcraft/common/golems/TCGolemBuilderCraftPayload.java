package thaumcraft.common.golems;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCGolemBuilderCraftPayload(BlockPos pos, long golemProps) implements CustomPacketPayload {
    public static final Type<TCGolemBuilderCraftPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "golem_builder_craft")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCGolemBuilderCraftPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCGolemBuilderCraftPayload decode(RegistryFriendlyByteBuf buffer) {
                    return new TCGolemBuilderCraftPayload(buffer.readBlockPos(), buffer.readLong());
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCGolemBuilderCraftPayload payload) {
                    buffer.writeBlockPos(payload.pos());
                    buffer.writeLong(payload.golemProps());
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
