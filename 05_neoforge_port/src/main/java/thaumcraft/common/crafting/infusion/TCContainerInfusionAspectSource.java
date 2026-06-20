package thaumcraft.common.crafting.infusion;

import java.util.ArrayList;
import java.util.List;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.essentia.container.TCAspectSourceContainer;

/**
 * Distance-ordered infusion source over legacy-shaped aspect containers.
 *
 * <p>The resolver supplies containers in the same nearest-first order used by legacy
 * {@code EssentiaHandler}. The executor still drains a complete audit plan at once, so this
 * adapter first prepares and simulates every allocation before committing any mutation.</p>
 */
public final class TCContainerInfusionAspectSource implements TCInfusionAspectSource {
    public static final String MISSING_ASPECTS = "container_source_missing_aspects";
    public static final String SIMULATION_FAILED = "container_source_simulation_failed";
    public static final String DRAIN_CHANGED_DURING_COMMIT = "container_source_drain_changed_during_commit";

    private final List<TCAspectSourceContainer> containers;

    public TCContainerInfusionAspectSource(List<? extends TCAspectSourceContainer> containers) {
        this.containers = containers == null ? List.of() : List.copyOf(containers);
    }

    @Override
    public AspectList availableAspects() {
        AspectList available = new AspectList();
        for (TCAspectSourceContainer container : containers) {
            if (container == null || container.isSourceBlocked()) {
                continue;
            }
            AspectList stored = container.storedAspects();
            if (stored != null) {
                available.add(stored);
            }
        }
        return available;
    }

    @Override
    public DrainResult drain(AspectList requiredAspects) {
        AspectList required = requiredAspects == null ? new AspectList() : requiredAspects.copy();
        if (required.visSize() <= 0) {
            return DrainResult.success(new AspectList(), availableAspects());
        }

        List<Allocation> allocations = new ArrayList<>();
        AspectList missing = new AspectList();
        for (Aspect aspect : required.getAspects()) {
            int remaining = required.getAmount(aspect);
            for (TCAspectSourceContainer container : containers) {
                if (remaining <= 0) {
                    break;
                }
                if (container == null || container.isSourceBlocked()) {
                    continue;
                }
                int available = container.storedAspects().getAmount(aspect);
                if (available <= 0) {
                    continue;
                }
                int allocated = Math.min(remaining, available);
                allocations.add(new Allocation(container, aspect, allocated));
                remaining -= allocated;
            }
            if (remaining > 0) {
                missing.add(aspect, remaining);
            }
        }
        if (missing.visSize() > 0) {
            return DrainResult.failed(MISSING_ASPECTS, missing, availableAspects());
        }

        for (Allocation allocation : allocations) {
            if (allocation.container().drainAspect(allocation.aspect(), allocation.amount(), true) != allocation.amount()) {
                return DrainResult.failed(SIMULATION_FAILED, required, availableAspects());
            }
        }

        AspectList drained = new AspectList();
        for (Allocation allocation : allocations) {
            int amount = allocation.container().drainAspect(allocation.aspect(), allocation.amount(), false);
            if (amount != allocation.amount()) {
                AspectList undrained = required.copy().remove(drained);
                return DrainResult.failed(DRAIN_CHANGED_DURING_COMMIT, undrained, availableAspects());
            }
            drained.add(allocation.aspect(), amount);
        }
        return DrainResult.success(drained, availableAspects());
    }

    private record Allocation(TCAspectSourceContainer container, Aspect aspect, int amount) {
    }
}
