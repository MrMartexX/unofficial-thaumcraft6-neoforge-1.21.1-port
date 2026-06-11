package thaumcraft.common.research.theorycraft;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;

public record TCResearchTableActionResultPayload(
        BlockPos pos,
        int actionId,
        boolean accepted,
        String resultKey,
        boolean hasData,
        CompoundTag data
) implements CustomPacketPayload {
    private static final int MAX_RESULT_KEY_LENGTH = 128;

    public static final Type<TCResearchTableActionResultPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_table_action_result")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TCResearchTableActionResultPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public TCResearchTableActionResultPayload decode(RegistryFriendlyByteBuf buffer) {
            BlockPos pos = buffer.readBlockPos();
            int actionId = buffer.readVarInt();
            boolean accepted = buffer.readBoolean();
            String resultKey = buffer.readUtf(MAX_RESULT_KEY_LENGTH);
            boolean hasData = buffer.readBoolean();
            CompoundTag data = hasData ? buffer.readNbt() : new CompoundTag();
            return new TCResearchTableActionResultPayload(pos, actionId, accepted, resultKey, hasData, data);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, TCResearchTableActionResultPayload payload) {
            buffer.writeBlockPos(payload.pos);
            buffer.writeVarInt(payload.actionId);
            buffer.writeBoolean(payload.accepted);
            buffer.writeUtf(payload.resultKey, MAX_RESULT_KEY_LENGTH);
            buffer.writeBoolean(payload.hasData);
            if (payload.hasData) {
                buffer.writeNbt(payload.data);
            }
        }
    };

    public TCResearchTableActionResultPayload {
        resultKey = resultKey == null || resultKey.isBlank() ? "unknown" : resultKey;
        data = data == null ? new CompoundTag() : data.copy();
    }

    public static TCResearchTableActionResultPayload fromTable(
            TCResearchTableBlockEntity table,
            int actionId,
            boolean accepted,
            String resultKey
    ) {
        TCResearchTableSyncPayload sync = table.toSyncPayload();
        return new TCResearchTableActionResultPayload(
                sync.pos(),
                actionId,
                accepted,
                resultKey,
                sync.hasData(),
                sync.data()
        );
    }

    public TCResearchTableSyncPayload toTableSyncPayload() {
        return new TCResearchTableSyncPayload(pos, hasData, data);
    }

    @Override
    public Type<TCResearchTableActionResultPayload> type() {
        return TYPE;
    }
}
