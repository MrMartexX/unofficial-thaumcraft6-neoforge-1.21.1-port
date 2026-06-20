package thaumcraft.common.crafting.infusion;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;

/**
 * Boundary for real essentia/aspect source discovery.
 *
 * <p>The first real policy is intentionally narrow: adjacent block entities implementing
 * TCEssentiaTransport may be exposed as a single-aspect source through the face pointing
 * toward the matrix. Unknown or empty sources fail closed. Player-facing completion remains
 * disabled until this path is fully audited and explicitly enabled.</p>
 */
public final class TCInfusionAspectSourceResolver {
    public static final String NO_SUPPORTED_SOURCE_FOUND = "no_supported_source_found";

    private TCInfusionAspectSourceResolver() {
    }

    public static Optional<TCInfusionAspectSource> findSource(TCInfusionMatrixBlockEntity matrix, TCInfusionCraftingPlan plan) {
        if (matrix == null || plan == null || matrix.getLevel() == null) {
            return Optional.empty();
        }
        Level level = matrix.getLevel();
        BlockPos matrixPos = matrix.getBlockPos();
        for (Direction direction : Direction.values()) {
            BlockEntity blockEntity = level.getBlockEntity(matrixPos.relative(direction));
            if (!(blockEntity instanceof TCEssentiaTransport transport)) {
                continue;
            }
            Direction sourceFace = direction.getOpposite();
            if (!transport.canOutputTo(sourceFace)) {
                continue;
            }
            if (transport.getEssentia(sourceFace).isEmpty()) {
                continue;
            }
            return Optional.of(new TCTransportInfusionAspectSource(transport, sourceFace));
        }
        return Optional.empty();
    }

    public static String unavailableReason() {
        return NO_SUPPORTED_SOURCE_FOUND;
    }
}
