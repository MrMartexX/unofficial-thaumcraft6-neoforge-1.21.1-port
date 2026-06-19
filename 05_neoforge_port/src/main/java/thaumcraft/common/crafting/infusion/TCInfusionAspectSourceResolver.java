package thaumcraft.common.crafting.infusion;

import java.util.Optional;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;

/**
 * Boundary for future real essentia/aspect source discovery.
 *
 * <p>The current implementation intentionally returns no source. This keeps the
 * audit-only in-memory source path separate from player-facing jar, tube, alembic,
 * aura or network-backed source policies until those systems have their own focused
 * design, implementation and validation path.
 */
public final class TCInfusionAspectSourceResolver {
    public static final String REAL_SOURCE_POLICY_NOT_IMPLEMENTED = "real_source_policy_not_implemented";

    private TCInfusionAspectSourceResolver() {
    }

    public static Optional<TCInfusionAspectSource> findSource(TCInfusionMatrixBlockEntity matrix, TCInfusionCraftingPlan plan) {
        return Optional.empty();
    }

    public static String unavailableReason() {
        return REAL_SOURCE_POLICY_NOT_IMPLEMENTED;
    }
}