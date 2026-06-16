package thaumcraft.common.essentia.transport;

import net.minecraft.core.Direction;

/**
 * Deterministic one-step transfer helper.
 *
 * Legacy note: TC6 transport is suction-driven. This helper only performs the core pull rule:
 * a target with stronger compatible suction may pull essentia from a source that can output.
 */
public final class TCLegacyEssentiaNetwork {
    private TCLegacyEssentiaNetwork() {}

    public static int transfer(
            TCEssentiaTransport source,
            Direction sourceFace,
            TCEssentiaTransport target,
            Direction targetFace,
            String aspect,
            int maxAmount
    ) {
        if (source == null || target == null || aspect == null || aspect.isBlank() || maxAmount <= 0) return 0;
        if (!source.canOutputTo(sourceFace) || !target.canInputFrom(targetFace)) return 0;

        TCEssentiaSuction targetSuction = target.getSuction(targetFace);
        TCEssentiaSuction sourceSuction = source.getSuction(sourceFace);
        if (!targetSuction.accepts(aspect) || !targetSuction.strongerThan(sourceSuction)) return 0;

        int available = source.takeEssentia(aspect, maxAmount, sourceFace, true);
        int accepted = target.addEssentia(aspect, available, targetFace, true);
        int moved = Math.min(available, accepted);
        if (moved <= 0) return 0;

        int actuallyTaken = source.takeEssentia(aspect, moved, sourceFace, false);
        int actuallyAdded = target.addEssentia(aspect, actuallyTaken, targetFace, false);
        return Math.min(actuallyTaken, actuallyAdded);
    }
}