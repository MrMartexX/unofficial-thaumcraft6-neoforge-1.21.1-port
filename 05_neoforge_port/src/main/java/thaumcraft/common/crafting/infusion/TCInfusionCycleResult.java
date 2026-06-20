package thaumcraft.common.crafting.infusion;

import net.minecraft.core.BlockPos;
import thaumcraft.api.aspects.Aspect;

public record TCInfusionCycleResult(
        Status status,
        String reason,
        Aspect aspect,
        BlockPos sourcePos,
        BlockPos componentPos,
        TCInfusionInstabilityEvent instabilityEvent
) {
    public TCInfusionCycleResult {
        reason = reason == null ? "" : reason;
    }

    public static TCInfusionCycleResult of(Status status, String reason) {
        return new TCInfusionCycleResult(status, reason, null, null, null, null);
    }

    public static TCInfusionCycleResult aspect(Status status, String reason, Aspect aspect, BlockPos sourcePos) {
        return new TCInfusionCycleResult(status, reason, aspect, sourcePos, null, null);
    }

    public static TCInfusionCycleResult component(Status status, String reason, BlockPos componentPos) {
        return new TCInfusionCycleResult(status, reason, null, null, componentPos, null);
    }

    public static TCInfusionCycleResult instability(TCInfusionInstabilityEvent event, String reason) {
        return new TCInfusionCycleResult(Status.INSTABILITY_EVENT, reason, null, null, null, event);
    }

    public enum Status {
        IDLE,
        ASPECT_DRAINED,
        WAITING_FOR_ASPECT,
        COMPONENT_TARGETED,
        COMPONENT_CHARGING,
        COMPONENT_CONSUMED,
        WAITING_FOR_COMPONENT,
        INSTABILITY_EVENT,
        COMPLETED,
        ABORTED,
        BLOCKED
    }
}
