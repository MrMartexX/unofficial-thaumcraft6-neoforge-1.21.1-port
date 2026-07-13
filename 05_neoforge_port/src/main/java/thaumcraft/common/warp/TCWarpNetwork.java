package thaumcraft.common.warp;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class TCWarpNetwork {
    private static final String NETWORK_VERSION = "1";

    private TCWarpNetwork() {
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(NETWORK_VERSION)
                .playToClient(
                        TCWarpSyncPayload.TYPE,
                        TCWarpSyncPayload.STREAM_CODEC,
                        TCWarpNetwork::handleWarpSync
                )
                .playToClient(
                        TCWarpMessagePayload.TYPE,
                        TCWarpMessagePayload.STREAM_CODEC,
                        TCWarpNetwork::handleWarpMessage
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

    public static void syncToPlayer(ServerPlayer player) {
        if (player != null) {
            PacketDistributor.sendToPlayer(player, TCWarpSyncPayload.from(TCPlayerWarpStore.get(player)));
        }
    }

    static void sendWarpMessage(ServerPlayer player, TCWarpType type, int change) {
        if (player != null && change != 0) {
            PacketDistributor.sendToPlayer(player, new TCWarpMessagePayload(type, change));
        }
    }

    private static void handleWarpSync(
            TCWarpSyncPayload payload,
            net.neoforged.neoforge.network.handling.IPayloadContext context
    ) {
        TCWarpClientCache.accept(payload);
    }

    private static void handleWarpMessage(
            TCWarpMessagePayload payload,
            net.neoforged.neoforge.network.handling.IPayloadContext context
    ) {
        TCWarpClientCache.accept(payload);
    }
}
