package thaumcraft.common.golems.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.tasks.Task;

public class TaskHandler {
    private static final int TASK_LIMIT = 10000;
    private static final Map<ResourceKey<Level>, Map<Integer, Task>> tasks = new ConcurrentHashMap<>();

    public static void addTask(Level level, Task task) {
        ResourceKey<Level> key = level.dimension();
        Map<Integer, Task> map = tasks.computeIfAbsent(key, k -> new ConcurrentHashMap<>());
        if (map.size() > TASK_LIMIT) {
            Iterator<Task> it = map.values().iterator();
            if (it.hasNext()) {
                it.next();
                it.remove();
            }
        }
        map.put(task.getId(), task);
    }

    public static Task getTask(Level level, int id) {
        return getTasks(level).get(id);
    }

    public static Map<Integer, Task> getTasks(Level level) {
        return tasks.computeIfAbsent(level.dimension(), k -> new ConcurrentHashMap<>());
    }

    public static List<Task> getBlockTasksSorted(Level level, UUID uuid, Entity golem) {
        Map<Integer, Task> map = getTasks(level);
        List<Task> out = new ArrayList<>();

        for (Task task : map.values()) {
            if (task.isReserved() || task.isCompleted() || task.isSuspended()) {
                continue;
            }
            if (task.getType() != 0) {
                continue;
            }
            if (uuid != null && task.getGolemUUID() != null && !uuid.equals(task.getGolemUUID())) {
                continue;
            }

            double dist = task.getPos().distToCenterSqr(golem.getX(), golem.getY(), golem.getZ());
            dist -= task.getPriority() * 256.0D;

            int insertIdx = out.size();
            for (int i = 0; i < out.size(); i++) {
                Task other = out.get(i);
                double otherDist = other.getPos().distToCenterSqr(golem.getX(), golem.getY(), golem.getZ())
                        - other.getPriority() * 256.0D;
                if (dist < otherDist) {
                    insertIdx = i;
                    break;
                }
            }
            out.add(insertIdx, task);
        }
        return out;
    }

    public static List<Task> getEntityTasksSorted(Level level, UUID uuid, Entity golem) {
        Map<Integer, Task> map = getTasks(level);
        List<Task> out = new ArrayList<>();

        for (Task task : map.values()) {
            if (task.isReserved() || task.isCompleted() || task.isSuspended()) {
                continue;
            }
            if (task.getType() != 1) {
                continue;
            }
            if (uuid != null && task.getGolemUUID() != null && !uuid.equals(task.getGolemUUID())) {
                continue;
            }
            if (task.getEntity() == null || !task.getEntity().isAlive()) {
                task.setSuspended(true);
                continue;
            }

            double dist = task.getEntity().distanceToSqr(golem);
            dist -= task.getPriority() * 256.0D;

            int insertIdx = out.size();
            for (int i = 0; i < out.size(); i++) {
                Task other = out.get(i);
                double otherDist = (other.getEntity() != null ? other.getEntity().distanceToSqr(golem) : 9999.0D)
                        - other.getPriority() * 256.0D;
                if (dist < otherDist) {
                    insertIdx = i;
                    break;
                }
            }
            out.add(insertIdx, task);
        }
        return out;
    }

    public static void completeTask(Level level, Task task, IGolemAPI golem) {
        if (task.isCompleted() || task.isSuspended()) return;
        var seal = task.getSealPos() == null ? null
                : thaumcraft.common.golems.seals.SealHandler.getSealEntity(level, task.getSealPos());
        task.setCompletion(seal == null || seal.getSeal().onTaskCompletion(level, golem, task));
    }

    public static void expireTasks(Level level) {
        getTasks(level).values().removeIf(task -> {
            if (!task.isSuspended() && task.getLifespan() > 0) {
                task.setLifespan((short)(task.getLifespan() - 1));
                return false;
            }
            var seal = task.getSealPos() == null ? null
                    : thaumcraft.common.golems.seals.SealHandler.getSealEntity(level, task.getSealPos());
            if (seal != null) seal.getSeal().onTaskSuspension(level, task);
            return true;
        });
    }

    public static void clear(Level level) {
        tasks.remove(level.dimension());
    }
}
