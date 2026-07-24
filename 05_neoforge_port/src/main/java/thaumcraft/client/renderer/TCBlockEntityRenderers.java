package thaumcraft.client.renderer;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import thaumcraft.common.registry.TCBlockEntities;

public final class TCBlockEntityRenderers {
    private TCBlockEntityRenderers() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(TCBlockEntityRenderers::onRegisterRenderers);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TCBlockEntities.RESEARCH_TABLE.get(), TCResearchTableRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.CRUCIBLE.get(), TCCrucibleRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.INFUSION_PEDESTAL.get(), TCInfusionPedestalRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.INFUSION_MATRIX.get(), TCInfusionMatrixRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.BELLOWS.get(), TCBellowsRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.TUBE_VALVE.get(), TCTubeValveRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.THAUMATORIUM.get(), TCThaumatoriumRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.WARDED_JAR.get(), TCWardedJarRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.JAR_VOID.get(), TCWardedJarRenderer::new);
    }
}
