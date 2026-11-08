package thaumcraft.common.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 1:1 legacy SlotGhostFull: displays an item stack that cannot be picked up or placed into.
 */
public class SlotGhostFull extends Slot {
    private final int limit;

    public SlotGhostFull(Container container, int index, int x, int y, int limit) {
        super(container, index, x, y);
        this.limit = limit;
    }

    public SlotGhostFull(Container container, int index, int x, int y) {
        this(container, index, x, y, Integer.MAX_VALUE);
    }

    @Override
    public int getMaxStackSize() {
        return limit;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }
}
