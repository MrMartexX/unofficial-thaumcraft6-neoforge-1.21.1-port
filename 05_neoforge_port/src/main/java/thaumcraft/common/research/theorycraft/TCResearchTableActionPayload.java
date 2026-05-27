package thaumcraft.common.research.theorycraft;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCResearchTableActionPayload(int actionId, int choiceIndex, List<String> aidKeys) implements CustomPacketPayload {
    public static final int ACTION_START_THEORY = 1;
    public static final int ACTION_DRAW_CARDS = 2;
    public static final int ACTION_COMMIT_SELECTED = 3;
    public static final int ACTION_SELECT_CARD = 4;
    public static final int ACTION_COMPLETE_THEORY = 7;
    public static final int ACTION_SCRAP_THEORY = 9;
    private static final int MAX_AID_KEYS = 64;

    public static final Type<TCResearchTableActionPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_table_action")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TCResearchTableActionPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public TCResearchTableActionPayload decode(RegistryFriendlyByteBuf buffer) {
            int actionId = buffer.readVarInt();
            int choiceIndex = buffer.readVarInt();
            int aidCount = buffer.readVarInt();
            if (aidCount < 0 || aidCount > MAX_AID_KEYS) {
                throw new IllegalArgumentException("Invalid research table aid key count: " + aidCount);
            }

            ArrayList<String> aidKeys = new ArrayList<>(aidCount);
            for (int index = 0; index < aidCount; index++) {
                aidKeys.add(buffer.readUtf());
            }
            return new TCResearchTableActionPayload(actionId, choiceIndex, aidKeys);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, TCResearchTableActionPayload payload) {
            buffer.writeVarInt(payload.actionId());
            buffer.writeVarInt(payload.choiceIndex());
            buffer.writeVarInt(payload.aidKeys().size());
            for (String aidKey : payload.aidKeys()) {
                buffer.writeUtf(aidKey);
            }
        }
    };

    public TCResearchTableActionPayload(int actionId, int choiceIndex) {
        this(actionId, choiceIndex, List.of());
    }

    public TCResearchTableActionPayload {
        aidKeys = aidKeys == null ? List.of() : List.copyOf(aidKeys);
    }

    @Override
    public Type<TCResearchTableActionPayload> type() {
        return TYPE;
    }
}
