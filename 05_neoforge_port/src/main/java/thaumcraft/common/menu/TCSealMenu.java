package thaumcraft.common.menu;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.seals.*;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.registry.TCMenus;

/** Legacy button IDs and ghost-filter semantics, transported through vanilla menu actions. */
public final class TCSealMenu extends AbstractContainerMenu {
    public final SealEntity seal;
    public final int[] categories;
    private final int filterSlots;
    private final SimpleContainer ghosts;
    private int category;

    public TCSealMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, readSeal(buffer));
    }

    private static SealEntity readSeal(RegistryFriendlyByteBuf buffer) {
        SealEntity seal = new SealEntity();
        var tag = buffer.readNbt();
        if (tag == null) throw new IllegalArgumentException("Missing seal opening data");
        seal.readNBT(tag, buffer.registryAccess());
        return seal;
    }

    public TCSealMenu(int id, Inventory inventory, SealEntity seal) {
        super(TCMenus.SEAL.get(), id);
        this.seal = seal;
        categories = seal.getSeal() instanceof ISealGui gui ? gui.getGuiCategories() : new int[]{0};
        category = categories.length == 0 ? 0 : categories[0];
        filterSlots = seal.getSeal() instanceof ISealConfigFilter filter ? filter.getFilterSize() : 0;
        ghosts = new SimpleContainer(filterSlots);
        for (int i = 0; i < filterSlots; i++) {
            final int index = i;
            int sx = 16 + (filterSlots - 1) % 3 * 12, sy = 16 + (filterSlots - 1) / 3 * 12;
            addSlot(new Slot(ghosts, i, 96 + i % 3 * 24 - sx, 80 + i / 3 * 24 - sy) {
                public boolean mayPlace(ItemStack stack) { return false; }
                public boolean mayPickup(Player player) { return false; }
                public boolean isActive() { return category == 1; }
                public int getMaxStackSize() { return 1; }
            });
            ISealConfigFilter filter = (ISealConfigFilter) seal.getSeal();
            ghosts.setItem(i, filter.getFilterSlot(i).copy());
            sync(() -> filter.getFilterSlotSize(index), value -> filter.setFilterSlotSize(index, value));
        }
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 150 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 208));
        sync(() -> category, value -> category = value);
        sync(seal::getPriority, value -> seal.setPriority((byte)value));
        sync(seal::getColor, value -> seal.setColor((byte)value));
        sync(() -> seal.isLocked() ? 1 : 0, value -> seal.setLocked(value != 0));
        sync(() -> seal.isRedstoneSensitive() ? 1 : 0, value -> seal.setRedstoneSensitive(value != 0));
        for (int axis = 0; axis < 3; axis++) {
            final int a = axis;
            sync(() -> seal.getArea().get(a == 0 ? net.minecraft.core.Direction.Axis.X : a == 1 ? net.minecraft.core.Direction.Axis.Y : net.minecraft.core.Direction.Axis.Z),
                    value -> setArea(a, value));
        }
        if (seal.getSeal() instanceof ISealConfigFilter filter)
            sync(() -> filter.isBlacklist() ? 1 : 0, value -> filter.setBlacklist(value != 0));
        if (seal.getSeal() instanceof ISealConfigToggles toggles) for (int i = 0; i < toggles.getToggles().length; i++) {
            final int index = i;
            sync(() -> toggles.getToggles()[index].getValue() ? 1 : 0, value -> toggles.setToggle(index, value != 0));
        }
    }

    private void sync(IntSupplier get, IntConsumer set) {
        addDataSlot(new DataSlot() {
            public int get() { return get.getAsInt(); }
            public void set(int value) { set.accept(value); }
        });
    }

    private void setArea(int axis, int value) {
        BlockPos old = seal.getArea();
        seal.setArea(new BlockPos(axis == 0 ? value : old.getX(), axis == 1 ? value : old.getY(), axis == 2 ? value : old.getZ()));
    }

    public int category() { return category; }

    @Override
    public boolean stillValid(Player player) {
        var pos = seal.getSealPos();
        return !player.isSpectator() && pos != null && player.distanceToSqr(pos.pos.getX() + .5, pos.pos.getY() + .5, pos.pos.getZ() + .5) <= 64
                && player.getStringUUID().equals(seal.getOwner()) && player.level().hasChunkAt(pos.pos)
                && (player.level().isClientSide || SealHandler.getSealEntity(player.level(), pos) == seal);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!stillValid(player)) return false;
        boolean changed = true;
        if (id >= 0 && id < categories.length) category = categories[id];
        else if (category == 0 && (id == 25 || id == 26)) seal.setLocked(id == 25);
        else if (id == 27 || id == 28) seal.setRedstoneSensitive(id == 27);
        else if (category == 1 && seal.getSeal() instanceof ISealConfigFilter filter && (id == 20 || id == 21)) filter.setBlacklist(id == 20);
        else if (category == 3 && seal.getSeal() instanceof ISealConfigToggles toggles
                && ((id >= 30 && id < 30 + toggles.getToggles().length) || (id >= 60 && id < 60 + toggles.getToggles().length)))
            toggles.setToggle(id >= 60 ? id - 60 : id - 30, id < 60);
        else if (id == 80 && seal.getPriority() > -5) seal.setPriority((byte)(seal.getPriority() - 1));
        else if (id == 81 && seal.getPriority() < 5) seal.setPriority((byte)(seal.getPriority() + 1));
        else if (id == 82 && seal.getColor() > 0) seal.setColor((byte)(seal.getColor() - 1));
        else if (id == 83 && seal.getColor() < 16) seal.setColor((byte)(seal.getColor() + 1));
        else if (id >= 90 && id <= 95 && seal.getSeal() instanceof ISealConfigArea) {
            int axis = (id - 90) / 2 == 0 ? 1 : (id - 90) / 2 == 1 ? 0 : 2;
            BlockPos area = seal.getArea();
            int value = axis == 0 ? area.getX() : axis == 1 ? area.getY() : area.getZ();
            setArea(axis, Math.max(1, Math.min(8, value + (id % 2 == 0 ? -1 : 1))));
        } else changed = false;
        if (changed && !player.level().isClientSide) { seal.syncToClient(player.level()); broadcastChanges(); }
        return changed;
    }

    @Override
    public void clicked(int slot, int button, ClickType type, Player player) {
        if (!stillValid(player)) return;
        if (slot >= 0 && slot < filterSlots) {
            if (category != 1 || (button != 0 && button != 1) || (type != ClickType.PICKUP && type != ClickType.QUICK_MOVE)) return;
            if (player.level().isClientSide) return;
            ISealConfigFilter filter = (ISealConfigFilter)seal.getSeal();
            ItemStack carried = getCarried(), old = filter.getFilterSlot(slot);
            int size = filter.getFilterSlotSize(slot);
            if (button == 1) {
                if (!filter.hasStacksizeLimiters()) { old = ItemStack.EMPTY; size = 0; }
                else if (carried.isEmpty() && !old.isEmpty()) size -= type == ClickType.QUICK_MOVE ? 10 : 1;
                else if (!old.isEmpty() && ItemStack.isSameItemSameComponents(old, carried)) size -= carried.getCount();
                if (size < 0) { old = ItemStack.EMPTY; size = 0; }
            } else if (carried.isEmpty()) {
                if (filter.hasStacksizeLimiters() && !old.isEmpty()) size = Math.min(32767, size + (type == ClickType.QUICK_MOVE ? 10 : 1));
            } else {
                size = filter.hasStacksizeLimiters() && ItemStack.isSameItemSameComponents(old, carried)
                        ? Math.min(32767, size + carried.getCount()) : 0;
                old = carried.copyWithCount(1);
            }
            filter.setFilterSlot(slot, old);
            filter.setFilterSlotSize(slot, size);
            ghosts.setItem(slot, old.copy());
            seal.syncToClient(player.level());
            broadcastChanges();
            return;
        }
        super.clicked(slot, button, type, player);
    }

    @Override
    public boolean canDragTo(Slot slot) { return slot.index >= filterSlots && super.canDragTo(slot); }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) { return ItemStack.EMPTY; }
}
