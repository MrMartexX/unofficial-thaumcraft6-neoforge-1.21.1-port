package thaumcraft.common.crafting.infusion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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
                    if (blockEntity instanceof TCAspectSourceContainer container) {
                        candidates.add(new Candidate(sourcePos, container));
                    }
                }
            }
        }
        candidates.sort(Comparator.comparingDouble(candidate -> candidate.pos().distSqr(matrixPos)));
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        List<TCAspectSourceContainer> containers = candidates.stream().map(Candidate::container).toList();
        return Optional.of(new TCContainerInfusionAspectSource(containers));
    }

    public static String unavailableReason() {
        return NO_SUPPORTED_SOURCE_FOUND;
    }

    private record Candidate(BlockPos pos, TCAspectSourceContainer container) {
    }
}
