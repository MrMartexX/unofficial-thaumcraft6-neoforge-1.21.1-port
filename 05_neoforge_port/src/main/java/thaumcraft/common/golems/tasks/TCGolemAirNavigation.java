package thaumcraft.common.golems.tasks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Six-axis legacy flight graph; deliberately not vanilla diagonal flying AI. */
public final class TCGolemAirNavigation extends FlyingPathNavigation {
    public TCGolemAirNavigation(Mob mob, Level level) { super(mob, level); }

    @Override protected PathFinder createPathFinder(int maxVisitedNodes) {
        nodeEvaluator = new AirNodes();
        return new PathFinder(nodeEvaluator, maxVisitedNodes);
    }
    @Override protected boolean canUpdatePath() { return true; }
    @Override protected Vec3 getTempMobPos() { return mob.position().add(0, mob.getBbHeight() * .5, 0); }
    @Override public boolean isStableDestination(BlockPos pos) { return level.hasChunkAt(pos) && level.getBlockState(pos).getCollisionShape(level, pos).isEmpty(); }
    @Override protected boolean canMoveDirectly(Vec3 start, Vec3 end) {
        return level.clip(new ClipContext(start, end.add(0, mob.getBbHeight() * .5, 0),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob)).getType() == HitResult.Type.MISS;
    }
    @Override protected void followThePath() {
        if (path == null || path.isDone()) return;
        Vec3 position = getTempMobPos();
        if (position.distanceToSqr(path.getNextEntityPos(mob)) < mob.getBbWidth() * mob.getBbWidth()) path.advance();
        for (int i = Math.min(path.getNextNodeIndex() + 6, path.getNodeCount() - 1); i > path.getNextNodeIndex(); i--) {
            Vec3 candidate = path.getEntityPosAtNode(mob, i);
            if (candidate.distanceToSqr(position) <= 36 && canMoveDirectly(position, candidate)) { path.setNextNodeIndex(i); break; }
        }
        doStuckDetection(position);
    }

    private static final class AirNodes extends NodeEvaluator {
        @Override public Node getStart() {
            return getNode(Mth.floor(mob.getBoundingBox().minX), Mth.floor(mob.getBoundingBox().minY + .5), Mth.floor(mob.getBoundingBox().minZ));
        }
        @Override public Target getTarget(double x, double y, double z) {
            // Modern PathFinder passes integer block coordinates; legacy supplied X/Z centers.
            return new Target(getNode(Mth.floor(x + .5 - mob.getBbWidth() / 2), Mth.floor(y + .5), Mth.floor(z + .5 - mob.getBbWidth() / 2)));
        }
        @Override public int getNeighbors(Node[] neighbors, Node node) {
            int count = 0;
            for (Direction side : Direction.values()) {
                int x = node.x + side.getStepX(), y = node.y + side.getStepY(), z = node.z + side.getStepZ();
                if (!free(x, y, z)) continue;
                Node next = getNode(x, y, z);
                if (!next.closed) neighbors[count++] = next;
            }
            return count;
        }
        private boolean free(int x, int y, int z) {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            for (int dx = 0; dx < entityWidth; dx++) for (int dy = 0; dy < entityHeight; dy++) for (int dz = 0; dz < entityDepth; dz++) {
                pos.set(x + dx, y + dy, z + dz);
                var state = currentContext.getBlockState(pos);
                if (!state.isAir() && !state.isPathfindable(PathComputationType.AIR)) return false;
            }
            return true;
        }
        @Override public PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob) { return PathType.WATER; }
        @Override public PathType getPathType(PathfindingContext context, int x, int y, int z) { return PathType.WATER; }
    }
}
