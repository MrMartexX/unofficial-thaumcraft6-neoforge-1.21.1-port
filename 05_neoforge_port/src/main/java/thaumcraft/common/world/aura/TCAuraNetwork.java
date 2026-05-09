package thaumcraft.common.world.aura;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class TCAuraNetwork {
    private static final String NETWORK_VERSION = "1";

    private TCAuraNetwork() {
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(NETWORK_VERSION)
                .playToClient(TCAuraSyncPayload.TYPE, TCAuraSyncPayload.STREAM_CODEC, TCAuraNetwork::handleAuraSync);
    }

    public static boolean sendAuraToPlayer(ServerPlayer player, BlockPos pos) {
        return sendAuraToPlayer(player, new ChunkPos(pos));
    }

    public static boolean sendAuraToPlayer(ServerPlayer player, ChunkPos chunkPos) {
        AuraChunk chunk = AuraHandler.ensureAuraChunk(player.serverLevel(), chunkPos);
        PacketDistributor.sendToPlayer(player, TCAuraSyncPayload.from(chunk));
        return true;
    }

    private static void handleAuraSync(TCAuraSyncPayload payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        TCAuraClientCache.accept(payload);
    }
}
