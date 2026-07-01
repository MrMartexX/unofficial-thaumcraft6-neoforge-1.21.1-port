package thaumcraft.common.items.casters;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public record TCFocalManipulatorDesignPayload(
        BlockPos pos,
        String encodedNodes,
        String focusName,
        boolean startCraft
) implements CustomPacketPayload {
    private static final int MAX_ENCODED_NODES_LENGTH = 4096;
    private static final int MAX_FOCUS_NAME_LENGTH = 64;

    public static final Type<TCFocalManipulatorDesignPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "focal_manipulator_design")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TCFocalManipulatorDesignPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public TCFocalManipulatorDesignPayload decode(RegistryFriendlyByteBuf buffer) {
            return new TCFocalManipulatorDesignPayload(
                    buffer.readBlockPos(),
                    buffer.readUtf(MAX_ENCODED_NODES_LENGTH),
                    buffer.readUtf(MAX_FOCUS_NAME_LENGTH),
                    buffer.readBoolean()
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, TCFocalManipulatorDesignPayload payload) {
            buffer.writeBlockPos(payload.pos());
            buffer.writeUtf(payload.encodedNodes(), MAX_ENCODED_NODES_LENGTH);
            buffer.writeUtf(payload.focusName(), MAX_FOCUS_NAME_LENGTH);
            buffer.writeBoolean(payload.startCraft());
        }
    };

    public TCFocalManipulatorDesignPayload {
        pos = pos == null ? BlockPos.ZERO : pos;
        encodedNodes = encodedNodes == null ? "" : encodedNodes.trim();
        if (encodedNodes.length() > MAX_ENCODED_NODES_LENGTH) {
            encodedNodes = encodedNodes.substring(0, MAX_ENCODED_NODES_LENGTH);
        }
        focusName = focusName == null ? "" : focusName.trim();
        if (focusName.length() > MAX_FOCUS_NAME_LENGTH) {
            focusName = focusName.substring(0, MAX_FOCUS_NAME_LENGTH);
        }
    }

    @Override
    public Type<TCFocalManipulatorDesignPayload> type() {
        return TYPE;
    }
}
