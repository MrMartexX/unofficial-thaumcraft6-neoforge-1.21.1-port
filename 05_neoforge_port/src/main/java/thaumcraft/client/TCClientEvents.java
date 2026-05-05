package thaumcraft.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.client.fx.legacy.TCLegacyParticleEngine;
import thaumcraft.common.lib.fx.TCFXDispatcher;

@EventBusSubscriber(modid = Thaumcraft.MODID, value = Dist.CLIENT)
public final class TCClientEvents {
    static {
        TCFXDispatcher.setClientSink(TCLegacyParticleEngine::drawWispyMotes);
    }

    private TCClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().level == null) {
            TCLegacyParticleEngine.clear();
            return;
        }

        TCLegacyParticleEngine.tick();
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        TCLegacyParticleEngine.render(
                event.getCamera(),
                event.getPartialTick().getGameTimeDeltaPartialTick(false)
        );
    }
}