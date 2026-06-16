package thaumcraft.common.essentia.transport;

import java.util.Map;
import java.util.Optional;

public interface TCEssentiaStorage {
    int capacity();

    int totalAmount();

    int amount(String aspect);

    int add(String aspect, int amount, boolean simulate);

    int take(String aspect, int amount, boolean simulate);

    Optional<String> dominantAspect();

    Map<String, Integer> snapshot();

    default int remainingCapacity() {
        return Math.max(0, capacity() - totalAmount());
    }
}