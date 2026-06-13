package thaumcraft.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.common.tiles.crafting.TileResearchTable;
import thaumcraft.init.ModBlocks;
import thaumcraft.init.ModMenuTypes;

import java.util.Set;

/**
 * ResearchTableMenu - Server-side menu for the Research Table.
 * 
 * Slots:
 * - 0: Scribing tools
 * - 1: Paper
 * - 2-28: Player inventory (3 rows of 9)
 * - 29-37: Player hotbar (9 slots)
 * 
 * Data:
 * - 0: Current inspiration
 * - 1: Starting inspiration
 * - 2: Has active theory (0/1)
 * - 3: Number of card choices
 */
public class ResearchTableMenu extends AbstractContainerMenu {
    
    private final TileResearchTable blockEntity;
    private final ContainerLevelAccess access;
    private final ContainerData data;
    
    // Client constructor (from network)
    public ResearchTableMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, extraData));
    }
    
    // Server constructor
    public ResearchTableMenu(int containerId, Inventory playerInventory, TileResearchTable blockEntity) {
        super(ModMenuTypes.RESEARCH_TABLE.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        
        // Create data container for syncing
        this.data = new SimpleContainerData(4);
        addDataSlots(this.data);
        
        // Scribing tools slot
        addSlot(new ScribingToolsSlot(blockEntity, TileResearchTable.SLOT_SCRIBING_TOOLS, 16, 207));
        
        // Paper slot
        addSlot(new PaperSlot(blockEntity, TileResearchTable.SLOT_PAPER, 34, 207));
        
        // Player inventory (3 rows) - positioned at bottom of GUI
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 56 + col * 18, 175 + row * 18));
            }
        }
        
        // Player hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 56 + col * 18, 233));
        }
    }
    
    private static TileResearchTable getBlockEntity(Inventory playerInventory, FriendlyByteBuf extraData) {
        BlockEntity be = playerInventory.player.level().getBlockEntity(extraData.readBlockPos());
        if (be instanceof TileResearchTable tile) {
            return tile;
        }
        throw new IllegalStateException("Block entity is not a TileResearchTable");
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.RESEARCH_TABLE.get());
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            
            // From table slots to player
            if (slotIndex < 2) {
                if (!moveItemStackTo(stackInSlot, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            }
            // From player inventory
            else {
                // Scribing tools
                if (stackInSlot.getItem() instanceof IScribeTools || stackInSlot.isDamageableItem()) {
                    if (!moveItemStackTo(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // Paper
                else if (stackInSlot.is(Items.PAPER)) {
                    if (!moveItemStackTo(stackInSlot, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // Between inventory and hotbar
                else if (slotIndex < 29) {
                    if (!moveItemStackTo(stackInSlot, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!moveItemStackTo(stackInSlot, 2, 29, false)) {
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
        
        return result;
    }
    
    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        
        // Sync theorycraft data
        ResearchTableData rtd = blockEntity.getResearchData();
        if (rtd != null) {
            data.set(0, rtd.inspiration);
            data.set(1, rtd.inspirationStart);
            data.set(2, 1);
            data.set(3, rtd.cardChoices.size());
        } else {
            data.set(0, 0);
            data.set(1, 0);
            data.set(2, 0);
            data.set(3, 0);
        }
    }
    
    // ==================== Button Handling ====================
    
    /**
     * Handle button clicks from the GUI.
     * Button IDs:
     * - 0: Start new theory
     * - 1: Finish theory
     * - 2: Abandon theory
     * - 10-12: Select card 0, 1, or 2
     * - 20: Draw new cards
     */
    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        switch (buttonId) {
            case 0 -> {
                // Start new theory
                if (!blockEntity.hasActiveTheory()) {
                    // Check for required materials
                    if (hasScribingTools() && hasPaper()) {
                        Set<String> aids = blockEntity.checkSurroundingAids();
                        blockEntity.startNewTheory(player, aids);
                        blockEntity.consumeInkFromTable();
                        blockEntity.consumePaperFromTable();
                        
                        // Draw initial cards
                        ResearchTableData data = blockEntity.getResearchData();
                        if (data != null) {
                            data.drawCards(3, player);
                        }
                        return true;
                    }
                }
            }
            case 1 -> {
                // Finish theory
                if (blockEntity.hasActiveTheory()) {
                    blockEntity.finishTheory(player);
                    return true;
                }
            }
            case 2 -> {
                // Abandon theory
                if (blockEntity.hasActiveTheory()) {
                    blockEntity.data = null;
                    blockEntity.syncTile(false);
                    blockEntity.setChanged();
                    return true;
                }
            }
            case 10, 11, 12 -> {
                // Select card
                int cardIndex = buttonId - 10;
                ResearchTableData data = blockEntity.getResearchData();
                if (data != null && cardIndex < data.cardChoices.size()) {
                    ResearchTableData.CardChoice choice = data.cardChoices.get(cardIndex);
                    if (!choice.selected && choice.card.getInspirationCost() <= data.inspiration) {
                        // Activate the card
                        if (choice.card.activate(player, data)) {
                            data.inspiration -= choice.card.getInspirationCost();
                            choice.selected = true;
                            data.placedCards++;
                            
                            // Consume ink for each card used
                            blockEntity.consumeInkFromTable();
                            
                            // Draw new cards if session not complete
                            if (!data.isComplete()) {
                                data.drawCards(data.bonusDraws > 0 ? 3 : 2, player);
                            }
                            
                            blockEntity.syncTile(false);
                            blockEntity.setChanged();
                            return true;
                        }
                    }
                }
            }
            case 20 -> {
                // Manual card draw (if allowed)
                ResearchTableData data = blockEntity.getResearchData();
                if (data != null && !data.isComplete() && data.cardChoices.isEmpty()) {
                    data.drawCards(data.bonusDraws > 0 ? 3 : 2, player);
                    blockEntity.syncTile(false);
                    blockEntity.setChanged();
                    return true;
                }
            }
        }
        return false;
    }
    
    // ==================== Accessors ====================
    
    public TileResearchTable getBlockEntity() {
        return blockEntity;
    }
    
    public int getInspiration() {
        return data.get(0);
    }
    
    public int getInspirationStart() {
        return data.get(1);
    }
    
    public boolean hasActiveTheory() {
        return data.get(2) == 1;
    }
    
    public int getCardChoiceCount() {
        return data.get(3);
    }
    
    public boolean hasScribingTools() {
        ItemStack tools = blockEntity.getItem(TileResearchTable.SLOT_SCRIBING_TOOLS);
        return !tools.isEmpty() && (tools.getItem() instanceof IScribeTools || tools.isDamageableItem());
    }
    
    public boolean hasPaper() {
        ItemStack paper = blockEntity.getItem(TileResearchTable.SLOT_PAPER);
        return !paper.isEmpty() && paper.is(Items.PAPER);
    }
    
    // ==================== Custom Slots ====================
    
    /** Slot for scribing tools */
    private static class ScribingToolsSlot extends Slot {
        public ScribingToolsSlot(TileResearchTable container, int slot, int x, int y) {
            super(container, slot, x, y);
        }
        
        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof IScribeTools || stack.isDamageableItem();
        }
    }
    
    /** Slot for paper */
    private static class PaperSlot extends Slot {
        public PaperSlot(TileResearchTable container, int slot, int x, int y) {
            super(container, slot, x, y);
        }
        
        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(Items.PAPER);
        }
    }
}
