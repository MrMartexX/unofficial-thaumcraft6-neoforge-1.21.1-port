package thaumcraft.common.golems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;

public final class TCLogisticsPayloads {
    private TCLogisticsPayloads() {
    }

    public record Request(BlockPos pos, Direction side, ItemStack stack, int count) implements CustomPacketPayload {
        public static final Type<Request> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "logistics_request")
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Request> STREAM_CODEC = StreamCodec.of(
                (buffer, msg) -> {
                    if (msg.pos == null || msg.side == null) {
                        buffer.writeBoolean(false);
                    } else {
                        buffer.writeBoolean(true);
                        buffer.writeBlockPos(msg.pos);
                        buffer.writeByte(msg.side.ordinal());
                    }
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, msg.stack);
                    buffer.writeInt(msg.count);
                },
                buffer -> {
                    BlockPos pos = null;
                    Direction side = null;
                    if (buffer.readBoolean()) {
                        pos = buffer.readBlockPos();
                        byte rawSide = buffer.readByte();
                        Direction[] dirs = Direction.values();
                        side = (rawSide >= 0 && rawSide < dirs.length) ? dirs[rawSide] : null;
                    }
                    ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
                    int count = buffer.readInt();
                    return new Request(pos, side, stack, count);
                }
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record Search(String text) implements CustomPacketPayload {
        public static final Type<Search> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "logistics_search")
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Search> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                Search::text,
                Search::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
