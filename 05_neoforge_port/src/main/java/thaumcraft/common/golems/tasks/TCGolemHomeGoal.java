package thaumcraft.common.golems.tasks;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.entities.TCGolemEntity;

public final class TCGolemHomeGoal extends Goal {
    private final TCGolemEntity golem;
    private int idle = 10;
    private Vec3 destination;
    public TCGolemHomeGoal(TCGolemEntity golem) { this.golem = golem; setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP)); }
    public boolean canUse() {
        if (golem.isFollowingOwner() || !golem.hasRestriction() || golem.isInCombat()) return false;
        if (idle-- > 0) return false;
        idle = 50;
        double distance = golem.getRestrictCenter().distToCenterSqr(golem.position());
        if (distance < 5) return false;
        Vec3 home = Vec3.atLowerCornerOf(golem.getRestrictCenter());
        destination = distance <= 1024 ? home : DefaultRandomPos.getPosTowards(golem, 16, 7, home, Math.PI / 2);
        return destination != null;
    }
    public void start() { golem.getNavigation().moveTo(destination.x, destination.y, destination.z, golem.getGolemMoveSpeed()); }
    public boolean canContinueToUse() {
        return !golem.isFollowingOwner() && golem.hasRestriction() && !golem.getNavigation().isDone()
                && golem.getRestrictCenter().distToCenterSqr(golem.position()) > 3;
    }
    public void stop() { idle = 50; golem.getNavigation().stop(); }
}
