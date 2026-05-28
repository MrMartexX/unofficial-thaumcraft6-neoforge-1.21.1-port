package thaumcraft.common.research.theorycraft;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.menu.TCResearchTableMenu;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;

public final class TCResearchTableNetwork {
    private static final String NETWORK_VERSION = "1";

    private TCResearchTableNetwork() {
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(NETWORK_VERSION);
        registrar.playToServer(
                TCResearchTableActionPayload.TYPE,
                TCResearchTableActionPayload.STREAM_CODEC,
                TCResearchTableNetwork::handleAction
        );
        registrar.playToClient(
                TCResearchTableSyncPayload.TYPE,
                TCResearchTableSyncPayload.STREAM_CODEC,
                TCResearchTableNetwork::handleSync
        );
        registrar.playToClient(
                TCResearchTableActionResultPayload.TYPE,
                TCResearchTableActionResultPayload.STREAM_CODEC,
                TCResearchTableNetwork::handleActionResult
        );
    }

    public static void syncToPlayer(ServerPlayer player, TCResearchTableBlockEntity table) {
        PacketDistributor.sendToPlayer(player, table.toSyncPayload());
    }

    static void sendActionResult(
            ServerPlayer player,
            TCResearchTableBlockEntity table,
            int actionId,
            boolean accepted,
            String resultKey
    ) {
        PacketDistributor.sendToPlayer(
                player,
                TCResearchTableActionResultPayload.fromTable(table, actionId, accepted, resultKey)
        );
    }

    private static void handleAction(TCResearchTableActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!(player.containerMenu instanceof TCResearchTableMenu menu)) {
                return;
            }
            TCResearchTableActions.handle(player, menu, payload);
        });
    }

    private static void handleSync(TCResearchTableSyncPayload payload, IPayloadContext context) {
        TCResearchTableClientCache.accept(payload);
    }

    private static void handleActionResult(TCResearchTableActionResultPayload payload, IPayloadContext context) {
        TCResearchTableClientCache.accept(payload);
    }
}
