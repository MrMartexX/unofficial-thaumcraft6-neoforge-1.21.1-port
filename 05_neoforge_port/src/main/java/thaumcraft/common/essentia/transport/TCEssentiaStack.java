package thaumcraft.common.essentia.transport;

import java.util.Objects;

/**
 * Immutable essentia amount holder.
 *
 * Legacy note: TC6 transport APIs moved Aspect + amount pairs through IEssentiaTransport.
 * This port-facing type keeps the same concept while delaying direct Aspect registry coupling.
 */
public final class TCEssentiaStack {
    public static final TCEssentiaStack EMPTY = new TCEssentiaStack("", 0);

    private final String aspect;
    private final int amount;

    public TCEssentiaStack(String aspect, int amount) {
        if (amount <= 0 || aspect == null || aspect.isBlank()) {
            this.aspect = "";
            this.amount = 0;
        } else {
            this.aspect = aspect;
            this.amount = amount;
        }
    }

    public static TCEssentiaStack of(String aspect, int amount) {
        return new TCEssentiaStack(aspect, amount);
    }

    public String aspect() {
        return aspect;
    }

    public int amount() {
        return amount;
    }

    public boolean isEmpty() {
        return amount <= 0 || aspect.isBlank();
    }

    public TCEssentiaStack withAmount(int newAmount) {
        return new TCEssentiaStack(aspect, newAmount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TCEssentiaStack other)) return false;
        return amount == other.amount && Objects.equals(aspect, other.aspect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aspect, amount);
    }

    @Override
    public String toString() {
        return isEmpty() ? "TCEssentiaStack.EMPTY" : "TCEssentiaStack[" + aspect + " x " + amount + "]";
    }
}