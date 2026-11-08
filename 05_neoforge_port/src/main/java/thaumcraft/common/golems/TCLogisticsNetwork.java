package thaumcraft.common.golems;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.common.menu.TCLogisticsMenu;

public final class TCLogisticsNetwork {
    private static final String NETWORK_VERSION = "1";

    private TCLogisticsNetwork() {
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(NETWORK_VERSION);
        registrar.playToServer(
                TCLogisticsPayloads.Request.TYPE,
                TCLogisticsPayloads.Request.STREAM_CODEC,
                TCLogisticsNetwork::handleRequest
        );
        registrar.playToServer(
                TCLogisticsPayloads.Search.TYPE,
                TCLogisticsPayloads.Search.STREAM_CODEC,
                TCLogisticsNetwork::handleSearch
        );
    }

    private static void handleRequest(TCLogisticsPayloads.Request message, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)
                    || !(player.containerMenu instanceof TCLogisticsMenu menu)
                    || !menu.stillValid(player)
                    || message.stack().isEmpty()) {
                return;
            }

            int remaining = message.count();
            if (remaining <= 0 || remaining > 6400) {
                return;
            }

            if (!java.util.Objects.equals(message.pos(), menu.getTargetPos())
                    || message.side() != menu.getTargetSide()) {
                return;
            }

            Level world = player.level();
            if (message.pos() != null) {
                if (message.side() == null
                        || !world.isLoaded(message.pos())
                        || player.distanceToSqr(
                                message.pos().getX() + 0.5D,
                                message.pos().getY() + 0.5D,
                                message.pos().getZ() + 0.5D) > 64.0D) {
                    return;
                }
            }

            int iterations = 0;
            while (remaining > 0 && iterations < 100) {
                ItemStack stack = message.stack().copy();
                int batch = Math.min(remaining, stack.getMaxStackSize());
                stack.setCount(batch);
                remaining -= batch;

                if (message.pos() != null && message.side() != null) {
                    GolemHelper.requestProvisioning(world, message.pos(), message.side(), stack);
                } else {
                    GolemHelper.requestProvisioning(world, player, stack);
                }
                iterations++;
            }
        });
    }

    private static void handleSearch(TCLogisticsPayloads.Search message, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player
                    && player.containerMenu instanceof TCLogisticsMenu menu) {
                menu.setSearchText(message.text());
            }
        });
    }
}
