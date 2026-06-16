package thaumcraft.common.essentia.transport;

/**
 * Directional suction descriptor.
 *
 * Legacy note: TC6 compared suction type and amount between neighboring transports.
 */
public final class TCEssentiaSuction {
    public static final TCEssentiaSuction NONE = new TCEssentiaSuction("", 0);

    private final String aspect;
    private final int amount;

    public TCEssentiaSuction(String aspect, int amount) {
        this.aspect = aspect == null ? "" : aspect;
        this.amount = Math.max(0, amount);
    }

    public String aspect() {
        return aspect;
    }

    public int amount() {
        return amount;
    }

    public boolean accepts(String candidateAspect) {
        return aspect.isBlank() || aspect.equals(candidateAspect);
    }

    public boolean strongerThan(TCEssentiaSuction other) {
        return other == null || amount > other.amount;
    }
}