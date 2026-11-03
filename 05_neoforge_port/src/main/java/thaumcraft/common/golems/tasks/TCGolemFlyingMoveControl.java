package thaumcraft.common.golems.tasks;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import thaumcraft.common.entities.TCGolemEntity;

public final class TCGolemFlyingMoveControl extends MoveControl {
    private final TCGolemEntity golem;
    public TCGolemFlyingMoveControl(TCGolemEntity golem) { super(golem); this.golem = golem; }
    @Override public void tick() {
        if (operation != Operation.MOVE_TO) return;
        double x = wantedX - golem.getX(), y = wantedY - golem.getY(), z = wantedZ - golem.getZ();
        double distance = (float)Math.sqrt(x * x + y * y + z * z);
        if (distance < golem.getBoundingBox().getSize()) {
            operation = Operation.WAIT;
            golem.setDeltaMovement(golem.getDeltaMovement().scale(.5));
        } else {
            golem.setDeltaMovement(golem.getDeltaMovement().add(x / distance * .033 * speedModifier,
                    y / distance * .0125 * speedModifier, z / distance * .033 * speedModifier));
            var target = golem.getTarget();
            double yawX = target == null ? golem.getDeltaMovement().x : target.getX() - golem.getX();
            double yawZ = target == null ? golem.getDeltaMovement().z : target.getZ() - golem.getZ();
            golem.setYRot(-(float)Mth.atan2(yawX, yawZ) * Mth.RAD_TO_DEG);
            golem.yBodyRot = golem.getYRot();
        }
    }
}
