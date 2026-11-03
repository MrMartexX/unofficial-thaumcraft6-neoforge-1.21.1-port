package thaumcraft.common.world;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/** Marginal sampling law of ClientLevel.doAnimateTick, not a uniform cube approximation. */
public final class TCForestMoteSampling {
    private TCForestMoteSampling() {}

    public static double positionProbability(int dx, int dy, int dz, int range) {
        return axisProbability(dx, range) * axisProbability(dy, range) * axisProbability(dz, range);
    }

    private static double axisProbability(int delta, int range) {
        return Math.max(0, range - Math.abs(delta)) / (double) (range * range);
    }

    /** Exact binomial sampling by skipping unsuccessful independent Bernoulli trials. */
    public static int triggers(RandomSource random, double probability) {
        if (probability <= 0) return 0;
        if (probability >= 1) return 667;
        double logFailure = Math.log1p(-probability);
        int cursor = 0;
        int successes = 0;
        while (cursor < 667) {
            double skip = Math.floor(Math.log1p(-random.nextDouble()) / logFailure);
            if (skip >= 667 - cursor) break;
            cursor += (int) skip + 1;
            successes++;
        }
        return successes;
    }

    public static int adjustedLight(int rawSky, int skyDarken, float sunAngle) {
        sunAngle += ((sunAngle < Math.PI ? 0.0F : Mth.TWO_PI) - sunAngle) * 0.2F;
        return Mth.clamp(Math.round((rawSky - skyDarken) * Mth.cos(sunAngle)), 0, 15);
    }

    public static boolean passesLightGate(int light, int roll) {
        return 4 + light * 2 < 1 + roll;
    }
}
