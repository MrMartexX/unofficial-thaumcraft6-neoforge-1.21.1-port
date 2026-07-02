package thaumcraft.client.renderer;

import net.minecraft.client.renderer.entity.ItemEntityRenderer;
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
    }
}
