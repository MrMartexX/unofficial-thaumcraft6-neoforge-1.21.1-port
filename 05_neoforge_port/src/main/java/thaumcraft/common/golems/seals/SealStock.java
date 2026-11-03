package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;

public class SealStock extends SealFiltered implements thaumcraft.api.golems.seals.ISealConfigToggles {
    private int delay;

    @Override
    public int getFilterSize() { return 9; }

    @Override
    public boolean hasStacksizeLimiters() { return true; }

    @Override
    public boolean isBlacklist() { return false; }

    @Override
    public String getKey() {
        return "thaumcraft:stock";
    }

    @Override
    public boolean canPlaceAt(Level level, BlockPos pos, Direction side) {
        return level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos, side) != null;
    }

    @Override
    public void tickSeal(Level level, ISealEntity seal) {
        if (delay++ % 20 != 0) return;
        var pos = seal.getSealPos();
        var handler = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos.pos, pos.face);
        if (handler == null) return;
        for (int filterSlot = 0; filterSlot < getFilterSize(); filterSlot++) {
            var requested = getFilterSlot(filterSlot).copy();
            if (requested.isEmpty()) continue;
            int present = 0;
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                var stack = handler.getStackInSlot(slot);
                if (matchesStack(requested, stack)) present += stack.getCount();
            }
            int missing = Math.min(requested.getMaxStackSize(), getFilterSlotSize(filterSlot) - present);
            if (missing <= 0) continue;
            requested.setCount(missing);
            int accepted = missing - net.neoforged.neoforge.items.ItemHandlerHelper.insertItemStacked(handler, requested.copy(), true).getCount();
            if (accepted > 0) {
                requested.setCount(accepted);
                thaumcraft.api.golems.GolemHelper.requestProvisioning(level, pos.pos, pos.face, requested);
            }
        }
    }

    @Override
    public void onTaskStarted(Level level, IGolemAPI golem, Task task) {
    }

    @Override
    public boolean onTaskCompletion(Level level, IGolemAPI golem, Task task) {
        golem.addRankXp(1);
        return true;
    }

    @Override
    public void onTaskSuspension(Level level, Task task) {
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return false;
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_stock.png");
    }

    @Override
    public void onRemoval(Level level, BlockPos pos, Direction side) {
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[0];
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.CLUMSY};
    }
}
