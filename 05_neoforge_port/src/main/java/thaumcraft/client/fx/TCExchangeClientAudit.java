package thaumcraft.client.fx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import thaumcraft.client.fx.legacy.TCLegacyFXGeneric;
import thaumcraft.common.items.casters.TCExchangeBamf;

/** Real client geometry checks, enabled only by the disposable-world visual probe. */
public final class TCExchangeClientAudit {
    private TCExchangeClientAudit() {}
    public static void run() {
        var mc = Minecraft.getInstance();
        var camera = mc.gameRenderer.getMainCamera();
        int[] vertices = {0};
        VertexConsumer sink = new VertexConsumer() {
            public VertexConsumer addVertex(float x, float y, float z) {
                if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z)) throw new IllegalStateException("Nonfinite Exchange vertex");
                vertices[0]++; return this;
            }
            public VertexConsumer setColor(int r, int g, int b, int a) { return this; }
            public VertexConsumer setUv(float u, float v) {
                if (u < 0 || u > 1 || v < 0 || v > 1) throw new IllegalStateException("Invalid Exchange atlas coordinates");
                return this;
            }
            public VertexConsumer setUv1(int u, int v) { return this; }
            public VertexConsumer setUv2(int u, int v) { return this; }
            public VertexConsumer setNormal(float x, float y, float z) { return this; }
        };
        int samples = 0;
        var motionRandom = new java.util.Random(0);
        for (int seed = 0; seed < 32; seed++) for (var p : TCExchangeBamf.generate(RandomSource.create(seed)).particles()) {
            var effect = new TCLegacyFXGeneric(mc.level, p.data(), mc.player.getX() + p.x(), mc.player.getY() + p.y(),
                    mc.player.getZ() + p.z(), p.vx(), p.vy(), p.vz());
            effect.configureLegacyRotation(p.rotationStart(), p.rotationDegrees(), p.yaw(), p.pitch());
            for (int age = 0; age < p.data().maxAge; age++) {
                int before = vertices[0];
                effect.render(sink, camera, .5F);
                if (vertices[0] != before + 4) throw new IllegalStateException("Exchange particle omitted its quad before expiration");
                samples++; effect.tick(motionRandom);
            }
            effect.tick(motionRandom);
            if (!effect.isRemoved()) throw new IllegalStateException("Exchange particle did not expire");
        }
        if (samples < 8000) throw new IllegalStateException("Insufficient Exchange geometry samples");
        com.mojang.logging.LogUtils.getLogger().info("TC_EXCHANGE_RENDER_PASS samples={} vertices={} finite=true atlas=true expires=true", samples, vertices[0]);
        mc.getSingleplayerServer().execute(() -> {
            var player = mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,
                    new thaumcraft.common.items.casters.TCExchangeFXPayload(player.level().dimension(), player.blockPosition()));
        });
    }
}
