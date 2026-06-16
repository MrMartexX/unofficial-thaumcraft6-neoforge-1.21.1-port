package thaumcraft.common.essentia.transport;

/**
 * Legacy-aligned logical tube modes.
 *
 * These names mirror the TC6 transport family: normal tube, valve, buffer, filter, one-way,
 * restriction tube, input/output transport bridge, and smelter endpoints.
 */
public enum TCEssentiaTubeMode {
    NORMAL(true, true, 0),
    BUFFER(true, true, 0),
    FILTER(true, true, 1),
    ONEWAY(true, true, 0),
    RESTRICT(true, true, 64),
    VALVE(true, true, 0),
    INPUT(true, false, 0),
    OUTPUT(false, true, 0),
    SMELTER_THAUMIUM(false, true, 0),
    SMELTER_VOID(false, true, 0);

    private final boolean input;
    private final boolean output;
    private final int minimumSuction;

    TCEssentiaTubeMode(boolean input, boolean output, int minimumSuction) {
        this.input = input;
        this.output = output;
        this.minimumSuction = minimumSuction;
    }

    public boolean allowsInput() {
        return input;
    }

    public boolean allowsOutput() {
        return output;
    }

    public int minimumSuction() {
        return minimumSuction;
    }
}