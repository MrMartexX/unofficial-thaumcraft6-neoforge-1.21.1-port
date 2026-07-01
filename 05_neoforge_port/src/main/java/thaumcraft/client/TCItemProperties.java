package thaumcraft.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.registry.TCItems;

public final class TCItemProperties {
    private TCItemProperties() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(TCItemProperties::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                TCItems.CASTER_BASIC.get(),
                ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "focus"),
                (stack, level, entity, seed) -> stack.getItem() instanceof ItemCaster caster && caster.hasFocus(stack) ? 1.0F : 0.0F
        ));
    }
}
