package thaumcraft.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.common.registry.TCMenus;
import thaumcraft.common.tiles.devices.TCVoidSiphonBlockEntity;

public final class TCVoidSiphonMenu extends AbstractContainerMenu {
    public static final int SLOT_OUTPUT = 0;
    public static final int PLAYER_INVENTORY_START = 1;
    public static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    public static final int PLAYER_HOTBAR_START = PLAYER_INVENTORY_END;
    public static final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    private final Container siphon;
    private final TCVoidSiphonBlockEntity blockEntity;
    private int progress;

    public TCVoidSiphonMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, clientSiphon(inventory, extraData));
    }

    public TCVoidSiphonMenu(int containerId, Inventory inventory, TCVoidSiphonBlockEntity siphon) {
        this(containerId, inventory, siphon, siphon);
    }

    private TCVoidSiphonMenu(int containerId, Inventory inventory, Container siphon) {
        this(containerId, inventory, siphon,
                siphon instanceof TCVoidSiphonBlockEntity blockEntity ? blockEntity : null);
    }

    private TCVoidSiphonMenu(
            int containerId,
            Inventory inventory,
            Container siphon,
            TCVoidSiphonBlockEntity blockEntity
    ) {
        super(TCMenus.VOID_SIPHON.get(), containerId);
        checkContainerSize(siphon, TCVoidSiphonBlockEntity.SLOT_COUNT);
        this.siphon = siphon;
        this.blockEntity = blockEntity;

        addSlot(new OutputSlot(siphon, SLOT_OUTPUT, 80, 35));
        bindPlayerInventory(inventory);
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity == null ? progress : blockEntity.progress();
            }

            @Override
            public void set(int value) {
                progress = value;
            }
        });
    }

    private static Container clientSiphon(Inventory inventory, RegistryFriendlyByteBuf extraData) {
        if (extraData == null) {
            return new SimpleContainer(TCVoidSiphonBlockEntity.SLOT_COUNT);
        }
        BlockPos pos = extraData.readBlockPos();
        Level level = inventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof TCVoidSiphonBlockEntity siphon
                ? siphon
                : new SimpleContainer(TCVoidSiphonBlockEntity.SLOT_COUNT);
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

    public int progressScaled(int height) {
        return progress * height / TCVoidSiphonBlockEntity.PROGRESS_REQUIRED;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == SLOT_OUTPUT) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
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
        return blockEntity != null && blockEntity.stillValid(player);
    }

    private static final class OutputSlot extends Slot {
        private OutputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
