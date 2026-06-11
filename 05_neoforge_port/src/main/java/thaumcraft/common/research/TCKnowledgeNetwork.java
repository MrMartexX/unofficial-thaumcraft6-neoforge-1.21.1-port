package thaumcraft.common.research;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class TCKnowledgeNetwork {
    private static final String NETWORK_VERSION = "1";

    private TCKnowledgeNetwork() {
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(NETWORK_VERSION)
                .playToClient(
                        TCKnowledgeSyncPayload.TYPE,
                        TCKnowledgeSyncPayload.STREAM_CODEC,
                        TCKnowledgeNetwork::handleKnowledgeSync
                )
                .playToClient(
                        TCKnowledgeGainPayload.TYPE,
                        TCKnowledgeGainPayload.STREAM_CODEC,
                        TCKnowledgeNetwork::handleKnowledgeGain
                );
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToPlayer(player);
        }
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToPlayer(player);
        }
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToPlayer(player);
        }
    }

    static void syncToPlayer(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, TCKnowledgeSyncPayload.from(TCPlayerKnowledgeStore.get(player)));
    }

    public static void sendKnowledgeGain(ServerPlayer player, TCKnowledgeType type, String category) {
        if (player == null || type == null) {
            return;
        }

        PacketDistributor.sendToPlayer(player, new TCKnowledgeGainPayload(type, category));
    }

    private static void handleKnowledgeSync(
            TCKnowledgeSyncPayload payload,
            net.neoforged.neoforge.network.handling.IPayloadContext context
    ) {
        TCKnowledgeClientCache.accept(payload);
    }

    private static void handleKnowledgeGain(
            TCKnowledgeGainPayload payload,
            net.neoforged.neoforge.network.handling.IPayloadContext context
    ) {
        TCKnowledgeClientCache.accept(payload);
    }
}
