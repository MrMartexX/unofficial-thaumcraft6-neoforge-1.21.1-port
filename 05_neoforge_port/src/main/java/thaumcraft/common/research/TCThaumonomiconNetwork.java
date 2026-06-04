package thaumcraft.common.research;

import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class TCThaumonomiconNetwork {
    private static final String NETWORK_VERSION = "3";

    private TCThaumonomiconNetwork() {
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(NETWORK_VERSION);
        registrar.playToServer(
                TCThaumonomiconIndexRequestPayload.TYPE,
                TCThaumonomiconIndexRequestPayload.STREAM_CODEC,
                TCThaumonomiconNetwork::handleIndexRequest
        );
        registrar.playToServer(
                TCThaumonomiconEntryRequestPayload.TYPE,
                TCThaumonomiconEntryRequestPayload.STREAM_CODEC,
                TCThaumonomiconNetwork::handleEntryRequest
        );
        registrar.playToServer(
                TCThaumonomiconActionPayload.TYPE,
                TCThaumonomiconActionPayload.STREAM_CODEC,
                TCThaumonomiconNetwork::handleAction
        );
        registrar.playToClient(
                TCThaumonomiconIndexPayload.TYPE,
                TCThaumonomiconIndexPayload.STREAM_CODEC,
                TCThaumonomiconNetwork::handleIndex
        );
        registrar.playToClient(
                TCThaumonomiconEntryPayload.TYPE,
                TCThaumonomiconEntryPayload.STREAM_CODEC,
                TCThaumonomiconNetwork::handleEntry
        );
    }

    public static void sendIndex(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, TCThaumonomiconService.buildIndex(player));
    }

    public static void openFor(ServerPlayer player) {
        TCPlayerKnowledgeStore.sync(player);
        TCThaumonomiconIndexPayload index = TCThaumonomiconService.buildIndex(player);
        PacketDistributor.sendToPlayer(
                player,
                new TCThaumonomiconIndexPayload(index.categories(), index.entries(), true)
        );
    }

    public static void sendEntry(ServerPlayer player, String researchKey, boolean accepted, String resultKey) {
        Optional<TCThaumonomiconEntryView> entry = TCThaumonomiconService.buildEntry(player, researchKey);
        boolean finalAccepted = accepted && entry.isPresent();
        PacketDistributor.sendToPlayer(
                player,
                new TCThaumonomiconEntryPayload(
                        finalAccepted,
                        accepted && entry.isEmpty() ? "not_visible_or_missing" : resultKey,
                        researchKey,
                        entry
                )
        );
    }

    private static void handleIndexRequest(TCThaumonomiconIndexRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                sendIndex(player);
            }
        });
    }

    private static void handleEntryRequest(TCThaumonomiconEntryRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            boolean visible = TCResearchManager.isResearchVisible(player, payload.researchKey());
            sendEntry(
                    player,
                    payload.researchKey(),
                    visible,
                    visible ? "entry_loaded" : "not_visible_or_missing"
            );
        });
    }

    private static void handleAction(TCThaumonomiconActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!TCResearchManager.isResearchVisible(player, payload.researchKey())) {
                sendEntry(player, payload.researchKey(), false, "not_visible_or_missing");
                return;
            }

            boolean accepted;
            String resultKey;
            switch (payload.actionId()) {
                case TCThaumonomiconActionPayload.ADVANCE_CURRENT_STAGE -> {
                    // Legacy GuiResearchPage sends first=false, checks=true, noFlags=true.
                    accepted = TCResearchManager.completeCurrentStageWithChecks(
                            player,
                            payload.researchKey(),
                            true,
                            true
                    );
                    resultKey = accepted ? "stage_advanced" : "requirements_not_met";
                }
                case TCThaumonomiconActionPayload.START_RESEARCH -> {
                    accepted = TCResearchManager.startResearchFromBrowser(player, payload.researchKey());
                    resultKey = accepted ? "research_started" : "research_not_unlockable";
                }
                case TCThaumonomiconActionPayload.ACKNOWLEDGE_ENTRY -> {
                    accepted = TCResearchManager.acknowledgeResearchEntry(player, payload.researchKey());
                    resultKey = accepted ? "entry_acknowledged" : "entry_not_known";
                }
                default -> {
                    sendEntry(player, payload.researchKey(), false, "unknown_action");
                    return;
                }
            }

            if (accepted) {
                sendIndex(player);
            }
            sendEntry(
                    player,
                    payload.researchKey(),
                    accepted,
                    resultKey
            );
        });
    }

    private static void handleIndex(TCThaumonomiconIndexPayload payload, IPayloadContext context) {
        TCThaumonomiconClientCache.accept(payload);
    }

    private static void handleEntry(TCThaumonomiconEntryPayload payload, IPayloadContext context) {
        TCThaumonomiconClientCache.accept(payload);
    }
}
