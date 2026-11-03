package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;

public class SealEmpty extends SealFiltered {

    private int delay = 0;
    private int filterIndex;
    private final java.util.Map<Integer, ItemStack> samples = new java.util.HashMap<>();

    @Override
    public String getKey() {
        return "thaumcraft:empty";
    }

    @Override
    public boolean canPlaceAt(Level level, BlockPos pos, Direction side) {
        return level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos, side) != null;
    }

    @Override
    public void tickSeal(Level level, ISealEntity seal) {
        if (delay++ % 20 != 0) return;

        samples.keySet().removeIf(id -> TaskHandler.getTask(level, id) == null);
        var handler = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, seal.getSealPos().pos, seal.getSealPos().face);
        ItemStack sample = findSample(handler);
        if (sample.isEmpty()) return;
        Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
        task.setPriority(seal.getPriority());
        task.setLifespan((short)5);
        samples.put(task.getId(), sample);
        TaskHandler.addTask(level, task);
    }

    @Override
    public void onTaskStarted(Level level, IGolemAPI golem, Task task) {
    }

    @Override
    public boolean onTaskCompletion(Level level, IGolemAPI golem, Task task) {
        var pos = task.getSealPos();
        var handler = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos.pos, pos.face);
        ItemStack sample = samples.getOrDefault(task.getId(), findSample(handler));
        if (handler != null && !sample.isEmpty()) {
            int limit = Math.min(sample.getCount(), golem.canCarryAmount(sample));
            if (option("pleave")) limit = Math.min(limit, Math.max(0, countMatching(handler, sample) - 1));
            for (int slot = 0; slot < handler.getSlots() && limit > 0; slot++) {
                ItemStack preview = handler.extractItem(slot, limit, true);
                if (!matchesStack(sample, preview)) continue;
                ItemStack extracted = handler.extractItem(slot, limit, false);
                limit -= extracted.getCount();
                ItemStack leftover = golem.holdItem(extracted);
                if (!leftover.isEmpty()) net.minecraft.world.level.block.Block.popResource(level, pos.pos.relative(pos.face), leftover);
                golem.swingArm();
            }
        }
        samples.remove(task.getId());
        filterIndex++;
        task.setSuspended(true);
        return true;
    }

    @Override
    public void onTaskSuspension(Level level, Task task) {
        samples.remove(task.getId());
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        var pos = task.getSealPos();
        var handler = golem.getGolemWorld().getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos.pos, pos.face);
        if (handler == null) return false;
        ItemStack sample = samples.computeIfAbsent(task.getId(), id -> findSample(handler));
        return !sample.isEmpty() && golem.canCarry(sample, true);
    }

    private ItemStack findSample(net.neoforged.neoforge.items.IItemHandler handler) {
        if (handler == null) return ItemStack.EMPTY;
        java.util.List<ItemStack> active = filter.stream().filter(stack -> !stack.isEmpty()).toList();
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack candidate = handler.extractItem(slot, Integer.MAX_VALUE, true);
            boolean match = option("pcycle") && !isBlacklist() && !active.isEmpty()
                    ? matchesStack(active.get(Math.floorMod(filterIndex, active.size())), candidate) : matchesFilter(candidate);
            if (!match) continue;
            int count = option("pleave") ? Math.min(candidate.getCount(), Math.max(0, countMatching(handler, candidate) - 1)) : candidate.getCount();
            if (count > 0) return candidate.copyWithCount(count);
        }
        return ItemStack.EMPTY;
    }

    private int countMatching(net.neoforged.neoforge.items.IItemHandler handler, ItemStack sample) {
        int count = 0;
        for (int slot = 0; slot < handler.getSlots(); slot++) if (matchesStack(sample, handler.getStackInSlot(slot))) count += handler.getStackInSlot(slot).getCount();
        return count;
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_empty.png");
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
