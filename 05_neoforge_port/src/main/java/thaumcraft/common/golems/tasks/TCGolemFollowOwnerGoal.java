package thaumcraft.common.golems.tasks;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.PathType;
import thaumcraft.common.entities.TCGolemEntity;

public final class TCGolemFollowOwnerGoal extends Goal {
    private final TCGolemEntity golem;
    private LivingEntity owner;
    private int recalc;
    private float waterCost;
    public TCGolemFollowOwnerGoal(TCGolemEntity golem) { this.golem = golem; setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
    public boolean canUse() {
        if (!golem.isFollowingOwner()) return false;
        owner = golem.getOwner();
        return owner != null && owner.isAlive() && golem.distanceToSqr(owner) >= 100;
    }
    public void start() { recalc = 0; waterCost = golem.getPathfindingMalus(PathType.WATER); golem.setPathfindingMalus(PathType.WATER, 0); }
    public boolean canContinueToUse() {
        return golem.isFollowingOwner() && owner != null && owner.isAlive() && !golem.getNavigation().isDone() && golem.distanceToSqr(owner) > 4;
    }
    public void tick() {
        golem.getLookControl().setLookAt(owner, 10, golem.getMaxHeadXRot());
        if (--recalc > 0) return;
        recalc = 10;
        if (golem.getNavigation().moveTo(owner, 1) || golem.isLeashed() || golem.distanceToSqr(owner) < 144) return;
        BlockPos origin = BlockPos.containing(owner.getX() - 2, owner.getBoundingBox().minY, owner.getZ() - 2);
        for (int x = 0; x <= 4; x++) for (int z = 0; z <= 4; z++) {
            if (x > 0 && z > 0 && x < 4 && z < 4) continue;
            BlockPos target = origin.offset(x, 0, z);
            var level = golem.level();
            if (!level.hasChunkAt(target) || !level.getBlockState(target.below()).isCollisionShapeFullBlock(level, target.below())
                    || level.getBlockState(target).isCollisionShapeFullBlock(level, target)
                    || level.getBlockState(target.above()).isCollisionShapeFullBlock(level, target.above())) continue;
            golem.moveTo(target.getX() + .5, target.getY(), target.getZ() + .5, golem.getYRot(), golem.getXRot());
            golem.getNavigation().stop();
            return;
        }
    }
    public boolean requiresUpdateEveryTick() { return true; }
    public void stop() { owner = null; golem.getNavigation().stop(); golem.setPathfindingMalus(PathType.WATER, waterCost); }
}
