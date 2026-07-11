package thaumcraft.common.capabilities;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import thaumcraft.common.registry.TCBlockEntities;

/** Modern sided automation adapters for server-owned Thaumcraft machine inventories. */
public final class TCMachineCapabilities {
    private TCMachineCapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TCBlockEntities.SMELTER_BASIC.get(),
                (smelter, side) -> smelter.itemHandler(side)
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TCBlockEntities.SMELTER_THAUMIUM.get(),
                (smelter, side) -> smelter.itemHandler(side)
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TCBlockEntities.SMELTER_VOID.get(),
                (smelter, side) -> smelter.itemHandler(side)
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TCBlockEntities.THAUMATORIUM.get(),
                (thaumatorium, side) -> thaumatorium.itemHandler(side)
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TCBlockEntities.THAUMATORIUM_TOP.get(),
                (thaumatoriumTop, side) -> thaumatoriumTop.itemHandler(side)
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TCBlockEntities.INFERNAL_FURNACE.get(),
                (infernalFurnace, side) -> infernalFurnace.itemHandler(side)
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TCBlockEntities.VOID_SIPHON.get(),
                (voidSiphon, side) -> voidSiphon.itemHandler(side)
        );
    }
}
