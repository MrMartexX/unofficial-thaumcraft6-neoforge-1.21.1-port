package thaumcraft.common.crafting.infusion;

import net.minecraft.core.Direction;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;

/**
 * Narrow real-source adapter for the first infusion source policy slice.
 *
 * <p>This adapter is intentionally conservative: the current transport API exposes a single
 * visible essentia stack through a face, so this source accepts exactly one required aspect.
 * Multi-aspect infusion completion remains fail-closed until a broader storage-backed source
 * policy is implemented and audited.</p>
 */
public final class TCTransportInfusionAspectSource implements TCInfusionAspectSource {
    public static final String SINGLE_ASPECT_ONLY = "transport_source_single_aspect_only";
    public static final String MISSING_ASPECTS = "transport_source_missing_aspects";
    public static final String SIMULATION_FAILED = "transport_source_simulation_failed";
    public static final String DRAIN_CHANGED_DURING_COMMIT = "transport_source_drain_changed_during_commit";

    private final TCEssentiaTransport transport;
    private final Direction face;

    public TCTransportInfusionAspectSource(TCEssentiaTransport transport, Direction face) {
        this.transport = transport;
        this.face = face;
    }

    @Override
    public AspectList availableAspects() {
        AspectList available = new AspectList();
        if (transport == null || face == null) {
            return available;
        }
        TCEssentiaStack stack = transport.getEssentia(face);
        if (stack == null || stack.isEmpty()) {
            return available;
        }
        Aspect aspect = Aspect.getAspect(stack.aspect());
        if (aspect == null) {
            return available;
        }
        available.add(aspect, stack.amount());
        return available;
    }

    @Override
    public DrainResult drain(AspectList requiredAspects) {
        AspectList required = requiredAspects == null ? new AspectList() : requiredAspects.copy();
        if (required.visSize() <= 0) {
            return DrainResult.success(new AspectList(), availableAspects());
        }
        Aspect[] requiredTypes = required.getAspects();
        if (requiredTypes.length != 1) {
            return DrainResult.failed(SINGLE_ASPECT_ONLY, required, availableAspects());
        }
        Aspect aspect = requiredTypes[0];
        int amount = required.getAmount(aspect);
        if (aspect == null || amount <= 0 || transport == null || face == null) {
            return DrainResult.failed(MISSING_ASPECTS, required, availableAspects());
        }

        AspectList available = availableAspects();
        int availableAmount = available.getAmount(aspect);
        if (availableAmount < amount) {
            AspectList missing = new AspectList().add(aspect, amount - availableAmount);
            return DrainResult.failed(MISSING_ASPECTS, missing, available);
        }

        int simulated = transport.takeEssentia(aspect.getTag(), amount, face, true);
        if (simulated < amount) {
            AspectList missing = new AspectList().add(aspect, amount - simulated);
            return DrainResult.failed(SIMULATION_FAILED, missing, availableAspects());
        }

        int drainedAmount = transport.takeEssentia(aspect.getTag(), amount, face, false);
        if (drainedAmount < amount) {
            AspectList missing = new AspectList().add(aspect, amount - drainedAmount);
            return DrainResult.failed(DRAIN_CHANGED_DURING_COMMIT, missing, availableAspects());
        }

        AspectList drained = new AspectList().add(aspect, drainedAmount);
        return DrainResult.success(drained, availableAspects());
    }
}
