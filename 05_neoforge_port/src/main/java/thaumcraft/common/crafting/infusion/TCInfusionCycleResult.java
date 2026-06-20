package thaumcraft.common.crafting.infusion;

import net.minecraft.core.BlockPos;
import thaumcraft.api.aspects.Aspect;

public record TCInfusionCycleResult(
        Status status,
        String reason,
        Aspect aspect,
        BlockPos sourcePos,
        BlockPos componentPos
) {
    public TCInfusionCycleResult {
        reason = reason == null ? "" : reason;
    }

    public static TCInfusionCycleResult of(Status status, String reason) {
        return new TCInfusionCycleResult(status, reason, null, null, null);
    }

    public static TCInfusionCycleResult aspect(Status status, String reason, Aspect aspect, BlockPos sourcePos) {
        return new TCInfusionCycleResult(status, reason, aspect, sourcePos, null);
    }

    public static TCInfusionCycleResult component(Status status, String reason, BlockPos componentPos) {
        return new TCInfusionCycleResult(status, reason, null, null, componentPos);
    }

    public enum Status {
        IDLE,
        ASPECT_DRAINED,
        WAITING_FOR_ASPECT,
        COMPONENT_TARGETED,
        COMPONENT_CHARGING,
        COMPONENT_CONSUMED,
        WAITING_FOR_COMPONENT,
        COMPLETED,
        ABORTED,
        BLOCKED
    }
}
