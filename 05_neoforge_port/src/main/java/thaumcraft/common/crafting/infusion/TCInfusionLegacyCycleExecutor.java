package thaumcraft.common.crafting.infusion;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;
import thaumcraft.common.tiles.crafting.TCInfusionPedestalBlockEntity;

/** Server-side, one-step executor for the legacy {@code TileInfusionMatrix.craftCycle} order. */
public final class TCInfusionLegacyCycleExecutor {
    private TCInfusionLegacyCycleExecutor() {
    }

    public static TCInfusionCycleResult advance(TCInfusionMatrixBlockEntity matrix) {
        if (matrix == null || matrix.getLevel() == null || matrix.getLevel().isClientSide) {
            return TCInfusionCycleResult.of(TCInfusionCycleResult.Status.IDLE, "missing_server_matrix");
        }
        TCInfusionCraftingPlan plan = matrix.activePlan().orElse(null);
        TCInfusionCycleState state = matrix.activeCycleState().orElse(null);
        if (plan == null || state == null) {
            return TCInfusionCycleResult.of(TCInfusionCycleResult.Status.IDLE, "missing_active_cycle");
        }
        state.recordCycle();

        TCInfusionPedestalBlockEntity center = matrix.centralPedestal().orElse(null);
        if (center == null || !plan.catalystMatches(center.getStoredStack())) {
            return matrix.abortCraftingFromCycle("catalyst_changed");
        }
        if (TCInfusionContainerRemainderPolicy.requiresExplicitPolicy(plan)) {
            return matrix.abortCraftingFromCycle("container_remainder_policy_required");
        }

        Aspect currentAspect = state.currentAspect();
        if (currentAspect != null) {
            int remainingAspectAmount = state.remainingAspects().getAmount(currentAspect);
            List<BlockPos> sourcePositions = matrix.cachedInfusionSourcePositions();
            if (sourcePositions.isEmpty()) {
                if (matrix.sourceRefreshCooldownTicks() > 0) {
                    TCInfusionCycleResult result = TCInfusionCycleResult.aspect(
                            TCInfusionCycleResult.Status.WAITING_FOR_ASPECT,
                            "source_rescan_delayed",
                            currentAspect,
                            null
                    );
                    matrix.recordCycleResult(result);
                    return result;
                }
                sourcePositions = matrix.refreshInfusionSourceCache();
            }
            TCInfusionAspectSourceResolver.OnePointDrainResult drain =
                    TCInfusionAspectSourceResolver.drainOne(matrix, currentAspect, sourcePositions);
            if (!drain.success()) {
                matrix.deferInfusionSourceRefresh();
                TCInfusionCycleResult result = TCInfusionCycleResult.aspect(
                        TCInfusionCycleResult.Status.WAITING_FOR_ASPECT,
                        drain.reason(),
                        currentAspect,
                        null
                );
                matrix.recordCycleResult(result);
                return result;
            }
            state.consumeAspectPoint(currentAspect);
            TCInfusionNetwork.sendEssentiaSource(
                    matrix,
                    drain.sourcePos(),
                    currentAspect.getColor(),
                    remainingAspectAmount > 1 ? state.cycleDelay() : 0
            );
            TCInfusionCycleResult result = TCInfusionCycleResult.aspect(
                    TCInfusionCycleResult.Status.ASPECT_DRAINED,
                    "drained_one",
                    currentAspect,
                    drain.sourcePos()
            );
            matrix.recordCycleResult(result);
            return result;
        }

        if (state.pendingComponentCount() > 0) {
            ComponentMatch match = findComponentMatch(state, matrix.findSurroundingPedestals());
            if (match == null) {
                TCInfusionCycleResult result = TCInfusionCycleResult.of(
                        TCInfusionCycleResult.Status.WAITING_FOR_COMPONENT,
                        "matching_component_not_found"
                );
                matrix.recordCycleResult(result);
                return result;
            }
            if (state.itemCountdown() == 0) {
                state.beginComponentCountdown();
                TCInfusionNetwork.sendComponentSource(matrix, match.pedestal().getBlockPos());
                TCInfusionCycleResult result = TCInfusionCycleResult.component(
                        TCInfusionCycleResult.Status.COMPONENT_TARGETED,
                        "component_beam_started",
                        match.pedestal().getBlockPos()
                );
                matrix.recordCycleResult(result);
                return result;
            }
            if (!state.advanceComponentCountdown()) {
                TCInfusionCycleResult result = TCInfusionCycleResult.component(
                        TCInfusionCycleResult.Status.COMPONENT_CHARGING,
                        "component_beam_charging",
                        match.pedestal().getBlockPos()
                );
                matrix.recordCycleResult(result);
                return result;
            }

            ItemStack remainder = TCInfusionContainerRemainderPolicy.remainderForComponent(match.expected());
            match.pedestal().extractStored();
            if (!remainder.isEmpty()) {
                match.pedestal().setStoredForCrafting(remainder);
            }
            state.removePendingComponent(match.componentIndex());
            TCInfusionCycleResult result = TCInfusionCycleResult.component(
                    TCInfusionCycleResult.Status.COMPONENT_CONSUMED,
                    "component_consumed",
                    match.pedestal().getBlockPos()
            );
            matrix.recordCycleResult(result);
            return result;
        }

        center.setStoredForCrafting(resultWithLegacyDamageRatio(center.getStoredStack(), plan.result()));
        return matrix.completeCraftingFromCycle();
    }

    private static ComponentMatch findComponentMatch(
            TCInfusionCycleState state,
            List<TCInfusionPedestalBlockEntity> pedestals
    ) {
        for (int componentIndex = 0; componentIndex < state.pendingComponentCount(); componentIndex++) {
            ItemStack expected = state.pendingComponent(componentIndex);
            for (TCInfusionPedestalBlockEntity pedestal : pedestals) {
                if (sameOne(expected, pedestal.getStoredStack())) {
                    return new ComponentMatch(componentIndex, expected, pedestal);
                }
            }
        }
        return null;
    }

    private static boolean sameOne(ItemStack left, ItemStack right) {
        if (left == null || right == null || left.isEmpty() || right.isEmpty()) {
            return false;
        }
        return ItemStack.isSameItemSameComponents(left.copyWithCount(1), right.copyWithCount(1));
    }

    static ItemStack resultWithLegacyDamageRatio(ItemStack catalyst, ItemStack result) {
        ItemStack output = result == null ? ItemStack.EMPTY : result.copy();
        if (catalyst == null
                || catalyst.isEmpty()
                || output.isEmpty()
                || !catalyst.isDamageableItem()
                || !catalyst.isDamaged()
                || !output.isDamageableItem()
                || output.isDamaged()) {
            return output;
        }
        float damageRatio = catalyst.getDamageValue() / (float) catalyst.getMaxDamage();
        output.setDamageValue((int) (output.getMaxDamage() * damageRatio));
        return output;
    }

    private record ComponentMatch(
            int componentIndex,
            ItemStack expected,
            TCInfusionPedestalBlockEntity pedestal
    ) {
    }
}
