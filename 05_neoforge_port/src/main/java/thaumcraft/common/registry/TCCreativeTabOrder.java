package thaumcraft.common.registry;

import net.minecraft.world.item.CreativeModeTab;

/**
 * Controls the visible Thaumcraft creative tab order.
 *
 * <p>Do not sort this class alphabetically and do not rely on registry declaration order.
 * The visible order should follow the Thaumcraft 6 1.12.2 creative inventory screenshots.</p>
 */
public final class TCCreativeTabOrder {
    private TCCreativeTabOrder() {
    }

    public static void addThaumcraftItems(CreativeModeTab.Output output) {
        output.accept(TCItems.AMBER.get());
        output.accept(TCItems.QUICKSILVER.get());
        output.accept(TCItems.FABRIC.get());
    }
}
