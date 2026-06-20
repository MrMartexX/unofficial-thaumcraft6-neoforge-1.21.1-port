package thaumcraft.common.crafting.infusion;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/** Persistent server-owned progress for the legacy infusion craft cycle. */
public final class TCInfusionCycleState {
    public static final int BASE_CYCLE_TIME = 10;
    public static final int BASE_CYCLE_DELAY = BASE_CYCLE_TIME / 2;
    public static final int COMPONENT_BEAM_COUNTDOWN = 5;
    private static final int MAX_COMPONENTS = 64;

    private final AspectList remainingAspects;
    private final ArrayList<ItemStack> pendingComponents;
    private int itemCountdown;
    private int cycleDelay;
    private long completedCycles;

    private TCInfusionCycleState(
            AspectList remainingAspects,
            List<ItemStack> pendingComponents,
            int itemCountdown,
            int cycleDelay,
            long completedCycles
    ) {
        this.remainingAspects = remainingAspects == null ? new AspectList() : remainingAspects.copy();
        this.pendingComponents = new ArrayList<>();
        if (pendingComponents != null) {
            for (ItemStack stack : pendingComponents) {
                if (stack != null && !stack.isEmpty() && this.pendingComponents.size() < MAX_COMPONENTS) {
                    this.pendingComponents.add(stack.copyWithCount(1));
                }
            }
        }
        this.itemCountdown = Math.max(0, itemCountdown);
        this.cycleDelay = Math.max(1, cycleDelay);
        this.completedCycles = Math.max(0L, completedCycles);
    }

    public static TCInfusionCycleState start(TCInfusionCraftingPlan plan) {
        if (plan == null) {
            return null;
        }
        return new TCInfusionCycleState(
                plan.requiredAspects(),
                plan.components(),
                0,
                BASE_CYCLE_DELAY,
                0L
        );
    }

    public AspectList remainingAspects() {
        return remainingAspects.copy();
    }

    public int remainingAspectAmount() {
        return remainingAspects.visSize();
    }

    public Aspect currentAspect() {
        for (Aspect aspect : remainingAspects.getAspects()) {
            if (remainingAspects.getAmount(aspect) > 0) {
                return aspect;
            }
        }
        return null;
    }

    public boolean consumeAspectPoint(Aspect aspect) {
        if (aspect == null || remainingAspects.getAmount(aspect) <= 0) {
            return false;
        }
        remainingAspects.reduce(aspect, 1);
        return true;
    }

    public List<ItemStack> pendingComponents() {
        return pendingComponents.stream().map(ItemStack::copy).toList();
    }

    public int pendingComponentCount() {
        return pendingComponents.size();
    }

    public ItemStack pendingComponent(int index) {
        return index < 0 || index >= pendingComponents.size()
                ? ItemStack.EMPTY
                : pendingComponents.get(index).copy();
    }

    public ItemStack removePendingComponent(int index) {
        if (index < 0 || index >= pendingComponents.size()) {
            return ItemStack.EMPTY;
        }
        return pendingComponents.remove(index).copy();
    }

    public int itemCountdown() {
        return itemCountdown;
    }

    public void beginComponentCountdown() {
        itemCountdown = COMPONENT_BEAM_COUNTDOWN;
    }

    public boolean advanceComponentCountdown() {
        if (itemCountdown <= 1) {
            itemCountdown = 0;
            return true;
        }
        itemCountdown--;
        return false;
    }

    public int cycleDelay() {
        return cycleDelay;
    }

    public long completedCycles() {
        return completedCycles;
    }

    public void recordCycle() {
        completedCycles++;
    }

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        remainingAspects.writeToNBT(tag, "RemainingAspects");
        tag.putInt("PendingComponentCount", pendingComponents.size());
        tag.putInt("ItemCountdown", itemCountdown);
        tag.putInt("CycleDelay", cycleDelay);
        tag.putLong("CompletedCycles", completedCycles);

        NonNullList<ItemStack> stacks = NonNullList.withSize(pendingComponents.size(), ItemStack.EMPTY);
        for (int index = 0; index < pendingComponents.size(); index++) {
            stacks.set(index, pendingComponents.get(index).copy());
        }
        CompoundTag stacksTag = new CompoundTag();
        ContainerHelper.saveAllItems(stacksTag, stacks, registries);
        tag.put("PendingComponents", stacksTag);
        return tag;
    }

    public static TCInfusionCycleState load(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag == null) {
            return null;
        }
        int componentCount = Math.max(0, tag.getInt("PendingComponentCount"));
        if (componentCount > MAX_COMPONENTS) {
            return null;
        }

        AspectList aspects = new AspectList();
        aspects.readFromNBT(tag, "RemainingAspects");
        NonNullList<ItemStack> stacks = NonNullList.withSize(componentCount, ItemStack.EMPTY);
        if (tag.contains("PendingComponents", Tag.TAG_COMPOUND)) {
            ContainerHelper.loadAllItems(tag.getCompound("PendingComponents"), stacks, registries);
        }
        ArrayList<ItemStack> components = new ArrayList<>(componentCount);
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                return null;
            }
            components.add(stack.copyWithCount(1));
        }
        return new TCInfusionCycleState(
                aspects,
                components,
                tag.getInt("ItemCountdown"),
                tag.getInt("CycleDelay"),
                tag.getLong("CompletedCycles")
        );
    }
}
