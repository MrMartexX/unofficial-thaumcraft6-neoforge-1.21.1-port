package thaumcraft.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.common.menu.slot.PotionSlot;
import thaumcraft.common.tiles.devices.TilePotionSprayer;
import thaumcraft.init.ModMenuTypes;

/**
 * PotionSprayerMenu - Menu for the Potion Sprayer block.
 * 
 * The Potion Sprayer disperses potion effects in an area.
 * Accepts regular, splash, and lingering potions.
 * 
 * Slot Layout:
 * - 0: Potion slot
 * - 1-27: Player inventory
 * - 28-36: Player hotbar
 */
public class PotionSprayerMenu extends AbstractContainerMenu {
    
    private final TilePotionSprayer blockEntity;
    
    // Client constructor
    public PotionSprayerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, extraData));
    }
    
    // Server constructor
    public PotionSprayerMenu(int containerId, Inventory playerInventory, TilePotionSprayer blockEntity) {
        super(ModMenuTypes.POTION_SPRAYER.get(), containerId);
        this.blockEntity = blockEntity;
        
        // Slot 0: Potion input
        addSlot(new PotionSlot(blockEntity, 0, 56, 64));
        
        // Player inventory (slightly offset from standard)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 16 + col * 18, 151 + row * 18));
            }
        }
        
        // Player hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 16 + col * 18, 209));
        }
    }
    
    private static TilePotionSprayer getBlockEntity(Inventory playerInventory, FriendlyByteBuf extraData) {
        BlockEntity be = playerInventory.player.level().getBlockEntity(extraData.readBlockPos());
        if (be instanceof TilePotionSprayer tile) {
            return tile;
        }
        throw new IllegalStateException("Block entity is not a TilePotionSprayer");
    }
    
    @Override
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            
            if (index == 0) {
                // Move from potion slot to player inventory
                if (!moveItemStackTo(stackInSlot, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (PotionSlot.isValidPotion(stackInSlot)) {
                // Move valid potion to potion slot
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
    
    public TilePotionSprayer getBlockEntity() {
        return blockEntity;
    }
}
