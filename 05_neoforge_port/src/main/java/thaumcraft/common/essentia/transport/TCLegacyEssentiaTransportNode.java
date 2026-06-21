package thaumcraft.common.essentia.transport;

import net.minecraft.core.Direction;

import java.util.Objects;

/**
 * Shared transport-node implementation for the first modernized transport pass.
 *
 * This preserves legacy transport concepts: directional connectability, input/output checks,
 * suction type/amount, minimum suction, filter aspect and valve/open state.
 */
public class TCLegacyEssentiaTransportNode implements TCEssentiaTransport {
    private final TCMutableEssentiaStorage storage;
    private TCEssentiaTubeMode mode;
    private Direction oneWayOutputFace = Direction.NORTH;
    private boolean valveOpen = true;
    private String filterAspect = "";
    private TCEssentiaSuction suction = TCEssentiaSuction.NONE;

    public TCLegacyEssentiaTransportNode(TCEssentiaTubeMode mode, int capacity) {
        this.mode = Objects.requireNonNull(mode, "mode");
        this.storage = new TCMutableEssentiaStorage(capacity);
    }

    public TCEssentiaTubeMode mode() {
        return mode;
    }

    public void setMode(TCEssentiaTubeMode mode) {
        this.mode = Objects.requireNonNull(mode, "mode");
    }

    public TCEssentiaStorage storage() {
        return storage;
    }

    public TCMutableEssentiaStorage mutableStorage() {
        return storage;
    }

    public void setOneWayOutputFace(Direction oneWayOutputFace) {
        if (oneWayOutputFace != null) {
            this.oneWayOutputFace = oneWayOutputFace;
        }
    }

    public void setValveOpen(boolean valveOpen) {
        this.valveOpen = valveOpen;
    }

    public boolean isValveOpen() {
        return valveOpen;
    }

    public void setFilterAspect(String filterAspect) {
        this.filterAspect = filterAspect == null ? "" : filterAspect;
    }

    public void setSuction(TCEssentiaSuction suction) {
        this.suction = suction == null ? TCEssentiaSuction.NONE : suction;
    }

    @Override
    public boolean isConnectable(Direction face) {
        return face != null;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        if (!isConnectable(face) || !valveOpen) return false;
        if (!mode.allowsInput()) return false;
        if (mode == TCEssentiaTubeMode.ONEWAY) return face != oneWayOutputFace;
        return true;
    }

    @Override
    public boolean canOutputTo(Direction face) {
        if (!isConnectable(face) || !valveOpen) return false;
        if (!mode.allowsOutput()) return false;
        if (mode == TCEssentiaTubeMode.ONEWAY) return face == oneWayOutputFace;
        return true;
    }

    @Override
    public TCEssentiaSuction getSuction(Direction face) {
        if (!isConnectable(face) || !valveOpen) return TCEssentiaSuction.NONE;
        if (mode == TCEssentiaTubeMode.FILTER && !filterAspect.isBlank()) {
            return new TCEssentiaSuction(filterAspect, Math.max(1, suction.amount()));
        }
        if (mode == TCEssentiaTubeMode.RESTRICT) {
            return new TCEssentiaSuction(suction.aspect(), Math.max(mode.minimumSuction(), suction.amount()));
        }
        return suction;
    }

    @Override
    public int getMinimumSuction() {
        return mode.minimumSuction();
    }

    @Override
    public TCEssentiaStack getEssentia(Direction face) {
        if (face != null && !isConnectable(face)) return TCEssentiaStack.EMPTY;
        return storage.dominantAspect()
                .map(aspect -> TCEssentiaStack.of(aspect, storage.amount(aspect)))
                .orElse(TCEssentiaStack.EMPTY);
    }

    @Override
    public int addEssentia(String aspect, int amount, Direction face, boolean simulate) {
        if (face != null && !canInputFrom(face)) return 0;
        if (mode == TCEssentiaTubeMode.FILTER && !filterAspect.isBlank() && !filterAspect.equals(aspect)) return 0;
        return storage.add(aspect, amount, simulate);
    }

    @Override
    public int takeEssentia(String aspect, int amount, Direction face, boolean simulate) {
        if (face != null && !canOutputTo(face)) return 0;
        return storage.take(aspect, amount, simulate);
    }
}
