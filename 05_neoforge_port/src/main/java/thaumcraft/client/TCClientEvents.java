package thaumcraft.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.client.fx.legacy.TCLegacyParticleEngine;
import thaumcraft.client.fx.TCInfusionClientEffects;
import thaumcraft.client.fx.TCWispClientEffects;
import thaumcraft.client.gui.TCThaumonomiconClientController;
import thaumcraft.common.entities.TCEntityFXNetwork;
import thaumcraft.common.lib.fx.TCFXDispatcher;
import thaumcraft.common.crafting.infusion.TCInfusionClientFXCache;
import thaumcraft.common.research.TCKnowledgeClientCache;
import thaumcraft.common.research.TCThaumonomiconClientCache;
import thaumcraft.common.research.theorycraft.TCResearchTableClientCache;
import thaumcraft.common.warp.TCWarpClientCache;
import thaumcraft.common.world.aura.TCAuraClientCache;

@EventBusSubscriber(modid = Thaumcraft.MODID, value = Dist.CLIENT)
public final class TCClientEvents {
    static {
        TCFXDispatcher.setClientSink(TCLegacyParticleEngine::addEffect);
        TCEntityFXNetwork.setWispZapSink(TCWispClientEffects::accept);
    }

    private TCClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        if (Minecraft.getInstance().level == null) {
            TCLegacyParticleEngine.clear();
            TCKnowledgeClientCache.clear();
            TCWarpClientCache.clear();
            TCAuraClientCache.clear();
            TCThaumonomiconClientCache.clear();
            TCResearchTableClientCache.clear();
            TCInfusionClientFXCache.clear();
            TCInfusionClientEffects.clear();
            TCWispClientEffects.clear();
            return;
        }

        TCLegacyParticleEngine.tick();
        TCWispClientEffects.tick();
        TCInfusionClientEffects.tick(Minecraft.getInstance().level);
        TCThaumometerClientEffects.onClientTick(Minecraft.getInstance());
        TCWarpClientEffects.onClientTick(Minecraft.getInstance());
        TCThaumonomiconClientController.tick(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            TCLegacyParticleEngine.render(
                    event.getCamera(),
                    event.getPartialTick().getGameTimeDeltaPartialTick(false)
            );
            TCInfusionClientEffects.render(
                    event.getCamera(),
                    event.getPartialTick().getGameTimeDeltaPartialTick(false)
            );
            TCWispClientEffects.render(
                    event.getCamera(),
                    event.getPartialTick().getGameTimeDeltaPartialTick(false)
            );
            return;
        }

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            TCThaumometerClientEffects.renderAspectOverlay(event);
        }
    }
}
