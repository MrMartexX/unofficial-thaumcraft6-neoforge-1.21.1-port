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
    private net.minecraft.core.HolderLookup.Provider registries = net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);

    public void setSerializationRegistries(net.minecraft.core.HolderLookup.Provider registries) { this.registries = registries; }

    public SealFiltered() {
        this.filter = NonNullList.withSize(getFilterSize(), ItemStack.EMPTY);
        this.filterSize = NonNullList.withSize(getFilterSize(), 0);
    }

    @Override
    public int[] getGuiCategories() {
        return this instanceof thaumcraft.api.golems.seals.ISealConfigArea
                ? new int[]{CAT_AREA, CAT_FILTER, CAT_PRIORITY, CAT_TAGS}
                : new int[]{CAT_FILTER, CAT_PRIORITY, CAT_TAGS};
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
            filter.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
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
            for (int i = 0; i < list.size() && i < filter.size(); i++) {
                final int slot = i;
                CompoundTag itemTag = list.getCompound(i);
                if (!itemTag.isEmpty()) filter.set(slot, ItemStack.parse(registries, itemTag)
                        .orElseThrow(() -> new IllegalStateException("Invalid saved seal filter at slot " + slot)).copyWithCount(1));
            }
        }
        this.blacklist = tag.getBoolean("bl");
        int[] sizes = tag.getIntArray("sizes");
        for (int i = 0; i < filterSize.size(); i++) filterSize.set(i, i < sizes.length ? sizes[i] : 0);
    }

    @Override
    public void writeCustomNBT(CompoundTag tag) {
        tag.putBoolean("bl", blacklist);
        tag.putIntArray("sizes", filterSize.stream().mapToInt(Integer::intValue).toArray());
        ListTag list = new ListTag();
        for (ItemStack stack : filter) {
            CompoundTag itemTag = new CompoundTag();
            if (!stack.isEmpty()) {
                itemTag = (CompoundTag) stack.save(registries);
            }
            list.add(itemTag);
        }
        tag.put("filter", list);
    }
}
