package thaumcraft.common.crafting.infusion;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/**
 * Non-mutating readiness plan for the next infusion crafting step.
 *
 * <p>Legacy TC6 records catalyst/components/essentia at crafting start, then later
 * re-checks the world before draining essentia and consuming pedestal items. This
 * class models that later check without changing pedestal contents or aspect state.
 */
public record TCInfusionCompletionPlan(
        boolean valid,
        String reason,
        TCInfusionCraftingPlan craftingPlan,
        AspectList availableAspects,
        AspectList missingAspects,
        AspectList remainingAspects,
        List<ComponentConsumption> componentConsumptions
) {
    public TCInfusionCompletionPlan {
        reason = reason == null ? "" : reason;
        availableAspects = availableAspects == null ? new AspectList() : availableAspects.copy();
        missingAspects = missingAspects == null ? new AspectList() : missingAspects.copy();
        remainingAspects = remainingAspects == null ? new AspectList() : remainingAspects.copy();
        componentConsumptions = componentConsumptions == null ? List.of() : List.copyOf(componentConsumptions);
    }

    public static TCInfusionCompletionPlan missingActivePlan(AspectList availableAspects) {
        return failed("missing_active_plan", null, availableAspects);
    }

    public static TCInfusionCompletionPlan failed(
            String reason,
            TCInfusionCraftingPlan craftingPlan,
            AspectList availableAspects
    ) {
        return new TCInfusionCompletionPlan(
                false,
                reason,
                craftingPlan,
                availableAspects,
                new AspectList(),
                availableAspects,
                List.of()
        );
    }

    public static TCInfusionCompletionPlan fromValidatedInputs(
            TCInfusionCraftingPlan craftingPlan,
            AspectList availableAspects,
            List<ComponentConsumption> componentConsumptions
    ) {
        if (craftingPlan == null) {
            return missingActivePlan(availableAspects);
        }
        AspectList available = availableAspects == null ? new AspectList() : availableAspects.copy();
        AspectList missing = missingAspects(craftingPlan.requiredAspects(), available);
        AspectList remaining = missing.visSize() == 0
                ? removeAspects(available, craftingPlan.requiredAspects())
                : available.copy();
        return new TCInfusionCompletionPlan(
                missing.visSize() == 0,
                missing.visSize() == 0 ? "valid" : "missing_aspects",
                craftingPlan,
                available,
                missing,
                remaining,
                componentConsumptions
        );
    }

    public int requiredAspectAmount() {
        return craftingPlan == null ? 0 : craftingPlan.requiredAspectAmount();
    }

    public int missingAspectAmount() {
        return missingAspects.visSize();
    }

    public int componentCount() {
        return componentConsumptions.size();
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

    private static AspectList removeAspects(AspectList availableAspects, AspectList requiredAspects) {
        AspectList remaining = availableAspects == null ? new AspectList() : availableAspects.copy();
        AspectList required = requiredAspects == null ? new AspectList() : requiredAspects.copy();
        for (Aspect aspect : required.getAspects()) {
            remaining.remove(aspect, required.getAmount(aspect));
        }
        return remaining;
    }

    public record ComponentConsumption(BlockPos pedestalPos, ItemStack expectedStack, ItemStack currentStack) {
        public ComponentConsumption {
            if (pedestalPos == null) {
                pedestalPos = BlockPos.ZERO;
            }
            expectedStack = expectedStack == null ? ItemStack.EMPTY : expectedStack.copyWithCount(1);
            currentStack = currentStack == null ? ItemStack.EMPTY : currentStack.copyWithCount(1);
        }
    }
}
