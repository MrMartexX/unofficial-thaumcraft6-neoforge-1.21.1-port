package thaumcraft.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;
import thaumcraft.common.entities.construct.EntityTurretCrossbowAdvanced;
import thaumcraft.common.menu.slot.ArrowSlot;
import thaumcraft.init.ModMenuTypes;

/**
 * TurretMenu - Menu for crossbow turrets (basic and advanced).
 * 
 * The turret holds arrows that it fires at targets.
 * Advanced turrets have additional targeting options.
 * 
 * Slot Layout:
 * - 0: Arrow slot
 * - 1-27: Player inventory
 * - 28-36: Player hotbar
 */
public class TurretMenu extends AbstractContainerMenu {
    
    private final EntityTurretCrossbow turret;
    private final boolean isAdvanced;
    
    // Client constructor
    public TurretMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, getEntity(playerInventory, extraData));
    }
    
    // Server constructor
    public TurretMenu(int containerId, Inventory playerInventory, EntityTurretCrossbow turret) {
        super(getMenuType(turret), containerId);
        this.turret = turret;
        this.isAdvanced = turret instanceof EntityTurretCrossbowAdvanced;
        
        // Slot 0: Arrow slot - different position for basic vs advanced
        int arrowX = isAdvanced ? 42 : 80;
        addSlot(new ArrowSlot(turret, 0, arrowX, 29));
        
        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        
        // Player hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    
    private static MenuType<?> getMenuType(EntityTurretCrossbow turret) {
        return turret instanceof EntityTurretCrossbowAdvanced 
                ? ModMenuTypes.TURRET_ADVANCED.get() 
                : ModMenuTypes.TURRET_BASIC.get();
    }
    
    private static EntityTurretCrossbow getEntity(Inventory playerInventory, FriendlyByteBuf extraData) {
        int entityId = extraData.readInt();
        Entity entity = playerInventory.player.level().getEntity(entityId);
        if (entity instanceof EntityTurretCrossbow turret) {
            return turret;
        }
        throw new IllegalStateException("Entity is not a turret crossbow");
    }
    
    @Override
    public boolean clickMenuButton(Player player, int button) {
        // Advanced turret targeting options
        if (isAdvanced && turret instanceof EntityTurretCrossbowAdvanced advanced) {
            switch (button) {
                case 1:
                    advanced.setTargetAnimal(!advanced.getTargetAnimal());
                    return true;
                case 2:
                    advanced.setTargetMob(!advanced.getTargetMob());
                    return true;
                case 3:
                    advanced.setTargetPlayer(!advanced.getTargetPlayer());
                    return true;
                case 4:
                    advanced.setTargetFriendly(!advanced.getTargetFriendly());
                    return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return turret.isAlive() && turret.distanceTo(player) < 8.0f;
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            
            if (index == 0) {
                // Move from arrow slot to player inventory
                if (!moveItemStackTo(stackInSlot, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (ArrowSlot.isValidArrow(stackInSlot)) {
                // Move arrows to arrow slot
                if (!moveItemStackTo(stackInSlot, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 1 && index < 28) {
                // Move from inventory to hotbar
                if (!moveItemStackTo(stackInSlot, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 28 && index < 37) {
                // Move from hotbar to inventory
                if (!moveItemStackTo(stackInSlot, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (stackInSlot.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(player, stackInSlot);
        }
        
        return result;
    }
    
    public EntityTurretCrossbow getTurret() {
        return turret;
    }
    
    public boolean isAdvanced() {
        return isAdvanced;
    }
}
