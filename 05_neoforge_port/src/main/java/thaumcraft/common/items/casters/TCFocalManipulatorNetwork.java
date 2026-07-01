package thaumcraft.common.items.casters;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.menu.TCFocalManipulatorMenu;
import thaumcraft.common.tiles.crafting.TCFocalManipulatorBlockEntity;

public final class TCFocalManipulatorNetwork {
    private static final String NETWORK_VERSION = "1";

    private TCFocalManipulatorNetwork() {
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(NETWORK_VERSION);
        registrar.playToServer(
                TCFocalManipulatorDesignPayload.TYPE,
                TCFocalManipulatorDesignPayload.STREAM_CODEC,
                TCFocalManipulatorNetwork::handleDesign
        );
    }

    private static void handleDesign(TCFocalManipulatorDesignPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!(player.containerMenu instanceof TCFocalManipulatorMenu menu) || !menu.blockPos().equals(payload.pos())) {
                return;
            }
            TCFocalManipulatorBlockEntity manipulator = menu.blockEntity();
            if (manipulator == null || !manipulator.stillValid(player)) {
                return;
            }
            boolean accepted = manipulator.applyDesignRequest(player, payload.encodedNodes(), payload.focusName());
            if (accepted && payload.startCraft()) {
                manipulator.startCraft(player);
            }
            menu.refreshData();
            menu.broadcastChanges();
        });
    }
}
