package thaumcraft.common.essentia.container;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Legacy-shaped {@code EssentiaHandler.getSources} search for aspect-source containers. */
public final class TCEssentiaSourceSearch {
    private TCEssentiaSourceSearch() {
    }

    public static List<BlockPos> discover(Level level, BlockPos origin, Direction direction, int range) {
        if (level == null || origin == null || range <= 0) {
            return List.of();
        }

        Direction scanDirection = direction;
        int start = 0;
        if (scanDirection == null) {
            start = -range;
            scanDirection = Direction.UP;
        }

        ArrayList<BlockPos> candidates = new ArrayList<>();
        for (int aa = -range; aa <= range; aa++) {
            for (int bb = -range; bb <= range; bb++) {
                for (int cc = start; cc < range; cc++) {
                    if (aa == 0 && bb == 0 && cc == 0) {
                        continue;
                    }
                    BlockPos candidate = offsetLegacy(origin, scanDirection, aa, bb, cc);
                    BlockEntity blockEntity = level.getBlockEntity(candidate);
                    if (blockEntity instanceof TCAspectSourceContainer) {
                        candidates.add(candidate);
                    }
                }
            }
        }
        candidates.sort(Comparator.comparingDouble(candidate -> candidate.distSqr(origin)));
        return candidates;
    }

    private static BlockPos offsetLegacy(BlockPos origin, Direction direction, int aa, int bb, int cc) {
        if (direction.getStepY() != 0) {
            return origin.offset(aa, cc * direction.getStepY(), bb);
        }
        if (direction.getStepX() == 0) {
            return origin.offset(aa, bb, cc * direction.getStepZ());
        }
        return origin.offset(cc * direction.getStepX(), aa, bb);
    }
}
