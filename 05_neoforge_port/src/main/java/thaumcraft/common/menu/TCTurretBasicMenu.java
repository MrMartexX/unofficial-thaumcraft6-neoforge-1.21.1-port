package thaumcraft.common.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.entities.construct.TCTurretCrossbowEntity;
import thaumcraft.common.registry.TCMenus;

public class TCTurretBasicMenu extends AbstractContainerMenu {
    protected final TCTurretCrossbowEntity turret;
    protected final Container ammoContainer;

    public TCTurretBasicMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(TCMenus.TURRET_BASIC.get(), containerId, inventory, clientTurret(inventory, extraData));
    }

    public TCTurretBasicMenu(int containerId, Inventory inventory, TCTurretCrossbowEntity turret) {
        this(TCMenus.TURRET_BASIC.get(), containerId, inventory, turret);
    }

    protected TCTurretBasicMenu(MenuType<?> menuType, int containerId, Inventory inventory, TCTurretCrossbowEntity turret) {
        super(menuType, containerId);
        this.turret = turret;
        this.ammoContainer = turret != null ? turret.getAmmoInventory() : new SimpleContainer(1);

        addTurretSlot();
        bindPlayerInventory(inventory);
    }

    protected void addTurretSlot() {
        // Slot 0 at 80, 29
        addSlot(new Slot(ammoContainer, 0, 80, 29) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !stack.isEmpty() && stack.getItem() instanceof ArrowItem;
            }
        });
    }

    protected void bindPlayerInventory(Inventory inventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    public TCTurretCrossbowEntity getTurret() {
        return turret;
    }

    @Override
    public boolean stillValid(Player player) {
        return turret != null && turret.isAlive() && turret.distanceToSqr(player) <= 64.0D;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (slotIndex == 0) {
                // Move from ammo slot to player inventory
                if (!this.moveItemStackTo(stackInSlot, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Move from player inventory to ammo slot
                if (stackInSlot.getItem() instanceof ArrowItem) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 1 && slotIndex < 28) {
                    if (!this.moveItemStackTo(stackInSlot, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 28 && slotIndex < 37) {
                    if (!this.moveItemStackTo(stackInSlot, 1, 28, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    private static TCTurretCrossbowEntity clientTurret(Inventory inventory, RegistryFriendlyByteBuf extraData) {
        if (extraData == null) {
            return null;
        }
        int id = extraData.readInt();
        return inventory.player.level().getEntity(id) instanceof TCTurretCrossbowEntity t ? t : null;
    }
}
