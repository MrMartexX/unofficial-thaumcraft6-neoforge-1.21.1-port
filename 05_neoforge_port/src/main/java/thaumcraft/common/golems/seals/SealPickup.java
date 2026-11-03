package thaumcraft.common.golems.seals;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;

public class SealPickup extends SealFiltered implements ISealConfigArea {

    private int delay = 0;
    private final java.util.Map<Integer, Integer> itemTasks = new java.util.HashMap<>();

    @Override
    public String getKey() {
        return "thaumcraft:pickup";
    }

    @Override
    public boolean canPlaceAt(Level level, BlockPos pos, Direction side) {
        return !level.isEmptyBlock(pos);
    }

    @Override
    public void tickSeal(Level level, ISealEntity seal) {
        if (delay++ % 5 != 0) return;
        itemTasks.entrySet().removeIf(entry -> {
            var item = level.getEntity(entry.getValue());
            return item == null || !item.isAlive() || TaskHandler.getTask(level, entry.getKey()) == null;
        });

        AABB area = GolemHelper.getBoundsForArea(seal);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);

        for (ItemEntity item : items) {
            if (item.isAlive() && item.onGround() && !item.hasPickUpDelay() && !item.getItem().isEmpty()
                    && !itemTasks.containsValue(item.getId()) && matchesFilter(item.getItem())) {
                Task task = new Task(seal.getSealPos(), item);
                task.setPriority(seal.getPriority());
                TaskHandler.addTask(level, task);
                itemTasks.put(task.getId(), item.getId());
                break;
            }
        }
    }

    @Override
    public void onTaskStarted(Level level, IGolemAPI golem, Task task) {
    }

    @Override
    public boolean onTaskCompletion(Level level, IGolemAPI golem, Task task) {
        if (task.getEntity() instanceof ItemEntity item && item.isAlive()) {
            ItemStack remaining = golem.holdItem(item.getItem());
            if (remaining.isEmpty()) {
                item.discard();
            } else {
                item.setItem(remaining);
            }
            golem.addRankXp(1);
            return true;
        }
        return false;
    }

    @Override
    public void onTaskSuspension(Level level, Task task) {
        itemTasks.remove(task.getId());
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return task.getEntity() instanceof ItemEntity item && item.isAlive() && golem.canCarry(item.getItem(), true);
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_pickup.png");
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
