package thaumcraft.common.tiles.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.crafting.IArcaneWorkbench;
import thaumcraft.common.crafting.arcane.TCArcaneWorkbenchCrafting;
import thaumcraft.common.menu.TCArcaneWorkbenchMenu;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;

public class TCArcaneWorkbenchBlockEntity extends BlockEntity implements Container, MenuProvider, IArcaneWorkbench {
    public static final int MATRIX_SLOT_START = 0;
    public static final int MATRIX_SLOT_COUNT = 9;
    public static final int CRYSTAL_SLOT_START = MATRIX_SLOT_START + MATRIX_SLOT_COUNT;
    public static final int CRYSTAL_SLOT_COUNT = 6;
    public static final int SLOT_COUNT = MATRIX_SLOT_COUNT + CRYSTAL_SLOT_COUNT;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public TCArcaneWorkbenchBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.ARCANE_WORKBENCH.get(), pos, blockState);
    }

    public CraftingInput craftingInput() {
        NonNullList<ItemStack> matrix = NonNullList.withSize(MATRIX_SLOT_COUNT, ItemStack.EMPTY);
        for (int slot = 0; slot < MATRIX_SLOT_COUNT; slot++) {
            matrix.set(slot, items.get(slot).copy());
        }
        return CraftingInput.of(3, 3, matrix);
    }

    @Override
    public Level arcaneLevel() {
        return level;
    }

    @Override
    public BlockPos arcanePosition() {
        return worldPosition;
    }

    @Override
    public ItemStack getArcaneItem(int slot) {
        return getItem(slot);
    }

    @Override
    public ItemStack removeArcaneItem(int slot, int amount) {
        return removeItem(slot, amount);
    }

    @Override
    public void setArcaneItem(int slot, ItemStack stack) {
        setItem(slot, stack);
    }

    @Override
    public void setArcaneChanged() {
        setChanged();
    }

    public int availableVis() {
        if (level == null) {
            return 0;
        }
        if (hasWorkbenchCharger()) {
            return availableVisInNineChunks();
        }
        if (level instanceof ServerLevel serverLevel) {
            AuraChunk chunk = AuraHandler.ensureAuraChunk(serverLevel, new ChunkPos(worldPosition));
            return (int) chunk.getVis();
        }
        return (int) AuraHandler.getVis(level, worldPosition);
    }

    public boolean canSpendVis(int amount) {
        if (amount <= 0) {
            return true;
        }
        return level instanceof ServerLevel && availableVis() >= amount;
    }

    public boolean spendVis(int amount) {
        if (amount <= 0) {
            return true;
        }
        if (!(level instanceof ServerLevel)) {
            return false;
        }
        if (!hasWorkbenchCharger()) {
            return AuraHandler.drainVis(level, worldPosition, amount, false) >= amount;
        }
        return spendVisFromNineChunks(amount);
    }

    public boolean hasWorkbenchCharger() {
        return level != null && level.getBlockState(worldPosition.above()).is(TCBlocks.ARCANE_WORKBENCH_CHARGER.get());
    }

    private int availableVisInNineChunks() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return 0;
        }
        int total = 0;
        ChunkPos center = new ChunkPos(worldPosition);
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                AuraChunk chunk = AuraHandler.ensureAuraChunk(serverLevel, new ChunkPos(center.x + xx, center.z + zz));
                total += (int) chunk.getVis();
            }
        }
        return total;
    }

    private boolean spendVisFromNineChunks(int amount) {
        if (!canSpendVis(amount)) {
            return false;
        }
        int remaining = amount;
        int chunkDrain = Math.max(1, amount / 9);
        int attempts = 0;
        while (remaining > 0) {
            attempts++;
            for (int xx = -1; xx <= 1; xx++) {
                for (int zz = -1; zz <= 1; zz++) {
                    if (chunkDrain > remaining) {
                        chunkDrain = remaining;
                    }
                    remaining -= (int) AuraHandler.drainVis(level, worldPosition.offset(xx * 16, 0, zz * 16), chunkDrain, false);
                    if (remaining <= 0 || attempts > 1000) {
                        return remaining <= 0;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) {
            setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= items.size()) {
            return;
        }
        ItemStack stored = stack.copy();
        stored.limitSize(getMaxStackSize(stored));
        items.set(slot, stored);
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot >= MATRIX_SLOT_START && slot < CRYSTAL_SLOT_START) {
            return true;
        }
        int crystalIndex = slot - CRYSTAL_SLOT_START;
        return crystalIndex >= 0
                && crystalIndex < TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.size()
                && TCArcaneWorkbenchCrafting.isCrystal(stack, TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.get(crystalIndex));
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.thaumcraft.arcane_workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new TCArcaneWorkbenchMenu(containerId, playerInventory, this);
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
    }

    public void dropContents(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
                items.set(slot, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int slot = 0; slot < items.size(); slot++) {
            items.set(slot, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, items, registries);
    }
}
