package thaumcraft.common.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.entities.TCArcaneBoreEntity;
import thaumcraft.common.registry.TCMenus;

public final class TCArcaneBoreMenu extends AbstractContainerMenu {
    public static final int SLOT_TOOL = 0;
    public static final int PLAYER_INVENTORY_START = 1;
    public static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    public static final int PLAYER_HOTBAR_START = PLAYER_INVENTORY_END;
    public static final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    private final Container bore;
    private final TCArcaneBoreEntity entity;

    public TCArcaneBoreMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, clientBore(inventory, extraData));
    }

    public TCArcaneBoreMenu(int containerId, Inventory inventory, TCArcaneBoreEntity bore) {
        this(containerId, inventory, bore, bore);
    }

    private TCArcaneBoreMenu(int containerId, Inventory inventory, Container bore) {
        this(containerId, inventory, bore, bore instanceof TCArcaneBoreEntity entity ? entity : null);
    }

    private TCArcaneBoreMenu(int containerId, Inventory inventory, Container bore, TCArcaneBoreEntity entity) {
        super(TCMenus.ARCANE_BORE.get(), containerId);
        checkContainerSize(bore, TCArcaneBoreEntity.SLOT_COUNT);
        this.bore = bore;
        this.entity = entity;
        addSlot(new PickaxeSlot(bore, SLOT_TOOL, 80, 29));
        bindPlayerInventory(inventory);
    }

    private static Container clientBore(Inventory inventory, RegistryFriendlyByteBuf extraData) {
        if (extraData == null) {
            return new SimpleContainer(TCArcaneBoreEntity.SLOT_COUNT);
        }
        Entity entity = inventory.player.level().getEntity(extraData.readVarInt());
        return entity instanceof TCArcaneBoreEntity bore ? bore : new SimpleContainer(TCArcaneBoreEntity.SLOT_COUNT);
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

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == SLOT_TOOL) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.is(ItemTags.PICKAXES)) {
            if (!moveItemStackTo(stack, SLOT_TOOL, SLOT_TOOL + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return entity != null && entity.stillValid(player);
    }

    private static final class PickaxeSlot extends Slot {
        private PickaxeSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(ItemTags.PICKAXES);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
