package thaumcraft.common.essentia.transport;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Minimal mutable essentia store.
 *
 * Legacy note: jars, buffers, smelters and tubes exchanged typed essentia amounts.
 * This storage intentionally keeps the semantics simple and deterministic for the first port batch.
 */
public final class TCMutableEssentiaStorage implements TCEssentiaStorage {
    private final int capacity;
    private final Map<String, Integer> essentia = new HashMap<>();

    public TCMutableEssentiaStorage(int capacity) {
        this.capacity = Math.max(0, capacity);
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int totalAmount() {
        int total = 0;
        for (int value : essentia.values()) {
            total += Math.max(0, value);
        }
        return total;
    }

    @Override
    public int amount(String aspect) {
        if (aspect == null || aspect.isBlank()) return 0;
        return Math.max(0, essentia.getOrDefault(aspect, 0));
    }

    @Override
    public int add(String aspect, int amount, boolean simulate) {
        if (aspect == null || aspect.isBlank() || amount <= 0) return 0;
        int accepted = Math.min(amount, remainingCapacity());
        if (!simulate && accepted > 0) {
            essentia.put(aspect, amount(aspect) + accepted);
        }
        return accepted;
    }

    @Override
    public int take(String aspect, int amount, boolean simulate) {
        if (aspect == null || aspect.isBlank() || amount <= 0) return 0;
        int taken = Math.min(amount(aspect), amount);
        if (!simulate && taken > 0) {
            int left = amount(aspect) - taken;
            if (left <= 0) {
                essentia.remove(aspect);
            } else {
                essentia.put(aspect, left);
            }
        }
        return taken;
    }

    @Override
    public Optional<String> dominantAspect() {
        return essentia.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }


    public void clear() {
        essentia.clear();
    }

    public void set(String aspect, int amount) {
        if (aspect == null || aspect.isBlank() || amount <= 0) {
            return;
        }
        int accepted = Math.min(amount, Math.max(0, capacity - totalAmount() + amount(aspect)));
        if (accepted <= 0) {
            essentia.remove(aspect);
        } else {
            essentia.put(aspect, accepted);
        }
    }
    @Override
    public Map<String, Integer> snapshot() {
        return Collections.unmodifiableMap(new HashMap<>(essentia));
    }
}
