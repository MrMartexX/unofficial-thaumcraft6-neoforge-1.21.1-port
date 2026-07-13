package thaumcraft.common.entities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;

/** Modern equivalent of TC6 AILongRangeAttack: ranged attacks only start outside a minimum distance. */
public class TCLongRangeAttackGoal extends RangedAttackGoal {
    private final Mob mob;
    private final double minDistance;

    public TCLongRangeAttackGoal(RangedAttackMob rangedAttackMob, double minDistance, double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius) {
        super(rangedAttackMob, speedModifier, attackIntervalMin, attackIntervalMax, attackRadius);
        this.mob = (Mob) rangedAttackMob;
        this.minDistance = minDistance;
    }

    @Override
    public boolean canUse() {
        if (!super.canUse()) {
            return false;
        }
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        double distance = mob.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
        return distance >= minDistance * minDistance;
    }

    public double minDistanceForValidation() {
        return minDistance;
    }
}
