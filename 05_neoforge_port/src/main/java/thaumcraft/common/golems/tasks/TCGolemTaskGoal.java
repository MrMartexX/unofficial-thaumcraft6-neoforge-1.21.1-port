package thaumcraft.common.golems.tasks;

import java.util.Arrays;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.entities.TCGolemEntity;
import thaumcraft.common.golems.seals.SealHandler;

/** Server task lifecycle adapted from legacy AIGoto/AIGotoBlock/AIGotoEntity. */
public final class TCGolemTaskGoal extends Goal {
    private final TCGolemEntity golem;
    private final byte type;
    private Task task;
    private BlockPos destination;
    private int cooldown;
    private int counter;
    private int pause;

    public TCGolemTaskGoal(TCGolemEntity golem, byte type) {
        this.golem = golem;
        this.type = type;
        setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (golem.level().isClientSide || golem.isInCombat()) return false;
        if (cooldown-- > 0) return false;
        cooldown = 5;
        var tasks = type == 0 ? TaskHandler.getBlockTasksSorted(golem.level(), golem.getUUID(), golem)
                : TaskHandler.getEntityTasksSorted(golem.level(), golem.getUUID(), golem);
        for (Task candidate : tasks) {
            if (!golem.level().hasChunkAt(candidate.getPos()) || !golem.isWithinRestriction(candidate.getPos())) continue;
            var seal = candidate.getSealPos() == null ? null : SealHandler.getSealEntity(golem.level(), candidate.getSealPos());
            if (seal == null || seal.isStoppedByRedstone(golem.level())) continue;
            if (golem.getGolemColor() > 0 && seal.getColor() > 0 && golem.getGolemColor() != seal.getColor()) continue;
            if (seal.isLocked() && (golem.getOwnerUUID() == null || !golem.getOwnerUUID().toString().equals(seal.getOwner()))) continue;
            var required = seal.getSeal().getRequiredTags();
            if (required != null && !golem.getProperties().getTraits().containsAll(Arrays.asList(required))) continue;
            var forbidden = seal.getSeal().getForbiddenTags();
            if (forbidden != null && Arrays.stream(forbidden).anyMatch(golem.getProperties()::hasTrait)) continue;
            if (!seal.getSeal().canGolemPerformTask(golem, candidate)) continue;
            BlockPos target = candidate.getPos();
            if (type == 0) {
                double closest = Double.MAX_VALUE;
                for (Direction face : Direction.Plane.HORIZONTAL) {
                    BlockPos adjacent = candidate.getPos().relative(face);
                    if (!golem.level().getBlockState(adjacent).getCollisionShape(golem.level(), adjacent).isEmpty()) continue;
                    double distance = adjacent.distToCenterSqr(golem.position());
                    if (distance < closest) { closest = distance; target = adjacent; }
                }
            }
            var path = golem.getNavigation().createPath(target, 1);
            if (target.distToCenterSqr(golem.position()) > 4 && (path == null || !path.canReach())) continue;
            task = candidate;
            destination = target;
            task.setReserved(true);
            seal.getSeal().onTaskStarted(golem.level(), golem, task);
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        counter = 0;
        pause = 0;
        moveTo();
    }

    @Override
    public boolean canContinueToUse() {
        return task != null && !task.isCompleted() && !task.isSuspended() && counter <= 1000
                && golem.level().hasChunkAt(task.getPos())
                && (type == 0 || (task.getEntity() != null && task.getEntity().isAlive()));
    }

    @Override
    public boolean requiresUpdateEveryTick() { return true; }

    @Override
    public void tick() {
        if (task == null || pause-- > 0) return;
        double distance = type == 0 ? destination.distToCenterSqr(golem.position()) : golem.distanceToSqr(task.getEntity());
        if (distance > 4) {
            if (++counter % 5 == 0) moveTo();
        } else {
            TaskHandler.completeTask(golem.level(), task, golem);
            if (!task.isCompleted()) { pause = 10; counter++; }
        }
    }

    private void moveTo() {
        if (type == 1 && task.getEntity() != null) golem.getNavigation().moveTo(task.getEntity(), 1.0);
        else golem.getNavigation().moveTo(destination.getX() + 0.5, destination.getY() + 0.5, destination.getZ() + 0.5, 1.0);
    }

    @Override
    public void stop() {
        golem.getNavigation().stop();
        if (task != null) {
            task.setReserved(false);
            if (task.isCompleted()) task.setSuspended(true);
        }
        task = null;
    }
}
