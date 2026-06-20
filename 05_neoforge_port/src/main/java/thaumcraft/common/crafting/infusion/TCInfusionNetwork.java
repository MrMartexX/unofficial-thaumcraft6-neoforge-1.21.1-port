package thaumcraft.common.crafting.infusion;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;

public final class TCInfusionNetwork {
    private static final String NETWORK_VERSION = "1";
    private static final double LEGACY_FX_RANGE_SQR = 32.0D * 32.0D;

    private TCInfusionNetwork() {
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(NETWORK_VERSION);
        registrar.playToClient(
                TCInfusionSourcePayload.TYPE,
                TCInfusionSourcePayload.STREAM_CODEC,
                (payload, context) -> TCInfusionClientFXCache.accept(payload)
        );
        registrar.playToClient(
                TCInfusionEssentiaSourcePayload.TYPE,
                TCInfusionEssentiaSourcePayload.STREAM_CODEC,
                (payload, context) -> TCInfusionClientFXCache.accept(payload)
        );
    }

    public static void sendComponentSource(TCInfusionMatrixBlockEntity matrix, BlockPos componentPos) {
        if (!(matrix.getLevel() instanceof ServerLevel level) || componentPos == null) {
            return;
        }
        sendToNearby(level, matrix.getBlockPos(), new TCInfusionSourcePayload(
                matrix.getBlockPos(),
                componentPos,
                0
        ));
    }

    public static void sendEssentiaSource(
            TCInfusionMatrixBlockEntity matrix,
            BlockPos sourcePos,
            int color,
            int extension
    ) {
        if (!(matrix.getLevel() instanceof ServerLevel level) || sourcePos == null) {
            return;
        }
        sendToNearby(level, matrix.getBlockPos(), new TCInfusionEssentiaSourcePayload(
                matrix.getBlockPos(),
                sourcePos,
                color,
                extension
        ));
    }

    private static void sendToNearby(ServerLevel level, BlockPos origin, CustomPacketPayload payload) {
        double x = origin.getX() + 0.5D;
        double y = origin.getY() + 0.5D;
        double z = origin.getZ() + 0.5D;
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(x, y, z) <= LEGACY_FX_RANGE_SQR) {
                PacketDistributor.sendToPlayer(player, payload);
            }
        }
    }
}
