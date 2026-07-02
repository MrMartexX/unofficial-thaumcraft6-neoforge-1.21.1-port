package thaumcraft.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.common.registry.TCMenus;
import thaumcraft.common.tiles.crafting.TCThaumatoriumBlockEntity;

public final class TCThaumatoriumMenu extends AbstractContainerMenu {
    public static final int SLOT_CATALYST = 0;
    public static final int PLAYER_INVENTORY_START = 1;
    public static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    public static final int PLAYER_HOTBAR_START = PLAYER_INVENTORY_END;
    public static final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    private final Container thaumatorium;
    private final TCThaumatoriumBlockEntity blockEntity;
    private final BlockPos blockPos;

    public TCThaumatoriumMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, clientContainer(inventory, extraData));
    }

    public TCThaumatoriumMenu(int containerId, Inventory inventory, TCThaumatoriumBlockEntity thaumatorium) {
        this(containerId, inventory, thaumatorium, thaumatorium, thaumatorium.getBlockPos());
    }

    private TCThaumatoriumMenu(int containerId, Inventory inventory, Container thaumatorium) {
        this(containerId, inventory, thaumatorium,
                thaumatorium instanceof TCThaumatoriumBlockEntity blockEntity ? blockEntity : null,
                thaumatorium instanceof TCThaumatoriumBlockEntity blockEntity ? blockEntity.getBlockPos() : BlockPos.ZERO);
    }

    private TCThaumatoriumMenu(
            int containerId,
            Inventory inventory,
            Container thaumatorium,
            TCThaumatoriumBlockEntity blockEntity,
            BlockPos blockPos
    ) {
        super(TCMenus.THAUMATORIUM.get(), containerId);
        checkContainerSize(thaumatorium, TCThaumatoriumBlockEntity.SLOT_COUNT);
        this.thaumatorium = thaumatorium;
        this.blockEntity = blockEntity;
        this.blockPos = blockPos;

        addSlot(new Slot(thaumatorium, TCThaumatoriumBlockEntity.SLOT_CATALYST, 55, 24));
        bindPlayerInventory(inventory);
    }

    private static Container clientContainer(Inventory inventory, RegistryFriendlyByteBuf extraData) {
        if (extraData == null) {
            return new SimpleContainer(TCThaumatoriumBlockEntity.SLOT_COUNT);
        }
        BlockPos pos = extraData.readBlockPos();
        Level level = inventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof TCThaumatoriumBlockEntity thaumatorium
                ? thaumatorium
                : new SimpleContainer(TCThaumatoriumBlockEntity.SLOT_COUNT);
    }

    private void bindPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 135 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 193));
        }
    }

    public BlockPos blockPos() {
        return blockPos;
    }

    public TCThaumatoriumBlockEntity blockEntity() {
        return blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index == SLOT_CATALYST) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, SLOT_CATALYST, SLOT_CATALYST + 1, false)) {
            if (index < PLAYER_INVENTORY_END) {
                if (!moveItemStackTo(stack, PLAYER_HOTBAR_START, PLAYER_HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                return ItemStack.EMPTY;
            }
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
    public boolean stillValid(Player player) {
        return blockEntity != null && blockEntity.stillValid(player);
    }
}
