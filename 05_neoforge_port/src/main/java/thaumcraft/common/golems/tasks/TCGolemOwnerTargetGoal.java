package thaumcraft.common.golems.tasks;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.common.entities.TCGolemEntity;

public final class TCGolemOwnerTargetGoal extends TargetGoal {
    private final TCGolemEntity golem;
    private final boolean defend;
    private int timestamp;
    private LivingEntity selected;
    public TCGolemOwnerTargetGoal(TCGolemEntity golem, boolean defend) {
        super(golem, false); this.golem = golem; this.defend = defend; setFlags(EnumSet.of(Flag.TARGET));
    }
    @Override public boolean canUse() {
        if (!golem.isFollowingOwner() || !golem.getProperties().hasTrait(EnumGolemTrait.FIGHTER)) return false;
        var owner = golem.getOwner();
        if (owner == null || !owner.isAlive()) return false;
        selected = defend ? owner.getLastHurtByMob() : owner.getLastHurtMob();
        int time = defend ? owner.getLastHurtByMobTimestamp() : owner.getLastHurtMobTimestamp();
        return time != timestamp && canAttack(selected, TargetingConditions.forCombat());
    }
    @Override public void start() {
        golem.setTarget(selected);
        var owner = golem.getOwner();
        if (owner != null) timestamp = defend ? owner.getLastHurtByMobTimestamp() : owner.getLastHurtMobTimestamp();
        super.start();
    }
    @Override public boolean canContinueToUse() {
        return golem.isFollowingOwner() && golem.getProperties().hasTrait(EnumGolemTrait.FIGHTER) && super.canContinueToUse();
    }
}
