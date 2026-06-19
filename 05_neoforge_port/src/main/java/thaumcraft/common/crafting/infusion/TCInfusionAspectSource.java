package thaumcraft.common.crafting.infusion;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/**
 * Audit-only aspect source boundary for infusion completion planning.
 *
 * <p>This interface is intentionally tiny so future jar, tube, alembic or aura-backed
 * sources can be added behind the same all-or-nothing drain contract. The current
 * implementation is an in-memory source used only by runtime audits.
 */
public interface TCInfusionAspectSource {
    AspectList availableAspects();

    default DrainResult drain(TCInfusionCraftingPlan plan) {
        if (plan == null) {
            return DrainResult.failed("missing_crafting_plan", new AspectList(), availableAspects());
        }
        return drain(plan.requiredAspects());
    }

    DrainResult drain(AspectList requiredAspects);

    static TCInfusionAspectSource memory(AspectList available) {
        return new Memory(available);
    }

    private static AspectList missingAspects(AspectList requiredAspects, AspectList availableAspects) {
        AspectList missing = new AspectList();
        AspectList required = requiredAspects == null ? new AspectList() : requiredAspects.copy();
        AspectList available = availableAspects == null ? new AspectList() : availableAspects.copy();
        for (Aspect aspect : required.getAspects()) {
            int missingAmount = required.getAmount(aspect) - available.getAmount(aspect);
            if (missingAmount > 0) {
                missing.add(aspect, missingAmount);
            }
        }
        return missing;
    }

    final class Memory implements TCInfusionAspectSource {
        private final AspectList available;

        private Memory(AspectList available) {
            this.available = available == null ? new AspectList() : available.copy();
        }

        @Override
        public AspectList availableAspects() {
            return available.copy();
        }

        @Override
        public DrainResult drain(AspectList requiredAspects) {
            AspectList required = requiredAspects == null ? new AspectList() : requiredAspects.copy();
            AspectList missing = missingAspects(required, available);
            if (missing.visSize() > 0) {
                return DrainResult.failed("missing_aspects", missing, available.copy());
            }

            AspectList drained = new AspectList();
            for (Aspect aspect : required.getAspects()) {
                int amount = required.getAmount(aspect);
                if (amount <= 0) {
                    continue;
                }
                available.remove(aspect, amount);
                drained.add(aspect, amount);
            }
            return DrainResult.success(drained, available.copy());
        }
    }

    record DrainResult(boolean success, String reason, AspectList drainedAspects, AspectList missingAspects, AspectList remainingAspects) {
        public DrainResult {
            reason = reason == null ? "" : reason;
            drainedAspects = drainedAspects == null ? new AspectList() : drainedAspects.copy();
            missingAspects = missingAspects == null ? new AspectList() : missingAspects.copy();
            remainingAspects = remainingAspects == null ? new AspectList() : remainingAspects.copy();
        }

        public static DrainResult success(AspectList drainedAspects, AspectList remainingAspects) {
            return new DrainResult(true, "drained", drainedAspects, new AspectList(), remainingAspects);
        }

        public static DrainResult failed(String reason, AspectList missingAspects, AspectList remainingAspects) {
            return new DrainResult(false, reason, new AspectList(), missingAspects, remainingAspects);
        }
    }
}