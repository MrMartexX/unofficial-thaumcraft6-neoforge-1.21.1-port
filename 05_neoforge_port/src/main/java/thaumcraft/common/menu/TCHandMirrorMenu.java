package thaumcraft.common.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.items.tools.ItemHandMirror;
import thaumcraft.common.registry.TCMenus;

/** Legacy hand mirror one-slot sender menu. */
public final class TCHandMirrorMenu extends AbstractContainerMenu {
    public static final int SLOT_INPUT = 0;
    public static final int PLAYER_INVENTORY_START = 1;
    public static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    public static final int PLAYER_HOTBAR_START = PLAYER_INVENTORY_END;
    public static final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    private final Player player;
    private final InteractionHand hand;
    private final int selectedSlot;
    private final SimpleContainer input = new SimpleContainer(1);
    private boolean transporting;

    public TCHandMirrorMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, readHand(extraData), readSelectedSlot(extraData));
    }

    public TCHandMirrorMenu(int containerId, Inventory inventory, InteractionHand hand, int selectedSlot) {
        super(TCMenus.HAND_MIRROR.get(), containerId);
        this.player = inventory.player;
        this.hand = hand;
        this.selectedSlot = selectedSlot;
        addSlot(new MirrorInputSlot(input, SLOT_INPUT, 80, 24, this));
        bindPlayerInventory(inventory);
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

    private void bindPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
    }

    public int selectedSlot() {
        return selectedSlot;
    }

    public void onInputChanged() {
        if (transporting || player.level().isClientSide) {
            return;
        }
        ItemStack stack = input.getItem(SLOT_INPUT);
        if (stack.isEmpty()) {
            return;
        }

        transporting = true;
        ItemStack transport = stack.copy();
        input.setItem(SLOT_INPUT, ItemStack.EMPTY);
        if (!ItemHandMirror.transport(heldMirror(), transport, player)) {
            input.setItem(SLOT_INPUT, transport);
        }
        input.setChanged();
        broadcastChanges();
        transporting = false;
    }

    private ItemStack heldMirror() {
        return player.getItemInHand(hand);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == SLOT_INPUT) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!ItemHandMirror.isHandMirror(stack) && !moveItemStackTo(stack, SLOT_INPUT, SLOT_INPUT + 1, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < slots.size() && ItemHandMirror.isHandMirror(slots.get(slotId).getItem())) {
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public boolean stillValid(Player player) {
        return ItemHandMirror.isHandMirror(heldMirror());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            ItemStack remaining = input.removeItemNoUpdate(SLOT_INPUT);
            if (!remaining.isEmpty()) {
                player.drop(remaining, false);
            }
        }
    }

    private static final class MirrorInputSlot extends Slot {
        private final TCHandMirrorMenu menu;

        private MirrorInputSlot(Container container, int slot, int x, int y, TCHandMirrorMenu menu) {
            super(container, slot, x, y);
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return !ItemHandMirror.isHandMirror(stack);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            menu.onInputChanged();
        }
    }
}
