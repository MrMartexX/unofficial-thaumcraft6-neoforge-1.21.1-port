package thaumcraft.client.gui;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import thaumcraft.common.registry.TCMenus;

public final class TCMenuScreens {
    private TCMenuScreens() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(TCMenuScreens::onRegisterMenuScreens);
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(TCMenus.RESEARCH_TABLE.get(), TCResearchTableScreen::new);
        event.register(TCMenus.ARCANE_WORKBENCH.get(), TCArcaneWorkbenchScreen::new);
        event.register(TCMenus.FOCAL_MANIPULATOR.get(), TCFocalManipulatorScreen::new);
        event.register(TCMenus.SMELTER.get(), TCSmelterScreen::new);
        event.register(TCMenus.THAUMATORIUM.get(), TCThaumatoriumScreen::new);
    }
}
