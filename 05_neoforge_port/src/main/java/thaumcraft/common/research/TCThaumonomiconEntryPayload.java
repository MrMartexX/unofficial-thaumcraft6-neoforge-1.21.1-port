package thaumcraft.common.research;

import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCThaumonomiconEntryPayload(
        boolean accepted,
        String resultKey,
        String researchKey,
        Optional<TCThaumonomiconEntryView> entry
) implements CustomPacketPayload {
    public static final Type<TCThaumonomiconEntryPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "thaumonomicon_entry")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TCThaumonomiconEntryPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TCThaumonomiconEntryPayload decode(RegistryFriendlyByteBuf buffer) {
                    return TCThaumonomiconCodec.readEntryPayload(buffer);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TCThaumonomiconEntryPayload payload) {
                    TCThaumonomiconCodec.writeEntryPayload(buffer, payload);
                }
            };

    public TCThaumonomiconEntryPayload {
        resultKey = resultKey == null || resultKey.isBlank() ? "unknown" : resultKey;
        researchKey = TCPlayerKnowledge.baseResearchKey(researchKey);
        entry = entry == null ? Optional.empty() : entry;
    }

    @Override
    public Type<TCThaumonomiconEntryPayload> type() {
        return TYPE;
    }
}
