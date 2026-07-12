package thaumcraft.common.research;

import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class TCThaumonomiconNetwork {
    private static final String NETWORK_VERSION = "7";

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
        registrar.playToServer(
                TCThaumonomiconDrilldownRequestPayload.TYPE,
                TCThaumonomiconDrilldownRequestPayload.STREAM_CODEC,
                TCThaumonomiconNetwork::handleDrilldownRequest
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
        registrar.playToClient(
                TCThaumonomiconDrilldownPayload.TYPE,
                TCThaumonomiconDrilldownPayload.STREAM_CODEC,
                TCThaumonomiconNetwork::handleDrilldown
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
                new TCThaumonomiconIndexPayload(index.categories(), index.entries(), index.revision(), true)
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
            if (!TCThaumonomiconService.isRevisionCurrent(player, payload.clientRevision())) {
                sendIndex(player);
                sendEntry(player, payload.researchKey(), false, "stale_revision");
                return;
            }
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
            ActionResult result = processAction(player, payload);
            if (result.refreshIndex()) {
                sendIndex(player);
            }
            sendEntry(
                    player,
                    payload.researchKey(),
                    result.accepted(),
                    result.resultKey()
            );
        });
    }

    private static void handleDrilldownRequest(TCThaumonomiconDrilldownRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            DrilldownResult result = processDrilldown(player, payload);
            if (result.refreshIndex()) {
                sendIndex(player);
            }
            PacketDistributor.sendToPlayer(player, new TCThaumonomiconDrilldownPayload(
                    result.accepted(),
                    result.resultKey(),
                    payload.stack(),
                    result.result().map(TCResearchPageDrilldownResult::bookmark),
                    result.result().map(TCResearchPageDrilldownResult::pageIndex).orElse(0)
            ));
        });
    }

    static ActionResult processAction(ServerPlayer player, TCThaumonomiconActionPayload payload) {
        if (!TCThaumonomiconService.isRevisionCurrent(player, payload.clientRevision())) {
            return new ActionResult(false, "stale_revision", true);
        }
        if (!TCResearchManager.isResearchVisible(player, payload.researchKey())) {
            return new ActionResult(false, "not_visible_or_missing", false);
        }

        return switch (payload.actionId()) {
            case TCThaumonomiconActionPayload.ADVANCE_CURRENT_STAGE -> {
                // Legacy GuiResearchPage sends first=false, checks=true, noFlags=true.
                boolean accepted = TCResearchManager.completeCurrentStageWithChecks(
                        player,
                        payload.researchKey(),
                        true,
                        true
                );
                yield new ActionResult(accepted, accepted ? "stage_advanced" : "requirements_not_met", accepted);
            }
            case TCThaumonomiconActionPayload.START_RESEARCH -> {
                boolean accepted = TCResearchManager.startResearchFromBrowser(player, payload.researchKey());
                yield new ActionResult(accepted, accepted ? "research_started" : "research_not_unlockable", accepted);
            }
            case TCThaumonomiconActionPayload.ACKNOWLEDGE_ENTRY -> {
                boolean accepted = TCResearchManager.acknowledgeResearchEntry(player, payload.researchKey());
                yield new ActionResult(accepted, accepted ? "entry_acknowledged" : "entry_not_known", accepted);
            }
            default -> new ActionResult(false, "unknown_action", false);
        };
    }

    static DrilldownResult processDrilldown(ServerPlayer player, TCThaumonomiconDrilldownRequestPayload payload) {
        if (!TCThaumonomiconService.isRevisionCurrent(player, payload.clientRevision())) {
            return new DrilldownResult(false, "stale_revision", true, Optional.empty());
        }
        if (payload.stack().isEmpty()) {
            return new DrilldownResult(false, "recipe_unavailable", false, Optional.empty());
        }
        Optional<TCResearchPageDrilldownResult> result = TCResearchPageCatalogManager.findRecipeDrilldown(
                player,
                payload.stack()
        );
        return result
                .map(value -> new DrilldownResult(true, "recipe_loaded", false, Optional.of(value)))
                .orElseGet(() -> new DrilldownResult(false, "recipe_unavailable", false, Optional.empty()));
    }

    private static void handleIndex(TCThaumonomiconIndexPayload payload, IPayloadContext context) {
        TCThaumonomiconClientCache.accept(payload);
    }

    private static void handleEntry(TCThaumonomiconEntryPayload payload, IPayloadContext context) {
        TCThaumonomiconClientCache.accept(payload);
    }

    private static void handleDrilldown(TCThaumonomiconDrilldownPayload payload, IPayloadContext context) {
        TCThaumonomiconClientCache.accept(payload);
    }

    record ActionResult(boolean accepted, String resultKey, boolean refreshIndex) {
    }

    record DrilldownResult(
            boolean accepted,
            String resultKey,
            boolean refreshIndex,
            Optional<TCResearchPageDrilldownResult> result
    ) {
    }
}
