package thaumcraft.client.world;

import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.ParticleStatus;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.client.fx.legacy.TCLegacyParticleEngine;

/** Real client sink/renderability evidence in a disposable, exposed flat forest, not a legacy screenshot comparison. */
@EventBusSubscriber(modid = "thaumcraft", value = Dist.CLIENT)
public final class TCForestAmbientAudit {
    private static int ticks, phase, spawned, nightCount, peakRenderable;
    private static boolean started;
    private static ParticleStatus previousParticles;
    private static final long DEADLINE = System.nanoTime() + java.util.concurrent.TimeUnit.MINUTES.toNanos(4);

    private TCForestAmbientAudit() {}

    public static void record(int count) {
        if (Boolean.getBoolean("tc.forestAmbientAudit")) spawned += count;
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (!Boolean.getBoolean("tc.forestAmbientAudit")) return;
        Minecraft mc = Minecraft.getInstance();
        if (System.nanoTime() > DEADLINE) throw new IllegalStateException("Ambient audit timed out");
        if (mc.level == null || mc.player == null || mc.screen != null) return;
        if (!started) {
            var server = mc.getSingleplayerServer();
            if (server == null || !server.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize().getFileName()
                    .toString().startsWith("tc_worldgen_ambient_audit_")) throw new IllegalStateException("Ambient audit world guard");
            mc.getWindow().setTitle("Thaumcraft forest mote audit - closes automatically");
            previousParticles = mc.options.particles().get();
            mc.options.particles().set(ParticleStatus.ALL);
            mc.player.setXRot(12);
            mc.player.setYRot(45);
            server.execute(() -> server.overworld().setDayTime(18000));
            Thaumcraft.LOGGER.info("Forest heightmap contract WORLD_SURFACE.sendToClient={} NO_LEAVES.sendToClient={}",
                    net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE.sendToClient(),
                    net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES.sendToClient());
            started = true;
        }
        ticks++;
        if (phase == 0 && ticks > 60) {
            spawned = 0;
            ticks = 0;
            phase = 1;
        } else if (phase == 1) {
            peakRenderable = Math.max(peakRenderable, TCLegacyParticleEngine.getRenderableEffectCount());
            if (ticks == 240) {
                nightCount = spawned;
                capture(mc);
                mc.getSingleplayerServer().execute(() -> mc.getSingleplayerServer().overworld().setDayTime(6000));
                phase = 2;
                ticks = 0;
            }
        } else if (phase == 2 && ticks > 60) {
            if (mc.level.getDayTime() % 24000 != 6000) throw new IllegalStateException("Day time did not sync");
            spawned = 0;
            ticks = 0;
            phase = 3;
        } else if (phase == 3 && ticks == 160) {
            boolean pass = nightCount > 0 && peakRenderable > 0 && spawned == 0;
            Thaumcraft.LOGGER.info("TC_FOREST_AMBIENT_AUDIT_{} nightSpawned={} peakRenderable={} daySpawned={} sky={}",
                    pass ? "PASS" : "FAIL", nightCount, peakRenderable, spawned,
                    mc.level.getBrightness(net.minecraft.world.level.LightLayer.SKY, mc.player.blockPosition()));
            mc.options.particles().set(previousParticles);
            if (!pass) throw new IllegalStateException("Forest ambient night/day audit failed");
            mc.stop();
        }
    }

    private static void capture(Minecraft mc) {
        try {
            Path output = Path.of("../../tools/reports/local/worldgen-20261103/night-motes.png").toAbsolutePath().normalize();
            Files.createDirectories(output.getParent());
            try (var picture = Screenshot.takeScreenshot(mc.getMainRenderTarget())) { picture.writeToFile(output); }
            Thaumcraft.LOGGER.info("Forest night capture {}", output);
        } catch (Exception e) { throw new IllegalStateException("Cannot capture forest motes", e); }
    }
}
