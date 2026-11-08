package thaumcraft.common.entities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class TCEntityFXNetwork {
    @FunctionalInterface
    public interface WispZapSink {
        void accept(TCWispZapPayload payload);
    }

    @FunctionalInterface
    public interface FocusZapSink {
        void accept(thaumcraft.common.items.casters.TCFocusZapPayload payload);
    }

    public static final double LEGACY_WISP_ZAP_RANGE_SQR = 32.0D * 32.0D;
    private static final String NETWORK_VERSION = "1";
    private static WispZapSink wispZapSink = payload -> {
    };
    private static FocusZapSink focusZapSink = payload -> {
    };

    private TCEntityFXNetwork() {
    }

    public static void setWispZapSink(WispZapSink sink) {
        wispZapSink = sink == null ? payload -> {
        } : sink;
    }

    public static void setFocusZapSink(FocusZapSink sink) {
        focusZapSink = sink == null ? payload -> {
        } : sink;
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(NETWORK_VERSION)
                .playToClient(thaumcraft.common.items.casters.TCExchangeFXPayload.TYPE,
                        thaumcraft.common.items.casters.TCExchangeFXPayload.STREAM_CODEC,
                        (payload, context) -> context.enqueueWork(() -> {
                            if (context.player().level().dimension().equals(payload.dimension()))
                                thaumcraft.common.items.casters.TCExchangeBamf.accept(payload);
                        }))
                .playToClient(
                        TCWispZapPayload.TYPE,
                        TCWispZapPayload.STREAM_CODEC,
                        (payload, context) -> context.enqueueWork(() -> wispZapSink.accept(payload))
                )
                .playToClient(
                        thaumcraft.common.items.casters.TCFocusZapPayload.TYPE,
                        thaumcraft.common.items.casters.TCFocusZapPayload.STREAM_CODEC,
                        (payload, context) -> context.enqueueWork(() -> focusZapSink.accept(payload))
                );
    }

    public static void sendFocusZap(ServerLevel level, double x1, double y1, double z1, double x2, double y2, double z2, int color, float width) {
        thaumcraft.common.items.casters.TCFocusZapPayload payload =
                new thaumcraft.common.items.casters.TCFocusZapPayload(x1, y1, z1, x2, y2, z2, color, width);
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(x1, y1, z1) <= 64.0D * 64.0D) {
                PacketDistributor.sendToPlayer(player, payload);
            }
        }
    }

    public static void sendWispZap(ServerLevel level, TCWispEntity source, LivingEntity target) {
        TCWispZapPayload payload = new TCWispZapPayload(source.getId(), target.getId());
        double x = source.getX();
        double y = source.getY();
        double z = source.getZ();
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(x, y, z) <= LEGACY_WISP_ZAP_RANGE_SQR) {
                PacketDistributor.sendToPlayer(player, payload);
            }
        }
    }
}
