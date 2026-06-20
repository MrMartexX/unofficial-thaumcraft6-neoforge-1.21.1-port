package thaumcraft.common.crafting.infusion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.blocks.crafting.TCInfusionPedestalBlock;
import thaumcraft.common.blocks.crafting.TCInfusionPillarBlock;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;

/** Exact server-side result of the legacy {@code TileInfusionMatrix#getSurroundings()} scan. */
public record TCInfusionStructureProfile(
        boolean valid,
        String reason,
        PillarSet pillarSet,
        int cycleTime,
        int cycleDelay,
        float costMultiplier,
        float stabilityReplenish,
        int surroundingPedestalCount,
        int stabilizerCandidateCount,
        List<BlockPos> problemBlocks
) {
    private static final int[][] PILLAR_OFFSETS = {
            {-1, -2, -1},
            {1, -2, -1},
            {1, -2, 1},
            {-1, -2, 1}
    };

    public TCInfusionStructureProfile {
        reason = reason == null ? "" : reason;
        pillarSet = pillarSet == null ? PillarSet.MIXED : pillarSet;
        cycleTime = Math.max(1, cycleTime);
        cycleDelay = Math.max(1, cycleDelay);
        costMultiplier = Math.max(0.5F, costMultiplier);
        problemBlocks = problemBlocks == null ? List.of() : List.copyOf(problemBlocks);
    }

    public static TCInfusionStructureProfile inspect(TCInfusionMatrixBlockEntity matrix) {
        if (matrix == null || matrix.getLevel() == null) {
            return invalid("missing_level");
        }
        Level level = matrix.getLevel();
        BlockPos origin = matrix.getBlockPos();
        LocationValidation location = validateLocation(level, origin);
        if (!location.valid()) {
            return invalid(location.reason());
        }
        List<Block> pillars = location.pillars();

        int cycleTime = TCInfusionCycleState.BASE_CYCLE_TIME;
        float costMultiplier = 1.0F;
        float stabilityReplenish = 0.0F;
        PillarSet pillarSet = classifyPillars(pillars);
        if (pillarSet == PillarSet.ANCIENT) {
            cycleTime--;
            costMultiplier -= 0.1F;
            stabilityReplenish -= 0.1F;
        } else if (pillarSet == PillarSet.ELDRITCH) {
            cycleTime -= 3;
            costMultiplier += 0.05F;
            stabilityReplenish += 0.2F;
        }

        for (int[] offset : PILLAR_OFFSETS) {
            Block modifier = level.getBlockState(origin.offset(offset[0], -3, offset[2])).getBlock();
            if (modifier == TCBlocks.MATRIX_SPEED.get()) {
                cycleTime--;
                costMultiplier += 0.01F;
            } else if (modifier == TCBlocks.MATRIX_COST.get()) {
                cycleTime++;
                costMultiplier -= 0.02F;
            }
        }

        Set<BlockPos> stabilizers = new LinkedHashSet<>();
        int pedestalCount = 0;
        for (int dx = -TCInfusionMatrixBlockEntity.LEGACY_HORIZONTAL_SCAN_RANGE;
             dx <= TCInfusionMatrixBlockEntity.LEGACY_HORIZONTAL_SCAN_RANGE; dx++) {
            for (int dz = -TCInfusionMatrixBlockEntity.LEGACY_HORIZONTAL_SCAN_RANGE;
                 dz <= TCInfusionMatrixBlockEntity.LEGACY_HORIZONTAL_SCAN_RANGE; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                for (int dy = TCInfusionMatrixBlockEntity.LEGACY_SCAN_MIN_Y_OFFSET;
                     dy <= TCInfusionMatrixBlockEntity.LEGACY_SCAN_MAX_Y_OFFSET; dy++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    Block block = level.getBlockState(pos).getBlock();
                    if (block instanceof TCInfusionPedestalBlock) {
                        pedestalCount++;
                        if (block == TCBlocks.ELDRITCH_PEDESTAL.get()) {
                            costMultiplier += 0.0025F;
                        } else if (block == TCBlocks.ANCIENT_PEDESTAL.get()) {
                            costMultiplier -= 0.01F;
                        }
                    }
                    if (isActiveStabilizer(level, pos, block)) {
                        stabilizers.add(pos.immutable());
                    }
                }
            }
        }

        StabilityResult stability = calculateStability(level, origin, stabilizers);
        stabilityReplenish += stability.amount();
        cycleTime = Math.max(1, cycleTime);
        return new TCInfusionStructureProfile(
                true,
                "valid",
                pillarSet,
                cycleTime,
                Math.max(1, cycleTime / 2),
                Math.max(0.5F, costMultiplier),
                stabilityReplenish,
                pedestalCount,
                stabilizers.size(),
                stability.problemBlocks()
        );
    }

    /** Cheap legacy {@code validLocation()} equivalent used by the active matrix tick. */
    public static LocationValidation validateLocation(TCInfusionMatrixBlockEntity matrix) {
        if (matrix == null || matrix.getLevel() == null) {
            return LocationValidation.invalid("missing_level");
        }
        return validateLocation(matrix.getLevel(), matrix.getBlockPos());
    }

    private static LocationValidation validateLocation(Level level, BlockPos origin) {
        if (!(level.getBlockState(origin.below(2)).getBlock() instanceof TCInfusionPedestalBlock)) {
            return LocationValidation.invalid("missing_central_pedestal");
        }

        ArrayList<Block> pillars = new ArrayList<>(4);
        for (int[] offset : PILLAR_OFFSETS) {
            Block block = level.getBlockState(origin.offset(offset[0], offset[1], offset[2])).getBlock();
            if (!(block instanceof TCInfusionPillarBlock)) {
                return LocationValidation.invalid("missing_infusion_pillar");
            }
            pillars.add(block);
        }
        return new LocationValidation(true, "valid", List.copyOf(pillars));
    }

    private static TCInfusionStructureProfile invalid(String reason) {
        return new TCInfusionStructureProfile(
                false,
                reason,
                PillarSet.MIXED,
                TCInfusionCycleState.BASE_CYCLE_TIME,
                TCInfusionCycleState.BASE_CYCLE_DELAY,
                1.0F,
                0.0F,
                0,
                0,
                List.of()
        );
    }

    private static PillarSet classifyPillars(List<Block> pillars) {
        if (pillars.stream().allMatch(block -> block == TCBlocks.PILLAR_ARCANE.get())) {
            return PillarSet.ARCANE;
        }
        if (pillars.stream().allMatch(block -> block == TCBlocks.PILLAR_ANCIENT.get())) {
            return PillarSet.ANCIENT;
        }
        if (pillars.stream().allMatch(block -> block == TCBlocks.PILLAR_ELDRITCH.get())) {
            return PillarSet.ELDRITCH;
        }
        return PillarSet.MIXED;
    }

    private static boolean isActiveStabilizer(Level level, BlockPos pos, Block block) {
        if (block instanceof AbstractSkullBlock) {
            return true;
        }
        return block instanceof IInfusionStabiliser stabilizer && stabilizer.canStabaliseInfusion(level, pos);
    }

    private static StabilityResult calculateStability(Level level, BlockPos origin, Set<BlockPos> candidates) {
        Set<BlockPos> remaining = new LinkedHashSet<>(candidates);
        Map<Block, Integer> matchedPairCounts = new HashMap<>();
        ArrayList<BlockPos> problemBlocks = new ArrayList<>();
        float amount = 0.0F;
        while (!remaining.isEmpty()) {
            BlockPos firstPos = remaining.iterator().next();
            remaining.remove(firstPos);
            BlockPos secondPos = new BlockPos(
                    origin.getX() * 2 - firstPos.getX(),
                    firstPos.getY(),
                    origin.getZ() * 2 - firstPos.getZ()
            );
            Block firstBlock = level.getBlockState(firstPos).getBlock();
            Block secondBlock = level.getBlockState(secondPos).getBlock();
            float firstAmount = stabilizationAmount(level, firstPos, firstBlock);
            float secondAmount = stabilizationAmount(level, secondPos, secondBlock);
            if (firstBlock == secondBlock && Float.compare(firstAmount, secondAmount) == 0) {
                if (firstBlock instanceof IInfusionStabiliserExt ext
                        && ext.hasSymmetryPenalty(level, firstPos, secondPos)) {
                    amount -= ext.getSymmetryPenalty(level, firstPos);
                    problemBlocks.add(firstPos);
                } else {
                    int priorPairs = matchedPairCounts.getOrDefault(firstBlock, 0);
                    amount += firstAmount * (float) Math.pow(0.75D, priorPairs);
                    matchedPairCounts.put(firstBlock, priorPairs + 1);
                }
            } else {
                amount -= Math.max(firstAmount, secondAmount);
                problemBlocks.add(firstPos);
            }
            remaining.remove(secondPos);
        }
        return new StabilityResult(amount, List.copyOf(problemBlocks));
    }

    private static float stabilizationAmount(Level level, BlockPos pos, Block block) {
        return block instanceof IInfusionStabiliserExt ext ? ext.getStabilizationAmount(level, pos) : 0.1F;
    }

    public enum PillarSet {
        ARCANE,
        ANCIENT,
        ELDRITCH,
        MIXED
    }

    private record StabilityResult(float amount, List<BlockPos> problemBlocks) {
    }

    public record LocationValidation(boolean valid, String reason, List<Block> pillars) {
        public LocationValidation {
            reason = reason == null ? "" : reason;
            pillars = pillars == null ? List.of() : List.copyOf(pillars);
        }

        private static LocationValidation invalid(String reason) {
            return new LocationValidation(false, reason, List.of());
        }
    }
}
