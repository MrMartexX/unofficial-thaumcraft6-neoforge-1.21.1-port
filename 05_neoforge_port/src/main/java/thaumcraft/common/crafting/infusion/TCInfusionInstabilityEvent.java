package thaumcraft.common.crafting.infusion;

/** Exact classification of the 24 legacy {@code craftCycle()} instability rolls. */
public enum TCInfusionInstabilityEvent {
    EJECT_ITEM_DROP,
    WARP,
    ZAP_ONE,
    ZAP_ALL,
    EJECT_FLUX_GOO_DROP,
    EJECT_FLUX_DROP,
    EJECT_FLUX_GOO_DELETE,
    EJECT_FLUX_DELETE,
    HARM_ONE,
    EJECT_EXPLOSIVE,
    HARM_ALL,
    MATRIX_EXPLOSION;

    public static TCInfusionInstabilityEvent fromLegacyRoll(int roll) {
        return switch (roll) {
            case 0, 1, 2, 3 -> EJECT_ITEM_DROP;
            case 4, 5, 6 -> WARP;
            case 7, 8, 9 -> ZAP_ONE;
            case 10, 11 -> ZAP_ALL;
            case 12, 13 -> EJECT_FLUX_GOO_DROP;
            case 14, 15 -> EJECT_FLUX_DROP;
            case 16 -> EJECT_FLUX_GOO_DELETE;
            case 17 -> EJECT_FLUX_DELETE;
            case 18, 19 -> HARM_ONE;
            case 20, 21 -> EJECT_EXPLOSIVE;
            case 22 -> HARM_ALL;
            case 23 -> MATRIX_EXPLOSION;
            default -> throw new IllegalArgumentException("Legacy instability roll must be in [0, 23]: " + roll);
        };
    }

    public boolean isSupportedByCurrentPort() {
        return this != EJECT_FLUX_GOO_DROP
                && this != EJECT_FLUX_GOO_DELETE
                && this != HARM_ONE
                && this != HARM_ALL;
    }

    public String missingDependency() {
        return switch (this) {
            case EJECT_FLUX_GOO_DROP, EJECT_FLUX_GOO_DELETE -> "thaumcraft:flux_goo";
            case HARM_ONE, HARM_ALL -> "thaumcraft:flux_taint_or_vis_exhaust_effect";
            default -> "";
        };
    }
}
