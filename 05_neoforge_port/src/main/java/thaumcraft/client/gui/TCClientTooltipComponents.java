package thaumcraft.client.gui;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

public final class TCClientTooltipComponents {
    private TCClientTooltipComponents() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(TCClientTooltipComponents::registerTooltipComponents);
    }

    @SubscribeEvent
    private static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(AspectTooltipComponent.class, ClientAspectTooltipComponent::new);
    }
}
