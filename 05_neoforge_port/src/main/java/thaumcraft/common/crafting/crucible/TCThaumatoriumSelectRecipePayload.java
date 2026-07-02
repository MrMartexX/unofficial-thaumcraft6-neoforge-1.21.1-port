package thaumcraft.common.crafting.crucible;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCThaumatoriumSelectRecipePayload(BlockPos pos, ResourceLocation recipeId) implements CustomPacketPayload {
    public static final Type<TCThaumatoriumSelectRecipePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "thaumatorium_select_recipe")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCThaumatoriumSelectRecipePayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCThaumatoriumSelectRecipePayload decode(RegistryFriendlyByteBuf buffer) {
                    return new TCThaumatoriumSelectRecipePayload(buffer.readBlockPos(), buffer.readResourceLocation());
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCThaumatoriumSelectRecipePayload payload) {
                    buffer.writeBlockPos(payload.pos());
                    buffer.writeResourceLocation(payload.recipeId());
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
