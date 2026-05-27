package thaumcraft.common.research.theorycraft;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCResearchTableSyncPayload(BlockPos pos, boolean hasData, CompoundTag data) implements CustomPacketPayload {
    public static final Type<TCResearchTableSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_table_sync")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TCResearchTableSyncPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public TCResearchTableSyncPayload decode(RegistryFriendlyByteBuf buffer) {
            BlockPos pos = buffer.readBlockPos();
            boolean hasData = buffer.readBoolean();
            CompoundTag data = hasData ? buffer.readNbt() : new CompoundTag();
            return new TCResearchTableSyncPayload(pos, hasData, data == null ? new CompoundTag() : data);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, TCResearchTableSyncPayload payload) {
            buffer.writeBlockPos(payload.pos);
            buffer.writeBoolean(payload.hasData);
            if (payload.hasData) {
                buffer.writeNbt(payload.data);
            }
        }
    };

    public TCResearchTableSyncPayload {
        data = data == null ? new CompoundTag() : data.copy();
    }

    @Override
    public Type<TCResearchTableSyncPayload> type() {
        return TYPE;
    }
}
