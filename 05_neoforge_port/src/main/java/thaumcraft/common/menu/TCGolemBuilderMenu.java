package thaumcraft.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.registry.TCMenus;
import thaumcraft.common.tiles.crafting.TCGolemBuilderBlockEntity;

public class TCGolemBuilderMenu extends AbstractContainerMenu {

    private final Container container;
    private final ContainerData data;
    private final TCGolemBuilderBlockEntity blockEntity;

    public TCGolemBuilderMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(1), new SimpleContainerData(6));
    }

    public TCGolemBuilderMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(TCMenus.GOLEM_BUILDER.get(), containerId);
        this.container = container;
        this.data = data;
        this.blockEntity = container instanceof TCGolemBuilderBlockEntity be ? be : null;

        checkContainerSize(container, 1);
        checkContainerDataCount(data, 6);

        // Output Slot 0
        this.addSlot(new Slot(container, 0, 160, 104) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // Player Inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 24 + col * 18, 142 + row * 18));
            }
        }

        // Player Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 24 + col * 18, 200));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && blockEntity != null && stillValid(player)) {
            // Button 0 or positive long encoded index
            long props = getGolemProps();
            return blockEntity.startCraft(props, player);
        }
        return false;
    }

    public int getCost() {
        return data.get(0);
    }

    public int getMaxCost() {
        return data.get(1);
    }

    public long getGolemProps() {
        long p0 = ((long) data.get(2)) & 0xFFFFL;
        long p1 = ((long) data.get(3)) & 0xFFFFL;
        long p2 = ((long) data.get(4)) & 0xFFFFL;
        long p3 = ((long) data.get(5)) & 0xFFFFL;
        return p0 | (p1 << 16) | (p2 << 32) | (p3 << 48);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index == 0) {
                // Moving from output to player inventory
                if (!this.moveItemStackTo(stackInSlot, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemstack);
            } else {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public TCGolemBuilderBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
