package thaumcraft.api.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;

/**
 * Inventory and aura contract shared by the physical Arcane Workbench and addon terminals.
 *
 * <p>Slots {@code 0..8} are the 3x3 crafting matrix and slots {@code 9..14} are the primal
 * crystal slots in aer, ignis, aqua, terra, ordo, perditio order.</p>
 */
public interface IArcaneWorkbench {
    int MATRIX_SLOT_COUNT = 9;
    int CRYSTAL_SLOT_START = MATRIX_SLOT_COUNT;
    int CRYSTAL_SLOT_COUNT = 6;
    int SLOT_COUNT = MATRIX_SLOT_COUNT + CRYSTAL_SLOT_COUNT;

    CraftingInput craftingInput();

    Level arcaneLevel();

    BlockPos arcanePosition();

    int availableVis();

    boolean canSpendVis(int amount);

    boolean spendVis(int amount);

    ItemStack getArcaneItem(int slot);

    ItemStack removeArcaneItem(int slot, int amount);

    void setArcaneItem(int slot, ItemStack stack);

    void setArcaneChanged();
}
