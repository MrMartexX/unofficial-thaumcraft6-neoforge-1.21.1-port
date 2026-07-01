package thaumcraft.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCMenus;
import thaumcraft.common.tiles.crafting.TCFocalManipulatorBlockEntity;

public class TCFocalManipulatorMenu extends AbstractContainerMenu {
    public static final int SLOT_FOCUS = 0;
    public static final int PLAYER_INVENTORY_START = 1;
    public static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    public static final int PLAYER_HOTBAR_START = PLAYER_INVENTORY_END;
    public static final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    private final Inventory playerInventory;
    private final Container manipulator;
    private final TCFocalManipulatorBlockEntity blockEntity;
    private final BlockPos blockPos;
    private int syncedRemainingVis;
    private int syncedXpCost;

    public TCFocalManipulatorMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, getClientSource(playerInventory, extraData));
    }

    public TCFocalManipulatorMenu(int containerId, Inventory playerInventory, TCFocalManipulatorBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity, blockEntity.getBlockPos());
    }

    private TCFocalManipulatorMenu(int containerId, Inventory playerInventory, MenuSource source) {
        this(containerId, playerInventory, source.container(), source.blockEntity(), source.pos());
    }

    private TCFocalManipulatorMenu(
            int containerId,
            Inventory playerInventory,
            Container container,
            TCFocalManipulatorBlockEntity blockEntity,
            BlockPos blockPos
    ) {
        super(TCMenus.FOCAL_MANIPULATOR.get(), containerId);
        checkContainerSize(container, TCFocalManipulatorBlockEntity.SLOT_COUNT);
        this.playerInventory = playerInventory;
        this.manipulator = container;
        this.blockEntity = blockEntity;
        this.blockPos = blockPos;

        addSlot(new FocusSlot(container, TCFocalManipulatorBlockEntity.SLOT_FOCUS, 32, 64));
        bindPlayerInventory(playerInventory);
        addDataSlot(slot(() -> syncedRemainingVis, value -> syncedRemainingVis = value));
        addDataSlot(slot(() -> syncedXpCost, value -> syncedXpCost = value));
        refreshData();
    }

    public int remainingVis() {
        return syncedRemainingVis;
    }

    public int xpCost() {
        return syncedXpCost;
    }

    public TCFocalManipulatorBlockEntity blockEntity() {
        return blockEntity;
    }

    public BlockPos blockPos() {
        return blockPos;
    }

    public void refreshData() {
        if (blockEntity != null) {
            syncedRemainingVis = (int)Math.ceil(blockEntity.remainingVis());
            syncedXpCost = blockEntity.xpCost();
        }
    }

    private static MenuSource getClientSource(Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        if (extraData == null) {
            return new MenuSource(new SimpleContainer(TCFocalManipulatorBlockEntity.SLOT_COUNT), null, BlockPos.ZERO);
        }
        BlockPos pos = extraData.readBlockPos();
        Level level = playerInventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TCFocalManipulatorBlockEntity focal) {
            return new MenuSource(focal, focal, pos);
        }
        return new MenuSource(new SimpleContainer(TCFocalManipulatorBlockEntity.SLOT_COUNT), null, pos);
    }

    private void bindPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 16 + column * 18, 151 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 16 + column * 18, 209));
        }
    }

    private DataSlot slot(IntGetter getter, IntSetter setter) {
        return new DataSlot() {
            @Override
            public int get() {
                return getter.get();
            }

            @Override
            public void set(int value) {
                setter.set(value);
            }
        };
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 && blockEntity != null && player instanceof ServerPlayer serverPlayer) {
            boolean started = blockEntity.startCraft(serverPlayer);
            refreshData();
            broadcastChanges();
            return started;
        }
        return false;
    }

    @Override
    public void broadcastChanges() {
        refreshData();
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == SLOT_FOCUS) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.getItem() instanceof ItemFocus) {
            if (!moveItemStackTo(stack, SLOT_FOCUS, SLOT_FOCUS + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= PLAYER_INVENTORY_START && index < PLAYER_INVENTORY_END) {
            if (!moveItemStackTo(stack, PLAYER_HOTBAR_START, PLAYER_HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= PLAYER_HOTBAR_START && index < PLAYER_HOTBAR_END
                && !moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null || blockEntity.getLevel() == null) {
            return false;
        }
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player,
                TCBlocks.WAND_WORKBENCH.get()
        );
    }

    private static final class FocusSlot extends Slot {
        private FocusSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof ItemFocus;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    private interface IntGetter {
        int get();
    }

    private interface IntSetter {
        void set(int value);
    }

    private record MenuSource(Container container, TCFocalManipulatorBlockEntity blockEntity, BlockPos pos) {
    }
}
