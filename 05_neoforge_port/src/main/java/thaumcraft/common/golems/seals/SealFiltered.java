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
    private thaumcraft.api.golems.seals.ISealConfigToggles.SealToggle[] filterOptions;

    public thaumcraft.api.golems.seals.ISealConfigToggles.SealToggle[] getToggles() {
        if (filterOptions == null) {
            var options = new java.util.ArrayList<thaumcraft.api.golems.seals.ISealConfigToggles.SealToggle>();
            options.add(new thaumcraft.api.golems.seals.ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"));
            options.add(new thaumcraft.api.golems.seals.ISealConfigToggles.SealToggle(true, "pnbt", "golem.prop.nbt"));
            options.add(new thaumcraft.api.golems.seals.ISealConfigToggles.SealToggle(false, "pore", "golem.prop.ore"));
            options.add(new thaumcraft.api.golems.seals.ISealConfigToggles.SealToggle(false, "pmod", "golem.prop.mod"));
            String[] extras = this instanceof SealUse ? new String[]{"pleft:left", "pempty:empty", "pemptyhand:emptyhand", "psneak:sneak", "ppro:provision.wl"}
                    : this instanceof SealEmpty ? new String[]{"pcycle:cycle", "pleave:leave"}
                    : this instanceof SealProvide ? new String[]{"psing:single", "pleave:leave"}
                    : this instanceof SealFill ? new String[]{"pexist:exist"} : new String[0];
            for (String extra : extras) {
                String[] parts = extra.split(":", 2);
                options.add(new thaumcraft.api.golems.seals.ISealConfigToggles.SealToggle(false, parts[0], "golem.prop." + parts[1]));
            }
            filterOptions = options.toArray(thaumcraft.api.golems.seals.ISealConfigToggles.SealToggle[]::new);
        }
        return filterOptions;
    }

    public void setToggle(int index, boolean value) {
        if (index >= 0 && index < getToggles().length) getToggles()[index].setValue(value);
    }

    protected boolean option(String key) {
        for (var toggle : getToggles()) if (toggle.getKey().equals(key)) return toggle.getValue();
        return key.equals("pmeta") || key.equals("pnbt");
    }

    public void setSerializationRegistries(net.minecraft.core.HolderLookup.Provider registries) { this.registries = registries; }

    public SealFiltered() {
        this.filter = NonNullList.withSize(getFilterSize(), ItemStack.EMPTY);
        this.filterSize = NonNullList.withSize(getFilterSize(), 0);
    }

    @Override
    public int[] getGuiCategories() {
        boolean toggles = this instanceof thaumcraft.api.golems.seals.ISealConfigToggles;
        return this instanceof thaumcraft.api.golems.seals.ISealConfigArea
                ? toggles ? new int[]{CAT_AREA, CAT_FILTER, CAT_TOGGLES, CAT_PRIORITY, CAT_TAGS} : new int[]{CAT_AREA, CAT_FILTER, CAT_PRIORITY, CAT_TAGS}
                : toggles ? new int[]{CAT_FILTER, CAT_TOGGLES, CAT_PRIORITY, CAT_TAGS} : new int[]{CAT_FILTER, CAT_PRIORITY, CAT_TAGS};
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
        return matchesFilter(stack, false);
    }

    public boolean matchesFilter(ItemStack stack, boolean wildcardMetadata) {
        if (stack == null || stack.isEmpty()) return false;

        boolean hasAnyFilter = false;
        boolean matched = false;

        for (ItemStack f : filter) {
            if (!f.isEmpty()) {
                hasAnyFilter = true;
                if (matchesStack(f, stack, wildcardMetadata)) {
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

    public boolean matchesStack(ItemStack a, ItemStack b) {
        return matchesStack(a, b, false);
    }

    private boolean matchesStack(ItemStack a, ItemStack b, boolean wildcardMetadata) {
        if (a.isEmpty() || b.isEmpty()) return a.isEmpty() && b.isEmpty();
        var aId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(a.getItem());
        var bId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(b.getItem());
        if (option("pmod")) return aId.getNamespace().equals(bId.getNamespace());
        if (option("pore") && TCSealOreGroups.matches(a, b)) return true;
        if (option("pore") && a.getTags().anyMatch(tag -> {
            var id = tag.location();
            boolean legacy = id.getNamespace().equals("thaumcraft") && id.getPath().startsWith("legacy_ore_dictionary/");
            boolean common = id.getNamespace().equals("c") && id.getPath().matches("(?:ores|ingots|nuggets|plates|dusts|gems|storage_blocks)/.+");
            return (legacy || common) && b.is(tag);
        })) return true;
        var aLegacy = a.get(thaumcraft.common.registry.TCDataComponents.LEGACY_ITEM.get());
        var bLegacy = b.get(thaumcraft.common.registry.TCDataComponents.LEGACY_ITEM.get());
        boolean same = a.getItem() == b.getItem() || (aLegacy != null && bLegacy != null && aId.getNamespace().equals(bId.getNamespace())
                && aLegacy.family().equals(bLegacy.family()));
        if (!same && (wildcardMetadata || !option("pmeta"))) same = TCSealItemIdentity.sameLegacyItem(a, b);
        if (!same) return false;
        int aDamage = aLegacy != null ? aLegacy.metadata() : a.getDamageValue();
        int bDamage = bLegacy != null ? bLegacy.metadata() : b.getDamageValue();
        if (!wildcardMetadata && option("pmeta") && aDamage != 32767 && bDamage != 32767 && aDamage != bDamage) return false;
        if (!option("pnbt")) return true;
        ItemStack left = a.copy(), right = b.copy();
        left.remove(net.minecraft.core.component.DataComponents.DAMAGE);
        right.remove(net.minecraft.core.component.DataComponents.DAMAGE);
        left.remove(thaumcraft.common.registry.TCDataComponents.LEGACY_ITEM.get());
        right.remove(thaumcraft.common.registry.TCDataComponents.LEGACY_ITEM.get());
        return left.getComponentsPatch().equals(right.getComponentsPatch());
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
        for (var toggle : getToggles()) if (tag.contains(toggle.getKey())) toggle.setValue(tag.getBoolean(toggle.getKey()));
        int[] sizes = tag.getIntArray("sizes");
        for (int i = 0; i < filterSize.size(); i++) filterSize.set(i, i < sizes.length ? sizes[i] : 0);
    }

    @Override
    public void writeCustomNBT(CompoundTag tag) {
        tag.putBoolean("bl", blacklist);
        for (var toggle : getToggles()) tag.putBoolean(toggle.getKey(), toggle.getValue());
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
