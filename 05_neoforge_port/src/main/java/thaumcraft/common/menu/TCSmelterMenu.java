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
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.registry.TCMenus;
import thaumcraft.common.tiles.essentia.TCSmelterBlockEntity;

/** Server-authoritative TC6 smelter menu with the five legacy progress fields. */
public final class TCSmelterMenu extends AbstractContainerMenu {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int PLAYER_INVENTORY_START = 2;
    public static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    public static final int PLAYER_HOTBAR_START = PLAYER_INVENTORY_END;
    public static final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    private final Container smelter;
    private final TCSmelterBlockEntity blockEntity;
    private int cookTime;
    private int burnTime;
    private int itemBurnTime;
    private int vis;
    private int smeltTime = TCSmelterBlockEntity.BASE_SMELT_TIME;

    public TCSmelterMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, clientSmelter(inventory, extraData));
    }

    public TCSmelterMenu(int containerId, Inventory inventory, TCSmelterBlockEntity smelter) {
        this(containerId, inventory, smelter, smelter);
    }

    private TCSmelterMenu(int containerId, Inventory inventory, Container smelter) {
        this(containerId, inventory, smelter,
                smelter instanceof TCSmelterBlockEntity blockEntity ? blockEntity : null);
    }

    private TCSmelterMenu(
            int containerId,
            Inventory inventory,
            Container smelter,
            TCSmelterBlockEntity blockEntity
    ) {
        super(TCMenus.SMELTER.get(), containerId);
        checkContainerSize(smelter, TCSmelterBlockEntity.SLOT_COUNT);
        this.smelter = smelter;
        this.blockEntity = blockEntity;

        addSlot(new AspectInputSlot(smelter, TCSmelterBlockEntity.SLOT_INPUT, 80, 8));
        addSlot(new FuelSlot(smelter, TCSmelterBlockEntity.SLOT_FUEL, 80, 48));
        bindPlayerInventory(inventory);
        bindData();
    }

    private static Container clientSmelter(Inventory inventory, RegistryFriendlyByteBuf extraData) {
        if (extraData == null) {
            return new SimpleContainer(TCSmelterBlockEntity.SLOT_COUNT);
        }
        BlockPos pos = extraData.readBlockPos();
        Level level = inventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof TCSmelterBlockEntity smelter
                ? smelter
                : new SimpleContainer(TCSmelterBlockEntity.SLOT_COUNT);
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

    private void bindData() {
        addDataSlot(data(() -> blockEntity == null ? cookTime : blockEntity.furnaceCookTime(),
                value -> cookTime = value));
        addDataSlot(data(() -> blockEntity == null ? burnTime : blockEntity.furnaceBurnTime(),
                value -> burnTime = value));
        addDataSlot(data(() -> blockEntity == null ? itemBurnTime : blockEntity.currentItemBurnTime(),
                value -> itemBurnTime = value));
        addDataSlot(data(() -> blockEntity == null ? vis : blockEntity.storedVis(),
                value -> vis = value));
        addDataSlot(data(() -> blockEntity == null ? smeltTime : blockEntity.smeltTime(),
                value -> smeltTime = Math.max(1, value)));
    }

    private static DataSlot data(IntGetter getter, IntSetter setter) {
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

    public int cookProgressScaled(int height) {
        return smeltTime <= 0 ? 0 : cookTime * height / smeltTime;
    }

    public int burnTimeRemainingScaled(int height) {
        int total = itemBurnTime <= 0 ? 200 : itemBurnTime;
        return burnTime * height / total;
    }

    public int visScaled(int height) {
        return vis * height / TCSmelterBlockEntity.MAX_VIS;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < PLAYER_INVENTORY_START) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (TCSmelterBlockEntity.getBurnTime(stack) > 0) {
            if (!moveItemStackTo(stack, SLOT_FUEL, SLOT_FUEL + 1, false)
                    && !moveItemStackTo(stack, SLOT_INPUT, SLOT_INPUT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (new AspectList(stack).size() > 0) {
            if (!moveItemStackTo(stack, SLOT_INPUT, SLOT_INPUT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < PLAYER_INVENTORY_END) {
            if (!moveItemStackTo(stack, PLAYER_HOTBAR_START, PLAYER_HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
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
    public boolean stillValid(Player player) {
        return blockEntity != null && blockEntity.stillValid(player);
    }

    private static final class AspectInputSlot extends Slot {
        private AspectInputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return !stack.isEmpty() && new AspectList(stack).size() > 0;
        }
    }

    private static final class FuelSlot extends Slot {
        private FuelSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return TCSmelterBlockEntity.getBurnTime(stack) > 0;
        }
    }

    @FunctionalInterface
    private interface IntGetter {
        int get();
    }

    @FunctionalInterface
    private interface IntSetter {
        void set(int value);
    }
}
