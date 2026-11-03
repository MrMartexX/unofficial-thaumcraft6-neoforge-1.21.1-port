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

public class SealFill extends SealFiltered implements ISealConfigArea {

    private int delay = 0;
    private int watchedTask = Integer.MIN_VALUE;

    @Override
    public String getKey() {
        return "thaumcraft:fill";
    }

    @Override
    public boolean canPlaceAt(Level level, BlockPos pos, Direction side) {
        return !level.isEmptyBlock(pos);
    }

    @Override
    public void tickSeal(Level level, ISealEntity seal) {
        if (delay++ % 20 != 0) return;

        Task previous = TaskHandler.getTask(level, watchedTask);
        if (previous != null && !previous.isReserved() && !previous.isCompleted() && !previous.isSuspended()) return;
        Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
        task.setPriority(seal.getPriority());
        TaskHandler.addTask(level, task);
        watchedTask = task.getId();
    }

    @Override
    public void onTaskStarted(Level level, IGolemAPI golem, Task task) {
    }

    @Override
    public boolean onTaskCompletion(Level level, IGolemAPI golem, Task task) {
        var pos = task.getSealPos();
        var handler = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos.pos, pos.face);
        for (ItemStack carried : golem.getCarrying()) {
            if (!matchesFilter(carried)) continue;
            ItemStack request = carried.copy();
            if (handler != null) {
                ItemStack leftover = net.neoforged.neoforge.items.ItemHandlerHelper.insertItemStacked(handler, request.copy(), true);
                request.setCount(request.getCount() - leftover.getCount());
            }
            if (request.isEmpty()) continue;
            ItemStack moved = golem.dropItem(request);
            if (handler == null) {
                net.minecraft.world.level.block.Block.popResource(level, pos.pos.relative(pos.face), moved);
            } else {
                ItemStack leftover = net.neoforged.neoforge.items.ItemHandlerHelper.insertItemStacked(handler, moved, false);
                ItemStack excess = golem.holdItem(leftover);
                if (!excess.isEmpty()) net.minecraft.world.level.block.Block.popResource(level, pos.pos.relative(pos.face), excess);
            }
            golem.swingArm();
            break;
        }
        golem.addRankXp(1);
        task.setSuspended(true);
        return true;
    }

    @Override
    public void onTaskSuspension(Level level, Task task) {
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        var pos = task.getSealPos();
        var handler = golem.getGolemWorld().getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos.pos, pos.face);
        for (ItemStack carried : golem.getCarrying()) {
            if (matchesFilter(carried) && (handler == null
                    || net.neoforged.neoforge.items.ItemHandlerHelper.insertItemStacked(handler, carried.copy(), true).getCount() < carried.getCount())) return true;
        }
        return false;
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_fill.png");
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
        return new EnumGolemTrait[0];
    }
}
