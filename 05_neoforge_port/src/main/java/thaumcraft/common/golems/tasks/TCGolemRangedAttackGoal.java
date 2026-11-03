package thaumcraft.common.golems.tasks;

import java.util.EnumSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.common.entities.TCGolemEntity;

/** Legacy AIArrowAttack: twenty visible ticks before standing, 20..25 cadence. */
public final class TCGolemRangedAttackGoal extends Goal {
    private final TCGolemEntity golem;
    private int attackTime = -1, seeTime;
    public TCGolemRangedAttackGoal(TCGolemEntity golem) { this.golem = golem; setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
    @Override public boolean canUse() {
        return golem.getProperties().hasTrait(EnumGolemTrait.FIGHTER) && golem.getProperties().hasTrait(EnumGolemTrait.RANGED)
                && golem.getTarget() != null;
    }
    @Override public boolean canContinueToUse() {
        return golem.getProperties().hasTrait(EnumGolemTrait.FIGHTER) && golem.getProperties().hasTrait(EnumGolemTrait.RANGED)
                && (canUse() || !golem.getNavigation().isDone());
    }
    @Override public void stop() { seeTime = 0; attackTime = -1; }
    @Override public boolean requiresUpdateEveryTick() { return true; }
    @Override public void tick() {
        var target = golem.getTarget();
        if (target == null) return;
        double distance = golem.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
        boolean visible = golem.getSensing().hasLineOfSight(target);
        seeTime = visible ? seeTime + 1 : 0;
        if (distance <= 256 && seeTime >= 20) golem.getNavigation().stop();
        else golem.getNavigation().moveTo(target, 1);
        golem.getLookControl().setLookAt(target, 10, 30);
        float fraction = (float)Math.sqrt(distance) / 16;
        if (--attackTime == 0) {
            if (distance > 256 || !visible) return;
            golem.performRangedAttack(target, Mth.clamp(fraction, .1F, 1));
            attackTime = Mth.floor(fraction * 5 + 20);
        } else if (attackTime < 0) attackTime = Mth.floor(fraction * 5 + 20);
    }
}
