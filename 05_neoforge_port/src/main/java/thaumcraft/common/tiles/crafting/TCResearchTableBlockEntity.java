package thaumcraft.common.tiles.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.registry.TCBlockEntities;

public class TCResearchTableBlockEntity extends BlockEntity {
    public static final int SLOT_SCRIBING_TOOLS = 0;
    public static final int SLOT_PAPER = 1;
    private static final int SLOT_COUNT = 2;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public TCResearchTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.RESEARCH_TABLE.get(), pos, blockState);
    }

    public ItemStack getScribingTools() {
        return items.get(SLOT_SCRIBING_TOOLS);
    }

    public int getPaperCount() {
        return items.get(SLOT_PAPER).getCount();
    }

    public boolean canInsertPaper() {
        return items.get(SLOT_PAPER).getCount() < Items.PAPER.getDefaultInstance().getMaxStackSize();
    }

    public boolean setScribingTools(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IScribeTools)) {
            return false;
        }
        ItemStack stored = stack.copy();
        stored.setCount(1);
        items.set(SLOT_SCRIBING_TOOLS, stored);
        setChanged();
        return true;
    }

    public int insertPaper(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(Items.PAPER)) {
            return 0;
        }

        ItemStack current = items.get(SLOT_PAPER);
        int max = stack.getMaxStackSize();
        int inserted = Math.min(stack.getCount(), max - current.getCount());
        if (inserted <= 0) {
            return 0;
        }

        if (current.isEmpty()) {
            ItemStack stored = new ItemStack(Items.PAPER, inserted);
            items.set(SLOT_PAPER, stored);
        } else {
            current.grow(inserted);
        }
        setChanged();
        return inserted;
    }

    public void dropContents(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }

        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
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
