package thaumcraft.client;

import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import thaumcraft.Thaumcraft;

@EventBusSubscriber(modid = Thaumcraft.MODID, value = Dist.CLIENT)
public final class TCClientStartupProbe {
    private static boolean reported;
    private TCClientStartupProbe() {}

    @SubscribeEvent
    public static void afterScreenRender(ScreenEvent.Render.Post event) {
        if (!reported && Boolean.getBoolean("tc.clientSmoke") && event.getScreen() instanceof TitleScreen) {
            reported = true;
            Thaumcraft.LOGGER.info("TC_CLIENT_READY_AFTER_TITLE_RENDER");
        }
    }
}
