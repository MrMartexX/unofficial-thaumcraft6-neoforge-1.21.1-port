package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;

public class SealProvide extends SealFiltered implements ISealConfigArea {
    private int delay;

    @Override
    public int getFilterSize() { return 9; }

    @Override
    public String getKey() {
        return "thaumcraft:provider";
    }

    @Override
    public boolean canPlaceAt(Level level, BlockPos pos, Direction side) {
        return !level.isEmptyBlock(pos);
    }

    @Override
    public void tickSeal(Level level, ISealEntity seal) {
        var requests = GolemHelper.provisionRequests.get(level.dimension());
        if (requests == null) return;
        if (delay % 100 == 0) requests.removeIf(request -> request.isInvalid()
                || (request.getLinkedTask() != null && (request.getLinkedTask().isCompleted() || request.getLinkedTask().isSuspended())));
        if (delay++ % 20 != 0) return;
        var pos = seal.getSealPos();
        var inventory = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos.pos, pos.face);
        if (inventory == null) return;
        for (var request : requests) {
            if (request.isInvalid() || request.getLinkedTask() != null || !matchesFilter(request.getStack())) continue;
            BlockPos target = request.getSeal() != null ? request.getSeal().getSealPos().pos
                    : request.getEntity() != null ? request.getEntity().blockPosition() : request.getPos();
            if (target == null || target.distSqr(pos.pos) >= 4096) continue;
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                ItemStack available = inventory.extractItem(slot, 1, true);
                if (!ItemStack.isSameItemSameComponents(available, request.getStack())) continue;
                Task task = new Task(pos, pos.pos);
                task.setPriority(request.getSeal() == null ? (byte) 5 : request.getSeal().getPriority());
                task.setLifespan((short)(request.getSeal() == null ? 31000 : 10));
                task.setLinkedProvision(request);
                request.setLinkedTask(task);
                TaskHandler.addTask(level, task);
                return;
            }
        }
    }

    @Override
    public void onTaskStarted(Level level, IGolemAPI golem, Task task) {
    }

    @Override
    public boolean onTaskCompletion(Level level, IGolemAPI golem, Task task) {
        var request = task.getLinkedProvision();
        if (request == null || request.isInvalid()) { task.setSuspended(true); return true; }
        var source = task.getSealPos();
        if (task.getData() == 0) {
            var inventory = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, source.pos, source.face);
            int needed = Math.min(request.getStack().getCount(), golem.canCarryAmount(request.getStack()));
            int extracted = 0;
            if (inventory != null) {
                for (int slot = 0; slot < inventory.getSlots() && needed > 0; slot++) {
                    ItemStack preview = inventory.extractItem(slot, needed, true);
                    if (!ItemStack.isSameItemSameComponents(preview, request.getStack())) continue;
                    ItemStack taken = inventory.extractItem(slot, needed, false);
                    int count = taken.getCount();
                    ItemStack leftover = golem.holdItem(taken);
                    extracted += count - leftover.getCount();
                    needed -= count;
                    if (!leftover.isEmpty()) net.minecraft.world.level.block.Block.popResource(level, source.pos.relative(source.face), leftover);
                }
            }
            if (extracted > 0) {
                Task delivery = request.getEntity() != null ? new Task(source, request.getEntity())
                        : request.getPos() != null ? new Task(source, request.getPos()) : null;
                if (delivery != null) {
                    delivery.setPriority(task.getPriority());
                    delivery.setData(request.getEntity() != null ? 1 : 2);
                    delivery.setLifespan((short)31000);
                    delivery.setLinkedProvision(request);
                    request.setLinkedTask(delivery);
                    TaskHandler.addTask(level, delivery);
                }
            }
        } else {
            ItemStack moved = golem.dropItem(request.getStack());
            if (task.getData() == 1 && request.getEntity() != null) {
                var target = request.getEntity();
                var dropped = new net.minecraft.world.entity.item.ItemEntity(level, target.getX(), target.getY(), target.getZ(), moved);
                level.addFreshEntity(dropped);
            } else if (request.getPos() != null && request.getSide() != null) {
                var inventory = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, request.getPos(), request.getSide());
                ItemStack leftover = inventory == null ? moved
                        : net.neoforged.neoforge.items.ItemHandlerHelper.insertItemStacked(inventory, moved, false);
                if (!leftover.isEmpty()) {
                    var excess = golem.holdItem(leftover);
                    if (!excess.isEmpty()) net.minecraft.world.level.block.Block.popResource(level, request.getPos().relative(request.getSide()), excess);
                }
            }
            request.setInvalid(true);
        }
        golem.addRankXp(1);
        golem.swingArm();
        task.setSuspended(true);
        return true;
    }

    @Override
    public void onTaskSuspension(Level level, Task task) {
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        var request = task.getLinkedProvision();
        if (request == null || request.isInvalid()) return false;
        var destination = request.getSeal();
        if (destination != null) {
            if (destination.isLocked() && (!(golem.getGolemEntity() instanceof net.minecraft.world.entity.OwnableEntity owned)
                    || owned.getOwnerUUID() == null || !owned.getOwnerUUID().toString().equals(destination.getOwner()))) return false;
            var required = destination.getSeal().getRequiredTags();
            if (required != null && !golem.getProperties().getTraits().containsAll(java.util.Arrays.asList(required))) return false;
            var forbidden = destination.getSeal().getForbiddenTags();
            if (forbidden != null && java.util.Arrays.stream(forbidden).anyMatch(golem.getProperties()::hasTrait)) return false;
        }
        BlockPos target = destination != null ? destination.getSealPos().pos
                : request.getEntity() != null ? request.getEntity().blockPosition() : request.getPos();
        if (target == null || !golem.getGolemWorld().hasChunkAt(target)) return false;
        if (golem.getGolemEntity() instanceof thaumcraft.common.entities.TCGolemEntity entity && !entity.isWithinRestriction(target)) return false;
        if (task.getData() != 0) return golem.isCarrying(request.getStack());
        return !golem.isCarrying(request.getStack()) && golem.canCarry(request.getStack(), true);
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_provider.png");
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
