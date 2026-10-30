package thaumcraft.common.items.curios;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.Thaumcraft;

@EventBusSubscriber(modid = Thaumcraft.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class TCBaubleNetwork {
    private static final String NETWORK_VERSION = "1";

    private TCBaubleNetwork() {
    }

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(NETWORK_VERSION)
                .playToServer(
                        TCCloudRingJumpPayload.TYPE,
                        TCCloudRingJumpPayload.STREAM_CODEC,
                        TCBaubleNetwork::handleCloudRingJump
                );
    }

    private static void handleCloudRingJump(TCCloudRingJumpPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                TCBaubleEvents.tryCloudRingJump(player);
            }
        });
    }
}
