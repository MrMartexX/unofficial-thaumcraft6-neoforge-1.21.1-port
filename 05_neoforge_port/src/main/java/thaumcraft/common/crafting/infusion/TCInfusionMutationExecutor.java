package thaumcraft.common.crafting.infusion;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;
import thaumcraft.common.tiles.crafting.TCInfusionPedestalBlockEntity;

/**
 * Audit-only mutation boundary for a previously validated infusion plan.
 *
 * <p>This deliberately stays separate from player-facing matrix activation. It performs
 * one atomic, server-side mutation after {@link TCInfusionCompletionPlan} has already
 * rechecked the active plan against the current world state and available aspects.
 */
public final class TCInfusionMutationExecutor {
    private TCInfusionMutationExecutor() {
    }

    public static Result execute(TCInfusionMatrixBlockEntity matrix, TCInfusionCompletionPlan completionPlan) {
        if (matrix == null) {
            return Result.failed("missing_matrix");
        }
        if (completionPlan == null || !completionPlan.valid()) {
            return Result.failed("invalid_completion_plan");
        }
        TCInfusionCraftingPlan plan = completionPlan.craftingPlan();
        if (plan == null) {
            return Result.failed("missing_crafting_plan");
        }
        Level level = matrix.getLevel();
        if (level == null || level.isClientSide) {
            return Result.failed("missing_server_level");
        }
        if (matrix.activePlan().isEmpty()) {
            return Result.failed("missing_active_plan");
        }
        if (!matrix.activePlan().map(active -> active.recipeId().equals(plan.recipeId())).orElse(false)) {
            return Result.failed("active_plan_mismatch");
        }

        var center = matrix.centralPedestal();
        if (center.isEmpty()) {
            return Result.failed("missing_central_pedestal");
        }
        if (!sameOne(center.get().getStoredStack(), plan.catalyst())) {
            return Result.failed("catalyst_changed");
        }

        List<TCInfusionCompletionPlan.ComponentConsumption> consumptions = completionPlan.componentConsumptions();
        if (consumptions.size() != plan.componentPedestalPositions().size()) {
            return Result.failed("component_count_mismatch");
        }

        ArrayList<TCInfusionPedestalBlockEntity> componentPedestals = new ArrayList<>(consumptions.size());
        for (TCInfusionCompletionPlan.ComponentConsumption consumption : consumptions) {
            BlockEntity blockEntity = level.getBlockEntity(consumption.pedestalPos());
            if (!(blockEntity instanceof TCInfusionPedestalBlockEntity pedestal)) {
                return Result.failed("missing_component_pedestal");
            }
            if (!sameOne(pedestal.getStoredStack(), consumption.expectedStack())) {
                return Result.failed("component_changed");
            }
            componentPedestals.add(pedestal);
        }

        for (TCInfusionPedestalBlockEntity pedestal : componentPedestals) {
            pedestal.extractStored();
        }
        ItemStack result = plan.result();
        center.get().setStoredForCrafting(result);
        matrix.abortCrafting();
        return Result.success(result, completionPlan.remainingAspects(), componentPedestals.size());
    }

    private static boolean sameOne(ItemStack left, ItemStack right) {
        if (left == null || right == null || left.isEmpty() || right.isEmpty()) {
            return false;
        }
        return ItemStack.isSameItemSameComponents(left.copyWithCount(1), right.copyWithCount(1));
    }

    public record Result(boolean success, String reason, ItemStack result, AspectList remainingAspects, int consumedComponents) {
        public Result {
            reason = reason == null ? "" : reason;
            result = result == null ? ItemStack.EMPTY : result.copy();
            remainingAspects = remainingAspects == null ? new AspectList() : remainingAspects.copy();
        }

        public static Result success(ItemStack result, AspectList remainingAspects, int consumedComponents) {
            return new Result(true, "executed", result, remainingAspects, consumedComponents);
        }

        public static Result failed(String reason) {
            return new Result(false, reason, ItemStack.EMPTY, new AspectList(), 0);
        }
    }
}