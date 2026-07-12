package thaumcraft.common.research;

import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;

public record TCThaumonomiconDrilldownPayload(
        boolean accepted,
        String resultKey,
        ItemStack requestedStack,
        Optional<TCResearchPageBookmark> bookmark,
        int pageIndex
) implements CustomPacketPayload {
    public static final Type<TCThaumonomiconDrilldownPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "thaumonomicon_drilldown")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCThaumonomiconDrilldownPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCThaumonomiconDrilldownPayload decode(RegistryFriendlyByteBuf buffer) {
                    return TCThaumonomiconCodec.readDrilldownPayload(buffer);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCThaumonomiconDrilldownPayload payload) {
                    TCThaumonomiconCodec.writeDrilldownPayload(buffer, payload);
                }
            };

    public TCThaumonomiconDrilldownPayload {
        resultKey = resultKey == null || resultKey.isBlank() ? "unknown" : resultKey;
        requestedStack = requestedStack == null ? ItemStack.EMPTY : requestedStack.copyWithCount(1);
        bookmark = bookmark == null ? Optional.empty() : bookmark;
        int requestedPageIndex = pageIndex;
        pageIndex = bookmark.isPresent()
                ? Math.max(0, Math.min(requestedPageIndex, Math.max(0, bookmark.get().pages().size() - 1)))
                : 0;
    }

    @Override
    public Type<TCThaumonomiconDrilldownPayload> type() {
        return TYPE;
    }
}
