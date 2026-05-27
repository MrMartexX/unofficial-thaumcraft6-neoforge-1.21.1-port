package thaumcraft.common.research.theorycraft;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCResearchTableActionPayload(int actionId, int choiceIndex) implements CustomPacketPayload {
    public static final int ACTION_START_THEORY = 1;
    public static final int ACTION_DRAW_CARDS = 2;
    public static final int ACTION_COMMIT_SELECTED = 3;
    public static final int ACTION_SELECT_CARD = 4;
    public static final int ACTION_COMPLETE_THEORY = 7;
    public static final int ACTION_SCRAP_THEORY = 9;

    public static final Type<TCResearchTableActionPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_table_action")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TCResearchTableActionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            TCResearchTableActionPayload::actionId,
            ByteBufCodecs.INT,
            TCResearchTableActionPayload::choiceIndex,
            TCResearchTableActionPayload::new
    );

    @Override
    public Type<TCResearchTableActionPayload> type() {
        return TYPE;
    }
}
