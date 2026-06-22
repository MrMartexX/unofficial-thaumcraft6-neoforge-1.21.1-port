package thaumcraft.common.items;

import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;

/**
 * Exact TC6 custom fuel overrides.
 *
 * <p>The two logs also match Minecraft's generic log fuel data map, so an event override is
 * required to preserve TC6's 500/400 tick values after tag expansion.</p>
 */
public final class TCFuelEvents {
    private TCFuelEvents() {
    }

    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().is(TCItems.ALUMENTUM.get())) {
            event.setBurnTime(4800);
        } else if (event.getItemStack().is(TCBlocks.LOG_GREATWOOD.get().asItem())) {
            event.setBurnTime(500);
        } else if (event.getItemStack().is(TCBlocks.LOG_SILVERWOOD.get().asItem())) {
            event.setBurnTime(400);
        }
    }
}
