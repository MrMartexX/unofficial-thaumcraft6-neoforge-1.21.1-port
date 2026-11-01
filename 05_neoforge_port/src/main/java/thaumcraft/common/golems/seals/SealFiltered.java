package thaumcraft.common.golems.seals;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealGui;

public abstract class SealFiltered implements ISeal, ISealGui, ISealConfigFilter {

    protected NonNullList<ItemStack> filter;
    protected NonNullList<Integer> filterSize;
    protected boolean blacklist = true;

    public SealFiltered() {
        this.filter = NonNullList.withSize(getFilterSize(), ItemStack.EMPTY);
        this.filterSize = NonNullList.withSize(getFilterSize(), 0);
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{CAT_PRIORITY, CAT_FILTER, CAT_AREA};
    }

    @Override
    public int getFilterSize() {
        return 1;
    }

    @Override
    public NonNullList<ItemStack> getInv() {
        return filter;
    }

    @Override
    public NonNullList<Integer> getSizes() {
        return filterSize;
    }

    @Override
    public ItemStack getFilterSlot(int i) {
        return i >= 0 && i < filter.size() ? filter.get(i) : ItemStack.EMPTY;
    }

    @Override
    public int getFilterSlotSize(int i) {
        return i >= 0 && i < filterSize.size() ? filterSize.get(i) : 0;
    }

    @Override
    public void setFilterSlot(int i, ItemStack stack) {
        if (i >= 0 && i < filter.size()) {
            filter.set(i, stack.copy());
        }
    }

    @Override
    public void setFilterSlotSize(int i, int size) {
        if (i >= 0 && i < filterSize.size()) {
            filterSize.set(i, size);
        }
    }

    @Override
    public boolean isBlacklist() {
        return blacklist;
    }

    @Override
    public void setBlacklist(boolean black) {
        this.blacklist = black;
    }

    @Override
    public boolean hasStacksizeLimiters() {
        return false;
    }

    public boolean matchesFilter(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        boolean hasAnyFilter = false;
        boolean matched = false;

        for (ItemStack f : filter) {
            if (!f.isEmpty()) {
                hasAnyFilter = true;
                if (ItemStack.isSameItemSameComponents(f, stack)) {
                    matched = true;
                    break;
                }
            }
        }

        if (blacklist) {
            return !matched;
        } else {
            return matched;
        }
    }

    @Override
    public void readCustomNBT(CompoundTag tag) {
        this.filter = NonNullList.withSize(getFilterSize(), ItemStack.EMPTY);
        if (tag.contains("filter", Tag.TAG_LIST)) {
            ListTag list = tag.getList("filter", Tag.TAG_COMPOUND);
            var lookup = net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);
            for (int i = 0; i < list.size() && i < filter.size(); i++) {
                final int slot = i;
                CompoundTag itemTag = list.getCompound(i);
                try {
                    ItemStack s = ItemStack.parseOptional(lookup, itemTag);
                    if (!s.isEmpty()) {
                        filter.set(slot, s);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        this.blacklist = tag.getBoolean("bl");
    }

    @Override
    public void writeCustomNBT(CompoundTag tag) {
        tag.putBoolean("bl", blacklist);
        ListTag list = new ListTag();
        var lookup = net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);
        for (ItemStack stack : filter) {
            CompoundTag itemTag = new CompoundTag();
            if (!stack.isEmpty()) {
                try {
                    itemTag = (CompoundTag) stack.save(lookup);
                } catch (Exception ignored) {
                }
            }
            list.add(itemTag);
        }
        tag.put("filter", list);
    }
}
