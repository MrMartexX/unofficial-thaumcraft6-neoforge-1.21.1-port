package thaumcraft.common.items;

import net.minecraft.world.item.Item;

/** Empty TC6 essentia phial. Filled phials remain aspect-variant stacks with ASPECT_STACK. */
public final class TCPhialItem extends Item {
    public static final int BASE_AMOUNT = 10;

    public TCPhialItem() {
        super(new Item.Properties());
    }
}
