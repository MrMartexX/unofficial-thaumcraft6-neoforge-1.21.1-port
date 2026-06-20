package thaumcraft.common.crafting.infusion;

/** Pure legacy stability math from {@code TileInfusionMatrix}. */
public final class TCInfusionStability {
    public static final float CAP = 25.0F;
    public static final float MINIMUM = -100.0F;
    public static final int EVENT_ROLL_BOUND = 1500;

    private TCInfusionStability() {
    }

    public static CycleUpdate advanceCycle(
            float current,
            int recipeInstability,
            float stabilityReplenish,
            float randomLossFactor
    ) {
        StabilityCategory category = category(current);
        float boundedRoll = Math.max(0.0F, Math.min(1.0F, randomLossFactor));
        float loss = boundedRoll * (Math.max(0, recipeInstability) / category.lossModifier());
        float updated = clamp(current - loss + stabilityReplenish);
        return new CycleUpdate(updated, loss, stabilityReplenish, category);
    }

    public static boolean shouldTriggerEvent(float stability, int eventRoll) {
        return stability < 0.0F
                && eventRoll >= 0
                && eventRoll < EVENT_ROLL_BOUND
                && eventRoll <= Math.abs(stability);
    }

    public static StabilityCategory category(float stability) {
        if (stability > 12.0F) {
            return StabilityCategory.VERY_STABLE;
        }
        if (stability >= 0.0F) {
            return StabilityCategory.STABLE;
        }
        if (stability > -25.0F) {
            return StabilityCategory.UNSTABLE;
        }
        return StabilityCategory.VERY_UNSTABLE;
    }

    public static float clamp(float stability) {
        return Math.max(MINIMUM, Math.min(CAP, stability));
    }

    public enum StabilityCategory {
        VERY_STABLE(5.0F),
        STABLE(6.0F),
        UNSTABLE(7.0F),
        VERY_UNSTABLE(8.0F);

        private final float lossModifier;

        StabilityCategory(float lossModifier) {
            this.lossModifier = lossModifier;
        }

        public float lossModifier() {
            return lossModifier;
        }
    }

    public record CycleUpdate(
            float stability,
            float loss,
            float replenish,
            StabilityCategory categoryBeforeCycle
    ) {
    }
}
