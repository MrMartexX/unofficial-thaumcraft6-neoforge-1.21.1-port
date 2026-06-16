package thaumcraft.common.essentia.transport;

import net.minecraft.core.Direction;

/**
 * Modern port-facing equivalent of the legacy IEssentiaTransport shape.
 */
public interface TCEssentiaTransport {
    boolean isConnectable(Direction face);

    boolean canInputFrom(Direction face);

    boolean canOutputTo(Direction face);

    TCEssentiaSuction getSuction(Direction face);

    int getMinimumSuction();

    TCEssentiaStack getEssentia(Direction face);

    int addEssentia(String aspect, int amount, Direction face, boolean simulate);

    int takeEssentia(String aspect, int amount, Direction face, boolean simulate);
}