package thaumcraft.common.crafting.infusion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.essentia.container.TCAspectSourceContainer;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;

/**
 * Boundary for real essentia/aspect source discovery.
 *
 * <p>Legacy {@code EssentiaHandler.drainEssentia(matrix, aspect, null, 12, ...)} searched a
 * 25 x 24 x 25 volume for {@code IAspectSource} containers and ordered them by distance. This
 * resolver preserves that discovery boundary for modern {@link TCAspectSourceContainer}
 * block entities. Transient tube buffers are deliberately not infusion sources.</p>
 */
public final class TCInfusionAspectSourceResolver {
    public static final String NO_SUPPORTED_SOURCE_FOUND = "no_supported_source_found";
    public static final int LEGACY_SOURCE_RANGE = 12;

    private TCInfusionAspectSourceResolver() {
    }

    public static Optional<TCInfusionAspectSource> findSource(TCInfusionMatrixBlockEntity matrix, TCInfusionCraftingPlan plan) {
        if (matrix == null || plan == null || matrix.getLevel() == null) {
            return Optional.empty();
        }
        Level level = matrix.getLevel();
        List<BlockPos> positions = discoverSourcePositions(matrix);
        if (positions.isEmpty()) {
            return Optional.empty();
        }
        List<TCAspectSourceContainer> containers = positions.stream()
                .map(level::getBlockEntity)
                .filter(TCAspectSourceContainer.class::isInstance)
                .map(TCAspectSourceContainer.class::cast)
                .toList();
        return containers.isEmpty()
                ? Optional.empty()
                : Optional.of(new TCContainerInfusionAspectSource(containers));
    }

    public static List<BlockPos> discoverSourcePositions(TCInfusionMatrixBlockEntity matrix) {
        if (matrix == null || matrix.getLevel() == null) {
            return List.of();
        }
        Level level = matrix.getLevel();
        BlockPos matrixPos = matrix.getBlockPos();
        List<Candidate> candidates = new ArrayList<>();
        for (int x = -LEGACY_SOURCE_RANGE; x <= LEGACY_SOURCE_RANGE; x++) {
            for (int z = -LEGACY_SOURCE_RANGE; z <= LEGACY_SOURCE_RANGE; z++) {
                for (int y = -LEGACY_SOURCE_RANGE; y < LEGACY_SOURCE_RANGE; y++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    BlockPos sourcePos = matrixPos.offset(x, y, z);
                    BlockEntity blockEntity = level.getBlockEntity(sourcePos);
                    if (blockEntity instanceof TCAspectSourceContainer) {
                        candidates.add(new Candidate(sourcePos));
                    }
                }
            }
        }
        candidates.sort(Comparator.comparingDouble(candidate -> candidate.pos().distSqr(matrixPos)));
        return candidates.stream().map(Candidate::pos).toList();
    }

    public static OnePointDrainResult drainOne(
            TCInfusionMatrixBlockEntity matrix,
            Aspect aspect,
            List<BlockPos> cachedSourcePositions
    ) {
        if (matrix == null || matrix.getLevel() == null || aspect == null) {
            return OnePointDrainResult.failed("invalid_one_point_drain_request");
        }
        if (cachedSourcePositions == null || cachedSourcePositions.isEmpty()) {
            return OnePointDrainResult.failed("no_cached_sources");
        }
        Level level = matrix.getLevel();
        for (BlockPos sourcePos : cachedSourcePositions) {
            BlockEntity blockEntity = level.getBlockEntity(sourcePos);
            if (!(blockEntity instanceof TCAspectSourceContainer container)) {
                return OnePointDrainResult.failed("source_cache_invalid");
            }
            if (container.isSourceBlocked()) {
                continue;
            }
            if (container.drainAspect(aspect, 1, true) != 1) {
                continue;
            }
            if (container.drainAspect(aspect, 1, false) == 1) {
                return OnePointDrainResult.success(aspect, sourcePos);
            }
            return OnePointDrainResult.failed("source_changed_during_commit");
        }
        return OnePointDrainResult.failed("aspect_unavailable");
    }

    public static String unavailableReason() {
        return NO_SUPPORTED_SOURCE_FOUND;
    }

    private record Candidate(BlockPos pos) {
    }

    public record OnePointDrainResult(boolean success, String reason, Aspect aspect, BlockPos sourcePos) {
        public OnePointDrainResult {
            reason = reason == null ? "" : reason;
        }

        public static OnePointDrainResult success(Aspect aspect, BlockPos sourcePos) {
            return new OnePointDrainResult(true, "drained", aspect, sourcePos);
        }

        public static OnePointDrainResult failed(String reason) {
            return new OnePointDrainResult(false, reason, null, null);
        }
    }
}
