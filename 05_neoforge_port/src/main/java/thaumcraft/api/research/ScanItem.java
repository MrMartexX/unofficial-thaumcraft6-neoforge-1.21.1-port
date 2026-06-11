package thaumcraft.api.research;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ScanItem implements IScanThing {
    private final String research;
    private final ItemStack stack;

    public ScanItem(String research, ItemStack stack) {
        this.research = research;
        this.stack = stack == null ? ItemStack.EMPTY : stack.copy();
    }

    @Override
    public boolean checkThing(ServerPlayer player, Object object) {
        ItemStack scanned = ScanningManager.getItemFromParams(player, object);
        return !scanned.isEmpty() && !stack.isEmpty() && scanned.getItem() == stack.getItem();
    }

    @Override
    public String getResearchKey(ServerPlayer player, Object object) {
        return research;
    }
}
