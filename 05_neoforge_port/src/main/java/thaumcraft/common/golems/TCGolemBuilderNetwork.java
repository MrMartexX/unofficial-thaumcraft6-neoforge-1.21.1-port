package thaumcraft.common.golems;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.menu.TCGolemBuilderMenu;
import thaumcraft.common.tiles.crafting.TCGolemBuilderBlockEntity;

public final class TCGolemBuilderNetwork {
    private static final String NETWORK_VERSION = "1";

    private TCGolemBuilderNetwork() {
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(NETWORK_VERSION).playToServer(
                TCGolemBuilderCraftPayload.TYPE,
                TCGolemBuilderCraftPayload.STREAM_CODEC,
                TCGolemBuilderNetwork::handleCraft
        );
    }

    private static void handleCraft(TCGolemBuilderCraftPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!(player.containerMenu instanceof TCGolemBuilderMenu menu)) {
                return;
            }
            TCGolemBuilderBlockEntity builder = menu.getBlockEntity();
            if (builder == null || !builder.stillValid(player)) {
                return;
            }
            builder.startCraft(payload.golemProps(), player);
            menu.broadcastChanges();
        });
    }
}
