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
        event.registerBlockEntityRenderer(TCBlockEntities.HUNGRY_CHEST.get(), TCHungryChestRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.RESEARCH_TABLE.get(), TCResearchTableRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.CRUCIBLE.get(), TCCrucibleRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.INFUSION_PEDESTAL.get(), TCInfusionPedestalRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.RECHARGE_PEDESTAL.get(), TCInfusionPedestalRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.INFUSION_MATRIX.get(), TCInfusionMatrixRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.BELLOWS.get(), TCBellowsRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.TUBE_VALVE.get(), TCTubeValveRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.THAUMATORIUM.get(), TCThaumatoriumRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.WARDED_JAR.get(), TCWardedJarRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.JAR_VOID.get(), TCWardedJarRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.GOLEM_BUILDER.get(), TCGolemBuilderRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.JAR_BRAIN.get(), TCBrainJarRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.DIOPTRA.get(), TCDioptraRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.MIRROR.get(), TCMirrorRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.MIRROR_ESSENTIA.get(), TCMirrorRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.CENTRIFUGE.get(), TCCentrifugeRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.TUBE_BUFFER.get(), TCTubeBufferRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.VOID_SIPHON.get(), TCVoidSiphonRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.FOCAL_MANIPULATOR.get(), TCFocalManipulatorRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.ALEMBIC.get(), TCAlembicRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.TUBE_ONEWAY.get(), TCTubeOnewayRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.PATTERN_CRAFTER.get(), TCPatternCrafterRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.HOLE.get(), TCHoleRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntities.BANNER.get(), TCBannerRenderer::new);
    }
}
