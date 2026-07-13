package thaumcraft.client.renderer;

import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import thaumcraft.common.registry.TCEntityTypes;

public final class TCEntityRenderers {
    private TCEntityRenderers() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(TCEntityRenderers::onRegisterRenderers);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(TCEntityTypes.SPECIAL_ITEM.get(), ItemEntityRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.FOLLOW_ITEM.get(), ItemEntityRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.FLUX_RIFT.get(), TCFluxRiftRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.ARCANE_BORE.get(), TCArcaneBoreRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.FALLING_TAINT.get(), TCFallingTaintRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.TAINT_SEED.get(), TCTaintSeedRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.TAINT_SEED_PRIME.get(), TCTaintSeedRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.THAUM_SLIME.get(), TCInvisibleEntityRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.TAINT_CRAWLER.get(), TCInvisibleEntityRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.TAINTACLE.get(), TCInvisibleEntityRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.TAINTACLE_TINY.get(), TCInvisibleEntityRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.TAINT_SWARM.get(), TCInvisibleEntityRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.WISP.get(), TCWispRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.BOTTLE_TAINT.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.ELDRITCH_ORB.get(), TCEldritchOrbRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.CULTIST_PORTAL_LESSER.get(), TCInvisibleEntityRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.CULTIST_KNIGHT.get(), TCInvisibleEntityRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.CULTIST_CLERIC.get(), TCInvisibleEntityRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.MIND_SPIDER.get(), TCInvisibleEntityRenderer::new);
        event.registerEntityRenderer(TCEntityTypes.ELDRITCH_GUARDIAN.get(), TCInvisibleEntityRenderer::new);
    }
}
