package thaumcraft.common.menu;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.ItemFocusPouch;
import thaumcraft.common.registry.TCMenus;

public class TCFocusPouchMenu extends AbstractContainerMenu {
    public static final int POUCH_SLOTS = 18;
    public static final int PLAYER_INV_START = 18;
    public static final int PLAYER_INV_END = PLAYER_INV_START + 27;
    public static final int PLAYER_HOTBAR_START = PLAYER_INV_END;
    public static final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    private final Player player;
    private final InteractionHand hand;
    private final int selectedSlot;
    private final SimpleContainer pouchContainer = new SimpleContainer(POUCH_SLOTS);

    public TCFocusPouchMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, readHand(extraData), readSelectedSlot(extraData));
    }

    public TCFocusPouchMenu(int containerId, Inventory inventory, InteractionHand hand, int selectedSlot) {
        super(TCMenus.FOCUS_POUCH.get(), containerId);
        this.player = inventory.player;
        this.hand = hand;
        this.selectedSlot = selectedSlot;

        ItemStack pouch = getPouchStack();
        if (!pouch.isEmpty() && pouch.getItem() instanceof ItemFocusPouch) {
            var items = ItemFocusPouch.getInventory(pouch);
            for (int i = 0; i < POUCH_SLOTS && i < items.size(); i++) {
                pouchContainer.setItem(i, items.get(i));
            }
        }

        // 18 pouch slots (6x3 grid)
        for (int a = 0; a < POUCH_SLOTS; ++a) {
            int x = 37 + (a % 6) * 18;
            int y = 51 + (a / 6) * 18;
            addSlot(new FocusSlot(pouchContainer, a, x, y));
        }

        // Player Inventory (3x9)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 151 + i * 18));
            }
        }

        // Hotbar (1x9)
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 209));
        }
    }

    private static InteractionHand readHand(RegistryFriendlyByteBuf extraData) {
        if (extraData == null) {
            return InteractionHand.MAIN_HAND;
        }
        return extraData.readByte() == 1 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    private static int readSelectedSlot(RegistryFriendlyByteBuf extraData) {
        if (extraData == null) {
            return -1;
        }
        return extraData.readVarInt();
    }

    private ItemStack getPouchStack() {
        if (hand != null) {
            return player.getItemInHand(hand);
        }
        if (selectedSlot >= 0 && selectedSlot < player.getInventory().getContainerSize()) {
            return player.getInventory().getItem(selectedSlot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        ItemStack pouch = getPouchStack();
        return !pouch.isEmpty() && pouch.getItem() instanceof ItemFocusPouch;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        savePouch();
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        savePouch();
    }

    private void savePouch() {
        ItemStack pouch = getPouchStack();
        if (!pouch.isEmpty() && pouch.getItem() instanceof ItemFocusPouch) {
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < POUCH_SLOTS; i++) {
                list.add(pouchContainer.getItem(i));
            }
            ItemFocusPouch.setInventory(pouch, list);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemstack = slot.getItem();
        ItemStack copy = itemstack.copy();

        if (index < POUCH_SLOTS) {
            // From pouch to player inventory
            if (!moveItemStackTo(itemstack, POUCH_SLOTS, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // From player inventory to pouch
            if (itemstack.getItem() instanceof ItemFocus) {
                if (!moveItemStackTo(itemstack, 0, POUCH_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (itemstack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }

    public static class FocusSlot extends Slot {
        public FocusSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof ItemFocus;
        }
    }
}
