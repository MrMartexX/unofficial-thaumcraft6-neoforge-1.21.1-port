package thaumcraft.common.menu;

import java.util.ArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.entities.TCPechEntity;
import thaumcraft.common.entities.TCPechTradeCatalog;
import thaumcraft.common.registry.TCMenus;
import thaumcraft.common.registry.TCSounds;

public final class TCPechMenu extends AbstractContainerMenu {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT_START = 1;
    public static final int SLOT_OUTPUT_END = 5;
    public static final int PLAYER_INVENTORY_START = 5;
    public static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    public static final int PLAYER_HOTBAR_START = PLAYER_INVENTORY_END;
    public static final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    private final SimpleContainer tradeInventory;
    private final TCPechEntity pech;

    public TCPechMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, clientPech(inventory, extraData));
    }

    public TCPechMenu(int containerId, Inventory inventory, TCPechEntity pech) {
        super(TCMenus.PECH.get(), containerId);
        this.pech = pech;
        this.tradeInventory = new SimpleContainer(SLOT_OUTPUT_END);
        if (pech != null) {
            pech.setTrading(true);
        }
        addSlot(new Slot(tradeInventory, SLOT_INPUT, 36, 29));
        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 2; column++) {
                addSlot(new OutputSlot(tradeInventory, SLOT_OUTPUT_START + column + row * 2, 106 + 18 * column, 20 + 18 * row));
            }
        }
        bindPlayerInventory(inventory);
    }

    private static TCPechEntity clientPech(Inventory inventory, RegistryFriendlyByteBuf extraData) {
        if (extraData == null) {
            return null;
        }
        Entity entity = inventory.player.level().getEntity(extraData.readVarInt());
        return entity instanceof TCPechEntity pech ? pech : null;
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
    public boolean clickMenuButton(Player player, int id) {
        if (id != 0 || pech == null || player.level().isClientSide) {
            return false;
        }
        boolean generated = generateContents(player);
        if (generated) {
            player.level().playSound(null, pech.blockPosition(), TCSounds.PECH_DICE.get(), SoundSource.NEUTRAL, 0.4F, 1.0F);
            broadcastChanges();
        }
        return generated;
    }

    public boolean canGenerateTrade() {
        return pech != null
                && pech.isValued(tradeInventory.getItem(SLOT_INPUT))
                && outputsEmpty();
    }

    private boolean generateContents(Player player) {
        if (!canGenerateTrade()) {
            return false;
        }
        ItemStack input = tradeInventory.getItem(SLOT_INPUT);
        int value = pech.getValue(input);
        if (player.level().random.nextInt(100) <= value / 2) {
            pech.setTamed(false);
            pech.playSound(TCSounds.PECH_TRADE.get(), 0.4F, 1.0F);
        }
        if (player.level().random.nextInt(5) == 0) {
            value += player.level().random.nextInt(3);
        } else if (player.level().random.nextBoolean()) {
            value -= player.level().random.nextInt(3);
        }

        while (value > 0) {
            int amount = Math.min(5, Math.max((value + 1) / 2, player.level().random.nextInt(value) + 1));
            value -= amount;
            if (amount == 1 && player.level().random.nextBoolean() && hasStuffInPack()) {
                addStack(takeRandomLoot(player).copyWithCount(1));
            } else {
                if (amount >= 4 && player.level().random.nextBoolean()) {
                    continue;
                }
                ItemStack trade = TCPechTradeCatalog.randomStack(pech.getPechType(), amount, player.level().random);
                if (!trade.isEmpty()) {
                    addStack(trade);
                }
            }
        }
        input.shrink(1);
        if (input.isEmpty()) {
            tradeInventory.setItem(SLOT_INPUT, ItemStack.EMPTY);
        }
        return true;
    }

    private boolean outputsEmpty() {
        for (int slot = SLOT_OUTPUT_START; slot < SLOT_OUTPUT_END; slot++) {
            if (!tradeInventory.getItem(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean hasStuffInPack() {
        if (pech == null) {
            return false;
        }
        for (int slot = 0; slot < pech.getContainerSize(); slot++) {
            if (!pech.getItem(slot).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private ItemStack takeRandomLoot(Player player) {
        ArrayList<Integer> filled = new ArrayList<>();
        for (int slot = 0; slot < pech.getContainerSize(); slot++) {
            if (!pech.getItem(slot).isEmpty()) {
                filled.add(slot);
            }
        }
        if (filled.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int slot = filled.get(player.level().random.nextInt(filled.size()));
        return pech.removeItem(slot, 1);
    }

    private void addStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        for (int slot = SLOT_OUTPUT_START; slot < SLOT_OUTPUT_END; slot++) {
            ItemStack current = tradeInventory.getItem(slot);
            if (current.isEmpty()) {
                tradeInventory.setItem(slot, stack.copy());
                return;
            }
            if (ItemStack.isSameItemSameComponents(current.copyWithCount(1), stack.copyWithCount(1))
                    && current.getCount() + stack.getCount() < current.getMaxStackSize()) {
                current.grow(stack.getCount());
                return;
            }
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
        if (index >= SLOT_INPUT && index < SLOT_OUTPUT_END) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (pech != null && pech.isValued(stack)) {
            if (!moveItemStackTo(stack, SLOT_INPUT, SLOT_INPUT + 1, false)) {
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
    public void removed(Player player) {
        super.removed(player);
        if (pech != null) {
            pech.setTrading(false);
        }
        if (!player.level().isClientSide) {
            for (int slot = 0; slot < tradeInventory.getContainerSize(); slot++) {
                ItemStack stack = tradeInventory.removeItemNoUpdate(slot);
                if (!stack.isEmpty()) {
                    ItemEntity dropped = player.drop(stack, false);
                    if (dropped != null) {
                        dropped.setNoPickUpDelay();
                    }
                }
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return pech != null && pech.stillValid(player);
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
