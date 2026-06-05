package thaumcraft.common.research;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCThaumonomiconIndexPayload(
        List<TCThaumonomiconCategoryView> categories,
        List<TCThaumonomiconResearchView> entries,
        int revision,
        boolean openScreen
) implements CustomPacketPayload {
    public static final Type<TCThaumonomiconIndexPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "thaumonomicon_index")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCThaumonomiconIndexPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCThaumonomiconIndexPayload decode(RegistryFriendlyByteBuf buffer) {
                    return TCThaumonomiconCodec.readIndex(buffer);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCThaumonomiconIndexPayload payload) {
                    TCThaumonomiconCodec.writeIndex(buffer, payload);
                }
            };

    public TCThaumonomiconIndexPayload {
        categories = List.copyOf(categories);
        entries = List.copyOf(entries);
    }

    public TCThaumonomiconIndexPayload(
            List<TCThaumonomiconCategoryView> categories,
            List<TCThaumonomiconResearchView> entries,
            int revision
    ) {
        this(categories, entries, revision, false);
    }

    public TCThaumonomiconIndexPayload(
            List<TCThaumonomiconCategoryView> categories,
            List<TCThaumonomiconResearchView> entries,
            boolean openScreen
    ) {
        this(categories, entries, 0, openScreen);
    }

    public TCThaumonomiconIndexPayload(
            List<TCThaumonomiconCategoryView> categories,
            List<TCThaumonomiconResearchView> entries
    ) {
        this(categories, entries, 0, false);
    }

    @Override
    public Type<TCThaumonomiconIndexPayload> type() {
        return TYPE;
    }
}
