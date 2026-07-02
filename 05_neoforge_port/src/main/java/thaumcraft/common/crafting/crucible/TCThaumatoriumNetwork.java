package thaumcraft.common.crafting.crucible;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.menu.TCThaumatoriumMenu;
import thaumcraft.common.tiles.crafting.TCThaumatoriumBlockEntity;

public final class TCThaumatoriumNetwork {
    private static final String NETWORK_VERSION = "1";

    private TCThaumatoriumNetwork() {
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(NETWORK_VERSION).playToServer(
                TCThaumatoriumSelectRecipePayload.TYPE,
                TCThaumatoriumSelectRecipePayload.STREAM_CODEC,
                TCThaumatoriumNetwork::handleSelectRecipe
        );
    }

    private static void handleSelectRecipe(TCThaumatoriumSelectRecipePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!(player.containerMenu instanceof TCThaumatoriumMenu menu) || !menu.blockPos().equals(payload.pos())) {
                return;
            }
            TCThaumatoriumBlockEntity thaumatorium = menu.blockEntity();
            if (thaumatorium == null || !thaumatorium.stillValid(player)) {
                return;
            }
            thaumatorium.toggleRecipe(player, payload.recipeId());
            menu.broadcastChanges();
        });
    }
}
